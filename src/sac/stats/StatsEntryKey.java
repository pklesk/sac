package sac.stats;

import java.util.Arrays;

/**
 * Entry key for statistics.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class StatsEntryKey {

	/**
	 * Category (quantity of interest) as string.
	 */
	private String category;

	/**
	 * Mutli index for the entry (typically, multi index is defined by a vector of current loop indices in an experiment).
	 */
	private Object[] multiIndex;

	/**
	 * Creates a new instance of StatsEntryKey.
	 * 
	 * @param category category (quantity of interest) as string
	 * @param multiIndex multi index for the entry
	 */
	public StatsEntryKey(String category, Object... multiIndex) {
		this.multiIndex = multiIndex;
		this.category = category;
	}

	/**
	 * Returns a boolean flag stating if given category and multi index (with possible nulls) match this entry key.
	 * 
	 * @param category category (quantity of interest) as string
	 * @param multiIndex multi index for the entry
	 * @return boolean flag stating if given category and multi index match this entry key
	 */
	public boolean matches(String category, Object... multiIndex) {
		if (category == null)
			return false;
		if (!this.category.equals(category))
			return false;
		for (int i = 0; i < multiIndex.length; i++) {
			if (multiIndex[i] == null)
				continue; // nulls allowed and serving as 'any'
			if ((this.multiIndex[i] == null) || (!this.multiIndex[i].equals(multiIndex[i])))
				return false;
		}
		return true;
	}

	/**
	 * Returns the category as string.
	 * 
	 * @return category as string
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Returns the multiindex as an array of objects.
	 * 
	 * @return multiindex as an array of objects
	 */
	public Object[] getMultiIndex() {
		return multiIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object otherStatsEntryKey) {
		StatsEntryKey key = (StatsEntryKey) otherStatsEntryKey;

		if (!category.equals(key.category))
			return false;
		if (this.multiIndex.length != key.multiIndex.length)
			return false;
		for (int i = 0; i < multiIndex.length; i++) {
			if (!multiIndex[i].equals(key.multiIndex[i]))
				return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + category + ";" + Arrays.toString(multiIndex) + ")";
	}
}