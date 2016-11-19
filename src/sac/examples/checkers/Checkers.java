package sac.examples.checkers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import sac.State;
import sac.StateFunction;
import sac.game.GameState;
import sac.game.GameStateImpl;

/**
 * Checkers state.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class Checkers extends GameStateImpl {

	/**
	 * Size of the board (n x n).
	 */
	public static int n;

	/**
	 * Forbidden direction NW.
	 */
	private static final int NW_FORBIDDEN = 0;

	/**
	 * Forbidden direction NE.
	 */
	private static final int NE_FORBIDDEN = 1;

	/**
	 * Forbidden direction SW.
	 */
	private static final int SW_FORBIDDEN = 2;

	/**
	 * Forbidden direction SE.
	 */
	private static final int SE_FORBIDDEN = 3;

	/**
	 * Forbidden direction none.
	 */
	private static final int NOTHING_FORBIDDEN = 4;

	/**
	 * Kind of piece - none.
	 */
	public static final int NO_PIECE = 0;

	/**
	 * Kind of piece - white pawn.
	 */
	public static final int WHITE_PAWN = 1;

	/**
	 * Kind of piece - white king.
	 */
	public static final int WHITE_KING = 2;

	/**
	 * Kind of piece - black pawn.
	 */
	public static final int BLACK_PAWN = 3;

	/**
	 * Kind of piece - black king.
	 */
	public static final int BLACK_KING = 4;

	/**
	 * Locations separator.
	 */
	public static final String LOCATIONS_SEPARATOR = ",";

	/**
	 * Description separator.
	 */
	public static final String DESCRIPTION_SEPARATOR = ";";

	/**
	 * Empty line character.
	 */
	public static final String NO_LOCATIONS = "-";

	/**
	 * Move separator (e.g. a move from A1 to B2 is "A1:B2").
	 */
	public static final String MOVE_SEPARATOR = ":";

	/**
	 * Description separator.
	 */
	public static final String DESCRIPTION_SEPARATOR_FOR_GRAPHVIZ_LABEL = ";<BR/>";

	/**
	 * Pawn heuristic factor.
	 */
	private static final double PAWN_HEURISTIC_FACTOR = 100.0;

	/**
	 * King heuristic factor.
	 */
	private static final double KING_HEURISTIC_FACTOR = 10.0 * PAWN_HEURISTIC_FACTOR;

	/**
	 * Locations of white pawns.
	 */
	private List<BoardLocation> whitePawns = null;

	/**
	 * Locations of white kings.
	 */
	private List<BoardLocation> whiteKings = null;

	/**
	 * Locations of black pawns.
	 */
	private List<BoardLocation> blackPawns = null;

	/**
	 * Locations of black kings.
	 */
	private List<BoardLocation> blackKings = null;

	/**
	 * List of possible moves as strings.
	 */
	private List<String> possibleMoves = null;

	/**
	 * Flag stating if this state has some kill moves to be played. The flag is calculated insisde getPossibleMoves()
	 * method.
	 */
	private boolean hasSomeKillMoves = false;

	/**
	 * Creates a new instance of checkers state as a copy of given parent.
	 * 
	 * @param parent state to copy from
	 */
	public Checkers(Checkers parent) {
		super();

		if (parent == null) {
			whitePawns = new ArrayList<BoardLocation>();
			whiteKings = new LinkedList<BoardLocation>();
			blackPawns = new LinkedList<BoardLocation>();
			blackKings = new LinkedList<BoardLocation>();
		} else {
			whitePawns = copyLocationList(parent.whitePawns);
			whiteKings = copyLocationList(parent.whiteKings);
			blackPawns = copyLocationList(parent.blackPawns);
			blackKings = copyLocationList(parent.blackKings);
		}

		setMaximizingTurnNow(parent.isMaximizingTurnNow());
	}
	
	/**
	 * Creates a new instance of checkers state, initializing locations of pieces from given lists.
	 * 
	 * @param whitePawns list of locations for white pawns
	 * @param whiteKings list of locations for white kings
	 * @param blackPawns list of locations for black pawns
	 * @param blackKings list of locations for black kings
	 * @param isWhiteTurnNow boolean flag indicating whether it is now white's turn to play
	 */
	public Checkers(List<BoardLocation> whitePawns, List<BoardLocation> whiteKings, List<BoardLocation> blackPawns, List<BoardLocation> blackKings,
			boolean isWhiteTurnNow) {
		super();

		this.whitePawns = whitePawns;
		this.whiteKings = whiteKings;
		this.blackPawns = blackPawns;
		this.blackKings = blackKings;

		setMaximizingTurnNow(isWhiteTurnNow);
	}

	/**
	 * Creates a deep copy of a locations list.
	 * 
	 * @param aSource a locations list
	 * @return copy of a locations list
	 */
	private static List<BoardLocation> copyLocationList(List<BoardLocation> aSource) {
		List<BoardLocation> copy = new LinkedList<BoardLocation>();
		Iterator<BoardLocation> iterator = aSource.iterator();
		while (iterator.hasNext())
			copy.add(new BoardLocation(iterator.next()));
		return copy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toShortString();
	}

	/**
	 * Converts the state to a short string representation 
	 * 
	 * @return short string representation
	 */
	public String toShortString() {
		StringBuilder builder = new StringBuilder("");
		builder.append(locationsToString(whitePawns));
		builder.append(DESCRIPTION_SEPARATOR);
		builder.append(locationsToString(whiteKings));
		builder.append(DESCRIPTION_SEPARATOR);
		builder.append(locationsToString(blackPawns));
		builder.append(DESCRIPTION_SEPARATOR);
		builder.append(locationsToString(blackKings));
		builder.append(DESCRIPTION_SEPARATOR);
		builder.append(Boolean.valueOf(isWhiteTurnNow()).toString());
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		List<Integer> locationsAsXY = new ArrayList<Integer>();

		for (BoardLocation location : whitePawns) {
			locationsAsXY.add(location.getX());
			locationsAsXY.add(location.getY());
		}
		locationsAsXY.add(0); // serving as separator

		for (BoardLocation location : whiteKings) {
			locationsAsXY.add(location.getX());
			locationsAsXY.add(location.getY());
		}
		locationsAsXY.add(0); // serving as separator

		for (BoardLocation location : blackPawns) {
			locationsAsXY.add(location.getX());
			locationsAsXY.add(location.getY());
		}
		locationsAsXY.add(0); // serving as separator
		for (BoardLocation location : blackKings) {
			locationsAsXY.add(location.getX());
			locationsAsXY.add(location.getY());
		}
		locationsAsXY.add(0); // serving as separator
		locationsAsXY.add(isWhiteTurnNow() ? 1 : -1); // serving as 'whos turn' identifier

		return locationsAsXY.hashCode();
	}

	/**
	 * Returns the Checkers state basing on a string.
	 * 
	 * @param n the size of the board
	 * @param checkersAsString string representation of the CheckersState
	 * @return checkers state built from the string
	 */
	public static Checkers stringToCheckersState(int n, String checkersAsString) {
		StringTokenizer tokenizer = new StringTokenizer(checkersAsString, DESCRIPTION_SEPARATOR);
		int i = 1;
		List<BoardLocation> whitePawns = null;
		List<BoardLocation> whiteKings = null;
		List<BoardLocation> blackPawns = null;
		List<BoardLocation> blackKings = null;
		boolean isWhitesTurnNow = true;

		while (tokenizer.hasMoreTokens()) {
			if (i == 1)
				whitePawns = stringToLocations(tokenizer.nextToken());
			else if (i == 2)
				whiteKings = stringToLocations(tokenizer.nextToken());
			else if (i == 3)
				blackPawns = stringToLocations(tokenizer.nextToken());
			else if (i == 4)
				blackKings = stringToLocations(tokenizer.nextToken());
			else
				isWhitesTurnNow = Boolean.valueOf(tokenizer.nextToken());
			i++;
		}
		Checkers.n = n;
		Checkers state = new Checkers(whitePawns, whiteKings, blackPawns, blackKings, isWhitesTurnNow);
		return state;
	}

	/**
	 * Converts a string to the list of locations.
	 * 
	 * @param locationsAsString locations as string
	 * @return the list of locations
	 */
	public static List<BoardLocation> stringToLocations(String locationsAsString) {
		List<BoardLocation> locations = new ArrayList<BoardLocation>();
		if (locationsAsString.equals(NO_LOCATIONS))
			return locations;
		StringTokenizer tokenizer = new StringTokenizer(locationsAsString, LOCATIONS_SEPARATOR);
		while (tokenizer.hasMoreTokens())
			locations.add(BoardLocation.stringToLocation(tokenizer.nextToken()));
		Collections.sort(locations);
		return locations;
	}

	/**
	 * Converts a given list of locations to a separated string.
	 * 
	 * @param locationsAsString list of locations
	 * @return string representation of a locations list
	 */
	public static String locationsToString(List<BoardLocation> locationsAsString) {
		Collections.sort(locationsAsString);

		String result = "";
		if (locationsAsString.isEmpty())
			return NO_LOCATIONS;
		Iterator<BoardLocation> iterator = locationsAsString.iterator();
		while (iterator.hasNext())
			result += iterator.next() + LOCATIONS_SEPARATOR;
		result = result.substring(0, result.length() - 1);
		return result;
	}

	private static String locationsToGraphvizLabel(List<BoardLocation> locationsAsString) {
		Collections.sort(locationsAsString);

		String result = "";
		if (locationsAsString.isEmpty())
			return NO_LOCATIONS;
		Iterator<BoardLocation> iterator = locationsAsString.iterator();
		int k = 0;
		while (iterator.hasNext()) {
			result += iterator.next();
			if (iterator.hasNext())
				result += LOCATIONS_SEPARATOR;
			k++;
			if ((k % 4 == 0) && (iterator.hasNext()))
				result += "<BR/>";
		}
		return result;
	}

	/**
	 * Returns n.
	 * 
	 * @return n
	 */
	public static int getN() {
		return n;
	}

	/**
	 * Returns the black pawns.
	 * 
	 * @return the black pawns
	 */
	public List<BoardLocation> getBlackPawns() {
		return blackPawns;
	}

	/**
	 * Returns the black kings.
	 * 
	 * @return the black kings
	 */
	public List<BoardLocation> getBlackKings() {
		return blackKings;
	}

	/**
	 * Returns the white pawns.
	 * 
	 * @return the white pawns.
	 */
	public List<BoardLocation> getWhitePawns() {
		return whitePawns;
	}

	/**
	 * Returns the white kings.
	 * 
	 * @return the white kings
	 */
	public List<BoardLocation> getWhiteKings() {
		return whiteKings;
	}

	/**
	 * Returns human-like representation of this checkers state.
	 * 
	 * @return human-like representation of this checkers state
	 */
	public String toHumanString() {
		StringBuilder result = new StringBuilder("");
		StringBuilder evenLine = new StringBuilder("  ");
		StringBuilder oddLine = new StringBuilder("  ");
		for (int i = 1; i <= n; i++)
			if ((i % 2) == 1) {
				oddLine.append("***");
				evenLine.append("   ");
			} else {
				oddLine.append("   ");
				evenLine.append("***");
			}

		for (int i = n; i >= 1; i--) {
			if ((i % 2) == 0)
				result.append(evenLine);
			else
				result.append(oddLine);
			result.append("\n");

			for (int j = 1; j <= n; j++) {
				String pieceString = "*";
				if ((i + j) % 2 == 1)
					pieceString = " ";
				int pieceConstant = pieceAt(new BoardLocation(j, i));
				switch (pieceConstant) {
				case WHITE_PAWN:
					pieceString = "w";
					break;
				case WHITE_KING:
					pieceString = "W";
					break;
				case BLACK_PAWN:
					pieceString = "b";
					break;
				case BLACK_KING:
					pieceString = "B";
					break;
				}

				if (j == 1)
					result.append(i + " ");

				if ((i + j) % 2 == 1)
					result.append(" " + pieceString + " ");
				else
					result.append("*" + pieceString + "*");
			}
			result.append("\n");
			if ((i % 2) == 0)
				result.append(evenLine);
			else
				result.append(oddLine);
			result.append("\n");
		}
		result.append("   A  B  C  D  E  F  G  H\n");
		result.append((isWhiteTurnNow()) ? "White's turn now." : "Blacks's turn now.");
		result.append("\n");
		return result.toString();
	}

	/**
	 * Returns the constant of a piece standing at given location.
	 * 
	 * @param location location to be checked
	 * @return piece constant
	 */
	public int pieceAt(BoardLocation location) {
		if (whitePawns.contains(location))
			return WHITE_PAWN;
		if (whiteKings.contains(location))
			return WHITE_KING;
		if (blackPawns.contains(location))
			return BLACK_PAWN;
		if (blackKings.contains(location))
			return BLACK_KING;
		return NO_PIECE;
	}

	/**
	 * Reads checkers state from a file.
	 * 
	 * @param filePath file path
	 * @param n board size
	 * @return CheckersState object
	 * @throws Exception whenever something with file open/read goes wrong
	 */
	public static Checkers fileToCheckersState(String filePath, int n) throws Exception {
		Checkers state = null;
		String stateAsString = "";

		File file = new File(filePath);
		FileReader reader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(reader);
		stateAsString = bufferedReader.readLine();
		bufferedReader.close();
		reader.close();

		state = Checkers.stringToCheckersState(n, stateAsString);

		return state;
	}

	/**
	 * Changes this checkers state by applying a given move.
	 * 
	 * @param moveString string representation of a move
	 * @param changeTurn should the turn change occur after this move
	 * @return new location of the moved piece
	 */
	public BoardLocation makeMove(String moveString, boolean changeTurn) {
		moveString = moveString.toUpperCase();
		possibleMoves = null;
		StringTokenizer tokenizer = new StringTokenizer(moveString, MOVE_SEPARATOR);

		List<BoardLocation> pawns = null;
		List<BoardLocation> kings = null;
		List<BoardLocation> opponentPawns = null;
		List<BoardLocation> opponentKings = null;

		if (isWhiteTurnNow()) {
			pawns = whitePawns;
			kings = whiteKings;
			opponentPawns = blackPawns;
			opponentKings = blackKings;
		} else {
			pawns = blackPawns;
			kings = blackKings;
			opponentPawns = whitePawns;
			opponentKings = whiteKings;
		}

		BoardLocation location = BoardLocation.stringToLocation(tokenizer.nextToken());
		BoardLocation newLocation = null;

		List<BoardLocation> piecesInPlay = null;
		boolean isPawnInPlay = false;
		if (pawns.contains(location)) {
			piecesInPlay = pawns;
			isPawnInPlay = true;
		} else
			piecesInPlay = kings;

		while (tokenizer.hasMoreTokens()) {
			newLocation = BoardLocation.stringToLocation(tokenizer.nextToken());
			int dx = (newLocation.getX() - location.getX() > 0) ? 1 : -1;
			int dy = (newLocation.getY() - location.getY() > 0) ? 1 : -1;
			piecesInPlay.remove(location);
			while (!location.equals(newLocation)) {
				location.setX(location.getX() + dx);
				location.setY(location.getY() + dy);
				if (opponentPawns.contains(location))
					opponentPawns.remove(location);
				else if (opponentKings.contains(location))
					opponentKings.remove(location);
			}
			piecesInPlay.add(newLocation);
			Collections.sort(piecesInPlay);
		}

		// promotion check
		if (isPawnInPlay) {
			if ((isMaximizingTurnNow()) && (newLocation.getY() == n)) {
				whitePawns.remove(newLocation);
				whiteKings.add(newLocation);
			} else if ((!isMaximizingTurnNow()) && (newLocation.getY() == 1)) {
				blackPawns.remove(newLocation);
				blackKings.add(newLocation);
			}
		}

		// ply
		if (changeTurn) {
			setMaximizingTurnNow(!isMaximizingTurnNow());
		}

		refresh();

		return newLocation;
	}

	/**
	 * Saves checkers state to a file.
	 * 
	 * @param filePath file path
	 * @throws Exception whenever something with file open/write goes wrong
	 */
	public void toFile(String filePath) throws Exception {
		File file = new File(filePath);
		FileWriter writer = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		bufferedWriter.write(this.toString());
		bufferedWriter.close();
		writer.close();
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
	 * Sets the boolean stating if it is whites turn now.
	 * 
	 * @param isWhiteTurnNow boolean value to be set
	 */
	public void setWhiteTurnNow(boolean isWhiteTurnNow) {
		setMaximizingTurnNow(isWhiteTurnNow);
	}

	/**
	 * Returns the list of all possible moves as strings.
	 * 
	 * @return list of all possible moves
	 */
	public List<String> getPossibleMoves() {
		if (possibleMoves != null)
			return possibleMoves;

		possibleMoves = new ArrayList<String>();

		// king kill-moves
		Iterator<BoardLocation> kingsIterator = (isWhiteTurnNow()) ? whiteKings.iterator() : blackKings.iterator();
		while (kingsIterator.hasNext()) {
			BoardLocation king = kingsIterator.next();
			populatePossibleKingKills(this, "", isWhiteTurnNow(), king, possibleMoves, NOTHING_FORBIDDEN);

			// comment the line beneath if free choice of kills is given (if maximal must be chosen,
			// leave the line uncommented)
			eliminateNonMaximalKills(possibleMoves);
		}

		// uncomment the line beneath if kings have the 'killing priority' over pawns
		// if (!result.isEmpty()) return result;

		// pawn kill-moves
		Iterator<BoardLocation> pawnsIterator = (isWhiteTurnNow()) ? whitePawns.iterator() : blackPawns.iterator();
		while (pawnsIterator.hasNext()) {
			BoardLocation pawn = pawnsIterator.next();
			populatePossiblePawnKills(this, "", isWhiteTurnNow(), pawn, possibleMoves);

			// comment the line beneath if free choice of kills is given (if maximal must be chosen,
			// leave the line uncommented)
			eliminateNonMaximalKills(possibleMoves);
		}
		if (!possibleMoves.isEmpty()) {
			hasSomeKillMoves = true;

			// comment the line beneath if kills are not mandatory
			return possibleMoves;
		}

		// king regular moves
		kingsIterator = (isWhiteTurnNow()) ? whiteKings.iterator() : blackKings.iterator();
		while (kingsIterator.hasNext()) {
			BoardLocation king = kingsIterator.next();
			populatePossibleKingMoves(this, king, possibleMoves);
		}

		// pawn regular moves
		pawnsIterator = (isWhiteTurnNow()) ? whitePawns.iterator() : blackPawns.iterator();
		while (pawnsIterator.hasNext()) {
			BoardLocation pawn = pawnsIterator.next();
			populatePossiblePawnMoves(this, isWhiteTurnNow(), pawn, possibleMoves);
		}

		return possibleMoves;
	}
	
	/**
	 * Finds recurrently all possible kill-moves for a given pawn.
	 * 
	 * @param checkers given state
	 * @param prefix move prefix that led to this state
	 * @param isWhitesTurnNow boolean stating if it is now whites turn
	 * @param pawn given pawn
	 * @param globalList global list of kill moves (shared by the whole recurrence)
	 */
	private static void populatePossiblePawnKills(Checkers checkers, String prefix, boolean isWhitesTurnNow, BoardLocation pawn, List<String> globalList) {

		List<BoardLocation> opponentPawns = null;
		List<BoardLocation> opponentKings = null;
		if (isWhitesTurnNow) {
			opponentPawns = checkers.getBlackPawns();
			opponentKings = checkers.getBlackKings();
		} else {
			opponentPawns = checkers.getWhitePawns();
			opponentKings = checkers.getWhiteKings();
		}

		List<String> moves = new ArrayList<String>();

		// NW
		BoardLocation testLocation = new BoardLocation(pawn.getX() - 1, pawn.getY() + 1);
		if ((opponentPawns.contains(testLocation)) || (opponentKings.contains(testLocation))) {
			testLocation.setX(testLocation.getX() - 1);
			testLocation.setY(testLocation.getY() + 1);

			if (checkers.isLocationEmpty(testLocation)) {
				moves.add(testLocation.toString());
			}
		}

		// NE
		testLocation = new BoardLocation(pawn.getX() + 1, pawn.getY() + 1);
		if ((opponentPawns.contains(testLocation)) || (opponentKings.contains(testLocation))) {
			testLocation.setX(testLocation.getX() + 1);
			testLocation.setY(testLocation.getY() + 1);

			if (checkers.isLocationEmpty(testLocation)) {
				moves.add(testLocation.toString());
			}
		}

		// SW
		testLocation = new BoardLocation(pawn.getX() - 1, pawn.getY() - 1);
		if ((opponentPawns.contains(testLocation)) || (opponentKings.contains(testLocation))) {
			testLocation.setX(testLocation.getX() - 1);
			testLocation.setY(testLocation.getY() - 1);

			if (checkers.isLocationEmpty(testLocation)) {
				moves.add(testLocation.toString());
			}
		}

		// SE
		BoardLocation se = new BoardLocation(pawn.getX() + 1, pawn.getY() - 1);
		if ((opponentPawns.contains(se)) || (opponentKings.contains(se))) {
			se.setX(se.getX() + 1);
			se.setY(se.getY() - 1);

			if (checkers.isLocationEmpty(se)) {
				moves.add(se.toString());
			}
		}

		if (moves.isEmpty()) {
			if (!prefix.equals(""))
				globalList.add(prefix);
		} else {
			Iterator<String> iterator = moves.iterator();
			while (iterator.hasNext()) {
				String move = iterator.next();
				Checkers copy = new Checkers(checkers);
				BoardLocation newPawn = copy.makeMove(pawn + MOVE_SEPARATOR + move, false);

				String tempPrefix = pawn + MOVE_SEPARATOR + move;
				if (!prefix.equals(""))
					tempPrefix = prefix + MOVE_SEPARATOR + move;
				populatePossiblePawnKills(copy, tempPrefix, isWhitesTurnNow, newPawn, globalList);
			}
		}
	}

	/**
	 * Checks for possible kills along a given direction. Adds results to given list.
	 * 
	 * @param checkers current state
	 * @param king given king
	 * @param dx dx of direction
	 * @param dy dy of direction
	 * @param opponentPawns list of opponent pawn locations
	 * @param opponentKings list of opponent king locations
	 * @param ownPawns list of own pawn locations
	 * @param ownKings list of own king locations
	 * @param moves list of results
	 */
	private static void checkKingKillsAlongDirection(Checkers checkers, BoardLocation king, int dx, int dy, List<BoardLocation> opponentPawns,
			List<BoardLocation> opponentKings, List<BoardLocation> ownPawns, List<BoardLocation> ownKings, List<String> moves) {
		BoardLocation newLocation = new BoardLocation(king);
		while (true) {
			newLocation = new BoardLocation(newLocation.getX() + dx, newLocation.getY() + dy);
			if (!checkers.isLocationInsideBoard(newLocation))
				break;
			if ((ownPawns.contains(newLocation)) || (ownKings.contains(newLocation)))
				break;
			if ((opponentPawns.contains(newLocation)) || (opponentKings.contains(newLocation))) {
				// landing places
				while (true) {
					newLocation = new BoardLocation(newLocation.getX() + dx, newLocation.getY() + dy);
					if (checkers.isLocationEmpty(newLocation)) {
						moves.add(newLocation.toString());
					} else
						break;
				}
				break;
			}
		}
	}

	/**
	 * Finds recurrently all possible kill-moves for a given king.
	 * 
	 * @param checkers given state
	 * @param prefix move prefix that led to this state
	 * @param isWhitesTurnNow boolean stating if it is now whites turn
	 * @param king given king
	 * @param globalList global list of kill moves (shared by the whole recurrence)
	 * @param forbiddenDirection constant saying in which direction the part-move is forbidden now (this direction is
	 *            opposite to the previous one)
	 */
	private static void populatePossibleKingKills(Checkers checkers, String prefix, boolean isWhitesTurnNow, BoardLocation king, List<String> globalList,
			int forbiddenDirection) {

		List<BoardLocation> opponentPawns = null;
		List<BoardLocation> opponentKings = null;
		List<BoardLocation> ownPawns = null;
		List<BoardLocation> ownKings = null;
		if (isWhitesTurnNow) {
			opponentPawns = checkers.getBlackPawns();
			opponentKings = checkers.getBlackKings();
			ownPawns = checkers.getWhitePawns();
			ownKings = checkers.getWhiteKings();
		} else {
			opponentPawns = checkers.getWhitePawns();
			opponentKings = checkers.getWhiteKings();
			ownPawns = checkers.getBlackPawns();
			ownKings = checkers.getBlackKings();
		}

		List<String> moves = new ArrayList<String>();
		List<Integer> forbiddenDirections = new ArrayList<Integer>();

		// NW
		if (forbiddenDirection != NW_FORBIDDEN) {
			checkKingKillsAlongDirection(checkers, king, -1, +1, opponentPawns, opponentKings, ownPawns, ownKings, moves);
			int difference = moves.size() - forbiddenDirections.size();
			for (int i = 0; i < difference; i++)
				// forbiddenDirections.add(Integer.valueOf(NOTHING_FORBIDDEN));
				forbiddenDirections.add(Integer.valueOf(SE_FORBIDDEN));
		}

		// NE
		if (forbiddenDirection != NE_FORBIDDEN) {
			checkKingKillsAlongDirection(checkers, king, +1, +1, opponentPawns, opponentKings, ownPawns, ownKings, moves);
			int difference = moves.size() - forbiddenDirections.size();
			for (int i = 0; i < difference; i++)
				// forbiddenDirections.add(Integer.valueOf(NOTHING_FORBIDDEN));
				forbiddenDirections.add(Integer.valueOf(SW_FORBIDDEN));
		}

		// SW
		if (forbiddenDirection != SW_FORBIDDEN) {
			checkKingKillsAlongDirection(checkers, king, -1, -1, opponentPawns, opponentKings, ownPawns, ownKings, moves);
			int difference = moves.size() - forbiddenDirections.size();
			for (int i = 0; i < difference; i++)
				// forbiddenDirections.add(Integer.valueOf(NOTHING_FORBIDDEN));
				forbiddenDirections.add(Integer.valueOf(NE_FORBIDDEN));
		}

		// SE
		if (forbiddenDirection != SE_FORBIDDEN) {
			checkKingKillsAlongDirection(checkers, king, +1, -1, opponentPawns, opponentKings, ownPawns, ownKings, moves);
			int difference = moves.size() - forbiddenDirections.size();
			for (int i = 0; i < difference; i++)
				// forbiddenDirections.add(Integer.valueOf(NOTHING_FORBIDDEN));
				forbiddenDirections.add(Integer.valueOf(NW_FORBIDDEN));
		}

		if (moves.isEmpty()) {
			if (!prefix.equals(""))
				globalList.add(prefix);
		} else {
			Iterator<String> iterator = moves.iterator();
			Iterator<Integer> forbiddenDirectionsIterator = forbiddenDirections.iterator();
			while (iterator.hasNext()) {
				String move = iterator.next();
				int tempForbiddenDirection = forbiddenDirectionsIterator.next();
				Checkers copy = new Checkers(checkers);
				BoardLocation newKing = copy.makeMove(king + MOVE_SEPARATOR + move, false);

				String tempPrefix = king + MOVE_SEPARATOR + move;
				if (!prefix.equals(""))
					tempPrefix = prefix + MOVE_SEPARATOR + move;
				populatePossibleKingKills(copy, tempPrefix, isWhitesTurnNow, newKing, globalList, tempForbiddenDirection);
			}
		}
	}

	/**
	 * Finds all possible moves for a given king.
	 * 
	 * @param checkers given state
	 * @param isWhitesTurnNow boolean stating if it is now whites turn
	 * @param king given king
	 * @param globalList global list of moves
	 */
	private static void populatePossibleKingMoves(Checkers checkers, BoardLocation king, List<String> globalList) {

		List<String> moves = new ArrayList<String>();

		// NW
		BoardLocation newLocation = new BoardLocation(king.getX() - 1, king.getY() + 1);
		while (true) {
			if (checkers.isLocationEmpty(newLocation))
				moves.add(king.toString() + MOVE_SEPARATOR + newLocation.toString());
			else
				break;
			newLocation = new BoardLocation(newLocation.getX() - 1, newLocation.getY() + 1);
		}
		// NE
		newLocation = new BoardLocation(king.getX() + 1, king.getY() + 1);
		while (true) {
			if (checkers.isLocationEmpty(newLocation))
				moves.add(king.toString() + MOVE_SEPARATOR + newLocation.toString());
			else
				break;
			newLocation = new BoardLocation(newLocation.getX() + 1, newLocation.getY() + 1);
		}
		// SW
		newLocation = new BoardLocation(king.getX() - 1, king.getY() - 1);
		while (true) {
			if (checkers.isLocationEmpty(newLocation))
				moves.add(king.toString() + MOVE_SEPARATOR + newLocation.toString());
			else
				break;
			newLocation = new BoardLocation(newLocation.getX() - 1, newLocation.getY() - 1);
		}
		// SE
		newLocation = new BoardLocation(king.getX() + 1, king.getY() - 1);
		while (true) {
			if (checkers.isLocationEmpty(newLocation))
				moves.add(king.toString() + MOVE_SEPARATOR + newLocation.toString());
			else
				break;
			newLocation = new BoardLocation(newLocation.getX() + 1, newLocation.getY() - 1);
		}

		if (!moves.isEmpty())
			globalList.addAll(moves);
	}

	/**
	 * Finds all possible moves for a given pawn.
	 * 
	 * @param checkers given state
	 * @param isWhitesTurnNow boolean stating if it is now whites turn
	 * @param pawn given pawn
	 * @param globalList global list of moves
	 */
	private static void populatePossiblePawnMoves(Checkers checkers, boolean isWhitesTurnNow, BoardLocation pawn, List<String> globalList) {

		List<String> moves = new ArrayList<String>();

		if (isWhitesTurnNow) {
			// NW
			BoardLocation nw = new BoardLocation(pawn.getX() - 1, pawn.getY() + 1);
			if (checkers.isLocationEmpty(nw))
				moves.add(pawn.toString() + MOVE_SEPARATOR + nw.toString());

			// NE
			BoardLocation ne = new BoardLocation(pawn.getX() + 1, pawn.getY() + 1);
			if (checkers.isLocationEmpty(ne))
				moves.add(pawn.toString() + MOVE_SEPARATOR + ne.toString());
		} else {
			// SW
			BoardLocation sw = new BoardLocation(pawn.getX() - 1, pawn.getY() - 1);
			if (checkers.isLocationEmpty(sw))
				moves.add(pawn.toString() + MOVE_SEPARATOR + sw.toString());

			// SE
			BoardLocation se = new BoardLocation(pawn.getX() + 1, pawn.getY() - 1);
			if (checkers.isLocationEmpty(se))
				moves.add(pawn.toString() + MOVE_SEPARATOR + se.toString());
		}

		if (!moves.isEmpty())
			globalList.addAll(moves);
	}

	/**
	 * If there is such a rule that with a choice of kill moves the one with maximal kills must be selected, this method
	 * should be applied. It checks the list of all kills and eliminates from the list non-maximal ones.
	 * 
	 * @param killMoves a list of all kill moves
	 */
	private void eliminateNonMaximalKills(List<String> killMoves) {
		int maximalNumberOfKills = 1;
		int[] killCounts = new int[killMoves.size()];
		for (int i = 0; i < killMoves.size(); i++) {
			String kill = killMoves.get(i);
			int numberOfKills = -1;
			StringTokenizer tokenizer = new StringTokenizer(kill, MOVE_SEPARATOR);
			while (tokenizer.hasMoreElements()) {
				numberOfKills++;
				tokenizer.nextElement();
			}
			if (numberOfKills > maximalNumberOfKills)
				maximalNumberOfKills = numberOfKills;
			killCounts[i] = numberOfKills;
		}

		List<String> newKillMoves = new ArrayList<String>();
		for (int i = 0; i < killCounts.length; i++) {
			if (killCounts[i] == maximalNumberOfKills)
				newKillMoves.add(killMoves.get(i));
		}
		killMoves.clear();
		killMoves.addAll(newKillMoves);
	}
	
	/**
	 * Checks if a location is empty.
	 * 
	 * @param aLocation given location
	 * @return boolean stating if a locaion is empty
	 */
	public boolean isLocationEmpty(BoardLocation aLocation) {
		if (((aLocation.getX() < 1) || (n < aLocation.getX())) || ((aLocation.getY() < 1) || (n < aLocation.getY())) || ((whitePawns.contains(aLocation)))
				|| ((whiteKings.contains(aLocation))) || ((blackPawns.contains(aLocation))) || ((blackKings.contains(aLocation))))
			return false;
		return true;
	}

	/**
	 * Checks if a location is inside the board.
	 * 
	 * @param aLocation given location
	 * @return boolean stating if a locaion is inside the board
	 */
	public boolean isLocationInsideBoard(BoardLocation aLocation) {
		if (((aLocation.getX() < 1) || (n < aLocation.getX())) || ((aLocation.getY() < 1) || (n < aLocation.getY())))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameState#generateChildren()
	 */
	@Override
	public List<GameState> generateChildren() {
		List<String> moves = getPossibleMoves();
		List<GameState> children = new LinkedList<GameState>();
		for (String move : moves) {
			Checkers child = new Checkers(this);
			child.makeMove(move, true);
			child.setMoveName(move);
			children.add(child);

		}
		return children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.GameStateImpl#isQuiet()
	 */
	@Override
	public boolean isQuiet() {
		getPossibleMoves(); // in order to calculate hasSomeKillingMoves flag
		return !hasSomeKillMoves;
	}

	@Override
	public String toGraphvizLabel() {
		StringBuilder builder = new StringBuilder("");
		builder.append(locationsToGraphvizLabel(whitePawns));
		builder.append(DESCRIPTION_SEPARATOR_FOR_GRAPHVIZ_LABEL);
		builder.append(locationsToGraphvizLabel(whiteKings));
		builder.append(DESCRIPTION_SEPARATOR_FOR_GRAPHVIZ_LABEL);
		builder.append(locationsToGraphvizLabel(blackPawns));
		builder.append(DESCRIPTION_SEPARATOR_FOR_GRAPHVIZ_LABEL);
		builder.append(locationsToGraphvizLabel(blackKings));
		builder.append(DESCRIPTION_SEPARATOR_FOR_GRAPHVIZ_LABEL);
		builder.append(Boolean.valueOf(isWhiteTurnNow()).toString());
		return builder.toString();
	}

	static {
		setHFunction(new StateFunction() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see sac.StateFunction#calculate(sac.State)
			 */
			@Override
			public double calculate(State state) {
				Checkers checkers = (Checkers) state;
				double value = 0.0;
				if ((checkers.whitePawns.size() + checkers.whiteKings.size() == 0) || ((checkers.isWhiteTurnNow()) && (checkers.getPossibleMoves().isEmpty()))) {
					value = Double.NEGATIVE_INFINITY;
					return value;
				} else if ((checkers.blackPawns.size() + checkers.blackKings.size() == 0)
						|| ((!checkers.isWhiteTurnNow()) && (checkers.getPossibleMoves().isEmpty()))) {
					value = Double.POSITIVE_INFINITY;
					return value;
				}

				// material
				value = PAWN_HEURISTIC_FACTOR * (checkers.whitePawns.size() - checkers.blackPawns.size()) + KING_HEURISTIC_FACTOR
						* (checkers.whiteKings.size() - checkers.blackKings.size());

				// pawn advancement
				for (BoardLocation location : checkers.whitePawns)
					value += location.getY();
				for (BoardLocation location : checkers.blackPawns)
					value -= (n + 1 - location.getY());

				return value;
			}
		});
	}

	private RenderedImage toImage(int areaWidth, int areaHeight) {
		final int AREA_WIDTH = areaWidth;
		final int AREA_HEIGHT = areaHeight;
		final Color BLACK_SQUARE_COLOR = Color.GRAY;
		final Color WHITE_SQUARE_COLOR = Color.WHITE;
		final Color BLACK_PAWN_COLOR = Color.BLACK;
		final Color WHITE_PAWN_COLOR = Color.WHITE;

		final String[] LETTERS = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
		final String[] NUMBERS = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

		BufferedImage bufferedImage = new BufferedImage(AREA_WIDTH, AREA_HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();

		final int squareHeight = (int) Math.round(AREA_HEIGHT / (n + 1));
		final int squareWidth = (int) Math.round(AREA_WIDTH / (n + 1));

		Font font = new Font("Tahoma", Font.PLAIN, 8);
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();
		int charHeight = fm.getHeight();
		int charWidth = (int) (0.5 * charHeight);

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHints(rh);

		int x, y, marg;

		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, AREA_WIDTH, AREA_HEIGHT);

		g2d.setColor(Color.BLACK);
		marg = 4;
		x = (int) Math.round(0.5 * squareWidth - marg) + 1;
		y = (int) Math.round(0.5 * squareHeight - marg) + 1;
		int width = n * squareWidth + marg + 1;
		int height = n * squareHeight + marg + 1;
		g2d.fillRect(x, y, width, height);
		g2d.drawRect(x, y, width, height);

		width = squareWidth;
		height = squareHeight;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if ((i + j) % 2 == 1)
					g2d.setColor(BLACK_SQUARE_COLOR);
				else
					g2d.setColor(WHITE_SQUARE_COLOR);
				x = (int) Math.round(0.5 * squareWidth + i * squareWidth);
				y = (int) Math.round(0.5 * squareHeight + j * squareHeight);

				g2d.fillRect(x, y, width, height);
			}

			g2d.setColor(Color.BLACK);
			g2d.drawString(LETTERS[i], (int) Math.round((i + 1) * squareWidth - 0.5 * charWidth),
					(int) Math.round(squareHeight * (n + 0.5) + 0.5 * marg + 1 * charHeight));
			g2d.drawString(NUMBERS[i], (int) Math.round(0.5 * squareWidth - marg - 1 * charWidth), (int) Math.round((n - i) * squareHeight + 0.5 * charHeight));
		}

		for (BoardLocation bl : getWhitePawns())
			drawPawn(g2d, bl, WHITE_PAWN_COLOR, BLACK_PAWN_COLOR, squareWidth, squareHeight);
		for (BoardLocation bl : getWhiteKings())
			drawKing(g2d, bl, WHITE_PAWN_COLOR, BLACK_PAWN_COLOR, squareWidth, squareHeight);
		for (BoardLocation bl : getBlackPawns())
			drawPawn(g2d, bl, BLACK_PAWN_COLOR, WHITE_PAWN_COLOR, squareWidth, squareHeight);
		for (BoardLocation bl : getBlackKings())
			drawKing(g2d, bl, BLACK_PAWN_COLOR, WHITE_PAWN_COLOR, squareWidth, squareHeight);

		g2d.dispose();
		return bufferedImage;
	}

	private void drawPawn(Graphics2D g2d, BoardLocation bl, Color fillColor, Color borderColor, int squareWidth, int squareHeight) {
		int width = (int) Math.round(2.0 / 3.0 * squareWidth);
		int height = (int) Math.round(2.0 / 3.0 * squareHeight);
		int x = (int) Math.round(bl.getX() * squareWidth - 0.5 * width);
		int y = (int) Math.round((n - bl.getY() + 1) * squareHeight - 0.5 * height);
		g2d.setColor(fillColor);
		g2d.fillOval(x, y, width, height);
		g2d.setColor(borderColor);
		g2d.drawOval(x, y, width, height);
	}

	private void drawKing(Graphics2D g2d, BoardLocation bl, Color fillColor, Color borderColor, int squareWidth, int squareHeight) {
		int width = (int) Math.round(9.0 / 10.0 * squareWidth);
		int height = (int) Math.round(9.0 / 10.0 * squareHeight);
		int x = (int) Math.round(bl.getX() * squareWidth - 0.5 * width);
		int y = (int) Math.round((n - bl.getY() + 1) * squareHeight - 0.5 * height);
		g2d.setColor(fillColor);
		g2d.fillOval(x - 1, y - 1, width, height);
		g2d.setColor(borderColor);
		g2d.drawOval(x - 1, y - 1, width - 1, height - 1);
		
		FontMetrics fm = g2d.getFontMetrics();		
		int charHeight = fm.getHeight();
		int charWidth = (int) (0.5 * charHeight);
		g2d.drawString("K", (int) Math.round(x + 0.5 * width - 0.5 * charWidth - 1), (int) Math.round(y + 0.5 * height + 0.25 * charHeight));
	}

	/**
	 * Saves checkers position at given path as a gif file.
	 * 
	 * @param path path at which the map should be saved
	 * @param areaWidth wanted image width
	 * @param areaHeight wanted image height
	 * @throws Exception if something during file writing goes wrong
	 */
	public void saveAsImage(String path, int areaWidth, int areaHeight) throws Exception {
		RenderedImage image = toImage(areaWidth, areaHeight);
		File file = new File(path);
		ImageIO.write(image, "gif", file);
	}
}