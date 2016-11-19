package sac.examples.rectpacking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import sac.State;
import sac.StateFunction;
import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

/**
 * A packing state representing a certain arrangement of some rectangles within the container and
 * possibly containing some remaining rectangles.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class PackingState extends GraphStateImpl {

	public static int BRANCHING_BY_REMAINING_RECTANGLES = 1;
	public static int BRANCHING_BY_EMPTY_SPACES = 3;
	public static final boolean USE_FLIPFLOP = true;
	public static double HEURISTICS_HOW_FAR = 0.25;

	protected Container container;
	protected List<Rectangle> remainingRectangles;
	protected double minArea;
	protected double minEdge;
	protected double emptyArea;
	protected double areaR; // area of all rectangles
	protected double areaE; // area of wasted spaces

	public PackingState() {
		super();
	}
	
	/**
	 * 
	 * @param width a container's width
	 * @param height a container's height
	 * @param rectangles a list of initial rectangles
	 */
	public PackingState(double width, double height, List<Rectangle> rectangles) {
		setup(width, height, rectangles);
	}

	public void setup(double width, double height, List<Rectangle> rectangles) {
		this.container = new RectanglesContainer(width, height);
		this.remainingRectangles = rectangles;
		this.emptyArea = this.container.getArea();
		this.areaR = 0.0;
		for (Rectangle rectangle : rectangles) {
			this.areaR = this.areaR + rectangle.getArea();
		}
		this.areaE = container.getArea() - this.areaR;
		setMinAreaEdge();		
	}

	
	public PackingState(List<Rectangle> rectangles, Container container, double minArea, double minmaxEdge) {
		this.container = container;
		this.remainingRectangles = rectangles;
		this.minArea = minArea;
		this.minEdge = minmaxEdge;
		setMinAreaEdge();
	}

	public String printState() {
		return null;
	}
	
	public void toFile(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
		toFile(out);
		out.close();
		fos.close();
	}

	public void toFile(Writer out) throws IOException {
		out.append(this.container.getWidth() + "," + this.container.getHeight()+"\n");
 		for (Rectangle rectangle : remainingRectangles) {
 			out.append(rectangle.getWidth() + "," + rectangle.getHeight() +"\n");
 		}
		
	}
	
	public void fromFile(String fname) {		
		List<Rectangle> rectangles = new ArrayList<Rectangle>();
		try {
            BufferedReader in = new BufferedReader(new FileReader(fname));

    		String str = in.readLine();
    		String[] vals=str.split(",");  
    		
    		double width = Double.parseDouble(vals[0]);
            double height = Double.parseDouble(vals[1]);
            this.container = new RectanglesContainer(width, height);
    		int id = 0;
            while ((str = in.readLine()) != null) {
                vals=str.split(",");      
                width = Double.parseDouble(vals[0]);
                height = Double.parseDouble(vals[1]);
                //rectangles.add(new Rectangle(width, height));
                rectangles.add(new Rectangle(width, height, id++));                
            }
            this.remainingRectangles = rectangles;
    		this.emptyArea = this.container.getArea();
    		this.areaR = 0.0;
    		setMinAreaEdge();
    		for (Rectangle rectangle : rectangles ) {
    			this.areaR = this.areaR + rectangle.getArea();
    		}
            this.areaE = container.getArea() - this.areaR;
            in.close();
        } catch (IOException e) {
            System.out.println("File Read Error");
        }
	}
	
	
	public void setMinAreaEdge() {
		this.minArea = Double.MAX_VALUE;
		this.minEdge = Double.MAX_VALUE;
		for (Rectangle r : remainingRectangles) {
			if (r.getArea() < this.minArea) {
				this.minArea = r.getArea();
			}
			if (r.getMinEdge() < this.minEdge) {
				this.minEdge = r.getMinEdge();
			}
		}
		this.container.setMinArea(minArea);
		this.container.setMinEdge(minEdge);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.GraphState#generateChildren()
	 */
	@Override
	public List<GraphState> generateChildren() {
		List<GraphState> list = new ArrayList<GraphState>();
		int n = remainingRectangles.size() < BRANCHING_BY_REMAINING_RECTANGLES ? remainingRectangles.size() : BRANCHING_BY_REMAINING_RECTANGLES;
		int ni = 0;
		for (Rectangle rect : remainingRectangles) {
			Rectangle rectFlip = rect.flip();
			List<Point2D> moves = container.getAvailableSpacesByArea(rect);
			List<Point2D> movesFlip = container.getAvailableSpacesByArea(rectFlip);

			if (ni == n)
				break;
			if (moves.size() == 0 && movesFlip.size() == 0)
				continue;
			else
				ni++;

			int m = moves.size() < BRANCHING_BY_EMPTY_SPACES ? moves.size() : BRANCHING_BY_EMPTY_SPACES;
			int mFlip = movesFlip.size() < BRANCHING_BY_EMPTY_SPACES ? movesFlip.size() : BRANCHING_BY_EMPTY_SPACES;

			List<Rectangle> newrects = new ArrayList<Rectangle>(remainingRectangles.size() - 1);

			for (Rectangle r : remainingRectangles) {
				if (!r.equals(rect))
					newrects.add(r);
			}
			for (Point2D point : moves.subList(0, m)) {
				RectanglesContainer c = (RectanglesContainer) Copier.copy(container);
				//List<Point2D> cmoves = c.getAvailableSpaces(rect);
				try {
					c.add(point, rect);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				PackingState state = new PackingState(newrects, c, minArea, minEdge);
				list.add(state);
			}
			if (USE_FLIPFLOP) {
				for (Point2D point : movesFlip.subList(0, mFlip)) {
					RectanglesContainer c = (RectanglesContainer) Copier.copy(container);
					//List<Point2D> cmoves = c.getAvailableSpaces(rectFlip);
					try {
						c.add(point, rectFlip);
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
					PackingState state = new PackingState(newrects, c, minArea, minEdge);
					list.add(state);
				}
			}
		}
		return list;
	}

	protected double getRemainingRectanglesArea() {
		double area = 0.0;
		for (Rectangle rect : remainingRectangles) {
			area = area + rect.getArea();
		}
		return area;
	}

	public double getAreaR() {
		return areaR;
	}

	public double getAreaE() {
		return areaE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.GraphState#isSolution()
	 */
	@Override
	public boolean isSolution() {
		for (Rectangle rect : remainingRectangles) {
			if (this.container.isAvailableSpace(rect))
				return false;
		}
		if (USE_FLIPFLOP)
			for (Rectangle rect : remainingRectangles) {
				if (this.container.isAvailableSpace(rect.flip()))
					return false;
			}
		return true;
	}

	/**
	 * It dumps tikz representation for a given packing state to the standard output.
	 * 
	 * @throws IOException when producing the tikz file fails
	 */
	public void toTikz() throws IOException {
		Writer sb = new PrintWriter(System.out);
		asTikz(sb);
		sb.close();
	}

	/**
	 * Makes a tikz file for a given packing state.   
	 * 
	 * @param string file name
	 * @throws IOException when producing the tikz file fails
	 */
	public void asTikz(String string) throws IOException {
		asTikz(new File(string));
	}

	/**
	 * Makes a tikz file for a given packing state. 
	 * 
	 * @param file object to save the tikz to
	 * @throws IOException when producing the tikz file fails
	 */
	public void asTikz(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
		asTikz(out);
		out.close();
		fos.close();
	}

	public void asTikz(Writer out) throws IOException {
		container.asTikz(out);
		double x1 = 0.0;
		double y1 = container.getHeight() + 5.0;
		double maxh = 0;
		for (Rectangle r : remainingRectangles) {
			double x2 = x1 + r.getWidth();
			double y2 = y1 + r.getHeight();
			out.append("\\filldraw[fill=lightgray, draw=darkgray, thin] (" + x1 + "," + y1 + ") ");
			out.append("rectangle (" + x2 + "," + y2 + ");\n");
			out.append("\\draw (" + x1 + "," + y1 + ") " + "node[anchor=south west]{\\tiny " + r.getId() + "};\n");
			if (maxh < r.getHeight()) {
				maxh = r.getHeight();
			}
			if (x2 > container.getWidth()) {
				x1 = 0.0;
				y1 = y1 + maxh + 2.0;
				maxh = 0;
			} else {
				x1 = x2;
			}
		}
	}

	public void showState() {
		RectanglesPlotter plotter = new RectanglesPlotter(this.container);
		JFrame mainFrame = new JFrame("Packing state");
		mainFrame.getContentPane().add(plotter);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	public String toString() {
		return "CONTAINER: " + container.toString() + "\n" + "REMAINING: " + remainingRectangles.toString();
	}

	@Override
	public int hashCode() {
		double[] table = new double[4 * container.rectangles.size()];
		int i = 0;
		for (Point2D point : container.rectangles.keySet()) {
			Rectangle rectangle = container.rectangles.get(point);
			table[i] = point.getX();
			table[i + 1] = point.getY();
			table[i + 2] = rectangle.getWidth();
			table[i + 3] = rectangle.getHeight();
			i += 4;
		}
		return Arrays.hashCode(table);
	}

	public void greedyAdd() {
		System.out.println("gereedy add" + this.remainingRectangles);
		for (Rectangle rect : this.remainingRectangles) {
			List<Point2D> moves = container.getAvailableSpacesByArea(rect);
			System.out.println("gereedy add:" + moves);
			if (moves.size() > 0) {
				//System.out.println(">" + rect + rect.getMinEdge() + "   " + rect.getArea());
				this.container.add(moves.get(0), rect);
			} else {
				//System.out.println("===XXX======================================================" + rect.getArea() + "    " + rect.getId());
			}
		}
	}

	protected Container getContainer() {
		return container;
	}

	public void dumpState() {
		System.out.println("$$$");
		System.out.print("remaining rectangles=\n");
		for (Rectangle r : remainingRectangles) {
			System.out.println(r + ",");
		}
		System.out.println("-----------");		
		System.out.println("g = " + getG());
		System.out.println("h = " + getH());
		System.out.println("f = " + getF());
		System.out.println("AreaUsed = " + container.getAreaUsed());
		System.out.println("RemainingRectangles = " + remainingRectangles);
		System.out.println("RemainingRectanglesArea = " + getRemainingRectanglesArea());
		System.out.println("UselessArea = " + container.getUselessArea());
		System.out.println("UselessArea2 = " + container.getUselessArea(minArea, minEdge));
		System.out.println("UselessArea3 = " + (container.getArea() - container.getAreaUsed()));
		System.out.println("MaxEmptySpace = " + container.getMaxEmptySpace().getArea());
		System.out.println("minarea = " + this.minArea + "  minmaxEdge = " + this.minEdge);
		this.container.dumpState();
	}
	
	static {
		setHFunction(new StateFunction() {

			/* (non-Javadoc)
			 * @see sac.StateFunction#calculate(sac.State)
			 */
			@Override
			public double calculate(State state) {
				PackingState ps = (PackingState) state;
				return (ps.container.getAreaUsed() / ps.container.getArea() < HEURISTICS_HOW_FAR) ? ps.container.getArea() - ps.container.getAreaUsed()
						- ps.container.getMaxEmptySpace().getArea() : 0.0;
			}			
		}
		);
		
		setGFunction(new StateFunction() {
			/* (non-Javadoc)
			 * @see sac.StateFunction#calculate(sac.State)
			 */
			@Override
			public double calculate(State state) {
				PackingState ps = (PackingState) state;
				return (!ps.isSolution()) ? ps.container.getUselessArea() : ps.container.getArea() - ps.container.getAreaUsed();
			}			
			
		}
		);
	}
}