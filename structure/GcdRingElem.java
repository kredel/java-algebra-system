/*
 * $Id: $
 */

package edu.jas.structure;


/**
 * GcdRingElement interface for use with the polynomial classes.
 * Adds greatest common divisor and extended greatest common divisor.
 * @author Heinz Kredel
 */

public interface GcdRingElem<C extends GcdRingElem<C>> 
                 extends RingElem<C> {

    /**
     * Greatest common divisor.
     * @param b other element.
     * @return gcd(this,b).
     */
    public C gcd(C b);


    /**
     * Extended greatest common divisor.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public C[] egcd(C b);

}
