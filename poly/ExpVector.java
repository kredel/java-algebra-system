/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Random;
import java.util.Vector;
import java.io.Serializable;


/**
 * ExpVector implements exponent vectors for polynomials.
 * Exponent vectors are implemented as arrays of longs
 * with the familiar MAS static method names.
 * The implementation is only tested for nonnegative exponents
 * but should work also for negative exponents.
 * Objects of this class are intended to be immutable, but 
 * exponents can be set (during construction).
 * Will be made generic in the future, e.g. ExpVector&lt;long&gt;.
 * @author Heinz Kredel
 */

public class ExpVector implements Cloneable, Serializable {


    /**
     * The data structure is an array of longs.
     */
    private final long[] val;


    private final static Random random = new Random();


    /**
     * Constructor for ExpVector.
     * @param n length of exponent vector.
     */
    public ExpVector(int n) {
        this( new long[n] );
    }

    
    /**
     * Constructor for ExpVector.
     * Sets exponent i to e.
     * @param n length of exponent vector.
     * @param i index of exponent to be set.
     * @param e exponent to be set.
     */
    public ExpVector(int n, int i, long e) {
        this( new long[n] );
        val[i] = e;
    }


    /**
     * Constructor for ExpVector.
     * Sets val.
     * @param v other exponent vector.
     */
    protected ExpVector(long[] v) {
        val = v;
    }


    /**
     * Constructor for ExpVector.
     * Converts a String representation to an ExpVector.
     * Accepted format = (1,2,3,4,5,6,7).
     * @param s String representation.
     */
    public ExpVector(String s) throws NumberFormatException {
        // first format = (1,2,3,4,5,6,7)
        Vector exps = new Vector();
        s.trim();
        int b = s.indexOf('(');
        int e = s.indexOf(')',b+1);
        String teil;
        int k;
        long a;
        if ( b >= 0 && e >= 0 ) {
            b++;
            while ( ( k = s.indexOf(',',b) ) >= 0 ) {
                teil = s.substring(b,k);
                a = Long.parseLong( teil );
                exps.add( new Long( a ) ); 
                b = k + 1;
            }
            if ( b <= e ) {
                teil = s.substring(b,e);
                a = Long.parseLong( teil );
                exps.add( new Long( a ) ); 
            }
            int length = exps.size();
            val = new long[ length ];
            for ( int j = 0; j < length; j++ ) {
                val[j] = ((Long)exps.elementAt(j)).longValue();
            }
        } else {
        // not implemented
        val = null;
        // length = -1;
        //Vector names = new Vector();
        //vars = s;
        }
    }


    /** Clone this.
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        long[] w = new long[ val.length ];
        System.arraycopy(val,0,w,0,val.length);
        return new ExpVector( w );
    }


    /**
     * Get the exponent vector. 
     * @return val.
     */
    public long[] getval() {
        return val;
    } 


    /**
     * Get the exponent at position i. 
     * @param i position.
     * @return val[i].
     */
    public long getVal(int i) {
        return val[i];
    } 


    /**
     * Set the exponent at position i to e. 
     * @param i
     * @param e
     * @return old val[i].
     */
    protected long setVal(int i, long e) {
        long x = val[i];
        val[i] = e;
        return x;
    } 


    /**
     * Get the length of this exponent vector. 
     * @return val.length.
     */
    public int length() {
        return val.length; 
    } 


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend this by i elements and set val[j] to e.
     * @param i number of elements to extend.
     * @param j index of element to be set.
     * @param e new exponent for val[j].
     * @return extended exponent vector.
     */
    public ExpVector extend(int i, int j, long e) {
        long[] w = new long[ val.length + i ];
        System.arraycopy(val,0,w,i,val.length);
        if ( j >= i ) {
           throw new RuntimeException("i "+i+" <= j "+j+" invalid");
        }
        w[j] = e;
        return new ExpVector( w );
    }


    /**
     * Contract variables. Used e.g. in module embedding.
     * Contract this to len elements.
     * @param i position of first element to be copied.
     * @param len new length.
     * @return contracted exponent vector.
     */
    public ExpVector contract(int i, int len) {
        if ( i+len > val.length ) {
           throw new RuntimeException("len "+len+" > val.len "+val.length);
        }
        long[] w = new long[ len ];
        System.arraycopy(val,i,w,0,len);
        return new ExpVector( w );
    }


