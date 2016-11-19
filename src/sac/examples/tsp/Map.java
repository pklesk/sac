package sac.examples.tsp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;

/**
 * Map of places in a TSP problem.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class Map {

	/**
	 * Places sorted lexicographically (by their ids).
	 */
	private SortedMap<Integer, Place> places = null;

	/**
	 * Reference to the starting place.
	 */
	private Place startPlace = null;

	/**
	 * Connections sorted lexicographically (by ids of connected places)
	 */
	private SortedSet<Connection> connections = null;

	// drawing constants (to scale from real to screen coordinates)
	private static final double MIN_X = 0.0;
	private static final double MAX_X = 1.0;
	private static final double MIN_Y = 0.0;
	private static final double MAX_Y = 1.0;

	private static final int SCREEN_MIN_X = 0;
	private static final int SCREEN_MAX_X = 320;
	private static final int SCREEN_MIN_Y = 0;
	private static final int SCREEN_MAX_Y = 320;

	private static final int SCREEN_MARGIN = 8;

	private static final int PLACE_CIRCLE_RADIUS = 7;

	private static final String IN_FILE_PLACE_DELIMITER = "\n";
	private static final String IN_FILE_COORDINATES_DELIMITER = ",";

	private static double a, b, c, d;

	/**
	 * Creates a new map with random places within a unit square.
	 * 
	 * @param n number of places
	 */
	public Map(int n) {
		calculateDrawingConstants();

		places = new TreeMap<Integer, Place>();
		connections = new TreeSet<Connection>();

		// random places in [MIN_X, MAX_X] x [MIN_Y, MAX_Y]
		for (int i = 1; i <= n; i++) {
			Place place = new Place(i, MIN_X + Math.random() * (MAX_X - MIN_X), MIN_Y + Math.random() * (MAX_Y - MIN_Y));
			places.put(i, place);
		}

		calculateCosts();

		startPlace = places.get(1); // first place as start place
	}

	/**
	 * Creates a new map with the given list of places.
	 * 
	 * @param listOfPlaces given list of places
	 */
	public Map(List<Place> listOfPlaces) {
		doMap(listOfPlaces);
	}

	/**
	 * Creates a new map from a text file (where places are written in successive lines, and coordinates are comma
	 * separated).
	 * 
	 * @param filepath path to the text file with a map
	 * @throws Exception if something goes wrong while the file is read and parsed
	 */
	public Map(String filepath) throws Exception {
		File file = new File(filepath);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		StringBuilder placesAsString = new StringBuilder();
		while (true) {
			String line = bufferedReader.readLine();
			if (line == null)
				break;
			placesAsString.append(line).append("\n");
		}

		StringTokenizer placesTokenizer = new StringTokenizer(placesAsString.toString(), IN_FILE_PLACE_DELIMITER);
		List<Place> listOfPlaces = new LinkedList<Place>();
		int i = 0;
		while (placesTokenizer.hasMoreTokens()) {
			String placeAsString = placesTokenizer.nextToken();
			StringTokenizer coordinatesTokenizer = new StringTokenizer(placeAsString, IN_FILE_COORDINATES_DELIMITER);
			double x = Double.parseDouble(coordinatesTokenizer.nextToken());
			double y = Double.parseDouble(coordinatesTokenizer.nextToken());
			Place place = new Place(++i, x, y);
			listOfPlaces.add(place);
		}

		bufferedReader.close();
		fileReader.close();

		doMap(listOfPlaces);
	}

	/**
	 * A helper method for constructors - creates the collection of places and connections, and calculates the drawing
	 * constants.
	 * 
	 * @param listOfPlaces given list of places
	 */
	private void doMap(List<Place> listOfPlaces) {
		calculateDrawingConstants();

		places = new TreeMap<Integer, Place>();
		connections = new TreeSet<Connection>();

		// scaling places to fit to [MIN_X, MAX_X] x [MIN_Y, MAX_Y], and imposing identifiers
		int n = listOfPlaces.size();
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		for (Place place : listOfPlaces) {
			minX = Math.min(minX, place.getX());
			maxX = Math.max(maxX, place.getX());
			minY = Math.min(minY, place.getY());
			maxY = Math.max(maxY, place.getY());
		}

		for (int i = 1; i <= n; i++) {
			Place originalPlace = listOfPlaces.get(i - 1);
			Place place = new Place(i, MIN_X + (MAX_X - MIN_X) * (originalPlace.getX() - minX) / (maxX - minX), MIN_Y + (MAX_Y - MIN_Y)
					* (originalPlace.getY() - minY) / (maxY - minY));
			places.put(i, place);
		}

		calculateCosts();

		startPlace = places.get(1); // first place as start place
	}

	/**
	 * A helper method for constructors - creates the collection of connections.
	 * 
	 * @param listOfPlaces given list of places
	 */
	private void calculateCosts() {
		int n = places.size();
		for (int i = 1; i <= n; i++)
			for (int j = i + 1; j <= n; j++) {
				Place place1 = places.get(i);
				Place place2 = places.get(j);
				double cost = Math.sqrt((place1.getX() - place2.getX()) * (place1.getX() - place2.getX()) + (place1.getY() - place2.getY())
						* (place1.getY() - place2.getY()));
				Connection connection = new Connection(place1, place2, cost);
				connections.add(connection);
				place1.getConnections().add(connection);
				place2.getConnections().add(connection);
			}
	}

	/**
	 * A helper method for constructors - calculates the drawing constants.
	 */
	private static void calculateDrawingConstants() {
		a = (SCREEN_MAX_X - SCREEN_MIN_X - 2 * SCREEN_MARGIN) / (MAX_X - MIN_X);
		b = SCREEN_MIN_X + SCREEN_MARGIN - a * MIN_X;
		c = (SCREEN_MAX_Y - SCREEN_MIN_Y - 2 * SCREEN_MARGIN) / (MAX_Y - MIN_Y);
		d = SCREEN_MIN_Y + SCREEN_MARGIN - c * MIN_Y;
	}

	/**
	 * Returns places sorted lexicographically as a SortedMap.
	 * 
	 * @return places sorted lexicographically as a SortedMap
	 */
	public SortedMap<Integer, Place> getPlaces() {
		return places;
	}

	/**
	 * Returns reference to the starting place.
	 * 
	 * @return reference to the starting place
	 */
	public Place getStartPlace() {
		return startPlace;
	}

	/**
	 * Returns connections sorted lexicographically as a SortedSet.
	 * 
	 * @return connections sorted lexicographically as a SortedSet
	 */
	public SortedSet<Connection> getConnections() {
		return connections;
	}

	/**
	 * Calculates the x screen coordinate (for drawing purposes) for given real x coordinate.
	 * 
	 * @param x real x coordinate
	 * @return screen x coordinate
	 */
	private static int screenX(double x) {
		return (int) Math.round(a * x + b);
	}

	/**
	 * Calculates the y screen coordinate (for drawing purposes) for given real y coordinate.
	 * 
	 * @param y real y coordinate
	 * @return screen y coordinate
	 */
	private static int screenY(double y) {
		return (int) Math.round(c * y + d);
	}

	/**
	 * Returns the map (only places, no connections) as an image.
	 * 
	 * @return map (only places, no connections) as an image
	 */
	public RenderedImage toImage() {
		BufferedImage bufferedImage = new BufferedImage((int) (SCREEN_MAX_X - SCREEN_MIN_Y + 1), (int) (SCREEN_MAX_Y - SCREEN_MIN_Y + 1),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();

		// background
		g2d.setColor(Color.WHITE);
		g2d.fillRect(SCREEN_MIN_X, SCREEN_MIN_Y, SCREEN_MAX_X - SCREEN_MIN_X + 1, SCREEN_MAX_Y - SCREEN_MIN_Y + 1);

		// frame
		g2d.setColor(Color.BLACK);
		g2d.drawRect(SCREEN_MIN_X, SCREEN_MIN_Y, SCREEN_MAX_X - SCREEN_MIN_X, SCREEN_MAX_Y - SCREEN_MIN_Y);

		// places as circles
		for (Place place : places.values()) {
			g2d.setColor(Color.BLACK);
			g2d.fillOval(screenX(place.getX()) - PLACE_CIRCLE_RADIUS / 2, screenY(place.getY()) - PLACE_CIRCLE_RADIUS / 2, PLACE_CIRCLE_RADIUS,
					PLACE_CIRCLE_RADIUS);
		}

		// places ids
		Font font = new Font("Courier", Font.PLAIN, 9);		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(font);
		g2d.setColor(Color.BLACK);
		for (Place place : places.values())
			g2d.drawString(Integer.toString(place.getId()), screenX(place.getX()) + PLACE_CIRCLE_RADIUS, screenY(place.getY()));

		g2d.dispose();
		return bufferedImage;
	}

	/**
	 * Adds connections to given image and returns it.
	 *
	 * @param connections list of connections
	 * @param color to be used
	 * @param image object
	 * @return map as an image
	 */
	public static RenderedImage addConnectionsToImage(List<Connection> connections, Color color, RenderedImage image) {
		BufferedImage bufferedImage = convertRenderedImage(image);
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setColor(color);
		for (Connection connection : connections) {
			g2d.drawLine(screenX(connection.getPlace1().getX()), screenY(connection.getPlace1().getY()), screenX(connection.getPlace2().getX()),
					screenY(connection.getPlace2().getY()));
		}

		g2d.dispose();
		return bufferedImage;
	}

	/**
	 * A helper method - converts rendered image to a buffered image.
	 * 
	 * @param img reference to a rendered image
	 * @return buffered image
	 */
	private static BufferedImage convertRenderedImage(RenderedImage img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		ColorModel cm = img.getColorModel();
		int width = img.getWidth();
		int height = img.getHeight();
		WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();

		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		String[] keys = img.getPropertyNames();
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				properties.put(keys[i], img.getProperty(keys[i]));
			}
		}
		BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
		img.copyData(raster);
		return result;
	}

	/**
	 * Saves map at given path as a gif file.
	 * 
	 * @param path path at which the map should be saved
	 * @throws Exception if something during file writing goes wrong
	 */
	public void saveAsImage(String path) throws Exception {
		RenderedImage image = toImage();
		File file = new File(path);
		ImageIO.write(image, "gif", file);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("");
		int i = 0;
		for (java.util.Map.Entry<Integer, Place> entry : places.entrySet()) {
			Integer id = entry.getKey();
			Place place = entry.getValue();
			result.append(id + ": (" + place.getX() + ", " + place.getY() + ")");
			if ((++i) < places.size())
				result.append("\n");
		}
		return result.toString();
	}
}