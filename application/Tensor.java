/*
 * $Id$
 */

package edu.jas.application;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.PrettyPrint;
import edu.jas.structure.RingElem;


/**
 * Tensor class element based on GenPolynomial with RingElem interface.
 * Objects of this class are immutable.
 * <b>Note:</b> Just for fun, reduction to lowest terms is not efficient.
 * @author Heinz Kredel
 */
public class Tensor<C extends RingElem<C> > 
             implements RingElem< Tensor<C> > {

    private static Logger logger = Logger.getLogger(Tensor.class);
    private boolean debug = logger.isDebugEnabled();


    /** Tensor class factory data structure. 
     */
    public final TensorRing<C> ring;


    /** Numerator part of the element data structure. 
     */
    public final GenPolynomial<C> num;


    /** Denominator part of the element data structure. 
     */
    public final GenPolynomial<C> den;


    /** The constructor creates a Tensor object 
     * from a ring factory. 
     * @param r ring factory.
     */
    public Tensor(TensorRing<C> r) {
        this( r, r.ring.getZERO() );
    }


    /** The constructor creates a Tensor object 
     * from a ring factory and a numerator polynomial. 
     * The denominator is assumed to be 1.
     * @param r ring factory.
     * @param n numerator polynomial.
     */
    public Tensor(TensorRing<C> r, GenPolynomial<C> n) {
        this( r, n, r.ring.getONE(), true );
    }


    /** The constructor creates a Tensor object 
     * from a ring factory and a numerator and denominator polynomial. 
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     */
    public Tensor(TensorRing<C> r, 
                    GenPolynomial<C> n, GenPolynomial<C> d) {
        this(r,n,d,false);
    }


    /** The constructor creates a Tensor object 
     * from a ring factory and a numerator and denominator polynomial. 
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     * @param isred true if gcd(n,d) == 1, else false.
     */
    protected Tensor(TensorRing<C> r, 
                       GenPolynomial<C> n, GenPolynomial<C> d,
                       boolean isred) {
        if ( d == null || d.isZERO() ) {
           throw new RuntimeException("denominator may not be zero");
        }
        ring = r;
        if ( d.signum() < 0 ) {
           n = n.negate();
           d = d.negate();
        }
        if ( isred ) {
           num = n;
           den = d;
           return;
        }
        // must reduce to lowest terms
        GenPolynomial<C> gcd = gcd( n, d );
        if ( true || debug ) {
           logger.info("gcd = " + gcd);
        }
        //GenPolynomial<C> gcd = ring.ring.getONE();
        if ( gcd.isONE() ) {
           num = n;
           den = d;
        } else {
           num = n.divide( gcd );
           den = d.divide( gcd );
        }
    }


    /** Least common multiple.
     * Just for fun, is not efficient.
     * @param n first polynomial.
     * @param d second polynomial.
     * @return lcm(n,d)
     */
    protected GenPolynomial<C> lcm(GenPolynomial<C> n, GenPolynomial<C> d) {
        List<GenPolynomial<C>> list;
        list = new ArrayList<GenPolynomial<C>>( 1 );
        list.add( n );
        Ideal<C> N = new Ideal<C>( ring.ring, list, true );
        list = new ArrayList<GenPolynomial<C>>( 1 );
        list.add( d );
        Ideal<C> D = new Ideal<C>( ring.ring, list, true );
        Ideal<C> L = N.intersect( D );
        if ( L.list.list.size() != 1 ) {
           throw new RuntimeException("lcm not uniqe");
        }
        GenPolynomial<C> lcm = L.list.list.get(0);
        return lcm;
    }


    /** Greatest common divisor.
     * Just for fun, is not efficient.
     * @param n first polynomial.
     * @param d second polynomial.
     * @return gcd(n,d)
     */
    protected GenPolynomial<C> gcd(GenPolynomial<C> n, GenPolynomial<C> d) {
        if ( n.isZERO() ) {
           return d;
        }
        if ( d.isZERO() ) {
           return n;
        }
        if ( n.isONE() ) {
           return n;
        }
        if ( d.isONE() ) {
           return d;
        }
        GenPolynomial<C> p = n.multiply(d);
        GenPolynomial<C> lcm = lcm(n,d);
        GenPolynomial<C> gcd = p.divide(lcm);
        return gcd;
    }


    /**  Clone this.
     * @see java.lang.Object#clone()
     */
    public Tensor<C> clone() {
        return new Tensor<C>( ring, num, den, true );
    }
   

    /** Is Tensor zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return num.isZERO();
    }


    /** Is Tensor one. 
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return num.equals( den );
    }


    /** Is Tensor unit. 
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if ( num.isZERO() ) {
           return false;
        } else {
           return true;
        }
    }


    /** Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if ( PrettyPrint.isTrue() ) {
           return num.toString( ring.ring.getVars() ) 
                  + "///" + den.toString( ring.ring.getVars() );
        } else {
           return "Tensor[ " + num.toString() 
                   + " / " + den.toString() + " ]";
        }
    }


    /** Tensor comparison.  
     * @param b Tensor.
     * @return sign(this-b).
     */
    public int compareTo(Tensor<C> b) {
        if ( b == null || b.isZERO() ) {
            return this.signum();
        }
        GenPolynomial<C> r = num.multiply( b.den );
        GenPolynomial<C> s = den.multiply( b.num );
        GenPolynomial<C> x = r.subtract( s );
        return x.signum();
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked") // not jet working
    @Override
    public boolean equals(Object b) {
        if ( ! ( b instanceof Tensor ) ) {
           return false;
        }
        Tensor<C> a = null;
        try {
            a = (Tensor<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        return ( 0 == compareTo( a ) );
    }


    /** Tensor absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public Tensor<C> abs() {
        return new Tensor<C>( ring, num.abs(), den, true );
    }


    /** Tensor addition.
     * @param S Tensor.
     * @return this+S.
     */
    public Tensor<C> add(Tensor<C> S) {
        if ( S == null || S.isZERO() ) {
           return this;
        }
        GenPolynomial<C> n = num.multiply( S.den );
        n = n.add( den.multiply( S.num ) ); 
        GenPolynomial<C> d = den.multiply( S.den );
        return new Tensor<C>( ring, n, d, false );
    }


    /** Tensor negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Tensor<C> negate() {
        return new Tensor<C>( ring, num.negate(), den, true );
    }


    /** Tensor signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return num.signum();
    }


    /** Tensor subtraction.
     * @param S Tensor.
     * @return this-S.
     */
    public Tensor<C> subtract(Tensor<C> S) {
        if ( S == null || S.isZERO() ) {
           return this;
        }
        GenPolynomial<C> n = num.multiply( S.den );
        n = n.subtract( den.multiply( S.num ) ); 
        GenPolynomial<C> d = den.multiply( S.den );
        return new Tensor<C>( ring, n, d, false );
    }


    /** Tensor division.
     * @param S Tensor.
     * @return this/S.
     */
    public Tensor<C> divide(Tensor<C> S) {
        return multiply( S.inverse() );
    }


    /** Tensor inverse.  
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this. 
     */
    public Tensor<C> inverse() {
        return new Tensor<C>( ring, den, num, true );
    }


    /** Tensor remainder.
     * @param S Tensor.
     * @return this - (this/S)*S.
     */
    public Tensor<C> remainder(Tensor<C> S) {
        if ( num.isZERO() ) {
           throw new RuntimeException("element not invertible " + this);
        }
        return ring.getZERO();
    }


    /** Tensor multiplication.
     * @param S Tensor.
     * @return this*S.
     */
    public Tensor<C> multiply(Tensor<C> S) {
        if ( S == null || S.isZERO() ) {
           return S;
        }
        if ( num.isZERO() ) {
           return this;
        }
        if ( S.isONE() ) {
           return this;
        }
        if ( this.isONE() ) {
           return S;
        }
        GenPolynomial<C> n = num.multiply( S.num );
        GenPolynomial<C> d = den.multiply( S.den );
        return new Tensor<C>( ring, n, d, false );
    }

 
    /** Tensor monic.
     * @return this with monic value part.
     */
    public Tensor<C> monic() {
        if ( num.isZERO() ) {
           return this;
        }
        C lbc = num.leadingBaseCoefficient();
        lbc = lbc.inverse();
        GenPolynomial<C> n = num.multiply( lbc );
        GenPolynomial<C> d = den.multiply( lbc );
        return new Tensor<C>( ring, n, d, true );
    }

}
