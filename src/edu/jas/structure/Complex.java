/*
 * $Id$
 */

package edu.jas.structure;

import java.math.BigInteger;
import java.util.Random;
import java.io.Reader;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.StarRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.util.StringUtil;


/**
 * Generic Complex class implementing the RingElem interface.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class Complex<C extends RingElem<C> >
             implements StarRingElem<Complex<C>>,
                        GcdRingElem<Complex<C>> {

    /** Complex class factory data structure. 
     */
    protected final ComplexRing<C> ring;


    /** Real part of the data structure. 
      */
    protected final C re;


    /** Imaginary part of the data structure. 
      */
    protected final C im;

    private final static Random random = new Random();

    private static final Logger logger = Logger.getLogger(Complex.class);


    /** The constructor creates a Complex object 
     * from two C objects as real and imaginary part. 
     * @param ring factory for Complex objects.
     * @param r real part.
     * @param i imaginary part.
     */
    public Complex(ComplexRing<C> ring, C r, C i) {
        this.ring = ring;
        re = r;
        im = i;
    }


    /** The constructor creates a Complex object 
     * from a C object as real part, 
     * the imaginary part is set to 0. 
     * @param r real part.
     */
    public Complex(ComplexRing<C> ring, C r) {
        this(ring, r, ring.ring.getZERO());
    }


    /** The constructor creates a Complex object 
     * from a long element as real part, 
     * the imaginary part is set to 0. 
     * @param r real part.
     */
    public Complex(ComplexRing<C> ring, long r) {
        this(ring, ring.ring.fromInteger(r));
    }


    /** The constructor creates a Complex object 
     * with real part 0 and imaginary part 0. 
     */
    public Complex(ComplexRing<C> ring) {
        this(ring, ring.ring.getZERO());
    }


    /** The constructor creates a Complex object 
     * from a String representation.
     * @param s string of a Complex.
     * @throws NumberFormatException
     */
    public Complex(ComplexRing<C> ring, String s) throws NumberFormatException {
        this.ring = ring;
        if ( s == null || s.length() == 0) {
            re = ring.ring.getZERO();
            im = ring.ring.getZERO();
            return;
        } 
        s = s.trim();
        int i = s.indexOf("i");
        if ( i < 0 ) {
           re = ring.ring.parse( s );
           im = ring.ring.getZERO();
           return;
        }
        //logger.warn("String constructor not done");
        String sr = "";
        if ( i > 0 ) {
            sr = s.substring(0,i);
        }
        String si = "";
        if ( i < s.length() ) {
            si = s.substring(i+1,s.length());
        }
        //int j = sr.indexOf("+");
        re = ring.ring.parse( sr.trim() );
        im = ring.ring.parse( si.trim() );
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public ComplexRing<C> factory() {
        return ring;
    }


    /** Get the real part. 
     * @return re.
     */
    public C getRe() { return re; }


    /** Get the imaginary part. 
     * @return im.
     */
    public C getIm() { return im; }


    /** Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public Complex<C> clone() {
        return new Complex<C>( ring, re, im );
    }


    /** Get the String representation.
     */
    @Override
    public String toString() {
        String s = re.toString();
        //logger.info("compareTo "+im+" ? 0 = "+i);
        if ( im.isZERO() ) {
            return s;
        }
        s += "i" + im;
        return s;
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer();
        if ( im.isZERO() ) {
            s.append(re.toScript());
        } else {
            s.append("(");
            s.append(re.toScript());
            s.append(",").append(im.toScript());
            s.append(")");
        }
        return s.toString();
    }


    /** Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    @Override
    public String toScriptFactory() {
        // Python case
        return ring.toScript();
    }


   /** Is Complex<C> number zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return    re.isZERO()
               && im.isZERO();
    }


    /** Is Complex<C> number one.  
     * @return If this is 1 then true is returned, else false. 
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return    re.isONE()
               && im.isZERO();
    }


    /** Is Complex<C> imaginary one.  
     * @return If this is i then true is returned, else false. 
     */
    public boolean isIMAG() {
        return    re.isZERO()
               && im.isONE();
    }


    /** Is Complex<C> unit element.
     * @return If this is a unit then true is returned, else false. 
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if ( isZERO() ) {
            return false;
        }
        if ( ring.isField() ) {
            return true;
        }
        return norm().re.isUnit();
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if ( ! ( b instanceof Complex ) ) {
           return false;
        }
        Complex<C> bc = null;
        try {
            bc = (Complex<C>) b;
        } catch (ClassCastException e) {
        }
        if ( bc == null ) {
            return false;
        }
        if ( ! ring.equals(bc.ring) ) {
            return false;
        }
        return    re.equals( bc.re ) 
               && im.equals( bc.im );
    }


    /** Hash code for this Complex<C>.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * re.hashCode() + im.hashCode();
    }


    /** Since complex numbers are unordered, 
     * we use lexicographical order of re and im.
     * @return 0 if this is equal to b;
     *         1 if re > b.re, or re == b.re and im > b.im;
     *        -1 if re < b.re, or re == b.re and im < b.im
     */
    @Override
    public int compareTo(Complex<C> b) {
        int s = re.compareTo( b.re );
        if ( s != 0 ) { 
            return s;
        }
        return im.compareTo( b.im );
    }


    /** Since complex numbers are unordered, 
     * we use lexicographical order of re and im.
     * @return 0 if this is equal to 0;
     *         1 if re > 0, or re == 0 and im > 0;
     *        -1 if re < 0, or re == 0 and im < 0
     * @see edu.jas.structure.RingElem#signum()
     */
    public int signum() {
      int s = re.signum();
      if ( s != 0 ) {
          return s;
      }
      return im.signum();
    }


    /* arithmetic operations: +, -, -
     */

    /** Complex<C> number summation.  
     * @param B a Complex<C> number.
     * @return this+B.
     */
    public Complex<C> sum(Complex<C> B) {
        return new Complex<C>(ring, re.sum( B.re ), 
                                    im.sum( B.im ) );
    }


    /** Complex<C> number subtract.  
     * @param B a Complex<C> number.
     * @return this-B.
     */
    public Complex<C> subtract(Complex<C> B) {
        return new Complex<C>(ring, re.subtract( B.re ), 
                                    im.subtract( B.im ) );
    }


    /** Complex<C> number negative.  
     * @return -this. 
     * @see edu.jas.structure.RingElem#negate()
     */
    public Complex<C> negate() {
        return new Complex<C>(ring, re.negate(), 
                                    im.negate());
    }


    /* arithmetic operations: conjugate, absolut value 
     */

    /** Complex<C> number conjugate.  
     * @return the complex conjugate of this. 
     */
    public Complex<C> conjugate() {
        return new Complex<C>(ring, re, im.negate());
    }


    /** Complex<C> number norm.  
     * @see edu.jas.structure.StarRingElem#norm()
     * @return ||this||.
     */
    public Complex<C> norm() {
        // this.conjugate().multiply(this);
        C v = re.multiply(re);
        v = v.sum( im.multiply(im) );
        return new Complex<C>(ring, v );
    }


    /** Complex<C> number absolute value.  
     * @see edu.jas.structure.RingElem#abs()
     * @return |this|^2.
     * Note: The square root is not jet implemented.
     */
    public Complex<C> abs() {
        Complex<C> n = norm();
        logger.error("abs() square root missing");
        // n = n.sqrt();
        return n;
    }


    /* arithmetic operations: *, inverse, / 
     */


    /** Complex<C> number product.  
     * @param B is a complex number.  
     * @return this*B.
     */
    public Complex<C> multiply(Complex<C> B) {
        return new Complex<C>(ring,
               re.multiply(B.re).subtract(im.multiply(B.im)),
               re.multiply(B.im).sum(im.multiply(B.re)) );
    }


    /** Complex<C> number inverse.  
     * @return S with S*this = 1. 
     * @see edu.jas.structure.RingElem#inverse()
     */
    public Complex<C> inverse() {
        C a = norm().re.inverse();
        return new Complex<C>(ring, re.multiply(a), 
                                    im.multiply(a.negate()) ); 
    }


    /** Complex<C> number remainder.  
     * @param S is a complex number.  
     * @return 0. 
     */
    public Complex<C> remainder(Complex<C> S) {
        if ( S.isZERO() ) {
           throw new RuntimeException("division by zero");
        }
        return ring.getZERO();
    }


    /** Complex<C> number divide.
     * @param B is a complex number, non-zero.
     * @return this/B. 
     */
    public Complex<C> divide (Complex<C> B) {
        return this.multiply( B.inverse() );
    }


    /** Complex<C> number greatest common divisor.  
     * @param S Complex<C>.
     * @return gcd(this,S).
     */
    public Complex<C> gcd(Complex<C> S) {
        if ( S == null || S.isZERO() ) {
            return this;
        }
        if ( this.isZERO() ) {
            return S;
        }
        if ( ring.isField() ) {
            return ring.getONE();
        }
        return new Complex<C>(ring, re.gcd(S.re), im.gcd(S.im) ); 
    }


    /**
     * Complex<C> extended greatest common divisor.
     * @param S Complex<C>.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    @SuppressWarnings("unchecked")
    public Complex<C>[] egcd(Complex<C> S) {
        Complex<C>[] ret = (Complex<C>[]) new Complex[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if ( S == null || S.isZERO() ) {
            ret[0] = this;
            return ret;
        }
        if ( this.isZERO() ) {
            ret[0] = S;
            return ret;
        }
        if ( ring.isField() ) {
            Complex<C> half = new Complex<C>(ring,ring.ring.fromInteger(1).divide(ring.ring.fromInteger(2)));
            ret[0] = ring.getONE();
            ret[1] = this.inverse().multiply(half);
            ret[2] = S.inverse().multiply(half);
        } else {
            C[] r = re.egcd(S.re);
            C[] i = im.egcd(S.im);
            ret[0] = new Complex<C>(ring,r[0],i[0]);
            ret[1] = new Complex<C>(ring,r[1],i[1]);
            ret[2] = new Complex<C>(ring,r[2],i[2]);
        }
        return ret;
    }

}
