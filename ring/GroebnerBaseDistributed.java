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
        DistributedListServer dls = new DistributedListServer(port+100);
        dls.init();
        logger.debug("list server running");

        OrderedPolynomial p;
        ArrayList P = new ArrayList();
        OrderedPairlist pairlist = null; 
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
               if ( pairlist == null ) 
                  pairlist = new OrderedPairlist( p.getTermOrder() );
               if ( p.isONE() ) {
                  pairlist.putOne( p );
               } else {
                  pairlist.put( p );
               }
            }
            else l--;
        }
        if ( l <= 1 ) {
           //return P; must signal termination to others
        }

        logger.debug("looking for clients");
        if ( threads < 1 ) threads = 1;
        while ( dls.size()< threads ) {
            try { 
                System.out.print("*");
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
              e.printStackTrace();
              cf.terminate();
              dls.terminate();
              return null;
            }
        }
        logger.info("all clients seen");

        DistributedList theList = new DistributedList( "localhost", port+100 );
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
        logger.debug("main loop waiting");
        fin.done();
        // System.out.println("\n main loop ended \n");
        logger.info("#distributed list = "+theList.size());
        // make sure all polynomials arrived
        P = (ArrayList)theList.getList();
        int ps = P.size();
        P = pairlist.getList();
        logger.info("#pairlist list = "+P.size());
        if ( ps != P.size() ) {
           logger.error("#distributed list = "+theList.size() 
                      + " #pairlist list = "+P.size() );
        }
        P = DIGBMI(P,T);
        cf.terminate();
        T.terminate();
        theList.terminate();
        dls.terminate();
        //System.out.println();
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
        private static Logger logger = Logger.getLogger(ReducerServer.class);

        ReducerServer(Terminator fin, ChannelFactory cf, DistributedList dl, List P, OrderedPairlist L) {
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
                       pool.beIdle(); set = true;
                       if ( ! pool.hasJobs() ) break;
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
                   if ( ! pairlist.hasNext() && ! pool.hasJobs() ) {
                      goon = false;
                      continue; //break?
                   }
                   if ( set ) pool.notIdle();

                   pair = (Pair) pairlist.removeNext();
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
                   logger.info("#distributed list = "+theList.size());
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
                   logger.info("received H polynomial = " + rh);
                   if ( rh != null ) {
                       /*
                        * update pair list
                        */
                       red++;
                       H = (OrderedPolynomial)rh;
                       logger.debug("H = " + H);
                       if ( ! H.isZERO() ) {
                           if ( H.isONE() ) {
                               // pool.allIdle();
                               pairlist.putOne( H );
                               goon = false;
                           } else {
                               pairlist.put( H );
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
           pool.allIdle();
           pairChannel.close();
        }

    }

   

    /**
     * GB distributed client
     */

    public static void DIRPGBClient(String host, int port) 
                            throws IOException {  

        ChannelFactory cf = new ChannelFactory(port+10);
        SocketChannel pairChannel = cf.getChannel(host,port);

        DistributedList theList = new DistributedList(host, port+100 );

        ReducerClient R = new ReducerClient(pairChannel,theList);
        R.run();

        pairChannel.close();
        theList.terminate();
        cf.terminate();
        return;
    }

    /**
     * distributed clients reducing worker threads
     */

    static class ReducerClient implements Runnable {
        private SocketChannel pairChannel;
        private DistributedList theList;
        private static Logger logger = Logger.getLogger(ReducerClient.class);

        ReducerClient(SocketChannel pc, DistributedList dl) {
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
               // pair = (Pair) pairlist.removeNext();

               Object dummy = new Integer(5);
               logger.info("send request, dummy = "+dummy);
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
                   if ( !S.isZERO() ) {
                       if ( logger.isDebugEnabled() ) {
                           logger.debug("ht(S) = " + S.leadingExpVector() );
                       }
                       H = Reduction.Normalform( theList.getList(), S );
                       red++;
                       if ( ! H.isZERO() ) {
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

               // send H or null
               logger.info("#distributed list = "+theList.size());
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
