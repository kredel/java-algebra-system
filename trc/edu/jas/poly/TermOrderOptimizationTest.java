/*
 * $Id$
 */

package edu.jas.poly;

import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;


/**
 * TermOrderOptimization tests with JUnit.
 * @author Heinz Kredel
 */
public class TermOrderOptimizationTest extends TestCase {


    /**
     * main.
     */
    public static void main (String[] args) {
        junit.textui.TestRunner.run( suite() );
    }


    /**
     * Constructs a <CODE>TermOrderOptimizationTest</CODE> object.
     * @param name String.
     */
    public TermOrderOptimizationTest(String name) {
        super(name);
    }


    /**
     * suite.
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(TermOrderOptimizationTest.class);
        return suite;
    }


    int rl = 7; 
    int kl = 3;
    int ll = 10;
    int el = 7;
    float q = 0.5f;


    GenPolynomialRing<BigRational> fac;

    GenPolynomial<BigRational> a, b, c, d, e;

    GenPolynomialRing<GenPolynomial<BigRational>> rfac;

    GenPolynomial<GenPolynomial<BigRational>> ar, br, cr, dr, er;


    protected void setUp() {
    }


    protected void tearDown() {
    }


    /**
     * Test permutations.
     */
    public void testPermutation() {
        List<Integer> P = new ArrayList<Integer>(); 
        P.add(2);
        P.add(1);
        P.add(4);
        P.add(0);
        P.add(3);
        //System.out.println("P = " + P);

        List<Integer> S = TermOrderOptimization.inversePermutation(P); 
        //System.out.println("S = " + S);
        assertFalse("P != id", TermOrderOptimization.isIdentityPermutation(P));
        assertFalse("S != id", TermOrderOptimization.isIdentityPermutation(S));

        List<Integer> T = TermOrderOptimization.multiplyPermutation(P,S); 
        //System.out.println("T = " + T);
        List<Integer> U = TermOrderOptimization.multiplyPermutation(S,P); 
        //System.out.println("U = " + U);

        assertTrue("T == id", TermOrderOptimization.isIdentityPermutation(T));
        assertTrue("U == id", TermOrderOptimization.isIdentityPermutation(U));
    }


    /**
     * Test polynomial optimization.
     */
    public void testPolyOptimization() {
        BigRational cf = new BigRational();    
        fac = new GenPolynomialRing<BigRational>(cf,rl);
        //System.out.println("fac = " + fac);

        a = fac.random(kl,ll,el,q);
        b = fac.random(kl,ll,el,q);
        c = fac.random(kl,ll,el,q);
        d = fac.random(kl,ll,el,q);
        e = fac.random(kl,ll,el,q);

        List<GenPolynomial<BigRational>> F = new ArrayList<GenPolynomial<BigRational>>();
        F.add(a);
        F.add(b);
        F.add(c);
        F.add(d);
        F.add(e);
        //System.out.println("F = " + F);

        OptimizedPolynomialList<BigRational> Fo, Fo2;
        Fo = TermOrderOptimization.<BigRational> optimizeTermOrder(fac,F); 
        //System.out.println("Fo = " + Fo.perm);

        Fo2 = TermOrderOptimization.<BigRational> optimizeTermOrder(Fo.ring,Fo.list);
        //System.out.println("Fo2 = " + Fo2.perm);
        assertEquals("Fo == Fo2: ", Fo, Fo2);

        List<Integer> S = TermOrderOptimization.inversePermutation(Fo.perm); 

        GenPolynomialRing<BigRational> faci = Fo.ring.permutation(S);
        //System.out.println("faci = " + faci);
        List<GenPolynomial<BigRational>> Fi = TermOrderOptimization.permutation(S,faci,Fo.list);

        //System.out.println("Fi = " + Fi);
        //System.out.println("Fi = " + Fi);
        assertEquals("r == ri: ", fac, faci);
        assertEquals("F == Fi: ", F, Fi);
    }


    /**
     * Test polynomial coefficients optimization.
     */
    public void testPolyCoefOptimization() {
        BigRational cf = new BigRational();    
        fac = new GenPolynomialRing<BigRational>(cf,rl);
        //System.out.println("fac = " + fac);

        rfac = new GenPolynomialRing<GenPolynomial<BigRational>>(fac,3);
        //System.out.println("rfac = " + rfac);

        ar = rfac.random(kl,ll,el,q);
        br = rfac.random(kl,ll,el,q);
        cr = rfac.random(kl,ll,el,q);
        dr = rfac.random(kl,ll,el,q);
        er = rfac.random(kl,ll,el,q);

        List<GenPolynomial<GenPolynomial<BigRational>>> F = new ArrayList<GenPolynomial<GenPolynomial<BigRational>>>();
        F.add(ar);
        F.add(br);
        F.add(cr);
        F.add(dr);
        F.add(er);
        //System.out.println("F = " + F);

        OptimizedPolynomialList<GenPolynomial<BigRational>> Fo, Fo2;
        Fo = TermOrderOptimization.<BigRational> optimizeTermOrderOnCoefficients(rfac,F); 
        //System.out.println("Fo = " + Fo.perm);

        Fo2 = TermOrderOptimization.<BigRational> optimizeTermOrderOnCoefficients(Fo.ring,Fo.list);
        //System.out.println("Fo2 = " + Fo2.perm);
        assertEquals("Fo == Fo2: ", Fo, Fo2);

        List<Integer> S = TermOrderOptimization.inversePermutation(Fo.perm); 

        GenPolynomialRing<BigRational> cof = (GenPolynomialRing<BigRational>) Fo.ring.coFac;
        cof = cof.permutation(S);

        GenPolynomialRing<GenPolynomial<BigRational>> faci = new GenPolynomialRing<GenPolynomial<BigRational>>(cof,rfac);
        //System.out.println("faci = " + faci);
        List<GenPolynomial<GenPolynomial<BigRational>>> Fi = TermOrderOptimization.permutationOnCoefficients(S,faci,Fo.list);

        //System.out.println("Fi = " + Fi);
        //System.out.println("Fi = " + Fi);
        assertEquals("r == ri: ", rfac, faci);
        assertEquals("F == Fi: ", F, Fi);
    }

}
