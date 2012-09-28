/*
 * $Id$
 */


package edu.jas.util;


import java.io.IOException;

import mpi.Comm;
import mpi.MPI;
import mpi.Status;

import org.apache.log4j.Logger;

import edu.jas.kern.MPJEngine;


/**
 * MPJChannel provides a communication channel for Java objects using MPJ to a
 * given rank.
 * @author Heinz Kredel
 */
public class MPJChannel {


    private static final Logger logger = Logger.getLogger(MPJChannel.class);


    public static final int CHANTAG = MPJEngine.TAG + 2;


    /*
     * Underlying MPJ engine.
     */
    private final Comm engine;


    /*
     * Partner rank.
     */
    private final int partnerRank;


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
        this(s,r,CHANTAG);
    }


    /**
     * Constructs a MPJ channel on the given MPJ engine.
     * @param s MPJ communicator object.
     * @param r rank of MPJ partner.
     * @param t tag for messages.
     */
    public MPJChannel(Comm s, int r, int t) throws IOException {
        engine = s;
        int size = engine.Size();
        if (r < 0 || size <= r) {
            throw new IOException("r out of bounds: 0 <= r < size: " + r + ", " + size);
        }
        partnerRank = r;
        tag = t;
    }


    /**
     * Get the MPJ engine.
     */
    public Comm getEngine() {
        return engine;
    }


    /**
     * Sends an object.
     */
    public void send(Object v) throws IOException {
        send(tag,v);
    }


    /**
     * Sends an object.
     * @param t message tag.
     */
    public void send(int t, Object v) throws IOException {
        Object[] va = new Object[1];
        va[0] = v;
        engine.Send(va, 0, va.length, MPI.OBJECT, partnerRank, t);
        //System.out.println("send: "+v);
    }


    /**
     * Receives an object.
     */
    public Object receive() throws IOException, ClassNotFoundException {
        return receive(tag);
    }

    /**
     * Receives an object.
     * @param t message tag.
     */
    public Object receive(int t) throws IOException, ClassNotFoundException {
        Object[] va = new Object[1];
        //System.out.println("engine.Recv");
        Status stat = engine.Recv(va, 0, va.length, MPI.OBJECT, partnerRank, t);
        int cnt = stat.Get_count(MPI.OBJECT);
        //System.out.println("engine.Recv, cnt = " + cnt);
        if (cnt == 0) {
            throw new IOException("no object received");
        }
        Object v = va[0];
        return v;
    }


    /**
     * Closes the channel.
     */
    public void close() {
        // nothing to do
    }


    /**
     * to string.
     */
    @Override
    public String toString() {
        return "MPJChannel(on=" + engine.Rank() + ",to=" + partnerRank + ",tag=" + tag + ")" ;
    }

}