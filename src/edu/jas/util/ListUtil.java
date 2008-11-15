/*
 * $Id$
 */

package edu.jas.util;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;


/**
 * List utilities, e.g. 
 * map functor on list elements.
 * @author Heinz Kredel
 */

public class ListUtil {


    private static final Logger logger = Logger.getLogger(ListUtil.class);
    // private static boolean debug = logger.isDebugEnabled();


    /**
     * Map a unary function to the list.
     * @param f evaluation functor.
     * @return new list elements f(this(e)).
     */
    public static <C extends RingElem<C>>
           List<C> map(List<C> list, UnaryFunctor<? super C,C> f) {
        if ( list == null || list.size() == 0) {
            return list;
        }
        List<C> nl;
        if ( list instanceof ArrayList ) {
            nl = new ArrayList<C>( list.size() );
        } else if ( list instanceof LinkedList ) {
            nl = new LinkedList<C>();
        } else {
            throw new RuntimeException("list type not implemented");
        }
        for ( C c : list ) {
            C n = f.eval( c );
            nl.add( c );
        }
        return nl;
    }

}