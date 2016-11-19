package sac.game;

import java.util.AbstractMap;
import java.util.List;

import sac.Identifier;

/**
 * Abstract initial implementation of transposition table. Meant to be extended by actual implementations of
 * transposition table.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public abstract class RefutationTableImpl implements RefutationTable {

	/**
	 * Default depth limit.
	 */
	protected final static double DEFAULT_DEPTH_LIMIT = 2.0;

	/**
	 * Counter of uses.
	 */
	protected double depthLimit;

	/**
	 * Counter of uses.
	 */
	protected int usesCount = 0;

	/**
	 * Map to memorize cut-off moves to be used in the next tree search iteration.
	 */
	protected AbstractMap<Identifier, Identifier> tableToSave = null;

	/**
	 * Map storing cut-off moves from the previous tree search iteration.
	 */
	protected AbstractMap<Identifier, Identifier> tableToRead = null;

	
	/**
	 * Creates new instance of refutation table and sets its depth limit.
	 * 
	 * @param depthLimit depth limit to be set
	 */
	public RefutationTableImpl(double depthLimit) {
		this.depthLimit = depthLimit;
	}

	/**
	 * Creates new instance of refutation table and sets a default depth limit to it.
	 */
	public RefutationTableImpl() {
		this(DEFAULT_DEPTH_LIMIT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.RefutationTable#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return (size() == 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.RefutationTable#getUsesCount()
	 */
	@Override
	public int getUsesCount() {
		return usesCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.RefutationTable#getDepthLimit()
	 */
	@Override
	public double getDepthLimit() {
		return depthLimit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.RefutationTable#setDepthLimit(double)
	 */
	@Override
	public void setDepthLimit(double depthLimit) {
		this.depthLimit = depthLimit;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.RefutationTable#put(sac.game.GameState, sac.game.GameState)
	 */
	@Override
	public void put(GameState parent, GameState child) {
		double depth = parent.getDepth() - 0.5; // in progressive search, we assume that next
												// iteration (possibly reading from refutation
												// table) will start from level +0.5, so moves at
												// level 0.0 are not memorized in refutation table
		if ((depth >= 0) && (depth <= depthLimit))
			tableToSave.put(parent.getIdentifier(), child.getIdentifier());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.RefutationTable#reorder(sac.game.GameState, java.util.List)
	 */
	@Override
	public void reorder(GameState parent, List<GameState> children) {
		if ((children == null) || (children.size() <= 1))
			return; // no reorder done
		double depth = parent.getDepth();
		if (depth > depthLimit)
			return; // no reorder done
		Identifier bestChildIdentifier = tableToRead.get(parent.getIdentifier());
		if (bestChildIdentifier == null)
			return; // no reorder done
		GameState bestChild = null;
		int i = 0;
		for (GameState child : children) {
			if (child.getIdentifier().equals(bestChildIdentifier)) {
				bestChild = child;
				break;
			}
			i++;
		}
		if (i == 0)
			return; // no reorder done
		if (bestChild != null) {
			children.remove(bestChild);
			children.add(0, bestChild); // putting best child in front
			usesCount++;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.RefutationTable#size()
	 */
	@Override
	public int size() {
		return tableToSave.size() + tableToRead.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.RefutationTable#clear()
	 */
	@Override
	public void clear() {
		tableToSave.clear();
		tableToRead.clear();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "tableToRead: " + tableToRead.toString() + ", tableToSave: " + tableToSave.toString();
	}
}