/*
 * $Id$
 */

package edu.jas.kern;


import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * ComputerThreads, provides global thread / executor service.
 * <p>
 * <b>Usage:</b> To obtain a reference to the thread pool use
 * <code>ComputerThreads.getPool()</code>. Once a pool has been created it must
 * be shutdown with <code>ComputerThreads.terminate()</code> to exit JAS.
 * </p>
 * @author Heinz Kredel
 */

public class ComputerThreads {


    private static final Logger logger = LogManager.getLogger(ComputerThreads.class);


    // private static final boolean debug = logger.isInfoEnabled();


    /**
     * Flag for thread usage. <b>Note:</b> Only introduced because Google app
     * engine does not support threads.
     * @see edu.jas.ufd.GCDFactory#getProxy(edu.jas.structure.RingFactory)
     */
    public static boolean NO_THREADS = false;


    /**
     * Number of processors.
     */
    public static final int N_CPUS = Runtime.getRuntime().availableProcessors();


    /*
      * Core number of threads.
      * N_CPUS x 1.5, x 2, x 2.5, min 3, ?.
      */
    public static final int N_THREADS = (N_CPUS < 3 ? 3 : N_CPUS + N_CPUS / 2);


    //public static final int N_THREADS = ( N_CPUS < 3 ? 5 : 3*N_CPUS );


    /**
     * Timeout for timed execution.
     * @see edu.jas.fd.SGCDParallelProxy
     */
    static long timeout = 10L; //-1L;


    /**
     * TimeUnit for timed execution.
     * @see edu.jas.fd.SGCDParallelProxy
     */
    static TimeUnit timeunit = TimeUnit.SECONDS;


    /*
      * Saturation policy.
      */
    //public static final RejectedExecutionHandler REH = new ThreadPoolExecutor.CallerRunsPolicy();
    //public static final RejectedExecutionHandler REH = new ThreadPoolExecutor.AbortPolicy();

    /**
     * ExecutorService thread pool.
     */
    //static ThreadPoolExecutor pool = null;
    static ExecutorService pool = null;


    /**
     * No public constructor.
     */
    private ComputerThreads() {
    }


    /**
     * Test if a pool is running.
     * @return true if a thread pool has been started or is running, else false.
     */
    public static synchronized boolean isRunning() {
        if (pool == null) {
            return false;
        }
        if (pool.isTerminated() || pool.isShutdown()) {
            return false;
        }
        return true;
    }


    /**
     * Get the thread pool.
     * @return pool ExecutorService.
     */
    public static synchronized ExecutorService getPool() {
        if (pool == null) {
            pool = Executors.newCachedThreadPool();
        }
        //System.out.println("pool_init = " + pool);
        return pool;
        //return Executors.unconfigurableExecutorService(pool);

        /* not useful, is not run from jython
        Runtime.getRuntime().addShutdownHook( );
        */
    }


    /**
     * Stop execution.
     */
    public static synchronized void terminate() {
        if (pool == null) {
            return;
        }
        if (pool instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) pool;
            //logger.info("task queue size         {}", Q_CAPACITY);
            logger.info("number of CPUs            {}", N_CPUS);
            logger.info("core number of threads    {}", N_THREADS);
            logger.info("current number of threads {}", tpe.getPoolSize());
            logger.info("maximal number of threads {}", tpe.getLargestPoolSize());
            BlockingQueue<Runnable> workpile = tpe.getQueue();
            if (workpile != null) {
                logger.info("queued tasks              {}", workpile.size());
            }
            List<Runnable> r = tpe.shutdownNow();
            if (r.size() != 0) {
                logger.info("unfinished tasks          {}", r.size());
            }
            logger.info("number of scheduled tasks  {}", tpe.getTaskCount());
            logger.info("number of completed tasks {}", tpe.getCompletedTaskCount());
        }
        pool = null;
        //workpile = null;
    }


    /**
     * Set no thread usage.
     */
    public static synchronized void setNoThreads() {
        NO_THREADS = true;
    }


    /**
     * Set thread usage.
     */
    public static synchronized void setThreads() {
        NO_THREADS = false;
    }


    /**
     * Set timeout.
     * @param t time value to set
     */
    public static synchronized void setTimeout(long t) {
        timeout = t;
    }


    /**
     * Get timeout.
     * @return timeout value
     */
    public static synchronized long getTimeout() {
        return timeout;
    }


    /**
     * Set TimeUnit.
     * @param t TimeUnit value to set
     */
    public static synchronized void setTimeUnit(TimeUnit t) {
        timeunit = t;
    }


    /**
     * Get TimeUnit.
     * @return timeunit value
     */
    public static synchronized TimeUnit getTimeUnit() {
        return timeunit;
    }

}
