
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

import edu.jas.util.PowerSet;
import edu.jas.util.KsubSet;


/**
 * Integer coefficients factorization algorithms.
 * @author Heinz Kredel
 */

public class FactorInteger //<C extends GcdRingElem<C> > 
       //extends FactorAbstract<BigInteger>
    {


    private static final Logger logger = Logger.getLogger(FactorInteger.class);
    private boolean debug = true || logger.isInfoEnabled();


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial<BigInteger>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
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
        // compute norm
        BigInteger an = P.maxNorm();
        BigInteger ac = P.leadingBaseCoefficient();
        //compute factor coefficient bounds
        ExpVector degv = P.degreeVector();
        BigInteger M = an.multiply( PolyUtil.factorBound( degv ) );
        M = M.multiply( ac.multiply( ac.fromInteger(8) ) );

        //initialize prime list and degree vector
        PrimeList primes = new PrimeList( PrimeList.Range.small );
        int pn = 10; //primes.size();
        ModIntegerRing cofac = new ModIntegerRing( 13, true );
        GreatestCommonDivisorAbstract<ModInteger> engine 
           = (GreatestCommonDivisorAbstract<ModInteger>)GCDFactory.<ModInteger>getImplementation( cofac );
        GenPolynomial<ModInteger> am = null;
        GenPolynomialRing<ModInteger> mfac;
        int i = 0;
        if ( debug ) {
           logger.debug("an  = " + an);
           logger.debug("ac  = " + ac);
           logger.debug("M   = " + M);
           logger.info("degv = " + degv);
        }
        for ( java.math.BigInteger p : primes ) {
            //System.out.println("next run ++++++++++++++++++++++++++++++++++");
            if ( ++i >= pn ) {
                logger.error("prime list exhausted, pn = " + pn);
                throw new RuntimeException("prime list exhausted");
            }
//             if ( i < 2 ) {
//                 p = new java.math.BigInteger("11");
//             }
            cofac = new ModIntegerRing( p, true );
            ModInteger nf = cofac.fromInteger( ac.getVal() );
            if ( nf.isZERO() ) {
                continue;
            }
            // initialize polynomial factory and map polynomial
            mfac = new GenPolynomialRing<ModInteger>(cofac,pfac);
            am = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,P);
            if ( ! am.degreeVector().equals( degv ) ) { // allways true
                continue;
            }
            GenPolynomial<ModInteger> ap = PolyUtil.<ModInteger> baseDeriviative(am);
            if ( ap.isZERO() ) {
                continue;
            }
            GenPolynomial<ModInteger> g = engine.baseGcd(am, ap);
            if ( g.isONE() ) {
                break;
            }
        }
        // now am is also squarefree mod p, so factor mod p
        FactorModular mengine = new FactorModular();
        List<GenPolynomial<ModInteger>> mlist = mengine.baseFactorsSquarefree(am);
        // lift via Hensel
        System.out.println("mlist = " + mlist);
        List<GenPolynomial<BigInteger>> ilist = PolyUfdUtil.liftHenselQuadratic(P,M,mlist);
        System.out.println("ilist = " + ilist);

        if ( ilist.size() <= 1 ) {
             factors.addAll( ilist );
             return factors;
        }
     
        int dl = (ilist.size()+1)/2;
        GenPolynomial<BigInteger> u = P;
        for ( int j = 1; j <= dl; j++ ) {
            KsubSet<GenPolynomial<BigInteger>> ps = new KsubSet<GenPolynomial<BigInteger>>( ilist, j );
            for ( List<GenPolynomial<BigInteger>> flist : ps ) {
                //System.out.println("flist = " + flist);
                GenPolynomial<BigInteger> trial = pfac.getONE();
                for ( int k = 0; k < flist.size(); k++ ) {
                    trial = trial.multiply( flist.get(k) );
                }
                if ( u.remainder(trial).isZERO() ) {
                    System.out.println("trial = " + trial);
                    factors.add( trial );
                    u = u.divide( trial );
                    if ( ilist.removeAll( flist ) ) {
                        System.out.println("new ilist = " + ilist);
                        dl = (ilist.size()+1)/2;
                        j = 1;
                        ps = new KsubSet<GenPolynomial<BigInteger>>( ilist, j );
                        break;
                    } else {
                       System.out.println("error removing flist from ilist = " + ilist);
                    }
                }
            }
        }
        if ( !u.isONE() && !u.equals(P) ) {
            System.out.println("rest u = " + u);
            factors.add( u );
        }
        if ( factors.size() == 0 ) {
            System.out.println("irred u = " + u);
            factors.add( P );
        }
        return factors;
    }


    /**
     * GenPolynomial base factorization.
     * @param P GenPolynomial<BigInteger>.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i**e_i.
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
        SortedMap<Integer,GenPolynomial<BigInteger>> facs = engine.baseSquarefreeFactors(P);
        System.out.println("facs    = " + facs);
        for ( Integer d : facs.keySet() ) {
            GenPolynomial<BigInteger> g = facs.get( d );
            List<GenPolynomial<BigInteger>> sfacs = baseFactorsSquarefree(g);
            //System.out.println("sfacs   = " + sfacs);
            for ( GenPolynomial<BigInteger> h : sfacs ) {
                factors.put( h, d );
            }
        }
        System.out.println("factors = " + factors);
        return factors;
    }

}
