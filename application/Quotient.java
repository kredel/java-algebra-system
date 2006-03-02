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
 * Quotient class element based on GenPolynomial with RingElem interface.
 * Objects of this class are immutable.
 * <b>Note:</b> Just for fun, reduction to lowest terms is not efficient.
 * @author Heinz Kredel
 */
public class Quotient<C extends RingElem<C> > 
             implements RingElem< Quotient<C> > {

    private static Logger logger = Logger.getLogger(Quotient.class);
    private boolean debug = logger.isDebugEnabled();


    /** Quotient class factory data structure. 
     */
    public final QuotientRing<C> ring;


    /** Numerator part of the element data structure. 
     */
    public final GenPolynomial<C> num;


    /** Denominator part of the element data structure. 
     */
    public final GenPolynomial<C> den;


    /** The constructor creates a Quotient object 
     * from a ring factory. 
     * @param r ring factory.
     */
    public Quotient(QuotientRing<C> r) {
        this( r, r.ring.getZERO() );
    }


    /** The constructor creates a Quotient object 
     * from a ring factory and a numerator polynomial. 
     * The denominator is assumed to be 1.
     * @param r ring factory.
     * @param n numerator polynomial.
     */
    public Quotient(QuotientRing<C> r, GenPolynomial<C> n) {
        this( r, n, r.ring.getONE(), true );
    }


    /** The constructor creates a Quotient object 
     * from a ring factory and a numerator and denominator polynomial. 
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     */
    public Quotient(QuotientRing<C> r, 
                    GenPolynomial<C> n, GenPolynomial<C> d) {
        this(r,n,d,false);
    }


    /** The constructor creates a Quotient object 
     * from a ring factory and a numerator and denominator polynomial. 
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     * @param isred true if gcd(n,d) == 1, else false.
     */
    protected Quotient(QuotientRing<C> r, 
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
    public Quotient<C> clone() {
        return new Quotient<C>( ring, num, den, true );
    }
   

    /** Is Quotient zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return num.isZERO();
    }


    /** Is Quotient one. 
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return num.equals( den );
    }


    /** Is Quotient unit. 
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
           return "Quotient[ " + num.toString() 
                   + " / " + den.toString() + " ]";
        }
    }


    /** Quotient comparison.  
     * @param b Quotient.
     * @return sign(this-b).
     */
    public int compareTo(Quotient<C> b) {
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
        if ( ! ( b instanceof Quotient ) ) {
           return false;
        }
        Quotient<C> a = null;
        try {
            a = (Quotient<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        return ( 0 == compareTo( a ) );
    }


    /** Quotient absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public Quotient<C> abs() {
        return new Quotient<C>( ring, num.abs(), den, true );
    }


    /** Quotient addition.
     * @param S Quotient.
     * @return this+S.
     */
    public Quotient<C> add(Quotient<C> S) {
        if ( S == null || S.isZERO() ) {
           return this;
        }
        GenPolynomial<C> n = num.multiply( S.den );
        n = n.add( den.multiply( S.num ) ); 
        GenPolynomial<C> d = den.multiply( S.den );
        return new Quotient<C>( ring, n, d, false );
    }


    /** Quotient negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Quotient<C> negate() {
        return new Quotient<C>( ring, num.negate(), den, true );
    }


    /** Quotient signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return num.signum();
    }


    /** Quotient subtraction.
     * @param S Quotient.
     * @return this-S.
     */
    public Quotient<C> subtract(Quotient<C> S) {
        if ( S == null || S.isZERO() ) {
           return this;
        }
        GenPolynomial<C> n = num.multiply( S.den );
        n = n.subtract( den.multiply( S.num ) ); 
        GenPolynomial<C> d = den.multiply( S.den );
        return new Quotient<C>( ring, n, d, false );
    }


    /** Quotient division.
     * @param S Quotient.
     * @return this/S.
     */
    public Quotient<C> divide(Quotient<C> S) {
        return multiply( S.inverse() );
    }


    /** Quotient inverse.  
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this. 
     */
    public Quotient<C> inverse() {
        return new Quotient<C>( ring, den, num, true );
    }


    /** Quotient remainder.
     * @param S Quotient.
     * @return this - (this/S)*S.
     */
    public Quotient<C> remainder(Quotient<C> S) {
        if ( num.isZERO() ) {
           throw new RuntimeException("element not invertible " + this);
        }
        return ring.getZERO();
    }


    /** Quotient multiplication.
     * @param S Quotient.
     * @return this*S.
     */
    public Quotient<C> multiply(Quotient<C> S) {
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
        return new Quotient<C>( ring, n, d, false );
    }

 
    /** Quotient monic.
     * @return this with monic value part.
     */
    public Quotient<C> monic() {
        if ( num.isZERO() ) {
           return this;
        }
        C lbc = num.leadingBaseCoefficient();
        lbc = lbc.inverse();
        GenPolynomial<C> n = num.multiply( lbc );
        GenPolynomial<C> d = den.multiply( lbc );
        return new Quotient<C>( ring, n, d, true );
    }

}
