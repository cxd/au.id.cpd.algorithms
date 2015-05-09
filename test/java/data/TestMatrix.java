/**
 * 
 */
package data;

import java.util.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import au.id.cpd.algorithms.data.*;

/**
 * @author cd
 *
 */
public class TestMatrix {

	private IMatrix matrix;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.matrix = new Matrix<Double>();
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#resize(au.id.cpd.algorithms.data.Size)}.
	 */
	@Test
	public void testResizeSize() {
		this.matrix.resize(4,4);
		assertTrue(this.matrix.size()==16);
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#resize(int, int)}.
	 */
	@Test
	public void testResizeIntInt() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#add(java.lang.Object)}.
	 */
	@Test
	public void testAddObject() {
		this.matrix.resize(4,4);
		for(int i=0;i<16;i++) {
			this.matrix.add(i);
		}
		System.out.println(this.matrix);
		int cnt = 0;
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				assertTrue((Integer)this.matrix.get(i,j)==cnt);
				cnt++;
			}
		}
	}
	/**
	 * 
	 *
	 */
	@Test
	public void testTransform() {
		this.matrix = new Matrix<Integer>();
		this.matrix.resize(4,4);
		for(int i=0;i<16;i++) {
			this.matrix.add(i);
		}
		System.out.println(this.matrix);
		IMatrix test = this.matrix.transform();
		System.out.println(test);
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				assertTrue((Integer)this.matrix.get(i,j)==(Integer)test.get(j,i));
			}
		}
	}
	/**
	 * 
	 *
	 */
	@Test
	public void testMultiply() {
		this.matrix = new Matrix<Integer>();
		this.matrix.resize(4,4);
		for(int i=0;i<16;i++) {
			this.matrix.add(i);
		}
		System.out.println(this.matrix);
		IMatrix<Double> test = this.matrix.transform();
		System.out.println(test);
		IMatrix<Double> result = this.matrix.multiply(test);
		System.out.println(result);
		List<Number> vec = new Vector<Number>();
		for(int i=0;i<this.matrix.getSize().getRows();i++) {
			vec.add(i+1);
		}
		List<Double> resVec = this.matrix.multiply(vec);
		System.out.println(resVec);
		
	}
	
	@Test
	public void testNormalise() {
		this.matrix = new Matrix<Integer>();
		this.matrix.resize(4,4);
		for(int i=0;i<16;i++) {
			this.matrix.add(i);
		}
		System.out.println(this.matrix);
		IMatrix test = this.matrix.normalise();
		System.out.println(test);
	}
	
	@Test
	public void testMinMaxNormalise() {
		this.matrix = new Matrix<Integer>();
		this.matrix.resize(4,4);
		for(int i=0;i<16;i++) {
			this.matrix.add(i);
		}
		System.out.println(this.matrix);
		IMatrix test = this.matrix.minMaxNormalise();
		System.out.println(test);
	}
	
	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#add(int, int, java.lang.Object)}.
	 */
	@Test
	public void testAddIntIntT() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#addAll(java.util.Collection)}.
	 */
	@Test
	public void testAddAll() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#clear()}.
	 */
	@Test
	public void testClear() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#contains(java.lang.Object)}.
	 */
	@Test
	public void testContains() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#containsAll(java.util.Collection)}.
	 */
	@Test
	public void testContainsAll() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#isEmpty()}.
	 */
	@Test
	public void testIsEmpty() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#iterator()}.
	 */
	@Test
	public void testIterator() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#remove(java.lang.Object)}.
	 */
	@Test
	public void testRemove() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#removeAll(java.util.Collection)}.
	 */
	@Test
	public void testRemoveAll() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#retainAll(java.util.Collection)}.
	 */
	@Test
	public void testRetainAll() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#size()}.
	 */
	@Test
	public void testSize() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#toArray()}.
	 */
	@Test
	public void testToArray() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link au.id.cpd.algorithms.data.Matrix#toArray(java.lang.Object[])}.
	 */
	@Test
	public void testToArrayObjectArray() {
		fail("Not yet implemented");
	}

}
