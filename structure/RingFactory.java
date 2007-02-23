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

}
