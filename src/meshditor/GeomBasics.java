package meshditor;

import java.io.*;
import java.util.ArrayList;

/** This is a basic geometry class with methods for reading and writing meshes, 
 * sorting Node lists, printing lists, topology inspection, etc.
 */

public class GeomBasics extends Constants {

    protected static ArrayList elementList;    
    protected static ArrayList triangleList;
    protected static ArrayList nodeList;
    protected static ArrayList edgeList;

    protected static Node leftmost=null, rightmost=null, uppermost=null, lowermost=null;

    protected static boolean step= false;

    protected static TopoCleanup topoCleanup;
    protected static GlobalSmooth globalSmooth;

    static String meshFilename;
    static String meshDirectory=".";
    static boolean meshLenOpt;
    static boolean meshAngOpt;

    public static void createNewLists() {
	elementList= new ArrayList();
	triangleList= new ArrayList();
	edgeList= new ArrayList();
	nodeList= new ArrayList();
    }

    public static void setParams(String filename, String dir, boolean len, boolean ang){
	meshFilename= filename;
	meshDirectory= dir;
	meshLenOpt= len;
	meshAngOpt= ang;
    } 

    /** Return the edgeList */
    public static ArrayList getEdgeList() {
	return edgeList;
    }

    /** Return the nodeList */
    public static ArrayList getNodeList() {
	return nodeList;
    }

    /** Return the triangleList */    
    public static ArrayList getTriangleList() {
	return triangleList;
    }

    /** Return the elementList */
    public static ArrayList getElementList() {
	return elementList;
    }

    private static GeomBasics curMethod= null;

    public static void setCurMethod(GeomBasics method) {
	curMethod= method;
    }
    
    public static GeomBasics getCurMethod() {
	return curMethod;
    }

    /** This method should be implemented in each of the subclasses. */
    public void step() {}

    /** Delete all the edges in the mesh. */
    public static void clearEdges() {
	Element curElem;
	for (int i= 0; i< elementList.size(); i++) {
	    curElem= (Element) elementList.get(i);
	    curElem.disconnectEdges();
	}

	Edge curEdge;
	for (int i= 0; i< edgeList.size(); i++) {
	    curEdge= (Edge) edgeList.get(i);
	    curEdge.disconnectNodes();
	}
	
	Node curNode;
	for (int i= 0; i< nodeList.size(); i++) {
	    curNode= (Node) nodeList.get(i);
	    curNode.edgeList.clear();
	}

	elementList.clear();
	if (triangleList!= null)
	  triangleList.clear();
	edgeList.clear();
    }

    /** Clear the nodeList, edgeList, triangleList and elementList. */
    public static void clearLists() {
	if (nodeList!= null)
	    nodeList.clear();
	else
	    nodeList= new ArrayList();
	if (edgeList!= null) 
	    edgeList.clear();
	else
	    edgeList= new ArrayList();
	if (triangleList!= null)
	    triangleList.clear();
	else
	    triangleList= new ArrayList();
	if (elementList!= null)
	    elementList.clear();
	else
	    elementList= new ArrayList();
    }

    /** Update distortion metric for all elements in mesh. */
    public static void updateMeshMetrics() {
	Triangle tri;
	Element elem;
	if (elementList.size()> 0)
	    for (int i= 0; i< elementList.size(); i++) {
		elem= (Element) elementList.get(i);
		if (elem!= null)
		    elem.updateDistortionMetric();
	    }
	else 
	    for (int i= 0; i< triangleList.size(); i++) {
		tri= (Triangle) triangleList.get(i);
		if (tri!= null)
		    tri.updateDistortionMetric();
	    }
    }

    /** @return a string containing the average and minimum element metrics. */
    public static String meshMetricsReport() {
	Triangle tri;
	Quad q;
	Element elem;
	Node n;
	double sumMetric= 0.0, minDM= java.lang.Double.MAX_VALUE;
	int size= 0, nTris= 0, nQuads= 0;
	String s;
	byte temp, no2valents= 0, no3valents= 0, no4valents= 0, no5valents= 0,
	    no6valents= 0, noXvalents= 0;

	if (elementList.size()> 0) {

	    for (int i= 0; i< elementList.size(); i++) {
		elem= (Element) elementList.get(i);
		
		if (elem!= null) {
		    size++;
		    if (elem instanceof Triangle)
			nTris++;
		    else if (elem instanceof Quad) {
			q= (Quad) elem;
			if (q.isFake)
			    nTris++;
			else
			    nQuads++;
		    }
		    elem.updateDistortionMetric();
		    sumMetric+= elem.distortionMetric;
		    if (elem.distortionMetric < minDM)
			minDM= elem.distortionMetric;
		}
	    }
	}
	
	if (triangleList.size()> 0) {
	    for (int i= 0; i< triangleList.size(); i++) {
		tri= (Triangle) triangleList.get(i);
		if (tri!= null) {
		    size++;
		    nTris++;
		    tri.updateDistortionMetric();
		    sumMetric+= tri.distortionMetric;
		    if (tri.distortionMetric < minDM)
			minDM= tri.distortionMetric;
		}
	    }
	    
	}
	
	s= "Average distortion metric: " + (sumMetric/size) +"\n" +
	    "Minimum distortion metric: " + minDM +"\n";


	for (int i= 0; i< nodeList.size(); i++) {
	    n= (Node)nodeList.get(i);
	    
	    temp= n.valence();
	    if (temp== 2)
		no2valents++;
	    else if (temp== 3)
		no3valents++;
	    else if (temp== 4)
		no4valents++;
	    else if (temp== 5)
		no5valents++;
	    else if (temp== 6)
		no6valents++;
	    else if (temp> 6)
		noXvalents++;
	}

	if (no2valents>0)
	    s=s+"Number of 2-valent nodes: "+no2valents + "\n";
	if (no3valents>0)
	    s=s+"Number of 3-valent nodes: "+no3valents + "\n";
	if (no4valents>0)
	    s=s+"Number of 4-valent nodes: "+no4valents + "\n";
	if (no5valents>0)
	    s=s+"Number of 5-valent nodes: "+no5valents + "\n";
	if (no6valents>0)
	    s=s+"Number of 6-valent nodes: "+no6valents + "\n";
	if (noXvalents>0)
	    s=s+"Number of nodes with valence > 6: "+noXvalents + "\n";
	
	s=s+"Number of quadrilateral elements: "+nQuads+"\n"+
	    "Number of triangular elements: "+nTris+"\n"+
	    "Number of edges: "+edgeList.size()+"\n"+
	    "Number of nodes: "+nodeList.size();

	return s;
    }

