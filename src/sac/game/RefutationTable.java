package sac.game;

import java.util.List;

/**
 * Common interface for refutation table containers. The purpose of a refutation table is to keep information about
 * such moves - (parent, child) pairs - that led to the best payoff for given parent or even caused a cut-off in the previous search iteration. 
 * This information can be taken advantage of in the future and be used to reorder children in the next search iteration (hoping from sooner
 * cut-offs). Typically, a refutation table should keep inside two collections - 'save collection' (meant for saving
 * entries for next tree search iteration), and 'read collection' (meant for reading entries from previous tree search
 * iteration).
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public interface RefutationTable {

	/**
	 * Adds new entry in refutation table for given move described by a (parent, child) pair.
	 * 
	 * @param parent reference to parent game state
	 * @param child reference to child game state
	 */
	public void put(GameState parent, GameState child);

	/**
	 * Reorders the given list of children (for given parent) using information kept in refutation table. More
	 * precisely, this method is supposed to put in front the state that caused a cut-off in the previous search
	 * iteration. The counter of uses should be updated within this method call.
	 * 
	 * @param parent reference to parent state
	 * @param children list of references to children states
	 */
	public void reorder(GameState parent, List<GameState> children);

	/**
	 * Returns current size of refutation table (total number of entries in both 'save' and 'read' collections).
	 * 
	 * @return current size of refutation table
	 */
	public int size();

	/**
	 * Returns boolean flag stating if refutation table is empty
	 * 
	 * @return boolean flag stating if refutation table is empty
	 */
	public boolean isEmpty();

	/**
	 * Clears the whole transposition table.
	 */
	public void clear();

	/**
	 * Resets the refutation table before next tree search iteration is started. This method is called by a game search
	 * algorithm in its execute() method and is meant to: (1) make the 'read collection' become the 'save collection'
	 * from the previous search (so that entries can be used by next search), and (2) emptify the 'save collection' (so
	 * that new entries can be collected in it in the next search).
	 */
	public void reset();

	/**
	 * Returns number of uses - calls to get() method - since construction or last clear() call.
	 * 
	 * @return number of uses
	 */
	public int getUsesCount();

	/**
	 * Returns the depth limit (up to which entries are memorized).
	 * 
	 * @return depth limit
	 */
	public double getDepthLimit();

	/**
	 * Sets new depth limit (up to which entries are memorized).
	 * 
	 * @param depthLimit depth limit to be set
	 */
	public void setDepthLimit(double depthLimit);
}