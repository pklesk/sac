package sac.graphviz;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import sac.game.GameSearchAlgorithm;
import sac.game.GameState;
import sac.game.TranspositionTableEntry;
import sac.util.ConsoleLogger;

/**
 * Generator of files (based on a terminated game search algorithm) which are compliant with Graphviz software
 * (http://www.graphviz.org).
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class GameSearchGraphvizer {

	/**
	 * Visualization constant - height of the initial state.
	 */
	private static final double HEIGHT_INITIAL = 0.2;

	/**
	 * Visualization constant - height of regular states.
	 */
	private static final double HEIGHT_REGULAR = 0.1;

	/**
	 * Visualization constant - height of cut-off states.
	 */
	private static final double HEIGHT_CUTOFF = 0.06;

	/**
	 * Visualization constant - height of states read from transposition table.
	 */
	private static final double HEIGHT_TRANSPOSED = 0.1;

	/**
	 * Visualization constant - height of terminal-non-win states.
	 */
	private static final double HEIGHT_TERMINAL_NON_WIN = 0.1;

	/**
	 * Visualization constant - height of terminal-win states.
	 */
	private static final double HEIGHT_TERMINAL_WIN = 0.25;

	/**
	 * Visualization constant - height of states along principal variation.
	 */
	private static final double HEIGHT_PRINCIPAL_VARIATION = 0.3;

	/**
	 * Visualization constant - height of states along principal variation being win terminals.
	 */
	private static final double HEIGHT_PRINCIPAL_VARIATION_TERMINAL_WIN = 0.4;

	/**
	 * Visualization constant - color for the initial state.
	 */
	public static final String COLOR_INITIAL = "yellow";

	/**
	 * Visualization constant - color for regular states.
	 */
	public static final String COLOR_REGULAR = "whitesmoke";

	/**
	 * Visualization constant - color for states that were cut off or their game value was read from a transposition
	 * table.
	 */
	public static final String COLOR_CUTOFF = "orangered";

	/**
	 * Visualization constant - color for states of which the game value was read from a transposition table.
	 */
	public static final String COLOR_TRANSPOSED = "firebrick";

	/**
	 * Visualization constant - fill color for terminal non-win states.
	 */
	public static final String COLOR_TERMINAL_NON_WIN = "dimgray";

	/**
	 * Visualization constant - fill color for terminal win states.
	 */
	public static final String COLOR_TERMINAL_WIN = "steelblue";

	/**
	 * Visualization constant - fill color for states along principal variation path.
	 */
	private static final String COLOR_PRINCIPAL_VARIATION = "limegreen";

	/**
	 * Visualization constant - color labels with game value or bound (only for WITHOUT_CONTENT node type).
	 */
	public static final String COLOR_GAME_VALUE_WIHTOUT_CONTENT = "blue";

	/**
	 * Visualization constant - font size for WITH_CONTENT node type.
	 */
	public static final int FONT_SIZE_WITH_CONTENT = 8;

	/**
	 * Visualization constant - font size for WITHOUT_CONTENT node type.
	 */
	public static final int FONT_SIZE_WITHOUT_CONTENT = 5;

	/**
	 * Reference to game search algorithm.
	 */
	private GameSearchAlgorithm algorithm = null;

	/**
	 * List of all searched states.
	 */
	private List<GameState> states = null;

	/**
	 * List of edges.
	 */
	private Set<Edge> edges = null;

	/**
	 * Wanted type of shape for states in the game tree to be generated.
	 */
	private GraphvizNodeType nodeType = null;

	/**
	 * Should moves names be displayed on edges.
	 */
	private boolean movesNamesOn = false;

	/**
	 * An edge in a game tree.
	 */
	private class Edge implements Comparable<Edge> {
		private String parentNodeId;
		private String childNodeId;

		Edge(String parentNodeId, String childNodeId) {
			this.parentNodeId = parentNodeId;
			this.childNodeId = childNodeId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Edge o) {
			int parentsComparison = parentNodeId.compareTo(o.parentNodeId);
			if (parentsComparison != 0)
				return parentsComparison;
			return childNodeId.compareTo(o.childNodeId);
		}
	}

	/**
	 * Creates a new instance of GameSearchGraphvizer.
	 * 
	 * @param algorithm reference to a game search algorithm
	 * @param nodesContentsOn boolean flag stating if nodes should be generated with contents (via toGraphvizLabel()
	 *            method) or not (just points)
	 * @param movesNamesOn boolean flag stating if move names should be displayed on edges
	 */
	private GameSearchGraphvizer(GameSearchAlgorithm algorithm, boolean nodesContentsOn, boolean movesNamesOn) {
		this.algorithm = algorithm;
		nodeType = (nodesContentsOn) ? GraphvizNodeType.WITH_CONTENT : GraphvizNodeType.WITHOUT_CONTENT;
		this.movesNamesOn = movesNamesOn;
		states = new LinkedList<GameState>();
		edges = new TreeSet<Edge>();
	}

	/**
	 * Recursively iterates over states in the game tree (using references to children) and adds them to list of states.
	 * 
	 * @param state reference to current state
	 * @param depth depth of the current state
	 */
	private void iterateOverStates(GameState state, double depth) {
		if (state != null) {
			states.add(state);
			for (GameState child : state.getChildren())
				iterateOverStates(child, depth + 1);
		}
	}

	/**
	 * Returns node id as string for given state (for Graphviz purposes).
	 * 
	 * @param state reference to a state
	 * @return Graphviz node id
	 */
	private String getNodeId(GameState state) {
		List<GameState> path = state.getPath();
		StringBuilder nodeId = new StringBuilder();
		nodeId.append("\"");
		for (GameState ancestor : path)
			nodeId.append("(" + ancestor.getIdentifier() + ")");
		nodeId.append("\"");
		return nodeId.toString();
	}

	/**
	 * Appends string representations of all nodes to the given string builder object.
	 * 
	 * @param stringBuilder reference to a string builder object
	 */
	private void outputNodes(StringBuilder stringBuilder) {
		for (GameState state : states) {
			String nodeId = getNodeId(state);

			String color = COLOR_REGULAR;
			double height = HEIGHT_REGULAR;
			if (algorithm.isGameStateTerminal(state, state.getDepth(), algorithm.getConfigurator().getDepthLimit())) {
				// terminal non win state
				color = COLOR_TERMINAL_NON_WIN;
				height = HEIGHT_TERMINAL_NON_WIN;
			}
			if (state.isWinTerminal()) {
				// terminal win state
				color = COLOR_TERMINAL_WIN;
				height = HEIGHT_TERMINAL_WIN;
			}
			if (!state.isVisited()) {
				// cutoff or transposed state
				color = COLOR_CUTOFF;
				height = HEIGHT_CUTOFF;
			}
			if (state.isReadFromTranspositionTable()) {
				// cutoff or transposed state
				color = COLOR_TRANSPOSED;
				height = HEIGHT_TRANSPOSED;
			}
			if (isGameStateAlongPrincipalVariation(state)) {
				if (state.isWinTerminal()) {
					color = COLOR_TERMINAL_WIN;
					height = HEIGHT_PRINCIPAL_VARIATION_TERMINAL_WIN;
				} else {
					color = COLOR_PRINCIPAL_VARIATION;
					height = HEIGHT_PRINCIPAL_VARIATION;
				}
			}
			if (algorithm.getInitial().equals(state)) {
				// initial state
				color = COLOR_INITIAL;
				height = HEIGHT_INITIAL;

			}

			stringBuilder.append(nodeId);
			stringBuilder.append(" [");

			// starting Graphviz label
			if (nodeType == GraphvizNodeType.WITH_CONTENT) {
				stringBuilder.append("label=<");

				// building HTML
				stringBuilder.append("<TABLE BORDER='0' CELLBORDER='1' CELLPADDING='2' CELLSPACING='0' BGCOLOR='" + color + "'>");
				// depth
				stringBuilder.append("<TR><TD><FONT FACE='monospace' POINT-SIZE='" + FONT_SIZE_WITH_CONTENT + "'>depth = " + state.getDepth()
						+ "</FONT></TD></TR>");
				// h
				final int displayHMaxLength = 6;
				String hForDisplay = Double.toString(state.getH());
				if (hForDisplay.length() > displayHMaxLength) {
					DecimalFormat formatter = new DecimalFormat("#0.##E0");
					hForDisplay = formatter.format(state.getH());
					hForDisplay = hForDisplay.replace(',', '.');
				}
				stringBuilder.append("<TR><TD><FONT FACE='monospace' POINT-SIZE='" + FONT_SIZE_WITH_CONTENT + "'>h = " + hForDisplay + "</FONT></TD></TR>");

				// entries from transposition table
				if ((algorithm.getConfigurator().isTranspositionTableOn()) && (algorithm.getTranspositionTable() != null)) {
					TranspositionTableEntry entry = algorithm.getTranspositionTable().get(state);
					if (entry != null) {
						if (entry.getExactGameValue() != null) {
							// exact value
							final int displayVMaxLength = 6;
							String vForDisplay = Double.toString(entry.getExactGameValue());
							if (vForDisplay.length() > displayVMaxLength) {
								DecimalFormat formatter = new DecimalFormat("#0.##E0");
								vForDisplay = formatter.format(entry.getExactGameValue());
								vForDisplay = vForDisplay.replace(',', '.');
							}
							stringBuilder.append("<TR><TD><FONT FACE='monospace' POINT-SIZE='" + FONT_SIZE_WITH_CONTENT + "'>v = " + vForDisplay
									+ "</FONT></TD></TR>");
						} else {
							// lower bound
							final int displayBoundMaxLength = 6;
							String boundForDisplay = "null";
							if (entry.getLowerBoundOnGameValue() != null) {
								boundForDisplay = Double.toString(entry.getLowerBoundOnGameValue());
								if (boundForDisplay.length() > displayBoundMaxLength) {
									DecimalFormat formatter = new DecimalFormat("#0.##E0");
									boundForDisplay = formatter.format(entry.getLowerBoundOnGameValue());
									boundForDisplay = boundForDisplay.replace(',', '.');
								}
								stringBuilder.append("<TR><TD><FONT FACE='monospace' POINT-SIZE='" + FONT_SIZE_WITH_CONTENT + "'>v &ge; " + boundForDisplay
										+ "</FONT></TD></TR>");
							}
							// upper bound
							boundForDisplay = "null";
							if (entry.getUpperBoundOnGameValue() != null) {
								boundForDisplay = Double.toString(entry.getUpperBoundOnGameValue());
								if (boundForDisplay.length() > displayBoundMaxLength) {
									DecimalFormat formatter = new DecimalFormat("#0.##E0");
									boundForDisplay = formatter.format(entry.getUpperBoundOnGameValue());
									boundForDisplay = boundForDisplay.replace(',', '.');
								}
								stringBuilder.append("<TR><TD><FONT FACE='monospace' POINT-SIZE='" + FONT_SIZE_WITH_CONTENT + "'>v &le; " + boundForDisplay
										+ "</FONT></TD></TR>");
							}
						}
					}
				}

				// label (by user)
				stringBuilder.append("<TR><TD><FONT FACE='monospace' POINT-SIZE='" + FONT_SIZE_WITHOUT_CONTENT + "'>"
						+ "<TABLE BORDER='0' CELLBORDER='1' CELLPADDING='2' CELLSPACING='0' BGCOLOR='white'><TR><TD>" + state.toGraphvizLabel()
						+ "</TD></TR></TABLE>" + "</FONT></TD></TR>");
				stringBuilder.append("</TABLE>");
				stringBuilder.append(">,");
			} else { // node without content version
				StringBuilder tempStringBuilder = new StringBuilder("");
				if ((algorithm.getConfigurator().isTranspositionTableOn()) && (algorithm.getTranspositionTable() != null)) {
					TranspositionTableEntry entry = algorithm.getTranspositionTable().get(state);
					if (entry != null) {
						if (entry.getExactGameValue() != null) {
							// exact value
							final int displayVMaxLength = 6;
							String vForDisplay = Double.toString(entry.getExactGameValue());
							if (vForDisplay.length() > displayVMaxLength) {
								DecimalFormat formatter = new DecimalFormat("#0.#E0");
								vForDisplay = formatter.format(entry.getExactGameValue());
								vForDisplay = vForDisplay.replace(',', '.');
							}
							tempStringBuilder.append("<TR><TD><FONT FACE='monospace' POINT-SIZE='" + FONT_SIZE_WITHOUT_CONTENT + "' COLOR='"
									+ COLOR_GAME_VALUE_WIHTOUT_CONTENT + "'>" + vForDisplay + "</FONT></TD></TR>");
						} else {
							// lower bound
							final int displayBoundMaxLength = 6;
							String boundForDisplay = "null";
							if (entry.getLowerBoundOnGameValue() != null) {
								boundForDisplay = Double.toString(entry.getLowerBoundOnGameValue());
								if (boundForDisplay.length() > displayBoundMaxLength) {
									DecimalFormat formatter = new DecimalFormat("#0.#E0");
									boundForDisplay = formatter.format(entry.getLowerBoundOnGameValue());
									boundForDisplay = boundForDisplay.replace(',', '.');
								}
								tempStringBuilder.append("<TR><TD><FONT FACE='monospace' POINT-SIZE='" + FONT_SIZE_WITHOUT_CONTENT + "' COLOR='"
										+ COLOR_GAME_VALUE_WIHTOUT_CONTENT + "'>&ge;" + boundForDisplay + "</FONT></TD></TR>");
							}
							// upper bound
							boundForDisplay = "null";
							if (entry.getUpperBoundOnGameValue() != null) {
								boundForDisplay = Double.toString(entry.getUpperBoundOnGameValue());
								if (boundForDisplay.length() > displayBoundMaxLength) {
									DecimalFormat formatter = new DecimalFormat("#0.#E0");
									boundForDisplay = formatter.format(entry.getUpperBoundOnGameValue());
									boundForDisplay = boundForDisplay.replace(',', '.');
								}
								tempStringBuilder.append("<TR><TD><FONT FACE='monospace' POINT-SIZE='" + FONT_SIZE_WITHOUT_CONTENT + "' COLOR='"
										+ COLOR_GAME_VALUE_WIHTOUT_CONTENT + "'>&le;" + boundForDisplay + "</FONT></TD></TR>");
							}
						}
					}
				}
				if (tempStringBuilder.toString().length() > 0) {
					stringBuilder.append("xlabel=<");
					stringBuilder.append("<TABLE BORDER='0' CELLBORDER='0' CELLPADDING='0' CELLSPACING='0'>");
					stringBuilder.append(tempStringBuilder.toString());
					stringBuilder.append("</TABLE>");
					stringBuilder.append(">,xlp=\"0.0,0.0\",");
				}
			}
			stringBuilder.append("fillcolor=" + color + ",height=" + height);
			stringBuilder.append("];\r\n"); // end of Graphviz label
		}
	}

	/**
	 * Appends string representations of all edges to the given string builder object.
	 * 
	 * @param stringBuilder reference to a string builder object
	 */
	private void outputEdges(StringBuilder stringBuilder) {
		String parentId = null;
		String nodeId = null;

		for (GameState state : states) {
			GameState parent = state.getParent();
			if (parent == null)
				continue;

			parentId = getNodeId(parent);
			nodeId = getNodeId(state);
			Edge edge = new Edge(parentId, nodeId);
			if (!edges.contains(edge)) {
				edges.add(edge);
				stringBuilder.append(parentId + " -> " + nodeId);
				stringBuilder.append("[");
				if (movesNamesOn)
					stringBuilder.append("label=<<FONT FACE='monospace' POINT-SIZE='"
							+ ((nodeType == GraphvizNodeType.WITH_CONTENT) ? FONT_SIZE_WITH_CONTENT + "'>&nbsp;" : FONT_SIZE_WITHOUT_CONTENT + "'>")
							+ state.getMoveName() + "</FONT>>,");
				if (nodeType == GraphvizNodeType.WITH_CONTENT)
					stringBuilder.append("arrowsize=0.4];\r\n");
				else
					stringBuilder.append("arrowsize=0.3,penwidth=0.6];\r\n");
			}
		}
	}

	/**
	 * Builds and returns representation of the search tree as a Graphviz string.
	 * 
	 * @return representation of search tree as a Graphviz string
	 */
	private String makeTree() {
		iterateOverStates(algorithm.getInitial(), algorithm.getInitial().getDepth());
		StringBuilder sb = new StringBuilder();

		// graph attributes
		sb.append("digraph g {\r\n");
		sb.append("forcelabels=true;\r\n");
		sb.append("ranksep=0.4;\r\n");
		sb.append("nodesep=0.15;\r\n");
		if (nodeType == GraphvizNodeType.WITHOUT_CONTENT)
			sb.append("outputorder=edgesfirst;\r\n");

		sb.append("node [shape=" + nodeType.toString() + ",height=" + HEIGHT_REGULAR + "];\r\n"); // node attributes
		outputNodes(sb);
		outputEdges(sb);
		sb.append("}");
		ConsoleLogger.info("Generating a graph file for Graphviz with " + this.states.size() + " nodes.");
		return sb.toString();
	}

	private boolean isGameStateAlongPrincipalVariation(GameState gameState) {
		List<String> principalVariation = algorithm.getInitial().getMovesAlongPrincipalVariation();
		List<String> path = gameState.getMovesAlongPath();

		if (path.isEmpty())
			return true;
		int minSize = (int) Math.min(principalVariation.size(), path.size());
		for (int i = 0; i < minSize; i++)
			if (!path.get(i).equals(principalVariation.get(i)))
				return false;

		return true;
	}

	/**
	 * Builds and saves the Graphviz representation of a search tree (based on a terminated search algorithm).
	 * 
	 * @param algorithm reference to search algorithm
	 * @param outputFilePath wanted location to save the file
	 * @param nodesContentsOn boolean flag stating if nodes should be generated with contents (via toGraphvizLabel()
	 *            method) or not (just points)
	 * @param movesNamesOn boolean flag stating if move names should be displayed on edges
	 */
	public static void go(GameSearchAlgorithm algorithm, String outputFilePath, boolean nodesContentsOn, boolean movesNamesOn) {
		try {
			FileWriter fstream = new FileWriter(outputFilePath);
			BufferedWriter out = new BufferedWriter(fstream);
			GameSearchGraphvizer graphvizer = new GameSearchGraphvizer(algorithm, nodesContentsOn, movesNamesOn);
			out.write(graphvizer.makeTree());
			out.close();
			fstream.close();
		} catch (IOException ioe) {
			String message = "IOException occurred while trying to create file for Graphviz: " + ioe.getMessage();
			ConsoleLogger.info(message);
			System.err.println(message);
		}
	}
}