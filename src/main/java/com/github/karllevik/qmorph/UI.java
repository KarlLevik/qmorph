package com.github.karllevik.qmorph;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

/**
 * This class implements the command-line user interface. It reads and interprets 
 * command line parameters, sets the corresponding globally available variables, and 
 * initiates the main class.
 *
 * This implementation only supports 2D triangle meshes.
 * @author Karl Erik Levik, karll@ifi.uio.no
*/

public class UI {
    public static void main(String[] args) {

	boolean lengthsOpt= false, anglesOpt= false;
	String filename= null;
	int count=0;
	for (count=0; count < args.length; count++) {
	    if (args[count].equals("-angles"))
		anglesOpt= true;
	    else if (args[count].equals("-lengths"))
		lengthsOpt= true;
	    // Other options might be:
	    // -sf   Smaller edges to be processed first
	}
	
	if (count>= 1)
	    filename = args[count-1];
	
	GeomBasics.setParams(filename, ".", lengthsOpt, anglesOpt);
	startMorpher();
    }

    public static void startMorpher()
    {
	Msg.debug("starting...");

	com.github.karllevik.qmorph.GeomBasics.loadTriangleMesh();
	QMorph qm= new QMorph();
	qm.writeQuadMesh("qmesh.dta", qm.getElementList());
	
	Msg.debug("frontList:");
	qm.printEdgeList(qm.getFrontList());
	Msg.debug("edgeList:");
	qm.printEdgeList(qm.getEdgeList());
	Edge.printStateLists();
	qm.printQuads(qm.getElementList());
	Msg.debug("Done!");
    }
}
