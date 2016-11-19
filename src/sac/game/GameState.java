package sac.game;

import java.util.List;

import sac.State;

/**
 * An abstract state in some game tree search problem.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public interface GameState extends State {

	/**
	 * Constant representing the maximum possible value of heuristic function in game search algorithms (can be regarded
	 * as infinity i.e. a value representing a win).
	 */
	public static final double H_SMALLEST_INFINITY = 0.5 * Double.MAX_VALUE;

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#getParent()
	 */
	@Override
	public GameState getParent();

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#getChildren()
	 */
	@Override
	public List<GameState> getChildren();

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#getPath()
	 */
	@Override
	public List<GameState> getPath();

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#genereateChildren()
	 */
	@Override
	public List<GameState> generateChildren();

	/**
	 * Returns boolean value indicating whether this state is quiet (for Quiescence option purposes).
	 * 
	 * @return boolean value indicating whether this state is quiet
	 */
	public boolean isQuiet();

	/**
	 * Returns boolean value indicating whether this state is flagged as visited during search. It is possible that
	 * copies of the same state in different places of search tree will have different 'visited' flags. Some of copies
	 * might be generated but cut off, or read from the transposition table.
	 * 
	 * @return boolean value indicating whether this state is flagged as visited during search
	 */
	public boolean isVisited();

	/**
	 * Sets boolean value indicating whether is flagged as visited during search. It is possible that
	 * copies of the same state in different places of search tree will have different 'visited' flags. Some of copies
	 * might be generated but cut off, or read from the transposition table.
	 * 
	 * @param visited value to be set
	 */
	public void setVisited(boolean visited);
	
	/**
	 * Returns boolean value indicating whether  this state instance is flagged as read (evaluated) from transposition table. It
	 * is possible that copies of the same state in different places of search tree will have different
	 * 'readFromTranspositionTable' flags. This flag is memorized only for informative purposes (in particular for
	 * Graphviz functionality).
	 * 
	 * @return boolean value indicating whether this state is flagged as read from transposition table
	 */
	public boolean isReadFromTranspositionTable();

	/**
	 * Sets boolean value indicating whether  this state instance is flagged as read (evaluated) from transposition table. It
	 * is possible that copies of the same state in different places of search tree will have different
	 * 'readFromTranspositionTable' flags. This flag is memorized only for informative purposes (in particular for
	 * Graphviz functionality).
	 * 
	 * @param readFromTranspositionTable value to be set
	 */
	public void setReadFromTranspositionTable(boolean readFromTranspositionTable);
	
	/**
	 * Returns boolean value indicating whether it is the turn for the maximizing player now.
	 * 
	 * @return boolean value indicating whether it is the turn for the maximizing player now
	 */
	public boolean isMaximizingTurnNow();

	/**
	 * Sets boolean value indicating whether it is the turn for the maximizing player now.
	 * 
	 * @param maximizingTurnNow value to be set
	 */
	public void setMaximizingTurnNow(boolean maximizingTurnNow);

	/**
	 * Returns list of moves (from this state downwards) found to be the principal variation.
	 * 
	 * @return list of moves (from this state downwards) found to be the principal variation
	 */
	public List<String> getMovesAlongPrincipalVariation();
	
	/**
	 * Returns boolean value indicating whether this state is a win terminal state. 
	 * 
	 * @return boolean value indicating whether this state is a win terminal state
	 */
	public boolean isWinTerminal();		
	
	/**
	 * Returns boolean value indicating whether this state is a terminal state (but non-win) 
	 * due to some rule of the game (e.g.~perpetual check in chess).
	 * 
	 * @return boolean value indicating whether this state is a terminal state (but non-win)
	 */
	public boolean isNonWinTerminal();
}