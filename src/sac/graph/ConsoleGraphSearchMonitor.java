package sac.graph;

import sac.util.ConsoleLogger;

/**
 * Implementation of monitor with System.out console as output.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class ConsoleGraphSearchMonitor extends GraphSearchMonitor {

	/**
	 * Creates new console monitor for a graph search.
	 * 
	 * @param algorithm reference to graph search algorithm
	 * @param refreshTime refresh time
	 */
	public ConsoleGraphSearchMonitor(GraphSearchAlgorithm algorithm, long refreshTime) {
		super(algorithm, refreshTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.GraphSearchMonitor#printCurrentSummary()
	 */
	@Override
	public synchronized void printCurrentSummary() {
		ConsoleLogger.info("*** Starting " + this.getClass().getName() + ".printCurrentSummary()...");

		double time = 0.001 * getTime();

		ConsoleLogger.info("Time: " + time + " s.");
		ConsoleLogger.info("Solutions so far: " + algorithm.getSolutions().size() + ".");
		ConsoleLogger.info("Closed states: " + algorithm.getClosedStatesCount() + ".");
		ConsoleLogger.info("Open states: " + algorithm.getOpenSet().size() + ".");

		GraphState bestSoFar = algorithm.getBestSoFar();
		if (bestSoFar != null) {
			ConsoleLogger.info("Best state's h: " + bestSoFar.getH() + ".");
			ConsoleLogger.info("Best state's f: " + bestSoFar.getF() + ".");
			ConsoleLogger.info("Best state's depth: " + bestSoFar.getDepth() + ".");
		}

		GraphState current = algorithm.getCurrent();
		if (current != null) {
			if (bestSoFar != null)
				ConsoleLogger.info("Current state's h: " + current.getH() + ".");
			ConsoleLogger.info("Current state's f: " + current.getF() + ".");
			ConsoleLogger.info("Current state's depth: " + current.getDepth() + ".");
		}

		ConsoleLogger.info("Free memory: " + Runtime.getRuntime().freeMemory());
		ConsoleLogger.info("Used memory: " + Runtime.getRuntime().totalMemory());
		ConsoleLogger.info("Max memory: " + Runtime.getRuntime().maxMemory());

		ConsoleLogger.info("*** Done with " + this.getClass().getName() + ".printCurrentSummary().");
	}
}