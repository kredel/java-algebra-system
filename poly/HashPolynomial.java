/*
 * $Id$
 */

package edu.jas.poly;

import java.util.TreeMap;
import java.util.Comparator;
import java.util.Set;
import java.util.Map;
import java.util.SortedMap;
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
import edu.jas.arith.Coefficient;

/**
 * Polynomial. 
 * Implementation based on HashMap / LinkedHashMap in add/difference/multiply.
 * Other methods work with any Map.
 */

public abstract class HashPolynomial implements Polynomial, Cloneable {

    private static Logger logger = Logger.getLogger(Polynomial.class);

    protected Map val = null;
    protected String[] vars = null;

    public final static int DEFAULT_EVORD = ExpVector.DEFAULT_EVORD;
    //public final static int DEFAULT_EVORD = ExpVector.INVLEX;
    protected int evord = DEFAULT_EVORD;

    protected Comparator horder = null;  // highest first 
    protected Comparator lorder = null;  // lowest first

    private class EVhorder implements Comparator {
	   public int compare(Object o1, Object o2) {
               return -ExpVector.EVCOMP( evord, 
                                         (ExpVector) o1, 
                                         (ExpVector) o2 ); 
           }
    }

    private class EVlorder implements Comparator {
	   public int compare(Object o1, Object o2) {
               return ExpVector.EVCOMP( evord, 
                                        (ExpVector) o1, 
                                        (ExpVector) o2 ); 
           }
    }

    //    public static final Polynomial ZERO;
    //    public static final Polynomial ONE;

    /**
     * creates a zero polynomial
     */
    public HashPolynomial() { 
        horder = new EVhorder();
        lorder = new EVlorder();
	//val = new TreeMap(horder);
        val = new LinkedHashMap(100);
    }

    public HashPolynomial(int eo, Comparator ho, Comparator lo) { 
        evord = eo;
        horder = ho;
        lorder = lo;
        //val = new TreeMap(horder);
        val = new LinkedHashMap(100);
    }

    public HashPolynomial(int r) { 
	this();
	vars = ExpVector.STDVARS(r);
    }

    public HashPolynomial(int r, int eo) { 
	this(r);
        evord = eo;
    }

    public HashPolynomial(TreeMap t, int eo) { 
	this();
        evord = eo;
        val = t;
    }

    public HashPolynomial(Map t, int eo) { 
	this();
        evord = eo;
        val = t;
    }

    public HashPolynomial(String[] v, int eo) { 
	this();
        evord = eo;
        vars = v;
    }

    public HashPolynomial(TreeMap t, int eo, String[] v) { 
	this();
        evord = eo;
        val = t;
	vars = v;
    }

    public HashPolynomial(Map t, int eo, String[] v) { 
	this();
        evord = eo;
        val = t;
	vars = v;
    }

    public HashPolynomial(TreeMap t) { 
	this();
        val = t;
    }

    public HashPolynomial(Map t) { 
	this();
        val = t;
    }

    public HashPolynomial(Object a, ExpVector e) { 
	this();
        val.put( e, a );
    }

    public Map getMap() { return val; }

    public Map setMap( Map m ) {  // ???
	Map t = val;
	val = m;
	return t;
    }

    public int getOrd() { return evord; }

    public int setOrd(int e) { 
       int t = evord;
       evord = e;
       return t; 
    }

    public Comparator getDescendComparator() { return horder; }

    public Comparator getAscendComparator() { return lorder; }

    public String[] getVars() { return vars; }

    public String[] setVars(String[] v) {
       String[] t = vars;
       vars = v;
       return t; 
    }

    public int length() { return val.size(); }

    abstract public Object clone();
    // { return new HashPolynomial(val,evord,vars); }

    abstract public Polynomial getZERO();
    // { return new HashPolynomial(vars,evord); }

    abstract public Polynomial getONE();
    // { return new HashPolynomial(vars,evord); }

