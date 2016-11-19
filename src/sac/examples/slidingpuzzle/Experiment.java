package sac.examples.slidingpuzzle;

import sac.StateFunction;
import sac.graph.AStar;
import sac.graph.OpenSetAsPriorityQueue;
import sac.graph.OpenSetAsPriorityQueueFastContains;
import sac.graph.OpenSetAsPriorityQueueFastContainsFastReplace;
import sac.graph.BestFirstSearch;
import sac.graph.GraphSearchConfigurator;
import sac.graph.GraphSearchAlgorithm;
import sac.stats.Stats;
import sac.stats.StatsBarChart;
import sac.stats.StatsXYChart;
import sac.stats.StatsCategory;
import sac.stats.StatsOperationType;

/**
 * Comparative experiment for sliding puzzle problem with multiple loops over: 100 random problems, two search
 * algorithms (BFS and A*), two heuristics (manhattan, linear conflicts), three types of open set classes. After
 * execution plots with comparisons are generated and saved at the current folder.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class Experiment {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception {
		
		System.out.println("Starting...");
		long t1 = System.currentTimeMillis();
		
		Stats stats = new Stats();

		// loop over random sliding puzzle problems
		for (int i = 0; i < 100; i++) {
			SlidingPuzzle puzzle = new SlidingPuzzle((byte) 3);
			puzzle.shuffle((Math.random() > 0.5) ? 1000 : 1001); // even or odd number of shuffling																	// moves

			// initial solution by A* so that optimal path length is known for further statistics
			SlidingPuzzle.setHFunction(new HFunctionLinearConflicts());
			AStar astar = new AStar(new SlidingPuzzle(puzzle));
			astar.execute();
			int optimalPathLength = astar.getSolutions().get(0).getPath().size();

			// loop over algorithms
			GraphSearchAlgorithm[] algorithms = { new BestFirstSearch(), new AStar() };
			for (GraphSearchAlgorithm algorithm : algorithms) {

				// loop over heuristics
				StateFunction[] heuristics = { new HFunctionManhattan(), new HFunctionLinearConflicts() };				
				for (StateFunction h : heuristics) {

					// loop over different open set implementations					
					Class[] openSetClasses = { OpenSetAsPriorityQueue.class, OpenSetAsPriorityQueueFastContains.class,
							OpenSetAsPriorityQueueFastContainsFastReplace.class };
					for (Class openSetClass : openSetClasses) {

						// impose settings from all loops
						algorithm.setInitial(new SlidingPuzzle(puzzle));
						SlidingPuzzle.setHFunction(h);
						GraphSearchConfigurator configurator = new GraphSearchConfigurator();
						configurator.setOpenSetClassName(openSetClass.getName());
						algorithm.setConfigurator(configurator);
												
						// search
						algorithm.execute();

						// register current single run in stats object
						stats.addEntries(algorithm, i, algorithm.getClass(), h.getClass(), openSetClass, optimalPathLength);
					}
				}
			}
		}

		long t2 = System.currentTimeMillis();
		System.out.println("Experiment total time [s]: " + (0.001 * (t2 - t1)));
			
		// sliding puzzle - duration over algorithms and heuristics (bar chart)
		StatsBarChart statsBarChart1 = new StatsBarChart(stats, "sliding puzzle - duration", "algorithm and heuristics", "time [ms]");
		statsBarChart1.setValue("LC", "A*", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_DURATION_TIME.toString(), null, AStar.class,
				HFunctionLinearConflicts.class, null, null);
		statsBarChart1.setValue("M", "A*", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_DURATION_TIME.toString(), null, AStar.class,
				HFunctionManhattan.class, null, null);
		statsBarChart1.setValue("LC", "BFS", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_DURATION_TIME.toString(), null, BestFirstSearch.class,
				HFunctionLinearConflicts.class, null, null);
		statsBarChart1.setValue("M", "BFS", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_DURATION_TIME.toString(), null, BestFirstSearch.class,
				HFunctionManhattan.class, null, null);
		statsBarChart1.saveAsJPEG("./sliding_puzzle_duration.jpg");

		// sliding puzzle - duration over open sets (bar chart)
		StatsBarChart statsBarChart2 = new StatsBarChart(stats, "sliding puzzle - duration", "open set and algorithm", "time [ms]");
		statsBarChart2.setValue("A*", "PQ", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_DURATION_TIME.toString(), null, AStar.class, null,
				OpenSetAsPriorityQueue.class, null);
		statsBarChart2.setValue("BFS", "PQ", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_DURATION_TIME.toString(), null, BestFirstSearch.class, null,
				OpenSetAsPriorityQueue.class, null);
		statsBarChart2.setValue("A*", "PQFC", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_DURATION_TIME.toString(), null, AStar.class, null,
				OpenSetAsPriorityQueueFastContains.class, null);
		statsBarChart2.setValue("BFS", "PQFC", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_DURATION_TIME.toString(), null, BestFirstSearch.class, null,
				OpenSetAsPriorityQueueFastContains.class, null);
		statsBarChart2.setValue("A*", "PQFCFR", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_DURATION_TIME.toString(), null, AStar.class, null,
				OpenSetAsPriorityQueueFastContainsFastReplace.class, null);
		statsBarChart2.setValue("BFS", "PQFCFR", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_DURATION_TIME.toString(), null, BestFirstSearch.class,
				null, OpenSetAsPriorityQueueFastContainsFastReplace.class, null);
		statsBarChart2.saveAsJPEG("./sliding_puzzle_duration2.jpg");

		// sliding puzzle - closed states distribution over optimal path length (xy chart)
		StatsXYChart statsXYChart1 = new StatsXYChart(stats, "sliding puzzle - closed states as path length grows", "optimal path length", "closed states");
		statsXYChart1.addSeries("A*_LC", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_CLOSED_STATES.toString(), 4, null, AStar.class,
				HFunctionLinearConflicts.class, null, null);
		statsXYChart1.addSeries("A*_M", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_CLOSED_STATES.toString(), 4, null, AStar.class,
				HFunctionManhattan.class, null, null);
		statsXYChart1.addSeries("BFS_LC", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_CLOSED_STATES.toString(), 4, null, BestFirstSearch.class,
				HFunctionLinearConflicts.class, null, null);
		statsXYChart1.addSeries("BFS_M", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_CLOSED_STATES.toString(), 4, null, BestFirstSearch.class,
				HFunctionManhattan.class, null, null);
		statsXYChart1.saveAsJPEG("./sliding_puzzle_closed_states.jpg");

		// sliding puzzle - found path length over optimal path length (xy chart)
		StatsXYChart statsXYChart2 = new StatsXYChart(stats, "sliding puzzle - found path length over optimal path length", "optimal path length",
				"found path length");
		statsXYChart2.addSeries("A*_any", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_PATH_LENGTH.toString(), 4, null, AStar.class, null, null, null);
		statsXYChart2.addSeries("BFS_LC", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_PATH_LENGTH.toString(), 4, null, BestFirstSearch.class,
				HFunctionLinearConflicts.class, null, null);
		statsXYChart2.addSeries("BFS_M", StatsOperationType.MEAN, StatsCategory.GRAPH_SEARCH_PATH_LENGTH.toString(), 4, null, BestFirstSearch.class,
				HFunctionManhattan.class, null, null);
		statsXYChart2.saveAsJPEG("./sliding_puzzle_path_lengths.jpg");

		System.out.println("Charts done.");
	}
}