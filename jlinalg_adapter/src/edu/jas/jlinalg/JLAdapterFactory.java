/*
 * $Id$
 */

package edu.jas.jlinalg;


import java.util.Random;

import org.jlinalg.IRingElement;
import org.jlinalg.IRingElementFactory;
import org.jlinalg.Matrix;
import org.jlinalg.Vector;

import edu.jas.structure.ElemFactory;
import edu.jas.structure.Element;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Class that wraps a JAS <code>RingFactory</code> in an JLinALg
 * <code>IRingElementFactory</code>.
 * @param <C> JAS ring element type
 * @author Heinz Kredel
 */

public class JLAdapterFactory<C extends RingElem<C>> implements IRingElementFactory<JLAdapter<C>> {


    public final RingFactory<C> fac;


    /**
     * @param f
     */
    public JLAdapterFactory(RingFactory<C> f) {
        fac = f;
    }


    /**
     * @param f
     */
    @SuppressWarnings("unchecked")
    public JLAdapterFactory(ElemFactory<C> f) {
        this((RingFactory<C>) f);
    }


    /**
     * Get the string representation.
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("unchecked")
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        //s.append("JLAdapterFactory(");
        String f = null;
        try {
            f = ((Element<C>) fac).toScriptFactory();
        } catch (Exception ignored) {
            f = fac.toScript();
        }
        if (f != null) {
            s.append(f);
        }
        //s.append(")");
        return s.toString();
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        //System.out.println("factory equals, this = " + this + ", obj = " + obj);
        if (!(obj instanceof IRingElementFactory)) {
            return false;
        }
        IRingElementFactory other = (IRingElementFactory) obj;
        if (!(other instanceof JLAdapterFactory)) {
            return false;
        }
        JLAdapterFactory<C> fother = (JLAdapterFactory<C>) other;
        RingFactory<C> ofac = fother.fac;
        //System.out.println("factory equals, this = " + fac.getClass() + ", obj = " + ofac.getClass());
        if (!fac.getClass().getName().equals(ofac.getClass().getName())) {
            return false;
        }
        RingFactory<C> ofac1 = null;
        try {
            ofac1 = (RingFactory<C>) ((RingElem<C>) ofac).factory();
        } catch (ClassCastException e) {
        }
        if ( /*fac*/ofac.equals(ofac1)) { // case BigInteger etc
            return true;
        }
        System.out.println("factory equals, this = " + ofac + ", obj = " + ofac1);
        //  if (fac.characteristic().equals(ffac.characteristic())) {
        //      return true;
        //  }
        return fac.equals(ofac);
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        RingFactory<C> fac1 = null;
        try {
            fac1 = (RingFactory<C>) ((RingElem<C>) fac).factory();
        } catch (ClassCastException e) {
        }
        if (fac.equals(fac1)) { // case BigInteger etc
            //int h = fac.getClass().getSimpleName().hashCode();
            //h = h * 37 + fac.characteristic().hashCode();
            return fac.getClass().getName().hashCode();
        }
        return fac.hashCode();
    }


    @Override
    public JLAdapter<C> get(Object o) {
        if (o == null) {
            return null;
        }
        String s = o.toString();
        return new JLAdapter<C>(fac.parse(s));
    }


    @Override
    public JLAdapter<C> get(int i) {
        return new JLAdapter<C>(fac.fromInteger(i));
    }


    @Override
    public JLAdapter<C> get(long i) {
        return new JLAdapter<C>(fac.fromInteger(i));
    }


    @Override
    public JLAdapter<C> get(double d) {
        throw new RuntimeException("not implemented " + d);
    }


    @SuppressWarnings("unchecked")
    @Override
    public JLAdapter<C>[] getArray(int size) {
        JLAdapter<C>[] arr = (JLAdapter<C>[]) new JLAdapter[size];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = zero();
        }
        return arr;
    }


    @SuppressWarnings("unchecked")
    @Override
    public JLAdapter<C>[][] getArray(int rows, int columns) {
        JLAdapter<C>[][] arr = (JLAdapter<C>[][]) new JLAdapter[rows][columns];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = getArray(columns);
        }
        return arr;
    }


    /**
     * Minus one? OK
     * @see org.jlinalg.IRingElementFactory#m_one()
     */
    @Override
    public JLAdapter<C> m_one() {
        return new JLAdapter<C>(fac.getONE().negate());
    }


    @Override
    public JLAdapter<C> one() {
        return new JLAdapter<C>(fac.getONE());
    }


    @Override
    public JLAdapter<C> zero() {
        return new JLAdapter<C>(fac.getZERO());
    }


    @Deprecated
    @Override
    public JLAdapter<C> randomValue(Random random) {
        return new JLAdapter<C>(fac.random(3, random));
    }


    @Override
    public JLAdapter<C> randomValue() {
        return new JLAdapter<C>(fac.random(3));
    }


    @Deprecated
    @Override
    public JLAdapter<C> gaussianRandomValue(Random random) {
        throw new RuntimeException("not implemented " + random);
    }


    @Override
    public JLAdapter<C> gaussianRandomValue() {
        throw new RuntimeException("not implemented");
    }


    @Deprecated
    @Override
    public JLAdapter<C> randomValue(Random random, JLAdapter<C> min, JLAdapter<C> max) {
        throw new RuntimeException("not implemented");
    }


    @Override
    public JLAdapter<C> randomValue(JLAdapter<C> min, JLAdapter<C> max) {
        throw new RuntimeException("not implemented");
    }


    @SuppressWarnings("unchecked")
    @Override
    public Vector<JLAdapter<C>> convert(Vector<? extends IRingElement<?>> from) {
        if (true) {
            throw new RuntimeException("not implemented");
        }
        JLAdapter<C>[] to = (JLAdapter<C>[]) new JLAdapter[from.length()];
        for (int row = 0; row < from.length(); row++) {
            to[row] = this.get(from.getEntry(row));
        }
        return new Vector<JLAdapter<C>>(to, this);
    }


    @SuppressWarnings("unchecked")
    @Override
    public Matrix<JLAdapter<C>> convert(Matrix<? extends IRingElement<?>> from) {
        if (true) {
            throw new RuntimeException("not implemented");
        }
        JLAdapter<C>[][] to = (JLAdapter<C>[][]) new JLAdapter[from.getRows()][from.getCols()];
        for (int row = 0; row < from.getRows(); row++) {
            for (int col = 0; col < from.getCols(); col++) {
                to[row][col] = this.get(from.get(row, col));
            }
        }
        return new Matrix<JLAdapter<C>>(to, this);
    }

}
