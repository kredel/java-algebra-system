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

import edu.jas.arith.BigInteger;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;


/**
 * Groebner base pseudo reduction parallel tests with JUnit.
 * @author Heinz Kredel.
 */

public class GroebnerBasePseudoParTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GroebnerBasePseudoParTest</CODE> object.
     * @param name String.
     */
    public GroebnerBasePseudoParTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBasePseudoParTest.class);
        return suite;
    }


    GenPolynomialRing<BigInteger> fac;


    List<GenPolynomial<BigInteger>> L;


    PolynomialList<BigInteger> F;


    List<GenPolynomial<BigInteger>> G;


    GroebnerBaseAbstract<BigInteger> bb;


    GenPolynomial<BigInteger> a;


    GenPolynomial<BigInteger> b;


    GenPolynomial<BigInteger> c;


    GenPolynomial<BigInteger> d;


    GenPolynomial<BigInteger> e;


    int rl = 3; //4; //3; 


    int kl = 10;


    int ll = 7;


    int el = 3;


    float q = 0.2f; //0.4f


    int threads = 2;


    @Override
    protected void setUp() {
        BigInteger coeff = new BigInteger(9);
        fac = new GenPolynomialRing<BigInteger>(coeff, rl);
        a = b = c = d = e = null;
        bb = new GroebnerBasePseudoParallel<BigInteger>(threads, coeff);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bb.terminate();
        bb = null;
    }


    /**
     * Test parallel GBase.
     * 
     */
    public void testParallelGBase() {

        L = new ArrayList<GenPolynomial<BigInteger>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        //if ( a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO() ) {
        //   return;
        //}

        L.add(a);
        L = bb.GB(L);
        assertTrue("isGB( { a } )", bb.isGB(L));

        L.add(b);
        //System.out.println("L = " + L.size() );
        L = bb.GB(L);
        assertTrue("isGB( { a, b } )", bb.isGB(L));

        L.add(c);
        L = bb.GB(L);
        assertTrue("isGB( { a, b, c } )", bb.isGB(L));

        L.add(d);
        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d } )", bb.isGB(L));

        L.add(e);
        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d, e } )", bb.isGB(L));
    }


    /**
     * Test Trinks6/7 GBase.
     * 
     */
    @SuppressWarnings("cast")
    public void testTrinks7GBase() {
        String exam = "Z(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                        + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                        + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                        + "( 99 W - 11 B S + 3 B**2 ), "
                        //+ "( 10000 B**2 + 6600 B + 2673 ) "
                        + ") ";
        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        try {
            F = (PolynomialList<BigInteger>) parser.nextPolynomialSet();
        } catch (ClassCastException e) {
            fail("" + e);
        } catch (IOException e) {
            fail("" + e);
        }
        //System.out.println("F = " + F);

        long s, t;
        t = System.currentTimeMillis();
        G = bb.GB(F.list);
        t = System.currentTimeMillis() - t;
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(G));
        assertEquals("#GB(Trinks7) == 6", 6, G.size());
        PolynomialList<BigInteger> Gpl = new PolynomialList<BigInteger>(F.ring, G);
        //System.out.println("G = " + Gpl);
        assertTrue("nonsense ", t >= 0L);

        GenPolynomialRing<BigInteger> ifac = F.ring;

        List<GenPolynomial<BigInteger>> Gi;
        GroebnerBasePseudoSeq<BigInteger> bbr = new GroebnerBasePseudoSeq<BigInteger>(ifac.coFac);
        s = System.currentTimeMillis();
        Gi = bbr.GB(F.list);
        s = System.currentTimeMillis() - s;
        PolynomialList<BigInteger> Gipl = new PolynomialList<BigInteger>(F.ring, Gi);
        //System.out.println("G = " + Gpl);
        //System.out.println("Gi = " + Gipl);

        assertEquals("seqGB == parGB", Gpl, Gipl);
        //System.out.println("time: seqGB = " + s + ", parGB = " + t);
        assertTrue("nonsense ", s >= 0L);
    }

}