    public String toString() { 
	StringBuffer erg = new StringBuffer();
        Set ent = val.entrySet();
        Iterator it = ent.iterator();
        while ( it.hasNext() ) {
            Map.Entry y = (Map.Entry) it.next();
	    ExpVector f = (ExpVector) y.getKey(); 
	    Object a = y.getValue();
	    erg.append(a + " " + f);  
	    if ( it.hasNext() ) {
		erg.append(" ");
	    } 
	}
        return erg.toString(); 
    }

    abstract public String toString(String[] vars); 


    public boolean equals( Object B ) { 
       if ( ! ( B instanceof Polynomial) ) return false;
       Polynomial c = this.subtract( (Polynomial) B );
       //       boolean t =    val.equals( b.getMap() )
       //      	   && evord == b.getOrd();
       //          && vars.equals( b.getVars() );
       return c.isZERO(); 
    }


    public void reSort() { 
	Map m = new TreeMap( horder );
	m.putAll( val );
	if ( val instanceof LinkedHashMap ) {
	   val = new LinkedHashMap( m );
	} else {
	   val = m;
	}
    }

    public void reSort(int ev) { 
	evord = ev;
        reSort();
    }

    /**
     * Leading monomial.
     */

    public Map.Entry LM() {  
	if ( val.size() == 0 ) return null;
        Set Ak = val.entrySet();
        Iterator ai = Ak.iterator();
        Map.Entry y = (Map.Entry) ai.next();
        return y;
    }

    public Map.Entry leadingMonomial() {  
	return this.LM();
    }

    public static Map.Entry DIPLM(Polynomial a) {  
	if ( a == null ) return null;
        return a.LM();
    }


    /**
     * Leading exponent vector.
     */

    public ExpVector LEV() {  
        Map.Entry m = LM();
	if ( m == null ) return null;
	/*
	if ( val.size() == 0 ) return null;
	Object h = val.firstKey();
	if ( h == null ) return null;
        */
        return (ExpVector)m.getKey();
    }

    public ExpVector leadingExpVector() {  
	return this.LEV();
    }

    public static ExpVector DIPLEV(Polynomial a) {  
	if ( a == null ) return null;
        return a.LEV();
    }


    /**
     * Number of variables.
     */

    public int NOV() {  
        ExpVector e = (ExpVector) this.LEV();
	if ( e == null ) { 
	    if ( vars != null ) return vars.length;
           return 0;
	}
        // ExpVector e = (ExpVector) m.getKey();
        return e.length();
    }

    public int numberOfVariables() {  
	return this.NOV();
    }

    public static int DIPNOV(Polynomial a) {  
	if ( a == null ) return 0;
        return a.NOV();
    }


    /**
     * Leading base coefficient.
     */

    public Object LBC() {  
        Map.Entry m = this.LM();
	if ( m == null ) return null;
        Object a = m.getValue();
        return a;
    }

    public Object leadingBaseCoefficient() {  
	return this.LBC();
    }

    public static Object DIPLBC(Polynomial a) {  
	if ( a == null ) return null;
        return a.LBC();
    }


