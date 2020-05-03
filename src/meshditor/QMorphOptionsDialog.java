package meshditor;

import java.awt.Dialog;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.Frame;
import java.awt.Button;
import java.awt.Label;
import java.awt.Container;
import java.awt.Checkbox;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/** A class for an options dialog for supplying parameters to the Q-Morph 
 * implementation. 
 */
public class QMorphOptionsDialog extends Dialog implements ItemListener {
    boolean runState= false;
    TextField epsilon1, epsilon2;
    TextField chevronMin;
    TextField coinctol, movetolerance, obstol, deltafactor, mymin, 
	thetamax, tol, gamma, maxiter;

    Label epsilon1Label, epsilon2Label;
    Label chevronMinLabel;
    Label coinctolLabel, movetoleranceLabel, obstolLabel, deltafactorLabel, myminLabel, 
	thetamaxLabel, tolLabel, gammaLabel, maxiterLabel;

    Button run, defaults, cancel;

    Container seamContainer, topoContainer, smoothContainer, buttonContainer;
    Checkbox tri2quadBox, topoBox, smoothBox;

    GridBagLayout gridbag;

    public QMorphOptionsDialog(Frame f) {//, String title, boolean modal) {
	super(f, "Parameters for QMorph", true); //, title, modal);

	gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	
	setLayout(gridbag);
	c.weightx = 0.0;
	c.weighty = 0.0;
	c.ipadx= 0;
	c.ipady= 0;
	c.insets= new Insets(0,0,0,0);

	seamContainer= new Container();
	seamContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
	smoothContainer= new Container();
	smoothContainer.setLayout(new GridLayout(3, 3));
	topoContainer= new Container();
	topoContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
	buttonContainer= new Container();
	buttonContainer.setLayout(new FlowLayout(FlowLayout.CENTER));

       	seamContainer.add(epsilon1Label= new Label("Epsilon1= e1 * PI, e1= "));
	seamContainer.add(epsilon1= new TextField("",6));
       	seamContainer.add(epsilon2Label= new Label("Epsilon2= e2 * PI, e2= "));
	seamContainer.add(epsilon2= new TextField("",6));

	topoContainer.add(chevronMinLabel= 
			  new Label("Minimum size of greatest angle in a chevron"));
	topoContainer.add(chevronMin= new TextField("",6));
			  
	smoothContainer.add(coinctolLabel= new Label("COINCTOL"));	
	smoothContainer.add(coinctol= new TextField("", 6));	
	smoothContainer.add(movetoleranceLabel= new Label("MOVETOLERANCE"));	
	smoothContainer.add(movetolerance= new TextField("", 6));	
	smoothContainer.add(obstolLabel= new Label("OBSTOL"));	
	smoothContainer.add(obstol= new TextField("", 6));	
	smoothContainer.add(deltafactorLabel= new Label("DELTAFACTOR"));	
	smoothContainer.add(deltafactor= new TextField("", 6));	
	smoothContainer.add(myminLabel= new Label("MYMIN"));	
	smoothContainer.add(mymin= new TextField("", 6));	
	smoothContainer.add(thetamaxLabel= new Label("THETAMAX"));	
	smoothContainer.add(thetamax= new TextField("", 6));	
	smoothContainer.add(tolLabel= new Label("TOL"));	
	smoothContainer.add(tol= new TextField("", 6));	
	smoothContainer.add(gammaLabel= new Label("GAMMA"));	
	smoothContainer.add(gamma= new TextField("", 6));	
	smoothContainer.add(maxiterLabel= new Label("MAXITER"));	
	smoothContainer.add(maxiter= new TextField("", 6));	

	buttonContainer.add(run =  new Button("Run"));
	buttonContainer.add(defaults =  new Button("Set defaults"));
	defaults.setActionCommand("Defaults");
	buttonContainer.add(cancel =  new Button("Cancel"));

	run.addActionListener(new ButtonActionListener());
	defaults.addActionListener(new ButtonActionListener());
	cancel.addActionListener(new ButtonActionListener());

	setDefaults();

	add(tri2quadBox= new Checkbox("Triangle to quad conversion", null, true),0);
	tri2quadBox.addItemListener(this);
	c.gridwidth = GridBagConstraints.REMAINDER; //end row
	c.fill= GridBagConstraints.NONE;
	gridbag.setConstraints(tri2quadBox, c);

	add(seamContainer, 1);
	c.gridwidth = GridBagConstraints.REMAINDER; //end row
	c.fill= GridBagConstraints.BOTH;
	gridbag.setConstraints(seamContainer, c);

	add(topoBox= new Checkbox("Topological clean-up", true),2);
	topoBox.addItemListener(this);
	c.gridwidth = GridBagConstraints.REMAINDER; //end row
	c.fill= GridBagConstraints.NONE;
	gridbag.setConstraints(topoBox, c);

	add(topoContainer, 3);
	c.gridwidth = GridBagConstraints.REMAINDER; //end row
	c.fill= GridBagConstraints.BOTH;
	gridbag.setConstraints(topoContainer, c);

	add(smoothBox= new Checkbox("Global smoothing", true),4);
	smoothBox.addItemListener(this);
	c.gridwidth = GridBagConstraints.REMAINDER; //end row
	c.fill= GridBagConstraints.NONE;
	gridbag.setConstraints(smoothBox, c);

	add(smoothContainer, 5);
	c.gridwidth = GridBagConstraints.REMAINDER; //end row
	c.fill= GridBagConstraints.BOTH;
	gridbag.setConstraints(smoothContainer, c);

	add(buttonContainer, 6);
	c.gridwidth = GridBagConstraints.REMAINDER; //end row
	c.fill= GridBagConstraints.BOTH;
	gridbag.setConstraints(buttonContainer, c);
	
	pack();
    }

