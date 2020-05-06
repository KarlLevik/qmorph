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

/** A class which opens an "about" dialog window. */

public class AboutDialog extends Dialog implements ItemListener {
    Button ok;
    TextArea textArea;
    String text= ""+
	"                           -=<#[ MeshDitor ]#>=-\n"+ 
	"\n"+
	"             The Q-Morph algorithm was developed by\n"+
	"    Steven J. Owen, Mathew L. Staten, Scott A. Canann, and Sunil Saigal.\n"+
	"\n"+
	"             The Clean-Up algorithm was developed by\n"+
	"                Paul Kinney of Ford Motor Company.\n"+
	"\n"+
	"          The global smoothing algorithm was developed by\n"+
	"     Scott A. Canann, Joseph R. Tristano, and Matthew L. Staten.\n"+
	"\n"+
	"    This implementation is based on these algorithms as outlined in the\n"+
	"    corresponding papers. It was developed as a part of my thesis for\n"+
	"    the siv.ing. degree at Dept. of Informatics, Univ. of Oslo, Norway.\n"+
	"\n"+
	"            Karl Erik Levik <karll@ifi.uio.no>\n";
    GridBagLayout gridbag;

    public AboutDialog(Frame f) {
	super(f, "About MeshDitor", true);

	gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	setLayout(gridbag);

	c.weightx = 0.0;
	c.weighty = 0.0;
	c.ipadx= 0;
	c.ipady= 0;
	c.fill= GridBagConstraints.NONE;

    	add(textArea= new TextArea(text, 18, 80, TextArea.SCROLLBARS_NONE));
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
