package meshditor;

import java.util.ArrayList;
import java.lang.Math;

// ==== ---- ==== ---- ==== ---- ==== ---- ==== ---- ==== ---- ==== ----
/**
 * This class constitutes a simple implementation of the cleanup
 * process as outlined by Paul Kinney: "CleanUp: Improving Quadrilateral
 * Finite Element Meshes" (1997). Please note that this is not a complete
 * and accurate implementation, as it had to be somewhat adapted to work
 * with the Q-Morph implementation. E.g. the size cleanup is not implemented.
 * Neither is cleaning up bowties and the mesh topology inspection.
 * Furthermore, the number of cleanup patterns is limited to those described in
 * Kinney's paper.<br>
 * <br>
 * TODO<br>
 * Draw all cases and test them.
 * The ones running correctly:
 *   Connectivity
 *    - stdCase1a
 *    - stdCase1b
 *    - stdCase2a
 *    - stdCase2b
 *    - stdCase3a
 *    - stdCase3b
 *    - case1a og case1b
 *    - case2
 *    - case3
 *    - case4
 *    - case5
 *
 *   Boundary:
 *    - diamond
 *    - case1
 *    - case2
 *    - case3
 *    - case4
 *    - boundary angle > 150 degrees with one & two row transition
 *
 *   Shape:
 *    - case1
 *    - case2
 *
 * TODO: All good, but:
 * - Why do I now see so little effect from class GlobalSmooth???
 *
 * @author Karl Erik Levik
 *
 */
// ==== ---- ==== ---- ==== ---- ==== ---- ==== ---- ==== ---- ==== ----

public class TopoCleanup extends GeomBasics {

    public TopoCleanup() {
    }

    /** A dart used when traversing the mesh in cleanup operations.*/
    private Dart d;

    /** Initialize the object */
    public void init() {
	Quad q;
	Triangle tri;

	setCurMethod(this);

	elimChevsFinished= false;
	connCleanupFinished= false;
	boundaryCleanupFinished= false;
	shapeCleanupFinished= false;

	bcaseTriQFin= false;
	bcaseValPat1Fin= false;
	bcaseValPat2Fin= false;
	bcaseValPat3Fin= false;
	bcaseValPat4Fin= false;
	bcaseDiamondFin= false;

	shape1stTypeFin= false;
	passNum= 0;
	count= 0;

	// Ok... Remove fake quads and replace with triangles:
	for (int i= 0; i< elementList.size(); i++) {
	    if (elementList.get(i) instanceof Quad) {
		q= (Quad) elementList.get(i);
		if (q.isFake) {
		    tri= new Triangle(q.edgeList[base], q.edgeList[left],
				      q.edgeList[right]);
		    elementList.set(i, tri);
		    q.disconnectEdges();
		    tri.connectEdges();
		}
	    }
	}

	deleteList= new ArrayList();
	nodes= (ArrayList)nodeList.clone();
	d= new Dart();
    }

    /** Main loop for global topological clean-up */
    public void run() {
	Msg.debug("Entering TopoCleanup.run()");

	int i, j;
	Quad q;
	Triangle tri;


	if (!step) {
	    // Initial pass to eliminate chevrons
	    while (!elimChevsFinished)
		elimChevsStep();


	    // Then the major cleanup processes:
	    for (j= 0; j< 3; j++) {

		// Perform connectivity cleanup:
		// Parse the list of nodes looking for cases that match. Fix these.
		connCleanupFinished= false;
		while (!connCleanupFinished)
		    connCleanupStep();
		// Run some kind of global smooth
		globalSmooth();

		// Boundary cleanup:
		// Parse the list of elements looking for cases that match. Fix these.
		boundaryCleanupFinished= false;
		bcaseValPat1Fin= false;
		bcaseValPat2Fin= false;
		bcaseValPat3Fin= false;
		bcaseValPat4Fin= false;
		bcaseTriQFin= false;
		bcaseDiamondFin= false;
		while (!boundaryCleanupFinished)
		    boundaryCleanupStep();
		// Run some kind of global smooth
		globalSmooth();

		// Shape cleanup:
		// Parse the list of elements looking for cases that match. Fix these.
		// Run a local smooth after each action.
		shapeCleanupFinished= false;
		shape1stTypeFin= false;
		while (!shapeCleanupFinished)
		    shapeCleanupStep();

		// Run some kind of global smooth
		globalSmooth();
	    }
	}

	if (doSmooth)
	    setCurMethod(globalSmooth);
	else
	    setCurMethod(null);
	Msg.debug("Leaving TopoCleanup.run()");
    }

    private boolean elimChevsFinished= false;
    private boolean connCleanupFinished= false;
    private boolean boundaryCleanupFinished= false;
    private boolean shapeCleanupFinished= false;

    private int passNum= 0;

    /** Method for stepping through the implementation one step at the time. */
    public void step() {
	Msg.debug("Entering TopoCleanup.step()");
	Element elem;
	int i, j;

	if (!elimChevsFinished) {
	    elimChevsStep();
	}
	else if (!connCleanupFinished) {
	    connCleanupStep();
	    if (connCleanupFinished)
		globalSmooth();
	}
	else if (!boundaryCleanupFinished) {
	    boundaryCleanupStep();
	    if (boundaryCleanupFinished)
		globalSmooth();
	}
	else if (!shapeCleanupFinished) {
	    shapeCleanupStep();
	    if (shapeCleanupFinished)
		globalSmooth();
	}
	else {
	    passNum++;
	    if (passNum== 3) {
		passNum= 0;
		if (doSmooth)
		    setCurMethod(globalSmooth);
		else
		    setCurMethod(null);
	    }
	    else {
		connCleanupFinished= false;
		boundaryCleanupFinished= false;
		bcaseValPat1Fin= false;
		bcaseValPat2Fin= false;
		bcaseValPat3Fin= false;
		bcaseValPat4Fin= false;
		bcaseTriQFin= false;
		bcaseDiamondFin= false;
		shapeCleanupFinished= false;
		shape1stTypeFin= false;
   	    }
	}
	Msg.debug("Leaving TopoCleanup.step()");
    }

    int count= 0;
    ArrayList deleteList;
    ArrayList nodes;

    /** Initial pass to detect and cleanup chevrons.*/
    private void elimChevsStep() {
	Msg.debug("Entering TopoCleanup.elimChevsStep()");

	Element elem;
	Quad q= null;
	int i, j;

	while (count< elementList.size()) {
	    elem= (Element) elementList.get(count);

	    if (elem== null || !(elem instanceof Quad))
		count++;
	    else {
		Msg.debug("...testing element "+elem.descr());

		q= (Quad)elem;
		if (q.isFake)
		    Msg.error("...Fake quad encountered!!!");

		if (q.isChevron()) {
		    eliminateChevron(q);
		    count++;
		    Msg.debug("Leaving TopoCleanup.elimChevsStep()");
		    return;
		}
		else
		    count++;
	    }
	}


	for (i= 0; i < deleteList.size(); i++) {
	    elem= (Element) deleteList.get(i);
	    elementList.remove(elementList.indexOf(elem));
	}
	deleteList.clear();

	elimChevsFinished= true;
	count= 0;
	Msg.debug("Leaving TopoCleanup.elimChevsStep(), all elements ok.");
    }

