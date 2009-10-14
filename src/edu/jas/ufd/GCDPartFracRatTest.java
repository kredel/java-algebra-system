/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;


/**
 * GCD partial fraction with rational coefficients algorithm tests with JUnit.
 * @author Heinz Kredel.
 */

public class GCDPartFracRatTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GCDPartFracRatTest</CODE> object.
     * @param name String.
     */
    public GCDPartFracRatTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GCDPartFracRatTest.class);
        return suite;
    }


    //private final static int bitlen = 100;

    GreatestCommonDivisorAbstract<BigRational> ufd;


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<BigRational> dfac;


    GenPolynomialRing<BigRational> cfac;


    GenPolynomialRing<GenPolynomial<BigRational>> rfac;


    BigRational mi;


    BigRational ai;


    BigRational bi;


    BigRational ci;


    BigRational di;


    BigRational ei;


    GenPolynomial<BigRational> a;


    GenPolynomial<BigRational> b;


    GenPolynomial<BigRational> c;


    GenPolynomial<BigRational> d;


    GenPolynomial<BigRational> e;


    GenPolynomial<GenPolynomial<BigRational>> ar;


    GenPolynomial<GenPolynomial<BigRational>> br;


    GenPolynomial<GenPolynomial<BigRational>> cr;


    GenPolynomial<GenPolynomial<BigRational>> dr;


    GenPolynomial<GenPolynomial<BigRational>> er;


    int rl = 3;


    int kl = 2;


    int ll = 3;


    int el = 3;


    float q = 0.25f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        mi = new BigRational(1);
        ufd = new GreatestCommonDivisorSubres<BigRational>();
        String[] vars = ExpVector.STDVARS(rl);
        String[] cvars = ExpVector.STDVARS(rl - 1);
        String[] rvars = new String[] { vars[rl - 1] };
        dfac = new GenPolynomialRing<BigRational>(mi, rl, to, vars);
        cfac = new GenPolynomialRing<BigRational>(mi, rl - 1, to, cvars);
        rfac = new GenPolynomialRing<GenPolynomial<BigRational>>(cfac, 1, to, rvars);
        //System.out.println("mi = " + mi);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        mi = null;
        ufd = null;
        dfac = null;
        cfac = null;
        rfac = null;
    }


    /**
     * Test base quotioent and remainder.
     * 
     */
    public void testBaseQR() {

        dfac = new GenPolynomialRing<BigRational>(mi, 1, to);

        for (int i = 0; i < 3; i++) {
            a = dfac.random(kl * (i + 2), ll + 2 * i, el + 2 * i, q);
            c = dfac.random(kl * (i + 2), ll + 2 * i, el + 2 * i, q);
            //a = ufd.basePrimitivePart(a).abs();
            //c = ufd.basePrimitivePart(c);
            do {
                ci = mi.random(kl * (i + 2));
                ci = ci.sum(mi.getONE());
            } while (ci.isZERO());

            //System.out.println("a  = " + a);
            //System.out.println("c  = " + c);
            //System.out.println("ci = " + ci);

            if (a.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            b = a.multiply(c);
            //System.out.println("b  = " + b);
            d = PolyUtil.<BigRational> basePseudoRemainder(b, c);
            //System.out.println("d  = " + d);

            assertTrue("rem(ac,c) == 0", d.isZERO());

            b = a.multiply(ci);
            //System.out.println("b  = " + b);
            d = b.divide(ci);
            //System.out.println("d  = " + d);

            assertEquals("a == ac/c", a, d);

            b = a.multiply(c);
            //System.out.println("b  = " + b);
            d = PolyUtil.<BigRational> basePseudoDivide(b, c);
            //System.out.println("d  = " + d);

            assertEquals("a == ac/c", a, d);

            b = a.multiply(c).sum( dfac.getONE() );
            //System.out.println("b  = " + b);
            //System.out.println("c  = " + c);
            GenPolynomial<BigRational>[] qr = PolyUtil.<BigRational> basePseudoQuotientRemainder(b, c);
            d = qr[0];
            e = qr[1];
            //System.out.println("d  = " + d);
            //System.out.println("e  = " + e);

            e = d.multiply(c).sum(e);
            //System.out.println("e  = " + e);
            assertEquals("b == b/c + b%c ", b, e);

        }
    }


    /**
     * Test base extended gcd.
     * 
     */
    public void testBaseExtGcd() {

        dfac = new GenPolynomialRing<BigRational>(mi, 1, to);

        for (int i = 0; i < 3; i++) {
            a = dfac.random(kl * (i + 2), ll + 2 * i, el + 2 * i, q);
            b = dfac.random(kl * (i + 2), ll + 2 * i, el + 2 * i, q);
            c = dfac.random(kl * (i + 2), ll + 2 * i, el + 2 * i, q);
            //a = ufd.basePrimitivePart(a);
            //b = ufd.basePrimitivePart(b);
            //c = ufd.basePrimitivePart(c).abs();

            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            //System.out.println("c  = " + c);

            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            a = a.multiply(c);
            b = b.multiply(c);

            GenPolynomial<BigRational>[] egcd = ufd.baseExtendedGcd(a, b);
            d = egcd[0];
            e = PolyUtil.<BigRational> basePseudoRemainder(d, c);
            //System.out.println("d  = " + d);
            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());

            e = egcd[1].multiply(a).sum( egcd[2].multiply(b) );
            assertEquals("gcd(a,b) = s a + t b ", d, e);

            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            //System.out.println("d  = " + d);
            GenPolynomial<BigRational>[] diop = ufd.baseGcdDiophant(a, b, d);
            e = diop[0].multiply(a).sum( diop[1].multiply(b) );
            //System.out.println("e  = " + e);
            assertEquals("d*gcd(a,b) = s a + t b ", d, e);
        }
    }


    /**
     * Test base partial fraction.
     * 
     */
    public void testBasePartFrac() {

        dfac = new GenPolynomialRing<BigRational>(mi, 1, to);

        for (int i = 0; i < 3; i++) {
            a = dfac.random(kl * (i + 2), ll + 2 * i, el + 2 * i, q);
            b = dfac.random(kl * (i + 2), ll + 2 * i, el + 2 * i, q);
            c = dfac.random(kl * (i + 2), ll + 2 * i, el + 2 * i, q);
            //a = dfac.random(kl, ll + 2 * i, el + 2 * i, q);
            //b = dfac.random(kl, ll + 2 * i, el + 2 * i, q);
            //c = dfac.random(kl, ll + 2 * i, el + 2 * i, q);
            //a = ufd.basePrimitivePart(a);
            //b = ufd.basePrimitivePart(b);
            //c = ufd.basePrimitivePart(c).abs();

            if ( b.isZERO() || c.isZERO() ) {
                // skip for this turn
                continue;
            }
            if ( b.isConstant() || c.isConstant() ) {
                // skip for this turn
                continue;
            }
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            //System.out.println("c  = " + c);

            // a / (b*c) = a0 + ap/b + as/c
            GenPolynomial<BigRational> gcd = ufd.baseGcd(b,c);
            //System.out.println("gcd = " + gcd);
            if ( !gcd.isONE() ) {
                b = PolyUtil.<BigRational> basePseudoDivide(b, gcd);
                c = PolyUtil.<BigRational> basePseudoDivide(c, gcd);
            }
            //System.out.println("b  = " + b);
            //System.out.println("c  = " + c);

            GenPolynomial<BigRational>[] pf = ufd.basePartialFraction(a, b, c);
            //System.out.println("a0  = " + pf[0]);
            //System.out.println("ap  = " + pf[1]);
            //System.out.println("as  = " + pf[2]);

            d = pf[0].multiply(b).multiply(c); // a0*b*c
            //System.out.println("d  = " + d);

            e = c.multiply( pf[1] ).sum( b.multiply(pf[2]) );  // ap * b + as * c
            //System.out.println("e  = " + e);

            d = d.sum( e ); // a0*b*c + ap * c + as * b
            //System.out.println("d  = " + d);

            assertEquals("a = a0*b*c + s * c + t * b ", a, d);

            List<GenPolynomial<BigRational>> D = new ArrayList<GenPolynomial<BigRational>>(2);
            D.add(b);
            D.add(c);
            //System.out.println("D  = " + D);
            List<GenPolynomial<BigRational>> F = ufd.basePartialFraction(a, D);
            //System.out.println("F  = " + F.size());

            boolean t = ufd.isBasePartialFraction(a, D, F);
            assertTrue("a/D = a0 + sum(fi/di)", t);
        }
    }


    /**
     * Test base partial fraction list.
     * 
     */
    public void testBasePartFracList() {

        dfac = new GenPolynomialRing<BigRational>(mi, 1, to);

        for (int i = 0; i < 3; i++) {
            a = dfac.random(kl * (i + 2), ll + 2 * i, el + 2 * i, q);
            //System.out.println("a  = " + a);

            List<GenPolynomial<BigRational>> D = new ArrayList<GenPolynomial<BigRational>>();
            for ( int j = 0; j < i*3; j++ ) {
                b = dfac.random(kl * (i + 1), ll + i, el + i, q);
                if ( b.isZERO() || b.isConstant() ) {
                    // skip for this turn
                    continue;
                }
                D.add(b);
            }
            //System.out.println("D  = " + D);
            D = ufd.coPrime(D);
            //System.out.println("D  = " + D);

            List<GenPolynomial<BigRational>> F = ufd.basePartialFraction(a, D);
            //System.out.println("F  = " + F.size());

            boolean t = ufd.isBasePartialFraction(a, D, F);
            assertTrue("a = a0*b*c + s * c + t * b ", t);
        }
    }


    /**
     * Test base partial fraction exponent.
     * 
     */
    public void testBasePartFracExponent() {

        dfac = new GenPolynomialRing<BigRational>(mi, 1, to);

        for (int i = 0; i < 3; i++) {
            a = dfac.random(kl * (i + 2), ll + 2 * i, el + 2 * i, q);
            //System.out.println("a  = " + a);

            b = dfac.random(kl * (i + 1), ll + i, el + i, q);
            if ( b.isZERO() || b.isConstant() ) {
                // skip for this turn
                continue;
            }
            //System.out.println("b  = " + b);

            List<GenPolynomial<BigRational>> F = ufd.basePartialFraction(a, b, 3);
            //System.out.println("F  = " + F);

            boolean t = ufd.isBasePartialFraction(a, b, 3, F);
            assertTrue("a/b^e = a0 + sum(ai/p^i) ", t);
        }
    }


    /**
     * Test base partial fraction list exponent (squarefree).
     * 
     */
    public void testBasePartFracListExponent() {

        SquarefreeAbstract<BigRational> sqf = SquarefreeFactory.<BigRational>getImplementation(mi);

        dfac = new GenPolynomialRing<BigRational>(mi, 1, to);

        for (int i = 0; i < 3; i++) {
            a = dfac.random(kl * (i + 2), ll + 2 * i, el + 2 * i, q);
            //System.out.println("a  = " + a);

            List<GenPolynomial<BigRational>> D = new ArrayList<GenPolynomial<BigRational>>();
            for ( int j = 0; j < i*3; j++ ) {
                b = dfac.random(kl, ll + 1 + i, el + i, q);
                if ( b.isZERO() || b.isConstant() ) {
                    // skip for this turn
                    continue;
                }
                D.add(b);
            }
            //System.out.println("D  = " + D);
            D = ufd.coPrime(D);
            //System.out.println("D  = " + D);

            SortedMap<GenPolynomial<BigRational>,Long> Ds = new TreeMap<GenPolynomial<BigRational>,Long>();
            long j = 1L;
            for ( GenPolynomial<BigRational> p : D ) {
                Ds.put(p,j);
                j++;
            }
            //System.out.println("Ds = " + Ds);

            List<List<GenPolynomial<BigRational>>> F = sqf.basePartialFraction(a, Ds);
            //System.out.println("F  = " + F.size());

            boolean t = sqf.isBasePartialFraction(a, Ds, F);
            assertTrue("a/b^e = a0 + sum(ai/p^i) ", t);
        }
    }

}
