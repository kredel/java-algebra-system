/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.jas.arith.Coefficient;

/**
 * Map Polynomial. 
 * Abstract implementation of UnorderedPolynomial.
 * Implementation based on Map / HashMap / LinkedHashMap 
 * @author Heinz Kredel
 */

public abstract class MapPolynomial 
                implements UnorderedPolynomial, Cloneable, Serializable {

    private static Logger logger = Logger.getLogger(MapPolynomial.class);

    protected final Map val;
    protected String[] vars;


    /**
     * Constructors for MapPolynomial
     */
    public MapPolynomial() { 
        vars = null;
        val = new LinkedHashMap(100);
    }

    public MapPolynomial(int r) { 
        this();
        vars = ExpVector.STDVARS(r);
    }

    public MapPolynomial(Map t) { 
        vars = null;
        val = new LinkedHashMap(t);
        // if (t instanceof LinkedHashMap) val = t.clone();
    }

    public MapPolynomial(String[] v) { 
        this();
        vars = v;
    }

    public MapPolynomial(Coefficient a, ExpVector e) { 
        this( e.length() );
        val.put( e, a );
    }


    /**
     * Methods of MapPolynomial
     */

    public int length() { 
        return val.size(); 
    }

    public Map getMap() { 
        return val; 
    }

    public String[] getVars() { 
        return vars; 
    }

    public String[] setVars(String[] v) {
       String[] t = vars;
       vars = v;
       return t; 
    }

    abstract public Object clone();
    // { return new MapPolynomial(val,evord,vars); }

    abstract public UnorderedPolynomial getZERO();
    // { return new MapPolynomial(vars,evord); }

    abstract public UnorderedPolynomial getONE();
    // { return new MapPolynomial(vars,evord); }

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

    //    abstract public String toString(String[] vars); 

    public String toString(String[] v) { 
        StringBuffer erg = new StringBuffer();
        Iterator it = val.entrySet().iterator();
        if ( ! it.hasNext() ) return erg.toString();
        Map.Entry y = (Map.Entry) it.next();
        ExpVector f = (ExpVector) y.getKey(); 
        Coefficient a = (Coefficient) y.getValue();
        boolean neg = false;
        while ( true ) {
            if ( neg ) {
               erg.append( a.negate() );
            } else {
              erg.append(a);
            }
            neg = false;
            erg.append(" " + f.toString(v));
            if ( it.hasNext() ) {
                y = (Map.Entry) it.next();
                f = (ExpVector) y.getKey(); 
                a = (Coefficient) y.getValue();
                if ( a.signum() < 0 ) {
                   erg.append(" - ");
                   neg = true;
                } else {
                   erg.append(" + ");
                   neg = false;
                }
            } else break; 
        } 
        return erg.toString(); 
    }

    public boolean equals( Object B ) { 
       if ( ! ( B instanceof UnorderedPolynomial) ) return false;
       boolean t = (this.subtract( (UnorderedPolynomial) B )).isZERO();
       // boolean t = val.equals( b.getMap() ); wrong impl in jdk1.4.0
       //      && vars.equals( b.getVars() );
       return t;
    }


    /**
     * Number of variables.
     */

    public int numberOfVariables() {  
        if ( vars != null ) return vars.length;
        if ( val.size() == 0 ) { 
           return 0;
        }
        Object e = ((Map.Entry)val.entrySet().iterator().next()).getKey();
        return ((ExpVector)e).length();
    }

    public static int DIPNOV(UnorderedPolynomial a) {  
        if ( a == null ) return 0;
        return a.numberOfVariables();
    }


    /**
     * Sum. Implementation based on LinkedHashMap without order.
     */

    public UnorderedPolynomial add(UnorderedPolynomial Bp) {  
        if ( this.length() == 0 ) return Bp;
        if ( Bp.length() == 0 ) return this;

        UnorderedPolynomial Cp = getZERO(); 
        Cp.setVars(vars);
        UnorderedPolynomial Ap = (UnorderedPolynomial) this.clone();

        Map A = Ap.getMap(); 
        Map B = Bp.getMap(); 
        Map C = Cp.getMap(); 

        Map.Entry x,y;
        ExpVector e,f;
        Coefficient a, b, c;

        Set Bs = B.entrySet();
        Iterator bi = Bs.iterator();
        while ( bi.hasNext() ) {
            y = (Map.Entry) bi.next();
            f = (ExpVector) y.getKey(); 
            b = (Coefficient) y.getValue();

            a = (Coefficient) A.remove( f ); // must be destructive
            if ( a == null ) {
                C.put( f, b );
            } else {
                c = a.add( b );
                if ( ! c.isZERO() ) { 
                   C.put( f, c ); 
                }
            }
        }

	// C.putAll(A) ?
        Set As = A.entrySet(); // rest of A
        Iterator ai = As.iterator();
        while ( ai.hasNext() ) {
            x = (Map.Entry) ai.next();
            e = (ExpVector) x.getKey(); 
            a = (Coefficient) x.getValue();

            b = null; //(Coefficient) C.get( e ); // is allways null
            if ( b == null ) {
                C.put( e, a );
            } else {
                System.out.println("b is not null " + b);
                c = a.add( b );
                if ( ! c.isZERO() ) { 
                   C.put( e, c ); 
                }
            }
        }
        return Cp;
    }

    /**
     * Sum. Implementation based on LinkedHashMap and given evord.
     */
    public UnorderedPolynomial add(int evord, UnorderedPolynomial Bp) {  
        if ( this.length() == 0 ) return Bp;
        if ( Bp.length() == 0 ) return this;

        UnorderedPolynomial Cp = getZERO(); 
        Cp.setVars(vars);
        UnorderedPolynomial Ap = (UnorderedPolynomial) this;

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

    public static UnorderedPolynomial DIPSUM(UnorderedPolynomial a, 
                                             UnorderedPolynomial b) {  
        if ( a == null ) return b;
        return a.add(b);
    }


    /**
     * Difference. Implementation based on LinkedHashMap without order.
     */
    public UnorderedPolynomial subtract(UnorderedPolynomial Bp) {  
        if ( this.length() == 0 ) return Bp;
        if ( Bp.length() == 0 ) return this;

        UnorderedPolynomial Cp = getZERO(); 
        Cp.setVars(vars);
        UnorderedPolynomial Ap = (UnorderedPolynomial) this.clone();

        Map A = Ap.getMap(); 
        Map B = Bp.getMap(); 
        Map C = Cp.getMap(); 

        Map.Entry x,y;
        ExpVector e,f;
        Coefficient a, b, c;

        Set Bs = B.entrySet();
        Iterator bi = Bs.iterator();
        while ( bi.hasNext() ) {
            y = (Map.Entry) bi.next();
            f = (ExpVector) y.getKey(); 
            b = (Coefficient) y.getValue();
            b = b.negate();

            a = (Coefficient) A.get( f ); 
            A.remove( f ); // must be destructive
            // System.out.println("f = " + f + " b = " + b + " a = " + a);
            if ( a == null ) {
                C.put( f, b );
            } else {
                c = a.add( b );
                if ( ! c.isZERO() ) { 
                   C.put( f, c ); 
                }
            }
        }

        Set As = A.entrySet(); // rest of A
        Iterator ai = As.iterator();
        while ( ai.hasNext() ) {
            x = (Map.Entry) ai.next();
            e = (ExpVector) x.getKey(); 
            a = (Coefficient) x.getValue();
            //System.out.println("e = " + e + " a = " + a);

            b = (Coefficient) C.get( e ); // is allways null
            if ( b == null ) {
                C.put( e, a );
            } else {
                System.out.println("b is not null " + b);
                c = a.add( b.negate() );
                if ( ! c.isZERO() ) { 
                   C.put( e, c ); 
                }
            }
        }
        return Cp;
    }


    /**
     * Difference. Implementation based on LinkedHashMap and evord.
     */
    public UnorderedPolynomial subtract(int evord, UnorderedPolynomial Bp) {  

        if ( this.length() == 0 ) return Bp.negate();
        if ( Bp.length() == 0 ) return this;

        UnorderedPolynomial Cp = getZERO(); 
        Cp.setVars(vars);
        UnorderedPolynomial Ap = (UnorderedPolynomial) this;

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
            //      System.out.println("s = " + s);
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

    public static UnorderedPolynomial DIPDIF(UnorderedPolynomial a, 
                                             UnorderedPolynomial b) {  
        if ( a == null ) return b;
        return a.subtract(b);
    }


    /**
     * multiply. Implementation based on any LinkedHashMap.
     */
     public UnorderedPolynomial multiply(UnorderedPolynomial Bp) {  
        UnorderedPolynomial Cp = getZERO(); 
        Cp.setVars(vars);
        Map A = val; // this.getMap();
        Set Ak = A.entrySet();
        Iterator ai = Ak.iterator();
        while ( ai.hasNext() ) {
            Map.Entry y = (Map.Entry) ai.next();
            ExpVector e = (ExpVector) y.getKey(); 
            //System.out.println("e = " + e);
            Coefficient a = (Coefficient) y.getValue(); 
            //System.out.println("a = " + a);
            UnorderedPolynomial Dp = Bp.multiply( a, e );
            //System.out.println("Dp = " + Dp.length());
            Cp = Dp.add(Cp);
            //System.out.println("Cp = " + Cp.length());
        }
        //System.out.println("Cp = " + Cp);
        return Cp;
    }

    public static UnorderedPolynomial DIPPR(UnorderedPolynomial a, 
                                            UnorderedPolynomial b) {  
        if ( a == null ) return null;
        if ( a.length() <= b.length() ) {
             return a.multiply(b);
        } else { 
             return b.multiply(a);
        }
    }

     public UnorderedPolynomial multiply(int evord, UnorderedPolynomial Bp) {  
        UnorderedPolynomial Cp = getZERO(); 
        Cp.setVars(vars);
        Map A = val; // this.getMap();
        Set Ak = A.entrySet();
        Iterator ai = Ak.iterator();
        while ( ai.hasNext() ) {
            Map.Entry y = (Map.Entry) ai.next();
            ExpVector e = (ExpVector) y.getKey(); 
            //System.out.println("e = " + e);
            Coefficient a = (Coefficient) y.getValue(); 
            //System.out.println("a = " + a);
            UnorderedPolynomial Dp = Bp.multiply( a, e );
            //System.out.println("Dp = " + Dp.length());
            Cp = ((MapPolynomial)Dp).add(evord,Cp);
            //System.out.println("Cp = " + Cp.length());
        }
        //System.out.println("Cp = " + Cp);
        return Cp;
    }

    /**
     * Product with number.
     */

    public UnorderedPolynomial multiply(Coefficient b) {  

        UnorderedPolynomial Cp = getZERO(); 
        Cp.setVars(vars);
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

    public static UnorderedPolynomial DIPRP(UnorderedPolynomial a, 
                                            Coefficient b) {  
        if ( a == null ) return null;
        return a.multiply(b);
    }


    /**
     * Product with number and exponent vector.
     */

    public UnorderedPolynomial multiply(Coefficient b, ExpVector e) {  
        UnorderedPolynomial Cp = getZERO(); 
        Cp.setVars(vars);
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

    public static UnorderedPolynomial DIPRP(UnorderedPolynomial a, 
                                            Coefficient b, 
                                            ExpVector e) {
        if ( a == null ) return null;
        return a.multiply(b,e);
    }

    /**
     * Product with 'monomial'.
     */

    public UnorderedPolynomial multiply(Map.Entry m) {  
        if ( m == null ) return null;
        return multiply( (Coefficient)m.getValue(), (ExpVector)m.getKey() );
    }

    public static UnorderedPolynomial DIPRP(UnorderedPolynomial a, 
                                            Map.Entry m) {  
        if ( a == null ) return null;
        return a.multiply(m);
    }

    /**
     * Negation.
     */

    public UnorderedPolynomial negate() {  
        UnorderedPolynomial Cp = getZERO(); 
        Cp.setVars(vars);
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

    public static UnorderedPolynomial DIPNEG(UnorderedPolynomial a) {  
        if ( a == null ) return null;
        return a.negate();
    }

    /**
     * Zero ?. Is this polynomial in the aequivalence class of zero?
     */

    public boolean isZERO() {  
        boolean t = false;
        if ( val.size() == 0 ) return true;
        if ( val.size() != 1 ) return t;
        Map.Entry y = (Map.Entry) val.entrySet().iterator().next();
        ExpVector e = (ExpVector) y.getKey(); 
        //System.out.println("e = " + e);
        if ( ExpVector.EVTDEG(e) != 0 ) return t;
        Coefficient a = (Coefficient) y.getValue(); 
        //System.out.println("a = " + a);
        if ( ! a.isZERO() ) return t;
        return true;
    }

    public static boolean DIPZERO(UnorderedPolynomial a) {  
        if ( a == null ) return false;
        return a.isZERO();
    }


    /**
     * One ?. Is this polynomial in the aequivalence class of one?
     */

    public boolean isONE() {  
        boolean t = false;
        if ( val.size() != 1 ) return t;
        Map.Entry y = (Map.Entry) val.entrySet().iterator().next();
        ExpVector e = (ExpVector) y.getKey(); 
        //        System.out.println("e = " + e);
        if ( ExpVector.EVTDEG(e) != 0 ) return t;
        Coefficient a = (Coefficient) y.getValue(); 
        //System.out.println("a = " + a);
        if ( ! a.isONE() ) return t;
        return true;
    }

    public static boolean DIPONE(UnorderedPolynomial a) {  
        if ( a == null ) return false;
        return a.isONE();
    }


}
