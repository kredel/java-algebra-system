/*
 * $Id$
 */

//package edu.unima.ky.parallel;
package edu.jas;

//import java.util.Stack;
import java.util.LinkedList;

/**
 * Thread Pool using stack workpile
 * @author Akitoshi Yoshida 
 * @author Heinz Kredel.
 */

public class ThreadPool {
    static final int SIZE = 3;
    int size;
    PoolThread[] workers;
    int nojobs = 0;
    //    Stack jobstack;
    LinkedList jobstack;

    public ThreadPool() {
        this(SIZE);
    }


/**
 * Constructs a new PoolThread
 * @param size of the pool
 */ 
    public ThreadPool(int size) {
	//        jobstack = new Stack();
        jobstack = new LinkedList();
        this.size = size;
        workers = new PoolThread[size];
        for (int i=0; i<size; i++) {
            workers[i] = new PoolThread(this);
            workers[i].start();
        }
    }

/**
 * Terminates the threads
 */
    public void terminate() {
        while ( hasJobs() ) {
            try {
                Thread.currentThread().sleep(100);
            }
            catch (InterruptedException e) {
            }
        }
        for (int i=0; i<size; i++) {
            try { 
                while ( workers[i].isAlive() ) {
                    workers[i].interrupt(); 
                    workers[i].join(100);
                }
            }
            catch (InterruptedException e) { }
        }
    }

/**
 * adds a job to the workpile
 */
    public synchronized void addJob(Runnable job) {
	//        jobstack.push(job);
        jobstack.addLast(job);
	// System.out.println("Thread["+Thread.currentThread().getName()+
        //                "] adding job" );
        if (nojobs > 0) {
	    // nojobs--;
	    // System.out.println("Thread["+Thread.currentThread().getName()+
            //                   "] notifying a jobless worker");
            notify();
        }
    }


/**
 * get a job for processing
 */
    public synchronized Runnable getJob() throws InterruptedException {
        while (jobstack.isEmpty()) {
            nojobs++;
	    // System.out.println("Thread["+Thread.currentThread().getName()+
            //                   "] waiting");
            wait();
            nojobs--;
        }
	//        return (Runnable)jobstack.pop();
        return (Runnable)jobstack.removeFirst();
    }


/**
 * check if there are jobs processed
 */
    public boolean hasJobs() {
	if ( jobstack.size() > 0 ) return true;
        for (int i=0; i<size; i++) {
	    if ( workers[i].working ) return true;
	}
        return false;
    }


/**
 * check if there are more than n jobs 
 */
    public boolean hasJobs(int n) {
	int j = jobstack.size();
	if ( j > 0 && ( j+size > n ) ) return true; 
           // ( ( j > 0 && ( j+size > n ) ) || ( j > n )
	int x = 0;
        for (int i=0; i<size; i++) {
	    if ( workers[i].working ) x++;;
	}
	if ( j+x > n ) return true;
        return false;
    }

}

/**
 * Implements one Thread of the pool.
 */
class PoolThread extends Thread {
    ThreadPool pool;

    boolean working = false;

    public PoolThread(ThreadPool pool) {
        this.pool = pool;
    }


/**
 * Runs the thread.
 */
    public void run() {
        System.out.println( "Thread["+
                        Thread.currentThread().getName()+
                        "] ready" );
	Runnable job;
	int done = 0;
	long time = 0;
	long t;
        boolean running = true;
        while (running) {
            try {
                //System.out.println( "Thread["+
                //               Thread.currentThread().getName()+
                //               "] looking for a job" );
                job = pool.getJob();
                working = true;
                System.out.println( "Thread["+
                               Thread.currentThread().getName()+
                               "] working" );
		t = System.currentTimeMillis();
                job.run(); 
                working = false;
		time += System.currentTimeMillis() - t;
		done++;
                System.out.println( "Thread["+
                               Thread.currentThread().getName()+
                               "] done" );
            }
            catch (InterruptedException e) { running = false; }
        }
        System.out.println( "Thread["+
                        Thread.currentThread().getName()+
                        "] terminated, done "+done+" jobs in " 
                        + time + " milliseconds");
    }

}
