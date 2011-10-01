/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.SortedMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.structure.Power;


/**
 * Squarefree factorization tests with JUnit.
 * @author Heinz Kredel.
 */

public class SquarefreeModLongTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>SquarefreeModLongTest</CODE> object.
     * @param name String.
     */
    public SquarefreeModLongTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SquarefreeModLongTest.class);
        return suite;
    }


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    int rl = 3;


    int kl = 3;


    int ll = 4;


    int el = 3;


    float q = 0.25f;


    String[] vars;


    String[] cvars;


    String[] c1vars;


    String[] rvars;


    ModLongRing fac;


    GreatestCommonDivisorAbstract<ModLong> ufd;


    SquarefreeFiniteFieldCharP<ModLong> sqf;


    GenPolynomialRing<ModLong> dfac;


    GenPolynomial<ModLong> a;


    GenPolynomial<ModLong> b;


    GenPolynomial<ModLong> c;


    GenPolynomial<ModLong> d;


    GenPolynomial<ModLong> e;


    GenPolynomialRing<ModLong> cfac;


    GenPolynomialRing<GenPolynomial<ModLong>> rfac;


    GenPolynomial<GenPolynomial<ModLong>> ar;


    GenPolynomial<GenPolynomial<ModLong>> br;


    GenPolynomial<GenPolynomial<ModLong>> cr;


    GenPolynomial<GenPolynomial<ModLong>> dr;


    GenPolynomial<GenPolynomial<ModLong>> er;


    @Override
    protected void setUp() {
        vars = ExpVector.STDVARS(rl);
        cvars = ExpVector.STDVARS(rl - 1);
        c1vars = new String[] { cvars[0] };
        rvars = new String[] { vars[rl - 1] };

        fac = new ModLongRing(11);
        //ufd = new GreatestCommonDivisorSubres<ModLong>();
        //ufd = GCDFactory.<ModLong> getImplementation(fac);
        ufd = GCDFactory.getProxy(fac);
        sqf = new SquarefreeFiniteFieldCharP<ModLong>(fac);

        SquarefreeAbstract<ModLong> sqff = SquarefreeFactory.getImplementation(fac);
        //System.out.println("sqf  = " + sqf);
        //System.out.println("sqff = " + sqff);
        assertEquals("sqf == sqff ", sqf.getClass(), sqff.getClass());

        a = b = c = d = e = null;
        ar = br = cr = dr = er = null;
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ar = br = cr = dr = er = null;
        //ComputerThreads.terminate();
    }


    /**
     * Test base squarefree.
     * 
     */
    public void testBaseSquarefree() {
        //System.out.println("\nbase:");

        dfac = new GenPolynomialRing<ModLong>(fac, 1, to, rvars);

        a = dfac.random(kl, ll, el + 2, q);
        b = dfac.random(kl, ll, el + 2, q);
        c = dfac.random(kl, ll, el, q);
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            // skip for this turn
            return;
        }

        // a a b b b c
        d = a.multiply(a).multiply(b).multiply(b).multiply(b).multiply(c);
        c = a.multiply(b).multiply(c);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);

        c = sqf.baseSquarefreePart(c);
        d = sqf.baseSquarefreePart(d);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);
        assertTrue("isSquarefree(c) " + c, sqf.isSquarefree(c));
        assertTrue("isSquarefree(d) " + d, sqf.isSquarefree(d));

        e = PolyUtil.<ModLong> basePseudoRemainder(d, c);
        //System.out.println("e  = " + e);
        assertTrue("squarefree(abc) | squarefree(aabbbc) " + e, e.isZERO());
    }


    /**
     * Test base squarefree factors.
     * 
     */
    public void testBaseSquarefreeFactors() {

        dfac = new GenPolynomialRing<ModLong>(fac, 1, to, rvars);

        a = dfac.random(kl, ll, el + 3, q);
        b = dfac.random(kl, ll, el + 3, q);
        c = dfac.random(kl, ll, el + 2, q);
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            // skip for this turn
            return;
        }

        // a a b b b c
        d = a.multiply(a).multiply(b).multiply(b).multiply(b).multiply(c);
        //System.out.println("d  = " + d);

        SortedMap<GenPolynomial<ModLong>, Long> sfactors;
        sfactors = sqf.baseSquarefreeFactors(d);
        //System.out.println("sfactors = " + sfactors);
        assertTrue("isFactorization(d,sfactors) ", sqf.isFactorization(d, sfactors));
    }


    /**
     * Test recursive squarefree.
     * 
     */
    public void testRecursiveSquarefree() {
        //System.out.println("\nrecursive:");

        cfac = new GenPolynomialRing<ModLong>(fac, 2 - 1, to, c1vars);
        rfac = new GenPolynomialRing<GenPolynomial<ModLong>>(cfac, 1, to, rvars);

        ar = rfac.random(kl, ll, el, q);
        br = rfac.random(kl, ll, el, q);
        cr = rfac.random(kl, ll, el, q);
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);
        //System.out.println("cr = " + cr);

        if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
            // skip for this turn
            return;
        }

        dr = ar.multiply(ar).multiply(br).multiply(br);
        cr = ar.multiply(br);
        //System.out.println("cr  = " + cr);
        //System.out.println("dr  = " + dr);

        cr = sqf.recursiveUnivariateSquarefreePart(cr);
        dr = sqf.recursiveUnivariateSquarefreePart(dr);
        //System.out.println("cr  = " + cr);
        //System.out.println("dr  = " + dr);
        assertTrue("isSquarefree(cr) " + cr, sqf.isRecursiveSquarefree(cr));
        assertTrue("isSquarefree(dr) " + dr, sqf.isRecursiveSquarefree(dr));

        er = PolyUtil.<ModLong> recursivePseudoRemainder(dr, cr);
        //System.out.println("er  = " + er);
        assertTrue("squarefree(abc) | squarefree(aabbc) " + er, er.isZERO());
    }


    /**
     * Test recursive squarefree factors.
     * 
     */
    public void testRecursiveSquarefreeFactors() {

        cfac = new GenPolynomialRing<ModLong>(fac, 2 - 1, to, c1vars);
        rfac = new GenPolynomialRing<GenPolynomial<ModLong>>(cfac, 1, to, rvars);

        ar = rfac.random(kl, 3, 2, q);
        br = rfac.random(kl, 3, 2, q);
        cr = rfac.random(kl, 3, 2, q);
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);
        //System.out.println("cr = " + cr);

        if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
            // skip for this turn
            return;
        }

        dr = ar.multiply(cr).multiply(br).multiply(br);
        //System.out.println("dr  = " + dr);

        SortedMap<GenPolynomial<GenPolynomial<ModLong>>, Long> sfactors;
        sfactors = sqf.recursiveUnivariateSquarefreeFactors(dr);
        //System.out.println("sfactors = " + sfactors);
        assertTrue("isFactorization(d,sfactors) ", sqf.isRecursiveFactorization(dr, sfactors));
    }


    /**
     * Test squarefree.
     * 
     */
    public void testSquarefree() {
        //System.out.println("\nfull:");

        dfac = new GenPolynomialRing<ModLong>(fac, rl, to, vars);

        a = dfac.random(kl, ll, 2, q);
        b = dfac.random(kl, ll, 2, q);
        c = dfac.random(kl, ll, 2, q);
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            // skip for this turn
            return;
        }

        d = a.multiply(a).multiply(b).multiply(b).multiply(c);
        c = a.multiply(b).multiply(c);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);

        c = sqf.squarefreePart(c);
        d = sqf.squarefreePart(d);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);
        assertTrue("isSquarefree(d) " + d, sqf.isSquarefree(d));
        assertTrue("isSquarefree(c) " + c, sqf.isSquarefree(c));

        e = PolyUtil.<ModLong> basePseudoRemainder(d, c);
        //System.out.println("e  = " + e);
        assertTrue("squarefree(abc) | squarefree(aabbc) " + e, e.isZERO());
    }


    /**
     * Test squarefree factors.
     * 
     */
    public void testSquarefreeFactors() {

        dfac = new GenPolynomialRing<ModLong>(fac, rl, to, vars);

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

        d = a.multiply(a).multiply(b).multiply(b).multiply(b).multiply(c);
        //System.out.println("d  = " + d);

        SortedMap<GenPolynomial<ModLong>, Long> sfactors;
        sfactors = sqf.squarefreeFactors(d);
        //System.out.println("sfactors = " + sfactors);
        assertTrue("isFactorization(d,sfactors) ", sqf.isFactorization(d, sfactors));
    }


    /* ------------char-th root ------------------------- */

    /**
     * Test base squarefree with char-th root.
     * 
     */
    public void testBaseSquarefreeCharRoot() {
        //System.out.println("\nbase CharRoot:");

        long p = fac.characteristic().longValue();

        dfac = new GenPolynomialRing<ModLong>(fac, 1, to, rvars);

        a = dfac.random(kl, ll + 2, el + 2, q);
        b = dfac.random(kl, ll + 2, el + 2, q);
        c = dfac.random(kl, ll, el, q);

        if (a.isZERO() || b.isZERO() || c.isZERO() || a.isConstant() || b.isConstant()) {
            // skip for this turn
            return;
        }
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        // a a b^p c
        d = a.multiply(a).multiply(Power.<GenPolynomial<ModLong>> positivePower(b, p)).multiply(c);
        c = a.multiply(b).multiply(c);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);

        c = sqf.baseSquarefreePart(c);
        d = sqf.baseSquarefreePart(d);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);
        assertTrue("isSquarefree(c) " + c, sqf.isSquarefree(c));
        assertTrue("isSquarefree(d) " + d, sqf.isSquarefree(d));

        e = PolyUtil.<ModLong> basePseudoRemainder(d, c);
        //System.out.println("e  = " + e);
        assertTrue("squarefree(abc) | squarefree(aab^pc) " + e, e.isZERO());
    }


    /**
     * Test base squarefree factors with char-th root.
     * 
     */
    public void testBaseSquarefreeFactorsCharRoot() {

        long p = fac.characteristic().longValue();

        dfac = new GenPolynomialRing<ModLong>(fac, 1, to, rvars);

        a = dfac.random(kl, ll + 2, el + 3, q);
        b = dfac.random(kl, ll + 2, el + 3, q);
        c = dfac.random(kl, ll, el + 2, q);

        if (a.isZERO() || b.isZERO() || c.isZERO() || a.isConstant() || b.isConstant()) {
            // skip for this turn
            return;
        }
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        // a a b^p c
        d = a.multiply(a).multiply(Power.<GenPolynomial<ModLong>> positivePower(b, p)).multiply(c);
        //System.out.println("d  = " + d);

        SortedMap<GenPolynomial<ModLong>, Long> sfactors;
        sfactors = sqf.baseSquarefreeFactors(d);
        //System.out.println("sfactors = " + sfactors);
        assertTrue("isFactorization(d,sfactors) ", sqf.isFactorization(d, sfactors));
    }


    /**
     * Test recursive squarefree with char-th root.
     * 
     */
    public void testRecursiveSquarefreeCharRoot() {
        //System.out.println("\nrecursive CharRoot:");

        long p = fac.characteristic().longValue();

        cfac = new GenPolynomialRing<ModLong>(fac, 2 - 1, to, c1vars);
        rfac = new GenPolynomialRing<GenPolynomial<ModLong>>(cfac, 1, to, rvars);

        ar = rfac.random(kl, ll, el + 1, q).monic();
        br = rfac.random(kl, ll, el + 1, q).monic();
        cr = rfac.random(kl, ll, el, q).monic();

        if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
            // skip for this turn
            return;
        }
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);
        //System.out.println("cr = " + cr);

        // a a b^p 
        dr = ar.multiply(ar).multiply(Power.<GenPolynomial<GenPolynomial<ModLong>>> positivePower(br, p));
        cr = ar.multiply(ar).multiply(br);
        //System.out.println("cr  = " + cr);
        //System.out.println("dr  = " + dr);

        cr = sqf.recursiveUnivariateSquarefreePart(cr);
        dr = sqf.recursiveUnivariateSquarefreePart(dr);
        //System.out.println("cr  = " + cr);
        //System.out.println("dr  = " + dr);
        assertTrue("isSquarefree(cr) " + cr, sqf.isRecursiveSquarefree(cr));
        assertTrue("isSquarefree(dr) " + dr, sqf.isRecursiveSquarefree(dr));

        er = PolyUtil.<ModLong> recursivePseudoRemainder(dr, cr);
        //System.out.println("er  = " + er);
        assertTrue("squarefree(abc) | squarefree(aabbc) " + er, er.isZERO());
    }


    /**
     * Test recursive squarefree factors with char-th root.
     * 
     */
    public void testRecursiveSquarefreeFactorsCharRoot() {

        long p = fac.characteristic().longValue();

        cfac = new GenPolynomialRing<ModLong>(fac, 2 - 1, to, c1vars);
        rfac = new GenPolynomialRing<GenPolynomial<ModLong>>(cfac, 1, to, rvars);

        ar = rfac.random(kl, 3, 2 + 1, q).monic();
        br = rfac.random(kl, 3, 2 + 1, q).monic();
        cr = rfac.random(kl, 3, 2, q).monic();

        if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
            // skip for this turn
            return;
        }
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);
        //System.out.println("cr = " + cr);

        // a a b^p c
        dr = ar.multiply(ar).multiply(Power.<GenPolynomial<GenPolynomial<ModLong>>> positivePower(br, p))
                .multiply(cr);
        //System.out.println("dr  = " + dr);

        SortedMap<GenPolynomial<GenPolynomial<ModLong>>, Long> sfactors;
        sfactors = sqf.recursiveUnivariateSquarefreeFactors(dr);
        //System.out.println("sfactors = " + sfactors);
        assertTrue("isFactorization(d,sfactors) ", sqf.isRecursiveFactorization(dr, sfactors));
    }


    /**
     * Test squarefree with char-th root.
     * 
     */
    public void testSquarefreeCharRoot() {
        //System.out.println("\nfull CharRoot:");

        long p = fac.characteristic().longValue();

        dfac = new GenPolynomialRing<ModLong>(fac, rl, to, vars);

        a = dfac.random(kl, ll, 3, q).monic();
        b = dfac.random(kl, ll, 3, q).monic();
        c = dfac.random(kl, ll, 3, q).monic();

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            // skip for this turn
            return;
        }
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        // a a b^p c
        d = a.multiply(a).multiply(Power.<GenPolynomial<ModLong>> positivePower(b, p)).multiply(c);
        c = a.multiply(b).multiply(c);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);

        c = sqf.squarefreePart(c);
        d = sqf.squarefreePart(d);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);
        assertTrue("isSquarefree(d) " + d, sqf.isSquarefree(d));
        assertTrue("isSquarefree(c) " + c, sqf.isSquarefree(c));

        e = PolyUtil.<ModLong> basePseudoRemainder(d, c);
        //System.out.println("e  = " + e);
        assertTrue("squarefree(abc) | squarefree(aab^pc) " + e, e.isZERO());
    }


    /**
     * Test squarefree factors with char-th root.
     * 
     */
    public void testSquarefreeFactorsCharRoot() {

        long p = fac.characteristic().longValue();

        dfac = new GenPolynomialRing<ModLong>(fac, rl, to, vars);

        a = dfac.random(kl, ll, 3, q).monic();
        b = dfac.random(kl, ll, 3, q).monic();
        c = dfac.random(kl, ll, 3, q).monic();

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            // skip for this turn
            return;
        }
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        // a a b^p c
        d = a.multiply(a).multiply(Power.<GenPolynomial<ModLong>> positivePower(b, p)).multiply(c);
        //System.out.println("d  = " + d);

        SortedMap<GenPolynomial<ModLong>, Long> sfactors;
        sfactors = sqf.squarefreeFactors(d);
        //System.out.println("sfactors = " + sfactors);
        assertTrue("isFactorization(d,sfactors) ", sqf.isFactorization(d, sfactors));
    }

}
