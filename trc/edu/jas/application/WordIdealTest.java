/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigRational;
import edu.jas.gb.WordGroebnerBaseSeq;
// import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * WordIdeal tests with JUnit.
 * @author Heinz Kredel
 */
public class WordIdealTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>WordIdealTest</CODE> object.
     * @param name String.
     */
    public WordIdealTest(String name) {
        super(name);
        //ComputerThreads.terminate();
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(WordIdealTest.class);
        return suite;
    }


    TermOrder to;


    GenWordPolynomialRing<BigRational> fac;


    List<GenWordPolynomial<BigRational>> L, M;


    PolynomialList<BigRational> F;


    List<GenWordPolynomial<BigRational>> G;


    WordGroebnerBaseSeq<BigRational> bb;


    GenWordPolynomial<BigRational> a, b, c, d, e;


    int kl = 3; //10


    int ll = 5; //7


    int el = 2;


    @Override
    protected void setUp() {
        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder( /*TermOrder.INVLEX*/);
        String[] vars = new String[] { "x", "y", "z" };
        //WordFactory wf = new WordFactory(vars);
        fac = new GenWordPolynomialRing<BigRational>(coeff, vars);
        bb = new WordGroebnerBaseSeq<BigRational>();
        //bb = GBFactory.getImplementation(coeff);
        //bb.timestatus.setNotActive(); // allow infintite computation
        a = b = c = d = e = null;
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bb = null;
    }


    /**
     * Test Ideal sum.
     */
    public void testIdealSum() {
        WordIdeal<BigRational> I, J, K;
        L = new ArrayList<GenWordPolynomial<BigRational>>();

        a = fac.random(kl, ll, el);
        b = fac.random(kl, ll, el);
        c = fac.random(kl, ll, el);
        d = fac.random(kl, ll, el);
        e = d; //fac.random(kl, ll, el);

        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        L.add(a);
        //System.out.println("L = " + L.size() );

        I = new WordIdeal<BigRational>(fac, L, true, bb);
        assertTrue("isGB( I )", I.isGB());

        I = new WordIdeal<BigRational>(fac, L, false, bb);
        assertTrue("isGB( I )", I.isGB());

        L = bb.GB(L);
        assertTrue("isGB( { a } )", bb.isGB(L));

        I = new WordIdeal<BigRational>(fac, L, true, bb);
        assertTrue("isGB( I )", I.isGB());

        I = new WordIdeal<BigRational>(fac, L, false, bb);
        assertTrue("isGB( I )", I.isGB());

        String s = I.toScript() + "\n" + I.toString();
        //System.out.println("#s = " + s.length() + ": " + s);
        assertTrue("#s >= 46: " + s + ", #s = " + s.length(), s.length() >= 46);

        K = I.getZERO();
        assertTrue("K == 0: " + K, K.isZERO());
        K = I.getONE();
        assertTrue("K == 1: " + K, K.isONE());

        //assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        //System.out.println("L = " + L.size() );

        I = new WordIdeal<BigRational>(fac, L, false, bb);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        //assertTrue("not isGB( I )", !I.isGB() );

        L = bb.GB(L);
        assertTrue("isGB( { a, b } )", bb.isGB(L));

        I = new WordIdeal<BigRational>(fac, L, true, bb);
        assertTrue("not isZERO( I )", !I.isZERO());
        // assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I;
        K = J.sum(I);
        //assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("equals( K, I )", K.equals(I));
        assertEquals("compareTo( K, I )", K.compareTo(I), 0);

        K = J.sum(I.getList());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("equals( K, I )", K.equals(I));
        assertEquals("compareTo( K, I )", K.compareTo(I), 0);

        K = J.sum(I.getList().get(0));
        assertTrue("isGB( K )", K.isGB());
        assertTrue("equals( K, I )", K.equals(I));
        assertEquals("compareTo( K, I )", K.compareTo(I), 0);

        L = new ArrayList<GenWordPolynomial<BigRational>>();

        L.add(c);
        assertTrue("isGB( { c } )", bb.isGB(L));

        J = new WordIdeal<BigRational>(fac, L, true, bb);
        K = J.sum(I);
        assertTrue("isGB( K )", K.isGB());
        assertTrue("K contains(I)", K.contains(I));
        assertTrue("K contains(J)", K.contains(J));

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        L.add(d);

        assertTrue("isGB( { d } )", bb.isGB(L));
        J = new WordIdeal<BigRational>(fac, L, true, bb);
        I = K;
        K = J.sum(I);
        assertTrue("isGB( K )", K.isGB());
        assertTrue("K contains(I)", K.contains(I));
        assertTrue("K contains(J)", K.contains(J));

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        L.add(e);

        assertTrue("isGB( { e } )", bb.isGB(L));
        J = new WordIdeal<BigRational>(fac, L, true, bb);
        I = K;
        K = J.sum(I);
        assertTrue("isGB( K )", K.isGB());
        assertTrue("equals( K, I )", K.equals(I));
        assertTrue("K contains(J)", K.contains(I));
        assertTrue("I contains(K)", I.contains(K));
        assertEquals("compareTo( K, I )", K.compareTo(I), 0);

        L.clear();
        e = fac.parse("x x");
        L.add(e);
        I = new WordIdeal<BigRational>(fac, L, true, bb);
        I.doGB();
        J = I.power(3);
        J = J.power(2);
        K = I.power(6);
        //System.out.println("I = " + I);
        //System.out.println("J = " + J);
        //System.out.println("K = " + K);
        assertEquals("equals( (I**3)**2, I**6 )", K, J);
    }


    /**
     * Test WordIdeal product. Sometimes non-terminating.
     */
    public void testWordIdealProduct() {
        WordIdeal<BigRational> I, J, K, H, G;
        a = fac.random(kl, ll, el);
        b = fac.random(kl, ll, el);
        c = fac.random(kl, ll, el);
        d = c; //fac.random(kl, ll, el);
        e = d; //fac.random(kl, ll, el);

        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        L.add(a);

        I = new WordIdeal<BigRational>(fac, L, false, bb);
        assertTrue("not isONE( I )", !I.isONE() || a.isConstant());
        assertTrue("isGB( I )", I.isGB());

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        L.add(b);

        J = new WordIdeal<BigRational>(fac, L, false, bb);
        assertTrue("not isONE( J )", !J.isONE() || a.isConstant() || b.isConstant());
        assertTrue("isGB( J )", J.isGB());

        K = I.product(J);
        //System.out.println("I = " + I);
        //System.out.println("J = " + J);
        //System.out.println("K = " + K);
        H = J.product(I);
        //System.out.println("H = " + H);
        G = K.sum(H);
        //System.out.println("G = " + G);
        assertTrue("isGB( K )", K.isGB());
        assertTrue("isGB( H )", H.isGB());
        assertTrue("isGB( G )", G.isGB());
        //non-com 
        assertTrue("I contains(K)", I.contains(K));
        assertTrue("J contains(H)", J.contains(H));

        K = I.product(J.getList().get(0));
        //System.out.println("I = " + I);
        //System.out.println("J = " + J);
        //System.out.println("K = " + K);
        assertTrue("isGB( K )", K.isGB());
        assertTrue("I contains(K)", I.contains(K));


        L = new ArrayList<GenWordPolynomial<BigRational>>();
        L.add(a);
        //L.add(c);
        L = bb.GB(L); // may be infinite with c

        I = new WordIdeal<BigRational>(fac, L, true, bb);
        //System.out.println("I = " + I);
        assertTrue("isGB( I )", I.isGB());

        //System.out.println("J = " + J);
        K = I.product(J);
        //System.out.println("K = " + K);
        assertTrue("isGB( K )", K.isGB());
        assertTrue("I contains(K)", I.contains(K));
        assertTrue("J contains(K)", J.contains(K));

        M = I.normalform(K.getList());
        H = new WordIdeal<BigRational>(fac, M, false, bb);
        //System.out.println("I = " + I);
        //System.out.println("K = " + K);
        //System.out.println("H = " + H);
        assertTrue("isGB( H )", H.isGB());
        assertTrue("isZERO( H )", H.isZERO());
    }


    /**
     * Test WordIdeal common zeros.
     */
    public void testWordIdealCommonZeros() {
        WordIdeal<BigRational> I, J;
        L = new ArrayList<GenWordPolynomial<BigRational>>();

        I = new WordIdeal<BigRational>(fac, L, true, bb);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), 1);

        a = fac.getZERO();
        L.add(a);
        I = new WordIdeal<BigRational>(fac, L, true, bb);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), 1);

        b = fac.getONE();
        L.add(b);
        I = new WordIdeal<BigRational>(fac, L, true, bb);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), -1);

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        a = fac.random(kl, ll, el);
        if (!a.isZERO() && !a.isConstant()) {
            L.add(a);
            I = new WordIdeal<BigRational>(fac, L, true, bb);
            assertEquals("commonZeroTest( I )", I.commonZeroTest(), 1);
        }

        L = (List<GenWordPolynomial<BigRational>>) fac.univariateList();
        //System.out.println("L = " + L);
        I = new WordIdeal<BigRational>(fac, L, true, bb);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), 0);

        J = I.product(I);
        //System.out.println("J = " + J);
        assertEquals("commonZeroTest( J )", J.commonZeroTest(), 0);

        L.remove(0);
        I = new WordIdeal<BigRational>(fac, L, true, bb);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), 1);
    }


    /**
     * Test WordIdeal contraction.
     */
    public void testContraction() {
        BigRational coeff = new BigRational(17, 1);
        String[] vs = new String[] { "beta", "x", "y", "z" };
        GenWordPolynomialRing<BigRational> fac2 = new GenWordPolynomialRing<BigRational>(coeff, vs);

        WordIdeal<BigRational> I, J, K;
        L = new ArrayList<GenWordPolynomial<BigRational>>();
        a = fac2.getZERO();
        L.add(a);
        //b = fac2.getONE();
        //L.add(b);

        b = fac2.random(3).monic();
        b = fac2.valueOf(b);
        L.add(b);

        c = fac.random(4).monic();
        c = fac2.valueOf(c);
        L.add(c);

        I = new WordIdeal<BigRational>(fac2, L, false, bb);
        //System.out.println("I = " + I);
        I.doGB();
        //System.out.println("I = " + I);

        // now intersect with word polynomial ring
        //System.out.println("fac = " + fac.toScript());
        J = I.intersect(fac);
        //System.out.println("J = " + J);

        List<GenWordPolynomial<BigRational>> ex = new ArrayList<GenWordPolynomial<BigRational>>();
        for (GenWordPolynomial<BigRational> p : J.getList()) {
            GenWordPolynomial<BigRational> pe = fac2.valueOf(p);
            ex.add(pe);
        }
        K = new WordIdeal<BigRational>(fac2, ex, false, bb);
        //System.out.println("K = " + K);

        assertTrue("intersect ", I.contains(K));
    }


    /**
     * Test WordIdeal intersection.
     */
    public void testIntersection() {
        WordIdeal<BigRational> I, J, K;

        // first ideal
        L = new ArrayList<GenWordPolynomial<BigRational>>();
        b = fac.random(3).monic();
        L.add(b);
        c = fac.random(4).monic();
        L.add(c);

        I = new WordIdeal<BigRational>(fac, L, false, bb);
        //System.out.println("I = " + I);

        // second ideal
        L.clear();
        a = fac.random(3).monic();
        L.add(a);

        J = new WordIdeal<BigRational>(fac, L, false, bb);
        //System.out.println("J = " + J);

        // now intersect with word ideal
        K = I.intersect(J);
        //System.out.println("I cap J = K = " + K);
        assertTrue("intersect ", I.contains(K));
        assertTrue("intersect ", J.contains(K));

        // now intersect with list of word ideals
        List<WordIdeal<BigRational>> Bl = new ArrayList<WordIdeal<BigRational>>();
        Bl.add(J);
        K = I.intersect(Bl);
        //System.out.println("I cap J = K = " + K);
        assertTrue("intersect ", I.contains(K));
        assertTrue("intersect ", J.contains(K));
    }

}
