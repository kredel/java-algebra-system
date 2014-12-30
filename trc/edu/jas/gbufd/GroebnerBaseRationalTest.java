/*
 * $Id$
 */

package edu.jas.gbufd;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;


/**
 * Groebner base sequential rational fraction free tests with JUnit.
 * @author Heinz Kredel.
 */

public class GroebnerBaseRationalTest extends TestCase {


    private static final Logger logger = Logger.getLogger(GroebnerBaseRationalTest.class);


    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GroebnerBaseRationalTest</CODE> object.
     * @param name String.
     */
    public GroebnerBaseRationalTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBaseRationalTest.class);
        return suite;
    }


    GenPolynomialRing<BigRational> fac;


    List<GenPolynomial<BigRational>> L, Lp;


    PolynomialList<BigRational> F;


    List<GenPolynomial<BigRational>> G, Gp;


    GroebnerBaseAbstract<BigRational> bb;


    GroebnerBaseAbstract<BigRational> bbp;


    GenPolynomial<BigRational> a, b, c, d, e;


    int threads = 2;


    int rl = 4; //4; //3; 


    int kl = 7; // 10


    int ll = 7;


    int el = 3; // 4


    float q = 0.2f; //0.4f


    @Override
    protected void setUp() {
        BigRational coeff = new BigRational(9);
        fac = new GenPolynomialRing<BigRational>(coeff, rl);
        a = b = c = d = e = null;
        bb = new GroebnerBaseRational<BigRational>();
        bbp = new GroebnerBaseRational<BigRational>(threads);
    }


    @Override
    protected void tearDown() {
        bbp.terminate();
        a = b = c = d = e = null;
        fac = null;
        bb = null;
        bbp = null;
    }


    /**
     * Test sequential GBase.
     */
    public void testSequentialGBase() {
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

        L = bb.GB(L);
        assertTrue("isGB( { a } )", bb.isGB(L));
        assertTrue("isMinimalGB( { a } )", bb.isMinimalGB(L));

        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        //System.out.println("L = " + L.size() );

        L = bb.GB(L);
        assertTrue("isGB( { a, b } )", bb.isGB(L));
        assertTrue("isMinimalGB( { a, b } )", bb.isMinimalGB(L));

        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c } )", bb.isGB(L));
        assertTrue("isMinimalGB( { a, b, c } )", bb.isMinimalGB(L));

        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d } )", bb.isGB(L));
        assertTrue("isMinimalGB( { a, b, c, d } )", bb.isMinimalGB(L));

        assertTrue("not isZERO( e )", !e.isZERO());
        L.add(e);

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d, e } )", bb.isGB(L));
        assertTrue("isMinimalGB( { a, b, c, d, e } )", bb.isMinimalGB(L));
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

        L = bbp.GB(L);
        assertTrue("isGB( { a } )", bbp.isGB(L));
        assertTrue("isMinimalGB( { a } )", bbp.isMinimalGB(L));

        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        //System.out.println("L = " + L.size() );

        L = bbp.GB(L);
        assertTrue("isGB( { a, b } )", bbp.isGB(L));
        assertTrue("isMinimalGB( { a, b } )", bbp.isMinimalGB(L));

        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);

        L = bbp.GB(L);
        assertTrue("isGB( { a, b, c } )", bbp.isGB(L));
        assertTrue("isMinimalGB( { a, b, c } )", bbp.isMinimalGB(L));

        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);

        L = bbp.GB(L);
        assertTrue("isGB( { a, b, c, d } )", bbp.isGB(L));
        assertTrue("isMinimalGB( { a, b, c, d } )", bbp.isMinimalGB(L));

        assertTrue("not isZERO( e )", !e.isZERO());
        L.add(e);

        L = bbp.GB(L);
        assertTrue("isGB( { a, b, c, d, e } )", bbp.isGB(L));
        assertTrue("isMinimalGB( { a, b, c, d, e } )", bbp.isMinimalGB(L));
    }


    /**
     * Test Trinks7 GBase.
     */
    @SuppressWarnings("cast")
    public void testTrinks7GBase() {
        String exam = "(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                        + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                        + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                        + "( 99 W - 11 B S + 3 B**2 ), " + "( B**2 + 33/50 B + 2673/10000 ) " + ") ";
        @SuppressWarnings("unused")
        String exam2 = "(x,y,z) L " + "( " + "( z y**2 + 2 x + 1/2 )" + "( z x**2 - y**2 - 1/2 x )"
                        + "( -z + y**2 x + 4 x**2 + 1/4 )" + " )";

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
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(G));
        assertTrue("isMinimalGB( GB(Trinks7) )", bb.isMinimalGB(G));
        assertEquals("#GB(Trinks7) == 6", 6, G.size());
        Gp = bbp.GB(F.list);
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(Gp));
        assertTrue("isMinimalGB( GB(Trinks7) )", bb.isMinimalGB(Gp));
        assertEquals("#GB(Trinks7) == 6", 6, Gp.size());
        assertEquals("G == Gp: ", G, Gp);
        //PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring,G);
        //System.out.println("G = " + trinks);
    }


    /**
     * Test Trinks7 compare GBase.
     */
    @SuppressWarnings("cast")
    public void testTrinks7CompareGBase() {
        String exam = "(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                        + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                        + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                        + "( 99 W - 11 B S + 3 B**2 ), "
                        //+ "( B**2 + 33/50 B + 2673/10000 ) "
                        + ") ";

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

        long i = System.currentTimeMillis();
        G = bb.GB(F.list);
        i = System.currentTimeMillis() - i;
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(G));
        assertTrue("isMinimalGB( GB(Trinks7) )", bb.isMinimalGB(G));
        assertEquals("#GB(Trinks7) == 6", 6, G.size());

        long p = System.currentTimeMillis();
        Gp = bbp.GB(F.list);
        p = System.currentTimeMillis() - p;
        assertTrue("isGB( GB(Trinks7) )", bbp.isGB(G));
        assertTrue("isMinimalGB( GB(Trinks7) )", bbp.isMinimalGB(G));
        assertEquals("#GB(Trinks7) == 6", 6, Gp.size());

        GroebnerBaseAbstract<BigRational> bbr = new GroebnerBaseSeq<BigRational>();
        List<GenPolynomial<BigRational>> Gr;
        long r = System.currentTimeMillis();
        Gr = bbr.GB(F.list);
        r = System.currentTimeMillis() - r;
        assertTrue("isGB( GB(Trinks7) )", bbr.isGB(Gr));
        assertTrue("isMinimalGB( GB(Trinks7) )", bbr.isMinimalGB(Gr));
        assertEquals("#GB(Trinks7) == 6", 6, Gr.size());

        if (logger.isInfoEnabled()) {
            logger.info("time: seq = " + i + ", par = " + p + ", rat = " + r);
        }
        assertEquals("GB_r == GB_i", G, Gr);
        assertEquals("GB_r == GB_p", G, Gp);
        //PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring,G);
        //System.out.println("G = " + trinks);
    }

}
