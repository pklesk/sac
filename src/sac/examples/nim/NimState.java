package sac.examples.nim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sac.State;
import sac.StateFunction;
import sac.game.GameState;
import sac.game.GameStateImpl;

/**
 * Nim game, for details @see: http://en.wikipedia.org/wiki/Nim.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class NimState extends GameStateImpl {
	
	private List<Integer> piles;

	/**
	 * Creates a new instance of Nim state. By default, it creates three piles with 3, 4, 5 elements.
	 */
	public NimState() {
		piles = new LinkedList<Integer>();
		piles.add(3);
		piles.add(4);
		piles.add(5);
		setMaximizingTurnNow(true);
	}

	/**
	 * Creates a new instance of Nim state using given settings. 
	 * 
	 * @param initial list with sizes of successive piles. 
	 * @param isWhiteTurnNow boolean flag indicating whether it is white turn to play now
	 */
	public NimState(List<Integer> initial, boolean isWhiteTurnNow) {
		piles = new ArrayList<Integer>(initial);
		Collections.copy(piles, initial);
		setMaximizingTurnNow(isWhiteTurnNow);
	}

	/**
	 * Creates a new instance of Nim state as a copy of given parent.
	 * 
	 * @param parent state to copy from
	 */
	public NimState(NimState parent) {
		super();
		piles = new ArrayList<Integer>();
		for (int item : parent.getPiles()) {
			piles.add(item);
		}
		setMaximizingTurnNow(parent.isMaximizingTurnNow());
	}

	/**
	 * Creates a new instance of Nim state as a copy of given parent and sets the turn-flag as specified.
	 * 
	 * @param parent state to copy from
	 * @param isWhiteTurnNow boolean flag indicating whether it is white turn to play now
	 */
	public NimState(NimState parent, boolean isWhiteTurnNow) {
		super();
		piles = new ArrayList<Integer>();
		for (int item : parent.getPiles()) {
			piles.add(item);
		}
		setMaximizingTurnNow(isWhiteTurnNow);
	}

	/**
	 * Returns the piles.
	 * 
	 * @return piles
	 */
	public List<Integer> getPiles() {
		return piles;
	}
	
	/**
	 * Returns the boolean stating if it is whites turn now.
	 * 
	 * @return boolean stating if it is whites turn now
	 */
	public boolean isWhiteTurnNow() {
		return isMaximizingTurnNow();
	}

	/**
	 * Generate possible moves for a given state.
	 * 
	 * @return list of possible moves
	 */
	public List<List<Integer>> getPossibleMoves() {
		List<List<Integer>> list = new ArrayList<List<Integer>>();
		int qi;
		for (int i = 0; i < piles.size(); i++) {
			qi = piles.get(i);
			for (int j = 1; j <= qi; j++) {
				ArrayList<Integer> move = new ArrayList<Integer>(
						Collections.nCopies(piles.size() - 1, 0));
				move.add(i, j);
				list.add(move);
			}
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		String string = toString();
		return string.hashCode();
	}

	/**
	 * Makes move from this state. E.g.: [0, 2, 0] means that we take two coins
	 * from the second pile. Only one non-zero element is allowed.
	 * 
	 * @param move a list of integers representing the move in the following form: [int1, int2, ..., intn].
	 */
	public void makeMove(List<Integer> move) {
		assert (checkMove(move));
		int newValue;
		for (int i = 0; i < piles.size(); i++) {
			newValue = piles.get(i) - move.get(i);			
			piles.remove(i);
			piles.add(i, newValue);
		}		
		setMaximizingTurnNow(!maximizingTurnNow);
		refresh();
	}

	/**
	 * Makes move from this state. E.g. [0, 2, 0] means that we take two coins
	 * from the second pile. Only one non-zero element is allowed.
	 * 
	 * @param stringMove string representing the move in the following form: [int1, int2, ..., intn].
	 */
	public void makeMove(String stringMove) {
		String[] si = stringMove.substring(1, stringMove.length() - 1).split(
				",");
		List<Integer> move = new LinkedList<Integer>();
		for (String s : si) {
			move.add(Integer.parseInt(s.trim()));
		}
		makeMove(move);
	}

	public List<Integer> parseMove(String stringMove) {
		List<Integer> move = new LinkedList<Integer>();
		if (stringMove.length()==0) {
			for (int i=0; i < piles.size(); i++) {
				move.add(0);
			}
		} else {
			String[] si = stringMove.substring(1, stringMove.length() - 1).split(",");
			for (String s : si) {
				move.add(Integer.parseInt(s.trim()));
			}
		}
		return move;
	}

	/**
	 * It checks whether move is legal.
	 * 
	 * @param move a list of integers representing the move in the following form: [int1, int2, ..., intn].
	 * @return true if move is legal, and false otherwise
	 */
	public boolean checkMove(List<Integer> move) {
		if (piles.size() != move.size()) {
			return false;
		}
		for (int i = 0; i < piles.size(); i++) {
			if (!((move.get(i) <= piles.get(i)) && (move.get(i) > 0))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public List<GameState> generateChildren() {
		List<GameState> children = new ArrayList<GameState>();
		for (List<Integer> move : getPossibleMoves()) {
			NimState child = new NimState(this);
			child.makeMove(move);
			child.setMoveName(move.toString());
			children.add(child);
		}
		return children;
	}

	public String toString() {
		return piles.toString() + "|" + maximizingTurnNow;
	}

	/**
	 * Returns 1.0 when this state is a winning state, and 0.0 otherwise.
	 * 
	 * @return zero or one.
	 */
	public double valueOfState() {
		return (NimNumbers.sum(piles) == 0) ? 1.0 : 0.0;
	}

	/**
	 * Checks whether the state is terminal.
	 * 
	 * @return True for terminal state, false otherwise.
	 */
	public boolean isTerminal() {
		for (Integer item : piles) {
			if (item > 0)
				return false;
		}
		return true;
	}
	
	static {
		setHFunction(new StateFunction() {
			@Override
			public double calculate(State state) {
				NimState nimState = (NimState) state;
				if (nimState.isTerminal()) 
					return Double.POSITIVE_INFINITY * (nimState.isMaximizingTurnNow() ? -1 : 1);					 
				return 0.0;
			}
		});
	}
}