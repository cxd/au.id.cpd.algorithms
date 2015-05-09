/**
 * 
 */
package au.id.cpd.algorithms.data.io;

import java.io.*;
import java.util.List;
import java.util.Vector;

import au.id.cpd.algorithms.data.*;
/**
 * @author cd
 *
 */
public class MatrixReader extends BufferedReader {

	public MatrixReader(Reader in) {
		super(in);
	}
	
	public MatrixReader(Reader in, int sz) {
		super(in, sz);
	}
	/**
	 * Read a comma delimited file into a matrix data type.
	 * @return Matrix<Double> matrix
	 */
	public IMatrix<Double> readMatrix() {
		String line = "";
		List<String[]> dataItems = new Vector<String[]>();
 		int cols = 0;
		try {
	 		while((line = this.readLine()) != null) {
				String[] items = line.split(",");
				if (cols == 0)
					cols = items.length;
				dataItems.add(items);
			}
	 		int rows = dataItems.size();
			int cnt = 0;
			Matrix<Double> matrix = new Matrix<Double>(rows, cols);
			for(String[] items : dataItems) {
				for(String item : items) {
					try {
					Double val = Double.parseDouble(item);
						matrix.add(val);
					} catch(NumberFormatException e) {
						matrix.add(0);
					}
				}
				cnt++;
			}
			return matrix;
		} catch(IOException e) {
			return null;
		}
	}
}