    /** The chevron is deleted along with one of its neighbors. A new node is created
     * and the deleted quads are replaced with three new ones surrounding the new
     * node. The neighbor chosen for deletion is the one that, when replaced by the
     * new quads, yields the optimal node valences.
     * @param q the chevron to be eliminated
     */
    private void eliminateChevron(Quad q) {
	Msg.debug("Entering eliminateChevron(..)");
	Msg.debug("...q== "+q.descr());

	Element neighbor1, neighbor2, neighbor3, neighbor4;
	Quad q1, q2, qn1, qn2, qn3;
	Triangle tri;
	Edge e1, e2, e3, e4;
	Node n, n1, n2, n3;

	int i, j;
	int [] valenceAlt1= new int[6];
	int [] valenceAlt2= new int[6];
	int irrAlt1= 0, irrAlt2= 0, badAlt1= 0, badAlt2= 0;

	n= q.nodeAtLargestAngle();
	Msg.debug("...n== "+n.descr());
	Msg.debug("...size of angle at node n is: "+Math.toDegrees(q.ang[q.angleIndex(n)])
		  +" degrees.");
	e3= q.neighborEdge(n);
	n3= e3.otherNode(n);
	e4= q.neighborEdge(n, e3);
	n1= e4.otherNode(n);

	e2= q.neighborEdge(n3, e3);
	n2= e2.otherNode(n3);
	e1= q.neighborEdge(n2, e2);


	neighbor1= q.neighbor(e1);
	neighbor2= q.neighbor(e2);

	if (!n.boundaryNode()) {
	    // Then the node can be relocated so that the element is no longer a chevron
	    Msg.debug("...trying to resolve chevron by smoothing...");
	    Node nOld= new Node(n.x, n.y), nNew= n.laplacianSmooth();

	    if (!n.equals(nNew)) {
		n.moveTo(nNew);
		inversionCheckAndRepair(n, nOld);
		if (!q.isChevron()) {
		    n.update();
		    Msg.debug("...success! Chevron resolved by smoothing!!!!");
		    return;
		}
		else {
		    n.setXY(nOld.x, nOld.y);
		    n.update();
		    Msg.debug("...unsuccessful! Chevron not resolved by smoothing!");
		}
	    }
	}

	if (neighbor1!= null && neighbor1 instanceof Quad &&
	    !((Quad)neighbor1).largestAngleGT180() )
	    q1= (Quad) neighbor1;
	else
	    q1= null;
	if (neighbor2!= null && neighbor2 instanceof Quad &&
	    !((Quad)neighbor2).largestAngleGT180() )
	    q2= (Quad) neighbor2;
	else
	    q2= null;

	if (q1!= null && q2!= null) {
	    Msg.debug("...n - node at largest angle of quad "+q.descr()+
		      " is: "+n.descr());


	    if (q.ang[q.angleIndex(n1)] + q1.ang[q1.angleIndex(n1)] < DEG_180) {
		valenceAlt1[0]= n.valence() + 1;
		valenceAlt1[1]= n1.valence() - 1;
		valenceAlt1[2]= n2.valence();
		valenceAlt1[3]= n3.valence();
		valenceAlt1[4]= q1.oppositeNode(n2).valence() + 1;
		valenceAlt1[5]= q1.oppositeNode(n1).valence();

		irrAlt1= 1; // the new central node will have valence 3
	    }
	    else { // then we must consider a fill_4(q, e1, n1):
		valenceAlt1[0]= n.valence() + 1;
		valenceAlt1[1]= n1.valence();
		valenceAlt1[2]= n2.valence();
		valenceAlt1[3]= n3.valence();
		valenceAlt1[4]= q1.oppositeNode(n2).valence();
		valenceAlt1[5]= q1.oppositeNode(n1).valence() + 1;

		irrAlt1= 2; // the two new central nodes will each have valence 3
	    }

	    if (q.ang[q.angleIndex(n3)] + q2.ang[q2.angleIndex(n3)] < DEG_180) {
		valenceAlt2[0]= n.valence() + 1;
		valenceAlt2[1]= n1.valence();
		valenceAlt2[2]= n2.valence();
		valenceAlt2[3]= n3.valence() - 1;
		valenceAlt2[4]= q2.oppositeNode(n2).valence() + 1;
		valenceAlt2[5]= q2.oppositeNode(n3).valence();

		irrAlt2= 1; // the new central node will have valence 3
	    }
	    else { // then we must consider a fill_4(q, e2, n3):
		valenceAlt2[0]= n.valence() + 1;
		valenceAlt2[1]= n1.valence();
		valenceAlt2[2]= n2.valence();
		valenceAlt2[3]= n3.valence();
		valenceAlt2[4]= q2.oppositeNode(n2).valence();
		valenceAlt2[5]= q2.oppositeNode(n3).valence() + 1;

		irrAlt2= 2; // the two new central nodes will each have valence 3
	    }

	    for (j= 0; j<= 5; j++) {
		if (valenceAlt1[j]!=4)
		    irrAlt1++;
		if (valenceAlt1[j]<3 || valenceAlt1[j]> 5)
		    badAlt1++;

		if (valenceAlt2[j]!=4)
		    irrAlt2++;
		if (valenceAlt2[j]<3 || valenceAlt2[j]> 5)
		    badAlt2++;
	    }
	}

	if ((q1!= null && q2== null) ||
	    (q1!= null && q2!= null && (badAlt1 < badAlt2 ||
					(badAlt1== badAlt2 &&
					 irrAlt1 <= irrAlt2)))) {

	    Msg.debug("...alt1 preferred, q1: "+q1.descr());
	    deleteList.add(null);
	    elementList.set(elementList.indexOf(q), null);
	    deleteList.add(null);
	    elementList.set(elementList.indexOf(q1), null);

	    if (q.ang[q.angleIndex(n1)] + q1.ang[q1.angleIndex(n1)] < DEG_180)
		fill3(q, e1, n2, true);
	    else
		fill4(q, e1, n2); //n1


	}
	else if (q2!= null) {
	    Msg.debug("...alt2 preferred, q2: "+q2.descr());
	    deleteList.add(null);     // q don't need any special treatment;
	    elementList.set(elementList.indexOf(q), null);
	    deleteList.add(null);  // but q2 does, because it migth be at a later pos
	    elementList.set(elementList.indexOf(q2), null);

	    if (q.ang[q.angleIndex(n3)] + q2.ang[q2.angleIndex(n3)] < DEG_180)
		fill3(q, e2, n2, true);
	    else
		fill4(q, e2, n2); //n3
	}
	else {
	    Msg.debug("...Both q1 and q2 are null");
	}

	Msg.debug("Leaving eliminateChevron(..)");
    }

    /** Combine with neighbor and fill with "fill_3" as defined in the paper by P.Kinney
     * Note that the method doesn't remove q and its neighbor from elementList.
     * @param q  the first quad
     * @param e  the edge which is adjacent to both q and its neighbor
     * @param n  a node belonging to q, e, and one of the three new edges to be created
     * @param safe boolean indicating whether a safe pos must be attempted for the
     * new node
     */
    private Dart fill3(Quad q, Edge e, Node n, boolean safe) {
    	Msg.debug("Entering TopoCleanup.fill3(..)");
	Msg.debug("...q= "+q.descr()+", e= "+e.descr()+", n= "+n.descr());

	Quad qn= (Quad)q.neighbor(e);
	Quad qn1, qn2, qn3;
	Edge b, l, r, t, ea, eb, ec;
	Node nOpp= q.oppositeNode(n);
	Edge e2= q.neighborEdge(n, e);
	Node eother= e.otherNode(n), e2other= e2.otherNode(n);
	Dart d= new Dart();

	Node newNode;
	if (safe)
	    newNode= e.midPoint();
	else
	    newNode= q.centroid();

	newNode.color= java.awt.Color.red; // creation in tCleanup
	ea= new Edge(nOpp, newNode);
	eb= new Edge(n, newNode);
	ec= new Edge(newNode, qn.oppositeNode(n));

	// Some minor updating...
	q.disconnectEdges();
	qn.disconnectEdges();

	ea.connectNodes();
	eb.connectNodes();
	ec.connectNodes();

	b= q.neighborEdge(e2other, e2);
	if (nOpp== b.rightNode) {
	    l= q.neighborEdge(b.leftNode, b);
	    r= ea;
	}
	else {
	    l= ea;
	    r= q.neighborEdge(b.rightNode, b);
	}
	t= eb;
	qn1= new Quad(b, l, r, t); // 1st replacement quad

	b= eb;
	if (ec.hasNode(b.rightNode)) {  //b.leftNode== n
	    l= qn.neighborEdge(n, e);
	    r= ec;
	}
	else {
	    r= qn.neighborEdge(n, e);
	    l= ec;
	}
	t= qn.oppositeEdge(e);
	qn2= new Quad(b, l, r, t); // 2nd replacement quad

	b= q.neighborEdge(eother, e);
	if (b.leftNode== nOpp) {
	    l= ea;
	    r= qn.neighborEdge(eother, e);
	}
	else {
	    r= ea;
	    l= qn.neighborEdge(eother, e);
	}
	t= ec;
	qn3= new Quad(b, l, r, t); // 3rd replacement quad

	// remember to update the lists (nodeList, edgeList,
	// elementList, the nodes' edgeLists, ...
	qn1.connectEdges();
	qn2.connectEdges();
	qn3.connectEdges();

	elementList.add(qn1);
	elementList.add(qn2);
	elementList.add(qn3);

	e.disconnectNodes();
	edgeList.remove(edgeList.indexOf(e));

	edgeList.add(ea);
	edgeList.add(eb);
	edgeList.add(ec);

	nodeList.add(newNode);
	nodes.add(newNode);

	Msg.debug("...qn1: "+qn1.descr());
	Msg.debug("...qn2: "+qn2.descr());
	Msg.debug("...qn3: "+qn3.descr());

	// Try smoothing the pos of newNode:
	Node nOld= new Node(newNode.x, newNode.y), smoothed= newNode.laplacianSmooth();
	if (!newNode.equals(smoothed)) {
	    newNode.moveTo(smoothed);
	    inversionCheckAndRepair(newNode, nOld);
	    newNode.update();
	}

	d.elem= qn1;
	d.e= eb;
	d.n= n;

    	Msg.debug("Leaving TopoCleanup.fill3(..)");
	return d;
    }

