/*
 * $Id$
 */

package edu.jas;

import edu.unima.ky.parallel.Semaphore;

    /**
     * terminating helper class
     * like a barrier with coming and going
     * @author Heinz Kredel
     */

public class Terminator {

	private int workers = 0;
	private int idler = 0;
	private Semaphore fin = new Semaphore(0);

	public Terminator(int workers) {
	    this.workers = workers;
	}

	public synchronized void beIdle() {
	    idler++;
	    if ( idler >= workers ) fin.V();
	}

	public synchronized void allIdle() {
	    idler = workers;
	    fin.V();
	}

	public synchronized void notIdle() {
	    idler--;
	}

	public boolean hasJobs() {
	    return ( idler < workers );
	}

	public void done() {
            try { fin.P();
	    } catch (InterruptedException e) { }
	}

}
