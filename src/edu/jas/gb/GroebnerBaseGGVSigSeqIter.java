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

public class GroebnerBaseGGVSigSeqIter<C extends RingElem<C>> extends GroebnerBaseSigSeqIter<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBaseGGVSigSeqIter.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public GroebnerBaseGGVSigSeqIter() {
        this(new SigReductionSeq<C>());
    }


    /**
     * Constructor.
     * @param red Reduction engine
     */
    public GroebnerBaseGGVSigSeqIter(SigReductionSeq<C> red) {
        super(red);
    }


    /**
     * S-Polynomial.
     * @param A polynomial.
     * @param B polynomial.
     * @return spol(A,B) the S-polynomial of A and B.
     */
    public GenPolynomial<C> SPolynomial(SigPoly<C> A, SigPoly<C> B) {
        return sred.SPolynomialHalf(A, B);
    }


    List<SigPair<C>> pruneP(List<SigPair<C>> P, List<ExpVector> syz) {
        List<SigPair<C>> res = new ArrayList<SigPair<C>>(P.size());
        for (SigPair<C> p : P) {
            ExpVector f = p.sigma.leadingExpVector();
            if (f == null) {
                continue;
            }
            boolean div = false;
            for (ExpVector e : syz) {
                if (f.multipleOf(e)) {
                    div = true;
                    break;
                }
            }
            if (! div) {
                res.add(p);
            }
        }
        return res;
    }

    List<SigPair<C>> pruneS(List<SigPair<C>> S, List<ExpVector> syz, Object done, List<SigPoly<C>> G) {
        List<SigPair<C>> res = new ArrayList<SigPair<C>>(S.size());
        for (SigPair<C> p : S) {
            ExpVector f = p.sigma.leadingExpVector();
            if (f == null) {
                continue;
            }
            boolean div = false;
            for (ExpVector e : syz) {
                if (f.multipleOf(e)) {
                    div = true;
                    break;
                }
            }
            if (div) {
                continue;
            }
            div = false;
            for (SigPair<C> q : S) {
                if (p.sigma.leadingExpVector().equals(q.sigma.leadingExpVector())) {
                    if (p.pi.poly.compareTo(q.pi.poly) < 0) {
                        div = true;
                        break;
                    }
                }
            }
            if (div) {
                continue;
            }
            div = false;
            for (SigPair<C> q : res) {
                if (p.sigma.leadingExpVector().equals(q.sigma.leadingExpVector())) {
                    div = true;
                    break;
                }
            }
            if (div) {
                continue;
            }
            res.add(p);
        }
        return res;
    }

    List<ExpVector> initializeSyz(List<GenPolynomial<C>> F, List<SigPoly<C>> G) {
        List<ExpVector> P = new ArrayList<ExpVector>();
        for (GenPolynomial<C> p : F) {
	    if (p.isZERO()) {
                continue;
            }
            P.add( p.leadingExpVector() );
        }
        return P;
    }

    void updateSyz(List<ExpVector> syz, SigPoly<C> r) {
        if (!r.poly.isZERO() && !r.sigma.isZERO()) {
            syz.add( r.sigma.leadingExpVector() );
        }
        return;
    }

}
