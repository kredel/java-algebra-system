/*
 * $Id$
 */
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

public class RatPolynomial extends Polynomial {

    public RatPolynomial(int r) { 
	super(r);
    }

    public RatPolynomial(int r, int eo) { 
	super(r,eo);
    }

    public RatPolynomial(TreeMap t, int eo) { 
	super(t,eo);
    }

    public RatPolynomial(TreeMap t, int eo, String[] v) { 
	super(t,eo,v);
    }

    public RatPolynomial(String[] v, int eo) { 
	super(v,eo);
    }

    public RatPolynomial(TreeMap t) { 
	super(t);
    }

    public RatPolynomial( BigRational a, ExpVector e) { 
	super(a,e);
    }

    public Object clone() { 
       return new RatPolynomial( (TreeMap)val.clone(), evord, vars); 
    }

    public Polynomial getZERO() { 
       return (Polynomial) ZERO.clone();
    }

    public Polynomial getONE() { 
       return (Polynomial) ONE.clone(); 
    }

    public static final RatPolynomial ZERO = new RatPolynomial(0);
    public static final RatPolynomial ONE = new RatPolynomial(
			                        BigRational.RNONE,
						new ExpVector()
                                                );

    public RatPolynomial(String s) { 
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
        ord = erg.ord;
        ordm = erg.ordm;
	val = erg.getval();
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
        if ( Ap.evord != Bp.evord ) { System.out.println("ERROR"); }

        TreeMap C = (TreeMap) (Ap.getval()).clone();

        TreeMap B = Bp.getval();
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
        if ( Ap.evord != Bp.evord ) { System.out.println("ERROR"); }

        TreeMap C = (TreeMap) (Ap.getval()).clone();

        TreeMap B = Bp.getval();
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
        if ( Ap.evord != Bp.evord ) { System.out.println("ERROR"); }

        RatPolynomial Cp = new RatPolynomial( (TreeMap)null, Ap.evord);
        TreeMap C = new TreeMap(Cp.ord);

        TreeMap A = Ap.getval();
        Set Ak = A.entrySet();
        Iterator ai = Ak.iterator();

        TreeMap B = Bp.getval();
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
                C.put( (Object) g, (Object) c );
            }
        }
        return new RatPolynomial(C,Ap.evord);
    }


    /**
     * Product with number.
     */

    public static RatPolynomial DIRPRP(RatPolynomial Ap, BigRational b) {  

        RatPolynomial Cp = new RatPolynomial( (TreeMap)null, Ap.evord);
        TreeMap C = new TreeMap(Cp.ord);

        TreeMap A = Ap.getval();
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
        return new RatPolynomial(C,Ap.evord);
    }


    /**
     * Negation.
     */

    public static RatPolynomial DIRPNG(RatPolynomial Ap) {  

        RatPolynomial Cp = new RatPolynomial( (TreeMap)null, Ap.evord);
        TreeMap C = new TreeMap(Cp.ord);

        TreeMap A = Ap.getval();
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
        return new RatPolynomial(C,Ap.evord);
    }


    /**
     * Monic.
     */

    public static RatPolynomial DIRPMC(RatPolynomial Ap) {  

        RatPolynomial Cp = new RatPolynomial( (TreeMap)null, Ap.evord);
        TreeMap C = new TreeMap(Cp.ord);

        BigRational b = null; // replaced by 1/lbcf

        TreeMap A = Ap.getval();
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
        return new RatPolynomial(C,Ap.evord);
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

        RatPolynomial x = new RatPolynomial(r,ExpVector.INVLEX);
        Comparator ord = x.ord;
        TreeMap C = new TreeMap(ord);

        for (int i = 0; i < l; i++ ) { 
            ExpVector U = ExpVector.EVRAND(r,e,q);
            BigRational a = BigRational.RNRAND(k);
            if ( ! a.isZERO() ) C.put( U, a );
        }
        return new RatPolynomial(C,ExpVector.INVLEX);
    }

}
