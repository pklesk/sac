package sac.examples.rectpacking;

import java.util.Map;

/**
 * Set of utility methods related to rectangles.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class RectanglesUtils {
	public static boolean isPointInRectangle(Point2D p1, Rectangle r1, Point2D p2) {
		return (p1.getX() < p2.getX()) && (p1.getY() < p2.getY()) && (p2.getX() < p1.getX() + r1.getWidth()) && (p2.getY() < p1.getY() + r1.getHeight());
	}

	public static boolean isRectanglesOverlaping(Point2D p1, Rectangle r1, Point2D p2, Rectangle r2) {
		return (p1.getX() < p2.getX() + r2.getWidth()) && (p1.getX() + r1.getWidth() > p2.getX()) && (p1.getY() < p2.getY() + r2.getHeight())
				&& (p1.getY() + r1.getHeight() > p2.getY());
	}

	public static Object[] rectanglesIntersection(Point2D p1, Rectangle r1, Point2D p2, Rectangle r2) {
		double x1 = Math.max(p1.getX(), p2.getX());
		double y1 = Math.max(p1.getY(), p2.getY());
		double x2 = Math.min(p1.getX() + r1.getWidth(), p2.getX() + r2.getWidth());
		double y2 = Math.min(p1.getY() + r1.getHeight(), p2.getY() + r2.getHeight());
		return new Object[] { new Point2D(x1, y1), new Rectangle(Math.max(0, x2 - x1), Math.max(0.0, y2 - y1)) };
	}

	public static boolean isRectanglesOverlaping(Map<Point2D, Rectangle> map, Point2D p, Rectangle rect) {
		for (Point2D pi : map.keySet()) {
			if (isRectanglesOverlaping(pi, map.get(pi), pi, rect)) {
				return false;
			}
		}
		return false;
	}

	public static boolean isRectangleContained(Point2D p1, Rectangle r1, Point2D p2, Rectangle r2) {
		Object[] result = rectanglesIntersection(p1, r1, p2, r2);
		Rectangle intersection = (Rectangle) result[1];
		return r1.getArea() == intersection.getArea();
	}

	public static double getAreaIntersectionUpperBound(Map<Point2D, Rectangle> map, Point2D p, Rectangle rect) {
		double area = 0;
		for (Point2D pi : map.keySet()) {
			Object[] result = rectanglesIntersection(pi, map.get(pi), p, rect);
			area = area + ((Rectangle) result[1]).getArea();
		}
		return Math.min(rect.getArea(), area);
	}
}