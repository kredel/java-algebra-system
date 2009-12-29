/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.arith.PrimeList;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;


/**
 * GCD Modular algorithm tests with JUnit.
 * @author Heinz Kredel.
 */

public class GCDModLongTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GCDModularTest</CODE> object.
     * @param name String.
     */
    public GCDModLongTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GCDModLongTest.class);
        return suite;
    }


    //private final static int bitlen = 100;

    GreatestCommonDivisorAbstract<ModLong> ufd;


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<ModLong> dfac;


    GenPolynomialRing<ModLong> cfac;


    GenPolynomialRing<GenPolynomial<ModLong>> rfac;


    PrimeList primes = new PrimeList();


    ModLongRing mi;


    ModLong ai;


    ModLong bi;


    ModLong ci;


    ModLong di;


    ModLong ei;


    GenPolynomial<ModLong> a;


    GenPolynomial<ModLong> b;


    GenPolynomial<ModLong> c;


    GenPolynomial<ModLong> d;


    GenPolynomial<ModLong> e;


    GenPolynomial<ModLong> ac;


    GenPolynomial<ModLong> bc;


    GenPolynomial<GenPolynomial<ModLong>> ar;


    GenPolynomial<GenPolynomial<ModLong>> br;


    GenPolynomial<GenPolynomial<ModLong>> cr;


    GenPolynomial<GenPolynomial<ModLong>> dr;


    GenPolynomial<GenPolynomial<ModLong>> er;


    GenPolynomial<GenPolynomial<ModLong>> arc;


    GenPolynomial<GenPolynomial<ModLong>> brc;


    int rl = 5;


    int kl = 4;


    int ll = 5;


    int el = 3;


    float q = 0.3f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        mi = new ModLongRing(primes.get(0), true);
        ufd = new GreatestCommonDivisorPrimitive<ModLong>();
        dfac = new GenPolynomialRing<ModLong>(mi, rl, to);
        cfac = new GenPolynomialRing<ModLong>(mi, rl - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<ModLong>>(cfac, 1, to);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        ufd = null;
        dfac = null;
        cfac = null;
        rfac = null;
    }


    /**
     * Test modular algorithm gcd with modular evaluation recursive algorithm.
     * 
     */
    public void testModularEvaluationGcd() {

        GreatestCommonDivisorAbstract<BigInteger> ufd_m = new GreatestCommonDivisorModular<ModLong>(); // dummy type

        GreatestCommonDivisorAbstract<BigInteger> ufd = new GreatestCommonDivisorPrimitive<BigInteger>();

        GenPolynomial<BigInteger> a;
        GenPolynomial<BigInteger> b;
        GenPolynomial<BigInteger> c;
        GenPolynomial<BigInteger> d;
        GenPolynomial<BigInteger> e;

        GenPolynomialRing<BigInteger> dfac = new GenPolynomialRing<BigInteger>(new BigInteger(), 3, to);

        for (int i = 0; i < 2; i++) {
            a = dfac.random(kl, ll + i, el + i, q);
            b = dfac.random(kl, ll + i, el + i, q);
            c = dfac.random(kl, ll + i, el + i, q);
            c = c.multiply(dfac.univariate(0));
            //a = ufd.basePrimitivePart(a);
            //b = ufd.basePrimitivePart(b);

            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            a = a.multiply(c);
            b = b.multiply(c);

            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);

            d = ufd_m.gcd(a, b);

            c = ufd.basePrimitivePart(c).abs();
            e = PolyUtil.<BigInteger> basePseudoRemainder(d, c);
            //System.out.println("c  = " + c);
            //System.out.println("d  = " + d);

            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());

            e = PolyUtil.<BigInteger> basePseudoRemainder(a, d);
            //System.out.println("e = " + e);
            assertTrue("gcd(a,b) | a" + e, e.isZERO());

            e = PolyUtil.<BigInteger> basePseudoRemainder(b, d);
            //System.out.println("e = " + e);
            assertTrue("gcd(a,b) | b" + e, e.isZERO());
        }
    }


    /**
     * Test modular algorithm gcd with simple PRS recursive algorithm.
     * 
     */
    public void testModularSimpleGcd() {

        GreatestCommonDivisorAbstract<BigInteger> ufd_m = new GreatestCommonDivisorModular<ModLong>(true); // dummy type

        GreatestCommonDivisorAbstract<BigInteger> ufd = new GreatestCommonDivisorPrimitive<BigInteger>();

        GenPolynomial<BigInteger> a;
        GenPolynomial<BigInteger> b;
        GenPolynomial<BigInteger> c;
        GenPolynomial<BigInteger> d;
        GenPolynomial<BigInteger> e;

        GenPolynomialRing<BigInteger> dfac = new GenPolynomialRing<BigInteger>(new BigInteger(), 3, to);

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll + i, el + i, q);
            b = dfac.random(kl, ll + i, el + i, q);
            c = dfac.random(kl, ll + i, el + i, q);
            c = c.multiply(dfac.univariate(0));
            //a = ufd.basePrimitivePart(a);
            //b = ufd.basePrimitivePart(b);

            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            a = a.multiply(c);
            b = b.multiply(c);

            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);

            d = ufd_m.gcd(a, b);

            c = ufd.basePrimitivePart(c).abs();
            e = PolyUtil.<BigInteger> basePseudoRemainder(d, c);
            //System.out.println("c  = " + c);
            //System.out.println("d  = " + d);

            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());

            e = PolyUtil.<BigInteger> basePseudoRemainder(a, d);
            //System.out.println("e = " + e);
            assertTrue("gcd(a,b) | a" + e, e.isZERO());

            e = PolyUtil.<BigInteger> basePseudoRemainder(b, d);
            //System.out.println("e = " + e);
            assertTrue("gcd(a,b) | b" + e, e.isZERO());
        }
    }


    /**
     * Test recursive content and primitive part, modular coefficients.
     * 
     */
    public void testRecursiveContentPPmodular() {

        dfac = new GenPolynomialRing<ModLong>(mi, 2, to);
        cfac = new GenPolynomialRing<ModLong>(mi, 2 - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<ModLong>>(cfac, 1, to);

        GreatestCommonDivisorAbstract<ModLong> ufd = new GreatestCommonDivisorPrimitive<ModLong>();

        for (int i = 0; i < 1; i++) {
            a = cfac.random(kl, ll + 2 * i, el + i, q).monic();
            cr = rfac.random(kl * (i + 2), ll + 2 * i, el + i, q);
            cr = PolyUtil.<ModLong> monic(cr);
            //System.out.println("a  = " + a);
            //System.out.println("cr = " + cr);
            //a = ufd.basePrimitivePart(a);
            //b = distribute(dfac,cr);
            //b = ufd.basePrimitivePart(b);
            //cr = recursive(rfac,b);
            //System.out.println("a  = " + a);
            //System.out.println("cr = " + cr);

            cr = cr.multiply(a);
            //System.out.println("cr = " + cr);

            if (cr.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( cr" + i + " ) <> 0", cr.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            c = ufd.recursiveContent(cr).monic();
            dr = ufd.recursivePrimitivePart(cr);
            dr = PolyUtil.<ModLong> monic(dr);
            //System.out.println("c  = " + c);
            //System.out.println("dr = " + dr);

            //System.out.println("monic(a) = " + a.monic());
            //System.out.println("monic(c) = " + c.monic());

            ar = dr.multiply(c);
            //System.out.println("ar = " + ar);
            assertEquals("c == cont(c)pp(c)", cr, ar);
        }
    }


    /**
     * Test base gcd modular coefficients.
     * 
     */
    public void testGCDbaseModular() {

        dfac = new GenPolynomialRing<ModLong>(mi, 1, to);

        GreatestCommonDivisorAbstract<ModLong> ufd = new GreatestCommonDivisorPrimitive<ModLong>();

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll, el + 3 + i, q).monic();
            b = dfac.random(kl, ll, el + 3 + i, q).monic();
            c = dfac.random(kl, ll, el + 3 + i, q).monic();
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            ac = a.multiply(c);
            bc = b.multiply(c);
            //System.out.println("ac = " + ac);
            //System.out.println("bc = " + bc);

            //e = PolyUtil.<ModLong>basePseudoRemainder(ac,c);
            //System.out.println("ac/c a = 0 " + e);
            //assertTrue("ac/c-a != 0 " + e, e.isZERO() );
            //e = PolyUtil.<ModLong>basePseudoRemainder(bc,c);
            //System.out.println("bc/c-b = 0 " + e);
            //assertTrue("bc/c-b != 0 " + e, e.isZERO() );

            d = ufd.baseGcd(ac, bc);
            d = d.monic(); // not required
            //System.out.println("d = " + d);

            e = PolyUtil.<ModLong> basePseudoRemainder(d, c);
            //System.out.println("e = " + e);

            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());
        }
    }


    /**
     * Test recursive gcd modular coefficients.
     * 
     */
    public void testRecursiveGCDModular() {

        dfac = new GenPolynomialRing<ModLong>(mi, 2, to);
        cfac = new GenPolynomialRing<ModLong>(mi, 2 - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<ModLong>>(cfac, 1, to);

        //     GreatestCommonDivisorAbstract<ModLong> ufd 
        //     = new GreatestCommonDivisorPrimitive<ModLong>();

        for (int i = 0; i < 1; i++) {
            ar = rfac.random(kl, 2, el + 2, q);
            br = rfac.random(kl, 2, el + 2, q);
            cr = rfac.random(kl, 2, el + 2, q);
            ar = PolyUtil.<ModLong> monic(ar);
            br = PolyUtil.<ModLong> monic(br);
            cr = PolyUtil.<ModLong> monic(cr);
            //System.out.println("ar = " + ar);
            //System.out.println("br = " + br);
            //System.out.println("cr = " + cr);

            if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( cr" + i + " ) <> 0", cr.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            arc = ar.multiply(cr);
            brc = br.multiply(cr);
            //System.out.println("arc = " + arc);
            //System.out.println("brc = " + brc);

            //er = PolyUtil.<ModLong>recursivePseudoRemainder(arc,cr);
            //System.out.println("ac/c-a = 0 " + er);
            //assertTrue("ac/c-a != 0 " + er, er.isZERO() );
            //er = PolyUtil.<ModLong>recursivePseudoRemainder(brc,cr);
            //System.out.println("bc/c-b = 0 " + er);
            //assertTrue("bc/c-b != 0 " + er, er.isZERO() );

            dr = ufd.recursiveUnivariateGcd(arc, brc);
            dr = PolyUtil.<ModLong> monic(dr);
            //System.out.println("cr = " + cr);
            //System.out.println("dr = " + dr);

            er = PolyUtil.<ModLong> recursivePseudoRemainder(dr, cr);
            //System.out.println("er = " + er);

            assertTrue("c | gcd(ac,bc) " + er, er.isZERO());
        }
    }


    /**
     * Test arbitrary recursive gcd modular coefficients.
     * 
     */
    public void testArbitraryRecursiveGCDModular() {

        dfac = new GenPolynomialRing<ModLong>(mi, 2, to);
        cfac = new GenPolynomialRing<ModLong>(mi, 2 - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<ModLong>>(cfac, 1, to);

        //     GreatestCommonDivisorAbstract<ModLong> ufd 
        //     = new GreatestCommonDivisorPrimitive<ModLong>();

        for (int i = 0; i < 1; i++) {
            ar = rfac.random(kl, 2, el + 2, q);
            br = rfac.random(kl, 2, el + 2, q);
            cr = rfac.random(kl, 2, el + 2, q);
            ar = PolyUtil.<ModLong> monic(ar);
            br = PolyUtil.<ModLong> monic(br);
            cr = PolyUtil.<ModLong> monic(cr);
            //System.out.println("ar = " + ar);
            //System.out.println("br = " + br);
            //System.out.println("cr = " + cr);

            if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( cr" + i + " ) <> 0", cr.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            arc = ar.multiply(cr);
            brc = br.multiply(cr);
            //System.out.println("arc = " + arc);
            //System.out.println("brc = " + brc);

            //er = PolyUtil.<ModLong>recursivePseudoRemainder(arc,cr);
            //System.out.println("ac/c-a = 0 " + er);
            //assertTrue("ac/c-a != 0 " + er, er.isZERO() );
            //er = PolyUtil.<ModLong>recursivePseudoRemainder(brc,cr);
            //System.out.println("bc/c-b = 0 " + er);
            //assertTrue("bc/c-b != 0 " + er, er.isZERO() );

            dr = ufd.recursiveGcd(arc, brc);
            dr = PolyUtil.<ModLong> monic(dr);
            //System.out.println("cr = " + cr);
            //System.out.println("dr = " + dr);

            er = PolyUtil.<ModLong> recursivePseudoRemainder(dr, cr);
            //System.out.println("er = " + er);

            assertTrue("c | gcd(ac,bc) " + er, er.isZERO());
        }
    }


    /**
     * Test gcd modular coefficients.
     * 
     */
    public void testGcdModular() {

        dfac = new GenPolynomialRing<ModLong>(mi, 4, to);

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll, el + i, q).monic();
            b = dfac.random(kl, ll, el + i, q).monic();
            c = dfac.random(kl, ll, el + i, q).monic();
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            ac = a.multiply(c);
            bc = b.multiply(c);
            //System.out.println("ac = " + ac);
            //System.out.println("bc = " + bc);

            //e = PolyUtil.<ModLong>basePseudoRemainder(ac,c);
            //System.out.println("ac/c-a = 0 " + e);
            //assertTrue("ac/c-a != 0 " + e, e.isZERO() );
            //e = PolyUtil.<ModLong>basePseudoRemainder(bc,c);
            //System.out.println("bc/c-b = 0 " + e);
            //assertTrue("bc/c-b != 0 " + e, e.isZERO() );

            d = ufd.gcd(ac, bc);
            //System.out.println("d = " + d);
            e = PolyUtil.<ModLong> basePseudoRemainder(d, c);
            //System.out.println("e = " + e);
            //System.out.println("c = " + c);
            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());

            e = PolyUtil.<ModLong> basePseudoRemainder(ac, d);
            //System.out.println("gcd(ac,bc) | ac " + e);
            assertTrue("gcd(ac,bc) | ac " + e, e.isZERO());
            e = PolyUtil.<ModLong> basePseudoRemainder(bc, d);
            //System.out.println("gcd(ac,bc) | bc " + e);
            assertTrue("gcd(ac,bc) | bc " + e, e.isZERO());
        }
    }


    /**
     * Test co-prime factors.
     * 
     */
    public void testCoPrime() {

        dfac = new GenPolynomialRing<ModLong>(mi, 3, to);

        a = dfac.random(kl, 3, 2, q);
        b = dfac.random(kl, 3, 2, q);
        c = dfac.random(kl, 3, 2, q);
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            // skip for this turn
            return;
        }
        assertTrue("length( a ) <> 0", a.length() > 0);

        d = a.multiply(a).multiply(b).multiply(b).multiply(b).multiply(c);
        e = a.multiply(b).multiply(c);
        //System.out.println("d  = " + d);
        //System.out.println("c  = " + c);

        List<GenPolynomial<ModLong>> F = new ArrayList<GenPolynomial<ModLong>>(5);
        F.add(a);
        F.add(b);
        F.add(c);
        F.add(d);
        F.add(e);

        List<GenPolynomial<ModLong>> P = ufd.coPrime(F);
        //System.out.println("F = " + F);
        //System.out.println("P = " + P);

        assertTrue("is co-prime ", ufd.isCoPrime(P));
        assertTrue("is co-prime of ", ufd.isCoPrime(P, F));

        P = ufd.coPrimeRec(F);
        //System.out.println("F = " + F);
        //System.out.println("P = " + P);

        assertTrue("is co-prime ", ufd.isCoPrime(P));
        assertTrue("is co-prime of ", ufd.isCoPrime(P, F));
    }

}
