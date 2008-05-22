
/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
//import java.util.Iterator;
import java.util.Collection;
import java.util.Comparator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
//import edu.jas.structure.NotInvertibleException;

//import edu.jas.kern.PrettyPrint;
//import edu.jas.kern.PreemptingException;

//import edu.jas.poly.ExpVector;
//import edu.jas.poly.GenPolynomialRing;


/**
 * Colored Polynomials implementing RingElem.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ColorPolynomial<C extends RingElem<C> > 
             /*implements RingElem< ColorPolynomial<C> >*/ {


    /** The part with green (= zero) terms and coefficients.
     */
    public final GenPolynomial<GenPolynomial<C>> green;


    /** The part with red (= non zero) terms and coefficients.
     */
    public final GenPolynomial<GenPolynomial<C>> red;


    /** The part with white (= unknown color) terms and coefficients.
     */
    public final GenPolynomial<GenPolynomial<C>> white;


    /** The constructor creates a colored polynomial from
     * the colored parts.
     * @param g green colored terms and coefficients.
     * @param r red colored terms and coefficients.
     * @param w white colored terms and coefficients.
     */
    public ColorPolynomial(GenPolynomial<GenPolynomial<C>> g, 
                           GenPolynomial<GenPolynomial<C>> r, 
                           GenPolynomial<GenPolynomial<C>> w) {
        if ( g == null || r == null || w == null ) {
           throw new IllegalArgumentException("g,r,w may not be null");
        }
        green = g;
        red = r;
        white = w;
    }


    /**
     * String representation of GenPolynomial.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append( ":green: " );
        s.append( green.toString() );
        s.append( " :red: " );
        s.append( red.toString() );
        s.append( " :white: " );
        s.append( white.toString() );
        return s.toString();
    }


    /**
     * Is this polynomial ZERO.
     * return true, if there are only green terms, else false.
     */
    public boolean isZERO() {
        return ( red.isZERO() && white.isZERO() );
    }


    /**
     * Is this polynomial determined.
     * return true, if there are nonzero red terms, else false.
     */
    public boolean isDetermined() {
        return ( !red.isZERO() );
    }


    /**
     * Check ordering invariants.
     * TT(green) > LT(red) and TT(red) > LT(green).
     * return true, if all ordering invariants are met, else false.
     */
    public boolean checkInvariant() {
        boolean t = true;
        ExpVector ttg, ltr, ttr, ltw;
        Comparator<ExpVector> cmp;
        if ( green.isZERO() && red.isZERO() && white.isZERO() ) {
           return true;
        }
        if ( green.isZERO() && red.isZERO() ) {
           return true;
        } 
        if ( red.isZERO() && white.isZERO() ) {
           return true;
        } 

        if ( !green.isZERO() && !red.isZERO() ) {
           ttg = green.trailingExpVector();
           ltr = red.leadingExpVector();
           cmp = green.ring.tord.getDescendComparator();
           t = t && ( cmp.compare(ttg,ltr) < 0 );
        }
        if ( !red.isZERO() && !white.isZERO() ) {
           ttr = red.trailingExpVector();
           ltw = white.leadingExpVector();
           cmp = white.ring.tord.getDescendComparator();
           t = t && ( cmp.compare(ttr,ltw) < 0 );
        }
        if ( red.isZERO() && !green.isZERO() && !white.isZERO() ) {
           ttg = green.trailingExpVector();
           ltw = white.leadingExpVector();
           cmp = white.ring.tord.getDescendComparator();
           t = t && ( cmp.compare(ttg,ltw) < 0 );
        }
        return t;
    }


    /**
     * Get zero condition on coefficients. 
     * @return green coefficients.
     */
    public List<GenPolynomial<C>> getConditionZero() {
        Collection<GenPolynomial<C>> c = green.getMap().values();
        return new ArrayList<GenPolynomial<C>>( c );
    }


    /**
     * Get non zero condition on coefficients. 
     * @return red coefficients.
     */
    public List<GenPolynomial<C>> getConditionNonZero() {
        Collection<GenPolynomial<C>> c = red.getMap().values();
        return new ArrayList<GenPolynomial<C>>( c );
    }


    /**
     * Get full polynomial. 
     * @return sum of al parts.
     */
    public GenPolynomial<GenPolynomial<C>> getPolynomial() {
        GenPolynomial<GenPolynomial<C>> f = green.sum(red).sum(white);
        int s = green.length() + red.length() + white.length();
        int t = f.length();
        if ( t != s ) {
           throw new RuntimeException("illegal coloring state " + s + " != " + t );
        }
        return f;
    }


    /**
     * ColorPolynomial summation. 
     * @param S ColorPolynomial.
     * @return this+S.
     */
    public ColorPolynomial<C> sum( ColorPolynomial<C> S ) {
        GenPolynomial<GenPolynomial<C>> g, r, w;
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
        GenPolynomial<GenPolynomial<C>> g, r, w;
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
    public ColorPolynomial<C> multiply( GenPolynomial<C> s, ExpVector e ) {
        GenPolynomial<GenPolynomial<C>> g, r, w;
        g = green.multiply( s, e );
        r = red.multiply( s, e );
        w = white.multiply( s, e );
        return new ColorPolynomial<C>(g,r,w);
    }


}