    /** Find inverted elements and paint them with red colour. */
    public static void detectInvertedElements() {
	Element elem;
	Triangle t;
	int i;
	for (i= 0; i< elementList.size(); i++) {
	    elem= (Element)elementList.get(i);
	    if (elem!= null && elem.inverted()) {
		elem.markEdgesIllegal();
		Msg.warning("Element "+elem.descr()+" is inverted.");
		Msg.warning("It has firstNode "+elem.firstNode.descr());
	    }
	}

	for (i= 0; i< triangleList.size(); i++) {
	    t= (Triangle)triangleList.get(i);
	    if (t!= null && t.inverted()) {
		t.markEdgesIllegal();
		Msg.warning("Triangle "+t.descr()+" is inverted.");
		Msg.warning("It has firstNode "+t.firstNode.descr());
	    }
	}
    }

    /** Output nr of tris and fake quads in mesh. */
    public static void countTriangles() {
	int fakes= 0, tris= 0;
	Triangle t;
	Element elem;
	for (int i= 0; i< elementList.size(); i++) {
	    elem= (Element)elementList.get(i);
	    if (elem instanceof Quad && ((Quad)elem).isFake)
		fakes++;
	    else if (elem instanceof Triangle)
		tris++;
	}

	Msg.debug("Counted # of fake quads: "+fakes);
	Msg.debug("Counted # of triangles: "+tris);
    }

    
    /** Output warnings if mesh is not consistent. */
    public static void consistencyCheck() {
	Msg.debug("Entering consistencyCheck()");
	Node n;
	for (int i= 0; i< nodeList.size(); i++) {
	    n= (Node) nodeList.get(i);
	    
	    Edge e;
	    if (n.edgeList.size()== 0)
		Msg.warning("edgeList.size()== 0 for node "+n.descr());

	    for (int j= 0; j< n.edgeList.size(); j++) {
		e= (Edge) n.edgeList.get(j);
		if (e== null)
		    Msg.warning("Node "+n.descr()+" has a null in its edgeList.");
		else if (edgeList.indexOf(e)== -1)
		    Msg.warning("Edge "+e.descr()+" found in the edgeList of Node "+
				n.descr()+", but not in global edgeList");
	    }
	}

	Edge e;
	for (int i= 0; i< edgeList.size(); i++) {
	    e= (Edge)edgeList.get(i);
	    if (e.leftNode.edgeList.indexOf(e)== -1)
		Msg.warning("leftNode of edge "+e.descr()+
			    " has not got that edge in its .edgeList");
	    if (e.rightNode.edgeList.indexOf(e)== -1)
		Msg.warning("rightNode of edge "+e.descr()+
			    " has not got that edge in its .edgeList");
   
	    if (e.element1== null && e.element2== null)
		Msg.warning("Edge "+e.descr()+" has null in both element pointers");

	    if (e.element1== null && e.element2!= null)
		Msg.warning("Edge "+e.descr()+" has null in element1 pointer");

	    if (e.element1!= null && !triangleList.contains(e.element1) 
		&& !elementList.contains(e.element1))
		Msg.warning("element1 of edge "+e.descr()+
			    " is not found in triangleList or elementList");

	    if (e.element2!= null && !triangleList.contains(e.element2) 
		&& !elementList.contains(e.element2))
		Msg.warning("element2 of edge "+e.descr()+
			    " is not found in triangleList or elementList");
	    
	    if (nodeList.indexOf(e.leftNode)== -1)
		Msg.warning("leftNode of edge "+e.descr()+" not found in nodeList.");
	    if (nodeList.indexOf(e.rightNode)== -1)
		Msg.warning("rightNode of edge "+e.descr()+" not found in nodeList.");
	}
	
	Triangle t;
	Node na,nb,nc;
	double cross1;

	for (int i= 0; i< triangleList.size(); i++) {
	    t= (Triangle)triangleList.get(i);
	    if (!t.edgeList[0].hasElement(t))
		Msg.warning("edgeList[0] of triangle "+t.descr()+
			    " has not got that triangle as an adjacent element");

	    if (!t.edgeList[1].hasElement(t))
		Msg.warning("edgeList[1] of triangle "+t.descr()+
			    " has not got that triangle as an adjacent element");

	    if (!t.edgeList[2].hasElement(t))
		Msg.warning("edgeList[2] of triangle "+t.descr()+
			    " has not got that triangle as an adjacent element");


	    if (t.edgeList[0].commonNode(t.edgeList[1])== null)
		Msg.warning("edgeList[0] and edgeList[1] of triangle "+t.descr()+
			    " has no common Node");
	    if (t.edgeList[1].commonNode(t.edgeList[2])== null)
		Msg.warning("edgeList[1] and edgeList[2] of triangle "+t.descr()+
			    " has no common Node");
	    if (t.edgeList[2].commonNode(t.edgeList[0])== null)
		Msg.warning("edgeList[2] and edgeList[0] of triangle "+t.descr()+
			    " has no common Node");

	    na= t.edgeList[0].leftNode;
	    nb= t.edgeList[0].rightNode;
	    nc= t.oppositeOfEdge(t.edgeList[0]);
	    
	    cross1= cross(na, nc, nb, nc); // The cross product nanc x nbnc

	    if (cross1== 0 /*!t.areaLargerThan0()*/)
		Msg.warning("Degenerate triangle in triangleList, t= "+t.descr());
	}


	Element elem;
	Quad q;
	for (int i= 0; i< elementList.size(); i++) {
	    elem= (Element)elementList.get(i);
	    if (elem== null)
		Msg.debug("elementList has a null-entry.");
	    else if (elem instanceof Quad) {
		q= (Quad)elem;
	    
		if (!q.edgeList[base].hasElement(q))
		    Msg.warning("edgeList[base] of quad "+q.descr()+
				" has not got that quad as an adjacent element");

		if (!q.edgeList[left].hasElement(q))
		    Msg.warning("edgeList[left] of quad "+q.descr()+
				" has not got that quad as an adjacent element");
		
		if (!q.edgeList[right].hasElement(q))
		    Msg.warning("edgeList[right] of quad "+q.descr()+
				" has not got that quad as an adjacent element");

		if (!q.isFake && !q.edgeList[top].hasElement(q))
		    Msg.warning("edgeList[top] of quad "+q.descr()+
				" has not got that quad as an adjacent element");


		if (q.edgeList[base].commonNode(q.edgeList[left])== null)
		    Msg.warning("edgeList[base] and edgeList[left] of quad "+q.descr()+
				" has no common Node");
		if (q.edgeList[base].commonNode(q.edgeList[right])== null)
		    Msg.warning("edgeList[base] and edgeList[right] of quad "+q.descr()+
				" has no common Node");
		if (!q.isFake && q.edgeList[left].commonNode(q.edgeList[top])== null)
		    Msg.warning("edgeList[left] and edgeList[top] of quad "+q.descr()+
				" has no common Node");
		if (!q.isFake && q.edgeList[right].commonNode(q.edgeList[top])== null)
		    Msg.warning("edgeList[right] and edgeList[top] of quad "+q.descr()+
				" has no common Node");

		if (q.isFake && q.edgeList[left].commonNode(q.edgeList[right])== null)
		    Msg.warning("edgeList[left] and edgeList[right] of fake quad "+
				q.descr()+" has no common Node");
	    }
	}

	Msg.debug("Leaving consistencyCheck()");




    }

