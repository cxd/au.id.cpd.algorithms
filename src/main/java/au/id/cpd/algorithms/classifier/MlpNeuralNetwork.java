/**
 * 
 */
package au.id.cpd.algorithms.classifier;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import au.id.cpd.algorithms.patterns.*;
import au.id.cpd.algorithms.data.*;
import au.id.cpd.algorithms.data.io.*;
import au.id.cpd.algorithms.classifier.ANN.*;

/**
 * The MlpNeural Network will
 * apply a matrix algebra approach to the mlp
 * instead of the graph structure provided by the 
 * NeuralNetwork implementation.
 * 
 * Layers in the mlp neural network are configured by
 * defining matrices of initial weights.
 * 
 * The dimensions of these matrices are defined
 * as configuration parameters to the network.
 * 
 * As well as defining the learing rate
 * the bias and the momentum.
 * 
 * @author Chris Davey
 *
 */
public class MlpNeuralNetwork extends Subject implements java.io.Serializable {
	/**
	 * Serial version id.
	 */
	static final long serialVersionUID = 11669364779179922L;
	/**
	 * Internal flag to determine if the network is initialized correctly.
	 */
	private boolean initialized;
	/**
	 * Number of outputs.
	 */
	private int outputCount;
	/**
	 * output weights.
	 */
	private IMatrix<Double> outputWeights;
	/**
	 * Number of hidden layers.
	 */
	private int hiddenLayerCount;
	
	/**
	 * Number of hidden units per layer.
	 */
	private int hiddenUnitCount;
	
	/**
	 * Collection of matrices for hidden layers.
	 * The dimensions of these hidden layers must be compatible
	 * with the output weights and with each other
	 * when performing operations h = w'x
	 */
	private List<IMatrix<Double>> hiddenWeights;
	/**
	 * Number of nodes in input layer.
	 */
	private int inputCount;
	
	/**
	 * Type of hidden layer activation.
	 */
	private ActivationType hiddenActivation;
	/**
	 * Type of output activation.
	 */
	private ActivationType outputActivation;
	
	/**
	 * Activation used in temperature.
	 */
	private Double temperature;
	
	/**
	 * Bias of network.
	 */
	private Double bias;
	
	/**
	 * Energy of activation.
	 */
	private Double energy;
	
	/**
	 * Momentum on learning.
	 */
	private Double momentum;

	/**
	 * Learning rate for network.
	 */
	private Double learningRate;
	/**
	 * Number of epochs.
	 */
	private int epoch;
	
	/**
	 * List of mean squared errors from each epoch
	 */
	private List<Double> errors;

	/**
	 * Minimum error threshold.
	 */
	private Double errorThreshold;
	
	/**
	 * Data Set containing training tuples and target column.
	 */
	private transient IMatrix<Double> trainingSet;
	/**
	 * Data Set containing test tuples and target column.
	 */
	private transient IMatrix<Double> testSet;
	
	/**
	 * Internal data used for training.
	 */
	private transient IMatrix<Double> data;
	
	/**
	 * Column number for class output.
	 */
	private List<Integer> targetColumns;
	

	/**
	 * Matrix of target values.
	 */
	private IMatrix<Double> targets;
	
	/**
	 * Default constructor.
	 * Initialise default values.
	 */
	public MlpNeuralNetwork() {
		super();
		hiddenActivation = ActivationType.SIGMOID;
		outputActivation = ActivationType.TANSIG;
		targetColumns = new ArrayList<Integer>();
		learningRate = 0.0001;
		momentum = 0.0001;
		bias = 0.0;
		energy = 1.0;
		temperature = 1.0;
		errorThreshold = 0.01;
		epoch = 1000;
	}
	
