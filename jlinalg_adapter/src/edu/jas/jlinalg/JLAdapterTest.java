/*
 * $Id$
 */

package edu.jas.jlinalg;


import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigRational;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * JLAdapter tests with JUnit
 * @author Heinz Kredel.
 */

public class JLAdapterTest extends TestCase {  


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>JLAdapterTest</CODE> object.
     * @param name String.
     */
    public JLAdapterTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(JLAdapterTest.class);
        return suite;
    }


    int rl = 5;


    int kl = 10;


    int ll = 10;


    float q = 0.5f;


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
    }


    /**
     * Test constructor and toString.
     * 
     */
    public void testConstruction() {
        BigRational z = new BigRational(0);

        JLAdapter<BigRational> a = new JLAdapter<BigRational>(z);
        //System.out.println("a = " + a);
        assertTrue("isZero( a )", a.isZero());

        BigRational o = new BigRational(1);
        JLAdapter<BigRational> b = new JLAdapter<BigRational>(o);
        //System.out.println("b = " + b);
        assertTrue("isOne( b )", b.isOne());

        JLAdapter<BigRational> c = b.subtract(b);
        //System.out.println("c = " + c);
        assertTrue("isZero( c )", c.isZero());

        assertEquals("a == c ", a, c);

        c = new JLAdapter<BigRational>(new BigRational(1, 2));
        //System.out.println("c = " + c);
        assertTrue("!isZero( c )", !c.isZero());

        JLAdapter<BigRational> d = c.invert();
        //System.out.println("d = " + d);
        assertTrue("!isZero( d )", !d.isZero());

        assertTrue("isOne( 1/2 * 2 ) ", d.multiply(c).isOne());

        JLAdapter<BigRational> e = b.divide(d);
        //System.out.println("e = " + e);
        assertEquals("1/2 == 1 / (2) ", c, e);
    }


    /**
     * Test factory and toString.
     * 
     */
    public void testFactory() {
        RingFactory<BigRational> z = new BigRational(0);

        JLAdapterFactory<BigRational> fac = new JLAdapterFactory<BigRational>( z );
        //System.out.println("fac = " + fac);

        JLAdapter<BigRational> a = fac.zero();
        //System.out.println("a = " + a);
        assertTrue("isZero( a )", a.isZero());

        JLAdapter<BigRational> b = fac.one();
        //System.out.println("b = " + b);
        assertTrue("isOne( b )", b.isOne());

    }


    /**
     * Test matrix solve.
     * 
     */
    public void xtestGenMatrixSolv() {
        MatrixExamples gms = new MatrixExamples();
        gms.main(null);
    }


    /**
     * Test vector conversions.
     * 
     */
    public void testVectorConversion() {
        RingFactory<BigRational> z = new BigRational(0);
        JLAdapterFactory<BigRational> fac = new JLAdapterFactory<BigRational>( z );

        JLAdapter<BigRational>[] vec1 = fac.getArray(ll);
        //System.out.println("vec1 =" + Arrays.toString(vec1));

        RingElem<BigRational>[] v1 = JLAdapterUtil.<BigRational>fromJLAdapter(vec1);
        //System.out.println("v1   =" + Arrays.toString(v1));

        JLAdapter<BigRational>[] vec2 = JLAdapterUtil.<BigRational>toJLAdapterRE(v1);
        //System.out.println("vec2 =" + Arrays.toString(vec2));

        assertTrue("v1[] == v2[] ", Arrays.equals(vec1,vec2));


        BigRational[] v2 = new BigRational[ll];
        for ( int i = 0; i < v2.length; i++ ) {
            v2[i] = z.random(kl);
        }
        //System.out.println("v2   =" + Arrays.toString(v2));

        JLAdapter<BigRational>[] vec3 = JLAdapterUtil.<BigRational>toJLAdapter(v2);
        //System.out.println("vec3 =" + Arrays.toString(vec3));

        RingElem<BigRational>[] v3 =  JLAdapterUtil.<BigRational>fromJLAdapter(vec3);
        //System.out.println("v3   =" + Arrays.toString(v3));

        assertTrue("v2[] == v3[] ", Arrays.equals(v2,v3));

    }


    /**
     * Test matrix conversions.
     * 
     */
    public void testMatrixConversion() {
        RingFactory<BigRational> z = new BigRational(0);
        JLAdapterFactory<BigRational> fac = new JLAdapterFactory<BigRational>( z );

        JLAdapter<BigRational>[][] vec1 = fac.getArray(ll,ll);
        //System.out.println("vec1   =" + matrixToString(vec1));

        RingElem<BigRational>[][] v1 = JLAdapterUtil.<BigRational>fromJLAdapter(vec1);
        //System.out.println("v1     =" + matrixToString(v1));

        JLAdapter<BigRational>[][] vec2 = JLAdapterUtil.<BigRational>toJLAdapterRE(v1);
        //System.out.println("vec2   =" + matrixToString(vec2));

        assertMatrixEquals(vec1,vec2);


        BigRational[][] v2 = new BigRational[ll][];
        for ( int i = 0; i < v2.length; i++ ) {
            v2[i] = new BigRational[ll];
            for ( int j = 0; j < v2.length; j++ ) {
                v2[i][j] = z.random(kl);
            }
        }
        //System.out.println("v2     =" + matrixToString(v2));

        JLAdapter<BigRational>[][] vec3 = JLAdapterUtil.<BigRational>toJLAdapter(v2);
        //System.out.println("vec1   =" + matrixToString(vec3));

        RingElem<BigRational>[][] v3 =  JLAdapterUtil.<BigRational>fromJLAdapter(vec3);
        //System.out.println("v3     =" + matrixToString(v3));

        //v3[0][0] = v3[1][1];
        assertMatrixEquals(v2,v3);

    }


    public String matrixToString(Object[][] m) {
        StringBuffer s = new StringBuffer("[");
        for ( int i = 0; i < m.length; i++ ) {
            if ( i != 0 ) {
                s.append(", ");
            }
            s.append( Arrays.toString(m[i]) );
        }
        s.append("]");
        return s.toString();
    }


    public void assertMatrixEquals(Object[][] m1, Object[][] m2) {
        for ( int i = 0; i < m1.length; i++ ) {
            assertTrue("m1[][] == m2[][] ", Arrays.equals(m1[i],m2[i]));
        }
    }

}
