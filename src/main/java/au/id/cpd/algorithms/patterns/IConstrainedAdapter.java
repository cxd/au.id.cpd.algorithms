/**
 * 
 */
package au.id.cpd.algorithms.patterns;

/**
 * A constrained adapter 
 * is able to place a constraint upon an adapter.
 * Either prior to performing the adaptation
 * or after performing the adaptation.
 * 
 * It is possible to use it to check for preconditions
 * or postconditions of an operation depending on the implementation.
 * 
 * @author cd
 *
 */
public interface IConstrainedAdapter<D,S,P> extends IConstraint<D>, IAdapter<S,P> {

}
