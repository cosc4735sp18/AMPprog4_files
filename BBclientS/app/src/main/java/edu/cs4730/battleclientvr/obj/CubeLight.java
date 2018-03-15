package edu.cs4730.battleclientvr.obj;

/**
 * Created by Seker on 7/2/2015.
 * <p>
 * <p>
 * This code actually will draw a cube.
 * <p>
 * Some of the code is used from https://github.com/christopherperry/cube-rotation
 * and changed up to opengl 3.0
 */

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import edu.cs4730.battleclientvr.myStereoRenderer;

public class CubeLight {
    private int mProgramObject;
    private int mMVPMatrixHandle;
    private int mColorHandle;

    private int cubePositionParam;
    private int cubeNormalParam;
    private int cubeColorParam;
    private int cubeModelParam;
    private int cubeModelViewParam;
    private int cubeModelViewProjectionParam;
    private int cubeLightPosParam;

    private static final int COORDS_PER_VERTEX = 3;
    private FloatBuffer mVertices, mNormals, mColors;


    //initial size of the cube.  set here, so it is easier to change later.
    float size = 10.0f;

    //this is the initial data, which will need to translated into the mVertices variable in the consturctor.
    float[] mVerticesData;
    float[] cube_normals;

    private void setupcube() {

        mVerticesData = new float[]{
                ////////////////////////////////////////////////////////////////////
                // FRONT
                ////////////////////////////////////////////////////////////////////

                -size, size, size, // top-left
                -size, -size, size, // bottom-left
                size, size, size, // top-right
                -size, -size, size, // bottom-left
                size, -size, size, // bottom-right
                size, size, size, // top-left
                ////////////////////////////////////////////////////////////////////
                // RIGHT
                ////////////////////////////////////////////////////////////////////
                size, size, -size, // top-left
                size, -size, -size, // bottom-left
                size, -size, size, // bottom-right
                size, -size, size, // bottom-right
                size, size, size, // top-right
                size, size, -size, // top-left

                ////////////////////////////////////////////////////////////////////
                // BACK
                ////////////////////////////////////////////////////////////////////
                -size, size, -size, // top-left
                -size, -size, -size, // bottom-left
                size, size, -size, // bottom-right
                -size, -size, -size, // bottom-right
                size, -size, -size, // top-right
                size, size, -size, // top-left

                ////////////////////////////////////////////////////////////////////
                // LEFT
                ////////////////////////////////////////////////////////////////////
                -size, size, -size, // top-left
                -size, -size, -size, // bottom-left
                -size, -size, size, // bottom-right
                -size, -size, size, // bottom-right
                -size, size, size, // top-right
                -size, size, -size, // top-left

                ////////////////////////////////////////////////////////////////////
                // TOP
                ////////////////////////////////////////////////////////////////////
                -size, size, -size, // top-left
                -size, size, size, // bottom-left
                size, size, size, // bottom-right
                size, size, size, // bottom-right
                size, size, -size, // top-right
                -size, size, -size, // top-left

                ////////////////////////////////////////////////////////////////////
                // BOTTOM
                ////////////////////////////////////////////////////////////////////
                -size, -size, -size, // top-left
                -size, -size, size, // bottom-left
                size, -size, size, // bottom-right
                size, -size, size, // bottom-right
                size, -size, -size, // top-right
                -size, -size, -size // top-left
        };

        //these normals are revered from what I think they should be.
        cube_normals = new float[]{
                // Front face
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,

                // Right face
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,

                // Back face
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,

                // Left face  was pos
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,

                // Top face
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                // Bottom face
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f
        };
    }


    //fragment shader code
    String fShaderStr =

            "precision mediump float;                    \n" +
                    "varying vec4 v_Color;                      \n" +
                    "                                           \n" +
                    "void main() {                               \n" +
                    "    gl_FragColor = v_Color;                 \n" +
                    "}                                           \n";

/*
            "precision mediump float;       \n"
                    + "varying vec4 v_Color;          \n"
                    + "varying vec3 v_Grid;           \n"
                    + "void main() {                  \n"
                    + "  float depth = gl_FragCoord.z / gl_FragCoord.w; // Calculate world-space distance. \n"
                    + "  if ((mod(abs(v_Grid.x), 10.0) < 0.1) || (mod(abs(v_Grid.z), 10.0) < 0.1)) {       \n"
                    + "     gl_FragColor = max(0.0, (90.0-depth) / 90.0) * vec4(1.0, 1.0, 1.0, 1.0)        \n"
                    + "     + min(1.0, depth / 90.0) * v_Color;                                            \n"
                    + "  } else {                         \n"
                    + "    gl_FragColor = v_Color;        \n"
                    + " }                                 \n"
                    + "}                                  \n";
*/
    //vertex shader code.
    String vShaderStr =

            "uniform mat4 u_Model;              \n" +
                    "uniform mat4 u_MVP;                \n" +
                    "uniform mat4 u_MVMatrix;           \n" +
                    "uniform vec3 u_LightPos;           \n" +
                    "attribute vec4 a_Position;         \n" +
                    "attribute vec4 a_Color;            \n" +
                    "attribute vec3 a_Normal;           \n" +
                    "varying vec4 v_Color;              \n" +
                    "varying vec3 v_Grid;               \n" +

