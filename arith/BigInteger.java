/*
 * $Id$
 */

package edu.jas.arith;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

import java.util.Random;
import java.io.Reader;

/**
 * BigInteger class to make java.math.BigInteger available with RingElem 
 * interface and with the familiar SAC static method names.
 * @author Heinz Kredel
 * @see java.math.BigInteger
 */

public class BigInteger implements RingElem<BigInteger>, 
                                   RingFactory<BigInteger> {

    private final static Random random = new Random();

    public final static BigInteger ZERO = new BigInteger( java.math.BigInteger.ZERO );
    public final static BigInteger ONE = new BigInteger( java.math.BigInteger.ONE );

    protected final java.math.BigInteger val;

    /**
     * Constructors for BigInteger
     */

    public BigInteger(java.math.BigInteger a) {
	val = a;
    }

    public BigInteger(long a) {
	val = new java.math.BigInteger( String.valueOf(a) );
    }

    public BigInteger(String s) {
	val = new java.math.BigInteger( s );
    }

    public BigInteger() {
	val = java.math.BigInteger.ZERO;
    }

    public java.math.BigInteger getVal() {
      return val;
    }

    public BigInteger clone() {
        return new BigInteger( val );
    }

    public BigInteger copy(BigInteger c) {
        return new BigInteger( c.val );
    }

    public BigInteger getZERO() {
        return ZERO;
    }

    public BigInteger getONE() {
        return ONE;
    }

    public BigInteger fromInteger(java.math.BigInteger a) {
	return new BigInteger(a);
    }

    public static BigInteger valueOf(java.math.BigInteger a) {
	return new BigInteger(a);
    }

    public BigInteger fromInteger(long a) {
	return new BigInteger(a);
    }

    public static BigInteger valueOf(long a) {
	return new BigInteger(a);
    }

    public boolean isZERO() {
	return val.equals( java.math.BigInteger.ZERO );
    }

    public boolean isONE() {
	return val.equals( java.math.BigInteger.ONE );
    }

    public boolean isUnit() {
	return ( this.isONE() || this.negate().isONE() );
    }

    public String toString() {
	return val.toString();
    }


    public int compareTo(BigInteger b) {
	return val.compareTo( b.val );
    }

    public static int ICOMP(BigInteger A, BigInteger B) {
      if ( A == null ) return -B.signum();
      return A.compareTo(B);
    }

    public boolean equals(Object b) {
	if ( ! ( b instanceof BigInteger ) ) return false;
	return val.equals( ((BigInteger)b).getVal() );
    }


    public BigInteger abs() {
      return new BigInteger( val.abs() );
    }

    public static BigInteger IABS(BigInteger A) {
      if ( A == null ) return null;
      return A.abs();
    }


    public BigInteger negate() {
      return new BigInteger( val.negate() );
    }

    public static BigInteger INEG(BigInteger A) {
      if ( A == null ) return null;
      return A.negate();
    }


    public int signum() {
      return val.signum();
    }

    public static int ISIGN(BigInteger A) {
      if ( A == null ) return 0;
      return A.signum();
    }


    public BigInteger subtract(BigInteger S) {
      return new BigInteger( val.subtract( S.val ) );
    }

    public static BigInteger IDIF(BigInteger A, BigInteger B) {
      if ( A == null ) return B.negate();
      return A.subtract(B);
    }


    public BigInteger divide(BigInteger S) {
      return new BigInteger( val.divide( S.val ) );
    }

    public static BigInteger IQ(BigInteger A, BigInteger B) {
      if ( A == null ) return null;
      return A.divide(B);
    }


    /** Integer inverse.  R is a non-zero integer.  
        S=1/R if defined else 0. */

    public BigInteger inverse() {
	if ( this.isONE() || this.negate().isONE() ) {
           return this;
	}
	return ZERO;
    }


    public BigInteger remainder(BigInteger S) {
      return new BigInteger( val.remainder( S.val ) );
    }

    public static BigInteger IREM(BigInteger A, BigInteger B) {
      if ( A == null ) return null;
      return A.remainder(B);
    }

    public BigInteger[] divideAndRemainder(BigInteger S) {
      BigInteger[] qr = new BigInteger[2];
      java.math.BigInteger[] C = val.divideAndRemainder( S.val );
      qr[0] = new BigInteger( C[0] );
      qr[1] = new BigInteger( C[1] );
      return qr;
    }

    /**
    Integer quotient and remainder.  A and B are integers, B ne 0.  Q is
    the quotient, integral part of A/B, and R is the remainder A-B*Q.
    */

    public static BigInteger[] IQR(BigInteger A, BigInteger B) {
      if ( A == null ) return null;
      return A.divideAndRemainder(B);
    }


    public BigInteger gcd(BigInteger S) {
      return new BigInteger( val.gcd( S.val ) );
    }

    public static BigInteger IGCD(BigInteger A, BigInteger B) {
      if ( A == null ) return null;
      return A.gcd(B);
    }


    public BigInteger random(int n) {
      return IRAND( n );
    }

    public static BigInteger IRAND(int NL) {
      return new BigInteger( new java.math.BigInteger( NL, random ) );
    }


    public BigInteger multiply(BigInteger S) {
      return new BigInteger( val.multiply( S.val ) );
    }

    public static BigInteger IPROD(BigInteger A, BigInteger B) {
      if ( A == null ) return null;
      return A.multiply(B);
    }


    public BigInteger add(BigInteger S) {
      return new BigInteger( val.add( S.val ) );
    }

    public static BigInteger ISUM(BigInteger A, BigInteger B) {
      if ( A == null ) return null;
      return A.add(B);
    }


    public BigInteger parse(String s) {
        return new BigInteger(s);
    }

    public BigInteger parse(Reader r) {
        return ZERO;
    }

}
