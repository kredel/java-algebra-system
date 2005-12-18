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
 * @param C coefficient type.
 */
public interface ModulFactory<M extends ModulElem<M,C>,
                              C extends RingElem> 
                 extends Serializable {

    /**
     * getZERO.
     * @return ZERO from the respective module.
     */
    public M getZERO();


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


    /**
     * copy.
     * @param c module element.
     * @return a copy of c.
     */
    public M copy(M c);


    /**
     * parse.
     * @param s string which contains a module element.
     * @return a module element.
     */
    public M parse(String s);


    /**
     * parse.
     * @param r a stream reader.
     * @return a module element.
     */
    public M parse(Reader r);

}