    public void itemStateChanged(ItemEvent e) {
	if (e.getSource() instanceof Checkbox) {
	    String box= (String) e.getItem();
	    if (box.equals("Triangle to quad conversion")) {
		if (tri2quadBox.getState()== true) {
		    epsilon1.setEnabled(true);
		    epsilon2.setEnabled(true);		    
		}
		else {
		    epsilon1.setEnabled(false);
		    epsilon2.setEnabled(false);		    
		}
	    }
	    else if (box.equals("Topological clean-up")) {
		if (topoBox.getState()== true)
		    chevronMin.setEnabled(true);
		else
		    chevronMin.setEnabled(false);
	    }
	    else if (box.equals("Global smoothing")) {

		if (smoothBox.getState()== true) {
		    coinctol.setEnabled(true);
		    movetolerance.setEnabled(true);		    
		    obstol.setEnabled(true);
		    deltafactor.setEnabled(true);
		    mymin.setEnabled(true);
		    thetamax.setEnabled(true);
		    tol.setEnabled(true);
		    gamma.setEnabled(true);
		    maxiter.setEnabled(true);
		}
		else {
		    coinctol.setEnabled(false);
		    movetolerance.setEnabled(false);		    
		    obstol.setEnabled(false);
		    deltafactor.setEnabled(false);
		    mymin.setEnabled(false);
		    thetamax.setEnabled(false);
		    tol.setEnabled(false);
		    gamma.setEnabled(false);
		    maxiter.setEnabled(false);
		}

	    }
	    repaint();
	}
    }

    public void setDefaults() {
	double tmp;
	epsilon1.setText(Double.toString(Constants.defaultE1Factor));
	epsilon2.setText(Double.toString(Constants.defaultE2Factor));
	tmp= Math.toDegrees(Constants.defaultCHEVRONMIN);
	chevronMin.setText(Double.toString(tmp));
	coinctol.setText(Double.toString(Constants.defaultCOINCTOL));
	movetolerance.setText(Double.toString(Constants.defaultMOVETOLERANCE));
	obstol.setText(Double.toString(Constants.defaultOBSTOL));
	deltafactor.setText(Double.toString(Constants.defaultDELTAFACTOR));
	mymin.setText(Double.toString(Constants.defaultMYMIN));
	tmp= Math.toDegrees(Constants.defaultTHETAMAX);
	thetamax.setText(Double.toString(tmp));
	tol.setText(Double.toString(Constants.defaultTOL));
	gamma.setText(Double.toString(Constants.defaultGAMMA));
	maxiter.setText(Integer.toString(Constants.defaultMAXITER));
	repaint();
    }

    public void copyToProgramParameters() {
	double tmp;
	Constants.EPSILON1= 
	    java.lang.Math.PI * Double.parseDouble(epsilon1.getText().trim());
	Constants.EPSILON2= 
	    java.lang.Math.PI * Double.parseDouble(epsilon2.getText().trim());
	tmp= Double.parseDouble(chevronMin.getText().trim());
	Constants.CHEVRONMIN= Math.toRadians(tmp);
	Constants.COINCTOL= Double.parseDouble(coinctol.getText().trim());
	Constants.MOVETOLERANCE= Double.parseDouble(movetolerance.getText().trim());
	Constants.OBSTOL= Double.parseDouble(obstol.getText().trim());
	Constants.DELTAFACTOR= Double.parseDouble(deltafactor.getText().trim());
	Constants.MYMIN= Double.parseDouble(mymin.getText().trim());
	tmp= Double.parseDouble(thetamax.getText().trim());
	Constants.THETAMAX= Math.toRadians(tmp);
	Constants.TOL= Double.parseDouble(tol.getText().trim());
	Constants.GAMMA= Double.parseDouble(gamma.getText().trim());
	Constants.MAXITER= Integer.parseInt(maxiter.getText().trim());

	Constants.doTri2QuadConversion=tri2quadBox.getState();
	Constants.doCleanUp= topoBox.getState();
	Constants.doSmooth= smoothBox.getState();
    }

    public boolean runPressed() {
	return runState;
    }

    class ButtonActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    String command= e.getActionCommand();
	    if (command.equals("Run")) {
		runState= true;
		dispose();
	    }
	    else if (command.equals("Defaults"))
		setDefaults();
	    else if (command.equals("Cancel"))
		dispose();
	}
    }

}
