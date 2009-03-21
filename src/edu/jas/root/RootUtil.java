/*
 * $Id$
 */

package edu.jas.root;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;

import edu.jas.util.ListUtil;


/**
 * Real root utilities. 
 * For example real root count.
 * @author Heinz Kredel
 */
public class RootUtil {


    private static final Logger logger = Logger.getLogger(RootUtil.class);
    private static boolean debug = logger.isDebugEnabled();


    /**
     * Count changes in sign. 
     * @param <C> coefficient type.
     * @param L list of coefficients.
     * @return number of sign changes in L.
     */
    public static <C extends RingElem<C>> long signVar( List<C> L ) {
        long v = 0;
        if ( L == null || L.isEmpty() ) {
            return v;
        }
        C A = L.get(0);
        for ( int i = 1; i < L.size(); i++ ) {
            C B = L.get(i);
            while ( B == null || B.signum() == 0 ) {
                i++;
                if ( i >= L.size() ) {
                    return v;
                }
                B = L.get(i);
            }
            if ( A.signum() * B.signum() < 0 ) {
                v++;
            }
            A = B;
        }
        return v;
    }

}