    /** Combine with neighbor and fill with "fill_4" as defined in paper by P.Kinney
     * Note that the method doesn't remove q and its neighbor from elementList.
     * @param q the first of the two quads to be combined
     * @param e the common edge of q and the second quad
     * @param n2 one of the nodes of edge e and whose opposite node in q will not
     * get connected to any new edge.
     */
    private Dart fill4(Quad q, Edge e, Node n2) {
    	Msg.debug("Entering TopoCleanup.fill4(..)");
	Msg.debug("...q= "+q.descr()+", e= "+e.descr()+", n= "+n2.descr());

	Dart d= new Dart();
	Quad qn= (Quad)q.neighbor(e);
	// First get the nodes and edges in the two quads
	Edge temp;
	Node n5= e.otherNode(n2);

	Edge e1= q.neighborEdge(n5, e);
	Node n0= e1.otherNode(n5);
	Edge e2= q.neighborEdge(n0, e1);
	Node n1= e2.otherNode(n0);

	Edge e3= q.neighborEdge(n1, e2);
	Edge e4= qn.neighborEdge(n2, e);
	Node n3= e4.otherNode(n2);
	Edge e5= qn.neighborEdge(n3, e4);
	Edge e6= qn.neighborEdge(n5, e);
	Node n4= e6.otherNode(n5);

	// Create new nodes and edges
	Node n6= e.midPoint();
	// This is new... hope it works...
	Node n7= qn.centroid(); //new Node((n5.x + n3.x)*0.5, (n5.y + n3.y)*0.5);

	n6.color= java.awt.Color.red; // creation in tCleanup
	n7.color= java.awt.Color.red; // creation in tCleanup

	Edge eNew1= new Edge(n0, n6);
	Edge eNew2= new Edge(n2, n6);
	Edge eNew3= new Edge(n6, n7);
	Edge eNew4= new Edge(n5, n7);
	Edge eNew5= new Edge(n3, n7);

	eNew1.connectNodes();
	eNew2.connectNodes();
	eNew3.connectNodes();
	eNew4.connectNodes();
	eNew5.connectNodes();

	// Create the new quads
	Edge l,r;
	if (eNew1.leftNode== n0) {
	    l= e2;
	    r= eNew2;
	}
	else {
	    r= e2;
	    l= eNew2;
	}
	Quad qNew1= new Quad(eNew1, l, r, e3);

	if (e1.leftNode== n5) {
	    l= eNew4;
	    r= eNew1;
	}
	else {
	    r= eNew4;
	    l= eNew1;
	}
	Quad qNew2= new Quad(e1, l, r, eNew3);

	if (e4.leftNode== n3) {
	    l= eNew5;
	    r= eNew2;
	}
	else {
	    r= eNew5;
	    l= eNew2;
	}
	Quad qNew3= new Quad(e4, l, r, eNew3);

	if (e6.leftNode== n4) {
	    l= e5;
	    r= eNew4;
	}
	else {
	    r= e5;
	    l= eNew4;
	}
	Quad qNew4= new Quad(e6, l, r, eNew5);

	// Update lists etc.
	e.disconnectNodes();
	q.disconnectEdges();
	qn.disconnectEdges();

	qNew1.connectEdges();
	qNew2.connectEdges();
	qNew3.connectEdges();
	qNew4.connectEdges();

	edgeList.remove(edgeList.indexOf(e));
	edgeList.add(eNew1);
	edgeList.add(eNew2);
	edgeList.add(eNew3);
	edgeList.add(eNew4);
	edgeList.add(eNew5);

	elementList.add(qNew1);
	elementList.add(qNew2);
	elementList.add(qNew3);
	elementList.add(qNew4);

	nodeList.add(n6);
	nodeList.add(n7);
	nodes.add(n6);
	nodes.add(n7);

	Node nOld= new Node(n6.x, n6.y), nNew= n6.laplacianSmooth();
	if (!n6.equals(nNew)) {
	    n6.moveTo(nNew);
	    inversionCheckAndRepair(n6, nOld);
	    n6.update();
	}

	nOld= new Node(n7.x, n7.y);
	nNew= n7.laplacianSmooth();
	if (!n7.equals(nNew)) {
	    n7.moveTo(nNew);
	    inversionCheckAndRepair(n7, nOld);
	    n7.update();
	}
	d.elem= qNew1;
	d.e= eNew2;
	d.n= n2;

	Msg.debug("Leaving TopoCleanup.fill4(..)");
	return d;
    }



    /** Replace the specified surrounding mesh elements (quads) with some other
     * specified quads. This is accomplished by applying a composition of alpha
     * iterators and mesh modify codes.
     *
     * @param startDart the dart where the composition is to start.
     * @param fillPat a composition of alpha iterators and mesh modification codes
     * that moves around on the existing mesh and modifies it according to the codes
     * in the composition.
     * The format of the byte array is:<br>
     * [Total number of bytes in array], [number of quads to be created],
     * [number of quads to be deleted], [iterators and modify codes].
     * In addition to the normal alpha iterator codes 0,1,2, we have mesh modify codes:
     <ul>
     <li>Code 0 for alpha iterator 0
     <li>Code 1 for alpha iterator 1
     <li>Code 2 for alpha iterator 2
     <li>Code 3 for closing the current quad, new pos of cur. node at the opposite node
     <li>Code 4 for closing the current quad, new pos of cur. node midway to oppos. node
     <li>Code 5 for filling cur. quad and neighbour with fill_3
     <li>Code 6 for filling cur. quad and neighbour with fill_4
     <li>Code 7 for splitting cur. quad into two new quads along diagonal from cur. node
     <li>Code 8 for switching cur. edge clockwise
     <li>Code 9 for switching cur. edge counter-clockwise
     */
    private void applyComposition(Dart startDart, byte[] fillPat) {
	Msg.debug("Entering applyComposition(..)");
	byte a;
	int qaIndex, qbIndex;
	d= startDart;
	Msg.debug("d== "+startDart.descr());

	for (int i= 1; i< fillPat[0]; i++) {
	    a= fillPat[i];

	    Msg.debug("a: "+a);

	    // Alpha iterators:
	    if (a==0)      // alpha 0
		d.n= d.e.otherNode(d.n);
	    else if (a==1) // alpha 1
		d.e= d.elem.neighborEdge(d.n, d.e);
	    else if (a==2) // alpha 2
		d.elem= d.elem.neighbor(d.e);

	    // Mesh modification codes:
	    else if (a==3)
		d= closeQuad((Quad)d.elem, d.e, d.n, false);
	    else if (a==4)
		d= closeQuad((Quad)d.elem, d.e, d.n, true);
	    else if (a==5) {
		qaIndex= elementList.indexOf(d.elem);
		elementList.remove(qaIndex);
		qbIndex= elementList.indexOf(d.elem.neighbor(d.e));
		elementList.remove(qbIndex);
		d= fill3((Quad)d.elem, d.e, d.n, true);
	    }
	    else if (a==6) {
		qaIndex= elementList.indexOf(d.elem);
		elementList.remove(qaIndex);
		qbIndex= elementList.indexOf(d.elem.neighbor(d.e));
		elementList.remove(qbIndex);
		d= fill4((Quad)d.elem, d.e, d.n);
	    }
	    else if (a==7)
		d= openQuad((Quad)d.elem, d.e, d.n);
	    else if (a==8)
		d= switchDiagonalCW((Quad)d.elem, d.e, d.n);
	    else if (a==9)
		d= switchDiagonalCCW((Quad)d.elem, d.e, d.n);
	    else
		Msg.error("Illegal mesh modification code: "+a);

	    if (d==null) {
		Msg.debug("Leaving applyComposition(..), unsuccessful!");
		return;
	    }

	    if (d.elem== null)
		Msg.error("d.elem== null");
	    if (d.e== null)
		Msg.error("d.e== null");
	    if (d.n== null)
		Msg.error("d.n== null");

	    Msg.debug("d== "+d.descr());
	}
	Msg.debug("Leaving applyComposition(..)");
    }


    /* The different cases: Their valence patterns, vertex patterns, boundary patterns
     * and compositions of mesh modification codes.
     *
     * In the valence patterns, the following codes apply:
     * 4- (code 14) means a valence of 4 or less
     * 4+ (code 24) means a valence of 4 or more
     * 5 means a valence of 5 or more
     * 0 means the valence is ignored and unchanged and is usually drawn as valence 4
     * 1st value in pattern is total length of pattern
     * 2nd value is the valence of the central node
     * The rest of the valences then follow in ccw order around the central node
     */