    /** Load a mesh from a file. */
    public static ArrayList loadMesh() {
	FileInputStream fis;
	Node node1, node2, node3, node4;
	Edge edge1, edge2, edge3, edge4;
	Triangle t;
	Quad q;
	
	elementList= new ArrayList();
	triangleList= new ArrayList();
	edgeList= new ArrayList();
	ArrayList usNodeList= new ArrayList();
	
	try {
	    fis = new FileInputStream(meshDirectory+meshFilename);
	    BufferedReader in = new BufferedReader(new InputStreamReader(fis));
	    double x1,x2,x3,x4,y1,y2,y3,y4;
	    int i= 0;
	    
	    try {
		String inputLine;
		inputLine= in.readLine();
		while (inputLine!= null) {
		    cInd= 0;
		    x1= nextDouble(inputLine);
		    y1= nextDouble(inputLine);
		    x2= nextDouble(inputLine);
		    y2= nextDouble(inputLine);
		    x3= nextDouble(inputLine);
		    y3= nextDouble(inputLine);
		    x4= nextDouble(inputLine);
		    y4= nextDouble(inputLine);
 		    
		    node1 = new Node(x1,y1);
		    if (!usNodeList.contains(node1))
			usNodeList.add(node1);
		    else
			node1=(Node)usNodeList.get(usNodeList.indexOf(node1));
		    node2 = new Node(x2,y2);
		    if (!usNodeList.contains(node2))
			usNodeList.add(node2);
		    else
			node2=(Node)usNodeList.get(usNodeList.indexOf(node2));
		    node3 = new Node(x3,y3);
		    if (!usNodeList.contains(node3))
			usNodeList.add(node3);
		    else
			node3=(Node)usNodeList.get(usNodeList.indexOf(node3));
		    
		    
		    edge1= new Edge(node1, node2);
		    if (!edgeList.contains(edge1)) {
			edgeList.add(edge1); 
			edge1.connectNodes();
		    }
		    else
			edge1= (Edge)edgeList.get(edgeList.indexOf(edge1));
		    
		    edge2= new Edge(node1, node3);
		    if (!edgeList.contains(edge2)) {
			edgeList.add(edge2); 
			edge2.connectNodes();
		    }
		    else
			edge2= (Edge)edgeList.get(edgeList.indexOf(edge2));
		    
		    if (!Double.isNaN(x4) && !Double.isNaN(y4)) {
			node4 = new Node(x4,y4);
			if (!usNodeList.contains(node4))
			    usNodeList.add(node4);
			else
			    node4=(Node)usNodeList.get(usNodeList.indexOf(node4));
 			
			edge3= new Edge(node2, node4);
			if (!edgeList.contains(edge3)) {
			    edgeList.add(edge3);
			    edge3.connectNodes();
			}
			else
			    edge3= (Edge)edgeList.get(edgeList.indexOf(edge3));
			
			edge4= new Edge(node3, node4);
			if (!edgeList.contains(edge4)) {
			    edgeList.add(edge4);
			    edge4.connectNodes();
			}
			else
			    edge4= (Edge)edgeList.get(edgeList.indexOf(edge4));
			
			q= new Quad(edge1, edge2, edge3, edge4);
			q.connectEdges();
			elementList.add(q);
		    }
		    else {
			edge3= new Edge(node2, node3);
			if (!edgeList.contains(edge3)) {
			    edgeList.add(edge3);
			    edge3.connectNodes();
			}
			else
			    edge3= (Edge)edgeList.get(edgeList.indexOf(edge3));
			
			t= new Triangle(edge1,edge2,edge3);
			t.connectEdges();
			triangleList.add(t);
			//elementList.add(t);
		    }
		    inputLine= in.readLine();
		}
	    }
	    catch (Exception e) {
		Msg.error("Cannot read triangle-mesh data.");
	    }
	}
	catch (Exception e) {
	    Msg.error("File "+meshFilename+" not found.");
	}

	nodeList= usNodeList; //sortNodes(usNodeList);
	return elementList;
    }
    
