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
 * Algebraic number class based on GenPolynomial with RingElem interface.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class TensorRing<C extends RingElem<C> > 
             implements RingFactory< Tensor<C> >  {

     private static Logger logger = Logger.getLogger(TensorRing.class);
     private boolean debug = logger.isDebugEnabled();


    /** Polynomial ring of the factory. 
     */
    public final GenPolynomialRing<C> ring;


    /** The constructor creates a TensorRing object 
     * from a GenPolynomialRing and a GenPolynomial list. 
     * @param r polynomial ring.
     */
    public TensorRing(GenPolynomialRing<C> r) {
        ring = r;
    }


    /** Copy Tensor element c.
     * @param c
     * @return a copy of c.
     */
    public Tensor<C> copy(Tensor<C> c) {
        return new Tensor<C>( c.ring, c.num, c.den, true );
    }


    /** Get the zero element.
     * @return 0 as Tensor.
     */
    public Tensor<C> getZERO() {
        return new Tensor<C>( this, ring.getZERO() );
    }


    /**  Get the one element.
     * @return 1 as Tensor.
     */
    public Tensor<C> getONE() {
        return new Tensor<C>( this, ring.getONE() );
    }

    
    /** Get a Tensor element from a BigInteger value.
     * @param a BigInteger.
     * @return a Tensor.
     */
    public Tensor<C> fromInteger(java.math.BigInteger a) {
        return new Tensor<C>( this, ring.fromInteger(a) );
    }


    /** Get a Tensor element from a long value.
     * @param a long.
     * @return a Tensor.
     */
    public Tensor<C> fromInteger(long a) {
        return new Tensor<C>( this, ring.fromInteger(a) );
    }
    

    /** Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Tensor[ " 
                + ring.toString() + " ]";
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object b) {
        if ( ! ( b instanceof TensorRing ) ) {
           return false;
        }
        TensorRing<C> a = null;
        try {
            a = (TensorRing<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        return ring.equals( a.ring );
    }


    /** Tensor random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random residue element.
     */
    public Tensor<C> random(int n) {
        GenPolynomial<C> r = ring.random( n ).monic();
        GenPolynomial<C> s = ring.random( n ).monic();
        while ( s.isZERO() ) {
            s = ring.random( n ).monic();
        }
        return new Tensor<C>( this, r, s, false );
    }


    /**
     * Generate a random residum polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @return a random residue polynomial.
     */
    public Tensor<C> random(int k, int l, int d, float q) {
        GenPolynomial<C> r = ring.random(k,l,d,q).monic();
        GenPolynomial<C> s = ring.random(k,l,d,q).monic();
        while ( s.isZERO() ) {
            s = ring.random( k,l,d,q ).monic();
        }
        return new Tensor<C>( this, r, s, false );
    }


    /** Tensor random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random residue element.
     */
    public Tensor<C> random(int n, Random rnd) {
        GenPolynomial<C> r = ring.random( n, rnd ).monic();
        GenPolynomial<C> s = ring.random( n, rnd ).monic();
        while ( s.isZERO() ) {
            s = ring.random( n, rnd ).monic();
        }
        return new Tensor<C>( this, r, s, false);
    }


    /** Parse Tensor from String.
     * @param s String.
     * @return Tensor from s.
     */
    public Tensor<C> parse(String s) {
        GenPolynomial<C> x = ring.parse( s );
        return new Tensor<C>( this, x );
    }


    /** Parse Tensor from Reader.
     * @param r Reader.
     * @return next Tensor from r.
     */
    public Tensor<C> parse(Reader r) {
        GenPolynomial<C> x = ring.parse( r );
        return new Tensor<C>( this, x );
    }

}
