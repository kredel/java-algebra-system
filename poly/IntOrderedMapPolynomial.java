/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Random;

import org.apache.log4j.Logger;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigInteger;

/**
 * BigInteger Ordered Map Polynomial. 
 * Extension of OrderedMapPolynomial with BigInteger Coefficients.
 * @author Hienz Kredel
 */

public class IntOrderedMapPolynomial extends OrderedMapPolynomial {

    /**
     * Constructors for IntOrderedMapPolynomial
     */
    public IntOrderedMapPolynomial() { 
        super();
    }

    public IntOrderedMapPolynomial(int r) { 
        super(r);
    }

    public IntOrderedMapPolynomial(Map t) { 
        super(t);
    }

    public IntOrderedMapPolynomial(String[] v) { 
        super(v);
    }

    public IntOrderedMapPolynomial(Coefficient a, ExpVector e) { 
        super( a, e );
    }

    public IntOrderedMapPolynomial(TermOrder to) { 
	super(to);
    }

    public IntOrderedMapPolynomial(TermOrder to, Map t) { 
	super(to,t);
    }

    public IntOrderedMapPolynomial(OrderedPolynomial o) { 
	super(o);
    }

    public IntOrderedMapPolynomial(UnorderedPolynomial u) { 
	super(u);
    }

    public IntOrderedMapPolynomial(TermOrder to, UnorderedPolynomial u) { 
	super(to,u);
    }

    public IntOrderedMapPolynomial(TermOrder to, OrderedPolynomial o) { 
	super(to,o);
    }


    public Object clone() { 
       IntOrderedMapPolynomial p = new IntOrderedMapPolynomial(order,val);
       p.setVars(vars);
       return p; 
    }

    public OrderedPolynomial getZERO() { 
       return new IntOrderedMapPolynomial();
    }

    public OrderedPolynomial getZERO(TermOrder to) { 
       return new IntOrderedMapPolynomial(to);
    }

    public OrderedPolynomial getONE() {
       return new IntOrderedMapPolynomial(BigInteger.ONE,
                                          new ExpVector()); 
    }

    public OrderedPolynomial getONE(TermOrder to) { 
       return new IntOrderedMapPolynomial(to,ONE);
    }

    public static final IntOrderedMapPolynomial ZERO = 
                        new IntOrderedMapPolynomial();
    public static final IntOrderedMapPolynomial ONE = 
                        new IntOrderedMapPolynomial(BigInteger.ONE,
                                                    new ExpVector());


    public String toString(String[] v) { 
        StringBuffer erg = new StringBuffer();
        Set ent = val.entrySet();
        Iterator it = ent.iterator();
        if ( ! it.hasNext() ) return erg.toString();
        Map.Entry y = (Map.Entry) it.next();
        ExpVector f = (ExpVector) y.getKey(); 
        BigInteger a = (BigInteger) y.getValue();
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
                a = (BigInteger) y.getValue();
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

    private final static Random random = new Random(); // 

    public static IntOrderedMapPolynomial DIIRAS(int r, int k, int l, int e, float q) {  
        IntOrderedMapPolynomial x = new IntOrderedMapPolynomial(r);
        Map C = x.getMap(); 
        for (int i = 0; i < l; i++ ) { 
            ExpVector U = ExpVector.EVRAND(r,e,q);
            BigInteger c = (BigInteger) C.get( U );
            BigInteger a = BigInteger.IRAND(k);
	    if ( random.nextFloat() < 0.4f ) a = (BigInteger)a.negate();
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
