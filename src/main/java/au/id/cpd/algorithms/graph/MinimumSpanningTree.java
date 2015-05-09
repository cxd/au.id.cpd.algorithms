/**
 * 
 */
package au.id.cpd.algorithms.graph;

import java.util.*;
import au.id.cpd.algorithms.adt.*;
import au.id.cpd.algorithms.patterns.*;
import au.id.cpd.algorithms.data.*;

/**
 * A minimum spanning tree takes a weighted graph
 * and calculates the minimum spanning tree of the 
 * connected components of that graph.
 * @author Chris Davey <cd@cpd.id.au> 2008-06-21
 *
 */
public class MinimumSpanningTree<P> implements IAdapter<Graph<P>, Graph<P>> {

	/**
	 * The source graph.
	 */
	private Graph<P> graph;
	
	/**
	 * A result graph consisting only of edges
	 * forming the MST.
	 */
	private Graph<P> resultGraph;

	/**
	 * Weight matrix of source graph.
	 */
	private IMatrix<Double> weights;
	
	/**
	 * Adjacency matrix.
	 */
	private IMatrix<Integer> adjacency;
	
	/**
	 * The index of the start node.
	 */
	private int startNode;
	
	/**
	 * A collection of visited outbound edges represented
	 * by node indices.
	 * The Map key is the source node index for outbound edges.
	 * The Tuple collection represents pairs of 
	 * inbound node indices and the edge weight between the source (outbound)
	 * and destination (inbound) node
	 */
	private Map<Integer, TupleCollection<Integer, ITuple<Integer,Double>>> visited;
	
	/**
	 * A collection of sorted outbound edges represented
	 * by node indices.
	 * The Map Key is the source node index.
	 * The Tuple collection represents pairs of 
	 * inbound node indices and the edge weight between the source (outbound)
	 * and destination (inbound) node.
	 * As the MST is constructed pairs from the tuple collection
	 * are removed from the sorted value and inserted into
	 * the corresponding tuple collection for the outbound node index
	 * in the visited map.
	 **/
	private Map<Integer, TupleCollection<Integer, ITuple<Integer,Double>>> sorted;
	
	/**
	 * A set of processed targets.
	 */
	private List<Integer> targets;
	
	/**
	 * Construct with graph instance.
	 */
	public MinimumSpanningTree(Graph<P> graph) {
		this.graph = graph;
		startNode = 0;
	}

	/**
	 * Construct with graph instance.
	 */
	public MinimumSpanningTree(Graph<P> graph, int start) {
		this.graph = graph;
		startNode = start;
	}
	
	/** Begin processing the minimum spanning tree.
	 * @see au.id.cpd.algorithms.patterns.IAdapter#adapt()
	 */
	public Graph<P> adapt() {
		resultGraph = new Graph<P>();
		resultGraph.setBidi(graph.isBidi());
		weights = graph.getWeights();
		adjacency = graph.getEdges();
		visited = new HashMap<Integer, TupleCollection<Integer, ITuple<Integer,Double>>>();
		sorted = new HashMap<Integer, TupleCollection<Integer, ITuple<Integer,Double>>>();
		targets = new ArrayList<Integer>();
		if (graph.getNodes().size() > 0)
			buildMST();
		return resultGraph;
	}
	
	/**
	 * Determine whether the source node i is adjacent to target node j.
	 * @param i
	 * @param j
	 * @return
	 */
	private boolean isAdjacent(int i, int j) {
		return (adjacency.get(i, j).intValue() == 1);
	}
	
	/**
	 * Construct the mst using Kruskals algorithm.
	 *  
	 *  MST-KRUSKAL(G,w)
	 *  
	 *  A <- 0
	 *  foreach vertex v in V[G]
	 *  	do MAKE-SET(v)
	 *  sort the edges of E into nondecreasing order by weight w
	 *  foreach edge (u,v) in E in nondecreasing order of weight
	 *  	do if FIND-SET(u) != FIND-SET(v)
	 *  		then A <- A union {(u,v)}
	 *  return A
	 * 
	 */
	private void buildMST() {
		// MAKE-SET
		for(int i=0;i<graph.getNodes().size();i++) {
			List<Double> row = weights.getRow(i);
			// we need to sort the row into order excluding
			// the indices that have already been processed for the
			// source node.
			sortRow(i, row);
		}
		// sort edges of E into nondecreasing order by weight.
		boolean flag = true;
		while(flag) {
			flag = visitEdges();	
		}
	}
	
	/**
	 * Retrieve the edge between source i and target j from the sorted map.
	 * @param i
	 * @param j
	 * @return
	 */
	private ITuple<Integer, ITuple<Integer,Double>> findSortedEdge(Integer i, Integer j) {
		if (!sorted.containsKey(i)) return null;
		TupleCollection<Integer, ITuple<Integer,Double>> tuples = sorted.get(i);
		for(ITuple<Integer,ITuple<Integer,Double>> tuple : tuples) {
			if (tuple.getValue().getKey().compareTo(j) == 0) {
				return tuple;
			}
		}
		return null;
	}
	
