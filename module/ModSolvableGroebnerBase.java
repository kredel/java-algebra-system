/*
 * $Id$
 */

package edu.jas.module;

//import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;
//import java.util.ListIterator;
//import java.util.Iterator;

import org.apache.log4j.Logger;


import edu.jas.structure.RingElem;

//import edu.jas.poly.ExpVector;
//import edu.jas.poly.GenPolynomial;
//import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;

//import edu.jas.ring.Reduction;
//import edu.jas.ring.GroebnerBase;
import edu.jas.ring.SolvableGroebnerBase;
import edu.jas.ring.SolvableGroebnerBaseSeq;

import edu.jas.module.ModuleList;


/**
 * Module Groebner Bases class.
 * Implements Groebner bases and GB test.
 * @author Heinz Kredel
 */

public class ModSolvableGroebnerBase<C extends RingElem<C>> {

    private static final Logger logger = Logger.getLogger(ModSolvableGroebnerBase.class);


    /**
     * Used Solvable Groebner base algorithm.
     */
    protected final SolvableGroebnerBase<C> sbb;


    /**
     * Constructor.
     */
    public ModSolvableGroebnerBase() {
        sbb = new SolvableGroebnerBaseSeq<C>();
    }


    /**
     * Module left Groebner base test.
     * @param C coefficient type.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return true, if F is a left Groebner base, else false.
     */
    public boolean 
           isLeftGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return sbb.isLeftGB(modv,F);
    }


    /**
     * Module left Groebner base test.
     * @param C coefficient type.
     * @param M a module basis.
     * @return true, if M is a left Groebner base, else false.
     */
    public boolean 
           isLeftGB(ModuleList<C> M) {  
        if ( M == null || M.list == null ) {
            return true;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return true;
        }
        int modv = M.cols; // > 0  
        PolynomialList<C> F = M.getPolynomialList();
        return sbb.isLeftGB(modv,F.castToSolvableList());
    }


    /**
     * Left Groebner base using pairlist class.
     * @param C coefficient type.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return leftGB(F) a left Groebner base for F.
     */
    public List<GenSolvablePolynomial<C>> 
           leftGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return sbb.leftGB(modv,F);
    }

    /**
     * Left Groebner base using pairlist class.
     * @param C coefficient type.
     * @param M a module basis.
     * @return leftGB(M) a left Groebner base for M.
     */
    public ModuleList<C> 
           leftGB(ModuleList<C> M) {  
        ModuleList<C> N = M;
        if ( M == null || M.list == null ) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }
        PolynomialList<C> F = M.getPolynomialList();
        GenSolvablePolynomialRing<C> sring 
            = (GenSolvablePolynomialRing<C>)F.ring;
        int modv = M.cols;
        List<GenSolvablePolynomial<C>> G 
            = sbb.leftGB(modv,F.castToSolvableList());
        F = new PolynomialList<C>(sring,G);
        N = F.getModuleList(modv);
        return N;
    }



    /**
     * Module twosided Groebner base test.
     * @param C coefficient type.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return true, if F is a twosided Groebner base, else false.
     */
    public boolean 
           isTwosidedGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return sbb.isTwosidedGB(modv,F);
    }

    /**
     * Module twosided Groebner base test.
     * @param C coefficient type.
     * @param M a module basis.
     * @return true, if M is a twosided Groebner base, else false.
     */
    public boolean 
           isTwosidedGB(ModuleList<C> M) {  
        if ( M == null || M.list == null ) {
            return true;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return true;
        }
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        return sbb.isTwosidedGB(modv,F.castToSolvableList());
    }


    /**
     * Twosided Groebner base using pairlist class.
     * @param C coefficient type.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return tsGB(F) a twosided Groebner base for F.
     */
    public List<GenSolvablePolynomial<C>> 
           twosidedGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return sbb.twosidedGB(modv,F);
    }

    /**
     * Twosided Groebner base using pairlist class.
     * @param C coefficient type.
     * @param M a module basis.
     * @return tsGB(M) a twosided Groebner base for M.
     */
    public ModuleList<C> 
           twosidedGB(ModuleList<C> M) {  
        ModuleList<C> N = M;
        if ( M == null || M.list == null ) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }
        PolynomialList<C> F = M.getPolynomialList();
        GenSolvablePolynomialRing<C> sring 
            = (GenSolvablePolynomialRing<C>)F.ring;
        int modv = M.cols;
        List<GenSolvablePolynomial<C>> G 
            = sbb.twosidedGB(modv,F.castToSolvableList());
        F = new PolynomialList<C>(sring,G);
        N = F.getModuleList(modv);
        return N;
    }


    /**
     * Module right Groebner base test.
     * @param C coefficient type.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return true, if F is a right Groebner base, else false.
     */
    public boolean 
           isRightGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return sbb.isRightGB(modv,F);
    }


    /**
     * Module right Groebner base test.
     * @param C coefficient type.
     * @param M a module basis.
     * @return true, if M is a right Groebner base, else false.
     */
    public boolean 
           isRightGB(ModuleList<C> M) {  
        if ( M == null || M.list == null ) {
            return true;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return true;
        }
        int modv = M.cols; // > 0  
        PolynomialList<C> F = M.getPolynomialList();
        return sbb.isRightGB(modv,F.castToSolvableList());
    }


    /**
     * Right Groebner base using pairlist class.
     * @param C coefficient type.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return rightGB(F) a right Groebner base for F.
     */
    public List<GenSolvablePolynomial<C>> 
           rightGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return sbb.rightGB(modv,F);
    }

    /**
     * Right Groebner base using pairlist class.
     * @param C coefficient type.
     * @param M a module basis.
     * @return rightGB(M) a right Groebner base for M.
     */
    public ModuleList<C> 
           rightGB(ModuleList<C> M) {  
        ModuleList<C> N = M;
        if ( M == null || M.list == null ) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }
        PolynomialList<C> F = M.getPolynomialList();
        GenSolvablePolynomialRing<C> sring 
            = (GenSolvablePolynomialRing<C>)F.ring;
        int modv = M.cols;
        List<GenSolvablePolynomial<C>> G 
            = sbb.rightGB(modv,F.castToSolvableList());
        F = new PolynomialList<C>(sring,G);
        //System.out.println("rightGB -------------------- \n" + F);
        N = F.getModuleList(modv);
        return N;
    }

}
