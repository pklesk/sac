package sac;

/**
 * Any function of search state (e.g. g, h cost functions in graph searches, or position heuristics in game tree
 * searches).
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class StateFunction {

	/**
	 * Creates a new instance of graph state.
	 */
	public StateFunction() {
	}

	/**
	 * Calculates and returns the value of the function for given state.
	 * 
	 * @param state reference to the state for which the function should be calculated
	 * @return calculated function value
	 */
	public double calculate(State state) {
		return 0.0;
	}
}