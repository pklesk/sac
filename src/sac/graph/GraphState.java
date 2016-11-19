package sac.graph;

import java.util.List;

import sac.State;

/**
 * An abstract state in some graph search problem.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public interface GraphState extends State {

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#getParent()
	 */
	@Override
	public GraphState getParent();

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#getChildren()
	 */
	@Override
	public List<GraphState> getChildren();

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#getPath()
	 */
	@Override
	public List<GraphState> getPath();

	/*
	 * (non-Javadoc)
	 * 
	 * @see sac.State#genereateChildren()
	 */
	@Override
	public List<GraphState> generateChildren();

	/**
	 * Returns the exact distance from the initial state to this state.
	 * 
	 * @return exact distance from initial state
	 */
	public double getG();

	/**
	 * Returns the sum of g and h.
	 * 
	 * @return sum of g and h
	 */
	public double getF();

	/**
	 * Returns true when this state is a solution, false otherwise.
	 * 
	 * @return true when state is solution, false otherwise
	 */
	public boolean isSolution();

	/**
	 * Updates g, h, f values.
	 */
	public void refreshCosts();
}