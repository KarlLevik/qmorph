package com.github.karllevik.qmorph;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TestHalfPlane {

	@Test
	void testNotInHalfplane() {
		Node pa = new Node(0.0, 0.0);
		Node pb = new Node(0.0, 2.0);
		Node pc = new Node(-1.0, 0.0);

		Edge e1 = new Edge(pa, pb);
		Edge e2 = new Edge(pb, pc);
		Edge e3 = new Edge(pa, pc);

		Triangle t = new Triangle(e1, e2, e3);

		Node p1 = new Node(7.2, 0.0);
		Node p2 = new Node(0.2, -30.0);
		Node p3 = new Node(48, 53.2);
		Node p4 = new Node(0.01, 0.0);

		assertFalse(p1.inHalfplane(t, e1) == 1, "p1 should not be in halfplane left of Edge " + e1.descr());
		assertFalse(p2.inHalfplane(t, e1) == 1, "p2 should not be in halfplane left of Edge " + e1.descr());
		assertFalse(p3.inHalfplane(t, e1) == 1, "p3 should not be in halfplane left of Edge " + e1.descr());
		assertFalse(p4.inHalfplane(t, e1) == 1, "p4 should not be in halfplane left of Edge " + e1.descr());
	}

	@Test
	void testInHalfplane() {
		Node pa = new Node(0.0, 0.0);
		Node pb = new Node(0.0, 2.0);
		Node pc = new Node(-1.0, 0.0);

		Edge e1 = new Edge(pa, pb);
		Edge e2 = new Edge(pb, pc);
		Edge e3 = new Edge(pa, pc);

		Triangle t = new Triangle(e1, e2, e3);

		Node p1 = new Node(-0.1, 0.0);
		Node p2 = new Node(-21.3, -123.2);
		Node p3 = new Node(-0.01, 100.0);
		Node p4 = new Node(-2.0, 12.3);

		assertTrue(p1.inHalfplane(t, e1) == 1, "p1 should be in halfplane left of Edge " + e1.descr());
		assertTrue(p2.inHalfplane(t, e1) == 1, "p2 should be in halfplane left of Edge " + e1.descr());
		assertTrue(p3.inHalfplane(t, e1) == 1, "p3 should be in halfplane left of Edge " + e1.descr());
		assertTrue(p4.inHalfplane(t, e1) == 1, "p4 should be in halfplane left of Edge " + e1.descr());
	}

	@Test
	void testRealLifeHalfplane() {
		Node a = new Node(-1.3, 4.8);
		Node b = new Node(-8.8, 0.7);
		Node c = new Node(-2.3, -2.7);
		Node p = new Node(-2.4, 0.7);

		Edge ab = new Edge(a, b);
		Edge bc = new Edge(b, c);
		Edge ac = new Edge(a, c);

		Triangle abc = new Triangle(ab, bc, ac);

		assertTrue(p.inHalfplane(abc, bc) == 1, "p should be in halfplane right of Edge " + bc.descr()); // Corrected assertion based on original code logic
	}

	@Test
	void testConvexity() {
		Node n1 = new Node(0.0, 0.0);
		Node n2 = new Node(0.0, 1.4);
		Node n3 = new Node(-0.4, 2.9);
		Node n4 = new Node(3.8, 0.9);

		Edge baseEdge = new Edge(n1, n2);
		Edge leftEdge = new Edge(n2, n3);
		Edge rightEdge = new Edge(n1, n4);
		Edge topEdge = new Edge(n3, n4);

		Quad q = new Quad(baseEdge, leftEdge, rightEdge, topEdge);

		assertFalse(q.isStrictlyConvex(), "Quad " + q.descr() + " should not be strictly convex");
	}

	@Test
	void testVectorIntersection() {
		Node n1 = new Node(0.0, 0.0);
		Node n2 = new Node(0.0, 1.4);
		Node n5 = new Node(0.0, 0.2);
		Node n6 = new Node(0.0, 1.1);

		MyVector v0 = new MyVector(n1, n2);
		MyVector v1 = new MyVector(n5, n6);

		assertTrue(v0.intersects(v1), "Vectors should intersect");
	}

}
