/*
 * $Id$
 */

package edu.jas.arith;

import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.Random;

/**
 * BigComplex class based on BigRational implementing the Coefficient 
 * interface and with the familiar SAC method names.
 * @author Heinz Kredel
 */

public class BigComplex implements Coefficient, Comparable {

    /* the data structure */

    private final BigRational re;  // der Realteil
    private final BigRational im;  // der Imaginärteil

    private final static Random random = new Random();

    private static Logger logger = Logger.getLogger(BigComplex.class);

    /** The constructors create a BigComplex object 
     * from two BigRational objects real and imaginary part. 
     */

    public BigComplex(BigRational r, BigRational i) {
        re = r;
        im = i;
    }

    public BigComplex(BigRational r) {
        this(r,BigRational.ZERO);
    }

    public BigComplex(int r) {
        this(new BigRational(r),BigRational.ZERO);
    }

    public BigComplex() {
        this(BigRational.ZERO);
    }

    public BigComplex(String s) throws NumberFormatException {
        if ( s == null || s.length() == 0) {
	    re = BigRational.ZERO;
            im = BigRational.ZERO;
            return;
	} 
	s = s.trim();
	int i = s.indexOf("i");
	if ( i < 0 ) {
           re = new BigRational( s );
           im = BigRational.ZERO;
	   return;
	}
	logger.warn("String constructor not done");
	String sr = "";
	if ( i > 0 ) {
           sr = s.substring(0,i);
	}
	String si = "";
	if ( i < s.length() ) {
           si = s.substring(i+1,s.length());
	}
	int j = sr.indexOf("+");
        re = new BigRational( sr.trim() );
        im = new BigRational( si.trim() );
    }

    public /*static*/ Coefficient fromInteger(BigInteger a) {
	return new BigComplex( new BigRational(a) );
    }

    public /*static*/ Coefficient fromInteger(long a) {
	return new BigComplex( new BigRational( a ) );
    }

    /** constants: 1, 0 und i 
     */

    public static final BigComplex ZERO = 
           new BigComplex();

    public static final BigComplex ONE = 
           new BigComplex(BigRational.ONE);

    public static final BigComplex I = 
           new BigComplex(BigRational.ZERO,BigRational.ONE);


    /** get the real and imaginary part 
     */

    public BigRational getRe() { return re; }

    public BigRational getIm() { return im; }


    /** string representation
     */

    public String toString() {
        String s = "" + re;
	int i = im.compareTo( BigRational.ZERO );
	logger.info("compareTo "+im+" ? 0 = "+i);
	if ( i == 0 ) return s;
	s += " i " + im;
	/*
        if ( i == 1 ) {
           s += " - " + (im.negate()) + "i";
        } else if ( i == -1 ) {
           s += " + " + im + "i";
	} else {
	    // do nothing
	}
	*/
        return s;
    }

   /**Complex number zero.  A is a complex number.  If A is 0 then
    true is returned, else false. */

    public static boolean isCZERO(BigComplex A) {
      if ( A == null ) return false;
      return A.isZERO();
    }

    public boolean isZERO() {
	return    re.equals( BigRational.ZERO )
               && im.equals( BigRational.ZERO );
    }

    /**Complex number one.  A is a complex number.  If A is 1 then
    true is returned, else false. */

    public static boolean isCONE(BigComplex A) {
      if ( A == null ) return false;
      return A.isONE();
    }

    public boolean isONE() {
	return    re.equals( BigRational.ONE )
               && im.equals( BigRational.ZERO );
    }

    public boolean isIMAG() {
	return    re.equals( BigRational.ZERO )
               && im.equals( BigRational.ONE );
    }


    /** comparison of two BigComplex numbers
     */

    public boolean equals(Object b) {
	if ( ! ( b instanceof BigComplex ) ) return false;
	BigComplex B = (BigComplex) b;
	return    re.equals( B.getRe() ) 
               && im.equals( B.getIm() );
    }

    /** since complex numbers are unordered, there is 
     * no compareTo method. 
     * We define the result to be 
     * @return -1 if b is not a BigComplex object
     * @return 0 if b is equal to this
     * @return 1 else
     */

    public int compareTo(Object b) {
      if ( ! (b instanceof BigComplex) ) return -1;
      if ( equals(b) ) { 
	  return 0;
      } else {
	  return 1;
      }
    }

