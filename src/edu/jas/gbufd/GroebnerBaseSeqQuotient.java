/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.ufd.PolyUfdUtil;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;
import edu.jas.structure.GcdRingElem;


/**
 * Groebner Base sequential algorithm for rational function coefficients, fraction free
 * computation. Implements Groebner bases.
 * @param <C> Quotient coefficient type
 * @author Heinz Kredel
 */

public class GroebnerBaseSeqQuotient<C extends GcdRingElem<C>> extends GroebnerBaseAbstract<Quotient<C>> {


    private static final Logger logger = Logger.getLogger(GroebnerBaseSeqQuotient.class);


    private final boolean debug = logger.isDebugEnabled();


    public final GroebnerBaseAbstract<GenPolynomial<C>> bba;


    /**
     * Constructor.
     * @param rf quotient coefficient ring factory.
     */
    public GroebnerBaseSeqQuotient(QuotientRing<C> rf) {
        super();
        bba = new GroebnerBasePseudoRecSeq<C>(rf.ring);
    }


    /**
     * Groebner base using fraction free computation.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    @Override
    public List<GenPolynomial<Quotient<C>>> GB(int modv, List<GenPolynomial<Quotient<C>>> F) {
        List<GenPolynomial<Quotient<C>>> G = F;
        if (F == null || F.isEmpty()) {
            return G;
        }
        GenPolynomialRing<Quotient<C>> rring = F.get(0).ring;
        QuotientRing<C> cf = (QuotientRing<C>) rring.coFac;
        GenPolynomialRing<GenPolynomial<C>> iring = new GenPolynomialRing<GenPolynomial<C>>(cf.ring, rring);
        List<GenPolynomial<GenPolynomial<C>>> Fi = PolyUfdUtil.<C> integralFromQuotientCoefficients(iring, F);
        //System.out.println("Fi = " + Fi);
        logger.info("#Fi = " + Fi.size());

        List<GenPolynomial<GenPolynomial<C>>> Gi = bba.GB(modv, Fi);
        //System.out.println("Gi = " + Gi);
        logger.info("#Gi = " + Gi.size());

        G = PolyUfdUtil.<C> quotientFromIntegralCoefficients(rring, Gi);
        G = PolyUtil.<Quotient<C>> monic(G);
        return G;
    }


    /**
     * Minimal ordered Groebner basis.
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     */
    @Override
    public List<GenPolynomial<Quotient<C>>> minimalGB(List<GenPolynomial<Quotient<C>>> Gp) {
        if (Gp == null || Gp.size() <= 1) {
            return Gp;
        }
        // remove zero polynomials
        List<GenPolynomial<Quotient<C>>> G = new ArrayList<GenPolynomial<Quotient<C>>>(Gp.size());
        for (GenPolynomial<Quotient<C>> a : Gp) {
            if (a != null && !a.isZERO()) { // always true in GB()
                // already positive a = a.abs();
                G.add(a);
            }
        }
        if (G.size() <= 1) {
            return G;
        }
        // remove top reducible polynomials
        GenPolynomial<Quotient<C>> a;
        List<GenPolynomial<Quotient<C>>> F;
        F = new ArrayList<GenPolynomial<Quotient<C>>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            if (red.isTopReducible(G, a) || red.isTopReducible(F, a)) {
                // drop polynomial 
                if (debug) {
                    System.out.println("dropped " + a);
                    List<GenPolynomial<Quotient<C>>> ff;
                    ff = new ArrayList<GenPolynomial<Quotient<C>>>(G);
                    ff.addAll(F);
                    a = red.normalform(ff, a);
                    if (!a.isZERO()) {
                        System.out.println("error, nf(a) " + a);
                    }
                }
            } else {
                F.add(a);
            }
        }
        G = F;
        if (G.size() <= 1) {
            return G;
        }

        // reduce remaining polynomials
        GenPolynomialRing<Quotient<C>> rring = G.get(0).ring;
        QuotientRing<C> cf = (QuotientRing<C>) rring.coFac;
        GenPolynomialRing<GenPolynomial<C>> iring = new GenPolynomialRing<GenPolynomial<C>>(cf.ring, rring);
        List<GenPolynomial<GenPolynomial<C>>> Fi = PolyUfdUtil.integralFromQuotientCoefficients(iring, F);
        logger.info("#Fi = " + Fi.size());

        List<GenPolynomial<GenPolynomial<C>>> Gi = bba.minimalGB(Fi);
        logger.info("#Gi = " + Gi.size());

        G = PolyUfdUtil.<C> quotientFromIntegralCoefficients(rring, Gi);
        G = PolyUtil.<Quotient<C>> monic(G);
        return G;
    }

}
