/** @(#)Observer.java 2006-03-04
 * 
 */
package au.id.cpd.algorithms.patterns;

/**
 * @author Chris Davey <cd@cpd.id.au>
 * @version 0.1 2006-03-06 An interface defining the Observer for use with the
 *          Subject class. The Observer is notified by the subject each time the
 *          subject changes state.
 * @see patterns.Subject
 */
public interface Observer {
	/**
	 * notify Observer is notified by the Subject each time the subject is
	 * updated.
	 * 
	 * @param sender -
	 *            originating Subject instance.
	 */
	void notify(ISubject sender);

	/**
	 * notify Observer is notified by the Subject each time the subject is
	 * updated. Java copies a reference to the source reference argument.
	 * Argument should provide an interface allowing the Observer to modify its
	 * internal fields.
	 * 
	 * @param sender -
	 *            Originaiting Subject instance.
	 * @param argument
	 */
	void notify(ISubject sender, Object argument);
}
