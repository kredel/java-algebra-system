/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.poly.WeylRelations;

import edu.jas.arith.BigRational;
import edu.jas.arith.Coefficient;

import org.apache.log4j.Logger;


/**
 * Generate Relation Table for Weyl Algebras
 * @author Heinz Kredel.
 */

public class WeylRelations {

    private static Logger logger = Logger.getLogger(WeylRelations.class);

    public RelationTable generate(int r) {
        RelationTable table = new RelationTable();
        if ( r <= 1 || (r % 2) != 0 ) {
           throw new IllegalArgumentException("WeylRelations, wrong r = "+r);
        }
        int m = r / 2;
        ExpVector z = new ExpVector(r);
        Coefficient one = BigRational.ONE;
        OrderedPolynomial a = new RatSolvableOrderedMapPolynomial(table,one,z);
        for ( int i = m; i < r; i++ ) {
            ExpVector f = new ExpVector(r,i,1); 
            int j = i - m;
            ExpVector e = new ExpVector(r,j,1);
            ExpVector ef = ExpVector.EVSUM(e,f);
            OrderedPolynomial b = new RatSolvableOrderedMapPolynomial(table,one,ef);
            OrderedPolynomial rel = a.add(b);
            table.update(e,f,rel);
        }
        if ( logger.isDebugEnabled() ) {
           logger.debug("\nWeyl relations = " + table);
        }
        return table;
    }

    public RelationTable generate(int r, SolvableOrderedMapPolynomial ref) {
        RelationTable table = new RelationTable();
        if ( r <= 1 || (r % 2) != 0 ) {
           throw new IllegalArgumentException("WeylRelations, wrong r = "+r);
        }
        int m = r / 2;
        ExpVector z = new ExpVector(r);
        OrderedPolynomial one = ref.getONE(table);
        OrderedPolynomial zero = ref.getZERO(table);
        // Coefficient one = a.leadingBaseCoefficient();
        for ( int i = m; i < r; i++ ) {
            ExpVector f = new ExpVector(r,i,1); 
            int j = i - m;
            ExpVector e = new ExpVector(r,j,1);
            ExpVector ef = ExpVector.EVSUM(e,f);
            OrderedPolynomial b = one.multiply(ef);
            OrderedPolynomial rel = one.add(b);
            table.update(e,f,rel);
        }
        if ( logger.isDebugEnabled() ) {
           logger.debug("\nWeyl relations = " + table);
        }
        return table;
    }

}
