/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigInteger;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;
import edu.jas.kern.ComputerThreads;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenMatrixRing;
import edu.jas.vector.BasicLinAlg;
import edu.jas.vector.LinAlg;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * GenMatrix tests with JUnit
 * @author Heinz Kredel
 * @see edu.jas.vector.GenMatrixTest
 */

public class GenMatrixFFTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GenMatrixFFTest</CODE> object.
     * @param name String.
     */
    public GenMatrixFFTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GenMatrixFFTest.class);
        return suite;
    }


    int rl = 5;


    int kl = 10;


    int ll = 10;


    float q = 0.5f;


    int rows = 3 + 20;


    int cols = 3 + 20;


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
    }


    /**
     * Test fraction free GE.
     */
    public void testFractionfreeGE() {
        BigInteger cfac = new BigInteger(1);
        int n = 4;
        int m = 5;
        GenMatrixRing<BigInteger> mfac = new GenMatrixRing<BigInteger>(cfac, n, m);//rows, cols);
        //GenVectorModul<BigInteger> vfac = new GenVectorModul<BigInteger>(cfac, m);//rows);

        GenMatrix<BigInteger> A, Ap, iA, AiA;
        //A = mfac.random(kl, 0.7f);
        A = mfac.parse("[ [3,4,-2,1,-2], [1,-1,2,2,7], [4,-3,4,-3,2], [-1,1,6,-1,1] ]");
        //System.out.println("A = " + A);
        if (A.isZERO()) {
            return;
        }
        assertTrue(" not isZERO( A )", !A.isZERO());
        Ap = A.copy();

        LinAlg<BigInteger> lu = new LinAlg<BigInteger>();
        BasicLinAlg<BigInteger> blas = new BasicLinAlg<BigInteger>();

        List<Integer> P = lu.fractionfreeGaussElimination(A);
        //System.out.println("P = " + P);
        if (P.size() == 0) {
            //System.out.println("undecomposable");
            return;
        }
        assertTrue("#P != 0: ", P.size() > 0);
        //System.out.println("A = " + A);

        n = 10;
        m = n;
        mfac = new GenMatrixRing<BigInteger>(cfac, n, m);//rows, cols);

        A = mfac.random(5, 0.67f);
        //System.out.println("A = " + A);
        if (A.isZERO()) {
            return;
        }
        assertTrue(" not isZERO( A )", !A.isZERO());
        Ap = A.copy();

        P = lu.fractionfreeGaussElimination(A);
        //System.out.println("P = " + P);
        if (P.size() == 0) {
            //System.out.println("undecomposable");
            return;
        }
        assertTrue("#P != 0: ", P.size() > 0);
        //System.out.println("A = " + A);
    }


    /**
     * Test fraction free GE polynomial.
     */
    public void testFractionfreeGEpoly() {
        BigRational cfac = new BigRational(1);
        int n = 4;
        int m = n;
        String[] vars = new String[]{ "a1", "a2", "a3", "a4" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, vars);
        GenMatrixRing<GenPolynomial<BigRational>> mfac = new GenMatrixRing<GenPolynomial<BigRational>>(pfac, n, m);

        GenMatrix<GenPolynomial<BigRational>> A, Ap, iA, AiA;
        A = mfac.random(4, 0.5f);
        //A = mfac.parse("[ [3,4,-2,1,-2], [1,-1,2,2,7], [4,-3,4,-3,2], [-1,1,6,-1,1] ]");
        //System.out.println("A = " + A);
        if (A.isZERO()) {
            return;
        }
        assertTrue(" not isZERO( A )", !A.isZERO());
        Ap = A.copy();

        LinAlg<GenPolynomial<BigRational>> lu = new LinAlg<GenPolynomial<BigRational>>();

        List<Integer> P = lu.fractionfreeGaussElimination(A);
        //System.out.println("P = " + P);
        if (P.size() == 0) {
            //System.out.println("undecomposable");
            return;
        }
        assertTrue("#P != 0: ", P.size() > 0);
        //System.out.println("A = " + A);


        vars = new String[]{ "a11", "a12", "a13", "a14", "a21", "a22", "a23", "a24", "a31", "a32", "a33", "a34", "a41", "a42", "a43", "a44" };
        pfac = new GenPolynomialRing<BigRational>(cfac, vars);
        mfac = new GenMatrixRing<GenPolynomial<BigRational>>(pfac, n, m);
        A = mfac.parse("[ [a11, a12, a13, a14], [a21, a22, a23, a24], [a31, a32, a33, a34], [a41, a42, a43, a44]] ");
        //System.out.println("A = " + A);
        if (A.isZERO()) {
            return;
        }
        assertTrue(" not isZERO( A )", !A.isZERO());
        Ap = A.copy();

        P = lu.fractionfreeGaussElimination(A);
        //System.out.println("P = " + P);
        if (P.size() == 0) {
            //System.out.println("undecomposable");
            return;
        }
        assertTrue("#P != 0: ", P.size() > 0);
        //System.out.println("A = " + A);

        FactorAbstract<BigRational> ufd = FactorFactory.getImplementation(cfac);
        int i = 0;
        for (ArrayList<GenPolynomial<BigRational>> row : A.matrix) {
             int j = 0;
             for (GenPolynomial<BigRational> elem : row) {
                 if (elem.isZERO()) {
                     j++;
                     continue;
                 }
                 SortedMap<GenPolynomial<BigRational>, Long> pf = ufd.factors(elem);
                 //System.out.println("A(" + i + "," + j + ") = " + elem);
                 //System.out.println("factors = " + pf);
                 assertTrue("#factors == 1:", pf.size() == 1);
                 j++;
             }
             i++;
        }
        ComputerThreads.terminate();
    }

}
