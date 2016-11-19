package sac.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import sac.Identifier;

/**
 * Open set implementation via: own-implementation of binary heap (faster replaces) and java.util.HashMap.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class OpenSetAsPriorityQueueFastContainsFastReplace extends OpenSetImpl {

	/**
	 * The binary heap.
	 */
	private ArrayList<GraphState> binaryHeap;

	/**
	 * Helper map.
	 */
	private Map<Identifier, MapEntry> map;

	/**
	 * A map entry consisting of a pair: graph state and its index in the heap.
	 */
	private class MapEntry {
		private GraphState graphState;
		private int binaryHeapIndex;

		/**
		 * Gets reference to graph state.
		 * 
		 * @return reference to graph state
		 */
		public GraphState getGraphState() {
			return graphState;
		}

		/**
		 * Sets new reference to graph state
		 * 
		 * @param graphState new reference to be set
		 */
		public void setGraphState(GraphState graphState) {
			this.graphState = graphState;
		}

		/**
		 * Gets index in the binary heap.
		 * 
		 * @return index in the binary heap
		 */
		public int getBinaryHeapIndex() {
			return binaryHeapIndex;
		}

		/**
		 * Sets new binary heap index for this map entry.
		 * 
		 * @param binaryHeapIndex new binary heap index
		 */
		public void setBinaryHeapIndex(int binaryHeapIndex) {
			this.binaryHeapIndex = binaryHeapIndex;
		}

		/**
		 * Creates new map entry.
		 * 
		 * @param graphState reference to graph state
		 * @param binaryHeapIndex index in the binary heap
		 */
		public MapEntry(GraphState graphState, int binaryHeapIndex) {
			this.graphState = graphState;
			this.binaryHeapIndex = binaryHeapIndex;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "(" + graphState.toString() + ", " + binaryHeapIndex + ")";
		}
	}

	/**
	 * Creates new instance of OpenSetAsPriorityQueueFastContainsFastReplace
	 * 
	 * @param comparator reference to comparator to be used by this open set
	 */
	public OpenSetAsPriorityQueueFastContainsFastReplace(Comparator<GraphState> comparator) {
		super(comparator);
		binaryHeap = new ArrayList<GraphState>(1024 * 1024);
		map = new HashMap<Identifier, MapEntry>(1024 * 1024, (float) 0.75);
	}

	/**
	 * Reorganizes recursively the heap by moving its element with given index upwards until the heap condition is
	 * satisified.
	 * 
	 * @param childIndex index of element to be reheaped up
	 */
	protected void reheapUp(int childIndex) {
		if (childIndex == 0)
			return; // stop of recursion
		int parentIndex = (childIndex - 1) / 2;
		GraphState parent = binaryHeap.get(parentIndex);
		GraphState child = binaryHeap.get(childIndex);
		if (comparator.compare(parent, child) > 0) {
			binaryHeap.set(parentIndex, child);
			binaryHeap.set(childIndex, parent);
			map.get(child.getIdentifier()).setBinaryHeapIndex(parentIndex);
			map.get(parent.getIdentifier()).setBinaryHeapIndex(childIndex);
			reheapUp(parentIndex);
		}
	}

	/**
	 * Reorganizes recursively the heap by moving its element with given index downwards until the heap condition is
	 * satisified.
	 * 
	 * @param parentIndex index of element to be reheaped down
	 */
	protected void reheapDown(int parentIndex) {
		int leftChildIndex = 2 * parentIndex + 1;
		if (leftChildIndex >= binaryHeap.size())
			return; // stop of recursion
		GraphState leftChild = binaryHeap.get(leftChildIndex);
		int rightChildIndex = leftChildIndex + 1;
		GraphState rightChild = (rightChildIndex >= binaryHeap.size()) ? null : binaryHeap.get(rightChildIndex);
		GraphState parent = binaryHeap.get(parentIndex);

		if ((comparator.compare(parent, leftChild) <= 0))
			if ((rightChild == null) || (comparator.compare(parent, rightChild) <= 0))
				return;

		int childToReplaceIndex = leftChildIndex;
		if ((rightChild != null) && (comparator.compare(rightChild, leftChild) < 0))
			childToReplaceIndex = rightChildIndex;

		GraphState childToReplace = binaryHeap.get(childToReplaceIndex);
		binaryHeap.set(childToReplaceIndex, parent);
		binaryHeap.set(parentIndex, childToReplace);
		map.get(parent.getIdentifier()).setBinaryHeapIndex(childToReplaceIndex);
		map.get(childToReplace.getIdentifier()).setBinaryHeapIndex(parentIndex);

		reheapDown(childToReplaceIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#add(sac.graph.GraphState)
	 */
	@Override
	public void add(GraphState graphState) {
		binaryHeap.add(graphState);
		MapEntry mapEntry = new MapEntry(graphState, binaryHeap.size() - 1);
		map.put(graphState.getIdentifier(), mapEntry);
		reheapUp(binaryHeap.size() - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#poll()
	 */
	@Override
	public GraphState poll() {
		if (binaryHeap.isEmpty())
			return null;
		GraphState first = binaryHeap.get(0);
		if (binaryHeap.size() == 1) {
			binaryHeap.remove(0);
			map.remove(first.getIdentifier());
		} else {
			GraphState last = binaryHeap.get(binaryHeap.size() - 1);
			binaryHeap.set(0, last);
			MapEntry lastMapEntry = map.get(last.getIdentifier());
			lastMapEntry.setBinaryHeapIndex(0);
			map.remove(first.getIdentifier());
			binaryHeap.remove(binaryHeap.size() - 1);
			reheapDown(0);
		}
		return first;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#peek()
	 */
	@Override
	public GraphState peek() {
		return (binaryHeap.isEmpty()) ? null : binaryHeap.get(0);
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
	 * @see sac.graph.OpenSet#update(sac.graph.GraphState, sac.graph.GraphState)
	 */
	@Override
	public void replace(GraphState graphState, GraphState replacer) {
		Identifier identifier = graphState.getIdentifier();
		Integer index = map.get(identifier).getBinaryHeapIndex();
		if (index == null)
			return;
		binaryHeap.set(index.intValue(), replacer);
		map.get(identifier).setGraphState(replacer);
		reheapUp(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#get(sac.graph.GraphState)
	 */
	@Override
	public GraphState get(GraphState graphState) {
		return map.get(graphState.getIdentifier()).getGraphState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#size()
	 */
	@Override
	public int size() {
		return binaryHeap.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return binaryHeap.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return binaryHeap.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.OpenSet#clear()
	 */
	@Override
	public void clear() {
		binaryHeap.clear();
		map.clear();
	}
}