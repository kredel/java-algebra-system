/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigQuaternion;

import org.apache.log4j.Logger;

/**
 * BigQuaternion Solvable Ordered Map Polynomial. 
 * Extension of SolvableOrderedMapPolynomial with 
 * BigQuaternion Coefficients.
 * @author Heinz Kredel
 */

public class QuatSolvableOrderedMapPolynomial 
             extends SolvableOrderedMapPolynomial {

    private static Logger logger = 
            Logger.getLogger(QuatSolvableOrderedMapPolynomial.class);

    /**
     * Constructors for QuatSolvableOrderedMapPolynomial
     */
    public QuatSolvableOrderedMapPolynomial(RelationTable table) { 
        super(table);
    }

    public QuatSolvableOrderedMapPolynomial(RelationTable table, 
                                           int r) { 
        super(table,r);
    }

    public QuatSolvableOrderedMapPolynomial(RelationTable table, 
                                           Map t) { 
        super(table,t);
    }

    public QuatSolvableOrderedMapPolynomial(RelationTable table, 
                                           String[] v) { 
        super(table,v);
    }

    public QuatSolvableOrderedMapPolynomial(RelationTable table, 
                                           Coefficient a, 
                                           ExpVector e) { 
        super(table, a, e );
    }

    public QuatSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to) { 
	super(table,to);
    }

    public QuatSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to, 
                                           Map t) { 
	super(table,to,t);
    }

    public QuatSolvableOrderedMapPolynomial(RelationTable table, 
                                           OrderedPolynomial o) { 
	super(table,o);
    }

    public QuatSolvableOrderedMapPolynomial(RelationTable table, 
                                           UnorderedPolynomial u) { 
	super(table,u);
    }

    public QuatSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to, 
                                           UnorderedPolynomial u) { 
	super(table,to,u);
    }

    public QuatSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to, 
                                           OrderedPolynomial o) { 
	super(table,to,o);
    }

    public Object clone() { 
       QuatSolvableOrderedMapPolynomial p;
       p = new QuatSolvableOrderedMapPolynomial(table,order,val);
       p.setVars(vars);
       return p; 
    }

    public OrderedPolynomial getZERO() { 
       return new QuatSolvableOrderedMapPolynomial(table);
    }

    public OrderedPolynomial getZERO(TermOrder to) { 
       return new QuatSolvableOrderedMapPolynomial(table,to);
    }

    public SolvablePolynomial getZERO(RelationTable table, 
                                     TermOrder to) { 
       return new QuatSolvableOrderedMapPolynomial(table,to);
    }

    public SolvablePolynomial getZERO(RelationTable table) { 
       return new QuatSolvableOrderedMapPolynomial(table);
    }

    public OrderedPolynomial getONE() {
       return new QuatSolvableOrderedMapPolynomial(table,
                                                  BigQuaternion.ONE,
                                 new ExpVector(numberOfVariables())); 
    }

    public OrderedPolynomial getONE(TermOrder to) { 
        return new QuatSolvableOrderedMapPolynomial(table,
                                                   to,
                                                   getONE());
    }

    public SolvablePolynomial getONE(RelationTable table) { 
       return new QuatSolvableOrderedMapPolynomial(table,
                                                   getONE());
    }

    public SolvablePolynomial getONE(RelationTable table, 
                                    TermOrder to) { 
        return new QuatSolvableOrderedMapPolynomial(table,
                                                   to,
                                                   getONE());
    }

    // begin wrong 
    public static final QuatSolvableOrderedMapPolynomial ZERO = 
        new QuatSolvableOrderedMapPolynomial((RelationTable)null);
    public static final QuatSolvableOrderedMapPolynomial ONE = 
        new QuatSolvableOrderedMapPolynomial((RelationTable)null,
                                            BigQuaternion.ONE,
                                            new ExpVector());
    // end wrong 


    /**
     * Random polynomial.
     */

    public static QuatSolvableOrderedMapPolynomial DIQRAS(RelationTable table, 
                                                         int r, 
                                                         int k, 
                                                         int l, 
                                                         int e, 
                                                         float q) {  
        QuatSolvableOrderedMapPolynomial x;
        x = new QuatSolvableOrderedMapPolynomial(table,r); 
        Map C = x.getMap(); 
        for ( int i = 0; i < l; i++ ) { 
            ExpVector U = ExpVector.EVRAND(r,e,q);
            BigQuaternion c = (BigQuaternion) C.get( U );
            BigQuaternion a = BigQuaternion.QRAND(k);
            if ( ! a.isZERO() ) {
                if ( c == null ) {
                   C.put( U, a );
                } else {
                   C.put( U, c.add(a) );
                }
            }
        }
        if ( logger.isDebugEnabled() ) {
           logger.debug("rat random = " + x);
        }
        return x; 
    }

}
