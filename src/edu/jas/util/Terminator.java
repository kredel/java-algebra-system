/*
 * $Id$
 */

package edu.jas.util;


import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;


//import edu.unima.ky.parallel.Semaphore;

/**
 * Terminating helper class. Like a barrier, but with coming and going.
 * @author Heinz Kredel
 */

public class Terminator {


    private static final Logger logger = Logger.getLogger(Terminator.class);


    private int workers = 0;


    private int idler = 0;


    private final Semaphore fin = new Semaphore(0);


    /**
     * Terminator.
     * @param workers number of expected threads.
     */
    public Terminator(int workers) {
        this.workers = workers;
    }


    /**
     * beIdle.
     * Checks for release().
     */
    public synchronized void beIdle() {
        idler++;
        logger.debug("beIdle, idler = " + idler);
        if (idler >= workers) {
            fin.release(); //fin.V();
        }
    }


    /**
     * initIdle.
     * No check for release().
     * @param i number of idle threads.
     */
    public synchronized void initIdle(int i) {
        idler += i;
        logger.info("initIdle, idler = " + idler);
    }


    /**
     * beIdle.
     * Checks for release().
     * @param i number of idle threads.
     */
    public synchronized void beIdle(int i) {
        idler += i;
        logger.debug("beIdle, idler = " + idler);
        if (idler >= workers) {
            fin.release(); //fin.V();
        }
    }


    /**
     * allIdle.
     * Checks for release().
     */
    public synchronized void allIdle() {
        idler = workers;
        fin.release(); //fin.V();
    }


    /**
     * notIdle.
     */
    public synchronized void notIdle() {
        idler--;
        logger.debug("notIdle, idler = " + idler);
    }


    /**
     * hasJobs.
     * @return true, if there are possibly jobs, else false.
     */
    public boolean hasJobs() {
        return (idler < workers);
    }


    /**
     * Release if possible.
     */
    public synchronized void release() {
        logger.info("release, workers = " + workers);
        if ( idler >= workers ) {
            fin.release(); //fin.V();
            idler++;
        }
        logger.info("release, idler = " + idler);
    }


    /**
     * Wait until released.
     */
    public void waitDone() {
        try {
            fin.acquire(); //fin.P();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.debug("done, idler = " + idler);
    }

}
