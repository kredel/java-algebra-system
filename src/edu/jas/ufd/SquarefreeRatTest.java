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

//import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
//import edu.jas.arith.ModInteger;
//import edu.jas.arith.ModIntegerRing;
//import edu.jas.arith.PrimeList;

import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;


/**
 * Squarefree factorization tests with JUnit.
 * @author Heinz Kredel.
 */

public class SquarefreeRatTest extends TestCase {

/**
 * main.
 */
 public static void main (String[] args) {
     //BasicConfigurator.configure();
     junit.textui.TestRunner.run( suite() );
 }


/**
 * Constructs a <CODE>SquarefreeRatTest</CODE> object.
 * @param name String.
 */
 public SquarefreeRatTest(String name) {
     super(name);
 }


/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(SquarefreeRatTest.class);
     return suite;
 }


    TermOrder to = new TermOrder( TermOrder.INVLEX );

    int rl = 3; 
    int kl = 3;
    int ll = 4;
    int el = 3;
    float q = 0.25f;

    String[] vars;
    String[] cvars;
    String[] c1vars;
    String[] rvars;

    protected void setUp() {
        vars   = ExpVector.STDVARS(rl);
        cvars  = ExpVector.STDVARS(rl-1);
        c1vars = new String[] { cvars[0] };
        rvars  = new String[] { vars[rl-1] };
    }

    protected void tearDown() {
        ComputerThreads.terminate();
    }


/**
 * Test base squarefree.
 * 
 */
 public void testBaseSquarefree() {
     System.out.println("\nbase:");

     BigRational fac = new BigRational(1);

     GreatestCommonDivisorAbstract<BigRational> ufd; 
     //ufd = new GreatestCommonDivisorSubres<BigRational>();
     //ufd = GCDFactory.<BigRational> getImplementation(fac);
     ufd = GCDFactory.<BigRational> getProxy(fac);

     SquarefreeFieldChar0<BigRational> sqf = new SquarefreeFieldChar0<BigRational>(fac);

     GenPolynomialRing<BigRational> dfac;
     dfac = new GenPolynomialRing<BigRational>(fac,1,to,rvars);

     GenPolynomial<BigRational> a;
     GenPolynomial<BigRational> b;
     GenPolynomial<BigRational> c;
     GenPolynomial<BigRational> d;
     GenPolynomial<BigRational> e;

     a = dfac.random(kl,ll,el+2,q);
     b = dfac.random(kl,ll,el+2,q);
     c = dfac.random(kl,ll,el,q);
     System.out.println("a  = " + a);
     System.out.println("b  = " + b);
     System.out.println("c  = " + c);

     if ( a.isZERO() || b.isZERO() || c.isZERO() ) {
        // skip for this turn
        return;
     }
         
     // a a b b b c
     d = a.multiply(a).multiply(b).multiply(b).multiply(b).multiply(c);
     c = a.multiply(b).multiply(c);
     System.out.println("d  = " + d);
     System.out.println("c  = " + c);

     c = sqf.baseSquarefreePart(c);
     d = sqf.baseSquarefreePart(d);
     System.out.println("d  = " + d);
     System.out.println("c  = " + c);
     assertTrue("isSquarefree(c) " + c, sqf.isSquarefree(c) );
     assertTrue("isSquarefree(d) " + d, sqf.isSquarefree(d) );

     e = PolyUtil.<BigRational>basePseudoRemainder(d,c);
     //System.out.println("e  = " + e);
     assertTrue("squarefree(abc) | squarefree(aabbbc) " + e, e.isZERO() );
 }


