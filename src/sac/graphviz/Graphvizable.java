package sac.graphviz;

/**
 * An interface allowing states to be visualized (as parts of whole graphs or trees) via Graphviz software
 * (http://www.graphviz.org).
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public interface Graphvizable {

	/**
	 * Returns a representation of a state (pure string or HTML) for Graphviz visualization. To be overridden by the
	 * user.
	 * 
	 * @return representation of a state (pure string or HTML)
	 */
	public String toGraphvizLabel();
}