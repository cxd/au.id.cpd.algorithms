/**
 * 
 */
package au.id.cpd.algorithms.patterns;

/**
 * A provider for actions of type T.
 * @author cd
 *
 */
public interface IActionProvider<T> {
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
