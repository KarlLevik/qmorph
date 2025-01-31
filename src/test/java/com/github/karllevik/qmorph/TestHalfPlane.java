package com.github.karllevik.qmorph;

class TestHalfPlane {

	public static void main(String[] args) {
		Node pa = new Node(0.0, 0.0);
		Node pb = new Node(0.0, 2.0);
		Node pc = new Node(-1.0, 0.0);

		Edge e1 = new Edge(pa, pb);
		Edge e2 = new Edge(pb, pc);
		Edge e3 = new Edge(pa, pc);

		Triangle t = new Triangle(e1, e2, e3);

		Msg.debug("*** First set of test Nodes (not in halfplane) ***");
		Node p1 = new Node(7.2, 0.0);
		Node p2 = new Node(0.2, -30.0);
		Node p3 = new Node(48, 53.2);
		Node p4 = new Node(0.01, 0.0);

		if (p1.inHalfplane(t, e1) == 1) {
			Msg.error("p1 incorrectly detected to belong to halfplane left of Edge " + e1.descr());
		} else {
			Msg.debug("p1 correctly detected not to belong to halfplane left of Edge " + e1.descr());
		}

		if (p2.inHalfplane(t, e1) == 1) {
			Msg.error("p2 incorrectly detected to belong to halfplane left of Edge " + e1.descr());
		} else {
			Msg.debug("p2 correctly detected not to belong to halfplane left of Edge " + e1.descr());
		}

		if (p3.inHalfplane(t, e1) == 1) {
			Msg.error("p3 incorrectly detected to belong to halfplane left of Edge " + e1.descr());
		} else {
			Msg.debug("p3 correctly detected not to belong to halfplane left of Edge " + e1.descr());
		}

		if (p4.inHalfplane(t, e1) == 1) {
			Msg.error("p4 incorrectly detected to belong to halfplane left of Edge " + e1.descr());
		} else {
			Msg.debug("p4 correctly detected not to belong to halfplane left of Edge " + e1.descr());
		}

		Msg.debug("*** Second set of test Nodes (in halfplane) ***");
		p1 = new Node(-0.1, 0.0);
		p2 = new Node(-21.3, -123.2);
		p3 = new Node(-0.01, 100.0);
		p4 = new Node(-2.0, 12.3);

		if (p1.inHalfplane(t, e1) == 1) {
			Msg.debug("p1 correctly detected to belong to halfplane left of Edge " + e1.descr());
		} else {
			Msg.error("p1 incorrectly detected not to belong to halfplane left of Edge " + e1.descr());
		}

		if (p2.inHalfplane(t, e1) == 1) {
			Msg.debug("p2 correctly detected to belong to halfplane left of Edge " + e1.descr());
		} else {
			Msg.error("p2 incorrectly detected not to belong to halfplane left of Edge " + e1.descr());
		}

		if (p3.inHalfplane(t, e1) == 1) {
			Msg.debug("p3 correctly detected to belong to halfplane left of Edge " + e1.descr());
		} else {
			Msg.error("p3 incorrectly detected not to belong to halfplane left of Edge " + e1.descr());
		}

		if (p4.inHalfplane(t, e1) == 1) {
			Msg.debug("p4 correctly detected to belong to halfplane left of Edge " + e1.descr());
		} else {
			Msg.error("p4 incorrectly detected not to belong to halfplane left of Edge " + e1.descr());
		}

		Msg.debug("*** A 'real life' test (in halfplane) ***");
		Node a = new Node(-1.3, 4.8);
		Node b = new Node(-8.8, 0.7);
		Node c = new Node(-2.3, -2.7);

		Node p = new Node(-2.4, 0.7);

		Edge ab = new Edge(a, b);
		Edge bc = new Edge(b, c);
		Edge ac = new Edge(a, c);

		Triangle abc = new Triangle(ab, bc, ac);

		if (p.inHalfplane(abc, bc) == 1) {
			Msg.debug("p correctly detected to belong to halfplane right of Edge " + bc.descr());
		} else {
			Msg.error("p incorrectly detected not to belong to halfplane right of Edge " + bc.descr());
		}

		testConvexity();
	}

	public static void testConvexity() {
		Node n1 = new Node(0.0, 0.0);
		Node n2 = new Node(0.0, 1.4);
		Node n3 = new Node(-0.4, 2.9);
		Node n4 = new Node(3.8, 0.9);

		Edge baseEdge = new Edge(n1, n2);
		Edge leftEdge = new Edge(n2, n3);
		Edge rightEdge = new Edge(n1, n4);
		Edge topEdge = new Edge(n3, n4);

		Quad q = new Quad(baseEdge, leftEdge, rightEdge, topEdge);

		if (q.isStrictlyConvex()) {
			Msg.error("method says Quad " + q.descr() + " is strictly convex, but it isn't");
		} else {
			Msg.debug("method correctly reports that Quad " + q.descr() + " is not strictly convex");
		}

		// Run a test on the intersects method of MyVector:
		Node n5 = new Node(0.0, 0.2);
		Node n6 = new Node(0.0, 1.1);

		MyVector v0 = new MyVector(n1, n2);
		MyVector v1 = new MyVector(n5, n6);

		if (v0.intersects(v1)) {
			Msg.debug("Correctly identified intersection");
		} else {
			Msg.error("Incorrectly rejected intersection");
		}
	}

}
