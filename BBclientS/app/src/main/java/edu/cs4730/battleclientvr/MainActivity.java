package edu.cs4730.battleclientvr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public String host, name="VRbot";
    public int port=3012, armor=0, power=0, scan=0;

    EditText et_host, et_port, et_name, et_armor, et_power, et_scan;
    Button btn_connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_host = findViewById(R.id.et_host);
        et_host.setText("192.168.1.101");
        et_port = findViewById(R.id.et_port);
        et_name = findViewById(R.id.et_name);
        et_armor =findViewById(R.id.et_armor);
        et_power = findViewById(R.id.et_power);
        et_scan = findViewById(R.id.et_scan);
        btn_connect = findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                host = et_host.getText().toString();
                port = Integer.parseInt(et_port.getText().toString());
                name = et_name.getText().toString();
                armor = Integer.parseInt(et_armor.getText().toString());
                power = Integer.parseInt(et_power.getText().toString());
                scan = Integer.parseInt(et_scan.getText().toString());

                String hostline = host + " " + port;
                String botline = name + " " + armor + " " + power + " " + scan;


                Intent intent = new Intent(MainActivity.this, GVRActivity.class);
                intent.putExtra("key1", hostline);
                intent.putExtra("key2", botline);
                // Set the request code to any code you like, you can identify the
                // callback via this code
                startActivity(intent);

            }
        });
    }

    public static String[] token(String text) {
        return text.split("[ ]+");
    }
}
