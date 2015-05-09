/**
 * 
 */
package algebra;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.*;
import au.id.cpd.algorithms.data.*;
import au.id.cpd.algorithms.algebra.*;

/**
 * @author cd
 *
 */
public class TestSolveLinearEquations {

	private Matrix<Double> A;
	private Matrix<Double> solutionA;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		A = new Matrix<Double>(3,4);
		double[] test = new double[] {
			1, 1, 2, -1,
			-1, 0, 2, -3,
			0, 1, 2, -2
		};
		for(double d : test) 
			A.add(d);
		solutionA = new Matrix<Double>(3, 1);
		test = new double[] {
		1, 0, -1
		};
		for(double d : test)
			solutionA.add(d);
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.algebra.SolveLinearEquationMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)}.
	 */
	@Test
	public void testSolve01() {
		SolveLinearEquationMatrixOperation op = new SolveLinearEquationMatrixOperation();
		IMatrix<Double> testSolution = op.operate(A);
		assertTrue(testSolution != null);
		assertTrue("testSolution: \n" + testSolution + " solutionA: \n" + solutionA, solutionA.equals(testSolution));
	}

}
