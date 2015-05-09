/**
 * 
 */
package au.id.cpd.algorithms.algebra;

import au.id.cpd.algorithms.data.IMatrix;
import au.id.cpd.algorithms.data.Matrix;

/**
 * @author cd
 *
 */
public class EuclideanDistanceMatrixOperation extends DistanceMatrixOperation {

	/**
	 * Euclidean distance operation
	 * calculates euclidean distance between two matrices.
	 * Will return a matrix with the distance between each column
	 * of the input matrices stored in the output matrix.
	 */
	public EuclideanDistanceMatrixOperation() {
		super();
	}
	
	
	/** Calculate the distance between A and B using sum of root mean square.
	 *  A and B must be of the same dimension.
	 *  @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 */
	public IMatrix<Double> operate(IMatrix<Double> A, IMatrix<Double> B, Distance type) {
		if (!A.getSize().equals(B.getSize())) return null;
		if (type == Distance.COLUMNS) {
			return getColumnDistance(A, B);
		} else {
			return getRowDistance(A, B);
		}
	}
	
	/**
	 * Return column vector containing distances between columns.
	 * @param A
	 * @param B
	 * @return
	 */
	private IMatrix<Double> getColumnDistance(IMatrix<Double> A, IMatrix<Double> B) {
		double e = 0.0;
		IMatrix<Double> distance = new Matrix<Double>(A.getSize().getCols(), 1);
		for(int j=0;j<A.getSize().getCols();j++) {
			e = 0.0;
			for(int i=0;i<A.getSize().getRows();i++) {
				e = e + Math.pow(A.get(i,j).doubleValue() - B.get(i, j).doubleValue(), 2.0);
			}
			distance.set(j, 0, Math.sqrt(e));
		}
		return distance;
	}
	
	/**
	 * Return column vector containing distances between rows.
	 * @param A
	 * @param B
	 * @return
	 */
	private IMatrix<Double> getRowDistance(IMatrix<Double> A, IMatrix<Double> B) {
		IMatrix<Double> distance = new Matrix<Double>(A.getSize().getRows(), 1);
		double e = 0.0;
		for(int i=0;i<A.getSize().getRows();i++) {
			e = 0.0;
			for(int j=0;j<A.getSize().getCols();j++) {
				e = e + Math.pow(A.get(i,j).doubleValue() - B.get(i, j).doubleValue(), 2.0);
			}
			distance.set(i, 0, Math.sqrt(e));
		}
		return distance;
	}


	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.IMatrix)
	 */
	public IMatrix<Double> operate(IMatrix<Double> input) {
		return super.operate(input);
	}
	
		
	
}
