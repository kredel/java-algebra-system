/*
 * $Id$
 */

package edu.jas.gb;


// import edu.jas.poly.GroebnerBase;

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


    String host = "localhost";


    BigRational cfac;


    GenSolvablePolynomialRing<BigRational> pfac;


    GenSolvablePolynomial<BigRational> a, b, c, d, e;


    TermOrder tord;


    GenSolvablePolynomial<BigRational> one, zero;


    RelationTable<BigRational> table;


    List<List<GenSolvablePolynomial<BigRational>>> L;


    List<GenSolvablePolynomial<BigRational>> V;


    PolynomialList<BigRational> F, G;


    ModuleList<BigRational> M, N;


    SolvableGroebnerBaseAbstract<BigRational> msbb;


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
            msbb = new SolvableGroebnerBaseSeq<BigRational>(); //cfac);
        } else {
            msbb = new SolvableGroebnerBaseParallel<BigRational>(); //cfac);
        }
        logger.info("test with " + msbb.getClass().getSimpleName());

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
        msbb.terminate();
        msbb = null;
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
        assertTrue("isLeftGB( { (a,0,1) } )", msbb.isLeftGB(M));
        //System.out.println("M = " + M );

        N = msbb.leftGB(M);
        //System.out.println("N = " + N );
        assertTrue("isLeftGB( { (a,0,1) } )", msbb.isLeftGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b);
        V.add(one);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        N = msbb.leftGB(M);
        assertTrue("isLeftGB( { (a,0,1),(b,1,0) } )", msbb.isLeftGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(c);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = msbb.leftGB(M);
        assertTrue("isLeftGB( { (a,),(b,),(c,) } )", msbb.isLeftGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(d);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = msbb.leftGB(M);
        assertTrue("isLeftGB( { (a,b,c,d) } )", msbb.isLeftGB(N));
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
        assertTrue("isLeftGB( { (a,0,1) } )", msbb.isLeftGB(M));

        N = msbb.leftGB(M);
        assertTrue("isLeftGB( { (a,0,1) } )", msbb.isLeftGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b);
        V.add(one);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        N = msbb.leftGB(M);
        assertTrue("isLeftGB( { (a,0,1),(b,1,0) } )", msbb.isLeftGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(c);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = msbb.leftGB(M);
        assertTrue("isLeftGB( { (a,),(b,),(c,) } )", msbb.isLeftGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(d);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = msbb.leftGB(M);
        assertTrue("isLeftGB( { (a,b,c,d) } )", msbb.isLeftGB(N));
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
        assertTrue("isTwosidedGB( { (a,0,1) } )", msbb.isTwosidedGB(M));

        N = msbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,0,1) } )", msbb.isTwosidedGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b);
        V.add(one);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        N = msbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,0,1),(b,1,0) } )", msbb.isTwosidedGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(c);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        N = msbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,),(b,),(c,) } )", msbb.isTwosidedGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(d);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        N = msbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,b,c,d) } )", msbb.isTwosidedGB(N));
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
        assertTrue("isTwosidedGB( { (a,0,1) } )", msbb.isTwosidedGB(M) || !pfac.isCommutative());

        N = msbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,0,1) } )", msbb.isTwosidedGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b);
        V.add(one);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        N = msbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,0,1),(b,1,0) } )", msbb.isTwosidedGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(c);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = msbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,),(b,),(c,) } )", msbb.isTwosidedGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(d);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = msbb.twosidedGB(M);
        assertTrue("isTwosidedGB( { (a,b,c,d) } )", msbb.isTwosidedGB(N));
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
        assertTrue("isRightGB( { (a,0,1) } )", msbb.isRightGB(M));
        //System.out.println("M = " + M );

        N = msbb.rightGB(M);
        //System.out.println("N = " + N );
        assertTrue("isRightGB( { (a,0,1) } )", msbb.isRightGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b);
        V.add(one);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        //System.out.println("M = " + M );
        N = msbb.rightGB(M);
        //System.out.println("N = " + N );
        assertTrue("isRightGB( { (a,0,1),(b,1,0) } )", msbb.isRightGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(c);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = msbb.rightGB(M);
        assertTrue("isRightGB( { (a,),(b,),(c,) } )", msbb.isRightGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(d);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = msbb.rightGB(M);
        assertTrue("isRightGB( { (a,b,c,d) } )", msbb.isRightGB(N));
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
        assertTrue("isRightGB( { (a,0,1) } )", msbb.isRightGB(M));

        N = msbb.rightGB(M);
        assertTrue("isRightGB( { (a,0,1) } )", msbb.isRightGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b);
        V.add(one);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("L = " + L.size() );

        //System.out.println("M = " + M );
        N = msbb.rightGB(M);
        //System.out.println("N = " + N );
        assertTrue("isRightGB( { (a,0,1),(b,1,0) } )", msbb.isRightGB(N));

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(c);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = msbb.rightGB(M);
        assertTrue("isRightGB( { (a,),(b,),(c,) } )", msbb.isRightGB(N));
        //System.out.println("N = " + N );

        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(d);
        V.add(zero);
        V.add(zero);
        L.add(V);
        M = new ModuleList<BigRational>(pfac, L);
        //System.out.println("M = " + M );
        //System.out.println("L = " + L.size() );

        N = msbb.rightGB(M);
        assertTrue("isRightGB( { (a,b,c,d) } )", msbb.isRightGB(N));
        //System.out.println("N = " + N );
    }

}
