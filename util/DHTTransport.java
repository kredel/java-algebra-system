/*
 * $Id$
 */

package edu.jas.util;

import java.io.Serializable;


/**
 * Transport Container for Distributed version of a HashTable.
 * @author Heinz Kredel.
 */

public class DHTTransport implements Serializable {

  public final Object key;
  public final Object value;


/**
 * Constructs a new DHTTransport Container
 */ 

  public DHTTransport(Object key, Object value) {
      this.key = key;
      this.value = value;
  }

  public String toString() {
      return "" + this.getClass().getName()
             + "("+key+","+value+")";

  }

}
