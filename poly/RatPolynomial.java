/*
 * $Id$
 */

package edu.jas.poly;

import java.util.TreeMap;
import java.util.Comparator;
import java.util.Set;
import java.util.Map;
import java.util.SortedMap;
import java.util.LinkedHashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;

  /**
   * old version of polynomial implementation
   * @author Heinz Kredel
   * @deprecated
   */

public class RatPolynomial extends TreePolynomial {

    private static Logger logger = Logger.getLogger(RatPolynomial.class);

    public RatPolynomial() { 
	super();
    }

    public RatPolynomial(int eo, Comparator ho, Comparator lo) { 
	super(eo,ho,lo);
    }

    public RatPolynomial(int r) { 
	super(r);
    }

    public RatPolynomial(int r, int eo) { 
	super(r,eo);
    }

    public RatPolynomial(TreeMap t, int eo) { 
	super(t,eo);
    }

    public RatPolynomial(Map t, int eo) { 
	super(t,eo);
    }

    public RatPolynomial(TreeMap t, int eo, String[] v) { 
	super(t,eo,v);
    }

    public RatPolynomial(Map t, int eo, String[] v) { 
	super(t,eo,v);
    }

    public RatPolynomial(String[] v, int eo) { 
	super(v,eo);
    }

    public RatPolynomial(TreeMap t) { 
	super(t);
    }

    public RatPolynomial(Map t) { 
	super(t);
    }

    public RatPolynomial( BigRational a, ExpVector e) { 
	super(a,e);
    }

    public Object clone() { 
       Map c = null;
       if ( val instanceof TreeMap ) 
          c = (Map)((TreeMap)val).clone();
       if ( val instanceof LinkedHashMap ) 
          c = (Map)((LinkedHashMap)val).clone();
       return new RatPolynomial( c, evord, vars); 
    }

    public Polynomial getZERO() { 
       return (Polynomial) ZERO.clone();
    }

    public Polynomial getONE() { 
       return (Polynomial) ONE.clone(); 
    }

    public static final RatPolynomial ZERO = new RatPolynomial();
    public static final RatPolynomial ONE = new RatPolynomial(
			                        BigRational.RNONE,
						new ExpVector()
                                                );

    public RatPolynomial(String s) { 
	this();
	// format  p/q (1,2) r/s (3,4) ...
	RatPolynomial erg = null;
	RatPolynomial mon = null;
	BigRational r;
	ExpVector u;
	String teil;
	int b = 0;
	int e = s.length();
	int k = s.indexOf('(');
	if ( k < 0 ) {
	    r = new BigRational( s.trim() );
	    u = new ExpVector( );
	    erg = new RatPolynomial( r, u );
	} else {
	    teil = s.substring(0,k);
	    r = new BigRational( teil.trim() );
	    b = k;
	    k = s.indexOf(')',b);
	    teil = s.substring(b,k+1);
	    u = new ExpVector( teil.trim() );
	    erg = new RatPolynomial( r, u );
	    b = k + 1;
	    while ( b <= e ) {
               k = s.indexOf('(',b);
	       if ( k < 0 ) break;
               teil = s.substring(b,k);
	       r = new BigRational( teil.trim() );
	       b = k;
	       k = s.indexOf(')',b);
	       teil = s.substring(b,k+1);
	       u = new ExpVector( teil.trim() );
	       mon = new RatPolynomial( r, u );
	       b = k + 1;
	       erg = DIRPSM( erg, mon );
	    }
	}
        horder = erg.horder;
        lorder = erg.lorder;
	val = erg.getMap();
    }

