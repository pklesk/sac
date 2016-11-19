package sac.examples.slidingpuzzle;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

/**
 * Sliding puzzle state.
 * 
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>) <br>
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class SlidingPuzzle extends GraphStateImpl {

	/**
	 * Size of the puzzle (one side of the board).
	 */
	protected static byte n;

	/**
	 * Size of the puzzle as n * n.
	 */
	protected static byte N; // N = n * n

	/**
	 * Index of the position containing 0.
	 */
	protected byte emptyIndex;

	/**
	 * Board with the sliding puzzle (kept as one-dimensional array, row after row).
	 */
	protected byte[] board;

	/**
	 * Creates new instance of the sliding puzzle.
	 * 
	 * @param n wanted size of the sliding puzzle
	 */
	public SlidingPuzzle(int n) {
		SlidingPuzzle.n = (byte) n;
		SlidingPuzzle.N = (byte) (n * n);
		board = new byte[N];
		for (byte i = 0; i < N; i++)
			board[i] = i;
		emptyIndex = 0;
	}

	/**
	 * Creates new instance of the sliding puzzle initialized from a given board.
	 * 
	 * @param board initial board as array of bytes
	 */
	public SlidingPuzzle(byte[] board) {
		N = (byte) board.length;
		n = (byte) Math.sqrt(N);
		this.board = new byte[N];

		for (byte i = 0; i < N; i++) {
			this.board[i] = board[i];
			if (board[i] == 0)
				emptyIndex = i;
		}
	}

	/**
	 * Creates new instance of the sliding puzzle as a copy of the given parent.
	 * 
	 * @param parent reference to parent to copy from
	 */
	public SlidingPuzzle(SlidingPuzzle parent) {
		board = new byte[N];
		for (byte i = 0; i < N; i++)
			board[i] = parent.board[i];
		emptyIndex = parent.emptyIndex;
	}

	/**
	 * Returns the list of possible moves (new positions for the empty element) for the current state.
	 * 
	 * @return list of possible moves
	 */
	public LinkedList<Byte> getPossibleMoves() {
		LinkedList<Byte> list = new LinkedList<Byte>();
		if ((emptyIndex % n) + 1 < n)
			list.add((byte) (emptyIndex + 1));
		if ((emptyIndex % n) - 1 >= 0)
			list.add((byte) (emptyIndex - 1));
		if (emptyIndex + n < N)
			list.add((byte) (emptyIndex + n));
		if (emptyIndex - n >= 0)
			list.add((byte) (emptyIndex - n));
		return list;
	}

	/**
	 * Makes a move (manipulation) on this sliding puzzle by indicating the new position of the empty element. We assume
	 * the new position is a legal postion and adjacent to the old position of empty (typically this method should be
	 * preceded with getPossibleMoves() method).
	 * 
	 * @param newEmptyIndex new index for the empty position.
	 */
	private void makeMove(byte newEmptyIndex) {
		board[emptyIndex] = board[newEmptyIndex];
		board[newEmptyIndex] = 0;
		emptyIndex = newEmptyIndex;
	}	
	
	/**
	 * Shuffles the sliding puzzle by a wanted number of random moves.
	 * 
	 * @param numberOfMoves wanted number of shuffling moves
	 */
	public void shuffle(int numberOfMoves) {
		Random randi = new Random();
		for (int i = 0; i < numberOfMoves; i++) {
			List<Byte> moves = getPossibleMoves();
			int index = randi.nextInt(moves.size());
			byte mov = moves.get(index);
			makeMove(mov);
		}
	}
		

	@Override
	public List<GraphState> generateChildren() {
		List<GraphState> list = new LinkedList<GraphState>();
		Iterator<Byte> it = getPossibleMoves().listIterator();
		while (it.hasNext()) {
			SlidingPuzzle child = new SlidingPuzzle(this);
			child.makeMove(it.next());
			String moveName = "D";
			if (this.emptyIndex - 1 == child.emptyIndex)
				moveName = "L";
			else if (this.emptyIndex + 1 == child.emptyIndex)
				moveName = "R";
			else if (this.emptyIndex - n == child.emptyIndex)
				moveName = "U";
			child.setMoveName(moveName);
			list.add(child);
		}
		return list;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(board);
	}

	@Override
	public boolean isSolution() {
		for (byte i = 0; i < N; i++)
			if (board[i] != i)
				return false;
		return true;
	}

	@Override
	public String toString() {		
		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder line = new StringBuilder();
		final int cellSize = 5;
		for (int i = 0; i < n * cellSize + 1; i++)
			line.append("-");

		stringBuilder.append(line).append("\n");
		int k = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				int boardAtK = board[k];
				stringBuilder.append(String.format("|%1$3d ", boardAtK));
				k++;
			}
			stringBuilder.append("|\n").append(line);
			if (i < n - 1)
				stringBuilder.append("\n");
		}

		return stringBuilder.toString();
	}

	@Override
	public String toGraphvizLabel() {
		StringBuilder builder = new StringBuilder();
		builder.append("<FONT FACE='monospace' POINT-SIZE='8'><TABLE BORDER='0' CELLBORDER='1' CELLSPACING='0'>");

		int k = 0;
		for (int i = 0; i < n; i++) {
			builder.append("<TR>");
			for (int j = 0; j < n; j++) {
				builder.append("<TD>" + ((board[k] == 0) ? "" : board[k]) + "</TD>");
				k++;
			}
			builder.append("</TR>");
		}
		builder.append("</TABLE></FONT>");

		return builder.toString();
	}

	static {
		setHFunction(new HFunctionLinearConflicts());
	}
}