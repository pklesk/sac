package sac.examples.nim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
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
import sac.game.MinMax;
import sac.game.Scout;

/**
 * GUI for the game of Nim.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class NimGame {

	// initial state and board parameters
	private List<Integer> initState;
	private int max_pile_count;
	private int number_of_piles;
	private int coinWidth;
	private int coinHeight;
	private int player = 0;
	private final Map<String, GameSearchAlgorithm> algorithmsMapForWhite;
	private final Map<String, GameSearchAlgorithm> algorithmsMapForBlack;
	private final long delayBetweenMoves = 500;
	private final long delayInAlgorithmExecute = 200;

	private GameSearchConfigurator configuratorWhite;
	private GameSearchConfigurator configuratorBlack;

	// gui elements and constants
	private NimState nim = null;
	private Display display = null;
	private Shell shell = null;
	private GridLayout gridLayout = null;
	private Canvas canvas = null;
	private Combo comboPlayer1 = null;
	private Combo comboPlayer2 = null;
	private Button buttonStart = null;
	private Text textConfigurator1 = null;
	private Text textConfigurator2 = null;
	private StyledText log = null;
	private GridData gridData1 = null;
	private GridData gridData2 = null;
	private GridData gridData3 = null;
	private GridData gridData4 = null;
	private Label labelPlayer1 = null;
	private Label labelConfigurator1 = null;
	private Label labelPlayer2 = null;
	private Label labelConfigurator2 = null;

	private final int SHELL_WIDTH = 400;
	private final int SHELL_HEIGHT = 840;
	private final int LOG_HEIGHT = 310;

	// control variables
	private boolean startFlag = false;	
	List<Integer> move = new ArrayList<Integer>(); // move[0] = number of pile,
													// mode[j] number of
													// selected coin in pile
													// mode[0]

	int[] listMove;
	private String humanMove = "";
	private Thread gameThread = null;
	private boolean newGameToBeSetup = false;

	private NimGame(List<Integer> initState) {

		configuratorWhite = new GameSearchConfigurator();
		configuratorBlack = new GameSearchConfigurator();
		algorithmsMapForWhite = new TreeMap<String, GameSearchAlgorithm>();
		algorithmsMapForBlack = new TreeMap<String, GameSearchAlgorithm>();
		resetAlgorithms();

		display = new Display();
		
		this.initState = initState;
		nim = new NimState(initState, true);
		max_pile_count = Collections.max(initState);
		number_of_piles = initState.size();
		coinWidth = SHELL_WIDTH / (number_of_piles + 1);
		coinHeight = SHELL_HEIGHT / (max_pile_count + 1);

		shell = new Shell(display);
		shell.setText("SAC NIM GAME");

		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		shell.setLayout(gridLayout);
		listMove = new int[number_of_piles];
	}

	private List<Integer> parseMove(String humanMove) {
		List<Integer> list = nim.parseMove(humanMove);
		move = new ArrayList<Integer>();
		int i = 0;
		for (int a : list) {
			if (a > 0) {
				move.add(i);
				for (int j = 0; j < a; j++) {
					move.add(nim.getPiles().get(i) - j - 1);
				}
			}
			i++;
		}
		return move;
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
		GameSearchAlgorithm algorithmWhite = algorithmsMapForWhite
				.get(algorithmWhiteAsString);
		GameSearchAlgorithm algorithmBlack = algorithmsMapForBlack
				.get(algorithmBlackAsString);
		if (algorithmBlack != null) {
			algorithmBlack.setConfigurator(configuratorBlack);
		}
		if (algorithmWhite != null) {
			algorithmWhite.setConfigurator(configuratorWhite);
		}

		int moves = 0;
		nim = new NimState(initState, true);
		GC gc = new GC(canvas);

		while (startFlag) {
			moves++;
			List<String> bestMoves;
			int bestMoveIndex;
			String bestMove;
			player = 1;
			if (algorithmWhite == null) {
				shell.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
				shell.update();
				
				List<List<Integer>> possibleMoves = nim.getPossibleMoves();
				List<String> possibleMovesSting = new ArrayList<String>();
				for (List<Integer> item : possibleMoves) {
					possibleMovesSting.add(item.toString());
				}
				humanMove = "";
				move = new ArrayList<Integer>();	
				listMove = new int[number_of_piles];
				drawBoard(gc, nim);
				move = new ArrayList<Integer>();
				info("POSSIBLE MOVES FOR WHITE: " + possibleMoves);
				while ((!possibleMovesSting.contains(humanMove)) && (startFlag)) {
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
				buttonStart.setCursor(new Cursor(display,
						SWT.CURSOR_APPSTARTING));

				algorithmWhite.setInitial(nim);
				infoAlgorithmStart(algorithmWhite);
				long t1 = System.currentTimeMillis();
				AlgorithmExecuteRunnable executeRunnable = new AlgorithmExecuteRunnable(
						algorithmWhite);
				(new Thread(executeRunnable)).start();
				while (executeRunnable.isRunning()) {
					if (!display.readAndDispatch())
						display.sleep();
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
					// occurs when time limit has been reached and move score
					// has been evaluated so
					// far
					List<List<Integer>> bestMovesList = nim.getPossibleMoves();
					bestMoves = new LinkedList<String>();
					for (List<Integer> move : bestMovesList) {
						bestMoves.add(move.toString());
					}
				}
				bestMoveIndex = (int) (Math.random() * bestMoves.size());
				bestMove = bestMoves.get(bestMoveIndex).toString();
				humanMove = bestMove;
				move = parseMove(humanMove);
				shell.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
				log.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
				buttonStart.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
			}

			if (!display.readAndDispatch())
				display.sleep();

			nim.makeMove(bestMove);
			drawBoard(gc, nim);
			info(moves + ". WHITE: " + bestMove);
			player = 2;
			try {
				Thread.sleep(delayBetweenMoves);
			} catch (InterruptedException e) {
			}
			if ((nim.isWinTerminal()) && (!nim.isMaximizingTurnNow())) {
				String whoWinsString = null;
				if (algorithmWhite == null) {
					whoWinsString = "HUMAN (WHITE) WINS";
				} else
					whoWinsString = "WHITE - ALGORITHM "
							+ algorithmWhite.getClass().getName() + " - WINS.";
				info(whoWinsString);
				MessageBox messageBox = new MessageBox(shell, SWT.OK
						| SWT.ICON_INFORMATION);
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
				List<List<Integer>> possibleMoves = nim.getPossibleMoves();
				humanMove = "";
				move = new ArrayList<Integer>();	
				listMove = new int[number_of_piles];
				drawBoard(gc, nim);
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
				buttonStart.setCursor(new Cursor(display,
						SWT.CURSOR_APPSTARTING));

				algorithmBlack.setInitial(nim);
				infoAlgorithmStart(algorithmBlack);
				long t1 = System.currentTimeMillis();
				AlgorithmExecuteRunnable executeRunnable = new AlgorithmExecuteRunnable(
						algorithmBlack);
				(new Thread(executeRunnable)).start();
				while (executeRunnable.isRunning()) {
					if (!display.readAndDispatch())
						display.sleep();
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
					// occurs when time limit has been reached and move score
					// has been evaluated so
					// far
					List<List<Integer>> bestMovesList = nim.getPossibleMoves();
					bestMoves = new LinkedList<String>();
					for (List<Integer> move : bestMovesList) {
						bestMoves.add(move.toString());
					}
				}
				bestMoveIndex = (int) (Math.random() * bestMoves.size());
				bestMove = bestMoves.get(bestMoveIndex);
				humanMove = bestMove;
				move = parseMove(humanMove);
				shell.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
				log.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
				buttonStart.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
			}

			if (!display.readAndDispatch())
				display.sleep();

			nim.makeMove(bestMove);
			drawBoard(gc, nim);
			info(moves + ". BLACK: " + bestMove);

			try {
				Thread.sleep(delayBetweenMoves);
			} catch (InterruptedException e) {
			}
			
			if ((nim.isWinTerminal()) && (nim.isMaximizingTurnNow())) {
				String whoWinsString = null;
				if (algorithmBlack == null) {
					whoWinsString = "HUMAN (BLACK) WINS";
				} else
					whoWinsString = "BLACK - ALGORITHM "
							+ algorithmBlack.getClass().getName() + " - WINS.";
				info(whoWinsString);
				MessageBox messageBox = new MessageBox(shell, SWT.OK
						| SWT.ICON_INFORMATION);
				messageBox.setText("A win");
				messageBox.setMessage(whoWinsString);
				messageBox.open();
				break;
			}
			int movesLimit = 100;
			if (moves >= movesLimit) {
				String whoWinsString = "MORE THAN " + movesLimit
						+ " DONE - SEEMS TO BE A DRAW...";
				info(whoWinsString);
				MessageBox messageBox = new MessageBox(shell, SWT.OK
						| SWT.ICON_INFORMATION);
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
							messageBox.setMessage("Settings:\n"	+ configuratorBlack.toString());
							messageBox.open();
						}
					} catch (Exception e1) {
						MessageBox messageBox = new MessageBox(shell, SWT.ERROR
								| SWT.ICON_ERROR);
						messageBox.setText("Configurator error");
						messageBox
								.setMessage("An error occurred while trying to read configurator from file.\nProceeding with a default one.");
						messageBox.open();
						configuratorBlack = new GameSearchConfigurator();
						textConfigurator1.setText("");
					}
				}
			}
		});

		canvas = new Canvas(shell, SWT.BORDER);
		canvas.setLayoutData(gridData2);
		canvas.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				Rectangle rect;
				rect = canvas.getClientArea();					
				coinWidth = rect.width / (number_of_piles + 1);
				coinHeight = rect.height / (max_pile_count + 1);
			}
		});
		
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				drawBoard(event.gc, nim);
			}
		});

		canvas.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				//System.out.println(e);
				if (e.count == 1) {
					/*
					 * MessageBox messageBox = new MessageBox(shell, SWT.OK |
					 * SWT.ICON_INFORMATION); messageBox.setText("Debug");
					 * messageBox.setMessage("x: " + e.x + ", y: " + e.y);
					 * messageBox.open();
					 */
					if (e.button > 1) {
						humanMove = Arrays.toString(listMove);
						move = new ArrayList<Integer>();	
						listMove = new int[number_of_piles];
					} else {
						int w = coinWidth;
						int h = coinHeight;
						int i = (e.x + w / 2) / w - 1;
						int j = max_pile_count - (e.y + h / 2) / h;
						if (i >= 0 && i < number_of_piles && j >= 0
								&& j < nim.getPiles().get(i)) {
							if (move == null || move.size() == 0) {
								move.add(i);
								move.add(j);
								listMove[move.get(0)] = 1;
							} else if (move.get(0) == i) {
								if (move.subList(1, move.size()).contains(j)) {
									for (int k = 1; k < move.size(); k++) {
										if (move.get(k) == j) {
											move.remove(k);
											break;
										}
									}
								} else {
									move.add(j);
								}
								if (i == move.get(0)) {
									listMove[move.get(0)] = move.size() - 1;
								}
							}
						}
					}
					
				} else if (e.count==2) {
					move = new ArrayList<Integer>();	
					listMove = new int[number_of_piles];
				}
				drawBoard(new GC(canvas), nim);
			}
		});

		labelPlayer2 = new Label(shell, SWT.FLAT);
		labelPlayer2.setText("White player:");
		labelConfigurator2 = new Label(shell, SWT.FLAT);
		labelConfigurator2.setText("White configurator:");

		comboPlayer2 = new Combo(shell, SWT.READ_ONLY);
		comboPlayer2.setItems((String[]) algorithmsMapForWhite.keySet()
				.toArray(new String[0]));
		comboPlayer2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
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
							configuratorWhite = new GameSearchConfigurator(
									configuratorFilePath);
							MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
							messageBox.setText("White configurator read successfully");
							messageBox.setMessage("Settings:\n" + configuratorWhite.toString());
							messageBox.open();
						}
					} catch (Exception e1) {
						MessageBox messageBox = new MessageBox(shell, SWT.ERROR
								| SWT.ICON_ERROR);
						messageBox.setText("Configurator error");
						messageBox
								.setMessage("An error occurred while trying to read configurator from file.\nProceeding with a default one.");
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
		gridData3 = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gridData3.horizontalSpan = 2;
		buttonStart.setLayoutData(gridData3);
		buttonStart.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent event) {
				drawBoard(new GC(canvas), nim);
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
					gameThread = getGameThread(comboPlayer2.getText(),
							comboPlayer1.getText());
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

		gridData4 = new GridData(GridData.FILL_HORIZONTAL);
		gridData4.horizontalSpan = 2;
		gridData4.heightHint = LOG_HEIGHT;
		log = new StyledText(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.WRAP);
		log.setEditable(false);
		log.setLayoutData(gridData4);
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

	private Thread getGameThread(final String algorithmWhite,
			final String algorithmBlack) {
		Thread thread = new Thread() {
			public void run() {
				// display async thread
				display.asyncExec(new Runnable() {
					public void run() {
						nim = new NimState(initState, true);
						GC gc = new GC(canvas);
						humanMove = "";
						move = new ArrayList<Integer>();	
						listMove = new int[number_of_piles];
						drawBoard(gc, nim);

						startGame(algorithmWhite, algorithmBlack);
						if (newGameToBeSetup) {
							newGameToBeSetup = false;
							nim = new NimState(initState, true);
							humanMove = "";
							move = new ArrayList<Integer>();	
							listMove = new int[number_of_piles];
							drawBoard(gc, nim);
							log.setText("");
							infoHello();
							log.update();
							shell.setCursor(new Cursor(display,
									SWT.CURSOR_ARROW));
						} else {
							buttonStart.setText("START");
							comboPlayer1.setEnabled(true);
							comboPlayer2.setEnabled(true);
							textConfigurator1.setEnabled(true);
							textConfigurator2.setEnabled(true);
							drawBoard(gc, nim);
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
		info("SAC NIM GAME");
		info("(c)2013 by {pklesk,mkorzen}@wi.zut.edu.pl");
		info("");
		info("This game is a part of the \"Search and Conquer\" library,");
		info("developed under the TEWI project.");
		info("For details see: http://www.wi.pb.edu.pl/index.php/projekty-ue/tewi");
		info("");
		info("Rules:");
		info("  1) You can take any number of coins from a chosen pile,");
		info("  2) Player who takes last coins -- wins.");
		info("");
		info("Usage:");
		info("  LEFT BUTTON selects the pile and coins");
		info("  RIGHT BUTTON accepts the choice");
		info("");
	}

	private void infoAlgorithmStart(GameSearchAlgorithm algorithm) {
		info("***");
		info("Searching with " + algorithm.getClass().getName() + " started...");
	}

	private void infoAlgorithmStop(GameSearchAlgorithm algorithm, long t1,
			long t2) {
		info("Searching with " + algorithm.getClass().getName() + " done in "
				+ (t2 - t1) + " ms.");
		info("Closed states: " + algorithm.getClosedStatesCount());
		info("General depth limit: " + algorithm.getConfigurator().getDepthLimit());
		info("Maximum depth reached (Quiescence): " + algorithm.getDepthReached());
		info("Transposition table size: " + algorithm.getTranspositionTable().size());
		info("Transposition table uses: " + algorithm.getTranspositionTable().getUsesCount());
		info("Refutation table size: " + algorithm.getRefutationTable().size());
		info("Refutation table uses: " + algorithm.getRefutationTable().getUsesCount());
		info("Moves scores: " + algorithm.getMovesScores());
		info("Best moves: " + algorithm.getBestMoves());
		info("***");
	}

	private void drawMove(GC gc) {
		if (!startFlag) {
			return;
		}
		if (move != null && move.size() > 0) {
			int i = move.get(0);
			int x = this.coinWidth / 2 + (i) * this.coinWidth;
			for (int j = 1; j < move.size(); j++) {
				int y = this.coinHeight / 2 + (max_pile_count - move.get(j) - 1) * this.coinHeight;
				int width = this.coinWidth - 5;
				int height = this.coinHeight - 5;
				gc.setAntialias(SWT.ON);
				if (player==2) {
					gc.setAlpha(200);					
					gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
					gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
				} else {
					gc.setAlpha(200);
					gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
					gc.setForeground(display.getSystemColor(SWT.COLOR_DARK_YELLOW));
				}
				gc.fillRectangle(x, y, width, height);
				gc.drawRectangle(x, y, width, height);
			}
			gc.setAlpha(200);					
			gc.setForeground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
			gc.drawRectangle(x-2, this.coinHeight/2-2, this.coinWidth, this.coinHeight*max_pile_count);
		}

	}

	private void drawBoard(GC canvasGC, NimState nim) {
		Rectangle area = canvas.getClientArea();
		Image im = new Image(display, area.width, area.height);
		GC gc = new GC(im);
		gc.setAntialias(SWT.ON);
		gc.setAlpha(255);
		int x, y, marg;
		marg = 4;
		gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		x = (int) Math.round(0.5 * coinWidth - marg);
		y = (int) Math.round(0.5 * coinHeight - marg);
		int width = number_of_piles * coinWidth + marg + 1;
		int height = max_pile_count * coinHeight + marg + 1;
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		gc.fillRectangle(x, y, width, height);
		gc.drawRectangle(x, y, width, height);
		
		width = coinWidth;
		height = coinHeight;
		List<Integer> piles = nim.getPiles();
		for (int i = 0; i < piles.size(); i++) {
			for (int j = 0; j< piles.get(i); j++) {
				x = (int) Math.round(0.5 * coinWidth + i * coinWidth);
				y = (int) Math.round(0.5 * coinHeight + (max_pile_count - j - 1) * coinHeight);
				gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
				gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
				gc.fillRectangle(x, y, coinWidth-5, coinHeight-5);
				gc.drawRectangle(x, y, coinWidth-5, coinHeight-5);
			}
			gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
			gc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
			if (player==1) {
				gc.drawString("Player 1: " + comboPlayer2.getText(), area.width/3, marg);
			}
			if (player==2) {
				gc.drawString("Player 2: " + comboPlayer1.getText(), area.width/3, marg);
			}
		}
		drawMove(gc);
		canvasGC.drawImage(im, 0, 0);
	}

	/**
	 * @param args
	 *            a string representation of the initial state of the game using
	 *            a comma-separated sequence of integers, e.g. 3,5,6
	 */
	public static void main(String[] args) {
		System.out.println("SAC NIM GAME");
		System.out.println("(c)2013 by {pklesk,mkorzen}@wi.zut.edu.pl");
		List<Integer> initState = new LinkedList<Integer>();
		if (args.length == 0) {
			initState = new LinkedList<Integer>(Arrays.asList(3, 1, 3, 4));			
		} else {
			String[] si = args[0].split(",");
			for (String s : si) {
				initState.add(Integer.parseInt(s.trim()));
			}
		}
		//System.out.println(Arrays.toString(args));
		System.out.println("init state: " + initState);
		
		NimGame checkersGame = new NimGame(initState);
		checkersGame.startApplication();
	}
}