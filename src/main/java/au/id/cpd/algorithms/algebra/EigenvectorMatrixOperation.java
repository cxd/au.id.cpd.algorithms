package au.id.cpd.algorithms.algebra;

import java.util.*;
import au.id.cpd.algorithms.data.*;

/**
 * The eigenvector matrix operation calculates the principle components 
 * of a square matrix.
 * @author Chris Davey <cd@cpd.id.au>
 *
 */
public class EigenvectorMatrixOperation implements IMatrixOperation {

	private EigenvalueDecomposition eig;
	
	private IMatrix<Double> eigenvalues;
	private IMatrix<Double> eigenvectors;
	
	public EigenvectorMatrixOperation() {
	}
	
	/**
	 * Compute the eigenvalue and eigvector matrix.
	 * The result is an mx2 matrix containing the complex numbers
	 * representing the eigenvalue.
	 * 
	 * @return the eigenvalue matrix.
	 */
	public IMatrix<Double> operate(IMatrix<Double> input) {
		eig = new EigenvalueDecomposition(input);
		eigenvalues = eig.getD();
		eigenvectors = eig.getV();
		return eigenvalues;
	}

	public Double operate(Number input) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * Access the eigenvectors Matrix.
	 * @return
	 */
	public IMatrix<Double> getEigenvectors() {
		return eigenvectors;
	}
	
	/**
	 * Access the eigenvalues.
	 * @return
	 */
	public IMatrix<Double> getEigenvalues() {
		return eigenvalues;
	}
	
}
