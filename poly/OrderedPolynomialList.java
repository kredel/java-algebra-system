/*
 * $Id$
 */

package edu.jas.poly;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.TreeMap;
import java.util.Comparator;

import java.io.Serializable;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

import edu.jas.arith.BigRational;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomialRing;


/**
 * list of polynomials
 * mainly for storage and printing/toString and sorting
 * @author Heinz Kredel
 */

public class OrderedPolynomialList<C extends RingElem<C> > 
             extends PolynomialList<C> {


    public OrderedPolynomialList( GenPolynomialRing< C > r,
                                  List< GenPolynomial< C > > l ) {
        super(r, sort(r,l) );
    }


    /**
     * equals from Object.
     */

    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object p) {
        if ( ! super.equals(p) ) {
            return false;
        }
        OrderedPolynomialList< C > pl = null;
        try {
            pl = (OrderedPolynomialList< C >)p;
        } catch (ClassCastException ignored) {
        }
        if ( pl == null ) {
           return false;
        }
        // compare sorted lists
        // done already in super.equals()
        return true;
    }


    /**
     * Sort a list of polynomials with respect to the ascending order 
     * of the leading Exponent vectors. 
     * The term order is taken from the ring.
     */

    public static <C extends RingElem<C> >
    List<GenPolynomial<C>> sort( GenPolynomialRing< C > r,
                                 List<GenPolynomial<C>> l ) {
        if ( l == null ) {
            return l;
        }
        if ( l.size() <= 1 ) { // nothing to sort
            return l;
        }
        final Comparator<ExpVector> evc = r.tord.getAscendComparator();
        Comparator<GenPolynomial<C>> cmp = new Comparator<GenPolynomial<C>>() {
                public int compare(GenPolynomial<C> p1, 
                                   GenPolynomial<C> p2) {
                       ExpVector e1 = p1.leadingExpVector();
                       ExpVector e2 = p2.leadingExpVector();
                       if ( e1 == null ) {
                          return -1; // dont care
                       }
                       if ( e2 == null ) {
                          return 1; // dont care
                       }
                       if ( e1.length() != e2.length() ) {
                          if ( e1.length() > e2.length() ) {
                             return 1; // dont care
                          } else {
                             return -1; // dont care
                          }
                       }
                       return evc.compare(e1,e2);
                }
            };
        GenPolynomial<C>[] s = null;
        try {
            s = new GenPolynomial[ l.size() ]; //<C>
            //System.out.println("s.length = " + s.length );
            //s = l.toArray(s);
            for ( int i = 0; i < l.size(); i++ ) {
                s[i] = l.get(i);
            }
            Arrays.<GenPolynomial<C>>sort( s, cmp );
            return new ArrayList<GenPolynomial<C>>( 
                            Arrays.<GenPolynomial<C>>asList(s) );
        } catch(ClassCastException ok) {
            System.out.println("Warning: polynomials not sorted");
        }
        return l; // unsorted
    }

}
