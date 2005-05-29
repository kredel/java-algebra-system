/*
 * $Id$
 */

package edu.jas.arith;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

import java.util.Random;
import java.io.Reader;

/**
 * ModInteger class with RingElem interface
 * and with the familiar SAC method names.
 * @author Heinz Kredel
 * @see java.math.BigInteger
 */

public class ModInteger implements RingElem<ModInteger>, 
                                   RingFactory<ModInteger> {

    protected final java.math.BigInteger modul;
    protected final java.math.BigInteger val;

    private final static Random random = new Random();

    /**
     * Constructors for ModInteger
     */

    public ModInteger(java.math.BigInteger m, java.math.BigInteger a) {
        modul = m;
	val = a.mod(modul);
    }

    public ModInteger(long m, long a) {
        this( 
	     new java.math.BigInteger( String.valueOf(m) ),
	     new java.math.BigInteger( String.valueOf(a) )
             );
    }

    public ModInteger(String m, String s) {
        this( 
	     new java.math.BigInteger( m ),
	     new java.math.BigInteger( s )
             );
    }

    public ModInteger(java.math.BigInteger m, String s) {
        this( m, new java.math.BigInteger( s ) );
    }

    public ModInteger(java.math.BigInteger m, long s) {
        this( m, new java.math.BigInteger( String.valueOf(s) ) );
    }

    public ModInteger(java.math.BigInteger m) {
        modul = m; // assert m != 0
	val = java.math.BigInteger.ZERO;
    }

    public java.math.BigInteger getVal() {
      return val;
    }

    public java.math.BigInteger getModul() {
      return modul;
    }

    public ModInteger clone() {
        return new ModInteger( modul, val );
    }

    public ModInteger copy(ModInteger c) {
        return new ModInteger( c.modul, c.val );
    }

    public ModInteger getZERO() {
        return new ModInteger( modul, java.math.BigInteger.ZERO );
    }

    public ModInteger getONE() {
        return new ModInteger( modul, java.math.BigInteger.ONE );
    }

    public ModInteger fromInteger(java.math.BigInteger a) {
	return new ModInteger(modul,a);
    }

    public ModInteger fromInteger(long a) {
	return new ModInteger(modul, a );
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
	return val.toString() + " mod(" + modul.toString() + ")";
    }


    public int compareTo(ModInteger b) {
        java.math.BigInteger v = b.val;
        if ( modul != b.modul ) {
            v = v.mod( modul );
        }
	return val.compareTo( v );
    }

    public static int MICOMP(ModInteger A, ModInteger B) {
      if ( A == null ) return -B.signum();
      return A.compareTo(B);
    }


    public boolean equals(Object b) {
	if ( ! ( b instanceof ModInteger ) ) {
           return false;
        }
	return (0 == compareTo( (ModInteger)b) );
    }


    public ModInteger abs() {
       return new ModInteger( modul, val.abs() );
    }

    public static ModInteger MIABS(ModInteger A) {
      if ( A == null ) return null;
      return A.abs();
    }


    public ModInteger negate() {
       return new ModInteger( modul, val.negate() );
    }

    public static ModInteger MINEG(ModInteger A) {
      if ( A == null ) return null;
      return A.negate();
    }


    public int signum() {
      return val.signum();
    }

    public static int MISIGN(ModInteger A) {
      if ( A == null ) return 0;
      return A.signum();
    }


    public ModInteger subtract(ModInteger S) {
      return new ModInteger( modul, val.subtract( S.val ) );
    }

    public static ModInteger MIDIF(ModInteger A, ModInteger B) {
      if ( A == null ) return (ModInteger) B.negate();
      return A.subtract(B);
    }


    public ModInteger divide(ModInteger S) {
     return multiply( S.inverse() );
    }

    public static ModInteger MIQ(ModInteger A, ModInteger B) {
      if ( A == null ) return null;
      return A.divide(B);
    }


    /** Integer inverse.  R is a non-zero integer.  
        S=1/R if defined else 0. */

    public ModInteger inverse() {
	return new ModInteger( modul, val.modInverse( modul ));
    }

    public static ModInteger MIINV(ModInteger A) {
      if ( A == null ) return null;
      return A.inverse();
    }


    public ModInteger remainder(ModInteger S) {
      return new ModInteger( modul, val.remainder( S.val ) );
    }

    public static ModInteger MIREM(ModInteger A, ModInteger B) {
      if ( A == null ) return null;
      return A.remainder(B);
    }


    public ModInteger random(int n) {
      return new ModInteger( modul, new java.math.BigInteger( n, random ) );
    }


    public ModInteger multiply(ModInteger S) {
      return new ModInteger( modul, val.multiply( S.val ) );
    }

    public static ModInteger MIPROD(ModInteger A, ModInteger B) {
      if ( A == null ) return null;
      return A.multiply(B);
    }


    public ModInteger add(ModInteger S) {
      return new ModInteger( modul, val.add( S.val ) );
    }

    public static ModInteger MISUM(ModInteger A, ModInteger B) {
      if ( A == null ) return null;
      return A.add(B);
    }


    public ModInteger parse(String s) {
        return new ModInteger(modul,s);
    }

    public ModInteger parse(Reader r) {
        return getZERO();
    }

}
