/** @(#)TrieNode.java 2006-03-05
 *  
 */
package au.id.cpd.algorithms.adt;

import java.util.*;

/**
 * TrieNode is a single node in the trie.
 * It contains a link to sibling nodes at the current level,
 * a link to the lower child level (children) and maintains
 * a connection to the upper level of the trie through a 
 * link to its immediate parent.
 * @author Chris Davey <cd@cpd.id.au>
 * @version 0.1 2006-03-06
 */
public class TrieNode {
	
	private TrieNode _sibling;
	private TrieNode _children;
	private TrieNode _parent;
	private Comparable _data;
	private boolean _isFinal;
	private int _count;
	private int _finalCount; 
	
	/** TrieNode
	 * Default constructor.
	 *
	 */
	public TrieNode()
	{
		this._children = null;
		this._sibling = null;
		this._parent = null; 
		this._data = null;
		this._isFinal = false;
		this._count = 0;
		this._finalCount = 0;
	}
	
	/** TrieNode
	 * Construct with data instance.
	 * @param data
	 */
	public TrieNode(Comparable data)
	{
		this._children = null;
		this._sibling = null;
		this._parent = null;
		this._data = data;
		this._isFinal = false;
		this._count = 0;
		this._finalCount = 0;
	}
	
	/** TrieNode
	 * Construct with data instance and final flag.
	 * @param data
	 * @param isfinal
	 */
	public TrieNode(Comparable data, boolean isfinal)
	{
		this._children = null;
		this._sibling = null;
		this._parent = null;
		this._data = data;
		this._isFinal = isfinal;
		this._count = 0;
		this._finalCount = 0;
	}
	
	/** TrieNode
	 * Construct with parent TrieNode, data instance and final flag.
	 * @param parent
	 * @param data
	 * @param isfinal
	 */
	public TrieNode(TrieNode parent, Comparable data, boolean isfinal)
	{
		this._children = null;
		this._sibling = null;
		this._parent = parent;
		this._data = data;
		this._isFinal = isfinal;
		this._count = 0;
		this._finalCount = 0;
	}
	
	/** getSibling
	 * Access the immediate sibling of this node.
	 * @return TrieNode sibling or null.
	 */
	public TrieNode getSibling()
	{
		return this._sibling;
	}
	/** setSibling
	 * Set the immediate sibling of this node.
	 * @param sibling
	 */
	public void setSibling(TrieNode sibling)
	{
		this._sibling = sibling;
	}
	/** getChild
	 * Access the immediate child of this node.
	 * @return TrieNode child
	 */
	public TrieNode getChild()
	{
		return this._children;
	}
	/** setChild
	 * Define the immediate child of this node.
	 * @param child
	 */
	public void setChild(TrieNode child)
	{
		this._children = child;
	}
	/** getData
	 * Access the data value stored at this node.
	 * @return Comparable data
	 */
	public Comparable getData()
	{
		return this._data;
	}
	/** setData
	 * Define the data stored at this node.
	 * @param data
	 */
	public void setData(Comparable data)
	{
		this._data = data;
	}
	/** isFinal
	 * Indicate whether this node is the last of a sequence of nodes.
	 * @return boolean isFinal
	 */
	public boolean isFinal()
	{
		return this._isFinal;
	}
	/** setFinal
	 * Modify the flag that indicates whether this node 
	 * is the last of a sequence of nodes
	 * @param flag
	 */
	public void setFinal(boolean flag)
	{
		this._isFinal = flag;
	}
	/** getCount
	 * Access the number of times this node's value occurs in the trie.
	 * @return int
	 */
	public int getCount()
	{
		return this._count;
	}
	/** setCount
	 * Define the number of times this node's value occurs in the trie.
	 * @param count
	 */
	public void setCount(int count)
	{
		this._count = count;
	}
	/** getFinalCount
	 * Access the number of times the sequence of values defined by
	 * the value of this node and its successive parent nodes' values
	 * occur in the trie as a sequence.
	 * @return int
	 */
	public int getFinalCount()
	{
		return this._finalCount;
	}
	/** setFinalCount
	 * Define the number of times the sequence of values defined by the
	 * value of this node and its successive parent nodes' values
	 * occur in the trie as a sequence.
	 * @param count
	 */
	public void setFinalCount(int count)
	{
		this._finalCount = count;
	}
	/** getParent
	 * Access the parent TrieNode of the current node.
	 * @return TrieNode or null.
	 */
	public TrieNode getParent()
	{
		return this._parent;
	}
	/** setParent
	 * Set the parent node for the current node.
	 * @param parent
	 */
	public void setParent(TrieNode parent)
	{
		this._parent = parent;
	}
	
