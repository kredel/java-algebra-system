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

public class DistributedList /* implements List ?*/{

    private static Logger logger = Logger.getLogger(DistributedList.class);

    protected /*final*/ List theList;
    protected ChannelFactory cf;
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
	theList = new ArrayList();
	cf = new ChannelFactory(port+1);
	try {
            channel = cf.getChannel(host,port);
	} catch (IOException e) {
	    e.printStackTrace();
	}
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
            System.out.println("send: "+o+" @ "+listener);
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
	cf.terminate();
	//theList.clear();
        System.out.println("terminate "+listener);
        listener.setDone(); 
	channel.close();
        try { 
 	     int t = 0;
             while ( listener.isAlive() ) {
                     System.out.println("interrupt "+listener);
                     listener.interrupt(); 
		     t++;
                     listener.join(100);
             }
        } catch (InterruptedException unused) { 
        }
	listener = null;
        System.out.println("listener = null");
    }

/**
 * clear the List
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
              System.out.println("listener receive @ "+Thread.currentThread());
              try {
                   o = channel.receive();
                   System.out.println("receive: "+o+" @ "+Thread.currentThread());
		   if ( this.isInterrupted() ) {
		       goon = false;
		       return;
		   }
		   list.add(o);
	      } catch (IOException e) {
		   goon = false;
                   return;
	      } catch (ClassNotFoundException e) {
                   goon = false;
                   return;
	      }
	}
    }

}
