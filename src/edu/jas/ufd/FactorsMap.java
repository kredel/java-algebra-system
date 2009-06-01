/*
 * $Id$
 */

package edu.jas.ufd;


import java.io.Serializable;
//import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
//import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;


/**
 * Container for the factors of a eventually non-squarefree factorization.
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class FactorsMap<C extends GcdRingElem<C>> implements Serializable {


    /**
     * Original polynomial to be factored with coefficients from C.
     */
    public final GenPolynomial<C> poly;


    /**
     * List of factors with coefficients from C.
     */
    public final SortedMap<GenPolynomial<C>,Long> factors;


    /**
     * List of factors with coefficients from  AlgebraicNumberRings.
     */
    public final SortedMap<Factors<C>,Long> afactors;


    /**
     * Constructor.
     * @param p given GenPolynomial over C.
     * @param map irreducible factors of p with coefficients from C.
     */
    public FactorsMap(GenPolynomial<C> p, SortedMap<GenPolynomial<C>,Long> map) {
        this(p,map,null);
    }


    /**
     * Constructor.
     * @param p given GenPolynomial over C.
     * @param map irreducible factors of p with coefficients from C.
     * @param amap irreducible factors of p with coefficients from an algebraic number field.
     */
    public FactorsMap(GenPolynomial<C> p, SortedMap<GenPolynomial<C>,Long> map, 
                      SortedMap<Factors<C>,Long> amap) {
        poly = p;
        factors = map;
        afactors = amap;
    }


    /** Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( poly.toString() );
        sb.append( " =\n" );
        boolean first = true;
        for ( GenPolynomial<C> p : factors.keySet() ) {
            if ( first ) {
                first = false;
            } else {
                sb.append(",\n ");
            }
            sb.append(p.toString());
            long e = factors.get(p);
            if ( e > 1 ) {
                sb.append("**"+e);
            }
        }
        if ( afactors == null ) {
            return sb.toString();
        }
        for ( Factors<C> f : afactors.keySet() ) {
            if ( first ) {
                first = false;
            } else {
                sb.append(",\n ");
            }
            sb.append(f.toString());
            long e = afactors.get(f);
            if ( e > 1 ) {
                sb.append("**"+e);
            }
        }
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
        sb.append( " =\n" );
        boolean first = true;
        for ( GenPolynomial<C> p : factors.keySet() ) {
            if ( first ) {
                first = false;
            } else {
                sb.append("\n * ");
            }
            sb.append(p.toScript());
            long e = factors.get(p);
            if ( e > 1 ) {
                sb.append("**"+e);
            }
        }
        if ( afactors == null ) {
            return sb.toString();
        }
        for ( Factors<C> f : afactors.keySet() ) {
            if ( first ) {
                first = false;
            } else {
                sb.append("\n * ");
            }
            sb.append(f.toScript());
            long e = afactors.get(f);
            if ( e > 1 ) {
                sb.append("**"+e);
            }
        }
        return sb.toString();
    }

}
