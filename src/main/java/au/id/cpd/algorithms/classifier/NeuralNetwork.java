/**
 * 
 */
package au.id.cpd.algorithms.classifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;
import au.id.cpd.algorithms.classifier.ANN.*;
import au.id.cpd.algorithms.data.*;
import au.id.cpd.algorithms.adt.*;
/**
 * @author Chris Davey cd@cpd.id.au
 * @version 1.0
 * 
 * This class provides implementation of a 
 * feedforward neural network that learns
 * via the backpropogation algorithm.
 * 
 * It is serializable to allow the network architecture
 * and its learned weights to be reloaded 
 * for use on classification between separate
 * training sessions. The training data and test set 
 * are not serialised.
 *
 */
public class NeuralNetwork extends Graph<Double> implements java.io.Serializable {
	/**
	 * Internal serial version id
	 */
	static final long serialVersionUID = 4596099805390721798L;
	
	/**
	 * Define the learning Rate of the network.
	 */
	private double learnRate;
	/**
	 * Small value for momentum 0 < m < 1
	 */
	private double momentum;
	/**
	 * The bias of the network. Defaults to 0.
	 */
	private double bias;
	/**
	 * Define the number of Epochs.
	 */
	private int epoch;
	/**
	 * Activation unit type.
	 */
	private ActivationType unitType;
	/**
	 * Number of input nodes.
	 */
	private int inputCount;
	/**
	 * Number of output nodes corresponds to classes
	 * or single continuous range.
	 */
	private int outputCount;
	/**
	 * Number of hidden Layers.
	 */
	private int hiddenLayerCount;
	/**
	 * Number of units in each hidden layer.
	 */
	private int hiddenUnitCount;
	/**
	 * Vector of weights for the input layer.
	 * Hidden layer weights will be estimated
	 * by computing the average of each connected
	 * input weight.
	 */
	private List<Double> inputWeights;
	/**
	 * Data Set containing training tuples and target column.
	 */
	private transient IMatrix<Double> trainingSet;
	
	/**
	 * Data Set containing training tuples 
	 * for multiple outputs that consist of a vector of the same 
	 * length as the output layer.
	 * Must have same number of rows as training set.
	 */
	private transient IMatrix<Double> trainingOutputVectors;
	
	/**
	 * Data Set containing test tuples and target column.
	 */
	private transient IMatrix<Double> testSet;
	
	/**
	 * Data Set containing test tuples 
	 * for multiple outputs that consist of a vector of the same 
	 * length as the output layer.
	 * Must have same number of rows as test set.
	 */
	private transient IMatrix<Double> testOutputVectors;

	
	/**
	 * Column number for class output.
	 */
	private int targetColumn;
	/**
	 * List of mean squared errors from each epoch
	 */
	private List<Double> errors;
	/**
	 * An ordered of target classes used to identify
	 * the classification for a specified output.
	 * Outputs are arranged in same order of
	 * target class.
	 */
	private List<Double> targetsClasses;
	
	/**
	 * Flag to indicate whether the target is 
	 * a threshold.
	 */
	private boolean isTargetThreshold;
	/**
	 * The target threshold to use
	 * to determine the resulting class
	 * of the output.
	 */
	private double targetThreshold;
	
	/**
	 * The threshold for the mean squared error.
	 * If the msq drops below this, the learning stops.
	 */
	private double errorThreshold;
	
	/**
	 * In the case of a network with multiple outputs
	 * each output can be seen as representing the
	 * probability of an instance 
	 * having a given classification represented by the
	 * output node. 
	 * In this case the outputs correspond
	 * to the discrete classifications available to the dataset
	 * and is accessible in the results vector
	 * after the tuple has been classified.
	 */
	private List<Double> results;
	
	
	/**
	 * Constructor.
	 */
	public NeuralNetwork() {
		this.learnRate = 0.1;
		this.momentum = 0.0;
		this.bias = 0.0;
		this.epoch = 500;
		this.unitType = ActivationType.PERCEPTRON;
		this.errors = new Vector<Double>();
		this.errorThreshold = 0.0001;
	}
	
