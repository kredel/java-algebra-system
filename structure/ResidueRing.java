/*
 * $Id$
 */

package edu.jas.structure;

import java.util.Random;
import java.io.Reader;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
//import edu.jas.structure.PrettyPrint;


/**
 * Residue ring class based on GenPolynomial with RingElem interface.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class ResidueRing<C extends RingElem<C> > 
             implements RingFactory< Residue<C> >  {

    private static Logger logger = Logger.getLogger(ResidueRing.class);
    private boolean debug = logger.isDebugEnabled();


    /** Ring elemsnt for reduction. 
     */
    protected final C modul;


    /** Ring factory. 
     */
    protected final RingFactory<C> ring;


    /** The constructor creates a ResidueRing object 
     * from an ring factory and a modul. 
     * @param r ring factory.
     * @param m modul.
     */
    public ResidueRing(RingFactory<C> r, C m) {
        ring = r;
        if ( m.isZERO() ) {
           throw new RuntimeException("modul may not be null");
        }
        if ( m.isONE() ) {
           logger.warn("modul is one");
        }
        if ( m.signum() < 0 ) {
           m = m.negate();
        }
        modul = m; 
    }


    /** Copy Residue element c.
     * @param c
     * @return a copy of c.
     */
    public Residue<C> copy(Residue<C> c) {
        return new Residue<C>( c.ring, c.val );
    }


    /** Get the zero element.
     * @return 0 as Residue.
     */
    public Residue<C> getZERO() {
        return new Residue<C>( this, ring.getZERO() );
    }


    /**  Get the one element.
     * @return 1 as Residue.
     */
    public Residue<C> getONE() {
        Residue<C> one = new Residue<C>( this, ring.getONE() );
        if ( one.isZERO() ) {
           logger.warn("one is zero, so all residues are 0");
        }
        return one;
    }

    
    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return ring.isCommutative();
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        return ring.isAssociative();
    }


    /** Get a Residue element from a BigInteger value.
     * @param a BigInteger.
     * @return a Residue.
     */
    public Residue<C> fromInteger(java.math.BigInteger a) {
        return new Residue<C>( this, ring.fromInteger(a) );
    }


    /** Get a Residue element from a long value.
     * @param a long.
     * @return a Residue.
     */
    public Residue<C> fromInteger(long a) {
        return new Residue<C>( this, ring.fromInteger(a) );
    }
    

    /** Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Residue[ " 
                + modul.toString() + " ]";
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object b) {
        if ( ! ( b instanceof ResidueRing ) ) {
           return false;
        }
        ResidueRing<C> a = null;
        try {
            a = (ResidueRing<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        if ( ! ring.equals( a.ring ) ) {
            return false;
        }
        return modul.equals( a.modul );
    }


    /** Hash code for this residue ring.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h;
       h = ring.hashCode();
       h = 37 * h + modul.hashCode();
       return h;
    }


    /** Residue random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random residue element.
     */
    public Residue<C> random(int n) {
      C x = ring.random( n );
      // x = x.sum( ring.getONE() );
      return new Residue<C>( this, x );
    }


    /** Residue random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random residue element.
     */
    public Residue<C> random(int n, Random rnd) {
      C x = ring.random( n, rnd );
      // x = x.sum( ring.getONE() );
      return new Residue<C>( this, x);
    }


    /** Parse Residue from String.
     * @param s String.
     * @return Residue from s.
     */
    public Residue<C> parse(String s) {
        C x = ring.parse( s );
        return new Residue<C>( this, x );
    }


    /** Parse Residue from Reader.
     * @param r Reader.
     * @return next Residue from r.
     */
    public Residue<C> parse(Reader r) {
        C x = ring.parse( r );
        return new Residue<C>( this, x );
    }

}