    /* The connectivity cases */
    static final byte [] stdCase1= {12, 5, 24,3,24,3,4,0,4,0,4,3};
    static final boolean [] stdVertexCase1=
    {true, false, true, false, false, true, false, true, false, false}; // ok...
    static final byte [] stdComp1= {4,
				    5,1,9};
    static final byte [] stdCase2a= {14, 6, 14,24,4,3,4,24,14,3,24,3,24,3};
    static final boolean [] stdVertexCase2a=
    {false, true, false, false, false, true, false, false, true, false,
     true, false}; // ok...
    static final byte [] stdComp2a= {22,
				    2,1,5,2,1,8,2,1,0,2,1,0,5,0,2,1,2,0,1,9,5};
    static final byte [] stdCase2b= {14, 6, 24,3,4,3,24,3,4,0,4,0,4,3};
    static final boolean [] stdVertexCase2b=
    {true, false, false, false, true, false, false, true, false, true,
     false, false}; // ok...
    static final byte [] stdComp2b= {9,
				     5,1,2,1,5,1,0,5};
    static final byte [] stdCase3a= {12, 5, 24,3,4,0,4,0,4,0,4,3};
    static final boolean [] stdVertexCase3a=
    {true, false, false, true, false, true, false, true,
     false, false}; // ok...
    static final byte [] stdComp3a= {2,
				     5};
    static final byte [] stdCase3b= {12, 5, 24,0,4,3,24,3,24,3,24,3};
    static final boolean [] stdVertexCase3b=
    {false, true, false, false, true, false, true, false,
     true, false}; // ok...
    static final byte [] stdComp3b= {18,
				     1,2,1,0,8,5,0,1,2,0,1,9,2,1,0,1,9};
    static final byte [] case1a= {12,5, 3,4,4,3,0,0, 0,0, 0,0}; // mirror of case1b
    static final byte [] comp1a= {3,
				  1,9};
    static final byte [] case1b= {12,5, 4,4,3,0,0,0, 0,0, 0,3}; // mirror of case1a
    static final byte [] comp1b= {2,
				  8};
    static final byte [] case2= {12,5, 3,4,24,4,3,0, 0,0, 0,0};
    static final byte [] comp2 = {4,
				  1,0,5};
    static final byte [] case3= {8,3, 24, 3, 24, 0, 0, 0};
    static final boolean [] vertexCase3= {false, false, false, true, false, true};
    static final boolean [] ipat3= {true,true,true,false,false,false};
    static final byte [] comp3 = {2,
				  4};
    static final byte [] case4= {8,3, 3,5,4,5,4,4};
    static final boolean [] ipat4= {true,false,false,false,false,false};
    static final byte [] comp4 = {5,  // Consider 4 instead of 3... nah, 3 is best here
				  0,3,0,3};
    static final byte [] case5= {10,4, 3,4,4,3, 4,4,4,5};
    static final boolean [] ipat5= {true,true,true,true,true,false,false,false};
    static final byte [] comp5 = {13, // Consider 4 instead of 3...
				  4,0,3,2,0,1,0,2,1,4,0,3};

    /* The boundary cases */
    static final byte [] bcase1a= {9,5, 4,3,5,4,3,4,4};
    static final boolean [] bpat1a= {true,true,false,true,true,false,false,true};
    static final byte [] bcomp1a = {3,
				   1,8};
    static final byte [] bcase1b= {9,5, 4,24,3,4,5,3,4};
    static final boolean [] bpat1b= {true,true,false,false,true,true,false,true};
    static final byte [] bcomp1b = {5,
				   1,2,1,9};
    static final byte [] bcase2a= {7,4, 4,3,5,4,4};
    static final boolean [] bpat2a= {true,true,false,true,true,true};
    static final byte [] bcomp2a = {3,
				   1,5};
    static final byte [] bcase2b= {7,4, 4,4,5,3,4};
    static final boolean [] bpat2b= {true,true,true,true,false,true};
    static final byte [] bcomp2b = {3,
				   1,5};


    static final byte [] bcase3= {10,4, 5,4,3,4,5,4,3,4};
    static final boolean [] bpat3= {false,true,false,false,true,true,false,false,true};
    static final byte [] bcomp3 = {12,
				   8,2,0,1,0,2,3,1,0,2,3};
				   //3,1,8,4};
    static final byte [] bcase4= {9,5, 4,3,5,3,5,3,4};
    static final boolean [] bpat4= {true,true,false,true,true,true,false,true};
    static final byte [] bcomp4 = {7,
				   1,2,1,5,1,8};


    /** Perform one more step of connectivity cleanup. */
    private void connCleanupStep() {
	Msg.debug("Entering TopoCleanup.connCleanupStep()");
	int i, vInd;
	Node c= null;
	Element elem;
	Dart d;
	byte [] pattern;
	Node [] ccwNeighbors= null;
	double [] angles;

	// First check for the standard patterns:
	for (i= 0; i< nodes.size(); i++) {
	    c= (Node) nodes.get(i);
	    if (c== null || c.boundaryOrTriangleNode())
		continue;
	    ccwNeighbors= c.ccwSortedNeighbors();
	    c.createValencePattern(ccwNeighbors);
	    if (c.irregNeighborNodes()<= 2)
		continue;

	    Msg.debug("The valence of node "+c.descr()+" is "+c.valDescr());
	    angles= c.surroundingAngles(ccwNeighbors, c.pattern[0]-2);

	    if ((vInd= c.patternMatch(stdCase1, stdVertexCase1, angles))!= -1) {
		Msg.debug("connCleanupStep(): matching stdCase1");
		d= getDartAt(c, ccwNeighbors, vInd-2);
		applyComposition(d, stdComp1);

		vInd= nodes.indexOf(c);
		if (vInd!= -1)
		    nodes.remove(vInd);
		addNodes(ccwNeighbors, c.pattern[0]-2);
	    }
	    else if ((vInd= c.patternMatch(stdCase2a, stdVertexCase2a, angles))!= -1) {
		Msg.debug("connCleanupStep(): matching stdCase2a");
		d= getDartAt(c, ccwNeighbors, vInd-2);
		applyComposition(d, stdComp2a);

		vInd= nodes.indexOf(c);
		if (vInd!= -1)
		    nodes.remove(vInd);
		addNodes(ccwNeighbors, c.pattern[0]-2);
	    }
	    else if ((vInd= c.patternMatch(stdCase2b, stdVertexCase2b, angles))!= -1) {
		Msg.debug("connCleanupStep(): matching stdCase2b");
		d= getDartAt(c, ccwNeighbors, vInd-2);
		applyComposition(d, stdComp2b);

		vInd= nodes.indexOf(c);
		if (vInd!= -1)
		    nodes.remove(vInd);
		addNodes(ccwNeighbors, c.pattern[0]-2);
	    }
	    else if ((vInd= c.patternMatch(stdCase3a, stdVertexCase3a, angles))!= -1) {
		Msg.debug("connCleanupStep(): matching stdCase3a");
		d= getDartAt(c, ccwNeighbors, vInd-2);
		applyComposition(d, stdComp3a);

		vInd= nodes.indexOf(c);
		if (vInd!= -1)
		    nodes.remove(vInd);
		addNodes(ccwNeighbors, c.pattern[0]-2);
	    }
	    else if ((vInd= c.patternMatch(stdCase3b, stdVertexCase3b, angles))!= -1) {
		Msg.debug("connCleanupStep(): matching stdCase3b");
		d= getDartAt(c, ccwNeighbors, vInd-2);
		applyComposition(d, stdComp3b);

		vInd= nodes.indexOf(c);
		if (vInd!= -1)
		    nodes.remove(vInd);
		addNodes(ccwNeighbors, c.pattern[0]-2);
	    }
	}

	// Then check for the other patterns:
	for (i= 0; i< nodes.size(); i++) {
	    c= (Node) nodes.get(i);
	    if (c== null || c.boundaryOrTriangleNode())
		continue;
	    ccwNeighbors= c.ccwSortedNeighbors();
	    c.createValencePattern(ccwNeighbors);
	    if (c.irregNeighborNodes()<= 2)
		continue;

	    Msg.debug("The valence of node "+c.descr()+" is "+c.valDescr());
	    angles= c.surroundingAngles(ccwNeighbors, c.pattern[0]-2);

	    if ((vInd= c.patternMatch(case1a))!= -1) {
		Msg.debug("connCleanupStep(): matching case1a");
		d= getDartAt(c, ccwNeighbors, vInd-2);
		if (d!= null) {
		    applyComposition(d, comp1a);
		    vInd= nodes.indexOf(c);
		    if (vInd!= -1)
			nodes.remove(vInd);
		    addNodes(ccwNeighbors, c.pattern[0]-2);
		}
	    }
	    else if ((vInd= c.patternMatch(case1b))!= -1) {
		Msg.debug("connCleanupStep(): matching case1b");
		d= getDartAt(c, ccwNeighbors, vInd-2);
		if (d!= null) {
		    applyComposition(d, comp1b);
		    vInd= nodes.indexOf(c);
		    if (vInd!= -1)
			nodes.remove(vInd);
		    addNodes(ccwNeighbors, c.pattern[0]-2);
		}
	    }
	    else if ((vInd= c.patternMatch(case2))!= -1) {
		Msg.debug("connCleanupStep(): matching case2");
		d= getDartAt(c, ccwNeighbors, vInd-2);
		if (d!= null) {
		    applyComposition(d, comp2);
		    vInd= nodes.indexOf(c);
		    if (vInd!= -1)
			nodes.remove(vInd);
		    addNodes(ccwNeighbors, c.pattern[0]-2);
		}
	    }
	    else if ((vInd= c.patternMatch(case3, vertexCase3, angles))!= -1 &&
		     internalNodes(ipat3, ccwNeighbors, vInd-2, c.pattern[0]-2)) {
		Msg.debug("connCleanupStep(): matching case3");
		d= getDartAt(c, ccwNeighbors, vInd-2);
		if (d!= null) {
		    applyComposition(d, comp3);
		    vInd= nodes.indexOf(c);
		    if (vInd!= -1)
			nodes.remove(vInd);
		    addNodes(ccwNeighbors, c.pattern[0]-2);
		}
	    }
	    else if ((vInd= c.patternMatch(case4))!= -1 &&
		     internalNodes(ipat4, ccwNeighbors, vInd-2, c.pattern[0]-2)) {
		Msg.debug("connCleanupStep(): matching case4");
		d= getDartAt(c, ccwNeighbors, vInd-2);
		if (d!= null) {
		    applyComposition(d, comp4);
		    vInd= nodes.indexOf(c);
		    if (vInd!= -1)
			nodes.remove(vInd);
		    addNodes(ccwNeighbors, c.pattern[0]-2);
		}
	    }
	    else if ((vInd= c.patternMatch(case5))!= -1 &&
		     internalNodes(ipat5, ccwNeighbors, vInd-2, c.pattern[0]-2)) {
		Msg.debug("connCleanupStep(): matching case5");
		d= getDartAt(c, ccwNeighbors, vInd-2);
		if (d!= null) {
		    applyComposition(d, comp5);
		    vInd= nodes.indexOf(c);
		    if (vInd!= -1)
			nodes.remove(vInd);
		    addNodes(ccwNeighbors, c.pattern[0]-2);
		}
	    }
	}

	nodes= (ArrayList)nodeList.clone();
	connCleanupFinished= true;
	count= 0;
	Msg.debug("Leaving TopoCleanup.connCleanupStep(), all is well");
    }

