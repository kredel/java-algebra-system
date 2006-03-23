/*
 * $Id: $
 */

package edu.jas.structure;


/**
 * AbelianGroupElement interface for use with the polynomial classes.
 * Defines the additive methods.
 * @author Heinz Kredel
 */

public interface AbelianGroupElem<C extends AbelianGroupElem<C>> 
         extends Element<C> {


    /**
     * Test if this is zero.
     * @return true if this is 0, else false.
     */
    public boolean isZERO();


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

}