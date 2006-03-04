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


    /**
     * Clone this RingElem.
     * @return Creates and returns a copy of this object.
     */
    public C clone();


    /**
     * Test if this is zero.
     * @return true if this is 0, else false.
     */
    public boolean isZERO();


    /**
     * Test if this is one.
     * @return true if this is 1, else false.
     */
    public boolean isONE();


    /**
     * Test if this is a unit. 
     * I.e. there exists x with this.multiply(x).isONE() == true.
     * @return true if this is a unit, else false.
     */
    public boolean isUnit();


    /**
     * Test if this is equal to b.
     * @param b
     * @return true if this is equal to b, else false.
     */
    public boolean equals(Object b);


    /**
     * Hashcode of this RingElem.
     * @return the hashCode.
     */
    public int hashCode();


    /**
     * Compare this to b.
     * @param b
     * @return 0 if this is equal to b, else +1 or -1.
     */
    public int compareTo(C b);


    /**
     * Signum.
     * @return the sign of this.
     */
    public int signum();



    /**
     * Sum of this and S.
     * @param S
     * @return this + S.
     */
    public C sum(C S);


    /**
     * Subtract S from this.
     * @param S
     * @return this - S.
     */
    public C subtract(C S);


    /**
     * Negate this.
     * @return - this.
     */
    public C negate();


    /**
     * Absolute value of this.
     * @return |this|.
     */
    public C abs();



    /**
     * Multiply this with S.
     * @param S
     * @return this * S.
     */
    public C multiply(C S);


    /**
     * Divide this by S.
     * @param S
     * @return this / S.
     */
    public C divide(C S);


    /**
     * Remainder after division of this by S.
     * @param S
     * @return this - (this / S) * S.
     */
    public C remainder(C S);


    /**
     * Inverse of this.
     * @return x with this * x = 1.
     */
    public C inverse();

}
