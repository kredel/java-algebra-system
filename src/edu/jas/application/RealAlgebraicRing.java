/*
 * $Id$
 */

package edu.jas.application;


import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.jas.arith.Rational;
import edu.jas.arith.BigRational;
import edu.jas.arith.BigDecimal;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;
import edu.jas.root.InvalidBoundaryException;
//import edu.jas.root.RealAlgebraicNumber;
import edu.jas.root.RealRootTuple;


/**
 * Complex algebraic number factory class based on bi-variate Ideal.
 * Objects of this class are immutable with the exception of the
 * isolating intervals.
 * @author Heinz Kredel
 */

public class RealAlgebraicRing<C extends GcdRingElem<C> & Rational>
implements RingFactory<RealAlgebraicNumber<C>> {


    /**
     * Representing univariate polynomials IdealWithUniv.
     */
    public final IdealWithUniv<C> univs;


    /**
     * Representing ResidueRing.
     */
    public final ResidueRing<C> algebraic;


    /**
     * Isolating intervals for the real algebraic roots of the real and imaginary part.
     * <b>Note: </b> intervals may shrink eventually.
     */
    /*package*/ RealRootTuple<C> root;


    /**
     * Recursive real root.
     */
    public final edu.jas.root.RealAlgebraicRing<edu.jas.root.RealAlgebraicNumber<C>> rere;


    /**
     * Epsilon of the isolating rectangle for a complex root.
     */
    protected C eps;


    /**
     * Precision of the isolating rectangle for a complex root.
     */
    public final int PRECISION = 9; //BigDecimal.DEFAULT_PRECISION;


    /**
     * The constructor creates a RealAlgebraicNumber factory object from a
     * IdealWithUniv, ResidueRing and a root tuple.
     * @param m module IdealWithUniv&lt;C&gt;.
     * @param a module ResidueRing&lt;C&gt;.
     * @param r isolating rectangle for a complex root.
     */
    public RealAlgebraicRing(IdealWithUniv<C> m, ResidueRing<C> a, RealRootTuple<C> r) {
        univs = m;
        algebraic = a;
        root = r;
        if (algebraic.characteristic().signum() > 0) {
            throw new IllegalArgumentException("characteristic not zero");
        }
        C e = m.ideal.list.ring.coFac.fromInteger(10L);
        e = e.inverse();
        e = Power.positivePower(e, PRECISION);
        eps = e;
        edu.jas.root.RealAlgebraicRing<C> rfac1 = root.tuple.get(0).factory(); 
        edu.jas.root.RealAlgebraicRing<C> rfac2 = root.tuple.get(1).factory(); 
        GenPolynomial<C> p0 = PolyUtilApp.<C> selectWithVariable(univs.ideal.list.list, 0);
        if (p0 == null) {
            throw new RuntimeException("no polynomial found in " + (0) + " of  " + univs.ideal);
        }
        //System.out.println("p0 = " + p0);
        rere = (edu.jas.root.RealAlgebraicRing<edu.jas.root.RealAlgebraicNumber<C>>) 
               ExtensionFieldBuilder.baseField(rfac1)
                   .realAlgebraicExtension(rfac2.algebraic.ring.getVars()[0],p0.toString(),rfac2.getRoot().toString())
                   .build(); 
        //System.out.println("rere = " + rere);
    }


    /**
     * The constructor creates a RealAlgebraicNumber factory object from a
     * GenPolynomial objects module.
     * @param m module GenPolynomial&lt;C&gt;.
     * @param root isolating rectangle for a complex root.
     */
    public RealAlgebraicRing(IdealWithUniv<C> m, RealRootTuple<C> root) {
        this(m,new ResidueRing<C>(m.ideal),root);
    }


    /**
     * The constructor creates a RealAlgebraicNumber factory object from a
     * GenPolynomial objects module.
     * @param m module GenPolynomial&lt;C&gt;.
     * @param root isolating rectangle for a complex root.
     * @param isField indicator if m is prime.
     */
    public RealAlgebraicRing(IdealWithUniv<C> m, RealRootTuple<C> root, boolean isField) {
        this(m,new ResidueRing<C>(m.ideal,isField),root);
    }


    /*
     * Get the module part.
     * @return modul. public GenPolynomial<C> getModul() { return
     *         algebraic.ideal; }
     */


    /**
     * Set a refined rectangle for the complex root. <b>Note: </b> rectangle may
     * shrink eventually.
     * @param v rectangle.
     */
    public synchronized void setRoot(RealRootTuple<C> v) {
        // assert v is contained in root
        this.root = v;
    }


    /**
     * Get rectangle for the complex root.
     * @return v rectangle.
     */
    public synchronized RealRootTuple<C> getRoot() {
        return this.root;
    }


    /**
     * Get epsilon. 
     * @return epsilon.
     */
    public synchronized C getEps() {
        return this.eps;
    }


    /**
     * Set a new epsilon. 
     * @param e epsilon.
     */
    public synchronized void setEps(C e) {
        this.eps = e;
    }


    /**
     * Set a new epsilon. 
     * @param e epsilon.
     */
    public synchronized void setEps(BigRational e) {
        this.eps = algebraic.ring.coFac.parse(e.toString());
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
     * Copy RealAlgebraicNumber element c.
     * @param c
     * @return a copy of c.
     */
    public RealAlgebraicNumber<C> copy(RealAlgebraicNumber<C> c) {
        return new RealAlgebraicNumber<C>(this, c.number);
    }


    /**
     * Get the zero element.
     * @return 0 as RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> getZERO() {
        return new RealAlgebraicNumber<C>(this, algebraic.getZERO());
    }


    /**
     * Get the one element.
     * @return 1 as RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> getONE() {
        return new RealAlgebraicNumber<C>(this, algebraic.getONE());
    }


    /**
     * Get the i element.
     * @return i as RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> getIMAG() {
        //ComplexRing<C> cr = (ComplexRing<C>) algebraic.ring.coFac;
        //Complex<C> I = cr.getIMAG(); 
        return new RealAlgebraicNumber<C>(this, algebraic.getZERO());
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<RealAlgebraicNumber<C>> generators() {
        List<Residue<C>> agens = algebraic.generators();
        List<RealAlgebraicNumber<C>> gens = new ArrayList<RealAlgebraicNumber<C>>(agens.size());
        for (Residue<C> a : agens) {
            gens.add(getZERO().sum(a));
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
     * Get a RealAlgebraicNumber element from a BigInteger value.
     * @param a BigInteger.
     * @return a RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> fromInteger(java.math.BigInteger a) {
        return new RealAlgebraicNumber<C>(this, algebraic.fromInteger(a));
    }


    /**
     * Get a RealAlgebraicNumber element from a long value.
     * @param a long.
     * @return a RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> fromInteger(long a) {
        return new RealAlgebraicNumber<C>(this, algebraic.fromInteger(a));
    }


    /**
     * Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RealAlgebraicRing[ " + algebraic.ideal.toString() + " in " + root + " | isField="
                + algebraic.isField() + " :: " + algebraic.ring.toString() + " ]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        return "ComplexN( " + algebraic.ideal.toScript() + ", " + root.toScript()
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
        if (!(b instanceof RealAlgebraicRing)) {
            return false;
        }
        RealAlgebraicRing<C> a = null;
        try {
            a = (RealAlgebraicRing<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        return algebraic.equals(a.algebraic) && root.equals(a.root);
    }


    /**
     * Hash code for this RealAlgebraicNumber.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * root.hashCode() + algebraic.hashCode();
    }


    /**
     * RealAlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random integer mod modul.
     */
    public RealAlgebraicNumber<C> random(int n) {
        return new RealAlgebraicNumber<C>(this, algebraic.random(n));
    }


    /**
     * RealAlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random integer mod modul.
     */
    public RealAlgebraicNumber<C> random(int n, Random rnd) {
        return new RealAlgebraicNumber<C>(this, algebraic.random(n, rnd));
    }


    /**
     * Parse RealAlgebraicNumber from String.
     * @param s String.
     * @return RealAlgebraicNumber from s.
     */
    public RealAlgebraicNumber<C> parse(String s) {
        return new RealAlgebraicNumber<C>(this, algebraic.parse(s));
    }


    /**
     * Parse RealAlgebraicNumber from Reader.
     * @param r Reader.
     * @return next RealAlgebraicNumber from r.
     */
    public RealAlgebraicNumber<C> parse(Reader r) {
        return new RealAlgebraicNumber<C>(this, algebraic.parse(r));
    }

}
