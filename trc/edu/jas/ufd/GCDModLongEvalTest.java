/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.arith.PrimeList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * GCD Modular Evaluation algorithm tests with JUnit.
 * @author Heinz Kredel
 */

public class GCDModLongEvalTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GCDModLongEvalTest</CODE> object.
     * @param name String.
     */
    public GCDModLongEvalTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GCDModLongEvalTest.class);
        return suite;
    }


    GreatestCommonDivisorAbstract<ModLong> ufd;


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<ModLong> dfac;


    GenPolynomialRing<ModLong> cfac;


    GenPolynomialRing<GenPolynomial<ModLong>> rfac;


    PrimeList primes = new PrimeList();


    ModLongRing mi;


    ModLong ai, bi, ci, di, ei;


    GenPolynomial<ModLong> a, b, c, d, e;


    GenPolynomial<GenPolynomial<ModLong>> ar, br, cr, dr, er;


    int rl = 3;


    int kl = 4;


    int ll = 5;


    int el = 3;


    float q = 0.3f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        //mi = new ModLongRing(primes.get(0),true);
        mi = new ModLongRing(19, true);
        //mi = new ModLongRing(19*17,true); // failing tests
        //mi = new ModLongRing(primes.get(0).multiply(primes.get(1)),false); // failing tests
        //ufd = new GreatestCommonDivisorPrimitive<ModLong>();
        ufd = new GreatestCommonDivisorModEval<ModLong>();
        String[] vars = ExpVector.STDVARS(rl);
        String[] cvars = ExpVector.STDVARS(rl - 1);
        String[] rvars = new String[] { vars[rl - 1] };
        dfac = new GenPolynomialRing<ModLong>(mi, rl, to, vars);
        cfac = new GenPolynomialRing<ModLong>(mi, rl - 1, to, cvars);
        rfac = new GenPolynomialRing<GenPolynomial<ModLong>>(cfac, 1, to, rvars);
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
     * Test modular evaluation gcd.
     */
    public void testModEvalGcd() {
        //GreatestCommonDivisorAbstract<ModLong> ufd_me
        //   = new GreatestCommonDivisorModEval();

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl * (i + 2), ll + 2 * i, el + 0 * i, q);
            b = dfac.random(kl * (i + 2), ll + 2 * i, el + 0 * i, q);
            c = dfac.random(kl * (i + 2), ll + 2 * i, el + 0 * i, q);
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

            d = ufd.gcd(a, b);

            c = ufd.basePrimitivePart(c).abs();
            e = PolyUtil.<ModLong> baseSparsePseudoRemainder(d, c);
            //System.out.println("c  = " + c);
            //System.out.println("d  = " + d);
            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());

            e = PolyUtil.<ModLong> baseSparsePseudoRemainder(a, d);
            //System.out.println("e = " + e);
            assertTrue("gcd(a,b) | a" + e, e.isZERO());

            e = PolyUtil.<ModLong> baseSparsePseudoRemainder(b, d);
            //System.out.println("e = " + e);
            assertTrue("gcd(a,b) | b" + e, e.isZERO());
        }
    }


    /**
     * Test base quotioent and remainder.
     */
    public void testBaseQR() {
        dfac = new GenPolynomialRing<ModLong>(mi, 1, to);

        for (int i = 0; i < 5; i++) {
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
            d = PolyUtil.<ModLong> baseSparsePseudoRemainder(b, c);
            //System.out.println("d  = " + d);

            assertTrue("rem(ac,c) == 0", d.isZERO());

            b = a.multiply(ci);
            //System.out.println("b  = " + b);
            d = b.divide(ci);
            //System.out.println("d  = " + d);

            assertEquals("a == ac/c", a, d);

            b = a.multiply(c);
            //System.out.println("b  = " + b);
            d = PolyUtil.<ModLong> basePseudoDivide(b, c);
            //System.out.println("d  = " + d);

            assertEquals("a == ac/c", a, d);
        }
    }


    /**
     * Test base content and primitive part.
     */
    public void testBaseContentPP() {
        for (int i = 0; i < 13; i++) {
            c = dfac.random(kl * (i + 2), ll + 2 * i, el + i, q);
            c = c.multiply(mi.random(kl * (i + 2)));

            if (c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            ci = ufd.baseContent(c);
            d = ufd.basePrimitivePart(c);
            //System.out.println("c  = " + c);
            //System.out.println("ci = " + ci);
            //System.out.println("d  = " + d);

            a = d.multiply(ci);
            assertEquals("c == cont(c)pp(c)", c, a);
        }
    }


    /**
     * Test base gcd.
     */
    public void testBaseGcd() {
        dfac = new GenPolynomialRing<ModLong>(mi, 1, to);

        for (int i = 0; i < 5; i++) {
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

            d = ufd.baseGcd(a, b);
            e = PolyUtil.<ModLong> baseSparsePseudoRemainder(d, c);
            //System.out.println("d  = " + d);

            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());
        }
    }


    /**
     * Test recursive quotioent and remainder.
     */
    public void testRecursiveQR() {
        dfac = new GenPolynomialRing<ModLong>(mi, 2, to);
        cfac = new GenPolynomialRing<ModLong>(mi, 2 - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<ModLong>>(cfac, 1, to);

        for (int i = 0; i < 5; i++) {
            a = dfac.random(kl * (i + 1), ll + i, el + i, q);
            a = ufd.basePrimitivePart(a).abs();

            c = dfac.random(kl * (i + 1), ll + i, el + i, q);
            c = ufd.basePrimitivePart(a).abs();
            cr = PolyUtil.<ModLong> recursive(rfac, c);

            c = cfac.random(kl * (i + 1), ll + 2 * i, el + 2 * i, q);
            c = ufd.basePrimitivePart(c).abs();

            ar = PolyUtil.<ModLong> recursive(rfac, a);
            //System.out.println("ar = " + ar);
            //System.out.println("a  = " + a);
            //System.out.println("c  = " + c);
            //System.out.println("cr = " + cr);

            if (cr.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( cr" + i + " ) <> 0", cr.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );


            br = ar.multiply(cr);
            //System.out.println("br = " + br);
            dr = PolyUtil.<ModLong> recursiveSparsePseudoRemainder(br, cr);
            //System.out.println("dr = " + dr);
            d = PolyUtil.<ModLong> distribute(dfac, dr);
            //System.out.println("d  = " + d);

            assertTrue("rem(ac,c) == 0", d.isZERO());

            br = ar.multiply(c);
            //System.out.println("br = " + br);
            dr = PolyUtil.<ModLong> recursiveDivide(br, c);
            //System.out.println("dr = " + dr);
            d = PolyUtil.<ModLong> distribute(dfac, dr);
            //System.out.println("d  = " + d);

            assertEquals("a == ac/c", a, d);
        }
    }


    /**
     * Test recursive content and primitive part.
     */
    public void testRecursiveContentPP() {
        dfac = new GenPolynomialRing<ModLong>(mi, 2, to);
        cfac = new GenPolynomialRing<ModLong>(mi, 2 - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<ModLong>>(cfac, 1, to);

        for (int i = 0; i < 3; i++) {
            cr = rfac.random(kl * (i + 2), ll + 2 * i, el + i, q);
            //System.out.println("cr = " + cr);

            assertTrue("length( cr" + i + " ) <> 0", cr.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            c = ufd.recursiveContent(cr);
            dr = ufd.recursivePrimitivePart(cr);
            //System.out.println("c  = " + c);
            //System.out.println("dr = " + dr);

            ar = dr.multiply(c);
            assertEquals("c == cont(c)pp(c)", cr, ar);
        }
    }


    /**
     * Test recursive gcd.
     */
    public void testRecursiveGCD() {
        dfac = new GenPolynomialRing<ModLong>(mi, 2, to);
        cfac = new GenPolynomialRing<ModLong>(mi, 2 - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<ModLong>>(cfac, 1, to);

        for (int i = 0; i < 2; i++) {
            ar = rfac.random(kl, ll, el + i, q);
            br = rfac.random(kl, ll, el, q);
            cr = rfac.random(kl, ll, el, q);
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

            ar = ar.multiply(cr);
            br = br.multiply(cr);
            //System.out.println("ar = " + ar);
            //System.out.println("br = " + br);

            dr = ufd.recursiveUnivariateGcd(ar, br);
            //System.out.println("dr = " + dr);

            er = PolyUtil.<ModLong> recursiveSparsePseudoRemainder(dr, cr);
            //System.out.println("er = " + er);

            assertTrue("c | gcd(ac,bc) " + er, er.isZERO());
        }
    }


    /**
     * Test arbitrary recursive gcd.
     */
    public void testArbitraryRecursiveGCD() {
        dfac = new GenPolynomialRing<ModLong>(mi, 2, to);
        cfac = new GenPolynomialRing<ModLong>(mi, 2 - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<ModLong>>(cfac, 1, to);

        for (int i = 0; i < 2; i++) {
            ar = rfac.random(kl, ll, el + i, q);
            br = rfac.random(kl, ll, el, q);
            cr = rfac.random(kl, ll, el, q);
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

            ar = ar.multiply(cr);
            br = br.multiply(cr);
            //System.out.println("ar = " + ar);
            //System.out.println("br = " + br);

            dr = ufd.recursiveGcd(ar, br);
            //System.out.println("dr = " + dr);

            er = PolyUtil.<ModLong> recursiveSparsePseudoRemainder(dr, cr);
            //System.out.println("er = " + er);

            assertTrue("c | gcd(ac,bc) " + er, er.isZERO());
        }
    }


    /**
     * Test content and primitive part.
     */
    public void testContentPP() {
        dfac = new GenPolynomialRing<ModLong>(mi, 3, to);

        for (int i = 0; i < 3; i++) {
            c = dfac.random(kl * (i + 2), ll + 2 * i, el + i, q);
            //System.out.println("cr = " + cr);
            if (c.isZERO()) {
                continue;
            }

            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            a = ufd.content(c);
            e = a.extend(dfac, 0, 0L);
            b = ufd.primitivePart(c);
            //System.out.println("c  = " + c);
            //System.out.println("a  = " + a);
            //System.out.println("e  = " + e);
            //System.out.println("b  = " + b);

            d = e.multiply(b);
            assertEquals("c == cont(c)pp(c)", d, c);
        }
    }


    /**
     * Test gcd 3 variables.
     */
    public void testGCD3() {
        dfac = new GenPolynomialRing<ModLong>(mi, 3, to);

        for (int i = 0; i < 4; i++) {
            a = dfac.random(kl, ll, el + i, q);
            b = dfac.random(kl, ll, el, q);
            c = dfac.random(kl, ll, el, q);
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

            a = a.multiply(c);
            b = b.multiply(c);
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);

            d = ufd.gcd(a, b);
            //System.out.println("d = " + d);

            e = PolyUtil.<ModLong> baseSparsePseudoRemainder(d, c);
            //System.out.println("e = " + e);

            assertTrue("c | gcd(ac,bc): c = " + c + ", d = " + d + ", " + e, e.isZERO());
        }
    }


    /**
     * Test gcd.
     */
    public void testGCD() {
        // dfac = new GenPolynomialRing<ModLong>(mi,3,to);

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll, el, q);
            b = dfac.random(kl, ll, el, q);
            c = dfac.random(kl, ll, el, q);
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

            a = a.multiply(c);
            b = b.multiply(c);
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);

            d = ufd.gcd(a, b);
            //System.out.println("d = " + d);

            e = PolyUtil.<ModLong> baseSparsePseudoRemainder(d, c);
            //System.out.println("e = " + e);
            assertTrue("c | gcd(ac,bc) " + e + ", " + c, e.isZERO());

            e = PolyUtil.<ModLong> baseSparsePseudoRemainder(a, d);
            //System.out.println("e = " + e);
            assertTrue("gcd(a,b) | a " + e + ", " + d, e.isZERO());

            e = PolyUtil.<ModLong> baseSparsePseudoRemainder(b, d);
            //System.out.println("e = " + e);
            assertTrue("gcd(a,b) | b " + e + ", " + d, e.isZERO());
        }
    }


    /**
     * Test lcm.
     */
    public void testLCM() {
        dfac = new GenPolynomialRing<ModLong>(mi, 3, to);

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll, el, q);
            b = dfac.random(kl, ll, el, q);
            c = dfac.random(kl, 3, 2, q);
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( a" + i + " ) <> 0", a.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            a = a.multiply(c);
            b = b.multiply(c);

            c = ufd.gcd(a, b);
            //System.out.println("c = " + c);

            d = ufd.lcm(a, b);
            //System.out.println("d = " + d);

            e = c.multiply(d);
            //System.out.println("e = " + e);
            c = a.multiply(b);
            //System.out.println("c = " + c);

            assertEquals("ab == gcd(a,b)lcm(ab)", c, e);
        }
    }


    /**
     * Test co-prime factors.
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
        F.add(d);
        F.add(a);
        F.add(b);
        F.add(c);
        F.add(e);

        List<GenPolynomial<ModLong>> P = ufd.coPrime(F);
        //System.out.println("F = " + F);
        //System.out.println("P = " + P);

        assertTrue("is co-prime ", ufd.isCoPrime(P));
        assertTrue("is co-prime of ", ufd.isCoPrime(P, F));


        //P = ufd.coPrimeSquarefree(F);
        //System.out.println("F = " + F);
        //System.out.println("P = " + P);
        //assertTrue("is co-prime ", ufd.isCoPrime(P) );
        //assertTrue("is co-prime of ", ufd.isCoPrime(P,F) );


        P = ufd.coPrimeRec(F);
        //System.out.println("F = " + F);
        //System.out.println("P = " + P);

        assertTrue("is co-prime ", ufd.isCoPrime(P));
        assertTrue("is co-prime of ", ufd.isCoPrime(P, F));
    }

}
