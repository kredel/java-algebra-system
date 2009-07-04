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
import edu.jas.structure.Power;

import edu.jas.poly.ExpVector;
import edu.jas.poly.Monomial;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;

import edu.jas.util.ListUtil;


/**
 * Squarefree decomposition for finite fields of characteristic p.
 * @author Heinz Kredel
 */

public class SquarefreeFiniteFieldCharP<C extends GcdRingElem<C>> {


    private static final Logger logger = Logger.getLogger(SquarefreeFiniteFieldCharP.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Factory for finite field of characteristic p coefficients.
     */
    protected final RingFactory<C> coFac;


    /**
     * GCD engine for finite field of characteristic p base coefficients.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;



    /**
     * Constructor. 
     */
    public SquarefreeFiniteFieldCharP(RingFactory<C> fac) {
//         isFinite() predicate not yet present
//         if ( !fac.isFinite() ) {
//             throw new IllegalArgumentException("fac must be finite"); 
//         }
        if ( !fac.isField() ) {
            throw new IllegalArgumentException("fac must be a field"); 
        }
        if ( fac.characteristic().signum() == 0 ) {
            throw new IllegalArgumentException("characterisic(fac) must be non-zero"); 
        }
        coFac = fac;
        //engine = GCDFactory.<C>getImplementation( fac );
        engine = GCDFactory.<C>getProxy( fac );
    }


    /** Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName() + " with " + engine + " over " + coFac;
    }


    /**
     * GenPolynomial polynomial greatest squarefree divisor.
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
        GenPolynomial<C> pp = engine.basePrimitivePart(P);
        pp = pp.monic();
        if ( pp.isConstant() ) {
            return pp;
        }
        GenPolynomial<C> d;
        long k = pp.degree(0);
        long j = 1;
        while ( true ) { 
            d = PolyUtil.<C> baseDeriviative(pp);
            d = d.monic();
            System.out.println("der(pp) = " + d);
            if ( !d.isZERO() ) { // || pp.isConstant()
                break;
            }
            long mp = pfac.characteristic().longValue(); // assert != 0
                 //pp = PolyUtil.<C> baseModRoot(pp,mp);
            pp = baseRootCharacteristic(pp);
            System.out.println("char root: pp = " + pp);
            if ( pp == null ) {
                throw new RuntimeException("can not happen");
            }
            j = j * mp;
            if ( j > k ) {
               throw new RuntimeException("polynomial mod " + mp + ", pp = " + pp + ", d = " + d);
            }
        } 
        GenPolynomial<C> g = engine.baseGcd(pp, d);
        g = g.monic();
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
        C ldbcf = A.leadingBaseCoefficient();
        if ( ! ldbcf.isONE() ) {
            A = A.divide(ldbcf);
            GenPolynomial<C> f1 = pfac.getONE().multiply(ldbcf);
            //System.out.println("gcda sqf f1 = " + f1);
            sfactors.put(f1,1L);
            ldbcf = pfac.coFac.getONE();
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
                T = engine.baseGcd(T0,Tp);
                T = T.monic();
                V = PolyUtil.<C> basePseudoDivide(T0,T);
                //System.out.println("iT0 = " + T0);
                //System.out.println("iTp = " + Tp);
                //System.out.println("iT  = " + T);
                //System.out.println("iV  = " + V);
                k = 0L;
                mp = 0L;
                init = false;
            }
            if ( V.isConstant() ) { 
                mp = pfac.characteristic().longValue(); // assert != 0
                   //T0 = PolyUtil.<C> baseModRoot(T,mp);
                T0 = baseRootCharacteristic(T);
                if ( T0 == null ) {
                   throw new RuntimeException("can not happen");
                }
                System.out.println("char root: T0 = " + T0 + ", T = " + T);
                e = e * mp;
                init = true;
                continue;
            }
            k++;
            if ( mp != 0L && k % mp == 0L ) {
                T = PolyUtil.<C> basePseudoDivide(T, V);
                System.out.println("k = " + k);
                //System.out.println("T = " + T);
                k++;
            }
            GenPolynomial<C> W = engine.baseGcd(T,V);
            W = W.monic();
            GenPolynomial<C> z = PolyUtil.<C> basePseudoDivide(V, W);
            //System.out.println("W = " + W);
            //System.out.println("z = " + z);
            V = W;
            T = PolyUtil.<C> basePseudoDivide(T, V);
            //System.out.println("V = " + V);
            //System.out.println("T = " + T);
            if ( z.degree(0) > 0 ) {
                if ( ldbcf.isONE() && !z.leadingBaseCoefficient().isONE() ) {
                    z = z.monic();
                    System.out.println("z,monic = " + z);
                }
                sfactors.put(z, (e*k));
            }
        }
//      look, a stupid error:
//         if ( !ldbcf.isONE() ) {
//             GenPolynomial<C> f1 = sfactors.firstKey();
//             long e1 = sfactors.remove(f1);
//             System.out.println("gcda sqf c = " + c);
//             f1 = f1.multiply(c);
//             //System.out.println("gcda sqf f1e = " + f1);
//             sfactors.put(f1,e1);
//         }
        return sfactors;
    }


    /**
     * GenPolynomial recursive polynomial greatest squarefree divisor.
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
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>)pfac.coFac;
        // squarefree content
        GenPolynomial<GenPolynomial<C>> pp = P;
        GenPolynomial<C> Pc = engine.recursiveContent(P);
        C cldbcf = Pc.leadingBaseCoefficient();
        if ( ! cldbcf.isONE() ) {
            C li = cldbcf.inverse();
            System.out.println("cli = " + li);
            Pc = Pc.multiply(li);
            //System.out.println("Pc,monic = " + Pc);
        }
        if ( ! Pc.isONE() ) {
           pp = PolyUtil.<C> coefficientPseudoDivide(pp, Pc);
           //System.out.println("pp,sqp = " + pp);
           GenPolynomial<C> Pr = squarefreePart(Pc);
           C rldbcf = Pr.leadingBaseCoefficient();
           if ( ! rldbcf.isONE() ) {
               C li = rldbcf.inverse();
               System.out.println("rcli = " + li);
               Pr = Pr.multiply(li);
           }
           //System.out.println("Pr,sqp = " + Pr);
        }
        if ( pp.leadingExpVector().getVal(0) < 1 ) {
            //System.out.println("pp = " + pp);
            //System.out.println("Pc = " + Pc);
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
            //pp = PolyUtil.<C> recursiveModRoot(pp,mp);
            GenPolynomial<GenPolynomial<C>> T0 = recursiveRootCharacteristic(pp);
            System.out.println("char root: pp,r = " + pp);
            System.out.println("char root: T0,r = " + T0);
            if ( T0 == null ) {
                d = pp;
                break;
            }
            pp = T0;
        } 
        // now d != 0
        GenPolynomial<GenPolynomial<C>> g = engine.recursiveUnivariateGcd(pp, d);
        //System.out.println("g,rec = " + g);
        C ldbcf = g.leadingBaseCoefficient().leadingBaseCoefficient();
        if ( ! ldbcf.isONE() ) {
            C li = ldbcf.inverse();
            System.out.println("li,rec = " + li);
            g = g.multiply( cfac.getONE().multiply(li) );
            //System.out.println("g,monic = " + g);
        }
        GenPolynomial<GenPolynomial<C>> q = PolyUtil.<C> recursivePseudoDivide(pp, g);
        C qldbcf = q.leadingBaseCoefficient().leadingBaseCoefficient();
        if ( ! qldbcf.isONE() ) {
            C li = qldbcf.inverse();
            System.out.println("li,rec,q = " + li);
            q = q.multiply( cfac.getONE().multiply(li) );
            //System.out.println("q,monic = " + q);
        }
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
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // recursiveContent not possible by return type
            throw new RuntimeException(this.getClass().getName()
                    + " only for univariate polynomials");
        }
        // if base coefficient ring is a field, make monic
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>)pfac.coFac;
        C ldbcf = P.leadingBaseCoefficient().leadingBaseCoefficient();
        if ( ! ldbcf.isONE() ) {
            GenPolynomial<C> lc = cfac.getONE().multiply(ldbcf);
            GenPolynomial<GenPolynomial<C>> pl = pfac.getONE().multiply(lc);
            sfactors.put(pl,1L);
            C li = ldbcf.inverse();
            System.out.println("li = " + li);
            P = P.multiply( cfac.getONE().multiply(li) );
            //System.out.println("P,monic = " + P);
            ldbcf = P.leadingBaseCoefficient().leadingBaseCoefficient();
        }
        // factors of content
        GenPolynomial<C> Pc = engine.recursiveContent(P);
        System.out.println("Pc = " + Pc);
        C cldbcf = Pc.leadingBaseCoefficient();
        if ( ! cldbcf.isONE() ) {
            C li = cldbcf.inverse();
            System.out.println("cli = " + li);
            Pc = Pc.multiply(li);
            //System.out.println("Pc,monic = " + Pc);
        }
        if ( !Pc.isONE() ) {
            P = PolyUtil.<C> coefficientPseudoDivide(P,Pc);
        }
        SortedMap<GenPolynomial<C>,Long> rsf = squarefreeFactors(Pc);
        System.out.println("rsf = " + rsf);
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
                T = engine.recursiveUnivariateGcd(T0,Tp);
                C tl = T.leadingBaseCoefficient().leadingBaseCoefficient();
                if ( ! tl.isONE() ) {
                    C ti = tl.inverse();
                    System.out.println("ti = " + ti);
                    GenPolynomial<C> tc = cfac.getONE().multiply(ti);
                    T = T.multiply(tc);
                }
                V = PolyUtil.<C> recursivePseudoDivide(T0,T);
                //System.out.println("iT0 = " + T0);
                //System.out.println("iTp = " + Tp);
                //System.out.println("iT = " + T);
                //System.out.println("iV = " + V);
                k = 0L;
                mp = 0L;
                init = false;
            }
            if ( V.isConstant() ) { 
                mp = pfac.characteristic().longValue(); // assert != 0
                    //T0 = PolyUtil.<C> recursiveModRoot(T,mp);
                T0 = recursiveRootCharacteristic(T);
                System.out.println("char root: T0r = " + T0 + ", Tr = " + T);
                if ( T0 == null ) {
                    break;
                }
                e = e * mp;
                init = true;
                continue;
            }
            k++;
            if ( mp != 0L && k % mp == 0L ) {
                T = PolyUtil.<C> recursivePseudoDivide(T, V);
                System.out.println("k = " + k);
                //System.out.println("T = " + T);
                k++;
            }
            GenPolynomial<GenPolynomial<C>> W = engine.recursiveUnivariateGcd(T,V);
            C wl = W.leadingBaseCoefficient().leadingBaseCoefficient();
            if ( ! wl.isONE() ) {
                C wi = wl.inverse();
                System.out.println("wi = " + wi);
                GenPolynomial<C> wc = cfac.getONE().multiply(wi);
                W = W.multiply(wc);
            }
            GenPolynomial<GenPolynomial<C>> z = PolyUtil.<C> recursivePseudoDivide(V, W);
            //System.out.println("W = " + W);
            //System.out.println("z = " + z);
            V = W;
            T = PolyUtil.<C> recursivePseudoDivide(T, V);
            //System.out.println("V = " + V);
            //System.out.println("T = " + T);
            //was: if ( z.degree(0) > 0 ) {
            if ( !z.isONE() && !z.isZERO() ) {
                C zl = z.leadingBaseCoefficient().leadingBaseCoefficient();
                if ( ldbcf.isONE() && !zl.isONE() ) {
                    C li = zl.inverse();
                    GenPolynomial<C> lc = cfac.getONE().multiply(li);
                    //System.out.println("lc = " + lc);
                    z = z.multiply(lc);
                    System.out.println("z,monic = " + z);
                }
                sfactors.put(z, (e*k));
            }
        }
        return sfactors;
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
        GenPolynomial<C> Pc = engine.recursiveContent(Pr);
        Pr = PolyUtil.<C> coefficientPseudoDivide(Pr, Pc);
        GenPolynomial<C> Ps = squarefreePart(Pc);
        GenPolynomial<GenPolynomial<C>> PP = recursiveSquarefreePart(Pr);
        GenPolynomial<GenPolynomial<C>> PS = PP.multiply(Ps);
        GenPolynomial<C> D = PolyUtil.<C> distribute(pfac, PS);
        return D;
    }


    /**
     * GenPolynomial test if is squarefree.
     * @param P GenPolynomial.
     * @return true if P is squarefree, else false.
     */
    public boolean isSquarefree(GenPolynomial<C> P) {
        GenPolynomial<C> S = squarefreePart(P);
        boolean f = P.equals(S);
        if (!f) {
            System.out.println("\nisSquarefree: " + f);
            System.out.println("S  = " + S);
            System.out.println("P  = " + P);
        }
        return f;
    }


    /**
     * Recursive GenPolynomial test if is squarefree.
     * @param P recursive GenPolynomial.
     * @return true if P is squarefree, else false.
     */
    public boolean isRecursiveSquarefree(GenPolynomial<GenPolynomial<C>> P) {
        GenPolynomial<GenPolynomial<C>> S = recursiveSquarefreePart(P);
        boolean f = P.equals(S);
        if (!f) {
            System.out.println("\nisSquarefree: " + f);
            System.out.println("S = " + S);
            System.out.println("P = " + P);
        }
        return f;
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
     * GenPolynomial is (squarefree) factorization.
     * @param P GenPolynomial.
     * @param F = [p_1,...,p_k].
     * @return true if P = prod_{i=1,...,r} p_i, else false.
     */
    public boolean isFactorization(GenPolynomial<C> P, List<GenPolynomial<C>> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        GenPolynomial<C> t = P.ring.getONE();
        for (GenPolynomial<C> f : F) {
            t = t.multiply(f);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            System.out.println("\nfactorization(list): " + f);
            System.out.println("F = " + F);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
        }
        return f;
    }


    /**
     * GenPolynomial is (squarefree) factorization.
     * @param P GenPolynomial.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i, else false.
     */
    public boolean isFactorization(GenPolynomial<C> P, SortedMap<GenPolynomial<C>, Long> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        if (P.isZERO() && F.size() == 0) {
            return true;
        }
        GenPolynomial<C> t = P.ring.getONE();
        for (GenPolynomial<C> f : F.keySet()) {
            Long E = F.get(f);
            long e = E.longValue();
            GenPolynomial<C> g = Power.<GenPolynomial<C>> positivePower(f, e);
            t = t.multiply(g);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            System.out.println("\nfactorization(map): " + f);
            System.out.println("F = " + F);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            //RuntimeException e = new RuntimeException("fac-map");
            //e.printStackTrace();
            //throw e;
        }
        return f;
    }


    /**
     * GenPolynomial is (squarefree) factorization.
     * @param P GenPolynomial.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i, else false.
     */
    public boolean isRecursiveFactorization(GenPolynomial<GenPolynomial<C>> P, 
                                            SortedMap<GenPolynomial<GenPolynomial<C>>, Long> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        if (P.isZERO() && F.size() == 0) {
            return true;
        }
        GenPolynomial<GenPolynomial<C>> t = P.ring.getONE();
        for (GenPolynomial<GenPolynomial<C>> f : F.keySet()) {
            Long E = F.get(f);
            long e = E.longValue();
            GenPolynomial<GenPolynomial<C>> g = Power.<GenPolynomial<GenPolynomial<C>>> positivePower(f, e);
            t = t.multiply(g);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            System.out.println("\nfactorization(map): " + f);
            System.out.println("F = " + F);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            //RuntimeException e = new RuntimeException("fac-map");
            //e.printStackTrace();
            //throw e;
        }
        return f;
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
        List<GenPolynomial<C>> B = engine.coPrime(S);
        return B;
    }


    /* --------- char-th roots --------------------- */

    /**
     * Characteristics root of a polynomial.
     * @param P polynomial.
     * @return [p -&gt; k] if exists k with e=k*charactristic(P) and P = p**e, else null.
     */
    public SortedMap<GenPolynomial<C>,Long> rootCharacteristic(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        java.math.BigInteger c = P.ring.characteristic();
        if ( c.signum() == 0 ) {
            return null;
        }
        SortedMap<GenPolynomial<C>,Long> root = new TreeMap<GenPolynomial<C>,Long>();
        if (P.isZERO()) {
            return root;
        }
        if (P.isONE()) {
            root.put(P,1L);
            return root;
        }
        SortedMap<GenPolynomial<C>,Long> sf = squarefreeFactors(P);
        System.out.println("sf = " + sf);
        // better: test if sf.size() == 1 // not ok
        Long k = null;
        for (GenPolynomial<C> p: sf.keySet() ) {
            if ( p.isConstant() ) {
                continue;
            }
            Long e = sf.get(p);
            java.math.BigInteger E = new java.math.BigInteger(e.toString());
            java.math.BigInteger r = E.remainder(c);
            if ( !r.equals(java.math.BigInteger.ZERO) ) {
                System.out.println("r = " + r);
                return null;
            }
            if ( k == null ) {
                k = e;
            } else if ( k.compareTo(e) >= 0 ) {
                k = e;
            }
        }
        // now c divides all exponents
        Long cl = c.longValue();
        GenPolynomial<C> rp = P.ring.getONE();
        for (GenPolynomial<C> q : sf.keySet()) {
            Long e = sf.get(q);
            if ( q.isConstant() ) {
                root.put(q,e);
                continue;
            }
            if ( e > k ) {
                long ep = e / cl;
                q = Power.<GenPolynomial<C>> positivePower(q, ep);
            }
            rp = rp.multiply(q);
        }
        if ( k != null ) {
            k = k / cl;
            root.put(rp,k);
        }
        return root;
    }


    /**
     * GenPolynomial char-th root main variable.
     * Base coefficient type must be finite field, 
     * that is ModInteger or AlgebraicNumber&lt;ModInteger&gt; etc.
     * @param P GenPolynomial.
     * @return char-th_rootOf(P), or null if no char-th root.
     */
    public GenPolynomial<C> baseRootCharacteristic( GenPolynomial<C> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // basePthRoot not possible by return type
           throw new RuntimeException(P.getClass().getName()
                     + " only for univariate polynomials");
        }
        RingFactory<C> rf = pfac.coFac;
        if ( rf.characteristic().signum() != 1  ) { 
           // basePthRoot not possible
           throw new RuntimeException(P.getClass().getName()
                     + " only for ModInteger polynomials " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<C> d = pfac.getZERO().clone();
        for ( Monomial<C> m : P ) {
            ExpVector f = m.e;  
            long fl = f.getVal(0);
            if ( fl % mp != 0 ) {
                return null;
//              throw new RuntimeException(P.getClass().getName()
//                        + " exponent not divisible by m " + fl);
            }
            fl = fl / mp;
            ExpVector e = ExpVector.create( 1, 0, fl );  
            d.doPutToMap(e,m.c);
        }
        return d; 
    }


    /**
     * GenPolynomial char-th root main variable.
     * @param P recursive univariate GenPolynomial.
     * @return char-th_rootOf(P), or null if P is no char-th root.
     */
    // param <C> base coefficient type must be ModInteger.
    public GenPolynomial<GenPolynomial<C>> 
      recursiveRootCharacteristic( GenPolynomial<GenPolynomial<C>> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // basePthRoot not possible by return type
           throw new RuntimeException(P.getClass().getName()
                     + " only for univariate polynomials");
        }
        RingFactory<GenPolynomial<C>> rf = pfac.coFac;
        if ( rf.characteristic().signum() != 1  ) { 
           // basePthRoot not possible
           throw new RuntimeException(P.getClass().getName()
                     + " only for ModInteger polynomials " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<GenPolynomial<C>> d = pfac.getZERO().clone();
        for ( Monomial<GenPolynomial<C>> m : P ) {
            ExpVector f = m.e;  
            long fl = f.getVal(0);
            if ( fl % mp != 0 ) {
                return null;
            }
            fl = fl / mp;
            SortedMap<GenPolynomial<C>, Long> sm = rootCharacteristic(m.c);
            if ( sm == null ) {
                return null;
            }
            GenPolynomial<C> r = rf.getONE();
            for (GenPolynomial<C> rp : sm.keySet() ) {
                long gl = sm.get(rp);
                if ( rp.isConstant() ) {
                    r = r.multiply(rp);
                    continue;
                }
                if ( gl > fl ) {
                    rp = Power.<GenPolynomial<C>> positivePower(rp, gl);
                }
                r = r.multiply(rp);
            }
            ExpVector e = ExpVector.create( 1, 0, fl );  
            d.doPutToMap(e,r);
        }
        return d; 
    }


    /**
     * Polynomial is char-th root.
     * @param P polynomial.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**(e_i*p), else false.
     */
    public boolean isCharRoot(GenPolynomial<C> P, SortedMap<GenPolynomial<C>, Long> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        if (P.isZERO() && F.size() == 0) {
            return true;
        }
        GenPolynomial<C> t = P.ring.getONE();
        long p = P.ring.characteristic().longValue();
        for (GenPolynomial<C> f : F.keySet()) {
            Long E = F.get(f);
            long e = E.longValue();
            GenPolynomial<C> g = Power.<GenPolynomial<C>> positivePower(f, e);
            if ( !f.isConstant() ) { 
               g = Power.<GenPolynomial<C>> positivePower(g, p);
            }
            t = t.multiply(g);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            System.out.println("\nfactorization(map): " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            P = P.monic();
            t = t.monic();
            f = P.equals(t) || P.equals(t.negate());
            if ( f ) {
                return f;
            }
            System.out.println("\nfactorization(map): " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
        }
        return f;
    }


    /**
     * Recursive polynomial is char-th root.
     * @param P recursive polynomial.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**(e_i*p), else false.
     */
    public boolean isRecursiveCharRoot(GenPolynomial<GenPolynomial<C>> P, SortedMap<GenPolynomial<GenPolynomial<C>>, Long> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        if (P.isZERO() && F.size() == 0) {
            return true;
        }
        GenPolynomial<GenPolynomial<C>> t = P.ring.getONE();
        long p = P.ring.characteristic().longValue();
        for (GenPolynomial<GenPolynomial<C>> f : F.keySet()) {
            Long E = F.get(f);
            long e = E.longValue();
            GenPolynomial<GenPolynomial<C>> g = Power.<GenPolynomial<GenPolynomial<C>>> positivePower(f, e);
            if ( !f.isConstant() ) { 
               g = Power.<GenPolynomial<GenPolynomial<C>>> positivePower(g, p);
            }
            t = t.multiply(g);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            System.out.println("\nfactorization(map): " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            P = P.monic();
            t = t.monic();
            f = P.equals(t) || P.equals(t.negate());
            if ( f ) {
                return f;
            }
            System.out.println("\nfactorization(map): " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
        }
        return f;
    }


    /**
     * Recursive polynomial is char-th root.
     * @param P recursive polynomial.
     * @param r = recursive polynomial.
     * @return true if P = r**p, else false.
     */
    public boolean isRecursiveCharRoot(GenPolynomial<GenPolynomial<C>> P, GenPolynomial<GenPolynomial<C>> r) {
        if (P == null || r == null) {
            throw new IllegalArgumentException("P and r may not be null");
        }
        if (P.isZERO() && r.isZERO()) {
            return true;
        }
        long p = P.ring.characteristic().longValue();
        GenPolynomial<GenPolynomial<C>> t = Power.<GenPolynomial<GenPolynomial<C>>> positivePower(r, p);

        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            System.out.println("\nisCharRoot: " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            P = P.monic();
            t = t.monic();
            f = P.equals(t) || P.equals(t.negate());
            if ( f ) {
                return f;
            }
            System.out.println("\nisCharRoot: " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
        }
        return f;
    }

}



