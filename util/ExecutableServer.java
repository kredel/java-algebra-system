/*
 * $Id$
 */

package edu.jas.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.unima.ky.parallel.ChannelFactory;
import edu.unima.ky.parallel.SocketChannel;

/**
 * Class ExecutableServer
 * used to receive and execute classes
 * @author Heinz Kredel
 */


public class ExecutableServer extends Thread {

    private static Logger logger = Logger.getLogger(ExecutableServer.class);

    protected final ChannelFactory cf;
    protected ArrayList servers = null;

    /**
     * DEFAULT_PORT to listen to
     */

    public static final int DEFAULT_PORT = 7411;
    public static final String DONE = "Done";

    private boolean goon = true;
    private Thread mythread = null;

    public ExecutableServer() {
	this(DEFAULT_PORT);
    }

    public ExecutableServer(int port) {
	this( new ChannelFactory(port) );
    }

    public ExecutableServer(ChannelFactory cf) {
	this.cf = cf;
	servers = new ArrayList();
    }


    /**
     * main method to start serving thread
     * @param args args[0] is port
     */

    public static void main(String[] args) {

        int port = DEFAULT_PORT;
	if ( args.length < 1 ) {
	    System.out.println("Usage: ExecutableServer <port>");
	} else {
            try {
                port = Integer.parseInt( args[0] );
            } catch (NumberFormatException e) {
            }
        }
        logger.info("ExecutableServer at port " + port);
	(new ExecutableServer(port)).run();
	// until CRTL-C
    }


/**
 * thread initialization and start
 */ 
    public void init() {
	this.start();
    }

/**
 * number of servers
 */ 
    public int size() {
	return servers.size();
    }

/**
 * run is main server method
 */ 

    public void run() {
       SocketChannel channel = null;
       Executor s = null;
       mythread = Thread.currentThread();
       while (goon) {
          logger.debug("execute server " + this + " go on");
          try {
               channel = cf.getChannel();
	       logger.debug("execute channel = "+channel);
	       if ( mythread.isInterrupted() ) {
		   goon = false;
	           logger.debug("execute server " + this + " interrupted");
	       } else {
	          s = new Executor(channel,servers);
	          servers.add( s );
	          s.start();
	          logger.debug("server " + s + " started");
	       }
          } catch (InterruptedException e) {
               goon = false;
               if ( logger.isDebugEnabled() ) {
	          e.printStackTrace();
               }
	  }
       }
       //logger.debug("execute server " + this + " terminated");
    }


/**
 * terminate all servers
 */ 

    public void terminate() {
	goon = false;
        logger.debug("terminating ExecutableServer");
        if ( cf != null ) cf.terminate();
	if ( servers != null ) {
	   Iterator it = servers.iterator();
	   while ( it.hasNext() ) {
	      Executor x = (Executor) it.next();
              x.channel.close();
              try { 
                  while ( x.isAlive() ) {
		          //System.out.print(".");
                          x.interrupt(); 
                          x.join(100);
                  }
	          logger.debug("server " + x + " terminated");
              } catch (InterruptedException e) { 
              }
	   }
	   servers = null;
	}
        logger.debug("Executors terminated");
	if ( mythread == null ) return;
        try { 
            while ( mythread.isAlive() ) {
	 	    //System.out.print("-");
                    mythread.interrupt(); 
                    mythread.join(100);
            }
            //logger.debug("server " + mythread + " terminated");
        } catch (InterruptedException e) { 
        }
        logger.debug("ExecuteServer terminated");
    }

}


/**
 * class for executing incoming objects
 */ 

class Executor extends Thread /*implements Runnable*/ {

    private static Logger logger = Logger.getLogger(Executor.class);
    protected final SocketChannel channel;
    private List list;

    Executor(SocketChannel s, List p) {
	channel = s;
	list = p;
    } 

    public void run() {
	Object o;
	RemoteExecutable re = null;
	boolean goon = true;
	logger.debug("executor started "+this);
	while (goon) {
              try {
                   o = channel.receive();
                   if ( this.isInterrupted() ) {
		        goon = false;
		   } else {
                     if ( logger.isDebugEnabled() ) {
                        logger.debug("receive: "+o+" from "+channel);
                     }
                     // check permission
		     if ( o instanceof RemoteExecutable ) {
		         re = (RemoteExecutable)o;
		         re.run();
		         if ( this.isInterrupted() ) {
		            goon = false;
		         } else {
                           channel.send(ExecutableServer.DONE);
			   goon = false; // stop this thread
			 }
		     }
		   }
	      } catch (IOException e) {
                   goon = false;
	      } catch (ClassNotFoundException e) {
                   goon = false;
	      }
	}
	logger.debug("executor terminated "+this);
	channel.close();
    }

}