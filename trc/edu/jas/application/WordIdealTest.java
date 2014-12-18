/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.gb.WordGroebnerBase;
import edu.jas.gb.WordGroebnerBaseSeq;
//import edu.jas.kern.ComputerThreads;
import edu.jas.poly.WordFactory;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;


/**
 * WordIdeal tests with JUnit.
 * @author Heinz Kredel.
 */
public class WordIdealTest extends TestCase {


    //private static final Logger logger = Logger.getLogger(WordIdealTest.class);


    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>WordIdealTest</CODE> object.
     * @param name String.
     */
    public WordIdealTest(String name) {
        super(name);
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


    WordGroebnerBase<BigRational> bb;


    GenWordPolynomial<BigRational> a, b, c, d, e;


    int kl = 3; //10


    int ll = 3; //7


    int el = 3;


    @Override
    protected void setUp() {
        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder( /*TermOrder.INVLEX*/);
        String[] vars = new String[] { "x", "y", "z" };
        WordFactory wf = new WordFactory(vars);
        fac = new GenWordPolynomialRing<BigRational>(coeff, wf);
        bb = new WordGroebnerBaseSeq<BigRational>();
        //bb = GBFactory.getImplementation(coeff);
        a = b = c = d = e = null;
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bb = null;
        //ComputerThreads.terminate();
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

        L.add(a);
        System.out.println("L = " + L.size() );

        I = new WordIdeal<BigRational>(fac, L, true);
        assertTrue("isGB( I )", I.isGB());

        I = new WordIdeal<BigRational>(fac, L, false);
        assertTrue("isGB( I )", I.isGB());

        L = bb.GB(L);
        assertTrue("isGB( { a } )", bb.isGB(L));

        I = new WordIdeal<BigRational>(fac, L, true);
        //assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        I = new WordIdeal<BigRational>(fac, L, false);
        //assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        //assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        System.out.println("L = " + L.size() );

        I = new WordIdeal<BigRational>(fac, L, false);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        //assertTrue("not isGB( I )", !I.isGB() );

        L = bb.GB(L);
        assertTrue("isGB( { a, b } )", bb.isGB(L));

        I = new WordIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        // assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I;
        K = J.sum(I);
        //assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("equals( K, I )", K.equals(I));

        L = new ArrayList<GenWordPolynomial<BigRational>>();

        L.add(c);
        assertTrue("isGB( { c } )", bb.isGB(L));

        J = new WordIdeal<BigRational>(fac, L, true);
        K = J.sum(I);
        assertTrue("isGB( K )", K.isGB());
        assertTrue("K contains(I)", K.contains(I));
        assertTrue("K contains(J)", K.contains(J));

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        L.add(d);

        assertTrue("isGB( { d } )", bb.isGB(L));
        J = new WordIdeal<BigRational>(fac, L, true);
        I = K;
        K = J.sum(I);
        assertTrue("isGB( K )", K.isGB());
        assertTrue("K contains(I)", K.contains(I));
        assertTrue("K contains(J)", K.contains(J));

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        L.add(e);

        assertTrue("isGB( { e } )", bb.isGB(L));
        J = new WordIdeal<BigRational>(fac, L, true);
        I = K;
        K = J.sum(I);
        assertTrue("isGB( K )", K.isGB());
        assertTrue("equals( K, I )", K.equals(I));
        assertTrue("K contains(J)", K.contains(I));
        assertTrue("I contains(K)", I.contains(K));
    }


    /**
     * Test WordIdeal product.
     */
    public void testWordIdealProduct() {
        WordIdeal<BigRational> I, J, K, H;
        a = fac.random(kl, ll, el);
        b = fac.random(kl, ll, el - 1);
        c = fac.random(kl, ll, el - 1);
        d = c; //fac.random(kl, ll, el);
        e = d; //fac.random(kl, ll, el);

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        //assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);

        I = new WordIdeal<BigRational>(fac, L, false);
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("not isONE( I )", !I.isONE()||a.isConstant());
        assertTrue("isGB( I )", I.isGB());

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        assertTrue("not isZERO( b )", !a.isZERO());
        L.add(b);

        J = new WordIdeal<BigRational>(fac, L, false);
        assertTrue("not isZERO( J )", !J.isZERO());
        assertTrue("not isONE( J )", !J.isONE()||a.isConstant()||b.isConstant());
        assertTrue("isGB( J )", J.isGB());

        K = I.product(J);
        //System.out.println("I = " + I);
        //System.out.println("J = " + J);
        //System.out.println("K = " + K);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        //non-com assertTrue("I contains(K)", I.contains(K));
        assertTrue("J contains(K)", J.contains(K));

        /*
        H = I.intersect(J);
        assertTrue("not isZERO( H )", !H.isZERO());
        assertTrue("isGB( H )", H.isGB());
        assertTrue("I contains(H)", I.contains(H));
        assertTrue("J contains(H)", J.contains(H));
        //non-com assertTrue("H contains(K)", H.contains(K));
        */

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);
        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);
        L = bb.GB(L);

        I = new WordIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        K = I.product(J);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        //non-com assertTrue("I contains(K)", I.contains(K));
        assertTrue("J contains(K)", J.contains(K));

        /*
        H = I.intersect(J);
        assertTrue("not isZERO( H )", !H.isZERO());
        assertTrue("isGB( H )", H.isGB());
        assertTrue("I contains(H)", I.contains(H));
        assertTrue("J contains(H)", J.contains(H));
        //non-com assertTrue("H contains(K)", H.contains(K));

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);
        L = bb.GB(L);

        J = new WordIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( J )", !J.isZERO());
        //assertTrue("not isONE( J )", !J.isONE() );
        assertTrue("isGB( J )", J.isGB());

        K = I.product(J);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        //non-com assertTrue("I contains(K)", I.contains(K));
        assertTrue("J contains(K)", J.contains(K));

        H = I.intersect(J);
        assertTrue("not isZERO( H )", !H.isZERO());
        assertTrue("isGB( H )", H.isGB());
        assertTrue("I contains(H)", I.contains(H));
        assertTrue("J contains(H)", J.contains(H));
        //non-com assertTrue("H contains(K)", H.contains(K));
        */
    }

}
