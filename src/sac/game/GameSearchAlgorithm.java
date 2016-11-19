package sac.game;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sac.Identifier;
import sac.SearchAlgorithm;

/**
 * Abstract game search algorithm. Meant to be extended by actual algorithms e.g.: MIN-MAX, alpha-beta cut-offs, Scout.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public abstract class GameSearchAlgorithm extends SearchAlgorithm {
	
	/**
	 * Reference to initial state.
	 */
	protected GameState initial = null;
	
	/**
	 * Reference to currently examined state.
	 */
	protected GameState current = null;

	/**
	 * Map of discovered scores for moves.
	 */
	protected Map<String, Double> movesScores = null;

	/**
	 * Transposition table.
	 */
	protected TranspositionTable transpositionTable = null;

	/**
	 * Refutation table.
	 */
	protected RefutationTable refutationTable = null;

	/**
	 * Graph search configurator object.
	 */
	protected GameSearchConfigurator configurator = null;

	/**
	 * Number of closed states (= number of calls of methods evaluateMaxState(), evaluateMinState) since last reset().
	 */
	protected int closedCount = 0;

	/**
	 * Maximum depth that was reached in the search (owing to quiescence) since last reset().
	 */
	protected double depthReached = 0.0;

	/**
	 * Boolean flag stating if stop was forced (e.g. from an outer thread).
	 */
	protected boolean stopForced = false;

	/**
	 * Creates new instance of game search algorithm.
	 * 
	 * @param initial reference to initial state
	 * @param configurator reference to configurator object
	 */
	public GameSearchAlgorithm(GameState initial, GameSearchConfigurator configurator) {
		this.configurator = (configurator != null) ? configurator : new GameSearchConfigurator();

		this.initial = initial;
		this.movesScores = new HashMap<String, Double>();

		reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.SearchAlgorithm#execute()
	 */
	@Override
	public final void execute() {
		reset();
		startTime = System.currentTimeMillis();
		doExecute();
		endTime = System.currentTimeMillis();
	}

	/**
	 * Actual execution of search algorithm invoked from inside of execute() method.
	 */
	protected void doExecute() {
		Double gameValue = null;
		if (initial.isMaximizingTurnNow())
			gameValue = evaluateMaxState(initial, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0, configurator.getDepthLimit());
		else
			gameValue = evaluateMinState(initial, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0, configurator.getDepthLimit());
		if (configurator.isTranspositionTableOn())
			transpositionTable.putOrUpdate(initial, gameValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		current = null;
	}

	/**
	 * Wrapping method around doEvaluateMaxState(...).
	 * 
	 * @param gameState given game state
	 * @param alpha lower bound on game value known for given game state
	 * @param beta upper bound on game value known for given game state
	 * @param depth current depth
	 * @param depthLimit depth limit
	 * @return calculated game value (or null if time limit is reached)
	 */
	protected final Double evaluateMaxState(GameState gameState, double alpha, double beta, double depth, double depthLimit) {
		// time limit check
		if ((stopForced) || (configurator.getTimeLimit() < Long.MAX_VALUE)) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - startTime > configurator.getTimeLimit()) {
				endTime = System.currentTimeMillis();
				return null;
			}
		}
		closedCount++;
		current = gameState;
		gameState.setVisited(true);
		return doEvaluateMaxState(gameState, alpha, beta, depth, depthLimit);
	}

	/**
	 * Wrapping method around doEvaluateMinState(...).
	 * 
	 * @param gameState given game state
	 * @param alpha lower bound on game value known for given game state
	 * @param beta upper bound on game value known for given game state
	 * @param depth current depth
	 * @param depthLimit depth limit
	 * @return calculated game value (or null if time limit is reached)
	 */
	protected final Double evaluateMinState(GameState gameState, double alpha, double beta, double depth, double depthLimit) {
		// time limit check
		if ((stopForced) || (configurator.getTimeLimit() < Long.MAX_VALUE)) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - startTime > configurator.getTimeLimit()) {
				endTime = System.currentTimeMillis();
				return null;
			}
		}
		closedCount++;
		current = gameState;
		gameState.setVisited(true);
		return doEvaluateMinState(gameState, alpha, beta, depth, depthLimit);
	}

	/**
	 * Evaluates given game state associated with the maximizing player.
	 * 
	 * @param gameState given game state
	 * @param alpha lower bound on game value known for given game state
	 * @param beta upper bound on game value known for given game state
	 * @param depth current depth
	 * @param depthLimit depth limit
	 * @return calculated game value (or null if time limit is reached)
	 */
	public abstract Double doEvaluateMaxState(GameState gameState, double alpha, double beta, double depth, double depthLimit);

	/**
	 * Evaluates given game state associated with the minimizing player.
	 * 
	 * @param gameState given game state
	 * @param alpha lower bound on game value known for given game state
	 * @param beta upper bound on game value known for given game state
	 * @param depth current depth
	 * @param depthLimit depth limit
	 * @return calculated game value (or null if time limit is reached)
	 */
	public abstract Double doEvaluateMinState(GameState gameState, double alpha, double beta, double depth, double depthLimit);

	/**
	 * Resets this algorithm. I.e. resets initial state (cuts off its children, if they exist), clears moves scores,
	 * clears transposition table. Refutation table remains not cleared due to its purpose - the progressive search.
	 */
	@SuppressWarnings("unchecked")
	protected void reset() {
		stopForced = false;

		// identifiers
		Identifier.setType(this.configurator.getIdentifierType()); // in case, it changed since last
																	// execute() call
		// root of the tree resetting
		if (initial != null) {
			initial.refresh();
			initial.setParent(null);
			initial.setMoveName("");
			initial.setDepth(0.0);
			initial.getChildren().clear();			
			recalculateHIfLarge(initial);
		}

		// clearing moves scores
		movesScores.clear();

		// transpostion table
		try {
			Constructor<TranspositionTable> constructor = (Constructor<TranspositionTable>) Class.forName(configurator.getTranspositionTableClassName())
					.getConstructor();
			transpositionTable = (TranspositionTable) constructor.newInstance();
		} catch (Exception e) {
			transpositionTable = new TranspositionTableAsHashMap();
			e.printStackTrace();
		}

		// refutation table
		if (refutationTable == null) {
			try {
				Constructor<RefutationTable> constructor = (Constructor<RefutationTable>) Class.forName(configurator.getRefutationTableClassName())
						.getConstructor(Double.TYPE);
				refutationTable = (RefutationTable) constructor.newInstance(configurator.getRefutationTableDepthLimit());
			} catch (Exception e) {
				refutationTable = new RefutationTableAsHashMap();
				e.printStackTrace();
			}
		} else
			refutationTable.reset();

		closedCount = 0;
		depthReached = 0.0;
	}

	/**
	 * Returns the map with scores of moves.
	 * 
	 * @return map with scores of moves
	 */
	public Map<String, Double> getMovesScores() {
		return movesScores;
	}

	/**
	 * Returns the best move, taking into account the player to move first (maximizing or minimizing) at initial state.
	 * If more than one best move (with equal value) is available returns the first one that occured in the scores map.
	 * It is possible that this method returns a null if time limit has been reached and no move score has been
	 * evaluated so far.
	 * 
	 * @return best move
	 */
	public final String getFirstBestMove() {
		double factor = ((initial != null) && (initial.isMaximizingTurnNow())) ? 1 : -1;
		String bestMove = null;
		double bestMoveValue = Double.NEGATIVE_INFINITY * factor;

		for (String move : movesScores.keySet()) {
			double value = movesScores.get(move);
			if (value * factor > bestMoveValue * factor) {
				bestMove = move;
				bestMoveValue = value;
			}
		}

		if ((bestMove == null) && (!movesScores.isEmpty())) // all moves are plus or minus
															// infinities, taking the first
			bestMove = movesScores.keySet().iterator().next();
		return bestMove;
	}

	/**
	 * Returns list of all best moves (with equal highest score). It is possible that this method returns an empty list
	 * if time limit has been reached and no move score has been evaluated so far.
	 * 
	 * @return list of all best moves (with equal highest score)
	 */
	public final List<String> getBestMoves() {
		double factor = ((initial != null) && (initial.isMaximizingTurnNow())) ? 1 : -1;
		double bestMoveValue = Double.NEGATIVE_INFINITY * factor;

		for (String move : movesScores.keySet()) {
			double value = movesScores.get(move);
			if (value * factor >= bestMoveValue * factor)
				bestMoveValue = value;
		}

		List<String> bestMoves = new ArrayList<String>();
		for (String move : movesScores.keySet()) {
			double value = movesScores.get(move);
			if (value * factor == bestMoveValue * factor)
				bestMoves.add(move);
		}

		return bestMoves;
	}

	/**
	 * Returns reference to initial game state.
	 * 
	 * @return reference to initial game state
	 */
	public final GameState getInitial() {
		return initial;
	}

	/**
	 * Sets reference to initial game state.
	 * 
	 * @param initial reference to initial game state
	 */
	public final void setInitial(GameState initial) {
		this.initial = initial;
	}
	
	
	/**
	 * Returns reference to currently examined state.
	 * 
	 * @return reference to currently examined state
	 */
	public GameState getCurrent() {
		return current;
	}

	/**
	 * Returns reference to transposition table.
	 * 
	 * @return reference to transposition table
	 */
	public final TranspositionTable getTranspositionTable() {
		return transpositionTable;
	}

	/**
	 * Returns reference to refutation table.
	 * 
	 * @return reference to refutation table
	 */
	public final RefutationTable getRefutationTable() {
		return refutationTable;
	}

	/**
	 * Gets reference to configurator object.
	 * 
	 * @return reference to configurator object
	 */
	public final GameSearchConfigurator getConfigurator() {
		return configurator;
	}

	/**
	 * Sets reference to configurator object.
	 * 
	 * @param configurator reference to configurator object
	 */
	public final void setConfigurator(GameSearchConfigurator configurator) {
		this.configurator = configurator;
	}

	@Override
	public final int getClosedStatesCount() {
		return closedCount;
	}

	/**
	 * Returns the maximum depth that was reached in the search (owing to quiescence) since last reset().
	 * 
	 * @return maximum depth that was reached in the search
	 */
	public final double getDepthReached() {
		return depthReached;
	}

	/**
	 * Calls parent.generateChildren() method and increments depths of children by 0.5 with respect to their parent.
	 * 
	 * @param parent reference to game state object for which children should be generated
	 * @return list of children states
	 */
	protected final List<GameState> generateChildrenWrapper(GameState parent) {
		List<GameState> children = parent.generateChildren();
		for (GameState child : children) {
			child.setParent(parent);
			child.setDepth(parent.getDepth() + 0.5);
			if (configurator.isParentsMemorizingChildren())
				parent.getChildren().add(child);
			recalculateHIfLarge(child);
		}
		return children;
	}

	/**
	 * Recalculates heuristic value for given state if its absolute value is greater than H_SMALLEST_INFINITY constant. The
	 * recalculation is done according to the formula: h = Math.signum(h) * H_SMALLEST_INFINITY * (1.0 + 1.0 /
	 * state.getDepth()).
	 * 
	 * @param state reference to game state for which the recalculation is executed
	 */
	protected final static void recalculateHIfLarge(GameState state) {
		double h = state.getH();
		if (Math.abs(h) > GameState.H_SMALLEST_INFINITY) {
			h = Math.signum(h) * GameState.H_SMALLEST_INFINITY * (1.0 + 1.0 / state.getDepth());
			state.setH(h);
		}
	}

	/**
	 * Returns a boolean flag showing if given state is a terminal (leaf).
	 * 
	 * @param gameState reference to game state
	 * @param depth current depth
	 * @param depthLimit depth limit
	 * @return boolean flag showing if given state is a terminal (leaf)
	 */
	public final boolean isGameStateTerminal(GameState gameState, double depth, double depthLimit) {
		depthReached = Math.max(depthReached, depth);
		if ((gameState.isWinTerminal()) || (gameState.isNonWinTerminal())) 
			return true;
		if (depth >= depthLimit)
			return (configurator.isQuiescenceOn()) ? gameState.isQuiet() : true;
		return false;
	}

	/**
	 * Returns a boolean flag stating if given value of a child is an exact game value for given alpha-beta window.
	 * 
	 * @param childValue value
	 * @param alpha alpha value
	 * @param beta beta value
	 * @return boolean flag stating if given value of a child is an exact game value for given alpha-beta window
	 */
	public final static boolean isExactGameValue(double childValue, double alpha, double beta) {
		return ((alpha < childValue) && (childValue < beta)) || ((childValue == Double.NEGATIVE_INFINITY) && (alpha == Double.NEGATIVE_INFINITY))
				|| ((childValue == Double.POSITIVE_INFINITY) && (beta == Double.POSITIVE_INFINITY));
	}

	/**
	 * Updates list of moves along principal variation for given parent and child (that led to an improvement). Resulting
	 * list of moves along principal variation consists of: parent move name (if not empty) and list of moves along
	 * principal variation for child.
	 * 
	 * @param parent reference to parent
	 * @param child reference to child
	 */
	public final static void updateMovesAlongPrincipalVariation(GameState parent, GameState child) {
		List<String> movesAlongPrincipalVariation = parent.getMovesAlongPrincipalVariation();
		movesAlongPrincipalVariation.clear();
		movesAlongPrincipalVariation.add(child.getMoveName());
		movesAlongPrincipalVariation.addAll(child.getMovesAlongPrincipalVariation());
	}

	/**
	 * Forces current execute() recursion to stop.
	 */
	public final void forceStop() {
		stopForced = true;
	}
}