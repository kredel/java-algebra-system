/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigComplex;

import org.apache.log4j.Logger;

/**
 * BigComplex Solvable Ordered Map Polynomial. 
 * Extension of SolvableOrderedMapPolynomial with 
 * BigComplex Coefficients.
 * @author Heinz Kredel
 */

public class ComplexSolvableOrderedMapPolynomial 
             extends SolvableOrderedMapPolynomial {

    private static Logger logger = 
            Logger.getLogger(ComplexSolvableOrderedMapPolynomial.class);

    /**
     * Constructors for ComplexSolvableOrderedMapPolynomial
     */
    public ComplexSolvableOrderedMapPolynomial(RelationTable table) { 
        super(table);
    }

    public ComplexSolvableOrderedMapPolynomial(RelationTable table, 
                                           int r) { 
        super(table,r);
    }

    public ComplexSolvableOrderedMapPolynomial(RelationTable table, 
                                           Map t) { 
        super(table,t);
    }

    public ComplexSolvableOrderedMapPolynomial(RelationTable table, 
                                           String[] v) { 
        super(table,v);
    }

    public ComplexSolvableOrderedMapPolynomial(RelationTable table, 
                                           Coefficient a, 
                                           ExpVector e) { 
        super(table, a, e );
    }

    public ComplexSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to) { 
	super(table,to);
    }

    public ComplexSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to, 
                                           Map t) { 
	super(table,to,t);
    }

    public ComplexSolvableOrderedMapPolynomial(RelationTable table, 
                                           OrderedPolynomial o) { 
	super(table,o);
    }

    public ComplexSolvableOrderedMapPolynomial(RelationTable table, 
                                           UnorderedPolynomial u) { 
	super(table,u);
    }

    public ComplexSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to, 
                                           UnorderedPolynomial u) { 
	super(table,to,u);
    }

    public ComplexSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to, 
                                           OrderedPolynomial o) { 
	super(table,to,o);
    }

    public Object clone() { 
       ComplexSolvableOrderedMapPolynomial p;
       p = new ComplexSolvableOrderedMapPolynomial(table,order,val);
       p.setVars(vars);
       return p; 
    }

    public OrderedPolynomial getZERO() { 
       return new ComplexSolvableOrderedMapPolynomial(table);
    }

    public OrderedPolynomial getZERO(TermOrder to) { 
       return new ComplexSolvableOrderedMapPolynomial(table,to);
    }

    public OrderedPolynomial getZERO(RelationTable table, 
                                     TermOrder to) { 
       return new ComplexSolvableOrderedMapPolynomial(table,to);
    }

    public OrderedPolynomial getZERO(RelationTable table) { 
       return new ComplexSolvableOrderedMapPolynomial(table);
    }

    public OrderedPolynomial getONE() {
       return new ComplexSolvableOrderedMapPolynomial(table,
                                                  BigComplex.ONE,
                                 new ExpVector(numberOfVariables())); 
    }

    public OrderedPolynomial getONE(TermOrder to) { 
        return new ComplexSolvableOrderedMapPolynomial(table,
                                                   to,
                                                   getONE());
    }

    public OrderedPolynomial getONE(RelationTable table) { 
       return new ComplexSolvableOrderedMapPolynomial(table);
    }

    public OrderedPolynomial getONE(RelationTable table, 
                                    TermOrder to) { 
        return new ComplexSolvableOrderedMapPolynomial(table,
                                                   to,
                                                   getONE());
    }

    // begin wrong 
    public static final ComplexSolvableOrderedMapPolynomial ZERO = 
        new ComplexSolvableOrderedMapPolynomial((RelationTable)null);
    public static final ComplexSolvableOrderedMapPolynomial ONE = 
        new ComplexSolvableOrderedMapPolynomial((RelationTable)null,
                                            BigComplex.ONE,
                                            new ExpVector());
    // end wrong 


    /**
     * Random polynomial.
     */

    public static ComplexSolvableOrderedMapPolynomial DIRRAS(RelationTable table, 
                                                         int r, 
                                                         int k, 
                                                         int l, 
                                                         int e, 
                                                         float q) {  
        ComplexSolvableOrderedMapPolynomial x;
        x = new ComplexSolvableOrderedMapPolynomial(table,r); 
        Map C = x.getMap(); 
        for ( int i = 0; i < l; i++ ) { 
            ExpVector U = ExpVector.EVRAND(r,e,q);
            BigComplex c = (BigComplex) C.get( U );
            BigComplex a = BigComplex.CRAND(k);
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
