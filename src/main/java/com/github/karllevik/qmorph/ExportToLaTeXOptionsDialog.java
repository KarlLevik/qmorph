package com.github.karllevik.qmorph;

import java.awt.Dialog;
import java.awt.TextField;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.Button;
import java.awt.Label;
import java.awt.Checkbox;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** This class supports exporting of meshes to LaTeX format. 
 * Make sure you include include both packages epic and eepic in the header of your 
 * LaTeX document.
*/
public class ExportToLaTeXOptionsDialog extends Dialog {
    boolean okState= false;
    TextField xCorr, yCorr, unitlength;
    Checkbox nodes;
    Dialog d;
    Button ok, cancel;
    Label xCorrLabel, yCorrLabel, unitlengthLabel;
    Container unitContainer, corrContainer, nodesContainer, buttonContainer;

    public ExportToLaTeXOptionsDialog(Frame f, String title, boolean modal) {
	super(f, title, modal);

	setLayout(new GridLayout(4,1));

	unitContainer= new Container();
	unitContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
	corrContainer= new Container();
	corrContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
	nodesContainer= new Container();
	nodesContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
	buttonContainer= new Container();
	buttonContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
	
       	unitContainer.add(unitlengthLabel=new Label("Length of a unit i mm: "),"West",0);
	unitContainer.add(unitlength= new TextField("10",4), "East", 1);

       	corrContainer.add(xCorrLabel= new Label("Offset for x coordinates: "), "West",0);
	corrContainer.add(xCorr= new TextField("0",4), 1);
	corrContainer.add(yCorrLabel= new Label("Offset for y coordinates: "), 2);
	corrContainer.add(yCorr= new TextField("0",4), "East", 3);

	nodesContainer.add(nodes= 
	  new Checkbox("Visible nodes (diameter of each node is 0.1 units)", true), "Center", 0);

	buttonContainer.add(ok =  new Button("OK"), "West", 0);
	buttonContainer.add(cancel =  new Button("Cancel"), "East", 1);

	ok.addActionListener(new ButtonActionListener());
	cancel.addActionListener(new ButtonActionListener());

	add(unitContainer, 0);
	add(corrContainer, 1);
	add(nodesContainer, 2);
	add(buttonContainer, 3);
	
	pack();
    }


    public int getUnitlength() {
	return Integer.parseInt(unitlength.getText().trim());
    }

    public double getXCorr() {
	return Double.parseDouble(xCorr.getText().trim());
    }

    public double getYCorr() {
	return Double.parseDouble(yCorr.getText().trim());
    }

    public boolean getVisibleNodes() {
	return nodes.getState();
    }

    public boolean okPressed() {
	return okState;
    }

    class ButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    String command= e.getActionCommand();
	    if (command.equals("OK"))
		okState= true;
	    dispose();
	}
    }

}
