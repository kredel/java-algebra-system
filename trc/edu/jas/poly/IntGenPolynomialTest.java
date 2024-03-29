/*
 * $Id$
 */

package edu.jas.poly;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.jas.arith.BigInteger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * BigInteger coefficients GenPolynomial tests with JUnit.
 * @author Heinz Kredel
 */

public class IntGenPolynomialTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>IntGenPolynomialTest</CODE> object.
     * @param name String.
     */
    public IntGenPolynomialTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(IntGenPolynomialTest.class);
        return suite;
    }

    GenPolynomialRing<BigInteger> fac;


    GenPolynomial<BigInteger> a, b, c, d, e;


    int rl = 7;


    int kl = 10;


    int ll = 10;


    int el = 5;


    float q = 0.3f;

    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        fac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl);
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
        assertTrue("#s == 50: " + s, s.length() == 50);

        List<GenPolynomial<BigInteger>> gens = fac.generators();
        assertFalse("#gens != () ", gens.isEmpty());
        //System.out.println("generators: " + gens);

        // test equals
        Set<GenPolynomial<BigInteger>> set = new HashSet<GenPolynomial<BigInteger>>(gens);
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
        for (GenPolynomial<BigInteger> p : gens) {
            //System.out.println("p = " + p.toScript() + ", # = " + p.hashCode() + ", red = " + p.reductum());
            assertTrue("red(p) == 0 ", p.reductum().isZERO());
            iset.add(p.hashCode());
        }
        assertEquals("#gens == #iset: ", gens.size(), iset.size());
    }


    /**
     * Test constructor and toString.
     */
    public void testConstruction() {
        c = fac.getONE();
        assertTrue("length( c ) = 1", c.length() == 1);
        assertTrue("isZERO( c )", !c.isZERO());
        assertTrue("isONE( c )", c.isONE());

        d = fac.getZERO();
        assertTrue("length( d ) = 0", d.length() == 0);
        assertTrue("isZERO( d )", d.isZERO());
        assertTrue("isONE( d )", !d.isONE());
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 0; i < 7; i++) {
            a = fac.random(kl * (i + 2), ll + 2 * i, el + i, q);
            assertTrue("length( a" + i + " ) <> 0", a.length() >= 0);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
        }
    }


    /**
     * Test addition.
     */
    public void testAddition() {
        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);

        c = a.sum(b);
        d = c.subtract(b);
        assertEquals("a+b-b = a", a, d);

        c = fac.random(kl, ll, el, q);

        ExpVector u = ExpVector.random(rl, el, q);
        BigInteger x = BigInteger.IRAND(kl);

        b = new GenPolynomial<BigInteger>(fac, x, u);
        c = a.sum(b);
        d = a.sum(x, u);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);

        c = a.subtract(b);
        d = a.subtract(x, u);
        assertEquals("a-p(x,u) = a-(x,u)", c, d);

        a = new GenPolynomial<BigInteger>(fac);
        b = new GenPolynomial<BigInteger>(fac, x, u);
        c = b.sum(a);
        d = a.sum(x, u);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);

        c = a.subtract(b);
        d = a.subtract(x, u);
        assertEquals("a-p(x,u) = a-(x,u)", c, d);
    }


    /**
     * Test object multiplication.
     */
    public void testMultiplication() {
        a = fac.random(kl, ll, el, q);
        assertTrue("not isZERO( a )", !a.isZERO());

        b = fac.random(kl, ll, el, q);
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

        c = fac.random(kl, ll, el, q);
        //System.out.println("c = " + c);
        d = a.multiply(b.multiply(c));
        e = (a.multiply(b)).multiply(c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        //System.out.println("d-e = " + d.subtract(c) );
        assertEquals("a(bc) = (ab)c", d, e);
        assertTrue("a(bc) = (ab)c", d.equals(e));

        //BigInteger x = a.leadingBaseCoefficient().inverse();
        //c = a.monic();
        //d = a.multiply(x);
        //assertEquals("a.monic() = a(1/ldcf(a))",c,d);

        BigInteger y = b.leadingBaseCoefficient();
        //c = b.monic();
        //d = b.multiply(y);
        //assertEquals("b.monic() = b(1/ldcf(b))",c,d);

        e = new GenPolynomial<BigInteger>(fac, y);
        c = b.multiply(e);
        // assertEquals("b.monic() = b(1/ldcf(b))",c,d);

        d = e.multiply(b);
        assertEquals("b*p(y,u) = p(y,u)*b", c, d);
    }


    /**
     * Test BLAS level 1.
     */
    public void testBLAS1() {
        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        ExpVector ev = ExpVector.random(rl, el, q);
        BigInteger lc = BigInteger.IRAND(kl);

        d = a.subtractMultiple(lc, b);
        e = a.subtract(b.multiply(lc));
        assertEquals("a - (lc) b == a - ((lc) b)", d, e);

        d = a.subtractMultiple(lc, ev, b);
        e = a.subtract(b.multiply(lc, ev));
        assertEquals("a - (lc ev) b == a - ((lc ev) b)", d, e);

        ExpVector fv = ExpVector.random(rl, el, q);
        BigInteger tc = BigInteger.IRAND(kl);

        d = a.scaleSubtractMultiple(tc, lc, ev, b);
        e = a.multiply(tc).subtract(b.multiply(lc, ev));
        assertEquals("(tc) a - (lc ev) b == ((tc) a - ((lc ev) b))", d, e);

        d = a.scaleSubtractMultiple(tc, fv, lc, ev, b);
        e = a.multiply(tc, fv).subtract(b.multiply(lc, ev));
        assertEquals("(tc fv) a - (lc ev) b == ((tc fv) a - ((lc ev) b))", d, e);

        d = a.scaleSubtractMultiple(tc, lc, b);
        e = a.multiply(tc).subtract(b.multiply(lc));
        assertEquals("tc a - lc b == (tc a) - (lc b)", d, e);
    }


    /**
     * Test distributive law.
     */
    public void testDistributive() {
        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);

        d = a.multiply(b.sum(c));
        e = a.multiply(b).sum(a.multiply(c));

        assertEquals("a(b+c) = ab+ac", d, e);
    }


    /**
     * Test absolute norm.
     */
    public void testAbsNorm() {
        BigInteger r, ar;
        a = fac.getONE().negate();
        //System.out.println("a = " + a);

        r = PolyUtil.<BigInteger> absNorm(a);
        //System.out.println("r = " + r);
        assertTrue("isONE( absNorm(-1) )", r.isONE());

        a = fac.random(kl * 2, ll + 2, el, q);
        //System.out.println("a = " + a);

        r = PolyUtil.<BigInteger> absNorm(a);
        //System.out.println("r = " + r);
        assertTrue(" not isZERO( absNorm(a) )", !r.isZERO() || a.isZERO());
    }


    /**
     * Test max norm.
     */
    public void testMaxNorm() {
        BigInteger r, s;
        a = fac.getZERO();
        //System.out.println("a = " + a);

        r = a.maxNorm();
        //System.out.println("r = " + r);
        assertTrue("isONE( maxNorm(0) )", r.isZERO());

        r = a.sumNorm();
        //System.out.println("r = " + r);
        assertTrue("isONE( sumNorm(0) )", r.isZERO());

        a = fac.getONE().negate();
        //System.out.println("a = " + a);

        r = a.maxNorm();
        //System.out.println("r = " + r);
        assertTrue("isONE( maxNorm(-1) )", r.isONE());

        r = a.sumNorm();
        //System.out.println("r = " + r);
        assertTrue("isONE( sumNorm(-1) )", r.isONE());

        a = fac.random(kl * 2, ll + 2, el, q);
        //System.out.println("a = " + a);

        r = a.maxNorm();
        //System.out.println("r = " + r);
        assertTrue("not isZERO( maxNorm(a) )", !r.isZERO() || a.isZERO());

        //s = a.multiply(a).maxNorm();
        //System.out.println("s = " + s + ", r*r = " + r.multiply(r));
        //assertEquals("s*s == maxNorm(a*a) )", r.multiply(r), s );

        r = a.sumNorm();
        //System.out.println("r = " + r);
        assertTrue("not isZERO( maxNorm(a) )", !r.isZERO() || a.isZERO());

        //s = a.multiply(a).sumNorm();
        //System.out.println("s = " + s + ", r*r = " + r.multiply(r));
        //assertEquals("s*s == sumNorm(a*a) ): ", r.multiply(r), s );
    }

}