/**
 * Test base squarefree factors.
 * 
 */
 public void testBaseSquarefreeFactors() {

     BigRational fac = new BigRational(1);

     GreatestCommonDivisorAbstract<BigRational> ufd; 
     //ufd = new GreatestCommonDivisorSubres<BigRational>();
     ufd = GCDFactory.<BigRational> getProxy(fac);

     SquarefreeFieldChar0<BigRational> sqf = new SquarefreeFieldChar0<BigRational>(fac);

     GenPolynomialRing<BigRational> dfac;
     dfac = new GenPolynomialRing<BigRational>(fac,1,to,rvars);

     GenPolynomial<BigRational> a;
     GenPolynomial<BigRational> b;
     GenPolynomial<BigRational> c;
     GenPolynomial<BigRational> d;
     GenPolynomial<BigRational> e;

     a = dfac.random(kl,ll,el+3,q);
     b = dfac.random(kl,ll,el+3,q);
     c = dfac.random(kl,ll,el+2,q);
     System.out.println("a  = " + a);
     System.out.println("b  = " + b);
     System.out.println("c  = " + c);

     if ( a.isZERO() || b.isZERO() || c.isZERO() ) {
        // skip for this turn
        return;
     }
     
     // a a b b b c
     d = a.multiply(a).multiply(b).multiply(b).multiply(b).multiply(c);
     System.out.println("d  = " + d);

     SortedMap<GenPolynomial<BigRational>,Long> sfactors;
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

     BigRational fac = new BigRational(1);

     GreatestCommonDivisorAbstract<BigRational> ufd; 
     //ufd = new GreatestCommonDivisorSubres<BigRational>();
     ufd = GCDFactory.<BigRational> getProxy(fac);

     SquarefreeFieldChar0<BigRational> sqf = new SquarefreeFieldChar0<BigRational>(fac);

     GenPolynomialRing<BigRational> cfac;
     GenPolynomialRing<GenPolynomial<BigRational>> rfac;
     cfac = new GenPolynomialRing<BigRational>(fac,2-1,to,c1vars);
     rfac = new GenPolynomialRing<GenPolynomial<BigRational>>(cfac,1,to,rvars);

     GenPolynomial<GenPolynomial<BigRational>> ar;
     GenPolynomial<GenPolynomial<BigRational>> br;
     GenPolynomial<GenPolynomial<BigRational>> cr;
     GenPolynomial<GenPolynomial<BigRational>> dr;
     GenPolynomial<GenPolynomial<BigRational>> er;

     ar = rfac.random(kl,ll,el,q);
     br = rfac.random(kl,ll,el,q);
     cr = rfac.random(kl,ll,el,q);
     System.out.println("ar = " + ar);
     System.out.println("br = " + br);
     //System.out.println("cr = " + cr);

     if ( ar.isZERO() || br.isZERO() || cr.isZERO() ) {
        // skip for this turn
        return;
     }

     dr = ar.multiply(ar).multiply(br).multiply(br);
     cr = ar.multiply(br);
     System.out.println("dr  = " + dr);
     System.out.println("cr  = " + cr);

     cr = sqf.recursiveSquarefreePart(cr);
     dr = sqf.recursiveSquarefreePart(dr);
     System.out.println("dr  = " + dr);
     System.out.println("cr  = " + cr);
     assertTrue("isSquarefree(cr) " + cr, sqf.isRecursiveSquarefree(cr) );
     assertTrue("isSquarefree(dr) " + dr, sqf.isRecursiveSquarefree(dr) );

     er = PolyUtil.<BigRational>recursivePseudoRemainder(dr,cr);
     //System.out.println("er  = " + er);
     assertTrue("squarefree(abc) | squarefree(aabbc) " + er, er.isZERO() );
 }


/**
 * Test recursive squarefree factors.
 * 
 */
 public void testRecursiveSquarefreeFactors() {

     BigRational fac = new BigRational(1);

     GreatestCommonDivisorAbstract<BigRational> ufd; 
     //ufd = new GreatestCommonDivisorSubres<BigRational>();
     ufd = GCDFactory.<BigRational> getProxy(fac);

     SquarefreeFieldChar0<BigRational> sqf = new SquarefreeFieldChar0<BigRational>(fac);

     GenPolynomialRing<BigRational> cfac;
     GenPolynomialRing<GenPolynomial<BigRational>> rfac;
     cfac = new GenPolynomialRing<BigRational>(fac,2-1,to,c1vars);
     rfac = new GenPolynomialRing<GenPolynomial<BigRational>>(cfac,1,to,rvars);

     GenPolynomial<GenPolynomial<BigRational>> ar;
     GenPolynomial<GenPolynomial<BigRational>> br;
     GenPolynomial<GenPolynomial<BigRational>> cr;
     GenPolynomial<GenPolynomial<BigRational>> dr;
     GenPolynomial<GenPolynomial<BigRational>> er;

     ar = rfac.random(kl,3,2,q);
     br = rfac.random(kl,3,2,q);
     cr = rfac.random(kl,3,2,q);
     System.out.println("ar = " + ar);
     System.out.println("br = " + br);
     System.out.println("cr = " + cr);

     if ( ar.isZERO() || br.isZERO() || cr.isZERO() ) {
        // skip for this turn
        return;
     }
         
     dr = ar.multiply(cr).multiply(br).multiply(br);
     System.out.println("dr  = " + dr);

     SortedMap<GenPolynomial<GenPolynomial<BigRational>>,Long> sfactors;
     sfactors = sqf.recursiveSquarefreeFactors(dr);
     System.out.println("sfactors = " + sfactors);

     assertTrue("isFactorization(d,sfactors) ", sqf.isRecursiveFactorization(dr,sfactors) );
 }


