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
public class TestNeuralNetwork {
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
		String file = "resources/data/pima-indians-diabetes.data";
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
		//		we need to read the test data into a Matrix set
		// and to identify the column under test.
		file = "resources/data/convert.txt";
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
	 * Test method for {@link au.id.cpd.algorithms.classifier.NeuralNetwork#test()}.
	 */
	@Test
	public void testTest() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.classifier.NeuralNetwork#learn()}.
	 */
	@Test
	public void testLearn() {
		this.ann = new NeuralNetwork();
		this.ann.setInputCount(8);
		this.ann.setOutputCount(1);
		this.ann.setHiddenLayerCount(2);
		this.ann.setHiddenUnitCount(7);
		this.ann.setTrainingSet(this.train1.normalise());
		this.ann.setTestSet(this.test1.normalise());
		this.ann.setTargetColumn(8);
		this.ann.setUnitType(ActivationType.SIGMOID);
		this.ann.setLearnRate(0.05);
		this.ann.setIsTargetThreshold(true);
		this.ann.setTargetThreshold(0.5);
		this.ann.setEpoch(500);
		// need to define initial weights.
		// we'll make the 1/n
		List<Double> weights = new Vector<Double>();
		Random rnd = new Random();
		rnd.setSeed(Calendar.getInstance().getTimeInMillis());
		for(int i=0;i<8;i++) {
			weights.add(rnd.nextDouble()/this.train1.getSize().getRows());
		}
		this.ann.setInputWeights(weights);
		try {
			this.ann.constructNetwork();
		} catch(Exception e) {
			fail(e.getMessage());
			return;
		}
		this.ann.learn();
		TestNeuralNetwork.errorA = this.ann.test();
		System.out.println("Percent misclassified: "+TestNeuralNetwork.errorA);
		//System.out.println("Weights: \n"+this.ann.getWeights());
		//System.out.println("Edges: \n"+this.ann.getEdges());
		//System.out.println("Errors: \n"+this.ann.getErrors());
		// write the errors to file for plotting in matlab.
		try {
			String outfile = "resources/test/ann/errors1.txt";
			FileWriter fout = new FileWriter(outfile);
			BufferedWriter writer = new BufferedWriter(fout);
			for(int i=0;i<this.ann.getErrors().size();i++) {
				if (i != this.ann.getErrors().size()-1) {
					writer.write(""+this.ann.getErrors().get(i)+",");
				} else {
					writer.write(""+this.ann.getErrors().get(i));
				}
			}
			writer.close(); 
		} catch(IOException e) {
			System.err.println(e.getMessage());
			fail("failed to write errors.");
		}
		// serialize the network.
		try {
			File out = new File("resources/test/ann/ann_test01.ser");
			if (out.exists()) {
				out.delete();
			}
			FileOutputStream os = new FileOutputStream(out);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(this.ann);
			oos.close();
		} catch(FileNotFoundException e) {
			fail(e.getMessage());
		} catch(IOException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link au.id.cpd.algorithms.classifier.NeuralNetwork#learn()}.
	 */
	@Test
	public void testLearn2() {
		
		
		this.ann = new NeuralNetwork();
		this.ann.setInputCount(12);
		this.ann.setOutputCount(1);
		this.ann.setHiddenLayerCount(1);
		this.ann.setHiddenUnitCount(11);
		this.ann.setTrainingSet(this.train2.normalise());
		this.ann.setTestSet(this.test2.normalise());
		this.ann.setTargetColumn(0);
		this.ann.setUnitType(ActivationType.SIGMOID);
		this.ann.setLearnRate(0.05);
		this.ann.setIsTargetThreshold(true);
		this.ann.setTargetThreshold(0.5);
		this.ann.setEpoch(1000);
		// need to define initial weights.
		// we'll make the 1/n
		List<Double> weights = new Vector<Double>();
		Random rnd = new Random();
		rnd.setSeed(Calendar.getInstance().getTimeInMillis());
		for(int i=0;i<12;i++) {
			weights.add(rnd.nextDouble()/this.train2.getSize().getRows());
		}
		this.ann.setInputWeights(weights);
		try {
			this.ann.constructNetwork();
		} catch(Exception e) {
			fail(e.getMessage());
			return;
		}
		this.ann.learn();
		TestNeuralNetwork.errorB = this.ann.test();
		System.out.println("Percent misclassified: "+TestNeuralNetwork.errorB);
		//System.out.println("Weights: \n"+this.ann.getWeights());
		//System.out.println("Edges: \n"+this.ann.getEdges());
		//System.out.println("Errors: \n"+this.ann.getErrors());
		// write the errors to file for plotting in matlab.
		try {
			String outfile = "resources/test/ann/errors2.txt";
			FileWriter fout = new FileWriter(outfile);
			BufferedWriter writer = new BufferedWriter(fout);
			for(int i=0;i<this.ann.getErrors().size();i++) {
				if (i != this.ann.getErrors().size()-1) {
					writer.write(""+this.ann.getErrors().get(i)+",");
				} else {
					writer.write(""+this.ann.getErrors().get(i));
				}
			}
			writer.close(); 
		} catch(IOException e) {
			System.err.println(e.getMessage());
			fail("Failed to write output.");
		}
		//	serialize the network.
		try {
			File out = new File("resources/test/ann/ann_test02.ser");
			if (out.exists()) {
				out.delete();
			}
			FileOutputStream os = new FileOutputStream(out);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(this.ann);
			oos.close();
		} catch(FileNotFoundException e) {
			fail(e.getMessage());
		} catch(IOException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link au.id.cpd.algorithms.classifier.NeuralNetwork#learn()}.
	 */
	@Test
	public void testSerialize1() {
		try {
			File fin = new File("resources/test/ann/ann_test01.ser");
			if (!fin.exists()) {
				fail("Object file does not exist.");
			}
			FileInputStream is = new FileInputStream(fin);
			ObjectInputStream ois = new ObjectInputStream(is);
			this.ann = (NeuralNetwork)ois.readObject();
			this.ann.setTestSet(this.test1.normalise());
			double error = this.ann.test();
			System.out.println("Percent misclassified: "+error);
			// actually errorA is not stored between test runs.
			assertTrue(error == TestNeuralNetwork.errorA);
		} catch(FileNotFoundException e) {
			fail(e.getMessage());
		} catch(IOException e) {
			fail(e.getMessage());
		} catch(ClassNotFoundException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link au.id.cpd.algorithms.classifier.NeuralNetwork#learn()}.
	 */
	@Test
	public void testSerialize2() {
		try {
			File fin = new File("resources/test/ann/ann_test02.ser");
			if (!fin.exists()) {
				fail("Object file does not exist.");
			}
			FileInputStream is = new FileInputStream(fin);
			ObjectInputStream ois = new ObjectInputStream(is);
			this.ann = (NeuralNetwork)ois.readObject();
			this.ann.setTestSet(this.test2.normalise());
			double error = this.ann.test();
			System.out.println("Percent misclassified: "+error);
			// actually errorB is not stored between test runs.
			assertTrue(error == TestNeuralNetwork.errorB);
		} catch(FileNotFoundException e) {
			fail(e.getMessage());
		} catch(IOException e) {
			fail(e.getMessage());
		} catch(ClassNotFoundException e) {
			fail(e.getMessage());
		}
	}

}
