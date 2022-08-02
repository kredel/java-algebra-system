/*
 * $Id$
 */

package edu.jas.ps;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Multivariate power series tests with JUnit.
 * @author Heinz Kredel
 */

public class MultiVarPowerSeriesTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>MultiVarPowerSeriesTest</CODE> object.
     * @param name String.
     */
    public MultiVarPowerSeriesTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(MultiVarPowerSeriesTest.class);
        return suite;
    }


    BigRational cfac;


    MultiVarPowerSeriesRing<BigRational> fac;


    MultiVarPowerSeries<BigRational> a, b, c, d, e, f;


    int rl = 2;


    int kl = 10;


    float q = 0.3f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        String[] vars = new String[] { "x", "y" };
        cfac = new BigRational(1);
        fac = new MultiVarPowerSeriesRing<BigRational>(cfac, rl, vars);
        //System.out.println("fac = " + fac);
        //System.out.println("fac = " + fac.toScript());
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
    }


    /**
     * Test generate.
     */
    public void testGenerate() {
        String s = fac.toScript();
        //System.out.println("fac.toScript: " + s + ", " + s.length());
        assertTrue("#s == 17: " + s, s.length() == 17);

        List<MultiVarPowerSeries<BigRational>> gens = fac.generators();
        assertFalse("#gens != () ", gens.isEmpty());
        //System.out.println("generators: " + gens);

        // test equals
        Set<MultiVarPowerSeries<BigRational>> set = new HashSet<MultiVarPowerSeries<BigRational>>(gens);
        //System.out.println("gen set: " + set);
        assertEquals("#gens == #set: ", gens.size(), set.size());

        // test for elements 0, 1
        a = fac.getZERO();
        b = fac.getONE();
        assertFalse("0 not in #set: ", set.contains(a));
        assertTrue("1 in #set: ", set.contains(b));

        // specific tests
        assertEquals("#gens == rl+1 ", rl + 1, gens.size());
        Set<Integer> iset = new HashSet<Integer>(set.size());
        for (MultiVarPowerSeries<BigRational> p : gens) {
            //System.out.println("p = " + p.toScript() + ", # = " + p.hashCode() + ", red = " + p.reductum());
            assertTrue("red(p) == 0 ", p.reductum().isZERO());
	    iset.add(p.hashCode());
        }
        assertEquals("#gens == #iset: ", gens.size(), iset.size());
    }


    /**
     * Test MultiVarCoefficients.
     */
    public void testCoefficients() {
        BigRational cf = new BigRational(0);
        GenPolynomialRing<BigRational> pring = new GenPolynomialRing<BigRational>(cf, rl);

        MultiVarCoefficients<BigRational> zeros = new Zeros(pring);
        MultiVarCoefficients<BigRational> ones = new Ones(pring);
        MultiVarCoefficients<BigRational> vars = new Vars(pring);

        int m = 5;
        ExpVectorIterable eitbl = new ExpVectorIterable(rl, true, m);
        //System.out.println("eitbl 0 = " + eitbl.iterator().hasNext());
        for (ExpVector e : eitbl) {
            BigRational c = zeros.get(e);
            //System.out.println("c = " + c + ", e = " + e);
            assertTrue("isZERO( c )", c.isZERO());
        }
        //System.out.println("coeffCache = " + zeros.coeffCache);
        //System.out.println("zeroCache  = " + zeros.zeroCache);
        assertTrue("coeffCache is one element", zeros.coeffCache.size() == (m + 1));

        //System.out.println("eitbl 1 = " + eitbl.iterator().hasNext());
        for (ExpVector e : eitbl) {
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
        //no-more: assertTrue("zeroCache is not empty", !vars.zeroCache.isEmpty());
        assertTrue("#coeffCache = " + m, vars.coeffCache.size() == (m + 1));
        assertTrue("#homCheck = " + m, vars.homCheck.length() == (m + 1));
    }


    /**
     * Test constructor and generators.
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

        List<MultiVarPowerSeries<BigRational>> gens = fac.generators();
        assertTrue("#gens == rl+1 ", rl + 1 == gens.size());
        for (MultiVarPowerSeries<BigRational> p : gens) {
            //System.out.println("p = " + p);
            assertTrue("red(p) == 0 ", p.reductum().isZERO());
        }

        a = fac.copy(c);
        b = c.copy();
        assertEquals("copy(c) == c.clone() ", a, b);
        assertTrue("copy(c) == c.clone() ", a.equals(b));

        a = fac.fromInteger(1);
        assertEquals("1 == fromInteger(1) ", a, c);

        b = fac.fromInteger(java.math.BigInteger.ONE);
        assertEquals("1 == fromInteger(1) ", b, c);

        e = fac.generate((i) -> i.isZERO() ? cfac.getONE() : cfac.getZERO());
        //System.out.println("e = " + e);
        assertTrue("isZERO( e )", !e.isZERO());
        assertTrue("isONE( e )", e.isONE());
    }


    /**
     * Test random power series.
     */
    public void testRandom() {
        for (int i = 0; i < 5; i++) {
            a = fac.random(i + 2);
            if (a.isZERO()||a.isONE()) {
                continue;
            }
            //System.out.println("a = " + a);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
        }
    }


    /**
     * Test MultiVarCoefficients in power series.
     */
    public void testCoefficientsInPS() {
        MultiVarCoefficients<BigRational> zeros = new Zeros(fac);
        MultiVarCoefficients<BigRational> ones = new Ones(fac);
        MultiVarCoefficients<BigRational> vars = new Vars(fac);

        a = new MultiVarPowerSeries<BigRational>(fac, zeros);
        b = new MultiVarPowerSeries<BigRational>(fac, ones);
        c = new MultiVarPowerSeries<BigRational>(fac, vars);

        int m = 5;
        ExpVectorIterable eitbl = new ExpVectorIterable(rl, true, m);
        for (ExpVector e : eitbl) {
            BigRational r = a.coefficient(e);
            //System.out.println("r = " + r + ", e = " + e);
            assertTrue("isZERO( r )", r.isZERO());
        }
        //System.out.println("#a = " + a.lazyCoeffs.coeffCache);
        assertTrue("coeffCache is one element", a.lazyCoeffs.coeffCache.size() == (m + 1));
        assertTrue("isZERO( a )", a.isZERO()); // after previous

        for (ExpVector e : eitbl) {
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
        //no-more:assertTrue("zeroCache is not empty", !c.lazyCoeffs.zeroCache.isEmpty());
        assertTrue("#coeffCache = " + m, c.lazyCoeffs.coeffCache.size() == (m + 1));
        assertTrue("#homCheck = " + m, c.lazyCoeffs.homCheck.length() == (m + 1));
    }


    /**
     * Test addition.
     */
    public void testAddition() {
        a = fac.random(kl);
        b = fac.random(kl);

        c = a.sum(b);
        d = b.sum(a);
        assertEquals("a+b = b+a", c, d);

        d = c.subtract(b);
        assertEquals("a+b-b = a", a, d);
        d = c.sum(b.negate());
        assertEquals("a+b-b = a", a, d);

        c = fac.random(kl);
        d = a.sum(b.sum(c));
        e = a.sum(b).sum(c);
        assertEquals("a+(b+c) = (a+b)+c", d, e);

        Map.Entry<ExpVector, BigRational> ma = a.orderMonomial();
        c = a.reductum().sum(ma);
        assertEquals("a = red(a)+om(a)", a, c);
    }


    /**
     * Test multiplication.
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
     * Test reductum.
     */
    public void testReductum() {
        a = fac.random(kl);
        //System.out.println("a = " + a);

        Map.Entry<ExpVector, BigRational> m = a.orderMonomial();
        //System.out.println("m = " + m);
        ExpVector k = m.getKey();
        BigRational br = m.getValue();

        b = fac.getONE().multiply(br, k);
        //System.out.println("b = " + b);

        c = a.reductum();
        //System.out.println("c = " + c);

        d = c.sum(b);
        //System.out.println("d = " + d);
        assertEquals("a = red(a)+1*lm(a) ", a, d);

        e = c.sum(br, k);
        //System.out.println("e = " + e);
        assertEquals("a = red(a)+lm(a) ", a, e);

        e = a.subtract(br, k);
        //System.out.println("e = " + e);
        assertEquals("a - lm(a) = red(a) ", c, e);

        b = fac.random(kl);
        String s = b.toString(); // generate and cache some coefficients
        //System.out.println("b = " + b);
        assertFalse("s.size > 0 " + s, s.length() == 0); // java-5

        c = a.sum(b);
        //System.out.println("c = " + c);

        d = a.sum(b.lazyCoeffs);
        //System.out.println("d = " + d);

        //         while ( !c.isZERO() ) {
        //             c = c.reductum();
        //             //System.out.println("c = " + c);
        //      }
        //         assertTrue("red^n(a) == 0 ", c.isZERO());

        br = new BigRational(2, 3);
        c = a.prepend(br, 0);
        d = c.reductum(0);
        assertEquals("red(a + br_0,0) = a ", d, a);

        c = a.shift(3, 0);
        d = c.shift(-3, 0);
        assertEquals("shift(shift(a,3,),-3,0) = a ", d, a);
    }


    /**
     * Test polynomial constructions.
     */
    public void testPolynomial() {
        GenPolynomialRing<BigRational> pr = fac.polyRing();
        //System.out.println("pr = " + pr);

        GenPolynomial<BigRational> p = pr.random(kl, 3, 3, q + q);
        //System.out.println("p = " + p);

        a = fac.fromPolynomial(p);
        //System.out.println("a = " + a);

        GenPolynomial<BigRational> s = a.asPolynomial();
        //System.out.println("s = " + s);
        assertEquals("asPolynomial(fromPolynomial(p)) = p ", p, s);

        b = fac.fromPolynomial(s);
        //System.out.println("b = " + b);
        assertEquals("fromPolynomial(asPolynomial(s)) = s ", a, b);
    }

}


class Zeros extends MultiVarCoefficients<BigRational> {


    public Zeros(MultiVarPowerSeriesRing<BigRational> pf) {
        super(pf);
    }


    public Zeros(GenPolynomialRing<BigRational> pf) {
        super(pf);
    }


    @Override
    public BigRational generate(ExpVector i) {
        return pfac.coFac.getZERO();
    }

}


class Ones extends MultiVarCoefficients<BigRational> {


    public Ones(MultiVarPowerSeriesRing<BigRational> pf) {
        super(pf);
    }


    public Ones(GenPolynomialRing<BigRational> pf) {
        super(pf);
    }


    @Override
    public BigRational generate(ExpVector i) {
        return pfac.coFac.getONE();
    }

}


class Vars extends MultiVarCoefficients<BigRational> {


    public Vars(MultiVarPowerSeriesRing<BigRational> pf) {
        super(pf);
    }


    public Vars(GenPolynomialRing<BigRational> pf) {
        super(pf);
    }


    @Override
    public BigRational generate(ExpVector i) {
        int[] v = i.dependencyOnVariables();
        if (v.length == 1 && i.getVal(v[0]) == 1L) {
            return pfac.coFac.getONE();
        }
        return pfac.coFac.getZERO();
    }

}
