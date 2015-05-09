/**
 * 
 */
package au.id.cpd.algorithms.classifier.ANN;

import java.util.*;
import au.id.cpd.algorithms.data.*;

/**
 * @author cd
 *
 */
public class PerceptronActivator implements IUnitActivator, IMatrixOperation, java.io.Serializable {
	
	/**
	 * Internal serial version id
	 */
	static final long serialVersionUID = -1201265180873709109L;

	/**
	 * A perceptron activation function.
	 * Linear approximator.
	 * Returns 1 if input >= 0
	 * -1 otherwise.
	 */
	public double activate(double input) {
		if (input >= 0) { 
			return 1.0;
		} else {
			return -1.0;
		}
	}
	
	/**
	 * Differentiate the activation function with
	 * respect to its result.
	 * The linear activation function is not differentiable.
	 * @return
	 */
	public double differentiate(double result) {
		return 1;
	}
	
	/**
	 * Activate the neuron using the supplied matrices
	 * 
	 * The perceptron activation function is not differentiable.
	 * 
	 * @param W - weight matrix
	 * @param X - input matrix
	 * @param A - energy
	 * @param T - temperature
	 * @return List of results where index 0 is the result and index 1 is the differentiation of the result. 
	 */
	public List<IMatrix<Double>> activate(IMatrix<Double> W, IMatrix<Double> X, double A, double T) {
		List<IMatrix<Double>> out = new ArrayList<IMatrix<Double>>();
		// h = w'x
		IMatrix<Double> h = W.transform().multiply(X.transform());
		h = h.operate(this);
		out.add(h);
		// not differentiable, so we initialise the do to ones.
		IMatrix<Double> d = Matrix.ones(h.getSize().getRows(), h.getSize().getCols());
		out.add(d);
		return out;
	}
	
	/**
	 * Operation to be performed on the member of a matrix
	 * @param input
	 * @return
	 */
	public Double operate(Number input) {
		return this.activate(input.doubleValue());
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 */
	public IMatrix<Double> operate(IMatrix<Double> input) {
		IMatrix<Double> result = new Matrix<Double>(input.getSize().getRows(), input.getSize().getCols());
		for(int i=0;i<input.getSize().getRows();i++) {
			for(int j=0;j<input.getSize().getCols();j++) {
				result.set(i, j, this.activate(input.get(i,j).doubleValue()));
			}
		}
		return result;
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