    /** Get the string representation.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        // if ( vars != null ) return toString(vars);
        StringBuffer s = new StringBuffer("(");
        for (int i = 0; i < val.length; i++ ) {
            s.append(val[i]);
            if ( i < val.length-1 ) {
               s.append(",");
            }
        }
        s.append(")");
        return s.toString();
    }


    /** Get the string representation with variable names.
     * @param vars names of variables.
     * @see java.lang.Object#toString()
     */
    public String toString(String[] vars) {
        String s = "";
        boolean pit;
	if ( val.length != vars.length ) {
           return toString();
        }
        for (int i = val.length-1; i > 0; i-- ) {
            if ( val[i] != 0 ) { 
               s += vars[val.length-1-i];
               if ( val[i] != 1 ) {
                  s += "^" + val[i];
               }
               pit = false;
               for ( int j = i-1; j >= 0; j-- ) {
                   if ( val[j] != 0 ) pit = true;
               }
               if ( pit ) s += " * ";
            }
        }
        if ( val[0] != 0 ) { 
            s += vars[val.length-1];
               if ( val[0] != 1 ) {
                  s += "^" + val[0] + "";
               }
        }
        return s; 
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object B ) { 
       if ( ! (B instanceof ExpVector) ) {
          return false;
       }
       ExpVector b = (ExpVector)B;
       int t = EVILCP( this, b );
       //System.out.println("equals: this = " + this + " B = " + B + " t = " + t);
       return (0 == t);
    }


    /** hashCode.
     * Optimized for small exponents, i.e. &le; 2<sup>4</sup>
     * and small number of variables, i.e. &le; 8.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() { 
        int h = 0;
        for (int i = 0; i < val.length; i++ ) {
            h = h<<4 + (int)val[i];
        }
        return h;
    }


    /** Is ExpVector zero. 
     * @return If this has all elements 0 then true is returned, else false.
     */
    public boolean isZERO() { 
       return (0 == EVSIGN( this ) );
    }


    /**
     * Standard variable names.
     * Generate standard names for variables, 
     * i.e. x0 to x(n-1). 
     * @return standard names.
     */
    public String[] stdVars() {
        return STDVARS("x",val.length);
    }


    /**
     * Generate variable names.
     * Generate names for variables, 
     * i.e. prefix0 to prefix(n-1). 
     * @param prefix name prefix.
     * @return standard names.
     */
    public String[] stdVars(String prefix) {
        return STDVARS(prefix,val.length);
    }


    /**
     * Standard variable names.
     * Generate standard names for variables, 
     * i.e. x0 to x(n-1). 
     * @param n size of names array
     * @return standard names.
     */
    public static String[] STDVARS(int n) {
        return STDVARS("x",n);
    }


    /**
     * Generate variable names.
     * Generate names for variables from given prefix.
     * i.e. prefix0 to prefix(n-1). 
     * @param n size of names array.
     * @param prefix name prefix.
     * @return vatiable names.
     */
    public static String[] STDVARS(String prefix, int n) {
        String[] vars = new String[ n ];
	if ( prefix == null || prefix.length() == 0 ) {
	    prefix = "x";
	}
        for ( int i = 0; i < n; i++) {
            vars[i] = prefix + i; //(n-1-i);
        }
        return vars;
    }


    /**
     * ExpVector summation.
     * @param U
     * @param V
     * @return U+V.
     */
    public static ExpVector EVSUM( ExpVector U, ExpVector V ) {
        long[] u = U.getval();
        long[] v = V.getval();
        long[] w = new long[u.length];
        for (int i = 0; i < u.length; i++ ) {
            w[i] = u[i] + v[i];
        }
        return new ExpVector( w );
    }

    /**
     * ExpVector summation.
     * @param V
     * @return this+V.
     */
    public ExpVector sum( ExpVector V ) {
        return EVSUM(this, V);
    }


