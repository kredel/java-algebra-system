/*
 * $Id$
 */

package edu.jas.util;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import mpi.Comm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import mpi.Request;

import org.apache.log4j.Logger;

import edu.jas.kern.MPJEngine;


/**
 * Distributed version of a HashTable using MPJ. Implemented with a SortedMap /
 * TreeMap to keep the sequence order of elements. Implemented using MPJ.
 * @author Heinz Kredel
 */

public class DistHashTableMPJ<K, V> extends AbstractMap<K, V> {


    private static final Logger logger = Logger.getLogger(DistHashTableMPJ.class);


    private static boolean debug = true; //logger.isDebugEnabled();


    protected final SortedMap<K, V> theList;


    protected final Comm engine;


    protected DHTMPJListener<K, V> listener;


    /**
     * Message tag for DHT communicaton.
     */
    public static final int DHTTAG = MPJEngine.TAG + 1;


    /**
     * DistHashTableMPJ.
     */
    public DistHashTableMPJ() {
        this(MPJEngine.getCommunicator());
    }


    /**
     * DistHashTableMPJ.
     * @param args command line for MPJ runtime system.
     */
    public DistHashTableMPJ(String[] args) {
        this(MPJEngine.getCommunicator(args));
    }


    /**
     * DistHashTableMPJ.
     * @param cm MPJ communicator to use.
     */
    public DistHashTableMPJ(Comm cm) {
        engine = cm;
        //theList = new ConcurrentSkipListMap<K, V>(); // Java 1.6
        theList = new TreeMap<K, V>();
        listener = new DHTMPJListener<K, V>(engine, theList);
    }


    /**
     * Hash code.
     */
    @Override
    public int hashCode() {
        return theList.hashCode();
    }


    /**
     * Equals.
     */
    @Override
    public boolean equals(Object o) {
        return theList.equals(o);
    }


    /**
     * Contains key.
     */
    @Override
    public boolean containsKey(Object o) {
        return theList.containsKey(o);
    }


    /**
     * Contains value.
     */
    @Override
    public boolean containsValue(Object o) {
        return theList.containsValue(o);
    }


    /**
     * Get the values as Collection.
     */
    @Override
    public Collection<V> values() {
        synchronized (theList) {
            return new ArrayList<V>(theList.values());
            //return theList.values();
        }
    }


    /**
     * Get the keys as set.
     */
    @Override
    public Set<K> keySet() {
        synchronized (theList) {
            return theList.keySet();
        }
    }


    /**
     * Get the entries as Set.
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        synchronized (theList) {
            return theList.entrySet();
        }
    }


    /**
     * Get the internal list, convert from Collection.
     */
    public List<V> getValueList() {
        synchronized (theList) {
            return new ArrayList<V>(theList.values());
        }
    }


    /**
     * Get the internal sorted map. For synchronization purpose in normalform.
     */
    public SortedMap<K, V> getList() {
        return theList;
    }


    /**
     * Size of the (local) list.
     */
    @Override
    public int size() {
        synchronized (theList) {
            return theList.size();
        }
    }


    /**
     * Is the List empty?
     */
    @Override
    public boolean isEmpty() {
        synchronized (theList) {
            return theList.isEmpty();
        }
    }


    /**
     * List key iterator.
     */
    public Iterator<K> iterator() {
        synchronized (theList) {
            return theList.keySet().iterator();
        }
    }


    /**
     * List value iterator.
     */
    public Iterator<V> valueIterator() {
        synchronized (theList) {
            return theList.values().iterator();
        }
    }


    /**
     * Put object to the distributed hash table. Blocks until the key value pair
     * is send and received from the server.
     * @param key
     * @param value
     */
    public void putWait(K key, V value) {
        put(key, value); // = send
        // assume key does not change multiple times before test:
        V val = null;
        do {
            val = getWait(key);
            //System.out.print("#");
        } while (!value.equals(val));
    }


