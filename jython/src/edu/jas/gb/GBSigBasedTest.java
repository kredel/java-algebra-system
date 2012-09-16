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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
//import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;


/**
 * Groebner base sig based via jython tests with JUnit.
 * @author Heinz Kredel.
 */

public class GBSigBasedTest extends TestCase {


    private static final Logger logger = Logger.getLogger(GBSigBasedTest.class);


    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GBSigBasedTest</CODE> object.
     * @param name String.
     */
    public GBSigBasedTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GBSigBasedTest.class);
        return suite;
    }


    GenPolynomialRing<BigRational> fac;


    List<GenPolynomial<BigRational>> L;


    PolynomialList<BigRational> F;


    List<GenPolynomial<BigRational>> G;


    GroebnerBaseAbstract<BigRational> bb;


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


    @Override
    protected void setUp() {
        BigRational coeff = new BigRational(9);
        fac = new GenPolynomialRing<BigRational>(coeff, rl);
        a = b = c = d = e = null;
        bb = new GBSigBased<BigRational>();
    }


    @Override
    protected void tearDown() {
        int s = bb.cancel();
        logger.debug("canceled tasks: " + s);
        //assertTrue("s >= 0 " + s, s >= 0);
        a = b = c = d = e = null;
        fac = null;
        bb = null;
    }


    /**
     * Test GBase.
     */
    public void testGBase() {
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

        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        //System.out.println("L = " + L.size() );

        L = bb.GB(L);
        assertTrue("isGB( { a, b } )", bb.isGB(L));

        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c } )", bb.isGB(L));

        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d } )", bb.isGB(L));

        assertTrue("not isZERO( e )", !e.isZERO());
        L.add(e);

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d, e } )", bb.isGB(L));
    }


    /**
     * Test Trinks7 GBase.
     */
    @SuppressWarnings("unchecked")
    public void testTrinks7GBase() {
        //String exam = "Mod 32003 (B,S,T,Z,P,W) L " 
        String exam = "Rat (B,S,T,Z,P,W) L " 
            + "( " + "( 45 P + 35 S - 165 B - 36 ), "
            + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
            + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
            + "( 99 W - 11 B S + 3 B**2 ), " + "10000 B**2 + 6600 B + 2673 ) " + ") ";
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
        assertEquals("#GB(Trinks7) == 6", 6, G.size());
        assertTrue("isGB( GB(Trinks7) ) " + G, bb.isGB(G));
        //PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring, G);
        //System.out.println("G = " + trinks);
        G = bb.GB(F.list);
        //G = bb.GB(F.list);
        assertEquals("#GB(Trinks7) == 6", 6, G.size());
    }

}
