/*
 * $Id$
 */

package edu.jas.root;


import edu.jas.structure.RingElem;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;

import edu.jas.poly.AlgebraicNumber;


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
     */
    public Interval(C left, C right) {
	this.left = left;
	this.right = right;
    }


    /**
     * Constructor.
     * @param mid left and right interval border.
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


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object b) {
        if ( ! ( b instanceof Interval ) ) {
            return false;
        }
        Interval<C> a = null;
        try {
            a = (Interval<C>) b;
        } catch (ClassCastException e) {
        }
        return left.equals(a.left) && right.equals(a.right);
    }


    /** Hash code for this Interval.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * left.hashCode() + right.hashCode();
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
        if ( (Object)left instanceof BigRational ) {
            BigDecimal l = new BigDecimal((BigRational)(Object)left);
            BigDecimal r = new BigDecimal((BigRational)(Object)right);
            BigDecimal two = new BigDecimal(2);
            BigDecimal v = l.sum(r).divide(two);
            return v;
        } else if ( (Object)left instanceof RealAlgebraicNumber ) {
            RealAlgebraicNumber x = (RealAlgebraicNumber) left;
            RealAlgebraicNumber y = (RealAlgebraicNumber) right;
            BigDecimal l = x.magnitude();
            BigDecimal r = y.magnitude();
            BigDecimal two = new BigDecimal(2);
            BigDecimal v = l.sum(r).divide(two);
            return v;
        } else {
            throw new RuntimeException("toDecimal of interval types not implemented");
        }
    }


    /*
     * Middle point.
     * @return (left-right)/2;
     */
//     public C middle() {
// 	C m = left.sum(right);
// 	C t = m.getRing().fromInteger(2L);
// 	m = m.divide(t);
// 	return m;
//     }

}
