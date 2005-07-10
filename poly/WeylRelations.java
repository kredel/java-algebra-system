/*
 * $Id$
 */

package edu.jas.poly;

import edu.jas.arith.BigRational;
import edu.jas.structure.RingElem;

import org.apache.log4j.Logger;


/**
 * Generate Relation Table for Weyl Algebras
 * @author Heinz Kredel.
 */

public class WeylRelations<C extends RingElem<C>> {

    private static Logger logger = Logger.getLogger(WeylRelations.class);

    private final GenSolvablePolynomialRing<C> ring;

    public WeylRelations(GenSolvablePolynomialRing<C> r) {
        if ( r == null ) {
           throw new IllegalArgumentException("WeylRelations, ring == null");
        }
        ring = r;
        if ( ring.nvar <= 1 || (ring.nvar % 2) != 0 ) {
           throw new IllegalArgumentException("WeylRelations, wrong nvar = "
                                              + ring.nvar);
        }
    }

    public void generate() {
        RelationTable<C> table = ring.table;
        int r = ring.nvar;
        int m =  r / 2;
        ExpVector z = ring.evzero;
        GenSolvablePolynomial<C> one = ring.getONE().clone();
        GenSolvablePolynomial<C> zero = ring.getZERO().clone();
        for ( int i = m; i < r; i++ ) {
            ExpVector f = new ExpVector(r,i,1); 
            int j = i - m;
            ExpVector e = new ExpVector(r,j,1);
            ExpVector ef = ExpVector.EVSUM(e,f);
            GenSolvablePolynomial<C> b = one.multiply(ef);
            GenSolvablePolynomial<C> rel 
               = (GenSolvablePolynomial<C>)one.add(b);
            if ( rel.isZERO() ) {
               logger.info("ring = " + ring);
               logger.info("one  = " + one);
               logger.info("zero = " + zero);
               logger.info("b    = " + b);
               logger.info("rel  = " + rel);
               System.exit(1);
            }
            table.update(e,f,rel);
        }
        if ( logger.isDebugEnabled() ) {
           logger.debug("\nWeyl relations = " + table);
        }
        return;
    }

}
