/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenSolvablePolynomial;

import edu.jas.structure.RingElem;


/**
 * Solvable Groebner Bases abstract class.
 * Implements common left and twosided Groebner bases 
 * and left and twosided GB tests.
 * @author Heinz Kredel.
 */

public abstract class SolvableGroebnerBaseAbstract<C extends RingElem<C>> 
       implements SolvableGroebnerBase<C> {

    private static final Logger logger = Logger.getLogger(SolvableGroebnerBaseAbstract.class);


    /**
     * Solvable reduction engine.
     */
    protected SolvableReduction<C> sred;


    /**
     * Reduction engine.
     */
    protected Reduction<C> red;


    /**
     * Constructor.
     */
    public SolvableGroebnerBaseAbstract() {
        red = new ReductionSeq<C>();
        sred = new SolvableReductionSeq<C>();
    }


    /**
     * Left Groebner base test.
     * @param C coefficient type.
     * @param F solvable polynomial list.
     * @return true, if F is a left Groebner base, else false.
     */
    public boolean isLeftGB(List<GenSolvablePolynomial<C>> F) {  
        return isLeftGB(0,F);
    }


    /**
     * Left Groebner base test.
     * @param C coefficient type.
     * @param modv number of module variables.
     * @param F solvable polynomial list.
     * @return true, if F is a left Groebner base, else false.
     */
    public boolean isLeftGB(int modv, 
                            List<GenSolvablePolynomial<C>> F) {  
        GenSolvablePolynomial<C> pi, pj, s, h;
        for ( int i = 0; i < F.size(); i++ ) {
            pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                   continue;
                }
                // if ( ! red.criterion4( pi, pj ) ) { continue; }
                s = sred.leftSPolynomial( pi, pj );
                if ( s.isZERO() ) {
                   continue;
                }
                h = sred.leftNormalform( F, s );
                if ( ! h.isZERO() ) {
                   return false;
                }
            }
        }
        return true;
    }


    /**
     * Twosided Groebner base test.
     * @param C coefficient type.
     * @param Fp solvable polynomial list.
     * @return true, if Fp is a two-sided Groebner base, else false.
     */
    public boolean isTwosidedGB(List<GenSolvablePolynomial<C>> Fp) {  
        return isTwosidedGB(0,Fp);
    }


    /**
     * Twosided Groebner base test.
     * @param C coefficient type.
     * @param modv number of module variables.
     * @param Fp solvable polynomial list.
     * @return true, if Fp is a two-sided Groebner base, else false.
     */
    public boolean isTwosidedGB(int modv, 
                                List<GenSolvablePolynomial<C>> Fp) {
        if ( Fp == null || Fp.size() == 0 ) { // 0 not 1
            return true;
        }
        List<GenSolvablePolynomial<C>> X = generateUnivar( modv, Fp );
        List<GenSolvablePolynomial<C>> F 
            = new ArrayList<GenSolvablePolynomial<C>>( Fp.size() * (1+X.size()) );
        F.addAll( Fp );
        GenSolvablePolynomial<C> p, x, pi, pj, s, h;
        for ( int i = 0; i < Fp.size(); i++ ) {
            p = Fp.get(i);
            for ( int j = 0; j < X.size(); j++ ) {
                x = X.get(j);
                p = p.multiply( x );
                F.add( p );
            }
        }
        //System.out.println("F to check = " + F);
        for ( int i = 0; i < F.size(); i++ ) {
            pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                   continue;
                }
                // if ( ! red.criterion4( pi, pj ) ) { continue; }
                s = sred.leftSPolynomial( pi, pj );
                if ( s.isZERO() ) {
                   continue;
                }
                h = sred.leftNormalform( F, s );
                if ( ! h.isZERO() ) {
                   logger.info("is not TwosidedGB: " + h);
                   return false;
                }
            }
        }
        return true;
    }


    /**
     * Generate solvable polynomials in each variable.
     * @param C coefficient type.
     * @param F solvable polynomial list.
     * @return a list of solvable univariate polynomials for each variable.
     */
    protected List<GenSolvablePolynomial<C>> 
              generateUnivar(List<GenSolvablePolynomial<C>> F) {
        return generateUnivar(0,F);
    }


    /**
     * Generate solvable polynomials in each variable.
     * Module variable polynomials are not generated.
     * @param C coefficient type.
     * @param modv number of module variables.
     * @param F solvable polynomial list.
     * @return a list of solvable univariate polynomials for each variable.
     */
    protected List<GenSolvablePolynomial<C>> 
              generateUnivar(int modv, 
                             List<GenSolvablePolynomial<C>> F) {
        GenSolvablePolynomial<C> p = F.get(0);
        GenSolvablePolynomial<C> zero = p.ring.getZERO();
        C one = p.ring.coFac.getONE();
        int r = p.numberOfVariables()-modv;
        ExpVector e;
        List<GenSolvablePolynomial<C>> pols 
            = new ArrayList<GenSolvablePolynomial<C>>(r);
        for ( int i = 0; i < r; i++ ) {
            e = new ExpVector(r,i,1);
            if ( modv > 0 ) {
                e = e.extend(modv,0,0l);
            }
            p = (GenSolvablePolynomial<C>)zero.add(one,e);
            pols.add( p );
        }
        return pols;
    }


    /**
     * Left Groebner base using pairlist class.
     * @param C coefficient type.
     * @param F solvable polynomial list.
     * @return leftGB(F) a left Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> 
           leftGB(List<GenSolvablePolynomial<C>> F) {  
        return leftGB(0,F);
    }


    /**
     * Left Groebner base using pairlist class.
     * @param C coefficient type.
     * @param modv number of module variables.
     * @param F solvable polynomial list.
     * @return leftGB(F) a left Groebner base of F.
     */
    public abstract List<GenSolvablePolynomial<C>> 
           leftGB(int modv, 
                  List<GenSolvablePolynomial<C>> F);


    /** 
     * Solvable Extended Groebner base using critical pair class.
     * @param C coefficient type.
     * @param F solvable polynomial list.
     * @return a container for an extended left Groebner base of F.
     */
    public SolvableExtendedGB<C>  
           extLeftGB( List<GenSolvablePolynomial<C>> F ) {
        return extLeftGB(0,F); 
    }


    /**
     * Solvable Extended Groebner base using critical pair class.
     * @param C coefficient type.
     * @param modv module variable number.
     * @param F solvable polynomial list.
     * @return a container for an extended left Groebner base of F.
     */
    public abstract SolvableExtendedGB<C> 
                    extLeftGB( int modv, 
                               List<GenSolvablePolynomial<C>> F );


    /**
     * Left minimal ordered groebner basis.
     * @param C coefficient type.
     * @param Gp a left Groebner base.
     * @return leftGBmi(F) a minimal left Groebner base of Gp.
     */
    public List<GenSolvablePolynomial<C>> 
               leftMinimalGB(List<GenSolvablePolynomial<C>> Gp) {  
        ArrayList<GenSolvablePolynomial<C>> G 
           = new ArrayList<GenSolvablePolynomial<C>>();
        ListIterator<GenSolvablePolynomial<C>> it = Gp.listIterator();
        for ( GenSolvablePolynomial<C> a: Gp ) { 
            // a = (SolvablePolynomial) it.next();
            if ( a.length() != 0 ) { // always true
               // already monic a = a.monic();
               G.add( a );
            }
        }
        if ( G.size() <= 1 ) {
           return G;
        }

        ExpVector e;        
        ExpVector f;        
        GenSolvablePolynomial<C> a, p;
        ArrayList<GenSolvablePolynomial<C>> F 
           = new ArrayList<GenSolvablePolynomial<C>>();
        boolean mt;

        while ( G.size() > 0 ) {
            a = G.remove(0);
            e = a.leadingExpVector();

            it = G.listIterator();
            mt = false;
            while ( it.hasNext() && ! mt ) {
               p = it.next();
               f = p.leadingExpVector();
               mt = ExpVector.EVMT( e, f );
            }
            it = F.listIterator();
            while ( it.hasNext() && ! mt ) {
               p = it.next();
               f = p.leadingExpVector();
               mt = ExpVector.EVMT( e, f );
            }
            if ( ! mt ) {
                F.add( a );
            } else {
                // System.out.println("dropped " + a.length());
            }
        }
        G = F;
        if ( G.size() <= 1 ) {
           return G;
        }

        F = new ArrayList<GenSolvablePolynomial<C>>();
        while ( G.size() > 0 ) {
            a = G.remove(0);
            // System.out.println("doing " + a.length());
            a = sred.leftNormalform( G, a );
            a = sred.leftNormalform( F, a );
            F.add( a );
        }
        return F;
    }


    /**
     * Twosided Groebner base using pairlist class.
     * @param C coefficient type.
     * @param Fp solvable polynomial list.
     * @return tsGB(Fp) a twosided Groebner base of Fp.
     */
    public List<GenSolvablePolynomial<C>> 
               twosidedGB(List<GenSolvablePolynomial<C>> Fp) {  
        return twosidedGB(0,Fp);
    }


    /**
     * Twosided Groebner base using pairlist class.
     * @param C coefficient type.
     * @param modv number of module variables.
     * @param Fp solvable polynomial list.
     * @return tsGB(Fp) a twosided Groebner base of Fp.
     */
    public abstract List<GenSolvablePolynomial<C>> 
           twosidedGB(int modv, 
                      List<GenSolvablePolynomial<C>> Fp);

}
