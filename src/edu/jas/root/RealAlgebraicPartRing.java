/*
 * $Id$
 */

package edu.jas.root;


import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.jas.arith.Rational;
import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;


/**
 * Real algebraic number part factory class based on
 * ComplexAlgebraicNumberRing with RingElem interface. Objects of this
 * class are immutable with the exception of the isolating intervals /
 * rectangle.
 * @author Heinz Kredel
 */

public class RealAlgebraicPartRing<C extends GcdRingElem<C> & Rational>
       /*extends AlgebraicNumberRing<C>*/
    implements RingFactory<RealAlgebraicNumberPart<C>> {


    /**
     * Representing ComplexAlgebraicRing.
     */
    public final ComplexAlgebraicRing<C> algebraic;


    /**
     * The constructor creates a RealAlgebraicNumberPart factory object from a
     * GenPolynomial objects module.
     * @param m module GenPolynomial<C>.
     * @param root isolating rectangle for a real root.
     */
    public RealAlgebraicPartRing(GenPolynomial<Complex<C>> m, Rectangle<C> root) {
        algebraic = new ComplexAlgebraicRing<C>(m,root);
        //this.root = root;
        //engine = new RealRootsSturm<C>();
        if (m.ring.characteristic().signum() > 0) {
            throw new RuntimeException("characteristic not zero");
        }
        //C e = m.ring.coFac.fromInteger(10L).getRe();
        //e = e.inverse();
        //C x = Power.positivePower(e,BigDecimal.DEFAULT_PRECISION);
        //e = Power.positivePower(e, 9); // better not too much for speed
        //eps = e;
    }


    /**
     * The constructor creates a RealAlgebraicNumberPart factory object from a
     * GenPolynomial objects module.
     * @param m module GenPolynomial<C>.
     * @param root isolating rectangle for a real root.
     * @param isField indicator if m is prime.
     */
    public RealAlgebraicPartRing(GenPolynomial<Complex<C>> m, Rectangle<C> root, boolean isField) {
        algebraic = new ComplexAlgebraicRing<C>(m, root, isField);
        //this.root = root;
        //engine = new RealRootsSturm<C>();
        if (m.ring.characteristic().signum() > 0) {
            throw new RuntimeException("characteristic not zero");
        }
        //C e = m.ring.coFac.fromInteger(10L).getRe();
        //e = e.inverse();
        //e = Power.positivePower(e, 9); //BigDecimal.DEFAULT_PRECISION);
        //eps = e;
    }


    /**
     * Get the module part.
     * @return modul. public GenPolynomial<C> getModul() { return
     *         algebraic.modul; }
     */


    /**
     * Get the rectangle for the real root. <b>Note: </b> rectangle may
     * shrink later.
     * @return real root isolating rectangle
     */
    public synchronized Rectangle<C> getRoot() {
	return algebraic.getRoot();
    }


    /**
     * Set a refined rectangle for the real root. <b>Note: </b> rectangle may
     * shrink eventually.
     * @param v rectangle.
     */
    public synchronized void setRoot(Rectangle<C> v) {
        algebraic.setRoot(v);
    }


    /**
     * Get the epsilon. 
     * @return eps.
     */
    public synchronized C getEps() {
        return algebraic.getEps();
    }


    /**
     * Set a new epsilon. 
     * @param e epsilon.
     */
    public synchronized void setEps(C e) {
        algebraic.setEps(e);
    }


    /**
     * Set a new epsilon. 
     * @param e epsilon.
     */
    public synchronized void setEps(BigRational e) {
        algebraic.setEps(e);
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return algebraic.isFinite();
    }


    /**
     * Copy RealAlgebraicNumberPart element c.
     * @param c
     * @return a copy of c.
     */
    public RealAlgebraicNumberPart<C> copy(RealAlgebraicNumberPart<C> c) {
        return new RealAlgebraicNumberPart<C>(this, c.number);
    }


    /**
     * Get the zero element.
     * @return 0 as RealAlgebraicNumberPart.
     */
    public RealAlgebraicNumberPart<C> getZERO() {
        return new RealAlgebraicNumberPart<C>(this, algebraic.getZERO());
    }


    /**
     * Get the one element.
     * @return 1 as RealAlgebraicNumberPart.
     */
    public RealAlgebraicNumberPart<C> getONE() {
        return new RealAlgebraicNumberPart<C>(this, algebraic.getONE());
    }


    /**
     * Get the generating element.
     * @return alpha as RealAlgebraicNumberPart.
     */
    public RealAlgebraicNumberPart<C> getGenerator() {
        return new RealAlgebraicNumberPart<C>(this, algebraic.getGenerator());
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<RealAlgebraicNumberPart<C>> generators() {
        List<ComplexAlgebraicNumber<C>> agens = algebraic.generators();
        List<RealAlgebraicNumberPart<C>> gens = new ArrayList<RealAlgebraicNumberPart<C>>(agens.size());
        for (ComplexAlgebraicNumber<C> a : agens) {
            gens.add(getZERO().sum(a.number));
        }
        return gens;
    }


    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return algebraic.isCommutative();
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        return algebraic.isAssociative();
    }


    /**
     * Query if this ring is a field.
     * @return true if algebraic is prime, else false.
     */
    public boolean isField() {
        return algebraic.isField();
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return algebraic.characteristic();
    }


    /**
     * Get a RealAlgebraicNumberPart element from a BigInteger value.
     * @param a BigInteger.
     * @return a RealAlgebraicNumberPart.
     */
    public RealAlgebraicNumberPart<C> fromInteger(java.math.BigInteger a) {
        return new RealAlgebraicNumberPart<C>(this, algebraic.fromInteger(a));
    }


    /**
     * Get a RealAlgebraicNumberPart element from a long value.
     * @param a long.
     * @return a RealAlgebraicNumberPart.
     */
    public RealAlgebraicNumberPart<C> fromInteger(long a) {
        return new RealAlgebraicNumberPart<C>(this, algebraic.fromInteger(a));
    }


    /**
     * Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RealAlgebraicPartRing[ " + algebraic.toString() + " in " + algebraic.root + " | isField="
                + algebraic.isField() + " :: " + algebraic.toString() + " ]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        return "RealN( " + algebraic.toScript() + ", " + algebraic.root.toScript()
        //+ ", " + algebraic.isField() 
                //+ ", " + algebraic.ring.toScript() 
                + " )";
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    // not jet working
    public boolean equals(Object b) {
        if (!(b instanceof RealAlgebraicPartRing)) {
            return false;
        }
        RealAlgebraicPartRing<C> a = null;
        try {
            a = (RealAlgebraicPartRing<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        return algebraic.equals(a.algebraic);
    }


    /**
     * Hash code for this RealAlgebraicNumberPart.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return algebraic.hashCode();
    }


    /**
     * RealAlgebraicNumberPart random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random integer mod modul.
     */
    public RealAlgebraicNumberPart<C> random(int n) {
        return new RealAlgebraicNumberPart<C>(this, algebraic.random(n));
    }


    /**
     * RealAlgebraicNumberPart random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random integer mod modul.
     */
    public RealAlgebraicNumberPart<C> random(int n, Random rnd) {
        return new RealAlgebraicNumberPart<C>(this, algebraic.random(n, rnd));
    }


    /**
     * Parse RealAlgebraicNumberPart from String.
     * @param s String.
     * @return RealAlgebraicNumberPart from s.
     */
    public RealAlgebraicNumberPart<C> parse(String s) {
        return new RealAlgebraicNumberPart<C>(this, algebraic.parse(s));
    }


    /**
     * Parse RealAlgebraicNumberPart from Reader.
     * @param r Reader.
     * @return next RealAlgebraicNumberPart from r.
     */
    public RealAlgebraicNumberPart<C> parse(Reader r) {
        return new RealAlgebraicNumberPart<C>(this, algebraic.parse(r));
    }

}
