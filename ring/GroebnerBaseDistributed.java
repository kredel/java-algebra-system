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
import java.io.IOException;

import org.apache.log4j.Logger;

//import edu.unima.ky.parallel.Barrier2;
import edu.unima.ky.parallel.Semaphore;
import edu.unima.ky.parallel.ChannelFactory;
import edu.unima.ky.parallel.SocketChannel;

import edu.jas.ThreadPool;
import edu.jas.Terminator;
import edu.jas.DistributedListServer;
import edu.jas.DistributedList;

import edu.jas.poly.ExpVector;
import edu.jas.poly.OrderedPolynomial;

/**
 * Groebner Base Distributed class.
 * Implements a distributed memory parallel version of Groebner bases.
 * @author Heinz Kredel
 */

public class GroebnerBaseDistributed  {

    private static final Logger logger = Logger.getLogger(GroebnerBaseDistributed.class);

    /**
     * Distributed Groebner base using pairlist class.
     * slaves maintain pairlist
     * distributed slaves do reduction
     * @unfug
     */

    public static ArrayList DIRPGB(List Pp, int threads) {  
	ArrayList al = null;
	try {
            al = DIRPGBServer(Pp, threads, 4711);
	} catch (IOException e) {
	    e.printStackTrace();
	} 
	return al;
    }

    /**
     * GB distributed server
     */

