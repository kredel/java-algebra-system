
/*
 * $Id$
 */

package edu.jas.ufd;


import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
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
 * Greatest common divisor algorithms 
 * with subresultant polynomial remainder sequence and univariate Hensel lifting.
 * @author Heinz Kredel
 */

public class GreatestCommonDivisorHensel //<C extends GcdRingElem<C> > 
       extends GreatestCommonDivisorSubres<BigInteger> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorHensel.class);
    private boolean debug = logger.isDebugEnabled();


    /**
     * Univariate GenPolynomial greatest comon divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<BigInteger> baseGcd( GenPolynomial<BigInteger> P,
                                              GenPolynomial<BigInteger> S ) {
        if ( S == null || S.isZERO() ) {
            return P;
        }
        if ( P == null || P.isZERO() ) {
            return S;
        }
        if ( P.ring.nvar > 1 ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " no univariate polynomial");
        }
        GenPolynomialRing<BigInteger> fac = P.ring;
        long e = P.degree(0);
        long f = S.degree(0);
        GenPolynomial<BigInteger> q;
        GenPolynomial<BigInteger> r;
        if ( f > e ) {
           r = P;
           q = S;
           long g = f;
           f = e;
           e = g;
        } else {
           q = P;
           r = S;
        }
        r = r.abs();
        q = q.abs();
        // compute contents and primitive parts
        BigInteger a = baseContent( r );
        BigInteger b = baseContent( q );
        // gcd of coefficient contents
        BigInteger c = gcd(a,b);  // indirection
        r = divide(r,a); // indirection
        q = divide(q,b); // indirection
        if ( r.isONE() ) {
           return r.multiply(c);
        }
        if ( q.isONE() ) {
           return q.multiply(c);
        }
        // compute normalization factor
        BigInteger ac = r.leadingBaseCoefficient();
        BigInteger bc = q.leadingBaseCoefficient();
        BigInteger cc = gcd(ac,bc);  // indirection
        // compute norms
        BigInteger an = r.maxNorm();
        BigInteger bn = q.maxNorm();
        BigInteger n = ( an.compareTo(bn) < 0 ? bn : an );
        n = n.multiply( cc ).multiply( n.fromInteger(2) );
        // compute degree vectors, only univeriate
        ExpVector rdegv = r.degreeVector();
        ExpVector qdegv = q.degreeVector();
        //compute factor coefficient bounds
        BigInteger af = an.multiply( PolyUtil.factorBound( rdegv ) );
        BigInteger bf = bn.multiply( PolyUtil.factorBound( qdegv ) );
        BigInteger cf = ( af.compareTo(bf) < 0 ? bf : af );
        cf = cf.multiply( cc.multiply( cc.fromInteger(8) ) );
        //initialize prime list and degree vector
        PrimeList primes = new PrimeList();
        int pn = 10; //primes.size();

        ModIntegerRing cofac;
        ModIntegerRing cofacM = null;
        GenPolynomial<ModInteger> qm;
        GenPolynomial<ModInteger> rm;
        GenPolynomialRing<ModInteger> mfac;
        GenPolynomialRing<ModInteger> rfac = null;
        int i = 0;
        BigInteger M = null;
        BigInteger cfe = null;
        GenPolynomial<ModInteger> cp = null;
        GenPolynomial<ModInteger> cm = null;
        GenPolynomial<ModInteger>[] ecm = null;
        GenPolynomial<ModInteger> sm = null;
        GenPolynomial<ModInteger> tm = null;
        GenPolynomial<BigInteger> cpi = null;
        GenPolynomial<BigInteger>[] lift = null;
        if ( debug ) {
           logger.debug("c = " + c);
           logger.debug("cc = " + cc);
           logger.debug("n  = " + n);
           logger.debug("cf = " + cf);
        }

        for ( java.math.BigInteger p : primes ) {
            //System.out.println("next run ++++++++++++++++++++++++++++++++++");
            if ( ++i >= pn ) {
                logger.error("prime list exhausted, pn = " + pn);
                //return iufd.gcd(P,S);
                throw new RuntimeException("prime list exhausted");
            }
            // initialize coefficient factory and map normalization factor
            cofac = new ModIntegerRing( p, true );
            ModInteger nf = cofac.fromInteger( cc.getVal() );
            if ( nf.isZERO() ) {
                continue;
            }
            // initialize polynomial factory and map polynomials
            mfac = new GenPolynomialRing<ModInteger>(cofac,fac.nvar,fac.tord,fac.getVars());
            qm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,q);
            if ( !qm.degreeVector().equals( qdegv ) ) {
                continue;
            }
            rm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,r);
            if ( !rm.degreeVector().equals( rdegv ) ) {
                continue;
            }
            if ( debug ) {
               logger.info("cofac = " + cofac.getModul() ); 
            }

            // compute univariate modular gcd
            ecm = rm.egcd(qm);
            cm = ecm[0];
            // test for constant g.c.d
            if ( cm.isConstant() ) {
               logger.debug("cm, constant = " + cm ); 
               return fac.getONE().multiply( c );
               //return cm.abs().multiply( c );
            }

            sm = ecm[1];
            tm = ecm[2];

            lift = PolyUfdUtil.liftHensel(c,mi,rm,qm,sm,tm);
            cm = lift[0];
        }


        q = basePrimitivePart( q );
        return (q.multiply(c)).abs(); 
    }

}
