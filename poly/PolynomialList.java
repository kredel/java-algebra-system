/*
 * $Id$
 */

package edu.jas.poly;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


/**
 * list of polynomials
 * mainly for storage and printing/toString and sorting
 * @author Heinz Kredel
 */

public class PolynomialList {

    public final String[] vars;
    public final TermOrder tord;
    public final List list;
    public final RelationTable table;

    public PolynomialList( String[] v, int eo, List l ) {
        this( v, new TermOrder(eo), l);
    }

    public PolynomialList( String[] v, TermOrder to, List l ) {
        this( v, to, l, null);
    }

    public PolynomialList( String[] v, TermOrder to, 
                           List l, RelationTable rt ) {
	vars = v;
	tord = to;
	list = sort(l);
        table = rt;
    }


    public String toString() {
	StringBuffer erg = new StringBuffer();
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
           erg.append(""+table+"\n\n");
        }

        OrderedPolynomial oa;
        String sa;
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
	        erg.append( sa );
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
	return erg.toString();
    }


    /**
     * Sort a list of polynomials with respect to the ascending order 
     * of the leading Exponent vectors. 
     * The term order is taken from the first polynomials TermOrder.
     */

    public static List sort(List l) {
        if ( l == null ) {
            return l;
        }
        if ( l.size() <= 1 ) { // nothing to sort
            return l;
        }
        OrderedPolynomial p = (OrderedPolynomial)l.get(0);
        Map map = new TreeMap( p.getTermOrder().getAscendComparator() );
        for ( Iterator it = l.iterator(); it.hasNext(); ) {
            p = (OrderedPolynomial)it.next();
            map.put( p.leadingExpVector(), p );
        }
        List s = new ArrayList( map.values() );
        return s;
    }


}
