package sac.examples.rectpacking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sac.graph.GraphState;

/**
 * A variant of packing state in which only full cuts can be made (cuts going through the whole
 * width or height of the container).
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class PackingStateFullCuts extends PackingState {

	public PackingStateFullCuts() {
		super();
	}
	
	public PackingStateFullCuts(double width, double height, List<Rectangle> rectangles) {
		super(width, height, rectangles);

		container = new RectanglesContainerFullCuts(width, height);
		setMinAreaEdge();
	}	
	
	/* (non-Javadoc)
	 * @see sac.examples.rectpacking.PackingState#setup(double, double, java.util.List)
	 */
	@Override
	public void setup(double width, double height, List<Rectangle> rectangles) {
		this.container = new RectanglesContainerFullCuts(width, height);
		this.remainingRectangles = rectangles;
		this.emptyArea = this.container.getArea();
		this.areaR = 0.0;
		for (Rectangle rectangle : rectangles) {
			this.areaR = this.areaR + rectangle.getArea();
		}
		this.areaE = container.getArea() - this.areaR;
		setMinAreaEdge();
	}

	public PackingStateFullCuts(List<Rectangle> rectangles, Container container, double minArea, double minmaxEdge) {
		this.container = (RectanglesContainerFullCuts) container;
		remainingRectangles = rectangles;
		this.minArea = minArea;
		this.minEdge = minmaxEdge;
		setMinAreaEdge();
	}


	/* (non-Javadoc)
	 * @see sac.examples.rectpacking.PackingState#generateChildren()
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
			int m = moves.size() < BRANCHING_BY_EMPTY_SPACES ? moves.size() : BRANCHING_BY_EMPTY_SPACES;
			int mFlip = movesFlip.size() < BRANCHING_BY_EMPTY_SPACES ? movesFlip.size() : BRANCHING_BY_EMPTY_SPACES;

			if (ni == n)
				break;
			if ((moves.size() == 0) && (movesFlip.size() == 0)) {
			} else {
				ni++;
				List<Rectangle> newrects = new ArrayList<Rectangle>(remainingRectangles.size() - 1);

				for (Rectangle r : remainingRectangles) {
					if (!r.equals(rect)) {
						newrects.add(r);
					}
				}
				for (Point2D point : moves.subList(0, m)) {
					RectanglesContainerFullCuts c1 = (RectanglesContainerFullCuts) Copier.copy(container);
					RectanglesContainerFullCuts c2 = (RectanglesContainerFullCuts) Copier.copy(container);

					c1.addHorizontal(point, rect);
					c2.addVertical(point, rect);

					PackingStateFullCuts state1 = new PackingStateFullCuts(newrects, c1, minArea, minEdge);
					PackingStateFullCuts state2 = new PackingStateFullCuts(newrects, c2, minArea, minEdge);
					list.add(state1);
					list.add(state2);
				}
				if (USE_FLIPFLOP) {
					for (Point2D point : movesFlip.subList(0, mFlip)) {
						RectanglesContainerFullCuts c1 = (RectanglesContainerFullCuts) Copier.copy(container);
						RectanglesContainerFullCuts c2 = (RectanglesContainerFullCuts) Copier.copy(container);

						c1.addHorizontal(point, rectFlip);
						c2.addVertical(point, rectFlip);

						PackingStateFullCuts state1 = new PackingStateFullCuts(newrects, c1, minArea, minEdge);
						PackingStateFullCuts state2 = new PackingStateFullCuts(newrects, c2, minArea, minEdge);
						list.add(state1);
						list.add(state2);
					}
				}
			}
		}
		return list;
	}

	public void greedyAdd() {
		System.out.println("greedy add" + this.remainingRectangles);
		for (Rectangle rect : this.remainingRectangles) {
			List<Point2D> moves = container.getAvailableSpacesByArea(rect);
			System.out.println("gereedy add:" + moves);
			if (moves.size() > 0) {
				System.out.println(">" + rect + rect.getMinEdge() + "   " + rect.getArea());
				((RectanglesContainerFullCuts) this.container).addVertical(moves.get(0), rect);
			} else {
				System.out.println("===XXX======================================================" + rect.getArea() + "    " + rect.getId());
			}
		}
	}

	@Override
	public int hashCode() {
		double[] table = new double[4 * container.rectangles.size() + 4 * container.emptyspaces.size()];
		int i = 0;
		for (Point2D point : container.rectangles.keySet()) {
			Rectangle rectangle = container.rectangles.get(point);
			table[i] = point.getX();
			table[i + 1] = point.getY();
			table[i + 2] = rectangle.getWidth();
			table[i + 3] = rectangle.getHeight();
			i += 4;
		}

		for (Point2D point : container.emptyspaces.keySet()) {
			Rectangle rectangle = container.emptyspaces.get(point);
			table[i] = point.getX();
			table[i + 1] = point.getY();
			table[i + 2] = rectangle.getWidth();
			table[i + 3] = rectangle.getHeight();
			i += 4;
		}
		return Arrays.hashCode(table);
	}
}
