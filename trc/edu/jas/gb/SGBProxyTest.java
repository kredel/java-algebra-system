/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.RelationGenerator;
import edu.jas.poly.RelationTable;
import edu.jas.poly.TermOrder;
import edu.jas.poly.WeylRelations;


/**
 * SolvableGroebnerBase proxy of sequential and parallel tests with JUnit.
 * @author Heinz Kredel
 */

public class SGBProxyTest extends TestCase {



    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>SGBProxyTest</CODE> object.
     * @param name String.
     */
    public SGBProxyTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SGBProxyTest.class);
        return suite;
    }


    GenSolvablePolynomial<BigRational> a, b, c, d, e;


    List<GenSolvablePolynomial<BigRational>> L;


    PolynomialList<BigRational> F, G;


    GenSolvablePolynomialRing<BigRational> ring;


    SolvableGroebnerBaseAbstract<BigRational> sbb;


    BigRational cfac;


    TermOrder tord;


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
        ring = new GenSolvablePolynomialRing<BigRational>(cfac, rl, tord);
        table = ring.table;
        a = b = c = d = e = null;
        int nt = ComputerThreads.N_CPUS - 1;
        SolvableGroebnerBaseAbstract<BigRational> bb1, bb2;
        bb1 = new SolvableGroebnerBaseSeq<BigRational>();
        bb2 = new SolvableGroebnerBaseParallel<BigRational>(nt);
        sbb = new SGBProxy<BigRational>(bb1, bb2);

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
        table = null;
        cfac = null;
        sbb.terminate();
        sbb = null;
    }


    /**
     * Test proxy GBase.
     */
    public void testProxyGBase() {
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
     * Test Weyl proxy GBase.
     */
    public void testWeylProxyGBase() {
        int rloc = 4;
        ring = new GenSolvablePolynomialRing<BigRational>(cfac, rloc);

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
     * Test proxy twosided GBase.
     */
    public void testProxyTSGBase() {
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
     * Test Weyl proxy twosided GBase is always 1.
     */
    public void testWeylProxyTSGBase() {
        int rloc = 4;
        ring = new GenSolvablePolynomialRing<BigRational>(cfac, rloc);

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

}