	/**
	 * Visit all edges of the graph to find the minimum edge.
	 */
	private boolean visitEdges() {
		TupleCollection<Integer, ITuple<Integer,Double>> tuples = new TupleCollection<Integer, ITuple<Integer,Double>>();
		for(int i=0;i<graph.getNodes().size();i++) {
			if ((sorted.get(i).size() > 0)&&
				(!graph.getNodes().get(i).isVisited()))
			{
				tuples.add(getMinimumEdge(i));
			}
		}
		if (tuples.size() == 0) return false;
		sort(tuples, 0, tuples.size()-1);
		ITuple<Integer, ITuple<Integer,Double>> tuple = tuples.get(0);
		// remove the minimum tuple from the sorted edge set.
		sorted.get(tuple.getKey()).remove(0);
		visited.get(tuple.getKey()).add(tuple);
		int source = (Integer)tuple.getKey();
		int target = (Integer)tuple.getValue().getKey();
		// if the source is bidirectional remove the complement edge
		// and add it to the visited collection.
		if (graph.isBidi()) {
			ITuple<Integer, ITuple<Integer,Double>> reverse = findSortedEdge(target, source);
			if (reverse != null) {
				sorted.get(target).remove(reverse);
				visited.get(target).add(reverse);
			}
		}
		// insert the nodes into our resultGraph A.
		try {
			P srcData = graph.getNodes().get(source).getData();
			P destData = graph.getNodes().get(target).getData();
			if (resultGraph.getNode(srcData) == null) {
				resultGraph.add(srcData);
			}
			if (resultGraph.getNode(destData) == null) {
				resultGraph.add(destData);
			}
			graph.getNodes().get(source).setVisited(true);
			
			resultGraph.buildEdgeMatrix();
			IMatrix<Integer> edges = resultGraph.getEdges();
			int idx = resultGraph.getNodes().indexOf(resultGraph.getNode(srcData));
			// column at idx will give us incoming connections to target.
			List<Integer> row = edges.getRow(idx);
			// no more than two connections.
			if (row.indexOf(1) < 0)
				resultGraph.connectNodes(resultGraph.getNode(srcData), resultGraph.getNode(destData));
		} catch(Exception e) {
			
		}
		return true;
	}
	
	/**
	 * Get the minimum outgoing edge from the current node.
	 * Remove it from the sorted set and insert it into the visited
	 * set.
	 * @return the tuple (target node index x weight) if it is available 
	 * null otherwise.
	 */
	private ITuple<Integer, ITuple<Integer,Double>> getMinimumEdge(int node) {
		if (!visited.containsKey(node)) {
			visited.put(node, new TupleCollection<Integer, ITuple<Integer,Double>>());
		}
		TupleCollection<Integer, ITuple<Integer,Double>> edgeSet = sorted.get(node);
		if (edgeSet.size() > 0) { 
			return edgeSet.get(0);
		}
		return null;
	}
	
	
	/**
	 * Generate a sorted row for the current node
	 * ordered by minimum to maximum.
	 * Insert it into the sorted map.
	 * @param node
	 * @param row
	 */
	private void sortRow(int node, List<Double> row) {
		// build the collection.
		TupleCollection<Integer,ITuple<Integer,Double>> tuples = new TupleCollection<Integer,ITuple<Integer,Double>>();
		sorted.put(node, tuples);
		for(int i=0;i<row.size();i++) {
			if (isAdjacent(node, i))
				tuples.add(new Tuple<Integer, ITuple<Integer,Double>>(node,new Tuple<Integer,Double>(i,row.get(i))));
		}
		// sort the collection.
		sort(tuples, 0, tuples.size() - 1);
	}
	
	/**
	 * Sort the collection using the partition technique.
	 * @param tuples
	 * @param m
	 * @param n
	 */
	private void sort(TupleCollection<Integer,ITuple<Integer,Double>> tuples, int m, int n) {
		if (m < n) {
			int p = partition(tuples, m, n);
			sort(tuples, m, p-1);
			sort(tuples,p+1,n);
		}
	}
	
	/**
	 * Partition the collection around the a index.
	 * @param tuples
	 * @param i
	 * @param j
	 * @return
	 */
	private int partition(TupleCollection<Integer,ITuple<Integer,Double>> tuples, int i, int j) {
		ITuple<Integer, ITuple<Integer,Double>> pivot,temp;
		int k,mid,p;
		mid = (i + j) / 2;
		pivot = tuples.get(mid);
		tuples.set(mid, tuples.get(i));
		tuples.set(i, pivot);
		p = i;
		for(k=i+i;k<=j;k++) {
			if (tuples.get(k).getValue().getValue() < pivot.getValue().getValue()) {
				temp = tuples.get(++p);
				tuples.set(p, tuples.get(k));
				tuples.set(k, temp);
			}
		}
		temp = tuples.get(i);
		tuples.set(i, tuples.get(p));
		tuples.set(p, temp);
		return p;
	}
		
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.patterns.IAdapter#adapt(java.lang.Object)
	 */
	public Graph<P> adapt(Graph<P> s) {
		graph = s;
		return adapt();
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.patterns.IAdapter#getProduct()
	 */
	public Graph<P> getProduct() {
		return resultGraph;
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.patterns.IAdapter#getSource()
	 */
	public Graph<P> getSource() {
		return graph;
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.patterns.IAdapter#setProduct(java.lang.Object)
	 */
	public void setProduct(Graph<P> p) {
		resultGraph = p;
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.patterns.IAdapter#setSource(java.lang.Object)
	 */
	public void setSource(Graph<P> s) {
		graph = s;
	}

	/**
	 * @return the startNode
	 */
	public int getStartNode() {
		return startNode;
	}

	/**
	 * @param startNode the startNode to set
	 */
	public void setStartNode(int startNode) {
		this.startNode = startNode;
	}
	
}
