/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import edu.jas.arith.BigRational;
import edu.jas.poly.RatOrderedMapPolynomial;
import edu.jas.poly.OrderedPolynomialTokenizer;
import edu.jas.poly.PolynomialList;

  /**
   * simple setup to run the Rose example.
   */

public class RunRose {


  public static void main( java.lang.String[] args ) {
      final int THREADS = 3;
      int threads = THREADS;
      if ( args.length > 0 ) {
	  try {
	      threads = Integer.parseInt( args[0] );
	  } catch (NumberFormatException e) { }
      }
      final int SPORT = 4711;
      int port = SPORT;
      if ( args.length > 1 ) {
	  try {
	      port = Integer.parseInt( args[1] );
	  } catch (NumberFormatException e) { }
      }

      String set = "(U3,U4,A46) G" 
                 + "( "  
                 + " U4^4 - 20/7 A46^2, "
                 + " A46^2 U3^4 + 7/10 A46 U3^4 + 7/48 U3^4 - 50/27 A46^2 - 35/27 A46 - 49/216, "
                 + " A46^5 U4^3 + 7/5 A46^4 U4^3 + 609/1000 A46^3 "
                 + " U4^3 + 49/1250 A46^2 U4^3 - 27391/800000 A46 U4^3 "
                 + " - 1029/160000 U4^3 + 3/7 A46^5 U3 U4^2 + 3/5 A46^6 "
                 + " U3 U4^2 + 63/200 A46^3 U3 U4^2 + 147/2000 A46^2 "
                 + " U3 U4^2 + 4137/800000 A46 U3 U4^2 - 7/20 A46^4 "
                 + " U3^2 U4 - 77/125 A46^3 U3^2 U4 - 23863/60000 A46^2 "
                 + " U3^2 U4 - 1078/9375 A46 U3^2 U4 - 24353/1920000 "
                 + " U3^2 U4 - 3/20 A46^4 U3^3 - 21/100 A46^3 U3^3 "
                 + " - 91/800 A46^2 U3^3 - 5887/200000 A46 U3^3 "
                 + " - 343/128000 U3^3 " 
                 + " ) ";

      Reader problem = new StringReader( set );
      OrderedPolynomialTokenizer tok = new OrderedPolynomialTokenizer(problem);
      System.out.println("start parsing"); 

      PolynomialList S = null; 
      try {
          S = tok.nextPolynomialSet();
      } catch (IOException e) { e.printStackTrace(); 
      }
      System.out.println("S =\n" + S); 

      runServer( S, threads, port );
      //runParallel( S, threads );
      //runSequential( S );
  }


  static void runServer(PolynomialList S, int threads, int port) {
      ArrayList L = S.list; 
      ArrayList G = null;
      long t;

      t = System.currentTimeMillis();
      System.out.println("\nGroebner base distributed ..."); 
      try {
          G = GroebnerBaseDistributed.DIRPGBServer( L, threads, port );
      } catch (IOException e) {
      }
      S = new PolynomialList( S.vars, S.tord, G );
      System.out.println("G =\n" + S ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t + " milliseconds" ); 
      System.out.println(""); 
  }

  static void runParallel(PolynomialList S, int threads) {
      ArrayList L = S.list; 
      ArrayList G;
      long t;

      t = System.currentTimeMillis();
      System.out.println("\nGroebner base parallel ..."); 
      G = GroebnerBaseParallel.DIRPGB(L,threads);
      S = new PolynomialList( S.vars, S.tord, G );
      System.out.println("G =\n" + S ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t + " milliseconds" ); 
      System.out.println(""); 
  }

  static void runSequential(PolynomialList S) {
      ArrayList L = S.list; 
      ArrayList G;
      long t;

      t = System.currentTimeMillis();
      System.out.println("\nGroebner base ..."); 
      G = GroebnerBase.DIRPGB(L);
      S = new PolynomialList( S.vars, S.tord, G );
      System.out.println("G =\n" + S ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t + " milliseconds" ); 
      System.out.println(""); 
  }

}
