
/*
 * $Id$
 */

package edu.jas.ufd;


import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import edu.jas.arith.BigInteger;
import edu.jas.kern.ComputerThreads;
import edu.jas.kern.PrettyPrint;
import edu.jas.poly.GenExteriorPolynomial;
import edu.jas.poly.GenExteriorPolynomialRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.IndexFactory;
import edu.jas.poly.TermOrder;
import edu.jas.structure.RingElem;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Quotient BigInteger coefficient GenPolynomial tests with JUnit.
 * @author Heinz Kredel
 */

public class QuotIntPolynomialTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>QoutIntPolynomialTest</CODE> object.
     * @param name String.
     */
    public QuotIntPolynomialTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(QuotIntPolynomialTest.class);
        return suite;
    }


    QuotientRing<BigInteger> eFac;


    GenPolynomialRing<BigInteger> mfac;


    GenPolynomialRing<Quotient<BigInteger>> qfac;


    GenPolynomial<Quotient<BigInteger>> a, b, c, d, e;


    int rl = 3;


    int kl = 2; // degree of coefficient polynomials!!!


    int ll = 4; //6;


    int el = 3;


    float q = 0.3f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        String[] cv = new String[] { "a", "b", "c" };
        BigInteger cfac = new BigInteger(1);
        mfac = new GenPolynomialRing<BigInteger>(cfac, rl, to, cv);
        eFac = new QuotientRing<BigInteger>(mfac);
        String[] v = new String[] { "w", "x", "y", "z" };
        qfac = new GenPolynomialRing<Quotient<BigInteger>>(eFac, rl + 1, v);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        //eFac.terminate();
        eFac = null;
        mfac = null;
        qfac = null;
        ComputerThreads.terminate();
    }


    /**
     * Test constructor and toString.
     */
    public void testConstruction() {
        c = qfac.getONE();
        //System.out.println("c = " + c);
        //System.out.println("c.val = " + c.val);
        assertTrue("length( c ) = 1", c.length() == 1);
        assertTrue("isZERO( c )", !c.isZERO());
        assertTrue("isONE( c )", c.isONE());

        d = qfac.getZERO();
        //System.out.println("d = " + d);
        //System.out.println("d.val = " + d.val);
        assertTrue("length( d ) = 0", d.length() == 0);
        assertTrue("isZERO( d )", d.isZERO());
        assertTrue("isONE( d )", !d.isONE());
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 0; i < 3; i++) {
            //a = qfac.random(ll+i);
            a = qfac.random(kl, ll + i, el, q);
            //System.out.println("a["+i+"] = " + a);
            if (a.isZERO() || a.isONE()) {
                continue;
            }
            assertTrue("length( a" + i + " ) <> 0", a.length() >= 0);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());

            b = a.monic();
            Quotient<BigInteger> ldbcf = b.leadingBaseCoefficient();
            //System.out.println("b["+i+"] = " + b);
            assertTrue("ldbcf( b" + i + " ) == 1 " + b + ", a = " + a, ldbcf.isONE());
        }
    }


    /**
     * Test addition.
     */
    public void testAddition() {
        a = qfac.random(kl, ll, el, q);
        b = qfac.random(kl, ll, el, q);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("a = " + a.toString( qfac.getVars() ));
        //System.out.println("b = " + b.toString( qfac.getVars() ));

        c = a.sum(b);
        d = c.subtract(b);
        assertEquals("a+b-b = a", a, d);

        c = a.sum(b);
        d = b.sum(a);
        //System.out.println("c = " + c.toString( qfac.getVars() ));
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        assertEquals("a+b = b+a", c, d);

        c = qfac.random(kl, ll, el, q);
        d = c.sum(a.sum(b));
        e = c.sum(a).sum(b);
        assertEquals("c+(a+b) = (c+a)+b", d, e);

        c = a.sum(qfac.getZERO());
        d = a.subtract(qfac.getZERO());
        assertEquals("a+0 = a-0", c, d);

        c = qfac.getZERO().sum(a);
        d = qfac.getZERO().subtract(a.negate());
        assertEquals("0+a = 0+(-a)", c, d);
    }


    /**
     * Test object multiplication.
     */
    public void testMultiplication() {
        a = qfac.random(kl, ll, el, q);
        //assertTrue("not isZERO( a )", !a.isZERO() );

        b = qfac.random(kl, ll, el, q);
        //assertTrue("not isZERO( b )", !b.isZERO() );

        c = b.multiply(a);
        d = a.multiply(b);
        if (!a.isZERO() && !b.isZERO()) {
            assertTrue("not isZERO( c )", !c.isZERO());
            assertTrue("not isZERO( d )", !d.isZERO());
        }

        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        e = d.subtract(c);
        assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO());

        assertTrue("a*b = b*a", c.equals(d));
        assertEquals("a*b = b*a", c, d);

        c = qfac.random(kl, ll, el, q);
        //System.out.println("c = " + c);
        d = a.multiply(b.multiply(c));
        e = (a.multiply(b)).multiply(c);

        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        //System.out.println("d-e = " + d.subtract(c) );

        assertEquals("a(bc) = (ab)c", d, e);
        assertTrue("a(bc) = (ab)c", d.equals(e));

        c = a.multiply(qfac.getONE());
        d = qfac.getONE().multiply(a);
        assertEquals("a*1 = 1*a", c, d);

        if (a.isUnit()) {
            c = a.inverse();
            d = c.multiply(a);
            //System.out.println("a = " + a);
            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            assertTrue("a*1/a = 1", d.isONE());
        }
    }


    /**
     * Test parse().
     */
    public void testParse() {
        a = qfac.random(kl, ll * 2, el * 2, q * 2);
        //assertTrue("not isZERO( a )", !a.isZERO() );

        //PrettyPrint.setInternal();
        //System.out.println("a = " + a);
        PrettyPrint.setPretty();
        //System.out.println("a = " + a);
        String p = a.toString(qfac.getVars());
        //System.out.println("p = " + p);
        b = qfac.parse(p);
        //System.out.println("b = " + b.toString( qfac.getVars() ) );

        //c = a.subtract(b);
        //System.out.println("c = " + c);
        assertEquals("parse(a.toSting()) = a", a, b);
    }


    /**
     * Test derivation.
     */
    @SuppressWarnings("unchecked")
    public void testDerivation() {
        // rationals
        BigInteger rf = new BigInteger();
        System.out.println("rf = " + rf.toScriptFactory());

        // 3/6 commuting vars
        //IndexFactory wf = new IndexFactory(6);
        //String[] vars = new String[]{ "a", "b", "c", "d", "e", "f" };
        String[] vars = new String[] { "x1", "x2", "x3" };
        System.out.println("vars = " + Arrays.toString(vars));

        // polynomials over rationals
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(rf, vars);
        System.out.println("pf = " + pf.toScript());

        QuotientRing<BigInteger> qf = new QuotientRing<BigInteger>(pf);
        System.out.println("qf = " + qf.toScript());

        assertTrue("commutative", qf.isCommutative());
        assertTrue("associative", qf.isAssociative());
        assertTrue("not field", qf.isField());
        assertEquals("qf == qf: ", qf, qf);

        String s = qf.toScript();
        System.out.println("qf.toScript: " + s + ", " + s.length());
        assertEquals("#s == 42: " + s, s.length(), 42);

        s = qf.toString();
        System.out.println("qf.toString: " + s + ", " + s.length());
        assertEquals("#s == 41: " + s, s.length(), 41);

        Quotient<BigInteger> p = qf.getONE();
        System.out.println("p = " + p);
        assertTrue("p == 1", p.isONE());
        p = qf.getZERO();
        assertTrue("p == 0", p.isZERO());
        System.out.println("p = " + p);
        //p = qf.random(kl, ll, el);
        //System.out.println("p = " + p);

        List<Quotient<BigInteger>> gens = qf.generators();
        System.out.println("gens = " + gens);
        assertTrue("#gens == 4", gens.size() == 4);

        gens = qf.generators();
        System.out.println("gens = " + gens);
        assertTrue("#gens == 4", gens.size() == 4);

        RingElem<Quotient<BigInteger>> qe = new Quotient<BigInteger>(qf);
        System.out.println("qe = " + qe);
        //System.out.println("qe.equals(qe) = " + qe.equals(qe) );
        //System.out.println("p.equals(p) = " + p.equals(p) );
        assertTrue("p.equals(qe) = ", p.equals(qe));
        assertTrue("p.equals(p) = ", p.equals(p));

        qe = qe.sum(p);
        System.out.println("qe = " + qe);
        assertTrue("qe.isZERO() = ", qe.isZERO());
        //p = pf.random(9);
        p = qf.random(kl, ll, el, q);
        p = p.subtract(p);
        System.out.println("p = " + p);
        System.out.println("p.isZERO() = " + p.isZERO());
        assertTrue("p.isZERO() = ", p.isZERO());


        // exterior polynomials over (polynomials over rationals)
        // 3 non-commuting vars
        IndexFactory wf2 = new IndexFactory(3);
        System.out.println("wf2 = " + wf2);

        GenExteriorPolynomialRing<Quotient<BigInteger>> ppf;
        ppf = new GenExteriorPolynomialRing<Quotient<BigInteger>>(qf, wf2);
        System.out.println("ppf = " + ppf.toScript());

        GenExteriorPolynomial<Quotient<BigInteger>> pp = ppf.getONE();
        System.out.println("pp = " + pp);
        assertTrue("pp == 1", pp.isONE());
        //pp = ppf.random(2);
        pp = ppf.random(kl, ll, el);
        System.out.println("pp = " + pp);
        pp = ppf.getZERO();
        System.out.println("pp = " + pp);
        assertTrue("pp == 0", pp.isZERO());

        List<GenExteriorPolynomial<Quotient<BigInteger>>> pgens = ppf.generators();
        System.out.println("pgens = " + pgens);
        assertTrue("#pgens == 4+3", pgens.size() == 4 + 3);

        //pp = ppf.random(2);
        pp = ppf.random(kl, ll, el);
        System.out.println("\npp = " + pp);
        //pp = pp.subtract(pp);
        //System.out.println("pp.isZERO() = " + pp.isZERO());
        //assertTrue("pp.isZERO() = ", pp.isZERO());

        GenExteriorPolynomial<Quotient<BigInteger>> der;
        //der = PolyUtil.<Quotient<BigInteger>> exteriorDerivative(pp);
        //System.out.println("der = " + der);

        der = PolyUfdUtil.<BigInteger> exteriorDerivativeQuot(pp);
        System.out.println("der = " + der);


        StringReader sr = new StringReader("{x1 x2 | x3 } E(1) E(2)");
        //System.out.println("sr = " + sr);
        GenPolynomialTokenizer tok = new GenPolynomialTokenizer(sr);
        //System.out.println("ppf = " + ppf.toScript());

        // parse with tokenizer
        try {
            pp = (GenExteriorPolynomial<Quotient<BigInteger>>) tok.nextExteriorPolynomial(ppf);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("\npp = " + pp);
        der = PolyUfdUtil.<BigInteger> exteriorDerivativeQuot(pp);
        System.out.println("der = " + der);

        sr = new StringReader("{x3 | x2} E(1) + x3 E(2)");
        //sr = new StringReader("x1 E(1) + x2 E(2)");
        //sr = new StringReader("x2 E(1) + x1 E(2)");
        //System.out.println("sr = " + sr);
        tok = new GenPolynomialTokenizer(sr);
        //System.out.println("ppf = " + ppf.toScript());

        // parse with tokenizer
        try {
            pp = (GenExteriorPolynomial<Quotient<BigInteger>>) tok.nextExteriorPolynomial(ppf);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("\npp = " + pp);
        der = PolyUfdUtil.<BigInteger> exteriorDerivativeQuot(pp);
        System.out.println("der = " + der);
    }

}
