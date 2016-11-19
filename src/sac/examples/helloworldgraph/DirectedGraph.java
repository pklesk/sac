package sac.examples.helloworldgraph;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import sac.graph.Dijkstra;
import sac.graph.GraphSearchAlgorithm;

/**
 * Exemplary implementation of directed graph with weighted edges.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class DirectedGraph {
	
	/**
	 * Costs of edges.
	 */
	private double[][] costs;
	
	/**
	 * Index of goal state (indexing starts from 0).
	 */
	private int goal;
	
	/**
	 * Creates new instance of directed graph.
	 * 
	 * @param howManyNodes how many nodes
	 * @param goal index of the goal state (indexing starts from 0)
	 */
	public DirectedGraph(int howManyNodes, int goal) {		
		costs = new double[howManyNodes][howManyNodes];
		for (int i = 0; i < howManyNodes; i++)
			for (int j = 0; j < howManyNodes; j++)
				costs[i][j] = Double.POSITIVE_INFINITY;
		this.goal = goal;
	}	
	
	/**
	 * Adds an edge from i to j along with its cost.
	 * 
	 * @param i index of the starting node in edge
	 * @param j index of the ending node in edge
	 * @param cost cost of edge
	 */
	public void addEdge(int i, int j, double cost) {
		costs[i][j] = cost;
	}
	
	/**
	 * Returns the array of cots.
	 * 
	 * @return array of costs
	 */
	public double[][] getCosts() {
		return costs;
	}
	
	/**
	 * Returns the index of the goal state.
	 * 
	 * @return index of the goal state
	 */
	public int getGoal() {
		return goal;
	}
		
	private String toGraphvizString() {
		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		df.applyPattern("#.###");
		
		StringBuilder sb =  new StringBuilder();
		sb.append("digraph g {\nrankdir=LR; \nnode [shape=circle];\n");
		
		for (int i = 0; i < costs.length; i++) {
			sb.append("" + i + " [" + ",label=<<FONT FACE='monospace' POINT-SIZE='11'>");
			if (i == goal) 
				sb.append("<B><FONT POINT-SIZE='14'>" + i + "</FONT></B>");
			else
				sb.append(i);
			sb.append("</FONT>>];\n");
		}			
		
		for (int i = 0; i < costs.length; i++)
			for (int j = 0; j < costs.length; j++)
				if (costs[i][j] < Double.POSITIVE_INFINITY) {
					sb.append("" + i + " -> " + j + " [arrowsize=0.4,label=<<FONT FACE='monospace' POINT-SIZE='8'>" + df.format(costs[i][j]) + "</FONT>>];\n");
				}
		sb.append("}");
		return sb.toString();
	}		

	public static void main(String[] args) throws Exception { 
		// "Hello world" for a graph 
		DirectedGraph myGraph = new DirectedGraph(8, 7);
		myGraph.addEdge(0, 1, 3.0);
		myGraph.addEdge(0, 2, 1.0);
		myGraph.addEdge(0, 5, 2.5);			
		myGraph.addEdge(1, 3, 2.0);
		myGraph.addEdge(1, 4, 1.5);
		myGraph.addEdge(2, 1, 1.0);
		myGraph.addEdge(2, 4, 3.0);
		myGraph.addEdge(3, 7, 1.0);
		myGraph.addEdge(4, 7, 2.0);
		myGraph.addEdge(5, 6, 4.0);
		myGraph.addEdge(6, 7, 0.5);				
				
		HelloWorldGraphState.dg = myGraph;
		HelloWorldGraphState state = new HelloWorldGraphState(0);				
								
		GraphSearchAlgorithm algorithm = new Dijkstra(state);
		algorithm.execute();
		HelloWorldGraphState solution = (HelloWorldGraphState) algorithm.getSolutions().get(0); 		
		
		System.out.println("SOLUTION: " + solution); 
		System.out.println("PATH: " + solution.getPath());
		System.out.println("PATH COST: " + solution.getG());		
		
		System.out.println("DURATION: " + algorithm.getDurationTime());
		System.out.println("CLOSED: " + algorithm.getClosedStatesCount());
		System.out.println("CLOSED: " + algorithm.getClosedSet().size());
		System.out.println("OPEN: " + algorithm.getOpenSet().size());
	}
}