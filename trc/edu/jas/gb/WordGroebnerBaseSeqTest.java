/*
 * $Id$
 */

package edu.jas.gb;


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

import edu.jas.arith.BigRational;
import edu.jas.poly.WordFactory;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;


/**
 * Groebner base sequential tests with JUnit.
 * @author Heinz Kredel.
 */

public class WordGroebnerBaseSeqTest extends TestCase {

    //private static final Logger logger = Logger.getLogger(WordGroebnerBaseSeqTest.class);

    /**
     * main
     */
    public static void main (String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run( suite() );
    }


    /**
     * Constructs a <CODE>WordGroebnerBaseSeqTest</CODE> object.
     * @param name String.
     */
    public WordGroebnerBaseSeqTest(String name) {
        super(name);
    }


    /**
     * suite.
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(WordGroebnerBaseSeqTest.class);
        return suite;
    }


    GenWordPolynomialRing<BigRational> fac;
    WordFactory wfac;

    List<GenWordPolynomial<BigRational>> L;
    PolynomialList<BigRational> F;
    List<GenWordPolynomial<BigRational>> G;

    WordGroebnerBase<BigRational> bb;

    GenWordPolynomial<BigRational> a;
    GenWordPolynomial<BigRational> b;
    GenWordPolynomial<BigRational> c;
    GenWordPolynomial<BigRational> d;
    GenWordPolynomial<BigRational> e;

    int rl = 4; //4; //3; 
    int kl = 3; // 10
    int ll = 5;
    int el = 3; // 4


    protected void setUp() {
        BigRational coeff = new BigRational(0);
        wfac = new WordFactory("abcd");
        fac = new GenWordPolynomialRing<BigRational>(coeff,wfac);
        a = b = c = d = e = null;
        bb = new WordGroebnerBaseSeq<BigRational>();
    }


    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bb = null;
    }


    /**
     * Test sequential Word GBase.
     */
    public void testSequentialGBase() {
        L = new ArrayList<GenWordPolynomial<BigRational>>();
        a = fac.random(kl, ll, el );
        b = fac.random(kl, ll, el );
        c = fac.random(kl, ll, el );
        d = fac.random(kl, ll, el );
        e = d; //fac.random(kl, ll, el );

        if ( a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO() ) {
            return;
        }

        L.add(a);
        L = bb.GB( L );
        assertTrue("isGB( { a } )", bb.isGB(L) );

        L.add(b);
        //System.out.println("L = " + L);
        L = bb.GB( L );
        assertTrue("isGB( { a, b } )", bb.isGB(L) );

        L.add(c);
        //System.out.println("L = " + L);
        L = bb.GB( L );
        assertTrue("isGB( { a, b, c } )", bb.isGB(L) );

        L.add(d);
        //System.out.println("L = " + L);
        L = bb.GB( L );
        assertTrue("isGB( { a, b, c, d } )", bb.isGB(L) );

        L.add(e);
        //System.out.println("L = " + L);
        L = bb.GB( L );
        assertTrue("isGB( { a, b, c, d, e } )", bb.isGB(L) );
    }


    /**
     * Test Trinks7 GBase.
     */
    @SuppressWarnings("unchecked") 
    public void xtestTrinks7GBase() {
        String exam = "(B,S,T,Z,P,W) L "
            + "( "  
            + "( 45 P + 35 S - 165 B - 36 ), " 
            + "( 35 P + 40 Z + 25 T - 27 S ), "
            + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
            + "( - 9 W + 15 T P + 20 S Z ), "
            + "( P W + 2 T Z - 11 B**3 ), "
            + "( 99 W - 11 B S + 3 B**2 ), "
            + "( B**2 + 33/50 B + 2673/10000 ) "
            + ") ";
        String exam2 = "(x,y,z) L "
            + "( "
            + "( z y**2 + 2 x + 1/2 )"
            + "( z x**2 - y**2 - 1/2 x )"
            + "( -z + y**2 x + 4 x**2 + 1/4 )"
            + " )";

        Reader source = new StringReader( exam );
        GenPolynomialTokenizer parser
            = new GenPolynomialTokenizer( source );
        try {
            F = null; 
            System.out.println("parser: " + (PolynomialList<BigRational>) parser.nextPolynomialSet());
        } catch(ClassCastException e) {
            fail(""+e);
        } catch(IOException e) {
            fail(""+e);
        }
        //System.out.println("F = " + F);

        //G = bb.GB(F.list);
        //assertTrue("isGB( GB(Trinks7) )", bb.isGB(G) );
        //assertEquals("#GB(Trinks7) == 6", 6, G.size() );
        //PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring,G);
        //System.out.println("G = " + trinks);
    }

}



