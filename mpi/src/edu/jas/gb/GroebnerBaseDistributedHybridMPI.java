/*
 * $Id$
 */

package edu.jas.gb;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

import mpi.Comm;
import mpi.MPIException;

import org.apache.log4j.Logger;

import edu.jas.kern.MPIEngine;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;
import edu.jas.util.DistHashTableMPI;
import edu.jas.util.MPIChannel;
import edu.jas.util.Terminator;
import edu.jas.util.ThreadPool;


/**
 * Groebner Base distributed hybrid algorithm with MPI. Implements a distributed
 * memory with multi-core CPUs parallel version of Groebner bases with MPI.
 * Using pairlist class, distributed multi-threaded tasks do reduction, one
 * communication channel per remote node.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GroebnerBaseDistributedHybridMPI<C extends RingElem<C>> extends GroebnerBaseAbstract<C> {


    public static final Logger logger = Logger.getLogger(GroebnerBaseDistributedHybridMPI.class);


    public final boolean debug = logger.isDebugEnabled();


    /**
     * Number of threads to use.
     */
    protected final int threads;


    /**
     * Default number of threads.
     */
    protected static final int DEFAULT_THREADS = 2;


    /**
     * Number of threads per node to use.
     */
    protected final int threadsPerNode;


    /**
     * Default number of threads per compute node.
     */
    protected static final int DEFAULT_THREADS_PER_NODE = 1;


    /**
     * Pool of threads to use.
     */
    //protected final ExecutorService pool; // not for single node tests
    protected transient final ThreadPool pool;


    /*
     * Underlying MPI engine.
     */
    protected transient final Comm engine;


    /**
     * Message tag for pairs.
     */
    public static final int pairTag = GroebnerBaseDistributedHybridEC.pairTag.intValue();


    /**
     * Message tag for results.
     */
    public static final int resultTag = GroebnerBaseDistributedHybridEC.resultTag.intValue();


    /**
     * Message tag for acknowledgments.
     */
    public static final int ackTag = GroebnerBaseDistributedHybridEC.ackTag.intValue();


    /**
     * Constructor.
     */
    public GroebnerBaseDistributedHybridMPI() throws MPIException {
        this(DEFAULT_THREADS);
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     */
    public GroebnerBaseDistributedHybridMPI(int threads) throws MPIException {
        this(threads, new ThreadPool(threads));
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param threadsPerNode threads per node to use.
     */
    public GroebnerBaseDistributedHybridMPI(int threads, int threadsPerNode) throws MPIException {
        this(threads, threadsPerNode, new ThreadPool(threads));
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param pool ThreadPool to use.
     */
    public GroebnerBaseDistributedHybridMPI(int threads, ThreadPool pool) throws MPIException {
        this(threads, DEFAULT_THREADS_PER_NODE, pool);
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param threadsPerNode threads per node to use.
     * @param pl pair selection strategy
     */
    public GroebnerBaseDistributedHybridMPI(int threads, int threadsPerNode, PairList<C> pl)
                    throws MPIException {
        this(threads, threadsPerNode, new ThreadPool(threads), pl);
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param threadsPerNode threads per node to use.
     */
    public GroebnerBaseDistributedHybridMPI(int threads, int threadsPerNode, ThreadPool pool)
                    throws MPIException {
        this(threads, threadsPerNode, pool, new OrderedPairlist<C>());
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param threadsPerNode threads per node to use.
     * @param pool ThreadPool to use.
     * @param pl pair selection strategy
     */
    public GroebnerBaseDistributedHybridMPI(int threads, int threadsPerNode, ThreadPool pool, PairList<C> pl)
                    throws MPIException {
        super(new ReductionPar<C>(), pl);
        this.engine = MPIEngine.getCommunicator();
        int size = engine.Size();
        if (size < 2) {
            throw new IllegalArgumentException("Minimal 2 MPI processes required, not " + size);
        }
        if (threads != size || pool.getNumber() != size) {
            throw new IllegalArgumentException("threads != size: " + threads + " != " + size + ", #pool "
                            + pool.getNumber());
        }
        this.threads = threads;
        this.pool = pool;
        this.threadsPerNode = threadsPerNode;
        //logger.info("generated pool: " + pool);
    }


    /**
     * Cleanup and terminate.
     */
    @Override
    public void terminate() {
        if (pool == null) {
            return;
        }
        //pool.terminate();
        pool.cancel();
    }


    /**
     * Distributed Groebner base.
     * @param modv number of module variables.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F or null, if a IOException occurs or on
     *         MPI client part.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        try {
            if (engine.Rank() == 0) {
                return GBmaster(modv, F);
            }
        } catch (MPIException e) {
            logger.info("GBmaster: " + e);
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            logger.info("GBmaster: " + e);
            e.printStackTrace();
            return null;
        }
        pool.terminate(); // not used on clients
        try {
            clientPart(0); // only 0
        } catch (IOException e) {
            logger.info("clientPart: " + e);
            e.printStackTrace();
        } catch (MPIException e) {
            logger.info("clientPart: " + e);
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Distributed hybrid Groebner base.
     * @param modv number of module variables.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F or null, if a IOException occurs.
     */
    public List<GenPolynomial<C>> GBmaster(int modv, List<GenPolynomial<C>> F) throws MPIException,
                    IOException {
        long t = System.currentTimeMillis();
        GenPolynomial<C> p;
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        PairList<C> pairlist = null;
        boolean oneInGB = false;
        int l = F.size();
        int unused = 0;
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while (it.hasNext()) {
            p = it.next();
            if (p.length() > 0) {
                p = p.monic();
                if (p.isONE()) {
                    oneInGB = true;
                    G.clear();
                    G.add(p);
                    //return G; must signal termination to others
                }
                if (!oneInGB) {
                    G.add(p);
                }
                if (pairlist == null) {
                    //pairlist = new OrderedPairlist<C>(modv, p.ring);
                    pairlist = strategy.create(modv, p.ring);
                    if (!p.ring.coFac.isField()) {
                        throw new IllegalArgumentException("coefficients not from a field");
                    }
                }
                // theList not updated here
                if (p.isONE()) {
                    unused = pairlist.putOne();
                } else {
                    unused = pairlist.put(p);
                }
            } else {
                l--;
            }
        }
        //if (l <= 1) {
        //return G; must signal termination to others
        //}
        logger.info("pairlist " + pairlist + ": " + unused);

        logger.debug("looking for clients");
        DistHashTableMPI<Integer, GenPolynomial<C>> theList = new DistHashTableMPI<Integer, GenPolynomial<C>>(
                        engine);
        theList.init();

        List<GenPolynomial<C>> al = pairlist.getList();
        for (int i = 0; i < al.size(); i++) {
            // no wait required
            GenPolynomial<C> nn = theList.put(Integer.valueOf(i), al.get(i));
            if (nn != null) {
                logger.info("double polynomials " + i + ", nn = " + nn + ", al(i) = " + al.get(i));
            }
        }

        Terminator finner = new Terminator((threads - 1) * threadsPerNode);
        HybridReducerServerMPI<C> R;
        logger.info("using pool = " + pool);
        for (int i = 1; i < threads; i++) {
            MPIChannel chan = new MPIChannel(engine, i); // closed in server
            R = new HybridReducerServerMPI<C>(i, threadsPerNode, finner, chan, theList, pairlist);
            pool.addJob(R);
            //logger.info("server submitted " + R);
        }
        logger.info("main loop waiting " + finner);
        finner.waitDone();
        int ps = theList.size();
        logger.info("#distributed list = " + ps);
        // make sure all polynomials arrived: not needed in master
        G = pairlist.getList();
        if (ps != G.size()) {
            logger.info("#distributed list = " + theList.size() + " #pairlist list = " + G.size());
        }
        for (GenPolynomial<C> q : theList.getValueList()) {
            if (q != null && !q.isZERO()) {
                logger.debug("final q = " + q.leadingExpVector());
            }
        }
        logger.debug("distributed list end");
        long time = System.currentTimeMillis();
        List<GenPolynomial<C>> Gp;
        Gp = minimalGB(G); // not jet distributed but threaded
        time = System.currentTimeMillis() - time;
        logger.debug("parallel gbmi time = " + time);
        G = Gp;
        logger.info("server theList.terminate() " + theList.size());
        theList.terminate();
        t = System.currentTimeMillis() - t;
        logger.info("server GB end, time = " + t + ", " + pairlist.toString());
        return G;
    }


    /**
     * GB distributed client.
     * @param rank of the MPI where the server runs on.
     * @throws IOException
     */
    public void clientPart(int rank) throws IOException, MPIException {
        if (rank != 0) {
            throw new UnsupportedOperationException("only master at rank 0 implemented: " + rank);
        }
        Comm engine = MPIEngine.getCommunicator();

        DistHashTableMPI<Integer, GenPolynomial<C>> theList = new DistHashTableMPI<Integer, GenPolynomial<C>>(
                        engine);
        theList.init();

        MPIChannel chan = new MPIChannel(engine, rank);

        ThreadPool pool = new ThreadPool(threadsPerNode);
        logger.info("client using pool = " + pool);
        for (int i = 0; i < threadsPerNode; i++) {
            HybridReducerClientMPI<C> Rr = new HybridReducerClientMPI<C>(chan, theList); // i
            pool.addJob(Rr);
        }
        if (debug) {
            logger.info("clients submitted");
        }
        pool.terminate();
        logger.info("client pool.terminate()");
        chan.close();
        theList.terminate();
        return;
    }


    /**
     * Minimal ordered groebner basis.
     * @param Fp a Groebner base.
     * @return a reduced Groebner base of Fp.
     */
    @Override
    public List<GenPolynomial<C>> minimalGB(List<GenPolynomial<C>> Fp) {
        GenPolynomial<C> a;
        ArrayList<GenPolynomial<C>> G;
        G = new ArrayList<GenPolynomial<C>>(Fp.size());
        ListIterator<GenPolynomial<C>> it = Fp.listIterator();
        while (it.hasNext()) {
            a = it.next();
            if (a.length() != 0) { // always true
                // already monic  a = a.monic();
                G.add(a);
            }
        }
        if (G.size() <= 1) {
            return G;
        }

        ExpVector e;
        ExpVector f;
        GenPolynomial<C> p;
        ArrayList<GenPolynomial<C>> F;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        boolean mt;

        while (G.size() > 0) {
            a = G.remove(0);
            e = a.leadingExpVector();

            it = G.listIterator();
            mt = false;
            while (it.hasNext() && !mt) {
                p = it.next();
                f = p.leadingExpVector();
                mt = e.multipleOf(f);
            }
            it = F.listIterator();
            while (it.hasNext() && !mt) {
                p = it.next();
                f = p.leadingExpVector();
                mt = e.multipleOf(f);
            }
            if (!mt) {
                F.add(a);
            } else {
                // System.out.println("dropped " + a.length());
            }
        }
        G = F;
        if (G.size() <= 1) {
            return G;
        }
        Collections.reverse(G); // important for lex GB

        MiMPIReducerServer<C>[] mirs = (MiMPIReducerServer<C>[]) new MiMPIReducerServer[G.size()];
        int i = 0;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            // System.out.println("doing " + a.length());
            List<GenPolynomial<C>> R = new ArrayList<GenPolynomial<C>>(G.size() + F.size());
            R.addAll(G);
            R.addAll(F);
            mirs[i] = new MiMPIReducerServer<C>(R, a);
            pool.addJob(mirs[i]);
            i++;
            F.add(a);
        }
        G = F;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        for (i = 0; i < mirs.length; i++) {
            a = mirs[i].getNF();
            F.add(a);
        }
        return F;
    }

}


/**
 * Distributed server reducing worker proxy threads.
 * @param <C> coefficient type
 */

class HybridReducerServerMPI<C extends RingElem<C>> implements Runnable {


    public static final Logger logger = Logger.getLogger(HybridReducerServerMPI.class);


    public final boolean debug = logger.isDebugEnabled();


    private final Terminator finner;


    private final MPIChannel pairChannel;


    //protected transient final Comm engine;


    private final DistHashTableMPI<Integer, GenPolynomial<C>> theList;


    private final PairList<C> pairlist;


    private final int threadsPerNode;


    final int rank;


    /**
     * Message tag for pairs.
     */
    public static final int pairTag = GroebnerBaseDistributedHybridMPI.pairTag;


    /**
     * Constructor.
     * @param r MPI rank of partner.
     * @param tpn number of threads per node
     * @param fin terminator
     * @param chan MPIChannel
     * @param dl distributed hash table
     * @param L ordered pair list
     */
    HybridReducerServerMPI(int r, int tpn, Terminator fin, MPIChannel chan,
                    DistHashTableMPI<Integer, GenPolynomial<C>> dl, PairList<C> L) {
        rank = r;
        threadsPerNode = tpn;
        finner = fin;
        this.pairChannel = chan;
        theList = dl;
        pairlist = L;
        //logger.info("reducer server created " + this);
    }


    /**
     * Work loop.
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        //logger.info("reducer server running with " + engine);
        // try {
        //     pairChannel = new MPIChannel(engine, rank); //,pairTag
        // } catch (IOException e) {
        //     e.printStackTrace();
        //     return;
        // } catch (MPIException e) {
        //     e.printStackTrace();
        //     return;
        // }
        if (logger.isInfoEnabled()) {
            logger.info("reducer server running: pairChannel = " + pairChannel);
        }
        // record idle remote workers (minus one?)
        //finner.beIdle(threadsPerNode-1);
        finner.initIdle(threadsPerNode);
        AtomicInteger active = new AtomicInteger(0);

        // start receiver
        HybridReducerReceiverMPI<C> receiver = new HybridReducerReceiverMPI<C>(rank, finner, active,
                        pairChannel, theList, pairlist);
        receiver.start();

        Pair<C> pair;
        //boolean set = false;
        boolean goon = true;
        //int polIndex = -1;
        int red = 0;
        int sleeps = 0;

        // while more requests
        while (goon) {
            // receive request if thread is reported incactive
            logger.debug("receive request");
            Object req = null;
            try {
                req = pairChannel.receive(pairTag);
                //} catch (InterruptedException e) {
                //goon = false;
                //e.printStackTrace();
            } catch (IOException e) {
                goon = false;
                e.printStackTrace();
            } catch (MPIException e) {
                e.printStackTrace();
                return;
            } catch (ClassNotFoundException e) {
                goon = false;
                e.printStackTrace();
            }
            logger.debug("received request, req = " + req);
            if (req == null) {
                goon = false;
                break;
            }
            if (!(req instanceof GBTransportMessReq)) {
                goon = false;
                break;
            }

            // find pair and manage termination status
            logger.debug("find pair");
            while (!pairlist.hasNext()) { // wait
                if (!finner.hasJobs() && !pairlist.hasNext()) {
                    goon = false;
                    break;
                }
                try {
                    sleeps++;
                    //if (sleeps % 10 == 0) {
                    logger.info("waiting for reducers, remaining = " + finner.getJobs());
                    //}
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    goon = false;
                    break;
                }
            }
            if (!pairlist.hasNext() && !finner.hasJobs()) {
                logger.info("termination detection: no pairs and no jobs left");
                goon = false;
                break; //continue; //break?
            }
            finner.notIdle(); // before pairlist get!!
            pair = pairlist.removeNext();
            // send pair to client, even if null
            if (debug) {
                logger.info("active count = " + active.get());
                logger.info("send pair = " + pair);
            }
            GBTransportMess msg = null;
            if (pair != null) {
                msg = new GBTransportMessPairIndex(pair);
            } else {
                msg = new GBTransportMess(); //not End(); at this time
                // goon ?= false;
            }
            try {
                red++;
                pairChannel.send(pairTag, msg);
                int a = active.getAndIncrement();
            } catch (IOException e) {
                e.printStackTrace();
                goon = false;
                break;
            } catch (MPIException e) {
                e.printStackTrace();
                goon = false;
                break;
            }
            //logger.debug("#distributed list = " + theList.size());
        }
        logger.info("terminated, send " + red + " reduction pairs");

        /*
         * send end mark to clients
         */
        logger.debug("send end");
        try {
            for (int i = 0; i < threadsPerNode; i++) { // -1
                pairChannel.send(pairTag, new GBTransportMessEnd());
            }
            logger.info("sent end to clients");
            // send also end to receiver, no more
            //pairChannel.send(resultTag, new GBTransportMessEnd(), engine.Rank());
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        } catch (MPIException e) {
            e.printStackTrace();
        }
        int d = active.get();
        if (d > 0) {
            logger.info("remaining active tasks = " + d);
        }
        receiver.terminate();
        //logger.info("terminated, send " + red + " reduction pairs");
        pairChannel.close();
        logger.info("redServ pairChannel.close()");
        finner.release();
    }
}


/**
 * Distributed server receiving worker thread.
 * @param <C> coefficient type
 */

class HybridReducerReceiverMPI<C extends RingElem<C>> extends Thread {


    public static final Logger logger = Logger.getLogger(HybridReducerReceiverMPI.class);


    public final boolean debug = logger.isDebugEnabled();


    private final DistHashTableMPI<Integer, GenPolynomial<C>> theList;


    private final PairList<C> pairlist;


    private final MPIChannel pairChannel;


    final int rank;


    private final Terminator finner;


    //private final int threadsPerNode;


    private final AtomicInteger active;


    private volatile boolean goon;


    /**
     * Message tag for results.
     */
    public static final int resultTag = GroebnerBaseDistributedHybridMPI.resultTag;


    /**
     * Message tag for acknowledgments.
     */
    public static final int ackTag = GroebnerBaseDistributedHybridMPI.ackTag;


    /**
     * Constructor.
     * @param r MPI rank of partner.
     * @param fin terminator
     * @param a active remote tasks count
     * @param pc tagged socket channel
     * @param dl distributed hash table
     * @param L ordered pair list
     */
    HybridReducerReceiverMPI(int r, Terminator fin, AtomicInteger a, MPIChannel pc,
                    DistHashTableMPI<Integer, GenPolynomial<C>> dl, PairList<C> L) {
        rank = r;
        active = a;
        //threadsPerNode = tpn;
        finner = fin;
        pairChannel = pc;
        theList = dl;
        pairlist = L;
        goon = true;
        //logger.info("reducer server created " + this);
    }


    /**
     * Work loop.
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        //Pair<C> pair = null;
        GenPolynomial<C> H = null;
        int red = 0;
        int polIndex = -1;
        //Integer senderId; // obsolete

        // while more requests
        while (goon) {
            // receive request
            logger.debug("receive result");
            //senderId = null;
            Object rh = null;
            try {
                rh = pairChannel.receive(resultTag);
                int i = active.getAndDecrement();
                //} catch (InterruptedException e) {
                //goon = false;
                ////e.printStackTrace();
                ////?? finner.initIdle(1);
                //break;
            } catch (IOException e) {
                e.printStackTrace();
                goon = false;
                finner.initIdle(1);
                break;
            } catch (MPIException e) {
                e.printStackTrace();
                goon = false;
                finner.initIdle(1);
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                goon = false;
                finner.initIdle(1);
                break;
            }
            logger.info("received result");
            if (rh == null) {
                if (this.isInterrupted()) {
                    goon = false;
                    finner.initIdle(1);
                    break;
                }
                //finner.initIdle(1);
            } else if (rh instanceof GBTransportMessEnd) { // should only happen from server
                logger.info("received GBTransportMessEnd");
                goon = false;
                //?? finner.initIdle(1);
                break;
            } else if (rh instanceof GBTransportMessPoly) {
                // update pair list
                red++;
                GBTransportMessPoly<C> mpi = (GBTransportMessPoly<C>) rh;
                H = mpi.pol;
                //senderId = mpi.threadId;
                if (H != null) {
                    if (logger.isInfoEnabled()) { // debug
                        logger.info("H = " + H.leadingExpVector());
                    }
                    if (!H.isZERO()) {
                        if (H.isONE()) {
                            polIndex = pairlist.putOne();
                            //GenPolynomial<C> nn = 
                            theList.putWait(Integer.valueOf(polIndex), H);
                            //goon = false; must wait for other clients
                            //finner.initIdle(1);
                            //break;
                        } else {
                            polIndex = pairlist.put(H);
                            // use putWait ? but still not all distributed
                            //GenPolynomial<C> nn = 
                            theList.putWait(Integer.valueOf(polIndex), H);
                        }
                    }
                }
            }
            // only after recording in pairlist !
            finner.initIdle(1);
            try {
                pairChannel.send(ackTag, new GBTransportMess());
                logger.debug("send acknowledgement");
            } catch (IOException e) {
                e.printStackTrace();
                goon = false;
                break;
            } catch (MPIException e) {
                e.printStackTrace();
                goon = false;
                break;
            }
        } // end while
        goon = false;
        logger.info("terminated, received " + red + " reductions");
    }


    /**
     * Terminate.
     */
    public void terminate() {
        goon = false;
        try {
            this.join();
            //this.interrupt();
        } catch (InterruptedException e) {
            // unfug Thread.currentThread().interrupt();
        }
        logger.info("terminate end");
    }

}


/**
 * Distributed clients reducing worker threads.
 */

class HybridReducerClientMPI<C extends RingElem<C>> implements Runnable {


    private static final Logger logger = Logger.getLogger(HybridReducerClientMPI.class);


    public final boolean debug = logger.isDebugEnabled();


    private final MPIChannel pairChannel;


    private final DistHashTableMPI<Integer, GenPolynomial<C>> theList;


    private final ReductionPar<C> red;


    //private final int threadsPerNode;


    /*
     * Identification number for this thread.
     */
    //public final Integer threadId; // obsolete


    /**
     * Message tag for pairs.
     */
    public static final int pairTag = GroebnerBaseDistributedHybridMPI.pairTag;


    /**
     * Message tag for results.
     */
    public static final int resultTag = GroebnerBaseDistributedHybridMPI.resultTag;


    /**
     * Message tag for acknowledgments.
     */
    public static final int ackTag = GroebnerBaseDistributedHybridMPI.ackTag;


    /**
     * Constructor.
     * @param tc tagged socket channel
     * @param dl distributed hash table
     */
    HybridReducerClientMPI(MPIChannel tc, DistHashTableMPI<Integer, GenPolynomial<C>> dl) {
        //this.threadsPerNode = tpn;
        pairChannel = tc;
        //threadId = 100 + tid; // keep distinct from other tags
        theList = dl;
        red = new ReductionPar<C>();
    }


    /**
     * Work loop.
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (debug) {
            logger.info("pairChannel   = " + pairChannel + " reducer client running");
        }
        Pair<C> pair = null;
        GenPolynomial<C> pi, pj, ps;
        GenPolynomial<C> S;
        GenPolynomial<C> H = null;
        //boolean set = false;
        boolean goon = true;
        boolean doEnd = true;
        int reduction = 0;
        //int sleeps = 0;
        Integer pix, pjx, psx;

        while (goon) {
            /* protocol:
             * request pair, process pair, send result, receive acknowledgment
             */
            // pair = (Pair) pairlist.removeNext();
            Object req = new GBTransportMessReq();
            logger.debug("send request = " + req);
            try {
                pairChannel.send(pairTag, req);
            } catch (IOException e) {
                goon = false;
                if (debug) {
                    e.printStackTrace();
                }
                logger.info("receive pair, exception ");
                break;
            } catch (MPIException e) {
                goon = false;
                if (debug) {
                    e.printStackTrace();
                }
                logger.info("receive pair, exception ");
                break;
            }
            logger.debug("receive pair, goon = " + goon);
            doEnd = true;
            Object pp = null;
            try {
                pp = pairChannel.receive(pairTag);
                //} catch (InterruptedException e) {
                //goon = false;
                //e.printStackTrace();
            } catch (IOException e) {
                goon = false;
                if (debug) {
                    e.printStackTrace();
                }
                break;
            } catch (MPIException e) {
                goon = false;
                if (debug) {
                    e.printStackTrace();
                }
                break;
            } catch (ClassNotFoundException e) {
                goon = false;
                e.printStackTrace();
            }
            if (debug) {
                logger.info("received pair = " + pp);
            }
            H = null;
            if (pp == null) { // should not happen
                continue;
            }
            if (pp instanceof GBTransportMessEnd) {
                goon = false;
                //doEnd = false; // bug
                continue;
            }
            if (pp instanceof GBTransportMessPair || pp instanceof GBTransportMessPairIndex) {
                pi = pj = ps = null;
                if (pp instanceof GBTransportMessPair) {
                    pair = ((GBTransportMessPair<C>) pp).pair;
                    if (pair != null) {
                        pi = pair.pi;
                        pj = pair.pj;
                        //logger.debug("pair: pix = " + pair.i 
                        //               + ", pjx = " + pair.j);
                    }
                }
                if (pp instanceof GBTransportMessPairIndex) {
                    pix = ((GBTransportMessPairIndex) pp).i;
                    pjx = ((GBTransportMessPairIndex) pp).j;
                    psx = ((GBTransportMessPairIndex) pp).s;
                    pi = theList.getWait(pix);
                    pj = theList.getWait(pjx);
                    ps = theList.getWait(psx);
                    //logger.info("pix = " + pix + ", pjx = " +pjx + ", psx = " +psx);
                }

                if (pi != null && pj != null) {
                    S = red.SPolynomial(pi, pj);
                    //System.out.println("S   = " + S);
                    logger.info("ht(S) = " + S.leadingExpVector());
                    if (S.isZERO()) {
                        // pair.setZero(); does not work in dist
                        H = S;
                    } else {
                        if (debug) {
                            logger.debug("ht(S) = " + S.leadingExpVector());
                        }
                        H = red.normalform(theList, S);
                        reduction++;
                        if (H.isZERO()) {
                            // pair.setZero(); does not work in dist
                        } else {
                            H = H.monic();
                            if (logger.isInfoEnabled()) {
                                logger.info("ht(H) = " + H.leadingExpVector());
                            }
                        }
                    }
                } else {
                    logger.info("pi = " + pi + ", pj = " + pj + ", ps = " + ps);
                }
            }
            if (pp instanceof GBTransportMess) {
                logger.debug("null pair results in null H poly");
            }

            // send H or must send null, if not at end
            if (debug) {
                logger.debug("#distributed list = " + theList.size());
                logger.debug("send H polynomial = " + H);
            }
            try {
                pairChannel.send(resultTag, new GBTransportMessPoly<C>(H)); //,threadId));
                doEnd = false;
            } catch (IOException e) {
                goon = false;
                e.printStackTrace();
            } catch (MPIException e) {
                goon = false;
                e.printStackTrace();
            }
            logger.debug("done send poly message of " + pp);
            try {
                pp = pairChannel.receive(ackTag);
                //} catch (InterruptedException e) {
                //goon = false;
                //e.printStackTrace();
            } catch (IOException e) {
                goon = false;
                if (debug) {
                    e.printStackTrace();
                }
                break;
            } catch (MPIException e) {
                goon = false;
                if (debug) {
                    e.printStackTrace();
                }
                break;
            } catch (ClassNotFoundException e) {
                goon = false;
                e.printStackTrace();
            }
            if (!(pp instanceof GBTransportMess)) {
                logger.error("invalid acknowledgement " + pp);
            }
            logger.debug("received acknowledgment " + pp);
        }
        logger.info("terminated, done " + reduction + " reductions");
        if (doEnd) {
            try {
                pairChannel.send(resultTag, new GBTransportMessEnd());
            } catch (IOException e) {
                //e.printStackTrace();
            } catch (MPIException e) {
                //e.printStackTrace();
            }
            logger.info("terminated, send done");
        }
    }
}
