/*
 * $Id$
 */

package edu.jas.module;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import edu.jas.structure.RingFactory;
import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;

import edu.jas.poly.PolynomialList;
import edu.jas.poly.OrderedPolynomialList;


/**
 * list of vectors of polynomials
 * mainly for storage and printing/toString and conversion
 * @author Heinz Kredel
 */

public class OrderedModuleList<C extends RingElem<C> > 
             extends ModuleList<C> {


    /**
     * Constructor.
     */
    public OrderedModuleList( GenPolynomialRing< C > r,
                              List<List<GenPolynomial<C>>> l ) {
        super( r, sort( r, ModuleList.padCols(r,l) ) );
    }


    /**
     * equals from Object.
     */

    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object m) {
        if ( ! super.equals(m) ) {
            return false;
        }
        OrderedModuleList<C> ml = null;
        try {
            ml = (OrderedModuleList<C>)m;
        } catch (ClassCastException ignored) {
        }
        if ( ml == null ) {
           return false;
        }
        // compare sorted lists
        // done already in super.equals()
        return true;
    }



    /**
     * Sort a list of vectors of polynomials with respect to the 
     * ascending order of the leading Exponent vectors of the 
     * first column. 
     * The term order is taken from the ring.
     */

    public static <C extends RingElem<C> >
           List<List<GenPolynomial<C>>> sort( GenPolynomialRing<C> r,
                                              List<List<GenPolynomial<C>>> l) {

        if ( l == null ) {
            return l;
        }
        if ( l.size() <= 1 ) { // nothing to sort
            return l;
        }
        final Comparator<ExpVector> evc = r.tord.getAscendComparator();
        Comparator<List<GenPolynomial<C>>> cmp 
              = new Comparator<List<GenPolynomial<C>>>() {
                public int compare(List<GenPolynomial<C>> l1, 
                                   List<GenPolynomial<C>> l2) {
                       int c = 0;
                       for ( int i = 0; i < l1.size(); i++ ) {
                           GenPolynomial<C> p1 = l1.get(i);
                           GenPolynomial<C> p2 = l2.get(i);
                           ExpVector e1 = p1.leadingExpVector();
                           ExpVector e2 = p2.leadingExpVector();
                           if ( e1 == null && e2 != null ) {
                               return -1; 
                           }
                           if ( e1 != null && e2 == null ) {
                               return 1; 
                           }
                           if ( e1 == null && e2 == null ) {
                               continue; 
                           }
                           if ( e1.length() != e2.length() ) {
                              if ( e1.length() > e2.length() ) {
                                 return 1; 
                              } else {
                                 return -1;
                              }
                           }
                           c = evc.compare(e1,e2);
                           if ( c != 0 ) {
                               return c;
                           }
                       }
                       return c;
                }
            };

        List<GenPolynomial<C>>[] s = null;
        try {
            s = new List[ l.size() ]; //<GenPolynomial<C>>
            //System.out.println("s.length = " + s.length );
            //s = l.toArray(s);
            for ( int i = 0; i < l.size(); i++ ) {
                s[i] = l.get(i);
            }
            Arrays.<List<GenPolynomial<C>>>sort( s, cmp );
            return new ArrayList<List<GenPolynomial<C>>>( 
                            Arrays.<List<GenPolynomial<C>>>asList(s) );
        } catch(ClassCastException ok) {
            System.out.println("Warning: polynomials not sorted");
        }
        return l; // unsorted
    }



    /**
     * get OrderedPolynomialList.
     * Embed module in a polynomial ring and sort polynomials. 
     */
    /*
    public OrderedPolynomialList getOrderedPolynomialList() {
        PolynomialList pl = super.getPolynomialList();
        return new OrderedPolynomialList(pl.coeff, pl.vars, pl.tord, 
                                         pl.list, pl.table);
    }
    */

    /**
     * get OrderedModuleList from PolynomialList.
     * Extract module from polynomial ring and sort generators. 
     */
    /*
    public static OrderedModuleList getOrderedModuleList(
                                       int i, 
                                       PolynomialList pl) {
        ModuleList ml = ModuleList.getModuleList(i,pl);
        return new OrderedModuleList(ml.coeff, ml.vars, ml.tord,
                                     ml.list, ml.table);
    }
    */

}
