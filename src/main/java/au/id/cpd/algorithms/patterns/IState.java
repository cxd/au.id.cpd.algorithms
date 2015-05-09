package au.id.cpd.algorithms.patterns;

import java.util.List;

public interface IState<T, C> {

	public abstract T getData();

	public abstract void setData(T d);

	public abstract List<T> getDataCollection();

	public abstract void setDataCollection(List<T> c);

	/**
	 * @return the error
	 */
	public abstract boolean isError();

	/**
	 * @param error the error to set
	 */
	public abstract void setError(boolean error);

	/**
	 * @return the criteria
	 */
	public abstract List<C> getCriteria();

	/**
	 * @param criteria the criteria to set
	 */
	public abstract void setCriteria(List<C> criteria);

}