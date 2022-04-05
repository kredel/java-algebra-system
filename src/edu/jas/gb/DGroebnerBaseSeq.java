/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.RingElem;


/**
 * D-Groebner Base sequential algorithm. Implements D-Groebner bases and GB
 * test. <b>Note:</b> Minimal reduced GBs are not unique. see BWK, section 10.1.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class DGroebnerBaseSeq<C extends RingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = LogManager.getLogger(DGroebnerBaseSeq.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Reduction engine.
     */
    protected DReduction<C> dred; // shadow super.red ??


    /**
     * Constructor.
     */
    public DGroebnerBaseSeq() {
        this(new DReductionSeq<C>());
    }


    /**
     * Constructor.
     * @param dred D-Reduction engine
     */
    public DGroebnerBaseSeq(DReduction<C> dred) {
        super(dred);
        this.dred = dred;
        assert this.dred == super.red;
    }


    /**
     * D-Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a D-Groebner base, else false.
     */
    @Override
    public boolean isGB(int modv, List<GenPolynomial<C>> F) {
        GenPolynomial<C> pi, pj, s, d;
        for (int i = 0; i < F.size(); i++) {
            pi = F.get(i);
            for (int j = i + 1; j < F.size(); j++) {
                pj = F.get(j);
                if (!dred.moduleCriterion(modv, pi, pj)) {
                    continue;
                }
                d = dred.GPolynomial(pi, pj);
                if (!d.isZERO()) {
                    // better check for top reduction only
                    d = dred.normalform(F, d);
                }
                if (!d.isZERO()) {
                    System.out.println("d-pol(" + pi + "," + pj + ") != 0: " + d);
                    return false;
                }
                // works ok
                if (!dred.criterion4(pi, pj)) {
                    continue;
                }
                s = dred.SPolynomial(pi, pj);
                if (!s.isZERO()) {
                    s = dred.normalform(F, s);
                }
                if (!s.isZERO()) {
                    System.out.println("s-pol(" + i + "," + j + ") != 0: " + s);
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * D-Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a D-Groebner base of F.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        List<GenPolynomial<C>> G = normalizeZerosOnes(F);
        if (G.size() <= 1) {
            return G;
        }
        GenPolynomialRing<C> ring = G.get(0).ring;
        OrderedDPairlist<C> pairlist = new OrderedDPairlist<C>(modv, ring);
        pairlist.put(G);

        Pair<C> pair;
        GenPolynomial<C> pi, pj, S, D, H;
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            //System.out.println("pair = " + pair);
            if (pair == null)
                continue;
            pi = pair.pi;
            pj = pair.pj;
            if (debug) {
                logger.debug("pi    = {}", pi);
                logger.debug("pj    = {}", pj);
            }
            H = null;

            // D-polynomial case ----------------------
            D = dred.GPolynomial(pi, pj);
            //System.out.println("D_d = " + D);
            if (!D.isZERO() && !dred.isTopReducible(G, D)) {
                H = dred.normalform(G, D);
                if (H.isONE()) {
                    G.clear();
                    G.add(H);
                    return G; // since no threads are activated
                }
                if (!H.isZERO()) {
                    logger.info("Dred = {}", H);
                    //l++;
                    G.add(H);
                    pairlist.put(H);
                }
            }

            // S-polynomial case -----------------------
            if (pair.getUseCriterion3() && pair.getUseCriterion4()) {
                S = dred.SPolynomial(pi, pj);
                //System.out.println("S_d = " + S);
                if (S.isZERO()) {
                    pair.setZero();
                    //continue;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("ht(S) = {}", S.leadingExpVector());
                }
                H = dred.normalform(G, S);
                if (H.isZERO()) {
                    pair.setZero();
                    //continue;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("ht(H) = {}", H.leadingExpVector());
                }
                if (H.isONE()) {
                    G.clear();
                    G.add(H);
                    return G; // since no threads are activated
                }
                logger.debug("H = {}", H);
                if (!H.isZERO()) {
                    logger.info("Sred = {}", H);
                    //len = G.size();
                    //l++;
                    G.add(H);
                    pairlist.put(H);
                }
            }
        }
        logger.debug("#sequential list = {}", G.size());
        G = minimalGB(G);
        logger.info("{}", pairlist);
        return G;
    }


    /**
     * Extended Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return a container for an extended Groebner base of F.
     */
    @Override
    public ExtendedGB<C> extGB(int modv, List<GenPolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            throw new IllegalArgumentException("null or empty F not allowed");
        }
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        List<List<GenPolynomial<C>>> F2G = new ArrayList<List<GenPolynomial<C>>>();
        List<List<GenPolynomial<C>>> G2F = new ArrayList<List<GenPolynomial<C>>>();
        OrderedDPairlist<C> pairlist = null;
        boolean oneInGB = false;
        int len = F.size();

        List<GenPolynomial<C>> row = null;
        List<GenPolynomial<C>> rows = null;
        List<GenPolynomial<C>> rowh = null;
        GenPolynomialRing<C> ring = null;
        GenPolynomial<C> H;
        GenPolynomial<C> p;

        int nzlen = 0;
        for (GenPolynomial<C> f : F ) {
            if (f.length() > 0) {
                nzlen++;
            }
            if (ring == null) {
                ring = f.ring;
            }
        }
        GenPolynomial<C> one = ring.getONE();
        int k = 0;
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while (it.hasNext()) {
            p = it.next();
            if (p.length() > 0) {
                row = blas.genVector(nzlen, null);
                int s = p.leadingBaseCoefficient().signum();
                if (s < 0) {
                    p = p.negate();
                    row.set(k, one.negate());
                } else {
                    row.set(k, one);
                }
                k++;
                if (p.isUnit()) {
                    G.clear(); G.add(p);
                    G2F.clear(); G2F.add(row);
                    oneInGB = true;
                    break;
                }
                G.add(p);
                logger.info("p row = {}", row);
                G2F.add(row);
                if (pairlist == null) {
                    //pairlist = strategy.create(modv, p.ring);
                    pairlist = new OrderedDPairlist<C>(modv, p.ring);
                    if (p.ring.coFac.isField()) {
                        //throw new RuntimeException("coefficients from a field");
                        logger.warn("coefficients from a field " + p.ring.coFac);
                    }
                }
                // putOne not required
                pairlist.put(p);
            } else {
                len--;
            }
        }
        ExtendedGB<C> exgb;
        if (len <= 1 || oneInGB) {
            // adjust F2G
            for (GenPolynomial<C> f : F) {
                row = blas.genVector(G.size(), null);
                H = dred.normalform(row, G, f);
                if (! H.isZERO()) {
                    logger.error("nonzero H = {}", H );
                }
                logger.info("f row = {}", row);
                F2G.add(row);
            }
            exgb = new ExtendedGB<C>(F,G,F2G,G2F);
            System.out.println("exgb 1 = " + exgb);
            return exgb;
        }

        Pair<C> pair;
        int i, j;
        GenPolynomial<C> pi, pj;
        GenPolynomial<C> S, D;
        GenPolynomial<C> x, y;
        k = 0;
        while (pairlist.hasNext() && ! oneInGB) {
            pair = pairlist.removeNext();
            if (pair == null) {
                continue;
            }
            i = pair.i;
            j = pair.j;
            pi = pair.pi;
            pj = pair.pj;
            if (true||debug) {
                logger.info("i, pi    = {}, {}", i, pi );
                logger.info("j, pj    = {}, {}", j, pj );
            }
            H = null;

            // D-polynomial case ----------------------
            rows = blas.genVector(G.size(), null);
            D = dred.GPolynomial(rows, i, pi, j, pj);
            logger.info("Gpol = {}", D);
            if (true||debug) {
                logger.info("is reduction D = "
                            + dred.isReductionNF(rows, G, D, ring.getZERO()) );
            }
            if (!D.isZERO() && !dred.isTopReducible(G, D)) {
                //continue;
                rowh = blas.genVector(G.size(), null);
                H = dred.normalform(rowh, G, D);
                if (true||debug) {
                    logger.info("is reduction H = "
                                + dred.isReductionNF(rowh, G, D, H) );
                }
                //H = H.monic();
                int s = H.leadingBaseCoefficient().signum();
                if (s < 0) {
                    H = H.negate();
                    //rowh = rowh.negate(); //rowh.set(G.size(), one.negate());
                    rowh = blas.scalarProduct(one.negate(), rowh);
                }
                if (H.isONE()) {
                    G.clear();
                    G.add(H);
                } else if (!H.isZERO()) {
                    logger.info("H_G red = {}", H);
                    G.add(H);
                    pairlist.put(H);
                }
                //System.out.println("rowd = " + rows);
                //rows = blas.scalarProduct(one.negate(), rows); //.negate()
                System.out.println("rowd = " + rows);
                System.out.println("rowh = " + rowh);
                row = blas.vectorCombineRep(rows,rowh);
                logger.info("H_G row = {}", row);
                G2F.add(row);
                if (true||debug) {
                    logger.debug("ht(H) = {}", H.leadingExpVector() );
                    logger.info("is reduction D,H = "
                                + dred.isReductionNF(row, G, H, ring.getZERO()) );
                }
            }

            // S-polynomial case ----------------------
            rows = blas.genVector(G.size(), null);
            S = dred.SPolynomial(rows, i, pi, j, pj);
            logger.info("Spol = {}", S);
            if (true||debug) {
                logger.info("is reduction S = "
                            + dred.isReductionNF(rows, G, S, ring.getZERO()) );
            }
            rowh = blas.genVector(G.size(), null);
            if (!S.isZERO() && !dred.isTopReducible(G, S)) {
                //continue;
                H = dred.normalform(rowh, G, S);
                if (true||debug) {
                    logger.info("is reduction H = "
                                + dred.isReductionNF(rowh, G, S, H) );
                }
                //H = H.monic();
                int s = H.leadingBaseCoefficient().signum();
                if (s < 0) {
                    logger.info("negate: H_S rowh = {}", rowh);
                    H = H.negate();
                    //rowh = rowh.negate(); //rowh.set(G.size(), one.negate());
                    rowh = blas.scalarProduct(one.negate(), rowh);
                }
                if (H.isONE()) {
                    G.clear();
                    G.add(H);
                } else if (!H.isZERO()) {
                    logger.info("H_S red = {}", H);
                    G.add(H);
                    pairlist.put(H);
                }
                System.out.println("rows = " + rows);
                System.out.println("rowh = " + rowh);
                row = blas.vectorCombineRep(rows,rowh);
                logger.info("H_S row = {}", row);
                G2F.add(row);
                if (true||debug) {
                    logger.debug("ht(H) = {}", H.leadingExpVector() );
                    logger.info("is reduction S,H = "
                                + dred.isReductionNF(row, G, H, ring.getZERO()) );
                }
            }
        }
        if (true||debug) {
            exgb = new ExtendedGB<C>(F, G, F2G, G2F);
            logger.info("exgb unnorm = {}", exgb);
            boolean t3 = isReductionMatrix(exgb);
            logger.info("exgb t_12 = {}", t3);
            //return exgb;
        }
        G2F = normalizeMatrix(F.size(), G2F);
        if (true||debug) {
            exgb = new ExtendedGB<C>(F, G, F2G, G2F);
            logger.info("exgb norm nonmin = {}", exgb);
            boolean t2 = isReductionMatrix(exgb);
            logger.info("exgb t2 = {}", t2);
            //return exgb;
        }
        exgb = minimalExtendedGB(F.size(), G, G2F);
        G = exgb.G;
        G2F = exgb.G2F;
        logger.debug("#sequential list = {}", G.size());
        logger.info("{}", pairlist);
        // setup matrices F and F2G
        for (GenPolynomial<C> f : F) {
            row = blas.genVector(G.size(), null);
            H = dred.normalform(row, G, f);
            if (! H.isZERO()) {
                logger.error("nonzero H, G = {}, {}", H, G);
                throw new RuntimeException("H != 0");
            }
            F2G.add( row );
        }
        exgb = new ExtendedGB<C>(F,G,F2G,G2F);
        if (debug) {
            logger.info("exgb nonmin = {}", exgb);
            boolean t2 = isReductionMatrix( exgb );
            logger.info("exgb t2 = {}", t2);
        }
        return exgb;
    }

}
