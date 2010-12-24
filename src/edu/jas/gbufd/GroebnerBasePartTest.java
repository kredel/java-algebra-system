/*
 * $Id$
 */

package edu.jas.gbufd;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.gb.GroebnerBase;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrderOptimization;
import edu.jas.util.KsubSet;


/**
 * Groebner base sequential tests with JUnit.
 * @author Heinz Kredel.
 */

public class GroebnerBasePartTest extends TestCase {


    //private static final Logger logger = Logger.getLogger(GroebnerBasePartTest.class);

    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GroebnerBasePartTest</CODE> object.
     * @param name String.
     */
    public GroebnerBasePartTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBasePartTest.class);
        return suite;
    }


    GenPolynomialRing<BigRational> fac;


    List<GenPolynomial<BigRational>> L;


    PolynomialList<BigRational> F;


    List<GenPolynomial<BigRational>> G;


    GroebnerBase<BigRational> bb;


    GroebnerBasePartial<BigRational> bbp;


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
        bb = new GroebnerBaseSeq<BigRational>();
        bbp = new GroebnerBasePartial<BigRational>();
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bb = null;
        ComputerThreads.terminate();
    }


    /**
     * Test partial recursive Trinks7 GBase.
     * 
     */
    @SuppressWarnings("unchecked")
    public void testTrinks7GBasePartRec() {
        String exam = "(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                + "( 99 W - 11 B S + 3 B**2 ) " + "( B**2 + 33/50 B + 2673/10000 ) " + ") ";

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

        //PolynomialList<BigRational> Fo = TermOrderOptimization.optimizeTermOrder(F);
        //System.out.println("\nFo = " + Fo);

        PolynomialList<GenPolynomial<BigRational>> rtrinks = bbp.partialGBrec(F.list, new String[] { "P",
                "Z", "T", "W" });
        assertTrue("isGB( GB(Trinks7) )", bbp.isGBrec(rtrinks.list));
        //System.out.println("\nTrinksR = " + rtrinks);

        // not meaning-full
        PolynomialList<BigRational> trinks = bbp.partialGB(F.list, new String[] { "P", "Z", "T", "W" });
        //System.out.println("\ntrinks = " + trinks);
        assertTrue("isGB( GB(Trinks7) )", bbp.isGB(trinks.list));
    }


    /**
     * Test partial Trinks7 GBase.
     * 
     */
    @SuppressWarnings("unchecked")
    public void testTrinks7GBasePart() {
        String exam = "(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                + "( 99 W - 11 B S + 3 B**2 ) " + "( B**2 + 33/50 B + 2673/10000 ) " + ") ";

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

        //PolynomialList<BigRational> Fo = TermOrderOptimization.optimizeTermOrder(F);
        //System.out.println("\nFo = " + Fo);

        PolynomialList<BigRational> trinks = bbp.partialGB(F.list, new String[] { "B", "S", "P", "Z", "T",
                "W" });
        //PolynomialList<BigRational> trinks = bbp.partialGB(F.list, new String[] { "T", "Z", "P", "W", "B", "S" });
        assertTrue("isGB( GB(Trinks7) )", bbp.isGB(trinks.list));
        //System.out.println("\nG = " + trinks);

        try {
            PolynomialList<GenPolynomial<BigRational>> tr = bbp.partialGBrec(F.list, new String[] { "B", "S",
                    "P", "Z", "T", "W" });
            fail("must throw exception");
        } catch (IllegalArgumentException e) {
            //pass
        }
    }


    /**
     * Test partial permutation.
     * 
     */
    public void testPartialPermutation() {
        String[] vars = new String[] { "B", "S", "T", "Z", "P", "W" };
        String[] pvars = new String[] { "P", "Z", "T", "W" };
        String[] rvars = new String[] { "S", "B" };
        List<Integer> perm1 = GroebnerBasePartial.partialPermutation(vars, pvars);
        //System.out.println("perm1 = " + perm1);

        List<Integer> perm2 = GroebnerBasePartial.partialPermutation(vars, pvars, null);
        //System.out.println("perm2 = " + perm2);

        assertEquals("perm1 == perm2 ", perm1, perm2);

        List<Integer> perm3 = GroebnerBasePartial.partialPermutation(vars, pvars, rvars);
        //System.out.println("perm3 = " + perm3);
        assertFalse("perm1 != perm3 ", perm1.equals(perm3));
    }


    /**
     * Test elimination partial permutation.
     * 
     */
    public void xtestElimPartialPermutation() {
        String[] vars = new String[] { "B", "S", "T", "Z", "P", "W" };
        String[] evars = new String[] { "P", "Z" };
        String[] pvars = new String[] { "T", "W" };
        String[] rvars = new String[] { "B", "S" };
        List<Integer> perm1 = GroebnerBasePartial.partialPermutation(vars, evars, pvars, rvars);
        System.out.println("perm1 = " + perm1);

        List<Integer> perm2 = GroebnerBasePartial.partialPermutation(vars, evars, pvars, null);
        System.out.println("perm2 = " + perm2);

        assertEquals("perm1 == perm2 ", perm1, perm2);

        rvars = new String[] { "S", "B" };

        List<Integer> perm3 = GroebnerBasePartial.partialPermutation(vars, evars, pvars, rvars);
        System.out.println("perm3 = " + perm3);
        assertFalse("perm1 != perm3 ", perm1.equals(perm3));
    }


    /**
     * Test elim partial Trinks7 GBase.
     * 
     */
    @SuppressWarnings("unchecked")
    public void testTrinks7GBaseElimPart() {
        String exam = "(B,S,T,Z,P,W) G " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                + "( 99 W - 11 B S + 3 B**2 ) " + "( B**2 + 33/50 B + 2673/10000 ) " + ") ";

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

        String[] evars = new String[] { "P", "Z" };
        String[] pvars = new String[] { "B", "S", "T", "W" };
        //System.out.println("evars = " + Arrays.toString(evars));
        //System.out.println("pvars = " + Arrays.toString(pvars));

        PolynomialList<BigRational> trinks = bbp.elimPartialGB(F.list, evars, pvars);
        assertTrue("isGB( GB(Trinks7) )", bbp.isGB(trinks.list));
        //System.out.println("\nG = " + trinks);
    }


    /**
     * Test partial GBase.
     * 
     */
    @SuppressWarnings("unchecked")
    public void testGBasePart() {
        String exam = "(a,b,c,d,e,f) G " + "( " + "( a ), " + "( b^2 ), " + "( c^3 ), " + "( d^4 ), "
                + "( e^5 ), " + "( f^6 ) " + ") ";

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

        // String[] evars = new String[] { "c", "d", "e", "f", "a", "b"};
        String[] evars = new String[] { "a", "b" };
        //System.out.println("evars = " + Arrays.toString(evars));

        PolynomialList<BigRational> G = bbp.partialGB(F.list, evars);
        assertTrue("isGB( GB(G) )", bbp.isGB(G.list));
        //System.out.println("evars = " + Arrays.toString(evars));
        //System.out.println("G = " + G);
    }


    /**
     * Test permutation generation.
     * 
     */
    @SuppressWarnings("unchecked")
    public void testPermGen() {
        String[] vars = new String[] { "a", "b", "c", "d", "e", "f" };
        //System.out.println("vars  = " + Arrays.toString(vars));

        List<String> sv = new ArrayList<String>(vars.length);
        for (int i = 0; i < vars.length; i++) {
            sv.add(vars[i]);
        }
        //System.out.println("sv    = " + sv);

        String exam = "(a,b,c,d,e,f) G " + "( " + "( a ), " + "( b^2 ), " + "( c^3 ), " + "( d^4 ), "
                + "( e^5 ), " + "( f^6 ) " + ") ";

        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        PolynomialList<BigRational> F = null;
        try {
            F = (PolynomialList<BigRational>) parser.nextPolynomialSet();
        } catch (ClassCastException e) {
            fail("" + e);
        } catch (IOException e) {
            fail("" + e);
        }
        //System.out.println("F = " + F);

        for (int i = 0; i <= vars.length; i++) {
            KsubSet<String> ps = new KsubSet<String>(sv, i);
            //System.out.println("========================== ps : " + i);
            for (List<String> ev : ps) {
                //System.out.println("ev = " + ev);

                String[] evars = new String[ev.size()];
                for (int j = 0; j < ev.size(); j++) {
                    evars[j] = ev.get(j);
                }
                //System.out.println("evars = " + Arrays.toString(evars));
                String[] rvars = GroebnerBasePartial.remainingVars(vars, evars);
                //System.out.println("rvars = " + Arrays.toString(rvars));

                List<Integer> perm1 = GroebnerBasePartial.partialPermutation(vars, evars);
                //System.out.println("perm1 = " + perm1);
                List<Integer> perm2 = GroebnerBasePartial.getPermutation(vars, rvars);
                //System.out.println("perm2 = " + perm2); 
                assertEquals("perm1 == perm2 " + Arrays.toString(evars), perm1, perm2);

                GenPolynomialRing<BigRational> r = new GenPolynomialRing<BigRational>(fac.coFac, vars);
                GenPolynomialRing<BigRational> pr1 = TermOrderOptimization
                        .<BigRational> permutation(perm1, r);
                //System.out.println("pr1 = " + pr1);
                GenPolynomialRing<BigRational> pr2 = TermOrderOptimization
                        .<BigRational> permutation(perm2, r);
                //System.out.println("pr2 = " + pr2);
                assertEquals("pr1 == pr2 ", pr1, pr2);

                List<GenPolynomial<BigRational>> pF1 = TermOrderOptimization.<BigRational> permutation(perm1,
                        pr1, F.list);
                //System.out.println("pF1 = " + pF1);
                List<GenPolynomial<BigRational>> pF2 = TermOrderOptimization.<BigRational> permutation(perm2,
                        pr2, F.list);
                //System.out.println("pF2 = " + pF2);
                assertEquals("pF1 == pF2 ", pF1, pF2);
            }
        }
    }

}
