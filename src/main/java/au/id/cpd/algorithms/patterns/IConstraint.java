/**
 * 
 */
package au.id.cpd.algorithms.patterns;

/**
 * 
 * A constraint can be Applied to type
 * of Domain D in order to test is Domain D satisfies Constraint C 
 * 
 * @author cd
 *
 */
public interface IConstraint<D> {

	/**
	 * Apply Constraint C to Domain D
	 * @param domain
	 * @return true if Domain satisfies Constraint
	 */
	public boolean apply(D domain);
	
	/**
	 * Apply Constraint C
	 * implies the Domain D is already defined.
	 * @return
	 */
	public boolean apply();
	
	/**
	 * Domain property
	 * @return
	 */
	public D getDomain();
	/**
	 * Domain property.
	 * @param d
	 */
	public void setDomain(D d);
	
}
