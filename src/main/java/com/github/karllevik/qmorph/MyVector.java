package com.github.karllevik.qmorph;

/*
import Ray;
import Constants;
import Msg;
*/
import java.math.BigDecimal;

/**
 * This class holds information for vectors, and has methods for dealing with
 * vector-related issues.
 */

public class MyVector extends Constants {
	/**
	 * @param origin the origin of the vector
	 * @param x      the x component
	 * @param y      the y component
	 */
	public MyVector(Node origin, double x, double y) {
		this.origin = origin;
		this.x = x;
		this.y = y;
	}

	/**
	 * "Convert" a ray into a vector.
	 *
	 * @param r      a ray (we use the origin and direction found in this ray)
	 * @param length the length of the vector
	 */
	public MyVector(Ray r, double length) {
		origin = r.origin;
		x = length * r.x;
		y = length * r.y;
	}

	/**
	 * @param angle  the angle of the vector
	 * @param length the length of the vector
	 * @param origin the origin of the vector
	 */
	public MyVector(double angle, double length, Node origin) {
		this.origin = origin;
		this.x = length * Math.cos(angle);
		this.y = length * Math.sin(angle);
	}

	/**
	 * @param a the origin of the vector
	 * @param b the endpoint of the vector
	 */
	public MyVector(Node a, Node b) {
		this.origin = a;
		this.x = b.x - a.x;
		this.y = b.y - a.y;
	}

