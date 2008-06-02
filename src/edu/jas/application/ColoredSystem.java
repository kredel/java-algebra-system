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


    public final Condition<C> condition;


    public final List<ColorPolynomial<C>> list;


    public final OrderedCPairlist<C> pairlist;


    /**
     * Constructor for a colored polynomial system.
     * @param cond a condition.
     * @param S a list of colored polynomials.
     */
    public ColoredSystem( Condition<C> cond,
                          List<ColorPolynomial<C>> S) {
        this(cond,S,null);
    }


    /**
     * Constructor for a colored polynomial system.
     * @param cond a condition.
     * @param S a list of colored polynomials.
     * @param pl a ordered pair list.
     */
    public ColoredSystem( Condition<C> cond,
                          List<ColorPolynomial<C>> S, 
                          OrderedCPairlist<C> pl ) {
        this.condition = cond;
        this.list = S;
        this.pairlist = pl;
    }


    /**
     * clone this colored polynomial system.
     * @return a clone of this.
     */
    public ColoredSystem<C> clone() {
        return new ColoredSystem<C>(condition,list,pairlist.clone());
    }


    /** Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("\nColoredSystem: \n");
        if ( list.size() > 0 ) {
           s.append("polynomial ring : " + list.get(0).green.ring + "\n");
        } else {
           s.append("parameter polynomial ring : " + condition.zero.list.ring + "\n");
        }
        s.append("conditions == 0 : " + getConditionZero() + "\n");
        s.append("conditions != 0 : " + getConditionNonZero() + "\n");
        s.append("colored polynomials:\n" + list + "\n");
        s.append("uncolored polynomials:\n" + getPolynomialList() + "\n");
        s.append("essential polynomials:\n" + getEssentialPolynomialList() + "\n");
        s.append("green coefficients:\n" + getGreenCoefficientsList() + "\n");
        if ( pairlist != null ) {
           s.append( pairlist.toString() + "\n" );
        }
        return s.toString();
    }


    /**
     * Get zero condition on coefficients. 
     * @return green coefficients.
     */
    public List<GenPolynomial<C>> getConditionZero() {
        List<GenPolynomial<C>> c = condition.zero.getList();
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
     * Get list of green coefficients of polynomials. 
     * @return list of all green coefficients of polynomials.
     */
    public List<GenPolynomial<C>> getGreenCoefficientsList() {
        List<GenPolynomial<C>> F
            = new ArrayList<GenPolynomial<C>>();
        for ( ColorPolynomial<C> s : list ) {
            F.addAll( s.green.getMap().values() );
        }
        List<GenPolynomial<C>> M = new ArrayList<GenPolynomial<C>>();
        for ( GenPolynomial<C> c : F ) {
            if ( c.isONE() ) {
               continue;
            }
            if ( !M.contains( c ) ) {
               M.add( c );
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
     * Get list of essential polynomials. 
     * @return list of all essential polynomials.
     */
    public List<GenPolynomial<GenPolynomial<C>>> getEssentialPolynomialList() {
        List<GenPolynomial<GenPolynomial<C>>> F
            = new ArrayList<GenPolynomial<GenPolynomial<C>>>();
        for ( ColorPolynomial<C> s : list ) {
            F.add( s.getEssentialPolynomial() );
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
        Ideal<C> id = condition.zero;
        List<GenPolynomial<C>> nz = condition.nonZero;
        for ( ColorPolynomial<C> s : list ) {
            if ( !s.checkInvariant() ) {
               System.out.println("notInvariant " + s);
               System.out.println("condition:   " + condition);
               return false;
            }
            for ( GenPolynomial<C> g : s.green.getMap().values() ) {
                if ( ! id.contains( g ) ) {
                   System.out.println("notGreen   " + g);
                   System.out.println("condition: " + condition);
                   System.out.println("colors:    " + s);
                   return false;
                }
            }
            for ( GenPolynomial<C> r : s.red.getMap().values() ) {
                if ( r.isConstant() ) {
                   continue;
                }
                if ( !nz.contains( r ) ) {
                   System.out.println("notRed     " + r);
                   System.out.println("condition: " + condition);
                   System.out.println("colors:    " + s);
                   return false;
                }
                if ( id.contains( r ) ) {
                   System.out.println("warning in consistent condition " + condition);
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
            if ( s.isZERO() ) {
               continue;
            }
            if ( !s.isDetermined() ) {
               System.out.println("notDetermined " + s);
               System.out.println("condition: " + condition);
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
        Ideal<C> id = condition.zero;
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
        if ( condition.zero.isONE() ) {
           return this;
        }
        List<ColorPolynomial<C>> Sn = new ArrayList<ColorPolynomial<C>>( list.size() );
        for ( ColorPolynomial<C> c : list ) {
            ColorPolynomial<C> a = reDetermine( c ); 
            //if ( !a.isZERO() ) {
            Sn.add( a ); // must also add zeros
            //}
        }
        return new ColoredSystem<C>(condition,Sn);
    }

}
