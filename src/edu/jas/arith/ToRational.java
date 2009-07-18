/*
 * $Id$
 */

package edu.jas.arith;


/**
 * Interface with toRational method.
 * @author Heinz Kredel
 */

public interface ToRational /*extends Cloneable*/ {

    /**
     * Return a BigRational approximation of this Element.
     * @return a BigRational approximation of this.
     */
    public BigRational toRational();

}
