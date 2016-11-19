package sac.examples.slidingpuzzle;

import sac.State;

/**
 * 'Linear conflicts' heuristics for sliding puzzle problem (actually: 'manhattan plus linear conflicts').
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class HFunctionLinearConflicts extends HFunctionManhattan {

	@Override
	public double calculate(State state) {
		SlidingPuzzle slidingPuzzle = (SlidingPuzzle) state;
		return super.calculate(state) + linearConflicts(slidingPuzzle);
	}

	/**
	 * Returns the number of linear conflicts (both in rows and columns) multiplied by two (since at least two extra
	 * moves are required to eliminate a single linear conflict).
	 * 
	 * @param slidingPuzzle reference to a sliding puzzle
	 * @return number of linear conflicts multiplied by two
	 */
	protected int linearConflicts(SlidingPuzzle slidingPuzzle) {
		int n = SlidingPuzzle.n;
		byte[] table = slidingPuzzle.board;

		int h = 0;

		int[] group = new int[n];
		int[] conflicts = new int[n];

		// rows
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++)
				group[j] = table[i * n + j];

			for (int j = 0; j < n - 1; j++) {
				if ((group[j] / n != i) && (group[j] > 0)) // is this row the goal row for group[j]
					conflicts[j] = 0;
				else {
					for (int k = j + 1; k < n; k++) {
						if ((group[k] / n == i) && (group[k] > 0) && (group[j] > group[k]))
							conflicts[j]++;
					}
				}
			}

			// while there remain some positive conflicts[j]
			while (true) {
				int max = Integer.MIN_VALUE;
				int jMax = -1;
				for (int j = 0; j < n - 1; j++)
					if (conflicts[j] > max) {
						max = conflicts[j];
						jMax = j;
					}
				if (max <= 0)
					break;
				conflicts[jMax] = 0;
				for (int k = jMax + 1; k < n; k++)
					if ((group[k] / n == i) && (group[jMax] > group[k])) {
						h += 2.0;
						conflicts[k]--;
					}
			}
		}

		// columns
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++)
				group[j] = table[j * n + i];

			for (int j = 0; j < n - 1; j++) {
				if ((group[j] % n != i) && (group[j] > 0)) // is this column the goal row for group[j]
					conflicts[j] = 0;
				else {
					for (int k = j + 1; k < n; k++) {
						if ((group[k] % n == i) && (group[k] > 0) && (group[j] > group[k]))
							conflicts[j]++;
					}
				}
			}

			// while there remain some positive conflicts[j]
			while (true) {
				int max = Integer.MIN_VALUE;
				int jMax = -1;
				for (int j = 0; j < n - 1; j++)
					if (conflicts[j] > max) {
						max = conflicts[j];
						jMax = j;
					}

				if (max <= 0)
					break;
				conflicts[jMax] = 0;
				for (int k = jMax + 1; k < n; k++)
					if ((group[k] % n == i) && (group[jMax] > group[k])) {
						h += 2.0;
						conflicts[k]--;
					}
			}
		}

		return h;
	}
}