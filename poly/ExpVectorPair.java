/*
 * $Id$
 */

package edu.jas.poly;

import java.io.Serializable;


/**
 * ExpVectorPair
 * implements exponent vectors for polynomials.
 * @author Heinz Kredel
 */


public class ExpVectorPair implements Serializable {

    private final ExpVector e1;
    private final ExpVector e2;


    /**
     * Constructors for ExpVectorPair
     */

    public ExpVectorPair(ExpVector e, ExpVector f) {
        e1 = e;
        e2 = f;
    }

    public ExpVector getFirst() {
        return e1;
    } 

    public ExpVector getSecond() {
        return e2;
    } 


    public String toString() {
        StringBuffer s = new StringBuffer("ExpVectorPair[");
        s.append(e1.toString());
        s.append(",");
        s.append(e2.toString());
        s.append("]");
        return s.toString();
    }


    public boolean equals(Object B) { 
       if ( ! (B instanceof ExpVectorPair) ) return false;
       return equals( (ExpVectorPair)B );
    }


    public boolean equals(ExpVectorPair b) { 
       boolean t = e1.equals( b.getFirst() ); 
       t = t && e2.equals( b.getSecond() ); 
       return t;
    }


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
