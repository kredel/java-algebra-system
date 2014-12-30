/*
 * $Id$
 */

package edu.jas.gb;


// import edu.jas.poly.GroebnerBase;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
// import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;


/**
 * Groebner base parallel, syzygy pair list, tests with JUnit.
 * @author Heinz Kredel.
 */

public class GroebnerBaseParSyzPairTest extends TestCase {


    //private static final Logger logger = Logger.getLogger(GroebnerBaseParSyzPairTest.class);

    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GroebnerBaseParSyzPairTest</CODE> object.
     * @param name String.
     */
    public GroebnerBaseParSyzPairTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBaseParSyzPairTest.class);
        return suite;
    }


    GenPolynomialRing<BigRational> fac;


    List<GenPolynomial<BigRational>> L;


    PolynomialList<BigRational> F;


    List<GenPolynomial<BigRational>> G;


    GroebnerBaseAbstract<BigRational> bbseq;


    GroebnerBaseAbstract<BigRational> bbpar;


    GroebnerBaseAbstract<BigRational> bbspar;


    GenPolynomial<BigRational> a;


    GenPolynomial<BigRational> b;


    GenPolynomial<BigRational> c;


    GenPolynomial<BigRational> d;


    GenPolynomial<BigRational> e;


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
        bbspar = new GroebnerBaseParallel<BigRational>(threads, new ReductionPar<BigRational>(),
                        new OrderedSyzPairlist<BigRational>());
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bbseq = null;
        bbpar.terminate();
        bbpar = null;
        bbspar.terminate();
        bbspar = null;
    }


    /**
     * Test syzygy pair parallel GBase.
     * 
     */
    public void testSyzPairParallelGBase() {

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

        L = bbspar.GB(L);
        assertTrue("isGB( { a } )", bbspar.isGB(L));

        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        //System.out.println("L = " + L.size() );

        L = bbspar.GB(L);
        assertTrue("isGB( { a, b } )", bbspar.isGB(L));

        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);

        L = bbspar.GB(L);
        assertTrue("isGB( { a, b, c } )", bbspar.isGB(L));

        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);

        L = bbspar.GB(L);
        assertTrue("isGB( { a, b, c, d } )", bbspar.isGB(L));

        assertTrue("not isZERO( e )", !e.isZERO());
        L.add(e);

        L = bbspar.GB(L);
        assertTrue("isGB( { a, b, c, d, e } )", bbspar.isGB(L));
    }


    /**
     * Test compare sequential with syzygy pair parallel GBase.
     * 
     */
    public void testSequentialSyzPairParallelGBase() {

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
        Gp = bbspar.GB(L);

        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp, Gp.containsAll(Gs));

        L = Gs;
        L.add(b);
        Gs = bbseq.GB(L);
        Gp = bbspar.GB(L);

        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp, Gp.containsAll(Gs));

        L = Gs;
        L.add(c);
        Gs = bbseq.GB(L);
        Gp = bbspar.GB(L);

        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp, Gp.containsAll(Gs));

        L = Gs;
        L.add(d);
        Gs = bbseq.GB(L);
        Gp = bbspar.GB(L);

        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp, Gp.containsAll(Gs));

        L = Gs;
        L.add(e);
        Gs = bbseq.GB(L);
        Gp = bbspar.GB(L);

        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp, Gp.containsAll(Gs));
    }


    /**
     * Test compare parallel with syzygy pair parallel GBase.
     * 
     */
    public void testParallelSyzPairParallelGBase() {

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
        Gs = bbpar.GB(L);
        Gp = bbspar.GB(L);

        assertTrue("Gs.containsAll(Gp) " + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs) " + Gs + ", " + Gp, Gp.containsAll(Gs));

        L = Gs;
        L.add(b);
        Gs = bbpar.GB(L);
        Gp = bbspar.GB(L);

        assertTrue("Gs.containsAll(Gp) " + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs) " + Gs + ", " + Gp, Gp.containsAll(Gs));

        L = Gs;
        L.add(c);
        Gs = bbpar.GB(L);
        Gp = bbspar.GB(L);

        assertTrue("Gs.containsAll(Gp) " + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs) " + Gs + ", " + Gp, Gp.containsAll(Gs));

        L = Gs;
        L.add(d);
        Gs = bbpar.GB(L);
        Gp = bbspar.GB(L);

        assertTrue("Gs.containsAll(Gp) " + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs) " + Gs + ", " + Gp, Gp.containsAll(Gs));

        L = Gs;
        L.add(e);
        Gs = bbpar.GB(L);
        Gp = bbspar.GB(L);

        assertTrue("Gs.containsAll(Gp) " + Gs + ", " + Gp, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs) " + Gs + ", " + Gp, Gp.containsAll(Gs));
    }


    /**
     * Test Trinks7 GBase.
     * 
     */
    @SuppressWarnings("cast")
    public void testTrinks7GBase() {
        String exam = "(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                        + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                        + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                        + "( 99 W - 11 B S + 3 B**2 ) " + ", ( B**2 + 33/50 B + 2673/10000 ) " + ") ";
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

        long t;
        /*     
               t = System.currentTimeMillis();
               G = bbseq.GB( F.list );
               t = System.currentTimeMillis() - t;
               System.out.println("bbseq ms = " + t);     
               t = System.currentTimeMillis();
               G = bbpar.GB( F.list );
               t = System.currentTimeMillis() - t;
               System.out.println("bbpar ms = " + t);     
        */
        t = System.currentTimeMillis();
        G = bbspar.GB(F.list);
        t = System.currentTimeMillis() - t;
        //System.out.println("bbspar ms = " + t);     
        assertTrue("nonsense ", t >= 0L);

        assertTrue("isGB( GB(Trinks7) )", bbspar.isGB(G));
        assertEquals("#GB(Trinks7) == 6", 6, G.size());
        //PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring,G);
        //System.out.println("G = " + trinks);
    }

}
