/*
 * $Id$
 */

package edu.jas.ufd;


//import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;


/**
 * GCD Modular algorithm tests with JUnit.
 * @author Heinz Kredel.
 */

public class GCDModularTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GCDModularTest</CODE> object.
     * @param name String.
     */
    public GCDModularTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GCDModularTest.class);
        return suite;
    }


    GreatestCommonDivisorAbstract<ModInteger> ufd;


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<ModInteger> dfac;


    GenPolynomialRing<ModInteger> cfac;


    GenPolynomialRing<GenPolynomial<ModInteger>> rfac;


    PrimeList primes = new PrimeList();


    ModIntegerRing mi;


    ModInteger ai;


    ModInteger bi;


    ModInteger ci;


    ModInteger di;


    ModInteger ei;


    GenPolynomial<ModInteger> a;


    GenPolynomial<ModInteger> b;


    GenPolynomial<ModInteger> c;


    GenPolynomial<ModInteger> d;


    GenPolynomial<ModInteger> e;


    GenPolynomial<ModInteger> ac;


    GenPolynomial<ModInteger> bc;


    GenPolynomial<GenPolynomial<ModInteger>> ar;


    GenPolynomial<GenPolynomial<ModInteger>> br;


    GenPolynomial<GenPolynomial<ModInteger>> cr;


    GenPolynomial<GenPolynomial<ModInteger>> dr;


    GenPolynomial<GenPolynomial<ModInteger>> er;


    GenPolynomial<GenPolynomial<ModInteger>> arc;


    GenPolynomial<GenPolynomial<ModInteger>> brc;


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
        //mi = new ModIntegerRing(primes.get(0), true);
        mi = new ModIntegerRing(19L, true);
        ufd = new GreatestCommonDivisorPrimitive<ModInteger>();
        dfac = new GenPolynomialRing<ModInteger>(mi, rl, to);
        cfac = new GenPolynomialRing<ModInteger>(mi, rl - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<ModInteger>>(cfac, 1, to);
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
     */
    public void testModularEvaluationGcd() {

        GreatestCommonDivisorAbstract<BigInteger> ufd_m = new GreatestCommonDivisorModular(/*false*/);
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
     */
    public void testModularSimpleGcd() {

        GreatestCommonDivisorAbstract<BigInteger> ufd_m = new GreatestCommonDivisorModular(true);
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
     */
    public void testRecursiveContentPPmodular() {

        dfac = new GenPolynomialRing<ModInteger>(mi, 2, to);
        cfac = new GenPolynomialRing<ModInteger>(mi, 2 - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<ModInteger>>(cfac, 1, to);

        GreatestCommonDivisorAbstract<ModInteger> ufd = new GreatestCommonDivisorPrimitive<ModInteger>();

        for (int i = 0; i < 1; i++) {
            a = cfac.random(kl, ll + 2 * i, el + i, q).monic();
            cr = rfac.random(kl * (i + 2), ll + 2 * i, el + i, q);
            cr = PolyUtil.<ModInteger> monic(cr);
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
            dr = PolyUtil.<ModInteger> monic(dr);
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
     */
    public void testGCDbaseModular() {

        dfac = new GenPolynomialRing<ModInteger>(mi, 1, to);

        GreatestCommonDivisorAbstract<ModInteger> ufd = new GreatestCommonDivisorPrimitive<ModInteger>();

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

            //e = PolyUtil.<ModInteger>basePseudoRemainder(ac,c);
            //System.out.println("ac/c a = 0 " + e);
            //assertTrue("ac/c-a != 0 " + e, e.isZERO() );
            //e = PolyUtil.<ModInteger>basePseudoRemainder(bc,c);
            //System.out.println("bc/c-b = 0 " + e);
            //assertTrue("bc/c-b != 0 " + e, e.isZERO() );

            d = ufd.baseGcd(ac, bc);
            d = d.monic(); // not required
            //System.out.println("d = " + d);

            e = PolyUtil.<ModInteger> basePseudoRemainder(d, c);
            //System.out.println("e = " + e);

            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());
        }
    }


    /**
     * Test recursive gcd modular coefficients.
     */
    public void testRecursiveGCDModular() {

        dfac = new GenPolynomialRing<ModInteger>(mi, 2, to);
        cfac = new GenPolynomialRing<ModInteger>(mi, 2 - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<ModInteger>>(cfac, 1, to);

        //     GreatestCommonDivisorAbstract<ModInteger> ufd 
        //     = new GreatestCommonDivisorPrimitive<ModInteger>();

        for (int i = 0; i < 1; i++) {
            ar = rfac.random(kl, 2, el + 2, q);
            br = rfac.random(kl, 2, el + 2, q);
            cr = rfac.random(kl, 2, el + 2, q);
            ar = PolyUtil.<ModInteger> monic(ar);
            br = PolyUtil.<ModInteger> monic(br);
            cr = PolyUtil.<ModInteger> monic(cr);
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

            //er = PolyUtil.<ModInteger>recursivePseudoRemainder(arc,cr);
            //System.out.println("ac/c-a = 0 " + er);
            //assertTrue("ac/c-a != 0 " + er, er.isZERO() );
            //er = PolyUtil.<ModInteger>recursivePseudoRemainder(brc,cr);
            //System.out.println("bc/c-b = 0 " + er);
            //assertTrue("bc/c-b != 0 " + er, er.isZERO() );

            dr = ufd.recursiveUnivariateGcd(arc, brc);
            dr = PolyUtil.<ModInteger> monic(dr);
            //System.out.println("cr = " + cr);
            //System.out.println("dr = " + dr);

            er = PolyUtil.<ModInteger> recursivePseudoRemainder(dr, cr);
            //System.out.println("er = " + er);

            assertTrue("c | gcd(ac,bc) " + er, er.isZERO());
        }
    }


    /**
     * Test arbitrary recursive gcd modular coefficients.
     */
    public void testArbitraryRecursiveGCDModular() {

        dfac = new GenPolynomialRing<ModInteger>(mi, 2, to);
        cfac = new GenPolynomialRing<ModInteger>(mi, 2 - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<ModInteger>>(cfac, 1, to);

        //     GreatestCommonDivisorAbstract<ModInteger> ufd 
        //     = new GreatestCommonDivisorPrimitive<ModInteger>();

        for (int i = 0; i < 1; i++) {
            ar = rfac.random(kl, 2, el + 2, q);
            br = rfac.random(kl, 2, el + 2, q);
            cr = rfac.random(kl, 2, el + 2, q);
            ar = PolyUtil.<ModInteger> monic(ar);
            br = PolyUtil.<ModInteger> monic(br);
            cr = PolyUtil.<ModInteger> monic(cr);
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

            //er = PolyUtil.<ModInteger>recursivePseudoRemainder(arc,cr);
            //System.out.println("ac/c-a = 0 " + er);
            //assertTrue("ac/c-a != 0 " + er, er.isZERO() );
            //er = PolyUtil.<ModInteger>recursivePseudoRemainder(brc,cr);
            //System.out.println("bc/c-b = 0 " + er);
            //assertTrue("bc/c-b != 0 " + er, er.isZERO() );

            dr = ufd.recursiveGcd(arc, brc);
            dr = PolyUtil.<ModInteger> monic(dr);
            //System.out.println("cr = " + cr);
            //System.out.println("dr = " + dr);

            er = PolyUtil.<ModInteger> recursivePseudoRemainder(dr, cr);
            //System.out.println("er = " + er);

            assertTrue("c | gcd(ac,bc) " + er, er.isZERO());
        }
    }


    /**
     * Test gcd modular coefficients.
     */
    public void testGcdModular() {

        dfac = new GenPolynomialRing<ModInteger>(mi, 4, to);

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

            //e = PolyUtil.<ModInteger>basePseudoRemainder(ac,c);
            //System.out.println("ac/c-a = 0 " + e);
            //assertTrue("ac/c-a != 0 " + e, e.isZERO() );
            //e = PolyUtil.<ModInteger>basePseudoRemainder(bc,c);
            //System.out.println("bc/c-b = 0 " + e);
            //assertTrue("bc/c-b != 0 " + e, e.isZERO() );

            d = ufd.gcd(ac, bc);
            //System.out.println("d = " + d);
            e = PolyUtil.<ModInteger> basePseudoRemainder(d, c);
            //System.out.println("e = " + e);
            //System.out.println("c = " + c);
            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());

            e = PolyUtil.<ModInteger> basePseudoRemainder(ac, d);
            //System.out.println("gcd(ac,bc) | ac " + e);
            assertTrue("gcd(ac,bc) | ac " + e, e.isZERO());
            e = PolyUtil.<ModInteger> basePseudoRemainder(bc, d);
            //System.out.println("gcd(ac,bc) | bc " + e);
            assertTrue("gcd(ac,bc) | bc " + e, e.isZERO());
        }
    }


    /**
     * Test co-prime factors.
     */
    public void testCoPrime() {

        dfac = new GenPolynomialRing<ModInteger>(mi, 3, to);

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

        List<GenPolynomial<ModInteger>> F = new ArrayList<GenPolynomial<ModInteger>>(5);
        F.add(a);
        F.add(b);
        F.add(c);
        F.add(d);
        F.add(e);

        List<GenPolynomial<ModInteger>> P = ufd.coPrime(F);
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


    /**
     * Test base resultant modular coefficients.
     */
    public void testResultantBaseModular() {

        dfac = new GenPolynomialRing<ModInteger>(mi, 1, to);

        GreatestCommonDivisorSimple<ModInteger> ufds = new GreatestCommonDivisorSimple<ModInteger>();
        GreatestCommonDivisorSubres<ModInteger> sres = new GreatestCommonDivisorSubres<ModInteger>();

        for (int i = 0; i < 3; i++) {
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
            if (c.isConstant()) {
                c = dfac.univariate(0,1);
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);

            d = ufds.baseResultant(a, b);
            //System.out.println("d = " + d);
            e = sres.baseResultant(a, b);
            //System.out.println("e = " + e);
            assertEquals("d == e: " + d.subtract(e), d.signum(), e.signum());
            //assertEquals("d == e: " + d.subtract(e), d, e);

            ac = a.multiply(c);
            bc = b.multiply(c);
            //System.out.println("ac = " + ac);
            //System.out.println("bc = " + bc);

            d = ufds.baseResultant(ac, bc);
            //System.out.println("d = " + d);
            assertTrue("d == 0: " + d, d.isZERO());

            e = sres.baseResultant(ac, bc);
            //System.out.println("e = " + e);
            assertTrue("e == 0: " + e, e.isZERO());

            //assertEquals("d == e: " + d.subtract(e), d, e);
        }
    }


    /**
     * Test recursive resultant modular coefficients.
     */
    public void testRecursiveResultantModular() {

        cfac = new GenPolynomialRing<ModInteger>(mi, 2 - 0, to);
        rfac = new GenPolynomialRing<GenPolynomial<ModInteger>>(cfac, 1, to);
        //System.out.println("rfac = " + rfac);
 
        GreatestCommonDivisorSimple<ModInteger> ufds = new GreatestCommonDivisorSimple<ModInteger>();
        GreatestCommonDivisorSubres<ModInteger> sres = new GreatestCommonDivisorSubres<ModInteger>();

        for (int i = 0; i < 1; i++) {
            ar = rfac.random(kl, 2, el + 2, q);
            br = rfac.random(kl, 2, el + 3, q);
            cr = rfac.random(kl, 2, el + 1, q);
            ar = PolyUtil.<ModInteger> monic(ar);
            br = PolyUtil.<ModInteger> monic(br);
            cr = PolyUtil.<ModInteger> monic(cr);
            //System.out.println("ar = " + ar);
            //System.out.println("br = " + br);
            //System.out.println("cr = " + cr);

            if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
                // skip for this turn
                continue;
            }
            if (cr.isConstant()) {
                cr = rfac.univariate(0,1);
            }
            assertTrue("length( cr" + i + " ) <> 0", cr.length() > 0);

            dr = ufds.recursiveUnivariateResultant(ar, br);
            //System.out.println("dr = " + dr);
            er = sres.recursiveUnivariateResultant(ar, br);
            //System.out.println("er = " + er);
            assertEquals("dr == er: " + dr.subtract(er), dr.signum(), er.signum());
            //assertEquals("dr == er: " + dr.subtract(er), dr, er);

            arc = ar.multiply(cr);
            brc = br.multiply(cr);
            //System.out.println("arc = " + arc);
            //System.out.println("brc = " + brc);

            dr = ufds.recursiveUnivariateResultant(arc, brc);
            //System.out.println("dr = " + dr);
            //assertTrue("dr == 0: " + dr, dr.isZERO());

            er = sres.recursiveUnivariateResultant(arc, brc);
            //System.out.println("er = " + er);
            //assertTrue("er == 0: " + er, er.isZERO());

            assertEquals("dr == er: " + dr.subtract(er), dr.signum(), er.signum());
        }
    }


    /**
     * Test resultant modular coefficients.
     */
    public void testResultantModular() {
        dfac = new GenPolynomialRing<ModInteger>(mi, 4, to);
        //System.out.println("dfac = " + dfac);

        GreatestCommonDivisorSimple<ModInteger> ufds = new GreatestCommonDivisorSimple<ModInteger>();
        GreatestCommonDivisorSubres<ModInteger> sres = new GreatestCommonDivisorSubres<ModInteger>();

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
            if (c.isConstant()) {
                c = dfac.univariate(0,1);
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);

            d = ufds.resultant(a, b);
            //System.out.println("d = " + d);
            e = sres.resultant(a, b);
            //System.out.println("e = " + e);
            assertEquals("d == e: " + d.subtract(e), d.signum(), e.signum());
            //assertEquals("d == e: " + d.subtract(e), d, e);

            ac = a.multiply(c);
            bc = b.multiply(c);
            //System.out.println("ac = " + ac);
            //System.out.println("bc = " + bc);

            d = ufds.resultant(ac, bc);
            //System.out.println("d = " + d);
            //assertTrue("d == 0: " + d, d.isZERO());

            e = sres.resultant(ac, bc);
            //System.out.println("e = " + e);
            //assertTrue("e == 0: " + e, e.isZERO());

            assertEquals("d == e: " + d.subtract(e), d.signum(), e.signum());
        }
    }

}
