/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import edu.jas.arith.BigRational;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;

import edu.jas.structure.RingElem;
//import edu.jas.structure.RingFactory;


/**
 * GenPolynomial Test using JUnit.
 * @author Heinz Kredel.
 */

public class GenPolynomialTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>GenPolynomialTest</CODE> object.
 * @param name String.
 */
   public GenPolynomialTest(String name) {
          super(name);
   }

/**
 * suite.
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(GenPolynomialTest.class);
     return suite;
   }

   //private final static int bitlen = 100;

   int rl = 6; 
   int kl = 10;
   int ll = 7;
   int el = 4;
   float q = 0.5f;

   protected void setUp() {
       // a = b = c = d = e = null;
   }

   protected void tearDown() {
       // a = b = c = d = e = null;
   }


/**
 * Test constructors and factory.
 * 
 */
 public void testConstructors() {
        // rational numbers
        BigRational rf = new BigRational();
        // System.out.println("rf = " + rf);

        BigRational r = rf.fromInteger( 99 );
        // System.out.println("r = " + r);
        r = rf.random( 9 );
        // System.out.println("r = " + r);

        RingElem<BigRational> re = new BigRational( 3 );
        // System.out.println("re = " + re);


        // polynomials over rational numbers
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(rf,2);
        // System.out.println("pf = " + pf);

        GenPolynomial<BigRational> p = pf.getONE();
        // System.out.println("p = " + p);
        p = pf.random( 9 );
        // System.out.println("p = " + p);
        p = pf.getZERO();
        // System.out.println("p = " + p);

        RingElem< GenPolynomial<BigRational> > pe = new GenPolynomial<BigRational>( pf );
        //System.out.println("pe = " + pe);
        //System.out.println("p.equals(pe) = " + p.equals(pe) );
        //System.out.println("p.equals(p) = " + p.equals(p) );
        assertTrue("p.equals(pe) = ", p.equals(pe) );
        assertTrue("p.equals(p) = ", p.equals(p) );

        pe = pe.sum( p ); // why not p = p.add(pe) ?
        //System.out.println("pe = " + pe);
        p = pf.random( 9 );
        p = p.subtract( p ); 
        //System.out.println("p = " + p);
        //System.out.println("p.isZERO() = " + p.isZERO());
        assertTrue("p.isZERO() = ", p.isZERO());


        // polynomials over (polynomials over rational numbers)
        GenPolynomialRing< GenPolynomial<BigRational> > ppf = new GenPolynomialRing< GenPolynomial<BigRational> >(pf,3);
        // System.out.println("ppf = " + ppf);

        GenPolynomial< GenPolynomial<BigRational> > pp = ppf.getONE();
        // System.out.println("pp = " + pp);
        pp = ppf.random( 2 );
        // System.out.println("pp = " + pp);
        pp = ppf.getZERO();
        // System.out.println("pp = " + pp);

        RingElem< GenPolynomial< GenPolynomial<BigRational> > > ppe = new GenPolynomial< GenPolynomial<BigRational> >( ppf );
        // System.out.println("ppe = " + ppe);
        // System.out.println("pp.equals(ppe) = " + pp.equals(ppe) );
        // System.out.println("pp.equals(pp) = " + pp.equals(pp) );
        assertTrue("pp.equals(ppe) = ", pp.equals(ppe) );
        assertTrue("pp.equals(pp) = ", pp.equals(pp) );

        ppe = ppe.sum( pp ); // why not pp = pp.add(ppe) ?
        //System.out.println("ppe = " + ppe);
        pp = ppf.random( 2 );
        pp = pp.subtract( pp ); 
        //System.out.println("pp = " + pp);
        //System.out.println("pp.isZERO() = " + pp.isZERO());
        assertTrue("pp.isZERO() = ", pp.isZERO());


        // polynomials over (polynomials over (polynomials over rational numbers))
        GenPolynomialRing< GenPolynomial< GenPolynomial<BigRational> > > pppf = new GenPolynomialRing< GenPolynomial< GenPolynomial<BigRational> > >(ppf,4);
        // System.out.println("pppf = " + pppf);

        GenPolynomial< GenPolynomial< GenPolynomial<BigRational> > > ppp = pppf.getONE();
        //System.out.println("ppp = " + ppp);
        ppp = pppf.random( 2 );
        // System.out.println("ppp = " + ppp);
        ppp = pppf.getZERO();
        // System.out.println("ppp = " + ppp);

        RingElem< GenPolynomial< GenPolynomial< GenPolynomial<BigRational> > > > pppe = new GenPolynomial< GenPolynomial< GenPolynomial<BigRational> > >( pppf );
        // System.out.println("pppe = " + pppe);
        // System.out.println("ppp.equals(pppe) = " + ppp.equals(pppe) );
        // System.out.println("ppp.equals(ppp) = " + ppp.equals(ppp) );
        assertTrue("ppp.equals(pppe) = ", ppp.equals(pppe) );
        assertTrue("ppp.equals(ppp) = ", ppp.equals(ppp) );

        pppe = pppe.sum( ppp ); // why not ppp = ppp.add(pppe) ?
        // System.out.println("pppe = " + pppe);
        ppp = pppf.random( 2 );
        ppp = ppp.subtract( ppp ); 
        // System.out.println("ppp = " + ppp);
        // System.out.println("ppp.isZERO() = " + ppp.isZERO());
        assertTrue("ppp.isZERO() = ", ppp.isZERO());

        // some tests
        //GenPolynomial<BigRational> pfx = new GenPolynomial<BigRational>();
        //System.out.println("pfx = " + pfx);

    }


/**
 * Test extension and contraction.
 * 
 */
 public void testExtendContract() {
     // rational numbers
     BigRational cf = new BigRational( 99 );
     // System.out.println("cf = " + cf);

     // polynomials over rational numbers
     GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(cf,rl);
     // System.out.println("pf = " + pf);

     GenPolynomial<BigRational> a = pf.random(kl,ll,el,q);
     //System.out.println("a = " + a);

     int k = rl;
     GenPolynomialRing<BigRational> pfe = pf.extend(k);
     GenPolynomialRing<BigRational> pfec = pfe.contract(k);
     assertEquals("pf == pfec",pf,pfec);

     GenPolynomial<BigRational> ae = a.extend(pfe,0,0);

     Map<ExpVector,GenPolynomial<BigRational>> m = ae.contract(pfec);
     List<GenPolynomial<BigRational>> ml = new ArrayList<GenPolynomial<BigRational>>( m.values() );
     GenPolynomial<BigRational> aec = ml.get(0);
     assertEquals("a == aec",a,aec);
     //System.out.println("ae = " + ae);
     //System.out.println("aec = " + aec);
 }


/**
 * Test reversion.
 * 
 */
 public void testReverse() {
     // rational numbers
     BigRational cf = new BigRational( 99 );
     // System.out.println("cf = " + cf);

     // polynomials over rational numbers
     GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(cf,rl);
     //System.out.println("pf = " + pf);

     GenPolynomial<BigRational> a = pf.random(kl,ll,el,q);
     //System.out.println("a = " + a);

     int k = rl;
     GenPolynomialRing<BigRational> pfr = pf.reverse();
     GenPolynomialRing<BigRational> pfrr = pfr.reverse();
     assertEquals("pf == pfrr",pf,pfrr);
     //System.out.println("pfr = " + pfr);

     GenPolynomial<BigRational> ar = a.reverse(pfr);
     GenPolynomial<BigRational> arr = ar.reverse(pfrr);
     assertEquals("a == arr",a,arr);
     //System.out.println("ar = " + ar);
     //System.out.println("arr = " + arr);
 }



}
