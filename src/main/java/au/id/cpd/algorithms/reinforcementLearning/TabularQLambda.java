/**
 * 
 */
package au.id.cpd.algorithms.reinforcementLearning;

import java.util.*;
import au.id.cpd.algorithms.data.*;

/**
 * 
 * A serial implementation of Watkins Q-lambda algorithm.
 * 
 * @author cd
 *
 */
public class TabularQLambda extends TabularQLearning {

	/**
	 * eligibility trace
	 * probability of s,a reoccuring given current action.
	 */
	private IMatrix<Double> e;
	
	private double lambda;
	
	public TabularQLambda() {
		
	}
	
	public TabularQLambda(StateEnumContainer s, ActionEnumContainer a) {
		super(s, a);
	}
	
	/**
	 * Initialise prerequisites for qlambda
	 */
	public void initialise() {
		super.initialise();
		e = Matrix.zeroes(this.getActions().size(), this.getStates().size());
	}
	
	/**
	 * Update the q function table.
	 * Q(s,a) = Q(s,a) + learnRate* gradient * e(s,a)
	 * gradient = r + gamma * max Q(s', a') - Q(s, a)
	 * e(s, a) = lambda * gamma * e(s, a) if (sn == s, an == a)
	 * e(s, a) = 0 otherwise
	 * @param visited State
	 */
	public void update(java.lang.Enum updateState) {
		this.setPrevState(this.getCurState());
		this.setCurState(updateState);
		int i = this.getStates().getIndex(this.getPrevState());
		int j = this.getStates().getIndex(this.getCurState());
		int k = this.getActions().getIndex(this.getCurAction());
		double r = this.getR().get(i, j).doubleValue();
		// the current value of Q(s,a)
		double t = this.getQ().get(k, i).doubleValue();
		java.lang.Enum next = this.nextAction();
		int kn = this.getActions().getIndex(next);
		// the value of the optimal action in the next state.
		double tn = this.getQ().get(kn, j).doubleValue();
		// determine the maxium action.
		java.lang.Enum maxAction = this.maxAction();
		// compute the gradient
		double g = r + this.getGamma() * (tn - t);
		// update the eligibility trace
		e.set(k, i, e.get(k, i).doubleValue() + 1.0);
		// apply the update to Q
		for(int ai = 0; ai < this.getActions().size(); ai++) {
			for(int si = 0; si < this.getStates().size(); si++) {
				this.getQ().set(ai, si, this.getQ().get(ai, si).doubleValue() + this.getLearnRate() * g * e.get(ai,si).doubleValue());
				if (next == maxAction) {
					e.set(ai, si, lambda * this.getGamma() * e.get(ai, si).doubleValue());
				} else {
					e.set(ai, si, 0.0);
				}
			}
		}
		// define the next action.
		this.setCurAction(next);
		this.getQValues().add(this.getQ().get(k, i).doubleValue());
		// normalise the q function.
		this.setQ(this.getQ().minMaxNormalise());
		this.setIteration(this.getIteration() + 1);
	}
	
}