	/**
	 * A convenience method to load a neural network from a serial file.
	 * @param filename
	 * @return
	 */
	public static NeuralNetwork loadNetwork(String filename) {
		try {
			File fin = new File(filename);
			if (!fin.exists()) {
				return null;
			}
			FileInputStream is = new FileInputStream(fin);
			ObjectInputStream ois = new ObjectInputStream(is);
			return (NeuralNetwork)ois.readObject();
		} catch(Exception e) {
		}
		return null;
	}
	
	/**
	 * 
	 * @param data
	 * @throws Exception if data does not implement comparable.
	 */
	public void add(NeuralNodeType ntype, Double data) throws Exception {
		this.getNodes().add(new NeuralNode<Double>(ntype, this.unitType, data));
	}
	
	
	/**
	 * Build the network based on the current settings.
	 * Will throw an exception if preconditions are not met.
	 * @throws Exception
	 */
	public void constructNetwork() throws Exception {
		if (this.inputCount == 0) {
			throw new Exception("Input count is not defined.");
		}
		if (this.inputCount != this.inputWeights.size()) {
			throw new Exception("Number of input weights not equal to input count.");
		}
		Size tsz = this.trainingSet.getSize();
		Size t2sz = this.testSet.getSize();
		if (tsz.getCols() != t2sz.getCols()) {
			throw new Exception("Unequal column count between training and test set.");
		}
		if ((this.inputCount != tsz.getCols() - 1)||(this.inputCount != t2sz.getCols() - 1)) {
			throw new Exception("Input count does not equal training or test column count.");
		}
		if (this.outputCount == 0) {
			throw new Exception("Output count is 0.");
		}
		if ((this.outputCount > 1)&&(this.outputCount != this.targetsClasses.size())) {
			throw new Exception("Output count > 1 but not equal in size to the targetClasses.");
		}
		this.build();
	}
	/**
	 * Firstly we will construct the network.
	 *
	 */
	private void build() {
		try {
			// nodes will simply be named with their counts.
			double count = 1.0;
			// to account for the bias add 1.
			this.inputCount+= 1;
			for(double i=0;i<this.inputCount;i++) {
				this.add(NeuralNodeType.INPUT, count);
				count++;
			}
			for(double i=0;i<this.hiddenLayerCount;i++) {
				for(double j=0;j<this.hiddenUnitCount;j++) {
					this.add(NeuralNodeType.HIDDEN, count);
					count++;
				}
			}
			for(double i=0;i<this.outputCount;i++) {
				this.add(NeuralNodeType.OUTPUT, count);
				count++;
			}
			// we have a set of nodes.
			List<GraphNode<Double>> nodes = this.getNodes();
			// now we need to connect the layers.
			// input layer to first layer - also is fully connected.
			for(int i=0;i<this.inputCount;i++) {
				for(int j=this.inputCount;j<this.inputCount+this.hiddenUnitCount;j++) {
					this.connectNodes(nodes.get(i), nodes.get(j));
				}
			}
			// if the hidden layer count is bigger than 1 then
			// we will also need to connect the subsequent hidden layers.
			// we also need to determine the start of the last layer
			// to connect it to the output layer.
			int hideStart = this.inputCount;
			int hideEnd = hideStart+this.hiddenLayerCount*this.hiddenUnitCount;
			int nextLayer = hideStart+this.hiddenUnitCount;
			if (this.hiddenLayerCount > 1) {
				for(int i=0;i<this.hiddenLayerCount;i++) {
					// we want to connect the next forward layer
					nextLayer = hideStart+this.hiddenUnitCount;
					if (nextLayer+this.hiddenUnitCount > hideEnd) break;
					for(int j=hideStart;j<hideStart+this.hiddenUnitCount;j++) {
						for(int k=nextLayer;k<nextLayer+this.hiddenUnitCount;k++) {
							this.connectNodes(nodes.get(j), nodes.get(k));
						}
					}
					hideStart+=this.hiddenUnitCount;
				}
			}
			// the output layer should start at hideStart
			// connect the last hidden layer with the output layer.
			for(int i=hideStart;i<hideEnd;i++) {
				for(int j=hideEnd;j<hideEnd+this.outputCount;j++) {
					this.connectNodes(nodes.get(i), nodes.get(j));
				}
			}
			// Now we need to assign the weights for our weight table.
			IMatrix<Integer> edges = this.buildEdgeMatrix();
			IMatrix<Double> weights = this.getWeights();
			Random rnd = new Random();
			rnd.setSeed(Calendar.getInstance().getTimeInMillis());
			// we assign weights to the edges in the network.
			for(int i=0;i<edges.getSize().getRows();i++) {
				for(int j=0;j<edges.getSize().getCols();j++) {
					if (i != j) {
						if (edges.get(i, j).doubleValue() == 1) {
							if (i < this.inputCount - 1) {
								weights.set(i, j, rnd.nextDouble() / Math.sqrt(this.inputCount + 1) );
							} else if (i == this.inputCount - 1){
								// store the bias in our network weights
								weights.set(i, j, this.bias);
							} else if (i < hideEnd) {
								weights.set(i, j, rnd.nextDouble() / Math.sqrt(this.hiddenLayerCount + 1) );
							} else {
								weights.set(i, j, rnd.nextDouble() / Math.sqrt(this.outputCount + 1) );
							}
						}
					}
				}
			}
			this.setWeights(weights);
		} catch(Exception e) {
			
		}
	}
	
