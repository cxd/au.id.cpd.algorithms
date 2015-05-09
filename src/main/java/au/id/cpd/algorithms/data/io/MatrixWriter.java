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
public class MatrixWriter extends BufferedWriter {

	public MatrixWriter(Writer in) {
		super(in);
	}
	
	public MatrixWriter(Writer in, int sz) {
		super(in, sz);
	}
	
	/**
	 * Write the matrix data to the output stream.
	 * @param matrix
	 * @param out
	 */
	public void writeMatrix(IMatrix<Double> matrix, FileWriter out) throws IOException {
		int rowCnt = matrix.getSize().getRows();
		int colCnt = matrix.getSize().getCols();
		for(int i=0;i<rowCnt;i++) {
			String row = "";
			for(int j=0;j<colCnt;j++) {
				row += matrix.get(i, j).toString();
				if (j<colCnt-1)
					row+=",";
			}
			row += System.getProperty("line.separator");
			out.write(row);
		}
	}
}
