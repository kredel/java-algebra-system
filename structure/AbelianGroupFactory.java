/*
 * $Id$
 */

package edu.jas.structure;


/**
 * RingFactory interface for use with the polynomial classes.
 * 
 * @author Heinz Kredel
 */

public interface AbelianGroupFactory<C extends AbelianGroupElem<C>> 
                 extends ElemFactory<C> {


    /**
     * Get the constant zero for the AbelianGroupElem.
     * @return 0.
     */
    public C getZERO();


}
