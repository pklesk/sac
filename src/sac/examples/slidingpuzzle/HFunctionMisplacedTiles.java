package sac.examples.slidingpuzzle;

import sac.State;
import sac.StateFunction;

/**
 * 'Misplaced tiles' heuristics for sliding puzzle problem.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class HFunctionMisplacedTiles extends StateFunction {

	@Override
	public double calculate(State state) {
		SlidingPuzzle slidingPuzzle = (SlidingPuzzle) state;
		double h = 0.0;
		for (int i = 0; i < slidingPuzzle.board.length; i++) {
			if ((i != slidingPuzzle.emptyIndex) && (slidingPuzzle.board[i] != i))
				h += 1.0;
		}
		return h;
	}
}