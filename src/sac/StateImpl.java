package sac;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract partial implementation of State interface.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public abstract class StateImpl implements State {

	/**
	 * Identifier for this state.
	 */
	protected Identifier identifier = null;

	/**
	 * Reference to this state's parent.
	 */
	protected State parent = null;

	/**
	 * List of references to this state's children.
	 */
	protected List<? extends State> children = null;

	/**
	 * Depth of this state (number of parent states above it).
	 */
	protected double depth = 0;

	/**
	 * The heuristics - estimated distance to the solution state. Remains null until the first call of getH().
	 */
	protected Double h = null;

	/**
	 * Name of the move that led to generating this state.
	 */
	protected String moveName = null;

	/**
	 * Default h function (returns 0).
	 */
	protected static StateFunction hFunction = null;

	/**
	 * Constructor for this abstract class. Sets reference to parent to null, and initializes children as an empty list
	 * (linked list).
	 */
	public StateImpl() {
		// construction of identifier is postponed until first call of getIdentifier()
		this.parent = null;
		this.children = new LinkedList<State>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#getIdentifier()
	 */
	@Override
	public final Identifier getIdentifier() {
		if (identifier == null) { // first call for identifier
			// at this point toString() and hashCode() methods for classes extending StateImpl are
			// ready to be used,
			// identifier will call the suitable method once (on construction) and memorize the
			// result
			identifier = new Identifier(this);
		}
		return identifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#refreshIdentifier()
	 */
	@Override
	public final void refreshIdentifier() {
		identifier = new Identifier(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#getParent()
	 */
	@Override
	public State getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#setParent(sac.State)
	 */
	@Override
	public final void setParent(State parent) {
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#setDepth(double)
	 */
	@Override
	public final void setDepth(double depth) {
		this.depth = depth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#getChildren()
	 */
	@Override
	public List<? extends State> getChildren() {
		return children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#getDepth()
	 */
	@Override
	public final double getDepth() {
		return depth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#getPath()
	 */
	@Override
	public List<? extends State> getPath() {
		List<State> path = new LinkedList<State>();
		State temp = this;
		path.add(temp);
		while (temp.getParent() != null) {
			temp = temp.getParent();
			path.add(temp);
		}
		Collections.reverse(path);
		return path;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#getMovesAlongPath()
	 */
	@Override
	public final List<String> getMovesAlongPath() {
		List<String> moves = new LinkedList<String>();
		List<? extends State> path = getPath();
		for (State state : path) {
			if (state.getParent() == null) continue;
			moves.add(state.getMoveName());
		}
		return moves;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object otherState) {
		State otherState2 = (State) otherState;
		return getIdentifier().equals(otherState2.getIdentifier());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(State otherState) {
		return getIdentifier().compareTo(otherState.getIdentifier());
	}

	/**
	 * Sets new h function.
	 * 
	 * @param hFunction to best
	 */
	public static final void setHFunction(StateFunction hFunction) {
		StateImpl.hFunction = hFunction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#getH()
	 */
	@Override
	public final double getH() {
		if (h == null)
			h = Double.valueOf(hFunction.calculate(this));
		return h;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#setH(Double)
	 */
	@Override
	public final void setH(Double h) {
		this.h = h;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#refreshH()
	 */
	@Override
	public final void refreshH() {
		h = null;
		hFunction.calculate(this);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#refresh()
	 */	
	@Override
	public void refresh() {
		refreshIdentifier();
		refreshH();		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#getMoveName()
	 */
	@Override
	public final String getMoveName() {
		return (moveName != null) ? moveName : getIdentifier().toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#setMoveName(java.lang.String)
	 */
	@Override
	public final void setMoveName(String moveName) {
		this.moveName = moveName;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graphviz.Graphvizable#toGraphvizLabel()
	 */
	@Override
	public String toGraphvizLabel() {
		return getIdentifier().toString(); // default content for visualization - identifier
	}
	
	static {
		hFunction = new StateFunction();
	}
}