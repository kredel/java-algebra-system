/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigInteger;

import org.apache.log4j.Logger;

/**
 * BigInteger Solvable Ordered Map Polynomial. 
 * Extension of SolvableOrderedMapPolynomial with 
 * BigInteger Coefficients.
 * @author Heinz Kredel
 */

public class IntSolvableOrderedMapPolynomial 
             extends SolvableOrderedMapPolynomial {

    private static Logger logger = 
            Logger.getLogger(IntSolvableOrderedMapPolynomial.class);

    /**
     * Constructors for IntSolvableOrderedMapPolynomial
     */
    public IntSolvableOrderedMapPolynomial(RelationTable table) { 
        super(table);
    }

    public IntSolvableOrderedMapPolynomial(RelationTable table, 
                                           int r) { 
        super(table,r);
    }

    public IntSolvableOrderedMapPolynomial(RelationTable table, 
                                           Map t) { 
        super(table,t);
    }

    public IntSolvableOrderedMapPolynomial(RelationTable table, 
                                           String[] v) { 
        super(table,v);
    }

    public IntSolvableOrderedMapPolynomial(RelationTable table, 
                                           Coefficient a, 
                                           ExpVector e) { 
        super(table, a, e );
    }

    public IntSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to) { 
	super(table,to);
    }

    public IntSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to, 
                                           Map t) { 
	super(table,to,t);
    }

    public IntSolvableOrderedMapPolynomial(RelationTable table, 
                                           OrderedPolynomial o) { 
	super(table,o);
    }

    public IntSolvableOrderedMapPolynomial(RelationTable table, 
                                           UnorderedPolynomial u) { 
	super(table,u);
    }

    public IntSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to, 
                                           UnorderedPolynomial u) { 
	super(table,to,u);
    }

    public IntSolvableOrderedMapPolynomial(RelationTable table, 
                                           TermOrder to, 
                                           OrderedPolynomial o) { 
	super(table,to,o);
    }

    public Object clone() { 
       IntSolvableOrderedMapPolynomial p;
       p = new IntSolvableOrderedMapPolynomial(table,order,val);
       p.setVars(vars);
       return p; 
    }

    public OrderedPolynomial getZERO() { 
       return new IntSolvableOrderedMapPolynomial(table);
    }

    public OrderedPolynomial getZERO(TermOrder to) { 
       return new IntSolvableOrderedMapPolynomial(table,to);
    }

    public SolvablePolynomial getZERO(RelationTable table, 
                                     TermOrder to) { 
       return new IntSolvableOrderedMapPolynomial(table,to);
    }

    public SolvablePolynomial getZERO(RelationTable table) { 
       return new IntSolvableOrderedMapPolynomial(table);
    }

    public OrderedPolynomial getONE() {
       return new IntSolvableOrderedMapPolynomial(table,
                                                  BigInteger.ONE,
                                 new ExpVector(numberOfVariables())); 
    }

    public OrderedPolynomial getONE(TermOrder to) { 
        return new IntSolvableOrderedMapPolynomial(table,
                                                   to,
                                                   getONE());
    }

    public SolvablePolynomial getONE(RelationTable table) { 
       return new IntSolvableOrderedMapPolynomial(table);
    }

    public SolvablePolynomial getONE(RelationTable table, 
                                    TermOrder to) { 
        return new IntSolvableOrderedMapPolynomial(table,
                                                   to,
                                                   getONE());
    }

    // begin wrong 
    public static final IntSolvableOrderedMapPolynomial ZERO = 
        new IntSolvableOrderedMapPolynomial((RelationTable)null);
    public static final IntSolvableOrderedMapPolynomial ONE = 
        new IntSolvableOrderedMapPolynomial((RelationTable)null,
                                            BigInteger.ONE,
                                            new ExpVector());
    // end wrong 


    /**
     * Random polynomial.
     */

    public static IntSolvableOrderedMapPolynomial DIIRAS(RelationTable table, 
                                                         int r, 
                                                         int k, 
                                                         int l, 
                                                         int e, 
                                                         float q) {  
        IntSolvableOrderedMapPolynomial x;
        x = new IntSolvableOrderedMapPolynomial(table,r); 
        Map C = x.getMap(); 
        for ( int i = 0; i < l; i++ ) { 
            ExpVector U = ExpVector.EVRAND(r,e,q);
            BigInteger c = (BigInteger) C.get( U );
            BigInteger a = BigInteger.IRAND(k);
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
