/*
 * $Id$
 */

package edu.jas.gb;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.OrderedPolynomialList;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrderByName;


/**
 * Groebner base signature based sequential iterative GB tests with JUnit.
 * @author Heinz Kredel
 */

public class GroebnerBaseSigSeqIterTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GroebnerBaseSigSeqIterTest</CODE> object.
     * @param name String.
     */
    public GroebnerBaseSigSeqIterTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBaseSigSeqIterTest.class);
        return suite;
    }


    GenPolynomialRing<BigRational> fac;


    List<GenPolynomial<BigRational>> L, G, Gp;


    PolynomialList<BigRational> F;


    GroebnerBaseAbstract<BigRational> bb, bbsig, bbggv, bbarri, bbf5z;


    GenPolynomial<BigRational> a, b, c, d, e;


    int rl = 4; //4; //3; 


    int kl = 3; // 10


    int ll = 5;


    int el = 3; // 4


    float q = 0.2f; //0.4f


    @Override
    protected void setUp() {
        BigRational coeff = new BigRational(9);
        String[] vars = new String[] { "u", "x", "y", "z" };
        fac = new GenPolynomialRing<BigRational>(coeff, vars, TermOrderByName.IGRLEX);
        a = b = c = d = e = null;
        bb = new GroebnerBaseSeqIter<BigRational>();
        bbsig = new GroebnerBaseSigSeqIter<BigRational>();
        bbggv = new GroebnerBaseGGVSigSeqIter<BigRational>();
        bbarri = new GroebnerBaseArriSigSeqIter<BigRational>();
        bbf5z = new GroebnerBaseF5zSigSeqIter<BigRational>();
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bb = null;
    }


    /**
     * Test sequential GBase.
     */
    public void testSequentialGBase() {
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.parse("x^4 + 4/5 x^2 - 12/25 u * x - 183/175");
        b = fac.parse("x^3 * y + 40/7 x^3 + 4/5 x * y - 12/25 u * y + 183/2450 u^2 + 32/7 x - 96/35 u");
        c = fac.parse("u^2 * x + 14 y + 80");
        d = fac.parse("y^2 - 5/4 x^2 - 1");
        e = fac.parse("z");

        int x = (new Random()).nextInt(4);
        switch (x) {
        case 0:
            bb = bbf5z;
            break;
        case 1:
            bb = bbggv;
            break;
        case 2:
            bb = bbarri;
            break;
        default:
            break;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        L.add(d);
        L.add(e);
        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d, e } ): " + L, bb.isGB(L));
        //System.out.println("L = " + L);
    }


    /**
     * Test random sequential GBase.
     */
    public void testRandomSequentialGBase() {
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.univariate(0); //fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        int x = (new Random()).nextInt(4);
        switch (x) {
        case 0:
            bb = bbf5z;
            break;
        case 1:
            bb = bbggv;
            break;
        case 2:
            bb = bbarri;
            break;
        default:
            break;
        }

        L.add(a);
        L = bb.GB(L);
        assertTrue("isGB( { a } ): " + L, bb.isGB(L));

        L.add(b);
        //System.out.println("L = " + L.size() );
        L = bb.GB(L);
        assertTrue("isGB( { a, b } ): " + L, bb.isGB(L));

        L.add(c);
        L = bb.GB(L);
        assertTrue("isGB( { a, b, c } ): " + L, bb.isGB(L));

        L.add(d);
        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d } ): " + L, bb.isGB(L));

        L.add(e);
        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d, e } ): " + L, bb.isGB(L));
        //System.out.println("L = " + L);
    }


    /**
     * Test Trinks7 GBase.
     */
    @SuppressWarnings({ "unchecked", "cast" })
    public void testTrinks7GBase() {
        String exam = "(B,S,T,Z,P,W) L " + "( "
                        + "( P W + 2 T Z - 11 B**3 ), "
                        //+ "( B**2 + 33/50 B + 2673/10000 ) " 
                        + "( 45 P + 35 S - 165 B - 36 ), " + "( 35 P + 40 Z + 25 T - 27 S ), "
                        + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), " + "( - 9 W + 15 T P + 20 S Z ), "
                        + "( 99 W - 11 B S + 3 B**2 ), " + ") ";

        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        try {
            F = (PolynomialList<BigRational>) parser.nextPolynomialSet();
        } catch (ClassCastException e) {
            fail("" + e);
        } catch (IOException e) {
            fail("" + e);
        }
        //System.out.println("F = " + F);

        G = bb.GB(F.list);
        long t1 = System.currentTimeMillis();
        G = bb.GB(F.list);
        t1 = System.currentTimeMillis() - t1;
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(G));
        assertEquals("#GB(Trinks7) == 6", 6, G.size());
        G = OrderedPolynomialList.<BigRational> sort(G);

        long t2 = System.currentTimeMillis();
        Gp = G; //bbsig.GB(F.list);
        t2 = System.currentTimeMillis() - t2;
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(Gp));
        assertEquals("#GB(Trinks7) == 6", 6, Gp.size());
        Gp = OrderedPolynomialList.<BigRational> sort(Gp);
        assertEquals("GB == GBp", G, Gp);

        Gp = bbf5z.GB(F.list);
        long t5 = System.currentTimeMillis();
        Gp = bbf5z.GB(F.list);
        t5 = System.currentTimeMillis() - t5;
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(Gp));
        assertEquals("#GB(Trinks7) == 6", 6, Gp.size());
        Gp = OrderedPolynomialList.<BigRational> sort(Gp);
        assertEquals("GB == GBp", G, Gp);

        Gp = bbarri.GB(F.list);
        long t4 = System.currentTimeMillis();
        Gp = bbarri.GB(F.list);
        t4 = System.currentTimeMillis() - t4;
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(Gp));
        assertEquals("#GB(Trinks7) == 6", 6, Gp.size());
        Gp = OrderedPolynomialList.<BigRational> sort(Gp);
        assertEquals("GB == GBp", G, Gp);

        Gp = bbggv.GB(F.list);
        long t3 = System.currentTimeMillis();
        Gp = bbggv.GB(F.list);
        t3 = System.currentTimeMillis() - t3;
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(Gp));
        assertEquals("#GB(Trinks7) == 6", 6, Gp.size());
        Gp = OrderedPolynomialList.<BigRational> sort(Gp);
        assertEquals("GB == GBp", G, Gp);

        //System.out.println("G = " + G);
        //System.out.println("iter  executed in " + t1 + " milliseconds");
        ////System.out.println("sig   executed in " + t2 + " milliseconds");
        //System.out.println("ggv   executed in " + t3 + " milliseconds");
        //System.out.println("arris executed in " + t4 + " milliseconds");
        //System.out.println("f5z   executed in " + t5 + " milliseconds");
        long t = t1 + t2 + t3 + t4 + t5;
        assertTrue("times >= 0: " + t, t >= 0); //findbugs and compiler

        assertTrue("isGB( GB(Trinks7) )", bb.isGB(G));
        assertEquals("#GB(Trinks7) == 6", 6, G.size());
        //PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring,G);
        //System.out.println("G = " + trinks);
    }

}
