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

import edu.jas.ThreadPool;
import edu.jas.Terminator;

import edu.jas.poly.ExpVector;
import edu.jas.poly.OrderedPolynomial;

/**
 * Groebner Base Parallel class.
 * Implements a shared memory parallel version of Groebner bases.
 * @author Heinz Kredel
 */

public class GroebnerBaseParallel  {

    private static final Logger logger = Logger.getLogger(GroebnerBaseParallel.class);

    /**
     * Parallel Groebner base using pairlist class.
     * slaves maintain pairlist
     */

    public static ArrayList DIRPGB(List Pp, int threads) {  
        OrderedPolynomial p;
        ArrayList P = new ArrayList();
        OrderedPairlist pairlist = null; 
        int l = Pp.size();
        ListIterator it = Pp.listIterator();
        while ( it.hasNext() ) { 
            p = (OrderedPolynomial) it.next();
            if ( p.length() > 0 ) {
               p = p.monic();
               if ( p.isONE() ) {
		  P.clear(); P.add( p );
                  return P; // since no threads activated jet
	       }
               P.add( (Object) p );
	       if ( pairlist == null ) {
                  pairlist = new OrderedPairlist( p.getTermOrder() );
               }
               // putOne not required
               pairlist.putParallel( p, null );
	    }
            else l--;
	}
        if ( l <= 1 ) return P; // since no threads activated jet

	if ( threads < 1 ) threads = 1;
	ThreadPool T = new ThreadPool(threads);
	Terminator fin = new Terminator(threads);
	Reducer R;
	for ( int i = 0; i < threads; i++ ) {
	      R = new Reducer( fin, P, pairlist );
	      T.addJob( R );
	}
	fin.done();
	P = DIGBMI(P,T);
	T.terminate();
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                  + " #total = " + pairlist.pairCount());
	return P;
    }


    /**
     * reducing worker threads
     */

    static class Reducer implements Runnable {
	private List P;
	private OrderedPairlist pairlist;
	private Terminator pool;
        private static Logger logger = Logger.getLogger(Reducer.class);

	Reducer(Terminator fin, List P, OrderedPairlist L) {
	    pool = fin;
	    this.P = P;
	    pairlist = L;
	} 

	public void run() {
           Pair pair;
           OrderedPolynomial pi;
           OrderedPolynomial pj;
           OrderedPolynomial S;
           OrderedPolynomial H;
	   boolean set = false;
	   int red = 0;
	   int sleeps = 0;
           while ( pairlist.hasNext() || pool.hasJobs() ) {
	      while ( ! pairlist.hasNext() ) {
                  // wait
                  pool.beIdle(); set = true;
		  try {
		      sleeps++;
                      if ( sleeps % 10 == 0 ) {
                         logger.info(" reducer is sleeping");
		      } else {
                         logger.debug("r");
		      }
		      Thread.currentThread().sleep(100);
		  } catch (InterruptedException e) {
                     break;
		  }
		  if ( ! pool.hasJobs() ) break;
              }
              if ( ! pairlist.hasNext() && ! pool.hasJobs() ) break;
              if ( set ) pool.notIdle();

              pair = (Pair) pairlist.removeNextParallel();
              if ( pair == null ) continue; 

              pi = pair.pi; 
              pj = pair.pj; 
	      //System.out.println("pi  = " + pi);
              //System.out.println("pj  = " + pj);

              S = Reduction.SPolynomial( pi, pj );
              //System.out.println("S   = " + S);
              if ( S.isZERO() ) { 
                 pair.setZero();
                 continue;
              }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
	      }
              H = Reduction.Normalform( P, S );
	      red++;
              if ( H.isZERO() ) {
                 pair.setZero();
                 continue;
              }
	      if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
	      }
              H = H.monic();
              // System.out.println("H   = " + H);
	      if ( H.isONE() ) { 
                  pairlist.putOne( H, pair ); // not really required
		  synchronized (P) {
                      P.clear(); P.add( H );
		  }
	          pool.allIdle();
                  return;
	      }
              synchronized (P) {
                     P.add( H );
              }
              pairlist.putParallel( H, pair );
	   }
           logger.info( "terminated, done " + red + " reductions");
	}

    }


    /**
     * Minimal ordered groebner basis, parallel.
     */

    public static ArrayList DIGBMI(List Pp, ThreadPool T) {  
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

	MiReducer[] mirs = new MiReducer[ P.size() ];
        int i = 0;
        Q = new ArrayList( P.size() );
        while ( P.size() > 0 ) {
            a = (OrderedPolynomial) P.remove(0);
	    // System.out.println("doing " + a.length());
	    mirs[i] = new MiReducer( (List)P.clone(), (List)Q.clone(), a );
	    T.addJob( mirs[i] );
	    i++;
            Q.add( (Object)a );
	}
	P = Q;
	Q = new ArrayList( P.size() );
	for ( i = 0; i < mirs.length; i++ ) {
	    a = (OrderedPolynomial) mirs[i].getNF();
            Q.add( (Object)a );
	}
	return Q;
    }


    /**
     * reducing worker threads for minimal GB
     */

    static class MiReducer implements Runnable {
	private List P;
	private List Q;
	private OrderedPolynomial S;
	private OrderedPolynomial H;
	private Semaphore done = new Semaphore(0);
        private static Logger logger = Logger.getLogger(MiReducer.class);

	MiReducer(List P, List Q, OrderedPolynomial p) {
	    this.P = P;
	    this.Q = Q;
	    S = p;
	    H = S;
	} 

	public OrderedPolynomial getNF() {
	    try { done.P();
	    } catch (InterruptedException e) { }
	    return H;
	}

	public void run() {
	    if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
	    }
            H = Reduction.Normalform( P, H );
            H = Reduction.Normalform( Q, H );
            done.V();
	    if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
	    }
	    // H = H.monic();
	}

    }


}
