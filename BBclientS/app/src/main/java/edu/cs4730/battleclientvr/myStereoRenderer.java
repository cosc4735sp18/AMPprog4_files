package edu.cs4730.battleclientvr;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;

import edu.cs4730.battleclientvr.obj.Cube;
import edu.cs4730.battleclientvr.obj.CubeLight;
import edu.cs4730.battleclientvr.obj.CubeOutline;
import edu.cs4730.battleclientvr.obj.Floor;

/**
 * This is implementation of the Render, using Cardboards StereoRenderer.
 * Code was used from the cardboardsample and combined with the opengl30Cube.
 * <p>
 * <p>
 * <p>
 * BIG NOTE:   the plain is x,z  (y is up/down).  So... while I say y in variable names I mean z.
 */
public class myStereoRenderer implements GvrView.StereoRenderer {
    private static String TAG = "StereoRenderer";

    boolean issetup = false;

    //board info and game
    float xsize = 200.0f, ysize = 200.0f;
    int pid;

    private float mAngle = 0.4f;  //spin of the cube.

    //to move the camera (ie our position)
    public float MoveX, MoveY, MoveZ;
    private float meX = 0f, meZ = 0f;
    public boolean moved = false;

    //other bots/shots
    ArrayList objects;

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 500.0f;

    private static final float CAMERA_Z = 0.01f;

    private Cube mCube;
    private Cube mShot;
    private CubeLight mPower;
    //private Cube1color mPower;
    private CubeOutline mCubeOutline;
    private Floor mFloor;

    private float objectDistance = 6f;
    private float floorDepth = 10f;


    private float[] CubeMatrix;
    private float[] camera;
    private float[] view;
    private float[] headView;
    private float[] modelview;
    private float[] mMVPMatrix;
    private float[] modelFloor;

    private float[] looking;
    private float[] euler;

    // We keep the light always position just above the user.
    //private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[]{0.0f, 2.0f, 0.0f, 1.0f};
    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[]{0.0f, 22.0f, 0.0f, 1.0f};

    private final float[] lightPosInEyeSpace = new float[4];

    myStereoRenderer() {
        pid = -1;
        xsize = -1;
        ysize = -1;
        issetup = false;
    }

    myStereoRenderer(int p, float x, float y) {
        pid = p;
        xsize = x;
        ysize = y;
        issetup = true;
    }

    public void move(float x, float z) {
        //Log.v(TAG, "move?");
        meX = x;
        meZ = z;

        if (-x == MoveX && -z == MoveZ) {
            //do nothing
            moved = false;
        } else {
            //Log.v(TAG, "yes");
            MoveX = -x;
            MoveZ = -z;
            moved = true;

        }
    }

    public void SetObjects(ArrayList l) {
        objects = l;
    }

