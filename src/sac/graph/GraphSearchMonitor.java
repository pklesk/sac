package sac.graph;

import sac.util.ConsoleLogger;

/**
 * Abstract monitor of graph search algorithm. Meant to be extended by displaying/logging classes suitable for given
 * environment.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public abstract class GraphSearchMonitor implements Runnable {

	/**
	 * Reference to graph search algorithm.
	 */
	protected GraphSearchAlgorithm algorithm;

	/**
	 * Refresh time (in miliseconds).
	 */
	protected long refreshTime;

	/**
	 * Start time of this monitor.
	 */
	protected long startTime;

	/**
	 * Boolean flag showing if monitor is running.
	 */
	private boolean running = false;

	/**
	 * Creates new instance of graph search monitor.
	 * 
	 * @param algorithm reference to graph search algorithm
	 * @param refreshTime refresh time (in miliseconds)
	 */
	public GraphSearchMonitor(GraphSearchAlgorithm algorithm, long refreshTime) {
		this.algorithm = algorithm;
		this.refreshTime = refreshTime;
		ConsoleLogger.info("*** Initializing " + this.getClass().getCanonicalName() + " with params: algorithm = " + algorithm.getClass().getCanonicalName()
				+ ", refreshTime = " + refreshTime + " ms.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		startTime = System.currentTimeMillis();
		running = true;
		while (running) {
			try {
				printCurrentSummary();
				Thread.sleep(refreshTime);
			} catch (InterruptedException ie) {
			}
		}
		printCurrentSummary(); // summary at the stop moment
	}

	/**
	 * Returns current running time of graph search algorithm (in miliseconds).
	 * 
	 * @return current running time of graph search algorithm (in miliseconds)
	 */
	public long getTime() {
		long time = System.currentTimeMillis() - algorithm.getStartTime();
		return (time > 0) ? time : 0;
	}

	/**
	 * Stops the monitor.
	 */
	public void stop() {
		running = false;
	}

	/**
	 * Returns reference to graph search algorithm this monitor observes.
	 * 
	 * @return reference to graph search algorithm
	 */
	public GraphSearchAlgorithm getAlgorithm() {
		return algorithm;
	}

	/**
	 * Sets reference to graph search algorithm
	 * 
	 * @param algorithm reference to graph search algorithm
	 */
	public void setAlgorithm(GraphSearchAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Returns refresh time.
	 * 
	 * @return refresh time
	 */
	public long getRefreshTime() {
		return refreshTime;
	}

	/**
	 * Sets refresh time.
	 * 
	 * @param refreshTime new refresh time value to be set
	 */
	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	/**
	 * Returns start time of this monitor.
	 * 
	 * @return start time of this monitor
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * To be overridden by the user.
	 */
	public abstract void printCurrentSummary();
}