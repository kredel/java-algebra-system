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

}
