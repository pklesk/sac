package sac.graph;

import java.util.Comparator;

/**
 * Best First Search algorithm.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class BestFirstSearch extends GraphSearchAlgorithm {

	/**
	 * Creates new instance of Best First Search algorithm.
	 * 
	 * @param initial reference to initial state
	 * @param configurator reference to configurator object
	 */
	public BestFirstSearch(GraphState initial, GraphSearchConfigurator configurator) {
		super(initial, configurator);
		setupOpenAndClosedSets(new BestFirstSearchComparator());
	}

	/**
	 * Creates new instance of Best First Search algorithm.
	 * 
	 * @param initial reference to initial state
	 */
	public BestFirstSearch(GraphState initial) {
		this(initial, null);
	}

	/**
	 * Creates new instance of Best First Search algorithm.
	 */
	public BestFirstSearch() {
		this(null, null);
	}

	/**
	 * Comparator for Best First Search algorithm.
	 */
	private class BestFirstSearchComparator implements Comparator<GraphState> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(GraphState gs1, GraphState gs2) {
			double difference = gs1.getH() - gs2.getH();
			if (difference == 0.0) {
				return gs1.getIdentifier().compareTo(gs2.getIdentifier());
			} else {
				return (difference > 0.0) ? 1 : -1;
			}
		}
	}
}