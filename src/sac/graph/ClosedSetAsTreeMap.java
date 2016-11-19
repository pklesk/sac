package sac.graph;

import java.util.TreeMap;

import sac.Identifier;

/**
 * Closed set implementation via: java.util.TreeSet.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */

public class ClosedSetAsTreeMap implements ClosedSet {

	/**
	 * The tree map.
	 */
	private TreeMap<Identifier, GraphState> treeMap;

	/**
	 * Creates new instance of ClosedSetAsTreeMap.
	 */
	public ClosedSetAsTreeMap() {
		this.treeMap = new TreeMap<Identifier, GraphState>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.ClosedSet#contains(sac.graph.GraphState)
	 */
	@Override
	public boolean contains(GraphState graphState) {
		return treeMap.containsKey(graphState.getIdentifier());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.ClosedSet#get(sac.graph.GraphState)
	 */
	@Override
	public GraphState get(GraphState graphState) {
		return treeMap.get(graphState.getIdentifier());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.ClosedSet#put(sac.graph.GraphState)
	 */
	@Override
	public void put(GraphState graphState) {
		treeMap.put(graphState.getIdentifier(), graphState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.ClosedSet#remove(sac.graph.GraphState)
	 */
	@Override
	public void remove(GraphState graphState) {
		treeMap.remove(graphState.getIdentifier());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.ClosedSet#size()
	 */
	@Override
	public int size() {
		return treeMap.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return treeMap.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.ClosedSet#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return treeMap.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.ClosedSet#clear()
	 */
	@Override
	public void clear() {
		treeMap.clear();
	}
}