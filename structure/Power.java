/*
 * $Id$
 */

package edu.jas.structure;

//import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Power class to compute powers of RingElem. 
 * @author Heinz Kredel
 */
public class Power<C extends RingElem<C> > {

    //private static final Logger logger = Logger.getLogger(Residue.class);
    //private boolean debug = logger.isDebugEnabled();


    private final RingFactory<C> fac;


    /** The constructor creates a Power object.
     */
    public Power() {
        this(null);
    }


    /** The constructor creates a Power object.
     * @param fac ring factory 
     */
    public Power(RingFactory<C> fac) {
        this.fac = fac;
    }


    /** power of a to the n-th, n positive. 
     * @param a element. 
     * @param n integer exponent > 0.
     * @return a^n.
     */
    public static <C extends RingElem<C>> C positivePower(C a, long n) {
        if ( n <= 0 ) {
            throw new IllegalArgumentException("only positive n allowed");
        }
        if ( a.isZERO() || a.isONE() ) {
           return a;
        }
        C p = a;
        for (long i = 1; i < n; i++ ) {
            p = p.multiply( a );
        }
        return p;
    }


    /**
     * power of a to the n-th.
     * @param a element.
     * @param n integer exponent.
     * @return a^n, with 0^0 = 0 and a^{-n} = {1/a}^n.
     */
    public static <C extends RingElem<C>> C power( RingFactory<C> fac, C a, long n ) {
        if ( a == null || a.isZERO() ) {
           return a;
        }
        if ( n == 0 ) {
           if ( fac == null ) {
              throw new IllegalArgumentException("fac may not be null for a^0");
           }
           return fac.getONE();
        }
        if ( a.isONE() ) {
           return a;
        }
        C b = a;
        if ( n < 0 ) {
           b = a.inverse();
           n = -n;
        }
        if ( n == 1 ) {
           return b;
        }
        C p = fac.getONE();
        long i = n;
        do {
           if ( i % 2 == 1 ) {
              p = p.multiply( b );
           }
           i = i / 2;
           if ( i > 0 ) {
              b = b.multiply( b );
           }
        } while ( i > 0 );
        if ( n > 11 ) {
            System.out.println("n  = " + n);
            System.out.println("p  = " + p);
        }
        return p;
    }


    /** power of a to the n-th. 
     * @param a element. 
     * @param n integer exponent.
     * @return a^n, with 0^0 = 0.
     */
    public C power(C a, long n) {
        return power( fac, a, n );
    }
 
}
