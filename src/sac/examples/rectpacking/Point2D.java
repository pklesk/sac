package sac.examples.rectpacking;

import java.io.Serializable;

/**
 * A two-dimensional point.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class Point2D implements Comparable<Point2D>, Serializable {
	private static final long serialVersionUID = 1L;
	private Double x;
	private Double y;
	private Integer layer;

	public Point2D(Double x, Double y) {
		this.x = x;
		this.y = y;
		this.layer = 0;
	}

	public Point2D(Double x, Double y, int layer) {
		this.x = x;
		this.y = y;
		this.layer = layer;
	}

	public Point2D(int i, int j) {
		this.x = (double) i;
		this.y = (double) j;
		this.layer = 0;
	}

	public Point2D(int i, int j, int layer) {
		this.x = (double) i;
		this.y = (double) j;
		this.layer = layer;
	}

	public double getX() {
		return this.x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return this.y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public Point2D add(Point2D p) {
		return new Point2D(this.x + p.getX(), this.y + p.getY());
	}

	public Point2D diff(Point2D p) {
		return new Point2D(this.x - p.getX(), this.y - p.getY());
	}

	public String toString() {
		return "(x = " + this.x + ", y = " + this.y + ")[" + layer + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Point2D o) {
		if (x.compareTo(o.getX()) == 0) {
			if (y.compareTo(o.getY()) == 0) {
				return layer.compareTo(o.getLayer());
			} else {
				return y.compareTo(o.getY());
			}

		}
		return x.compareTo(o.getX());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return equals((Point2D) obj);
	}

	public boolean equals(Point2D point) {
		return (this.x == point.getX()) && (this.y == point.getY()) && (this.layer == point.getLayer());
	}
}
