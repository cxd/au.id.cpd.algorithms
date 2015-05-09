/**
 * 
 */
package au.id.cpd.algorithms.algebra;

import au.id.cpd.algorithms.data.IMatrix;
import au.id.cpd.algorithms.data.IMatrixOperation;
import au.id.cpd.algorithms.data.Matrix;

/**
 * @author cd
 *
 */
public abstract class DistanceMatrixOperation implements IMatrixOperation {

	/**
	 * The type of distance to calculate.
	 * @author cd
	 *
	 */
	public enum Distance {
		COLUMNS,
		ROWS
	}
	
	/**
	 * distance field.
	 */
	private double distance = Double.MAX_VALUE;
	
	public DistanceMatrixOperation() {
		
	}
	
	/**
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 */
	public IMatrix<Double> operate(IMatrix<Double> input) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(java.lang.Number)
	 */
	public Double operate(Number input) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrixOperation#operate(au.id.cpd.algorithms.data.Matrix)
	 */
	public abstract IMatrix<Double> operate(IMatrix<Double> A, IMatrix<Double> B, Distance type);
	
	/**
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

}
