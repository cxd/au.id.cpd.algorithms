/**
 * 
 */
package au.id.cpd.algorithms.patterns;

/**
 * Adapt a Source S to produce a Product P.
 * 
 * @author cd
 *
 */
public interface IAdapter<S,P> {

	/**
	 * Product property
	 * @return
	 */
	public P getProduct();
	
	/**
	 * Product property
	 * @param p
	 */
	public void setProduct(P p);
	
	/**
	 * Source property
	 * @return
	 */
	public S getSource();
	
	/**
	 * Source property
	 * @param s
	 */
	public void setSource(S s);
	/**
	 * Adapt a Source S to produce a Product P.
	 * @return Product P
	 */
	public P adapt(S s);
	/**
	 * Adapt a Source S to produce a Product P.
	 * @return Product P
	 */
	public P adapt();
	
}
