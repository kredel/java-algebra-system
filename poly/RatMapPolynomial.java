/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigRational;

/**
 * BigRational Map Polynomial. 
 * Extension of MapPolynomial with BigRational Coefficients.
 */

public class RatMapPolynomial extends MapPolynomial {

    /**
     * Constructors for RatMapPolynomial
     */
    public RatMapPolynomial() { 
	super();
    }

    public RatMapPolynomial(int r) { 
	super(r);
    }

    public RatMapPolynomial(Map t) { 
	super(t);
    }

    public RatMapPolynomial(String[] v) { 
	super(v);
    }

    public RatMapPolynomial(Coefficient a, ExpVector e) { 
	super( a, e );
    }


    public Object clone() { 
       RatMapPolynomial p = new RatMapPolynomial(val);
       p.setVars(vars);
       return p; 
    }

    public UnorderedPolynomial getZERO() { 
       return (UnorderedPolynomial) ZERO.clone();
    }

    public UnorderedPolynomial getONE() { 
       return (UnorderedPolynomial) ONE.clone(); 
    }

    public static final RatMapPolynomial ZERO = new RatMapPolynomial();
    public static final RatMapPolynomial ONE = new RatMapPolynomial(
			                              BigRational.ONE,
					              new ExpVector()
                                                      );

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
	       erg.append( a.negate() );
	    } else {
              erg.append(a);
	    }
            neg = false;
	    erg.append(" " + f.toString(v));
	    if ( it.hasNext() ) {
                y = (Map.Entry) it.next();
	        f = (ExpVector) y.getKey(); 
	        a = (BigRational) y.getValue();
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


    /**
     * Random polynomial.
     */

    public static RatMapPolynomial DIRRAS(int r, int k, int l, int e, float q) {  
        RatMapPolynomial x = new RatMapPolynomial(r);
        Map C = x.getMap(); 
        for (int i = 0; i < l; i++ ) { 
            ExpVector U = ExpVector.EVRAND(r,e,q);
	    BigRational c = (BigRational) C.get( U );
            BigRational a = BigRational.RNRAND(k);
	    // System.out.println("rat random U = " + U + " c = " + c + " a = " +a);
            if ( ! a.isZERO() ) {
		if ( c == null ) {
                   C.put( U, a );
		} else {
                   C.put( U, c.add(a) );
		}
	    }
        }
	//System.out.println("rat random = " + x);
        return x; 
    }

}