	/**
	 * Sum the weights in the supplied vector.
	 * @param weights
	 * @return
	 */
	private double sumWeights(List<Double> weights) {
		double sum = 0.0;
		for(int i=0;i<weights.size();i++) {
			sum += weights.get(i);
		}
		return sum;
	}
	
	/**
	 * Calculate the percentage of the
	 * number of samples misclassified
	 * for the current network.
	 * @return
	 */
	public double test() {
		int misClass = 0;
		List<Double> testOutputs = new Vector<Double>();
		for(int row=0;row<this.testSet.getSize().getRows();row++) {
			List<Double> dataRow = this.testSet.getRow(row);
			double target = this.testSet.getRow(row).get(this.targetColumn);
			// if output has at least one value matching the target then it is correct.
			double res = this.classify(dataRow);
			testOutputs.add(res);
			if (!this.isTargetThreshold) {
				
				System.out.println("Test: "+res + " Equals: " + target);
				if (res != target) {
					misClass++;
				}
			} else {
				if ((res > this.targetThreshold)&&(target <= this.targetThreshold)) {
					misClass++;
				} else if ((res <= this.targetThreshold)&&(target > this.targetThreshold)) {
					misClass++;
				}
			}
		}
		System.err.println("Misclassified count: "+misClass+"/"+this.testSet.getSize().getRows());
		System.err.println("Test Outputs:\n"+testOutputs+"\n");
		double rowCnt = this.testSet.getSize().getRows();
		double res = (double)(misClass/rowCnt);
		return res;
	}
	
	/**
	 * Get the classification for the supplied typle.
	 * If using a target threshold the classification is 
	 * considered to be a binary type of either 0 or 1
	 * otherwise the classification will be one of the defined
	 * class instances defined in the targetClasses vector.
	 * @param tuple
	 * @return double classification
	 */
	public double getClassification(List<Double> tuple) {
		double result = this.classify(tuple);
		if (!this.isTargetThreshold) {
			return result;
		} else {
			if (result > this.targetThreshold) {
				return 1.0;
			}
		}
		return 0.0;
	}
	
	
	
	
	/**
	 * Classify the given tuple
	 * return the majority classification.
	 * @param tuple
	 * @return
	 */
	public double classify(List<Double> tuple) {
		this.feedForward(tuple);
		// use the output layer to determine the classifications.
		int startOutput = this.getNodes().size() - this.outputCount;
		double msq = 0.0;
		double cnt = 0.0;
		this.results = new Vector<Double>();
		// the type of network can affect the type of classification.
		// individual nodes may represent multiple classes
		// or the class may be continuous represented by
		// a single node
		for(int i=startOutput;i<this.getNodes().size();i++) {
			NeuralNode<Double> node = (NeuralNode<Double>)this.getNodes().get(i);
			double o = node.getOutput();
			this.results.add(o);
		}
		if (this.outputCount == 1) {
			return this.results.get(0);
		} else {
			// the classification of k-outputs
			// should represent the probability
			// of the tuple being a member of the class
			// associated with the output
			// rather than the value of the output.
			// therefore we will need to have a list
			// of target values that we can use to identify
			// the class that the output with the maximum probability
			// belongs to.
			double target = 0.0;
			double max = Double.MIN_VALUE;
			for(int i=0;i<this.results.size();i++) {
				if (this.results.get(i) > max) {
					max = this.results.get(i);
					target = this.targetsClasses.get(i);
					System.out.println("Target Class: " + target);
				}
			}
			return target;
		}
	}
	
