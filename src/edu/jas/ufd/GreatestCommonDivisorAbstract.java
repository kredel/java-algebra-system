/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;

import edu.jas.util.ListUtil;


/**
 * Greatest common divisor algorithms.
 * @author Heinz Kredel
 */

public abstract class GreatestCommonDivisorAbstract<C extends GcdRingElem<C>> implements
        GreatestCommonDivisor<C> {


    private static final Logger logger = Logger
            .getLogger(GreatestCommonDivisorAbstract.class);


    private final boolean debug = logger.isDebugEnabled();


    /** Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName();
    }


    /**
     * GenPolynomial base coefficient content.
     * @param P GenPolynomial.
     * @return cont(P).
     */
    public C baseContent(GenPolynomial<C> P) {
        if ( P == null ) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        if ( P.isZERO() ) {
            return P.ring.getZEROCoefficient();
        }
        C d = null;
        for ( C c : P.getMap().values() ) {
            if ( d == null ) {
                d = c;
            } else {
                d = d.gcd(c);
            }
            if ( d.isONE() ) {
                return d;
            }
        }
        return d.abs();
    }


    /**
     * GenPolynomial base coefficient primitive part.
     * @param P GenPolynomial.
     * @return pp(P).
     */
    public GenPolynomial<C> basePrimitivePart(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P;
        }
        C d = baseContent(P);
        if (d.isONE()) {
            return P;
        }
        GenPolynomial<C> pp = P.divide(d);
        if (debug) {
            GenPolynomial<C> p = pp.multiply(d);
            if (!p.equals(P)) {
                throw new RuntimeException("pp(p)*cont(p) != p: ");
            }
        }
        return pp;
    }


    /**
     * Univariate GenPolynomial greatest common divisor. Uses sparse
     * pseudoRemainder for remainder.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    public abstract GenPolynomial<C> baseGcd(GenPolynomial<C> P, GenPolynomial<C> S);


    /**
     * GenPolynomial polynomial greatest squarefee divisor.
     * @param P GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public GenPolynomial<C> baseSquarefreePart(GenPolynomial<C> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName()
                    + " only for univariate polynomials");
        }
        GenPolynomial<C> pp = basePrimitivePart(P);
        if ( pp.isConstant() ) {
            return pp;
        }
        GenPolynomial<C> d;
        long k = pp.degree(0);
        long j = 1;
        while ( true ) { 
            d = PolyUtil.<C> baseDeriviative(pp);
            //System.out.println("d = " + d);
            if ( !d.isZERO() ) { // || pp.isConstant()
                break;
            }
            long mp = pfac.characteristic().longValue(); // assert != 0
            pp = PolyUtil.<C> baseModRoot(pp,mp);
            j = j * mp;
            if ( j > k ) {
               throw new RuntimeException("polynomial mod " + mp + ", pp = " + pp + ", d = " + d);
            }
        } 
        GenPolynomial<C> g = baseGcd(pp, d);
        GenPolynomial<C> q = PolyUtil.<C> basePseudoDivide(pp, g);
        return q;
    }


    /**
     * GenPolynomial polynomial squarefree factorization.
     * @param A GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i} and p_i squarefree.
     */
    public SortedMap<GenPolynomial<C>,Long> baseSquarefreeFactors( GenPolynomial<C> A ) {
        SortedMap<GenPolynomial<C>,Long> sfactors = new TreeMap<GenPolynomial<C>,Long>();
        if ( A == null || A.isZERO() ) {
            return sfactors;
        }
        if ( A.isConstant() ) {
            sfactors.put(A,1L);
            return sfactors;
        }
        GenPolynomialRing<C> pfac = A.ring;
        if ( pfac.nvar > 1 ) {
            throw new RuntimeException(this.getClass().getName()
                    + " only for univariate polynomials");
        }
        if ( pfac.characteristic().signum() > 0 && !A.leadingBaseCoefficient().isONE() ) {
            throw new RuntimeException("A mod p not monic");
        }
        GenPolynomial<C> T0 = A;
        long e = 1L;
        GenPolynomial<C> Tp;
        GenPolynomial<C> T = null;
        GenPolynomial<C> V = null;
        long k = 0L;
        long mp = 0L;
        boolean init = true;
        while ( true ) { 
            if ( init ) {
                if ( T0.isConstant() || T0.isZERO() ) {
                     break;
                }
                Tp = PolyUtil.<C> baseDeriviative(T0);
                T = baseGcd(T0,Tp);
                V = PolyUtil.<C> basePseudoDivide(T0,T);
                //System.out.println("Tp = " + Tp);
                //System.out.println("T = " + T);
                //System.out.println("V = " + V);
                k = 0L;
                mp = 0L;
                init = false;
            }
            if ( V.isConstant() ) { 
                mp = pfac.characteristic().longValue(); // assert != 0
                if ( mp > 0 ) {
                   T0 = PolyUtil.<C> baseModRoot(T,mp);
                   //System.out.println("T0b = " + T0 + ", Tb = " + T);
                   e = e * mp;
                   init = true;
                   continue;
                } else {
                   break;
                }
            }
            k++;
            if ( mp != 0L && k % mp == 0L ) {
                T = PolyUtil.<C> basePseudoDivide(T, V);
                System.out.println("k = " + k);
                //System.out.println("T = " + T);
                k++;
            }
            GenPolynomial<C> W = baseGcd(T,V);
            GenPolynomial<C> z = PolyUtil.<C> basePseudoDivide(V, W);
            //System.out.println("W = " + W);
            //System.out.println("z = " + z);
            V = W;
            T = PolyUtil.<C> basePseudoDivide(T, V);
            //System.out.println("V = " + V);
            //System.out.println("T = " + T);
            if ( z.degree(0) > 0 ) {
                sfactors.put(z, (e*k));
            }
        }
        return sfactors;
    }


    /**
     * GenPolynomial recursive content.
     * @param P recursive GenPolynomial.
     * @return cont(P).
     */
    public GenPolynomial<C> recursiveContent(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P.ring.getZEROCoefficient();
        }
        GenPolynomial<C> d = null;
        for (GenPolynomial<C> c : P.getMap().values()) {
            if (d == null) {
                d = c;
            } else {
                d = gcd(d, c); // go to recursion
            }
            if (d.isONE()) {
                return d;
            }
        }
        return d.abs();
    }


    /**
     * GenPolynomial recursive primitive part.
     * @param P recursive GenPolynomial.
     * @return pp(P).
     */
    public GenPolynomial<GenPolynomial<C>> recursivePrimitivePart(
            GenPolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P;
        }
        GenPolynomial<C> d = recursiveContent(P);
        if (d.isONE()) {
            return P;
        }
        GenPolynomial<GenPolynomial<C>> pp = PolyUtil.<C> recursiveDivide(P, d);
        return pp;
    }


    /**
     * GenPolynomial recursive greatest common divisor. Uses
     * pseudoRemainder for remainder.
     * @param P recursive GenPolynomial.
     * @param S recursive GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<GenPolynomial<C>> recursiveGcd(
           GenPolynomial<GenPolynomial<C>> P, GenPolynomial<GenPolynomial<C>> S) {
        if ( S == null || S.isZERO() ) {
            return P;
        }
        if ( P == null || P.isZERO() ) {
            return S;
        }
        if ( P.ring.nvar <= 1 ) {
            return recursiveUnivariateGcd( P, S );
        }
        // distributed polynomials gcd
        GenPolynomialRing<GenPolynomial<C>> rfac = P.ring;
        RingFactory<GenPolynomial<C>> rrfac = rfac.coFac;
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>)rrfac;
        GenPolynomialRing<C> dfac = cfac.extend( rfac.nvar );
        GenPolynomial<C> Pd = PolyUtil.<C> distribute(dfac, P);
        GenPolynomial<C> Sd = PolyUtil.<C> distribute(dfac, S);
        GenPolynomial<C> Dd = gcd(Pd,Sd);
        // convert to recursive
        GenPolynomial<GenPolynomial<C>> C = PolyUtil.<C> recursive(rfac, Dd);
        return C;
    }


    /**
     * Univariate GenPolynomial recursive greatest common divisor. Uses
     * pseudoRemainder for remainder.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return gcd(P,S).
     */
    public abstract GenPolynomial<GenPolynomial<C>> recursiveUnivariateGcd(
            GenPolynomial<GenPolynomial<C>> P, GenPolynomial<GenPolynomial<C>> S);


    /**
     * GenPolynomial recursive polynomial greatest squarefee divisor.
     * @param P recursive GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public GenPolynomial<GenPolynomial<C>> 
      recursiveSquarefreePart( GenPolynomial<GenPolynomial<C>> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if ( pfac.nvar > 1 ) {
            throw new RuntimeException(this.getClass().getName()
                    + " only for multivariate polynomials");
        }
        // squarefree content
        GenPolynomial<GenPolynomial<C>> pp = P;
        GenPolynomial<C> Pc = recursiveContent(P);
        if ( ! Pc.isONE() ) {
           GenPolynomial<C> Pr = squarefreePart(Pc);
           pp = PolyUtil.<C> coefficientPseudoDivide(pp, Pr);
           if ( pp.isZERO() ) { // how can this happen?
               System.out.println("pp = " + pp);
               System.out.println("Pc = " + Pc);
               System.out.println("Pr = " + Pr);
               System.out.println("P  = " + P);
               //pp = PolyUtil.<C> coefficientPseudoDivide(pp, Pc);
               //pp = P;
           }
        }
        if ( pp.leadingExpVector().getVal(0) < 1 ) {
            return pp.multiply(Pc);
        }
        // mod p case
        GenPolynomial<GenPolynomial<C>> d;
        while ( true ) { 
            d = PolyUtil.<C>recursiveDeriviative(pp);
            //System.out.println("d = " + d);
            if ( !d.isZERO() ) { // || pp.isConstant()
                break;
            }
            int mp = pfac.characteristic().intValue(); // assert != 0
            pp = PolyUtil.<C> recursiveModRoot(pp,mp);
        } 
        // now d != 0
        GenPolynomial<GenPolynomial<C>> g = recursiveUnivariateGcd(pp, d);
        GenPolynomial<GenPolynomial<C>> q = PolyUtil.<C> recursivePseudoDivide(pp, g);
        return q.multiply(Pc);
    }


   /**
     * GenPolynomial recursive polynomial squarefree factorization.
     * @param P GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i} and p_i squarefree.
     */
    public SortedMap<GenPolynomial<GenPolynomial<C>>,Long> 
      recursiveSquarefreeFactors( GenPolynomial<GenPolynomial<C>> P ) {
        SortedMap<GenPolynomial<GenPolynomial<C>>,Long> sfactors 
            = new TreeMap<GenPolynomial<GenPolynomial<C>>,Long>();
        if (P == null || P.isZERO()) {
            return sfactors;
        }
        if ( P.isConstant() ) {
            sfactors.put(P,1L);
            return sfactors;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // recursiveContent not possible by return type
            throw new RuntimeException(this.getClass().getName()
                    + " only for univariate polynomials");
        }

        // factors of content
        GenPolynomial<C> Pc = recursiveContent(P);
        if ( !Pc.isONE() ) {
            P = PolyUtil.<C> coefficientPseudoDivide(P,Pc);
        }
        SortedMap<GenPolynomial<C>,Long> rsf = squarefreeFactors(Pc);
        //System.out.println("rsf = " + rsf);
        // add factors of content
        for (GenPolynomial<C> c : rsf.keySet()) {
            if ( !c.isONE() ) {
               GenPolynomial<GenPolynomial<C>>  cr = pfac.getONE().multiply(c);
               Long rk = rsf.get(c);
               sfactors.put(cr,rk);
            }
        }

        // factors of recursive polynomial
        GenPolynomial<GenPolynomial<C>> T0 = P;
        long e = 1L;
        GenPolynomial<GenPolynomial<C>> Tp;
        GenPolynomial<GenPolynomial<C>> T = null;
        GenPolynomial<GenPolynomial<C>> V = null;
        long k = 0L;
        long mp = 0L;
        boolean init = true;
        while ( true ) { 
            if ( init ) {
                if ( T0.isConstant() || T0.isZERO() ) {
                     break;
                }
                Tp = PolyUtil.<C> recursiveDeriviative(T0);
                T = recursiveUnivariateGcd(T0,Tp);
                V = PolyUtil.<C> recursivePseudoDivide(T0,T);
                //System.out.println("Tp = " + Tp);
                //System.out.println("T = " + T);
                //System.out.println("V = " + V);
                k = 0L;
                mp = 0L;
                init = false;
            }
            if ( V.isConstant() ) { 
                mp = pfac.characteristic().longValue(); // assert != 0
                if ( mp > 0 ) {
                   T0 = PolyUtil.<C> recursiveModRoot(T,mp);
                   //System.out.println("T0r = " + T0 + ", Tr = " + T);
                   e = e * mp;
                   init = true;
                   continue;
                } else {
                   break;
                }
            }
            k++;
            if ( mp != 0L && k % mp == 0L ) {
                T = PolyUtil.<C> recursivePseudoDivide(T, V);
                System.out.println("k = " + k);
                //System.out.println("T = " + T);
                k++;
            }
            GenPolynomial<GenPolynomial<C>> W = recursiveUnivariateGcd(T,V);
            GenPolynomial<GenPolynomial<C>> z = PolyUtil.<C> recursivePseudoDivide(V, W);
            //System.out.println("W = " + W);
            //System.out.println("z = " + z);
            V = W;
            T = PolyUtil.<C> recursivePseudoDivide(T, V);
            //System.out.println("V = " + V);
            //System.out.println("T = " + T);
            if ( z.degree(0) > 0 ) {
                sfactors.put(z, (e*k));
            }
        }
        return sfactors;
    }


    /**
     * GenPolynomial content.
     * @param P GenPolynomial.
     * @return cont(P).
     */
    public GenPolynomial<C> content(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            // baseContent not possible by return type
            throw new RuntimeException(this.getClass().getName()
                    + " use baseContent for univariate polynomials");

        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(
                cfac, 1);

        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C> recursive(rfac, P);
        GenPolynomial<C> D = recursiveContent(Pr);
        return D;
    }


    /**
     * GenPolynomial primitive part.
     * @param P GenPolynomial.
     * @return pp(P).
     */
    public GenPolynomial<C> primitivePart(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            return basePrimitivePart(P);
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(
                cfac, 1);

        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C> recursive(rfac, P);
        GenPolynomial<GenPolynomial<C>> PP = recursivePrimitivePart(Pr);

        GenPolynomial<C> D = PolyUtil.<C> distribute(pfac, PP);
        return D;
    }


    /**
     * GenPolynomial division. Indirection to GenPolynomial method.
     * @param a GenPolynomial.
     * @param b coefficient.
     * @return a/b.
     */
    public GenPolynomial<C> divide(GenPolynomial<C> a, C b) {
        if (b == null || b.isZERO()) {
            throw new RuntimeException(this.getClass().getName() + " division by zero");

        }
        if (a == null || a.isZERO()) {
            return a;
        }
        return a.divide(b);
    }


    /**
     * Coefficient greatest common divisor. Indirection to coefficient method.
     * @param a coefficient.
     * @param b coefficient.
     * @return gcd(a,b).
     */
    public C gcd(C a, C b) {
        if (b == null || b.isZERO()) {
            return a;
        }
        if (a == null || a.isZERO()) {
            return b;
        }
        return a.gcd(b);
    }


    /**
     * GenPolynomial greatest common divisor.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<C> gcd(GenPolynomial<C> P, GenPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            GenPolynomial<C> T = baseGcd(P, S);
            return T;
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>( cfac, 1);

        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C> recursive(rfac, P);
        GenPolynomial<GenPolynomial<C>> Sr = PolyUtil.<C> recursive(rfac, S);
        GenPolynomial<GenPolynomial<C>> Dr = recursiveUnivariateGcd(Pr, Sr);
        GenPolynomial<C> D = PolyUtil.<C> distribute(pfac, Dr);
        return D;
    }


    /**
     * GenPolynomial least common multiple.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return lcm(P,S).
     */
    public GenPolynomial<C> lcm(GenPolynomial<C> P, GenPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return S;
        }
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomial<C> C = gcd(P, S);
        GenPolynomial<C> A = P.multiply(S);
        return PolyUtil.<C> basePseudoDivide(A, C);
    }


    /**
     * GenPolynomial greatest squarefree divisor.
     * @param P GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public GenPolynomial<C> squarefreePart(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            return baseSquarefreePart(P);
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac 
            = new GenPolynomialRing<GenPolynomial<C>>(cfac, 1);

        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C> recursive(rfac, P);
        GenPolynomial<C> Pc = recursiveContent(Pr);
        Pr = PolyUtil.<C> coefficientPseudoDivide(Pr, Pc);
        GenPolynomial<C> Ps = squarefreePart(Pc);
        GenPolynomial<GenPolynomial<C>> PP = recursiveSquarefreePart(Pr);
        GenPolynomial<GenPolynomial<C>> PS = PP.multiply(Ps);
        GenPolynomial<C> D = PolyUtil.<C> distribute(pfac, PS);
        return D;
    }


    /**
     * GenPolynomial squarefree factorization.
     * @param P GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i} and p_i squarefree.
     */
    public SortedMap<GenPolynomial<C>,Long> squarefreeFactors(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            return baseSquarefreeFactors(P);
        }
        SortedMap<GenPolynomial<C>,Long> sfactors = new TreeMap<GenPolynomial<C>,Long>();
        if (P.isZERO()) {
            return sfactors;
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac 
            = new GenPolynomialRing<GenPolynomial<C>>(cfac, 1);

        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C> recursive(rfac, P);
        SortedMap<GenPolynomial<GenPolynomial<C>>,Long> PP = recursiveSquarefreeFactors(Pr);

        for (Map.Entry<GenPolynomial<GenPolynomial<C>>,Long> m : PP.entrySet()) {
            Long i = m.getValue();
            GenPolynomial<GenPolynomial<C>> Dr = m.getKey();
            GenPolynomial<C> D = PolyUtil.<C> distribute(pfac, Dr);
            sfactors.put(D,i);
        }
        return sfactors;
    }


    /**
     * GenPolynomial resultant.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return res(P,S).
     */
    public GenPolynomial<C> resultant(GenPolynomial<C> P, GenPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return S;
        }
        if (P == null || P.isZERO()) {
            return P;
        }
        // hack
        GreatestCommonDivisorSubres<C> ufd_sr = new GreatestCommonDivisorSubres<C>();
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            GenPolynomial<C> T = ufd_sr.baseResultant(P, S);
            return T;
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(
                cfac, 1);

        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C> recursive(rfac, P);
        GenPolynomial<GenPolynomial<C>> Sr = PolyUtil.<C> recursive(rfac, S);

        GenPolynomial<GenPolynomial<C>> Dr = ufd_sr.recursiveResultant(Pr, Sr);
        GenPolynomial<C> D = PolyUtil.<C> distribute(pfac, Dr);
        return D;
    }


    /**
     * GenPolynomial squarefree and co-prime list.
     * @param A list of GenPolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B 
     *         and for all non-constant a in A there exists b in B with b|a
     *         and each b in B is squarefree. 
     *         B does not contain zero or constant polynomials.
     */
    public List<GenPolynomial<C>> coPrimeSquarefree(List<GenPolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            return A;
        }
        List<GenPolynomial<C>> S = new ArrayList<GenPolynomial<C>>();
        for ( GenPolynomial<C> g : A ) {
            SortedMap<GenPolynomial<C>,Long> sm = squarefreeFactors(g);
            S.addAll( sm.keySet() );
        }
        List<GenPolynomial<C>> B = coPrime(S);
        return B;
    }


    /**
     * GenPolynomial co-prime list.
     * @param A list of GenPolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B 
     *         and for all non-constant a in A there exists b in B with b|a.
     *         B does not contain zero or constant polynomials.
     */
    public List<GenPolynomial<C>> coPrime(List<GenPolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            return A;
        }
        List<GenPolynomial<C>> B = new ArrayList<GenPolynomial<C>>( A.size() );
        // make a coprime to rest of list
        GenPolynomial<C> a = A.get(0);
        //System.out.println("a = " + a);
        if ( !a.isZERO() && !a.isConstant() ) {
            for ( int i = 1; i < A.size(); i++ ) {
                GenPolynomial<C> b = A.get(i);
                GenPolynomial<C> g = gcd(a,b).abs();
                if ( !g.isONE() ) {
                    a = PolyUtil.<C> basePseudoDivide(a,g);
                    b = PolyUtil.<C> basePseudoDivide(b,g);
                    GenPolynomial<C> gp = gcd(a,g).abs();
                    while ( !gp.isONE() ) {
                       a = PolyUtil.<C> basePseudoDivide(a,gp);
                       g = PolyUtil.<C> basePseudoDivide(g,gp);
                       B.add(g); // gcd(a,g) == 1
                       g = gp;
                       gp = gcd(a,gp).abs();
                    }
                    if ( !g.isZERO() && !g.isConstant() /*&& !B.contains(g)*/ ) {
                        B.add(g); // gcd(a,g) == 1
                    }
                } 
                if ( !b.isZERO() && !b.isConstant() ) {
                    B.add(b); // gcd(a,b) == 1
                }
            }
        } else {
            B.addAll( A.subList(1,A.size()) );
        }
        a = a.abs();
        // make rest coprime
        B = coPrime(B);
        //System.out.println("B = " + B);
        //System.out.println("red(a) = " + a);
        if ( !a.isZERO() && !a.isConstant() /*&& !B.contains(a)*/ ) {
            B.add(a);
        }
        return B;
    }


    /**
     * GenPolynomial co-prime list.
     * @param A list of GenPolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B 
     *         and for all non-constant a in A there exists b in B with b|a.
     *         B does not contain zero or constant polynomials.
     */
    public List<GenPolynomial<C>> coPrimeRec(List<GenPolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            return A;
        }
        List<GenPolynomial<C>> B = new ArrayList<GenPolynomial<C>>();
        // make a co-prime to rest of list
        for ( GenPolynomial<C> a : A ) {
            //System.out.println("a = " + a);
            B = coPrime(a,B);
            //System.out.println("B = " + B);
        }
        return B;
    }


    /**
     * GenPolynomial co-prime list.
     * @param a GenPolynomial.
     * @param P co-prime list of GenPolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B 
     *         and for non-constant a there exists b in P with b|a.
     *         B does not contain zero or constant polynomials.
     */
    public List<GenPolynomial<C>> coPrime(GenPolynomial<C> a, List<GenPolynomial<C>> P) {
        if ( a == null || a.isZERO() || a.isConstant() ) {
            return P;
        }
        List<GenPolynomial<C>> B = new ArrayList<GenPolynomial<C>>( P.size()+1 );
        // make a coprime to elements of the list P
        for ( int i = 0; i < P.size(); i++ ) {
            GenPolynomial<C> b = P.get(i);
            GenPolynomial<C> g = gcd(a,b).abs();
            if ( !g.isONE() ) {
                a = PolyUtil.<C> basePseudoDivide(a,g);
                b = PolyUtil.<C> basePseudoDivide(b,g);
                // make g co-prime to new a, g is co-prime to c != b in P, B
                GenPolynomial<C> gp = gcd(a,g).abs();
                while ( !gp.isONE() ) {
                    a = PolyUtil.<C> basePseudoDivide(a,gp);
                    g = PolyUtil.<C> basePseudoDivide(g,gp);
                    if ( !g.isZERO() && !g.isConstant() /*&& !B.contains(g)*/ ) {
                        B.add(g); // gcd(a,g) == 1 and gcd(g,c) == 1 for c != b in P, B
                    }
                    g = gp;
                    gp = gcd(a,gp).abs();
                }
                // make new g co-prime to new b
                gp = gcd(b,g).abs();
                while ( !gp.isONE() ) {
                    b = PolyUtil.<C> basePseudoDivide(b,gp);
                    g = PolyUtil.<C> basePseudoDivide(g,gp);
                    if ( !g.isZERO() && !g.isConstant() /*&& !B.contains(g)*/ ) {
                        B.add(g); // gcd(a,g) == 1 and gcd(g,c) == 1 for c != b in P, B
                    }
                    g = gp;
                    gp = gcd(b,gp).abs();
                }
                if ( !g.isZERO() && !g.isConstant() /*&& !B.contains(g)*/ ) {
                    B.add(g); // gcd(a,g) == 1 and gcd(g,c) == 1 for c != b in P, B
                }
            }
            if ( !b.isZERO() && !b.isConstant() /*&& !B.contains(b)*/ ) {
                B.add(b); // gcd(a,b) == 1 and gcd(b,c) == 1 for c != b in P, B
            }
        }
        if ( !a.isZERO() && !a.isConstant() /*&& !B.contains(a)*/ ) {
            B.add(a);
        }
        return B;
    }


    /**
     * GenPolynomial test for co-prime list.
     * @param A list of GenPolynomials.
     * @return true if gcd(b,c) = 1 for all b != c in B, else false.
     */
    public boolean isCoPrime(List<GenPolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            return true;
        }
        if ( A.size() == 1 ) {
            return true;
        }
        for ( int i = 0; i < A.size(); i++ ) {
            GenPolynomial<C> a = A.get(i);
            for ( int j = i+1; j < A.size(); j++ ) {
                GenPolynomial<C> b = A.get(j);
                GenPolynomial<C> g = gcd(a,b);
                if ( !g.isONE() ) {
                    System.out.println("not co-prime, a: " + a);
                    System.out.println("not co-prime, b: " + b);
                    System.out.println("not co-prime, g: " + g);
                    return false;
                } 
            }
        }
        return true;
    }


    /**
     * GenPolynomial test for co-prime list of given list.
     * @param A list of GenPolynomials.
     * @param P list of co-prime GenPolynomials.
     * @return true if isCoPrime(P) and for all a in A exists p in P with p | a, else false.
     */
    public boolean isCoPrime(List<GenPolynomial<C>> P, List<GenPolynomial<C>> A) {
        if ( !isCoPrime(P) ) {
            return false;
        }
        if (A == null || A.isEmpty()) {
            return true;
        }
        for ( GenPolynomial<C> q : A ) {
            if ( q.isZERO() || q.isConstant() ) {
                continue;
            }
            boolean divides = false;
            for ( GenPolynomial<C> p : P ) {
                GenPolynomial<C> a = PolyUtil.<C> basePseudoRemainder(q,p);
                if ( a.isZERO() ) { // p divides q
                    divides = true;
                    break;
                }
            }
            if ( !divides ) {
                System.out.println("no divisor for: " + q);
                return false;
            } 
        }
        return true;
    }

}
