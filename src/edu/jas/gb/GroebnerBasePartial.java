/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.OptimizedPolynomialList;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrderOptimization;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Groebner Bases for subsets of variables. 
 * methods.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GroebnerBasePartial<C extends GcdRingElem<C>>
             extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBasePartial.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Backing Groebner base engine.
     */
    protected GroebnerBaseAbstract<C> bb;


    /**
     * Backing recursive Groebner base engine.
     */
    protected GroebnerBaseAbstract<GenPolynomial<C>> rbb; // can be null


    /**
     * Constructor.
     */
    public GroebnerBasePartial() {
        this(new GroebnerBaseSeq<C>(),null);
    }


    /**
     * Constructor.
     * @param rf coefficient ring factory.
     */
    public GroebnerBasePartial(RingFactory<GenPolynomial<C>> rf) {
        this(new GroebnerBaseSeq<C>(),new GroebnerBasePseudoRecSeq<C>(rf));
    }


    /**
     * Constructor.
     * @param bb Groebner base engine
     * @param rbb recursive Groebner base engine
     */
    public GroebnerBasePartial(GroebnerBaseAbstract<C> bb, GroebnerBaseAbstract<GenPolynomial<C>> rbb) {
        super();
        this.bb = bb;
        this.rbb = rbb;
        if ( rbb == null ) {
            logger.warn("no recursive GB given");
        }
    }


    /**
     * Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> 
             GB( int modv, 
                 List<GenPolynomial<C>> F ) {  
        return bb.GB(modv,F);
    }


    /**
     * Groebner base test.
     * @param F polynomial list.
     * @return true, if F is a partial Groebner base, else false.
     */
    public boolean isGBrec(List<GenPolynomial<GenPolynomial<C>>> F) {  
        return isGBrec(0,F);
    }


    /**
     * Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a  partial Groebner base, else false.
     */
    public boolean isGBrec(int modv, List<GenPolynomial<GenPolynomial<C>>> F) {  
        if ( F == null || F.size() == 0) {
            return true;
        }
        if ( true ) {
            rbb = new GroebnerBasePseudoRecSeq<C>(F.get(0).ring.coFac);
        }
        return rbb.isGB(modv,F);
    }


    /**
     * Partial permuation for specific variables. Computes a permutation perm
     * for the variables vars, such that perm(vars) == pvars ... (vars \ pvars).
     * Uses internal (reversed) variable sorting.
     * @param vars names for all variables.
     * @param pvars names for main variables, pvars subseteq vars.
     * @return permutation for vars, such that perm(vars) == pvars ... (vars \
     *         pvars).
     */
    public List<Integer> partialPermutation(String[] vars, String[] pvars) {
        // can use: return partialPermutation(vars,pvars,null);
        if (vars == null || pvars == null) {
            throw new IllegalArgumentException("no variable names found");
        }
        List<String> variables = new ArrayList<String>(vars.length);
        List<String> pvariables = new ArrayList<String>(pvars.length);
        for (int i = 0; i < vars.length; i++) {
            variables.add(vars[i]);
        }
        for (int i = 0; i < pvars.length; i++) {
            pvariables.add(pvars[i]);
        }
        if (!variables.containsAll(pvariables)) {
            throw new IllegalArgumentException("partial variables not contained in all variables ");
        }
        Collections.reverse(variables);
        Collections.reverse(pvariables);
        //System.out.println("\nvariables  = " + variables);
        //System.out.println("pvariables = " + pvariables);

        List<Integer> perm = new ArrayList<Integer>();
        List<Integer> pv = new ArrayList<Integer>();
        int i = 0;
        for (String s : variables) {
            if (pvariables.contains(s)) {
                perm.add(i);
            } else {
                pv.add(i);
            }
            i++;
        }
        //System.out.println("perm = " + perm);
        //System.out.println("pv   = " + pv);
        // sort perm according to pvars
        int ps = perm.size(); // == pvars.length
        for (int k = 0; k < ps; k++) {
            for (int j = k + 1; j < ps; j++) {
                int kk = variables.indexOf(pvariables.get(k));
                int jj = variables.indexOf(pvariables.get(j));
                if (kk > jj) { // swap
                    int t = perm.get(k);
                    //System.out.println("swap " + t + " with " + perm.get(j));
                    perm.set(k, perm.get(j));
                    perm.set(j, t);
                }
            }
        }
        //System.out.println("perm = " + perm);
        perm.addAll(pv);
        //System.out.println("perm = " + perm);
        return perm;
    }


    /**
     * Partial permuation for specific variables. Computes a permutation perm
     * for the variables vars, such that perm(vars) == pvars ... (vars \ pvars).
     * Uses internal (reversed) variable sorting.
     * @param vars names for all variables.
     * @param pvars names for main variables, pvars subseteq vars.
     * @param rvars names for remaining variables, rvars subseteq vars.
     * @return permutation for vars, such that perm(vars) == (pvars, pvars).
     */
    public List<Integer> partialPermutation(String[] vars, String[] pvars, String[] rvars) {
        if (vars == null || pvars == null) {
            throw new IllegalArgumentException("no variable names found");
        }
        List<String> variables = new ArrayList<String>(vars.length);
        List<String> pvariables = new ArrayList<String>(pvars.length);
        for (int i = 0; i < vars.length; i++) {
            variables.add(vars[i]);
        }
        for (int i = 0; i < pvars.length; i++) {
            pvariables.add(pvars[i]);
        }
        if ( rvars == null ) {
            rvars = remainingVars(vars,pvars);
        }
        List<String> rvariables = new ArrayList<String>(rvars.length);
        for (int i = 0; i < rvars.length; i++) {
            rvariables.add(rvars[i]);
        }
        if (!variables.containsAll(pvariables) || !variables.containsAll(rvariables)) {
            throw new IllegalArgumentException("partial variables not contained in all variables ");
        }
        Collections.reverse(variables);
        Collections.reverse(pvariables);
        Collections.reverse(rvariables);
        //System.out.println("\nvariables  = " + variables);
        //System.out.println("pvariables = " + pvariables);
        //System.out.println("rvariables = " + rvariables);

        List<Integer> perm = new ArrayList<Integer>();
        List<Integer> pv = new ArrayList<Integer>();
        int i = 0;
        for (String s : variables) {
            if (pvariables.contains(s)) {
                perm.add(i);
            } else {
                pv.add(i);
            }
            i++;
        }
        //System.out.println("perm = " + perm);
        //System.out.println("pv   = " + pv);
        // sort perm according to pvars
        int ps = perm.size(); // == pvars.length
        for (int k = 0; k < ps; k++) {
            for (int j = k + 1; j < ps; j++) {
                int kk = variables.indexOf(pvariables.get(k));
                int jj = variables.indexOf(pvariables.get(j));
                if (kk > jj) { // swap
                    int t = perm.get(k);
                    //System.out.println("swap " + t + " with " + perm.get(j));
                    perm.set(k, perm.get(j));
                    perm.set(j, t);
                }
            }
        }
        //System.out.println("perm = " + perm);
        // sort pv according to rvars
        int rs = pv.size(); // == rvars.length
        for (int k = 0; k < rs; k++) {
            for (int j = k + 1; j < rs; j++) {
                int kk = variables.indexOf(rvariables.get(k));
                int jj = variables.indexOf(rvariables.get(j));
                if (kk > jj) { // swap
                    int t = pv.get(k);
                    //System.out.println("swap " + t + " with " + perm.get(j));
                    pv.set(k, pv.get(j));
                    pv.set(j, t);
                }
            }
        }
        //System.out.println("pv   = " + pv);
        perm.addAll(pv);
        //System.out.println("perm = " + perm);
        return perm;
    }


    /**
     * Remaining variables vars \ pvars. Uses internal (reversed) variable
     * sorting, original order is preserved.
     * @param vars names for all variables.
     * @param pvars names for main variables, pvars subseteq vars.
     * @return remaining vars = (vars \ pvars).
     */
    public String[] remainingVars(String[] vars, String[] pvars) {
        if (vars == null || pvars == null) {
            throw new IllegalArgumentException("no variable names found");
        }
        List<String> variables = new ArrayList<String>(vars.length);
        List<String> pvariables = new ArrayList<String>(pvars.length);
        for (int i = 0; i < vars.length; i++) {
            variables.add(vars[i]);
        }
        for (int i = 0; i < pvars.length; i++) {
            pvariables.add(pvars[i]);
        }
        if (!variables.containsAll(pvariables)) {
            throw new IllegalArgumentException("partial variables not contained in all variables ");
        }
        // variables.setMinus(pvariables)
        List<String> rvariables = new ArrayList<String>(variables);
        for (String s : pvariables) {
            rvariables.remove(s);
        }
        int cl = vars.length - pvars.length;
        String[] rvars = new String[cl];
        int i = 0;
        for (String s : rvariables) {
            rvars[i++] = s;
        }
        return rvars;
    }


    /**
     * Partial recursive Groebner base for specific variables. Computes Groebner base in
     * K[pvars] with coefficients from K[vars \ pvars].
     * @param F polynomial list.
     * @param pvars names for main variables of partial Groebner base
     *            computation.
     * @return a container for a partial Groebner base of F wrt pvars.
     */
    public OptimizedPolynomialList<GenPolynomial<C>> partialGBrec(List<GenPolynomial<C>> F, String[] pvars) {
        if (F == null && F.isEmpty()) {
            throw new IllegalArgumentException("empty F not allowed");
        }
        GenPolynomialRing<C> fac = F.get(0).ring;
        String[] vars = fac.getVars();
        if (vars == null || pvars == null) {
            throw new IllegalArgumentException("not all variable names found");
        }
        if (vars.length == pvars.length) {
            throw new IllegalArgumentException("use non recursive partialGB algorithm");
        }

        // compute permutation (in reverse sorting)
        List<Integer> perm = partialPermutation(vars, pvars);

        GenPolynomialRing<C> pfac = TermOrderOptimization.<C> permutation(perm, fac);
        if (logger.isInfoEnabled()) {
            logger.info("pfac = " + pfac);
        }
        //System.out.println("pfac = " + pfac);

        List<GenPolynomial<C>> ppolys = TermOrderOptimization.<C> permutation(perm, pfac, F);
        //System.out.println("ppolys = " + ppolys);

        int cl = fac.nvar - pvars.length; // > 0
        int pl = pvars.length;
        String[] rvars = remainingVars(vars, pvars);

        GenPolynomialRing<C> cfac = new GenPolynomialRing<C>(fac.coFac, cl, fac.tord, rvars);
        //System.out.println("cfac = " + cfac);

        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, pl,
                fac.tord, pvars);
        if (logger.isInfoEnabled()) {
            logger.info("rfac = " + rfac);
        }
        //System.out.println("rfac = " + rfac);

        List<GenPolynomial<GenPolynomial<C>>> Fr = PolyUtil.<C> recursive(rfac, ppolys);
        //System.out.println("\nFr = " + Fr);

        if ( true ) {
            rbb = new GroebnerBasePseudoRecSeq<C>(cfac);
        }

        List<GenPolynomial<GenPolynomial<C>>> Gr = rbb.GB(Fr);
        //System.out.println("\nGr = " + Gr);

        //perm = perm.subList(0,pl);
        OptimizedPolynomialList<GenPolynomial<C>> pgb = new OptimizedPolynomialList<GenPolynomial<C>>(perm,rfac, Gr);
        return pgb;
    }


    /**
     * Partial Groebner base for specific variables. Computes Groebner base in
     * K[pvars] with coefficients from K[vars \ pvars] but returns polynomials in K[pvars, vars \ pvars].
     * @param F polynomial list.
     * @param pvars names for main variables of partial Groebner base
     *            computation.
     * @return a container for a partial Groebner base of F wrt pvars.
     */
    public OptimizedPolynomialList<C> partialGB(List<GenPolynomial<C>> F, String[] pvars) {
        if (F == null && F.isEmpty()) {
            throw new IllegalArgumentException("empty F not allowed");
        }
        GenPolynomialRing<C> fac = F.get(0).ring;
        String[] vars = fac.getVars();
        // compute permutation (in reverse sorting)
        List<Integer> perm = partialPermutation(vars, pvars);

        GenPolynomialRing<C> pfac = TermOrderOptimization.<C> permutation(perm, fac);
        if (logger.isInfoEnabled()) {
            logger.info("pfac = " + pfac);
        }
        // System.out.println("pfac = " + pfac);

        List<GenPolynomial<C>> ppolys = TermOrderOptimization.<C> permutation(perm, pfac, F);
        //System.out.println("ppolys = " + ppolys);

        int cl = fac.nvar - pvars.length;
        if ( cl == 0 ) { // non recursive case
            //GroebnerBaseSeq<C> bb = new GroebnerBaseSeq<C>();
            List<GenPolynomial<C>> G = bb.GB(ppolys);
            OptimizedPolynomialList<C> pgb = new OptimizedPolynomialList<C>(perm, pfac, G);
            return pgb;
        }
        // recursive case
        int pl = pvars.length;
        String[] rvars = remainingVars(vars, pvars);
        GenPolynomialRing<C> cfac = new GenPolynomialRing<C>(fac.coFac, cl, fac.tord, rvars);
        //System.out.println("cfac = " + cfac);

        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, pl,fac.tord, pvars);
        if (logger.isInfoEnabled()) {
            logger.info("rfac = " + rfac);
        }
        //System.out.println("rfac = " + rfac);

        List<GenPolynomial<GenPolynomial<C>>> Fr = PolyUtil.<C> recursive(rfac, ppolys);
        //System.out.println("\nFr = " + Fr);

        if ( true ) {
            rbb = new GroebnerBasePseudoRecSeq<C>(cfac);
        }
        List<GenPolynomial<GenPolynomial<C>>> Gr = rbb.GB(Fr);
        //System.out.println("\nGr = " + Gr);

        List<GenPolynomial<C>> G = PolyUtil.<C> distribute(pfac, Gr);
        //System.out.println("\nG = " + G);

        OptimizedPolynomialList<C> pgb = new OptimizedPolynomialList<C>(perm, pfac, G);
        return pgb;
    }

}
