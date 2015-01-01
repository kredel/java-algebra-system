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
import edu.jas.gb.WordGroebnerBaseAbstract;
import edu.jas.gb.WordGroebnerBaseSeq;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;
import edu.jas.ufd.PolyUfdUtil;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;


/**
 * Word Groebner base recursive pseudo reduction sequential tests with JUnit.
 * @author Heinz Kredel.
 */

public class WordGroebnerBasePseudoRecSeqTest extends TestCase {


    //private static final Logger logger = Logger.getLogger(WordGroebnerBasePseudoRecSeqTest.class);

    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>WordGroebnerBasePseudoRecSeqTest</CODE> object.
     * @param name String.
     */
    public WordGroebnerBasePseudoRecSeqTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(WordGroebnerBasePseudoRecSeqTest.class);
        return suite;
    }


    GenWordPolynomialRing<GenPolynomial<BigInteger>> fac;


    List<GenWordPolynomial<GenPolynomial<BigInteger>>> L, G;


    WordGroebnerBaseAbstract<GenPolynomial<BigInteger>> bb;


    WordGroebnerBaseAbstract<GenPolynomial<BigRational>> bbr;


    GenWordPolynomial<GenPolynomial<BigInteger>> a, b, c, d, e;


    int rl = 2; //4; //3; 


    int kl = 2;


    int ll = 3;


    int el = 2;


    @Override
    protected void setUp() {
        BigInteger coeff = new BigInteger(9);
        String[] cvars = new String[] { "x", "y" };
        GenPolynomialRing<BigInteger> cofac = new GenPolynomialRing<BigInteger>(coeff, cvars);
        String vars = "a b";
        fac = new GenWordPolynomialRing<GenPolynomial<BigInteger>>(cofac, vars);
        System.out.println("fac = " + fac.toScript());
        a = b = c = d = e = null;
        bb = new WordGroebnerBasePseudoRecSeq<BigInteger>(cofac);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        //bb.terminate();
        bb = null;
    }


    /**
     * Test recursive sequential GBase.
     */
    public void testRecSequentialGBase() {
        L = new ArrayList<GenWordPolynomial<GenPolynomial<BigInteger>>>();

        a = fac.random(kl, ll, el);
        b = fac.random(kl, ll, el);
        c = fac.random(kl, ll, el);
        d = fac.getZERO(); //fac.random(kl, ll, el);
        e = c; //fac.random(kl, ll, el);
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

        L.add(a);
        //System.out.println("La = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a } )", bb.isGB(L));

        L.add(b);
        //System.out.println("Lb = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a, b } )", bb.isGB(L));

        if (bb.commonZeroTest(L) < 0) {
            //System.out.println("Gb = " + L );
            //d = a;
            L.clear();
        }
        L.add(c);
        //System.out.println("Lc = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c } )", bb.isGB(L));

        L.add(d);
        //System.out.println("Ld = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d } )", bb.isGB(L));

        L.add(e);
        //System.out.println("Le = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d, e } )", bb.isGB(L));
    }


    /**
     * Test Hawes2 GBase with commutative relations.
     */
    //@SuppressWarnings("unchecked")
    public void testHawes2GBase() {
        String exam = "IntFunc(a, c, b) (y2, y1, z1, z2, x) G" + "("
                        + "( x + 2 y1 z1 + { 3 a } y1^2 + 5 y1^4 + { 2 c } y1 ),"
                        + "( x + 2 y2 z2 + { 3 a } y2^2 + 5 y2^4 + { 2 c } y2 ),"
                        + "( 2 z2 + { 6 a } y2 + 20 y2^3 + { 2 c } )," + "( 3 z1^2 + y1^2 + { b } ),"
                        + "( 3 z2^2 + y2^2 + { b } )" + ")";
        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        PolynomialList<GenPolynomial<BigRational>> Fr = null;
        try {
            Fr = parser.nextPolynomialSet();
        } catch (ClassCastException e) {
            fail("" + e);
        } catch (IOException e) {
            fail("" + e);
        }
        GenPolynomialRing<BigRational> cofac = (GenPolynomialRing<BigRational>) Fr.ring.coFac;
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(), cofac);
        GenPolynomialRing<GenPolynomial<BigInteger>> rifac;
        rifac = new GenPolynomialRing<GenPolynomial<BigInteger>>(ifac, Fr.ring);
        List<GenPolynomial<GenPolynomial<BigInteger>>> Li;
        Li = PolyUfdUtil.integerFromRationalCoefficients(rifac, Fr.list);
        System.out.println("Fr = " + Fr);

        fac = new GenWordPolynomialRing<GenPolynomial<BigInteger>>(rifac);
        System.out.println("fac = " + fac.toScript());

        L = fac.valueOf(Li);
        System.out.println("L = " + L);
        L.addAll(fac.commute());
        System.out.println("L = " + L);

        long t, i;
        t = System.currentTimeMillis();
        G = bb.GB(L);
        t = System.currentTimeMillis() - t;
        System.out.println("G = " + G);
        assertTrue("isGB( G )", bb.isGB(G));


        QuotientRing<BigInteger> qi = new QuotientRing<BigInteger>(ifac);
        GenPolynomialRing<Quotient<BigInteger>> iring;
        iring = new GenPolynomialRing<Quotient<BigInteger>>(qi, rifac);
        List<GenPolynomial<Quotient<BigInteger>>> Lqi;
        Lqi = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(iring, Li);
        Lqi = PolyUtil.<Quotient<BigInteger>> monic(Lqi);
        System.out.println("Lqi = " + Lqi);

        WordGroebnerBaseAbstract<Quotient<BigInteger>> bbqi;
        bbqi = new WordGroebnerBaseSeq<Quotient<BigInteger>>(); //qi);
        System.out.println("bbqi = " + bbqi);

        GenWordPolynomialRing<Quotient<BigInteger>> qfac;
        qfac = new GenWordPolynomialRing<Quotient<BigInteger>>(iring);
        System.out.println("qfac = " + qfac.toScript());

        List<GenWordPolynomial<Quotient<BigInteger>>> Lq, Gq;
        Lq = qfac.valueOf(Lqi);
        System.out.println("Lq = " + Lq);
        Lq.addAll(qfac.commute());
        System.out.println("Lq = " + Lq);

        i = System.currentTimeMillis();
        Gq = bbqi.GB(Lq);
        i = System.currentTimeMillis() - i;
        System.out.println("Gq = " + Gq);
        assertTrue("isGB( Gq )", bbqi.isGB(Gq));

        System.out.println("time: intGB = " + t + ", quotIntGB = " + i);
    }

}
