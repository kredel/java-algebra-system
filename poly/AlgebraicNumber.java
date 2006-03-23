/*
 * $Id$
 */

package edu.jas.poly;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.PrettyPrint;

import edu.jas.poly.GenPolynomial;

import java.util.Random;
import java.io.Reader;

/**
 * Algebraic number class based on GenPolynomial with RingElem interface.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */

public class AlgebraicNumber<C extends RingElem<C> > 
                               implements RingElem< AlgebraicNumber<C> >, 
                                          RingFactory< AlgebraicNumber<C> >  {


    /** Module part of the factory data structure. 
     */
    protected final GenPolynomial<C> modul;


    /** Value part of the element data structure. 
     */
    protected final GenPolynomial<C> val;


    /** Flag to remember if this algebraic number is a unit.
     * -1 is unknown, 1 is unit, 0 not a unit.
     */
    protected int isunit = -1; // initially unknown


    /** The constructor creates a AlgebraicNumber object 
     * from two GenPolynomial objects module and value part. 
     * @param m module GenPolynomial<C>.
     * @param a value GenPolynomial<C>.
     */
    public AlgebraicNumber(GenPolynomial<C> m, GenPolynomial<C> a) {
        modul = m;
        val = a.remainder(modul); //.monic() no go
    }


    /** The constructor creates a AlgebraicNumber object 
     * from a GenPolynomial object module. 
     * @param m module GenPolynomial<C>.
     */
    public AlgebraicNumber(GenPolynomial<C> m) {

        modul = m; // assert m != 0
        val = m.ring.getZERO();
    }


    /** Get the value part. 
     * @return val.
     */
    public GenPolynomial<C> getVal() {
        return val;
    }


    /** Get the module part. 
     * @return modul.
     */
    public GenPolynomial<C> getModul() {
        return modul;
    }


    /**  Clone this.
     * @see java.lang.Object#clone()
     */
    public AlgebraicNumber<C> clone() {
        return new AlgebraicNumber<C>( modul, val );
    }


    /** Copy AlgebraicNumber element c.
     * @param c
     * @return a copy of c.
     */
    public AlgebraicNumber<C> copy(AlgebraicNumber<C> c) {
        return new AlgebraicNumber<C>( c.modul, c.val );
    }


    /** Get the zero element.
     * @return 0 as AlgebraicNumber.
     */
    public AlgebraicNumber<C> getZERO() {
        return new AlgebraicNumber<C>( modul, modul.ring.getZERO() );
    }


    /**  Get the one element.
     * @return 1 as AlgebraicNumber.
     */
    public AlgebraicNumber<C> getONE() {
        return new AlgebraicNumber<C>( modul, modul.ring.getONE() );
    }

    
    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return modul.ring.isCommutative();
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        return modul.ring.isAssociative();
    }


    /** Get a AlgebraicNumber element from a BigInteger value.
     * @param a BigInteger.
     * @return a AlgebraicNumber.
     */
    public AlgebraicNumber<C> fromInteger(java.math.BigInteger a) {
        return new AlgebraicNumber<C>(modul, modul.ring.fromInteger(a) );
    }


    /** Get a AlgebraicNumber element from a long value.
     * @param a long.
     * @return a AlgebraicNumber.
     */
    public AlgebraicNumber<C> fromInteger(long a) {
        return new AlgebraicNumber<C>(modul, modul.ring.fromInteger(a) );
    }
    

    /** Is AlgebraicNumber zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return val.equals( val.ring.getZERO() );
    }


    /** Is AlgebraicNumber one. 
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return val.equals( val.ring.getONE() );
    }


    /** Is AlgebraicNumber unit. 
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if ( isunit > 0 ) {
            return true;
        } 
        if ( isunit == 0 ) {
            return false;
        } 
        // not jet known
        boolean u = val.gcd(modul).isUnit();
        if ( u ) {
            isunit = 1;
        } else {
            isunit = 0;
        }
        return ( u );
    }


    /** Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if ( PrettyPrint.isTrue() ) {
            return val.toString( val.ring.vars );
        } else {
            return "AlgebraicNumber[ " + val.toString() 
                + " mod " + modul.toString() + " ]";
        }
    }


    /** Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    public String toStringFac() {
        return "AlgebraicNumber[ " 
            + modul.toString() + " ]";
    }


    /** AlgebraicNumber comparison.  
     * @param b AlgebraicNumber.
     * @return sign(this-b).
     */
    public int compareTo(AlgebraicNumber<C> b) {
        GenPolynomial<C> v = b.val;
        if ( modul != b.modul ) {
            v = v.remainder( modul );
        }
        return val.compareTo( v );
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
        @SuppressWarnings("unchecked") // not jet working
        public boolean equals(Object b) {
        if ( ! ( b instanceof AlgebraicNumber ) ) {
            return false;
        }
        AlgebraicNumber<C> a = null;
        try {
            a = (AlgebraicNumber<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        return (0 == compareTo( a ) );
    }


    /** AlgebraicNumber absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public AlgebraicNumber<C> abs() {
        return new AlgebraicNumber<C>( modul, val.abs() );
    }


    /** AlgebraicNumber summation.
     * @param S AlgebraicNumber.
     * @return this+S.
     */
    public AlgebraicNumber<C> sum(AlgebraicNumber<C> S) {
        return new AlgebraicNumber<C>( modul, val.sum( S.val ) );
    }


    /** AlgebraicNumber negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public AlgebraicNumber<C> negate() {
        return new AlgebraicNumber<C>( modul, val.negate() );
    }


    /** AlgebraicNumber signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return val.signum();
    }


    /** AlgebraicNumber subtraction.
     * @param S AlgebraicNumber.
     * @return this-S.
     */
    public AlgebraicNumber<C> subtract(AlgebraicNumber<C> S) {
        return new AlgebraicNumber<C>( modul, val.subtract( S.val ) );
    }


    /** AlgebraicNumber division.
     * @param S AlgebraicNumber.
     * @return this/S.
     */
    public AlgebraicNumber<C> divide(AlgebraicNumber<C> S) {
        return multiply( S.inverse() );
    }


    /** AlgebraicNumber inverse.  
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this if defined. 
     */
    public AlgebraicNumber<C> inverse() {
        GenPolynomial<C> x = val.modInverse( modul );
        return new AlgebraicNumber<C>( modul, x );
    }


    /** AlgebraicNumber remainder.
     * @param S AlgebraicNumber.
     * @return this - (this/S)*S.
     */
    public AlgebraicNumber<C> remainder(AlgebraicNumber<C> S) {
        GenPolynomial<C> x = val.remainder( S.val );
        return new AlgebraicNumber<C>( modul, x );
    }


    /** AlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random integer mod modul.
     */
    public AlgebraicNumber<C> random(int n) {
        GenPolynomial<C> x = modul.ring.random( n ).monic();
        return new AlgebraicNumber<C>( modul, x);
    }


    /** AlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random integer mod modul.
     */
    public AlgebraicNumber<C> random(int n, Random rnd) {
        GenPolynomial<C> x = modul.ring.random( n, rnd ).monic();
        return new AlgebraicNumber<C>( modul, x);
    }


    /** AlgebraicNumber multiplication.
     * @param S AlgebraicNumber.
     * @return this*S.
     */
    public AlgebraicNumber<C> multiply(AlgebraicNumber<C> S) {
        GenPolynomial<C> x = val.multiply( S.val );
        return new AlgebraicNumber<C>( modul, x );
    }


    /** AlgebraicNumber monic.
     * @return this with monic value part.
     */
    public AlgebraicNumber<C> monic() {
        return new AlgebraicNumber<C>( modul, val.monic() );
    }


    /** Parse AlgebraicNumber from String.
     * @param s String.
     * @return AlgebraicNumber from s.
     */
    public AlgebraicNumber<C> parse(String s) {
        GenPolynomial<C> x = modul.ring.parse( s );
        return new AlgebraicNumber<C>( modul, x );
    }


    /** Parse AlgebraicNumber from Reader.
     * @param r Reader.
     * @return next AlgebraicNumber from r.
     */
    public AlgebraicNumber<C> parse(Reader r) {
        GenPolynomial<C> x = modul.ring.parse( r );
        return new AlgebraicNumber<C>( modul, x );
    }

}
