/*
 * $Id$
 */

package edu.jas.poly;

//import java.math.BigInteger;
//import edu.jas.arith.BigRational;
//import edu.jas.poly.RatPolynomial;
//import edu.jas.poly.IntPolynomial;

import java.util.Date;
import java.io.IOException;
import edu.jas.Datei;

public class testbirg
{

static void tuwas_rg_sum() throws IOException
  {
      System.out.println("Timing Rat/Coeff-Polynomial \n"); 

      Datei pro = new Datei("protocol-sum.txt",true);

      RatPolynomial A, B, C, D, E, F, ApB, AmB;
      int rl = 50; 
      int kl = 100;
      int ll = 2000;
      int el = 5;
      float q = 0.5f;
      int loops = 1000;

      A = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("A = " + A ); 
      System.out.println("A.size() = " + A.length() ); 

      B = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("B = " + B ); 
      System.out.println("B.size() = " + B.length() ); 

      ApB = (RatPolynomial) RatPolynomial.DIRPSM(A,B);
      System.out.println("Sum = " + ApB ); 
      System.out.println("Sum.size() = " + ApB.length() ); 

      /*
      AmB = (RatPolynomial) RatPolynomial.DIRPPR(A,B);
      System.out.println("Prod = " + AmB ); 
      System.out.println("Prod.size() = " + AmB.length() ); 
      */

      C = null;
      long tg, tr;

      System.out.println("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIRPSM(A,B);
      }

      System.out.println("Timing DIPSUM ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIPSUM(A,B);
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatPolynomial) RatPolynomial.DIRPDF(ApB,C);
      if ( ! D.isZERO() ) {
         System.out.println("C = " + C ); 
         System.out.println("D = " + D ); 
      }

      System.out.println("Timing DIRPSM ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIRPSM(A,B);
      }
      tr = System.currentTimeMillis() - tr;
      D = (RatPolynomial) RatPolynomial.DIRPDF(ApB,C);
      if ( ! D.isZERO() ) {
         System.out.println("C = " + C ); 
         System.out.println("D = " + D ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)C.length();
      fr = (tr/(float)loops)/(float)C.length();

      System.out.println("Parameters:             " + new Date());
      System.out.println("Variables:              " + rl);
      System.out.println("Coefficient size:       " + kl);
      System.out.println("Number of coefficients: " + ll);
      System.out.println("Coefficients in result: " + C.length());
      System.out.println("maximal Degrees:        " + el);
      System.out.println("Exponent density:       " + q);
      System.out.println("Number of loops:        " + loops);

      System.out.println("time coefficient =      " + tg + " milliseconds" ); 
      System.out.println("time rational =         " + tr + " milliseconds" ); 
      System.out.println("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr) ); 
      System.out.println("time c / loop / size =  " + fg ); 
      System.out.println("time r / loop / size =  " + fr ); 
      System.out.println();

      // Protokoll
      pro.println("Parameters:             " + new Date());
      pro.println("Variables:              " + rl);
      pro.println("Coefficient size:       " + kl);
      pro.println("Number of coefficients: " + ll);
      pro.println("Coefficients in result: " + C.length());
      pro.println("maximal Degrees:        " + el);
      pro.println("Exponent density:       " + q);
      pro.println("Number of loops:        " + loops);

      pro.println("time coefficient =      " + tg + " milliseconds" ); 
      pro.println("time rational =         " + tr + " milliseconds" ); 
      pro.println("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr)); 
      pro.println("time c / loop / size =  " + fg ); 
      pro.println("time r / loop / size =  " + fr ); 

      pro.println("\n"); 
      pro.close();

  }


static void tuwas_rg_prod() throws IOException
  {
      System.out.println("Timing Rat/Coeff-Polynomial \n"); 

      Datei pro = new Datei("protocol-prod.txt",true);

      RatPolynomial A, B, C, D, E, F, ApB, AmB;
      int rl = 10; 
      int kl = 10;
      int ll = 100;
      int el = 5;
      float q = 0.3f;
      int loops = 50;

      A = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("A = " + A ); 
      System.out.println("A.size() = " + A.length() ); 

      B = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("B = " + B ); 
      System.out.println("B.size() = " + B.length() ); 

      /*
      ApB = (RatPolynomial) RatPolynomial.DIRPSM(A,B);
      System.out.println("Sum = " + ApB ); 
      System.out.println("Sum.size() = " + ApB.length() ); 
      */
      
      AmB = (RatPolynomial) RatPolynomial.DIRPPR(A,B);
      System.out.println("Prod = " + AmB ); 
      System.out.println("Prod.size() = " + AmB.length() ); 
      

      C = null;
      long tg, tr;

      System.out.println("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIRPPR(A,B);
      }

      System.out.println("Timing DIPPR ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIPPR(A,B);
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatPolynomial) RatPolynomial.DIRPDF(AmB,C);
      if ( ! D.isZERO() ) {
         System.out.println("C = " + C ); 
         System.out.println("D = " + D ); 
      }

      System.out.println("Timing DIRPPR ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIRPPR(A,B);
      }
      tr = System.currentTimeMillis() - tr;
      D = (RatPolynomial) RatPolynomial.DIRPDF(AmB,C);
      if ( ! D.isZERO() ) {
         System.out.println("C = " + C ); 
         System.out.println("D = " + D ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)C.length();
      fr = (tr/(float)loops)/(float)C.length();

      System.out.println("Parameters:             " + new Date());
      System.out.println("Variables:              " + rl);
      System.out.println("Coefficient size:       " + kl);
      System.out.println("Number of coefficients: " + ll);
      System.out.println("Coefficients in result: " + C.length());
      System.out.println("maximal Degrees:        " + el);
      System.out.println("Exponent density:       " + q);
      System.out.println("Number of loops:        " + loops);

      System.out.println("time coefficient =      " + tg + " milliseconds" ); 
      System.out.println("time rational =         " + tr + " milliseconds" ); 
      System.out.println("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr) ); 
      System.out.println("time c / loop / size =  " + fg ); 
      System.out.println("time r / loop / size =  " + fr ); 
      System.out.println();

      // Protokoll
      pro.println("Parameters:             " + new Date());
      pro.println("Variables:              " + rl);
      pro.println("Coefficient size:       " + kl);
      pro.println("Number of coefficients: " + ll);
      pro.println("Coefficients in result: " + C.length());
      pro.println("maximal Degrees:        " + el);
      pro.println("Exponent density:       " + q);
      pro.println("Number of loops:        " + loops);

      pro.println("time coefficient =      " + tg + " milliseconds" ); 
      pro.println("time rational =         " + tr + " milliseconds" ); 
      pro.println("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr)); 
      pro.println("time c / loop / size =  " + fg ); 
      pro.println("time r / loop / size =  " + fr ); 

      pro.println("\n"); 
      pro.close();

  }



  public static void main( java.lang.String[] _args ) throws IOException
  {
    try
    {
      //      tuwas_rg_sum();
      tuwas_rg_prod();
      // tuwas_i();
      if (_args != null)
      {
        java.lang.System.runFinalization();
        java.lang.System.exit(0);
      }
    }
    finally { }
  }
}