    /** Method to confirm whether marked nodes are truely internal .*/
    private boolean internalNodes(boolean [] ipat, Node [] ccwNodes, int index,int len){
	Msg.debug("Entering internalNodes(..)");
	int i= index, j= 0;
	while (j< len) {
	    if (ipat[j] && ccwNodes[i].boundaryNode()) {
		Msg.debug("Leaving internalNodes(..): false");
		return false;
	    }
	    j++;
	    i++;
	    if (i== len)
		i= 0;
	}
	Msg.debug("Leaving internalNodes(..): true");
	return true;
    }

    private void addNodes(Node [] arrayOfNodes, int len) {
	Node n;
	int j;
	for (int i= 0; i< len; i++) {
	    n= arrayOfNodes[i];
	    if (n!= null) {
		j= nodes.indexOf(n);
		if (j== -1) {
		    j= nodeList.indexOf(n);
		    if (j!= -1)
			nodes.add(n);
		}
	    }
	}
    }

    private boolean bcaseTriQFin= false;
    private boolean bcaseValPat1Fin= false;
    private boolean bcaseValPat2Fin= false;
    private boolean bcaseValPat3Fin= false;
    private boolean bcaseValPat4Fin= false;
    private boolean bcaseDiamondFin= false;

    /** Perform one more steps of boundary cleanup. */
    private void boundaryCleanupStep() {
	Msg.debug("Entering TopoCleanup.boundaryCleanupStep()");
	int i, j, index;
	Element elem;
	Triangle tri;
	Quad q= null, pq= null;
	Node n, n0, n1= null, n2, n3;
	Edge e1= null, e2= null, ep= null, e;
	Node [] ccwNeighbors;
	Dart d;

	if (!bcaseValPat1Fin) {

	    for (i= 0; i< nodes.size(); i++) {
		n1= (Node)nodes.get(i);
		if (n1== null)
		    continue;
		ccwNeighbors= n1.ccwSortedNeighbors();
		if (ccwNeighbors== null)
		    continue;
		n1.createValencePattern((byte)(n1.edgeList.size()*2 -1),
					ccwNeighbors);

		if (n1.boundaryPatternMatch(bcase1a, bpat1a, ccwNeighbors)) {
		    Msg.debug("boundaryCleanupStep(): matching bcase1a, bpat1a");
		    d= getDartAt(n1, ccwNeighbors, 0);
		    applyComposition(d, bcomp1a);

		    j= nodes.indexOf(n1);
		    if (j!= -1)
			nodes.remove(j);
		    addNodes(ccwNeighbors, n1.pattern[0]-2);
		}
		else if (n1.boundaryPatternMatch(bcase1b, bpat1b, ccwNeighbors)) {
		    Msg.debug("boundaryCleanupStep(): matching bcase1b, bpat1b");
		    d= getDartAt(n1, ccwNeighbors, 0);
		    applyComposition(d, bcomp1b);

		    j= nodes.indexOf(n1);
		    if (j!= -1)
			nodes.remove(j);
		    addNodes(ccwNeighbors, n1.pattern[0]-2);
		}
	    }
	    //nodes= (ArrayList)nodeList.clone();
	    bcaseValPat1Fin= true;
	    Msg.debug("Leaving boundaryCleanupStep(): Done looking for case 1s.");
	    return;
	}
	else if (!bcaseValPat2Fin) {

	    for (i= 0; i< nodes.size(); i++) {
		n1= (Node)nodes.get(i);
		if (n1== null)
		    continue;
		ccwNeighbors= n1.ccwSortedNeighbors();
		if (ccwNeighbors== null)
		    continue;
		n1.createValencePattern((byte)(n1.edgeList.size()*2 -1),
					ccwNeighbors);

		if (n1.boundaryPatternMatch(bcase2a, bpat2a, ccwNeighbors)) {
		    Msg.debug("boundaryCleanupStep(): matching bcase2a, bpat2a");
		    d= getDartAt(n1, ccwNeighbors, 0);
		    applyComposition(d, bcomp2a);

		    j= nodes.indexOf(n1);
		    if (j!= -1)
			nodes.remove(j);
		    addNodes(ccwNeighbors, n1.pattern[0]-2);
		}
		else if (n1.boundaryPatternMatch(bcase2b, bpat2b, ccwNeighbors)) {
		    Msg.debug("boundaryCleanupStep(): matching bcase2b, bpat2b");
		    d= getDartAt(n1, ccwNeighbors, 0);
		    applyComposition(d, bcomp2b);

		    j= nodes.indexOf(n1);
		    if (j!= -1)
			nodes.remove(j);
		    addNodes(ccwNeighbors, n1.pattern[0]-2);
		}
	    }
	    //nodes= (ArrayList)nodeList.clone();
	    bcaseValPat2Fin= true;
	    Msg.debug("Leaving boundaryCleanupStep(): Done looking for case 2s.");
	    return;
	}
	else if (!bcaseValPat3Fin) {

	    for (i= 0; i< nodes.size(); i++) {
		n1= (Node)nodes.get(i);
		if (n1== null)
		    continue;
		Msg.debug("...testing node "+n1.descr());
		if (n1.boundaryNode())
		    continue;

		ccwNeighbors= n1.ccwSortedNeighbors();
		if (ccwNeighbors== null)
		    continue;
		n1.createValencePattern((byte)(n1.edgeList.size()*2),
					ccwNeighbors);

		if ((index=
		     n1.boundaryPatternMatchSpecial(bcase3, bpat3, ccwNeighbors))!=-1) {
		    Msg.debug("boundaryCleanupStep(): matching bcase3, bpat3");
		    d= getDartAt(n1, ccwNeighbors, index-2);
		    applyComposition(d, bcomp3);

		    j= nodes.indexOf(n1);
		    if (j!= -1)
			nodes.remove(j);
		    addNodes(ccwNeighbors, n1.pattern[0]-2);
		}
	    }
	    //nodes= (ArrayList)nodeList.clone();
	    bcaseValPat3Fin= true;
	    Msg.debug("Leaving boundaryCleanupStep(): Done looking for case 3s.");
	    return;
	}
	else if (!bcaseValPat4Fin) {
	    for (i= 0; i< nodes.size(); i++) {
		n1= (Node)nodes.get(i);
		if (n1== null)
		    continue;
		ccwNeighbors= n1.ccwSortedNeighbors();
		if (ccwNeighbors== null)
		    continue;
		n1.createValencePattern((byte)(n1.edgeList.size()*2 -1),
					ccwNeighbors);

		if (n1.boundaryPatternMatch(bcase4, bpat4, ccwNeighbors)) {
		    Msg.debug("boundaryCleanupStep(): matching bcase4, bpat4");
		    d= getDartAt(n1, ccwNeighbors, 0);
		    applyComposition(d, bcomp4);

		    j= nodes.indexOf(n1);
		    if (j!= -1)
			nodes.remove(j);
		    addNodes(ccwNeighbors, n1.pattern[0]-2);
		}
	    }
	    //nodes= (ArrayList)nodeList.clone();
	    bcaseValPat4Fin= true;
	    Msg.debug("Leaving boundaryCleanupStep(): Done looking for case 4s.");
	    return;
	}
	else if (!bcaseTriQFin) {
	    for (i= 0; i< nodes.size(); i++) {
		n1= (Node)nodes.get(i);

		if (n1== null)
		    continue;

		if (n1.boundaryNode()) {

		    Msg.debug("...testing node "+n1.descr());
		    e1= (Edge)n1.edgeList.get(0);
		    elem= e1.element1;
		    if (!(elem instanceof Quad))  // Must be quad
			continue;
		    q= (Quad)elem;

		    if (n1.edgeList.size()==2 && q.ang[q.angleIndex(n1)] > DEG_150) {
			Msg.debug("...inside if (n1.edgeList.size()==2 ...");
			e2= q.neighborEdge(n1, e1);
			if (e1.boundaryEdge() && e2.boundaryEdge()) {
			    Msg.debug("...inside if (e1.boundaryEdge() ...");
			    Quad q3= null, q4= null, q33= null, q44= null, qNew,
				qn= null;
			    Edge e3, e4, e33, e44;
			    n0= e1.otherNode(n1);
			    n2= e2.otherNode(n1);
			    e3= q.neighborEdge(n2, e2);
			    n3= e3.otherNode(n2);
			    e4= q.neighborEdge(n3, e3);

			    elem= q.neighbor(e3);
			    if (elem instanceof Quad)
				q3= (Quad)elem;

			    elem= q.neighbor(e4);
			    if (elem instanceof Quad)
				q4= (Quad)elem;

			    if (q3!= null && q4!= null) {

				e33= q3.neighborEdge(n3, e3);
				e44= q4.neighborEdge(n3, e4);

				elem= q3.neighbor(e33);
				if (elem instanceof Quad)
				    q33= (Quad)elem;

				elem= q4.neighbor(e44);
				if (elem instanceof Quad)
				    q44= (Quad)elem;

				if (q33!= null) {
				    e= q33.neighborEdge(n3, e33);
				    elem= q33.neighbor(e);
				    if (elem instanceof Quad)
					qn= (Quad)elem;
				}

				if (q33== null || q44== null || qn!= q44) {
				    // One row transition
				    elementList.remove(elementList.indexOf(q));
				    elementList.remove(elementList.indexOf(q3));
				    elementList.remove(elementList.indexOf(q4));

				    fill4(q, e4, n3);
				    qNew= (Quad)q3.neighbor(e3);
				    fill3(q3, e3, n2, true);
				    elementList.remove(elementList.indexOf(qNew));

				    j= nodes.indexOf(n1);
				    if (j!= -1)
					nodes.remove(j);
				}
				else if (qn== q44) {
				    // Two row transition
				    elementList.remove(elementList.indexOf(q));
				    elementList.remove(elementList.indexOf(q4));
				    elementList.remove(elementList.indexOf(q44));

				    fill3(q4, e44, e44.otherNode(n3), true);
				    qNew= (Quad)q.neighbor(e4);
				    fill3(q, e4, n3, true);

				    elementList.remove(elementList.indexOf(qNew));

				    j= nodes.indexOf(n1);
				    if (j!= -1)
					nodes.remove(j);
				}
			    }
			}
		    }
		}
	    }
	    Msg.debug("Leaving boundaryCleanupStep(): Done removing triangular quads.");
	    bcaseTriQFin= true;
	    return;
	}
	else if (!bcaseDiamondFin) {

	    for (i= 0; i< nodes.size(); i++) {
		n1= (Node)nodes.get(i);

		if (n1== null)
		    continue;

		if (n1.boundaryNode() && n1.valence()> 4) {
		    Msg.debug("...testing node "+n1.descr());

		    // First find a quad having an edge at the boundary at node n1,
		    // or if this does not exist, the find the first quad when looking
		    // from the boundary an inwards around node n1:
		    e1= n1.anotherBoundaryEdge(null);
		    if (e1.element1 instanceof Quad) {
			q=(Quad)e1.element1;
			pq= q;
			ep= e1;
		    }
		    else {
			tri= (Triangle)e1.element1;
			e1= e1.nextQuadEdgeAt(n1, e1.element1);
			if (e1!= null) {
			    q= e1.getQuadElement();
			    pq= q;
			    ep= e1;
			}
		    }

		    if (q!= null)
			Msg.debug("...first quad found is "+q.descr());

		    // Then parse each quad from one boundary edge to the other until
		    // a match is made:
		    int val, n2val, n3val, maxdev;
		    while (q!= null) {
			if (q.boundaryDiamond()) {

			    Msg.debug("...Considering to close quad");

			    n2= e1.otherNode(n1);
			    e2= q.neighborEdge(n1, e1);
			    n3= e2.otherNode(n1);

			    n2val= n2.valence();
			    n3val= n3.valence();
			    maxdev= Math.max(Math.abs(4-n2val), Math.abs(4-n3val));
			    val= n2val + n3val;

			    if (Math.abs(4-(val-2)) <= maxdev) {
				Msg.debug("...closing quad "+q.descr());
				d= closeQuad(q, e1, e1.otherNode(n1), true);
				if (d!= null) {
				    q= pq;
				    e1= ep;
				}
			    }
			}
			Msg.debug("...q: "+q.descr()+", e1: "+e1.descr()+", n1: "+n1.descr());
			pq= q;
			ep= e1;
			e1= e1.nextQuadEdgeAt(n1, q);
			if (e1!= null)
			    q= (Quad)q.neighbor(e1);
			else
			    break;
		    }
		}
	    }
	    bcaseDiamondFin= true;
	    Msg.debug("Leaving boundaryCleanupStep(): Done removing diamonds.");
	    return;
	}
	else {
	    nodes= (ArrayList)nodeList.clone();
	    boundaryCleanupFinished= true;
	    count= 0;
	    Msg.debug("Leaving TopoCleanup.boundaryCleanupStep(), all is well.");
	    return;
	}
    }

