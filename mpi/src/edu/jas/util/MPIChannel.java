/*
 * $Id$
 */


package edu.jas.util;


import java.io.IOException;
import java.util.Arrays;

import mpi.Comm;
import mpi.MPIException;
import mpi.MPI;
import mpi.Status;

import org.apache.log4j.Logger;

import edu.jas.kern.MPIEngine;


/**
 * MPIChannel provides a communication channel for Java objects using MPI or TCP/IP 
 * to a given rank.
 * @author Heinz Kredel
 */
public final class MPIChannel {


    private static final Logger logger = Logger.getLogger(MPIChannel.class);


    public static final int CHANTAG = MPIEngine.TAG + 2;


    /*
     * Underlying MPI engine.
     */
    private final Comm engine; // essentially static (when useTCP) !


    /*
     * Size of Comm.
     */
    private final int size;


    /*
     * This rank.
     */
    private final int rank;


    /*
     * TCP/IP object channels with tags.
     */
    private static TaggedSocketChannel[] soc = null;


    /*
     * Transport layer.
     * true: use TCP/IP socket layer, false: use MPI transport layer.
     * Can not be set to false for OpenMPI Java: not working.
     */
    static final boolean useTCP = true;


    /*
     * Partner rank.
     */
    private final int partnerRank;


    /*
     * Message tag.
     */
    private final int tag;


    /**
     * Constructs a MPI channel on the given MPI engine.
     * @param s MPI communicator object.
     * @param r rank of MPI partner.
     */
    public MPIChannel(Comm s, int r) throws IOException, MPIException {
        this(s, r, CHANTAG);
    }


    /**
     * Constructs a MPI channel on the given MPI engine.
     * @param s MPI communicator object.
     * @param r rank of MPI partner.
     * @param t tag for messages.
     */
    public MPIChannel(Comm s, int r, int t) throws IOException, MPIException {
        engine = s;
        rank = engine.Rank();
        size = engine.Size();
        if (r < 0 || size <= r) {
            throw new IOException("r out of bounds: 0 <= r < size: " + r + ", " + size);
        }
        partnerRank = r;
        tag = t;
        synchronized (engine) {
            if (soc == null && useTCP) {
                int port = ChannelFactory.DEFAULT_PORT;
                ChannelFactory cf;
                if (rank == 0) {
                    cf = new ChannelFactory(port);
                    cf.init();
                    soc = new TaggedSocketChannel[size];
                    soc[0] = null;
                    try {
                        for (int i = 1; i < size; i++) {
                            SocketChannel sc = cf.getChannel(); // TODO not correct wrt rank
                            soc[i] = new TaggedSocketChannel(sc);
                            soc[i].init();
                        }
                    } catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                    cf.terminate();
                } else {
                    cf = new ChannelFactory(port-1); // in case of localhost
                    soc = new TaggedSocketChannel[1];
                    SocketChannel sc = cf.getChannel(MPIEngine.hostNames.get(0), port);
                    soc[0] = new TaggedSocketChannel(sc);
                    soc[0].init();
                    cf.terminate();
                }
            }
        }
        logger.info("constructor: " + this.toString() + ", useTCP: " + useTCP);
    }


    /**
     * Get the MPI engine.
     */
    public Comm getEngine() {
        return engine;
    }


    /**
     * Sends an object.
     * @param v message object.
     */
    public void send(Object v) throws IOException, MPIException {
        send(tag, v, partnerRank);
    }


    /**
     * Sends an object.
     * @param t message tag.
     * @param v message object.
     */
    public void send(int t, Object v) throws IOException, MPIException {
        send(t, v, partnerRank);
    }


    /**
     * Sends an object.
     * @param t message tag.
     * @param v message object.
     * @param pr partner rank.
     */
    void send(int t, Object v, int pr) throws IOException, MPIException {
        if (useTCP) {
            if (soc == null) {
                logger.warn("soc not initialized: lost " + v);
                return;
            }
            if (soc[pr] == null) {
                logger.warn("soc[" + pr + "] not initialized: lost " + v);
                return;
            }
            soc[pr].send(t, v);
        } else {
            Object[] va = new Object[] { v };
            //synchronized (MPJEngine.class) {
            engine.Send(va, 0, va.length, MPI.OBJECT, pr, t);
            //}
        }
    }


    /**
     * Receives an object.
     * @return a message object.
     */
    public Object receive() throws IOException, ClassNotFoundException, MPIException {
        return receive(tag);
    }


    /**
     * Receives an object.
     * @param t message tag.
     * @return a message object.
     */
    public Object receive(int t) throws IOException, ClassNotFoundException, MPIException {
        if (useTCP) {
            if (soc == null) {
                logger.warn("soc not initialized");
                return null;
            }
            if (soc[partnerRank] == null) {
                logger.warn("soc[" + partnerRank + "] not initialized");
                return null;
            }
            try {
                return soc[partnerRank].receive(t);
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
        } else {
            Object[] va = new Object[1];
            Status stat = null;
            //synchronized (MPJEngine.class) {
            stat = engine.Recv(va, 0, va.length, MPI.OBJECT, partnerRank, t);
            //}
            if (stat == null) {
                throw new IOException("received null Status");
            }
            int cnt = stat.Get_count(MPI.OBJECT);
            if (cnt == 0) {
                throw new IOException("no object received");
            }
            if (cnt > 1) {
                logger.warn("too many objects received, ignored " + (cnt - 1));
            }
            // int pr = stat.source;
            // if (pr != partnerRank) {
            //     logger.warn("received out of order message from " + pr);
            // }
            return va[0];
        }
    }


    /**
     * Closes the channel.
     */
    public void close() {
        if (useTCP) {
            if (soc == null) {
                return;
            }
            for (int i = 0; i < soc.length; i++) {
                if (soc[i] != null) {
                    soc[i].close();
                    soc[i] = null;
                }
            }
        }
    }


    /**
     * to string.
     */
    @Override
    public String toString() {
        return "MPIChannel(on=" + rank + ",to=" + partnerRank + ",tag=" + tag + "," + Arrays.toString(soc)
                        + ")";
    }

}
