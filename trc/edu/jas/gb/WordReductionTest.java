/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.Overlap;
import edu.jas.poly.OverlapList;
import edu.jas.poly.Word;
import edu.jas.poly.WordFactory;


/**
 * Word reduction tests with JUnit.
 * @author Heinz Kredel.
 */

public class WordReductionTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>WordReductionTest</CODE> object.
     * @param name String
     */
    public WordReductionTest(String name) {
        super(name);
    }


    /**
     * suite.
     * @return a test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(WordReductionTest.class);
        return suite;
    }


    GenWordPolynomialRing<BigRational> fac;


    WordFactory wfac;


    BigRational cfac;


    GenWordPolynomial<BigRational> a, b, c, d, e;


    List<GenWordPolynomial<BigRational>> L;


    WordReductionSeq<BigRational> red;


    int kl = 3;


    int ll = 7;


    int el = 5;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        cfac = new BigRational(0);
        wfac = new WordFactory("abcdef");
        fac = new GenWordPolynomialRing<BigRational>(cfac, wfac);
        red = new WordReductionSeq<BigRational>();
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        red = null;
    }


    /**
     * Test constants and empty list reduction.
     */
    public void testRatReduction0() {
        L = new ArrayList<GenWordPolynomial<BigRational>>();

        a = fac.random(kl, ll, el);
        c = fac.getONE();
        d = fac.getZERO();

        e = red.normalform(L, c);
        assertTrue("isONE( e )", e.isONE());
        e = red.normalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());

        L.add(c);
        e = red.normalform(L, c);
        assertTrue("isZERO( e )", e.isZERO());
        e = red.normalform(L, a);
        assertTrue("isZERO( e )", e.isZERO());
        e = red.normalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        L.add(d);
        e = red.normalform(L, c);
        assertTrue("isONE( e )", e.isONE());
        e = red.normalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());
    }


    /**
     * Test rational coefficient reduction.
     */
    public void testRatReduction() {
        do {
            a = fac.random(kl, ll, el);
        } while (a.isZERO());
        do {
            b = fac.random(kl, ll, el);
        } while (b.isZERO());
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        L.add(a);

        e = red.normalform(L, a);
        //System.out.println("e = " + e);
        assertTrue("isZERO( e )", e.isZERO());

        L.add(b);
        e = red.normalform(L, a);
        //System.out.println("e = " + e);
        assertTrue("isZERO( e ) some times", e.isZERO());

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        L.add(a);
        assertTrue("isTopRed( a )", red.isTopReducible(L, a));
        assertTrue("isRed( a )", red.isReducible(L, a));
        L.add(b);
        assertTrue("isTopRed( b )", red.isTopReducible(L, b));
        assertTrue("isRed( b )", red.isReducible(L, b));

        c = fac.random(kl, ll, el);
        //System.out.println("c = " + c);
        e = red.normalform(L, c);
        //System.out.println("e = " + e);
        assertTrue("isNF( e )", red.isNormalform(L, e));

        Word u = new Word(wfac, "f");
        Word v = new Word(wfac, "abc");
        c = a.multiply(cfac.getONE(), u, v);
        //System.out.println("c = " + c);
        e = red.normalform(L, c);
        //System.out.println("e = " + e);
        assertTrue("isNF( e )", red.isNormalform(L, e));
        assertTrue("e == 0", e.isZERO());
    }


    /**
     * Test rational coefficient reduction with recording.
     */
    public void testRatReductionRecording() {
        List<GenWordPolynomial<BigRational>> lrow, rrow = null;
        do {
            a = fac.random(kl, ll, el);
        } while (a.isZERO());
        do {
            b = fac.random(kl, ll, el);
        } while (b.isZERO());
        c = fac.random(kl, ll, el);
        d = fac.random(kl, ll, el);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        L = new ArrayList<GenWordPolynomial<BigRational>>();
        L.add(a);
        lrow = new ArrayList<GenWordPolynomial<BigRational>>(L.size());
        rrow = new ArrayList<GenWordPolynomial<BigRational>>(L.size());
        e = fac.getZERO();
        for (int m = 0; m < L.size(); m++) {
            lrow.add(e);
            rrow.add(e);
        }
        e = red.normalform(lrow, rrow, L, a);
        //System.out.println("e = " + e);
        //System.out.println("lrow = " + lrow);
        //System.out.println("rrow = " + rrow);
        assertTrue("isZERO( e )", e.isZERO());
        assertTrue("is Reduction ", red.isReductionNF(lrow, rrow, L, a, e));

        L.add(b);
        lrow = new ArrayList<GenWordPolynomial<BigRational>>(L.size());
        rrow = new ArrayList<GenWordPolynomial<BigRational>>(L.size());
        e = fac.getZERO();
        for (int m = 0; m < L.size(); m++) {
            lrow.add(e);
            rrow.add(e);
        }
        e = red.normalform(lrow, rrow, L, b);
        //System.out.println("e = " + e);
        //System.out.println("lrow = " + lrow);
        //System.out.println("rrow = " + rrow);
        assertTrue("is Reduction ", red.isReductionNF(lrow, rrow, L, b, e));

        L.add(c);
        lrow = new ArrayList<GenWordPolynomial<BigRational>>(L.size());
        rrow = new ArrayList<GenWordPolynomial<BigRational>>(L.size());
        e = fac.getZERO();
        for (int m = 0; m < L.size(); m++) {
            lrow.add(e);
            rrow.add(e);
        }
        e = red.normalform(lrow, rrow, L, c);
        //System.out.println("e = " + e);
        //System.out.println("lrow = " + lrow);
        //System.out.println("rrow = " + rrow);
        assertTrue("is Reduction ", red.isReductionNF(lrow, rrow, L, c, e));

        L.add(d);
        lrow = new ArrayList<GenWordPolynomial<BigRational>>(L.size());
        rrow = new ArrayList<GenWordPolynomial<BigRational>>(L.size());
        e = fac.getZERO();
        for (int m = 0; m < L.size(); m++) {
            lrow.add(e);
            rrow.add(e);
        }
        Word u = new Word(wfac, "f");
        Word v = new Word(wfac, "abc");
        d = a.multiply(cfac.random(3), u, v);
        //System.out.println("d = " + d);
        e = red.normalform(lrow, rrow, L, d);
        //System.out.println("e = " + e);
        //System.out.println("lrow = " + lrow);
        //System.out.println("rrow = " + rrow);
        assertTrue("is Reduction ", red.isReductionNF(lrow, rrow, L, d, e));
    }


    /**
     * Test rational S-polynomial.
     */
    public void testRatSpolynomial() {
        do {
            a = fac.random(kl, ll, el);
        } while (a.isZERO());
        do {
            b = fac.random(kl, ll, el);
        } while (b.isZERO());
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        Word de = new Word(wfac, "a");
        a = a.multiply(wfac.getONE(), de);
        b = b.multiply(de, wfac.getONE());
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        Word ae = a.leadingWord();
        Word be = b.leadingWord();
        //System.out.println("ae = " + ae);
        //System.out.println("be = " + be);

        List<GenWordPolynomial<BigRational>> S = red.SPolynomials(a, b);
        //System.out.println("S = " + S);
        OverlapList oll = ae.overlap(be);
        //System.out.println("oll = " + oll);
        for (GenWordPolynomial<BigRational> s : S) {
            //System.out.println("s = " + s);
            Word ee = s.leadingWord();
            //System.out.println("ee = " + ee);
            boolean t = false;
            Word ce = fac.alphabet.getONE();
            for (Overlap ol : oll.ols) {
                ce = ol.l1.multiply(ae).multiply(ol.r1);
                //System.out.println("ce = " + ce);
                if (fac.alphabet.getAscendComparator().compare(ce, ee) > 0) {
                    t = true;
                    break;
                }
            }
            assertTrue("ce > ee: " + ce + " > " + ee, t);
            // findbugs
        }
    }

}
