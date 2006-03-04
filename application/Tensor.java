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
 * @author Heinz Kredel
 */
public class Tensor<C extends RingElem<C> > 
             implements RingElem< Tensor<C> > {

    private static Logger logger = Logger.getLogger(Tensor.class);
    private boolean debug = logger.isDebugEnabled();


    /** Tensor class factory data structure. 
     */
    public final TensorRing<C> ring;


    /** Coefficient part of the element data structure. 
     */
    public final C coeff;


    /** First part of the element data structure. 
     */
    public final GenPolynomial<C> pol1;


    /** Second part of the element data structure. 
     */
    public final GenPolynomial<C> pol2;


    /** The constructor creates a Tensor object 
     * from a ring factory. 
     * @param r ring factory.
     */
    public Tensor(TensorRing<C> r) {
        this( r, r.ring1.getZERO(), r.ring2.getZERO() );
    }


    /** The constructor creates a Tensor object 
     * from a ring factory and two polynomials. 
     * @param r ring factory.
     * @param p1 first polynomial.
     * @param p2 second polynomial.
     */
    public Tensor(TensorRing<C> r, 
                  GenPolynomial<C> p1, GenPolynomial<C> p2) {
        this(r,null,p1,p2);
    }


    /** The constructor creates a Tensor object 
     * from a ring factory and two polynomials. 
     * @param r ring factory.
     * @param c a coefficent, c == null, if not known
     * @param p1 first polynomial.
     * @param p2 second polynomial.
     */
    protected Tensor(TensorRing<C> r, 
                     C c,
                     GenPolynomial<C> p1, 
                     GenPolynomial<C> p2) {
        ring = r;
        if ( c != null ) {
           coeff = c;
           pol1 = p1;
           pol2 = p2;
           return;
        }
        // must construct coefficient
        c = ring.ring1.coFac.getZERO();
        //= ring.ring2.coFac.getZERO();
        C c1 = p1.leadingBaseCoefficient();
        if ( c1 != null && c1.isUnit() ) {
           p1 = p1.multiply( c1.inverse() );
           c = c1;
        }
        C c2 = p2.leadingBaseCoefficient();
        if ( c2 != null && c2.isUnit() ) {
           p2 = p2.multiply( c2.inverse() );
           c = c.multiply( c2 );
        }
        if ( true || debug ) {
           logger.info("c  = " + c);
           logger.info("p1 = " + p1);
           logger.info("p2 = " + p2);
        }
        if ( c.isZERO() ) { 
           p1 = ring.ring1.getZERO(); 
           p2 = ring.ring2.getZERO(); 
        }
        coeff = c;
        pol1 = p1;
        pol2 = p2;
    }


    /**  Clone this.
     * @see java.lang.Object#clone()
     */
    public Tensor<C> clone() {
        return new Tensor<C>( ring, coeff, pol1, pol2 );
    }
   

    /** Is Tensor zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return coeff.isZERO();
    }


    /** Is Tensor one. 
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return coeff.isONE() && pol1.isONE() && pol2.isONE();
    }


    /** Is Tensor unit. 
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if ( coeff.isZERO() ) {
           return false;
        }
        return coeff.isUnit() && pol1.isUnit() && pol2.isUnit();
    }


    /** Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if ( PrettyPrint.isTrue() ) {
           return coeff.toString() + " * " 
                  + pol1.toString( ring.ring1.getVars() ) 
                  + " {*} "
                  + pol2.toString( ring.ring2.getVars() );
        } else {
           return "Tensor[ " 
                  + coeff.toString() + ", " 
                  + pol1.toString() 
                  + ", "
                  + pol2.toString()
                  + " ]";
        }
    }


    /** Tensor comparison.  
     * @param b Tensor.
     * @return sign(this-b).
     */
    public int compareTo(Tensor<C> b) {
        if ( b == null || b.isZERO() ) {
           return coeff.signum();
        }
        C c = coeff.subtract( b.coeff );
        return c.signum();
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
        return ( 0 == this.compareTo( a ) );
    }


    /** Tensor absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public Tensor<C> abs() {
        return new Tensor<C>( ring, coeff.abs(), pol1, pol2 );
    }


    /** Tensor addition.
     * @param S Tensor.
     * @return this+S.
     */
    public Tensor<C> add(Tensor<C> S) {
        if ( S == null || S.isZERO() ) {
           return this;
        }
        if ( this.isZERO() ) {
           return S;
        }
        GenPolynomial<C> p1 =   pol1.multiply( coeff );
        GenPolynomial<C> s  = S.pol1.multiply( S.coeff );
                         p1 = p1.add( s );
        GenPolynomial<C> p2 = pol2.add( S.pol2 );
        return new Tensor<C>( ring, p1, p2 );
    }


    /** Tensor negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Tensor<C> negate() {
        return new Tensor<C>( ring, coeff.negate(), pol1, pol2 );
    }


    /** Tensor signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return coeff.signum();
    }


    /** Tensor subtraction.
     * @param S Tensor.
     * @return this-S.
     */
    public Tensor<C> subtract(Tensor<C> S) {
        if ( S == null || S.isZERO() ) {
           return this;
        }
        if ( this.isZERO() ) {
           return S.negate();
        }
        GenPolynomial<C> p1 =   pol1.multiply( coeff );
        GenPolynomial<C> s  = S.pol1.multiply( S.coeff );
                         p1 = p1.subtract( s );
        GenPolynomial<C> p2 = pol2.subtract( S.pol2 );
        return new Tensor<C>( ring, p1, p2 );
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
        return new Tensor<C>( ring, 
                              coeff.inverse(), 
                              pol1.inverse(), 
                              pol2.inverse() );
    }


    /** Tensor remainder.
     * @param S Tensor.
     * @return this - (this/S)*S.
     */
    public Tensor<C> remainder(Tensor<C> S) {
        if ( S == null || S.isZERO() ) {
           throw new RuntimeException("division by zero " + S);
        }
        if ( this.isZERO() ) {
           return this;
        }
        GenPolynomial<C> p1 =   pol1.multiply( coeff );
        GenPolynomial<C> s  = S.pol1.multiply( S.coeff );
                         p1 = p1.remainder( s );
        GenPolynomial<C> p2 =   pol2.remainder( S.pol2 );
        return new Tensor<C>( ring, p1, p2 );
    }


    /** Tensor multiplication.
     * @param S Tensor.
     * @return this*S.
     */
    public Tensor<C> multiply(Tensor<C> S) {
        if ( S == null || S.isZERO() ) {
           return S;
        }
        if ( coeff.isZERO() ) {
           return this;
        }
        if ( S.isONE() ) {
           return this;
        }
        if ( this.isONE() ) {
           return S;
        }
        C c = coeff.multiply( S.coeff );
        GenPolynomial<C> p1 = pol1.multiply( S.pol1 );
        GenPolynomial<C> p2 = pol2.multiply( S.pol2 );
        return new Tensor<C>( ring, c, p1, p2 );
    }

 
    /** Tensor monic.
     * @return this with monic value part.
     */
    public Tensor<C> monic() {
        if ( coeff.isZERO() ) {
           return this;
        }
        C c = ring.ring1.coFac.getONE();
        return new Tensor<C>( ring, c, pol1, pol2 );
    }

}
