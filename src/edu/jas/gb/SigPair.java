/*
 * $Id$
 */

package edu.jas.gb;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;


/**
 * Serializable subclass to hold pairs of polynomials.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */
public class SigPair<C extends RingElem<C> > //extends AbstractSigPair<C>
             implements Comparable<SigPair<C>> {

    public final GenPolynomial<C> sigma;
    public final SigPoly<C> pi;
    public final SigPoly<C> pj;
    public final int i;
    public final int j;
    public final List<SigPoly<C>> Gs;


    /**
     * SigPair constructor.
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     */
    public SigPair(SigPoly<C> a, SigPoly<C> b, 
                int i, int j) {
        this(a,b,i,j,null);
    }


    /**
     * SigPair constructor.
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     * @param Gs list.
     */
    public SigPair(SigPoly<C> a, SigPoly<C> b, 
                List<SigPoly<C>> Gs) {
        this(a.poly.leadingExpVector().lcm(b.poly.leadingExpVector()).subtract(a.poly.leadingExpVector()),a,b,Gs);
    }


    /**
     * SigPair constructor.
     * @param a polynomial i.
     * @param b polynomial j.
     */
    public SigPair(SigPoly<C> a, SigPoly<C> b, 
                int i, int j, List<SigPoly<C>> Gs) {
        this(a.poly.leadingExpVector().lcm(b.poly.leadingExpVector()).subtract(a.poly.leadingExpVector()),a,b,i,j,Gs);
    }


    /**
     * SigPair constructor.
     * @param signature of pair.
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     */
    public SigPair(ExpVector lcm, SigPoly<C> a, SigPoly<C> b, 
		   int i, int j, List<SigPoly<C>> Gs) {
        this(a.poly.ring.valueOf(lcm),a,b,i,j,Gs);
    }


    /**
     * SigPair constructor.
     * @param signature of pair.
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     */
    public SigPair(GenPolynomial<C> sig, SigPoly<C> a, SigPoly<C> b, 
		   int i, int j, List<SigPoly<C>> Gs) {
        this.sigma = sig;
        pi = a;
        pj = b;
        this.i = i;        
        this.j = j;
        this.Gs = Gs;
    }


    /**
     * SigPair constructor.
     * @param signature of pair.
     * @param a polynomial i.
     * @param b polynomial j.
     */
    public SigPair(ExpVector lcm, SigPoly<C> a, SigPoly<C> b, 
		   List<SigPoly<C>> Gs) {
        this(a.poly.ring.valueOf(lcm),a,b,Gs);
    }


    /**
     * SigPair constructor.
     * @param signature of pair.
     * @param a polynomial i.
     * @param b polynomial j.
     */
    public SigPair(GenPolynomial<C> sig, SigPoly<C> a, SigPoly<C> b, 
		   List<SigPoly<C>> Gs) {
        this.sigma = sig;
        pi = a;
        pj = b;
        this.i = -1;        
        this.j = -1;
        this.Gs = Gs;
    }


    /**
     * getter for sigma
     */
    GenPolynomial<C> getSigma() {
        return sigma; 
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return "pair(" + sigma + " @ " + pi + "," + pj + ")";
    }


    /**
     * equals.
     * @param ob an Object.
     * @return true if this is equal to o, else false.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object ob) {
        if ( ! (ob instanceof SigPair) ) {
           return false;
           // throw new ClassCastException("SigPair "+n+" o "+o);
        }
        return 0 == compareTo( (SigPair)ob );
    }


    /**
     * compareTo used in TreeMap // not used at moment.
     * Comparison is based on the number of the pairs.
     * @param p a SigPair.
     * @return 1 if (this &lt; o), 0 if (this == o), -1 if (this &gt; o).
     */
    public int compareTo(SigPair<C> p) {
        return sigma.compareTo(p.sigma);
    }


    /**
     * Hash code for this SigPair.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (i << 16) + j;
    }

}
