/*
 * $Id$
 */

package edu.jas.ufd;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;
import edu.jas.structure.Power;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;

import edu.jas.poly.ExpVector;
import edu.jas.poly.Monomial;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;

import edu.jas.util.ListUtil;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;


/**
 * Polynomial ufd utilities, like 
 * conversion between different representations and Hensel lifting.
 * @author Heinz Kredel
 */

public class PolyUfdUtil {


    private static final Logger logger = Logger.getLogger(PolyUfdUtil.class);
    private static boolean debug = logger.isDebugEnabled();


    /**
     * Integral polynomial from rational function coefficients. 
     * Represent as polynomial with integral polynomial coefficients by 
     * multiplication with the lcm of the numerators of the 
     * rational function coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with rational function coefficients to be converted.
     * @return polynomial with integral polynomial coefficients.
     */
    public static <C extends GcdRingElem<C>> 
        GenPolynomial<GenPolynomial<C>>
        integralFromQuotientCoefficients( GenPolynomialRing<GenPolynomial<C>> fac,
                                          GenPolynomial<Quotient<C>> A ) {
        GenPolynomial<GenPolynomial<C>> B = fac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
           return B;
        }
        GenPolynomial<C> c = null;
        GenPolynomial<C> d;
        GenPolynomial<C> x;
        GreatestCommonDivisor<C> ufd = new GreatestCommonDivisorSubres<C>();
        int s = 0;
        // lcm of denominators
        for ( Quotient<C> y: A.getMap().values() ) {
            x = y.den;
            // c = lcm(c,x)
            if ( c == null ) {
               c = x; 
               s = x.signum();
            } else {
               d = ufd.gcd( c, x );
               c = c.multiply( x.divide( d ) );
            }
        }
        if ( s < 0 ) {
           c = c.negate();
        }
        for ( Map.Entry<ExpVector,Quotient<C>> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            Quotient<C> a = y.getValue();
            // p = n*(c/d)
            GenPolynomial<C> b = c.divide( a.den );
            GenPolynomial<C> p = a.num.multiply( b );
            //B = B.sum( p, e ); // inefficient
            B.doPutToMap( e, p ); 
        }
        return B;
    }


    /**
     * Integral polynomial from rational function coefficients. 
     * Represent as polynomial with integral polynomial coefficients by 
     * multiplication with the lcm of the numerators of the 
     * rational function coefficients.
     * @param fac result polynomial factory.
     * @param L list of polynomial with rational function coefficients to be converted.
     * @return list of polynomials with integral polynomial coefficients.
     */
    public static <C extends GcdRingElem<C>> 
        List<GenPolynomial<GenPolynomial<C>>>
        integralFromQuotientCoefficients( GenPolynomialRing<GenPolynomial<C>> fac,
                                          Collection<GenPolynomial<Quotient<C>>> L ) {
        if ( L == null ) {
           return null;
        }
        List<GenPolynomial<GenPolynomial<C>>> list 
            = new ArrayList<GenPolynomial<GenPolynomial<C>>>( L.size() );
        for ( GenPolynomial<Quotient<C>> p : L ) {
            list.add( integralFromQuotientCoefficients(fac,p) );
        }
        return list;
    }


    /**
     * Rational function from integral polynomial coefficients. 
     * Represent as polynomial with type Quotient<C> coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with integral polynomial coefficients to be converted.
     * @return polynomial with type Quotient<C> coefficients.
     */
    public static <C extends GcdRingElem<C>>
        GenPolynomial<Quotient<C>> 
        quotientFromIntegralCoefficients( GenPolynomialRing<Quotient<C>> fac,
                                          GenPolynomial<GenPolynomial<C>> A ) {
        GenPolynomial<Quotient<C>> B = fac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
           return B;
        }
        RingFactory<Quotient<C>> cfac = fac.coFac;
        QuotientRing<C> qfac = (QuotientRing<C>)cfac; 
        for ( Map.Entry<ExpVector,GenPolynomial<C>> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            Quotient<C> p = new Quotient<C>(qfac, a); // can not be zero
            if ( p != null && !p.isZERO() ) {
               //B = B.sum( p, e ); // inefficient
               B.doPutToMap( e, p ); 
            }
        }
        return B;
    }


    /**
     * Rational function from integral polynomial coefficients. 
     * Represent as polynomial with type Quotient<C> coefficients.
     * @param fac result polynomial factory.
     * @param L list of polynomials with integral polynomial 
     *        coefficients to be converted.
     * @return list of polynomials with type Quotient<C> coefficients.
     */
    public static <C extends GcdRingElem<C>>
        List<GenPolynomial<Quotient<C>>> 
        quotientFromIntegralCoefficients( GenPolynomialRing<Quotient<C>> fac,
                                          Collection<GenPolynomial<GenPolynomial<C>>> L ) {
        if ( L == null ) {
           return null;
        }
        List<GenPolynomial<Quotient<C>>> list 
            = new ArrayList<GenPolynomial<Quotient<C>>>( L.size() );
        for ( GenPolynomial<GenPolynomial<C>> p : L ) {
            list.add( quotientFromIntegralCoefficients(fac,p) );
        }
        return list;
    }


    /**
     * From BigInteger coefficients. 
     * Represent as polynomial with type GenPolynomial&lt;C&gt; coefficients,
     * e.g. ModInteger or BigRational.
     * @param fac result polynomial factory.
     * @param A polynomial with GenPolynomial&lt;BigInteger&gt; coefficients to be converted.
     * @return polynomial with type GenPolynomial&lt;C&gt; coefficients.
     */
    public static <C extends RingElem<C>>
        GenPolynomial<GenPolynomial<C>> 
        fromIntegerCoefficients( GenPolynomialRing<GenPolynomial<C>> fac,
                                 GenPolynomial<GenPolynomial<BigInteger>> A ) {
        GenPolynomial<GenPolynomial<C>> B = fac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
           return B;
        }
        RingFactory<GenPolynomial<C>> cfac = fac.coFac;
        GenPolynomialRing<C> rfac = (GenPolynomialRing<C>)cfac;
        for ( Map.Entry<ExpVector,GenPolynomial<BigInteger>> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            GenPolynomial<BigInteger> a = y.getValue();
            GenPolynomial<C> p = PolyUtil.<C>fromIntegerCoefficients( rfac, a );
            if ( p != null && !p.isZERO() ) {
               //B = B.sum( p, e ); // inefficient
               B.doPutToMap( e, p ); 
            }
        }
        return B;
    }


    /**
     * From BigInteger coefficients. 
     * Represent as polynomial with type GenPolynomial&lt;C&gt; coefficients,
     * e.g. ModInteger or BigRational.
     * @param fac result polynomial factory.
     * @param L polynomial list with GenPolynomial&lt;BigInteger&gt; 
     * coefficients to be converted.
     * @return polynomial list with polynomials with 
     * type GenPolynomial&lt;C&gt; coefficients.
     */
    public static <C extends RingElem<C>>
        List<GenPolynomial<GenPolynomial<C>>> 
        fromIntegerCoefficients( GenPolynomialRing<GenPolynomial<C>> fac,
                                 List<GenPolynomial<GenPolynomial<BigInteger>>> L ) {
        List<GenPolynomial<GenPolynomial<C>>> K = null;
        if ( L == null ) {
           return K;
        }
        K = new ArrayList<GenPolynomial<GenPolynomial<C>>>( L.size() );
        if ( L.size() == 0 ) {
           return K;
        }
        for ( GenPolynomial<GenPolynomial<BigInteger>> a: L ) {
            GenPolynomial<GenPolynomial<C>> b 
               = fromIntegerCoefficients( fac, a );
            K.add( b );
        }
        return K;
    }


    /**
     * Introduce lower variable. 
     * Represent as polynomial with type GenPolynomial&lt;C&gt; coefficients.
     * @param rfac result polynomial factory.
     * @param A polynomial to be extended.
     * @return polynomial with type GenPolynomial&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C>>
      GenPolynomial<GenPolynomial<C>> 
      introduceLowerVariable( GenPolynomialRing<GenPolynomial<C>> rfac,
                              GenPolynomial<C> A ) {
        if ( A == null || rfac == null ) {
            return null;
        }
        GenPolynomial<GenPolynomial<C>> Pc = rfac.getONE().multiply(A);
        if ( Pc.isZERO() ) {
            return Pc;
        }
        Pc = PolyUtil.<C> switchVariables(Pc);
        return Pc;
    }


    /**
     * From AlgebraicNumber coefficients. 
     * Represent as polynomial with type GenPolynomial&lt;C&gt; coefficients,
     * e.g. ModInteger or BigRational.
     * @param rfac result polynomial factory.
     * @param A polynomial with AlgebraicNumber coefficients to be converted.
     * @param k for (y-k x) substitution.
     * @return polynomial with type GenPolynomial&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C>>
      GenPolynomial<GenPolynomial<C>> 
      substituteFromAlgebraicCoefficients( GenPolynomialRing<GenPolynomial<C>> rfac,
                                           GenPolynomial<AlgebraicNumber<C>> A, long k ) {
        if ( A == null || rfac == null ) {
            return null;
        }
        if ( A.isZERO() ) {
            return rfac.getZERO();
        }
        // setup x - k alpha
        GenPolynomialRing<AlgebraicNumber<C>> apfac = A.ring;
        GenPolynomial<AlgebraicNumber<C>> x = apfac.univariate(0);
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) A.ring.coFac;
        AlgebraicNumber<C> alpha = afac.getGenerator();
        AlgebraicNumber<C> ka = afac.fromInteger(k);
        GenPolynomial<AlgebraicNumber<C>> s = x.subtract( ka.multiply( alpha) ); // x - k alpha
        // substitute, convert and switch
        GenPolynomial<AlgebraicNumber<C>> B = PolyUtil.<AlgebraicNumber<C>> substituteMain(A,s); 
        GenPolynomial<GenPolynomial<C>> Pc  
           = PolyUtil.<C> fromAlgebraicCoefficients(rfac,B); // Q[alpha][x]
        Pc = PolyUtil.<C> switchVariables(Pc); // Q[x][alpha]
        return Pc;
    }


  /**
     * Convert to AlgebraicNumber coefficients. 
     * Represent as polynomial with AlgebraicNumber<C> coefficients,
     * C is e.g. ModInteger or BigRational.
     * @param pfac result polynomial factory.
     * @param A polynomial with GenPolynomial&lt;BigInteger&gt; 
     * coefficients to be converted.
     * @param k for (y-k x) substitution.
     * @return polynomial with AlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C>>
      GenPolynomial<AlgebraicNumber<C>> 
      substituteConvertToAlgebraicCoefficients( GenPolynomialRing<AlgebraicNumber<C>> pfac,
                                                GenPolynomial<C> A, long k ) {
        if ( A == null || pfac == null ) {
            return null;
        }
        if ( A.isZERO() ) {
            return pfac.getZERO();
        }
        // convert to Q(alpha)[x]
        GenPolynomial<AlgebraicNumber<C>> B = PolyUtil.<C> convertToAlgebraicCoefficients(pfac,A);
        // setup x _+_ k alpha for back substitution
        GenPolynomial<AlgebraicNumber<C>> x = pfac.univariate(0);
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) pfac.coFac;
        AlgebraicNumber<C> alpha = afac.getGenerator();
        AlgebraicNumber<C> ka = afac.fromInteger(k);
        GenPolynomial<AlgebraicNumber<C>> s = x.sum( ka.multiply( alpha) ); // x + k alpha
        // substitute
        GenPolynomial<AlgebraicNumber<C>> N = PolyUtil.<AlgebraicNumber<C>> substituteMain(B,s); 
        return N;
    }


    /**
     * Norm of a polynomial with AlgebraicNumber coefficients. 
     * @param A polynomial from GenPolynomial&lt;AlgebraicNumber&lt;C&gt;&gt;.
     * @param k for (y - k x) substitution.
     * @return norm(A) = res_x(A(x,y),m(x)) in GenPolynomial&lt;C&gt;.
     */
    public static <C extends GcdRingElem<C>>
      GenPolynomial<C> norm( GenPolynomial<AlgebraicNumber<C>> A, long k ) {
        if ( A == null ) {
            return null;
        }
        GenPolynomialRing<AlgebraicNumber<C>> pfac = A.ring; // Q(alpha)[x]
        if ( pfac.nvar > 1 ) {
            throw new RuntimeException("only for univariate polynomials");
        }
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>)pfac.coFac;
        GenPolynomial<C> agen = afac.modul;
        GenPolynomialRing<C> cfac = afac.ring;
        if ( A.isZERO() ) {
            return cfac.getZERO();
        }
        AlgebraicNumber<C> ldcf = A.leadingBaseCoefficient();
        if ( !ldcf.isONE() ) {
            A = A.monic();
        }
        GenPolynomialRing<GenPolynomial<C>> rfac 
             = new GenPolynomialRing<GenPolynomial<C>>(cfac,pfac);

        // transform minimal polynomial to bi-variate polynomial
        GenPolynomial<GenPolynomial<C>> Ac 
            = PolyUfdUtil.<C>  introduceLowerVariable(rfac,agen);
        System.out.println("Ac = " + Ac);

        // transform to bi-variate polynomial, 
        // switching varaible sequence from Q[alpha][x] to Q[X][alpha]
        GenPolynomial<GenPolynomial<C>> Pc          
           = PolyUfdUtil.<C>  substituteFromAlgebraicCoefficients( rfac, A, k );
        Pc = PolyUtil.<C>monic(Pc);
        System.out.println("Pc = " + Pc);

        GreatestCommonDivisorSubres<C> engine 
            = new GreatestCommonDivisorSubres<C>( /*cfac.coFac*/ );
              // = (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getImplementation( cfac.coFac );

        GenPolynomial<GenPolynomial<C>> Rc = engine.recursiveResultant(Pc,Ac);
        System.out.println("Rc = " + Rc);
        GenPolynomial<C> res = Rc.leadingBaseCoefficient();
        res = res.monic();
        return res;
    }


    /**
     * Norm of a polynomial with AlgebraicNumber coefficients. 
     * @param A polynomial from GenPolynomial&lt;AlgebraicNumber&lt;C&gt;&gt;.
     * @return norm(A) = resultant_x( A(x,y), m(x) ) in K[y].
     */
    public static <C extends GcdRingElem<C>>
      GenPolynomial<C> norm( GenPolynomial<AlgebraicNumber<C>> A ) {
        return norm(A,0L);
    }


    /** ModInteger Hensel lifting algorithm on coefficients.
     * Let p = A.ring.coFac.modul() = B.ring.coFac.modul() 
     * and assume C == A*B mod p with ggt(A,B) == 1 mod p and
     * S A + T B == 1 mod p. 
     * See Algorithm 6.1. in Geddes et.al.. 
     * Linear version, as it does not lift S A + T B == 1 mod p^{e+1}.
     * @param C GenPolynomial<BigInteger>.
     * @param A GenPolynomial<ModInteger>.
     * @param B other GenPolynomial<ModInteger>.
     * @param S GenPolynomial<ModInteger>.
     * @param T GenPolynomial<ModInteger>.
     * @param M bound on the coefficients of A1 and B1 as factors of C.
     * @return [A1,B1] = lift(C,A,B), with C = A1 * B1.
     */
    @SuppressWarnings("unchecked") 
    public static //<C extends RingElem<C>>
        GenPolynomial<BigInteger>[] 
        liftHensel( GenPolynomial<BigInteger> C,
                    BigInteger M,
                    GenPolynomial<ModInteger> A,
                    GenPolynomial<ModInteger> B, 
                    GenPolynomial<ModInteger> S,
                    GenPolynomial<ModInteger> T ) {
        GenPolynomial<BigInteger>[] AB = (GenPolynomial<BigInteger>[])new GenPolynomial[2];
        if ( C == null || C.isZERO() ) {
           AB[0] = C;
           AB[1] = C;
           return AB;
        }
        if ( A == null || A.isZERO() || B == null || B.isZERO() ) {
           throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if ( fac.nvar != 1 ) { // todo assert
           throw new RuntimeException("polynomial ring not univariate");
        }
        // setup factories
        GenPolynomialRing<ModInteger> pfac = A.ring;
        RingFactory<ModInteger> p = pfac.coFac;
        RingFactory<ModInteger> q = p;
        ModIntegerRing P = (ModIntegerRing)p;
        ModIntegerRing Q = (ModIntegerRing)q;
        BigInteger Qi = new BigInteger( Q.getModul() );
        BigInteger M2 = M.multiply( M.fromInteger(2) );
        BigInteger Mq = Qi;

        // normalize c and a, b factors, assert p is prime
        GenPolynomial<BigInteger> Ai;
        GenPolynomial<BigInteger> Bi;
        BigInteger c = C.leadingBaseCoefficient();
        C = C.multiply(c); // sic
        ModInteger a = A.leadingBaseCoefficient();
        if ( !a.isONE() ) { // A = A.monic();
           A = A.divide( a );
           S = S.multiply( a );
        }
        ModInteger b = B.leadingBaseCoefficient();
        if ( !b.isONE() ) { // B = B.monic();
           B = B.divide( b );
           T = T.multiply( b );
        }
        ModInteger ci = P.fromInteger( c.getVal() );
        A = A.multiply( ci );
        B = B.multiply( ci );
        T = T.divide( ci );
        S = S.divide( ci );
        Ai = PolyUtil.integerFromModularCoefficients( fac, A );
        Bi = PolyUtil.integerFromModularCoefficients( fac, B );
        // replace leading base coefficients
        ExpVector ea = Ai.leadingExpVector();
        ExpVector eb = Bi.leadingExpVector();
        Ai.doPutToMap(ea,c);
        Bi.doPutToMap(eb,c);

        // polynomials mod p
        GenPolynomial<ModInteger> Ap; 
        GenPolynomial<ModInteger> Bp;
        GenPolynomial<ModInteger> A1p; 
        GenPolynomial<ModInteger> B1p;
        GenPolynomial<ModInteger> Ep;

        // polynomials over the integers
        GenPolynomial<BigInteger> E;
        GenPolynomial<BigInteger> Ea;
        GenPolynomial<BigInteger> Eb;
        GenPolynomial<BigInteger> Ea1;
        GenPolynomial<BigInteger> Eb1;

        while ( Mq.compareTo( M2 ) < 0 ) {
            // compute E=(C-AB)/q over the integers
            E = C.subtract( Ai.multiply(Bi) );
            if ( E.isZERO() ) {
               //System.out.println("leaving on zero error");
               logger.info("leaving on zero error");
               break;
            }
            E = E.divide( Qi );
            // E mod p
            Ep = PolyUtil.<ModInteger>fromIntegerCoefficients(pfac,E); 
            //logger.info("Ep = " + Ep);

            // construct approximation mod p
            Ap = S.multiply( Ep ); // S,T ++ T,S
            Bp = T.multiply( Ep );
            GenPolynomial<ModInteger>[] QR;
            QR = Ap.divideAndRemainder( B );
            GenPolynomial<ModInteger> Qp;
            GenPolynomial<ModInteger> Rp;
            Qp = QR[0];
            Rp = QR[1];
            A1p = Rp;
            B1p = Bp.sum( A.multiply( Qp ) );

            // construct q-adic approximation, convert to integer
            Ea = PolyUtil.integerFromModularCoefficients(fac,A1p);
            Eb = PolyUtil.integerFromModularCoefficients(fac,B1p);
            Ea1 = Ea.multiply( Qi );
            Eb1 = Eb.multiply( Qi );

            Ea = Ai.sum( Eb1 ); // Eb1 and Ea1 are required
            Eb = Bi.sum( Ea1 ); //--------------------------
            assert ( Ea.degree(0)+Eb.degree(0) <= C.degree(0) );
            //if ( Ea.degree(0)+Eb.degree(0) > C.degree(0) ) { // debug
            //   throw new RuntimeException("deg(A)+deg(B) > deg(C)");
            //}

            // prepare for next iteration
            Mq = Qi;
            Qi = new BigInteger( Q.getModul().multiply( P.getModul() ) );
            Q = new ModIntegerRing( Qi.getVal() );
            Ai = Ea;
            Bi = Eb;
        }
        GreatestCommonDivisorAbstract<BigInteger> ufd
            = new GreatestCommonDivisorPrimitive<BigInteger>();

        // remove normalization
        BigInteger ai = ufd.baseContent(Ai);
        Ai = Ai.divide( ai );
        BigInteger bi = c.divide(ai);
        Bi = Bi.divide( bi ); // divide( c/a )
        AB[0] = Ai;
        AB[1] = Bi;
        return AB;
    }


    /** ModInteger Hensel lifting algorithm on coefficients.
     * Let p = A.ring.coFac.modul() = B.ring.coFac.modul() 
     * and assume C == A*B mod p with ggt(A,B) == 1 mod p and
     * S A + T B == 1 mod p. 
     * See algorithm 6.1. in Geddes et.al. and algorithms 3.5.{5,6} in Cohen. 
     * Quadratic version, as it also lifts S A + T B == 1 mod p^{e+1}.
     * @param C GenPolynomial<BigInteger>.
     * @param A GenPolynomial<ModInteger>.
     * @param B other GenPolynomial<ModInteger>.
     * @param S GenPolynomial<ModInteger>.
     * @param T GenPolynomial<ModInteger>.
     * @param M bound on the coefficients of A1 and B1 as factors of C.
     * @return [A1,B1] = lift(C,A,B), with C = A1 * B1.
     */
    @SuppressWarnings("unchecked") 
    public static //<C extends RingElem<C>>
        GenPolynomial<BigInteger>[] 
        liftHenselQuadratic( GenPolynomial<BigInteger> C,
                             BigInteger M,
                             GenPolynomial<ModInteger> A,
                             GenPolynomial<ModInteger> B, 
                             GenPolynomial<ModInteger> S,
                             GenPolynomial<ModInteger> T ) {
        GenPolynomial<BigInteger>[] AB = (GenPolynomial<BigInteger>[])new GenPolynomial[2];
        if ( C == null || C.isZERO() ) {
           AB[0] = C;
           AB[1] = C;
           return AB;
        }
        if ( A == null || A.isZERO() || B == null || B.isZERO() ) {
           throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if ( fac.nvar != 1 ) { // todo assert
           throw new RuntimeException("polynomial ring not univariate");
        }
        // setup factories
        GenPolynomialRing<ModInteger> pfac = A.ring;
        RingFactory<ModInteger> p = pfac.coFac;
        RingFactory<ModInteger> q = p;
        ModIntegerRing P = (ModIntegerRing)p;
        ModIntegerRing Q = (ModIntegerRing)q;
        BigInteger Qi = new BigInteger( Q.getModul() );
        BigInteger M2 = M.multiply( M.fromInteger(2) );
        BigInteger Mq = Qi;
        GenPolynomialRing<ModInteger> qfac;
        qfac = new GenPolynomialRing<ModInteger>(Q,pfac);
        GenPolynomialRing<ModInteger> mfac;
        BigInteger Qm = new BigInteger( Q.getModul().multiply( Q.getModul() ) );
        ModIntegerRing Qmm = new ModIntegerRing( Qm.getVal() );
        mfac = new GenPolynomialRing<ModInteger>(Qmm,qfac);

        // normalize c and a, b factors, assert p is prime
        GenPolynomial<BigInteger> Ai;
        GenPolynomial<BigInteger> Bi;
        BigInteger c = C.leadingBaseCoefficient();
        C = C.multiply(c); // sic
        ModInteger a = A.leadingBaseCoefficient();
        if ( !a.isONE() ) { // A = A.monic();
           A = A.divide( a );
           S = S.multiply( a );
        }
        ModInteger b = B.leadingBaseCoefficient();
        if ( !b.isONE() ) { // B = B.monic();
           B = B.divide( b );
           T = T.multiply( b );
        }
        ModInteger ci = P.fromInteger( c.getVal() );
        A = A.multiply( ci );
        B = B.multiply( ci );
        T = T.divide( ci );
        S = S.divide( ci );
        Ai = PolyUtil.integerFromModularCoefficients( fac, A );
        Bi = PolyUtil.integerFromModularCoefficients( fac, B );
        // replace leading base coefficients
        ExpVector ea = Ai.leadingExpVector();
        ExpVector eb = Bi.leadingExpVector();
        Ai.doPutToMap(ea,c);
        Bi.doPutToMap(eb,c);

        // polynomials mod p
        GenPolynomial<ModInteger> Ap; 
        GenPolynomial<ModInteger> Bp;
        GenPolynomial<ModInteger> A1p; 
        GenPolynomial<ModInteger> B1p;
        GenPolynomial<ModInteger> Ep;
        GenPolynomial<ModInteger> Sp = S;
        GenPolynomial<ModInteger> Tp = T;

        // polynomials mod q
        GenPolynomial<ModInteger> Aq; 
        GenPolynomial<ModInteger> Bq;
        GenPolynomial<ModInteger> Eq;

        // polynomials over the integers
        GenPolynomial<BigInteger> E;
        GenPolynomial<BigInteger> Ea;
        GenPolynomial<BigInteger> Eb;
        GenPolynomial<BigInteger> Ea1;
        GenPolynomial<BigInteger> Eb1;
        GenPolynomial<BigInteger> Si;
        GenPolynomial<BigInteger> Ti;

        Si = PolyUtil.integerFromModularCoefficients( fac, S );
        Ti = PolyUtil.integerFromModularCoefficients( fac, T );

        Aq = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Ai); 
        Bq = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Bi); 

        while ( Mq.compareTo( M2 ) < 0 ) {
            // compute E=(C-AB)/q over the integers
            E = C.subtract( Ai.multiply(Bi) );
            if ( E.isZERO() ) {
               //System.out.println("leaving on zero error");
               logger.info("leaving on zero error");
               break;
            }
            E = E.divide( Qi );
            // E mod p
            Ep = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,E); 
            //logger.info("Ep = " + Ep);
            if ( Ep.isZERO() ) {
               //System.out.println("leaving on zero error");
               logger.info("leaving on zero error Ep");
               break;
            }

            // construct approximation mod p
            Ap = Sp.multiply( Ep ); // S,T ++ T,S
            Bp = Tp.multiply( Ep );
            GenPolynomial<ModInteger>[] QR;
            QR = Ap.divideAndRemainder( Bq );
            GenPolynomial<ModInteger> Qp;
            GenPolynomial<ModInteger> Rp;
            Qp = QR[0];
            Rp = QR[1];
            A1p = Rp;
            B1p = Bp.sum( Aq.multiply( Qp ) );

            // construct q-adic approximation, convert to integer
            Ea = PolyUtil.integerFromModularCoefficients(fac,A1p);
            Eb = PolyUtil.integerFromModularCoefficients(fac,B1p);
            Ea1 = Ea.multiply( Qi );
            Eb1 = Eb.multiply( Qi );
            Ea = Ai.sum( Eb1 ); // Eb1 and Ea1 are required
            Eb = Bi.sum( Ea1 ); //--------------------------
            assert ( Ea.degree(0)+Eb.degree(0) <= C.degree(0) );
            //if ( Ea.degree(0)+Eb.degree(0) > C.degree(0) ) { // debug
            //   throw new RuntimeException("deg(A)+deg(B) > deg(C)");
            //}
            Ai = Ea;
            Bi = Eb;

            // gcd representation factors error --------------------------------
            // compute E=(1-SA-TB)/q over the integers
            E = fac.getONE();
            E = E.subtract( Si.multiply(Ai) ).subtract( Ti.multiply(Bi) );
            E = E.divide( Qi );
            // E mod q
            Ep = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,E); 
            //logger.info("Ep2 = " + Ep);

            // construct approximation mod q
            Ap = Sp.multiply( Ep ); // S,T ++ T,S
            Bp = Tp.multiply( Ep );
            QR = Bp.divideAndRemainder( Aq ); // Ai == A mod p ?
            Qp = QR[0];
            Rp = QR[1];
            B1p = Rp;
            A1p = Ap.sum( Bq.multiply( Qp ) );

            if ( false && debug ) {
               Eq = A1p.multiply(Aq).sum(B1p.multiply(Bq)).subtract(Ep);
               if ( !Eq.isZERO() ) {
                  System.out.println("A*A1p+B*B1p-Ep2 != 0 " + Eq );
                  throw new RuntimeException("A*A1p+B*B1p-Ep2 != 0 mod " + Q.getModul());
               }
            }

            // construct q-adic approximation, convert to integer
            Ea = PolyUtil.integerFromModularCoefficients(fac,A1p);
            Eb = PolyUtil.integerFromModularCoefficients(fac,B1p);
            Ea1 = Ea.multiply( Qi );
            Eb1 = Eb.multiply( Qi );
            Ea = Si.sum( Ea1 ); // Eb1 and Ea1 are required
            Eb = Ti.sum( Eb1 ); //--------------------------
            Si = Ea;
            Ti = Eb;

            // prepare for next iteration
            //Qi = new BigInteger( Q.getModul().multiply( P.getModul() ) );
            Mq = Qi;
            Qi = new BigInteger( Q.getModul().multiply( Q.getModul() ) );
            Q = new ModIntegerRing( Qi.getVal() );
            qfac = new GenPolynomialRing<ModInteger>(Q,pfac);
            Qm = new BigInteger( Q.getModul().multiply( Q.getModul() ) );
            Qmm = new ModIntegerRing( Qm.getVal() );
            mfac = new GenPolynomialRing<ModInteger>(Qmm,qfac);

            Aq = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Ai);
            Bq = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Bi);
            Sp = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Si);
            Tp = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Ti);
            if ( false && debug ) {
               E = Ai.multiply(Si).sum( Bi.multiply(Ti) );
               Eq = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,E); 
               if ( !Eq.isONE() ) {
                  System.out.println("Ai*Si+Bi*Ti=1 " + Eq );
                  throw new RuntimeException("Ai*Si+Bi*Ti != 1 mod " + Q.getModul());
               }
            }
        }
        GreatestCommonDivisorAbstract<BigInteger> ufd
            = new GreatestCommonDivisorPrimitive<BigInteger>();

        // remove normalization
        BigInteger ai = ufd.baseContent(Ai);
        Ai = Ai.divide( ai );
        BigInteger bi = c.divide(ai);
        try {
            Bi = Bi.divide( bi ); // divide( c/a )
        } catch ( Exception e ) {
            System.out.println("C  = " + C );
            System.out.println("Ai = " + Ai );
            System.out.println("Bi = " + Bi );
            System.out.println("c  = " + c );
            System.out.println("ai = " + ai );
            System.out.println("bi = " + bi );
        }

        AB[0] = Ai;
        AB[1] = Bi;
        return AB;
    }


    /** ModInteger Hensel lifting algorithm on coefficients.
     * Let p = A.ring.coFac.modul() = B.ring.coFac.modul() 
     * and assume C == A*B mod p with ggt(A,B) == 1 mod p. 
     * See algorithm 6.1. in Geddes et.al. and algorithms 3.5.{5,6} in Cohen. 
     * Quadratic version.
     * @param C GenPolynomial<BigInteger>.
     * @param A GenPolynomial<ModInteger>.
     * @param B other GenPolynomial<ModInteger>.
     * @param M bound on the coefficients of A1 and B1 as factors of C.
     * @return [A1,B1] = lift(C,A,B), with C = A1 * B1.
     */
    @SuppressWarnings("unchecked") 
    public static //<C extends RingElem<C>>
        GenPolynomial<BigInteger>[] 
        liftHenselQuadratic( GenPolynomial<BigInteger> C,
                             BigInteger M,
                             GenPolynomial<ModInteger> A,
                             GenPolynomial<ModInteger> B ) {
        GenPolynomial<BigInteger>[] AB = (GenPolynomial<BigInteger>[])new GenPolynomial[2];
        if ( C == null || C.isZERO() ) {
           AB[0] = C;
           AB[1] = C;
           return AB;
        }
        if ( A == null || A.isZERO() || B == null || B.isZERO() ) {
           throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if ( fac.nvar != 1 ) { // todo assert
           throw new RuntimeException("polynomial ring not univariate");
        }
        // one Hensel step on part polynomials
        GenPolynomial<ModInteger>[] gst = A.egcd( B );
        if ( ! gst[0].isONE() ) {
           throw new RuntimeException("A and B not coprime, gcd = " + gst[0] 
                                      + ", A = " + A + ", B = " + B);
        }
        GenPolynomial<ModInteger> s = gst[1];
        GenPolynomial<ModInteger> t = gst[2];
        GenPolynomial<BigInteger>[] ab = liftHenselQuadratic(C,M,A,B,s,t);
        return ab;
    }


    /** ModInteger Hensel lifting algorithm on coefficients.
     * Let p = A.ring.coFac.modul() = B.ring.coFac.modul() 
     * and assume C == A*B mod p with ggt(A,B) == 1 mod p. 
     * See algorithm 6.1. in Geddes et.al. and algorithms 3.5.{5,6} in Cohen. 
     * Quadratic version.
     * @param C GenPolynomial<BigInteger>.
     * @param A GenPolynomial<ModInteger>.
     * @param B other GenPolynomial<ModInteger>.
     * @param M bound on the coefficients of A1 and B1 as factors of C.
     * @return [A1,B1] = lift(C,A,B), with C = A1 * B1.
     */
    @SuppressWarnings("unchecked") 
    public static //<C extends RingElem<C>>
        GenPolynomial<BigInteger>[] 
        liftHenselQuadraticFac( GenPolynomial<BigInteger> C,
                                BigInteger M,
                                GenPolynomial<ModInteger> A,
                                GenPolynomial<ModInteger> B ) {
        GenPolynomial<BigInteger>[] AB = (GenPolynomial<BigInteger>[])new GenPolynomial[2];
        if ( C == null || C.isZERO() ) {
           throw new RuntimeException("C must be nonzero");
        }
        if ( A == null || A.isZERO() || B == null || B.isZERO() ) {
           throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if ( fac.nvar != 1 ) { // todo assert
           throw new RuntimeException("polynomial ring not univariate");
        }
        // one Hensel step on part polynomials
        GenPolynomial<ModInteger>[] gst = A.egcd( B );
        if ( ! gst[0].isONE() ) {
           throw new RuntimeException("A and B not coprime, gcd = " + gst[0] 
                                      + ", A = " + A + ", B = " + B);
        }
        GenPolynomial<ModInteger> s = gst[1];
        GenPolynomial<ModInteger> t = gst[2];
        GenPolynomial<BigInteger>[] ab = liftHenselQuadraticFac(C,M,A,B,s,t);
        return ab;
    }


    /** ModInteger Hensel lifting algorithm on coefficients.
     * Let p = f_i.ring.coFac.modul() i = 0, ..., n-1
     * and assume C == prod_{0,...,n-1} f_i mod p with ggt(f_i,f_j) == 1 mod p for i != j
     * @param C GenPolynomial<BigInteger>.
     * @param F = [f_0,...,f_{n-1}] List<GenPolynomial<ModInteger>>.
     * @param M bound on the coefficients of g_i as factors of C.
     * @return [g_0,...,g_{n-1}] = lift(C,F), with C = prod_{0,...,n-1} g_i mod p**e.
     */
    public static //<C extends RingElem<C>>
        List<GenPolynomial<BigInteger>>
        liftHenselQuadratic( GenPolynomial<BigInteger> C,
                             BigInteger M,
                             List<GenPolynomial<ModInteger>> F) {
        if ( C == null || C.isZERO() || F == null || F.size() == 0 ) {
           throw new RuntimeException("C must be nonzero and F must be nonempty");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if ( fac.nvar != 1 ) { // todo assert
           throw new RuntimeException("polynomial ring not univariate");
        }
        List<GenPolynomial<BigInteger>> lift = new ArrayList<GenPolynomial<BigInteger>>( F.size() );
        int n = F.size();
        if ( n == 1 ) { // use C itself
            lift.add( C );
            return lift;
        }
        if ( n == 2 ) { // only one step
            GenPolynomial<BigInteger>[] ab = liftHenselQuadraticFac(C,M,F.get(0),F.get(1));
            lift.add( ab[0] );
            lift.add( ab[1] );
            return lift;
        }

        BigInteger lc = C.leadingBaseCoefficient();
        GenPolynomial<ModInteger> f = F.get(0);
        GenPolynomialRing<ModInteger> mfac = f.ring;
        ModIntegerRing mr = (ModIntegerRing)mfac.coFac;
        BigInteger P = new BigInteger( mr.modul );
        // split list in two parts and prepare polynomials
        int k = n/2;
        List<GenPolynomial<ModInteger>> F1 = new ArrayList<GenPolynomial<ModInteger>>( k );
        GenPolynomial<ModInteger> A = mfac.getONE();
        ModInteger mlc = mr.fromInteger( lc.getVal() );
        A = A.multiply( mlc );
        for ( int i = 0; i < k; i++ ) {
            GenPolynomial<ModInteger> fi = F.get(i);
            if ( fi != null && !fi.isZERO() ) {
                A = A.multiply( fi );
                F1.add( fi );
            } else {
                System.out.println("A = " + A + ", fi = " + fi);
            }
        }
        List<GenPolynomial<ModInteger>> F2 = new ArrayList<GenPolynomial<ModInteger>>( k );
        GenPolynomial<ModInteger> B = mfac.getONE();
        for ( int i = k; i < n; i++ ) {
            GenPolynomial<ModInteger> fi = F.get(i);
            if ( fi != null && !fi.isZERO() ) {
                B = B.multiply( fi );
                F2.add( fi );
            } else {
                System.out.println("B = " + B + ", fi = " + fi);
            }
        }
        // one Hensel step on part polynomials
        GenPolynomial<BigInteger>[] ab = liftHenselQuadraticFac(C,M,A,B);
        GenPolynomial<BigInteger> A1 = ab[0];
        GenPolynomial<BigInteger> B1 = ab[1];
        if ( ! isHenselLift(C,M,P,A1,B1) ) {
            System.out.println("A = " + A + ", F1 = " + F1);
            System.out.println("B = " + B + ", F2 = " + F2);
            System.out.println("A1 = " + A1 + ", B1 = " + B1);
            //throw new RuntimeException("no lifting A1, B1");
        }
        BigInteger Mh = M.divide( new BigInteger(2) );
        // recursion on list parts
        List<GenPolynomial<BigInteger>> G1 = liftHenselQuadratic(A1,Mh,F1);
        if ( ! isHenselLift(A1,Mh,P,G1) ) {
            System.out.println("A = " + A + ", F1 = " + F1);
            System.out.println("B = " + B + ", F2 = " + F2);
            System.out.println("G1 = " + G1);
            //throw new RuntimeException("no lifting G1");
        }
        List<GenPolynomial<BigInteger>> G2 = liftHenselQuadratic(B1,Mh,F2);
        if ( ! isHenselLift(B1,Mh,P,G2) ) {
            System.out.println("A = " + A + ", F1 = " + F1);
            System.out.println("B = " + B + ", F2 = " + F2);
            System.out.println("G2 = " + G2);
            //throw new RuntimeException("no lifting G2");
        }
        lift.addAll( G1 );
        lift.addAll( G2 );
        return lift;
    }


    /** ModInteger Hensel lifting algorithm on coefficients.
     * Let p = A.ring.coFac.modul() = B.ring.coFac.modul() 
     * and assume C == A*B mod p with ggt(A,B) == 1 mod p and
     * S A + T B == 1 mod p. 
     * See algorithm 6.1. in Geddes et.al. and algorithms 3.5.{5,6} in Cohen. 
     * Quadratic version, as it also lifts S A + T B == 1 mod p^{e+1}.
     * @param C GenPolynomial<BigInteger>.
     * @param A GenPolynomial<ModInteger>.
     * @param B other GenPolynomial<ModInteger>.
     * @param S GenPolynomial<ModInteger>.
     * @param T GenPolynomial<ModInteger>.
     * @param M bound on the coefficients of A1 and B1 as factors of C.
     * @return [A1,B1] = lift(C,A,B), with C = A1 * B1.
     */
    @SuppressWarnings("unchecked") 
    public static //<C extends RingElem<C>>
        GenPolynomial<BigInteger>[] 
        liftHenselQuadraticFac( GenPolynomial<BigInteger> C,
                                BigInteger M,
                                GenPolynomial<ModInteger> A,
                                GenPolynomial<ModInteger> B, 
                                GenPolynomial<ModInteger> S,
                                GenPolynomial<ModInteger> T ) {
        System.out.println("*** version for factorization *** ");
        GenPolynomial<BigInteger>[] AB = (GenPolynomial<BigInteger>[])new GenPolynomial[2];
        if ( C == null || C.isZERO() ) {
           throw new RuntimeException("C must be nonzero");
        }
        if ( A == null || A.isZERO() || B == null || B.isZERO() ) {
           throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if ( fac.nvar != 1 ) { // todo assert
           throw new RuntimeException("polynomial ring not univariate");
        }
        // setup factories
        GenPolynomialRing<ModInteger> pfac = A.ring;
        RingFactory<ModInteger> p = pfac.coFac;
        RingFactory<ModInteger> q = p;
        ModIntegerRing P = (ModIntegerRing)p;
        ModIntegerRing Q = (ModIntegerRing)q;
        BigInteger PP = new BigInteger( Q.getModul() );
        BigInteger Qi = PP;
        BigInteger M2 = M.multiply( M.fromInteger(2) );
        if ( debug ) {
            //System.out.println("M2 =  " + M2);
            logger.debug("M2 =  " + M2);
        }
        BigInteger Mq = Qi;
        GenPolynomialRing<ModInteger> qfac;
        qfac = new GenPolynomialRing<ModInteger>(Q,pfac); // mod p
        GenPolynomialRing<ModInteger> mfac;
        BigInteger Mi = new BigInteger( Q.getModul().multiply( Q.getModul() ) );
        ModIntegerRing Qmm = new ModIntegerRing( Mi.getVal() );
        mfac = new GenPolynomialRing<ModInteger>(Qmm,qfac);   // mod p^e
        ModInteger Pm = Qmm.fromInteger( P.getModul() );
        ModInteger Qm = Qmm.fromInteger( Qi.getVal() );

        // partly normalize c and a, b factors, assert p is prime
        GenPolynomial<BigInteger> Ai;
        GenPolynomial<BigInteger> Bi;
        BigInteger c = C.leadingBaseCoefficient();
        //##C = C.multiply(c); // sic
        ModInteger a = A.leadingBaseCoefficient();
        if ( !a.isONE() ) { // A = A.monic();
           A = A.divide( a );
           S = S.multiply( a );
        }
        ModInteger b = B.leadingBaseCoefficient();
        if ( !b.isONE() ) { // B = B.monic();
           B = B.divide( b );
           T = T.multiply( b );
        }
        ModInteger ci = P.fromInteger( c.getVal() );
        if ( ci.isZERO() ) {
            System.out.println("c =  " + c);
            System.out.println("P =  " + P);
            throw new RuntimeException("c mod p == 0 not meaningful");
        }
        // mod p
        A = A.multiply( ci );
        S = S.divide( ci );
        //##B = B.multiply( ci );
        //##T = T.divide( ci );
        Ai = PolyUtil.integerFromModularCoefficients( fac, A );
        Bi = PolyUtil.integerFromModularCoefficients( fac, B );
        //##// replace leading base coefficients
        //##ExpVector ea = Ai.leadingExpVector();
        //##ExpVector eb = Bi.leadingExpVector();
        //##Ai.doPutToMap(ea,c);
        //##Bi.doPutToMap(eb,c);

        // polynomials mod p
        GenPolynomial<ModInteger> Ap; 
        GenPolynomial<ModInteger> Bp;
        GenPolynomial<ModInteger> A1p; 
        GenPolynomial<ModInteger> B1p;
        GenPolynomial<ModInteger> Ep;
        GenPolynomial<ModInteger> Sp = S;
        GenPolynomial<ModInteger> Tp = T;

        // polynomials mod q
        GenPolynomial<ModInteger> Aq; 
        GenPolynomial<ModInteger> Bq;
        GenPolynomial<ModInteger> Eq;
        GenPolynomial<ModInteger> Eqp;

        // polynomials mod p^e
        GenPolynomial<ModInteger> Cm; 
        GenPolynomial<ModInteger> Am; 
        GenPolynomial<ModInteger> Bm;
        GenPolynomial<ModInteger> Em;
        GenPolynomial<ModInteger> Emp;
        GenPolynomial<ModInteger> Sm;
        GenPolynomial<ModInteger> Tm;
        GenPolynomial<ModInteger> Ema;
        GenPolynomial<ModInteger> Emb;
        GenPolynomial<ModInteger> Ema1;
        GenPolynomial<ModInteger> Emb1;

        // polynomials over the integers
        GenPolynomial<BigInteger> E;
        GenPolynomial<BigInteger> Ei;
        GenPolynomial<BigInteger> Ea;
        GenPolynomial<BigInteger> Eb;
        GenPolynomial<BigInteger> Ea1;
        GenPolynomial<BigInteger> Eb1;
        GenPolynomial<BigInteger> Si;
        GenPolynomial<BigInteger> Ti;

        Si = PolyUtil.integerFromModularCoefficients( fac, S );
        Ti = PolyUtil.integerFromModularCoefficients( fac, T );

        Aq = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Ai); 
        Bq = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Bi); 

        Cm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,C); 
        Am = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,Ai); 
        Bm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,Bi); 
        Sm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,Si); 
        Tm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,Ti); 
        //System.out.println("Cm =  " + Cm);
        //System.out.println("Am =  " + Am);
        //System.out.println("Bm =  " + Bm);
        //System.out.println("Ai =  " + Ai);
        //System.out.println("Bi =  " + Bi);
        //System.out.println("mfac =  " + mfac);

        while ( Mq.compareTo(M2) < 0 ) {
            // compute E=(C-AB)/p mod p^e
            if ( debug ) {
                //System.out.println("mfac =  " + Cm.ring);
                logger.debug("mfac =  " + Cm.ring);
            }
            Em = Cm.subtract( Am.multiply(Bm) );
            //System.out.println("Em =  " + Em);
            if ( Em.isZERO() ) {
                if ( C.subtract( Ai.multiply(Bi) ).isZERO() ) {
                    //System.out.println("leaving on zero error");
                    logger.info("leaving on zero error");
                    break;
                }
            }
            // Em = Em.divide( Qm );
            Ei = PolyUtil.integerFromModularCoefficients(fac,Em);
            Ei = Ei.divide( Qi );
            //System.out.println("Ei =  " + Ei);

            // Ei mod p
            Emp = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Ei);
            //            Emp = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,
            //               PolyUtil.integerFromModularCoefficients(fac,Em) ); 
            //System.out.println("Emp =  " + Emp);
            //logger.info("Emp = " + Emp);
            if ( Emp.isZERO() ) {
                if ( C.subtract( Ai.multiply(Bi) ).isZERO() ) {
                    //System.out.println("leaving on zero error Emp");
                    logger.info("leaving on zero error Emp");
                    break;
                }
            }

            // construct approximation mod p
            Ap = Sp.multiply( Emp ); // S,T ++ T,S 
            Bp = Tp.multiply( Emp );               
            GenPolynomial<ModInteger>[] QR = null;
            QR = Ap.divideAndRemainder( Bq );   // Bq !
            GenPolynomial<ModInteger> Qp = QR[0];
            GenPolynomial<ModInteger> Rp = QR[1];
            A1p = Rp;
            B1p = Bp.sum( Aq.multiply( Qp ) );  // Aq !
            //System.out.println("A1p =  " + A1p);
            //System.out.println("B1p =  " + B1p);

            // construct q-adic approximation
            Ema = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,
                           PolyUtil.integerFromModularCoefficients(fac,A1p) );
            Emb = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,
                           PolyUtil.integerFromModularCoefficients(fac,B1p) );
            //System.out.println("Ema =  " + Ema);
            //System.out.println("Emb =  " + Emb);
            Ema1 = Ema.multiply( Qm );
            Emb1 = Emb.multiply( Qm );
            Ema = Am.sum( Emb1 ); // Eb1 and Ea1 are required
            Emb = Bm.sum( Ema1 ); //--------------------------
            assert ( Ema.degree(0)+Emb.degree(0) <= Cm.degree(0) );
            //if ( Ema.degree(0)+Emb.degree(0) > Cm.degree(0) ) { // debug
            //   throw new RuntimeException("deg(A)+deg(B) > deg(C)");
            //}
            Am = Ema; 
            Bm = Emb;
            Ai = PolyUtil.integerFromModularCoefficients(fac,Am);
            Bi = PolyUtil.integerFromModularCoefficients(fac,Bm);
            //System.out.println("Am =  " + Am);
            //System.out.println("Bm =  " + Bm);
            //System.out.println("Ai =  " + Ai);
            //System.out.println("Bi =  " + Bi + "\n");

            // gcd representation factors error --------------------------------
            // compute E=(1-SA-TB)/p mod p^e
            Em = mfac.getONE();
            Em = Em.subtract( Sm.multiply(Am) ).subtract( Tm.multiply(Bm) );
            //System.out.println("Em  =  " + Em);
            // Em = Em.divide( Pm );

            Ei = PolyUtil.integerFromModularCoefficients(fac,Em);
            Ei = Ei.divide( Qi );
            //System.out.println("Ei =  " + Ei);
            // Ei mod p
            Emp = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Ei);
            //Emp = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,
            //               PolyUtil.integerFromModularCoefficients(fac,Em) );
            //System.out.println("Emp =  " + Emp);

            // construct approximation mod p
            Ap = Sp.multiply( Emp ); // S,T ++ T,S // Ep Eqp
            Bp = Tp.multiply( Emp );               // Ep Eqp
            QR = Bp.divideAndRemainder( Aq );      // Ap Aq ! // Ai == A mod p ?
            Qp = QR[0];
            Rp = QR[1];
            B1p = Rp;
            A1p = Ap.sum( Bq.multiply( Qp ) );
            //System.out.println("A1p =  " + A1p);
            //System.out.println("B1p =  " + B1p);

            // construct p^e-adic approximation
            Ema = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,
                           PolyUtil.integerFromModularCoefficients(fac,A1p));
            Emb =  PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,
                          PolyUtil.integerFromModularCoefficients(fac,B1p));
            Ema1 = Ema.multiply( Qm );
            Emb1 = Emb.multiply( Qm );
            Ema = Sm.sum( Ema1 ); // Emb1 and Ema1 are required
            Emb = Tm.sum( Emb1 ); //--------------------------
            Sm = Ema;
            Tm = Emb;
            Si = PolyUtil.integerFromModularCoefficients(fac,Sm); 
            Ti = PolyUtil.integerFromModularCoefficients(fac,Tm);
            //System.out.println("Sm =  " + Sm);
            //System.out.println("Tm =  " + Tm);
            //System.out.println("Si =  " + Si);
            //System.out.println("Ti =  " + Ti + "\n");

            // prepare for next iteration
            Qi = new BigInteger( Q.getModul().multiply( Q.getModul() ) );
            Mq = Qi;
            Q = new ModIntegerRing( Qi.getVal() );
            qfac = new GenPolynomialRing<ModInteger>(Q,pfac);
            Qmm = new ModIntegerRing( Qmm.getModul().multiply( Qmm.getModul() ) );
            mfac = new GenPolynomialRing<ModInteger>(Qmm,qfac);
            Qm = Qmm.fromInteger( Qi.getVal() );

            Cm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,C);
            Am = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,Ai);
            Bm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,Bi);
            Sm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,Si);
            Tm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,Ti);

            assert ( isHenselLift(C,Mi,PP,Ai,Bi) );
            //System.out.println("is Hensel lift =  " + ih);
            Mi = Mi.fromInteger( Qmm.getModul() );

            Aq = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Ai);
            Bq = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Bi);
            Sp = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Si);
            Tp = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Ti);

            //System.out.println("Am =  " + Am);
            //System.out.println("Bm =  " + Bm);
            //System.out.println("Sm =  " + Sm);
            //System.out.println("Tm =  " + Tm);
            //System.out.println("mfac =  " + mfac);
            //System.out.println("Qmm = " + Qmm + ", M2   =  " + M2 + ", Mq   =  " + Mq + "\n");
        }
        //System.out.println("*Ai =  " + Ai);
        //System.out.println("*Bi =  " + Bi);

        // remove normalization not ok when not exact factorization
        //##C = C.divide(c);
