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
import edu.jas.util.Datei;

/**
 * @deprecated
 */

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
      int loops = 5; //50;

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
         System.out.println("C = " + C.length() ); 
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
         System.out.println("C = " + C.length() ); 
         System.out.println("D = " + D.length() ); 
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

static void tuwas_rg_prod2() throws IOException
  {
      System.out.println("Timing multipy/multiply2-Polynomial \n"); 

      Datei pro = new Datei("protocol-prod2.txt",true);

      RatPolynomial A, B, C, D, E, F, ApB, AmB;
      int rl = 10; 
      int kl = 10;
      int ll = 100;
      int el = 5;
      float q = 0.3f;
      int loops = 5; //50;

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
          C = (RatPolynomial) RatPolynomial.DIPPR(A,B);
      }

      System.out.println("Timing RatPolynomial.DIPPR ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIPPR(A,B);
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatPolynomial) RatPolynomial.DIRPDF(AmB,C);
      if ( ! D.isZERO() ) {
	  System.out.println("C = " + C.length() ); 
	  System.out.println("D = " + D.length() ); 
      }

      System.out.println("Timing HashPolynomial.DIPPR ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) HashPolynomial.DIPPR(A,B);
      }
      tr = System.currentTimeMillis() - tr;
      D = (RatPolynomial) RatPolynomial.DIRPDF(AmB,C);
      if ( ! D.isZERO() ) {
	  System.out.println("C = " + C.length() ); 
	  System.out.println("D = " + D.length() ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)AmB.length();
      fr = (tr/(float)loops)/(float)AmB.length();

      System.out.println("Parameters:             " + new Date());
      System.out.println("Variables:              " + rl);
      System.out.println("Coefficient size:       " + kl);
      System.out.println("Number of coefficients: " + ll);
      System.out.println("Coefficients in result: " + C.length());
      System.out.println("maximal Degrees:        " + el);
      System.out.println("Exponent density:       " + q);
      System.out.println("Number of loops:        " + loops);

      System.out.println("time multiply =         " + tg + " milliseconds" ); 
      System.out.println("time multiply2 =        " + tr + " milliseconds" ); 
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

      pro.println("time multiply =         " + tg + " milliseconds" ); 
      pro.println("time multiply2 =        " + tr + " milliseconds" ); 
      pro.println("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr)); 
      pro.println("time c / loop / size =  " + fg ); 
      pro.println("time r / loop / size =  " + fr ); 

      pro.println("\n"); 
      pro.close();

  }

static void tuwas_rg_prod3() throws IOException
  {
      System.out.println("Timing multipy/multiply2-LinkedHashMap-Polynomial \n"); 

      Datei pro = new Datei("protocol-prod2.txt",true);

      RatPolynomial A, B, C, D, E, F, ApB, AmB;
      int rl = 10; 
      int kl = 10;
      int ll = 100;
      int el = 5;
      float q = 0.3f;
      int loops = 5; //50;

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
      
      AmB = (RatPolynomial) HashPolynomial.DIPPR(A,B);
      System.out.println("Prod = " + AmB ); 
      System.out.println("Prod.size() = " + AmB.length() ); 
      

      C = null;
      long tg, tr;

      System.out.println("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) HashPolynomial.DIPPR(A,B);
      }

      System.out.println("Timing DIPPR ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIPPR(A,B);
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatPolynomial) HashPolynomial.DIPDIF(AmB,C);
      if ( ! D.isZERO() ) {
	  System.out.println("C = " + C.length() ); 
	  System.out.println("D = " + D.length() ); 
      }

      System.out.println("Timing DIPPR2 ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) HashPolynomial.DIPPR(A,B);
      }
      tr = System.currentTimeMillis() - tr;
      D = (RatPolynomial) HashPolynomial.DIPDIF(AmB,C);
      if ( ! D.isZERO() ) {
	  System.out.println("C = " + C.length() ); 
	  System.out.println("D = " + D.length() ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)AmB.length();
      fr = (tr/(float)loops)/(float)AmB.length();

      System.out.println("Parameters:             " + new Date());
      System.out.println("Variables:              " + rl);
      System.out.println("Coefficient size:       " + kl);
      System.out.println("Number of coefficients: " + ll);
      System.out.println("Coefficients in result: " + C.length());
      System.out.println("maximal Degrees:        " + el);
      System.out.println("Exponent density:       " + q);
      System.out.println("Number of loops:        " + loops);

      System.out.println("time multiply =         " + tg + " milliseconds" ); 
      System.out.println("time multiply2 =        " + tr + " milliseconds" ); 
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

      pro.println("time multiply =         " + tg + " milliseconds" ); 
      pro.println("time multiply2 =        " + tr + " milliseconds" ); 
      pro.println("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr)); 
      pro.println("time c / loop / size =  " + fg ); 
      pro.println("time r / loop / size =  " + fr ); 

      pro.println("\n"); 
      pro.close();

  }


static void tuwas_rg_prod4() throws IOException
  {
      System.out.println("Timing multipy/multiply-Rat/RatOrderedMap-Polynomial \n"); 

      Datei pro = new Datei("protocol-prod3.txt",true);

      RatPolynomial A, B, C, D, E, F, ApB, AmB;
      OrderedMapPolynomial As, Bs, Cs, Ds, AmBs;
      int rl = 10; 
      int kl = 10;
      int ll = 100;
      int el = 5;
      float q = 0.3f;
      int loops = 5; //50;

      A = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("A = " + A ); 
      System.out.println("A.size() = " + A.length() ); 

      B = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("B = " + B ); 
      System.out.println("B.size() = " + B.length() ); 

      As = new RatOrderedMapPolynomial(A.getMap());
      Bs = new RatOrderedMapPolynomial(B.getMap());

      /*
      AsB = (RatPolynomial) RatPolynomial.DIRPSM(A,B);
      System.out.println("Sum = " + ApB ); 
      System.out.println("Sum.size() = " + ApB.length() ); 
      */
      
      AmB = (RatPolynomial) RatPolynomial.DIPPR(A,B);
      System.out.println("Prod = " + AmB ); 
      System.out.println("Prod.size() = " + AmB.length() ); 
      
      AmBs = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPPR(As,Bs);
      System.out.println("Prod = " + AmBs ); 
      System.out.println("Prod.size() = " + AmBs.length() ); 
      

      Cs = null; C = null;
      long tg, tr;

      System.out.println("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIPPR(A,B);
      }
      for (int i = 0; i < loops; i++) {
          Cs = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPPR(As,Bs);
      }

      System.out.println("Timing DIPPR ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIPPR(A,B);
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatPolynomial) RatPolynomial.DIPDIF(AmB,C);
      if ( ! D.isZERO() ) {
	  System.out.println("C = " + C.length() ); 
	  System.out.println("D = " + D.length() ); 
      }


      System.out.println("Timing Ordered DIPPR ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          Cs = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPPR(As,Bs);
      }
      tr = System.currentTimeMillis() - tr;
      Ds = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPDIF(AmBs,Cs);
      if ( ! Ds.isZERO() ) {
	  System.out.println("Cs = " + Cs.length() ); 
	  System.out.println("Ds = " + Ds.length() ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)AmB.length();
      fr = (tr/(float)loops)/(float)AmB.length();

      System.out.println("Parameters:             " + new Date());
      System.out.println("Variables:              " + rl);
      System.out.println("Coefficient size:       " + kl);
      System.out.println("Number of coefficients: " + ll);
      System.out.println("Coefficients in result: " + C.length());
      System.out.println("maximal Degrees:        " + el);
      System.out.println("Exponent density:       " + q);
      System.out.println("Number of loops:        " + loops);

      System.out.println("time multiply =         " + tg + " milliseconds" ); 
      System.out.println("time multiply2 =        " + tr + " milliseconds" ); 
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

      pro.println("time multiply =         " + tg + " milliseconds" ); 
      pro.println("time multiply2 =        " + tr + " milliseconds" ); 
      pro.println("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr)); 
      pro.println("time c / loop / size =  " + fg ); 
      pro.println("time r / loop / size =  " + fr ); 

      pro.println("\n"); 
      pro.close();

  }

static void tuwas_rg_prod5() throws IOException
  {
      System.out.println("Timing multipy/multiply-RatMap/RatOrderedMap-Polynomial \n"); 

      Datei pro = new Datei("protocol-prod4.txt",true);

      OrderedMapPolynomial A, B, C, D, E, F, ApB, AmB;
      MapPolynomial As, Bs, Cs, Ds, AmBs;
      int rl = 10; 
      int kl = 10;
      int ll = 40;
      int el = 5;
      float q = 0.3f;
      int loops = 5; //50;

      A = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("A = " + A ); 
      System.out.println("A.size() = " + A.length() ); 

      B = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("B = " + B ); 
      System.out.println("B.size() = " + B.length() ); 

      As = new RatMapPolynomial(A.getMap());
      Bs = new RatMapPolynomial(B.getMap());

      /*
      AsB = (RatPolynomial) RatPolynomial.DIRPSM(A,B);
      System.out.println("Sum = " + ApB ); 
      System.out.println("Sum.size() = " + ApB.length() ); 
      */
      
      AmB = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPPR(A,B);
      System.out.println("Prod = " + AmB ); 
      System.out.println("Prod.size() = " + AmB.length() ); 
      
      AmBs = (RatMapPolynomial) RatMapPolynomial.DIPPR(As,Bs);
      System.out.println("Prod = " + AmBs ); 
      System.out.println("Prod.size() = " + AmBs.length() ); 
      

      Cs = null; C = null;
      long tg, tr;

      System.out.println("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          C = (RatOrderedMapPolynomial) A.multiply(B); //RatOrderedMapPolynomial.DIPPR(A,B);
      }
      for (int i = 0; i < loops; i++) {
          Cs = (RatMapPolynomial) As.multiply(Bs); //RatMapPolynomial.DIPPR(As,Bs);
      }

      System.out.println("Timing OrderedMap multiply ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatOrderedMapPolynomial) A.multiply(B); // RatOrderedMapPolynomial.DIPPR(A,B);
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPDIF(AmB,C);
      if ( ! D.isZERO() ) {
	  System.out.println("C = " + C.length() ); 
	  System.out.println("D = " + D.length() ); 
      }


      System.out.println("Timing Map multiply ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          Cs = (RatMapPolynomial) As.multiply(Bs); //RatMapPolynomial.DIPPR(As,Bs);
      }
      tr = System.currentTimeMillis() - tr;
      Ds = (RatMapPolynomial) RatMapPolynomial.DIPDIF(AmBs,Cs);
      if ( ! Ds.isZERO() ) {
	  System.out.println("Cs = " + Cs.length() ); 
	  System.out.println("Ds = " + Ds.length() ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)AmB.length();
      fr = (tr/(float)loops)/(float)AmB.length();

      System.out.println("Parameters:             " + new Date());
      System.out.println("Variables:              " + rl);
      System.out.println("Coefficient size:       " + kl);
      System.out.println("Number of coefficients: " + ll);
      System.out.println("Coefficients in result: " + C.length());
      System.out.println("maximal Degrees:        " + el);
      System.out.println("Exponent density:       " + q);
      System.out.println("Number of loops:        " + loops);

      System.out.println("time multiply =         " + tg + " milliseconds" ); 
      System.out.println("time multiply2 =        " + tr + " milliseconds" ); 
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

      pro.println("time multiply =         " + tg + " milliseconds" ); 
      pro.println("time multiply2 =        " + tr + " milliseconds" ); 
      pro.println("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr)); 
      pro.println("time c / loop / size =  " + fg ); 
      pro.println("time r / loop / size =  " + fr ); 

      pro.println("\n"); 
      pro.close();

  }

static void tuwas_rg_prod6() throws IOException
  {
      System.out.println("Timing multipy(evord)/multiply-RatMap/RatOrderedMap-Polynomial \n"); 

      Datei pro = new Datei("protocol-prod5.txt",true);

      OrderedMapPolynomial A, B, C, D, E, F, ApB, AmB;
      MapPolynomial As, Bs, Cs, Ds, AmBs;
      int rl = 10; 
      int kl = 10;
      int ll = 40;
      int el = 5;
      float q = 0.3f;
      int loops = 5; //50;

      int evord = TermOrder.DEFAULT_EVORD;

      A = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("A = " + A ); 
      System.out.println("A.size() = " + A.length() ); 

      B = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("B = " + B ); 
      System.out.println("B.size() = " + B.length() ); 

      As = new RatMapPolynomial(A.getMap());
      Bs = new RatMapPolynomial(B.getMap());

      /*
      AsB = (RatPolynomial) RatPolynomial.DIRPSM(A,B);
      System.out.println("Sum = " + ApB ); 
      System.out.println("Sum.size() = " + ApB.length() ); 
      */
      
      AmB = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPPR(A,B);
      System.out.println("Prod = " + AmB ); 
      System.out.println("Prod.size() = " + AmB.length() ); 
      
      AmBs = (RatMapPolynomial) RatMapPolynomial.DIPPR(As,Bs);
      System.out.println("Prod = " + AmBs ); 
      System.out.println("Prod.size() = " + AmBs.length() ); 
      

      Cs = null; C = null;
      long tg, tr;

      System.out.println("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          C = (RatOrderedMapPolynomial) A.multiply(B); 
      }
      for (int i = 0; i < loops; i++) {
          Cs = (RatMapPolynomial)((RatMapPolynomial) As).multiply(evord,Bs); 
      }

      System.out.println("Timing OrderedMap multiply ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatOrderedMapPolynomial) A.multiply(B); 
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPDIF(AmB,C);
      if ( ! D.isZERO() ) {
	  System.out.println("C = " + C.length() ); 
	  System.out.println("D = " + D.length() ); 
      }


      System.out.println("Timing Map multiply ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          Cs = (RatMapPolynomial)((RatMapPolynomial) As).multiply(evord,Bs); 
      }
      tr = System.currentTimeMillis() - tr;
      Ds = (RatMapPolynomial) RatMapPolynomial.DIPDIF(AmBs,Cs);
      if ( ! Ds.isZERO() ) {
	  System.out.println("Cs = " + Cs.length() ); 
	  System.out.println("Ds = " + Ds.length() ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)AmB.length();
      fr = (tr/(float)loops)/(float)AmB.length();

      System.out.println("Parameters:             " + new Date());
      System.out.println("Variables:              " + rl);
      System.out.println("Coefficient size:       " + kl);
      System.out.println("Number of coefficients: " + ll);
      System.out.println("Coefficients in result: " + C.length());
      System.out.println("maximal Degrees:        " + el);
      System.out.println("Exponent density:       " + q);
      System.out.println("Number of loops:        " + loops);

      System.out.println("time multiply =         " + tg + " milliseconds" ); 
      System.out.println("time multiply2 =        " + tr + " milliseconds" ); 
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

      pro.println("time multiply =         " + tg + " milliseconds" ); 
      pro.println("time multiply2 =        " + tr + " milliseconds" ); 
      pro.println("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr)); 
      pro.println("time c / loop / size =  " + fg ); 
      pro.println("time r / loop / size =  " + fr ); 

      pro.println("\n"); 
      pro.close();

  }

static void tuwas_rg_prod7() throws IOException
  {
      System.out.println("Timing multipy(evord)/multiply-RatMap-Polynomial \n"); 

      Datei pro = new Datei("protocol-prod6.txt",true);

      OrderedMapPolynomial A, B, C, D, E, F, ApB, AmB;
      MapPolynomial As, Ap, Bs, Bp, Cs, Cp, Ds, Dp, AmBs, AmBp;
      int rl = 10; 
      int kl = 10;
      int ll = 40;
      int el = 5;
      float q = 0.3f;
      int loops = 5; //50;

      int evord = TermOrder.DEFAULT_EVORD;

      A = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("A = " + A ); 
      System.out.println("A.size() = " + A.length() ); 

      B = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("B = " + B ); 
      System.out.println("B.size() = " + B.length() ); 

      As = new RatMapPolynomial(A.getMap());
      Bs = new RatMapPolynomial(B.getMap());
      Ap = As;
      Bp = Bs;

      /*
      AsB = (RatPolynomial) RatPolynomial.DIRPSM(A,B);
      System.out.println("Sum = " + ApB ); 
      System.out.println("Sum.size() = " + ApB.length() ); 
      */
      
      AmBp = (RatMapPolynomial) RatMapPolynomial.DIPPR(Ap,Bp);
      System.out.println("Prod = " + AmBp ); 
      System.out.println("Prod.size() = " + AmBp.length() ); 
      
      AmBs = (RatMapPolynomial) RatMapPolynomial.DIPPR(As,Bs);
      System.out.println("Prod = " + AmBs ); 
      System.out.println("Prod.size() = " + AmBs.length() ); 
      

      Cs = null; C = null; Cp = null;
      long tg, tr;

      System.out.println("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          Cp = (RatMapPolynomial) Ap.multiply(Bp); 
      }
      for (int i = 0; i < loops; i++) {
          Cs = (RatMapPolynomial)((RatMapPolynomial) As).multiply(evord,Bs); 
      }

      System.out.println("Timing Map multiply ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          Cp = (RatMapPolynomial) Ap.multiply(Bp); 
      }
      tg = System.currentTimeMillis() - tg;
      Dp = (RatMapPolynomial) RatMapPolynomial.DIPDIF(AmBp,Cp);
      if ( ! Dp.isZERO() ) {
	  System.out.println("Cp = " + Cp.length() ); 
	  System.out.println("Dp = " + Dp.length() ); 
      }


      System.out.println("Timing Map multiply(evord) ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          Cs = (RatMapPolynomial)((RatMapPolynomial) As).multiply(evord,Bs); 
      }
      tr = System.currentTimeMillis() - tr;
      Ds = (RatMapPolynomial) RatMapPolynomial.DIPDIF(AmBs,Cs);
      if ( ! Ds.isZERO() ) {
	  System.out.println("Cs = " + Cs.length() ); 
	  System.out.println("Ds = " + Ds.length() ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)AmBp.length();
      fr = (tr/(float)loops)/(float)AmBs.length();

      System.out.println("Parameters:             " + new Date());
      System.out.println("Variables:              " + rl);
      System.out.println("Coefficient size:       " + kl);
      System.out.println("Number of coefficients: " + ll);
      System.out.println("Coefficients in result: " + Cp.length());
      System.out.println("maximal Degrees:        " + el);
      System.out.println("Exponent density:       " + q);
      System.out.println("Number of loops:        " + loops);

      System.out.println("time multiply =         " + tg + " milliseconds" ); 
      System.out.println("time multiply2 =        " + tr + " milliseconds" ); 
      System.out.println("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr) ); 
      System.out.println("time c / loop / size =  " + fg ); 
      System.out.println("time r / loop / size =  " + fr ); 
      System.out.println();

      // Protokoll
      pro.println("Parameters:             " + new Date());
      pro.println("Variables:              " + rl);
      pro.println("Coefficient size:       " + kl);
      pro.println("Number of coefficients: " + ll);
      pro.println("Coefficients in result: " + Cp.length());
      pro.println("maximal Degrees:        " + el);
      pro.println("Exponent density:       " + q);
      pro.println("Number of loops:        " + loops);

      pro.println("time multiply =         " + tg + " milliseconds" ); 
      pro.println("time multiply2 =        " + tr + " milliseconds" ); 
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
      // tuwas_rg_sum();
      // tuwas_rg_prod();
      tuwas_rg_prod2();
      // tuwas_rg_prod3();
      // tuwas_rg_prod4();
      // tuwas_rg_prod5();
      // tuwas_rg_prod6();
      // tuwas_rg_prod7();
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
