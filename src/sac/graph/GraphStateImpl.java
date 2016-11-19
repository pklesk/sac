package sac.graph;

import java.util.List;

import sac.State;
import sac.StateFunction;
import sac.StateImpl;

/**
 * Abstract partial implementation of GraphState interface. User's graph state classes should extend this class.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public abstract class GraphStateImpl extends StateImpl implements GraphState {

	/**
	 * The exact distance from the initial state.
	 */
	protected Double g = 0.0;

	/**
	 * The sum of g and h. Remains null until the first call of getF().
	 */
	protected Double f = null;

	/**
	 * Default g function (returns parent's g + 1).
	 */
	protected static StateFunction gFunction;

	/**
	 * Default implementation of the true cost function (g function). Returns parent's g (if exists) plus one. Suitable
	 * for problems where the number of moves is to be minimized.
	 */
	public static class GFunction extends StateFunction {
		/*
		 * (non-Javadoc)
		 * 
		 * @see sac.StateFunction#calculate(sac.State)
		 */
		@Override
		public double calculate(State state) {
			return (state.getParent() == null) ? 0.0 : ((GraphState) state.getParent()).getG() + 1.0;
		}
	}

	/**
	 * Sets new g function.
	 * 
	 * @param gFunction to be set
	 */
	public final static void setGFunction(StateFunction gFunction) {
		GraphStateImpl.gFunction = gFunction;
	}

	/**
	 * Creates a new instance of graph state.
	 */
	public GraphStateImpl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.GraphState#getParent()
	 */
	@Override
	public final GraphState getParent() {
		return (parent == null) ? null : (GraphState) parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.GraphState#getChildren()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final List<GraphState> getChildren() {
		return (List<GraphState>) children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.GraphState#getPath()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final List<GraphState> getPath() {
		return (List<GraphState>) super.getPath();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.GraphState#getG()
	 */
	@Override
	public final double getG() {
		if (g == null)
			g = Double.valueOf(gFunction.calculate(this));
		return g;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.GraphState#getF()
	 */
	@Override
	public final double getF() {
		if (f == null)
			f = Double.valueOf(getG() + getH());
		return f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.graph.GraphState#refreshCosts()
	 */
	@Override
	public final void refreshCosts() {
		g = Double.valueOf(gFunction.calculate(this));
		h = Double.valueOf(hFunction.calculate(this));
		f = Double.valueOf(getG() + getH());
	}

	static {
		gFunction = new GFunction();
	}
}