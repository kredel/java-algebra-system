/*
 * $Id$
 */

package edu.jas.kern;


import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import mpi.Comm;
import mpi.Intracomm;
import mpi.MPI;
import mpi.Request;
import mpi.Status;
import mpi.MPIException;

import org.apache.log4j.Logger;


/**
 * MPJ engine, provides global MPI service. <b>Note:</b> could eventually be
 * done directly with MPJ, but provides logging. <b>Usage:</b> To obtain a
 * reference to the MPJ service communicator use
 * <code>MPJEngine.getComminicator()</code>. Once an engine has been created it
 * must be shutdown to exit JAS with <code>MPJEngine.terminate()</code>.
 * @author Heinz Kredel
 */

public final class MPJEngine {


    private static final Logger logger = Logger.getLogger(MPJEngine.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Command line arguments. Required for MPJ runtime system.
     */
    protected static String[] cmdline;


    /**
     * Hostnames of MPI partners.
     */
    public static ArrayList<String> hostNames = new ArrayList<String>();


    /**
     * Flag for MPJ usage. <b>Note:</b> Only introduced because Google app
     * engine does not support MPJ.
     */
    public static boolean NO_MPI = false;


    /**
     * Number of processors.
     */
    public static final int N_CPUS = Runtime.getRuntime().availableProcessors();


    /*
     * Core number of threads.
     * N_CPUS x 1.5, x 2, x 2.5, min 3, ?.
     */
    public static final int N_THREADS = (N_CPUS < 3 ? 3 : N_CPUS + N_CPUS / 2);


    /**
     * MPJ communicator engine.
     */
    static Intracomm mpiComm;


    /**
     * MPJ engine base tag number.
     */
    public static final int TAG = 11;


    /**
     * Hostname suffix.
     */
    public static final String hostSuf = "-ib";


    // /*
    //  * Send locks per tag.
    //  */
    // private static SortedMap<Integer,Object> sendLocks = new TreeMap<Integer,Object>();


    // /*
    //  * receive locks per tag.
    //  */
    // private static SortedMap<Integer,Object> recvLocks = new TreeMap<Integer,Object>();


    /**
     * No public constructor.
     */
    private MPJEngine() {
    }


    /**
     * Set the commandline.
     * @param args the command line to use for the MPJ runtime system.
     */
    public static synchronized void setCommandLine(String[] args) {
        cmdline = args;
    }


    /**
     * Test if a pool is running.
     * @return true if a thread pool has been started or is running, else false.
     */
    public static synchronized boolean isRunning() {
        if (mpiComm == null) {
            return false;
        }
        if (MPI.Finalized()) {
            return false;
        }
        return true;
    }


    /**
     * Get the MPJ communicator.
     * @return a Communicator constructed for cmdline.
     */
    public static synchronized Comm getCommunicator() throws IOException {
        if (cmdline == null) {
            throw new IllegalArgumentException("command line not set");
        }
        return getCommunicator(cmdline);
    }


    /**
     * Get the MPJ communicator.
     * @param args the command line to use for the MPJ runtime system.
     * @return a Communicator.
     */
    public static synchronized Comm getCommunicator(String[] args) throws IOException {
        if (NO_MPI) {
            return null;
        }
        if (mpiComm == null) {
            //String[] args = new String[] { }; //"-np " + N_THREADS };
            if (!MPI.Initialized()) {
                if (args == null) {
                    throw new IllegalArgumentException("command line is null");
                }
                cmdline = args;
                args = MPI.Init(args);
                //int tl = MPI.Init_thread(args,MPI.THREAD_MULTIPLE);
                logger.info("MPJ initialized on " + MPI.Get_processor_name());
                //logger.info("thread level MPI.THREAD_MULTIPLE: " + MPI.THREAD_MULTIPLE 
                //           + ", provided: " + tl);
                if (debug) {
                    logger.debug("remaining args: " + Arrays.toString(args));
                }
            }
            mpiComm = MPI.COMM_WORLD;
            int size = mpiComm.Size();
            int rank = mpiComm.Rank();
            logger.info("MPI size = " + size + ", rank = " + rank);
            // maintain list of hostnames of partners
            hostNames.ensureCapacity(size);
            for ( int i = 0; i < size; i++ ) {
                 hostNames.add("");
            }
            hostNames.set(rank,MPI.Get_processor_name() + hostSuf);
            if (rank == 0) {
                String[] va = new String[1];
                va[0] = hostNames.get(0);
                mpiComm.Bcast(va, 0, va.length, MPI.OBJECT, 0);
                for (int i = 1; i < size; i ++) {
                    Status stat = mpiComm.Recv(va, 0, va.length, MPI.OBJECT, i, TAG);
                    if (stat == null) {
                        throw new IOException("no Status received");
                        //throw new MPIException("no Status received");
                    }
                    int cnt = stat.Get_count(MPI.OBJECT);
                    if (cnt == 0) {
                        throw new IOException("no object received");
                        //throw new MPIException("no object received");
                    }
                    String v = (String) va[0];
                    hostNames.set(i,v);
                }
                logger.info("MPJ partner host names = " + hostNames);
            } else {
                String[] va = new String[1];
                mpiComm.Bcast(va, 0, va.length, MPI.OBJECT, 0);
                hostNames.set(0,va[0]);
                va[0] = hostNames.get(rank);
                mpiComm.Send(va, 0, va.length, MPI.OBJECT, 0, TAG);
            }
        }
        return mpiComm;
    }


    /**
     * Stop execution.
     */
    public static synchronized void terminate() {
        if (mpiComm == null) {
            return;
        }
        logger.info("terminating MPJ on rank = " + mpiComm.Rank());
        mpiComm = null;
        if (MPI.Finalized()) {
            return;
        }
        MPI.Finalize();
    }


    /**
     * Set no MPI usage.
     */
    public static synchronized void setNoMPI() {
        NO_MPI = true;
        terminate();
    }


    /**
     * Set MPI usage.
     */
    public static synchronized void setMPI() {
        NO_MPI = false;
    }


    // /*
    //  * Get send lock per tag.
    //  * @param tag message tag.
    //  * @return a lock for sends.
    //  */
    // public static synchronized Object getSendLock(int tag) {
    //     tag = 11; // one global lock
    //     Object lock = sendLocks.get(tag);
    //     if ( lock == null ) {
    //         lock = new Object();
    //         sendLocks.put(tag,lock);
    //     }
    //     return lock;
    // }


    // /*
    //  * Get receive lock per tag.
    //  * @param tag message tag.
    //  * @return a lock for receives.
    //  */
    // public static synchronized Object getRecvLock(int tag) {
    //     Object lock = recvLocks.get(tag);
    //     if ( lock == null ) {
    //         lock = new Object();
    //         recvLocks.put(tag,lock);
    //     }
    //     return lock;
    // }


    // /*
    //  * Wait for termination of a mpj Request.
    //  * @param req a Request.
    //  * @return a Status after termination of req.Wait().
    //  */
    // public static Status waitRequest(final Request req) {
    //     if ( req == null ) {
    //         throw new IllegalArgumentException("null request");
    //     }
    //     int delay = 10;
    //     int delcnt = 0;
    //     Status stat = null;
    //     while (true) {
    //         synchronized (MPJEngine.class) { // global static lock
    //             stat = req.Get_status(); // should be non destructive, but is not
    //             if ( stat != null ) {
    //                 return req.Wait(); // should terminate immediately
    //             }
    //         }
    //         try {
    //             Thread.currentThread().sleep(delay); // varied a bit
    //         } catch (InterruptedException e) {
    //             logger.info("sleep interrupted");
    //             e.printStackTrace();
    //         }
    //         delcnt++; 
    //         if ( delcnt % 7 == 0 ) {
    //             delay++;
    //             System.out.println("delay(" + delay + "): " + Thread.currentThread().toString());
    //         } 
    //     }
    // }

}
