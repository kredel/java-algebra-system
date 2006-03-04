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
 * Tensor ring class based on GenPolynomial with RingElem interface.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class TensorRing<C extends RingElem<C> > 
             implements RingFactory< Tensor<C> >  {

     private static Logger logger = Logger.getLogger(TensorRing.class);
     private boolean debug = logger.isDebugEnabled();


    /** Polynomial ring 1 of the factory. 
     */
    public final GenPolynomialRing<C> ring1;


    /** Polynomial ring 2 of the factory. 
     */
    public final GenPolynomialRing<C> ring2;


    /** The constructor creates a TensorRing object 
     * from two GenPolynomialRings. 
     * @param r1 first polynomial ring.
     * @param r2 second polynomial ring.
     */
    public TensorRing(GenPolynomialRing<C> r1,GenPolynomialRing<C> r2) {
        if ( true || debug ) {
           if ( ! r1.coFac.equals( r2.coFac ) ) { // can not happen 
              throw new RuntimeException("coefficient rings not equal");
           }
        }
        ring1 = r1;
        ring2 = r2;
    }


    /** Copy Tensor element c.
     * @param c
     * @return a copy of c.
     */
    public Tensor<C> copy(Tensor<C> c) {
        return new Tensor<C>( c.ring, c.coeff, c.pol1, c.pol2 );
    }


    /** Get the zero element.
     * @return 0 as Tensor.
     */
    public Tensor<C> getZERO() {
        return new Tensor<C>( this, 
                              ring1.coFac.getZERO(),
                              ring1.getZERO(),
                              ring2.getZERO() );
    }


    /**  Get the one element.
     * @return 1 as Tensor.
     */
    public Tensor<C> getONE() {
        return new Tensor<C>( this, 
                              ring1.coFac.getONE(),
                              ring1.getONE(),
                              ring2.getONE() );
    }

    
    /** Get a Tensor element from a BigInteger value.
     * @param a BigInteger.
     * @return a Tensor.
     */
    public Tensor<C> fromInteger(java.math.BigInteger a) {
        return new Tensor<C>( this, 
                              ring1.coFac.fromInteger(a),
                              ring1.getONE(),
                              ring2.getONE() );
    }


    /** Get a Tensor element from a long value.
     * @param a long.
     * @return a Tensor.
     */
    public Tensor<C> fromInteger(long a) {
        return new Tensor<C>( this, 
                              ring1.coFac.fromInteger(a),
                              ring1.getONE(),
                              ring2.getONE() );
    }
    

    /** Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "TensorRing[ " 
                + ring1.toString() + ", "
                + ring2.toString() + " ]";
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
        return ring1.equals( a.ring1 ) && ring2.equals( a.ring2 );
    }


    /** Tensor random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random residue element.
     */
    public Tensor<C> random(int n) {
        GenPolynomial<C> p1 = ring1.random( n ).monic();
        GenPolynomial<C> p2 = ring2.random( n ).monic();
        return new Tensor<C>( this, null, p1, p2 );
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
        GenPolynomial<C> p1 = ring1.random(k,l,d,q).monic();
        GenPolynomial<C> p2 = ring2.random(k,l,d,q).monic();
        return new Tensor<C>( this, null, p1, p2 );
    }


    /** Tensor random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random residue element.
     */
    public Tensor<C> random(int n, Random rnd) {
        GenPolynomial<C> p1 = ring1.random( n, rnd ).monic();
        GenPolynomial<C> p2 = ring2.random( n, rnd ).monic();
        return new Tensor<C>( this, null, p1, p2 );
    }


    /** Parse Tensor from String.
     * @param s String.
     * @return Tensor from s.
     * does not work
     */
    public Tensor<C> parse(String s) {
        GenPolynomial<C> p1 = ring1.parse( s );
        GenPolynomial<C> p2 = ring2.parse( s );
        return new Tensor<C>( this, null , p1, p2 );
    }


    /** Parse Tensor from Reader.
     * @param r Reader.
     * @return next Tensor from r.
     * does not work
     */
    public Tensor<C> parse(Reader r) {
        GenPolynomial<C> p1 = ring1.parse( r );
        GenPolynomial<C> p2 = ring1.parse( r );
        return new Tensor<C>( this, null, p1, p2 );
    }

}
