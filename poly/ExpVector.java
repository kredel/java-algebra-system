/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Random;
import java.util.Vector;
import java.io.Serializable;


/**
 * ExpVector
 * implements exponent vectors for polynomials.
 * @author Heinz Kredel
 */


public class ExpVector implements Cloneable, Serializable {

    private final long[] val;

    //public static final int DEFAULT_EVORD = TermOrder.DEFAULT_EVORD;

    private final static Random random = new Random();


    /**
     * Constructors for ExpVector
     */

    public ExpVector(int n) {
        this( new long[n] );
    }

    public ExpVector(int n, int i, long e) {
        this( new long[n] );
        val[i] = e;
    }

    public ExpVector() {
        this( new long[0] );
    }

    public ExpVector(long[] v) {
        val = v;
    }

    public ExpVector(String[] v) {
        this( new long[v.length] );
    }

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

    public Object clone() {
        long[] w = new long[ val.length ];
        for (int i = 0; i < w.length; i++ ) {
            w[i] = val[i];
        }
        return new ExpVector( w );
    }

    public long[] getval() {
        return val;
    } 

    public long getVal(int i) {
        return val[i];
    } 

    public long setVal(int i, long e) {
        long x = val[i];
        val[i] = e;
        return x;
    } 

    public int length() {
        return val.length; 
    } 

    public String toString() {
        // if ( vars != null ) return toString(vars);
        StringBuffer s = new StringBuffer("(");
        for (int i = 0; i < val.length; i++ ) {
            s.append(val[i]);
            if ( i < val.length-1 ) s.append(",");
        }
        s.append(")");
        return s.toString();
    }

    public String toString(String[] vars) {
        String s = "";
        boolean pit;
	if ( val.length != vars.length ) return toString();
        for (int i = 0; i < (val.length-1); i++ ) {
            if ( val[i] != 0 ) { 
               s += vars[val.length-1-i];
               if ( val[i] != 1 ) {
                  s += "^" + val[i];
               }
               pit = false;
               for ( int j = i+1; j < val.length; j++ ) {
                   if ( val[j] != 0 ) pit = true;
               }
               if ( pit ) s += " * ";
            }
        }
        if ( val[val.length-1] != 0 ) { 
            s += vars[val.length-1-(val.length-1)];
               if ( val[val.length-1] != 1 ) {
                  s += "^" + val[val.length-1] + "";
               }
        }
        return s; 
    }

    public boolean equals( Object B ) { 
       if ( ! (B instanceof ExpVector) ) return false;
       int t = EVILCP( this, (ExpVector)B );
       //System.out.println("equals: this = " + this + " B = " + B + " t = " + t);
       return (0 == t);
    }

    public int hashCode() { 
        int h = 0;
        for (int i = 0; i < val.length; i++ ) {
            h = h<<4 + (int)val[i];
        }
        return h;
    }

    public boolean isZERO() { 
       return (0 == EVSIGN( this ) );
    }

    /**
     * Standard vars.
     */

    public String[] stdVars() {
        return STDVARS(val.length);
    }

    public static String[] STDVARS(int n) {
        String[] vars = new String[ n ];
        for ( int i = 0; i < n; i++) {
            vars[i] = "x" + (n-1-i);
        }
        return vars;
    }


    /**
     * Sum.
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

    public ExpVector sum( ExpVector V ) {
        return EVSUM(this, V);
    }


    /**
     * Difference.
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

    public ExpVector dif( ExpVector V ) {
        return EVDIF(this, V);
    }


    /**
     * Substitute.
     */

    public static ExpVector EVSU( ExpVector U, int i, long d ) {
        ExpVector V = (ExpVector)U.clone();
        long e = V.setVal( i, d );
        return V;
    }

    public ExpVector subst( int i, long d ) {
        return EVSU(this, i, d);
    }


    /**
     * Random.
     */

    public static ExpVector EVRAND( int r, int k, float q ) {
        long[] w = new long[r];
        long e;
        float f;
        for (int i = 0; i < w.length; i++ ) {
            f = random.nextFloat(); 
            if ( f > q ) { e = 0; }
            else { e = random.nextLong() % k; 
                   if ( e < 0 ) e = -e; 
            }
            w[i] = e;
        }
        return new ExpVector( w );
    }

    public static ExpVector EVRAND( int r, int k) {
        return EVRAND( r, k, (float)1.0 );
    }


    /**
     * Sign.
     */

    public static int EVSIGN( ExpVector U ) {
        int t = 0;
        long[] u = U.getval();
        for (int i = 0; i < u.length; i++ ) {
            if ( u[i] < 0 ) return -1;
            if ( u[i] > 0 ) t = 1;
        }
        return t;
    }


    /**
     * Total degree.
     */

    public static int EVTDEG( ExpVector U ) {
        int t = 0;
        long[] u = U.getval();
        for (int i = 0; i < u.length; i++ ) {
            t += u[i];
        }
        return t;
    }


    /**
     * Least common multiple.
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
     * Dependency on variables.
     */

    public static int[] EVDOV( ExpVector U ) {
        if ( U == null ) {
            return null;
        }
        return U.dependencyOnVariables();
    }

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
     * Multiple test. 
     * @returns true if U is a multiple of V, else false.
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
     * Inverse graded lexicographical compare.
     */

    public static int EVIGLC( ExpVector U, ExpVector V ) {
        long[] u = U.getval();
        long[] v = V.getval();
        int t = 0;
        int i;
        for ( i = 0; i < u.length; i++ ) {
            if ( u[i] > v[i] ) { t = 1; break; }
            if ( u[i] < v[i] ) { t = -1; break; }
        }
        if ( t == 0 ) return t;
        long up = 0; 
        long vp = 0; 
        for (int j = i; j < u.length; j++ ) {
            up += u[j]; vp += v[j]; 
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
     * Compare.
     */

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

}
