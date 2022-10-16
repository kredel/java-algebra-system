/*
 * $Id$
 */

package edu.jas.gb;


import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.kern.TimeStatus;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingElem;


/**
 * Non-commutative word Groebner Base sequential algorithm. Implements Groebner
 * bases and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class WordGroebnerBaseSeq<C extends RingElem<C>> extends WordGroebnerBaseAbstract<C> {


    private static final Logger logger = LogManager.getLogger(WordGroebnerBaseSeq.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public WordGroebnerBaseSeq() {
        super();
        TimeStatus.setActive();
        TimeStatus.setLimit(10);
        TimeStatus.setCallBack(new TimeStatus.TSCall(false));
    }


    /**
     * Constructor.
     * @param red Reduction engine
     */
    public WordGroebnerBaseSeq(WordReduction<C> red) {
        super(red);
        TimeStatus.setActive();
        TimeStatus.setLimit(10);
        TimeStatus.setCallBack(new TimeStatus.TSCall(false));
    }


    /**
     * Constructor.
     * @param red Reduction engine
     * @param pl pair selection strategy
     */
    public WordGroebnerBaseSeq(WordReduction<C> red, WordPairList<C> pl) {
        super(red, pl);
        TimeStatus.setActive();
        TimeStatus.setLimit(10);
        TimeStatus.setCallBack(new TimeStatus.TSCall(false));
    }


    /**
     * Word Groebner base using word pairlist class.
     * @param F word polynomial list.
     * @return GB(F) a finite non-commutative Groebner base of F, if it exists.
     */
    @Override
    public List<GenWordPolynomial<C>> GB(List<GenWordPolynomial<C>> F) {
        List<GenWordPolynomial<C>> G = normalizeZerosOnes(F);
        G = PolyUtil.<C> wordMonic(G);
        if (G.size() <= 1) {
            return G;
        }
        GenWordPolynomialRing<C> ring = G.get(0).ring;
        if (!ring.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }
        //Collections.sort(G);
        OrderedWordPairlist<C> pairlist = (OrderedWordPairlist<C>) strategy.create(ring);
        pairlist.put(G);
        logger.info("start {}", pairlist);

        WordPair<C> pair;
        GenWordPolynomial<C> pi;
        GenWordPolynomial<C> pj;
        List<GenWordPolynomial<C>> S;
        GenWordPolynomial<C> H;
        int infin = 0;
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            //logger.debug("pair = {}", pair);
            if (pair == null) {
                continue;
            }
            pi = pair.pi;
            pj = pair.pj;
            if (debug) {
                logger.info("pi   = {}, pj = {}", pi, pj);
            }

            S = red.SPolynomials(pi, pj);
            if (S.isEmpty()) {
                continue;
            }
            for (GenWordPolynomial<C> s : S) {
                if (s.isZERO()) {
                    //pair.setZero();
                    continue;
                }
                if (debug) {
                    logger.info("ht(S) = {}", s.leadingWord());
                }
                boolean t = pairlist.criterion3(pair.i, pair.j, s.leadingWord());
                //System.out.println("criterion3(" + pair.i + "," + pair.j + ") = " + t);
                //if ( !t ) {
                //    continue;  
                //}

                H = red.normalform(G, s);
                if (debug) {
                    //logger.info("pair = {}", pair);
                    //logger.info("ht(S) = {}", S.monic()); //.leadingWord() );
                    logger.info("ht(H) = {}", H.monic()); //.leadingWord() );
                }
                if (H.isZERO()) {
                    //pair.setZero();
                    continue;
                }
                if (!t) {
                    logger.info("criterion3({},{}) wrong: {} --> {}", pair.i, pair.j, s.leadingWord(), H.leadingWord());
                }

                H = H.monic();
                if (debug) {
                    logger.info("ht(H) = {}", H.leadingWord());
                }
                if (H.isONE()) {
                    G.clear();
                    G.add(H);
                    return G; // since no threads are activated
                }
                if (debug) {
                    logger.info("H = {}", H);
                }
                if (H.length() > 0) {
                    //l++;
                    G.add(H);
                    pairlist.put(H);
                }
                if (H.degree() > 9) {
                    System.out.println("deg(H): " + H.degree());
                    logger.warn("word GB too high degree {}", H.degree());
                    TimeStatus.checkTime("word GB degree > 9");
                }

                if (s.leadingWord().dependencyOnVariables().equals(H.leadingWord().dependencyOnVariables())) {
                    logger.info("LT depend: {} --> {}", s.leadingWord().dependencyOnVariables(), H.leadingWord().dependencyOnVariables());
                    logger.info("LT depend: {} --> {}", s, H);
                    infin++;
                    if (infin > 500) {
                        throw new RuntimeException("no convergence in word GB: " + infin);
                    }
                }
            }
        }
        //logger.info("#sequential list = {}", G.size());
        G = minimalGB(G);
        logger.info("end   {}", pairlist);
        //Collections.sort(G);
        //Collections.reverse(G);
        return G;
    }

}
