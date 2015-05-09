/** @(#)LinkedList.java 2006-03-04
 * 
 */
package au.id.cpd.algorithms.adt;

/**
 * A simple LinkedList implementation.
 * redundant since java.util.LinkedList can be used instead.
 * @author Chris Davey <cd@cpd.id.au>
 * @version 0.1 2006-03-06
 */
public class LinkedList {

	ListNode _root;
	/**
	 * Default Constructor
	 *
	 */
	public LinkedList()
	{
		this._root = null;
	}
	/**
	 * Add an object to the list.
	 * 
	 * @param data
	 */
	public void append(Object data)
	{
		if (this._root != null)
			this._root.append(data);
		else 
			this._root = new ListNode(data);
	}
	
	/** insert
	 * Insert an object into the list at the given position.
	 * @param data
	 * @param position - position must be between 0 and size of list.
	 */
	public void insert(Object data, int position)
	{
		if (this._root != null)
		{
			ListNode object = this._root.insert(data, position, 0);
			if (position == 0)
			{
				this._root = object;
			}
		}
	}
	/**
	 * Remove an object from the list.
	 * Uses the Object.equals method to determine equivalence.
	 * @param data
	 * @return Object value of element removed from list.
	 */
	public Object remove(Object data)
	{
		if (this._root != null)
			return this._root.remove(data);
		return null;
	}
	/**
	 * Remove an object from the list at the supplied position.
	 * @param position
	 * @return Object value of element removed from list.
	 */
	public Object removeAt(int position)
	{
		if (this._root != null)
		{
			if (position == 0)
			{
				ListNode tmp = this._root;
				this._root = this._root.getOutEdge();
				return tmp.getData();
			} else {
				return this._root.removeAt(position, 1);
			}
		}
		return null;
	}
	
	/**
	 * reset the list to an empty list.
	 * Discard all nodes.
	 *
	 */
	public void reset()
	{
		this._root = null;
	}
	/**
	 * Access the size of the list.
	 * @return int
	 */
	public int size()
	{
		if (this._root != null)
			return this._root.size();
		return 0;
	}
	/**
	 * Access an object at the position in the list.
	 * @param index must be >=0 < List size
	 * @return Object value of element at supplied positon or null.
	 * @throws IndexOutOfBoundsException
	 */
	public Object get(int index) throws IndexOutOfBoundsException
	{
		if (this._root != null)
			return this._root.get(index, 0);
		throw new IndexOutOfBoundsException(index+" out of bounds List size is: "+this.size());
	}
	
	/**
	 * Find an object of the supplied value from the list.
	 * Uses the Object.equals method to test for equivalence.
	 * @param test
	 * @return Object value of matching element in list.
	 */
	public Object find(Object test)
	{
		if (this._root != null)
			return this._root.find(test);
		return null;
	}
	/**
	 * Return a string representation of the list and all nodes in the list.
	 * @return string
	 */
	public String toString()
	{
		if (this._root != null)
			return this._root.toString();
		return "";
	}
	
	
}
