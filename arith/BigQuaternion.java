/*
 * $Id$
 */

package edu.jas.arith;

import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.Random;

/**
 * BigQuaternion class based on BigRational implementing the Coefficient 
 * interface and with the familiar SAC method names.
 * @author Heinz Kredel
 */

public class BigQuaternion implements Coefficient, Comparable {

    /* the data structure */

    private final BigRational re;  // der Realteil
    private final BigRational im;  // der i Imaginärteil
    private final BigRational jm;  // der j Imaginärteil
    private final BigRational km;  // der k Imaginärteil

    private final static Random random = new Random();

    private static Logger logger = Logger.getLogger(BigQuaternion.class);

    /** The constructors create a BigQuaternion object 
     * from two BigRational objects real and imaginary part. 
     */

    public BigQuaternion(BigRational r, BigRational i, BigRational j, BigRational k) {
        re = r;
        im = i;
        jm = j;
        km = k;
    }

    public BigQuaternion(BigRational r, BigRational i, BigRational j) {
        this(r,i,j,BigRational.ZERO);
    }

    public BigQuaternion(BigRational r, BigRational i) {
        this(r,i,BigRational.ZERO);
    }

    public BigQuaternion(BigRational r) {
        this(r,BigRational.ZERO);
    }

    public BigQuaternion(int r) {
        this(new BigRational(r),BigRational.ZERO);
    }

    public BigQuaternion() {
        this(BigRational.ZERO);
    }