    public String toString(String[] v) { 
	StringBuffer erg = new StringBuffer();
        Set ent = val.entrySet();
        Iterator it = ent.iterator();
	if ( ! it.hasNext() ) return erg.toString();
        Map.Entry y = (Map.Entry) it.next();
        ExpVector f = (ExpVector) y.getKey(); 
	BigRational a = (BigRational) y.getValue();
	boolean neg = false;
        while ( true ) {
	    if ( neg ) {
	       erg.append(BigRational.RNNEG(a));
	    } else {
              erg.append(a);
	    }
            neg = false;
	    erg.append(" " + f.toString(v));
	    if ( it.hasNext() ) {
                y = (Map.Entry) it.next();
	        f = (ExpVector) y.getKey(); 
	        a = (BigRational) y.getValue();
		if ( BigRational.RNSIGN(a) < 0 ) {
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

    /**
     * Sum.
     */

    public static RatPolynomial DIRPSM(RatPolynomial Ap, RatPolynomial Bp) {  
        if ( Ap.evord != Bp.evord ) { 
           logger.error("orderings not equal"); 
        }

        Map cp = null;
	Map vp = Ap.getMap();
        if ( vp instanceof TreeMap ) 
           cp = (Map)((TreeMap)vp).clone();
        if ( vp instanceof LinkedHashMap ) 
           cp = (Map)((LinkedHashMap)vp).clone();
        Map C = cp; 

        Map B = Bp.getMap();
        Set Bk = B.entrySet();
        Iterator bi = Bk.iterator();
        while ( bi.hasNext() ) {
            Map.Entry y = (Map.Entry) bi.next();
	    ExpVector f = (ExpVector) y.getKey(); 
	    // System.out.println("f = " + f);

            BigRational b = (BigRational) y.getValue(); 
	    // System.out.println("b = " + b);

            BigRational c = (BigRational) C.remove( (Object) f );
            // System.out.println("c = " + c);

            if ( c != null ) { 
               c = BigRational.RNSUM(c,b);
               // System.out.println("c = " + c);

               if ( !c.equals(BigRational.RNZERO) ) { 
                  C.put( (Object) f, (Object) c ); 
               }
            }
            else { C.put( (Object) f, (Object) b ); };
        }
        return new RatPolynomial(C,Ap.evord);
    }


    /**
     * Difference.
     */

    public static RatPolynomial DIRPDF(RatPolynomial Ap, RatPolynomial Bp) {  
        if ( Ap.evord != Bp.evord ) { 
           logger.error("orderings not equal"); 
        }

        Map cp = null;
	Map vp = Ap.getMap();
        if ( vp instanceof TreeMap ) 
           cp = (Map)((TreeMap)vp).clone();
        if ( vp instanceof LinkedHashMap ) 
           cp = (Map)((LinkedHashMap)vp).clone();
        Map C = cp; 

        Map B = Bp.getMap();
        Set Bk = B.entrySet();
        Iterator bi = Bk.iterator();
        while ( bi.hasNext() ) {
            Map.Entry y = (Map.Entry) bi.next();
	    ExpVector f = (ExpVector) y.getKey(); 
            //System.out.println("f = " + f);

            BigRational b = (BigRational) y.getValue(); 
            //System.out.println("b = " + b);

            BigRational c = (BigRational) C.remove( (Object) f );
            //System.out.println("c = " + c);

            if ( c != null ) { 
               c = BigRational.RNDIF(c,b);
               //System.out.println("c = " + c);

               if (  BigRational.RNCOMP(c,BigRational.RNZERO) != 0 ) { C.put( (Object) f, (Object) c ); }
            }
            else { C.put( (Object) f, (Object) BigRational.RNNEG(b) ); };
        }
        return new RatPolynomial(C,Ap.evord);
    }


    /**
     * Product.
     */

    public static RatPolynomial DIRPPR(RatPolynomial Ap, RatPolynomial Bp) {  
        if ( Ap.evord != Bp.evord ) { 
           logger.error("orderings not equal"); 
        }

        RatPolynomial Cp = new RatPolynomial( Ap.evord, Ap.horder, Ap.lorder );
        Map C = Cp.getMap();

        Map A = Ap.getMap();
        Set Ak = A.entrySet();
        Iterator ai = Ak.iterator();

        Map B = Bp.getMap();
        Set Bk = B.entrySet();

        while ( ai.hasNext() ) {
            Map.Entry y = (Map.Entry) ai.next();
	    ExpVector e = (ExpVector) y.getKey(); 
            //System.out.println("e = " + e);

            BigRational a = (BigRational) y.getValue(); 
            //System.out.println("a = " + a);

            Iterator bi = Bk.iterator();
            while ( bi.hasNext() ) {
                Map.Entry x = (Map.Entry) bi.next();
	        ExpVector f = (ExpVector) x.getKey(); 
                //System.out.println("f = " + f);

                BigRational b = (BigRational) x.getValue(); 
                //System.out.println("b = " + b);

	        ExpVector g = ExpVector.EVSUM(e,f); 
                //System.out.println("g = " + g);
  
                BigRational c = BigRational.RNPROD(a,b);
                //System.out.println("c = " + c);

                BigRational d = (BigRational) C.get(g);
                //System.out.println("d = " + d);
		if ( d == null ) { 
                     d = c; 
		} else { 
                     d = d.add(c); 
                }
                //System.out.println("d+c = " + d);

                C.put( (Object) g, (Object) d );
            }
        }
        return Cp; //new RatPolynomial(C,Ap.evord);
    }


    /**
     * Product with number.
     */

    public static RatPolynomial DIRPRP(RatPolynomial Ap, BigRational b) {  

        RatPolynomial Cp = new RatPolynomial( Ap.evord, Ap.horder, Ap.lorder );
        Map C = Cp.getMap();

        Map A = Ap.getMap();
        Set Ak = A.entrySet();
        Iterator ai = Ak.iterator();

        while ( ai.hasNext() ) {
            Map.Entry y = (Map.Entry) ai.next();
	    ExpVector e = (ExpVector) y.getKey(); 
            //System.out.println("e = " + e);

            BigRational a = (BigRational) y.getValue(); 
            //System.out.println("a = " + a);

            BigRational c = BigRational.RNPROD(a,b);
            //System.out.println("c = " + c);
            C.put( (Object) e, (Object) c );
        }
        return Cp; //new RatPolynomial(C,Ap.evord);
    }


    /**
     * Negation.
     */

    public static RatPolynomial DIRPNG(RatPolynomial Ap) {  

        RatPolynomial Cp = new RatPolynomial( Ap.evord, Ap.horder, Ap.lorder );
        Map C = Cp.getMap();

        Map A = Ap.getMap();
        Set Ak = A.entrySet();
        Iterator ai = Ak.iterator();

        while ( ai.hasNext() ) {
            Map.Entry y = (Map.Entry) ai.next();
	    ExpVector e = (ExpVector) y.getKey(); 
            //System.out.println("e = " + e);

            BigRational a = (BigRational) y.getValue(); 
            //System.out.println("a = " + a);

            BigRational c = BigRational.RNNEG(a);
            //System.out.println("c = " + c);
            C.put( (Object) e, (Object) c );
        }
        return Cp; //new RatPolynomial(C,Ap.evord);
    }


    /**
     * Monic.
     */

    public static RatPolynomial DIRPMC(RatPolynomial Ap) {  

        RatPolynomial Cp = new RatPolynomial( Ap.evord, Ap.horder, Ap.lorder );
        Map C = Cp.getMap();

        BigRational b = null; // replaced by 1/lbcf

        Map A = Ap.getMap();
        Set Ak = A.entrySet();
        Iterator ai = Ak.iterator();

        while ( ai.hasNext() ) {
            Map.Entry y = (Map.Entry) ai.next();
	    ExpVector e = (ExpVector) y.getKey(); 
            //System.out.println("e = " + e);

            BigRational a = (BigRational) y.getValue(); 
            //System.out.println("a = " + a);

            if ( b == null ) b = BigRational.RNINV(a); 

            BigRational c = BigRational.RNPROD(a,b);
            //System.out.println("c = " + c);
            C.put( (Object) e, (Object) c );
        }
        return Cp; //new RatPolynomial(C,Ap.evord);
    }


    /**
     * One ?.
     */

    // abstract public boolean isONE()

    public static boolean DIRPON(RatPolynomial a) {  
	if ( a == null ) return false;
        return a.isONE();
    }


    /**
     * Leading monomial.
     */

    public static Map.Entry DIRPLM(RatPolynomial Ap) {  
	if ( Ap == null ) return null;
	return Ap.LM();
    }


    /**
     * Leading exponent vector.
     */

    public static ExpVector DIRPEV(RatPolynomial Ap) {  
	if ( Ap == null ) return null;
	return Ap.LEV();
    }



    /**
     * Leading base coefficient.
     */

    public static BigRational DIRLBC(RatPolynomial Ap) {  
	if ( Ap == null ) return null;
	return (BigRational) Ap.LBC();
    }


    /**
     * Random polynomial.
     */

    public static RatPolynomial DIRRAS(int r, int k, int l, int e, float q) {  
        RatPolynomial x = new RatPolynomial(r);
        Map C = x.getMap(); 
	if ( ! (C instanceof SortedMap) ) C = new TreeMap( x.horder );
        for (int i = 0; i < l; i++ ) { 
            ExpVector U = ExpVector.EVRAND(r,e,q);
            BigRational a = BigRational.RNRAND(k);
            if ( ! a.isZERO() ) C.put( U, a );
        }
	if ( x.getMap() instanceof LinkedHashMap) {
           C = new LinkedHashMap( C );
	   x.setMap( C );
	}
        return x; 
    }

}
