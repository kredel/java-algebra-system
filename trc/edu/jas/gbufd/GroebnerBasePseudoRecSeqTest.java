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
// import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;


/**
 * Groebner base recursive pseudo reduction sequential tests with JUnit.
 * @author Heinz Kredel.
 */

public class GroebnerBasePseudoRecSeqTest extends TestCase {


    //private static final Logger logger = Logger.getLogger(GroebnerBasePseudoRecSeqTest.class);

    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>GroebnerBasePseudoRecSeqTest</CODE> object.
     * @param name String.
     */
    public GroebnerBasePseudoRecSeqTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBasePseudoRecSeqTest.class);
        return suite;
    }


    GenPolynomialRing<GenPolynomial<BigInteger>> fac;


    List<GenPolynomial<GenPolynomial<BigInteger>>> L;


    PolynomialList<GenPolynomial<BigInteger>> F;


    List<GenPolynomial<GenPolynomial<BigInteger>>> G;


    GroebnerBaseAbstract<GenPolynomial<BigInteger>> bb;


    GenPolynomial<GenPolynomial<BigInteger>> a, b, c, d, e;


    int rl = 2; //4; //3; 


    int kl = 2;


    int ll = 5;


    int el = 4;


    float q = 0.3f; //0.4f


    @Override
    protected void setUp() {
        BigInteger coeff = new BigInteger(9);
        GenPolynomialRing<BigInteger> cofac = new GenPolynomialRing<BigInteger>(coeff, 1);
        fac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cofac, rl);
        a = b = c = d = e = null;
        bb = new GroebnerBasePseudoRecSeq<BigInteger>(cofac);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        //bb.terminate();
        fac = null;
        bb = null;
    }


    /**
     * Test recursive sequential GBase.
     */
    public void testRecSequentialGBase() {

        L = new ArrayList<GenPolynomial<GenPolynomial<BigInteger>>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.getZERO(); //fac.random(kl, ll, el, q);
        e = c; //fac.random(kl, ll, el, q );

        L.add(a);
        System.out.println("L = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a } )", bb.isGB(L));

        L.add(b);
        System.out.println("L = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a, b } )", bb.isGB(L));

        if (bb.commonZeroTest(L) < 0) {
            L.clear();
        }
        L.add(c);
        System.out.println("L = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c } )", bb.isGB(L));

        L.add(d);
        System.out.println("L = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d } )", bb.isGB(L));

        L.add(e);
        System.out.println("L = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d, e } )", bb.isGB(L));
    }


    /**
     * Test Trinks7 GBase.
    @SuppressWarnings("unchecked")
    public void testTrinks7GBase() {
        String exam = "Z(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                        + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                        + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                        + "( 99 W - 11 B S + 3 B**2 ), " + "( 10000 B**2 + 6600 B + 2673 ) " + ") ";
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
        //PolynomialList<BigInteger> trinks = new PolynomialList<BigInteger>(F.ring,G);
        //System.out.println("G = " + trinks);
        assertTrue("nonsense ", t >= 0L);

        GenPolynomialRing<BigInteger> ifac = F.ring;
        BigRational cf = new BigRational();
        GenPolynomialRing<BigRational> rfac = new GenPolynomialRing<BigRational>(cf, ifac);

        List<GenPolynomial<BigRational>> Gr, Fr, Gir;
        Fr = PolyUtil.<BigRational> fromIntegerCoefficients(rfac, F.list);
        GroebnerBaseSeq<BigRational> bbr = new GroebnerBaseSeq<BigRational>();
        s = System.currentTimeMillis();
        Gr = bbr.GB(Fr);
        s = System.currentTimeMillis() - s;

        Gir = PolyUtil.<BigRational> fromIntegerCoefficients(rfac, G);
        Gir = PolyUtil.<BigRational> monic(Gir);

        assertEquals("ratGB == intGB", Gr, Gir);
        //System.out.println("time: ratGB = " + s + ", intGB = " + t);
        assertTrue("nonsense ", s >= 0L);
    }
     */

}
