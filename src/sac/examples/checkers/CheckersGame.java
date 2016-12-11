package sac.examples.checkers;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import sac.game.AlphaBetaPruning;
import sac.game.GameSearchAlgorithm;
import sac.game.GameSearchConfigurator;
import sac.game.GameState;
import sac.game.MinMax;
import sac.game.Scout;

/**
 * GUI for the game of checkers.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class CheckersGame {

	// general constants and variables
	private static final String[] LETTERS = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
	private static final String[] NUMBERS = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
	
	private final int n = 8;
	private final String initialAsString = "A1,C1,E1,G1,B2,D2,F2,H2,A3,C3,E3,G3;-;B6,D6,F6,H6,A7,C7,E7,G7,B8,D8,F8,H8;-;true";		
		
	//private final int n = 10;	
	//private final String initialAsString10 = "A1,C1,E1,G1,I1,B2,D2,F2,H2,J2,A3,C3,E3,G3,I3,B4,D4,F4,H4,J4;-;A7,C7,E7,G7,I7,B8,D8,F8,H8,J8,A9,C9,E9,G9,I9,B10,D10,F10,H10,J10;-;true";

	private final Map<String, GameSearchAlgorithm> algorithmsMapForWhite;
	private final Map<String, GameSearchAlgorithm> algorithmsMapForBlack;
	private final long delayBetweenMoves = 100;
	private final long delayInAlgorithmExecute = 25;
	private GameSearchConfigurator configuratorWhite;
	private GameSearchConfigurator configuratorBlack;

	// gui elements and constants
	private Checkers checkers = null;
	private Display display = null;
	private Shell shell = null;
	private GridLayout gridLayout = null;
	private int squareWidth = 40;
	private int squareHeight = 40;
	private Canvas canvas = null;
	private Combo comboPlayer1 = null;
	private Combo comboPlayer2 = null;
	private Button buttonStart = null;
	private Button buttonLogAnalysisCheckbox = null;
	private Text textConfigurator1 = null;
	private Text textConfigurator2 = null;	
	private StyledText log = null;
	private StyledText logAnalysis = null;	
	private GridData gridData1 = null;
	private GridData gridData2 = null;
	private GridData gridData3 = null;
	private GridData gridData4 = null;
	private GridData gridData5 = null;
	private GridData gridData6 = null;
	private GridData gridData7 = null;
	private GridData gridData8 = null;
	private Label labelPlayer1 = null;
	private Label labelConfigurator1 = null;
	private Label labelPlayer2 = null;
	private Label labelConfigurator2 = null;
	private Label labelLogAnalysis = null;
	private Label labelLog = null;

	private final Color BLACK_SQUARE_COLOR;
	private final Color WHITE_SQUARE_COLOR;
	private final Color BLACK_PAWN_COLOR;
	private final Color WHITE_PAWN_COLOR;

	private final int SHELL_WIDTH = 390;
	private final int SHELL_HEIGHT = 840;
	private final int LOG_HEIGHT = 280;
	private final int LOG_COMMUNICATION_HEIGHT = 220;
	private final int LOG_ANALYSIS_HEIGHT = LOG_HEIGHT - LOG_COMMUNICATION_HEIGHT;
	private final int LOG_ANALYSIS_FONT_SIZE = 7;

	// control variables
	private boolean startFlag = false;
	private String humanMove = "";
	private Thread gameThread = null;
	private boolean newGameToBeSetup = false;

	private CheckersGame() {
		configuratorWhite = new GameSearchConfigurator();		
		configuratorBlack = new GameSearchConfigurator();
		algorithmsMapForWhite = new TreeMap<String, GameSearchAlgorithm>();
		algorithmsMapForBlack = new TreeMap<String, GameSearchAlgorithm>();
		resetAlgorithms();

		checkers = Checkers.stringToCheckersState(n, initialAsString);
		display = new Display();
		BLACK_SQUARE_COLOR = display.getSystemColor(SWT.COLOR_GRAY);
		WHITE_SQUARE_COLOR = display.getSystemColor(SWT.COLOR_WHITE);
		BLACK_PAWN_COLOR = display.getSystemColor(SWT.COLOR_BLACK);
		WHITE_PAWN_COLOR = display.getSystemColor(SWT.COLOR_WHITE);
		shell = new Shell(display);
		shell.setText("SAC CHECKERS");

		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		shell.setLayout(gridLayout);

		shell.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				Rectangle rect = shell.getClientArea();
				squareWidth = rect.width / (n + 1);
				squareHeight = (rect.height - LOG_HEIGHT) / (n + 1);
			}
		});
	}

	private void resetAlgorithms() {
		algorithmsMapForWhite.clear();
		algorithmsMapForBlack.clear();
		algorithmsMapForWhite.put("alfa-beta pruning", new AlphaBetaPruning());
		algorithmsMapForWhite.put("min-max", new MinMax());
		algorithmsMapForWhite.put("scout", new Scout());
		algorithmsMapForWhite.put("human", null);
		algorithmsMapForBlack.put("alfa-beta pruning", new AlphaBetaPruning());
		algorithmsMapForBlack.put("min-max", new MinMax());
		algorithmsMapForBlack.put("scout", new Scout());
		algorithmsMapForBlack.put("human", null);
	}

	private void startGame(String algorithmWhiteAsString, String algorithmBlackAsString) {
		GameSearchAlgorithm algorithmWhite = algorithmsMapForWhite.get(algorithmWhiteAsString);
		GameSearchAlgorithm algorithmBlack = algorithmsMapForBlack.get(algorithmBlackAsString);
		if (algorithmBlack != null) {
			algorithmBlack.setConfigurator(configuratorBlack);
		}
		if (algorithmWhite != null) {
			algorithmWhite.setConfigurator(configuratorWhite);
		}

		int moves = 0;
		checkers = new Checkers(checkers.getWhitePawns(), checkers.getWhiteKings(), checkers.getBlackPawns(), checkers.getBlackKings(), true);
		GC gc = new GC(canvas);

		while (startFlag) {
			moves++;
			List<String> bestMoves;
			int bestMoveIndex;
			String bestMove;
			if (algorithmWhite == null) {
				shell.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
				shell.update();

				List<String> possibleMoves = checkers.getPossibleMoves();
				humanMove = "";
				info("POSSIBLE MOVES FOR WHITE: " + possibleMoves);
				while ((!possibleMoves.contains(humanMove)) && (startFlag)) {
					if (!display.readAndDispatch())
						display.sleep();
				}
				if (!startFlag)
					break;
				bestMove = humanMove;
				humanMove = "";
			} else {
				shell.setCursor(new Cursor(display, SWT.CURSOR_WAIT));
				log.setCursor(new Cursor(display, SWT.CURSOR_WAIT));
				buttonStart.setCursor(new Cursor(display, SWT.CURSOR_APPSTARTING));

				algorithmWhite.setInitial(checkers);
				infoAlgorithmStart(algorithmWhite);
				long t1 = System.currentTimeMillis();				
				AlgorithmExecuteRunnable executeRunnable = new AlgorithmExecuteRunnable(algorithmWhite);
				(new Thread(executeRunnable)).start();
				logAnalysis.setText("");
				while (executeRunnable.isRunning()) {
					if (buttonLogAnalysisCheckbox.getSelection()) infoAnalysis(algorithmWhite);
					display.readAndDispatch();
					if (!startFlag) {
						algorithmWhite.forceStop();
						break;
					}
					try {
						Thread.sleep(delayInAlgorithmExecute);
					} catch (InterruptedException e) {
					}
				}
				long t2 = System.currentTimeMillis();
				infoAlgorithmStop(algorithmWhite, t1, t2);
				bestMoves = algorithmWhite.getBestMoves();
				if ((bestMoves == null) || (bestMoves.isEmpty())) {
					// occurs when time limit has been reached and move score has been evaluated so
					// far
					bestMoves = checkers.getPossibleMoves();
				}
				bestMoveIndex = (int) (Math.random() * bestMoves.size());
				bestMove = bestMoves.get(bestMoveIndex);
				humanMove = bestMove;

				shell.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
				log.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
				buttonStart.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
			}

			if (!display.readAndDispatch())
				display.sleep();

			checkers.makeMove(bestMove, true);
			drawBoard(gc, checkers);
			info(moves + ". WHITE: " + bestMove);

			try {
				Thread.sleep(delayBetweenMoves);
			} catch (InterruptedException e) {
			}

			if ((checkers.isWinTerminal()) && (!checkers.isWhiteTurnNow())) {
				String whoWinsString = null;
				if (algorithmWhite == null) {
					whoWinsString = "HUMAN (WHITE) WINS";
				} else
					whoWinsString = "WHITE - ALGORITHM " + algorithmWhite.getClass().getName() + " - WINS.";
				info(whoWinsString);
				MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
				messageBox.setText("A win");
				messageBox.setMessage(whoWinsString);
				messageBox.open();
				break;
			}
			if (!startFlag)
				break;

			if (algorithmBlack == null) {
				shell.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
				shell.update();
				List<String> possibleMoves = checkers.getPossibleMoves();
				humanMove = "";
				info("POSSIBLE MOVES FOR BLACK: " + possibleMoves);
				while ((!possibleMoves.contains(humanMove)) && (startFlag)) {
					if (!display.readAndDispatch())
						display.sleep();
				}
				if (!startFlag)
					break;
				bestMove = humanMove;
				humanMove = "";
			} else {
				shell.setCursor(new Cursor(display, SWT.CURSOR_WAIT));
				log.setCursor(new Cursor(display, SWT.CURSOR_WAIT));
				buttonStart.setCursor(new Cursor(display, SWT.CURSOR_APPSTARTING));

				algorithmBlack.setInitial(checkers);
				infoAlgorithmStart(algorithmBlack);
				long t1 = System.currentTimeMillis();
				AlgorithmExecuteRunnable executeRunnable = new AlgorithmExecuteRunnable(algorithmBlack);
				(new Thread(executeRunnable)).start();
				logAnalysis.setText("");
				while (executeRunnable.isRunning()) {
					if (buttonLogAnalysisCheckbox.getSelection()) infoAnalysis(algorithmBlack);
					display.readAndDispatch();
					if (!startFlag) {
						algorithmBlack.forceStop();
						break;
					}
					try {
						Thread.sleep(delayInAlgorithmExecute);
					} catch (InterruptedException e) {
					}
				}
				long t2 = System.currentTimeMillis();
				infoAlgorithmStop(algorithmBlack, t1, t2);
				bestMoves = algorithmBlack.getBestMoves();
				if ((bestMoves == null) || (bestMoves.isEmpty())) {
					// occurs when time limit has been reached and move score has been evaluated so
					// far
					bestMoves = checkers.getPossibleMoves();
				}
				bestMoveIndex = (int) (Math.random() * bestMoves.size());
				bestMove = bestMoves.get(bestMoveIndex);
				humanMove = bestMove;

				shell.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
				log.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
				buttonStart.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
			}

			if (!display.readAndDispatch())
				display.sleep();

			checkers.makeMove(bestMove, true);
			drawBoard(gc, checkers);
			info(moves + ". BLACK: " + bestMove);

			try {
				Thread.sleep(delayBetweenMoves);
			} catch (InterruptedException e) {
			}

			if ((checkers.isWinTerminal()) && (checkers.isMaximizingTurnNow())) {
				String whoWinsString = null;
				if (algorithmBlack == null) {
					whoWinsString = "HUMAN (BLACK) WINS";
				} else
					whoWinsString = "BLACK - ALGORITHM " + algorithmBlack.getClass().getName() + " - WINS.";
				info(whoWinsString);
				MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
				messageBox.setText("A win");
				messageBox.setMessage(whoWinsString);
				messageBox.open();
				break;
			}
			int movesLimit = 100;
			if (moves >= movesLimit) {
				String whoWinsString = "DONE WERE " + movesLimit + " MOVES - SEEMS TO BE A DRAW...";
				info(whoWinsString);
				MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
				messageBox.setText("A draw");
				messageBox.setMessage(whoWinsString);
				messageBox.open();
				break; // draw
			}
		}
		shell.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
		shell.update();
		startFlag = false;
	}

	private void startApplication() {
		shell.setBounds(1, 1, SHELL_WIDTH, SHELL_HEIGHT);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = false;
		gridLayout.numColumns = 2;
		shell.setLayout(gridLayout);

		gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 1;
		gridData2 = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData2.horizontalSpan = 2;

		labelPlayer1 = new Label(shell, SWT.FLAT);
		labelPlayer1.setText("Black player:");
		labelConfigurator1 = new Label(shell, SWT.FLAT);
		labelConfigurator1.setText("Black configurator:");

		comboPlayer1 = new Combo(shell, SWT.READ_ONLY);
		comboPlayer1.setItems((String[]) algorithmsMapForBlack.keySet().toArray(new String[0]));
		comboPlayer1.setLayoutData(gridData1);
		textConfigurator1 = new Text(shell, SWT.BORDER);
		textConfigurator1.setLayoutData(gridData1);
		textConfigurator1.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				if (e.count == 1) {
					String[] filterExtensions = { "*.properties" };
					FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
					fileDialog.setText("FileDialog Demo");
					fileDialog.setFilterPath(".");
					fileDialog.setFilterExtensions(filterExtensions);
					String configuratorFilePath = fileDialog.open();
					try {
						if (configuratorFilePath != null) {
							textConfigurator1.setText(configuratorFilePath);
							configuratorBlack = new GameSearchConfigurator(configuratorFilePath);

							MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
							messageBox.setText("Black configurator read successfully");
							messageBox.setMessage("Settings:\n" + configuratorBlack.toString());
							messageBox.open();

						}
					} catch (Exception e1) {
						MessageBox messageBox = new MessageBox(shell, SWT.ERROR | SWT.ICON_ERROR);
						messageBox.setText("Configurator error");
						messageBox.setMessage("An error occurred while trying to read configurator from file.\nProceeding with a default one.");
						messageBox.open();
						configuratorBlack = new GameSearchConfigurator();
						textConfigurator1.setText("");
					}
				}
			}
		});

		canvas = new Canvas(shell, SWT.BORDER);
		canvas.setLayoutData(gridData2);

		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				drawBoard(event.gc, checkers);
			}
		});

		canvas.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				if (e.count == 1) {
					/*
					 * MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
					 * messageBox.setText("Debug"); messageBox.setMessage("x: " + e.x + ", y: " + e.y);
					 * messageBox.open();
					 */
					if (e.button > 1) {
						humanMove = "";
					} else {
						int i = (e.x - squareWidth / 2) / squareWidth;
						int j = n - 1 - (e.y - squareHeight / 2) / squareHeight;
						if ((0 <= i && i < n) && (0 <= j && j < n)) {
							if (humanMove.length() == 0) {
								humanMove = humanMove + LETTERS[i] + NUMBERS[j];
							} else {
								humanMove = humanMove + ":" + LETTERS[i] + NUMBERS[j];
							}
						}
					}
					drawBoard(new GC(canvas), checkers);
				}
			}
		});

		labelPlayer2 = new Label(shell, SWT.FLAT);
		labelPlayer2.setText("White player:");
		labelConfigurator2 = new Label(shell, SWT.FLAT);
		labelConfigurator2.setText("White configurator:");

		comboPlayer2 = new Combo(shell, SWT.READ_ONLY);
		comboPlayer2.setItems((String[]) algorithmsMapForWhite.keySet().toArray(new String[0]));
		comboPlayer2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		comboPlayer1.select(0);
		comboPlayer2.select(0);
		comboPlayer1.setEnabled(true);

		textConfigurator2 = new Text(shell, SWT.BORDER);
		textConfigurator2.setLayoutData(gridData1);
		textConfigurator2.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				if (e.count == 1) {
					String[] filterExtensions = { "*.properties" };
					FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
					fileDialog.setText("FileDialog Demo");
					fileDialog.setFilterPath(".");
					fileDialog.setFilterExtensions(filterExtensions);
					String configuratorFilePath = fileDialog.open();
					try {
						if (configuratorFilePath != null) {
							textConfigurator2.setText(configuratorFilePath);
							configuratorWhite = new GameSearchConfigurator(configuratorFilePath);

							MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
							messageBox.setText("White configurator read successfully");
							messageBox.setMessage("Settings:\n" + configuratorWhite.toString());
							messageBox.open();
						}
					} catch (Exception e1) {
						MessageBox messageBox = new MessageBox(shell, SWT.ERROR | SWT.ICON_ERROR);
						messageBox.setText("Configurator error");
						messageBox.setMessage("An error occurred while trying to read configurator from file.\nProceeding with a default one.");
						messageBox.open();
						configuratorWhite = new GameSearchConfigurator();
						textConfigurator2.setText("");
					}
				}
			}
		});

		buttonStart = new Button(shell, SWT.PUSH);
		buttonStart.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
		buttonStart.setText("START");
		gridData3 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData3.horizontalSpan = 2;
		buttonStart.setLayoutData(gridData3);
		buttonStart.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent event) {
				drawBoard(new GC(canvas), checkers);
				if (!startFlag) {
					((Button) event.widget).setText("NEW");
					comboPlayer1.setEnabled(false);
					comboPlayer2.setEnabled(false);
					textConfigurator1.setEnabled(false);
					textConfigurator2.setEnabled(false);
					log.setText("");
					infoHello();
					log.update();
					startFlag = true;
					gameThread = getGameThread(comboPlayer2.getText(), comboPlayer1.getText());
					gameThread.start();
				} else {
					resetAlgorithms();
					newGameToBeSetup = true;
					startFlag = false;
					gameThread = null;
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
					}
					((Button) event.widget).setText("START");
					comboPlayer1.setEnabled(true);
					comboPlayer2.setEnabled(true);
					textConfigurator1.setEnabled(true);
					textConfigurator2.setEnabled(true);
				}
			}
		});

		labelLogAnalysis = new Label(shell, SWT.FLAT);
		gridData4 = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		labelLogAnalysis.setLayoutData(gridData4);
		labelLogAnalysis.setText("Log of tree analysis (every " + delayInAlgorithmExecute + " ms):");
		
		buttonLogAnalysisCheckbox = new Button(shell, SWT.CHECK);
		gridData5 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		buttonLogAnalysisCheckbox.setLayoutData(gridData5);
		buttonLogAnalysisCheckbox.setText("on / off");
		buttonLogAnalysisCheckbox.setSelection(true);
		buttonLogAnalysisCheckbox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				logAnalysis.setText("");				
			}			
		});

		gridData6 = new GridData(GridData.FILL_HORIZONTAL);
		gridData6.horizontalSpan = 2;
		gridData6.heightHint = LOG_ANALYSIS_HEIGHT;
		logAnalysis = new StyledText(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
		logAnalysis.setEditable(false);
		logAnalysis.setLayoutData(gridData6);
		String fontName = display.getSystemFont().getFontData()[0].getName();
		Font font = new Font(shell.getDisplay(), fontName, LOG_ANALYSIS_FONT_SIZE, SWT.NORMAL);
		logAnalysis.setFont(font);	
		logAnalysis.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		logAnalysis.update();		

		
		labelLog = new Label(shell, SWT.FLAT);
		gridData7 = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gridData7.horizontalSpan = 2;
		labelLog.setLayoutData(gridData7);
		labelLog.setText("Log of game:");
		
		gridData8 = new GridData(GridData.FILL_HORIZONTAL);
		gridData8.horizontalSpan = 2;
		gridData8.heightHint = LOG_COMMUNICATION_HEIGHT;
		log = new StyledText(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
		log.setEditable(false);
		log.setLayoutData(gridData8);
		infoHello();
		log.update();
		
		// shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private class AlgorithmExecuteRunnable implements Runnable {
		private boolean running = false;
		private GameSearchAlgorithm algorithm = null;

		public AlgorithmExecuteRunnable(GameSearchAlgorithm algorithm) {
			this.algorithm = algorithm;
			running = true;
		}

		public boolean isRunning() {
			return running;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			algorithm.execute();
			running = false;
		}
	}

	private Thread getGameThread(final String algorithmWhite, final String algorithmBlack) {
		Thread thread = new Thread() {
			public void run() {
				// display async thread
				display.asyncExec(new Runnable() {
					public void run() {
						checkers = Checkers.stringToCheckersState(n, initialAsString);
						GC gc = new GC(canvas);
						humanMove = "";
						drawBoard(gc, checkers);

						startGame(algorithmWhite, algorithmBlack);

						if (newGameToBeSetup) {
							newGameToBeSetup = false;
							checkers = Checkers.stringToCheckersState(8, initialAsString);
							humanMove = "";
							drawBoard(gc, checkers);
							log.setText("");
							infoHello();
							log.update();
							shell.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
						} else {
							buttonStart.setText("START");
							comboPlayer1.setEnabled(true);
							comboPlayer2.setEnabled(true);
							textConfigurator1.setEnabled(true);
							textConfigurator2.setEnabled(true);
						}
					}
				});
			}
		};
		return thread;
	}

	private void info(String msg) {
		log.append(msg + "\n");
		log.setTopIndex(log.getLineCount() - 1);
		log.update();
	}

	private void infoHello() {
		info("SAC CHECKERS");
		info("(c)2013 by {pklesk,mkorzen}@wi.zut.edu.pl");
		info("");
		info("This game is a part of the \"Search and Conquer\" library,");
		info("developed under the TEWI project.");
		info("For details see: http://pklesk.github.io/sac");
		info("");
	}

	private void infoAlgorithmStart(GameSearchAlgorithm algorithm) {
		info("***");
		info("Searching with " + algorithm.getClass().getName() + " started...");
	}

	private void infoAlgorithmStop(GameSearchAlgorithm algorithm, long t1, long t2) {
		info("Searching with " + algorithm.getClass().getName() + " done in " + (t2 - t1) + " ms.");
		info("Closed states: " + algorithm.getClosedStatesCount());
		info("General depth limit: " + algorithm.getConfigurator().getDepthLimit());
		info("Maximum depth reached (Quiescence): " + algorithm.getDepthReached());
		info("Transposition table size: " + algorithm.getTranspositionTable().size());
		info("Transposition table uses: " + algorithm.getTranspositionTable().getUsesCount());
		info("Refutation table size: " + algorithm.getRefutationTable().size());
		info("Refutation table uses: " + algorithm.getRefutationTable().getUsesCount());
		info("Moves scores: " + algorithm.getMovesScores());		
		info("Best moves: " + algorithm.getBestMoves());
		info("Principal variation: " + algorithm.getInitial().getMovesAlongPrincipalVariation());
		info("***");
	}

	private void infoAnalysis(GameSearchAlgorithm algorithm) {
		GameState current = algorithm.getCurrent();
		if (current != null) {
			String msg = algorithm.getClosedStatesCount() + ": " + current.getMovesAlongPath().toString() + "=" + current.getH();
			logAnalysis.append(msg + "\n");
			logAnalysis.setTopIndex(logAnalysis.getLineCount() - 1);
			logAnalysis.update();
		}
	}
	
	private void drawPawn(GC gc, BoardLocation bl, Color fillColor, Color borderColor) {
		gc.setBackground(fillColor);
		int width = (int) Math.round(2.0 / 3.0 * squareWidth);
		int height = (int) Math.round(2.0 / 3.0 * squareHeight);
		int x = (int) Math.round(bl.getX() * squareWidth - 0.5 * width);
		int y = (int) Math.round((n - bl.getY() + 1) * squareHeight - 0.5 * height);
		gc.fillOval(x, y, width, height);
		gc.setForeground(borderColor);
		gc.drawOval(x, y, width, height);
	}

	private void drawKing(GC gc, BoardLocation bl, Color fillColor, Color borderColor) {
		gc.setBackground(fillColor);
		int width = (int) Math.round(9.0 / 10.0 * squareWidth);
		int height = (int) Math.round(9.0 / 10.0 * squareHeight);
		int x = (int) Math.round(bl.getX() * squareWidth - 0.5 * width);
		int y = (int) Math.round((n - bl.getY() + 1) * squareHeight - 0.5 * height);
		gc.fillOval(x - 1, y - 1, width, height);
		gc.setForeground(borderColor);
		gc.drawOval(x - 1, y - 1, width - 1, height - 1);
		
		FontMetrics fm = gc.getFontMetrics();
		int charWidth = fm.getAverageCharWidth();
		int charHeight = fm.getHeight();		
		gc.drawString("K", (int) (x + 0.5 * width - 0.5 * charWidth), (int) (y + 0.5 * height - 0.5 * charHeight));
	}

	private void drawMove(GC gc) {
		String[] list = humanMove.split(":");
		for (String move : list) {
			if (move.length() == 0) {
				return;
			}
			BoardLocation bl = BoardLocation.stringToLocation(move);
			int i = bl.getX();
			int j = bl.getY();
			int x = this.squareWidth / 2 + (i - 1) * this.squareWidth + 1;
			int y = this.squareHeight / 2 + (n - j) * this.squareHeight + 1;
			int width = this.squareWidth - 5;
			int height = this.squareHeight - 5;
			gc.setAntialias(SWT.ON);
			gc.setAlpha(100);
			gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
			gc.setForeground(display.getSystemColor(SWT.COLOR_DARK_RED));
			gc.fillRectangle(x, y, width, height);
			gc.drawRectangle(x, y, width, height);
		}

	}

	private void drawBoard(GC canvasGC, Checkers chekers) {
		
		Rectangle area = canvas.getClientArea();
		squareHeight = (int) Math.round(area.height / (n + 1));
		squareWidth = (int) Math.round(area.width / (n + 1));

		FontMetrics fm = canvasGC.getFontMetrics();
		int charWidth = fm.getAverageCharWidth();
		int charHeight = fm.getHeight();

		Image im = new Image(display, area.width, area.height);
		GC gc = new GC(im);
		gc.setAntialias(SWT.ON);
		gc.setAlpha(255);
		int x, y, marg;
		marg = 4;
		gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		x = (int) Math.round(0.5 * squareWidth - marg) + 1;
		y = (int) Math.round(0.5 * squareHeight - marg) + 1;
		int width = n * squareWidth + marg + 1;
		int height = n * squareHeight + marg + 1;
		gc.fillRectangle(x, y, width, height);
		gc.drawRectangle(x, y, width, height);
		width = squareWidth;
		height = squareHeight;
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.n; j++) {
				if ((i + j) % 2 == 1)
					gc.setBackground(BLACK_SQUARE_COLOR);
				else
					gc.setBackground(WHITE_SQUARE_COLOR);
				x = (int) Math.round(0.5 * squareWidth + i * squareWidth);
				y = (int) Math.round(0.5 * squareHeight + j * squareHeight);

				gc.fillRectangle(x, y, width, height);
			}
			marg = 4;

			gc.drawString(LETTERS[i], (int) Math.round((i + 1) * squareWidth - 0.5 * charWidth), (int) Math.round(squareHeight * (n + 0.5) + 0.5 * marg), true);
			gc.drawString(NUMBERS[i], (int) Math.round(0.5 * squareWidth - marg - 2 * charWidth), (int) Math.round((n - i) * squareHeight - 0.5 * charHeight),
					true);
		}

		for (BoardLocation bl : checkers.getWhitePawns())
			drawPawn(gc, bl, WHITE_PAWN_COLOR, BLACK_PAWN_COLOR);
		for (BoardLocation bl : checkers.getWhiteKings())
			drawKing(gc, bl, WHITE_PAWN_COLOR, BLACK_PAWN_COLOR);
		for (BoardLocation bl : checkers.getBlackPawns())
			drawPawn(gc, bl, BLACK_PAWN_COLOR, WHITE_PAWN_COLOR);
		for (BoardLocation bl : checkers.getBlackKings())
			drawKing(gc, bl, BLACK_PAWN_COLOR, WHITE_PAWN_COLOR);

		drawMove(gc);
		canvasGC.drawImage(im, 0, 0);
	}
	
	public static void main(String[] args) {
		System.out.println("SAC CHECKERS");
		System.out.println("(c)2013 by {pklesk,mkorzen}@wi.zut.edu.pl");
		CheckersGame checkersGame = new CheckersGame();
		checkersGame.startApplication();
	}
}