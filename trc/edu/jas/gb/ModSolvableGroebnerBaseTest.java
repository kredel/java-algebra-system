/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.ModuleList;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.RelationGenerator;
import edu.jas.poly.RelationTable;
import edu.jas.poly.TermOrder;
import edu.jas.poly.WeylRelations;


/**
 * ModSolvableGroebnerBase sequential and parallel tests with JUnit.
 * @author Heinz Kredel
 */

public class ModSolvableGroebnerBaseTest extends TestCase {


    private static final Logger logger = LogManager.getLogger(ModSolvableGroebnerBaseTest.class);


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>ModSolvableGroebnerBaseTest</CODE> object.
     * @param name String.
     */
    public ModSolvableGroebnerBaseTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ModSolvableGroebnerBaseTest.class);
        return suite;
    }


    int port = 4711;


    BigRational cfac;


    GenSolvablePolynomialRing<BigRational> pfac;


    GenSolvablePolynomial<BigRational> a, b, c, d, e;


    TermOrder tord;


    GenSolvablePolynomial<BigRational> one, zero;


    RelationTable<BigRational> table;


    List<List<GenSolvablePolynomial<BigRational>>> L;


    List<GenSolvablePolynomial<BigRational>> V;


    PolynomialList<BigRational> F, G;


    ModuleList<BigRational> M, N, K, I;


    SolvableGroebnerBaseAbstract<BigRational> sbb;


    SolvableReductionAbstract<BigRational> sred;


    int rl = 3; //4; //3; 


    int kl = 4;


    int ll = 3;


    int el = 2;


    float q = 0.2f; //0.4f


    @Override
    protected void setUp() {
        cfac = new BigRational(1);
        tord = new TermOrder();
        pfac = new GenSolvablePolynomialRing<BigRational>(cfac, rl, tord);
        if (Math.random() > 0.5) {
            sbb = new SolvableGroebnerBaseSeq<BigRational>(); //cfac);
        } else {
            sbb = new SolvableGroebnerBaseParallel<BigRational>(); //cfac);
        }
        sred = new SolvableReductionSeq<BigRational>();
        logger.info("test with " + sbb.getClass().getSimpleName());

        a = b = c = d = e = null;
        do {
            a = pfac.random(kl, ll, el, q);
            b = pfac.random(kl, ll, el, q);
            c = pfac.random(kl, ll, el, q);
            d = pfac.random(kl, ll, el, q);
        } while (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO());
        e = d; // = pfac.random(kl, ll, el, q );
        one = pfac.getONE();
        zero = pfac.getZERO();
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        one = null;
        zero = null;
        sbb.terminate();
        sbb = null;
        sred = null;
    }


    /**
     * Test sequential left GBase.
     */
    public void testSequentialLeftModSolvableGB() {
        L = new ArrayList<List<GenSolvablePolynomial<BigRational>>>();

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(a);
        V.add(zero);
        V.add(one);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        assertTrue("isLeftGB( { (a,0,1) } )", sbb.isLeftGB(M));
        //System.out.println("M = " + M );

        N = sbb.leftGB(M);
        //System.out.println("N = " + N );
        assertTrue("isLeftGB( { (a,0,1) } )", sbb.isLeftGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b);
        V.add(one);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        N = sbb.leftGB(M);
        assertTrue("isLeftGB( { (a,0,1),(b,1,0) } )", sbb.isLeftGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(c);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = sbb.leftGB(M);
        assertTrue("isLeftGB( { (a,),(b,),(c,) } )", sbb.isLeftGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(d);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = sbb.leftGB(M);
        assertTrue("isLeftGB( { (a,b,c,d) } )", sbb.isLeftGB(N));
        //System.out.println("N = " + N );

    }


    /**
     * Test sequential left Weyl GBase.
     */
    public void testSequentialLeftModSolvableWeylGB() {
        int rloc = 4;
        pfac = new GenSolvablePolynomialRing<BigRational>(cfac, rloc, tord);
        //System.out.println("pfac = " + pfac);
        //System.out.println("pfac end");

        RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
        //System.out.println("wl = ");
        wl.generate(pfac);
        //System.out.println("generate = ");
        table = pfac.table;
        //System.out.println("table = ");
        //System.out.println("table = " + table.size());

        do {
            a = pfac.random(kl, ll, el, q);
            b = pfac.random(kl, ll, el, q);
            c = pfac.random(kl, ll, el, q);
            d = pfac.random(kl, ll, el, q);
        } while (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO());
        e = d; // = pfac.random(kl, ll, el, q );
        one = pfac.getONE();
        zero = pfac.getZERO();
        //System.out.println("a = " + a );
        //System.out.println("b = " + b );
        //System.out.println("c = " + c );
        //System.out.println("d = " + d );
        //System.out.println("e = " + e );

        L = new ArrayList<List<GenSolvablePolynomial<BigRational>>>();

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(a);
        V.add(zero);
        V.add(one);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        assertTrue("isLeftGB( { (a,0,1) } )", sbb.isLeftGB(M));

        N = sbb.leftGB(M);
        assertTrue("isLeftGB( { (a,0,1) } )", sbb.isLeftGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b);
        V.add(one);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        N = sbb.leftGB(M);
        assertTrue("isLeftGB( { (a,0,1),(b,1,0) } )", sbb.isLeftGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(c);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = sbb.leftGB(M);
        assertTrue("isLeftGB( { (a,),(b,),(c,) } )", sbb.isLeftGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(d);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = sbb.leftGB(M);
        assertTrue("isLeftGB( { (a,b,c,d) } )", sbb.isLeftGB(N));
        //System.out.println("N = " + N );
    }


    /**
     * Test sequential twosided GBase.
     */
    public void testSequentialTSModSolvableGB() {
        L = new ArrayList<List<GenSolvablePolynomial<BigRational>>>();

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(a);
        V.add(zero);
        V.add(one);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        assertTrue("isTwosidedGB( { (a,0,1) } )", sbb.isTwosidedGB(M));

        N = sbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,0,1) } )", sbb.isTwosidedGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b);
        V.add(one);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        N = sbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,0,1),(b,1,0) } )", sbb.isTwosidedGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(c);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        N = sbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,),(b,),(c,) } )", sbb.isTwosidedGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(d);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        N = sbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,b,c,d) } )", sbb.isTwosidedGB(N));
        //System.out.println("N = " + N );
    }


    /**
     * Test sequential twosided Weyl GBase.
     */
    public void testSequentialTSModSolvableWeylGB() {
        int rloc = 4;
        pfac = new GenSolvablePolynomialRing<BigRational>(cfac, rloc, tord);
        //System.out.println("pfac = " + pfac);
        //System.out.println("pfac end");

        RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
        //System.out.println("wl = ");
        wl.generate(pfac);
        //System.out.println("generate = ");
        table = pfac.table;
        //System.out.println("table = ");
        //System.out.println("table = " + table.size());

        do {
            a = pfac.random(kl, ll, el, q);
            b = pfac.random(kl, ll, el, q);
            c = pfac.random(kl, ll, el, q);
            d = pfac.random(kl, ll, el, q);
        } while (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO());
        e = d; // = pfac.random(kl, ll, el, q );
        one = pfac.getONE();
        zero = pfac.getZERO();
        //System.out.println("a = " + a );
        //System.out.println("b = " + b );
        //System.out.println("c = " + c );
        //System.out.println("d = " + d );
        //System.out.println("e = " + e );

        L = new ArrayList<List<GenSolvablePolynomial<BigRational>>>();

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(a);
        V.add(zero);
        V.add(one);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        // not true in general
        assertTrue("isTwosidedGB( { (a,0,1) } )", sbb.isTwosidedGB(M) || !pfac.isCommutative());

        N = sbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,0,1) } )", sbb.isTwosidedGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b);
        V.add(one);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        N = sbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,0,1),(b,1,0) } )", sbb.isTwosidedGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(c);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = sbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,),(b,),(c,) } )", sbb.isTwosidedGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(d);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = sbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,b,c,d) } )", sbb.isTwosidedGB(N));
        //System.out.println("N = " + N );
    }


    /**
     * Test sequential right GBase.
     */
    public void testSequentialRightModSolvableGB() {
        L = new ArrayList<List<GenSolvablePolynomial<BigRational>>>();

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(a);
        V.add(zero);
        V.add(one);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        assertTrue("isRightGB( { (a,0,1) } )", sbb.isRightGB(M));
        //System.out.println("M = " + M );

        N = sbb.rightGB(M);
        //System.out.println("N = " + N );
        assertTrue("isRightGB( { (a,0,1) } )", sbb.isRightGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b);
        V.add(one);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        //System.out.println("M = " + M );
        N = sbb.rightGB(M);
        //System.out.println("N = " + N );
        assertTrue("isRightGB( { (a,0,1),(b,1,0) } )", sbb.isRightGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(c);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = sbb.rightGB(M);
        assertTrue("isRightGB( { (a,),(b,),(c,) } )", sbb.isRightGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(d);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = sbb.rightGB(M);
        assertTrue("isRightGB( { (a,b,c,d) } )", sbb.isRightGB(N));
        //System.out.println("N = " + N );
    }


    /**
     * Test sequential right Weyl GBase.
     */
    public void testSequentialRightModSolvableWeylGB() {
        int rloc = 4;
        pfac = new GenSolvablePolynomialRing<BigRational>(cfac, rloc, tord);
        //System.out.println("pfac = " + pfac);
        //System.out.println("pfac end");

        RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
        //System.out.println("wl = ");
        wl.generate(pfac);
        //System.out.println("generate = ");
        table = pfac.table;
        //System.out.println("table = ");
        //System.out.println("table = " + table.size());

        do {
            a = pfac.random(kl, ll, el, q);
            b = pfac.random(kl, ll, el, q);
            c = pfac.random(kl, ll, el, q);
            d = pfac.random(kl, ll, el, q);
        } while (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO());
        e = d; // = pfac.random(kl, ll, el, q );
        one = pfac.getONE();
        zero = pfac.getZERO();
        //System.out.println("a = " + a );
        //System.out.println("b = " + b );
        //System.out.println("c = " + c );
        //System.out.println("d = " + d );
        //System.out.println("e = " + e );

        L = new ArrayList<List<GenSolvablePolynomial<BigRational>>>();

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(a);
        V.add(zero);
        V.add(one);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        assertTrue("isRightGB( { (a,0,1) } )", sbb.isRightGB(M));

        N = sbb.rightGB(M);
        assertTrue("isRightGB( { (a,0,1) } )", sbb.isRightGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b);
        V.add(one);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        //System.out.println("M = " + M );
        N = sbb.rightGB(M);
        //System.out.println("N = " + N );
        assertTrue("isRightGB( { (a,0,1),(b,1,0) } )", sbb.isRightGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(c);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = sbb.rightGB(M);
        assertTrue("isRightGB( { (a,),(b,),(c,) } )", sbb.isRightGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(d);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = sbb.rightGB(M);
        assertTrue("isRightGB( { (a,b,c,d) } )", sbb.isRightGB(N));
        //System.out.println("N = " + N );
    }


    /**
     * Test sequential left GBase with TOP and POT term order.
     */
    public void testSequentialModTOPleftGB() {
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        L = new ArrayList<List<GenSolvablePolynomial<BigRational>>>();
        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(a);
        V.add(pfac.getZERO());
        V.add(pfac.getONE());
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        assertTrue("isGB( { (a,0,1) } )", sbb.isLeftGB(M));

        N = sbb.leftGB(M);
        assertTrue("is( { (a,0,1) } )", sbb.isLeftGB(N));

        K = sbb.leftGB(M, true);
        assertTrue("is( { (a,0,1) } )", sbb.isLeftGB(K, true));
        assertEquals("N == K", N, K);

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b);
        V.add(pfac.getONE());
        V.add(pfac.getZERO());
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M);

        N = sbb.leftGB(M);
        assertTrue("isGB( { (a,0,1),(b,1,0) } )", sbb.isLeftGB(N));
        //System.out.println("N = " + N);

        K = sbb.leftGB(M,true);
        assertTrue("is( { (a,0,1) } )", sbb.isLeftGB(K, true));
        //System.out.println("K = " + K);

        I = sred.leftNormalform(N, K);
        //System.out.println("I = " + I);
        assertTrue("N.lnf(K) == (0)", I.isZERO());

        I = sred.leftNormalform(K, N, true);
        //System.out.println("I = " + I);
        assertTrue("K.lnf(N) == (0)", I.isZERO());
    }

    
    /**
     * Test sequential twosided GBase with TOP and POT term order.
     */
    public void testSequentialModTOPtwosidedGB() {
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        L = new ArrayList<List<GenSolvablePolynomial<BigRational>>>();
        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(a);
        V.add(pfac.getZERO());
        V.add(pfac.getONE());
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        assertTrue("isGB( { (a,0,1) } )", sbb.isTwosidedGB(M));

        N = sbb.twosidedGB(M);
        assertTrue("is( { (a,0,1) } )", sbb.isTwosidedGB(N));

        K = sbb.twosidedGB(M, true);
        assertTrue("is( { (a,0,1) } )", sbb.isTwosidedGB(K, true));
        assertEquals("N == K", N, K);

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b);
        V.add(pfac.getONE());
        V.add(pfac.getZERO());
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M.toScript());

        N = sbb.twosidedGB(M);
        assertTrue("isGB( { (a,0,1),(b,1,0) } )", sbb.isTwosidedGB(N));
        //System.out.println("N = " + N);

        K = sbb.twosidedGB(M,true);
        assertTrue("is( { (a,0,1) } )", sbb.isTwosidedGB(K, true));
        //System.out.println("K = " + K);

        I = sred.leftNormalform(N, K);
        //System.out.println("I = " + I);
        assertTrue("N.lnf(K) == (0)", I.isZERO());

        I = sred.leftNormalform(K, N, true);
        //System.out.println("I = " + I);
        assertTrue("K.lnf(N) == (0)", I.isZERO());
    }

    
    /*
     * No TOP term order for sequential right GBase.
     */

}
