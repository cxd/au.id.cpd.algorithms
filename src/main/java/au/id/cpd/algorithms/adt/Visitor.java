/** @(#)Visitor.java 2006-03-05
 * 
 */
package au.id.cpd.algorithms.adt;

/**
 * @author Chris Davey <cd@cpd.id.au>
 * @version 0.1 2006-03-06
 */
public interface Visitor {
	/** visit
	 * An ADT can be traversed by a visitor instance.
	 * The node can be processed during traversal.
	 * @param node
	 */
	public void visit(Object node);
}
