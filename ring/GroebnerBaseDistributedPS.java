/*
 * $Id$
 */

package edu.jas.ring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.util.DistributedList;
import edu.jas.util.DistributedListServer;
import edu.jas.util.Terminator;
import edu.jas.util.ThreadPool;
import edu.unima.ky.parallel.ChannelFactory;
import edu.unima.ky.parallel.Semaphore;
import edu.unima.ky.parallel.SocketChannel;

/**
 * Groebner Base Distributed class.
 * Implements a distributed memory parallel version of Groebner bases.
 * @author Heinz Kredel
 */

public class GroebnerBaseDistributedPS  {

    private static final Logger logger = Logger.getLogger(GroebnerBaseDistributedPS.class);

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

	final int DL_PORT = port + 100;
        ChannelFactory cf = new ChannelFactory(port);
        DistributedListServer dls = new DistributedListServer(DL_PORT);
        dls.init();
        logger.debug("dist-list server running");

        OrderedPolynomial p;
        ArrayList P = new ArrayList();
        OrderedPairlistPS pairlist = null; 
        boolean oneInGB = false;
        int l = Pp.size();
        ListIterator it = Pp.listIterator();
        while ( it.hasNext() ) { 
            p = (OrderedPolynomial) it.next();
            if ( p.length() > 0 ) {
               p = p.monic();
               if ( p.isONE() ) {
                  oneInGB = true;
                  P.clear(); 
                  P.add( p );
                  //return P; must signal termination to others
               }
               if ( ! oneInGB ) {
                  P.add( p );
               }
               if ( pairlist == null ) {
                  pairlist = new OrderedPairlistPS( p.getTermOrder() );
               }
               if ( p.isONE() ) {
                   pairlist.putOne( p, null );
               } else {
                   pairlist.putParallel( p, null );
               }
            }
            else l--;
        }
        if ( l <= 1 ) {
           //return P; must signal termination to others
        }

        logger.debug("looking for clients");
        long t = System.currentTimeMillis();
        if ( threads < 1 ) threads = 1;
	/*
        while ( dls.size() < threads ) {
            try { 
                //System.out.print("*");
                Thread.currentThread().sleep(200);
            } catch (InterruptedException e) {
              e.printStackTrace();
              cf.terminate();
              dls.terminate();
              return null;
            }
        }
        t = System.currentTimeMillis() - t;
        logger.info("all " + threads + " clients seen, wait time = " + t + " milliseconds");
	*/

        DistributedList theList = new DistributedList( "localhost", DL_PORT );
        Iterator il = P.iterator();
        while ( il.hasNext() ) {
            theList.add( il.next() );
        }

