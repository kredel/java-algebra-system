
/*
 * $Id$
 */

package edu.jas.poly;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomialRing;

/**
 * GenPolynomial generic polynomials implementing RingElem.
 * n-variate ordered polynomials over C.
 * @author Heinz Kredel
 */

public class GenPolynomial<C extends RingElem<C> > implements RingElem< GenPolynomial<C> > {

    public final GenPolynomialRing< C > ring;
    protected final SortedMap<ExpVector,C> val;

    private static Logger logger = Logger.getLogger(GenPolynomial.class);


    /**
     * Constructors for GenPolynomial
     */

    // protected GenPolynomial() { ring = null; val = null; } // don't use

    public GenPolynomial(GenPolynomialRing< C > r) {
        ring = r;
        val = new TreeMap<ExpVector,C>( ring.tord.getDescendComparator() );
    }

    public GenPolynomial(GenPolynomialRing< C > r, SortedMap<ExpVector,C> v) {
        this(r);
        val.putAll( v );
    }

    public GenPolynomial(GenPolynomialRing< C > r, C c, ExpVector e) {
        this(r);
        val.put(e,c);
    }

    public GenPolynomial<C> clone() {
        // return ring.copy(this);
        return new GenPolynomial<C>(ring,this.val);
    }


    /**
     * Methods of GenPolynomial
     */

    public int length() { 
        return val.size(); 
    }


    public Map<ExpVector,C> getMap() { 
        return val; 
    }


    public String toString() {
        StringBuffer s = new StringBuffer( this.getClass().getSimpleName() + "[ " );
        boolean first = true;
        for ( Map.Entry<ExpVector,C> m : val.entrySet() ) {
            if ( first ) {
               first = false;
            } else {
               s.append( ", " );
            }
            s.append( m.getValue().toString() );
            s.append( " " );
            s.append( m.getKey().toString() );
        }
        s.append(" ] :: " + ring.toString() );
        return s.toString();
    }


    public String toString(String[] v) {
        StringBuffer s = new StringBuffer( this.getClass().getSimpleName() + "[ " );
        if ( val.size() == 0 ) {
           s.append( "0" );
        } else {
           boolean first = true;
           for ( Map.Entry<ExpVector,C> m : val.entrySet() ) {
               C c = m.getValue();
               if ( first ) {
                  first = false;
               } else {
                   if ( c.signum() < 0 ) {
                      s.append( " - " );
                      c = c.negate();
                   } else {
                      s.append( " + " );
                   }
               }
               s.append( c.toString() );
               s.append( " " );
               s.append( m.getKey().toString(v) );
           }
        }
        s.append(" ] :: " + ring.toString() );
        return s.toString();
    }


    public boolean isZERO() {
        return ( val.size() == 0 );
    }

    public boolean isONE() {
        if ( val.size() != 1 ) {
            return false;
        }
        C c = val.get( ring.evzero );
        if ( c == null ) {
            return false;
        }
        if ( ! c.isONE() ) {
            return false;
        }
        return true;
    }

