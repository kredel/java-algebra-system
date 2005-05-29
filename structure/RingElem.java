/*
 * $Id$
 */

package edu.jas.structure;

import java.io.Serializable;

/**
 * RingElement interface for use with the polynomial classes.
 * @author Heinz Kredel
 */

public interface RingElem<C extends RingElem> extends Cloneable, 
                                                      Comparable<C>, 
                                                      Serializable {

    public C clone();

    public boolean isZERO();

    public boolean isONE();

    public boolean isUnit();

    public boolean equals(Object b);

    public int compareTo(C b);

    public int signum();

    public C add(C S);

    public C subtract(C S);

    public C negate();

    public C abs();

    public C multiply(C S);

    public C divide(C S);

    public C remainder(C S);

    public C inverse();

}
