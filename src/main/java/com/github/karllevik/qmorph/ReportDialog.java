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

/** A class which opens an "mesh metrics report" dialog window. */

public class ReportDialog extends Dialog implements ItemListener {
	Button ok;
	TextArea textArea;
	GridBagLayout gridbag;

	public ReportDialog(Frame f, String text) {
		super(f, "Mesh Metrics Report", true);

		gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);

		c.weightx = 0.0;
		c.weighty = 0.0;
		c.ipadx = 0;
		c.ipady = 0;
		c.fill = GridBagConstraints.NONE;

		add(textArea = new TextArea(text, 18, 80, TextArea.SCROLLBARS_NONE));
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
