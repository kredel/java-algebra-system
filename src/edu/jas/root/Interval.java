/*
 * $Id$
 */

package edu.jas.root;


import edu.jas.structure.RingElem;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;


/**
 * Interval.
 * For example isolating interval for real roots.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public class Interval<C extends RingElem<C>> {


    /**
     * left interval border.
     */
    public final C left;
 

    /**
     * right interval border.
     */
    public final C right; 


    /**
     * Constructor.
     * @param left interval border.
     * @param right interval border.
     * @return interval [left,right].
     */
    public Interval(C left, C right) {
	this.left = left;
	this.right = right;
    }


    /**
     * Constructor.
     * @param mid left and right interval border.
     * @return interval [mid,mid].
     */
    public Interval(C mid) {
	this(mid,mid);
    }


    /**
     * String representation of Interval.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "["+left+", "+right+"]";
    }


    /**
     * Length.
     * @return |left-right|;
     */
     public C length() {
 	C m = right.subtract(left);
 	return m.abs();
     }


    /**
     * BigDecimal representation of Interval.
     */
    public BigDecimal toDecimal() {
	BigDecimal l = new BigDecimal((BigRational)(Object)left);
	BigDecimal r = new BigDecimal((BigRational)(Object)right);
	BigDecimal two = new BigDecimal(2);
	BigDecimal v = l.sum(r).divide(two);
	return v;
    }


    /*
     * Middle point.
     * @return (left-right)/2;
     */
//     public C middle() {
// 	C m = left.sum(right);
// 	C t = m.fromInteger(2L);
// 	m = m.divide(t);
// 	return m;
//     }

}
