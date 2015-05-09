/**
 * 
 */
package au.id.cpd.algorithms.data;

/**
 * @author cd
 *
 */
public class Tuple<K,V> implements ITuple<K,V> {

	 static final long serialVersionUID = -4966381416684736919L;
	
	private Comparable<K> key;
	
	private V value;
	
	/**
	 * Default ctor
	 */
	public Tuple() {
		
	}
	
	/**
	 * Instantiate with a simple key value pair.
	 * @param k
	 * @param v
	 */
	public Tuple(Comparable<K> k, V v) {
		key = k;
		value = v;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.ITuple#getKey()
	 */
	public Comparable<K> getKey() {
		// TODO Auto-generated method stub
		return key;
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.ITuple#getValue()
	 */
	public V getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.ITuple#setKey(java.lang.Object)
	 */
	public void setKey(Comparable<K> k) {
		// TODO Auto-generated method stub
		this.key = k;
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.ITuple#setValue()
	 */
	public void setValue(V val) {
		// TODO Auto-generated method stub
		this.value = val;
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

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(K o) {
		return key.compareTo(o);
	}
	
}
