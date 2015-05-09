/**
 * 
 */
package au.id.cpd.algorithms.adt;

/**
 * @author Chris Davey cd@cpd.id.au
 * @version 1.0
 *
 */
public class Node<T> implements Comparable, java.io.Serializable {

	/**
	 * Serial version UI.
	 */
	static final long serialVersionUID = -7319746030157337717L;
	/**
	 * Internal data instance.
	 */
	private T data;
	/**
	 * Internal flag for visited.
	 */
	private boolean isVisited;
	/**
	 * Default constructor.
	 *
	 */
	public Node() {
		this.data = null;
		this.isVisited = false;
	}
	/**
	 * Data must implement comparable.
	 * @param data
	 * @throws Exception
	 */
	public Node(T data) throws Exception {
		if (!(data instanceof Comparable)) {
			throw new Exception("Data must implement Comparable.");
		}
		this.data = data;
		this.isVisited = false;
	}
	
	public T getData() {
		return this.data;
	}
	public void setData(T data) throws Exception {
		if (!(data instanceof Comparable)) {
			throw new Exception("Data must implement Comparable.");
		}
		this.data = data;
	}
	/**
	 * CompareTo implementation.
	 * @see Comparable.compareTo
	 */
	public int compareTo(Object o) {
		Comparable<T> to = (Comparable<T>)o;
		if (to.compareTo(this.data) < 0) {
			return 1;
		} else if (to.compareTo(this.data) > 0) {
			return -1;
		}
		return 0;
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
	/**
	 * @return the isVisited
	 */
	public boolean isVisited() {
		return isVisited;
	}
	/**
	 * @param isVisited the isVisited to set
	 */
	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}
}
