package sac.examples.rectpacking;

import java.io.Serializable;

/**
 * A rectangle.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class Rectangle implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private double width;
	private double height;

	public Rectangle(double width, double height) {
		super();
		this.id = 0;
		this.width = width;
		this.height = height;
	}

	public Rectangle(double width, double height, int id) {
		super();
		this.id = id;
		this.width = width;
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public Double getMinEdge() {
		return height > width ? width : height;
	}
	
	public double getMaxEdge() {
		return height > width ? height : width;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public Rectangle flip() {
		return new Rectangle(height, width, id);
	}
	
	public double getArea() {
		return this.width * this.height;
	}

	public Point2D getPoint2D() {
		return new Point2D(this.width, this.height);
	}

	public boolean isLargerThen(Rectangle smaller) {
		return (smaller.getWidth() < this.width) && (smaller.getHeight() < this.height);
	}
	
	public String toString() {
		return "[w = " + this.width + ", h = " + this.height + ", a = " + getArea() + "]";
	}
	public String toTkz(Point2D p) {
		return "\\draw (" + p.getX() + "," + p.getY() + ")" +  "rectangle (" + p.getX() + "," + p.getY() + ");";   
	}
}