        ThreadPool T = new ThreadPool(threads);
        Terminator fin = new Terminator(threads);
        ReducerServerPS R;
        for ( int i = 0; i < threads; i++ ) {
              R = new ReducerServerPS( fin, cf, theList, P, pairlist );
              T.addJob( R );
        }
        logger.debug("main loop waiting");
        fin.done();
        int ps = theList.size();
        logger.debug("#distributed list = "+ps);
        // make sure all polynomials arrived
	// P = (ArrayList)theList.getList();
        P = pairlist.getList();
        logger.debug("#pairlist list = "+P.size());
        if ( ps != P.size() ) {
           logger.error("#distributed list = "+theList.size() 
                      + " #pairlist list = "+P.size() );
        }
        P = DIGBMI(P,T);
        cf.terminate();
        T.terminate();
        theList.terminate();
        dls.terminate();
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                  + " #total = " + pairlist.pairCount());
        return P;
    }


    /**
     * distributed server reducing worker threads
     */

    static class ReducerServerPS implements Runnable {
        private Terminator pool;
        private ChannelFactory cf;
        private SocketChannel pairChannel;
        private DistributedList theList;
        private List P;
        private OrderedPairlistPS pairlist;
        private static Logger logger = Logger.getLogger(ReducerServerPS.class);

        ReducerServerPS(Terminator fin, ChannelFactory cf, DistributedList dl, List P, OrderedPairlistPS L) {
            pool = fin;
            this.cf = cf;
            theList = dl;
            this.P = P;
            pairlist = L;
        } 

        public void run() {
           logger.debug("reducer server running");
           try {
                pairChannel = cf.getChannel();
           } catch (InterruptedException e) {
                logger.info("get pair channel interrupted");
                e.printStackTrace();
                return;
           } 
           logger.debug("pairChannel = "+pairChannel);

           Pair pair;
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
               // receive request
               logger.debug("receive request");
               Object dummy = null;
               try {
                   dummy = pairChannel.receive();
               } catch (IOException e) {
                   goon = false;
                   e.printStackTrace();
               } catch (ClassNotFoundException e) {
                   goon = false;
                   e.printStackTrace();
               }
               logger.debug("received request, dummy = "+dummy+" goon = "+goon);
               // if ( dummy == null ) continue;

               // find pair
               logger.debug("find pair");
               while ( goon && ( pairlist.hasNext() || pool.hasJobs() ) ) {
                   while ( ! pairlist.hasNext() ) {
                       // wait
		       if ( ! set ) {
                          pool.beIdle(); 
                          set = true;
		       }
                       if ( ! pool.hasJobs() && ! pairlist.hasNext() ) {
			   goon = false;
                           if ( set ) { 
	                      set = false;
                              pool.notIdle();
		           }
                           break;
		       }
                       try {
                           sleeps++;
                           if ( sleeps % 10 == 0 ) {
                               logger.info(" reducer is sleeping");
                           } else {
                               logger.debug("r");
                           }
                           Thread.sleep(100);
                       } catch (InterruptedException e) {
                           goon = false;
                           if ( set ) { 
	                      set = false;
                              pool.notIdle();
		           }
                           break;
                       }
                       if ( ! pool.hasJobs() && ! pairlist.hasNext() ) {
			  goon = false;
                          if ( set ) { 
	                     set = false;
                             pool.notIdle();
		          }
                          break;
		       }
                   }
                   if ( ! pairlist.hasNext() && ! pool.hasJobs() ) {
                      goon = false;
                      if ( set ) { 
			 set = false;
                         pool.notIdle();
		      }
                      continue; //break?
                   }
                   if ( set ) {
		      set = false;
                      pool.notIdle();
		   }

                   pair = (Pair) pairlist.removeNextParallel();
                   // if ( pair == null ) continue; 

                   /*
                    * send pair to client, receive H
                    */
                   logger.debug("send pair = " + pair );
                   try {
                       pairChannel.send( pair );
                   } catch (IOException e) {
                       goon = false;
                       e.printStackTrace();
                   }
                   logger.debug("#distributed list = "+theList.size());
                   logger.debug("receive H polynomial");
                   Object rh = null;
                   try {
                       rh = pairChannel.receive();
                   } catch (IOException e) {
                       goon = false;
                       e.printStackTrace();
                   } catch (ClassNotFoundException e) {
                       goon = false;
                       e.printStackTrace();
                   }
                   logger.debug("received H polynomial");
                   if ( rh == null ) {
                       if ( pair != null ) {
                           pair.setZero();
                       }
                   } else {
                       /*
                        * update pair list
                        */
                       red++;
                       H = (OrderedPolynomial)rh;
                       logger.debug("H = " + H);
                       if ( H.isZERO() ) {
                           pair.setZero();
                       } else {
                           if ( H.isONE() ) {
                               // pool.allIdle();
                               pairlist.putOne( H, pair );
                               goon = false;
                           } else {
                               pairlist.putParallel( H, pair );
			   }
                       }
                   }

	           if ( ! pairlist.hasNext() && ! pool.hasJobs() ) {
                      goon = false;
		      break;
                   }

               // receive request
               logger.debug("receive request");
               dummy = null;
               try {
                   dummy = pairChannel.receive();
               } catch (IOException e) {
                   goon = false;
                   e.printStackTrace();
               } catch (ClassNotFoundException e) {
                   goon = false;
                   e.printStackTrace();
               }
               logger.debug("received request, dummy = "+dummy+" goon = "+goon);

               }
           }
           logger.info( "terminated, done " + red + " reductions");

           /*
            * send end mark pair to client
            */
           logger.debug("send pair = " + new Integer(-1) );
           try {
               pairChannel.send( new Integer(-1) );
           } catch (IOException e) {
               goon = false;
               e.printStackTrace();
           }
	   // pool.allIdle();
	   pool.beIdle();
           pairChannel.close();
        }

    }

   

    /**
     * GB distributed client
     */

    public static void DIRPGBClient(String host, int port) 
                            throws IOException {  

        ChannelFactory cf = new ChannelFactory(port+10); // != port for localhost
        SocketChannel pairChannel = cf.getChannel(host,port);

	final int DL_PORT = port + 100;
        DistributedList theList = new DistributedList(host, DL_PORT );

        ReducerClientPS R = new ReducerClientPS(pairChannel,theList);
        R.run();

        pairChannel.close();
        theList.terminate();
        cf.terminate();
        return;
    }

    /**
     * distributed clients reducing worker threads
     */

    static class ReducerClientPS implements Runnable {
        private SocketChannel pairChannel;
        private DistributedList theList;
        private static Logger logger = Logger.getLogger(ReducerClientPS.class);

        ReducerClientPS(SocketChannel pc, DistributedList dl) {
             pairChannel = pc;
             theList = dl;
        } 

        public void run() {
           logger.debug("pairChannel = "+pairChannel);
           logger.debug("reducer client running");
           Pair pair = null;
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
               // pair = (Pair) pairlist.removeNextParallel();

               Object dummy = new Integer(5);
               logger.debug("send request, dummy = "+dummy);
               try {
                    pairChannel.send(dummy);
               } catch (IOException e) {
                   goon = false;
                   e.printStackTrace();
               }
               logger.debug("receive pair, goon = "+goon);
               Object pp = null;
               try {
                   pp = pairChannel.receive();
               } catch (IOException e) {
                   goon = false;
                   e.printStackTrace();
               } catch (ClassNotFoundException e) {
                   goon = false;
                   e.printStackTrace();
               }
               logger.info("received pair = " + pp + " goon = "+goon);
               H = null;
               if ( pp instanceof Integer) {
                   Integer done = (Integer) pp;
                   if ( done.intValue() == -1 ) {
                       goon = false;
                       continue;
                   }
               }
               if ( pp != null ) {
                   pair = (Pair)pp;

                   pi = pair.pi; 
                   pj = pair.pj; 
                   //System.out.println("pi  = " + pi);
                   //System.out.println("pj  = " + pj);

                   S = Reduction.SPolynomial( pi, pj );
                   //System.out.println("S   = " + S);
                   if ( S.isZERO() ) {
                       // pair.setZero(); does not work in dist
                   } else {
                       if ( logger.isDebugEnabled() ) {
                           logger.debug("ht(S) = " + S.leadingExpVector() );
                       }
                       H = Reduction.Normalform( theList.getList(), S );
                       red++;
                       if ( H.isZERO() ) {
                           // pair.setZero(); does not work in dist
                       } else {
                           if ( logger.isDebugEnabled() ) {
                               logger.debug("ht(H) = " + H.leadingExpVector() );
                           }
                           H = H.monic();
                           // System.out.println("H = " + H);
                           synchronized (theList) {
                               theList.add( H );
                           }
                       }
                   }
               }

               // send H or must send null
               logger.debug("#distributed list = "+theList.size());
               logger.debug("send H polynomial = " + H);
               try {
                    pairChannel.send(H);
               } catch (IOException e) {
                    goon = false;
                    e.printStackTrace();
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

        MiReducerServerPS[] mirs = new MiReducerServerPS[ P.size() ];
        int i = 0;
        Q = new ArrayList( P.size() );
        while ( P.size() > 0 ) {
            a = (OrderedPolynomial) P.remove(0);
            // System.out.println("doing " + a.length());
            mirs[i] = new MiReducerServerPS( (List)P.clone(), (List)Q.clone(), a );
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

    static class MiReducerServerPS implements Runnable {
        private List P;
        private List Q;
        private OrderedPolynomial S;
        private OrderedPolynomial H;
        private Semaphore done = new Semaphore(0);
        private static Logger logger = Logger.getLogger(MiReducerServerPS.class);

        MiReducerServerPS(List P, List Q, OrderedPolynomial p) {
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

    static class MiReducerClientPS implements Runnable {
        private List P;
        private List Q;
        private OrderedPolynomial S;
        private OrderedPolynomial H;
        private Semaphore done = new Semaphore(0);
        private static Logger logger = Logger.getLogger(MiReducerClientPS.class);

        MiReducerClientPS(List P, List Q, OrderedPolynomial p) {
            this.P = P;
            this.Q = Q;
            S = p;
            H = S;
        } 

        public OrderedPolynomial getNF() {
            try { done.P();
            } catch (InterruptedException unused) { }
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
