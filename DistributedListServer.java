/*
 * $Id$
 */

package edu.jas;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import edu.unima.ky.parallel.ChannelFactory;
import edu.unima.ky.parallel.SocketChannel;


import org.apache.log4j.Logger;

/**
 * Server for the distributed version of a list.
 * @author Heinz Kredel.
 */

public class DistributedListServer extends Thread {

    private static Logger logger = Logger.getLogger(DistributedListServer.class);

    // protected /*final*/ List theList;
    protected final ChannelFactory cf;
    protected ArrayList servers = null;

    public final static int DEFAULT_PORT = 4711;
    private boolean goon;
    private Thread mythread;

/**
 * Constructs a new DistributedListServer
 */ 

    public DistributedListServer() {
	this(DEFAULT_PORT);
    }

    public DistributedListServer(int port) {
	this( new ChannelFactory(port) );
    }

    public DistributedListServer(ChannelFactory cf) {
	//theList = new ArrayList();
	this.cf = cf;
	servers = new ArrayList();
    }

    public static void main(String[] args) {
	int port = DEFAULT_PORT;
	if ( args.length < 1 ) {
	    System.out.println("Usage: DistributedListServer <port>");
	} else {
	   try {
	        port = Integer.parseInt( args[0] );
	    } catch (NumberFormatException e) {
	    }
	}
	(new DistributedListServer(port)).run();
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
       Broadcaster s = null;
       mythread = Thread.currentThread();
       goon = true;
       while (goon) {
          logger.debug("list server " + this + " go on");
          try {
               channel = cf.getChannel();
	       logger.debug("dls channel = "+channel);
	       if ( mythread.isInterrupted() ) {
		   goon = false;
	           //logger.info("list server " + this + " interrupted");
	       } else {
	          s = new Broadcaster(channel,servers);
	          servers.add( s );
	          s.start();
	          logger.debug("server " + s + " started");
	       }
          } catch (InterruptedException e) {
               goon = false;
	       // e.printStackTrace();
	  }
       }
       //logger.debug("listserver " + this + " terminated");
    }

/**
 * terminate all servers
 */ 

    public void terminate() {
	goon = false;
        logger.debug("terminating ListServer");
        if ( cf != null ) cf.terminate();
	if ( servers != null ) {
	   Iterator it = servers.iterator();
	   while ( it.hasNext() ) {
	      Broadcaster x = (Broadcaster) it.next();
              x.channel.close();
              try { 
                  while ( x.isAlive() ) {
                          System.out.print(".");
                          x.interrupt(); 
                          x.join(100);
                  }
	          logger.debug("server " + x + " terminated");
              } catch (InterruptedException e) { 
              }
	   }
	   servers = null;
	}
        logger.debug("Broadcasters terminated");
	if ( mythread == null ) return;
        try { 
            while ( mythread.isAlive() ) {
                    System.out.print("-");
                    mythread.interrupt(); 
                    mythread.join(100);
            }
            //logger.debug("server " + mythread + " terminated");
        } catch (InterruptedException e) { 
        }
        logger.debug("ListServer terminated");
    }


/**
 * number of servers
 */ 
    public int size() {
	return servers.size();
    }

}

/**
 * class for broadcasting all incoming objects
 */ 

class Broadcaster extends Thread /*implements Runnable*/ {

    private static Logger logger = Logger.getLogger(Broadcaster.class);
    protected final SocketChannel channel;
    private List list;

    Broadcaster(SocketChannel s, List p) {
	channel = s;
	list = p;
    } 

    void broadcast(Object o) {
	synchronized (list) {
	    Iterator it = list.iterator();
	    while ( it.hasNext() ) {
		Broadcaster x = (Broadcaster) it.next();
		try {
		    if ( x.channel != channel ) {
			x.channel.send(o);
			//System.out.println("bcast: "+o+" to "+x.channel);
		    } 
		} catch (IOException e) {
		    try { 
			x.channel.close();
			while ( x.isAlive() ) {
			    x.interrupt(); 
			    x.join(100);
			}
		    } catch (InterruptedException unused) { 
		    }
		    list.remove( x );
		}
	    }
	}
    }

    public void run() {
	Object o;
	boolean goon = true;
	while (goon) {
              try {
                   o = channel.receive();
                   //System.out.println("receive: "+o+" from "+channel);
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
	logger.debug("broadcaster terminated "+this);
	channel.close();
    }

}
