/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.gb.GroebnerBase;
import edu.jas.gbufd.GBFactory;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.ufd.Quotient;
import edu.jas.util.KsubSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Ideal tests with JUnit.
 * @author Heinz Kredel
 */
public class IdealTest extends TestCase {


    private static final Logger logger = LogManager.getLogger(IdealTest.class);


    /**
     * main
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>IdealTest</CODE> object.
     * @param name String.
     */
    public IdealTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(IdealTest.class);
        return suite;
    }


    TermOrder to;


    GenPolynomialRing<BigRational> fac;


    List<GenPolynomial<BigRational>> L, M, G;


    PolynomialList<BigRational> F;


    GroebnerBase<BigRational> bb;


    GenPolynomial<BigRational> a, b, c, d, e;


    int rl = 3; //4; //3; 


    int kl = 4; //10


    int ll = 5; //7


    int el = 3;


    float q = 0.2f; //0.4f


    @Override
    protected void setUp() {
        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder( /*TermOrder.INVLEX*/);
        String[] vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);
        //bb = new GroebnerBaseSeq<BigRational>();
        bb = GBFactory.getImplementation(coeff);
        a = b = c = d = e = null;
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bb = null;
        ComputerThreads.terminate();
    }


    /**
     * Test Ideal sum.
     */
    public void testIdealSum() {
        Ideal<BigRational> I, J, K;

        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);

        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("not isONE( I )", !I.isONE());
        assertTrue("isGB( I )", I.isGB());

        I = new Ideal<BigRational>(fac, L, false);
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("not isONE( I )", !I.isONE());
        assertTrue("isGB( I )", I.isGB());


        L = bb.GB(L);
        assertTrue("isGB( { a } )", bb.isGB(L));

        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        I = new Ideal<BigRational>(fac, L, false);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        String s = I.toScript() + "\n" + I.toString();
        //System.out.println("#s = " + s.length() + ": " + s);
        assertTrue("#s >= 90: ", s.length() >= 90);

        K = I.getZERO();
        assertTrue("K == 0: " + K, K.isZERO());
        K = I.getONE();
        assertTrue("K == 1: " + K, K.isONE());


        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        //System.out.println("L = " + L.size() );

        I = new Ideal<BigRational>(fac, L, false);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        //assertTrue("not isGB( I )", !I.isGB() );


        L = bb.GB(L);
        assertTrue("isGB( { a, b } )", bb.isGB(L));

        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        // assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());


        J = I;
        K = J.sum(I);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("equals( K, I )", K.equals(I));


        L = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);
        assertTrue("isGB( { c } )", bb.isGB(L));

        J = new Ideal<BigRational>(fac, L, true);
        K = J.sum(I);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("K contains(I)", K.contains(I));
        assertTrue("K contains(J)", K.contains(J));

        L = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);

        assertTrue("isGB( { d } )", bb.isGB(L));
        J = new Ideal<BigRational>(fac, L, true);
        I = K;
        K = J.sum(I);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("K contains(I)", K.contains(I));
        assertTrue("K contains(J)", K.contains(J));


        L = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( e )", !e.isZERO());
        L.add(e);

        assertTrue("isGB( { e } )", bb.isGB(L));
        J = new Ideal<BigRational>(fac, L, true);
        I = K;
        K = J.sum(I);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("equals( K, I )", K.equals(I));
        assertTrue("K contains(J)", K.contains(I));
        assertTrue("I contains(K)", I.contains(K));
    }


    /**
     * Test Ideal product.
     */
    public void testIdealProduct() {
        Ideal<BigRational> I, J, K, H;

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        L = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);

        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("not isONE( I )", !I.isONE());
        assertTrue("isGB( I )", I.isGB());

        L = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( b )", !a.isZERO());
        L.add(b);

        J = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( J )", !J.isZERO());
        assertTrue("not isONE( J )", !J.isONE());
        assertTrue("isGB( J )", J.isGB());

        K = I.product(J);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("I contains(K)", I.contains(K));
        assertTrue("J contains(K)", J.contains(K));

        H = I.intersect(J);
        assertTrue("not isZERO( H )", !H.isZERO());
        assertTrue("isGB( H )", H.isGB());
        assertTrue("I contains(H)", I.contains(H));
        assertTrue("J contains(H)", J.contains(H));
        assertTrue("H contains(K)", H.contains(K));
        //if (false /*! H.equals(K)*/) {
        //    System.out.println("I = " + I);
        //    System.out.println("J = " + J);
        //    System.out.println("K = " + K);
        //    System.out.println("H = " + H);
        //}


        L = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);
        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);
        L = bb.GB(L);

        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        K = I.product(J);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("I contains(K)", I.contains(K));
        assertTrue("J contains(K)", J.contains(K));

        H = I.intersect(J);
        assertTrue("not isZERO( H )", !H.isZERO());
        assertTrue("isGB( H )", H.isGB());
        assertTrue("I contains(H)", I.contains(H));
        assertTrue("J contains(H)", J.contains(H));
        assertTrue("H contains(K)", H.contains(K));
        //if (false /*! H.equals(K)*/) {
        //    System.out.println("I = " + I);
        //    System.out.println("J = " + J);
        //    System.out.println("K = " + K);
        //    System.out.println("H = " + H);
        //}


        L = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);
        L = bb.GB(L);

        J = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( J )", !J.isZERO());
        //assertTrue("not isONE( J )", !J.isONE() );
        assertTrue("isGB( J )", J.isGB());

        K = I.product(J);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("I contains(K)", I.contains(K));
        assertTrue("J contains(K)", J.contains(K));

        H = I.intersect(J);
        assertTrue("not isZERO( H )", !H.isZERO());
        assertTrue("isGB( H )", H.isGB());
        assertTrue("I contains(H)", I.contains(H));
        assertTrue("J contains(H)", J.contains(H));
        assertTrue("H contains(K)", H.contains(K));
        //if (false /*! H.equals(K)*/) {
        //    System.out.println("I = " + I);
        //    System.out.println("J = " + J);
        //    System.out.println("K = " + K);
        //    System.out.println("H = " + H);
        //}
    }


    /**
     * Test Ideal quotient.
     */
    public void testIdealQuotient() {
        Ideal<BigRational> I, J, K, H;

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        L = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);
        L = bb.GB(L);

        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());


        L = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( b )", !a.isZERO());
        L.add(b);
        L = bb.GB(L);

        J = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( J )", !J.isZERO());
        //assertTrue("not isONE( J )", !J.isONE() );
        assertTrue("isGB( J )", J.isGB());

        K = I.product(J);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("I contains(K)", I.contains(K));
        assertTrue("J contains(K)", J.contains(K));

        H = K.quotient(J.getList().get(0));
        assertTrue("not isZERO( H )", !H.isZERO());
        assertTrue("isGB( H )", H.isGB());
        assertTrue("equals(H,I)", H.equals(I)); // GBs only

        H = K.quotient(J);
        assertTrue("not isZERO( H )", !H.isZERO());
        assertTrue("isGB( H )", H.isGB());
        assertTrue("equals(H,I)", H.equals(I)); // GBs only


        L = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);
        L = bb.GB(L);

        J = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( J )", !J.isZERO());
        //assertTrue("not isONE( J )", !J.isONE() );
        assertTrue("isGB( J )", J.isGB());

        K = I.product(J);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("I contains(K)", I.contains(K));
        assertTrue("J contains(K)", J.contains(K));

        H = K.quotient(J);
        assertTrue("not isZERO( H )", !H.isZERO());
        assertTrue("isGB( H )", H.isGB());
        assertTrue("equals(H,I)", H.equals(I)); // GBs only


        L = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);
        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);
        L = bb.GB(L);

        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( J )", !J.isONE() );
        assertTrue("isGB( I )", I.isGB());

        K = I.product(J);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("I contains(K)", I.contains(K));
        assertTrue("J contains(K)", J.contains(K));

        H = K.quotient(J);
        assertTrue("not isZERO( H )", !H.isZERO());
        assertTrue("isGB( H )", H.isGB());
        assertTrue("equals(H,I)", H.equals(I)); // GBs only
    }


    /**
     * Test Ideal infinite quotient.
     */
    public void testIdealInfiniteQuotient() {
        Ideal<BigRational> I, J, K;

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        L = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        L = bb.GB(L);
        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I.infiniteQuotient(a);

        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);
        L = bb.GB(L);
        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I.infiniteQuotient(a);
        assertTrue("equals(J,I)", J.equals(I)); // GBs only

        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);
        L = bb.GB(L);
        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I.infiniteQuotient(a);
        assertTrue("isGB( J )", J.isGB());
        assertTrue("equals(J,I)", J.equals(I)); // GBs only


        G = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( a )", !a.isZERO());
        G.add(a);
        G = bb.GB(G);
        K = new Ideal<BigRational>(fac, G, true);
        assertTrue("not isZERO( K )", !K.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( K )", K.isGB());

        J = I.infiniteQuotient(K);
        assertTrue("equals(J,I)", J.equals(I)); // GBs only


        assertTrue("not isZERO( e )", !e.isZERO());
        G.add(e);
        G = bb.GB(G);
        K = new Ideal<BigRational>(fac, G, true);
        assertTrue("not isZERO( K )", !K.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( K )", K.isGB());

        J = I.infiniteQuotient(K);
        assertTrue("equals(J,I)", J.equals(I)); // GBs only
    }


    /**
     * Test Ideal infinite quotient with Rabinowich trick.
     */
    public void testIdealInfiniteQuotientRabi() {
        Ideal<BigRational> I;
        Ideal<BigRational> J;
        Ideal<BigRational> K;
        Ideal<BigRational> JJ;

        a = fac.random(kl - 1, ll - 1, el - 1, q / 2);
        b = fac.random(kl - 1, ll - 1, el, q / 2);
        c = fac.random(kl - 1, ll - 1, el, q / 2);
        d = fac.random(kl - 1, ll - 1, el, q / 2);
        e = a; //fac.random(kl, ll-1, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        L = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        L = bb.GB(L);
        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I.infiniteQuotientRab(a);
        JJ = I.infiniteQuotient(a);
        assertTrue("equals(J,JJ)", J.equals(JJ)); // GBs only

        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);
        L = bb.GB(L);
        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I.infiniteQuotientRab(a);
        assertTrue("equals(J,I)", J.equals(I)); // GBs only
        JJ = I.infiniteQuotient(a);
        assertTrue("equals(J,JJ)", J.equals(JJ)); // GBs only

        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);
        L = bb.GB(L);
        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I.infiniteQuotientRab(a);
        assertTrue("isGB( J )", J.isGB());
        assertTrue("equals(J,I)", J.equals(I)); // GBs only
        JJ = I.infiniteQuotient(a);
        assertTrue("equals(J,JJ)", J.equals(JJ)); // GBs only


        G = new ArrayList<GenPolynomial<BigRational>>();
        assertTrue("not isZERO( a )", !a.isZERO());
        G.add(a);
        G = bb.GB(G);
        K = new Ideal<BigRational>(fac, G, true);
        assertTrue("not isZERO( K )", !K.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( K )", K.isGB());

        J = I.infiniteQuotientRab(K);
        assertTrue("equals(J,I)", J.equals(I)); // GBs only
        JJ = I.infiniteQuotient(a);
        assertTrue("equals(J,JJ)", J.equals(JJ)); // GBs only


        assertTrue("not isZERO( e )", !e.isZERO());
        G.add(e);
        G = bb.GB(G);
        K = new Ideal<BigRational>(fac, G, true);
        assertTrue("not isZERO( K )", !K.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( K )", K.isGB());

        J = I.infiniteQuotientRab(K);
        assertTrue("equals(J,I)", J.equals(I)); // GBs only
        JJ = I.infiniteQuotient(a);
        assertTrue("equals(J,JJ)", J.equals(JJ)); // GBs only
    }


    /**
     * Test Ideal radical membership.
     */
    public void testIdealRadicalMember() {
        Ideal<BigRational> I;

        a = fac.random(kl - 1, ll, el - 1, q);
        b = fac.random(kl - 1, ll, el, q);
        c = fac.random(kl - 1, ll - 1, el, q / 2);
        d = fac.random(kl - 1, ll - 1, el, q / 2);
        e = a; //fac.random(kl, ll-1, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        L = new ArrayList<GenPolynomial<BigRational>>();
        L.add(b);
        L = bb.GB(L);
        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("I = " + I);

        if (!I.isONE()) {
            assertFalse("a in radical(b)", I.isRadicalMember(a));
            assertTrue("b in radical(b)", I.isRadicalMember(b));
        }

        L = new ArrayList<GenPolynomial<BigRational>>();
        L.add(b.multiply(b));
        L = bb.GB(L);
        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        //System.out.println("I = " + I);

        if (!I.isONE()) {
            assertFalse("a in radical(b*b)", I.isRadicalMember(a));
            assertTrue("b in radical(b*b)", I.isRadicalMember(b));
        }

        //System.out.println("c = " + c);
        L.add(c);
        L = bb.GB(L);
        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        //System.out.println("I = " + I);

        if (!I.isONE()) {
            assertFalse("a in radical(b*b)", I.isRadicalMember(a));
            assertTrue("b in radical(b*b)", I.isRadicalMember(b));
        }
    }


    /**
     * Test Ideal common zeros.
     */
    @SuppressWarnings("unchecked")
    public void testIdealCommonZeros() {
        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        I = new Ideal<BigRational>(fac, L, true);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), 1);

        a = fac.getZERO();
        L.add(a);
        I = new Ideal<BigRational>(fac, L, true);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), 1);

        b = fac.getONE();
        L.add(b);
        I = new Ideal<BigRational>(fac, L, true);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), -1);

        L = new ArrayList<GenPolynomial<BigRational>>();
        a = fac.random(kl, ll, el, q);
        if (!a.isZERO() && !a.isConstant()) {
            L.add(a);
            I = new Ideal<BigRational>(fac, L, true);
            assertEquals("commonZeroTest( I )", I.commonZeroTest(), 1);
        }

        L = (List<GenPolynomial<BigRational>>) fac.univariateList();
        I = new Ideal<BigRational>(fac, L, true);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), 0);

        L.remove(0);
        I = new Ideal<BigRational>(fac, L, true);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), 1);
    }


    /**
     * Test Ideal dimension.
     */
    @SuppressWarnings("unchecked")
    public void testIdealDimension() {
        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();
        Dimension dim;

        I = new Ideal<BigRational>(fac, L, true);
        assertEquals("dimension( I )", rl, I.dimension().d);

        a = fac.getZERO();
        L.add(a);
        I = new Ideal<BigRational>(fac, L, true);
        assertEquals("dimension( I )", rl, I.dimension().d);

        b = fac.getONE();
        L.add(b);
        I = new Ideal<BigRational>(fac, L, true);
        assertEquals("dimension( I )", -1, I.dimension().d);

        L = new ArrayList<GenPolynomial<BigRational>>();
        a = fac.random(kl, ll, el, q);
        if (!a.isZERO() && !a.isConstant()) {
            L.add(a);
            I = new Ideal<BigRational>(fac, L, true);
            //System.out.println("a = " + a);
            dim = I.dimension();
            //System.out.println("dim(I) = " + dim);
            assertTrue("dimension( I )", dim.d >= 1);
        }

        L = (List<GenPolynomial<BigRational>>) fac.univariateList();
        I = new Ideal<BigRational>(fac, L, true);
        dim = I.dimension();
        assertEquals("dimension( I )", 0, dim.d);

        while (L.size() > 0) {
            L.remove(0);
            I = new Ideal<BigRational>(fac, L, true);
            //System.out.println("I = " + I);
            dim = I.dimension();
            //System.out.println("dim(I) = " + dim);
            assertEquals("dimension( I )", rl - L.size(), dim.d);
        }

        L = (List<GenPolynomial<BigRational>>) fac.univariateList();
        I = new Ideal<BigRational>(fac, L, true);
        I = I.product(I);
        //System.out.println("I = " + I);
        dim = I.dimension();
        //System.out.println("dim(I) = " + dim);
        assertEquals("dimension( I )", 0, dim.d);

        L = I.getList();
        while (L.size() > 0) {
            L.remove(0);
            I = new Ideal<BigRational>(fac, L, true);
            //System.out.println("I = " + I);
            dim = I.dimension();
            //System.out.println("dim(I) = " + dim);
            assertTrue("dimension( I )", dim.d > 0);
        }
    }


    /**
     * Test Ideal term order optimization.
     */
    public void testIdealTopt() {
        Ideal<BigRational> I, J, K;

        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);

        I = new Ideal<BigRational>(fac, L);
        I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        J = I.copy(); //new Ideal<BigRational>(fac,L);
        J.doToptimize();
        assertTrue("not isZERO( J )", !J.isZERO());
        assertTrue("isGB( J )", J.isGB());
        //System.out.println("J = " + J);

        if (I.isONE()) {
            return;
        }

        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);

        I = new Ideal<BigRational>(fac, L);
        K = I.copy();
        I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("GB(I) = " + I);

        K.doToptimize();
        K.doGB();
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        //System.out.println("GB(opt(K)) = " + K);

        J = I.copy();
        J.doToptimize();
        assertTrue("not isZERO( J )", !J.isZERO());
        assertTrue("isGB( J )", J.isGB());
        //System.out.println("opt(GB(J)) = " + J);

        if (I.isONE()) {
            return;
        }

        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);

        I = new Ideal<BigRational>(fac, L);
        K = I.copy();
        I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("GB(I) = " + I);

        K.doToptimize();
        K.doGB();
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        //System.out.println("GB(opt(K)) = " + K);

        J = I.copy();
        J.doToptimize();
        assertTrue("not isZERO( J )", !J.isZERO());
        assertTrue("isGB( J )", J.isGB());
        //System.out.println("opt(GB(J)) = " + J);
    }


    /**
     * Test elimination Ideals.
     */
    public void testElimIdeal() {
        String[] vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);

        Ideal<BigRational> I, J;

        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.univariate(2, 3L); //fac.random(kl, ll, el, q );
        b = fac.univariate(1, 2L); //fac.random(kl, ll, el, q );
        c = fac.univariate(0, 1L); //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);

        I = new Ideal<BigRational>(fac, L);
        //I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        List<String> sv = new ArrayList<String>(vars.length);
        for (int i = 0; i < vars.length; i++) {
            sv.add(vars[i]);
        }
        //System.out.println("sv    = " + sv);

        for (int i = 0; i <= vars.length; i++) {
            KsubSet<String> ps = new KsubSet<String>(sv, i);
            //System.out.println("========================== ps : " + i);
            for (List<String> ev : ps) {
                //System.out.println("ev = " + ev);

                String[] evars = new String[ev.size()];
                for (int j = 0; j < ev.size(); j++) {
                    evars[j] = ev.get(j);
                }
                GenPolynomialRing<BigRational> efac;
                efac = new GenPolynomialRing<BigRational>(fac.coFac, evars.length, fac.tord, evars);
                //System.out.println("efac = " + efac);

                J = I.eliminate(efac);
                assertTrue("isGB( J )", J.isGB());
                assertTrue("size( J ) <=  |ev|", J.getList().size() <= ev.size());
                //System.out.println("J = " + J);
            }
        }
    }


    /**
     * Test univariate polynomials in ideal.
     */
    public void testUnivPoly() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.parse("( x^3 + 34/55 x^2 + 1/9 x + 99 )");
        b = fac.parse("( y^4 - x )");
        c = fac.parse("( z^3 - x y )");

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        I = new Ideal<BigRational>(fac, L);
        //I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        for (int i = 0; i < rl; i++) { // rl
            GenPolynomial<BigRational> u = I.constructUnivariate(rl - 1 - i);
            //System.out.println("u = " + u);
            GenPolynomial<BigRational> U = fac.parse(u.toString());
            //System.out.println("U = " + U + "\n");
            assertTrue("I.contains(U) ", I.contains(U));
        }

        List<GenPolynomial<BigRational>> Us = I.constructUnivariate();
        for (GenPolynomial<BigRational> u : Us) {
            //System.out.println("u = " + u);
            GenPolynomial<BigRational> U = fac.parse(u.toString());
            //System.out.println("U = " + U + "\n");
            assertTrue("I.contains(U) ", I.contains(U));
        }
    }


    /**
     * Test complex roots univariate polynomials in zero dim ideal.
     */
    public void testComplexRoot() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.parse("( x^3 - 27 )");
        b = fac.parse("( y^2 - 9 )");
        c = fac.parse("( z - 7 )");

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        I = new Ideal<BigRational>(fac, L);
        //I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        BigRational eps = new BigRational(1, 1000000);
        eps = eps.multiply(eps);
        eps = eps.multiply(eps).multiply(eps);
        BigDecimal e = new BigDecimal(eps.getRational());
        e = e.abs(); //.multiply(e); 

        BigDecimal dc = BigDecimal.ONE;
        GenPolynomialRing<BigDecimal> dfac = new GenPolynomialRing<BigDecimal>(dc, fac);
        //System.out.println("dfac = " + dfac);
        ComplexRing<BigDecimal> dcc = new ComplexRing<BigDecimal>(dc);
        GenPolynomialRing<Complex<BigDecimal>> dcfac = new GenPolynomialRing<Complex<BigDecimal>>(dcc, dfac);
        //System.out.println("dcfac = " + dcfac);

        List<List<Complex<BigDecimal>>> roots = PolyUtilApp.<BigRational> complexRootTuples(I, eps);
        //System.out.println("roots = " + roots + "\n");
        for (GenPolynomial<BigRational> p : I.getList()) {
            GenPolynomial<BigDecimal> dp = PolyUtil.<BigRational> decimalFromRational(dfac, p);
            GenPolynomial<Complex<BigDecimal>> dpc = PolyUtil.<BigDecimal> toComplex(dcfac, dp);
            //System.out.println("dpc = " + dpc);
            for (List<Complex<BigDecimal>> r : roots) {
                //System.out.println("r = " + r);
                Complex<BigDecimal> ev = PolyUtil.<Complex<BigDecimal>> evaluateAll(dcc, dpc, r);
                if (ev.norm().getRe().compareTo(e) > 0) {
                    //System.out.println("ev = " + ev);
                    fail("ev > eps : " + ev + " > " + e);
                }
            }
        }
        //System.out.println();
    }


    /**
     * Test real roots univariate polynomials in zero dim ideal.
     */
    public void testRealRoot() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.parse("( x^3 - 27 )");
        b = fac.parse("( y^4 - x )");
        c = fac.parse("( z^2 - x^2 )");

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        I = new Ideal<BigRational>(fac, L);
        //I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        BigRational eps = new BigRational(1, 10);
        eps = eps.power(BigDecimal.DEFAULT_PRECISION / 2);
        BigDecimal e = new BigDecimal(eps.getRational());
        e = e.abs(); //.multiply(e);
        eps = eps.multiply(new BigRational(1, 100));

        List<List<BigDecimal>> roots = PolyUtilApp.<BigRational> realRootTuples(I, eps);
        //System.out.println("roots = " + roots + "\n");
        // polynomials with decimal coefficients
        BigDecimal dc = BigDecimal.ONE;
        GenPolynomialRing<BigDecimal> dfac = new GenPolynomialRing<BigDecimal>(dc, fac);
        //System.out.println("dfac = " + dfac);
        for (GenPolynomial<BigRational> p : I.getList()) {
            GenPolynomial<BigDecimal> dp = PolyUtil.<BigRational> decimalFromRational(dfac, p);
            //System.out.println("dp = " + dp);
            for (List<BigDecimal> r : roots) {
                //System.out.println("r = " + r);
                BigDecimal ev = PolyUtil.<BigDecimal> evaluateAll(dc, dp, r);
                if (ev.abs().compareTo(e) > 0) {
                    //System.out.println("ev = " + ev);
                    fail("ev > e : " + ev + " > " + e + ", eps = " + new BigDecimal(eps));
                }
            }
        }
        //System.out.println();
    }


    /**
     * Test zero dimensional decomposition.
     */
    public void testZeroDimDecomp() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.parse("( x^3 - 27 )");
        b = fac.parse("( y^4 - x )");
        c = fac.parse("( z^2 - x^2 )");

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        I = new Ideal<BigRational>(fac, L);
        //I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        List<IdealWithUniv<BigRational>> zd = I.zeroDimDecomposition();
        //System.out.println("I = " + I);
        //System.out.println("zd = " + zd);

        boolean t = I.isZeroDimDecomposition(zd);
        //System.out.println("t = " + t);
        assertTrue("is decomposition ", t);

        List<IdealWithUniv<BigRational>> zde = I.zeroDimElimination(zd);
        //System.out.println("I = " + I);
        //System.out.println("zde = " + zde);

        t = I.isZeroDimDecomposition(zde);
        //System.out.println("t = " + t);
        assertTrue("is decomposition elimination", t);
    }


    /**
     * Test zero dimensional radical decomposition.
     */
    public void testZeroDimRadicalDecomp() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.parse("( x^3 - 27 )**3 ( x**2 - 11 )**2 ( x + 1 ) ");
        b = fac.parse("( y^4 - x )");
        c = fac.parse("( z^2 - x^2 )");

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        I = new Ideal<BigRational>(fac, L);
        //I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        List<IdealWithUniv<BigRational>> zd = I.zeroDimRadicalDecomposition();
        //System.out.println("I = " + I);
        //System.out.println("zd = " + zd);

        boolean t = I.isZeroDimDecomposition(zd);
        //System.out.println("t = " + t);
        assertTrue("is decomposition ", t);

        for (IdealWithUniv<BigRational> ru : zd) {
            t = I.isRadical(ru); // works also for ZeroDim
            //System.out.println("t = " + t);
            assertTrue("is radical decomposition: " + ru, t);

            t = ru.ideal.isZeroDimRadical();
            //System.out.println("t = " + t);
            assertTrue("is 0-dim radical decomposition: " + ru, t);
        }
    }


    /**
     * Test real roots univariate polynomials in zero dim ideal.
     */
    public void testIdealRealRoot() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.parse("( x^3 - 27 )");
        b = fac.parse("( y^4 - x )");
        c = fac.parse("( z^2 - x^2 )");

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        I = new Ideal<BigRational>(fac, L);
        //I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        List<IdealWithUniv<BigRational>> zd = I.zeroDimDecomposition();
        //System.out.println("zd = " + zd);
        assertTrue("is decomposition ", I.isZeroDimDecomposition(zd));

        BigRational eps = new BigRational(1, 10);
        eps = eps.power(BigDecimal.DEFAULT_PRECISION / 2);
        BigDecimal e = new BigDecimal(eps.getRational());
        e = e.abs(); //.multiply(e);
        eps = eps.multiply(new BigRational(1, 10));
        BigDecimal dc = BigDecimal.ONE;

        List<IdealWithRealRoots<BigRational>> roots = PolyUtilApp.<BigRational> realRoots(zd, eps);
        //System.out.println("roots = " + roots + "\n");

        for (IdealWithRealRoots<BigRational> Ir : roots) {
            List<GenPolynomial<BigRational>> L = Ir.ideal.getList();
            List<GenPolynomial<BigDecimal>> Ld = new ArrayList<GenPolynomial<BigDecimal>>(L.size());

            GenPolynomialRing<BigDecimal> dfac = new GenPolynomialRing<BigDecimal>(dc, Ir.ideal.list.ring);
            //System.out.println("dfac = " + dfac);

            for (GenPolynomial<BigRational> p : L) {
                GenPolynomial<BigDecimal> dp = PolyUtil.<BigRational> decimalFromRational(dfac, p);
                //System.out.println("dp = " + dp);
                Ld.add(dp);
            }
            boolean t = PolyUtilApp.isRealRoots(Ld, Ir.rroots, e);
            assertTrue("isRealRoots ", t); // this example only
        }
    }


    /**
     * Test complex roots univariate polynomials in zero dim ideal.
     */
    public void testIdealComplexRoot() {
        String[] vars;
        BigRational coeff = new BigRational(1, 1);
        to = new TermOrder(TermOrder.INVLEX);
        //vars = new String[] { "x", "y" };
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, vars.length, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.parse("( x^3 + 3 )");
        //a = fac.parse("( x^3 - 3 )");
        //a = fac.parse("( x^2 + 3 )");
        b = fac.parse("( y^2 - x )");
        //b = fac.parse("( y^2 + x )");
        //b = fac.parse("( y^2 + 4 )");
        c = fac.parse("( z^2 - x y )");
        //c = fac.parse("( z^2 - 5 )");

        if (a.isZERO() || b.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        I = new Ideal<BigRational>(fac, L);
        //I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        List<IdealWithUniv<BigRational>> zd = I.zeroDimRootDecomposition();
        //System.out.println("zd = " + zd);
        assertTrue("is decomposition ", I.isZeroDimDecomposition(zd));

        BigRational eps = new BigRational(1, 1000000);
        //eps = eps.multiply(eps);
        eps = eps.multiply(eps).multiply(eps);
        BigDecimal e = new BigDecimal(eps.getRational());
        e = e.abs(); //.multiply(e);

        List<IdealWithComplexAlgebraicRoots<BigRational>> roots = PolyUtilApp
                        .<BigRational> complexAlgebraicRoots(zd); //, eps);
        //System.out.println("roots = " + roots + "\n");

        ComplexRing<BigDecimal> dcc = new ComplexRing<BigDecimal>(e);

        int d = 0;
        int s = 0;
        for (IdealWithComplexAlgebraicRoots<BigRational> Ic : roots) {
            List<GenPolynomial<BigRational>> L = Ic.ideal.getList();
            List<GenPolynomial<Complex<BigDecimal>>> Ld = new ArrayList<GenPolynomial<Complex<BigDecimal>>>(
                            L.size());
            s += Ic.can.size();

            GenPolynomialRing<BigDecimal> dfac = new GenPolynomialRing<BigDecimal>(e, Ic.ideal.list.ring);
            //System.out.println("dfac = " + dfac);
            GenPolynomialRing<Complex<BigDecimal>> dcfac;
            dcfac = new GenPolynomialRing<Complex<BigDecimal>>(dcc, dfac);
            //System.out.println("dcfac = " + dcfac);
            int ds = 1;
            for (GenPolynomial<BigRational> p : L) {
                long dl = p.leadingExpVector().totalDeg();
                ds *= dl;
                GenPolynomial<BigDecimal> dp = PolyUtil.<BigRational> decimalFromRational(dfac, p);
                GenPolynomial<Complex<BigDecimal>> dpc = PolyUtil.<BigDecimal> toComplex(dcfac, dp);
                //System.out.println("p   = " + p);
                //System.out.println("dpc = " + dpc);
                Ld.add(dpc);
            }
            d += ds;
            assertTrue("#L == #Ld: ", L.size() == Ld.size());
            //List<List<Complex<BigDecimal>>> droot = Ic.decimalApproximation();
            Ic.doDecimalApproximation();
            //System.out.println("Ic = " + Ic.isDecimalApproximation());
            assertTrue("is decimal approximation ", Ic.isDecimalApproximation());
            assertTrue("#droot == ds: ", Ic.decimalApproximation().size() == ds);
        }
        logger.info("#roots = " + s + ", #vr-dim = " + d);
        assertTrue("#roots(" + s + ") == degree(" + d + "): ", s == d);
    }


    /**
     * Test normal position.
     */
    public void testNormalPosition() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.parse("( x^3 - 27 )");
        b = fac.parse("( y^3 - x )");
        c = fac.parse("( z^2 - x^2 )");

        L.add(a);
        L.add(b);
        L.add(c);
        I = new Ideal<BigRational>(fac, L);
        I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        int[] np = I.normalPositionIndex2Vars();
        //System.out.println("np = " + Arrays.toString(np));
        if (np == null) {
            np = I.normalPositionIndexUnivars();
            //System.out.println("np = " + Arrays.toString(np));
        }
        if (np == null) {
            return;
        }
        int i = np[0];
        int j = np[1];
        IdealWithUniv<BigRational> Ip = I.normalPositionFor(i, j, null);
        //System.out.println("Ip = " + Ip);

        boolean t = Ip.ideal.isNormalPositionFor(i + 1, j + 1); // sic
        //System.out.println("t = " + t);
        assertTrue("is normal position ", t);

        np = Ip.ideal.normalPositionIndex2Vars();
        //System.out.println("np = " + Arrays.toString(np));
        if (np == null) {
            np = Ip.ideal.normalPositionIndexUnivars();
            //System.out.println("np = " + Arrays.toString(np));
        }
        if (np == null) {
            return;
        }
        i = np[0];
        j = np[1];
        assertTrue("i == 0: " + Arrays.toString(np), i == 0);
        assertTrue("j == 2: " + Arrays.toString(np), j == 3); // fixed, was 3, again to 3
    }


    /**
     * Test 0-dim root decomposition char 0.
     */
    public void testRootDecompositionChar0() {
        String[] vars;
        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);
        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.parse("( x^2 - 7 )");
        b = fac.parse("( y^2 - 5 )");
        c = fac.parse("( z^3 - x * y )");

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        I = new Ideal<BigRational>(fac, L);
        I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        List<IdealWithUniv<BigRational>> rzd = I.zeroDimRootDecomposition();
        //System.out.println("rzd = " + rzd);

        assertTrue("is contained in intersection ", I.isZeroDimDecomposition(rzd));
    }


    /**
     * Test 0-dim root decomposition char p = 13.
     */
    public void testRootDecompositionCharP() {
        String[] vars;
        ModIntegerRing coeff = new ModIntegerRing(13, true); // 13 important for field extension
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        GenPolynomialRing<ModInteger> fac;
        fac = new GenPolynomialRing<ModInteger>(coeff, rl, to, vars);
        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac.toScript());
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<ModInteger> I;
        List<GenPolynomial<ModInteger>> L;
        L = new ArrayList<GenPolynomial<ModInteger>>();
        GenPolynomial<ModInteger> a, b, c;

        // need field extension for this test:
        a = fac.parse("( x^2 - 7 )");
        b = fac.parse("( y^3 + 3 )");
        c = fac.parse("( z^5 + x * y )");

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        I = new Ideal<ModInteger>(fac, L);
        //System.out.println("I = " + I);
        I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        List<IdealWithUniv<ModInteger>> rzd = I.zeroDimRootDecomposition();
        //System.out.println("rzd = " + rzd);
        //System.out.println("#rzd = " + rzd.size());
        assertTrue("is zero dim decomposition ", I.isZeroDimDecomposition(rzd));

        /* not ok when field extensions are used:
        List<Ideal<ModInteger>> izd = IdealWithUniv.asListOfIdeals(rzd);
        System.out.println("izd = " + izd);
        Ideal<ModInteger> J = I.intersect(izd);
        System.out.println("I = " + I.toScript());
        System.out.println("J = " + J.toScript());
        assertEquals("I == J ", I, J);
        */
    }


    /**
     * Test 0-dim prime decomposition.
     */
    public void testPrimeDecomposition() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.parse("( x^2 - 5 )^2 ");
        b = fac.parse("( y^2 - 5 )");
        c = fac.parse("( z^3 - x )");

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        I = new Ideal<BigRational>(fac, L);
        I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        List<IdealWithUniv<BigRational>> pzd = I.zeroDimPrimeDecomposition();
        //System.out.println("pzd = " + pzd);
        //System.out.println("I   = " + I);

        assertTrue("is contained in intersection ", I.isZeroDimDecomposition(pzd));
    }


    /**
     * Test 0-dim primary decomposition.
     */
    public void testPrimaryDecomposition() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.parse("( x^2 - 5 )^2 ");
        b = fac.parse("( y^2 - 5 )");
        c = fac.parse("( z^3 - x )");

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        I = new Ideal<BigRational>(fac, L);
        I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        List<PrimaryComponent<BigRational>> qzd = I.zeroDimPrimaryDecomposition();
        //System.out.println("qzd = " + qzd);
        //System.out.println("I   = " + I);

        assertTrue("is intersection ", I.isPrimaryDecomposition(qzd));
    }


    /**
     * Test 0-dim root decomposition and real roots.
     */
    public void testRootDecompositionReal() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.parse("( x^2 - 5 )");
        b = fac.parse("( y^2 - 7 )");
        c = fac.parse("( z^3 - x * y )");

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        I = new Ideal<BigRational>(fac, L);
        I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        List<IdealWithRealAlgebraicRoots<BigRational>> iur;
        iur = PolyUtilApp.<BigRational> realAlgebraicRoots(I);

        List<IdealWithUniv<BigRational>> iul = new ArrayList<IdealWithUniv<BigRational>>();
        for (IdealWithRealAlgebraicRoots<BigRational> iu : iur) {
            iul.add(iu);
        }
        assertTrue("is contained in intersection ", I.isZeroDimDecomposition(iul));

        for (IdealWithRealAlgebraicRoots<BigRational> iu : iur) {
            //@SuppressWarnings("unused")
            //List<List<BigDecimal>> rd = iu.decimalApproximation();
            iu.doDecimalApproximation(); // side effect compute decimal approx
            //System.out.println("iu = " + iu);
            //System.out.println("isDecimalApproximation: = " + iu.isDecimalApproximation());
            assertTrue("is decimal approximation ", iu.isDecimalApproximation());
        }
    }


    /**
     * Test extension-contraction.
     */
    public void testExtCont() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(); //TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        //a = fac.parse("( y^2 - 5 ) x ");
        //b = fac.parse("( y^2 - 5 ) x ");
        //c = fac.parse("( x z^3 - 3 )");

        //a = fac.parse("( x^2 + 2 x y z + z^4 ) ");
        //b = fac.parse("( y z - z^2 ) ");
        //c = fac.parse("0");

        a = fac.parse("( y + x y^2 ) ");
        b = fac.parse("( x z + x^2 y ) ");
        //c = fac.parse("0");

        if (a.isZERO() || b.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        //L.add(c);
        I = new Ideal<BigRational>(fac, L);
        I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        IdealWithUniv<Quotient<BigRational>> Ext = I.extension(new String[] { "x" });
        //Ideal<Quotient<BigRational>> Ext = I.extension( new String[] { "y", "z" } );
        //System.out.println("Ext = " + Ext);
        //System.out.println("I   = " + I);

        IdealWithUniv<BigRational> Con = I.permContraction(Ext);
        //System.out.println("Con = " + Con);
        //System.out.println("I   = " + I);

        assertTrue("I subseteq Con(Ext(I)) ", Con.ideal.contains(I));
    }


    /**
     * Test prime ideal decomposition.
     */
    public void testPrimeDecomp() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        //a = fac.parse("( y^2 - 5 ) x ");
        //b = fac.parse("( y^2 - 5 ) x ");
        //c = fac.parse("( x z^3 - 3 )");

        //a = fac.parse("( x^2 + 2 x y z + z^4 ) ");
        //b = fac.parse("( y z - z^2 ) ");

        //a = fac.parse("( y + x y^2 ) ");
        //b = fac.parse("( x z + x^2 y ) ");

        a = fac.parse("( z^2 - x ) ");
        b = fac.parse("( y^2 - x ) ");

        //a = fac.parse("( x y ) ");
        //b = fac.parse("( x z ) ");

        if (a.isZERO() || b.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        //L.add(c);
        I = new Ideal<BigRational>(fac, L);
        I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        List<IdealWithUniv<BigRational>> pdec = I.primeDecomposition();
        //System.out.println("pdec = " + pdec);
        //System.out.println("I    = " + I);

        assertTrue("I subseteq cup G_i ", I.isDecomposition(pdec));

        List<Ideal<BigRational>> dec = new ArrayList<Ideal<BigRational>>(pdec.size());
        for (IdealWithUniv<BigRational> pu : pdec) {
            dec.add(pu.ideal);
        }
        Ideal<BigRational> Ii = I.intersect(dec);
        //System.out.println("Ii   = " + Ii);
        //System.out.println("I    = " + I);

        // not always:
        assertTrue("I == Ii ", I.equals(Ii));
    }


    /**
     * Test radical ideal decomposition.
     */
    public void testRadicalDecomp() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        //a = fac.parse("( y^2 - 5 ) x ");
        //b = fac.parse("( y^2 - 5 ) x ");
        //c = fac.parse("( x z^3 - 3 )");

        a = fac.parse("( x^2 + 2 x y z + z^4 ) ");
        b = fac.parse("( y z - z^2 ) ");

        //a = fac.parse("( y + x y^2 ) ");
        //b = fac.parse("( x z + x^2 y ) ");

        //a = fac.parse("( z^2 - x )^2 ");
        //b = fac.parse("( y^2 - x ) ");

        //a = fac.parse("( x^2 y^3 ) ");
        //b = fac.parse("( x^2 z^5 ) ");

        if (a.isZERO() || b.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        //L.add(c);
        I = new Ideal<BigRational>(fac, L);
        I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        List<IdealWithUniv<BigRational>> rdec = I.radicalDecomposition();
        //System.out.println("rdec = " + rdec);
        //System.out.println("I    = " + I);

        assertTrue("I subseteq cup G_i ", I.isDecomposition(rdec));

        List<Ideal<BigRational>> dec = new ArrayList<Ideal<BigRational>>(rdec.size());
        for (IdealWithUniv<BigRational> ru : rdec) {
            dec.add(ru.ideal);
        }
        Ideal<BigRational> Ii = I.intersect(dec);
        //System.out.println("Ii   = " + Ii);
        //System.out.println("I    = " + I);
        assertTrue("Ii.contains(I) ", Ii.contains(I));

        for (IdealWithUniv<BigRational> ru : rdec) {
            //System.out.println("ru = " + ru);
            boolean t = I.isRadical(ru);
            assertTrue("isRadical: " + ru, t);
        }

        //Ii = I.radical();
        //System.out.println("Ii   = " + Ii);
        //System.out.println("I    = " + I);
        //assertTrue("Ii.contains(I) ", Ii.contains(I));
    }


    /**
     * Test ideal decomposition.
     */
    public void testIrredDecomp() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        //a = fac.parse("( y^2 - 5 ) x ");
        //b = fac.parse("( y^2 - 5 ) x ");
        //c = fac.parse("( x z^3 - 3 )");

        a = fac.parse("( x^2 + 2 x y z + z^4 ) ");
        b = fac.parse("( y z - z^2 ) ");

        //a = fac.parse("( y + x y^2 ) ");
        //b = fac.parse("( x z + x^2 y ) ");

        //a = fac.parse("( z^2 - x )^2 ");
        //b = fac.parse("( y^2 - x ) ");

        //a = fac.parse("( x^2 y^3 ) ");
        //b = fac.parse("( x^2 z^5 ) ");

        if (a.isZERO() || b.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        //L.add(c);
        I = new Ideal<BigRational>(fac, L);
        I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        List<IdealWithUniv<BigRational>> rdec = I.decomposition();
        //System.out.println("rdec = " + rdec);
        //System.out.println("I    = " + I);

        assertTrue("I subseteq cup G_i ", I.isDecomposition(rdec));

        List<Ideal<BigRational>> dec = new ArrayList<Ideal<BigRational>>(rdec.size());
        for (IdealWithUniv<BigRational> ru : rdec) {
            dec.add(ru.ideal);
        }
        Ideal<BigRational> Ii = I.intersect(dec);
        //System.out.println("Ii   = " + Ii);
        //System.out.println("I    = " + I);

        assertTrue("Ii.contains(I) ", Ii.contains(I));

        //Ii = I.radical();
        //System.out.println("Ii   = " + Ii);
        //System.out.println("I    = " + I);
        //assertTrue("Ii.contains(I) ", Ii.contains(I));
    }


    /**
     * Test primary ideal decomposition.
     */
    public void testPrimaryDecomp() {
        String[] vars;

        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        vars = new String[] { "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 3 ", vars.length == 3);

        Ideal<BigRational> I;
        L = new ArrayList<GenPolynomial<BigRational>>();

        //a = fac.parse("( y^2 - 5 ) x ");
        //b = fac.parse("( y^2 - 5 ) x ");
        //c = fac.parse("( x z^3 - 3 )");

        //a = fac.parse("( x^2 + 2 x y z + z^4 ) ");
        //b = fac.parse("( y z - z^2 ) ");

        //a = fac.parse("( y + x y^2 ) ");
        //b = fac.parse("( x z + x^2 y ) ");

        a = fac.parse("( x z^2 - 1 )^2 ");
        b = fac.parse("( y^2 - x ) ");

        //a = fac.parse("( x^2 y ) ");
        //b = fac.parse("( x z^3 ) ");

        if (a.isZERO() || b.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        //L.add(c);
        I = new Ideal<BigRational>(fac, L);
        I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        List<PrimaryComponent<BigRational>> qdec = I.primaryDecomposition();
        //System.out.println("qdec = " + qdec);
        //System.out.println("I    = " + I);

        List<Ideal<BigRational>> dec = new ArrayList<Ideal<BigRational>>(qdec.size());
        for (PrimaryComponent<BigRational> ru : qdec) {
            dec.add(ru.primary);
        }
        assertTrue("#qdec == #dec: ", qdec.size() == dec.size());
        assertTrue("I eq cup G_i ", I.isPrimaryDecomposition(qdec));
    }


    /**
     * Test Ideal annihilator.
     */
    public void testAnnihilator() {
        Ideal<BigRational> I, J, K;
        do {
            a = fac.random(kl, ll, el, q);
        } while (a.isZERO() || a.isConstant());
        b = fac.univariate(1);
        c = fac.univariate(rl - 1);
        //b = fac.random(kl - 1, ll, el, q);
        //c = fac.random(kl - 1, ll - 1, el, q / 2);

        L = new ArrayList<GenPolynomial<BigRational>>();
        L.add(a);
        L.add(b);
        //System.out.println("L = " + L);
        L = bb.GB(L);
        I = new Ideal<BigRational>(fac, L, true);
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I + "\n");

        J = I.annihilator(c);
        //System.out.println("J = " + J + "\n");
        J.doGB();
        //System.out.println("c = " + c);
        //System.out.println("J = " + J + "\n");
        assertTrue("isAnnihilator(c,J)", I.isAnnihilator(c, J));

        d = fac.univariate(rl - 2);
        //d = fac.random(kl - 1, ll, el, q);
        M = new ArrayList<GenPolynomial<BigRational>>();
        M.add(c);
        M.add(d);
        //System.out.println("M = " + M);
        K = new Ideal<BigRational>(fac, M);
        //System.out.println("K = " + K + "\n");

        J = I.annihilator(K);
        //System.out.println("J = " + J + "\n");
        J.doGB();
        //System.out.println("K = " + K);
        //System.out.println("J = " + J + "\n");
        assertTrue("isAnnihilator(M,J)", I.isAnnihilator(K, J));
    }

}
