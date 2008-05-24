/*
 * $Id$
 */

package edu.jas.application;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;

import edu.jas.poly.Monomial;
import edu.jas.poly.ColorPolynomial;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyIterator;
import edu.jas.poly.GenPolynomialRing;

import edu.jas.application.Ideal;



/**
  * Container for a condition and corresponding colored polynomial system.
  * @param <C> coefficient type
  * @param F an ideal base.
  */
public class ColoredSystem<C extends GcdRingElem<C>> {


    public final Ideal<C> conditions;


    public final List<ColorPolynomial<C>> list;


    public final OrderedCPairlist<C> pairlist;


    /**
     * Constructor for a colored polynomial system.
     * @param id an ideal base.
     * @param S a list of colored polynomials.
     */
    public ColoredSystem( Ideal<C> id,
                          List<ColorPolynomial<C>> S) {
        this(id,S,null);
    }


    /**
     * Constructor for a colored polynomial system.
     * @param id an ideal base.
     * @param S a list of colored polynomials.
     * @param pl a ordered pair list.
     */
    public ColoredSystem( Ideal<C> id,
                          List<ColorPolynomial<C>> S, 
                          OrderedCPairlist<C> pl ) {
        this.conditions = id;
        this.list = S;
        this.pairlist = pl;
    }


    /** Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("ColoredSystem: \n\n");
        s.append("conditions: " + conditions + "\n\n");
        s.append("system: " + list + "\n\n");
        if ( pairlist != null ) {
           s.append(pairlist.toString() + "\n");
        }
        return s.toString();
    }


    /**
     * Get zero condition on coefficients. 
     * @return green coefficients.
     */
    public List<GenPolynomial<C>> getConditionZero() {
        List<GenPolynomial<C>> c = conditions.getList();
        return new ArrayList<GenPolynomial<C>>( c );
    }


    /**
     * Get non zero condition on coefficients. 
     * @return red coefficients.
     */
    public List<GenPolynomial<C>> getConditionNonZero() {
        List<GenPolynomial<C>> N = new ArrayList<GenPolynomial<C>>();
        for ( ColorPolynomial<C> c : list ) {
            List<GenPolynomial<C>> r = c.getConditionNonZero();
            N.addAll( r );
        }
        List<GenPolynomial<C>> M = new ArrayList<GenPolynomial<C>>();
        for ( GenPolynomial<C> c : N ) {
            GenPolynomial<C> m = c.monic();
            if ( m.isONE() ) {
               continue;
            }
            if ( !M.contains( m ) ) {
               M.add( m );
            }
        }
        return M;
    }


    /**
     * Get list of full polynomials. 
     * @return list of all full polynomials.
     */
    public List<GenPolynomial<GenPolynomial<C>>> getPolynomialList() {
        List<GenPolynomial<GenPolynomial<C>>> F
            = new ArrayList<GenPolynomial<GenPolynomial<C>>>();
        for ( ColorPolynomial<C> s : list ) {
            F.add( s.getPolynomial() );
        }
        return F;
    }


    /**
     * Check invariants.
     * return true, if all invariants are met, else false.
     */
    public boolean checkInvariant() {
        if ( !isDetermined() ) {
           return false;
        }
        Ideal<C> id = conditions;
        for ( ColorPolynomial<C> s : list ) {
            if ( !s.checkInvariant() ) {
               System.out.println("notInvariant " + s);
               System.out.println("Ideal: " + id);
               return false;
            }
            for ( GenPolynomial<C> g : s.green.getMap().values() ) {
                if ( ! id.contains( g ) ) {
                   System.out.println("notGreen " + g);
                   System.out.println("Ideal: " + id);
                   System.out.println("colors: " + s);
                   return false;
                }
            }
            for ( GenPolynomial<C> r : s.red.getMap().values() ) {
                if ( id.contains( r ) ) {
                   System.out.println("notRed " + r);
                   System.out.println("Ideal: " + id);
                   System.out.println("colors: " + s);
                   return false;
                }
            }
        }
        return true;
    }


    /**
     * Is this colored system completely determined.
     * return true, if each ColorPolynomial is determined, else false.
     */
    public boolean isDetermined() {
        for ( ColorPolynomial<C> s : list ) {
            if ( !s.isDetermined() ) {
               System.out.println("notDetermined " + s);
               System.out.println("Ideal: " + conditions);
               return false;
            }
        }
        return true;
    }


    /**
     * Re determine colored polynomial.
     * @param s colored polynomial.
     * @return determined colored polynomial wrt. this.conditions.
     */
    public ColorPolynomial<C> reDetermine( ColorPolynomial<C> s ) {
        Ideal<C> id = conditions;
        if ( id.isONE() ) {
           return s;
        }
        GenPolynomial<GenPolynomial<C>> green = s.green;
        GenPolynomial<GenPolynomial<C>> red = s.red.clone();
        GenPolynomial<GenPolynomial<C>> white = s.white;
        Iterator<Monomial<GenPolynomial<C>>> ri = red.monomialIterator();
        while ( ri.hasNext() ) {
            Monomial<GenPolynomial<C>> m = ri.next();
            if ( !id.contains( m.c ) ) {
               break;
            }
            ri.remove();
            green = green.sum( m.c, m.e );
        }
        if ( !red.isZERO() ) {
           return new ColorPolynomial<C>( green, red, white );
        }
        // now red == 0
        white = s.white.clone();
        Iterator<Monomial<GenPolynomial<C>>> wi = white.monomialIterator();
        while ( wi.hasNext() ) {
            Monomial<GenPolynomial<C>> m = wi.next();
            wi.remove();
            if ( !id.contains( m.c ) ) {
               red = red.sum( m.c, m.e );
               break;
            }
            green = green.sum( m.c, m.e );
        }
        return new ColorPolynomial<C>( green, red, white );
    }


    /**
     * Re determine colorings of polynomials.
     * @return re determined colored polynomials wrt. this.conditions.
     */
    public ColoredSystem<C> reDetermine() {
        if ( conditions.isONE() ) {
           return this;
        }
        List<ColorPolynomial<C>> Sn = new ArrayList<ColorPolynomial<C>>( list.size() );
        for ( ColorPolynomial<C> c : list ) {
            ColorPolynomial<C> a = reDetermine( c ); 
            Sn.add( a );
        }
        return new ColoredSystem<C>(conditions,Sn);
    }

}
