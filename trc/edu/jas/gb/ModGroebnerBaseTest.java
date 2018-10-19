/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ModuleList;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;


/**
 * ModGroebnerBase sequential and parallel tests with JUnit.
 * @author Heinz Kredel
 */

public class ModGroebnerBaseTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>ModGroebnerBaseTest</CODE> object.
     * @param name String.
     */
    public ModGroebnerBaseTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ModGroebnerBaseTest.class);
        return suite;
    }


    GenPolynomialRing<BigRational> fac;


    PolynomialList<BigRational> F;


    List<GenPolynomial<BigRational>> G;


    GenPolynomial<BigRational> a, b, c, d, e;


    TermOrder tord;


    List<List<GenPolynomial<BigRational>>> L;


    List<GenPolynomial<BigRational>> V;


    ModuleList<BigRational> M, N, K, I;


    GroebnerBaseAbstract<BigRational> mbb;


    ReductionAbstract<BigRational> red;


    int rl = 3; //4; //3; 


    int kl = 7;


    int ll = 5;


    int el = 2;


    float q = 0.2f; //0.4f


    BigRational coeff;


    @Override
    protected void setUp() {
        coeff = new BigRational();
        tord = TermOrderByName.DEFAULT; // INVLEX
        fac = new GenPolynomialRing<BigRational>(coeff, rl, tord);
        mbb = new GroebnerBaseSeq<BigRational>(); //coeff);
        red = new ReductionSeq<BigRational>();
        a = b = c = d = e = null;

        do {
            a = fac.random(kl, ll, el, q);
            b = fac.random(kl, ll, el, q);
            c = fac.random(kl, ll, el, q);
            d = fac.random(kl, ll, el, q);
        } while (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO());
        e = d; //fac.random(kl, ll, el, q );
    }


    @Override
    protected void tearDown() {
        mbb.terminate();
        mbb = null;
        red = null;
        a = b = c = d = e = null;
        fac = null;
        tord = null;
    }


    /**
     * Test sequential GBase.
     */
    public void testSequentialModGB() {
        L = new ArrayList<List<GenPolynomial<BigRational>>>();
        V = new ArrayList<GenPolynomial<BigRational>>();
        V.add(a);
        V.add(fac.getZERO());
        V.add(fac.getONE());
        L.add(V);
        M = new ModuleList<BigRational>(fac, L);
        assertTrue("isGB( { (a,0,1) } )", mbb.isGB(M));

        N = mbb.GB(M);
        assertTrue("is( { (a,0,1) } )", mbb.isGB(N));

        V = new ArrayList<GenPolynomial<BigRational>>();
        V.add(b);
        V.add(fac.getONE());
        V.add(fac.getZERO());
        L.add(V);
        M = new ModuleList<BigRational>(fac, L);
        //System.out.println("L = " + L.size() );

        N = mbb.GB(M);
        assertTrue("isGB( { (a,0,1),(b,1,0) } )", mbb.isGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenPolynomial<BigRational>>();
        V.add(c);
        V.add(fac.getZERO());
        V.add(fac.getZERO());
        L.add(V);
        M = new ModuleList<BigRational>(fac, L);
        //System.out.println("L = " + L.size() );

        N = mbb.GB(M);
        assertTrue("isGB( { (a,),(b,),(c,) } )", mbb.isGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenPolynomial<BigRational>>();
        V.add(d);
        V.add(fac.getZERO());
        V.add(fac.getZERO());
        L.add(V);
        M = new ModuleList<BigRational>(fac, L);
        //System.out.println("L = " + L.size() );

        N = mbb.GB(M);
        assertTrue("isGB( { (a,b,c,d) } )", mbb.isGB(N));
        //System.out.println("N = " + N );
    }


    /**
     * Test parallel GBase.
     */
    public void testParallelModGB() {
        mbb = new GroebnerBaseParallel<BigRational>(); //coeff);

        L = new ArrayList<List<GenPolynomial<BigRational>>>();

        V = new ArrayList<GenPolynomial<BigRational>>();
        V.add(a);
        V.add(fac.getZERO());
        V.add(fac.getONE());
        L.add(V);
        M = new ModuleList<BigRational>(fac, L);
        assertTrue("isGB( { (a,0,1) } )", mbb.isGB(M));

        N = mbb.GB(M);
        assertTrue("is( { (a,0,1) } )", mbb.isGB(N));

        V = new ArrayList<GenPolynomial<BigRational>>();
        V.add(b);
        V.add(fac.getONE());
        V.add(fac.getZERO());
        L.add(V);
        M = new ModuleList<BigRational>(fac, L);
        //System.out.println("L = " + L.size() );

        N = mbb.GB(M);
        assertTrue("isGB( { (a,0,1),(b,1,0) } )", mbb.isGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenPolynomial<BigRational>>();
        V.add(c);
        V.add(fac.getZERO());
        V.add(fac.getZERO());
        L.add(V);
        M = new ModuleList<BigRational>(fac, L);
        //System.out.println("L = " + L.size() );

        N = mbb.GB(M);
        assertTrue("isGB( { (a,),(b,),(c,) } )", mbb.isGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenPolynomial<BigRational>>();
        V.add(d);
        V.add(fac.getZERO());
        V.add(fac.getZERO());
        L.add(V);
        M = new ModuleList<BigRational>(fac, L);
        //System.out.println("L = " + L.size() );

        N = mbb.GB(M);
        assertTrue("isGB( { (a,b,c,d) } )", mbb.isGB(N));
        //System.out.println("N = " + N );
    }

    
    /**
     * Test sequential GBase with TOP and POT term order.
     */
    public void testSequentialModTOPGB() {
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        L = new ArrayList<List<GenPolynomial<BigRational>>>();
        V = new ArrayList<GenPolynomial<BigRational>>();
        V.add(a);
        V.add(fac.getZERO());
        V.add(fac.getONE());
        L.add(V);
        M = new ModuleList<BigRational>(fac, L);
        assertTrue("isGB( { (a,0,1) } )", mbb.isGB(M));

        N = mbb.GB(M);
        assertTrue("is( { (a,0,1) } )", mbb.isGB(N));

        K = mbb.GB(M, true);
        assertTrue("is( { (a,0,1) } )", mbb.isGB(K, true));
        assertEquals("N == K", N, K);

        V = new ArrayList<GenPolynomial<BigRational>>();
        V.add(b);
        V.add(fac.getONE());
        V.add(fac.getZERO());
        L.add(V);
        M = new ModuleList<BigRational>(fac, L);
        //System.out.println("M = " + M);

        N = mbb.GB(M);
        assertTrue("isGB( { (a,0,1),(b,1,0) } )", mbb.isGB(N));
        //System.out.println("N = " + N);

        K = mbb.GB(M,true);
        assertTrue("is( { (a,0,1) } )", mbb.isGB(K, true));
        //System.out.println("K = " + K);

        I = red.normalform(K, N, true);
        //System.out.println("I = " + I);
        assertTrue("K.nf(N) == (0)", I.isZERO());
    
        I = red.normalform(N, K);
        //System.out.println("I = " + I);
        assertTrue("N.nf(K) == (0)", I.isZERO());
    }

}
