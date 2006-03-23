/*
 * $Id$
 */

package edu.jas.arith;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.PrettyPrint;

import java.util.Random;
import java.io.Reader;

/**
 * ModInteger class with RingElem interface
 * and with the familiar SAC method names.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 * @see java.math.BigInteger
 */

public final class ModInteger implements RingElem<ModInteger>, 
                                         RingFactory<ModInteger> {


    /** Module part of the factory data structure. 
     */
    protected final java.math.BigInteger modul;


    /** Value part of the element data structure. 
     */
    protected final java.math.BigInteger val;


    private final static Random random = new Random();


    /** The constructor creates a ModInteger object 
     * from two BigInteger objects module and value part. 
     * @param m math.BigInteger.
     * @param a math.BigInteger.
     */
    public ModInteger(java.math.BigInteger m, java.math.BigInteger a) {
        modul = m;
        val = a.mod(modul);
    }


    /** The constructor creates a ModInteger object 
     * from two longs objects module and value part. 
     * @param m long.
     * @param a long.
     */
    public ModInteger(long m, long a) {
        this( 
             new java.math.BigInteger( String.valueOf(m) ),
             new java.math.BigInteger( String.valueOf(a) )
             );
    }


    /** The constructor creates a ModInteger object 
     * from two String objects module and value part. 
     * @param m String.
     * @param s String.
     */
    public ModInteger(String m, String s) {
        this( 
             new java.math.BigInteger( m ),
             new java.math.BigInteger( s )
             );
    }


    /** The constructor creates a ModInteger object 
     * from a BigInteger object module and a String value part. 
     * @param m BigInteger.
     * @param s String.
     */
    public ModInteger(java.math.BigInteger m, String s) {
        this( m, new java.math.BigInteger( s ) );
    }


    /** The constructor creates a ModInteger object 
     * from a BigInteger object module and a long value part. 
     * @param m BigInteger.
     * @param s long.
     */
    public ModInteger(java.math.BigInteger m, long s) {
        this( m, new java.math.BigInteger( String.valueOf(s) ) );
    }


    /** The constructor creates a 0 ModInteger object 
     * from a BigInteger object module. 
     * @param m BigInteger.
     */
    public ModInteger(java.math.BigInteger m) {
        modul = m; // assert m != 0
        val = java.math.BigInteger.ZERO;
    }


    /** Get the value part. 
     * @return val.
     */
    public java.math.BigInteger getVal() {
        return val;
    }


    /** Get the module part. 
     * @return modul.
     */
    public java.math.BigInteger getModul() {
        return modul;
    }


    /**  Clone this.
     * @see java.lang.Object#clone()
     */
    public ModInteger clone() {
        return new ModInteger( modul, val );
    }


    /** Copy ModInteger element c.
     * @param c
     * @return a copy of c.
     */
    public ModInteger copy(ModInteger c) {
        return new ModInteger( c.modul, c.val );
    }


    /** Get the zero element.
     * @return 0 as ModInteger.
     */
    public ModInteger getZERO() {
        return new ModInteger( modul, java.math.BigInteger.ZERO );
    }


    /**  Get the one element.
     * @return 1 as ModInteger.
     */
    public ModInteger getONE() {
        return new ModInteger( modul, java.math.BigInteger.ONE );
    }


    /**
     * Query if this ring is commutative.
     * @return true.
     */
    public boolean isCommutative() {
        return true;
    }


    /**
     * Query if this ring is associative.
     * @return true.
     */
    public boolean isAssociative() {
        return true;
    }


    /** Get a ModInteger element from a BigInteger value.
     * @param a BigInteger.
     * @return a ModInteger.
     */
    public ModInteger fromInteger(java.math.BigInteger a) {
        return new ModInteger(modul,a);
    }


    /** Get a ModInteger element from a long value.
     * @param a long.
     * @return a ModInteger.
     */
    public ModInteger fromInteger(long a) {
        return new ModInteger(modul, a );
    }


    /** Is ModInteger number zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return val.equals( java.math.BigInteger.ZERO );
    }


    /** Is ModInteger number one. 
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return val.equals( java.math.BigInteger.ONE );
    }


    /** Is ModInteger number a unit. 
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        java.math.BigInteger g = modul.gcd( val );
        return ( g.equals(java.math.BigInteger.ONE) 
                 || g.equals(java.math.BigInteger.ONE.negate()) );
    }


    /**  Get the String representation.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if ( PrettyPrint.isTrue() ) {
            return val.toString();
        } else {
            return val.toString() + " mod(" + modul.toString() + ")";
        }
    }


    /** ModInteger comparison.  
     * @param b ModInteger.
     * @return sign(this-b).
     */
    public int compareTo(ModInteger b) {
        java.math.BigInteger v = b.val;
        if ( modul != b.modul ) {
            v = v.mod( modul );
        }
        return val.compareTo( v );
    }


    /** ModInteger comparison.
     * @param A  ModInteger.
     * @param B  ModInteger.
     * @return sign(this-b).
     */
    public static int MICOMP(ModInteger A, ModInteger B) {
        if ( A == null ) return -B.signum();
        return A.compareTo(B);
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
        public boolean equals(Object b) {
        if ( ! ( b instanceof ModInteger ) ) {
            return false;
        }
        return (0 == compareTo( (ModInteger)b) );
    }


    /** Hash code for this ModInteger.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return 37 * val.hashCode() + modul.hashCode();
    }


    /** ModInteger absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public ModInteger abs() {
        return new ModInteger( modul, val.abs() );
    }


    /** ModInteger absolute value.
     * @param A ModInteger.
     * @return the absolute value of A.
     */
    public static ModInteger MIABS(ModInteger A) {
        if ( A == null ) return null;
        return A.abs();
    }


    /** ModInteger negative. 
     * @see edu.jas.structure.RingElem#negate()
     * @return -this.
     */
    public ModInteger negate() {
        return new ModInteger( modul, val.negate() );
    }


    /** ModInteger negative.
     * @param A ModInteger.
     * @return -A.
     */
    public static ModInteger MINEG(ModInteger A) {
        if ( A == null ) return null;
        return A.negate();
    }


    /** ModInteger signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return val.signum();
    }


    /** ModInteger signum.
     * @param A ModInteger
     * @return signum(A).
     */
    public static int MISIGN(ModInteger A) {
        if ( A == null ) return 0;
        return A.signum();
    }


    /** ModInteger subtraction.
     * @param S ModInteger. 
     * @return this-S.
     */
    public ModInteger subtract(ModInteger S) {
        return new ModInteger( modul, val.subtract( S.val ) );
    }


    /** ModInteger subtraction.
     * @param A ModInteger.
     * @param B ModInteger.
     * @return A-B.
     */
    public static ModInteger MIDIF(ModInteger A, ModInteger B) {
        if ( A == null ) return B.negate();
        return A.subtract(B);
    }


    /** ModInteger divide.
     * @param S ModInteger.
     * @return this/S.
     */
    public ModInteger divide(ModInteger S) {
        return multiply( S.inverse() );
    }


    /** ModInteger quotient.
     * @param A ModInteger. 
     * @param B ModInteger.
     * @return A/B.
     */
    public static ModInteger MIQ(ModInteger A, ModInteger B) {
        if ( A == null ) return null;
        return A.divide(B);
    }


    /** ModInteger inverse.  
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S=1/this if defined. 
     */
    public ModInteger inverse() {
        return new ModInteger( modul, val.modInverse( modul ));
    }


    /** ModInteger inverse.  
     * @param A is a non-zero integer.  
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S=1/A if defined.
     */
    public static ModInteger MIINV(ModInteger A) {
        if ( A == null ) return null;
        return A.inverse();
    }


    /** ModInteger remainder.
     * @param S ModInteger.
     * @return this - (this/S)*S.
     */
    public ModInteger remainder(ModInteger S) {
        return new ModInteger( modul, val.remainder( S.val ) );
    }


    /** ModInteger remainder.
     * @param A ModInteger.
     * @param B ModInteger.
     * @return A - (A/B)*B.
     */
    public static ModInteger MIREM(ModInteger A, ModInteger B) {
        if ( A == null ) return null;
        return A.remainder(B);
    }


    /** ModInteger random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random integer mod modul.
     */
    public ModInteger random(int n) {
        return random( n, random );
    }


    /** ModInteger random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random integer mod modul.
     */
    public ModInteger random(int n, Random rnd) {
        java.math.BigInteger v = new java.math.BigInteger( n, rnd );
        return new ModInteger( modul, v );
    }


    /** ModInteger multiply.
     * @param S ModInteger.
     * @return this*S.
     */
    public ModInteger multiply(ModInteger S) {
        return new ModInteger( modul, val.multiply( S.val ) );
    }


    /** ModInteger product.
     * @param A ModInteger.
     * @param B ModInteger.
     * @return A*B.
     */
    public static ModInteger MIPROD(ModInteger A, ModInteger B) {
        if ( A == null ) return null;
        return A.multiply(B);
    }


    /** ModInteger summation.
     * @param S ModInteger.
     * @return this+S.
     */
    public ModInteger sum(ModInteger S) {
        return new ModInteger( modul, val.add( S.val ) );
    }


    /** ModInteger summation.
     * @param A ModInteger.
     * @param B ModInteger.
     * @return A+B.
     */
    public static ModInteger MISUM(ModInteger A, ModInteger B) {
        if ( A == null ) return null;
        return A.sum(B);
    }


    /** Parse ModInteger from String.
     * @param s String.
     * @return ModInteger from s.
     */
    public ModInteger parse(String s) {
        return new ModInteger(modul,s);
    }


    /** Parse ModInteger from Reader.
     * @param r Reader.
     * @return next ModInteger from r.
     */
    public ModInteger parse(Reader r) {
        return getZERO();
    }

}
