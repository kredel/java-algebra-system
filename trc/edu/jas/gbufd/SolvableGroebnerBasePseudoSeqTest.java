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
//import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.RelationTable;
import edu.jas.poly.TermOrder;
import edu.jas.poly.WeylRelations;
import edu.jas.poly.RelationGenerator;
import edu.jas.gb.SolvableExtendedGB;
import edu.jas.gb.SolvableGroebnerBaseAbstract;


/**
 * Solvable Groebner base pseudo sequential tests with JUnit.
 * @author Heinz Kredel.
 */

public class SolvableGroebnerBasePseudoSeqTest extends TestCase {

    //private static final Logger logger = Logger.getLogger(SolvableGroebnerBasePseudoSeqTest.class);

    /**
     * main.
     */
    public static void main (String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run( suite() );
    }

    /**
     * Constructs a <CODE>SolvableGroebnerBasePseudoSeqTest</CODE> object.
     * @param name String.
     */
    public SolvableGroebnerBasePseudoSeqTest(String name) {
        super(name);
    }

    /**
     * suite.
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(SolvableGroebnerBasePseudoSeqTest.class);
        return suite;
    }


    GenSolvablePolynomial<BigInteger> a, b, c, d, e;

    List<GenSolvablePolynomial<BigInteger>> L;
    PolynomialList<BigInteger> F, G;

    GenSolvablePolynomialRing<BigInteger> ring;

    SolvableGroebnerBaseAbstract<BigInteger> sbb;

    BigInteger cfac;
    TermOrder tord;
    RelationTable<BigInteger> table;

    int rl = 4; //4; //3; 
    int kl = 3;
    int ll = 4;
    int el = 3;
    float q = 0.25f; //0.4f

    protected void setUp() {
        cfac = new BigInteger(9);
        tord = new TermOrder();
        String[] vars = new String[] { "w", "x", "y", "z" };
        ring = new GenSolvablePolynomialRing<BigInteger>(cfac,tord,vars);
        table = ring.table;
        a = b = c = d = e = null;
        sbb = new SolvableGroebnerBasePseudoSeq<BigInteger>(cfac);

        a = ring.random(kl, ll, el, q );
        b = ring.random(kl, ll, el, q );
        c = ring.random(kl, ll, el, q );
        d = ring.random(kl, ll, el, q );
        e = d; //ring.random(kl, ll, el, q );
    }

    protected void tearDown() {
        a = b = c = d = e = null;
        ring = null;
        tord = null;
        table = null;
        cfac = null;
        sbb = null;
    }


    /**
     * Test sequential GBase.
     */
    public void testSequentialGBase() {
        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();

        L.add(a);
        L = sbb.leftGB( L );
        assertTrue("isLeftGB( { a } )", sbb.isLeftGB(L) );

        L.add(b);
        //System.out.println("L = " + L.size() );
        L = sbb.leftGB( L );
        assertTrue("isLeftGB( { a, b } )", sbb.isLeftGB(L) );

        L.add(c);
        L = sbb.leftGB( L );
        assertTrue("isLeftGB( { a, b, c } )", sbb.isLeftGB(L) );

        L.add(d);
        L = sbb.leftGB( L );
        assertTrue("isLeftGB( { a, b, c, d } )", sbb.isLeftGB(L) );

        L.add(e);
        L = sbb.leftGB( L );
        assertTrue("isLeftGB( { a, b, c, d, e } )", sbb.isLeftGB(L) );
    }


