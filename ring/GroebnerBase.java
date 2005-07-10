/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;

//import edu.jas.poly.GenSolvablePolynomial;

import edu.jas.ring.OrderedPairlist;

/**
 * Groebner Bases class.
 * Implements Groebner bases and GB test.
 * @author Heinz Kredel
 */

public class GroebnerBase  {

    private static final Logger logger = Logger.getLogger(GroebnerBase.class);

    /**
     * Groebner base test
     */

    public static <C extends RingElem<C>> 
           boolean isGB(List<GenPolynomial<C>> F) {  
        return isGB(0,F);
    }

    public static <C extends RingElem<C>> 
           boolean isGB(int modv, List<GenPolynomial<C>> F) {  
        GenPolynomial<C> pi, pj, s, h;
	for ( int i = 0; i < F.size(); i++ ) {
	    pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
		if ( ! Reduction.<C>ModuleCriterion( modv, pi, pj ) ) {
                   continue;
                }
		if ( ! Reduction.<C>GBCriterion4( pi, pj ) ) { 
                   continue;
                }
		s = Reduction.<C>SPolynomial( pi, pj );
		if ( s.isZERO() ) {
                   continue;
                }
		h = Reduction.<C>normalform( F, s );
		if ( ! h.isZERO() ) {
                   return false;
                }
	    }
	}
	return true;
    }


    /**
     * Groebner base using pairlist class.
     */

    public static <C extends RingElem<C>> 
           ArrayList<GenPolynomial<C>> GB(List<GenPolynomial<C>> F) {  
        return GB(0,F);
    }

    public static <C extends RingElem<C>> 
           ArrayList<GenPolynomial<C>> GB(int modv, 
                                          List<GenPolynomial<C>> F) {  
        GenPolynomial<C> p;
        ArrayList<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        OrderedPairlist pairlist = null; 
        int l = F.size();
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( p.length() > 0 ) {
               p = p.monic();
               if ( p.isONE() ) {
		  G.clear(); G.add( p );
                  return G; // since no threads are activated
	       }
               G.add( p );
	       if ( pairlist == null ) {
                  pairlist = new OrderedPairlist( modv, p.ring );
               }
               // putOne not required
               pairlist.put( p );
	    } else { 
               l--;
            }
	}
        if ( l <= 1 ) {
           return G; // since no threads are activated
        }

        Pair<C> pair;
        GenPolynomial<C> pi;
        GenPolynomial<C> pj;
        GenPolynomial<C> S;
        GenPolynomial<C> H;
        while ( pairlist.hasNext() ) {
              pair = (Pair<C>) pairlist.removeNext();
              if ( pair == null ) continue; 

              pi = pair.pi; 
              pj = pair.pj; 
              if ( false && logger.isDebugEnabled() ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
	      }

              S = Reduction.<C>SPolynomial( pi, pj );
              if ( S.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
	      }

              H = Reduction.<C>normalform( G, S );
              if ( H.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
	      }

	      H = H.monic();
	      if ( H.isONE() ) {
		  G.clear(); G.add( H );
                  return G; // since no threads are activated
	      }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("H = " + H );
	      }
              if ( H.length() > 0 ) {
		 l++;
                 G.add( H );
                 pairlist.put( H );
              }
	}
        logger.debug("#sequential list = "+G.size());
	G = GBmi(G);
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
	return G;
    }


    /**
     * Minimal ordered groebner basis.
     */

    public static <C extends RingElem<C>>
           ArrayList<GenPolynomial<C>> GBmi(List<GenPolynomial<C>> Gp) {  
        GenPolynomial<C> a;
        ArrayList<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
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
        ArrayList<GenPolynomial<C>> F = new ArrayList<GenPolynomial<C>>();
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

        F = new ArrayList<GenPolynomial<C>>();
        while ( G.size() > 0 ) {
            a = G.remove(0);
	    // System.out.println("doing " + a.length());
            a = Reduction.<C>normalform( G, a );
            a = Reduction.<C>normalform( F, a );
            F.add( a );
	}
	return F;
    }

}
