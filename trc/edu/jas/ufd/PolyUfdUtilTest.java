/*
 * $Id$
 */

package edu.jas.ufd;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;

import edu.jas.kern.ComputerThreads;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.poly.PolyUtil;


/**
 * PolyUfdUtil tests with JUnit.
 * @author Heinz Kredel.
 */

public class PolyUfdUtilTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>PolyUtilTest</CODE> object.
     * @param name String.
     */
    public PolyUfdUtilTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(PolyUfdUtilTest.class);
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
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl, to);
        cfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cfac, 1, to);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        dfac = null;
        cfac = null;
        rfac = null;
        ComputerThreads.terminate();
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
     * Test Kronecker substitution.
     */
    public void testKroneckerSubstitution() {

        for (int i = 0; i < 10; i++) {
            a = dfac.random(kl, ll * 2, el * 5, q);
            long d = a.degree() + 1L;
            //System.out.println("\na        = " + a);
            //System.out.println("deg(a)+1 = " + d);

            b = PolyUfdUtil.<BigInteger> substituteKronecker(a, d);
            //System.out.println("b        = " + b);

            c = PolyUfdUtil.<BigInteger> backSubstituteKronecker(dfac, b, d);
            //System.out.println("c        = " + c);
            e = a.subtract(c);
            //System.out.println("e        = " + e);
            assertTrue("back(subst(a)) = a", e.isZERO());
        }
    }


    /**
     * Test recursive dense pseudo division.
     */
    public void testRecursivePseudoDivisionDense() {
        String[] cnames = new String[] { "x" };
        String[] mnames = new String[] { "t" };
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),to,cnames);
        //GenPolynomialRing<BigRational> rdfac = new GenPolynomialRing<BigRational>(new BigRational(1),dfac);
        rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(dfac, to, mnames);
        QuotientRing<BigInteger> qfac = new QuotientRing<BigInteger>(dfac);
        GenPolynomialRing<Quotient<BigInteger>> rqfac = new GenPolynomialRing<Quotient<BigInteger>>(qfac,rfac);
        //System.out.println("\ndfac  = " + dfac);
        //System.out.println("rdfac = " + rdfac);
        //System.out.println("rfac  = " + rfac);
        //System.out.println("qfac  = " + qfac);
        //System.out.println("rqfac = " + rqfac);

        ar = rfac.random(kl, 2*ll, el+4, q);
        //ar = rfac.parse(" ( -2 x^4 + 8 x^3 - 5 x^2 - x + 6  ) t^3 + ( 2 x - 8  ) t^2 - ( 13 x^4 - 13 x^3 + x^2 + 2 x - 13  ) ");
        br = rfac.random(kl, 2*ll, el+2, q);
        //ar = ar.multiply(br);
        //br = rfac.parse(" ( 13 ) t^3 + ( 3 x^2 - 6  ) t - ( 13 x^4 - 8 x^3 + 10 x^2 + 22 x + 21  ) ");
        //System.out.println("ar   = " + ar);
        //System.out.println("br   = " + br);

        dr = PolyUtil.<BigInteger> recursivePseudoDivide(ar, br);
        //System.out.println("qr   = " + dr);
        cr = PolyUtil.<BigInteger> recursiveDensePseudoRemainder(ar, br);
        //System.out.println("rr   = " + cr);

        boolean t = PolyUtil.<BigInteger> isRecursivePseudoQuotientRemainder(ar, br, dr, cr);
        //System.out.println("assertTrue lc^n a = q b + r: " + t);
        //assertTrue("lc^n a = q b + r: " + cr, t); // ?? not always true

        GenPolynomial<Quotient<BigInteger>> ap = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(rqfac,ar);
        GenPolynomial<Quotient<BigInteger>> bp = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(rqfac,br);
        GenPolynomial<Quotient<BigInteger>> cp = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(rqfac,cr);
        GenPolynomial<Quotient<BigInteger>> dp = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(rqfac,dr);
        //System.out.println("ap  = " + ap);
        //System.out.println("bp  = " + bp);
        //System.out.println("cp  = " + cp);
        ////System.out.println("dp  = " + dp);
        //System.out.println("dp  = " + dp.monic());

        GenPolynomial<Quotient<BigInteger>> qp = ap.divide(bp);
        GenPolynomial<Quotient<BigInteger>> rp = ap.remainder(bp);
        ////System.out.println("qp  = " + qp);
        //System.out.println("qp  = " + qp.monic());
        //System.out.println("rp  = " + rp);
        GenPolynomial<Quotient<BigInteger>> rhs = qp.multiply(bp).sum(rp);
        //System.out.println("qp bp + rp  = " + rhs);

        assertEquals("ap = qp bp + rp: ", ap, rhs);

        assertEquals("cp = rp: ", rp.monic(), cp.monic() );
        //System.out.println("dp = qp: " + qp.monic().equals(dp.monic()) );
        assertEquals("dp = qp: ", qp.monic(), dp.monic() ); // ??
    }


    /**
     * Test recursive sparse pseudo division.
     */
    public void testRecursivePseudoDivisionSparse() {
        String[] cnames = new String[] { "x" };
        String[] mnames = new String[] { "t" };
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),to,cnames);
        //GenPolynomialRing<BigRational> rdfac = new GenPolynomialRing<BigRational>(new BigRational(1),dfac);
        rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(dfac, to, mnames);
        QuotientRing<BigInteger> qfac = new QuotientRing<BigInteger>(dfac);
        GenPolynomialRing<Quotient<BigInteger>> rqfac = new GenPolynomialRing<Quotient<BigInteger>>(qfac,rfac);
        //System.out.println("\ndfac  = " + dfac);
        //System.out.println("rdfac = " + rdfac);
        //System.out.println("rfac  = " + rfac);
        //System.out.println("qfac  = " + qfac);
        //System.out.println("rqfac = " + rqfac);

        ar = rfac.random(kl, 2*ll, el+4, q);
        //ar = rfac.parse(" ( -2 x^4 + 8 x^3 - 5 x^2 - x + 6  ) t^3 + ( 2 x - 8  ) t^2 - ( 13 x^4 - 13 x^3 + x^2 + 2 x - 13  ) ");
        br = rfac.random(kl, 2*ll, el+2, q);
        //ar = ar.multiply(br);
        //br = rfac.parse(" ( 13 ) t^3 + ( 3 x^2 - 6  ) t - ( 13 x^4 - 8 x^3 + 10 x^2 + 22 x + 21  ) ");
        //System.out.println("ar   = " + ar);
        //System.out.println("br   = " + br);

        dr = PolyUtil.<BigInteger> recursivePseudoDivide(ar, br);
        //System.out.println("qr   = " + dr);
        cr = PolyUtil.<BigInteger> recursiveSparsePseudoRemainder(ar, br);
        //System.out.println("rr   = " + cr);

        boolean t = PolyUtil.<BigInteger> isRecursivePseudoQuotientRemainder(ar, br, dr, cr);
        //System.out.println("assertTrue lc^n a = q b + r: " + t);
        //assertTrue("lc^n a = q b + r: " + cr, t); // ?? not always true

        GenPolynomial<Quotient<BigInteger>> ap = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(rqfac,ar);
        GenPolynomial<Quotient<BigInteger>> bp = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(rqfac,br);
        GenPolynomial<Quotient<BigInteger>> cp = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(rqfac,cr);
        GenPolynomial<Quotient<BigInteger>> dp = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(rqfac,dr);
        //System.out.println("ap  = " + ap);
        //System.out.println("bp  = " + bp);
        //System.out.println("cp  = " + cp);
        ////System.out.println("dp  = " + dp);
        //System.out.println("dp  = " + dp.monic());

        GenPolynomial<Quotient<BigInteger>> qp = ap.divide(bp);
        GenPolynomial<Quotient<BigInteger>> rp = ap.remainder(bp);
        ////System.out.println("qp  = " + qp);
        //System.out.println("qp  = " + qp.monic());
        //System.out.println("rp  = " + rp);
        GenPolynomial<Quotient<BigInteger>> rhs = qp.multiply(bp).sum(rp);
        //System.out.println("qp bp + rp  = " + rhs);

        assertEquals("ap = qp bp + rp: ", ap, rhs);

        assertEquals("cp = rp: ", rp.monic(), cp.monic() );
        //System.out.println("dp = qp: " + qp.monic().equals(dp.monic()) );
        assertEquals("dp = qp: ", qp.monic(), dp.monic() ); // ??
    }

}
