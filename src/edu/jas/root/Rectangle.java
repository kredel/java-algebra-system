/*
 * $Id$
 */

package edu.jas.root;


import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.arith.ToRational;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.ElemFactory;
import edu.jas.structure.Complex;


/**
 * Rectangle. For example isolating interval for real roots.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public class Rectangle<C extends RingElem<C> /*& ToRational*/ > {


    /**
     * rectangle corners.
     */
    public final Complex<C>[] corners;


    /**
     * Constructor.
     * @param left interval border.
     * @param right interval border.
     */
    @SuppressWarnings("unchecked")
    public Rectangle(Complex<C>[] c) {
        if ( c.length < 5 ) {
            corners = (Complex<C>[]) new Complex[5];
            for ( int i = 0; i < 4; i++ ) {
                corners[i] = c[i];
            }
        } else {
            corners = c;
        }
        if ( corners[4] == null ) {
            corners[4] = corners[0];
        }
    }


    /**
     * String representation of Rectangle.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return centerApprox() + " = [" + corners[0] + ", " + corners[1] + ", " + corners[2] + ", " + corners[3] + "]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Rectangle.
     */
    public String toScript() {
        // Python case
        return "(" + corners[0] + ", " + corners[1] + ", " + corners[2] + ", " + corners[3] + ")";
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public Rectangle<C> clone() {
        return new Rectangle<C>(corners);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof Rectangle)) {
            return false;
        }
        Rectangle<C> a = null;
        try {
            a = (Rectangle<C>) b;
        } catch (ClassCastException e) {
        }
        for ( int i = 0; i < 4; i++ ) {
            if ( ! corners[i].equals(a.corners[i]) ) {
                return false;
            }
        }
        return true;
    }


    /**
     * Hash code for this Rectangle.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hc = 0;
        for ( int i = 0; i < 3; i++ ) {
            hc += 37 * corners[i].hashCode();
        }
        return 37 * hc + corners[3].hashCode();
    }


    /**
     * Approximation of center.
     * @return r + i m as decimal approximation.
     */
    public String centerApprox() {
        C r = corners[2].getRe().subtract( corners[1].getRe() );
        C m = corners[0].getIm().subtract( corners[1].getIm() );
        ElemFactory<C> rf = r.factory();
        C two = rf.fromInteger(2);
        r = r.divide(two);
        m = m.divide(two);
        r = corners[1].getRe().sum( r );
        m = corners[1].getIm().sum( m );

        BigRational rs = new BigRational(r.toString());
        //System.out.println("s  = " + s);
        BigDecimal rd = new BigDecimal(rs);

        BigRational ms = new BigRational(m.toString());
        //System.out.println("m  = " + m);
        BigDecimal md = new BigDecimal(ms);

        StringBuffer s = new StringBuffer();
        s.append("[ ");
        s.append(rd.toString());
        s.append(" i ");
        s.append(md.toString());
        s.append(" ]");
        return s.toString();
    }

    /**
     * Length.
     * @return |left-right|;
    public C length() {
        C m = right.subtract(left);
        return m.abs();
    }
     */


    /**
     * BigDecimal representation of Rectangle.
    public BigDecimal toDecimal() {
        BigDecimal l = new BigDecimal(left.toRational());
        BigDecimal r = new BigDecimal(right.toRational());
        BigDecimal two = new BigDecimal(2);
        BigDecimal v = l.sum(r).divide(two);
        return v;
    }
     */


    /**
     * Rational middle point.
     * @return (left-right)/2;
    public BigRational rationalMiddle() {
        BigRational m = left.toRational().sum(right.toRational());
        BigRational t = new BigRational(1L,2L);
        m = m.multiply(t);
        return m;
    }
     */
}
