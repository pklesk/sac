package sac.examples.tsp;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import sac.graph.AStar;
import sac.graph.GraphSearchConfigurator;
import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphState;
import sac.graphviz.GraphSearchGraphvizer;

/**
 * Console solver for Traveling Salesman Problem.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class ConsoleSolver {

	public static final String COMMAND_LINE_PARAMETER_HELP = "-h";
	public static final String COMMAND_LINE_PARAMETER_TSP_INPUT_FILEPATH = "-tsp";
	public static final String COMMAND_LINE_PARAMETER_TSP_OUTPUT_IMAGE_FILEPATH = "-tspImage";
	public static final String COMMAND_LINE_PARAMETER_TSP_OUTPUT_SOLUTION_IMAGE_FILEPATH = "-tspSolutionImage";
	public static final String COMMAND_LINE_PARAMETER_ALGORITHM = "-a";
	public static final String COMMAND_LINE_PARAMETER_CONFIGURATOR_FILEPATH = "-c";
	public static final String COMMAND_LINE_PARAMETER_GRAPHVIZ_OUTPUT_FILEPATH = "-g";
	public static final String COMMAND_LINE_PARAMETER_GRAPHVIZ_POINTS_WITH_CONTENT = "-gWithContent";

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		// help
		System.out.println("TRAVELING SALESMAN PROBLEM (TSP) SOLVER");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		System.out.println("PARAMETERS: ");
		System.out.println(COMMAND_LINE_PARAMETER_TSP_INPUT_FILEPATH
				+ " - input path to text file (places in successive lines written as: x,y) with a TSP to be solved");
		System.out.println(COMMAND_LINE_PARAMETER_ALGORITHM + " - full class name of graph search algorithm to be used (deafault: sac.graph.AStar)");
		System.out.println(COMMAND_LINE_PARAMETER_TSP_OUTPUT_IMAGE_FILEPATH + " - output path to .gif file representing the TSP to be solved)");
		System.out.println(COMMAND_LINE_PARAMETER_TSP_OUTPUT_SOLUTION_IMAGE_FILEPATH + " - output path to .gif file representing the solution of TSP)");
		System.out.println(COMMAND_LINE_PARAMETER_CONFIGURATOR_FILEPATH + " - input path to .properties file with configuration settings for search process");
		System.out.println(COMMAND_LINE_PARAMETER_GRAPHVIZ_OUTPUT_FILEPATH
				+ " - output path to .dot file in Graphviz format representing graph that was searched)");
		System.out.println(COMMAND_LINE_PARAMETER_GRAPHVIZ_POINTS_WITH_CONTENT
				+ " - true/false flag stating if points in Graphviz graph should be drawn with a content or no");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");

		Map<String, String> parameters = new HashMap<String, String>();
		for (int i = 0; i < args.length; i = i + 2) {
			if (i + 1 < args.length)
				parameters.put(args[i], args[i + 1]);
			else {
				System.out.println("PARAMETER " + args[i] + " IS MISSING ITS VALUE.");
				return;
			}
		}

		// tsp to solve
		TravelingSalesmanProblem tsp = null;
		sac.examples.tsp.Map map = null;
		if (parameters.get(COMMAND_LINE_PARAMETER_TSP_INPUT_FILEPATH) != null) {
			try {
				map = new sac.examples.tsp.Map(parameters.get(COMMAND_LINE_PARAMETER_TSP_INPUT_FILEPATH));
				tsp = new TravelingSalesmanProblem(map);
			} catch (Exception e) {
				System.out.println("SPECIFIED INPUT FILE WITH TSP DOES NOT EXIST OR IS CORRUPT.");
				return;
			}
		} else {
			System.out.println("PREPARING A RANDOM TSP");
			map = new sac.examples.tsp.Map(10);
			tsp = new TravelingSalesmanProblem(map);
		}

		// graph search algorithm
		GraphSearchAlgorithm algorithm = null;
		if (parameters.get(COMMAND_LINE_PARAMETER_ALGORITHM) != null) {
			try {
				Constructor<GraphSearchAlgorithm> constructor = (Constructor<GraphSearchAlgorithm>) Class.forName(
						parameters.get(COMMAND_LINE_PARAMETER_ALGORITHM)).getConstructor(GraphState.class);
				algorithm = (GraphSearchAlgorithm) constructor.newInstance(tsp);
			} catch (Exception e) {
				System.out.println("SPECIFIED ALGORITHM CLASS NAME IS WRONG OR ALGORITHM CANNOT BE INSTANTIATED.");
				return;
			}
		} else
			algorithm = new AStar(tsp);

		// graph search configurator
		GraphSearchConfigurator configurator = null;
		if (parameters.get(COMMAND_LINE_PARAMETER_CONFIGURATOR_FILEPATH) != null)
			configurator = new GraphSearchConfigurator(parameters.get(COMMAND_LINE_PARAMETER_CONFIGURATOR_FILEPATH));
		else
			configurator = new GraphSearchConfigurator();

		// graphviz output filepath
		String graphvizFilepath = null;
		if (parameters.get(COMMAND_LINE_PARAMETER_GRAPHVIZ_OUTPUT_FILEPATH) != null) {
			graphvizFilepath = parameters.get(COMMAND_LINE_PARAMETER_GRAPHVIZ_OUTPUT_FILEPATH);
			configurator.setParentsMemorizingChildren(true); // needed, so that graph connections
																// are memorized
		}
		algorithm.setConfigurator(configurator);

		boolean graphvizPointsWithContent = false;
		if (parameters.get(COMMAND_LINE_PARAMETER_GRAPHVIZ_POINTS_WITH_CONTENT) != null)
			graphvizPointsWithContent = Boolean.valueOf(parameters.get(COMMAND_LINE_PARAMETER_GRAPHVIZ_POINTS_WITH_CONTENT));

		System.out.println("TSP TO SOLVE:");
		System.out.println(TravelingSalesmanProblem.map);

		if (parameters.get(COMMAND_LINE_PARAMETER_TSP_OUTPUT_IMAGE_FILEPATH) != null) {
			try {
				tsp.saveAsImage(parameters.get(COMMAND_LINE_PARAMETER_TSP_OUTPUT_IMAGE_FILEPATH));
			} catch (Exception e) {
				System.out.println("A PROBLEM OCCURRED WHILE TRYING TO SAVE THE INPUT TSP AS AN IMAGE.");
			}
		}

		System.out.println("ALGORITHM: " + algorithm.getClass().getName());

		System.out.println("SOLVING...");
		algorithm.execute();

		System.out.println("DURATION TIME: " + algorithm.getDurationTime() + " ms.");
		System.out.println("CLOSED STATES: " + algorithm.getClosedStatesCount() + ".");
		System.out.println("OPEN STATES: " + algorithm.getOpenSet().size() + ".");

		if (algorithm.getSolutions().isEmpty()) {
			System.out.println("NO SOLUTIONS FOUND.");
			System.out.println("BEST STATE SO FAR: ");
			System.out.println(algorithm.getBestSoFar());
		} else {
			TravelingSalesmanProblem solution = (TravelingSalesmanProblem) algorithm.getSolutions().get(0);
			System.out.println("SOLUTION:");
			System.out.println(solution);
			System.out.println("PATH COST (LENGTH): " + solution.getG());
			if (parameters.get(COMMAND_LINE_PARAMETER_TSP_OUTPUT_SOLUTION_IMAGE_FILEPATH) != null) {
				try {
					solution.saveAsImage(parameters.get(COMMAND_LINE_PARAMETER_TSP_OUTPUT_SOLUTION_IMAGE_FILEPATH));
				} catch (Exception e) {
					System.out.println("A PROBLEM OCCURRED WHILE TRYING TO SAVE THE SOLUTION OF THE TSP AS AN IMAGE.");
				}
			}
		}

		if (graphvizFilepath != null)
			GraphSearchGraphvizer.go(algorithm, graphvizFilepath, (graphvizPointsWithContent), true);

		System.out.println("ALL DONE.");
	}
}