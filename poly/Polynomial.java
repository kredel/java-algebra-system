/*
 * $Id$
 */

package edu.jas.poly;

import java.util.TreeMap;
import java.util.Comparator;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;

import edu.jas.arith.BigRational;
import edu.jas.arith.Coefficient;

public abstract class Polynomial implements Cloneable {

    protected TreeMap val = null;
    protected String[] vars = null;

    public final static int DEFAULT_EVORD = ExpVector.INVLEX;
    protected int evord = DEFAULT_EVORD;

    protected Comparator ord = null;
    protected static Comparator ordm = null;

    private class EV extends ExpVector implements Comparator {
	   public int compare(Object o1, Object o2) {
               return -ExpVector.EVCOMP( evord, 
                                         (ExpVector) o1, 
                                         (ExpVector) o2 ); 
           }
    }

    private class EVm extends ExpVector implements Comparator {
	   public int compare(Object o1, Object o2) {
               return ExpVector.EVCOMP( evord, 
                                        (ExpVector) o1, 
                                        (ExpVector) o2 ); 
           }
    }

    //    public static final Polynomial ZERO;
    //    public static final Polynomial ONE;

    public Polynomial() { 
        ord = new EV();
        if ( ordm == null ) ordm = new EVm();
        val = new TreeMap(ord);
    }

    public Polynomial(int r) { 
	this();
    }

    public Polynomial(int r, int eo) { 
	this();
        evord = eo;
    }

    public Polynomial(TreeMap t, int eo) { 
	this();
        evord = eo;
        val = t;
    }

    public Polynomial(String[] v, int eo) { 
	this();
        evord = eo;
        vars = v;
    }

    public Polynomial(TreeMap t, int eo, String[] v) { 
	this();
        evord = eo;
        val = t;
	vars = v;
    }

    public Polynomial(TreeMap t) { 
	this();
        val = t;
    }

    public Polynomial(Object a, ExpVector e) { 
	this();
        val.put( e, a );
    }

    public TreeMap getval() { return val; }

    public int getord() { return evord; }

    public int setord(int e) { 
       int t = evord;
       evord = e;
       return t; 
    }

    public String[] getvars() { return vars; }

    public String[] setvars(String[] v) {
       String[] t = vars;
       vars = v;
       return t; 
    }

    public int length() { return val.size(); }

    abstract public Object clone();
    // { return new Polynomial(val,evord,vars); }

    abstract public Polynomial getZERO();
    // { return new Polynomial(vars,evord); }

    abstract public Polynomial getONE();
    // { return new Polynomial(vars,evord); }

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

    //    abstract public boolean isONE(); 

    //    abstract public boolean isZERO(); 


