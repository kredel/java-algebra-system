/*
 * $Id$
 */

package edu.jas.application;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.PrettyPrint;
import edu.jas.structure.RingElem;


/**
 * Local class element based on GenPolynomial with RingElem interface.
 * Objects of this class are (nearly) immutable.
 * @author Heinz Kredel
 */
public class Local<C extends RingElem<C> > 
             implements RingElem< Local<C> > {


    /** Local class factory data structure. 
     */
    protected final LocalRing<C> ring;


    /** Value part of the element data structure. 
     */
    protected final GenPolynomial<C> val;


    /** Flag to remember if this residue element is a unit.
     * -1 is unknown, 1 is unit, 0 not a unit.
     */
    protected int isunit = -1; // initially unknown


    /** The constructor creates a Local object 
     * from a ring factory. 
     * @param r ring factory.
     */
    public Local(LocalRing<C> r) {
        this( r, r.ring.getZERO(), 0 );
    }


    /** The constructor creates a Local object 
     * from a ring factory and a polynomial list. 
     * @param r ring factory.
     * @param a polynomial list.
     */
    public Local(LocalRing<C> r, GenPolynomial<C> a) {
        this( r, a, -1 );
    }


    /** The constructor creates a Local object 
     * from a ring factory, a polynomial list and an indicator if a is a unit. 
     * @param r ring factory.
     * @param a polynomial list.
     * @param u isunit indicator, -1, 0, 1.
     */
    public Local(LocalRing<C> r, GenPolynomial<C> a, int u) {
        ring = r;
        val = ring.ideal.normalform( a ); //.monic() no go
        switch ( u ) {
        case 0:  isunit = u;
                 break;
        case 1:  isunit = u;
                 break;
        default: isunit = -1;
        }
        if ( val.isONE() ) {
           isunit = 1;
        }
    }


    /**  Clone this.
     * @see java.lang.Object#clone()
     */
    public Local<C> clone() {
        return new Local<C>( ring, val );
    }
   

    /** Is Local zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return val.equals( ring.ring.getZERO() );
    }


    /** Is Local one. 
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return val.equals( ring.ring.getONE() );
    }


    /** Is Local unit. 
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if ( isunit > 0 ) {
            return true;
        } 
        if ( isunit == 0 ) {
            return false;
        } 
        // not jet known
        boolean u = ring.ideal.isUnit( val );
        if ( u ) {
           isunit = 1;
        } else {
           isunit = 0;
        }
        return ( u );
    }


    /** Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if ( PrettyPrint.isTrue() ) {
           return val.toString( ring.ring.getVars() );
        } else {
           return "Local[ " + val.toString() 
                   + " mod " + ring.toString() + " ]";
        }
    }


    /** Local comparison.  
     * @param b Local.
     * @return sign(this-b).
     */
    public int compareTo(Local<C> b) {
        GenPolynomial<C> v = b.val;
        if ( ! ring.equals( b.ring ) ) {
           v = ring.ideal.normalform( v );
        }
        return val.compareTo( v );
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked") // not jet working
    @Override
    public boolean equals(Object b) {
        if ( ! ( b instanceof Local ) ) {
           return false;
        }
        Local<C> a = null;
        try {
            a = (Local<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        return ( 0 == compareTo( a ) );
    }


    /** Local absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public Local<C> abs() {
        return new Local<C>( ring, val.abs() );
    }


    /** Local addition.
     * @param S Local.
     * @return this+S.
     */
    public Local<C> add(Local<C> S) {
        return new Local<C>( ring, val.add( S.val ) );
    }


    /** Local negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Local<C> negate() {
        return new Local<C>( ring, val.negate() );
    }


    /** Local signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return val.signum();
    }


    /** Local subtraction.
     * @param S Local.
     * @return this-S.
     */
    public Local<C> subtract(Local<C> S) {
        return new Local<C>( ring, val.subtract( S.val ) );
    }


    /** Local division.
     * @param S Local.
     * @return this/S.
     */
    public Local<C> divide(Local<C> S) {
        return multiply( S.inverse() );
    }


    /** Local inverse.  
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this if defined. 
     */
    public Local<C> inverse() {
        throw new RuntimeException("inverse not implemented");
        //GenPolynomial<C> x = ring.ideal.inverse( val );
        //return new Local<C>( ring, x );
    }


    /** Local remainder.
     * @param S Local.
     * @return this - (this/S)*S.
     */
    public Local<C> remainder(Local<C> S) {
        GenPolynomial<C> x = val.remainder( S.val );
        return new Local<C>( ring, x );
    }


    /** Local multiplication.
     * @param S Local.
     * @return this*S.
     */
    public Local<C> multiply(Local<C> S) {
        GenPolynomial<C> x = val.multiply( S.val );
        return new Local<C>( ring, x );
    }

 
    /** Local monic.
     * @return this with monic value part.
     */
    public Local<C> monic() {
        return new Local<C>( ring, val.monic() );
    }

}
