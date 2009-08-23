/*
 * $Id$
 */

package edu.jas.application;


import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ExpVector;


/**
 * Condition. An ideal of polynomials considered to be zero and a list of
 * polynomials considered to be non-zero.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */
public class Condition<C extends GcdRingElem<C>> implements Serializable {


    private static final Logger logger = Logger.getLogger(Condition.class);


    private final boolean debug = true || logger.isDebugEnabled();


    /**
     * Colors.
     */
    public static enum Color {
        GREEN, RED, WHITE
    };


    /**
     * Data structure for condition zero.
     */
    public final Ideal<C> zero;


    /**
     * Data structure for condition non-zero.
     */
    public final MultiplicativeSet<C> nonZero;


    /**
     * Condition constructor. Constructs an empty condition.
     * @param ring polynomial ring factory for coefficients.
     */
    public Condition(GenPolynomialRing<C> ring) {
        this(new Ideal<C>(ring),new MultiplicativeSetSquarefree<C>(ring));
        if (ring == null) {
            throw new RuntimeException("only for non null rings");
        }
    }


    /**
     * Condition constructor.
     * @param z an ideal of zero polynomials.
     */
    public Condition(Ideal<C> z) {
        this(z,new MultiplicativeSetSquarefree<C>(z.list.ring));
    }


    /**
     * Condition constructor.
     * @param nz a list of non-zero polynomials.
     */
    public Condition(MultiplicativeSet<C> nz) {
        this(new Ideal<C>(nz.ring),nz);
    }


    /**
     * Condition constructor.
     * @param z an ideal of zero polynomials.
     * @param nz a list of non-zero polynomials.
     */
    public Condition(Ideal<C> z, MultiplicativeSet<C> nz) {
        if (z == null || nz == null) {
            throw new RuntimeException("only for non null condition parts");
        }
        zero = z;
        nonZero = nz;
    }


    /**
     * toString.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Condition[ 0 == " + zero.list.list.toString() + ", 0 != " + nonZero.mset.toString()
                + " ]";
    }


    /**
     * equals.
     * @param ob an Object.
     * @return true if this is equal to o, else false.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object ob) {
        Condition<C> c = null;
        try {
            c = (Condition<C>) ob;
        } catch (ClassCastException e) {
            return false;
        }
        if (c == null) {
            return false;
        }
        if (!zero.equals(c.zero)) {
            return false;
        }
        if (!nonZero.equals(c.nonZero)) {
            return false;
        }
        // better:
        //if ( nonZero.removeFactors(c.nonZero).size() != 0 ) {
        //    return false;
        //}
        //if ( c.nonZero.removeFactors(nonZero).size() != 0 ) {
        //    return false;
        //}
        return true;
    }


    /**
     * Hash code for this condition.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = zero.getList().hashCode();
        h = h << 17;
        h += nonZero.hashCode();
        // h = h << 11;
        // h += pairlist.hashCode();
        return h;
    }


    /**
     * Is empty condition.
     * @return true if this is the empty condition, else false.
     */
    public boolean isEmpty() {
        return (zero.isZERO() && nonZero.isEmpty());
    }


    /**
     * Is contradictory.
     * @return true if this condition is contradictory, else false.
     */
    public boolean isContradictory() {
        return (zero.isONE() || nonZero.contains(zero.list.list) || zero.contains(nonZero.mset) );
    }


    /**
     * Extend condition with zero polynomial.
     * @param z a polynomial to be treated as zero.
     * @return new condition.
     */
    public Condition<C> extendZero(GenPolynomial<C> z) {
        // assert color(z) == white 
        z = zero.engine.squarefreePart(z); 
        Ideal<C> idz = zero.sum(z);
        logger.info("added to ideal: " + z);

        // reduce non-zero wrt new ideal:
        List<GenPolynomial<C>> list = idz.normalform(nonZero.mset);
        if (list.size() != nonZero.mset.size()) { // contradiction
            if (debug) {
                logger.info("contradiction(==0):");
                logger.info("zero    = " + zero.getList());
                logger.info("z       = " + z);
                logger.info("idz     = " + idz.getList());
                logger.info("list    = " + list);
                logger.info("nonZero = " + nonZero);
            }
            return null;
        }
        MultiplicativeSet<C> ms = nonZero.replace(list);
        Condition<C> nc = new Condition<C>(idz, ms);

        // remove factors from new ideal wrt new multiplicative set:
        List<GenPolynomial<C>> Z = ms.removeFactors(idz.getList());
        if (Z.size() != idz.getList().size()) {
            System.out.println("contradiction(==0):");
            System.out.println("zero = " + zero.getList());
            System.out.println("ZZZZ = " + Z);
            System.out.println("list = " + list);
            //Ideal<C> id = new Ideal<C>(zero.getRing(), Z);
            // return new Condition<C>( id, L );
            return null; // contradiction
            //no-no: idz = new Ideal<C>(zero.getRing(), Z);
        }
        idz = new Ideal<C>(zero.getRing(),Z);
        nc = new Condition<C>(idz, ms);
        return nc;
    }


