/*
 * $Id$
 */

package edu.jas.ufd;

import java.util.Map;
import java.util.SortedMap;
import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.kern.ComputerThreads;

import edu.jas.structure.Power;

//import edu.jas.arith.BigInteger;
//import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
//import edu.jas.arith.PrimeList;

import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;

//import edu.jas.poly.AlgebraicNumber;
//import edu.jas.poly.AlgebraicNumberRing;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;


/**
 * Squarefree factorization tests with JUnit.
 * @author Heinz Kredel.
 */

public class SquarefreeQuotModTest extends TestCase {

/**
 * main.
 */
 public static void main (String[] args) {
     //BasicConfigurator.configure();
     junit.textui.TestRunner.run( suite() );
     ComputerThreads.terminate();
 }


/**
 * Constructs a <CODE>SquarefreeQuotModTest</CODE> object.
 * @param name String.
 */
 public SquarefreeQuotModTest(String name) {
     super(name);
 }


/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(SquarefreeQuotModTest.class);
     return suite;
 }


    TermOrder to = new TermOrder( TermOrder.INVLEX );

    int rl = 3; 
    int kl = 1;
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
    QuotientRing<ModInteger> fac;

    GreatestCommonDivisorAbstract<Quotient<ModInteger>> ufd; 

    SquarefreeInfiniteFieldCharP<ModInteger> sqf;

    GenPolynomialRing<Quotient<ModInteger>> dfac;


    GenPolynomial<Quotient<ModInteger>> a;
    GenPolynomial<Quotient<ModInteger>> b;
    GenPolynomial<Quotient<ModInteger>> c;
    GenPolynomial<Quotient<ModInteger>> d;
    GenPolynomial<Quotient<ModInteger>> e;

    GenPolynomialRing<Quotient<ModInteger>> cfac;
    GenPolynomialRing<GenPolynomial<Quotient<ModInteger>>> rfac;

    GenPolynomial<GenPolynomial<Quotient<ModInteger>>> ar;
    GenPolynomial<GenPolynomial<Quotient<ModInteger>>> br;
    GenPolynomial<GenPolynomial<Quotient<ModInteger>>> cr;
    GenPolynomial<GenPolynomial<Quotient<ModInteger>>> dr;
    GenPolynomial<GenPolynomial<Quotient<ModInteger>>> er;


    protected void setUp() {
        vars   = ExpVector.STDVARS(rl);
        cvars  = ExpVector.STDVARS(rl-1);
        c1vars = new String[] { cvars[0] };
        rvars  = new String[] { vars[rl-1] };

        mfac = new ModIntegerRing(7);
        alpha = new String[] { "u" };
        mpfac = new GenPolynomialRing<ModInteger>(mfac, 1, to, alpha);
        fac = new QuotientRing<ModInteger>(mpfac);

        //ufd = new GreatestCommonDivisorSubres<Quotient<ModInteger>>();
        //ufd = GCDFactory.<Quotient<ModInteger>> getImplementation(fac);
        ufd = GCDFactory.<Quotient<ModInteger>> getProxy(fac);

        sqf = new SquarefreeInfiniteFieldCharP<ModInteger>(fac);
        a = b = c = d = e = null;
        ar = br = cr = dr = er = null;
    }

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
     System.out.println("\nbase:");

     dfac = new GenPolynomialRing<Quotient<ModInteger>>(fac,1,to,rvars);

     a = dfac.random(kl+1,ll,el+2,q);
     b = dfac.random(kl+1,ll,el+2,q);
     c = dfac.random(kl,ll,el,q);
     //System.out.println("a  = " + a);
     //System.out.println("b  = " + b);
     //System.out.println("c  = " + c);

     if ( a.isZERO() || b.isZERO() || c.isZERO() ) {
        // skip for this turn
        return;
     }
         
     // a a b b b c
     d = a.multiply(a).multiply(b).multiply(b).multiply(b).multiply(c);
     c = a.multiply(b).multiply(c);
     //System.out.println("d  = " + d);
     //System.out.println("c  = " + c);

     c = sqf.baseSquarefreePart(c);
     d = sqf.baseSquarefreePart(d);
     System.out.println("d  = " + d);
     System.out.println("c  = " + c);
     assertTrue("isSquarefree(c) " + c, sqf.isSquarefree(c) );
     assertTrue("isSquarefree(d) " + d, sqf.isSquarefree(d) );

     e = PolyUtil.<Quotient<ModInteger>>basePseudoRemainder(d,c);
     //System.out.println("e  = " + e);
     assertTrue("squarefree(abc) | squarefree(aabbbc) " + e, e.isZERO() );
 }


