/*
 * $Id$
 */

package edu.jas.util;

import org.apache.log4j.Logger;

import edu.unima.ky.parallel.Semaphore;

    /**
     * terminating helper class
     * like a barrier with coming and going
     * @author Heinz Kredel
     */

public class Terminator {

        private static Logger logger = Logger.getLogger(Terminator.class);

        private int workers = 0;
        private int idler = 0;
        private Semaphore fin = new Semaphore(0);

        public Terminator(int workers) {
            this.workers = workers;
        }

        public synchronized void beIdle() {
            idler++;
            logger.debug("beIdle, idler = " + idler);
            if ( idler >= workers ) {
               fin.V();
            }
        }

        public synchronized void allIdle() {
            idler = workers;
            fin.V();
        }

        public synchronized void notIdle() {
            idler--;
            logger.debug("notIdle, idler = " + idler);
        }

        public boolean hasJobs() {
            return ( idler < workers );
        }

        public void done() {
            try { fin.P();
            } catch (InterruptedException e) { }
            logger.debug("done, idler = " + idler);
        }

}