    /**
     * Extend condition with non-zero polynomial.
     * @param nz a polynomial to be treated as non-zero.
     * @return new condition.
     */
    public Condition<C> extendNonZero(GenPolynomial<C> nz) {
        // assert color(nz) == white 
        GenPolynomial<C> n = zero.normalform(nz).monic();
        if (n == null || n.isZERO()) {
            return this;
        }
	if ( !zero.isZERO() && zero.isRadicalMember(n) ) { // ideal is approx. radical
            return this;
	}
        MultiplicativeSet<C> ms = nonZero.add(n);
        Condition<C> nc = new Condition<C>(zero, ms);

        // remove factors from new ideal wrt new multiplicative set:
        List<GenPolynomial<C>> Z = ms.removeFactors(nc.zero.getList());
        if (Z.size() != nc.zero.getList().size()) {
            System.out.println("contradiction(!=0):");
            System.out.println("zero    = " + zero.getList());
            System.out.println("ZZZZ    = " + Z);
            System.out.println("list,nz = " + nonZero);
            Ideal<C> id = new Ideal<C>(zero.getRing(), Z);
            nc = new Condition<C>( id, nc.nonZero );
            //return nc; // null not ok
        }
        return nc;
    }


    /**
     * Determine color of polynomial.
     * @param c polynomial to be colored.
     * @return color of c.
     */
    public Color color(GenPolynomial<C> c) {
        GenPolynomial<C> m = zero.normalform(c).monic();
        //non-sense: m = nonZero.removeFactors(m);
        if (m.isZERO()) { // zero.contains(m)
            // System.out.println("m in id = " + m);
            return Color.GREEN;
        }
        if (m.isConstant()) {
            // System.out.println("m constant " + m);
            return Color.RED;
        }
        if (nonZero.contains(m)||nonZero.contains(c)) {
            // System.out.println("m or c in nonzero " + m);
            return Color.RED;
        }
        // System.out.println("m white " + m);
        return Color.WHITE;
    }


    /**
     * Determine polynomial. If this condition does not determine the
     * polynomial, then a run-time exception is thrown.
     * @param A polynomial.
     * @return new determined colored polynomial.
     */
    public ColorPolynomial<C> determine(GenPolynomial<GenPolynomial<C>> A) {
        ColorPolynomial<C> cp = null;
        if (A == null) {
            return cp;
        }
        GenPolynomial<GenPolynomial<C>> zero = A.ring.getZERO();
        GenPolynomial<GenPolynomial<C>> green = zero;
        GenPolynomial<GenPolynomial<C>> red = zero;
        GenPolynomial<GenPolynomial<C>> white = zero;
        if (A.isZERO()) {
            cp = new ColorPolynomial<C>(green, red, white);
            return cp;
        }
        GenPolynomial<GenPolynomial<C>> Ap = A;
        GenPolynomial<GenPolynomial<C>> Bp;
        while (!Ap.isZERO()) {
            Map.Entry<ExpVector, GenPolynomial<C>> m = Ap.leadingMonomial();
            ExpVector e = m.getKey();
            GenPolynomial<C> c = m.getValue();
            Bp = Ap.reductum();
            // System.out.println( "color(" + c + ") = " + color(c) );
            switch (color(c)) {
            case GREEN:
                green = green.sum(c, e);
                Ap = Bp;
                continue;
            case RED:
                red = red.sum(c, e);
                white = Bp;
                return new ColorPolynomial<C>(green, red, white);
                // since break is not possible
            default:
                System.out.println("error cond       = " + this);
                System.out.println("error poly     A = " + A);
                System.out.println("error poly green = " + green);
                System.out.println("error poly    Ap = " + Ap);
                throw new RuntimeException("error, c is white = " + c);
                // is catched in minimalGB
            }
        }
        cp = new ColorPolynomial<C>(green, red, white);
        // System.out.println("determined = " + cp);
        return cp;
    }


    /**
     * Re determine colored polynomial.
     * @param s colored polynomial.
     * @return determined colored polynomial wrt. this.conditions.
     */
    public ColorPolynomial<C> reDetermine(ColorPolynomial<C> s) {
        ColorPolynomial<C> p = determine(s.getEssentialPolynomial());
        // assume green terms stay green wrt. this condition
        GenPolynomial<GenPolynomial<C>> g = s.green.sum( p.green );
        p = new ColorPolynomial<C>(g, p.red, p.white);
        return p;
    }


    /**
     * Determine list of polynomials. If this condition does not determine all
     * polynomials, then a run-time exception is thrown. The returned list does
     * not contain polynomials with all green terms.
     * @param L list of polynomial.
     * @return new determined list of colored polynomials.
     */
    public List<ColorPolynomial<C>> determine(List<GenPolynomial<GenPolynomial<C>>> L) {
        List<ColorPolynomial<C>> cl = null;
        if (L == null) {
            return cl;
        }
        cl = new ArrayList<ColorPolynomial<C>>(L.size());
        for (GenPolynomial<GenPolynomial<C>> A : L) {
            ColorPolynomial<C> c = determine(A);
            if (c != null && !c.isZERO()) {
                cl.add(c);
            }
        }
        return cl;
    }


}
