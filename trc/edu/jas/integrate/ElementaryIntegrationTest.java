/*
 * $Id$
 */

package edu.jas.integrate;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.poly.PolyUtil;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;

import edu.jas.kern.ComputerThreads;


/**
 * Quotient over BigRational GenPolynomial tests with JUnit. 
 * @author Heinz Kredel.
 */

public class ElementaryIntegrationTest extends TestCase {

    /**
     * main.
     */
    public static void main (String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run( suite() );
    }

    /**
     * Constructs a <CODE>ElementaryIntegrationTest</CODE> object.
     * @param name String.
     */
    public ElementaryIntegrationTest(String name) {
        super(name);
    }

    /**
     * suite.
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(ElementaryIntegrationTest.class);
        return suite;
    }

    //private final static int bitlen = 100;

    TermOrder tord;
    QuotientRing<BigRational> qfac;
    GenPolynomialRing<BigRational> mfac;
    ElementaryIntegration<BigRational> integrator;
    QuotIntegral<BigRational> rint;

    Quotient< BigRational > a;
    Quotient< BigRational > b;
    Quotient< BigRational > c;
    Quotient< BigRational > d;
    Quotient< BigRational > e;

    int rl = 1; // only univariate polynomials
    int kl = 5;
    int ll = 3; //6;
    int el = 4;
    float q = 0.4f;

    protected void setUp() {
        a = b = c = d = e = null;
        tord = new TermOrder( TermOrder.INVLEX );
        BigRational br = new BigRational(1);
        String[] vars = new String[]{ "x" };
        mfac = new GenPolynomialRing<BigRational>( br, rl, tord, vars );
        qfac = new QuotientRing<BigRational>( mfac );
        integrator = new ElementaryIntegration<BigRational>(br);
    }

    protected void tearDown() {
        a = b = c = d = e = null;
        //qfac.terminate();
        qfac = null;
        ComputerThreads.terminate();
    }


    /**
     * Test rational integral.
     */
    public void testRational() {
        for (int i = 0; i < 3; i++) {
            a = qfac.random(kl, ll+2*i, el+i, q );
            //System.out.println("a = " + a);
//             if ( a.isZERO() || a.isONE() ) {
//                 continue;
//             }
            b = integrator.deriviative(a);
            //System.out.println("b =  " + b);
            rint = integrator.integrate(b);
            //System.out.println("QuotIntegral: " + rint);

            assertTrue("isIntegral ", integrator.isIntegral(rint));
        }
    }


    /**
     * Test 1/p pure logarithm integral.
     */
    public void testPureLogarithm1() {
        for (int i = 0; i < 3; i++) {
            a = qfac.random(kl, ll+i, el+i, q );
            //System.out.println("a = " + a);
//             if ( a.isZERO() || a.isONE() ) {
//                 continue;
//             }
            b = new Quotient<BigRational>(qfac,qfac.getONE().num,a.den);
            //System.out.println("b =  " + b);
            rint = integrator.integrate(b);
            //System.out.println("QuotIntegral: " + rint);

            assertTrue("isIntegral ", integrator.isIntegral(rint));
        }
    }


    /**
     * Test p'/p pure logarithm integral.
     * 
     */
    public void testPureLogarithmD() {
        for (int i = 0; i < 3; i++) {
            a = qfac.random(kl, ll+i, el+i, q );
            //System.out.println("a = " + a);
//             if ( a.isZERO() || a.isONE() ) {
//                 continue;
//             }
            GenPolynomial<BigRational> pp = PolyUtil.<BigRational> baseDeriviative(a.den);
            b = new Quotient<BigRational>(qfac, pp ,a.den);
            //System.out.println("b =  " + b);
            rint = integrator.integrate(b);
            //System.out.println("QuotIntegral: " + rint);

            assertTrue("isIntegral ", integrator.isIntegral(rint));
        }
    }


    /**
     * Test mixed rational with p'/p logarithm integral.
     */
    public void testRationalWithLogarithmD() {
        for (int i = 0; i < 3; i++) {
            a = qfac.random(kl, ll+i, el+i, q );
            //System.out.println("a = " + a);
//             if ( a.isZERO() || a.isONE() ) {
//                 continue;
//             }

            b = integrator.deriviative(a);
            //System.out.println("b =  " + b);

            GenPolynomial<BigRational> pp = PolyUtil.<BigRational> baseDeriviative(a.den);
            c = new Quotient<BigRational>(qfac, pp ,a.den);
            //System.out.println("c =  " + c);

            e = b.sum(c); 
            //System.out.println("e =  " + e);

            rint = integrator.integrate(e);
            //System.out.println("QuotIntegral: " + rint);

            assertTrue("isIntegral ", integrator.isIntegral(rint));
        }
    }


    /**
     * Test mixed rational with 1/p logarithm integral.
     */
    public void xtestRationalWithLogarithm1() {
        for (int i = 0; i < 3; i++) {
            a = qfac.random(kl, ll+i, el+i, q );
            //System.out.println("a = " + a);
//             if ( a.isZERO() || a.isONE() ) {
//                 continue;
//             }

            b = integrator.deriviative(a);
            //System.out.println("b =  " + b);

            d = new Quotient<BigRational>(qfac,qfac.getONE().num,a.den);
            //System.out.println("d =  " + d);

            e = b.sum(d);
            //System.out.println("e =  " + e);

            rint = integrator.integrate(e);
            //System.out.println("QuotIntegral: " + rint);

            assertTrue("isIntegral ", integrator.isIntegral(rint));
        }
    }


