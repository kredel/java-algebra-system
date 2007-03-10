/*
 * $Id$
 */

package edu.jas.structure;


/**
 * RingFactory interface for use with the polynomial classes.
 * 
 * @author Heinz Kredel
 */

public interface RingFactory<C extends RingElem<C>> 
    extends AbelianGroupFactory<C>, MonoidFactory<C> {


    /**
     * Query if this ring is a field.
     * May return false if it is to hard to determine if this ring is a field.
     * @return true if it is known that this ring is a field, else false.
     */
    public boolean isField();

}