    /** Load a triangle mesh from a file. */
    public static ArrayList loadTriangleMesh() {
	FileInputStream fis;
	Node node1, node2, node3;
	Edge edge1, edge2, edge3;
	Triangle t;
	
	triangleList= new ArrayList();
	edgeList= new ArrayList();
	ArrayList usNodeList= new ArrayList();
	
	try {
	    fis = new FileInputStream(meshDirectory+meshFilename);
	    BufferedReader in = new BufferedReader(new InputStreamReader(fis));
	    double x1,x2,x3,y1,y2,y3,len1=0,len2=0,len3=0,ang1=0,ang2=0,ang3=0;
	    int i= 0;
	    
	    try {
		String inputLine;
		inputLine= in.readLine();
		while (inputLine!= null) {
		    cInd= 0;
		    x1= nextDouble(inputLine);
		    y1= nextDouble(inputLine);
		    x2= nextDouble(inputLine);
		    y2= nextDouble(inputLine);
		    x3= nextDouble(inputLine);
		    y3= nextDouble(inputLine);
		    
		    node1 = new Node(x1,y1);
		    if (!usNodeList.contains(node1))
			usNodeList.add(node1);
		    else
			node1=(Node)usNodeList.get(usNodeList.indexOf(node1));
		    node2 = new Node(x2,y2);
		    if (!usNodeList.contains(node2))
			usNodeList.add(node2);
		    else
			node2=(Node)usNodeList.get(usNodeList.indexOf(node2));
		    node3 = new Node(x3,y3);
		    if (!usNodeList.contains(node3))
			usNodeList.add(node3);
		    else
			node3=(Node)usNodeList.get(usNodeList.indexOf(node3));
		    
		    
		    edge1= new Edge(node1, node2);
		    if (!edgeList.contains(edge1))
			edgeList.add(edge1);
		    else
			edge1= (Edge)edgeList.get(edgeList.indexOf(edge1));
		    edge1.leftNode.connectToEdge(edge1);
		    edge1.rightNode.connectToEdge(edge1);
		    
		    edge2= new Edge(node2, node3);
		    if (!edgeList.contains(edge2))
			edgeList.add(edge2);
		    else
			edge2= (Edge)edgeList.get(edgeList.indexOf(edge2));
		    edge2.leftNode.connectToEdge(edge2);
		    edge2.rightNode.connectToEdge(edge2);
		    
		    edge3= new Edge(node1, node3);
		    if (!edgeList.contains(edge3))
			edgeList.add(edge3);
		    else
			edge3= (Edge)edgeList.get(edgeList.indexOf(edge3));
		    edge3.leftNode.connectToEdge(edge3);
		    edge3.rightNode.connectToEdge(edge3);
		    
		    if (meshLenOpt) {
			len1= nextDouble(inputLine);
			len2= nextDouble(inputLine);
			len3= nextDouble(inputLine);
		    }
		    
		    if (meshAngOpt) {
			ang1= nextDouble(inputLine);
			ang2= nextDouble(inputLine);
			ang3= nextDouble(inputLine);
		    }
		    
		    t= new Triangle(edge1,edge2,edge3,len1,len2,len3,ang1,ang2,ang3,meshLenOpt, meshAngOpt);
		    t.connectEdges();
		    triangleList.add(t);
		    inputLine= in.readLine();
		}
	    }
	    catch (Exception e) {
		Msg.error("Cannot read triangle-mesh data.");
	    }
	}
	catch (Exception e) {
	    Msg.error("File "+meshFilename+" not found.");
	}
	nodeList= usNodeList; //sortNodes(usNodeList);
	return triangleList;
    }

    /** A method to read node files. */
    public static ArrayList loadNodes() {
	FileInputStream fis;
	Node node1, node2, node3, node4;
	ArrayList usNodeList= new ArrayList();
	
	try {
	    fis = new FileInputStream(meshDirectory+meshFilename);
	    BufferedReader in = new BufferedReader(new InputStreamReader(fis));
	    double x1,x2,x3,y1,y2,y3,x4,y4;
	    
	    try {
		String inputLine;
		inputLine= in.readLine();
		while (inputLine!= null) {
		    cInd= 0;
		    x1= nextDouble(inputLine);
		    y1= nextDouble(inputLine);
		    x2= nextDouble(inputLine);
		    y2= nextDouble(inputLine);
		    x3= nextDouble(inputLine);
		    y3= nextDouble(inputLine);
		    x4= nextDouble(inputLine);
		    y4= nextDouble(inputLine);
		    
		    if (!Double.isNaN(x1) && !Double.isNaN(y1)) {
			node1 = new Node(x1,y1);
			if (!usNodeList.contains(node1))
			    usNodeList.add(node1);
		    }
		    if (!Double.isNaN(x2) && !Double.isNaN(y2)) {
			node2 = new Node(x2,y2);
			if (!usNodeList.contains(node2))
			    usNodeList.add(node2);
		    }
		    if (!Double.isNaN(x3) && !Double.isNaN(y3)) {
			node3 = new Node(x3,y3);
			if (!usNodeList.contains(node3))
			    usNodeList.add(node3);
		    }
		    if (!Double.isNaN(x4) && !Double.isNaN(y4)) {
		      node4 = new Node(x4,y4);
		      if (!usNodeList.contains(node4))
			usNodeList.add(node4);
		    }
		    inputLine= in.readLine();
		}
	    }
	    catch (Exception e) {
		Msg.error("Cannot read node file data.");
	    }
	}
	catch (Exception e) {
	    Msg.error("File "+meshFilename+" not found.");
	}
	
	//	nodeList= sortNodes(usNodeList);
	nodeList= usNodeList;
	return usNodeList; 
    }