    public boolean equals( Object B ) { 
       if ( ! ( B instanceof Polynomial) ) return false;
       Polynomial b = (Polynomial) B;
       boolean t =    val.equals( b.getval() )
             	   && evord == b.evord;
       //          && vars.equals( b.getvars() );
       return t; 
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

    public static Map.Entry DIPLM(Polynomial a) {  
	if ( a == null ) return null;
        return a.LM();
    }


    /**
     * Leading exponent vector.
     */

    public ExpVector LEV() {  
        Map.Entry m = this.LM();
	if ( m == null ) return null;
        ExpVector e = (ExpVector) m.getKey();
        return e;
    }

    public static ExpVector DIPLEV(Polynomial a) {  
	if ( a == null ) return null;
        return a.LEV();
    }


    /**
     * Number of variables.
     */

    public int NOV() {  
        Map.Entry m = this.LM();
	if ( m == null ) return 0;
        ExpVector e = (ExpVector) m.getKey();
        return e.length;
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

    public static Object DIPLBC(Polynomial a) {  
	if ( a == null ) return null;
        return a.LBC();
    }


    /**
     * Sum.
     */

    public Polynomial add(Polynomial B) {  
        if ( evord != B.evord ) { 
           System.err.println("ERROR: orderings not equal"); 
        }
	Polynomial Ap = (Polynomial) this.clone();
        TreeMap C = Ap.getval();
        TreeMap Bt = B.getval();
        Set Bk = Bt.entrySet();
        Iterator bi = Bk.iterator();
        while ( bi.hasNext() ) {
            Map.Entry y = (Map.Entry) bi.next();
	    ExpVector f = (ExpVector) y.getKey(); 
	    // System.out.println("f = " + f);
            Coefficient b = (Coefficient) y.getValue(); 
	    // System.out.println("b = " + b);
            Coefficient c = (Coefficient) C.remove( f );
            // System.out.println("c = " + c);
            if ( c != null ) { 
               c = c.add(b);
               // System.out.println("c = " + c);
               if ( ! c.isZERO() ) { 
                  C.put( f, c ); 
               }
            } else { 
               C.put( f, b ); 
            }
        }
        return Ap;
    }

    public static Polynomial DIPSUM(Polynomial a, Polynomial b) {  
	if ( a == null ) return b;
        return a.add(b);
    }

    /**
     * Difference.
     */

    public Polynomial subtract(Polynomial B) {  
        if ( evord != B.evord ) { 
           System.err.println("ERROR: orderings not equal"); 
        }
	Polynomial Ap = (Polynomial) this.clone();
        TreeMap C = Ap.getval();
        TreeMap Bt = B.getval();
        Set Bk = Bt.entrySet();
        Iterator bi = Bk.iterator();
        while ( bi.hasNext() ) {
            Map.Entry y = (Map.Entry) bi.next();
	    ExpVector f = (ExpVector) y.getKey(); 
            //System.out.println("f = " + f);
            Coefficient b = (Coefficient) y.getValue(); 
            //System.out.println("b = " + b);
            Coefficient c = (Coefficient) C.remove( (Object) f );
            //System.out.println("c = " + c);
            if ( c != null ) { 
               c = c.subtract(b);
               //System.out.println("c = " + c);
               if (  ! c.isZERO() ) { C.put( f, c ); }
            } else { 
               C.put( f, b.negate() ); 
            }
        }
        return Ap;
    }

    public static Polynomial DIPDIF(Polynomial a, Polynomial b) {  
	if ( a == null ) return b;
        return a.subtract(b);
    }

    /**
     * Product.
     */

    public Polynomial multiply(Polynomial Bp) {  
        if ( evord != Bp.evord ) { 
           System.err.println("ERROR: orderings not equal"); 
        }
        Polynomial Cp = (Polynomial) this.clone(); //getZERO(); //new Polynomial( vars, evord );
	Cp.setvars( vars ); Cp.setord( evord );
        TreeMap C = Cp.getval();
        TreeMap A = val; //this.getval();
        Set Ak = A.entrySet();
        Iterator ai = Ak.iterator();
        TreeMap B = Bp.getval();
        Set Bk = B.entrySet();
        while ( ai.hasNext() ) {
            Map.Entry y = (Map.Entry) ai.next();
	    ExpVector e = (ExpVector) y.getKey(); 
            //System.out.println("e = " + e);
            Coefficient a = (Coefficient) y.getValue(); 
            //System.out.println("a = " + a);
            Iterator bi = Bk.iterator();
            while ( bi.hasNext() ) {
                Map.Entry x = (Map.Entry) bi.next();
	        ExpVector f = (ExpVector) x.getKey(); 
                //System.out.println("f = " + f);
                Coefficient b = (Coefficient) x.getValue(); 
                //System.out.println("b = " + b);
	        ExpVector g = ExpVector.EVSUM(e,f); 
                //System.out.println("g = " + g);
                Coefficient c = a.multiply(b);
                //System.out.println("c = " + c);
                C.put( g, c );
            }
        }
        return Cp;
    }

    public static Polynomial DIPPR(Polynomial a, Polynomial b) {  
	if ( a == null ) return b;
        return a.multiply(b);
    }

    /**
     * Product with number.
     */

    public Polynomial multiply(Coefficient b) {  
        Polynomial Cp = (Polynomial) this.clone();
        TreeMap C = Cp.getval();
        TreeMap A = val; //this.getval();
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
     * Negation.
     */

    public Polynomial negate() {  
        Polynomial Cp = (Polynomial) this.clone(); //getZERO();
        TreeMap C = Cp.getval();
        TreeMap A = val; //this.getval();
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
