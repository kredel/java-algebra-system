
/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;
//import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Collections;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.NotInvertibleException;

import edu.jas.kern.PrettyPrint;
import edu.jas.kern.PreemptingException;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomialRing;


/**
 * Colored Polynomials implementing RingElem.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ColorPolynomial<C extends RingElem<C> > 
             /*implements RingElem< ColorPolynomial<C> >*/ {


    /** The part with green (= zero) terms and coefficients.
     */
    public final GenPolynomial<C> green;


    /** The part with red (= non zero) terms and coefficients.
     */
    public final GenPolynomial<C> red;


    /** The part with white (= unknown color) terms and coefficients.
     */
    public final GenPolynomial<C> white;


    /** The constructor creates a colored polynomial from
     * the colored parts.
     * @param g green colored terms and coefficients.
     * @param r red colored terms and coefficients.
     * @param w white colored terms and coefficients.
     */
    public ColorPolynomial(GenPolynomial<C> g, GenPolynomial<C> r, GenPolynomial<C> w) {
        if ( g == null || r == null || w == null ) {
           throw new IllegalArgumentException("g,r,w may not be null");
        }
        green = g;
        red = r;
        white = w;
    }


    /**
     * ColorPolynomial summation. 
     * @param S ColorPolynomial.
     * @return this+S.
     */
    public ColorPolynomial<C> sum( ColorPolynomial<C> S ) {
        GenPolynomial<C> g, r, w;
        g = green.sum( S.green );
        r = red.sum( S.red );
        w = white.sum( S.white );
        //assert g.trailingExpvector() > r.leadingExpvector();
        //assert r.trailingExpvector() > w.leadingExpvector();
        return new ColorPolynomial<C>(g,r,w);
    }


    /**
     * ColorPolynomial subtraction. 
     * @param S ColorPolynomial.
     * @return this-S.
     */
    public ColorPolynomial<C> subtract( ColorPolynomial<C> S ) {
        GenPolynomial<C> g, r, w;
        g = green.subtract( S.green );
        r = red.subtract( S.red );
        w = white.subtract( S.white );
        //assert g.trailingExpvector() > r.leadingExpvector();
        //assert r.trailingExpvector() > w.leadingExpvector();
        return new ColorPolynomial<C>(g,r,w);
    }


    /**
     * ColorPolynomial multiplicatinon by monomial. 
     * @param e Coefficient.
     * @param s Expvector.
     * @return this * (c t).
     */
    public ColorPolynomial<C> multiply( C s, ExpVector e ) {
        GenPolynomial<C> g, r, w;
        g = green.multiply( s, e );
        r = red.multiply( s, e );
        w = white.multiply( s, e );
        return new ColorPolynomial<C>(g,r,w);
    }


}