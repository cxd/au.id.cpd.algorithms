/** @(#)Trie.java 2006-03-05
 * 
 */
package au.id.cpd.algorithms.adt;

import java.util.*;

/**
 * Trie is a retrieval tree.
 * Each node of the tree contains n indices called siblings 
 * and may contain 0 or more children nodes.
 * @author Chris Davey <cd@cpd.id.au>
 * @version 0.1 2006-03-06
 */
public class Trie {
	private TrieNode _root;
	
	public Trie()
	{
		this._root = null;
	}
	/** add
	 * Add a sibling to the root node.
	 * @param data
	 */
	public void add(Comparable data, boolean isfinal)
	{
		if (this._root == null)
		{
			this._root = new TrieNode(data, isfinal);
			this._root.setCount(1);
			if (isfinal)
				this._root.setFinalCount(1);
		} else {
			this._root.addSibling(null, data, isfinal);
		}
	}
	
	/** addPath
	 * Add a set of values to the trie.
	 * The LinkedList must contain a set of comparable keys.
	 * @param data
	 * @param path - must be a set of Comparable objects. Size must be > 0
	 * and consists of the set of data preceding the supplied data instance.
	 */
	public void addPath(Comparable data, java.util.LinkedList<Comparable> path)
	{
		if (path.size() == 0)
			return;
		Comparable key  = (Comparable)path.get(0);
		if (this._root == null)
		{
			this.add(key, false);
			// the count will be automatically updated by the trie node.
			this._root.setCount(0);
		}
		this._root.addChild(data, path);
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
		if (this._root != null)
			this._root.traverse(process);
	}
}
