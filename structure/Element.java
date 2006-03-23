/*
 * $Id: $
 */

package edu.jas.structure;

import java.io.Serializable;


/**
 * Element interface for use with the polynomial classes.
 * @author Heinz Kredel
 */

public interface Element<C extends Element<C>> extends Cloneable, 
                                                       Comparable<C>, 
                                                       Serializable {

    /**
     * Clone this Element.
     * @return Creates and returns a copy of this Elemnt.
     */
    public C clone();


    /**
     * Test if this is equal to b.
     * @param b
     * @return true if this is equal to b, else false.
     */
    public boolean equals(Object b);


    /**
     * Hashcode of this Element.
     * @return the hashCode.
     */
    public int hashCode();


    /**
     * Compare this to b.
     * <b>Note:</b> may not be meaningful if structure has no order.
     * @param b
     * @return 0 if this is equal to b, else +1 or -1.
     */
    public int compareTo(C b);

}