    /** Method for writing to a LaTeX drawing format (need the epic and eepic packages). 
     */ 
    public static boolean exportMeshToLaTeX(String filename, int unitlength, 
					    double xcorr, double ycorr, boolean visibleNodes) {
	FileOutputStream fos;
	Edge edge;
	Node n;
	int i;
	ArrayList boundary= new ArrayList();

	findExtremeNodes();
	
	// Collect boundary edges in a list
	for (i= 0; i< edgeList.size(); i++) {
	    edge= (Edge)edgeList.get(i);
	    if (edge.boundaryEdge())
		boundary.add(edge);
	}


	try {
	    fos = new FileOutputStream(filename);
	    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
	    double x1,x2,x3,x4,y1,y2,y3,y4;
	    double width= rightmost.x -leftmost.x, height= uppermost.y -lowermost.y;

	    try {

		out.write("% Include in the header of your file:"); out.newLine();
		out.write("% \\usepackage{epic, eepic}"); out.newLine();
		out.newLine();
		out.write("\\begin{figure}[!Htbp]"); out.newLine();
		out.write("\\begin{center}"); out.newLine();
		out.write("\\setlength{\\unitlength}{"+unitlength+"mm}"); out.newLine();
		out.write("\\begin{picture}("+width+","+height+")"); out.newLine();
		out.write("\\filltype{black}"); out.newLine();

		// All boundary edges...
		out.write("\\thicklines"); out.newLine();
		for (i= 0; i< boundary.size(); i++) {
		    edge= (Edge)boundary.get(i);

		    x1= edge.leftNode.x + xcorr;
		    y1= edge.leftNode.y + ycorr;
		    x2= edge.rightNode.x + xcorr;
		    y2= edge.rightNode.y + ycorr;
		    
		    out.write("\\drawline[1]("+x1+","+y1+")("+x2+","+y2+")");
		    out.newLine();
		}

		// All other edges...
		out.write("\\thinlines"); out.newLine();
		for (i= 0; i< edgeList.size(); i++) {
		    edge= (Edge)edgeList.get(i);
		    
		    if (!edge.boundaryEdge()) {
			x1= edge.leftNode.x + xcorr;
			y1= edge.leftNode.y + ycorr;
			x2= edge.rightNode.x + xcorr;
			y2= edge.rightNode.y + ycorr;
			
			out.write("\\drawline[1]("+x1+","+y1+")("+x2+","+y2+")");
			out.newLine();
		    }
		}

		// All nodes...
		if (visibleNodes) 
		    for (i= 0; i< nodeList.size(); i++) {
			n= (Node)nodeList.get(i);
			out.write("\\put("+(n.x+xcorr)+","+(n.y+ycorr)+"){\\circle*{0.1}}"); 
			out.newLine();
		    }

		out.write("\\end{picture}"); out.newLine();
		out.write("\\end{center}"); out.newLine();
		out.write("\\end{figure}"); out.newLine();

		out.close();
	    }
	    catch (Exception e) {
		Msg.error("Cannot write quad-mesh data export file.");
	    }
	}
	catch (Exception e) {
	    Msg.error("File "+filename+" not found.");
	}
	return true;
    }

    /** Write all elements in elementList to a file.*/
    public static boolean writeQuadMesh(String filename, ArrayList elementList) {
	FileOutputStream fos;
	Element elem;
	Triangle t;
	Quad q;
	
	try {
	    fos = new FileOutputStream(filename);
	    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
	    double x1,x2,x3,x4,y1,y2,y3,y4;
	    
	    try {
		for (int i= 0; i< elementList.size(); i++) {
		    elem= (Element) elementList.get(i);
		    if (elem instanceof Quad) {
			q= (Quad)elem;
			x1= q.edgeList[base].leftNode.x;
			y1= q.edgeList[base].leftNode.y;
			x2= q.edgeList[base].rightNode.x;
			y2= q.edgeList[base].rightNode.y;
			x3= q.edgeList[left].otherNode(q.edgeList[base].leftNode).x;
			y3= q.edgeList[left].otherNode(q.edgeList[base].leftNode).y;
			x4= q.edgeList[right].otherNode(q.edgeList[base].rightNode).x;
			y4= q.edgeList[right].otherNode(q.edgeList[base].rightNode).y;
			
			out.write(x1+", "+y1+", "+x2+", "+y2+", "+
				  x3+", "+y3+", "+x4+", "+y4);
		    }
		    else {
			t= (Triangle)elem;
			x1= t.edgeList[0].leftNode.x;
			y1= t.edgeList[0].leftNode.y;
			x2= t.edgeList[0].rightNode.x;
			y2= t.edgeList[0].rightNode.y;
			if (!t.edgeList[1].leftNode.equals(t.edgeList[0].leftNode) &&
			    !t.edgeList[1].leftNode.equals(t.edgeList[0].rightNode)) {
			    x3= t.edgeList[1].leftNode.x;
			    y3= t.edgeList[1].leftNode.y;
			}
			else {
			    x3= t.edgeList[1].rightNode.x;
			    y3= t.edgeList[1].rightNode.y;
			}
			out.write(x1+", "+y1+", "+x2+", "+y2+", "+x3+", "+y3);
		    }
		    out.newLine();
		}
		out.close();
	    }
	    catch (Exception e) {
		Msg.error("Cannot write quad-mesh data.");
	    }
	}
	catch (Exception e) {
	    Msg.error("File "+filename+" not found.");
	}
	return true;
    }

