package sac.graph;

import java.util.Comparator;

/**
 * Common interface for containers with open states.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public interface OpenSet {

	/**
	 * Adds new graph state to open set in the suitable place (according to order specified by comparator).
	 * 
	 * @param graphState graph state to be added
	 */
	public void add(GraphState graphState);

	/**
	 * Polls the first graph state from open set.
	 * 
	 * @return first graph state in open set
	 */
	public GraphState poll();

	/**
	 * Peeks at first graph state in open set (the state remains in open set).
	 * 
	 * @return first graph state in open set
	 */
	public GraphState peek();

	/**
	 * Checks if open set contains given graph state.
	 * 
	 * @param graphState graph state to be checked if exists in open set
	 * @return boolean flag stating if graph state is in open set
	 */
	public boolean contains(GraphState graphState);

	/**
	 * Replaces state existing in open set with its better counterpart.
	 * 
	 * @param graphState graph state to be replaced
	 * @param replacer graph state - replacer
	 */
	public void replace(GraphState graphState, GraphState replacer);

	/**
	 * Gets reference to an equivalent of given graph state from open set.
	 * 
	 * @param graphState graph state to be found in open set
	 * @return reference to graph state in open set
	 */
	public GraphState get(GraphState graphState);

	/**
	 * Returns current size of open set (numer of graph states in it).
	 * 
	 * @return current size of open set
	 */
	public int size();

	/**
	 * Returns boolean flag stating if open set is empty
	 * 
	 * @return boolean flag stating if open set is empty
	 */
	public boolean isEmpty();

	/**
	 * Clears whole open set.
	 */
	public void clear();

	/**
	 * Returns reference to comparator, which orders this open set.
	 * 
	 * @return reference to comparator , which orders this open set
	 */
	public Comparator<GraphState> getComparator();
}