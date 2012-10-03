/*
 * $Id$
 */

package edu.jas.gb;


//import edu.jas.poly.GroebnerBase;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator; //import org.apache.log4j.Logger;

import edu.jas.kern.ComputerThreads;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.RingElem;
import edu.jas.util.ExecutableServer;


/**
 * Distributed hybrid GroebnerBase tests with JUnit.
 * @author Heinz Kredel
 */

public class GroebnerBaseDistHybridECTest extends TestCase {


    //private static final Logger logger = Logger.getLogger(GroebnerBaseDistHybridECTest.class);

    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        //ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>GroebnerBaseDistHybridECTest</CODE> object.
     * @param name String.
     */
    public GroebnerBaseDistHybridECTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBaseDistHybridECTest.class);
        return suite;
    }


    int port = 55711;


    String host = "localhost";


    String mfile = "examples/machines.localhost"; // contains localhost


    GenPolynomialRing<BigRational> fac;


    List<GenPolynomial<BigRational>> L;


    PolynomialList<BigRational> F;


    List<GenPolynomial<BigRational>> G;


    GroebnerBase<BigRational> bbseq;


    GroebnerBaseAbstract<BigRational> bbdist;


    GroebnerBaseAbstract<BigRational> bbdists;


    GenPolynomial<BigRational> a;


    GenPolynomial<BigRational> b;


    GenPolynomial<BigRational> c;


    GenPolynomial<BigRational> d;


    GenPolynomial<BigRational> e;


    int rl = 3; //4; //3; 


    int kl = 4;


    int ll = 7;


    int el = 3;


    float q = 0.2f; //0.4f


    int threads = 2;


    ExecutableServer es1;


    ExecutableServer es2;


    @Override
    protected void setUp() {
        es1 = new ExecutableServer(4712); // == machines.localhost:4712
        es1.init();
        es2 = new ExecutableServer(4711); // == machines.localhost:4711
        es2.init();
        BigRational coeff = new BigRational(9);
        fac = new GenPolynomialRing<BigRational>(coeff, rl);
        a = b = c = d = e = null;
        bbseq = new GroebnerBaseSeq<BigRational>();
        bbdist = new GroebnerBaseDistributedHybridEC<BigRational>(mfile, threads, port);
        //bbdists = new GroebnerBaseDistributedHybridEC<BigRational>(mfile, threads, new OrderedSyzPairlist<BigRational>(), port);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bbseq = null;
        bbdist.terminate();
        bbdist = null;
        //bbdists.terminate();
        //bbdists = null;
        es1.terminate();
        es2.terminate();
        es1 = null;
        es2 = null;
        ComputerThreads.terminate();
    }


    /**
     * Test distributed GBase.
     */
    public void testDistributedGBase() {
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        L.add(a);
        L = bbdist.GB(L);
        assertTrue("isGB( { a } )", bbseq.isGB(L));

        L.add(b);
        //System.out.println("L = " + L.size() );
        L = bbdist.GB(L);
        assertTrue("isGB( { a, b } )", bbseq.isGB(L));

        L.add(c);
        L = bbdist.GB(L);
        assertTrue("isGB( { a, b, c } )", bbseq.isGB(L));

        L.add(d);
        L = bbdist.GB(L);
        assertTrue("isGB( { a, b, c, d } )", bbseq.isGB(L));

        L.add(e);
        L = bbdist.GB(L);
        assertTrue("isGB( { a, b, c, d, e } )", bbseq.isGB(L));
    }


    /**
     * Test compare sequential with distributed GBase.
     */
    public void testSequentialDistributedGBase() {
        List<GenPolynomial<BigRational>> Gs, Gp = null;
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        L.add(a);
        Gs = bbseq.GB(L);
        Gp = bbdist.GB(L);
        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp + ", " + L, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp + ", " + L, Gp.containsAll(Gs));

        L = Gs;
        L.add(b);
        Gs = bbseq.GB(L);
        Gp = bbdist.GB(L);
        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp + ", " + L, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp + ", " + L, Gp.containsAll(Gs));

        L = Gs;
        L.add(c);
        Gs = bbseq.GB(L);
        Gp = bbdist.GB(L);
        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp + ", " + L, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp + ", " + L, Gp.containsAll(Gs));

        L = Gs;
        L.add(d);
        Gs = bbseq.GB(L);
        Gp = bbdist.GB(L);
        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp + ", " + L, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp + ", " + L, Gp.containsAll(Gs));

        L = Gs;
        L.add(e);
        Gs = bbseq.GB(L);
        Gp = bbdist.GB(L);
        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp + ", " + L, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp + ", " + L, Gp.containsAll(Gs));
    }


    /**
     * Test Trinks7 GBase.
     */
    public void testTrinks7GBase() {
        String exam = "(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                + "( 99 W - 11 B S + 3 B**2 ), " + "( B**2 + 33/50 B + 2673/10000 ) " + ") ";
        //exam = "(x3,x4,x5) L " + 
        //        "( (x3^2 - 13974703710478159/3775194259200) , (x4 - 34297/840), (x5^2 - 6389/480), (-4/3 x5^2 + x3^2 + x3 - 833/180) ) ";
        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        try {
            F = (PolynomialList<BigRational>) parser.nextPolynomialSet();
        } catch (IOException e) {
            fail("" + e);
        }
        System.out.println("F = " + F);

        G = bbdist.GB(F.list);
        System.out.println("G = " + G);
        G = bbseq.GB(F.list);
        System.out.println("G = " + G);

        assertTrue("isGB( GB(Trinks7) )", bbseq.isGB(G));
        assertEquals("#GB(Trinks7) == 6", 6, G.size());
        //PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring, G);
        //System.out.println("G = " + trinks);
    }

}
