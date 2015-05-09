/**
 * 
 */
package au.id.cpd.algorithms.classifier.ANN;

import java.util.List;

import au.id.cpd.algorithms.data.IMatrix;
import au.id.cpd.algorithms.data.Matrix;

/**
 * @author cd
 *
 */
public interface IUnitActivator {
	/**
	 * Apply the activation function to the input.
	 * @param input
	 * @return
	 */
	public double activate(double input);
	/**
	 * Differentiate the activation function with
	 * respect to its result.
	 * @return
	 */
	public double differentiate(double result);
	
	/**
	 * Activate the neuron using the supplied matrices
	 * 
	 * @param W - weight matrix
	 * @param X - input matrix
	 * @param A - energy
	 * @param T - temperature
	 * @return List of results where index 0 is the result and index 1 is the differentiation of the result. 
	 */
	public List<IMatrix<Double>> activate(IMatrix<Double> W, IMatrix<Double> X, double A, double T);
		
}
