/*
 * $Id$
 */

package edu.jas.poly;

import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import edu.jas.arith.BigRational;
//import edu.jas.poly.RatPolynomial;

public class testrose
{

  static void tuwas()
  {
      String set = "(U3,U4,A46) L" 
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
      PolynomialTokenizer tok = new PolynomialTokenizer(problem);
      System.out.println("start parsing"); 

      PolynomialList S = null; 
      try {
          S = tok.nextPolynomialSet();
      } catch (IOException e) { e.printStackTrace(); 
      }
      System.out.println("S =\n" + S); 
      ArrayList L = S.list; 

      ArrayList G;
      long t;

      t = System.currentTimeMillis();
      System.out.println("\nGroebner base parallel ..."); 
      G = RatGBase.DIRPGBparallel(L);
      S = new PolynomialList( S.vars, S.evord, G );
      System.out.println("G =\n" + S ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t + " milliseconds" ); 
      System.out.println(""); 
      /*
      t = System.currentTimeMillis();
      System.out.println("\nGroebner base ..."); 
      G = RatGBase.DIRPGB(L);
      S = new PolynomialList( S.vars, S.evord, G );
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
      tuwas();
      if (_args != null)
      {
        java.lang.System.runFinalization();
        java.lang.System.exit(0);
      }
    }
    finally { }
  }
}