	/**
	 * The prediction method will return
	 * the result of feeding data forward through
	 * the network without attempting to apply a 
	 * classification.
	 * A vector of all output values are returned.
	 * @param tuple
	 * @return
	 */
	public List<Double> predict(List<Double> tuple) {
		this.feedForward(tuple);
		// use the output layer to determine the classifications.
		int startOutput = this.getNodes().size() - this.outputCount;
		double msq = 0.0;
		double cnt = 0.0;
		this.results = new Vector<Double>();
		// the type of network can affect the type of classification.
		// individual nodes may represent multiple classes
		// or the class may be continuous represented by
		// a single node
		for(int i=startOutput;i<this.getNodes().size();i++) {
			NeuralNode<Double> node = (NeuralNode<Double>)this.getNodes().get(i);
			double o = node.getOutput();
			this.results.add(o);
		}
		return this.results;
	}
	
	/**
	 * Compute the error surface of the network using the training data set.
	 * @return Matrix<Double> error surface
	 */
	public IMatrix<Double> computeErrorSurface() {
		// this method currently computes the
		// separating plane - which can be useful for charting separation.
		// TODO: We need to compute the error surface instead.
		List<Double> errors = new ArrayList<Double>();
		for(int row=0;row<this.trainingSet.getSize().getRows();row++) {
			// feed it forward.
			List<Double> dataRow = this.trainingSet.getRow(row);
			this.feedForward(dataRow);
			// now we need to collect the squared error.
			errors.add(this.calculateSquaredError(row));
		}
		// create a square matrix repeating the value of the error in each column
		IMatrix<Double> E = new Matrix<Double>(errors.size(), errors.size());
		for(int i=0;i<errors.size();i++) {
			for(int j=0;j<errors.size();j++) {
				E.set(i, j, errors.get(i));
			}
		}
		// calculate the covariance matrix.
		IMatrix<Double> C = E.covariance();
		List<Double> d = C.diagonal();
		IMatrix<Double> t = new Matrix<Double>(1,d.size());
		for(int i=0;i<d.size();i++) {
			t.set(0, i, d.get(i));
		}
		// resize the diagonal.
		int r = (int)Math.sqrt(d.size());
		// reshape to convert to an error surface.
		return t.reshape(r,r);
	}
	
	
	/**
	 * Process the training set until
	 * convergence of the squared error is reached
	 * or the total number of epochs have expired.
	 */
	public void learn() {
		this.errors = new Vector<Double>();
		// iterate until total number of epochs 
		for(int cnt = 0;cnt < this.epoch;cnt++) {
			// process each row of the training set.
			double error = 0.0;
			for(int row=0;row<this.trainingSet.getSize().getRows();row++) {
				// feed it forward.
				List<Double> dataRow = this.trainingSet.getRow(row);
				this.feedForward(dataRow);
				// now we need to collect the squared error.
				error += this.calculateSquaredError(row);
				double target = this.trainingSet.get(row, this.targetColumn).doubleValue();
				// we need to backpropogate the error and update the weights.
				this.backPropogate(target);
			}
			if (error != 0.0) {
				error /= this.trainingSet.getSize().getRows();
			}
			this.errors.add(error);
			if (this.errors.size() > 1) {
				if (this.errors.get(this.errors.size() - 1) > this.errors.get(this.errors.size() - 2)) {
					// reduce the learning rate by a fraction - 10th.
					this.learnRate -= this.learnRate / 10.0;
				}
			}
			// what if the error is not decreasing but remaining the same?
			// we should increase the learning rate slightly.
			// 100 is an arbitrary decision.
			if (this.errors.size() > 100) {
				// only increase the learnRate if the error is not increasing.
				boolean increase = (this.errors.get(this.errors.size() - 1) > this.errors.get(this.errors.size() - 2));
				if ((this.errors.get(this.errors.size() - 1) == this.errors.get(this.errors.size() - 100))&&(!increase)) {
					// increase the learning rate by a fraction - 10th.
					this.learnRate += this.learnRate / 10.0;
				}
			}
			// notify observers that the error list has been updated.
			this.update();
			// can also check for convergence here.
			// using the errorThreshold.
			if (error < this.errorThreshold) break;
			//System.err.println("Epoch: "+cnt+" Err: "+error);
		}
	}
	
