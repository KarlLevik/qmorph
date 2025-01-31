package com.github.karllevik.qmorph.geom;

import java.awt.Color;
import java.util.ArrayList;

import com.github.karllevik.qmorph.Constants;
import com.github.karllevik.qmorph.Msg;
import com.github.karllevik.qmorph.Node;

/**
 * This class holds information for edges, and has methods for handling issues
 * involving edges.
 */

public class Edge extends Constants {

	public Node leftNode, rightNode; // This Edge has these two nodes
	public Element element1 = null, element2 = null; // Belongs to these Elements (Quads/Triangles)
	public Edge leftFrontNeighbor, rightFrontNeighbor;
	public int level;
	
	public static ArrayList[] stateList = new ArrayList[3];

	public boolean frontEdge = false;
	public boolean swappable = true;
	public boolean selectable = true;
	// Edge leftSide= null, rightSide= null; // Side edges when building a quad
	public boolean leftSide = false, rightSide = false; // Indicates if frontNeighbor is
													// to be used as side edge in quad
	public double len; // length of this edge
	public Color color = Color.green;

	public Edge(Node node1, Node node2) {
		if ((node1.x < node2.x) || (node1.x == node2.x && node1.y > node2.y)) {
			leftNode = node1;
			rightNode = node2;
		} else {
			leftNode = node2;
			rightNode = node1;
		}

		len = computeLength();
	}

