/*
 * $Id$
 */

package edu.jas.ps;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.util.ExpVectorIterable;


/**
 * Multivariate power series tests with JUnit.
 * @author Heinz Kredel.
 */

public class MultiPowerSeriesTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>MultiPowerSeriesTest</CODE> object.
     * @param name String.
     */
    public MultiPowerSeriesTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(MultiPowerSeriesTest.class);
        return suite;
    }


    MultiVarPowerSeriesRing<BigRational> fac;


    MultiVarPowerSeries<BigRational> a;


    MultiVarPowerSeries<BigRational> b;


    MultiVarPowerSeries<BigRational> c;


    MultiVarPowerSeries<BigRational> d;


    MultiVarPowerSeries<BigRational> e;


    int rl = 2;


    int kl = 10;


    float q = 0.5f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        fac = new MultiVarPowerSeriesRing<BigRational>(new BigRational(1),rl);
        System.out.println("fac = " + fac);
        System.out.println("fac = " + fac.toScript());
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
    }


    /**
     * Test MultiVarCoefficients.
     * 
     */
    public void testCoefficients() {
        BigRational cf = new BigRational(0);
        GenPolynomialRing<BigRational> pring = new GenPolynomialRing<BigRational>(cf,rl);

        MultiVarCoefficients<BigRational> zeros = new MultiVarCoefficients<BigRational>(pring) {
            @Override
            public BigRational generate(ExpVector i) {
                return pfac.coFac.getZERO();
            }
        };
        MultiVarCoefficients<BigRational> ones = new MultiVarCoefficients<BigRational>(pring) {
            @Override
            public BigRational generate(ExpVector i) {
                return pfac.coFac.getONE();
            }
        };
        MultiVarCoefficients<BigRational> vars = new MultiVarCoefficients<BigRational>(pring) {
            @Override
            public BigRational generate(ExpVector i) {
                int[] v = i.dependencyOnVariables();
                if ( v.length == 1 && i.getVal(v[0]) == 1L) {
                    return pfac.coFac.getONE();
                } else {
                    return pfac.coFac.getZERO();
                }
            }
        };

        int m = 5;
        ExpVectorIterable eiter = new ExpVectorIterable(rl,true,m);
        for ( ExpVector e : eiter ) {
            BigRational c = zeros.get(e);
            //System.out.println("c = " + c + ", e = " + e);
            assertTrue("isZERO( c )", c.isZERO());
        }
        //System.out.println("coeffCache = " + zeros.coeffCache);
        //System.out.println("zeroCache  = " + zeros.zeroCache);
        assertTrue("coeffCache is one element", zeros.coeffCache.size() == (m+1));

        for ( ExpVector e : eiter ) {
            BigRational c = ones.get(e);
            //System.out.println("c = " + c + ", e = " + e);
            assertTrue("isONE( c )", c.isONE());
        }
        //System.out.println("coeffCache = " + ones.coeffCache);
        //System.out.println("zeroCache  = " + ones.zeroCache);
        assertTrue("zeroCache is empty", ones.zeroCache.isEmpty());

        for ( int i = 0; i <= m; i++ ) {
            GenPolynomial<BigRational> c = ones.homPart(i);
            //System.out.println("c = " + c + ", i = " + i);
            GenPolynomial<BigRational> d = ones.homPart(i);
            //System.out.println("d = " + d + ", i = " + i);
            assertTrue("c.equals(d) ", c.equals(d));
        }
        //System.out.println("coeffCache = " + ones.coeffCache);
        //System.out.println("zeroCache  = " + ones.zeroCache);
        //System.out.println("homCheck   = " + ones.homCheck);
        //System.out.println("homCheck   = " + ones.homCheck.length());
        assertTrue("zeroCache is empty", ones.zeroCache.isEmpty());
        assertTrue("#coeffCache = " + m, ones.coeffCache.size()==(m+1));
        assertTrue("#homCheck = " + m, ones.homCheck.length()==(m+1));

        for ( int i = 0; i <= m; i++ ) {
            GenPolynomial<BigRational> c = vars.homPart(i);
            //System.out.println("c = " + c + ", i = " + i);
            assertTrue("c==0 || deg(c)==1 ", c.isZERO() || c.degree() == 1L );
        }
        //System.out.println("coeffCache = " + vars.coeffCache);
        //System.out.println("zeroCache  = " + vars.zeroCache);
        //System.out.println("homCheck   = " + vars.homCheck);
        //System.out.println("homCheck   = " + vars.homCheck.length());
        assertTrue("zeroCache is not empty", !vars.zeroCache.isEmpty());
        assertTrue("#coeffCache = " + m, vars.coeffCache.size()==(m+1));
        assertTrue("#homCheck = " + m, vars.homCheck.length()==(m+1));
    }


    /**
     * Test constructor and toString.
     * 
     */
    public void testConstruction() {
        c = fac.getONE();
        System.out.println("c = " + c);
        assertTrue("isZERO( c )", !c.isZERO());
        assertTrue("isONE( c )", c.isONE());

        d = fac.getZERO();
        System.out.println("d = " + d);
        assertTrue("isZERO( d )", d.isZERO());
        assertTrue("isONE( d )", !d.isONE());
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 0; i < 5; i++) {
            a = fac.random(i + 2);
            System.out.println("a = " + a);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
        }
    }


    /**
     * Test addition.
     * 
     */
    public void testAddition() {
        a = fac.random(kl);
        b = fac.random(kl);

        c = a.sum(b);
        d = b.sum(a);
        assertEquals("a+b = b+a", c, d);

        d = c.subtract(b);
        assertEquals("a+b-b = a", a, d);

        c = fac.random(kl);
        d = a.sum(b.sum(c));
        e = a.sum(b).sum(c);
        assertEquals("a+(b+c) = (a+b)+c", d, e);
    }

}
