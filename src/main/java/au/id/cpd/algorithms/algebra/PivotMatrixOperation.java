/**
 * 
 */
package au.id.cpd.algorithms.algebra;

import java.util.*;
import au.id.cpd.algorithms.data.*;

/**
 * The Pivot Matrix operation performs a partial pivot of the supplied matrix.
 * This will only operate on square matrices.
 * 
 * @author Chris Davey <cd@cpd.id.au>
 *
 * @reference:
 * Java For Number Crunchers (JNC) by Ronald Mak.
 * Pub Date	: October 29, 2002
 * ISBN	: 0-13-046041-9
 * Chapter 10.
 */
public class PivotMatrixOperation implements IMatrixOperation {

	/**
	 * The resulting pivot matrix.
	 */
	private IMatrix<Double> matrix;
	/**
	 * The source matrix.
	 */
	private IMatrix<Double> source;
	
	/**
	 * Permutations stores the swapped columns.
	 * It is a column vector with n rows.
	 */
	private IMatrix<Double> permutations;
	/**
	 * Scales is an optional column vector.
	 * Allows scaling to be applied to input matrix.
	 * Has the same number of rows as the supplied matrix.
	 */
	private IMatrix<Double> scales;
	/**
	 * The number of exchanges that have taken place
	 * during the pivot operation.
	 */
	private int exchangeCount;
	
	private boolean singular;
	
	public PivotMatrixOperation() {
		scales = null;
		permutations = null;
		matrix = null;
		exchangeCount = 0;
		singular = false;
	}
	
	
	
	/**
	 * Pivot the input matrix.
	 * This will only be applied to square non-singular matrices.
	 * 
	 * This is equivalent to the forwardElimination algorithm 
	 * as shown in the implementation provided by Ronald Mak in Chapter 10 of JNC. 
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 */
	public IMatrix<Double> operate(IMatrix<Double> input) {
		if (input.getSize().getRows() != input.getSize().getCols())
			return null;
		source = input;
		matrix = source.clone();
		if (scales == null) {
			scales = Matrix.ones(source.getSize().getRows(), 1);
		}
		// initialise the permutations to store the original row indices.
		permutations = Matrix.zeroes(source.getSize().getRows(), 1);
		for(int i=0;i<source.getSize().getRows();i++) {
			permutations.set(i, 0, i);
		}
		forwardElimination();
		return matrix;
	}
	
	/**
	 * Perform forward elimination on the source matrix
	 * and populate the output matrix.
	 * This will only be applied to square nonsingular matrices.
	 */
	private void forwardElimination() {
		int maxPivotRow = 0;
		// current permutation pivot row.
		int pr = 0;
		// current value.
		Double curValue = 0.0;
		for(int pIdx=0;pIdx<matrix.getSize().getRows() - 1; pIdx++) {
			Double maxValue = 0.0;
			// search for the largest pivot value below the current row.
			for(int n=pIdx;n<matrix.getSize().getRows(); n++) {
				// use the permutations list to lookup the row index
				pr = permutations.get(n, 0).intValue();
				Double v = Math.abs(matrix.get(pr, pIdx).doubleValue())*scales.get(pr, 0).doubleValue();
				if (maxValue < v) {
					maxValue = v;
					maxPivotRow = n;
				}
			}
			
			
			// if the matrix is singular we cannot proceed
			// cannot proceed.
			if (maxValue == 0) {
				singular = true; 
				return;
			}
			// swap the rows if necessary using the permutations array.
			if (maxPivotRow != pIdx) {
				int temp = permutations.get(pIdx, 0).intValue();
				permutations.set(pIdx, 0, maxPivotRow);
				permutations.set(maxPivotRow, 0, temp);
				exchangeCount++;
			}
			
			pr = permutations.get(pIdx, 0).intValue();
			curValue = matrix.get(pr, pIdx).doubleValue();
			// iterate down the column and perform the elimination.
			for(int n=pIdx+1;n<matrix.getSize().getRows();n++) {
				int temp = permutations.get(n,0).intValue();
				// calculate the multiple.
				Double multiple = matrix.get(temp, pIdx).doubleValue() / curValue;
				// if the multiple is not 0 then eliminate it - it is an unknown variable (eg x2).
				if (multiple != 0.0) {
					for(int c = pIdx + 1;c<matrix.getSize().getCols();c++) {
						Double v = matrix.get(temp, c).doubleValue();
						// subtract the multiple at the current row pr and column c.
						v -= multiple*matrix.get(pr,c).doubleValue();
						// redefine the local value.
						matrix.set(temp, c, v);
					}
				}
			}
			
		}
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(java.lang.Number)
	 */
	public Double operate(Number input) {
		// TODO Auto-generated method stub
		return null;
	}



	/**
	 * @return the matrix
	 */
	public IMatrix<Double> getMatrix() {
		return matrix;
	}



	/**
	 * @param matrix the matrix to set
	 */
	public void setMatrix(Matrix<Double> matrix) {
		this.matrix = matrix;
	}



	/**
	 * @return the permutations
	 */
	public IMatrix<Double> getPermutations() {
		return permutations;
	}



	/**
	 * @param permutations the permutations to set
	 */
	public void setPermutations(IMatrix<Double> permutations) {
		this.permutations = permutations;
	}



	/**
	 * @return the scales
	 */
	public IMatrix<Double> getScales() {
		return scales;
	}



	/**
	 * @param scales the scales to set
	 */
	public void setScales(IMatrix<Double> scales) {
		this.scales = scales;
	}



	/**
	 * @return the singular
	 */
	public boolean isSingular() {
		return singular;
	}



	/**
	 * @param singular the singular to set
	 */
	public void setSingular(boolean singular) {
		this.singular = singular;
	}



	/**
	 * @return the source
	 */
	public IMatrix<Double> getSource() {
		return source;
	}



	/**
	 * @param source the source to set
	 */
	public void setSource(Matrix<Double> source) {
		this.source = source;
	}



	/**
	 * @return the exchangeCount
	 */
	public int getExchangeCount() {
		return exchangeCount;
	}



	/**
	 * @param exchangeCount the exchangeCount to set
	 */
	public void setExchangeCount(int exchangeCount) {
		this.exchangeCount = exchangeCount;
	}

}