	/**
	 * Initialise the network matrices.
	 *
	 */
	public void constructNetwork() {
		// Arows == Bcols
		hiddenWeights = new ArrayList<IMatrix<Double>>();
		int hCols = hiddenUnitCount;
		int hRows = inputCount + 1; // add 1 fpr the first hidden layer bias.
		double m = 1.0;
		if (this.trainingSet != null) {
			m = this.trainingSet.getSize().getRows() - 1;
		}
		if (m == 0) m = 1.0;
		
		// build the required number of hidden weights.
		// we need the network to be conformable
		// so we need to swap the weights around.
		for(int i=0;i<hiddenLayerCount;i++) {
			IMatrix<Double> h;
			if (i % 2 == 0) {
				// each matrix will need to be transversed
				h = Matrix.ones(hCols, hRows);
			} else {
				h = Matrix.ones(hRows, hCols);
			}
			Random rnd = new Random();
			hiddenWeights.add(h.multiply(rnd.nextDouble() * m).divisorOf(Math.sqrt(this.inputCount*1.0)));
			// multiplied by inputs
			// AB = [A_cols x B_rows]
			// to be conformant
			// A'_rows == AB_cols
			hRows = hCols;
			// need to determine the next size
			if ( (i < hiddenLayerCount - 1) && (hCols - 1 > outputCount)) {
				hCols--;
			} else {
				hCols = outputCount;
			}
		}
		// initialise the network bias.
		if (hiddenWeights.size() > 0) {
			for(int i=0;i<hiddenWeights.size();i++) {
				if (i % 2 == 0) {
					for(int j=0;j<hiddenWeights.get(i).getSize().getCols();j++) {
						// inputCount == hidden layer num_rows - 1
						hiddenWeights.get(i).set(inputCount, j, bias);
					}
				} else {
					for(int j=0;j<hiddenWeights.get(i).getSize().getRows();j++) {
						// inputCount == hidden layer num_rows - 1
						hiddenWeights.get(i).set(j, inputCount, bias);
					}
				}
			}
		}
		// A'_rows == AB_cols.
		//  each matrix will need to be transversed
		Random rnd = new Random();
		// make sure outputWeights is conformable with prior hidden weights.
		if (hiddenWeights.size() % 2 != 0) {
			outputWeights = Matrix.ones(inputCount + 1, outputCount).multiply(rnd.nextDouble()*m).divisorOf(Math.sqrt(this.outputCount*1.0));
			// initialise the network bias.
			for(int j=0;j<outputWeights.getSize().getCols();j++) {
				// inputCount == hidden layer num_rows - 1
				outputWeights.set(inputCount, j, bias);
			}
		} else {
			outputWeights = Matrix.ones(outputCount, inputCount + 1).multiply(rnd.nextDouble()*m).divisorOf(Math.sqrt(this.outputCount*1.0));
			// initialise the network bias.
			for(int j=0;j<outputWeights.getSize().getRows();j++) {
				// inputCount == hidden layer num_rows - 1
				outputWeights.set(j, inputCount, bias);
			}
		}
	}
	
	/**
	 * Initialise Data.
	 * The supplied data cannot have the bias added to it as yet.
	 * This method will increase the size of the columns of the data
	 * to allow for the network bias used in the weight matrix.
	 */
	public void initialiseData(IMatrix<Double> tData) {
		// we need to copy the targets from the target data.
		targets = new Matrix<Double>(tData.getSize().getRows(), outputCount);
		for(int j=0;j<targetColumns.size();j++) {
			for(int i=0;i<tData.getSize().getRows();i++) {
				targets.set(i, j, tData.get(i, targetColumns.get(j)));
			}
		}
		data = new Matrix<Double>(tData.getSize().getRows(), tData.getSize().getCols() + 1);
		for(int i=0;i<tData.getSize().getRows();i++) {
			for(int j=0;j<tData.getSize().getCols()+1;j++) {
				if (j <tData.getSize().getCols()) {
					data.set(i, j, tData.get(i,j));
				} else {
					// allow for network bias.
					data.set(i, j, -1.0);
				}
			}
		}
		// remove the data columns from the data.
		for(int j=0;j<targetColumns.size();j++) {
			data.removeColumn(targetColumns.get(j));
			if (j < targetColumns.size() - 1) {
				// if the next target column is larger than the one we
				// have just removed, then decrement it.
				if (targetColumns.get(j + 1) > targetColumns.get(j)) {
					targetColumns.set(j + 1, targetColumns.get(j + 1) - 1);
				}
			}
		}
	}
	
