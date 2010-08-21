/*
 * $Id$
 */

package edu.jas.util;


import java.util.Iterator;


/**
 * Iterable for Long.
 * @author Heinz Kredel
 */
public class LongIterable implements Iterable<Long> {


    /**
     * Constructor.
     */
    public LongIterable() {
    }


    private boolean nonNegative = true;


    /** Set the iteration algorithm to all elements.
     */
    public void setAllIterator() {
        nonNegative = false;
    }


    /** Set the iteration algorithm to non-negative elements.
     */
    public void setNonNegativeIterator() {
        nonNegative = true;
    }


    /**
     * Get an iterator over Long.
     * @return an iterator.
     */
    public Iterator<Long> iterator() {
        return new LongIterator(nonNegative);
    }

}


/**
 * Long iterator.
 * @author Heinz Kredel
 */
class LongIterator implements Iterator<Long> {


    /**
     * data structure.
     */
    long current;


    boolean empty;


    final boolean nonNegative;


    /**
     * Long iterator constructor.
     */
    public LongIterator() {
        this(false);
    }


    /**
     * Long iterator constructor.
     * @param nn true for an iterator over non-negative longs, false for all elements iterator.
     */
    public LongIterator(boolean nn) {
        current = 0L;
        //System.out.println("current = " + current);
        empty = false;
        nonNegative = nn;
    }


    /**
     * Test for availability of a next long.
     * @return true if the iteration has more Longs, else false.
     */
    public synchronized boolean hasNext() {
        return !empty;
    }


    /**
     * Get next Long.
     * @return next Long.
     */
    public synchronized Long next() {
        if (empty) {
            throw new RuntimeException("invalid call of next()");
        }
        Long res = new Long(current);
        if ( nonNegative ) {
            current++;
        } else if ( current > 0L ) {
            current = -current;
        } else {
            current = -current;
            current++;
        }
        if ( current == Long.MAX_VALUE ) {
            empty = true;
        }
        return res;
    }


    /**
     * Remove a tuple if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannnot remove elements");
    }

}
