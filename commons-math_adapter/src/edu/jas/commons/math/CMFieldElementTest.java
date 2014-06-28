package edu.jas.commons.math;

import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.jas.arith.BigRational;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * CMFieldElementTest tests with JUnit
 *  
 */

public class CMFieldElementTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>CMFieldElementTest</CODE> object.
     * @param name String.
     */
    public CMFieldElementTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(CMFieldElementTest.class);
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

        CMFieldElement<BigRational> a = new CMFieldElement<BigRational>(z);
        //System.out.println("a = " + a);
        assertTrue("isZero( a )", a.isZero());

        BigRational o = new BigRational(1);
        CMFieldElement<BigRational> b = new CMFieldElement<BigRational>(o);
        //System.out.println("b = " + b);
        assertTrue("isOne( b )", b.isOne());

        CMFieldElement<BigRational> c = b.subtract(b);
        //System.out.println("c = " + c);
        assertTrue("isZero( c )", c.isZero());

        assertEquals("a == c ", a, c);

        c = new CMFieldElement<BigRational>(new BigRational(1, 2));
        //System.out.println("c = " + c);
        assertTrue("!isZero( c )", !c.isZero());

//        CMFieldElement<BigRational> d = c.invert();
//        //System.out.println("d = " + d);
//        assertTrue("!isZero( d )", !d.isZero());
//
//        assertTrue("isOne( 1/2 * 2 ) ", d.multiply(c).isOne());
//
//        CMFieldElement<BigRational> e = b.divide(d);
//        //System.out.println("e = " + e);
//        assertEquals("1/2 == 1 / (2) ", c, e);
    }


    /**
     * Test factory and toString.
     * 
     */
    public void testFactory() {
        RingFactory<BigRational> z = new BigRational(0);

        CMField<BigRational> fac = new CMField<BigRational>( z );
        //System.out.println("fac = " + fac);

        CMFieldElement<BigRational> a = fac.getZero();
        //System.out.println("a = " + a);
        assertTrue("isZero( a )", a.isZero());

        CMFieldElement<BigRational> b = fac.getOne();
        //System.out.println("b = " + b);
        assertTrue("isOne( b )", b.isOne());

    }


    /**
     * Test matrix solve.
     * 
     */
//    public void testGenMatrixSolv() {
//        MatrixExamples gms = new MatrixExamples();
//        gms.main(null);
//    }


    /**
     * Test vector conversions.
     * 
     */
    public void testVectorConversion() {
        RingFactory<BigRational> z = new BigRational(0);
        CMField<BigRational> fac = new CMField<BigRational>( z );

        CMFieldElement<BigRational>[] vec1 = fac.getArray(ll);
        //System.out.println("vec1 =" + Arrays.toString(vec1));

        RingElem<BigRational>[] v1 = CMFieldElementUtil.<BigRational>fromCMFieldElement(vec1);
        //System.out.println("v1   =" + Arrays.toString(v1));

        CMFieldElement<BigRational>[] vec2 = CMFieldElementUtil.<BigRational>toCMFieldElementRE(v1);
        //System.out.println("vec2 =" + Arrays.toString(vec2));

        assertTrue("v1[] == v2[] ", Arrays.equals(vec1,vec2));


        BigRational[] v2 = new BigRational[ll];
        for ( int i = 0; i < v2.length; i++ ) {
            v2[i] = z.random(kl);
        }
        //System.out.println("v2   =" + Arrays.toString(v2));

        CMFieldElement<BigRational>[] vec3 = CMFieldElementUtil.<BigRational>toCMFieldElement(v2);
        //System.out.println("vec3 =" + Arrays.toString(vec3));

        RingElem<BigRational>[] v3 =  CMFieldElementUtil.<BigRational>fromCMFieldElement(vec3);
        //System.out.println("v3   =" + Arrays.toString(v3));

        assertTrue("v2[] == v3[] ", Arrays.equals(v2,v3));

    }


    /**
     * Test matrix conversions.
     * 
     */
    public void testMatrixConversion() {
        RingFactory<BigRational> z = new BigRational(0);
        CMField<BigRational> fac = new CMField<BigRational>( z );

        CMFieldElement<BigRational>[][] vec1 = fac.getArray(ll,ll);
        //System.out.println("vec1   =" + matrixToString(vec1));

        RingElem<BigRational>[][] v1 = CMFieldElementUtil.<BigRational>fromCMFieldElement(vec1);
        //System.out.println("v1     =" + matrixToString(v1));

        CMFieldElement<BigRational>[][] vec2 = CMFieldElementUtil.<BigRational>toCMFieldElementRE(v1);
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

        CMFieldElement<BigRational>[][] vec3 = CMFieldElementUtil.<BigRational>toCMFieldElement(v2);
        //System.out.println("vec1   =" + matrixToString(vec3));

        RingElem<BigRational>[][] v3 =  CMFieldElementUtil.<BigRational>fromCMFieldElement(vec3);
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
