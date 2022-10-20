/*
 * $Id$
 */

package edu.jas.structure;


/**
 * Value factory interface. Defines constructor from value.
 * @typeparam C base element type
 * @typeparam D result element type
 * @author Heinz Kredel
 */
public interface ValueFactory<C extends RingElem<C>, D extends RingElem<D>> {


    /**
     * Create from value.
     */
    public D create(C n);


    /**
     * Factory for value elements.
     */
    public RingFactory<C> valueFactory();

}
