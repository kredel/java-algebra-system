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

import edu.jas.arith.BigRational;
import edu.jas.gb.GroebnerBase;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.gb.OrderedSyzPairlist;
import edu.jas.gb.ReductionSeq;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.Complex;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;


/**
 * Groebner base via FGLM tests with JUnit.
 * @author Heinz Kredel.
 */

public class GroebnerBaseFGLMTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GroebnerBaseFGLMTest</CODE> object.
     * @param name String.
     */
    public GroebnerBaseFGLMTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBaseFGLMTest.class);
        return suite;
    }


    GenPolynomialRing<BigRational> fac;


    List<GenPolynomial<BigRational>> L;


    PolynomialList<BigRational> F;


    List<GenPolynomial<BigRational>> G, Gs;


    GroebnerBase<BigRational> bb;


    GenPolynomial<BigRational> a;


    GenPolynomial<BigRational> b;


    GenPolynomial<BigRational> c;


    GenPolynomial<BigRational> d;


    GenPolynomial<BigRational> e;


    int rl = 4; //4; //3; 


    int kl = 7; // 10


    int ll = 7;


    int el = 3; // 4


    float q = 0.2f; //0.4f


    @Override
    protected void setUp() {
        BigRational coeff = new BigRational(9);
        fac = new GenPolynomialRing<BigRational>(coeff, rl, new TermOrder(TermOrder.INVLEX));
        a = b = c = d = e = null;
        bb = new GroebnerBaseFGLM<BigRational>();
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bb = null;
    }


    /**
     * Test Trinks7 GBase.
     */
    @SuppressWarnings("cast")
    public void testTrinks7GBase() {
        GroebnerBase<BigRational> bbs = new GroebnerBaseSeq<BigRational>(new ReductionSeq<BigRational>(),
                        new OrderedSyzPairlist<BigRational>());
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
        PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring, G);
        //System.out.println("G = " + trinks);
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(G));
        assertEquals("#GB(Trinks7) == 6", 6, G.size());

        Gs = bbs.GB(F.list);
        PolynomialList<BigRational> trinks2 = new PolynomialList<BigRational>(F.ring, Gs);
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(Gs));
        assertEquals("#GB(Trinks7) == 6", 6, Gs.size());

        assertEquals("GB == FGLM", trinks, trinks2);
    }


    /**
     * Test Trinks6 GBase.
     */
    @SuppressWarnings("cast")
    public void testTrinks6GBase() {
        GroebnerBase<BigRational> bbs = new GroebnerBaseSeq<BigRational>(new ReductionSeq<BigRational>(),
                        new OrderedSyzPairlist<BigRational>());
        String exam = "(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                        + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                        + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
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
        PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring, G);
        //System.out.println("G = " + trinks);
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(G));
        assertEquals("#GB(Trinks7) == 6", 6, G.size());

        Gs = bbs.GB(F.list);
        PolynomialList<BigRational> trinks2 = new PolynomialList<BigRational>(F.ring, Gs);
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(Gs));
        assertEquals("#GB(Trinks7) == 6", 6, Gs.size());

        assertEquals("GB == FGLM", trinks, trinks2);
    }


    /**
     * Test Trinks7 GBase over Q(sqrt(2)).
     */
    @SuppressWarnings("cast")
    public void testTrinks7GBaseSqrt() {
        GroebnerBase<AlgebraicNumber<BigRational>> bbs = new GroebnerBaseSeq<AlgebraicNumber<BigRational>>(
                        new ReductionSeq<AlgebraicNumber<BigRational>>(),
                        new OrderedSyzPairlist<AlgebraicNumber<BigRational>>());
        GroebnerBase<AlgebraicNumber<BigRational>> bb = new GroebnerBaseFGLM<AlgebraicNumber<BigRational>>();
        String exam = "AN[ (w2) (w2^2 - 2) ] (B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                        + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                        + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                        + "( 99 W - 11 B S + 3 B**2 ), " + "( B**2 + 33/50 B + 2673/10000 ) " + ") ";

        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        PolynomialList<AlgebraicNumber<BigRational>> F = null;
        List<GenPolynomial<AlgebraicNumber<BigRational>>> G, Gs;
        try {
            F = (PolynomialList<AlgebraicNumber<BigRational>>) parser.nextPolynomialSet();
        } catch (ClassCastException e) {
            fail("" + e);
        } catch (IOException e) {
            fail("" + e);
        }
        //System.out.println("F = " + F);
        GenPolynomialRing<AlgebraicNumber<BigRational>> pfac = F.ring;
        AlgebraicNumberRing<BigRational> afac = (AlgebraicNumberRing<BigRational>) pfac.coFac;
        //System.out.println("afac = " + afac);
        afac = new AlgebraicNumberRing<BigRational>(afac.modul, true);
        //System.out.println("afac = " + afac);
        pfac = new GenPolynomialRing<AlgebraicNumber<BigRational>>(afac, pfac);
        List<GenPolynomial<AlgebraicNumber<BigRational>>> Fp = new ArrayList<GenPolynomial<AlgebraicNumber<BigRational>>>(
                        F.list.size());
        for (GenPolynomial<AlgebraicNumber<BigRational>> p : F.list) {
            GenPolynomial<AlgebraicNumber<BigRational>> pp = pfac.copy(p);
            Fp.add(pp);
        }
        F = new PolynomialList<AlgebraicNumber<BigRational>>(pfac, Fp);

        G = bb.GB(F.list);
        PolynomialList<AlgebraicNumber<BigRational>> trinks = new PolynomialList<AlgebraicNumber<BigRational>>(
                        F.ring, G);
        //System.out.println("G = " + trinks);
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(G));
        assertEquals("#GB(Trinks7) == 6", 6, G.size());

        Gs = bbs.GB(F.list);
        PolynomialList<AlgebraicNumber<BigRational>> trinks2 = new PolynomialList<AlgebraicNumber<BigRational>>(
                        F.ring, Gs);
        //System.out.println("Gs = " + trinks2);
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(Gs));
        assertEquals("#GB(Trinks7) == 6", 6, Gs.size());

        assertEquals("GB == FGLM", trinks, trinks2);
    }


    /**
     * Test Trinks7 GBase over Q(i).
     */
    @SuppressWarnings("cast")
    public void testTrinks7GBaseCompl() {
        GroebnerBase<Complex<BigRational>> bbs = new GroebnerBaseSeq<Complex<BigRational>>(
                        new ReductionSeq<Complex<BigRational>>(),
                        new OrderedSyzPairlist<Complex<BigRational>>());
        GroebnerBase<Complex<BigRational>> bb = new GroebnerBaseFGLM<Complex<BigRational>>();
        String exam = "Complex (B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                        + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                        + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                        + "( 99 W - 11 B S + 3 B**2 ), " + "( B**2 + 33/50 B + 2673/10000 ) " + ") ";

        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        PolynomialList<Complex<BigRational>> F = null;
        List<GenPolynomial<Complex<BigRational>>> G, Gs;
        try {
            F = (PolynomialList<Complex<BigRational>>) parser.nextPolynomialSet();
        } catch (ClassCastException e) {
            fail("" + e);
        } catch (IOException e) {
            fail("" + e);
        }
        //System.out.println("F = " + F);

        G = bb.GB(F.list);
        PolynomialList<Complex<BigRational>> trinks = new PolynomialList<Complex<BigRational>>(F.ring, G);
        //System.out.println("G = " + trinks);
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(G));
        assertEquals("#GB(Trinks7) == 6", 6, G.size());

        Gs = bbs.GB(F.list);
        PolynomialList<Complex<BigRational>> trinks2 = new PolynomialList<Complex<BigRational>>(F.ring, Gs);
        //System.out.println("Gs = " + trinks2);
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(Gs));
        assertEquals("#GB(Trinks7) == 6", 6, Gs.size());

        assertEquals("GB == FGLM", trinks, trinks2);
    }

}
