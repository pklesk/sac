package sac.examples.rectpacking;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sac.graph.AStar;
import sac.graph.Dijkstra;
import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphSearchConfigurator;
import sac.stats.Stats;
import sac.stats.StatsBarChart;
import sac.stats.StatsCategory;
import sac.stats.StatsOperationType;

public class Experiment1 {

	private static Random random = new Random(2L);
	//private static Random random = new Random(0L);

	private static String USELESS_RATIO = "USELESS_RATIO";

	private static List<Rectangle> randomRectangles(int n) {
		List<Rectangle> list = new ArrayList<Rectangle>();
		for (int i = 0; i < n; i++) {
			list.add(new Rectangle(75 + random.nextInt(125), 75 + random.nextInt(125), i));
			//list.add(new Rectangle(50 + random.nextInt(150), 50 + random.nextInt(150), i));
		}
		return list;
	}

	public static void main(String[] args) throws Exception {

		long t1 = System.currentTimeMillis();

		Stats stats = new Stats();
		GraphSearchConfigurator configurator = new GraphSearchConfigurator("./graph_configurator.pk.properties");
		
		int howManyProblems = 1;
		PackingState[] packingStates = { new PackingState(), new PackingStateFullCuts() };
		GraphSearchAlgorithm[] algorithms = { new AStar(), new Dijkstra() };
		double[] heuristicsHowFar = {0.5, 0.4, 0.3, 0.2, 0.1};

		int k = 0;
		int K = howManyProblems * packingStates.length * (heuristicsHowFar.length + 1);

		for (int i = 0; i < howManyProblems; i++) {
			List<Rectangle> problem = randomRectangles(20);

			for (PackingState packingState : packingStates) {
				if (packingState instanceof PackingStateFullCuts) 
					PackingState.BRANCHING_BY_REMAINING_RECTANGLES = 1;
				else 
					PackingState.BRANCHING_BY_REMAINING_RECTANGLES = 2;
				packingState.setup(500.0, 350.0, new ArrayList<Rectangle>(problem));
					
				for (GraphSearchAlgorithm algorithm : algorithms) {

					int jLimit = (algorithm instanceof Dijkstra) ? 1 : heuristicsHowFar.length;
					for (int j = 0; j < jLimit; j++) {
						PackingState.HEURISTICS_HOW_FAR = heuristicsHowFar[j];

						algorithm.setInitial(packingState);
						algorithm.setConfigurator(configurator);
						algorithm.execute();

						stats.addEntries(algorithm, i, packingState.getClass(), algorithm.getClass(), heuristicsHowFar[j]);
						if (!algorithm.getSolutions().isEmpty()) {
							PackingState solution = (PackingState) algorithm.getSolutions().get(0);
							stats.addEntry(USELESS_RATIO, 1.0 - solution.getContainer().getAreaUsed() / solution.getContainer().getArea(), i, packingState.getClass(), algorithm
									.getClass(), heuristicsHowFar[j]);
						}
						
						System.out.println("Progress: " + (++k) + " / " + K);
						System.gc();
					}
				}
			}
		}

		long t2 = System.currentTimeMillis();
		System.out.println("All done. Time: " + (0.001 * (t2 - t1)) + " s.");

		// bar chart solution quality
		StatsBarChart chartQuality = new StatsBarChart(stats, "Solution quality", "algorithm", "wastes ratio");
		for (double hhf : heuristicsHowFar) {
			chartQuality.setValue("any cuts", "A*_" + hhf, StatsOperationType.MEAN, USELESS_RATIO, null, PackingState.class, AStar.class, hhf);
			chartQuality.setValue("full cuts", "A*_" + hhf, StatsOperationType.MEAN, USELESS_RATIO, null, PackingStateFullCuts.class, AStar.class, hhf);
		}
		chartQuality.setValue("any cuts", "Dijkstra", StatsOperationType.MEAN, USELESS_RATIO, null, PackingState.class, Dijkstra.class, null);
		chartQuality.setValue("full cuts", "Dijkstra", StatsOperationType.MEAN, USELESS_RATIO, null, PackingStateFullCuts.class, Dijkstra.class, null);
		chartQuality.saveAsJPEG("./rectpacking_chart_quality.jpg");

		// bar chart number of states
		StatsBarChart chartStates = new StatsBarChart(stats, "Number of closed, open states", "algorithm", "states");
		for (double hhf : heuristicsHowFar) {
			chartStates.setValue("any cuts - closed", "A*_" + hhf, StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_CLOSED_STATES.toString(), null, PackingState.class,
					AStar.class, hhf);
			chartStates.setValue("full cuts - closed", "A*_" + hhf, StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_CLOSED_STATES.toString(), null,
					PackingStateFullCuts.class, AStar.class, hhf);
		}
		chartStates.setValue("any cuts - closed", "Dijkstra", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_CLOSED_STATES.toString(), null, PackingState.class,
				Dijkstra.class, null);
		chartStates.setValue("full cuts - closed", "Dijkstra", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_CLOSED_STATES.toString(), null,
				PackingStateFullCuts.class, Dijkstra.class, null);

		chartStates.saveAsJPEG("./rectpacking_chart_states.jpg");
		
		/*
		// graph viz example		
		List<Rectangle> rectangles = randomRectangles(15);
		PackingState initial = new PackingState(500.0, 300.0, new ArrayList<Rectangle>(rectangles));
		initial.asTikz("d:/pk_initial.tikz");
		GraphSearchAlgorithm algorithm = new BestFirstSearch();
		PackingState.HEURISTICS_HOW_FAR = 0.4;
		configurator.setParentsMemorizingChildren(true);
		algorithm.setConfigurator(configurator);
		algorithm.setInitial(initial);
		algorithm.execute();
		PackingState solution = (PackingState) algorithm.getSolutions().get(0); 
		solution.showState();
		solution.dumpState();
		solution.asTikz(new File("d:/pk_bfs1.tikz"));
		GraphSearchGraphvizer.go(algorithm, "d:/pk_bfs1.dot", GraphvizStateShapeType.POINT_WITH_NO_CONTENT);
		*/
	}
}