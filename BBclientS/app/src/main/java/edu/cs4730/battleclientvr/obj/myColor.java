package edu.cs4730.battleclientvr.obj;

/**
 * Created by Seker on 7/1/2015.
 *
 * Some color static methods so I can setup the color quickly and not think hard either.
 */

public class myColor {
     //changed to the battle bot colors.
    /*
    static float[] red() {
        return new float[]{
                Color.red(Color.RED) / 255f,
                Color.green(Color.RED) / 255f,
                Color.blue(Color.RED) / 255f,
                1.0f
        };
    }

    static float[] green() {
        return new float[]{
                Color.red(Color.GREEN) / 255f,
                Color.green(Color.GREEN) / 255f,
                Color.blue(Color.GREEN) / 255f,
                1.0f
        };
    }

    static float[] blue() {
        return new float[]{
                Color.red(Color.BLUE) / 255f,
                Color.green(Color.BLUE) / 255f,
                Color.blue(Color.BLUE) / 255f,
                1.0f
        };
    }

    static float[] yellow() {
        return new float[]{
                Color.red(Color.YELLOW) / 255f,
                Color.green(Color.YELLOW) / 255f,
                Color.blue(Color.YELLOW) / 255f,
                1.0f
        };
    }

    static float[] cyan() {
        return new float[]{
                Color.red(Color.CYAN) / 255f,
                Color.green(Color.CYAN) / 255f,
                Color.blue(Color.CYAN) / 255f,
                1.0f
        };
    }

    static float[] gray() {
        return new float[]{
                Color.red(Color.GRAY) / 255f,
                Color.green(Color.GRAY) / 255f,
                Color.blue(Color.GRAY) / 255f,
                1.0f
        };

    }
    */
    static float[] black() {return new float[]{ 255/ 255f, 255/ 255f, 255/ 255f, 1.0f }; }
     //list.add(new Color(255,0  ,0  )); //0 Red

    static float[] red() { return new float[]{ 255/ 255f, 0/ 255f, 0/ 255f, 1.0f }; }
     //list.add(new Color(0  ,0  ,255)); //1 Blue
    static float[] blue() { return new float[]{ 0/ 255f, 0/ 255f, 255/ 255f, 1.0f }; }
    //list.add(new Color(0  ,128,0  )); //2 Green
    static float[] green() { return new float[]{ 0/ 255f, 128/ 255f, 0/ 255f, 1.0f }; }
    //list.add(new Color(255,105,0  )); //3 Orange
    static float[] orange() { return new float[]{ 255/ 255f, 105/ 255f, 0/ 255f, 1.0f }; }
    //list.add(new Color(255,204,0  )); //4 Yellow
    static float[] yellow() { return new float[]{ 255/ 255f, 204/ 255f, 0/ 255f, 1.0f }; }
    //case 13: me = new Color(128,0,128); break; //purple
    static float[] purple() { return new float[]{ 114/ 255f, 66/ 255f, 163/ 255f, 1.0f }; }
    //   case 22: me = new Color(73,56,41); break; //brown
    static float[] brown() { return new float[]{ 137/ 255f, 104/ 255f, 89/ 255f, 1.0f }; }
    //list.add(new Color(128,128,128)); //7 gray
    static float[] gray() { return new float[]{ 128/ 255f, 128/ 255f, 128/ 255f, 1.0f }; }
    //list.add(new Color(0  ,128,128)); //8 medium cyan  which is blue-green
    static float[] cyan() { return new float[]{ 0/ 255f, 128/ 255f, 128/ 255f, 1.0f }; }
    //list.add(new Color(128,0  ,128)); //9 magenta  which is red-blue
    static float[] magenta() { return new float[]{ 255/ 255f, 0/ 255f, 255/ 255f, 1.0f }; }
    //   case 14: me = new Color(128,128,0); break; //teal
    static float[] teal() { return new float[]{ 128/ 255f, 128/ 255f, 0/ 255f, 1.0f }; }

