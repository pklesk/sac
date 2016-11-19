package sac.examples.slidingpuzzle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import sac.StateFunction;
import sac.graph.AStar;
import sac.graph.GraphSearchConfigurator;
import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphState;
import sac.graphviz.GraphSearchGraphvizer;

/**
 * Console solver for sliding puzzle.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class ConsoleSolver {

	public static final String COMMAND_LINE_PARAMETER_HELP = "-h";
	public static final String COMMAND_LINE_PARAMETER_SLIDING_PUZZLE_INPUT_FILEPATH = "-sp";
	public static final String COMMAND_LINE_PARAMETER_ALGORITHM = "-a";
	public static final String COMMAND_LINE_PARAMETER_HEURISTICS = "-h";
	public static final String COMMAND_LINE_PARAMETER_CONFIGURATOR_FILEPATH = "-c";
	public static final String COMMAND_LINE_PARAMETER_GRAPHVIZ_OUTPUT_FILEPATH = "-g";
	public static final String COMMAND_LINE_PARAMETER_GRAPHVIZ_POINTS_WITH_CONTENT = "-gWithContent";

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		// help
		System.out.println("SLIDING PUZZLE SOLVER");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		System.out.println("PARAMETERS: ");
		System.out.println(COMMAND_LINE_PARAMETER_SLIDING_PUZZLE_INPUT_FILEPATH
				+ " - input path to text file (one line, comma-separated) with sliding puzzle to be solved ('0' assumed as an empty tile)");
		System.out.println(COMMAND_LINE_PARAMETER_ALGORITHM + " - full class name of graph search algorithm to be used (deafault: sac.graph.AStar)");
		System.out.println(COMMAND_LINE_PARAMETER_HEURISTICS
				+ " - full class name of heuristic function be used (deafault: sac.examples.slidingpuzzle.HFunctionLinearConflicts)");
		System.out.println(COMMAND_LINE_PARAMETER_CONFIGURATOR_FILEPATH + " - input path to .properties file with configuration settings for search process");
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
				System.out.println("PARAMETER " + args[i] + " IS MISSING ITS VALUE.");
				return;
			}
		}

		// sliding puzzle to solve
		SlidingPuzzle slidingPuzzle = null;
		String delimiter = ",";
		if (parameters.get(COMMAND_LINE_PARAMETER_SLIDING_PUZZLE_INPUT_FILEPATH) != null) {
			try {
				File file = new File(parameters.get(COMMAND_LINE_PARAMETER_SLIDING_PUZZLE_INPUT_FILEPATH));
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String slidingPuzzleAsString = bufferedReader.readLine();
				StringTokenizer tokenizer = new StringTokenizer(slidingPuzzleAsString, delimiter);
				int tokensCount = tokenizer.countTokens();
				double n = Math.pow(tokensCount, 0.5);
				if (n - Math.floor(n) > 0.0) {
					System.out.println("SPECIFIED FILE DOES NOT CONTAIN A VALID SLIDING PUZZLE (POSSIBLY WRONG NUMBER OF ELEMENTS).");
					bufferedReader.close();
					fileReader.close();
					return;
				}
				bufferedReader.close();
				fileReader.close();
				byte[] table = new byte[(int) (n * n)];
				int i = 0;
				while (tokenizer.hasMoreTokens())
					table[i++] = Byte.valueOf(tokenizer.nextToken());
				slidingPuzzle = new SlidingPuzzle(table);
			} catch (Exception e) {
				System.out.println("SPECIFIED INPUT FILE WITH SLIDING PUZZLE DOES NOT EXIST OR IS CORRUPT.");
				return;
			}
		} else {
			String slidingPuzzleAsString = "0,3,2,4,7,8,1,5,6";
			StringTokenizer tokenizer = new StringTokenizer(slidingPuzzleAsString, delimiter);
			System.out.println("DEFAULT SLIDING PUZZLE: '" + slidingPuzzleAsString + "'.");
			byte[] table = new byte[tokenizer.countTokens()];
			int i = 0;
			while (tokenizer.hasMoreTokens())
				table[i++] = Byte.valueOf(tokenizer.nextToken());
			slidingPuzzle = new SlidingPuzzle(table);
		}

		// graph search algorithm
		GraphSearchAlgorithm algorithm = null;
		if (parameters.get(COMMAND_LINE_PARAMETER_ALGORITHM) != null) {
			try {
				Constructor<GraphSearchAlgorithm> constructor = (Constructor<GraphSearchAlgorithm>) Class.forName(
						parameters.get(COMMAND_LINE_PARAMETER_ALGORITHM)).getConstructor(GraphState.class);
				algorithm = (GraphSearchAlgorithm) constructor.newInstance(slidingPuzzle);
			} catch (Exception e) {
				System.out.println("SPECIFIED ALGORITHM CLASS NAME IS WRONG OR ALGORITHM CANNOT BE INSTANTIATED.");
				return;
			}
		} else
			algorithm = new AStar(slidingPuzzle);

		// heuristics
		StateFunction heuristics = null;
		if (parameters.get(COMMAND_LINE_PARAMETER_HEURISTICS) != null) {
			try {
				Constructor<StateFunction> constructor = (Constructor<StateFunction>) Class.forName(parameters.get(COMMAND_LINE_PARAMETER_HEURISTICS))
						.getConstructor();
				heuristics = (StateFunction) constructor.newInstance();
			} catch (Exception e) {
				System.out.println("SPECIFIED HEURISTICS CLASS NAME IS WRONG OR HEURISTICS CANNOT BE INSTANTIATED.");
				return;
			}
		} else
			heuristics = new HFunctionLinearConflicts();
		SlidingPuzzle.setHFunction(heuristics);

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

		System.out.println("SLIDING PUZZLE TO SOLVE:");
		System.out.println(slidingPuzzle);

		System.out.println("ALGORITHM: " + algorithm.getClass().getName() + ".");
		System.out.println("HEURISTICS: " + heuristics.getClass().getName() + ".");

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
			SlidingPuzzle solution = (SlidingPuzzle) algorithm.getSolutions().get(0);
			System.out.println("SOLUTION:");
			System.out.println(solution);
			System.out.println("PATH LENGTH (INCLUDING TERMINAL STATES): " + solution.getPath().size() + ".");
			System.out.println("PATH AS SEQUENCE OF MOVES: " + solution.getMovesAlongPath() + ".");
		}

		if (graphvizFilepath != null)
			GraphSearchGraphvizer.go(algorithm, graphvizFilepath, (graphvizPointsWithContent), true);

		System.out.println("ALL DONE.");
	}
}