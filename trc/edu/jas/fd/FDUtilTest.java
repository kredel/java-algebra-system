/*
 * $Id$
 */

package edu.jas.fd;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigComplex;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.Product;
import edu.jas.arith.ProductRing;

import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.RecSolvablePolynomial;
import edu.jas.poly.RecSolvablePolynomialRing;
import edu.jas.poly.RelationGenerator;
import edu.jas.poly.WeylRelationsIterated;

import edu.jas.kern.ComputerThreads;
import edu.jas.ufd.PolyUfdUtil;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;


/**
 * FDUtil tests with JUnit.
 * @author Heinz Kredel.
 */

public class FDUtilTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>FDUtilTest</CODE> object.
     * @param name String.
     */
    public FDUtilTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(FDUtilTest.class);
        return suite;
    }


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenSolvablePolynomialRing<BigInteger> dfac;


    GenSolvablePolynomialRing<BigRational> rdfac;


    //GenSolvablePolynomialRing<BigRational> cfac;


    GenSolvablePolynomialRing<GenPolynomial<BigInteger>> rfac;


    GenSolvablePolynomialRing<GenPolynomial<BigRational>> rrfac;


    //BigRational ai, bi, ci, di, ei;


    GenSolvablePolynomial<BigInteger> a, b, c, d, e, f;


    GenSolvablePolynomial<GenPolynomial<BigInteger>> ar, br, cr, dr, er, fr;


    GenSolvablePolynomial<GenPolynomial<BigRational>> arr, brr, crr, drr, err, frr;


    int rl = 4;


    int kl = 3;


    int ll = 4;


    int el = 3;


    float q = 0.35f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        //ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        String[] vars = new String[] { "a", "b", "c", "d" };
        dfac = new GenSolvablePolynomialRing<BigInteger>(new BigInteger(1), rl, to, vars);
        RelationGenerator<BigInteger> wl = new WeylRelationsIterated<BigInteger>();
        dfac.addRelations(wl);
        rfac = (RecSolvablePolynomialRing<BigInteger>) dfac.recursive(1);
        //cfac = (GenSolvablePolynomialRing<BigInteger>) rfac.coFac;
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        //ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        dfac = null;
        //cfac = null;
        rfac = null;
    }


    /**
     * Test base pseudo division.
     */
    public void xtestBasePseudoDivision() {
        String[] names = new String[] { "x" };
        dfac = new GenSolvablePolynomialRing<BigInteger>(new BigInteger(1),to,names);
        GenSolvablePolynomialRing<BigRational> rdfac;
        rdfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1),dfac);
        //System.out.println("\ndfac  = " + dfac);

        a = dfac.random(kl, 2*ll, el+17, q);
        //a = dfac.parse(" 3 x^5 + 44 ");
        //b = a;
        b = dfac.random(kl, 2*ll, el+3, q);
        //a = a.multiply(b);
        //a = a.sum(b);
        //b = dfac.parse(" 2 x^2 + 40 ");
        System.out.println("a   = " + a);
        System.out.println("b   = " + b);

        GenPolynomial<BigInteger>[] QR = PolyUtil.<BigInteger> basePseudoQuotientRemainder(a, b);
        c = (GenSolvablePolynomial<BigInteger>) QR[0];
        d = (GenSolvablePolynomial<BigInteger>) QR[1];
        System.out.println("q   = " + c);
        System.out.println("r   = " + d);

        GenSolvablePolynomial<BigInteger> n = (GenSolvablePolynomial<BigInteger>) c.multiply(b).sum(d);
        //System.out.println("n   = " + n); // + ", " + m.monic());
        //System.out.println("a   = " + a); // + ", " + a.monic());

        boolean t = PolyUtil.<BigInteger> isBasePseudoQuotientRemainder(a, b, c, d);
        assertTrue("lc^n a = q b + r: " + d, t);

        GenSolvablePolynomial<BigInteger>[] QRs = FDUtil.<BigInteger> basePseudoQuotientRemainder(a, b);
        e = QRs[0];
        f = QRs[1];
        System.out.println("");
        System.out.println("q   = " + e);
        System.out.println("r   = " + f);

        GenSolvablePolynomial<BigInteger> m = (GenSolvablePolynomial<BigInteger>) e.multiply(b).sum(f);
        System.out.println("n   = " + n); // + ", " + m.monic());
        System.out.println("m   = " + m); // + ", " + m.monic());
        System.out.println("a   = " + a); // + ", " + a.monic());

        t = PolyUtil.<BigInteger> isBasePseudoQuotientRemainder(a, b, e, f);
        assertTrue("ore(lc^n) a = q b + r: " + f, t);

        // compare with field coefficients:
        GenSolvablePolynomial<BigRational> ap = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> fromIntegerCoefficients(rdfac,a);
        GenSolvablePolynomial<BigRational> bp = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> fromIntegerCoefficients(rdfac,b);
        GenSolvablePolynomial<BigRational> cp = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> fromIntegerCoefficients(rdfac,c);
        GenSolvablePolynomial<BigRational> dp = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> fromIntegerCoefficients(rdfac,d);
        GenSolvablePolynomial<BigRational> ep = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> fromIntegerCoefficients(rdfac,e);
        GenSolvablePolynomial<BigRational> fp = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> fromIntegerCoefficients(rdfac,f);
        //System.out.println("ap  = " + ap);
        //System.out.println("bp  = " + bp);
        //System.out.println("cp  = " + cp);
        //System.out.println("dp  = " + dp);
        //System.out.println("ep  = " + ep);
        //System.out.println("fp  = " + fp);

        GenSolvablePolynomial<BigRational> qp = (GenSolvablePolynomial<BigRational>) ap.divide(bp);
        GenSolvablePolynomial<BigRational> rp = (GenSolvablePolynomial<BigRational>) ap.remainder(bp);
        //System.out.println("qp  = " + qp);
        //System.out.println("rp  = " + rp);
        GenSolvablePolynomial<BigRational> rhs = (GenSolvablePolynomial<BigRational>) qp.multiply(bp).sum(rp);
        //System.out.println("qp bp + rp  = " + rhs);
        assertEquals("ap == qp bp + rp: ", ap, rhs);

        assertEquals("cp == qp: ", qp.monic(), cp.monic() );
        assertEquals("dp == rp: ", rp.monic(), dp.monic() );
        // System.out.println("dp = qp: " + qp.monic().equals(dp.monic()) );
        assertEquals("ep == qp: ", ep.monic(), cp.monic() );
        assertEquals("fp == rp: ", fp.monic(), dp.monic() );
    }


    /**
     * Test recursive pseudo division.
     * @see edu.jas.ufd.PolyUfdUtilTest#testRecursivePseudoDivisionSparse
     */
    public void testRecursivePseudoDivision() {
        //String[] cnames = new String[] { "x" };
        //String[] mnames = new String[] { "t" };
        String[] names = new String[] { "t", "x", "y", "z" };
        rdfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1),to,names);
        RelationGenerator<BigRational> wl = new WeylRelationsIterated<BigRational>();
        rdfac.addRelations(wl);
        rrfac = rdfac.recursive(1);
        //QuotientRing<BigInteger> qfac = new QuotientRing<BigInteger>(dfac);
        //GenPolynomialRing<Quotient<BigInteger>> rqfac = new GenPolynomialRing<Quotient<BigInteger>>(qfac,rfac);
        System.out.println("\nrdfac  = " + rdfac.toScript());
        System.out.println("rrfac  = " + rrfac.toScript());
        //System.out.println("qfac  = " + qfac);
        //System.out.println("rqfac = " + rqfac);

        // kl = 2;
        q = q / 2.0f;

        //ar = rfac.random(kl, ll, el, q);
        //arr = rrfac.parse(" ( t + x + y ) z^2 + ( 2 x - 8 ) y^2 - ( 13 t^4 - 13 t^3 + t^2 + 2 t - 13 ) ");
        arr = rrfac.parse(" ( t ) z^2 + ( x + y ) ");
        //br = rrfac.random(kl, ll, el, q);
        //ar = ar.multiply(br);
        //brr = rrfac.parse(" ( 3 x^2 - 6  ) z - ( 13 y^4 - 8 y^3 + 10 y^2 + 22 y + 21  ) ");
        brr = rrfac.parse(" ( x ) z - ( t ) ");
        System.out.println("arr   = " + arr);
        System.out.println("brr   = " + brr);

        drr = (GenSolvablePolynomial<GenPolynomial<BigRational>>) FDUtil.<BigRational> recursivePseudoQuotient(arr, brr);
        System.out.println("qr   = " + drr);
        crr = (GenSolvablePolynomial<GenPolynomial<BigRational>>) FDUtil.<BigRational> recursiveSparsePseudoRemainder(arr, brr);
        System.out.println("rr   = " + crr);

        //boolean t = PolyUtil.<BigRational> isRecursivePseudoQuotientRemainder(arr, brr, drr, crr);
        boolean t = FDUtil.<BigRational> isRecursivePseudoQuotientRemainder(arr, brr, drr, crr);
        System.out.println("assertTrue ore(lc^n) a = q b + r: " + t);
        //assertTrue("ore(lc^n) a = q b + r: " + crr, t); // ?? not always true

        // GenPolynomial<Quotient<BigInteger>> ap = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(rqfac,ar);
        // GenPolynomial<Quotient<BigInteger>> bp = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(rqfac,br);
        // GenPolynomial<Quotient<BigInteger>> cp = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(rqfac,cr);
        // GenPolynomial<Quotient<BigInteger>> dp = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(rqfac,dr);
        // //System.out.println("ap  = " + ap);
        // //System.out.println("bp  = " + bp);
        // //System.out.println("cp  = " + cp);
        // ////System.out.println("dp  = " + dp);
        // //System.out.println("dp  = " + dp.monic());

        // GenPolynomial<Quotient<BigInteger>> qp = ap.divide(bp);
        // GenPolynomial<Quotient<BigInteger>> rp = ap.remainder(bp);
        // ////System.out.println("qp  = " + qp);
        // //System.out.println("qp  = " + qp.monic());
        // //System.out.println("rp  = " + rp);
        // GenPolynomial<Quotient<BigInteger>> rhs = qp.multiply(bp).sum(rp);
        // //System.out.println("qp bp + rp  = " + rhs);

        // assertEquals("ap = qp bp + rp: ", ap, rhs);

        // assertEquals("cp = rp: ", rp.monic(), cp.monic() );
        // //System.out.println("dp = qp: " + qp.monic().equals(dp.monic()) );
        // assertEquals("dp = qp: ", qp.monic(), dp.monic() ); // ??
    }

}
