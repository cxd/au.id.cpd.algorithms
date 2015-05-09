/**
 * 
 */
package au.id.cpd.algorithms.data;

import java.util.*;
import java.io.*;
import java.lang.*;
import au.id.cpd.algorithms.data.io.*;
/**
 * @author Chris Davey cd at cpd.id.au
 * @version 0.1
 * @todo Complete methods that have not yet been implemented.
 * 
 */
public class Matrix< Number > extends AbstractMatrix<Number> {
	
	/**
	 * serial version id.
	 */
	static final long serialVersionUID = -1662213726242529761L;
	
	/**
	 * @serial object array of values.
	 */
	private Object[][] values;
	
	public Matrix() {
		setSize(new Size());
		this.values = new Object[1][1];
	}
	public Matrix(int rows, int cols) {
		setSize(new Size(rows, cols));
		this.values = new Object[rows][cols];
	}
	
	public Matrix(Size s) {
		setSize(s);
		this.values = new Object[s.getRows()][s.getCols()];
	}
	
	
		
	private void fillValues() {
		if (this.values.length == 0) return;
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				this.values[i][j] = null;
			}
		}
	}
	
	private void fillValues(int rowOffset, int colOffset) {
		if (this.values.length == 0) return;
		for(int i=rowOffset;i<this.getSize().getRows();i++) {
			for(int j=colOffset;j<this.getSize().getCols();j++) {
				this.values[i][j] = null;
			}
		}
	}
	
	/**
	 * Fill the matrix of dimension rows x cols with supplied value.
	 * @param val
	 */
	private void fillValues(double val) {
		if (this.values.length == 0) return;
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				this.values[i][j] = val;
			}
		}
	}
	
	private void copy(Object[][] source, Object[][] target) {
		for(int i=0;i<source.length;i++) {
			for(int j=0;j<source[i].length;j++) {
				target[i][j] = source[i][j];
			}
		}
	}
	
	/**
	 * Multiply dot product
	 * of row vector at row
	 * by column vector at matrix column.
	 * Row length must be equal to column length
	 * @param matrix
	 * @return
	 */
	public double ddot(int row, IMatrix<Number> matrix, int col) {
		int m = getSize().getRows();
		int n = getSize().getCols();

		Object[] rowData = values[row];
			
		double result = 0.0;
			
		for(int k=0;k<rowData.length;k++) {
			result = result + ((Double)rowData[k])*matrix.get(k,col).doubleValue();
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#clone()
	 */
	public Matrix<Double> clone() {
		Matrix<Double> child = new Matrix<Double>(this.getSize());
		child.copy(this.values, child.values);
		return child;
	}
	
	public IMatrix<Double> repmat(int x, int y) {
		int rows = getSize().getRows();
		int cols = getSize().getCols();
		int newRows = rows*x;
		int newCols = cols*y;
		IMatrix<Double> m =  new Matrix<Double>(newRows, newCols);
		return repmat(x, y, m);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#regetSize()(au.id.cpd.algorithms.data.Size)
	 */
	public void resize(Size s) {
		if (this.getSize().compareTo(s) > 0) 
			this.reduce(s);
		else if (this.getSize().compareTo(s) < 0)
			this.increase(s);
		this.setSize(s);
	}
	
	private void reduce(Size s) {
		if (this.values.length == 0) return;
		Object[][] tmp = new Object[s.getRows()][s.getCols()];
		for(int i=0;i<tmp.length;i++) {
			for(int j=0;j<tmp[i].length;j++) {
				tmp[i][j] = this.values[i][j];
			}
		}
		this.values = tmp;
	}
	
	private void increase(Size s) {
		if (this.values.length == 0) return;
		Object[][] tmp = new Object[s.getRows()][s.getCols()];
		for(int i=0;i<this.values.length;i++) {
			for(int j=0;j<this.values[i].length;j++) {
				tmp[i][j] = this.values[i][j];
			}
		}
		this.values = tmp;
	}
	
	/**
	 * Return a matrix of ones of dimension rows x cols
	 * @param rows
	 * @param cols
	 * @return
	 */
	public static IMatrix<Double> ones(int rows, int cols) {
		Matrix<Double> m = new Matrix<Double>(rows, cols);
		m.fillValues(1.0);
		return m;
	}
	
	/**
	 * Return a matrix of zeroes of dimension rows x cols
	 * @param rows
	 * @param cols
	 * @return
	 */
	public static IMatrix<Double> zeroes(int rows, int cols) {
		Matrix<Double> m = new Matrix<Double>(rows, cols);
		m.fillValues(0.0);
		return m;
	}

	/**
	 * Construct a matrix with 1.0 in the diagonal.
	 * rows should be the same as cols.
	 * @param rows
	 * @param cols
	 * @return
	 */
	public static IMatrix<Double> identity(int rows, int cols) {
		Matrix<Double> m = new Matrix<Double>(rows, cols);
		m.fillValues(0.0);
		for(int i=0;i<rows;i++) {
			if (i < cols) m.set(i, i, 1.0);
		}
		return m;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#reshape(int, int)
	 */
	public IMatrix<Double> reshape(int rows, int cols) {
		IMatrix<Double> m = new Matrix<Double>(rows, cols);
		return reshape(rows, cols, m);
	}
	
	
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#transform()
	 */
	public IMatrix<Number> transform() {
		IMatrix<Number> matrix = new Matrix<Number>(this.getSize().getCols(), this.getSize().getRows());
		for(int i=0;i<this.values.length;i++) {
			for(int j=0;j<this.values[i].length;j++) {
				matrix.add(j, i, this.values[i][j]);
			}
		}
		return matrix;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#multiply(au.id.cpd.algorithms.data.IMatrix)
	 */
	public IMatrix<Number> multiply(IMatrix<Number> matrix) {
		IMatrix<Number> result = new Matrix<Number>(this.getSize().getRows(), matrix.getSize().getCols());
		return multiply(matrix, result);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#multiply(java.lang.Double)
	 */
	public IMatrix<Number> multiply(Double m) {
		IMatrix<Number> result = new Matrix<Number>(this.getSize().getRows(), this.getSize().getCols());
		return multiply(m, result);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#pointwiseMultiply(au.id.cpd.algorithms.data.IMatrix)
	 */
	public IMatrix<Number> pointwiseMultiply(IMatrix<Number> matrix) {
		IMatrix<Number> result = new Matrix<Number>(this.getSize().getRows(), this.getSize().getCols());
		return pointwiseMultiply(matrix, result);
	}
	
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#divide(java.lang.Double)
	 */
	public IMatrix<Number> divide(Double m) {
		IMatrix<Number> result = new Matrix<Number>(this.getSize().getRows(), this.getSize().getCols());
		return divide(m, result);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#divisorOf(java.lang.Double)
	 */
	public IMatrix<Number> divisorOf(Double m) {
		IMatrix<Number> result = new Matrix<Number>(this.getSize().getRows(), this.getSize().getCols());
		return divisorOf(m, result);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#operate(au.id.cpd.algorithms.data.IMatrixOperation)
	 */
	public IMatrix<Number> operate(IMatrixOperation op) {
		IMatrix<Number> result = new Matrix<Number>(this.getSize().getRows(), this.getSize().getCols());
		return operate(op, result);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#pointwiseDivide(au.id.cpd.algorithms.data.IMatrix)
	 */
	public IMatrix<Number> pointwiseDivide(IMatrix<Number> matrix) {
		IMatrix<Number> result = new Matrix<Number>(this.getSize().getRows(), this.getSize().getCols());
		return pointwiseDivide(matrix, result);
	}
	
	
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#exp()
	 */
	public IMatrix<Number> exp() {
		IMatrix<Number> result = new Matrix<Number>(this.getSize().getRows(), this.getSize().getCols());
		return exp(result);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#tanh()
	 */
	public IMatrix<Number> tanh() {
		IMatrix<Number> result = new Matrix<Number>(this.getSize().getRows(), this.getSize().getCols());
		return tanh(result);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#sin()
	 */
	public IMatrix<Number> sin() {
		IMatrix<Number> result = new Matrix<Number>(this.getSize().getRows(), this.getSize().getCols());
		return sin(result);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#cos()
	 */
	public IMatrix<Number> cos() {
		IMatrix<Number> result = new Matrix<Number>(this.getSize().getRows(), this.getSize().getCols());
		return cos(result);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#power(java.lang.Double)
	 */
	public IMatrix<Number> power(Double p) {
		IMatrix<Number> result = new Matrix<Number>(this.getSize().getRows(), this.getSize().getCols());
		return power(p, result);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#normalise()
	 */
	public IMatrix<Double> normalise() {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		// normalise column by column.
		IMatrix<Double> normal = new Matrix<Double>(rows, cols);
		return normalise(normal);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#meanNormalise()
	 */
	public IMatrix<Double> meanNormalise() {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		// normalise column by column.
		IMatrix<Double> normal = new Matrix<Double>(rows, cols);
		return meanNormalise(normal);
	}
	
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#minMaxNormalise()
	 */
	public IMatrix<Double> minMaxNormalise() {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		// normalise column by column.
		IMatrix<Double> normal = new Matrix<Double>(rows, cols);
		return minMaxNormalise(normal);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#rowsMeans()
	 */
	public IMatrix<Double> rowsMeans() {
		int rows = this.getSize().getRows();
		IMatrix<Double> means = new Matrix<Double>(rows, 1);
		return rowsMeans(means);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#columnMeans()
	 */
	public IMatrix<Double> columnMeans() {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		// normalise column by column.
		IMatrix<Double> means = new Matrix<Double>(1, cols);
		return columnMeans(means);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#normalise(int)
	 */
	public IMatrix<Double> normalise(int exclude) {
		IMatrix<Double> norm = this.normalise();
		for(int i=0;i<norm.getSize().getRows();i++) {
			norm.set(i, exclude, this.values[i][exclude]);
		}
		return norm;
	}
	
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#shuffle()
	 */
	public IMatrix<Double> shuffle() {
		IMatrix<Double> shuffled = new Matrix<Double>(this.getSize().getRows(), this.getSize().getCols());
		return shuffle(shuffled);
	}
	
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#sum(java.lang.Double)
	 */
	public IMatrix<Number> sum(Double b) {
		IMatrix<Number> result = new Matrix<Number>(this.getSize());
		return sum(b, result);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#sum(au.id.cpd.algorithms.data.IMatrix)
	 */
	public IMatrix<Number> sum(IMatrix<Number> matrix) {
		IMatrix<Number> result = new Matrix<Number>(this.getSize());
		return sum(matrix, result);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#subtract(au.id.cpd.algorithms.data.IMatrix)
	 */
	public IMatrix<Number> subtract(IMatrix<Number> matrix) {
		IMatrix<Number> result = new Matrix<Number>(this.getSize());
		return subtract(matrix, result);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#subtract(java.lang.Double)
	 */
	public IMatrix<Number> subtract(Double b) {
		IMatrix<Number> result = new Matrix<Number>(this.getSize());
		return subtract(b, result);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#subtractFrom(java.lang.Double)
	 */
	public IMatrix<Number> subtractFrom(Double b) {
		IMatrix<Number> result = new Matrix<Number>(this.getSize());
		return subtractFrom(b, result);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#add(java.lang.Object)
	 */
	public boolean add(Object o) {
		boolean flag = false;
		// unfortunately this is O(n^(i*j))
		for(int i=0;i<this.values.length;i++) {
			for(int j=0;j<this.values[i].length;j++) {
				if (this.values[i][j] == null) {
					this.values[i][j] = o;
					flag = true;
					break;
				}
			}
			if (flag) break;
		}
		if (flag) return true;
		this.resize(new Size(this.getSize().getRows()+1, this.getSize().getCols()+1));
		return this.add(this.getSize().getRows(), 0, o);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#add(int, int, java.lang.Object)
	 */
	public boolean add(int row, int col, Object item) {
		if ((this.values.length < row)&&(this.values[0].length < col)) {
			this.resize(new Size(this.getSize().getRows()+row-this.getSize().getRows(), this.getSize().getCols()+col-this.getSize().getCols()));
			return this.add(row, col, item);
		} else if (this.values.length < row) {
			this.resize(new Size(this.getSize().getRows()+row-this.getSize().getRows(), this.getSize().getCols()));
			return this.add(row, col, item);
		} else if (this.values[0].length < col) {
			this.resize(new Size(this.getSize().getRows(), this.getSize().getCols()+col-this.getSize().getCols()));
			return this.add(row, col, item);
		}
		this.values[row][col] = item;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#set(int, int, java.lang.Object)
	 */
	public void set(int row, int col, Object item) {
		if (this.values.length == 0) return;
		if ((this.values.length < row)||(this.values[0].length < col))
			return;
		this.values[row][col] = item;
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#get(int, int)
	 */
	public java.lang.Number get(int row, int col) {
		if (this.values.length == 0) return null;
		if ((this.values.length < row)||(this.values[0].length < col))
			return null;
		return (java.lang.Number)this.values[row][col];
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#getRow(int)
	 */
	public List<Number> getRow(int row) {
		List<Number> rowList = new Vector<Number>();
		if (row >= this.values.length) return null;
		for(int j=0;j<this.values[row].length;j++) {
			rowList.add((Number)this.values[row][j]);
		}
		return rowList;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#setRow(int, java.util.List)
	 */
	public void setRow(int row, List<Number> rowValues) {
		if ( (row < 0) || 
			(row > this.getSize().getRows()) || 
			(rowValues.size() != this.getSize().getCols()) ) 
			return;
		for(int j=0;j<this.getSize().getCols();j++) {
			this.set(row, j, rowValues.get(j));
		}
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#sumRow(int)
	 */
	public Double sumRow(int row) {
		Double n = 0.0;
		if (row >= this.values.length) return n;
		for(int j=0;j<this.values[row].length;j++) {
			n += (Double)this.values[row][j];
		}
		return n;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#getColumn(int)
	 */
	public List<Number> getColumn(int col) {
		if (this.values.length == 0) return null;
		if (col >= this.values[0].length) return null;
		List<Number> colList = new Vector<Number>();
		for(int i=0;i<this.values.length;i++) {
			colList.add((Number)this.values[i][col]);
		}
		return colList;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#setColumn(int, java.util.List)
	 */
	public void setColumn(int col, List<Number> colValues) {
		if ( (col < 0) || 
			(col > this.getSize().getCols()) || 
			(colValues.size() != this.getSize().getRows()) ) 
			return;
		for(int i=0;i<this.getSize().getRows();i++) {
			this.set(i, col, colValues.get(i));
		}
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#sumColumn(int)
	 */
	public Double sumColumn(int col) {
		Double n = 0.0;
		if (this.values.length == 0) return n;
		if (col > this.values[0].length) return n;
		for(int i=0;i<this.values.length;i++) {
			n += (Double)this.values[i][col];
		}
		return n;
	}

	
	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#clear()
	 */
	public void clear() {
		this.resize(1,1);
		this.values[0][0] = null;
	}


	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#rowContains(int, Number)
	 */
	public boolean rowContains(int row, Number o) {
		if (row > this.values.length) return false;
		for(int j=0;j<this.values[row].length;j++) {
			if (this.values[row][j].equals(o)) return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#findColumns(int, Number)
	 */
	@Override
	public List<Integer> findColumns(int row, Number o) {
		List<Integer> columns = new Vector<Integer>();
		if (row >= this.values.length) return columns;
		for(int j=0;j<this.values[row].length;j++) {
			if (this.values[row][j].equals(o)) {
				columns.add(j);
			}
		}
		return columns;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#columnContains(int, Number)
	 */
	public boolean columnContains(int col, Number o) {
		if (this.values.length == 0) return false;
		if (col >= this.values[0].length) return false;
		for(int i=0;i<this.values.length;i++) {
			if (this.values[i][col].equals(o)) return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#findRows(int, Number)
	 */
	public List<Integer> findRows(int col, Number o) {
		List<Integer> rows = new Vector<Integer>();
		if (this.values.length == 0) return rows;
		if (col >= this.values[0].length) return rows;
		for(int i=0;i<this.values.length;i++) {
			if (this.values[i][col].equals(o)) {
				rows.add(i);
			}
		}
		return rows;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection c) {
		Object[] arr = this.toArray();
		Arrays.sort(arr);
		for(Object o : c) {
			if (Arrays.binarySearch(arr, o) < 0) return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#rowContainsAll(int, java.util.Collection)
	 */
	public boolean rowContainsAll(int row, Collection c) {
		if (row >= this.values.length) return false;
		Object[] arr = this.values[row];
		Arrays.sort(arr);
		for(Object o : c) {
			if (Arrays.binarySearch(arr, o) < 0) return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#columnContainsAll(int, java.util.Collection)
	 */
	public boolean columnContainsAll(int col, Collection c) {
		if (this.values.length == 0) return false;
		if (col >= this.values[0].length) return false;
		Object[] arr = new Object[this.values.length];
		for(int i=0;i<this.values.length;i++) {
			arr[i] = this.values[i][col];
		}
		Arrays.sort(arr);
		for(Object o : c) {
			if (Arrays.binarySearch(arr, o) < 0) return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#isEmpty()
	 */
	public boolean isEmpty() {
		if (this.values.length == 0) return true;
		for(Object o : this) {
			if (o != null) return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#removeRow(int)
	 */
	@Override
	public boolean removeRow(int row) {
		if (row > this.values.length) return false;
		Object[][] tmp = new Object[this.getSize().getRows()-1][this.getSize().getCols()];
		int cnt = 0;
		for(int i=0;i<this.values.length;i++) {
			if (i == row) continue;
			for(int j=0;j<this.values[i].length;j++) {
				tmp[cnt][j] = this.values[i][j];
			}
			cnt++;
		}
		this.values = tmp;
		this.setSize(new Size(this.getSize().getRows()-1, this.getSize().getCols()));
		return true;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#removeColumn(int)
	 */
	public boolean removeColumn(int col) {
		if (this.values.length == 0) return false;
		if (col > this.values[0].length) return false;
		Object[][] tmp = new Object[this.getSize().getRows()][this.getSize().getCols()-1];
		for(int i=0;i<this.values.length;i++) {
			int cnt = 0;
			for(int j=0;j<this.values[i].length;j++) {
				if (j==col) continue;
				tmp[i][cnt] = this.values[i][j];
				cnt++;
			}
		}
		this.values = tmp;
		this.setSize(new Size(this.getSize().getRows(), this.getSize().getCols()-1));
		return true;
	}
	
	
	

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#toArray()
	 */
	public Object[] toArray() {
		Object[] arr = new Object[this.size()];
		int cnt = 0;
		for(int i=0;i<this.values.length;i++) {
			for(int j=0;j<this.values[i].length;j++) {
				arr[cnt] = this.values[i][j];
				cnt++;
			}
		}
		return arr;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#toArray(java.lang.Object[])
	 */
	public Object[] toArray(Object[] a) {
		int cnt = 0;
		boolean flag = false;
		for(int i=0;i<this.values.length;i++) {
			for(int j=0;j<this.values.length;j++) {
				if (cnt < a.length) {
					a[cnt] = this.values[i][j];
					cnt++;
				} else {
					flag = true;
					break;
				}
			}
			if (flag) break;
			if (cnt < a.length) {
				for(i=cnt;i<a.length;i++)
					a[i]=null;
			}
		}
		return a;
	}
	
	
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#toString()
	 */
	public String toString() {
		String str = "";
		for(int i=0;i<this.values.length;i++) {
			String row = "";
			for(int j=0;j<this.values[i].length;j++) {
				row += this.values[i][j];
				if (j<this.values[i].length-1)
					row+=",";
			}
			row += System.getProperty("line.separator");
			str += row;
		}
		return str;
	}
	
	/**
	 * Save the matrix to the supplied file.
	 * @param file
	 * @return
	 */
	public boolean save(String file) {
		try {
			java.io.FileWriter fout = new java.io.FileWriter(file);
			MatrixWriter writer = new MatrixWriter(fout);
			writer.writeMatrix((IMatrix<Double>)this, fout);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * java.io.Serializable.readObject(ObjectInputStream is)
	 */
	private void readObject(java.io.ObjectInputStream is) throws ClassNotFoundException, java.io.IOException {
		is.defaultReadObject();
	}
	/**
	 * java.io.Serializable.writeObject(ObjectOutputStream os)
	 */
	private void writeObject(java.io.ObjectOutputStream os) throws ClassNotFoundException, java.io.IOException {
		os.defaultWriteObject();
	}

}