	/**
	 * Get the unit activator for the current typ of activation.
	 * @param t
	 * @return
	 */
	private IUnitActivator getActivator(ActivationType t) {
		IUnitActivator a = null;
		if (this.hiddenActivation == ActivationType.PERCEPTRON) {
			a = new PerceptronActivator();
		} else if (this.hiddenActivation == ActivationType.SIGMOID) {
			a = new SigmoidActivator();
		} else if (this.hiddenActivation == ActivationType.TANSIG) {
			a = new TanSigmoidActivator();
		}
		return a;
	}
	
	/**
	 * Process the training set until
	 * convergence of the squared error is reached
	 * or the total number of epochs have expired.
	 */
	public void learn() throws Exception {
		this.errors = new Vector<Double>();
		if (this.trainingSet == null) throw new Exception("Training data cannot be null.");
		// initialise the training data.
		this.initialiseData(this.trainingSet);
		// iterate until total number of epochs 
		for(int cnt = 0;cnt < this.epoch;cnt++) {
			// a propogation table for the back propogation of the error.
			List< List< IMatrix<Double> > > propogation = new ArrayList< List< IMatrix<Double> > >();
			// feedforward through each layer.
			IMatrix<Double> in = this.data;
			this.feedForward(propogation, in);
			// perform backpropogation.
			IMatrix<Double> errorsMat = this.backPropogate(propogation);
			// determine the squared error.
			double err = this.calculateSquaredError(errorsMat);
			errors.add(err);
			// update observers indicating the error has been updated.
			update();
			if (err <= this.errorThreshold) 
				break;
		}
	}
	
	/**
	 * Perform a test using the test data.
	 * Calculate the squared errors
	 * 
	 * @throws Exception
	 */
	public double test() throws Exception {
		this.errors = new Vector<Double>();
		if (this.testSet == null) throw new Exception("Test data cannot be null.");
		this.initialiseData(this.testSet);
		// a propogation table for the back propogation of the error.
		List< List< IMatrix<Double> > > propogation = new ArrayList< List< IMatrix<Double> > >();
		// feedforward through each layer.
		IMatrix<Double> in = this.data;
		this.feedForward(propogation, in);
		// perform backpropogation.
		IMatrix<Double> errors = this.backPropogate(propogation);
		// determine the squared error.
		double err = this.calculateSquaredError(errors);
		return err;
	}
	
	/**
	 * Peform the feed forward of data through the network.
	 * @param propogation
	 * @return
	 */
	public List<IMatrix<Double>> feedForward(List< List< IMatrix<Double> > > propogation, IMatrix<Double> in) {
		IUnitActivator hActivator = this.getActivator(this.hiddenActivation);
		IUnitActivator oActivator = this.getActivator(this.outputActivation);
		//	 propograte through each layer.
		for (int i=0;i<this.hiddenWeights.size();i++) {
			List<IMatrix<Double>> out = hActivator.activate(this.hiddenWeights.get(i), in, this.energy, this.temperature);
			propogation.add(out);
			in = out.get(0);
		}
		// propogate through the output layer.
		List<IMatrix<Double>> out = oActivator.activate(this.outputWeights, in.transform(), this.energy, this.temperature);
		propogation.add(out);
		return out;
	}
	
