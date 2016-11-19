package sac;

/**
 * Abstract search algorithm, a common class for both graph searches and game tree searches.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public abstract class SearchAlgorithm {

	/**
	 * Start time of search algorithm.
	 */
	protected long startTime;

	/**
	 * End time of search algorithm.
	 */
	protected long endTime;

	/**
	 * Returns start time of this graph search algorithm.
	 * 
	 * @return start time of this graph search algorithm
	 */
	public final long getStartTime() {
		return startTime;
	}

	/**
	 * Returns end time of this graph search algorithm.
	 * 
	 * @return end time of this graph search algorithm
	 */
	public final long getEndTime() {
		return endTime;
	}

	/**
	 * Returns duration time of this graph search algorithm. Warning: it is assumed the algorithm hast stopped,
	 * otherwise its end time is undetermined and returned duration time can be meaningless.
	 * 
	 * @return duration time of this graph search algorithm
	 */
	public final long getDurationTime() {
		return endTime - startTime;
	}

	/**
	 * Executes this search algorithm.
	 */
	public abstract void execute();

	/**
	 * Returns the number of closed states by the search algorithm during the last search (the last execute() call), or
	 * possibly if when the search is still ongoing.
	 * 
	 * @return number of closed states
	 */
	public abstract int getClosedStatesCount();
}