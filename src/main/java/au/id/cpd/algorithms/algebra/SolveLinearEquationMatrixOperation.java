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
public class SolveLinearEquationMatrixOperation extends LUDecompositionMatrixOperation {

	/**
	 * The matrix of unknowns and their coefficients
	 */
	private IMatrix<Double> matrix;
	/**
	 * The matrix of outputs.
	 */
	private IMatrix<Double> outputs;
	/**
	 * The solution column vector for unknowns.
	 */
	private IMatrix<Double> solution;
	/**
	 * Column vector containing permutations 
	 * after decomposition.
	 */
	private IMatrix<Double> permutations;

	/**
	 * Default constructor.
	 */
	public SolveLinearEquationMatrixOperation() {
		super();
	}
	
	/**
	 * Solve the system of linear equations.
	 * 
	 * In this case the supplied matrix is an (n x n+1) matrix
	 * The mth column contains the output vector.
	 * This column will be copied into a matrix and stored as the output.
	 * It will be removed from a copy of the input matrix forming
	 * a (n x n) matrix that will be used to solve for the unknown variables.
	 *
	 * The result will iterate for improvement until max iterations is completed.
	 * 
	 * This class uses LUDecompositionMatrixOperation to create the pivot matrix.
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 */
	public IMatrix<Double> operate(Matrix<Double> input) {
		
		if (input.getSize().getRows() != input.getSize().getCols() - 1 ) {
			return null;
		}
		// outputs is a list of values length n
		outputs = new Matrix<Double>(input.getSize().getRows(), 1);
		outputs.addAll(input.getColumn(input.getSize().getCols() - 1));
		// matrix is a square matrix.
		matrix = new Matrix<Double>(input.getSize().getRows(), input.getSize().getRows());
		for(int i=0;i<input.getSize().getRows();i++) {
			for(int j=0;j<input.getSize().getRows();j++) {
				matrix.set(i, j, input.get(i, j));
			}
		}
		return this.solve(matrix, outputs);
	}
	
	/**
	 * Solve the system of linear equations.
	 * 
	 * The supplied matrix in this case is a column contains the output vector.
	 *
	 * The precondition to the use of this method is that the system has 
	 * previously been solved using operate.
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 */
	public IMatrix<Double> solve(IMatrix<Double> input, IMatrix<Double> out) {
		if (input.getSize().getRows() != input.getSize().getCols()) {
			return null;
		}
		if (out.getSize().getRows() != input.getSize().getRows()) return null;
		// outputs is a list of values length n
		outputs = out;
		// matrix is a square matrix.
		matrix = new Matrix<Double>(input.getSize().getRows(), input.getSize().getRows());
		for(int i=0;i<input.getSize().getRows();i++) {
			for(int j=0;j<input.getSize().getCols();j++) {
				matrix.set(i, j, input.get(i, j));
			}
		}
		// solution is a column vector with n rows.
		solution = new Matrix<Double>(input.getSize().getRows(), 1);
		// decompose.
		if (super.operate(matrix) == null) return null;
		matrix = super.getMatrix();
		// store the permutations.
		permutations = this.getPermutations();
		// solve the system.
		IMatrix<Double> y = this.forwardSubstitution(outputs);
		solution = this.backwardSubstitution(y);
		// our result may suffer from imprecision.
		// the book uses the epsilon value to
		// improve the precision of the result
		// but uses the floating point precision instead of Double.
		
		// return the solution to the system x is the column vector of values for unknowns.
		return solution;
	}
	
	/**
	 * Solve the system of linear equations.
	 * 
	 * The supplied matrix in this case is a column contains the output vector.
	 *
	 * The precondition to the use of this method is that the system has 
	 * previously been solved using operate.
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 */
	public IMatrix<Double> solve(IMatrix<Double> out) {
		if (out.getSize().getRows() != matrix.getSize().getRows()) return null;
		// outputs is a list of values length n
		outputs = out;
		// store the permutations.
		permutations = this.getPermutations();
		// solve the system.
		IMatrix<Double> y = this.forwardSubstitution(outputs);
		solution = this.backwardSubstitution(y);
		// our result may suffer from imprecision.
		// the book uses the epsilon value to
		// improve the precision of the result
		// but uses the floating point precision instead of Double.
		
		// return the solution to the system x is the column vector of values for unknowns.
		return solution;
	}

	 /**
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(java.lang.Number)
	 */
	public Double operate(Number input) {
		return null;
	}
	
	/**
     * Solve Ly = b for y by forward substitution.
     * L is the lower component of the pivot matrix.
     * From the chapter 10 of JNC.
     * @param b the column vector b
     * @return the column vector y
     */
	private IMatrix<Double> forwardSubstitution(IMatrix<Double> out) {
		IMatrix<Double> y = Matrix.ones(out.getSize().getRows(), 1);
		for(int i=0;i<out.getSize().getRows();i++) {
			int pr = permutations.get(i, 0).intValue();
			Double dotProduct = 0.0;
			for(int c=0;c<i;c++) {
				dotProduct += matrix.get(pr, c).doubleValue() * y.get(c, 0).doubleValue();
			}
			y.set(i, 0, out.get(pr, 0).doubleValue() - dotProduct);
		}
		return y;
	}
	
	/**
     * Solve Ux = y for x by back substitution.
     * U is the upper component of the pivot matrix.
     * From the chapter 10 of JNC.
     * @param y the column vector y
     * @return the solution column vector x
	 */
	private IMatrix<Double> backwardSubstitution(IMatrix<Double> y) {
		IMatrix<Double> x = Matrix.ones(y.getSize().getRows(), 1);
		for(int i=y.getSize().getRows() - 1; i>= 0; i--) {
			int pr = permutations.get(i, 0).intValue();
			Double dotProduct = 0.0;
			for(int c=i+1;c<y.getSize().getRows();c++) {
				dotProduct += matrix.get(pr, c).doubleValue() * x.get(c, 0).doubleValue();
			}
			double tmp = (matrix.get(pr, i).doubleValue() == 0.0) ? 1.0 : matrix.get(pr, i).doubleValue();
			x.set(i, 0, (y.get(pr, 0).doubleValue() - dotProduct) / tmp);
		}
		return x;
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
	public void setMatrix(IMatrix<Double> matrix) {
		this.matrix = matrix;
	}

	/**
	 * @return the outputs
	 */
	public IMatrix<Double> getOutputs() {
		return outputs;
	}

	/**
	 * @param outputs the outputs to set
	 */
	public void setOutputs(IMatrix<Double> outputs) {
		this.outputs = outputs;
	}

	/**
	 * @return the solution
	 */
	public IMatrix<Double> getSolution() {
		return solution;
	}

	/**
	 * @param solution the solution to set
	 */
	public void setSolution(IMatrix<Double> solution) {
		this.solution = solution;
	}

}
