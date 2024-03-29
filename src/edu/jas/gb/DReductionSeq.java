/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial D-Reduction sequential use algorithm. Implements normalform.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class DReductionSeq<C extends RingElem<C>> extends ReductionAbstract<C> implements DReduction<C> {


    private static final Logger logger = LogManager.getLogger(DReductionSeq.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public DReductionSeq() {
    }


    /**
     * Is top reducible.
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is top reducible with respect to P.
     */
    @Override
    public boolean isTopReducible(List<GenPolynomial<C>> P, GenPolynomial<C> A) {
        if (P == null || P.isEmpty()) {
            return false;
        }
        if (A == null || A.isZERO()) {
            return false;
        }
        boolean mt = false;
        ExpVector e = A.leadingExpVector();
        C a = A.leadingBaseCoefficient();
        for (GenPolynomial<C> p : P) {
            mt = e.multipleOf(p.leadingExpVector());
            if (mt) {
                C b = p.leadingBaseCoefficient();
                C r = a.remainder(b);
                mt = r.isZERO();
                if (mt) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Is in Normalform.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return true if Ap is in normalform with respect to Pp.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean isNormalform(List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return true;
        }
        if (Ap == null || Ap.isZERO()) {
            return true;
        }
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = new GenPolynomial[l];
            //P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i);
            }
        }
        ExpVector[] htl = new ExpVector[l];
        C[] lbc = (C[]) new RingElem[l]; // want <C>
        GenPolynomial<C>[] p = new GenPolynomial[l];
        Map.Entry<ExpVector, C> m;
        int i;
        int j = 0;
        for (i = 0; i < l; i++) {
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        boolean mt = false;
        Map<ExpVector, C> Am = Ap.getMap();
        for (Map.Entry<ExpVector, C> me : Am.entrySet()) {
            ExpVector e = me.getKey();
            C a = me.getValue(); //Am.get(e);
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt) {
                    C r = a.remainder(lbc[i]);
                    mt = r.isZERO();
                    if (mt) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Normalform using d-reduction.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return d-nf(Ap) with respect to Pp.
     */
    @SuppressWarnings("unchecked")
    @Override
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenPolynomial<C>[]) new GenPolynomial[l];
            //P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i); //.abs();
            }
        }
        //System.out.println("l = " + l);
        Map.Entry<ExpVector, C> m;
        ExpVector[] htl = new ExpVector[l];
        C[] lbc = (C[]) new RingElem[l]; // want <C>
        GenPolynomial<C>[] p = (GenPolynomial<C>[]) new GenPolynomial[l];
        int i;
        int j = 0;
        for (i = 0; i < l; i++) {
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e;
        C a;
        C r = null;
        boolean mt = false;
        GenPolynomial<C> R = Ap.ring.getZERO();
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
        while (S.length() > 0) {
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt) {
                    r = a.remainder(lbc[i]);
                    mt = r.isZERO(); // && mt
                }
                if (mt)
                    break;
            }
            if (!mt) {
                //logger.debug("irred");
                R = R.sum(a, e);
                //S = S.subtract( a, e ); 
                S = S.reductum();
                //System.out.println(" S = " + S);
            } else {
                //logger.info("red div = {}", e);
                ExpVector f = e.subtract(htl[i]);
                C b = a.divide(lbc[i]);
                // r == 0: R = R.sum(r, e);
                Q = p[i].multiply(b, f);
                S = S.reductum().subtract(Q.reductum()); // ok also with reductum
            }
        }
        return R.abs(); // ok with d-reduction
    }


    /**
     * S-Polynomial.
     * @param Ap polynomial.
     * @param Bp polynomial.
     * @return spol(Ap,Bp) the S-polynomial of Ap and Bp.
     */
    @Override
    public GenPolynomial<C> SPolynomial(GenPolynomial<C> Ap, GenPolynomial<C> Bp) {
        if (logger.isInfoEnabled()) {
            if (Bp == null || Bp.isZERO()) {
                return Ap.ring.getZERO();
            }
            if (Ap == null || Ap.isZERO()) {
                return Bp.ring.getZERO();
            }
            if (!Ap.ring.equals(Bp.ring)) {
                logger.error("rings not equal");
            }
        }
        Map.Entry<ExpVector, C> ma = Ap.leadingMonomial();
        Map.Entry<ExpVector, C> mb = Bp.leadingMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g = e.lcm(f);
        ExpVector e1 = g.subtract(e);
        ExpVector f1 = g.subtract(f);

        C a = ma.getValue();
        C b = mb.getValue();

        //c = lcm(a,b)
        C c = a.gcd(b);
        C m = a.multiply(b);
        C l = m.divide(c);
        C a1 = l.divide(a);
        C b1 = l.divide(b);

        //Cp = a1 e1 * A - b1 f1 * B
        GenPolynomial<C> Cp = Ap.scaleSubtractMultiple(a1, e1, b1, f1, Bp);
        return Cp;
    }


    /**
     * G-Polynomial.
     * @param Ap polynomial.
     * @param Bp polynomial.
     * @return gpol(Ap,Bp) the g-polynomial of Ap and Bp.
     */
    @Override
    public GenPolynomial<C> GPolynomial(GenPolynomial<C> Ap, GenPolynomial<C> Bp) {
        if (logger.isInfoEnabled()) {
            if (Bp == null || Bp.isZERO()) {
                return Ap.ring.getZERO();
            }
            if (Ap == null || Ap.isZERO()) {
                return Bp.ring.getZERO();
            }
            if (!Ap.ring.equals(Bp.ring)) {
                logger.error("rings not equal");
            }
        }
        Map.Entry<ExpVector, C> ma = Ap.leadingMonomial();
        Map.Entry<ExpVector, C> mb = Bp.leadingMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g = e.lcm(f);
        ExpVector e1 = g.subtract(e);
        ExpVector f1 = g.subtract(f);

        C a = ma.getValue();
        C b = mb.getValue();

        C[] c = a.egcd(b); // c[0] = c[1] a + c[2] b
        //System.out.println("egcd[0] " + c[0]);
        //Cp = c[1] e1 * A + c[2] f1 * B = c[1] e1 * A - (-c[2]) f1 * B
        GenPolynomial<C> Cp = Ap.scaleSubtractMultiple(c[1], e1, c[2].negate(), f1, Bp);
        return Cp; // .negate() -
    }


    /**
     * S-Polynomial with recording.
     * @param row recording vector, is modified.
     * @param i index of Ap in basis list.
     * @param Ap a polynomial.
     * @param j index of Bp in basis list.
     * @param Bp a polynomial.
     * @return spol(Ap, Bp), the s-Polynomial for Ap and Bp.
     */
    @Override
    public GenPolynomial<C> SPolynomial(List<GenPolynomial<C>> row, int i, GenPolynomial<C> Ap, int j,
                    GenPolynomial<C> Bp) {
        if (logger.isInfoEnabled()) {
            if (Bp == null || Bp.isZERO()) {
                return Ap.ring.getZERO();
            }
            if (Ap == null || Ap.isZERO()) {
                return Bp.ring.getZERO();
            }
            if (!Ap.ring.equals(Bp.ring)) {
                logger.error("rings not equal");
            }
            //if ((row.get(i) != null && !row.get(i).isZERO()) || (row.get(j) != null && !row.get(j).isZERO())) {
            //    throw new IllegalArgumentException("row(i), row(j): " + row.get(i) + ", " + row.get(j));
            //}
        }
        Map.Entry<ExpVector, C> ma = Ap.leadingMonomial();
        Map.Entry<ExpVector, C> mb = Bp.leadingMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g = e.lcm(f);
        ExpVector e1 = g.subtract(e);
        ExpVector f1 = g.subtract(f);

        C a = ma.getValue();
        C b = mb.getValue();

        //c = lcm(a,b)
        C c = a.gcd(b);
        C m = a.multiply(b);
        C l = m.divide(c);
        C a1 = l.divide(a);
        C b1 = l.divide(b);
        //Cp = a1 e1 * A - b1 f1 * B
        GenPolynomial<C> Cp = Ap.scaleSubtractMultiple(a1, e1, b1, f1, Bp);

        GenPolynomial<C> zero = Ap.ring.getZERO();
        GenPolynomial<C> As = zero.sum(a1, e1); /*not correct .negate()*/
        GenPolynomial<C> Bs = zero.sum(b1.negate(), f1); /*correct */
        row.set(i, As);
        row.set(j, Bs);
        return Cp;
    }


    /**
     * G-Polynomial with recording.
     * @param row recording matrix, is modified.
     * @param i index of Ap in basis list.
     * @param Ap a polynomial.
     * @param j index of Bp in basis list.
     * @param Bp a polynomial.
     * @return gpol(Ap, Bp), the g-Polynomial for Ap and Bp.
     */
    @Override
    public GenPolynomial<C> GPolynomial(List<GenPolynomial<C>> row, int i, GenPolynomial<C> Ap, int j,
                    GenPolynomial<C> Bp) {
        if (logger.isInfoEnabled()) {
            if (Bp == null || Bp.isZERO()) {
                return Ap.ring.getZERO();
            }
            if (Ap == null || Ap.isZERO()) {
                return Bp.ring.getZERO();
            }
            if (!Ap.ring.equals(Bp.ring)) {
                logger.error("rings not equal");
            }
            //if ((row.get(i) != null && !row.get(i).isZERO()) || (row.get(j) != null && !row.get(j).isZERO())) {
            //    throw new IllegalArgumentException("row(i), row(j): " + row.get(i) + ", " + row.get(j));
            //}
        }
        Map.Entry<ExpVector, C> ma = Ap.leadingMonomial();
        Map.Entry<ExpVector, C> mb = Bp.leadingMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g = e.lcm(f);
        ExpVector e1 = g.subtract(e);
        ExpVector f1 = g.subtract(f);

        C a = ma.getValue();
        C b = mb.getValue();

        C[] c = a.egcd(b); // c[0] = c[1] a + c[2] b
        //System.out.println("egcd[0] " + c[0]);
        //Cp = c[1] e1 * A + c[2] f1 * B = c[1] e1 * A - (-c[2]) f1 * B
        GenPolynomial<C> Cp = Ap.scaleSubtractMultiple(c[1], e1, c[2].negate(), f1, Bp);

        GenPolynomial<C> zero = Ap.ring.getZERO();
        GenPolynomial<C> As = zero.sum(c[1], e1); // .negate() -
        GenPolynomial<C> Bs = zero.sum(c[2], f1); // .negate() -
        row.set(i, As);
        row.set(j, Bs);
        return Cp; // .negate() -
    }


    /**
     * GB criterium 4. Use only for commutative polynomial rings. This version
     * works also for d-Groebner bases.
     * @param A polynomial.
     * @param B polynomial.
     * @param e = lcm(ht(A),ht(B))
     * @return true if the S-polynomial(i,j) is required, else false.
     */
    @Override
    public boolean criterion4(GenPolynomial<C> A, GenPolynomial<C> B, ExpVector e) {
        if (logger.isInfoEnabled()) {
            if (!A.ring.equals(B.ring)) {
                logger.error("rings equal");
            }
            if (A instanceof GenSolvablePolynomial || B instanceof GenSolvablePolynomial) {
                logger.error("GBCriterion4 not applicabable to SolvablePolynomials");
                return true;
            }
        }
        ExpVector ei = A.leadingExpVector();
        ExpVector ej = B.leadingExpVector();
        ExpVector g = ei.sum(ej);
        // boolean t =  g == e ;
        ExpVector h = g.subtract(e);
        int s = h.signum();
        if (s == 0) { // disjoint ht
            C a = A.leadingBaseCoefficient();
            C b = B.leadingBaseCoefficient();
            C d = a.gcd(b);
            if (d.isONE()) { // disjoint hc
                //System.out.println("d1 = " + d + ", a = " + a + ", b = " + b);
                return false; // can skip pair
            }
        }
        return true; //! ( s == 0 );
    }


    /**
     * GB criterium 4. Use only for commutative polynomial rings. This version
     * works also for d-Groebner bases.
     * @param A polynomial.
     * @param B polynomial.
     * @return true if the S-polynomial(i,j) is required, else false.
     */
    @Override
    public boolean criterion4(GenPolynomial<C> A, GenPolynomial<C> B) {
        if (logger.isInfoEnabled()) {
            if (A instanceof GenSolvablePolynomial || B instanceof GenSolvablePolynomial) {
                logger.error("GBCriterion4 not applicabable to SolvablePolynomials");
                return true;
            }
        }
        ExpVector ei = A.leadingExpVector();
        ExpVector ej = B.leadingExpVector();
        ExpVector g = ei.sum(ej);
        ExpVector e = ei.lcm(ej);
        //        boolean t =  g == e ;
        ExpVector h = g.subtract(e);
        int s = h.signum();
        if (s == 0) { // disjoint ht
            C a = A.leadingBaseCoefficient();
            C b = B.leadingBaseCoefficient();
            C d = a.gcd(b);
            if (d.isONE()) { // disjoint hc
                return false; // can skip pair
            }
        }
        return true; //! ( s == 0 );
    }


    /**
     * Normalform with recording.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the normal form of Ap wrt. Pp.
     */
    @SuppressWarnings("unchecked")
    @Override
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> row, List<GenPolynomial<C>> Pp,
                    GenPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenPolynomial<C>[]) new GenPolynomial[l];
            //P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i);
            }
        }
        ExpVector[] htl = new ExpVector[l];
        C[] lbc = (C[]) new RingElem[l]; // want <C>
        GenPolynomial<C>[] p = (GenPolynomial<C>[]) new GenPolynomial[l];
        Map.Entry<ExpVector, C> m;
        int j = 0;
        int i;
        for (i = 0; i < l; i++) {
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e = null;
        ExpVector f = null;
        C a = null;
        C b = null;
        C r = null;
        GenPolynomialRing<C> pfac = Ap.ring;
        GenPolynomial<C> R = pfac.getZERO();
        GenPolynomial<C> zero = pfac.getZERO();

        GenPolynomial<C> fac = null;
        // GenPolynomial<C> T = null;
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
        while (S.length() > 0) {
            boolean mt = false;
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt) {
                    r = a.remainder(lbc[i]);
                    mt = r.isZERO();
                }
                if (mt)
                    break;
            }
            if (!mt) {
                //logger.debug("irred");
                R = R.sum(a, e);
                S = S.subtract(a, e);
                //System.out.println("a = " + a + ", e = " + e);
            } else {
                //logger.info("red div = {}", e);
                f = e.subtract(htl[i]);
                b = a.divide(lbc[i]);
                //System.out.println("b = " + b + ", f = " + f);
                // r == 0: R = R.sum(r, f);
                Q = p[i].multiply(b, f);
                S = S.reductum().subtract(Q.reductum()); // ok also with reductum
                fac = row.get(i);
                if (fac == null) {
                    fac = zero.sum(b, f);
                } else {
                    fac = fac.sum(b, f);
                }
                row.set(i, fac);
            }
        }
        return R;
    }


    /**
     * Irreducible set.
     * @param Pp polynomial list.
     * @return a list P of polynomials which are in normalform wrt. P.
     */
    @Override
    public List<GenPolynomial<C>> irreducibleSet(List<GenPolynomial<C>> Pp) {
        ArrayList<GenPolynomial<C>> P = new ArrayList<GenPolynomial<C>>();
        if (Pp == null) {
            return null;
        }
        for (GenPolynomial<C> a : Pp) {
            if (!a.isZERO()) {
                P.add(a);
            }
        }
        int l = P.size();
        if (l <= 1)
            return P;

        int irr = 0;
        ExpVector e;
        ExpVector f;
        GenPolynomial<C> a;
        logger.debug("irr = ");
        while (irr != l) {
            a = P.remove(0);
            e = a.leadingExpVector();
            a = normalform(P, a);
            logger.debug(String.valueOf(irr));
            if (a.isZERO()) {
                l--;
                if (l <= 1) {
                    return P;
                }
            } else {
                f = a.leadingExpVector();
                if (e.equals(f)) {
                    irr++;
                } else {
                    irr = 0;
                }
                P.add(a);
            }
        }
        //System.out.println();
        return P;
    }


    // inherit is okay:
    //public boolean isReductionNF(List<GenPolynomial<C>> row, List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap,
    //                 GenPolynomial<C> Np) {

}
