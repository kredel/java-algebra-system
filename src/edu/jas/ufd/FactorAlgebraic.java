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
import edu.jas.structure.GcdRingElem;


/**
 * Algebraic number coefficients factorization algorithms.
 * This class implements factorization methods for polynomials over algebraic numbers 
 * over rational numbers or over (prime) modular integers.
 * @author Heinz Kredel
 */

public class FactorAlgebraic<C extends GcdRingElem<C>> extends FactorAbstract<AlgebraicNumber<C>> {


    private static final Logger logger = Logger.getLogger(FactorAlgebraic.class);


    private final boolean debug = true || logger.isInfoEnabled();


    /**
     * Factorization engine for base coefficients.
     */
    protected final FactorAbstract<C> factorCoeff;


    /**
     * No argument constructor. <b>Note:</b> can't use this constructor.
     */
    protected FactorAlgebraic() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor.
     * @param factorCoeff factorization engine for base coefficients.
     */
    public FactorAlgebraic(FactorAbstract<C> factorCoeff) {
        this.factorCoeff = factorCoeff;
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree GenPolynomial<AlgebraicNumber<C>>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<AlgebraicNumber<C>>> baseFactorsSquarefree(GenPolynomial<AlgebraicNumber<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<AlgebraicNumber<C>>> factors = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<AlgebraicNumber<C>> pfac = P.ring; // Q(alpha)[x]
        if (pfac.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials");
        }
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) pfac.coFac;

        AlgebraicNumber<C> ldcf = P.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            P = P.monic();
            factors.add(pfac.getONE().multiply(ldcf));
        }
        //System.out.println("\nP = " + P);

        GreatestCommonDivisor<AlgebraicNumber<C>> aengine //= GCDFactory.<AlgebraicNumber<C>> getProxy(afac);
          = new GreatestCommonDivisorSimple<AlgebraicNumber<C>>( /*cfac.coFac*/ );

        // search squarefree norm
        long k = 0L;
        long ks = k;
        GenPolynomial<C> res = null;
        boolean sqf = false;
        while (!sqf) {
            if (k >= 6) {
                break;
            }
            if (k > 4) {
                k = -1;
                //break;
            }
            if (k < -4) {
                k = 17;
            }
            // compute norm with x -> ( y - k x )
            ks = k;
            res = PolyUfdUtil.<C> norm(P, ks);
            //System.out.println("res = " + res);
            if (res.isZERO() || res.isConstant()) {
                if (k < 0) {
                    k--;
                } else {
                    k++;
                }
                continue;
            }
            sqf = factorCoeff.isSquarefree(res);
            //System.out.println("sqf("+ks+") = " + sqf + "\n");
            //System.out.println("resfact = " + factorCoeff.baseFactors(res) + "\n");
            if (k < 0) {
                k--;
            } else {
                k++;
            }
        }
        // if Res is now squarefree, we can factor it, else must stop
        List<GenPolynomial<C>> nfacs;
        if (!sqf) {
            System.out.println("\nres = " + res); 
            System.out.println("sqf("+ks+") = " + sqf + "\n");
            throw new RuntimeException("resultant not squarefree after 10 tries");
        } 
        nfacs = factorCoeff.baseFactorsSquarefree(res);
        if (debug && !factorCoeff.isFactorization(res, nfacs)) {
            System.out.println("\nres = " + res); 
            System.out.println("nfacs = " + nfacs); 
            throw new RuntimeException("isFactorization = false");
        }
        if ( debug ) {
            logger.info("res facs = " + nfacs); // Q[X]
            //System.out.println("\nnfacs = " + nfacs); // Q[X]
        }
        if (nfacs.size() == 1) {
            factors.add(P);
            return factors;
        }

        // compute gcds of factors with polynomial in Q(alpha)[X]
        GenPolynomial<AlgebraicNumber<C>> Pp = P;
        //System.out.println("Pp = " + Pp);
        GenPolynomial<AlgebraicNumber<C>> Ni;
        for (GenPolynomial<C> nfi : nfacs) {
            //System.out.println("nfi = " + nfi);
            Ni = PolyUfdUtil.<C> substituteConvertToAlgebraicCoefficients(pfac, nfi, ks);
            if (logger.isInfoEnabled()) {
                logger.info("Ni = " + Ni);
            }
            //System.out.println("Pp = " + Pp);
            //System.out.println("Ni = " + Ni);
            // compute gcds of factors with polynomial
            GenPolynomial<AlgebraicNumber<C>> pni = aengine.gcd(Ni, Pp);
            //System.out.println("pni = " + pni);
            if (!pni.leadingBaseCoefficient().isONE()) {
                System.out.println("gcd(Ni,Pp) not monic " + pni);
                pni = pni.monic();
            }
            //System.out.println("gcd(Ni,Pp) = " + pni);
            if (!pni.isONE()) {
                factors.add(pni);
                Pp = Pp.divide(pni);
            } else {
                GenPolynomial<AlgebraicNumber<C>> qni = Pp.divide(Ni);
                GenPolynomial<AlgebraicNumber<C>> rni = Pp.remainder(Ni);
                System.out.println("div qni = " + qni);
                System.out.println("div rni = " + rni);
                throw new RuntimeException("gcd(Ni,Pp) == 1");
            }
        }
        if (!Pp.isZERO() && !Pp.isONE()) { // hack to pretend factorization
            factors.add(Pp);
        }
        //System.out.println("afactors = " + factors);
        return factors;
    }

}
