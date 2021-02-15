/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.jas.arith.BigRational;
import edu.jas.gb.SolvableGroebnerBase;
import edu.jas.gb.SolvableGroebnerBaseSeq;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.RelationGenerator;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderOptimization;
import edu.jas.poly.WeylRelations;
import edu.jas.poly.WeylRelationsIterated;
import edu.jas.util.KsubSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * SolvableIdeal tests with JUnit.
 * @author Heinz Kredel
 */
public class SolvableIdealTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>SolvableIdealTest</CODE> object.
     * @param name String.
     */
    public SolvableIdealTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SolvableIdealTest.class);
        return suite;
    }


    TermOrder to;


    GenSolvablePolynomialRing<BigRational> fac;


    List<GenSolvablePolynomial<BigRational>> L, M;


    PolynomialList<BigRational> F;


    List<GenSolvablePolynomial<BigRational>> G;


    SolvableGroebnerBase<BigRational> bb;


    GenSolvablePolynomial<BigRational> a, b, c, d, e;


    int rl = 4; // even for Weyl relations


    int kl = 2; //10


    int ll = 3; //7


    int el = 3;


    float q = 0.15f; //0.4f


    @Override
    protected void setUp() {
        BigRational coeff = new BigRational(17, 1);
        to = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "w", "x", "y", "z" };
        fac = new GenSolvablePolynomialRing<BigRational>(coeff, rl, to, vars);
        RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
        wl.generate(fac);
        bb = new SolvableGroebnerBaseSeq<BigRational>();
        //bb = GBFactory.getImplementation(coeff);
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
        SolvableIdeal<BigRational> I, J, K;
        L = new ArrayList<GenSolvablePolynomial<BigRational>>();

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

        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE());
        assertTrue("isGB( I )", I.isGB());

        I = new SolvableIdeal<BigRational>(fac, L, false);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE());
        assertTrue("isGB( I )", I.isGB());

        L = bb.leftGB(L);
        assertTrue("isGB( { a } )", bb.isLeftGB(L));

        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        I = new SolvableIdeal<BigRational>(fac, L, false);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        //System.out.println("L = " + L.size() );

        I = new SolvableIdeal<BigRational>(fac, L, false);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        //assertTrue("not isGB( I )", !I.isGB() );

        L = bb.leftGB(L);
        assertTrue("isGB( { a, b } )", bb.isLeftGB(L));

        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        // assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I;
        K = J.sum(I);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("equals( K, I )", K.equals(I));

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);
        assertTrue("isGB( { c } )", bb.isLeftGB(L));

        J = new SolvableIdeal<BigRational>(fac, L, true);
        K = J.sum(I);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("K contains(I)", K.contains(I));
        assertTrue("K contains(J)", K.contains(J));

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);

        assertTrue("isGB( { d } )", bb.isLeftGB(L));
        J = new SolvableIdeal<BigRational>(fac, L, true);
        I = K;
        K = J.sum(I);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("K contains(I)", K.contains(I));
        assertTrue("K contains(J)", K.contains(J));

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        assertTrue("not isZERO( e )", !e.isZERO());
        L.add(e);

        assertTrue("isGB( { e } )", bb.isLeftGB(L));
        J = new SolvableIdeal<BigRational>(fac, L, true);
        I = K;
        K = J.sum(I);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        assertTrue("equals( K, I )", K.equals(I));
        assertTrue("K contains(J)", K.contains(I));
        assertTrue("I contains(K)", I.contains(K));
    }


    /**
     * Test SolvableIdeal product.
     */
    public void testSolvableIdealProduct() {
        SolvableIdeal<BigRational> I, J, K, H;
        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el - 1, q);
        c = fac.random(kl, ll, el - 1, q);
        d = c; //fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        //assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);

        I = new SolvableIdeal<BigRational>(fac, L, false);
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("not isONE( I )", !I.isONE() || a.isConstant());
        assertTrue("isGB( I )", I.isGB());

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        assertTrue("not isZERO( b )", !a.isZERO());
        L.add(b);

        J = new SolvableIdeal<BigRational>(fac, L, false);
        assertTrue("not isZERO( J )", !J.isZERO());
        assertTrue("not isONE( J )", !J.isONE() || a.isConstant() || b.isConstant());
        assertTrue("isGB( J )", J.isGB());

        K = I.product(J);
        //System.out.println("I = " + I);
        //System.out.println("J = " + J);
        //System.out.println("K = " + K);
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

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);
        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);
        L = bb.leftGB(L);

        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

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

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);
        L = bb.leftGB(L);

        J = new SolvableIdeal<BigRational>(fac, L, true);
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
    }


    /**
     * Test SolvableIdeal quotient.
     */
    public void testSolvableIdealQuotient() {
        SolvableIdeal<BigRational> I, J, K, H;
        a = fac.random(kl, ll - 2, el, q);
        b = fac.random(kl, ll, el, q / 2);
        c = fac.random(kl, ll, el - 1, q);
        d = c; //fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);
        L = bb.leftGB(L);

        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        assertTrue("not isZERO( b )", !a.isZERO());
        L.add(b);
        L = bb.leftGB(L);

        J = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( J )", !J.isZERO());
        //assertTrue("not isONE( J )", !J.isONE() );
        assertTrue("isGB( J )", J.isGB());

        K = I.product(J);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        //non-com assertTrue("I contains(K)", I.contains(K));
        assertTrue("J contains(K)", J.contains(K));

        H = K.quotient(J.getList().get(0));
        assertTrue("not isZERO( H )", !H.isZERO());
        assertTrue("isGB( H )", H.isGB());
        //non-com assertTrue("equals(H,I)", H.equals(I)); // GBs only

        H = K.quotient(J);
        assertTrue("not isZERO( H )", !H.isZERO());
        assertTrue("isGB( H )", H.isGB());
        //non-com assertTrue("equals(H,I)", H.equals(I)); // GBs only

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);
        L = bb.leftGB(L);

        J = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( J )", !J.isZERO());
        //assertTrue("not isONE( J )", !J.isONE() );
        assertTrue("isGB( J )", J.isGB());

        K = I.product(J);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        //non-com assertTrue("I contains(K)", I.contains(K));
        assertTrue("J contains(K)", J.contains(K));

        H = K.quotient(J);
        assertTrue("not isZERO( H )", !H.isZERO());
        assertTrue("isGB( H )", H.isGB());
        //non-com assertTrue("equals(H,I)", H.equals(I)); // GBs only

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);
        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);
        L = bb.leftGB(L);

        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( J )", !J.isONE() );
        assertTrue("isGB( I )", I.isGB());

        K = I.product(J);
        assertTrue("not isZERO( K )", !K.isZERO());
        assertTrue("isGB( K )", K.isGB());
        //non-com assertTrue("I contains(K)", I.contains(K));
        assertTrue("J contains(K)", J.contains(K));

        H = K.quotient(J);
        assertTrue("not isZERO( H )", !H.isZERO());
        assertTrue("isGB( H )", H.isGB());
        //non-com assertTrue("equals(H,I)", H.equals(I)); // GBs only
    }


    /**
     * Test SolvableIdeal infinite quotient.
     */
    public void testSolvableIdealInfiniteQuotient() {
        SolvableIdeal<BigRational> I, J, K;
        a = fac.random(kl, ll - 2, el, q);
        b = fac.random(kl, ll - 1, el - 1, q);
        c = fac.random(kl, ll / 2, el - 1, q / 2);
        //a = fac.parse(" -1/2 w");
        //b = fac.parse(" y - 2/3");
        //c = fac.parse(" -2 w * y + 4/3 w + 2");
        //c = fac.parse(" -2 y^2 + 8/3 y - 8/9");
        d = c; //fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        L = bb.leftGB(L);
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I.infiniteQuotient(a);

        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);
        L = bb.leftGB(L);
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I.infiniteQuotient(a);
        assertTrue("equals(J,I)", J.equals(I)); // GBs only

        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);
        L = bb.leftGB(L);
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I.infiniteQuotient(a);
        assertTrue("isGB( J )", J.isGB());
        assertTrue("equals(J,I)", J.equals(I)); // GBs only

        G = new ArrayList<GenSolvablePolynomial<BigRational>>();
        assertTrue("not isZERO( a )", !a.isZERO());
        G.add(a);
        G = bb.leftGB(G);
        K = new SolvableIdeal<BigRational>(fac, G, true);
        assertTrue("not isZERO( K )", !K.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( K )", K.isGB());

        J = I.infiniteQuotient(K);
        assertTrue("equals(J,I)", J.equals(I)); // GBs only

        assertTrue("not isZERO( e )", !e.isZERO());
        G.add(e);
        G = bb.leftGB(G);
        K = new SolvableIdeal<BigRational>(fac, G, true);
        assertTrue("not isZERO( K )", !K.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( K )", K.isGB());

        J = I.infiniteQuotient(K);
        assertTrue("equals(J,I)", J.equals(I)); // GBs only
    }


    /**
     * Test (commutative) SolvableIdeal infinite quotient with Rabinowich trick.
     */
    public void testSolvableIdealInfiniteQuotientRabi() {
        fac = new GenSolvablePolynomialRing<BigRational>(fac.coFac, rl, fac.tord, fac.getVars());
        SolvableIdeal<BigRational> I, J, K, JJ;
        a = fac.random(kl - 1, ll - 1, el - 1, q / 2);
        b = fac.random(kl - 1, ll - 1, el, q / 2);
        c = fac.random(kl - 1, ll - 1, el, q / 2);
        d = fac.random(kl - 1, ll - 1, el, q / 2);
        e = a; //fac.random(kl, ll-1, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        L = bb.leftGB(L);
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I.infiniteQuotientRab(a);
        JJ = I.infiniteQuotient(a);
        assertTrue("equals(J,JJ)", J.equals(JJ)); // GBs only

        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);
        L = bb.leftGB(L);
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I.infiniteQuotientRab(a);
        assertTrue("equals(J,I)", J.equals(I)); // GBs only
        JJ = I.infiniteQuotient(a);
        assertTrue("equals(J,JJ)", J.equals(JJ)); // GBs only

        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);
        L = bb.leftGB(L);
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        J = I.infiniteQuotientRab(a);
        assertTrue("isGB( J )", J.isGB());
        assertTrue("equals(J,I)", J.equals(I)); // GBs only
        JJ = I.infiniteQuotient(a);
        assertTrue("equals(J,JJ)", J.equals(JJ)); // GBs only

        G = new ArrayList<GenSolvablePolynomial<BigRational>>();
        assertTrue("not isZERO( a )", !a.isZERO());
        G.add(a);
        G = bb.leftGB(G);
        K = new SolvableIdeal<BigRational>(fac, G, true);
        assertTrue("not isZERO( K )", !K.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( K )", K.isGB());

        J = I.infiniteQuotientRab(K);
        assertTrue("equals(J,I)", J.equals(I)); // GBs only
        JJ = I.infiniteQuotient(a);
        assertTrue("equals(J,JJ)", J.equals(JJ)); // GBs only

        assertTrue("not isZERO( e )", !e.isZERO());
        G.add(e);
        G = bb.leftGB(G);
        K = new SolvableIdeal<BigRational>(fac, G, true);
        assertTrue("not isZERO( K )", !K.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( K )", K.isGB());

        J = I.infiniteQuotientRab(K);
        assertTrue("equals(J,I)", J.equals(I)); // GBs only
        JJ = I.infiniteQuotient(a);
        assertTrue("equals(J,JJ)", J.equals(JJ)); // GBs only
    }


    /**
     * Test (commutative) SolvableIdeal radical membership.
     */
    public void testSolvableIdealRadicalMember() {
        fac = new GenSolvablePolynomialRing<BigRational>(fac.coFac, rl, fac.tord, fac.getVars());
        SolvableIdeal<BigRational> I;
        a = fac.random(kl - 1, ll, el - 1, q);
        b = fac.random(kl - 1, ll, el, q);
        c = fac.random(kl - 1, ll - 1, el, q / 2);
        //d = fac.random(kl - 1, ll - 1, el, q / 2);
        //e = a; //fac.random(kl, ll-1, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        L.add(b);
        L = bb.leftGB(L);
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("I = " + I);

        if (!I.isONE() && !a.equals(b)) {
            assertFalse("a in radical(b)", I.isRadicalMember(a));
            assertTrue("b in radical(b)", I.isRadicalMember(b));
        }

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        L.add(b.multiply(b));
        L = bb.leftGB(L);
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        //System.out.println("I = " + I);

        if (!I.isONE() && !a.equals(b)) {
            assertFalse("a in radical(b*b)", I.isRadicalMember(a));
            assertTrue("b in radical(b*b)", I.isRadicalMember(b));
        }

        //System.out.println("c = " + c);
        L.add(c);
        L = bb.leftGB(L);
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE() );
        assertTrue("isGB( I )", I.isGB());

        //System.out.println("I = " + I);

        if (!I.isONE() && !a.equals(b)) {
            assertFalse("a in radical(b*b,c)", I.isRadicalMember(a));
            assertTrue("b in radical(b*b,c)", I.isRadicalMember(b));
        }
    }


    /**
     * Test SolvableIdeal common zeros.
     */
    @SuppressWarnings("unchecked")
    public void testSolvableIdealCommonZeros() {
        SolvableIdeal<BigRational> I;
        L = new ArrayList<GenSolvablePolynomial<BigRational>>();

        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), 1);

        a = fac.getZERO();
        L.add(a);
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), 1);

        b = fac.getONE();
        L.add(b);
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), -1);

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        a = fac.random(kl, ll, el, q);
        if (!a.isZERO() && !a.isConstant()) {
            L.add(a);
            I = new SolvableIdeal<BigRational>(fac, L, true);
            assertEquals("commonZeroTest( I )", I.commonZeroTest(), 1);
        }

        L = (List<GenSolvablePolynomial<BigRational>>) fac.univariateList();
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), 0);

        L.remove(0);
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertEquals("commonZeroTest( I )", I.commonZeroTest(), 1);
    }


    /**
     * Test SolvableIdeal dimension.
     */
    @SuppressWarnings("unchecked")
    public void testSolvableIdealDimension() {
        SolvableIdeal<BigRational> I;
        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        Dimension dim;

        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertEquals("dimension( I )", rl, I.dimension().d);

        a = fac.getZERO();
        L.add(a);
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertEquals("dimension( I )", rl, I.dimension().d);

        b = fac.getONE();
        L.add(b);
        I = new SolvableIdeal<BigRational>(fac, L, true);
        assertEquals("dimension( I )", -1, I.dimension().d);

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        a = fac.random(kl, ll, el, q);
        if (!a.isZERO() && !a.isConstant()) {
            L.add(a);
            I = new SolvableIdeal<BigRational>(fac, L, true);
            //System.out.println("a = " + a);
            dim = I.dimension();
            //System.out.println("dim(I) = " + dim);
            assertTrue("dimension( I )", dim.d >= 1);
        }

        L = (List<GenSolvablePolynomial<BigRational>>) fac.univariateList();
        I = new SolvableIdeal<BigRational>(fac, L, true);
        dim = I.dimension();
        assertEquals("dimension( I )", 0, dim.d);

        while (L.size() > 0) {
            L.remove(0);
            I = new SolvableIdeal<BigRational>(fac, L, true);
            //System.out.println("I = " + I);
            dim = I.dimension();
            //System.out.println("dim(I) = " + dim);
            assertEquals("dimension( I )", rl - L.size(), dim.d);
        }

        L = (List<GenSolvablePolynomial<BigRational>>) fac.univariateList();
        I = new SolvableIdeal<BigRational>(fac, L, true);
        I = I.product(I);
        //System.out.println("I = " + I);
        dim = I.dimension();
        //System.out.println("dim(I) = " + dim);
        assertTrue("dimension( I )", 0 >= dim.d);

        L = I.getList();
        while (L.size() > 0) {
            L.remove(0);
            I = new SolvableIdeal<BigRational>(fac, L, true);
            //System.out.println("I = " + I);
            dim = I.dimension();
            //System.out.println("dim(I) = " + dim);
            assertTrue("dimension( I )", dim.d > 0);
        }
    }


    /**
     * Test elimination SolvableIdeals.
     */
    public void testElimSolvableIdeal() {
        BigRational coeff = new BigRational(17, 1);
        String[] vars = new String[] { "w", "x", "y", "z" };
        fac = new GenSolvablePolynomialRing<BigRational>(coeff, rl, to, vars);
        RelationGenerator<BigRational> wli = new WeylRelationsIterated<BigRational>();
        wli.generate(fac);
        //String[] vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);

        SolvableIdeal<BigRational> I, J, K;
        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        //non-com
        a = fac.univariate(0, 3L); //fac.random(kl, ll, el, q );
        b = fac.univariate(1, 2L); //fac.random(kl, ll, el, q );
        c = fac.univariate(0, 1L); //fac.random(kl, ll, el, q );

        L.add(a);
        //L.add(b);
        L.add(c);

        I = new SolvableIdeal<BigRational>(fac, L);
        I.doGB();
        //System.out.println("I = " + I.toScript());
        assertTrue("not isZERO( I )", !I.isZERO());
        //assertTrue("not isONE( I )", !I.isONE());
        assertTrue("isGB( I )", I.isGB());

        List<String> sv = new ArrayList<String>(vars.length);
        for (int i = 0; i < vars.length; i++) {
            sv.add(vars[i]);
        }
        //System.out.println("sv    = " + sv);

        for (int i = 2; i <= vars.length; i = i + 2) {
            KsubSet<String> ps = new KsubSet<String>(sv, i);
            //System.out.println("========================== ps : " + i);
            for (List<String> ev : ps) {
                int si = Collections.indexOfSubList(sv, ev);
                if (si < 0 || si % 2 != 0) { // substring and iterated Weyl algebra
                    continue;
                }
                //System.out.println("ev = " + ev);
                List<String> svr = new ArrayList<String>(vars.length);
                K = null;
                if (si != 0) { // and substring on even index
                    svr.addAll(ev);
                    for (String e : sv) {
                        if (svr.contains(e)) {
                            continue;
                        }
                        svr.add(e);
                    }
                    //System.out.println("svr = " + svr);
                    String[] rvars = new String[svr.size()];
                    for (int j = 0; j < svr.size(); j++) {
                        rvars[j] = svr.get(j);
                    }
                    List<Integer> P = new ArrayList<Integer>(sv.size());
                    for (int j = 0; j < rvars.length; j++) {
                        int k = sv.indexOf(rvars[j]);
                        P.add(k);
                    }
                    //System.out.println("P = " + P);
                    GenSolvablePolynomialRing<BigRational> rfac;
                    rfac = (GenSolvablePolynomialRing<BigRational>) fac.permutation(P);
                    //System.out.println("rfac = " + rfac.toScript());
                    List<GenSolvablePolynomial<BigRational>> id = TermOrderOptimization
                                    .<BigRational> permutation(P, rfac, I.list.castToSolvableList());
                    K = new SolvableIdeal<BigRational>(rfac, id);
                    //System.out.println("K = " + K.toScript());
                    //continue;
                }
                String[] evars = new String[ev.size()];
                for (int j = 0; j < ev.size(); j++) {
                    evars[j] = ev.get(j);
                }
                GenSolvablePolynomialRing<BigRational> efac;
                efac = new GenSolvablePolynomialRing<BigRational>(fac.coFac, evars.length, fac.tord, evars);
                //RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
                RelationGenerator<BigRational> wl = new WeylRelationsIterated<BigRational>();
                wl.generate(efac);
                //System.out.println("efac = " + efac.toScript());

                if (K == null) {
                    J = I.eliminate(efac);
                } else {
                    J = K.eliminate(efac);
                }
                //System.out.println("J = " + J.toScript());
                assertTrue("isGB( J )", J.isGB());
                assertTrue("size( J ) <=  |ev|", J.getList().size() <= ev.size());
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
        vars = new String[] { "w", "x", "y", "z" };
        fac = new GenSolvablePolynomialRing<BigRational>(coeff, rl, to, vars);

        vars = fac.getVars();
        //System.out.println("vars = " + Arrays.toString(vars));
        //System.out.println("fac = " + fac);
        assertTrue("vars.length == 4 ", vars.length == 4);

        SolvableIdeal<BigRational> I;
        L = new ArrayList<GenSolvablePolynomial<BigRational>>();

        a = fac.parse("( x^3 + 34/55 x^2 + 1/9 x + 99 )");
        b = fac.parse("( y^4 - x )");
        c = fac.parse("( z^3 - x y )");
        d = fac.parse("( w^2 + 3 )");

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        L.add(d);
        I = new SolvableIdeal<BigRational>(fac, L);
        //I.doGB();
        assertTrue("not isZERO( I )", !I.isZERO());
        assertTrue("isGB( I )", I.isGB());
        //System.out.println("I = " + I);

        for (int i = 0; i < rl; i++) { // rl
            GenSolvablePolynomial<BigRational> u = I.constructUnivariate(rl - 1 - i);
            //System.out.println("u = " + u);
            GenSolvablePolynomial<BigRational> U = fac.parse(u.toString());
            //System.out.println("U = " + U + "\n");
            assertTrue("I.contains(U) ", I.contains(U));
        }

        List<GenSolvablePolynomial<BigRational>> Us = I.constructUnivariate();
        for (GenSolvablePolynomial<BigRational> u : Us) {
            //System.out.println("u = " + u);
            GenSolvablePolynomial<BigRational> U = fac.parse(u.toString());
            //System.out.println("U = " + U + "\n");
            assertTrue("I.contains(U) ", I.contains(U));
        }
    }


    /**
     * Test SolvableIdeal annihilator.
     */
    public void testAnnihilator() {
        SolvableIdeal<BigRational> I, J, K;
        do {
            a = fac.random(kl, ll, el, q);
        } while (a.isZERO() || a.isConstant());
        b = fac.univariate(1);
        c = fac.univariate(rl - 1);
        //b = fac.random(kl - 1, ll, el, q);
        //c = fac.random(kl - 1, ll - 1, el, q / 2);

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();
        L.add(a);
        L.add(b);
        //L.add(c);
        //System.out.println("L = " + L);
        L = bb.leftGB(L);
        I = new SolvableIdeal<BigRational>(fac, L, true);
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
        M = new ArrayList<GenSolvablePolynomial<BigRational>>();
        M.add(c);
        M.add(d);
        //System.out.println("M = " + M);
        K = new SolvableIdeal<BigRational>(fac, M);
        //System.out.println("K = " + K + "\n");

        J = I.annihilator(K);
        //System.out.println("J = " + J + "\n");
        J.doGB();
        //System.out.println("K = " + K);
        //System.out.println("J = " + J + "\n");
        assertTrue("isAnnihilator(M,J)", I.isAnnihilator(K, J));
    }

}