    // list.add(new Color(128,0  ,0  )); //0 dark Red
    static float[] darkred() { return new float[]{ 128/ 255f, 0/ 255f, 0/ 255f, 1.0f }; }
    // list.add(new Color(0  ,0  ,128)); //1 dark Blue
    static float[] darkblue() { return new float[]{ 0/ 255f, 0/ 255f, 128/ 255f, 1.0f }; }
    //   list.add(new Color( 85,107,47 )); //2 dark Green  dark olive green
    static float[] darkgreen() { return new float[]{ 85/ 255f, 107/ 255f, 47/ 255f, 1.0f }; }
    //list.add(new Color(169, 84,0  )); //3 Dark Orange
    static float[] darkorange() { return new float[]{ 169/ 255f, 84/ 255f, 0/ 255f, 1.0f }; }
    //list.add(new Color(218,165,32 )); //4 Dark Yellow
    static float[] darkyellow() { return new float[]{ 218/ 255f, 165/ 255f, 32/ 255f, 1.0f }; }
    //list.add(new Color(109,31 ,148)); //5 Dark purple
    static float[] darkpurple() { return new float[]{ 109/ 255f, 31/ 255f, 148/ 255f, 1.0f }; }
    // list.add(new Color(73 ,56 ,41 )); //6 dark brown
    static float[] darkbrown() { return new float[]{ 73/ 255f, 56/ 255f, 41/ 255f, 1.0f }; }
    //list.add(new Color( 96, 96, 96)); //7 darkgray
    static float[] darkgray() { return new float[]{ 169/ 255f, 169/ 255f, 169/ 255f, 1.0f }; }
    // list.add(new Color(0  , 96, 96)); //8 dark cyan  which is blue-green
    static float[] darkcyan() { return new float[]{ 0/ 255f, 96/ 255f, 96/ 255f, 1.0f }; }
    // list.add(new Color( 96,0  , 96)); //9 magenta  which is red-blue
    static float[] darkbmagenta() { return new float[]{ 96/ 255f, 0/ 255f, 96/ 255f, 1.0f }; }
    // list.add(new Color(70 ,130,180)); //10 steal blue
    static float[] steelblue() { return new float[]{ 70/ 255f, 130/ 255f, 180/ 255f, 1.0f }; }


    // list.add(new Color(255,192,203)); //0 light red Pink
    static float[] lightred() { return new float[]{ 255/ 255f, 192/ 255f, 203/ 255f, 1.0f }; }
    // list.add(new Color(0  ,128,255)); //1 light blue
    static float[] lightblue() { return new float[]{ 0/ 255f, 128/ 255f, 255/ 255f, 1.0f }; }
    // list.add(new Color(106,212,0  )); //2 light Green
     static float[] lightgreen() { return new float[]{ 106/ 255f, 212/ 255f, 0/ 255f, 1.0f }; }
    // list.add(new Color(255,155,66 )); //3 light Orange
    static float[] lightorange() { return new float[]{ 255/ 255f, 155/ 255f, 66/ 255f, 1.0f }; }
    // list.add(new Color(199,129,70  )); //4 lightish Yellow
    static float[] lightyellow() { return new float[]{ 199/ 255f, 129/ 255f, 70/ 255f, 1.0f }; }
    //list.add(new Color(189,122,246)); //5 light purple
    static float[] lightpurple() { return new float[]{ 189/ 255f, 122/ 255f, 246/ 255f, 1.0f }; }
    // list.add(new Color(129,108,91 )); //6 light brown
    static float[] lightbrown() { return new float[]{ 129/ 255f, 108/ 255f, 91/ 255f, 1.0f }; }
    // list.add(new Color(211,211,211)); //7 lighter gray
    static float[] lightgray() { return new float[]{ 211/ 255f, 211/ 255f, 211/ 255f, 1.0f }; }
    // list.add(new Color(0  ,255,255)); //8 cyan  which is blue-green
    static float[] lightcyan() { return new float[]{ 0/ 255f, 255/ 255f, 255/ 255f, 1.0f }; }
    //list.add(new Color(255,0  ,255)); //9 magenta  which is red-blue
    static float[] lightmagenta() { return new float[]{ 255/ 255f, 0/ 255f, 255/ 255f, 1.0f }; }
    //list.add(new Color(51 ,153,204)); //10 lighter steal blue.
    static float[] lightsteelblue() { return new float[]{ 51/ 255f, 153/ 255f, 204/ 255f, 1.0f }; }


    /*   this is for the myColor...
     //list.add(new Color(128,128,0  )); //10 teal?
     static float[] teal() { return new float[]{ 128/ 255f, 0/ 255f, 128/ 255f, 1.0f }; }
*/


    public static float[] pickcolor(int id) {
        switch (id) {
            case 0: return red(); //Color.red; break;
            case 1: return blue(); //Color.blue
            case 2: return  green();
            case 3: return orange(); //Color.orange
            case 4: return  yellow();
            case 5: return purple();
            case 6: return brown();
            case 7: return gray();
            case 8: return cyan();
            case 9: return magenta();
            case 10: return teal();

            case 11: return darkred();
            case 12: return darkblue();
            case 13: return darkgreen();
            case 14: return darkorange();
            case 15: return darkyellow();
            case 16: return darkpurple();
            case 17: return darkbrown();
            case 18: return darkgray();
            case 19: return darkcyan();
            case 20: return darkbmagenta();
            case 21: return steelblue(); //steel blue

            case 22: return lightred();
            case 23: return lightblue();
            case 24: return lightgreen();
            case 25: return lightorange();
            case 26: return lightyellow();
            case 27: return lightpurple();
            case 28: return lightbrown();
            case 29: return lightgray();
            case 30: return lightcyan();
            case 31: return lightmagenta();
            case 32: return lightsteelblue();

            default: return black();
        }

    }


}
