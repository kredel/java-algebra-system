/*
 * $Id$
 */


package edu.jas.util;


import java.io.IOException;
import java.util.Arrays;

import mpi.Comm;
import mpi.MPI;
import mpi.Status;
import mpi.Request;
import mpi.MPIException;

import org.apache.log4j.Logger;

import edu.jas.kern.MPJEngine;


/**
 * MPJChannel provides a communication channel for Java objects using MPJ to a
 * given rank.
 * @author Heinz Kredel
 */
public final class MPJChannel {


    private static final Logger logger = Logger.getLogger(MPJChannel.class);


    public static final int CHANTAG = MPJEngine.TAG + 2;


    /*
     * Underlying MPJ engine.
     */
    private final Comm engine; // static !


    /*
     * TCP/IP object channels with tags.
     */
    private static TaggedSocketChannel[] soc = null;


    /*
     * Size of Comm.
     */
    private final int size;


    /*
     * Partner rank.
     */
    private final int partnerRank;


    /*
     * This rank.
     */
    private final int rank;


    /*
     * Message tag.
     */
    private final int tag;


    /**
     * Constructs a MPJ channel on the given MPJ engine.
     * @param s MPJ communicator object.
     * @param r rank of MPJ partner.
     */
    public MPJChannel(Comm s, int r) throws IOException {
        this(s, r, CHANTAG);
    }


    /**
     * Constructs a MPJ channel on the given MPJ engine.
     * @param s MPJ communicator object.
     * @param r rank of MPJ partner.
     * @param t tag for messages.
     */
    public MPJChannel(Comm s, int r, int t) throws IOException {
        engine = s;
        rank = engine.Rank();
        size = engine.Size();
        if (r < 0 || size <= r) {
            throw new IOException("r out of bounds: 0 <= r < size: " + r + ", " + size);
        }
        partnerRank = r;
        tag = t;
        synchronized (engine) {
            if ( soc == null ) {
                int port = ChannelFactory.DEFAULT_PORT;
                ChannelFactory cf = new ChannelFactory(port);
                if (rank == 0) {
                    cf.init();
                    soc = new TaggedSocketChannel[size];
                    soc[0] = null;
                    try {
                        for ( int i = 1; i < size; i++ ) {
                            SocketChannel sc = cf.getChannel(); // TODO not correct wrt rank
                            soc[i] = new TaggedSocketChannel(sc); 
                            soc[i].init();
                        }
                    } catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                    cf.terminate();
                } else {
                    soc = new TaggedSocketChannel[1];
                    SocketChannel sc = cf.getChannel(MPJEngine.hostNames.get(0),port);
                    soc[0] = new TaggedSocketChannel(sc);
                    soc[0].init();
                }
            }
        }
        logger.info("constructor: " + this.toString());
    }


    /**
     * Get the MPJ engine.
     */
    public Comm getEngine() {
        return engine;
    }


    /**
     * Sends an object.
     * @param v message object.
     */
    public void send(Object v) throws IOException {
        send(tag, v, partnerRank);
    }


    /**
     * Sends an object.
     * @param t message tag.
     * @param v message object.
     */
    public void send(int t, Object v) throws IOException {
        send(t, v, partnerRank);
    }


    /**
     * Sends an object.
     * @param t message tag.
     * @param v message object.
     * @param pr partner rank.
     */
    void send(int t, Object v, int pr) throws IOException {
        if ( soc == null ) {
            logger.warn("soc not initialized: lost " + v);
            return;
        }
        if ( soc[pr] == null ) {
            logger.warn("soc[" + pr + "] not initialized: lost " + v);
            return;
        }
        soc[pr].send(t,v);
        // Object[] va = new Object[1];
        // va[0] = v;
        // Status stat = null;
        // synchronized (MPJEngine.class) {
        //     engine.Send(va, 0, va.length, MPI.OBJECT, pr, t);
        // }
    }


    /**
     * Receives an object.
     * @return a message object.
     */
    public Object receive() throws IOException, ClassNotFoundException {
        return receive(tag);
    }


    /**
     * Receives an object.
     * @param t message tag.
     * @return a message object.
     */
    public Object receive(int t) throws IOException, ClassNotFoundException {
        if ( soc == null ) {
            logger.warn("soc not initialized");
            return null;
        }
        if ( soc[partnerRank] == null ) {
            logger.warn("soc[" + partnerRank + "] not initialized");
            return null;
        }
        try {
             return soc[partnerRank].receive(t);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
        // Object[] va = new Object[1];
        // Status stat = null;
        // synchronized (MPJEngine.getRecvLock(t)) {
        //     stat = engine.Recv(va, 0, va.length, MPI.OBJECT, partnerRank, t);
        // }
        // int cnt = stat.Get_count(MPI.OBJECT);
        // if (cnt == 0) {
        //     throw new IOException("no object received");
        // }
        // //int pr = stat.source;
        // //if (pr != partnerRank) {
        // //    logger.warn("received out of order message from " + pr);
        // //}
        // Object v = va[0];
        // return v;
    }


    /**
     * Closes the channel.
     */
    public void close() {
        if ( soc == null ) {
            return;
        }
        for ( int i = 0; i < soc.length; i++ ) {
            if (soc[i] != null) {
                soc[i].close();
                soc[i] = null; 
            }
        }
    }


    /**
     * to string.
     */
    @Override
    public String toString() {
        return "MPJChannel(on=" + rank + ",to=" + partnerRank + ",tag=" + tag
               + "," + Arrays.toString(soc) + ")";
    }

}
