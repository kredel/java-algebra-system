/*
 * $Id$
 */

package edu.jas.application;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import edu.jas.gb.ExtendedGB;
import edu.jas.gb.SolvableExtendedGB;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.gb.SolvableGroebnerBaseSeq;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.gb.Reduction;
import edu.jas.gb.SolvableReduction;
import edu.jas.gb.SolvableReductionSeq;
import edu.jas.gbufd.GBFactory;
import edu.jas.gbufd.GroebnerBasePartial;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.OptimizedPolynomialList;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderOptimization;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.NotInvertibleException;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.PolyUfdUtil;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Solvable Ideal implements some methods for ideal arithmetic, for example intersection,
 * quotient.
 * @author Heinz Kredel
 */
public class SolvableIdeal<C extends GcdRingElem<C>> implements Comparable<SolvableIdeal<C>>, Serializable {


    private static final Logger logger = Logger.getLogger(SolvableIdeal.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * The data structure is a PolynomialList.
     */
    protected PolynomialList<C> list;


    /**
     * Indicator if list is a Groebner Base.
     */
    protected boolean isGB;


    /**
     * Indicator if test has been performed if this is a Groebner Base.
     */
    protected boolean testGB;


    /**
     * Indicator if list has optimized term order.
     */
    protected boolean isTopt;


    /**
     * Groebner base engine.
     */
    protected final SolvableGroebnerBaseAbstract<C> bb;


    /**
     * Reduction engine.
     */
    protected final SolvableReduction<C> red;


    /**
     * Constructor.
     * @param ring solvable polynomial ring
     */
    public SolvableIdeal(GenSolvablePolynomialRing<C> ring) {
        this(ring, new ArrayList<GenSolvablePolynomial<C>>());
    }


    /**
     * Constructor.
     * @param ring solvable polynomial ring
     * @param F list of solvable polynomials
     */
    public SolvableIdeal(GenSolvablePolynomialRing<C> ring, List<GenSolvablePolynomial<C>> F) {
        this(new PolynomialList<C>(ring, F));
    }


    /**
     * Constructor.
     * @param ring solvable polynomial ring
     * @param F list of solvable polynomials
     * @param gb true if F is known to be a Groebner Base, else false
     */
    public SolvableIdeal(GenSolvablePolynomialRing<C> ring, List<GenSolvablePolynomial<C>> F, boolean gb) {
        this(new PolynomialList<C>(ring, F), gb);
    }


    /**
     * Constructor.
     * @param ring solvable polynomial ring
     * @param F list of solvable polynomials
     * @param gb true if F is known to be a Groebner Base, else false
     * @param topt true if term order is optimized, else false
     */
    public SolvableIdeal(GenSolvablePolynomialRing<C> ring, List<GenSolvablePolynomial<C>> F, boolean gb, boolean topt) {
        this(new PolynomialList<C>(ring, F), gb, topt);
    }


    /**
     * Constructor.
     * @param list polynomial list
     */
    public SolvableIdeal(PolynomialList<C> list) {
        this(list, false);
    }


    /**
     * Constructor.
     * @param list polynomial list
     * @param bb Groebner Base engine
     * @param red Reduction engine
     */
    public SolvableIdeal(PolynomialList<C> list, SolvableGroebnerBaseAbstract<C> bb, SolvableReduction<C> red) {
        this(list, false, bb, red);
    }


    /**
     * Constructor.
     * @param list polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     */
    public SolvableIdeal(PolynomialList<C> list, boolean gb) {
        this(list, gb, new SolvableGroebnerBaseSeq<C>(), new SolvableReductionSeq<C>());
        //this(list, gb, GBFactory.getImplementation(list.ring.coFac));
    }


    /**
     * Constructor.
     * @param list polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     * @param topt true if term order is optimized, else false
     */
    public SolvableIdeal(PolynomialList<C> list, boolean gb, boolean topt) {
        this(list, gb, topt, new SolvableGroebnerBaseSeq<C>(), new SolvableReductionSeq<C>());
        //this(list, gb, topt, GBFactory.getImplementation(list.ring.coFac));
    }


    /**
     * Constructor.
     * @param list polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     * @param bb Groebner Base engine
     * @param red Reduction engine
     */
    public SolvableIdeal(PolynomialList<C> list, boolean gb, SolvableGroebnerBaseAbstract<C> bb, SolvableReduction<C> red) {
        this(list, gb, false, bb, red);
    }


    /**
     * Constructor.
     * @param list polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     * @param bb Groebner Base engine
     */
    public SolvableIdeal(PolynomialList<C> list, boolean gb, SolvableGroebnerBaseAbstract<C> bb) {
        this(list, gb, false, bb, bb.sred);
    }


    /**
     * Constructor.
     * @param list polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     * @param topt true if term order is optimized, else false
     * @param bb Groebner Base engine
     */
    public SolvableIdeal(PolynomialList<C> list, boolean gb, boolean topt, SolvableGroebnerBaseAbstract<C> bb) {
        this(list, gb, topt, bb, bb.sred);
    }


    /**
     * Constructor.
     * @param list polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     * @param topt true if term order is optimized, else false
     * @param bb Groebner Base engine
     * @param red Reduction engine
     */
    public SolvableIdeal(PolynomialList<C> list, boolean gb, boolean topt, 
                         SolvableGroebnerBaseAbstract<C> bb,
                         SolvableReduction<C> red) {
        if (list == null || list.list == null) {
            throw new IllegalArgumentException("list and list.list may not be null");
        }
        this.list = list;
        this.isGB = gb;
        this.isTopt = topt;
        this.testGB = (gb ? true : false); // ??
        this.bb = bb;
        this.red = red;
    }


    /**
     * Clone this.
     * @return a copy of this.
     */
    public SolvableIdeal<C> copy() {
        return new SolvableIdeal<C>(list.copy(), isGB, isTopt, bb, red);
    }


    /**
     * Get the List of GenPolynomials.
     * @return list.list
     */
    public List<GenSolvablePolynomial<C>> getList() {
        return list.getSolvableList();
    }


    /**
     * Get the GenPolynomialRing.
     * @return list.ring
     */
    public GenSolvablePolynomialRing<C> getRing() {
        return list.getSolvableRing();
    }


    /**
     * Get the zero ideal.
     * @return ideal(0)
     */
    public SolvableIdeal<C> getZERO() {
        List<GenSolvablePolynomial<C>> z = new ArrayList<GenSolvablePolynomial<C>>(0);
        PolynomialList<C> pl = new PolynomialList<C>(getRing(), z);
        return new SolvableIdeal<C>(pl, true, isTopt, bb, red);
    }


    /**
     * Get the one ideal.
     * @return ideal(1)
     */
    public SolvableIdeal<C> getONE() {
        List<GenSolvablePolynomial<C>> one = new ArrayList<GenSolvablePolynomial<C>>(1);
        one.add(getRing().getONE());
        PolynomialList<C> pl = new PolynomialList<C>(getRing(), one);
        return new SolvableIdeal<C>(pl, true, isTopt, bb, red);
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return list.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    public String toScript() {
        // Python case
        return list.toScript();
    }


    /**
     * Comparison with any other object. Note: If both ideals are not Groebner
     * Bases, then false may be returned even the ideals are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof SolvableIdeal)) {
            logger.warn("equals no Ideal");
            return false;
        }
        SolvableIdeal<C> B = null;
        try {
            B = (SolvableIdeal<C>) b;
        } catch (ClassCastException ignored) {
            return false;
        }
        //if ( isGB && B.isGB ) {
        //   return list.equals( B.list ); requires also monic polys
        //} else { // compute GBs ?
        return this.contains(B) && B.contains(this);
        //}
    }


    /**
     * Ideal list comparison.
     * @param L other Ideal.
     * @return compareTo() of polynomial lists.
     */
    public int compareTo(SolvableIdeal<C> L) {
        return list.compareTo(L.list);
    }


    /**
     * Hash code for this ideal.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = list.hashCode();
        if (isGB) {
            h = h << 1;
        }
        if (testGB) {
            h += 1;
        }
        return h;
    }


    /**
     * Test if ZERO ideal.
     * @return true, if this is the 0 ideal, else false
     */
    public boolean isZERO() {
        return list.isZERO();
    }


    /**
     * Test if ONE is contained in the ideal. To test for a proper ideal use
     * <code>! id.isONE()</code>.
     * @return true, if this is the 1 ideal, else false
     */
    public boolean isONE() {
        return list.isONE();
    }


    /**
     * Test if this is a Groebner base.
     * @return true, if this is a Groebner base, else false
     */
    public boolean isGB() {
        if (testGB) {
            return isGB;
        }
        logger.warn("isGB computing");
        isGB = bb.isLeftGB(getList());
        testGB = true;
        return isGB;
    }


    /**
     * Do Groebner Base. compute the Groebner Base for this ideal.
     */
    @SuppressWarnings("unchecked")
    public void doGB() {
        if (isGB && testGB) {
            return;
        }
        //logger.warn("GB computing");
        List<GenSolvablePolynomial<C>> G = getList();
        logger.info("GB computing = " + G);
        G = bb.leftGB(G);
        //if (isTopt) {
        //    List<Integer> perm = ((OptimizedPolynomialList<C>) list).perm;
        //    list = new OptimizedPolynomialList<C>(perm, getRing(), G);
        //} else {
            list = new PolynomialList<C>(getRing(), G);
	//}
        isGB = true;
        testGB = true;
        return;
    }


    /**
     * Groebner Base. Get a Groebner Base for this ideal.
     * @return GB(this)
     */
    public SolvableIdeal<C> GB() {
        if (isGB) {
            return this;
        }
        doGB();
        return this;
    }


    /**
     * Ideal containment. Test if B is contained in this ideal. Note: this is
     * eventually modified to become a Groebner Base.
     * @param B ideal
     * @return true, if B is contained in this, else false
     */
    public boolean contains(SolvableIdeal<C> B) {
        if (B == null || B.isZERO()) {
            return true;
        }
        return contains(B.getList());
    }


    /**
     * Ideal containment. Test if b is contained in this ideal. Note: this is
     * eventually modified to become a Groebner Base.
     * @param b polynomial
     * @return true, if b is contained in this, else false
     */
    public boolean contains(GenSolvablePolynomial<C> b) {
        if (b == null || b.isZERO()) {
            return true;
        }
        if (this.isONE()) {
            return true;
        }
        if (this.isZERO()) {
            return false;
        }
        if (!isGB) {
            doGB();
        }
        GenSolvablePolynomial<C> z;
        z = red.leftNormalform(getList(), b);
        if (z == null || z.isZERO()) {
            return true;
        }
        return false;
    }


    /**
     * Ideal containment. Test if each b in B is contained in this ideal. Note:
     * this is eventually modified to become a Groebner Base.
     * @param B list of polynomials
     * @return true, if each b in B is contained in this, else false
     */
    public boolean contains(List<GenSolvablePolynomial<C>> B) {
        if (B == null || B.size() == 0) {
            return true;
        }
        if (this.isONE()) {
            return true;
        }
        if (!isGB) {
            doGB();
        }
        for (GenSolvablePolynomial<C> b : B) {
            if (b == null) {
                continue;
            }
            GenSolvablePolynomial<C> z = red.leftNormalform(getList(), b);
            if (!z.isZERO()) {
                //System.out.println("contains nf(b) != 0: " + b);
                return false;
            }
        }
        return true;
    }


    /**
     * Summation. Generators for the sum of ideals. Note: if both ideals are
     * Groebner bases, a Groebner base is returned.
     * @param B ideal
     * @return ideal(this+B)
     */
    public SolvableIdeal<C> sum(SolvableIdeal<C> B) {
        if (B == null || B.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return B;
        }
        int s = getList().size() + B.getList().size();
        List<GenSolvablePolynomial<C>> c;
        c = new ArrayList<GenSolvablePolynomial<C>>(s);
        c.addAll(getList());
        c.addAll(B.getList());
        SolvableIdeal<C> I = new SolvableIdeal<C>(getRing(), c, false);
        if (isGB && B.isGB) {
            I.doGB();
        }
        return I;
    }


    /**
     * Summation. Generators for the sum of ideal and a polynomial. Note: if
     * this ideal is a Groebner base, a Groebner base is returned.
     * @param b polynomial
     * @return ideal(this+{b})
     */
    public SolvableIdeal<C> sum(GenSolvablePolynomial<C> b) {
        if (b == null || b.isZERO()) {
            return this;
        }
        int s = getList().size() + 1;
        List<GenSolvablePolynomial<C>> c;
        c = new ArrayList<GenSolvablePolynomial<C>>(s);
        c.addAll(getList());
        c.add(b);
        SolvableIdeal<C> I = new SolvableIdeal<C>(getRing(), c, false);
        if (isGB) {
            I.doGB();
        }
        return I;
    }


    /**
     * Summation. Generators for the sum of this ideal and a list of
     * polynomials. Note: if this ideal is a Groebner base, a Groebner base is
     * returned.
     * @param L list of polynomials
     * @return ideal(this+L)
     */
    public SolvableIdeal<C> sum(List<GenSolvablePolynomial<C>> L) {
        if (L == null || L.isEmpty()) {
            return this;
        }
        int s = getList().size() + L.size();
        List<GenSolvablePolynomial<C>> c = new ArrayList<GenSolvablePolynomial<C>>(s);
        c.addAll(getList());
        c.addAll(L);
        SolvableIdeal<C> I = new SolvableIdeal<C>(getRing(), c, false);
        if (isGB) {
            I.doGB();
        }
        return I;
    }


    /**
     * Product. Generators for the product of ideals. Note: if both ideals are
     * Groebner bases, a Groebner base is returned.
     * @param B ideal
     * @return ideal(this*B)
     */
    public SolvableIdeal<C> product(SolvableIdeal<C> B) {
        if (B == null || B.isZERO()) {
            return B;
        }
        if (this.isZERO()) {
            return this;
        }
        int s = getList().size() * B.getList().size();
        List<GenSolvablePolynomial<C>> c;
        c = new ArrayList<GenSolvablePolynomial<C>>(s);
        for (GenSolvablePolynomial<C> p : getList()) {
            for (GenSolvablePolynomial<C> q : B.getList()) {
                q = p.multiply(q);
                c.add(q);
            }
        }
        SolvableIdeal<C> I = new SolvableIdeal<C>(getRing(), c, false);
        if (isGB && B.isGB) {
            I.doGB();
        }
        return I;
    }


    /**
     * Intersection. Generators for the intersection of ideals. Using an
     * iterative algorithm.
     * @param Bl list of ideals
     * @return ideal(cap_i B_i), a Groebner base
     */
    public SolvableIdeal<C> intersect(List<SolvableIdeal<C>> Bl) {
        if (Bl == null || Bl.size() == 0) {
            return getZERO();
        }
        SolvableIdeal<C> I = null;
        for (SolvableIdeal<C> B : Bl) {
            if (I == null) {
                I = B;
                continue;
            }
            if (I.isONE()) {
                return I;
            }
            I = I.intersect(B);
        }
        return I;
    }


    /**
     * Intersection. Generators for the intersection of ideals.
     * @param B ideal
     * @return ideal(this \cap B), a Groebner base
     */
    public SolvableIdeal<C> intersect(SolvableIdeal<C> B) {
        if (B == null || B.isZERO()) { // (0)
            return B;
        }
        if (this.isZERO()) {
            return this;
        }
        int s = getList().size() + B.getList().size();
        List<GenSolvablePolynomial<C>> c;
        c = new ArrayList<GenSolvablePolynomial<C>>(s);
        List<GenSolvablePolynomial<C>> a = getList();
        List<GenSolvablePolynomial<C>> b = B.getList();

        GenSolvablePolynomialRing<C> tfac = getRing().extend(1);
        // term order is also adjusted
        for (GenSolvablePolynomial<C> p : a) {
            p = (GenSolvablePolynomial<C>) p.extend(tfac, 0, 1L); // t*p
            c.add(p);
        }
        for (GenSolvablePolynomial<C> p : b) {
            GenSolvablePolynomial<C> q = (GenSolvablePolynomial<C>) p.extend(tfac, 0, 1L);
            GenSolvablePolynomial<C> r = (GenSolvablePolynomial<C>) p.extend(tfac, 0, 0L);
            p = (GenSolvablePolynomial<C>) r.subtract(q); // (1-t)*p
            c.add(p);
        }
        logger.warn("intersect computing GB");
        List<GenSolvablePolynomial<C>> g = bb.leftGB(c);
        if (debug) {
            logger.debug("intersect GB = " + g);
        }
        SolvableIdeal<C> E = new SolvableIdeal<C>(tfac, g, true);
        SolvableIdeal<C> I = E.intersect(getRing());
        return I;
    }


    /**
     * Intersection. Generators for the intersection of a ideal with a
     * polynomial ring. The polynomial ring of this ideal must be a contraction
     * of R and the TermOrder must be an elimination order.
     * @param R polynomial ring
     * @return ideal(this \cap R)
     */
    public SolvableIdeal<C> intersect(GenSolvablePolynomialRing<C> R) {
        if (R == null) {
            throw new IllegalArgumentException("R may not be null");
        }
        int d = getRing().nvar - R.nvar;
        if (d <= 0) {
            return this;
        }
        List<GenSolvablePolynomial<C>> H = new ArrayList<GenSolvablePolynomial<C>>(getList().size());
        for (GenSolvablePolynomial<C> p : getList()) {
            Map<ExpVector, GenPolynomial<C>> m = null;
            m = p.contract(R);
            if (debug) {
                logger.debug("intersect contract m = " + m);
            }
            if (m.size() == 1) { // contains one power of variables
                for (Map.Entry<ExpVector, GenPolynomial<C>> me : m.entrySet()) {
                    ExpVector e = me.getKey();
                    GenSolvablePolynomial<C> mv = (GenSolvablePolynomial<C>) me.getValue();
                    if (e.isZERO()) {
                        H.add(mv); //m.get(e));
                    }
                }
            }
        }
        GenSolvablePolynomialRing<C> tfac = getRing().contract(d);
        if (tfac.equals(R)) { // check 
            return new SolvableIdeal<C>(R, H, isGB, isTopt);
        }
        logger.info("tfac, R = " + tfac + ", " + R);
        // throw new RuntimeException("contract(this) != R");
        return new SolvableIdeal<C>(R, H); // compute GB
    }


    /**
     * Eliminate. Generators for the intersection of a ideal with a polynomial
     * ring. The polynomial rings must have variable names.
     * @param R polynomial ring
     * @return ideal(this \cap R)
     */
    public SolvableIdeal<C> eliminate(GenSolvablePolynomialRing<C> R) {
        if (R == null) {
            throw new IllegalArgumentException("R may not be null");
        }
        if (getRing().equals(R)) {
            return this;
        }
        String[] ename = R.getVars();
        SolvableIdeal<C> I = null; //eliminate(ename);
        return I.intersect(R);
    }


    /*
     * Eliminate. Preparation of generators for the intersection of a ideal with
     * a polynomial ring.
     * @param ename variables for the elimination ring.
     * @return ideal(this) in K[ename,{vars \ ename}])
    public SolvableIdeal<C> eliminate(String... ename) {
        //System.out.println("ename = " + Arrays.toString(ename));
        if (ename == null) {
            throw new IllegalArgumentException("ename may not be null");
        }
        String[] aname = getRing().getVars();
        //System.out.println("aname = " + Arrays.toString(aname));
        if (aname == null) {
            throw new IllegalArgumentException("aname may not be null");
        }

        GroebnerBasePartial<C> bbp = new GroebnerBasePartial<C>(bb, null);
        String[] rname = GroebnerBasePartial.remainingVars(aname, ename);
        //System.out.println("rname = " + Arrays.toString(rname));
        PolynomialList<C> Pl = null;
        if (rname.length == 0) {
            if (Arrays.equals(aname, ename)) {
                return this;
            }
            Pl = bbp.partialGB(getList(), ename); // normal GB
        } else {
            Pl = bbp.elimPartialGB(getList(), rname, ename); // reversed!
        }
        //System.out.println("Pl = " + Pl);
        if (debug) {
            logger.debug("elimination GB = " + Pl);
        }
        Ideal<C> I = new Ideal<C>(Pl, true);
        return I;
    }
     */


    /**
     * Quotient. Generators for the ideal quotient.
     * @param h polynomial
     * @return ideal(this : h), a Groebner base
     */
    public SolvableIdeal<C> quotient(GenSolvablePolynomial<C> h) {
        if (h == null) { // == (0)
            return this;
        }
        if (h.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        List<GenSolvablePolynomial<C>> H;
        H = new ArrayList<GenSolvablePolynomial<C>>(1);
        H.add(h);
        SolvableIdeal<C> Hi = new SolvableIdeal<C>(getRing(), H, true);

        SolvableIdeal<C> I = this.intersect(Hi);

        List<GenSolvablePolynomial<C>> Q;
        Q = new ArrayList<GenSolvablePolynomial<C>>(I.getList().size());
        for (GenSolvablePolynomial<C> q : I.getList()) {
            q = (GenSolvablePolynomial<C>) q.divide(h); // remainder == 0
            Q.add(q);
        }
        return new SolvableIdeal<C>(getRing(), Q, true /*false?*/);
    }


    /**
     * Quotient. Generators for the ideal quotient.
     * @param H ideal
     * @return ideal(this : H), a Groebner base
     */
    public SolvableIdeal<C> quotient(SolvableIdeal<C> H) {
        if (H == null) { // == (0)
            return this;
        }
        if (H.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        SolvableIdeal<C> Q = null;
        for (GenSolvablePolynomial<C> h : H.getList()) {
            SolvableIdeal<C> Hi = this.quotient(h);
            if (Q == null) {
                Q = Hi;
            } else {
                Q = Q.intersect(Hi);
            }
        }
        return Q;
    }


    /**
     * Infinite quotient. Generators for the infinite ideal quotient.
     * @param h polynomial
     * @return ideal(this : h<sup>s</sup>), a Groebner base
     */
    public SolvableIdeal<C> infiniteQuotientRab(GenSolvablePolynomial<C> h) {
        if (h == null || h.isZERO()) { // == (0)
            return getONE();
        }
        if (h.isONE()) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        SolvableIdeal<C> I = this.GB(); // should be already
        List<GenSolvablePolynomial<C>> a = I.getList();
        List<GenSolvablePolynomial<C>> c;
        c = new ArrayList<GenSolvablePolynomial<C>>(a.size() + 1);

        GenSolvablePolynomialRing<C> tfac = getRing().extend(1);
        // term order is also adjusted
        for (GenSolvablePolynomial<C> p : a) {
            p = (GenSolvablePolynomial<C>) p.extend(tfac, 0, 0L); // p
            c.add(p);
        }
        GenSolvablePolynomial<C> q = (GenSolvablePolynomial<C>) h.extend(tfac, 0, 1L);
        GenSolvablePolynomial<C> r = tfac.getONE(); // h.extend( tfac, 0, 0L );
        GenSolvablePolynomial<C> hs = (GenSolvablePolynomial<C>) q.subtract(r); // 1 - t*h // (1-t)*h
        c.add(hs);
        logger.warn("infiniteQuotientRab computing GB ");
        List<GenSolvablePolynomial<C>> g = bb.leftGB(c);
        if (debug) {
            logger.info("infiniteQuotientRab    = " + tfac + ", c = " + c);
            logger.info("infiniteQuotientRab GB = " + g);
        }
        SolvableIdeal<C> E = new SolvableIdeal<C>(tfac, g, true);
        SolvableIdeal<C> Is = E.intersect(getRing());
        return Is;
    }


    /**
     * Infinite quotient exponent.
     * @param h polynomial
     * @param Q quotient this : h^\infinity
     * @return s with Q = this : h<sup>s</sup>
     */
    public int infiniteQuotientExponent(GenSolvablePolynomial<C> h, SolvableIdeal<C> Q) {
        int s = 0;
        if (h == null) { // == 0
            return s;
        }
        if (h.isZERO() || h.isONE()) {
            return s;
        }
        if (this.isZERO() || this.isONE()) {
            return s;
        }
        //see below: if (this.contains(Q)) {
        //    return s;
        //}
        GenSolvablePolynomial<C> p = getRing().getONE();
        for (GenSolvablePolynomial<C> q : Q.getList()) {
            if (this.contains(q)) {
                continue;
            }
            //System.out.println("q = " + q + ", p = " + p + ", s = " + s);
            GenSolvablePolynomial<C> qp = q.multiply(p);
            while (!this.contains(qp)) {
                p = p.multiply(h);
                s++;
                qp = q.multiply(p);
            }
        }
        return s;
    }


    /**
     * Infinite quotient. Generators for the infinite ideal quotient.
     * @param h polynomial
     * @return ideal(this : h<sup>s</sup>), a Groebner base
     */
    public SolvableIdeal<C> infiniteQuotient(GenSolvablePolynomial<C> h) {
        if (h == null) { // == (0)
            return this;
        }
        if (h.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        int s = 0;
        SolvableIdeal<C> I = this.GB(); // should be already
        GenSolvablePolynomial<C> hs = h;
        SolvableIdeal<C> Is = I;

        boolean eq = false;
        while (!eq) {
            Is = I.quotient(hs);
            Is = Is.GB(); // should be already
            logger.info("infiniteQuotient s = " + s);
            eq = Is.contains(I); // I.contains(Is) always
            if (!eq) {
                I = Is;
                s++;
                // hs = hs.multiply( h );
            }
        }
        return Is;
    }


    /**
     * Radical membership test.
     * @param h polynomial
     * @return true if h is contained in the radical of ideal(this), else false.
     */
    public boolean isRadicalMember(GenSolvablePolynomial<C> h) {
        if (h == null) { // == (0)
            return true;
        }
        if (h.isZERO()) {
            return true;
        }
        if (this.isZERO()) {
            return true;
        }
        SolvableIdeal<C> x = infiniteQuotientRab(h);
        if (debug) {
            logger.debug("infiniteQuotientRab = " + x);
        }
        return x.isONE();
    }


    /**
     * Infinite Quotient. Generators for the ideal infinite quotient.
     * @param H ideal
     * @return ideal(this : H<sup>s</sup>), a Groebner base
     */
    public SolvableIdeal<C> infiniteQuotient(SolvableIdeal<C> H) {
        if (H == null) { // == (0)
            return this;
        }
        if (H.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        SolvableIdeal<C> Q = null;
        for (GenSolvablePolynomial<C> h : H.getList()) {
            SolvableIdeal<C> Hi = this.infiniteQuotient(h);
            if (Q == null) {
                Q = Hi;
            } else {
                Q = Q.intersect(Hi);
            }
        }
        return Q;
    }


    /**
     * Infinite Quotient. Generators for the ideal infinite quotient.
     * @param H ideal
     * @return ideal(this : H<sup>s</sup>), a Groebner base
     */
    public SolvableIdeal<C> infiniteQuotientRab(SolvableIdeal<C> H) {
        if (H == null) { // == (0)
            return this;
        }
        if (H.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        SolvableIdeal<C> Q = null;
        for (GenSolvablePolynomial<C> h : H.getList()) {
            SolvableIdeal<C> Hi = this.infiniteQuotientRab(h);
            if (Q == null) {
                Q = Hi;
            } else {
                Q = Q.intersect(Hi);
            }
        }
        return Q;
    }


    /**
     * Power. Generators for the power of this ideal. Note: if this ideal is a
     * Groebner base, a Groebner base is returned.
     * @param d integer
     * @return ideal(this^d)
     */
    public SolvableIdeal<C> power(int d) {
        if (d <= 0) {
            return getONE();
        }
        if (this.isZERO() || this.isONE()) {
            return this;
        }
        SolvableIdeal<C> c = this;
        for (int i = 1; i < d; i++) {
            c = c.product(this);
        }
        return c;
    }


    /**
     * Normalform for element.
     * @param h polynomial
     * @return normalform of h with respect to this
     */
    public GenSolvablePolynomial<C> normalform(GenSolvablePolynomial<C> h) {
        if (h == null) {
            return h;
        }
        if (h.isZERO()) {
            return h;
        }
        if (this.isZERO()) {
            return h;
        }
        GenSolvablePolynomial<C> r;
        r = red.leftNormalform(getList(), h);
        return r;
    }


    /**
     * Normalform for list of elements.
     * @param L polynomial list
     * @return list of normalforms of the elements of L with respect to this
     */
    public List<GenSolvablePolynomial<C>> normalform(List<GenSolvablePolynomial<C>> L) {
        if (L == null) {
            return L;
        }
        if (L.size() == 0) {
            return L;
        }
        if (this.isZERO()) {
            return L;
        }
        List<GenSolvablePolynomial<C>> M = new ArrayList<GenSolvablePolynomial<C>>(L.size());
        for (GenSolvablePolynomial<C> h : L) {
            GenSolvablePolynomial<C> r = normalform(h);
            if (r != null && !r.isZERO()) {
                M.add(r);
            }
        }
        return M;
    }


    /**
     * Inverse for element modulo this ideal.
     * @param h polynomial
     * @return inverse of h with respect to this, if defined
     */
    public GenSolvablePolynomial<C> inverse(GenSolvablePolynomial<C> h) {
        if (h == null || h.isZERO()) {
            throw new NotInvertibleException("zero not invertible");
        }
        if (this.isZERO()) {
            throw new NotInvertibleException("zero ideal");
        }
        if (h.isUnit()) {
            return (GenSolvablePolynomial<C>) h.inverse();
        }
        doGB();
        List<GenSolvablePolynomial<C>> F = new ArrayList<GenSolvablePolynomial<C>>(1 + list.list.size());
        F.add(h);
        F.addAll(getList());
        //System.out.println("F = " + F);
        SolvableExtendedGB<C> x = bb.extLeftGB(F);
        List<GenSolvablePolynomial<C>> G = x.G;
        //System.out.println("G = " + G);
        GenSolvablePolynomial<C> one = null;
        int i = -1;
        for (GenSolvablePolynomial<C> p : G) {
            i++;
            if (p == null) {
                continue;
            }
            if (p.isUnit()) {
                one = p;
                break;
            }
        }
        if (one == null) {
            throw new NotInvertibleException(" h = " + h);
        }
        List<GenSolvablePolynomial<C>> row = x.G2F.get(i); // != -1
        GenSolvablePolynomial<C> g = row.get(0);
        if (g == null || g.isZERO()) {
            throw new NotInvertibleException(" h = " + h);
        }
        // adjust g to get g*h == 1
        GenSolvablePolynomial<C> f = g.multiply(h);
        GenSolvablePolynomial<C> k = red.leftNormalform(getList(), f);
        if (!k.isONE()) {
            C lbc = k.leadingBaseCoefficient();
            lbc = lbc.inverse();
            g = g.multiply(lbc);
        }
        if (debug) {
            //logger.info("inv G = " + G);
            //logger.info("inv G2F = " + x.G2F);
            //logger.info("inv row "+i+" = " + row);
            //logger.info("inv h = " + h);
            //logger.info("inv g = " + g);
            //logger.info("inv f = " + f);
            f = g.multiply(h);
            k = red.leftNormalform(getList(), f);
            logger.debug("inv k = " + k);
            if (!k.isUnit()) {
                throw new NotInvertibleException(" k = " + k);
            }
        }
        return g;
    }


    /**
     * Test if element is a unit modulo this ideal.
     * @param h polynomial
     * @return true if h is a unit with respect to this, else false
     */
    public boolean isUnit(GenSolvablePolynomial<C> h) {
        if (h == null || h.isZERO()) {
            return false;
        }
        if (this.isZERO()) {
            return false;
        }
        List<GenSolvablePolynomial<C>> F = new ArrayList<GenSolvablePolynomial<C>>(1 + list.list.size());
        F.add(h);
        F.addAll(getList());
        List<GenSolvablePolynomial<C>> G = bb.leftGB(F);
        for (GenSolvablePolynomial<C> p : G) {
            if (p == null) {
                continue;
            }
            if (p.isUnit()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Ideal common zero test.
     * @return -1, 0 or 1 if dimension(this) &eq; -1, 0 or &ge; 1.
     */
    public int commonZeroTest() {
        if (this.isZERO()) {
            return 1;
        }
        if (!isGB) {
            doGB();
        }
        if (this.isONE()) {
            return -1;
        }
        GroebnerBaseAbstract<C> bbc = new GroebnerBaseSeq<C>();
        return bbc.commonZeroTest(list.list);
    }


    /**
     * Test if this ideal is maximal.
     * @return true, if this is maximal and not one, else false.
     */
    public boolean isMaximal() {
        if (commonZeroTest() != 0) {
            return false;
        }
        for (Long d : univariateDegrees()) {
            if (d > 1L) {
                // todo: test if irreducible
                return false;
            }
        }
        return true;
    }


    /**
     * Univariate head term degrees.
     * @return a list of the degrees of univariate head terms.
     */
    public List<Long> univariateDegrees() {
        List<Long> ud = new ArrayList<Long>();
        if (this.isZERO()) {
            return ud;
        }
        if (!isGB) {
            doGB();
        }
        if (this.isONE()) {
            return ud;
        }
        return bb.univariateDegrees(getList());
    }


    /*
     * Ideal dimension.
     * @return a dimension container (dim,maxIndep,list(maxIndep),vars).
    public Dimension dimension() {
        int t = commonZeroTest();
        Set<Integer> S = new HashSet<Integer>();
        Set<Set<Integer>> M = new HashSet<Set<Integer>>();
        if (t <= 0) {
            return new Dimension(t, S, M, this.list.ring.getVars());
        }
        int d = 0;
        Set<Integer> U = new HashSet<Integer>();
        for (int i = 0; i < this.list.ring.nvar; i++) {
            U.add(i);
        }
        M = dimension(S, U, M);
        for (Set<Integer> m : M) {
            int dp = m.size();
            if (dp > d) {
                d = dp;
                S = m;
            }
        }
        return new Dimension(d, S, M, this.list.ring.getVars());
    }
     */


    /**
     * Ideal head term containment test.
     * @param G list of polynomials.
     * @param H index set.
     * @return true, if the vaiables of the head terms of each polynomial in G
     *         are contained in H, else false.
     */
    protected boolean containsHT(Set<Integer> H, List<GenSolvablePolynomial<C>> G) {
        Set<Integer> S = null;
        for (GenSolvablePolynomial<C> p : G) {
            if (p == null) {
                continue;
            }
            ExpVector e = p.leadingExpVector();
            if (e == null) {
                continue;
            }
            int[] v = e.dependencyOnVariables();
            if (v == null) {
                continue;
            }
            //System.out.println("v = " + Arrays.toString(v));
            if (S == null) { // revert indices
                S = new HashSet<Integer>(H.size());
                int r = e.length() - 1;
                for (Integer i : H) {
                    S.add(r - i);
                }
            }
            if (contains(v, S)) { // v \subset S
                return true;
            }
        }
        return false;
    }


    /**
     * Set containment. is v \subset H.
     * @param v index array.
     * @param H index set.
     * @return true, if each element of v is contained in H, else false .
     */
    protected boolean contains(int[] v, Set<Integer> H) {
        for (int i = 0; i < v.length; i++) {
            if (!H.contains(v[i])) {
                return false;
            }
        }
        return true;
    }


    /**
     * Construct univariate polynomials of minimal degree in all variables in
     * zero dimensional ideal(G).
     * @return list of univariate polynomial of minimal degree in each variable
     *         in ideal(G)
     */
    public List<GenSolvablePolynomial<C>> constructUnivariate() {
        List<GenSolvablePolynomial<C>> univs = new ArrayList<GenSolvablePolynomial<C>>();
        for (int i = getRing().nvar - 1; i >= 0; i--) {
            GenSolvablePolynomial<C> u = constructUnivariate(i);
            univs.add(u);
        }
        return univs;
    }


    /**
     * Construct univariate polynomial of minimal degree in variable i in zero
     * dimensional ideal(G).
     * @param i variable index.
     * @return univariate polynomial of minimal degree in variable i in ideal(G)
     */
    public GenSolvablePolynomial<C> constructUnivariate(int i) {
        doGB();
        return bb.constructUnivariate(i, getList());
    }

}
