package sac.examples.checkers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import sac.game.AlphaBetaPruning;
import sac.game.GameSearchAlgorithm;
import sac.game.GameSearchConfigurator;
import sac.game.Scout;

/**
 * Console checkers interface (human vs computer). Results for computer are derived by several algorithms given as an
 * array (and their configurators) and displayed to the console, but moves are selected only on the basis of the second
 * algorithm.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class Experiment1 {
	public static void main(String[] args) throws Exception {
		final boolean humanStarts = true;
		
		final String initialAsString = "A1,C1,E1,G1,B2,D2,F2,H2,A3,C3,E3,G3;-;B6,D6,F6,H6,A7,C7,E7,G7,B8,D8,F8,H8;-;true";
				
		Checkers checkers = Checkers.stringToCheckersState(8, initialAsString);
		
		System.out.println(checkers.toHumanString());

		BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
		String playerMove = "";
		
		if (humanStarts) {
			System.out.println("POSSIBLE MOVES: " + checkers.getPossibleMoves());
			System.out.println("YOUR MOVE: ");
			playerMove = inReader.readLine();
			checkers.makeMove(playerMove, true);
			System.out.println("-----------------------------------------------------------------------");
			System.out.println(checkers.toHumanString());
		}
		
		GameSearchConfigurator configurator1 = new GameSearchConfigurator();
		configurator1.setDepthLimit(1.5);

		GameSearchConfigurator configurator2 = new GameSearchConfigurator();
		configurator2.setDepthLimit(1.0);

		GameSearchAlgorithm[] algorithms = { new AlphaBetaPruning(checkers, configurator1), new Scout(checkers, configurator2) };							
		
		while (true) {
			String bestMoveName = null;
			for (GameSearchAlgorithm algorithm : algorithms) {
				System.out.println("Searching with " + algorithm.getClass().getName() + "...");

				long t1 = System.currentTimeMillis();
				algorithm.execute();
				long t2 = System.currentTimeMillis();					
				
				System.out.println("Searching done. Time: " + (t2 - t1) + " ms.");
				System.out.println("Closed states: " + algorithm.getClosedStatesCount());
				System.out.println("General depth limit: " + algorithm.getConfigurator().getDepthLimit());
				System.out.println("Maximum depth reached (quiescence): " + algorithm.getDepthReached());
				System.out.println("Transposition table size: " + algorithm.getTranspositionTable().size());
				System.out.println("Transposition table uses: " + algorithm.getTranspositionTable().getUsesCount());
				System.out.println("Refutation table size: " + algorithm.getRefutationTable().size());
				System.out.println("Refutation table uses: " + ((algorithm.getRefutationTable() == null) ? 0 : algorithm.getRefutationTable().getUsesCount()));

				System.out.println("Scores: " + algorithm.getMovesScores());
				System.out.println("Best move: " + algorithm.getFirstBestMove());
				System.out.println("Principal variation: " + algorithm.getInitial().getMovesAlongPrincipalVariation());
				bestMoveName = algorithm.getFirstBestMove();
				
				System.out.println("##########");
			}

			if (bestMoveName == null) {
				List<String> moves = checkers.getPossibleMoves();
				int randomIndex = (int) (Math.random() * moves.size());
				bestMoveName = moves.get(randomIndex);
			}

			checkers.makeMove(bestMoveName, true);
			System.out.println("-----------------------------------------------------------------------");
			System.out.println(checkers.toHumanString());

			if ((checkers.getH() == Double.POSITIVE_INFINITY) || (checkers.getH() == Double.NEGATIVE_INFINITY) || (checkers.getPossibleMoves().isEmpty()))
				break;

			System.out.println("h: " + checkers.getH());
			System.out.println("POSSIBLE MOVES: " + checkers.getPossibleMoves());
			System.out.println("YOUR MOVE: ");
			inReader = new BufferedReader(new InputStreamReader(System.in));
			playerMove = inReader.readLine();
			checkers.makeMove(playerMove, true);
			System.out.println("-----------------------------------------------------------------------");
			System.out.println(checkers.toHumanString());

			if ((checkers.getH() == Double.POSITIVE_INFINITY) || (checkers.getH() == Double.NEGATIVE_INFINITY) || (checkers.getPossibleMoves().isEmpty()))
				break;
		}
		System.out.println("GAME OVER.");
	}
}