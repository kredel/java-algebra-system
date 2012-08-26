/*
 * $Id$
 */

package edu.jas.poly;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.structure.RingElem;


/**
 * GenWordPolynomial tests with JUnit.
 * @author Heinz Kredel.
 */

public class GenWordPolynomialTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GenWordPolynomialTest</CODE> object.
     * @param name String.
     */
    public GenWordPolynomialTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GenWordPolynomialTest.class);
        return suite;
    }


    int rl = 6;


    int kl = 10;


    int ll = 7;


    int el = 5;


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
    }


    /**
     * Test constructors and factory.
     */
    public void testConstructors() {
        // integers
        BigInteger rf = new BigInteger();
        //System.out.println("rf = " + rf);

        // non-commuting vars: abcdef
        WordFactory wf = new WordFactory("abcdef");
        //System.out.println("wf = " + wf);

        // polynomials over integers
        GenWordPolynomialRing<BigInteger> pf = new GenWordPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("pf = " + pf);

        GenWordPolynomial<BigInteger> p = pf.getONE();
        //System.out.println("p = " + p);
        assertTrue("p == 1", p.isONE());
        p = pf.getZERO();
        assertTrue("p == 1", p.isZERO());
        //System.out.println("p = " + p);
        //p = pf.random(9);
        //System.out.println("p = " + p);

        RingElem<GenWordPolynomial<BigInteger>> pe = new GenWordPolynomial<BigInteger>(pf);
        //System.out.println("pe = " + pe);
        //System.out.println("p.equals(pe) = " + p.equals(pe) );
        //System.out.println("p.equals(p) = " + p.equals(p) );
        assertTrue("p.equals(pe) = ", p.equals(pe));
        assertTrue("p.equals(p) = ", p.equals(p));

        pe = pe.sum(p);
        //System.out.println("pe = " + pe);
        assertTrue("pe.isZERO() = ", pe.isZERO());
        p = pf.random(9);
        p = p.subtract(p);
        //System.out.println("p = " + p);
        //System.out.println("p.isZERO() = " + p.isZERO());
        assertTrue("p.isZERO() = ", p.isZERO());

        // polynomials over (polynomials over integers)
        // non-commuting vars: xyz
        WordFactory wf2 = new WordFactory("xyz");
        //System.out.println("wf2 = " + wf2);

        GenWordPolynomialRing<GenWordPolynomial<BigInteger>> ppf = new GenWordPolynomialRing<GenWordPolynomial<BigInteger>>(
                        pf, wf2);
        //System.out.println("ppf = " + ppf);

        GenWordPolynomial<GenWordPolynomial<BigInteger>> pp = ppf.getONE();
        //System.out.println("pp = " + pp);
        assertTrue("pp == 1", pp.isONE());
        //pp = ppf.random(2);
        //System.out.println("pp = " + pp);
        pp = ppf.getZERO();
        //System.out.println("pp = " + pp);
        assertTrue("pp == 0", pp.isZERO());

        RingElem<GenWordPolynomial<GenWordPolynomial<BigInteger>>> ppe = new GenWordPolynomial<GenWordPolynomial<BigInteger>>(
                        ppf);
        //System.out.println("ppe = " + ppe);
        //System.out.println("pp.equals(ppe) = " + pp.equals(ppe) );
        //System.out.println("pp.equals(pp) = " + pp.equals(pp) );
        assertTrue("pp.equals(ppe) = ", pp.equals(ppe));
        assertTrue("pp.equals(pp) = ", pp.equals(pp));

        ppe = ppe.sum(pp); // why not pp = pp.add(ppe) ?
        //System.out.println("ppe = " + ppe);
        assertTrue("ppe.isZERO() = ", ppe.isZERO());
        pp = ppf.random(2);
        pp = pp.subtract(pp);
        //System.out.println("pp = " + pp);
        //System.out.println("pp.isZERO() = " + pp.isZERO());
        assertTrue("pp.isZERO() = ", pp.isZERO());

        // polynomials over (polynomials over (polynomials over integers))
        // non-commuting vars: uvw
        WordFactory wf3 = new WordFactory("uvw");
        //System.out.println("wf3 = " + wf3);
        GenWordPolynomialRing<GenWordPolynomial<GenWordPolynomial<BigInteger>>> pppf = new GenWordPolynomialRing<GenWordPolynomial<GenWordPolynomial<BigInteger>>>(
                        ppf, wf3);
        //System.out.println("pppf = " + pppf);

        GenWordPolynomial<GenWordPolynomial<GenWordPolynomial<BigInteger>>> ppp = pppf.getONE();
        //System.out.println("ppp = " + ppp);
        assertTrue("ppp == 1", ppp.isONE());
        //ppp = pppf.random(2);
        //System.out.println("ppp = " + ppp);
        ppp = pppf.getZERO();
        //System.out.println("ppp = " + ppp);
        assertTrue("ppp == 0", ppp.isZERO());

        RingElem<GenWordPolynomial<GenWordPolynomial<GenWordPolynomial<BigInteger>>>> pppe = new GenWordPolynomial<GenWordPolynomial<GenWordPolynomial<BigInteger>>>(
                        pppf);
        //System.out.println("pppe = " + pppe);
        // System.out.println("ppp.equals(pppe) = " + ppp.equals(pppe) );
        // System.out.println("ppp.equals(ppp) = " + ppp.equals(ppp) );
        assertTrue("ppp.equals(pppe) = ", ppp.equals(pppe));
        assertTrue("ppp.equals(ppp) = ", ppp.equals(ppp));

        pppe = pppe.sum(ppp);
        //System.out.println("pppe = " + pppe);
        assertTrue("pppe.isZERO() = ", pppe.isZERO());
        //ppp = pppf.random(2);
        ppp = ppp.subtract(ppp);
        //System.out.println("ppp = " + ppp);
        //System.out.println("ppp.isZERO() = " + ppp.isZERO());
        assertTrue("ppp.isZERO() = ", ppp.isZERO());
    }


    /**
     * Test accessors.
     */
    public void testAccessors() {
        // integers
        BigInteger rf = new BigInteger();
        // System.out.println("rf = " + rf);

        // non-commuting vars: abcdef
        WordFactory wf = new WordFactory("abcdef");
        //System.out.println("wf = " + wf);

        // polynomials over integers
        GenWordPolynomialRing<BigInteger> pf = new GenWordPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("pf = " + pf);

        // test 1
        GenWordPolynomial<BigInteger> p = pf.getONE();
        //System.out.println("p = " + p);

        Word e = p.leadingWord();
        BigInteger c = p.leadingBaseCoefficient();

        GenWordPolynomial<BigInteger> f = new GenWordPolynomial<BigInteger>(pf, c, e);
        assertEquals("1 == 1 ", p, f);

        GenWordPolynomial<BigInteger> r = p.reductum();
        assertTrue("red(1) == 0 ", r.isZERO());

        // test 0
        p = pf.getZERO();
        // System.out.println("p = " + p);
        e = p.leadingWord();
        c = p.leadingBaseCoefficient();

        f = new GenWordPolynomial<BigInteger>(pf, c, e);
        assertEquals("0 == 0 ", p, f);

        r = p.reductum();
        assertTrue("red(0) == 0 ", r.isZERO());

        // test random
        p = pf.random(kl, ll, el);
        // System.out.println("p = " + p);
        e = p.leadingWord();
        c = p.leadingBaseCoefficient();
        r = p.reductum();

        f = new GenWordPolynomial<BigInteger>(pf, c, e);
        f = r.sum(f);
        assertEquals("p == lm(f)+red(f) ", p, f);

        // test iteration over random
        GenWordPolynomial<BigInteger> g;
        g = p;
        f = pf.getZERO();
        while (!g.isZERO()) {
            e = g.leadingWord();
            c = g.leadingBaseCoefficient();
            //System.out.println("c e = " + c + " " + e);
            r = g.reductum();
            f = f.sum(c, e);
            g = r;
        }
        assertEquals("p == lm(f)+lm(red(f))+... ", p, f);
    }


    /**
     * Test addition.
     */
    public void testAddition() {
        // integers
        BigInteger rf = new BigInteger();
        // System.out.println("rf = " + rf);

        // non-commuting vars: abcdef
        WordFactory wf = new WordFactory("abcdef");
        //System.out.println("wf = " + wf);

        // polynomials over integers
        GenWordPolynomialRing<BigInteger> fac = new GenWordPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("fac = " + fac);

        GenWordPolynomial<BigInteger> a = fac.random(kl, ll, el);
        GenWordPolynomial<BigInteger> b = fac.random(kl, ll, el);

        GenWordPolynomial<BigInteger> c = a.sum(b);
        GenWordPolynomial<BigInteger> d = c.subtract(b);
        GenWordPolynomial<BigInteger> e;
        assertEquals("a+b-b = a", a, d);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        c = fac.random(kl, ll, el);
        //System.out.println("\nc = " + c);
        d = a.sum(b.sum(c));
        e = (a.sum(b)).sum(c);

        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        //System.out.println("d-e = " + d.subtract(e) );
        assertEquals("a+(b+c) = (a+b)+c", d, e);

        Word u = wf.random(rl);
        BigInteger x = rf.random(kl);

        b = new GenWordPolynomial<BigInteger>(fac, x, u);
        c = a.sum(b);
        d = a.sum(x, u);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);
        //System.out.println("\nc = " + c);
        //System.out.println("d = " + d);

        c = a.subtract(b);
        d = a.subtract(x, u);
        assertEquals("a-p(x,u) = a-(x,u)", c, d);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        //a = new GenWordPolynomial<BigInteger>(fac);
        b = new GenWordPolynomial<BigInteger>(fac, x, u);
        c = b.sum(a);
        d = a.sum(x, u);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        c = a.subtract(b);
        d = a.subtract(x, u);
        assertEquals("a-p(x,u) = a-(x,u)", c, d);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
    }


    /**
     * Test multiplication.
     */
    public void testMultiplication() {
        // integers
        BigInteger rf = new BigInteger();
        // System.out.println("rf = " + rf);

        // non-commuting vars: abcdef
        WordFactory wf = new WordFactory("abcdef");
        //System.out.println("wf = " + wf);

        // polynomials over integers
        GenWordPolynomialRing<BigInteger> fac = new GenWordPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("fac = " + fac);

        GenWordPolynomial<BigInteger> a = fac.random(kl, ll, el);
        GenWordPolynomial<BigInteger> b = fac.random(kl, ll, el);

        GenWordPolynomial<BigInteger> c = a.multiply(b);
        GenWordPolynomial<BigInteger> d = b.multiply(a);
        GenWordPolynomial<BigInteger> e;
        assertFalse("a*b != b*a", c.equals(d));
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        c = fac.random(kl, ll, el);
        //System.out.println("c = " + c);
        d = a.multiply(b.multiply(c));
        e = (a.multiply(b)).multiply(c);

        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        //System.out.println("d-e = " + d.subtract(c) );
        assertEquals("a*(b*c) = (a*b)*c", d, e);

        Word u = wf.random(rl);
        BigInteger x = rf.random(kl);

        b = new GenWordPolynomial<BigInteger>(fac, x, u);
        c = a.multiply(b);
        d = a.multiply(x, u);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        //a = new GenWordPolynomial<BigInteger>(fac);
        b = new GenWordPolynomial<BigInteger>(fac, x, u);
        c = a.multiply(b);
        d = a.multiply(x, u);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
    }


    /**
     * Test distributive law.
     */
    public void testDistributive() {
        // integers
        BigInteger rf = new BigInteger();
        // System.out.println("rf = " + rf);

        // non-commuting vars: abcdef
        WordFactory wf = new WordFactory("abcdef");
        //System.out.println("wf = " + wf);

        // polynomials over integers
        GenWordPolynomialRing<BigInteger> fac = new GenWordPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("fac = " + fac);

        GenWordPolynomial<BigInteger> a = fac.random(kl, ll, el);
        GenWordPolynomial<BigInteger> b = fac.random(kl, ll, el);
        GenWordPolynomial<BigInteger> c = fac.random(kl, ll, el);
        GenWordPolynomial<BigInteger> d, e;

        d = a.multiply(b.sum(c));
        e = a.multiply(b).sum(a.multiply(c));

        assertEquals("a(b+c) = ab+ac", d, e);
    }


    /**
     * Test univariate division.
     */
    public void testUnivDivision() {
        // rational numbers
        BigRational rf = new BigRational();
        //System.out.println("rf = " + rf);

        // non-commuting vars: x
        WordFactory wf = new WordFactory("x");
        //System.out.println("wf = " + wf);

        // polynomials over rational numbers
        GenWordPolynomialRing<BigRational> fac = new GenWordPolynomialRing<BigRational>(rf, wf);
        //System.out.println("fac = " + fac);

        GenWordPolynomial<BigRational> a = fac.random(7, ll, el).monic();
        GenWordPolynomial<BigRational> b = fac.random(7, ll, el).monic();

        GenWordPolynomial<BigRational> c = a.multiply(b);
        GenWordPolynomial<BigRational> d = b.multiply(a);
        GenWordPolynomial<BigRational> e, f;
        assertTrue("a*b == b*a", c.equals(d)); // since univariate
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        e = c.divide(a);
        //System.out.println("e = " + e);
        assertTrue("a*b/a == b", b.equals(e));
        d = c.divide(b);
        //System.out.println("d = " + d);
        assertTrue("a*b/b == a", a.equals(d));

        d = c.gcd(a);
        //System.out.println("d = " + d);
        assertTrue("gcd(a*b,a) == a", a.equals(d));

        d = a.gcd(b);
        //System.out.println("d = " + d);
        if (d.isConstant()) {
            assertTrue("gcd(b,a) == 1", d.isONE());
        } else {
            return;
        }
        d = a.modInverse(b);
        //System.out.println("d = " + d);
        e = d.multiply(a);
        //System.out.println("e = " + e);
        f = e.remainder(b);
        //System.out.println("f = " + f);
        assertTrue("d * a == 1 mod b ", f.isONE());
    }


    /**
     * Test multivariate 2 division.
     */
    public void testMulti2Division() {
        // rational numbers
        BigRational rf = new BigRational();
        // System.out.println("rf = " + rf);

        // non-commuting vars: xy
        WordFactory wf = new WordFactory("xy");
        //System.out.println("wf = " + wf);

        // polynomials over rational numbers
        GenWordPolynomialRing<BigRational> fac = new GenWordPolynomialRing<BigRational>(rf, wf);
        //System.out.println("fac = " + fac);

        GenWordPolynomial<BigRational> a = fac.random(7, ll, el).monic();
        GenWordPolynomial<BigRational> b = fac.random(7, ll, el).monic();

        GenWordPolynomial<BigRational> c = a.multiply(b);
        GenWordPolynomial<BigRational> d = b.multiply(a);
        GenWordPolynomial<BigRational> e, f;
        assertFalse("a*b == b*a", c.equals(d));
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        e = c.divide(a);
        //System.out.println("e = " + e);
        assertTrue("a*b/a == b", b.equals(e));
        f = d.divide(b);
        //System.out.println("f = " + f);
        assertTrue("a*b/b == a", a.equals(f));

        try {
            f = a.divide(b);
            //System.out.println("f = " + f);
        } catch (RuntimeException re) {
            System.out.println("a divide b fail: " + a + ", " + b);
            return;
        }

        WordFactory.WordComparator cmp = fac.alphabet.getDescendComparator();
        f = a.remainder(b);
        //System.out.println("a = " + a);
        //System.out.println("f = " + f);
        assertTrue("a rem2 b <= a", cmp.compare(a.leadingWord(), f.leadingWord()) <= 0);
    }


    /**
     * Test multivariate 3 division.
     */
    public void testMulti3Division() {
        // rational numbers
        BigRational rf = new BigRational();
        // System.out.println("rf = " + rf);

        // non-commuting vars: xyz
        WordFactory wf = new WordFactory("xyz");
        //System.out.println("wf = " + wf);

        // polynomials over rational numbers
        GenWordPolynomialRing<BigRational> fac = new GenWordPolynomialRing<BigRational>(rf, wf);
        //System.out.println("fac = " + fac);

        GenWordPolynomial<BigRational> a = fac.random(7, ll, el).monic();
        GenWordPolynomial<BigRational> b = fac.random(7, ll, el).monic();

        GenWordPolynomial<BigRational> c = a.multiply(b);
        GenWordPolynomial<BigRational> d = b.multiply(a);
        GenWordPolynomial<BigRational> e, f;
        assertFalse("a*b == b*a", c.equals(d));
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        e = c.divide(a);
        //System.out.println("e = " + e);
        assertTrue("a*b/a == b", b.equals(e));
        f = d.divide(b);
        //System.out.println("f = " + f);
        assertTrue("a*b/b == a", a.equals(f));

        try {
            f = a.divide(b);
            //System.out.println("f = " + f);
        } catch (RuntimeException re) {
            System.out.println("a divide b fail: " + a + ", " + b);
            return;
        }

        WordFactory.WordComparator cmp = fac.alphabet.getDescendComparator();
        f = a.remainder(b);
        //System.out.println("a = " + a);
        //System.out.println("f = " + f);
        assertTrue("a rem3 b <= a: " + a.leadingWord() + ", " + f.leadingWord(),
                        cmp.compare(a.leadingWord(), f.leadingWord()) <= 0);
    }

}