    private boolean shape1stTypeFin= false;

    /** The shape cleanup */
    private void shapeCleanupStep() {
	Msg.debug("Entering TopoCleanup.shapeCleanupStep()");

	Element elem;
	Quad q= null, q2= null, qo= null, qtemp;
	Edge e1, e2, e3, e4, eo;
	Node n, n1, n2, n3, n4, nqOpp, nq2Opp, noOpp;
	int i, j;
	double ang, ang1= 0, ang2= 0, ango= 0, angtmp, q2angn3;

	if (!shape1stTypeFin) {
	    Msg.debug("...hallo???");
	    for (i= 0; i< nodes.size(); i++) {
		n= (Node)nodes.get(i);

		if (n== null)
		    continue;

		if (n.boundaryNode()) {

		    if (n.edgeList.size()== 3) {


			e1= n.anotherBoundaryEdge(null);
			elem= e1.element1;
			if (!(elem instanceof Quad))
			    continue;
			q= (Quad)elem;
			ang= q.ang[q.angleIndex(n)];

			e2= q.neighborEdge(n, e1);
			elem= q.neighbor(e2);
			if (elem instanceof Quad) {
			    q2= (Quad)elem;

			    ang2= q2.ang[q2.angleIndex(n)];
			    if (ang2> DEG_160 && ang2> ang) {
				qtemp= q;
				q= q2;
				q2= qtemp;
				angtmp= ang;
				ang= ang2;
				ang2= angtmp;

				e1= q.neighborEdge(n, e2);
			    }
			}

			if (ang < DEG_160)
			    continue;

			n1= e1.otherNode(n);
			n2= e2.otherNode(n);
			nqOpp= q.oppositeNode(n);

			ang1= q.ang[q.angleIndex(n1)];

			if (!n1.boundaryNode() || !n2.boundaryNode() ||
			    !nqOpp.boundaryNode())
			    continue;

			eo= q.neighborEdge(n1, e1);

			elem= q.neighbor(eo);
			if (elem instanceof Quad) {
			    qo= (Quad)elem;
			    ango= qo.ang[qo.angleIndex(n1)];
			}

			if (q2== null || qo== null) // Was && instead of ||
			    continue;


			e3= q2.neighborEdge(n, e2);
			n3= e3.otherNode(n);
			q2angn3= q2.ang[q2.angleIndex(n3)];
			nq2Opp= q2.oppositeNode(n);

			e4= qo.neighborEdge(n1, eo);
			n4= e4.otherNode(n1);
			noOpp= qo.oppositeNode(n1);

			if (ang2!= 0 && q2angn3> ango && n3.boundaryNode() &&
			    nq2Opp.boundaryNode()) {

			    elementList.remove(elementList.indexOf(q));
			    elementList.remove(elementList.indexOf(q2));
			    fill4(q, e2, n);
			}
			else if (ango!= 0 && ango > q2angn3 && n4.boundaryNode()
				 && nq2Opp.boundaryNode()) {

			    elementList.remove(elementList.indexOf(q));
			    elementList.remove(elementList.indexOf(qo));
			    fill4(qo, eo, n1);
			}
		    }
		    else if (n.edgeList.size()== 4) {
			e1= n.anotherBoundaryEdge(null);
			elem= e1.element1;
			if (elem instanceof Quad)
			    q2= (Quad)elem;
			else
			    continue;

			e2= q2.neighborEdge(n, e1);
			elem= q2.neighbor(e2);
			if (elem instanceof Quad)
			    q= (Quad)elem;
			else
			    continue;

			if (q.ang[q.angleIndex(n)]< DEG_160)
			    continue;

			e3= q.neighborEdge(n, e2);
			elem= q.neighbor(e3);
			if (elem instanceof Quad)
			    qo= (Quad)elem;
			else
			    continue;
			e4= qo.neighborEdge(n, e3);

			n1= e1.otherNode(n);
			n2= e2.otherNode(n);
			n3= e3.otherNode(n);
			n4= e4.otherNode(n);

			if (!n1.boundaryNode() || !n2.boundaryNode() ||
			    !n3.boundaryNode() || !n4.boundaryNode())
			    continue;

			openQuad(q, e2, n);
			elementList.remove(elementList.indexOf(q2.neighbor(e2)));
			elementList.remove(elementList.indexOf(qo.neighbor(e3)));
			fill3(q2, e2, n2, true);
			fill3(qo, e3, n3, true);

			elementList.remove(elementList.indexOf(q2));
			elementList.remove(elementList.indexOf(qo));
		    }
		}
	    }
	    shape1stTypeFin= true;
	    return;
	}

	while (count< elementList.size()) {
	    elem= (Element) elementList.get(count);

	    if (elem== null || !(elem instanceof Quad))
		count++;
	    else {
		Msg.debug("...testing element "+elem.descr());

		q= (Quad)elem;
		if (q.isFake)
		    Msg.error("...Fake quad encountered!!!");

		if (q.isChevron()) {
		    eliminateChevron(q);
		    count++;
		}
		else
		    count++;
	    }
	}

	for (i= 0; i < deleteList.size(); i++) {
	    elem= (Element) deleteList.get(i);
	    elementList.remove(elementList.indexOf(elem));
	}
	deleteList.clear();

	nodes= (ArrayList)nodeList.clone();
	shapeCleanupFinished= true;
	count= 0;
	Msg.debug("Leaving TopoCleanup.shapeCleanupStep(), all elements ok.");
    }

