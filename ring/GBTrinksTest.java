/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.Iterator;
//import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
import edu.jas.poly.RatPolynomial;
import edu.jas.poly.ExpVector;
import edu.jas.poly.RatOrderedMapPolynomial;

  /**
   * Test GB with Trinks example
   */

public class GBTrinksTest extends TestCase {

    private static Logger logger = Logger.getLogger(GBTrinksTest.class);

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>GBTrinksTest</CODE> object.
 * @param name String
 */
   public GBTrinksTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(GBTrinksTest.class);
     return suite;
   }

    ArrayList La;
    ArrayList Ga;
    ArrayList Lb;
    ArrayList Gb;

    RatPolynomial           p1, p2, p3, p4, p5, p6, p7;
    RatOrderedMapPolynomial q1, q2, q3, q4, q5, q6, q7;

   protected void setUp() {
       La = new ArrayList();
       Ga = null;

      p1 = new RatPolynomial( " 45 (0,1,0,0,0,0) 35 (0,0,0,0,1,0) -165 (0,0,0,0,0,1) -36 (0,0,0,0,0,0) " );
      if ( logger.isDebugEnabled() ) {
         logger.debug("p1 = " + p1 ); 
      }

      p2 = new RatPolynomial( " 35 (0,1,0,0,0,0) 40 (0,0,1,0,0,0) 25 (0,0,0,1,0,0) -27 (0,0,0,0,1,0) " );
      if ( logger.isDebugEnabled() ) {
         logger.debug("p2 = " + p2 ); 
      }

      p3 = new RatPolynomial( " 15 (1,0,0,0,0,0) 25 (0,1,0,0,1,0) 30 (0,0,1,0,0,0) -18 (0,0,0,1,0,0) -165 (0,0,0,0,0,2) " );
      if ( logger.isDebugEnabled() ) {
         logger.debug("p3 = " + p3 );
      }
      
      p4 = new RatPolynomial( " -9 (1,0,0,0,0,0) 15 (0,1,0,1,0,0) 20 (0,0,1,0,1,0) " );
      if ( logger.isDebugEnabled() ) {
         logger.debug("p4 = " + p4 );
      }

      p5 = new RatPolynomial( " 1 (1,1,0,0,0,0) 2 (0,0,1,1,0,0) -11 (0,0,0,0,0,3) " );
      if ( logger.isDebugEnabled() ) {
         logger.debug("p5 = " + p5 );
      }

      p6 = new RatPolynomial( " 99 (1,0,0,0,0,0) -11 (0,0,0,0,1,1) 3 (0,0,0,0,0,2) " );
      if ( logger.isDebugEnabled() ) {
         logger.debug("p6 = " + p6 );
      }

      p7 = new RatPolynomial( " 1 (0,0,0,0,0,2) 33/50 (0,0,0,0,0,1)  2673/10000 (0,0,0,0,0,0)" );
      if ( logger.isDebugEnabled() ) {
         logger.debug("p7 = " + p7 );
      }

      La.add( (Object) p1 );
      La.add( (Object) p2 );
      La.add( (Object) p3 );
      La.add( (Object) p4 );
      La.add( (Object) p5 );
      La.add( (Object) p6 );
      //                      La.add( (Object) p7 ); // trinks 6 oder 7

       Lb = new ArrayList();
       Gb = null;

      q1 = RatOrderedMapPolynomial.fromString( p1.toString() );
      if ( logger.isDebugEnabled() ) {
         logger.debug("q1 = " + q1 ); 
      }

      q2 = RatOrderedMapPolynomial.fromString( p2.toString() );
      if ( logger.isDebugEnabled() ) {
         logger.debug("q2 = " + q2 ); 
      }

      q3 = RatOrderedMapPolynomial.fromString( p3.toString() );
      if ( logger.isDebugEnabled() ) {
         logger.debug("q3 = " + q3 );
      }
      
      q4 = RatOrderedMapPolynomial.fromString( p4.toString() );
      if ( logger.isDebugEnabled() ) {
         logger.debug("q4 = " + q4 );
      }

      q5 = RatOrderedMapPolynomial.fromString( p5.toString() );
      if ( logger.isDebugEnabled() ) {
         logger.debug("q5 = " + q5 );
      }

      q6 = RatOrderedMapPolynomial.fromString( p6.toString() );
      if ( logger.isDebugEnabled() ) {
         logger.debug("q6 = " + q6 );
      }

      q7 = RatOrderedMapPolynomial.fromString( p7.toString() );
      if ( logger.isDebugEnabled() ) {
         logger.debug("q7 = " + q7 );
      }

      Lb.add( (Object) q1 );
      Lb.add( (Object) q2 );
      Lb.add( (Object) q3 );
      Lb.add( (Object) q4 );
      Lb.add( (Object) q5 );
      Lb.add( (Object) q6 );
      //                      Lb.add( (Object) q7 ); // trinks 6 oder 7
   }

   protected void tearDown() {
       La = null;
       Ga = null;
       Lb = null;
       Gb = null;
   }

