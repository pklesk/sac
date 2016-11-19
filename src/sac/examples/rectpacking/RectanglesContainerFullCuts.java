package sac.examples.rectpacking;

import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;

/**
 * A variant of container allowing only for full cuts (cuts going through the whole width or height
 * of the container).
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class RectanglesContainerFullCuts extends Container {

	private static final long serialVersionUID = 1L;

	public RectanglesContainerFullCuts(double width, double height) {
		super(width, height);
	}

	public boolean addVertical(Point2D point, Rectangle rect) {
		Rectangle space = emptyspaces.get(point);
		emptyspaces.remove(point);
		Point2D p1 = new Point2D(point.getX() + rect.getWidth(), point.getY());
		Rectangle s1 = new Rectangle(space.getWidth() - rect.getWidth(), space.getHeight());
		Point2D p2 = new Point2D(point.getX(), point.getY() + rect.getHeight());
		Rectangle s2 = new Rectangle(rect.getWidth(), space.getHeight() - rect.getHeight());
		emptyspaces.put(p1, s1);
		emptyspaces.put(p2, s2);
		rectangles.put(point, rect);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.examples.rectpacking.Container#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + emptyspaces.toString();
	}

	public boolean addHorizontal(Point2D point, Rectangle rect) {
		Rectangle space = emptyspaces.get(point);
		emptyspaces.remove(point);
		Point2D p1 = new Point2D(point.getX() + rect.getWidth(), point.getY());
		Rectangle s1 = new Rectangle(space.getWidth() - rect.getWidth(), rect.getHeight());
		Point2D p2 = new Point2D(point.getX(), point.getY() + rect.getHeight());
		Rectangle s2 = new Rectangle(space.getWidth(), space.getHeight() - rect.getHeight());
		emptyspaces.put(p1, s1);
		emptyspaces.put(p2, s2);
		rectangles.put(point, rect);
		return true;
	}

	public void showState() {
		RectanglesPlotter plotter = new RectanglesPlotter(this);
		JFrame mainFrame = new JFrame("");
		mainFrame.getContentPane().add(plotter);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.examples.rectpacking.Container#getUselessArea(double, double)
	 */
	@Override
	public double getUselessArea(double minArea, double minEdge) {
		double area = 0.0;
		for (Rectangle space : this.emptyspaces.values()) {
			if ((space.getArea() < minArea) || (space.getMinEdge() < minEdge)) {
				area = area + space.getArea();
			}
		}
		return area;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.examples.rectpacking.Container#getUselessSpaces(double, double)
	 */
	@Override
	public Map<Point2D, Rectangle> getUselessSpaces(double minArea, double minEdge) {
		Map<Point2D, Rectangle> spaces = new TreeMap<Point2D, Rectangle>();
		for (Point2D p : this.emptyspaces.keySet()) {
			Rectangle rect = emptyspaces.get(p);
			if (rect.getArea() < minArea || rect.getMinEdge() < minEdge) {
				spaces.put(p, rect);
			}
		}
		return spaces;
	}
}