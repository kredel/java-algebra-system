/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigRational;

/**
 * BigRational Map Polynomial. 
 * Extension of MapPolynomial with BigRational Coefficients.
 * @author Heinz Kredel
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
       return new RatMapPolynomial();
    }

    public UnorderedPolynomial getONE() { 
       return new RatMapPolynomial(BigRational.ONE, new ExpVector()); 
    }

    public static final RatMapPolynomial ZERO = new RatMapPolynomial();
    public static final RatMapPolynomial ONE = new RatMapPolynomial(
                                                      BigRational.ONE,
                                                      new ExpVector()
                                                      );

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
