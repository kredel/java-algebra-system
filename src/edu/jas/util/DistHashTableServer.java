/*
 * $Id$
 */

package edu.jas.util;

import java.io.IOException;
import java.io.Serializable;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

//import edu.unima.ky.parallel.ChannelFactory;
//import edu.unima.ky.parallel.SocketChannel;


/**
 * Server for the distributed version of a list.
 * @author Heinz Kredel
 * @todo redistribute list for late comming clients, removal of elements.
 */

public class DistHashTableServer<K> extends Thread {

    private static final Logger logger = Logger.getLogger(DistHashTableServer.class);

    public final static int DEFAULT_PORT = 9009; //ChannelFactory.DEFAULT_PORT + 99;
    protected final ChannelFactory cf;

    protected List<DHTBroadcaster<K>> servers;

    private boolean goon = true;
    private Thread mythread = null;

    //private DHTCounter listElem = null;
    protected final SortedMap<K,DHTTransport> theList;


    /**
     * Constructs a new DistHashTableServer.
     */ 
    public DistHashTableServer() {
        this(DEFAULT_PORT);
    }


    /**
     * DistHashTableServer.
     * @param port to run server on.
     */
    public DistHashTableServer(int port) {
        this( new ChannelFactory(port) );
    }


/**
 * DistHashTableServer.
 * @param cf ChannelFactory to use.
 */
    public DistHashTableServer(ChannelFactory cf) {
        //listElem = new DHTCounter(0);
        this.cf = cf;
        servers = new ArrayList<DHTBroadcaster<K>>();
        theList = new TreeMap<K,DHTTransport>();
    }


/**
 * main.
 * Usage: DistHashTableServer &lt;port&gt;
 */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if ( args.length < 1 ) {
            System.out.println("Usage: DistHashTableServer <port>");
        } else {
           try {
                port = Integer.parseInt( args[0] );
            } catch (NumberFormatException e) {
            }
        }
        (new DistHashTableServer/*raw: <K>*/(port)).run();
        // until CRTL-C
    }


/**
 * thread initialization and start.
 */ 
    public void init() {
        this.start();
    }


/**
 * main server method.
 */ 
    @Override
     public void run() {
       SocketChannel channel = null;
       DHTBroadcaster<K> s = null;
       mythread = Thread.currentThread();
       Entry<K,DHTTransport> e;
       K n;
       DHTTransport tc;
       while (goon) {
          //logger.debug("list server " + this + " go on");
          try {
               channel = cf.getChannel();
               if ( logger.isDebugEnabled() ) {
                  logger.debug("dls channel = "+channel);
               }
               if ( mythread.isInterrupted() ) {
                  goon = false;
                  //logger.info("list server " + this + " interrupted");
               } else {
		  s = new DHTBroadcaster<K>(channel,servers,/*listElem,*/theList);
                  int ls = 0;
                  synchronized (servers) {
                     if ( goon ) {
                        servers.add( s );
                        ls = theList.size();
                        s.start();
                     }
                  }
                  if ( logger.isInfoEnabled() ) {
                     logger.info("server " + s + " started " + s.isAlive());
                  }
                  if ( ls > 0 ) {
                     //logger.debug("sending " + ls + " list elements");
                     synchronized (theList) {
                         Iterator<Entry<K,DHTTransport>> it = theList.entrySet().iterator();
                         for ( int i = 0; i < ls; i++ ) {
                             e = it.next();
                             n = e.getKey();
                             tc = e.getValue();
			     //DHTTransport tc = (DHTTransport) o;                             
                             try {
                                 s.sendChannel( tc );
                             } catch (IOException ioe) {
                                 // stop s
                             }
                         }
                     } 
                  }
               }
          } catch (InterruptedException end) {
               goon = false;
               Thread.currentThread().interrupt();
          }
       }
       if ( logger.isDebugEnabled() ) {
          logger.debug("listserver " + this + " terminated");
       }
    }


/**
 * terminate all servers.
 */ 
    public void terminate() {
        goon = false;
        logger.debug("terminating ListServer");
        if ( cf != null ) {
           cf.terminate();
        }
        if ( servers != null ) {
           synchronized (servers) {
              Iterator<DHTBroadcaster<K>> it = servers.iterator();
              while ( it.hasNext() ) {
                 DHTBroadcaster<K> br = it.next();
                 br.closeChannel();
                 try { 
                     int c = 0; 
                     while ( br.isAlive() ) {
                           c++;
                           if ( c > 10 ) {
                              logger.warn("giving up on " + br);
                              break;
                           }
                           //System.out.print(".");
                           br.interrupt(); 
                           br.join(100);
                     }
                     if ( logger.isDebugEnabled() ) {
                        logger.debug("server " + br + " terminated");
                     }
                 } catch (InterruptedException e) { 
                     Thread.currentThread().interrupt();
                 }
              }
              servers.clear();
           }
           //? servers = null;
        }
        logger.debug("DHTBroadcasters terminated");
        if ( mythread == null ) {
           return;
        }
        try { 
            while ( mythread.isAlive() ) {
                  //System.out.print("-");
                  mythread.interrupt(); 
                  mythread.join(100);
            }
            if ( logger.isDebugEnabled() ) {
               logger.debug("server terminated " + mythread);
            }
        } catch (InterruptedException e) { 
            Thread.currentThread().interrupt();
        }
        mythread = null;
        logger.debug("ListServer terminated");
    }


