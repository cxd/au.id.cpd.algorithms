/**
 * 
 */
package au.id.cpd.algorithms.data;

import java.util.*;

/**
 * @author cd
 *
 */
public class TupleCollection<K, V> extends ArrayList<ITuple<K,V>> implements ITupleCollection<K,V>  {

	static final long serialVersionUID = 590246651188642227L;
	
	/**
	 * Default constructor.
	 */
	public TupleCollection() {
		super();
	}
	
	/**
	 * java.io.Serializable.readObject(ObjectInputStream is)
	 */
	private void readObject(java.io.ObjectInputStream is) throws ClassNotFoundException, java.io.IOException {
		is.defaultReadObject();
	}
	/**
	 * java.io.Serializable.writeObject(ObjectOutputStream os)
	 */
	private void writeObject(java.io.ObjectOutputStream os) throws ClassNotFoundException, java.io.IOException {
		os.defaultWriteObject();
	}


}
