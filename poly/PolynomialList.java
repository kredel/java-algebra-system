/*
 * $Id$
 */

package edu.jas.poly;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Comparator;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigRational;

import java.io.Serializable;


/**
 * list of polynomials
 * mainly for storage and printing/toString and sorting
 * @author Heinz Kredel
 */

public class PolynomialList implements Serializable {

    public final Coefficient coeff;
    public final String[] vars;
    public final TermOrder tord;
    public final List list;
    public final RelationTable table;

    public PolynomialList( String[] v, int eo, List l ) {
        this( null, v, new TermOrder(eo), l);
    }

    public PolynomialList( String[] v, TermOrder to, List l ) {
        this( null, v, to, l, null);
    }

    public PolynomialList( String[] v, TermOrder to, List l, RelationTable rt ) {
        this( null, v, to, l, rt);
    }

    public PolynomialList( Coefficient c, String[] v, int eo, List l ) {
        this( c, v, new TermOrder(eo), l);
    }

    public PolynomialList( Coefficient c, String[] v, TermOrder to, List l ) {
        this( c, v, to, l, null);
    }

    public PolynomialList( Coefficient c, String[] v, TermOrder to, 
                           List l, RelationTable rt ) {
        if ( c == null ) {
           coeff = new BigRational();
        } else {
           coeff = c;
        }
	vars = v;
	tord = to;
	list = l; //sort(l);
        table = rt;
    }


    /**
     * equals from Object.
     */

    public boolean equals(Object p) {
        if ( ! (p instanceof PolynomialList) ) {
            System.out.println("PolynomialList");
            return false;
        }
        PolynomialList pl = (PolynomialList)p;
        if ( ! coeff.equals( pl.coeff ) ) {
            System.out.println("Coefficient");
            return false;
        }
        if ( ! Arrays.equals( vars, pl.vars ) ) {
            System.out.println("String[]");
            return false;
        }
        if ( ! tord.equals( pl.tord ) ) {
            System.out.println("TermOrder");
            return false;
        }
        if ( list == null && pl.list != null ) {
            System.out.println("List, null");
            return false;
        }
        if ( list != null && pl.list == null ) {
            System.out.println("List, null");
            return false;
        }
        if ( list.size() != pl.list.size() ) {
            System.out.println("List, size");
            return false;
        }
        // compare sorted lists
        // compare sorted lists
        List otl = OrderedPolynomialList.sort( list );
        List opl = OrderedPolynomialList.sort( pl.list );
        if ( ! otl.equals(opl) ) {
            return false;
        }

        if ( table == null && pl.table != null ) {
            return false;
        }
        if ( table != null && pl.table == null ) {
            return false;
        }
        // otherwise tables may be different
        return true;
    }


    public String toString() {
	StringBuffer erg = new StringBuffer();
        if ( coeff != null ) {
            if ( coeff instanceof BigRational ) {
               erg.append("Rat");
            }
        }
        erg.append("(");
        for ( int i = 0; i < vars.length; i++ ) {
            erg.append(vars[i]); 
	    if ( i < vars.length-1 ) {
               erg.append(",");
            } 
        }
        erg.append(")");
        if ( tord.getWeight() == null ) {
           erg.append(" "+tord);
        } else {
           erg.append(" "+tord.weightToString());
        }
        erg.append("\n");

        if ( table != null ) {
            erg.append(table.toString(vars) + "\n\n");
        }

        OrderedPolynomial oa;
        String sa;
        if ( list.size() > 0 ) {
           Iterator it = list.iterator();
           erg.append("(\n");
           while ( it.hasNext() ) {
                 Object o = it.next();
	         //if ( o instanceof Polynomial ) {
                 //  a = (Polynomial) o;
	         //  erg.append( a.toString(vars) );
	         //} else 
                 sa = "";
                 if ( o instanceof OrderedPolynomial ) {
                    oa = (OrderedPolynomial) o;
                    sa = oa.toString(vars);
	            erg.append( "( " + sa + " )" );
	         } else {
	            erg.append( o.toString() );
	         }
	         if ( it.hasNext() ) {
                    erg.append(",\n");
                    if ( sa.length() > 100 ) {
                       erg.append("\n");
                    }
	         } else { 
                    erg.append("\n");
                 }
           }
           erg.append(")");
        }
	return erg.toString();
    }

}