    /** Write all elements in elementList and triangleList to a file.*/
    public static boolean writeMesh(String filename) {
	FileOutputStream fos= null;
	Element elem;
	Triangle t;
	Quad q;
	
	try {
	    fos = new FileOutputStream(filename);
	}	
	catch (Exception e) {
	    Msg.error("File "+filename+" not found.");
	}

	BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
	double x1,x2,x3,x4,y1,y2,y3,y4;
	    
	if (triangleList!= null)
	    for (int i= 0; i< triangleList.size(); i++) {
		t= (Triangle) triangleList.get(i);
		x1= t.edgeList[0].leftNode.x;
		y1= t.edgeList[0].leftNode.y;
		x2= t.edgeList[0].rightNode.x;
		y2= t.edgeList[0].rightNode.y;
		if (!t.edgeList[1].leftNode.equals(t.edgeList[0].leftNode) &&
		    !t.edgeList[1].leftNode.equals(t.edgeList[0].rightNode)) {
		    x3= t.edgeList[1].leftNode.x;
		    y3= t.edgeList[1].leftNode.y;
		}
		else {
		    x3= t.edgeList[1].rightNode.x;
		    y3= t.edgeList[1].rightNode.y;
		}
		try {
		    out.write(x1+", "+y1+", "+x2+", "+y2+", "+x3+", "+y3);
		    out.newLine();
		}
		catch (Exception e) {
		    Msg.error("Cannot write quad-mesh data.");
		}

	    }
		
	if (elementList!= null)
	    for (int i= 0; i< elementList.size(); i++) {
		
		if (elementList.get(i) instanceof Quad) {
		    q= (Quad) elementList.get(i);
		
		    x1= q.edgeList[base].leftNode.x;
		    y1= q.edgeList[base].leftNode.y;
		    x2= q.edgeList[base].rightNode.x;
		    y2= q.edgeList[base].rightNode.y;
		    x3= q.edgeList[left].otherNode(q.edgeList[base].leftNode).x;
		    y3= q.edgeList[left].otherNode(q.edgeList[base].leftNode).y;
		    if (!q.isFake) {
			x4= q.edgeList[right].otherNode(q.edgeList[base].rightNode).x;
			y4= q.edgeList[right].otherNode(q.edgeList[base].rightNode).y;
			try {
			    out.write(x1+", "+y1+", "+x2+", "+y2+", "+
				      x3+", "+y3+", "+x4+", "+y4);
			    out.newLine();	
			}
			catch (Exception e) {
			    Msg.error("Cannot write quad-mesh data.");
			}
			
		    }
		    else {
			try {
			    out.write(x1+", "+y1+", "+x2+", "+y2+", "+x3+", "+y3);
			}
			catch (Exception e) {
			    Msg.error("Cannot write quad-mesh data.");
			}
		    }
		}
		else if (elementList.get(i) instanceof Triangle) {
		    t= (Triangle) elementList.get(i);
		    x1= t.edgeList[0].leftNode.x;
		    y1= t.edgeList[0].leftNode.y;
		    x2= t.edgeList[0].rightNode.x;
		    y2= t.edgeList[0].rightNode.y;
		    if (!t.edgeList[1].leftNode.equals(t.edgeList[0].leftNode) &&
			!t.edgeList[1].leftNode.equals(t.edgeList[0].rightNode)) {
			x3= t.edgeList[1].leftNode.x;
			y3= t.edgeList[1].leftNode.y;
		    }
		    else {
			x3= t.edgeList[1].rightNode.x;
			y3= t.edgeList[1].rightNode.y;
		    }
		    try {
			out.write(x1+", "+y1+", "+x2+", "+y2+", "+x3+", "+y3);
			out.newLine();
		    }
		    catch (Exception e) {
			Msg.error("Cannot write quad-mesh data.");
		    }
		}
	    }

	try {
	    out.close();
	}
	catch (Exception e) {
	    Msg.error("Cannot write quad-mesh data.");
	}
	return true;
    }

    /** Write all nodes in nodeList to a file.*/
    public static boolean writeNodes(String filename) {
	FileOutputStream fos;
	Node n;

	try {
	    fos = new FileOutputStream(filename);
	    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
	    double x,y;
	    
	    try {
		if (nodeList!= null)
		    for (int i= 0; i< nodeList.size(); i++) {
			n= (Node) nodeList.get(i);
			x= n.x;
			y= n.y;
			out.write(x+", "+y);
			out.newLine();
		    }
		out.close();
	    }
	    catch (Exception e) {
		Msg.error("Cannot write node data.");
	    }
	}
	catch (Exception e) {
	    Msg.error("Could not open file "+filename);
	}
	return true;
    }


    /** Find the leftmost, rightmost, uppermost, and lowermost nodes. */
    public static void findExtremeNodes() {
	//	nodeList= sortNodes(nodeList);
	if (nodeList== null || nodeList.size()== 0) {
	    leftmost= null;
	    rightmost= null;
	    uppermost= null;
	    lowermost= null;
	    return;
	}
	
	leftmost= (Node)nodeList.get(0);
	rightmost= leftmost;
	uppermost= leftmost;
	lowermost= leftmost;

	Node curNode;
	for (int i= 1; i< nodeList.size(); i++) {
	    curNode= (Node)nodeList.get(i);

	    if ((curNode.x< leftmost.x) || 
		(curNode.x== leftmost.x && curNode.y > leftmost.y))
		leftmost= curNode;
	    if ((curNode.x> rightmost.x) ||
		(curNode.x== rightmost.x && curNode.y < rightmost.y))
		rightmost= curNode;

	    if ((curNode.y> uppermost.y) ||
		(curNode.y== uppermost.y && curNode.x < uppermost.x))
		uppermost= curNode;
	    if ((curNode.y< lowermost.y) ||
		(curNode.y== lowermost.y && curNode.x < lowermost.x))
		lowermost= curNode;
	}

    }

    /** Sort nodes left to right. Higher y-values are preferred to lower ones. */
    public static ArrayList sortNodes(ArrayList unsortedNodes) {
	ArrayList sortedNodes= new ArrayList();
	Node curNode, candNode;
	while (unsortedNodes.size()> 0) {
	    curNode= (Node)unsortedNodes.get(0);
	    for (int i= 1; i< unsortedNodes.size(); i++) {
		candNode= (Node)unsortedNodes.get(i);
		if (candNode.x < curNode.x || 
 		    (candNode.x== curNode.x && candNode.y < curNode.y))
		    curNode= candNode;
	    }
	    sortedNodes.add(curNode);
	    unsortedNodes.remove(unsortedNodes.indexOf(curNode));
	}

	// Find the leftmost, rightmost, uppermost, and lowermost nodes. 
	leftmost= (Node)sortedNodes.get(0);
	rightmost= (Node)sortedNodes.get(sortedNodes.size()-1);
	uppermost= leftmost;
	lowermost= leftmost;

	for (int i= 1; i< sortedNodes.size(); i++) {
	    curNode= (Node)sortedNodes.get(i);
	    if (curNode.y> uppermost.y)
		uppermost= curNode;
	    if (curNode.y< lowermost.y)
		lowermost= curNode;
	}

	return sortedNodes;
    }


    private static int cInd=0;
    /** Method to assist the different load... methods.
     * @param iline a comma-separated string
     * @return the next double value from iline. If no more numbers, then return NaN.
     */
    private static double nextDouble(String iline) {
	String ndouble;
	if (cInd>iline.length())
	    return Double.NaN;
	int nInd=iline.indexOf(",", cInd);
	if (nInd== -1)
	    nInd= iline.length();
	
	ndouble= iline.substring(cInd, nInd);
	cInd= nInd+1;
	return java.lang.Double.valueOf(ndouble).doubleValue();
    }

