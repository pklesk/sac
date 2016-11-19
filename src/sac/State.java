package sac;

import java.util.List;

import sac.graphviz.Graphvizable;

/**
 * An abstract state in some search problem, common for graphs and game trees.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public interface State extends Comparable<State>, Graphvizable {

	/**
	 * Returns the identifier for this state.
	 * 
	 * @return identifier
	 */
	public Identifier getIdentifier();

	/**
	 * Forces a refresh on identifier. This method may be useful when in the runtime a change is made on the type of
	 * identifiers (static field of Identifier class). Before executing a search, any search algorithm should refresh
	 * the identifier of the initial state.
	 */
	public void refreshIdentifier();

	/**
	 * Returns the reference to this state's parent.
	 * 
	 * @return parent of this state
	 */
	public State getParent();

	/**
	 * Sets reference to parent state for this state.
	 * 
	 * @param parent reference to parent
	 */
	public void setParent(State parent);

	/**
	 * Returns the list of references to children (descendants) of this state.
	 * 
	 * @return list of references to children
	 */
	public List<? extends State> getChildren();

	/**
	 * Returns the depth of this state (number of parent states above it)
	 * 
	 * @return depth of this state
	 */
	public double getDepth();

	/**
	 * Sets depth of this state.
	 * 
	 * @param depth to be set
	 */
	public void setDepth(double depth);

	/**
	 * Returns the path for this state (reversed list of references to successive parents upwards).
	 * 
	 * @return path
	 */
	public List<? extends State> getPath();

	/**
	 * Returns a sequence of move names indicating how this state was reached from the initial state.
	 * 
	 * @return sequence of move names
	 */
	public List<String> getMovesAlongPath();

	/**
	 * Generates and returns children (descendants) for this state. This method is meant to be implemented by user and
	 * called by search algorithms.
	 * 
	 * @return generated children
	 */
	public List<? extends State> generateChildren();

	/**
	 * Returns the value of heuristics for this state. For graph searches, heuristics is an estimated distance from this
	 * state to the goal state. For game tree searches heuristics is an evaluation of the game position represented by
	 * this state (positive values indicate an advantage of the maximizing player, negative values indicate an advantege
	 * of the minimizing player).
	 * 
	 * @return value of heuristics for this state
	 */
	public double getH();

	/**
	 * Explicitly sets the value of heuristics for this state. This method is meant only for special purposes i.e. to be
	 * called only from within the SaC API. E.g. in game searches, it is necessary to introduce a grading of infinite
	 * (win) values of heuristics. Thus, the user should typically use only getH() and refresh() methods.
	 * 
	 * @param h value of heuristics to be set
	 */
	public void setH(Double h);

	/**
	 * Forces a refresh of the heuristics value (in case some manipulation or move on this state has been made).
	 */
	public void refreshH();

	/**
	 * Returns the name of the move (or operation) that led to generating this state.
	 * 
	 * @return name of the move that led to generating this state.
	 */
	public String getMoveName();

	/**
	 * Sets the name of the move (or operation) that led to generating this state.
	 * 
	 * @param moveName move name to be set
	 */
	public void setMoveName(String moveName);

	/**
	 * Forces a refresh of both the identifier and the heuristics for this state.
	 */
	public void refresh();
}