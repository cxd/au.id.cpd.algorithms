/**
 * 
 */
package au.id.cpd.algorithms.algebra;

import au.id.cpd.algorithms.data.IMatrix;
import au.id.cpd.algorithms.data.IMatrixOperation;
import au.id.cpd.algorithms.data.Matrix;

/**
 * @author Chris Davey <cd@cpd.id.au>
 * 
 * @reference:
 * Java For Number Crunchers (JNC) by Ronald Mak.
 * Pub Date	: October 29, 2002
 * ISBN	: 0-13-046041-9
 * Chapter 10.
 */
public class DeterminantMatrixOperation extends LUDecompositionMatrixOperation {

	private Double determinant = 0.0;
	private Double trace = 0.0;
	
	/**
	 * Default ctor.
	 *
	 */
	public DeterminantMatrixOperation() {
		super();
	}
	
	/**
	 * Calculate the determinant of supplied matrix.
	 * Returns a 1x1 matrix containing the determinant value.
	 * 
	 * This operation can only be applied to square non-singular matrices.
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 */
	public IMatrix<Double> operate(IMatrix<Double> input) {
		if (input.getSize().getRows() != input.getSize().getCols())
			return null;
		IMatrix<Double> result = super.operate(input);
		if (result == null) 
			return null;
		IMatrix<Double> permutations = this.getPermutations();
		int exchangeCount = this.getExchangeCount();
		determinant = -1.0;
		trace = 0.0;
		if ((exchangeCount % 2) == 0) {
			determinant = 1.0;
		}
		for(int i=0;i<result.getSize().getRows();i++) {
			determinant *= result.get(permutations.get(i, 0).intValue(), i).doubleValue();
			trace += result.get(permutations.get(i, 0).intValue(), i).doubleValue();
		}
		IMatrix<Double> dMat = Matrix.ones(1,1);
		dMat.set(0, 0, determinant);
		return dMat;
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(java.lang.Number)
	 */
	public Double operate(Number input) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the determinant
	 */
	public Double getDeterminant() {
		return determinant;
	}

	/**
	 * @param determinant the determinant to set
	 */
	public void setDeterminant(Double determinant) {
		this.determinant = determinant;
	}

	/**
	 * @return the trace
	 */
	public Double getTrace() {
		return trace;
	}

	/**
	 * @param trace the trace to set
	 */
	public void setTrace(Double trace) {
		this.trace = trace;
	}

}
