/*
 * $Id$
 */

package edu.jas.poly;

import edu.jas.poly.ExpVector;

import java.util.Comparator;

/**
 * Term order class for ordered polynomials. 
 */

public final class TermOrder {

    public final static int DEFAULT_EVORD = ExpVector.DEFAULT_EVORD;
    //public final static int DEFAULT_EVORD = ExpVector.INVLEX;

    private final int evord;

    private final Comparator horder;  // highest first 
    private final Comparator lorder;  // lowest first

    private final class EVhorder implements Comparator {
           public int compare(Object o1, Object o2) {
               return -ExpVector.EVCOMP( evord, 
                                         (ExpVector) o1, 
                                         (ExpVector) o2 ); 
           }
    }

    private final class EVlorder implements Comparator {
           public int compare(Object o1, Object o2) {
               return ExpVector.EVCOMP( evord, 
                                        (ExpVector) o1, 
                                        (ExpVector) o2 ); 
           }
    }


    public TermOrder() {
        this(DEFAULT_EVORD);
    }


    public TermOrder(int evord) {
        this.evord = evord;
        horder = new EVhorder();
        lorder = new EVlorder();
    }


    public int getEvord() { 
        return evord; 
    }


    public Comparator getDescendComparator() { 
        return horder; 
    }


    public Comparator getAscendComparator() { 
        return lorder; 
    }

    public boolean equals( Object B ) { 
       if ( ! (B instanceof TermOrder) ) return false;
       return evord == ((TermOrder)B).getEvord();
    }

}
