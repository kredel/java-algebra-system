
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

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomialRing;

/**
 * GenPolynomial generic polynomials implementing RingElem.
 * n-variate ordered polynomials over C.
 * @author Heinz Kredel
 */

public class GenPolynomial<C extends RingElem<C> > 
             implements RingElem< GenPolynomial<C> > {

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
        val.putAll( v ); // assume no zero coefficients
    }

    public GenPolynomial(GenPolynomialRing< C > r, C c, ExpVector e) {
        this(r);
        if ( ! c.isZERO() ) {
           val.put(e,c);
        }
    }


    /**
     * Clone this GenPolynomial
     */
    public GenPolynomial<C> clone() {
        //return ring.copy(this);
        return new GenPolynomial<C>(ring,this.val);
    }


    /**
     * Methods of GenPolynomial
     */

    /**
     * Length = number of coefficients GenPolynomial
     */
    public int length() { 
        return val.size(); 
    }


    /**
     * ExpVector to coefficient map of GenPolynomial
     */
    public Map<ExpVector,C> getMap() { 
        return val; 
    }


    /**
     * String representation of GenPolynomial
     */
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


    /**
     * String representation of GenPolynomial
     * using names for variables from v
     */
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
               nv.put( e, y.negate() );
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
     * monic, i.e. ldcf = 1.
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


    /**
     * Division with remainder.
     */

    public GenPolynomial<C>[] divideRemainder(GenPolynomial<C> S) {
        if ( S == null || S.isZERO() ) {
           throw new RuntimeException(this.getClass().getName()
                                      + " division by zero");
        }
        C c = S.leadingBaseCoefficient();
        if ( ! c.isUnit() ) {
            logger.error("leadingBaseCoefficient not invertible " + c);
            throw new RuntimeException(this.getClass().getName()
                                        + " lbc not invertible " + c);
        }
        C ci = c.inverse();
        ExpVector e = S.leadingExpVector();
        //System.out.println("e = " + e);
        GenPolynomial<C> h;
        GenPolynomial<C> q = ring.getZERO().clone();
        GenPolynomial<C> r = this.clone(); 
        GenPolynomial<C> rx; 
        while ( ! r.isZERO() ) {
             ExpVector f = r.leadingExpVector();
             //System.out.println("f = " + f);
             if ( ExpVector.EVMT(f,e) ) {
                 C a = r.leadingBaseCoefficient();
		 f = ExpVector.EVDIF( f, e );
                 //logger.info("red div = " + e);
                 C ax = a;
                 a = a.multiply( ci );
                 q = q.add( a, f );
                 h = S.multiply( a, f );
                 rx = r;
                 r = r.subtract( h );
                 //if ( h.leadingExpVector().equals(r.leadingExpVector()) ) {
                 //   logger.error("degree r not decreasing");
                 //   throw new RuntimeException(this.getClass().getName()
                 //                       + " degree r not decreasing");
                 //}
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


    public GenPolynomial<C> divide(GenPolynomial<C> S) {
        //throw new RuntimeException(this.getClass().getName()
        //                           + " divide() not implemented");
        return divideRemainder(S)[0];
    }

    public GenPolynomial<C> remainder(GenPolynomial<C> S) {
        if ( S == null || S.isZERO() ) {
           throw new RuntimeException(this.getClass().getName()
                                      + " division by zero");
        }
        C c = S.leadingBaseCoefficient();
        if ( ! c.isUnit() ) {
            logger.error("leadingBaseCoefficient not invertible " + c);
            throw new RuntimeException(this.getClass().getName()
                                        + " lbc not invertible " + c);
        }
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> r = this.clone(); 
        while ( ! r.isZERO() ) {
             ExpVector f = r.leadingExpVector();
             if ( ExpVector.EVMT(f,e) ) {
                 C a = r.leadingBaseCoefficient();
		 f = ExpVector.EVDIF( f, e );
                 //logger.info("red div = " + e);
                 a = a.divide( c );
                 h = S.multiply( a, f );
                 r = r.subtract( h );
             } else {
                 break;
             }
        }
        return r;
    }


    public GenPolynomial<C> gcd(GenPolynomial<C> S) {
        if ( S == null || S.isZERO() ) {
            return this;
        }
        if ( this.isZERO() ) {
            return S;
        }
        if ( ring.nvar != 1 ) {
           logger.info("gcd only for univariate polynomials");
           // keep going
           return ring.getONE();
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
           logger.info("gcd only for univariate polynomials");
           // keep going
           ret[0] = ring.getONE();
           return ret;
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
            qr = q.divideRemainder(r);
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


    public GenPolynomial<C> inverse() {
        throw new RuntimeException(this.getClass().getName()
                                   + " inverse() not implemented");
        //return this;
    }

    public GenPolynomial<C> modInverse(GenPolynomial<C> m) {
        GenPolynomial<C>[] xegcd = this.egcd(m);
        GenPolynomial<C> a = xegcd[0];
        if ( !a.isUnit() ) {
           throw new RuntimeException(this.getClass().getName()
                                   + " not invertible");
        }
        a = xegcd[1];
        if ( a.isZERO() ) { // why does this happen?
           throw new RuntimeException(this.getClass().getName()
                                   + " not invertible");
        }
        return a; 
    }


    public GenPolynomial<C> pseudoRemainder(GenPolynomial<C> S) {
        if ( S == null || S.isZERO() ) {
           throw new RuntimeException(this.getClass().getName()
                                      + " division by zero");
        }
        C c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> r = this.clone(); 
        while ( ! r.isZERO() ) {
             ExpVector f = r.leadingExpVector();
             if ( ExpVector.EVMT(f,e) ) {
                 C a = r.leadingBaseCoefficient();
		 f = ExpVector.EVDIF( f, e );
                 //logger.info("red div = " + e);
                 r = r.multiply( c );
                 h = S.multiply( a, f );
                 r = r.subtract( h );
             } else {
                 break;
             }
        }
        return r;
    }

    public GenPolynomial<C> pseudoGcd(GenPolynomial<C> S) {
        if ( S == null || S.isZERO() ) {
            return this;
        }
        if ( this.isZERO() ) {
            return S;
        }
        if ( ring.nvar != 1 ) {
           logger.info("gcd only for univariate polynomials");
           // keep going
           return ring.getONE();
        }
        GenPolynomial<C> x;
        GenPolynomial<C> q = this;
        GenPolynomial<C> r = S;
        while ( !r.isZERO() ) {
            x = q.pseudoRemainder(r);
            q = r;
            r = x;
        }
        return q; // p.primitivePart() //q.monic(); // normalize
    }


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend all ExpVectors by i elements and multiply by x_j^k.
     */
    public GenPolynomial<C> extend(GenPolynomialRing<C> pfac, int j, long k) {
        //GenPolynomialRing<C> pfac = ring.extend(i);
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
     */
    public Map<ExpVector,GenPolynomial<C>> contract(GenPolynomialRing<C> pfac) {
        //GenPolynomialRing<C> pfac = ring.contract(i);
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
            p = p.add( a, g );
            B.put( f, p );
        }
        return B;
    }


}