	private final static BigDecimal zero = new BigDecimal(0.0);
	private final static BigDecimal one = new BigDecimal(1.0);

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MyVector)) {
			return false;
		}

		MyVector v = (MyVector) o;
		if (origin.x != v.origin.x || origin.y != v.origin.y) {
			return false;
		}

		if (x / y != v.x / v.y) {
			return false;
		} else {
			return true;
		}
	}

	/** @return length of vector */
	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	/**
	 * @return angle relative to the x-axis (which is directed from the origin (0,0)
	 *         to the right, btw) in the range (-180, 180) in radians.
	 */
	public double angle() {
		// Math.acos returns values in the range 0.0 through pi, avoiding neg numbers.
		// If this is CW to x-axis, then return pos acos, else return neg acos
		if (x == 0 && y > 0) {
			return Math.PI / 2.0;
		} else if (x == 0 && y < 0) {
			return -Math.PI / 2.0;
		} else if (y == 0 && x > 0) {
			return 0;
		} else if (y == 0 && x < 0) {
			return Math.PI;
		} else {
			// double cLen= Math.sqrt(x*x + y*y);
			double aLen = Math.sqrt(x * x + y * y);
			if (y > 0) {
				return Math.acos((aLen * aLen + x * x - y * y) / (2 * aLen * x));
			} else {
				return -Math.acos((aLen * aLen + x * x - y * y) / (2 * aLen * x));
			}
		}
	}

	/**
	 * @return angle relative to the x-axis (which is directed from the origin (0,0)
	 *         to the right, btw) in the range (0, 360) in radians.
	 */
	public double posAngle() {
		// Math.acos returns values in the range 0.0 through pi, avoiding neg numbers.
		// If this is CW to x-axis, then return a number in the range pi through 2*pi
		if (x == 0 && y > 0) {
			return PIdiv2;
		} else if (x == 0 && y < 0) {
			return PIx3div2;
		} else if (y == 0 && x > 0) {
			return 0;
		} else if (y == 0 && x < 0) {
			return Math.PI;
		} else {
			// double cLen= Math.sqrt(x*x + y*y);
			double aLen = Math.sqrt(x * x + y * y);
			if (y > 0) {
				return Math.acos((aLen * aLen + x * x - y * y) / (2 * aLen * x));
			} else {
				return PIx2 - Math.acos((aLen * aLen + x * x - y * y) / (2 * aLen * x));
			}
		}
	}

	public void setLengthAndAngle(double length, double angle) {
		this.x = length * Math.cos(angle);
		this.y = length * Math.sin(angle);
	}

	/** The origin is really overlooked here... I just use this.origin ... */
	public MyVector plus(MyVector v) {
		return new MyVector(origin, x + v.x, y + v.y);
	}

	public MyVector minus(MyVector v) {
		return new MyVector(origin, x - v.x, y - v.y);
	}

	public MyVector mul(double d) {
		return new MyVector(origin, d * x, d * y);
	}

	public MyVector div(double d) {
		return new MyVector(origin, x / d, y / d);
	}

	/**
	 * @param v another vector
	 * @return the smalles positive angle between this and v in radians
	 */
	public double computePosAngle(MyVector v) {
		double aLen = Math.sqrt(x * x + y * y);
		double bLen = Math.sqrt(v.x * v.x + v.y * v.y);

		Node cOrigin = new Node(origin.x + x, origin.y + y);
		Node cEnd = new Node(v.origin.x + v.x, v.origin.y + v.y);

		double cxLen = cOrigin.x - cEnd.x;
		double cyLen = cOrigin.y - cEnd.y;
		double cLen = Math.sqrt(cxLen * cxLen + cyLen * cyLen);

		return Math.acos((aLen * aLen + bLen * bLen - cLen * cLen) / (2 * aLen * bLen));
	}

	/**
	 * @param v another vector
	 * @return the true angle between this and v in radians or NaN if length of this
	 *         or v is 0.
	 */
	public double computeAngle(MyVector v) {
		double aLen = Math.sqrt(x * x + y * y);
		double bLen = Math.sqrt(v.x * v.x + v.y * v.y);

		Node cOrigin = new Node(origin.x + x, origin.y + y);
		Node cEnd = new Node(v.origin.x + v.x, v.origin.y + v.y);

		double cxLen = cOrigin.x - cEnd.x;
		double cyLen = cOrigin.y - cEnd.y;
		double cLen = Math.sqrt(cxLen * cxLen + cyLen * cyLen);

		// Math.acos returns values in the range 0.0 through pi, avoiding neg numbers.
		// If this is CW to v, then return pos acos, else return neg acos
		if (this.isCWto(v)) {
			return Math.acos((aLen * aLen + bLen * bLen - cLen * cLen) / (2 * aLen * bLen));
		} else {
			return -Math.acos((aLen * aLen + bLen * bLen - cLen * cLen) / (2 * aLen * bLen));
		}
	}

	/**
	 * Compute the dot product
	 *
	 * @param v another vector
	 * @return the dot product defined as |this|*|v|*cos(theta)
	 */
	public double dot(MyVector v) {
		// Must use computePosAngle because the dot product is commutative
		double theta = computePosAngle(v); // computeAngle(v);
		double aLen = Math.sqrt(x * x + y * y);
		double bLen = Math.sqrt(v.x * v.x + v.y * v.y);
		// System.out.println("theta== "+theta+" aLen= "+aLen+" bLen= "+bLen);
		// System.out.println("Math.cos(theta)== "+Math.cos(theta));
		// System.out.println("aLen*bLen*Math.cos(theta)=="+aLen*bLen*Math.cos(theta));
		return aLen * bLen * Math.cos(theta);
	}

	/**
	 * Compute the dot product. Much faster and more accurate than the
	 * MyVector.dot(MyVector) method.
	 *
	 * @param vx the x component of the other vector
	 * @param vy the y component of the other vector
	 * @return the dot product defined as |this|*|v|*cos(theta)
	 */
	public double dot(double vx, double vy) {
		// Must use computePosAngle because the dot product is commutative
		double sqraLen = x * x + y * y;
		double aLen = Math.sqrt(sqraLen);
		double sqrbLen = vx * vx + vy * vy;
		double bLen = Math.sqrt(sqrbLen);

		double cxLen = x - vx;
		double cyLen = y - vy;
		double sqrcLen = cxLen * cxLen + cyLen * cyLen;

		double temp = (sqraLen + sqrbLen - sqrcLen) / (2 * aLen * bLen);
		return aLen * bLen * temp;
	}

	/** Return true if the cross product is greater than zero */
	public boolean newcross(MyVector v) {
		BigDecimal d0x = new BigDecimal(x), d0y = new BigDecimal(y), d1x = new BigDecimal(v.x), d1y = new BigDecimal(v.y);
		BigDecimal d0Xd1 = d0x.multiply(d1y).subtract(d1x.multiply(d0y));
		if (d0Xd1.compareTo(zero) == 1) {
			return true;
		} else {
			return false;
		}
	}

	/** Compute the cross product */
	public double cross(MyVector v) {
		return x * v.y - v.x * y;
	}

	/** Compute the cross product */
	public double cross(Ray r) {
		return x * r.y - r.x * y;
	}

	/**
	 * Methods to assist stupid Math.acos(..). Tested ok (simple test) Vectors with
	 * equal slopes *ARE* considered to be cw to each other.
	 *
	 * @param v another vector
	 * @return a boolean indicating whether this vector is clockwise (cw) to v
	 */
	public boolean isCWto(MyVector v) {
	    if (this.equals(v)) {
	        return false; // A vector cannot be CW to itself
	    }
		double thisR, vR;

		if (x != 0) {
			thisR = y / x;
		} else {
			thisR = Double.NEGATIVE_INFINITY;
		}

		if (v.x != 0) {
			vR = v.y / v.x;
		} else {
			vR = Double.NEGATIVE_INFINITY;
		}

		if (x > 0 && y >= 0) { // ----- First quadrant -----
			if (v.x > 0 && v.y >= 0) { // First quadrant
				if (thisR <= vR) {
					return true;
				} else {
					return false;
				}
			} else if (v.x <= 0 && v.y > 0) { // Second quadrant
				return true;
			} else if (v.x < 0 && v.y <= 0) { // Third quadrant
				if (thisR >= vR) {
					return true;
				} else {
					return false;
				}
			} else { // Third quadrant
				return false;
			}
		} else if (x <= 0 && y > 0) { // ----- Second quadrant -----
			if (v.x > 0 && v.y >= 0) { // First quadrant
				return false;
			} else if (v.x <= 0 && v.y > 0) { // Second quadrant
				if (thisR <= vR) {
					return true;
				} else {
					return false;
				}
			} else if (v.x < 0 && v.y <= 0) { // Third quadrant
				return true;
			} else {// if (v.x>=0 && v.y< 0) { // Fourth quadrant
				if (thisR >= vR) {
					return true;
				} else {
					return false;
				}
			}
		} else if (x < 0 && y <= 0) { // ----- Third quadrant -----
			if (v.x > 0 && v.y >= 0) { // First quadrant
				if (thisR >= vR) {
					return true;
				} else {
					return false;
				}
			} else if (v.x <= 0 && v.y > 0) { // Second quadrant
				return false;
			} else if (v.x < 0 && v.y <= 0) { // Third quadrant
				if (thisR <= vR) {
					return true;
				} else {
					return false;
				}
			} else { // Third quadrant
				return true;
			}

		} else { // if (x>=0 && y< 0) { // ----- Fourth quadrant -----
			if (v.x > 0 && v.y >= 0) { // First quadrant
				return true;
			} else if (v.x <= 0 && v.y > 0) { // Second quadrant
				if (thisR >= vR) {
					return true;
				} else {
					return false;
				}
			} else if (v.x < 0 && v.y <= 0) { // Third quadrant
				return false;
			} else {// if (v.x>=0 && v.y< 0) { // Fourth quadrant
				if (thisR <= vR) { // <=
					return true;
				} else { // <=
					return false;
				}
			}
		}
	}

	// Note: java.awt.geom.Line2D has an intersects method.. but no intersectsAt
	// method
	// Stupid!!!! So I had to make this... blood, sweat and tears, my friend...
	// Method derived from Intersection of Linear and Circular Components in 2D
	// Local copy at ~karll/hfag/teori/IntersectionLin2Cir2.pdf

	/**
	 * @return if vectors intersects at one point exactly, return a node for this
	 *         point Else return null.
	 */
	public Node pointIntersectsAt(MyVector d1) {
		Node p0 = origin, p1 = d1.origin;
		MyVector delta = new MyVector(p0, p1.x - p0.x, p1.y - p0.y);
		MyVector d0 = this;
		double d0crossd1 = d0.cross(d1);

		if (d0crossd1 == 0) {
			return null; // Does not intersect at all OR the same MyVector
		} else {
			double s = delta.cross(d1) / d0crossd1;
			double t = delta.cross(d0) / d0crossd1;
			if (t < 0 || t > 1 || s < 0 || s > 1) {
				return null; // Intersects not at an edge point (but extension)
			} else {
				double x = d1.origin.x + t * d1.x;
				double y = d1.origin.y + t * d1.y;
				Node poi = new Node(x, y);
				return poi; // Intersects at this edge point
			}
		}
	}

	/** @return true if this and d1 intersect, else false. */
	public boolean intersects(MyVector d1) {
		Node p0 = origin, p1 = d1.origin;
		MyVector delta = new MyVector(p0, p1.x - p0.x, p1.y - p0.y);
		MyVector d0 = this;
		double d0crossd1 = d0.cross(d1);

		if (d0crossd1 == 0) { // Non-intersecting and parallel OR intersect in an interval
			if (delta.cross(d0) == 0) { // Parallel and on the same line?

				// Non-intersecting if both start and end of d0 is greater than or
				// less than both start and end of d1.

				// If x is okay to use...
				if (d0.origin.x != d1.origin.x || d0.origin.x != d1.origin.x + d1.x) {
					double d0sx = d0.origin.x, d0ex = d0.origin.x + d0.x;
					double d1sx = d1.origin.x, d1ex = d1.origin.x + d1.x;
					if ((d0sx < d1sx && d0sx < d1ex && d0ex < d1sx && d0ex < d1ex) || (d0sx > d1sx && d0sx > d1ex && d0ex > d1sx && d0ex > d1ex)) {
						return false;
					} else {
						return true;
					}
				} else { // ...no, use y instead
					double d0sy = d0.origin.y, d0ey = d0.origin.y + d0.y;
					double d1sy = d1.origin.y, d1ey = d1.origin.y + d1.y;
					if ((d0sy < d1sy && d0sy < d1ey && d0ey < d1sy && d0ey < d1ey) || (d0sy > d1sy && d0sy > d1ey && d0ey > d1sy && d0ey > d1ey)) {
						return false;
					} else {
						return true;
					}
				}

			} else {
				Msg.debug(this.descr() + " doesn't intersect " + d1.descr() + " (1)");
				return false;
			}
		} else {
			double s = delta.cross(d1) / d0crossd1;
			double t = delta.cross(d0) / d0crossd1;
			Msg.debug("s: " + s);
			Msg.debug("t: " + t);

			if (t < 0 || t > 1 || s < 0 || s > 1) {// Intersects not at an Edge point
				Msg.debug(this.descr() + " doesn't intersect " + d1.descr() + " (2)");
				return false; // (but on the lines extending the Edges)
			} else {
				Msg.debug(this.descr() + " intersects " + d1.descr() + " (2)");
				return true; // Intersects at an Edge point
			}
		}
	}

	/** @return true if this and d1 intersect in a single point, else false. */
	public boolean pointIntersects(MyVector d1) {
		Node p0 = origin, p1 = d1.origin;
		MyVector delta = new MyVector(p0, p1.x - p0.x, p1.y - p0.y);
		MyVector d0 = this;
		double d0crossd1 = d0.cross(d1);

		if (d0crossd1 == 0) { // Non-intersecting and parallel OR intersect in an interval
			Msg.debug(this.descr() + " doesn't point intersect " + d1.descr() + " (1)");
			return false;
		} else {
			double s = delta.cross(d1) / d0crossd1;
			double t = delta.cross(d0) / d0crossd1;

			if (t < 0 || t > 1 || s < 0 || s > 1) {// Intersects not at an Edge point
				Msg.debug(this.descr() + " doesn't point intersect " + d1.descr() + " (2)");
				return false; // (but on the lines extending the Edges)
			} else { // Intersects not at an Edge point
				return true; // Intersects at an Edge point
			}
		}
	}

	/**
	 * @return true if this and d1 intersect in a inner point of each other, (that
	 *         is, a point that is not an endpoint, ) else return false.
	 */
	public boolean innerpointIntersects(MyVector d1) {
		Node p0 = origin, p1 = d1.origin;
		MyVector delta = new MyVector(p0, p1.x - p0.x, p1.y - p0.y);
		MyVector d0 = this;

		if (d0.origin.equals(d1.origin) || (d0.origin.x == d1.origin.x + d1.x && d0.origin.y == d1.origin.y + d1.y)
				|| (d0.origin.x + d0.x == d1.origin.x && d0.origin.y + d0.y == d1.origin.y)
				|| (d0.origin.x + d0.x == d1.origin.x + d1.x && d0.origin.y + d0.y == d1.origin.y + d1.y)) {
			Msg.debug(descr() + " doesn't intersect innerpoint of " + d1.descr() + " (0)");
			return false;
		}

		double d0crossd1 = d0.cross(d1);

		if (d0crossd1 == 0) { // Non-intersecting and parallel OR intersect in an interval
			Msg.debug(this.descr() + " doesn't intersect in inner point of " + d1.descr() + " (1)");
			return false;
		} else {
			double s = delta.cross(d1) / d0crossd1;
			double t = delta.cross(d0) / d0crossd1;

			Msg.debug("innerpointIntersects(..): s: " + s);
			Msg.debug("innerpointIntersects(..): t: " + t);

			if (t <= 0 || t >= 1 || s <= 0 || s >= 1) {
				Msg.debug(this.descr() + " doesn't innerintersect " + d1.descr() + " (2)");
				return false; // Intersects not at an Edge point (but possibly an interval)
			} else {
				return true; // Intersects at an Edge point
			}
		}
	}

	/*
	 * // Return true if this and d1 intersect in a inner point of each other, //
	 * (that is, a point that is not an endpoint, ) // else return false. public
	 * boolean innerpointIntersects(MyVector d1) { Node p0= origin, p1= d1.origin;
	 * MyVector delta= new MyVector(p0, p1.x- p0.x, p1.y- p0.y); MyVector d0= this;
	 * 
	 * BigDecimal d0x= new BigDecimal(d0.x), d0y= new BigDecimal(d0.y), d1x= new
	 * BigDecimal(d1.x), d1y= new BigDecimal(d1.y); BigDecimal d0Xd1=
	 * d0x.multiply(d1y).subtract(d1x.multiply(d0y)); if (d0Xd1.compareTo(zero)== 0)
	 * { // Non-intersecting and parallel OR intersect in an interval
	 * Msg.debug(this.descr()+" doesn't intersect in inner point of "+d1.descr()
	 * +" (1)"); return false; } else { BigDecimal deltax= new BigDecimal(delta.x),
	 * deltay= new BigDecimal(delta.y); BigDecimal deltaXd1=
	 * deltax.multiply(d1y).subtract(d1x.multiply(deltay)); BigDecimal deltaXd0=
	 * deltax.multiply(d0y).subtract(d0x.multiply(deltay));
	 * 
	 * BigDecimal s= deltaXd1.divide(d0Xd1, BigDecimal.ROUND_UP); BigDecimal t=
	 * deltaXd0.divide(d0Xd1, BigDecimal.ROUND_UP);
	 * 
	 * Msg.debug("innerpointIntersects(..): s: "+s.toString());
	 * Msg.debug("innerpointIntersects(..): t: "+t.toString());
	 * 
	 * if (t.compareTo(zero)<=0 || t.compareTo(one)>=0 || s.compareTo(zero)<=0 ||
	 * s.compareTo(one)>=0) {
	 * Msg.debug(this.descr()+" doesn't innerintersect "+d1.descr()+" (2)"); return
	 * false; // Intersects not at an Edge point (but possibly an interval) } else
	 * return true; // Intersects at an Edge point } }
	 */

	/** @return a string representation of this vector. */
	public String descr() {
		return origin.descr() + ", (" + (x + origin.x) + ", " + (y + origin.y) + ")";
	}

	/** Print a string representation of this vector. */
	public void printMe() {
		System.out.println(descr());
	}

	Node origin;
	double x, y;
	Edge edge;
}
