/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Comparator;
import java.io.Serializable;

import edu.jas.poly.ExpVector;

/**
 * Term order class for ordered polynomials. 
 * @author Heinz Kredel
 */

public final class TermOrder implements Serializable {

    public static final int LEX     = 1;
    public static final int INVLEX  = 2;
    public static final int GRLEX   = 3;
    public static final int IGRLEX  = 4;
    public static final int REVLEX  = 5;
    public static final int REVILEX = 6;
    public static final int REVTDEG = 7;
    public static final int REVITDG = 8;

    public final static int DEFAULT_EVORD = IGRLEX;
    //public final static int DEFAULT_EVORD = INVLEX;

    private final int evord;

    private final Comparator horder;  // highest first 
    private final Comparator lorder;  // lowest first
    private final Comparator sugar;   // graded lowest first

    private final class EVhorder implements Comparator, Serializable {
           public int compare(Object o1, Object o2) {
               return -ExpVector.EVCOMP( evord, 
                                         (ExpVector) o1, 
                                         (ExpVector) o2 ); 
           }
    }

    private final class EVlorder implements Comparator, Serializable {
           public int compare(Object o1, Object o2) {
               return ExpVector.EVCOMP( evord, 
                                        (ExpVector) o1, 
                                        (ExpVector) o2 ); 
           }
    }

    private final class EVsugar implements Comparator, Serializable {
           public int compare(Object o1, Object o2) {
               return ExpVector.EVCOMP( /*INVLEX*/ IGRLEX , 
                                        (ExpVector) o1, 
                                        (ExpVector) o2 ); 
           }
    }

    public TermOrder() {
        this(DEFAULT_EVORD);
    }


    public TermOrder(int evord) {
        if ( evord < LEX || REVITDG < evord ) {
           throw new IllegalArgumentException("invalid term order: "+evord);
        }
        this.evord = evord;
        horder = new EVhorder();
        lorder = new EVlorder();
        sugar  = new EVsugar();
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

    public Comparator getSugarComparator() { 
        return sugar; 
    }


    public boolean equals( Object B ) { 
       if ( ! (B instanceof TermOrder) ) return false;
       return evord == ((TermOrder)B).getEvord();
    }

    public String toString() {
	StringBuffer erg = new StringBuffer();
	switch ( evord ) {
	case INVLEX: erg.append("INVLEX("+evord+")");  break ;
	case IGRLEX: erg.append("IGRLEX("+evord+")");  break ;
	case LEX:    erg.append("LEX("+evord+")");     break ;
	case GRLEX:  erg.append("GRLEX("+evord+")");   break ;
        default:     erg.append("invalid("+evord+")"); break ;
	}
        return erg.toString();
    }
}
