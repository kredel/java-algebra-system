/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.poly.WordFactory;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.gb.WordGroebnerBase;


/**
 * Groebner base sequential tests with JUnit.
 * @author Heinz Kredel.
 */

public class WordGroebnerBasePseudoSeqTest extends TestCase {

    //private static final Logger logger = Logger.getLogger(WordGroebnerBasePseudoSeqTest.class);

    /**
     * main
     */
    public static void main (String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run( suite() );
    }


    /**
     * Constructs a <CODE>WordGroebnerBasePseudoSeqTest</CODE> object.
     * @param name String.
     */
    public WordGroebnerBasePseudoSeqTest(String name) {
        super(name);
    }


    /**
     * suite.
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(WordGroebnerBasePseudoSeqTest.class);
        return suite;
    }


    GenWordPolynomialRing<BigInteger> fac;
    WordFactory wfac;

    List<GenWordPolynomial<BigInteger>> L, G;
    PolynomialList<BigInteger> F;

    WordGroebnerBase<BigInteger> bb;

    GenWordPolynomial<BigInteger> a, b, c, d, e;

    int kl = 3; // 10
    int ll = 7;
    int el = 4; // 4


    protected void setUp() {
        BigInteger coeff = new BigInteger(0);
        wfac = new WordFactory("a");
        fac = new GenWordPolynomialRing<BigInteger>(coeff,wfac);
        a = b = c = d = e = null;
        bb = new WordGroebnerBasePseudoSeq<BigInteger>(coeff);
    }


    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bb = null;
    }


    /**
     * Test sequential univariate Word GBase.
     */
    public void testSequentialGBase() {
        L = new ArrayList<GenWordPolynomial<BigInteger>>();
        a = fac.random(kl, ll, el);
        b = fac.random(kl, ll, el);
        c = fac.random(kl, ll, el);
        d = fac.random(kl, ll, el);
        e = d; //fac.random(kl, ll, el);
        while ( a.isZERO() ) {
            a = fac.random(kl, ll, el);
        }
        while ( b.isZERO() ) {
            b = fac.random(kl, ll, el);
        }
        while ( c.isZERO() ) {
            c = fac.random(kl, ll, el);
        }
        while ( d.isZERO() ) {
            d = fac.random(kl, ll, el);
        }

        L.add(a);
        //System.out.println("L = " + L);
        L = bb.GB( L );
        assertTrue("isGB( { a } )", bb.isGB(L) );

        L.add(a.multiply(b));
        //System.out.println("L = " + L);
        L = bb.GB( L );
        assertTrue("isGB( { a, b } )", bb.isGB(L) );

        L.add(a.multiply(c));
        //System.out.println("L = " + L);
        L = bb.GB( L );
        assertTrue("isGB( { a, b, c } )", bb.isGB(L) );

        L.add(a.multiply(d));
        //System.out.println("L = " + L);
        L = bb.GB( L );
        assertTrue("isGB( { a, b, c, d } )", bb.isGB(L) );

        L.add(e);
        //System.out.println("L = " + L);
        L = bb.GB( L );
        assertTrue("isGB( { a, b, c, d, e } )", bb.isGB(L) );

        L.clear();
        L.add(a);
        L.add(a.multiply(b));
        L.add(a.multiply(c));
        L.add(a.multiply(d));
        //System.out.println("L = " + L);
        L = bb.GB( L );
        assertTrue("isGB( { a, b, c, d } )", bb.isGB(L) );
    }


    /**
     * Test example 1 word GBase.
     */
    @SuppressWarnings("unchecked") 
    public void testExample1GBase() {
        String exam = "(x,y,z) L "
            + "( "
            + "( z y**2 + 2 x + 1/2 )"
            + "( z x**2 - y**2 - 1/2 x )"
            + "( -z + y**2 x + 4 x**2 + 1/4 )"
            + " )";

        Reader source = new StringReader( exam );
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer( source );
        try {
            F = (PolynomialList<BigInteger>) parser.nextPolynomialSet();
        } catch(ClassCastException e) {
            fail(""+e);
        } catch(IOException e) {
            fail(""+e);
        }
        //System.out.println("F = " + F);

        fac = new GenWordPolynomialRing(F.ring);
        //System.out.println("fac = " + fac);

        L = fac.valueOf(F.list);
        //System.out.println("L = " + L);

        G = bb.GB(L);
        //System.out.println("G = " + G);
        assertTrue("isGB( G )", bb.isGB(G) );
    }


    /**
     * Test Trinks7 as non-commutative example word GBase.
     */
    @SuppressWarnings("unchecked") 
    public void testTrinks7GBase() {
        String exam = "(B,S,T,Z,P,W) L "
            + "( "  
            + "( 45 P + 35 S - 165 B - 36 ), " 
            + "( 35 P + 40 Z + 25 T - 27 S ), "
            + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
            + "( - 9 W + 15 T P + 20 S Z ), "
            + "( P W + 2 T Z - 11 B**3 ), "
            + "( 99 W - 11 B S + 3 B**2 ), "
            + "( B**2 + 33/50 B + 2673/10000 ) " // is needed
            + ") ";

        Reader source = new StringReader( exam );
        GenPolynomialTokenizer parser
            = new GenPolynomialTokenizer( source );
        try {
            F = (PolynomialList<BigInteger>) parser.nextPolynomialSet();
        } catch(ClassCastException e) {
            fail(""+e);
        } catch(IOException e) {
            fail(""+e);
        }
        System.out.println("F = " + F);

        fac = new GenWordPolynomialRing(F.ring);
        //System.out.println("fac = " + fac);

        L = fac.valueOf(F.list);
        System.out.println("L = " + L);

        G = bb.GB(L);
        //System.out.println("G = " + G);
        assertTrue("isGB( G )", bb.isGB(G) );
        assertTrue("#G == 6", G.size() == 6);
    }


    /**
     * Test example 3 word GBase.
     */
    @SuppressWarnings("unchecked") 
    public void testExample2GBase() {
        String exam = "(x,y,z) L "
            + "( "
            + "( x y - z )" // will not be correct when converted to non-com
            + "( y z + 2 x + z )"
            + "( y z + x )"
            + " )";

        Reader source = new StringReader( exam );
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer( source );
        try {
            F = (PolynomialList<BigInteger>) parser.nextPolynomialSet();
        } catch(ClassCastException e) {
            fail(""+e);
        } catch(IOException e) {
            fail(""+e);
        }
        //System.out.println("F = " + F);

        fac = new GenWordPolynomialRing(F.ring);
        //System.out.println("fac = " + fac);

        L = fac.valueOf(F.list);
        //System.out.println("L = " + L);

        G = bb.GB(L);
        //System.out.println("G = " + G);
        assertTrue("isGB( G )", bb.isGB(G) );
    }

}



