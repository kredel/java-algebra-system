/*
 * $Id$
 */

package edu.jas.ring;

import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import edu.jas.arith.BigRational;
import edu.jas.poly.RatPolynomial;
import edu.jas.poly.PolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.ExpVector;

  /**
   * @deprecated
   */

public class testpar
{

  static void tuwas(String[] args)
  {
      final int THREADS = 3;
      int threads = THREADS;
      if ( args.length > 0 ) {
	  try {
	      threads = Integer.parseInt( args[0] );
	  } catch (NumberFormatException e) { }
      }

      String[] vars = new String[] { "B", "S", "T", "Z", "P", "W" };
      int evord = ExpVector.INVLEX;
      //int evord = ExpVector.IGRLEX;
      String poly = "(B,S,T,Z,P,W) L" 
                  + "( "
                  + "45 P + 35 S - 165 B - 36, "
                  + "35 P + 40 Z + 25 T - 27 S, "
                  + "15 W + 25 S P + 30 Z - 18 T - 165 B^2, "
                  + "- 9 W + 15 T P + 20 S Z, "
                  + "P W + 2 T Z - 11 B^3, "
            	  + "99 W - 11 B S + 3 B^2, "  
	  //	            + "B^2 + 33/50 B + 2673/10000, "
                  + ") ";

      Reader problem = new StringReader( poly );
      PolynomialTokenizer tok = new PolynomialTokenizer(problem);
      System.out.println("start parsing"); 
      PolynomialList S = null; 
      try {
          S = tok.nextPolynomialSet();
      } catch (IOException e) { e.printStackTrace(); 
      }
      System.out.println("S =\n" + S); 
      ArrayList L = S.list; 

      long t;
      ArrayList G;
      
      t = System.currentTimeMillis();
      System.out.println("\nGroebner base " + threads 
                       + "-times parallel ..."); 
      G = RatGBase.DIRPGBparallel(L,threads);
      S = new PolynomialList( S.vars, S.tord, G );
      System.out.println("G =\n" + S ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t + " milliseconds" ); 
      System.out.println(""); 
      /*
      t = System.currentTimeMillis();
      System.out.println("\nGroebner base ..."); 
      G = RatGBase.DIRPGB(L);
      S = new PolynomialList( S.vars, S.tord, G );
      System.out.println("G =\n" + S ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t + " milliseconds" ); 
      System.out.println(""); 
      */
  }



  public static void main( java.lang.String[] _args )
  {
    try
    {
      tuwas( _args );
      if (_args != null)
      {
        java.lang.System.runFinalization();
        java.lang.System.exit(0);
      }
    }
    finally { }
  }
}
