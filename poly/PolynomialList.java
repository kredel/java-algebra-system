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


/**
 * list of polynomials
 * mainly for storage and printing/toString and sorting
 * @author Heinz Kredel
 */

public class PolynomialList {

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
	list = sort(l);
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
        Iterator jt = pl.list.iterator();
        for ( Iterator it = list.iterator(); 
              it.hasNext() && jt.hasNext(); ) {
            Object pi = it.next();
            Object pj = jt.next();
            if ( ! ( pi instanceof OrderedPolynomial ) ) {
                System.out.println("OrderedPolynomial, pi");
                return false;
            }
            if ( ! ( pj instanceof OrderedPolynomial ) ) {
                System.out.println("OrderedPolynomial, pj");
                return false;
            }
            OrderedPolynomial pip = (OrderedPolynomial)pi;
            OrderedPolynomial pjp = (OrderedPolynomial)pj;
            if ( ! pip.equals( pjp ) ) {
               System.out.println("OrderedPolynomial");
               System.out.println("pip = " + pip);
               System.out.println("pjp = " + pjp);
               return false;
            }
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
        final Comparator e = p.getTermOrder().getAscendComparator();
        Comparator c = new Comparator() {
                public int compare(Object o1, Object o2) {
                       OrderedPolynomial p1 = (OrderedPolynomial)o1;
                       OrderedPolynomial p2 = (OrderedPolynomial)o2;
                       ExpVector e1 = p1.leadingExpVector();
                       ExpVector e2 = p2.leadingExpVector();
                       if ( e1 == null ) {
                          return -1; // dont care
                       }
                       if ( e2 == null ) {
                          return 1; // dont care
                       }
                       if ( e1.length() != e2.length() ) {
                          if ( e1.length() > e2.length() ) {
                             return 1; // dont care
                          } else {
                             return -1; // dont care
                          }
                       }
                       return e.compare(e1,e2);
                }
            };
        Object[] s = l.toArray();
        Arrays.sort( s, c );
        return new ArrayList( Arrays.asList(s) );
    }


}
