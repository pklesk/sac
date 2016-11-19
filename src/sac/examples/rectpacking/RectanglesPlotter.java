package sac.examples.rectpacking;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Plotter for rectangles.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class RectanglesPlotter extends JPanel {

	private static final long serialVersionUID = 1L;
	Container container;

	public RectanglesPlotter(Container container) {
		super();
		this.container = container;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(this.container.toImage(), 0, 0, null);
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension((int) Math.round(this.container.getWidth()), (int) Math.round(this.container.getHeight()));
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
}
