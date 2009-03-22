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
            return val.toString(ring.ring.getVars());
        } else {
            return "RealAlgebraicNumber[ " + val.toString() + " ]";
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
        throw new RuntimeException("real compare to");
        //return val.compareTo(b.val);
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
        return (0 == compareTo(a));
    }


    /**
     * Hash code for this RealAlgebraicNumber.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * val.hashCode() + ring.hashCode();
    }


    /**
     * RealAlgebraicNumber absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public RealAlgebraicNumber<C> abs() {
        return new RealAlgebraicNumber<C>(ring, val.abs());
    }


    /**
     * RealAlgebraicNumber summation.
     * @param S RealAlgebraicNumber.
     * @return this+S.
     */
    public RealAlgebraicNumber<C> sum(RealAlgebraicNumber<C> S) {
        return new RealAlgebraicNumber<C>(ring, val.sum(S.val));
    }


    /**
     * RealAlgebraicNumber summation.
     * @param c coefficient.
     * @return this+c.
     */
    public RealAlgebraicNumber<C> sum(GenPolynomial<C> c) {
        return new RealAlgebraicNumber<C>(ring, val.sum(c));
    }


    /**
     * RealAlgebraicNumber summation.
     * @param c polynomial.
     * @return this+c.
     */
    public RealAlgebraicNumber<C> sum(C c) {
        return new RealAlgebraicNumber<C>(ring, val.sum(c));
    }


    /**
     * RealAlgebraicNumber negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public RealAlgebraicNumber<C> negate() {
        return new RealAlgebraicNumber<C>(ring, val.negate());
    }


    /**
     * RealAlgebraicNumber signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        //return val.signum();
        throw new RuntimeException("real compare to");
    }


    /**
     * RealAlgebraicNumber subtraction.
     * @param S RealAlgebraicNumber.
     * @return this-S.
     */
    public RealAlgebraicNumber<C> subtract(RealAlgebraicNumber<C> S) {
        return new RealAlgebraicNumber<C>(ring, val.subtract(S.val));
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
        try {
            return new RealAlgebraicNumber<C>(ring, val.modInverse(ring.modul));
        } catch (NotInvertibleException e) {
            throw new NotInvertibleException("val = " + val + ", modul = " + ring.modul + ", gcd = " + val.gcd(ring.modul));
        }
    }


    /**
     * RealAlgebraicNumber remainder.
     * @param S RealAlgebraicNumber.
     * @return this - (this/S)*S.
     */
    public RealAlgebraicNumber<C> remainder(RealAlgebraicNumber<C> S) {
        if ( S == null || S.isZERO()) {
           throw new RuntimeException(this.getClass().getName()
                                      + " division by zero");
        }
        if ( S.isONE()) {
           return ring.getZERO();
        }
        if ( S.isUnit() ) {
           return ring.getZERO();
        }
        GenPolynomial<C> x = val.remainder(S.val);
        return new RealAlgebraicNumber<C>(ring, x);
    }


    /**
     * RealAlgebraicNumber multiplication.
     * @param S RealAlgebraicNumber.
     * @return this*S.
     */
    public RealAlgebraicNumber<C> multiply(RealAlgebraicNumber<C> S) {
        GenPolynomial<C> x = val.multiply(S.val);
        return new RealAlgebraicNumber<C>(ring, x);
    }


    /**
     * RealAlgebraicNumber multiplication.
     * @param c coefficient.
     * @return this*c.
     */
    public RealAlgebraicNumber<C> multiply(C c) {
        GenPolynomial<C> x = val.multiply(c);
        return new RealAlgebraicNumber<C>(ring, x);
    }


    /**
     * RealAlgebraicNumber multiplication.
     * @param c polynomial.
     * @return this*c.
     */
    public RealAlgebraicNumber<C> multiply(GenPolynomial<C> c) {
        GenPolynomial<C> x = val.multiply(c);
        return new RealAlgebraicNumber<C>(ring, x);
    }


    /**
     * RealAlgebraicNumber monic.
     * @return this with monic value part.
     */
    public RealAlgebraicNumber<C> monic() {
        return new RealAlgebraicNumber<C>(ring, val.monic());
    }


    /**
     * RealAlgebraicNumber greatest common divisor.
     * @param S RealAlgebraicNumber.
     * @return gcd(this,S).
     */
    public RealAlgebraicNumber<C> gcd(RealAlgebraicNumber<C> S) {
        if (S.isZERO()) {
            return this;
        }
        if (isZERO()) {
            return S;
        }
        if (isUnit() || S.isUnit()) {
            return ring.getONE();
        }
        return new RealAlgebraicNumber<C>(ring, val.gcd(S.val));
    }


    /**
     * RealAlgebraicNumber extended greatest common divisor.
     * @param S RealAlgebraicNumber.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    @SuppressWarnings("unchecked")
    public RealAlgebraicNumber<C>[] egcd(RealAlgebraicNumber<C> S) {
        RealAlgebraicNumber<C>[] ret = new RealAlgebraicNumber[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if (S == null || S.isZERO()) {
            ret[0] = this;
            return ret;
        }
        if (isZERO()) {
            ret[0] = S;
            return ret;
        }
        if (this.isUnit() || S.isUnit()) {
            ret[0] = ring.getONE();
            if (this.isUnit() && S.isUnit()) {
                RealAlgebraicNumber<C> half = ring.fromInteger(2).inverse();
                ret[1] = this.inverse().multiply(half);
                ret[2] = S.inverse().multiply(half);
                return ret;
            }
            if (this.isUnit()) {
                // oder inverse(S-1)?
                ret[1] = this.inverse();
                ret[2] = ring.getZERO();
                return ret;
            }
            // if ( S.isUnit() ) {
            // oder inverse(this-1)?
            ret[1] = ring.getZERO();
            ret[2] = S.inverse();
            return ret;
            //}
        }
        //System.out.println("this = " + this + ", S = " + S);
        GenPolynomial<C>[] qr;
        GenPolynomial<C> q = this.val;
        GenPolynomial<C> r = S.val;
        GenPolynomial<C> c1 = ring.ring.getONE();
        GenPolynomial<C> d1 = ring.ring.getZERO();
        GenPolynomial<C> c2 = ring.ring.getZERO();
        GenPolynomial<C> d2 = ring.ring.getONE();
        GenPolynomial<C> x1;
        GenPolynomial<C> x2;
        while (!r.isZERO()) {
            qr = q.divideAndRemainder(r);
            q = qr[0];
            x1 = c1.subtract(q.multiply(d1));
            x2 = c2.subtract(q.multiply(d2));
            c1 = d1;
            c2 = d2;
            d1 = x1;
            d2 = x2;
            q = r;
            r = qr[1];
        }
        //System.out.println("q = " + q + "\n c1 = " + c1 + "\n c2 = " + c2);
        ret[0] = new RealAlgebraicNumber<C>(ring, q);
        ret[1] = new RealAlgebraicNumber<C>(ring, c1);
        ret[2] = new RealAlgebraicNumber<C>(ring, c2);
        return ret;
    }

}