    /**
     * Sum. Implementation based on LinkedHashMap.
     */
    public Polynomial add(Polynomial Bp) {  
        if ( evord != Bp.getOrd() ) { 
           logger.error("orderings not equal"); 
        }
        if ( ! (val instanceof SortedMap) && ! (val instanceof LinkedHashMap) ) { 
           logger.error("add requires LinkedHashMap or SortedMap"); 
        }
	if ( this.length() == 0 ) return Bp;
	if ( Bp.length() == 0 ) return this;
        Polynomial Cp = new RatPolynomial(evord,horder,lorder); 
	Cp.setVars( vars ); 
	Polynomial Ap = (Polynomial) this;

	Map A = Ap.getMap(); 
        Map B = Bp.getMap(); 
        Map C = Cp.getMap(); 

        Set As = A.entrySet();
        Iterator ai = As.iterator();
        Set Bs = B.entrySet();
        Iterator bi = Bs.iterator();

	Map.Entry x,y;
	ExpVector e,f;
        Coefficient a, b, c;

            // hasNext() == true here
            x = (Map.Entry) ai.next();
	    e = (ExpVector) x.getKey(); 
            y = (Map.Entry) bi.next();
	    f = (ExpVector) y.getKey(); 
            a = (Coefficient) x.getValue();
	    b = (Coefficient) y.getValue();

        do {
	    int s = ExpVector.EVCOMP( evord, e, f );
	    // System.out.println("s = " + s);
	    if ( s == 1 ) {
                C.put( e, a ); 
		if ( ai.hasNext() ) {
                   x = (Map.Entry) ai.next();
		   e = (ExpVector) x.getKey();
                   a = (Coefficient) x.getValue();
		} else {
		    x = null;
		    e = null;
		    a = null;
		}
	    } else if ( s == -1 ) {
                C.put( f, b ); 
		if ( bi.hasNext() ) {
                   y = (Map.Entry) bi.next();
		   f = (ExpVector) y.getKey();
		   b = (Coefficient) y.getValue();
		} else {
		    y = null;
		    f = null;
		    b = null;
		}
	    } else {
		c = a.add(b);
                if ( ! c.isZERO() ) { 
                   C.put( f, c ); 
                }
		if ( ai.hasNext() ) {
                   x = (Map.Entry) ai.next();
		   e = (ExpVector) x.getKey();
		   a = (Coefficient) x.getValue();
		} else {
		    x = null;
		    e = null;
		    a = null;
		}
		if ( bi.hasNext() ) {
                   y = (Map.Entry) bi.next();
		   f = (ExpVector) y.getKey();
		   b = (Coefficient) y.getValue();
		} else {
		    y = null;
		    f = null;
		    b = null;
		}
	    }
        } while ( x != null && y != null ); 

	if ( x != null ) {
	    C.put( e, a );
	}
	if ( y != null ) {
	    C.put( f, b );
	}
	while ( ai.hasNext() ) {
              x = (Map.Entry) ai.next();
              e = (ExpVector) x.getKey();
              a = (Coefficient) x.getValue();
	      C.put( e, a );
	}
	while ( bi.hasNext() ) {
              y = (Map.Entry) bi.next();
              f = (ExpVector) y.getKey();
              b = (Coefficient) y.getValue();
	      C.put( f, b );
	}
        return Cp;
    }

    public static Polynomial DIPSUM(Polynomial a, Polynomial b) {  
	if ( a == null ) return b;
        return a.add(b);
    }