/**
 * Test base squarefree factors.
 * 
 */
 public void testBaseSquarefreeFactors() {

     dfac = new GenPolynomialRing<Quotient<ModInteger>>(fac,1,to,rvars);

     a = dfac.random(kl+1,ll,el+3,q);
     b = dfac.random(kl+1,ll,el+3,q);
     c = dfac.random(kl,ll,el+2,q);
     //System.out.println("a  = " + a);
     //System.out.println("b  = " + b);
     //System.out.println("c  = " + c);

     if ( a.isZERO() || b.isZERO() || c.isZERO() ) {
        // skip for this turn
        return;
     }
     
     // a a b b b c
     d = a.multiply(a).multiply(b).multiply(b).multiply(b).multiply(c);
     //System.out.println("d  = " + d);

     SortedMap<GenPolynomial<Quotient<ModInteger>>,Long> sfactors;
     sfactors = sqf.baseSquarefreeFactors(d);
     System.out.println("sfactors = " + sfactors);

     assertTrue("isFactorization(d,sfactors) ", sqf.isFactorization(d,sfactors) );
 }


/**
 * Test recursive squarefree.
 * 
 */
 public void testRecursiveSquarefree() {
     System.out.println("\nrecursive:");

     cfac = new GenPolynomialRing<Quotient<ModInteger>>(fac,2-1,to,c1vars);
     rfac = new GenPolynomialRing<GenPolynomial<Quotient<ModInteger>>>(cfac,1,to,rvars);

     ar = rfac.random(kl,3,2,q);
     br = rfac.random(kl,3,2,q);
     cr = rfac.random(kl,ll,el,q);
     //System.out.println("ar = " + ar);
     //System.out.println("br = " + br);
     //System.out.println("cr = " + cr);

     if ( ar.isZERO() || br.isZERO() || cr.isZERO() ) {
        // skip for this turn
        return;
     }

     dr = ar.multiply(ar).multiply(br).multiply(br);
     cr = ar.multiply(br);
     //System.out.println("dr  = " + dr);
     //System.out.println("cr  = " + cr);

     cr = sqf.recursiveUnivariateSquarefreePart(cr);
     dr = sqf.recursiveUnivariateSquarefreePart(dr);
     System.out.println("dr  = " + dr);
     System.out.println("cr  = " + cr);
     assertTrue("isSquarefree(cr) " + cr, sqf.isRecursiveSquarefree(cr) );
     assertTrue("isSquarefree(dr) " + dr, sqf.isRecursiveSquarefree(dr) );

     er = PolyUtil.<Quotient<ModInteger>>recursivePseudoRemainder(dr,cr);
     //System.out.println("er  = " + er);
     assertTrue("squarefree(abc) | squarefree(aabbc) " + er, er.isZERO() );
 }


/**
 * Test recursive squarefree factors.
 * 
 */
 public void testRecursiveSquarefreeFactors() {

     cfac = new GenPolynomialRing<Quotient<ModInteger>>(fac,2-1,to,c1vars);
     rfac = new GenPolynomialRing<GenPolynomial<Quotient<ModInteger>>>(cfac,1,to,rvars);

     ar = rfac.random(kl,3,2,q);
     br = rfac.random(kl,3,2,q);
     cr = rfac.random(kl,3,2,q);
     //System.out.println("ar = " + ar);
     //System.out.println("br = " + br);
     //System.out.println("cr = " + cr);

     if ( ar.isZERO() || br.isZERO() || cr.isZERO() ) {
        // skip for this turn
        return;
     }
         
     dr = ar.multiply(cr).multiply(br).multiply(br);
     //System.out.println("dr  = " + dr);

     SortedMap<GenPolynomial<GenPolynomial<Quotient<ModInteger>>>,Long> sfactors;
     sfactors = sqf.recursiveUnivariateSquarefreeFactors(dr);
     System.out.println("sfactors = " + sfactors);

     assertTrue("isFactorization(d,sfactors) ", sqf.isRecursiveFactorization(dr,sfactors) );
 }


