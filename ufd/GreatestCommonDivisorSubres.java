
/*
 * $Id$
 */

package edu.jas.ufd;


import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ExpVector;
import static edu.jas.poly.PolyUtil.distribute;
import static edu.jas.poly.PolyUtil.recursive;


/**
 * Greatest common divisor algorithms with subresultant polynomial remainder sequence.
 * @author Heinz Kredel
 */

public class GreatestCommonDivisorSubres<C extends GcdRingElem<C> > 
       extends GreatestCommonDivisorAbstract<C> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorSubres.class);
    private boolean debug = logger.isDebugEnabled();


    /**
     * GenPolynomial pseudo remainder.
     * For univariate polynomials.
     * @param P GenPolynomial.
     * @param S nonzero GenPolynomial.
     * @return remainder with ldcf(S)<sup>m</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public GenPolynomial<C> basePseudoRemainder( GenPolynomial<C> P, 
                                                 GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(this.getClass().getName()
                                       + " division by zero");
        }
        if ( P.isZERO() ) {
            return P;
        }
        if ( S.degree() <= 0 ) {
            return P.ring.getZERO();
        }
        long m = P.degree(0);
        long n = S.degree(0);
        C c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> r = P; 
        for ( long i = m; i >= n; i-- ) {
            if ( r.isZERO() ) {
               return r;
            }
            long k = r.degree(0);
            if ( i == k ) {
                ExpVector f = r.leadingExpVector();
                C a = r.leadingBaseCoefficient();
                f = ExpVector.EVDIF( f, e );
                //logger.info("red div = " + e);
                //System.out.println("red div = " + f);
                r = r.multiply( c );    // coeff ac
                h = S.multiply( a, f ); // coeff ac
                r = r.subtract( h );
            } else {
                r = r.multiply( c );
            }
        }
        return r;
    }


    /**
     * Univariate GenPolynomial greatest comon divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<C> baseGcd( GenPolynomial<C> P,
                                     GenPolynomial<C> S ) {
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
        GenPolynomial<C> q;
        GenPolynomial<C> r;
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
        C a = baseContent( r );
        C b = baseContent( q );
        C c = gcd(a,b);  // indirection
        r = divide(r,a); // indirection
        q = divide(q,b); // indirection
        if ( r.isONE() ) {
           return r.multiply(c);
        }
        if ( q.isONE() ) {
           return q.multiply(c);
        }
        C g = r.ring.getONECoefficient();
        C h = r.ring.getONECoefficient();
        GenPolynomial<C> x;
        C z;
        while ( !r.isZERO() ) {
            long delta = q.degree(0) - r.degree(0);
            //System.out.println("delta    = " + delta);
            //System.out.println("r.degree = " + r.degree());
            //x = basePseudoRemainder(q,r);
            x = basePseudoRemainder(q,r);
            q = r;
            if ( !x.isZERO() ) {
                //System.out.println("x  = " + x);
                z = g.multiply( power( P.ring.coFac, h, delta ) );
                //System.out.println("z  = " + z);
                //r = basePrimitivePart( x );
                //r = x;
                r = x.divide( z );
                //System.out.println("x   = " + x);
                //System.out.println("r   = " + r);
                //System.out.println("rp  = " + basePrimitivePart( r ));
                //System.out.println("rs  = " + x.divide( z ));
                //System.out.println("rps = " + basePrimitivePart( x.divide( z ) ));
                g = q.leadingBaseCoefficient();
                    z = power( P.ring.coFac, g, delta );
                h = z.divide( power( P.ring.coFac, h, delta-1 )  );
                //System.out.println("g  = " + g);
                //System.out.println("h  = " + h);
            } else {
                r = x;
            }
        }
        //System.out.println("q  = " + q);
        q = basePrimitivePart( q );
        //System.out.println("qp = " + q);
        return (q.multiply(c)).abs(); 
        //return q.abs(); 
    }


    /**
     * Coefficient power.
     * @param A coefficient
     * @param i exponent.
     * @return A^i.
     */
    public C power( RingFactory<C> fac, C A, long i ) {
        if ( i == 0 ) {
           return fac.getONE();
        }
        if ( A == null || A.isZERO() ) {
           return A;
        }
        C p = A;
        for ( long j = 1; j < i; j++ ) {
            p = p.multiply(A);
        }
        return p;
    }


    /**
     * GenPolynomial pseudo remainder.
     * For recursive polynomials.
     * @param P recursive GenPolynomial.
     * @param S nonzero recursive GenPolynomial.
     * @return remainder with ldcf(S)<sup>m</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public GenPolynomial<GenPolynomial<C>> 
           recursivePseudoRemainder( GenPolynomial<GenPolynomial<C>> P, 
                                     GenPolynomial<GenPolynomial<C>> S) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(this.getClass().getName()
                                       + " division by zero");
        }
        if ( P == null || P.isZERO() ) {
            return P;
        }
        if ( S.degree() <= 0 ) {
            return P.ring.getZERO();
        }
        long m = P.degree(0);
        long n = S.degree(0);
        GenPolynomial<C> c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<GenPolynomial<C>> h;
        GenPolynomial<GenPolynomial<C>> r = P; 
        for ( long i = m; i >= n; i-- ) {
            if ( r.isZERO() ) {
               return r;
            }
            long k = r.degree(0);
            if ( i == k ) {
                ExpVector f = r.leadingExpVector();
                GenPolynomial<C> a = r.leadingBaseCoefficient();
                f = ExpVector.EVDIF( f, e );
                //logger.info("red div = " + e);
                //System.out.println("red div = " + f);
                r = r.multiply( c );    // coeff ac
                h = S.multiply( a, f ); // coeff ac
                r = r.subtract( h );
            } else {
                r = r.multiply( c );
            }
        }
        return r;
    }


    /**
     * Univariate GenPolynomial recursive greatest comon divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<GenPolynomial<C>> 
        recursiveGcd( GenPolynomial<GenPolynomial<C>> P,
                      GenPolynomial<GenPolynomial<C>> S ) {
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
        long e = P.degree(0);
        long f = S.degree(0);
        GenPolynomial<GenPolynomial<C>> q;
        GenPolynomial<GenPolynomial<C>> r;
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
        //System.out.println("rgcd r = " + r);
        //System.out.println("rgcd q = " + q);
        GenPolynomial<C> a = recursiveContent(r);
        //System.out.println("rgcd a = " + a);
        GenPolynomial<C> b = recursiveContent(q);
        //System.out.println("rgcd b = " + b);

        GenPolynomial<C> c = gcd(a,b); // go to recursion
        //System.out.println("rgcd c = " + c);
        r = recursiveDivide(r,a);
        q = recursiveDivide(q,b);
        if ( r.isONE() ) {
           return r.multiply(c);
        }
        if ( q.isONE() ) {
           return q.multiply(c);
        }
        if ( q.isConstant() || r.isConstant() ) {
           System.out.println("rgcd q = " + q);
           System.out.println("rgcd r = " + r);
           throw new RuntimeException(this.getClass().getName()
                                       + " error in recursive Content");
        }
        GenPolynomial<C> g = r.ring.getONECoefficient();
        GenPolynomial<C> h = r.ring.getONECoefficient();
        GenPolynomial<GenPolynomial<C>> x;
        GenPolynomial<C> z;
        while ( !r.isZERO() ) {
            long delta = q.degree(0) - r.degree(0);
            //System.out.println("rgcd delta = " + delta);
            //x = recursivePseudoRemainder(q,r);
            x = recursivePseudoRemainder(q,r);
            //System.out.println("rgcd x = " + x);
            q = r;
            if ( !x.isZERO() ) {
                //System.out.println("x  = " + x);
                z = g.multiply( power( P.ring.coFac, h, delta ) );
                //r = recursivePrimitivePart( x );
                //r = x;
                //r = x.divide( z );
                r = recursiveDivide( x, z );
                //System.out.println("rgcd r = " + r);
                g = q.leadingBaseCoefficient();
                    z = power( P.ring.coFac, g, delta );
                h = basePseudoDivide(z, power( P.ring.coFac, h, delta-1 )  );
                //System.out.println("rgcd g  = " + g);
                //System.out.println("rgcd h  = " + h);
            } else {
                r = x;
            }
        }
        //System.out.println("q = " + q);
        q = recursivePrimitivePart( q );
        //System.out.println("q = " + q);
        return q.abs().multiply(c); //.abs();
    }


    /**
     * Polynomial power.
     * @param A polynomial.
     * @param i exponent.
     * @return A^i.
     */
    public GenPolynomial<C> 
           power( RingFactory<GenPolynomial<C>> fac, 
                  GenPolynomial<C> A, 
                  long i ) {
        if ( i == 0 ) {
           return fac.getONE();
        }
        if ( A == null || A.isZERO() ) {
           return A;
        }
        GenPolynomial<C> p = A;
        for ( long j = 1; j < i; j++ ) {
            p = p.multiply(A);
        }
        return p;
    }


    /**
     * Univariate GenPolynomial resultant.
     * Uses pseudoRemainder for remainder.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return res(P,S).
     */
    public GenPolynomial<C> baseResultant( GenPolynomial<C> P,
                                           GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            return S;
        }
        if ( P == null || P.isZERO() ) {
            return P;
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
        GenPolynomial<C> q;
        GenPolynomial<C> r;
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
        C a = baseContent( r );
        C b = baseContent( q );
        r = divide(r,a); // indirection
        q = divide(q,b); // indirection
        //System.out.println("r = " + r);
        //System.out.println("q = " + q);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        RingFactory<C> cofac = P.ring.coFac;
        C g = cofac.getONE();
        C h = cofac.getONE();
        C t = power(cofac,a,e);
        t = t.multiply( power(cofac,b,f) );
        // System.out.println("t = " + t);
        long s = 1;
        GenPolynomial<C> x;
        C z;
        while ( r.degree(0) > 0 ) {
            long delta = q.degree(0) - r.degree(0);
            //System.out.println("delta    = " + delta);
            //System.out.println("deg(r)   = " + r.degree(0));
            //System.out.println("deg(q)   = " + q.degree(0));
            if ( (q.degree(0) % 2 != 0) && (r.degree(0) % 2 != 0) ) {
               s = -s;
            }
            x = basePseudoRemainder(q,r);
            q = r;
            if ( x.degree(0) > 0 ) {
                //System.out.println("x  = " + x);
                z = g.multiply( power( cofac, h, delta ) );
                //System.out.println("z  = " + z);
                r = x.divide( z );
                //System.out.println("x   = " + x);
                //System.out.println("r   = " + r);
                g = q.leadingBaseCoefficient();
                    z = power( cofac, g, delta );
                h = z.divide( power( cofac, h, delta-1 )  );
                //System.out.println("g  = " + g);
                //System.out.println("h  = " + h);
            } else {
                r = x;
            }
        }
        z = power( cofac, r.leadingBaseCoefficient(), q.degree(0) );
        h = z.divide( power( cofac, h, q.degree(0)-1 )  );
        //System.out.println("h  = " + h);
        //System.out.println("t = " + t);
        //System.out.println("s = " + s);
        z = cofac.fromInteger( s );
        z = h.multiply( t ).multiply( z );
        x = P.ring.getONE().multiply( z );
        //System.out.println("x = " + x);
        return x; 
    }


    /**
     * Univariate GenPolynomial recursive resultant.
     * Uses pseudoRemainder for remainder.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return res(P,S).
     */
    public GenPolynomial<GenPolynomial<C>> 
        recursiveResultant( GenPolynomial<GenPolynomial<C>> P,
                            GenPolynomial<GenPolynomial<C>> S ) {
        if ( S == null || S.isZERO() ) {
            return S;
        }
        if ( P == null || P.isZERO() ) {
            return P;
        }
        if ( P.ring.nvar > 1 ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " no univariate polynomial");
        }
        long e = P.degree(0);
        long f = S.degree(0);
        GenPolynomial<GenPolynomial<C>> q;
        GenPolynomial<GenPolynomial<C>> r;
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
        //System.out.println("rgcd r = " + r);
        //System.out.println("rgcd q = " + q);
        GenPolynomial<C> a = recursiveContent(r);
        //System.out.println("rgcd a = " + a);
        GenPolynomial<C> b = recursiveContent(q);
        //System.out.println("rgcd b = " + b);
        r = recursiveDivide(r,a);
        q = recursiveDivide(q,b);
        RingFactory<GenPolynomial<C>> cofac = P.ring.coFac;
        GenPolynomial<C> g = cofac.getONE();
        GenPolynomial<C> h = cofac.getONE();
        GenPolynomial<GenPolynomial<C>> x;
        GenPolynomial<C> t;
        if ( f == 0 && e == 0 && g.ring.nvar > 0 ) { 
           // if coeffs are multivariate (and non constant)
           t = resultant( a, b );
           x = P.ring.getONE().multiply( t );
           return x;
        }
        t = power(cofac,a,e);
        t = t.multiply( power(cofac,b,f) );
        // System.out.println("t = " + t);
        long s = 1;
        GenPolynomial<C> z;
        while ( r.degree(0) > 0 ) {
            long delta = q.degree(0) - r.degree(0);
            //System.out.println("delta    = " + delta);
            //System.out.println("deg(r)   = " + r.degree(0));
            //System.out.println("deg(q)   = " + q.degree(0));
            if ( (q.degree(0) % 2 != 0) && (r.degree(0) % 2 != 0) ) {
               s = -s;
            }
            x = recursivePseudoRemainder(q,r);
            q = r;
            if ( x.degree(0) > 0 ) {
                //System.out.println("x  = " + x);
                z = g.multiply( power( P.ring.coFac, h, delta ) );
                //System.out.println("z  = " + z);
                //r = x.divide( z );
                r = recursiveDivide( x, z );
                //System.out.println("rgcd r = " + r);
                g = q.leadingBaseCoefficient();
                    z = power( cofac, g, delta );
                h = basePseudoDivide(z, power( cofac, h, delta-1 )  );
                //System.out.println("rgcd g  = " + g);
                //System.out.println("rgcd h  = " + h);
            } else {
                r = x;
            }
        }
        z = power( cofac, r.leadingBaseCoefficient(), q.degree(0) );
        h = basePseudoDivide(z, power( cofac, h, q.degree(0)-1 )  );
        //System.out.println("h  = " + h);
        //System.out.println("t = " + t);
        //System.out.println("s = " + s);
        z = cofac.fromInteger( s );
        z = h.multiply( t ).multiply( z );
        x = P.ring.getONE().multiply( z );
        //System.out.println("x = " + x);
        return x;
    }

}
