package sac.graphviz;

/**
 * Types according to which Graphviz software (http://www.graphviz.org) renders graph nodes.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public enum GraphvizNodeType {
	WITHOUT_CONTENT("point"), WITH_CONTENT("none");

	private String asString;

	GraphvizNodeType(String asString) {
		this.asString = asString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return asString;
	}
}