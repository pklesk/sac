package sac.examples.tsp;

/**
 * A connection between to places (with its cost) in a TSP problem.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class Connection implements Comparable<Connection> {

	/**
	 * Reference to the starting place.
	 */
	private Place place1 = null;

	/**
	 * Reference to the ending place.
	 */
	private Place place2 = null;

	/**
	 * Cost of the connection.
	 */
	private double cost;

	/**
	 * Creates new connection.
	 * 
	 * @param place1 reference to the starting place
	 * @param place2 reference to the ending place
	 * @param cost cost of the connection
	 */
	public Connection(Place place1, Place place2, double cost) {
		if (place1.compareTo(place2) < 0) {
			this.place1 = place1;
			this.place2 = place2;
		} else {
			this.place1 = place2;
			this.place2 = place1;
		}
		this.cost = cost;
	}

	/**
	 * Returns the reference to the starting place.
	 * 
	 * @return reference to the starting place
	 */
	public Place getPlace1() {
		return place1;
	}

	/**
	 * Returns the reference to the ending place.
	 * 
	 * @return reference to the ending place
	 */
	public Place getPlace2() {
		return place2;
	}

	/**
	 * Returns the cost of the connection.
	 * 
	 * @return cost of the connection
	 */
	public double getCost() {
		return cost;
	}

	@Override
	public String toString() {
		return "(" + place1.getId() + "," + place2.getId() + ")";
	}

	@Override
	public int compareTo(Connection otherConnection) {
		int place1IdDifference = place1.getId() - otherConnection.getPlace1().getId();
		if (place1IdDifference == 0)
			return place2.getId() - otherConnection.getPlace2().getId();
		return place1IdDifference;
	}

	@Override
	public boolean equals(Object otherConnection) {
		Connection otherConnection2 = (Connection) otherConnection;
		return (compareTo(otherConnection2) == 0);
	}
}