    /**
     * Difference. Implementation based on LinkedHashMap.
     */
    public Polynomial subtract(Polynomial Bp) {  
        if ( evord != Bp.getOrd() ) { 
           logger.error("orderings not equal"); 
        }
        if ( ! (val instanceof SortedMap) && ! (val instanceof LinkedHashMap) ) { 
           logger.error("subtract requires LinkedHashMap or SortedMap"); 
        }
	if ( this.length() == 0 ) return Bp.negate();
	if ( Bp.length() == 0 ) return this;
        Polynomial Cp = new RatPolynomial(evord,horder,lorder); 
	Cp.setVars( vars ); 
	Polynomial Ap = (Polynomial) this;

	Map A = Ap.getMap(); 
        Map B = Bp.getMap(); 
        Map C = Cp.getMap(); 

        Set As = A.entrySet();
        Iterator ai = As.iterator();
        Set Bs = B.entrySet();
        Iterator bi = Bs.iterator();

	Map.Entry x,y;
	ExpVector e,f;
        Coefficient a, b, c;

            // hasNext() == true here
            x = (Map.Entry) ai.next();
	    e = (ExpVector) x.getKey(); 
            y = (Map.Entry) bi.next();
	    f = (ExpVector) y.getKey(); 
            a = (Coefficient) x.getValue();
	    b = (Coefficient) y.getValue();

        do {
	    int s = ExpVector.EVCOMP( evord, e, f );
	    //	    System.out.println("s = " + s);
	    if ( s == 1 ) {
                C.put( e, a ); 
		if ( ai.hasNext() ) {
                   x = (Map.Entry) ai.next();
		   e = (ExpVector) x.getKey();
                   a = (Coefficient) x.getValue();
		} else {
		    x = null;
		    e = null;
		    a = null;
		}
	    } else if ( s == -1 ) {
                C.put( f, b.negate() ); 
		if ( bi.hasNext() ) {
                   y = (Map.Entry) bi.next();
		   f = (ExpVector) y.getKey();
		   b = (Coefficient) y.getValue();
		} else {
		    y = null;
		    f = null;
		    b = null;
		}
	    } else {
		c = a.subtract(b);
                if ( ! c.isZERO() ) { 
                   C.put( f, c ); 
                }
		if ( ai.hasNext() ) {
                   x = (Map.Entry) ai.next();
		   e = (ExpVector) x.getKey();
		   a = (Coefficient) x.getValue();
		} else {
		    x = null;
		    e = null;
		    a = null;
		}
		if ( bi.hasNext() ) {
                   y = (Map.Entry) bi.next();
		   f = (ExpVector) y.getKey();
		   b = (Coefficient) y.getValue();
		} else {
		    y = null;
		    f = null;
		    b = null;
		}
	    }
        } while ( x != null && y != null ); 

	if ( x != null ) {
	    C.put( e, a );
	}
	if ( y != null ) {
	    C.put( f, b.negate() );
	}
	while ( ai.hasNext() ) {
              x = (Map.Entry) ai.next();
              e = (ExpVector) x.getKey();
              a = (Coefficient) x.getValue();
	      C.put( e, a );
	}
	while ( bi.hasNext() ) {
              y = (Map.Entry) bi.next();
              f = (ExpVector) y.getKey();
              b = (Coefficient) y.getValue();
	      C.put( f, b.negate() );
	}
        return Cp;
    }

    public static Polynomial DIPDIF(Polynomial a, Polynomial b) {  
	if ( a == null ) return b;
        return a.subtract(b);
    }


    /**
     * multiply. Implementation based on any LinkedHashMap.
     */
     public Polynomial multiply(Polynomial Bp) {  
        if ( evord != Bp.getOrd() ) { 
           logger.error("orderings not equal"); 
        }
        Polynomial Cp = new RatPolynomial(evord,horder,lorder); 
	Cp.setVars( vars ); // Cp.setord( evord );
        Map A = val; // this.getMap();
        Set Ak = A.entrySet();
        Iterator ai = Ak.iterator();
        while ( ai.hasNext() ) {
            Map.Entry y = (Map.Entry) ai.next();
	    ExpVector e = (ExpVector) y.getKey(); 
            //System.out.println("e = " + e);
            Coefficient a = (Coefficient) y.getValue(); 
            //System.out.println("a = " + a);
	    Polynomial Dp = Bp.multiply( a, e );
            //System.out.println("Dp = " + Dp.length());
	    Cp = Cp.add(Dp);
            //System.out.println("Cp = " + Cp.length());
        }
        return Cp;
    }

    public static Polynomial DIPPR(Polynomial a, Polynomial b) {  
	if ( a == null ) return null;
	if ( a.length() <= b.length() ) {
             return a.multiply(b);
	} else { 
             return b.multiply(a);
	}
    }

    /**
     * Product with number.
     */

    public Polynomial multiply(Coefficient b) {  
        Polynomial Cp = new RatPolynomial(evord,horder,lorder);
        Map C = Cp.getMap();
        Map A = val; //this.getMap();
        Set Ak = A.entrySet();
        Iterator ai = Ak.iterator();
        while ( ai.hasNext() ) {
            Map.Entry y = (Map.Entry) ai.next();
	    ExpVector e = (ExpVector) y.getKey(); 
            //System.out.println("e = " + e);
            Coefficient a = (Coefficient) y.getValue(); 
            //System.out.println("a = " + a);
            Coefficient c = a.multiply(b);
            //System.out.println("c = " + c);
            C.put( e, c );
        }
        return Cp;
    }

