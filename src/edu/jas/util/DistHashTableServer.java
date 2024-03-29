/*
 * $Id$
 */

package edu.jas.util;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Server for the distributed version of a list. TODO: redistribute list for
 * late coming clients, removal of elements.
 * @author Heinz Kredel
 */

public class DistHashTableServer<K> extends Thread {


    private static final Logger logger = LogManager.getLogger(DistHashTableServer.class);


    private static final boolean debug = logger.isDebugEnabled();


    public final static int DEFAULT_PORT = 9009; //ChannelFactory.DEFAULT_PORT + 99;


    protected final ChannelFactory cf;


    protected List<DHTBroadcaster<K>> servers;


    private volatile boolean goon = true;


    private volatile Thread mythread = null;


    protected final SortedMap<K, DHTTransport> theList;


    private long etime;


    private long dtime;


    private long ertime;


    private long drtime;


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
        this(new ChannelFactory(port));
    }


    /**
     * DistHashTableServer.
     * @param cf ChannelFactory to use.
     */
    public DistHashTableServer(ChannelFactory cf) {
        this.cf = cf;
        cf.init();
        servers = new ArrayList<DHTBroadcaster<K>>();
        theList = new TreeMap<K, DHTTransport>();
        etime = DHTTransport.etime;
        dtime = DHTTransport.dtime;
        ertime = DHTTransport.ertime;
        drtime = DHTTransport.drtime;
    }


    /**
     * main. Usage: DistHashTableServer &lt;port&gt;
     */
    public static void main(String[] args) throws InterruptedException {
        int port = DEFAULT_PORT;
        if (args.length < 1) {
            System.out.println("Usage: DistHashTableServer <port>");
        } else {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
            }
        }
        DistHashTableServer dhts = new DistHashTableServer/*raw: <K>*/(port);
        dhts.init();
        dhts.join();
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
        Entry<K, DHTTransport> e;
        DHTTransport tc;
        while (goon) {
            //logger.debug("list server {} go on", this);
            try {
                channel = cf.getChannel();
                if (debug) {
                    logger.debug("dls channel = {}", channel);
                }
                if (mythread.isInterrupted()) {
                    goon = false;
                    //logger.info("list server {} interrupted", this);
                } else {
                    s = new DHTBroadcaster<K>(channel, servers, /*listElem,*/theList);
                    int ls = 0;
                    synchronized (servers) {
                        if (goon) {
                            servers.add(s);
                            ls = theList.size();
                            s.start();
                        }
                    }
                    if (debug) {
                        logger.info("server {} started {}", s, s.isAlive());
                    }
                    if (ls > 0) {
                        //logger.debug("sending {} list elements", ls);
                        synchronized (theList) {
                            Iterator<Entry<K, DHTTransport>> it = theList.entrySet().iterator();
                            for (int i = 0; i < ls; i++) {
                                e = it.next();
                                // n = e.getKey(); // findbugs, already in tc
                                tc = e.getValue();
                                //DHTTransport tc = (DHTTransport) o;                             
                                try {
                                    s.sendChannel(tc);
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
        if (debug) {
            logger.info("DHTserver {} terminated", this);
        }
    }


    /**
     * terminate all servers.
     */
    public void terminate() {
        goon = false;
        logger.info("terminating");
        if (cf != null) {
            cf.terminate();
        }
        int svs = 0;
        List<DHTBroadcaster<K>> scopy = null;
        if (servers != null) {
            synchronized (servers) {
                svs = servers.size();
                scopy = new ArrayList<DHTBroadcaster<K>>(servers);
                Iterator<DHTBroadcaster<K>> it = scopy.iterator();
                while (it.hasNext()) {
                    DHTBroadcaster<K> br = it.next();
                    br.goon = false;
                    br.closeChannel();
                    try {
                        int c = 0;
                        while (br.isAlive()) {
                            c++;
                            if (c > 10) {
                                logger.warn("giving up on {}", br);
                                break;
                            }
                            //System.out.print(".");
                            br.interrupt();
                            br.join(50);
                        }
                        if (debug) {
                            logger.info("server {} terminated", br);
                        }
                        // now possible: 
                        servers.remove(br);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                servers.clear();
            }
            logger.info("{} broadcasters terminated {}", svs, scopy);
            //? servers = null;
        }
        logger.debug("DHTBroadcasters terminated");
        long enc = DHTTransport.etime - etime;
        long dec = DHTTransport.dtime - dtime;
        long encr = DHTTransport.ertime - ertime;
        long decr = DHTTransport.drtime - drtime;
        long drest = (encr * dec) / (enc + 1);
        long sumest = enc + dec + encr + drest; // +decr not meaningful
        logger.info("DHT time: encode = {}, decode = {}, enc raw = {}, dec raw wait = {}, dec raw est = {}, sum est = {}", enc, dec, encr, decr, drest, sumest);
        if (mythread == null) {
            return;
        }
        try {
            while (mythread.isAlive()) {
                //System.out.print("-");
                mythread.interrupt();
                mythread.join(100);
            }
            logger.warn("server terminated {}", mythread);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        mythread = null;
        logger.info("terminated");
    }


    /**
     * number of servers.
     */
    public int size() {
        if (servers == null) {
            return -1;
        }
        //synchronized (servers) removed
        return servers.size();
    }


    /**
     * toString.
     * @return a string representation of this.
     */
    @Override
    public String toString() {
        return "DHTServer(" + servers.size() + ", " + cf + ", " + super.toString() + ")";
    }

}


/**
 * Thread for broadcasting all incoming objects to the list clients.
 */
class DHTBroadcaster<K> extends Thread /*implements Runnable*/ {


    private static final Logger logger = LogManager.getLogger(DHTBroadcaster.class);


    private static final boolean debug = logger.isDebugEnabled();


    private final SocketChannel channel;


    private final List<DHTBroadcaster<K>> bcaster;


    private final SortedMap<K, DHTTransport> theList;


    volatile boolean goon = true;


    /**
     * DHTBroadcaster.
     * @param s SocketChannel to use.
     * @param bc list of broadcasters.
     * @param sm SortedMap with key value pairs.
     */
    public DHTBroadcaster(SocketChannel s, List<DHTBroadcaster<K>> bc, SortedMap<K, DHTTransport> sm) {
        channel = s;
        bcaster = bc;
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
        if (goon) {
            channel.send(tc);
        }
    }


    /**
     * broadcast.
     * @param o DHTTransport element to broadcast.
     */
    @SuppressWarnings({ "unchecked", "cast" })
    public void broadcast(DHTTransport o) {
        if (debug) {
            logger.debug("broadcast = {}", o);
        }
        DHTTransport<K, Object> tc = null;
        if (o == null) {
            return;
        }
        //if ( ! (o instanceof DHTTransport) ) {
        //   return;
        //}
        tc = (DHTTransport<K, Object>) o;
        K key = null;
        synchronized (theList) {
            //test
            //Object x = theList.get( tc.key );
            //if ( x != null ) {
            //   logger.info("theList duplicate key {}", tc.key );
            //}
            try {
                if (!(o instanceof DHTTransportClear)) {
                    key = tc.key();
                    theList.put(key, tc);
                }
            } catch (IOException e) {
                logger.warn("IO exception: tc.key() not ok {}", tc);
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                logger.warn("CNF exception: tc.key() not ok {}", tc);
                e.printStackTrace();
            } catch (Exception e) {
                logger.warn("exception: tc.key() not ok {}", tc);
                e.printStackTrace();
            }
        }
        logger.info("sending key={} to {} nodes", key, bcaster.size());
        List<DHTBroadcaster<K>> bccopy = null;
        synchronized (bcaster) {
            bccopy = new ArrayList<DHTBroadcaster<K>>(bcaster);
        }
        Iterator<DHTBroadcaster<K>> it = bccopy.iterator();
        while (it.hasNext()) {
            DHTBroadcaster<K> br = it.next();
            try {
                if (debug) {
                    logger.debug("bcasting to {}", br);
                }
                br.sendChannel(tc);
            } catch (IOException e) {
                logger.info("bcaster, IOexception {}", e);
                synchronized (bcaster) {
                    bcaster.remove(br); //no more: ConcurrentModificationException
                }
                try {
                    br.goon = false;
                    br.closeChannel();
                    while (br.isAlive()) {
                        br.interrupt();
                        br.join(100);
                    }
                } catch (InterruptedException w) {
                    Thread.currentThread().interrupt();
                }
                //
                logger.info("bcaster.remove() {}", br);
            } catch (Exception e) {
                logger.info("bcaster, exception {}", e);
            }
        }
    }


    /**
     * run.
     */
    @Override
    public void run() {
        goon = true;
        while (goon) {
            try {
                logger.debug("trying to receive");
                Object o = channel.receive();
                if (this.isInterrupted()) {
                    goon = false;
                    break;
                }
                if (debug) {
                    logger.debug("received = {}", o);
                }
                if (!(o instanceof DHTTransport)) {
                    logger.warn("wrong object type: {}", o);
                    goon = false;
                    break; //continue;
                }
                if (o instanceof DHTTransportClear) {
                    logger.info("receive, clear");
                    synchronized (theList) {
                        theList.clear();
                        theList.notifyAll();
                    }
                }
                DHTTransport tc = (DHTTransport) o;
                broadcast(tc);
                if (this.isInterrupted()) {
                    goon = false;
                }
            } catch (IOException e) {
                goon = false;
                logger.info("receive, IO exception {}", e);
                //e.printStackTrace();
            } catch (ClassNotFoundException e) {
                goon = false;
                logger.info("receive, CNF exception {}", e);
                e.printStackTrace();
            } catch (Exception e) {
                goon = false;
                logger.info("receive, exception {}", e);
                e.printStackTrace();
            }
        }
        logger.info("ending {}", this);
        synchronized (bcaster) {
            bcaster.remove(this);
        }
        channel.close();
    }


    /**
     * toString.
     * @return a string representation of this.
     */
    @Override
    public String toString() {
        return "DHTBroadcaster(" + channel + "," + bcaster.size() + ")";
    }

}
