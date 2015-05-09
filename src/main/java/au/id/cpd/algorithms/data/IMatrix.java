package au.id.cpd.algorithms.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public interface IMatrix<Number> extends Collection, Serializable {

	public Size getSize();

	/**
	 * Clone the current matrix instance.
	 */
	public IMatrix<Double> clone();

	/**
	 * Close operation.
	 */
	public void close();
	
	public void resize(Size s);

	/**
	 * Resize a matrix
	 * @param rows
	 * @param cols
	 */
	public void resize(int rows, int cols);

	/**
	 * Reshape the current matrix to the supplied dimensions
	 * the matrix must be dimensioned such that rows x cols are
	 * valid factorials of current rows x cols
	 * @param rows
	 * @param col
	 * @return
	 */
	public IMatrix<Double> reshape(int rows, int cols);

	/**
	 * Get the covariance matrix of the current matrix with itself
	 * will only work for square matrices
	 * @return
	 */
	public IMatrix<Number> covariance();

	/**
	 * Return the covariance matrix formed by this matrix and 
	 * supplied matrix b.
	 * @param b
	 * @return
	 */
	public IMatrix<Number> covariance(IMatrix<Number> b);

	/**
	 * Access the diagonal of a square matrix
	 * @return vector from diagonal of matrix
	 */
	public List<Number> diagonal();

	/**
	 * Transform all columns to rows and rows to columns
	 * @return Matrix matrix
	 */
	public IMatrix<Number> transform();

	/**
	 * Mij = for k sum AikBkj
	 * For this to work the matrix must contain
	 * numeric values that can be cast to double.
	 * @param matrix
	 * @return
	 */
	public IMatrix<Number> multiply(IMatrix<Number> matrix);
	
	/**
	 * Multiply dot product
	 * of row vector at row
	 * by column vector at matrix column.
	 * Row length must be equal to column length
	 * @param matrix
	 * @return
	 */
	public double ddot(int row, IMatrix<Number> matrix, int col);

	/**
	 * Multiply this matrix by the supplied value.
	 * @param m
	 * @return
	 */
	public IMatrix<Number> multiply(Double m);

	/**
	 * Perform pointwise multiplication on the supplied matrix.
	 * Both matrices must have the same size.
	 * @param matrix
	 * @return
	 */
	public IMatrix<Number> pointwiseMultiply(IMatrix<Number> matrix);

	/**
	 * Divide this matrix by the supplied value.
	 * @param m
	 * @return
	 */
	public IMatrix<Number> divide(Double m);

	/**
	 * Divide the supplied value by each value in this matrix.
	 * @param m
	 * @return
	 */
	public IMatrix<Number> divisorOf(Double m);

	/**
	 * Divide this matrix by the supplied value.
	 * @param m
	 * @return
	 */
	public IMatrix<Number> operate(IMatrixOperation op);

	/**
	 * Perform pointwise multiplication on the supplied matrix.
	 * Both matrices must have the same size.
	 * @param matrix
	 * @return
	 */
	public IMatrix<Number> pointwiseDivide(IMatrix<Number> matrix);

	/**
	 * Multiply the matrix by a row vector.
	 * @param list
	 * @return
	 */
	public List<Double> multiply(List<java.lang.Number> list);

	/**
	 * Divide the matrix by a row vector.
	 * @param list
	 * @return
	 */
	public List<Double> divide(List<java.lang.Number> list);

	/**
	 * Elementwise exp operation on this matrix.
	 * @param m
	 * @return
	 */
	public IMatrix<Number> exp();

	/**
	 * Elementwise tanh operation on this matrix.
	 * @param m
	 * @return
	 */
	public IMatrix<Number> tanh();

	/**
	 * Elementwise sin operation on this matrix.
	 * @param m
	 * @return
	 */
	public IMatrix<Number> sin();

	/**
	 * Elementwise cos operation on this matrix.
	 * @param m
	 * @return
	 */
	public IMatrix<Number> cos();

	/**
	 * Elementwise operation rasing elements to the supplied power.
	 * @param m
	 * @return
	 */
	public IMatrix<Number> power(Double p);

	/**
	 * Normalise the values in the current matrix
	 * by use of standard deviation.
	 * 
	 * This normalises over the columns rather then rows.
	 * 
	 * @return
	 */
	public IMatrix<Double> normalise();

	/**
	 * Normalise the values in each column
	 * by use of mean value of each column.
	 * 
	 * This normalises over the columns rather then rows.
	 * 
	 * Can be used to centre the data around
	 * the mean values of each column
	 * 
	 * @return
	 */
	public IMatrix<Double> meanNormalise();

	/**
	 * Normalise the values in the current matrix
	 * by use of standard deviation.
	 * @return
	 */
	public IMatrix<Double> minMaxNormalise();

	/**
	 * Return a single row containing the
	 * mean values of each column.
	 * @return
	 */
	public IMatrix<Double> rowsMeans();

	/**
	 * Return a single row containing the
	 * mean values of each column.
	 * @return
	 */
	public IMatrix<Double> columnMeans();

	/**
	 * Normalise the current matrix but exclude the
	 * supplied column from the normalisation.
	 * @param excludeColumn
	 * @return
	 */
	public IMatrix<Double> normalise(int exclude);

	/**
	 * Compute the euclidean Norm of this matrix.
	 * ||A|| = sqrt ( sum aij^2 )
	 * @return
	 */
	public double euclideanNorm();

	/**
	 * Find the maximum value of the matrix.
	 * @return
	 */
	public double max();

	/**
	 * Find the min value of the matrix.
	 * @return
	 */
	public double min();

	/**
	 * Duplicate this matrix by x vertically, y horizontally
	 * @param rows
	 * @param cols
	 * @return
	 */
	public IMatrix<Double> repmat(int x, int y);
	
	/**
	 * Shuffle the rows of the matrix so as to produce a matrix
	 * containing the same tuples at different row positions.
	 * Useful when attempting to automatically test such algorithms
	 * as classifiers.
	 * @return Matrix<Double>
	 */
	public IMatrix<Double> shuffle();

	/**
	 * Sum two matrixes.
	 * Element wise sum.
	 * (m+n)i,j = mi,j + ni,j
	 * @param matrix
	 * @return
	 */
	public IMatrix<Number> sum(Double b);

	/**
	 * Sum two matrixes.
	 * Element wise sum.
	 * (m+n)i,j = mi,j + ni,j
	 * @param matrix
	 * @return
	 */
	public IMatrix<Number> sum(IMatrix<Number> matrix);

	/**
	 * Subtract two matrixes.
	 * Scalar addition.
	 * @param double
	 * @return
	 */
	public IMatrix<Number> subtract(IMatrix<Number> matrix);

	/**
	 * Scalar subtraction.
	 * @param double
	 * @return
	 */
	public IMatrix<Number> subtract(Double b);

	/**
	 * Scalar subtraction where scalar value is on the left.
	 * @param double
	 * @return
	 */
	public IMatrix<Number> subtractFrom(Double b);

	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Object o);

	public boolean add(int row, int col, Object item);

	public void set(int row, int col, Object item);

	public java.lang.Number get(int row, int col);

	/**
	 * Access the row and return it as a list.
	 * @param row
	 * @return List<Number>
	 */
	public List<Number> getRow(int row);

	/**
	 * Assign the values of the supplied list to the row index.
	 * Row index must be within range.
	 * @param row
	 * @param rowValues
	 */
	public void setRow(int row, List<Number> rowValues);

	/**
	 * Sum the row.
	 * @param row
	 * @return
	 */
	public Double sumRow(int row);

	/**
	 * Access the column and return it as a list.
	 * @param col
	 * @return List<Number>
	 */
	public List<Number> getColumn(int col);

	/**
	 * Assign the values of the supplied list to the columns index.
	 * Column index must be within range.
	 * @param col
	 * @param colValues
	 */
	public void setColumn(int col, List<Number> colValues);

	/**
	 * Sum the column.
	 * @param col
	 * @return
	 */
	public Double sumColumn(int col);

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection c);

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	public void clear();

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object o);

	/**
	 * Determine if the row contains the value for number.
	 * @param row
	 * @param o
	 * @return
	 */
	public boolean rowContains(int row, Number o);

	/**
	 * Return a List of all column indices containing the supplied value
	 * @param row
	 * @param o
	 * @return List of column indices or empty list otherwise.
	 */
	public List<Integer> findColumns(int row, Number o);

	/**
	 * Determine if the column contains the supplied number.
	 * @param col
	 * @param o
	 * @return
	 */
	public boolean columnContains(int col, Number o);

	/**
	 * Find the indices of rows containing the supplied value.
	 * @param col
	 * @param o
	 * @return List of row indices or empty list otherwise.
	 */
	public List<Integer> findRows(int col, Number o);

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection c);

	public boolean rowContainsAll(int row, Collection c);

	public boolean columnContainsAll(int col, Collection c);

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty();

	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	public Iterator iterator();

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object o);

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection c);

	public boolean removeRow(int row);

	/**
	 * Remove the column at the supplied index.
	 * @param col
	 * @return
	 */
	public boolean removeColumn(int col);

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection c);

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	public int size();

	/**
	 * Get the minimum value in this matrix
	 * @return
	 */
	public double getMin();

	/**
	 * Get the maximum value in this matrix
	 * @param m
	 * @return
	 */
	public double getMax();

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray();

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	public Object[] toArray(Object[] a);

	/**
	 * Convert the matrix to nested doubles array.
	 * @return
	 */
	public double[][] convertToDoubles();

	/**
	 * Convert to string.
	 */
	public String toString();

	/**
	 * Determine if current matrix equals supplied matrix.
	 * @param m
	 * @return true if equal false otherwise
	 */
	public boolean equals(IMatrix<Double> m);

	/**
	 * Save the matrix to the supplied file.
	 * @param file
	 * @return
	 */
	public boolean save(String file);
}