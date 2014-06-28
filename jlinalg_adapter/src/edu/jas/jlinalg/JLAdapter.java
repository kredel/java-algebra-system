/*
 * $Id$
 */

package edu.jas.jlinalg;


import org.jlinalg.DivisionByZeroException;
import org.jlinalg.IRingElement;
import org.jlinalg.IRingElementFactory;
import org.jlinalg.operator.MonadicOperator;

import edu.jas.structure.RingElem;


/**
 * Class that wraps a JAS <code>RingElem</code> in an JLinALg <code>IRingElement</code>.
 * @param <C> JAS ring element type 
 * @author Heinz Kredel
 */

public class JLAdapter<C extends RingElem<C>> implements IRingElement<JLAdapter<C>> {


    public final C val;


    public JLAdapter(C v) {
        val = v;
    }


    /**
     * Get the string representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        //s.append("JLAdapter(");
        s.append(val.toString());
        //s.append(")");
        return s.toString();
    }


    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JLAdapter)) {
            return false;
        }
        JLAdapter<C> other = (JLAdapter<C>) obj;
        return this.compareTo(other) == 0;
    }


    @Override
    public int hashCode() {
        return val.hashCode();
    }


    @Override
    public JLAdapter<C> abs() {
        return new JLAdapter<C>(val.abs());
    }


    @Override
    public JLAdapter<C> norm() {
        return new JLAdapter<C>(val.abs());
    }


    @Override
    public JLAdapter<C> add(JLAdapter<C> other) {
        return new JLAdapter<C>(val.sum(other.val));
    }


    @Override
    public JLAdapter<C> apply(MonadicOperator<JLAdapter<C>> fun) {
        return fun.apply(this);
    }


    @Override
    public int compareTo(JLAdapter<C> other) {
        return val.compareTo(other.val);
    }


    @Override
    public JLAdapter<C> divide(JLAdapter<C> other) throws DivisionByZeroException {
        return new JLAdapter<C>(val.divide(other.val));
    }


    @Override
    public boolean ge(JLAdapter<C> val) {
        return this.compareTo(val) >= 0;
    }


    @Override
    public IRingElementFactory<JLAdapter<C>> getFactory() {
        return new JLAdapterFactory<C>(val.factory());
    }


    @Override
    public boolean gt(JLAdapter<C> val) {
        return this.compareTo(val) > 0;
    }


    @Override
    public JLAdapter<C> invert() throws DivisionByZeroException {
        return new JLAdapter<C>(val.inverse());
    }


    @Override
    public boolean isOne() {
        return val.isONE();
    }


    @Override
    public boolean isZero() {
        return val.isZERO();
    }


    @Override
    public boolean le(JLAdapter<C> val) {
        return this.compareTo(val) <= 0;
    }


    @Override
    public boolean lt(JLAdapter<C> val) {
        return this.compareTo(val) < 0;
    }


    @Override
    public JLAdapter<C> multiply(JLAdapter<C> other) {
        return new JLAdapter<C>(val.multiply(other.val));
    }


    @Override
    public JLAdapter<C> negate() {
        return new JLAdapter<C>(val.negate());
    }


    @Override
    public JLAdapter<C> subtract(JLAdapter<C> other) {
        return new JLAdapter<C>(val.subtract(other.val));
    }

}
