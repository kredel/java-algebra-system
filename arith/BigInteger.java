/*
 * $Id$
 */

package edu.jas.arith;

import java.util.Random;

public class BigInteger implements Coefficient {

    private final static Random random = new Random();

    public final static BigInteger ZERO = new BigInteger( java.math.BigInteger.ZERO );
    public final static BigInteger ONE = new BigInteger( java.math.BigInteger.ONE );

    private final java.math.BigInteger val;

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

    public java.math.BigInteger getval() {
      return val;
    }

    public /*static*/ Coefficient fromInteger(java.math.BigInteger a) {
	return new BigInteger(a);
    }

    public static BigInteger valueOf(java.math.BigInteger a) {
	return new BigInteger(a);
    }

    public /*static*/ Coefficient fromInteger(long a) {
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

    public String toString() {
	return val.toString();
    }

    public int compareTo(Object b) {
        if ( ! (b instanceof BigInteger) ) {
	   System.err.println("BigInteger.compareTo not a BigInteger");
           return Integer.MAX_VALUE;
        }
	return val.compareTo( ((BigInteger)b).getval() );
    }

    public boolean equals(Object b) {
	if ( ! ( b instanceof BigInteger ) ) return false;
	return val.equals( ((BigInteger)b).getval() );
    }

    public Coefficient abs() {
      return new BigInteger( val.abs() );
    }

    public Coefficient negate() {
      return new BigInteger( val.negate() );
    }

    public int signum() {
      return val.signum();
    }

    public Coefficient subtract(Coefficient S) {
      return new BigInteger( val.subtract( ((BigInteger)S).getval() ) );
    }

    public Coefficient divide(Coefficient S) {
      return new BigInteger( val.divide( ((BigInteger)S).getval() ) );
    }

    public Coefficient random(int n) {
      return new BigInteger( new java.math.BigInteger( n, random ) );
  }

    public Coefficient multiply(Coefficient S) {
      return new BigInteger( val.multiply( ((BigInteger)S).getval() ) );
    }

    public Coefficient add(Coefficient S) {
      return new BigInteger( val.add( ((BigInteger)S).getval() ) );
    }


}