    public static Polynomial DIPRP(Polynomial a, Coefficient b) {  
	if ( a == null ) return null;
        return a.multiply(b);
    }

    /**
     * Product with number and exponent vector.
     */

    public Polynomial multiply(Coefficient b, ExpVector e) {  
        Polynomial Cp = new RatPolynomial(evord,horder,lorder);
        Map C = Cp.getMap();
        Map A = val; //this.getMap();
        Set Ak = A.entrySet();
        Iterator ai = Ak.iterator();
        while ( ai.hasNext() ) {
            Map.Entry y = (Map.Entry) ai.next();
	    ExpVector f = (ExpVector) y.getKey(); 
            //System.out.println("e = " + e);
            Coefficient a = (Coefficient) y.getValue(); 
            //System.out.println("a = " + a);
            Coefficient c = a.multiply(b);
            //System.out.println("c = " + c);
	    ExpVector g = f.sum(e);
            //System.out.println("g = " + g);
            C.put( g, c );
        }
        return Cp;
    }

    public static Polynomial DIPRP(Polynomial a, Coefficient b, ExpVector e) {
	if ( a == null ) return null;
        return a.multiply(b,e);
    }

    /**
     * Product with 'monomial'.
     */

    public Polynomial multiply(Map.Entry m) {  
	if ( m == null ) return null;
	return multiply( (Coefficient)m.getValue(), (ExpVector)m.getKey() );
    }

    public static Polynomial DIPRP(Polynomial a, Map.Entry m) {  
	if ( a == null ) return null;
        return a.multiply(m);
    }

    /**
     * Negation.
     */

    public Polynomial negate() {  
        Polynomial Cp = new RatPolynomial(evord,horder,lorder);
        Map C = Cp.getMap();
        Map A = val; //this.getMap();
        Set Ak = A.entrySet();
        Iterator ai = Ak.iterator();
        while ( ai.hasNext() ) {
            Map.Entry y = (Map.Entry) ai.next();
	    ExpVector e = (ExpVector) y.getKey(); 
            //System.out.println("e = " + e);
            Coefficient a = (Coefficient) y.getValue(); 
            //System.out.println("a = " + a);
            Coefficient c = a.negate();
            //System.out.println("c = " + c);
            C.put( e, c );
        }
        return Cp;
    }

    public static Polynomial DIPNEG(Polynomial a) {  
	if ( a == null ) return null;
        return a.negate();
    }

    /**
     * Zero ?.
     */

    public boolean isZERO() {  
        boolean t = false;
        if ( val.size() == 0 ) return true;
        if ( val.size() != 1 ) return t;
        Set Ak = val.entrySet();
        Iterator ai = Ak.iterator();
        Map.Entry y = (Map.Entry) ai.next();
        ExpVector e = (ExpVector) y.getKey(); 
        //System.out.println("e = " + e);
        if ( ExpVector.EVTDEG(e) != 0 ) return t;
        Coefficient a = (Coefficient) y.getValue(); 
        //System.out.println("a = " + a);
        if ( ! a.isZERO() ) return t;
        return true;
    }

    public static boolean DIPZERO(Polynomial a) {  
	if ( a == null ) return false;
        return a.isZERO();
    }


    /**
     * One ?.
     */

    public boolean isONE() {  
        boolean t = false;
        if ( val.size() != 1 ) return t;
        Set Ak = val.entrySet();
        Iterator ai = Ak.iterator();
        Map.Entry y = (Map.Entry) ai.next();
        ExpVector e = (ExpVector) y.getKey(); 
	//        System.out.println("e = " + e);
        if ( ExpVector.EVTDEG(e) != 0 ) return t;
        Coefficient a = (Coefficient) y.getValue(); 
        //System.out.println("a = " + a);
        if ( ! a.isONE() ) return t;
        return true;
    }

    public static boolean DIPONE(Polynomial a) {  
	if ( a == null ) return false;
        return a.isONE();
    }


}
