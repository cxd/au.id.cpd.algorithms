/**
 * 
 */
package classifier;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import au.id.cpd.algorithms.data.*;
import au.id.cpd.algorithms.data.io.*;
import au.id.cpd.algorithms.classifier.*;
import au.id.cpd.algorithms.classifier.ANN.*;
import java.io.*;
import java.util.*;
/**
 * @author cd
 *
 */
public class TestNeuralNetworkSpeed {
	private IMatrix<Double> data;
	private Matrix<Double> train1;
	private Matrix<Double> test1;
	private Matrix<Double> train2;
	private Matrix<Double> test2;
	
	private NeuralNetwork ann;
	private static double errorA = 0.0;
	private static double errorB = 0.0;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// we need to read the test data into a Matrix set
		// and to identify the column under test.
		String file = "resources/data/speed-continuous/leftdata.csv";
		FileReader fin = new FileReader(new File(file));
		MatrixReader reader = new MatrixReader(fin);
		IMatrix<Double> matrix = reader.readMatrix();
		// we need to split the data into training and test data.
		// 70% train, 30% test
		int rows = matrix.getSize().getRows();
		int cols = matrix.getSize().getCols();
		int trows = (int)(0.7*rows);
		int cnt = 0;
		this.train1 = new Matrix<Double>(trows, cols);
		this.test1 = new Matrix<Double>(rows - trows, cols);
		for(int i=0;i<rows;i++) {
			for(int j=0;j<cols;j++) {
				if (i < trows) {
					this.train1.add(matrix.get(i,j));
				} else {
					this.test1.add(matrix.get(i, j));
				}
			}
		}
		// we need to read the test data into a Matrix set
		// and to identify the column under test.
		file = "resources/data/speed-continuous/rightdata.csv";
		matrix = null;
		try {
			fin = new FileReader(new File(file));
			reader = new MatrixReader(fin);
			matrix = reader.readMatrix();
		} catch(FileNotFoundException e) {
			fail("Failed to load file.");
		}
		// we need to split the data into training and test data.
		// 70% train, 30% test
		rows = matrix.getSize().getRows();
		cols = matrix.getSize().getCols();
		System.err.println("Rows: "+rows+" Cols: "+cols);
		trows = (int)(0.7*rows);
		cnt = 0;
		this.train2 = new Matrix<Double>(trows, cols);
		this.test2 = new Matrix<Double>(rows - trows, cols);
		for(int i=0;i<rows;i++) {
			for(int j=0;j<cols;j++) {
				if (i < trows) {
					this.train2.add(matrix.get(i,j));
				} else {
					this.test2.add(matrix.get(i, j));
				}
			}
		}
		
	}
	
	/**
	 * Test method for {@link au.id.cpd.algorithms.classifier.NeuralNetwork#learn()}.
	 */
	@Test
	public void testTrainLeft() {
		try {
			File fin = new File("model/speed-model/left-speed.ann");
			if (!fin.exists()) {
				fail("Object file does not exist.");
			}
			FileInputStream is = new FileInputStream(fin);
			ObjectInputStream ois = new ObjectInputStream(is);
			this.ann = (NeuralNetwork)ois.readObject();
			this.ann.setTestSet(this.test1.normalise());
			this.ann.setEpoch(1000);
			this.ann.setMomentum(0.001);
			System.out.println("Train Left");
			this.ann.learn();
			double error = this.ann.test();
			File out = new File("model/speed-model/left-speed.ann");
			if (out.exists()) {
				out.delete();
			}
			FileOutputStream os = new FileOutputStream(out);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(this.ann);
			oos.close();
			System.out.println("Left Percent misclassified: "+error);
		} catch(FileNotFoundException e) {
			fail(e.getMessage());
		} catch(IOException e) {
			fail(e.getMessage());
		} catch(ClassNotFoundException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testTrainRight() {
		try {
			File fin = new File("model/speed-model/right-speed.ann");
			if (!fin.exists()) {
				fail("Object file does not exist.");
			}
			FileInputStream is = new FileInputStream(fin);
			ObjectInputStream ois = new ObjectInputStream(is);
			this.ann = (NeuralNetwork)ois.readObject();
			this.ann.setEpoch(1000);
			this.ann.setMomentum(0.001);
			this.ann.setTestSet(this.test2.normalise());
			System.out.println("Train Right");
			this.ann.learn();
			double error = this.ann.test();
			File out = new File("model/speed-model/right-speed.ann");
			if (out.exists()) {
				out.delete();
			}
			FileOutputStream os = new FileOutputStream(out);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(this.ann);
			oos.close();
			System.out.println("Right Percent misclassified: "+error);
		} catch(FileNotFoundException e) {
			fail(e.getMessage());
		} catch(IOException e) {
			fail(e.getMessage());
		} catch(ClassNotFoundException e) {
			fail(e.getMessage());
		}
	}
	

}
