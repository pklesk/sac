package sac.graph;

import java.util.HashMap;
import java.util.Map;

import sac.Identifier;

/**
 * Closed set implementation via: java.util.HashMap.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class ClosedSetAsHashMap implements ClosedSet {

	/**
	 * The map.
	 */
	private Map<Identifier, GraphState> map;

	/**
	 * Creates new instance of this ClosedSetAsHashMap.
	 */
	public ClosedSetAsHashMap() {
		this.map = new HashMap<Identifier, GraphState>(8 * 1024 * 1024, (float) 0.75);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.ClosedSet#contains(sac.graph.GraphState)
	 */
	@Override
	public boolean contains(GraphState graphState) {
		return map.containsKey(graphState.getIdentifier());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.ClosedSet#get(sac.graph.GraphState)
	 */
	@Override
	public GraphState get(GraphState graphState) {
		return map.get(graphState.getIdentifier());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.ClosedSet#put(sac.graph.GraphState)
	 */
	@Override
	public void put(GraphState graphState) {
		map.put(graphState.getIdentifier(), graphState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.ClosedSet#remove(sac.graph.GraphState)
	 */
	@Override
	public void remove(GraphState graphState) {
		map.remove(graphState.getIdentifier());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.ClosedSet#size()
	 */
	@Override
	public int size() {
		return map.size();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.ClosedSet#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.ClosedSet#clear()
	 */
	@Override
	public void clear() {
		map.clear();
	}
}