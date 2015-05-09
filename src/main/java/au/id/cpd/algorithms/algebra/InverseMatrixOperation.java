/**
 * 
 */
package au.id.cpd.algorithms.algebra;

import java.util.*;

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
public class InverseMatrixOperation extends DeterminantMatrixOperation {

	/**
	 * The resulting inverted matrix.
	 */
	private Matrix<Double> inverse;
	/**
	 * The condition of the matrix's representation of the system of equations
	 * how well it represents the system of equations.
	 * condition = ||A|| * ||A-1||
	 */
	private double condition;
	
	/**
	 * Constructor.
	 */
	public InverseMatrixOperation() {
		super();
	}
	
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 */
	public Matrix<Double> operate(Matrix<Double> input) {
		if (input.getSize().getRows() != input.getSize().getCols())
			return null;
		if (super.operate(input) == null) return null;
		IMatrix<Double> identity = Matrix.identity(input.getSize().getRows(), input.getSize().getCols());
		inverse = new Matrix<Double>(input.getSize().getRows(), input.getSize().getCols());
		// reuse the solver to avoid recomputing the LUDecomposition too many times.
		SolveLinearEquationMatrixOperation solver = new SolveLinearEquationMatrixOperation();
		
		for(int j=0;j<this.getMatrix().getSize().getCols();j++) {
			IMatrix<Double> solution = null;
			// get the identity column
			List<Double> col = identity.getColumn(j);
			Matrix<Double> output = new Matrix<Double>(this.getMatrix().getSize().getRows(), 1);
			output.addAll(col);
			// solve using the identity column
			if (j == 0) {
				solution = solver.solve(input, output);
			} else {
				solution = solver.solve(output);
			}
			if (solution == null) return null;
			// assign the solution to the current column of the inverse matrix.
			for(int i=0;i<solution.getSize().getRows();i++) {
				inverse.set(i, j, solution.get(i, 0));
			}
		}
		return inverse;
	}
	
	/**
	 * Calculate the condition of the matrix.
	 * Precondition: must compute the matrix inverse before hand.
	 * @return double - the condition of the matrix.
	 */
	public double calculateCondition() {
		condition = this.getMatrix().euclideanNorm() * inverse.euclideanNorm();
		return condition;
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(java.lang.Number)
	 */
	public Double operate(Number input) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * @return the inverse
	 */
	public IMatrix<Double> getInverse() {
		return inverse;
	}


	/**
	 * @param inverse the inverse to set
	 */
	public void setInverse(Matrix<Double> inverse) {
		this.inverse = inverse;
	}


	/**
	 * @return the condition
	 */
	public double getCondition() {
		return condition;
	}


	/**
	 * @param condition the condition to set
	 */
	public void setCondition(double condition) {
		this.condition = condition;
	}

}
