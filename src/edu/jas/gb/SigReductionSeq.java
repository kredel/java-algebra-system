/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.RingElem;


/**
 * Polynomial SigReduction class. Implements common S-Polynomial,
 * normalform.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class SigReductionSeq<C extends RingElem<C>> implements SigReduction<C> {


    private static final Logger logger = Logger.getLogger(SigReductionSeq.class);


    //private static final boolean debug = logger.isDebugEnabled();


    final ReductionAbstract<C> red;


    /**
     * Constructor.
     */
    public SigReductionSeq() {
        red = new ReductionSeq<C>();
    }


    /**
     * S-Polynomial.
     * @param A polynomial.
     * @param B polynomial.
     * @return spol(A,B) the S-polynomial of A and B.
     */
    public GenPolynomial<C> SPolynomial(SigPoly<C> A, SigPoly<C> B) {
        GenPolynomial<C> s = red.SPolynomial(A.poly, B.poly);
        return s;
    }


    /**
     * S-Polynomial factors.
     * @param A monic polynomial.
     * @param B monic polynomial.
     * @return exponent vectors [e,f] such that spol(A,B) = e*a - f*B.
     */
    public ExpVector[] SPolynomialExpVectorFactors(SigPoly<C> A, SigPoly<C> B) {
        Map.Entry<ExpVector, C> ma = A.poly.leadingMonomial();
        Map.Entry<ExpVector, C> mb = B.poly.leadingMonomial();
        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();
        ExpVector g = e.lcm(f);
        ExpVector e1 = g.subtract(e);
        ExpVector f1 = g.subtract(f);
        ExpVector[] F = new ExpVector[]{ e1, f1 };
        return F;
    }


    /**
     * S-Polynomial half.
     * @param A monic polynomial.
     * @param B monic polynomial.
     * @return e*A "half" of an S-polynomial such that spol(A,B) = e*A - f*B.
     */
    public GenPolynomial<C> SPolynomialHalf(SigPoly<C> A, SigPoly<C> B) {
        Map.Entry<ExpVector, C> ma = A.poly.leadingMonomial();
        Map.Entry<ExpVector, C> mb = B.poly.leadingMonomial();
        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();
        ExpVector g = e.lcm(f);
        ExpVector e1 = g.subtract(e);
        GenPolynomial<C> F = A.poly.multiply(e1);
        return F;
    }


    /**
     * S-Polynomial polynomial factors.
     * @param A monic polynomial.
     * @param B monic polynomial.
     * @return polynomials [e,f] such that spol(A,B) = e*a - f*B.
     */
    public GenPolynomial<C>[] SPolynomialFactors(SigPoly<C> A, SigPoly<C> B) {
        ExpVector[] ev = SPolynomialExpVectorFactors(A,B);
        GenPolynomial<C> e1 = A.poly.ring.valueOf(ev[0]);
        GenPolynomial<C> f1 = A.poly.ring.valueOf(ev[1]);
        @SuppressWarnings("unchecked")
        GenPolynomial<C>[] F = new GenPolynomial[]{ e1, f1 };
        return F;
    }


    /**
     * Is top reducible. Condition is lt(B) | lt(A) for some B in F or G.
     * @param A polynomial.
     * @param F polynomial list.
     * @param G polynomial list.
     * @return true if A is top reducible with respect to F and G.
     */
    public boolean isSigReducible(List<SigPoly<C>> F, List<SigPoly<C>> G, SigPoly<C> A) {
        return !isSigNormalform(F, G, A);
    }


    /**
     * Is in top normalform.
     * @param A polynomial.
     * @param F polynomial list.
     * @param G polynomial list.
     * @return true if A is in top normalform with respect to F and G.
     */
    public boolean isSigNormalform(List<SigPoly<C>> F, List<SigPoly<C>> G, SigPoly<C> A) {
        if (F.isEmpty() && G.isEmpty()) {
            return true;
        }
        if (A.poly.isZERO()) {
            return true;
        }
        boolean mt = false;
        for (ExpVector e : A.poly.getMap().keySet()) {
            for (SigPoly<C> p : F) {
                ExpVector f = p.poly.leadingExpVector();
                mt = e.multipleOf(f);
                if (mt) {
                     return false;
                }
            }
            for (SigPoly<C> p : G) {
                if (p.poly.isZERO()) {
                    continue;
                }
                ExpVector f = p.poly.leadingExpVector();
                mt = e.multipleOf(f);
                if (mt) {
                    ExpVector g = e.subtract(f); 
                    GenPolynomial<C> sigma = p.sigma.multiply(g);
                    if (sigma.leadingExpVector().compareTo(A.sigma.leadingExpVector()) < 0 ) {
                        return false;
                    }
                    if (sigma.leadingExpVector().compareTo(A.sigma.leadingExpVector()) == 0
                        && sigma.leadingBaseCoefficient().compareTo(A.sigma.leadingBaseCoefficient()) != 0   ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }



    /**
     * Is sigma redundant.
     * @param A polynomial.
     * @param G polynomial list.
     * @return true if A is sigma redundant with respect to G.
     */
    public boolean isSigRedundant(List<SigPoly<C>> G, SigPoly<C> A) {
        if (G.isEmpty()) {
            return false;
        }
        ExpVector e = A.sigma.leadingExpVector();
        if (e == null) {
            e = A.poly.ring.evzero;
        }
        for (SigPoly<C> p : G) {
            if (p.sigma.isZERO()) {
                continue;
            }
            ExpVector f = p.sigma.leadingExpVector();
            if (f == null) {
                f = p.poly.ring.evzero;
            }
            boolean mt = e.multipleOf(f);
            if (mt) {
                ExpVector g = e.subtract(f); 
                ExpVector h = p.poly.leadingExpVector();
                h = h.sum(g);
                if (h.compareTo(A.poly.leadingExpVector()) == 0 ) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Top normalform.
     * @param A polynomial.
     * @param F polynomial list.
     * @param G polynomial list.
     * @return nf(A) with respect to F and G.
     */
    public SigPoly<C> sigNormalform(List<GenPolynomial<C>> F, List<SigPoly<C>> G, SigPoly<C> A) {
        if (F.isEmpty() && G.isEmpty()) {
            return A;
        }
        if (A.poly.isZERO()) {
            return A;
        }
        List<GenPolynomial<C>> ff = F; //polys(F);
        GenPolynomial<C> a = A.poly;
        GenPolynomial<C> sigma = A.sigma;
        GenPolynomialRing<C> ring = a.ring;
        boolean reduced = true;
        while (!a.isZERO() && reduced) {
            reduced = false;
            a = red.normalform(ff,a);
            if (a.isZERO()) {
                continue;
            }
            ExpVector e = a.leadingExpVector();
            for (SigPoly<C> p : G) {
                if (p.poly.isZERO()) {
                    continue;
                }
                ExpVector f = p.poly.leadingExpVector();
                boolean mt = e.multipleOf(f);
                if (mt) {
                    ExpVector g = e.subtract(f); 
                    C sc = a.leadingBaseCoefficient().divide(p.poly.leadingBaseCoefficient());
                    GenPolynomial<C> sigup = p.sigma.multiply(sc, g);
                    ExpVector se = sigma.leadingExpVector();
                    if (se == null) {
                        se = ring.evzero;
                    }
                    ExpVector sp = sigup.leadingExpVector();
                    if (sp == null) {
                        sp = ring.evzero;
                    }
                    //logger.info("sp, se        = " + sp + ", " + se);
                    //logger.info("sigup, sigma  = " + sigup + ", " + sigma);
                    boolean sigeq = (sigup.compareTo(sigma) < 0)
                        || ( (sp.compareTo(se) == 0
                              && (sigup.leadingBaseCoefficient().compareTo(sigma.leadingBaseCoefficient()) != 0) ));
                    //logger.info("sigup < sigma = " + sigup.compareTo(sigma));
                    //logger.info("sp  < se  = " + sp.invGradCompareTo(se));
                    //logger.info("spc < sec = " + sigup.leadingBaseCoefficient().compareTo(sigma.leadingBaseCoefficient()));
                    if (sigeq) {
                        reduced = true;
                        a = a.subtractMultiple(sc, g, p.poly);
                        if (sp.invGradCompareTo(se) == 0) {
                            sigma = sigma.subtract(sigup);
                        }
                        //logger.info("sigma, a = " + sigma + ", a = " + a + ", sigup = " + sigup);
                        if (a.isZERO()) {
                            break;
                        }
                        e = a.leadingExpVector();
                    } else {
                        //logger.info("not reduced: a = " + a + ", p = " + p.poly);
                        //logger.info("not reduced: sigma = " + sigma + ", sigup = " + sigup);
                    }
                }
            }
        }
        C ac = a.leadingBaseCoefficient();
        if ( !ac.isONE() ) {
            ac = ac.inverse();
            a = a.multiply(ac);
            sigma = sigma.multiply(ac);
        }
        return new SigPoly<C>(sigma, a);
    }    


    /**
     * Top semi-complete normalform.
     * @param A polynomial.
     * @param F polynomial list.
     * @param G polynomial list.
     * @return nf(A) with respect to F and G.
     */
    public SigPoly<C> sigSemiNormalform(List<GenPolynomial<C>> F, List<SigPoly<C>> G, SigPoly<C> A) {
        if (F.isEmpty() && G.isEmpty()) {
            return A;
        }
        if (A.poly.isZERO()) {
            return A;
        }
        List<GenPolynomial<C>> ff = F; //polys(F);
        GenPolynomial<C> a = A.poly;
        GenPolynomial<C> sigma = A.sigma;
        GenPolynomialRing<C> ring = a.ring;
        boolean reduced = true;
        while (!a.isZERO() && reduced) {
            reduced = false;
            a = red.normalform(ff,a);
            if (a.isZERO()) {
                continue;
            }
            ExpVector e = a.leadingExpVector();
            for (SigPoly<C> p : G) {
                if (p.poly.isZERO()) {
                    continue;
                }
                ExpVector f = p.poly.leadingExpVector();
                boolean mt = e.multipleOf(f);
                if (mt) {
                    ExpVector g = e.subtract(f); 
                    C sc = a.leadingBaseCoefficient().divide(p.poly.leadingBaseCoefficient());
                    GenPolynomial<C> sigup = p.sigma.multiply(sc, g);

                    //wrong: boolean sigeq = (sigup.compareTo(sigma) < 0);
                    boolean sigeq = (sigup.leadingExpVector().compareTo(sigma.leadingExpVector()) < 0);
                    if (sigeq) {
                        //logger.info("reduced: sigup = " + sigup + ", sigma = " + sigma);
                        reduced = true;
                        a = a.subtractMultiple(sc, g, p.poly);
                        if (a.isZERO()) {
                            break;
                        }
                        e = a.leadingExpVector();
                    } else {
                        //logger.info("not reduced: sigup = " + sigup + ", sigma = " + sigma);
                        //logger.info("not reduced: a = " + a + ", p = " + p.poly);
                    }
                }
            }
        }
        C ac = a.leadingBaseCoefficient();
        if ( !ac.isONE() ) {
            ac = ac.inverse();
            a = a.multiply(ac);
        }
        return new SigPoly<C>(sigma, a);
    }    


    public List<GenPolynomial<C>> polys(List<SigPoly<C>> F) {
        List<GenPolynomial<C>> ff = new ArrayList<GenPolynomial<C>>();
        for (SigPoly<C> p : F) {
            if (!p.poly.isZERO()) { 
                ff.add(p.poly);
            }
        }
        return ff;
    }


    public long minimalSigDegree(List<SigPair<C>> F) {
        long deg = Long.MAX_VALUE;
        for (SigPair<C> p : F) {
            long d = p.sigma.totalDegree();
            if (d < deg) { 
                deg = d;
            }
        }
        return deg;
    }


    public List<SigPair<C>>[] minDegSubset(List<SigPair<C>> F) {
        long mdeg = minimalSigDegree(F);
        List<SigPair<C>> ff = new ArrayList<SigPair<C>>();
        List<SigPair<C>> pp = new ArrayList<SigPair<C>>();
        for (SigPair<C> p : F) {
            if (p.sigma.totalDegree() == mdeg) { 
                ff.add(p);
            } else {
                pp.add(p);
            }
        }
        @SuppressWarnings("unchecked")
	List<SigPair<C>>[] P = new List[2];
        P[0] = ff;
        P[1] = pp;
        return P;
    }


    public List<SigPair<C>> sortSigma(List<SigPair<C>> F) {
        //GenPolynomialRing<C> ring = F.get(0).poly.ring;
        //final Comparator<GenPolynomial<C>> cmp = ring.getComparator();
        Comparator<SigPair<C>> sigcmp = Comparator.comparing(SigPair::getSigma);
        List<SigPair<C>> ff = F.stream().sorted(sigcmp).collect(Collectors.toList());
        return ff;
    }

}