    /**
     * ExpVector difference.
     * Result may have negative entries.
     * @param U
     * @param V
     * @return U-V.
     */
    public static ExpVector EVDIF( ExpVector U, ExpVector V ) {
        long[] u = U.getval();
        long[] v = V.getval();
        long[] w = new long[u.length];
        for (int i = 0; i < u.length; i++ ) { 
            w[i] = u[i] - v[i];
        }
        return new ExpVector( w );
    }


    /**
     * ExpVector difference.
     * Result may have negative entries.
     * @param V
     * @return this-V.
     */
    public ExpVector dif( ExpVector V ) {
        return EVDIF(this, V);
    }


    /**
     * ExpVector substitution.
     * Clone and set exponent to d at position i.
     * @param U
     * @param i position.
     * @param d new exponent.
     * @return substituted ExpVector.
     */
    public static ExpVector EVSU( ExpVector U, int i, long d ) {
        ExpVector V = (ExpVector)U.clone();
        long e = V.setVal( i, d );
        return V;
    }


    /**
     * ExpVector substitution.
     * Clone and set exponent to d at position i.
     * @param i position.
     * @param d new exponent.
     * @return substituted ExpVector.
     */
    public ExpVector subst( int i, long d ) {
        return EVSU(this, i, d);
    }


    /**
     * Generate a random ExpVector.
     * @param r length of new ExpVector. 
     * @param k maximal degree in each exponent.
     * @param q density of nozero exponents.
     * @return random ExpVector.
     */
    public static ExpVector EVRAND( int r, long k, float q ) {
        return EVRAND(r,k,q,random);
    }


    /**
     * Generate a random ExpVector.
     * @param r length of new ExpVector. 
     * @param k maximal degree in each exponent.
     * @param q density of nozero exponents.
     * @param rnd is a source for random bits.
     * @return random ExpVector.
     */
    public static ExpVector EVRAND( int r, long k, float q, Random rnd ) {
        long[] w = new long[r];
        long e;
        float f;
        for (int i = 0; i < w.length; i++ ) {
            f = rnd.nextFloat(); 
            if ( f > q ) { 
               e = 0; 
            } else { 
               e = rnd.nextLong() % k; 
               if ( e < 0 ) {
                  e = -e;
               } 
            }
            w[i] = e;
        }
        return new ExpVector( w );
    }


    /**
     * Generate a random ExpVector.
     * @param r length of new ExpVector. 
     * @param k maximal degree in each exponent.
     * @param q density of nozero exponents.
     * @return random ExpVector.
     */
    public static ExpVector random( int r, long k, float q ) {
        return EVRAND(r,k,q,random);
    }


    /**
     * Generate a random ExpVector.
     * @param r length of new ExpVector. 
     * @param k maximal degree in each exponent.
     * @param q density of nozero exponents.
     * @param rnd is a source for random bits.
     * @return random ExpVector.
     */
    public static ExpVector random( int r, long k, float q, Random rnd ) {
        return EVRAND(r,k,q,rnd);
    }


    /**
     * ExpVector sign.
     * @param U
     * @return 0 if U is zero, -1 if some entry is negative, 
               1 if no entry is negativ and at least one entry is positive.
     */
    public static int EVSIGN( ExpVector U ) {
        int t = 0;
        long[] u = U.getval();
        for (int i = 0; i < u.length; i++ ) {
            if ( u[i] < 0 ) {
               return -1;
            }
            if ( u[i] > 0 ) {
               t = 1;
            }
        }
        return t;
    }


    /**
     * ExpVector total degree.
     * @param U
     * @return sum of all exponents.
     */
    public static long EVTDEG( ExpVector U ) {
        long t = 0;
        long[] u = U.getval();
        for (int i = 0; i < u.length; i++ ) {
            t += u[i];
        }
        return t;
    }


    /**
     * ExpVector weighted degree.
     * @param w weights.
     * @param U
     * @return weighted sum of all exponents.
     */
    public static long EVWDEG( long[][] w, ExpVector U ) {
        if ( w == null || w.length == 0 ) { 
            return EVTDEG( U ); // assume weight 1 
        }
        long t = 0;
        long[] u = U.getval();
	for ( int j = 0; j < w.length; j++ ) {
	    long[] wj = w[j];
            for (int i = 0; i < u.length; i++ ) {
                t += wj[i] * u[i];
            }
	}
        return t;
    }