    /**
     * Put object to the distributed hash table. Returns immediately after
     * sending does not block.
     * @param key
     * @param value
     */
    @Override
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("null keys or values not allowed");
        }
        try {
            DHTTransport<K, V> tc = DHTTransport.<K, V> create(key, value);
            DHTTransport[] tcl = new DHTTransport[1];
            tcl[0] = tc;
            int size = engine.Size();
            for (int i = 0; i < size; i++) { // send also to self.listener
                synchronized (MPJEngine.getSendLock(DHTTAG)) {
                   engine.Send(tcl, 0, tcl.length, MPI.OBJECT, i, DHTTAG);
                }
            }
            //System.out.println("send: "+tc);
        } catch (MPIException e) {
            logger.info("sending(key=" + key + ")");
            logger.info("send " + e);
            e.printStackTrace();
        } catch (Exception e) {
            logger.info("sending(key=" + key + ")");
            logger.info("send " + e);
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Get value under key from DHT. Blocks until the object is send and
     * received from the server (actually it blocks until some value under key
     * is received).
     * @param key
     * @return the value stored under the key.
     */
    public V getWait(K key) {
        V value = null;
        try {
            synchronized (theList) {
                value = theList.get(key);
                //value = get(key);
                while (value == null) {
                    //System.out.print("-");
                    theList.wait(100);
                    value = theList.get(key);
                    //value = get(key);
                }
            }
        } catch (InterruptedException e) {
            //Thread.currentThread().interrupt();
            e.printStackTrace();
            return value;
        }
        return value;
    }


    /**
     * Get value under key from DHT. If no value is jet available null is
     * returned.
     * @param key
     * @return the value stored under the key.
     */
    @Override
    public V get(Object key) {
        synchronized (theList) {
            return theList.get(key);
        }
    }


    /**
     * Clear the List. Caveat: must be called on all clients.
     */
    @Override
    public void clear() {
        // send clear message to others
        synchronized (theList) {
            theList.clear();
        }
    }


    /**
     * Initialize and start the list thread.
     */
    public void init() {
        logger.debug("init " + listener + ", theList = " + theList);
        if (listener == null) {
            return;
        }
        if (listener.isDone()) {
            return;
        }
        if (debug) {
            logger.debug("initialize " + listener);
        }
        synchronized (theList) {
            listener.start();
        }
    }


    /**
     * Terminate the list thread.
     */
    public void terminate() {
        if (listener == null) {
            return;
        }
        if (debug) {
            Runtime rt = Runtime.getRuntime();
            logger.debug("terminate " + listener + ", runtime = " + rt.hashCode());
        }
        try {
            DHTTransport<K, V> tc = new DHTTransportTerminate<K, V>();
            DHTTransport[] tcl = new DHTTransport[1];
            tcl[0] = tc;
            // send only to self.listener
            synchronized (MPJEngine.getSendLock(DHTTAG)) {
               engine.Send(tcl, 0, tcl.length, MPI.OBJECT, engine.Rank(), DHTTAG);
            }
            logger.debug("send terminate to " + engine.Rank());
        } catch (MPIException e) {
            logger.info("sending(terminate)");
            logger.info("send " + e);
            e.printStackTrace();
        } catch (Exception e) {
            logger.info("sending(terminate)");
            logger.info("send " + e);
            e.printStackTrace();
        }
        listener.setDone();
        try {
            while (listener.isAlive()) {
                System.out.print("+++++");
                listener.join(999);
                //listener.interrupt();
            }
        } catch (InterruptedException e) {
            //Thread.currentThread().interrupt();
        }
        listener = null;
    }

}


/**
 * Thread to comunicate with the other DHT lists.
 */

class DHTMPJListener<K, V> extends Thread {


    private static final Logger logger = Logger.getLogger(DHTMPJListener.class);


    private static boolean debug = true; //logger.isDebugEnabled();


    private final Comm engine;


    private final SortedMap<K, V> theList;


    private boolean goon;


    /**
     * Constructor.
     */
    DHTMPJListener(Comm cm, SortedMap<K, V> list) {
        engine = cm;
        theList = list;
        goon = true;
    }


    /**
     * Test if done.
     */
    boolean isDone() {
        return !goon;
    }


    /**
     * Set to done status.
     */
    void setDone() {
        goon = false;
    }


    /**
     * run.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        logger.debug("listener run() " + this);
        DHTTransport<K, V> tc;
        //goon = true;
        while (goon) {
            tc = null;
            try {
                DHTTransport[] tcl = new DHTTransport[1];
                //System.out.println("engine.Recv");
                Status stat = null;
                synchronized (MPJEngine.getRecvLock(DistHashTableMPJ.DHTTAG)) {
                    //stat = engine.Recv(tcl, 0, tcl.length, MPI.OBJECT, MPI.ANY_SOURCE,
                    //                  DistHashTableMPJ.DHTTAG);
                    Request req = engine.Irecv(tcl, 0, tcl.length, MPI.OBJECT, MPI.ANY_SOURCE, 
                                         DistHashTableMPJ.DHTTAG);
                    stat = MPJEngine.waitRequest(req); //req.Wait();
                }
                int cnt = stat.Get_count(MPI.OBJECT);
                //System.out.println("engine.Recv, cnt = " + cnt);
                if (cnt == 0) {
                    goon = false;
                    break;
                }
                tc = (DHTTransport<K, V>) tcl[0];
                if (debug) {
                    logger.debug("receive(" + tc + ")");
                }
                if (tc instanceof DHTTransportTerminate) {
                    goon = false;
                    break;
                }
                if (this.isInterrupted()) {
                    goon = false;
                    break;
                }
                K key = tc.key();
                if (key != null) {
                    logger.info("receive(" + engine.Rank() + "," + stat.source + "), key=" + key);
                    V val = tc.value();
                    synchronized (theList) {
                        theList.put(key, val);
                        theList.notifyAll();
                    }
                }
            } catch (MPIException e) {
                goon = false;
                logger.info("receive(MPI) " + e);
                //e.printStackTrace();
            } catch (ClassNotFoundException e) {
                goon = false;
                logger.info("receive(Class) " + e);
                e.printStackTrace();
            } catch (Exception e) {
                goon = false;
                logger.info("receive " + e);
                e.printStackTrace();
            }
        }
        logger.info("terminated at " + engine.Rank());
    }

}