/**
 * Test squarefree.
 * 
 */
 public void testSquarefree() {
     System.out.println("\nfull:");

     dfac = new GenPolynomialRing<Quotient<ModInteger>>(fac,rl,to,vars);

     a = dfac.random(kl,ll,2,q);
     b = dfac.random(kl,ll-1,2,q);
     c = dfac.random(kl,ll,2,q);
     System.out.println("a  = " + a);
     System.out.println("b  = " + b);
     System.out.println("c  = " + c);

     if ( a.isZERO() || b.isZERO() || c.isZERO() ) {
        // skip for this turn
        return;
     }
         
     d = a.multiply(a).multiply(b).multiply(b).multiply(c);
     c = a.multiply(b).multiply(c);
     //System.out.println("d  = " + d);
     //System.out.println("c  = " + c);

     c = sqf.squarefreePart(c); 
     d = sqf.squarefreePart(d);
     System.out.println("c  = " + c);
     System.out.println("d  = " + d);
     assertTrue("isSquarefree(d) " + d, sqf.isSquarefree(d) );
     assertTrue("isSquarefree(c) " + c, sqf.isSquarefree(c) );

     e = PolyUtil.<Quotient<ModInteger>>basePseudoRemainder(d,c);
     //System.out.println("e  = " + e);

     assertTrue("squarefree(abc) | squarefree(aabbc) " + e, e.isZERO() );
 }


/**
 * Test squarefree factors.
 * 
 */
 public void testSquarefreeFactors() {

     dfac = new GenPolynomialRing<Quotient<ModInteger>>(fac,rl,to,vars);

     a = dfac.random(kl,3,2,q);
     b = dfac.random(kl,2,2,q);
     c = dfac.random(kl,3,2,q);
     //System.out.println("a  = " + a);
     //System.out.println("b  = " + b);
     //System.out.println("c  = " + c);

     if ( a.isZERO() || b.isZERO() || c.isZERO() ) {
        // skip for this turn
        return;
     }
         
     d = a.multiply(a).multiply(b).multiply(b).multiply(b).multiply(c);
     //System.out.println("d  = " + d);

     SortedMap<GenPolynomial<Quotient<ModInteger>>,Long> sfactors;
     sfactors = sqf.squarefreeFactors(d);
     System.out.println("sfactors = " + sfactors);

     assertTrue("isFactorization(d,sfactors) ", sqf.isFactorization(d,sfactors) );
 }


    /* ------------char-th root ------------------------- */


/**
 * Test base squarefree with char-th root.
 * 
 */
 public void testBaseSquarefreeCharRoot() {
     System.out.println("\nbase CharRoot:");

     long p = fac.characteristic().longValue();

     //dfac = new GenPolynomialRing<ModInteger>(fac,1,to,rvars);
     dfac = new GenPolynomialRing<Quotient<ModInteger>>(fac,1,to,rvars);

     a = dfac.random(kl+1,ll+1,el+2,q).monic();
     b = dfac.random(kl,ll+1,el+2,q).monic();
     c = dfac.random(kl+1,ll,el,q).monic();

     if ( a.isZERO() || b.isZERO() || c.isZERO() || a.isConstant() || b.isConstant() ) {
        // skip for this turn
        return;
     }
     System.out.println("a  = " + a);
     System.out.println("b  = " + b);
     System.out.println("c  = " + c);
         
     // a a b^p c
     d = a.multiply(a).multiply( Power.<GenPolynomial<Quotient<ModInteger>>> positivePower(b, p) ).multiply(c);
     c = a.multiply(b).multiply(c);
     System.out.println("c  = " + c);
     System.out.println("d  = " + d);

     c = sqf.baseSquarefreePart(c);
     d = sqf.baseSquarefreePart(d);
     System.out.println("c  = " + c);
     System.out.println("d  = " + d);
     assertTrue("isSquarefree(c) " + c, sqf.isSquarefree(c) );
     assertTrue("isSquarefree(d) " + d, sqf.isSquarefree(d) );

     e = PolyUtil.<Quotient<ModInteger>>basePseudoRemainder(d,c);
     System.out.println("e  = " + e);
     assertTrue("squarefree(abc) | squarefree(aab^pc) " + e, e.isZERO() );
 }


