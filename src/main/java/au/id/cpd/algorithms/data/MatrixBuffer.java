/**
 * 
 */
package au.id.cpd.algorithms.data;

import java.lang.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.security.*;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;

import org.netlib.util.MatConv;
import sun.misc.*;

import org.netlib.blas.*;

import au.id.cpd.algorithms.algebra.ThreadMultiplierOperation;
import au.id.cpd.algorithms.data.io.*;

/**
 * @author cd
 * 
 */
public class MatrixBuffer<Number> extends AbstractMatrix<Number> {

	/**
	 * The row index.
	 */
	private int row;

	/**
	 * The column index.
	 */
	private int col;

	/**
	 * Lock over the memory resource in use.
	 */
	private volatile FileLock lock;

	/**
	 * Random access file.
	 */
	private volatile RandomAccessFile dataFile;

	/**
	 * The name of the file used in the map.
	 */
	private String fileName;

	/**
	 * Channel used to access the file.
	 */
	private volatile FileChannel channel;

	/**
	 * Mapped byte buffer from which we extract the DoubleBuffer views. Used
	 * primarily in writing.
	 */
	private volatile MappedByteBuffer map;

	/**
	 * Number of bytes in a double.
	 */
	private static int DOUBLE_SIZE = 8;

	/**
	 * Return a matrix of ones of dimension rows x cols
	 * 
	 * @param rows
	 * @param cols
	 * @return
	 */
	public static MatrixBuffer<Double> ones(int rows, int cols) {
		MatrixBuffer<Double> m = MatrixBuffer.CreateMatrixBuffer(rows, cols);
		m.fillValues(1.0);
		return m;
	}

	/**
	 * Return a matrix of zeroes of dimension rows x cols
	 * 
	 * @param rows
	 * @param cols
	 * @return
	 */
	public static MatrixBuffer<Double> zeros(int rows, int cols) {
		MatrixBuffer<Double> m = MatrixBuffer.CreateMatrixBuffer(rows, cols);
		m.fillValues(0.0);
		return m;
	}

	/**
	 * Construct a matrix with 1.0 in the diagonal. rows should be the same as
	 * cols.
	 * 
	 * @param rows
	 * @param cols
	 * @return
	 */
	public static MatrixBuffer<Double> identity(int rows, int cols) {
		MatrixBuffer<Double> m = MatrixBuffer.CreateMatrixBuffer(rows, cols);
		return null;
	}

	public static MatrixBuffer<Double> CreateMatrixBuffer() {
		Size sz = new Size(0, 0);
		return CreateMatrixBuffer(sz);
	}

	public static MatrixBuffer<Double> CreateMatrixBuffer(int rows, int cols) {
		Size sz = new Size(rows, cols);
		return CreateMatrixBuffer(sz);
	}