	// Create a clone of Edge e with all the important fields
	private Edge(Edge e) {
		leftNode = e.leftNode;
		rightNode = e.rightNode;
		len = e.len;
		e.element1 = element1;
		e.element2 = element2;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Edge) {
			Edge e = (Edge) o;
			if (leftNode.equals(e.leftNode) && rightNode.equals(e.rightNode)) {
				return true;
			}
		}
		return false;
	}

	// Return a copy of the edge
	public Edge copy() {
		return new Edge(this);
	}

	public static void clearStateList() {
		stateList[0] = new ArrayList<>();
		stateList[1] = new ArrayList<>();
		stateList[2] = new ArrayList<>();
	}

	// Removes an Edge from the stateLists
	// Returns true if the Edge was successfully removed, else false.
	public boolean removeFromStateList() {
		int i;
		int state = getState();
		i = stateList[state].indexOf(this);
		if (i == -1) {
			return false;
		}
		stateList[state].remove(i);
		return true;
	}

	// Removes an Edge from the stateLists
	// Returns true if the Edge was successfully removed, else false.
	public boolean removeFromStateList(int state) {
		int i;
		i = stateList[state].indexOf(this);
		if (i == -1) {
			return false;
		}
		stateList[state].remove(i);
		return true;
	}

	public int getState() {
		int ret = 0;
		if (leftSide) {
			ret++;
		}
		if (rightSide) {
			ret++;
		}
		return ret;
	}

	public int getTrueState() {
		return getState();
	}

	public boolean alterLeftState(boolean newLeftState) {
		int state = getState();
		int i = stateList[state].indexOf(this);
		if (i == -1) {
			return false;
		}
		leftSide = newLeftState;
		int newState = getState();
		if (state != newState) {
			stateList[state].remove(i);
			stateList[newState].add(this);
		}
		return true;
	}

	public boolean alterRightState(boolean newRightState) {
		int state = getState();
		int i = stateList[state].indexOf(this);
		if (i == -1) {
			return false;
		}
		rightSide = newRightState;
		int newState = getState();
		if (state != newState) {
			stateList[state].remove(i);
			stateList[newState].add(this);
		}
		return true;
	}

	// Determine whether the frontNeighbor is an appropriate side Edge for a future
	// Quad with Edge e as base Edge. Return the frontNeighbor if so, else null.
	// elem== null if n.boundaryNode()== true
	public Edge evalPotSideEdge(Edge frontNeighbor, Node n) {
		Msg.debug("Entering Edge.evalPotSideEdge(..)");
		Element tri = getTriangleElement(), quad = getQuadElement();
		double ang;

		if (tri != null) {
			ang = sumAngle(tri, n, frontNeighbor);
		} else {
			ang = PIx2 - sumAngle(quad, n, frontNeighbor);
		}

		Msg.debug("sumAngle(..) between " + descr() + " and " + frontNeighbor.descr() + ": " + Math.toDegrees(ang));

		Msg.debug("Leaving Edge.evalPotSideEdge(..)");
		if (ang < PIx3div4) { // if (ang< PIdiv2+EPSILON) // Could this be better?
			return frontNeighbor;
		} else {
			return null;
		}
	}

	// Determine the state bit at both Nodes and set the left and right side Edges.
	// If a state bit is set, then the corresponding front neighbor Edge must get
	// Edge this as a side Edge at that Node. If it is not set, then it must get a
	// null
	// value instead.

	// this: the edge to be classified (gets a state value, and is added to a
	// statelist)
	public void classifyStateOfFrontEdge() {
		Msg.debug("Entering Edge.classifyStateOfFrontEdge()");
		Msg.debug("this: " + descr());
		final Edge lfn = leftFrontNeighbor, rfn = rightFrontNeighbor;

		Edge l, r;

		// Alter states and side Edges on left side:
		l = evalPotSideEdge(lfn, leftNode);
		if (l != null) {
			leftSide = true;
			if (leftNode == lfn.leftNode) {
				lfn.alterLeftState(true);
			} else {
				lfn.alterRightState(true);
			}
		} else {
			leftSide = false;
			if (leftNode == lfn.leftNode) {
				lfn.alterLeftState(false);
			} else {
				lfn.alterRightState(false);
			}
		}

		// Alter states and side Edges on right side:
		r = evalPotSideEdge(rfn, rightNode);
		if (r != null) {
			rightSide = true;
			if (rightNode == rfn.leftNode) {
				rfn.alterLeftState(true);
			} else {
				rfn.alterRightState(true);
			}
		} else {
			rightSide = false;
			if (rightNode == rfn.leftNode) {
				rfn.alterLeftState(false);
			} else {
				rfn.alterRightState(false);
			}
		}

		// Add this to a stateList:
		stateList[getState()].add(this);
		Msg.debug("Leaving Edge.classifyStateOfFrontEdge()");
	}

	public boolean isLargeTransition(Edge e) {
		double ratio;
		double e1Len = length();
		double e2Len = e.length();

		if (e1Len > e2Len) {
			ratio = e1Len / e2Len;
		} else {
			ratio = e2Len / e1Len;
		}

		if (ratio > 2.5) {
			return true;
		} else {
			return false;
		}
	}

	// Select the next front to be processed. The selection criteria is:
	// Primary: the edge state
	// Secondary: the edge level
	// If the candidate edge is part of a large transition on the front where
	// longest - shortest length ratio > 2.5, and the candidate edge is not in
	// state 1-1, then the shorter edge is selected.
	public static Edge getNextFront(/* ArrayList frontList, */) {
		Edge current, selected = null;
		int selState, curState = 2, i;

		// Select a front preferrably in stateList[2]

		while (curState >= 0 && selected == null) {
			for (i = 0; i < stateList[curState].size(); i++) {
				current = (Edge) stateList[curState].get(i);
				if (current.selectable) {
					selected = current;
					break;
				}
			}
			curState--;
		}
		if (selected == null) {
			Msg.warning("getNextFront(): no selectable fronts found in stateLists.");
			return null;
		}

		selState = selected.getState();

		for (i = 0; i < stateList[selState].size(); i++) {
			current = (Edge) stateList[selState].get(i);

			if (current.selectable && (current.level < selected.level || (current.level == selected.level && current.length() < selected.length()))) {
				selected = current;
			}
		}

		if (selState != 2) {
			if (selected.isLargeTransition(selected.leftFrontNeighbor)) {
				if (selected.length() > selected.leftFrontNeighbor.length() && selected.leftFrontNeighbor.selectable) {
					return selected.leftFrontNeighbor;
				}
			}

			if (selected.isLargeTransition(selected.rightFrontNeighbor)) {
				if (selected.length() > selected.rightFrontNeighbor.length() && selected.rightFrontNeighbor.selectable) {
					return selected.rightFrontNeighbor;
				}
			}
		}
		return selected;
	}

	public static void markAllSelectable() {
		Edge e;
		for (int i = 0; i < 3; i++) {
			for (Object element : stateList[i]) {
				e = (Edge) element;
				e.selectable = true;
			}
		}
	}

	public static void printStateLists() {
		if (Msg.debugMode) {
			System.out.println("frontsInState 1-1:");
			for (Object element : stateList[2]) {
				Edge edge = (Edge) element;
				System.out.println("" + edge.descr() + ", (" + edge.getState() + ")");
			}
			System.out.println("frontsInState 0-1 and 1-0:");
			for (Object element : stateList[1]) {
				Edge edge = (Edge) element;
				System.out.println("" + edge.descr() + ", (" + edge.getState() + ")");
			}
			System.out.println("frontsInState 0-0:");
			for (Object element : stateList[0]) {
				Edge edge = (Edge) element;
				System.out.println("" + edge.descr() + ", (" + edge.getState() + ")");
			}
		}
	}

	// If e.leftNode is leftmore than this.leftNode, return true, else false
	public boolean leftTo(Edge e) {
		if ((leftNode.x < e.leftNode.x) || (leftNode.x == e.leftNode.x && leftNode.y < e.leftNode.y)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isFrontEdge() {
		if ((element1 instanceof Triangle && !(element2 instanceof Triangle)) || (element2 instanceof Triangle && !(element1 instanceof Triangle))) {
			return true;
		} else {
			return false;
		}

		/*
		 * if ((element1 instanceof Quad && element2 instanceof Quad) || (element1
		 * instanceof Triangle && element2 instanceof Triangle)) return false; else
		 * return true;
		 */
	}

	public String descr() {
		return "(" + leftNode.x + ", " + leftNode.y + "), (" + rightNode.x + ", " + rightNode.y + ")";
	}

	public void printMe() {
		System.out.println(descr());
	}

	public double length() {
		return len;
	}

	// Replace this edge's node n1 with the node n2:
	public boolean replaceNode(Node n1, Node n2) {
		if (leftNode.equals(n1)) {
			leftNode = n2;
		} else if (rightNode.equals(n1)) {
			rightNode = n2;
		} else {
			return false;
		}
		len = computeLength();
		return true;
	}

	// Seam two edges together. The method assumes that they already have one common
	// node. For each edge in the edgeList of the otherNode (otherE) of edge e:
	// Provided that otherThis or the otherNode of otherE is not found in any edge
	// in
	// the edgeList of the otherNode of this edge (otherThis), then the edge is
	// added
	// to this' edgeList.

	// Assumes that the quad area defined by the three distinct nodes of the two
	// edges,
	// and the node in the top of the triangle adjacent the otherNodes (of the
	// common
	// node of the two edges), is empty.

	public void seamWith(Edge e) {
		Node nK = commonNode(e);
		Node nKp1 = otherNode(nK), nKm1 = e.otherNode(nK), other;
		boolean found = false;
		Edge eI, eJ;

		for (int i = 0; i < nKm1.edgeList.size(); i++) {
			eI = (Edge) nKm1.edgeList.get(i);
			other = eI.otherNode(nKm1);

			if (other != nKp1) {
				for (int j = 0; j < nKp1.edgeList.size(); j++) {
					eJ = (Edge) nKp1.edgeList.get(j);

					if (other == eJ.otherNode(nKp1)) {
						found = true;

						other.edgeList.remove(other.edgeList.indexOf(eI));

						if (eI.element1.firstNode == nKm1) { // Don't forget firstNode!!
							eI.element1.firstNode = nKp1;
						}
						eI.element1.replaceEdge(eI, eJ);
						eJ.connectToElement(eI.element1);
						break;
					}
				}
				if (!found) {
					if (eI.element1.firstNode == nKm1) { // Don't forget firstNode!!
						eI.element1.firstNode = nKp1;
					}
					if (eI.element2.firstNode == nKm1) { // Don't forget firstNode!!
						eI.element2.firstNode = nKp1;
					}

					eI.replaceNode(nKm1, nKp1);
					nKp1.edgeList.add(eI);
				} else {
					found = false;
				}
			} else {
				// Remove the edge between eKp1 and eKm1 (from the edgeList of eKp1)
				nKp1.edgeList.remove(nKp1.edgeList.indexOf(eI));
			}
		}
	}

	// Return the midpoint (represented by a new Node) of this edge:
	public Node midPoint() {
		double xDiff = rightNode.x - leftNode.x;
		double yDiff = rightNode.y - leftNode.y;

		return new Node(leftNode.x + xDiff * 0.5, leftNode.y + yDiff * 0.5);
	}

	public double computeLength() {
		double xdiff = rightNode.x - leftNode.x;
		double ydiff = rightNode.y - leftNode.y;
		return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
	}

	public double length(double x1, double y1, double x2, double y2) {
		double xdiff = x2 - x1;
		double ydiff = y2 - y1;
		return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
	}

	public double length(Node node1, Node node2) {
		double xdiff = node2.x - node1.x;
		double ydiff = node2.y - node1.y;
		return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
	}

	// Returns angle relative to the x-axis (which is directed from the origin (0,0)
	// to
	// the right, btw) at node n (which is leftNode or rightNode).
	// Range: <-180, 180>
	public double angleAt(Node n) {
		// Math.acos returns values in the range 0.0 through pi, avoiding neg numbers.
		// If this is CW to x-axis, then return pos acos, else return neg acos
		// double x= leftNode.x-rightNode.x;
		// double y= leftNode.y-rightNode.y;
		Node other = otherNode(n);

		double x = n.x - other.x;
		double y = n.y - other.y;

		if (x == 0 && y > 0) {
			return -PIdiv2;
		} else if (x == 0 && y < 0) {
			return PIdiv2;
		} else {
			double hyp = Math.sqrt(x * x + y * y);

			if (x > 0) {
				if (y > 0) {
					return Math.PI + Math.acos(x / hyp);
				} else {
					return Math.PI - Math.acos(x / hyp);
				}
			} else {
				if (y > 0) {
					return Math.PI + Math.PI - Math.acos(-x / hyp);
				} else {
					return Math.acos(-x / hyp);
				}
			}

			// double cLen= Math.sqrt(x*x + y*y);
			/*
			 * double aLen= Math.sqrt(x*x + y*y); if (y> 0) return -Math.acos((aLen*aLen +
			 * x*x -y*y)/(2*aLen*Math.abs(x))); else return Math.acos((aLen*aLen + x*x
			 * -y*y)/(2*aLen*Math.abs(x)));
			 */
		}
	}

	// Returns the angle from this Edge to eEdge by summing the angles of the
	// Elements
	// adjacent Node n.
	public double sumAngle(Element sElem, Node n, Edge eEdge) {
		Msg.debug("Entering sumAngle(..)");
		Msg.debug("this: " + descr());
		if (sElem != null) {
			Msg.debug("sElem: " + sElem.descr());
		}
		if (n != null) {
			Msg.debug("n: " + n.descr());
		}
		if (eEdge != null) {
			Msg.debug("eEdge: " + eEdge.descr());
		}

		Element curElem = sElem;
		Edge curEdge = this;
		double ang = 0, iang = 0;
		double d;

		while (curEdge != eEdge && curElem != null) {
			d = curElem.angle(curEdge, n);
			// Msg.debug("curEdge= "+curEdge.descr());
			// Msg.debug("curElem.angle(..) returns "+Math.toDegrees(d));
			ang += d;
			curEdge = curElem.neighborEdge(n, curEdge);
			curElem = curElem.neighbor(curEdge);
		}

		// If n is a boundaryNode, the situation gets more complicated:
		if (curEdge != eEdge) {
			// Sum the "internal" angles:
			curEdge = this;
			curElem = sElem.neighbor(curEdge);

			while (curEdge != eEdge && curElem != null) {
				d = curElem.angle(curEdge, n);
				// Msg.debug("curEdge= "+curEdge.descr());
				// Msg.debug("curElem.angle(..) returns "+Math.toDegrees(d));
				iang += d;
				curEdge = curElem.neighborEdge(n, curEdge);
				curElem = curElem.neighbor(curEdge);
			}
			ang = PIx2 - iang;
		}
		Msg.debug("Leaving sumAngle(..), returning " + Math.toDegrees(ang));
		return ang;
	}

	// Return the first front Edge adjacent Node n starting check at
	// this Edge in Element sElem.
	public Edge firstFrontEdgeAt(Element sElem, Node n) {
		Element curElem = sElem;
		Edge curEdge = this;

		while (!curEdge.frontEdge) {
			curEdge = curElem.neighborEdge(n, curEdge);
			curElem = curElem.neighbor(curEdge);
		}
		return curEdge;
	}

	// Compute the internal angle between this Edge and Edge edge at Node n.
	// Returns a positive value.
	public double computePosAngle(Edge edge, Node n) {
		double a, b, c;
		if (edge == this) {
			Msg.warning("Edge.computePosAngle(..): The parameter Edge is the same as this Edge.");
			return 2 * Math.PI;
		}

		if (leftNode.equals(n)) {
			if (n.equals(edge.leftNode)) {
				c = length(rightNode, edge.rightNode);
			} else if (n.equals(edge.rightNode)) {
				c = length(rightNode, edge.leftNode);
			} else {
				Msg.error("Edge::computePosAngle(..): These edges are not connected.");
				return 0;
			}
		} else if (rightNode.equals(n)) {
			if (n.equals(edge.leftNode)) {
				c = length(leftNode, edge.rightNode);
			} else if (n.equals(edge.rightNode)) {
				c = length(leftNode, edge.leftNode);
			} else {
				Msg.error("Edge::computePosAngle(..): These edges are not connected.");
				return 0;
			}
		} else {
			Msg.error("Edge::computePosAngle(..): These edges are not connected.");
			return 0;
		}
		a = computeLength(); // len; // try this later...!
		b = edge.computeLength(); // edge.len; // try this later...!

		// Math.acos returns a value in the range [0, PI],
		// and input *MUST BE STRICTLY* in the range [-1, 1] !!!!!!!!
		// ^^^^^^^^
		double itemp = (a * a + b * b - c * c) / (2 * a * b);
		if (itemp > 1.0) {
			return 0;
		} else if (itemp < -1.0) {
			return Math.PI;
		} else {
			return Math.acos(itemp);
		}
	}

	// Compute the ccw directed angle between this Edge and Edge edge at Node n.
	// Returns a positive value in range [0, 2*PI>.
	public double computeCCWAngle(Edge edge) {
		Node n = commonNode(edge);
		double temp = computePosAngle(edge, n);

		MyVector thisVector = getVector(n);
		MyVector edgeVector = edge.getVector(n);

		if (thisVector.isCWto(edgeVector)) {
			return temp;
		} else {
			return PIx2 - temp;
		}
	}

	// Return a common node for edges this and e
	public Node commonNode(Edge e) {
		if (hasNode(e.leftNode)) {
			return e.leftNode;
		} else if (hasNode(e.rightNode)) {
			return e.rightNode;
		} else {
			return null;
		}
	}

	// Return a common element for edges this and e
	public Element commonElement(Edge e) {
		if (hasElement(e.element1)) {
			return e.element1;
		} else if (e.element2 != null && hasElement(e.element2)) {
			return e.element2;
		} else {
			return null;
		}
	}

	// Add this Edge to the nodes' edgeLists. Careful, there's no safety checks!
	public void connectNodes() {
		leftNode.edgeList.add(this);
		rightNode.edgeList.add(this);
	}

	// Remove this Edge from the nodes' edgeLists. Careful, there's no safety
	// checks!
	public void disconnectNodes() {
		leftNode.edgeList.remove(leftNode.edgeList.indexOf(this));
		rightNode.edgeList.remove(rightNode.edgeList.indexOf(this));
	}

	// Remove this Edge from the nodes' edgeLists. Safety checks...
	public void tryToDisconnectNodes() {
		int i;
		i = leftNode.edgeList.indexOf(this);
		if (i != -1) {
			leftNode.edgeList.remove(i);
		}
		i = rightNode.edgeList.indexOf(this);
		if (i != -1) {
			rightNode.edgeList.remove(i);
		}
	}

	public void connectToTriangle(Triangle triangle) {
		if (hasElement(triangle)) {
			return;
		}
		if (element1 == null) {
			element1 = triangle;
		} else if (element2 == null) {
			element2 = triangle;
		} else {
			Msg.error("Edge.connectToTriangle(..): An edge cannot be connected to more than two elements. edge= " + descr());
		}
	}

	public void connectToQuad(Quad q) {
		if (hasElement(q)) {
			return;
		}
		if (element1 == null) {
			element1 = q;
		} else if (element2 == null) {
			element2 = q;
		} else {
			Msg.error("Edge.connectToQuad(..):An edge cannot be connected to more than two elements.");
		}
	}

	public void connectToElement(Element elem) {
		if (hasElement(elem)) {
			return;
		}
		if (element1 == null) {
			element1 = elem;
		} else if (element2 == null) {
			element2 = elem;
		} else {
			Msg.error("Edge.connectToElement(..):An edge cannot be connected to more than two elements.");
		}
	}

	// element1 should never be null:
	public void disconnectFromElement(Element elem) {
		if (element1 == elem) {
			element1 = element2;
			element2 = null;
		} else if (element2 == elem) {
			element2 = null;
		} else {
			Msg.error("Edge " + descr() + " is not connected to element " + elem.descr() + ".");
		}
	}

	/**
	 * @param wrongNode a node that we don't want returned
	 * @return a node opposite to this edge in an adjacent triangle
	 */
	public Node oppositeNode(Node wrongNode) {
		Node candidate;
		Edge otherEdge;

		// Pick one of the other edges in element1
		int ind = element1.indexOf(this);
		if (ind == 0 || ind == 1) {
			otherEdge = element1.edgeList[2];
		} else {
			otherEdge = element1.edgeList[1];
		}

		// This edge contains an opposite node... get this node....
		if (otherEdge.leftNode != leftNode && otherEdge.leftNode != rightNode) {
			candidate = otherEdge.leftNode;
		} else {
			candidate = otherEdge.rightNode;
		}

		// Damn, it's the wrong node! Then we must go look in element2.
		if (candidate == wrongNode) {

			// Pick one of the other edges in element2
			ind = element2.indexOf(this);
			if (ind == 0 || ind == 1) {
				otherEdge = element2.edgeList[2];
			} else if (ind == 2) {
				otherEdge = element2.edgeList[1];
			}

			// This edge contains an opposite node
			// get this node....
			if (otherEdge.leftNode != leftNode && otherEdge.leftNode != rightNode) {
				candidate = otherEdge.leftNode;
			} else if (otherEdge.rightNode != leftNode && otherEdge.rightNode != rightNode) {
				candidate = otherEdge.rightNode;
			}
		}
		return candidate;
	}

	// Construct an Edge that is a unit normal to this Edge.
	// (Remember to add the new Node to nodeList if you want to keep it.)
	// nB: one of the nodes on this edge (leftNode or rightNode)

	/*
	 * C b ____----x ___---- | b=1 ___---- | x----------------------x A c B
	 * 
	 * angle(B)= PI/2
	 * 
	 */
	// xdiff= xB - xA
	// ydiff= yB - yA
	// a= 1, c= sqrt(xdiff^2 + ydiff^2)
	// xC = xA + b* cos(alpha +ang.A)
	// = xA + xdiff - ydiff*a/c
	// = xB - ydiff*a/c
	// = xB - ydiff/c
	//
	// yC = yA + b* sin(alpha + ang.A)
	// = yA + ydiff + xdiff*a/c
	// = yB + xdiff*a/c
	// = yB + xdiff/c
	//
	public Edge unitNormalAt(Node n) {
		Msg.debug("Entering Edge.unitNormalAt(..)");

		double xdiff = rightNode.x - leftNode.x;
		double ydiff = rightNode.y - leftNode.y;

		Msg.debug("this: " + descr() + ", n: " + n.descr());

		double c = Math.sqrt(xdiff * xdiff + ydiff * ydiff);

		double xn = n.x - ydiff / c;
		double yn = n.y + xdiff / c;

		Node newNode = new Node(xn, yn);

		Msg.debug("Leaving Edge.unitNormalAt(..)");
		return new Edge(n, newNode);
	}

	public Edge getSwappedEdge() {
		if (element2 == null) {
			Msg.warning("getSwappedEdge: Cannot swap a boundary edge.");
			return null;
		}
		if (element1 instanceof Quad || element2 instanceof Quad) {
			Msg.warning("getSwappedEdge: Edge must lie between two triangles.");
			return null;
		}

		Node n = this.oppositeNode(null);
		Node m = this.oppositeNode(n);
		Edge swappedEdge = new Edge(n, m);

		return swappedEdge;
	}

	/**
	 * Swap diagonal between the edge's two triangles and update locally (To be used
	 * with getSwappedEdge())
	 */
	public void swapToAndSetElementsFor(Edge e) {
		Msg.debug("Entering Edge.swapToAndSetElementsFor(..)");
		if (element1 == null || element2 == null) {
			Msg.error("Edge.swapToAndSetElementsFor(..): both elements not set");
		}

		Msg.debug("element1: " + element1.descr());
		Msg.debug("element2: " + element2.descr());

		Msg.debug("...this: " + descr());
		// extract the outer edges
		ArrayList edges = new ArrayList();

		Edge e1 = element1.neighborEdge(leftNode, this);
		Edge e2 = element1.neighborEdge(e1.otherNode(leftNode), e1);
		Edge e3 = element2.neighborEdge(rightNode, this);
		Edge e4 = element2.neighborEdge(e3.otherNode(rightNode), e3);

		element2.disconnectEdges(); // important: element2 *first*, then element1
		element1.disconnectEdges();

		Triangle t1 = new Triangle(e, e2, e3);
		Triangle t2 = new Triangle(e, e4, e1);

		t1.connectEdges();
		t2.connectEdges();

		// Update edgeLists at this.leftNode and this.rightNode
		// and at e.leftNode and e.rightNode:
		disconnectNodes();
		e.connectNodes();

		Msg.debug("Leaving Edge.swapToAndSetElementsFor(..)");
	}

	public MyVector getVector() {
		return new MyVector(leftNode, rightNode);
	}

	public MyVector getVector(Node origin) {
		if (origin.equals(leftNode)) {
			return new MyVector(leftNode, rightNode);
		} else if (origin.equals(rightNode)) {
			return new MyVector(rightNode, leftNode);
		} else {
			Msg.error("Edge::getVector(Node): Node not an endpoint in this edge.");
			return null;
		}
	}

	public boolean bordersToTriangle() {
		if (element1 instanceof Triangle) {
			return true;
		} else if (element2 != null && element2 instanceof Triangle) {
			return true;
		} else {
			return false;
		}
	}

	public boolean boundaryEdge() {
		if (element1 == null || element2 == null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean boundaryOrTriangleEdge() {
		if (element1 == null || element2 == null || element1 instanceof Triangle || element2 instanceof Triangle) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hasNode(Node n) {
		if (leftNode == n || rightNode == n) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hasElement(Element elem) {
		if (element1 == elem || element2 == elem) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hasFalseFrontNeighbor() {
		if (leftFrontNeighbor == null || !leftFrontNeighbor.frontEdge || rightFrontNeighbor == null || !rightFrontNeighbor.frontEdge) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hasFrontNeighbor(Edge e) {
		if (leftFrontNeighbor == e || rightFrontNeighbor == e) {
			return true;
		} else {
			return false;
		}
	}

	public Node otherNode(Node n) {
		if (n.equals(leftNode)) {
			return rightNode;
		} else if (n.equals(rightNode)) {
			return leftNode;
		} else {
			Msg.debug("this: " + descr());
			Msg.debug("n: " + n.descr());
			Msg.error("Edge.otherNode(Node): n is not on this edge");
			return null;
		}
	}

	/**
	 * Extend this edge at a given node and to a given lenth.
	 *
	 * @param length the new length of this edge
	 * @param nJ     the node from which the edge is extended
	 * @return the other node on the new edge
	 */
	public Node otherNodeGivenNewLength(double length, Node nJ) {
		// First find the angle between the existing edge and the x-axis:
		// Use a triangle containing one 90 degrees angle:
		MyVector v = new MyVector(nJ, this.otherNode(nJ));
		v.setLengthAndAngle(length, v.angle());

		// Use this to create the new node:
		return new Node(v.origin.x + v.x, v.origin.y + v.y);
	}

	// Prefers the left node if they are at equal y positions
	public Node upperNode() {
		if (leftNode.y >= rightNode.y) {
			return leftNode;
		} else {
			return rightNode;
		}
	}

	// Prefers the right node if they are at equal y positions
	public Node lowerNode() {
		if (rightNode.y <= leftNode.y) {
			return rightNode;
		} else {
			return leftNode;
		}
	}

	/**
	 * Return true if the 1-orbit around this.commonNode(e) through quad startQ from
	 * edge this to edge e doesn't contain any triangle elements. If node n lies on
	 * the boundary, and the orbit contains a hole, the orbit simply skips the hole
	 * and continues on the other side.
	 */
	public boolean noTrianglesInOrbit(Edge e, Quad startQ) {
		Msg.debug("Entering Edge.noTrianglesInOrbit(..)");
		Edge curEdge = this;
		Element curElem = startQ;
		Node n = commonNode(e);
		if (n == null) {
			Msg.debug("Leaving Edge.noTrianglesInOrbit(..), returns false");
			return false;
		}
		if (curEdge.boundaryEdge()) {
			curEdge = n.anotherBoundaryEdge(curEdge);
			curElem = curEdge.element1;
		}
		do {
			if (curElem instanceof Triangle) {
				Msg.debug("Leaving Edge.noTrianglesInOrbit(..), returns false");
				return false;
			}
			curEdge = curElem.neighborEdge(n, curEdge);
			if (curEdge.boundaryEdge()) {
				curEdge = n.anotherBoundaryEdge(curEdge);
				curElem = curEdge.element1;
			} else {
				curElem = curElem.neighbor(curEdge);
			}
		} while (curEdge != e);

		Msg.debug("Leaving Edge.noTrianglesInOrbit(..), returns true");
		return true;
	}

	public Edge findLeftFrontNeighbor(ArrayList frontList) {
		ArrayList list = new ArrayList();
		Edge leftEdge;
		Edge candidate = null;
		double candAng = Double.POSITIVE_INFINITY, curAng;
		Triangle t;

		for (int j = 0; j < leftNode.edgeList.size(); j++) {
			leftEdge = (Edge) leftNode.edgeList.get(j);
			if (leftEdge != this && leftEdge.isFrontEdge() /* leftEdge.frontEdge */) {
				list.add(leftEdge);
			}
		}
		if (list.size() == 1) {
			return (Edge) list.get(0);
		} else if (list.size() > 0) {

			// Choose the front edge with the smallest angle
			t = getTriangleElement();

			for (Object element : list) {
				leftEdge = (Edge) element;
				curAng = sumAngle(t, leftNode, leftEdge);
				if (curAng < candAng) {
					candAng = curAng;
					candidate = leftEdge;
				}

				// if (noTrianglesInOrbit(leftEdge, q))
				// return leftEdge;
			}
			return candidate;
		}
		Msg.warning("findLeftFrontNeighbor(..): Returning null");
		return null;
	}

	public Edge findRightFrontNeighbor(ArrayList frontList) {
		ArrayList list = new ArrayList();
		Edge rightEdge;
		Edge candidate = null;
		double candAng = Double.POSITIVE_INFINITY, curAng;
		Triangle t;

		for (int j = 0; j < rightNode.edgeList.size(); j++) {
			rightEdge = (Edge) rightNode.edgeList.get(j);
			if (rightEdge != this && rightEdge.isFrontEdge()/* frontList.contains(rightEdge) */) {
				list.add(rightEdge);
			}
		}
		if (list.size() == 1) {
			return (Edge) list.get(0);
		} else if (list.size() > 0) {
			t = getTriangleElement();
			for (Object element : list) {
				rightEdge = (Edge) element;
				curAng = sumAngle(t, rightNode, rightEdge);

				Msg.debug("findRightFrontNeighbor(): Angle between edge this: " + descr() + " and edge " + rightEdge.descr() + ": " + curAng);
				if (curAng < candAng) {
					candAng = curAng;
					candidate = rightEdge;
				}

				// if (noTrianglesInOrbit(rightEdge, q))
				// return rightEdge;
			}
			Msg.debug("findRightFrontNeighbor(): Returning candidate " + candidate.descr());
			return candidate;
		}
		Msg.warning("findRightFrontNeighbor(..): List.size== " + list.size() + ". Returning null");
		return null;
	}

	/** Set the appropriate front neighbor to edge e. */
	public void setFrontNeighbor(Edge e) {
		if (e.hasNode(leftNode)) {
			leftFrontNeighbor = e;
		} else if (e.hasNode(rightNode)) {
			rightFrontNeighbor = e;
		} else {
			Msg.warning("Edge.setFrontNeighbor(..): Could not set.");
		}
	}

	/** Returns true if the frontEdgeNeighbors are changed. */
	public boolean setFrontNeighbors(ArrayList frontList) {
		Edge lFront = findLeftFrontNeighbor(frontList);
		Edge rFront = findRightFrontNeighbor(frontList);
		boolean res = false;
		if (lFront != leftFrontNeighbor || rFront != rightFrontNeighbor) {
			res = true;
		}
		leftFrontNeighbor = lFront;
		rightFrontNeighbor = rFront;

		if (lFront != null && !lFront.hasFrontNeighbor(this)) {
			// res= true;
			lFront.setFrontNeighbor(this);
		}
		if (rFront != null && !rFront.hasFrontNeighbor(this)) {
			// res= true;
			rFront.setFrontNeighbor(this);
		}
		return res;
	}

	public void promoteToFront(int level, ArrayList frontList) {
		if (!frontEdge) {
			frontList.add(this);
			this.level = level;
			frontEdge = true;
		}
	}

	public boolean removeFromFront(ArrayList frontList) {
		int i = frontList.indexOf(this);
		frontEdge = false;
		if (i != -1) {
			frontList.remove(i);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Halve this Edge by introducing a new Node at the midpoint, and create two
	 * Edges from this midpoint to the each of the two opposite Nodes of Edge this:
	 * one in element1 and one in element2. Also create two new Edges from Node mid
	 * to the two Nodes of Edge this. Create four new Triangles. Update everything
	 * (also remove this Edge from edgeList and disconnect the nodes).
	 *
	 * @return the new Edge incident with Node ben.
	 */
	public Edge splitTrianglesAt(Node nN, Node ben, ArrayList triangleList, ArrayList edgeList, ArrayList nodeList) {
		Msg.debug("Entering Edge.splitTrianglesAt(..)");
		Edge eK1 = new Edge(leftNode, nN);
		Edge eK2 = new Edge(rightNode, nN);

		Triangle tri1 = (Triangle) element1;
		Triangle tri2 = (Triangle) element2;

		Node n1 = tri1.oppositeOfEdge(this);
		Node n2 = tri2.oppositeOfEdge(this);
		Edge diagonal1 = new Edge(nN, n1);
		Edge diagonal2 = new Edge(nN, n2);

		Edge e12 = tri1.neighborEdge(leftNode, this);
		Edge e13 = tri1.neighborEdge(rightNode, this);
		Edge e22 = tri2.neighborEdge(leftNode, this);
		Edge e23 = tri2.neighborEdge(rightNode, this);

		Triangle t11 = new Triangle(diagonal1, e12, eK1);
		Triangle t12 = new Triangle(diagonal1, e13, eK2);
		Triangle t21 = new Triangle(diagonal2, e22, eK1);
		Triangle t22 = new Triangle(diagonal2, e23, eK2);

		// Update the nodes' edgeLists
		disconnectNodes();
		eK1.connectNodes();
		eK2.connectNodes();
		diagonal1.connectNodes();
		diagonal2.connectNodes();

		// Disconnect old Triangles
		tri1.disconnectEdges();
		tri2.disconnectEdges();

		// Connect Edges to new Triangles
		t11.connectEdges();
		t12.connectEdges();
		t21.connectEdges();
		t22.connectEdges();

		// Update "global" lists
		edgeList.remove(edgeList.indexOf(this));
		edgeList.add(eK1);
		edgeList.add(eK2);
		edgeList.add(diagonal1);
		edgeList.add(diagonal2);

		triangleList.remove(triangleList.indexOf(tri1));
		triangleList.remove(triangleList.indexOf(tri2));
		triangleList.add(t11);
		triangleList.add(t12);
		triangleList.add(t21);
		triangleList.add(t22);

		Msg.debug("...Created triangle " + t11.descr());
		Msg.debug("...Created triangle " + t12.descr());
		Msg.debug("...Created triangle " + t21.descr());
		Msg.debug("...Created triangle " + t22.descr());

		Msg.debug("Leaving Edge.splitTrianglesAt(..)");
		if (eK1.hasNode(ben)) {
			return eK1;
		} else if (eK2.hasNode(ben)) {
			return eK2;
		} else {
			Msg.error("");
			return null;
		}
	}

	/**
	 * Make new triangles by introducing new Edges at this' midpoint.
	 *
	 * @return the "lower" (the one incident with the baseEdge) of the two edges
	 *         created from splitting this edge.
	 */
	public Edge splitTrianglesAtMyMidPoint(ArrayList triangleList, ArrayList edgeList, ArrayList nodeList, Edge baseEdge) {
		Msg.debug("Entering Edge.splitTrianglesAtMyMidPoint(..).");

		Edge lowerEdge;
		Node ben = baseEdge.commonNode(this);
		Node mid = this.midPoint();
		nodeList.add(mid);
		mid.color = java.awt.Color.blue;

		Msg.debug("Splitting edge " + descr());
		Msg.debug("Creating new Node: " + mid.descr());

		lowerEdge = splitTrianglesAt(mid, ben, triangleList, edgeList, nodeList);

		Msg.debug("Leaving Edge.splitTrianglesAtMyMidPoint(..).");
		return lowerEdge;
	}

	/**
	 * Find the next edge adjacent a quad element, starting at this edge which is
	 * part of a given element and which is adjacent a given node. Note that the
	 * method stops if the boundary is encountered.
	 *
	 * @param n         the node
	 * @param startElem
	 * @return the first edge of a quad found when parsing around node n, starting
	 *         at edge e in element startElem and moving in the direction from e to
	 *         e's neighbor edge at n in startElem. If startElem happens to be a
	 *         quad, the method won't consider that particular quad. If
	 *         unsuccessful, the method returns null.
	 */
	public Edge nextQuadEdgeAt(Node n, Element startElem) {
		Msg.debug("Entering Edge.nextQuadEdgeAt(..)");
		Element elem;
		Edge e;
		int i = 3;

		e = startElem.neighborEdge(n, this);
		elem = startElem.neighbor(e);

		while (elem != null && !(elem instanceof Quad) && elem != startElem) {
			e = elem.neighborEdge(n, e);
			Msg.debug("..." + i);
			i++;
			elem = elem.neighbor(e);
		}
		Msg.debug("Leaving Edge.nextQuadEdgeAt(..)");
		if (elem != null && elem instanceof Quad && elem != startElem) {
			return e;
		} else {
			return null;
		}
	}

	// Returns a neighboring element that is a quad. When applied to inner front
	// edges,
	// there are, of course, only one possible quad to return.
	public Quad getQuadElement() {
		if (element1 instanceof Quad) {
			return (Quad) element1;
		} else if (element2 instanceof Quad) {
			return (Quad) element2;
		} else {
			return null;
		}
	}

	// Returns a neighboring element that is a triangle. The method should work as
	// long
	// as it is applied to a front edge.
	public Triangle getTriangleElement() {
		if (element1 instanceof Triangle) {
			return (Triangle) element1;
		} else if (element2 instanceof Triangle) {
			return (Triangle) element2;
		} else {
			return null;
		}
	}

	/** Return the neighboring quad that is also a neighbor of edge e. */
	public Quad getQuadWithEdge(Edge e) {
		if (element1 instanceof Quad && element1.hasEdge(e)) {
			return (Quad) element1;
		} else if (element2 instanceof Quad && element2.hasEdge(e)) {
			return (Quad) element2;
		} else {
			return null;
		}
	}

	/** Return the front neighbor edge at node n. */
	public Edge frontNeighborAt(Node n) {
		if (leftFrontNeighbor != null && commonNode(leftFrontNeighbor) == n) {
			return leftFrontNeighbor;
		} else if (rightFrontNeighbor != null && commonNode(rightFrontNeighbor) == n) {
			return rightFrontNeighbor;
		} else {
			return null;
		}
	}

	/** @return the front neighbor next to this (not the prev edge). */
	public Edge nextFrontNeighbor(Edge prev) {
		if (leftFrontNeighbor != prev) {
			return leftFrontNeighbor;
		} else if (rightFrontNeighbor != prev) {
			return rightFrontNeighbor;
		} else {
			Msg.error("Edge.nextFrontNeighbor(Edge): Cannot find a suitable next edge.");
			return null;
		}
	}

	// Return a neighbor edge at node n, that is front edge according to the
	// definition,
	// and that is part of the same loop as this edge.
	// Assumes that this is a true front edge.
	public Edge trueFrontNeighborAt(Node n) {
		Element curElem = getTriangleElement();
		Edge curEdge = this;

		if (!hasNode(n)) {
			Msg.error("trueFrontNeighborAt(..): this Edge hasn't got Node " + n.descr());
		}

		do {
			curEdge = curElem.neighborEdge(n, curEdge);
			curElem = curElem.neighbor(curEdge);
		} while (!curEdge.isFrontEdge());

		return curEdge;
	}
	
	@Override
	public String toString() {
		return descr();
	}
}
