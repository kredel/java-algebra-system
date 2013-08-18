/*
 * $Id$
 */

package edu.jas.gb;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Semaphore;

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
 * Groebner Base distributed algorithm with MPI. Implements a distributed memory
 * parallel version of Groebner bases. Using MPI and pairlist class, distributed
 * tasks do reduction.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GroebnerBaseDistributedMPI<C extends RingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBaseDistributedMPI.class);


    /**
     * Number of threads to use.
     */
    protected final int threads;


    /**
     * Default number of threads.
     */
    public static final int DEFAULT_THREADS = 2;


    /*
     * Pool of threads to use. <b>Note:</b> No ComputerThreads for one node
     * tests
     */
    protected transient final ThreadPool pool;


    /*
     * Underlying MPI engine.
     */
    protected transient final Comm engine;


    /**
     * Constructor.
     */
    public GroebnerBaseDistributedMPI() throws MPIException {
        this(DEFAULT_THREADS);
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     */
    public GroebnerBaseDistributedMPI(int threads) throws MPIException {
        this(threads, new ThreadPool(threads));
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param pool ThreadPool to use.
     */
    public GroebnerBaseDistributedMPI(int threads, ThreadPool pool) throws MPIException {
        this(threads, pool, new OrderedPairlist<C>());
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param pl pair selection strategy
     */
    public GroebnerBaseDistributedMPI(int threads, PairList<C> pl) throws MPIException {
        this(threads, new ThreadPool(threads), pl);
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param pool ThreadPool to use.
     * @param pl pair selection strategy
     */
    public GroebnerBaseDistributedMPI(int threads, ThreadPool pool, PairList<C> pl) throws MPIException {
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
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    @Override
    public void terminate() {
        if (pool == null) {
            return;
        }
        pool.terminate();
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
                logger.info("GBmaster: rank 0");
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
        logger.info("clientPart: rank != 0");
        try {
            clientPart(0);
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
     * Distributed Groebner base, part for MPI master.
     * @param modv number of module variables.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F or null, if a IOException occurs.
     */
    public List<GenPolynomial<C>> GBmaster(int modv, List<GenPolynomial<C>> F) throws MPIException, IOException  {
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        GenPolynomial<C> p;
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
        logger.info("initialize DHT, done pairlist: " + unused);

        DistHashTableMPI<Integer, GenPolynomial<C>> theList = new DistHashTableMPI<Integer, GenPolynomial<C>>(
                        engine);
        theList.init();
        logger.info("done DHT: " + theList);

        List<GenPolynomial<C>> al = pairlist.getList();
        for (int i = 0; i < al.size(); i++) {
            // no wait required
            GenPolynomial<C> nn = theList.put(Integer.valueOf(i), al.get(i));
            if (nn != null) {
                logger.info("double polynomials " + i + ", nn = " + nn + ", al(i) = " + al.get(i));
            }
        }

        Terminator fin = new Terminator(threads - 1);
        MPIReducerServer<C> R;
        for (int i = 1; i < threads; i++) {
            logger.debug("addJob " + i + " of " + threads);
            R = new MPIReducerServer<C>(i, fin, engine, theList, pairlist);
            pool.addJob(R);
        }
        logger.debug("main loop waiting");
        fin.waitDone();
        int ps = theList.size();
        logger.info("#distributed list = " + ps);
        // make sure all polynomials arrived: not needed in master
        // G = (ArrayList)theList.values();
        G = pairlist.getList();
        if (ps != G.size()) {
            logger.info("#distributed list = " + theList.size() + " #pairlist list = " + G.size());
        }
        long time = System.currentTimeMillis();
        List<GenPolynomial<C>> Gp = minimalGB(G); // not jet distributed but threaded
        time = System.currentTimeMillis() - time;
        logger.debug("parallel gbmi = " + time);
        G = Gp;
        logger.info("theList.terminate()");
        theList.terminate();
        logger.info("end" + pairlist);
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

        DistHashTableMPI<Integer, GenPolynomial<C>> theList = new DistHashTableMPI<Integer, GenPolynomial<C>>();
        theList.init();

        MPIChannel chan = new MPIChannel(engine, rank);

        MPIReducerClient<C> R = new MPIReducerClient<C>(chan, theList);
        R.run();

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
 * Distributed server reducing worker threads.
 * @param <C> coefficient type
 */

class MPIReducerServer<C extends RingElem<C>> implements Runnable {


    /*
     * Termination detection coordinator.
     */
    private final Terminator finaler;


    /*
     * Underlying MPI engine.
     */
    protected transient final Comm engine;


    /*
     * MPI channel.
     */
    private MPIChannel pairChannel;


    /*
     * GB rank.
     */
    final int rank;


    /*
     * Distributed HashTable of polynomials.
     */
    private final DistHashTableMPI<Integer, GenPolynomial<C>> theList;


    /*
     * Critical pair list of polynomials.
     */
    private final PairList<C> pairlist;


    private static final Logger logger = Logger.getLogger(MPIReducerServer.class);


    /**
     * Constructor.
     * @param r MPI rank of partner.
     * @param fin termination coordinator to use.
     * @param e MPI communicator to use.
     * @param dl DHT to use.
     * @param L pair selection strategy
     */
    MPIReducerServer(int r, Terminator fin, Comm e, DistHashTableMPI<Integer, GenPolynomial<C>> dl,
                    PairList<C> L) {
        rank = r;
        finaler = fin;
        engine = e;
        theList = dl;
        pairlist = L;
        logger.info("reducer server constructor: " + r);
    }


    /**
     * Main method.
     */
    public void run() {
        logger.info("reducer server running: " + this);
        try {
            pairChannel = new MPIChannel(engine, rank);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (MPIException e) {
            e.printStackTrace();
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("reducer server running: pairChannel = " + pairChannel);
        }
        Pair<C> pair;
        GenPolynomial<C> H = null;
        boolean set = false;
        boolean goon = true;
        int polIndex = -1;
        int red = 0;
        int sleeps = 0;

        // while more requests
        while (goon) {
            // receive request
            logger.debug("receive request");
            Object req = null;
            try {
                req = pairChannel.receive();
            } catch (IOException e) {
                goon = false;
                e.printStackTrace();
            } catch (MPIException e) {
                goon = false;
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                goon = false;
                e.printStackTrace();
            }
            //logger.debug("received request, req = " + req);
            if (req == null) {
                goon = false;
                break;
            }
            if (!(req instanceof GBTransportMessReq)) {
                goon = false;
                break;
            }

            // find pair
            logger.debug("find pair");
            while (!pairlist.hasNext()) { // wait
                if (!set) {
                    finaler.beIdle();
                    set = true;
                }
                if (!finaler.hasJobs() && !pairlist.hasNext()) {
                    goon = false;
                    break;
                }
                try {
                    sleeps++;
                    if (sleeps % 10 == 0) {
                        logger.info(" reducer is sleeping");
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    goon = false;
                    break;
                }
            }
            if (!pairlist.hasNext() && !finaler.hasJobs()) {
                goon = false;
                break; //continue; //break?
            }
            if (set) {
                set = false;
                finaler.notIdle();
            }

            pair = pairlist.removeNext();
            /*
             * send pair to client, receive H
             */
            logger.debug("send pair = " + pair);
            GBTransportMess msg = null;
            if (pair != null) {
                msg = new GBTransportMessPairIndex(pair);
            } else {
                msg = new GBTransportMess(); //End();
                // goon ?= false;
            }
            try {
                pairChannel.send(msg);
            } catch (IOException e) {
                e.printStackTrace();
                goon = false;
                break;
            } catch (MPIException e) {
                e.printStackTrace();
                goon = false;
                break;
            }
            logger.debug("#distributed list = " + theList.size());
            Object rh = null;
            try {
                rh = pairChannel.receive();
            } catch (IOException e) {
                e.printStackTrace();
                goon = false;
                break;
            } catch (MPIException e) {
                e.printStackTrace();
                goon = false;
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                goon = false;
                break;
            }
            //logger.debug("received H polynomial");
            if (rh == null) {
                if (pair != null) {
                    pair.setZero();
                }
            } else if (rh instanceof GBTransportMessPoly) {
                // update pair list
                red++;
                H = ((GBTransportMessPoly<C>) rh).pol;
                if (logger.isDebugEnabled()) {
                    logger.debug("H = " + H);
                }
                if (H == null) {
                    if (pair != null) {
                        pair.setZero();
                    }
                } else {
                    if (H.isZERO()) {
                        pair.setZero();
                    } else {
                        if (H.isONE()) {
                            polIndex = pairlist.putOne();
                            //GenPolynomial<C> nn = 
                            theList.putWait(Integer.valueOf(polIndex), H);
                            goon = false;
                            break;
                        }
                        polIndex = pairlist.put(H);
                        // use putWait ? but still not all distributed
                        //GenPolynomial<C> nn = 
                        theList.putWait(Integer.valueOf(polIndex), H);
                    }
                }
            }
        }
        logger.info("terminated, done " + red + " reductions");

        /*
         * send end mark to client
         */
        logger.debug("send end");
        try {
            pairChannel.send(new GBTransportMessEnd());
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        } catch (MPIException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        finaler.beIdle();
        pairChannel.close();
    }

}


/**
 * Distributed clients reducing worker threads.
 */

class MPIReducerClient<C extends RingElem<C>> implements Runnable {


    private final MPIChannel pairChannel;


    private final DistHashTableMPI<Integer, GenPolynomial<C>> theList;


    private final ReductionPar<C> red;


    private static final Logger logger = Logger.getLogger(MPIReducerClient.class);


    /**
     * Constructor.
     * @param pc MPI communication channel.
     * @param dl DHT to use.
     */
    MPIReducerClient(MPIChannel pc, DistHashTableMPI<Integer, GenPolynomial<C>> dl) {
        pairChannel = pc;
        theList = dl;
        red = new ReductionPar<C>();
    }


    /**
     * Main run method.
     */
    public void run() {
        logger.info("pairChannel = " + pairChannel + " reducer client running");
        Pair<C> pair = null;
        GenPolynomial<C> pi, pj, ps;
        GenPolynomial<C> S;
        GenPolynomial<C> H = null;
        //boolean set = false;
        boolean goon = true;
        int reduction = 0;
        //int sleeps = 0;
        Integer pix, pjx, psx;

        while (goon) {
            /* protocol:
             * request pair, process pair, send result
             */
            // pair = (Pair) pairlist.removeNext();
            Object req = new GBTransportMessReq();
            logger.debug("send request");
            try {
                pairChannel.send(req);
            } catch (IOException e) {
                goon = false;
                e.printStackTrace();
                break;
            } catch (MPIException e) {
                goon = false;
                e.printStackTrace();
                break;
            }
            logger.debug("receive pair, goon");
            Object pp = null;
            try {
                pp = pairChannel.receive();
            } catch (IOException e) {
                goon = false;
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
                break;
            } catch (MPIException e) {
                goon = false;
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
                break;
            } catch (ClassNotFoundException e) {
                goon = false;
                e.printStackTrace();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("received pair = " + pp);
            }
            H = null;
            if (pp == null) { // should not happen
                continue;
            }
            if (pp instanceof GBTransportMessEnd) {
                goon = false;
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
                    if (S.isZERO()) {
                        // pair.setZero(); does not work in dist
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.info("ht(S) = " + S.leadingExpVector());
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
                }
            }

            // send H or must send null
            if (logger.isDebugEnabled()) {
                logger.debug("#distributed list = " + theList.size());
                logger.debug("send H polynomial = " + H);
            }
            try {
                pairChannel.send(new GBTransportMessPoly<C>(H));
            } catch (IOException e) {
                goon = false;
                e.printStackTrace();
            } catch (MPIException e) {
                goon = false;
                e.printStackTrace();
            }
        }
        logger.info("terminated, done " + reduction + " reductions");
        pairChannel.close();
    }
}


/**
 * Distributed server reducing worker threads for minimal GB Not jet distributed
 * but threaded.
 */

class MiMPIReducerServer<C extends RingElem<C>> implements Runnable {


    private final List<GenPolynomial<C>> G;


    private GenPolynomial<C> H;


    private final Semaphore done = new Semaphore(0);


    private final Reduction<C> red;


    private static final Logger logger = Logger.getLogger(MiMPIReducerServer.class);


    /**
     * Constructor.
     * @param G polynomial list.
     * @param p polynomial.
     */
    MiMPIReducerServer(List<GenPolynomial<C>> G, GenPolynomial<C> p) {
        this.G = G;
        H = p;
        red = new ReductionPar<C>();
    }


    /**
     * getNF. Blocks until the normal form is computed.
     * @return the computed normal form.
     */
    public GenPolynomial<C> getNF() {
        try {
            done.acquire(); //done.P();
        } catch (InterruptedException e) {
        }
        return H;
    }


    /**
     * Main run method.
     */
    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("ht(H) = " + H.leadingExpVector());
        }
        H = red.normalform(G, H); //mod
        done.release(); //done.V();
        if (logger.isDebugEnabled()) {
            logger.debug("ht(H) = " + H.leadingExpVector());
        }
        // H = H.monic();
    }
}