    /**
     * Test mixed rational with p'/p + 1/p logarithm integral.
     * 
     */
    public void testRationalWithLogarithm() {
        for (int i = 0; i < 3; i++) {
            a = qfac.random(kl, ll+i, el+i, q );
            //System.out.println("a = " + a);
//             if ( a.isZERO() || a.isONE() ) {
//                 continue;
//             }

            b = integrator.deriviative(a);
            //System.out.println("b =  " + b);

            GenPolynomial<BigRational> pp = PolyUtil.<BigRational> baseDeriviative(a.den);
            c = new Quotient<BigRational>(qfac, pp ,a.den);
            //System.out.println("c =  " + c);

            d = new Quotient<BigRational>(qfac,qfac.getONE().num,a.den);
            //System.out.println("d =  " + d);

            e = b.sum(c).sum(d);
            //System.out.println("e =  " + e);

            rint = integrator.integrate(e);
            //System.out.println("QuotIntegral: " + rint);

            assertTrue("isIntegral ", integrator.isIntegral(rint));
        }
    }


    /**
     * Test rational integral with quotient coefficients.
     */
    public void testRationalRecursive() {
        QuotientRing<Quotient<BigRational>> qqfac;
        GenPolynomialRing<Quotient<BigRational>> qmfac;
        ElementaryIntegration<Quotient<BigRational>> qintegrator;
        QuotIntegral<Quotient<BigRational>> qrint;
        String[] vars = new String[]{ "y" };

        qmfac = new GenPolynomialRing<Quotient<BigRational>>(qfac,1,tord,vars);
        qqfac = new QuotientRing<Quotient<BigRational>>(qmfac);

        qintegrator = new ElementaryIntegration<Quotient<BigRational>>(qfac);

        Quotient< Quotient< BigRational > > qa, qb;

        for (int i = 0; i < 2; i++) {
            qa = qqfac.random(2, ll, el, q );
            //System.out.println("qa = " + qa);
            //             if ( a.isZERO() || a.isONE() ) {
            //                 continue;
            //             }
            qb = qintegrator.deriviative(qa);
            //System.out.println("qb =  " + qb);
            qrint = qintegrator.integrate(qb);
            //System.out.println("QuotIntegral: " + qrint);

            assertTrue("isIntegral ", qintegrator.isIntegral(qrint));
        }
    }


    /**
     * Test mixed rational integral.
     */
    public void testMixedRational() {
        //integrate( (3*x^16-19*x^15+43*x^14-20*x^13-91*x^12+183*x^11-81*x^10-166*x^9+271*x^8-101*x^7-127*x^6+168*x^5-53*x^4-31*x^3+41*x^2-2*x-2)/(4*x^14-20*x^13+28*x^12+24*x^11-108*x^10+84*x^9+76*x^8-176*x^7+76*x^6+84*x^5-108*x^4+24*x^3+28*x^2-20*x+4), x)

        BigRational br = new BigRational(1);
        String[] vars = new String[]{ "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br,vars);
        QuotientRing<BigRational> qfac = new QuotientRing<BigRational>(pfac);
        GenPolynomial<BigRational> n = pfac.parse("(3*x^16-19*x^15+43*x^14-20*x^13-91*x^12+183*x^11-81*x^10-166*x^9+271*x^8-101*x^7-127*x^6+168*x^5-53*x^4-31*x^3+41*x^2-2*x-2)");
        GenPolynomial<BigRational> d = pfac.parse("(4*x^14-20*x^13+28*x^12+24*x^11-108*x^10+84*x^9+76*x^8-176*x^7+76*x^6+84*x^5-108*x^4+24*x^3+28*x^2-20*x+4)");
        //System.out.println("n = " + n);
        //System.out.println("d = " + d);
        Quotient<BigRational> a = new Quotient<BigRational>(qfac,n,d);
        //System.out.println("a = " + a);

        QuotIntegral<BigRational> rint = integrator.integrate(a);
        //System.out.println("QuotIntegral: " + rint);
        assertTrue("isIntegral ", integrator.isIntegral(rint));

        Quotient<BigRational> b = qfac.parse("{ 3*x^16-19*x^15+43*x^14-20*x^13-91*x^12+183*x^11-81*x^10-166*x^9+271*x^8-101*x^7-127*x^6+168*x^5-53*x^4-31*x^3+41*x^2-2*x-2 | 4*x^14-20*x^13+28*x^12+24*x^11-108*x^10+84*x^9+76*x^8-176*x^7+76*x^6+84*x^5-108*x^4+24*x^3+28*x^2-20*x+4 }");
        //System.out.println("b = " + b);
        assertEquals("a == b: ", a, b);

        rint = integrator.integrate(b);
        //System.out.println("QuotIntegral: " + rint);
        assertTrue("isIntegral ", integrator.isIntegral(rint));
    }

}
