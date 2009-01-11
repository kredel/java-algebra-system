
/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.List;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.structure.Power;
import edu.jas.structure.GcdRingElem;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.Monomial;

import edu.jas.util.PowerSet;
import edu.jas.util.KsubSet;


/**
 * Algebraic number coefficients factorization algorithms.
 * @author Heinz Kredel
 */

public class FactorAlgebraic <C extends GcdRingElem<C>> 
       extends FactorAbstract<AlgebraicNumber<C>> {


    private static final Logger logger = Logger.getLogger(FactorAlgebraic.class);
    private boolean debug = true || logger.isInfoEnabled();


    /**
     * Factorization engine for base coefficients.
     */
    protected final FactorAbstract<C> factorCoeff;


    /**
     * No argument constructor.
     * <b>Note:</b> can't use this constructor.
     */
    protected FactorAlgebraic() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor.
     * @parm factorCoeff factorization engine for base coefficients.
     */
    public FactorAlgebraic(FactorAbstract<C> factorCoeff) {
        this.factorCoeff = factorCoeff;
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial<AlgebraicNumber<C>>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    public List<GenPolynomial<AlgebraicNumber<C>>> 
      baseFactorsSquarefree(GenPolynomial<AlgebraicNumber<C>> P) {
        if ( P == null ) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<AlgebraicNumber<C>>> factors = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();
        if ( P.isZERO() ) {
            return factors;
        }
        if ( P.isONE() ) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<AlgebraicNumber<C>> pfac = P.ring; // Q(alpha)[x]
        if ( pfac.nvar > 1 ) {
            throw new RuntimeException("only for univariate polynomials");
        }
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>)pfac.coFac;
        GenPolynomialRing<C> cfac = afac.ring;
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac,pfac);

        AlgebraicNumber<C> ldcf = P.leadingBaseCoefficient();
        if ( !ldcf.isONE() ) {
            P = P.monic();
            factors.add( pfac.getONE().multiply(ldcf) );
        }
        //System.out.println("\nP = " + P);

        GreatestCommonDivisor<AlgebraicNumber<C>> aengine 
            = GCDFactory.<AlgebraicNumber<C>>getProxy( afac );
            //= new GreatestCommonDivisorSubres<AlgebraicNumber<C>>( /*cfac.coFac*/ );

        // search squarefree norm
        long k = 0L;
        long ks = k;
        GenPolynomial<C> res = null;
        boolean sqf = false;
        while ( !sqf ) {
            if ( k > 4 ) {
                k = -1;
                //break;
            }
            if ( k < -1 ) {
                break;
            }
            // compute norm with x -> ( y - k x )
            ks = k;
            res = PolyUfdUtil.<C> norm( P, ks );
            //System.out.println("res = " + res);
            if ( res.isZERO() || res.isConstant() ) {
                k++;
                continue;
            }
            sqf = factorCoeff.isSquarefree(res);
            //System.out.println("sqf = " + sqf + "\n");
            if ( k < 0 ) {
                k--;
            } else {
                k++;
            }
        }
        // if Res is now squarefree, we can factor it, else must use complete factorization
        List<GenPolynomial<C>> nfacs;
        if ( !sqf ) {
           SortedMap<GenPolynomial<C>,Long> nfacsx = factorCoeff.baseFactors( res );
           if ( !factorCoeff.isFactorization( res, nfacsx ) ) {
              throw new RuntimeException("isFactorization = false"); 
           }
           nfacs = new ArrayList<GenPolynomial<C>>( nfacsx.keySet() );
        } else {
           nfacs = factorCoeff.baseFactorsSquarefree( res );
           if ( !factorCoeff.isFactorization( res, nfacs ) ) {
              throw new RuntimeException("isFactorization = false"); 
           }
        }
        //System.out.println("\nnfacs = " + nfacs); // Q[X]
        if ( nfacs.size() == 1 ) {
            factors.add(P);
            return factors;
        }

        // compute gcds of factors with polynomial in Q(alpha)[X]
        GenPolynomial<AlgebraicNumber<C>> Pp = P;
        //System.out.println("Pp = " + Pp);
        GenPolynomial<AlgebraicNumber<C>> Ni;
        for ( GenPolynomial<C> nfi : nfacs ) { 
             //System.out.println("nfi = " + nfi);
             Ni = PolyUfdUtil.<C> substituteConvertToAlgebraicCoefficients(pfac,nfi,ks);
             if ( logger.isInfoEnabled() ) {
                logger.info("Ni = " + Ni);
             }
             // compute gcds of factors with polynomial
             GenPolynomial<AlgebraicNumber<C>> pni = aengine.gcd(Ni,Pp);
             if ( !pni.leadingBaseCoefficient().isONE() ) {
                System.out.println("gcd(Ni,Pp) not monic " + pni);
                pni = pni.monic();
             }
             //System.out.println("gcd(Ni,Pp) = " + pni);
             if ( !pni.isONE() ) {
                 factors.add( pni );
                 Pp = Pp.divide( pni );
             } else {
                 GenPolynomial<AlgebraicNumber<C>> qni = Pp.divide(Ni);
                 GenPolynomial<AlgebraicNumber<C>> rni = Pp.remainder(Ni);
                 System.out.println("div qni = " + qni);
                 System.out.println("div rni = " + rni);
                 throw new RuntimeException("gcd(Ni,Pp) == 1");
             }
        }
        if ( ! Pp.isZERO() && ! Pp.isONE() ) { // hack to pretend factorization
            factors.add( Pp );
        }
        //System.out.println("afactors = " + factors);
        return factors;
    }

}
