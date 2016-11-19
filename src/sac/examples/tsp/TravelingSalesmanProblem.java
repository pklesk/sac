package sac.examples.tsp;

import java.awt.Color;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import sac.State;
import sac.StateFunction;
import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

/**
 * Traveling Salesman Problem (TSP) state (version for undirected graphs).
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class TravelingSalesmanProblem extends GraphStateImpl {

	/**
	 * Reference to the map, common for all states.
	 */
	public static Map map = null;

	/**
	 * Current route as list of connections travelled by.
	 */
	private List<Connection> route = null;

	/**
	 * Current route as list of places' ids.
	 */
	private List<Integer> routeAsIds = null;

	/**
	 * Current place.
	 */
	private Place currentPlace = null;

	/**
	 * Remaining places sorted lexicographically.
	 */
	private SortedSet<Place> remaining = null;

	/**
	 * Minimum spanning tree built on remaining places.
	 */
	private MinimumSpanningTree mst = null;

	/**
	 * Best (cheapest) connection from the current place to some place in the minimum spanning tree of remaining places.
	 */
	private Connection bestConnectionToMST = null;

	/**
	 * Creates a new TSP for given map.
	 * 
	 * @param map reference to the map
	 */
	public TravelingSalesmanProblem(Map map) {
		TravelingSalesmanProblem.map = map;
		route = new ArrayList<Connection>();
		currentPlace = map.getStartPlace();
		routeAsIds = new ArrayList<Integer>();
		routeAsIds.add(currentPlace.getId());
		remaining = new TreeSet<Place>();
		remaining.addAll(map.getPlaces().values());
	}

	/**
	 * Creates new TSP as a copy of given parent
	 * 
	 * @param parent TSP to be copied
	 */
	public TravelingSalesmanProblem(TravelingSalesmanProblem parent) {
		route = new ArrayList<Connection>();
		for (Connection connection : parent.route)
			route.add(connection);
		routeAsIds = new ArrayList<Integer>();
		for (Integer id : parent.routeAsIds)
			routeAsIds.add(id);
		currentPlace = parent.currentPlace;
		remaining = new TreeSet<Place>();
		remaining.addAll(parent.remaining);
	}

	@Override
	public List<GraphState> generateChildren() {
		List<GraphState> children = new LinkedList<GraphState>();
		for (Connection connection : currentPlace.getConnections()) {
			Place otherPlace = (connection.getPlace1().equals(currentPlace)) ? connection.getPlace2() : connection.getPlace1();
			if (remaining.contains(otherPlace)) {
				if ((otherPlace.equals(map.getStartPlace())) && (remaining.size() > 1))
					continue;
				TravelingSalesmanProblem child = new TravelingSalesmanProblem(this);
				child.route.add(connection);
				child.routeAsIds.add(otherPlace.getId());
				child.currentPlace = otherPlace;
				child.remaining.remove(otherPlace);
				child.setMoveName(String.valueOf(otherPlace.getId()));
				children.add(child);
			}
		}
		return children;
	}

	@Override
	public boolean isSolution() {
		return remaining.isEmpty();
	}

	@Override
	public String toString() {
		return routeAsIds.toString();
	}

	@Override
	public int hashCode() {
		return routeAsIds.hashCode();
	}

	static {
		setHFunction(new StateFunction() {
			@Override
			public double calculate(State state) {
				TravelingSalesmanProblem tsp = (TravelingSalesmanProblem) state;

				// checking if parent's MST can be used (specifically: if the place to
				// which parent's bestConnectionToMST leads does not fork further)
				if (tsp.parent != null) {
					TravelingSalesmanProblem tspParent = (TravelingSalesmanProblem) tsp.parent;
					Connection singleConnection = tspParent.mst.isPlaceWithSingleConnection(tsp.currentPlace);
					if (singleConnection != null) {
						tsp.mst = new MinimumSpanningTree(tspParent.mst);
						tsp.mst.getConnections().remove(singleConnection);
						tsp.mst.setCost(tsp.mst.getCost() - singleConnection.getCost());
						tsp.bestConnectionToMST = singleConnection;
						return tsp.mst.getCost() + tsp.bestConnectionToMST.getCost();
					}
				}
				// construction of new MST required
				tsp.mst = new MinimumSpanningTree(tsp.remaining);
				tsp.bestConnectionToMST = null;
				double bestCost = Double.POSITIVE_INFINITY;
				for (Connection connection : tsp.currentPlace.getConnections()) {
					Place otherPlace = (connection.getPlace1() == tsp.currentPlace) ? connection.getPlace2() : connection.getPlace1();
					if ((tsp.remaining.contains(otherPlace)) && (connection.getCost() < bestCost)) {
						bestCost = connection.getCost();
						tsp.bestConnectionToMST = connection;
					}
				}
				double cost = tsp.mst.getCost();
				if (tsp.bestConnectionToMST != null)
					cost += bestCost;
				return cost;
			}
		});

		setGFunction(new StateFunction() {
			@Override
			public double calculate(State state) {
				TravelingSalesmanProblem tsp = (TravelingSalesmanProblem) state;
				return (tsp.parent == null) ? 0.0 : (tsp.getParent()).getG() + tsp.route.get(tsp.route.size() - 1).getCost();
			}
		});
	}

	@Override
	public String toGraphvizLabel() {
		return "<FONT FACE='monospace' POINT-SIZE='8'>" + toStringUnderscoreSeparated() + "</FONT>";
	}

	/**
	 * Returns string representation of this TSP state with underscores as separators.
	 * 
	 * @return string representation of this TSP state with underscores as separators
	 */
	private String toStringUnderscoreSeparated() {
		StringBuilder builder = new StringBuilder();
		builder.append(Integer.toString(routeAsIds.get(0)));
		for (int i = 1; i < routeAsIds.size(); i++)
			builder.append("_" + routeAsIds.get(i));
		return builder.toString();
	}

	/**
	 * Saves this TSP state as a gif image.
	 * 
	 * @param path wanted destination
	 * @throws Exception if something during file writing goes wrong
	 */
	public void saveAsImage(String path) throws Exception {
		RenderedImage image = toImage();
		File file = new File(path);
		ImageIO.write(image, "gif", file);
	}

	/**
	 * Returns this TSP state as an image.
	 * 
	 * @return this TSP state as an image
	 */
	public RenderedImage toImage() {
		RenderedImage image = map.toImage();
		if ((mst != null) && (!mst.getConnections().isEmpty()))
			image = Map.addConnectionsToImage(mst.getConnections(), Color.LIGHT_GRAY, image);
		if (bestConnectionToMST != null) {
			List<Connection> list = new LinkedList<Connection>();
			list.add(bestConnectionToMST);
			image = Map.addConnectionsToImage(list, Color.LIGHT_GRAY, image);
		}
		image = Map.addConnectionsToImage(route, Color.BLACK, image);
		return image;
	}	
}