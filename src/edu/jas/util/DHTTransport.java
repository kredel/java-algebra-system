/*
 * $Id$
 */

package edu.jas.util;


import java.io.Serializable;
import java.io.IOException;
import java.rmi.MarshalledObject;


/**
 * Transport container for a distributed version of a HashTable. Immutable
 * objects. Uses MarshalledObject to avoid deserialization on server side.
 * @author Heinz Kredel
 */

public class DHTTransport<K, V> implements Serializable {


    protected final MarshalledObject<K> key;


    protected final MarshalledObject<V> value;


    /**
     * Constructs a new DHTTransport Container.
     * @param key
     * @param value
     */
    public DHTTransport(K key, V value) throws IOException {
        this.key = new MarshalledObject<K>(key);
        this.value = new MarshalledObject<V>(value);
    }


    /**
     * Get the key from this DHTTransport Container.
     */
    public K key() throws IOException, ClassNotFoundException {
        return this.key.get();
    }


    /**
     * Get the value from this DHTTransport Container.
     */
    public V value() throws IOException, ClassNotFoundException {
        return this.value.get();
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return "" + this.getClass().getName() + "(" + key + "," + value + ")";

    }

}
