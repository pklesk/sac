package sac.game;

import java.util.HashMap;

/**
 * Implementation of transposition table based on hash map.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class TranspositionTableAsHashMap extends TranspositionTableImpl {

	/**
	 * Creates a new instance of TranspositionTableAsHashMap.
	 */
	public TranspositionTableAsHashMap() {
		this.map = new HashMap<TranspositionTableKey, TranspositionTableEntry>(512 * 1024, (float) 0.75);
	}
}