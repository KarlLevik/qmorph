package com.github.karllevik.qmorph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * This is the executable class. It has methods for outputting version and help
 * information, and for processing user command line options.
 *
 * @author Karl Erik Levik <karll@ifi.uio.no>
 * @version 1.0
 */

public class MeshDitor {

	public static void main(String[] args) {
		int count = 0;
		String path, dir, filename;
		GUI gui;
		FileOutputStream fos;
		// Capture Java error messages
		try {
			fos = new FileOutputStream("MeshDitor.log");
			MyFilterOutputStream mfops = new MyFilterOutputStream(fos);
			PrintStream pstream = new PrintStream(mfops, true);
			System.setErr(pstream);
		} catch (Exception e) {
			Msg.error("Can not open file MeshDitor.log (to which errors are logged).");
		}

		// Process command line arguments
		if (args != null) {
			for (count = 0; count < args.length; count++) {
				if (args[count].equals("-help") || args[count].equals("--help")) {
					outputVersion();
					outputHelp();
					System.exit(0);
				} else if (args[count].equals("-version") || args[count].equals("--version")) {
					outputVersion();
					System.exit(0);
				} else if (args[count].equals("-noGUI") || args[count].equals("--noGUI")) {
					if (count < args.length - 1) {
						filename = args[args.length - 1];
						GeomBasics.setParams(filename, "", false, false);
						GeomBasics.loadTriangleMesh();
						QMorph qm = new QMorph();
						qm.init();
						qm.run();
						GeomBasics.writeQuadMesh("qmesh.dta", GeomBasics.getElementList());
						System.exit(0);
					} else {
						outputVersion();
						System.out.println("ERROR: You must also supply a filename.");
						outputHelp();
						System.exit(0);
					}
				}
			}
		}

		// Run GUI
		if (count >= 1) {
			path = args[count - 1];

			int i = path.lastIndexOf(File.separator);
			if (i != -1) {
				dir = path.substring(0, i + 1);
				filename = path.substring(i + 1);
			} else {
				dir = "." + File.separator;
				filename = path;
			}
			gui = new GUI(dir, filename);
			gui.startGUI();
		} else {
			gui = new GUI();
			gui.startGUI();
		}
	}

	private static void outputVersion() {
		System.out.println("MeshDitor v1.0");
	}

	private static void outputHelp() {
		System.out.println("USAGE:");
		System.out.println("  java -jar meshditor.jar {OPTIONS} {MESH FILENAME}");
		System.out.println("OPTIONS:");
		System.out.println("  -help       Print version, usage, list these options");
		System.out.println("              and then exit.");
		System.out.println("  -version    Print version and exit.");
		System.out.println("  -noGUI      Do not start GUI, but load the mesh from the");
		System.out.println("              supplied filename and run QMorph with");
		System.out.println("              default parameter values. The result is");
		System.out.println("              written to a file called 'qmesh.dta'.");
	}
}