/**
 * Test squarefree.
 * 
 */
 public void testSquarefree() {
     System.out.println("\nfull:");

     BigRational fac = new BigRational(1);

     GreatestCommonDivisorAbstract<BigRational> ufd; 
     //ufd = new GreatestCommonDivisorSubres<BigRational>();
     ufd = GCDFactory.<BigRational> getProxy(fac);

     SquarefreeFieldChar0<BigRational> sqf = new SquarefreeFieldChar0<BigRational>(fac);

     GenPolynomialRing<BigRational> dfac;
     dfac = new GenPolynomialRing<BigRational>(fac,rl,to,vars);

     GenPolynomial<BigRational> a;
     GenPolynomial<BigRational> b;
     GenPolynomial<BigRational> c;
     GenPolynomial<BigRational> d;
     GenPolynomial<BigRational> e;

     a = dfac.random(kl,ll,2,q);
     b = dfac.random(kl,ll,2,q);
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
     System.out.println("d  = " + d);
     System.out.println("c  = " + c);

     c = sqf.squarefreePart(c); 
     d = sqf.squarefreePart(d);
     System.out.println("c  = " + c);
     System.out.println("d  = " + d);
     assertTrue("isSquarefree(d) " + d, sqf.isSquarefree(d) );
     assertTrue("isSquarefree(c) " + c, sqf.isSquarefree(c) );

     e = PolyUtil.<BigRational>basePseudoRemainder(d,c);
     //System.out.println("e  = " + e);

     assertTrue("squarefree(abc) | squarefree(aabbc) " + e, e.isZERO() );
 }


/**
 * Test squarefree factors.
 * 
 */
 public void testSquarefreeFactors() {

     BigRational fac = new BigRational(1);

     GreatestCommonDivisorAbstract<BigRational> ufd; 
     //ufd = new GreatestCommonDivisorSubres<BigRational>();
     ufd = GCDFactory.<BigRational> getProxy(fac);

     SquarefreeFieldChar0<BigRational> sqf = new SquarefreeFieldChar0<BigRational>(fac);

     GenPolynomialRing<BigRational> dfac;
     dfac = new GenPolynomialRing<BigRational>(fac,rl,to,vars);

     GenPolynomial<BigRational> a;
     GenPolynomial<BigRational> b;
     GenPolynomial<BigRational> c;
     GenPolynomial<BigRational> d;
     GenPolynomial<BigRational> e;

     a = dfac.random(kl,3,2,q);
     b = dfac.random(kl,3,2,q);
     c = dfac.random(kl,3,2,q);
     System.out.println("a  = " + a);
     System.out.println("b  = " + b);
     System.out.println("c  = " + c);

     if ( a.isZERO() || b.isZERO() || c.isZERO() ) {
        // skip for this turn
        return;
     }
         
     d = a.multiply(a).multiply(b).multiply(b).multiply(b).multiply(c);
     System.out.println("d  = " + d);

     SortedMap<GenPolynomial<BigRational>,Long> sfactors;
     sfactors = sqf.squarefreeFactors(d);
     System.out.println("sfactors = " + sfactors);

     assertTrue("isFactorization(d,sfactors) ", sqf.isFactorization(d,sfactors) );
 }

}
