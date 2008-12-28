
/*
 * $Id$
 */

package edu.jas.ufd;

import java.io.Serializable;

import java.util.Map;
import java.util.SortedMap;
import java.util.List;

import edu.jas.structure.Power;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.poly.GenPolynomial;


/**
 * Abstract factorization algorithm class.
 * @author Heinz Kredel
 */

public abstract class FactorAbstract<C extends GcdRingElem<C> > 
                      implements Factorization<C> {


    /**
     * GenPolynomial test if is irreducible.
     * @param P GenPolynomial<C>.
     * @return true if P is irreducible, else false.
     */
    public boolean isIrreducible( GenPolynomial<C> P ) {
        return ! isReducible( P );
    }


    /**
     * GenPolynomial test if a non trivial factorization exsists.
     * @param P GenPolynomial<C>.
     * @return true if P is reducible, else false.
     */
    public abstract boolean isReducible( GenPolynomial<C> P );


    /**
     * GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial<C>.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    public abstract List<GenPolynomial<C>> factorsSquarefree( GenPolynomial<C> P );


    /**
     * GenPolynomial factorization.
     * @param P GenPolynomial<C>.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i**e_i.
     */
    public abstract SortedMap<GenPolynomial<C>,Integer> factors( GenPolynomial<C> P );


    /**
     * GenPolynomial greatest squarefree divisor.
     * @param P GenPolynomial.
     * @return squarefree(P).
     */
    public GenPolynomial<C> squarefreePart( GenPolynomial<C> P ) {
        if ( P == null ) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        //GenPolynomialRing<C> pfac = P.ring;
        RingFactory<C> cfac = P.ring.coFac;
        GreatestCommonDivisor<C> engine = GCDFactory.<C> getProxy( cfac );
        return engine.squarefreePart(P);
    }


    /**
     * GenPolynomial squarefree factorization.
     * @param P GenPolynomial.
     * @return [e_1 -> p_1, ..., e_k -> p_k] with P = prod_{i=1,...,k} p_i**e_i. 
     */
    public Map<Integer,GenPolynomial<C>> squarefreeFactors( GenPolynomial<C> P ) {
        if ( P == null ) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        //GenPolynomialRing<C> pfac = P.ring;
        RingFactory<C> cfac = P.ring.coFac;
        GreatestCommonDivisor<C> engine = GCDFactory.<C> getProxy( cfac );
        return engine.squarefreeFactors(P);
    }


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial<C>.
     * @param F = [p_1,...,p_k].
     * @return true if P = prod_{i=1,...,r} p_i, else false.
     */
    public boolean isFactorization( GenPolynomial<C> P, List<GenPolynomial<C>> F ) {
        if ( P == null || F == null ) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        GenPolynomial<C> t = P.ring.getONE();
        for ( GenPolynomial<C> f: F ) {
            t = t.multiply( f );
        }
        return P.equals(t);
    }


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial<C>.
     * @param F = [p_1 -> e_1, ..., p_k -> e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i , else false.
     */
    public boolean isFactorization( GenPolynomial<C> P, SortedMap<GenPolynomial<C>,Integer>  F ) {
        if ( P == null || F == null ) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        GenPolynomial<C> t = P.ring.getONE();
        for ( GenPolynomial<C> f: F.keySet() ) {
            int e = F.get(f);
            GenPolynomial<C> g = Power.<GenPolynomial<C>> positivePower( f, e );
            t = t.multiply( g );
        }
        return P.equals(t);
    }

}
