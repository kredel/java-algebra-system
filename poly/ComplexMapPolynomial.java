/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigComplex;

/**
 * BigComplex Map Polynomial. 
 * Extension of MapPolynomial with BigComplex Coefficients.
 * @author Heinz Kredel
 */

public class ComplexMapPolynomial extends MapPolynomial {

    /**
     * Constructors for ComplexMapPolynomial
     */
    public ComplexMapPolynomial() { 
        super();
    }

    public ComplexMapPolynomial(int r) { 
        super(r);
    }

    public ComplexMapPolynomial(Map t) { 
        super(t);
    }

    public ComplexMapPolynomial(String[] v) { 
        super(v);
    }

    public ComplexMapPolynomial(Coefficient a, ExpVector e) { 
        super( a, e );
    }


    public Object clone() { 
       ComplexMapPolynomial p = new ComplexMapPolynomial(val);
       p.setVars(vars);
       return p; 
    }

    public UnorderedPolynomial getZERO() { 
       return new ComplexMapPolynomial();
    }

    public UnorderedPolynomial getONE() { 
       return new ComplexMapPolynomial(BigComplex.ONE, new ExpVector()); 
    }

    public static final ComplexMapPolynomial ZERO = new ComplexMapPolynomial();
    public static final ComplexMapPolynomial ONE = new ComplexMapPolynomial(
                                                      BigComplex.ONE,
                                                      new ExpVector()
                                                      );

    /**
     * Random polynomial.
     */

    public static ComplexMapPolynomial DICRAS(int r, int k, int l, int e, float q) {  
        ComplexMapPolynomial x = new ComplexMapPolynomial(r);
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
