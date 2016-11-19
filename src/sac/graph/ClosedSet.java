package sac.graph;

/**
 * Common interface for closed graph states containers.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public interface ClosedSet {

	/**
	 * Checks if closed set contains given graph state.
	 * 
	 * @param graphState graph state to be checked if exists in closed set
	 * @return boolean flag stating if graph state is in closed set
	 */
	public boolean contains(GraphState graphState);

	/**
	 * For given state returns reference to its equivalent in closed set or null if given state was not closed.
	 * 
	 * @param graphState state to be checked if exists in closed set
	 * @return reference to given state, which was already closed
	 */
	public GraphState get(GraphState graphState);

	/**
	 * Adds new graph state to closed set.
	 * 
	 * @param graphState graph state to be put to closed set
	 */
	public void put(GraphState graphState);

	/**
	 * Removes given graph state from closed set.
	 * 
	 * @param graphState graph state to be removed from closed set
	 */
	public void remove(GraphState graphState);

	/**
	 * Returns current size of closed set (number of graph states in it).
	 * 
	 * @return current size of closed set
	 */
	public int size();

	/**
	 * Returns boolean flag stating if closed set is empty
	 * 
	 * @return boolean flag stating if closed set is empty
	 */
	public boolean isEmpty();

	/**
	 * Clears the whole closed set.
	 */
	public void clear();
}