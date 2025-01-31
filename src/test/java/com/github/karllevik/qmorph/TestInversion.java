package com.github.karllevik.qmorph;

import com.github.karllevik.qmorph.geom.Edge;
import com.github.karllevik.qmorph.geom.Quad;
import com.github.karllevik.qmorph.geom.Triangle;

class TestInversion {

	public static void main(String[] args) {
		Node n1 = new Node(0.0, 0.0);
		Node n2 = new Node(1.0, 0.0);
		Node n3 = new Node(0.0, 1.0);
		Node n4 = new Node(1.0, 1.0);

		Node oldN3 = n3.copyXY();
		Node oldN4 = n4.copyXY();
		Node newN3 = new Node(0.0, 2.0);
		Node newN4 = new Node(1.0, -1.0);

		n3.setXY(newN3); // *Should not* invert quad...
		n4.setXY(newN4); // *Should* invert quad...

		Edge e1 = new Edge(n1, n2);
		Edge e2 = new Edge(n1, n3);
		Edge e3 = new Edge(n2, n4);
		Edge e4 = new Edge(n3, n4);

		Quad q = new Quad(e1, e2, e3, e4);

		/*
		 * if (q.inverted(oldN3, n3)) Msg.error("Shit, there's something wrong"); else
		 * Msg.debug("Yessss, seems to work...newN3 didn't invert quad!!!");
		 * 
		 * if (q.inverted(oldN4, n4))
		 * Msg.debug("Yessss, seems to work...newN4 inverted quad!!!"); else
		 * Msg.error("Shit, there's something wrong");
		 * 
		 */

		// n3.setXY(oldN3);
		Edge e5 = new Edge(n2, n3);

		Triangle t = new Triangle(e1, e2, e5);
		Msg.debug("e1= " + e1.descr());
		Msg.debug("e2= " + e2.descr());
		Msg.debug("e5= " + e5.descr());

		Msg.debug("angle between e1 and e2: " + Math.toDegrees(t.angle(e1, e1.commonNode(e2))));
		Msg.debug("angle between e2 and e5: " + Math.toDegrees(t.angle(e2, e2.commonNode(e5))));
		Msg.debug("angle between e1 and e5: " + Math.toDegrees(t.angle(e1, e1.commonNode(e5))));

		Msg.debug("angle between e2 and e1: " + Math.toDegrees(t.angle(e2, e2.commonNode(e1))));
		Msg.debug("angle between e5 and e2: " + Math.toDegrees(t.angle(e5, e5.commonNode(e2))));
		Msg.debug("angle between e5 and e1: " + Math.toDegrees(t.angle(e5, e5.commonNode(e1))));

		Msg.debug("angleIndex(e2, e1)= " + t.angleIndex(e2, e1));
		Msg.debug("angleIndex(e5, e2)= " + t.angleIndex(e5, e2));
		Msg.debug("angleIndex(e5, e1)= " + t.angleIndex(e5, e1));

		Msg.debug("ang[0]= " + Math.toDegrees(t.ang[0]));
		Msg.debug("ang[1]= " + Math.toDegrees(t.ang[1]));
		Msg.debug("ang[2]= " + Math.toDegrees(t.ang[2]));

	}

}
