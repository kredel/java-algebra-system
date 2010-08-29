/*
 * $Id$
 */

package edu.jas.util;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.ArrayList;


import edu.jas.poly.ExpVector;


/**
 * Iterable for ExpVector, using total degree enumeration.
 * @author Heinz Kredel
 */
public class ExpVectorIterable implements Iterable<ExpVector> {


    protected long upperBound;


    final boolean infinite;


    final int nvar;


    /**
     * Constructor.
     * @param nv number of variables.
     */
    public ExpVectorIterable(int nv) {
        this(nv,true,Long.MAX_VALUE);
    }


    /**
     * Constructor.
     * @param nv number of variables.
     * @param ub upper bound for the components.
     */
    public ExpVectorIterable(int nv, long ub) {
        this(nv,false,ub);
    }


    /**
     * Constructor.
     * @param nv number of variables.
     * @param all true, if all elements between 0 and upper bound are enumerated, 
                  false, if only elements of exact upper bund are to be processed.
     * @param ub upper bound for the components.
     */
    protected ExpVectorIterable(int nv, boolean all, long ub) {
        upperBound = ub;
        infinite = all;
        nvar = nv;
    }


    /** Set the upper bound for the iterator.
     * @param ub an upper bound for the iterator elements. 
     */
    public void setUpperBound(long ub) {
        upperBound = ub;
    }


    /** Get the upper bound for the iterator.
     * @return the upper bound for the iterator elements. 
     */
    public long getUpperBound() {
        return upperBound;
    }


    /**
     * Get an iterator over ExpVector.
     * @return an iterator.
     */
    public Iterator<ExpVector> iterator() {
        return new ExpVectorIterator(nvar,infinite,upperBound);
    }

}


/**
 * ExpVector iterator using CartesianProductLongIterator.
 * @author Heinz Kredel
 */
class ExpVectorIterator implements Iterator<ExpVector> {


    /**
     * data structure.
     */
    ExpVector current;


    final long upperBound;


    final boolean infinite;


    final int nvar;


    Iterator<List<Long>> liter;


    protected long totalDegree;


    protected boolean empty;



    /**
     * ExpVector iterator constructor.
     * @param nv number of variables.
     */
    public ExpVectorIterator(int nv) {
        this(nv,true,Long.MAX_VALUE);
    }


    /**
     * ExpVector iterator constructor.
     * @param nv number of variables.
     * @param ub upper bound for the components.
     */
    public ExpVectorIterator(int nv,long ub) {
        this(nv,false,ub);
    }


    /**
     * ExpVector iterator constructor.
     * @param nv number of variables.
     * @param inf true, if all elements between 0 and upper bound are enumerated, 
                  false, if only elements of exact upper bund are to be processed.
     * @param ub an upper bound for the entrys.
     */
    protected ExpVectorIterator(int nv, boolean inf, long ub) {
        nvar = nv;
        infinite = inf;
        upperBound = ub;
        if (upperBound < 0L) {
            throw new IllegalArgumentException("negative upper bound not allowed");
        }
        long totalDegree = 0L;
        if ( !infinite ) {
            totalDegree = upperBound;
        }
        LongIterable li = new LongIterable();
        li.setNonNegativeIterator();
        li.setUpperBound(totalDegree);
        List<LongIterable> tlist = new ArrayList<LongIterable>(nvar);
        for (int i = 0; i < nvar; i++) {
            tlist.add(li); // can reuse li
        }
        Iterable<List<Long>> ib = new CartesianProductLong(tlist,totalDegree);
        liter = ib.iterator();
        empty = (totalDegree > upperBound) || !liter.hasNext();
        current = ExpVector.create(nvar);
        if ( !empty ) {
            List<Long> el = liter.next();
            current = ExpVector.create(el);
            //System.out.println("current = " + current);
        }
    }


    /**
     * Test for availability of a next long.
     * @return true if the iteration has more ExpVectors, else false.
     */
    public synchronized boolean hasNext() {
        return liter.hasNext() || !empty;
    }


    /**
     * Get next ExpVector.
     * @return next ExpVector.
     */
    public synchronized ExpVector next() {
        ExpVector res = current;
        if ( liter.hasNext() ) {
            List<Long> el = liter.next();
            current = ExpVector.create(el);
            //System.out.println("current, liter = " + current);
            if ( !liter.hasNext() ) {
                if ( totalDegree >= upperBound || !infinite ) {
                    empty = true;
                }
            }
            return res;
        }
        totalDegree++;
        //System.out.println("totalDegree = " + totalDegree);
        if ( totalDegree > upperBound ) {
            throw new NoSuchElementException("invalid call of next()");
        }
        LongIterable li = new LongIterable();
        li.setNonNegativeIterator();
        li.setUpperBound(totalDegree);
        List<LongIterable> tlist = new ArrayList<LongIterable>(nvar);
        for (int i = 0; i < nvar; i++) {
            tlist.add(li); // can reuse li
        }
        Iterable<List<Long>> ib = new CartesianProductLong(tlist,totalDegree);
        liter = ib.iterator();
        List<Long> el = liter.next();
        current = ExpVector.create(el);
        //System.out.println("current, tdeg = " + current);
        return res;
    }


    /**
     * Remove a tuple if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannnot remove elements");
    }

}
