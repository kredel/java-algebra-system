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
import edu.jas.arith.BigComplex;

/**
 * BigComplex Ordered Map Polynomial. 
 * Extension of OrderedMapPolynomial with BigComplex Coefficients.
 * @author Heinz Kredel
 */

public class ComplexOrderedMapPolynomial extends OrderedMapPolynomial {

    /**
     * Constructors for ComplexOrderedMapPolynomial
     */
    public ComplexOrderedMapPolynomial() { 
        super();
    }

    public ComplexOrderedMapPolynomial(int r) { 
        super(r);
    }

    public ComplexOrderedMapPolynomial(Map t) { 
        super(t);
    }

    public ComplexOrderedMapPolynomial(String[] v) { 
        super(v);
    }

    public ComplexOrderedMapPolynomial(Coefficient a, ExpVector e) { 
        super( a, e );
    }

    public ComplexOrderedMapPolynomial(TermOrder to) { 
	super(to);
    }

    public ComplexOrderedMapPolynomial(TermOrder to, Map t) { 
	super(to,t);
    }

    public ComplexOrderedMapPolynomial(OrderedPolynomial o) { 
	super(o);
    }

    public ComplexOrderedMapPolynomial(UnorderedPolynomial u) { 
	super(u);
    }

    public ComplexOrderedMapPolynomial(TermOrder to, UnorderedPolynomial u) { 
	super(to,u);
    }

    public ComplexOrderedMapPolynomial(TermOrder to, OrderedPolynomial o) { 
	super(to,o);
    }


    public Object clone() { 
       ComplexOrderedMapPolynomial p = new ComplexOrderedMapPolynomial(order,val);
       p.setVars(vars);
       return p; 
    }

    public OrderedPolynomial getZERO() { 
       return new ComplexOrderedMapPolynomial();
    }

    public OrderedPolynomial getZERO(TermOrder to) { 
       return new ComplexOrderedMapPolynomial(to);
    }

    public OrderedPolynomial getONE() {
       return new ComplexOrderedMapPolynomial(BigComplex.ONE,
                                          new ExpVector()); 
    }

    public OrderedPolynomial getONE(TermOrder to) { 
       return new ComplexOrderedMapPolynomial(to,ONE);
    }

    public static final ComplexOrderedMapPolynomial ZERO = 
                        new ComplexOrderedMapPolynomial();
    public static final ComplexOrderedMapPolynomial ONE = 
                        new ComplexOrderedMapPolynomial(BigComplex.ONE,
                                                    new ExpVector());

    /**
     * Random polynomial.
     */

    public static ComplexOrderedMapPolynomial DICRAS(int r, int k, int l, int e, float q) {
        ComplexOrderedMapPolynomial x = new ComplexOrderedMapPolynomial(r);
        Map C = x.getMap(); 
        for (int i = 0; i < l; i++ ) { 
            ExpVector U = ExpVector.EVRAND(r,e,q);
            BigComplex c = (BigComplex) C.get( U );
            BigComplex a = BigComplex.CRAND(k);
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
