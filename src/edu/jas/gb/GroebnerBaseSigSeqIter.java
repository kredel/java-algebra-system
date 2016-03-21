/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.OrderedPolynomialList;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingElem;


/**
 * Groebner Base signatur based sequential iterative
 * algorithm. Implements Groebner bases and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 * 
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see edu.jas.gbufd.GBFactory
 */

public class GroebnerBaseSigSeqIter<C extends RingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBaseSigSeqIter.class);


    private static final boolean debug = logger.isDebugEnabled();


    final SigReductionSeq<C> sred;


    /**
     * Constructor.
     */
    public GroebnerBaseSigSeqIter() {
        this(new SigReductionSeq<C>());
    }


    /**
     * Constructor.
     * @param red Reduction engine
     */
    public GroebnerBaseSigSeqIter(SigReductionSeq<C> red) {
        super();
        sred = red;
    }


    /**
     * Groebner base using pairlist class, iterative algorithm.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        List<GenPolynomial<C>> G = normalizeZerosOnes(F);
        G = PolyUtil.<C> monic(G);
        if (G.size() <= 1) {
            return G;
        }
        //logger.info("F = " + G);
        // sort, no reverse
        G = OrderedPolynomialList.<C> sort(G);
        //no: Collections.reverse(G);
        logger.info("G-sort = " + G);
        List<GenPolynomial<C>> Gp = new ArrayList<GenPolynomial<C>>();
        for (GenPolynomial<C> p : G) {
            if (debug) {
                logger.info("p = " + p);
            }
            Gp = GB(modv, Gp, p);
            //System.out.println("GB(Gp+p) = " + Gp);
            if (Gp.size() > 0) {
                if (Gp.get(0).isONE()) {
                    return Gp;
                }
            }
        }
        return Gp;
    }


    /**
     * Groebner base using pairlist class.
     * @param modv module variable number.
     * @param G polynomial list of a Groebner base.
     * @param f polynomial.
     * @return GB(G,f) a Groebner base of G+(f).
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> G, GenPolynomial<C> f) {
        List<GenPolynomial<C>> F = new ArrayList<GenPolynomial<C>>(G);
        GenPolynomial<C> g = f.monic();
        if (F.isEmpty()) {
            F.add(g);
            return F; // commutative
        }
        if (g.isZERO()) {
            return F;
        }
        if (g.isONE()) {
            F.clear();
            F.add(g);
            return F;
        }
        GenPolynomialRing<C> ring = F.get(0).ring;
        if (!ring.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }
        List<SigPoly<C>> Gs = new ArrayList<SigPoly<C>>();
        for ( GenPolynomial<C> p : F ) {
            Gs.add( new SigPoly<C>(ring.getZERO(), p) );
        }
        SigPoly<C> gs = new SigPoly<C>(ring.getONE(), g);
        Gs.add( gs );
        List<SigPair<C>> pairlist = new ArrayList<SigPair<C>>();
        for ( SigPoly<C> p : Gs ) {
            if (p.equals(gs)) {
                continue;
            }
            pairlist.add( newPair(gs,p,Gs) );
        }
        //PairList<C> pairlist = strategy.create(modv, ring);
        //pairlist.setList(G);
        //G.add(g);
        //pairlist.put(g);
        logger.info("start " + pairlist.size());

        List<ExpVector> syz = initializeSyz(F,Gs);
        List<SigPoly<C>> done = new ArrayList<SigPoly<C>>();

        SigPair<C> pair;
        SigPoly<C> pi, pj;
        GenPolynomial<C> S, H, sigma;
        while (!pairlist.isEmpty()) {
            pairlist = pruneP(pairlist,syz);
            if (pairlist.isEmpty()) {
                continue;
            }
            List<SigPair<C>>[] spl = sred.minDegSubset(pairlist);
            List<SigPair<C>> Sl = spl[0];
            long mdeg = sred.minimalSigDegree(Sl);
            pairlist = spl[1];
            logger.info("treating " + Sl.size() + " signatures of degree " + mdeg);
            //logger.info("Sl(" + mdeg + ") = " + Sl);
            //logger.info("Gs    = " + Gs);
            ///Sl = sred.sortSigma(Sl);
            //logger.info("Sl_sort = " + Sl);
            while (!Sl.isEmpty()) {
                Sl = pruneS(Sl,syz,done,Gs);
                if (Sl.isEmpty()) {
                    continue;
                }
                Sl = sred.sortSigma(Sl);
                //logger.info("Sl_sort = " + Sl);
                pair = Sl.remove(0);
                if (pair == null) {
                    continue;
                }
                //logger.info("pair.sigma = " + pair.sigma);
                //S = SPolynomial(pair.pi, pair.pj);
                S = SPolynomial(pair);
                if (S.isZERO()) {
                    //pair.setZero();
                    continue;
                }
                if (debug) {
                    logger.debug("ht(S) = " + S.leadingExpVector());
                }
 
                SigPoly<C> Ss = new SigPoly<C>(pair.sigma, S);
                SigPoly<C> Hs = sigNormalform(F, Gs, Ss);
                H = Hs.poly;
                sigma = Hs.sigma;
                if (debug) {
                    //logger.info("pair = " + pair); 
                    //logger.info("ht(S) = " + S.monic()); //.leadingExpVector() );
                    logger.info("Hs = " + Hs); //.leadingExpVector() );
                }
                if (H.isZERO()) {
                    //pair.setZero();
                    updateSyz(syz,Hs);
                    done.add(Hs);
                    continue;
                }
                H = H.monic();
                if (debug) {
                    logger.info("ht(H) = " + H.leadingExpVector());
                }

                H = H.monic();
                if (H.isONE()) {
                    G.clear();
                    G.add(H);
                    //pairlist.putOne();
                    logger.info("end " + pairlist);
                    return G; // since no threads are activated
                }
                if (sred.isSigRedundant(Gs,Hs)) {
                    continue;
                }
                if (debug) {
                    logger.info("Hs = " + Hs);
                }
                if (H.length() > 0) {
                    //l++;
                    //pairlist.put(H);
                    for ( SigPoly<C> p : Gs ) {
                        if (p.poly.isZERO()) {
                            continue;
                        }
                        //System.out.println("p = " + p);
                        GenPolynomial<C> tau = p.sigma;
                        GenPolynomial<C>[] mult = SPolynomialFactors(Hs,p);
                        //System.out.print("sigma = " + sigma + ", tau = " + tau);
                        //System.out.println(", mult  = " + Arrays.toString(mult));
                        ExpVector se = sigma.leadingExpVector();
                        if (se == null) {
                            se = ring.evzero;
                        }
                        ExpVector te = tau.leadingExpVector();
                        if (te == null) {
                            te = ring.evzero;
                        }
                        if ( mult[0].multiply(se).equals(mult[1].multiply(te)) ) {
                            logger.debug("skip by sigma");
                            continue;
                        }
                        SigPair<C> pp;
                        if ( mult[0].multiply(se).compareTo(mult[1].multiply(te)) > 0 ) {
                            pp = newPair(sigma.multiply(mult[0]),Hs,p,Gs);
                        } else {
                            pp = newPair(tau.multiply(mult[1]),p,Hs,Gs);
                        }
                        //pp = newPair(Hs,p,Gs);
                        //System.out.println("new pair: pp.sigma = " + pp.sigma + ", xx.sigma = " + (new SigPair<C>(Hs,p,Gs)).sigma + ", compareTo " + (mult[0].multiply(se).compareTo(mult[1].multiply(te))));
                        if (pp.sigma.totalDegree() == mdeg) {
                            Sl.add( pp );
                        } else {
                            pairlist.add( pp );
                        }
                    }
                    Gs.add(Hs);
                    done.add(Hs);
                }
            }
        }
        logger.info("#sequential list = " + Gs.size());
        G = minimalGB( sred.polys(Gs) );
        logger.info("end " + pairlist);
        return G;
    }


    /**
     * S-Polynomial.
     * @param p pair.
     * @return spol(A,B) the S-polynomial of the pair (A,B).
     */
    public GenPolynomial<C> SPolynomial(SigPair<C> P) {
        return sred.SPolynomial(P.pi, P.pj);
    }


    /**
     * S-Polynomial polynomial factors.
     * @param A monic polynomial.
     * @param B monic polynomial.
     * @return polynomials [e,f] such that spol(A,B) = e*a - f*B.
     */
    public GenPolynomial<C>[] SPolynomialFactors(SigPoly<C> A, SigPoly<C> B) {
        return sred.SPolynomialFactors(A, B);
    }


    public SigPair<C> newPair(SigPoly<C> A, SigPoly<C> B, List<SigPoly<C>> G) {
        ExpVector e = A.poly.leadingExpVector().lcm(B.poly.leadingExpVector()).subtract(A.poly.leadingExpVector());
	return new SigPair<C>(e,A,B,G);
    }


    public SigPair<C> newPair(GenPolynomial<C> s, SigPoly<C> A, SigPoly<C> B, List<SigPoly<C>> G) {
	return new SigPair<C>(s,A,B,G);
    }


    /**
     * Top normalform.
     * @param A polynomial.
     * @param F polynomial list.
     * @param G polynomial list.
     * @return nf(A) with respect to F and G.
     */
    public SigPoly<C> sigNormalform(List<GenPolynomial<C>> F, List<SigPoly<C>> G, SigPoly<C> A) {
        return sred.sigNormalform(F, G, A);
    }


    List<SigPair<C>> pruneP(List<SigPair<C>> P, List<ExpVector> syz) {
        return P;
    }

    List<SigPair<C>> pruneS(List<SigPair<C>> S, List<ExpVector> syz, List<SigPoly<C>> done, List<SigPoly<C>> G) {
        return S;
    }

    List<ExpVector> initializeSyz(List<GenPolynomial<C>> F, List<SigPoly<C>> G) {
        List<ExpVector> P = new ArrayList<ExpVector>();
        return P;
    }

    void updateSyz(List<ExpVector> syz, SigPoly<C> r) {
        return;
    }

}
