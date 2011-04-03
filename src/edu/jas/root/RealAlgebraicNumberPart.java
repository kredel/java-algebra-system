/*
 * $Id$
 */

package edu.jas.root;


//import edu.jas.structure.RingElem;
import edu.jas.arith.BigRational;
import edu.jas.arith.BigDecimal;
import edu.jas.arith.Rational;
import edu.jas.kern.PrettyPrint;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.NotInvertibleException;


/**
 * Real algebraic number class based on ComplexAlgebraicNumber as real
 * or imaginary part. Objects of this class are immutable.
 * @author Heinz Kredel
 */

public class RealAlgebraicNumberPart<C extends GcdRingElem<C> & Rational>
    implements GcdRingElem<RealAlgebraicNumberPart<C>>, Rational {


    /**
     * Representing ComplexAlgebraicNumber.
     */
    public final ComplexAlgebraicNumber<C> number;


    /**
     * Ring part of the data structure.
     */
    public final RealAlgebraicPartRing<C> ring;


    /**
     * The constructor creates a RealAlgebraicNumberPart object from
     * RealAlgebraicPartRing modul and a GenPolynomial value.
     * @param r ring RealAlgebraicPartRing<C>.
     * @param a value GenPolynomial<C>.
     */
    public RealAlgebraicNumberPart(RealAlgebraicPartRing<C> r, GenPolynomial<Complex<C>> a) {
        this(r, new ComplexAlgebraicNumber<C>(r.algebraic, a));
    }


    /**
     * The constructor creates a RealAlgebraicNumberPart object from
     * RealAlgebraicPartRing modul and a AlgebraicNumber value.
     * @param r ring RealAlgebraicPartRing<C>.
     * @param a value AlgebraicNumber<C>.
     */
    public RealAlgebraicNumberPart(RealAlgebraicPartRing<C> r, ComplexAlgebraicNumber<C> a) {
        number = a;
        ring = r;
    }


    /**
     * The constructor creates a RealAlgebraicNumberPart object from a GenPolynomial
     * object module.
     * @param r ring RealAlgebraicPartRing<C>.
     */
    public RealAlgebraicNumberPart(RealAlgebraicPartRing<C> r) {
        this(r, r.algebraic.getZERO());
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public RealAlgebraicPartRing<C> factory() {
        return ring;
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public RealAlgebraicNumberPart<C> clone() {
        return new RealAlgebraicNumberPart<C>(ring, number);
    }


    /**
     * Return a BigRational approximation of this Element.
     * @return a BigRational approximation of this.
     * @see edu.jas.arith.Rational#getRational()
     */
    public BigRational getRational() {
        return magnitude();
    }


    /**
     * Is RealAlgebraicNumberPart zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return magnitude().isZERO(); // ??
    }


    /**
     * Is RealAlgebraicNumberPart one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return magnitude().isONE(); // ??
    }


    /**
     * Is RealAlgebraicNumberPart unit.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        return magnitude().isUnit(); // ??
    }


    /**
     * Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (PrettyPrint.isTrue()) {
            switch( ring.axis ) {
            case REAL: return "re{ " + number.toString() + " }";
            case IMAG: return "im{ " + number.toString() + " }";
            default:   throw new IllegalArgumentException("cannot happen");
            }
        } else {
            switch( ring.axis ) {
            case REAL: return "Real( " + number.toString() + " )";
            case IMAG: return "Imag( " + number.toString() + " )";
            default:   throw new IllegalArgumentException("cannot happen");
            }
        }
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        switch( ring.axis ) {
        case REAL: return "re(" + number.toScript() + ")";
        case IMAG: return "im(" + number.toScript() + ")";
        default:   throw new IllegalArgumentException("cannot happen");
        }
    }


    /**
     * Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    //JAVA6only: @Override
    public String toScriptFactory() {
        // Python case
        return factory().toScript();
    }


    /**
     * RealAlgebraicNumberPart comparison.
     * @param b RealAlgebraicNumberPart.
     * @return sign(this-b).
     */
    //JAVA6only: @Override
    public int compareTo(RealAlgebraicNumberPart<C> b) {
        int s = 0;
        if (number.ring != b.number.ring) { // avoid compareTo if possible
            s = ( number.ring.equals(b.number.ring) ? 0 : 1 );
            System.out.println("s_mod = " + s);
        }
        if (s != 0) {
            return s;
        }
        if ( ring.axis != b.ring.axis ) {
            throw new IllegalArgumentException("axis not equal");
        }
        s = magnitude().compareTo(b.magnitude()); // TODO
        //System.out.println("s_real = " + s);
        return s;
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof RealAlgebraicNumberPart)) {
            return false;
        }
        RealAlgebraicNumberPart<C> a = null;
        try {
            a = (RealAlgebraicNumberPart<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        if (!ring.equals(a.ring)) {
            return false;
        }
        return compareTo(a) == 0;
    }


    /**
     * Hash code for this RealAlgebraicNumberPart.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * number.hashCode() + ring.hashCode();
    }


    /**
     * RealAlgebraicNumberPart absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public RealAlgebraicNumberPart<C> abs() {
        if (this.signum() < 0) {
            return new RealAlgebraicNumberPart<C>(ring, number.negate());
        } else {
            return this;
        }
    }


    /**
     * RealAlgebraicNumberPart summation.
     * @param S RealAlgebraicNumberPart.
     * @return this+S.
     */
    public RealAlgebraicNumberPart<C> sum(RealAlgebraicNumberPart<C> S) {
        if ( ring.axis != S.ring.axis ) {
            throw new IllegalArgumentException("axis not equal");
        }
        return new RealAlgebraicNumberPart<C>(ring, number.sum(S.number));
    }


    /**
     * RealAlgebraicNumberPart summation.
     * @param c complex polynomial.
     * @return this+c.
     */
    public RealAlgebraicNumberPart<C> sum(GenPolynomial<Complex<C>> c) {
        return new RealAlgebraicNumberPart<C>(ring, number.sum(c));
    }


    /**
     * RealAlgebraicNumberPart summation.
     * @param c algebraic number.
     * @return this+c.
     */
    public RealAlgebraicNumberPart<C> sum(AlgebraicNumber<Complex<C>> c) {
        return new RealAlgebraicNumberPart<C>(ring, number.sum(c));
    }


    /**
     * RealAlgebraicNumberPart summation.
     * @param c coefficient.
     * @return this+c.
     */
    public RealAlgebraicNumberPart<C> sum(Complex<C> c) {
        return new RealAlgebraicNumberPart<C>(ring, number.sum(c));
    }


    /**
     * RealAlgebraicNumberPart negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public RealAlgebraicNumberPart<C> negate() {
        return new RealAlgebraicNumberPart<C>(ring, number.negate());
    }


    /**
     * RealAlgebraicNumberPart signum. 
     * <b>Note: </b> Modifies ring.algebraic.root eventually.
     * @see edu.jas.structure.RingElem#signum()
     * @return real signum(this).
     */
    public int signum() {
        int s = number.signum(); // changes root
        Rectangle<C> v = ring.algebraic.root;
        Complex<C> c = v.getCenter();
        switch( ring.axis ) {
        case REAL: return c.getRe().signum();
        case IMAG: return c.getIm().signum();
        default:   throw new IllegalArgumentException("cannot happen");
        }
    }


    /**
     * RealAlgebraicNumberPart magnitude.
     * @return |this|.
     */
    public BigRational magnitude() {
        Complex<BigRational> m = number.magnitude();
        switch( ring.axis ) {
        case REAL: return m.getRe();
        case IMAG: return m.getIm();
        default:   throw new IllegalArgumentException("cannot happen");
        }
    }


    /**
     * RealAlgebraicNumberPart magnitude.
     * @return |this| as big decimal.
     */
    public BigDecimal decimalMagnitude() {
        return new BigDecimal(magnitude());
    }


    /**
     * RealAlgebraicNumberPart subtraction.
     * @param S RealAlgebraicNumberPart.
     * @return this-S.
     */
    public RealAlgebraicNumberPart<C> subtract(RealAlgebraicNumberPart<C> S) {
        if ( ring.axis != S.ring.axis ) {
            throw new IllegalArgumentException("axis not equal");
        }
        return new RealAlgebraicNumberPart<C>(ring, number.subtract(S.number));
    }


    /**
     * RealAlgebraicNumberPart division.
     * @param S RealAlgebraicNumberPart.
     * @return this/S.
     */
    public RealAlgebraicNumberPart<C> divide(RealAlgebraicNumberPart<C> S) {
        if ( ring.axis != S.ring.axis ) {
            throw new IllegalArgumentException("axis not equal");
        }
        return multiply(S.inverse());
    }


    /**
     * RealAlgebraicNumberPart inverse.
     * @see edu.jas.structure.RingElem#inverse()
     * @throws NotInvertibleException if the element is not invertible.
     * @return S with S = 1/this if defined.
     */
    public RealAlgebraicNumberPart<C> inverse() {
        return new RealAlgebraicNumberPart<C>(ring, number.inverse());
    }


    /**
     * RealAlgebraicNumberPart remainder.
     * @param S RealAlgebraicNumberPart.
     * @return this - (this/S)*S.
     */
    public RealAlgebraicNumberPart<C> remainder(RealAlgebraicNumberPart<C> S) {
        if ( ring.axis != S.ring.axis ) {
            throw new IllegalArgumentException("axis not equal");
        }
        return new RealAlgebraicNumberPart<C>(ring, number.remainder(S.number));
    }


    /**
     * RealAlgebraicNumberPart multiplication.
     * @param S RealAlgebraicNumberPart.
     * @return this*S.
     */
    public RealAlgebraicNumberPart<C> multiply(RealAlgebraicNumberPart<C> S) {
        if ( ring.axis != S.ring.axis ) {
            throw new IllegalArgumentException("axis not equal");
        }
        return new RealAlgebraicNumberPart<C>(ring, number.multiply(S.number));
    }


    /**
     * RealAlgebraicNumberPart multiplication.
     * @param c coefficient.
     * @return this*c.
     */
    public RealAlgebraicNumberPart<C> multiply(Complex<C> c) {
        return new RealAlgebraicNumberPart<C>(ring, number.multiply(c));
    }


    /**
     * RealAlgebraicNumberPart multiplication.
     * @param c polynomial.
     * @return this*c.
     */
    public RealAlgebraicNumberPart<C> multiply(GenPolynomial<Complex<C>> c) {
        return new RealAlgebraicNumberPart<C>(ring, number.multiply(c));
    }


    /**
     * RealAlgebraicNumberPart monic.
     * @return this with monic value part.
     */
    public RealAlgebraicNumberPart<C> monic() {
        return new RealAlgebraicNumberPart<C>(ring, number.monic());
    }


    /**
     * RealAlgebraicNumberPart greatest common divisor.
     * @param S RealAlgebraicNumberPart.
     * @return gcd(this,S).
     */
    public RealAlgebraicNumberPart<C> gcd(RealAlgebraicNumberPart<C> S) {
        if ( ring.axis != S.ring.axis ) {
            throw new IllegalArgumentException("axis not equal");
        }
        return new RealAlgebraicNumberPart<C>(ring, number.gcd(S.number));
    }


    /**
     * RealAlgebraicNumberPart extended greatest common divisor.
     * @param S RealAlgebraicNumberPart.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    @SuppressWarnings("unchecked")
    public RealAlgebraicNumberPart<C>[] egcd(RealAlgebraicNumberPart<C> S) {
        if ( ring.axis != S.ring.axis ) {
            throw new IllegalArgumentException("axis not equal");
        }
        ComplexAlgebraicNumber<C>[] aret = number.egcd(S.number);
        RealAlgebraicNumberPart<C>[] ret = new RealAlgebraicNumberPart[3];
        ret[0] = new RealAlgebraicNumberPart<C>(ring, aret[0]);
        ret[1] = new RealAlgebraicNumberPart<C>(ring, aret[1]);
        ret[2] = new RealAlgebraicNumberPart<C>(ring, aret[2]);
        return ret;
    }

}
