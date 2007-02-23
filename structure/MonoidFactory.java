/*
 * $Id$
 */

package edu.jas.structure;


/**
 * MonoidFactory interface for use with the polynomial classes.
 * 
 * @author Heinz Kredel
 */

public interface MonoidFactory<C extends MonoidElem<C>> 
                 extends ElemFactory<C> {


    /**
     * Get the constant one for the MonoidElem.
     * @return 1.
     */
    public C getONE();


    /**
     * Query if this monoid is commutative.
     * @return true if this monoid is commutative, else false.
     */
    public boolean isCommutative();


    /**
     * Query if this ring is associative.
     * @return true if this monoid is associative, else false.
     */
    public boolean isAssociative();


}
