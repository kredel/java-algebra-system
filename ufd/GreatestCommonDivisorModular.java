
/*
 * $Id$
 */

package edu.jas.ufd;


import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.PrimeList;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.PolyUtil;


/**
 * Greatest common divisor algorithms with primitive polynomial remainder sequence.
 * @author Heinz Kredel
 */

public class GreatestCommonDivisorModular //<C extends GcdRingElem<C> > 
       extends GreatestCommonDivisorAbstract<BigInteger> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorModular.class);
    private boolean debug = logger.isDebugEnabled();


    protected final 
        GreatestCommonDivisorAbstract<ModInteger> mufd =  
              //new GreatestCommonDivisorSimple<ModInteger>();
              new GreatestCommonDivisorPrimitive<ModInteger>();
              //new GreatestCommonDivisorSubres<ModInteger>();

    protected final 
        GreatestCommonDivisorAbstract<BigInteger> iufd =  
              new GreatestCommonDivisorSubres<BigInteger>();

    /**
     * Univariate GenPolynomial greatest comon divisor.
     * Delegate to subresultant baseGcd, should not be needed.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<BigInteger> baseGcd( GenPolynomial<BigInteger> P,
                                              GenPolynomial<BigInteger> S ) {
        return iufd.baseGcd(P,S);
    }



    /**
     * Univariate GenPolynomial recursive greatest comon divisor.
     * Delegate to subresultant recursiveGcd, should not be needed.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<GenPolynomial<BigInteger>> 
        recursiveGcd( GenPolynomial<GenPolynomial<BigInteger>> P,
                      GenPolynomial<GenPolynomial<BigInteger>> S ) {
        return iufd.recursiveGcd(P,S);
    }


    /**
     * GenPolynomial greatest comon divisor, modular algorithm.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<BigInteger> gcd( GenPolynomial<BigInteger> P,
                                          GenPolynomial<BigInteger> S ) {
        if ( S == null || S.isZERO() ) {
            return P;
        }
        if ( P == null || P.isZERO() ) {
            return S;
        }
        GenPolynomialRing<BigInteger> fac = P.ring;
        if ( fac.nvar <= 1 ) {
            //System.out.println("P = " + P);
            //System.out.println("S = " + S);
            GenPolynomial<BigInteger> T = baseGcd(P,S);
            //System.out.println("T = " + T);
            if ( false || debug ) {
               GenPolynomial<BigInteger> X;
               X = basePseudoRemainder(P,T);
               System.out.println("X1 =" + X);
               if ( !X.isZERO() ) {
                    throw new RuntimeException(this.getClass().getName()
                                       + " X != null");
               }
               X = basePseudoRemainder(S,T);
               System.out.println("X2 =" + X);
               if ( !X.isZERO() ) {
                    throw new RuntimeException(this.getClass().getName()
                                       + " X != null");
               }
            }
            return T;
        }

        long e = P.degree(0);
        long f = S.degree(0);
        System.out.println("e = " + e);
        System.out.println("f = " + f);
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
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        r = divide(r,a); // indirection
        q = divide(q,b); // indirection
        System.out.println("r = " + r);
        System.out.println("q = " + q);
        System.out.println("c = " + c);
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
        System.out.println("ac = " + ac);
        System.out.println("bc = " + bc);
        System.out.println("cc = " + cc);
        // compute norms
        BigInteger an = r.maxNorm();
        BigInteger bn = q.maxNorm();
        BigInteger n = ( an.compareTo(bn) < 0 ? bn : an );
        n = n.multiply( cc ).multiply( n.fromInteger(2) );
        System.out.println("an = " + an);
        System.out.println("bn = " + bn);
        System.out.println("n  = " + n);
        // compute degree vectors
        ExpVector rdegv = r.degreeVector();
        ExpVector qdegv = q.degreeVector();
        System.out.println("rdegv  = " + rdegv);
        System.out.println("qdegv  = " + qdegv);
        //compute factor coefficient bounds
        BigInteger af = an.multiply( PolyUtil.factorBound( rdegv ) );
        BigInteger bf = bn.multiply( PolyUtil.factorBound( qdegv ) );
        BigInteger cf = ( af.compareTo(bf) < 0 ? bf : af );
        cf = cf.multiply( cc.multiply( cc.fromInteger(8) ) );
        System.out.println("af = " + af);
        System.out.println("bf = " + bf);
        System.out.println("cf = " + cf);
        //initialize prime list and degree vector
        PrimeList primes = new PrimeList();
        int pn = 10; //primes.size();
        ExpVector wdegv = rdegv.subst( 0, rdegv.getVal(0) + 1 );
        // +1 seems to be a hack for the unlucky prime test
        System.out.println("wdegv = " + wdegv);
        ModInteger cofac;
        ModInteger cofacM = null;
        GenPolynomial<ModInteger> qm;
        GenPolynomial<ModInteger> rm;
        GenPolynomialRing<ModInteger> mfac;
        GenPolynomialRing<ModInteger> rfac = null;
        int i = 0;
        BigInteger M = null;
        BigInteger cfe = null;
        GenPolynomial<ModInteger> cp = null;
        GenPolynomial<ModInteger> cm = null;
        GenPolynomial<BigInteger> cpi = null;
        for ( java.math.BigInteger p : primes ) {
            if ( ++i >= pn ) {
                System.out.println("prime list exhausted");
                return iufd.gcd(P,S);
                //throw new RuntimeException("prime list exhausted");
                //break;
            }
            // initialize coefficient factory and map normalization factor
            cofac = new ModInteger( p, true );
            System.out.println("cofac = " + cofac.getModul());
            ModInteger nf = cofac.fromInteger( cc.getVal() );
            System.out.println("nf = " + nf);
            if ( nf.isZERO() ) {
                continue;
            }
            // initialize polynomial factory and map polynomials
            mfac = new GenPolynomialRing<ModInteger>(cofac,fac.nvar,fac.tord,fac.getVars());
            System.out.println("mfac = " + mfac);
            qm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,q);
            System.out.println("qm = " + qm);
            if ( !qm.degreeVector().equals( qdegv ) ) {
                continue;
            }
            rm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,r);
            System.out.println("rm = " + rm);
            if ( !rm.degreeVector().equals( rdegv ) ) {
                continue;
            }
            // compute modular gcd
            cm = mufd.gcd(rm,qm);
            System.out.println("cm = " + cm);
            if ( true || debug ) {
               System.out.println("cm | rm = " + mufd.basePseudoRemainder(rm,cm));
               System.out.println("cm | qm = " + mufd.basePseudoRemainder(qm,cm));
            }
            // test for constant g.c.d
            if ( cm.isConstant() ) {
               return fac.getONE();
            }
            // test for unlucky prime
            ExpVector mdegv = cm.degreeVector();
            System.out.println("mdegv = " + mdegv);
            if ( wdegv.equals(mdegv) ) { // TL = 0
               System.out.println("TL = 0");
               // prime ok, next round
               if ( M != null ) {
                  if ( M.compareTo(cfe) > 0 ) {
                     System.out.println("M > cfe: " + M + " > " + cfe);
                     // continue;
                  }
               }
            } else { // TL = 3
               boolean ok = false;
               if ( ExpVector.EVMT(wdegv,mdegv) ) { // TL = 2
                  System.out.println("TL = 2");
                  M = null;  // init chinese remainder
                  ok = true; // prime ok
               }
               if ( ExpVector.EVMT(mdegv,wdegv) ) { // TL = 1
                  System.out.println("TL = 1");
                  continue; // skip this prime
               }
               if ( !ok ) {
                  System.out.println("TL = 3");
                  M = null; // discard chinese remainder and previous work
                  continue; // prime not ok
               }
            }
            wdegv = mdegv;
            // prepare chinese remainder algorithm
            cm = cm.multiply(nf);
            System.out.println("cm = " + cm);
            if ( M == null ) {
               // initialize chinese remainder algorithm
               M = new BigInteger(p);
               cofacM = cofac;
               rfac = mfac;
               cp = cm; 
               wdegv = ExpVector.EVGCD(wdegv,mdegv);
               cfe = cf;
               for ( int k = 0; k < wdegv.length(); k++ ) {
                   cfe = cfe.multiply( new BigInteger( wdegv.getVal(k)+1 ) );
               }
               System.out.println("cfe = " + cfe);
            } else {
               // apply chinese remainder algorithm
               ModInteger mi = cofac.fromInteger( M.getVal() ); 
               mi = mi.inverse(); // mod p
               System.out.println("mi = " + mi);
               M = M.multiply( new BigInteger(p) );
               System.out.println("M = " + M);
               cofacM = new ModInteger( M.getVal() );
               rfac = new GenPolynomialRing<ModInteger>(cofacM,fac.nvar,fac.tord,fac.getVars());
               cp = PolyUtil.chineseRemainder(rfac,cp,mi,cm);
            }
            System.out.println("cp = " + cp);
            // test for completion
            if ( n.compareTo(M) <= 0 ) {
               System.out.println("done on M = " + M);
               break;
            }
            // must use integer.sumNorm
            cpi = PolyUtil.integerFromModularCoefficients(fac,cp);
            BigInteger cmn = cpi.sumNorm();
            cmn = cmn.multiply( cmn.fromInteger(4) );
            System.out.println("cmn = " + cmn);
            if ( cmn.compareTo( M ) <= 0 ) {
               System.out.println("done on cmn = " + cmn);
               // does not work: only if cofactors are also considered?
               // break;
            }
        }
        // remove normalization
        q = PolyUtil.integerFromModularCoefficients(fac,cp);
        System.out.println("q  = " + q);
        q = basePrimitivePart( q ); 
        System.out.println("q  = " + q);
        return q.abs().multiply( c ); 
    }

}
