/*
 * $Id$
 */

package edu.jas.structure;

import java.io.Serializable;

import java.util.List;


/**
 * AlgabraElement interface.
 * @author Heinz Kredel
 * @typeparam A algebra type.
 * @typeparam C scalar type.
 */
public interface AlgebraElem<A extends AlgebraElem<A,C>,
                             C extends RingElem<C>> 
                 extends RingElem< A > {

    public A scalarMultiply(C s);

    public A linearCombination(C a, A b, C s);

    public A linearCombination(A b, C s);

}
