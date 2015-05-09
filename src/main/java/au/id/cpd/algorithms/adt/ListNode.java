/** @(#)ListNode.java 2006-03-04
 * 
 */
package au.id.cpd.algorithms.adt;

/**
 * Node for use in a linked list.
 * @author Chris Davey
 * @version 0.1 2006-03-06
 */
public class ListNode {

	private Object _data;
	private ListNode _edge;
	/**
	 * Default constructor.
	 *
	 */
	public ListNode()
	{
		this._edge = null;
	}
	/**
	 * Construct with data value.
	 * @param data
	 */
	public ListNode(Object data)
	{
		this._edge = null;
		this._data = data;
	}
	/**
	 * Construct with data value and next node.
	 * @param data
	 * @param edge
	 */
	public ListNode(Object data, ListNode edge)
	{
		this._edge = edge;
		this._data = data;
	}
	/**
	 * Access the current data value.
	 * @return
	 */
	public Object getData()
	{
		return this._data;
	}
	/**
	 * Define the current data value.
	 * @param data
	 */
	public void setData(Object data)
	{
		this._data = data;
	}
	/**
	 * Access the next connected node.
	 * @return
	 */
	public ListNode getOutEdge()
	{
		return this._edge;
	}
	/**
	 * Define the next connected node.
	 * @param edge
	 */
	public void setOutEdge(ListNode edge)
	{
		this._edge = edge;
	}
	/**
	 * Append a data value to the current node.
	 * Will continue to recurse until an avalable position is found.
	 * List will grow in size.
	 * @param data
	 */
	public void append(Object data)
	{
		if (this._edge == null)
			this._edge = new ListNode(data);
		else
			this._edge.append(data);
	}
	/**
	 * Insert a value at the supplied position
	 * for the supplied count 
	 * @param data
	 * @param position
	 * @param count
	 * @return
	 */
	public ListNode insert(Object data, int position, int count)
	{
		if (count == position)
		{
			ListNode node = new ListNode(data);
			ListNode tmp = this;
			node.setOutEdge(tmp);
			return node;
		} else if (this._edge != null) {
			this._edge = this._edge.insert(data, position, ++count);
		}
		return this;
	}
	/**
	 * Remove a value from the list.
	 * Uses the Object.equals method to test for equivalence.
	 * @param data
	 * @return
	 */
	public Object remove(Object data)
	{
		if (this._edge == null)
			return null;
		if (this._edge.getData().equals(data))
		{
			ListNode tmp = this._edge;
			if (this._edge.getOutEdge() != null)
			{
				this._edge = this._edge.getOutEdge();
			} else {
				this._edge = null;
			}
			return tmp.getData();
		} else {
			return this._edge.remove(data);
		}
	}
	/**
	 * Remove an object at the supplied position and count.
	 * @param position
	 * @param count
	 * @return
	 */
	public Object removeAt(int position, int count)
	{
		if (this._edge == null)
			return null;
		if (position == count)
		{
			ListNode tmp = this._edge;
			this._edge = this._edge.getOutEdge();
			return tmp.getData();
		} else {
			return this._edge.removeAt(position, ++count);
		}
	}
	/**
	 * Access the current size of the list.
	 * @return
	 */
	public int size()
	{
		if (this._edge != null)
			return 1 + this._edge.size();
		return 1;
	}
	/**
	 * Get an object at the requested permission.
	 * @param position
	 * @param count
	 * @return
	 */
	public Object get(int position, int count)
	{
		if (position == count)
		{
			return this._data;
		} else if (this._edge != null) {
			return this._edge.get(position, ++count);
		}
		return null;
	}
	/**
	 * Find a node that equals the supplied test value.
	 * Uses the Object.equals method to determine equivalence.
	 * @param test
	 * @return
	 */
	public Object find(Object test)
	{
		if (this._data.equals(test))
			return this._data;
		else if (this._edge != null)
			return this._edge.find(test);
		return null;
	}
	/**
	 * Access a string representation of the list.
	 * 
	 * @return String
	 */
	public String toString()
	{
		if (this._edge != null)
			return this._data.toString()+this._edge.toString();
		return this._data.toString();
	}
	
}
