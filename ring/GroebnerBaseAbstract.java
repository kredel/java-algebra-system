/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Groebner Bases abstract class.
 * Implements common Groebner bases and GB test methods.
 * @author Heinz Kredel
 */

public abstract class GroebnerBaseAbstract<C extends RingElem<C>> 
                      implements GroebnerBase<C> {

    //unused private static final Logger logger = Logger.getLogger(GroebnerBaseAbstract.class);


    /**
     * Reduction engine.
     */
    protected Reduction<C> red;


    /**
     * Constructor.
     */
    public GroebnerBaseAbstract() {
        red = new ReductionSeq<C>();
    }


    /**
     * Groebner base test.
     * @param C coefficient type.
     * @param F polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(List<GenPolynomial<C>> F) {  
        return isGB(0,F);
    }


    /**
     * Groebner base test.
     * @param C coefficient type.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(int modv, List<GenPolynomial<C>> F) {  
        GenPolynomial<C> pi, pj, s, h;
        for ( int i = 0; i < F.size(); i++ ) {
            pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                   continue;
                }
                if ( ! red.criterion4( pi, pj ) ) { 
                   continue;
                }
                s = red.SPolynomial( pi, pj );
                if ( s.isZERO() ) {
                   continue;
                }
                h = red.normalform( F, s );
                if ( ! h.isZERO() ) {
                   return false;
                }
            }
        }
        return true;
    }


    /**
     * Groebner base using pairlist class.
     * @param C coefficient type.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> 
             GB( List<GenPolynomial<C>> F ) {  
        return GB(0,F);
    }


    /**
     * Groebner base using pairlist class.
     * @param C coefficient type.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public abstract List<GenPolynomial<C>> 
                    GB( int modv, 
                        List<GenPolynomial<C>> F );



    /** 
     * Extended Groebner base using critical pair class.
     * @param C coefficient type.
     * @param F polynomial list.
     * @return a container for an extended Groebner base of F.
     */
    public ExtendedGB<C>  
                  extGB( List<GenPolynomial<C>> F ) {
        return extGB(0,F); 
    }


    /**
     * Extended Groebner base using critical pair class.
     * @param C coefficient type.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return a container for an extended Groebner base of F.
     */
    public ExtendedGB<C> 
           extGB( int modv, 
                  List<GenPolynomial<C>> F ) {
        throw new RuntimeException("extGB not implemented in " 
                                   + this.getClass().getSimpleName());
    }


    /**
     * Minimal ordered groebner basis.
     * @param C coefficient type.
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     */
    public List<GenPolynomial<C>> 
                minimalGB(List<GenPolynomial<C>> Gp) {  
        if ( Gp == null ) {
            return Gp;
        }
        GenPolynomial<C> a;
        List<GenPolynomial<C>> G;
        G = new ArrayList<GenPolynomial<C>>( Gp.size() );
        ListIterator<GenPolynomial<C>> it = Gp.listIterator();
        while ( it.hasNext() ) { 
            a = it.next();
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
        GenPolynomial<C> p;
        List<GenPolynomial<C>> F;
        F = new ArrayList<GenPolynomial<C>>( G.size() );
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

        F = new ArrayList<GenPolynomial<C>>( G.size() );
        while ( G.size() > 0 ) {
            a = G.remove(0);
            // System.out.println("doing " + a.length());
            a = red.normalform( G, a );
            a = red.normalform( F, a );
            F.add( a );
        }
        return F;
    }

}


