/**
 * 
 */
package au.id.cpd.algorithms.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;


/**
 * @author cd
 *
 */
public abstract class AbstractMatrix<Number> implements au.id.cpd.algorithms.data.IMatrix<Number>, Serializable, Collection {

	/**
	 * @serial size of matrix
	 */
	private Size size;
	
	/**
	 * Clone the current matrix instance.
	 */
	public abstract IMatrix<Double> clone();
	
	/**
	 * Close any open resources - not implemented.
	 */
	public void close() {}
	
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#getSize()
	 */
	public Size getSize() {
		return this.size;
	}
	
	protected void setSize(Size sz) {
		size = sz;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#addAll(java.util.Collection)
	 */
	public  boolean addAll(Collection c) {
		for(Object o : c) {
			this.add(o);
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#columnMeans()
	 */
	public  IMatrix<Double> columnMeans(IMatrix<Double> means) {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		// calculate the mean and standard deviation for each column
		for(int k=0;k<cols;k++) {
			double sum = 0.0;
			double max = -1*Double.MIN_VALUE;
			double min = Double.MAX_VALUE;
			for(int j=0;j<rows;j++) {
				double val = (this.get(j,k) != null) ? this.get(j,k).doubleValue() : 0;
				if (val < min) {
					min = val;
				}
				if (val > max) {
					max = val;
				}
				sum += val;
			}
			if (sum != 0) {
				means.set(0, k, sum/rows);
			} else {
				means.set(0, k, 0.0);
			}
		}
		return means;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#contains(java.lang.Object)
	 */
	public  boolean contains(Object o) {
		for(Object obj : this) {
			if (obj.equals(o)) return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#cos()
	 */
	public  IMatrix<Number> cos(IMatrix<Number> result) {
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				result.set(i, j, Math.cos(this.get(i,j).doubleValue()));
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#covariance()
	 */
	public  IMatrix<Number> covariance() {
		IMatrix<Number> t = this.transform();
		return this.multiply(t);
	}
	
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#covariance(au.id.cpd.algorithms.data.IMatrix)
	 */
	public  IMatrix<Number> covariance(IMatrix<Number> b) {
		return this.multiply(b);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#diagonal()
	 */
	public  List<Number> diagonal() {
		if (this.getSize().getRows() != this.getSize().getCols()) return null;
		List<Number> v = new ArrayList<Number>();
		for(int i=0;i<this.getSize().getRows();i++) {
			v.add((Number)this.get(i,i));
		}
		return v;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#divide(java.lang.Double)
	 */
	public  IMatrix<Number> divide(Double m, IMatrix<Number> result) {
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				double v = this.get(i,j).doubleValue();
				if ( ( this.get(i,j).doubleValue() != 0 ) && ( m != 0) ) {
					v = this.get(i,j).doubleValue()/m;
				}
				result.set(i, j, v);
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#divisorOf(java.lang.Double)
	 */
	public  IMatrix<Number> divisorOf(Double m, IMatrix<Number> result) {
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				result.set(i, j, m/this.get(i,j).doubleValue());
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#exp()
	 */
	public  IMatrix<Number> exp(IMatrix<Number> result) {
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				result.set(i, j, Math.exp(this.get(i,j).doubleValue()));
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#convertToDoubles()
	 */
	public  double[][] convertToDoubles() {
		double[][] result = new double[this.getSize().getRows()][this.getSize().getCols()];
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				result[i][j] = this.get(i, j).doubleValue();
			}
		}
		return result;
	}
	

	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#divide(java.util.List)
	 */
	public  List<Double> divide(List<java.lang.Number> list) {
		if (list.size() != this.getSize().getCols()) return null;
		List<Double> result = new Vector<Double>();
		Double[] nums = new Double[this.getSize().getRows()];
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				for(int k=0;k<list.size();k++) {
					// again some trouble with type conversion.
					double a = list.get(k).doubleValue(); // Double.parseDouble(""+list.get(k));
					double b = this.get(i, j).doubleValue();
					double v = 0.0;
					if ( (a != 0) && (b != 0)) {
						v = a/b;
					} 
					if (nums[k] != null) 
						nums[k] += v;
					else
						nums[k] = v;
				}
			}
		}
		for(int k=0;k<list.size();k++) {
			result.add(nums[k]);
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#euclideanNorm()
	 */
	public  double euclideanNorm() {
		double norm = 0.0;
		for(int i=0;i<this.getSize().getRows(); i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				norm += this.get(i, j).doubleValue() * this.get(i, j).doubleValue();
			}
		}
		norm = Math.sqrt(norm);
		return norm;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#equals(au.id.cpd.algorithms.data.IMatrix)
	 */
	public  boolean equals(IMatrix<Double> m) {
		if (!this.getSize().equals(m.getSize())) return false;
		for(int i=0;i<m.getSize().getRows();i++) {
			for(int j=0;j<m.getSize().getCols();j++) {
				if (this.get(i, j).doubleValue() != m.get(i, j).doubleValue()) return false;
			}
		}
		return true;
	}
	
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#findColumns(int, java.lang.Object)
	 */
	public  List<Integer> findColumns(int row, Number o) {
		List<Integer> columns = new Vector<Integer>();
		if (row >= this.getSize().getRows()) return columns;
		for(int j=0;j<this.getSize().getCols();j++) {
			if (this.get(row, j).equals(o)) {
				columns.add(j);
			}
		}
		return columns;
	}

	

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#findRows(int, Number)
	 */
	public  List<Integer> findRows(int col, Number o) {
		List<Integer> rows = new Vector<Integer>();
		if (this.getSize().getRows() == 0) return rows;
		if (col >= this.getSize().getCols()) return rows;
		for(int i=0;i<this.getSize().getRows();i++) {
			if (this.get(i, col).equals(o)) {
				rows.add(i);
			}
		}
		return rows;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#getMin()
	 */
	public  double getMin() {
		double min = Double.MAX_VALUE;
		Size sz = this.getSize();
		for(int i=0;i<sz.getRows();i++) {
			for(int j=0;j<sz.getCols();j++) {
				double z = (Double)this.get(i, j);
				if (z < min)
					min = z;
			}
		}
		return min;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#getMax()
	 */
	public  double getMax() {
		double max = Double.MIN_VALUE;
		Size sz = this.getSize();
		for(int i=0;i<sz.getRows();i++) {
			for(int j=0;j<sz.getCols();j++) {
				double z = (Double)this.get(i, j);
				if (z > max)
					max = z;
			}
		}
		return max;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#iterator()
	 */
	public  Iterator iterator() {
		return new MatrixIterator(this);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#max()
	 */
	public  double max() {
		double max = Double.MIN_VALUE;
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				if (this.get(i,j).doubleValue() > max) {
					max = this.get(i, j).doubleValue();
				}
			}
		}
		return max;
	}


	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#meanNormalise()
	 */
	public  IMatrix<Double> meanNormalise(IMatrix<Double> normal) {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		if (rows == 1) return clone();
		// normalise column by column.
		List<Double> means = new Vector<Double>();
		// calculate the mean and standard deviation for each column
		for(int k=0;k<cols;k++) {
			double sum = 0.0;
			double max = -1*Double.MIN_VALUE;
			double min = Double.MAX_VALUE;
			for(int j=0;j<rows;j++) {
				double val = (this.get(j,k) != null) ? this.get(j,k).doubleValue() : 0;
				if (val < min) {
					min = val;
				}
				if (val > max) {
					max = val;
				}
				sum += val;
			}
			if (sum != 0) {
				means.add(sum/rows);
			} else {
				means.add(0.0);
			}
		}
		for(int j=0;j<rows;j++) {
			for(int k=0;k<cols;k++) {
				double val = (this.get(j, k) != null) ? this.get(j, k).doubleValue() : 0;
				normal.set(j, k, val-means.get(k));
			}
		}
		return normal;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#min()
	 */
	public  double min() {
		double min = Double.MAX_VALUE;
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				if (this.get(i,j).doubleValue() < min) {
					min = this.get(i, j).doubleValue();
				}
			}
		}
		return min;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#minMaxNormalise()
	 */
	public  IMatrix<Double> minMaxNormalise(IMatrix<Double> normal) {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		// normalise column by column.
		double max = this.max();
		double min = this.min();
		double delta = max - min;
		for(int j=0;j<rows;j++) {
			for(int k=0;k<cols;k++) {
				double val = (this.get(j, k) != null) ? this.get(j, k).doubleValue() : 0;
				if ( (val != 0.0) && (delta != 0.0) ) {
					normal.set(j, k, (val - min) / delta);
				} else {
					normal.set(j, k, val);
				}
			}
		}
		return normal;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#multiply(au.id.cpd.algorithms.data.IMatrix)
	 */
	public  IMatrix<Number> multiply(IMatrix<Number> matrix, IMatrix<Number> result) {
		// unique case where 1x1 matrix - instead of scalar.
		if ( (this.getSize().getCols() == 1) && (this.getSize().getRows() == 1) )
			return matrix.multiply(this.get(0, 0).doubleValue());
		else if ( (matrix.getSize().getCols() == 1) && (matrix.getSize().getRows() == 1) )
			return this.multiply(matrix.get(0, 0).doubleValue());
		// general case
		int rows = matrix.getSize().getRows();
		int cols = this.getSize().getCols();
		if (rows != cols) return null;
		
		Double d = new Double(0);
		for(int j=0;j<matrix.getSize().getCols();j++) {
			for(int i=0;i<this.getSize().getRows();i++) {
				for(int k=0;k<matrix.getSize().getRows();k++) {
					// Acols == Brows
					// sum A(i,k)*B(k,j)
					d += this.get(i, k).doubleValue() * matrix.get(k, j).doubleValue();
				}
				result.set(i,j,d);
				d = new Double(0);
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#multiply(java.lang.Double)
	 */
	public  IMatrix<Number> multiply(Double m, IMatrix<Number> result) {
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				result.set(i, j, this.get(i,j).doubleValue()*m);
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#multiply(java.util.List)
	 */
	public  List<Double> multiply(List<java.lang.Number> list) {
		if (list.size() != this.getSize().getCols()) return null;
		List<Double> result = new Vector<Double>();
		Double[] nums = new Double[this.getSize().getRows()];
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				for(int k=0;k<list.size();k++) {
					// again some trouble with type conversion.
					double a = list.get(k).doubleValue(); // Double.parseDouble(""+list.get(k));
					double b = this.get(i, j).doubleValue();
					if (nums[k] != null) 
						nums[k] += a*b;
					else
						nums[k] = a*b;
				}
			}
		}
		for(int k=0;k<list.size();k++) {
			result.add(nums[k]);
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#normalise()
	 */
	public  IMatrix<Double> normalise(IMatrix<Double> normal) {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		if (rows == 1) return clone();
		// normalise column by column.
		List<Double> means = new Vector<Double>();
		List<Double> std = new Vector<Double>();
		// calculate the mean and standard deviation for each column
		for(int k=0;k<cols;k++) {
				double sum = 0.0;
				double max = Double.MIN_VALUE;
				double min = Double.MAX_VALUE;
				for(int j=0;j<rows;j++) {
					double val = (this.get(j,k) != null) ? this.get(j,k).doubleValue() : 0;
					if (val < min) {
						min = val;
					}
					if (val > max) {
						max = val;
					}
					sum += val;
				}
				if (sum != 0) {
					means.add(sum/rows);
				} else {
					means.add(0.0);
				}
				std.add(max - min);
			}
		for(int j=0;j<rows;j++) {
			for(int k=0;k<cols;k++) {
				double val = (this.get(j, k) != null) ? this.get(j, k).doubleValue() : 0;
				if (std.get(k) != 0) {
					normal.set(j, k, (val-means.get(k))/std.get(k));
				} else {
					normal.set(j, k, val-means.get(k));
				} 
			}
		}
		return normal;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#normalise(int)
	 */
	public  IMatrix<Double> normalise(int exclude) {
		IMatrix<Double> norm = this.normalise();
		for(int i=0;i<norm.getSize().getRows();i++) {
			norm.set(i, exclude, this.get(i,exclude));
		}
		return norm;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#operate(au.id.cpd.algorithms.data.IMatrixOperation)
	 */
	public  IMatrix<Number> operate(IMatrixOperation op, IMatrix<Number> result) {
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				result.set(i, j, op.operate(this.get(i,j).doubleValue()));
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#pointwiseDivide(au.id.cpd.algorithms.data.IMatrix)
	 */
	public  IMatrix<Number> pointwiseDivide(IMatrix<Number> matrix, IMatrix<Number> result) {
		if (this.getSize().compareTo(matrix.getSize()) != 0) return null;
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				result.set(i, j, this.get(i,j).doubleValue()/matrix.get(i, j).doubleValue());
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#pointwiseMultiply(au.id.cpd.algorithms.data.IMatrix)
	 */
	public  IMatrix<Number> pointwiseMultiply(IMatrix<Number> matrix, IMatrix<Number> result) {
		if (this.getSize().compareTo(matrix.getSize()) != 0) return null;
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				result.set(i, j, this.get(i,j).doubleValue()*matrix.get(i, j).doubleValue());
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#power(java.lang.Double)
	 */
	public  IMatrix<Number> power(Double p, IMatrix<Number> result) {
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				result.set(i, j, Math.pow(this.get(i,j).doubleValue(), p));
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#regetSize()(int, int)
	 */
	public  void resize(int rows, int cols) {
		this.resize(new Size(rows, cols));
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#remove(java.lang.Object)
	 */
	public  boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
	


	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#removeAll(java.util.Collection)
	 */
	public  boolean removeAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#removeColumn(int)
	 */
	public  boolean removeColumn(int col) {
		if (this.getSize().getRows() == 0) return false;
		if (col > this.getSize().getCols()) return false;
		for(int i=0;i<this.getSize().getRows();i++) {
			int cnt = 0;
			for(int j=0;j<this.getSize().getCols();j++) {
				if (j==col) continue;
				this.set(i, cnt, this.get(i, j));
				cnt++;
			}
		}
		this.setSize(new Size(this.getSize().getRows(), this.getSize().getCols()-1));
		return true;
	}
	
	/**
	 * Duplicate this matrix by x vertically, y horizontally
	 * @param rows
	 * @param cols
	 * @return
	 */
	public IMatrix<Double> repmat(int x, int y, IMatrix<Double> matrix) {
		int rows = getSize().getRows();
		int cols = getSize().getCols();
		int newRows = rows*x;
		int newCols = cols*y;
		int row = 0;
		for(int i=0;i<newRows;i++) {
			int col = 0;
			if (row >= rows) {
				row = 0;
			}
			for(int j=0;j<newCols;j++) {
				if (col >= cols) {
					col = 0;
				}
				matrix.set(i, j, get(row, col));
				col++;
			}
			row++;
		}
		return matrix;
	}
	
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#removeRow(int)
	 */
	public  boolean removeRow(int row) {
		if (row > this.getSize().getRows()) return false;
		int cnt = 0;
		for(int i=0;i<this.getSize().getRows();i++) {
			if (i == row) continue;
			for(int j=0;j<this.getSize().getCols();j++) {
				this.set(cnt, j, this.get(i, j));
			}
			cnt++;
		}
		this.setSize(new Size(this.getSize().getRows()-1, this.getSize().getCols()));
		return true;
	}

	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#reshape(int, int)
	 */
	public  IMatrix<Double> reshape(int rows, int cols, IMatrix<Double> target) {
		if (rows*cols > this.getSize().getRows()*this.getSize().getCols()) {
			return null;
		}
		int n = 0;
		int k = 0;
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				if (k >= cols) {
					k = 0;
					n++;
				}
				if ((n < rows) && (k < cols)) {
					target.set(n, k, this.get(i,j));
					k++;
				}
			}
		}
		return target;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#retainAll(java.util.Collection)
	 */
	public  boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#shuffle()
	 */
	public  IMatrix<Double> shuffle(IMatrix<Double> shuffled) {
		List<Integer> rows = new Vector<Integer>();
		for(int i=0;i<this.getSize().getRows();i++) {
			rows.add(i);
		}
		this.shuffleRows(rows);
		for(int i=0;i<rows.size();i++) {
			int idx = rows.get(i);
			for(int j=0;j<this.getSize().getCols();j++) {
				shuffled.set(i, j, this.get(idx, j));
			}
		}
		return shuffled;
	}
	
	private  void shuffleRows(List<Integer> rows) {
		Random rand = new Random();
		rand.setSeed(Calendar.getInstance().getTimeInMillis());
		for(int i=0;i<this.getSize().getRows()/2;i++) {
			for(int j=this.getSize().getRows() -1;j>this.getSize().getRows()/2;j--) {
				if (rand.nextInt() % 2 == 0) {
					int tmp = rows.get(i);
					rows.set(i, rows.get(j));
					rows.set(j, rows.get(j));
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#rowsMeans()
	 */
	public  IMatrix<Double> rowsMeans(IMatrix<Double> means) {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		// calculate the mean and standard deviation for each column
		for(int j=0;j<rows;j++) {
			double sum = 0.0;
			double max = -1*Double.MIN_VALUE;
			double min = Double.MAX_VALUE;
			for(int k=0;k<cols;k++) {
				double val = (this.get(j,k) != null) ? this.get(j,k).doubleValue() : 0;
				if (val < min) {
					min = val;
				}
				if (val > max) {
					max = val;
				}
				sum += val;
			}
			if (sum != 0) {
				means.set(j, 0, sum/rows);
			} else {
				means.set(j, 0, 0.0);
			}
		}
		return means;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#sin()
	 */
	public  IMatrix<Number> sin(IMatrix<Number> result) {
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				result.set(i, j, Math.sin(this.get(i,j).doubleValue()));
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#getSize()()
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#getSize()()
	 */
	public  int size() {
		// TODO Auto-generated method stub
		return this.getSize().getWidth()*this.getSize().getHeight();
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#subtract(au.id.cpd.algorithms.data.IMatrix)
	 */
	public  IMatrix<Number> subtract(IMatrix<Number> matrix, IMatrix<Number> result) {
		if (this.getSize().compareTo(matrix.getSize()) != 0) return null;
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				double a = this.get(i,j).doubleValue();
				double b = matrix.get(i,j).doubleValue();
				result.set(i, j, a-b);
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#subtract(java.lang.Double)
	 */
	public  IMatrix<Number> subtract(Double b, IMatrix<Number> result) {
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				double a = this.get(i,j).doubleValue();
				result.set(i, j, a-b);
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#subtractFrom(java.lang.Double)
	 */
	public  IMatrix<Number> subtractFrom(Double b, IMatrix<Number> result) {
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				double a = this.get(i,j).doubleValue();
				result.set(i, j, b - a);
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#sum(java.lang.Double)
	 */
	public  IMatrix<Number> sum(Double b, IMatrix<Number> result) {
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				double a = this.get(i,j).doubleValue();
				result.set(i, j, a+b);
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#sum(au.id.cpd.algorithms.data.IMatrix)
	 */
	public  IMatrix<Number> sum(IMatrix<Number> matrix, IMatrix<Number> result) {
		if (this.getSize().compareTo(matrix.getSize()) != 0) return null;
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				double a = this.get(i,j).doubleValue();
				double b = matrix.get(i,j).doubleValue();
				result.set(i, j, a+b);
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#tanh()
	 */
	public  IMatrix<Number> tanh(IMatrix<Number> result) {
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				result.set(i, j, Math.tanh(this.get(i,j).doubleValue()));
			}
		}
		return result;
	}
	
	/**
	 * This could potentially be very large.
	 * Not much point writing a large matrix to memory
	 * (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#toArray()
	 */
	public  Object[] toArray() {
		Object[] arr = new Object[this.size()];
		int cnt = 0;
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				arr[cnt] = this.get(i,j);
				cnt++;
			}
		}
		return arr;
	}
	
	/**
	 * This could potentially be very large.
	 * Not much point writing a large matrix to memory
	 *  (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#toArray(java.lang.Object[])
	 */
	public  Object[] toArray(Object[] a) {
		int cnt = 0;
		boolean flag = false;
		for(int i=0;i<this.getSize().getRows();i++) {
			for(int j=0;j<this.getSize().getCols();j++) {
				if (cnt < a.length) {
					a[cnt] = this.get(i, j);
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
	 * @see au.id.cpd.algorithms.data.IMatrix#transform()
	 */
	public  IMatrix<Number> transform(IMatrix<Number> matrix) {
		for(int i=0;i<this.size.getRows();i++) {
			for(int j=0;j<this.size.getCols();j++) {
				matrix.set(j, i, this.get(i,j));
			}
		}
		return matrix;
	}
	
}
