/*
 * $Id$
 */

package edu.jas.root;


//import edu.jas.structure.RingElem;
import edu.jas.kern.PrettyPrint;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.NotInvertibleException;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;


/**
 * Real algebraic number class based on GenPolynomial with RingElem interface.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */

public class RealAlgebraicNumber<C extends GcdRingElem<C>> 
             extends AlgebraicNumber<C> {


    /**
     * Ring part of the data structure.
     * Shadows super.ring.
     */
    public final RealAlgebraicRing<C> ring;


    /**
     * The constructor creates a RealAlgebraicNumber object from RealAlgebraicRing
     * modul and a GenPolynomial value.
     * @param r ring RealAlgebraicRing<C>.
     * @param a value GenPolynomial<C>.
     */
    public RealAlgebraicNumber(RealAlgebraicRing<C> r, GenPolynomial<C> a) {
        super(r,a);
        ring = r;
    }


    /**
     * The constructor creates a RealAlgebraicNumber object from RealAlgebraicRing
     * modul and a AlgebraicNumber value.
     * @param r ring RealAlgebraicRing<C>.
     * @param a value AlgebraicNumber<C>.
     */
    public RealAlgebraicNumber(RealAlgebraicRing<C> r, AlgebraicNumber<C> a) {
        super(r,a.val);
        ring = r;
    }


    /**
     * The constructor creates a RealAlgebraicNumber object from a GenPolynomial
     * object module.
     * @param r ring RealAlgebraicRing<C>.
     */
    public RealAlgebraicNumber(RealAlgebraicRing<C> r) {
        this(r, r.ring.getZERO());
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public RealAlgebraicNumber<C> clone() {
        return new RealAlgebraicNumber<C>(ring, val);
    }


    /**
     * Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (PrettyPrint.isTrue()) {
            return super.toString();
        } else {
            return "Real" + super.toString();
        }
    }


    /**
     * RealAlgebraicNumber comparison.
     * @param b RealAlgebraicNumber.
     * @return sign(this-b).
     */
    public int compareTo(RealAlgebraicNumber<C> b) {
        int s = 0;
        if ( ring.modul != b.ring.modul ) { // avoid compareTo if possible
           s = ring.modul.compareTo( b.ring.modul );
        }
        if ( s != 0 ) {
            return s;
        }
        //return val.compareTo(b.val);
        s = this.subtract(b).signum();
        return s;
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof RealAlgebraicNumber)) {
            return false;
        }
        RealAlgebraicNumber<C> a = null;
        try {
            a = (RealAlgebraicNumber<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        if ( !ring.equals( a.ring ) ) {
            return false;
        }
        return val.equals(a.val);
    }


    /**
     * Hash code for this RealAlgebraicNumber.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }


    /**
     * RealAlgebraicNumber absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public RealAlgebraicNumber<C> abs() {
        return new RealAlgebraicNumber<C>(ring, super.abs());
    }


    /**
     * RealAlgebraicNumber summation.
     * @param S RealAlgebraicNumber.
     * @return this+S.
     */
    public RealAlgebraicNumber<C> sum(RealAlgebraicNumber<C> S) {
        return new RealAlgebraicNumber<C>(ring, super.sum(S));
    }


    /**
     * RealAlgebraicNumber summation.
     * @param c coefficient.
     * @return this+c.
     */
    public RealAlgebraicNumber<C> sum(GenPolynomial<C> c) {
        return new RealAlgebraicNumber<C>(ring, super.sum(c));
    }


    /**
     * RealAlgebraicNumber summation.
     * @param c polynomial.
     * @return this+c.
     */
    public RealAlgebraicNumber<C> sum(C c) {
        return new RealAlgebraicNumber<C>(ring, super.sum(c));
    }


    /**
     * RealAlgebraicNumber negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public RealAlgebraicNumber<C> negate() {
        return new RealAlgebraicNumber<C>(ring, super.negate());
    }


    /**
     * RealAlgebraicNumber signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        //return val.signum();
        int s = ring.engine.algebraicSign(ring.root,ring.modul,val);
        return s;
    }


    /**
     * RealAlgebraicNumber subtraction.
     * @param S RealAlgebraicNumber.
     * @return this-S.
     */
    public RealAlgebraicNumber<C> subtract(RealAlgebraicNumber<C> S) {
        return new RealAlgebraicNumber<C>(ring, super.subtract(S));
    }


    /**
     * RealAlgebraicNumber division.
     * @param S RealAlgebraicNumber.
     * @return this/S.
     */
    public RealAlgebraicNumber<C> divide(RealAlgebraicNumber<C> S) {
        return multiply(S.inverse());
    }


    /**
     * RealAlgebraicNumber inverse.
     * @see edu.jas.structure.RingElem#inverse()
     * @throws NotInvertibleException if the element is not invertible.
     * @return S with S = 1/this if defined.
     */
    public RealAlgebraicNumber<C> inverse() {
        return new RealAlgebraicNumber<C>(ring, super.inverse());
    }


    /**
     * RealAlgebraicNumber remainder.
     * @param S RealAlgebraicNumber.
     * @return this - (this/S)*S.
     */
    public RealAlgebraicNumber<C> remainder(RealAlgebraicNumber<C> S) {
        return new RealAlgebraicNumber<C>(ring, super.remainder(S));
    }


    /**
     * RealAlgebraicNumber multiplication.
     * @param S RealAlgebraicNumber.
     * @return this*S.
     */
    public RealAlgebraicNumber<C> multiply(RealAlgebraicNumber<C> S) {
        return new RealAlgebraicNumber<C>(ring, super.multiply(S));
    }


    /**
     * RealAlgebraicNumber multiplication.
     * @param c coefficient.
     * @return this*c.
     */
    public RealAlgebraicNumber<C> multiply(C c) {
        return new RealAlgebraicNumber<C>(ring, super.multiply(c));
    }


    /**
     * RealAlgebraicNumber multiplication.
     * @param c polynomial.
     * @return this*c.
     */
    public RealAlgebraicNumber<C> multiply(GenPolynomial<C> c) {
        return new RealAlgebraicNumber<C>(ring, super.multiply(c));
    }


    /**
     * RealAlgebraicNumber monic.
     * @return this with monic value part.
     */
    public RealAlgebraicNumber<C> monic() {
        return new RealAlgebraicNumber<C>(ring, super.monic());
    }


    /**
     * RealAlgebraicNumber greatest common divisor.
     * @param S RealAlgebraicNumber.
     * @return gcd(this,S).
     */
    public RealAlgebraicNumber<C> gcd(RealAlgebraicNumber<C> S) {
        return new RealAlgebraicNumber<C>(ring, super.gcd(S));
    }


    /**
     * RealAlgebraicNumber extended greatest common divisor.
     * @param S RealAlgebraicNumber.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    @SuppressWarnings("unchecked")
    public RealAlgebraicNumber<C>[] egcd(RealAlgebraicNumber<C> S) {
        AlgebraicNumber<C>[] aret = super.egcd(S);
        RealAlgebraicNumber<C>[] ret = new RealAlgebraicNumber[3];
        ret[0] = new RealAlgebraicNumber<C>(ring, aret[0]);
        ret[1] = new RealAlgebraicNumber<C>(ring, aret[1]);
        ret[2] = new RealAlgebraicNumber<C>(ring, aret[2]);
        return ret;
    }

}
