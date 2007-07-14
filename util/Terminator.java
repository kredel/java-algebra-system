/*
 * $Id$
 */

package edu.jas.util;

import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

//import edu.unima.ky.parallel.Semaphore;

/**
 * Terminating helper class.
 * Like a barrier, but with coming and going.
 * @author Heinz Kredel
 */

public class Terminator {

        private static final Logger logger = Logger.getLogger(Terminator.class);

        private int workers = 0;
        private int idler = 0;
        private Semaphore fin = new Semaphore(0);

/**
 * Terminator.
 * @param workers number of expected threads.
 */
        public Terminator(int workers) {
            this.workers = workers;
        }

 /**
  * beIdle.
  */
        public synchronized void beIdle() {
            idler++;
            logger.debug("beIdle, idler = " + idler);
            if ( idler >= workers ) {
               fin.release(); //fin.V();
            }
        }

/**
 * allIdle.
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
            return ( idler < workers );
        }

/**
 * wait done.
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
