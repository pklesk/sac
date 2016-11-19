package sac.game;

import java.util.LinkedList;
import java.util.List;

import sac.StateImpl;

/**
 * Abstract partial implementation of GameState interface. User game state classes should extend this class.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public abstract class GameStateImpl extends StateImpl implements GameState {

	/**
	 * Boolean value indicating whether it is the maximizing player turn to play now.
	 */
	protected boolean maximizingTurnNow = true;

	/**
	 * List of moves along principal variation.
	 */
	protected List<String> movesAlongPrincipalVariation = null;

	/**
	 * Boolean value indicating whether this state instance is flagged as visited during search. It is possible that
	 * copies of the same state in different places of search tree will have different 'visited' flags. Some of copies
	 * might be generated but cut off, or read from the transposition table. This flag is memorized only for informative
	 * purposes (in particular for Graphviz functionality).
	 */
	protected boolean visited = false;

	/**
	 * Boolean value indicating whether this state instance is flagged as read (evaluated) from transposition table. It
	 * is possible that copies of the same state in different places of search tree will have different
	 * 'readFromTranspositionTable' flags. This flag is memorized only for informative purposes (in particular for
	 * Graphviz functionality).
	 */
	protected boolean readFromTranspositionTable = false;

	/**
	 * Constructor for this abstract class. Initializes list for moves along principal variation as an empty list
	 * (linked list).
	 */
	public GameStateImpl() {
		movesAlongPrincipalVariation = new LinkedList<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameState#getParent()
	 */
	@Override
	public GameState getParent() {
		return (parent == null) ? null : (GameState) parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameState#getChildren()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final List<GameState> getChildren() {
		return (List<GameState>) children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameState#getPath()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final List<GameState> getPath() {
		return (List<GameState>) super.getPath();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameState#isMaximizingTurnNow()
	 */
	@Override
	public final boolean isMaximizingTurnNow() {
		return maximizingTurnNow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameState#setMaximizingTurnNow(boolean)
	 */
	@Override
	public final void setMaximizingTurnNow(boolean maximizingTurnNow) {
		this.maximizingTurnNow = maximizingTurnNow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameState#isQuiet()
	 */
	@Override
	public boolean isQuiet() {
		return true; // default implementation
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameState#isVisited()
	 */
	@Override
	public final boolean isVisited() {
		return visited;
	}	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameState#setVisited()
	 */
	@Override
	public final void setVisited(boolean visited) {
		this.visited = visited;
	}	

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameState#isReadFromTranspositionTable()
	 */
	@Override
	public boolean isReadFromTranspositionTable() {
		return readFromTranspositionTable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameState#setReadFromTranspositionTable()
	 */
	@Override
	public void setReadFromTranspositionTable(boolean readFromTranspositionTable) {
		this.readFromTranspositionTable = readFromTranspositionTable;		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameState#getMovesAlongPrincipalVariation()
	 */
	@Override
	public final List<String> getMovesAlongPrincipalVariation() {
		return movesAlongPrincipalVariation;
	}

	@Override
	public boolean isNonWinTerminal() {
		return false;
	}	
	
	@Override
	public final boolean isWinTerminal() {
		return Math.abs(getH()) >= H_SMALLEST_INFINITY;
	}
}