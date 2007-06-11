/*
 * $Id$
 */

package edu.jas.structure;

import java.util.List;
import java.io.Reader;
import java.io.Serializable;

/**
 * AlgebraFactory interface for use with the polynomial classes.
 * @author Heinz Kredel
 * @param A algebra type.
 * @typeparam C coefficient type.
 */
public interface AlgebraFactory<A extends AlgebraElem<A,C>,
                                C extends RingElem<C>> 
                 extends RingFactory<A> {


    /**
     * Convert list to module.
     * @param v list of ring elements.
     * @return a module element with the elements from v.
     */
    public A fromList(List<C> v);

}
