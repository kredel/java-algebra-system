/*
 * $Id$
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.OptimizedPolynomialList;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrderOptimization;

import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;


/**
 * Groebner Bases abstract class.
 * Implements common Groebner bases and GB test methods.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GroebnerBasePartial<C extends GcdRingElem<C>> 
                                                    /*implements GroebnerBase<C>*/ {

    private static final Logger logger = Logger.getLogger(GroebnerBasePartial.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Reduction engine.
     */
    protected Reduction<C> red;


    /**
     * Constructor.
     */
    public GroebnerBasePartial() {
        this( new ReductionSeq<C>() );
    }


    /**
     * Constructor.
     * @param red Reduction engine
     */
    public GroebnerBasePartial(Reduction<C> red) {
        this.red = red;
    }


    /** 
     * Partial Groebner base for specific variables.
     * Computes Groebner base in K[pvars] with coefficients from K[vars \ pvars].
     * @param F polynomial list.
     * @param pvars names for main variables of partial Groebner base computation.
     * @return a container for a partial Groebner base of F wrt vars.
     */
    public OptimizedPolynomialList<GenPolynomial<C>> partialGB( List<GenPolynomial<C>> F, String[] pvars ) {
        if ( F == null && F.isEmpty() ) {
            throw new IllegalArgumentException("empty F not allowed");
        }
        GenPolynomialRing<C> fac = F.get(0).ring;
        String[] vars = fac.getVars();
        if ( vars == null || pvars == null ) {
            throw new IllegalArgumentException("no variable names found");
        }
        List<String> variables = new ArrayList<String>(vars.length);
        List<String> pvariables = new ArrayList<String>(pvars.length);
        for ( int i = 0; i < vars.length; i++ ) {
            variables.add( vars[i] );
        }
        for ( int i = 0; i < pvars.length; i++ ) {
            pvariables.add( pvars[i] );
        }
        if ( ! variables.containsAll(pvariables) ) {
            throw new IllegalArgumentException("partial variables not contained in all variables ");
        }
        // variables.setMinus(pvariables)
        List<String> rvariables = new ArrayList<String>(variables);
        for ( String s : pvariables ) {
            rvariables.remove(s);
        }
        System.out.println("variables  = " + variables);
        System.out.println("pvariables = " + pvariables);
        System.out.println("rvariables = " + rvariables);
        Collections.reverse(variables);
        Collections.reverse(pvariables);
        System.out.println("\nvariables  = " + variables);
        System.out.println("pvariables = " + pvariables);

        List<Integer> perm = new ArrayList<Integer>();
        List<Integer> pv = new ArrayList<Integer>();
        int i = 0;
        for ( String s : variables ) {
            if ( pvariables.contains(s) ) {
                perm.add( i );
            } else {
                pv.add( i );
            }
            i++;
        }
        System.out.println("\nperm = " + perm);
        System.out.println("pv   = " + pv);
        // sort perm according to pvars
        int ps = perm.size(); // == pvars.length
        for ( int k = 0; k < ps; k++ ) {
            for ( int j = k+1; j < ps; j++ ) {
                int kk = variables.indexOf( pvariables.get(k) );
                int jj = variables.indexOf( pvariables.get(j) );
                if ( kk > jj ) { // swap
                    int t = perm.get(k);
                    System.out.println("swap " + t + " with " + perm.get(j));
                    perm.set(k,perm.get(j));
                    perm.set(j,t);
                }
            }
        }
        System.out.println("perm = " + perm);

        perm.addAll(pv);
        System.out.println("perm = " + perm);

        GenPolynomialRing<C> pfac;
        pfac = TermOrderOptimization.<C>permutation( perm, fac );
        System.out.println("pfac = " + pfac);

        List<GenPolynomial<C>> ppolys;
        ppolys = TermOrderOptimization.<C>permutation( perm, pfac, F );
        System.out.println("ppolys = " + ppolys);

        int cl = fac.nvar - pvars.length;
        int pl = pvars.length;
        String[] rvars = new String[cl];
        i = 0;
        for ( String s : rvariables ) {
            rvars[i++] = s;
        }
        GenPolynomialRing<C> cfac = new GenPolynomialRing<C>(fac.coFac,cl,fac.tord,rvars);
        System.out.println("cfac = " + cfac);

        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac,pl,fac.tord,pvars);
        System.out.println("rfac = " + rfac);

        List<GenPolynomial<GenPolynomial<C>>> Fr = PolyUtil.<C>recursive(rfac,ppolys);
        System.out.println("\nFr = " + Fr);

        GroebnerBasePseudoRecSeq<C> bb = new GroebnerBasePseudoRecSeq<C>(cfac);

        List<GenPolynomial<GenPolynomial<C>>> Gr = bb.GB(Fr);
        System.out.println("\nGr = " + Gr);

        OptimizedPolynomialList<GenPolynomial<C>> pgb = null;
        pgb = new OptimizedPolynomialList<GenPolynomial<C>>(perm,rfac,Gr);
        return pgb; 
    }

}
