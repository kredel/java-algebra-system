/*
 * $Id$
 */

package edu.jas.poly;

import java.util.ArrayList;
import java.util.Iterator;

public class PolynomialList {

    public final String[] vars;
    public final int evord;
    public final ArrayList list;

    public PolynomialList( String[] v, int eo, ArrayList l ) {
	vars = v;
	evord = eo;
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
	switch ( evord ) {
	case ExpVector.INVLEX: erg.append(" INVLEX "); break ;
	case ExpVector.IGRLEX: erg.append(" IGRLEX "); break ;
	case ExpVector.LEX: erg.append(" LEX "); break ;
	case ExpVector.GRLEX: erg.append(" GRLEX "); break ;
	}
        erg.append("\n");

        Polynomial a;
        Iterator it = list.iterator();
        erg.append("(\n");
        while ( it.hasNext() ) {
	      a = (Polynomial) it.next();
	      erg.append(a.toString(vars));
	      if ( it.hasNext() ) erg.append(",\n");
	      else erg.append("\n");
        }
        erg.append(")");
	return erg.toString();
    }
}
