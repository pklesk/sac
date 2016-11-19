package sac.stats;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sac.SearchAlgorithm;
import sac.game.GameSearchAlgorithm;
import sac.graph.GraphSearchAlgorithm;

/**
 * Statistics object, meant to memorize quantities of interest from a search algorithm after its execution has been
 * finished.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class Stats {

	/**
	 * The map with statistics entries.
	 */
	private Map<StatsEntryKey, Double> entries;

	/**
	 * Create new instance of statistics object.
	 */
	public Stats() {
		entries = new HashMap<StatsEntryKey, Double>();
	}

	/**
	 * Adds an entry into this statistics object for given category, value and multi index.
	 * 
	 * @param category category (quantity of interest) as string
	 * @param value value to be memorized for given category
	 * @param multiIndex multi index for the entry (typically, multi index is defined by a vector of current loop
	 *            indices in an experiment)
	 */
	public void addEntry(String category, double value, Object... multiIndex) {
		StatsEntryKey key = new StatsEntryKey(category, multiIndex);
		entries.put(key, Double.valueOf(value));
	}

	/**
	 * Adds entries into this statistics object for multiple categories of search algorithm and given multi index. This
	 * method checks inside if given algorithm is an instance of GraphSearchAlgorithm or GameSearchAlgorithm.
	 * 
	 * @param algorithm reference to search algorithm
	 * @param multiIndex multi index for entries (typically, multi index is defined by a vector of current loop indices
	 *            in an experiment)
	 */
	public void addEntries(SearchAlgorithm algorithm, Object... multiIndex) {
		if (algorithm instanceof GraphSearchAlgorithm) {
			GraphSearchAlgorithm graphSA = (GraphSearchAlgorithm) algorithm;
			addEntry(StatsCategory.GRAPH_SEARCH_DURATION_TIME.toString(), graphSA.getDurationTime(), multiIndex);
			addEntry(StatsCategory.GRAPH_SEARCH_SOLUTIONS.toString(), Double.valueOf(graphSA.getSolutions().size()), multiIndex);
			addEntry(StatsCategory.GRAPH_SEARCH_CLOSED_STATES.toString(), Double.valueOf(graphSA.getClosedStatesCount()), multiIndex);
			addEntry(StatsCategory.GRAPH_SEARCH_OPEN_STATES.toString(), Double.valueOf(graphSA.getOpenSet().size()), multiIndex);
			if (!graphSA.getSolutions().isEmpty()) {
				addEntry(StatsCategory.GRAPH_SEARCH_PATH_LENGTH.toString(), Double.valueOf(graphSA.getSolutions().get(0).getPath().size()), multiIndex);
				addEntry(StatsCategory.GRAPH_SEARCH_PATH_G.toString(), Double.valueOf(graphSA.getSolutions().get(0).getG()), multiIndex);
			}
		} else if (algorithm instanceof GameSearchAlgorithm) {
			GameSearchAlgorithm gameSA = (GameSearchAlgorithm) algorithm;
			addEntry(StatsCategory.GAME_SEARCH_DURATION_TIME.toString(), gameSA.getDurationTime(), multiIndex);
			addEntry(StatsCategory.GAME_SEARCH_CLOSED_STATES.toString(), Double.valueOf(gameSA.getClosedStatesCount()), multiIndex);
			addEntry(StatsCategory.GAME_SEARCH_TRANSPOSITION_TABLE_SIZE.toString(), Double.valueOf(gameSA.getTranspositionTable().size()), multiIndex);
			addEntry(StatsCategory.GAME_SEARCH_TRANSPOSITION_TABLE_USES.toString(), Double.valueOf(gameSA.getTranspositionTable().getUsesCount()), multiIndex);
			addEntry(StatsCategory.GAME_SEARCH_REFUTATION_TABLE_SIZE.toString(), Double.valueOf(gameSA.getRefutationTable().size()), multiIndex);
			addEntry(StatsCategory.GAME_SEARCH_REFUTATION_TABLE_USES.toString(), Double.valueOf(gameSA.getRefutationTable().getUsesCount()), multiIndex);
			addEntry(StatsCategory.GAME_SEARCH_DEPTH_REACHED.toString(), Double.valueOf(gameSA.getDepthReached()), multiIndex);
		}
	}

	/**
	 * Returns a list of all values memorized for given category and multi index pattern (the pattern may contain nulls
	 * working as 'any').
	 * 
	 * @param category category (quantity of interest) as string
	 * @param multiIndexPattern multi index pattern (may contain nulls)
	 * @return list of values memorized for given category and multi index pattern
	 */
	public List<Double> values(String category, Object... multiIndexPattern) {
		List<Double> uniques = new LinkedList<Double>();
		for (StatsEntryKey key : entries.keySet()) {
			if (key.matches(category, multiIndexPattern))
				uniques.add(entries.get(key));
		}
		return uniques;
	}

	/**
	 * Returns the result of a wanted operation (mean, variance, min, max or count) for given category and multi index
	 * pattern.
	 * 
	 * @param operationType operation type of statistics
	 * @param category category (quantity of interest) as string
	 * @param multiIndexPattern multi index pattern (may contain nulls)
	 * @return result of a wanted operation (mean, variance, min, max or count) for given category and multi index
	 *         pattern
	 */
	public double operation(StatsOperationType operationType, String category, Object... multiIndexPattern) {
		double value = 0.0;
		switch (operationType) {
		case MEAN:
			value = mean(category, multiIndexPattern);
			break;
		case VARIANCE:
			value = variance(category, multiIndexPattern);
			break;
		case MIN:
			value = min(category, multiIndexPattern);
			break;
		case MAX:
			value = max(category, multiIndexPattern);
			break;
		case COUNT:
			value = count(category, multiIndexPattern);
			break;
		default:
			value = mean(category, multiIndexPattern);
			break;
		}
		;
		return value;
	}

	/**
	 * Returns the result of 'mean' operation for given category and multi index pattern.
	 * 
	 * @param category category (quantity of interest) as string
	 * @param multiIndexPattern multi index pattern (may contain nulls)
	 * @return result of 'mean' operation for given category and multi index pattern
	 */
	public double mean(String category, Object... multiIndexPattern) {
		double sum = 0.0;
		int count = 0;
		for (StatsEntryKey key : entries.keySet()) {
			if (key.matches(category, multiIndexPattern)) {
				sum += entries.get(key);
				count++;
			}
		}
		return (count != 0) ? sum / count : 0.0;
	}

	/**
	 * Returns the result of 'variance' operation for given category and multi index pattern.
	 * 
	 * @param category category (quantity of interest) as string
	 * @param multiIndexPattern multi index pattern (may contain nulls)
	 * @return result of 'variance' operation for given category and multi index pattern
	 */
	public double variance(String category, Object... multiIndexPattern) {
		double sum = 0.0;
		double sumOfSquares = 0.0;
		int count = 0;
		for (StatsEntryKey key : entries.keySet()) {
			if (key.matches(category, multiIndexPattern)) {
				double value = entries.get(key);
				sum += value;
				sumOfSquares += value * value;
				count++;
			}
		}
		double mean = sum / count;
		double meanOfSquares = sumOfSquares / count;
		return (count != 0) ? meanOfSquares - mean * mean : 0.0;
	}

	/**
	 * Returns the result of 'min' operation for given category and multi index pattern.
	 * 
	 * @param category category (quantity of interest) as string
	 * @param multiIndexPattern multi index pattern (may contain nulls)
	 * @return result of 'min' operation for given category and multi index pattern
	 */
	public double min(String category, Object... multiIndexPattern) {
		double min = Double.POSITIVE_INFINITY;
		for (StatsEntryKey key : entries.keySet()) {
			if (key.matches(category, multiIndexPattern)) {
				double value = entries.get(key);
				if (value < min)
					min = value;
			}
		}
		return min;
	}

	/**
	 * Returns the result of 'max' operation for given category and multi index pattern.
	 * 
	 * @param category category (quantity of interest) as string
	 * @param multiIndexPattern multi index pattern (may contain nulls)
	 * @return result of 'max' operation for given category and multi index pattern
	 */
	public double max(String category, Object... multiIndexPattern) {
		double max = Double.NEGATIVE_INFINITY;
		for (StatsEntryKey key : entries.keySet()) {
			if (key.matches(category, multiIndexPattern)) {
				double value = entries.get(key);
				if (value > max)
					max = value;
			}
		}
		return max;
	}

	/**
	 * Returns the result of 'count' operation for given category and multi index pattern.
	 * 
	 * @param category category (quantity of interest) as string
	 * @param multiIndexPattern multi index pattern (may contain nulls)
	 * @return result of 'count' operation for given category and multi index pattern
	 */
	public int count(String category, Object... multiIndexPattern) {
		int count = 0;
		for (StatsEntryKey key : entries.keySet()) {
			if (key.matches(category, multiIndexPattern))
				count++;
		}
		return count;
	}

	/**
	 * Returns the list of values for a specific position in the multi index pattern and given category.
	 * 
	 * @param category category (quantity of interest) as string
	 * @param subindexPosition position of interest in the index
	 * @param multiIndexPattern multi index pattern (may contain nulls)
	 * @return list of values for a specific position in the multi index pattern and given category
	 */
	List<Object> subindexValues(String category, int subindexPosition, Object... multiIndexPattern) {
		List<Object> values = new LinkedList<Object>();
		for (StatsEntryKey key : entries.keySet()) {
			if (key.matches(category, multiIndexPattern)) {
				values.add(key.getMultiIndex()[subindexPosition]);
			}
		}
		return values;
	}
}