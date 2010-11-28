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
import edu.jas.gb.GroebnerBase;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;


/**
 * Groebner base sequential tests with JUnit.
 * @author Heinz Kredel.
 */

public class PairListTest extends TestCase {


    /**
     * main
     */
    public static void main (String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run( suite() );
    }

    /**
     * Constructs a <CODE>PairListTest</CODE> object.
     * @param name String.
     */
    public PairListTest(String name) {
        super(name);
    }

    /**
     * suite.
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(PairListTest.class);
        return suite;
    }

    GenPolynomialRing<BigRational> fac;

    List<GenPolynomial<BigRational>> L;
    PolynomialList<BigRational> F;
    List<GenPolynomial<BigRational>> G;

    PairList<BigRational> pairlist;

    GenPolynomial<BigRational> a;
    GenPolynomial<BigRational> b;
    GenPolynomial<BigRational> c;
    GenPolynomial<BigRational> d;
    GenPolynomial<BigRational> e;

    int rl = 4; //4; //3; 
    int kl = 5;
    int ll = 2;
    int el = 4;
    float q = 0.3f; //0.4f

    protected void setUp() {
        BigRational coeff = new BigRational(9);
        fac = new GenPolynomialRing<BigRational>(coeff,rl);
        a = b = c = d = e = null;
    }

    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
    }


    /**
     * Test and compare random OrderedPairlist and CriticalPairList.
     */
    public void xtestRandomPL() {
        pairlist = new OrderedPairlist<BigRational>(fac);
        System.out.println("pairlist = " + pairlist);

        CriticalPairList<BigRational> cpl = new CriticalPairList<BigRational>(fac);
        System.out.println("cpl = " + cpl);

        L = new ArrayList<GenPolynomial<BigRational>>();
        for ( int i = 0; i < 7; i++ ) {
             a = fac.random(kl, ll, el, q );
             if ( a.isZERO() ) {
                 continue;
             }
             pairlist.put(a);
             cpl.put(a);
        }
        System.out.println("pairlist = " + pairlist);
        System.out.println("cpl = " + cpl);

        while ( pairlist.hasNext() && cpl.hasNext() ) {
            Pair<BigRational> pair = pairlist.removeNext();
            System.out.println("pair = " + pair);
            CriticalPair<BigRational> cpair = cpl.getNext();
            System.out.println("cpair = " + cpair);
            if ( cpair != null ) {
                cpl.update(cpair,fac.getZERO());
            } else {
                cpl.update();
            }
            if ( pair != null && cpair != null ) {
                boolean t = (pair.i == cpair.i) && (pair.j == cpair.j);
                assertTrue("pair == cpair ", t);
            }
        }
        System.out.println("pairlist = " + pairlist);
        System.out.println("cpl = " + cpl);
        boolean t = pairlist.hasNext() || cpl.hasNext();
        assertFalse("#pairlist == #cpl ", t);
    }


    /**
     * Test and compare random OrderedPairlist and OrderedSyzPairlist.
     */
    public void testRandomSyzPL() {
        pairlist = new OrderedPairlist<BigRational>(fac);
        System.out.println("pairlist = " + pairlist);

        OrderedSyzPairlist<BigRational> spl = new OrderedSyzPairlist<BigRational>(fac);
        System.out.println("spl = " + spl);

        L = new ArrayList<GenPolynomial<BigRational>>();
        for ( int i = 0; i < 7; i++ ) {
             a = fac.random(kl, ll, el, q );
             if ( a.isZERO() ) {
                 continue;
             }
             pairlist.put(a);
             spl.put(a);
        }
        System.out.println("pairlist = " + pairlist);
        System.out.println("spl = " + spl);

        while ( pairlist.hasNext() && spl.hasNext() ) {
            Pair<BigRational> pair = pairlist.removeNext();
            System.out.println("pair = " + pair);
            Pair<BigRational> spair = spl.removeNext();
            System.out.println("spair = " + spair);
            if ( pair != null && spair != null ) {
                boolean t = (pair.i == spair.i) && (pair.j == spair.j);
                //assertTrue("pair == spair " + pair + ", " + spair, t);
            }
        }
        System.out.println("pairlist = " + pairlist);
        System.out.println("spl = " + spl);
        boolean t = pairlist.hasNext() && spl.hasNext();
        assertFalse("#pairlist == #spl ", t);
    }


    /**
     * Test Trinks7 GBase.
     * 
     */
    @SuppressWarnings("unchecked") // not jet working
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
        Reader source = new StringReader( exam );
        GenPolynomialTokenizer parser
            = new GenPolynomialTokenizer( source );
        try {
            F = (PolynomialList<BigRational>) parser.nextPolynomialSet();
        } catch(ClassCastException e) {
            fail(""+e);
        } catch(IOException e) {
            fail(""+e);
        }
        //System.out.println("F = " + F);

        pairlist = new OrderedPairlist<BigRational>(F.ring);

    }

}
