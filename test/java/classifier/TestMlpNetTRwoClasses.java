package classifier;


import static org.junit.Assert.fail;

import org.junit.*;

import java.io.File;
import java.io.FileReader;
import java.util.*;

import au.id.cpd.algorithms.patterns.ISubject;
import au.id.cpd.algorithms.patterns.Observer;
import au.id.cpd.algorithms.classifier.*;
import au.id.cpd.algorithms.classifier.ANN.*;
import au.id.cpd.algorithms.data.*;
import au.id.cpd.algorithms.data.io.*;
import au.id.cpd.algorithms.utilities.plot.*;


public class TestMlpNetTRwoClasses implements Observer {
	

	private static String data1 = "resources/data/test-pima-diabetes/pima-indians-diabetes.data";
	private Matrix<Double> train1;
	private Matrix<Double> test1;
	private IMatrix<Double> data;
	private MlpNeuralNetwork net;
	private GradientDescentFrame plotter;
	
	
	public IMatrix<Double> readData(String file) {
		try {
			FileReader fin = new FileReader(new File(file));
			MatrixReader reader = new MatrixReader(fin);
			IMatrix<Double> matrix = reader.readMatrix();
			return matrix;
		} catch(Exception e) {
			return null;
		}
	}
	
	@Before
	public void setUp() throws Exception {
		IMatrix<Double> matrix = readData(data1);
		// we need to split the data into training and test data.
		// 70% train, 30% test
		// normalise the data.
		matrix = matrix.normalise();
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
	}
	
	@Test
	public void testLearn() {
		int epoch = 5000;
		net = new MlpNeuralNetwork();
		net.setBias(0.0);
		net.setLearningRate(0.001);
		net.setMomentum(0.001);
		net.setEnergy(0.1);
		net.setTemperature(0.05);
		net.setEpoch(epoch);
		net.setInputCount(8);
		net.setHiddenUnitCount(9);
		net.setHiddenLayerCount(1);
		net.setOutputCount(1);
		List<Integer> t = new ArrayList<Integer>();
		t.add(8); // target column indices - 0 based.
		net.setTargetColumn(t);
		net.setHiddenActivation(ActivationType.SIGMOID);
		net.setOutputActivation(ActivationType.SIGMOID);
		net.setTrainingSet(train1);
		net.setTestSet(test1);
		// plot the error curve.
		try {
			net.constructNetwork();
			net.addObserver(this);
			plotter = new GradientDescentFrame(epoch);
			plotter.startCollection();
			
			net.learn();
			MlpNeuralNetwork.writeNetwork(net, "model/pima-data/test_mlp.ser");
			System.out.println("Errors: " + net.getErrors());
			double error = net.test();
			System.out.println("Test Squared Error: " + error);
			
		} catch(Exception e) {
			System.out.println("Net Learn Failed: " + e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.patterns.Observer#notify(au.id.cpd.algorithms.patterns.ISubject, java.lang.Object)
	 */
	public void notify(ISubject sender, Object argument) {
		plotter.updateTrace(net.getErrors());
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.patterns.Observer#notify(au.id.cpd.algorithms.patterns.ISubject)
	 */
	public void notify(ISubject sender) {
		plotter.updateTrace(net.getErrors());
	}


}
