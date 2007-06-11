/*
 * $Id$
 */

package edu.jas.vector;

//import java.io.IOException;
import java.io.Reader;
//import java.io.StringReader;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.AlgebraFactory;


/**
 * GenMatrixRing generic vector implementing RingElem.
 * vectors of n columns over C.
 * @author Heinz Kredel
 */

public class GenMatrixRing<C extends RingElem<C> > 
            implements AlgebraFactory< GenMatrix<C>, C > {

    private static Logger logger = Logger.getLogger(GenMatrixRing.class);

    public final RingFactory< C > coFac;

    public final int cols;

    public final GenMatrix<C> ZERO;

    public final List<GenMatrix<C>> BASIS;

    private final static Random random = new Random(); 

    public final static float DEFAULT_DENSITY = 0.5f; 

    private float density = DEFAULT_DENSITY; 



/**
 * Constructors for GenMatrixRing.
 */

    public GenMatrixRing(RingFactory< C > b, int s) {
        coFac = b;
        cols = s;
        ArrayList<C> z = new ArrayList<C>( cols ); 
        for ( int i = 0; i < cols; i++ ) {
            z.add( coFac.getZERO() );
        }
        ZERO = new GenMatrix<C>( this, z );
        BASIS = new ArrayList<GenMatrix<C>>( cols ); 
        C one = coFac.getONE();
        ArrayList<C> v; 
        for ( int i = 0; i < cols; i++ ) {
            v = (ArrayList<C>)z.clone();
            v.set(i, one );
            BASIS.add( new GenMatrix<C>( this, v ) );
        }
    }


    /**
     * toString method.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append( coFac.getClass().getSimpleName() );
        s.append("[" + cols + "]");
        return s.toString();
    }


    /**
     * Get the constant one for the GenMatrix.
     * @return ZERO.
     */
    public GenMatrix<C> getZERO() {
        return ZERO;
    }


    /**
     * Get the constant one for the GenMatrix.
     * @return 1.
     */
    public GenMatrix<C> getONE() {
        return null;
    }


    @Override
    public boolean equals( Object other ) { 
        if ( ! (other instanceof GenMatrixRing) ) {
            return false;
        }
        GenMatrixRing omod = (GenMatrixRing)other;
        if ( cols != omod.cols ) {
            return false;
        }
        if ( ! coFac.equals(omod.coFac) ) {
            return false;
        }
        return true;
    }


    /** Hash code for this matrix ring.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h;
       h = cols;
       h = 37 * h + coFac.hashCode();
       return h;
    }


    /**
     * Query if this ring is a field.
     * May return false if it is to hard to determine if this ring is a field.
     * @return true if it is known that this ring is a field, else false.
     */
    public boolean isField() {
       return false;
    }


    /**
     * Query if this monoid is commutative.
     * @return true if this monoid is commutative, else false.
     */
    public boolean isCommutative() {
        return false;
    }


    /**
     * Query if this ring is associative.
     * @return true if this monoid is associative, else false.
     */
    public boolean isAssociative() {
        return true;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
       return coFac.characteristic();
    }


    /**
     * Get the vector for a.
     * @param a long
     * @return vector corresponding to a.
     */
    public GenMatrix<C> fromInteger(long a) {
        C c = coFac.fromInteger(a);
        return BASIS.get(0).scalarMultiply(c);
    }


    /**
     * Get the vector for a.
     * @param a long
     * @return vector corresponding to a.
     */
    public GenMatrix<C> fromInteger(BigInteger a) {
        C c = coFac.fromInteger(a);
        return BASIS.get(0).scalarMultiply(c);
    }


    /**
     * From List of coefficients.
     * @param v list of coefficients.
     */
    public GenMatrix<C> fromList(List<C> v) {
        if ( v == null ) {
            return ZERO;
        }
        if ( v.size() > cols ) {
           throw new RuntimeException("size v > cols " + cols + " < " + v);
        }
        List<C> r = new ArrayList<C>( cols ); 
        r.addAll( v );
        // pad with zeros if required:
        for ( int i = r.size(); i < cols; i++ ) {
            r.add( coFac.getZERO() );
        }
        return new GenMatrix<C>( this, r );
    }


    /**
     * Random vector.
     * @param k size of random coefficients.
     */
    public GenMatrix<C> random(int k) {
        return random( k, density, random );
    }


    /**
     * Random vector.
     * @param k size of random coefficients.
     * @param q density of nozero coefficients.
     */
    public GenMatrix<C> random(int k, float q) {
        return random( k, q, random );
    }


    /**
     * Random vector.
     * @param k size of random coefficients.
     * @param random is a source for random bits.
     * @return a random element.
     */
    public GenMatrix<C> random(int k, Random random) {
        return random( k, density, random );
    }


    /**
     * Random vector.
     * @param k size of random coefficients.
     * @param q density of nozero coefficients.
     * @param random is a source for random bits.
     * @return a random element.
     */
    public GenMatrix<C> random(int k, float q, Random random) {
        List<C> r = new ArrayList<C>( cols ); 
        for ( int i = 0; i < cols; i++ ) {
            if ( random.nextFloat() < q ) {
                r.add( coFac.random(k) );
            } else {
                r.add( coFac.getZERO() );
            }
        }
        return new GenMatrix<C>( this, r );
    }


    /**
     * copy vector.
     */
    public GenMatrix<C> copy(GenMatrix<C> c) {
        if ( c == null ) {
           return c;
        } else {
           return c.clone();
        }
        //return new GenMatrix<C>( this, c.val );//clone val
    }


    /**
     * parse a vector from a String.
     */
    public GenMatrix<C> parse(String s) {
        throw new RuntimeException("parse not jet implemented");
        //return ZERO;
    }


    /**
     * parse a vector from a Reader.
     */
    public GenMatrix<C> parse(Reader r) {
        throw new RuntimeException("parse not jet implemented");
        //return ZERO;
    }


}
