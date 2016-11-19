package sac.examples.helloworldgame;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import sac.State;
import sac.StateFunction;
import sac.game.GameSearchAlgorithm;
import sac.game.GameState;
import sac.game.GameStateImpl;
import sac.game.MinMax;

/**
 * 'Hello World' example for a game. Given n stones, players interchangeably make theirs moves by taking either one or
 * two stones. The player left with the last stone loses.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class HelloWorldGameState extends GameStateImpl {

	/**
	 * Number of stones.
	 */
	private int n;

	/**
	 * Creates new instance of the HelloWorldGameState state.
	 * 
	 * @param n number of stones.
	 */
	public HelloWorldGameState(int n) {
		this.n = n;
	}

	@Override
	public List<GameState> generateChildren() {
		List<GameState> children = new LinkedList<GameState>();
		for (int i = 1; i <= 2 && i < n; i++) {
			HelloWorldGameState child = new HelloWorldGameState(n - i);
			child.setMoveName(Integer.toString(i));
			child.setMaximizingTurnNow(!isMaximizingTurnNow());
			children.add(child);
		}
		return children;
	}

	@Override
	public int hashCode() {
		int[] pair = { n, (isMaximizingTurnNow() ? 1 : -1) };
		return Arrays.hashCode(pair);
	}

	static {
		setHFunction(new StateFunction() {
			@Override
			public double calculate(State state) {
				HelloWorldGameState hwgs = (HelloWorldGameState) state;
				if (hwgs.n == 1)
					return (hwgs.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
				return 0.0;
			}
		});
	}

	@Override
	public String toGraphvizLabel() {
		return "<FONT FACE='monospace' POINT-SIZE='8'>" + "n = " + n + ", " + (isMaximizingTurnNow() ? "max" : "min") + "</FONT>";
	}

	@Override
	public String toString() {
		return "depth=" + depth + ",n=" + n + "," + (isMaximizingTurnNow() ? "max" : "min");
	}

	public static void main(String[] args) throws Exception {
		GameSearchAlgorithm algorithm = new MinMax(new HelloWorldGameState(6));
		algorithm.execute();
		System.out.println("MOVES SCORES: " + algorithm.getMovesScores());
	}
}