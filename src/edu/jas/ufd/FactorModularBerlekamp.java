/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModLongRing;
import edu.jas.arith.Modular;
import edu.jas.arith.ModularRingFactory;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;
import edu.jas.vector.GenVector;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenMatrixRing;
import edu.jas.vector.LinAlg;


/**
 * Modular coefficients Berlekamp factorization algorithms. This class
 * implements Berlekamp, Cantor and Zassenhaus factorization methods
 * for polynomials over (prime) modular integers.
 * @author Heinz Kredel
 */

public class FactorModularBerlekamp<MOD extends GcdRingElem<MOD> & Modular> extends FactorAbsolute<MOD> {


    private static final Logger logger = LogManager.getLogger(FactorModularBerlekamp.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * No argument constructor, do not use.
     */
    @SuppressWarnings({ "unchecked", "unused" })
    private FactorModularBerlekamp() {
        this((RingFactory<MOD>) (Object) new ModLongRing(13, true)); // hack, 13 unimportant
    }


    /**
     * Constructor.
     * @param cfac coefficient ring factory.
     */
    public FactorModularBerlekamp(RingFactory<MOD> cfac) {
        super(cfac);
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree and monic! GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    @Override
    public List<GenPolynomial<MOD>> baseFactorsSquarefree(GenPolynomial<MOD> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<MOD>> factors = new ArrayList<GenPolynomial<MOD>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<MOD> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException("only for univariate polynomials");
        }
        if (!P.leadingBaseCoefficient().isONE()) {
            throw new IllegalArgumentException("ldcf(P) != 1: " + P);
        }
        ArrayList<ArrayList<MOD>> Q = PolyUfdUtil.<MOD>constructQmatrix(P);
        System.out.println("Q = " + Q);

        int n = Q.size();
        int m = Q.get(0).size();
        GenMatrixRing<MOD> mfac = new GenMatrixRing<MOD>(pfac.coFac,n,m);
        System.out.println("mfac = " + mfac.toScript());
        GenMatrix<MOD> Qm = new GenMatrix<MOD>(mfac,Q);
        System.out.println("Qm = " + Qm);
        GenMatrix<MOD> Qm1 = Qm.subtract(mfac.getONE());
        System.out.println("Qm1 = " + Qm1);
        LinAlg<MOD> lu = new LinAlg<MOD>();
        List<GenVector<MOD>> Nsb = lu.nullSpaceBasis(Qm1);
        System.out.println("Nsb = " + Nsb);
        int k = Nsb.size();
        int d = (int)P.degree(0);
        GenMatrix<MOD> Ns = mfac.fromVectors(Nsb);
        System.out.println("Ns = " + Ns);
        GenMatrix<MOD> L1 = Ns.negate(); //mfac.getONE().subtract(Ns);
        System.out.println("L1 = " + L1);
        List<GenPolynomial<MOD>> trials = new ArrayList<GenPolynomial<MOD>>();
        for (int i = 0; i < L1.ring.rows; i++) {
            GenVector<MOD> rv = L1.getRow(i);
            GenPolynomial<MOD> rp = pfac.fromVector(rv);
            System.out.println("rp = " + rp.toScript());
            if (!rp.isONE()) { 
                trials.add(rp);
            }
        }
        System.out.println("k = " + k);
        System.out.println("trials = " + trials);
        factors.add(P);
        // ModularRingFactory cofac = (ModularRingFactory) pfac.coFac;
        // for (Object so : cofac) {
        //     MOD sm = (MOD) so;
        //     System.out.print(" " + sm + " ");
        // }
        MOD inc = pfac.coFac.getONE();
        for (GenPolynomial<MOD> t : trials) {
            if (factors.size() == k || factors.size() == 0) {
                break;
            }
            System.out.println("t = " + t);
            GenPolynomial<MOD> a = factors.remove(0);
            System.out.println("a = " + a);
            MOD s = pfac.coFac.getZERO();
            do {//for (MOD s : pfac.coFac) {
                //System.out.println("s = " + s);
                GenPolynomial<MOD> v = t.subtract(s);
                s = s.sum(inc);
                GenPolynomial<MOD> g = v.gcd(a);
                if (g.isONE() || g.equals(a)) {
                    continue;
                }
                System.out.println("s = " + s + ", g = " + g);
                factors.add(g);
                a = a.divide(g);
                if (a.isONE()) {
                    break;
                }
            } while (!s.isZERO());
            if (!a.isONE()) {
                factors.add(a);
            }
        }
        //System.out.println("factors  = " + factors);
        factors = PolyUtil.<MOD> monic(factors);
        SortedSet<GenPolynomial<MOD>> ss = new TreeSet<GenPolynomial<MOD>>(factors);
        //System.out.println("sorted   = " + ss);
        factors.clear();
        factors.addAll(ss);
        return factors;
    }

}