	public static MatrixBuffer<Double> CreateMatrixBuffer(Size size) {
		try {
			String macAlg = "HmacSHA1";
			SecureRandom sr = new SecureRandom();
			byte[] keyData = new byte[20];
			sr.nextBytes(keyData);
			SecretKey sk = new SecretKeySpec(keyData, macAlg);
			Mac mac = Mac.getInstance(macAlg);
			mac.init(sk);
			String className = MatrixBuffer.class.getCanonicalName();
			byte[] bytes = className.getBytes();
			byte[] result = mac.doFinal(bytes);
			String b64Encoded = new BASE64Encoder().encodeBuffer(result);
			String tmpdir = System.getProperty("java.io.tmpdir");
			String separator = System.getProperty("file.separator");
			b64Encoded = b64Encoded.replaceAll("\n", "");
			b64Encoded = b64Encoded.replaceAll(separator, "");
			String fileName = tmpdir + separator + b64Encoded + ".jmat";
			File f = new File(fileName);
			f.deleteOnExit(); // request that the temp file is removed when jvm terminates.
			MatrixBuffer<Double> mat = new MatrixBuffer<Double>(fileName, size);
			return mat;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param name
	 * @throws FileNotFoundException
	 */
	public MatrixBuffer(String name) throws FileNotFoundException, IOException {
		fileName = name;
		dataFile = new RandomAccessFile(name, "rw");
		init();
	}

	/**
	 * @param file
	 * @throws FileNotFoundException
	 */
	public MatrixBuffer(File file) throws FileNotFoundException, IOException {
		fileName = file.getAbsolutePath();
		dataFile = new RandomAccessFile(file, "rw");
		init();
	}

	/**
	 * @param name
	 * @param mode
	 * @throws FileNotFoundException
	 */
	public MatrixBuffer(String name, String mode) throws FileNotFoundException,
			IOException {
		fileName = name;
		dataFile = new RandomAccessFile(name, mode);
		init();
	}

	/**
	 * @param file
	 * @param mode
	 * @throws FileNotFoundException
	 */
	public MatrixBuffer(File file, String mode) throws FileNotFoundException,
			IOException {
		fileName = file.getAbsolutePath();
		dataFile = new RandomAccessFile(file, mode);
		init();
	}

	/**
	 * @param name
	 * @param rows
	 * @param cols
	 * @throws FileNotFoundException
	 */
	public MatrixBuffer(String name, int rows, int cols)
			throws FileNotFoundException, IOException {
		fileName = name;
		dataFile = new RandomAccessFile(name, "rw");
		this.setSize(new Size(rows, cols));
		init();
	}

	/**
	 * @param name
	 * @param size
	 * @throws FileNotFoundException
	 */
	public MatrixBuffer(String name, Size size) throws FileNotFoundException,
			IOException {
		fileName = name;
		dataFile = new RandomAccessFile(name, "rw");
		this.setSize(size);
		init();
	}

	/**
	 * @param file
	 * @param rows
	 * @param cols
	 * @throws FileNotFoundException
	 */
	public MatrixBuffer(File file, int rows, int cols)
			throws FileNotFoundException, IOException {
		fileName = file.getAbsolutePath();
		dataFile = new RandomAccessFile(file, "rw");
		this.setSize(new Size(rows, cols));
		init();
	}

	/**
	 * @param file
	 * @param size
	 * @throws FileNotFoundException
	 */
	public MatrixBuffer(File file, Size size) throws FileNotFoundException,
			IOException {
		fileName = file.getAbsolutePath();
		dataFile = new RandomAccessFile(file, "rw");
		this.setSize(size);
		init();
	}

	/**
	 * @param name
	 * @param mode
	 * @throws FileNotFoundException
	 */
	public MatrixBuffer(String name, String mode, int rows, int cols)
			throws FileNotFoundException, IOException {
		fileName = name;
		dataFile = new RandomAccessFile(name, mode);
		this.setSize(new Size(rows, cols));
		init();
	}

	/**
	 * @param name
	 * @param mode
	 * @throws FileNotFoundException
	 */
	public MatrixBuffer(String name, String mode, Size size)
			throws FileNotFoundException, IOException {
		fileName = name;
		dataFile = new RandomAccessFile(name, mode);
		this.setSize(size);
		init();
	}

	/**
	 * @param file
	 * @param mode
	 * @throws FileNotFoundException
	 */
	public MatrixBuffer(File file, String mode, int rows, int cols)
			throws FileNotFoundException, IOException {
		fileName = file.getAbsolutePath();
		dataFile = new RandomAccessFile(file, mode);
		this.setSize(new Size(rows, cols));
		init();
	}

	/**
	 * @param file
	 * @param mode
	 * @throws FileNotFoundException
	 */
	public MatrixBuffer(File file, String mode, Size size)
			throws FileNotFoundException, IOException {
		fileName = file.getAbsolutePath();
		dataFile = new RandomAccessFile(file, mode);
		this.setSize(size);
		init();
	}

	public void finalize() {
		try {
			if (lock.isValid()) {
				lock.release();
			}
			if (channel.isOpen()) {
				channel.close();
			}
			if (dataFile != null) {
				dataFile.close();
			}
			File f = new File(fileName);
			// remove the file if it is a temporary file.
			String tmpdir = System.getProperty("java.io.tmpdir");
			if (f.getAbsolutePath().indexOf(tmpdir) >= 0) {
				f.delete();
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Initialize internal members.
	 * 
	 */
	private void init() throws IOException {
		if (getSize() == null) {
			setSize(new Size(0, 0));
			try {
				String sizeFile = fileName + ".size";
				File sFile = new File(sizeFile);
				if (sFile.exists()) {
					FileReader fin = new FileReader(sFile);
					MatrixReader reader = new MatrixReader(fin);
					IMatrix<Double> sMat = reader.readMatrix();
					reader.close();
					if (sMat.size() > 0) {
						int r = sMat.get(0, 0).intValue();
						int c = sMat.get(0, 1).intValue();
						this.setSize(new Size(r, c));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		channel = dataFile.getChannel();
		row = -1;
		col = -1;
	}

	/**
	 * Calculate the index for two dimensions mapped onto a one dimensional
	 * array.
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	private int index(int i, int j) {
		return i * this.getSize().getCols() + j;
	}

	/**
	 * Check if the index is out of bounds.
	 * 
	 * @param idx
	 * @return
	 */
	private boolean isOutOfBounds(int idx) {
		if ((idx < 0)
				|| (idx >= this.getSize().getCols() * this.getSize().getRows()))
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public  IMatrix<Double> clone() {
		try {
			IMatrix<Double> copy = MatrixBuffer.CreateMatrixBuffer(getSize());
			for (int i = 0; i < getSize().getRows(); i++) {
				List<Number> list = getRow(i);
				if (list == null)
					return null;
				for (int j = 0; j < getSize().getCols(); j++) {
					copy.set(i, j, list.get(j));
				}
			}
			return copy;
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * Lock the file.
	 * 
	 * @throws IOException
	 */
	private void lock() throws IOException {
		if (lock == null)
			lock = channel.lock();
		if (!lock.isValid())
			lock = channel.lock();
	}

	/**
	 * Close the memory mapped resource.
	 * 
	 * 
	 */
	public void close() {
		try {
			if (channel != null)
				channel.close();
			if (dataFile != null)
				dataFile.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save the matrix to the supplied file.
	 * @param file
	 * @return
	 */
	public boolean save(String file) {
		return copyFileTo(file);
	}
	
	/**
	 * Copy the mapped memory file to the supplied destination.
	 * 
	 * @param dest
	 */
	public  boolean copyFileTo(String dest) {
		try {
			File f = new File(dest);
			if (f.exists()) {
				f.delete();
			}
			f = null;
			lock();
			RandomAccessFile fos = new RandomAccessFile(dest, "rw");
			FileChannel outChannel = fos.getChannel();
			FileLock outLock = outChannel.lock();
			int m = getSize().getRows();
			int n = getSize().getCols();
			for (int i = 0; i < m; i++) {
				byte[] rowData = new byte[n * DOUBLE_SIZE];
				int position = index(i, 0) * DOUBLE_SIZE;
				int len = n * DOUBLE_SIZE;
				
				ByteBuffer inBuf = ByteBuffer.allocate(len);
				channel.read(inBuf, position);
				inBuf.rewind();
				inBuf.position(0);
				rowData = inBuf.array();
				
				ByteBuffer outBuf = ByteBuffer.allocateDirect(rowData.length);
				outBuf.put(rowData);
				outBuf.rewind();
				outBuf.position(0);
				
				outChannel.write(outBuf);
				outBuf.clear();
				outBuf = null;
			}
			outLock.release();
			//outChannel.force(true);
			outChannel.close();
			fos.close();
			lock.release();
			String sizeFile = dest + ".size";
			IMatrix<Double> sMat = new Matrix<Double>(1, 2);
			sMat.add(this.getSize().getRows());
			sMat.add(this.getSize().getCols());
			FileWriter fout = new FileWriter(new File(sizeFile));
			MatrixWriter writer = new MatrixWriter(fout);
			writer.writeMatrix(sMat, fout);
			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Add an item to the collection. This method works best if the Size is
	 * predefined on the Matrix.
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#add(java.lang.Object)
	 */
	public  boolean add(Object o) {
		boolean flag = false;
		int m = getSize().getRows();
		int n = getSize().getCols();
		if (row + 1 >= m) {
			resize(new Size(m + 1, n + 1));
			flag = true;
		}
		try {
			if (flag) {
				++row;
				col = 0;
			} else if (col + 1 >= n) {
				++row;
				col = 0;
			} else {
				++col;
			}
			int position = index(row, col) * DOUBLE_SIZE;
			channel.position(position);
			ByteBuffer outBuf = ByteBuffer.allocateDirect(DOUBLE_SIZE);
			outBuf.asDoubleBuffer().put((Double)o);
			channel.write(outBuf);
			//channel.force(true);
			outBuf.clear();
			outBuf = null;
			lock.release();
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#add(int, int, java.lang.Object)
	 */
	public  boolean add(int row, int col, Object item) {
		// m x n matrix
		int m = getSize().getRows();
		int n = getSize().getCols();
		if ((row >= m) && (col >= n)) {
			resize(new Size(m + 1, n + 1));
		} else if (row >= m) {
			resize(new Size(m + 1, n));
		} else if (col >= n) {
			resize(new Size(m, n + 1));
		}
		try {
			this.row = row;
			this.col = col;
			int position = index(row, col) * DOUBLE_SIZE;
			lock();
			channel.position(position);
			ByteBuffer outBuf = ByteBuffer.allocateDirect(DOUBLE_SIZE);
			outBuf.asDoubleBuffer().put((Double)item);
			channel.write(outBuf);
			outBuf.clear();
			outBuf = null;
			//channel.force(true);
			lock.release();
			return true;
		} catch (Exception e) {

		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#addAll(java.util.Collection)
	 */
	public  boolean addAll(Collection c) {
		for (Object o : c) {
			if (!this.add(o))
				return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#clear()
	 */
	public  void clear() {
		// can we remove data from the file?

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#columnContains(int, Number)
	 */
	public  boolean columnContains(int col, Number o) {
		if ((getSize().getRows() == 0) || (getSize().getCols() == 0))
			return false;
		if (col >= getSize().getCols())
			return false;
		for (int i = 0; i < getSize().getRows(); i++) {
			Double n = get(i, col);
			if (n == null)
				return false;
			if (n.equals(o))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#columnContainsAll(int,
	 *      java.util.Collection)
	 */
	public  boolean columnContainsAll(int col, Collection c) {
		if ((getSize().getRows() == 0) || (getSize().getCols() == 0))
			return false;
		if (col >= getSize().getCols())
			return false;
		List<Number> list = this.getColumn(col);
		Object[] arr = list.toArray();
		Arrays.sort(arr);
		for (Object o : c) {
			if (Arrays.binarySearch(arr, o) < 0)
				return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#columnMeans()
	 */
	public  IMatrix<Double> columnMeans() {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		// normalise column by column.
		IMatrix<Double> means = MatrixBuffer.CreateMatrixBuffer(1, cols);
		return columnMeans(means);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#contains(java.lang.Object)
	 */
	public  boolean contains(Object o) {
		for (Object obj : this) {
			if (obj == null)
				return false;
			if (obj.equals(o))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#containsAll(java.util.Collection)
	 */
	public  boolean containsAll(Collection c) {
		for (Object o : c) {
			if (!contains(o))
				return false;
		}
		return true;
	}

	/**
	 * This method will retrieve all values from the buffer into the heap. It is
	 * not recommended for use as it defeats the purpose of using a file to
	 * store large matrices. It is likely that we would run out of memory when
	 * trying to retrieve the entire matrix. Use the getCol and getRow methods
	 * to retrieve segments of the data set into memory.
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#convertToDoubles()
	 */
	public  double[][] convertToDoubles() {
		try {
			int m = getSize().getRows();
			int n = getSize().getCols();
			double[][] result = new double[m][n];
			lock();
			for (int i = 0; i < m; i++) {
				int position = index(i, 0) * DOUBLE_SIZE;
				int len = index(i, n) * DOUBLE_SIZE - position;
				ByteBuffer outBuf = ByteBuffer.allocateDirect(len);
				channel.position(position);
				channel.read(outBuf);
				double[] rowData = new double[n];
				outBuf.rewind();
				outBuf.position(0);
				outBuf.asDoubleBuffer().get(rowData, 0, n);
				result[i] = rowData;
				outBuf.clear();
				outBuf = null;
			}
			lock.release();
			return result;
		} catch(Exception e) {
			
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#cos()
	 */
	public  IMatrix<Number> cos() {
		IMatrix<Number> result = (IMatrix<Number>) MatrixBuffer
				.CreateMatrixBuffer(getSize());
		return cos(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#divide(java.lang.Double)
	 */
	public IMatrix<Number> divide(Double m) {
		IMatrix<Number> result = (IMatrix<Number>) CreateMatrixBuffer(this
				.getSize().getRows(), this.getSize().getCols());
		return divide(m, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#divisorOf(java.lang.Double)
	 */
	public  IMatrix divisorOf(Double m) {
		IMatrix<Number> result = (IMatrix<Number>) CreateMatrixBuffer(this
				.getSize().getRows(), this.getSize().getCols());
		return divisorOf(m, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#exp()
	 */
	public  IMatrix exp() {
		IMatrix<Number> result = (IMatrix<Number>) CreateMatrixBuffer(this
				.getSize().getRows(), this.getSize().getCols());
		return exp(result);
	}

	private  void fillValues() {
		for (int i = 0; i < this.getSize().getRows(); i++) {
			for (int j = 0; j < this.getSize().getCols(); j++) {
				this.set(i, j, null);
			}

		}
	}

	private  void fillValues(int rowOffset, int colOffset) {
		for (int i = rowOffset; i < this.getSize().getRows(); i++) {
			for (int j = colOffset; j < this.getSize().getCols(); j++) {
				this.set(i, j, null);
			}
		}
	}

	/**
	 * Fill the matrix of dimension rows x cols with supplied value.
	 * 
	 * @param val
	 */
	private  void fillValues(double val) {
		try {
			int m = getSize().getRows();
			int n = getSize().getCols();
			lock();
			for (int i = 0; i < m; i++) {
				int position = index(i, 0) * DOUBLE_SIZE;
				int len = index(i, n) * DOUBLE_SIZE
						- position;
				ByteBuffer outBuf = ByteBuffer.allocateDirect(len);
				double[] rowData = new double[n];
				for (int j = 0; j < n; j++) {
					rowData[j] = val;
				}
				outBuf.asDoubleBuffer().put(rowData);
				channel.position(position);
				channel.write(outBuf);
				outBuf.clear();
				outBuf = null;
			}
			//channel.force(true);
			lock.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#get(int, int)
	 */
	public Double get(int r, int c) {
		
		int m = getSize().getRows();
		int n = getSize().getCols();
		
		if ((r >= 0) && (r < m) && (c >= 0)
				&& (c < n)) {
			try {
				lock();
				int position = index(r, c) * DOUBLE_SIZE;
				ByteBuffer dst = ByteBuffer.allocateDirect(DOUBLE_SIZE);
				int read = channel.read(dst, position);
				dst.rewind();
				dst.position(0);
				Double result = dst.asDoubleBuffer().get();
				dst.clear();
				dst = null;
				lock.release();
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#getColumn(int)
	 */
	public  List<Number> getColumn(int col) {
		
		int m = getSize().getRows();
		int n = getSize().getCols();
		
		if ((col < 0) || (col >= n))
			return null;
		List<Number> list = new ArrayList<Number>();
		for (int i = 0; i < m; i++) {
			Number k = (Number) this.get(i, col);
			if (k == null)
				return null;
			list.add(k);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#getRow(int)
	 */
	public  List<Number> getRow(int row) {
		
		int m = getSize().getRows();
		int n = getSize().getCols();
		
		if ((row < 0) || (row >= m))
			return null;
		try {
			lock();
			int position = index(row, 0) * DOUBLE_SIZE;
			int len = index(row, n) * DOUBLE_SIZE - position;
			
			double[] rowData = new double[getSize().getCols()];
			
			ByteBuffer outBuf = ByteBuffer.allocateDirect(len);
			
			channel.read(outBuf, position);
			outBuf.rewind();
			outBuf.position(0);
			outBuf.asDoubleBuffer().get(rowData, 0, n);
			outBuf.clear();
			outBuf = null;
			
			List<Number> list = new ArrayList<Number>();
			for (double d : rowData) {
				Double k = new Double(d);
				list.add((Number) k);
			}
			lock.release();
			return list;
		} catch (Exception e) {

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#isEmpty()
	 */
	public  boolean isEmpty() {
		// TODO Auto-generated method stub
		return (size() == 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#max()
	 */
	public  double max() {
		
		double max = Double.MIN_VALUE;
		int m = getSize().getRows();
		int n = getSize().getCols();
		
		try {
			lock();
			for (int i = 0; i < m; i++) {
				int position = index(i, 0) * DOUBLE_SIZE;
				int len = index(i, n) * DOUBLE_SIZE - position;
				double[] rowData = new double[n];
				
				ByteBuffer outBuf = ByteBuffer.allocateDirect(len);
				channel.read(outBuf, position);
				outBuf.rewind();
				outBuf.position(0);
				outBuf.asDoubleBuffer().get(rowData, 0, n);
				outBuf.clear();
				outBuf = null;
				for (int j = 0; j < n; j++) {
					if (rowData[j] > max) {
						max = rowData[j];
					}
				}
				lock.release();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return max;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#meanNormalise()
	 */
	public  IMatrix<Double> meanNormalise() {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		// normalise column by column.
		IMatrix<Double> normal = CreateMatrixBuffer(rows, cols);
		return meanNormalise(normal);
	}
	
	/* (non-Javadoc)
	 * @see au.id.cpd.algorithms.data.IMatrix#minMaxNormalise()
	 */
	@Override
	public  IMatrix<Double> minMaxNormalise(IMatrix<Double> normal) {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		// normalise column by column.
		double max = this.max();
		double min = this.min();
		double delta = max - min;
		try {
			lock();
			((MatrixBuffer) normal).lock();
			for(int j=0;j<rows;j++) {
				int position = index(j, 0) * DOUBLE_SIZE;
				int len = index(j, cols) * DOUBLE_SIZE
						- position;
				
				ByteBuffer outBuf = ByteBuffer.allocateDirect(len);
				double[] rowData = new double[cols];
				channel.read(outBuf, position);
				outBuf.rewind();
				outBuf.position(0);
				outBuf.asDoubleBuffer().get(rowData, 0, cols);
				outBuf.clear();
				outBuf = null;
				
				ByteBuffer nBuf = ByteBuffer.allocateDirect(len);
				
				double[] nRowData = new double[normal.getSize().getCols()];
				
				for(int k=0;k<cols;k++) {
					double val = rowData[k];
					if ( (val != 0.0) && (delta != 0.0) ) {
						nRowData[k] = (val - min) / delta;
					} else {
						nRowData[k] = val;
					}
				}
				nBuf.asDoubleBuffer().put(nRowData);
				((MatrixBuffer) normal).getChannel().write(nBuf);
				nBuf.clear();
				nBuf = null;
			}
			lock.release();
			//((MatrixBuffer) normal).getChannel().force(true);
			((MatrixBuffer) normal).lock.release();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return normal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#minMaxNormalise()
	 */
	public  IMatrix<Double> minMaxNormalise() {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		// normalise column by column.
		IMatrix<Double> normal = CreateMatrixBuffer(rows, cols);
		return minMaxNormalise(normal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#multiply(au.id.cpd.algorithms.data.IMatrix)
	 */
	public  IMatrix<Number> multiply(IMatrix matrix) {
		IMatrix<Number> result = (IMatrix<Number>) MatrixBuffer
				.CreateMatrixBuffer(this.getSize().getRows(), matrix.getSize()
						.getCols());
		if (result.size() < 1000) {
			return multiply(matrix, (MatrixBuffer<Number>)result);
		} else {
			ThreadMultiplierOperation op = new ThreadMultiplierOperation();
			op.operate((IMatrix<Double>)this, (IMatrix<Double>)matrix, (IMatrix<Double>)result);
			return result;
			//return blasMultiply(matrix, (MatrixBuffer<Number>)result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#multiply(java.lang.Double)
	 */
	public  IMatrix<Number> multiply(Double m) {
		IMatrix<Number> result = (IMatrix<Number>) MatrixBuffer
				.CreateMatrixBuffer(this.getSize().getRows(), this.getSize()
						.getCols());
		return multiply(m, (MatrixBuffer<Number>)result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#multiply(au.id.cpd.algorithms.data.IMatrix)
	 */
	public  IMatrix<Number> multiply(IMatrix<Number> matrix,
			MatrixBuffer<Number> result) {
		
		int m = getSize().getRows();
		int n = getSize().getCols();
		
		// unique case where 1x1 matrix - instead of scalar.
		if ((m == 1) && (n == 1))
			return matrix.multiply(this.get(0, 0).doubleValue());
		else if ((matrix.getSize().getCols() == 1)
				&& (matrix.getSize().getRows() == 1))
			return this.multiply(matrix.get(0, 0).doubleValue());
		// general case
		int rows = matrix.getSize().getRows();
		int cols = n;
		if (rows != cols)
			return null;

		Double d = new Double(0);

		try {
			lock();
			result.lock();
			
			ByteBuffer rOutBuf = ByteBuffer.allocate(DOUBLE_SIZE);
			
			for (int j = 0; j < matrix.getSize().getCols(); j++) {
				for (int i = 0; i < m; i++) {

					int position = index(i, 0) * DOUBLE_SIZE;
					int len = index(i, n) * DOUBLE_SIZE
							- position;
					
					ByteBuffer outBuf = ByteBuffer.allocateDirect(len);
					double[] rowData = new double[n];
					channel.read(outBuf, position);
					outBuf.rewind();
					outBuf.position(0);
					// as double buffer places position and limit to 0.
					outBuf.asDoubleBuffer().get(rowData, 0, n);
					outBuf.clear();
					outBuf = null;
					
					for (int k = 0; k < matrix.getSize().getRows(); k++) {
						// Acols == Brows
						// sum A(i,k)*B(k,j)
						d += rowData[k] * matrix.get(k, j).doubleValue();
					}
					
					int rPosition = index(i, j) * DOUBLE_SIZE;
					if (position < 0) {
						throw new Exception("Negative Position");
					}
					
					rOutBuf.asDoubleBuffer().put((Double)d);
					result.getChannel().position(rPosition);
					result.getChannel().write(rOutBuf);
					rOutBuf.clear();
					
					d = new Double(0);
				}
			}
			rOutBuf.clear();
			rOutBuf = null;
			//result.getChannel().force(true);
			result.lock.release();
			lock.release();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * copied from blas_simple
	 * @param var0
	 * @param var1
	 * @param var2
	 * @param var3
	 * @param var4
	 * @param var5
	 * @param var7
	 * @param var8
	 * @param var9
	 * @param var11
	 */
	public static void DGEMM(String var0, String var1, int var2, int var3, int var4, double var5, double[][] var7, double[][] var8, double var9, double[][] var11) {
		double[] var12 = MatConv.doubleTwoDtoOneD(var7);
		double[] var13 = MatConv.doubleTwoDtoOneD(var8);
		double[] var14 = MatConv.doubleTwoDtoOneD(var11);
		Dgemm.dgemm(var0, var1, var2, var3, var4, var5, var12, 0, var7.length, var13, 0, var8.length, var9, var14, 0, var11.length);
		MatConv.copyOneDintoTwoD(var7, var12);
		MatConv.copyOneDintoTwoD(var8, var13);
		MatConv.copyOneDintoTwoD(var11, var14);
	}
	
	/**
	 * Multiply using the blas methods for fast matrix multiplication.
	 * @return
	 */
	public  IMatrix<Number> blasMultiply(IMatrix<Number> matrix, MatrixBuffer<Number> result) {
		double[][] A = this.convertToDoubles();
		double[][] B = matrix.convertToDoubles();
		double[][] C = new double[result.getSize().getRows()][result.getSize().getCols()];
		if ( (A == null) || (B == null) ) return null;

		DGEMM("N",
				"N", 
				getSize().getRows(), 
				matrix.getSize().getCols(), 
				getSize().getCols(), 
				1.0, 
				A,
				B,
				1.0,
				C);
		A = null;
		B = null;
		
		try {
			for(int i=0; i < result.getSize().getRows();i++) {
				int position = index(i, 0) * DOUBLE_SIZE;
				int len = index(i, result.getSize().getCols()) * DOUBLE_SIZE - position;
				
				MappedByteBuffer nMap = ((MatrixBuffer) result)
				.getChannel().map(FileChannel.MapMode.READ_WRITE,
						position, len);
				
				DoubleBuffer nDblBuf = nMap.asDoubleBuffer();
				nDblBuf.put(C[i]);
				nMap.clear();
				nMap = null;
				// free memory a row at a time.
				C[i] = null;
			}
			System.err.println("blas completed.");
			//result.getChannel().force(true);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Multiply dot product
	 * of row vector at row
	 * by column vector at matrix column.
	 * Row length must be equal to column length
	 * @param matrix
	 * @return
	 */
	public double ddot(int row, IMatrix<Number> matrix, int col) {
		int m = getSize().getRows();
		int n = getSize().getCols();
		int position = index(row, 0) * DOUBLE_SIZE;
		int len = index(row, n) * DOUBLE_SIZE
				- position;
		
		try {
			ByteBuffer outBuf = ByteBuffer.allocateDirect(len);
			double[] rowData = new double[n];
			channel.read(outBuf, position);
			outBuf.rewind();
			outBuf.position(0);
			// as double buffer places position and limit to 0.
			outBuf.asDoubleBuffer().get(rowData, 0, n);
			outBuf.clear();
			outBuf = null;
			
			double result = 0.0;
			for(int k=0;k<rowData.length;k++) {
				result = result + rowData[k]*matrix.get(k,col).doubleValue();	
			}
			return result;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return Double.NaN;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#normalise()
	 */
	public  IMatrix<Double> normalise() {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		// normalise column by column.
		IMatrix<Double> normal = CreateMatrixBuffer(rows, cols);
		return normalise(normal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#normalise()
	 */
	@Override
	public  IMatrix<Double> normalise(IMatrix<Double> normal) {
		int rows = this.getSize().getRows();
		int cols = this.getSize().getCols();
		// normalise column by column.
		List<Double> means = new Vector<Double>();
		List<Double> std = new Vector<Double>();
		// calculate the mean and standard deviation for each column
		for (int k = 0; k < cols; k++) {
			double sum = 0.0;
			double max = -1 * Double.MIN_VALUE;
			double min = Double.MAX_VALUE;
			for (int j = 0; j < rows; j++) {
				double val = (this.get(j, k) != null) ? this.get(j, k)
						.doubleValue() : 0;
				if (val < min) {
					min = val;
				}
				if (val > max) {
					max = val;
				}
				sum += val;
			}
			if (sum != 0) {
				means.add(sum / rows);
			} else {
				means.add(0.0);
			}
			std.add(max - min);
		}
		try {

			lock();
			((MatrixBuffer) normal).lock();

			for (int j = 0; j < rows; j++) {
				int position = index(j, 0) * DOUBLE_SIZE;
				int len = index(j, cols) * DOUBLE_SIZE
						- position;
				
				ByteBuffer outBuf = ByteBuffer.allocateDirect(len);
				double[] rowData = new double[cols];
				
				channel.read(outBuf, position);
				outBuf.rewind();
				outBuf.position(0);
				outBuf.asDoubleBuffer().get(rowData, 0, cols);
				outBuf.clear();
				outBuf = null;
				
				((MatrixBuffer) normal).getChannel().position(position);
				ByteBuffer nOutBuf = ByteBuffer.allocateDirect(len);
				double[] nRowData = new double[normal.getSize().getCols()];
				
				for (int k = 0; k < cols; k++) {
					double val = rowData[k];
					if (std.get(k) != 0) {
						nRowData[k] = (val - means.get(k)) / std.get(k);
					} else {
						nRowData[k] = val - means.get(k);
					}
				}
				
				nOutBuf.asDoubleBuffer().put(nRowData);
				((MatrixBuffer) normal).getChannel().write(nOutBuf);
				nOutBuf.clear();
				nOutBuf = null;
			}
			lock.release();
			//((MatrixBuffer) normal).getChannel().force(true);
			((MatrixBuffer) normal).getLock().release();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return normal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#operate(au.id.cpd.algorithms.data.IMatrixOperation)
	 */
	public  IMatrix<Number> operate(IMatrixOperation op) {
		IMatrix<Number> result = (IMatrix<Number>) CreateMatrixBuffer(this
				.getSize().getRows(), this.getSize().getCols());
		return operate(op, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#pointwiseDivide(au.id.cpd.algorithms.data.IMatrix)
	 */
	public  IMatrix<Number> pointwiseDivide(IMatrix matrix) {
		IMatrix<Number> result = (IMatrix<Number>) CreateMatrixBuffer(this
				.getSize().getRows(), this.getSize().getCols());
		return pointwiseDivide(matrix, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#pointwiseMultiply(au.id.cpd.algorithms.data.IMatrix)
	 */
	public  IMatrix<Number> pointwiseMultiply(IMatrix matrix) {
		IMatrix<Number> result = (IMatrix<Number>) MatrixBuffer
				.CreateMatrixBuffer(this.getSize().getRows(), this.getSize()
						.getCols());
		return pointwiseMultiply(matrix, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#power(java.lang.Double)
	 */
	public  IMatrix<Number> power(Double p) {
		IMatrix<Number> result = (IMatrix<Number>) CreateMatrixBuffer(this
				.getSize().getRows(), this.getSize().getCols());
		return power(p, result);
	}

	
	private  void read(FileChannel chan, ByteBuffer buf) throws IOException {
		buf.rewind();
		while(chan.read(buf) != -1) {
			// buffer is being populated by read.
		}
	}
	
	public IMatrix<Double> repmat(int x, int y) {
		int rows = getSize().getRows();
		int cols = getSize().getCols();
		int newRows = rows*x;
		int newCols = cols*y;
		IMatrix<Double> m = MatrixBuffer.CreateMatrixBuffer(newRows, newCols);
		return repmat(x, y, m);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#reshape(int, int)
	 */
	public  IMatrix reshape(int rows, int cols) {
		IMatrix<Double> m = MatrixBuffer.CreateMatrixBuffer(rows, cols);
		return reshape(rows, cols, m);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#resize(au.id.cpd.algorithms.data.Size)
	 */
	public  void resize(Size s) {
		if (this.getSize().compareTo(s) > 0)
			this.reduce(s);
		else if (this.getSize().compareTo(s) < 0)
			this.increase(s);
		this.setSize(s);
	}

	private void reduce(Size s) {
		// do we need to do anything with the mapped memory?
	}

	private void increase(Size s) {
		// do we need to do anything with the mapped memory?
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#rowContains(int, java.lang.Object)
	 * 
	 * 
	 */
	public  boolean rowContains(int row, Number o) {
		if ((getSize().getRows() == 0) || (getSize().getCols() == 0))
			return false;
		if (row >= getSize().getRows())
			return false;
		for (int j = 0; j < getSize().getCols(); j++) {
			Double n = this.get(row, j);
			if (n == null)
				return false;
			if (n.equals(o))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#rowContainsAll(int,
	 *      java.util.Collection)
	 */
	public  boolean rowContainsAll(int row, Collection c) {
		List<Number> curRow = this.getRow(row);
		if (curRow == null)
			return false;
		Object[] data = curRow.toArray();
		Arrays.sort(data);
		for (Object o : c) {
			if (Arrays.binarySearch(data, o) < 0)
				return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#rowsMeans()
	 */
	public  IMatrix rowsMeans() {
		int rows = this.getSize().getRows();
		IMatrix<Double> means = CreateMatrixBuffer(rows, 1);
		return rowsMeans(means);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#set(int, int, java.lang.Object)
	 */
	public void set(int r, int c, Object item) {
		if ((r >= 0) && (r < getSize().getRows()) && (c >= 0)
				&& (c < getSize().getCols())) {
			try {
				lock();
				int position = index(r, c) * DOUBLE_SIZE;
				if (position < 0) {
					throw new Exception("Negative Position");
				}
				ByteBuffer dst = ByteBuffer.allocateDirect(DOUBLE_SIZE);
				dst.asDoubleBuffer().put((Double)item);
				channel.write(dst, position);
				dst.clear();
				dst = null;
				dst = null;
				//channel.force(true);
				lock.release();
			} catch (Exception e) {
				System.err.println("Cannot write: " + r + ", " + c + " = " + item);
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#setColumn(int, java.util.List)
	 */
	public  void setColumn(int col, List<Number> colValues) {
		if ((col < 0) || (col > this.getSize().getCols())
				|| (colValues.size() != this.getSize().getRows()))
			return;
		for (int i = 0; i < this.getSize().getRows(); i++) {
			this.set(i, col, colValues.get(i));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#setRow(int, java.util.List)
	 */
	public  void setRow(int row, List<Number> rowValues) {
		if ((row < 0) || (row > this.getSize().getRows())
				|| (rowValues.size() != this.getSize().getCols()))
			return;
		for (int j = 0; j < this.getSize().getCols(); j++) {
			this.set(row, j, rowValues.get(j));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#shuffle()
	 */
	public  IMatrix shuffle() {
		IMatrix<Double> shuffled = CreateMatrixBuffer(this.getSize().getRows(),
				this.getSize().getCols());
		return shuffle(shuffled);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#sin()
	 */
	public  IMatrix sin() {
		IMatrix<Number> result = (IMatrix<Number>) CreateMatrixBuffer(this
				.getSize().getRows(), this.getSize().getCols());
		return sin(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#subtract(au.id.cpd.algorithms.data.IMatrix)
	 */
	public  IMatrix subtract(IMatrix matrix) {
		IMatrix<Number> result = (IMatrix<Number>) CreateMatrixBuffer(this
				.getSize());
		return subtract(matrix, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#subtract(java.lang.Double)
	 */
	public  IMatrix subtract(Double b) {
		IMatrix<Number> result = (IMatrix<Number>) CreateMatrixBuffer(this
				.getSize());
		return subtract(b, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#subtractFrom(java.lang.Double)
	 */
	public  IMatrix subtractFrom(Double b) {
		IMatrix<Number> result = (IMatrix<Number>) CreateMatrixBuffer(this
				.getSize());
		return subtractFrom(b, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#sum(java.lang.Double)
	 */
	public  IMatrix<Number> sum(Double b) {
		IMatrix<Number> result = (IMatrix<Number>) CreateMatrixBuffer(this
				.getSize());
		return sum(b, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#sum(au.id.cpd.algorithms.data.IMatrix)
	 */
	public  IMatrix<Number> sum(IMatrix<Number> matrix) {
		IMatrix<Number> result = (IMatrix<Number>) CreateMatrixBuffer(this
				.getSize());
		return sum(matrix, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#sumColumn(int)
	 */
	public  Double sumColumn(int col) {
		Double n = 0.0;
		if (this.getSize().getRows() == 0)
			return n;
		if (col > this.getSize().getCols())
			return n;
		for (int i = 0; i < this.getSize().getRows(); i++) {
			n += (Double) this.get(i, col);
		}
		return n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#sumRow(int)
	 */
	public  Double sumRow(int row) {
		Double n = 0.0;
		if (row >= this.getSize().getRows())
			return n;
		for (int j = 0; j < this.getSize().getCols(); j++) {
			n += (Double) this.get(row, j);
		}
		return n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#tanh()
	 */
	public  IMatrix tanh() {
		IMatrix<Number> result = (IMatrix<Number>) CreateMatrixBuffer(this
				.getSize().getRows(), this.getSize().getCols());
		return tanh(result);
	}

	/**
	 * This could potentially be very large. Not much point writing a large
	 * matrix to string unless absolutely required. (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#toString()
	 */
	public  String toString() {
		String str = "";
		for (int i = 0; i < this.getSize().getRows(); i++) {
			String row = "";
			for (int j = 0; j < this.getSize().getCols(); j++) {
				row += this.get(i, j);
				if (j < this.getSize().getCols() - 1)
					row += ",";
			}
			row += System.getProperty("line.separator");
			str += row;
		}
		return str;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.id.cpd.algorithms.data.IMatrix#transform()
	 */
	public  IMatrix<Number> transform() {
		IMatrix<Number> matrix = (IMatrix<Number>) MatrixBuffer
				.CreateMatrixBuffer(this.getSize().getCols(), this.getSize()
						.getRows());
		return transform(matrix);
	}

	/**
	 * java.io.Serializable.readObject(ObjectInputStream is)
	 */
	private void readObject(java.io.ObjectInputStream is)
			throws ClassNotFoundException, java.io.IOException {
		is.defaultReadObject();
	}

	/**
	 * java.io.Serializable.writeObject(ObjectOutputStream os)
	 */
	private void writeObject(java.io.ObjectOutputStream os)
			throws ClassNotFoundException, java.io.IOException {
		os.defaultWriteObject();
	}

	/**
	 * @return the channel
	 */
	public FileChannel getChannel() {
		return channel;
	}

	/**
	 * @param channel
	 *            the channel to set
	 */
	public void setChannel(FileChannel channel) {
		this.channel = channel;
	}

	/**
	 * @return the dataFile
	 */
	public RandomAccessFile getDataFile() {
		return dataFile;
	}

	/**
	 * @param dataFile
	 *            the dataFile to set
	 */
	public void setDataFile(RandomAccessFile dataFile) {
		this.dataFile = dataFile;
	}

	/**
	 * @return the lock
	 */
	public FileLock getLock() {
		return lock;
	}

	/**
	 * @param lock
	 *            the lock to set
	 */
	public void setLock(FileLock lock) {
		this.lock = lock;
	}

	/**
	 * @return the map
	 */
	public MappedByteBuffer getMap() {
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(MappedByteBuffer map) {
		this.map = map;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
