/*
 * $Id$
 */

package edu.jas.gbufd;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;


import edu.jas.arith.BigRational;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrderByName;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Groebner base Groebner Walk tests with JUnit.
 * @author Heinz Kredel
 */

public class GroebnerBaseWalkTest extends TestCase {




    /**
     * main
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GroebnerBaseWalkTest</CODE> object.
     * @param name String.
     */
    public GroebnerBaseWalkTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBaseWalkTest.class);
        return suite;
    }


    List<GenPolynomial<BigRational>> L, Lp;


    PolynomialList<BigRational> F;


    List<GenPolynomial<BigRational>> G, Gp;


    GroebnerBaseAbstract<BigRational> bb;


    GroebnerBaseAbstract<BigRational> bbw;


    @Override
    protected void setUp() {
        //BigRational cf = new BigRational();
        //bb = new GroebnerBaseSeq<BigRational>(cf);
        bb = GBFactory.<BigRational> getImplementation(/*cf*/);
        bbw = new GroebnerBaseWalk<BigRational>(bb);
    }


    @Override
    protected void tearDown() {
        bb.terminate();
        bbw.terminate();
        bb = null;
        bbw = null;
    }


    /**
     * Test FJLT GBase. Example from the FJLT paper.
     */
    @SuppressWarnings({ "unchecked", "cast" })
    public void testFJLTGBase() { // (y,x)
        String exam = "(y,x) L " // REVILEX REVITDG
                        + "( (x**2 - y**3), (x**3 - y**2 - x) )";

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
        PolynomialList<BigRational> seq = new PolynomialList<BigRational>(F.ring, G);
        //System.out.println("seq G = " + seq);
        assertTrue("isGB( GB(FJLT) )", bb.isGB(G));
        assertTrue("isMinimalGB( GB(FJLT) )", bb.isMinimalGB(G));
        assertEquals("#GB(FJLT) == 2", 2, G.size());
        //assertEquals("#GB(FJLT) == 3", 3, G.size());
        Gp = bbw.GB(F.list);
        PolynomialList<BigRational> fjlt = new PolynomialList<BigRational>(F.ring, Gp);
        //System.out.println("walk G = " + fjlt);
        assertTrue("isGB( GB(FJLT) )", bb.isGB(Gp));
        assertTrue("isMinimalGB( GB(FJLT) )", bb.isMinimalGB(Gp));
        assertEquals("#GB(FJLT) == 2", 2, Gp.size());
        //assertEquals("#GB(FJLT) == 3", 3, Gp.size());
        //Collections.reverse(G); // now in minimal
        assertEquals("G == Gp: ", seq, fjlt);
    }


    /**
     * Test example GBase.
     */
    @SuppressWarnings({ "unchecked", "cast" })
    public void testFGLMGBase() { //(z,y,x)
        String exam = "(x,y,z) L " + "( " + "( z y**2 + 2 x + 1/2 )" + "( z x**2 - y**2 - 1/2 x )"
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
        PolynomialList<BigRational> P = new PolynomialList<BigRational>(F.ring, G);
        //System.out.println("G = " + P);
        assertTrue("isGB( GB(P) )", bb.isGB(G));
        assertEquals("#GB(P) == 3", 3, G.size());

        Gp = bbw.GB(F.list);
        PolynomialList<BigRational> P2 = new PolynomialList<BigRational>(F.ring, Gp);
        //System.out.println("G = " + P2);
        assertTrue("isGB( GB(P2) )", bb.isGB(Gp));
        assertEquals("#GB(P2) == 3", 3, Gp.size());
        assertEquals("GB == FGLM", P, P2);
    }


    /**
     * Test Trinks GBase.
     */
    @SuppressWarnings({ "unchecked", "cast" })
    public void testTrinksGBase() {
        String exam = "(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                        + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                        + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                        + "( 99 W - 11 B S + 3 B**2 ) " /*+ ", ( 10000 B**2 + 6600 B + 2673 )"*/ + ") ";

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

        long t = System.currentTimeMillis();
        G = bb.GB(F.list);
        t = System.currentTimeMillis() - t;
        PolynomialList<BigRational> seq = new PolynomialList<BigRational>(F.ring, G);
        //System.out.println("seq G = " + seq);
        assertTrue("isGB( GB(Trinks) )", bb.isGB(G));
        assertTrue("isMinimalGB( GB(Trinks) )", bb.isMinimalGB(G));
        assertEquals("#GB(Trinks) == 6", 6, G.size());
        long w = System.currentTimeMillis();
        Gp = bbw.GB(F.list);
        w = System.currentTimeMillis() - w;
        PolynomialList<BigRational> tri = new PolynomialList<BigRational>(F.ring, Gp);
        //System.out.println("walk G = " + tri);
        //System.out.println("lex = " + t + ", walk = " + w + " in milliseconds");
        assertTrue("findbugs ", t + w >= 0L);
        assertTrue("isGB( GB(Trinks) )", bb.isGB(Gp));
        assertTrue("isMinimalGB( GB(Trinks) )", bb.isMinimalGB(Gp));
        //assertEquals("#GB(Trinks) == 6", 6, Gp.size());
        //Collections.reverse(G); // now in minimal
        assertEquals("G == Gp: ", seq, tri); // ideal.equals
    }


    /**
     * Test Trinks GBase with different t1 and t2.
     */
    @SuppressWarnings({ "unchecked", "cast" })
    public void testTrinksGBaseT1T2() { // t2 = G|4| = IGRLEX.blockOrder(4)
        String exam = "(B,S,T,Z,P,W) G|4| " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                        + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                        + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                        + "( 99 W - 11 B S + 3 B**2 ) " /*+ ", ( 10000 B**2 + 6600 B + 2673 )"*/ + ") ";

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
        // set t1, t2 = TermOrderNyName.IGRLEX.blockOrder(4)
        bbw = new GroebnerBaseWalk<BigRational>(bb, TermOrderByName.IGRLEX.blockOrder(2));
        // Test wrong way from INVLEX to IGRLEX.
        //bbw = new GroebnerBaseWalk<BigRational>(bb, TermOrderByName.INVLEX);
        //System.out.println("bbw = " + bbw);

        Gp = bbw.GB(F.list);
        //PolynomialList<BigRational> tri = new PolynomialList<BigRational>(F.ring, Gp);
        //System.out.println("walk G = " + tri);
        assertTrue("isGB( GB(Trinks) )", bb.isGB(Gp));
        assertTrue("isMinimalGB( GB(Trinks) )", bb.isMinimalGB(Gp));
        //assertEquals("#GB(Trinks) == 6", 6, Gp.size());
        //Collections.reverse(G); // now in minimal
    }


    /**
     * Test ISSAC GBase. Example from the FGLM paper.
     */
    @SuppressWarnings({ "unchecked", "cast" })
    public void testFGLMissacGBase() { // 
        String exam = "Mod 32003 (w,z,y,x) L " // Mod 9223372036854775783 536870909 32003 (z,y,x,w)
                        + "( "
                        + " (8*w^2 + 5*w*x - 4*w*y + 2*w*z + 3*w + 5*x^2 + 2*x*y - 7*x*z - 7*x + 7*y^2 -8*y*z - 7*y + 7*z^2 - 8*z + 8),"
                        + "(3*w^2 - 5*w*x - 3*w*y - 6*w*z + 9*w + 4*x^2 + 2*x*y - 2*x*z + 7*x + 9*y^2 + 6*y*z + 5*y + 7*z^2 + 7*z + 5),"
                        + "(-2*w^2 + 9*w*x + 9*w*y - 7*w*z - 4*w + 8*x^2 + 9*x*y - 3*x*z + 8*x + 6*y^2 - 7*y*z + 4*y - 6*z^2 + 8*z + 2),"
                        + "(7*w^2 + 5*w*x + 3*w*y - 5*w*z - 5*w + 2*x^2 + 9*x*y - 7*x*z + 4*x -4*y^2 - 5*y*z + 6*y - 4*z^2 - 9*z + 2)"
                        + " )";

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

        //G = bb.GB(F.list);
        long t = System.currentTimeMillis();
        G = bb.GB(F.list);
        t = System.currentTimeMillis() - t;
        //PolynomialList<BigRational> seq = new PolynomialList<BigRational>(F.ring, G);
        //System.out.println("seq G = " + seq);
        assertTrue("isGB( GB() )", bb.isGB(G));
        assertTrue("isMinimalGB( GB() )", bb.isMinimalGB(G));
        //assertEquals("#GB(FJLT) == 2", 2, G.size());

        long w = System.currentTimeMillis();
        Gp = bbw.GB(F.list);
        w = System.currentTimeMillis() - w;
        //PolynomialList<BigRational> fjlt = new PolynomialList<BigRational>(F.ring, Gp);
        //System.out.println("walk G = " + fjlt);
        //System.out.println("lex = " + t + ", walk = " + w + " in milliseconds");
        assertTrue("findbugs ", t + w >= 0L);
        assertTrue("isGB( GB() )", bb.isGB(Gp));
        assertTrue("isMinimalGB( GB() )", bb.isMinimalGB(Gp));
        //assertEquals("#GB(FJLT) == 2", 2, Gp.size());
        //Collections.reverse(G); // now in minimal
        assertEquals("G == Gp: ", G, Gp);
    }
}
