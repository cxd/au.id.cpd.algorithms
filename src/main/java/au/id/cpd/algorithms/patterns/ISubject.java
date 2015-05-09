/**
 * 
 */
package au.id.cpd.algorithms.patterns;

import java.util.Iterator;

/**
 * @author cd
 *
 */
public interface ISubject {
	/** update
	 * Iterate over the collection of registered Observers
	 * and notify each observer in turn.
	 */
	public void update();
	
	/** update
	 * Iterate over the collection of registered observers
	 * and notify each observer in turn with the supplied argument.
	 * @param argument - object argument to supply to observers.
	 */
	public void update(Object argument);
	
	/** add
	 * Register an Observer instance with the Subject instance.
	 * @param registrant
	 */
	public void addObserver(Observer registrant);
	
	/** remove
	 * Remove a registered Observer instance from the Subject's collection
	 * of Observers. 
	 * @param registrant
	 */
	public void removeObserver(Observer registrant);
	
}
