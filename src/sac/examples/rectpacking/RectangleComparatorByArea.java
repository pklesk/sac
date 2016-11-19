package sac.examples.rectpacking;

import java.util.Comparator;

public class RectangleComparatorByArea implements Comparator<Rectangle> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Rectangle o1, Rectangle o2) {
		return Double.compare(o2.getArea(), o1.getArea());
	}
}