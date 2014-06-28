/*
 * $Id$
 */

package edu.jas.commons.math;


import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

import edu.jas.structure.RingElem;


/**
 * Class that wraps a JAS <code>RingElem</code> in a commons-math
 * <code>FieldElement</code>.
 * 
 * @param <C> JAS ring element type
 */
public class CMFieldElement<C extends RingElem<C>> implements FieldElement<CMFieldElement<C>>,
                Comparable<CMFieldElement<C>> {


    public final C val;


    public CMFieldElement(C v) {
        val = v;
    }


    @Override
    public CMFieldElement<C> add(CMFieldElement<C> other) {
        return new CMFieldElement<C>(val.sum(other.val));
    }


    @Override
    public CMFieldElement<C> negate() {
        return new CMFieldElement<C>(val.negate());
    }


    @Override
    public int compareTo(CMFieldElement<C> other) {
        return val.compareTo(other.val);
    }


    @Override
    public CMFieldElement<C> divide(CMFieldElement<C> other) throws ArithmeticException {
        return new CMFieldElement<C>(val.divide(other.val));
    }


    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CMFieldElement)) {
            return false;
        }
        CMFieldElement<C> other = (CMFieldElement<C>) obj;
        return this.compareTo(other) == 0;
    }


    @Override
    public Field<CMFieldElement<C>> getField() {
        return new CMField<C>(val.factory());
    }


    @Override
    public int hashCode() {
        return val.hashCode();
    }


    @Override
    public CMFieldElement<C> multiply(CMFieldElement<C> other) {
        return new CMFieldElement<C>(val.multiply(other.val));
    }


    @Override
    public CMFieldElement<C> multiply(int n) {
        return new CMFieldElement<C>(val.multiply(val.factory().fromInteger(n)));
    }


    @Override
    public CMFieldElement<C> subtract(CMFieldElement<C> other) {
        return new CMFieldElement<C>(val.subtract(other.val));
    }


    @Override
    public CMFieldElement<C> reciprocal() {
        return new CMFieldElement<C>(val.inverse());
    }


    /**
     * Get the string representation.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        // s.append("JLAdapter(");
        s.append(val.toString());
        // s.append(")");
        return s.toString();
    }


    public boolean isOne() {
        return val.isONE();
    }


    public boolean isZero() {
        return val.isZERO();
    }

}
