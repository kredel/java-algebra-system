/*
 * $Id$
 */

package edu.jas.integrate;


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
import edu.jas.structure.RingFactory;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorAbsolute;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.PartialFraction;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GreatestCommonDivisorSubres;
import edu.jas.ufd.PolyUfdUtil;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Methods related to the Bernoulli algorithm for elementary
 * integration.  The denomiantor is factored into linear factors over
 * iterated algebraic extensions over the rational numbers.
 * 
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class ElementaryIntegrationBernoulli<C extends GcdRingElem<C>> extends ElementaryIntegration<C> {


    private static final Logger logger = Logger.getLogger(ElementaryIntegrationBernoulli.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public ElementaryIntegrationBernoulli(RingFactory<C> br) {
        super(br);
        if ( ! (irr instanceof FactorAbsolute)) {
            throw new IllegalArgumentException("no absolute factorization available for coefficient ring " + br);
        }
        irredLogPart = true; // force to true?
    }


    /**
     * Univariate GenPolynomial integration of the logaritmic part,
     * Bernoulli linear factorization algorithm.
     * @param A univariate GenPolynomial, deg(A) < deg(P).
     * @param P univariate squarefree or irreducible GenPolynomial. // gcd(A,P) == 1 automatic
     * @return logarithmic part container.
     */
    @Override
    public LogIntegral<C> integrateLogPart(GenPolynomial<C> A, GenPolynomial<C> P) {
        if (P == null || P.isZERO()) {
            throw new IllegalArgumentException("P == null or P == 0");
        }
        //System.out.println("\nP_base_algeb_part = " + P);
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException("only for univariate polynomials " + pfac);
        }
        // pfac.coFac.isField() by FactorAbsolute
        List<C> cfactors = new ArrayList<C>();
        List<GenPolynomial<C>> cdenom = new ArrayList<GenPolynomial<C>>();
        List<AlgebraicNumber<C>> afactors = new ArrayList<AlgebraicNumber<C>>();
        List<GenPolynomial<AlgebraicNumber<C>>> adenom = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();
        // P linear
        if (P.degree(0) <= 1) {
            cfactors.add(A.leadingBaseCoefficient());
            cdenom.add(P);
            return new LogIntegral<C>(A, P, cfactors, cdenom, afactors, adenom);
        }
        // complete factorization to linear factors
        PartialFraction<C> F = ((FactorAbsolute<C>)irr).baseAlgebraicPartialFraction(A, P);
        System.out.println("\npartial fraction " + F);

        return new LogIntegral<C>(A, P, F.cfactors, F.cdenom, F.afactors, F.adenom);
    }

}
