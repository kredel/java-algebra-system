/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Random;
import java.io.Reader;

//import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;


/**
 * Algebraic number factory class based on GenPolynomial with RingElem 
 * interface.
 * Special version as regular ring.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */

public class ANumRegularRing<C extends GcdRingElem<C> > 
             extends AlgebraicNumberRing<C>
             implements RingFactory< AlgebraicNumber<C> >  {


    /** The constructor creates a AlgebraicNumber factory object 
     * from a GenPolynomial objects module. 
     * @param m module GenPolynomial<C>.
     */
    public ANumRegularRing(GenPolynomial<C> m) {
        super(m);
        if (    m.degree() != 2 
             || !m.leadingBaseCoefficient().isONE()
             || !m.reductum().leadingBaseCoefficient().negate().isONE()
             || m.numberOfVariables() != 1 ) {
           throw new RuntimeException("invalid modul " + m);
        }
        isField = 1; // regular ring
    }


    /** The constructor creates a AlgebraicNumber factory object 
     * from a GenPolynomial objects module. 
     * @param m module GenPolynomial<C>.
     * @param isField indicator if m is prime.
     */
    public ANumRegularRing(GenPolynomial<C> m, boolean isField) {
        this(m);
        this.isField = ( isField ? 1 :  0 );
    }


    /** Copy AlgebraicNumber element c.
     * @param c
     * @return a copy of c.
     */
    public AlgebraicNumber<C> copy(AlgebraicNumber<C> c) {
        return new ANumRegular<C>( this, c.val );
    }


    /** Get the zero element.
     * @return 0 as AlgebraicNumber.
     */
    public AlgebraicNumber<C> getZERO() {
        return new ANumRegular<C>( this, ring.getZERO() );
    }


    /**  Get the one element.
     * @return 1 as AlgebraicNumber.
     */
    public AlgebraicNumber<C> getONE() {
        return new ANumRegular<C>( this, ring.getONE() );
    }


    /** Get a AlgebraicNumber element from a BigInteger value.
     * @param a BigInteger.
     * @return a AlgebraicNumber.
     */
    public AlgebraicNumber<C> fromInteger(java.math.BigInteger a) {
        return new ANumRegular<C>( this, ring.fromInteger(a) );
    }


    /** Get a AlgebraicNumber element from a long value.
     * @param a long.
     * @return a AlgebraicNumber.
     */
    public AlgebraicNumber<C> fromInteger(long a) {
        return new ANumRegular<C>( this, ring.fromInteger(a) );
    }
    

    /** Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "ANumRegularRing[ " 
              + modul.toString() + " | isField="
              + isField + " :: "
              + ring.toString() + " ]";
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") 
    public boolean equals(Object b) {
        if ( ! ( b instanceof ANumRegularRing ) ) {
            return false;
        }
        ANumRegularRing<C> a = null;
        try {
            a = (ANumRegularRing<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        return modul.equals( a.modul );
    }


    /** AlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random integer mod modul.
     */
    public AlgebraicNumber<C> random(int n) {
        GenPolynomial<C> x = ring.random( n ).monic();
        return new ANumRegular<C>( this, x);
    }


    /** AlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random integer mod modul.
     */
    public AlgebraicNumber<C> random(int n, Random rnd) {
        GenPolynomial<C> x = ring.random( n, rnd ).monic();
        return new ANumRegular<C>( this, x);
    }


    /** Parse AlgebraicNumber from String.
     * @param s String.
     * @return AlgebraicNumber from s.
     */
    public AlgebraicNumber<C> parse(String s) {
        GenPolynomial<C> x = ring.parse( s );
        return new ANumRegular<C>( this, x );
    }


    /** Parse AlgebraicNumber from Reader.
     * @param r Reader.
     * @return next AlgebraicNumber from r.
     */
    public AlgebraicNumber<C> parse(Reader r) {
        GenPolynomial<C> x = ring.parse( r );
        return new ANumRegular<C>( this, x );
    }


    /** AlgebraicNumber chinese remainder algorithm.  
     * Assert deg(c.modul) >= deg(a.modul) 
     * and c.modul * a.modul = this.modul.
     * @param c AlgebraicNumber.
     * @param ci inverse of c.modul in ring of a.
     * @param a other AlgebraicNumber.
     * @return S, with S mod c.modul == c and S mod a.modul == a. 
     */
    public AlgebraicNumber<C>
           chineseRemainder(AlgebraicNumber<C> c, 
                            AlgebraicNumber<C> ci, 
                            ANumRegular<C> a) {
        if ( true ) { // debug
            if ( c.ring.modul.compareTo( a.ring.modul ) < 1 ) {
               System.out.println("AlgebraicNumber error " + c + ", " + a);
           }
        }
        ANumRegularRing<C> ar = (ANumRegularRing<C>)a.ring;
        AlgebraicNumber<C> b = new ANumRegular<C>( ar, c.val ); 
                              // c mod a.modul
                              // c( tbcf(a.modul) ) if deg(a.modul)==1
        AlgebraicNumber<C> d = a.subtract( b ); // a-c mod a.modul
        if ( d.isZERO() ) {
           return new ANumRegular<C>( this, c.val );
        }
        b = d.multiply( ci ); // b = (a-c)*ci mod a.modul
        // (c.modul*b)+c mod this.modul = c mod c.modul = 
        // (c.modul*ci*(a-c)+c) mod a.modul = a mod a.modul
        GenPolynomial<C> s = c.ring.modul.multiply( b.val );
        s = s.sum( c.val );
        return new ANumRegular<C>( this, s );
    }


    /** AlgebraicNumber interpolation algorithm.  
     * Assert deg(c.modul) >= deg(A.modul) 
     * and c.modul * A.modul = this.modul.
     * Special case with deg(A.modul) == 1.
     * Similar algorithm as chinese remainder algortihm.
     * @param c AlgebraicNumber.
     * @param ci inverse of (c.modul)(a) in ring of A.
     * @param am trailing base coefficient of modul of other AlgebraicNumber A.
     * @param a value of other AlgebraicNumber A.
     * @return S, with S(c) == c and S(A) == a.
     */
    public AlgebraicNumber<C>
           interpolate(AlgebraicNumber<C> c, 
                       C ci, 
                       C am,
                       C a) {
        C b = PolyUtil.<C>evaluateMain( ring.coFac /*a*/, c.val, am ); 
                              // c mod a.modul
                              // c( tbcf(a.modul) ) if deg(a.modul)==1
        C d = a.subtract( b ); // a-c mod a.modul
        if ( d.isZERO() ) {
           return new ANumRegular<C>( this, c.val );
        }
        b = d.multiply( ci ); // b = (a-c)*ci mod a.modul
        // (c.modul*b)+c mod this.modul = c mod c.modul = 
        // (c.modul*ci*(a-c)+c) mod a.modul = a mod a.modul
        GenPolynomial<C> s = c.ring.modul.multiply( b );
        s = s.sum( c.val );
        return new ANumRegular<C>( this, s );
    }


}