	/** addSibling
	 * Add a sibling node at the current level in the trie.
	 * @param parent - current parent of this level.
	 * @param key - data value to add.
	 * @param isfinal - indicate whether this is the last of a sequence of values.
	 */
	public void addSibling(TrieNode parent, Comparable key, boolean isfinal)
	{
		if (this._sibling == null)
		{
			this._sibling = new TrieNode(this._parent, key, isfinal);
			this._sibling.setCount(1);
			if (isfinal)
				this._sibling.setFinalCount(1);
		} else if (this._sibling.getData().compareTo(key) != 0) {
			this._sibling.addSibling(this._parent, key, isfinal);
		} else {
			this._sibling.setCount(this._sibling.getCount() + 1);
			if (isfinal)
				this._sibling.setFinalCount(this._sibling.getFinalCount() + 1);
		}
	}
	
	/** getSibling
	 * Access a sibling at the given position.
	 * @param position
	 * @param count
	 * @return
	 */
	public TrieNode getSibling(int position, int count)
	{
		if (position == count)
		{
			return this;
		} else if (this._sibling != null) {
			return this._sibling.getSibling(position, ++count);
		}
		return null;
	}
	/** getSiblingsCount()
	 * Get the number of siblings at the current level.
	 * @return int
	 */
	public int getSiblingsCount()
	{
		if (this._sibling != null)
			return this._sibling.getSiblingsCount() + 1;
		return 1;
	}
	
	/** findSibling
	 * Find a sibling containing a given value.
	 * @param key
	 * @return TrieNode or null if not found.
	 */
	public TrieNode findSibling(Comparable key)
	{
		if (this._data.compareTo(key) == 0)
			return this;
		if (this._sibling != null)
			return this._sibling.findSibling(key);
		return null;
	}
	
	/** addChild
	 * Add a child into the TrieNode
	 * @param child Value of the child.
	 * @param path Path of Comparable values that precede the 
	 * child does not include the child value but is inclusive of the key.
	 * @return true on success false on failure.
	 */
	public boolean addChild(Comparable child, java.util.LinkedList<Comparable> path)
	{
		return this.addChild(child, path, path.size(), 0);
	}
	/** addChild
	 * Add a child into the TrieNode
	 * @param key Value of the child parent.
	 * @param child Value of the child.
	 * @param path Path of Comparable values that precede the 
	 * child does not include the child value but is inclusive of the key.
	 * @param depth size of path.
	 * @param count current index in path.
	 * @return true on success false on failure.
	 */
	private boolean addChild(Comparable child, java.util.LinkedList<Comparable> path, int depth, int count)
	{
		if ((depth - 1) == count)
		{
			Comparable curKey = (Comparable)path.get(count);
			// add a child under the current depth.
			TrieNode sibling = this.findSibling(curKey);
			if (sibling == null)
			{
				this.addSibling(this._parent, curKey, false);
				sibling = this.findSibling(curKey);
			}
			TrieNode children = sibling.getChild();
			if (children != null)
			{
				TrieNode findChild = children.findSibling(child);
				if (findChild != null)
				{
					findChild.setFinal(true);
					findChild.setFinalCount(findChild.getFinalCount() + 1);
				} else {
					children.addSibling(sibling, child, true);
				}
			} else {
				sibling.setChild(new TrieNode(sibling, child, true));
				sibling.getChild().setCount(1);
				sibling.getChild().setFinalCount(1);
			}
			return true;
		} else {
			Comparable curKey = (Comparable)path.get(count);
			TrieNode sibling = this.findSibling(curKey);
			if (sibling == null)
			{
				// this should only occur on the first recursion.
				this.addSibling(this._parent, curKey, false);
				sibling = this.findSibling(curKey);
			} else {
				sibling.setCount(sibling.getCount() + 1);
			}
			if (sibling.getChild() == null)
			{
				// we need to add the next value into the child of our sibling.
				sibling.setChild(new TrieNode(sibling, (Comparable)path.get(++count), false));
				sibling.getChild().setCount(1);
			} else {
				++count;
			}
			return sibling.getChild().addChild(child, path, depth, count);
		}
	}
	
	/** traverse
	 * Visit all nodes in infix order.
	 * All children are traversed first, then the current node
	 * the all siblings are processed in infix order.
	 * Each node is visited and the TrieNode 
	 * is supplied as the argument to the Visitor.
	 * @param process
	 */
	public void traverse(Visitor process)
	{
		if (this._children != null)
		{
			this._children.traverse(process);
		}
		process.visit(this);
		if (this._sibling != null)
		{
			this._sibling.traverse(process);
		}
	}
}
