
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
        System.out.println("\nP = " + P);
        GenPolynomialRing<AlgebraicNumber<C>> pfac = P.ring;
        if ( pfac.nvar > 1 ) {
            throw new RuntimeException(this.getClass().getName()
                    + " only for univariate polynomials");
        }
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>)pfac.coFac;

        GreatestCommonDivisorSubres<C> engine 
            = new GreatestCommonDivisorSubres<C>( /*cfac.coFac*/ );
              // = (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getImplementation( cfac.coFac );
        GreatestCommonDivisor<AlgebraicNumber<C>> aengine 
            = GCDFactory.<AlgebraicNumber<C>>getProxy( afac );
              //= new GreatestCommonDivisorSubres<AlgebraicNumber<C>>( /*cfac.coFac*/ );

        GenPolynomial<C> agen = afac.modul;
        GenPolynomialRing<C> cfac = afac.ring;
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac,pfac);
        //GenPolynomialRing<C> dfac = cfac.extend(1);

        // transform minimal polynomial to bi-variate polynomial
        GenPolynomial<GenPolynomial<C>> Ac = rfac.getONE().clone(); 
        //Ac = Ac.multiply(agen); // to lower variable 
        GenPolynomial<C> fx = cfac.getONE();
        for ( Monomial<C> m : agen ) {
            C c = m.c;
            GenPolynomial<C> ac = fx.multiply(c); // to upper variable
            Ac.doPutToMap(m.e,ac);
        }
        System.out.println("Ac = " + Ac);

        // search squarefree resultant
        long k = 0;
        GenPolynomial<C> res = null;
        GenPolynomial<GenPolynomial<C>> Pc;
        while ( true ) {
            // transform to bi-variate polynomial, switching varaible sequence
            Pc = rfac.getZERO().clone();
            GenPolynomial<C> fy = cfac.getONE();
            for ( Monomial<AlgebraicNumber<C>> mx : P ) {
                //ExpVector e = mx.e;
                AlgebraicNumber<C> c = mx.c;
                GenPolynomial<C> ac = c.val;
                for ( Monomial<C> my : ac ) {
                    ExpVector ey = my.e;
                    C cy = my.c;
                    GenPolynomial<C> ay = fy.multiply(cy,mx.e);
                    Pc = Pc.sum(ay,my.e);
                }
            }
            System.out.println("Pc = " + Pc);
            Pc = PolyUtil.<C>monic(Pc);
            System.out.println("Pc = " + Pc);

            GenPolynomial<GenPolynomial<C>> Rc = engine.recursiveResultant(Pc,Ac);
            System.out.println("Rc = " + Rc);
            res = Rc.leadingBaseCoefficient();
            System.out.println("res = " + res);

            boolean sq = factorCoeff.isSquarefree(res);
            System.out.println("sq = " + sq);
            if ( sq ) {
                break;
            } else {
                k++;
                break; // also
            }
            //k++;
        }
        // Res is now squarefree, so we can factor it
        SortedMap<GenPolynomial<C>,Integer> nfacs = factorCoeff.baseFactors( res );
        System.out.println("nfacs = " + nfacs);

        // compute gcds of factors with polynomial
        GenPolynomial<AlgebraicNumber<C>> Ni = pfac.getZERO().clone();
        for ( GenPolynomial<C> nfi : nfacs.keySet() ) {
             System.out.println("nfi = " + nfi);
             // transform to Q(alpha) coefficients
             for ( Monomial<C> m : nfi ) {
                 ExpVector e = m.e;
                 C c = m.c;
                 AlgebraicNumber<C> ac = afac.getONE();
                 GenPolynomial<C> pc = cfac.univariate(0); 
                 pc = pc.multiply( c ); 
                 ac = new AlgebraicNumber<C>( afac, pc );
                 Ni.doPutToMap(e,ac); 
             }
             System.out.println("Ni = " + Ni);

             // compute gcds of factors with polynomial
             GenPolynomial<AlgebraicNumber<C>> pni = aengine.gcd(Ni,P);
             System.out.println("pni = " + pni);
             factors.add( pni );
        }
        System.out.println("factors = " + factors);
        return factors;
    }

}
