/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.SortedMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
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

public class SquarefreeAlgModTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>SquarefreeAlgModTest</CODE> object.
     * @param name String.
     */
    public SquarefreeAlgModTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SquarefreeAlgModTest.class);
        return suite;
    }


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    //long p = 11L;
    long p = 7L;


    long qp;


    int rl = 3;


    int kl = 3;


    int ll = 4;


    int el = 3;


    float q = 0.25f;


    String[] vars;


    String[] cvars;


    String[] c1vars;


    String[] rvars;


    ModIntegerRing mfac;


    String[] alpha;


    GenPolynomialRing<ModInteger> mpfac;


    GenPolynomial<ModInteger> agen;


    AlgebraicNumberRing<ModInteger> fac;


    GreatestCommonDivisorAbstract<AlgebraicNumber<ModInteger>> ufd;


    SquarefreeFiniteFieldCharP<AlgebraicNumber<ModInteger>> sqf;


    GenPolynomialRing<AlgebraicNumber<ModInteger>> dfac;


    GenPolynomial<AlgebraicNumber<ModInteger>> a;


    GenPolynomial<AlgebraicNumber<ModInteger>> b;


    GenPolynomial<AlgebraicNumber<ModInteger>> c;


    GenPolynomial<AlgebraicNumber<ModInteger>> d;


    GenPolynomial<AlgebraicNumber<ModInteger>> e;


    GenPolynomialRing<AlgebraicNumber<ModInteger>> cfac;


    GenPolynomialRing<GenPolynomial<AlgebraicNumber<ModInteger>>> rfac;


    GenPolynomial<GenPolynomial<AlgebraicNumber<ModInteger>>> ar;


    GenPolynomial<GenPolynomial<AlgebraicNumber<ModInteger>>> br;


    GenPolynomial<GenPolynomial<AlgebraicNumber<ModInteger>>> cr;


    GenPolynomial<GenPolynomial<AlgebraicNumber<ModInteger>>> dr;


    GenPolynomial<GenPolynomial<AlgebraicNumber<ModInteger>>> er;


    @Override
    protected void setUp() {
        vars = ExpVector.STDVARS(rl);
        cvars = ExpVector.STDVARS(rl - 1);
        c1vars = new String[] { cvars[0] };
        rvars = new String[] { vars[rl - 1] };

        mfac = new ModIntegerRing(p);
        alpha = new String[] { "alpha" };
        mpfac = new GenPolynomialRing<ModInteger>(mfac, 1, to, alpha);
        agen = mpfac.univariate(0, 2);
        agen = agen.sum(mpfac.getONE()); // x^2 + 1, is irred mod 7, 11, 19
        fac = new AlgebraicNumberRing<ModInteger>(agen, true);
        qp = 1L;
        for (int i = 0; i < agen.degree(0); i++) {
            qp = qp * p;
        }
        //System.out.println("p = " + p + ", qp = " + qp);

        //ufd = new GreatestCommonDivisorSubres<AlgebraicNumber<ModInteger>>();
        //ufd = GCDFactory.<AlgebraicNumber<ModInteger>> getImplementation(fac);
        ufd = GCDFactory.<AlgebraicNumber<ModInteger>> getProxy(fac);

        sqf = new SquarefreeFiniteFieldCharP<AlgebraicNumber<ModInteger>>(fac);

        SquarefreeAbstract<AlgebraicNumber<ModInteger>> sqff = SquarefreeFactory.getImplementation(fac);
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

        dfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac, 1, to, rvars);

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

        e = PolyUtil.<AlgebraicNumber<ModInteger>> basePseudoRemainder(d, c);
        //System.out.println("e  = " + e);
        assertTrue("squarefree(abc) | squarefree(aabbbc) " + e, e.isZERO());
    }


    /**
     * Test base squarefree factors.
     * 
     */
    public void testBaseSquarefreeFactors() {

        dfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac, 1, to, rvars);

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

        SortedMap<GenPolynomial<AlgebraicNumber<ModInteger>>, Long> sfactors;
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

        cfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac, 2 - 1, to, c1vars);
        rfac = new GenPolynomialRing<GenPolynomial<AlgebraicNumber<ModInteger>>>(cfac, 1, to, rvars);

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

        er = PolyUtil.<AlgebraicNumber<ModInteger>> recursivePseudoRemainder(dr, cr);
        //System.out.println("er  = " + er);
        assertTrue("squarefree(abc) | squarefree(aabbc) " + er, er.isZERO());
    }


    /**
     * Test recursive squarefree factors.
     * 
     */
    public void testRecursiveSquarefreeFactors() {

        cfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac, 2 - 1, to, c1vars);
        rfac = new GenPolynomialRing<GenPolynomial<AlgebraicNumber<ModInteger>>>(cfac, 1, to, rvars);

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

        SortedMap<GenPolynomial<GenPolynomial<AlgebraicNumber<ModInteger>>>, Long> sfactors;
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

        dfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac, rl, to, vars);

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

        e = PolyUtil.<AlgebraicNumber<ModInteger>> basePseudoRemainder(d, c);
        //System.out.println("e  = " + e);
        assertTrue("squarefree(abc) | squarefree(aabbc) " + e, e.isZERO());
    }


    /**
     * Test squarefree factors.
     * 
     */
    public void testSquarefreeFactors() {

        dfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac, rl, to, vars);

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

        SortedMap<GenPolynomial<AlgebraicNumber<ModInteger>>, Long> sfactors;
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

        //dfac = new GenPolynomialRing<ModInteger>(fac,1,to,rvars);
        dfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac, 1, to, rvars);

        a = dfac.random(kl, ll + 1, el + 1, q).monic();
        b = dfac.random(kl, ll + 1, el + 1, q).monic();
        c = dfac.random(kl, ll, el, q).monic();

        if (a.isZERO() || b.isZERO() || c.isZERO() || a.isConstant() || b.isConstant()) {
            // skip for this turn
            return;
        }
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        e = Power.<GenPolynomial<AlgebraicNumber<ModInteger>>> positivePower(b, p);
        //System.out.println("b^p  = " + e);
        // a a b^p c
        d = a.multiply(a).multiply(e).multiply(c);
        c = a.multiply(b).multiply(c);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);

        c = sqf.baseSquarefreePart(c);
        d = sqf.baseSquarefreePart(d);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);
        assertTrue("isSquarefree(c) " + c, sqf.isSquarefree(c));
        assertTrue("isSquarefree(d) " + d, sqf.isSquarefree(d));

        e = PolyUtil.<AlgebraicNumber<ModInteger>> basePseudoRemainder(d, c);
        //System.out.println("e  = " + e);
        assertTrue("squarefree(abc) | squarefree(aab^pc) " + e, e.isZERO());
    }


    /**
     * Test base squarefree factors with char-th root.
     * 
     */
    public void testBaseSquarefreeFactorsCharRoot() {

        long p = fac.characteristic().longValue();

        //dfac = new GenPolynomialRing<ModInteger>(fac,1,to,rvars);
        dfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac, 1, to, rvars);

        a = dfac.random(kl, ll + 1, el + 2, q).monic();
        b = dfac.random(kl, ll + 1, el + 2, q).monic();
        c = dfac.random(kl, ll, el + 1, q).monic();

        if (a.isZERO() || b.isZERO() || c.isZERO() || a.isConstant() || b.isConstant()) {
            // skip for this turn
            return;
        }
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        e = Power.<GenPolynomial<AlgebraicNumber<ModInteger>>> positivePower(b, p);
        //System.out.println("b^p  = " + e);
        // a a b^p 
        d = a.multiply(a).multiply(e);
        //d = d.monic();
        //System.out.println("d  = " + d);

        SortedMap<GenPolynomial<AlgebraicNumber<ModInteger>>, Long> sfactors;
        sfactors = sqf.baseSquarefreeFactors(d);
        //System.out.println("sfactors = " + sfactors);
        assertTrue("isFactorization(d,sfactors) ", sqf.isFactorization(d, sfactors));
    }


    /**
     * Test base squarefree with char-th root, two times.
     * 
     */
    public void testBaseSquarefreeCharRoot2() {
        //System.out.println("\nbase CharRoot 2:");
        //long p = fac.characteristic().longValue();

        //dfac = new GenPolynomialRing<ModInteger>(fac,1,to,rvars);
        dfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac, 1, to, rvars);

        a = dfac.random(kl, ll + 1, el + 1, q).monic();
        b = dfac.random(kl, ll + 1, el + 1, q).monic();
        c = dfac.random(kl, ll, el, q).monic();

        if (a.isZERO() || b.isZERO() || c.isZERO() || a.isConstant() || b.isConstant()) {
            // skip for this turn
            return;
        }
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        e = Power.<GenPolynomial<AlgebraicNumber<ModInteger>>> positivePower(b, qp);
        //System.out.println("b^qp  = " + e);
        // a a b^qp c
        d = a.multiply(a).multiply(e).multiply(c);
        c = a.multiply(b).multiply(c);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);

        c = sqf.baseSquarefreePart(c);
        d = sqf.baseSquarefreePart(d);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);
        assertTrue("isSquarefree(c) " + c, sqf.isSquarefree(c));
        assertTrue("isSquarefree(d) " + d, sqf.isSquarefree(d));

        e = PolyUtil.<AlgebraicNumber<ModInteger>> basePseudoRemainder(d, c);
        //System.out.println("e  = " + e);
        assertTrue("squarefree(abc) | squarefree(aab^pc) " + e, e.isZERO());
    }


    /**
     * Test base squarefree factors with char-th root, two times.
     * 
     */
    public void testBaseSquarefreeFactorsCharRoot2() {
        //long p = fac.characteristic().longValue();

        //dfac = new GenPolynomialRing<ModInteger>(fac,1,to,rvars);
        dfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac, 1, to, rvars);

        a = dfac.random(kl, ll + 1, el + 2, q).monic();
        b = dfac.random(kl, ll + 1, el + 2, q).monic();
        c = dfac.random(kl, ll, el + 2, q).monic();

        if (a.isZERO() || b.isZERO() || c.isZERO() || a.isConstant() || b.isConstant()) {
            // skip for this turn
            return;
        }
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        e = Power.<GenPolynomial<AlgebraicNumber<ModInteger>>> positivePower(b, qp);
        //System.out.println("b^qp  = " + e);
        // a a b^qp 
        d = a.multiply(a).multiply(e);
        //d = d.monic();
        //System.out.println("d  = " + d);

        SortedMap<GenPolynomial<AlgebraicNumber<ModInteger>>, Long> sfactors;
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

        cfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac, 2 - 1, to, c1vars);
        rfac = new GenPolynomialRing<GenPolynomial<AlgebraicNumber<ModInteger>>>(cfac, 1, to, rvars);

        ar = rfac.random(kl, 3, el, q);
        br = rfac.random(kl, 3, el, q);
        cr = rfac.random(kl, 3, el, q);
        //cr = rfac.getONE();

        if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
            // skip for this turn
            return;
        }
        //ar = PolyUtil.<AlgebraicNumber<ModInteger>>monic(ar);
        //br = PolyUtil.<AlgebraicNumber<ModInteger>>monic(br);
        //cr = PolyUtil.<AlgebraicNumber<ModInteger>>monic(cr);
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);
        //System.out.println("cr = " + cr);

        er = Power.<GenPolynomial<GenPolynomial<AlgebraicNumber<ModInteger>>>> positivePower(br, p);
        //System.out.println("b^p  = " + er);
        // a a b^p c
        dr = ar.multiply(ar).multiply(er).multiply(cr);
        cr = ar.multiply(br).multiply(cr);
        //System.out.println("cr  = " + cr);
        //System.out.println("dr  = " + dr);

        cr = sqf.recursiveUnivariateSquarefreePart(cr);
        dr = sqf.recursiveUnivariateSquarefreePart(dr);
        //System.out.println("cr  = " + cr);
        //System.out.println("dr  = " + dr);
        assertTrue("isSquarefree(cr) " + cr, sqf.isRecursiveSquarefree(cr));
        assertTrue("isSquarefree(dr) " + dr, sqf.isRecursiveSquarefree(dr));

        er = PolyUtil.<AlgebraicNumber<ModInteger>> recursivePseudoRemainder(dr, cr);
        //System.out.println("er  = " + er);
        assertTrue("squarefree(abc) | squarefree(aabbc) " + er, er.isZERO());
    }


    /**
     * Test recursive squarefree factors with char-th root.
     * 
     */
    public void testRecursiveSquarefreeFactorsCharRoot() {

        long p = fac.characteristic().longValue();

        cfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac, 2 - 1, to, c1vars);
        rfac = new GenPolynomialRing<GenPolynomial<AlgebraicNumber<ModInteger>>>(cfac, 1, to, rvars);

        ar = rfac.random(kl, 3, 2, q);
        br = rfac.random(kl, 3, 2, q);
        cr = rfac.random(kl, 3, 2, q);
        //cr = rfac.getONE();

        if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
            // skip for this turn
            return;
        }
        //ar = PolyUtil.<AlgebraicNumber<ModInteger>>monic(ar);
        //br = PolyUtil.<AlgebraicNumber<ModInteger>>monic(br);
        //cr = PolyUtil.<AlgebraicNumber<ModInteger>>monic(cr);
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);
        //System.out.println("cr = " + cr);

        er = Power.<GenPolynomial<GenPolynomial<AlgebraicNumber<ModInteger>>>> positivePower(br, p);
        //System.out.println("b^p  = " + er);
        // a a b^p c
        dr = ar.multiply(ar).multiply(er).multiply(cr);
        //System.out.println("dr  = " + dr);

        SortedMap<GenPolynomial<GenPolynomial<AlgebraicNumber<ModInteger>>>, Long> sfactors;
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

        dfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac, rl, to, vars);

        a = dfac.random(kl, 3, 3, q);
        b = dfac.random(kl, 3, 3, q);
        c = dfac.random(kl, 3, 3, q);
        //c = dfac.getONE();

        if (a.isZERO() || b.isZERO() || c.isZERO() || a.isConstant() || b.isConstant()) {
            // skip for this turn
            return;
        }
        //a = a.monic();
        //b = b.monic();
        //c = c.monic();
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        e = Power.<GenPolynomial<AlgebraicNumber<ModInteger>>> positivePower(b, p);
        //System.out.println("b^p  = " + e);
        // a a b^p c
        d = a.multiply(a).multiply(e).multiply(c);
        c = a.multiply(b).multiply(c);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);

        c = sqf.squarefreePart(c);
        d = sqf.squarefreePart(d);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);
        assertTrue("isSquarefree(d) " + d, sqf.isSquarefree(d));
        assertTrue("isSquarefree(c) " + c, sqf.isSquarefree(c));

        e = PolyUtil.<AlgebraicNumber<ModInteger>> basePseudoRemainder(d, c);
        //System.out.println("e  = " + e);
        assertTrue("squarefree(abc) | squarefree(aab^pc) " + e, e.isZERO());
    }


    /**
     * Test squarefree factors with char-th root.
     * 
     */
    public void testSquarefreeFactorsCharRoot() {

        long p = fac.characteristic().longValue();

        dfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac, rl, to, vars);

        a = dfac.random(kl, 3, 3, q);
        b = dfac.random(kl, 3, 3, q);
        c = dfac.random(kl, 3, 3, q);
        //c = dfac.getONE();

        if (a.isZERO() || b.isZERO() || c.isZERO() || a.isConstant() || b.isConstant()) {
            // skip for this turn
            return;
        }
        //a = a.monic();
        //b = b.monic();
        //c = c.monic();
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        e = Power.<GenPolynomial<AlgebraicNumber<ModInteger>>> positivePower(b, p);
        //System.out.println("b^p  = " + e);
        // a a b^p c
        d = a.multiply(a).multiply(e).multiply(c);
        //System.out.println("d  = " + d);

        SortedMap<GenPolynomial<AlgebraicNumber<ModInteger>>, Long> sfactors;
        sfactors = sqf.squarefreeFactors(d);
        //System.out.println("sfactors = " + sfactors);

        assertTrue("isFactorization(d,sfactors) ", sqf.isFactorization(d, sfactors));
    }

}
