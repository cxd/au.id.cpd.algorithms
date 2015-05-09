package algebra;

import static org.junit.Assert.*;

import au.id.cpd.algorithms.algebra.*;
import au.id.cpd.algorithms.data.*;

import org.junit.Before;
import org.junit.Test;

public class TestEigenDecomposition {

	private IMatrix<Double> M;
	private IMatrix<Double> M2;
	
	@Before
	public void setUp() throws Exception {
		M = new Matrix<Double>(5,5);
		double[] test = new double[] {
				17,    24,     1,     8,    15,
			    23,     5,     7,    14,    16,
			     4,     6,    13,    20,    22,
			    10,    12,    19,    21,    3,
			    11,    18,    25,     2,     9,
		};
		for(double d : test) {
			M.add(d);
		}
		M2 = Matrix.ones(100, 100);
	}

	@Test
	public void testOperateMatrixOfDouble() {
		EigenvectorMatrixOperation e = new EigenvectorMatrixOperation();
		e.operate(M);
		IMatrix<Double> values = e.getEigenvalues();
		IMatrix<Double> vectors = e.getEigenvectors();
		System.out.println("Eigenvalues: \n" + values + "\n" + "Eigenvectors: \n" + vectors);
	}
	
	@Test
	public void testOperateMatrixOfDouble2() {
		EigenvectorMatrixOperation e = new EigenvectorMatrixOperation();
		e.operate(M2);
		IMatrix<Double> values = e.getEigenvalues();
		IMatrix<Double> vectors = e.getEigenvectors();
		System.out.println("Eigenvalues: \n" + values + "\n" + "Eigenvectors: \n" + vectors);
	}


}
