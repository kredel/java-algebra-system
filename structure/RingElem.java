/*
 * $Id$
 */

package edu.jas.structure;


/**
 * RingElement interface for use with the polynomial classes.
 * Combines aditive and multiplicative methods.
 * @author Heinz Kredel
 */

public interface RingElem<C extends RingElem<C>> 
                 extends AbelianGroupElem<C>, MonoidElem<C> {

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

}
