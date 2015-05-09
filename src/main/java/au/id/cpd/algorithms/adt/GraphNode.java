/**
 * 
 */
package au.id.cpd.algorithms.adt;

import java.util.*;

/**
 * @author Chris Davey cd@cpd.id.au
 * @version 1.0
 * A GraphNode for storage within a graph structure.
 */
public class GraphNode<T> extends Node<T> implements java.io.Serializable {
	/**
	 * Internal serializer id.
	 */
	static final long serialVersionUID = -6209358018953179378L;
	
	/**
	 * Adjacency list of neighbours.
	 */
	private List<GraphNode<T>> neighbours;
	
	/**
	 * default constructor.
	 */
	public GraphNode() {
		super();
		this.neighbours = new Vector<GraphNode<T>>();
	}
	/**
	 * @param data
	 * @throws Exception
	 */
	public GraphNode(T data) throws Exception {
		super(data);
		this.neighbours = new Vector<GraphNode<T>>();
	}
	/**
	 * @return the neighbours
	 */
	public List<GraphNode<T>> getNeighbours() {
		return neighbours;
	}
	/**
	 * @param neighbours the neighbours to set
	 */
	public void setNeighbours(List<GraphNode<T>> neighbours) {
		this.neighbours = neighbours;
	}
	/**
	 * Count the number of adjacent neighbours.
	 * @return
	 */
	public int adjacentCount() {
		return this.neighbours.size();
	}
	/**
	 * Add a neighbour to the adjacent list.
	 * @param precondition node is not connected.
	 * @param node
	 */
	public void addNeighbour(GraphNode<T> node) {
		this.neighbours.add(node);
	}
	/**
	 * Remove a neighbour from the adjacent list.
	 * @param node
	 * @return neighbour
	 */
	public GraphNode<T> removeNeighbour(GraphNode<T> node) {
		GraphNode<T> n = this.findNeighbour(node);
		this.neighbours.remove(node);
		return n;
	}
	/**
	 * Determine if the node is adjacent to the current node.
	 * @param node
	 * @return
	 */
	public boolean isAdjacent(GraphNode<T> node) {
		return (this.findNeighbour(node) != null);
	}
	/**
	 * Find the node in the adjacent list.
	 * @param node
	 * @return
	 */
	public GraphNode<T> findNeighbour(GraphNode<T> node) {
		int i = this.neighbours.indexOf(node);
		if (i >= 0)
			return this.neighbours.get(i);
		return null;
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
