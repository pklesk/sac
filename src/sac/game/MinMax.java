package sac.game;

import java.util.List;

/**
 * MIN-MAX algorithm.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class MinMax extends GameSearchAlgorithm {

	/**
	 * Creates new instance of MIN-MAX algorithm.
	 * 
	 * @param initial reference to initial state
	 * @param configurator reference to configurator object
	 */
	public MinMax(GameState initial, GameSearchConfigurator configurator) {
		super(initial, configurator);
	}

	/**
	 * Creates new instance of MIN-MAX algorithm.
	 * 
	 * @param initial reference to initial state
	 */
	public MinMax(GameState initial) {
		super(initial, null);
	}

	/**
	 * Creates new instance of MIN-MAX algorithm.
	 * 
	 */
	public MinMax() {
		super(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameSearchAlgorithm#evaluateMaxState(sac.game.GameState, double, double, double, double)
	 */
	@Override
	public Double doEvaluateMaxState(GameState gameState, double alpha, double beta, double depth, double depthLimit) {
		if (isGameStateTerminal(gameState, depth, depthLimit)) {
			if (configurator.isTranspositionTableOn())
				transpositionTable.putOrUpdate(gameState, gameState.getH(), alpha, beta);
			return gameState.getH();
		}
		List<GameState> children = generateChildrenWrapper(gameState);
		double value = Double.NEGATIVE_INFINITY;
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
			if (childValue > value) {
				value = childValue;
				updateMovesAlongPrincipalVariation(gameState, child);
			}
			if (depth == 0.0)
				movesScores.put(child.getMoveName(), childValue);
		}

		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameSearchAlgorithm#evaluateMinState(sac.game.GameState, double, double, double, double)
	 */
	@Override
	public Double doEvaluateMinState(GameState gameState, double alpha, double beta, double depth, double depthLimit) {
		if (isGameStateTerminal(gameState, depth, depthLimit)) {
			if (configurator.isTranspositionTableOn())
				transpositionTable.putOrUpdate(gameState, gameState.getH(), alpha, beta);
			return gameState.getH();
		}
		List<GameState> children = generateChildrenWrapper(gameState);
		double value = Double.POSITIVE_INFINITY;
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
			if (childValue < value) {
				value = childValue;
				updateMovesAlongPrincipalVariation(gameState, child);
			}
			if (depth == 0.0)
				movesScores.put(child.getMoveName(), childValue);
		}

		return value;
	}
}