    /**
     * Test Weyl sequential GBase.
     */
    public void testWeylSequentialGBase() {
        //int rloc = 4;
        //ring = new GenSolvablePolynomialRing<BigInteger>(cfac,rloc);

        RelationGenerator<BigInteger> wl = new WeylRelations<BigInteger>();
        wl.generate(ring);
        table = ring.table;

        a = ring.random(kl, ll, el, q );
        b = ring.random(kl, ll, el, q );
        c = ring.random(kl, ll, el, q );
        d = ring.random(kl, ll, el, q );
        e = d; //ring.random(kl, ll, el, q );

        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();

        L.add(a);
        L = sbb.leftGB( L );
        assertTrue("isLeftGB( { a } )", sbb.isLeftGB(L) );

        L.add(b);
        //System.out.println("L = " + L.size() );
        L = sbb.leftGB( L );
        assertTrue("isLeftGB( { a, b } )", sbb.isLeftGB(L) );

        L.add(c);
        L = sbb.leftGB( L );
        assertTrue("isLeftGB( { a, b, c } )", sbb.isLeftGB(L) );

        L.add(d);
        L = sbb.leftGB( L );
        assertTrue("isLeftGB( { a, b, c, d } )", sbb.isLeftGB(L) );

        L.add(e);
        L = sbb.leftGB( L );
        assertTrue("isLeftGB( { a, b, c, d, e } )", sbb.isLeftGB(L) );
    }


    /**
     * Test sequential twosided GBase.
     */
    public void testSequentialTSGBase() {
        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();

        L.add(a);
        L = sbb.twosidedGB( L );
        //System.out.println("L = " + L.size() );
        assertTrue("isTwosidedGB( { a } )", sbb.isTwosidedGB(L) );

        L.add(b);
        L = sbb.twosidedGB( L );
        //System.out.println("L = " + L.size() );
        assertTrue("isTwosidedGB( { a, b } )", sbb.isTwosidedGB(L) );

        L.add(c);
        L = sbb.twosidedGB( L );
        //System.out.println("L = " + L.size() );
        assertTrue("isTwosidedGB( { a, b, c } )", sbb.isTwosidedGB(L) );

        L.add(d);
        L = sbb.twosidedGB( L );
        //System.out.println("L = " + L.size() );
        assertTrue("isTwosidedGB( { a, b, c, d } )", sbb.isTwosidedGB(L) );

        L.add(e);
        L = sbb.twosidedGB( L );
        //System.out.println("L = " + L.size() );
        assertTrue("isTwosidedGB( { a, b, c, d, e } )", sbb.isTwosidedGB(L) );
    }



    /**
     * Test Weyl sequential twosided GBase
     * is always 1.
     */
    public void testWeylSequentialTSGBase() {
        //int rloc = 4;
        //ring = new GenSolvablePolynomialRing<BigInteger>(cfac,rloc);

        RelationGenerator<BigInteger> wl = new WeylRelations<BigInteger>();
        wl.generate(ring);
        table = ring.table;

        a = ring.random(kl, ll, el, q );
        b = ring.random(kl, ll, el, q );
        c = ring.random(kl, ll, el, q );
        d = ring.random(kl, ll, el, q );
        e = d; //ring.random(kl, ll, el, q );

        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();

        L.add(a);
        //System.out.println("La = " + L );
        L = sbb.twosidedGB( L );
        //System.out.println("L = " + L );
        assertTrue("isTwosidedGB( { a } )", sbb.isTwosidedGB(L) );

        L.add(b);
        L = sbb.twosidedGB( L );
        //System.out.println("L = " + L );
        assertTrue("isTwosidedGB( { a, b } )", sbb.isTwosidedGB(L) );

        L.add(c);
        L = sbb.twosidedGB( L );
        //System.out.println("L = " + L );
        assertTrue("isTwosidedGB( { a, b, c } )", sbb.isTwosidedGB(L) );

        L.add(d);
        L = sbb.twosidedGB( L );
        //System.out.println("L = " + L );
        assertTrue("isTwosidedGB( { a, b, c, d } )", sbb.isTwosidedGB(L) );

        L.add(e);
        L = sbb.twosidedGB( L );
        //System.out.println("L = " + L );
        assertTrue("isTwosidedGB( { a, b, c, d, e } )", sbb.isTwosidedGB(L) );
    }


