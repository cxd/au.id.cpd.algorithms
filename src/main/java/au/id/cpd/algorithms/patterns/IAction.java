package au.id.cpd.algorithms.patterns;

import au.id.cpd.algorithms.patterns.IState;

/**
 * IAction of type <C>
 * C is the Criteria of the State.
 * Use of IConstraint is optional.
 * @author cd
 *
 * @param <C>
 */
public interface IAction<C> {

	/**
	 * Take the action and return the resulting state.
	 * @return
	 */
	public abstract IState<?, C> performAction();

	/**
	 * Access the current state.
	 * @return
	 */
	public abstract IState<?, C> getState();

	/**
	 * Define the state.
	 * @param s
	 */
	public abstract void setState(IState<?, C> s);

}