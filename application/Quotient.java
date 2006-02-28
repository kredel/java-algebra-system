/*
 * $Id$
 */

package edu.jas.application;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.PrettyPrint;
import edu.jas.structure.RingElem;


/**
 * Quotient class element based on GenPolynomial with RingElem interface.
 * Objects of this class are (nearly) immutable.
 * @author Heinz Kredel
 */
public class Quotient<C extends RingElem<C> > 
             implements RingElem< Quotient<C> > {


    /** Quotient class factory data structure. 
     */
    protected final QuotientRing<C> ring;


    /** Numerator part of the element data structure. 
     */
    protected final GenPolynomial<C> num;


    /** Denominator part of the element data structure. 
     */
    protected final GenPolynomial<C> den;


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
        this( r, n, r.ring.getONE() );
    }


    /** The constructor creates a Quotient object 
     * from a ring factory and a numerator and denominator polynomial. 
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     */
    public Quotient(QuotientRing<C> r, 
                    GenPolynomial<C> n, GenPolynomial<C> d) {
        ring = r;
        // must reduce to lowest terms
        if ( d == null || d.isZERO() ) {
           throw new RuntimeException("denominator may not be zero");
        }
        if ( d.signum() >= 0 ) {
           num = n;
           den = d;
        } else {
           num = n.negate();
           den = d.negate();
        }
    }


    /**  Clone this.
     * @see java.lang.Object#clone()
     */
    public Quotient<C> clone() {
        return new Quotient<C>( ring, num, den );
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
                  + "/" + den.toString( ring.ring.getVars() );
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
        Quotient<C> s = this.subtract( b );
        return s.signum();
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
        return new Quotient<C>( ring, num.abs(), den );
    }


    /** Quotient addition.
     * @param S Quotient.
     * @return this+S.
     */
    public Quotient<C> add(Quotient<C> S) {
        if ( S == null || S.isZERO() ) {
           return this;
        }
        GenPolynomial<C> d = den.multiply( S.den );
        GenPolynomial<C> n = num.multiply( S.den );
        n.add( den.multiply( S.num ) ); 
        return new Quotient<C>( ring, n, d );
    }


    /** Quotient negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Quotient<C> negate() {
        return new Quotient<C>( ring, num.negate(), den );
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
        GenPolynomial<C> d = den.multiply( S.den );
        GenPolynomial<C> n = num.multiply( S.den );
        n.subtract( den.multiply( S.num ) ); 
        return new Quotient<C>( ring, n, d );
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
        return new Quotient<C>( ring, den, num );
    }


    /** Quotient remainder.
     * @param S Quotient.
     * @return this - (this/S)*S.
     */
    public Quotient<C> remainder(Quotient<C> S) {
        if ( this.isZERO() ) {

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
        GenPolynomial<C> d = den.multiply( S.den );
        GenPolynomial<C> n = num.multiply( S.num );
        return new Quotient<C>( ring, n, d );

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
        return new Quotient<C>( ring, n, d );
    }

}
