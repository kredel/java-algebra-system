/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigQuaternion;

/**
 * BigQuaternion Map Polynomial. 
 * Extension of MapPolynomial with BigQuaternion Coefficients.
 * @author Heinz Kredel
 */

public class QuatMapPolynomial extends MapPolynomial {

    /**
     * Constructors for QuatMapPolynomial
     */
    public QuatMapPolynomial() { 
        super();
    }

    public QuatMapPolynomial(int r) { 
        super(r);
    }

    public QuatMapPolynomial(Map t) { 
        super(t);
    }

    public QuatMapPolynomial(String[] v) { 
        super(v);
    }

    public QuatMapPolynomial(Coefficient a, ExpVector e) { 
        super( a, e );
    }


    public Object clone() { 
       QuatMapPolynomial p = new QuatMapPolynomial(val);
       p.setVars(vars);
       return p; 
    }

    public UnorderedPolynomial getZERO() { 
       return new QuatMapPolynomial();
    }

    public UnorderedPolynomial getONE() { 
       return new QuatMapPolynomial(BigQuaternion.ONE, new ExpVector()); 
    }

    public static final QuatMapPolynomial ZERO = new QuatMapPolynomial();
    public static final QuatMapPolynomial ONE = new QuatMapPolynomial(
                                                      BigQuaternion.ONE,
                                                      new ExpVector()
                                                      );

    /**
     * Random polynomial.
     */

    public static QuatMapPolynomial DIQRAS(int r, int k, int l, int e, float q) {  
        QuatMapPolynomial x = new QuatMapPolynomial(r);
        Map C = x.getMap(); 
        for (int i = 0; i < l; i++ ) { 
            ExpVector U = ExpVector.EVRAND(r,e,q);
            BigQuaternion c = (BigQuaternion) C.get( U );
            BigQuaternion a = BigQuaternion.QRAND(k);
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