	/**
	 * Back propogate through the network.
	 * 
	 * @param propogation
	 * @return the output errors.
	 */
	public IMatrix<Double> backPropogate(List< List< IMatrix<Double> > > propogation) {
		// calculate the errors.
		int sz = propogation.size() - 1;
		List<IMatrix<Double>> out = propogation.get(sz);
		IMatrix<Double> errorsMat = this.errors(out.get(0), this.targets.transform());
		// calculate the gradient.
		// output layer.
		// g = e*do'
		IMatrix<Double> gradient = errorsMat.pointwiseMultiply(out.get(1));
		// dw = r*g*x' = r * mom * g * x'
		// r*mom
		Double r = this.learningRate * this.momentum;
		out = propogation.get(sz - 1);
		sz--;
		// dw = r*g*x' = r * mom * g * x'
		IMatrix<Double> deltaWeights = out.get(0).multiply(gradient.transform()).multiply(r);
		// now we update the outputWeights.
		// w = w + dw
		this.outputWeights = this.outputWeights.sum(deltaWeights);
		IMatrix<Double> pw = this.outputWeights;
		// now we transfer through the network.
		// TODO: work out the right transforms for the hidden layers.
		for(int j=this.hiddenWeights.size() - 1; j >= 0; j--) {
			// for hidden layer the error is:
			// gj = dh'*gk*wk
			IMatrix<Double> e = gradient.transform().multiply(pw.transform());
			gradient = out.get(1).pointwiseMultiply(e.transform());
			if (sz - 1 >= 0) {
				out = propogation.get(sz - 1);
				sz--;
				// the previous layer is a hidden layer.
				deltaWeights = out.get(0).multiply(gradient.transform()).multiply(r);
			} else {
				// at the previous layer we have the input layer.
				deltaWeights = data.transform().multiply(gradient.transform()).multiply(r);
			}
			// deltaWeights must be the same dimension as hiddenWeights j
			this.hiddenWeights.set(j, this.hiddenWeights.get(j).sum(deltaWeights));
		}
		return errorsMat;
	}
	/**
	 * Calculate the errors.
	 * @param out
	 * @param targets
	 * @return
	 */
	public IMatrix<Double> errors(IMatrix<Double> out, IMatrix<Double> targets) {
		return targets.subtract(out);
	}
	
	/**
	 * Calculate the average squared error.
	 * @param errors
	 * @return
	 */
	public double calculateSquaredError(IMatrix<Double> errors) {
		double sum = 0;
		errors = errors.normalise().power(2.0);
		for(int i=0;i<errors.getSize().getRows();i++) {
			double rowSum = 0.0;
			for(int j=0;j<errors.getSize().getCols();j++) {
				rowSum += errors.get(i, j).doubleValue();
			}
			sum += rowSum;
		}
		return ( 1.0 / errors.getSize().getRows() ) * sum;
	}
	
	/**
	 * Perform scalar prediction.
	 * This only applies to a single row of input data and 
	 * a single output node.
	 * @return
	 */
	public Double predict() throws Exception {
		this.errors = new Vector<Double>();
		if (this.trainingSet == null) throw new Exception("Training data cannot be null.");
		// initialise the training data.
		this.initialiseData(this.trainingSet);
		// a propogation table for the back propogation of the error.
		List< List< IMatrix<Double> > > propogation = new ArrayList< List< IMatrix<Double> > >();
		// feedforward through each layer.
		IMatrix<Double> in = this.data;
		List<IMatrix<Double>> out = this.feedForward(propogation, in);
		IMatrix<Double> o = out.get(0);
		// return the first output value.
		return o.get(0, 0).doubleValue();
	}

	
	/**
	 * @return the bias
	 */
	public Double getBias() {
		return bias;
	}

	/**
	 * @param bias the bias to set
	 */
	public void setBias(Double bias) {
		this.bias = bias;
	}

	/**
	 * @return the energy
	 */
	public Double getEnergy() {
		return energy;
	}