    /**
     * ExpVector least common multiple.
     * @param U
     * @param V
     * @return component wise maximum of U and V.
     */
    public static ExpVector EVLCM( ExpVector U, ExpVector V ) {
        long[] u = U.getval();
        long[] v = V.getval();
        long[] w = new long[u.length];
        for (int i = 0; i < u.length; i++ ) {
            w[i] = ( u[i] >= v[i] ? u[i] : v[i] );
        }
        return new ExpVector( w );
    }


    /**
     * ExpVector dependency on variables.
     * @param U
     * @return array of indices where U has positive exponents.
     */
    public static int[] EVDOV( ExpVector U ) {
        if ( U == null ) {
            return null;
        }
        return U.dependencyOnVariables();
    }


    /**
     * ExpVector dependency on variables.
     * @return array of indices where val has positive exponents.
     */
    public int[] dependencyOnVariables() {
        long[] u = val;
        int l = 0;
        for (int i = 0; i < u.length; i++ ) {
            if ( u[i] > 0 ) {
                l++;
            }
        }
        int[] dep = new int[ l ];
        if ( l == 0 ) {
            return dep;
        }
        int j = 0;
        for (int i = 0; i < u.length; i++ ) {
            if ( u[i] > 0 ) {
                dep[j] = i; j++;
            }
        }
        return dep;
    }


    /**
     * ExpVector multiple test.
     * Test if U is component wise greater or equal to V.
     * @param U
     * @param V
     * @return true if U is a multiple of V, else false.
     */
    public static boolean EVMT( ExpVector U, ExpVector V ) {
        long[] u = U.getval();
        long[] v = V.getval();
        boolean t = true;
        for (int i = 0; i < u.length; i++ ) {
            if ( u[i] < v[i] ) { 
               return false;
            }
        }
        return t;
    }


