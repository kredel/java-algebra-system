/*
 * $Id$
 */

package edu.jas.ps;


import java.util.HashMap;

import edu.jas.structure.RingElem;


/**
 * Interface for generating functions for coefficients of power series.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public abstract class Coefficients<C extends RingElem<C>> {


    /**
     * Cache for already computed coefficients.
     */
    public final HashMap<Integer,C> coeffCache;


    /**
     * Protected no arguments constructor.
     */
    public Coefficients() {
        this( new HashMap<Integer,C>() );
    }


    /**
     * Public no arguments constructor.
     */
    public Coefficients(HashMap<Integer,C> cache) {
        coeffCache = cache;
    }


    /**
     * Get cached coefficient or generate coefficient.
     * @param index of requested coefficient.
     * @return coefficient at index.
     */
    public C get(int index) {
        Integer i = index;
        C c = coeffCache.get( i );
        if ( c != null ) {
            return c;
        }
        c = generate( index );
        coeffCache.put( i, c );
        return c;
    }


    /**
     * Generate coefficient.
     * @param index of requested coefficient.
     * @return coefficient at index.
     */
    protected abstract C generate(int index);
 
}
