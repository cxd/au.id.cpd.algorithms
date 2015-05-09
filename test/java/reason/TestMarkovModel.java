/**
 * 
 */
package reason;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Before;
import org.junit.Test;

import au.id.cpd.algorithms.reason.*;
import au.id.cpd.algorithms.data.*;

/**
 * @author cd
 *
 */
public class TestMarkovModel {
	private MarkovModel model;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.reason.MarkovModel#filter(int)}.
	 * This is an example of filtering from the aima textbook
	 * we can test for the expected result given that we know the answer.
	 * The example is concerned with a Rain, Umbrella world
	 * where the hidden state variables represent the probability of Rain
	 * and the Evidence variables represent the presence of an umbrella.
	 * Distributions are taken from the text book's example.
	 */
	@Test
	public void testFilter() {
		// set up the state model X
		List<Double> stateValues = new Vector<Double>();
		stateValues.add(1.0);
		stateValues.add(0.0);
		// set up our prior probabilities.
		List<Double> priors = new Vector<Double>();
		priors.add(0.7);
		priors.add(0.3);
		// Set up the initial state probability distribution.
		IMatrix<Double> probabilities = new Matrix<Double>(3, 2);
		probabilities.set(0, 0, 0.5);
		probabilities.set(0, 1, 0.5);
		// set up the sensor model.
		List<Double> sensorValues = new Vector<Double>();
		sensorValues.add(1.0);
		
		// Set up the initial sensor state.
		IMatrix<Double> sensor = new Matrix<Double>(3,2);
		sensor.set(0,0,0.5);
		sensor.set(0,1,0.5);
		sensor.set(1,0,0.9);
		sensor.set(1,1,0.2);
		sensor.set(2,0,0.9);
		sensor.set(2,1,0.2);
		this.model = new MarkovModel(stateValues,
									sensorValues,
									priors,
									sensor,
									probabilities);
		List<Double> filtered = this.model.filter(2);
		System.out.println(filtered);
		// according to the text the result should be
		// [0.883, 0.117]
		assertTrue((filtered.get(0) > 0.882)&&(filtered.get(0) < 0.884)&&(filtered.get(1) > 0.116)&&(filtered.get(1) < 0.118));
	}

}
