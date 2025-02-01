package com.github.karllevik.qmorph.geom;

import com.github.karllevik.qmorph.Msg;

/**
 * This class holds information for rays, and has methods for dealing with
 * ray-related issues. The purpose of this class is solely to determine the
 * intersection point between a ray (origin and direction) and a vector (origin
 * and x,y giving the direction, the length of the ray is considered to be
 * infinite).
 */

public class Ray {

	// origin: the point where the ray starts
	// relEdge: an edge that the angle is relative to
	// angle: the angle relative to relEdge
	public Ray(Node origin, Edge relEdge, double angle) {
		this.origin = origin;
		double temp = relEdge.angleAt(origin);
		Msg.debug("relEdge.angleAt(origin)==" + Math.toDegrees(temp) + " degrees");
		double ang = temp + angle;
		Msg.debug("Ray(..): relEdge.angleAt(origin)+ angle== " + Math.toDegrees(ang) + " degrees");

		this.x = Math.cos(ang);
		this.y = Math.sin(ang);
	}

	// origin: the point where the ray starts
	// angle: the angle relative to relEdge
	// angle is relative to the x-axis
	public Ray(Node origin, double angle) {
		this.origin = origin;
		this.x = Math.cos(angle);
		this.y = Math.sin(angle);
	}

	// origin: the point where the ray starts
	// passThrough: a point which the ray passes through
	public Ray(Node origin, Node passThrough) {
		this.origin = origin;
		double tempx = passThrough.x - origin.x;
		double tempy = passThrough.y - origin.y;
		double hyp = Math.sqrt(tempx * tempx + tempy * tempy);

		this.x = tempx / hyp;
		this.y = tempy / hyp;
	}

	public double cross(MyVector v) {
		return x * v.y - v.x * y;
	}

	public Node pointIntersectsAt(MyVector d1) {
		Node p0 = origin, p1 = d1.origin;
		MyVector delta = new MyVector(p0, p1.x - p0.x, p1.y - p0.y);
		Ray d0 = this;
		double d0crossd1 = d0.cross(d1);

		if (d0crossd1 == 0) {
			if (delta.cross(d0) == 0) { // Parallel
				if (d0.origin.equals(d1.origin) || (d0.origin.x == d1.origin.x + d1.x && d0.origin.y == d1.origin.y + d1.y)) {
					return d0.origin;
				}
			}
			return null;
		} else {
			double s = delta.cross(d1) / d0crossd1;
			double t = delta.cross(d0) / d0crossd1;
			if (t < 0 || t > 1 || s < 0) {
				return null; // Intersects not at a ray/vector point
			} else {
				double x = d1.origin.x + t * d1.x;
				double y = d1.origin.y + t * d1.y;
				return new Node(x, y); // Intersects at this ray point
			}
		}
	}

	public String values() {
		return origin.descr() + ", x= " + x + ", y= " + y;
	}

	public String descr() {
		return origin.descr() + ", (" + (x + origin.x) + ", " + (y + origin.y) + ")";
	}

	public void printMe() {
		System.out.println(descr());
	}

	public double x, y; // hyp= 1= sqrt(x^2 + y^2)
	public Node origin;
}
