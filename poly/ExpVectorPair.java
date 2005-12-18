/*
 * $Id$
 */

package edu.jas.poly;

import java.io.Serializable;


/**
 * ExpVectorPair.
 * implements exponent vectors for polynomials.
 * @author Heinz Kredel
 */


public class ExpVectorPair implements Serializable {

    private final ExpVector e1;
    private final ExpVector e2;


    /**
     * Constructors for ExpVectorPair.
     * @param e first part.
     * @param f second part.
     */
    public ExpVectorPair(ExpVector e, ExpVector f) {
        e1 = e;
        e2 = f;
    }

    /**
     * @return first part.
     */
    public ExpVector getFirst() {
        return e1;
    } 

    /**
     * @return second part.
     */
    public ExpVector getSecond() {
        return e2;
    } 

    /**
     * toString.
     */
    public String toString() {
        StringBuffer s = new StringBuffer("ExpVectorPair[");
        s.append(e1.toString());
        s.append(",");
        s.append(e2.toString());
        s.append("]");
        return s.toString();
    }

    /**
     * equals.
     * @param b other.
     * @return true, if this == b, else false.
     */
    public boolean equals(Object B) { 
       if ( ! (B instanceof ExpVectorPair) ) return false;
       return equals( (ExpVectorPair)B );
    }


    /**
     * equals.
     * @param b other.
     * @return true, if this == b, else false.
     */
    public boolean equals(ExpVectorPair b) { 
       boolean t = e1.equals( b.getFirst() ); 
       t = t && e2.equals( b.getSecond() ); 
       return t;
    }


    /**
     * isMultiple.
     * @param p other.
     * @return true, if this is a multiple of b, else false.
     */
    public boolean isMultiple(ExpVectorPair p) {
       boolean w = ExpVector.EVMT( e1, p.getFirst() );
       if ( !w ) {
           return w;
       }
       w = ExpVector.EVMT( e2, p.getSecond() );
       if ( !w ) {
           return w;
       }
       return true;
    }

}
