package com.github.karllevik.qmorph.meshing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.karllevik.qmorph.geom.Edge;
import com.github.karllevik.qmorph.geom.Node;
import com.github.karllevik.qmorph.geom.Triangle;
import java.awt.geom.Point2D;

public class MeshLoader {

	public static List<Triangle> triangleList;
	public static List<Edge> edgeList;
	public static List<Node> nodeList;

	private static int cInd;

	public static List<Triangle> loadTriangleMesh(String meshDirectory, String meshFilename) {
		triangleList = new ArrayList<>();
		edgeList = new ArrayList<>();
		// Use HashMap instead of ArrayList for nodes for faster lookup
		Map<Point2D.Double, Node> nodeMap = new HashMap<>();
		List<Node> usNodeList = new ArrayList<>(); // Still keep a list to maintain order if needed, or can be removed if nodeMap
													// is sufficient.

		try (FileInputStream fis = new FileInputStream(meshDirectory + meshFilename); BufferedReader in = new BufferedReader(new InputStreamReader(fis))) {

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				cInd = 0;
				double x1 = nextDouble(inputLine);
				double y1 = nextDouble(inputLine);
				double x2 = nextDouble(inputLine);
				double y2 = nextDouble(inputLine);
				double x3 = nextDouble(inputLine);
				double y3 = nextDouble(inputLine);

				Node node1 = getNode(nodeMap, usNodeList, x1, y1);
				Node node2 = getNode(nodeMap, usNodeList, x2, y2);
				Node node3 = getNode(nodeMap, usNodeList, x3, y3);

				Edge edge1 = getEdge(edgeList, node1, node2);
				Edge edge2 = getEdge(edgeList, node2, node3);
				Edge edge3 = getEdge(edgeList, node1, node3);

				Triangle t = new Triangle(edge1, edge2, edge3);
				t.connectEdges();
				triangleList.add(t);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		nodeList = usNodeList;
		return triangleList;
	}

	/**
	 * Loads a triangle mesh from an array of triangle coordinates. The array should
	 * be in the format double[n][6], where each row represents a triangle with
	 * coordinates x1, y1, x2, y2, x3, y3.
	 *
	 * @param triangleCoordinates A 2D array of triangle coordinates.
	 * @return A list of triangles representing the mesh, or null if an error
	 *         occurred (e.g., invalid input array).
	 */
	public static List<Triangle> loadTriangleMeshFromArray(double[][] triangleCoordinates, boolean meshLenOpt, boolean meshAngOpt) {
		triangleList = new ArrayList<>();
		edgeList = new ArrayList<>();
		Map<Point2D.Double, Node> nodeMap = new HashMap<>();
		List<Node> usNodeList = new ArrayList<>();

		for (double[] coords : triangleCoordinates) {
			double x1 = coords[0];
			double y1 = coords[1];
			double x2 = coords[2];
			double y2 = coords[3];
			double x3 = coords[4];
			double y3 = coords[5];

			Node node1 = getNode(nodeMap, usNodeList, x1, y1);
			Node node2 = getNode(nodeMap, usNodeList, x2, y2);
			Node node3 = getNode(nodeMap, usNodeList, x3, y3);

			Edge edge1 = getEdge(edgeList, node1, node2);
			Edge edge2 = getEdge(edgeList, node2, node3);
			Edge edge3 = getEdge(edgeList, node1, node3);

			Triangle t = new Triangle(edge1, edge2, edge3);
			t.connectEdges();
			triangleList.add(t);
		}
		nodeList = usNodeList; // Assign to class level nodeList if needed
		return triangleList;
	}

	private static Node getNode(Map<Point2D.Double, Node> nodeMap, List<Node> usNodeList, double x, double y) {
		Point2D.Double point = new Point2D.Double(x, y);
		Node node = nodeMap.get(point);
		if (node == null) {
			node = new Node(x, y);
			nodeMap.put(point, node);
			usNodeList.add(node);
		}
		return node;
	}

	private static Edge getEdge(List<Edge> edgeList, Node node1, Node node2) {
		Edge edge = new Edge(node1, node2);
		int index = edgeList.indexOf(edge); // Assuming Edge class has proper equals() and hashCode()
		if (index == -1) {
			edgeList.add(edge);
		} else {
			edge = edgeList.get(index);
		}
		edge.leftNode.connectToEdge(edge);
		edge.rightNode.connectToEdge(edge);
		return edge;
	}

	private static double nextDouble(String inputLine) {
		int startIndex = cInd;
		while (cInd < inputLine.length() && inputLine.charAt(cInd) != ' ' && inputLine.charAt(cInd) != '\t' && inputLine.charAt(cInd) != ',') {
			cInd++;
		}
		String doubleStr = inputLine.substring(startIndex, cInd).trim();
		while (cInd < inputLine.length() && (inputLine.charAt(cInd) == ' ' || inputLine.charAt(cInd) == '\t' || inputLine.charAt(cInd) == ',')) {
			cInd++;
		}
		return Double.parseDouble(doubleStr);
	}
}