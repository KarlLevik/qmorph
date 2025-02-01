package com.github.karllevik.qmorph;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.karllevik.qmorph.geom.Quad;
import com.github.karllevik.qmorph.geom.Triangle;
import com.github.karllevik.qmorph.meshing.GeomBasics;
import com.github.karllevik.qmorph.meshing.QMorph;

class TestMeshing {

	@Test
	void testSimple() {
		GeomBasics.meshDirectory = "src/test/resources/";
		GeomBasics.meshFilename = "mesh.txt";

		List<Triangle> triangles = GeomBasics.loadTriangleMesh();
		
		assertEquals(4, GeomBasics.triangleList.size());
		assertEquals(4, triangles.size());
		assertEquals(6, GeomBasics.nodeList.size());
		assertEquals(9, GeomBasics.edgeList.size());

		QMorph q = new QMorph();
		q.init();
		q.run();
		
		assertEquals(2, GeomBasics.getElementList().size()); // 2 quads
		GeomBasics.getElementList().forEach(e -> {
			assertTrue(e instanceof Quad);
			assertEquals(4, e.edgeList.length);
		});
	}
	
	@Test
	void testComplex() {
		GeomBasics.meshDirectory = "src/test/resources/";
		GeomBasics.meshFilename = "difficult.mesh";
		
		List<Triangle> triangles = GeomBasics.loadTriangleMesh();
		
		assertEquals(206, GeomBasics.triangleList.size());
		assertEquals(206, triangles.size());
		assertEquals(157, GeomBasics.nodeList.size());
		assertEquals(364, GeomBasics.edgeList.size());
		
		QMorph q = new QMorph();
		q.init();
		q.run();
		
		assertEquals(95, GeomBasics.getElementList().size());
		GeomBasics.getElementList().forEach(e -> {
			assertTrue(e instanceof Quad);
			assertEquals(4, e.edgeList.length);
		});
	}

}
