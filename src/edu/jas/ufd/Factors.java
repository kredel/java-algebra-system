/*
 * $Id: $
 */

package edu.jas.ufd;


import java.io.Serializable;
//import java.util.ArrayList;
import java.util.List;
//import java.util.SortedMap;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
//import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;


/**
 * Container for the factors of absolute factorization.
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class Factors<C extends GcdRingElem<C>> implements Comparable<Factors<C>>, Serializable {


    /**
     * Original polynomial to be factored with coefficients from C.
     */
    public final GenPolynomial<C> poly;


    /**
     * Algebraic field extension over C.
     * Should be null, if p is absolutely irreducible.
     */
    public final AlgebraicNumberRing<C> afac;


    /**
     * Original polynomial to be factored with coefficients from AlgebraicNumberRing<C>.
     * Should be null, if p is absolutely irreducible.
     */
    public final GenPolynomial<AlgebraicNumber<C>> apoly;


    /**
     * List of factors with coefficients from AlgebraicNumberRing<C>.
     * Should be null, if p is absolutely irreducible.
     */
    public final List<GenPolynomial<AlgebraicNumber<C>>> afactors;


    /**
     * Constructor.
     * @param p absolute irreducible GenPolynomial.
     */
    public Factors(GenPolynomial<C> p) {
        this(p,null,null,null);
    }


    /**
     * Constructor.
     * @param p irreducible GenPolynomial over C.
     * @param af algebraic extension field of C where p has factors from afact.
     * @param ap GenPolynomial p represented with coefficients from af.
     * @param afact absolute irreducible factors of p with coefficients from af.
     */
    public Factors(GenPolynomial<C> p, AlgebraicNumberRing<C> af, 
                   GenPolynomial<AlgebraicNumber<C>> ap, 
                   List<GenPolynomial<AlgebraicNumber<C>>> afact) {
        poly = p;
        afac = af;
        apoly = ap;
        afactors = afact;
    }


    /** Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( poly.toString() );
        if ( afac == null ) {
            return sb.toString();
        }
        sb.append( " = " );
        boolean first = true;
        for ( GenPolynomial<AlgebraicNumber<C>> ap : afactors ) {
            if ( first ) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(ap.toString());
        }
        sb.append(" over "+ afac.toString());
        return sb.toString();
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this container.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    public String toScript() {
        // Python case
        StringBuffer sb = new StringBuffer();
        sb.append( poly.toScript() );
        if ( afac == null ) {
            return sb.toString();
        }
        sb.append( " =\n" );
        boolean first = true;
        for ( GenPolynomial<AlgebraicNumber<C>> ap : afactors ) {
            if ( first ) {
                first = false;
            } else {
                sb.append("\n * ");
            }
            sb.append("( " + ap.toScript() + " )");
        }
        sb.append("\n over "+ afac.toScript());
        return sb.toString();
    }


    /** Hash code for this Factors.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h;
       h = poly.hashCode();
       if ( afac == null ) {
           return h;
       }
       h = ( h << 27 );
       h += afac.hashCode();
       return h;
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals( Object B ) { 
       if ( ! ( B instanceof GenPolynomial ) ) {
          return false;
       }
       Factors<C> a = null;
       try {
           a = (Factors<C>) B;
       } catch (ClassCastException ignored) {
       }
       if ( a == null ) {
           return false;
       }
       return this.compareTo(a) == 0;
    }


    /** Comparison.  
     * @param facs factors container.
     * @return sign(this.poly-facs.poly) lexicographic &gt; sign(afac.modul-facs.afac.modul).
     */
    public int compareTo(Factors<C> facs) { 
        int s = poly.compareTo(facs.poly);        
        if ( s != 0 ) {
            return s; 
        }
        if ( afac == null ) {
            return -1;
        }
        if ( facs.afac == null) {
            return +1;
        }
        return afac.modul.compareTo(facs.afac.modul);
    }

}