    public BigQuaternion(String s) throws NumberFormatException {
        if ( s == null || s.length() == 0) {
            re = BigRational.ZERO;
            im = BigRational.ZERO;
            jm = null;
            km = null;
            return;
        } 
        s = s.trim();
        int i = s.indexOf("i");
        if ( i < 0 ) {
           re = new BigRational( s );
           im = BigRational.ZERO;
           jm = null;
           km = null;
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
        jm = null;
        km = null;
    }

    public /*static*/ Coefficient fromInteger(BigInteger a) {
        return new BigQuaternion( new BigRational(a) );
    }

    public /*static*/ Coefficient fromInteger(long a) {
        return new BigQuaternion( new BigRational( a ) );
    }

    /** constants: 1, 0 und i 
     */

    public static final BigQuaternion ZERO = 
           new BigQuaternion();

    public static final BigQuaternion ONE = 
           new BigQuaternion(BigRational.ONE);

    public static final BigQuaternion I = 
           new BigQuaternion(BigRational.ZERO, BigRational.ONE);

    public static final BigQuaternion J = 
           new BigQuaternion(BigRational.ZERO, 
                             BigRational.ZERO,
                             BigRational.ONE);

    public static final BigQuaternion K = 
           new BigQuaternion(BigRational.ZERO,
                             BigRational.ZERO,
                             BigRational.ZERO,
                             BigRational.ONE);


    /** get the real and imaginary part 
     */

    public BigRational getRe() { return re; }

    public BigRational getIm() { return im; }

    public BigRational getJm() { return jm; }

    public BigRational getKm() { return km; }


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

   /**Quaternion number zero.  A is a quaternion number.  If A is 0 then
    true is returned, else false. */

    public static boolean isCZERO(BigQuaternion A) {
      if ( A == null ) return false;
      return A.isZERO();
    }

    public boolean isZERO() {
        return    re.equals( BigRational.ZERO )
               && im.equals( BigRational.ZERO )
               && jm.equals( BigRational.ZERO )
               && km.equals( BigRational.ZERO );
    }

    /**Quaternion number one.  A is a quaternion number.  If A is 1 then
    true is returned, else false. */

    public static boolean isCONE(BigQuaternion A) {
      if ( A == null ) return false;
      return A.isONE();
    }

    public boolean isONE() {
        return    re.equals( BigRational.ONE )
               && im.equals( BigRational.ZERO )
               && jm.equals( BigRational.ZERO )
               && km.equals( BigRational.ZERO );
    }

    public boolean isIMAG() {
        return    re.equals( BigRational.ZERO )
               && im.equals( BigRational.ONE )
               && jm.equals( BigRational.ZERO )
               && km.equals( BigRational.ZERO );
    }


    /** comparison of two BigQuaternion numbers
     */

    public boolean equals(Object b) {
        if ( ! ( b instanceof BigQuaternion ) ) return false;
        BigQuaternion B = (BigQuaternion) b;
        return    re.equals( B.getRe() ) 
               && im.equals( B.getIm() )
               && jm.equals( B.getJm() )
               && km.equals( B.getKm() );
    }

    /** since quaternion numbers are unordered, there is 
     * no compareTo method. 
     * We define the result to be 
     * @return -1 if b is not a BigQuaternion object
     * @return 0 if b is equal to this
     * @return 1 else
     */

    public int compareTo(Object b) {
      if ( ! (b instanceof BigQuaternion) ) return -1;
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
        if ( ! ( b instanceof BigQuaternion ) ) return this;
        BigQuaternion B = (BigQuaternion) b;
        return new BigQuaternion( re.add(B.getRe()), 
                                  im.add(B.getIm()), 
                                  jm.add(B.getJm()), 
                                  km.add(B.getKm()));
    }

    /**Quaternion number sum.  A and B are quaternion numbers.  
      @return T=A+B. */

    public static BigQuaternion QSUM(BigQuaternion A, BigQuaternion B) {
      if ( A == null ) return null;
      return (BigQuaternion) A.add(B);
    }

    /**Quaternion number difference.  A and B are quaternion numbers.  
       @return T=A-B. */

    public static BigQuaternion QDIF(BigQuaternion A, BigQuaternion B) {
      if ( A == null ) return null;
      return (BigQuaternion) A.subtract(B);
    }

    public Coefficient subtract(Coefficient b) {
        if ( ! ( b instanceof BigQuaternion ) ) return this;
        BigQuaternion B = (BigQuaternion) b;
        return new BigQuaternion( re.subtract(B.getRe()), 
                                  im.subtract(B.getIm()),
                                  jm.subtract(B.getIm()),
                                  km.subtract(B.getKm()) );
    }

    /**Quaternion number negative.  A is a quaternion number.  
      @return S=-A. */

    public static BigQuaternion QNEG(BigQuaternion A) {
      if ( A == null ) return null;
      return (BigQuaternion) A.negate();
    }

    public Coefficient negate() {
        return new BigQuaternion( (BigRational)re.negate(), 
                                  (BigRational)im.negate(),
                                  (BigRational)jm.negate(),
                                  (BigRational)km.negate() );
    }


    /**Quaternion number conjugate.  A is a quaternion number. S is the
    quaternion conjugate of A. */

    public static BigQuaternion QCON(BigQuaternion A) {
      if ( A == null ) return null;
      return (BigQuaternion) A.conjugate();
    }

    /** arithmetic operations: conjugate, absolut value 
     */

    public BigQuaternion conjugate() {
        return new BigQuaternion( re, 
                                  (BigRational)im.negate(), 
                                  (BigRational)jm.negate(),
                                  (BigRational)km.negate() );
    }

    public Coefficient abs() {
        BigRational v = re.multiply(re);
        v = v.add(im.multiply(im));
        v = v.add(jm.multiply(jm));
        v = v.add(km.multiply(km));
        logger.error("abs() square root missing");
        // v = v.sqrt();
        return ( v );
    }

    /**Quaternion number absolute value.  R is a quaternion number.  S is the
      absolute value of R, a rational number. 
     */

    public static BigRational QABS(BigQuaternion A) {
      if ( A == null ) return null;
      return (BigRational) A.abs();
    }

    /**Quaternion number product.  A and B are quaternion numbers.  
      @return T=A*B. */

    public static BigQuaternion QPROD(BigQuaternion A, BigQuaternion B) {
      if ( A == null ) return null;
      return (BigQuaternion) A.multiply(B);
    }

    /** arithmetic operations: *, inverse, / 
     */

    public Coefficient multiply(Coefficient b) {
        if ( ! ( b instanceof BigQuaternion ) ) return this;
        BigQuaternion B = (BigQuaternion) b;
	BigRational r = re.multiply(B.getRe());
	r = r.subtract(im.multiply(B.getIm()));
	r = r.subtract(jm.multiply(B.getJm()));
	r = r.subtract(km.multiply(B.getKm()));
	BigRational i = re.multiply(B.getIm());
	i = i.add( im.multiply(B.getRe()) );
	i = i.add( jm.multiply(B.getKm()) );
	i = i.subtract( km.multiply(B.getJm()) );

	BigRational j = re.multiply(B.getJm());
	j = j.subtract( im.multiply(B.getKm()) );
	j = j.add( jm.multiply(B.getRe()) );
	j = j.add( km.multiply(B.getIm()) );

	BigRational k = re.multiply(B.getKm());
	k = k.add( im.multiply(B.getJm()) );
	k = k.subtract( jm.multiply(B.getIm()) );
	k = k.add( km.multiply(B.getRe()) );

        return new BigQuaternion( r, i, j, k );
    }

    /**Quaternion number inverse.  A is a non-zero quaternion number.  
       S A=1. */

    public static BigQuaternion QINV(BigQuaternion A) {
      if ( A == null ) return null;
      return (BigQuaternion) A.inverse();
    }

    public Coefficient inverse() {
        BigRational a = re.multiply(re);
        a = a.add(im.multiply(im));
        a = a.add(jm.multiply(jm));
        a = a.add(km.multiply(km));
        return new BigQuaternion(             re.divide(a), 
                                 (BigRational)im.divide(a).negate(), 
				 (BigRational)jm.divide(a).negate(), 
                                 (BigRational)km.divide(a).negate() ); 
    }


    /**Quaternion number quotient.  A and B are quaternion numbers, 
      B non-zero.
      @return T=R/S. */

    public static BigQuaternion QQ(BigQuaternion A, BigQuaternion B) {
      if ( A == null ) return null;
      return (BigQuaternion) A.divide(B);
    }

    public Coefficient divide (Coefficient b) {
        if ( ! ( b instanceof BigQuaternion ) ) return this;
        BigQuaternion B = (BigQuaternion) b;
        return this.multiply( B.inverse() );
    }


    /**Quaternion number, random.  n is a positive beta-integer.  Random 
    rational numbers A and B are generated using RNRAND(n). Then 
    R is the quaternion number with real part A and imaginary part B. */

    public static BigQuaternion CRAND(int n) {
      return new BigQuaternion( new BigRational(n), 
                                new BigRational(n), 
                                new BigRational(n),
                                new BigRational(n) );
    }

    /** random quaternion number 
     */

    public Coefficient random(int n) {
        return CRAND(n);
    }

}