    public static ArrayList DIRPGBServer(List Pp, int threads, int port) 
                            throws IOException {  

	ChannelFactory cf = new ChannelFactory(port);
	DistributedListServer dls = new DistributedListServer(cf);
	dls.init();

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
                  dls.terminate();
                  return P;
	       }
               P.add( (Object) p );
	       if ( pairlist == null ) 
                  pairlist = new OrderedPairlist( p.getTermOrder() );
               pairlist.put( p );
	    }
            else l--;
	}
        if ( l <= 1 ) {
           dls.terminate();
           return P;
	}

	if ( threads < 1 ) threads = 1;
	while ( dls.size() < threads ) {
	    try {
		Thread.currentThread().sleep(100);
	    } catch (InterruptedException e) {
              dls.terminate();
              return null;
	    }
	}
	DistributedList theList = new DistributedList( "localhost" );
	Iterator il = P.iterator();
	while ( il.hasNext() ) {
	    theList.add( il.next() );
	}

	ThreadPool T = new ThreadPool(threads);
	Terminator fin = new Terminator(threads);
	ReducerServer R;
	for ( int i = 0; i < threads; i++ ) {
	      R = new ReducerServer( fin, cf, theList, P, pairlist );
	      T.addJob( R );
	}
	fin.done();
	// System.out.println("\n main loop ended \n");
	P = DIGBMI(P,T);
	T.terminate();
        //System.out.println();
	dls.terminate();
	return P;
    }


    /**
     * distributed server reducing worker threads
     */

    static class ReducerServer implements Runnable {
	private Terminator pool;
	private ChannelFactory cf;
	private SocketChannel pairChannel;
	private DistributedList theList;
	private List P;
	private OrderedPairlist pairlist;
        private static Logger logger = Logger.getLogger(ReducerClient.class);

	ReducerServer(Terminator fin, ChannelFactory cf, DistributedList dl, List P, OrderedPairlist L) {
	    pool = fin;
	    this.cf = cf;
	    theList = dl;
	    this.P = P;
	    pairlist = L;
	} 

	public void run() {
	   try {
	        pairChannel = cf.getChannel();
	   } catch (InterruptedException e) {
                logger.info("get pair channel interrupted");
		return;
	   } 

           OrderedPairlist.Pair pair;
           OrderedPolynomial pi;
           OrderedPolynomial pj;
           OrderedPolynomial S;
           OrderedPolynomial H = null;
	   boolean set = false;
	   boolean goon = true;
	   int red = 0;
	   int sleeps = 0;

	   // while more requests

	   while ( goon ) {
	       Object dummy = null;
	       try {
                   dummy = pairChannel.receive();
	       } catch (IOException e) {
                   goon = false;
	       } catch (ClassNotFoundException e) {
                   goon = false;
	       }
	       if ( dummy == null ) continue;

	       // receive request
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

		   pair = (OrderedPairlist.Pair) pairlist.removeNext();
		   if ( pair == null ) continue; 

		   /*
		    * send pair to client, receive H
		    */
		   try {
		       pairChannel.send( pair );
		   } catch (IOException e) {
		       goon = false;
		   }
		   Object rh = null;
		   try {
		       rh = pairChannel.receive();
		   } catch (IOException e) {
		       goon = false;
		   } catch (ClassNotFoundException e) {
		       goon = false;
		   }
		   if ( rh == null ) continue;

		   /*
		    * update pair list
		    */
		   H = (OrderedPolynomial)rh;
		   logger.info("H   = " + H);
		   if ( H.isONE() ) {
		       synchronized (P) {
			   P.clear(); P.add( H );
		       }
		       pool.allIdle();
		       return;
		   }
		   pairlist.put( H );
	       }
	   }
           logger.info( "terminated, done " + red + " reductions");
	}

    }

   

    /**
     * GB distributed client
     */

    public static void DIRPGBClient(String host, int port) 
                            throws IOException {  

	ChannelFactory cf = new ChannelFactory(port);

	DistributedList theList = new DistributedList( host );

	SocketChannel pairChannel = cf.getChannel(host,port);

        ReducerClient R = new ReducerClient(pairChannel,theList);
	R.run();

	return;
    }

    /**
     * distributed clients reducing worker threads
     */

    static class ReducerClient implements Runnable {
	private SocketChannel pairChannel;
	private DistributedList theList;
        private static Logger logger = Logger.getLogger(ReducerServer.class);

	ReducerClient(SocketChannel pc, DistributedList dl) {
 	     pairChannel = pc;
	     theList = dl;
	} 

	public void run() {
           OrderedPairlist.Pair pair = null;
           OrderedPolynomial pi;
           OrderedPolynomial pj;
           OrderedPolynomial S;
           OrderedPolynomial H = null;
	   boolean set = false;
	   boolean goon = true;
	   int red = 0;
	   int sleeps = 0;
           while ( goon ) {

	       /*
		* request pair and process, send result
		*/
	       // pair = (OrderedPairlist.Pair) pairlist.removeNext();

	       Object dummy = new Object();
	       try {
		    pairChannel.send(dummy);
	       } catch (IOException e) {
		      goon = false;
               }
	       Object pp = null;
	       try {
		   pp = pairChannel.receive();
	       } catch (IOException e) {
		   goon = false;
	       } catch (ClassNotFoundException e) {
		   goon = false;
	       }
	       H = null;
	       if ( pp != null ) {
		   pair = (OrderedPairlist.Pair)pp;

		   pi = pair.pi; 
		   pj = pair.pj; 
		   //System.out.println("pi  = " + pi);
		   //System.out.println("pj  = " + pj);

		   S = Reduction.SPolynomial( pi, pj );
		   //System.out.println("S   = " + S);
		   if ( !S.isZERO() ) {
		       if ( logger.isDebugEnabled() ) {
			   logger.debug("ht(S) = " + S.leadingExpVector() );
		       }
		       H = Reduction.Normalform( theList.getList(), S );
		       red++;
		       if ( H.isZERO() ) {
			   if ( logger.isDebugEnabled() ) {
			       logger.debug("ht(H) = " + H.leadingExpVector() );
			   }
			   H = H.monic();
			   // System.out.println("H   = " + H);
			   synchronized (theList) {
			       theList.add( H );
			   }
		       }
		   }
	       }

	       // send H or null
	       try {
		    pairChannel.send(H);
	       } catch (IOException e) {
		      goon = false;
               }
	   }
	   pairChannel.close();
	   logger.info( "terminated, done " + red + " reductions");
	}

    }


    /**
     * Minimal ordered groebner basis, distributed.
     */

    public static ArrayList DIGBMI(List Pp, ThreadPool T) {  
        return DIGBMIServer(Pp, T);
    }

    public static ArrayList DIGBMIServer(List Pp, ThreadPool T) {  
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

	MiReducerServer[] mirs = new MiReducerServer[ P.size() ];
        int i = 0;
        Q = new ArrayList( P.size() );
        while ( P.size() > 0 ) {
            a = (OrderedPolynomial) P.remove(0);
	    // System.out.println("doing " + a.length());
	    mirs[i] = new MiReducerServer( (List)P.clone(), (List)Q.clone(), a );
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
     * distributed server reducing worker threads for minimal GB
     */

    static class MiReducerServer implements Runnable {
	private List P;
	private List Q;
	private OrderedPolynomial S;
	private OrderedPolynomial H;
	private Semaphore done = new Semaphore(0);
        private static Logger logger = Logger.getLogger(MiReducerServer.class);

	MiReducerServer(List P, List Q, OrderedPolynomial p) {
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

    /**
     * distributed clients reducing worker threads for minimal GB
     */

    static class MiReducerClient implements Runnable {
	private List P;
	private List Q;
	private OrderedPolynomial S;
	private OrderedPolynomial H;
	private Semaphore done = new Semaphore(0);
        private static Logger logger = Logger.getLogger(MiReducerClient.class);

	MiReducerClient(List P, List Q, OrderedPolynomial p) {
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
