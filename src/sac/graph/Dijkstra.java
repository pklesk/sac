package sac.graph;

import java.util.Comparator;

/**
 * Dijkstra's algorithm.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class Dijkstra extends GraphSearchAlgorithm {

	/**
	 * Creates new instance of Dijkstra's algorithm.
	 * 
	 * @param initial reference to initial state
	 * @param configurator reference to configurator object
	 */
	public Dijkstra(GraphState initial, GraphSearchConfigurator configurator) {
		super(initial, configurator);
		setupOpenAndClosedSets(new DijkstraComparator());
	}

	/**
	 * Creates new instance of Dijkstra's algorithm.
	 * 
	 * @param initial reference to initial state
	 */
	public Dijkstra(GraphState initial) {
		this(initial, null);
	}

	/**
	 * Creates new instance of Dijkstra's algorithm.
	 */
	public Dijkstra() {
		this(null, null);
	}

	/**
	 * Comparator for Dijkstra's algorithm.
	 */
	private class DijkstraComparator implements Comparator<GraphState> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(GraphState gs1, GraphState gs2) {
			double difference = gs1.getG() - gs2.getG();
			if (difference == 0.0) {
				return gs1.getIdentifier().compareTo(gs2.getIdentifier());
			} else {
				return (difference > 0.0) ? 1 : -1;
			}
		}
	}
}