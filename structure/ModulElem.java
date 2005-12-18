/*
 * $Id$
 */

package edu.jas.structure;

import java.io.Serializable;

import java.util.List;


/**
 * ModulElement interface for use with the polynomial classes.
 * @author Heinz Kredel
 * @param M module type.
 * @param C coefficient type.
 */
public interface ModulElem<M extends ModulElem<M,C>,
                           C extends RingElem> 
                 extends Cloneable, 
                         Comparable< M >, 
                         Serializable {
    public M clone();


    public boolean isZERO();

    public boolean equals(Object b);

    public int hashCode();

    public int compareTo(M b);

    public int signum();


    public M add(M b);

    public M subtract(M b);

    public M negate();


    public M scalarMultiply(C s);

    public M linearCombination(C a, M b, C s);

    public M linearCombination(M b, C s);

    public C scalarProduct(M b);

    public M scalarProduct(List<M> b);

}
