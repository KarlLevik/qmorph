package com.github.karllevik.qmorph.viewer;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.github.karllevik.qmorph.meshing.GeomBasics;

/**
 * The Panel class with step button, zoom menu, and axis and grid toggle buttons
 * etc.
 */
class GControls extends Panel implements ItemListener {
	Label constructStatus, zoomText;
	public Label clickStatus = new Label("3");
	Checkbox grid, axis;
	GUI gui;
	GCanvas canvas;
	Button b;

	/**
	 * Constructor for the panel.
	 *
	 * @param gui  a pointer to the GUI instance
	 * @param cvas a pointer to the Canvas instance
	 */
	public GControls(GUI gui, GCanvas cvas) {
		this.gui = gui;
		this.canvas = cvas;

		add(constructStatus = new Label("# of nodes remaining: "));
		add(clickStatus);
		add(grid = new Checkbox("Show grid", true));
		add(axis = new Checkbox("Show axis", true));
		grid.addItemListener(this);
		axis.addItemListener(this);

		add(zoomText = new Label("View: "));
		Choice zoom = new Choice();
		zoom.addItemListener(this);
		zoom.addItem("400%");
		zoom.addItem("200%");
		zoom.addItem("100%");
		zoom.addItem("90%");
		zoom.addItem("80%");
		zoom.addItem("70%");
		zoom.addItem("60%");
		zoom.addItem("50%");
		zoom.addItem("40%");
		zoom.addItem("30%");
		zoom.addItem("20%");
		zoom.addItem("10%");
		zoom.setBackground(Color.lightGray);
		zoom.select("100%");
		add(zoom);

		add(b = new Button("Step"));
		b.addActionListener(new MyButtonActionListener());
	}

	/**
	 * Method which is automatically called when the state of the subscribed items
	 * changes due to user interaction. The item is identified and the required
	 * action is taken.
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {

		if (e.getSource() instanceof Choice) {
			String zoom = (String) e.getItem();
			if (zoom.equals("400%")) {
				gui.scale = 400;
				canvas.setScale(gui.scale);
				canvas.repaint();
			} else if (zoom.equals("200%")) {
				gui.scale = 200;
				canvas.setScale(gui.scale);
				canvas.repaint();
			} else if (zoom.equals("100%")) {
				gui.scale = 100;
				canvas.setScale(gui.scale);
				canvas.repaint();
			} else if (zoom.equals("90%")) {
				gui.scale = 90;
				canvas.setScale(gui.scale);
				canvas.repaint();
			} else if (zoom.equals("80%")) {
				gui.scale = 80;
				canvas.setScale(gui.scale);
				canvas.repaint();
			} else if (zoom.equals("70%")) {
				gui.scale = 70;
				canvas.setScale(gui.scale);
				canvas.repaint();
			} else if (zoom.equals("60%")) {
				gui.scale = 60;
				canvas.setScale(gui.scale);
				canvas.repaint();
			} else if (zoom.equals("50%")) {
				gui.scale = 50;
				canvas.setScale(gui.scale);
				canvas.repaint();
			} else if (zoom.equals("40%")) {
				gui.scale = 40;
				canvas.setScale(gui.scale);
				canvas.repaint();
			} else if (zoom.equals("30%")) {
				gui.scale = 30;
				canvas.setScale(gui.scale);
				canvas.repaint();
			} else if (zoom.equals("20%")) {
				gui.scale = 20;
				canvas.setScale(gui.scale);
				canvas.repaint();
			} else if (zoom.equals("10%")) {
				gui.scale = 10;
				canvas.setScale(gui.scale);
				canvas.repaint();
			}

		} else if (e.getSource() instanceof Checkbox) {
			String box = (String) e.getItem();
			if (box.equals("Show grid")) {
				gui.grid = grid.getState();
				canvas.repaint();
			} else if (box.equals("Show axis")) {
				gui.axis = axis.getState();
				canvas.repaint();
			}
		}
	}

	/** A listener class for the step button. */
	class MyButtonActionListener implements ActionListener {

		/**
		 * Method which is automatically called when the button is pressed. The required
		 * action is taken.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			MsgDialog md;
			GeomBasics method = GeomBasics.getCurMethod();
			if (method != null) {
				method.step(); // Run one more step ...

				if (GeomBasics.leftmost == null) {
					GeomBasics.findExtremeNodes();
				}
				// canvas.repaint();
				// GeomBasics.findExtremeNodes();
				canvas.resize(GeomBasics.leftmost.x, GeomBasics.lowermost.y, GeomBasics.rightmost.x, GeomBasics.uppermost.y, gui.scale);
			} else {
				md = new MsgDialog(gui.f, "Program message", "You must choose a method first.", 40, 1);
				md.show();
			}
		}
	}
}
