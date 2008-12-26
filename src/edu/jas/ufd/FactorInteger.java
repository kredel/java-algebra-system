
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

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.PolyUtil;


/**
 * Integer coefficients factorization algorithms.
 * @author Heinz Kredel
 */

public class FactorInteger //<C extends GcdRingElem<C> > 
       //extends FactorAbstract<BigInteger>
    {


    private static final Logger logger = Logger.getLogger(FactorInteger.class);
    private boolean debug = logger.isInfoEnabled();


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial<ModInteger>.
     * @return (P).
     */
    public List<GenPolynomial<BigInteger>> baseFactorsSquarefree(GenPolynomial<BigInteger> P) {
        if ( P == null ) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<BigInteger>> factors = new ArrayList<GenPolynomial<BigInteger>>();
        if ( P.isZERO() ) {
            return factors;
        }
        if ( P.isONE() ) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<BigInteger> pfac = P.ring;
        if ( pfac.nvar > 1 ) {
            throw new RuntimeException(this.getClass().getName()
                    + " only for univariate polynomials");
        }
//         SortedMap<Long,GenPolynomial<ModInteger>> dfacs = baseDistinctDegreeFactors(P);
//         System.out.println("dfacs    = " + dfacs);
//         for ( Long e : dfacs.keySet() ) {
//             GenPolynomial<ModInteger> f = dfacs.get( e );
//             List<GenPolynomial<ModInteger>> efacs = baseEqualDegreeFactors(f,e);
//             System.out.println("efacs    = " + efacs);
//             for ( GenPolynomial<ModInteger> h : efacs ) {
//                 factors.add( h );
//             }
//         }
        return factors;
    }


    /**
     * GenPolynomial base factorization.
     * @param P GenPolynomial<BigInteger>.
     * @return (P).
     */
    public SortedMap<GenPolynomial<BigInteger>,Integer> baseFactors(GenPolynomial<BigInteger> P) {
        if ( P == null ) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        SortedMap<GenPolynomial<BigInteger>,Integer> factors
           = new TreeMap<GenPolynomial<BigInteger>,Integer>();
        if ( P.isZERO() ) {
            return factors;
        }
        GenPolynomialRing<BigInteger> pfac = P.ring;
        if ( pfac.nvar > 1 ) {
            throw new RuntimeException(this.getClass().getName()
                    + " only for univariate polynomials");
        }
        GreatestCommonDivisorAbstract<BigInteger> engine 
         = (GreatestCommonDivisorAbstract<BigInteger>)GCDFactory.<BigInteger>getImplementation( pfac.coFac );
        BigInteger c = engine.baseContent(P);
        if ( ! c.isONE() ) {
           GenPolynomial<BigInteger> pc = pfac.getONE().multiply( c );
           factors.put( pc, 1 );
           P = P.divide(c); // make primitive
        }
//         SortedMap<Integer,GenPolynomial<ModInteger>> facs = engine.baseSquarefreeFactors(P);
//         System.out.println("facs    = " + facs);
//         for ( Integer d : facs.keySet() ) {
//             GenPolynomial<ModInteger> g = facs.get( d );
//             List<GenPolynomial<ModInteger>> sfacs = baseFactorsSquarefree(g);
//             //System.out.println("sfacs   = " + sfacs);
//             for ( GenPolynomial<ModInteger> h : sfacs ) {
//                 factors.put( h, d );
//             }
//         }
        System.out.println("factors = " + factors);
        return factors;
    }

}
