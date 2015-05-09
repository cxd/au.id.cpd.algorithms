/**
 * 
 */
package au.id.cpd.algorithms.data;

import java.util.*;

/**
 * @author cd
 *
 */
public interface ITuple<K,V>  extends Comparable<K>, java.io.Serializable {

	/**
	 * Access the tuple key.
	 * @return
	 */
	public Comparable<K> getKey();
	/**
	 * Define the tuple key.
	 * @param key
	 * @return
	 */
	public void setKey(Comparable<K> key);
	
	/**
	 * Get the tuple value.
	 * @return
	 */
	public V getValue();
	/**
	 * set the tuple value.
	 * @return
	 */
	public void setValue(V val);
}
