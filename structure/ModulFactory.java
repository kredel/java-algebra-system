/*
 * $Id$
 */

package edu.jas.structure;

import java.util.List;
import java.io.Reader;
import java.io.Serializable;

/**
 * ModulFactory interface for use with the polynomial classes.
 * 
 * @author Heinz Kredel
 */

public interface ModulFactory<M extends ModulElem<M,C>,
                              C extends RingElem> 
                 extends Serializable {

    public M getZERO();

    public M fromList(List<C> v);

    public M random(int k, float q);

    public M copy(M c);

    public M parse(String s);

    public M parse(Reader r);

}
