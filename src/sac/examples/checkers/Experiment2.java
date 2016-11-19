package sac.examples.checkers;

import java.util.List;

import sac.game.AlphaBetaPruning;
import sac.game.GameSearchAlgorithm;
import sac.game.GameSearchConfigurator;
import sac.game.Scout;

/**
 * 'Computer vs computer' automatic experiment repeated for given number of trials. Both computer programs are set up by
 * the choice of algorithm and configurator.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class Experiment2 {
	public static void main(String[] args) throws Exception {

		int trials = 5;

		GameSearchConfigurator configurator1 = new GameSearchConfigurator();
		configurator1.setDepthLimit(1.5);
		configurator1.setQuiescenceOn(true);
		configurator1.setTranspositionTableOn(true);
		configurator1.setRefutationTableOn(true);
		
		GameSearchAlgorithm algorithm1 = new AlphaBetaPruning(null, configurator1);

		
		GameSearchConfigurator configurator2 = new GameSearchConfigurator();
		configurator2.setDepthLimit(2.5);
		configurator2.setQuiescenceOn(true);
		configurator2.setTranspositionTableOn(true);
		configurator2.setRefutationTableOn(true);
		
		GameSearchAlgorithm algorithm2 = new Scout(null, configurator2);

		int algorithm1Wins = 0;
		int algorithm2Wins = 0;		
		long algorithm1Time = 0;
		long algorithm2Time = 0;		
		int algorithm1States = 0;
		int algorithm2States = 0;

		GameSearchAlgorithm algorithmWhite = null;
		GameSearchAlgorithm algorithmBlack = null;

		
		long searchTime1 = 0, searchTime2 = 0;
		
		long t1 = System.currentTimeMillis();

		for (int t = 0; t < trials; t++) {
			for (int i = 0; i < 2; i++) {
				if (i == 0) {
					algorithmWhite = algorithm1;
					algorithmBlack = algorithm2;
				} else {
					algorithmWhite = algorithm2;
					algorithmBlack = algorithm1;
				}

				String initialAsString = "A1,C1,E1,G1,B2,D2,F2,H2,A3,C3,E3,G3;-;B6,D6,F6,H6,A7,C7,E7,G7,B8,D8,F8,H8;-;true";
				Checkers checkers = Checkers.stringToCheckersState(8, initialAsString);

				System.out.println("Game (" + (t + 1) + "," + i + "):");
				int moves = 0;
				while (true) {
					moves++;

					algorithmWhite.setInitial(checkers);
					searchTime1 = System.currentTimeMillis();
					algorithmWhite.execute();
					searchTime2 = System.currentTimeMillis();
					
					algorithm1Time += (searchTime2 - searchTime1);
					algorithm1States += algorithmWhite.getClosedStatesCount();
					
					List<String> bestMoves = (moves != 1) ? algorithmWhite.getBestMoves() : checkers.getPossibleMoves();
					int bestMoveIndex = (int) (Math.random() * bestMoves.size());
					String bestMove = bestMoves.get(bestMoveIndex);
					checkers.makeMove(bestMove, true);
					System.out.println(moves + ": " + bestMove);
					if ((checkers.isWinTerminal()) && (!checkers.isMaximizingTurnNow())) {
						if (i == 0) {
							System.out.println("Algorithm 1 - " + algorithm1.getClass().getName() + " wins.");
							algorithm1Wins++;
						} else {
							System.out.println("Algorithm 2 - " + algorithm2.getClass().getName() + " wins.");
							algorithm2Wins++;
						}
						break;
					}
					algorithmBlack.setInitial(checkers);					
					searchTime1 = System.currentTimeMillis();
					algorithmBlack.execute();
					searchTime2 = System.currentTimeMillis();
					
					algorithm2Time += (searchTime2 - searchTime1);
					algorithm2States += algorithmBlack.getClosedStatesCount();
					
					bestMoves = (moves != 1) ? algorithmBlack.getBestMoves() : checkers.getPossibleMoves();
					bestMoveIndex = (int) (Math.random() * bestMoves.size());
					bestMove = bestMoves.get(bestMoveIndex);
					checkers.makeMove(bestMove, true);
					System.out.println(moves + ": " + bestMove);
					if ((checkers.isWinTerminal()) && (checkers.isMaximizingTurnNow())) {
						if (i == 0) {
							System.out.println("Algorithm 2 - " + algorithm2.getClass().getName() + " wins.");
							algorithm2Wins++;
						} else {
							System.out.println("Algorithm 1 - " + algorithm1.getClass().getName() + " wins.");
							algorithm1Wins++;
						}
						break;
					}
					if (moves >= 100)
						break; // draw
				}
				System.out.println("-----------------------------------------------------------------------------");
			}
		}

		long t2 = System.currentTimeMillis();

		System.out.println("==================================================================================");
		System.out.println("Algorithm 1 - " + algorithm1.getClass().getName() + ", wins: " + algorithm1Wins 
				+ ", search time [s]: " + (algorithm1Time / 1000.0) + ", states: " + algorithm1States);
		System.out.println("Algorithm 2 - " + algorithm2.getClass().getName() + ", wins: " + algorithm2Wins 
				+ ", search time [s]: " + (algorithm2Time / 1000.0) + ", states: " + algorithm2States);
		System.out.println("Total time: " + (t2 - t1) + " ms.");
	}
}