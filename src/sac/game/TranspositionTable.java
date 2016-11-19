package sac.game;

/**
 * Common interface for transposition table containers.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public interface TranspositionTable {

	/**
	 * Returns null or exact game value or suitable bound on it from the transposition table for given game state and
	 * alpha-beta window. Null is returned when the entry for given sate does not exist or when call conditions are not
	 * appropriate for a bound to be returned.
	 * 
	 * @param gameState reference to game state
	 * @param alpha alpha value
	 * @param beta beta value
	 * @return exact game value or suitable bound
	 */
	public Double get(GameState gameState, double alpha, double beta);
	
	/**
	 * Return transposition table entry or null for given game state.
	 * 
	 * @param gameState reference to game state
	 * @return transposition table entry
	 */
	public TranspositionTableEntry get(GameState gameState);

	/**
	 * Puts or updates an entry in the transpostion table for given game state and alpha-beta window.
	 * 
	 * @param gameState reference to game state
	 * @param value value to be tried for putting or updating
	 * @param alpha alpha value
	 * @param beta beta value
	 */
	public void putOrUpdate(GameState gameState, Double value, double alpha, double beta);

	/**
	 * Removes entry for given game state from transposition table.
	 * 
	 * @param gameState state given game state
	 */
	public void remove(GameState gameState);

	/**
	 * Returns current size of transposition table (number of entries in it).
	 * 
	 * @return current size of transposition table
	 */
	public int size();

	/**
	 * Returns boolean flag stating if transposition table is empty
	 * 
	 * @return boolean flag stating if transposition table is empty
	 */
	public boolean isEmpty();

	/**
	 * Clears the whole transposition table.
	 */
	public void clear();

	/**
	 * Returns number of uses - calls to get() method - since construction or last clear() call.
	 * 
	 * @return number of uses
	 */
	public int getUsesCount();
}