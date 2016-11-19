package sac.game;

import java.util.Arrays;

import sac.Identifier;

/**
 * An key in the transposition table.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class TranspositionTableKey implements Comparable<TranspositionTableKey> {
	/**
	 * Identifier of game state.
	 */
	private Identifier identifier = null;

	/**
	 * Depth of game state.
	 */
	private double depth;

	public TranspositionTableKey(GameState gameState) {
		identifier = gameState.getIdentifier();
		depth = gameState.getDepth();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object otherTranspositionTableKey) {
		TranspositionTableKey otherTranspositionTableKey2 = (TranspositionTableKey) otherTranspositionTableKey;
		return (identifier.equals(otherTranspositionTableKey2.identifier) && (depth == otherTranspositionTableKey2.depth));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		double[] pair = new double[2];
		pair[0] = identifier.hashCode();
		pair[1] = depth;
		return Arrays.hashCode(pair);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + identifier + "," + depth + ")";
	}

	@Override
	public int compareTo(TranspositionTableKey o) {
		int difference = identifier.compareTo(o.identifier);
		return (difference != 0) ? difference :  (int) (2 * (depth - o.depth));
	}	
}