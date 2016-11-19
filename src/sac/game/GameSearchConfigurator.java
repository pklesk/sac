package sac.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import sac.IdentifierType;

/**
 * Set of configuration settings for game searches.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class GameSearchConfigurator {

	/**
	 * Identifier type for states. By default: STRING.
	 */
	private IdentifierType identifierType = IdentifierType.HASH_CODE;

	/**
	 * Depth limit expressed in full moves (not plies). By default: 3.5 (i.e. 7 plies).
	 */
	private double depthLimit = 3.5;

	/**
	 * Is transposition table on. By default: true.
	 */
	private boolean transpositionTableOn = true;

	/**
	 * Class name for transposition table. By default: sac.game.TranspositionTableAsHashMap.
	 */
	private String transpositionTableClassName = "sac.game.TranspositionTableAsHashMap";

	/**
	 * Is quiescence on. By default: true.
	 */
	private boolean quiescenceOn = true;

	/**
	 * Is refutation table on. By default: true.
	 */
	private boolean refutationTableOn = true;

	/**
	 * Class name for refutation table. By default: sac.game.RefutationTableAsHashMap.
	 */
	private String refutationTableClassName = "sac.game.RefutationTableAsHashMap";

	/**
	 * Depth limit for refutation table. By default: 2.0 (RefutationTableImpl.DEFAULT_DEPTH_LIMIT).
	 */
	private double refutationTableDepthLimit = RefutationTableImpl.DEFAULT_DEPTH_LIMIT;

	/**
	 * Do parents memorize references to their children. Set to false for lower memory usage (WARNING: in that case
	 * drawing game tree via GraphViz is impossible). By default: false.
	 */
	private boolean parentsMemorizingChildren = false;

	/**
	 * Time limit in milliseconds. By default: 'infinity' in long type (Long.MAX_VALUE).
	 */
	private long timeLimit = Long.MAX_VALUE;

	/**
	 * Creates new configurator with default settings.
	 */
	public GameSearchConfigurator() {
	}

	/**
	 * Createas new configurator from given properties file.
	 * 
	 * @param propertiesFilePath path to properties file
	 * @throws Exception whenever reading properties file or parsing settings fails
	 */
	public GameSearchConfigurator(String propertiesFilePath) throws Exception {
		File file = new File(propertiesFilePath);
		InputStream is = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(is);

		identifierType = IdentifierType.valueOf(properties.getProperty("identifierType"));
		depthLimit = Double.valueOf(properties.getProperty("depthLimit"));
		transpositionTableOn = Boolean.valueOf(properties.getProperty("transpositionTableOn"));
		transpositionTableClassName = properties.getProperty("transpositionTableClassName");
		quiescenceOn = Boolean.valueOf(properties.getProperty("quiescenceOn"));
		refutationTableOn = Boolean.valueOf(properties.getProperty("refutationTableOn"));
		refutationTableClassName = properties.getProperty("refutationTableClassName");
		parentsMemorizingChildren = Boolean.valueOf(properties.getProperty("parentsMemorizingChildren"));
		String timeLimitString = properties.getProperty("timeLimit");
		if ((timeLimitString == null) || (timeLimitString.trim().length() == 0) || (timeLimitString.trim().equals("Long.MAX_VALUE")))
			timeLimit = Long.MAX_VALUE;
		else
			timeLimit = Long.valueOf(properties.getProperty("timeLimit"));
	}

	/**
	 * Returns boolean flag deciding if transposition table should be used.
	 * 
	 * @return boolean flag deciding if transposition table should be used
	 */
	public boolean isTranspositionTableOn() {
		return transpositionTableOn;
	}

	/**
	 * Sets boolean flag deciding if transposition table should be used.
	 * 
	 * @param transpositionTableOn boolean flag to be set
	 */
	public void setTranspositionTableOn(boolean transpositionTableOn) {
		this.transpositionTableOn = transpositionTableOn;
	}

	/**
	 * Returns transposition table class name.
	 * 
	 * @return transposition table class name
	 */
	public String getTranspositionTableClassName() {
		return transpositionTableClassName;
	}

	/**
	 * Sets transposition table class name.
	 * 
	 * @param transpositionTableClassName transposition table class name to be set
	 */
	public void setTranspositionTableClassName(String transpositionTableClassName) {
		this.transpositionTableClassName = transpositionTableClassName;
	}

	/**
	 * Returns boolean flag deciding if quiescence search should be used.
	 * 
	 * @return boolean flag deciding if quiescence search should be used
	 */
	public boolean isQuiescenceOn() {
		return quiescenceOn;
	}

	/**
	 * Sets boolean flag deciding if quiescence search should be used.
	 * 
	 * @param quiescenceOn boolean flag to be set
	 */
	public void setQuiescenceOn(boolean quiescenceOn) {
		this.quiescenceOn = quiescenceOn;
	}

	/**
	 * Returns boolean flag deciding if refutation table should be used.
	 * 
	 * @return boolean flag deciding if refutation table should be used
	 */
	public boolean isRefutationTableOn() {
		return refutationTableOn;
	}

	/**
	 * Sets boolean flag deciding if refutation table should be used.
	 * 
	 * @param refutationTableOn boolean flag to be set
	 */
	public void setRefutationTableOn(boolean refutationTableOn) {
		this.refutationTableOn = refutationTableOn;
	}

	/**
	 * Returns refutation table class name.
	 * 
	 * @return refutation table class name
	 */
	public String getRefutationTableClassName() {
		return refutationTableClassName;
	}

	/**
	 * Sets refutation table class name.
	 * 
	 * @param refutationTableClassName refutation table class name to be set
	 */
	public void setRefutationTableClassName(String refutationTableClassName) {
		this.refutationTableClassName = refutationTableClassName;
	}

	/**
	 * Returns depth limit for refutation table.
	 * 
	 * @return depth limit for refutation table
	 */
	public double getRefutationTableDepthLimit() {
		return refutationTableDepthLimit;
	}

	/**
	 * Sets depth limit for refutation table.
	 * 
	 * @param refutationTableDepthLimit depth limit for refutation table to be set
	 */
	public void setRefutationTableDepthLimit(double refutationTableDepthLimit) {
		this.refutationTableDepthLimit = refutationTableDepthLimit;
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
	 * Returns depth limit.
	 * 
	 * @return depth limit
	 */
	public double getDepthLimit() {
		return depthLimit;
	}

	/**
	 * Sets depth limit.
	 * 
	 * @param depthLimit value to be set
	 */
	public void setDepthLimit(double depthLimit) {
		this.depthLimit = depthLimit;
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
		builder.append("depthLimit = " + depthLimit);
		builder.append("\n");
		builder.append("transpositionTableOn = " + transpositionTableOn);
		builder.append("\n");
		builder.append("transpositionTableClassName = " + transpositionTableClassName);
		builder.append("\n");
		builder.append("quiescenceOn = " + quiescenceOn);
		builder.append("\n");
		builder.append("refutationTableOn = " + refutationTableOn);
		builder.append("\n");
		builder.append("refutationTableClassName = " + refutationTableClassName);
		builder.append("\n");
		builder.append("refutationTableDepthLimit = " + refutationTableDepthLimit);
		builder.append("\n");
		builder.append("parentsMemorizingChildren= " + parentsMemorizingChildren);
		builder.append("\n");
		builder.append("timeLimit = " + ((timeLimit < Long.MAX_VALUE) ? timeLimit : "Long.MAX_VALUE"));
		return builder.toString();
	}	
}