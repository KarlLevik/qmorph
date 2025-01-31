package com.github.karllevik.qmorph.geom;

import com.github.karllevik.qmorph.Constants;
import com.github.karllevik.qmorph.Node;

/**
 * This class declares methods and variables that are common to quads and
 * triangles.
 */

public abstract class Element extends Constants {
	
	/** An array of interior angles */
	public double[] ang;
	/** An array of edges */
	public Edge[] edgeList;
	/** Node used for determining inversion, amonst other things. */
	public Node firstNode;
	/**
	 * Doubles to hold the cur. distortion metric & the metric after perturbation
	 */
	public double distortionMetric, newDistortionMetric;
	/** Doubles to hold the gradient vector */
	public double gX, gY;
	
	/** @return neighbor element sharing edge e */
	public abstract Element neighbor(Edge e);

	/** @return local angle inside element at Node n */
	public abstract double angle(Edge e, Node n);

	/** Compute & set the angles at the nodes of the element. */
	public abstract void updateAngles();

	/**
	 * Compute & set the angle at this particular Node incident with this Element
	 * Edge
	 */
	public abstract void updateAngle(Node n);

	/** @return description string for element (list of node coordinates) */
	public abstract String descr();

	/** Output description string for element (list of node coordinates) */
	public abstract void printMe();

	/** Verify that the quad has the specified edge. */
	public abstract boolean hasEdge(Edge e);

	/** Verify that the quad has the specified node. */
	public abstract boolean hasNode(Node n);

	/** Verify that the area of the quad is greater than 0. */
	public abstract boolean areaLargerThan0();

	/** Return local neighboring edge at node n. */
	public abstract Edge neighborEdge(Node n, Edge e);

	/** Return the index to this edge in this element's edgeList */
	public abstract int indexOf(Edge e);

	/** Return the index to this angle in this element's ang array */
	public abstract int angleIndex(Edge e1, Edge e2);

	public abstract int angleIndex(Node n);

	/** Return the angle between this Element's Edges e1 and e2. */
	public abstract double angle(Edge e1, Edge e2);

	/** @return true if the element has become inverted */
	public abstract boolean inverted();

	/** @return true if the element has become inverted or its area is zero. */
	public abstract boolean invertedOrZeroArea();

	/** @return true if the element has a concavity at its Node n. */
	public abstract boolean concavityAt(Node n);

	/** Replace one of the specified edges e with a replacement edge. */
	public abstract void replaceEdge(Edge e, Edge replacement);

	/** Make one element pointer of each Edge in edgeList point to this Element. */
	public abstract void connectEdges();

	/**
	 * Point the element pointer of each Edge in edgeList that previously pointed to
	 * this Element to point to null.
	 */
	public abstract void disconnectEdges();

	/** Create a simple element for testing purposes only. */
	public abstract Element elementWithExchangedNodes(Node original, Node replacement);

	/**
	 * @return true if the quad becomes inverted when node n1 is relocated to pos.
	 *         n2. Else return false.
	 */
	public abstract boolean invertedWhenNodeRelocated(Node n1, Node n2);

	/**
	 * Update the distortion metric according to the article "An approach to
	 * Combined Laplacian and Optimization-Based Smoothing for Triangular,
	 * Quadrilateral and Quad-Dominant Meshes" by by Cannan, Tristano, and Staten
	 */
	public abstract void updateDistortionMetric();

	/** Return the length of the longest Edge. */
	public abstract double longestEdgeLength();

	/** Return the size of the largest angle. */
	public abstract double largestAngle();

	/** Return the node at the largest interior angle. */
	public abstract Node nodeAtLargestAngle();

	/** Set the color of the edges to red. */
	public abstract void markEdgesIllegal();

	/** Set the color of the edges to green. */
	public abstract void markEdgesLegal();

	/**
	 * A method for fast computation of the cross product of two vectors.
	 *
	 * @param o1 origin of first vector
	 * @param p1 endpoint of first vector
	 * @param o2 origin of second vector
	 * @param p2 endpoint of second vector
	 * @return the cross product of the two vectors
	 */
	protected double cross(Node o1, Node p1, Node o2, Node p2) {
		double x1 = p1.x - o1.x;
		double x2 = p2.x - o2.x;
		double y1 = p1.y - o1.y;
		double y2 = p2.y - o2.y;
		return x1 * y2 - x2 * y1;
	}
}
