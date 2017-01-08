/*
 * $Id$
 */

package edu.jas.structure;


/**
 * Ring element interface. Combines additive and multiplicative methods. Adds
 * also gcd because of polynomials.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface RingElem<C extends RingElem<C>> extends AbelianGroupElem<C>, MonoidElem<C> {


    //     /** Quotient and remainder.
    //      * @param b other element.
    //      * @return C[] { q, r } with this = q b + r and 0 &le; r &lt; |b|.
    //      */
    //     public C[] quotientRemainder(C b);


    /**
     * Greatest common divisor.
     * @param b other element.
     * @return gcd(this,b).
     */
    public C gcd(C b);


    /**
     * Extended greatest common divisor.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public C[] egcd(C b);


    /**
     * Right division.
     * Returns commutative divide if not overwritten.
     * @param a element.
     * @return right, with a * right = this
     */
    default public C rightDivide(C a) {
	return divide(a);
    }


    /**
     * Left division.
     * Returns commutative divide if not overwritten.
     * @param a element.
     * @return left, with left * a = this
     */
    default public C leftDivide(C a) {
	return divide(a);
    }


    /**
     * Right remainder.
     * Returns commutative remainder if not overwritten.
     * @param a element.
     * @return r = this - a * (1/right), where a * right = this.
     */
    default public C rightRemainder(C a) {
	return remainder(a);
    }


    /**
     * Left remainder.
     * Returns commutative remainder if not overwritten.
     * @param a element.
     * @return r = this - (1/left) * a, where left * a = this.
     */
    default public C leftRemainder(C a) {
	return remainder(a);
    }


    /**
     * Two-sided division.
     * Returns commutative divide if not overwritten.
     * @param a element.
     * @return [left,right], with left * a * right = this
     */
    @SuppressWarnings("unchecked")
    default public C[] twosidedDivide(C a) {
        C[] ret = (C[]) new Object[2];
        ret[0] = divide(a);
        ret[1] = ((RingFactory<C>)factory()).getONE(); //rightDivide(a);
	return ret;
    }


    /**
     * Two-sided remainder.
     * Returns commutative remainder if not overwritten.
     * @param a element.
     * @return r = this - (a/left) * a * (a/right), where left * a * right = this.
     */
    default public C twosidedRemainder(C a){
	return remainder(a);
    }



    /**
     * Left greatest common divisor.
     * Returns commutative greatest common divisor if not overwritten.
     * @param b other element.
     * @return leftGcd(this,b).
     */
    default public C leftGcd(C b) {
	return gcd(b);
    }


    /**
     * Right greatest common divisor.
     * Returns commutative greatest common divisor if not overwritten.
     * @param b other element.
     * @return rightGcd(this,b).
     */
    default public C rightGcd(C b) {
	return gcd(b);
    }

}
