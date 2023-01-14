/*
 * $Id$
 */

package edu.jas.poly;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigComplex;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.structure.RingElem;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * GenExteriorPolynomial tests with JUnit.
 * @author Heinz Kredel
 */

public class GenExteriorPolynomialTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GenExteriorPolynomialTest</CODE> object.
     * @param name String.
     */
    public GenExteriorPolynomialTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GenExteriorPolynomialTest.class);
        return suite;
    }


    int rl = 6;


    int kl = 10;


    int ll = 7;


    int el = 5;


    float ql = 0.5f;


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
        IndexList wf = IndexList.valueOf("abcdef");
        System.out.println("wf = " + wf);

        // polynomials over integers
        GenExteriorPolynomialRing<BigInteger> pf = new GenExteriorPolynomialRing<BigInteger>(rf, wf);
        System.out.println("pf = " + pf);
        assertFalse("not commutative", pf.isCommutative());
        assertTrue("associative", pf.isAssociative());
        assertFalse("not field", pf.isField());

        String s = pf.toScript();
        //System.out.println("pf.toScript: " + s + ", " + s.length());
        assertEquals("#s == 37: " + s, s.length(), 37);

        s = pf.toString();
        //System.out.println("pf.toString: " + s + ", " + s.length());
        assertEquals("#s == 37: " + s, s.length(), 37);

        GenExteriorPolynomial<BigInteger> p = pf.getONE();
        //System.out.println("p = " + p);
        assertTrue("p == 1", p.isONE());
        p = pf.getZERO();
        assertTrue("p == 0", p.isZERO());
        //System.out.println("p = " + p);
        //p = pf.random(kl, ll, el);
        //System.out.println("p = " + p);

        List<GenExteriorPolynomial<BigInteger>> gens = pf.generators();
        System.out.println("gens = " + gens);
        assertTrue("#gens == 7", gens.size() == 7);

        RingElem<GenExteriorPolynomial<BigInteger>> pe = new GenExteriorPolynomial<BigInteger>(pf);
        //System.out.println("pe = " + pe);
        //System.out.println("p.equals(pe) = " + p.equals(pe) );
        //System.out.println("p.equals(p) = " + p.equals(p) );
        assertTrue("p.equals(pe) = ", p.equals(pe));
        assertTrue("p.equals(p) = ", p.equals(p));

        pe = pe.sum(p);
        //System.out.println("pe = " + pe);
        assertTrue("pe.isZERO() = ", pe.isZERO());
        //p = pf.random(9);
        p = pf.random(kl, ll, el);
        p = p.subtract(p);
        //System.out.println("p = " + p);
        //System.out.println("p.isZERO() = " + p.isZERO());
        assertTrue("p.isZERO() = ", p.isZERO());

        // polynomials over (polynomials over integers)
        // non-commuting vars: xyz
        IndexList wf2 = IndexList.valueOf("xyz");
        //System.out.println("wf2 = " + wf2);

        GenExteriorPolynomialRing<GenExteriorPolynomial<BigInteger>> ppf;
	ppf = new GenExteriorPolynomialRing<GenExteriorPolynomial<BigInteger>>(pf, wf2);
        System.out.println("ppf = " + ppf);

        GenExteriorPolynomial<GenExteriorPolynomial<BigInteger>> pp = ppf.getONE();
        //System.out.println("pp = " + pp);
        assertTrue("pp == 1", pp.isONE());
        //pp = ppf.random(2);
        pp = ppf.random(kl, ll, el);
        //System.out.println("pp = " + pp);
        pp = ppf.getZERO();
        //System.out.println("pp = " + pp);
        assertTrue("pp == 0", pp.isZERO());

        List<GenExteriorPolynomial<GenExteriorPolynomial<BigInteger>>> pgens = ppf.generators();
        System.out.println("pgens = " + pgens);
        assertTrue("#pgens == 7+3", pgens.size() == 10);

        RingElem<GenExteriorPolynomial<GenExteriorPolynomial<BigInteger>>> ppe;
	ppe = new GenExteriorPolynomial<GenExteriorPolynomial<BigInteger>>(ppf);
        //System.out.println("ppe = " + ppe);
        //System.out.println("pp.equals(ppe) = " + pp.equals(ppe) );
        //System.out.println("pp.equals(pp) = " + pp.equals(pp) );
        assertTrue("pp.equals(ppe) = ", pp.equals(ppe));
        assertTrue("pp.equals(pp) = ", pp.equals(pp));

        ppe = ppe.sum(pp); // why not pp = pp.sum(ppe) ?
        //System.out.println("ppe = " + ppe);
        assertTrue("ppe.isZERO() = ", ppe.isZERO());
        //pp = ppf.random(2);
        pp = ppf.random(kl, ll, el);
        //System.out.println("pp = " + pp);
        pp = pp.subtract(pp);
        //System.out.println("pp.isZERO() = " + pp.isZERO());
        assertTrue("pp.isZERO() = ", pp.isZERO());

        // polynomials over (polynomials over (polynomials over integers))
        // non-commuting vars: uvw
        IndexList wf3 = IndexList.valueOf("uvw");
        //System.out.println("wf3 = " + wf3);
        GenExteriorPolynomialRing<GenExteriorPolynomial<GenExteriorPolynomial<BigInteger>>> pppf;
	pppf = new GenExteriorPolynomialRing<GenExteriorPolynomial<GenExteriorPolynomial<BigInteger>>>(ppf, wf3);
        System.out.println("pppf = " + pppf);

        GenExteriorPolynomial<GenExteriorPolynomial<GenExteriorPolynomial<BigInteger>>> ppp = pppf.getONE();
        //System.out.println("ppp = " + ppp);
        assertTrue("ppp == 1", ppp.isONE());
        //ppp = pppf.random(2);
        ppp = pppf.random(kl, ll, el);
        //System.out.println("ppp = " + ppp);
        ppp = pppf.getZERO();
        //System.out.println("ppp = " + ppp);
        assertTrue("ppp == 0", ppp.isZERO());

        List<GenExteriorPolynomial<GenExteriorPolynomial<GenExteriorPolynomial<BigInteger>>>> ppgens = pppf.generators();
        System.out.println("ppgens = " + ppgens);
        assertTrue("#ppgens == 7+3+3", ppgens.size() == 13);

        RingElem<GenExteriorPolynomial<GenExteriorPolynomial<GenExteriorPolynomial<BigInteger>>>> pppe;
	pppe = new GenExteriorPolynomial<GenExteriorPolynomial<GenExteriorPolynomial<BigInteger>>>(pppf);
        //System.out.println("pppe = " + pppe);
        // System.out.println("ppp.equals(pppe) = " + ppp.equals(pppe) );
        // System.out.println("ppp.equals(ppp) = " + ppp.equals(ppp) );
        assertTrue("ppp.equals(pppe) = ", ppp.equals(pppe));
        assertTrue("ppp.equals(ppp) = ", ppp.equals(ppp));

        pppe = pppe.sum(ppp);
        //System.out.println("pppe = " + pppe);
        assertTrue("pppe.isZERO() = ", pppe.isZERO());
        //ppp = pppf.random(2);
        ppp = pppf.random(kl, ll, el);
        //System.out.println("ppp = " + ppp);
        ppp = ppp.subtract(ppp);
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
        IndexList wf = IndexList.valueOf("abcdef");
        //System.out.println("wf = " + wf);

        // polynomials over integers
        GenExteriorPolynomialRing<BigInteger> pf = new GenExteriorPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("pf = " + pf);

        // test 1
        GenExteriorPolynomial<BigInteger> p = pf.getONE();
        //System.out.println("p = " + p);

        IndexList e = p.leadingIndexList();
        BigInteger c = p.leadingBaseCoefficient();

        GenExteriorPolynomial<BigInteger> f = new GenExteriorPolynomial<BigInteger>(pf, c, e);
        assertEquals("1 == 1 ", p, f);

        GenExteriorPolynomial<BigInteger> r = p.reductum();
        assertTrue("red(1) == 0 ", r.isZERO());

        // test 0
        p = pf.getZERO();
        // System.out.println("p = " + p);
        e = p.leadingIndexList();
        c = p.leadingBaseCoefficient();

        f = new GenExteriorPolynomial<BigInteger>(pf, c, e);
        assertEquals("0 == 0 ", p, f);

        r = p.reductum();
        assertTrue("red(0) == 0 ", r.isZERO());

        // test random
        p = pf.random(kl, ll, el);
        //System.out.println("p = " + p);
        e = p.leadingIndexList();
        c = p.leadingBaseCoefficient();
        r = p.reductum();

        f = new GenExteriorPolynomial<BigInteger>(pf, c, e);
        f = r.sum(f);
        assertEquals("p == lm(f)+red(f) ", p, f);

        // test iteration over random
        GenExteriorPolynomial<BigInteger> g;
        g = p;
        f = pf.getZERO();
        while (!g.isZERO()) {
            e = g.leadingIndexList();
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
        IndexList wf = IndexList.valueOf("abcdef");
        //System.out.println("wf = " + wf);

        // polynomials over integers
        GenExteriorPolynomialRing<BigInteger> fac = new GenExteriorPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("fac = " + fac);

        GenExteriorPolynomial<BigInteger> a = fac.random(kl, ll, el);
        GenExteriorPolynomial<BigInteger> b = fac.random(kl, ll, el);

        GenExteriorPolynomial<BigInteger> c = a.sum(b);
        GenExteriorPolynomial<BigInteger> d = c.subtract(b);
        GenExteriorPolynomial<BigInteger> e;
        assertEquals("a+b-b = a", a, d);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertTrue("deg(a+b) >= deg(a)", c.degree() >= a.degree());
        assertTrue("deg(a+b) >= deg(b)", c.degree() >= b.degree());

        c = fac.random(kl, ll, el);
        //System.out.println("\nc = " + c);
        d = a.sum(b.sum(c));
        e = (a.sum(b)).sum(c);

        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        //System.out.println("d-e = " + d.subtract(e) );
        assertEquals("a+(b+c) = (a+b)+c", d, e);

        IndexList u = wf.random(rl);
        BigInteger x = rf.random(kl);

        b = new GenExteriorPolynomial<BigInteger>(fac, x, u);
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

        //a = new GenExteriorPolynomial<BigInteger>(fac);
        b = new GenExteriorPolynomial<BigInteger>(fac, x, u);
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
        IndexList wf = IndexList.valueOf("abcdef");
        //System.out.println("wf = " + wf);

        // polynomials over integers
        GenExteriorPolynomialRing<BigInteger> fac = new GenExteriorPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("fac = " + fac);

        GenExteriorPolynomial<BigInteger> a = fac.random(kl, ll, el);
        GenExteriorPolynomial<BigInteger> b = fac.random(kl, ll, el);

        GenExteriorPolynomial<BigInteger> c = a.multiply(b);
        GenExteriorPolynomial<BigInteger> d = b.multiply(a);
        GenExteriorPolynomial<BigInteger> e;
        //assertFalse("a*b != b*a: " + a + " /\\ " + b + " == " + c, c.equals(d)); // to many fails
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertTrue("maxNorm(a*b) >= maxNorm(a)", c.maxNorm().compareTo(a.maxNorm()) >= 0);
        assertTrue("maxNorm(a*b) >= maxNorm(b)", c.maxNorm().compareTo(b.maxNorm()) >= 0);

        c = fac.random(kl, ll, el);
        //System.out.println("c = " + c);
        d = a.multiply(b.multiply(c));
        e = (a.multiply(b)).multiply(c);

        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        //System.out.println("d-e = " + d.subtract(e) );
        assertEquals("a*(b*c) = (a*b)*c:", d, e);

        IndexList u = wf.random(rl);
        BigInteger x = rf.random(kl);

        b = new GenExteriorPolynomial<BigInteger>(fac, x, u);
        c = a.multiply(b);
        d = a.multiply(x, u);
        assertEquals("a*p(x,u) = a*(x,u)", c, d);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        //a = new GenExteriorPolynomial<BigInteger>(fac);
        b = new GenExteriorPolynomial<BigInteger>(fac, x, u);
        c = a.multiply(b);
        d = a.multiply(x, u);
        assertEquals("a*p(x,u) = a*(x,u)", c, d);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        BigInteger y = rf.random(kl);
        c = a.multiply(x, y);
        //System.out.println("c = " + c);
        d = a.multiply(y, x);
        //System.out.println("d = " + d);
        assertEquals("x a y = y a x", c, d);
    }


    /**
     * Test distributive law.
     */
    public void testDistributive() {
        // integers
        BigInteger rf = new BigInteger();
        // System.out.println("rf = " + rf);

        // non-commuting vars: abcdef
        IndexList wf = IndexList.valueOf("abcdef");
        //System.out.println("wf = " + wf);

        // polynomials over integers
        GenExteriorPolynomialRing<BigInteger> fac = new GenExteriorPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("fac = " + fac);

        GenExteriorPolynomial<BigInteger> a = fac.random(kl, ll, el);
        GenExteriorPolynomial<BigInteger> b = fac.random(kl, ll, el);
        GenExteriorPolynomial<BigInteger> c = fac.random(kl, ll, el);
        GenExteriorPolynomial<BigInteger> d, e;

        d = a.multiply(b.sum(c));
        e = a.multiply(b).sum(a.multiply(c));

        assertEquals("a(b+c) = ab+ac", d, e);
    }


    /*
     * Test constructors and factory.
    @SuppressWarnings("unchecked")
    public void testParser() {
        BigInteger rf = new BigInteger();
        //System.out.println("rf = " + rf.toScriptFactory());

        // non-commuting vars: abcdef
        String[] sa = new String[] { "a", "b", "c", "d", "e", "f" };
        IndexList wf = new IndexList(sa);
        //System.out.println("wf = " + wf.toScript());

        // index list polynomials over integers
        GenExteriorPolynomialRing<BigInteger> pf = new GenExteriorPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("pf = " + pf.toScript());
        assertFalse("not commutative", pf.isCommutative());
        assertTrue("associative", pf.isAssociative());
        assertFalse("not field", pf.isField());

        List<GenExteriorPolynomial<BigInteger>> gens = pf.generators();
        //System.out.println("pf = " + gens);
        GenExteriorPolynomial<BigInteger> ga, gb, crel;
        ga = gens.get(1);
        gb = gens.get(2);
        //System.out.println("ga = " + ga + ", " + ga.toScript());
        //System.out.println("gb = " + gb);
        assertEquals("#s == 3: ", ga.toString().length(), 3);
        assertEquals("#s == 1: ", ga.toScript().length(), 1);

        crel = ga.multiply(gb).subtract(gb.multiply(ga));
        //System.out.println("crel = " + crel);

        StringReader sr = new StringReader("a b - b a, b c - c b");
        GenPolynomialTokenizer tok = new GenPolynomialTokenizer(sr);

        GenExteriorPolynomial<BigInteger> a;
        // parse of tokenizer
        try {
            a = (GenExteriorPolynomial) tok.nextWordPolynomial(pf);
        } catch (IOException e) {
            a = null;
            e.printStackTrace();
        }
        //System.out.println("a = " + a);
        assertEquals("parse() == ab - ba: ", a, crel);

        // now parse of factory
        a = pf.parse("a b - b a");
        //System.out.println("a = " + a);
        assertEquals("parse() == ab - ba: ", a, crel);

        // polynomials over integers
        GenPolynomialRing<BigInteger> fac = new GenPolynomialRing<BigInteger>(rf, sa);
        //System.out.println("fac = " + fac.toScript());
        assertTrue("commutative", fac.isCommutative());
        assertTrue("associative", fac.isAssociative());
        assertFalse("not field", fac.isField());

        sr = new StringReader("a b - b a, b c - c b");
        tok = new GenPolynomialTokenizer(fac, sr);
        // parse of tokenizer
        try {
            a = (GenExteriorPolynomial) tok.nextWordPolynomial();
        } catch (IOException e) {
            a = null;
            e.printStackTrace();
        }
        //System.out.println("a = " + a);
        assertEquals("parse() == ab - ba: ", a, crel);
    }
     */


    /**
     * Test iterators.
     */
    public void testIterators() {
        // integers
        BigInteger rf = new BigInteger();
        //System.out.println("rf = " + rf);

        // index list polynomials over integral numbers
        GenExteriorPolynomialRing<BigInteger> pf = new GenExteriorPolynomialRing<BigInteger>(rf, "abcdef");
        //System.out.println("pf = " + pf);

        // random polynomial
        GenExteriorPolynomial<BigInteger> p = pf.random(kl, 2 * ll, el);
        //System.out.println("p = " + p);

        // test monomials
        for (IndexListMonomial<BigInteger> m : p) {
            //System.out.println("m = " + m);
            assertFalse("m.c == 0 ", m.coefficient().isZERO());
            assertFalse("m.e == 0 ", m.indexlist().isZERO());
        }
    }

}
