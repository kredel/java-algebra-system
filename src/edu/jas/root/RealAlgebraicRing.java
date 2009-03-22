/*
 * $Id$
 */

package edu.jas.root;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.io.Reader;

//import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;


/**
 * Real algebraic number factory class based on GenPolynomial with RingElem 
 * interface.
 * Objects of this class are immutable with the exception of the isolating intervals.
 * @author Heinz Kredel
 */

public class RealAlgebraicRing<C extends GcdRingElem<C> > 
              extends AlgebraicNumberRing<C> {


    /** Isolating interval for a real root. 
     */
    public final Interval<C> root;


    /** The constructor creates a RealAlgebraicNumber factory object 
     * from a GenPolynomial objects module. 
     * @param m module GenPolynomial<C>.
     * @param root isolating interval for a real root.
     */
    public RealAlgebraicRing(GenPolynomial<C> m, Interval<C> root) {
        super(m);
        this.root = root;
    }


    /** The constructor creates a RealAlgebraicNumber factory object 
     * from a GenPolynomial objects module. 
     * @param m module GenPolynomial<C>.
     * @param root isolating interval for a real root.
     * @param isField indicator if m is prime.
     */
    public RealAlgebraicRing(GenPolynomial<C> m, Interval<C> root, boolean isField) {
        super(m,isField);
        this.root = root;
    }


    /** Get the module part. 
     * @return modul.
    public GenPolynomial<C> getModul() {
        return modul;
    }
     */


    /** Copy RealAlgebraicNumber element c.
     * @param c
     * @return a copy of c.
     */
    public RealAlgebraicNumber<C> copy(RealAlgebraicNumber<C> c) {
        return new RealAlgebraicNumber<C>( this, c.val );
    }


    /** Get the zero element.
     * @return 0 as RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> getZERO() {
        return new RealAlgebraicNumber<C>( this, ring.getZERO() );
    }


    /**  Get the one element.
     * @return 1 as RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> getONE() {
        return new RealAlgebraicNumber<C>( this, ring.getONE() );
    }

    
    /**  Get the generating element.
     * @return alpha as RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> getGenerator() {
        return new RealAlgebraicNumber<C>( this, ring.univariate(0) );
    }


    /**  Get the generating elements.
     * @return a list of generating elements for this ring.
     */
    public List<AlgebraicNumber<C>> getGenerators() {
        List<AlgebraicNumber<C>> gens = new ArrayList<AlgebraicNumber<C>>( 2 );
        gens.add( getONE() );
        gens.add( getGenerator() );
        return gens;
    }

    
    /** Get a RealAlgebraicNumber element from a BigInteger value.
     * @param a BigInteger.
     * @return a RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> fromInteger(java.math.BigInteger a) {
        return new RealAlgebraicNumber<C>( this, ring.fromInteger(a) );
    }


    /** Get a RealAlgebraicNumber element from a long value.
     * @param a long.
     * @return a RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> fromInteger(long a) {
        return new RealAlgebraicNumber<C>( this, ring.fromInteger(a) );
    }
    

    /** Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RealAlgebraicRing[ " 
              + modul.toString() 
              + " in " + root
              + " | isField="
              + isField + " :: "
              + ring.toString() + " ]";
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object b) {
        if ( ! ( b instanceof RealAlgebraicRing ) ) {
            return false;
        }
        RealAlgebraicRing<C> a = null;
        try {
            a = (RealAlgebraicRing<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        return modul.equals( a.modul ) && root.equals( a.root );
    }


    /** Hash code for this RealAlgebraicNumber.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = 37 * modul.hashCode() + root.hashCode();
        return 37 * h + ring.hashCode();
    }


    /** RealAlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random integer mod modul.
     */
    public RealAlgebraicNumber<C> random(int n) {
        GenPolynomial<C> x = ring.random( n ).monic();
        return new RealAlgebraicNumber<C>( this, x);
    }


    /** RealAlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random integer mod modul.
     */
    public RealAlgebraicNumber<C> random(int n, Random rnd) {
        GenPolynomial<C> x = ring.random( n, rnd ).monic();
        return new RealAlgebraicNumber<C>( this, x);
    }


    /** Parse RealAlgebraicNumber from String.
     * @param s String.
     * @return RealAlgebraicNumber from s.
     */
    public RealAlgebraicNumber<C> parse(String s) {
        GenPolynomial<C> x = ring.parse( s );
        return new RealAlgebraicNumber<C>( this, x );
    }


    /** Parse RealAlgebraicNumber from Reader.
     * @param r Reader.
     * @return next RealAlgebraicNumber from r.
     */
    public RealAlgebraicNumber<C> parse(Reader r) {
        GenPolynomial<C> x = ring.parse( r );
        return new RealAlgebraicNumber<C>( this, x );
    }

}
