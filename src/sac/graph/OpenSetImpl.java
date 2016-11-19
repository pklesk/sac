package sac.graph;

import java.util.Comparator;

/**
 * Abstract initial implementation of open set. Meant to be extended by actual implementations of open set.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public abstract class OpenSetImpl implements OpenSet {

	/**
	 * The comparator.
	 */
	protected Comparator<GraphState> comparator = null;

	public OpenSetImpl(Comparator<GraphState> comparator) {
		this.comparator = comparator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#getComparator()
	 */
	@Override
	public Comparator<GraphState> getComparator() {
		return comparator;
	}
}