/**
 * Test base squarefree factors with char-th root.
 * 
 */
 public void testBaseSquarefreeFactorsCharRoot() {

     long p = fac.characteristic().longValue();

     //dfac = new GenPolynomialRing<ModInteger>(fac,1,to,rvars);
     dfac = new GenPolynomialRing<Quotient<ModInteger>>(fac,1,to,rvars);

     a = dfac.random(kl+1,ll+1,el+3,q).monic();
     b = dfac.random(kl+1,ll+1,el+3,q).monic();
     c = dfac.random(kl+1,ll,el+2,q).monic();

     if ( a.isZERO() || b.isZERO() || c.isZERO() || a.isConstant() || b.isConstant() ) {
        // skip for this turn
        return;
     }
     System.out.println("a  = " + a);
     System.out.println("b  = " + b);
     System.out.println("c  = " + c);
     
     // a a b^p c
     d = a.multiply(a).multiply( Power.<GenPolynomial<Quotient<ModInteger>>> positivePower(b, p) ).multiply(c);
     //d = d.monic();
     System.out.println("d  = " + d);

     SortedMap<GenPolynomial<Quotient<ModInteger>>,Long> sfactors;
     sfactors = sqf.baseSquarefreeFactors(d);
     System.out.println("sfactors = " + sfactors);

     assertTrue("isFactorization(d,sfactors) ", sqf.isFactorization(d,sfactors) );
 }


/**
 * Test recursive squarefree with char-th root.
 * 
 */
 public void testRecursiveSquarefreeCharRoot() {
     System.out.println("\nrecursive CharRoot:");

     long p = fac.characteristic().longValue();

     cfac = new GenPolynomialRing<Quotient<ModInteger>>(fac,2-1,to,c1vars);
     rfac = new GenPolynomialRing<GenPolynomial<Quotient<ModInteger>>>(cfac,1,to,rvars);

     ar = rfac.random(kl+1,3,2+1,q);
     br = rfac.random(kl,3,2,q);
     cr = rfac.random(kl,ll,el,q);

     if ( ar.isZERO() || br.isZERO() || cr.isZERO() ) {
        // skip for this turn
        return;
     }
     ar = PolyUtil.<Quotient<ModInteger>>monic(ar);
     br = PolyUtil.<Quotient<ModInteger>>monic(br);
     cr = PolyUtil.<Quotient<ModInteger>>monic(cr);
     System.out.println("ar = " + ar);
     System.out.println("br = " + br);
     System.out.println("cr = " + cr);

     // a b^p c
     dr = ar.multiply( Power.<GenPolynomial<GenPolynomial<Quotient<ModInteger>>>> positivePower(br, p) ).multiply(cr);
     cr = ar.multiply(br).multiply(cr);
     System.out.println("cr  = " + cr);
     System.out.println("dr  = " + dr);

     cr = sqf.recursiveUnivariateSquarefreePart(cr);
     dr = sqf.recursiveUnivariateSquarefreePart(dr);
     System.out.println("cr  = " + cr);
     System.out.println("dr  = " + dr);
     assertTrue("isSquarefree(cr) " + cr, sqf.isRecursiveSquarefree(cr) );
     assertTrue("isSquarefree(dr) " + dr, sqf.isRecursiveSquarefree(dr) );

     er = PolyUtil.<Quotient<ModInteger>>recursivePseudoRemainder(dr,cr);
     System.out.println("er  = " + er);
     assertTrue("squarefree(abc) | squarefree(aabbc) " + er, er.isZERO() );
 }


