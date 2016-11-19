package sac.graph;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.List;

/**
 * IDA* algorithm.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class IterativeDeepeningAStar extends GraphSearchAlgorithm {

	/**
	 * Creates new instance of IDA* algorithm.
	 * 
	 * @param initial reference to initial state
	 * @param configurator reference to configurator object
	 */
	public IterativeDeepeningAStar(GraphState initial, GraphSearchConfigurator configurator) {
		super(initial, configurator);
		setupOpenAndClosedSets(new IterativeDeepeningAStarComparator());		
	}

	/**
	 * Creates new instance of IDA* algorithm.
	 * 
	 * @param initial reference to initial state
	 */
	public IterativeDeepeningAStar(GraphState initial) {
		this(initial, null);
	}

	/**
	 * Creates new instance of IDA* algorithm.
	 */
	public IterativeDeepeningAStar() {
		this(null, null);
	}

	/**
	 * Comparator for A* algorithm.
	 */
	private class IterativeDeepeningAStarComparator implements Comparator<GraphState> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(GraphState gs1, GraphState gs2) {
			double difference = gs1.getDepth() - gs2.getDepth();
			if (difference == 0.0) {
				double fDifference = gs1.getF() - gs2.getF();
				if (fDifference == 0.0)
					return gs1.getIdentifier().compareTo(gs2.getIdentifier());
				else
					return (fDifference > 0.0) ? -1 : 1;
			} else {
				return (difference > 0.0) ? -1 : 1;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.GraphSearchAlgorithm#setupOpenAndClosedSets(java.util.Comparator)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void setupOpenAndClosedSets(Comparator<GraphState> openSetComparator) {
		// open set
		try {
			Constructor<OpenSet> constructor = (Constructor<OpenSet>) Class.forName(configurator.getOpenSetClassName()).getConstructor(Comparator.class);
			this.openSet = (OpenSet) constructor.newInstance(openSetComparator);
		} catch (Exception e) {
			this.openSet = new OpenSetAsPriorityQueueFastContainsFastReplace(openSetComparator);
		}

		// closed set not used by IDA* algorithm (!)
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.GraphSearchAlgorithm#doExecute()
	 */
	@Override
	protected void doExecute() {
		startTime = System.currentTimeMillis();
		openSet.add(initial);
		step = 0;

		double costLimit = initial.getH();
		double nextCostLimit = Double.POSITIVE_INFINITY;
		while (!openSet.isEmpty()) {
			step++;

			// time limit check
			if (configurator.getTimeLimit() < Long.MAX_VALUE) {
				long currentTime = System.currentTimeMillis();
				if (currentTime - startTime > configurator.getTimeLimit())
					break;
			}

			// poll
			current = openSet.poll();

			double minimumCost = current.getF();
			if (minimumCost > costLimit) {
				if (minimumCost < nextCostLimit)
					nextCostLimit = minimumCost;
				if (openSet.isEmpty()) {
					// re-run of main loop
					costLimit = nextCostLimit;
					nextCostLimit = Double.POSITIVE_INFINITY;
					openSet.add(initial);
					// step = 0; // so that after last deepening (and solution found) the number of
					// closed states without duplicates can be told
				}
				continue;
			}

			// keeping best so far
			if ((bestSoFar == null) || (current.getH() < bestSoFar.getH()))
				bestSoFar = current;

			// solution check
			boolean isSolution = current.isSolution();

			// registering solution
			if (isSolution) {
				if (solutions.isEmpty())
					bestSoFar = current;
				solutions.add(current);
				if (configurator.getWantedNumberOfSolutions() == solutions.size())
					break;
			}

			// generating children
			List<GraphState> children = current.generateChildren();
			current.getChildren().clear();

			// iterating over children
			for (GraphState child : children) {

				// set child -> parent link and depth
				child.setParent(current);
				child.setDepth(current.getDepth() + 1);

				// update scores g, h, f
				child.refreshCosts();

				boolean openSetContains = openSet.contains(child);
				if (!openSetContains) {
					// add child reference to parent
					if (configurator.isParentsMemorizingChildren())
						current.getChildren().add(child);

					// add child to open set
					openSet.add(child);

				} else {
					// getting reference to child existing in open set
					GraphState existingChild = openSet.get(child);

					// replacing, if new child better than existing (not via comparator, since
					// comparator works according to depth)
					if (child.getF() < existingChild.getF()) {
						openSet.replace(existingChild, child);

						// add child reference to parent (better child)
						if (configurator.isParentsMemorizingChildren())
							current.getChildren().add(child);
					} else {
						// add child reference to parent (existing child)
						if (configurator.isParentsMemorizingChildren())
							current.getChildren().add(child);
					}
				}
			}
		}
		endTime = System.currentTimeMillis();
	}	
}