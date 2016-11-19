package sac.examples.checkers;

/**
 * A location of a piece on the board of checkers.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class BoardLocation implements Comparable<BoardLocation> {

	/**
	 * ASCII code for the letter 'A'.
	 */
	public static final int A_ASCII_CODE = 65;

	/**
	 * Creates new instance of board location.
	 * 
	 * @param x x coordinate to be set
	 * @param y y coordinate to be set
	 */
	public BoardLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * The coordinate 'x'.
	 */
	private int x = 1;

	/**
	 * The coordinate 'y'.
	 */
	private int y = 1;

	/**
	 * Returns the x.
	 * 
	 * @return x
	 */
	public int getX() {
		return x;
	}

	/**
	 * The x to be set.
	 * 
	 * @param x x coordinate to be set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Returns the y.
	 * 
	 * @return y
	 */
	public int getY() {
		return y;
	}

	/**
	 * The y to be set.
	 * 
	 * @param y y coordinate to be set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Creates new instance of board location as a copy.
	 * 
	 * @param toCopy board location to be copied
	 */
	public BoardLocation(BoardLocation toCopy) {
		x = toCopy.x;
		y = toCopy.y;
	}

	@Override
	public String toString() {
		return String.valueOf(Character.toChars(A_ASCII_CODE + x - 1)) + y;
	}

	@Override
	public boolean equals(Object otherLocation) {
		return (x == ((BoardLocation) otherLocation).getX()) && (y == ((BoardLocation) otherLocation).getY());
	}

	/**
	 * Converts a string to board location. Asumes the first character in the string is a letter,
	 * and the remainder are numbers (e.g. A10).
	 * 
	 * @param locationAsString given two-characters string
	 * @return board location built from string
	 */
	public static BoardLocation stringToLocation(String locationAsString) {
		return new BoardLocation(locationAsString.charAt(0) - A_ASCII_CODE + 1, Integer.valueOf(locationAsString.substring(1, locationAsString.length())));
	}

	@Override
	public int compareTo(BoardLocation otherLocation) {
		if (otherLocation.x == x)
			return y - otherLocation.y;
		return x - otherLocation.x;
	}
}