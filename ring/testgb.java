/*
 * $Id$
 */

package edu.jas.ring;

import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigRational;
import edu.jas.poly.RatPolynomial;

  /**
   * @deprecated
   */

public class testgb 
{

  static void tuwas()
  {

      RatPolynomial p1, p2, p3, p4, p5, p6, p7;
      p1 = new RatPolynomial("1 (0,0,0,0,0,0) ");
      p2 = p1; p3 = p1; p4 = p1; p5 = p1; p6 = p1; p7 = p1;

      p1 = new RatPolynomial( " 45 (0,1,0,0,0,0) 35 (0,0,0,0,1,0) -165 (0,0,0,0,0,1) -36 (0,0,0,0,0,0) " );
      System.out.println("p1 = " + p1 ); 

      p2 = new RatPolynomial( " 35 (0,1,0,0,0,0) 40 (0,0,1,0,0,0) 25 (0,0,0,1,0,0) -27 (0,0,0,0,1,0) " );
      System.out.println("p2 = " + p2 ); 

      p3 = new RatPolynomial( " 15 (1,0,0,0,0,0) 25 (0,1,0,0,1,0) 30 (0,0,1,0,0,0) -18 (0,0,0,1,0,0) -165 (0,0,0,0,0,2) " );
      System.out.println("p3 = " + p3 );
      
      p4 = new RatPolynomial( " -9 (1,0,0,0,0,0) 15 (0,1,0,1,0,0) 20 (0,0,1,0,1,0) " );
      System.out.println("p4 = " + p4 );

      p5 = new RatPolynomial( " 1 (1,1,0,0,0,0) 2 (0,0,1,1,0,0) -11 (0,0,0,0,0,3) " );
      System.out.println("p5 = " + p5 );

      p6 = new RatPolynomial( " 99 (1,0,0,0,0,0) -11 (0,0,0,0,1,1) 3 (0,0,0,0,0,2) " );
      System.out.println("p6 = " + p6 );

      p7 = new RatPolynomial( " 1 (0,0,0,0,0,2) 33/50 (0,0,0,0,0,1)  2673/10000 (0,0,0,0,0,0)" );
      System.out.println("p7 = " + p7 );

      ArrayList L = new ArrayList();
      L.add( (Object) p1 );
      L.add( (Object) p2 );
      L.add( (Object) p3 );
      L.add( (Object) p4 );
      L.add( (Object) p5 );
      L.add( (Object) p6 );
      //                      L.add( (Object) p7 ); // trinks 6 oder 7
      System.out.println("L = " + L ); 
      System.out.println("L.size() = " + L.size() ); 

      long t = System.currentTimeMillis();
      System.out.println("\nGroebner base ..."); 
      ArrayList G = RatGBase.DIRPGB(L);
      System.out.println("G = " + G ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t + " milliseconds" ); 

  
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
