package sac.graph;

import java.util.Comparator;

/**
 * A* algorithm.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class AStar extends GraphSearchAlgorithm {

	/**
	 * Creates new instance of A* algorithm.
	 * 
	 * @param initial reference to initial state
	 * @param configurator reference to configurator object
	 */
	public AStar(GraphState initial, GraphSearchConfigurator configurator) {
		super(initial, configurator);
		setupOpenAndClosedSets(new AStarComparator());
	}

	/**
	 * Creates new instance of A* algorithm.
	 * 
	 * @param initial reference to initial state
	 */
	public AStar(GraphState initial) {
		this(initial, null);
	}

	/**
	 * Creates new instance of A* algorithm.
	 */
	public AStar() {
		this(null, null);
	}

	/**
	 * Comparator for A* algorithm.
	 */
	private class AStarComparator implements Comparator<GraphState> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(GraphState gs1, GraphState gs2) {
			double difference = gs1.getF() - gs2.getF();
			if (difference == 0.0) {
				return gs1.getIdentifier().compareTo(gs2.getIdentifier());
			} else {
				return (difference > 0.0) ? 1 : -1;
			}
		}
	}
}