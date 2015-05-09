package au.id.cpd.algorithms.algebra;

import au.id.cpd.algorithms.algebra.DistanceMatrixOperation.Distance;
import au.id.cpd.algorithms.data.IMatrix;
import au.id.cpd.algorithms.data.IMatrixOperation;

/**
 * 
 * @author cd
 *
 */
public interface IMultiplierOperation extends IMatrixOperation {

	/**
	 * Produce product C of A*B.
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 */
	void operate(IMatrix<Double> A, IMatrix<Double> B, IMatrix<Double> C);
	
	/**
	 * Produce product A*B
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 */
	IMatrix<Double> operate(IMatrix<Double> A, IMatrix<Double> B);
	
}
