/*
 * $Id$
 */

package edu.jas.structure;

import java.util.List;
import java.io.Reader;
import java.io.Serializable;

/**
 * ModulFactory interface for use with the polynomial classes.
 * @author Heinz Kredel
 * @param M module type.
 * @typeparam C coefficient type.
 */
public interface ModulFactory<M extends ModulElem<M,C>,
                              C extends RingElem<C>> 
                 extends AbelianGroupFactory<M> {


    /**
     * Convert list to module.
     * @param v list of ring elements.
     * @return a module element with the elements from v.
     */
    public M fromList(List<C> v);


    /**
     * random.
     * @param k length of vectors.
     * @param q fraction of non zero elements.
     * @return a random module element.
     */
    public M random(int k, float q);


}
