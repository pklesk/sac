package sac.graph;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Open set implementation via: java.util.PriorityQueue and java.util.HashMap.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class OpenSetAsPriorityQueue extends OpenSetImpl {

	/**
	 * The queue.
	 */
	private PriorityQueue<GraphState> queue;

	/**
	 * Creates new instance of OpenSetAsPriorityQueue.
	 * 
	 * @param comparator reference to comparator to be used by this open set
	 */
	public OpenSetAsPriorityQueue(Comparator<GraphState> comparator) {
		super(comparator);
		this.queue = new PriorityQueue<GraphState>(1024 * 1024, comparator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#add(sac.graph.GraphState)
	 */
	@Override
	public void add(GraphState graphState) {
		queue.add(graphState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#poll()
	 */
	@Override
	public GraphState poll() {
		return queue.poll();
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
		return queue.contains(graphState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#get(sac.graph.GraphState)
	 */
	@Override
	public GraphState get(GraphState graphState) {
		Iterator<GraphState> iterator = queue.iterator();
		while (iterator.hasNext()) {
			GraphState stateInQueue = iterator.next();
			if (stateInQueue.equals(graphState))
				return stateInQueue;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#replace(sac.graph.GraphState, sac.graph.GraphState)
	 */
	@Override
	public void replace(GraphState graphState, GraphState replacer) {
		queue.remove(graphState);
		queue.add(replacer);
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
	}
}