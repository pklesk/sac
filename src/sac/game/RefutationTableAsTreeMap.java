package sac.game;

import java.util.TreeMap;

import sac.Identifier;

/**
 * Implementation of refutation table based on tree map (red-black tree).
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class RefutationTableAsTreeMap extends RefutationTableImpl {

	/**
	 * Creates new instance of RefutationTableAsHashMap with a default depth limit.
	 */
	public RefutationTableAsTreeMap() {
		super();
		tableToSave = new TreeMap<Identifier, Identifier>();
		tableToRead = new TreeMap<Identifier, Identifier>();
	}

	/**
	 * Creates new instance of RefutationTableAsHashMap.
	 * 
	 * @param depthLimit depth limit
	 */
	public RefutationTableAsTreeMap(double depthLimit) {
		super(depthLimit);
		tableToSave = new TreeMap<Identifier, Identifier>();
		tableToRead = new TreeMap<Identifier, Identifier>();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.RefutationTable#reset()
	 */
	@Override
	public void reset() {
		tableToRead = tableToSave;
		tableToSave = new TreeMap<Identifier, Identifier>();
	}
}