    public static void printVectors(ArrayList vectorList) {
	if (Msg.debugMode) {
	    MyVector v;
	    for (int i= 0; i< vectorList.size(); i++) {
		v= (MyVector)vectorList.get(i);
		v.printMe();
	    }
	}
    }
    
    public static void printElements(ArrayList elemList) {
	if (Msg.debugMode) {
	    Element elem;
	    for (int i= 0; i< elemList.size(); i++) {
		elem= (Element)elemList.get(i);
		elem.printMe();
	    }
	}
    }
    
    public static void printTriangles(ArrayList triangleList) {
	Msg.debug("triangleList: (size== "+triangleList.size()+")");
	printElements(triangleList);
    }
    
    public static void printQuads(ArrayList quadList) {
	Msg.debug("quadList: (size== "+quadList.size()+")");
	printElements(quadList);
    }

    public static void printEdgeList(ArrayList edgeList) {
	if (Msg.debugMode) {
	    for (int i= 0; i< edgeList.size(); i++) {
		Edge edge = (Edge)edgeList.get(i);
		edge.printMe();
	    }
	}
    }
    
    public static void printNodes(ArrayList nodeList) {
	if (Msg.debugMode) {
	    Msg.debug("nodeList:");
	    for (int i= 0; i< nodeList.size(); i++) {
		Node node = (Node)nodeList.get(i);
		node.printMe();
	    }
	}
    }


    /** */
    public static void printValences() {
	Node n;
	for (int i= 0; i< nodeList.size(); i++) {
	    n= (Node)nodeList.get(i);
	    Msg.debug("Node "+n.descr()+" has valence "+n.valence());
	}
    }

    /** */
    public static void printValPatterns() {
	Node n;
	Node [] neighbors;
	for (int i= 0; i< nodeList.size(); i++) {
	    n= (Node)nodeList.get(i);
	    if (!n.boundaryNode()) {
		neighbors= n.ccwSortedNeighbors();
		n.createValencePattern(neighbors);
		Msg.debug("Node "+n.descr()+" has valence pattern "+n.valDescr());
	    }
	}
    }

    /** */
    public static void printAnglesAtSurrondingNodes() {
	Node n;
	Node [] neighbors;
	double [] angles;
	for (int i= 0; i< nodeList.size(); i++) {
	    n= (Node)nodeList.get(i);
	    if (!n.boundaryNode()) {
		neighbors= n.ccwSortedNeighbors();
		n.createValencePattern(neighbors);
		angles= n.surroundingAngles(neighbors, n.pattern[0]-2);
		
		Msg.debug("Angles at the nodes surrounding node "+n.descr()+":");
		for (int j= 0; j< n.pattern[0]-2; j++) 
		    Msg.debug("angles["+j+"]== "+Math.toDegrees(angles[j])+
			      " (in degrees)");
	    }
	}
    }

    /** Do inversion test and repair inversion if requiered
     * @return true if any repairing was neccessary, else return false.
     */
    public static boolean inversionCheckAndRepair(Node newN, Node oldPos) {
	Msg.debug("Entering inversionCheckAndRepair(..), node oldPos: "+oldPos.descr());
	Element elem= null;
	ArrayList elements= newN.adjElements();
	if (newN.invertedOrZeroAreaElements(elements)) {
	    if (!newN.incrAdjustUntilNotInvertedOrZeroArea(oldPos, elements)) {
		
		for (int i= 0; i< elements.size(); i++) {
		    elem= (Element)elements.get(i);
		    if (elem.invertedOrZeroArea())
			break;
		} 
		
		Msg.error("It seems that an element was inverted initially: "
			  +elem.descr());
		return false;
	    }
	    Msg.debug("Leaving inversionCheckAndRepair(..)");
	    return true;
	}
	else {
	    Msg.debug("Leaving inversionCheckAndRepair(..)");
	    return false;
	}
    }

