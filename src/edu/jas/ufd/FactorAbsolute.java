/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;

import edu.jas.structure.GcdRingElem;


/**
 * Absolute factorization algorithms.
 * Factorization over algebraicaly closed fields.
 * @author Heinz Kredel
 */

public class FactorAbsolute<C extends GcdRingElem<C>> extends FactorAbstract<C> {


    private static final Logger logger = Logger.getLogger(FactorAbsolute.class);


    private final boolean debug = true || logger.isInfoEnabled();


    /**
     * Factorization engine for base coefficients.
     */
    protected final FactorAbstract<C> factorCoeff;


    /**
     * No argument constructor. <b>Note:</b> can't use this constructor.
     */
    protected FactorAbsolute() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor.
     * @param factorCoeff factorization engine for base coefficients.
     */
    public FactorAbsolute(FactorAbstract<C> factorCoeff) {
        this.factorCoeff = factorCoeff;
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial<AlgebraicNumber<C>>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<C>> baseFactorsSquarefree(GenPolynomial<C> P) {
         throw new RuntimeException("method not implemented");
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial<AlgebraicNumber<C>>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    // @Override
    public List<GenPolynomial<AlgebraicNumber<C>>> baseFactorsSquarefreeAbsolute(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<AlgebraicNumber<C>>> factors = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials");
        }

        //System.out.println("\nP = " + P);
        List<GenPolynomial<C>> facs = factorCoeff.baseFactorsSquarefree(P);

        if (!factorCoeff.isFactorization(P, facs)) {
             throw new RuntimeException("isFactorization = false");
        }
        System.out.println("\nfacs = " + facs); // Q[X]

        for ( GenPolynomial<C> p : facs ) {
            List<GenPolynomial<AlgebraicNumber<C>>> afacs = baseFactorsAbsolute(p);
            factors.addAll( afacs );
        }
        System.out.println("factors = " + factors);
        return factors;
    }


    /**
     * GenPolynomial base absolute factorization of a irreducible polynomial.
     * @param P irreducible! GenPolynomial<C>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i in K(alpha)[x] for suitable alpha
     * and p_i irreducible over L[x], 
     * where K \subset K(alpha) \subset L is an algebraically closed field over K.
     */
    public List<GenPolynomial<AlgebraicNumber<C>>> baseFactorsAbsolute(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<AlgebraicNumber<C>>> factors = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials");
        }
        // setup field extension K(alpha)
        String[] vars = new String[] { "z_"+"17" /*"pfac.getVars().hashCode()*/ };
        AlgebraicNumberRing<C> afac = new AlgebraicNumberRing<C>(P,true); // since irreducible
        GenPolynomialRing<AlgebraicNumber<C>> pafac 
            = new GenPolynomialRing<AlgebraicNumber<C>>(afac, P.ring.nvar,P.ring.tord,vars);
        // convert to K(alpha)
        GenPolynomial<AlgebraicNumber<C>> Pa 
            = PolyUtil.<C> convertToAlgebraicCoefficients(pafac, P);
        if ( Pa.degree(0) <= 1 ) {
            factors.add(Pa);
            return factors;
        }
        System.out.println("Pa = " + Pa);
        // factor over K(alpha)
        FactorAbstract<AlgebraicNumber<C>> engine = FactorFactory.<C>getImplementation(afac);
        factors = engine.baseFactorsSquarefree( Pa );
        System.out.println("factors = " + factors);
        return factors;
    }

}
