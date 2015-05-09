/**
 * 
 */
package au.id.cpd.algorithms.adt;

import java.util.*;
import au.id.cpd.algorithms.data.*;
import au.id.cpd.algorithms.patterns.*;
/**
 * @author Chris Davey cd@cpd.id.au
 * @version 1.0
 * 
 * A Graph implementation which implements serializable.
 *
 */
public class Graph<T> extends Subject implements java.io.Serializable {

	/**
	 * Internal serializable id.
	 */
	static final long serialVersionUID = -7082262721964243831L;
	
	private List<GraphNode<T>> nodes;
	private IMatrix<Integer> edges;
	private IMatrix<Double> weights;
	private boolean isWeighted;
	private boolean isBidi;
	/**
	 * Constructor
	 *
	 */
	public Graph() {
		super();
		this.isWeighted = false;
		this.isBidi = false;
		this.nodes = new Vector<GraphNode<T>>();
	}
	
	/**
	 * Construct with flag for bidirectional.
	 * @param bidi
	 */
	public Graph(boolean bidi) {
		this();
		this.isBidi = bidi;
	}
	
	/**
	 * Construct with flags for bidirectional and weighted
	 * @param bidi
	 * @param weighted
	 */
	public Graph(boolean bidi, boolean weighted) {
		this();
		this.isBidi = bidi;
		this.isWeighted = weighted;
	}
	
	/**
	 * 
	 * @param data
	 * @throws Exception if data does not implement comparable.
	 */
	public void add(T data) throws Exception {
		this.nodes.add(new GraphNode<T>(data));
	}
	
	/**
	 * Access the node containing the supplied data.
	 * @param data
	 * @return
	 */
	public GraphNode<T> getNode(T data) {
		try {
			for(GraphNode<T> node : nodes) {
				if (((Comparable)node.getData()).compareTo(data) == 0) return node;
			}
		} catch(Exception e) {
		}
		return null;
	}
	
	/**
	 * Remove a node containing the supplied data.
	 * @param data
	 * @return
	 */
	public T remove(T data) {
		try {
			int idx = this.nodes.indexOf(new GraphNode<T>(data));
			if (idx >= 0) {
				GraphNode<T> target = this.nodes.get(idx);
				for(GraphNode<T> node : this.nodes) {
					if (node.isAdjacent(target)) {
						node.removeNeighbour(target);
					}
				}
				this.nodes.remove(idx);
				return target.getData();
			}
		} catch(Exception e) {
		}
		return null;
	}
	
	/**
	 * Create an edge between 2 nodes.
	 * @param node1
	 * @param node2
	 */
	public void connectNodes(GraphNode<T> node1, GraphNode<T> node2) {
		int idx1 = this.nodes.indexOf(node1);
		int idx2 = this.nodes.indexOf(node2);
		if ((idx1 >= 0)&&(idx2 >= 0)) {
			if (!this.nodes.get(idx1).isAdjacent(node2))
				this.nodes.get(idx1).addNeighbour(node2);
			if ((this.isBidi)&&(!this.nodes.get(idx2).isAdjacent(node1))) 
				this.nodes.get(idx2).addNeighbour(node1);
		}
 	}
	/**
	 * Remove the connection between two nodes.
	 * @param node1
	 * @param node2
	 */
	public void disconnectNodes(GraphNode<T> node1, GraphNode<T> node2) {
		int idx1 = this.nodes.indexOf(node1);
		int idx2 = this.nodes.indexOf(node2);
		if ((idx1 >= 0)&&(idx2 >= 0)) {
			if (!this.nodes.get(idx1).isAdjacent(node2))
				this.nodes.get(idx1).removeNeighbour(node2);
			if ((this.isBidi)&&(!this.nodes.get(idx2).isAdjacent(node1))) 
				this.nodes.get(idx2).removeNeighbour(node1);
		}
	}
	
	/**
	 * Add all data points in the supplied collection
	 * as nodes in the graph.
	 * @param data
	 * @throws Exception is an item in the collection does
	 * not implement the comparable interface.
	 */
	public void addAll(Collection<T> data) throws Exception {
		for(T d : data) {
			this.nodes.add(new GraphNode<T>(d));
		}
	}
	
	/**
	 * Once the graph has been constructed and the
	 * appropriate nodes are connected it is possible to
	 * build a simple adjacency matrix containing 1 for
	 * connected and 0 for disconnected.
	 * Also initialises the weight matrix at the same time.
	 * All weights are set to 0.0
	 * @return edge matrix.
	 */
	public IMatrix<Integer> buildEdgeMatrix() {
		this.edges = new Matrix<Integer>(this.nodes.size(), this.nodes.size());
		this.weights = new Matrix<Double>(this.nodes.size(), this.nodes.size());
		for(int i=0;i<this.nodes.size()*this.nodes.size();i++) {
			this.edges.add(0);
			this.weights.add(0.0);
		}
		for(int i=0;i<this.nodes.size();i++) {
			for(int j=0;j<this.nodes.size();j++) {
				if (this.nodes.get(i).isAdjacent(this.nodes.get(j))) {
					this.edges.set(i, j, 1);
				}
			}
		}
		return this.edges;
	}

	/**
	 * @return the edges
	 */
	public IMatrix<Integer> getEdges() {
		return edges;
	}

	/**
	 * @param edges the edges to set
	 */
	public void setEdges(IMatrix<Integer> edges) {
		this.edges = edges;
	}

	/**
	 * @return the isBidi
	 */
	public boolean isBidi() {
		return isBidi;
	}

	/**
	 * @param isBidi the isBidi to set
	 */
	public void setBidi(boolean isBidi) {
		this.isBidi = isBidi;
	}

	/**
	 * @return the isWeighted
	 */
	public boolean isWeighted() {
		return isWeighted;
	}

	/**
	 * @param isWeighted the isWeighted to set
	 */
	public void setWeighted(boolean isWeighted) {
		this.isWeighted = isWeighted;
	}

	/**
	 * @return the nodes
	 */
	public List<GraphNode<T>> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(List<GraphNode<T>> nodes) {
		this.nodes = nodes;
	}

	/**
	 * @return the weights
	 */
	public IMatrix<Double> getWeights() {
		return weights;
	}

	/**
	 * @param weights the weights to set
	 */
	public void setWeights(IMatrix<Double> weights) {
		this.weights = weights;
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
