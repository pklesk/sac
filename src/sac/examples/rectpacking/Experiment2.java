package sac.examples.rectpacking;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sac.graph.AStar;
import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphSearchConfigurator;
import sac.graphviz.GraphSearchGraphvizer;

public class Experiment2 {

	private static Random random = new Random();

	// private static Random random = new Random(2L);

	private static List<Rectangle> randomRectangles(int n) {
		List<Rectangle> list = new ArrayList<Rectangle>();
		for (int i = 0; i < n; i++) {
			// list.add(new Rectangle(75 + random.nextInt(125), 75 + random.nextInt(125), i));
			list.add(new Rectangle(50 + random.nextInt(150), 50 + random.nextInt(150), i));
		}
		return list;
	}

	public static void main(String[] args) throws Exception {

		System.out.println("Starting a rectangle packing example...");

		GraphSearchConfigurator configurator = new GraphSearchConfigurator("./graph_configurator.properties");

		// graph viz example
		List<Rectangle> rectangles = randomRectangles(15);
		PackingState initial = new PackingState(500.0, 300.0, new ArrayList<Rectangle>(rectangles));

		System.out.println("INITIAL:");
		initial.dumpState();

		GraphSearchAlgorithm algorithm = new AStar();
		PackingState.HEURISTICS_HOW_FAR = 0.25;
		configurator.setParentsMemorizingChildren(true);
		algorithm.setConfigurator(configurator);
		algorithm.setInitial(initial);
		long t1 = System.currentTimeMillis();
		algorithm.execute();
		long t2 = System.currentTimeMillis();
		PackingState solution = (PackingState) algorithm.getSolutions().get(0);

		System.out.println("SOLUTION:");
		solution.dumpState();

		System.out.println("DONE IN " + (t2 - t1) + " MILIS.");
		double uselessRatio = Math.round(10000 * (1.0 - solution.getContainer().getAreaUsed() / solution.getContainer().getArea())) / 100.0;
		System.out.println("SOLUTION WASTED RATIO: " + uselessRatio + "%");
		System.out.println("CHECKED STATES: " + algorithm.getClosedStatesCount());

		GraphSearchGraphvizer.go(algorithm, "./rectpacking_search_graph.dot", false, false);
		System.out.println("GRAPH PDF DONE.");

		initial.asTikz("./initial.tikz");
		solution.asTikz("./solution.tikz");
		System.out.println("TIKZ FIGURES DONE.");
	}
}