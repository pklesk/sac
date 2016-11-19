package sac.examples.rectpacking;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import javax.swing.JPanel;

import sac.graph.AStar;
import sac.graph.BestFirstSearch;
import sac.graph.Dijkstra;
import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphSearchConfigurator;
import sac.graphviz.GraphSearchGraphvizer;

public class RectanglesTester extends JPanel {

	private static final long serialVersionUID = 1L;
	RectanglesContainer container;

	public RectanglesTester() {
		super();
	}

	public RectanglesTester(RectanglesContainer container) {
		super();
		this.container = container;
	}

	public void paintComponent(Graphics g) {
		try {
			g.drawImage(this.container.toImage(), 0, 0, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension((int) Math.round(this.container.getWidth()), (int) Math.round(this.container.getHeight()));
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	private List<Rectangle> getList1(int n) {
		Random rnd = new Random(0L);
		List<Rectangle> list = new ArrayList<Rectangle>();
		for (int i = 0; i < n; i++) {
			// list.add(new Rectangle(rnd.nextInt(90)+40, rnd.nextInt(60)+40, i));
			list.add(new Rectangle(rnd.nextInt(100) + 40, rnd.nextInt(100) + 40, i));
		}
		return list;
	}

	@SuppressWarnings("unused")
	private Collection<Rectangle> getListMin(int n) {
		List<Rectangle> list = getList1(n);
		PriorityQueue<Rectangle> queue = new PriorityQueue<Rectangle>(list.size(), new RectangleComparatorByMinEdge());
		queue.addAll(list);
		List<Rectangle> list2 = new ArrayList<Rectangle>(n);
		int i = 0;
		while (!queue.isEmpty()) {
			i++;
			Rectangle r = queue.poll();
			r.setId(i);
			list2.add(r);
		}
		return list2;
	}

	@SuppressWarnings("unused")
	private Collection<Rectangle> getListMax(int n) {
		List<Rectangle> list = getList1(n);

		PriorityQueue<Rectangle> queue = new PriorityQueue<Rectangle>(list.size(), new RectangleComparatorByMaxEdge());
		queue.addAll(list);
		List<Rectangle> list2 = new ArrayList<Rectangle>(n);
		int i = 0;
		while (!queue.isEmpty()) {
			i++;
			Rectangle r = queue.poll();
			r.setId(i);
			list2.add(r);
		}
		return list2;
	}

	private Collection<Rectangle> getListArea(int n) {
		List<Rectangle> list = getList1(n);

		PriorityQueue<Rectangle> queue = new PriorityQueue<Rectangle>(list.size(), new RectangleComparatorByArea());
		queue.addAll(list);
		List<Rectangle> list2 = new ArrayList<Rectangle>(n);
		int i = 0;
		while (!queue.isEmpty()) {
			i++;
			Rectangle r = queue.poll();
			r.setId(i);
			list2.add(r);
		}
		return list2;
	}

	@SuppressWarnings("unused")
	private List<Rectangle> getList2() {
		Rectangle r1 = new Rectangle(40, 100);
		Rectangle r2 = new Rectangle(100, 100);
		Rectangle r3 = new Rectangle(30, 290);
		Rectangle r4 = new Rectangle(90, 90);
		Rectangle r5 = new Rectangle(80, 80);
		Rectangle r6 = new Rectangle(350, 60);
		Rectangle r7 = new Rectangle(200, 200);
		Rectangle r8 = new Rectangle(50, 50);
		List<Rectangle> list = new ArrayList<Rectangle>();
		list.add(r1);
		list.add(r2);
		list.add(r5);
		list.add(r6);
		list.add(r7);
		list.add(r8);
		return list;
	}

	@SuppressWarnings("unused")
	private List<Rectangle> getList3() {
		Rectangle r1 = new Rectangle(180, 80);
		Rectangle r2 = new Rectangle(102, 120);
		Rectangle r3 = new Rectangle(110, 110);
		Rectangle r4 = new Rectangle(200, 210);
		Rectangle r5 = new Rectangle(300, 200);

		List<Rectangle> list = new ArrayList<Rectangle>();
		list.add(r1);
		list.add(r2);
		list.add(r3);
		list.add(r4);
		list.add(r5);
		return list;
	}

	@SuppressWarnings("unused")
	private void greedyAdd(Collection<Rectangle> list) throws IOException, InterruptedException {

		for (Rectangle rect : list) {
			List<Point2D> moves = container.getAvailableSpacesByMinEdge(rect);
			if (moves.size() > 0) {
				System.out.println(rect.getMinEdge() + "   " + rect.getArea());
				this.container.add(moves.get(0), rect);
			} else {
				System.out.println("===========================================================" + rect.getArea() + "    " + rect.getId());
			}
		}
	}

	@SuppressWarnings("unused")
	private void greedyAdd2(Collection<Rectangle> list) throws IOException, InterruptedException {

		for (Rectangle rect : list) {
			List<Point2D> moves = container.getAvailableSpacesByMinEdge(rect);
			if (moves.size() > 0) {
				System.out.println(rect.getMinEdge() + "   " + rect.getArea());
				this.container.add(moves.get(0), rect);
			} else {
				System.out.println("===========================================================" + rect.getArea() + "    " + rect.getId());
			}
		}
	}

	@SuppressWarnings("unused")
	private void bfs(Collection<Rectangle> list) throws Exception {
		PackingState state = new PackingState(400.0, 300.0, new ArrayList<Rectangle>(list));
		state.dumpState();
		state.asTikz(new File("files/tex/prezentacjaWIZUT2013/tikz/tmpstate.tikz"));
		GraphSearchConfigurator configurator = new GraphSearchConfigurator("./graph_configurator.mk.properties");
		GraphSearchAlgorithm algorithm = new BestFirstSearch(state, configurator);
		// state.generateChildren();
		algorithm.execute();
		PackingState solution = (PackingState) algorithm.getSolutions().get(0);
		solution.asTikz(new File("files/tex/prezentacjaWIZUT2013/tikz/tmpbfs.tikz"));
		GraphSearchGraphvizer.go(algorithm, "files/dot/rectpacking/tmpbfs.dot", false, false);
		solution.dumpState();

	}

	@SuppressWarnings("unused")
	private void astarFull(Collection<Rectangle> list) throws Exception {
		PackingStateFullCuts state = new PackingStateFullCuts(400.0, 300.0, new ArrayList<Rectangle>(list));
		state.dumpState();
		state.asTikz(new File("./files/tex/userguide/tikz/init2.tikz"));
		GraphSearchConfigurator configurator = new GraphSearchConfigurator("./graph_configurator.mk.properties");
		GraphSearchAlgorithm algorithm = new Dijkstra(state, configurator);
		// state.generateChildren();
		algorithm.execute();
		PackingState solution;
		if (algorithm.getSolutions().size() > 0) {
			solution = (PackingState) algorithm.getSolutions().get(0);
		} else {
			solution = (PackingState) algorithm.getBestSoFar();
		}
		// PackingState solution = (PackingState) algorithm.getSolutions().get(0);
		solution.asTikz("./files/tex/userguide/tikz/final2.tikz");
		// solution.asTikz("./files/tex/konfSMCLodz2013/tikz/squares.tikz");
		// GraphSearchGraphvizer.go(algorithm, "tmpastar.dot",
		// GraphvizStateShapeType.POINT_WITH_NO_CONTENT);
		solution.dumpState();
		solution.showState();
		System.out.println(configurator);
	}

	private void astar(Collection<Rectangle> list) throws Exception {
		//PackingState state = new PackingState(400.0, 300.0, new ArrayList<Rectangle>(list));
		//state.dumpState();
		//state.toFile(new File("./files/tex/userguide/tikz/inti1.txt"));
		PackingState state = new PackingState();
		state.fromFile("./files/tex/userguide/tikz/inti1.txt");
		System.out.println(state);
		state.asTikz("./files/tex/userguide/tikz/init1.tikz");
		GraphSearchConfigurator configurator = new GraphSearchConfigurator("./graph_configurator.pk.properties");
		GraphSearchAlgorithm algorithm = new AStar(state, configurator);

		algorithm.execute();
		PackingState solution;
		if (algorithm.getSolutions().size() > 0) {
			solution = (PackingState) algorithm.getSolutions().get(0);
		} else {
			solution = (PackingState) algorithm.getBestSoFar();
		}
		System.out.println(solution);
		// /solution.asTikz("files/tex/konfSMCLodz2013/tikz/tmpastar.tikz");
		solution.asTikz("./files/tex/userguide/tikz/final1.tikz");
		// GraphSearchGraphvizer.go(algorithm, "tmp.dot",
		// GraphvizStateShapeType.POINT_WITH_NO_CONTENT);
		solution.dumpState();
		solution.showState();
		// System.out.println(algorithm.getClosedStatesCount());
		// System.out.println(algorithm.getClosedSet());
	}

	public static void main(String[] args) throws Exception {

		RectanglesTester tester = new RectanglesTester();
		Collection<Rectangle> list = tester.getListArea(15);
		System.out.println(list);

//		System.out.println("=============================================================================");
//		tester.astar(list);
		
		System.out.println("=============================================================================");
		tester.astar(list);
	}
}