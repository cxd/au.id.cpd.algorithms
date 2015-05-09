/**
 * 
 */
package au.id.cpd.algorithms.reason;


import java.util.*;
import au.id.cpd.algorithms.data.*;

/**
 * @author cd at cpd.id.au
 * @version 0.1
 * <h1>Probabilistic Reasoning over Time</h1>
 * <h2>Hidden Markov Models</h2>
 * <p>
 * A changing world is modelled using a random variable
 * for each aspect of the world state at each point in time.
 * </p>
 * <p><b>Background Principles</b>
 * 	<ul>
 * 	<li>
 * P(A,B) = P(A|B)P(B) = P(B|A)P(A) - <i>Bayes Rule</i>
 * </li>
 * <li>
 * P(B) = sum for i P(B,A i) = sum for i P(B|A i)P(A i) - <i>Total Probabilities</i>
 * </li>
 *  </ul>
 * </p>
 * <p>
 * A Markov Model consists of the following:
 * <ul>
 * <li>
 * A Set of States {1,2...N}
 * </li>
 * <li>
 * A Transition probability Matrix P(Xt+1 = j | Xt = i) ie: [i,j]
 * </li>
 * <li>
 * An Initial State Distribution P(X0 = j)
 * </li>
 * </ul>
 * The question put to the Markov Model is What is the probability that X = value i at time t?<br/>
 * P(Xt = i)
 * </p>
 * <p>
 * Xt - denotes a set of unobservable variables at time <i>t</i><br/>
 * Et - set of observable evidence at time <i>t</i><br/>
 * Observation at time <i>t</i> = Et for values et.<br/>
 * </p>
 * <p>
 * Generally assume that the Evidence at time t depends only on the current state of Xt.
 * </p>
 * <p>
 * P(Et | X0:t, Et-1) = P(Et|Xt) - <i>simplified assumption</i>
 * </p>
 * <p>
 * The conditional distribution P(Et|Xt) is the sensor model. It describes how the
 * sensors perceive the world, that is how the evidence variables are affected by 
 * the actual state of the world.
 * </p>
 * <p>
 * We also need to specify the prior probability of the starting state P(X0) at time 0.
 * </p>
 * <p>
 * The three variables:
 * <ul>
 * <li>
 * P(Xt | X0:t-1) = P(Xt | Xt-1) - the current state at t.
 * </li>
 * <li>
 * P(Et | X0:t, E0:t-1) = P(Et | Xt) - the evidence at t.
 * </li>
 * <li>
 * P(X0) = the prior probability of state at t = 0.
 * </li>
 * </ul>
 * Give us the complete specification for the joint distribution
 * over all variables at time t.
 * </p>
 * <p>
 * P(X0,X1...Xt, E1...Et) = P(X0) times the product for t i=1 P(Xi|Xi-1)P(Ei|Xi)
 * </p>
 * <h2>Inference with Markov Models</h2>
 * <p>
 * The tasks performed by Markov Models are:<br/>
 * <ul>
 * <li><b>Filtering</b> - This is the task of computing the 
 * <b>belief state</b> - the posterior distribution over the current state given all evidence to date. 
 * P(Xt|e1:t) [1]
 * </li>
 * <li>
 * <b>Prediction</b> - The task of computing the posterier distribution over the <b>future state</b> given all evidence
 * to date. P(Xt+k|e1:t) [1]
 * </li>
 * <li>
 * <b>Smoothing or Hindsight</b> - This is the task of computing the posterier distribution
 * over a <b>past state</b> given all evidence to date. P(Xk|e1:t) [1]
 * </li>
 * <li>
 * <b>Most likely explanation</b> - Given a sequence of observations, we would like to find the 
 * sequence of states that are most likely to have generated those observations. argmax P(x1:t|e1:t) [1]
 * </li>
 * </ul>
 * </p>
 * 
 * <h2>References</h2>
 * <ul>
 * 1. Russell, S. Norvig, P. "Artificial Intelligence A Modern Approach" 2nd Ed, Prentice Hall, New Jersey 2003.
 * </ul>
 *
 */
public class MarkovModel {
	/**
	 * List of values for hidden states.
	 * The range of potential values for a state.
	 * eg: Rain = [Rain, ~Rain]
	 * Length of stateValue must equal columns in 
	 * transition matrix.
	 */
	private List<Double> stateValues;
	/** 
	 * This is a simple model.
	 * A State is a discrete value.
	 * transition probability matrix.
	 * m x n matrix
	 * m - time t
	 * n = probabiliy for state = stateValue{n} at time t
	 */
	private IMatrix<Double> transitions;
	/**
	 * List of evidence values for evidence node.
	 * Range of values must equal the number of columns
	 * in the evidence probability matrix.
	 */
	private List<Double> evidenceValues;
	/**
	 * A evidence probability matrix
	 * m x n
	 * m - time t
	 * n - probability of evidence = evidenceValue{n} at time t
	 * n+1 - probability of evidence ~evidenceValue{n} at time t
	 * Therefore n = 2 * length of evidenceValues
	 * to provide positive and negative probabilities for each evidence value. 
	 */
	private IMatrix<Double> evidence;
	/** 
	 * Initial prior distribution for X0
	 * Range of distribution = the length of stateValues
	 */
	private List<Double> priorTransition;
	
	
	// number of states to filter.
	private int time;
	// A list of filtered probabilities for
	// states at time t.
	private List<Double> filters;
	
