/*
 * $Id$
 */

package edu.jas.structure;

import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;


/**
 * Direct product element based on RingElem.
 * Objects of this class are (nearly) immutable.
 * @author Heinz Kredel
 */
public class Product<C extends RingElem<C> > 
             implements RegularRingElem< Product<C> > {

    private static final Logger logger = Logger.getLogger(Product.class);
    private boolean debug = logger.isDebugEnabled();


    /** Product class factory data structure. 
     */
    protected final ProductRing<C> ring;


    /** Value part of the element data structure. 
     */
    protected final SortedMap<Integer,C> val;


    /** Flag to remember if this residue element is a unit.
     * -1 is unknown, 1 is unit, 0 not a unit.
     */
    protected int isunit = -1; // initially unknown


    /** The constructor creates a Product object 
     * from a ring factory. 
     * @param r ring factory.
     */
    public Product(ProductRing<C> r) {
        this( r, new TreeMap<Integer,C>(), 0 );
    }


    /** The constructor creates a Product object 
     * from a ring factory and a ring element. 
     * @param r ring factory.
     * @param a ring element.
     */
    public Product(ProductRing<C> r, SortedMap<Integer,C> a) {
        this( r, a, -1 );
    }


    /** The constructor creates a Product object 
     * from a ring factory, a ring element and an indicator if a is a unit. 
     * @param r ring factory.
     * @param a ring element.
     * @param u isunit indicator, -1, 0, 1.
     */
    public Product(ProductRing<C> r, SortedMap<Integer,C> a, int u) {
        ring = r;
        val = a;
        isunit = u;
    }


    /**  Clone this.
     * @see java.lang.Object#clone()
     */
    public Product<C> clone() {
        return new Product<C>( ring, val );
    }
   

    /** Is Product zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return val.size() == 0;
    }


    /** Is Product one. 
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        if ( val.size() != ring.length() ) {
           return false;
        }
        for ( C e : val.values() ) {
            if ( ! e.isONE() ) {
               return false;
            }
        } 
        return true;
    }


    /** Is Product unit. 
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
        //if ( val.size() != ring.length() ) {
        //   isunit = 0;
        //   return false;
        //}
        for ( C e : val.values() ) {
            if ( ! e.isUnit() ) {
               isunit = 0;
               return false;
            }
        }
        isunit = 1;
        return true;
    }


    /** Is Product idempotent. 
     * @return If this is a idempotent element then true is returned, else false.
     */
    public boolean isIdempotent() {
        //if ( isUnit() ) {
        //   return true;
        //} 
        for ( C e : val.values() ) {
            if ( ! e.isONE() ) {
               return false;
            }
        }
        return true;
    }


    /** Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return val.toString(); 
        //        return "Product[ " + val.toString() 
        //     + " ] @ " + ring.toString();
    }


    /** Product comparison.  
     * @param b Product.
     * @return sign(this-b).
     */
    public int compareTo(Product<C> b) {
        if ( ! ring.equals( b.ring ) ) {
           throw new RuntimeException("rings not comparable " + this);
        }
        SortedMap<Integer,C> v = b.val;
        Iterator<Map.Entry<Integer,C>> ti = val.entrySet().iterator();
        Iterator<Map.Entry<Integer,C>> bi = v.entrySet().iterator();
        int s;
        while ( ti.hasNext() && bi.hasNext() ) {
            Map.Entry<Integer,C> te = ti.next();
            Map.Entry<Integer,C> be = bi.next();
            s = te.getKey().compareTo( be.getKey() );
            if ( s != 0 ) {
               return s;
            }
            s = te.getValue().compareTo( be.getValue() );
            if ( s != 0 ) {
               return s;
            }
        }
        if ( !ti.hasNext() && !bi.hasNext() ) {
           return 0;
        }
        if ( ti.hasNext() ) {
           return -1;
        }
        if ( bi.hasNext() ) {
           return 1;
        }
        return 0;
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked") 
    @Override
    public boolean equals(Object b) {
        if ( ! ( b instanceof Product ) ) {
           return false;
        }
        Product<C> a = null;
        try {
            a = (Product<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        return ( 0 == compareTo( a ) );
    }


    /** Hash code for this local.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h;
       h = ring.hashCode();
       h = 37 * h + val.hashCode();
       return h;
    }


    /** Product absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public Product<C> abs() {
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( Integer i : val.keySet() ) {
            C v = val.get(i).abs();
            elem.put( i, v );
        }
        return new Product<C>( ring, elem );
    }


    /** Product summation.
     * @param S Product.
     * @return this+S.
     */
    public Product<C> sum(Product<C> S) {
        if ( S == null || S.isZERO() ) {
           return this;
        }
        if ( this.isZERO() ) {
           return S;
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>( val );
        SortedMap<Integer,C> sel = S.val;
        for ( Integer i : sel.keySet() ) {
            C x = elem.get( i );
            C y = sel.get( i ); // assert y != null
            if ( x != null ) {
                x = x.sum(y);
                if ( ! x.isZERO() ) {
                    elem.put( i, x );
                } else {
                    elem.remove( i );
                }
            } else {
                elem.put( i, y );
            }
        }
        return new Product<C>( ring, elem );
    }


    /** Product negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Product<C> negate() {
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( Integer i : val.keySet() ) {
            C v = val.get(i).negate();
            elem.put( i, v );
        }
        return new Product<C>( ring, elem );
    }


    /** Product signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        if ( val.size() == 0 ) {
           return 0;
        }
        C v = val.get( val.firstKey() );
        return v.signum();
    }


    /** Product subtraction.
     * @param S Product.
     * @return this-S.
     */
    public Product<C> subtract(Product<C> S) {
        return sum( S.negate() );
    }


    /** Product division.
     * @param S Product.
     * @return this/S.
     */
    public Product<C> divide(Product<C> S) {
        return multiply( S.inverse() );
    }


    /** Product quasi-inverse.  
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this if defined. 
     */
    public Product<C> inverse() {
        if ( this.isZERO() ) {
           return this;
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( Integer i : val.keySet() ) {
            C x = val.get( i );
            x = x.inverse();
            if ( ! x.isZERO() ) { // cannot happen
               elem.put( i, x );
            }
        }
        return new Product<C>( ring, elem );
    }


    /** Product idempotent.  
     * @return smallest S with this*S = this.
     */
    public Product<C> idempotent() {
        if ( this.isZERO() ) {
           return this;
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( Integer i : val.keySet() ) {
            RingFactory<C> f = ring.getFactory( i );
            C x = f.getONE();
            elem.put( i, x );
        }
        return new Product<C>( ring, elem );
    }


    /** Product idempotent complement.  
     * @return 1-this.idempotent().
     */
    public Product<C> idemComplement() {
        if ( this.isZERO() ) {
            return ring.getONE();
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( int i = 0; i < ring.length(); i++ ) {
            C v = val.get( i );
            if ( v == null ) {
               RingFactory<C> f = ring.getFactory( i );
               C x = f.getONE();
               elem.put( i, x );
            }
        }
        return new Product<C>( ring, elem );
    }


    /** Product remainder.
     * @param S Product.
     * @return this - (this/S)*S.
     */
    public Product<C> remainder(Product<C> S) {
        return subtract( this.divide( S ).multiply( S ) );
    }


    /** Product multiplication.
     * @param S Product.
     * @return this*S.
     */
    public Product<C> multiply(Product<C> S) {
        if ( S == null ) {
           return ring.getZERO();
        }
        if ( S.isZERO() ) {
           return S;
        }
        if ( this.isZERO() ) {
           return this;
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        SortedMap<Integer,C> sel = S.val;
        for ( Integer i : val.keySet() ) {
            C y = sel.get( i ); 
            if ( y != null ) {
               C x = val.get( i );
               x = x.multiply(y);
               if ( ! x.isZERO() ) {
                  elem.put( i, x );
               }
            }
        }
        return new Product<C>( ring, elem );
    }


    /**
     * Greatest common divisor.
     * <b>Note: </b>Not implemented, throws RuntimeException.
     * @param b other element.
     * @return gcd(this,b).
     */
    public Product<C> gcd(Product<C> b) {
        throw new RuntimeException("gcd not implemented " + this.getClass().getName());
    }


    /**
     * Extended greatest common divisor.
     * <b>Note: </b>Not implemented, throws RuntimeException.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public Product<C>[] egcd(Product<C> b) {
        throw new RuntimeException("egcd not implemented " + this.getClass().getName());
    }
 
}
