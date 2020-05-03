package meshditor;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** A class which opens a message dialog box. */

public class MsgDialog extends Dialog implements ItemListener {
    Button ok;
    TextArea textArea;
    GridBagLayout gridbag;

    public MsgDialog(Frame f, String title, String text, int x, int y) {
	super(f, title, true); 

	gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	setLayout(gridbag);

	c.weightx = 0.0;
	c.weighty = 0.0;
	c.ipadx= 0;
	c.ipady= 0;
	c.fill= GridBagConstraints.NONE;

    	add(textArea= new TextArea(text, y, x, TextArea.SCROLLBARS_NONE));
	textArea.setEditable(false);
	textArea.setBackground(Color.black);
	textArea.setForeground(Color.yellow);
	textArea.setFont(new Font("Monospaced", Font.PLAIN, 10));	
	c.gridwidth = GridBagConstraints.REMAINDER; //end row
	gridbag.setConstraints(textArea, c);

	add(ok =  new Button("OK"));
	c.gridwidth = GridBagConstraints.REMAINDER; //end row
	gridbag.setConstraints(ok, c);

	ok.addActionListener(new ButtonActionListener());
	pack();
    }

    public void itemStateChanged(ItemEvent e) {}

    class ButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    String command= e.getActionCommand();
	    if (command.equals("OK")) {
		dispose();
	    }
	}
    }
}
