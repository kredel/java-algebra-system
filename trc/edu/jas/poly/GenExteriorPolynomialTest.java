/*
 * $Id$
 */

package edu.jas.poly;


import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenMatrixRing;
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


    int kl = 8;


    int ll = 6;


    int el = 4;


    float ql = 0.4f;


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

        // 6 non-commuting vars
        IndexFactory wf = new IndexFactory(6);
        //System.out.println("wf = " + wf);

        // polynomials over integers
        GenExteriorPolynomialRing<BigInteger> pf = new GenExteriorPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("pf = " + pf);
        assertFalse("not commutative", pf.isCommutative());
        assertTrue("associative", pf.isAssociative());
        assertFalse("not field", pf.isField());
        assertEquals("pf == pf: ", pf, pf);

        String s = pf.toScript();
        //System.out.println("pf.toScript: " + s + ", " + s.length());
        assertEquals("#s == 35: " + s, s.length(), 35);

        s = pf.toString();
        //System.out.println("pf.toString: " + s + ", " + s.length());
        assertEquals("#s == 40: " + s, s.length(), 40);

        GenExteriorPolynomial<BigInteger> p = pf.getONE();
        //System.out.println("p = " + p);
        assertTrue("p == 1", p.isONE());
        p = pf.getZERO();
        assertTrue("p == 0", p.isZERO());
        //System.out.println("p = " + p);
        //p = pf.random(kl, ll, el);
        //System.out.println("p = " + p);

        List<GenExteriorPolynomial<BigInteger>> gens = pf.generators();
        //System.out.println("gens = " + gens);
        assertTrue("#gens == 7", gens.size() == 7);

        gens = pf.getGenerators();
        //System.out.println("gens = " + gens);
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
        // 3 non-commuting vars
        IndexFactory wf2 = new IndexFactory(3);
        //System.out.println("wf2 = " + wf2);

        GenExteriorPolynomialRing<GenExteriorPolynomial<BigInteger>> ppf;
        ppf = new GenExteriorPolynomialRing<GenExteriorPolynomial<BigInteger>>(pf, wf2);
        //System.out.println("ppf = " + ppf);

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
        //System.out.println("pgens = " + pgens);
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
        // 3 non-commuting vars
        IndexFactory wf3 = new IndexFactory(3);
        //System.out.println("wf3 = " + wf3);
        GenExteriorPolynomialRing<GenExteriorPolynomial<GenExteriorPolynomial<BigInteger>>> pppf;
        pppf = new GenExteriorPolynomialRing<GenExteriorPolynomial<GenExteriorPolynomial<BigInteger>>>(ppf,
                        wf3);
        //System.out.println("pppf = " + pppf);

        GenExteriorPolynomial<GenExteriorPolynomial<GenExteriorPolynomial<BigInteger>>> ppp = pppf.getONE();
        //System.out.println("ppp = " + ppp);
        assertTrue("ppp == 1", ppp.isONE());
        //ppp = pppf.random(2);
        ppp = pppf.random(kl, ll, el);
        //System.out.println("ppp = " + ppp);
        ppp = pppf.getZERO();
        //System.out.println("ppp = " + ppp);
        assertTrue("ppp == 0", ppp.isZERO());

        List<GenExteriorPolynomial<GenExteriorPolynomial<GenExteriorPolynomial<BigInteger>>>> ppgens = pppf
                        .generators();
        //System.out.println("ppgens = " + ppgens);
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

        // 6 non-commuting vars
        IndexFactory wf = new IndexFactory(6);
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

        // 6 non-commuting vars
        IndexFactory wf = new IndexFactory(6);
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

        // 6 non-commuting vars
        IndexFactory wf = new IndexFactory(6);
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

        IndexList v = wf.random(rl);
        c = a.multiply(x, u, y, v);
        //System.out.println("c = " + c);
        d = a.multiply(y, u, x, v);
        //System.out.println("d = " + d);
        assertEquals("x u a y v = y u a x v", c, d);

        c = a.multiply(u, v);
        //System.out.println("c = " + c);
        d = a.multiply(rf.getONE(), u, v);
        //System.out.println("d = " + d);
        assertEquals("u a v = 1 u a v", c, d);
    }


    /**
     * Test distributive law.
     */
    public void testDistributive() {
        // integers
        BigInteger rf = new BigInteger();
        // System.out.println("rf = " + rf);

        // 6 non-commuting vars
        IndexFactory wf = new IndexFactory(6);
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


    /**
     * Test division.
     */
    public void testDivision() {
        // rational
        BigRational rf = new BigRational();
        // System.out.println("rf = " + rf);

        // 6 non-commuting vars
        IndexFactory wf = new IndexFactory(6);
        //System.out.println("wf = " + wf);

        // polynomials over integers
        GenExteriorPolynomialRing<BigRational> fac = new GenExteriorPolynomialRing<BigRational>(rf, wf);
        //System.out.println("fac = " + fac);

        GenExteriorPolynomial<BigRational> a = fac.random(kl+2, ll, el);
        GenExteriorPolynomial<BigRational> b = fac.random(kl-1, ll, 2);
        GenExteriorPolynomial<BigRational> c, d, e;

        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        //c = fac.getZERO();
        //d = a;
        GenExteriorPolynomial<BigRational>[] qr = a.quotientRemainder(fac.getONE());
        c = qr[0];
        d = qr[1];
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        e = c.multiply(fac.getONE()).sum(d);
        //System.out.println("e = " + e);
        assertEquals("a = 0 a + a: ", a, e);

        //c = fac.getONE();
        //d = fac.getZERO();
        qr = a.quotientRemainder(b);
        c = qr[0];
        d = qr[1];
        //System.out.println("q = " + c);
        //System.out.println("r = " + d);
        e = c.multiply(b).sum(d);
        //System.out.println("q b = " + c.multiply(b));
        //System.out.println("e = " + e);
        assertEquals("a = q b + r: ", a, e);
    }


    /*
     * Test constructors and factory.
     */
    @SuppressWarnings("unchecked")
    public void testParser() {
        BigInteger rf = new BigInteger();
        //System.out.println("rf = " + rf.toScriptFactory());

        // commuting vars: abcdef
        String[] sa = new String[] { "a", "b", "c", "d", "e", "f" };
        //String ss = "E(1,2,3,4,5,6)";
        IndexFactory wf = new IndexFactory(6);
        //System.out.println("wf = " + wf.toScript());

        // index list polynomials over integers
        GenExteriorPolynomialRing<BigInteger> pf = new GenExteriorPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("pf = " + pf.toScript());
        assertFalse("not commutative", pf.isCommutative());
        assertTrue("associative", pf.isAssociative());
        assertFalse("not field", pf.isField());

        List<GenExteriorPolynomial<BigInteger>> gens = pf.generators();
        //System.out.println("gens = " + gens);
        GenExteriorPolynomial<BigInteger> g1, g2, g3, g4, epol;
        g1 = gens.get(1);
        g2 = gens.get(2);
        g3 = gens.get(3);
        g4 = gens.get(4);
        //System.out.println("g1 = " + g1 + ", " + g1.toScript());
        //System.out.println("g2 = " + g2);
        assertEquals("#s == 5: ", g1.toString().length(), 5);
        assertEquals("#s == 4: ", g2.toScript().length(), 4);

        epol = g1.multiply(g2).subtract(g3.multiply(g4));
        //System.out.println("epol = " + epol);

        //StringReader sr = new StringReader("1 E(1,2)**2 + E(1,2) - 1 E(3,4)**0");
        StringReader sr = new StringReader("1 E(1,2) - 1 E(3,4)");
        GenPolynomialTokenizer tok = new GenPolynomialTokenizer(sr);

        GenExteriorPolynomial<BigInteger> a, b;
        // parse of tokenizer
        try {
            a = (GenExteriorPolynomial) tok.nextExteriorPolynomial(pf);
        } catch (IOException e) {
            a = null;
            e.printStackTrace();
        }
        //System.out.println("a = " + a);
        assertEquals("parse() == ab - ba: ", a, epol);

        // now parse of factory
        a = pf.parse("1 E(1,2) - 1 E(3,4)");
        //System.out.println("a = " + a);
        assertEquals("parse() == 1 E(1,2) - 1 E(3,4): ", a, epol);

        // commutative polynomials over integers
        GenPolynomialRing<BigInteger> fac = new GenPolynomialRing<BigInteger>(rf, sa);
        //System.out.println("fac = " + fac.toScript());
        assertTrue("commutative", fac.isCommutative());
        assertTrue("associative", fac.isAssociative());
        assertFalse("not field", fac.isField());

        sr = new StringReader("E(1,2) - E(3,4)");
        tok = new GenPolynomialTokenizer(fac, sr);
        // parse exterior with tokenizer
        try {
            a = (GenExteriorPolynomial) tok.nextExteriorPolynomial();
        } catch (IOException e) {
            a = null;
            e.printStackTrace();
        }
        //System.out.println("a = " + a);
        assertEquals("parse() == E(1,2) - E(3,4): ", a, epol);
    }


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


    /**
     * Test matrix and vector.
     */
    public void testMatrix() {
        // rational numbers
        BigRational rf = new BigRational();
        // System.out.println("rf = " + rf);

        // matrix size
        int c = 13; // to big 23;
        IndexFactory ixfac = new IndexFactory(c);
        //System.out.println("ixfac = " + ixfac);

        // exterior polynomials over rational numbers
        GenExteriorPolynomialRing<BigRational> epf;
        epf = new GenExteriorPolynomialRing<BigRational>(rf, ixfac);
        //System.out.println("epf = " + epf.toScript());

        // matrix over rational numbers
        int r = c;
        GenMatrixRing<BigRational> mf = new GenMatrixRing<BigRational>(rf, r, r);
        //System.out.println("mf = " + mf.toScript());

        GenMatrix<BigRational> A = mf.getONE();
        //System.out.println("A = " + A);
        List<GenExteriorPolynomial<BigRational>> em = epf.fromMatrix(A);
        //System.out.println("em = " + em);
        assertEquals("#em == #A: ", em.size(), A.ring.rows);
        BigRational dr = epf.determinant(em);
        //System.out.println("dr = " + dr);
        assertTrue("det(em) == 1: ", dr.isONE());

        A = mf.getZERO();
        //System.out.println("A = " + A);
        em = epf.fromMatrix(A);
        //System.out.println("em = " + em);
        assertEquals("#em == #A: ", em.size(), 0);
        dr = epf.determinant(em);
        //System.out.println("dr = " + dr);
        assertTrue("det(em) == 0: ", dr.isZERO());

        A = mf.randomUpper(3, 0.55f);
        //System.out.println("A = " + A);
        em = epf.fromMatrix(A);
        //System.out.println("em = " + em);
        assertTrue("#em <= #A: ", em.size() <= A.matrix.size());
        dr = epf.determinant(em);
        //System.out.println("dr = " + dr);
        assertTrue("det(em) == 0: ", dr.isZERO());

        A = mf.random(5, 0.67f);
        //System.out.println("A = " + A);
        em = epf.fromMatrix(A);
        //System.out.println("em = " + em);
        assertTrue("#em <= #A: ", em.size() <= A.matrix.size());
        dr = epf.determinant(em);
        //System.out.println("dr = " + dr);
        assertFalse("det(em) != 0: ", dr.isZERO());

        GenPolynomialRing<BigRational> fac;
        fac = new GenPolynomialRing<BigRational>(rf, new String[] { "lambda" });
        //System.out.println("fac = " + fac.toScript());

        //System.out.println("A = " + A);
        //GenPolynomial<BigRational> cp = fac.charPolynomial(A);
        //System.out.println("cp = " + cp);
        //BigRational er = fac.determinantFromCharPol(cp);

        BigRational er = fac.determinant(A);
        //System.out.println("\ner = " + er);
        //System.out.println("dr = " + dr);
        assertEquals("det_exterior(A) == det_charpol(A): ", dr, er);
    }


    /*
     * Test conversions.
     */
    public void testConversion() {
        BigRational rf = new BigRational();
        //System.out.println("rf = " + rf.toScriptFactory());

        IndexFactory wf = new IndexFactory(0,20);
        //System.out.println("wf = " + wf.toScript());

        // exterior polynomials over integers
        GenExteriorPolynomialRing<BigRational> pf;
        pf = new GenExteriorPolynomialRing<BigRational>(rf, wf);
        //System.out.println("pf = " + pf.toScript());

        // commutative polynomials over rationals
        GenPolynomialRing<BigRational> fac;
        fac = new GenPolynomialRing<BigRational>(rf, new String[] { "a", "b", "c", "d" });
        //System.out.println("fac = " + fac.toScript());

        GenPolynomial<BigRational> p = fac.random(kl/2, ll, 2, ql);
        //System.out.println("p = " + p);

        GenExteriorPolynomial<BigRational> a = pf.valueOf(p);
        //System.out.println("a = " + a);
        List<GenPolynomial<BigRational>> Pl = new ArrayList<GenPolynomial<BigRational>>();
        Pl.add(p);
        List<GenExteriorPolynomial<BigRational>> El = pf.valueOf(Pl);
        assertEquals("a == El[0]: ", a, El.get(0));
        assertEquals("sumNorm(p) == sumNorm(a): ", p.sumNorm(), a.sumNorm());

        GenExteriorPolynomial<BigRational> b = pf.valueOf(a);
        //System.out.println("b = " + b);
        assertEquals("a == b: ", a, b);

        GenExteriorPolynomial<BigRational> c = pf.fromInteger(7);
        //System.out.println("c = " + c);
        assertEquals("7 == ldcf(c): ", rf.fromInteger(7), c.leadingBaseCoefficient());
    }


    /*
     * Test resultant.
     */
    public void testResultant() {
        BigRational rf = new BigRational();
        //System.out.println("rf = " + rf.toScriptFactory());
        IndexFactory wf = new IndexFactory(0,20);
        //System.out.println("wf = " + wf.toScript());

        // exterior polynomials over rationals
        GenExteriorPolynomialRing<BigRational> pf;
        pf = new GenExteriorPolynomialRing<BigRational>(rf, wf);
        //System.out.println("pf = " + pf.toScript());

        // commutative univariate polynomials over rationals
        GenPolynomialRing<BigRational> fac;
        fac = new GenPolynomialRing<BigRational>(rf, new String[] { "i" });
        //System.out.println("fac = " + fac.toScript());

        GenPolynomial<BigRational> p = fac.random(kl/2, ll, el, ql);
        //System.out.println("p = " + p);
        GenPolynomial<BigRational> q = fac.random(kl/2, ll, el, ql);
        //System.out.println("q = " + q);
        BigRational r = pf.resultant(p, q);
        //System.out.println("r = " + r);

        GenPolynomial<BigRational> g = p.gcd(q);
        //System.out.println("g = " + g);
        assertTrue("res != 0 && gcd == 1: " + r + ", " + g, !r.isZERO() && g.isONE());

        //System.out.println("fac.gens = " + fac.generators());
        GenPolynomial<BigRational> f = fac.random(kl/3, ll, el/2, ql);
        f = f.sum( fac.generators().get(1) );
        //System.out.println("f = " + f);
        p = p.multiply(f);
        q = q.multiply(f);
        //System.out.println("p = " + p);
        //System.out.println("q = " + q);
        r = pf.resultant(p, q);
        //System.out.println("r = " + r);

        g = p.gcd(q);
        //System.out.println("g = " + g);
        assertTrue("res == 0 && gcd != 1: " + r + ", " + g, r.isZERO() && !g.isONE());
    }


    /*
     * Test old example after Blonski, 1983.
     */
    @SuppressWarnings("unchecked")
    public void xtestExample() {
        BigInteger rf = new BigInteger();
        //System.out.println("rf = " + rf.toScriptFactory());

        // non-commuting indexes: 1 2 3 4
        //String ss = "E(1,2,3,4)";
        IndexFactory wf = new IndexFactory(4); // (1,4)
        //System.out.println("wf = " + wf.toScript());

        // index list polynomials over integers
        GenExteriorPolynomialRing<BigInteger> pf;
        pf = new GenExteriorPolynomialRing<BigInteger>(rf, wf);
        //System.out.println("pf = " + pf.toScript());
        assertFalse("not commutative", pf.isCommutative());
        assertTrue("associative", pf.isAssociative());
        assertFalse("not field", pf.isField());

        GenExteriorPolynomial<BigInteger> emaxd, p1, p2, q1, q2, s, g1, g2, e1, e2,
            e1dual, e2dual, q, qs, qt, g1dual, g2dual, s1, s2;
        // parse points in 4-space as polynomials
        emaxd = pf.parse("E(1,2,3,4)"); // wf.imax 
        System.out.println("emaxd = " + emaxd + ", imax = " + pf.ixfac.imax);
        p1 = pf.parse("1 E(1) + 5 E(2) - 2 E(3) + 1 E(4)");
        p2 = pf.parse("4 E(1) + 3 E(2) + 6 E(3) + 1 E(4)");
        System.out.println("p1 = " + p1);
        System.out.println("p2 = " + p2);
        q1 = pf.parse("3 E(1) - 2 E(2) - 1 E(3) + 1 E(4)");
        q2 = pf.parse("1 E(2) + 5 E(3) + 1 E(4)");
        System.out.println("q1 = " + q1);
        System.out.println("q2 = " + q2);
        s = pf.parse("1 E(3) + 1 E(4)");
        System.out.println("s = " + s);

        // compute line(gerade) p1..p2 and q1..q2
        g1 = p1.multiply(p2).abs();
        g2 = q1.multiply(q2).abs().divide(new BigInteger(3));
        System.out.println("g1 = p1 /\\ p2 = " + g1);
        System.out.println("g2 = q1 /\\ q2 = " + g2);
        System.out.println("pp(g2) = " + q1.multiply(q2).coeffPrimitivePart());
        assertEquals("g2 == pp(g2): ", g2, q1.multiply(q2).coeffPrimitivePart());

        // compute plane(ebene) g1..s and g2..s
        e1 = g1.multiply(s).abs().divide(new BigInteger(17));
        e2 = g2.multiply(s);
        System.out.println("e1 = g1 /\\ s = " + e1);
        System.out.println("e2 = g2 /\\ s = " + e2);
        assertEquals("e1 == pp(e1): ", e1, g1.multiply(s).coeffPrimitivePart());

        // compute dual planes of e1, e2 as e1..emaxd and e2..emaxd
        e1dual = e1.interiorRightProduct(emaxd).abs();
        e2dual = e2.interiorRightProduct(emaxd).abs();
        System.out.println("e1dual = e1 |_ emaxd = " + e1dual);
        System.out.println("e2dual = e2 |_ emaxd = " + e2dual);

        // compute intersection of plane e1, e2 via dual plane sum
        q = e1dual.multiply(e2dual).abs().divide(new BigInteger(5));
        System.out.println("q  = (e1dual /\\ e2dual) = " + q);
        assertEquals("q == pp(q): ", q, e1dual.multiply(e2dual).coeffPrimitivePart());
        qs = q.interiorRightProduct(emaxd).abs();
        System.out.println("qs = (e1dual /\\ e2dual) |_ emaxd = " + qs);
        qt = e1.interiorLeftProduct(e2dual).abs().divide(new BigInteger(5));
        System.out.println("qt = e1 _| e2dual                = " + qt);
        assertEquals("qt == pp(qt): ", qt, e1.interiorLeftProduct(e2dual).coeffPrimitivePart());
        assertEquals("qs == qt: ", qs, qt);

        // compute dual line(gerade) of g1, g2
        g1dual = g1.interiorRightProduct(emaxd);
        g2dual = g2.interiorRightProduct(emaxd).abs();
        System.out.println("g1dual = g1 |_ emaxd = " + g1dual);
        System.out.println("g2dual = g2 |_ emaxd = " + g2dual);

        // compute intersection of g1..e2 and g2..e1
        s1 = e2.interiorLeftProduct(g1dual).abs().divide(new BigInteger(5));
        System.out.println("s1 = e2 _| g1dual = " + s1);
        s2 = e1.interiorLeftProduct(g2dual).abs().divide(new BigInteger(5));
        System.out.println("s2 = e1 _| g2dual = " + s2);

        // check intersection of s..qs, qs..e1 and qs..e2
        System.out.println(" s /\\ qs =  s \\in qs = " + s.multiply(qs));
        System.out.println("qs /\\ e1 = qs \\in e1 = " + qs.multiply(e1));
        System.out.println("qs /\\ e2 = qs \\in e2 = " + qs.multiply(e2));
        assertTrue("qs /\\ s == 0: ", qs.multiply(s).isZERO());
        assertTrue("qs /\\ e1 == 0: ", qs.multiply(e1).isZERO());
        assertTrue("qs /\\ e2 == 0: ", qs.multiply(e2).isZERO());
    }


    /**
     * Test derivation.
     */
    @SuppressWarnings("unchecked")
    public void testDerivation() {
        // rationals
        BigRational rf = new BigRational();
        System.out.println("rf = " + rf.toScriptFactory());

        // 3/6 commuting vars
        //IndexFactory wf = new IndexFactory(6);
        //String[] vars = new String[]{ "a", "b", "c", "d", "e", "f" };
        String[] vars = new String[]{ "x1", "x2", "x3" };
        System.out.println("vars = " + Arrays.toString(vars));

        // polynomials over rationals
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(rf, vars);
        System.out.println("pf = " + pf);
        assertTrue("commutative", pf.isCommutative());
        assertTrue("associative", pf.isAssociative());
        assertFalse("not field", pf.isField());
        assertEquals("pf == pf: ", pf, pf);

        String s = pf.toScript();
        System.out.println("pf.toScript: " + s + ", " + s.length());
        assertEquals("#s == 38: " + s, s.length(), 38);

        s = pf.toString();
        System.out.println("pf.toString: " + s + ", " + s.length());
        assertEquals("#s == 31: " + s, s.length(), 31);

        GenPolynomial<BigRational> p = pf.getONE();
        System.out.println("p = " + p);
        assertTrue("p == 1", p.isONE());
        p = pf.getZERO();
        assertTrue("p == 0", p.isZERO());
        System.out.println("p = " + p);
        //p = pf.random(kl, ll, el);
        //System.out.println("p = " + p);

        List<GenPolynomial<BigRational>> gens = pf.generators();
        System.out.println("gens = " + gens);
        assertTrue("#gens == 4", gens.size() == 4);

        gens = pf.getGenerators();
        System.out.println("gens = " + gens);
        assertTrue("#gens == 4", gens.size() == 4);

        RingElem<GenPolynomial<BigRational>> pe = new GenPolynomial<BigRational>(pf);
        System.out.println("pe = " + pe);
        //System.out.println("pe.equals(pe) = " + pe.equals(pe) );
        //System.out.println("p.equals(p) = " + p.equals(p) );
        assertTrue("p.equals(pe) = ", p.equals(pe));
        assertTrue("p.equals(p) = ", p.equals(p));

        pe = pe.sum(p);
        System.out.println("pe = " + pe);
        assertTrue("pe.isZERO() = ", pe.isZERO());
        //p = pf.random(9);
        p = pf.random(kl, ll, el, ql);
        p = p.subtract(p);
        System.out.println("p = " + p);
        System.out.println("p.isZERO() = " + p.isZERO());
        assertTrue("p.isZERO() = ", p.isZERO());


        // exterior polynomials over (polynomials over rationals)
        // 3 non-commuting vars
        IndexFactory wf2 = new IndexFactory(3);
        System.out.println("wf2 = " + wf2);

        GenExteriorPolynomialRing<GenPolynomial<BigRational>> ppf;
        ppf = new GenExteriorPolynomialRing<GenPolynomial<BigRational>>(pf, wf2);
        System.out.println("ppf = " + ppf.toScript());

        GenExteriorPolynomial<GenPolynomial<BigRational>> pp = ppf.getONE();
        System.out.println("pp = " + pp);
        assertTrue("pp == 1", pp.isONE());
        //pp = ppf.random(2);
        pp = ppf.random(kl, ll, el);
        System.out.println("pp = " + pp);
        pp = ppf.getZERO();
        System.out.println("pp = " + pp);
        assertTrue("pp == 0", pp.isZERO());

        List<GenExteriorPolynomial<GenPolynomial<BigRational>>> pgens = ppf.generators();
        System.out.println("pgens = " + pgens);
        assertTrue("#pgens == 4+3", pgens.size() == 4+3);

        //pp = ppf.random(2);
        pp = ppf.random(kl/2, ll, el);
        System.out.println("\npp = " + pp);
        //pp = pp.subtract(pp);
        //System.out.println("pp.isZERO() = " + pp.isZERO());
        //assertTrue("pp.isZERO() = ", pp.isZERO());

        GenExteriorPolynomial<GenPolynomial<BigRational>> der;
        der = PolyUtil.<GenPolynomial<BigRational>> exteriorDerivative(pp);
        //System.out.println("der = " + der);

        der = PolyUtil.<BigRational> exteriorDerivativePoly(pp);
        System.out.println("der = " + der);


        StringReader sr = new StringReader("x1 x2 x3 E(1) E(2)");
        //System.out.println("sr = " + sr);
        GenPolynomialTokenizer tok = new GenPolynomialTokenizer(sr);
        //System.out.println("ppf = " + ppf.toScript());

        // parse with tokenizer
        try {
            pp = (GenExteriorPolynomial<GenPolynomial<BigRational>>) tok.nextExteriorPolynomial(ppf);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("\npp = " + pp);
        der = PolyUtil.<BigRational> exteriorDerivativePoly(pp);
        System.out.println("der = " + der);

        //sr = new StringReader("x3 E(1) + x3 E(2)");
        //sr = new StringReader("x1 E(1) + x2 E(2)");
        sr = new StringReader("x2 E(1) + x1 E(2)");
        //System.out.println("sr = " + sr);
        tok = new GenPolynomialTokenizer(sr);
        //System.out.println("ppf = " + ppf.toScript());

        // parse with tokenizer
        try {
            pp = (GenExteriorPolynomial<GenPolynomial<BigRational>>) tok.nextExteriorPolynomial(ppf);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("\npp = " + pp);
        der = PolyUtil.<BigRational> exteriorDerivativePoly(pp);
        System.out.println("der = " + der);
    }

}
