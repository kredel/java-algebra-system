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

import edu.unima.ky.parallel.ChannelFactory;
import edu.unima.ky.parallel.SocketChannel;


/**
 * Server for the distributed version of a list.
 * @author Heinz Kredel.
 * @todo redistribute list for late comming clients, removal of elements
 */

public class DistHashTableServer extends Thread {

    private static Logger logger = Logger.getLogger(DistHashTableServer.class);

    public final static int DEFAULT_PORT = ChannelFactory.DEFAULT_PORT + 99;
    protected final ChannelFactory cf;

    protected List servers;

    private boolean goon = true;
    private Thread mythread = null;

    private DHTCounter listElem = null;
    protected final SortedMap theList;


/**
 * Constructs a new DistHashTableServer
 */ 

    public DistHashTableServer() {
	this(DEFAULT_PORT);
    }

    public DistHashTableServer(int port) {
	this( new ChannelFactory(port) );
    }

    public DistHashTableServer(ChannelFactory cf) {
	listElem = new DHTCounter(0);
	this.cf = cf;
	servers = new ArrayList();
	theList = new TreeMap();
    }

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
	(new DistHashTableServer(port)).run();
	// until CRTL-C
    }


/**
 * thread initialization and start
 */ 
    public void init() {
	this.start();
    }


/**
 * main server method
 */ 
    public void run() {
       SocketChannel channel = null;
       DHTBroadcaster s = null;
       mythread = Thread.currentThread();
       Entry e;
       Object n;
       Object o;
       while (goon) {
          //logger.debug("list server " + this + " go on");
          try {
               channel = cf.getChannel();
	       logger.debug("dls channel = "+channel);
	       if ( mythread.isInterrupted() ) {
		  goon = false;
	          //logger.info("list server " + this + " interrupted");
	       } else {
		  s = new DHTBroadcaster(channel,servers,listElem,theList);
		  int ls = 0;
		  synchronized (servers) {
	             servers.add( s );
		     ls = theList.size();
	             s.start();
		  }
	          logger.info("server " + s + " started");
		  if ( ls > 0 ) {
	             logger.debug("sending " + ls + " list elements");
		     synchronized (theList) {
			 Iterator it = theList.entrySet().iterator();
			 for ( int i = 0; i < ls; i++ ) {
			     e = (Entry)it.next();
			     n = e.getKey();
			     o = e.getValue();
			     try {
			         s.sendChannel( n, o );
		             } catch (IOException ioe) {
				 // stop s
			     }
			 }
		     } 
		  }
	       }
          } catch (InterruptedException end) {
               goon = false;
	  }
       }
       logger.debug("listserver " + this + " terminated");
    }


/**
 * terminate all servers
 */ 

    public void terminate() {
	goon = false;
        logger.debug("terminating ListServer");
        if ( cf != null ) cf.terminate();
	if ( servers != null ) {
           synchronized (servers) {
	      Iterator it = servers.iterator();
	      while ( it.hasNext() ) {
	         DHTBroadcaster br = (DHTBroadcaster) it.next();
                 br.closeChannel();
                 try { 
                     while ( br.isAlive() ) {
		           //System.out.print(".");
                           br.interrupt(); 
                           br.join(100);
                     }
	             logger.debug("server " + br + " terminated");
                 } catch (InterruptedException e) { 
                 }
	      }
           }
	   //? servers = null;
	}
        logger.debug("DHTBroadcasters terminated");
	if ( mythread == null ) {
           return;
        }
        try { 
            while ( mythread.isAlive() ) {
		    // System.out.print("-");
                    mythread.interrupt(); 
                    mythread.join(100);
            }
            logger.debug("server terminated " + mythread);
        } catch (InterruptedException e) { 
        }
        logger.debug("ListServer terminated");
    }


/**
 * number of servers
 */ 
    public int size() {
        synchronized (servers) {
	   return servers.size();
        }
    }

}


/**
 * Class for holding the list index used a key in TreeMap.
 * Implemented since Integer has no add() method.
 * Must implement Comparable so that TreeMap works with correct ordering.
 * @unused
 */ 

class DHTCounter implements Serializable, Comparable {

    private int value;

    public DHTCounter() {
	this(0);
    }

    public DHTCounter(int v) {
	value = v;
    }

    public int intValue() {
	return value;
    }

    public void add(int v) { // synchronized elsewhere
	value += v;
    }

    public int compareTo(Object o) throws ClassCastException {
	if ( ! (o instanceof DHTCounter) ) {
	    throw new ClassCastException("DHTCounter "+value+" o "+o);
	}
	int x = ((DHTCounter)o).intValue();
	if ( value > x ) { 
           return 1;
	}
	if ( value < x ) { 
           return -1;
	}
	return 0;
    }

    public String toString() {
	return "DHTCounter("+value+")";
    }

}


/**
 * Thread for broadcasting all incoming objects to the list clients
 */ 

class DHTBroadcaster extends Thread /*implements Runnable*/ {

    private static Logger logger = Logger.getLogger(Broadcaster.class);
    private final SocketChannel channel;
    private final List bcaster;
    private DHTCounter listElem;
    private final SortedMap theList;


    public DHTBroadcaster(SocketChannel s, 
                          List bc, 
                          DHTCounter le, 
                          SortedMap sm) {
	channel = s;
	bcaster = bc;
	listElem = le;
        theList = sm;
    } 


    public void closeChannel() {
	channel.close();
    }


    public void sendChannel(Object n, Object o) throws IOException {
        DHTTransport tc = new DHTTransport(n,o);
	channel.send(tc);
    }

    public void sendChannel(DHTTransport tc) throws IOException {
	channel.send(tc);
    }


    public void broadcast(Object o) {
        logger.debug("broadcast = "+o);
        DHTTransport tc = null;
        if ( o == null ) {
            return;
        }
        if ( ! (o instanceof DHTTransport) ) {
            return;
        }
        tc = (DHTTransport)o;
	synchronized (theList) {
	    theList.put( tc.key, tc.value );
	}
	synchronized (bcaster) {
	    Iterator it = bcaster.iterator();
	    while ( it.hasNext() ) {
		DHTBroadcaster br = (DHTBroadcaster) it.next();
		try {
                    if ( logger.isDebugEnabled() ) {
		       logger.debug("bcasting("+tc.key+") = "+tc.value);
                    }
		    br.sendChannel( tc );
		} catch (IOException e) {
		    try { 
			br.closeChannel();
			while ( br.isAlive() ) {
			    br.interrupt(); 
			    br.join(100);
			}
		    } catch (InterruptedException unused) { 
		    }
		    it.remove( /*br*/ ); //ConcurrentModificationException
                    logger.debug("bcaster.remove() " + br);
		}
	    }
	}
    }


    public void run() {
	Object o;
	boolean goon = true;
	while (goon) {
              try {
                  //logger.debug("trying to receive");
                  o = channel.receive();
                  if ( logger.isDebugEnabled() ) {
                     logger.debug("received = "+o);
                  }
		  broadcast(o);
		  if ( this.isInterrupted() ) {
		      goon = false;
		  }
	      } catch (IOException e) {
                  goon = false;
	      } catch (ClassNotFoundException e) {
                  goon = false;
	      }
	}
	logger.debug("DHTBroadcaster terminated "+this);
	channel.close();
    }


    public String toSting() {
	return "DHTBroadcaster("+channel+","+bcaster.size()+","+listElem+")";
    }

}
