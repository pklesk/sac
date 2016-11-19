package sac.graph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import sac.Identifier;

/**
 * Open set implementation via: java.util.PriorityQueue and java.util.HashMap.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class OpenSetAsPriorityQueueFastContains extends OpenSetImpl {

	/**
	 * The queue.
	 */
	private PriorityQueue<GraphState> queue;

	/**
	 * Helper map for fast checking if this open set contains some state.
	 */
	private Map<Identifier, GraphState> map;

	/**
	 * Creates new instance of OpenSetAsPriorityQueueFastContains.
	 * 
	 * @param comparator reference to comparator to be used by this open set
	 */
	public OpenSetAsPriorityQueueFastContains(Comparator<GraphState> comparator) {
		super(comparator);
		this.queue = new PriorityQueue<GraphState>(1024 * 1024, comparator);
		this.map = new HashMap<Identifier, GraphState>(1024 * 1024, (float) 0.75);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#add(sac.graph.GraphState)
	 */
	@Override
	public void add(GraphState graphState) {
		queue.add(graphState);
		map.put(graphState.getIdentifier(), graphState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#poll()
	 */
	@Override
	public GraphState poll() {
		if (!queue.isEmpty()) {
			GraphState first = queue.poll();
			map.remove(first.getIdentifier());
			return first;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#peek()
	 */
	@Override
	public GraphState peek() {
		return queue.peek();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#contains()
	 */
	@Override
	public boolean contains(GraphState graphState) {
		return map.containsKey(graphState.getIdentifier());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#get(sac.graph.GraphState)
	 */
	@Override
	public GraphState get(GraphState graphState) {
		return map.get(graphState.getIdentifier());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#replace(sac.graph.GraphState, sac.graph.GraphState)
	 */
	@Override
	public void replace(GraphState graphState, GraphState replacer) {
		Identifier key = graphState.getIdentifier();
		if (map.containsKey(key)) {
			map.remove(key);
			queue.remove(graphState);
			add(replacer);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#size()
	 */
	@Override
	public int size() {
		return queue.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return queue.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#clear()
	 */
	@Override
	public void clear() {
		queue.clear();
		map.clear();
	}
}