    ///
    // Create a shader object, load the shader source, and
    // compile the shader.
    //
    public static int LoadShader(int type, String shaderSrc) {
        int shader;
        int[] compiled = new int[1];

        // Create the shader object
        shader = GLES30.glCreateShader(type);

        if (shader == 0) {
            return 0;
        }

        // Load the shader source
        GLES30.glShaderSource(shader, shaderSrc);

        // Compile the shader
        GLES30.glCompileShader(shader);

        // Check the compile status
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            Log.e(TAG, "Erorr!!!!");
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     * <p>
     * <pre>
     * mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     * <p>
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Prepares OpenGL ES before we draw a frame.
     *
     * @param headTransform The head transformation in the new frame.
     */
    @Override
    public void onNewFrame(HeadTransform headTransform) {

        //rotate the cube, mangle is how fast, x,y,z which directions it rotates.
        //Matrix.rotateM(mRotationMatrix, 0, mAngle, 0.7f, 0.7f, 1.0f);

        if (moved) {
            //Matrix.translateM(camera, 0, 0, 0, 0.1f);
            Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
            Matrix.translateM(camera, 0, MoveX - 5f, 0, MoveZ - 5f);
            // Build the camera matrix and apply it to the ModelView.
            //  Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
            moved = false;
        }

        //I don't think I need this at all.
        headTransform.getHeadView(headView, 0);

        //used to determine forward direction.  3x1 unit vector
        //note the Z is inverted by openGL, so invert it.
        //headTransform.getForwardVector(looking,0);
        //looking[2] = -looking[2];
        //headTransform.getRightVector(looking,0);
        headTransform.getEulerAngles(looking, 0);
    }

    /**
     * Draws a frame for an eye.
     *
     * @param eye The eye to render. Includes all required transformations.
     */
    @Override
    public void onDrawEye(Eye eye) {
        // Clear the color buffer  set above by glClearColor.
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        //need this otherwise, it will over right stuff and the cube will look wrong!
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);


        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        // combine the model-view with the projection matrix
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

        //draw cube outline around bot
        //get the identity matrix and set the object to the correct spot.
        Matrix.setIdentityM(CubeMatrix, 0);
        //                               X    Y  Y
        Matrix.translateM(CubeMatrix, 0, meX, 0, meZ);  // Z

        // combine the model with the view matrix to create the modelview matreix
        Matrix.multiplyMM(modelview, 0, view, 0, CubeMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, perspective, 0, modelview, 0);

        if (issetup) {
            //finally draw the cube with the full Model-view-projection matrix.
            mCubeOutline.draw(mMVPMatrix);

            if (objects != null) {
                //draw the objects here
                String str[];
                if (!objects.isEmpty()) {
                    //something to draw.
                    for (int i = 0; i < objects.size(); i++) {
                        str = MainActivity.token(objects.get(i).toString());
                        if (str[1].compareTo("bot") == 0) {
                            //Log.v(TAG, "drawing bot");
                            //get the identity matrix and set the object to the correct spot.
                            Matrix.setIdentityM(CubeMatrix, 0);
                            //                               X                          Y
                            Matrix.translateM(CubeMatrix, 0, Float.parseFloat(str[3]), 0,
                                    Float.parseFloat(str[4]));  // Z

                            // combine the model with the view matrix to create the modelview matrix
                            Matrix.multiplyMM(modelview, 0, view, 0, CubeMatrix, 0);
                            Matrix.multiplyMM(mMVPMatrix, 0, perspective, 0, modelview, 0);

                            //finally draw the cube with the full Model-view-projection matrix.
                            mCube.draw(mMVPMatrix);
                        } else if (str[1].compareTo("shot") == 0) {
                            //Log.v(TAG, "drawing shot");
                            //get the identity matrix and set the object to the correct spot.
                            Matrix.setIdentityM(CubeMatrix, 0);
                            //                               X                          Y
                            Matrix.translateM(CubeMatrix, 0, Float.parseFloat(str[5]), 0,
                                    Float.parseFloat(str[6]));  // Z

                            // combine the model with the view matrix to create the modelview matrix
                            Matrix.multiplyMM(modelview, 0, view, 0, CubeMatrix, 0);
                            Matrix.multiplyMM(mMVPMatrix, 0, perspective, 0, modelview, 0);

                            //finally draw the cube with the full Model-view-projection matrix.
                            mShot.draw(mMVPMatrix);
                        } else if (str[1].compareTo("powerup") == 0) {
                            //Log.v(TAG, "drawing shot");
                            //get the identity matrix and set the object to the correct spot.
                            Matrix.setIdentityM(CubeMatrix, 0);
                            //                               X                          Y
                            Matrix.translateM(CubeMatrix, 0, Float.parseFloat(str[3]) + 5.0f, 0,
                                    Float.parseFloat(str[4]) + 5.0f);  // Z

                            // combine the model with the view matrix to create the modelview matrix
                            Matrix.multiplyMM(modelview, 0, view, 0, CubeMatrix, 0);
                            Matrix.multiplyMM(mMVPMatrix, 0, perspective, 0, modelview, 0);

                            //finally draw the cube with the full Model-view-projection matrix.
                            // Log.v(TAG, "power is " + str[2]);
                            //for cubelight
                            mPower.draw(mMVPMatrix, CubeMatrix, modelFloor, lightPosInEyeSpace, Integer.parseInt(str[2]));
                            //mPower.draw(mMVPMatrix, Integer.parseInt(str[2]));
                        }
                    }
                }
            }


            //now calculate for the floor
            Matrix.multiplyMM(modelview, 0, view, 0, modelFloor, 0);
            // combine the model-view with the projection matrix
            Matrix.multiplyMM(mMVPMatrix, 0, perspective, 0, modelview, 0);
            mFloor.drawFloor(mMVPMatrix, modelFloor, modelview, lightPosInEyeSpace);
        }
    }


    public int getAngle() {
        //assumes the looking matrix is set.  otherwise, this will likely explode.
        //using  tan-1(y/x)  where z is x is our plane.
        //return (int) Math.toDegrees(Math.atan(looking[0]/looking[2]));

        //for eulers this should be easier
        return (int) Math.toDegrees(looking[1]);  //should be the Yaw.  -pi, pi
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
        //no clue, example code was blank here.
    }

    @Override
    public void onSurfaceChanged(int i, int i1) {
        Log.i(TAG, "onSurfaceChanged");  //should not happen, set landscape in the manifest file.
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        CubeMatrix = new float[16];
        camera = new float[16];
        view = new float[16];
        mMVPMatrix = new float[16];
        modelview = new float[16];
        headView = new float[16];
        modelFloor = new float[16];


        looking = new float[4];  //likely only need three, but 4 just in case I need it.

        GLES30.glClearColor(0.1f, 0.1f, 0.1f, 0.5f); // Dark background so text shows up well.
        //initialize the cube code for drawing.
        mCube = new Cube(10.0f);
        mShot = new Cube(2.0f);
        mPower = new CubeLight(10.0f, 1);
        // mPower = new Cube1color(10.0f);
        mCubeOutline = new CubeOutline(10.0f);

        // Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, -CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        //floor object
        mFloor = new Floor(500.0f, 500.0f);
        Matrix.setIdentityM(modelFloor, 0);
        Matrix.translateM(modelFloor, 0, 0, -floorDepth, 0); // Floor appears below user.
    }

    public void setup(int p, float x, float y) {
        pid = p;
        xsize = x;
        ysize = y;

        Log.wtf(TAG, "p is " + p + "x is " + x + "y is " + y);




        issetup = true;

    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
    }
}
