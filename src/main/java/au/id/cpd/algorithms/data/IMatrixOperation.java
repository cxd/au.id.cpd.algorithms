package au.id.cpd.algorithms.data;

/**
 * An interface that defines an operation that can be performed on a matrix.
 * @author cd
 *
 */
public interface IMatrixOperation {
	
	/**
	 * Operation to be performed on the matrix
	 * @param input
	 * @return
	 */
	public IMatrix<Double> operate(IMatrix<Double> input);
	
	/**
	 * Operation to be performed on the member of a matrix
	 * @param input
	 * @return
	 */
	public Double operate(Number input);

}
