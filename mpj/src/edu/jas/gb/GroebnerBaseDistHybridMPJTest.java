/*
 * $Id$
 */

package edu.jas.gb;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import mpi.Comm;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.kern.MPJEngine;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;


/**
 * Distributed GroebnerBase MPJ tests with JUnit.
 * @author Heinz Kredel
 */

public class GroebnerBaseDistHybridMPJTest extends TestCase {


    protected static Comm engine;


    boolean mpjBug = true; // bug after cancel recv


    /**
     * main
     */
    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        engine = MPJEngine.getCommunicator(args);
        junit.textui.TestRunner.run(suite());
        engine.Barrier();
        MPJEngine.terminate();
        //ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>GroebnerBaseDistHybridMPJTest</CODE> object.
     * @param name String.
     */
    public GroebnerBaseDistHybridMPJTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBaseDistHybridMPJTest.class);
        return suite;
    }


    int port = 4711;


    String host = "localhost";


    GenPolynomialRing<BigRational> fac;


    List<GenPolynomial<BigRational>> L;


    PolynomialList<BigRational> F;


    List<GenPolynomial<BigRational>> G;


    GroebnerBase<BigRational> bbseq;


    GroebnerBaseDistributedHybridMPJ<BigRational> bbdist;


    GroebnerBaseDistributedHybridMPJ<BigRational> bbdists;


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


    int threads;


    int threadsPerNode = 3;


    @Override
    protected void setUp() {
        try {
            threads = engine.Size();
            BigRational coeff = new BigRational(9);
            fac = new GenPolynomialRing<BigRational>(coeff, rl);
            a = b = c = d = e = null;
            bbseq = new GroebnerBaseSeq<BigRational>();
            bbdist = new GroebnerBaseDistributedHybridMPJ<BigRational>(threads, threadsPerNode);
            //bbdists = new GroebnerBaseDistributedHybridMPJ<BigRational>(threads,threadsPerNode, new OrderedSyzPairlist<BigRational>());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bbseq = null;
        bbdist.terminate();
        bbdist = null;
        //bbdists.terminate();
        bbdists = null;
        ComputerThreads.terminate();
    }


    /**
     * Test distributed GBase.
     */
    public void onlyOnetestDistributedGBase() {
        L = new ArrayList<GenPolynomial<BigRational>>();
        if (engine.Rank() == 0) {
            a = fac.random(kl, ll, el, q);
            b = fac.random(kl, ll, el, q);
            c = fac.random(kl, ll, el, q);
            d = fac.random(kl, ll, el, q);
            e = d; //fac.random(kl, ll, el, q );
        }
        if (engine.Rank() == 0) {
            L.add(a);
            L.add(b);
            System.out.println("L = " + L);
        }

        L = bbdist.GB(L);
        if (engine.Rank() == 0) {
            System.out.println("L0 = " + L);
            assertTrue("isGB( { a } )", bbseq.isGB(L));
            L.add(b);
        }
        if (mpjBug) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        L = bbdist.GB(L);
        if (engine.Rank() == 0) {
            System.out.println("L1 = " + L);
            assertTrue("isGB( { a, b } )", bbseq.isGB(L));
            L.add(c);
        }

        L = bbdist.GB(L);
        if (engine.Rank() == 0) {
            System.out.println("L2 = " + L);
            assertTrue("isGB( { a, b, c } )", bbseq.isGB(L));
            L.add(d);
        }

        L = bbdist.GB(L);
        if (engine.Rank() == 0) {
            System.out.println("L3 = " + L);
            assertTrue("isGB( { a, b, c, d } )", bbseq.isGB(L));
            L.add(e);
        }
        L = bbdist.GB(L);
        if (engine.Rank() == 0) {
            System.out.println("L4 = " + L);
            assertTrue("isGB( { a, b, c, d, e } )", bbseq.isGB(L));
        } else {
            System.out.println("rank = " + engine.Rank());
        }
    }


    /**
     * Test Trinks7 GBase.
     */
    public void testTrinks7GBase() {
        List<GenPolynomial<BigRational>> Fl;
        long t = 0;
        if (engine.Rank() == 0) {
            String exam = "(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                            + "( 35 P + 40 Z + 25 T - 27 S ), "
                            + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                            + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                            + "( 99 W - 11 B S + 3 B**2 ), " + "( B**2 + 33/50 B + 2673/10000 ) " + ") ";
            Reader source = new StringReader(exam);
            GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
            try {
                F = (PolynomialList<BigRational>) parser.nextPolynomialSet();
            } catch (IOException e) {
                fail("" + e);
            }
            System.out.println("F = " + F);
            Fl = F.list;
            t = System.currentTimeMillis();
        } else {
            Fl = null;
        }

        G = bbdist.GB(Fl);

        if (engine.Rank() == 0) {
            t = System.currentTimeMillis() - t;
            assertTrue("isGB( GB(Trinks7) )", bbseq.isGB(G));
            assertEquals("#GB(Trinks7) == 6", 6, G.size());
            //PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring, G);
            System.out.println("G = " + G);
            System.out.println("executed in " + t + " milliseconds");
        } else {
            assertTrue("G == null: ", G == null);
        }
    }

}