    /** Quad q is to be collapsed. Nodes n1 and n2 are two opposite nodes in q.
     * This method tries to find a location inside the current q to which n1 and n2 can 
     * safely be relocated and joined without causing any adjacent elements to become 
     * inverted. The first candidate location is the centroid of the quad. If that 
     * location is not suitable, the method tries locations on the vectors from the
     * centroid towards n1 and from the centroid towards n2. The first suitable location
     * found is returned.
     * @param q the quad to be collapsed
     * @param n1 the node in quad q that is to be joined with opposite node n2 
     * @param n2 the node in quad q that is to be joined with opposite node n1 
     * @return a position inside quad q to which both n1 and n2 can be relocated without
     * inverting any of their adjacent elements. */
    public static Node safeNewPosWhenCollapsingQuad(Quad q, Node n1, Node n2) {
	Msg.debug("Entering safeNewPosWhenCollapsingQuad(..)");

	Node n= q.centroid();
	MyVector back2n1= new MyVector(n, n1), back2n2= new MyVector(n, n2);
	double startX= n.x, startY= n.y;
	double xstepn1= back2n1.x/50.0, ystepn1= back2n1.y/50.0, 
	    xstepn2=back2n2.x/50.0, ystepn2= back2n2.y/50.0; 
	double xincn1, yincn1, xincn2, yincn2;
	int steps2n1, steps2n2, i;
	ArrayList l1= n1.adjElements(), l2= n2.adjElements();

	if (!q.anyInvertedElementsWhenCollapsed(n, n1, n2, l1, l2)) {
	    Msg.debug("Leaving safeNewPosWhenCollapsingQuad(..): found");
	    return n;
	}

	// Calculate the parameters for direction n to n1
	if (Math.abs(xstepn1)< COINCTOL || Math.abs(ystepn1)< COINCTOL) {
	    Msg.debug("...ok, resorting to use of minimum increment");
	    if (Math.abs(back2n1.x) < Math.abs(back2n1.y)) {
		if (back2n1.x< 0)
		    xstepn1= -COINCTOL;
		else
		    xstepn1= COINCTOL;

		// abs(ystepn1/xstepn1) = abs(n1.y/n1.x) 
		ystepn1= Math.abs(n1.y)* COINCTOL/Math.abs(n1.x);
		if (back2n1.y< 0)
		    ystepn1= -ystepn1;
		
		steps2n1= (int)(back2n1.x/xstepn1);
	    }
	    else {
		if (back2n1.y< 0)
		    ystepn1= -COINCTOL;
		else
		    ystepn1= COINCTOL;
		
		// abs(xstepn1/ystepn1) = abs(n1.x/n1.y) 
		xstepn1= Math.abs(n1.x)* COINCTOL/Math.abs(n1.y);
		if (back2n1.x< 0)
		    xstepn1= -xstepn1;
		
		steps2n1= (int)(back2n1.y/ystepn1);
	    }
	}
	else {
	    xstepn1= back2n1.x/50.0;
	    ystepn1= back2n1.x/50.0;
	    steps2n1= 50;
	}

	// Calculate the parameters for direction n to n2
	if (Math.abs(xstepn2)< COINCTOL || Math.abs(ystepn2)< COINCTOL) {
	    Msg.debug("...ok, resorting to use of minimum increment");
	    if (Math.abs(back2n2.x) < Math.abs(back2n2.y)) {
		if (back2n2.x< 0)
		    xstepn2= -COINCTOL;
		else
		    xstepn2= COINCTOL;
		
		// abs(ystepn2/xstepn2) = abs(n2.y/n2.x) 
		ystepn2= Math.abs(n2.y)* COINCTOL/Math.abs(n2.x);
		if (back2n2.y< 0)
		    ystepn2= -ystepn2;
		
		steps2n2= (int)(back2n2.x/xstepn2);
	    }
	    else {
		if (back2n2.y< 0)
		    ystepn2= -COINCTOL;
		else
		    ystepn2= COINCTOL;
		
		// abs(xstepn2/ystepn2) = abs(n2.x/n2.y) 
		xstepn2= Math.abs(n2.x)* COINCTOL/Math.abs(n2.y);
		if (back2n2.x< 0)
		    xstepn2= -xstepn2;
		
		steps2n2= (int)(back2n2.y/ystepn2);
	    }
	}
	else {
	    xstepn2= back2n2.x/50.0;
	    ystepn2= back2n2.x/50.0;
	    steps2n2= 50;
	}

	Msg.debug("...back2n1.x is: "+back2n1.x);
	Msg.debug("...back2n1.y is: "+back2n1.y);
	Msg.debug("...xstepn1 is: " + xstepn1);
	Msg.debug("...ystepn1 is: " + ystepn1);

	Msg.debug("...back2n2.x is: "+back2n2.x);
	Msg.debug("...back2n2.y is: "+back2n2.y);
	Msg.debug("...xstepn2 is: " + xstepn2);
	Msg.debug("...ystepn2 is: " + ystepn2);
	
	// Try to find a location
	for (i=1; i<= steps2n1 || i<= steps2n2; i++) {
	    if (i<= steps2n1) {
		n.x= startX + xstepn1*i;
		n.y= startY + ystepn1*i;
		if (!q.anyInvertedElementsWhenCollapsed(n, n1, n2, l1, l2)) {
		    Msg.debug("Leaving safeNewPosWhenCollapsingQuad(..): found");
		    return n;
		}
	    }
	    if (i<= steps2n2) {
		n.x= startX + xstepn2*i;
		n.y= startY + ystepn2*i;
		if (!q.anyInvertedElementsWhenCollapsed(n, n1, n2, l1, l2)) {
		    Msg.debug("Leaving safeNewPosWhenCollapsingQuad(..): found");
		    return n;
		}
	    }
	}
    
	Msg.debug("Leaving safeNewPosWhenCollapsingQuad(..): not found");
	return null;
    }

    /** To be used only with all-triangle meshes. 
     * @return true if any zero area triangles were removed. */
    boolean repairZeroAreaTriangles() {
	Msg.debug("Entering GeomBasics.repairZeroAreaTriangles()");
	boolean res= false;
	int j;
	Triangle t, old1, old2;
	Edge e, eS, e1, e2;

	for (int i= 0; i< triangleList.size(); i++) {
	    if (!(triangleList.get(i) instanceof Triangle))
		continue;
	    t= (Triangle)triangleList.get(i);
	    if (t.zeroArea()) {
		e= t.longestEdge();
		e1= t.otherEdge(e);
		e2= t.otherEdge(e, e1);
		res= true; 
		
		Msg.debug("...longest edge is "+e.descr());
		if (!e.boundaryEdge()) {
		    Msg.debug("...longest edge not on boundary!");
		    old1= (Triangle)e.element1;
		    old2= (Triangle)e.element2;
		    eS= e.getSwappedEdge();
		    e.swapToAndSetElementsFor(eS);
		    
		    triangleList.set(triangleList.indexOf(old1), null);
		    triangleList.set(triangleList.indexOf(old2), null);
		    
		    triangleList.add(eS.element1);
		    triangleList.add(eS.element2);
		    
		    edgeList.remove(edgeList.indexOf(e));
		    edgeList.add(eS);
		}
		else {
		    // The zero area triangle has its longest edge on the boundary...
		    // Then we can just remove the triangle and the long edge!
		    // Note that we now get a new boundary node...  
		    Msg.debug("...longest edge is on boundary!");
		    triangleList.set(triangleList.indexOf(t), null);
		    t.disconnectEdges();
		    edgeList.remove(edgeList.indexOf(e));
		    e.disconnectNodes();
		}

	    }
	}

	// Remove those entries that were set to null above.
	int i= 0;
	do {
	    t= (Triangle)triangleList.get(i);
	    if (t== null)
		triangleList.remove(i);
	    else
		i++;
	}
	while (i < triangleList.size());

	Msg.debug("Leaving GeomBasics.repairZeroAreaTriangles()");
	return res;
    }

    /** A method for fast computation of the cross product of two vectors.
     * @param o1 origin of first vector 
     * @param p1 endpoint of first vector 
     * @param o2 origin of second vector 
     * @param p2 endpoint of second vector 
     * @return the cross product of the two vectors
     */
    public static double cross(Node o1, Node p1, Node o2, Node p2) {
	double x1= p1.x-o1.x;
	double x2= p2.x-o2.x;
	double y1= p1.y-o1.y;	
	double y2= p2.y-o2.y;
	return x1*y2 - x2*y1;
    }
} // End of class GeomBasics

