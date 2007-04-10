
/*
 * $Id$
 */

package edu.jas.ufd;


import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;

import edu.jas.poly.GenPolynomial;
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
              new GreatestCommonDivisorSimple<ModInteger>();

    /**
     * Univariate GenPolynomial greatest comon divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
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
           //logger.info("pseudoGcd only for univaraite polynomials");
           // guess
           //return P.ring.getONE();
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
        ExpVector rdegv1 = rdegv.subst( 0, rdegv.getVal(0) + 1 );
        ExpVector qdegv = q.degreeVector();
        System.out.println("rdegv  = " + rdegv);
        System.out.println("rdegv1 = " + rdegv1);
        System.out.println("qdegv  = " + qdegv);
        //compute factor coefficient bounds
        BigInteger af = an.multiply( PolyUtil.factorBound( rdegv ) );
        BigInteger bf = bn.multiply( PolyUtil.factorBound( qdegv ) );
        BigInteger cf = ( af.compareTo(bf) < 0 ? bf : af );
        cf = cf.multiply( cc.multiply( cc.fromInteger(8) ) );
        System.out.println("af = " + af);
        System.out.println("bf = " + bf);
        System.out.println("cf = " + cf);


        GenPolynomial<BigInteger> x;
        while ( !r.isZERO() ) {
            x = basePseudoRemainder(q,r);
            //System.out.println("x  = " + x);
            q = r;
            //r = basePrimitivePart( x );
            r = x;
        }
        q = basePrimitivePart( q );
        //System.out.println("q  = " + q);
        return (q.multiply(c)).abs(); 
    }


    /**
     * Univariate GenPolynomial recursive greatest comon divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<GenPolynomial<BigInteger>> 
        recursiveGcd( GenPolynomial<GenPolynomial<BigInteger>> P,
                      GenPolynomial<GenPolynomial<BigInteger>> S ) {
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
        boolean field = P.leadingBaseCoefficient().ring.coFac.isField();
        long e = P.degree(0);
        long f = S.degree(0);
        GenPolynomial<GenPolynomial<BigInteger>> q;
        GenPolynomial<GenPolynomial<BigInteger>> r;
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
        if ( field ) {
           r = PolyUtil.<BigInteger>monic(r);
           q = PolyUtil.<BigInteger>monic(q);
        } else {
           r = r.abs();
           q = q.abs();
        }
        //System.out.println("rgcd r = " + r);
        //System.out.println("rgcd q = " + q);
        GenPolynomial<BigInteger> a = recursiveContent(r);
        GenPolynomial<BigInteger> b = recursiveContent(q);
        //System.out.println("rgcd a = " + a);
        //System.out.println("rgcd b = " + b);

        GenPolynomial<BigInteger> c = gcd(a,b); // go to recursion
        //System.out.println("rgcd c = " + c);
        r = recursiveDivide(r,a);
        q = recursiveDivide(q,b);
        if ( r.isONE() ) {
           return r.multiply(c);
        }
        if ( q.isONE() ) {
           return q.multiply(c);
        }
        //System.out.println("rgcd q = " + q);
        //System.out.println("rgcd r = " + r);
        if ( debug && ( q.isConstant() || r.isConstant() ) ) {
           System.out.println("rgcd q = " + q);
           System.out.println("rgcd r = " + r);
           throw new RuntimeException(this.getClass().getName()
                                       + " error in recursive Content");
        }

        GenPolynomial<GenPolynomial<BigInteger>> x;
        while ( !r.isZERO() ) {
            x = recursivePseudoRemainder(q,r);
            //System.out.println("rgcd x = " + x);
            //if ( !x.isZERO() && x.isConstant() ) {
            //   System.out.println("rpg x = is constant " + x);
            //}
            q = r;
            //r = recursivePrimitivePart( x );
            if ( field ) {
               r = PolyUtil.<BigInteger>monic(x);
            } else {
               r = x;
            }
            //System.out.println("rgcd r = " + r);
        }
        //System.out.println("sign q = " + q.signum());
        return q.abs().multiply(c); //.abs();
    }

}
