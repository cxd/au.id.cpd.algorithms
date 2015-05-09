/**
 * 
 */
package au.id.cpd.algorithms.patterns.agency;


import au.id.cpd.algorithms.patterns.*;
/**
 * A simple reactive agent.
 * 
 * A reactive agent interprets state
 * and takes action causing the state to change.
 * 
 * @author cd
 *
 */
public interface IAgent<T> extends Observer {

	/**
	 * Define the action provider for this agent.
	 * @return
	 */
	public IActionProvider<T> getActionProvider();
	/**
	 * Define the action provider for this agent.
	 * @return
	 */
	public void setActionProvider(IActionProvider<T> a);
	
	/**
	 * The current state.
	 * @return
	 */
	public IState<?,T> getState();
	
	/**
	 * Set the current state.
	 * @param st
	 */
	public void setState(IState<?,T> st);
	
	/**
	 * The current action it is created internally by the agent.
	 * @return
	 */
	public IAction<T> getAction();
	
	/**
	 * Perform a selected action on the current state
	 * @return
	 */
	public IState<?,T> performAction();
	
	/**
	 * Perform a selected action on the supplied state.
	 * @param st
	 * @return
	 */
	public IState<?,T> performAction(IState<?,T> st);
	
	/**
	 * Select the next action to perform for the current state.
	 * @param st
	 * @return
	 */
	public IAction<T> selectAction(IState<?, T> st);
	
	/**
	 * Select the next action to perform for the current state.
	 * @return
	 */
	public IAction<T> selectAction();
	
}
