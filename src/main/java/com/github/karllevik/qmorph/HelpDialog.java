package com.github.karllevik.qmorph;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/** A class which opens a help dialog window. */

public class HelpDialog extends Dialog implements ItemListener {
	Button ok;
	TextArea textArea;
	String text = "" + "                Quick help for MeshDitor\n" + "                ------------------------\n" + "\n"
			+ "    This GUI was written mainly to assist debugging and tuning\n" + "of parameters for the implementation of the Q-Morph algorithm.\n"
			+ "So if it looks like a mess, this is the reason.\n" + "\n" + "Most of the menu items should be self-explanatory. Nevertheless, the details\n"
			+ "of some of the interesting items are given below:\n" + "\n" + "* 'Export mesh to LaTeX file' - Assuming that you have the epic and eepic\n"
			+ "packages for LaTeX, you can include exported meshes in LaTeX documents.\n" + "\n"
			+ "* 'Step on' - When selected, the incremental Delaunay method and the Q-Morph\n"
			+ "method will run in step mode. Use the step button (in the lower part of the\n" + "program window) to step through the methods.\n" + "\n"
			+ "* 'Run incr. Delaunay method' - Run the implementation of an incremental\n" + "Delaunay algorithm.\n" + "\n"
			+ "* 'Run Q-Morph' - Run the implementation of the Q-Morph algorithm.\n" + "Supply parameters for the algorithm in the dialog box.\n";

	GridBagLayout gridbag;

	public HelpDialog(Frame f) {
		super(f, "Quick help for MeshDitor", true);

		gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);

		c.weightx = 0.0;
		c.weighty = 0.0;
		c.ipadx = 0;
		c.ipady = 0;
		c.fill = GridBagConstraints.NONE;

		add(textArea = new TextArea(text, 18, 80, TextArea.SCROLLBARS_VERTICAL_ONLY));
		textArea.setEditable(false);
		textArea.setBackground(Color.black);
		textArea.setForeground(Color.yellow);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(textArea, c);

		add(ok = new Button("OK"));
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(ok, c);

		ok.addActionListener(new ButtonActionListener());
		pack();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
	}

	class ButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.equals("OK")) {
				dispose();
			}
		}
	}

}
