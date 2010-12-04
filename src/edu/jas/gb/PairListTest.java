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
    public void testRandomPL() {
        pairlist = new OrderedPairlist<BigRational>(fac);
        //System.out.println("pairlist = " + pairlist);

        CriticalPairList<BigRational> cpl = new CriticalPairList<BigRational>(fac);
        //System.out.println("cpl = " + cpl);

        L = new ArrayList<GenPolynomial<BigRational>>();
        for ( int i = 0; i < 7; i++ ) {
            a = fac.random(kl, ll, el, q );
            if ( a.isZERO() ) {
                continue;
            }
            pairlist.put(a);
            cpl.put(a);
        }
        //System.out.println("pairlist = " + pairlist);
        //System.out.println("cpl = " + cpl);

        while ( pairlist.hasNext() && cpl.hasNext() ) {
            Pair<BigRational> pair = pairlist.removeNext();
            //System.out.println("pair = " + pair);
            CriticalPair<BigRational> cpair = cpl.getNext();
            //System.out.println("cpair = " + cpair);
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
        //System.out.println("pairlist = " + pairlist);
        //System.out.println("cpl = " + cpl);
        boolean t = pairlist.hasNext() || cpl.hasNext();
        assertFalse("#pairlist == #cpl ", t);
    }


    /**
     * Test and compare random OrderedPairlist and OrderedSyzPairlist.
     */
    public void testRandomSyzPL() {
        pairlist = new OrderedPairlist<BigRational>(fac);
        //System.out.println("pairlist = " + pairlist);

        OrderedSyzPairlist<BigRational> spl = new OrderedSyzPairlist<BigRational>(fac);
        //System.out.println("spl = " + spl);

        L = new ArrayList<GenPolynomial<BigRational>>();
        for ( int i = 0; i < 7; i++ ) {
            a = fac.random(kl, ll, el, q );
            if ( a.isZERO() ) {
                continue;
            }
            pairlist.put(a);
            spl.put(a);
        }
        //System.out.println("pairlist = " + pairlist);
        //System.out.println("spl = " + spl);

        while ( pairlist.hasNext() && spl.hasNext() ) {
            Pair<BigRational> pair = pairlist.removeNext();
            //System.out.println("pair = " + pair);
            Pair<BigRational> spair = spl.removeNext();
            //System.out.println("spair = " + spair);
            if ( pair != null && spair != null ) {
                boolean t = (pair.i == spair.i) && (pair.j == spair.j);
                //not always true: assertTrue("pair == spair " + pair + ", " + spair, t);
            }
        }
        //System.out.println("pairlist = " + pairlist);
        //System.out.println("spl = " + spl);
        boolean t = pairlist.hasNext() && spl.hasNext();
        assertFalse("#pairlist == #spl ", t);
    }

}
