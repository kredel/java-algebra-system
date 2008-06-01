/*
 * $Id$
 */

package edu.jas.application;

import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;


import edu.jas.structure.GcdRingElem;

import edu.jas.poly.GenPolynomial;


/**
 * Condition as a pair of zero and non-zero polynomials.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */
public class Condition<C extends GcdRingElem<C> > 
             implements Serializable /*, Comparable<Condition>*/ {

    public final Ideal<C> zero;
    public final List<GenPolynomial<C>> nonZero;


    /**
     * Condition constructor.
     * @param z an ideal of zero polynomials.
     * @param nz a list of non-zero polynomials.
     */
    public Condition(Ideal<C> z, List<GenPolynomial<C>> nz) {
        if ( z == null || nz == null ) {
            throw new RuntimeException("only for non nill condition parts");
        }
        zero = z;
        nonZero = nz;
    }


    /**
     * Condition constructor.
     * @param z an ideal of zero polynomials.
     */
    public Condition(Ideal<C> z) {
        this(z, new ArrayList<GenPolynomial<C>>());
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return "Condition[ 0 == " + zero.list.list.toString() 
                      + ", 0 != " + nonZero + "]";
    }


    /**
     * equals.
     * @param ob an Object.
     * @return true if this is equal to o, else false.
     */
    @Override
    public boolean equals(Object ob) {
        if ( ! (ob instanceof Condition) ) {
           return false;
        }
        Condition<C> c = (Condition<C>) ob;
        if ( ! zero.equals( c.zero ) ) {
           return false;
        }
        for ( GenPolynomial<C> p : nonZero ) {
            if ( ! c.nonZero.contains( p ) ) {
               return false;
            }
        }
        List<GenPolynomial<C>> cnz = c.nonZero;
        for ( GenPolynomial<C> p : c.nonZero ) {
            if ( ! nonZero.contains( p ) ) {
               return false;
            }
        }
        return true;
    }


    /**
     * Is empty condition.
     * @return true if this is the empty condition, else false.
     */
    public boolean isEmpty() {
        return ( zero.isZERO() && nonZero.size() == 0 );
    }


    /**
     * Sum new zero polynomial.
     * @param z a zero polynomial.
     */
    public Condition<C> sumZero(GenPolynomial<C> z) {
        return new Condition<C>( zero.sum( z ), nonZero );
    }


    /**
     * Sum new non-zero polynomial.
     * @param nz a non-zero polynomial.
     */
    public Condition<C> sumNonZero(GenPolynomial<C> nz) {
        List<GenPolynomial<C>> list = new ArrayList<GenPolynomial<C>>( nonZero );
        list.add( nz );
        return new Condition<C>( zero, list );
    }

}
