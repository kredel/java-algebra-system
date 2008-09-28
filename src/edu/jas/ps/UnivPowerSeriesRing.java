/*
 * $Id$
 */

package edu.jas.ps;


import java.io.Reader;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

//import edu.jas.arith.BigInteger;


/**
 * Univariate power series implementation.
 * Uses inner classes and lazy evaluated generating function for coefficients.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public class UnivPowerSeriesRing<C extends RingElem<C>> 
             implements RingFactory< UnivPowerSeries<C> > {


    /**
     * A default random sequence generator.
     */
    protected final static Random random = new Random(); 


    /**
     * Default truncate.
     */
    public final static int DEFAULT_TRUNCATE = 11;


    /**
     * Truncate.
     */
    int truncate;



    /**
     * Default variable name.
     */
    public final static String DEFAULT_NAME = "x";


    /**
     * Variable name.
     */
    String var;


    /**
     * Coefficient ring factory.
     */
    public final RingFactory<C> coFac;


    /**
     * The constant power series 1 for this ring.
     */
    public final UnivPowerSeries<C> ONE;


    /**
     * The constant power series 0 for this ring.
     */
    public final UnivPowerSeries<C> ZERO;


    /**
     * No argument constructor.
     */
    private UnivPowerSeriesRing() {
	throw new RuntimeException("do not use no-argument constructor");
	//coFac = null;
	//ONE = null;
	//ZERO = null;
    }


    /**
     * Constructor.
     */
    public UnivPowerSeriesRing(RingFactory<C> coFac) {
	this( coFac, DEFAULT_TRUNCATE, DEFAULT_NAME );
    }


    /**
     * Constructor.
     */
    public UnivPowerSeriesRing(RingFactory<C> coFac, int truncate) {
	this( coFac, truncate, DEFAULT_NAME );
    }


    /**
     * Constructor.
     */
    public UnivPowerSeriesRing(RingFactory<C> coFac, String name) {
	this( coFac, DEFAULT_TRUNCATE, name );
    }


    /**
     * Constructor.
     */
    public UnivPowerSeriesRing(final RingFactory<C> coFac, int truncate, String name) {
	this.coFac = coFac;
        this.truncate = truncate;
	this.var = name;
        this.ONE = new UnivPowerSeries<C>(this,
                   new Coefficients<C>() {
                       public C get(int i) {
                           if ( i == 0 ) { 
                               return coFac.getONE();
                           } else {
                               return coFac.getZERO();
                           }
                       }
                   }//, null
                                              );
        this.ZERO = new UnivPowerSeries<C>(this,
                    new Coefficients<C>() {
                        public C get(int i) {
                            return coFac.getZERO();
                        }
                    }//, null
                                                    );
    }


    /**
     * Fix point construction.
     * Cannot be a static method because a power series ring is required.
     * @return fix point wrt map.
     */
    public UnivPowerSeries<C> fixPoint(PowerSeriesMap<C> map) {
             UnivPowerSeries<C> ps1 = new UnivPowerSeries<C>(this);
             UnivPowerSeries<C> ps2 = (UnivPowerSeries<C>) map.map(ps1);
             ps1.lazyCoeffs = ps2.lazyCoeffs;
             return ps2;
    }


    /**
     * To String.
     * @return string representation of this.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String scf = coFac.getClass().getSimpleName();
        sb.append(scf + "((" + var + "))");
        return sb.toString();
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals( Object B ) { 
       if ( ! ( B instanceof UnivPowerSeriesRing ) ) {
          return false;
       }
       UnivPowerSeriesRing<C> a = null;
       try {
           a = (UnivPowerSeriesRing<C>) B;
       } catch (ClassCastException ignored) {
       }
       if ( var.equals( a.var ) ) {
           return true;
       }
       return false;
    }


    /** Hash code for this .
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h = 0;
       h = ( var.hashCode() << 27 );
       h += truncate;
       return h;
    }


    /** Get the zero element.
     * @return 0 as UnivPowerSeriesRing<C>.
     */
    public UnivPowerSeries<C> getZERO() {
        return ZERO;
    }


    /** Get the one element.
     * @return 1 as UnivPowerSeriesRing<C>.
     */
    public UnivPowerSeries<C> getONE() {
        return ONE;
    }



    /**
     * Is commuative.
     * @return true, if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return coFac.isCommutative();
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        return coFac.isAssociative();
    }


    /**
     * Query if this ring is a field.
     * @return false.
     */
    public boolean isField() {
        return false;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return coFac.characteristic();
    }


    /** Get a (constant) UnivPowerSeries<C> from a long value.
     * @param a long.
     * @return a UnivPowerSeries<C>.
     */
    public UnivPowerSeries<C> fromInteger(long a) {
        return ONE.multiply( coFac.fromInteger(a) );
    }


    /** Get a (constant) UnivPowerSeries<C> from a java.math.BigInteger.
     * @param a BigInteger.
     * @return a UnivPowerSeries<C>.
     */
    public UnivPowerSeries<C> fromInteger(java.math.BigInteger a) {
        return ONE.multiply( coFac.fromInteger(a) );
    }


    /**
     * Generate a random power series with
     * k = 5, 
     * d = 0.7.
     * @param rnd is a source for random bits.
     * @return a random power series.
     */
    public UnivPowerSeries<C> random() {
        return random(5,0.7f,random);
    }


   /**
     * Generate a random power series with
     * d = 0.7.
     * @param k bitsize of random coefficients.
     * @return a random power series.
     */
    public UnivPowerSeries<C> random(int k) {
        return random(k,0.7f,random);
    }


   /**
     * Generate a random power series with
     * d = 0.7.
     * @param k bitsize of random coefficients.
     * @param rnd is a source for random bits.
     * @return a random power series.
     */
    public UnivPowerSeries<C> random(int k, Random rnd) {
        return random(k,0.7f,rnd);
    }


   /**
     * Generate a random power series.
     * @param k bitsize of random coefficients.
     * @param q density of non-zero coefficients.
     * @param rnd is a source for random bits.
     * @return a random power series.
     */
    public UnivPowerSeries<C> random(final int k, final float q, final Random rnd) {
        return new UnivPowerSeries<C>(this,
                   new Coefficients<C>() {
                       public C get(int i) {
                           float f = rnd.nextFloat(); 
                           if ( f < q ) { 
                               return coFac.random(k,rnd);
                           } else {
                               return coFac.getZERO();
                           }
                       }
                   }//, null
                                              );
     }


    /**
     * Copy power series.
     * @param c
     * @return a copy of c.
     */
    public UnivPowerSeries<C> copy(UnivPowerSeries<C> c) {
        System.out.println("GP copy = " + this);
        return new UnivPowerSeries<C>( this, c.lazyCoeffs, c.coeffCache );
    }


    /**
     * Parse a power series.
     * @param s String.
     * @return power series from s.
     */
    public UnivPowerSeries<C> parse(String s) {
        throw new RuntimeException("parse for power series not implemented");
    }


    /**
     * Parse a power series.
     * @param r Reader.
     * @return next power series from r.
     */
    public UnivPowerSeries<C> parse(Reader r) {
        throw new RuntimeException("parse for power series not implemented");
    }

}
