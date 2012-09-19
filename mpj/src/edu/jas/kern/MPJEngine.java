/*
 * $Id$
 */

package edu.jas.kern;


import org.apache.log4j.Logger;

import mpi.MPI;
import mpi.Comm;


/**
 * MPJ engine, provides global MPI service.
 * <b>Note:</b> could eventually be done directly with MPJ, but provides logging. 
 * @author Heinz Kredel
 * <b>Usage:</b> To obtain a reference to the MPJ service communicator use
 *        <code>MPJEngine.getComminicator()</code>. Once an engine has been
 *        created it must be shutdown to exit JAS with
 *        <code>MPJEngine.terminate()</code>. 
 */

public class MPJEngine {


    private static final Logger logger = Logger.getLogger(MPJEngine.class);


    /**
     * Command line arguments. Required for MPJ runtime system.
     */
    protected static String[] cmdline;


    /**
     * Flag for MPJ usage. <b>Note:</b> Only introduced because Google app
     * engine does not support MPJ.
     */
    public static boolean NO_MPJ = false;


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
    static Comm mpjComm;


    /**
     * MPJ engine base tag number.
     */
    public static final int TAG = 11;


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
        if (mpjComm == null) {
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
    public static synchronized Comm getCommunicator() {
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
    public static synchronized Comm getCommunicator(String[] args) {
        if (NO_MPJ) {
            return null;
        }
        if (mpjComm == null) {
            //String[] args = new String[] { }; //"-np " + N_THREADS };
            if (!MPI.Initialized()) {
                if (args == null) {
                    throw new IllegalArgumentException("command line is null");
                }
                cmdline = args;
                args = MPI.Init(args);
                logger.info("MPJ initialized on " + MPI.Get_processor_name());
            }
            mpjComm = MPI.COMM_WORLD;
            logger.info("MPJ size = " + mpjComm.Size() + ", rank = " + mpjComm.Rank());
        }
        return mpjComm;
    }


    /**
     * Stop execution.
     */
    public static synchronized void terminate() {
        if (mpjComm == null) {
            return;
        }
        logger.info("terminating MPJ on rank = " + mpjComm.Rank());
        mpjComm = null;
        if (MPI.Finalized()) {
            return;
        }
        MPI.Finalize();
    }


    /**
     * Set no MPJ usage.
     */
    public static synchronized void setNoMPJ() {
        NO_MPJ = true;
        terminate();
    }


    /**
     * Set MPJ usage.
     */
    public static synchronized void setMPJ() {
        NO_MPJ = false;
    }

}