	/**
	 * @param energy the energy to set
	 */
	public void setEnergy(Double energy) {
		this.energy = energy;
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
	 * @return the hiddenActivation
	 */
	public ActivationType getHiddenActivation() {
		return hiddenActivation;
	}

	/**
	 * @param hiddenActivation the hiddenActivation to set
	 */
	public void setHiddenActivation(ActivationType hiddenActivation) {
		this.hiddenActivation = hiddenActivation;
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
	 * @return the hiddenWeights
	 */
	public List<IMatrix<Double>> getHiddenWeights() {
		return hiddenWeights;
	}

	/**
	 * @param hiddenWeights the hiddenWeights to set
	 */
	public void setHiddenWeights(List<IMatrix<Double>> hiddenWeights) {
		this.hiddenWeights = hiddenWeights;
	}

	/**
	 * @return the initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * @param initialized the initialized to set
	 */
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
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
	 * @return the learningRate
	 */
	public Double getLearningRate() {
		return learningRate;
	}

	/**
	 * @param learningRate the learningRate to set
	 */
	public void setLearningRate(Double learningRate) {
		this.learningRate = learningRate;
	}

	/**
	 * @return the momentum
	 */
	public Double getMomentum() {
		return momentum;
	}

	/**
	 * @param momentum the momentum to set
	 */
	public void setMomentum(Double momentum) {
		this.momentum = momentum;
	}

	/**
	 * @return the outputActivation
	 */
	public ActivationType getOutputActivation() {
		return outputActivation;
	}

	/**
	 * @param outputActivation the outputActivation to set
	 */
	public void setOutputActivation(ActivationType outputActivation) {
		this.outputActivation = outputActivation;
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
	 * @return the outputWeights
	 */
	public IMatrix<Double> getOutputWeights() {
		return outputWeights;
	}

	/**
	 * @param outputWeights the outputWeights to set
	 */
	public void setOutputWeights(IMatrix<Double> outputWeights) {
		this.outputWeights = outputWeights;
	}

	/**
	 * @return the targetColumn
	 */
	public List<Integer> getTargetColumns() {
		return targetColumns;
	}

	/**
	 * @param targetColumn the targetColumn to set
	 */
	public void setTargetColumn(List<Integer> targetColumns) {
		this.targetColumns = targetColumns;
	}

	/**
	 * @return the temperature
	 */
	public Double getTemperature() {
		return temperature;
	}

	/**
	 * @param temperature the temperature to set
	 */
	public void setTemperature(Double temperature) {
		this.temperature = temperature;
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
	 * A convenience method to load a neural network from a serial file.
	 * @param filename
	 * @return
	 */
	public static MlpNeuralNetwork loadNetwork(String filename) {
		try {
			File fin = new File(filename);
			if (!fin.exists()) {
				return null;
			}
			FileInputStream is = new FileInputStream(fin);
			ObjectInputStream ois = new ObjectInputStream(is);
			return (MlpNeuralNetwork)ois.readObject();
		} catch(Exception e) {
		}
		return null;
	}
	
	public static void writeNetwork(MlpNeuralNetwork net, String file) {
		File out = new File(file);
		if (out.exists()) {
			out.delete();
		}
		try {
			FileOutputStream os = new FileOutputStream(out);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(net);
			oos.close();
		} catch(Exception e) {
			
		}
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
	 * @return the errorThreshold
	 */
	public Double getErrorThreshold() {
		return errorThreshold;
	}

	/**
	 * @param errorThreshold the errorThreshold to set
	 */
	public void setErrorThreshold(Double errorThreshold) {
		this.errorThreshold = errorThreshold;
	}

	/**
	 * @return the targets
	 */
	public IMatrix<Double> getTargets() {
		return targets;
	}

	/**
	 * @param targets the targets to set
	 */
	public void setTargets(IMatrix<Double> targets) {
		this.targets = targets;
	}

	/**
	 * @param targetColumns the targetColumns to set
	 */
	public void setTargetColumns(List<Integer> targetColumns) {
		this.targetColumns = targetColumns;
	}
	
}
