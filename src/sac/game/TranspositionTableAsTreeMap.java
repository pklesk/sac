package sac.game;

import java.util.TreeMap;


/**
 * Implementation of transposition table based on tree map (red-black tree).
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class TranspositionTableAsTreeMap extends TranspositionTableImpl {

	/**
	 * Creates a new instance of TranspositionTableAsHashMap.
	 */
	public TranspositionTableAsTreeMap() {
		this.map = new TreeMap<TranspositionTableKey, TranspositionTableEntry>();
	}	
}