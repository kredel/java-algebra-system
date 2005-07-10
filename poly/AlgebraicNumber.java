/*
 * $Id$
 */

package edu.jas.poly;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

import edu.jas.poly.GenPolynomial;

//import java.util.Random;
import java.io.Reader;

/**
 * Algebraic GenPolynomial class with RingElem interface
 * @author Heinz Kredel
 */

public class AlgebraicNumber<C extends RingElem<C> > 
                               implements RingElem< AlgebraicNumber<C> >, 
                                          RingFactory< AlgebraicNumber<C> >  {

    protected final GenPolynomial<C> modul;
    protected final GenPolynomial<C> val;
    protected int isunit = -1; // unknown, remember this fact


    /**
     * Constructors for AlgebraicNumber
     */

    public AlgebraicNumber(GenPolynomial<C> m, GenPolynomial<C> a) {
        modul = m;
	val = a.remainder(modul); //.monic() no go
    }

    public AlgebraicNumber(GenPolynomial<C> m) {
        modul = m; // assert m != 0
	val = m.ring.getZERO();
    }

    public GenPolynomial<C> getVal() {
      return val;
    }

    public GenPolynomial<C> getModul() {
      return modul;
    }

    public AlgebraicNumber<C> clone() {
        return new AlgebraicNumber<C>( modul, val );
    }

    public AlgebraicNumber<C> copy(AlgebraicNumber<C> c) {
        return new AlgebraicNumber<C>( c.modul, c.val );
    }

    public AlgebraicNumber<C> getZERO() {
        return new AlgebraicNumber<C>( modul, modul.ring.getZERO() );
    }

    public AlgebraicNumber<C> getONE() {
        return new AlgebraicNumber<C>( modul, modul.ring.getONE() );
    }

    
    public AlgebraicNumber<C> fromInteger(java.math.BigInteger a) {
	return new AlgebraicNumber<C>(modul, modul.ring.fromInteger(a) );
    }

    public AlgebraicNumber<C> fromInteger(long a) {
	return new AlgebraicNumber<C>(modul, modul.ring.fromInteger(a) );
    }
    

    public boolean isZERO() {
	return val.equals( val.ring.getZERO() );
    }

    public boolean isONE() {
	return val.equals( val.ring.getONE() );
    }

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

    public String toString() {
	return "AlgebraicNumber[ " + val.toString() + " mod " + modul.toString() + " ]";
    }


    public int compareTo(AlgebraicNumber<C> b) {
        GenPolynomial<C> v = b.val;
        if ( modul != b.modul ) {
            v = v.remainder( modul );
        }
	return val.compareTo( v );
    }


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


    public AlgebraicNumber<C> add(AlgebraicNumber<C> S) {
      return new AlgebraicNumber<C>( modul, val.add( S.val ) );
    }

    public AlgebraicNumber<C> abs() {
       return new AlgebraicNumber<C>( modul, val.abs() );
    }

    public AlgebraicNumber<C> negate() {
       return new AlgebraicNumber<C>( modul, val.negate() );
    }

    public int signum() {
      return val.signum();
    }

    public AlgebraicNumber<C> subtract(AlgebraicNumber<C> S) {
      return new AlgebraicNumber<C>( modul, val.subtract( S.val ) );
    }



    public AlgebraicNumber<C> divide(AlgebraicNumber<C> S) {
     return multiply( S.inverse() );
    }

    /** Integer inverse.  R is a non-zero integer.  
        S=1/R if defined else 0. */

    public AlgebraicNumber<C> inverse() {
        GenPolynomial<C> x = val.modInverse( modul );
	return new AlgebraicNumber<C>( modul, x );
    }


    public AlgebraicNumber<C> remainder(AlgebraicNumber<C> S) {
      GenPolynomial<C> x = val.remainder( S.val );
      return new AlgebraicNumber<C>( modul, x );
    }


    public AlgebraicNumber<C> random(int n) {
      GenPolynomial<C> x = modul.ring.random( n ).monic();
      return new AlgebraicNumber<C>( modul, x);
    }


    public AlgebraicNumber<C> multiply(AlgebraicNumber<C> S) {
      GenPolynomial<C> x = val.multiply( S.val );
      return new AlgebraicNumber<C>( modul, x );
    }

    public AlgebraicNumber<C> monic() {
	return new AlgebraicNumber<C>( modul, val.monic() );
    }


    public AlgebraicNumber<C> parse(String s) {
        GenPolynomial<C> x = modul.ring.parse( s );
	return new AlgebraicNumber<C>( modul, x );
    }

    public AlgebraicNumber<C> parse(Reader r) {
        GenPolynomial<C> x = modul.ring.parse( r );
	return new AlgebraicNumber<C>( modul, x );
    }

}
