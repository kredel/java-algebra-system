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

    public static <C extends RingElem<C>> 
           ArrayList<GenPolynomial<C>> 
                  GB(List<GenPolynomial<C>> F, 
                     int threads) {  
        return GB(0,F,threads);
    }

    public static <C extends RingElem<C>>
           ArrayList<GenPolynomial<C>> 
                  GB(int modv,
                     List<GenPolynomial<C>> F, 
                     int threads) {  
        GenPolynomial<C> p;
        ArrayList<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        OrderedPairlist<C> pairlist = null; 
        int l = F.size();
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( p.length() > 0 ) {
               p = p.monic();
               if ( p.isONE() ) {
		  G.clear(); G.add( p );
                  return G; // since no threads activated jet
	       }
               G.add( p );
	       if ( pairlist == null ) {
                  pairlist = new OrderedPairlist<C>( modv, p.ring );
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
	      R = new Reducer<C>( fin, G, pairlist );
	      T.addJob( R );
	}
	fin.done();
        logger.debug("#parallel list = "+G.size());
	G = GBmi(G,T);
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

    static class Reducer<C extends RingElem<C>> implements Runnable {
	private List<GenPolynomial<C>> G;
	private OrderedPairlist<C> pairlist;
	private Terminator pool;
        private static Logger logger = Logger.getLogger(Reducer.class);

	Reducer(Terminator fin, 
                List<GenPolynomial<C>> G, 
                OrderedPairlist<C> L) {
	    pool = fin;
	    this.G = G;
	    pairlist = L;
	} 

	public void run() {
           Pair<C> pair;
           GenPolynomial<C> pi;
           GenPolynomial<C> pj;
           GenPolynomial<C> S;
           GenPolynomial<C> H;
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

              pair = pairlist.removeNext();
              if ( pair == null ) {
                 continue; 
              }

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

              H = Reduction.<C>normalformMod( G, S );
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
              if ( logger.isDebugEnabled() ) {
                 logger.debug("H = " + H );
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

    public static <C extends RingElem<C>>
           ArrayList<GenPolynomial<C>> 
                  GBmi(List<GenPolynomial<C>> Fp, 
                       ThreadPool T) {  
        GenPolynomial<C> a;
        ArrayList<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        ListIterator<GenPolynomial<C>> it = Fp.listIterator();
        while ( it.hasNext() ) { 
            a = it.next();
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
		F.add( a ); // no thread at this point
	    } else {
		// System.out.println("dropped " + a.length());
	    }
	}
	G = F;
        if ( G.size() <= 1 ) {
           return G;
        }

	MiReducer<C>[] mirs = new MiReducer[ G.size() ];
        int i = 0;
        F = new ArrayList<GenPolynomial<C>>( G.size() );
        while ( G.size() > 0 ) {
            a = G.remove(0);
	    // System.out.println("doing " + a.length());
	    mirs[i] = new MiReducer( (List<GenPolynomial<C>>)G.clone(), 
                                     (List<GenPolynomial<C>>)F.clone(), 
                                     a );
	    T.addJob( mirs[i] );
	    i++;
            F.add( a );
	}
	G = F;
	F = new ArrayList<GenPolynomial<C>>( G.size() );
	for ( i = 0; i < mirs.length; i++ ) {
	    a = mirs[i].getNF();
            F.add( a );
	}
	return F;
    }


    /**
     * reducing worker threads for minimal GB
     */

    static class MiReducer<C extends RingElem<C>> implements Runnable {
	private List<GenPolynomial<C>> G;
	private List<GenPolynomial<C>> F;
	private GenPolynomial<C> S;
	private GenPolynomial<C> H;
	private Semaphore done = new Semaphore(0);
        private static Logger logger = Logger.getLogger(MiReducer.class);

	MiReducer(List<GenPolynomial<C>> G, 
                  List<GenPolynomial<C>> F, 
                  GenPolynomial<C> p) {
	    this.G = G;
	    this.F = F;
	    S = p;
	    H = S;
	} 

	public GenPolynomial<C> getNF() {
	    try { done.P();
	    } catch (InterruptedException e) { }
	    return H;
	}

	public void run() {
	    if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
	    }
            H = Reduction.<C>normalformMod( G, H );
            H = Reduction.<C>normalformMod( F, H );
            done.V();
	    if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
	    }
	    // H = H.monic();
	}

    }


}
