package sac.examples.tsp;

import java.util.Comparator;

/**
 * Comparator of connections according to their costs.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class ConnectionCostComparator implements Comparator<Connection> {

	@Override
	public int compare(Connection c1, Connection c2) {
		double costDifference = c1.getCost() - c2.getCost();
		if (costDifference == 0.0) {
			return c1.compareTo(c2); // lexicographic order if same costs
		}
		return (costDifference < 0.0) ? -1 : 1;
	}
}