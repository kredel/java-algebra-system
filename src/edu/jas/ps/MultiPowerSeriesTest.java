/*
 * $Id$
 */

package edu.jas.ps;


import java.util.Map;

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


    float q = 0.3f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        fac = new MultiVarPowerSeriesRing<BigRational>(new BigRational(1), rl);
        //System.out.println("fac = " + fac);
        //System.out.println("fac = " + fac.toScript());
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
        GenPolynomialRing<BigRational> pring = new GenPolynomialRing<BigRational>(cf, rl);

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
                if (v.length == 1 && i.getVal(v[0]) == 1L) {
                    return pfac.coFac.getONE();
                } else {
                    return pfac.coFac.getZERO();
                }
            }
        };

        int m = 5;
        ExpVectorIterable eiter = new ExpVectorIterable(rl, true, m);
        for (ExpVector e : eiter) {
            BigRational c = zeros.get(e);
            //System.out.println("c = " + c + ", e = " + e);
            assertTrue("isZERO( c )", c.isZERO());
        }
        //System.out.println("coeffCache = " + zeros.coeffCache);
        //System.out.println("zeroCache  = " + zeros.zeroCache);
        assertTrue("coeffCache is one element", zeros.coeffCache.size() == (m + 1));

        for (ExpVector e : eiter) {
            BigRational c = ones.get(e);
            //System.out.println("c = " + c + ", e = " + e);
            assertTrue("isONE( c )", c.isONE());
        }
        //System.out.println("coeffCache = " + ones.coeffCache);
        //System.out.println("zeroCache  = " + ones.zeroCache);
        assertTrue("zeroCache is empty", ones.zeroCache.isEmpty());

        for (int i = 0; i <= m; i++) {
            GenPolynomial<BigRational> c = ones.getHomPart(i);
            //System.out.println("c = " + c + ", i = " + i);
            GenPolynomial<BigRational> d = ones.getHomPart(i);
            //System.out.println("d = " + d + ", i = " + i);
            assertTrue("c.equals(d) ", c.equals(d));
        }
        //System.out.println("coeffCache = " + ones.coeffCache);
        //System.out.println("zeroCache  = " + ones.zeroCache);
        //System.out.println("homCheck   = " + ones.homCheck);
        //System.out.println("homCheck   = " + ones.homCheck.length());
        assertTrue("zeroCache is empty", ones.zeroCache.isEmpty());
        assertTrue("#coeffCache = " + m, ones.coeffCache.size() == (m + 1));
        assertTrue("#homCheck = " + m, ones.homCheck.length() == (m + 1));

        for (int i = 0; i <= m; i++) {
            GenPolynomial<BigRational> c = vars.getHomPart(i);
            //System.out.println("c = " + c + ", i = " + i);
            assertTrue("c==0 || deg(c)==1 ", c.isZERO() || c.degree() == 1L);
        }
        //System.out.println("coeffCache = " + vars.coeffCache);
        //System.out.println("zeroCache  = " + vars.zeroCache);
        //System.out.println("homCheck   = " + vars.homCheck);
        //System.out.println("homCheck   = " + vars.homCheck.length());
        assertTrue("zeroCache is not empty", !vars.zeroCache.isEmpty());
        assertTrue("#coeffCache = " + m, vars.coeffCache.size() == (m + 1));
        assertTrue("#homCheck = " + m, vars.homCheck.length() == (m + 1));
    }


    /**
     * Test constructor and toString.
     * 
     */
    public void testConstruction() {
        //System.out.println("fac = " + fac);
        //System.out.println("fac = " + fac.toScript());

        c = fac.getONE();
        //System.out.println("c = " + c);
        assertTrue("isZERO( c )", !c.isZERO());
        assertTrue("isONE( c )", c.isONE());

        d = fac.getZERO();
        //System.out.println("d = " + d);
        assertTrue("isZERO( d )", d.isZERO());
        assertTrue("isONE( d )", !d.isONE());
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 0; i < 5; i++) {
            a = fac.random(i + 2);
            //System.out.println("a = " + a);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
        }
    }


    /**
     * Test MultiVarCoefficients in power series.
     * 
     */
    public void testCoefficientsInPS() {

        MultiVarCoefficients<BigRational> zeros = new MultiVarCoefficients<BigRational>(fac) {


            @Override
            public BigRational generate(ExpVector i) {
                return pfac.coFac.getZERO();
            }
        };
        MultiVarCoefficients<BigRational> ones = new MultiVarCoefficients<BigRational>(fac) {


            @Override
            public BigRational generate(ExpVector i) {
                return pfac.coFac.getONE();
            }
        };
        MultiVarCoefficients<BigRational> vars = new MultiVarCoefficients<BigRational>(fac) {


            @Override
            public BigRational generate(ExpVector i) {
                int[] v = i.dependencyOnVariables();
                if (v.length == 1 && i.getVal(v[0]) == 1L) {
                    return pfac.coFac.getONE();
                } else {
                    return pfac.coFac.getZERO();
                }
            }
        };

        a = new MultiVarPowerSeries<BigRational>(fac, zeros);
        b = new MultiVarPowerSeries<BigRational>(fac, ones);
        c = new MultiVarPowerSeries<BigRational>(fac, vars);

        int m = 5;
        ExpVectorIterable eiter = new ExpVectorIterable(rl, true, m);
        for (ExpVector e : eiter) {
            BigRational r = a.coefficient(e);
            //System.out.println("r = " + r + ", e = " + e);
            assertTrue("isZERO( r )", r.isZERO());
        }
        //System.out.println("#a = " + a.lazyCoeffs.coeffCache);
        assertTrue("coeffCache is one element", a.lazyCoeffs.coeffCache.size() == (m + 1));
        assertTrue("isZERO( a )", a.isZERO()); // after previous

        for (ExpVector e : eiter) {
            BigRational r = b.coefficient(e);
            //System.out.println("r = " + r + ", e = " + e);
            assertTrue("isONE( r )", r.isONE());
        }
        assertTrue("zeroCache is empty", b.lazyCoeffs.zeroCache.isEmpty());

        for (int i = 0; i <= m; i++) {
            GenPolynomial<BigRational> p = b.homogeneousPart(i);
            //System.out.println("p = " + p + ", i = " + i);
            GenPolynomial<BigRational> q = b.homogeneousPart(i);
            //System.out.println("q = " + q + ", i = " + i);
            assertTrue("p.equals(q) ", p.equals(q));
        }
        assertTrue("zeroCache is empty", b.lazyCoeffs.zeroCache.isEmpty());
        assertTrue("#coeffCache = " + m, b.lazyCoeffs.coeffCache.size() == (m + 1));
        assertTrue("#homCheck = " + m, b.lazyCoeffs.homCheck.length() == (m + 1));

        for (int i = 0; i <= m; i++) {
            GenPolynomial<BigRational> p = c.homogeneousPart(i);
            //System.out.println("p = " + p + ", i = " + i);
            assertTrue("p==0 || deg(p)==1 ", p.isZERO() || p.degree() == 1L);
        }
        assertTrue("zeroCache is not empty", !c.lazyCoeffs.zeroCache.isEmpty());
        assertTrue("#coeffCache = " + m, c.lazyCoeffs.coeffCache.size() == (m + 1));
        assertTrue("#homCheck = " + m, c.lazyCoeffs.homCheck.length() == (m + 1));
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


    /**
     * Test multiplication.
     * 
     */
    public void testMultiplication() {
        a = fac.random(kl);
        b = fac.random(kl);

        if (a.isZERO() || b.isZERO()) {
            return;
        }
        assertTrue("not isZERO( a )", !a.isZERO());
        assertTrue("not isZERO( b )", !b.isZERO());

        c = b.multiply(a);
        d = a.multiply(b);
        assertTrue("not isZERO( c )", !c.isZERO());
        assertTrue("not isZERO( d )", !d.isZERO());

        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        e = d.subtract(c);
        assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO());

        assertTrue("a*b = b*a", c.equals(d));
        assertEquals("a*b = b*a", c, d);

        c = fac.random(kl);
        //System.out.println("c = " + c);
        d = a.multiply(b.multiply(c));
        e = (a.multiply(b)).multiply(c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        //System.out.println("d-e = " + d.subtract(c) );

        assertEquals("a(bc) = (ab)c", d, e);
        assertTrue("a(bc) = (ab)c", d.equals(e));

        ExpVector ev = ExpVector.random(rl, 5, 0.8f);
        BigRational br = fac.coFac.random(5);

        b = a.shift(ev).multiply(br);
        c = a.multiply(br, ev);
        assertEquals("(a ev) br = a (ev,br)", b, c);
        //System.out.println("a  = " + a);
        //System.out.println("ev = " + ev);
        //System.out.println("br = " + br);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);
    }


    /**
     * Test distributive law.
     * 
     */
    public void testDistributive() {
        a = fac.random(kl, q);
        b = fac.random(kl, q);
        c = fac.random(kl, q);

        d = a.multiply(b.sum(c));
        e = a.multiply(b).sum(a.multiply(c));

        assertEquals("a(b+c) = ab+ac", d, e);
    }


    /**
     * Test inverse.
     * 
     */
    public void testInverse() {
        a = fac.getONE();
        assertTrue("not isZERO( a )", !a.isZERO());
        assertTrue("isUnit( a )", a.isUnit());
        //System.out.println("a = " + a);

        b = a.inverse();
        c = a.multiply(b);
        assertTrue("isONE( c )", c.isONE());
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);

        a = fac.random(kl);
        if (!a.isUnit()) {
            a = fac.fromInteger(23); //return;
        }
        //System.out.println("a = " + a);
        b = a.inverse();
        c = a.multiply(b);
        assertTrue("isONE( c )", c.isONE());
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);

        b = fac.random(kl);
        c = b.divide(a);
        d = c.multiply(a);
        assertEquals("b/a * a == b ", d, b);
    }


    /**
     * Test fix point constructions.
     * 
     */
    public void testFixpoints() {
        int r = 0;
        UnivPowerSeriesRing<BigRational> ufac = new UnivPowerSeriesRing<BigRational>(fac.coFac, fac.vars[r]);
        UnivPowerSeries<BigRational> exp = ufac.getEXP();
        //System.out.println("exp = " + exp);

        a = fac.fromPowerSeries(exp, 0);
        b = fac.fromPowerSeries(exp, 1);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        c = fac.getEXP(0);
        d = fac.getEXP(1);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a == c ", a, c);
        assertEquals("b == d ", b, d);

        e = d.differentiate(0);
        //System.out.println("e = " + e);
        assertTrue("isZERO( e )", e.isZERO());
        e = d.differentiate(1);
        //System.out.println("e = " + e);
        assertEquals("e == d ", d, e);
    }


    /**
     * Test reduction.
     * 
     */
    public void testReduction() {
        a = fac.random(kl);
        //System.out.println("a = " + a);

        Map.Entry<ExpVector, BigRational> m = a.orderMonomial();
        //System.out.println("m = " + m);
        ExpVector k = m.getKey();
        BigRational br = m.getValue();

        b = fac.getONE().multiply(br,k);
        //System.out.println("b = " + b);

        c = a.reductum();
        //System.out.println("c = " + c);

        d = c.sum(b);
        //System.out.println("d = " + d);
        assertEquals("a = red(a)+1*lm(a) ", a, d);

        e = c.sum(br,k);
        //System.out.println("e = " + e);
        assertEquals("a = red(a)+lm(a) ", a, e);

        e = a.subtract(br,k);
        //System.out.println("e = " + e);
        assertEquals("a - lm(a) = red(a) ", c, e);

        b = fac.random(kl);
        String s = b.toString(); // generate and cache some coefficients
        //System.out.println("b = " + b);

        c = a.sum(b);
        //System.out.println("c = " + c);

        d = a.sum(b.lazyCoeffs);
        //System.out.println("d = " + d);

//         while ( !c.isZERO() ) {
//             c = c.reductum();
//             //System.out.println("c = " + c);
//      }
//         assertTrue("red^n(a) == 0 ", c.isZERO());
    }
}
