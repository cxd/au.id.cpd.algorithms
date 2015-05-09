/**
 * 
 */
package au.id.cpd.algorithms.classifier.ANN;

import java.util.ArrayList;
import java.util.List;

import au.id.cpd.algorithms.data.IMatrix;
import au.id.cpd.algorithms.data.Matrix;

/**
 * @author cd
 *
 */
public class TanSigmoidActivator implements IUnitActivator, java.io.Serializable {

	/**
	 * internal serial version id
	 */
	static final long serialVersionUID = -2503681172168726678L;
	
	/**
	 * Sigmoid activation function.
	 * Returns 1/(1 + euler^-input)
	 * @see au.id.cpd.algorithms.classifier.ANN.IUnitActivator#activate(double)
	 */
	public double activate(double input) {
		// e = eulers number.
		// for scaling between -1 .. 1 in output.
		/*
		 * equation is typically
		 * 
		 * a * tanh(b*v) where (a,b) > 0
		 */
		return Math.tanh(input); 
		// 2.0 / (1.0 + Math.exp(-1*input)) - 1;
	}
	
	/**
	 * Differentiate the activation function with
	 * respect to its result.
	 * @return
	 */
	public double differentiate(double result) {
		/*
		 * differentiation of the tansig function is
		 * 
		 * b / a * (a - y ) * ( a + y )
		 * 
		 * In the case below 
		 */
		return (1 - result) * ( 1 + result );
	}
	
	/**
	 * Activate the neuron using the supplied matrices
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
		IMatrix<Double> o = h.tanh();
		IMatrix<Double> d = o.sum(1.0).multiply(o.subtractFrom(1.0));
		out.add(o);
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
