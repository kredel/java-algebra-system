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
import java.io.IOException;
import java.util.Arrays;

import mpi.Comm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import mpi.Request;

import org.apache.log4j.Logger;

import edu.jas.kern.MPIEngine;


/**
 * Distributed version of a HashTable using MPI. Implemented with a SortedMap /
 * TreeMap to keep the sequence order of elements. Implemented using MPI.
 * @author Heinz Kredel
 */

public class DistHashTableMPI<K, V> extends AbstractMap<K, V> {


    private static final Logger logger = Logger.getLogger(DistHashTableMPI.class);


    private static boolean debug = logger.isDebugEnabled();


    /*
     * Backing data structure.
     */
    protected final SortedMap<K, V> theList;


    /*
     * Thread for receiving pairs.
     */
    protected DHTMPIListener<K, V> listener;


    /*
     * MPI communicator.
     */
    protected final Comm engine;


    /*
     * Size of Comm.
     */
    private final int size;


    /*
     * This rank.
     */
    private final int rank;


    /**
     * Message tag for DHT communicaton.
     */
    public static final int DHTTAG = MPIEngine.TAG + 1;


    /*
     * TCP/IP object channels.
     */
    private final SocketChannel[] soc;


    /*
     * Transport layer.
     * true: use TCP/IP socket layer, false: use MPJ transport layer.
     */
    static final boolean useTCP = false;


    /**
     * DistHashTableMPI.
     */
    public DistHashTableMPI() throws MPIException, IOException {
        this(MPIEngine.getCommunicator());
    }


    /**
     * DistHashTableMPI.
     * @param args command line for MPI runtime system.
     */
    public DistHashTableMPI(String[] args) throws MPIException, IOException {
        this(MPIEngine.getCommunicator(args));
    }


    /**
     * DistHashTableMPI.
     * @param cm MPI communicator to use.
     */
    public DistHashTableMPI(Comm cm) throws MPIException, IOException {
        engine = cm;
        rank = engine.Rank();
        size = engine.Size();
        if (useTCP) { // && soc == null
            int port = ChannelFactory.DEFAULT_PORT + 11;
            ChannelFactory cf = new ChannelFactory(port);
            if (rank == 0) {
                cf.init();
                soc = new SocketChannel[size];
                soc[0] = null;
                try {
                    for ( int i = 1; i < size; i++ ) {
                        SocketChannel sc = cf.getChannel(); // TODO not correct wrt rank
                        soc[i] = sc; 
                    }
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
                cf.terminate();
            } else {
                soc = new SocketChannel[1];
                SocketChannel sc = cf.getChannel(MPIEngine.hostNames.get(0),port);
                soc[0] = sc; 
            }
        } else {
            soc = null;
        }
        theList = new TreeMap<K, V>();
        //theList = new ConcurrentSkipListMap<K, V>(); // Java 1.6
        listener = new DHTMPIListener<K, V>(engine, soc, theList);
        logger.info("constructor: " + rank + "/" + size);
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
            for (int i = 1; i < size; i++) { // send not to self.listener
                if (useTCP) {
                    soc[i].send(tc);
                } else {
                    DHTTransport[] tcl = new DHTTransport[] { tc };
                    synchronized (MPIEngine.class) { // remove
                        engine.Send(tcl, 0, tcl.length, MPI.OBJECT, i, DHTTAG);
                    }
                }
            }
            synchronized (theList) { // add to self.listener
                theList.put(tc.key(), tc.value());
                theList.notifyAll();
            }
            //System.out.println("send: "+tc);
        } catch (ClassNotFoundException e) {
            logger.info("sending(key=" + key + ")");
            logger.warn("send " + e);
            e.printStackTrace();
        } catch (MPIException e) {
            logger.info("sending(key=" + key + ")");
            logger.warn("send " + e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.info("sending(key=" + key + ")");
            logger.warn("send " + e);
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
                while (value == null) {
                    //System.out.print("-");
                    theList.wait(100);
                    value = theList.get(key);
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
        logger.info("init " + listener + ", theList = " + theList);
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
        listener.setDone();
        DHTTransport<K, V> tc = new DHTTransportTerminate<K, V>();
        try {
            if ( rank == 0 ) {
                //logger.info("send(" + rank + ") terminate");
                for (int i = 1; i < size; i++) { // send not to self.listener
                    if (useTCP) {
                        soc[i].send(tc);
                    } else {
                        DHTTransport[] tcl = new DHTTransport[] { tc };
                        synchronized (MPIEngine.class) { 
                            engine.Send(tcl, 0, tcl.length, MPI.OBJECT, i, DHTTAG);
                        }
                    }
                }
            }
        } catch (MPIException e) {
            logger.info("sending(terminate)");
            logger.info("send " + e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.info("sending(terminate)");
            logger.info("send " + e);
            e.printStackTrace();
        }
        try {
            while (listener.isAlive()) {
                //System.out.print("+++++");
                listener.join(999);
                listener.interrupt();
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
class DHTMPIListener<K, V> extends Thread {


    private static final Logger logger = Logger.getLogger(DHTMPIListener.class);


    private static boolean debug = logger.isDebugEnabled();


    private final Comm engine;


    private final SortedMap<K, V> theList;


    private final SocketChannel[] soc;


    private boolean goon;


    /**
     * Constructor.
     */
    DHTMPIListener(Comm cm, SocketChannel[] s, SortedMap<K, V> list) {
        engine = cm;
        theList = list;
        goon = true;
        soc = s;
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
        logger.info("listener run() " + this);
        int rank = -1;
        DHTTransport<K, V> tc;
        //goon = true;
        while (goon) {
            tc = null;
            try {
                if ( rank < 0 ) {
                    rank = engine.Rank();
                }
                if ( rank == 0 ) {
                    logger.info("listener on rank 0 stopped");
                    goon = false;
                    continue;
                }
                Object to = null;
                if (DistHashTableMPI.useTCP) {
                    to = soc[0].receive();
                } else {
                    DHTTransport[] tcl = new DHTTransport[1];
                    Status stat = null;
                    synchronized (MPIEngine.class) { // global static lock , // only from 0:
                        stat = engine.Recv(tcl, 0, tcl.length, MPI.OBJECT, MPI.ANY_SOURCE, 
                                           DistHashTableMPI.DHTTAG);
                    }
                    //logger.info("waitRequest done: stat = " + stat);
                    if (stat == null) {
                        goon = false;
                        break;
                    }
                    int cnt = stat.Get_count(MPI.OBJECT);
                    if (cnt == 0) {
                        goon = false;
                        break;
                    }
                    to = tcl[0];
                }
                tc = (DHTTransport<K, V>) to;
                if (debug) {
                    logger.debug("receive(" + tc + ")");
                }
                if (tc instanceof DHTTransportTerminate) {
                    logger.info("receive(" + rank + ") terminate");
                    goon = false;
                    break;
                }
                if (this.isInterrupted()) {
                    goon = false;
                    break;
                }
                K key = tc.key();
                if (key != null) {
                    logger.info("receive(" + rank + "), key=" + key);
                    V val = tc.value();
                    synchronized (theList) {
                        theList.put(key, val);
                        theList.notifyAll();
                    }
                }
            } catch (MPIException e) {
                goon = false;
                logger.warn("receive(MPI) " + e);
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
        logger.info("terminated at " + rank);
    }

}
