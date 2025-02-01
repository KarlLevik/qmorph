package com.github.karllevik.qmorph;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.karllevik.qmorph.geom.Quad;
import com.github.karllevik.qmorph.geom.Triangle;
import com.github.karllevik.qmorph.meshing.GeomBasics;
import com.github.karllevik.qmorph.meshing.MeshLoader;
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
		
		List<Triangle> triangles = MeshLoader.loadTriangleMesh("src/test/resources/", "difficult.mesh");
		GeomBasics.edgeList = MeshLoader.edgeList;
		GeomBasics.nodeList = MeshLoader.nodeList;
		GeomBasics.triangleList = MeshLoader.triangleList;

		assertEquals(206, GeomBasics.triangleList.size());
		assertEquals(206, triangles.size());
		assertEquals(157, GeomBasics.nodeList.size());
		assertEquals(countUniqueCoordinates("src/test/resources/" + "difficult.mesh"), GeomBasics.nodeList.size());
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

	private static class Coordinate {
		double x;
		double y;

		public Coordinate(double x, double y) {
			this.x = x;
			this.y = y;
		}

		// Override equals() and hashCode() to ensure proper behavior in a Set
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			Coordinate that = (Coordinate) obj;
			return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0;
		}

		@Override
		public int hashCode() {
			return java.util.Objects.hash(x, y);
		}

		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}

	private static int countUniqueCoordinates(String filePath) {
		// HashSet is used to store unique coordinates
		Set<Coordinate> uniqueCoordinates = new HashSet<>();

		// Try-with-resources ensures the reader is closed automatically
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;

			// Read each line from the file
			while ((line = br.readLine()) != null) {
				// Split the line by commas to get individual numbers
				String[] numberStrings = line.split(",");

				// Process the numbers in pairs (x, y)
				for (int i = 0; i < numberStrings.length - 1; i += 2) {
					try {
						double x = Double.parseDouble(numberStrings[i].trim());
						double y = Double.parseDouble(numberStrings[i + 1].trim());

						// Create a Coordinate object and add it to the set
						uniqueCoordinates.add(new Coordinate(x, y));
					} catch (NumberFormatException e) {
						// Log an error if the value cannot be parsed as a double
						System.err.println("Invalid number format: " + numberStrings[i] + ", " + numberStrings[i + 1]);
					}
				}
			}
		} catch (IOException e) {
			// Handle file reading errors
			System.err.println("Error reading file: " + e.getMessage());
		}

		// Return the count of unique coordinates
		return uniqueCoordinates.size();
	}

}
