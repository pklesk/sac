package sac.examples.sudoku;

import sac.State;
import sac.StateFunction;

/**
 * 'Empty cells' heuristics for sudoku.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class HFunctionEmptyCells extends StateFunction {

	@Override
	public double calculate(State state) {
			Sudoku sudoku = (Sudoku) state;
			sudoku.precalculateForHeuristics();
			return ((sudoku.minRemainingPossibilities == 0) && (sudoku.emptyCells > 0)) ? Double.POSITIVE_INFINITY : sudoku.emptyCells;				
	}	
}