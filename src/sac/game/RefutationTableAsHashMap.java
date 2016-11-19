package sac.game;

import java.util.HashMap;

import sac.Identifier;

/**
 * Implementaiton of refutation table based on hash map.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class RefutationTableAsHashMap extends RefutationTableImpl {

	/**
	 * Creates new instance of RefutationTableAsHashMap with a default depth limit.
	 */
	public RefutationTableAsHashMap() {
		super();
		tableToSave = new HashMap<Identifier, Identifier>();
		tableToRead = new HashMap<Identifier, Identifier>();
	}

	/**
	 * Creates new instance of RefutationTableAsHashMap.
	 * 
	 * @param depthLimit depth limit
	 */
	public RefutationTableAsHashMap(double depthLimit) {
		super(depthLimit);
		tableToSave = new HashMap<Identifier, Identifier>();
		tableToRead = new HashMap<Identifier, Identifier>();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.game.RefutationTable#reset()
	 */
	@Override
	public void reset() {
		tableToRead = tableToSave;
		tableToSave = new HashMap<Identifier, Identifier>();
	}
}