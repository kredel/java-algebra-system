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
//import edu.jas.poly.SolvableOrderedPolynomial;
import edu.jas.poly.PolynomialList;

import edu.jas.ring.Reduction;
import edu.jas.ring.SolvableGroebnerBase;

/**
 * Module Groebner Bases class.
 * Implements Groebner bases and GB test.
 * @author Heinz Kredel
 */

public class ModSolvableGroebnerBase  {

    private static final Logger logger = Logger.getLogger(ModSolvableGroebnerBase.class);

    /**
     * Module left Groebner base test
     */

    public static boolean isLeftGB(int modv, List F) {  
        return SolvableGroebnerBase.isLeftGB(modv,F);
    }

    public static boolean isLeftGB(ModuleList M) {  
        if ( M == null ) {
            return true;
        }
        if ( M.list == null ) {
            return true;
        }
        if ( M.list.size() == 0 ) {
            return true;
        }
        PolynomialList F = M.getPolynomialList();
        int modv = ((List)M.list.get(0)).size();
        return SolvableGroebnerBase.isLeftGB(modv,F.list);
    }


    /**
     * Left Groebner base using pairlist class.
     */

    public static ArrayList leftGB(int modv, List F) {  
        return SolvableGroebnerBase.leftGB(modv,F);
    }

    public static ModuleList leftGB(ModuleList M) {  
        PolynomialList F = M.getPolynomialList();
        ModuleList N = M;
        List t = (List)M.list;
        int modv = 0;
        if ( t == null || t.size() == 0 ) {
           return N;
        }
        modv = ((List)t.get(0)).size();
        List G = SolvableGroebnerBase.leftGB(modv,F.list);
        F = new PolynomialList(F.coeff,F.vars,F.tord,G,F.table);
        N = ModuleList.getModuleList(modv,F);
        return N;
    }



    /**
     * Module twosided Groebner base test
     */

    public static boolean isTwosidedGB(int modv, List F) {  
        return SolvableGroebnerBase.isTwosidedGB(modv,F);
    }

    public static boolean isTwosidedGB(ModuleList M) {  
        if ( M == null ) {
            return true;
        }
        if ( M.list == null ) {
            return true;
        }
        if ( M.list.size() == 0 ) {
            return true;
        }
        PolynomialList F = M.getPolynomialList();
        int modv = ((List)M.list.get(0)).size();
        return SolvableGroebnerBase.isTwosidedGB(modv,F.list);
    }


    /**
     * Twosided Groebner base using pairlist class.
     */

    public static ArrayList twosidedGB(int modv, List F) {  
        return SolvableGroebnerBase.twosidedGB(modv,F);
    }

    public static ModuleList twosidedGB(ModuleList M) {  
        PolynomialList F = M.getPolynomialList();
        ModuleList N = M;
        List t = (List)M.list;
        int modv = 0;
        if ( t == null || t.size() == 0 ) {
           return N;
        }
        modv = ((List)t.get(0)).size();
        List G = SolvableGroebnerBase.twosidedGB(modv,F.list);
        F = new PolynomialList(F.coeff,F.vars,F.tord,G,F.table);
        N = ModuleList.getModuleList(modv,F);
        return N;
    }

}
