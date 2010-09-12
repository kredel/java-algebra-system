/*
 * $Id$
 */

package edu.jas.ps;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.RingElem;


/**
 * Multivariate power series reduction sequential use algorithm. Implements Mora
 * normalform.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ReductionSeq<C extends RingElem<C>> // should be FieldElem<C>>
/*extends ReductionAbstract<C>*/{


    private static final Logger logger = Logger.getLogger(ReductionSeq.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public ReductionSeq() {
    }


    /**
     * Module criterium.
     * @param modv number of module variables.
     * @param A power series.
     * @param B power series.
     * @return true if the module S-power-series(i,j) is required.
     */
    public boolean moduleCriterion(int modv, MultiVarPowerSeries<C> A, MultiVarPowerSeries<C> B) {
        if (modv == 0) {
            return true;
        }
        ExpVector ei = A.orderExpVector();
        ExpVector ej = B.orderExpVector();
        return moduleCriterion(modv, ei, ej);
    }


    /**
     * Module criterium.
     * @param modv number of module variables.
     * @param ei ExpVector.
     * @param ej ExpVector.
     * @return true if the module S-power-series(i,j) is required.
     */
    public boolean moduleCriterion(int modv, ExpVector ei, ExpVector ej) {
        if (modv == 0) {
            return true;
        }
        if (ei.invLexCompareTo(ej, 0, modv) != 0) {
            return false; // skip pair
        }
        return true;
    }


    /**
     * GB criterium 4. Use only for commutative power series rings.
     * @param A power series.
     * @param B power series.
     * @param e = lcm(ht(A),ht(B))
     * @return true if the S-power-series(i,j) is required, else false.
     */
    public boolean criterion4(MultiVarPowerSeries<C> A, MultiVarPowerSeries<C> B, ExpVector e) {
        if (logger.isInfoEnabled()) {
            if (!A.ring.equals(B.ring)) {
                logger.error("rings not equal " + A.ring + ", " + B.ring);
            }
            if (!A.ring.isCommutative()) {
                logger.error("GBCriterion4 not applicabable to non-commutative power series");
                return true;
            }
        }
        ExpVector ei = A.orderExpVector();
        ExpVector ej = B.orderExpVector();
        ExpVector g = ei.sum(ej);
        // boolean t =  g == e ;
        ExpVector h = g.subtract(e);
        int s = h.signum();
        return !(s == 0);
    }


    /**
     * S-Power-series, S-polynomial.
     * @param A power series.
     * @param B power series.
     * @return spol(A,B) the S-power-series of A and B.
     */
    public MultiVarPowerSeries<C> SPolynomial(MultiVarPowerSeries<C> A, MultiVarPowerSeries<C> B) {
        if (B == null /*|| B.isZERO()*/) {
            if (A == null) {
                return B;
            }
            return A.ring.getZERO();
        }
        if (A == null /*|| A.isZERO()*/) {
            return B.ring.getZERO();
        }
        if (debug) {
            if (!A.ring.equals(B.ring)) {
                logger.error("rings not equal " + A.ring + ", " + B.ring);
            }
        }
        Map.Entry<ExpVector, C> ma = A.orderMonomial();
        Map.Entry<ExpVector, C> mb = B.orderMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g = e.lcm(f);
        ExpVector e1 = g.subtract(e);
        ExpVector f1 = g.subtract(f);

        C a = ma.getValue();
        C b = mb.getValue();

        MultiVarPowerSeries<C> Ap = A.multiply(b, e1);
        MultiVarPowerSeries<C> Bp = B.multiply(a, f1);
        MultiVarPowerSeries<C> C = Ap.subtract(Bp);
        return C;
    }


    /**
     * Top normalform with Mora's algorithm.
     * @param Ap power series.
     * @param Pp power series list.
     * @return top-nf(Ap) with respect to Pp.
     */
    //@SuppressWarnings("unchecked") 
    public MultiVarPowerSeries<C> normalform(List<MultiVarPowerSeries<C>> Pp, MultiVarPowerSeries<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null) {
            return Ap;
        }
        if (!Ap.ring.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }
        int l;
        MultiVarPowerSeries<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = new MultiVarPowerSeries[l];
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i);
            }
        }
        ArrayList<ExpVector> htl = new ArrayList<ExpVector>(l);
        ArrayList<C> lbc = new ArrayList<C>(l);
        ArrayList<MultiVarPowerSeries<C>> p = new ArrayList<MultiVarPowerSeries<C>>(l);
        ArrayList<Long> ecart = new ArrayList<Long>(l);
        Map.Entry<ExpVector, C> m;
        int i;
        int j = 0;
        for (i = 0; i < l; i++) {
            m = P[i].orderMonomial();
            //System.out.println("m_i = " + m);
            if (m != null) {
                p.add(P[i]);
                //System.out.println("e = " + m.getKey().toString(Ap.ring.vars));
                htl.add(m.getKey());
                lbc.add(m.getValue());
                ecart.add(P[i].ecart());
                j++;
            }
        }
        l = j;
        MultiVarPowerSeries<C> R = Ap.ring.getZERO();
        //System.out.println("R = " + R);
        MultiVarPowerSeries<C> S = Ap;
        m = S.orderMonomial();
        while (true) {
            //System.out.println("m = " + m);
            //System.out.println("S = " + S);
            if (m == null) {
                R = R.sum(S);
                return R;
            }
            if (S.isZERO()) {
                return R;
            }
            ExpVector e = m.getKey();
            if (debug) {
                logger.debug("e = " + e.toString(Ap.ring.vars));
            }
            if (e.totalDeg() > S.truncate()) {
                throw new RuntimeException("not convergent, deg = " + e.totalDeg() + " > " + S.truncate());
            }
            // search ps with ht(ps) | ht(S)
            List<Integer> li = new ArrayList<Integer>();
            for (i = 0; i < l; i++) {
                if (e.multipleOf(htl.get(i))) {
                    //System.out.println("m = " + m);
                    li.add(i);
                }
            }
            if (li.size() == 0) {
                R = R.sum(S);
                return R;
            }
            //System.out.println("li = " + li);
            // select ps with smallest ecart
            long mi = Long.MAX_VALUE;
            for (int k = 0; k < li.size(); k++) {
                int ki = li.get(k);
                long x = ecart.get(ki); //p.get( ki ).ecart();
                if (x < mi) { // first or last?
                    mi = x;
                    i = ki;
                }
            }
            //System.out.println("i = " + i + ", p_i = " + p.get(i));
            long si = S.ecart();
            if (mi > si) {
                //System.out.println("ecart_i = " + mi + ", ecart_S = " + si + ", S+ = " + S);
                p.add(S);
                htl.add(m.getKey());
                lbc.add(m.getValue());
                ecart.add(si);
                l++;
            }
            e = e.subtract(htl.get(i));
            C a = m.getValue().divide(lbc.get(i));
            MultiVarPowerSeries<C> Q = p.get(i).multiply(a, e);
            S = S.subtract(Q);
            m = S.orderMonomial();
        }
    }


    /**
     * Total reduced normalform with Mora's algorithm.
     * @param A power series.
     * @param P power series list.
     * @return total-nf(A) with respect to P.
     */
    public MultiVarPowerSeries<C> totalNormalform(List<MultiVarPowerSeries<C>> P, MultiVarPowerSeries<C> A) {
        if (P == null || P.isEmpty()) {
            return A;
        }
        if (A == null) {
            return A;
        }
        MultiVarPowerSeries<C> R = normalform(P,A);
        if ( R.isZERO() ) {
            return R;
        }
        MultiVarCoefficients<C> Rc = new MultiVarCoefficients<C>(A.ring) {
            @Override
            public C generate(ExpVector i) { // will not be used
                return pfac.coFac.getZERO();
            }
        };
        GenPolynomialRing<C> pfac = A.lazyCoeffs.pfac;
        while ( !R.isZERO() ) {
              Map.Entry<ExpVector, C> m = R.orderMonomial();
              R = R.reductum();
              ExpVector e = m.getKey();
              long t = e.totalDeg();
              GenPolynomial<C> p = Rc.coeffCache.get(t);
              if ( p == null ) {
                  p = pfac.getZERO();
              }
              p = p.sum(m.getValue(),e);
              Rc.coeffCache.put(t,p); 
              // zeros need never update

              R = normalform(P,R);
        }
        R = R.sum(Rc);
        return R;
    }


    /**
     * Total reduced normalform with Mora's algorithm.
     * @param P power series list.
     * @return total-nf(p) for p with respect to P\{p}.
     */
    public List<MultiVarPowerSeries<C>> totalNormalform(List<MultiVarPowerSeries<C>> P) {
        if (P == null || P.isEmpty()) {
            return P;
        }
        List<MultiVarPowerSeries<C>> R = new ArrayList<MultiVarPowerSeries<C>>(P.size());
        List<MultiVarPowerSeries<C>> S = new ArrayList<MultiVarPowerSeries<C>>(P);
        for ( MultiVarPowerSeries<C> a : P ) {
            S.remove(a);
            MultiVarPowerSeries<C> b = totalNormalform(S,a);
            S.add(a);
            R.add(b);
        }
        return R;
    }


    /**
     * Is top reducible.
     * @param A power series.
     * @param P power series list.
     * @return true if A is top reducible with respect to P.
     */
    public boolean isTopReducible(List<MultiVarPowerSeries<C>> P, MultiVarPowerSeries<C> A) {
        if (P == null || P.isEmpty()) {
            return false;
        }
        if (A == null) {
            return false;
        }
        ExpVector e = A.orderExpVector();
        for (MultiVarPowerSeries<C> p : P) {
            if (e.multipleOf(p.orderExpVector())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Ideal containment. Test if each b in B is contained in ideal S. 
     * @param S standard base.
     * @param B list of power series
     * @return true, if each b in B is contained in ideal(S), else false
     */
    public boolean contains(List<MultiVarPowerSeries<C>> S, List<MultiVarPowerSeries<C>> B) {
        if (B == null || B.size() == 0) {
            return true;
        }
        if (S == null || S.size() == 0) {
            return true;
        }
        for (MultiVarPowerSeries<C> b : B) {
            if (b == null) {
                continue;
            }
            MultiVarPowerSeries<C> z = normalform(S, b);
            if (!z.isZERO()) {
                System.out.println("contains nf(b) != 0: " + b + ", z = " + z);
                return false;
            }
        }
        return true;
    }

}
