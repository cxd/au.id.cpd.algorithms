/**
 * 
 */
package au.id.cpd.algorithms.data;

import java.util.Iterator;

/**
 * @author cd
 *
 */
public class MatrixIterator implements Iterator {

	private IMatrix matrix;
	private int rowIndex;
	private int colIndex;
	
	public MatrixIterator() {
		this.matrix = null;
	}
	
	public MatrixIterator(IMatrix m) {
		this.matrix = m;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if ((this.rowIndex < this.matrix.getSize().getRows())&&(this.colIndex < this.matrix.getSize().getCols()))
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		Object val = this.matrix.get(this.rowIndex, this.colIndex);
		this.rowIndex++;
		this.colIndex++;
		return val;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		// TODO Auto-generated method stub
		this.matrix.set(this.rowIndex, this.colIndex, null);
	}

}
