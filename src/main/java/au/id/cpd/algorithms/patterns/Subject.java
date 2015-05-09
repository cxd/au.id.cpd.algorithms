/** @(#)Subject.java 2005-03-04
 * 
 */
package au.id.cpd.algorithms.patterns;

import java.util.*;

/** Subject
 * 
 * @author Chris Davey <cd@cpd.id.au>
 * @version 0.1 2006-03-06
 * A Subject pattern that allows a set of registered Observer
 * classes to be notified whenever a state change occurs.
 * 
 */
public class Subject implements ISubject {

	/** _observers
	 * Internal collection of Observers.
	 */
	private ArrayList _observers;

	/** Subject
	 * Constructor.
	 */
	public Subject() {
		this._observers = new ArrayList();
	}

	/** update
	 * Iterate over the collection of registered Observers
	 * and notify each observer in turn.
	 */
	public void update() {
		if (this._observers.size() == 0)
			return;
		Iterator lstItr = this._observers.iterator();
		Observer instance;
		while (lstItr.hasNext()) {
			instance = (Observer) lstItr.next();
			instance.notify(this);
		}
	}

	/** update
	 * Iterate over the collection of registered observers
	 * and notify each observer in turn with the supplied argument.
	 * @param argument - object argument to supply to observers.
	 */
	public void update(Object argument) {
		if (this._observers.size() == 0)
			return;
		Iterator lstItr = this._observers.iterator();
		Observer instance;
		while (lstItr.hasNext()) {
			instance = (Observer) lstItr.next();
			instance.notify(this, argument);
		}
	}

	/** add
	 * Register an Observer instance with the Subject instance.
	 * @param registrant
	 */
	public void addObserver(Observer registrant) {
		this._observers.add(registrant);
	}

	/** remove
	 * Remove a registered Observer instance from the Subject's collection
	 * of Observers. 
	 * @param registrant
	 */
	public void removeObserver(Observer registrant) {
		if (this._observers.contains(registrant)) {
			this._observers.remove(registrant);
		}
	}
}
