package sac.examples.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

/**
 * Sudoku state.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class Sudoku extends GraphStateImpl {

	/**
	 * Side of subsquare.
	 */
	protected static byte n;

	/**
	 * Side of sudoku board.
	 */
	protected static byte N; // n * n

	/**
	 * Sudoku board.
	 */
	protected byte[][] board = null;

	/**
	 * Number of empty cells.
	 */
	protected int emptyCells; 

	/**
	 * Sum of remaining possibilities in all cells.
	 */
	protected int sumRemainingPossibilities;
	
	/**
	 * Minimum number of remaining possibilities in some cell.
	 */
	protected int minRemainingPossibilities;

	/**
	 * Row position of the cell with mimum number of possibilities.
	 */
	protected int minI = -1;

	/**
	 * Column position of the cell with mimum number of possibilities.
	 */
	protected int minJ = -1;

	/**
	 * Remaining possibilities in the 'minimum cell'.
	 */
	protected SortedSet<Byte> possibilities = null;

	/**
	 * Creates new sudoku as a copy of given parent.
	 * 
	 * @param parent sudoku to be copied
	 */
	public Sudoku(Sudoku parent) {
		board = new byte[N][N];
		emptyCells = parent.emptyCells;
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				board[i][j] = parent.board[i][j];
			}
		possibilities = new TreeSet<Byte>();
	}

	/**
	 * Creates a sudoku with an empty board of size N x N, where N = n * n.
	 * 
	 * @param n side of subsquare (defines the size of sudoku)
	 */
	public Sudoku(int n) {
		Sudoku.n = (byte) n;
		N = (byte) (n * n);
		board = new byte[N][N];
		emptyCells = N * N;
		possibilities = new TreeSet<Byte>();
	}

	/**
	 * Creates a sudoku and populates it from a comma separated string.
	 * 
	 * @param n side of subsquare (defines the size of sudoku)
	 * @param sudokuAsCommaSeparatedString sudoku board given as a comma seperated string
	 */
	public Sudoku(int n, String sudokuAsCommaSeparatedString) {
		Sudoku.n = (byte) n;
		N = (byte) (n * n);
		board = new byte[N][N];

		emptyCells = N * N;

		StringTokenizer tokenizer = new StringTokenizer(sudokuAsCommaSeparatedString, ",");
		int z = 0;
		while (tokenizer.hasMoreElements()) {
			byte number = Byte.valueOf((String) tokenizer.nextElement());
			int i = z / N;
			int j = z % N;
			board[i][j] = number;
			if (number > 0) {
				emptyCells--;
			}
			z++;
		}
		possibilities = new TreeSet<Byte>();
	}

	@Override
	public List<GraphState> generateChildren() {
		List<GraphState> children = new LinkedList<GraphState>();
		double theH = getH(); // call made in order to do pre-calculations for heuristics and to
								// discover minI, minJ
								// (if not present so far)
		if (minRemainingPossibilities == 0)
			return children; // discrepancy or solution
		if (theH > 0) {
			for (byte possibility : possibilities) {
				Sudoku child = new Sudoku(this);
				child.board[minI][minJ] = possibility;
				if (child.isAdmissible(minI, minJ)) {
					child.emptyCells = emptyCells - 1;
					child.setMoveName("(" + (minI + 1) + "," + (minJ + 1) + "):=" + possibility);
					children.add(child);
				}
			}
		}
		return children;
	}

	@Override
	public boolean isSolution() {
		return (emptyCells == 0);
	}

	@Override
	public int hashCode() {
		byte[] linearBoard = new byte[N * N];

		for (int i = 0; i < N; i++)
			System.arraycopy(board[i], 0, linearBoard, i * N, N);

		return Arrays.hashCode(linearBoard);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				builder.append(board[i][j]);
				if (j < N - 1)
					builder.append(",");
			}
			if (i < N - 1)
				builder.append("\n");
		}
		return builder.toString();
	}
	
	/**
	 * Returns a boolean flag stating if this sudoku is admissible around 
	 * the cell (i, j). False indicates that there exists at least one
	 * conflict in the i-th row or the j-th column or the subsquare containing 
	 * the cell (i, j).   
	 * 
	 * @param i row index
	 * @param j column
	 * @return boolean flag stating if this sudoku is admissible around (i, j)
	 */
	protected boolean isAdmissible(int i, int j) {
		List<Byte> groupUnderCheck = new ArrayList<Byte>();

		// square around (i, j) 
		int minI = (i / n) * n;
		int minJ = (j / n) * n;

		for (int ii = minI; ii < minI + n; ii++)
			for (int jj = minJ; jj < minJ + n; jj++)
				if (board[ii][jj] > 0)
					groupUnderCheck.add(Byte.valueOf(board[ii][jj]));
		if (!isGroupAdmissible(groupUnderCheck))
				return false;
		groupUnderCheck.clear();
		

		// i-th row
		for (int jj = 0; jj < N; jj++)
			if (board[i][jj] > 0)
				groupUnderCheck.add(Byte.valueOf(board[i][jj]));
		if (!isGroupAdmissible(groupUnderCheck))
			return false;
		groupUnderCheck.clear();

		// j-th column
		for (int ii = 0; ii < N; ii++) 
			if (board[ii][j] > 0)
				groupUnderCheck.add(Byte.valueOf(board[ii][j]));
		if (!isGroupAdmissible(groupUnderCheck))
			return false;
		

		return true;
	}
	
	/**
	 * A helper method for isAdmissible() method. Returns a boolean flag indicating if the given group of numbers (row,
	 * column, or subsquare) is admissible (i.e. contains no conflicts).
	 * 
	 * @param group group to be checked
	 * @return boolean flag indicating if given group of numbers is admissible
	 */
	protected boolean isGroupAdmissible(List<Byte> group) {
		if (group.size() == 0)
			return true; // empty group is implied by all zeros in it
		
		boolean[] visited = new boolean[N];
		for (int i = 0; i < N; i++)
			visited[i] = false;
		
		for (Byte element : group)
			if (visited[element.byteValue() - 1])
				return false;
			else
				visited[element.byteValue() - 1] = true;
		
		return true;
	}
	
	/**
	 * Performs preliminary calculations needed for heuristics.
	 */
	protected void precalculateForHeuristics() {
		minRemainingPossibilities = Integer.MAX_VALUE;
		sumRemainingPossibilities = 0;
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				if (board[i][j] > 0)
					continue;
				SortedSet<Byte> remaining = remainingPossibilities(i, j);
				int remainingSize = remaining.size();
				sumRemainingPossibilities += remainingSize;
				if (remainingSize < minRemainingPossibilities) {
					minRemainingPossibilities = remainingSize;					
					minI = i;
					minJ = j;
					possibilities = remaining;
					if (minRemainingPossibilities == 0)
						return;
				}
			}
	}

	/**
	 * Returns the number of remaining possibilities for the cell at (i, j), by eliminating the possibilities existing
	 * in the i-th row, j-th column and the subsquare that cell (i, j) belongs to.
	 * 
	 * @param i row index
	 * @param j column index
	 * @return number of remaining possibilities for the cell at (i, j)
	 */
	private SortedSet<Byte> remainingPossibilities(int i, int j) {
		SortedSet<Byte> remaining = new TreeSet<Byte>();
		for (int k = 1; k <= N; k++)
			remaining.add((byte) k);

		// removing from remaining numbers existing in i-th row and j-th column
		for (int k = 0; k < N; k++) {
			remaining.remove(board[i][k]);
			remaining.remove(board[k][j]);
		}

		// removing number as a possibility from the square i, j belongs to
		int iMin = (i / n) * n;
		int iMax = iMin + n;
		int jMin = (j / n) * n;
		int jMax = jMin + n;
		for (int k = iMin; k < iMax; k++)
			for (int l = jMin; l < jMax; l++)
				remaining.remove(board[k][l]);

		return remaining;
	}
	
	static {
		setHFunction(new HFunctionSumRemainingPossibilities());
	}

	@Override
	public String toGraphvizLabel() {
		StringBuilder builder = new StringBuilder();
		builder.append("<FONT FACE='monospace' POINT-SIZE='6'><TABLE BORDER='0' CELLBORDER='1' CELLSPACING='0' CELLPADDING='0'>");
		Sudoku sudokuParent = (Sudoku) parent;
		for (int i = 0; i < n; i++) {
			builder.append("<TR>");
			for (int j = 0; j < n; j++) {
				builder.append("<TD>");
				builder.append("<FONT FACE='monospace' POINT-SIZE='6'><TABLE BORDER='0' CELLBORDER='0' CELLSPACING='0' CELLPADDING='0'>");
				for (int k = 0; k < n; k++) {
					builder.append("<TR>");
					for (int l = 0; l < n; l++) {
						int q = i * n + k;
						int r = j * n + l;
						if ((sudokuParent != null) && (board[q][r] > 0) && (sudokuParent.board[q][r] == 0)) {
							builder.append("<TD BGCOLOR='gray'>");
							builder.append("" + board[q][r] + "");
						} else {
							builder.append("<TD>");
							builder.append((board[q][r] != 0) ? board[q][r] : "*");
						}
						builder.append("</TD>");
					}
					builder.append("</TR>");
				}
				builder.append("</TABLE></FONT>");
				builder.append("</TD>");
			}
			builder.append("</TR>");
		}
		builder.append("</TABLE></FONT>");

		return builder.toString();
	}
}