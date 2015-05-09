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
public class SigmoidActivator implements IUnitActivator, java.io.Serializable {

	/**
	 * internal serial version id
	 */
	static final long serialVersionUID = -4103750496139830439L;
	
	/**
	 * Sigmoid activation function.
	 * Returns 1/(1 + euler^-input)
	 * @see au.id.cpd.algorithms.classifier.ANN.IUnitActivator#activate(double)
	 */
	public double activate(double input) {
		// e = eulers number.
		// for scaling between 0 .. 1 in output.
		return 1.0 / (1.0 + Math.exp(-1*input));
		// for scaling between -1 .. 1 in output.
		// return 2.0 / (1.0 + Math.exp(-1*input)) - 1;
	}
	
	/**
	 * Differentiate the activation function with
	 * respect to its result.
	 * @return
	 */
	public double differentiate(double result) {
		return result * ( 1.0 - result );
	}
	
	/**
	 * Activate the neuron using the supplied matrices
	 * 
	 * @param W
	 * @param X
	 * @param A
	 * @param T
	 * @return List of results where index 0 is the result and index 1 is the differentiation of the result. 
	 */
	public List<IMatrix<Double>> activate(IMatrix<Double> W, IMatrix<Double> X, double A, double T) {
		// temperature cannot be 0.
		if (T == 0) 
			T = 1.0;
		List<IMatrix<Double>> out = new ArrayList<IMatrix<Double>>();
		// h = w'x
		IMatrix<Double> h = W.transform().multiply(X.transform());
		IMatrix<Double> a = Matrix.ones(h.getSize().getRows(), h.getSize().getCols());
		a = a.multiply(A);
		// A / (1 + exp(-Th) 
		IMatrix<Double> o = h.multiply(-1.0*T);
		// 1 / n = 0.1*n
		o = a.pointwiseDivide(o.exp().sum(1.0));
		out.add(o);
		// differentiate.
		// do = T * o * (A - o)
		IMatrix<Double> d = o.multiply(T).pointwiseMultiply(a.subtract(o));
		out.add(d);
		return out;
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
