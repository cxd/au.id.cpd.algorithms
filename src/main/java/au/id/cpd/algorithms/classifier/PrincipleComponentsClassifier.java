/**
 * 
 */
package au.id.cpd.algorithms.classifier;

import java.util.*;
import au.id.cpd.algorithms.algebra.*;
import au.id.cpd.algorithms.data.*;

/**
 * PrincipleComponentsClassifier
 * 
 * @author cd
 *
 */
public class PrincipleComponentsClassifier {

	private IMatrix<Double> trainingSet;
	
	/**
	 * Working data is used during calculation of
	 * principle components and during projection.
	 * It is the centralised data.
	 */
	private IMatrix<Double> workingData;
	
	/**
	 * The mean data is the normalised data.
	 */
	private IMatrix<Double> meanData;
	
	private IMatrix<Double> covarianceMatrix;
	
	private List<Double> eigenValues;
	/**
	 * The sorted eigen values
	 * arranged in descending order.
	 */
	private List<Double> sortedEigenValues;
	/**
	 * The sorted eigenvectors arranged
	 * in descending order for their corresponding
	 * eigenvalues.
	 * 
	 * The sorted eigen vectors are used during projection.
	 * 
	 */
	private IMatrix<Double> sortedEigenVectors;
	
	/**
	 * An array used to store the sorted eigenValues.
	 */
	private List<Integer> permutation;
	
	/**
	 * Resulting unsorted eigenVectors.
	 */
	private IMatrix<Double> eigenVectors;
	
	/**
	 * The resulting feature projection.
	 * This feature matrix contains
	 * each of the principle components for the supplied data
	 * in descending order of eigenvalue
	 * projected against the input data.
	 */
	private IMatrix<Double> featureMatrix;
	
	/**
	 * The maximum number of components to store.
	 */
	private int numComponents;
	
	private boolean success;
	
	/**
	 * Matching index for search query.
	 */
	private int matchIndex;
	
	/**
	 * Operation to calculate eigenvectors.
	 */
	private EigenvectorMatrixOperation operator;
	
	/**
	 * A distance operation to calculate distances
	 * in projection.
	 * Defaults to euclidean distance operation.
	 */
	private DistanceMatrixOperation distanceOperator;
	
	/**
	 * A search will result in a distance table.
	 * This is an N-rows x 1 column vector containing 
	 * the distance measures for each label at the index of their position
	 * in the image data file.
	 */
	private IMatrix<Double> distanceTable;
	
	/**
	 * The distance of query from the match.
	 */
	private double matchDistance = Double.MAX_VALUE;
	
	/**
	 * Default constructor.
	 */
	public PrincipleComponentsClassifier() {
		numComponents = 0;
		success = false;
		distanceOperator = new EuclideanDistanceMatrixOperation();
	}
	
	/**
	 * Process the input data set
	 * and extract the principle components.
	 * Once complete the 
	 * sortedEigenValues,
	 * sortedEigenVectors
	 * and featureMatrix will have been constructed.
	 * Additionally the workingData set is the mean normalised data set.
	 * 
	 * Supplied training set should be square if possible.
	 *
	 */
	public void learn() {
		if (trainingSet == null) return;
	
		// normalise each column of the trainingSet.
		meanData = trainingSet.meanNormalise();
		IMatrix<Double> singleMean = meanData.columnMeans();
		meanData = singleMean.repmat(meanData.getSize().getRows(), 1);
		// centralise around the mean.
		workingData = trainingSet.subtract(meanData);
		
		System.out.println("Normalized matrix.");
		System.err.println();
		
		// calculate the covariance matrix.
		covarianceMatrix = workingData.covariance().multiply(1.0 / workingData.getSize().getRows() );
		
		System.out.println("Built covariance matrix." + covarianceMatrix.getSize());
		
		operator = new EigenvectorMatrixOperation();
		if (operator.operate(covarianceMatrix) == null) return;
	
		System.err.println("Applied eigenvector operation.");
		System.err.println();
		
		eigenValues = operator.getEigenvalues().diagonal();
		
		System.err.println("EigenValues: " + eigenValues);
		System.err.println();
		
		eigenVectors = operator.getEigenvectors();
		
		permutation = new ArrayList<Integer>();
		for(int i=0;i<eigenValues.size();i++) {
			permutation.add(i);
		}
		
		this.sortEigenValues();
		
		//System.out.println("Sorted eigenvalues." + sortedEigenVectors.getRow(0));
		//System.err.println();
		
		System.err.println("Data Size: "+workingData.getSize().toString() + " Unsorted EigenVectors Size: " + eigenVectors.getSize().toString() + " SortedEigenVectors Size: " + sortedEigenVectors.getSize().toString());
		System.err.println();
		
		//IMatrix<Double> t = sortedEigenVectors.transform();
		
		System.err.println();
		//System.err.println("Working Data: " + workingData.getRow(0));
		//System.err.println();
		//System.err.println("Transform: " + t.getRow(0));
		
		featureMatrix = workingData.transform().multiply(sortedEigenVectors.transform());
		featureMatrix = featureMatrix.transform();
		System.err.println();
		System.out.println("Feature Matrix Size: " + featureMatrix.getSize().toString());
		System.err.println();
		System.err.println("Extracted feature matrix. " + featureMatrix.getRow(0));
		
		success = true;
	}
	
