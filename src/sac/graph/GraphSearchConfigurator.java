package sac.graph;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import sac.IdentifierType;

/**
 * Set of configuration settings for graph searches.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class GraphSearchConfigurator {

	/**
	 * Identifier type for states. By default: HASH_CODE.
	 */
	private IdentifierType identifierType = IdentifierType.HASH_CODE;

	/**
	 * Class name for open set. By default: sac.graph.OpenSetSacFast.
	 */
	private String openSetClassName = OpenSetAsPriorityQueueFastContainsFastReplace.class.getName();

	/**
	 * Is closed set on. By default: true. Closed set can be off when the search space is a tree (not a graph with
	 * cycles).
	 */
	private boolean closedSetOn = true;

	/**
	 * Class name for closed set. By default: sac.graph.ClosedSetAsHashMap.
	 */
	private String closedSetClassName = ClosedSetAsHashMap.class.getName();

	/**
	 * Do parents memorize references to their children. Set to false for lower memory usage (WARNING: in that case
	 * drawing graph via GraphViz is impossible). By default: false.
	 */
	private boolean parentsMemorizingChildren = false;

	/**
	 * Wanted number of solutions. By default: 1.
	 */
	private int wantedNumberOfSolutions = 1;

	/**
	 * Time limit in milliseconds. By default: 'infinity' in long type (Long.MAX_VALUE).
	 */
	private long timeLimit = Long.MAX_VALUE;

	/**
	 * Is monitor on. By default: false;
	 */
	private boolean monitorOn = false;

	/**
	 * Class name for monitor. By default: sac.graph.DefaultConsoleMonitor.
	 */
	private String monitorClassName = ConsoleGraphSearchMonitor.class.getName();

	/**
	 * Monitor time period. By default: 1000 ms.
	 */
	private long monitorRefreshTime = 1000;

	/**
	 * Creates new configurator with default settings.
	 */
	public GraphSearchConfigurator() {
	}

	/**
	 * Createas new configurator from given properties file.
	 * 
	 * @param propertiesFilePath path to properties file
	 * @throws Exception whenever reading properties file or parsing settings fails
	 */
	public GraphSearchConfigurator(String propertiesFilePath) throws Exception {
		File file = new File(propertiesFilePath);
		InputStream is = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(is);

		identifierType = IdentifierType.valueOf(properties.getProperty("identifierType"));
		openSetClassName = properties.getProperty("openSetClassName");
		closedSetOn = Boolean.valueOf(properties.getProperty("closedSetOn"));
		closedSetClassName = properties.getProperty("closedSetClassName");
		parentsMemorizingChildren = Boolean.valueOf(properties.getProperty("parentsMemorizingChildren"));
		String wantedNumberOfSolutionsString = properties.getProperty("wantedNumberOfSolutions");
		if ((wantedNumberOfSolutionsString == null) || (wantedNumberOfSolutionsString.trim().length() == 0)
				|| (wantedNumberOfSolutionsString.trim().equals("Integer.MAX_VALUE")))
			wantedNumberOfSolutions = Integer.MAX_VALUE;
		else
			wantedNumberOfSolutions = Integer.valueOf(properties.getProperty("wantedNumberOfSolutions"));
		String timeLimitString = properties.getProperty("timeLimit");
		if ((timeLimitString == null) || (timeLimitString.trim().length() == 0) || (timeLimitString.trim().equals("Long.MAX_VALUE")))
			timeLimit = Long.MAX_VALUE;
		else
			timeLimit = Long.valueOf(properties.getProperty("timeLimit"));
		monitorOn = Boolean.valueOf(properties.getProperty("monitorOn"));
		monitorClassName = properties.getProperty("monitorClassName");
		monitorRefreshTime = Long.valueOf(properties.getProperty("monitorRefreshTime"));
	}

	/**
	 * Gets open set class name.
	 * 
	 * @return open set class name
	 */
	public String getOpenSetClassName() {
		return openSetClassName;
	}

	/**
	 * Sets open set class name.
	 * 
	 * @param openSetClassName open set class name to be set
	 */
	public void setOpenSetClassName(String openSetClassName) {
		this.openSetClassName = openSetClassName;
	}

	/**
	 * Returns boolean flag deciding if closed set should be used.
	 * 
	 * @return boolean flag deciding if closed set should be used
	 */
	public boolean isClosedSetOn() {
		return closedSetOn;
	}

	/**
	 * Sets boolean flag deciding if closed set should be used.
	 * 
	 * @param closedSetOn boolean flag to be set
	 */
	public void setClosedSetOn(boolean closedSetOn) {
		this.closedSetOn = closedSetOn;
	}

	/**
	 * Returns closed set class name.
	 * 
	 * @return closed set class name
	 */
	public String getClosedSetClassName() {
		return closedSetClassName;
	}

	/**
	 * Sets closed set class name.
	 * 
	 * @param closedSetClassName closed set class name to be set
	 */
	public void setClosedSetClassName(String closedSetClassName) {
		this.closedSetClassName = closedSetClassName;
	}

	/**
	 * Returns identifier type.
	 * 
	 * @return identifier type
	 */
	public IdentifierType getIdentifierType() {
		return identifierType;
	}

	/**
	 * Sets identifier type.
	 * 
	 * @param identifierType type to be set
	 */
	public void setIdentifierType(IdentifierType identifierType) {
		this.identifierType = identifierType;
	}

	/**
	 * Returns boolean flag deciding if parents memorize references to their children.
	 * 
	 * @return boolean flag deciding if parents memorize references to their children
	 */
	public boolean isParentsMemorizingChildren() {
		return parentsMemorizingChildren;
	}

	/**
	 * Sets boolean flag deciding if parents memorize references to their children.
	 * 
	 * @param parentsMemorizingChildren boolean flag to be set
	 */
	public void setParentsMemorizingChildren(boolean parentsMemorizingChildren) {
		this.parentsMemorizingChildren = parentsMemorizingChildren;
	}

	/**
	 * Returns the number of wanted solutions.
	 * 
	 * @return number of wanted solutions
	 */
	public int getWantedNumberOfSolutions() {
		return wantedNumberOfSolutions;
	}

	/**
	 * Sets the number of wanted solutions.
	 * 
	 * @param wantedNumberOfSolutions value to be set
	 */
	public void setWantedNumberOfSolutions(int wantedNumberOfSolutions) {
		this.wantedNumberOfSolutions = wantedNumberOfSolutions;
	}

	/**
	 * Returns time limit (in milliseconds).
	 * 
	 * @return time limit
	 */
	public long getTimeLimit() {
		return timeLimit;
	}

	/**
	 * Sets time limit (in milliseconds).
	 * 
	 * @param timeLimit value to be set
	 */
	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}

	/**
	 * Returns boolean flag deciding if monitor is on.
	 * 
	 * @return boolean flag deciding if monitor is on
	 */
	public boolean isMonitorOn() {
		return monitorOn;
	}

	/**
	 * Sets boolean flag deciding if monitor is on.
	 * 
	 * @param monitorOn boolean flag to be set
	 */
	public void setMonitorOn(boolean monitorOn) {
		this.monitorOn = monitorOn;
	}

	/**
	 * Returns monitor class name.
	 * 
	 * @return monitor class name
	 */
	public String getMonitorClassName() {
		return monitorClassName;
	}

	/**
	 * Sets monitor class name.
	 * 
	 * @param monitorClassName monitor class name to be set
	 */
	public void setMonitorClassName(String monitorClassName) {
		this.monitorClassName = monitorClassName;
	}

	/**
	 * Returns monitor refresh time (in milliseconds).
	 * 
	 * @return monitor refresh time
	 */
	public long getMonitorRefreshTime() {
		return monitorRefreshTime;
	}

	/**
	 * Sets monitor refresh time (in milliseconds).
	 * 
	 * @param monitorRefreshTime value to be set
	 */
	public void setMonitorRefreshTime(long monitorRefreshTime) {
		this.monitorRefreshTime = monitorRefreshTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("");
		builder.append("identifierType = " + identifierType);
		builder.append("\n");
		builder.append("openSetClassName = " + openSetClassName);
		builder.append("\n");
		builder.append("closedSetOn = " + closedSetOn);
		builder.append("\n");
		builder.append("closedSetClassName = " + closedSetClassName);
		builder.append("\n");
		builder.append("parentsMemorizingChildren= " + parentsMemorizingChildren);
		builder.append("\n");
		builder.append("wantedNumberOfSolutions = " + wantedNumberOfSolutions);
		builder.append("\n");
		builder.append("timeLimit = " + ((timeLimit < Long.MAX_VALUE) ? timeLimit : "Long.MAX_VALUE"));
		builder.append("\n");
		builder.append("monitorOn = " + monitorOn);
		builder.append("\n");
		builder.append("monitorClassName = " + monitorClassName);
		builder.append("\n");
		builder.append("monitorRefreshTime = " + ((monitorRefreshTime < Long.MAX_VALUE) ? monitorRefreshTime : "Long.MAX_VALUE"));
		return builder.toString();
	}
}