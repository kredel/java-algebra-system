/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.OrderedPolynomial;

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

    public static boolean isDIRPGB(List F) {  
        OrderedPolynomial pi, pj, s, h;
	for ( int i = 0; i < F.size(); i++ ) {
	    pi = (OrderedPolynomial) F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = (OrderedPolynomial) F.get(j);
		if ( ! Reduction.GBCriterion4( pi, pj ) ) continue;
		s = Reduction.SPolynomial( pi, pj );
		if ( s.isZERO() ) continue;
		h = Reduction.Normalform( F, s );
		if ( ! h.isZERO() ) return false;
	    }
	}
	return true;
    }


    /**
     * Groebner base using pairlist class.
     */

    public static ArrayList DIRPGB(List F) {  
        OrderedPolynomial p;
        ArrayList G = new ArrayList();
        OrderedPairlist pairlist = null; 
        int l = F.size();
        ListIterator it = F.listIterator();
        while ( it.hasNext() ) { 
            p = (OrderedPolynomial) it.next();
            if ( p.length() > 0 ) {
               p = p.monic();
               if ( p.isONE() ) {
		  G.clear(); G.add( p );
                  return G; // since no threads are activated
	       }
               G.add( p );
	       if ( pairlist == null ) {
                  pairlist = new OrderedPairlist( p.getTermOrder() );
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

        Pair pair;
        OrderedPolynomial pi;
        OrderedPolynomial pj;
        OrderedPolynomial S;
        OrderedPolynomial H;
        while ( pairlist.hasNext() ) {
              pair = (Pair) pairlist.removeNext();
              if ( pair == null ) continue; 

              pi = pair.pi; 
              pj = pair.pj; 
              if ( false && logger.isDebugEnabled() ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
	      }

              S = Reduction.SPolynomial( pi, pj );
              if ( S.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
	      }

              H = Reduction.Normalform( G, S );
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
	G = DIGBMI(G);
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
	return G;
    }


    /**
     * Minimal ordered groebner basis.
     */

    public static ArrayList DIGBMI(List Gp) {  
        OrderedPolynomial a;
        ArrayList G = new ArrayList();
        ListIterator it = Gp.listIterator();
        while ( it.hasNext() ) { 
            a = (OrderedPolynomial) it.next();
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
        OrderedPolynomial p;
        ArrayList F = new ArrayList();
	boolean mt;

        while ( G.size() > 0 ) {
            a = (OrderedPolynomial) G.remove(0);
	    e = a.leadingExpVector();

            it = G.listIterator();
	    mt = false;
	    while ( it.hasNext() && ! mt ) {
               p = (OrderedPolynomial) it.next();
               f = p.leadingExpVector();
	       mt = ExpVector.EVMT( e, f );
	    }
            it = F.listIterator();
	    while ( it.hasNext() && ! mt ) {
               p = (OrderedPolynomial) it.next();
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

        F = new ArrayList();
        while ( G.size() > 0 ) {
            a = (OrderedPolynomial) G.remove(0);
	    // System.out.println("doing " + a.length());
            a = Reduction.Normalform( G, a );
            a = Reduction.Normalform( F, a );
            F.add( a );
	}
	return F;
    }

}
