/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;

import edu.jas.poly.RatPolynomial;

  /**
   * @deprecated
   */

public class testbigb {

  static void tuwas()
  {

      RatPolynomial A;
      RatPolynomial B;
      RatPolynomial C;
      int rl = 3; 
      int kl = 8;
      int ll = 10;
      int el = 3;
      float q = 0.3f;

      //  RatPolynomial.evord = ExpVector.IGRLEX;

      A = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("A = " + A ); 
      System.out.println("A.size() = " + A.length() ); 

      B = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("B = " + B ); 
      System.out.println("B.size() = " + B.length() ); 

      C = RatGBase.DIRPSP(A,B);
      System.out.println("SP = " + C ); 
      System.out.println("SP.size() = " + C.length() ); 


      ArrayList L = new ArrayList();
      L.add( (Object) A );
      L.add( (Object) B );
      //L.add( (Object) C );
      System.out.println("L = " + L ); 
      System.out.println("L.size() = " + L.size() ); 

      C = RatGBase.DIRPNF(L,C);
      System.out.println("NF = " + C ); 
      System.out.println("NF.size() = " + C.length() ); 
      
      if ( C.length() != 0 ) L.add(C); 
      System.out.println("L = " + L ); 
      System.out.println("L.size() = " + L.size() ); 
      L = RatGBase.DIRLIS(L);
      System.out.println("LIS = " + L ); 
      System.out.println("LIS.size() = " + L.size() ); 


      System.out.println("\nGroebner base ..."); 
      L = RatGBase.DIRPGB(L);
      System.out.println("GB = " + L ); 
      System.out.println("GB.size() = " + L.size() ); 

   
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