	public MarkovModel() {
		
	}
	
	public MarkovModel(	List<Double> states, 
						List<Double> evidenceValues,
						List<Double> priors,
						IMatrix<Double> evidence,
						IMatrix<Double> transitions) {
		this.stateValues = states; 
		this.evidenceValues = evidenceValues;
		this.priorTransition = priors;
		this.evidence = evidence;
		this.transitions = transitions;
	}
	
	/**
	 * Predict the <b>belief state</b> the posterier distribution
	 * over the current state, given all evidence to date.
	 * 
	 * Predict the value of state Xt at time t
	 * based on prior distribution and evidence distribution.
	 * Prior distribution will have a vector length equal to
	 * the vector length of state values.
	 * The evidence variables will be a n x m matrix
	 * where n = t (the desired time at which to predict X).
	 * and m = 2* length of vector evidenceValues
	 * 
	 * The two columns in the evidence matrix represent [P(E), P(~E)]
	 * 
	 * @param Precondition state vector must be defined.
	 * @param Prior transitions probability matrix must be defined.
	 * @return List of probability distributions for states 1..n at time t.
	 */
	public List<Double> filter(int t) {
		this.time = t;
		this.filters = new Vector<Double>();
		this.forward(t);
		for(int i=0;i<this.stateValues.size();i++) {
			this.filters.add(this.transitions.get(t, i).doubleValue());
		}
		return this.filters;
	}
	/**
	 * Predict the value of state Xt at time t
	 * based on prior distribution and evidence distribution.
	 * Prior distribution will have a vector length equal to
	 * the vector length of state values.
	 * The evidence variables will be a n x m matrix
	 * where n = t (the desired time at which to predict X)
	 * and m = 2* length of vector evidenceValues
	 * @param t
	 * @todo Modify forward algorithm to handle more 
	 * then 1 state variable 
	 * and 1 evidence variable in the network.
	 */
	public void forward(int t) {
		// compute the probability of the states. 
		// P(Xt) = sum P(Xt|Xt-1)*P(Xt-1)
		// sum 2 vectors.
		// [a,b] = [ [pk1, npk1]x prior1, [pk2, npk2]x prior2 ]
 		// update for evidence
		// [et, net] x [a,b]
		// normalise
		Matrix<Double> set = new Matrix<Double>(1, 2);
		set.add(0.0);
		set.add(0.0);
		for(int i=0;i<this.stateValues.size();i++) {
			// compute probability of Xi
			// in the case of P(X) we also need to count 1 - P(X)
			Double pt = this.transitions.get(t, i).doubleValue();
			Double npt = 1 - pt;
			Double pk = this.priorTransition.get(i);
			set.set(0,0, set.get(0,0).doubleValue()+pt*pk);
			set.set(0,1, set.get(0,1).doubleValue()+npt*pk);
		}
		// evidence should represent a set of 2 column vectors
		// for each evidence variable et.
		// update for the evidence variables.
		// P(Xt|Et) = alpha P(Et|Xt)P(Xt)
		// P(Et|Xt) must be defined in the sensor/evidence matrix.
		// sensor/evidence matrix is updated from outside the class
		// for time t.
		// row vector T * row vector - elementwise multiplication.
		Double e = 1.0; 
		Double ne = 1.0; 
		// the product of the set of evidence variables is taken
		// this is also naive since it 
		// assumes conditional independance between
		// the evidence variables.
		for(int i=0;i<this.evidence.getSize().getCols();i+=2) {
			e *= (Double)this.evidence.get(t+1, i);
			ne *= (Double)this.evidence.get(t+1, i+1);
		}
		set.set(0, 0, set.get(0, 0).doubleValue()*e);
		set.set(0, 1, set.get(0, 1).doubleValue()*ne);
		Double sum = set.sumRow(0);
		Double alpha = 1.0/sum;
		// normalise the probability and update the state Xt+1 with the result.
		// 
		for(int i=0;i<set.getSize().getCols();i++) {
			this.transitions.set(t+1, i, set.get(0, i).doubleValue()*alpha);
		}
		if (t == this.time-1) return;
		else this.forward(++t);
	}

	/**
	 * @return the evidence
	 */
	public IMatrix<Double> getEvidence() {
		return evidence;
	}

	/**
	 * @param evidence the evidence to set
	 */
	public void setEvidence(IMatrix<Double> evidence) {
		this.evidence = evidence;
	}

	/**
	 * @return the priorTransition
	 */
	public List<Double> getPriorTransition() {
		return priorTransition;
	}

	/**
	 * @param priorTransition the priorTransition to set
	 */
	public void setPriorTransition(List<Double> priorTransition) {
		this.priorTransition = priorTransition;
	}

	

	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * @return the transitions
	 */
	public IMatrix<Double> getTransitions() {
		return transitions;
	}

	/**
	 * @param transitions the transitions to set
	 */
	public void setTransitions(IMatrix<Double> transitions) {
		this.transitions = transitions;
	}

	/**
	 * @return the filters
	 */
	public List<Double> getFilters() {
		return filters;
	}

	/**
	 * @param filters the filters to set
	 */
	public void setFilters(List<Double> filters) {
		this.filters = filters;
	}
}
