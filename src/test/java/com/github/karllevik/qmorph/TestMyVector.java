package com.github.karllevik.qmorph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.karllevik.qmorph.geom.MyVector;
import com.github.karllevik.qmorph.geom.Node;

class TestMyVector {

	@Test
	void testPointIntersectsAt() {
		Node p0 = new Node(0, 0);
		Node p1 = new Node(0, 1);

		MyVector d0 = new MyVector(p0, 5, 5);
		MyVector d1 = new MyVector(p1, 10, 0);

		Node poi = d0.pointIntersectsAt(d1);
		assertNotNull(poi, "poi should not be null");
	}

	@Test
	void testIsCWto() {
		Node p0 = new Node(0, 0);

		// Test 1: Same quadrant (1st)
		MyVector v0 = new MyVector(p0, 10, 10);
		MyVector v1 = new MyVector(p0, 10, 9);
		assertTrue(v1.isCWto(v0), "v1 should be clockwise to v0");

		// Test 2: Opposite quadrants (this in third, v in 1st)
		MyVector v2 = new MyVector(p0, 10, 10);
		MyVector v3 = new MyVector(p0, -9, -10);
		assertTrue(v3.isCWto(v2), "v3 should be clockwise to v2");

		// Test 3: Opposite quadrants (this in second, v in fourth)
		MyVector v4 = new MyVector(p0, 10, -10);
		MyVector v5 = new MyVector(p0, -11, 10);
		assertTrue(v5.isCWto(v4), "v5 should be clockwise to v4");

		// Test 4: Same vector
		MyVector v7 = new MyVector(p0, -9, -10);
		assertFalse(v7.isCWto(v7), "v7 should not be clockwise to itself");

		// Test 5: Special case
		MyVector v8 = new MyVector(p0, -9, 0);
		MyVector v9 = new MyVector(p0, 0, -9);
		assertTrue(v8.isCWto(v9), "v8 should be clockwise to v9");

		// Test 6: Special case with small difference
		MyVector v10 = new MyVector(p0, 0.01, -9);
		assertTrue(v9.isCWto(v10), "v9 should be clockwise to v10");
	}

	@Test
	void testComputeAngle() {
		Node p0 = new Node(0, 0);

		MyVector va = new MyVector(p0, 1, 0);
		MyVector vb = new MyVector(p0, 0, 1);

		double angle0 = vb.computeAngle(vb);
		assertEquals(0, Math.toDegrees(angle0), 0.001, "Angle should be 0 degrees");

		double angle1 = vb.computeAngle(va);
		assertEquals(-90, Math.toDegrees(angle1), 0.001, "Angle should be -90 degrees");

		MyVector vc = new MyVector(p0, -1, -1);
		double angle2 = vb.computeAngle(vc);
		assertEquals(135, Math.toDegrees(angle2), 0.001, "Angle should be 135 degrees");

		MyVector vd = new MyVector(p0, 1, 1.1);
		double angle3 = vd.computeAngle(vc);
		assertTrue(Math.toDegrees(angle3) > 170, "Angle should be near 180 degrees");

		double angle4 = vc.computeAngle(vd);
		assertTrue(Math.toDegrees(angle4) < -170, "Angle should be near -180 degrees");

		MyVector ve = new MyVector(p0, 1, 1);
		double angle5 = ve.computeAngle(vd);
		assertTrue(Math.toDegrees(angle5) < 10, "Angle should be a small positive value");
	}

	@Test
	void testDot() {
		Node p0 = new Node(0, 0);

		MyVector va = new MyVector(p0, 1, 0);
		MyVector vb = new MyVector(p0, 0, 1);
		MyVector vc = new MyVector(p0, -1, -1);
		MyVector vd = new MyVector(p0, 1, 1.1);
		MyVector ve = new MyVector(p0, 1, 1);

		double dot0 = va.dot(va);
		assertEquals(1, dot0, 0.001, "Dot product should be 1");

		double dot1 = vb.dot(va);
		assertEquals(0, dot1, 0.001, "Dot product should be 0");

		double dot2 = vb.dot(vc);
		assertTrue(dot2 < 0, "Dot product should be negative");

		double dot3 = vd.dot(vc);
		assertTrue(dot3 < 0, "Dot product should be negative");

		double dot4 = vc.dot(vd);
		assertTrue(dot4 < 0, "Dot product should be near negative");

		double dot5 = ve.dot(vd);
		assertTrue(dot5 > 0, "Dot product should be near positive");
	}
}
