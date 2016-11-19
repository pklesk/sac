package sac.examples.rectpacking;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * Abstract container for rectangles.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public abstract class Container implements Cloneable, Serializable {
	protected static final long serialVersionUID = 1L;
	protected double width;
	protected double height;

	protected Map<Point2D, Rectangle> emptyspaces;
	protected Map<Point2D, Rectangle> rectangles;

	protected double minArea = 0.0;
	protected double minEdge = 0.0;

	static ArrayList<Color> COLORS, COLORS_;
	static {
		COLORS = new ArrayList<Color>();
		COLORS_ = new ArrayList<Color>();
		Random rnd = new Random();
		for (int i = 0; i < 100; i++) {
			int g = rnd.nextInt(70);
			int b = rnd.nextInt(70);

			COLORS.add(new Color(50, 180 + g, 180 + b, 220));
			COLORS_.add(new Color(105, 120 + g, 120 + b, 200));
		}
	}

	public Container(double width, double height) {
		super();
		this.width = width;
		this.height = height;
		this.rectangles = new TreeMap<Point2D, Rectangle>();
		this.emptyspaces = new TreeMap<Point2D, Rectangle>();
		this.emptyspaces.put(new Point2D(0.0, 0.0), new Rectangle(this.width, this.height));
	}

	public boolean add(Point2D point, Rectangle rect) {
		return false;
	}

	public boolean addVertical(Point2D point, Rectangle rect) {
		return false;
	}

	public boolean addHorizontal(Point2D point, Rectangle rect) {
		return false;
	}

	public Container clone() {
		return (Container) Copier.copy(this);
	}

	public Container copy() {
		return (Container) Copier.copy(this);
	}

	public Map<Point2D, Rectangle> getRectangles() {
		return rectangles;
	}

	public Map<Point2D, Rectangle> getEmptyspaces() {
		return emptyspaces;
	}

	public double getMinArea() {
		return minArea;
	}

	public void setMinArea(double minArea) {
		this.minArea = minArea;
	}

	public double getMinEdge() {
		return minEdge;
	}

	public void setMinEdge(double minEdge) {
		this.minEdge = minEdge;
	}

	public boolean isAvailableSpace(Rectangle rect) {
		for (Rectangle emptyrect : emptyspaces.values()) {
			if (emptyrect.isLargerThen(rect)) {
				return true;
			}
		}
		return false;
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
	}

	public Rectangle getMaxEmptySpace() {
		return emptyspaces.get(getMaxEmptyPoint());
	}

	public double getUselessArea() {
		return getUselessArea(minArea, minEdge);
	}

	public abstract double getUselessArea(double minArea, double minEdge);

	public Map<Point2D, Rectangle> getUselessSpaces() {
		return getUselessSpaces(minArea, minEdge);
	}

	public abstract Map<Point2D, Rectangle> getUselessSpaces(double minArea, double minEdge);

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

	public BufferedImage toImage() {
		BufferedImage bufferedImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufferedImage.createGraphics();
		int i;
		i = 0;
		for (Point2D pos : rectangles.keySet()) {
			Rectangle rect = rectangles.get(pos);
			g.setColor(Color.GRAY);
			int x = (int) Math.round(pos.getX());
			int y = (int) Math.round(pos.getY());
			g.fillRect(x, y, (int) Math.round(rect.getWidth()), (int) Math.round(rect.getHeight()));
			g.setColor(Color.black);
			g.drawRect(x, y, (int) Math.round(rect.getWidth()), (int) Math.round(rect.getHeight()));
			g.drawString("" + i + "(" + rect.getId() + ")", x + 5, y + 10);
			i++;
		}
		i = 0;
		for (Point2D pos : emptyspaces.keySet()) {

			Rectangle rect = emptyspaces.get(pos);
			g.setColor(COLORS.get(i));
			g.fillRect((int) Math.round(pos.getX()), (int) Math.round(pos.getY()), (int) Math.round(rect.getWidth()), (int) Math.round(rect.getHeight()));
			g.setColor(Color.black);
			g.fillRect((int) Math.round(pos.getX()), (int) Math.round(pos.getY()), 3, 3);
			i++;
		}
		i = 0;
		for (Point2D pos : emptyspaces.keySet()) {
			Rectangle rect = emptyspaces.get(pos);

			g.setColor(COLORS_.get(i));
			g.drawRect((int) Math.round(pos.getX()), (int) Math.round(pos.getY()), (int) Math.round(rect.getWidth()), (int) Math.round(rect.getHeight()));
			i++;
		}
		for (Point2D pos : emptyspaces.keySet()) {
			g.setColor(new Color(20, 50, 30, 200));
			g.fillOval((int) Math.round(pos.getX()), (int) Math.round(pos.getY()), 4, 4);
			//

		}
		Random rnd = new Random();

		Point2D posm = getMaxEmptyPoint();
		Rectangle rectm = getMaxEmptySpace();
		g.setColor(Color.YELLOW);
		g.fillRect((int) Math.round(posm.getX()), (int) Math.round(posm.getY()), (int) Math.round(rectm.getWidth()), (int) Math.round(rectm.getHeight()));
		for (Point2D pos : getUselessSpaces().keySet()) {
			Rectangle rect = emptyspaces.get(pos);

			g.setColor(new Color(100 + rnd.nextInt(100), 40, 50, 200));
			g.fillRect((int) Math.round(pos.getX()), (int) Math.round(pos.getY()), (int) Math.round(rect.getWidth()), (int) Math.round(rect.getHeight()));
			g.setColor(new Color(10 + rnd.nextInt(50), 20, 30, 0));
			g.fillOval((int) Math.round(pos.getX()), (int) Math.round(pos.getY()), 4, 4);

		}

		return bufferedImage;
	}

	public void toTikz() throws IOException {
		Writer sb = new PrintWriter(System.out);
		asTikz(sb);
		sb.close();
	}

	public void asTikz(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
		asTikz(out);
		out.close();
		fos.close();
	}

	public void asTikz(Writer out) throws IOException {
		out.append("\\filldraw[fill=white, draw=black, thick] (0,0) ");
		out.append("rectangle (" + width + "," + height + ");\n");
		Point2D maxp = getMaxEmptyPoint();
		Rectangle maxs = getMaxEmptySpace();
		out.append("\\filldraw[fill=yellow, draw=black] (" + maxp.getX() + "," + maxp.getY() + ") ");
		out.append("rectangle (" + (maxp.getX() + maxs.getWidth()) + "," + (maxp.getY() + maxs.getHeight()) + ");\n");
		for (Point2D p : emptyspaces.keySet()) {
			Rectangle r = emptyspaces.get(p);
			out.append("\\draw[black, ultra thin] (" + p.getX() + "," + p.getY() + ") ");
			out.append("rectangle (" + (p.getX() + r.getWidth()) + "," + (p.getY() + r.getHeight()) + ");\n");
		}
		for (Point2D p : rectangles.keySet()) {
			Rectangle r = rectangles.get(p);
			out.append("\\filldraw[fill=lightgray, draw=darkgray, thin] (" + p.getX() + "," + p.getY() + ") ");
			out.append("rectangle (" + (p.getX() + r.getWidth()) + "," + (p.getY() + r.getHeight()) + ");\n");
			out.append("\\draw (" + p.getX() + "," + p.getY() + ") " + "node[anchor=south west]{\\tiny " + r.getId() + "};\n");
		}
		for (Point2D p : emptyspaces.keySet()) {
			out.append("\\draw[fill] (" + p.getX() + "," + p.getY() + ") circle [radius=2cm];\n");
		}
	}

	public void saveAsImage(String path) throws Exception {
		RenderedImage image = toImage();
		File file = new File(path);
		ImageIO.write(image, "png", file);
	}

	public double getWidth() {
		return this.width;
	}

	public double getHeight() {
		return this.height;
	}

	public void dumpState() {
		System.out.println("=================================================");
		System.out.println("w=" + width + ", h=" + height + ", nrect=" + this.getRectangles().size() + ", nempty=" + this.getEmptyspaces().size() + ", area="
				+ this.getArea() + ", areaUsed=" + this.getAreaUsed() + ", areaAv=" + this.getAreaAvailable());
		System.out.println("emptyoints=" + this.getEmptyspaces());
		System.out.println("rectangles=" + this.rectangles);
		System.out.println("-------------------------------------------------");
	}

	public void showState() {
		RectanglesPlotter plotter = new RectanglesPlotter(this);
		JFrame mainFrame = new JFrame("Graphics demo");
		mainFrame.getContentPane().add(plotter);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return rectangles.toString(); // lexigraphical order of tree map with rectangles
	}
}