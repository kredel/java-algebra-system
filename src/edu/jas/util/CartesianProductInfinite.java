/*
 * $Id$
 */

package edu.jas.util;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;


/**
 * Cartesian product of infinite components with iterator.
 * @author Heinz Kredel
 */
public class CartesianProductInfinite<E> implements Iterable<List<E>> {


    /**
     * data structure.
     */
    public final List<Iterable<E>> comps; 


    /**
     * CartesianProduct constructor.
     * @param comps components of the cartesian product.
     */
    public CartesianProductInfinite(List<Iterable<E>> comps) {
        if (comps == null) {
            throw new IllegalArgumentException("null components not allowed");
        }
        this.comps = comps;
    }


    /**
     * Get an iterator over subsets.
     * @return an iterator.
     */
    public Iterator<List<E>> iterator() {
        return new CartesianProductInfiniteIterator<E>(comps);
    }

}


/**
 * Cartesian product infinite iterator.
 * @author Heinz Kredel
 */
class CartesianProductInfiniteIterator<E> implements Iterator<List<E>> {


    /**
     * data structure.
     */
    final List<Iterable<E>> comps;


    final List<Iterator<E>> compit;


    final List<List<E>> fincomps;


    final List<Iterator<E>> fincompit;


    List<E> current;


    long level;


    boolean empty;


    /**
     * CartesianProduct iterator constructor.
     * @param comps components of the cartesian product.
     */
    public CartesianProductInfiniteIterator(List<Iterable<E>> comps) {
        if (comps == null || comps.size() != 2) {
            throw new IllegalArgumentException("null comps not allowed");
        }
        this.comps = comps;
        current = new ArrayList<E>(comps.size());
        compit = new ArrayList<Iterator<E>>(comps.size());
        fincomps = new ArrayList<List<E>>(comps.size());
        fincompit = new ArrayList<Iterator<E>>(comps.size());
        empty = false;
        level = 0;
        for (Iterable<E> ci : comps) {
            Iterator<E> it = ci.iterator();
            E e = it.next();
            current.add(e);
            compit.add(it);
            List<E> fc = new ArrayList<E>();
            fc.add(e); 
            fincomps.add(fc);
            Iterator<E> fit = fc.iterator();
            E d = fit.next(); // remove current
            fincompit.add(fit);
        }
        System.out.println("current   = " + current);
        System.out.println("comps     = " + comps);
        System.out.println("fincomps  = " + fincomps);
    }


    /**
     * Test for availability of a next tuple.
     * @return true if the iteration has more tuples, else false.
     */
    public synchronized boolean hasNext() {
        return !empty;
    }


    /**
     * Get next tuple.
     * @return next tuple.
     */
    public synchronized List<E> next() {
        if (empty) {
            throw new RuntimeException("invalid call of next()");
        }
        List<E> res = new ArrayList<E>(current);
        if ( fincompit.get(0).hasNext() && fincompit.get(1).hasNext() ) {
	    E e0 = fincompit.get(0).next();
	    E e1 = fincompit.get(1).next();
            current = new ArrayList<E>();
            current.add(e0);
            current.add(e1);
            return res;
        }
        level++;
        if ( level % 2 == 1 ) {
            Collections.reverse(fincomps.get(0));
        } else {
            Collections.reverse(fincomps.get(1));
        }
        fincomps.get(0).add( compit.get(0).next() );
        fincomps.get(1).add( compit.get(1).next() );
        if ( level % 2 == 0 ) {
            Collections.reverse(fincomps.get(0));
        } else {
            Collections.reverse(fincomps.get(1));
        }
        System.out.println("list(0) = " + fincomps.get(0));
        System.out.println("list(1) = " + fincomps.get(1));
        fincompit.set(0, fincomps.get(0).iterator() );
        fincompit.set(1, fincomps.get(1).iterator() );
        E e0 = fincompit.get(0).next();
	E e1 = fincompit.get(1).next();
	current = new ArrayList<E>();
	current.add(e0);
	current.add(e1);
        return res;
    }


    /**
     * Remove a tuple if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannnot remove tuples");
    }

}