    public boolean isUnit() {
        if ( val.size() != 1 ) {
            return false;
        }
        C c = val.get( ring.evzero );
        if ( c == null ) {
            return false;
        }
        if ( c.isUnit() ) {
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals( Object B ) { 
       GenPolynomial<C> a = null;
       try {
           a = (GenPolynomial<C>) B;
       } catch (ClassCastException ignored) {
       }
       if ( a == null ) {
           return false;
       }
       return this.subtract( a ).isZERO();
    }

    public int compareTo(GenPolynomial<C> b) { 
        if ( b == null ) {
            return this.signum();
        }
        int s = this.subtract( b ).signum();
        return s;
    }

    public int signum() {
        if ( this.isZERO() ) {
            return 0;
        }
        ExpVector t = val.firstKey();
        C c = val.get( t );
        return c.signum();
    }


    /**
     * Number of variables.
     */

    public int numberOfVariables() {  
        return ring.nvar;
    }


    /**
     * Leading monomial.
     */

    public Map.Entry<ExpVector,C> leadingMonomial() {
	if ( val.size() == 0 ) return null;
        Iterator<Map.Entry<ExpVector,C>> ai = val.entrySet().iterator();
        return ai.next();
    }

    /**
     * Leading exponent vector.
     */

    public ExpVector leadingExpVector() {
	if ( val.size() == 0 ) {
	    return null;
	}
        return val.firstKey(); 
    }


    /**
     * Leading base coefficient.
     */

    public C leadingBaseCoefficient() {
        Map.Entry<ExpVector,C> m = this.leadingMonomial();
	if ( m == null ) return null;
        return m.getValue();
    }


    /**
     * Sum. Implemantation based on TreeMap / SortedMap.
     */

    public GenPolynomial<C> add(GenPolynomial<C> S) {
        if ( S == null ) {
            return this;
        }
        if ( S.isZERO() ) {
            return this;
        }
        if ( this.isZERO() ) {
            return S;
        }
        GenPolynomial<C> n = this.clone(); //new GenPolynomial<C>(ring, val); 
        SortedMap<ExpVector,C> nv = n.val;
        SortedMap<ExpVector,C> sv = S.val;
        for ( ExpVector e : sv.keySet() ) {
            C x = nv.get( e );
            C y = sv.get( e ); // assert y != null
            if ( x != null ) {
               x = x.add(y);
               if ( ! x.isZERO() ) {
                  nv.put( e, x );
               } else {
                  nv.remove( e );
               }
            } else {
               nv.put( e, y );
            }
        }
        return n;
    }

    public GenPolynomial<C> add(C a, ExpVector e) {
        if ( a == null ) {
            return this;
        }
        if ( a.isZERO() ) {
            return this;
        }
        GenPolynomial<C> n = this.clone(); //new GenPolynomial<C>(ring, val); 
        SortedMap<ExpVector,C> nv = n.val;
        //if ( nv.size() == 0 ) { nv.put(e,a); return n; }
        C x = nv.get( e );
        if ( x != null ) {
           x = x.add(a);
           if ( ! x.isZERO() ) {
              nv.put( e, x );
           } else {
              nv.remove( e );
           }
        } else {
           nv.put( e, a );
        }
        return n;
    }


    /**
     * Difference. Implementation based on TreeMap / SortedMap.
     */

    public GenPolynomial<C> subtract(GenPolynomial<C> S) {
        if ( S == null ) {
            return this;
        }
        if ( S.isZERO() ) {
            return this;
        }
        if ( this.isZERO() ) {
            return S.negate();
        }
        GenPolynomial<C> n = this.clone(); //new GenPolynomial<C>(ring, val); 
        SortedMap<ExpVector,C> nv = n.val;
        SortedMap<ExpVector,C> sv = S.val;
        for ( ExpVector e : sv.keySet() ) {
            C x = nv.get( e );
            C y = sv.get( e ); // assert y != null
            if ( x != null ) {
               x = x.subtract(y);
               if ( ! x.isZERO() ) {
                  nv.put( e, x );
               } else {
                  nv.remove( e );
               }
            } else {
               nv.put( e, y );
            }
        }
        return n;
    }

    public GenPolynomial<C> subtract(C a, ExpVector e) {
        if ( a == null ) {
            return this;
        }
        if ( a.isZERO() ) {
            return this;
        }
        GenPolynomial<C> n = this.clone(); //new GenPolynomial<C>(ring, val); 
        SortedMap<ExpVector,C> nv = n.val;
        C x = nv.get( e );
        if ( x != null ) {
           x = x.subtract(a);
           if ( ! x.isZERO() ) {
              nv.put( e, x );
           } else {
              nv.remove( e );
           }
        } else {
            nv.put( e, a.negate() );
        }
        return n;
    }


    /**
     * Negation.
     */

    public GenPolynomial<C> negate() {
        GenPolynomial<C> n = ring.getZERO().clone(); 
                    //new GenPolynomial<C>(ring, ring.getZERO().val);
        SortedMap<ExpVector,C> v = n.val;
        for ( Map.Entry<ExpVector,C> m : val.entrySet() ) {
            C x = m.getValue(); // != null, 0
            v.put( m.getKey(), x.negate() );
            // or m.setValue( x.negate() ) if this cloned 
        }
        return n;
    }


    /**
     * Absolute value, i.e. leadingCoefficient > 0.
     */

    public GenPolynomial<C> abs() {
        if ( leadingBaseCoefficient().signum() < 0 ) {
           return this.negate();
         } else {
           return this;
        }
    }


    /**
     * Multiply. Implementation using map.put on result polynomial.
     */

    public GenPolynomial<C> multiply(GenPolynomial<C> S) {
        if ( S == null ) {
            return ring.getZERO();
        }
        if ( S.isZERO() ) {
            return ring.getZERO();
        }
        if ( this.isZERO() ) {
            return this;
        }
        GenPolynomial<C> p = ring.getZERO().clone(); 
        SortedMap<ExpVector,C> pv = p.val;
        for ( Map.Entry<ExpVector,C> m1 : val.entrySet() ) {
            C c1 = m1.getValue();
            ExpVector e1 = m1.getKey();
            for ( Map.Entry<ExpVector,C> m2 : S.val.entrySet() ) {
                C c2 = m2.getValue();
                ExpVector e2 = m2.getKey();
                C c = c1.multiply(c2);
                ExpVector e = e1.sum(e2);
                C c0 = pv.get( e );
                if ( c0 == null ) {
                    pv.put( e, c);
                } else {
                    c0 = c0.add( c );
                    if ( ! c0.isZERO() ) {
                       pv.put( e, c);
                    } else { // should not happen in integral domains
                        pv.remove( e );
                    }
                }
            }
        }
        return p;
    }


    /**
     * Product with ring element.
     */

    public GenPolynomial<C> multiply(C s) {
        if ( s == null ) {
            return ring.getZERO();
        }
        if ( s.isZERO() ) {
            return ring.getZERO();
        }
        if ( this.isZERO() ) {
            return this;
        }
        GenPolynomial<C> p = ring.getZERO().clone(); 
        SortedMap<ExpVector,C> pv = p.val;
        for ( Map.Entry<ExpVector,C> m1 : val.entrySet() ) {
            C c1 = m1.getValue();
            ExpVector e1 = m1.getKey();
            C c = c1.multiply(s);
            pv.put( e1, c ); // or m1.setValue( c )
        }
        return p;
    }


    /**
     * Product with ring element and exponent vector.
     */

    public GenPolynomial<C> multiply(C s, ExpVector e) {
        if ( s == null ) {
            return ring.getZERO();
        }
        if ( s.isZERO() ) {
            return ring.getZERO();
        }
        if ( this.isZERO() ) {
            return this;
        }
        GenPolynomial<C> p = ring.getZERO().clone(); 
        SortedMap<ExpVector,C> pv = p.val;
        for ( Map.Entry<ExpVector,C> m1 : val.entrySet() ) {
            C c1 = m1.getValue();
            ExpVector e1 = m1.getKey();
            C c = c1.multiply(s);
            ExpVector e2 = e1.sum(e);
            pv.put( e2, c ); 
        }
        return p;
    }


    /**
     * Product with exponent vector.
     */

    public GenPolynomial<C> multiply(ExpVector e) {
        if ( this.isZERO() ) {
            return this;
        }
        GenPolynomial<C> p = ring.getZERO().clone(); 
        SortedMap<ExpVector,C> pv = p.val;
        for ( Map.Entry<ExpVector,C> m1 : val.entrySet() ) {
            C c1 = m1.getValue();
            ExpVector e1 = m1.getKey();
            ExpVector e2 = e1.sum(e);
            pv.put( e2, c1 ); 
        }
        return p;
    }

    /**
     * Product with 'monomial'.
     */

    public GenPolynomial<C> multiply(Map.Entry<ExpVector,C> m) {
        if ( m == null ) {
            return ring.getZERO();
        }
        return multiply( m.getValue(), m.getKey() );
    }


    public GenPolynomial<C> divide(GenPolynomial<C> S) {
        throw new RuntimeException(this.getClass().getName()
                                   + " divide() not implemented");
        //return S;
    }

    public GenPolynomial<C> remainder(GenPolynomial<C> S) {
        throw new RuntimeException(this.getClass().getName()
                                   + " remainder() not implemented");
        //return S;
    }

    public GenPolynomial<C> inverse() {
        throw new RuntimeException(this.getClass().getName()
                                   + " inverse() not implemented");
        //return this;
    }

    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend all ExpVectors by i elements and multiply by x_j^k.
     */

    /**
     * Contract variables. Used e.g. in module embedding.
     * remove i elements of each ExpVector.
     */


}