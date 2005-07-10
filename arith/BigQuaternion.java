/*
 * $Id$
 */

package edu.jas.arith;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.Random;
import java.io.Reader;

/**
 * BigQuaternion class based on BigRational implementing the Coefficient 
 * interface and with the familiar SAC method names.
 * @author Heinz Kredel
 */

public class BigQuaternion implements RingElem<BigQuaternion>, 
                                      RingFactory<BigQuaternion> {

    /* the data structure */

    private final BigRational re;  // real part
    private final BigRational im;  // i imaginary part
    private final BigRational jm;  // j imaginary part
    private final BigRational km;  // k imaginary part

    private final static Random random = new Random();

    private static Logger logger = Logger.getLogger(BigQuaternion.class);

    /** The constructors create a BigQuaternion object 
     * from two BigRational objects real and imaginary parts. 
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


    /** The BigQuaternion string constructor accepts the
     * following formats:
     * empty string, "rational", or "rat i rat j rat k rat"
     * with no blanks around i, j or k if used as polynoial coefficient.
     */

    public BigQuaternion(String s) throws NumberFormatException {
        if ( s == null || s.length() == 0) {
           re = BigRational.ZERO;
           im = BigRational.ZERO;
           jm = BigRational.ZERO;
           km = BigRational.ZERO;
           return;
        } 
        s = s.trim();
	int r = s.indexOf("i") + s.indexOf("j") + s.indexOf("k");
	if ( r == -3 ) {
           re = new BigRational(s);
           im = BigRational.ZERO;
           jm = BigRational.ZERO;
           km = BigRational.ZERO;
           return;
	}

        int i = s.indexOf("i");
        String sr = "";
        if ( i > 0 ) {
           sr = s.substring(0,i);
        } else if ( i < 0 ) {
           throw new NumberFormatException("BigQuaternion missing i");
        }
        String si = "";
        if ( i < s.length() ) {
           s = s.substring(i+1,s.length());
        }
        int j = s.indexOf("j");
        if ( j > 0 ) {
           si = s.substring(0,j);
        } else if ( j < 0 ) {
           throw new NumberFormatException("BigQuaternion missing j");
        }
        String sj = "";
        if ( j < s.length() ) {
           s = s.substring(j+1,s.length());
        }
        int k = s.indexOf("k");
        if ( k > 0 ) {
           sj = s.substring(0,k);
        } else if ( k < 0 ) {
           throw new NumberFormatException("BigQuaternion missing k");
        }
        String sk = "";
        if ( k < s.length() ) {
           s = s.substring(k+1,s.length());
        }
	sk = s;

        re = new BigRational( sr.trim() );
        im = new BigRational( si.trim() );
        jm = new BigRational( sj.trim() );
        km = new BigRational( sk.trim() );
    }

    public BigQuaternion clone() {
        return new BigQuaternion( re, im, jm, km );
    }

    public BigQuaternion copy(BigQuaternion c) {
        return new BigQuaternion( c.re, c.im, c.jm, c.km );
    }

    public BigQuaternion getZERO() {
        return ZERO;
    }

    public BigQuaternion getONE() {
        return ONE;
    }

    public BigQuaternion fromInteger(BigInteger a) {
        return new BigQuaternion( new BigRational(a) );
    }

    public BigQuaternion fromInteger(long a) {
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
     * compatible with string constructor
     */

    public String toString() {
        String s = "" + re;
        int i = im.compareTo( BigRational.ZERO );
        int j = jm.compareTo( BigRational.ZERO );
        int k = km.compareTo( BigRational.ZERO );
        logger.debug("compareTo "+im+" ? 0 = "+i);
        logger.debug("compareTo "+jm+" ? 0 = "+j);
        logger.debug("compareTo "+km+" ? 0 = "+k);
        if ( i == 0 && j == 0 && k == 0 ) return s;
        s += "i" + im;
        s += "j" + jm;
        s += "k" + km;
        return s;
    }

   /**Quaternion number zero.  A is a quaternion number.  If A is 0 then
    true is returned, else false. */

    public static boolean isQZERO(BigQuaternion A) {
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

    public static boolean isQONE(BigQuaternion A) {
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

    public boolean isUnit() {
	return ( ! isZERO() );
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

    public int compareTo(BigQuaternion b) {
      //if ( ! (b instanceof BigQuaternion) ) return -1;
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

    public BigQuaternion add(BigQuaternion B) {
        return new BigQuaternion( re.add(B.getRe()), 
                                  im.add(B.getIm()), 
                                  jm.add(B.getJm()), 
                                  km.add(B.getKm()));
    }

    /**Quaternion number sum.  A and B are quaternion numbers.  
      @return T=A+B. */

    public static BigQuaternion QSUM(BigQuaternion A, BigQuaternion B) {
      if ( A == null ) return null;
      return A.add(B);
    }

    /**Quaternion number difference.  A and B are quaternion numbers.  
       @return T=A-B. */

    public static BigQuaternion QDIF(BigQuaternion A, BigQuaternion B) {
      if ( A == null ) return null;
      return A.subtract(B);
    }

    public BigQuaternion subtract(BigQuaternion B) {
        return new BigQuaternion( re.subtract(B.getRe()), 
                                  im.subtract(B.getIm()),
                                  jm.subtract(B.getJm()),
                                  km.subtract(B.getKm()) );
    }

    /**Quaternion number negative.  A is a quaternion number.  
      @return S=-A. */

    public static BigQuaternion QNEG(BigQuaternion A) {
      if ( A == null ) return null;
      return A.negate();
    }

    public BigQuaternion negate() {
        return new BigQuaternion( (BigRational)re.negate(), 
                                  (BigRational)im.negate(),
                                  (BigRational)jm.negate(),
                                  (BigRational)km.negate() );
    }


    /**Quaternion number conjugate.  A is a quaternion number. S is the
    quaternion conjugate of A. */

    public static BigQuaternion QCON(BigQuaternion A) {
      if ( A == null ) return null;
      return A.conjugate();
    }

    /** arithmetic operations: conjugate, absolut value 
     */

    public BigQuaternion conjugate() {
        return new BigQuaternion( re, 
                                  (BigRational)im.negate(), 
                                  (BigRational)jm.negate(),
                                  (BigRational)km.negate() );
    }

    public BigQuaternion abs() {
        BigRational v = re.multiply(re);
        v = v.add(im.multiply(im));
        v = v.add(jm.multiply(jm));
        v = v.add(km.multiply(km));
        logger.error("abs() square root missing");
        // v = v.sqrt();
        return new BigQuaternion( v );
    }

    /**Quaternion number absolute value.  R is a quaternion number.  S is the
      absolute value of R, a rational number. 
     */

    public static BigRational QABS(BigQuaternion A) {
      if ( A == null ) return null;
      return A.abs().getRe();
    }

    /**Quaternion number product.  A and B are quaternion numbers.  
      @return T=A*B. */

    public static BigQuaternion QPROD(BigQuaternion A, BigQuaternion B) {
      if ( A == null ) return null;
      return A.multiply(B);
    }

    /** arithmetic operations: *, inverse, / 
     */

    public BigQuaternion multiply(BigQuaternion B) {
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
      return A.inverse();
    }

    public BigQuaternion inverse() {
        BigRational a = re.multiply(re);
        a = a.add(im.multiply(im));
        a = a.add(jm.multiply(jm));
        a = a.add(km.multiply(km));
        return new BigQuaternion(             re.divide(a), 
                                 (BigRational)im.divide(a).negate(), 
				 (BigRational)jm.divide(a).negate(), 
                                 (BigRational)km.divide(a).negate() ); 
    }


  public BigQuaternion remainder(BigQuaternion S) {
      if ( S.isZERO() ) {
          throw new RuntimeException("division by zero");
      }
      return ZERO;
  }

    /**Quaternion number quotient.  A and B are quaternion numbers, 
      B non-zero.
      @return T=R/S. */

    public static BigQuaternion QQ(BigQuaternion A, BigQuaternion B) {
      if ( A == null ) return null;
      return A.divide(B);
    }

    public BigQuaternion divide (BigQuaternion b) {
	return this.multiply( b.inverse() );
    }

    /**Quaternion number, random.  n is a positive beta-integer.  Random 
    rational numbers A and B are generated using RNRAND(n). Then 
    R is the quaternion number with real part A and imaginary part B. */

    public static BigQuaternion QRAND(int n) {
      return new BigQuaternion( BigRational.RNRAND(n), 
                                BigRational.RNRAND(n), 
                                BigRational.RNRAND(n),
                                BigRational.RNRAND(n) );
    }

    /** random quaternion number 
     */

    public BigQuaternion random(int n) {
        return QRAND(n);
    }

    public BigQuaternion parse(String s) {
        return new BigQuaternion(s);
    }

    public BigQuaternion parse(Reader r) {
        return ZERO;
    }

}
