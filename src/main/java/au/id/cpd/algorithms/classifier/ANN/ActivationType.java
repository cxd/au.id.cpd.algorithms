/**
 * 
 */
package au.id.cpd.algorithms.classifier.ANN;

/**
 * @author cd
 *
 */
public enum ActivationType {
	PERCEPTRON(0, "Step Function", "au.id.cpd.algorithms.classifier.ANN.PerceptronActivator"), 
	SIGMOID(1, "Sigmoid Function", "au.id.cpd.algorithms.classifier.ANN.SigmoidActivator"),
	TANSIG(2, "TanSig Function", "au.id.cpd.algorithms.classifier.ANN.TanSigmoidActivator");
	
	private int val;
	private String name;
	private String className;
	
	private ActivationType(int val, String name, String clsName) {
		this.val = val;
		this.name = name;
		this.className = clsName;
	}
	
	public int getValue() {
		return this.val;
	}
	
	public void setValue(int val) {
		this.val = val;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
}
