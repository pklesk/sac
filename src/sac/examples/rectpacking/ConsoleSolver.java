package sac.examples.rectpacking;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import sac.graph.AStar;
import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphSearchConfigurator;
import sac.graph.GraphState;
import sac.graphviz.GraphSearchGraphvizer;

/**
 * Console solver for rectangle packing.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class ConsoleSolver {

	public static final String COMMAND_LINE_PARAMETER_HELP = "-h";
	public static final String COMMAND_LINE_PARAMETER_INPUT_FILEPATH = "-f";
	public static final String COMMAND_LINE_PARAMETER_ALGORITHM = "-a";
	public static final String COMMAND_LINE_PARAMETER_CUT_PROCEDURE = "-p";
	public static final String COMMAND_LINE_PARAMETER_CONFIGURATOR_FILEPATH = "-c";
	public static final String COMMAND_LINE_PARAMETER_GRAPHVIZ_OUTPUT_FILEPATH = "-g";
	public static final String COMMAND_LINE_PARAMETER_TIKZ_OUTPUT_FILEPATH = "-t";
	public static final String COMMAND_LINE_PARAMETER_SHOW_STATE = "-s";
	public static final String COMMAND_LINE_PARAMETER_GRAPHVIZ_POINTS_WITH_CONTENT = "-gWithContent";

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		// help
		System.out.println("RECTANGLE PACKING SOLVER");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		System.out.println("PARAMETERS: ");
		System.out.println(COMMAND_LINE_PARAMETER_INPUT_FILEPATH
				+ " - input path to text file, one rectangle per line in form: widith, height");
		System.out.println(COMMAND_LINE_PARAMETER_CUT_PROCEDURE + " - full|any depending on the cut procedure");
		System.out.println(COMMAND_LINE_PARAMETER_CONFIGURATOR_FILEPATH + " - input path to .properties file with configuration settings for search process");
		System.out.println(COMMAND_LINE_PARAMETER_ALGORITHM + " - full class name of graph search algorithm to be used (deafault: sac.graph.AStar)");
		System.out.println(COMMAND_LINE_PARAMETER_SHOW_STATE
				+ " - swt|console show solution on swt panel or on console");
		System.out.println(COMMAND_LINE_PARAMETER_TIKZ_OUTPUT_FILEPATH
				+ " - output path to .tikz file");
		System.out.println(COMMAND_LINE_PARAMETER_GRAPHVIZ_OUTPUT_FILEPATH
				+ " - output path to .dot file in Graphviz format, representing graph that was searched");
		System.out.println(COMMAND_LINE_PARAMETER_GRAPHVIZ_POINTS_WITH_CONTENT
				+ " - true/false flag stating if points in Graphviz graph should be drawn with a content or no");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");

		Map<String, String> parameters = new HashMap<String, String>();
		for (int i = 0; i < args.length; i = i + 2) {
			if (i + 1 < args.length)
				parameters.put(args[i], args[i + 1]);
			else {
				System.out.println("PARAMETER " + args[i] + " IS MISSING ITS VALUE");
				return;
			}
		}
		if (parameters.get(COMMAND_LINE_PARAMETER_CUT_PROCEDURE) == null) {
			parameters.put(COMMAND_LINE_PARAMETER_CUT_PROCEDURE, "any");
		}
		// state to solve
		PackingState state = null;
		if  (parameters.get(COMMAND_LINE_PARAMETER_CUT_PROCEDURE).equalsIgnoreCase("any")) {
			state = new PackingState();
		} else if  (parameters.get(COMMAND_LINE_PARAMETER_CUT_PROCEDURE).equalsIgnoreCase("full")) {
			state = new PackingStateFullCuts();
		}
		
		if (parameters.get(COMMAND_LINE_PARAMETER_INPUT_FILEPATH) == null)
			return;
		
		state.fromFile(parameters.get(COMMAND_LINE_PARAMETER_INPUT_FILEPATH));
		
		// graph search algorithm
		GraphSearchAlgorithm algorithm = null;
		if (parameters.get(COMMAND_LINE_PARAMETER_ALGORITHM) != null) {
			try {
				Constructor<GraphSearchAlgorithm> constructor = (Constructor<GraphSearchAlgorithm>) Class.forName(
						parameters.get(COMMAND_LINE_PARAMETER_ALGORITHM)).getConstructor(GraphState.class);
				algorithm = (GraphSearchAlgorithm) constructor.newInstance(state);
			} catch (Exception e) {
				System.out.println("SPECIFIED ALGORITHM CLASS NAME IS WRONG OR ALGORITHM CANNOT BE INSTANTIATED.");
				return;
			}
		} else
			if  (parameters.get(COMMAND_LINE_PARAMETER_CUT_PROCEDURE).equalsIgnoreCase("full")) {
				algorithm = new AStar((PackingStateFullCuts) state);
			} else {
				algorithm = new AStar(state);
			}

				
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

		
		System.out.println("STATE TO SOLVE:");
		System.out.println(state);

		System.out.println("ALGORITHM: " + algorithm.getClass().getName() + ".");
		System.out.println("CLASS: " + state.getClass().getName() + ".");

		System.out.println("SOLVING...");
		algorithm.execute();

		System.out.println("DURATION TIME: " + algorithm.getDurationTime() + " ms.");
		System.out.println("ClOSED STATES: " + algorithm.getClosedStatesCount() + ".");
		System.out.println("OPEN STATES: " + algorithm.getOpenSet().size() + ".");
		PackingState solution;
		if (algorithm.getSolutions().isEmpty()) {
			System.out.println("NO SOLUTIONS FOUND");
			System.out.println("BEST STATE SO FAR: ");
			solution= (PackingState) algorithm.getBestSoFar();
			System.out.println(solution);
			
		} else {
			solution = (PackingState) algorithm.getSolutions().get(0);
			System.out.println("SOLUTION:");
			System.out.println(solution);
			System.out.println("PATH LENGTH (INCLUDING TERMINAL STATES): " + solution.getPath().size() + ".");
			System.out.println("PATH AS SEQUENCE OF MOVES: " + solution.getMovesAlongPath() + ".");
			
		}
		if (parameters.get(COMMAND_LINE_PARAMETER_TIKZ_OUTPUT_FILEPATH) != null) {
			System.out.println("WRITE SOLUTION as TIKZ: " + parameters.get(COMMAND_LINE_PARAMETER_TIKZ_OUTPUT_FILEPATH));
			solution.asTikz(parameters.get(COMMAND_LINE_PARAMETER_TIKZ_OUTPUT_FILEPATH));
		}
		if (parameters.get(COMMAND_LINE_PARAMETER_SHOW_STATE) != null) {
			if (parameters.get(COMMAND_LINE_PARAMETER_SHOW_STATE).equalsIgnoreCase("swt")) {
				solution.showState();
			} else if (parameters.get(COMMAND_LINE_PARAMETER_SHOW_STATE).equalsIgnoreCase("console")) {
				System.out.println("DETAILED PACKING STATE:");
				solution.dumpState();
			} 
		}
		if (graphvizFilepath != null)
			GraphSearchGraphvizer.go(algorithm, graphvizFilepath, (graphvizPointsWithContent), true);
		
		System.out.println("ALL DONE.");
	}
}