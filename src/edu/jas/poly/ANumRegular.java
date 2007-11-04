/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RegularRingElem;

import edu.jas.kern.PrettyPrint;
import edu.jas.structure.NotInvertibleException;

import edu.jas.poly.GenPolynomial;


/**
 * Algebraic number class based on GenPolynomial with RingElem interface.
 * Special version as regular ring.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */

public class ANumRegular<C extends GcdRingElem<C> > 
             extends AlgebraicNumber<C> 
             implements GcdRingElem< AlgebraicNumber<C> > {


    /** Ring part of the data structure. 
     */
    //public final ANumRegularRing<C> ring; // shadow super.ring


    /** The constructor creates a AlgebraicNumber object 
     * from AlgebraicNumberRing modul and a GenPolynomial value. 
     * @param r ring AlgebraicNumberRing<C>.
     * @param a value GenPolynomial<C>.
     */
    public ANumRegular(ANumRegularRing<C> r, GenPolynomial<C> a) {
        super(r,a);
        //ring = r;
        isunit = 1; // regular ring
        if ( val.isZERO() ) {
           isunit = 0;
        } 
    }


    /** The constructor creates a AlgebraicNumber object 
     * from a GenPolynomial object module. 
     * @param r ring AlgebraicNumberRing<C>.
     */
    public ANumRegular(ANumRegularRing<C> r) {
        this( r, r.ring.getZERO() );
    }


    /** Is AlgebraicNumber unit. 
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
        // known
        return true;
    }


    /** AlgebraicNumber inverse.  
     * @see edu.jas.structure.RingElem#inverse()
     * @throws NotInvertibleException if the element is not invertible.
     * @return S with S = 1/this if defined. 
     */
    @Override
    public AlgebraicNumber<C> inverse() {
        if ( val.degree() > 1 ) { // cannot happen
           throw new RuntimeException("invalid value " + val);
        }
        //System.out.println("ANumRegular.inverse() " + this);
        GenPolynomialRing<C> mring = ring.ring;
	try {
            C a;
            if ( val.degree() == 1 ) {
               a = val.leadingBaseCoefficient();
            } else {
               a = mring.coFac.getZERO();
            }
            C b = val.trailingBaseCoefficient();
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);
            C c = a.sum(b);
            if ( !c.isZERO() ) {
               c = c.inverse();
            } // else c remains 0
            C d = b;
            if ( !d.isZERO() ) {
               d = d.inverse();
            } // else d remains 0
            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            c = c.subtract(d);
            GenPolynomial<C> v = mring.univariate(0,1);
            v = v.multiply(c);
            v = v.sum( mring.getONE().multiply(d) );
            return new AlgebraicNumber<C>( ring, v );
	} catch (NotInvertibleException e) { // cannot happen
	    throw new NotInvertibleException(e.getCause());
	}
    }

}