/**
 * Test Trinks 6Groebner Base with RatPolynomial
 */ 

  public void notestRatPolynomial() {
      if ( logger.isDebugEnabled() ) {
         logger.debug("La = " + La ); 
         logger.debug("La.size() = " + La.size() ); 
      }
      long t = System.currentTimeMillis();
      logger.info("\nGroebner base ..."); 
      Ga = RatGBase.DIRPGB(La);
      if ( logger.isDebugEnabled() ) {
         logger.debug("Ga = " + Ga ); 
      }
      logger.info("Ga.size() = " + Ga.size() ); 
      t = System.currentTimeMillis() - t;
      logger.info("time = " + t + " milliseconds" ); 

      int evord = p1.getOrd();
      if ( evord == ExpVector.IGRLEX ) {
         assertTrue("#(trinks)=6", La.size() == 6 );
         assertTrue("#GB(trinks)=14", Ga.size() == 14 );
      }
      if ( evord == ExpVector.INVLEX ) {
         assertTrue("#(trinks)=6", La.size() == 6 );
         assertTrue("#GB(trinks)=6", Ga.size() == 6 );
      }
  }


/**
 * Test Trinks 6 Groebner Base with RatOrderedMapPolynomial
 */ 

  public void notestRatOrderedMapPolynomial() {
      if ( logger.isDebugEnabled() ) {
         logger.debug("Lb = " + Lb ); 
         logger.debug("Lb.size() = " + Lb.size() ); 
      }

      long t = System.currentTimeMillis();
      logger.info("\nGroebner base ..."); 
      Gb = GroebnerBase.DIRPGB(Lb);
      if ( logger.isDebugEnabled() ) {
         logger.debug("Gb = " + Gb ); 
      }
      logger.info("Gb.size() = " + Gb.size() ); 
      t = System.currentTimeMillis() - t;
      logger.info("time = " + t + " milliseconds" ); 

      int evord = q1.getTermOrder().getEvord();
      if ( evord == ExpVector.IGRLEX ) {
         assertTrue("#(trinks)=6", Lb.size() == 6 );
         assertTrue("#GB(trinks)=14", Gb.size() == 14 );
      }
      if ( evord == ExpVector.INVLEX ) {
         assertTrue("#(trinks)=6", Lb.size() == 6 );
         assertTrue("#GB(trinks)=6", Gb.size() == 6 );
      }
  }


/**
 * Test Trinks 6 Groebner Base with RatPolynomial and RatOrderedMapPolynomial
 */ 

  public void testRatAndRatOrderedMapPolynomial() {
      if ( logger.isDebugEnabled() ) {
         logger.debug("La = " + La ); 
         logger.debug("La.size() = " + La.size() ); 
      }
      long t = System.currentTimeMillis();
      logger.info("\nGroebner base ..."); 
      Ga = RatGBase.DIRPGB(La);
      if ( logger.isDebugEnabled() ) {
         logger.debug("Ga = " + Ga ); 
      }
      logger.info("Ga.size() = " + Ga.size() ); 
      t = System.currentTimeMillis() - t;
      logger.info("time = " + t + " milliseconds" ); 

      if ( logger.isDebugEnabled() ) {
         logger.debug("Lb = " + Lb ); 
         logger.debug("Lb.size() = " + Lb.size() ); 
      }

      t = System.currentTimeMillis();
      logger.info("\nGroebner base ..."); 
      Gb = GroebnerBase.DIRPGB(Lb);
      if ( logger.isDebugEnabled() ) {
         logger.debug("Gb = " + Gb ); 
      }
      logger.info("Gb.size() = " + Gb.size() ); 
      t = System.currentTimeMillis() - t;
      logger.info("time = " + t + " milliseconds" ); 

      Iterator ia = Ga.iterator(); 
      Iterator ib = Gb.iterator(); 
      while ( ia.hasNext() && ib.hasNext() ) {
	  RatPolynomial p = (RatPolynomial) ia.next();
	  RatOrderedMapPolynomial q = (RatOrderedMapPolynomial) ib.next();
	  RatOrderedMapPolynomial r = RatOrderedMapPolynomial.fromString( p.toString() );
	  assertEquals("qi.equals(pi)", q, r);
      }
      assertFalse("Ga = Gb = empty " , ia.hasNext() || ib.hasNext() );
  }

}
