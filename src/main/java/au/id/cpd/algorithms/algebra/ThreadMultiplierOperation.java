/**
 * 
 */
package au.id.cpd.algorithms.algebra;

import java.io.*;
import au.id.cpd.algorithms.algebra.DistanceMatrixOperation.Distance;
import au.id.cpd.algorithms.data.*;

/**
 * @author cd
 * 
 */
public class ThreadMultiplierOperation implements IMultiplierOperation {

	/**
	 * Input Matrix A
	 */
	private double[][] A;
	/**
	 * Input Matrix B
	 */
	private double[][] B;
	
	/**
	 * Output Matrix C
	 */
	private IMatrix<Double> C;

	/**
	 * Maximum number of threads.
	 */
	private static int MAX_THREADS = 50;

	/**
	 * The thread multiplier performs the calculation on the inner loops of the
	 * multiplication.
	 * 
	 * @author cd
	 * 
	 */
	private class ThreadMultiplier implements Runnable {
		private int start_i;
		private int start_k;
		private int start_j;
		private int max_aRows;
		private int max_bRows;
		private Thread t;

		public ThreadMultiplier(int i, int j, int k, int maxA, int maxB) {
			start_i = i;
			start_k = k;
			start_j = j;
			max_aRows = maxA;
			max_bRows = maxB;
		}

		/**
		 * Execute the inner loop.
		 */
		public synchronized void run() {
			
			for (int i = start_i; i < max_aRows; i++) {
				synchronized (C) {
					try {
						if (C == null) {
							throw new Exception("Matrix C is null.");
						}
						Number num = C.get(i, start_j);
						if (num == null) {
							C.set(i, start_j, 0.0);
							num = 0.0;
						}
						double v = num.doubleValue();
						// A row_i * B col_j
						for(int k=0;k<max_bRows;k++) {
							v = v + A[i][k]*B[k][start_j];
						}
						C.set(i, start_j, v);
					} catch(Exception e) {
						System.err.println("j: " + start_j + " i: " + i);
						e.printStackTrace();
					}
				}
			}

		}

		/**
		 * @return the start_i
		 */
		public synchronized int getStart_i() {
			return start_i;
		}

		/**
		 * @param start_i
		 *            the start_i to set
		 */
		public synchronized void setStart_i(int start_i) {
			this.start_i = start_i;
		}

		/**
		 * @return the start_k
		 */
		public synchronized int getStart_k() {
			return start_k;
		}

		/**
		 * @param start_k
		 *            the start_k to set
		 */
		public synchronized void setStart_k(int start_k) {
			this.start_k = start_k;
		}

		/**
		 * @return the start_j
		 */
		public synchronized int getStart_j() {
			return start_j;
		}

		/**
		 * @param start_j
		 *            the start_j to set
		 */
		public synchronized void setStart_j(int start_j) {
			this.start_j = start_j;
		}

	}

	public ThreadMultiplierOperation() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.algebra.IMultiplierOperation#operate(au.id.cpd.algorithms.data.IMatrix,
	 *      au.id.cpd.algorithms.data.IMatrix,
	 *      au.id.cpd.algorithms.algebra.DistanceMatrixOperation.Distance)
	 */
	public void operate(IMatrix<Double> A, IMatrix<Double> B, IMatrix<Double> C) {
		int aRows = A.getSize().getRows();
		int aCols = A.getSize().getCols();
		int bRows = B.getSize().getRows();
		int bCols = B.getSize().getCols();

		if (aCols != bRows) {
			System.err.println("A Cols != B Rows : " + A.getSize() + " " + B.getSize());
			return;
		}
		// pull A and B into memory to speed up the computation.
		this.A = A.convertToDoubles();
		this.B = B.convertToDoubles();
		this.C = C;
		try {

			ThreadGroup pool = new ThreadGroup(this.getClass().getName());
			for (int j = 0; j < bCols; j++) {
				int t;
				int start = j;
				for (t = 0; t < MAX_THREADS; t++) {
					if (start >= bCols)
						break;
					ThreadMultiplier multiplier = new ThreadMultiplier(0,
							start, 0, aRows, bRows);
					Thread runner = new Thread(pool, multiplier);
					runner.start();
					start++;
				}
				j = start;
				while (pool.activeCount() > 0) {
					Thread.sleep(100);
				}
			}
			// clean up the thread pool.
			pool.destroy();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.A = null;
		this.B = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 */
	public IMatrix<Double> operate(IMatrix<Double> A, IMatrix<Double> B) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.IMatrix)
	 */
	public IMatrix<Double> operate(IMatrix<Double> input) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(java.lang.Number)
	 */
	public Double operate(Number input) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the c
	 */
	public IMatrix<Double> getC() {
		return C;
	}

	/**
	 * @param c
	 *            the c to set
	 */
	public synchronized void setC(IMatrix<Double> c) {
		C = c;
	}

}
