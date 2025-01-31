package com.github.karllevik.qmorph;

public class TestMyVector extends GeomBasics {

	public static void main(String[] args) {
		Node p0 = new Node(0, 0);
		Node p1 = new Node(0, 1);

		MyVector d0 = new MyVector(p0, 5, 5);
		MyVector d1 = new MyVector(p1, 10, 0);

		Node poi = d0.pointIntersectsAt(d1);
		if (poi != null) {
			poi.printMe();
		} else {
			Msg.debug("poi is null!");
		}

		// ----- Test boolean isCWto(MyVector v) ----- //

		// Same quadrant (1st)
		MyVector v0 = new MyVector(p0, 10, 10);
		MyVector v1 = new MyVector(p0, 10, 9);

		if (v1.isCWto(v0)) {
			Msg.debug("isCWto test1: riktig!");
		} else {
			Msg.debug("isCWto test1: galt!");
		}

		// Opposite quadrants (this in third, v in 1st)
		MyVector v2 = new MyVector(p0, 10, 10);
		MyVector v3 = new MyVector(p0, -9, -10);

		if (v3.isCWto(v2)) {
			Msg.debug("isCWto test2: riktig!");
		} else {
			Msg.debug("isCWto test2: galt!");
		}

		// Opposite quadrants (this in second, v in fourth)
		MyVector v4 = new MyVector(p0, 10, -10);
		MyVector v5 = new MyVector(p0, -11, 10);

		if (v5.isCWto(v4)) {
			Msg.debug("isCWto test3: riktig!");
		} else {
			Msg.debug("isCWto test3: galt!");
		}

		// Opposite quadrants (this in third, v in 1st)
		MyVector v6 = new MyVector(p0, 10, 10);
		MyVector v7 = new MyVector(p0, -9, -10);

		if (v7.isCWto(v6)) {
			Msg.debug("isCWto test4: riktig!");
		} else {
			Msg.debug("isCWto test4: galt!");
		}

		// Same vector
		if (v7.isCWto(v7)) {
			Msg.debug("isCWto test5: v7.isCWto(v7)");
		} else {
			Msg.debug("isCWto test5: ikke v7.isCWto(v7)");
		}

		//
		MyVector v8 = new MyVector(p0, -9, 0);
		MyVector v9 = new MyVector(p0, 0, -9);
		if (v8.isCWto(v9)) {
			Msg.debug("isCWto test6: riktig!");
		} else {
			Msg.debug("isCWto test6: galt!");
		}

		MyVector v10 = new MyVector(p0, 0.01, -9);
		if (v9.isCWto(v10)) {
			Msg.debug("isCWto test7: riktig!");
		} else {
			Msg.debug("isCWto test7: galt!");
		}

		Msg.debug("----- Special tests of isCWto(MyVector v) -----");
		Node nC = new Node(-0.3, 1.2);
		Node nD = new Node(-0.3, 0.3);
		Node nK = new Node(-1.7538203106994033, 1.2016864852680453);
		Node nKp1 = new Node(-0.2, 0.7);

		MyVector vS = new MyVector(nC, nD);
		MyVector vK = new MyVector(nC, nK);
		MyVector vKp1 = new MyVector(nC, nKp1);

		if (!vS.isCWto(vK) && vS.isCWto(vKp1)) {
			Msg.debug("isCWto test8: riktig!");
		} else {
			Msg.debug("isCWto test8: galt!");
		}

		nC = new Node(-0.4289644776132451, 1.2568646152466634);
		nD = new Node(-0.3, 0.30000000000000004);

		nK = new Node(-0.5924654806651178, 0.8719787380936597);
		nKp1 = new Node(0.8, 1.0);

		vS = new MyVector(nC, nD);
		vK = new MyVector(nC, nK);
		vKp1 = new MyVector(nC, nKp1);

		if (!vS.isCWto(vK) && vS.isCWto(vKp1)) {
			Msg.debug("isCWto test9: riktig!");
		} else {
			Msg.debug("isCWto test9: galt!");
		}

		nC = new Node(-0.3, 1.2);
		nD = new Node(-0.3, 0.30000000000000004);
		nK = new Node(-1.3, 1.2);
		nKp1 = new Node(-0.2, 0.7);
		vS = new MyVector(nC, nD);
		vK = new MyVector(nC, nK);
		vKp1 = new MyVector(nC, nKp1);

		if (!vS.isCWto(vK) && vS.isCWto(vKp1)) {
			Msg.debug("yeah, seems to work!!");
		} else {
			Msg.debug("naaah, something wrong, yeah...");
		}

		// ----- Test double computeAngle(MyVector v) ----- //
		Msg.debug("----- Test double computeAngle(MyVector v) -----");

		MyVector va = new MyVector(p0, 1, 0);
		MyVector vb = new MyVector(p0, 0, 1);

		double angle0 = vb.computeAngle(vb);
		Msg.debug("Skal v�re 0:  " + Math.toDegrees(angle0));

		double angle1 = vb.computeAngle(va);
		Msg.debug("Skal v�re -90:  " + Math.toDegrees(angle1));

		MyVector vc = new MyVector(p0, -1, -1);
		double angle2 = vb.computeAngle(vc);
		Msg.debug("Skal v�re 135:  " + Math.toDegrees(angle2));

		MyVector vd = new MyVector(p0, 1, 1.1);
		double angle3 = vd.computeAngle(vc);
		Msg.debug("Skal v�re n�r opptil 180:  " + Math.toDegrees(angle3));

		double angle4 = vc.computeAngle(vd);
		Msg.debug("Skal v�re n�r nedtil -180:  " + Math.toDegrees(angle4));

		MyVector ve = new MyVector(p0, 1, 1);
		double angle5 = ve.computeAngle(vd);
		Msg.debug("Skal v�re ganske lite, pos.:  " + Math.toDegrees(angle5));

		// ----- Test double dot(MyVector v) ----- //
		Msg.debug("----- Test double dot(MyVector v) -----");

		double dot0 = va.dot(va);
		Msg.debug("Skal v�re 1:  " + dot0);

		double dot1 = vb.dot(va);
		Msg.debug("Skal v�re 0:  " + dot1);

		double dot2 = vb.dot(vc);
		Msg.debug("Skal v�re negativt:  " + dot2);

		double dot3 = vd.dot(vc);
		Msg.debug("Skal v�re negativt:  " + dot3);

		double dot4 = vc.dot(vd);
		Msg.debug("Skal v�re n�r negativt:  " + dot4);

		double dot5 = ve.dot(vd);
		Msg.debug("Skal v�re n�r positivt:  " + dot5);

		Msg.debug("cos(0)= " + Math.cos(0));
		Msg.debug("cos(PI)= " + Math.cos(Math.PI));
		Msg.debug("cos(PI/2)= " + Math.cos(Math.PI / 2));

		Msg.debug("cos(" + angle1 + ")= " + Math.cos(angle1));
		Msg.debug("cos(" + angle2 + ")= " + Math.cos(angle2));
		Msg.debug("cos(" + angle3 + ")= " + Math.cos(angle3));
		Msg.debug("cos(" + angle4 + ")= " + Math.cos(angle4));
		Msg.debug("cos(" + angle5 + ")= " + Math.cos(angle5));

	}

}