    /*
     * Test sequential extended GBase.
    public void testSequentialExtendedGBase() {
        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();

        SolvableExtendedGB<BigInteger> exgb;

        L.add(a);
        //System.out.println("L = " + L );

        exgb = sbb.extLeftGB( L );
        //System.out.println("exgb = " + exgb );
        assertTrue("isLeftGB( { a } )", sbb.isLeftGB(exgb.G) );
        assertTrue("isLeftRmat( { a } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(b);
        //System.out.println("L = " + L );
        exgb = sbb.extLeftGB( L );
        //System.out.println("exgb = " + exgb );
        assertTrue("isLeftGB( { a, b } )", sbb.isLeftGB(exgb.G) );
        assertTrue("isLeftRmat( { a, b } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(c);
        exgb = sbb.extLeftGB( L );
        //System.out.println("exgb = " + exgb );
        assertTrue("isLeftGB( { a, b, c } )", sbb.isLeftGB(exgb.G) );
        assertTrue("isLeftRmat( { a, b, c } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(d);
        exgb = sbb.extLeftGB( L );
        //System.out.println("exgb = " + exgb );
        assertTrue("isLeftGB( { a, b, c, d } )", sbb.isLeftGB(exgb.G) );
        assertTrue("isLeftRmat( { a, b, c, d } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(e);
        exgb = sbb.extLeftGB( L );
        //System.out.println("exgb = " + exgb );
        assertTrue("isLeftGB( { a, b, c, d, e } )", sbb.isLeftGB(exgb.G) );
        assertTrue("isLeftRmat( { a, b, c, d, e } )", sbb.isLeftReductionMatrix(exgb) );
    }
     */


    /*
     * Test Weyl sequential extended GBase.
    public void testWeylSequentialExtendedGBase() {
        //int rloc = 4;
        //ring = new GenSolvablePolynomialRing<BigInteger>(cfac,rloc);

        RelationGenerator<BigInteger> wl = new WeylRelations<BigInteger>();
        wl.generate(ring);
        table = ring.table;

        a = ring.random(kl, ll, el, q );
        b = ring.random(kl, ll, el, q );
        c = ring.random(kl, ll, el, q );
        d = ring.random(kl, ll, el, q );
        e = d; //ring.random(kl, ll, el, q );

        SolvableExtendedGB<BigInteger> exgb;

        L = new ArrayList<GenSolvablePolynomial<BigInteger>>();

        L.add(a);
        exgb = sbb.extLeftGB( L );
        // System.out.println("exgb = " + exgb );
        assertTrue("isLeftGB( { a } )", sbb.isLeftGB(exgb.G) );
        assertTrue("isRmat( { a } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(b);
        //System.out.println("L = " + L.size() );
        exgb = sbb.extLeftGB( L );
        //System.out.println("exgb = " + exgb );
        assertTrue("isLeftGB( { a, b } )", sbb.isLeftGB(exgb.G) );
        assertTrue("isRmat( { a, b } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(c);
        exgb = sbb.extLeftGB( L );
        //System.out.println("exgb = " + exgb );
        assertTrue("isLeftGB( { a, b, c } )", sbb.isLeftGB(exgb.G) );
        assertTrue("isRmat( { a, b, c } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(d);
        exgb = sbb.extLeftGB( L );
        //System.out.println("exgb = " + exgb );
        assertTrue("isLeftGB( { a, b, c, d } )", sbb.isLeftGB(exgb.G) );
        assertTrue("isRmat( { a, b, c, d } )", sbb.isLeftReductionMatrix(exgb) );

        L.add(e);
        exgb = sbb.extLeftGB( L );
        //System.out.println("exgb = " + exgb );
        assertTrue("isLeftGB( { a, b, c, d, e } )", sbb.isLeftGB(exgb.G) );
        assertTrue("isRmat( { a, b, c, d, e } )", sbb.isLeftReductionMatrix(exgb) );
    }
     */

}
