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
import edu.jas.util.Terminator;
import edu.jas.util.ThreadPool;
import edu.unima.ky.parallel.Semaphore;

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

    public static ArrayList DIRPGB(List F, int threads) {  
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
                  return G; // since no threads activated jet
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
           return G; // since no threads activated jet
        }

	if ( threads < 1 ) {
           threads = 1;
        }
	ThreadPool T = new ThreadPool(threads);
	Terminator fin = new Terminator(threads);
	Reducer R;
	for ( int i = 0; i < threads; i++ ) {
	      R = new Reducer( fin, G, pairlist );
	      T.addJob( R );
	}
	fin.done();
	G = DIGBMI(G,T);
	T.terminate();
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    //+ " #total = " + pairlist.pairCount()
                   );
	return G;
    }


    /**
     * reducing worker threads
     */

    static class Reducer implements Runnable {
	private List G;
	private OrderedPairlist pairlist;
	private Terminator pool;
        private static Logger logger = Logger.getLogger(Reducer.class);

	Reducer(Terminator fin, List G, OrderedPairlist L) {
	    pool = fin;
	    this.G = G;
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
		      Thread.sleep(100);
		  } catch (InterruptedException e) {
                     break;
		  }
		  if ( ! pool.hasJobs() ) {
                     break;
                  }
              }
              if ( ! pairlist.hasNext() && ! pool.hasJobs() ) {
                 break;
              }
              if ( set ) {
                 pool.notIdle();
              }

              pair = (Pair) pairlist.removeNext();
              if ( pair == null ) {
                 continue; 
              }

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
              H = Reduction.Normalform( G, S );
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
                  pairlist.putOne( H ); // not really required
		  synchronized (G) {
                      G.clear(); G.add( H );
		  }
	          pool.allIdle();
                  return;
	      }
              synchronized (G) {
                     G.add( H );
              }
              pairlist.put( H );
	   }
           logger.info( "terminated, done " + red + " reductions");
	}

    }


    /**
     * Minimal ordered groebner basis, parallel.
     */

    public static ArrayList DIGBMI(List Fp, ThreadPool T) {  
        OrderedPolynomial a;
        ArrayList G = new ArrayList();
        ListIterator it = Fp.listIterator();
        while ( it.hasNext() ) { 
            a = (OrderedPolynomial) it.next();
            if ( a.length() != 0 ) { // always true
	       // already monic  a = a.monic();
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

	MiReducer[] mirs = new MiReducer[ G.size() ];
        int i = 0;
        F = new ArrayList( G.size() );
        while ( G.size() > 0 ) {
            a = (OrderedPolynomial) G.remove(0);
	    // System.out.println("doing " + a.length());
	    mirs[i] = new MiReducer( (List)G.clone(), (List)F.clone(), a );
	    T.addJob( mirs[i] );
	    i++;
            F.add( a );
	}
	G = F;
	F = new ArrayList( G.size() );
	for ( i = 0; i < mirs.length; i++ ) {
	    a = (OrderedPolynomial) mirs[i].getNF();
            F.add( a );
	}
	return F;
    }


    /**
     * reducing worker threads for minimal GB
     */

    static class MiReducer implements Runnable {
	private List G;
	private List F;
	private OrderedPolynomial S;
	private OrderedPolynomial H;
	private Semaphore done = new Semaphore(0);
        private static Logger logger = Logger.getLogger(MiReducer.class);

	MiReducer(List G, List F, OrderedPolynomial p) {
	    this.G = G;
	    this.F = F;
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
            H = Reduction.Normalform( G, H );
            H = Reduction.Normalform( F, H );
            done.V();
	    if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
	    }
	    // H = H.monic();
	}

    }


}
