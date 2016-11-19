package sac.game;

import java.util.List;

/**
 * Scout algorithm.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class Scout extends GameSearchAlgorithm {

	/**
	 * Creates new instance of Scout algorithm.
	 * 
	 * @param initial reference to initial state
	 * @param configurator reference to configurator object
	 */
	public Scout(GameState initial, GameSearchConfigurator configurator) {
		super(initial, configurator);
	}

	/**
	 * Creates new instance of Scout algorithm.
	 * 
	 * @param initial reference to initial state
	 */
	public Scout(GameState initial) {
		super(initial, null);
	}

	/**
	 * Creates new instance of Scout algorithm.
	 */
	public Scout() {
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
		double b = beta;
		for (int i = 0; i < children.size(); i++) {
			GameState child = children.get(i);
			Double childValue = null;
			boolean researchNeeded = false;
			double bound = alpha;
			if (configurator.isTranspositionTableOn())
				childValue = transpositionTable.get(child, alpha, b);
			if (childValue == null) {
				if (child.isMaximizingTurnNow()) {
					// scout search with zero window
					childValue = evaluateMaxState(child, alpha, b, depth + 0.5, depthLimit);
					if (childValue == null)
						return null; // time limit reached
					// checking if window fails high, if so, research with broader window
					if ((i > 0) && (b <= childValue) && (childValue < beta) && ((configurator.isQuiescenceOn()) || (depthLimit - depth > 0.5))) {
						researchNeeded = true;
						bound = childValue;
						childValue = evaluateMaxState(child, bound, beta, depth + 0.5, depthLimit);
						if (childValue == null)
							return null; // time limit reached
					}
				} else {
					// scout search with zero window
					childValue = evaluateMinState(child, alpha, b, depth + 0.5, depthLimit);
					if (childValue == null)
						return null; // time limit reached
					// checking if window fails high, if so, research with broader window
					if ((i > 0) && (b <= childValue) && (childValue < beta) && ((configurator.isQuiescenceOn()) || (depthLimit - depth > 0.5))) {
						researchNeeded = true;
						bound = childValue;
						childValue = evaluateMinState(child, bound, beta, depth + 0.5, depthLimit);
						if (childValue == null)
							return null; // time limit reached
					}
				}
			}
			if (configurator.isTranspositionTableOn()) {
				if (!researchNeeded)
					transpositionTable.putOrUpdate(child, childValue, alpha, b);
				else
					transpositionTable.putOrUpdate(child, childValue, bound, beta);
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
			if (Math.abs(alpha) < GameState.H_SMALLEST_INFINITY)
				b = alpha + 1.0;
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
		double a = alpha;
		for (int i = 0; i < children.size(); i++) {
			GameState child = children.get(i);
			Double childValue = null;
			boolean researchNeeded = false;
			double bound = beta;
			if (configurator.isTranspositionTableOn())
				childValue = transpositionTable.get(child, a, beta);
			if (childValue == null) {
				if (child.isMaximizingTurnNow()) {
					// scout search with zero window
					childValue = evaluateMaxState(child, a, beta, depth + 0.5, depthLimit);
					if (childValue == null)
						return null; // time limit reached
					// checking if window fails low, if so, research with broader window
					if ((i > 0) && (childValue <= a) && (alpha < childValue) && ((configurator.isQuiescenceOn()) || (depthLimit - depth > 0.5))) {
						researchNeeded = true;
						bound = childValue;
						childValue = evaluateMaxState(child, alpha, bound, depth + 0.5, depthLimit);
						if (childValue == null)
							return null; // time limit reached
					}
				} else {
					// scout search with zero window
					childValue = evaluateMinState(child, a, beta, depth + 0.5, depthLimit);
					if (childValue == null)
						return null; // time limit reached
					// checking if window fails low, if so, research with broader window
					if ((i > 0) && (childValue <= a) && (alpha < childValue) && ((configurator.isQuiescenceOn()) || (depthLimit - depth > 0.5))) {
						researchNeeded = true;
						bound = childValue;
						childValue = evaluateMinState(child, alpha, bound, depth + 0.5, depthLimit);
						if (childValue == null)
							return null; // time limit reached
					}
				}
			}
			if (configurator.isTranspositionTableOn()) {
				if (!researchNeeded)
					transpositionTable.putOrUpdate(child, childValue, a, beta);
				else
					transpositionTable.putOrUpdate(child, childValue, alpha, bound);
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
			if (Math.abs(beta) < GameState.H_SMALLEST_INFINITY)
				a = beta - 1.0;
		}

		return beta;
	}
}