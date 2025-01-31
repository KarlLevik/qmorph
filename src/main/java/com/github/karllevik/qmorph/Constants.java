package com.github.karllevik.qmorph;

/**
 * This class holds the program "constants". That is, they are given as parameters to
 * the Q-Morph implementation. 
*/

public class Constants {
    /** A boolean indicating whether the triangle to quad conversion should run. */ 
    public static boolean doTri2QuadConversion= true;
    /** A boolean indicating whether the topological cleanup should run. */ 
    public static boolean doCleanUp= true;
    /** A boolean indicating whether the global smoothing should run. */ 
    public static boolean doSmooth= true;

    /* Some common constants */ 
    public static final double sqrt3x2= 2.0*Math.sqrt(3.0);

    // Constants for quads
    public static final int base= 0;
    public static final int left= 1;
    public static final int right= 2;
    public static final int top= 3;
    
    // Some useful constants involving PI:
    /** PI/6 or 30 degrees*/
    public static final double PIdiv6 = java.lang.Math.PI/6.0;
    /** PI/2 or 90 degrees */
    public static final double PIdiv2 = java.lang.Math.PI/2.0;
    /** 3*PI/4 or 135 degrees */
    public static final double PIx3div4 = 3*java.lang.Math.PI/4.0;
    /** 5*PI/4 or 225 degrees */
    public static final double PIx5div4 = 5*java.lang.Math.PI/4.0;
    /** 3*PI/2 or 270 degrees */
    public static final double PIx3div2 = 3*java.lang.Math.PI/2.0; 
    /** 2*PI or 360 degrees */
    public static final double PIx2 = java.lang.Math.PI*2.0; 

    // Some useful constants holding the radian values of common angles in degrees
    /** 6 degrees in radians */
    public static final double DEG_6= Math.toRadians(6);
    /** 150 degrees in radians */
    public static final double DEG_150= Math.toRadians(150);
    /** 160 degrees in radians */
    public static final double DEG_160= Math.toRadians(160);
    /** 179 degrees in radians */
    public static final double DEG_179= Math.toRadians(179);
    /** 180 degrees in radians */
    public static final double DEG_180= Math.toRadians(180);
    /** 200 degrees in radians */
    public static final double DEG_200= Math.toRadians(200);    
    
    // Constants for the seam, transition seam and transition split operations: 
    // Note that we must have (EPSILON1 < EPSILON2)
    public static double EPSILON1 = java.lang.Math.PI*0.04;
    public static double EPSILON2 = java.lang.Math.PI*0.09;
    
    /** The minimum size of the greatest angle in a chevron. */ 
    public static double CHEVRONMIN = DEG_200;

    // Constants for side edge selection (EPSILON < EPSILONLARGER)
    public static final double sqrt3div2= Math.sqrt(3.0)/2.0;

    public static final double EPSILON = java.lang.Math.PI/6.0;
    public static final double EPSILONLARGER = java.lang.Math.PI;
    
    // Constants for post smoothing
    /** The node coincidence tolerance */
    public static double COINCTOL= 0.01;      
    /** A value for the move tolerance. I don't know if it's any good. */
    public static double MOVETOLERANCE= 0.01; 
    /** OBS tolerance. Should be 0.1, but may be adjusted to trigger OBS. */
    public static double OBSTOL= 0.1;
    public static double DELTAFACTOR= 0.00001;
    public static double MYMIN= 0.05;  
    /** The maximum angle allowed in an element (not given in the paper) */ 
    public static double THETAMAX= Math.toRadians(200); 
    public static double TOL= 0.00001;
    public static double GAMMA= 0.8;  // 0.8
    public static int MAXITER= 5;

    // And now some doubles to hold the default values of some of the above:
    public static final double defaultE1Factor = 0.04;
    public static final double defaultE2Factor = 0.09;
    public static final double defaultCHEVRONMIN = DEG_200;
    public static final double defaultCOINCTOL= 0.01; // The node coincidence tolerance
    public static final double defaultMOVETOLERANCE= 0.01; // dunno if it's a good choice
    public static final double defaultOBSTOL= 0.1;//should be 0.1, adjust to trigger OBS
    public static final double defaultDELTAFACTOR= 0.00001;
    public static final double defaultMYMIN= 0.05;  
    public static final double defaultTHETAMAX= DEG_200; 
    public static final double defaultTOL= 0.00001;
    public static final double defaultGAMMA= 0.8;  // 0.8
    public static final int defaultMAXITER= 5;

    /** The origin, only used as a reference. */
    static final Node origin= new Node(0,0);
}

