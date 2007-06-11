/*
 * $Id$
 */

package edu.jas.vector;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.AlgebraElem;


/**
 * GenMatrix generic vector implementing RingElem.
 * vectors of n columns over C.
 * @author Heinz Kredel
 */

public class GenMatrix<C extends RingElem<C> > 
    implements AlgebraElem<GenMatrix<C>,C> {

    private static Logger logger = Logger.getLogger(GenMatrix.class);

    public final GenMatrixRing< C > ring;
    public final List<C> val;


    /**
     * Constructors for GenMatrix.
     */

    public GenMatrix(GenMatrixRing< C > m) {
        this( m, m.getZERO().val );
    }

    protected GenMatrix(GenMatrixRing< C > m, List<C> v) {
        ring = m;
        val = v;
    }


    /**
     * toString method.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("( ");
        boolean first = true;
        for ( C c : val ) {
            if ( first ) {
                first = false;
            } else {
                s.append(", ");
            }
            s.append( c.toString() );
        }
        s.append(" ) :: ");
        s.append(ring.toString());
        s.append("\n");
        return s.toString();
    }


    /**
     * clone method.
     */
    @Override
    public GenMatrix<C> clone() {
        //return ring.copy(this);
        ArrayList<C> av = (ArrayList<C>) val;
        return new GenMatrix<C>( ring, (List<C>)av.clone() );
    }


    /**
     * test if this is equal to a zero vector.
     */
    public boolean isZERO() {
        return ( 0 == this.compareTo( ring.getZERO() ) );
    }


    /**
     * Test if this is one.
     * @return true if this is 1, else false.
     */
    public boolean isONE() {
        return ( 0 == this.compareTo( ring.getONE() ) );
    }


    /**
     * equals method.
     */
    @Override
    public boolean equals( Object other ) { 
        if ( ! (other instanceof GenMatrix) ) {
            return false;
        }
        GenMatrix ovec = (GenMatrix)other;
        if ( ! ring.equals(ovec.ring) ) {
            return false;
        }
        if ( ! val.equals(ovec.val) ) {
            return false;
        }
        return true;
    }


    /** Hash code for this GenMatrix.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return 37 * val.hashCode() + ring.hashCode();
    }


    /**
     * compareTo, lexicogaphical comparison.
     * @param b other
     * @return 1 if (this &lt; b), 0 if (this == b) or -1 if (this &gt; b).
     */
    public int compareTo(GenMatrix<C> b) {
        if ( ! ring.equals(b.ring) ) {
            return -1;
        }
        List<C> oval = b.val;
        int i = 0;
        for ( C c : val ) {
            int s = c.compareTo( oval.get( i++ ) );
            if ( s != 0 ) {
                return s;
            }
        }
        return 0;
    }


    /**
     * Test if this is a unit. 
     * I.e. there exists x with this.multiply(x).isONE() == true.
     * @return true if this is a unit, else false.
     */
    public boolean isUnit() {
        return false;
    }


    /**
     * sign of vector.
     * @return 1 if (this &lt; 0), 0 if (this == 0) or -1 if (this &gt; 0).
     */
    public int signum() {
        return compareTo( ring.getZERO() );
    }


    /**
     * Sum of vectors.
     * @return this+b
     */
    public GenMatrix<C> sum(GenMatrix<C> b) {
        List<C> oval = b.val;
        ArrayList<C> a = new ArrayList<C>( ring.cols );
        int i = 0;
        for ( C c : val ) {
            C e = c.sum( oval.get( i++ ) );
            a.add( e );
        }
        return new GenMatrix<C>(ring,a);
    }


    /**
     * Difference of vectors.
     * @return this-b
     */
    public GenMatrix<C> subtract(GenMatrix<C> b) {
        List<C> oval = b.val;
        ArrayList<C> a = new ArrayList<C>( ring.cols );
        int i = 0;
        for ( C c : val ) {
            C e = c.subtract( oval.get( i++ ) );
            a.add( e );
        }
        return new GenMatrix<C>(ring,a);
    }


    /**
     * Negative of this vector.
     * @return -this
     */
    public GenMatrix<C> negate() {
        ArrayList<C> a = new ArrayList<C>( ring.cols );
        for ( C c : val ) {
            C e = c.negate();
            a.add( e );
        }
        return new GenMatrix<C>(ring,a);
    }


    /**
     * Absolute value of this vector.
     * @return abs(this)
     */
    public GenMatrix<C> abs() {
        if ( signum() < 0 ) { 
           return negate();
        } else {
           return this;
        }
    }


    /**
     * Product of this vector with scalar.
     * @return this*s
     */
    public GenMatrix<C> scalarMultiply(C s) {
        ArrayList<C> a = new ArrayList<C>( ring.cols );
        for ( C c : val ) {
            C e = c.multiply( s );
            a.add( e );
        }
        return new GenMatrix<C>(ring,a);
    }


    /**
     * Left product of this vector with scalar.
     * @return s*this
     */
    public GenMatrix<C> leftScalarMultiply(C s) {
        ArrayList<C> a = new ArrayList<C>( ring.cols );
        for ( C c : val ) {
            C e = s.multiply( c );
            a.add( e );
        }
        return new GenMatrix<C>(ring,a);
    }


    /**
     * Linear compination of this vector with 
     * scalar multiple of other vector.
     * @return this*s+b*t
     */
    public GenMatrix<C> linearCombination(C s, GenMatrix<C> b, C t) {
        List<C> oval = b.val;
        ArrayList<C> a = new ArrayList<C>( ring.cols );
        int i = 0;
        for ( C c : val ) {
            C c1 = c.multiply(s);
            C c2 = oval.get( i++ ).multiply( t );
            C e = c1.sum( c2 );
            a.add( e );
        }
        return new GenMatrix<C>(ring,a);
    }


    /**
     * Linear compination of this vector with 
     * scalar multiple of other vector.
     * @return this+b*t
     */
    public GenMatrix<C> linearCombination(GenMatrix<C> b, C t) {
        List<C> oval = b.val;
        ArrayList<C> a = new ArrayList<C>( ring.cols );
        int i = 0;
        for ( C c : val ) {
            C c2 = oval.get( i++ ).multiply( t );
            C e = c.sum( c2 );
            a.add( e );
        }
        return new GenMatrix<C>(ring,a);
    }


    /**
     * Left linear compination of this vector with 
     * scalar multiple of other vector.
     * @return this+t*b
     */
    public GenMatrix<C> linearCombination(C t, GenMatrix<C> b) {
        List<C> oval = b.val;
        ArrayList<C> a = new ArrayList<C>( ring.cols );
        int i = 0;
        for ( C c : val ) {
            C c2 = t.multiply( oval.get( i++ ) );
            C e = c.sum( c2 );
            a.add( e );
        }
        return new GenMatrix<C>(ring,a);
    }


    /**
     * left linear compination of this vector with 
     * scalar multiple of other vector.
     * @return s*this+t*b
     */
    public GenMatrix<C> leftLinearCombination(C s, C t, 
                                              GenMatrix<C> b) {
        List<C> oval = b.val;
        ArrayList<C> a = new ArrayList<C>( ring.cols );
        int i = 0;
        for ( C c : val ) {
            C c1 = s.multiply(c);
            C c2 = t.multiply( oval.get( i++ ) );
            C e = c1.sum( c2 );
            a.add( e );
        }
        return new GenMatrix<C>(ring,a);
    }



    /**
     * scalar / dot product of this vector with other vector.
     * @return this . b
     */
    public C scalarProduct(GenMatrix<C> b) {
        C a = ring.coFac.getZERO();
        List<C> oval = b.val;
        int i = 0;
        for ( C c : val ) {
            C c2 = c.multiply( oval.get( i++ ) );
            a = a.sum( c2 );
        }
        return a;
    }


    /**
     * scalar / dot product of this vector with list of other vectors.
     * @return this * b
     */
    public GenMatrix<C> scalarProduct(List<GenMatrix<C>> B) {
        GenMatrix<C> A = ring.getZERO();
        int i = 0;
        for ( C c : val ) {
            GenMatrix<C> b = B.get( i++ );
            GenMatrix<C> a = b.leftScalarMultiply( c );
            A = A.sum( a );
        }
        return A;
    }


    /**
     * right scalar / dot product of this vector with list 
     * of other vectors.
     * @return b * this
     */
    public GenMatrix<C> rightScalarProduct(List<GenMatrix<C>> B) {
        GenMatrix<C> A = ring.getZERO();
        int i = 0;
        for ( C c : val ) {
            GenMatrix<C> b = B.get( i++ );
            GenMatrix<C> a = b.scalarMultiply( c );
            A = A.sum( a );
        }
        return A;
    }


    /**
     * Multiply this with S.
     * @param S
     * @return this * S.
     */
    public GenMatrix<C> multiply(GenMatrix<C> S) {
        throw new RuntimeException("multiply not implemented");
        //return ZERO;
    }


    /**
     * Divide this by S.
     * @param S
     * @return this / S.
     */
    public GenMatrix<C> divide(GenMatrix<C> S) {
        throw new RuntimeException("divide not jet implemented");
        //return ZERO;
    }


    /**
     * Remainder after division of this by S.
     * @param S
     * @return this - (this / S) * S.
     */
    public GenMatrix<C> remainder(GenMatrix<C> S) {
        throw new RuntimeException("remainder not implemented");
        //return ZERO;
    }


    /**
     * Inverse of this.
     * @return x with this * x = 1, if it exists.
     */
    public GenMatrix<C> inverse() {
        throw new RuntimeException("inverse not jet implemented");
        //return ZERO;
    }


    /**
     * Greatest common divisor.
     * @param b other element.
     * @return gcd(this,b).
     */
    public GenMatrix<C> gcd(GenMatrix<C> b) {
        throw new RuntimeException("gcd not implemented");
        //return ZERO;
    }


    /**
     * Extended greatest common divisor.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public GenMatrix<C>[] egcd(GenMatrix<C> b) {
        throw new RuntimeException("egcd not implemented");
        //return ZERO;
    }


}
