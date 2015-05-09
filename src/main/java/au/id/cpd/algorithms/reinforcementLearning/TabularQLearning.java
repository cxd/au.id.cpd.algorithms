/**
 * 
 */
package au.id.cpd.algorithms.reinforcementLearning;

import java.util.*;

import au.id.cpd.algorithms.data.*;
import au.id.cpd.algorithms.data.io.*;


/**
 * The TabularQLearning algorithm
 * allows state,action selection to learn a value function
 * Q(s,a) using e-greedy explorarion.
 * 
 * This uses a tabular qlearning method rather than 
 * a function approximation method.
 * 
 * @author cd
 *
 */
public class TabularQLearning {

	/**
	 * Discount gamma
	 */
	private double gamma = 0.9;
	/**
	 * Constant learning rate.
	 */
	private double learnRate = 0.1;
	/**
	 * Q function.
	 */
	private IMatrix<Double> Q;
	/**
	 * Rewards lookup table.
	 */
	private IMatrix<Double> R;

	/**
	 * A trail of averaged qValues.
	 */
	private List<Double> qValues;
	
	/**
	 * Count of iterations.
	 */
	private double iteration = 0.0;
	
	/**
	 * A container for states.
	 * Enum acts as a tabular lookup.
	 */
	private StateEnumContainer states;
	
	/**
	 * A container for actions.
	 * Enum acts as a tabular lookup.
	 */
	private ActionEnumContainer actions;
	/**
	 * The currently visited state.
	 */
	private java.lang.Enum curState;
	/**
	 * The previous state
	 */
	private java.lang.Enum prevState;
	/**
	 * The currently selected action
	 * according to the e-greedy policy.
	 */
	private java.lang.Enum curAction;
	
	/**
	 * Constructor.
	 *
	 */
	public TabularQLearning() {
	
	}
	
	/**
	 * Construct with state and action enumerations.
	 *
	 */
	public TabularQLearning(StateEnumContainer s, ActionEnumContainer a) {
		states = s;
		actions = a;
		initialise();
	}
	
	/**
	 * Initialise the Q function table
	 * and the Rewards table.
	 *
	 */
	public void initialise() {
		this.R = Matrix.zeroes(states.size(), states.size());
		this.Q = Matrix.zeroes(actions.size(), states.size());
		this.qValues = new ArrayList<Double>();
		for(int i=0;i<states.size();i++) {
			for(int j=0;j<states.size();j++) {
				// transition from state j to state i
				// receive reward i.
				R.set(j, i, states.getReward(states.get(i)));
			}
		}
	}
	

	/**
	 * Update the q function table.
	 * Q(s,a) = Q(s,a) + l* [r + g * max(Q(s',a') - Q(s,a)]
	 * Where l is the learning rate
	 * g is the discount (gamma)
	 * s and a are the current state and action
	 * s' is the next state
	 * a' is the next action selected from policy Q' with e-greedy selection.
	 * @param visited State
	 */
	public void update(java.lang.Enum updateState) {
		prevState = curState;
		curState = updateState;
		int i = states.getIndex(prevState);
		int j = states.getIndex(curState);
		int k = actions.getIndex(curAction);
		double r = R.get(i, j).doubleValue();
		// the current value of Q(s,a)
		double t = Q.get(k, i).doubleValue();
		java.lang.Enum next = nextAction();
		int kn = actions.getIndex(next);
		// the value of the optimal action in the next state.
		double tn = Q.get(kn, j).doubleValue();
		// apply the update
		double v = t + learnRate * ( r + gamma * ( tn - t) );
		// update the q table.
		Q.set(k, i, v);
		// define the next action.
		curAction = next;
		qValues.add(v);
		// normalise the q function.
		Q = Q.minMaxNormalise();
		iteration++;
	}
	
