/**
 * 
 */
package au.id.cpd.algorithms.reinforcementLearning;

/**
 * @author cd
 *
 */
public interface StateEnumContainer extends EnumContainer {

	
	public double getReward(java.lang.Enum e);
	
	public boolean canTransition(java.lang.Enum curState, java.lang.Enum proposedAction);
}
