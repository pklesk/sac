package sac.graph;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import sac.Identifier;
import sac.SearchAlgorithm;

/**
 * Abstract graph search algorithm. Meant to be extended by actual algorithms e.g.: Breadth First Search, Depth First
 * Search, Dijkstra's, Best First Search, A*, IDA*.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public abstract class GraphSearchAlgorithm extends SearchAlgorithm {

	/**
	 * Reference to initial state.
	 */
	protected GraphState initial = null;

	/**
	 * Open set.
	 */
	protected OpenSet openSet = null;

	/**
	 * Closed set.
	 */
	protected ClosedSet closedSet = null;

	/**
	 * Graph search configurator object.
	 */
	protected GraphSearchConfigurator configurator = null;

	/**
	 * List of solution states.
	 */
	protected List<GraphState> solutions = null;

	/**
	 * Current step number (of main searching loop).
	 */
	protected int step = 0;

	/**
	 * Reference to currently examined state in the main searching loop.
	 */
	protected GraphState current = null;

	/**
	 * Reference to state with best (so far) value of h function.
	 */
	protected GraphState bestSoFar = null;

	/**
	 * Creates new instance of graph search algorithm.
	 * 
	 * @param initial reference to initial state
	 * @param configurator reference to configurator object
	 */
	public GraphSearchAlgorithm(GraphState initial, GraphSearchConfigurator configurator) {
		this.configurator = (configurator != null) ? configurator : new GraphSearchConfigurator();
		Identifier.setType(this.configurator.getIdentifierType());
		this.initial = initial;
		this.solutions = new ArrayList<GraphState>();
	}

	/**
	 * Creates (via reflection) open and closed sets according to configurator object (default or specified on
	 * construction).
	 * 
	 * @param openSetComparator comparator object for open set
	 */
	@SuppressWarnings("unchecked")
	protected void setupOpenAndClosedSets(Comparator<GraphState> openSetComparator) {
		// open set
		try {
			Constructor<OpenSet> constructor = (Constructor<OpenSet>) Class.forName(configurator.getOpenSetClassName()).getConstructor(Comparator.class);
			this.openSet = (OpenSet) constructor.newInstance(openSetComparator);
		} catch (Exception e) {
			this.openSet = new OpenSetAsPriorityQueueFastContainsFastReplace(openSetComparator);
			e.printStackTrace();
		}

		// closed set
		if (configurator.isClosedSetOn())
			try {
				Constructor<ClosedSet> constructor = (Constructor<ClosedSet>) Class.forName(configurator.getClosedSetClassName()).getConstructor();
				this.closedSet = (ClosedSet) constructor.newInstance();
			} catch (Exception e) {
				this.closedSet = new ClosedSetAsHashMap();
				e.printStackTrace();
			}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.SearchAlgorithm#execute()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void execute() {
		reset();

		GraphSearchMonitor monitor = null;
		Thread monitorThread = null;

		if (configurator.isMonitorOn()) {
			// starting monitor thread
			try {
				Constructor<GraphSearchMonitor> constructor = (Constructor<GraphSearchMonitor>) Class.forName(configurator.getMonitorClassName())
						.getConstructor(GraphSearchAlgorithm.class, Long.TYPE);
				monitor = (GraphSearchMonitor) constructor.newInstance(this, configurator.getMonitorRefreshTime());
			} catch (Exception e) {
				monitor = new ConsoleGraphSearchMonitor(this, configurator.getMonitorRefreshTime());
				e.printStackTrace();
			}

			monitorThread = new Thread(monitor);
			monitorThread.start();
		}

		doExecute(); // actual search start

		if (configurator.isMonitorOn()) {
			monitor.stop();
			// waiting for monitor thread to stop
			while (true) {
				if (!monitorThread.isAlive())
					break;
				try {
					Thread.sleep(100);
				} catch (InterruptedException ie) {
				}
			}
		}
	}

	/**
	 * Resets this algorithm. I.e. resets initial state (cuts off its children, if they exist), clears list of
	 * solutions, clears open and closed sets.
	 */
	protected void reset() {
		Identifier.setType(this.configurator.getIdentifierType());

		if (initial != null) {
			initial.refresh();
			initial.getChildren().clear();
			initial.refreshCosts();
		}

		solutions.clear();
		bestSoFar = null;
		current = null;

		setupOpenAndClosedSets(openSet.getComparator()); // inspects configurator (possibly new
																// since last call), and sets up
																// open and closed sets, but
																// reuses the so-far comparator for
																// open set
	}

	/**
	 * Actual execution of search algorithm invoked from inside of execute() method.
	 */
	protected void doExecute() {
		startTime = System.currentTimeMillis();
		if (initial == null)
			return;
		openSet.add(initial);
		step = 0;
		while (!openSet.isEmpty()) {
			step++;

			// time limit check
			if (configurator.getTimeLimit() < Long.MAX_VALUE) {
				long currentTime = System.currentTimeMillis();
				if (currentTime - startTime > configurator.getTimeLimit()) {
					endTime = System.currentTimeMillis();
					break;
				}
			}

			// poll current best from queue
			current = openSet.poll();

			// putting current to closed set
			// (by doing it now, we prevent from a situation of current's child equal to current and
			// added to closed)
			if (configurator.isClosedSetOn())
				closedSet.put(current);

			// keeping best so far
			if ((initial.getH() > 0) && ((bestSoFar == null) || (current.getH() < bestSoFar.getH())))
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

			// iterating over children
			for (GraphState child : children) {

				// check if child was closed
				boolean closedSetContains = (configurator.isClosedSetOn()) ? closedSet.contains(child) : false;

				if (!closedSetContains) { // child not in closed set

					// set child -> parent link and depth
					child.setParent(current);
					child.setDepth(current.getDepth() + 1);

					// update scores g, h, f
					child.refreshCosts();

					// check if child is in open set
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

						// replacing, if new child better than existing
						if (openSet.getComparator().compare(child, existingChild) < 0) {
							openSet.replace(existingChild, child);

							// add child reference to parent (better child)
							if (configurator.isParentsMemorizingChildren())
								current.getChildren().add(child);

							// removing from some other parent reference to worse existing child
							existingChild.getParent().getChildren().remove(existingChild);
						}
					}
				}
			}
		}

		endTime = System.currentTimeMillis();
	}

	/**
	 * Returns list of solutions.
	 * 
	 * @return list of solutions
	 */
	public final List<GraphState> getSolutions() {
		return solutions;
	}

	/**
	 * Returns reference to closed set.
	 * 
	 * @return reference to closed set
	 */
	public final ClosedSet getClosedSet() {
		return closedSet;
	}

	/**
	 * Returns reference to open set.
	 * 
	 * @return reference to open set
	 */
	public final OpenSet getOpenSet() {
		return openSet;
	}

	/**
	 * Returns reference to current state (state being examined in current iteration of main loop).
	 * 
	 * @return reference to current state
	 */
	public final GraphState getCurrent() {
		return current;
	}

	/**
	 * Returns reference to state with best h score so far.
	 * 
	 * @return reference to state with best h score so far
	 */
	public final GraphState getBestSoFar() {
		return bestSoFar;
	}

	/**
	 * Returns reference to initial graph state.
	 * 
	 * @return reference to initial graph state
	 */
	public final GraphState getInitial() {
		return initial;
	}

	/**
	 * Sets reference to initial graph state.
	 * 
	 * @param initial reference to initial graph state
	 */
	public final void setInitial(GraphState initial) {
		this.initial = initial;
	}

	/**
	 * Gets reference to configurator object.
	 * 
	 * @return reference to configurator object
	 */
	public final GraphSearchConfigurator getConfigurator() {
		return configurator;
	}

	/**
	 * Sets reference to configurator object.
	 * 
	 * @param configurator reference to configurator object
	 */
	public final void setConfigurator(GraphSearchConfigurator configurator) {
		this.configurator = configurator;
	}

	@Override
	public final int getClosedStatesCount() {
		return step;
	}
}