
/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;
//import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.PrettyPrint;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomialRing;


/**
 * GenPolynomial generic polynomials implementing RingElem.
 * n-variate ordered polynomials over C.
 * Objects of this class are intended to be immutable.
 * The implementation is based on TreeMap respectively SortedMap 
 * from exponents to coefficients.
 * Only the coefficients are modeled with generic types,
 * the exponents are fixed to ExpVector with long entries 
 * (this will eventually be changed in the future).
 * C should be a domain, i.e. should not contain zero divisors, 
 * since multiply() does not check for zeros.
 * @author Heinz Kredel
 */

public class GenPolynomial<C extends RingElem<C> > 
             implements /*Gcd*/RingElem< GenPolynomial<C> > {

    /** The factory for the polynomial ring. 
     */
    public final GenPolynomialRing< C > ring;


    /** The data structure for polynomials. 
     */
    protected final SortedMap<ExpVector,C> val;


    private static Logger logger = Logger.getLogger(GenPolynomial.class);
    private final boolean debug = logger.isDebugEnabled();


    // protected GenPolynomial() { ring = null; val = null; } // don't use


    /**
     * Constructor for zero GenPolynomial.
     * @param r polynomial ring factory.
     */
    public GenPolynomial(GenPolynomialRing< C > r) {
        ring = r;
        val = new TreeMap<ExpVector,C>( ring.tord.getDescendComparator() );
    }


    /**
     * Constructor for GenPolynomial c * x<sup>e</sup>.
     * @param r polynomial ring factory.
     * @param c coefficient.
     * @param e exponent.
     */
    public GenPolynomial(GenPolynomialRing< C > r, C c, ExpVector e) {
        this(r);
        if ( ! c.isZERO() ) {
           val.put(e,c);
        }
    }


    /**
     * Constructor for GenPolynomial.
     * @param r polynomial ring factory.
     * @param v the SortedMap of some other polynomial.
     */
    protected GenPolynomial(GenPolynomialRing< C > r, 
                            SortedMap<ExpVector,C> v) {
        this(r);
        val.putAll( v ); // assume no zero coefficients
    }


    /**
     * Clone this GenPolynomial.
     * @see java.lang.Object#clone()
     */
    public GenPolynomial<C> clone() {
        //return ring.copy(this);
        return new GenPolynomial<C>(ring,this.val);
    }


    /**
     * Length of GenPolynomial. 
     * @return number of coefficients of this GenPolynomial.
     */
    public int length() { 
        return val.size(); 
    }


    /**
     * ExpVector to coefficient map of GenPolynomial.
     * @return val SortedMap.
     */
    public SortedMap<ExpVector,C> getMap() { 
        return val; 
    }


    /**
     * String representation of GenPolynomial.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append( this.getClass().getSimpleName() + "[ ");
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
        s.append(" ] "); // no not use: ring.toString() );
        return s.toString();
    }


    /**
     * String representation of GenPolynomial.
     * @param v names for variables.
     * @see java.lang.Object#toString()
     */
    public String toString(String[] v) {
        StringBuffer s = new StringBuffer();
        if ( PrettyPrint.isTrue() ) {
            if ( val.size() == 0 ) {
                s.append( "0" );
            } else {
                // s.append( "( " );
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
                    ExpVector e = m.getKey();
                    if ( !c.isONE() || e.isZERO() ) {
                       s.append( c.toString() );
                       s.append( " " );
                    }
                    if ( e != null && v != null ) {
                       s.append( e.toString(v) );
                    } else {
                       s.append( e );
                    }
                }
                //s.append(" )");
            }
        } else {
            s.append( this.getClass().getSimpleName() + "[ ");
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
                    ExpVector e = m.getKey();
                    if ( !c.isONE() || e.isZERO() ) {
                       s.append( c.toString() );
                       s.append( " " );
                    }
                    s.append( e.toString(v) );
                }
            }
            s.append(" ] "); // no not use: ring.toString() );
        }
        return s.toString();
    }


    /** Is GenPolynomial<C> zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return ( val.size() == 0 );
    }


    /** Is GenPolynomial<C> one. 
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
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


    /** Is GenPolynomial<C> a unit. 
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
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


    /** Is GenPolynomial<C> a constant. 
     * @return If this is a constant polynomial then true is returned, else false.
     */
    public boolean isConstant() {
        if ( val.size() != 1 ) {
            return false;
        }
        C c = val.get( ring.evzero );
        if ( c == null ) {
            return false;
        }
        return true;
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals( Object B ) { 
       if ( ! ( B instanceof GenPolynomial ) ) {
          return false;
       }
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


    /** Hash code for this polynomial.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h;
       h = ( ring.hashCode() << 27 );
       h += val.hashCode();
       return h;
    }


    /** GenPolynomial comparison.  
     * @param b GenPolynomial.
     * @return sign(this-b).
     */
    public int compareTo(GenPolynomial<C> b) { 
        if ( b == null ) {
            return this.signum();
        }
        int s = this.subtract( b ).signum();
        return s;
    }


    /** GenPolynomial signum.  
     * @return sign(ldcf(this)).
     */
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
     * @return ring.nvar.
     */
    public int numberOfVariables() {  
        return ring.nvar;
    }


    /**
     * Leading monomial.
     * @return first map entry.
     */
    public Map.Entry<ExpVector,C> leadingMonomial() {
        if ( val.size() == 0 ) return null;
        Iterator<Map.Entry<ExpVector,C>> ai = val.entrySet().iterator();
        return ai.next();
    }


    /**
     * Leading exponent vector.
     * @return first exponent.
     */
    public ExpVector leadingExpVector() {
        if ( val.size() == 0 ) {
           return null; // ring.evzero or null ?;
        }
        return val.firstKey(); 
    }


    /**
     * Leading base coefficient.
     * @return first coefficient.
     */
    public C leadingBaseCoefficient() {
        if ( val.size() == 0 ) {
           return ring.coFac.getZERO();
        }
        return val.get( val.firstKey() ); 
    }


    /**
     * Trailing base coefficient.
     * @return coefficient of constant term.
     */
    public C trailingBaseCoefficient() {
        C c = val.get( ring.evzero );
        if ( c == null ) {
           return ring.coFac.getZERO();
        }
        return c;
    }


    /**
     * Degree in variable i.
     * @return maximal degree in the variable i.
     */
    public long degree(int i) {
        if ( val.size() == 0 ) {
           return 0; // 0 or -1 ?;
        }
        int j = ring.nvar - 1 - i;
        long deg = 0;
        for ( ExpVector e : val.keySet() ) {
            long d = e.getVal(j);
            if ( d > deg ) {
               deg = d;
            }
        }
        return deg;
    }


    /**
     * Maximal degree.
     * @return maximal degree in any variables.
     */
    public long degree() {
        if ( val.size() == 0 ) {
           return 0; // 0 or -1 ?;
        }
        long deg = 0;
        for ( ExpVector e : val.keySet() ) {
            long d = ExpVector.EVMDEG( e );
            if ( d > deg ) {
               deg = d;
            }
        }
        return deg;
    }


    /**
     * Maximal degree vector.
     * @return maximal degree vector of all variables.
     */
    public ExpVector degreeVector() {
        ExpVector deg = ring.evzero;
        if ( val.size() == 0 ) {
           return deg; 
        }
        for ( ExpVector e : val.keySet() ) {
            deg = ExpVector.EVLCM( deg, e );
        }
        return deg;
    }


    /**
     * GenPolynomial maximum norm. 
     * @return ||this||.
     */
    public C maxNorm() {
        C n = ring.getZEROCoefficient(); 
        for ( C c : val.values() ) {
            C x = c.abs();
            if ( n.compareTo(x) < 0 ) {
               n = x;
            }
        }
        return n;
    }


    /**
     * GenPolynomial sum norm. 
     * @return sum of all absolute values of coefficients.
     */
    public C sumNorm() {
        C n = ring.getZEROCoefficient(); 
        for ( C c : val.values() ) {
            C x = c.abs();
            n = n.sum(x);
        }
        return n;
    }


    /**
     * GenPolynomial summation. 
     * @param S GenPolynomial.
     * @return this+S.
     */
    public GenPolynomial<C> sum(GenPolynomial<C> S) {
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
                x = x.sum(y);
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


    /**
     * GenPolynomial addition. 
     * This method is not very efficient, since this is copied.
     * @param a coefficient.
     * @param e exponent.
     * @return this + a x<sup>e</sup>.
     */
    public GenPolynomial<C> sum(C a, ExpVector e) {
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
            x = x.sum(a);
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
     * GenPolynomial subtraction. 
     * @param S GenPolynomial.
     * @return this-S.
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
                nv.put( e, y.negate() );
            }
        }
        return n;
    }


    /**
     * GenPolynomial subtraction. 
     * This method is not very efficient, since this is copied.
     * @param a coefficient.
     * @param e exponent.
     * @return this - a x<sup>e</sup>.
     */
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
     * GenPolynomial negation. 
     * @return -this.
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
     * GenPolynomial absolute value, i.e. leadingCoefficient &gt; 0.
     * @return abs(this).
     */
    public GenPolynomial<C> abs() {
        if ( leadingBaseCoefficient().signum() < 0 ) {
            return this.negate();
        } else {
            return this;
        }
    }


    /**
     * GenPolynomial multiplication. 
     * @param S GenPolynomial.
     * @return this*S.
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
                C c = c1.multiply(c2); // check non zero if not domain
                ExpVector e = e1.sum(e2);
                C c0 = pv.get( e );
                if ( c0 == null ) {
                    pv.put( e, c );
                } else {
                    c0 = c0.sum( c );
                    if ( ! c0.isZERO() ) {
                        pv.put( e, c0 );
                    } else { 
                        pv.remove( e );
                    }
                }
            }
        }
        return p;
    }


    /**
     * GenPolynomial multiplication. 
     * Product with coefficient ring element.
     * @param s coefficient.
     * @return this*s.
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
            C c = c1.multiply(s); // check non zero if not domain
            pv.put( e1, c ); // or m1.setValue( c )
        }
        return p;
    }


    /**
     * GenPolynomial monic, i.e. leadingCoefficient == 1.
     * If leadingCoefficient is not invertible returns this unmodified.
     * @return monic(this).
     */
    public GenPolynomial<C> monic() {
        if ( this.isZERO() ) {
            return this;
        }
        C lc = leadingBaseCoefficient();
        if ( !lc.isUnit() ) {
            //System.out.println("lc = "+lc);
            return this;
        }
        C lm = lc.inverse();
        return multiply(lm);
    }


    /**
     * GenPolynomial multiplication. 
     * Product with ring element and exponent vector.
     * @param s coefficient.
     * @param e exponent.
     * @return this * s x<sup>e</sup>.
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
            C c = c1.multiply(s); // check non zero if not domain
            ExpVector e2 = e1.sum(e);
            pv.put( e2, c ); 
        }
        return p;
    }


    /**
     * GenPolynomial multiplication. 
     * Product with exponent vector.
     * @param e exponent (!= null).
     * @return this * x<sup>e</sup>.
     */
    public GenPolynomial<C> multiply(ExpVector e) {
        // assert e != null. This is never allowed.
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
     * GenPolynomial multiplication. 
     * Product with 'monomial'.
     * @param m 'monomial'.
     * @return this * m.
     */
    public GenPolynomial<C> multiply(Map.Entry<ExpVector,C> m) {
        if ( m == null ) {
            return ring.getZERO();
        }
        return multiply( m.getValue(), m.getKey() );
    }


    /**
     * GenPolynomial division. 
     * Division by coefficient ring element.
     * Fails, if exact division is not possible.
     * @param s coefficient.
     * @return this/s.
     */
    public GenPolynomial<C> divide(C s) {
        if ( s == null || s.isZERO() ) {
           throw new RuntimeException(this.getClass().getName()
                                      + " division by zero");
        }
        if ( this.isZERO() ) {
            return this;
        }
        GenPolynomial<C> p = ring.getZERO().clone(); 
        SortedMap<ExpVector,C> pv = p.val;
        for ( Map.Entry<ExpVector,C> m : val.entrySet() ) {
            ExpVector e = m.getKey();
            C c1 = m.getValue();
            C c = c1.divide(s);
            if ( true ) {
                C x = c1.remainder(s);
                if ( !x.isZERO() ) {
                   System.out.println("divide x = " + x);
                   throw new RuntimeException(this.getClass().getName()
                                + " no exact division: " + c1 + "/" + s);
                }
            }
            if ( c.isZERO() ) {
               throw new RuntimeException(this.getClass().getName()
                                + " no exact division: " + c1 + "/" + s);
            }
            pv.put( e, c ); // or m1.setValue( c )
        }
        return p;
    }


    /**
     * GenPolynomial division with remainder.
     * Fails, if exact division by leading base coefficient is not possible.
     * Meaningful only for univariate polynomials over fields, but works 
     * in any case.
     * @param S nonzero GenPolynomial with invertible leading coefficient.
     * @return [ quotient , remainder ] with this = quotient * S + remainder.
     * @see edu.jas.ufd.GreatestCommonDivisorAbstract#basePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial).
     */
    public GenPolynomial<C>[] divideAndRemainder(GenPolynomial<C> S) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(this.getClass().getName()
                                       + " division by zero");
        }
        C c = S.leadingBaseCoefficient();
        if ( ! c.isUnit() ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " lbc not invertible " + c);
        }
        C ci = c.inverse();
        ExpVector e = S.leadingExpVector();
        //System.out.println("e = " + e);
        GenPolynomial<C> h;
        GenPolynomial<C> q = ring.getZERO().clone();
        GenPolynomial<C> r = this.clone(); 
        //GenPolynomial<C> rx; 
        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            //System.out.println("f = " + f);
            if ( ExpVector.EVMT(f,e) ) {
                C a = r.leadingBaseCoefficient();
                f = ExpVector.EVDIF( f, e );
                //logger.info("red div = " + e);
                //C ax = a;
                a = a.multiply( ci );
                q = q.sum( a, f );
                h = S.multiply( a, f );
                //rx = r;
                r = r.subtract( h );
            } else {
                break;
            }
        }
        //System.out.println("q = " + q + ", r = " +r);
        GenPolynomial<C>[] ret = new GenPolynomial[2];
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * GenPolynomial division.
     * Fails, if exact division by leading base coefficient is not possible.
     * Meaningful only for univariate polynomials over fields, but works 
     * in any case.
     * @param S nonzero GenPolynomial with invertible leading coefficient.
     * @return quotient with this = quotient * S + remainder.
     * @see edu.jas.ufd.GreatestCommonDivisorAbstract#basePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial).
     */
    public GenPolynomial<C> divide(GenPolynomial<C> S) {
        return divideAndRemainder(S)[0];
    }


    /**
     * GenPolynomial remainder.
     * Fails, if exact division by leading base coefficient is not possible.
     * Meaningful only for univariate polynomials over fields, but works 
     * in any case.
     * @param S nonzero GenPolynomial with invertible leading coefficient.
     * @return remainder with this = quotient * S + remainder.
     * @see edu.jas.ufd.GreatestCommonDivisorAbstract#basePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial).
     */
    public GenPolynomial<C> remainder(GenPolynomial<C> S) {
        if ( S == null || S.isZERO() ) {
           throw new RuntimeException(this.getClass().getName()
                                      + " division by zero");
        }
        C c = S.leadingBaseCoefficient();
        if ( ! c.isUnit() ) {
           throw new RuntimeException(this.getClass().getName()
                                      + " lbc not invertible " + c);
        }
        C ci = c.inverse();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> r = this.clone(); 
        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            if ( ExpVector.EVMT(f,e) ) {
                C a = r.leadingBaseCoefficient();
                f = ExpVector.EVDIF( f, e );
                //logger.info("red div = " + e);
                a = a.multiply( ci );
                h = S.multiply( a, f );
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenPolynomial greatest common divisor.
     * Only for univariate polynomials over fields.
     * @param S GenPolynomial.
     * @return gcd(this,S).
     */
    public GenPolynomial<C> gcd(GenPolynomial<C> S) {
        if ( S == null || S.isZERO() ) {
            return this;
        }
        if ( this.isZERO() ) {
            return S;
        }
        if ( ring.nvar != 1 ) {
           throw new RuntimeException(this.getClass().getName()
                                      + " not univariate polynomials" + ring);
        }
        GenPolynomial<C> x;
        GenPolynomial<C> q = this;
        GenPolynomial<C> r = S;
        while ( !r.isZERO() ) {
            x = q.remainder(r);
            q = r;
            r = x;
        }
        return q.monic(); // normalize
    }


    /**
     * GenPolynomial extended greatest comon divisor.
     * Only for univariate polynomials over fields.
     * @param S GenPolynomial.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    public GenPolynomial<C>[] egcd(GenPolynomial<C> S) {
        GenPolynomial<C>[] ret = new GenPolynomial[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if ( S == null || S.isZERO() ) {
            ret[0] = this;
            return ret;
        }
        if ( this.isZERO() ) {
            ret[0] = S;
            return ret;
        }
        if ( ring.nvar != 1 ) {
           throw new RuntimeException(this.getClass().getName()
                                      + " not univariate polynomials" + ring);
        }
        //System.out.println("this = " + this + ", S = " + S);
        GenPolynomial<C>[] qr;
        GenPolynomial<C> q = this; 
        GenPolynomial<C> r = S;
        GenPolynomial<C> c1 = ring.getONE().clone();
        GenPolynomial<C> d1 = ring.getZERO().clone();
        GenPolynomial<C> c2 = ring.getZERO().clone();
        GenPolynomial<C> d2 = ring.getONE().clone();
        GenPolynomial<C> x1;
        GenPolynomial<C> x2;
        while ( !r.isZERO() ) {
            qr = q.divideAndRemainder(r);
            q = qr[0];
            x1 = c1.subtract( q.multiply(d1) );
            x2 = c2.subtract( q.multiply(d2) );
            c1 = d1; c2 = d2;
            d1 = x1; d2 = x2;
            q = r;
            r = qr[1];
        }
        // normalize ldcf(q) to 1, i.e. make monic
        C g = q.leadingBaseCoefficient();
        if ( g.isUnit() ) {
            C h = g.inverse();
            q = q.multiply( h );
            c1 = c1.multiply( h );
            c2 = c2.multiply( h );
        }        
        //System.out.println("q = " + q + "\n c1 = " + c1 + "\n c2 = " + c2);
        ret[0] = q; 
        ret[1] = c1;
        ret[2] = c2;
        return ret;
    }


    /**
     * GenPolynomial inverse.
     * Required by RingElem.
     * Throws not implemented exception.
     */
    public GenPolynomial<C> inverse() {
        if ( isUnit() ) { // only possible if ldcf is unit
           C c = leadingBaseCoefficient().inverse();
           return ring.getONE().multiply( c );
        }
        throw new RuntimeException("element not invertible " + this);
    }


    /**
     * GenPolynomial modular inverse.
     * Only for univariate polynomials over fields.
     * @param m GenPolynomial.
     * @return a with with a*this = 1 mod m.
     */
    public GenPolynomial<C> modInverse(GenPolynomial<C> m) {
        GenPolynomial<C>[] xegcd = this.egcd(m);
        GenPolynomial<C> a = xegcd[0];
        if ( !a.isUnit() ) {
           throw new RuntimeException("element not invertible " + a);
        }
        a = xegcd[1];
        if ( a.isZERO() ) { // why does this happen?
           throw new RuntimeException("element not invertible " + a);
        }
        return a; 
    }


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend all ExpVectors by i elements and multiply by x_j^k.
     * @param pfac extended polynomial ring factory (by i variables).
     * @param j index of variable to be used for multiplication.
     * @param k exponent for x_j.
     * @return extended polynomial.
     */
    public GenPolynomial<C> extend(GenPolynomialRing<C> pfac, int j, long k) {
        GenPolynomial<C> Cp = pfac.getZERO().clone();
        int i = pfac.nvar - ring.nvar;

        if ( this.isZERO() ) return Cp;
        Map<ExpVector,C> C = Cp.getMap();
        Map<ExpVector,C> A = val;
        for ( Map.Entry<ExpVector,C> y: A.entrySet() ) {
            ExpVector e = y.getKey();
            //System.out.println("e = " + e);
            C a = y.getValue();
            //System.out.println("a = " + a);
            ExpVector f = e.extend(i,j,k);
            //System.out.println("e = " + e + ", f = " + f);
            C.put( f, a );
        }
        return Cp;
    }


    /**
     * Contract variables. Used e.g. in module embedding.
     * remove i elements of each ExpVector.
     * @param pfac contracted polynomial ring factory (by i variables).
     * @return Map of exponents and contracted polynomials.
     * <b>Note:</b> could return SortedMap
     */
    public Map<ExpVector,GenPolynomial<C>> contract(GenPolynomialRing<C> pfac) {
        GenPolynomial<C> zero = pfac.getZERO();
        int i = ring.nvar - pfac.nvar;

        TermOrder t = new TermOrder( TermOrder.INVLEX );
        Map<ExpVector,GenPolynomial<C>> B
            = new TreeMap<ExpVector,GenPolynomial<C>>( t.getAscendComparator() );

        if ( this.isZERO() ) return B;
        Map<ExpVector,C> A = val;
        for ( Map.Entry<ExpVector,C> y: A.entrySet() ) {
            ExpVector e = y.getKey();
            //System.out.println("e = " + e);
            C a = y.getValue();
            //System.out.println("a = " + a);
            ExpVector f = e.contract(0,i);
            ExpVector g = e.contract(i,e.length()-i);
            //System.out.println("e = " + e + ", f = " + f + ", g = " + g );
            GenPolynomial<C> p = B.get(f);
            if ( p == null ) {
                p = zero;
            }
            p = p.sum( a, g );
            B.put( f, p );
        }
        return B;
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @return polynomial with reversed variables.
     */
    public GenPolynomial<C> reverse(GenPolynomialRing<C> oring) {
        GenPolynomial<C> Cp = oring.getZERO().clone();
        if ( this.isZERO() ) return Cp;
        int k = -1;
        if ( oring.tord.getEvord2() != 0 && oring.partial ) {
            k = oring.tord.getSplit();
        }
        // logger.debug("poly k split = " + k );

        Map<ExpVector,C> C = Cp.getMap();
        Map<ExpVector,C> A = val;
        ExpVector f;
        for ( Map.Entry<ExpVector,C> y: A.entrySet() ) {
            ExpVector e = y.getKey();
            //System.out.println("e = " + e);
            if ( k >= 0 ) {
                f = e.reverse(k);
            } else {
                f = e.reverse();
            }
            //System.out.println("e = " + e + ", f = " + f);
            C a = y.getValue();
            //System.out.println("a = " + a);
            C.put( f, a );
        }
        return Cp;
    }


}
