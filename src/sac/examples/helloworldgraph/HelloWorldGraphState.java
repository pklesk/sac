package sac.examples.helloworldgraph;

import java.util.ArrayList;
import java.util.List;

import sac.State;
import sac.StateFunction;
import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

/**
 * 'Hello World' example for a graph. Given a directed graph (assigned via a static field),
 * it shows how: children nodes can be generated, identifiers can be produced, and cost
 * functions (g, h) can be attached to a search state. 
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class HelloWorldGraphState extends GraphStateImpl {
	
	/**
	 * Static reference to a directed graph. 
	 */
	public static DirectedGraph dg = null;
	
	/**
	 * Current node index.
	 */
	private int i;	
	
	/**
	 * Creates new instance of the HelloWorldGameState state.
	 * 
	 * @param i current node index
	 */
	public HelloWorldGraphState(int i) {	
		this.i = i;		
	}
		
	@Override
	public List<GraphState> generateChildren() {
		List<GraphState> children = new ArrayList<GraphState>();
		double[][] costs = dg.getCosts();
		for (int j = 0; j < costs.length; j++)
			if (costs[i][j] < Double.POSITIVE_INFINITY)
				children.add(new HelloWorldGraphState(j));
		return children;
	}
	
	@Override
	public int hashCode() {
		return i;
	}
	
	@Override
	public boolean isSolution() {
		return (i == dg.getGoal());
	}

	@Override
	public String toString() {
		return Integer.toString(i);
	}
		
	static {		
		setGFunction(new StateFunction() {

			@Override
			public double calculate(State state) {
				HelloWorldGraphState hwgs = (HelloWorldGraphState) state;
				HelloWorldGraphState parent = (HelloWorldGraphState) hwgs.getParent();
				return (parent == null) ? 0.0 : parent.getG() + dg.getCosts()[parent.i][hwgs.i];
			}			
		}
		);
		
		setHFunction(new StateFunction() {

			@Override
			public double calculate(State state) {
				HelloWorldGraphState hwgs = (HelloWorldGraphState) state;
				if (HelloWorldGraphState.dg instanceof DirectedGraphWith2DCoordinates) {
					DirectedGraphWith2DCoordinates dg2D = (DirectedGraphWith2DCoordinates) HelloWorldGraphState.dg;
					return Math.sqrt(
							  Math.pow(dg2D.getCoordinates()[hwgs.i][0] - dg2D.getCoordinates()[dg2D.getGoal()][0], 2)
							+ Math.pow(dg2D.getCoordinates()[hwgs.i][1] - dg2D.getCoordinates()[dg2D.getGoal()][1], 2)
							);
				}
				else 
					return 0.0;
			}			
		});
	}

	@Override
	public String toGraphvizLabel() {
		return "<FONT FACE='monospace' POINT-SIZE='14'>" + i + "</FONT>";
	}		
}