//         if ( false /*C.subtract(Ai.multiply(Bi)).isZERO()*/ ) {
//             GreatestCommonDivisorAbstract<BigInteger> ufd
//                 = new GreatestCommonDivisorPrimitive<BigInteger>();
//             // remove normalization
//             BigInteger ai = ufd.baseContent(Ai);
//             Ai = Ai.divide( ai );
//             System.out.println("*Ai =  " + Ai);
//             System.out.println("*ai =  " + ai);
//             BigInteger bi = c.divide(ai);
//             System.out.println("*c  =  " + c);
//             System.out.println("*bi =  " + bi);
//             if ( !bi.isZERO() && !bi.isONE() ) {
//                 Bi = Bi.divide( bi ); // divide( c/a )
//             }
//             System.out.println("*Bi =  " + Bi);
//             boolean ih = isHenselLift(C.divide(c),Mi,PP,Ai,Bi);
//             System.out.println("*is Hensel lift =  " + ih);
//         //##} else {
//         //##    System.out.println("*no exact factorization: " + C.subtract(Ai.multiply(Bi)));
//         }
        AB[0] = Ai;
        AB[1] = Bi;
        return AB;
    }


    /** ModInteger Hensel lifting test.
     * Let p be a prime number and 
     * assume C == prod_{0,...,n-1} g_i mod p with ggt(g_i,g_j) == 1 mod p for i != j.
     * @param C GenPolynomial<BigInteger>.
     * @param G = [g_0,...,g_{n-1}] List<GenPolynomial<ModInteger>>.
     * @param M bound on the coefficients of g_i as factors of C.
     * @param p prime number.
     * @return true if C = prod_{0,...,n-1} g_i mod p**e, else false.
     */
    public static //<C extends RingElem<C>>
        boolean isHenselLift( GenPolynomial<BigInteger> C,
                              BigInteger M,
                              BigInteger p,
                              List<GenPolynomial<BigInteger>> G) {
        if ( C == null || C.isZERO() ) {
            return false;
        }
        GenPolynomialRing<BigInteger> pfac = C.ring;
        ModIntegerRing pm = new ModIntegerRing(p.getVal(),true);
        GenPolynomialRing<ModInteger> mfac
             = new GenPolynomialRing<ModInteger>(pm,pfac);

        // check mod p
        GenPolynomial<ModInteger> cl = mfac.getONE();
        GenPolynomial<ModInteger> hlp;
        for ( GenPolynomial<BigInteger> hl : G ) {
            //System.out.println("hl       = " + hl);
             hlp = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,hl);
             //System.out.println("hl mod p = " + hlp);
             cl = cl.multiply( hlp );
        }
        GenPolynomial<ModInteger> cp = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,C);
        if ( !cp.equals(cl) ) {
            System.out.println("Hensel precondition wrong!");
            System.out.println("cl    = " + cl);
            System.out.println("cp    = " + cp);
            System.out.println("cp-cl = " + cp.subtract(cl));
            System.out.println("M = " + M + ", p = " + p);
            return false;
        }

        // check mod p^e 
        BigInteger mip = p;
        while ( mip.compareTo(M) < 0 ) {
            mip = mip.multiply(mip); // p
        }
        // mip = mip.multiply(p);
        pm = new ModIntegerRing(mip.getVal(),false);
        mfac = new GenPolynomialRing<ModInteger>(pm,pfac);

        cl = mfac.getONE();
        for ( GenPolynomial<BigInteger> hl : G ) {
            //System.out.println("hl         = " + hl);
             hlp = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,hl);
             //System.out.println("hl mod p^e = " + hlp);
             cl = cl.multiply( hlp );
        }
        cp = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,C);
        if ( !cp.equals(cl) ) {
            System.out.println("Hensel post condition wrong!");
            System.out.println("cl    = " + cl);
            System.out.println("cp    = " + cp);
            System.out.println("cp-cl = " + cp.subtract(cl));
            System.out.println("M = " + M + ", p = " + p + ", p^e = " + mip);
            return false;
        }
        return true;
    }


    /** ModInteger Hensel lifting test.
     * Let p be a prime number and assume C == A * B mod p with ggt(A,B) == 1 mod p.
     * @param C GenPolynomial<BigInteger>.
     * @param A GenPolynomial<BigInteger>.
     * @param B GenPolynomial<BigInteger>.
     * @param M bound on the coefficients of A and B as factors of C.
     * @param p prime number.
     * @return true if C = A * B mod p**e, else false.
     */
    public static //<C extends RingElem<C>>
        boolean isHenselLift( GenPolynomial<BigInteger> C,
                              BigInteger M,
                              BigInteger p,
                              GenPolynomial<BigInteger> A,
                              GenPolynomial<BigInteger> B) {
        List<GenPolynomial<BigInteger>> G = new ArrayList<GenPolynomial<BigInteger>>(2);
        G.add(A);
        G.add(B);
        return isHenselLift(C,M,p,G);
    }


    /**
     * Kronecker substitution. 
     * Substitute x_i by x**d**(i-1) to construct a univariate polynomial.
     * @param A polynomial to be converted.
     * @return a univariate polynomial.
     */
    public static <C extends GcdRingElem<C>> 
      GenPolynomial<C> substituteKronecker( GenPolynomial<C> A ) {
        if ( A == null ) {
            return A;
        }
        long d = A.degree() + 1L;
        return substituteKronecker( A, d );
    }


    /**
     * Kronecker substitution. 
     * Substitute x_i by x**d**(i-1) to construct a univariate polynomial.
     * @param A polynomial to be converted.
     * @return a univariate polynomial.
     */
    public static <C extends GcdRingElem<C>> 
      GenPolynomial<C> substituteKronecker( GenPolynomial<C> A, long d ) {
        if ( A == null ) {
            return A;
        }
        RingFactory<C> cfac = A.ring.coFac; 
        GenPolynomialRing<C> ufac = new GenPolynomialRing<C>(cfac,1);
        GenPolynomial<C> B = ufac.getZERO().clone();
        if ( A.isZERO() ) {
           return B;
        }
        for ( Map.Entry<ExpVector,C> y : A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            long f = 0L;
            long h = 1L;
            for ( int i = 0; i < e.length(); i++ ) {
                long j = e.getVal(i) * h;
                f += j;
                h *= d;
            }
            ExpVector g = ExpVector.create(1,0,f);
            B.doPutToMap( g, a );
        }
        return B;
    }


    /**
     * Kronecker substitution. 
     * Substitute x_i by x**d**(i-1) to construct a univariate polynomials.
     * @param A list of polynomials to be converted.
     * @return a list of univariate polynomials.
     */
    public static <C extends GcdRingElem<C>> 
      List<GenPolynomial<C>> substituteKronecker( List<GenPolynomial<C>> A, int d ) {
        if ( A == null || A.get(0) == null ) {
            return null;
        }
        return ListUtil.<GenPolynomial<C>,GenPolynomial<C>>map( A, new SubstKronecker<C>(d) );
    }


    /**
     * Kronecker back substitution. 
     * Substitute x**d**(i-1) to x_i to construct a multivariate polynomial.
     * @param A polynomial to be converted.
     * @param fac result polynomial factory.
     * @return a multivariate polynomial.
     */
    public static <C extends GcdRingElem<C>> 
      GenPolynomial<C> backSubstituteKronecker( GenPolynomialRing<C> fac, GenPolynomial<C> A, long d ) {
        if ( A == null ) {
            return A;
        }
        if ( fac == null ) {
            throw new IllegalArgumentException("null factory not allowed ");
        }
        int n = fac.nvar;
        GenPolynomial<C> B = fac.getZERO().clone();
        if ( A.isZERO() ) {
           return B;
        }
        for ( Map.Entry<ExpVector,C> y : A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            long f = e.getVal(0);
            ExpVector g = ExpVector.create(n);
            long h = 1L;
            for ( int i = 0; i < n; i++ ) {
                long j = f % d;
                f /= d;
                g = g.subst(i,j);
            }
            B.doPutToMap( g, a );
        }
        return B;
    }


    /**
     * Kronecker back substitution. 
     * Substitute x**d**(i-1) to x_i to construct a multivariate polynomials.
     * @param A list of polynomials to be converted.
     * @param fac result polynomial factory.
     * @return a list of multivariate polynomials.
     */
    public static <C extends GcdRingElem<C>> 
      List<GenPolynomial<C>> backSubstituteKronecker( GenPolynomialRing<C> fac, 
                                                      List<GenPolynomial<C>> A, long d ) {
        return ListUtil.<GenPolynomial<C>,GenPolynomial<C>>map( A, new BackSubstKronecker<C>(fac,d) );
    }

}


/**
 * Kronecker substitutuion functor.
 */
class SubstKronecker<C extends GcdRingElem<C>>  
      implements UnaryFunctor<GenPolynomial<C>,GenPolynomial<C>> {

    final long d;

    public SubstKronecker(long d) {
        this.d = d;
    }

    public GenPolynomial<C> eval(GenPolynomial<C> c) {
        if ( c == null ) {
            return null;
        } else {
            return PolyUfdUtil.<C>substituteKronecker(c,d);
        }
    }
}


/**
 * Kronecker back substitutuion functor.
 */
class BackSubstKronecker<C extends GcdRingElem<C>> 
       implements UnaryFunctor<GenPolynomial<C>,GenPolynomial<C>> {

    final long d;
    final GenPolynomialRing<C> fac;

    public BackSubstKronecker(GenPolynomialRing<C> fac, long d) {
        this.d = d;
        this.fac = fac;
    }

    public GenPolynomial<C> eval(GenPolynomial<C> c) {
        if ( c == null ) {
            return null;
        } else {
            return PolyUfdUtil.<C>backSubstituteKronecker(fac,c,d);
        }
    }
}
