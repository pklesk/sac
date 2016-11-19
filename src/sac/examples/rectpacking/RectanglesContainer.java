package sac.examples.rectpacking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>) <br>
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class RectanglesContainer extends Container implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	private double width;
	private double height;

	private TreeMap<Double, Set<Point2D>> pointsXidx;
	private TreeMap<Double, Set<Point2D>> pointsYidx;

	private TreeMap<Double, Set<Point2D>> rectXidx;
	private TreeMap<Double, Set<Point2D>> rectYidx;

	public RectanglesContainer clone() {
		return (RectanglesContainer) Copier.copy(this);
	}

	public RectanglesContainer copy() {
		return (RectanglesContainer) Copier.copy(this);
	}

	public RectanglesContainer(double width, double height) {
		super(width, height);
		this.width = width;
		this.height = height;
		this.pointsXidx = new TreeMap<Double, Set<Point2D>>();
		this.pointsYidx = new TreeMap<Double, Set<Point2D>>();
		this.rectXidx = new TreeMap<Double, Set<Point2D>>();
		this.rectYidx = new TreeMap<Double, Set<Point2D>>();
		this.rectangles = new TreeMap<Point2D, Rectangle>();
		this.emptyspaces = new TreeMap<Point2D, Rectangle>();
		Point2D Zero = new Point2D(0.0, 0.0);
		pointsXidx.put(0.0, new TreeSet<Point2D>(Arrays.asList(Zero)));
		pointsYidx.put(0.0, new TreeSet<Point2D>(Arrays.asList(Zero)));
		// rectXidx.put(0.0, new TreeSet<Point2D>());
		// rectYidx.put(0.0, new TreeSet<Point2D>());
		this.getEmptyspaces().put(new Point2D(0.0, 0.0), new Rectangle(this.width, this.height));
	}

	private Set<Point2D> getPointsBetweenX(Double x0, Double x1, boolean toKey) {
		Map<Double, Set<Point2D>> xbetweenMap = pointsXidx.subMap(x0, true, x1, toKey);
		Set<Point2D> pointsBetween = new TreeSet<Point2D>();
		for (Double xi : xbetweenMap.keySet()) {
			pointsBetween.addAll(xbetweenMap.get(xi));
		}
		return pointsBetween;
	}

	private Set<Point2D> getPointsBetweenY(Double y0, Double y1, boolean toKey) {
		Map<Double, Set<Point2D>> ybetweenMap = pointsYidx.subMap(y0, true, y1, toKey);
		Set<Point2D> pointsBetween = new TreeSet<Point2D>();
		for (Double yi : ybetweenMap.keySet()) {
			pointsBetween.addAll(ybetweenMap.get(yi));
		}
		return pointsBetween;
	}

	private Set<Point2D> getPointsBetweenXY(Double x0, Double x1, boolean toKey, Double y0, Double y1, boolean toKey2) {
		Set<Point2D> pointsx = getPointsBetweenX(x0, x1, toKey);
		Set<Point2D> pointsy = getPointsBetweenY(y0, y1, toKey2);
		pointsx.retainAll(pointsy);
		return pointsx;
	}

	private void addPointXidx(Double x, Point2D p) {
		if (pointsXidx.containsKey(x)) {
			pointsXidx.get(x).add(p);
		} else {
			pointsXidx.put(x, new TreeSet<Point2D>(Arrays.asList(p)));
		}
	}

	private void addPointYidx(Double y, Point2D p) {
		if (pointsYidx.containsKey(y)) {
			pointsYidx.get(y).add(p);
		} else {
			pointsYidx.put(y, new TreeSet<Point2D>(Arrays.asList(p)));
		}
	}

	private void addRectXidx(Double x, Point2D p) {
		if (rectXidx.containsKey(x)) {
			rectXidx.get(x).add(p);
		} else {
			rectXidx.put(x, new TreeSet<Point2D>(Arrays.asList(p)));
		}
	}

	private void addRectYidx(Double y, Point2D p) {
		if (rectYidx.containsKey(y)) {
			rectYidx.get(y).add(p);
		} else {
			rectYidx.put(y, new TreeSet<Point2D>(Arrays.asList(p)));
		}
	}

	private void removePointXidx(Double x, Point2D p) {
		pointsXidx.get(x).remove(p);
	}

	public void removePointYidx(Double y, Point2D p) {
		pointsYidx.get(y).remove(p);
	}

	private void removeEmptyspace(Point2D xy) {
		// System.out.println("remove emptyspace:" + xy);
		removePointXidx(xy.getX(), xy);
		removePointYidx(xy.getY(), xy);
		emptyspaces.remove(xy);
	}

	private void addAndUpdateEmptyspaces(Point2D xy, Rectangle rect) {
		// System.out.println("addAndUpdateEmptyspaces:" + xy + rect);
		double w0 = rect.getWidth();
		double h0 = rect.getHeight();
		Double x0 = xy.getX();
		Double y0 = xy.getY();

		Set<Point2D> betweenxy = getPointsBetweenXY(0.0, x0, false, 0.0, y0, false);
		// System.out.println(">>.," + betweenxy);
		for (Point2D p : betweenxy) {
			Rectangle emptyRect = emptyspaces.get(p);
			// System.out.println("---" + xy);
			// System.out.println("---" + p + emptyRect);
			if (emptyRect == null) {
				System.out.println("!!" + p);
			}
			if (p.getX() + emptyRect.getWidth() > xy.getX() && p.getY() + emptyRect.getHeight() > xy.getY()) {
				double emptyWidth = emptyRect.getWidth();
				//double emptyHeight = emptyRect.getHeight();
				double px = p.getX();
				//double py = p.getY();
				double dx = xy.getX() - px;
				if (dx > 0) {
					emptyRect.setWidth(dx);
				} else {
					removeEmptyspace(p);
				}

				Point2D p2 = new Point2D(p.getX(), p.getY());
				Rectangle r2 = new Rectangle(emptyWidth, xy.getY() - p.getY());
				// System.out.println("2-" + p2 + r2);
				if (r2.getHeight() > 0 && r2.getWidth() > 0) {
					addEmptyspace(p2, r2);
					// System.out.println("2-" + p2 + r2);
				}
			}
		}

		Set<Point2D> betweeny = getPointsBetweenXY(0.0, x0, true, y0, y0 + h0, false);
		// System.out.println("=================================");
		// System.out.println(">>.," + x0 + " " + y0);
		// System.out.println(">>.," + w0 + " " + h0);
		// System.out.println(">>.," + x0 + " " + y0 + " " + (y0+h0) + betweeny);
		// System.out.println(">>:," + emptyspaces);
		for (Point2D p : betweeny) {
			Rectangle emptyRect = emptyspaces.get(p);
			// System.out.println("=========emptyRect:" + p+ emptyRect);
			if (emptyRect == null) {
				continue;
				// System.out.println(p);
				// System.out.println(xy);
				// System.out.println(emptyspaces);
				// System.out.println(pointsXidx);
				// System.out.println(pointsYidx);
				// System.out.println(getAvailableSpaces(rect));
				// this.showState();
				// throw new RuntimeException("aaa");
			}
			if (p.getX() + emptyRect.getWidth() > xy.getX() && p.getY() + emptyRect.getHeight() > xy.getY()) {
				// if (p.getX() + emptyRect.getWidth() > xy.getX() &&
				// p.getY() + emptyRect.getHeight() > xy.getY()) {
				double emptyWidth = emptyRect.getWidth();
				double emptyHeight = emptyRect.getHeight();
				double px = p.getX();
				double py = p.getY();
				double dx = xy.getX() - px;
				double dy = py - xy.getY();
				if (dx > 0) {
					emptyRect.setWidth(dx);
					// System.out.println("emptyRect:" + p+ emptyRect);
				} else {
					// System.out.println(":" + p );
					removeEmptyspace(p);
				}
				// System.out.println("emptyRect:" + p+ emptyRect);
				Point2D p1 = new Point2D(px, xy.getY() + rect.getHeight());
				Rectangle r1 = new Rectangle(emptyWidth, emptyHeight - (rect.getHeight() - dy));
				if (r1.getHeight() > 0 && r1.getWidth() > 0) {
					addEmptyspace(p1, r1);
				}
				if ((px + emptyWidth) > (xy.getX() + rect.getWidth())) {
					Point2D p2 = new Point2D(xy.getX() + rect.getWidth(), py);
					Rectangle r2 = new Rectangle(emptyWidth - dx - rect.getWidth(), emptyHeight);
					addEmptyspace(p2, r2);
				}
			}
		}

		Set<Point2D> betweenx = getPointsBetweenXY(x0, x0 + w0, false, 0.0, y0, true);
		// System.out.println(">>."+ betweenx);
		for (Point2D p : betweenx) {
			Rectangle emptyRect = emptyspaces.get(p);
			if ((p.getX() + emptyRect.getWidth() > xy.getX()) && p.getY() + emptyRect.getHeight() > xy.getY()) {
				double emptyWidth = emptyRect.getWidth();
				double emptyHeight = emptyRect.getHeight();
				double px = p.getX();
				double py = p.getY();
				double dx = px - xy.getX();
				double dy = xy.getY() - py;
				if (dy > 0) {
					emptyRect.setHeight(dy);
				} else {
					removeEmptyspace(p);
				}
				Point2D p3 = new Point2D(xy.getX() + rect.getWidth(), py);
				Rectangle r3 = new Rectangle(emptyWidth - (rect.getWidth() - dx), emptyHeight);
				if (r3.getHeight() > 0 && r3.getWidth() > 0) {
					addEmptyspace(p3, r3);
				}
				if ((py + emptyHeight) > (xy.getY() + rect.getHeight())) {
					Point2D p4 = new Point2D(px, xy.getY() + rect.getHeight());
					Rectangle r4 = new Rectangle(emptyWidth, emptyHeight - dy - rect.getHeight());
					addEmptyspace(p4, r4);
				}
			}
		}
		betweenxy = getPointsBetweenXY(x0, x0 + w0, false, y0, y0 + h0, false);
		// System.out.println(">>.....," + betweenxy);
		for (Point2D p : betweenxy) {
			Rectangle emptyRect = emptyspaces.get(p);
			if (xy.getX() + rect.getWidth() > p.getX() && xy.getY() + rect.getHeight() > p.getY()) {
				double emptyWidth = emptyRect.getWidth();
				double emptyHeight = emptyRect.getHeight();

				Point2D p1 = new Point2D(x0 + w0, p.getY());
				Rectangle r1 = new Rectangle(emptyWidth - (p1.getX() - p.getX()), emptyHeight);
				Point2D p2 = new Point2D(p.getX(), y0 + h0);
				Rectangle r2 = new Rectangle(emptyWidth, emptyHeight - (p2.getY() - p.getY()));
				removeEmptyspace(p);
				// System.out.println(":::" + p);
				if (r1.getHeight() > 0 && r1.getWidth() > 0) {
					addEmptyspace(p1, r1);
				}
				if (r2.getHeight() > 0 && r2.getWidth() > 0) {
					addEmptyspace(p2, r2);
				}
			}
		}
	}

	private void addRectangle(Point2D point, Rectangle rect) {
		// System.out.println("add rectangle:" + point + rect);
		rectangles.put(point, rect);
		addRectXidx(point.getX(), point);
		addRectYidx(point.getY(), point);
	}

	// public void cleanX(Double x) {
	// Set<Point2D> points = pointsXidx.get(x);
	// List<Point2D> list;
	// if (points!=null) {
	// list = new ArrayList<Point2D>(points);
	// //System.out.println("::list=" + list);
	//
	// } else {
	// return;
	// }
	// for (int i = list.size()-1; i > 0; i--) {
	// /* x
	// * |
	// * p1 +------+r1
	// * | |
	// * p2 +--+r2 |
	// * | | |
	// */
	// Point2D p1 = list.get(i-1);
	// Point2D p2 = list.get(i);
	// Rectangle r1 = emptyspaces.get(p1);
	// Rectangle r2 = emptyspaces.get(p2);
	// // if (p1.getY() <= p2.getY() &&
	// // p2.getY() <= p1.getY()+ r1.getHeight() &&
	// // r2.getWidth()<= r1.getWidth()) {
	// if (GeoUtils.isRectangleContained(p2, r2, p1, r1)) {
	// removeEmptyspace(p2);
	// //System.out.println("clean=" + p2+r2 + "  include in" + p1+r1);
	// //System.out.println("clean=" + GeoUtils.isRectangleContained(p2, r2, p1, r1));
	// }
	// }
	// }

	public void cleanXY(Point2D p) {
		Set<Point2D> pointsx = pointsXidx.get(p.getX());
		Set<Point2D> pointsy = pointsYidx.get(p.getY());
		List<Point2D> listx;
		if (pointsx != null) {
			listx = new ArrayList<Point2D>(pointsx);
			for (int i = listx.size() - 1; i > 0; i--) {
				/*
				 * x | p1 +------+r1 | | p2 +--+r2 | | | |
				 */
				Point2D p1 = listx.get(i - 1);
				Point2D p2 = listx.get(i);
				Rectangle r1 = emptyspaces.get(p1);
				Rectangle r2 = emptyspaces.get(p2);
				// if (p1.getY() <= p2.getY() &&
				// p2.getY() <= p1.getY()+ r1.getHeight() &&
				// r2.getWidth()<= r1.getWidth()) {
				if (RectanglesUtils.isRectangleContained(p2, r2, p1, r1)) {
					removeEmptyspace(p2);
					// System.out.println("cleanX=" + p2+r2 + "  include in" + p1+r1);
					// System.out.println("cleanX=" + GeoUtils.isRectangleContained(p2, r2, p1,
					// r1));
				}
			}

		}
		if (pointsy != null) {
			List<Point2D> listy = new ArrayList<Point2D>(pointsy);
			// System.out.println("::list=" + listy);

			for (int i = listy.size() - 1; i > 0; i--) {
				/*
				 * x | p1 +------+r1 | | p2 +--+r2 | | | |
				 */
				Point2D p1 = listy.get(i - 1);
				Point2D p2 = listy.get(i);
				Rectangle r1 = emptyspaces.get(p1);
				Rectangle r2 = emptyspaces.get(p2);
				// if (p1.getY() <= p2.getY() &&
				// p2.getY() <= p1.getY()+ r1.getHeight() &&
				// r2.getWidth()<= r1.getWidth()) {
				if (RectanglesUtils.isRectangleContained(p2, r2, p1, r1)) {
					removeEmptyspace(p2);
					// System.out.println("cleanY=" + p2 + r2 + "  include in" + p1
					// + r1);
					// System.out.println("cleanY="
					// + GeoUtils.isRectangleContained(p2, r2, p1, r1));
				}
			}
		}
	}

	public boolean emptyspaceContainsX(Point2D point, Rectangle rect) {
		Set<Point2D> points = pointsXidx.get(point.getX());
		if (points == null)
			return false;
		for (Point2D xy : points) {
			Rectangle rectxy = emptyspaces.get(xy);
			if (xy.getY() <= point.getY() && point.getY() <= xy.getY() + rectxy.getHeight() && rect.getWidth() <= rectxy.getWidth()) {
				System.out.println("--X:" + point + rect + " in " + xy + rectxy);
				return true;
			}

		}
		return false;
	}

	public boolean emptyspaceContainsY(Point2D point, Rectangle rect) {
		Set<Point2D> points = pointsYidx.get(point.getY());
		if (points == null)
			return false;
		for (Point2D xy : points) {
			Rectangle rectxy = emptyspaces.get(xy);

			if (xy.getX() <= point.getX() && point.getX() <= xy.getX() + rectxy.getWidth() && rect.getHeight() <= rectxy.getHeight()) {
				System.out.println("--Y:" + point + rect + " in " + xy + rectxy);
				return true;
			}

		}
		return false;
	}

	private void addEmptyspace(Point2D point, Rectangle rect) {
		// System.out.println("contained: " +point+rect+
		// GeoUtils.isRectanglesOverlaping(emptyspaces, point, rect));

		// if (!emptyspaceContainsX(point, rect) && !emptyspaceContainsY(point, rect)) {

		// System.out.println("--: " + emptyspaces);
		if (!RectanglesUtils.isRectanglesOverlaping(emptyspaces, point, rect)) {
			if (!emptyspaces.containsKey(point)) {
				emptyspaces.put(point, rect);
				addPointXidx(point.getX(), point);
				addPointYidx(point.getY(), point);
				// System.out.println("added space: " + point + rect);
			} else {
				point.setLayer(point.getLayer() + 1);
				emptyspaces.put(point, rect);
				addPointXidx(point.getX(), point);
				addPointYidx(point.getY(), point);
				// System.out.println("added layer: " + point + rect);
			}
		} else {
			// System.out.println("contained----: " + point);
		}
		// System.out.println("--: " + emptyspaces);
		// //cleanX(point.getX());
		cleanXY(point);
		// System.out.println("--: " + emptyspaces);
		// System.out.println("-----------------------");

	}

	public boolean add(Point2D point, Rectangle rect) {
		// dodalemy prostokat do kolekcji
		// rect.setId(this.rectangles.size()+1);
		addRectangle(point, rect);
		// uaktualnimy indeksy i dostepne obszary
		addAndUpdateEmptyspaces(point, rect);
		return true;
	}

	public List<Point2D> getAvailableSpaces(Rectangle rect) {
		List<Point2D> list = new ArrayList<Point2D>();
		for (Point2D point : getEmptyspaces().keySet()) {
			Rectangle emptyrect = getEmptyspaces().get(point);
			if (emptyrect.isLargerThen(rect)) {
				list.add(point);
			}
		}
		return list;
	}

	public Point2D getMaxEmptyPoint() {

		return Collections.max(emptyspaces.keySet(), new Comparator<Point2D>() {
			@Override
			public int compare(Point2D p0, Point2D p1) {
				Rectangle r0 = getEmptyspaces().get(p0);
				Rectangle r1 = getEmptyspaces().get(p1);
				return Double.compare(r0.getArea(), r1.getArea());
			}
		});
		// return Collections.min(this.emptyspaces.values(), new RectangleComparatorByArea());
	}

	public Rectangle getMaxEmptySpace() {
		return emptyspaces.get(getMaxEmptyPoint());
		// return Collections.min(this.emptyspaces.values(), new RectangleComparatorByArea());
		// return emptyspaces.get(getAvailableSpacesByArea(new ).get(0));
	}

	@Override
	public Map<Point2D, Rectangle> getUselessSpaces(double minArea, double minEdge) {
		TreeMap<Point2D, Rectangle> uselessSpaces = new TreeMap<Point2D, Rectangle>();
		for (Point2D p : this.emptyspaces.keySet()) {
			Rectangle rect = emptyspaces.get(p);
			if (rect.getArea() < minArea && rect.getMinEdge() < minEdge) {
				uselessSpaces.put(p, rect);
			}
		}
		return uselessSpaces;
	}

	@Override
	public double getUselessArea(double minArea, double minEdge) {
		double area = 0.0;
		TreeMap<Point2D, Rectangle> uselessSpaces = new TreeMap<Point2D, Rectangle>();
		for (Point2D p : this.emptyspaces.keySet()) {
			Rectangle rect = emptyspaces.get(p);
			if ((rect.getArea() < minArea) || (rect.getMinEdge() < minEdge)) {
				double areaIntersectionUpperBound = RectanglesUtils.getAreaIntersectionUpperBound(uselessSpaces, p, rect);
				area = area + rect.getArea() - areaIntersectionUpperBound;
				uselessSpaces.put(p, rect);
			}
		}
		return area;
	}

	public List<Point2D> getAvailableSpacesByArea(Rectangle rect) {
		List<Point2D> list = this.getAvailableSpaces(rect);
		Collections.sort(list, new Comparator<Point2D>() {
			@Override
			public int compare(Point2D p0, Point2D p1) {
				Rectangle r0 = getEmptyspaces().get(p0);
				Rectangle r1 = getEmptyspaces().get(p1);
				return Double.compare(r0.getArea(), r1.getArea());
			}
		});
		return list;
	}

	public List<Point2D> getAvailableSpacesByMinEdge(Rectangle rect) {
		List<Point2D> list = this.getAvailableSpaces(rect);
		Collections.sort(list, new Comparator<Point2D>() {
			@Override
			public int compare(Point2D p0, Point2D p1) {
				Rectangle r0 = getEmptyspaces().get(p0);
				Rectangle r1 = getEmptyspaces().get(p1);
				return Double.compare(r0.getMinEdge(), r1.getMinEdge());
			}
		});
		return list;
	}

	public List<Point2D> getAvailableSpacesByMaxEdge(Rectangle rect) {
		List<Point2D> list = this.getAvailableSpaces(rect);
		Collections.sort(list, new Comparator<Point2D>() {
			@Override
			public int compare(Point2D p0, Point2D p1) {
				Rectangle r0 = getEmptyspaces().get(p0);
				Rectangle r1 = getEmptyspaces().get(p1);
				return Double.compare(r0.getMaxEdge(), r1.getMaxEdge());
			}
		});
		return list;
	}

	public double getArea() {
		return width * height;
	}

	public double getAreaUsed() {
		double area = 0.0;
		for (Point2D position : rectangles.keySet()) {
			area += rectangles.get(position).getArea();
		}
		return area;
	}

	public double getAreaAvailable() {
		return getArea() - getAreaUsed();
	}

	// public BufferedImage toImage() {
	// BufferedImage bufferedImage = new BufferedImage((int) width,
	// (int) height, BufferedImage.TYPE_INT_RGB);
	// Graphics2D g = bufferedImage.createGraphics();
	// int i;
	// i = 0;
	// for (Point2D pos : rectangles.keySet()) {
	// Rectangle rect = rectangles.get(pos);
	// g.setColor(Color.DARK_GRAY);
	// g.fillRect((int) Math.round(pos.getX()),
	// (int) Math.round(pos.getY()),
	// (int) Math.round(rect.getWidth()),
	// (int) Math.round(rect.getHeight()));
	// g.setColor(Color.black);
	// g.drawRect((int) Math.round(pos.getX()),
	// (int) Math.round(pos.getY()),
	// (int) Math.round(rect.getWidth()),
	// (int) Math.round(rect.getHeight()));
	// }
	// i = 0;
	// for (Point2D pos : emptyspaces.keySet()) {
	//
	// Rectangle rect = emptyspaces.get(pos);
	// //g.setColor(new Color(70, 150, 30, 10));
	// g.setColor(COLORS.get(i));
	//
	// //System.out.println("pos=" + pos);
	// //System.out.println("rect=" + rect);
	// g.fillRect((int) Math.round(pos.getX()),
	// (int) Math.round(pos.getY()),
	// (int) Math.round(rect.getWidth()),
	// (int) Math.round(rect.getHeight()));
	// g.setColor(Color.black);
	// g.fillRect((int) Math.round(pos.getX()),
	// (int) Math.round(pos.getY()), 3, 3);
	// i++;
	// }
	// i = 0;
	// for (Point2D pos : emptyspaces.keySet()) {
	// Rectangle rect = emptyspaces.get(pos);
	// //g.setColor(new Color(70, 150, 30, 10));
	//
	// g.setColor(COLORS_.get(i));
	// g.drawRect((int) Math.round(pos.getX()),
	// (int) Math.round(pos.getY()),
	// (int) Math.round(rect.getWidth()),
	// (int) Math.round(rect.getHeight()));
	//
	// // g.setColor(new Color(20, 50, 30, 200));
	// // g.fillOval((int) Math.round(pos.getX()),
	// // (int) Math.round(pos.getY()), 4, 4);
	// //
	//
	// i++;
	// }
	//
	// return bufferedImage;
	// }
	/*
	 * public void toTikz() throws IOException { Writer sb = new PrintWriter(System.out);
	 * asTikz(sb); sb.close(); } public void asTikz(File file) throws IOException { FileOutputStream
	 * fos = new FileOutputStream(file); OutputStreamWriter out = new OutputStreamWriter(fos,
	 * "UTF-8"); asTikz(out); out.close(); fos.close(); }
	 */
	// public void asTikz(Writer out) throws IOException {
	// //out.append("\\begin{tikzpicture}[scale=0.02]\n");
	// out.append("\\filldraw[fill=white, draw=black, thick] (0,0) ");
	// out.append("rectangle (" + width + "," + height + ");\n");
	// Point2D maxp = getMaxEmptyPoint();
	// Rectangle maxs = getMaxEmptySpace();
	// out.append("\\filldraw[fill=yellow, draw=black] (" + maxp.getX() + "," + maxp.getY() + ") ");
	// //sb.append("\\draw (" + p.getX() + "," + p.getY() + ") ");
	// out.append("rectangle (" + (maxp.getX() + maxs.getWidth()) + "," + (maxp.getY() +
	// maxs.getHeight()) + ");\n");
	// for (Point2D p : emptyspaces.keySet()) {
	// Rectangle r = emptyspaces.get(p);
	// //sb.append("\\draw[fill] (" + p.getX() + "," + p.getY() + ") circle [radius=0.2cm];\n");
	// out.append("\\draw[black, ultra thin] (" + p.getX() + "," + p.getY() + ") ");
	// //sb.append("\\draw (" + p.getX() + "," + p.getY() + ") ");
	// out.append("rectangle (" + (p.getX() + r.getWidth()) + "," + (p.getY() + r.getHeight()) +
	// ");\n");
	// }
	// for (Point2D p : rectangles.keySet()) {
	// Rectangle r = rectangles.get(p);
	// out.append("\\filldraw[fill=lightgray, draw=darkgray, thin] (" + p.getX() + "," + p.getY() +
	// ") ");
	// //sb.append("\\draw (" + p.getX() + "," + p.getY() + ") ");
	// out.append("rectangle (" + (p.getX() + r.getWidth()) + "," + (p.getY() + r.getHeight()) +
	// ");\n");
	// out.append("\\draw (" + p.getX() + "," + p.getY() + ") " +
	// "node[anchor=south west]{\\tiny " + r.getId() + "};\n"); //"(" + r.getArea() + ")
	// }
	// for (Point2D p : emptyspaces.keySet()) {
	// Rectangle r = emptyspaces.get(p);
	// out.append("\\draw[fill] (" + p.getX() + "," + p.getY() + ") circle [radius=2cm];\n");
	// //out.append("\\draw[fill] (" + (p.getX() + r.getWidth()) + "," + (p.getY() + r.getHeight())
	// + ") circle [radius=2cm];\n");
	// }
	//
	// //out.append("\\end{tikzpicture}");
	// }
	/*
	 * 
	 * 
	 * 
	 * public void saveAsImage(String path) throws Exception { RenderedImage image = toImage(); File
	 * file = new File(path); ImageIO.write(image, "png", file); }
	 * 
	 * public double getWidth() { return this.width; }
	 * 
	 * public double getHeight() { return this.height; }
	 */
	public void dumpState() {
		System.out.println("=================================================");
		System.out.println("w=" + width + ", h=" + height + ", nrect=" + this.getRectangles().size() + ", nempty=" + this.getEmptyspaces().size() + ", area="
				+ this.getArea() + ", areaUsed=" + this.getAreaUsed() + ", areaAv=" + this.getAreaAvailable());
		System.out.println(rectangles);
		System.out.println("pointsXidx=" + this.pointsXidx);
		System.out.println("pointsYidx=" + this.pointsYidx);
		System.out.println("rectxidx=" + this.rectXidx);
		System.out.println("rectyidx=" + this.rectYidx);
		System.out.println("emptyoints=" + this.getEmptyspaces());
		System.out.println("rectangles=" + this.rectangles);
		System.out.println("-------------------------------------------------");
	}

	public Map<Point2D, Rectangle> getRectangles() {
		return rectangles;
	}

	public Map<Point2D, Rectangle> getEmptyspaces() {
		return emptyspaces;
	}

	public TreeMap<Double, Set<Point2D>> getXidx() {
		return pointsXidx;
	}

	public TreeMap<Double, Set<Point2D>> getYidx() {
		return pointsYidx;
	}
}
