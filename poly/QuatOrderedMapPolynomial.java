/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigQuaternion;

/**
 * BigQuaternion Ordered Map Polynomial. 
 * Extension of OrderedMapPolynomial with BigQuaternion Coefficients.
 * @author Heinz Kredel
 */

public class QuatOrderedMapPolynomial extends OrderedMapPolynomial {

    /**
     * Constructors for QuatOrderedMapPolynomial
     */
    public QuatOrderedMapPolynomial() { 
        super();
    }

    public QuatOrderedMapPolynomial(int r) { 
        super(r);
    }

    public QuatOrderedMapPolynomial(Map t) { 
        super(t);
    }

    public QuatOrderedMapPolynomial(String[] v) { 
        super(v);
    }

    public QuatOrderedMapPolynomial(Coefficient a, ExpVector e) { 
        super( a, e );
    }

    public QuatOrderedMapPolynomial(TermOrder to) { 
	super(to);
    }

    public QuatOrderedMapPolynomial(TermOrder to, Map t) { 
	super(to,t);
    }

    public QuatOrderedMapPolynomial(OrderedPolynomial o) { 
	super(o);
    }

    public QuatOrderedMapPolynomial(UnorderedPolynomial u) { 
	super(u);
    }

    public QuatOrderedMapPolynomial(TermOrder to, UnorderedPolynomial u) { 
	super(to,u);
    }

    public QuatOrderedMapPolynomial(TermOrder to, OrderedPolynomial o) { 
	super(to,o);
    }


    public Object clone() { 
       QuatOrderedMapPolynomial p = new QuatOrderedMapPolynomial(order,val);
       p.setVars(vars);
       return p; 
    }

    public OrderedPolynomial getZERO() { 
       return new QuatOrderedMapPolynomial();
    }

    public OrderedPolynomial getZERO(TermOrder to) { 
       return new QuatOrderedMapPolynomial(to);
    }

    public OrderedPolynomial getONE() {
       return new QuatOrderedMapPolynomial(BigQuaternion.ONE,
                                          new ExpVector()); 
    }

    public OrderedPolynomial getONE(TermOrder to) { 
       return new QuatOrderedMapPolynomial(to,ONE);
    }

    public static final QuatOrderedMapPolynomial ZERO = 
                        new QuatOrderedMapPolynomial();
    public static final QuatOrderedMapPolynomial ONE = 
                        new QuatOrderedMapPolynomial(BigQuaternion.ONE,
                                                    new ExpVector());

    /**
     * Random polynomial.
     */

    public static QuatOrderedMapPolynomial DIQRAS(int r, int k, int l, int e, float q) {  
        QuatOrderedMapPolynomial x = new QuatOrderedMapPolynomial(r);
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