    /** Return the dart with node c, the edge connecting c and the node at pos. i in
     * neighbors, and the quad with that edge and node at pos. i+1
     * @param c             the central node
     * @param neighbors     array of neighboring nodes to c
     * @param i             index into neighbors
     */
    private Dart getDartAt(Node c, Node [] neighbors, int i) {
	Msg.debug("Entering getDartAt(..)");
	Edge e= c.commonEdge(neighbors[i]);
	if (e== null) {
	    Msg.debug("Leaving getDartAt(..), not found!");
	    return null;
	}
	Msg.debug("...1st matching node in neighbors: "
		  +neighbors[i].descr());
	Quad q1= (Quad)e.element1;
	Quad q2= (Quad)e.element2;

	Msg.debug("Leaving getDartAt(..)");
	if (q1.hasNode(neighbors[i+1]))
	    return new Dart(c, e, q1);
	else if (q2.hasNode(neighbors[i+1]))
	    return new Dart(c, e, q2);
	else
	    return null;
    }

    /** Collapse a quad by joining two and two of its consecutive edges.
     * @param q the quad to be collapsed
     * @param e1 an edge of q that has the node nK
     * @param nK the node that is to be joined with its opposite node in q
     * @param centroid boolean indicating whether to look for a new pos for the joined
     * nodes somewhere between the original positions, starting at the centroid of q,
     * or to unconditionally try using the position of the node in q which is opposite
     * to nK.
     * @return the new current dart.
     */
    private Dart closeQuad(Quad q, Edge e1, Node nK, boolean centroid) {
	Msg.debug("Entering closeQuad(..)");
	Dart d= new Dart();
	Element nElem= q.neighbor(e1); // Save for later...
	Node nKOpp= q.oppositeNode(nK);
	Node nKp1= e1.otherNode(nK); //, nKm1= eKm1.otherNode(nK);
	Edge e2= q.neighborEdge(nKp1, e1), e4= q.neighborEdge(nK, e1);

	ArrayList lK= nK.adjElements(), lKOpp= nKOpp.adjElements();
	Node n= null;
	int i;

	if (centroid) {
	    n= safeNewPosWhenCollapsingQuad(q, nK, nKOpp);
	    if (n== null) {
		Msg.debug("Leaving closeQuad(..), returning null!");
		return null;
	    }
	}
	else {
	    if (q.anyInvertedElementsWhenCollapsed(nKOpp, nK, nKOpp, lK, lKOpp)) {
		Msg.debug("Leaving closeQuad(..), returning null!");
		return null;
	    }
	    else
		n= nKOpp;
	}
	elementList.remove(elementList.indexOf(q));

	edgeList.remove(edgeList.indexOf(e1)); //e2
	edgeList.remove(edgeList.indexOf(q.neighborEdge(nK, e1))); //(nKOpp, e2)
	q.disconnectEdges();
	q.closeQuad(e2, e1); //(e1,e2)

	nKOpp.setXY(n); //nK.setXY(n);
	nodeList.remove(nodeList.indexOf(nK)); //nKOpp
	i= nodes.indexOf(nK);
	if (i!= -1)
	    nodes.set(i, null); //nKOpp
	nKOpp.update();  // nK.update();

	d.elem= nElem;
	d.e= e2; //e1;
	d.n= nKOpp; //nK;

	Msg.debug("Leaving closeQuad(..)");
	return d;
    }

    /** Create a new quad by "opening" one at the specified node inside the specified
     * quad. This effectively results in a splitting of the specified quad.
     * @param q the quad
     * @param e an edge in q (not used by method), but contained in returned dart
     * @param n1 split q along the diagonal between n1 and its opposite node
     */
    private Dart openQuad(Quad q, Edge e, Node n1) {
	Msg.debug("Entering openQuad(..)");
	Dart d= new Dart();
	Node c= q.centroid();
	Edge e1= q.neighborEdge(n1);
	Edge e2= q.neighborEdge(n1, e1);

	Node n2= e2.otherNode(n1);
	Edge e3= q.neighborEdge(n2, e2);
	Node n3= e3.otherNode(n2);
	Edge e4= q.neighborEdge(n3, e3);
	Node n4= e4.otherNode(n3);

	Edge e1New= new Edge(c, n1);
	Edge e4New= new Edge(c, n3);

	e1New.connectNodes();
	e4New.connectNodes();

	q.replaceEdge(e1, e1New);
	q.replaceEdge(e4, e4New);
	e1.disconnectFromElement(q);
	e4.disconnectFromElement(q);

	e1New.connectToQuad(q);
	e4New.connectToQuad(q);

	Quad qNew;
	if (e1.leftNode == n1)
	    qNew= new Quad(e1, e1New, e4, e4New);
	else
	    qNew= new Quad(e1, e4, e1New, e4New);

	qNew.connectEdges();


	edgeList.add(e1New);
	edgeList.add(e4New);

	elementList.add(qNew);
	c.color= java.awt.Color.red; // Indicate it was created during clean-up
	nodeList.add(c);
	nodes.add(c);

	q.updateLR();

	d.elem= q;
	d.e= e;
	d.n= n1;

	Msg.debug("Leaving openQuad(..)");
	return d;
    }

