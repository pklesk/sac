package sac.examples.helloworldgraph;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

import sac.graph.AStar;
import sac.graph.GraphSearchAlgorithm;

/**
 * Exemplary implementation of directed graph with weighted edges and 2D coordinates of nodes.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class DirectedGraphWith2DCoordinates extends DirectedGraph {

	/**
	 * The coordinates.
	 */
	private double[][] coordinates;
	
	/**
	 * Creates new instance of directed graph with 2D coordinates. 
	 * 
	 * @param howManyNodes how many nodes
	 * @param goal index of the goal state (indexing starts from 0)
	 */
	public DirectedGraphWith2DCoordinates(int howManyNodes, int goal) {
		super(howManyNodes, goal);
		coordinates = new double[howManyNodes][2];
	}
	
	/**
	 * Adds coordinates to i-th node.
	 * 
	 * @param i node index
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public void addCoordinates(int i, double x, double y) {
		coordinates[i][0] = x;
		coordinates[i][1] = y;
	}

	/**
	 * Returns the array with coordinates.
	 * 
	 * @return array with coordinates
	 */
	public double[][] getCoordinates() {
		return coordinates;
	}
	
	private String toGraphvizString() {
		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		df.applyPattern("#.##");
		
		StringBuilder sb =  new StringBuilder();
		sb.append("digraph g {\nsplines=curved; \nnode [shape=circle];\n");
		
		for (int i = 0; i < getCosts().length; i++) {
			String position = "pos=\"" + 1.0 * coordinates[i][0] + "," + 1.0 * coordinates[i][1] + "!\""; 
			sb.append("" + i + " [" + position + ",label=<<FONT FACE='monospace' POINT-SIZE='22'>");
			if (i == getGoal()) 
				sb.append("<B><FONT POINT-SIZE='28'>" + i + "</FONT></B>");
			else
				sb.append(i);
			sb.append("</FONT>>];\n");
		}			
		
		for (int i = 0; i < getCosts().length; i++)
			for (int j = 0; j < getCosts().length; j++)
				if (getCosts()[i][j] < Double.POSITIVE_INFINITY) {
					sb.append("" + i + " -> " + j + " [penwidth=0.25,color=lightgray,arrowsize=0.4,label=<<FONT FACE='monospace' POINT-SIZE='16'>" + df.format(getCosts()[i][j]) + "</FONT>>];\n");
				}
		sb.append("}");
		return sb.toString();
	}		

	public static void main(String[] args) throws Exception {
		
		int n = 100;		
		DirectedGraphWith2DCoordinates myGraph = new DirectedGraphWith2DCoordinates(n, n - 1);		
				
		double side = 100.0;		
		myGraph.addCoordinates(0, 0.0, side); //initial
		myGraph.addCoordinates(n - 1, side, 0.0); //goal
		
		Random random = new Random(1234); // java.util.Random, imposed randomization seed: 1234 
		for (int i = 1; i < n - 1; i++) 
			myGraph.addCoordinates(i, side * random.nextDouble(), side * random.nextDouble());
		
		int e = (int) Math.round(0.1 * n * (n - 1)); // 10 % of a complete graph
		double maxDistance = 0.2 * side * Math.sqrt(2.0); // max distance between two nodes to be connected
		for (int k = 0; k < e; k++) {			
			int i, j;
			double distance;
			do {
				i = random.nextInt(n);
				j = random.nextInt(n);
				distance = Math.sqrt(
						Math.pow(myGraph.coordinates[i][0] - myGraph.coordinates[j][0], 2) 
					  + Math.pow(myGraph.coordinates[i][1] - myGraph.coordinates[j][1], 2)
					  );

			} while ((i == j) || (myGraph.getCosts()[i][j] < Double.POSITIVE_INFINITY) || (distance > maxDistance));
			
			double epsilon = random.nextDouble() * 0.1 * distance;
			myGraph.addEdge(i, j, distance + epsilon);
		}		
		
		HelloWorldGraphState.dg = myGraph;							
		
		GraphSearchAlgorithm algorithm = new AStar(new HelloWorldGraphState(0));
		algorithm.execute();
		HelloWorldGraphState solution = (HelloWorldGraphState) algorithm.getSolutions().get(0); 		
		
		System.out.println("SOLUTION: " + solution); 
		System.out.println("PATH: " + solution.getPath());
		System.out.println("PATH COST: " + solution.getG());				
		System.out.println("DURATION [ms]: " + algorithm.getDurationTime());
		System.out.println("CLOSED: " + algorithm.getClosedStatesCount());
		System.out.println("OPEN: " + algorithm.getOpenSet().size());		
	}
}
