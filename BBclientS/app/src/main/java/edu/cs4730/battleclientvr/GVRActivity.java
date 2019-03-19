package edu.cs4730.battleclientvr;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.controller.Controller;
import com.google.vr.sdk.controller.ControllerManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class GVRActivity extends GvrActivity {

    String TAG = "GVRActivity";
    boolean game = false;

    //networking variables
    boolean connected = false, running = false;
    network mynetwork = null;
    String host, botline = null;
    int port;

    //game variables
    ArrayList ScanInfo = new ArrayList();

    // These two objects are the primary APIs for interacting with the Daydream controller.
    private ControllerManager controllerManager;
    private Controller controller;

    //gvr variables
    CardboardOverlayView overlayView;
    myStereoRenderer render;
    GvrView gvrView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gvr);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        String mParam1 = extras.getString("key1");
        String arr[] = MainActivity.token(mParam1);
        host = arr[0];
        port = Integer.parseInt(arr[1]);
        botline = extras.getString("key2");


        gvrView = (GvrView) findViewById(R.id.cardboard_view);

        render = new myStereoRenderer();
        gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        gvrView.setRenderer(render);

        // gvrView.setTransitionViewEnabled(true);
        // Enable Cardboard-trigger feedback with Daydream headsets. This is a simple way of supporting
        // Daydream controller input for basic interactions using the existing Cardboard trigger API.
        gvrView.enableCardboardTriggerEmulation();

        //now send everything to the screen.
        setGvrView(gvrView);
        if (!connected) {
            mynetwork = new network();
            mynetwork.set(host, port, botline);
            new Thread(mynetwork).start();
        }
        //this is overlay code from google, that allows us to put text on the "screen" easily.
        overlayView = (CardboardOverlayView) findViewById(R.id.overlay);
        overlayView.show3DToast("Waiting to connect.");

    }

    public void setupGVR (String line, String line2) {
        Log.wtf(TAG, "line is " + line);
        String str[] = MainActivity.token(line); //seti[ pid, xsize, ysize, numberofbots
        render.setup(Integer.parseInt(str[1]), Float.parseFloat(str[2]), Float.parseFloat(str[3]));
    }


    public int getAngle() {
        //test firing first.
        int angle = render.getAngle();  //it's off by 90 degrees, I think.


        //so 0 to 180 is 0 to -180, 181 to 360 ti 180 to 0
        if (angle < 0) {
            angle = -angle;
        } else if (angle > 0 && angle < 181) {
            angle = 360 - angle;
        } else {
            //well shit...
            angle = -2;  //error code basically.
        }
        Log.d(TAG, "Angle is " + angle);
        return angle;
    }


    private void onMove(int angle) {
        
        String cmdn = "";

        /* based on direction you are facing, move that way.  If you want to.  This is your call.
        if I could suggest group of 45 angle, which is +/-22.5 from say 0 (UP), but we are using integers, so 22 or 23 you'll call..
          which would set cmdn="move 0 -1";
         */

	//NOTE this the way you must set the command, otherwise you will get an concurrentency error.
        synchronized (mynetwork.cmd) {
            mynetwork.cmd = cmdn;
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String str = msg.getData().getString("msg");
            if (str == null) {
                return true;
            }
            if (str.startsWith("Err: ")) {
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
            } else if (str.startsWith("READY")) {
                               game =true;

            } else if (str.startsWith("RESET")) {
                //We can now switch the GVRfragment.
                game = false;
                finish();
                //no with are done!

            } else if (str.startsWith("STATUS")) {
                //Log.v(TAG, "Status: " + str);
                //statusupdate(str);
            } else {
                Log.v(TAG, "General: " + str);
                //output.setText(str);
                //procesStr(str);
            }
            return true;
        }

    });


    public void setme(float x, float y) {
        //this is my location, so change render camera view.
        if (render != null)  //so it appears the status get called sometimes, before the vr has setup!
            render.move(x, y);
    }

    public void SetInfo(ArrayList ScanInfo) {
        //do something...
        if (render != null)
            render.SetObjects(ScanInfo);
    }


    public static String[] token(String text) {
        return text.split("[ ]+");
    }

    public void mkmsg(String str) {
        //handler junk, because thread can't update screen!
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("msg", str);
        msg.setData(b);
        handler.sendMessage(msg);
    }


    public class network implements Runnable {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        String host, botline;
        int port;
        //public boolean send = false;
        String cmd = "";

        String TAG = "myNetwork";

        network() {
            host = "k2win.cs.uwyo.edu";
            port = 3012;
            botline = "Testbot 0 0 4";
        }

        public void set(String h, int p, String b) {
            host = h;
            port = p;
            botline = b;
        }

        public void done() {
            running = false;
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                //don't care.
                System.out.println("yea. died while closing!");
            }
            in = null;
            out = null;
            socket = null;
            connected = false;

        }

        public boolean connect() {
            //int p = Integer.parseInt(port.getText().toString());
            //String h = hostname.getText().toString();
            InetAddress serverAddr;
            Log.v(TAG, "host is " + host);
            Log.v(TAG, "port is " + port);
            try {
                serverAddr = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                mkmsg("Err: Unknown host");
                return false;
            }
            try {
                socket = new Socket(serverAddr, port);
            } catch (IOException e) {
                mkmsg("Err: Unable to make connection");
                socket = null;
                return false;
            }
            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                mkmsg("Err: Made connection, but streams failed");
                in = null;
                out = null;
                return false;
            }
            connected = true;
            return true;
        }


        @Override
        public void run() {
            if (!connect()) {
                return;
            }
            running = true;
            String line;
            //get init data and bot setup.
            line = readln();  //setup line
            //tokenize and get PID!
            Log.v(TAG, "Setup: " + line);
            //line ia PID WidthArena HeighArena NumberOfBots
            //String[] str = token(line);
            // myPID = Integer.valueOf(str[1]);
            // mkmsg("SETUP " + line);

            writeln(botline);  //my bot

            //Now the response with
            //name ArmourValue MoveRate ScanDistance BulletPower RateOfFire BulletDistance
            String line2 = readln();  //init line about bot.
            Log.v(TAG, "Setup2: " + line2);

            // send the message here to startup the GVR fragment!

            //myGVRFrag = GVRFragment.newInstance(line, line2);
            setupGVR(line, line2);


            mkmsg("READY");

            //now wait for game to start and get the first status line
            String str[];
            String temp;
            while (running && connected) {
                //Read in the status line, plus info lines.
                /// System.out.println("Info or status line: ");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                str = readandToken();
                int mr, sl;
                // System.out.println("str[0] is "+str[0]);
                while (str[0].equals("Info")) {
                    if (str[1].equals("Dead") || str[1].equals("GameOver")) {
                        running = false;
                        //disconnect and connected = false
                        done();
                        break;
                    } else if (str[1].equals("Alive")) {
                        //abots = Integer.parseInt(str[2]);
                    }
                    str = readandToken();
                }
                // So assuming we are still alive, issue a command.
                if (running) {
                    //get status line
                    //          x = Integer.parseInt(str[1]);  //curent x pos
                    //          y = Integer.parseInt(str[2]);  //curent y pos
                    //          mr = Integer.parseInt(str[3]); //movement rate
                    //          sl = Integer.parseInt(str[4]); //shot left to fire
                    //          hp = Integer.parseInt(str[5]); //hit points
                    //send status message for drawing purposes
                    mkmsg("STATUS " + str[1] + " " + str[2] + " " + str[3] + " " + str[4] + " " + str[5]);
                    setme(Float.parseFloat(str[1]), Float.parseFloat(str[2]));
                    mr = Integer.parseInt(str[3]); //movement rate
                    sl = Integer.parseInt(str[4]);
                    synchronized (cmd) {

                        if (mr != 0 && sl != 0)
                            doScan();
                        else if (cmd.compareTo("") == 0)   // there is no command, so issue a scan
                            doScan();
                        else if (cmd.startsWith("fire") && sl != 0)
                            doScan();
                        else if (cmd.startsWith("move") && mr != 0)
                            doScan();
                        else {
                            writeln(cmd);
                            Log.d(TAG, "Wrote: " + cmd);
                            cmd = "";
                        }
                    }  //end of synchronized.
                }
            }
            mkmsg("RESET");

        }

        public void doScan() {
            String temp;
            writeln("scan");
            ScanInfo.clear();
            temp = readln();
            //System.out.println("Scan: "+temp);
            while (temp != null && !temp.equals("scan done") && !temp.equals("FAILED")) {
//                            Log.v(TAG, "Scan: " + temp);
                ScanInfo.add(temp);
                //read in scan info and deal it
                temp = readln();
            }
            //now send that to the GVR to display.
            SetInfo(ScanInfo);
        }

        public void writeln(String str) {
            out.println(str);
            out.flush();
        }

        public String readln() {
            String str = "FAILED";
            if (in != null && running) {
                try {
                    str = in.readLine();
                } catch (IOException e) {
                    mkmsg("Err: Read failed");
                    connected = false;
                    running = false;
                }
            } else {
                connected = false;
                running = false;
            }
            return str;
        }

        public String[] readandToken() {
            String Failed[] = {"Failed"};
            String line = readln();
            if (line == null) {
                line = "FAILED";
            }
            // mkmsg(line);
            //System.out.println(line);
            if (line.compareTo("FAILED") == 0) {
                return Failed;
            } else {
                //return  token(line);
                return line.split("[ ]+");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
	//controllerManager.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
	//controllerManager.stop();
    }

    // We receive all events from the Controller through this listener. In this example, our
    // listener handles both ControllerManager.EventListener and Controller.EventListener events. which is the daydream.
/*
    private class EventListener extends Controller.EventListener
            implements ControllerManager.EventListener {
    }
*/

/* bluetooth controllers use the following 

    //getting the buttons.  note, there is down and up action.  this only
    //looks for down actions.
    @Override
    public boolean dispatchKeyEvent(android.view.KeyEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {

            if (game) {  //game has started, so accept commands.
            	  synchronized (mynetwork.cmd) {
                        mynetwork.cmd = "fire " + whatever way you figured to fire.;
                    }

            }
            return true;
        }

        return false;
    }


    //getting the "joystick" or dpad motion.  likely more the move direction.
    @Override
    public boolean onGenericMotionEvent(android.view.MotionEvent motionEvent) {
        float xaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X);
        float yaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y);

        // use the DEFAIRY one, so ignore this code unless you are using something else.
        //if ( ((Float.compare(motionEvent.getAxisValue(MotionEvent.AXIS_X), 0.0f) != 0)) ||
        //        ((Float.compare(motionEvent.getAxisValue(MotionEvent.AXIS_Y), 0.0f) != 0))  )   {
        //    xaxis = motionEvent.getAxisValue(MotionEvent.AXIS_X);
        //    yaxis = motionEvent.getAxisValue(MotionEvent.AXIS_Y);
       // }
           
        boolean handled = false;
        if (!game) {  //still in the connection mode.
            return handled;
        }


        //assuming use joystick to move, then calculate the info and call onMove()  and set handled=true.


        return handled;
    }


*/



}
