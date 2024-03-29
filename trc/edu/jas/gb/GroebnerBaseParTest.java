/*
 * $Id$
 */

package edu.jas.gb;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * GroebnerBase parallel tests with JUnit.
 * @author Heinz Kredel
 */

public class GroebnerBaseParTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GroebnerBaseParTest</CODE> object.
     * @param name String.
     */
    public GroebnerBaseParTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBaseParTest.class);
        return suite;
    }


    GenPolynomialRing<BigRational> fac;


    List<GenPolynomial<BigRational>> L, G;


    PolynomialList<BigRational> F;


    GroebnerBase<BigRational> bbseq;


    GroebnerBase<BigRational> bbpar;


    GenPolynomial<BigRational> a, b, c, d, e;


    int rl = 3; //4; //3; 


    int kl = 10;


    int ll = 7;


    int el = 3;


    float q = 0.2f; //0.4f


    int threads = 2;


    @Override
    protected void setUp() {
        BigRational coeff = new BigRational(9);
        fac = new GenPolynomialRing<BigRational>(coeff, rl);
        a = b = c = d = e = null;
        bbseq = new GroebnerBaseSeq<BigRational>();
        bbpar = new GroebnerBaseParallel<BigRational>(threads);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bbseq = null;
        ((GroebnerBaseParallel<BigRational>) bbpar).terminate();
        bbpar = null;
    }


    /**
     * Test parallel GBase.
     */
    public void testParallelGBase() {

        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);

        L = bbpar.GB(L);
        assertTrue("isGB( { a } )", bbpar.isGB(L));

        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        //System.out.println("L = " + L.size() );

        L = bbpar.GB(L);
        assertTrue("isGB( { a, b } )", bbpar.isGB(L));

        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);

        L = bbpar.GB(L);
        assertTrue("isGB( { a, b, c } )", bbpar.isGB(L));

        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);

        L = bbpar.GB(L);
        assertTrue("isGB( { a, b, c, d } )", bbpar.isGB(L));

        assertTrue("not isZERO( e )", !e.isZERO());
        L.add(e);

        L = bbpar.GB(L);
        assertTrue("isGB( { a, b, c, d, e } )", bbpar.isGB(L));
    }


    /**
     * Test compare sequential with parallel GBase.
     */
    public void testSequentialParallelGBase() {

        List<GenPolynomial<BigRational>> Gs, Gp;

        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        L.add(a);
        Gs = bbseq.GB(L);
        Gp = bbpar.GB(L);

        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp, Gp.containsAll(Gs));

        L = Gs;
        L.add(b);
        Gs = bbseq.GB(L);
        Gp = bbpar.GB(L);

        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp, Gp.containsAll(Gs));

        L = Gs;
        L.add(c);
        Gs = bbseq.GB(L);
        Gp = bbpar.GB(L);

        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp, Gp.containsAll(Gs));

        L = Gs;
        L.add(d);
        Gs = bbseq.GB(L);
        Gp = bbpar.GB(L);

        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp, Gp.containsAll(Gs));

        L = Gs;
        L.add(e);
        Gs = bbseq.GB(L);
        Gp = bbpar.GB(L);

        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp, Gp.containsAll(Gs));
    }


    /**
     * Test Trinks7 GBase.
     */
    @SuppressWarnings("unchecked")
    public void testTrinks7GBase() {
        String exam = "(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                        + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                        + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                        + "( 99 W - 11 B S + 3 B**2 ), " + "( B**2 + 33/50 B + 2673/10000 ) " + ") ";
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

        G = bbpar.GB(F.list);
        assertTrue("isGB( GB(Trinks7) )", bbpar.isGB(G));
        assertEquals("#GB(Trinks7) == 6", 6, G.size());
        //PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring,G);
        //System.out.println("G = " + trinks);
    }
}
