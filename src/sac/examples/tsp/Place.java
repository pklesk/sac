package sac.examples.tsp;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A place in a TSP problem.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class Place implements Comparable<Place> {

	/**
	 * Id of this place.
	 */
	private int id;

	/**
	 * X coordinate.
	 */
	private double x;

	/**
	 * Y coordinate.
	 */
	private double y;

	/**
	 * Connections attached to this place.
	 */
	private SortedSet<Connection> connections = null;

	/**
	 * Creates a new place.
	 * 
	 * @param id identifier
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public Place(int id, double x, double y) {
		this.id = id;
		this.x = x;
		this.y = y;
		connections = new TreeSet<Connection>();
	}

	/**
	 * Returns id.
	 * 
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns x coordinate.
	 * 
	 * @return x coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns y coordinate.
	 * 
	 * @return y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns connections sorted lexicographically.
	 * 
	 * @return connections sorted lexicographically
	 */
	public SortedSet<Connection> getConnections() {
		return connections;
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

	@Override
	public int compareTo(Place otherPlace) {
		return id - otherPlace.id;
	}

	@Override
	public boolean equals(Object otherPlace) {
		Place otherPlace2 = (Place) otherPlace;
		return (compareTo(otherPlace2) == 0);
	}
}