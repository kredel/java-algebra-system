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
 * BigRational Ordered Map Polynomial. 
 * Extension of OrderedMapPolynomial with BigRational Coefficients.
 * @author Hienz Kredel
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
       return new RatOrderedMapPolynomial();
    }

    public OrderedPolynomial getZERO(TermOrder to) { 
       return new RatOrderedMapPolynomial(to);
    }

    public OrderedPolynomial getONE() {
       return new RatOrderedMapPolynomial(BigRational.ONE,
                                          new ExpVector()); 
    }

    public OrderedPolynomial getONE(TermOrder to) { 
       return new RatOrderedMapPolynomial(to,ONE);
    }

    public static final RatOrderedMapPolynomial ZERO = 
                        new RatOrderedMapPolynomial();
    public static final RatOrderedMapPolynomial ONE = 
                        new RatOrderedMapPolynomial(BigRational.ONE,
                                                    new ExpVector());


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
