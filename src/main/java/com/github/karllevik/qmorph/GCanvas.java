package com.github.karllevik.qmorph;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * The Canvas class which paints the background grid, the nodes, the edges etc.
 */

class GCanvas extends Canvas {
	GUI gui;

	double xmin, ymin, xmax, ymax;
	int gridIncr;
	int scale;
	int width = 640, height = 480;

	int xaxis_yval;
	int yaxis_xval;

	public GCanvas(GUI gui, int scale) {
		this.gui = gui;
		this.scale = scale;

		xmin = -2.0;
		xmax = 2.0;
		ymin = -2.0;
		ymax = 2.0;

		gridIncr = (int) (scale / 10.0);

		int w = (int) ((xmax - xmin) * scale) + 2 * gridIncr;
		int h = (int) ((ymax - ymin) * scale) + 2 * gridIncr;

		if (w > width) {
			width = w;
		}

		if (h > height) {
			height = h;
		}

		setSize(width, height);

		double ymaxXscale = ymax * scale, xminXscale = xmin * scale;
		double rounded_ymaxXscale = signOf(ymax) * (Math.abs(ymaxXscale) + gridIncr - Math.IEEEremainder(Math.abs(ymaxXscale), gridIncr));
		double rounded_xminXscale = signOf(xmin) * (Math.abs(xminXscale) + gridIncr - Math.IEEEremainder(Math.abs(xminXscale), gridIncr));

		xaxis_yval = gridIncr + (int) (rounded_ymaxXscale);
		yaxis_xval = gridIncr + (int) (-rounded_xminXscale);
	}

	public GCanvas(GUI gui, double xmin, double ymin, double xmax, double ymax, int scale) {
		this.gui = gui;
		this.scale = scale;
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;

		gridIncr = (int) (scale / 10.0);

		int w = (int) ((xmax - xmin) * scale) + 2 * gridIncr;
		int h = (int) ((ymax - ymin) * scale) + 2 * gridIncr;

		if (w > width) {
			width = w;
		}

		if (h > height) {
			height = h;
		}

		setSize(width, height);

		double ymaxXscale = ymax * scale, xminXscale = xmin * scale;
		double rounded_ymaxXscale = signOf(ymax) * (Math.abs(ymaxXscale) + gridIncr - Math.IEEEremainder(Math.abs(ymaxXscale), gridIncr));
		double rounded_xminXscale = signOf(xmin) * (Math.abs(xminXscale) + gridIncr - Math.IEEEremainder(Math.abs(xminXscale), gridIncr));

		xaxis_yval = gridIncr + (int) (rounded_ymaxXscale);
		yaxis_xval = gridIncr + (int) (-rounded_xminXscale);
	}

	/** Returns the sign of the parameter. */
	double signOf(double val) {
		if (val < 0) {
			return -1;
		} else if (val == 0) {
			return 0;
		} else {
			return 1;
		}
	}

	public int getYAxisXPos() {
		return yaxis_xval;
	}

	public int getXAxisYPos() {
		return xaxis_yval;
	}

	public void setScale(int scale) {
		this.scale = scale;

		gridIncr = (int) (scale / 10.0);

		int w = (int) ((xmax - xmin) * scale) + 2 * gridIncr;
		int h = (int) ((ymax - ymin) * scale) + 2 * gridIncr;

		if (w > width) {
			width = w;
		}

		if (h > height) {
			height = h;
		}

		setSize(width, height);

		double ymaxXscale = ymax * scale, xminXscale = xmin * scale;
		double rounded_ymaxXscale = signOf(ymax) * (Math.abs(ymaxXscale) + gridIncr - Math.IEEEremainder(Math.abs(ymaxXscale), gridIncr));
		double rounded_xminXscale = signOf(xmin) * (Math.abs(xminXscale) + gridIncr - Math.IEEEremainder(Math.abs(xminXscale), gridIncr));

		xaxis_yval = gridIncr + (int) (rounded_ymaxXscale);
		yaxis_xval = gridIncr + (int) (-rounded_xminXscale);
		repaint();
	}

	public void resize(double xmin, double ymin, double xmax, double ymax, int scale) {
		this.scale = scale;
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;

		setScale(scale);
	}

	public void clear() {
		repaint();
	}

	// Method for drawing everything
	@Override
	public void paint(Graphics g) {
		Edge e;
		Node n;
		ArrayList nodeList = GeomBasics.getNodeList();
		ArrayList edgeList = GeomBasics.getEdgeList();
		int halfGridIncr = gridIncr / 2;

		g.clearRect(0, 0, getWidth(), getHeight());

		// Draw background grid
		if (gui.grid) {
			g.setColor(Color.gray);
			for (int i = 0; i < getWidth(); i += gridIncr) {
				g.drawLine(i, 0, i, getHeight());
			}

			for (int i = 0; i < getHeight(); i += gridIncr) {
				g.drawLine(0, i, getWidth(), i);
			}
		}

		// Draw axis
		if (gui.axis) {

			g.setColor(Color.white);
			g.drawLine(0, xaxis_yval, getWidth(), xaxis_yval);
			g.drawLine(yaxis_xval, 0, yaxis_xval, getHeight());

			for (int i = yaxis_xval; i < getWidth(); i += gridIncr * 5) {
				g.drawLine(i, xaxis_yval + halfGridIncr, i, xaxis_yval);
				g.drawString(Double.toString(((double) (i - yaxis_xval)) / scale), i, xaxis_yval + gridIncr);
			}
			for (int i = yaxis_xval - gridIncr * 5; i >= 0; i -= gridIncr * 5) {
				g.drawLine(i, xaxis_yval + halfGridIncr, i, xaxis_yval);
				g.drawString(Double.toString(((double) (i - yaxis_xval)) / scale), i, xaxis_yval + gridIncr);
			}

			for (int i = xaxis_yval + gridIncr * 5; i < getHeight(); i += gridIncr * 5) {
				g.drawLine(yaxis_xval - halfGridIncr, i, yaxis_xval, i);
				g.drawString(Double.toString(((double) (xaxis_yval - i)) / scale), yaxis_xval - halfGridIncr, i);
			}
			for (int i = xaxis_yval - gridIncr * 5; i >= 0; i -= gridIncr * 5) {
				g.drawLine(yaxis_xval - halfGridIncr, i, yaxis_xval, i);
				g.drawString(Double.toString(((double) (xaxis_yval - i)) / scale), yaxis_xval - halfGridIncr, i);
			}

		}

		// Draw mesh
		if (edgeList != null) {
			for (int i = 0; i < edgeList.size(); i++) {
				e = (Edge) edgeList.get(i);

				if (e.color == java.awt.Color.red) {
					g.setColor(e.color);
				} else if (e.leftNode.edgeList.indexOf(e) == -1 && e.rightNode.edgeList.indexOf(e) == -1) {
					g.setColor(Color.blue);
				} else if (e.frontEdge) {
					g.setColor(Color.yellow);
				} else {
					g.setColor(e.color);
				}

				g.drawLine((int) (e.leftNode.x * scale + yaxis_xval), (int) (-e.leftNode.y * scale + xaxis_yval), (int) (e.rightNode.x * scale + yaxis_xval),
						(int) (-e.rightNode.y * scale + xaxis_yval));
				g.setColor(Color.green);

			}
		}

		if (nodeList != null) {
			for (Object element : nodeList) {
				n = (Node) element;
				g.setColor(n.color);
				g.fillOval((int) (n.x * scale + yaxis_xval - 3), (int) (-n.y * scale + xaxis_yval - 3), 5, 5);
			}
		}

	}

}