	/**
	 * Project the supplied matrix against the supplied mean and the vector components.
	 * 
	 * The difference between the image and meanImage is calculated 
	 * it's transform is multiplied against the vectors.
	 * 
	 * 
	 * 
	 * @param image
	 * @return
	 */
	public IMatrix<Double> project(IMatrix<Double> image, IMatrix<Double> meanImage, IMatrix<Double> vectors) {
		IMatrix<Double> difference = image.subtract(meanImage);
		return vectors.multiply(difference.transform());
	}
	
	/**
	 * Search for the supplied query against the trained data set.
	 * Uses the projection to find the closest corresponding data.
	 * The supplied query is a row vector.
	 * Returns the row vector from the training data that most closely matches
	 * the query vector.
	 * @param query
	 * @return
	 */
	public IMatrix<Double> search(IMatrix<Double> query) {
		IMatrix<Double> singleMean = null;
		if ( meanData == null ) {
			//  normalise each column of the trainingSet.
			meanData = trainingSet.meanNormalise();
			singleMean = meanData.columnMeans();
			meanData = singleMean.repmat(meanData.getSize().getRows(), 1);
			
		} else {
			singleMean = meanData.columnMeans();
		}
		IMatrix<Double> projection = project(trainingSet, meanData, featureMatrix);
		IMatrix<Double> searchProjection = project(query, singleMean, featureMatrix);
		// using a distance metric calculate the closest vector 
		// in the projection matrix to the searchProjection.
		// also require a distance threshold to determine the distance.
		
		// n rows x 1 columns column A = index B = distance measure.
		distanceTable = new Matrix<Double>(projection.getSize().getCols(), 1);
		
		double min = Double.MAX_VALUE;
		int minIdx = -1;
		matchIndex = -1;
		matchDistance = Double.MAX_VALUE;
		
		for(int i=0;i<projection.getSize().getCols();i++) {
			List<Double> column = projection.getColumn(i);
			// convert column to matrix.
			Matrix<Double> colMatrix = new Matrix<Double>(column.size(), 1);
			colMatrix.addAll(column);
			// determine distance between column and search projection.
			
			IMatrix<Double> distance = distanceOperator.operate(searchProjection, colMatrix, DistanceMatrixOperation.Distance.COLUMNS);
			// 1x1 matrix.
			distanceTable.set(i, 0, distance.get(0, 0));
			if (min > distance.get(0,0).doubleValue()) {
				minIdx = i;
				min = distance.get(0,0).doubleValue();
			}
			System.err.println(i + "Distance: " + distance.get(0,0).doubleValue() );
		}
		
		Matrix<Double> matchMatrix = null;
		if (minIdx > -1) {
			matchIndex = minIdx;
			List<Double> match = trainingSet.getRow(minIdx);
			matchMatrix = new Matrix<Double>(1, match.size());
			matchMatrix.addAll(match);
			matchDistance = distanceTable.get(minIdx, 0).doubleValue();
			System.err.println("MinID: " + minIdx + " Min Distance; " + distanceTable.get(minIdx, 0));
		}
		return matchMatrix;
	}
	
	
	/**
	 * Store the sequence of indices ordering the eigenvalues
	 * in descending order in the permutations list.
	 *
	 */
	private void sortEigenValues() {
		permutation = new ArrayList<Integer>();
		for(int i=0;i<eigenValues.size();i++) {
			permutation.add(0);
		}
		sortEigenValues(0,eigenValues.size()-1);
		if (numComponents == 0) {
			numComponents = eigenValues.size();
		}
		sortedEigenValues = new ArrayList<Double>();
		sortedEigenVectors = new Matrix<Double>(numComponents, eigenVectors.getSize().getCols());
		// in descending order.
		int cnt = 0;
		for(int i=permutation.size() - 1; i>=0;i--) {
			int idx = permutation.get(i);
			List<Double> row = eigenVectors.getRow(idx);
			sortedEigenVectors.setRow(cnt, row);
			cnt++;
			if (cnt == numComponents) break;
		}
	}
	
	/**
	 * Sort the eigenvalues in ascending order.
	 * @param m
	 * @param n
	 */
	private void sortEigenValues(int m, int n) {
		if (m < n) {
			int p = partition(m, n);
			sortEigenValues(m, p-1);
			sortEigenValues(p+1,n);
		}
	}
	/**
	 * Partition the eigen values and store the partitioning
	 * indices in the permutations list.
	 * @param i
	 * @param j
	 * @return
	 */
	private int partition(int i, int j) {
		int middleIdx = (i + j) / 2;
		int p = i;
		int tempIdx;
		double pivotValue = eigenValues.get(p);
		permutation.set(middleIdx, i);
		permutation.set(i, middleIdx);
		
		for(int k=i+1;k<=j;k++) {
			int idx = permutation.get(k);
			double compareValue = eigenValues.get(idx);
			if ( (compareValue < pivotValue) && (p+1 < eigenValues.size())) {
				tempIdx = permutation.get(++p);
				permutation.set(p, k);
				permutation.set(k, tempIdx);
			}
		}
		permutation.set(i, p);
		permutation.set(p, i);
		return p;
	}
	
