/*
 * $Id$
 */

package edu.jas;
//package edu.unima.ky.parallel;

import java.io.*;

/**
 * Semaphore
 * This class is safe against Thread InterruptedException.
 * @author Akitoshi Yoshida
 * @author Heinz Kredel.
 */

public class Semaphore implements Serializable {
  private int init;
  private int s;
  private int del;

  /**
   * Constructs a default semaphore
   */
  public Semaphore() {
    this(0);
  }

  /**
   * Constructs a semaphore with the given upper limit value
   */
  public Semaphore(int i) {
    if (i >=0) { init = i; } else { init = 0; }
    s = init;
    del = 0;
  }

  /**
   * Finalizes this object
   */
  protected void finalize() throws Throwable {
    if (init != s) { 
        int x = s - init;
        System.out.println("Semaphore: " + x + " pending operations."); 
    }
   super.finalize();
  }

  /**
   * Performs the P operation
   */
  public synchronized void P() throws InterruptedException {
    while (s <= 0) {
      del++;
      try { this.wait(); 
      } finally { del--; }
    }
    s--;
  }

  /**
   * Performs the time limited P operation
   */
  public synchronized boolean P(int m) throws InterruptedException {
    if (s <= 0) {
      del++;
      try { this.wait(m); 
      } finally { del--; }
      if (s <= 0) return false;
    }
    s--;
    return true;
  }

  /**
   * Performs the V operation
   */
  public synchronized void V() {
    s++;
    if (del > 0) {
      this.notify();
    }
  }

  /**
   * checks if Semaphore is positive
   */
  public boolean isPositive() {
      return (s > 0);
  }

}
