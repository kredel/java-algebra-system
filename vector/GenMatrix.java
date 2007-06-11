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
 * GenMatrix generic matrix implementing RingElem.
 * matrixs of n columns over C.
 * @author Heinz Kredel
 */

public class GenMatrix<C extends RingElem<C> > 
    implements AlgebraElem<GenMatrix<C>,C> {

    private static Logger logger = Logger.getLogger(GenMatrix.class);

    public final GenMatrixRing< C > ring;
    public final List<C> val; //remove 
    public final ArrayList<ArrayList<C>> matrix;
    private int hashValue = 0;


    /**
     * Constructors for GenMatrix.
     */

    public GenMatrix(GenMatrixRing< C > r) {
        this( r, r.getZERO().matrix );
    }


    protected GenMatrix(GenMatrixRing< C > r, ArrayList<ArrayList<C>> m) {
        ring = r;
        matrix = m;
        val = null;
    }


    /**
     * toString method.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        boolean firstRow = true;
        for ( List<C> val : matrix ) {
            s.append("( ");
            if ( firstRow ) {
                 firstRow = false;
            } else {
                 s.append(", ");
            }
            boolean first = true;
            for ( C c : val ) {
                if ( first ) {
                   first = false;
                } else {
                   s.append(", ");
                }
                s.append( c.toString() );
            }
            s.append(" ) ");
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
        return new GenMatrix<C>( ring, (ArrayList<ArrayList<C>>)matrix.clone() );
    }


    /**
     * test if this is equal to a zero matrix.
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
        GenMatrix om = (GenMatrix)other;
        if ( ! ring.equals(om.ring) ) {
            return false;
        }
        if ( ! matrix.equals(om.matrix) ) {
            return false;
        }
        return true;
    }


    /** Hash code for this GenMatrix.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        if ( hashValue == 0 ) {
           hashValue = 37 * matrix.hashCode() + ring.hashCode();
           if ( hashValue == 0 ) {
              hashValue = 1;
           }
        }
        return hashValue;
    }


    /**
     * compareTo, lexicogaphical comparison.
     * @param b other
     * @return 1 if (this &lt; b), 0 if (this == b) or -1 if (this &gt; b).
     */
    public int compareTo(GenMatrix<C> b) {
        if ( ! ring.equals( b.ring ) ) {
            return -1;
        }
        ArrayList<ArrayList<C>> om = b.matrix;
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> ov = om.get( i++ );
            int j = 0;
            for ( C c : val ) {
                int s = c.compareTo( ov.get( j++ ) );
                if ( s != 0 ) {
                    return s;
                }
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
     * sign of matrix.
     * @return 1 if (this &lt; 0), 0 if (this == 0) or -1 if (this &gt; 0).
     */
    public int signum() {
        return compareTo( ring.getZERO() );
    }


    /**
     * Sum of matrices.
     * @return this+b
     */
    public GenMatrix<C> sum(GenMatrix<C> b) {
        ArrayList<ArrayList<C>> om = b.matrix;
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> ov = om.get( i++ );
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            int j = 0;
            for ( C c : val ) {
                C e = c.sum( ov.get( j++ ) );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Difference of matrices.
     * @return this-b
     */
    public GenMatrix<C> subtract(GenMatrix<C> b) {
        ArrayList<ArrayList<C>> om = b.matrix;
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> ov = om.get( i++ );
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            int j = 0;
            for ( C c : val ) {
                C e = c.subtract( ov.get( j++ ) );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Negative of this matrix.
     * @return -this
     */
    public GenMatrix<C> negate() {
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            for ( C c : val ) {
                C e = c.negate();
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Absolute value of this matrix.
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
     * Product of this matrix with scalar.
     * @return this*s
     */
    public GenMatrix<C> scalarMultiply(C s) {
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            for ( C c : val ) {
                C e = c.multiply( s );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Left product of this matrix with scalar.
     * @return s*this
     */
    public GenMatrix<C> leftScalarMultiply(C s) {
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            for ( C c : val ) {
                C e = s.multiply( c );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Linear compination of this matrix with 
     * scalar multiple of other matrix.
     * @return this*s+b*t
     */
    public GenMatrix<C> linearCombination(C s, GenMatrix<C> b, C t) {
        ArrayList<ArrayList<C>> om = b.matrix;
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> ov = om.get( i++ );
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            int j = 0;
            for ( C c : val ) {
                C c1 = c.multiply(s);
                C c2 = ov.get( j++ ).multiply( t );
                C e = c1.sum( c2 );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Linear compination of this matrix with 
     * scalar multiple of other matrix.
     * @return this+b*t
     */
    public GenMatrix<C> linearCombination(GenMatrix<C> b, C t) {
        ArrayList<ArrayList<C>> om = b.matrix;
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> ov = om.get( i++ );
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            int j = 0;
            for ( C c : val ) {
                C c2 = ov.get( j++ ).multiply( t );
                C e = c.sum( c2 );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Left linear compination of this matrix with 
     * scalar multiple of other matrix.
     * @return this+t*b
     */
    public GenMatrix<C> linearCombination(C t, GenMatrix<C> b) {
        ArrayList<ArrayList<C>> om = b.matrix;
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> ov = om.get( i++ );
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            int j = 0;
            for ( C c : val ) {
                C c2 = t.multiply( ov.get( j++ ) );
                C e = c.sum( c2 );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * left linear compination of this matrix with 
     * scalar multiple of other matrix.
     * @return s*this+t*b
     */
    public GenMatrix<C> leftLinearCombination(C s, C t, 
                                              GenMatrix<C> b) {
        ArrayList<ArrayList<C>> om = b.matrix;
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> ov = om.get( i++ );
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            int j = 0;
            for ( C c : val ) {
                C c1 = s.multiply(c);
                C c2 = t.multiply( ov.get( j++ ) );
                C e = c1.sum( c2 );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
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