    /** Create 2 new quads from 2 specified quads, in which the common edge of the given
     * quads has been rotated one step in the CCW direction. Delete the old quads.
     * @param qa one of the two quads adjacent the edge to be switched, e1a
     * @param e1a the edge to be switched
     * @param n one of e1a's nodes
     * @return a dart representing the input dart after the operation is performed.
     */
    private Dart switchDiagonalCCW(Quad qa, Edge e1a, Node n){
	Msg.debug("Entering switchDiagonalCCW(..)");
	Dart d= new Dart();
	Node n1a, n2a, n3a, n4a, n1b, n2b, n3b, n4b;
	Edge e2a, e3a, e4a, e1b, e2b, e3b, e4b;
	Edge eNew, l, r;
	Quad q1, q2;

	Quad qb= (Quad)qa.neighbor(e1a);
	int qaIndex= elementList.indexOf(qa), qbIndex= elementList.indexOf(qb);

	// First get the edges of qa in ccw order:
	n2a= qa.nextCCWNode(e1a.leftNode);
	if (n2a== e1a.rightNode) {
	    n1a= e1a.leftNode;
	}
	else {
	    n1a= e1a.rightNode;
	    n2a= e1a.leftNode;
	}

	e2a= qa.neighborEdge(n2a, e1a);
	n3a= e2a.otherNode(n2a);
	e3a= qa.neighborEdge(n3a, e2a);
	n4a= e3a.otherNode(n3a);
	e4a= qa.neighborEdge(n4a, e3a);

	// Now get the edges of qb in ccw order:
	e1b= e1a;
	n2b= qb.nextCCWNode(e1b.leftNode);
	if (n2b== e1b.rightNode) {
	    n1b= e1b.leftNode;
	}
	else {
	    n1b= e1b.rightNode;
	    n2b= e1b.leftNode;
	}
	e2b= qb.neighborEdge(n2b, e1b);
	n3b= e2b.otherNode(n2b);
	e3b= qb.neighborEdge(n3b, e2b);
	n4b= e3b.otherNode(n3b);
	e4b= qb.neighborEdge(n4b, e3b);

	Node nOld, smoothed;
	// Check to see if the switch will violate the mesh topology:
	if (e4a.sumAngle(qa, n1a, e2b)>= Math.PI) { // if angle >= 180 degrees...
	    if (n1a.boundaryNode()) { // exit if node on boundary
		Msg.debug("Leaving switchDiagonalCCW(..): returning null");
		return null;
	    }

	    // ...then try smoothing the pos of the node:
	    nOld= new Node(n1a.x, n1a.y);
	    smoothed= n1a.laplacianSmoothExclude(n2a);
	    if (!n1a.equals(smoothed)) {
		n1a.setXY(smoothed.x, smoothed.y);
		inversionCheckAndRepair(n1a, nOld);
		n1a.update();
	    }

	    if (e4a.sumAngle(qa, n1a, e2b)>= Math.PI) { // Still angle >= 180 degrees?
		Msg.debug("Leaving switchDiagonalCCW(..): returning null");
		return null;
	    }
	}

	if (e2a.sumAngle(qa, n2a, e4b)>= Math.PI) { // if angle >= 180 degrees...
	    if (n2a.boundaryNode()) { // exit if node on boundary
		Msg.debug("Leaving switchDiagonalCCW(..): returning null");
		return null;
	    }

	    // ...then try smoothing the pos of the node:
	    nOld= new Node(n2a.x, n2a.y);
	    smoothed= n2a.laplacianSmoothExclude(n1a);
	    if (!n2a.equals(smoothed)) {
		n2a.setXY(smoothed.x, smoothed.y);
		inversionCheckAndRepair(n2a, nOld);
		n2a.update();
	    }

	    if (e2a.sumAngle(qa, n2a, e4b)>= Math.PI) { // Still angle >= 180 degrees?
		Msg.debug("Leaving switchDiagonalCCW(..): returning null");
		return null;
	    }
	}
	// The new diagonal:
	eNew= new Edge(n3a, n3b);

	// Create the new quads:
	l= qa.neighborEdge(e4a.leftNode, e4a);
	r= qa.neighborEdge(e4a.rightNode, e4a);
	if (l== e1a)
	    l= e2b;
	else
	    r= e2b;
	q1= new Quad(e4a, l, r, eNew);

	l= qb.neighborEdge(e4b.leftNode, e4b);
	r= qb.neighborEdge(e4b.rightNode, e4b);
	if (l== e1b)
	    l= e2a;
	else
	    r= e2a;
	q2= new Quad(e4b, l, r, eNew);

	qa.disconnectEdges();
	qb.disconnectEdges();
	e1a.disconnectNodes();
	q1.connectEdges();
	q2.connectEdges();
	eNew.connectNodes();

	// Update lists:
	edgeList.set(edgeList.indexOf(e1a), eNew);

	elementList.set(qaIndex, q1);
	elementList.set(qbIndex, q2);

	d.elem= q1;
	d.e= eNew;
	if (n==n1a)
	    d.n= n3b;
	else
	    d.n= n3a;

	Msg.debug("Leaving switchDiagonalCCW(..)");
	return d;
    }

    /** Create 2 new quads from 2 specified quads, in which the common edge of the given
     * quads has been rotated one step in the CW direction. Delete the old quads.
     * Update the nodes list.
     * @param qa one of the two quads adjacent the edge to be switched, e1a
     * @param e1a the edge to be switched
     * @param n one of e1a's nodes
     * @return a dart representing the input dart after the operation is performed.
     */
    private Dart switchDiagonalCW(Quad qa, Edge e1a, Node n){
	Msg.debug("Entering switchDiagonalCW(..)");
	Dart d= new Dart();
	Node n1a, n2a, n3a, n4a, n1b, n2b, n3b, n4b;
	Edge e2a, e3a, e4a, e1b, e2b, e3b, e4b;
	Edge eNew, l, r;
	Quad q1, q2;

	Quad qb= (Quad)qa.neighbor(e1a);
	int qaIndex= elementList.indexOf(qa), qbIndex= elementList.indexOf(qb);

	// First get the edges of qa in ccw order:
	n2a= qa.nextCCWNode(e1a.leftNode);
	if (n2a== e1a.rightNode) {
	    n1a= e1a.leftNode;
	}
	else {
	    n1a= e1a.rightNode;
	    n2a= e1a.leftNode;
	}
	e2a= qa.neighborEdge(n2a, e1a);
	n3a= e2a.otherNode(n2a);
	e3a= qa.neighborEdge(n3a, e2a);
	n4a= e3a.otherNode(n3a);
	e4a= qa.neighborEdge(n4a, e3a);

	// Now get the edges of qb in ccw order:
	e1b= e1a;
	n2b= qb.nextCCWNode(e1b.leftNode);
	if (n2b== e1b.rightNode) {
	    n1b= e1b.leftNode;
	}
	else {
	    n1b= e1b.rightNode;
	    n2b= e1b.leftNode;
	}
	e2b= qb.neighborEdge(n2b, e1b);
	n3b= e2b.otherNode(n2b);
	e3b= qb.neighborEdge(n3b, e2b);
	n4b= e3b.otherNode(n3b);
	e4b= qb.neighborEdge(n4b, e3b);

	Node nOld, smoothed;
	// Check to see if the switch will violate the mesh topology:
	if (e4a.sumAngle(qa, n1a, e2b)>= Math.PI) { // if angle >= 180 degrees...
	    // ...then try smoothing the pos of the node:
	    nOld= new Node(n1a.x, n1a.y);
	    smoothed= n1a.laplacianSmooth();
	    if (!n1a.equals(smoothed)) {
		n1a.moveTo(smoothed);
		inversionCheckAndRepair(n1a, nOld);
		n1a.update();
	    }

	    if (e4a.sumAngle(qa, n1a, e2b)>= Math.PI) { // Still angle >= 180 degrees?
		Msg.debug("Leaving switchDiagonalCW(..): returning null");
		return null;
	    }
	}

	// Check to see if the switch will violate the mesh topology:
	if (e2a.sumAngle(qa, n2a, e4b)>= Math.PI) { // if angle >= 180 degrees...
	    // ...then try smoothing the pos of the node:
	    nOld= new Node(n2a.x, n2a.y);
	    smoothed= n2a.laplacianSmooth();
	    if (!n2a.equals(smoothed)) {
		n2a.moveTo(smoothed);
		inversionCheckAndRepair(n2a, nOld);
		n2a.update();
	    }

	    if (e2a.sumAngle(qa, n2a, e4b)>= Math.PI) { // Still angle >= 180 degrees?
		Msg.debug("Leaving switchDiagonalCW(..): returning null");
		return null;
	    }
	}

	// The new diagonal:
	eNew= new Edge(n4a, n4b);

	// Create the new quads:
	l= qa.neighborEdge(e2a.leftNode, e2a);
	r= qa.neighborEdge(e2a.rightNode, e2a);
	if (l== e1a)
	    l= e4b;
	else
	    r= e4b;
	q1= new Quad(e2a, l, r, eNew);

	l= qb.neighborEdge(e2b.leftNode, e2b);
	r= qb.neighborEdge(e2b.rightNode, e2b);
	if (l== e1b)
	    l= e4a;
	else
	    r= e4a;
	q2= new Quad(e2b, l, r, eNew);

	qa.disconnectEdges();
	qb.disconnectEdges();
	e1a.disconnectNodes();
	q1.connectEdges();
	q2.connectEdges();
	eNew.connectNodes();

	// Update lists:
	edgeList.set(edgeList.indexOf(e1a), eNew);

	elementList.set(qaIndex, q1);
	elementList.set(qbIndex, q2);

	d.elem= q1;
	d.e= eNew;
	if (n==n1a)
	    d.n= n4a;
	else
	    d.n= n4b;

	Msg.debug("Leaving switchDiagonalCW(..)");
	return d;
    }

    private void globalSmooth() {
	Msg.debug("Entering TopoCleanup.globalSmoth()");
	Node n, nn, nOld;

	for (int i= 0; i< nodeList.size(); i++) {
	    n= (Node)nodeList.get(i);

	    if (!n.boundaryNode()) {

		// Try smoothing the pos of the node:
		nOld= new Node(n.x, n.y);
		nn= n.laplacianSmooth();
		if (!n.equals(nn)) {
		    n.setXY(nn.x, nn.y);
		    inversionCheckAndRepair(n, nOld);
		    n.update();
		}
	    }
	}
	Msg.debug("Leaving TopoCleanup.globalSmoth()");
    }

}