/**
 * Test recursive squarefree factors with char-th root.
 * 
 */
 public void testRecursiveSquarefreeFactorsCharRoot() {

     long p = fac.characteristic().longValue();

     cfac = new GenPolynomialRing<Quotient<ModInteger>>(fac,2-1,to,c1vars);
     rfac = new GenPolynomialRing<GenPolynomial<Quotient<ModInteger>>>(cfac,1,to,rvars);

     ar = rfac.random(kl+1,3,2+1,q);
     br = rfac.random(kl,3,2,q);
     cr = rfac.random(kl,3,2,q);

     if ( ar.isZERO() || br.isZERO() || cr.isZERO() ) {
        // skip for this turn
        return;
     }
     ar = PolyUtil.<Quotient<ModInteger>>monic(ar);
     br = PolyUtil.<Quotient<ModInteger>>monic(br);
     cr = PolyUtil.<Quotient<ModInteger>>monic(cr);
     System.out.println("ar = " + ar);
     System.out.println("br = " + br);
     System.out.println("cr = " + cr);

     // a b^p c
     dr = ar.multiply( Power.<GenPolynomial<GenPolynomial<Quotient<ModInteger>>>> positivePower(br, p) ).multiply(cr);
     //System.out.println("dr  = " + dr);

     SortedMap<GenPolynomial<GenPolynomial<Quotient<ModInteger>>>,Long> sfactors;
     sfactors = sqf.recursiveUnivariateSquarefreeFactors(dr);
     System.out.println("sfactors = " + sfactors);

     assertTrue("isFactorization(d,sfactors) ", sqf.isRecursiveFactorization(dr,sfactors) );
 }


/**
 * Test squarefree with char-th root.
 * 
 */
 public void testSquarefreeCharRoot() {
     System.out.println("\nfull CharRoot:");

     long p = fac.characteristic().longValue();

     dfac = new GenPolynomialRing<Quotient<ModInteger>>(fac,rl,to,vars);

     a = dfac.random(kl,ll,3,q);
     b = dfac.random(kl,3,2+1,q);
     c = dfac.random(kl,ll,3,q);

     if ( a.isZERO() || b.isZERO() || c.isZERO() || b.isConstant() ) {
        // skip for this turn
        return;
     }
     System.out.println("a  = " + a);
     System.out.println("b  = " + b);
     System.out.println("c  = " + c);
         
     // a a b^p c
     d = a.multiply(a).multiply( Power.<GenPolynomial<Quotient<ModInteger>>> positivePower(b, p) ).multiply(c);
     c = a.multiply(b).multiply(c);
     System.out.println("c  = " + c);
     System.out.println("d  = " + d);

     c = sqf.squarefreePart(c); 
     d = sqf.squarefreePart(d);
     System.out.println("c  = " + c);
     System.out.println("d  = " + d);
     assertTrue("isSquarefree(d) " + d, sqf.isSquarefree(d) );
     assertTrue("isSquarefree(c) " + c, sqf.isSquarefree(c) );

     e = PolyUtil.<Quotient<ModInteger>>basePseudoRemainder(d,c);
     System.out.println("e  = " + e);
     assertTrue("squarefree(abc) | squarefree(aab^pc) " + e, e.isZERO() );
 }


/**
 * Test squarefree factors with char-th root.
 * 
 */
 public void testSquarefreeFactorsCharRoot() {

     long p = fac.characteristic().longValue();

     dfac = new GenPolynomialRing<Quotient<ModInteger>>(fac,rl,to,vars);

     a = dfac.random(kl,ll,3,q);
     b = dfac.random(kl,3,2+1,q);
     c = dfac.random(kl,ll,3,q);

     if ( a.isZERO() || b.isZERO() || c.isZERO() || b.isConstant() ) {
        // skip for this turn
        return;
     }
     System.out.println("a  = " + a);
     System.out.println("b  = " + b);
     System.out.println("c  = " + c);
         
     // a a b^p c
     d = a.multiply(a).multiply( Power.<GenPolynomial<Quotient<ModInteger>>> positivePower(b, p) ).multiply(c);
     System.out.println("d  = " + d);

     SortedMap<GenPolynomial<Quotient<ModInteger>>,Long> sfactors;
     sfactors = sqf.squarefreeFactors(d);
     System.out.println("sfactors = " + sfactors);

     assertTrue("isFactorization(d,sfactors) ", sqf.isFactorization(d,sfactors) );
 }

}

