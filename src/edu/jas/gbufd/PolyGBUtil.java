/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.gb.GroebnerBaseAbstract;


/**
 * Polynomial gbufd utilities.
 * @author Heinz Kredel
 */

public class PolyGBUtil {


    private static final Logger logger = Logger.getLogger(PolyGBUtil.class);


    private static boolean debug = logger.isDebugEnabled();


    /**
     * Test for resultant. 
     * @param A generic polynomial.
     * @param B generic polynomial.
     * @param r generic polynomial.
     * @return true if r == res(A,B), else false.
     */
    public static <C extends GcdRingElem<C>> boolean isResultant(GenPolynomial<C> A, GenPolynomial<C> B, GenPolynomial<C> r) {
        if ( r == null || r.isZERO() ) {
            return true;
        }
        GroebnerBaseAbstract<C> bb = GBFactory.<C>getImplementation(r.ring.coFac);
        List<GenPolynomial<C>> F = new ArrayList<GenPolynomial<C>>(2); 
        F.add(A);
        F.add(B);
        List<GenPolynomial<C>> G = bb.GB(F);
        System.out.println("G = " + G);
        GenPolynomial<C> n = bb.red.normalform(G,r);
        //System.out.println("n = " + n);
        return n.isZERO();
    }

}
