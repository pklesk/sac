package sac.examples.sudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import sac.StateFunction;
import sac.examples.slidingpuzzle.SlidingPuzzle;
import sac.graph.BestFirstSearch;
import sac.graph.GraphSearchConfigurator;
import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphState;
import sac.graphviz.GraphSearchGraphvizer;

/**
 * Console solver for sudoku.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class ConsoleSolver {

	public static final String COMMAND_LINE_PARAMETER_HELP = "-h";
	public static final String COMMAND_LINE_PARAMETER_SUDOKU_INPUT_FILEPATH = "-s";
	public static final String COMMAND_LINE_PARAMETER_HEURISTICS = "-h";
	public static final String COMMAND_LINE_PARAMETER_CONFIGURATOR_FILEPATH = "-c";
	public static final String COMMAND_LINE_PARAMETER_GRAPHVIZ_OUTPUT_FILEPATH = "-g";
	public static final String COMMAND_LINE_PARAMETER_GRAPHVIZ_POINTS_WITH_CONTENT = "-gWithContent";

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		// help
		System.out.println("SUDOKU SOLVER");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		System.out.println("PARAMETERS: ");
		System.out.println(COMMAND_LINE_PARAMETER_SUDOKU_INPUT_FILEPATH
				+ " - input path to text file (one line, comma-separated) with sudoku to be solved (possible sudokus: 4x4, 9x9, 16x16, etc.)");
		System.out.println(COMMAND_LINE_PARAMETER_HEURISTICS
				+ " - full class name of heuristic function be used (deafault: sac.examples.sudoku.HFunctionSumRemainingPossibilities)");
		System.out.println(COMMAND_LINE_PARAMETER_CONFIGURATOR_FILEPATH + " - input path to .properties file with configuration settings for search process");
		System.out.println(COMMAND_LINE_PARAMETER_GRAPHVIZ_OUTPUT_FILEPATH
				+ " - output path to .dot file in Graphviz format representing graph that was searched");
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

		// sudoku to solve
		Sudoku sudoku = null;
		if (parameters.get(COMMAND_LINE_PARAMETER_SUDOKU_INPUT_FILEPATH) != null) {
			try {
				File file = new File(parameters.get(COMMAND_LINE_PARAMETER_SUDOKU_INPUT_FILEPATH));
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String sudokuAsString = bufferedReader.readLine();
				String delimiter = ",";
				StringTokenizer tokenizer = new StringTokenizer(sudokuAsString, delimiter);
				int tokensCount = tokenizer.countTokens();
				double n = Math.pow(tokensCount, 0.25);
				if (n - Math.floor(n) > 0.0) {
					System.out.println("SPECIFIED FILE DOES NOT CONTAIN A VALID SUDOKU (POSSIBLY WRONG NUMBER OF ELEMENTS).");
					bufferedReader.close();
					fileReader.close();
					return;
				}
				bufferedReader.close();
				fileReader.close();
				sudoku = new Sudoku((byte) n, sudokuAsString);
			} catch (Exception e) {
				System.out.println("SPECIFIED INPUT FILE WITH SUDOKU DOES NOT EXIST OR IS CORRUPT.");
				return;
			}
		} else {						
			System.out.println("DEFAULT SUDOKU 'QASSIM HAMZA'.");
			String sudokuString = 
					  "0,0,0,7,0,0,8,0,0," 
					+ "0,0,0,0,4,0,0,3,0," 
					+ "0,0,0,0,0,9,0,0,1," 
					+ "6,0,0,5,0,0,0,0,0," 
					+ "0,1,0,0,3,0,0,4,0,"
					+ "0,0,5,0,0,1,0,0,7," 
					+ "5,0,0,2,0,0,6,0,0," 
					+ "0,3,0,0,8,0,0,9,0," 
					+ "0,0,7,0,0,0,0,0,2";
			
			sudoku = new Sudoku((byte) 3, sudokuString);
		}
		
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
			heuristics = new HFunctionSumRemainingPossibilities();
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

		boolean graphvizPointsWithContent = false;
		if (parameters.get(COMMAND_LINE_PARAMETER_GRAPHVIZ_POINTS_WITH_CONTENT) != null)
			graphvizPointsWithContent = Boolean.valueOf(parameters.get(COMMAND_LINE_PARAMETER_GRAPHVIZ_POINTS_WITH_CONTENT));

		System.out.println("SUDOKU TO SOLVE:");
		System.out.println(sudoku);		
		
		GraphSearchAlgorithm algorithm = new BestFirstSearch(sudoku, configurator);
		
		System.out.println("HEURISTICS: " + heuristics.getClass().getName() + ".");
		System.out.println("SOLVING...");
		
		algorithm.execute();

		System.out.println("DURATION TIME " +  algorithm.getDurationTime() + " ms.");
		System.out.println("CLOSED STATES: " + algorithm.getClosedStatesCount() + ".");
		System.out.println("OPEN STATES: " + algorithm.getOpenSet().size() + ".");

		if (algorithm.getSolutions().isEmpty()) {
			System.out.println("NO SOLUTIONS FOUND.");
			System.out.println("BEST STATE SO FAR: ");
			System.out.println(algorithm.getBestSoFar());
		} else {
			System.out.println("FOUND " + algorithm.getSolutions().size() + " SOLUTION(S):");
			int i = 0;
			for (GraphState solution : algorithm.getSolutions()) {
				System.out.println("---");
				System.out.println("SOLUTION " + (++i) + ":");
				System.out.println(solution);
			}
		}

		if (graphvizFilepath != null)
			GraphSearchGraphvizer.go(algorithm, graphvizFilepath, (graphvizPointsWithContent), true);

		System.out.println("ALL DONE.");
	}
}