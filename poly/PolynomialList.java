/*
 * $Id$
 */

package edu.jas.poly;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * list of polynomials
 * mainly for storage and printing/toString
 * @author Heinz Kredel
 */

public class PolynomialList {

    public final String[] vars;
    public final TermOrder tord;
    public final ArrayList list;

    public PolynomialList( String[] v, int eo, ArrayList l ) {
	vars = v;
	tord = new TermOrder(eo);
	list = l;
    }

    public PolynomialList( String[] v, TermOrder to, ArrayList l ) {
	vars = v;
	tord = to;
	list = l;
    }

    public String toString() {
	StringBuffer erg = new StringBuffer();
        erg.append("(");
        for ( int i = 0; i < vars.length; i++ ) {
            erg.append(vars[i]); 
	    if ( i < vars.length-1 ) erg.append(","); 
        }
        erg.append(")");
        erg.append(" "+tord);
        erg.append("\n");

        Polynomial a;
        OrderedPolynomial oa;
        Iterator it = list.iterator();
        erg.append("(\n");
        while ( it.hasNext() ) {
              Object o = it.next();
	      if ( o instanceof Polynomial ) {
                a = (Polynomial) o;
	        erg.append( a.toString(vars) );
	      } else if ( o instanceof OrderedPolynomial ) {
                oa = (OrderedPolynomial) o;
	        erg.append( oa.toString(vars) );
	      } else {
	        erg.append( o.toString() );
	      }
	      if ( it.hasNext() ) erg.append(",\n");
	      else erg.append("\n");
        }
        erg.append(")");
	return erg.toString();
    }
}
