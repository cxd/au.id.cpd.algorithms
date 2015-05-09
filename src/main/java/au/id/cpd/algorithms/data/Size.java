/**
 * 
 */
package au.id.cpd.algorithms.data;

/**
 * @author Chris Davey cd@cpd.id.au
 *
 */
public class Size implements java.lang.Comparable, java.io.Serializable {
	/**
	 * serial version id
	 */
	static final long serialVersionUID = 1079926595466194804L;

	/**
	 * @serial int width
	 */
	private int width;
	/**
	 * @serial int height
	 */
	private int height;
	/**
	 * default constructor
	 *
	 */
	public Size() {
		this.width=0;
		this.height=0;
	}
	
	public Size(Size s) {
		this.width = s.getWidth();
		this.height = s.getHeight();
	}
	
	public Size(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getRows() {
		return this.width;
	}
	public void setRows(int r) {
		this.width = r;
	}
	public int getCols() {
		return this.height;
	}
	public void setCols(int c) {
		this.height = c;
	}
	
	public void setSize(Size s) {
		this.width = s.getWidth();
		this.height = s.getHeight();
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public boolean equals(Object o) {
		return ((((Size)o).getWidth() == this.width)&&
				(((Size)o).getHeight() == this.height));
	}
	
	public int compareTo(Object o) {
		Size s = (Size)o;
		if ((this.width < s.width)||(this.height < s.height)) {
			return -1;
		}
		if ((this.width > s.width)||(this.height > s.height)) {
			return 1;
		}
		return 0;
	}
	
	public String toString() {
		return ""+this.width+","+this.height;
	}
	
	/**
	 * java.io.Serializable.readObject(ObjectInputStream is)
	 */
	private void readObject(java.io.ObjectInputStream is) throws ClassNotFoundException, java.io.IOException {
		is.defaultReadObject();
	}
	/**
	 * java.io.Serializable.writeObject(ObjectOutputStream os)
	 */
	private void writeObject(java.io.ObjectOutputStream os) throws ClassNotFoundException, java.io.IOException {
		os.defaultWriteObject();
	}
}
