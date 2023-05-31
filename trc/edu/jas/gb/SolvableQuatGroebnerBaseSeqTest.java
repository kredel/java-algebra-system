/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigQuaternion;
import edu.jas.arith.BigQuaternionRing;
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
 * Solvable quaternion coefficients Groebner base sequential tests with JUnit.
 * @author Heinz Kredel
 */

public class SolvableQuatGroebnerBaseSeqTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>SolvableQuatGroebnerBaseSeqTest</CODE> object.
     * @param name String.
     */
    public SolvableQuatGroebnerBaseSeqTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SolvableQuatGroebnerBaseSeqTest.class);
        return suite;
    }

    GenSolvablePolynomial<BigQuaternion> a, b, c, d, e;


    List<GenSolvablePolynomial<BigQuaternion>> L;


    PolynomialList<BigQuaternion> F, G;


    GenSolvablePolynomialRing<BigQuaternion> ring;


    SolvableGroebnerBaseAbstract<BigQuaternion> sbb;


    BigQuaternionRing cfac;


    TermOrder tord;


    String[] vars;


    RelationTable<BigQuaternion> table;


    int rl = 4; //4; //3;


    int kl = 1;


    int ll = 3;


    int el = 2;


    float q = 0.3f; //0.4f

    @Override
    protected void setUp() {
        cfac = new BigQuaternionRing();
        tord = new TermOrder();
        vars = new String[] { "x1", "x2", "x3", "x4" };
        ring = new GenSolvablePolynomialRing<BigQuaternion>(cfac, tord, vars);
        table = ring.table;
        a = b = c = d = e = null;
        sbb = new SolvableGroebnerBaseSeq<BigQuaternion>();

        a = ring.random(kl, ll, el, q);
        b = ring.random(kl, ll, el, q);
        c = ring.random(kl, ll, el, q);
        d = ring.random(kl, ll, el, q);
        e = d; //ring.random(kl, ll, el, q );
        //System.out.println("gens = " + ring.generators());
        if (a.isConstant()) {
            a = (GenSolvablePolynomial<BigQuaternion>) a.sum(ring.univariate(1));
            //System.out.println("a+x3 = " + a + ", univ(1) = " + ring.univariate(1));
        }
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
     * Test sequential left GBase.
     */
    public void testSequentialGBase() {
        //System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d);
        L = new ArrayList<GenSolvablePolynomial<BigQuaternion>>();
        L.add(a);

        L = sbb.leftGB(L);
        //System.out.println("L_l = " + L );
        assertTrue("isLeftGB( { a } )", sbb.isLeftGB(L));

        L.add(b);
        L = sbb.leftGB(L);
        //System.out.println("L_l = " + L );
        assertTrue("isLeftGB( { a, b } )", sbb.isLeftGB(L));

        L.add(c);
        L = sbb.leftGB(L);
        //System.out.println("L_l = " + L );
        assertTrue("isLeftGB( { a, b, c } )", sbb.isLeftGB(L));

        L.add(d);
        L = sbb.leftGB(L);
        //System.out.println("L_l = " + L );
        assertTrue("isLeftGB( { a, b, c, d } )", sbb.isLeftGB(L));

        L.add(e);
        L = sbb.leftGB(L);
        //System.out.println("L_l = " + L );
        assertTrue("isLeftGB( { a, b, c, d, e } )", sbb.isLeftGB(L));
    }


    /**
     * Test Weyl sequential left GBase.
     */
    public void testWeylSequentialGBase() {
        //int rloc = 4;
        ring = new GenSolvablePolynomialRing<BigQuaternion>(cfac, tord, vars);

        RelationGenerator<BigQuaternion> wl = new WeylRelations<BigQuaternion>();
        wl.generate(ring);
        table = ring.table;

        a = ring.random(kl, ll, el, q);
        b = ring.random(kl, ll, el, q);
        c = ring.random(kl, ll, el, q);
        d = ring.random(kl, ll, el, q);
        e = d; //ring.random(kl, ll, el, q );
        if (a.isConstant()) {
            a = (GenSolvablePolynomial<BigQuaternion>) a.sum(ring.univariate(1));
            //System.out.println("a+x3 = " + a);
        }
        //System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d);

        L = new ArrayList<GenSolvablePolynomial<BigQuaternion>>();

        L.add(a);
        L = sbb.leftGB(L);
        //System.out.println("L_lw = " + L );
        assertTrue("isLeftGB( { a } )", sbb.isLeftGB(L));

        L.add(b);
        L = sbb.leftGB(L);
        //System.out.println("L_lw = " + L );
        assertTrue("isLeftGB( { a, b } )", sbb.isLeftGB(L));

        L.add(c);
        L = sbb.leftGB(L);
        //System.out.println("L_lw = " + L );
        assertTrue("isLeftGB( { a, b, c } )", sbb.isLeftGB(L));

        L.add(d);
        L = sbb.leftGB(L);
        //System.out.println("L_lw = " + L );
        assertTrue("isLeftGB( { a, b, c, d } )", sbb.isLeftGB(L));

        L.add(e);
        L = sbb.leftGB(L);
        //System.out.println("L_lw = " + L );
        assertTrue("isLeftGB( { a, b, c, d, e } )", sbb.isLeftGB(L));
    }


    /**
     * Test sequential right GBase.
     */
    public void testSequentialRightGBase() {
        //System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d);

        L = new ArrayList<GenSolvablePolynomial<BigQuaternion>>();

        L.add(a);
        L = sbb.rightGB(L);
        //System.out.println("L_r = " + L);
        assertTrue("isRightGB( { a } )", sbb.isRightGB(L));

        L.add(b);
        L = sbb.rightGB(L);
        //System.out.println("L_r = " + L);
        // while (!sbb.isRightGB(L)) {
        //     L = sbb.rightGB( L );
        //     System.out.println("L_r = " + L );
        // }
        assertTrue("isRightGB( { a, b } )", sbb.isRightGB(L));

        L.add(c);
        L = sbb.rightGB(L);
        //System.out.println("L_r = " + L);
        assertTrue("isRightGB( { a, b, c } )", sbb.isRightGB(L));

        L.add(d);
        L = sbb.rightGB(L);
        //System.out.println("L_r = " + L);
        assertTrue("isRightGB( { a, b, c, d } )", sbb.isRightGB(L));

        L.add(e);
        L = sbb.rightGB(L);
        //System.out.println("L_r = " + L);
        assertTrue("isRightGB( { a, b, c, d, e } )", sbb.isRightGB(L));
    }


    /**
     * Test Weyl sequential right GBase is always 1.
     */
    public void testWeylSequentialRightGBase() {
        //int rloc = 4; //#vars
        ring = new GenSolvablePolynomialRing<BigQuaternion>(cfac, tord, vars);

        RelationGenerator<BigQuaternion> wl = new WeylRelations<BigQuaternion>();
        wl.generate(ring);
        table = ring.table;

        a = ring.random(kl, ll, el, q);
        b = ring.random(kl, ll, el, q);
        c = ring.random(kl, ll, el, q);
        d = ring.random(kl, ll, el, q);
        e = d; //ring.random(kl, ll, el, q );
        if (a.isConstant()) {
            a = (GenSolvablePolynomial<BigQuaternion>) a.sum(ring.univariate(1));
            //System.out.println("a+x3 = " + a);
        }
        //System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d);

        L = new ArrayList<GenSolvablePolynomial<BigQuaternion>>();

        L.add(a);
        //System.out.println("La = " + L );
        L = sbb.rightGB(L);
        //System.out.println("L_rw = " + L);
        assertTrue("isRightGB( { a } )", sbb.isRightGB(L));

        L.add(b);
        L = sbb.rightGB(L);
        //System.out.println("L_rw = " + L);
        assertTrue("isRightGB( { a, b } )", sbb.isRightGB(L));

        L.add(c);
        L = sbb.rightGB(L);
        //System.out.println("L_rw = " + L);
        assertTrue("isRightGB( { a, b, c } )", sbb.isRightGB(L));

        L.add(d);
        L = sbb.rightGB(L);
        //System.out.println("L_rw = " + L);
        assertTrue("isRightGB( { a, b, c, d } )", sbb.isRightGB(L));

        L.add(e);
        L = sbb.rightGB(L);
        //System.out.println("L_rw = " + L);
        assertTrue("isRightGB( { a, b, c, d, e } )", sbb.isRightGB(L));
    }


    /**
     * Test sequential extended GBase.
     */
    public void testSequentialExtendedGBase() {
        //System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d);

        L = new ArrayList<GenSolvablePolynomial<BigQuaternion>>();
        SolvableExtendedGB<BigQuaternion> exgb;

        L.add(a);
        //System.out.println("L_le = " + L);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isLeftGB( { a } )", sbb.isLeftGB(exgb.G));
        //assertTrue("isLeftRmat( { a } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(b);
        //System.out.println("L_le = " + L);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isLeftGB( { a, b } )", sbb.isLeftGB(exgb.G));
        //assertTrue("isLeftRmat( { a, b } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(c);
        //System.out.println("L_le = " + L);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isLeftGB( { a, b, c } )", sbb.isLeftGB(exgb.G));
        //assertTrue("isLeftRmat( { a, b, c } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(d);
        //System.out.println("L_le = " + L);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isLeftGB( { a, b, c, d } )", sbb.isLeftGB(exgb.G));
        //assertTrue("isLeftRmat( { a, b, c, d } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(e);
        //System.out.println("L_le = " + L);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isLeftGB( { a, b, c, d, e } )", sbb.isLeftGB(exgb.G));
        //assertTrue("isLeftRmat( { a, b, c, d, e } )", sbb.isLeftReductionMatrix(exgb) );
    }


    /**
     * Test Weyl sequential extended GBase.
     */
    public void testWeylSequentialExtendedGBase() {
        //int rloc = 4; // = #vars
        ring = new GenSolvablePolynomialRing<BigQuaternion>(cfac, tord, vars);

        RelationGenerator<BigQuaternion> wl = new WeylRelations<BigQuaternion>();
        wl.generate(ring);
        table = ring.table;

        a = ring.random(kl, ll, el, q);
        b = ring.random(kl, ll, el, q);
        c = ring.random(kl, ll, el, q);
        d = ring.random(kl, ll, el, q);
        e = d; //ring.random(kl, ll, el, q );
        if (a.isConstant()) {
            a = (GenSolvablePolynomial<BigQuaternion>) a.sum(ring.univariate(1));
            //System.out.println("a+x3 = " + a);
        }
        //System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d);

        SolvableExtendedGB<BigQuaternion> exgb;
        L = new ArrayList<GenSolvablePolynomial<BigQuaternion>>();

        L.add(a);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isLeftGB( { a } )", sbb.isLeftGB(exgb.G));
        //assertTrue("isRmat( { a } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(b);
        //System.out.println("L = " + L.size() );
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isLeftGB( { a, b } )", sbb.isLeftGB(exgb.G));
        //assertTrue("isRmat( { a, b } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(c);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isLeftGB( { a, b, c } )", sbb.isLeftGB(exgb.G));
        //assertTrue("isRmat( { a, b, c } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(d);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isLeftGB( { a, b, c, d } )", sbb.isLeftGB(exgb.G));
        //assertTrue("isRmat( { a, b, c, d } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(e);
        exgb = sbb.extLeftGB(L);
        //System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isLeftGB( { a, b, c, d, e } )", sbb.isLeftGB(exgb.G));
        //assertTrue("isRmat( { a, b, c, d, e } )", sbb.isLeftReductionMatrix(exgb) );
    }


    /*
     * Test sequential extended right GBase. extRightGB not yet implemented.
     */
    public void noTestSequentialExtendedRightGBase() {
        System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d);

        L = new ArrayList<GenSolvablePolynomial<BigQuaternion>>();
        SolvableExtendedGB<BigQuaternion> exgb;

        L.add(a);
        System.out.println("L_le = " + L);
        exgb = sbb.extRightGB(L);
        System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isRightGB( { a } )", sbb.isRightGB(exgb.G));
        //assertTrue("isRightRmat( { a } )", sbb.isRightReductionMatrix(exgb) );

        L.add(b);
        System.out.println("L_le = " + L);
        exgb = sbb.extRightGB(L);
        System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isRightGB( { a, b } )", sbb.isRightGB(exgb.G));
        //assertTrue("isRightRmat( { a, b } )", sbb.isRightReductionMatrix(exgb) );

        L.add(c);
        System.out.println("L_le = " + L);
        exgb = sbb.extRightGB(L);
        System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isRightGB( { a, b, c } )", sbb.isRightGB(exgb.G));
        //assertTrue("isRightRmat( { a, b, c } )", sbb.isRightReductionMatrix(exgb) );

        L.add(d);
        System.out.println("L_le = " + L);
        exgb = sbb.extRightGB(L);
        System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isRightGB( { a, b, c, d } )", sbb.isRightGB(exgb.G));
        //assertTrue("isRightRmat( { a, b, c, d } )", sbb.isRightReductionMatrix(exgb) );

        L.add(e);
        System.out.println("L_le = " + L);
        exgb = sbb.extRightGB(L);
        System.out.println("exgb.GB_l = " + exgb.G);
        assertTrue("isRightGB( { a, b, c, d, e } )", sbb.isRightGB(exgb.G));
        //assertTrue("isRightRmat( { a, b, c, d, e } )", sbb.isRightReductionMatrix(exgb) );
    }

}
