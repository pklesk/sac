package sac.game;

/**
 * An entry in the transposition table.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class TranspositionTableEntry {

	/**
	 * Lower bound on game value.
	 */
	private Double lowerBoundOnGameValue = null;

	/**
	 * Exact game value.
	 */
	private Double exactGameValue = null;

	/**
	 * Upper bound on game value.
	 */
	private Double upperBoundOnGameValue = null;

	/**
	 * Creates new instance of transposition table entry.
	 * 
	 * @param lowerBoundOnGameValue lower bound on game value or null
	 * @param exactGameValue exact game value or null
	 * @param upperBoundOnGameValue upper bound on game value or null
	 */
	public TranspositionTableEntry(Double lowerBoundOnGameValue, Double exactGameValue, Double upperBoundOnGameValue) {
		this.lowerBoundOnGameValue = lowerBoundOnGameValue;
		this.exactGameValue = exactGameValue;
		this.upperBoundOnGameValue = upperBoundOnGameValue;
	}

	/**
	 * Returns the lower bound on game value or null (if not known).
	 * 
	 * @return lower bound on game value
	 */
	public Double getLowerBoundOnGameValue() {
		return lowerBoundOnGameValue;
	}

	/**
	 * Sets the lower bound on game value.
	 * 
	 * @param lowerBoundOnGameValue lower bound on game value to be set
	 */
	public void setLowerBoundOnGameValue(Double lowerBoundOnGameValue) {
		this.lowerBoundOnGameValue = lowerBoundOnGameValue;
	}

	/**
	 * Returns the exact game value or null (if not known).
	 * 
	 * @return exact game value or null (if not known)
	 */
	public Double getExactGameValue() {
		return exactGameValue;
	}

	/**
	 * Sets the exact game value.
	 * 
	 * @param exactGameValue exact game value to be set
	 */
	public void setExactGameValue(Double exactGameValue) {
		this.exactGameValue = exactGameValue;
	}

	/**
	 * Returns the upper bound on game value or null (if not known).
	 * 
	 * @return upper bound on game value
	 */
	public Double getUpperBoundOnGameValue() {
		return upperBoundOnGameValue;
	}

	/**
	 * Sets the upper bound on game value.
	 * 
	 * @param upperBoundOnGameValue upper bound on game value to be set
	 */
	public void setUpperBoundOnGameValue(Double upperBoundOnGameValue) {
		this.upperBoundOnGameValue = upperBoundOnGameValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + lowerBoundOnGameValue + "," + exactGameValue + "," + upperBoundOnGameValue + ")";
	}
}