	/**
	 * @return the covarianceMatrix
	 */
	public IMatrix<Double> getCovarianceMatrix() {
		return covarianceMatrix;
	}

	/**
	 * @param covarianceMatrix the covarianceMatrix to set
	 */
	public void setCovarianceMatrix(Matrix<Double> covarianceMatrix) {
		this.covarianceMatrix = covarianceMatrix;
	}

	/**
	 * @return the eigenValues
	 */
	public List<Double> getEigenValues() {
		return eigenValues;
	}

	/**
	 * @param eigenValues the eigenValues to set
	 */
	public void setEigenValues(List<Double> eigenValues) {
		this.eigenValues = eigenValues;
	}

	/**
	 * @return the eigenVectors
	 */
	public IMatrix<Double> getEigenVectors() {
		return eigenVectors;
	}

	/**
	 * @param eigenVectors the eigenVectors to set
	 */
	public void setEigenVectors(IMatrix<Double> eigenVectors) {
		this.eigenVectors = eigenVectors;
	}

	/**
	 * @return the numComponents
	 */
	public int getNumComponents() {
		return numComponents;
	}

	/**
	 * @param numComponents the numComponents to set
	 */
	public void setNumComponents(int numComponents) {
		this.numComponents = numComponents;
	}

	/**
	 * @return the operator
	 */
	public EigenvectorMatrixOperation getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(EigenvectorMatrixOperation operator) {
		this.operator = operator;
	}

	/**
	 * @return the permutation
	 */
	public List<Integer> getPermutation() {
		return permutation;
	}

	/**
	 * @param permutation the permutation to set
	 */
	public void setPermutation(List<Integer> permutation) {
		this.permutation = permutation;
	}

	/**
	 * @return the principleComponents
	 */
	public IMatrix<Double> getFeatureMatrix() {
		return featureMatrix;
	}

	/**
	 * @param principleComponents the principleComponents to set
	 */
	public void setFeatureMatrix(IMatrix<Double> fM) {
		this.featureMatrix = fM;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the trainingSet
	 */
	public IMatrix<Double> getTrainingSet() {
		return trainingSet;
	}

	/**
	 * @param trainingSet the trainingSet to set
	 */
	public void setTrainingSet(IMatrix<Double> trainingSet) {
		this.trainingSet = trainingSet;
	}

	/**
	 * @return the workingData
	 */
	public IMatrix<Double> getWorkingData() {
		return workingData;
	}

	/**
	 * @param workingData the workingData to set
	 */
	public void setWorkingData(IMatrix<Double> workingData) {
		this.workingData = workingData;
	}

	/**
	 * @return the sortedEigenValues
	 */
	public List<Double> getSortedEigenValues() {
		return sortedEigenValues;
	}

	/**
	 * @param sortedEigenValues the sortedEigenValues to set
	 */
	public void setSortedEigenValues(List<Double> sortedEigenValues) {
		this.sortedEigenValues = sortedEigenValues;
	}

	/**
	 * @return the sortedEigenVectors
	 */
	public IMatrix<Double> getSortedEigenVectors() {
		return sortedEigenVectors;
	}

	/**
	 * @param sortedEigenVectors the sortedEigenVectors to set
	 */
	public void setSortedEigenVectors(IMatrix<Double> sortedEigenVectors) {
		this.sortedEigenVectors = sortedEigenVectors;
	}

	/**
	 * @return the distanceOperator
	 */
	public DistanceMatrixOperation getDistanceOperator() {
		return distanceOperator;
	}

	/**
	 * @param distanceOperator the distanceOperator to set
	 */
	public void setDistanceOperator(DistanceMatrixOperation distanceOperator) {
		this.distanceOperator = distanceOperator;
	}

	/**
	 * @return the matchIndex
	 */
	public int getMatchIndex() {
		return matchIndex;
	}

	/**
	 * @param matchIndex the matchIndex to set
	 */
	public void setMatchIndex(int matchIndex) {
		this.matchIndex = matchIndex;
	}

	/**
	 * @return the matchDistance
	 */
	public double getMatchDistance() {
		return matchDistance;
	}

	/**
	 * @param matchDistance the matchDistance to set
	 */
	public void setMatchDistance(double matchDistance) {
		this.matchDistance = matchDistance;
	}

	/**
	 * @return the distanceTable
	 */
	public IMatrix<Double> getDistanceTable() {
		return distanceTable;
	}

	/**
	 * @param distanceTable the distanceTable to set
	 */
	public void setDistanceTable(IMatrix<Double> distanceTable) {
		this.distanceTable = distanceTable;
	}
}
