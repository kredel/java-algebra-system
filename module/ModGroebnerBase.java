/*
 * $Id$
 */

package edu.jas.module;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.PolynomialList;

import edu.jas.ring.Reduction;
import edu.jas.ring.GroebnerBase;

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

    public static boolean isGB(int modv, List F) {  
        return GroebnerBase.isDIRPGB(modv,F);
    }

    public static boolean isGB(ModuleList M) {  
        PolynomialList F = M.getPolynomialList();
        int modv = ((List)M.list.get(0)).size();
        return GroebnerBase.isDIRPGB(modv,F.list);
    }


    /**
     * Groebner base using pairlist class.
     */

    public static ArrayList GB(int modv, List F) {  
        return GroebnerBase.DIRPGB(modv,F);
    }

    public static ModuleList GB(ModuleList M) {  
        PolynomialList F = M.getPolynomialList();
        ModuleList N = M;
        List t = (List)M.list;
        int modv = 0;
        if ( t == null || t.size() == 0 ) {
           return N;
        }
        modv = ((List)t.get(0)).size();
        List G = GroebnerBase.DIRPGB(modv,F.list);
        F = new PolynomialList(F.coeff,F.vars,F.tord,G,F.table);
        N = ModuleList.getModuleList(modv,F);
        return N;
    }


}
