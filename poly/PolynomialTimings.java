/*
 * $Id$
 */

package edu.jas.poly;

//import java.math.BigInteger;
//import edu.jas.arith.BigRational;
//import edu.jas.poly.RatPolynomial;
//import edu.jas.poly.IntPolynomial;

import org.apache.log4j.Logger;

import java.util.Date;
import java.io.IOException;

/**
 * Timings for various aspects of polynomial implementation details.
 * @author Heinz Kredel
 */

public class PolynomialTimings {

    private static final int dauer = 5; // 1=kurs, 5=mittel, 20=lang

    private static Logger logger = Logger.getLogger(PolynomialTimings.class);


static void sumRatGeneric() throws IOException {
      logger.info("Timing Rat/Coeff-Polynomial sumRatGeneric "); 

      RatPolynomial A, B, C, D, E, F, ApB, AmB;
      int rl = 50; 
      int kl = 100;
      int ll = 200*dauer;
      int el = 5;
      float q = 0.5f;
      int loops = 100;

      A = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("A = " + A ); 
      logger.info("A.size() = " + A.length() ); 

      B = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("B = " + B ); 
      logger.info("B.size() = " + B.length() ); 

      ApB = (RatPolynomial) RatPolynomial.DIRPSM(A,B);
      if ( logger.isDebugEnabled() ) logger.debug("Sum = " + ApB ); 
      logger.info("Sum.size() = " + ApB.length() ); 

      C = null;
      long tg, tr;

      logger.info("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIRPSM(A,B);
      }

      logger.info("Timing DIPSUM ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIPSUM(A,B);
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatPolynomial) RatPolynomial.DIRPDF(ApB,C);
      if ( ! D.isZERO() ) {
         logger.error("C = " + C ); 
         logger.error("D = " + D ); 
      }

      logger.info("Timing DIRPSM ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIRPSM(A,B);
      }
      tr = System.currentTimeMillis() - tr;
      D = (RatPolynomial) RatPolynomial.DIRPDF(ApB,C);
      if ( ! D.isZERO() ) {
         logger.error("C = " + C ); 
         logger.error("D = " + D ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)C.length();
      fr = (tr/(float)loops)/(float)C.length();

      logger.info("Parameters:             " + new Date());
      logger.info("Variables:              " + rl);
      logger.info("Coefficient size:       " + kl);
      logger.info("Number of coefficients: " + ll);
      logger.info("Coefficients in result: " + C.length());
      logger.info("maximal Degrees:        " + el);
      logger.info("Exponent density:       " + q);
      logger.info("Number of loops:        " + loops);

      logger.info("time coefficient =      " + tg + " milliseconds" ); 
      logger.info("time rational =         " + tr + " milliseconds" ); 
      logger.info("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr) ); 
      logger.info("time c / loop / size =  " + fg ); 
      logger.info("time r / loop / size =  " + fr ); 
      logger.info("----------");
  }


static void prodRatGeneric() throws IOException {
      logger.info("Timing Rat/Coeff-Polynomial prodRatGeneric "); 

      RatPolynomial A, B, C, D, E, F, ApB, AmB;
      int rl = 10; 
      int kl = 10;
      int ll = 10*dauer;
      int el = 5;
      float q = 0.3f;
      int loops = 50; //50;

      A = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("A = " + A ); 
      logger.info("A.size() = " + A.length() ); 

      B = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("B = " + B ); 
      logger.info("B.size() = " + B.length() ); 

      AmB = (RatPolynomial) RatPolynomial.DIRPPR(A,B);
      if ( logger.isDebugEnabled() ) logger.debug("Prod = " + AmB ); 
      logger.info("Prod.size() = " + AmB.length() ); 
      

      C = null;
      long tg, tr;

      logger.info("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIRPPR(A,B);
      }

      logger.info("Timing DIPPR ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIPPR(A,B);
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatPolynomial) RatPolynomial.DIRPDF(AmB,C);
      if ( ! D.isZERO() ) {
         logger.error("C = " + C.length() ); 
         logger.error("D = " + D ); 
      }

      logger.info("Timing DIRPPR ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIRPPR(A,B);
      }
      tr = System.currentTimeMillis() - tr;
      D = (RatPolynomial) RatPolynomial.DIRPDF(AmB,C);
      if ( ! D.isZERO() ) {
         logger.error("C = " + C.length() ); 
         logger.error("D = " + D.length() ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)C.length();
      fr = (tr/(float)loops)/(float)C.length();

      logger.info("Parameters:             " + new Date());
      logger.info("Variables:              " + rl);
      logger.info("Coefficient size:       " + kl);
      logger.info("Number of coefficients: " + ll);
      logger.info("Coefficients in result: " + C.length());
      logger.info("maximal Degrees:        " + el);
      logger.info("Exponent density:       " + q);
      logger.info("Number of loops:        " + loops);

      logger.info("time coefficient =      " + tg + " milliseconds" ); 
      logger.info("time rational =         " + tr + " milliseconds" ); 
      logger.info("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr) ); 
      logger.info("time c / loop / size =  " + fg ); 
      logger.info("time r / loop / size =  " + fr ); 
      logger.info("----------");
  }

static void prodTreeHash() throws IOException {
      logger.info("Timing multipy/multiply2-Polynomial prodTreeHash "); 

      RatPolynomial A, B, C, D, E, F, ApB, AmB;
      int rl = 10; 
      int kl = 10;
      int ll = 10*dauer;
      int el = 5;
      float q = 0.3f;
      int loops = 50; //50;

      A = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("A = " + A ); 
      logger.info("A.size() = " + A.length() ); 

      B = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("B = " + B ); 
      logger.info("B.size() = " + B.length() ); 

      AmB = (RatPolynomial) RatPolynomial.DIRPPR(A,B);
      if ( logger.isDebugEnabled() ) logger.debug("Prod = " + AmB ); 
      logger.info("Prod.size() = " + AmB.length() ); 
      

      C = null;
      long tg, tr;

      logger.info("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIPPR(A,B);
      }

      logger.info("Timing TreePolynomial.DIPPR ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIPPR(A,B);
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatPolynomial) RatPolynomial.DIRPDF(AmB,C);
      if ( ! D.isZERO() ) {
	  logger.error("C = " + C.length() ); 
	  logger.error("D = " + D.length() ); 
      }

      logger.info("Timing HashPolynomial.DIPPR ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) HashPolynomial.DIPPR(A,B);
      }
      tr = System.currentTimeMillis() - tr;
      D = (RatPolynomial) RatPolynomial.DIRPDF(AmB,C);
      if ( ! D.isZERO() ) {
	  logger.error("C = " + C.length() ); 
	  logger.error("D = " + D.length() ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)AmB.length();
      fr = (tr/(float)loops)/(float)AmB.length();

      logger.info("Parameters:             " + new Date());
      logger.info("Variables:              " + rl);
      logger.info("Coefficient size:       " + kl);
      logger.info("Number of coefficients: " + ll);
      logger.info("Coefficients in result: " + C.length());
      logger.info("maximal Degrees:        " + el);
      logger.info("Exponent density:       " + q);
      logger.info("Number of loops:        " + loops);

      logger.info("time multiply =         " + tg + " milliseconds" ); 
      logger.info("time multiply2 =        " + tr + " milliseconds" ); 
      logger.info("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr) ); 
      logger.info("time c / loop / size =  " + fg ); 
      logger.info("time r / loop / size =  " + fr ); 
      logger.info("----------");
  }


static void prodMapOrdered() throws IOException {
      logger.info("Timing multipy/multiply-Rat/RatOrderedMap-Polynomial prodMapOrdered"); 

      RatPolynomial A, B, C, D, E, F, ApB, AmB;
      OrderedMapPolynomial As, Bs, Cs, Ds, AmBs;
      int rl = 10; 
      int kl = 10;
      int ll = 10*dauer;
      int el = 5;
      float q = 0.3f;
      int loops = 50; //50;

      A = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("A = " + A ); 
      logger.info("A.size() = " + A.length() ); 

      B = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("B = " + B ); 
      logger.info("B.size() = " + B.length() ); 

      As = new RatOrderedMapPolynomial(A.getMap());
      Bs = new RatOrderedMapPolynomial(B.getMap());


      AmB = (RatPolynomial) RatPolynomial.DIPPR(A,B);
      if ( logger.isDebugEnabled() ) logger.debug("Prod = " + AmB ); 
      logger.info("Prod.size() = " + AmB.length() ); 
      
      AmBs = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPPR(As,Bs);
      if ( logger.isDebugEnabled() ) logger.debug("Prod = " + AmBs ); 
      logger.info("Prod.size() = " + AmBs.length() ); 
      

      Cs = null; C = null;
      long tg, tr;

      logger.info("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIPPR(A,B);
      }
      for (int i = 0; i < loops; i++) {
          Cs = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPPR(As,Bs);
      }

      logger.info("Timing RatPolynomial.DIPPR ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) RatPolynomial.DIPPR(A,B);
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatPolynomial) RatPolynomial.DIPDIF(AmB,C);
      if ( ! D.isZERO() ) {
	  logger.error("C = " + C.length() ); 
	  logger.error("D = " + D.length() ); 
      }


      logger.info("Timing RatOrderedMapPolynomial.DIPPR ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          Cs = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPPR(As,Bs);
      }
      tr = System.currentTimeMillis() - tr;
      Ds = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPDIF(AmBs,Cs);
      if ( ! Ds.isZERO() ) {
	  logger.error("Cs = " + Cs.length() ); 
	  logger.error("Ds = " + Ds.length() ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)AmB.length();
      fr = (tr/(float)loops)/(float)AmB.length();

      logger.info("Parameters:             " + new Date());
      logger.info("Variables:              " + rl);
      logger.info("Coefficient size:       " + kl);
      logger.info("Number of coefficients: " + ll);
      logger.info("Coefficients in result: " + C.length());
      logger.info("maximal Degrees:        " + el);
      logger.info("Exponent density:       " + q);
      logger.info("Number of loops:        " + loops);

      logger.info("time multiply =         " + tg + " milliseconds" ); 
      logger.info("time multiply2 =        " + tr + " milliseconds" ); 
      logger.info("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr) ); 
      logger.info("time c / loop / size =  " + fg ); 
      logger.info("time r / loop / size =  " + fr ); 
      logger.info("----------");
  }

static void prodRatMapOrdered() throws IOException {
      logger.info("Timing multipy/multiply-RatPolynomial/RatOrderedMap-Polynomial prodRatMapOrdered"); 

      RatPolynomial A, B, C, D, E, F, ApB, AmB;
      OrderedMapPolynomial As, Bs, Cs, Ds, AmBs;
      int rl = 10; 
      int kl = 10;
      int ll = 10*dauer;
      int el = 5;
      float q = 0.3f;
      int loops = 50; //50;

      A = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("A = " + A ); 
      logger.info("A.size() = " + A.length() ); 

      B = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("B = " + B ); 
      logger.info("B.size() = " + B.length() ); 

      As = new RatOrderedMapPolynomial(A.getMap());
      Bs = new RatOrderedMapPolynomial(B.getMap());

      AmB = (RatPolynomial) A.multiply(B);
      if ( logger.isDebugEnabled() ) logger.debug("Prod = " + AmB ); 
      logger.info("Prod.size() = " + AmB.length() ); 
      
      AmBs = (RatOrderedMapPolynomial) As.multiply(Bs);
      if ( logger.isDebugEnabled() ) logger.debug("Prod = " + AmBs ); 
      logger.info("Prod.size() = " + AmBs.length() ); 
      

      Cs = null; C = null;
      long tg, tr;

      logger.info("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) A.multiply(B); 
      }
      for (int i = 0; i < loops; i++) {
          Cs = (RatOrderedMapPolynomial) As.multiply(Bs); 
      }

      logger.info("Timing RatPolynomial multiply ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatPolynomial) A.multiply(B); 
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatPolynomial) RatPolynomial.DIPDIF(AmB,C);
      if ( ! D.isZERO() ) {
	  logger.error("C = " + C.length() ); 
	  logger.error("D = " + D.length() ); 
      }

      logger.info("Timing RatOrderedMapPolynomial multiply ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          Cs = (RatOrderedMapPolynomial) As.multiply(Bs); 
      }
      tr = System.currentTimeMillis() - tr;
      Ds = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPDIF(AmBs,Cs);
      if ( ! Ds.isZERO() ) {
	  logger.error("Cs = " + Cs.length() ); 
	  logger.error("Ds = " + Ds.length() ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)AmB.length();
      fr = (tr/(float)loops)/(float)AmB.length();

      logger.info("Parameters:             " + new Date());
      logger.info("Variables:              " + rl);
      logger.info("Coefficient size:       " + kl);
      logger.info("Number of coefficients: " + ll);
      logger.info("Coefficients in result: " + C.length());
      logger.info("maximal Degrees:        " + el);
      logger.info("Exponent density:       " + q);
      logger.info("Number of loops:        " + loops);

      logger.info("time multiply =         " + tg + " milliseconds" ); 
      logger.info("time multiply2 =        " + tr + " milliseconds" ); 
      logger.info("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr) ); 
      logger.info("time c / loop / size =  " + fg ); 
      logger.info("time r / loop / size =  " + fr ); 
      logger.info("----------");
  }

static void prodaddRatMapOrdered() throws IOException {
      logger.info("Timing multipy/multiplyAdd-RatOrderedMap-Polynomial prodaddRatMapOrdered"); 

      OrderedMapPolynomial A, B, C, D, E, F, ApB, AmB;
      OrderedMapPolynomial As, Bs, Cs, Ds, AmBs;
      int rl = 10; 
      int kl = 10;
      int ll = 10*dauer;
      int el = 5;
      float q = 0.3f;
      int loops = 50; //50;

      A = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("A = " + A ); 
      logger.info("A.size() = " + A.length() ); 

      B = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("B = " + B ); 
      logger.info("B.size() = " + B.length() ); 

      As = new RatOrderedMapPolynomial(A.getMap());
      Bs = new RatOrderedMapPolynomial(B.getMap());

      AmB = (RatOrderedMapPolynomial) A.multiplyA(B);
      if ( logger.isDebugEnabled() ) logger.debug("Prod = " + AmB ); 
      logger.info("Prod.size() = " + AmB.length() ); 
      
      AmBs = (RatOrderedMapPolynomial) As.multiply(Bs);
      if ( logger.isDebugEnabled() ) logger.debug("Prod = " + AmBs ); 
      logger.info("Prod.size() = " + AmBs.length() ); 
      

      Cs = null; C = null;
      long tg, tr;

      logger.info("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          C = (RatOrderedMapPolynomial) A.multiplyA(B); 
      }
      for (int i = 0; i < loops; i++) {
          Cs = (RatOrderedMapPolynomial) As.multiply(Bs); 
      }

      logger.info("Timing RatOrderedMapPolynomial multiply-add ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatOrderedMapPolynomial) A.multiplyA(B); 
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPDIF(AmB,C);
      if ( ! D.isZERO() ) {
	  logger.error("C = " + C.length() ); 
	  logger.error("D = " + D.length() ); 
      }

      logger.info("Timing RatOrderedMapPolynomial multiply ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          Cs = (RatOrderedMapPolynomial) As.multiply(Bs); 
      }
      tr = System.currentTimeMillis() - tr;
      Ds = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPDIF(AmBs,Cs);
      if ( ! Ds.isZERO() ) {
	  logger.error("Cs = " + Cs.length() ); 
	  logger.error("Ds = " + Ds.length() ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)AmB.length();
      fr = (tr/(float)loops)/(float)AmB.length();

      logger.info("Parameters:             " + new Date());
      logger.info("Variables:              " + rl);
      logger.info("Coefficient size:       " + kl);
      logger.info("Number of coefficients: " + ll);
      logger.info("Coefficients in result: " + C.length());
      logger.info("maximal Degrees:        " + el);
      logger.info("Exponent density:       " + q);
      logger.info("Number of loops:        " + loops);

      logger.info("time multiply =         " + tg + " milliseconds" ); 
      logger.info("time multiply2 =        " + tr + " milliseconds" ); 
      logger.info("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr) ); 
      logger.info("time c / loop / size =  " + fg ); 
      logger.info("time r / loop / size =  " + fr ); 
      logger.info("----------");
  }

static void prodObjectMapOrdered() throws IOException {
      logger.info("Timing multipy/multiply-RatMap/RatOrderedMap-Polynomial prodObjectMapOrdered"); 

      OrderedMapPolynomial A, B, C, D, E, F, ApB, AmB;
      MapPolynomial As, Bs, Cs, Ds, AmBs;
      int rl = 10; 
      int kl = 10;
      int ll = 5*dauer;
      int el = 5;
      float q = 0.3f;
      int loops = 50; //50;

      A = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("A = " + A ); 
      logger.info("A.size() = " + A.length() ); 

      B = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("B = " + B ); 
      logger.info("B.size() = " + B.length() ); 

      As = new RatMapPolynomial(A.getMap());
      Bs = new RatMapPolynomial(B.getMap());

      AmB = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPPR(A,B);
      if ( logger.isDebugEnabled() ) logger.debug("Prod = " + AmB ); 
      logger.info("Prod.size() = " + AmB.length() ); 
      
      AmBs = (RatMapPolynomial) RatMapPolynomial.DIPPR(As,Bs);
      if ( logger.isDebugEnabled() ) logger.debug("Prod = " + AmBs ); 
      logger.info("Prod.size() = " + AmBs.length() ); 
      

      Cs = null; C = null;
      long tg, tr;

      logger.info("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          C = (RatOrderedMapPolynomial) A.multiply(B); 
      }
      for (int i = 0; i < loops; i++) {
          Cs = (RatMapPolynomial) As.multiply(Bs); 
      }

      logger.info("Timing RatOrderedMapPolynomial multiply ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatOrderedMapPolynomial) A.multiply(B); 
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPDIF(AmB,C);
      if ( ! D.isZERO() ) {
	  logger.error("C = " + C.length() ); 
	  logger.error("D = " + D.length() ); 
      }

      logger.info("Timing RatMapPolynomial multiply ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          Cs = (RatMapPolynomial) As.multiply(Bs); 
      }
      tr = System.currentTimeMillis() - tr;
      Ds = (RatMapPolynomial) RatMapPolynomial.DIPDIF(AmBs,Cs);
      if ( ! Ds.isZERO() ) {
	  logger.error("Cs = " + Cs.length() ); 
	  logger.error("Ds = " + Ds.length() ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)AmB.length();
      fr = (tr/(float)loops)/(float)AmB.length();

      logger.info("Parameters:             " + new Date());
      logger.info("Variables:              " + rl);
      logger.info("Coefficient size:       " + kl);
      logger.info("Number of coefficients: " + ll);
      logger.info("Coefficients in result: " + C.length());
      logger.info("maximal Degrees:        " + el);
      logger.info("Exponent density:       " + q);
      logger.info("Number of loops:        " + loops);

      logger.info("time multiply =         " + tg + " milliseconds" ); 
      logger.info("time multiply2 =        " + tr + " milliseconds" ); 
      logger.info("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr) ); 
      logger.info("time c / loop / size =  " + fg ); 
      logger.info("time r / loop / size =  " + fr ); 
      logger.info("----------");
  }

static void prodMapEvordOrdered() throws IOException {
      logger.info("Timing multipy(evord)/multiply-RatMap/RatOrderedMap-Polynomial prodMapEvordOrdered"); 

      OrderedMapPolynomial A, B, C, D, E, F, ApB, AmB;
      MapPolynomial As, Bs, Cs, Ds, AmBs;
      int rl = 10; 
      int kl = 10;
      int ll = 5*dauer;
      int el = 5;
      float q = 0.3f;
      int loops = 50; //50;

      int evord = TermOrder.DEFAULT_EVORD;

      A = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("A = " + A ); 
      logger.info("A.size() = " + A.length() ); 

      B = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("B = " + B ); 
      logger.info("B.size() = " + B.length() ); 

      As = new RatMapPolynomial(A.getMap());
      Bs = new RatMapPolynomial(B.getMap());

      AmB = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPPR(A,B);
      if ( logger.isDebugEnabled() ) logger.debug("Prod = " + AmB ); 
      logger.info("Prod.size() = " + AmB.length() ); 
      
      AmBs = (RatMapPolynomial) RatMapPolynomial.DIPPR(As,Bs);
      if ( logger.isDebugEnabled() ) logger.debug("Prod = " + AmBs ); 
      logger.info("Prod.size() = " + AmBs.length() ); 
      

      Cs = null; C = null;
      long tg, tr;

      logger.info("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          C = (RatOrderedMapPolynomial) A.multiply(B); 
      }
      for (int i = 0; i < loops; i++) {
          Cs = (RatMapPolynomial)((RatMapPolynomial) As).multiply(evord,Bs); 
      }

      logger.info("Timing RatOrderedMapPolynomial multiply ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          C = (RatOrderedMapPolynomial) A.multiply(B); 
      }
      tg = System.currentTimeMillis() - tg;
      D = (RatOrderedMapPolynomial) RatOrderedMapPolynomial.DIPDIF(AmB,C);
      if ( ! D.isZERO() ) {
	  logger.error("C = " + C.length() ); 
	  logger.error("D = " + D.length() ); 
      }

      logger.info("Timing RatMapPolynomial multiply(evord) ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          Cs = (RatMapPolynomial)((RatMapPolynomial) As).multiply(evord,Bs); 
      }
      tr = System.currentTimeMillis() - tr;
      Ds = (RatMapPolynomial) RatMapPolynomial.DIPDIF(AmBs,Cs);
      if ( ! Ds.isZERO() ) {
	  logger.error("Cs = " + Cs.length() ); 
	  logger.error("Ds = " + Ds.length() ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)AmB.length();
      fr = (tr/(float)loops)/(float)AmB.length();

      logger.info("Parameters:             " + new Date());
      logger.info("Variables:              " + rl);
      logger.info("Coefficient size:       " + kl);
      logger.info("Number of coefficients: " + ll);
      logger.info("Coefficients in result: " + C.length());
      logger.info("maximal Degrees:        " + el);
      logger.info("Exponent density:       " + q);
      logger.info("Number of loops:        " + loops);

      logger.info("time multiply =         " + tg + " milliseconds" ); 
      logger.info("time multiply2 =        " + tr + " milliseconds" ); 
      logger.info("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr) ); 
      logger.info("time c / loop / size =  " + fg ); 
      logger.info("time r / loop / size =  " + fr ); 
      logger.info("----------");
  }

static void prodMapEvord() throws IOException {
      logger.info("Timing multipy(evord)/multiply-RatMap-Polynomial prodMapEvord"); 

      OrderedMapPolynomial A, B, C, D, E, F, ApB, AmB;
      MapPolynomial As, Ap, Bs, Bp, Cs, Cp, Ds, Dp, AmBs, AmBp;
      int rl = 10; 
      int kl = 10;
      int ll = 5*dauer;
      int el = 5;
      float q = 0.3f;
      int loops = 50; //50;

      int evord = TermOrder.DEFAULT_EVORD;

      A = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("A = " + A ); 
      logger.info("A.size() = " + A.length() ); 

      B = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
      if ( logger.isDebugEnabled() ) logger.debug("B = " + B ); 
      logger.info("B.size() = " + B.length() ); 

      As = new RatMapPolynomial(A.getMap());
      Bs = new RatMapPolynomial(B.getMap());
      Ap = As;
      Bp = Bs;

      AmBp = (RatMapPolynomial) RatMapPolynomial.DIPPR(Ap,Bp);
      if ( logger.isDebugEnabled() ) logger.debug("Prod = " + AmBp ); 
      logger.info("Prod.size() = " + AmBp.length() ); 
      
      AmBs = (RatMapPolynomial) RatMapPolynomial.DIPPR(As,Bs);
      if ( logger.isDebugEnabled() ) logger.debug("Prod = " + AmBs ); 
      logger.info("Prod.size() = " + AmBs.length() ); 
      

      Cs = null; C = null; Cp = null;
      long tg, tr;

      logger.info("Warming up ... " ); 
      for (int i = 0; i < loops; i++) {
          Cp = (RatMapPolynomial) Ap.multiply(Bp); 
      }
      for (int i = 0; i < loops; i++) {
          Cs = (RatMapPolynomial)((RatMapPolynomial) As).multiply(evord,Bs); 
      }

      logger.info("Timing RatMapPolynomial multiply ..." ); 
      tg = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          Cp = (RatMapPolynomial) Ap.multiply(Bp); 
      }
      tg = System.currentTimeMillis() - tg;
      Dp = (RatMapPolynomial) RatMapPolynomial.DIPDIF(AmBp,Cp);
      if ( ! Dp.isZERO() ) {
	  logger.error("Cp = " + Cp.length() ); 
	  logger.error("Dp = " + Dp.length() ); 
      }

      logger.info("Timing RatMapPolynomial multiply(evord) ..." ); 
      tr = System.currentTimeMillis();
      for (int i = 0; i < loops; i++) {
          Cs = (RatMapPolynomial)((RatMapPolynomial) As).multiply(evord,Bs); 
      }
      tr = System.currentTimeMillis() - tr;
      Ds = (RatMapPolynomial) RatMapPolynomial.DIPDIF(AmBs,Cs);
      if ( ! Ds.isZERO() ) {
	  logger.info("Cs = " + Cs.length() ); 
	  logger.info("Ds = " + Ds.length() ); 
      }

      // normalization
      float fg = (float)tg;
      float fr = (float)tr;

      fg = (tg/(float)loops)/(float)AmBp.length();
      fr = (tr/(float)loops)/(float)AmBs.length();

      logger.info("Parameters:             " + new Date());
      logger.info("Variables:              " + rl);
      logger.info("Coefficient size:       " + kl);
      logger.info("Number of coefficients: " + ll);
      logger.info("Coefficients in result: " + Cp.length());
      logger.info("maximal Degrees:        " + el);
      logger.info("Exponent density:       " + q);
      logger.info("Number of loops:        " + loops);

      logger.info("time multiply =         " + tg + " milliseconds" ); 
      logger.info("time multiply2 =        " + tr + " milliseconds" ); 
      logger.info("time derivation tg-tr = " + (float)(tg-tr)/(float)(tg+tr) ); 
      logger.info("time c / loop / size =  " + fg ); 
      logger.info("time r / loop / size =  " + fr ); 
      logger.info("----------");
  }


public static void main( String[] args ) throws IOException {
      logger.info("new PolynomialTimings ------------------------------"); 
      sumRatGeneric();
      prodRatGeneric();
      prodTreeHash();
      prodMapOrdered();
      prodRatMapOrdered();
      prodaddRatMapOrdered();
      prodObjectMapOrdered();
      prodMapEvordOrdered();
      prodMapEvord();
  }
}
