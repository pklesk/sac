package sac;

/**
 * An object meant to identify states during search algorithms.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public final class Identifier implements Comparable<Identifier> {

	/**
	 * Global-scope parameter deciding how states are identified.
	 */
	private static IdentifierType type = IdentifierType.HASH_CODE;

	/**
	 * Actual id.
	 */
	private Object id = null;

	/**
	 * Creates new identifier for given state.
	 * 
	 * @param state to be identified
	 */
	public Identifier(State state) {
		id = (type == IdentifierType.HASH_CODE) ? Integer.valueOf(state.hashCode()) : state.toString();
	}

	/**
	 * Returns identifier type being used.
	 * 
	 * @return identifier type being used
	 */
	public static final IdentifierType getType() {
		return type;
	}

	/**
	 * Sets identifier type to be used.
	 * 
	 * @param type to be set
	 */
	public static final void setType(IdentifierType type) {
		Identifier.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Identifier otherIdentifier) {
		if (type == IdentifierType.HASH_CODE)
			return ((Integer) id).compareTo((Integer) otherIdentifier.id);
		else
			return ((String) id).compareTo((String) otherIdentifier.id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object otherIdentifier) {
		Identifier otherIdentifier2 = (Identifier) otherIdentifier;
		if (type == IdentifierType.HASH_CODE)
			return ((Integer) id).equals((Integer) otherIdentifier2.id);
		else
			return ((String) id).equals((String) otherIdentifier2.id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = (type == IdentifierType.HASH_CODE) ? ((Integer) id).intValue() : ((String) id).hashCode();
		return result;
	}
}