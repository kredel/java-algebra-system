/*
 * $Id$
 */

package edu.jas.ps;


import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Univariate power series implementation.
 * Uses inner classes and lazy evaluated generating function for coefficients.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public class UnivPowerSeries<C extends RingElem<C>> 
    implements RingElem<UnivPowerSeries<C>>, PowerSeries<C> {


    /**
     * Power series ring factory.
     */
    public final UnivPowerSeriesRing<C> ring;


    /**
     * Data structure / generating function for coeffcients.
     * Cannot be final because of fixPoint, must be accessible in factory.
     */
    /*package*/ Coefficients<C> lazyCoeffs;


    /**
     * Cache for already computed coefficients.
     */
    public final HashMap<Integer,C> coeffCache;


    /**
     * Truncation of computations.
     */
    private int truncate = 11;


    /**
     * Order of power series.
     */
    private int order = -1; // == unknown


    /**
     * Private constructor.
     */
    private UnivPowerSeries() {
        throw new RuntimeException("do not use no-argument constructor");
    }


    /**
     * Package constructor.
     * Use in fixPoint only, must be accessible in factory.
     * @param ring power series ring.
     */
    /*package*/ UnivPowerSeries(UnivPowerSeriesRing<C> ring) {
        this.ring = ring;
        this.lazyCoeffs = null;
        this.coeffCache = null;
    }


    /**
     * Constructor.
     * @param ring power series ring.
     * @param lazyCoeffs generating function for coefficients.
     */
    public UnivPowerSeries(UnivPowerSeriesRing<C> ring, Coefficients<C> lazyCoeffs) {
        this( ring, lazyCoeffs, new HashMap<Integer,C>() );
    }


    /**
     * Constructor.
     * @param ring power series ring.
     * @param lazyCoeffs generating function for coefficients.
     * @param coeffs cache for already known coefficients.
     */
    public UnivPowerSeries(UnivPowerSeriesRing<C> ring, Coefficients<C> lazyCoeffs, HashMap<Integer,C> coeffs) {
        if ( lazyCoeffs == null || ring == null ) {
            throw new IllegalArgumentException("null not allowed: ring = " + ring + ", lazyCoeffs = " + lazyCoeffs);
        }
        this.ring = ring;
        this.lazyCoeffs = lazyCoeffs;
        this.coeffCache = coeffs;
    }


    /**
     * Clone this power series.
     * @see java.lang.Object#clone()
     */
    @Override
    public UnivPowerSeries<C> clone() {
        //return ring.copy(this);
        return new UnivPowerSeries<C>(ring,lazyCoeffs,coeffCache);
    }


    /**
     * String representation of power series.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString(truncate);
    }


    /**
     * To String with given truncate.
     * @return string representation of this to given truncate.
     */
    public String toString(int truncate) {
        StringBuffer sb = new StringBuffer();
        UnivPowerSeries<C> s = this;
        //System.out.println("cache = " + s.coeffCache);
        for (int i = 0; i < truncate; i++ ) {
            C c = s.coefficient(i);
            int si = c.signum();
            if ( si != 0 ) {
                if ( si > 0 ) {
                    if ( i > 0 ) {
                       sb.append(" + ");
                    }
                    sb.append(c.toString() + " * x^" + i);
                } else {
                    c = c.negate();
                    sb.append(" - " + c.toString() + " * x^" + i);
                }
            //sb.append(c.toString() + ", ");
            }
            //System.out.println("cache = " + s.coeffCache);
        }
        if ( sb.length() == 0 ) {
           sb.append("0");
        }
        sb.append(" + BigO(x^" + truncate + ")");
        //sb.append("...");
        return sb.toString();
    }


    /**
     * Get coefficient.
     * @param index number of requested coefficient.
     * @return coefficient at index.
     */
    public C coefficient(int index) {
        if ( index < 0 ) {
            throw new IndexOutOfBoundsException("negative index not allowed");
        }
        //System.out.println("cache = " + coeffCache);
        if ( coeffCache != null ) {
           Integer i = index;
           C c = coeffCache.get( i );
           if ( c != null ) {
              return c;
           }
           c = lazyCoeffs.get( index );
           coeffCache.put( i, c );
           return c;
        } else {
            return lazyCoeffs.get( index );
        }
    }


    /**
     * Leading base coefficient.
     * @return first coefficient.
     */
    public C leadingCoefficient() {
        return coefficient(0);
    }


    /**
     * Reductum.
     * @return this - leading monomial.
     */
    public UnivPowerSeries<C> reductum() {
        return new UnivPowerSeries<C>(ring,
                   new Coefficients<C>() {
                       public C get(int i) {
                              return UnivPowerSeries.this.coefficient(i+1);
                       }
                   }
                                       );
    }


    /**
     * Prepend a new leading coefficient.
     * @param h new coefficient.
     * @return new power series.
     */
    public UnivPowerSeries<C> prepend(final C h) {
        return new UnivPowerSeries<C>(ring,
                   new Coefficients<C>() {
                       public C get(int i) {
                              if ( i == 0 ) {
                                  return h;
                              } else {
                                  return UnivPowerSeries.this.coefficient(i-1);
                              }
                       }
                   }
                                       );
    }


    /**
     * Shift coefficients.
     * @param k shift index.
     * @return new power series with coefficient(i) = old.coefficient(i+k).
     */
    public UnivPowerSeries<C> shift(final int k) {
        return new UnivPowerSeries<C>(ring,
                   new Coefficients<C>() {
                       public C get(int i) {
                              if ( k+i < 0 ) {
                                  return ring.coFac.getZERO();
                              } else {
                                  return UnivPowerSeries.this.coefficient(i+k);
                              }
                       }
                   }
                                       );
    }


    /**
     * Select coefficients.
     * @param sel selector functor.
     * @return new power series with selected coefficients.
     */
    public UnivPowerSeries<C> select(final Selector<? super C> sel) {
        return new UnivPowerSeries<C>(ring,
                   new Coefficients<C>() {
                       int pos = 0;
                       public C get(int i) {
                           //if ( i > 0 ) {
                                  //  C c = get(i-1);
                                  //System.out.println("warning: select get c = " + c);
                           //  }
                              C c = null;
                              do { 
                                   c = UnivPowerSeries.this.coefficient( pos++ );
                              } while( !sel.select(c));
                              return c;
                       }
                   }
                                       );
    }


    /**
     * Map a unary function to this power series.
     * @param f evaluation functor.
     * @return new power series with coefficients f(this(i)).
     */
    public UnivPowerSeries<C> map(final UnaryFunctor<? super C,C> f) {
        return new UnivPowerSeries<C>(ring,
                   new Coefficients<C>() {
                       public C get(int i) {
                              return f.eval( UnivPowerSeries.this.coefficient(i) );
                       }
                   }
                                       );
    }


    /**
     * Map a binary function to this and another power series.
     * @param f evaluation functor with coefficients f(this(i),other(i)).
     * @param ps other power series.
     * @return new power series.
     */
    public <C2 extends RingElem<C2>> 
        UnivPowerSeries<C> zip(
            final BinaryFunctor<? super C,? super C2,C> f,
            final PowerSeries<C2> ps
                           ) {
        return new UnivPowerSeries<C>(ring,
                   new Coefficients<C>() {
                       public C get(int i) {
                              return f.eval( UnivPowerSeries.this.coefficient(i), ps.coefficient(i) );
                       }
                   }
                                       );
    }


    /* arithmetic method functors */


    static class Sum<C extends RingElem<C>> implements BinaryFunctor<C,C,C> {
        public C eval(C c1, C c2) {
            return c1.sum(c2);
        }
    }


    static class Subtract<C extends RingElem<C>> implements BinaryFunctor<C,C,C> {
        public C eval(C c1, C c2) {
            return c1.subtract(c2);
        }
    }


    static class Multiply<C extends RingElem<C>> implements UnaryFunctor<C,C> {
        C x;
        public Multiply(C x) {
            this.x = x;
        }
        public C eval(C c) {
            return c.multiply(x);
        }
    }


    static class Negate<C extends RingElem<C>> implements UnaryFunctor<C,C> {
        public C eval(C c) {
            return c.negate();
        }
    }


    static class Abs<C extends RingElem<C>> implements UnaryFunctor<C,C> {
        int sign = 0;
        public C eval(C c) {
            int s = c.signum();
            if ( s == 0 ) {
               return c;
            }
            if ( sign > 0 ) {
               return c;
            } else if ( sign < 0 ) {
               return c.negate();
            }
            // first non zero coefficient:
            sign = s;
            if ( s > 0 ) {
               return c;
            }
            return c.negate();
        }
    }


    /**
     * Sum of two power series.
     * @param ps other power series.
     * @return this + ps.
     */
    public UnivPowerSeries<C> sum(UnivPowerSeries<C> ps) {
        return zip( new Sum<C>(), ps );
    } 


    /**
     * Subtraction of two power series.
     * @param ps other power series.
     * @return this - ps.
     */
    public UnivPowerSeries<C> subtract(UnivPowerSeries<C> ps) {
        return zip( new Subtract<C>(), ps );
    } 


    /**
     * Multiply by coefficient.
     * @param c coefficient.
     * @return this * c.
     */
    public UnivPowerSeries<C> multiply(C c) {
        return map( new Multiply<C>(c) );
    } 


    /**
     * Negate.
     * @return - this.
     */
    public UnivPowerSeries<C> negate() {
        return map( new Negate<C>() );
    } 


    /**
     * Absolute value.
     * @return abs(this).
     */
    public UnivPowerSeries<C> abs() {
        return map( new Abs<C>() );
    }


    /**
     * Evaluate at given point.
     * @return ps(c).
     */
    public C evaluate(C e) {
        C v = coefficient( 0 );
        C p = e;
        for ( int i = 1; i < truncate; i++ ) {
            C c = coefficient( i ).multiply( p );
            v = v.sum( c );
            p = p.multiply(e);
        }
        return v;
    }


    /**
     * Order.
     * @return index of first non zero coefficient.
     */
    public int order() {
        if ( order < 0 ) { // compute it
            for ( int i = 0; i <= truncate; i ++ ) {
                if ( !coefficient(i).isZERO() ) {
                    order = i;
                    break;
                }
                order = truncate+1;
            }
        }
        return order;
    }


    /**
     * Truncate.
     * @return truncate index of power series.
     */
    public int truncate() {
        return truncate;
    }


    /**
     * Set truncate.
     * @param t new truncate index.
     * @return old truncate index of power series.
     */
    public int setTruncate(int t) {
        if ( t < 0 ) {
            throw new RuntimeException("negative truncate not allowed");
        }
        int ot = truncate;
        truncate = t;
        return ot;
    }


    /**
     * Signum.
     * @return sign of first non zero coefficient.
     */
    public int signum() {
        int pos = 0;
        C c = null;
        do { 
            c = coefficient( pos++ );
        } while( c.isZERO() && pos <= truncate );
        return c.signum();
    }


    /**
     * Compare to.
     * <b>Note: </b> compare only up to truncate.
     * @return sign of first non zero coefficient of this-ps.
     */
    public int compareTo(UnivPowerSeries<C> ps) {
        int s = 0;
        int pos = 0;
        do { 
            s = coefficient( pos ).compareTo( ps.coefficient( pos ) );
            pos++;
        } while( s == 0 && pos <= truncate );
        return s;
    }


    /** Is power series zero. 
     * <b>Note: </b> compare only up to truncate.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return ( compareTo(ring.ZERO) == 0 );
    }


    /** Is power series one. 
     * <b>Note: </b> compare only up to truncate.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return ( compareTo(ring.ONE) == 0 );
    }


    /** Comparison with any other object.
     * <b>Note: </b> compare only up to truncate.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals( Object B ) { 
       if ( ! ( B instanceof PowerSeries ) ) {
          return false;
       }
       UnivPowerSeries<C> a = null;
       try {
           a = (UnivPowerSeries<C>) B;
       } catch (ClassCastException ignored) {
       }
       if ( a == null ) {
           return false;
       }
       return compareTo( a ) == 0;
    }


    /** Hash code for this polynomial.
     * <b>Note: </b> only up to truncate.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h = 0;
       //h = ( ring.hashCode() << 27 );
       //h += val.hashCode();
       for ( int i = 0; i <= truncate; i++ ) { 
           h += coefficient( i ).hashCode();
           h = ( h << 23 );
       };
       return h;
    }


    /**
     * Is unit.
     * @return true, if this power series is invertible, else false.
     */
    public boolean isUnit() {
        return leadingCoefficient().isUnit();
    }


    /**
     * Multiply by another power series.
     * @return this * ps.
     */
    public UnivPowerSeries<C> multiply( final UnivPowerSeries<C> ps ) {
        return new UnivPowerSeries<C>(ring,
                   new Coefficients<C>() {
                       public C get(int i) {
                           C c = null; //fac.getZERO();
                           for ( int k = 0; k <= i; k++ ) {
                               C m = coefficient(k).multiply( ps.coefficient(i-k) );
                               if ( c == null ) {
                                   c = m;
                               } else {
                                   c = c.sum(m);
                               }
                           }
                           return c;
                       }
                   }
                                       );
    }


    /**
     * Inverse power series.
     * @return ps with this * ps = 1.
     */
    public UnivPowerSeries<C> inverse() {
        return new UnivPowerSeries<C>(ring,
                   new Coefficients<C>() {
                       public C get(int i) {
                           C d = leadingCoefficient().inverse(); // may fail
                           if ( i == 0 ) {
                              return d;
                           }
                           C c = null; //fac.getZERO();
                           for ( int k = 0; k < i; k++ ) {
                               C m = coefficient(i-k).multiply( get(k) );
                               if ( c == null ) {
                                   c = m;
                               } else {
                                   c = c.sum(m);
                               }
                           }
                           return c.multiply( d.negate() );
                       }
                   }
                                       );
    }


    /**
     * Divide by another power series.
     * @return this * ps^{-1}.
     */
    public UnivPowerSeries<C> divide( UnivPowerSeries<C> ps ) {
        if ( ps.isUnit() ) {
           return multiply( ps.inverse() );
        }
        int m = order();
        int n = ps.order();
        if ( m < n ) {
            return ring.getZERO();
        }
        if ( ! ps.coefficient(n).isUnit() ) {
            throw new RuntimeException("division by non unit coefficient");
        }
        // now m >= n
        UnivPowerSeries<C> st, sps, q, sq;
        if ( m == 0 ) {
            st = this;
        } else {
            st = this.shift(m);
        }
        if ( n == 0 ) {
            sps = ps;
        } else {
            sps = ps.shift(n);
        }
        q = st.multiply( sps.inverse() );
        if ( m == n ) {
            sq = q;
        } else {
            sq = q.shift( -(m-n) );
        }
        return sq;
    }


    /**
     * Power series remainder.
     * @param ps nonzero power series with invertible leading coefficient.
     * @return remainder with this = quotient * ps + remainder.
     */
    public UnivPowerSeries<C> remainder( UnivPowerSeries<C> ps ) {
        int m = order();
        int n = ps.order();
        if ( m >= n ) {
            return ring.getZERO();
        }
        return this;
    }


    /**
     * Differentiate.
     * @return differentiate(this).
     */
    public UnivPowerSeries<C> differentiate() {
        return new UnivPowerSeries<C>(ring,
                   new Coefficients<C>() {
                       public C get(int i) {
                           C v = coefficient( i + 1 );
                           System.out.println("not implemented: fac.fromInteger(i+1)");
                           //v = v.multiply( fac.fromInteger(i+1) );
                           return v;
                       }
                   }
                                       );
    }


    /**
     * Integrate with given constant.
     * @return integrate(this).
     */
    public UnivPowerSeries<C> integrate( final C c ) {
        return new UnivPowerSeries<C>(ring,
                   new Coefficients<C>() {
                       public C get(int i) {
                           if ( i == 0 ) {
                              return c;
                           }
                           C v = coefficient( i - 1 );
                           System.out.println("not implemented: fac.fromInteger(i)");
                           //v = v.divide( fac.fromInteger(i) );
                           return v;
                       }
                   }
                                       );
    }


    /**
     * Power series greatest common divisor.
     * @param ps power series.
     * @return gcd(this,ps).
     */
    public UnivPowerSeries<C> gcd(UnivPowerSeries<C> ps) {
        if ( ps.isZERO() ) {
            return this;
        }
        if ( this.isZERO() ) {
            return ps;
        }
        int m = order();
        int n = ps.order();
        int l = ( m < n ) ? m : n;
        return ring.getONE().shift(-l);
    }


    /**
     * Power series extended greatest common divisor.
     * <b>Note:</b> not implemented.
     * @param S power series.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    //SuppressWarnings("unchecked")
    public UnivPowerSeries<C>[] egcd(UnivPowerSeries<C>  S) {
        throw new RuntimeException("egcd for power series not implemented");
    }

}