	/**
	 * Feed forward the outputs computed
	 * for the tuple in the supplied row.
	 * @param dataRow
	 */
	public void feedForward(List<Double> dataRow) {
		IMatrix<Double> weights = this.getWeights();
		IMatrix<Integer> edges = this.getEdges();
		dataRow.remove(this.targetColumn);
		for(int i=0;i<this.getNodes().size();i++) {
			if (i < this.inputCount - 1) {
				NeuralNode<Double> node = (NeuralNode<Double>)this.getNodes().get(i);
				node.activate(dataRow.get(i));
			} else if (i == this.inputCount - 1) {
				// multiply the bias by -1.
				NeuralNode<Double> node = (NeuralNode<Double>)this.getNodes().get(i);
				node.activate(-1);
			} else {
				// get the inbound weights.
				List<Double> w = weights.getColumn(i);
				// each index in this column is the index in
				// our node list of the inbound connected node.
				double accumulate = 0.0;
				for(int j=0;j<w.size();j++) {
					if (edges.get(j, i).doubleValue() == 1.0) {
						NeuralNode<Double> node = (NeuralNode<Double>)this.getNodes().get(j);
						accumulate += w.get(j)*node.getOutput();
					}
				}
				NeuralNode<Double> node = (NeuralNode<Double>)this.getNodes().get(i);
				node.activate(accumulate);
			}
		}
	}
	/**
	 * Calculate the squared error for the current row
	 * in the dataset.
	 * @param row
	 * @return
	 */
	public double calculateSquaredError(int row) {
		// get the output unit/s
		int startOutput = this.getNodes().size() - this.outputCount;
		double msq = 0.0;
		double cnt = 0.0;
		double target = this.trainingSet.get(row, this.targetColumn).doubleValue();
		
		if ( (this.outputCount > 1) && (this.getTargetsClasses() != null) ) {
			// find the maximum activated output.
			// expect single output.
			double value = 0.0;
			int n = 0;
			for(int i=startOutput;i<this.getNodes().size();i++) {
				if ( (this.outputCount > 1) && (this.getTargetsClasses() != null) ) {
					// find the position of the highest activated output neuron.
					int pos = getTargetsClasses().indexOf(target);
					if ( (pos >= 0) && (n != pos) ) {
						value = 0.0;
					} else if (pos < 0) {
						value = 0.0;
					} else {
						// highest activation for index of target.
						value = 1.0;
					}
				}
				NeuralNode<Double> node = (NeuralNode<Double>)this.getNodes().get(i);
				double o = node.getOutput();
				double error = 0.5*Math.pow(value - o, 2);
				// yet the output node must have its internal error set also
				node.setError(value - o);
				msq += error;
				cnt++;
				n++;
			}
			
		} else if (this.outputCount > 1) {
			// expect vector output of continuous values
			// this vector of continuous values can be selected
			// from a third table that contains the exact number of rows
			// for training and test data.
			// here each value in training output vector is meant to be the value of the activated neuron.
			List<Double> outputVector = trainingOutputVectors.getRow(row);
			int n = 0;
			for(int i=startOutput;i<this.getNodes().size();i++) {
				NeuralNode<Double> node = (NeuralNode<Double>)this.getNodes().get(i);
				double o = node.getOutput();
				double error = 0.5*Math.pow(outputVector.get(n) - o, 2);
				// yet the output node must have its internal error set also
				node.setError(outputVector.get(n) - o);
				msq += error;
				cnt++;
				n++;
			}
			
		} else {
			// expect single output.
			for(int i=startOutput;i<this.getNodes().size();i++) {
				if ( (this.outputCount > 1) && (this.getTargetsClasses() != null) ) {
					// find the position of the highest activated output neuron.
					int pos = getTargetsClasses().indexOf(target);
					if ( (pos >= 0) && (i != pos) ) {
						target = 0.0;
					} else if (pos < 0) {
						target = 0.0;
					} else {
						// highest activation for index of target.
						target = 1.0;
					}
				}
				NeuralNode<Double> node = (NeuralNode<Double>)this.getNodes().get(i);
				double o = node.getOutput();
				double error = 0.5*Math.pow(target - o, 2);
				// yet the output node must have its internal error set also
				node.setError(target - o);
				msq += error;
				cnt++;
			}
		}
		
		
		return msq/cnt;
	}
	/**
	 * Back propogate the error from output to input nodes
	 * and update the weight matrix.
	 * @param target target value is supplied to backpropogate algorithm
	 */
	public void backPropogate(double target) {
		int startOutput = this.getNodes().size() - this.outputCount - 1;
		IMatrix<Integer> edges = this.getEdges();
		IMatrix<Double> weights = this.getWeights();
		// back propogating for hidden nodes.
		for(int k=(this.getNodes().size()-1);k>=(this.inputCount-1);k--) {
			// we want the node at k which will be a hidden node.
			NeuralNode<Double> hNode = (NeuralNode<Double>)this.getNodes().get(k);
			// we still need to calculate the error for hidden nodes.
			// but it has already been calculated for output layer.
			if (k <= startOutput) {
				// we use our neigbours to calculate the accumulated error
				// we will also need our adjacency list to determine whether the nodes are adjacent
				// we also want the index of each neighbour to use in our lookup table.
				double sumErr = 0.0;
				int cnt = 0;
				for(GraphNode<Double> neighbour : hNode.getNeighbours()) {
					NeuralNode<Double> kNode = (NeuralNode<Double>)neighbour;
					// an outer node.
					int j = this.getNodes().indexOf(neighbour);
					double err = kNode.getError();
					// link from k to j must exist for it to be a neighbour.
					double wkj = weights.get(k, j).doubleValue();
					sumErr += wkj*err;
					cnt++;
				}
				// 
				double error = hNode.getActivator().differentiate(hNode.getOutput())*sumErr;
				hNode.setError(error);
			}
			// now we backpropogate for the change in weight.
			// we know that our column of weights will contain
			// all values for connections from row to k
			List<Integer> adjacent = edges.getColumn(k);
			for(int i=0;i<adjacent.size();i++) {
				if (adjacent.get(i) == 0) continue;
				if ((k >= this.inputCount-1)&&(k < this.inputCount -1 + this.hiddenUnitCount)) {
					if (i == this.inputCount -1) {
						// don't update the bias weight.
						continue; 
					}
				}
				// otherwise we are able to calculate delta for w(i,k)
				NeuralNode<Double> iNode = (NeuralNode<Double>)this.getNodes().get(i);
				double deltaW = this.learnRate*hNode.getError()*iNode.getOutput();
				double w = weights.get(i, k).doubleValue();
				// add weight momentum
				double m = this.momentum*w;
				// update the weight.
				w = w + m + deltaW;
				if (Double.isNaN(w)) w = 0.0;
				weights.set(i, k, w);
				this.setWeights(weights);
			}
		}
	}
	
