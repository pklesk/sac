package sac.game;

import java.util.AbstractMap;

/**
 * Abstract initial implementaiton of transposition table. Meant to be extended by actual implementations of
 * transposition table.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public abstract class TranspositionTableImpl implements TranspositionTable {
	
	/**
	 * The map.
	 */
	protected AbstractMap<TranspositionTableKey, TranspositionTableEntry> map;

	
	/**
	 * Counter of uses.
	 */
	protected int usesCount = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.TranspositionTable#getUsesCount()
	 */
	@Override
	public int getUsesCount() {
		return usesCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.TranspositionTable#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return (size() == 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.TranspositionTable#get(sac.game.GameState, java.lang.Double, double, double)
	 */
	@Override
	public Double get(GameState gameState, double alpha, double beta) {
		Double valueOrBoundOrNull = doGet(gameState, alpha, beta);
		if (valueOrBoundOrNull != null) gameState.setReadFromTranspositionTable(true);
		return valueOrBoundOrNull;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.TranspositionTable#get(sac.game.GameState)
	 */
	@Override
	public TranspositionTableEntry get(GameState gameState) {
		return doGet(gameState);		
	}
	
	/**
	 * Actual inner method in the wrapper get(GameState gameState, double alpha, double beta).
	 * 
	 * @param gameState reference to game state
	 * @param alpha alpha value
	 * @param beta beta value
	 * @return exact game value or suitable bound 
	 */
	protected Double doGet(GameState gameState, double alpha, double beta) {
		TranspositionTableEntry entry = map.get(new TranspositionTableKey(gameState));
		if (entry == null)
			return null;
		if (entry.getExactGameValue() != null) {
			usesCount++;
			return entry.getExactGameValue();
		} else {
			if ((entry.getUpperBoundOnGameValue() != null) && (entry.getUpperBoundOnGameValue() <= alpha)) {
				usesCount++;
				return entry.getUpperBoundOnGameValue();
			} else if ((entry.getLowerBoundOnGameValue() != null) && (beta <= entry.getLowerBoundOnGameValue())) {
				usesCount++;
				return entry.getLowerBoundOnGameValue();
			}
		}

		return null;
	}
	
	/**
	 * Actual inner method in the wrapper get(GameState gameState).
	 * 
	 * @param gameState reference to game state
	 * @return transposition table entry
	 */
	protected TranspositionTableEntry doGet(GameState gameState) {
		return map.get(new TranspositionTableKey(gameState));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.TranspositionTable#putOrUpdate(GameState, Double, double, double)
	 */
	@Override
	public void putOrUpdate(GameState gameState, Double value, double alpha, double beta) {
		TranspositionTableKey key = new TranspositionTableKey(gameState);
		TranspositionTableEntry entry = map.get(key);
		if (entry == null) {
			// put
			if (GameSearchAlgorithm.isExactGameValue(value, alpha, beta))
				entry = new TranspositionTableEntry(null, new Double(value), null);
			else {
				if (value <= alpha) // alpha-beta window fails low - value is an upper bound
					entry = new TranspositionTableEntry(null, null, new Double(value));
				else
					// alpha-beta window fails high - value is a lower bound
					entry = new TranspositionTableEntry(new Double(value), null, null);
			}
			map.put(new TranspositionTableKey(gameState), entry);
		} else {
			// update
			if (GameSearchAlgorithm.isExactGameValue(value, alpha, beta)) {
				entry.setExactGameValue(value);
				entry.setLowerBoundOnGameValue(null);
				entry.setUpperBoundOnGameValue(null);
			} else {
				if ((value <= alpha) && ((entry.getUpperBoundOnGameValue() == null) || (value < entry.getUpperBoundOnGameValue()))) {
					entry.setUpperBoundOnGameValue(value); // tighter upper bound
					if ((entry.getLowerBoundOnGameValue() != null) && (entry.getUpperBoundOnGameValue() != null)
							&& (entry.getLowerBoundOnGameValue().doubleValue() == entry.getUpperBoundOnGameValue().doubleValue())) {
						entry.setExactGameValue(new Double(entry.getLowerBoundOnGameValue().doubleValue()));
						entry.setLowerBoundOnGameValue(null);
						entry.setUpperBoundOnGameValue(null);
					}
				} else if ((beta <= value) && ((entry.getLowerBoundOnGameValue() == null) || (entry.getLowerBoundOnGameValue() < value))) {
					entry.setLowerBoundOnGameValue(value); // tighter lower bound
					if ((entry.getLowerBoundOnGameValue() != null) && (entry.getUpperBoundOnGameValue() != null)
							&& (entry.getLowerBoundOnGameValue().doubleValue() == entry.getUpperBoundOnGameValue().doubleValue())) {
						entry.setExactGameValue(new Double(entry.getLowerBoundOnGameValue().doubleValue()));
						entry.setLowerBoundOnGameValue(null);
						entry.setUpperBoundOnGameValue(null);
					}
				}
			}
		}		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.TranspositionTable#remove(sac.game.GameState)
	 */
	@Override
	public void remove(GameState gameState) {
		map.remove(gameState.getIdentifier());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.TranspositionTable#size()
	 */
	@Override
	public int size() {
		return map.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.TranspositionTable#clear()
	 */
	@Override
	public void clear() {
		map.clear();
		usesCount = 0;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return map.toString();
	}	
}