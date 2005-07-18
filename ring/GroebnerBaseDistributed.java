/*
 * $Id$
 */

package edu.jas.ring;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
//import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.ring.OrderedPairlist;

import edu.jas.util.DistHashTable;
import edu.jas.util.DistHashTableServer;
import edu.jas.util.Terminator;
import edu.jas.util.ThreadPool;

import edu.unima.ky.parallel.Semaphore;
import edu.unima.ky.parallel.ChannelFactory;
import edu.unima.ky.parallel.Semaphore;
import edu.unima.ky.parallel.SocketChannel;


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
     */


    public static <C extends RingElem<C>>
           ArrayList<GenPolynomial<C>> 
                  Server(List<GenPolynomial<C>> F, 
                         int threads, 
                         int port) 
                  throws IOException {  
        return Server(0,F,threads,port);
    }


    /**
     * GB distributed server
     */

    public static <C extends RingElem<C>>
           ArrayList<GenPolynomial<C>> 
                  Server(int modv,
                         List<GenPolynomial<C>> F, 
                         int threads, 
                         int port) 
                  throws IOException {  

        final int DL_PORT = port + 100;
        ChannelFactory cf = new ChannelFactory(port);
        DistHashTableServer dls = new DistHashTableServer(DL_PORT);
        dls.init();
        logger.debug("dist-list server running");

        GenPolynomial<C> p;
        ArrayList<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        OrderedPairlist<C> pairlist = null; 
        boolean oneInGB = false;
        int l = F.size();
        int unused;
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( p.length() > 0 ) {
               p = p.monic();
               if ( p.isONE() ) {
                  oneInGB = true;
                  G.clear(); 
                  G.add( p );
                  //return G; must signal termination to others
               }
               if ( ! oneInGB ) {
                  G.add( p );
               }
               if ( pairlist == null ) {
                  pairlist = new OrderedPairlist<C>( modv, p.ring );
               }
               // theList not updated here
               if ( p.isONE() ) {
                   unused = pairlist.putOne( p );
               } else {
                   unused = pairlist.put( p );
               }
            } else {
               l--;
            }
        }
        if ( l <= 1 ) {
           //return G; must signal termination to others
        }

        logger.debug("looking for clients");
        long t = System.currentTimeMillis();
        if ( threads < 1 ) {
           threads = 1;
        }
        // now in DL, uses resend for late clients
        //while ( dls.size() < threads ) { sleep(); }

        DistHashTable theList = new DistHashTable( "localhost", DL_PORT );
        ArrayList<GenPolynomial<C>> al = pairlist.getList();
        for ( int i = 0; i < al.size(); i++ ) {
            // no wait required
            theList.put( new Integer(i), al.get(i) );
        }

        ThreadPool T = new ThreadPool(threads);
        Terminator fin = new Terminator(threads);
        ReducerServer<C> R;
        for ( int i = 0; i < threads; i++ ) {
              R = new ReducerServer<C>( fin, cf, theList, G, pairlist );
              T.addJob( R );
        }
        logger.debug("main loop waiting");
        fin.done();
        int ps = theList.size();
        logger.debug("#distributed list = "+ps);
        // make sure all polynomials arrived
        // G = (ArrayList)theList.values();
        G = pairlist.getList();
        logger.debug("#pairlist list = "+G.size());
        if ( ps != G.size() ) {
           logger.error("#distributed list = "+theList.size() 
                      + " #pairlist list = "+G.size() );
        }
        long time = System.currentTimeMillis();
        ArrayList<GenPolynomial<C>> Gp 
             = MiServer(G,T); // not jet distributed but threaded
        time = System.currentTimeMillis() - time;
        logger.info("parallel gbmi = " + time);
        /*
        time = System.currentTimeMillis();
        G = GroebnerBase.<C>GBmi(G); // sequential
        time = System.currentTimeMillis() - time;
        logger.info("sequential gbmi = " + time);
        */
        G = Gp;
        cf.terminate();
        T.terminate();
        theList.terminate();
        dls.terminate();
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    //+ " #total = " + pairlist.pairCount()
                   );
        return G;
    }

  
    /**
     * GB distributed client
     */

    public static <C extends RingElem<C>>
           void 
                  Client(String host, int port) 
                  throws IOException {  

        ChannelFactory cf = new ChannelFactory(port+10); // != port for localhost
        SocketChannel pairChannel = cf.getChannel(host,port);

        final int DL_PORT = port + 100;
        DistHashTable theList = new DistHashTable(host, DL_PORT );

        ReducerClient<C> R = new ReducerClient<C>(pairChannel,theList);
        R.run();

        pairChannel.close();
        theList.terminate();
        cf.terminate();
        return;
    }


    /**
     * Minimal ordered groebner basis, distributed.
     * not jet distributed but threaded
     */

    public static <C extends RingElem<C>>
           ArrayList<GenPolynomial<C>> 
                  MiServer(List<GenPolynomial<C>> Fp, 
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
                F.add( a );
            } else {
                // System.out.println("dropped " + a.length());
            }
        }
        G = F;
        if ( G.size() <= 1 ) {
           return G;
        }

        MiReducerServer<C>[] mirs = new MiReducerServer[ G.size() ];
        int i = 0;
        F = new ArrayList<GenPolynomial<C>>( G.size() );
        while ( G.size() > 0 ) {
            a = G.remove(0);
            // System.out.println("doing " + a.length());
            mirs[i] = new MiReducerServer<C>((List<GenPolynomial<C>>)G.clone(), 
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

}



    /**
     * distributed server reducing worker threads
     */

class ReducerServer <C extends RingElem<C>> implements Runnable {
        private Terminator pool;
        private ChannelFactory cf;
        private SocketChannel pairChannel;
        private DistHashTable theList;
        private List<GenPolynomial<C>> G;
        private OrderedPairlist<C> pairlist;
        private static Logger logger = Logger.getLogger(ReducerServer.class);

      ReducerServer(Terminator fin, 
                    ChannelFactory cf, 
                    DistHashTable dl, 
                    List<GenPolynomial<C>> G, 
                    OrderedPairlist<C> L) {
            pool = fin;
            this.cf = cf;
            theList = dl;
            this.G = G;
            pairlist = L;
      } 

      public void run() {
           logger.debug("reducer server running");
           try {
                pairChannel = cf.getChannel();
           } catch (InterruptedException e) {
                logger.debug("get pair channel interrupted");
                e.printStackTrace();
                return;
           } 
           logger.debug("pairChannel = "+pairChannel);

           Pair pair;
           GenPolynomial<C> pi;
           GenPolynomial<C> pj;
           GenPolynomial<C> S;
           GenPolynomial<C> H = null;
           boolean set = false;
           boolean goon = true;
           int polIndex = -1;
           int red = 0;
           int sleeps = 0;

           // while more requests
           while ( goon ) {
               // receive request
               logger.debug("receive request");
               Object req = null;
               try {
                   req = pairChannel.receive();
               } catch (IOException e) {
                   goon = false;
                   e.printStackTrace();
               } catch (ClassNotFoundException e) {
                   goon = false;
                   e.printStackTrace();
               }
               logger.debug("received request, req = " + req);
               if ( req == null ) { 
                  goon = false;
                  break;
               }
               if ( ! (req instanceof GBTransportMessReq ) ) {
                  goon = false;
                  break;
               }

               // find pair
               logger.debug("find pair");
               while ( ! pairlist.hasNext() ) { // wait
                     if ( ! set ) {
                        pool.beIdle(); 
                        set = true;
                     }
                     if ( ! pool.hasJobs() && ! pairlist.hasNext() ) {
                        goon = false;
                        break;
                     }
                     try {
                         sleeps++;
                         if ( sleeps % 10 == 0 ) {
                            logger.info(" reducer is sleeping");
                         }
                         Thread.sleep(100);
                     } catch (InterruptedException e) {
                         goon = false;
                         break;
                     }
               }
               if ( ! pairlist.hasNext() && ! pool.hasJobs() ) {
                  goon = false;
                  break; //continue; //break?
               }
               if ( set ) {
                  set = false;
                  pool.notIdle();
               }

               pair = pairlist.removeNext();
               /*
                * send pair to client, receive H
                */
               logger.debug("send pair = " + pair );
               GBTransportMess msg = null;
               if ( pair != null ) {
                  msg = new GBTransportMessPairIndex( pair );
               } else {
                  msg = new GBTransportMess(); //End();
                  // goon ?= false;
               }
               try {
                   pairChannel.send( msg );
               } catch (IOException e) {
                   e.printStackTrace();
                   goon = false;
                   break;
               }
               logger.debug("#distributed list = "+theList.size());
               logger.debug("receive H polynomial");
               Object rh = null;
               try {
                   rh = pairChannel.receive();
               } catch (IOException e) {
                   e.printStackTrace();
                   goon = false;
                   break;
               } catch (ClassNotFoundException e) {
                   e.printStackTrace();
                   goon = false;
                   break;
               }
               logger.debug("received H polynomial");
               if ( rh == null ) {
                  if ( pair != null ) {
                     pair.setZero();
                  }
               } else if ( rh instanceof GBTransportMessPoly ) {
                  // update pair list
                  red++;
                  H = ((GBTransportMessPoly<C>)rh).pol;
                  logger.debug("H = " + H);
                  if ( H == null ) {
                     if ( pair != null ) {
                        pair.setZero();
                     }
                  } else {
                     if ( H.isZERO() ) {
                        pair.setZero();
                     } else {
                        if ( H.isONE() ) {
                           // pool.allIdle();
                           polIndex = pairlist.putOne( H );
                           theList.put( new Integer(polIndex), H );
                           goon = false;
                           break;
                        } else {
                           polIndex = pairlist.put( H );
                           // use putWait ? but still not all distributed
                           theList.put( new Integer(polIndex), H );
                        }
                     }
                  }
               }
           }
           logger.info( "terminated, done " + red + " reductions");

           /*
            * send end mark to client
            */
           logger.debug("send end" );
           try {
               pairChannel.send( new GBTransportMessEnd() );
           } catch (IOException e) {
               if ( logger.isDebugEnabled() ) {
                  e.printStackTrace();
               }
           }
           pool.beIdle();
           pairChannel.close();
      }

}


/**
 * Distributed GB transport message.
 */

class GBTransportMess implements Serializable {
    public String toString() {
        return "" + this.getClass().getName();
    }
}

/**
 * Distributed GB transport message for requests.
 */

class GBTransportMessReq extends GBTransportMess {
    public GBTransportMessReq() {
    }
}

/**
 * Distributed GB transport message for termination.
 */

class GBTransportMessEnd extends GBTransportMess {
    public GBTransportMessEnd() {
    }
}

/**
 * Distributed GB transport message for polynomial.
 */

class GBTransportMessPoly<C extends RingElem<C>> extends GBTransportMess {
    public final GenPolynomial<C> pol;
    public GBTransportMessPoly(GenPolynomial<C> p) {
        this.pol = p;
    }
    public String toString() {
        return super.toString() + "( " + pol + " )";
    }
}

/**
 * Distributed GB transport message for pairs.
 */

class GBTransportMessPair extends GBTransportMess {
    public final Pair pair;
    public GBTransportMessPair(Pair p) {
        this.pair = p;
    }
    public String toString() {
        return super.toString() + "( " + pair + " )";
    }
}

/**
 * Distributed GB transport message for index pairs.
 */

class GBTransportMessPairIndex extends GBTransportMess {
    public final Integer i;
    public final Integer j;
    public GBTransportMessPairIndex(Pair p) {
        if ( p == null ) {
            throw new NullPointerException("pair may not be null");
        }
        this.i = new Integer( p.i );
        this.j = new Integer( p.j );
    }
    public GBTransportMessPairIndex(int i, int j) {
        this.i = new Integer(i);
        this.j = new Integer(j);
    }
    public GBTransportMessPairIndex(Integer i, Integer j) {
        this.i = i;
        this.j = j;
    }
    public String toString() {
        return super.toString() + "( " + i + "," +j + " )";
    }
}


    /**
     * distributed clients reducing worker threads
     */

class ReducerClient <C extends RingElem<C>> implements Runnable {
        private SocketChannel pairChannel;
        private DistHashTable theList;
        private static Logger logger = Logger.getLogger(ReducerClient.class);

      ReducerClient(SocketChannel pc, DistHashTable dl) {
             pairChannel = pc;
             theList = dl;
      } 

      public void run() {
           logger.debug("pairChannel = "+pairChannel);
           logger.debug("reducer client running");
           Pair pair = null;
           GenPolynomial<C> pi;
           GenPolynomial<C> pj;
           GenPolynomial<C> S;
           GenPolynomial<C> H = null;
           boolean set = false;
           boolean goon = true;
           int red = 0;
           int sleeps = 0;
           Integer pix;
           Integer pjx;

           while ( goon ) {
               /* protocol:
                * request pair, process pair, send result
                */
               // pair = (Pair) pairlist.removeNext();

               Object req = new GBTransportMessReq();
               logger.debug("send request = "+req);
               try {
                    pairChannel.send( req );
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
                   if ( logger.isDebugEnabled() ) {
                      e.printStackTrace();
                   }
               } catch (ClassNotFoundException e) {
                   goon = false;
                   e.printStackTrace();
               }
               logger.debug("received pair = " + pp);
               H = null;
               if ( pp == null ) { // should not happen
                   continue;
               }
               if ( pp instanceof GBTransportMessEnd ) {
                  goon = false;
                  continue;
               }
               if ( pp instanceof GBTransportMessPair 
                  || pp instanceof GBTransportMessPairIndex ) {
                  pi = pj = null;
                  if ( pp instanceof GBTransportMessPair ) {
                     pair = ((GBTransportMessPair)pp).pair;
                     if ( pair != null ) {
                        pi = pair.pi; 
                        pj = pair.pj; 
                        //logger.debug("pair: pix = " + pair.i 
                        //               + ", pjx = " + pair.j);
                     }
                  }
                  if ( pp instanceof GBTransportMessPairIndex ) {
                      pix = ((GBTransportMessPairIndex)pp).i;
                      pjx = ((GBTransportMessPairIndex)pp).j;
                      pi = (GenPolynomial<C>)theList.getWait( pix );
                      pj = (GenPolynomial<C>)theList.getWait( pjx );
                      //logger.info("pix = " + pix + ", pjx = " +pjx);
                  }

                  if ( pi != null && pj != null ) {
                      S = Reduction.<C>SPolynomial( pi, pj );
                      //System.out.println("S   = " + S);
                      if ( S.isZERO() ) {
                         // pair.setZero(); does not work in dist
                      } else {
                         if ( logger.isDebugEnabled() ) {
                            logger.debug("ht(S) = " + S.leadingExpVector() );
                         }
                         H = Reduction.<C>normalformMod( theList, S );
                         red++;
                         if ( H.isZERO() ) {
                            // pair.setZero(); does not work in dist
                         } else {
                            H = H.monic();
                            if ( logger.isInfoEnabled() ) {
                               logger.info("ht(H) = " + H.leadingExpVector() );
                            }
                         }
                      }
                   }
               }

               // send H or must send null
               logger.debug("#distributed list = "+theList.size());
               logger.debug("send H polynomial = " + H);
               try {
                   pairChannel.send( new GBTransportMessPoly<C>( H ) );
               } catch (IOException e) {
                   goon = false;
                   e.printStackTrace();
               }
           }
           logger.info( "terminated, done " + red + " reductions");
           pairChannel.close();
      }
}


    /**
     * distributed server reducing worker threads for minimal GB
     * not jet distributed but threaded
     */

class MiReducerServer <C extends RingElem<C>> implements Runnable {
        private List G;
        private List F;
        private GenPolynomial<C> S;
        private GenPolynomial<C> H;
        private Semaphore done = new Semaphore(0);
        private static Logger logger = Logger.getLogger(MiReducerServer.class);

      MiReducerServer(List G, List F, GenPolynomial<C> p) {
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


    /**
     * distributed clients reducing worker threads for minimal GB
     * not jet used
     */

class MiReducerClient <C extends RingElem<C>> implements Runnable {
        private List G;
        private List F;
        private GenPolynomial<C> S;
        private GenPolynomial<C> H;
        private Semaphore done = new Semaphore(0);
        private static Logger logger = Logger.getLogger(MiReducerClient.class);

      MiReducerClient(List G, List F, GenPolynomial<C> p) {
            this.G = G;
            this.F = F;
            S = p;
            H = S;
      } 

      public GenPolynomial<C> getNF() {
            try { done.P();
            } catch (InterruptedException unused) { }
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

