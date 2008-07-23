/*
 * $Id$
 */

package edu.jas.application;

import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;


import edu.jas.structure.GcdRingElem;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ExpVector;


/**
 * Condition. 
 * A pair of lists of polynomials to be considered zero respectively non-zero.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */
public class Condition<C extends GcdRingElem<C> > 
             implements Serializable /*, Comparable<Condition>*/ {


    /**
     * Colors.
     */
    public static enum Color { GREEN, RED, WHITE };


    /**
     * Data structure for condition zero.
     */
    public final Ideal<C> zero;


    /**
     * Data structure for condition non-zero.
     */
    public final List<GenPolynomial<C>> nonZero;


    /**
     * Condition constructor.
     * Constructs an empty condition.
     * @param ring polynomial ring factory for coefficients.
     */
    public Condition(GenPolynomialRing<C> ring) {
        this( new Ideal<C>( ring, new ArrayList<GenPolynomial<C>>() ) );
        if ( ring == null ) {
           throw new RuntimeException("only for non null rings");
        }
    }


    /**
     * Condition constructor.
     * @param z an ideal of zero polynomials.
     * @param nz a list of non-zero polynomials.
     */
    public Condition(Ideal<C> z, List<GenPolynomial<C>> nz) {
        if ( z == null || nz == null ) {
            throw new RuntimeException("only for non null condition parts");
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
                      + ", 0 != " + nonZero + " ]";
    }


    /**
     * equals.
     * @param ob an Object.
     * @return true if this is equal to o, else false.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object ob) {
        Condition<C> c = null;
        try {
            c = (Condition<C>) ob;
        } catch (ClassCastException e) {
            return false;
        }
        // if ( ! zero.getList().equals( c.zero.getList() ) ) {
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
     * Extend condition with zero polynomial.
     * @param z a polynomial to be treated as zero.
     */
    public Condition<C> extendZero(GenPolynomial<C> z) {
       z = zero.engine.squarefreePart( z ); // leads to errors in nonZero? -no more
       Ideal<C> idz = zero.sum( z );
       List<GenPolynomial<C>> list = idz.normalform( nonZero );
       if ( list.size() != nonZero.size() ) { // contradiction
          System.out.println("zero    = " + zero.getList());
          System.out.println("z       = " + z);
          System.out.println("idz     = " + idz.getList());
          System.out.println("list    = " + list);
          System.out.println("nonZero = " + nonZero);
          return null;
       }
       List<GenPolynomial<C>> L = new ArrayList<GenPolynomial<C>>( list.size() );
       for ( GenPolynomial<C> h : list ) {
           if ( h != null && !h.isZERO() ) {
              GenPolynomial<C> r = h.monic();
              L.add( r );
           }
       }
       return new Condition<C>( idz, L );
    }


    /**
     * Extend condition with non-zero polynomial.
     * @param nz a polynomial to be treated as non-zero.
     */
    public Condition<C> extendNonZero(GenPolynomial<C> nz) {
        GenPolynomial<C> n = zero.normalform( nz ).monic();
        if ( n == null || n.isZERO() ) {
           return this;
        } 
        GenPolynomial<C> nq = zero.engine.squarefreePart( n );
        if ( nq.equals(n) ) {
           List<GenPolynomial<C>> list = addNonZero( n );
           return new Condition<C>( zero, list );
        }
        System.out.println("squarefree...    " + nz );
        System.out.println("squarefree... of " + n  );
        System.out.println("squarefreePart = " + nq );
        GenPolynomial<C> q = n.divide(nq);
        List<GenPolynomial<C>> list = addNonZero( nq );
        Condition<C> nc = new Condition<C>( zero, list );
        list = nc.addNonZero( q );
        return new Condition<C>( zero, list );
    }


    /**
     * Determine color of polynomial.
     * @param c polynomial to be colored.
     */
    public Color color(GenPolynomial<C> c) {
        c = zero.normalform(c).monic();
        if ( zero.contains( c ) ) { 
           //System.out.println("c in id = " + c);
           return Color.GREEN;
        } 
        if ( c.isConstant() ) {
           //System.out.println("c constant " + c);
           return Color.RED;
        }
        // if ( nonZero.contains( c ) ) {
        if ( isNonZero( c ) ) {
           //System.out.println("c in nonzero " + c);
           return Color.RED;
        }
        return Color.WHITE;
    }


    /**
     * Test if a polynomial is contained in nonZero.
     * NonZero is treated as multiplicative set.
     * @param c polynomial searched in nonZero.
     */
    public boolean isNonZero(GenPolynomial<C> c) {
        if ( c == null || c.isZERO() ) { // do not look into zero list
           return false;
        }
        if ( nonZero == null || nonZero.size() == 0 ) {
           return false;
        }
        for ( GenPolynomial<C> n : nonZero ) {
            GenPolynomial<C> q;
            GenPolynomial<C> r;
            do {
                GenPolynomial<C>[] qr = c.divideAndRemainder( n );
                q = qr[0];
                r = qr[1];
                if ( r != null && ! r.isZERO() ) {
                    continue;
                }
                if ( q != null && q.isConstant() ) {
                    return true;
                }
                c = q;
            } while ( r.isZERO() && !c.isConstant() );
        }
        return false;
    }


    /**
     * Add polynomial to nonZero.
     * NonZero is treated as multiplicative set.
     * @param c polynomial to bee added to nonZero.
     */
    public List<GenPolynomial<C>> addNonZero(GenPolynomial<C> c) {
        if ( c == null || c.isZERO() ) { // do not look into zero list
           return nonZero;
        }
        List<GenPolynomial<C>> list;
        if ( nonZero == null ) { // cannot happen
           list = new ArrayList<GenPolynomial<C>>();
           list.add( c );
           return list;
        }
        list = new ArrayList<GenPolynomial<C>>( nonZero );
        if ( nonZero.size() == 0 ) {
           System.out.println("added to empty nonzero = " + c);
           list.add( c );
           return list;
        }
        for ( GenPolynomial<C> n : nonZero ) {
            GenPolynomial<C> q;
            GenPolynomial<C> r;
            do {
                GenPolynomial<C>[] qr = c.divideAndRemainder( n );
                q = qr[0];
                r = qr[1];
                if ( r != null && ! r.isZERO() ) {
                    continue;
                }
                if ( q != null && q.isConstant() ) {
                    return list;
                }
                c = q;
            } while ( r.isZERO() && !c.isConstant() );
        }
        System.out.println("added to nonzero = " + c);
        list.add( c );
        return list;
    }


    /**
     * Determine polynomial.
     * If this condition does not determine the polynomial, then 
     * a run-time exception is thrown.
     * @param A polynomial.
     * @return new determined colored polynomial.
     */
    public ColorPolynomial<C> determine( GenPolynomial<GenPolynomial<C>> A ) {  
        ColorPolynomial<C> cp = null;
        if ( A == null ) {
           return cp;
        }
        GenPolynomial<GenPolynomial<C>> zero = A.ring.getZERO();
        GenPolynomial<GenPolynomial<C>> green = zero;
        GenPolynomial<GenPolynomial<C>> red = zero;
        GenPolynomial<GenPolynomial<C>> white = zero;
        if ( A.isZERO() ) {
           cp = new ColorPolynomial<C>(green,red,white); 
           return cp;
        }
        GenPolynomial<GenPolynomial<C>> Ap = A;
        GenPolynomial<GenPolynomial<C>> Bp;
        while( !Ap.isZERO() ) {
            Map.Entry<ExpVector,GenPolynomial<C>> m = Ap.leadingMonomial();
            ExpVector e = m.getKey();
            GenPolynomial<C> c = m.getValue();
            Bp = Ap.reductum();
            //System.out.println( "color(" + c + ") = " + color(c) );
            switch ( color( c ) ) {
            case GREEN:
               green = green.sum(c,e);
               Ap = Bp;
               continue;
            case RED:
               red = red.sum(c,e);
               white = Bp;
               return new ColorPolynomial<C>(green,red,white); 
               // since break is not possible
            default: 
               System.out.println("error cond = " + this);
               throw new RuntimeException("error, c is white = " + c);
            }
        }
        cp = new ColorPolynomial<C>(green,red,white); 
        //System.out.println("determined = " + cp);
        return cp;
    }


    /**
     * Re determine colored polynomial.
     * @param s colored polynomial.
     * @return determined colored polynomial wrt. this.conditions.
     */
    public ColorPolynomial<C> reDetermine( ColorPolynomial<C> s ) {
        return determine( s.getPolynomial() );
//         Ideal<C> id = condition.zero;
//         //if ( id.isONE() ) {
//         //   return s;
//         //}
//         GenPolynomial<GenPolynomial<C>> green = s.green;
//         GenPolynomial<GenPolynomial<C>> red = s.red.clone();
//         GenPolynomial<GenPolynomial<C>> white = s.white.clone();
//         Iterator<Monomial<GenPolynomial<C>>> ri = red.monomialIterator();
//         while ( ri.hasNext() ) {
//             Monomial<GenPolynomial<C>> m = ri.next();
//             if ( m.c.isConstant() ) { // red
//                 break;
//             }
//             //if ( id.getList().contains( m.c ) ) { // green
//             if ( id.contains( m.c ) ) { // green
//                 ri.remove();
//                 green = green.sum( m.c, m.e );
//             } else if ( condition.nonZero.contains( m.c ) ) { // red
//                 break;
//             } else { // white
//                 white = white.sum( red );
//                 red = red.ring.getZERO();
//                 break;
//             }
//         }
//         if ( !red.isZERO() ) {
//            return new ColorPolynomial<C>( green, red, white );
//         }
//         // now red == 0
//         //white = s.white.clone();
//         Iterator<Monomial<GenPolynomial<C>>> wi = white.monomialIterator();
//         while ( wi.hasNext() ) {
//             Monomial<GenPolynomial<C>> m = wi.next();
//             if ( m.c.isConstant() ) { // red
//                 wi.remove();
//                 red = red.sum( m.c, m.e );
//                 break;
//             }
//             //if ( id.getList().contains( m.c ) ) { // green
//             if ( id.contains( m.c ) ) { // green
//                 wi.remove();
//                 green = green.sum( m.c, m.e );
//             } else if ( condition.nonZero.contains( m.c ) ) { // red
//                 wi.remove();
//                 red = red.sum( m.c, m.e );
//                 break;
//             } else { // white
//                 break;
//             }
//         }
//         return new ColorPolynomial<C>( green, red, white );
    }


    /**
     * Determine list of polynomials.
     * If this condition does not determine all polynomials, then 
     * a run-time exception is thrown.
     * The returned list does not contain polynomials with all green terms.
     * @param L list of polynomial.
     * @return new determined list of colored polynomials.
     */
    public List<ColorPolynomial<C>> determine( List<GenPolynomial<GenPolynomial<C>>> L ) {
        List<ColorPolynomial<C>> cl = null;
        if ( L == null ) {
           return cl;
        }
        cl = new ArrayList<ColorPolynomial<C>>( L.size() );
        for ( GenPolynomial<GenPolynomial<C>> A : L ) {
            ColorPolynomial<C> c = determine( A );
            if ( c != null && ! c.isZERO() ) {
               cl.add( c );
            }
        }
        return cl;
    }


}
