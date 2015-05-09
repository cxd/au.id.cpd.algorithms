/**
 * 
 */
package classifier;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import au.id.cpd.algorithms.data.*;
import au.id.cpd.algorithms.data.io.*;
import au.id.cpd.algorithms.patterns.ISubject;
import au.id.cpd.algorithms.utilities.plot.GradientDescentFrame;
import au.id.cpd.algorithms.classifier.*;
import au.id.cpd.algorithms.classifier.ANN.*;


import java.io.*;
import java.util.*;
/**
 * @author cd
 *
 */
public class TestTrainNeuralNetworkWine implements au.id.cpd.algorithms.patterns.Observer {
	private IMatrix<Double> data;
	private IMatrix<Double> train1;
	private IMatrix<Double> test1;
	
	
	private NeuralNetwork ann;
	private static double errorA = 0.0;
	
	private static double LEARN_RATE = 0.001;
	private static double MOMENTUM = 0.0000001;
	private static int NET_EPOCHS = 1000;
	private static double NET_MULTI_BIAS = 0.01;
	private static double NET_ERR_THRESHOLD = 0.01;
	
	private GradientDescentFrame plotter;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// we need to read the test data into a Matrix set
		// and to identify the column under test.
		String file = "resources/data/wine2/wine.data";
		FileReader fin = new FileReader(new File(file));
		MatrixReader reader = new MatrixReader(fin);
		IMatrix<Double> matrix = reader.readMatrix();
		// we need to split the data into training and test data.
		// 70% train, 30% test
		int rows = matrix.getSize().getRows();
		int cols = matrix.getSize().getCols();
		int trows = (int)(0.7*rows);
		int cnt = 0;
		// permute the matrix to get a mixture of classes.
		// not enough data to divide the classes here so we can't do 70% training 30% test
		// but we could on larger datasets.
		matrix = matrix.shuffle();
		this.train1 = new Matrix<Double>(rows, cols);
		this.test1 = new Matrix<Double>(rows, cols);
		for(int i=0;i<rows;i++) {
			for(int j=0;j<cols;j++) {
				//if (i < trows) {
					this.train1.add(matrix.get(i,j));
				//} else {
					this.test1.add(matrix.get(i, j));
				//}
			}
		}
	}
	
	/**
	 * Test method for {@link au.id.cpd.algorithms.classifier.NeuralNetwork#learn()}.
	 */
	@Test
	public void testTrain() {
		try {
			ann = new NeuralNetwork();
			
			ann.setInputCount(train1.getSize().getCols() - 1);
			// number of individual classes.
			List<Double> targets = new ArrayList<Double>();
			targets.add(1.0);
			targets.add(2.0);
			targets.add(3.0);
			ann.setTargetsClasses(targets);
			ann.setOutputCount(targets.size());
			ann.setHiddenLayerCount(1);
			// hidden unit count is number of inputs - 1
			ann.setHiddenUnitCount(train1.getSize().getCols() - 2);
			IMatrix<Double> tmp = train1;
			List<Double> trainTargets = train1.getColumn(0);
			List<Double> testTargets = test1.getColumn(0);
			tmp = train1.normalise();
			tmp.setColumn(0, trainTargets);
			ann.setTrainingSet(tmp);
			tmp = test1.normalise();
			tmp.setColumn(0, testTargets);
			ann.setTestSet(tmp);
			
			ann.setTargetColumn(0);
			ann.setUnitType(ActivationType.SIGMOID);
			ann.setLearnRate(LEARN_RATE);
			//ann.setIsTargetThreshold(false);
			ann.setEpoch(NET_EPOCHS);
			ann.setBias(NET_MULTI_BIAS);
			ann.setErrorThreshold(NET_ERR_THRESHOLD);
			ann.setMomentum(MOMENTUM);
			ann.addObserver(this);
			plotter = new GradientDescentFrame(ann.getEpoch());
			// need to define initial weights.
			// we'll make the 1/n
			List<Double> weights = new Vector<Double>();
			Random rnd = new Random();
			rnd.setSeed(Calendar.getInstance().getTimeInMillis());
			for(int i=0;i<ann.getInputCount();i++) {
				weights.add(rnd.nextDouble()/this.train1.getSize().getRows());
			}
			ann.setInputWeights(weights);
			ann.constructNetwork();
			plotter.startCollection();
			
			System.out.println("Train");
			this.ann.learn();
			double error = this.ann.test();
			File out = new File("model/win2/wine.ann");
			if (out.exists()) {
				out.delete();
			}
			FileOutputStream os = new FileOutputStream(out);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(this.ann);
			oos.close();
			System.out.println("Left Percent misclassified: "+error);
		} catch(Exception e) {
			fail(e.getMessage());
		} 
	}
	
	
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.patterns.Observer#notify(au.id.cpd.algorithms.patterns.ISubject, java.lang.Object)
	 */
	public void notify(ISubject sender, Object argument) {
		notify(sender);
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.patterns.Observer#notify(au.id.cpd.algorithms.patterns.ISubject)
	 */
	public void notify(ISubject sender) {
		if (ann.getErrors() == null) return;
		if (ann.getErrors().size() == 0) return;
		plotter.updateTrace(ann.getErrors());
		System.out.println(ann.getErrors().get(ann.getErrors().size() - 1));
	}
	
	
	

}