/**
 * number of servers.
 */ 
    public int size() {
        synchronized (servers) {
           return servers.size();
        }
    }

}


/**
 * Thread for broadcasting all incoming objects to the list clients.
 */ 

class DHTBroadcaster<K> extends Thread /*implements Runnable*/ {

    private static final Logger logger = Logger.getLogger(DHTBroadcaster.class);
    private final SocketChannel channel;
    private final List<DHTBroadcaster<K>> bcaster;
    //private DHTCounter listElem;
    private final SortedMap<K,DHTTransport> theList;


/**
 * DHTBroadcaster.
 * @param s SocketChannel to use.
 * @param bc list of broadcasters.
 * @param le DHTCounter.
 * @param sm SortedMap with key value pairs.
 */
    public DHTBroadcaster(SocketChannel s, 
                          List<DHTBroadcaster<K>> bc, 
                          //DHTCounter le, 
                          SortedMap<K,DHTTransport> sm) {
        channel = s;
        bcaster = bc;
        //listElem = le;
        theList = sm;
    } 


/**
 * closeChannel.
 */
    public void closeChannel() {
        channel.close();
    }


/**
 * sendChannel.
 * @param tc DHTTransport.
 * @throws IOException
 */
    public void sendChannel(DHTTransport tc) throws IOException {
        channel.send(tc);
    }


/** 
 * broadcast.
 * @param o DHTTransport element to broadcast.
 */
    public void broadcast(DHTTransport o) {
        if ( logger.isDebugEnabled() ) {
           logger.debug("broadcast = "+o);
        }
        DHTTransport<K,Object> tc = null;
        if ( o == null ) {
            return;
        }
	//if ( ! (o instanceof DHTTransport) ) {
        //   return;
        //}
        tc = (DHTTransport<K,Object>)o;
        synchronized (theList) {
            //test
            //Object x = theList.get( tc.key );
            //if ( x != null ) {
            //   logger.info("theList duplicate key " + tc.key );
            //}
           try {
               theList.put( tc.key(), tc );
           } catch (IOException e) {
	       e.printStackTrace();
               logger.warn("tc.key() not ok " + tc );
           } catch (ClassNotFoundException e) {
	       e.printStackTrace();
               logger.warn("tc.key() not ok " + tc );
	   }
        }
        synchronized (bcaster) {
            Iterator<DHTBroadcaster<K>> it = bcaster.iterator();
            while ( it.hasNext() ) {
                DHTBroadcaster<K> br = it.next();
                try {
                    if ( logger.isDebugEnabled() ) {
                       logger.debug("bcasting to " + br);
                    }
                    br.sendChannel( tc );
                } catch (IOException e) {
                    try { 
                        br.closeChannel();
                        while ( br.isAlive() ) {
                            br.interrupt(); 
                            br.join(100);
                        }
                    } catch (InterruptedException w) { 
                        Thread.currentThread().interrupt();
                    }
                    it.remove( /*br*/ ); //ConcurrentModificationException
                    logger.debug("bcaster.remove() " + br);
                }
            }
        }
    }


/**
 * run.
 */
    @Override
    public void run() {
        boolean goon = true;
        while (goon) {
              try {
                  logger.debug("trying to receive");
                  Object o = channel.receive();
                  if ( this.isInterrupted() ) {
                      break;
                  }
                  if ( logger.isDebugEnabled() ) {
                     logger.debug("received = "+o);
                  }
	          if ( ! (o instanceof DHTTransport) ) {
                      logger.warn("swallowed: "+o);
                      continue;
                  }
		  DHTTransport tc = (DHTTransport) o;
                  broadcast(tc);
                  if ( this.isInterrupted() ) {
                      goon = false;
                  }
              } catch (IOException e) {
                  goon = false;
                  //e.printStackTrace();
              } catch (ClassNotFoundException e) {
                  goon = false;
                  e.printStackTrace();
              }
        }
        if ( logger.isDebugEnabled() ) {
           logger.debug("DHTBroadcaster terminated "+this);
        }
        channel.close();
    }


/**
 * toString.
 * @return a string representation of this.
 */
    @Override
     public String toString() {
        return "DHTBroadcaster("+channel+","+bcaster.size()+ /*","+listElem+*/ ")";
    }

}