	/**
	 * @return the epoch
	 */
	public int getEpoch() {
		return epoch;
	}
	/**
	 * @param epoch the epoch to set
	 */
	public void setEpoch(int epoch) {
		this.epoch = epoch;
	}
	/**
	 * @return the hiddenLayerCount
	 */
	public int getHiddenLayerCount() {
		return hiddenLayerCount;
	}
	/**
	 * @param hiddenLayerCount the hiddenLayerCount to set
	 */
	public void setHiddenLayerCount(int hiddenLayerCount) {
		this.hiddenLayerCount = hiddenLayerCount;
	}
	/**
	 * @return the hiddenUnitCount
	 */
	public int getHiddenUnitCount() {
		return hiddenUnitCount;
	}
	/**
	 * @param hiddenUnitCount the hiddenUnitCount to set
	 */
	public void setHiddenUnitCount(int hiddenUnitCount) {
		this.hiddenUnitCount = hiddenUnitCount;
	}
	/**
	 * @return the inputCount
	 */
	public int getInputCount() {
		return inputCount;
	}
	/**
	 * @param inputCount the inputCount to set
	 */
	public void setInputCount(int inputCount) {
		this.inputCount = inputCount;
	}
	/**
	 * @return the inputWeights
	 */
	public List<Double> getInputWeights() {
		return inputWeights;
	}
	/**
	 * @param inputWeights the inputWeights to set
	 */
	public void setInputWeights(List<Double> inputWeights) {
		this.inputWeights = inputWeights;
	}
	/**
	 * @return the learnRate
	 */
	public double getLearnRate() {
		return learnRate;
	}
	/**
	 * @param learnRate the learnRate to set
	 */
	public void setLearnRate(double learnRate) {
		this.learnRate = learnRate;
	}
	/**
	 * @return the outputCount
	 */
	public int getOutputCount() {
		return outputCount;
	}
	/**
	 * @param outputCount the outputCount to set
	 */
	public void setOutputCount(int outputCount) {
		this.outputCount = outputCount;
	}
	/**
	 * @return the unitType
	 */
	public ActivationType getUnitType() {
		return unitType;
	}
	/**
	 * @param unitType the unitType to set
	 */
	public void setUnitType(ActivationType unitType) {
		this.unitType = unitType;
	}
	/**
	 * @return the targetColumn
	 */
	public int getTargetColumn() {
		return targetColumn;
	}
	/**
	 * @param targetColumn the targetColumn to set
	 */
	public void setTargetColumn(int targetColumn) {
		this.targetColumn = targetColumn;
	}
	/**
	 * @return the testSet
	 */
	public IMatrix<Double> getTestSet() {
		return testSet;
	}
	/**
	 * @param testSet the testSet to set
	 */
	public void setTestSet(IMatrix<Double> testSet) {
		this.testSet = testSet;
	}
	/**
	 * @return the trainingSet
	 */
	public IMatrix<Double> getTrainingSet() {
		return trainingSet;
	}
	/**
	 * @param trainingSet the trainingSet to set
	 */
	public void setTrainingSet(IMatrix<Double> trainingSet) {
		this.trainingSet = trainingSet;
	}

