/*
 * $Id$
 */

//package edu.unima.ky.parallel;
package edu.jas;

//import java.util.Stack;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import edu.unima.ky.parallel.Semaphore;

/**
 * Thread Pool using stack / list workpile
 * @author Akitoshi Yoshida 
 * @author Heinz Kredel.
 */

public class ThreadPool {
    static final int DEFAULT_SIZE = 3;
    protected PoolThread[] workers;
    protected int idleworkers = 0; 
    // should be expressed using strategy pattern
    // List or Collection is not appropriate
                                   // LIFO strategy for recursion
    protected LinkedList jobstack; // FIFO strategy for GB

    protected StrategyEnumeration strategy = StrategyEnumeration.LIFO;

    private static Logger logger = Logger.getLogger(ThreadPool.class);

/**
 * Constructs a new ThreadPool
 * with strategy StrategyEnumeration.FIFO
 * and size DEFAULT_SIZE
 */ 

    public ThreadPool() {
        this(StrategyEnumeration.FIFO,DEFAULT_SIZE);
    }


/**
 * Constructs a new ThreadPool
 * with size DEFAULT_SIZE
 * @param strategy for job processing
 */ 

    public ThreadPool(StrategyEnumeration strategy) {
        this(strategy,DEFAULT_SIZE);
    }


/**
 * Constructs a new ThreadPool
 * with strategy StrategyEnumeration.FIFO
 * @param size of the pool
 */ 

    public ThreadPool(int size) {
        this(StrategyEnumeration.FIFO,size);
    }

/**
 * Constructs a new ThreadPool
 * @param strategy for job processing
 * @param size of the pool
 */ 
    public ThreadPool(StrategyEnumeration strategy, int size) {
        this.strategy = strategy;
        jobstack = new LinkedList(); // ok for all strategies ?
        workers = new PoolThread[size];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new PoolThread(this);
            workers[i].start();
        }
        logger.info("strategy = " + strategy);
    }

/**
 * number of worker threads
 */
    public int getNumber() {
        return workers.length; // not null
    }

/**
 * get used strategy
 */
    public StrategyEnumeration getStrategy() {
        return strategy; 
    }

/**
 * Terminates the threads
 */
    public void terminate() {
        while ( hasJobs() ) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        for (int i = 0; i < workers.length; i++) {
            try { 
                while ( workers[i].isAlive() ) {
                        workers[i].interrupt(); 
                        workers[i].join(100);
                }
            } catch (InterruptedException e) { 
            }
        }
    }

/**
 * adds a job to the workpile
 */
    public synchronized void addJob(Runnable job) {
        jobstack.addLast(job);
        logger.debug("adding job" );
        if (idleworkers > 0) {
            logger.debug("notifying a jobless worker");
            notify();
        }
    }


/**
 * get a job for processing
 */
    protected synchronized Runnable getJob() throws InterruptedException {
        while (jobstack.isEmpty()) {
            idleworkers++;
            logger.debug("waiting");
            wait();
            idleworkers--;
        }
        // is expressed using strategy enumeration
        if (strategy == StrategyEnumeration.LIFO) { 
             return (Runnable)jobstack.removeLast(); // LIFO
        } else {
             return (Runnable)jobstack.removeFirst(); // FIFO
        }
    }


/**
 * check if there are jobs for processing
 */
    public boolean hasJobs() {
        if ( jobstack.size() > 0 ) {
            return true;
        }
        for (int i = 0; i < workers.length; i++) {
            if ( workers[i].working ) return true;
        }
        return false;
    }


/**
 * check if there are more than n jobs for processing
 */
    public boolean hasJobs(int n) {
        int j = jobstack.size();
        if ( j > 0 && ( j + workers.length > n ) ) return true;
           // if j > 0 no worker should be idle
           // ( ( j > 0 && ( j+workers.length > n ) ) || ( j > n )
        int x = 0;
        for (int i=0; i < workers.length; i++) {
            if ( workers[i].working ) x++;
        }
        if ( (j + x) > n ) return true;
        return false;
    }

}

/**
 * Implements one Thread of the pool.
 */
class PoolThread extends Thread {
    ThreadPool pool;
    private static Logger logger = Logger.getLogger(ThreadPool.class);

    boolean working = false;

    public PoolThread(ThreadPool pool) {
        this.pool = pool;
    }


/**
 * Run the thread.
 */
    public void run() {
        logger.info( "ready" );
        Runnable job;
        int done = 0;
        long time = 0;
        long t;
        boolean running = true;
        while (running) {
            try {
                logger.debug( "looking for a job" );
                job = pool.getJob();
                working = true;
                logger.info( "working" );
                t = System.currentTimeMillis();
                job.run(); 
                working = false;
                time += System.currentTimeMillis() - t;
                done++;
                logger.info( "done" );
            } catch (InterruptedException e) { 
              running = false; 
            }
        }
        logger.info( "terminated, done " + done + " jobs in " 
                        + time + " milliseconds");
    }

}