	/**
	 * Select the next action using the e-greedy policy for Q*
	 * 
	 * There is a small probability that a random action is chosen.
	 * 
	 * @return ActionCategory next action to take.
	 */
	public java.lang.Enum nextAction() {
		Random r = new Random();
		double rnd = r.nextDouble();
		java.lang.Enum next = null;
		int idx = 0;
		if (rnd*100 < Math.E) {
			// choose a random value.
			idx = 0;
			Random r2 = new Random();
			idx = r2.nextInt(actions.size());
		} else {
			// choose the maximum Q(s',a') using policy Q.
			int stateIndex = states.getIndex(curState);
			List<Double> col = Q.getColumn(stateIndex);
			int maxRow = 0;
			double max = Double.MIN_VALUE;
			for(int i=0;i<col.size();i++) {
				if ((i < 0)||(i >= actions.size()))
					continue;
				if (states.canTransition(curState, actions.get(i))) {
					maxRow = i;
					max = col.get(i);
				}
			}
			// find the maximum action value.
			idx = maxRow;
		}
		int cnt = 0;
		
		for(int i=0;i<actions.size();i++) {
			if (cnt == idx) {
				next = actions.get(i);
				break;
			}
			cnt++;
		}
		return next;
	}
	
	/**
	 * Select the next action using the greedy policy for Q*
	 * No random selection.
	 * 
	 * @return ActionCategory next action to take.
	 */
	public java.lang.Enum maxAction() {
		Random r = new Random();
		double rnd = r.nextDouble();
		java.lang.Enum next = null;
		int idx = 0;
		// choose the maximum Q(s',a') using policy Q.
		int stateIndex = states.getIndex(curState);
		List<Double> col = Q.getColumn(stateIndex);
		int maxRow = 0;
		double max = Double.MIN_VALUE;
		for(int i=0;i<col.size();i++) {
			if (col.get(i) > max) {
				if ((i < 0)||(i >= actions.size()))
					continue;
				if (states.canTransition(curState, actions.get(i))) {
					maxRow = i;
					max = col.get(i);
				}
			}
		}
		// find the maximum action value.
		idx = maxRow;
		int cnt = 0;
		
		for(int i=0;i<actions.size();i++) {
			if (cnt == idx) {
				next = actions.get(i);
				break;
			}
			cnt++;
		}
		return next;
	}

	/**
	 * @return the actions
	 */
	public ActionEnumContainer getActions() {
		return actions;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(ActionEnumContainer actions) {
		this.actions = actions;
	}

	/**
	 * @return the curAction
	 */
	public java.lang.Enum getCurAction() {
		return curAction;
	}

	/**
	 * @param curAction the curAction to set
	 */
	public void setCurAction(java.lang.Enum curAction) {
		this.curAction = curAction;
	}

	/**
	 * @return the curState
	 */
	public java.lang.Enum getCurState() {
		return curState;
	}

	/**
	 * @param curState the curState to set
	 */
	public void setCurState(java.lang.Enum curState) {
		this.curState = curState;
	}

	/**
	 * @return the gamma
	 */
	public double getGamma() {
		return gamma;
	}

	/**
	 * @param gamma the gamma to set
	 */
	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	/**
	 * @return the iteration
	 */
	public double getIteration() {
		return iteration;
	}

	/**
	 * @param iteration the iteration to set
	 */
	public void setIteration(double iteration) {
		this.iteration = iteration;
	}

	/**
	 * @return the learnRate
	 */
	public double getLearnRate() {
		return learnRate;
	}

	/**
	 * @param learnRate the learnRate to set
	 */
	public void setLearnRate(double learnRate) {
		this.learnRate = learnRate;
	}

	/**
	 * @return the prevState
	 */
	public java.lang.Enum getPrevState() {
		return prevState;
	}

	/**
	 * @param prevState the prevState to set
	 */
	public void setPrevState(java.lang.Enum prevState) {
		this.prevState = prevState;
	}

	/**
	 * @return the q
	 */
	public IMatrix<Double> getQ() {
		return Q;
	}

	/**
	 * @param q the q to set
	 */
	public void setQ(IMatrix<Double> q) {
		Q = q;
	}

	/**
	 * @return the qValues
	 */
	public List<Double> getQValues() {
		return qValues;
	}

	/**
	 * @param values the qValues to set
	 */
	public void setQValues(List<Double> values) {
		qValues = values;
	}

	/**
	 * @return the r
	 */
	public IMatrix<Double> getR() {
		return R;
	}

	/**
	 * @param r the r to set
	 */
	public void setR(IMatrix<Double> r) {
		R = r;
	}

	/**
	 * @return the states
	 */
	public StateEnumContainer getStates() {
		return states;
	}

	/**
	 * @param states the states to set
	 */
	public void setStates(StateEnumContainer states) {
		this.states = states;
	}
	
	
}