    public int signum() {
      if ( this.equals(ZERO) ) {
         return 0;
      } else {
         return 1;
      }
    }

    /** arithmetic operations: +, -, -
     */

    public Coefficient add(Coefficient b) {
	if ( ! ( b instanceof BigComplex ) ) return this;
	BigComplex B = (BigComplex) b;
	return new BigComplex( re.add(B.getRe()), 
                               im.add(B.getIm()) );
    }

    /**Complex number sum.  A and B are complex numbers.  
      @return T=A+B. */

    public static BigComplex CSUM(BigComplex A, BigComplex B) {
      if ( A == null ) return null;
      return (BigComplex) A.add(B);
    }

    /**Complex number difference.  A and B are complex numbers.  
       @return T=A-B. */

    public static BigComplex CDIF(BigComplex A, BigComplex B) {
      if ( A == null ) return null;
      return (BigComplex) A.subtract(B);
    }

    public Coefficient subtract(Coefficient b) {
	if ( ! ( b instanceof BigComplex ) ) return this;
	BigComplex B = (BigComplex) b;
	return new BigComplex(re.subtract(B.getRe()), 
                              im.subtract(B.getIm()));
    }

    /**Complex number negative.  A is a complex number.  
      @return S=-A. */

    public static BigComplex CNEG(BigComplex A) {
      if ( A == null ) return null;
      return (BigComplex) A.negate();
    }

    public Coefficient negate() {
	return new BigComplex( (BigRational)re.negate(), 
                               (BigRational)im.negate());
    }


    /**Complex number conjugate.  A is a complex number. S is the
    complex conjugate of A. */

    public static BigComplex CCON(BigComplex A) {
      if ( A == null ) return null;
      return (BigComplex) A.conjugate();
    }

    /** arithmetic operations: conjugate, absolut value 
     */

    public BigComplex conjugate() {
	return new BigComplex(re, (BigRational)im.negate());
    }

    public Coefficient abs() {
	BigRational v = re.multiply(re).add(im.multiply(im));
	logger.error("abs() square root missing");
	// v = v.sqrt();
	return ( v );
    }

    /**Complex number absolute value.  R is a complex number.  S is the
      absolute value of R, a rational number. 
     */

    public static BigRational CABS(BigComplex A) {
      if ( A == null ) return null;
      return (BigRational) A.abs();
    }

    /**Complex number product.  A and B are complex numbers.  
      @return T=A*B. */

    public static BigComplex CPROD(BigComplex A, BigComplex B) {
      if ( A == null ) return null;
      return (BigComplex) A.multiply(B);
    }

    /** arithmetic operations: *, inverse, / 
     */

    public Coefficient multiply(Coefficient b) {
	if ( ! ( b instanceof BigComplex ) ) return this;
	BigComplex B = (BigComplex) b;
	return new BigComplex(
               re.multiply(B.getRe()).subtract(im.multiply(B.getIm())),
               re.multiply(B.getIm()).add(im.multiply(B.getRe())) );
    }

    /**Complex number inverse.  A is a non-zero complex number.  
       S A=1. */

    public static BigComplex CINV(BigComplex A) {
      if ( A == null ) return null;
      return (BigComplex) A.inverse();
    }

    public Coefficient inverse() {
        BigRational a = re.multiply(re).add(im.multiply(im));
	return new BigComplex(             re.divide(a), 
                              (BigRational)im.divide(a).negate() ); 
    }


    /**Complex number quotient.  A and B are complex numbers, 
      B non-zero.
      @return T=R/S. */

    public static BigComplex CQ(BigComplex A, BigComplex B) {
      if ( A == null ) return null;
      return (BigComplex) A.divide(B);
    }

    public Coefficient divide (Coefficient b) {
	if ( ! ( b instanceof BigComplex ) ) return this;
	BigComplex B = (BigComplex) b;
	return this.multiply( B.inverse() );
    }


    /**Complex number, random.  n is a positive beta-integer.  Random 
    rational numbers A and B are generated using RNRAND(n). Then 
    R is the complex number with real part A and imaginary part B. */

    public static BigComplex CRAND(int n) {
      return new BigComplex( new BigRational(n), 
                             new BigRational(n) );
    }

    /** random complex number 
     */

    public Coefficient random(int n) {
	return CRAND(n);
    }

}
