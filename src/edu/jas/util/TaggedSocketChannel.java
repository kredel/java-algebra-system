/*
 * $Id$
 */

package edu.jas.util;


import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;


/**
 * TaggedSocketChannel provides a communication channel with message tags for
 * Java objects using TCP/IP sockets.
 * @author Heinz Kredel.
 */
public class TaggedSocketChannel extends Thread {


    private static final Logger logger = Logger.getLogger(TaggedSocketChannel.class);


    private static final boolean debug = true || logger.isDebugEnabled();


    /**
     * Message tag for errors.
     */
    public static final Integer errorTag = new Integer(-1);


    /**
     * Underlying socket channel.
     */
    protected final SocketChannel sc;


    /**
     * Queues for each message tag.
     */
    protected final Map<Integer, BlockingQueue> queues;


    /**
     * Constructs a tagged socket channel on the given socket channel s.
     * @param s A socket channel object.
     */
    public TaggedSocketChannel(SocketChannel s) {
        sc = s;
        queues = new HashMap<Integer, BlockingQueue>();
        BlockingQueue tq = new LinkedBlockingQueue();
        synchronized (queues) {
            queues.put(errorTag, tq);
            this.start();
        }
    }


    /**
     * Get the SocketChannel
     */
    public SocketChannel getSocket() {
        return sc;
    }


    /**
     * Sends an object.
     * @param tag message tag
     * @param v object to send
     * @throws IOException
     */
    public void send(Integer tag, Object v) throws IOException {
        if (tag == null || errorTag.equals(tag)) {
            throw new IllegalArgumentException("tag " + tag + " not allowed");
        }
        TaggedMessage tm = new TaggedMessage(tag, v);
        sc.send(tm);
    }


    /**
     * Receive an object.
     * @param tag message tag
     * @return object received
     * @throws InterruptedException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object receive(Integer tag) throws InterruptedException, IOException, ClassNotFoundException {
        synchronized (queues) { // not really required
            BlockingQueue eq = queues.get(errorTag);
            if (eq != null && eq.size() > 0) {
                Object e = eq.take();
                if (e instanceof InterruptedException) {
                    throw (InterruptedException) e;
                }
                if (e instanceof IOException) {
                    throw (IOException) e;
                }
                if (e instanceof ClassNotFoundException) {
                    throw (ClassNotFoundException) e;
                }
                throw new RuntimeException(e.toString());
            }
        }
        BlockingQueue tq = null;
        synchronized (queues) {
            tq = queues.get(tag);
            if (tq == null) {
                tq = new LinkedBlockingQueue();
                queues.put(tag, tq);
            }
        }
        return tq.take();
    }


    /**
     * Closes the channel.
     */
    public void close() {
        terminate();
    }


    /**
     * To string.
     * @see java.lang.Thread#toString()
     */
    @Override
    public String toString() {
        return "socketChannel(" + sc + ", tags = " + queues.keySet() + ")";
        //return "socketChannel(" + sc + ", tags = " + queues.keySet() + ", values = " + queues.values() + ")";
    }


    /**
     * Number of tags.
     * @return size of key set.
     */
    public int tagSize() {
        return queues.keySet().size();
    }


    /**
     * Number of messages.
     * @return sum of all messages in queues.
     */
    public int messages() {
        int m = 0;
        for ( BlockingQueue tq : queues.values() ) {
            m += tq.size();
        }
        return m;
    }


    /**
     * Run receive() in an infinite loop.
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        if (sc == null) {
            return; // nothing to do
        }
        while (true) {
            try {
                Object r = null;
                try {
                    logger.debug("waiting for tagged object");
                    r = sc.receive();
                    if (this.isInterrupted()) {
                        // r = new InterruptedException();
                    }
                } catch (IOException e) {
                    r = e;
                } catch (ClassNotFoundException e) {
                    r = e;
                }
                //logger.debug("Socket = " +s);
                logger.debug("object recieved");
                if (r instanceof TaggedMessage) {
                    TaggedMessage tm = (TaggedMessage) r;
                    BlockingQueue tq = null;
                    synchronized (queues) {
                        tq = queues.get(tm.tag);
                        if (tq == null) {
                            tq = new LinkedBlockingQueue();
                            queues.put(tm.tag, tq);
                        }
                    }
                    tq.put(tm.msg);
                } else {
                    if (debug) {
                        logger.info("no tagged message " + r);
                    }
                    synchronized (queues) {
                        BlockingQueue eq = queues.get(errorTag);
                        eq.put(r);
                        if (r instanceof Exception) {
                            return;
                        }
                    }
                }
            } catch (InterruptedException e) {
                // unfug Thread.currentThread().interrupt();
                //logger.debug("ChannelFactory IE terminating");
                return;
            }
        }
    }


    /**
     * Terminate the TaggedSocketChannel.
     */
    public void terminate() {
        this.interrupt();
        if (sc != null) {
            sc.close();
        }
        this.interrupt();
        for (Entry<Integer, BlockingQueue> tq : queues.entrySet()) {
            if (tq.getValue().size() != 0) {
                logger.warn("queue for tag " + tq.getKey() + " not empty " + tq.getValue());
            }
        }
        try {
            this.join();
        } catch (InterruptedException e) {
            // unfug Thread.currentThread().interrupt();
        }
        logger.debug("TaggedSocketChannel terminated");
    }

}


/**
 * TaggedMessage container.
 * @author kredel
 * 
 */
class TaggedMessage implements Serializable {


    public final Integer tag;


    public final Object msg;


    /**
     * Constructor.
     * @param tag message tag
     * @param msg message object
     */
    public TaggedMessage(Integer tag, Object msg) {
        this.tag = tag;
        this.msg = msg;
    }

}