	/**
	 * @return the targetsClasses
	 */
	public List<Double> getTargetsClasses() {
		return targetsClasses;
	}

	/**
	 * @param targetsClasses the targetsClasses to set
	 */
	public void setTargetsClasses(List<Double> targetsClasses) {
		this.targetsClasses = targetsClasses;
	}

	/**
	 * @return the errors
	 */
	public List<Double> getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List<Double> errors) {
		this.errors = errors;
	}

	/**
	 * @return the isTargetThreshold
	 */
	public boolean isTargetThreshold() {
		return isTargetThreshold;
	}

	/**
	 * @param isTargetThreshold the isTargetThreshold to set
	 */
	public void setIsTargetThreshold(boolean isTargetThreshold) {
		this.isTargetThreshold = isTargetThreshold;
	}

	/**
	 * @return the targetThreshold
	 */
	public double getTargetThreshold() {
		return targetThreshold;
	}

	/**
	 * @param targetThreshold the targetThreshold to set
	 */
	public void setTargetThreshold(double targetThreshold) {
		this.targetThreshold = targetThreshold;
	}

	/**
	 * @return the errorThreshold
	 */
	public double getErrorThreshold() {
		return errorThreshold;
	}

	/**
	 * @param errorThreshold the errorThreshold to set
	 */
	public void setErrorThreshold(double errorThreshold) {
		this.errorThreshold = errorThreshold;
	}

	/**
	 * @return the results
	 */
	public List<Double> getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(List<Double> results) {
		this.results = results;
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
	 * @return the momentum
	 */
	public double getMomentum() {
		return momentum;
	}

	/**
	 * @param momentum the momentum to set
	 */
	public void setMomentum(double momentum) {
		this.momentum = momentum;
	}

	/**
	 * @return the bias
	 */
	public double getBias() {
		return bias;
	}

	/**
	 * @param bias the bias to set
	 */
	public void setBias(double bias) {
		this.bias = bias;
	}

	/**
	 * @return the trainingOutputVectors
	 */
	public IMatrix<Double> getTrainingOutputVectors() {
		return trainingOutputVectors;
	}

	/**
	 * @param trainingOutputVectors the trainingOutputVectors to set
	 */
	public void setTrainingOutputVectors(IMatrix<Double> trainingOutputVectors) {
		this.trainingOutputVectors = trainingOutputVectors;
	}

	/**
	 * @return the testOutputVectors
	 */
	public IMatrix<Double> getTestOutputVectors() {
		return testOutputVectors;
	}

	/**
	 * @param testOutputVectors the testOutputVectors to set
	 */
	public void setTestOutputVectors(IMatrix<Double> testOutputVectors) {
		this.testOutputVectors = testOutputVectors;
	}
	

}
