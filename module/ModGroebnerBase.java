/*
 * $Id$
 */

package edu.jas.module;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolynomialList;

import edu.jas.poly.GenSolvablePolynomial;

import edu.jas.ring.Reduction;
import edu.jas.ring.GroebnerBase;

import edu.jas.module.ModuleList;


/**
 * Module Groebner Bases class.
 * Implements Groebner bases and GB test.
 * @author Heinz Kredel
 */

public class ModGroebnerBase  {

    private static final Logger logger = Logger.getLogger(ModGroebnerBase.class);

    /**
     * Module Groebner base test
     */

    public static <C extends RingElem<C>> 
           boolean isGB(int modv, List<GenPolynomial<C>> F) {  
        return GroebnerBase.isGB(modv,F);
    }

    public static <C extends RingElem<C>>
           boolean isGB(ModuleList<C> M) {  
        if ( M == null || M.list == null ) {
            return true;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return true;
        }
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        return GroebnerBase.isGB(modv,F.list);
    }


    /**
     * Groebner base using pairlist class.
     */

    public static <C extends RingElem<C>>
           ArrayList<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {  
        return GroebnerBase.GB(modv,F);
    }

    public static <C extends RingElem<C>>
           ModuleList<C> GB(ModuleList<C> M) {  
        ModuleList<C> N = M;
        if ( M == null || M.list == null ) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }

        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols;
        List<GenPolynomial<C>> G = GroebnerBase.GB(modv,F.list);
        F = new PolynomialList<C>(F.ring,G);
        N = F.getModuleList(modv);
        return N;
    }


}
