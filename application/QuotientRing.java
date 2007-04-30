/*
 * $Id$
 */

package edu.jas.application;

import java.util.Random;
import java.io.Reader;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
//import edu.jas.structure.PrettyPrint;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;


/**
 * Quotient ring class based on GenPolynomial with RingElem interface.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class QuotientRing<C extends RingElem<C> > 
             implements RingFactory< Quotient<C> >  {

     private static Logger logger = Logger.getLogger(QuotientRing.class);
     private boolean debug = logger.isDebugEnabled();


    /** Polynomial ring of the factory. 
     */
    public final GenPolynomialRing<C> ring;


    /** The constructor creates a QuotientRing object 
     * from a GenPolynomialRing and a GenPolynomial list. 
     * @param r polynomial ring.
     */
    public QuotientRing(GenPolynomialRing<C> r) {
        ring = r;
    }


    /** Copy Quotient element c.
     * @param c
     * @return a copy of c.
     */
    public Quotient<C> copy(Quotient<C> c) {
        return new Quotient<C>( c.ring, c.num, c.den, true );
    }


    /** Get the zero element.
     * @return 0 as Quotient.
     */
    public Quotient<C> getZERO() {
        return new Quotient<C>( this, ring.getZERO() );
    }


    /**  Get the one element.
     * @return 1 as Quotient.
     */
    public Quotient<C> getONE() {
        return new Quotient<C>( this, ring.getONE() );
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
     * @return true.
     */
    public boolean isField() {
        return true;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return ring.characteristic();
    }


    /** Get a Quotient element from a BigInteger value.
     * @param a BigInteger.
     * @return a Quotient.
     */
    public Quotient<C> fromInteger(java.math.BigInteger a) {
        return new Quotient<C>( this, ring.fromInteger(a) );
    }


    /** Get a Quotient element from a long value.
     * @param a long.
     * @return a Quotient.
     */
    public Quotient<C> fromInteger(long a) {
        return new Quotient<C>( this, ring.fromInteger(a) );
    }
    

    /** Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Quotient[ " 
                + ring.toString() + " ]";
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object b) {
        if ( ! ( b instanceof QuotientRing ) ) {
           return false;
        }
        QuotientRing<C> a = null;
        try {
            a = (QuotientRing<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        return ring.equals( a.ring );
    }


    /** Hash code for this quotient ring.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h;
       h = ring.hashCode();
       return h;
    }


    /** Quotient random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random residue element.
     */
    public Quotient<C> random(int n) {
        GenPolynomial<C> r = ring.random( n ).monic();
        GenPolynomial<C> s = ring.random( n ).monic();
        while ( s.isZERO() ) {
            s = ring.random( n ).monic();
        }
        return new Quotient<C>( this, r, s, false );
    }


    /**
     * Generate a random residum polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @return a random residue polynomial.
     */
    public Quotient<C> random(int k, int l, int d, float q) {
        GenPolynomial<C> r = ring.random(k,l,d,q).monic();
        GenPolynomial<C> s = ring.random(k,l,d,q).monic();
        while ( s.isZERO() ) {
            s = ring.random( k,l,d,q ).monic();
        }
        return new Quotient<C>( this, r, s, false );
    }


    /** Quotient random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random residue element.
     */
    public Quotient<C> random(int n, Random rnd) {
        GenPolynomial<C> r = ring.random( n, rnd ).monic();
        GenPolynomial<C> s = ring.random( n, rnd ).monic();
        while ( s.isZERO() ) {
            s = ring.random( n, rnd ).monic();
        }
        return new Quotient<C>( this, r, s, false);
    }


    /** Parse Quotient from String.
     * @param s String.
     * @return Quotient from s.
     */
    public Quotient<C> parse(String s) {
        GenPolynomial<C> x = ring.parse( s );
        return new Quotient<C>( this, x );
    }


    /** Parse Quotient from Reader.
     * @param r Reader.
     * @return next Quotient from r.
     */
    public Quotient<C> parse(Reader r) {
        GenPolynomial<C> x = ring.parse( r );
        return new Quotient<C>( this, x );
    }

}
