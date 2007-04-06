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
 * Algebraic number factory class based on GenPolynomial with RingElem 
 * interface.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */

public class AlgebraicNumberRing<C extends RingElem<C> > 
              implements RingFactory< AlgebraicNumber<C> >  {


    /** Ring part of the factory data structure. 
     */
    public final GenPolynomialRing<C> ring;


    /** Module part of the factory data structure. 
     */
    public final GenPolynomial<C> modul;


    /** Indicator if this ring is a field.
     */
    protected int isField = -1; // initially unknown


    /** The constructor creates a AlgebraicNumber factory object 
     * from a GenPolynomial objects module. 
     * @param m module GenPolynomial<C>.
     */
    public AlgebraicNumberRing(GenPolynomial<C> m) {
        ring = m.ring;
        modul = m; // assert m != 0
    }


    /** The constructor creates a AlgebraicNumber factory object 
     * from a GenPolynomial objects module. 
     * @param m module GenPolynomial<C>.
     * @param isField indicator if m is prime.
     */
    public AlgebraicNumberRing(GenPolynomial<C> m, boolean isField) {
        ring = m.ring;
        modul = m; // assert m != 0
        this.isField = ( isField ? 1 :  0 );
    }


    /** Get the module part. 
     * @return modul.
    public GenPolynomial<C> getModul() {
        return modul;
    }
     */


    /** Copy AlgebraicNumber element c.
     * @param c
     * @return a copy of c.
     */
    public AlgebraicNumber<C> copy(AlgebraicNumber<C> c) {
        return new AlgebraicNumber<C>( this, c.val );
    }


    /** Get the zero element.
     * @return 0 as AlgebraicNumber.
     */
    public AlgebraicNumber<C> getZERO() {
        return new AlgebraicNumber<C>( this, ring.getZERO() );
    }


    /**  Get the one element.
     * @return 1 as AlgebraicNumber.
     */
    public AlgebraicNumber<C> getONE() {
        return new AlgebraicNumber<C>( this, ring.getONE() );
    }

    
    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return ring.isCommutative();
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        return ring.isAssociative();
    }


    /**
     * Query if this ring is a field.
     * @return true if modul is prime, else false.
     */
    public boolean isField() {
        if ( isField > 0 ) { 
           return true;
        }
        if ( isField == 0 ) { 
           return false;
        }
        //if ( modul.isProbablePrime(certainty) ) {
        //   isField = 1;
        //   return true;
        //}
        //isField = 0;
        return false;
    }


    /** Get a AlgebraicNumber element from a BigInteger value.
     * @param a BigInteger.
     * @return a AlgebraicNumber.
     */
    public AlgebraicNumber<C> fromInteger(java.math.BigInteger a) {
        return new AlgebraicNumber<C>( this, ring.fromInteger(a) );
    }


    /** Get a AlgebraicNumber element from a long value.
     * @param a long.
     * @return a AlgebraicNumber.
     */
    public AlgebraicNumber<C> fromInteger(long a) {
        return new AlgebraicNumber<C>( this, ring.fromInteger(a) );
    }
    

    /** Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "AlgebraicNumberRing[ " 
              + modul.toString() + " | isField="
              + isField + " :: "
              + ring.toString() + " ]";
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
        @SuppressWarnings("unchecked") // not jet working
        public boolean equals(Object b) {
        if ( ! ( b instanceof AlgebraicNumberRing ) ) {
            return false;
        }
        AlgebraicNumberRing<C> a = null;
        try {
            a = (AlgebraicNumberRing<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        return modul.equals( a.modul );
    }


    /** AlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random integer mod modul.
     */
    public AlgebraicNumber<C> random(int n) {
        GenPolynomial<C> x = ring.random( n ).monic();
        return new AlgebraicNumber<C>( this, x);
    }


    /** AlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random integer mod modul.
     */
    public AlgebraicNumber<C> random(int n, Random rnd) {
        GenPolynomial<C> x = ring.random( n, rnd ).monic();
        return new AlgebraicNumber<C>( this, x);
    }


    /** Parse AlgebraicNumber from String.
     * @param s String.
     * @return AlgebraicNumber from s.
     */
    public AlgebraicNumber<C> parse(String s) {
        GenPolynomial<C> x = ring.parse( s );
        return new AlgebraicNumber<C>( this, x );
    }


    /** Parse AlgebraicNumber from Reader.
     * @param r Reader.
     * @return next AlgebraicNumber from r.
     */
    public AlgebraicNumber<C> parse(Reader r) {
        GenPolynomial<C> x = ring.parse( r );
        return new AlgebraicNumber<C>( this, x );
    }

}
