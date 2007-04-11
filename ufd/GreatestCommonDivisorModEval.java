
/*
 * $Id$
 */

package edu.jas.ufd;


import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

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

public class GreatestCommonDivisorModEval //<C extends GcdRingElem<C> > 
    extends GreatestCommonDivisorAbstract<ModInteger> { 


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorModEval.class);
    private boolean debug = logger.isDebugEnabled();

    protected final 
        GreatestCommonDivisorAbstract<ModInteger> mufd =  
              //new GreatestCommonDivisorModular();
              //new GreatestCommonDivisorSimple<ModInteger>();
              new GreatestCommonDivisorPrimitive<ModInteger>();
              //new GreatestCommonDivisorSubres<ModInteger>();

    //        GreatestCommonDivisorModular mufd =  
    //          new GreatestCommonDivisorModular();

    protected final 
        GreatestCommonDivisorAbstract<BigInteger> iufd =  
              new GreatestCommonDivisorSubres<BigInteger>();
              //new GreatestCommonDivisorSimple<ModInteger>();
              //new GreatestCommonDivisorPrimitive<ModInteger>();


    /**
     * Univariate GenPolynomial greatest comon divisor.
     * Delegate to subresultant baseGcd, should not be needed.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<ModInteger> baseGcd( GenPolynomial<ModInteger> P,
                                              GenPolynomial<ModInteger> S ) {
        return mufd.baseGcd(P,S);
    }



    /**
     * Univariate GenPolynomial recursive greatest comon divisor.
     * Delegate to subresultant recursiveGcd, should not be needed.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<GenPolynomial<ModInteger>> 
        recursiveGcd( GenPolynomial<GenPolynomial<ModInteger>> P,
                      GenPolynomial<GenPolynomial<ModInteger>> S ) {
        return mufd.recursiveGcd(P,S);
    }


    /**
     * GenPolynomial greatest comon divisor, modular evaluation algorithm.
     * Mathod name must be different because of parameter type erasure.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<ModInteger> gcd( GenPolynomial<ModInteger> P,
                                          GenPolynomial<ModInteger> S ) {
        if ( S == null || S.isZERO() ) {
            return P;
        }
        if ( P == null || P.isZERO() ) {
            return S;
        }
        GenPolynomialRing<ModInteger> fac = P.ring;
        // special case for univariate polynomials
        if ( fac.nvar <= 1 ) {
           GenPolynomial<ModInteger> T = baseGcd(P,S);
           return T;
        }
        long e = P.degree(0);
        long f = S.degree(0);
        System.out.println("e = " + e);
        System.out.println("f = " + f);
        GenPolynomial<ModInteger> q;
        GenPolynomial<ModInteger> r;
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
        // compute univariate contents and primitive parts ?MUPGCD??
        ModInteger a = baseContent( r );
        ModInteger b = baseContent( q );
        // gcd of coefficient contents
        ModInteger c = gcd(a,b);  // indirection
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
        // compute normalization factor ???
        ModInteger ac = r.leadingBaseCoefficient();
        ModInteger bc = q.leadingBaseCoefficient();
        //ModInteger cc = gcd(ac,bc);  // indirection
        GenPolynomial<ModInteger> cc = null;
        System.out.println("ac = " + ac);
        System.out.println("bc = " + bc);
        System.out.println("cc = " + cc);
        // compute degrees and degree vectors
        ExpVector rdegv = r.degreeVector(); // remove one
        ExpVector qdegv = q.degreeVector(); // remove one
        System.out.println("rdegv = " + rdegv);
        System.out.println("qdegv = " + qdegv);
        long rd0 = r.degree(0);
        long qd0 = q.degree(0);
        System.out.println("rd0 = " + rd0);
        System.out.println("qd0 = " + qd0);
        long cd0 = 0; //c.degree(0);
        long G = ( rd0 >= qd0 ? rd0 : qd0 ) + cd0;
        System.out.println("G = " + G);

        // initialize element and degree vector
        ExpVector wdegv = rdegv.subst( 0, rdegv.getVal(0) + 1 );
        // +1 seems to be a hack for the unlucky prime test
        System.out.println("wdegv = " + wdegv);
        RingFactory<ModInteger> cofac = P.ring.coFac;
        System.out.println("cofac = " + cofac);
        GenPolynomialRing<ModInteger> mfac
           = new GenPolynomialRing<ModInteger>(cofac,fac.nvar-1,fac.tord);
        GenPolynomialRing<ModInteger> ufac
           = new GenPolynomialRing<ModInteger>(cofac,1,fac.tord);
        GenPolynomialRing<GenPolynomial<ModInteger>> rfac
           = new GenPolynomialRing<GenPolynomial<ModInteger>>(ufac,fac.nvar-1,fac.tord);
        ModInteger inc = cofac.getONE();
        long i = 0;
        long en = inc.getModul().longValue()-1;
        System.out.println("en = " + en);
        GenPolynomial<ModInteger> M;
        GenPolynomial<ModInteger> qm;
        GenPolynomial<ModInteger> rm;
        GenPolynomial<ModInteger> cm;
        GenPolynomial<ModInteger> cp = null;
        for ( ModInteger d = cofac.getZERO(); /* d <= en */ ; d = d.sum(inc) ) {
            if ( ++i >= en ) {
                System.out.println("elements of Z_p exhausted");
                return mufd.gcd(P,S);
                //throw new RuntimeException("prime list exhausted");
                //break;
            }
            // map normalization factor
            ModInteger nf = PolyUtil.<ModInteger>evaluateMain( cofac, cc, d );
            System.out.println("nf = " + nf);
            if ( nf.isZERO() ) {
                continue;
            }
            // map polynomials
            qm = PolyUtil.<ModInteger>evaluateFirst( ufac, mfac, q, d );
            System.out.println("qm = " + qm);
            if ( !qm.degreeVector().equals( qdegv ) ) {
                continue;
            }
            rm = PolyUtil.<ModInteger>evaluateFirst( ufac, mfac, r, d );
            System.out.println("rm = " + rm);
            if ( !rm.degreeVector().equals( rdegv ) ) {
                continue;
            }
            // compute modular gcd in recursion
            cm = gcd(rm,qm);
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
                  if ( M.degree(0) > G ) {
                     System.out.println("deg(M) > G: " + M.degree(0) + " > " + G);
                     // continue; // why should this be required?
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
            // prepare interpolation algorithm
            cm = cm.multiply(nf);
            //System.out.println("cm = " + cm);
            if ( M == null ) {
               // initialize interpolation
               M = ufac.getONE();
               cp = cm; 
               wdegv = ExpVector.EVGCD(wdegv,mdegv);
            } else {
               // interpolate
               ModInteger mi = PolyUtil.<ModInteger>evaluateMain( cofac, M, d );
               mi = mi.inverse(); // mod p
               System.out.println("mi = " + mi);


               GenPolynomial<ModInteger> mn = ufac.getONE().multiply(d);
               mn = ufac.univariate(0).subtract(mn);
               System.out.println("mn = " + mn); // (x_1 - d)
               M = M.multiply( mn );
               System.out.println("M = " + M);

               cp = PolyUtil.interpolate(rfac,cp,M,mi,cm,d);
            }
            // test for completion
            if ( M.degree(0) > G ) {
               System.out.println("done on M = " + M);
               break;
            }
            long cmn = cp.degree(0);
            System.out.println("cmn = " + cmn);
            if ( M.degree(0) > cmn ) {
               System.out.println("done on cmn = " + cmn);
               // does not work: only if cofactors are also considered?
               // break;
            }


        }
        // remove normalization
        q = cp; //PolyUtil.integerFromModularCoefficients(fac,cp);
        System.out.println("q  = " + q);
        q = basePrimitivePart( q ); 
        System.out.println("q  = " + q);
        return q.abs().multiply( c ); 
    }

}
