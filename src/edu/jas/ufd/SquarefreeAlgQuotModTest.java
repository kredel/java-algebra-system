/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.SortedMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.structure.Power;


/**
 * Squarefree factorization tests with JUnit.
 * @author Heinz Kredel.
 */

public class SquarefreeAlgQuotModTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>SquarefreeAlgQuotModTest</CODE> object.
     * @param name String.
     */
    public SquarefreeAlgQuotModTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SquarefreeAlgQuotModTest.class);
        return suite;
    }


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    int rl = 3;


    int kl = 1;


    int ll = 3;


    int el = 3;


    float q = 0.25f;


    String[] vars;


    String[] cvars;


    String[] c1vars;


    String[] rvars;


    ModIntegerRing mfac;


    String[] alpha;


    String[] beta;


    GenPolynomialRing<ModInteger> mpfac;


    GenPolynomial<ModInteger> agen;


    QuotientRing<ModInteger> fac;


    AlgebraicNumberRing<Quotient<ModInteger>> afac;


    SquarefreeInfiniteFieldCharP<ModInteger> sqf;


    SquarefreeInfiniteAlgebraicFieldCharP<Quotient<ModInteger>> asqf;


    GenPolynomialRing<AlgebraicNumber<Quotient<ModInteger>>> dfac;


    GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>> a;


    GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>> b;


    GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>> c;


    GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>> d;


    GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>> e;


    GenPolynomialRing<AlgebraicNumber<Quotient<ModInteger>>> cfac;


    GenPolynomialRing<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>> rfac;


    GenPolynomial<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>> ar;


    GenPolynomial<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>> br;


    GenPolynomial<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>> cr;


    GenPolynomial<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>> dr;


    GenPolynomial<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>> er;


    @Override
    protected void setUp() {
        vars = ExpVector.STDVARS(rl);
        cvars = ExpVector.STDVARS(rl - 1);
        c1vars = new String[] { cvars[0] };
        rvars = new String[] { vars[rl - 1] };

        mfac = new ModIntegerRing(7);
        alpha = new String[] { "u" };
        beta = new String[] { "b" };
        mpfac = new GenPolynomialRing<ModInteger>(mfac, 1, to, alpha);
        fac = new QuotientRing<ModInteger>(mpfac);

        GenPolynomialRing<Quotient<ModInteger>> qpfac 
           = new GenPolynomialRing<Quotient<ModInteger>>(fac, 1, to, beta);

        // beta^2 - 3
        GenPolynomial<Quotient<ModInteger>> an = qpfac.univariate(0,2L);
        an = an.subtract(qpfac.fromInteger(3));

        afac = new AlgebraicNumberRing<Quotient<ModInteger>>(an,true);


        sqf = new SquarefreeInfiniteFieldCharP<ModInteger>(fac);
        asqf = new SquarefreeInfiniteAlgebraicFieldCharP<Quotient<ModInteger>>(afac);

        SquarefreeAbstract<AlgebraicNumber<Quotient<ModInteger>>> sqff = SquarefreeFactory.getImplementation(afac);
        System.out.println("sqf  = " + sqf);
        System.out.println("sqff = " + sqff);
        //assertEquals("sqf == sqff ", sqf.getClass(), sqff.getClass());

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

        dfac = new GenPolynomialRing<AlgebraicNumber<Quotient<ModInteger>>>(afac, 1, to, rvars);

        a = dfac.random(kl + 1, ll, el + 1, q);
        b = dfac.random(kl + 1, ll, el + 1, q);
        c = dfac.random(kl, ll, el, q);
        System.out.println("a  = " + a);
        System.out.println("b  = " + b);
        System.out.println("c  = " + c);

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            // skip for this turn
            return;
        }

        // a a b b b c
        d = a.multiply(a).multiply(b).multiply(b).multiply(b).multiply(c);
        c = a.multiply(b).multiply(c);
        System.out.println("d  = " + d);
        System.out.println("c  = " + c);

        c = asqf.baseSquarefreePart(c);
        d = asqf.baseSquarefreePart(d);
        System.out.println("d  = " + d);
        System.out.println("c  = " + c);
        assertTrue("isSquarefree(c) " + c, asqf.isSquarefree(c));
        assertTrue("isSquarefree(d) " + d, asqf.isSquarefree(d));

        e = PolyUtil.<AlgebraicNumber<Quotient<ModInteger>>> basePseudoRemainder(d, c);
        System.out.println("e  = " + e);
        assertTrue("squarefree(abc) | squarefree(aabbbc) " + e, e.isZERO());
    }


    /**
     * Test base squarefree factors.
     * 
     */
    public void xtestBaseSquarefreeFactors() {

        dfac = new GenPolynomialRing<AlgebraicNumber<Quotient<ModInteger>>>(afac, 1, to, rvars);

        a = dfac.random(kl + 1, ll, el + 2, q);
        b = dfac.random(kl + 1, ll, el + 2, q);
        c = dfac.random(kl, ll, el + 1, q);
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

        SortedMap<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>, Long> sfactors;
        sfactors = asqf.baseSquarefreeFactors(d);
        //System.out.println("sfactors = " + sfactors);

        assertTrue("isFactorization(d,sfactors) ", asqf.isFactorization(d, sfactors));
    }


    /**
     * Test recursive squarefree.
     * 
     */
    public void xtestRecursiveSquarefree() {
        //System.out.println("\nrecursive:");

        cfac = new GenPolynomialRing<AlgebraicNumber<Quotient<ModInteger>>>(afac, 2 - 1, to, c1vars);
        rfac = new GenPolynomialRing<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>>(cfac, 1, to, rvars);

        ar = rfac.random(kl, 3, 2, q);
        br = rfac.random(kl, 3, 2, q);
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
        //System.out.println("dr  = " + dr);
        //System.out.println("cr  = " + cr);

        cr = asqf.recursiveUnivariateSquarefreePart(cr);
        dr = asqf.recursiveUnivariateSquarefreePart(dr);
        //System.out.println("dr  = " + dr);
        //System.out.println("cr  = " + cr);
        assertTrue("isSquarefree(cr) " + cr, asqf.isRecursiveSquarefree(cr));
        assertTrue("isSquarefree(dr) " + dr, asqf.isRecursiveSquarefree(dr));

        er = PolyUtil.<AlgebraicNumber<Quotient<ModInteger>>> recursivePseudoRemainder(dr, cr);
        //System.out.println("er  = " + er);
        assertTrue("squarefree(abc) | squarefree(aabbc) " + er, er.isZERO());
    }


    /**
     * Test recursive squarefree factors.
     * 
     */
    public void xtestRecursiveSquarefreeFactors() {

        cfac = new GenPolynomialRing<AlgebraicNumber<Quotient<ModInteger>>>(afac, 2 - 1, to, c1vars);
        rfac = new GenPolynomialRing<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>>(cfac, 1, to, rvars);

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

        SortedMap<GenPolynomial<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>>, Long> sfactors;
        sfactors = asqf.recursiveUnivariateSquarefreeFactors(dr);
        //System.out.println("sfactors = " + sfactors);

        assertTrue("isFactorization(d,sfactors) ", asqf.isRecursiveFactorization(dr, sfactors));
    }


    /**
     * Test squarefree.
     * 
     */
    public void xtestSquarefree() {
        //System.out.println("\nfull:");

        dfac = new GenPolynomialRing<AlgebraicNumber<Quotient<ModInteger>>>(afac, rl, to, vars);

        a = dfac.random(kl, ll, 2, q);
        b = dfac.random(kl, ll - 1, 2, q);
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
        //System.out.println("d  = " + d);
        //System.out.println("c  = " + c);

        c = asqf.squarefreePart(c);
        d = asqf.squarefreePart(d);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);
        assertTrue("isSquarefree(d) " + d, asqf.isSquarefree(d));
        assertTrue("isSquarefree(c) " + c, asqf.isSquarefree(c));

        e = PolyUtil.<AlgebraicNumber<Quotient<ModInteger>>> basePseudoRemainder(d, c);
        //System.out.println("e  = " + e);

        assertTrue("squarefree(abc) | squarefree(aabbc) " + e, e.isZERO());
    }


    /**
     * Test squarefree factors.
     * 
     */
    public void xtestSquarefreeFactors() {

        dfac = new GenPolynomialRing<AlgebraicNumber<Quotient<ModInteger>>>(afac, rl, to, vars);

        a = dfac.random(kl, 3, 2, q);
        b = dfac.random(kl, 2, 2, q);
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

        SortedMap<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>, Long> sfactors;
        sfactors = asqf.squarefreeFactors(d);
        //System.out.println("sfactors = " + sfactors);

        assertTrue("isFactorization(d,sfactors) ", asqf.isFactorization(d, sfactors));
    }


    /* ------------char-th root ------------------------- */


    /**
     * Test base squarefree with char-th root.
     * 
     */
    public void xtestBaseSquarefreeCharRoot() {
        //System.out.println("\nbase CharRoot:");

        long p = fac.characteristic().longValue();

        //dfac = new GenPolynomialRing<ModInteger>(fac,1,to,rvars);
        dfac = new GenPolynomialRing<AlgebraicNumber<Quotient<ModInteger>>>(afac, 1, to, rvars);

        a = dfac.random(kl + 1, ll + 1, el + 2, q).monic();
        b = dfac.random(kl, ll + 1, el + 2, q).monic();
        c = dfac.random(kl + 1, ll, el, q).monic();

        if (a.isZERO() || b.isZERO() || c.isZERO() || a.isConstant() || b.isConstant()) {
            // skip for this turn
            return;
        }
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        // a a b^p c
        d = a.multiply(a).multiply(Power.<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>> positivePower(b, p)).multiply(
                c);
        c = a.multiply(b).multiply(c);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);

        c = asqf.baseSquarefreePart(c);
        d = asqf.baseSquarefreePart(d);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);
        assertTrue("isSquarefree(c) " + c, asqf.isSquarefree(c));
        assertTrue("isSquarefree(d) " + d, asqf.isSquarefree(d));

        e = PolyUtil.<AlgebraicNumber<Quotient<ModInteger>>> basePseudoRemainder(d, c);
        //System.out.println("e  = " + e);
        assertTrue("squarefree(abc) | squarefree(aab^pc) " + e, e.isZERO());
    }


    /**
     * Test base squarefree factors with char-th root.
     * 
     */
    public void xtestBaseSquarefreeFactorsCharRoot() {

        long p = fac.characteristic().longValue();

        //dfac = new GenPolynomialRing<ModInteger>(fac,1,to,rvars);
        dfac = new GenPolynomialRing<AlgebraicNumber<Quotient<ModInteger>>>(afac, 1, to, rvars);

        a = dfac.random(kl, ll + 1, el + 3, q).monic();
        b = dfac.random(kl, ll + 1, el + 3, q).monic();
        c = dfac.random(kl, ll, el + 2, q).monic();

        if (a.isZERO() || b.isZERO() || c.isZERO() || a.isConstant() || b.isConstant()) {
            // skip for this turn
            return;
        }
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        // a a b^p c
        d = a.multiply(a).multiply(Power.<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>> positivePower(b, p)).multiply(
                c);
        //d = d.monic();
        //System.out.println("d  = " + d);

        SortedMap<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>, Long> sfactors;
        sfactors = asqf.baseSquarefreeFactors(d);
        //System.out.println("sfactors = " + sfactors);

        assertTrue("isFactorization(d,sfactors) ", asqf.isFactorization(d, sfactors));
    }


    /**
     * Test recursive squarefree with char-th root.
     * 
     */
    public void xtestRecursiveSquarefreeCharRoot() {
        //System.out.println("\nrecursive CharRoot:");

        long p = fac.characteristic().longValue();

        cfac = new GenPolynomialRing<AlgebraicNumber<Quotient<ModInteger>>>(afac, 2 - 1, to, c1vars);
        rfac = new GenPolynomialRing<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>>(cfac, 1, to, rvars);

        ar = rfac.random(kl, 3, 2 + 1, q);
        br = rfac.random(kl, 3, 2, q);
        cr = rfac.random(kl, ll, el, q);

        if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
            // skip for this turn
            return;
        }
        ar = PolyUtil.<AlgebraicNumber<Quotient<ModInteger>>> monic(ar);
        br = PolyUtil.<AlgebraicNumber<Quotient<ModInteger>>> monic(br);
        cr = PolyUtil.<AlgebraicNumber<Quotient<ModInteger>>> monic(cr);
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);
        //System.out.println("cr = " + cr);

        // a b^p c
        dr = ar.multiply(Power.<GenPolynomial<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>>> positivePower(br, p)).multiply(cr);
        cr = ar.multiply(br).multiply(cr);
        //System.out.println("cr  = " + cr);
        //System.out.println("dr  = " + dr);

        cr = asqf.recursiveUnivariateSquarefreePart(cr);
        dr = asqf.recursiveUnivariateSquarefreePart(dr);
        //System.out.println("cr  = " + cr);
        //System.out.println("dr  = " + dr);
        assertTrue("isSquarefree(cr) " + cr, asqf.isRecursiveSquarefree(cr));
        assertTrue("isSquarefree(dr) " + dr, asqf.isRecursiveSquarefree(dr));

        er = PolyUtil.<AlgebraicNumber<Quotient<ModInteger>>> recursivePseudoRemainder(dr, cr);
        //System.out.println("er  = " + er);
        assertTrue("squarefree(abc) | squarefree(aabbc) " + er, er.isZERO());
    }


    /**
     * Test recursive squarefree factors with char-th root.
     * 
     */
    public void xtestRecursiveSquarefreeFactorsCharRoot() {

        long p = fac.characteristic().longValue();

        cfac = new GenPolynomialRing<AlgebraicNumber<Quotient<ModInteger>>>(afac, 2 - 1, to, c1vars);
        rfac = new GenPolynomialRing<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>>(cfac, 1, to, rvars);

        ar = rfac.random(kl, 3, 2 + 1, q);
        br = rfac.random(kl, 3, 2, q);
        cr = rfac.random(kl, 3, 2, q);

        if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
            // skip for this turn
            return;
        }
        ar = PolyUtil.<AlgebraicNumber<Quotient<ModInteger>>> monic(ar);
        br = PolyUtil.<AlgebraicNumber<Quotient<ModInteger>>> monic(br);
        cr = PolyUtil.<AlgebraicNumber<Quotient<ModInteger>>> monic(cr);
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);
        //System.out.println("cr = " + cr);

        // a b^p c
        dr = ar.multiply(Power.<GenPolynomial<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>>> positivePower(br, p)).multiply(cr);
        //System.out.println("dr  = " + dr);

        SortedMap<GenPolynomial<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>>, Long> sfactors;
        sfactors = asqf.recursiveUnivariateSquarefreeFactors(dr);
        //System.out.println("sfactors = " + sfactors);

        assertTrue("isFactorization(d,sfactors) ", asqf.isRecursiveFactorization(dr, sfactors));
    }


    /**
     * Test squarefree with char-th root.
     * 
     */
    public void xtestSquarefreeCharRoot() {
        //System.out.println("\nfull CharRoot:");

        long p = fac.characteristic().longValue();

        dfac = new GenPolynomialRing<AlgebraicNumber<Quotient<ModInteger>>>(afac, rl, to, vars);

        a = dfac.random(kl, ll, 3, q);
        b = dfac.random(kl, 3, 2, q);
        c = dfac.random(kl, ll, 3, q);

        if (a.isZERO() || b.isZERO() || c.isZERO() || b.isConstant()) {
            // skip for this turn
            return;
        }
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        // a a b^p c
        d = a.multiply(a).multiply(Power.<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>> positivePower(b, p)).multiply(c);
        c = a.multiply(b).multiply(c);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);

        c = asqf.squarefreePart(c);
        d = asqf.squarefreePart(d);
        //System.out.println("c  = " + c);
        //System.out.println("d  = " + d);
        assertTrue("isSquarefree(d) " + d, asqf.isSquarefree(d));
        assertTrue("isSquarefree(c) " + c, asqf.isSquarefree(c));

        e = PolyUtil.<AlgebraicNumber<Quotient<ModInteger>>> basePseudoRemainder(d, c);
        //System.out.println("e  = " + e);
        assertTrue("squarefree(abc) | squarefree(aab^pc) " + e, e.isZERO());
    }


    /**
     * Test squarefree factors with char-th root.
     * 
     */
    public void xtestSquarefreeFactorsCharRoot() {

        long p = fac.characteristic().longValue();

        dfac = new GenPolynomialRing<AlgebraicNumber<Quotient<ModInteger>>>(afac, rl, to, vars);

        a = dfac.random(kl, ll, 3, q);
        b = dfac.random(kl, 3, 2, q);
        c = dfac.random(kl, ll, 3, q);

        if (a.isZERO() || b.isZERO() || c.isZERO() || b.isConstant()) {
            // skip for this turn
            return;
        }
        //System.out.println("a  = " + a);
        //System.out.println("b  = " + b);
        //System.out.println("c  = " + c);

        // a a b^p c
        d = a.multiply(a).multiply(Power.<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>> positivePower(b, p)).multiply(c);
        //System.out.println("d  = " + d);

        SortedMap<GenPolynomial<AlgebraicNumber<Quotient<ModInteger>>>, Long> sfactors;
        sfactors = asqf.squarefreeFactors(d);
        //System.out.println("sfactors = " + sfactors);

        assertTrue("isFactorization(d,sfactors) ", asqf.isFactorization(d, sfactors));
    }

}
