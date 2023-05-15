/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.RelationGenerator;
import edu.jas.poly.RelationTable;
import edu.jas.poly.TermOrder;
import edu.jas.poly.WeylRelations;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Solvable Groebner base sequential tests with JUnit.
 * @author Heinz Kredel
 */

public class SolvableGroebnerBaseSeqTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>SolvableGroebnerBaseSeqTest</CODE> object.
     * @param name String.
     */
    public SolvableGroebnerBaseSeqTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SolvableGroebnerBaseSeqTest.class);
        return suite;
    }


    GenSolvablePolynomial<BigRational> a, b, c, d, e;


    List<GenSolvablePolynomial<BigRational>> L;


    PolynomialList<BigRational> F, G;


    GenSolvablePolynomialRing<BigRational> ring;


    SolvableGroebnerBase<BigRational> sbb;


    BigRational cfac;


    TermOrder tord;


    String[] vars;


    RelationTable<BigRational> table;


    int rl = 4; //4; //3; 


    int kl = 10;


    int ll = 4;


    int el = 2;


    float q = 0.3f; //0.4f

    @Override
    protected void setUp() {
        cfac = new BigRational(9);
        tord = new TermOrder();
        vars = new String[] { "x1", "x2", "x3", "x4" };
        ring = new GenSolvablePolynomialRing<BigRational>(cfac, tord, vars);
        table = ring.table;
        a = b = c = d = e = null;
        sbb = new SolvableGroebnerBaseSeq<BigRational>();

        a = ring.random(kl, ll, el, q);
        b = ring.random(kl, ll, el, q);
        c = ring.random(kl, ll, el, q);
        d = ring.random(kl, ll, el, q);
        e = d; //ring.random(kl, ll, el, q );
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ring = null;
        tord = null;
        vars = null;
        table = null;
        cfac = null;
        sbb = null;
    }


    /**
     * Test sequential GBase.
     */
    public void testSequentialGBase() {
        L = new ArrayList<GenSolvablePolynomial<BigRational>>();

        L.add(a);
        L = sbb.leftGB(L);
        assertTrue("isLeftGB( { a } )", sbb.isLeftGB(L));

        L.add(b);
        //System.out.println("L = " + L.size() );
        L = sbb.leftGB(L);
        assertTrue("isLeftGB( { a, b } )", sbb.isLeftGB(L));

        L.add(c);
        L = sbb.leftGB(L);
        assertTrue("isLeftGB( { a, b, c } )", sbb.isLeftGB(L));

        L.add(d);
        L = sbb.leftGB(L);
        assertTrue("isLeftGB( { a, b, c, d } )", sbb.isLeftGB(L));

        L.add(e);
        L = sbb.leftGB(L);
        assertTrue("isLeftGB( { a, b, c, d, e } )", sbb.isLeftGB(L));
    }


    /**
     * Test Weyl sequential GBase.
     */
    public void testWeylSequentialGBase() {
        //int rloc = 4;
        ring = new GenSolvablePolynomialRing<BigRational>(cfac, vars);

        RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
        wl.generate(ring);
        table = ring.table;

        a = ring.random(kl, ll, el, q);
        b = ring.random(kl, ll, el, q);
        c = ring.random(kl, ll, el, q);
        d = ring.random(kl, ll, el, q);
        e = d; //ring.random(kl, ll, el, q );

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();

        L.add(a);
        L = sbb.leftGB(L);
        assertTrue("isLeftGB( { a } )", sbb.isLeftGB(L));

        L.add(b);
        //System.out.println("L = " + L.size() );
        L = sbb.leftGB(L);
        assertTrue("isLeftGB( { a, b } )", sbb.isLeftGB(L));

        L.add(c);
        L = sbb.leftGB(L);
        assertTrue("isLeftGB( { a, b, c } )", sbb.isLeftGB(L));

        L.add(d);
        L = sbb.leftGB(L);
        assertTrue("isLeftGB( { a, b, c, d } )", sbb.isLeftGB(L));

        L.add(e);
        L = sbb.leftGB(L);
        assertTrue("isLeftGB( { a, b, c, d, e } )", sbb.isLeftGB(L));
    }


    /**
     * Test sequential twosided GBase.
     */
    public void testSequentialTSGBase() {
        L = new ArrayList<GenSolvablePolynomial<BigRational>>();

        L.add(a);
        L = sbb.twosidedGB(L);
        //System.out.println("L = " + L.size() );
        assertTrue("isTwosidedGB( { a } )", sbb.isTwosidedGB(L));

        L.add(b);
        L = sbb.twosidedGB(L);
        //System.out.println("L = " + L.size() );
        assertTrue("isTwosidedGB( { a, b } )", sbb.isTwosidedGB(L));

        L.add(c);
        L = sbb.twosidedGB(L);
        //System.out.println("L = " + L.size() );
        assertTrue("isTwosidedGB( { a, b, c } )", sbb.isTwosidedGB(L));

        L.add(d);
        L = sbb.twosidedGB(L);
        //System.out.println("L = " + L.size() );
        assertTrue("isTwosidedGB( { a, b, c, d } )", sbb.isTwosidedGB(L));

        L.add(e);
        L = sbb.twosidedGB(L);
        //System.out.println("L = " + L.size() );
        assertTrue("isTwosidedGB( { a, b, c, d, e } )", sbb.isTwosidedGB(L));
    }


    /**
     * Test Weyl sequential twosided GBase is always 1.
     */
    public void testWeylSequentialTSGBase() {
        //int rloc = 4;
        ring = new GenSolvablePolynomialRing<BigRational>(cfac, vars);

        RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
        wl.generate(ring);
        table = ring.table;

        a = ring.random(kl, ll, el, q);
        b = ring.random(kl, ll, el, q);
        c = ring.random(kl, ll, el, q);
        d = ring.random(kl, ll, el, q);
        e = d; //ring.random(kl, ll, el, q );

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();

        L.add(a);
        //System.out.println("La = " + L );
        L = sbb.twosidedGB(L);
        //System.out.println("L = " + L );
        assertTrue("isTwosidedGB( { a } )", sbb.isTwosidedGB(L));

        L.add(b);
        L = sbb.twosidedGB(L);
        //System.out.println("L = " + L );
        assertTrue("isTwosidedGB( { a, b } )", sbb.isTwosidedGB(L));

        L.add(c);
        L = sbb.twosidedGB(L);
        //System.out.println("L = " + L );
        assertTrue("isTwosidedGB( { a, b, c } )", sbb.isTwosidedGB(L));

        L.add(d);
        L = sbb.twosidedGB(L);
        //System.out.println("L = " + L );
        assertTrue("isTwosidedGB( { a, b, c, d } )", sbb.isTwosidedGB(L));

        L.add(e);
        L = sbb.twosidedGB(L);
        //System.out.println("L = " + L );
        assertTrue("isTwosidedGB( { a, b, c, d, e } )", sbb.isTwosidedGB(L));
    }


    /**
     * Test sequential right GBase.
     */
    public void testSequentialRightGBase() {
        L = new ArrayList<GenSolvablePolynomial<BigRational>>();

        L.add(a);
        L = sbb.rightGB(L);
        //System.out.println("L_r = " + L );
        assertTrue("isRightGB( { a } )", sbb.isRightGB(L));

        L.add(b);
        L = sbb.rightGB(L);
        //System.out.println("L_r = " + L );
        assertTrue("isRightGB( { a, b } )", sbb.isRightGB(L));

        L.add(c);
        L = sbb.rightGB(L);
        //System.out.println("L_r = " + L );
        assertTrue("isRightGB( { a, b, c } )", sbb.isRightGB(L));

        L.add(d);
        L = sbb.rightGB(L);
        //System.out.println("L_r = " + L );
        assertTrue("isRightGB( { a, b, c, d } )", sbb.isRightGB(L));

        L.add(e);
        L = sbb.rightGB(L);
        //System.out.println("L_r = " + L );
        assertTrue("isRightGB( { a, b, c, d, e } )", sbb.isRightGB(L));
    }


    /**
     * Test Weyl sequential right GBase.
     */
    public void testWeylSequentialRightGBase() {
        //int rloc = 4;
        ring = new GenSolvablePolynomialRing<BigRational>(cfac, vars);

        RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
        wl.generate(ring);
        table = ring.table;

        a = ring.random(kl, ll, el, q);
        b = ring.random(kl, ll, el, q);
        c = ring.random(kl, ll, el, q);
        d = ring.random(kl, ll, el, q);
        e = d; //ring.random(kl, ll, el, q );

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();

        L.add(a);
        //System.out.println("La = " + L );
        L = sbb.rightGB(L);
        //System.out.println("L_rw = " + L );
        assertTrue("isRightGB( { a } )", sbb.isRightGB(L));

        L.add(b);
        L = sbb.rightGB(L);
        //System.out.println("L_rw = " + L );
        assertTrue("isRightGB( { a, b } )", sbb.isRightGB(L));

        L.add(c);
        L = sbb.rightGB(L);
        //System.out.println("L_rw = " + L );
        assertTrue("isRightGB( { a, b, c } )", sbb.isRightGB(L));

        L.add(d);
        L = sbb.rightGB(L);
        //System.out.println("L_rw = " + L );
        assertTrue("isRightGB( { a, b, c, d } )", sbb.isRightGB(L));

        L.add(e);
        L = sbb.rightGB(L);
        //System.out.println("L_rw = " + L );
        assertTrue("isRightGB( { a, b, c, d, e } )", sbb.isRightGB(L));
    }


    /**
     * Test sequential extended GBase.
     */
    public void testSequentialExtendedGBase() {
        L = new ArrayList<GenSolvablePolynomial<BigRational>>();

        SolvableExtendedGB<BigRational> exgb;

        L.add(a);
        //System.out.println("L = " + L );
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.G_l = " + exgb.G );
        assertTrue("isLeftGB( { a } )", sbb.isLeftGB(exgb.G));
        assertTrue("isLeftRmat( { a } )", sbb.isLeftReductionMatrix(exgb));

        L.add(b);
        //System.out.println("L = " + L );
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.G_l = " + exgb.G );
        assertTrue("isLeftGB( { a, b } )", sbb.isLeftGB(exgb.G));
        assertTrue("isLeftRmat( { a, b } )", sbb.isLeftReductionMatrix(exgb));

        L.add(c);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.G_l = " + exgb.G );
        assertTrue("isLeftGB( { a, b, c } )", sbb.isLeftGB(exgb.G));
        assertTrue("isLeftRmat( { a, b, c } )", sbb.isLeftReductionMatrix(exgb));

        L.add(d);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.G_l = " + exgb.G );
        assertTrue("isLeftGB( { a, b, c, d } )", sbb.isLeftGB(exgb.G));
        assertTrue("isLeftRmat( { a, b, c, d } )", sbb.isLeftReductionMatrix(exgb));

        L.add(e);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.G_l = " + exgb.G );
        assertTrue("isLeftGB( { a, b, c, d, e } )", sbb.isLeftGB(exgb.G));
        assertTrue("isLeftRmat( { a, b, c, d, e } )", sbb.isLeftReductionMatrix(exgb));
    }


    /**
     * Test Weyl sequential extended GBase.
     */
    public void testWeylSequentialExtendedGBase() {
        //int rloc = 4;
        ring = new GenSolvablePolynomialRing<BigRational>(cfac, vars);

        RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
        wl.generate(ring);
        table = ring.table;

        a = ring.random(kl, ll, el, q);
        b = ring.random(kl, ll, el, q);
        c = ring.random(kl, ll, el, q);
        d = ring.random(kl, ll, el, q);
        e = d; //ring.random(kl, ll, el, q );

        SolvableExtendedGB<BigRational> exgb;

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();

        L.add(a);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.G_lw = " + exgb.G );
        assertTrue("isLeftGB( { a } )", sbb.isLeftGB(exgb.G));
        assertTrue("isRmat( { a } )", sbb.isLeftReductionMatrix(exgb));

        L.add(b);
        //System.out.println("L = " + L.size() );
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.G_lw = " + exgb.G );
        assertTrue("isLeftGB( { a, b } )", sbb.isLeftGB(exgb.G));
        assertTrue("isRmat( { a, b } )", sbb.isLeftReductionMatrix(exgb));

        L.add(c);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.G_lw = " + exgb.G );
        assertTrue("isLeftGB( { a, b, c } )", sbb.isLeftGB(exgb.G));
        assertTrue("isRmat( { a, b, c } )", sbb.isLeftReductionMatrix(exgb));

        L.add(d);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.G_lw = " + exgb.G );
        assertTrue("isLeftGB( { a, b, c, d } )", sbb.isLeftGB(exgb.G));
        assertTrue("isRmat( { a, b, c, d } )", sbb.isLeftReductionMatrix(exgb));

        L.add(e);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.G_lw = " + exgb.G );
        assertTrue("isLeftGB( { a, b, c, d, e } )", sbb.isLeftGB(exgb.G));
        assertTrue("isRmat( { a, b, c, d, e } )", sbb.isLeftReductionMatrix(exgb));
    }

}