    /**
     * Inverse lexicographical compare.
     * @param U
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVILCP( ExpVector U, ExpVector V ) {
        long[] u = U.getval();
        long[] v = V.getval();
        int t = 0;
        for (int i = 0; i < u.length; i++ ) {
            if ( u[i] > v[i] ) return 1;
            if ( u[i] < v[i] ) return -1;
        }
        return t;
    }


    /**
     * Inverse lexicographical compare part.
     * Compare entries between begin and end (-1).
     * @param U
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVILCP( ExpVector U, ExpVector V, int begin, int end ) {
        long[] u = U.getval();
        long[] v = V.getval();
        int t = 0;
        for (int i = begin; i < end; i++ ) {
            if ( u[i] > v[i] ) return 1;
            if ( u[i] < v[i] ) return -1;
        }
        return t;
    }


    /**
     * Inverse graded lexicographical compare.
     * @param U
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVIGLC( ExpVector U, ExpVector V ) {
        long[] u = U.getval();
        long[] v = V.getval();
        int t = 0;
        int i;
        for ( i = 0; i < u.length; i++ ) {
            if ( u[i] > v[i] ) { 
               t = 1; break; 
            }
            if ( u[i] < v[i] ) { 
               t = -1; break; 
            }
        }
        if ( t == 0 ) { 
           return t;
        }
        long up = 0; 
        long vp = 0; 
        for (int j = i; j < u.length; j++ ) {
            up += u[j]; 
            vp += v[j]; 
        }
        if ( up > vp ) { 
           t = 1; 
        } else { 
           if ( up < vp ) { 
              t = -1; 
           }
        }
        return t;
    }


    /**
     * Inverse weighted lexicographical compare.
     * @param w weight array.
     * @param U
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVIWLC( long[][] w, ExpVector U, ExpVector V ) {
        long[] u = U.getval();
        long[] v = V.getval();
        int t = 0;
        int i;
        for ( i = 0; i < u.length; i++ ) {
            if ( u[i] > v[i] ) { 
               t = 1; break; 
            }
            if ( u[i] < v[i] ) { 
               t = -1; break; 
            }
        }
        if ( t == 0 ) {
           return t;
        }
	for ( int k = 0; k < w.length; k++ ) {
	    long[] wk = w[k];
            long up = 0; 
            long vp = 0; 
            for (int j = i; j < u.length; j++ ) {
                up += wk[j] * u[j]; 
                vp += wk[j] * v[j]; 
            }
            if ( up > vp ) { 
               return 1;
            } else if ( up < vp ) { 
               return -1;
            }
	}
        return t;
    }


    /**
     * Inverse graded lexicographical compare part.
     * Compare entries between begin and end (-1).
     * @param U
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVIGLC( ExpVector U, ExpVector V, int begin, int end ) {
        long[] u = U.getval();
        long[] v = V.getval();
        int t = 0;
        int i;
        for ( i = begin; i < end; i++ ) {
            if ( u[i] > v[i] ) { 
               t = 1; break; 
            }
            if ( u[i] < v[i] ) { 
               t = -1; break; 
            }
        }
        if ( t == 0 ) {
           return t;
        }
        long up = 0; 
        long vp = 0; 
        for (int j = i; j < end; j++ ) {
            up += u[j]; 
            vp += v[j]; 
        }
        if ( up > vp ) { 
           t = 1; 
        } else { 
           if ( up < vp ) { 
              t = -1; 
           }
        }
        return t;
    }


    /**
     * Inverse weighted lexicographical compare part.
     * Compare entries between begin and end (-1).
     * @param w weight array.
     * @param U
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVIWLC( long[][] w, ExpVector U, ExpVector V, int begin, int end ) {
        long[] u = U.getval();
        long[] v = V.getval();
        int t = 0;
        int i;
        for ( i = begin; i < end; i++ ) {
            if ( u[i] > v[i] ) { 
               t = 1; break; 
            }
            if ( u[i] < v[i] ) { 
               t = -1; break; 
            }
        }
        if ( t == 0 ) {
           return t;
        }
	for ( int k = 0; k < w.length; k++ ) {
	    long[] wk = w[k];
            long up = 0; 
            long vp = 0; 
            for (int j = i; j < end; j++ ) {
                up += wk[j] * u[j]; 
                vp += wk[j] * v[j]; 
            }
            if ( up > vp ) { 
               return 1;
            } else if ( up < vp ) { 
               return -1;
            }
	}
        return t;
    }


    /*
     * Compare.
     * @param evord
     * @param U
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     * @deprecated Now obsoleted by TermOrder.get*Comparator, 
     * used only in TermOrderTest.
     *
    public static int EVCOMP( int evord, ExpVector U, ExpVector V ) {
        int t = 0;
        switch ( evord ) {
            case TermOrder.LEX:    { 
                        t = ( -EVILCP( U, V ) );  break; 
            }
            case TermOrder.INVLEX: { 
                        t =    EVILCP( U, V )  ;  break; 
            }
            case TermOrder.GRLEX:  { 
                        t = ( -EVIGLC( U, V ) );  break; 
            }
            case TermOrder.IGRLEX: { 
                        t =    EVIGLC( U, V )  ;  break; 
            }
            default:     { 
                        throw new IllegalArgumentException("EVCOMP, undefined term order.");
            }
        }
        return t;
    }
     */


    /*
     * Compare part.
     * Compare entries between begin and end (-1).
     * @param evord
     * @param U
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     * @deprecated Now obsoleted by TermOrder.get*Comparator, 
     * used only in TermOrderTest.
     *
    public static int EVCOMP( int evord, ExpVector U, ExpVector V, 
                              int begin, int end ) {
        int t = 0;
        switch ( evord ) {
            case TermOrder.LEX:    { 
                        t = ( -EVILCP( U, V, begin, end ) );  break; 
            }
            case TermOrder.INVLEX: { 
                        t =    EVILCP( U, V, begin, end )  ;  break; 
            }
            case TermOrder.GRLEX:  { 
                        t = ( -EVIGLC( U, V, begin, end ) );  break; 
            }
            case TermOrder.IGRLEX: { 
                        t =    EVIGLC( U, V, begin, end )  ;  break; 
            }
            default:    { 
                        throw new IllegalArgumentException("EVCOMP, undefined term order.");
            }
        }
        return t;
    }
     */

}
