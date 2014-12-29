/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigInteger;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.RelationGenerator;
import edu.jas.poly.RelationTable;
import edu.jas.poly.WeylRelations;
import edu.jas.util.ListUtil;


/**
 * Solvable Reduction tests with JUnit.
 * @author Heinz Kredel.
 */

public class SolvablePseudoReductionTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>SolvablePseudoReductionTest</CODE> object.
     * @param name String.
     */
    public SolvablePseudoReductionTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SolvablePseudoReductionTest.class);
        return suite;
    }


    GenSolvablePolynomialRing<BigInteger> fac;


    RelationTable table;


    GenSolvablePolynomial<BigInteger> a, b, c, d, e, f, g, h;


    List<GenSolvablePolynomial<BigInteger>> L;


    PolynomialList<BigInteger> F, G;


    SolvablePseudoReduction<BigInteger> sred;


    //SolvablePseudoReduction<BigInteger> sredpar;

    int rl = 4;


    int kl = 10;


    int ll = 5;


    int el = 3;


    float q = 0.4f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        String[] vars = new String[] { "w", "x", "y", "z" };
        fac = new GenSolvablePolynomialRing<BigInteger>(new BigInteger(0), vars);
        sred = new SolvablePseudoReductionSeq<BigInteger>();
        //sredpar = new SolvablePseudoReductionPar<BigInteger>();
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        sred = null;
        //sredpar = null;
    }


    /**
     * Test constants and empty list reduction.
     */
    public void testIntReduction0() {
        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();

        a = fac.random(kl, ll, el, q);
        c = fac.getONE();
        d = fac.getZERO();

        e = sred.leftNormalform(L, c);
        assertTrue("isONE( e )", e.isONE());

        e = sred.leftNormalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());


        L.add(c);
        e = sred.leftNormalform(L, c);
        assertTrue("isZERO( e )", e.isZERO());

        // e = Reduction.leftNormalform( L, a );
        // assertTrue("isZERO( e )", e.isZERO() ); 

        e = sred.leftNormalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());


        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();
        L.add(d);
        e = sred.leftNormalform(L, c);
        assertTrue("isONE( e )", e.isONE());

        e = sred.leftNormalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());
    }


    /**
     * Test constants and empty list reduction.
     */
    public void testWeylIntReduction0() {
        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();
        RelationGenerator<BigInteger> wl = new WeylRelations<BigInteger>();
        wl.generate(fac);

        a = fac.random(kl, ll, el, q);
        c = fac.getONE();
        d = fac.getZERO();

        e = sred.leftNormalform(L, c);
        assertTrue("isONE( e )", e.isONE());

        e = sred.leftNormalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());


        L.add(c);
        e = sred.leftNormalform(L, c);
        assertTrue("isZERO( e )", e.isZERO());

        e = sred.leftNormalform(L, a);
        assertTrue("isZERO( e )", e.isZERO());

        e = sred.leftNormalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());


        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();
        L.add(d);
        e = sred.leftNormalform(L, c);
        assertTrue("isONE( e )", e.isONE());

        e = sred.leftNormalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());
    }


    /**
     * Test Int reduction.
     */
    public void testIntReduction() {
        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);

        assertTrue("not isZERO( a )", !a.isZERO());

        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();
        L.add(a);

        e = sred.leftNormalform(L, a);
        assertTrue("isZERO( e )", e.isZERO());

        assertTrue("not isZERO( b )", !b.isZERO());

        L.add(b);
        e = sred.leftNormalform(L, a);
        assertTrue("isZERO( e ) some times", e.isZERO());
    }


    /**
     * Test Weyl Integer reduction.
     */
    public void testWeylIntReduction() {
        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();

        RelationGenerator<BigInteger> wl = new WeylRelations<BigInteger>();
        wl.generate(fac);

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);

        assertTrue("not isZERO( a )", !a.isZERO());

        L.add(a);

        e = sred.leftNormalform(L, a);
        assertTrue("isZERO( e )", e.isZERO());

        assertTrue("not isZERO( b )", !b.isZERO());

        L.add(b);
        e = sred.leftNormalform(L, a);
        assertTrue("isZERO( e ) some times", e.isZERO());
    }


    /**
     * Test Int reduction recording.
     */
    public void testIntReductionRecording() {
        List<GenSolvablePolynomial<BigInteger>> row = null;
        PseudoReductionEntry<BigInteger> mf;

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);

        assertTrue("not isZERO( a )", !a.isZERO());

        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();

        L.add(a);
        mf = sred.leftNormalformFactor(L, a);
        f = a.multiply(mf.multiplicator);
        row = ListUtil.<GenSolvablePolynomial<BigInteger>> fill(L.size(), fac.getZERO());
        //d = sred.leftNormalform( row, L, a );
        d = sred.leftNormalform(row, L, f);
        assertTrue("isZERO( d )", d.isZERO());
        assertTrue("is leftReduction ", sred.isLeftReductionNF(row, L, f, d));

        L.add(b);
        mf = sred.leftNormalformFactor(L, b);
        f = b.multiply(mf.multiplicator);
        row = ListUtil.<GenSolvablePolynomial<BigInteger>> fill(L.size(), fac.getZERO());
        //e = sred.leftNormalform( row, L, b );
        d = sred.leftNormalform(row, L, f);
        assertTrue("is leftReduction ", sred.isLeftReductionNF(row, L, f, d));

        L.add(c);
        mf = sred.leftNormalformFactor(L, c);
        f = c.multiply(mf.multiplicator);
        row = ListUtil.<GenSolvablePolynomial<BigInteger>> fill(L.size(), fac.getZERO());
        d = sred.leftNormalform(row, L, f);
        assertTrue("is leftReduction ", sred.isLeftReductionNF(row, L, f, d));

        L.add(d);
        mf = sred.leftNormalformFactor(L, d);
        f = d.multiply(mf.multiplicator);
        row = ListUtil.<GenSolvablePolynomial<BigInteger>> fill(L.size(), fac.getZERO());
        e = sred.leftNormalform(row, L, f);
        assertTrue("is leftReduction ", sred.isLeftReductionNF(row, L, f, e));
    }


    /*
     * Test Int reduction parallel.
    public void testIntReductionPar() {
        a = fac.random(kl, ll, el, q );
        b = fac.random(kl, ll, el, q );

        assertTrue("not isZERO( a )", !a.isZERO() );

        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();
        L.add(a);

        e = sredpar.leftNormalform( L, a );
        assertTrue("isZERO( e )", e.isZERO() );

        assertTrue("not isZERO( b )", !b.isZERO() );

        L.add(b);
        e = sredpar.leftNormalform( L, a );
        assertTrue("isZERO( e ) some times", e.isZERO() ); 
    }
     */

    /*
     * Test Weyl Integer reduction parallel.
    public void testWeylIntReductionPar() {
        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();

        RelationGenerator<BigInteger> wl = new WeylRelations<BigInteger>();
        wl.generate(fac);

        a = fac.random(kl, ll, el, q );
        b = fac.random(kl, ll, el, q );

        assertTrue("not isZERO( a )", !a.isZERO() );

        L.add(a);

        e = sredpar.leftNormalform( L, a );
        assertTrue("isZERO( e )", e.isZERO() );

        assertTrue("not isZERO( b )", !b.isZERO() );

        L.add(b);
        e = sredpar.leftNormalform( L, a );
        assertTrue("isZERO( e ) some times", e.isZERO() ); 
    }
     */

}
