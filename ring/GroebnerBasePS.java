/*
 * $Id$
 */

package edu.jas.ring;

import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Iterator;

import org.apache.log4j.Logger;

import edu.unima.ky.parallel.Semaphore;

import edu.jas.util.ThreadPool;
import edu.jas.util.Terminator;

import edu.jas.poly.ExpVector;
import edu.jas.poly.OrderedPolynomial;

/**
 * Groebner Bases class.
 * Implements Groebner bases and GB test.
 * @author Heinz Kredel
 */

public class GroebnerBasePS  {

    private static final Logger logger = Logger.getLogger(GroebnerBasePS.class);

    /**
     * Groebner base test
     */

    public static boolean isDIRPGB(List Pp) {  
        OrderedPolynomial pi, pj, s, h;
	for ( int i = 0; i < Pp.size(); i++ ) {
	    pi = (OrderedPolynomial) Pp.get(i);
            for ( int j = i+1; j < Pp.size(); j++ ) {
                pj = (OrderedPolynomial) Pp.get(j);
		if ( ! Reduction.GBCriterion4( pi, pj ) ) continue;
		s = Reduction.SPolynomial( pi, pj );
		if ( s.isZERO() ) continue;
		h = Reduction.Normalform( Pp, s );
		if ( ! h.isZERO() ) return false;
	    }
	}
	return true;
    }


    /**
     * Groebner base using pairlist class.
     */

    public static ArrayList DIRPGB(List Pp) {  
        OrderedPolynomial p;
        ArrayList P = new ArrayList();
        OrderedPairlistPS pairlist = null; 
        int l = Pp.size();
        ListIterator it = Pp.listIterator();
        while ( it.hasNext() ) { 
            p = (OrderedPolynomial) it.next();
            if ( p.length() > 0 ) {
               p = p.monic();
               if ( p.isONE() ) {
		  P.clear(); P.add( p );
                  return P; // since no threads are activated
	       }
               P.add( (Object) p );
	       if ( pairlist == null ) {
                  pairlist = new OrderedPairlistPS( p.getTermOrder() );
               }
               // putOne not required
               pairlist.put( p, null );
	    }
            else l--;
	}
        if ( l <= 1 ) {
           return P; // since no threads are activated
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

              H = Reduction.Normalform( P, S );
              if ( H.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
	      }

	      H = H.monic();
	      if ( H.isONE() ) {
		  P.clear(); P.add( H );
                  return P; // since no threads are activated
	      }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("H = " + H );
	      }
              if ( H.length() > 0 ) {
		 l++;
                 P.add( (Object) H );
                 pairlist.put( H, null );
              }
	}
	P = DIGBMI(P);
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                  + " #total = " + pairlist.pairCount());
	return P;
    }


    /**
     * Minimal ordered groebner basis.
     */

    public static ArrayList DIGBMI(List Pp) {  
        OrderedPolynomial a;
        ArrayList P = new ArrayList();
        ListIterator it = Pp.listIterator();
        while ( it.hasNext() ) { 
            a = (OrderedPolynomial) it.next();
            if ( a.length() != 0 ) { // always true
	       // already monic  a = a.monic();
               P.add( (Object) a );
	    }
	}
        if ( P.size() <= 1 ) return P;

        ExpVector e;        
        ExpVector f;        
        OrderedPolynomial p;
        ArrayList Q = new ArrayList();
	boolean mt;

        while ( P.size() > 0 ) {
            a = (OrderedPolynomial) P.remove(0);
	    e = a.leadingExpVector();

            it = P.listIterator();
	    mt = false;
	    while ( it.hasNext() && ! mt ) {
               p = (OrderedPolynomial) it.next();
               f = p.leadingExpVector();
	       mt = ExpVector.EVMT( e, f );
	    }
            it = Q.listIterator();
	    while ( it.hasNext() && ! mt ) {
               p = (OrderedPolynomial) it.next();
               f = p.leadingExpVector();
	       mt = ExpVector.EVMT( e, f );
	    }
	    if ( ! mt ) {
		Q.add( (Object)a );
	    } else {
		// System.out.println("dropped " + a.length());
	    }
	}
	P = Q;
        if ( P.size() <= 1 ) return P;

        Q = new ArrayList();
        while ( P.size() > 0 ) {
            a = (OrderedPolynomial) P.remove(0);
	    // System.out.println("doing " + a.length());
            a = Reduction.Normalform( P, a );
            a = Reduction.Normalform( Q, a );
            Q.add( (Object)a );
	}
	return Q;
    }

}
