/*
 * $Id$
 */

package edu.jas.poly;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;


/**
 * Generate Relation Table for Weyl Algebras
 * Adds the respective relations to the relation table
 * of the given solvable ring factory. Relations are of the 
 * form x<sub>j</sub> * x<sub>i</sub> = x<sub>i</sub> x<sub>j</sub> + 1.
 * @author Heinz Kredel.
 */

public class WeylRelations<C extends RingElem<C>> {


    /** The factory for the solvable polynomial ring. 
     */
    private final GenSolvablePolynomialRing<C> ring;


    private static Logger logger = Logger.getLogger(WeylRelations.class);


    /** The constructor requires a ring factory.
     * The relation table of this ring is setup to a Weyl Algebra.
     * @param r solvable polynomial ring factory,
     * r must have even number of variables.
     */
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


    /** Generates the relation table of this ring.
     */
    public void generate() {
        RelationTable<C> table = ring.table;
        int r = ring.nvar;
        int m =  r / 2;
        //ExpVector z = ring.evzero;
        GenSolvablePolynomial<C> one  = ring.getONE().clone();
        GenSolvablePolynomial<C> zero = ring.getZERO().clone();
        for ( int i = m; i < r; i++ ) {
            ExpVector f = new ExpVector(r,i,1); 
            int j = i - m;
            ExpVector e = new ExpVector(r,j,1);
            ExpVector ef = ExpVector.EVSUM(e,f);
            GenSolvablePolynomial<C> b = one.multiply(ef);
            GenSolvablePolynomial<C> rel 
                = (GenSolvablePolynomial<C>)b.sum(one);
            //                = (GenSolvablePolynomial<C>)b.subtract(one);
            if ( rel.isZERO() ) {
               logger.info("ring = " + ring);
               logger.info("one  = " + one);
               logger.info("zero = " + zero);
               logger.info("b    = " + b);
               logger.info("rel  = " + rel);
               System.exit(1);
            }
            //System.out.println("rel = " + rel.toString(ring.vars));
            table.update(e,f,rel);
        }
        if ( logger.isDebugEnabled() ) {
           logger.debug("\nWeyl relations = " + table);
        }
        return;
    }

}
