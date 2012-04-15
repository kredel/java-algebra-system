/*
 * $Id$
 */

package edu.jas.poly;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigComplex;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.Product;
import edu.jas.arith.ProductRing;

import edu.jas.kern.ComputerThreads;
import edu.jas.ufd.PolyUfdUtil;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;


/**
 * PolyUtil tests with JUnit.
 * @author Heinz Kredel.
 */

public class PolyUtilTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>PolyUtilTest</CODE> object.
     * @param name String.
     */
    public PolyUtilTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(PolyUtilTest.class);
        return suite;
    }


    //private final static int bitlen = 100;

    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<BigInteger> dfac;


    GenPolynomialRing<BigInteger> cfac;


    GenPolynomialRing<GenPolynomial<BigInteger>> rfac;


    BigInteger ai;


    BigInteger bi;


    BigInteger ci;


    BigInteger di;


    BigInteger ei;


    GenPolynomial<BigInteger> a;


    GenPolynomial<BigInteger> b;


    GenPolynomial<BigInteger> c;


    GenPolynomial<BigInteger> d;


    GenPolynomial<BigInteger> e;


    GenPolynomial<GenPolynomial<BigInteger>> ar;


    GenPolynomial<GenPolynomial<BigInteger>> br;


    GenPolynomial<GenPolynomial<BigInteger>> cr;


    GenPolynomial<GenPolynomial<BigInteger>> dr;


    GenPolynomial<GenPolynomial<BigInteger>> er;


    int rl = 5;


    int kl = 5;


    int ll = 5;


    int el = 3;


    float q = 0.3f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl, to);
        cfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cfac, 1, to);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        dfac = null;
        cfac = null;
        rfac = null;
    }


    protected static java.math.BigInteger getPrime1() {
        long prime = 2; //2^60-93; // 2^30-35; //19; knuth (2,390)
        for (int i = 1; i < 60; i++) {
            prime *= 2;
        }
        prime -= 93;
        //prime = 37;
        //System.out.println("p1 = " + prime);
        return new java.math.BigInteger("" + prime);
    }


    protected static java.math.BigInteger getPrime2() {
        long prime = 2; //2^60-93; // 2^30-35; //19; knuth (2,390)
        for (int i = 1; i < 30; i++) {
            prime *= 2;
        }
        prime -= 35;
        //prime = 19;
        //System.out.println("p1 = " + prime);
        return new java.math.BigInteger("" + prime);
    }


    /**
     * Test recursive <--> distributive conversion.
     */
    public void testConversion() {
        c = dfac.getONE();
        assertTrue("length( c ) = 1", c.length() == 1);
        assertTrue("isZERO( c )", !c.isZERO());
        assertTrue("isONE( c )", c.isONE());

        cr = PolyUtil.recursive(rfac, c);
        a = PolyUtil.distribute(dfac, cr);
        assertEquals("c == dist(rec(c))", c, a);

        d = dfac.getZERO();
        assertTrue("length( d ) = 0", d.length() == 0);
        assertTrue("isZERO( d )", d.isZERO());
        assertTrue("isONE( d )", !d.isONE());

        dr = PolyUtil.recursive(rfac, d);
        b = PolyUtil.distribute(dfac, dr);
        assertEquals("d == dist(rec(d))", d, b);
    }


    /**
     * Test recursive <--> distributive ring conversion.
     */
    public void testConversionRing() {
        GenPolynomialRing<GenPolynomial<BigInteger>> rf = dfac.recursive(1);
        GenPolynomialRing<BigInteger> cf = (GenPolynomialRing<BigInteger>)rf.coFac;
        assertEquals("rfac#var == rf#var ", rfac.nvar, rf.nvar);
        assertEquals("rfac.coFac#var == rf.coFac#var ", cfac.nvar, cf.nvar);
        assertEquals("rfac.coFac.coFac == rf.coFac.coFac ", cfac.coFac, cf.coFac);
        // variable names not same in this test
    }


    /**
     * Test random recursive <--> distributive conversion.
     */
    public void testRandomConversion() {
        for (int i = 0; i < 7; i++) {
            c = dfac.random(kl * (i + 2), ll + 2 * i, el + i, q);

            assertTrue("length( c" + i + " ) <> 0", c.length() >= 0);
            assertTrue(" not isZERO( c" + i + " )", !c.isZERO());
            assertTrue(" not isONE( c" + i + " )", !c.isONE());

            cr = PolyUtil.recursive(rfac, c);
            a = PolyUtil.distribute(dfac, cr);
            //System.out.println("c   = " + c);
            //System.out.println("cr  = " + cr);
            //System.out.println("crd = " + a);

            assertEquals("c == dist(rec(c))", c, a);
        }
    }


    /**
     * Test random rational <--> integer conversion.
     */
    public void testRationalConversion() {
        GenPolynomialRing<BigRational> rfac = new GenPolynomialRing<BigRational>(new BigRational(1), rl, to);

        GenPolynomial<BigRational> ar;
        GenPolynomial<BigRational> br;

        for (int i = 0; i < 3; i++) {
            c = dfac.random(kl * (i + 9), ll * (i + 3), el + i, q).abs();
            //c = c.multiply( new BigInteger(99) ); // fails, since not primitive
            //c = GreatestCommonDivisor.primitivePart(c);

            assertTrue("length( c" + i + " ) <> 0", c.length() >= 0);
            assertTrue(" not isZERO( c" + i + " )", !c.isZERO());
            assertTrue(" not isONE( c" + i + " )", !c.isONE());

            ar = PolyUtil.<BigRational> fromIntegerCoefficients(rfac, c);
            br = ar.monic();
            a = PolyUtil.integerFromRationalCoefficients(dfac, br);
            //System.out.println("c   = " + c);
            //System.out.println("ar  = " + ar);
            //System.out.println("br  = " + br);
            //System.out.println("crd = " + a);

            assertEquals("c == integer(rational(c))", c, a);
        }
    }


    /**
     * Test random modular <--> integer conversion.
     */
    public void testModularConversion() {
        ModIntegerRing pm = new ModIntegerRing(getPrime1());
        GenPolynomialRing<ModInteger> mfac = new GenPolynomialRing<ModInteger>(pm, rl, to);

        GenPolynomial<ModInteger> ar;

        for (int i = 0; i < 3; i++) {
            c = dfac.random(kl * (i + 2), ll * (i + 1), el + i, q).abs();
            //c = c.multiply( new BigInteger(99) ); // fails, since not primitive
            //c = GreatestCommonDivisor.primitivePart(c);

            assertTrue("length( c" + i + " ) <> 0", c.length() >= 0);
            assertTrue(" not isZERO( c" + i + " )", !c.isZERO());
            assertTrue(" not isONE( c" + i + " )", !c.isONE());

            ar = PolyUtil.<ModInteger> fromIntegerCoefficients(mfac, c);
            a = PolyUtil.integerFromModularCoefficients(dfac, ar);
            //System.out.println("c   = " + c);
            //System.out.println("ar  = " + ar);
            //System.out.println("crd = " + a);

            assertEquals("c == integer(modular(c))", c, a);
        }
    }


    /**
     * Test chinese remainder.
     */
    public void testChineseRemainder() {
        java.math.BigInteger p1 = getPrime1();
        java.math.BigInteger p2 = getPrime2();
        java.math.BigInteger p12 = p1.multiply(p2);

        ModIntegerRing pm1 = new ModIntegerRing(p1);
        GenPolynomialRing<ModInteger> mfac1 = new GenPolynomialRing<ModInteger>(pm1, rl, to);

        ModIntegerRing pm2 = new ModIntegerRing(p2);
        GenPolynomialRing<ModInteger> mfac2 = new GenPolynomialRing<ModInteger>(pm2, rl, to);

        ModIntegerRing pm12 = new ModIntegerRing(p12);
        GenPolynomialRing<ModInteger> mfac = new GenPolynomialRing<ModInteger>(pm12, rl, to);

        ModInteger di = pm2.create(p1);
        di = di.inverse();
        //System.out.println("di = " + di);

        GenPolynomial<ModInteger> am;
        GenPolynomial<ModInteger> bm;
        GenPolynomial<ModInteger> cm;

        ExpVector degv, qdegv;

        for (int i = 0; i < 3; i++) {
            c = dfac.random((59 + 29) / 2, ll * (i + 1), el + i, q);
            //c = c.multiply( new BigInteger(99) ); // fails, since not primitive
            //c = GreatestCommonDivisor.primitivePart(c);
            degv = c.degreeVector();
            //System.out.println("degv  = " + degv);

            assertTrue("length( c" + i + " ) <> 0", c.length() >= 0);
            assertTrue(" not isZERO( c" + i + " )", !c.isZERO());
            assertTrue(" not isONE( c" + i + " )", !c.isONE());

            am = PolyUtil.<ModInteger> fromIntegerCoefficients(mfac1, c);
            qdegv = am.degreeVector();
            //System.out.println("qdegv  = " + qdegv);
            if (!degv.equals(qdegv)) {
                continue;
            }
            bm = PolyUtil.<ModInteger> fromIntegerCoefficients(mfac2, c);
            qdegv = bm.degreeVector();
            //System.out.println("qdegv  = " + qdegv);
            if (!degv.equals(qdegv)) {
                continue;
            }

            cm = PolyUtil.chineseRemainder(mfac, am, di, bm);
            a = PolyUtil.integerFromModularCoefficients(dfac, cm);

            //System.out.println("c  = " + c);
            //System.out.println("am = " + am);
            //System.out.println("bm = " + bm);
            //System.out.println("cm = " + cm);
            //System.out.println("a  = " + a);

            assertEquals("cra(c mod p1,c mod p2) = c", c, a);
        }
    }


    /**
     * Test complex conversion.
     */
    public void testComplexConversion() {
        BigRational rf = new BigRational(1);
        GenPolynomialRing<BigRational> rfac = new GenPolynomialRing<BigRational>(rf, rl, to);

        BigComplex cf = new BigComplex(1);
        GenPolynomialRing<BigComplex> cfac = new GenPolynomialRing<BigComplex>(cf, rl, to);

        BigComplex imag = BigComplex.I;

        GenPolynomial<BigRational> rp;
        GenPolynomial<BigRational> ip;
        GenPolynomial<BigComplex> crp;
        GenPolynomial<BigComplex> cip;
        GenPolynomial<BigComplex> cp;
        GenPolynomial<BigComplex> ap;

        for (int i = 0; i < 3; i++) {
            cp = cfac.random(kl + 2 * i, ll * (i + 1), el + i, q);

            assertTrue("length( c" + i + " ) <> 0", cp.length() >= 0);
            assertTrue(" not isZERO( c" + i + " )", !cp.isZERO());
            assertTrue(" not isONE( c" + i + " )", !cp.isONE());

            rp = PolyUtil.realPart(rfac, cp);
            ip = PolyUtil.imaginaryPart(rfac, cp);

            crp = PolyUtil.complexFromRational(cfac, rp);
            cip = PolyUtil.complexFromRational(cfac, ip);

            ap = crp.sum(cip.multiply(imag));

            //System.out.println("cp = " + cp);
            //System.out.println("rp = " + rp);
            //System.out.println("ip = " + ip);
            //System.out.println("crp = " + crp);
            //System.out.println("cip = " + cip);
            //System.out.println("ap  = " + ap);

            assertEquals("re(c)+i*im(c) = c", cp, ap);
        }
    }


    /**
     * Test base pseudo division.
     */
    public void testBasePseudoDivision() {
        String[] names = new String[] { "x" };
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),to,names);
        GenPolynomialRing<BigRational> rdfac = new GenPolynomialRing<BigRational>(new BigRational(1),dfac);
        //System.out.println("\ndfac  = " + dfac);
        //System.out.println("rdfac = " + rdfac);

        a = dfac.random(kl, 2*ll, el+17, q);
        //a = dfac.parse(" 3 x^5 + 44 ");
        //b = a;
        b = dfac.random(kl, 2*ll, el+3, q);
        //a = a.multiply(b);
        //a = a.sum(b);
        //b = dfac.parse(" 2 x^2 + 40 ");
        //System.out.println("a   = " + a);
        //System.out.println("b   = " + b);

        GenPolynomial<BigInteger>[] QR = PolyUtil.<BigInteger> basePseudoQuotientRemainder(a, b);
        c = QR[1];
        d = QR[0];
        //d = PolyUtil.<BigInteger> baseDensePseudoQuotient(a, b);
        //d = PolyUtil.<BigInteger> basePseudoDivide(a, b);
        //System.out.println("q   = " + d);
        //c = PolyUtil.<BigInteger> baseDensePseudoRemainder(a, b);
        //c = PolyUtil.<BigInteger> baseSparsePseudoRemainder(a, b);
        //System.out.println("r   = " + c);
        //System.out.println("q b = " + d.multiply(b));

        boolean t = PolyUtil.<BigInteger> isBasePseudoQuotientRemainder(a, b, d, c);
        assertTrue("lc^n a = q b + r: " + c, t);

        GenPolynomial<BigRational> ap = PolyUtil.<BigRational> fromIntegerCoefficients(rdfac,a);
        GenPolynomial<BigRational> bp = PolyUtil.<BigRational> fromIntegerCoefficients(rdfac,b);
        GenPolynomial<BigRational> cp = PolyUtil.<BigRational> fromIntegerCoefficients(rdfac,c);
        GenPolynomial<BigRational> dp = PolyUtil.<BigRational> fromIntegerCoefficients(rdfac,d);
        //System.out.println("ap  = " + ap);
        //System.out.println("bp  = " + bp);
        //System.out.println("cp  = " + cp);
        ////System.out.println("dp  = " + dp);
        //System.out.println("dp  = " + dp.monic());

        GenPolynomial<BigRational> qp = ap.divide(bp);
        GenPolynomial<BigRational> rp = ap.remainder(bp);
        //System.out.println("qp  = " + qp);
        //System.out.println("qp  = " + qp.monic());
        //System.out.println("rp  = " + rp);
        GenPolynomial<BigRational> rhs = qp.multiply(bp).sum(rp);
        //System.out.println("qp bp + rp  = " + rhs);

        assertEquals("ap = qp bp + rp: ", ap, rhs);

        assertEquals("cp = rp: ", rp.monic(), cp.monic() );
        assertEquals("dp = qp: ", qp.monic(), dp.monic() ); // ??
        //System.out.println("dp = qp: " + qp.monic().equals(dp.monic()) );
    }


    /**
     * Test recursive pseudo division.
     * @see edu.jas.ufd.PolyUfdUtilTest#testRecursivePseudoDivision
     */
    public void testRecursivePseudoDivision() {
    }


    /**
     * Test evaluate main recursive.
     */
    public void testEvalMainRecursive() {
        ai = (new BigInteger()).random(kl);
        //System.out.println("ai  = " + ai);

        ar = rfac.getZERO();
        //System.out.println("ar  = " + ar);

        a = PolyUtil.<BigInteger> evaluateMainRecursive(cfac, ar, ai);
        //System.out.println("a   = " + a);

        assertTrue("isZERO( a )", a.isZERO());

        ar = rfac.getONE();
        //System.out.println("ar  = " + ar);

        a = PolyUtil.<BigInteger> evaluateMainRecursive(cfac, ar, ai);
        //System.out.println("a   = " + a);

        assertTrue("isONE( a )", a.isONE());


        //ar = rfac.getONE();
        ar = rfac.random(kl, ll, el, q);
        //System.out.println("ar  = " + ar);
        //br = rfac.getONE();
        br = rfac.random(kl, ll, el, q);
        //System.out.println("br  = " + br);

        cr = br.sum(ar);
        //System.out.println("cr  = " + cr);

        a = PolyUtil.<BigInteger> evaluateMainRecursive(cfac, ar, ai);
        b = PolyUtil.<BigInteger> evaluateMainRecursive(cfac, br, ai);
        c = PolyUtil.<BigInteger> evaluateMainRecursive(cfac, cr, ai);
        //System.out.println("a   = " + a);
        //System.out.println("b   = " + b);
        //System.out.println("c   = " + c);

        d = a.sum(b);
        //System.out.println("d   = " + d);

        assertEquals("eval(a+b) == eval(a) + eval(b)", c, d);


        cr = br.multiply(ar);
        //System.out.println("cr  = " + cr);

        a = PolyUtil.<BigInteger> evaluateMainRecursive(cfac, ar, ai);
        b = PolyUtil.<BigInteger> evaluateMainRecursive(cfac, br, ai);
        c = PolyUtil.<BigInteger> evaluateMainRecursive(cfac, cr, ai);
        //System.out.println("a   = " + a);
        //System.out.println("b   = " + b);
        //System.out.println("c   = " + c);

        d = a.multiply(b);
        //System.out.println("d   = " + d);

        assertEquals("eval(a*b) == eval(a) * eval(b)", c, d);
    }


    /**
     * Test evaluate main.
     */
    public void testEvalMain() {
        ei = (new BigInteger()).random(kl);
        //System.out.println("ei  = " + ei);

        cfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), 1, to);
        //System.out.println("cfac  = " + cfac);

        a = cfac.getZERO();
        //System.out.println("a  = " + a);

        ai = PolyUtil.<BigInteger> evaluateMain(ei, a, ei);
        //System.out.println("ai   = " + ai);

        assertTrue("isZERO( ai )", ai.isZERO());

        a = cfac.getONE();
        //System.out.println("a  = " + a);

        ai = PolyUtil.<BigInteger> evaluateMain(ei, a, ei);
        //System.out.println("ai   = " + ai);

        assertTrue("isONE( ai )", ai.isONE());

        //a = cfac.getONE();
        a = cfac.random(kl, ll, el, q);
        //System.out.println("a  = " + a);
        //b = cfac.getONE();
        b = cfac.random(kl, ll, el, q);
        //System.out.println("b  = " + b);

        c = b.sum(a);
        //System.out.println("c  = " + c);

        ai = PolyUtil.<BigInteger> evaluateMain(ei, a, ei);
        bi = PolyUtil.<BigInteger> evaluateMain(ei, b, ei);
        ci = PolyUtil.<BigInteger> evaluateMain(ei, c, ei);
        //System.out.println("ai   = " + ai);
        //System.out.println("bi   = " + bi);
        //System.out.println("ci   = " + ci);

        di = bi.sum(ai);
        //System.out.println("di   = " + di);

        assertEquals("eval(a+b) == eval(a) + eval(b)", ci, di);

        c = b.multiply(a);
        //System.out.println("c  = " + c);

        ai = PolyUtil.<BigInteger> evaluateMain(ei, a, ei);
        bi = PolyUtil.<BigInteger> evaluateMain(ei, b, ei);
        ci = PolyUtil.<BigInteger> evaluateMain(ei, c, ei);
        //System.out.println("ai   = " + ai);
        //System.out.println("bi   = " + bi);
        //System.out.println("ci   = " + ci);

        di = bi.multiply(ai);
        //System.out.println("di   = " + di);

        assertEquals("eval(a*b) == eval(a) * eval(b)", ci, di);
    }


    /**
     * Test evaluate first.
     */
    public void testEvalFirst() {
        ei = (new BigInteger()).random(kl);
        //System.out.println("ei  = " + ei);

        GenPolynomial<BigInteger> ae, be, ce, de;

        GenPolynomialRing<BigInteger> fac;
        fac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl, to);
        //System.out.println("fac  = " + fac);

        cfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), 1, to);
        //System.out.println("cfac  = " + cfac);

        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl - 1, to);
        //System.out.println("dfac  = " + dfac);

        a = fac.getZERO();
        //System.out.println("a  = " + a);

        ae = PolyUtil.<BigInteger> evaluateFirst(cfac, dfac, a, ei);
        //System.out.println("ae   = " + ae);

        assertTrue("isZERO( ae )", ae.isZERO());

        a = fac.getONE();
        //System.out.println("a  = " + a);

        ae = PolyUtil.<BigInteger> evaluateFirst(cfac, dfac, a, ei);
        //System.out.println("ae   = " + ae);

        assertTrue("isONE( ae )", ae.isONE());

        //a = fac.getONE();
        a = fac.random(kl, ll, el, q);
        //System.out.println("a  = " + a);
        //b = fac.getONE();
        b = fac.random(kl, ll, el, q);
        //System.out.println("b  = " + b);

        c = b.sum(a);
        //System.out.println("c  = " + c);

        ae = PolyUtil.<BigInteger> evaluateFirst(cfac, dfac, a, ei);
        be = PolyUtil.<BigInteger> evaluateFirst(cfac, dfac, b, ei);
        ce = PolyUtil.<BigInteger> evaluateFirst(cfac, dfac, c, ei);
        //System.out.println("ae   = " + ae);
        //System.out.println("be   = " + be);
        //System.out.println("ce   = " + ce);

        de = be.sum(ae);
        //System.out.println("de   = " + de);

        assertEquals("eval(a+b) == eval(a) + eval(b)", ce, de);

        c = b.multiply(a);
        //System.out.println("c  = " + c);

        ae = PolyUtil.<BigInteger> evaluateFirst(cfac, dfac, a, ei);
        be = PolyUtil.<BigInteger> evaluateFirst(cfac, dfac, b, ei);
        ce = PolyUtil.<BigInteger> evaluateFirst(cfac, dfac, c, ei);
        //System.out.println("ae   = " + ae);
        //System.out.println("be   = " + be);
        //System.out.println("ce   = " + ce);

        de = be.multiply(ae);
        //System.out.println("de   = " + de);

        assertEquals("eval(a*b) == eval(a) * eval(b)", ce, de);
    }


    /**
     * Test evaluate all.
     */
    public void testEvalAll() {
        BigInteger cfac = new BigInteger();

        List<BigInteger> Ev = new ArrayList<BigInteger>();
        for (int i = 0; i < rl; i++) {
            ei = cfac.random(kl);
            Ev.add(ei);
        }
        //System.out.println("Ev  = " + Ev);

        BigInteger ae, be, ce, de;

        GenPolynomialRing<BigInteger> fac;
        fac = new GenPolynomialRing<BigInteger>(cfac, rl, to);
        //System.out.println("fac  = " + fac);

        a = fac.getZERO();
        //System.out.println("a  = " + a);

        ae = PolyUtil.<BigInteger> evaluateAll(cfac, dfac, a, Ev);
        //System.out.println("ae   = " + ae);

        assertTrue("isZERO( ae )", ae.isZERO());

        a = fac.getONE();
        //System.out.println("a  = " + a);

        ae = PolyUtil.<BigInteger> evaluateAll(cfac, dfac, a, Ev);
        //System.out.println("ae   = " + ae);

        assertTrue("isONE( ae )", ae.isONE());

        //a = fac.getONE();
        a = fac.random(kl, ll, el, q);
        //System.out.println("a  = " + a);
        //b = fac.getONE();
        b = fac.random(kl, ll, el, q);
        //System.out.println("b  = " + b);

        c = b.sum(a);
        //System.out.println("c  = " + c);

        ae = PolyUtil.<BigInteger> evaluateAll(cfac, dfac, a, Ev);
        be = PolyUtil.<BigInteger> evaluateAll(cfac, dfac, b, Ev);
        ce = PolyUtil.<BigInteger> evaluateAll(cfac, dfac, c, Ev);
        //System.out.println("ae   = " + ae);
        //System.out.println("be   = " + be);
        //System.out.println("ce   = " + ce);

        de = be.sum(ae);
        //System.out.println("de   = " + de);

        assertEquals("eval(a+b) == eval(a) + eval(b)", ce, de);

        c = b.multiply(a);
        //System.out.println("c  = " + c);

        ce = PolyUtil.<BigInteger> evaluateAll(cfac, dfac, c, Ev);
        //System.out.println("ae   = " + ae);
        //System.out.println("be   = " + be);
        //System.out.println("ce   = " + ce);

        de = be.multiply(ae);
        //System.out.println("de   = " + de);

        assertEquals("eval(a*b) == eval(a) * eval(b)", ce, de);
    }


    /**
     * Test interpolate univariate 1 polynomial.
     */
    public void testInterpolateUnivariateOne() {
        ModInteger ai, bi, ci, di, ei, fi, gi, hi;
        GenPolynomial<ModInteger> a;
        GenPolynomialRing<ModInteger> cfac;
        ModIntegerRing fac;
        GenPolynomial<ModInteger> r;
        GenPolynomial<ModInteger> Q;
        GenPolynomial<ModInteger> Qp;

        fac = new ModIntegerRing(19);
        //System.out.println("fac.modul  = " + fac.getModul());
        cfac = new GenPolynomialRing<ModInteger>(fac, 1, to);
        //System.out.println("cfac  = " + cfac);


        a = cfac.getONE();
        //System.out.println("a  = " + a);


        ei = fac.fromInteger(11);
        //System.out.println("ei  = " + ei);
        // a(ei)
        ai = PolyUtil.<ModInteger> evaluateMain(fac, a, ei);
        //System.out.println("ai   = " + ai);
        assertTrue("isONE( ai )", ai.isONE());

        di = fac.fromInteger(13);
        //System.out.println("di  = " + di);
        // a(di)
        bi = PolyUtil.<ModInteger> evaluateMain(fac, a, di);
        //System.out.println("bi   = " + bi);
        assertTrue("isONE( bi )", bi.isONE());


        // interpolation result
        r = cfac.getZERO();
        //System.out.println("r   = " + r);

        // interpolation polynomials product
        Q = cfac.getONE();
        //System.out.println("Q   = " + Q);


        ci = PolyUtil.<ModInteger> evaluateMain(fac, Q, ei);
        //System.out.println("ci   = " + ci);
        // Q(ei)^-1
        fi = ci.inverse();
        //System.out.println("fi   = " + fi);
        r = PolyUtil.<ModInteger> interpolate(cfac, r, Q, fi, ai, ei);
        //System.out.println("r   = " + r);


        // next evaluation polynomial
        Qp = cfac.univariate(0);
        Qp = Qp.subtract(cfac.getONE().multiply(ei));
        //System.out.println("Qp   = " + Qp);
        Q = Q.multiply(Qp);
        //System.out.println("Q   = " + Q);

        ci = PolyUtil.<ModInteger> evaluateMain(fac, Q, di);
        //System.out.println("ci   = " + ci);
        // Q(di)^-1
        fi = ci.inverse();
        //System.out.println("fi   = " + fi);
        r = PolyUtil.<ModInteger> interpolate(cfac, r, Q, fi, bi, di);
        //System.out.println("r   = " + r);

        // check evaluation
        gi = PolyUtil.<ModInteger> evaluateMain(fac, r, ei);
        //System.out.println("gi   = " + gi);
        hi = PolyUtil.<ModInteger> evaluateMain(fac, r, di);
        //System.out.println("hi   = " + hi);

        //            interpolate( a(ei), a(di) )            = a (mod 19)
        assertEquals("interpolate(a mod (x-ei),a mod (x-di)) = a (mod 19)", a, r);
    }


    /**
     * Test interpolate univariate deg > 0 polynomial.
     */
    public void testInterpolateUnivariate() {
        ModInteger ai, ci, ei, fi;
        GenPolynomial<ModInteger> a;
        GenPolynomialRing<ModInteger> cfac;
        ModIntegerRing fac;
        GenPolynomial<ModInteger> r;
        GenPolynomial<ModInteger> Q;
        GenPolynomial<ModInteger> Qp;

        //long prime = 19;
        long prime = getPrime1().longValue();
        fac = new ModIntegerRing(prime);
        //System.out.println("fac.modul  = " + fac.getModul());
        cfac = new GenPolynomialRing<ModInteger>(fac, 1, to);
        //System.out.println("cfac  = " + cfac);
        int maxdeg = 19;

        // polynomial to interpolate
        long deg = 0;
        do {
            a = cfac.random(kl, ll, maxdeg, q);
            if (!a.isZERO()) {
                deg = a.degree(0);
            }
        } while (deg <= 0);
        //System.out.println("a  = " + a);
        //System.out.println("deg  = " + deg);

        // interpolation result
        r = cfac.getZERO();
        //System.out.println("r   = " + r);

        // interpolation polynomials product
        Q = cfac.getONE();
        //System.out.println("Q   = " + Q);

        long i = -1;
        long qdeg;
        do {
            i++;
            if (i >= prime) {
                assertTrue("elements of Z_prime exhausted", i < prime);
            }
            qdeg = Q.degree(0);
            ei = fac.fromInteger(i);
            //System.out.println("ei  = " + ei);
            // a(ei)
            ai = PolyUtil.<ModInteger> evaluateMain(fac, a, ei);
            //System.out.println("ai   = " + ai);

            ci = PolyUtil.<ModInteger> evaluateMain(fac, Q, ei);
            //System.out.println("ci   = " + ci);
            // Q(ei)^-1
            fi = ci.inverse();
            //System.out.println("fi   = " + fi);
            r = PolyUtil.<ModInteger> interpolate(cfac, r, Q, fi, ai, ei);
            //System.out.println("r   = " + r);

            // next evaluation polynomial
            Qp = cfac.univariate(0);
            Qp = Qp.subtract(cfac.getONE().multiply(ei));
            //System.out.println("Qp   = " + Qp);
            Q = Q.multiply(Qp);
            //System.out.println("Q   = " + Q);
        } while (qdeg < deg);

        //System.out.println("a   = " + a);
        //System.out.println("r   = " + r);

        //            interpolate( a(e1), ..., a(ei) )           = a (mod 19)
        assertEquals("interpolate(a mod (x-e1),...,a mod (x-ei)) = a (mod 19)", a, r);
    }


    /**
     * Test interpolate multivariate deg > 0 polynomial.
     */
    public void testInterpolateMultivariate() {
        ModInteger ci, ei, fi;
        GenPolynomial<ModInteger> ap, bp;
        GenPolynomial<GenPolynomial<ModInteger>> a;
        GenPolynomialRing<GenPolynomial<ModInteger>> cfac;
        GenPolynomialRing<ModInteger> ufac;
        GenPolynomialRing<ModInteger> dfac;
        ModIntegerRing fac;
        GenPolynomial<GenPolynomial<ModInteger>> r;
        GenPolynomial<ModInteger> Q;
        GenPolynomial<ModInteger> Qp;

        //long prime = 19;
        long prime = getPrime1().longValue();
        fac = new ModIntegerRing(prime);
        //System.out.println("fac.modul  = " + fac.getModul());
        ufac = new GenPolynomialRing<ModInteger>(fac, 1, to);
        //System.out.println("ufac  = " + ufac);
        cfac = new GenPolynomialRing<GenPolynomial<ModInteger>>(ufac, rl, to);
        //System.out.println("cfac  = " + cfac);
        dfac = new GenPolynomialRing<ModInteger>(fac, rl, to);
        //System.out.println("dfac  = " + dfac);
        int maxdeg = 19;

        // polynomial to interpolate
        long deg = 0;
        do {
            a = cfac.random(kl, ll + 9, maxdeg, q);
            if (!a.isZERO()) {
                deg = PolyUtil.<ModInteger> coeffMaxDegree(a);
            }
        } while (deg <= 0);
        //System.out.println("a  = " + a);
        //System.out.println("deg  = " + deg);
        ExpVector degv = a.degreeVector();
        //System.out.println("degv  = " + degv);

        // interpolation result
        r = cfac.getZERO();
        //System.out.println("r   = " + r);

        // interpolation polynomials product
        Q = ufac.getONE();
        //System.out.println("Q   = " + Q);

        long i = -1;
        long qdeg;
        ExpVector qdegv;
        do {
            i++;
            if (i >= prime) {
                assertTrue("elements of Z_prime exhausted", i < prime);
            }
            qdeg = Q.degree(0);
            ei = fac.fromInteger(i);
            //System.out.println("ei  = " + ei);
            // a(ei)
            ap = PolyUtil.<ModInteger> evaluateFirstRec(ufac, dfac, a, ei);
            //System.out.println("ap   = " + ap);
            qdegv = ap.degreeVector();
            //System.out.println("qdegv = " + qdegv);
            if (!degv.equals(qdegv)) {
                continue;
            }
            ci = PolyUtil.<ModInteger> evaluateMain(fac, Q, ei);
            //System.out.println("ci   = " + ci);
            // Q(ei)^-1
            fi = ci.inverse();
            //System.out.println("fi   = " + fi);
            r = PolyUtil.<ModInteger> interpolate(cfac, r, Q, fi, ap, ei);
            //System.out.println("r   = " + r);

            // check
            bp = PolyUtil.<ModInteger> evaluateFirstRec(ufac, dfac, r, ei);
            //System.out.println("bp   = " + bp);
            assertEquals("interpolate(a)(ei) == a ", bp, ap);


            // next evaluation polynomial
            Qp = ufac.univariate(0);
            Qp = Qp.subtract(ufac.getONE().multiply(ei));
            //System.out.println("Qp   = " + Qp);
            Q = Q.multiply(Qp);
            //System.out.println("Q   = " + Q);
        } while (qdeg <= deg);

        //System.out.println("a   = " + a);
        //System.out.println("r   = " + r);

        //            interpolate( a(e1), ..., a(ei) )           = a (mod 19)
        assertEquals("interpolate(a mod (x-e1),...,a mod (x-ei)) = a (mod 19)", a, r);
    }


    /**
     * Test interpolate rational multivariate deg > 0 polynomial.
     */
    public void testInterpolateRationalMultivariate() {
        BigRational ci, ei, fi;
        GenPolynomial<BigRational> ap, bp;
        GenPolynomial<GenPolynomial<BigRational>> a;
        GenPolynomialRing<GenPolynomial<BigRational>> cfac;
        GenPolynomialRing<BigRational> ufac;
        GenPolynomialRing<BigRational> dfac;
        BigRational fac;
        GenPolynomial<GenPolynomial<BigRational>> r;
        GenPolynomial<BigRational> Q;
        GenPolynomial<BigRational> Qp;

        fac = new BigRational();
        //System.out.println("fac.modul  = " + fac.getModul());
        ufac = new GenPolynomialRing<BigRational>(fac, 1, to);
        //System.out.println("ufac  = " + ufac);
        cfac = new GenPolynomialRing<GenPolynomial<BigRational>>(ufac, rl, to);
        //System.out.println("cfac  = " + cfac);
        dfac = new GenPolynomialRing<BigRational>(fac, rl, to);
        //System.out.println("dfac  = " + dfac);
        int maxdeg = 19;

        // polynomial to interpolate
        long deg = 0;
        do {
            a = cfac.random(kl, ll + 9, maxdeg, q);
            if (!a.isZERO()) {
                deg = PolyUtil.<BigRational> coeffMaxDegree(a);
            }
        } while (deg <= 0);
        //System.out.println("a  = " + a);
        //System.out.println("deg  = " + deg);
        ExpVector degv = a.degreeVector();
        //System.out.println("degv  = " + degv);

        // interpolation result
        r = cfac.getZERO();
        //System.out.println("r   = " + r);

        // interpolation polynomials product
        Q = ufac.getONE();
        //System.out.println("Q   = " + Q);

        long i = -1;
        long qdeg;
        ExpVector qdegv;
        do {
            i++;
            qdeg = Q.degree(0);
            ei = fac.fromInteger(i);
            //System.out.println("ei  = " + ei);
            // a(ei)
            ap = PolyUtil.<BigRational> evaluateFirstRec(ufac, dfac, a, ei);
            //System.out.println("ap   = " + ap);
            qdegv = ap.degreeVector();
            //System.out.println("qdegv = " + qdegv);
            if (!degv.equals(qdegv)) {
                continue;
            }
            ci = PolyUtil.<BigRational> evaluateMain(fac, Q, ei);
            //System.out.println("ci   = " + ci);
            // Q(ei)^-1
            fi = ci.inverse();
            //System.out.println("fi   = " + fi);
            r = PolyUtil.<BigRational> interpolate(cfac, r, Q, fi, ap, ei);
            //System.out.println("r   = " + r);

            // check
            bp = PolyUtil.<BigRational> evaluateFirstRec(ufac, dfac, r, ei);
            //System.out.println("bp   = " + bp);
            assertEquals("interpolate(a)(ei) == a ", bp, ap);


            // next evaluation polynomial
            Qp = ufac.univariate(0);
            Qp = Qp.subtract(ufac.getONE().multiply(ei));
            //System.out.println("Qp   = " + Qp);
            Q = Q.multiply(Qp);
            //System.out.println("Q   = " + Q);
        } while (qdeg <= deg);

        //System.out.println("a   = " + a);
        //System.out.println("r   = " + r);

        //            interpolate( a(e1), ..., a(ei) )           = a (mod 19)
        assertEquals("interpolate(a mod (x-e1),...,a mod (x-ei)) = a (mod 19)", a, r);
    }


    /**
     * Test coefficient map function.
     */
    public void testMap() {
        // integers
        BigInteger fi = new BigInteger();
        //System.out.println("fi = " + fi);

        // rational numbers
        BigRational fr = new BigRational();
        //System.out.println("fr = " + fr);

        // modular integers
        ModIntegerRing fm = new ModIntegerRing(17);
        //System.out.println("fm = " + fm);

        // polynomials over integral numbers
        GenPolynomialRing<BigInteger> pfi = new GenPolynomialRing<BigInteger>(fi, rl);
        //System.out.println("pfi = " + pfi);

        // polynomials over rational numbers
        GenPolynomialRing<BigRational> pfr = new GenPolynomialRing<BigRational>(fr, rl);
        //System.out.println("pfr = " + pfr);

        // polynomials over modular integers
        GenPolynomialRing<ModInteger> pfm = new GenPolynomialRing<ModInteger>(fm, rl);
        //System.out.println("pfm = " + pfm);


        // random polynomial
        GenPolynomial<BigInteger> pi = pfi.random(kl, 2 * ll, el, q);
        //System.out.println("pi = " + pi);

        // random polynomial
        GenPolynomial<BigRational> pr = pfr.random(kl, 2 * ll, el, q);
        //System.out.println("pr = " + pr);

        // random polynomial
        GenPolynomial<ModInteger> pm = pfm.random(kl, 2 * ll, el, q);
        //System.out.println("pm = " + pm);

        // test integer to rational and back
        GenPolynomial<BigRational> qr;
        GenPolynomial<BigInteger> qi;
        qr = PolyUtil.<BigInteger, BigRational> map(pfr, pi, new FromInteger<BigRational>(fr));
        qi = PolyUtil.<BigRational, BigInteger> map(pfi, qr, new RatNumer());
        //System.out.println("qr = " + qr);
        //System.out.println("qi = " + qi);
        assertEquals("pi == qi ", pi, qi);

        // test symmetric modular integer to integer and back
        GenPolynomial<ModInteger> qm;
        qi = PolyUtil.<ModInteger, BigInteger> map(pfi, pm, new ModSymToInt<ModInteger>());
        qm = PolyUtil.<BigInteger, ModInteger> map(pfm, qi, new FromInteger<ModInteger>(fm));
        //System.out.println("qi = " + qi);
        //System.out.println("qm = " + qm);
        assertEquals("pm == qm ", pm, qm);

        // test modular integer to integer and back
        qi = PolyUtil.<ModInteger, BigInteger> map(pfi, pm, new ModToInt<ModInteger>());
        qm = PolyUtil.<BigInteger, ModInteger> map(pfm, qi, new FromInteger<ModInteger>(fm));
        //System.out.println("qi = " + qi);
        //System.out.println("qm = " + qm);
        assertEquals("pm == qm ", pm, qm);

        // test symmetric modular integer to integer to rational and back
        qi = PolyUtil.<ModInteger, BigInteger> map(pfi, pm, new ModSymToInt<ModInteger>());
        qr = PolyUtil.<BigInteger, BigRational> map(pfr, qi, new FromInteger<BigRational>(fr));
        qi = PolyUtil.<BigRational, BigInteger> map(pfi, qr, new RatNumer());
        qm = PolyUtil.<BigInteger, ModInteger> map(pfm, qi, new FromInteger<ModInteger>(fm));
        //System.out.println("qi = " + qi);
        //System.out.println("qm = " + qm);
        assertEquals("pm == qm ", pm, qm);
    }


    /**
     * Test substitution.
     */
    public void testSubstitution() {
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), 1, to);

        // subs = x - 7
        GenPolynomial<BigInteger> s = dfac.univariate(0).subtract(dfac.fromInteger(7));
        GenPolynomial<BigInteger> s1 = dfac.univariate(0).sum(dfac.fromInteger(7));
        //System.out.println("s = " + s);
        //System.out.println("s1 = " + s1);

        for (int i = 0; i < 5; i++) {
            a = dfac.random(kl, ll, el, q);
            //System.out.println("a = " + a);
            b = PolyUtil.<BigInteger> substituteMain(a, s);
            c = PolyUtil.<BigInteger> substituteMain(b, s1);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);
            //System.out.println("a == c " + a.equals(c));
            assertEquals("a == c ", a, c);
        }
    }


    /**
     * Test algebraic substitution.
     */
    public void testAlgebraicSubstitution() {

        BigRational cfac = new BigRational(1);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "z" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, alpha);
        GenPolynomial<BigRational> agen = pfac.univariate(0, 2);
        agen = agen.sum(pfac.getONE()); // x^2 + 1
        AlgebraicNumberRing<BigRational> afac = new AlgebraicNumberRing<BigRational>(agen, true);
        GenPolynomialRing<AlgebraicNumber<BigRational>> apfac 
           = new GenPolynomialRing<AlgebraicNumber<BigRational>>(afac, 1, to, vars); // univariate

        //System.out.println("agen  = " + agen);
        //System.out.println("afac  = " + afac);
        //System.out.println("apfac = " + apfac);

        // subs = x - 7
        GenPolynomial<AlgebraicNumber<BigRational>> s 
           = apfac.univariate(0).subtract(apfac.fromInteger(7).multiply(afac.getGenerator()));
        GenPolynomial<AlgebraicNumber<BigRational>> s1 
           = apfac.univariate(0).sum(apfac.fromInteger(7).multiply(afac.getGenerator()));
        //System.out.println("s = " + s);
        //System.out.println("s1 = " + s1);

        GenPolynomial<AlgebraicNumber<BigRational>> a, b, c;
        for (int i = 0; i < 5; i++) {
            a = apfac.random(kl, ll, el, q);
            //System.out.println("a = " + a);
            b = PolyUtil.<AlgebraicNumber<BigRational>> substituteMain(a, s);
            c = PolyUtil.<AlgebraicNumber<BigRational>> substituteMain(b, s1);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);
            //System.out.println("a == c " + a.equals(c));
            assertEquals("a == c ", a, c);
        }
    }


    /**
     * Test switch variables.
     */
    public void testSwitchVariables() {

        BigRational cfac = new BigRational(1);
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, rl, to);
        GenPolynomialRing<GenPolynomial<BigRational>> rfac 
           = new GenPolynomialRing<GenPolynomial<BigRational>>(pfac, rl, to);

        //System.out.println("pfac  = " + pfac);
        //System.out.println("rfac  = " + rfac);

        GenPolynomial<GenPolynomial<BigRational>> a, c;
        GenPolynomial<GenPolynomial<BigRational>> b;
        for (int i = 0; i < 5; i++) {
            a = rfac.random(kl, ll, el, q);
            //System.out.println("a = " + a);
            b = PolyUtil.<BigRational> switchVariables(a);
            c = PolyUtil.<BigRational> switchVariables(b);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);
            //System.out.println("a == c " + a.equals(c));
            assertEquals("a == c ", a, c);
        }
    }


    /**
     * Test algebraic conversions.
     */
    public void testAlgebraicConversions() {

        BigRational cfac = new BigRational(1);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "z" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, alpha);
        GenPolynomial<BigRational> agen = pfac.univariate(0, 2);
        agen = agen.sum(pfac.getONE()); // x^2 + 1
        AlgebraicNumberRing<BigRational> afac = new AlgebraicNumberRing<BigRational>(agen, true);
        GenPolynomialRing<AlgebraicNumber<BigRational>> apfac 
           = new GenPolynomialRing<AlgebraicNumber<BigRational>>(afac, rl, to);
        GenPolynomialRing<GenPolynomial<BigRational>> rfac 
           = new GenPolynomialRing<GenPolynomial<BigRational>>(pfac, rl, to);

        //System.out.println("agen  = " + agen);
        //System.out.println("afac  = " + afac);
        //System.out.println("apfac = " + apfac);
        //System.out.println("pfac  = " + pfac);
        //System.out.println("rfac  = " + rfac);

        GenPolynomial<AlgebraicNumber<BigRational>> a, c;
        GenPolynomial<GenPolynomial<BigRational>> b;
        for (int i = 0; i < 5; i++) {
            a = apfac.random(kl, ll, el, q);
            //System.out.println("a = " + a);
            b = PolyUtil.<BigRational> fromAlgebraicCoefficients(rfac, a);
            c = PolyUtil.<BigRational> convertRecursiveToAlgebraicCoefficients(apfac, b);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);
            //System.out.println("a == c " + a.equals(c));
            assertEquals("a == c ", a, c);
        }
    }


    /**
     * Test Taylor series.
     */
    public void testTaylorSeries() {
        GenPolynomial<BigRational> a;
        GenPolynomial<BigRational> b;
        GenPolynomial<BigRational> c;
        GenPolynomialRing<BigRational> dfac;
        BigRational cfac;

        cfac = new BigRational(1);
        String[] vars = new String[] { "x" };
        dfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);

        a = dfac.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        BigRational v = cfac.getZERO();
        //System.out.println("v = " + v);

        b = PolyUtil.<BigRational> seriesOfTaylor(a, v);
        //System.out.println("taylor(a,0) = " + b);
        assertTrue("taylor(a,0) == a ", a.equals(b));

        v = cfac.random(kl);
        //System.out.println("v = " + v);

        b = PolyUtil.<BigRational> seriesOfTaylor(a, v);
        //System.out.println("taylor(a,v) = " + b);

        c = PolyUtil.<BigRational> seriesOfTaylor(b, v.negate());
        //System.out.println("tailor(taylor(a,v),-v) = " + c);
        assertTrue("tailor(taylor(a,v),-v) == a ", a.equals(c));
    }


    /**
     * Test Complex real and imaginary part.
     */
    public void testComplexParts() {
        BigRational rf = new BigRational(1);
        GenPolynomialRing<BigRational> rfac = new GenPolynomialRing<BigRational>(rf, rl, to);

        ComplexRing<BigRational> cf = new ComplexRing<BigRational>(new BigRational(1));
        GenPolynomialRing<Complex<BigRational>> cfac = new GenPolynomialRing<Complex<BigRational>>(cf, rl, to);

        Complex<BigRational> imag = cf.getIMAG();

        GenPolynomial<BigRational> rp;
        GenPolynomial<BigRational> ip;
        GenPolynomial<Complex<BigRational>> crp;
        GenPolynomial<Complex<BigRational>> cip;
        GenPolynomial<Complex<BigRational>> cp;
        GenPolynomial<Complex<BigRational>> ap;

        for (int i = 0; i < 3; i++) {
            cp = cfac.random(kl + 2 * i, ll * (i + 1), el + i, q);

            assertTrue("length( c" + i + " ) <> 0", cp.length() >= 0);
            assertTrue(" not isZERO( c" + i + " )", !cp.isZERO());
            assertTrue(" not isONE( c" + i + " )", !cp.isONE());

            rp = PolyUtil.<BigRational> realPartFromComplex(rfac, cp);
            ip = PolyUtil.<BigRational> imaginaryPartFromComplex(rfac, cp);

            crp = PolyUtil.<BigRational> toComplex(cfac, rp);
            cip = PolyUtil.<BigRational> toComplex(cfac, ip);

            ap = crp.sum(cip.multiply(imag));

            //System.out.println("cp = " + cp);
            //System.out.println("rp = " + rp);
            //System.out.println("ip = " + ip);
            //System.out.println("crp = " + crp);
            //System.out.println("cip = " + cip);
            //System.out.println("ap  = " + ap);

            assertEquals("re(c)+i*im(c) = c", cp, ap);
        }
    }


    /**
     * Test product represenation conversion, rational numbers.
     */
    public void testProductConversionRN() {
        GenPolynomialRing<BigRational> ufac;
        ufac = new GenPolynomialRing<BigRational>(new BigRational(1), 1);

        ProductRing<GenPolynomial<BigRational>> pfac;
        pfac = new ProductRing<GenPolynomial<BigRational>>(ufac, rl);

        GenPolynomialRing<BigRational> dfac = new GenPolynomialRing<BigRational>(new BigRational(1), rl, to);

        GenPolynomial<BigRational> c;
        Product<GenPolynomial<BigRational>> cp;

        c = dfac.getONE();
        //System.out.println("c = " + c);
 
        cp = PolyUtil.<BigRational> toProduct(pfac, c);
        //System.out.println("cp = " + cp);
        assertTrue("isONE( cp )", cp.isONE());

        c = dfac.random(kl, ll, el, q);
        //System.out.println("c = " + c);

        cp = PolyUtil.<BigRational> toProduct(pfac, c);
        //System.out.println("cp = " + cp);
        assertTrue("!isONE( cp )", !cp.isONE());
    }


    /**
     * Test polynomal over product represenation conversion, algebraic numbers.
     */
    public void testPolyProductConversionAN() {
        GenPolynomialRing<BigRational> ufac;
        ufac = new GenPolynomialRing<BigRational>(new BigRational(1), 1);

        GenPolynomial<BigRational> m;
        m = ufac.univariate(0, 2);
        m = m.subtract(ufac.univariate(0, 1));
        //System.out.println("m = " + m);

        AlgebraicNumberRing<BigRational> afac;
        afac = new AlgebraicNumberRing<BigRational>(m);
        //System.out.println("afac = " + afac);

        ProductRing<AlgebraicNumber<BigRational>> pfac;
        pfac = new ProductRing<AlgebraicNumber<BigRational>>(afac, rl);

        GenPolynomialRing<Product<AlgebraicNumber<BigRational>>> dpfac;
        dpfac = new GenPolynomialRing<Product<AlgebraicNumber<BigRational>>>(pfac, 2);

        GenPolynomialRing<AlgebraicNumber<BigRational>> dfac;
        dfac = new GenPolynomialRing<AlgebraicNumber<BigRational>>(afac, 2, to);


        GenPolynomial<AlgebraicNumber<BigRational>> c;
        GenPolynomial<Product<AlgebraicNumber<BigRational>>> cp;

        c = dfac.getONE();
        //System.out.println("c = " + c);

        cp = PolyUtil.<AlgebraicNumber<BigRational>> toProductGen(dpfac, c);
        //System.out.println("cp = " + cp);
        assertTrue("isZERO( cp )", cp.isONE());

        c = dfac.random(kl, ll, el, q);
        //System.out.println("c = " + c);

        cp = PolyUtil.<AlgebraicNumber<BigRational>> toProductGen(dpfac, c);
        //System.out.println("cp = " + cp);
        assertTrue("!isONE( cp )", !cp.isONE());
    }

}
