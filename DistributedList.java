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
 * Distributed version of a List.
 * @author Heinz Kredel.
 */

public class DistributedList /* implements List not jet */{

    private static Logger logger = Logger.getLogger(DistributedList.class);

    protected final List theList;
    protected final ChannelFactory cf;
    protected SocketChannel channel = null;
    protected Listener listener = null;

/**
 * Constructs a new DistributedList
 * @param host Name or IP of server host
 */ 

    public DistributedList(String host) {
	this(host,DistributedListServer.DEFAULT_PORT);
    }

    public DistributedList(String host,int port) {
	this(new ChannelFactory(port+1),host,port);
    }

    public DistributedList(ChannelFactory cf,String host,int port) {
	this.cf = cf;
	try {
            channel = cf.getChannel(host,port);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	logger.debug("dl channel = "+channel);
	theList = new ArrayList();
	listener = new Listener(channel,theList);
	listener.start();
    }

    public DistributedList(SocketChannel sc) {
	cf = null;
	channel = sc;
	theList = new ArrayList();
	listener = new Listener(channel,theList);
	listener.start();
    }

/**
 * get the internal List
 */ 
    public List getList() {
	return theList;
    }

/**
 * size of the List
 */ 
    public int size() {
	return theList.size();
    }

/**
 * add object to the list and distrubute
 */ 
    public synchronized boolean add(Object o) {
	try {
            channel.send(o);
            //System.out.println("send: "+o+" @ "+listener);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	Thread.currentThread().yield();
	return theList.add(o);
    }

/**
 * terminate the List thread
 */ 
    public void terminate() {
	if ( cf != null ) {
           cf.terminate();
	}
        if ( channel != null ) {
           channel.close();
	}
	//theList.clear();
	if ( listener == null ) return;
        logger.debug("terminate "+listener);
        listener.setDone(); 
        try { 
             while ( listener.isAlive() ) {
                     listener.interrupt(); 
                     listener.join(100);
             }
        } catch (InterruptedException unused) { 
        }
	listener = null;
    }

/**
 * clear the List
 * caveat: must be called on all clients
 */ 
    public synchronized void clear() {
	theList.clear();
    }

/**
 * is the List empty
 */ 
    public boolean isEmpty() {
        return theList.isEmpty();
    }

/**
 * List iterator
 */ 
    public synchronized Iterator iterator() {
        return theList.iterator();
    }

}

class Listener extends Thread /*implements Runnable*/ {

    private SocketChannel channel;
    private List list;
    private boolean goon;

    Listener(SocketChannel s, List p) {
	channel = s;
	list = p;
    } 

    void setDone() {
	goon = false;
    }

    public void run() {
	Object o;
	goon = true;
	while (goon) {
              try {
                   o = channel.receive();
                   //System.out.println("receive: "+o+" @ "+Thread.currentThread());
		   if ( this.isInterrupted() ) {
		       goon = false;
		   }
		   list.add(o);
	      } catch (IOException e) {
		   goon = false;
	      } catch (ClassNotFoundException e) {
                   goon = false;
	      }
	}
    }

}
