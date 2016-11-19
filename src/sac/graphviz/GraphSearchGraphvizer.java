package sac.graphviz;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.TreeSet;

import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphState;
import sac.util.ConsoleLogger;

/**
 * Generator of files (based on a terminated graph search algorithm) which are compliant with Graphviz software
 * (http://www.graphviz.org).
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class GraphSearchGraphvizer {

	/**
	 * Visualization constant - height of the initial state.
	 */
	private static final double HEIGHT_INITIAL = 0.2;
	
	/**
	 * Visualization constant - height of closed states.
	 */
	private static final double HEIGHT_CLOSED = 0.1;
	
	/**
	 * Visualization constant - height of open states.
	 */
	private static final double HEIGHT_OPEN = 0.06;

	/**
	 * Visualization constant - height of state lying on a path to solution.
	 */
	private static final double HEIGHT_PATH = 0.3;

	/**
	 * Visualization constant - height of the solution state.
	 */
	private static final double HEIGHT_SOLUTION = 0.4;

	/**
	 * Visualization constant - color for the initial state.
	 */
	private static final String COLOR_INITIAL = "yellow";

	/**
	 * Visualization constant - color for closed states.
	 */
	private static final String COLOR_CLOSED = "whitesmoke";
	
	/**
	 * Visualization constant - color for open states.
	 */
	private static final String COLOR_OPEN = "orangered";

	/**
	 * Visualization constant - fill color for states on a path to the solution.
	 */
	private static final String COLOR_PATH = "limegreen";
	
	/**
	 * Visualization constant - color for the solution state.
	 */
	private static final String COLOR_SOLUTION = "steelblue";


	/**
	 * Reference to a graph search algorithm.
	 */
	private GraphSearchAlgorithm algorithm = null;

	/**
	 * Set of all searched states (note that set structure is used rather than a list to avoid duplications).
	 */
	private Set<GraphState> states = null;

	/**
	 * Set of paths.
	 */
	private Set<GraphState> paths = null;

	/**
	 * Smallest depth at which a solution was found.
	 */
	private double solutionDepth = 0;

	/**
	 * Wanted type of shape for states in the search graph to be generated.
	 */
	private GraphvizNodeType nodeType = null;
	
	/**
	 * Should moves names be displayed on edges.
	 */
	private boolean movesNamesOn = false;
	
	/**
	 * Creates a new instance of GraphSearchGraphvizer.
	 * 
	 * @param algorithm reference to a graph search algorithm
	 * @param nodesContentsOn boolean flag stating if nodes should be generated with contents (via toGraphvizLabel() method) or not (just points)
	 * @param movesNamesOn boolean flag stating if move  names should be displayed on edges
	 */
	private GraphSearchGraphvizer(GraphSearchAlgorithm algorithm, boolean nodesContentsOn, boolean movesNamesOn) {
		this.algorithm = algorithm;
		nodeType = (nodesContentsOn) ? GraphvizNodeType.WITH_CONTENT : GraphvizNodeType.WITHOUT_CONTENT;
		this.movesNamesOn = movesNamesOn;

		states = new TreeSet<GraphState>();
		paths = new TreeSet<GraphState>();
		for (GraphState solution : algorithm.getSolutions()) {
			if (solutionDepth < solution.getDepth()) {
				solutionDepth = solution.getDepth();
			}
			paths.addAll(solution.getPath());
		}
	}

	/**
	 * Recursively iterates over states in the search graph (using references to children) and adds them to set of
	 * states.
	 * 
	 * @param state reference to current state
	 * @param depth depth of the current state
	 */
	private void iterateOverStates(GraphState node, double depth) {
		if ((states != null) && (!states.contains(node))) {
			states.add(node);
			if (algorithm.getClosedSet() != null) {
				if ((algorithm.getClosedSet().contains(node)) && (depth < solutionDepth + 2)) {
					for (GraphState child : node.getChildren())
						if ((child.getDepth() == depth + 1) && (!states.contains(child))) 
							iterateOverStates(child, depth + 1);
				}
			}
			else if (depth < solutionDepth + 2) {
				for (GraphState child : node.getChildren())
					if ((child.getDepth() == depth + 1) && (!states.contains(child))) 
						iterateOverStates(child, depth + 1);
			}
		}
	}

	/**
	 * Returns node id as string for given state (for Graphviz purposes).
	 * 
	 * @param state reference to a state
	 * @return Graphviz node id
	 */
	private String getNodeId(GraphState state) {		
		StringBuilder nodeId = new StringBuilder();
		nodeId.append("\n");
		nodeId.append((state == null) ? "null" : state.getIdentifier().toString());
		nodeId.append("\n");
		return nodeId.toString();
	}

	/**
	 * Appends string representations of all nodes to the given string builder object.
	 * 
	 * @param stringBuilder reference to a string builder object
	 */
	private void outputNodes(StringBuilder stringBuilder) {		
		for (GraphState state : states) {
			String nodeId = getNodeId(state);
			
			String color = COLOR_CLOSED;
			double height = HEIGHT_CLOSED;
			if (algorithm.getOpenSet().contains(state)) {
				// open state
				color = COLOR_OPEN;
				height = HEIGHT_OPEN;
			}
			if (paths.contains(state)) {
				// path state
				color = COLOR_PATH;
				height = HEIGHT_PATH;
			}
			if (algorithm.getInitial().equals(state)) {
				// initial state
				color = COLOR_INITIAL;
				height = HEIGHT_INITIAL;

			}
			if (algorithm.getSolutions().contains(state)) {
				// solution state
				color = COLOR_SOLUTION;
				height = HEIGHT_SOLUTION;
			}
						
			stringBuilder.append(nodeId);
			stringBuilder.append(" [");
			
			// starting Graphviz label			
			if (nodeType == GraphvizNodeType.WITH_CONTENT) {
				stringBuilder.append("label=<");
				// building HTML
				stringBuilder.append("<TABLE BORDER='0' CELLBORDER='1' CELLPADDING='2' CELLSPACING='0' BGCOLOR='" + color + "'>");									
				// depth 			
				stringBuilder.append("<TR><TD><FONT FACE='monospace' POINT-SIZE='8'>depth = " + state.getDepth() + "</FONT></TD></TR>");
				// g 
				final int displayFunctionsMaxLength = 6;
				String gForDisplay = Double.toString(state.getG());
				if (gForDisplay.length() > displayFunctionsMaxLength) {
					DecimalFormat formatter = new DecimalFormat("#0.##E0");
					gForDisplay = formatter.format(state.getG());	
					gForDisplay = gForDisplay.replace(',', '.');
				}
				stringBuilder.append("<TR><TD><FONT FACE='monospace' POINT-SIZE='8'>g = " + gForDisplay + "</FONT></TD></TR>");
				// h 
				String hForDisplay = Double.toString(state.getH());
				if (hForDisplay.length() > displayFunctionsMaxLength) {
					DecimalFormat formatter = new DecimalFormat("#0.##E0");
					hForDisplay = formatter.format(state.getH());
					hForDisplay = hForDisplay.replace(',', '.');
				}
				stringBuilder.append("<TR><TD><FONT FACE='monospace' POINT-SIZE='8'>h = " + hForDisplay + "</FONT></TD></TR>");
				// f
				String fForDisplay = Double.toString(state.getF());
				if (fForDisplay.length() > displayFunctionsMaxLength) {
					DecimalFormat formatter = new DecimalFormat("#0.##E0");
					fForDisplay = formatter.format(state.getF());
					fForDisplay = fForDisplay.replace(',', '.');
				}
				stringBuilder.append("<TR><TD><FONT FACE='monospace' POINT-SIZE='8'>f = " + fForDisplay + "</FONT></TD></TR>");
				// label (by user)
				stringBuilder.append("<TR><TD>" 
						+ "<TABLE BORDER='0' CELLBORDER='1' CELLPADDING='2' CELLSPACING='0' BGCOLOR='white'><TR><TD>"
						+ state.toGraphvizLabel() 
						+ "</TD></TR></TABLE>"
						+ "</TD></TR>");			
				stringBuilder.append("</TABLE>");
				stringBuilder.append(">,");
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
		String parentId = "";
		String nodeId = "";
		for (GraphState state : states) {
			GraphState parent = state.getParent();
			parentId = getNodeId(parent);
			nodeId = getNodeId(state);
			if (parent != null) {
				stringBuilder.append(parentId + " -> " + nodeId);
				stringBuilder.append("[");
				if (movesNamesOn) 
					stringBuilder.append("label=<<FONT FACE='monospace' POINT-SIZE='6'>" + ((nodeType == GraphvizNodeType.WITH_CONTENT) ?  "&nbsp;" : "")  + state.getMoveName() + "</FONT>>,");
				stringBuilder.append("arrowsize=0.4];\r\n");
			}
		}
	}

	/**
	 * Builds and returns representation of the search graph as a Graphviz string.
	 * 
	 * @return representation of search tree as a Graphviz string
	 */
	private String makeTree() {
		iterateOverStates(algorithm.getInitial(), algorithm.getInitial().getDepth());
		StringBuilder sb = new StringBuilder();
		sb.append("digraph g {\r\n");
		sb.append("ranksep=0.25;\r\n"); // graph attributes
		sb.append("node [shape=" + nodeType.toString() + ",height=" + HEIGHT_CLOSED + "];\r\n"); // node attributes
		outputNodes(sb);
		outputEdges(sb);
		sb.append("}");
		ConsoleLogger.info("Generating a graph file for Graphviz with " + states.size() + " nodes.");
		return sb.toString();
	}

	/**
	 * Builds and saves the Graphviz representation of a search graph (based on a terminated search algorithm).
	 * 
	 * @param algorithm reference to search algorithm
	 * @param outputFilePath wanted location to save the file
	 * @param nodesContentsOn boolean flag stating if nodes should be generated with contents (via toGraphvizLabel() method) or not (just points)
	 * @param movesNamesOn boolean flag stating if move  names should be displayed on edges
	 */
	public static void go(GraphSearchAlgorithm algorithm, String outputFilePath, boolean nodesContentsOn, boolean movesNamesOn) {
		try {
			FileWriter fstream = new FileWriter(outputFilePath);
			BufferedWriter out = new BufferedWriter(fstream);
			GraphSearchGraphvizer graphvizer = new GraphSearchGraphvizer(algorithm, nodesContentsOn, movesNamesOn);
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