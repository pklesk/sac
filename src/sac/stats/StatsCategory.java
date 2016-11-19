package sac.stats;

/**
 * Category (quantity of interest) in statistics.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public enum StatsCategory {
	GRAPH_SEARCH_DURATION_TIME, GRAPH_SEARCH_CLOSED_STATES, GRAPH_SEARCH_OPEN_STATES, GRAPH_SEARCH_SOLUTIONS, GRAPH_SEARCH_PATH_LENGTH, GRAPH_SEARCH_PATH_G,
	GAME_SEARCH_DURATION_TIME, GAME_SEARCH_CLOSED_STATES, GAME_SEARCH_TRANSPOSITION_TABLE_SIZE, GAME_SEARCH_TRANSPOSITION_TABLE_USES, GAME_SEARCH_REFUTATION_TABLE_SIZE,
	GAME_SEARCH_REFUTATION_TABLE_USES, GAME_SEARCH_DEPTH_REACHED;
}
