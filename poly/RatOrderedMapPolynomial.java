/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigRational;

/**
 * BigRational Ordered Map Polynomial. 
 * Extension of OrderedMapPolynomial with BigRational Coefficients.
 * @author Heinz Kredel
 */

public class RatOrderedMapPolynomial extends OrderedMapPolynomial {

    /**
     * Constructors for RatOrderedMapPolynomial
     */
    public RatOrderedMapPolynomial() { 
        super();
    }

    public RatOrderedMapPolynomial(int r) { 
        super(r);
    }

    public RatOrderedMapPolynomial(Map t) { 
        super(t);
    }

    public RatOrderedMapPolynomial(String[] v) { 
        super(v);
    }

    public RatOrderedMapPolynomial(Coefficient a, ExpVector e) { 
        super( a, e );
    }

    public RatOrderedMapPolynomial(TermOrder to) { 
	super(to);
    }

    public RatOrderedMapPolynomial(TermOrder to, Map t) { 
	super(to,t);
    }

    public RatOrderedMapPolynomial(OrderedPolynomial o) { 
	super(o);
    }

    public RatOrderedMapPolynomial(UnorderedPolynomial u) { 
	super(u);
    }

    public RatOrderedMapPolynomial(TermOrder to, UnorderedPolynomial u) { 
	super(to,u);
    }

    public RatOrderedMapPolynomial(TermOrder to, OrderedPolynomial o) { 
	super(to,o);
    }

    public Object clone() { 
       RatOrderedMapPolynomial p = new RatOrderedMapPolynomial(order,val);
       p.setVars(vars);
       return p; 
    }

    public OrderedPolynomial getZERO() { 
       return new RatOrderedMapPolynomial(order);
    }

    public OrderedPolynomial getZERO(TermOrder to) { 
       return new RatOrderedMapPolynomial(to);
    }

    public OrderedPolynomial getONE() {
        return getZERO().add(BigRational.ONE,
                             new ExpVector(numberOfVariables())); 
        // return new RatOrderedMapPolynomial(BigRational.ONE,
        //                new ExpVector(numberOfVariables())); 
    }

    public OrderedPolynomial getONE(TermOrder to) { 
        return new RatOrderedMapPolynomial(to,getONE());
    }

    public static final RatOrderedMapPolynomial ZERO = 
                        new RatOrderedMapPolynomial();
    public static final RatOrderedMapPolynomial ONE = 
                        new RatOrderedMapPolynomial(BigRational.ONE,
                                                    new ExpVector());


    /**
     * polynomial from string.
     * simple format:  p/q (1,2) r/s (3,4) ...
     * @see PolynomialTokenizer
     */

    public static RatOrderedMapPolynomial fromString(String s) { 
	RatOrderedMapPolynomial erg = null;
	BigRational r;
	ExpVector u;
	String teil;
	int b = 0;
	int e = s.length();
	int k = s.indexOf('(');
	if ( k < 0 ) {
	    r = new BigRational( s.trim() );
	    u = new ExpVector( );
	    erg = new RatOrderedMapPolynomial( r, u );
	} else {
	    teil = s.substring(0,k);
	    r = new BigRational( teil.trim() );
	    b = k;
	    k = s.indexOf(')',b);
	    teil = s.substring(b,k+1);
	    u = new ExpVector( teil.trim() );
	    erg = new RatOrderedMapPolynomial( r, u );
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
	       b = k + 1;
	       erg = (RatOrderedMapPolynomial) erg.add( r, u );
	    }
	}
	return erg; //val = (SortedMap) erg.getMap();
    }

    /**
     * Random polynomial.
     */

    public static RatOrderedMapPolynomial DIRRAS(int r, int k, int l, int e, float q) {  
        RatOrderedMapPolynomial x = new RatOrderedMapPolynomial(r);
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
