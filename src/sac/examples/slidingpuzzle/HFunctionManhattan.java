package sac.examples.slidingpuzzle;

import sac.State;
import sac.StateFunction;

/**
 * 'Manhattan' heuristics for sliding puzzle problem.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class HFunctionManhattan extends StateFunction {

	@Override
	public double calculate(State state) {
		SlidingPuzzle slidingPuzzle = (SlidingPuzzle) state;
		double h = 0.0;
		for (int i = 0; i < slidingPuzzle.board.length; i++) {
			if (i != slidingPuzzle.emptyIndex)
				h += manhattan(slidingPuzzle, i);
		}
		return h;
	}
	
	/**
	 * For a number located at given index in the sliding puzzle returns its Manhattan distance from its target
	 * location.
	 * 
	 * @param slidingPuzzle reference to a sliding puzzle
	 * @param index given index
	 * @return Manhattan distance for a number at given index
	 */
	protected int manhattan(SlidingPuzzle slidingPuzzle, int index) {
		int n = SlidingPuzzle.n;
		int i1 = slidingPuzzle.board[index] / n;
		int j1 = slidingPuzzle.board[index] % n;
		int i2 = index / n;
		int j2 = index % n;
		return Math.abs(i1 - i2) + Math.abs(j1 - j2);
	}
}