package com.github.karllevik.qmorph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.github.karllevik.qmorph.geom.Edge;
import com.github.karllevik.qmorph.geom.Triangle;

class TestTriangle {

    @Test
    void testNextCWEdgeAndNextCCWEdge() {
        // Arrange
        Node n1 = new Node(0, 0);
        Node n2 = new Node(3, 0);
        Node n3 = new Node(0, 3);

        Edge e1 = new Edge(n1, n2);
        Edge e2 = new Edge(n1, n3);
        Edge e3 = new Edge(n2, n3);

        Triangle t = new Triangle(e1, e2, e3);

        // Act
        Edge cwEdge = t.nextCWEdge(e1);
        Edge ccwEdge = t.nextCCWEdge(e1);

        // Assert
        assertEquals(e2, cwEdge, "The next clockwise edge to e1 should be e2");
        assertEquals(e3, t.nextCWEdge(e2), "The next clockwise edge to e2 should be e3");
        assertEquals(e1, t.nextCWEdge(e3), "The next clockwise edge to e3 should be e1");
        assertEquals(e3, ccwEdge, "The next counter-clockwise edge to e1 should be e3");
    }
    
    @Test
    void testClockwiseNodesBecomeCounterclockwise() {
        // Arrange: Create nodes in clockwise order
        Node n1 = new Node(0, 0);  // Bottom-left
        Node n2 = new Node(3, 0);  // Bottom-right
        Node n3 = new Node(0, 3);  // Top-left

        // Create edges in clockwise order
        Edge e1 = new Edge(n1, n2);  // Bottom edge
        Edge e2 = new Edge(n2, n3);  // Right edge
        Edge e3 = new Edge(n3, n1);  // Left edge

        // Act: Create the triangle with clockwise edges
        Triangle triangle = new Triangle(e1, e2, e3, 3, 4.24, 3, 90, 45, 45, true, true);

        // Assert: Check that the triangle is not inverted (i.e., it is CCW)
        assertFalse(triangle.inverted(), "Triangle should not be inverted after initialization");
    }
}
