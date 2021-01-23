/*
 * $Id$
 */

package edu.jas.poly;


import java.util.List;
import java.util.ArrayList;
import java.io.StringReader;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.BigComplex;
import edu.jas.structure.RingElem;


/**
 * GenWordPolynomial tests with JUnit.
 * @author Heinz Kredel
 */

public class GenWordPolynomialTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
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
        assertFalse("not commutative",pf.isCommutative());
        assertTrue("associative",pf.isAssociative());
        assertFalse("not field",pf.isField());

        GenWordPolynomial<BigInteger> p = pf.getONE();
        //System.out.println("p = " + p);
        assertTrue("p == 1", p.isONE());
        p = pf.getZERO();
        assertTrue("p == 0", p.isZERO());
        //System.out.println("p = " + p);
        //p = pf.random(9);
        //System.out.println("p = " + p);

        List<GenWordPolynomial<BigInteger>> gens = pf.generators();
        //System.out.println("gens = " + gens);
        assertTrue("#gens == 7", gens.size() == 7);

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

        GenWordPolynomialRing<GenWordPolynomial<BigInteger>> 
            ppf = new GenWordPolynomialRing<GenWordPolynomial<BigInteger>>(pf, wf2);
        //System.out.println("ppf = " + ppf);

        GenWordPolynomial<GenWordPolynomial<BigInteger>> pp = ppf.getONE();
        //System.out.println("pp = " + pp);
        assertTrue("pp == 1", pp.isONE());
        //pp = ppf.random(2);
        //System.out.println("pp = " + pp);
        pp = ppf.getZERO();
        //System.out.println("pp = " + pp);
        assertTrue("pp == 0", pp.isZERO());

        List<GenWordPolynomial<GenWordPolynomial<BigInteger>>> pgens = ppf.generators();
        //System.out.println("pgens = " + pgens);
        assertTrue("#pgens == 7+3", pgens.size() == 10);

        RingElem<GenWordPolynomial<GenWordPolynomial<BigInteger>>> 
            ppe = new GenWordPolynomial<GenWordPolynomial<BigInteger>>(ppf);
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
        GenWordPolynomialRing<GenWordPolynomial<GenWordPolynomial<BigInteger>>> 
            pppf = new GenWordPolynomialRing<GenWordPolynomial<GenWordPolynomial<BigInteger>>>(ppf, wf3);
        //System.out.println("pppf = " + pppf);

        GenWordPolynomial<GenWordPolynomial<GenWordPolynomial<BigInteger>>> ppp = pppf.getONE();
        //System.out.println("ppp = " + ppp);
        assertTrue("ppp == 1", ppp.isONE());
        //ppp = pppf.random(2);
        //System.out.println("ppp = " + ppp);
        ppp = pppf.getZERO();
        //System.out.println("ppp = " + ppp);
        assertTrue("ppp == 0", ppp.isZERO());

        List<GenWordPolynomial<GenWordPolynomial<GenWordPolynomial<BigInteger>>>> ppgens = pppf.generators();
        //System.out.println("ppgens = " + ppgens);
        assertTrue("#ppgens == 7+3+3", ppgens.size() == 13);

        RingElem<GenWordPolynomial<GenWordPolynomial<GenWordPolynomial<BigInteger>>>> 
            pppe = new GenWordPolynomial<GenWordPolynomial<GenWordPolynomial<BigInteger>>>(pppf);
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


    /**
     * Test polynomial and solvable coefficients.
     */
    public void testCoefficients() {
        // integers
        BigComplex rf = new BigComplex();
        //System.out.println("rf = " + rf);

        // commuting vars: uvw
        String[] cvar = new String[] { "u", "v", "w" };
        GenPolynomialRing<BigComplex> cf = new GenPolynomialRing<BigComplex>(rf, cvar);
        //System.out.println("cf = " + cf);

        // solvable vars: x1 x2 y1 y2
        String[] svar = new String[] { "x1", "x2", "y1", "y2" };
        GenSolvablePolynomialRing<GenPolynomial<BigComplex>> sf;
        sf = new GenSolvablePolynomialRing<GenPolynomial<BigComplex>>(cf, svar);
        //System.out.println("sf = " + sf);
        RelationGenerator<GenPolynomial<BigComplex>> wr = new WeylRelations<GenPolynomial<BigComplex>>();
        wr.generate(sf);
        //System.out.println("sf = " + sf);

        // non-commuting vars: abcdef
        WordFactory wf = new WordFactory("abcdef");
        //System.out.println("wf = " + wf);
        // non-commuting polynomials over commuting and solvable coefficients
        GenWordPolynomialRing<GenPolynomial<GenPolynomial<BigComplex>>> nf;
        nf = new GenWordPolynomialRing<GenPolynomial<GenPolynomial<BigComplex>>>(sf, wf);
        //want: GenWordPolynomialRing<GenSolvablePolynomial<GenPolynomial<BigComplex>>> nf;
        //System.out.println("nf = " + nf.toScript());

        assertFalse("not commutative",nf.isCommutative());
        assertTrue("associative",nf.isAssociative());
        assertFalse("not field",nf.isField());

        GenWordPolynomial<GenPolynomial<GenPolynomial<BigComplex>>> p = nf.getONE();
        //System.out.println("p = " + p);
        assertTrue("p == 1", p.isONE());
        p = nf.getZERO();
        //System.out.println("p = " + p);
        assertTrue("p == 0", p.isZERO());
        p = nf.random(3);
        //System.out.println("p = " + p);
        //p = p.sum(p);
        p = p.multiply(p);
        //System.out.println("p = " + p);
        p = p.subtract(p);
        //System.out.println("p = " + p);
        assertTrue("p == 0", p.isZERO());

        List<GenWordPolynomial<GenPolynomial<GenPolynomial<BigComplex>>>> gens = nf.generators();
        //System.out.println("gens = " + gens);
        assertTrue("#gens == 2+3+4+6", gens.size() == 15);
    }

    
    /**
     * Test contraction.
     */
    public void testContraction() {
        // integers
        BigInteger rf = new BigInteger();
        //System.out.println("rf = " + rf);

        // non-commuting vars: abcdef
        WordFactory wf = new WordFactory("abcdef");
        //System.out.println("wf = " + wf);
        WordFactory wfs = new WordFactory("abc");
        //System.out.println("wf = " + wf);

        // polynomials over integers
        GenWordPolynomialRing<BigInteger> pf = new GenWordPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("pf = " + pf);
        GenWordPolynomialRing<BigInteger> pfs = new GenWordPolynomialRing<BigInteger>(rf, wfs);
        //System.out.println("pfs = " + pfs);

        List<GenWordPolynomial<BigInteger>> H = new ArrayList<GenWordPolynomial<BigInteger>>();

        GenWordPolynomial<BigInteger> a = pf.random(5).abs();
        //System.out.println("a = " + a);
        GenWordPolynomial<BigInteger> as = pfs.random(5).abs();
        //System.out.println("as = " + as);
        GenWordPolynomial<BigInteger> asf = pf.valueOf(as);
        H.add(asf);
        H.add(asf.multiply(pf.valueOf(pfs.random(5).abs())));
        H.add(pfs.random(5).abs());
        //System.out.println("asf = " + asf);
        GenWordPolynomial<BigInteger> asfc = asf.contract(pfs);
        //System.out.println("asfc = " + asfc);
        assertEquals("as == contract(extend(as)): ", as, asfc);

        // mostly not contractable
        GenWordPolynomial<BigInteger> ac = a.contract(pfs);
        H.add(a);
        //System.out.println("ac = " + ac);
        assertTrue("contract(a) == 0: " + ac, ac.isZERO() || pf.valueOf(ac).equals(a)); 

        // 1 always contractable
        a = pf.getONE();
        H.add(a);
        ac = a.contract(pfs);
        //System.out.println("ac = " + ac);
        assertTrue("contract(1) == 1: ", ac.isONE());

        // now contract lists of word polynomials
        //System.out.println("H = " + H);
        List<GenWordPolynomial<BigInteger>> M = PolyUtil.<BigInteger> intersect(pfs,H);
        //System.out.println("M = " + M);
        int i = 0;
        for (GenWordPolynomial<BigInteger> h : H) {
            if (!h.contract(pfs).isZERO()) {
                assertEquals("extend(contract(h)) == h: " + h, h, pf.valueOf(M.get(i++)) ); 
            }
        }
    }


    /**
     * Test constructors and factory.
     */
    @SuppressWarnings("unchecked")
    public void testParser() {
        BigInteger rf = new BigInteger();
        //System.out.println("rf = " + rf.toScriptFactory());

        // non-commuting vars: abcdef
        String[] sa = new String[]{"a", "b", "c", "d", "e", "f"};
        WordFactory wf = new WordFactory(sa);
        //System.out.println("wf = " + wf.toScript());

        // word polynomials over integers
        GenWordPolynomialRing<BigInteger> pf = new GenWordPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("pf = " + pf.toScript());
        assertFalse("not commutative",pf.isCommutative());
        assertTrue("associative",pf.isAssociative());
        assertFalse("not field",pf.isField());

        List<GenWordPolynomial<BigInteger>> gens = pf.generators();
        //System.out.println("pf = " + gens);
        GenWordPolynomial<BigInteger> ga, gb, crel;
	ga = gens.get(1);
	gb = gens.get(2);
        //System.out.println("ga = " + ga);
        //System.out.println("gb = " + gb);
	crel = ga.multiply(gb).subtract(gb.multiply(ga));
        //System.out.println("crel = " + crel);

        StringReader sr = new StringReader("a b - b a, b c - c b");
        GenPolynomialTokenizer tok = new GenPolynomialTokenizer(sr);

        GenWordPolynomial<BigInteger> a;
        // parse of tokenizer
        try {
            a = (GenWordPolynomial) tok.nextWordPolynomial(pf);
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
        assertTrue("commutative",fac.isCommutative());
        assertTrue("associative",fac.isAssociative());
        assertFalse("not field",fac.isField());

        sr = new StringReader("a b - b a, b c - c b");
        tok = new GenPolynomialTokenizer(fac,sr);
        // parse of tokenizer
        try {
            a = (GenWordPolynomial) tok.nextWordPolynomial();
        } catch (IOException e) {
            a = null;
            e.printStackTrace();
        }
        //System.out.println("a = " + a);
        assertEquals("parse() == ab - ba: ", a, crel);
    }

}
