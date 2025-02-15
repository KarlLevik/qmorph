package com.github.karllevik.qmorph.viewer;

import java.awt.Frame;

/**
 * This class outputs messages to the user.
 */

public class Msg {
	public static boolean debugMode = false;

	/** Output an error message and then exit the program. */
	public static void error(String err) {
		Frame f = new Frame();
		MsgDialog errorDialog;
		Error error = new Error(err);
		error.printStackTrace();

		errorDialog = new MsgDialog(f, "Program error", "Program error.\nSee the log-file for details.", 40, 2);
		errorDialog.show();

		System.exit(1);
	}

	/** Output a warning message. */
	public static void warning(String warn) {
		if (debugMode) {
			System.out.println("Warning: " + warn);
		}
	}

	/** Output a debug message. */
	public static void debug(String msg) {
		if (debugMode) {
			System.out.println("Debug: " + msg);
		}
	}
}
