/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigRational;

import org.apache.log4j.Logger;

/**
 * BigRational Solvable Ordered Map Polynomial. 
 * Extension of SolvableOrderedMapPolynomial with 
 * BigRational Coefficients.
 * @author Heinz Kredel
 */

public class RatSolvableOrderedMapPolynomial 
             extends SolvableOrderedMapPolynomial {

    private static Logger logger = 
            Logger.getLogger(RatSolvableOrderedMapPolynomial.class);

    /**
     * Constructors for RatSolvableOrderedMapPolynomial
     */
    public RatSolvableOrderedMapPolynomial(RelationTable table) { 
        super(table);
    }

    public RatSolvableOrderedMapPolynomial(RelationTable table, 
                                           int r) { 
        super(table,r);
    }

    public RatSolvableOrderedMapPolynomial(RelationTable table, 
                                           Map t) { 
        super(table,t);
    }

    public RatSolvableOrderedMapPolynomial(RelationTable table, 
                                           String[] v) { 
        super(table,v);
    }

    public RatSolvableOrderedMapPolynomial(RelationTable table, 
                                           Coefficient a, 
                                           ExpVector e) { 
        super(table, a, e );
    }

    public RatSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to) { 
	super(table,to);
    }

    public RatSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to, 
                                           Map t) { 
	super(table,to,t);
    }

    public RatSolvableOrderedMapPolynomial(RelationTable table, 
                                           OrderedPolynomial o) { 
	super(table,o);
    }

    public RatSolvableOrderedMapPolynomial(RelationTable table, 
                                           UnorderedPolynomial u) { 
	super(table,u);
    }

    public RatSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to, 
                                           UnorderedPolynomial u) { 
	super(table,to,u);
    }

    public RatSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to, 
                                           OrderedPolynomial o) { 
	super(table,to,o);
    }

    public Object clone() { 
       RatSolvableOrderedMapPolynomial p;
       p = new RatSolvableOrderedMapPolynomial(table,order,val);
       p.setVars(vars);
       return p; 
    }

    public OrderedPolynomial getZERO() { 
       return new RatSolvableOrderedMapPolynomial(table);
    }

    public OrderedPolynomial getZERO(TermOrder to) { 
       return new RatSolvableOrderedMapPolynomial(table,to);
    }

    public SolvablePolynomial getZERO(RelationTable table, 
                                     TermOrder to) { 
       return new RatSolvableOrderedMapPolynomial(table,to);
    }

    public SolvablePolynomial getZERO(RelationTable table) { 
       return new RatSolvableOrderedMapPolynomial(table);
    }

    public OrderedPolynomial getONE() {
       return new RatSolvableOrderedMapPolynomial(table,
                                                  BigRational.ONE,
                                 new ExpVector(numberOfVariables())); 
    }

    public OrderedPolynomial getONE(TermOrder to) { 
        return new RatSolvableOrderedMapPolynomial(table,
                                                   to,
                                                   getONE());
    }

    public SolvablePolynomial getONE(RelationTable table) { 
       return new RatSolvableOrderedMapPolynomial(table);
    }

    public SolvablePolynomial getONE(RelationTable table, 
                                    TermOrder to) { 
        return new RatSolvableOrderedMapPolynomial(table,
                                                   to,
                                                   getONE());
    }

    // begin wrong 
    public static final RatSolvableOrderedMapPolynomial ZERO = 
        new RatSolvableOrderedMapPolynomial((RelationTable)null);
    public static final RatSolvableOrderedMapPolynomial ONE = 
        new RatSolvableOrderedMapPolynomial((RelationTable)null,
                                            BigRational.ONE,
                                            new ExpVector());
    // end wrong 


    /**
     * Random polynomial.
     */

    public static RatSolvableOrderedMapPolynomial DIRRAS(RelationTable table, 
                                                         int r, 
                                                         int k, 
                                                         int l, 
                                                         int e, 
                                                         float q) {  
        RatSolvableOrderedMapPolynomial x;
        x = new RatSolvableOrderedMapPolynomial(table,r); 
        Map C = x.getMap(); 
        for ( int i = 0; i < l; i++ ) { 
            ExpVector U = ExpVector.EVRAND(r,e,q);
            BigRational c = (BigRational) C.get( U );
            BigRational a = BigRational.RNRAND(k);
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