                    "void main() {                                                         \n" +
                    "   v_Grid = vec3(u_Model * a_Position);                               \n" +
                    "   vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);              \n" +
                    "   vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));     \n" +
                    "   float distance = length(u_LightPos - modelViewVertex);             \n" +
                    "   vec3 lightVector = normalize(u_LightPos - modelViewVertex);        \n" +
                    "   float diffuse = max(dot(modelViewNormal, lightVector), 0.5);         \n" +
 //                   "   float diffuse = max(dot(modelViewNormal, lightVector), 0.0);         \n" +
                    "   diffuse = diffuse * (1.0 / (1.0 + (0.00001 * distance * distance))); \n" +
                    "   v_Color = a_Color * diffuse;                                         \n" +
                    "   gl_Position = u_MVP * a_Position;                                    \n" +
                    "}                                                                       \n";


    String TAG = "Cube";


    //finally some methods
    //constructor
    public CubeLight(float s, int id) {
        size = s;
        setupcube();
        //first setup the mVertices correctly.
        mVertices = ByteBuffer
                .allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(mVerticesData);
        mVertices.position(0);

        //need to setup cube colors here:

        setColor(id);

        ByteBuffer bbNormals = ByteBuffer.allocateDirect(cube_normals.length * 4);
        bbNormals.order(ByteOrder.nativeOrder());
        mNormals = bbNormals.asFloatBuffer();
        mNormals.put(cube_normals);
        mNormals.position(0);


        //setup the shaders
        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        // Load the vertex/fragment shaders
        vertexShader = myStereoRenderer.LoadShader(GLES30.GL_VERTEX_SHADER, vShaderStr);
        fragmentShader = myStereoRenderer.LoadShader(GLES30.GL_FRAGMENT_SHADER, fShaderStr);

        // Create the program object
        programObject = GLES30.glCreateProgram();

        if (programObject == 0) {
            Log.e(TAG, "So some kind of error, but what?");
            return;
        }

        GLES30.glAttachShader(programObject, vertexShader);
        GLES30.glAttachShader(programObject, fragmentShader);

        // Bind vPosition to attribute 0
        GLES30.glBindAttribLocation(programObject, 0, "vPosition");

        // Link the program
        GLES30.glLinkProgram(programObject);

        // Check the link status
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0);

        if (linked[0] == 0) {
            Log.e(TAG, "Error linking program:");
            Log.e(TAG, GLES30.glGetProgramInfoLog(programObject));
            GLES30.glDeleteProgram(programObject);
            return;
        }

        // Store the program object
        mProgramObject = programObject;

        //now everything is setup and ready to draw.
    }

    public void setColor(int id) {
        float[] rgb = myColor.pickcolor(id);
        float[] cube_colors = new float[]{
                // front
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],

                // right, blue
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],

                // back, also green
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],

                // left, also blue
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],

                // top, red
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],

                // bottom, also red
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
                rgb[0], rgb[1], rgb[2], rgb[3],
        };

        ByteBuffer bbColors = ByteBuffer.allocateDirect(cube_colors.length * 4);
        bbColors.order(ByteOrder.nativeOrder());
        mColors = bbColors.asFloatBuffer();
        mColors.put(cube_colors);
        mColors.position(0);
    }

    public void draw(float[] mvpMatrix, float[] modelCube, float[] modelView, float[] lightPosInEyeSpace, int id) {

        // Use the program object
        GLES30.glUseProgram(mProgramObject);

        // get handles
        cubePositionParam = GLES30.glGetAttribLocation(mProgramObject, "a_Position");
        cubeNormalParam = GLES30.glGetAttribLocation(mProgramObject, "a_Normal");
        cubeColorParam = GLES30.glGetAttribLocation(mProgramObject, "a_Color");

        cubeModelParam = GLES30.glGetUniformLocation(mProgramObject, "u_Model");
        cubeModelViewParam = GLES30.glGetUniformLocation(mProgramObject, "u_MVMatrix");
        cubeModelViewProjectionParam = GLES30.glGetUniformLocation(mProgramObject, "u_MVP");
        cubeLightPosParam = GLES30.glGetUniformLocation(mProgramObject, "u_LightPos");
        myStereoRenderer.checkGlError("glGetUniformLocation");

        GLES30.glEnableVertexAttribArray(cubePositionParam);
        GLES30.glEnableVertexAttribArray(cubeNormalParam);
        GLES30.glEnableVertexAttribArray(cubeColorParam);


        GLES30.glUniform3fv(cubeLightPosParam, 1, lightPosInEyeSpace, 0);

        // Set the Model in the shader, used to calculate lighting
        GLES30.glUniformMatrix4fv(cubeModelParam, 1, false, modelCube, 0);

        // Set the ModelView in the shader, used to calculate lighting
        GLES30.glUniformMatrix4fv(cubeModelViewParam, 1, false, modelView, 0);

        // Set the position of the cube
        GLES30.glVertexAttribPointer(cubePositionParam, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 0, mVertices);

        // Set the ModelViewProjection matrix in the shader.
        GLES30.glUniformMatrix4fv(cubeModelViewProjectionParam, 1, false, mvpMatrix, 0);

        myStereoRenderer.checkGlError("glUniformMatrix4fv");

        setColor(id);

        // Set the normal positions of the cube, again for shading
        GLES30.glVertexAttribPointer(cubeNormalParam, 3, GLES30.GL_FLOAT, false, 0, mNormals);
        GLES30.glVertexAttribPointer(cubeColorParam, 4, GLES30.GL_FLOAT, false, 0, mColors);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
    }
}
