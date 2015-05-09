/**
 * 
 */
package au.id.cpd.algorithms.classifier.ANN;

import au.id.cpd.algorithms.adt.*;

/**
 * @author cd
 *
 */
public class NeuralNode<Double> extends GraphNode<Double> implements java.io.Serializable {
	/**
	 * serial version id
	 */
	static final long serialVersionUID = -1171962505566176151L;
	/**
	 * Type of node.
	 */
	private NeuralNodeType nodeType;
	/**
	 * Type of activator.
	 */
	private ActivationType gType;
	/**
	 * Internal activation function.
	 */
	private IUnitActivator activator;
	/**
	 * Output from node.
	 */
	private double output;
	/**
	 * Input from node.
	 */
	private double input;
	/**
	 * Internal error.
	 */
	private double error;
	
	/**
	 * Constructor.
	 */
	public NeuralNode() {
		super();
		this.nodeType = NeuralNodeType.INPUT;
		this.gType = ActivationType.PERCEPTRON;
		this.activator = new PerceptronActivator();
	}
	/**
	 * Construct with data.
	 * @param nType
	 * @param data
	 * @throws Exception
	 */
	public NeuralNode(NeuralNodeType nType, Double data) throws Exception {
		super(data);
		this.nodeType = nType;
		this.gType = ActivationType.PERCEPTRON;
		this.activator = new PerceptronActivator();
	}
	/**
	 * Construct with data.
	 * @param nType
	 * @param data
	 * @throws Exception
	 */
	public NeuralNode(NeuralNodeType nType, ActivationType act, Double data) throws Exception {
		super(data);
		this.nodeType = nType;
		this.gType = act;
		try {
			this.activator = (IUnitActivator)Class.forName(act.getClassName()).newInstance();
		} catch(Exception e) {
			this.activator = new PerceptronActivator(); // failed to instantiate activation.
		}
	}
	/**
	 * Activate the output for the current input.
	 * @return
	 */
	public double activate() {
		this.output = this.activator.activate(this.input);
		return this.output;
	}
	/**
	 * Activate the output for the current input.
	 * @return
	 */
	public double activate(double input) {
		this.input = input;
		if (this.nodeType != NeuralNodeType.INPUT)
			this.output = this.activator.activate(this.input);
		else 
			this.output = input;
		return this.output;
	}
	/**
	 * @return the activator
	 */
	public IUnitActivator getActivator() {
		return activator;
	}
	/**
	 * @param activator the activator to set
	 */
	public void setActivator(IUnitActivator activator) {
		this.activator = activator;
	}
	/**
	 * @return the error
	 */
	public double getError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(double error) {
		this.error = error;
	}
	/**
	 * @return the gType
	 */
	public ActivationType getActivationType() {
		return gType;
	}
	/**
	 * @param type the gType to set
	 */
	public void setActivationType(ActivationType type) {
		gType = type;
	}
	/**
	 * @return the input
	 */
	public double getInput() {
		return input;
	}
	/**
	 * @param input the input to set
	 */
	public void setInput(double input) {
		this.input = input;
	}
	/**
	 * @return the nodeType
	 */
	public NeuralNodeType getNodeType() {
		return nodeType;
	}
	/**
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType(NeuralNodeType nodeType) {
		this.nodeType = nodeType;
	}
	/**
	 * @return the output
	 */
	public double getOutput() {
		return output;
	}
	/**
	 * @param output the output to set
	 */
	public void setOutput(double output) {
		this.output = output;
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
