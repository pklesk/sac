package sac.game;

import java.util.List;

/**
 * Alpha-beta cutoffs algorithm.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class AlphaBetaPruning extends GameSearchAlgorithm {

	/**
	 * Creates new instance of alpha-beta cutoffs algorithm.
	 * 
	 * @param initial reference to initial state
	 * @param configurator reference to configurator object
	 */
	public AlphaBetaPruning(GameState initial, GameSearchConfigurator configurator) {
		super(initial, configurator);
	}

	/**
	 * Creates new instance of alpha-beta cutoffs algorithm.
	 * 
	 * @param initial reference to initial state
	 */
	public AlphaBetaPruning(GameState initial) {
		super(initial, null);
	}

	/**
	 * Creates new instance of alpha-beta cutoffs algorithm.
	 */
	public AlphaBetaPruning() {
		super(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameSearchAlgorithm#doEvaluateMaxState(sac.game.GameState, double, double, double, double)
	 */
	@Override
	public Double doEvaluateMaxState(GameState gameState, double alpha, double beta, double depth, double depthLimit) {
		if (isGameStateTerminal(gameState, depth, depthLimit)) {
			if (configurator.isTranspositionTableOn())
				transpositionTable.putOrUpdate(gameState, gameState.getH(), alpha, beta);
			return gameState.getH();
		}
		List<GameState> children = generateChildrenWrapper(gameState);
		if (configurator.isRefutationTableOn())
			refutationTable.reorder(gameState, children);
		for (GameState child : children) {
			Double childValue = null;
			if (configurator.isTranspositionTableOn())
				childValue = transpositionTable.get(child, alpha, beta);
			if (childValue == null) {
				childValue = (child.isMaximizingTurnNow()) ? evaluateMaxState(child, alpha, beta, depth + 0.5, depthLimit) : evaluateMinState(child, alpha,
						beta, depth + 0.5, depthLimit);
				if (childValue == null)
					return null;
				if (configurator.isTranspositionTableOn())
					transpositionTable.putOrUpdate(child, childValue, alpha, beta);
			}
			if ((depth == 0.0) && (isExactGameValue(childValue, alpha, beta))) 
				movesScores.put(child.getMoveName(), childValue);
			if (childValue > alpha) {
				alpha = childValue;
				updateMovesAlongPrincipalVariation(gameState, child);
				if (configurator.isRefutationTableOn())
					refutationTable.put(gameState, child);
			}
			if (alpha >= beta) 
				return alpha;			
		}

		return alpha;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameSearchAlgorithm#doEvaluateMinState(sac.game.GameState, double, double, double, double)
	 */
	@Override
	public Double doEvaluateMinState(GameState gameState, double alpha, double beta, double depth, double depthLimit) {
		if (isGameStateTerminal(gameState, depth, depthLimit)) {
			if (configurator.isTranspositionTableOn())
				transpositionTable.putOrUpdate(gameState, gameState.getH(), alpha, beta);
			return gameState.getH();
		}
		List<GameState> children = generateChildrenWrapper(gameState);
		if (configurator.isRefutationTableOn())
			refutationTable.reorder(gameState, children);
		for (GameState child : children) {
			Double childValue = null;
			if (configurator.isTranspositionTableOn())
				childValue = transpositionTable.get(child, alpha, beta);
			if (childValue == null) {
				childValue = (child.isMaximizingTurnNow()) ? evaluateMaxState(child, alpha, beta, depth + 0.5, depthLimit) : evaluateMinState(child, alpha,
						beta, depth + 0.5, depthLimit);
				if (childValue == null)
					return null;
				if (configurator.isTranspositionTableOn())
					transpositionTable.putOrUpdate(child, childValue, alpha, beta);
			}
			if ((depth == 0.0) && (isExactGameValue(childValue, alpha, beta)))
				movesScores.put(child.getMoveName(), childValue);
			if (childValue < beta) {
				beta = childValue;
				updateMovesAlongPrincipalVariation(gameState, child);
				if (configurator.isRefutationTableOn())
					refutationTable.put(gameState, child);
			}
			if (alpha >= beta) 
				return beta;
		}

		return beta;
	}
}