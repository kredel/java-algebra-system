/*
 * $Id$
 */

package edu.jas.structure;

import java.io.Serializable;

import java.util.List;


/**
 * ModulElement interface.
 * @author Heinz Kredel
 * @typeparam M module type.
 * @typeparam C scalar type.
 */
public interface ModulElem<M extends ModulElem<M,C>,
                           C extends RingElem<C>> 
                 extends AbelianGroupElem< M > {

    public M scalarMultiply(C s);

    public M linearCombination(C a, M b, C s);

    public M linearCombination(M b, C s);

    public C scalarProduct(M b);

    public M scalarProduct(List<M> b);

}
