package com.github.karllevik.qmorph.geom;

/** A very simple implementation of a dart. */
public class Dart {
	public Dart() {
		n = null;
		e = null;
		elem = null;
	}

	public Dart(Node n, Edge e, Element elem) {
		this.n = n;
		this.e = e;
		this.elem = elem;
	}

	public String descr() {
		return "(elem: " + elem.descr() + ", e: " + e.descr() + ", n: " + n.descr() + ")";
	}

	public Node n;
	public Edge e;
	public Element elem;

}
