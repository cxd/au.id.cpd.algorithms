/**
 * 
 */
package au.id.cpd.algorithms.algebra;

import java.util.*;
import au.id.cpd.algorithms.data.*;

/**
 * @author Chris Davey <cd@cpd.id.au>
 * 
 * @reference:
 * Java For Number Crunchers (JNC) by Ronald Mak.
 * Pub Date	: October 29, 2002
 * ISBN	: 0-13-046041-9
 * Chapter 10.
 */
public class LUDecompositionMatrixOperation extends PivotMatrixOperation {
	
	/**
	 * Constructor.
	 *
	 */
	public LUDecompositionMatrixOperation() {
		super();
	}
	
	/** Perform LUDecomposition upon the matrix
	 * using the PivotMatrixOperation and scaling the values in the matrix.
	 * 
	 * This operation can only be applied to square non-singular matrices.
	 * 
	 * This uses the same implementation of the technique shown in
	 * the book JNC (above).
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 * @see au.id.cpd.algorithms.algebra.PivotMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 */
	public IMatrix<Double> operate(IMatrix<Double> input) {
		if (input.getSize().getRows() != input.getSize().getCols())
			return null;
		// calculate the scaled factors.
		IMatrix<Double> scales = Matrix.ones(input.getSize().getRows(), 1);
		for(int i=0;i<input.getSize().getRows();i++) {
			Double maxValue = Double.MIN_VALUE;
			for(int j=0;j<input.getSize().getCols();j++) {
				Double v = input.get(i,j).doubleValue();
				if (maxValue < v)
					maxValue = v;
			}
			if ( (maxValue != Double.MIN_VALUE) && (maxValue != 0) ) {
				scales.set(i, 0, 1.0/maxValue);
			}
		}
		this.setScales(scales);
		IMatrix<Double> result = super.operate(input);
		if (result != null) {
			if ((result.get(this.getPermutations().get(result.getSize().getRows() - 1, 0).intValue(), result.getSize().getCols() - 1).doubleValue() == 0.0)) {
				this.setSingular(true);
				result = null;
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(java.lang.Number)
	 */
	public Double operate(Number input) {
		// TODO Auto-generated method stub
		return null;
	}

}
