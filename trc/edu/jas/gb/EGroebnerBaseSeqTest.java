/*
 * $Id$
 */

package edu.jas.gb;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * EGroebner base sequential tests with JUnit.
 * @author Heinz Kredel
 */

public class EGroebnerBaseSeqTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>EGroebnerBaseSeqTest</CODE> object.
     * @param name String.
     */
    public EGroebnerBaseSeqTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(EGroebnerBaseSeqTest.class);
        return suite;
    }


    GenPolynomialRing<BigInteger> fac;


    List<GenPolynomial<BigInteger>> L, G;


    PolynomialList<BigInteger> F;


    GroebnerBaseAbstract<BigInteger> bb;


    GenPolynomial<BigInteger> a, b, c, d, e;


    int rl = 3; //4; //3; 


    int kl = 4; //4; 10


    int ll = 4;


    int el = 3;


    float q = 0.2f; //0.4f


    @Override
    protected void setUp() {
        BigInteger coeff = new BigInteger(9);
        fac = new GenPolynomialRing<BigInteger>(coeff, rl);
        a = b = c = d = e = null;
        bb = new EGroebnerBaseSeq<BigInteger>();
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bb = null;
        ComputerThreads.terminate();
    }


    /**
     * Test sequential GBase.
     */
    public void testSequentialGBase() {
        L = new ArrayList<GenPolynomial<BigInteger>>();

        a = fac.random(kl, ll, el, q).abs();
        b = fac.random(kl, ll, el, q).abs();
        c = fac.random(kl, ll / 2, el, q).abs();
        d = fac.random(kl, ll / 2, el, q).abs();
        e = d; //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        L.add(a);
        //System.out.println("    L  = " + L );
        L = bb.GB(L);
        //System.out.println("eGB(L) = " + L );
        assertTrue("isGB( { a } )", bb.isGB(L));

        L.add(b);
        //System.out.println("    L  = " + L );
        L = bb.GB(L);
        //System.out.println("eGB(L) = " + L );
        assertTrue("isGB( { a, b } )", bb.isGB(L));

        L.add(c);
        //System.out.println("    L  = " + L );
        L = bb.GB(L);
        //System.out.println("eGB(L) = " + L );
        assertTrue("isGB( { a, b, c } )", bb.isGB(L));

        L.add(d);
        //System.out.println("    L  = " + L );
        L = bb.GB(L);
        //System.out.println("eGB(L) = " + L );
        assertTrue("isGB( { a, b, c, d } )", bb.isGB(L));

        L.add(e);
        //System.out.println("    L  = " + L );
        L = bb.GB(L);
        //System.out.println("eGB(L) = " + L );
        assertTrue("isGB( { a, b, c, d, e } )", bb.isGB(L));
    }


    /**
     * Test Trinks7 GBase over Z.
     */
    @SuppressWarnings("unchecked")
    public void testTrinks7GBaseZ() { // needs 20 sec
        String exam = "Z(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                        + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                        + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                        + "( 99 W - 11 B S + 3 B**2 ), " + "( 10000 B**2 + 6600 B + 2673 ) " + ") ";
        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        try {
            F = (PolynomialList<BigInteger>) parser.nextPolynomialSet();
        } catch (ClassCastException e) {
            fail("" + e);
        } catch (IOException e) {
            fail("" + e);
        }
        //System.out.println("F = " + F);

        G = bb.GB(F.list);
        PolynomialList<BigInteger> trinks = new PolynomialList<BigInteger>(F.ring, G);
        //System.out.println("G = " + trinks);
        //System.out.println("G.size() = " + G.size());
        assertTrue("isGB( eGB(Trinks7) )", bb.isGB(G));
        //assertEquals("#GB(Trinks7) == 44", 44, G.size() );
    }


    /**
     * Test Trinks7 GBase over Z(B).
     */
    @SuppressWarnings("unchecked")
    public void testTrinks7GBaseZ_B() {
        String exam = "IntFunc{ B } (S,T,Z,P,W) G " + "( " + "( { 45 } P + { 35 } S - { 165 B } - { 36 } ), "
                        + "( { 35 } P + { 40 } Z + { 25 } T - { 27 } S ), "
                        + "( { 15 } W + { 25 } S P + { 30 } Z - { 18 } T - { 165 B**2 } ), "
                        + "( { - 9 } W + { 15 } T P + { 20 } S Z ), " + "( P W + { 2 } T Z - { 11 B**3 } ), "
                        + "( { 99 } W - { 11 B } S + { 3 B**2 } ), " + "( { 10000 B**2 + 6600 B + 2673 } ) "
                        + ") ";
        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        EGroebnerBaseSeq<GenPolynomial<BigRational>> bb = new EGroebnerBaseSeq<GenPolynomial<BigRational>>();
        PolynomialList<GenPolynomial<BigRational>> F = null;
        List<GenPolynomial<GenPolynomial<BigRational>>> G = null;
        try {
            F = (PolynomialList<GenPolynomial<BigRational>>) parser.nextPolynomialSet();
        } catch (ClassCastException e) {
            fail("" + e);
        } catch (IOException e) {
            fail("" + e);
        }
        //System.out.println("F = " + F);

        List<GenPolynomial<GenPolynomial<BigRational>>> Fp = new ArrayList<GenPolynomial<GenPolynomial<BigRational>>>(
                        F.list.size());
        for (GenPolynomial<GenPolynomial<BigRational>> p : F.list) {
            p = PolyUtil.<BigRational> monic(p);
            Fp.add(p);
        }
        //System.out.println("Fp = " + Fp);
        G = bb.GB(Fp);
        //System.out.println("G = " + G);

        List<GenPolynomial<GenPolynomial<BigRational>>> Gp = new ArrayList<GenPolynomial<GenPolynomial<BigRational>>>(
                        F.list.size());
        for (GenPolynomial<GenPolynomial<BigRational>> p : G) {
            p = PolyUtil.<BigRational> monic(p);
            Gp.add(p);
        }
        PolynomialList<GenPolynomial<BigRational>> trinks = new PolynomialList<GenPolynomial<BigRational>>(
                        F.ring, Gp);
        //System.out.println("G = " + trinks);
        //System.out.println("G.size() = " + G.size());

        assertTrue("isGB( GB(Trinks7) )", bb.isGB(G));
        //assertEquals("#GB(Trinks7) == 1", 1, G.size() );
    }


    /**
     * Test sequential extended GBase.
     */
    public void testSequentialExtendedGBase() {
        L = new ArrayList<GenPolynomial<BigInteger>>();
        ExtendedGB<BigInteger> exgb;

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
        //System.out.println("L1 = " + L);

        exgb = bb.extGB(L);
        //System.out.println("exgb 1 = " + exgb);
        assertTrue("isGB( { a } )", bb.isGB(exgb.G));
        assertTrue("isRmat( { a } )", bb.isMinReductionMatrix(exgb));

        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        //System.out.println("L2 = " + L);

        exgb = bb.extGB(L);
        //System.out.println("exgb 2 = " + exgb);
        assertTrue("isGB( { a, b } )", bb.isGB(exgb.G));
        assertTrue("isRmat( { a, b } )", bb.isMinReductionMatrix(exgb));

        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);
        //System.out.println("L3 = " + L);

        exgb = bb.extGB(L);
        //System.out.println("exgb 3 = " + exgb );
        assertTrue("isGB( { a, b, c } )", bb.isGB(exgb.G));
        assertTrue("isRmat( { a, b, c } )", bb.isMinReductionMatrix(exgb));
        //if (true) { return; }

        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);
        //System.out.println("L4 = " + L);

        exgb = bb.extGB(L);
        //System.out.println("exgb 4 = " + exgb );
        assertTrue("isGB( { a, b, c, d } )", bb.isGB(exgb.G));
        assertTrue("isRmat( { a, b, c, d } )", bb.isMinReductionMatrix(exgb));


        assertTrue("not isZERO( e )", !e.isZERO());
        L.add(e);
        //System.out.println("L5 = " + L);

        exgb = bb.extGB(L);
        //System.out.println("exgb 5 = " + exgb );
        assertTrue("isGB( { a, b, c, d, e } )", bb.isGB(exgb.G));
        assertTrue("isRmat( { a, b, c, d, e } )", bb.isMinReductionMatrix(exgb));
    }

}
