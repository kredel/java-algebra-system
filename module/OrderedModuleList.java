/*
 * $Id$
 */

package edu.jas.module;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Comparator;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigRational;

import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.OrderedMapPolynomial;
import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;
import edu.jas.poly.RelationTable;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.OrderedPolynomialList;


/**
 * list of vectors of polynomials
 * mainly for storage and printing/toString and conversion
 * @author Heinz Kredel
 */

public class OrderedModuleList extends ModuleList {

    public OrderedModuleList( String[] v, int eo, List l ) {
        this( null, v, new TermOrder(eo), l);
    }

    public OrderedModuleList( String[] v, TermOrder to, List l ) {
        this( null, v, to, l, null);
    }

    public OrderedModuleList( String[] v, TermOrder to, List l, RelationTable rt ) {
        this( null, v, to, l, rt);
    }

    public OrderedModuleList( Coefficient c, String[] v, int eo, List l ) {
        this( c, v, new TermOrder(eo), l);
    }

    public OrderedModuleList( Coefficient c, String[] v, TermOrder to, List l ) {
        this( c, v, to, l, null);
    }

    public OrderedModuleList( Coefficient c, String[] v, TermOrder to, 
                       List l, RelationTable rt ) {
        super( c, v, to, sort( l ), rt );
    }


    /**
     * equals from Object.
     */

    public boolean equals(Object m) {
        if ( ! (m instanceof OrderedModuleList) ) {
            //System.out.println("ModuleList");
            return false;
        }
        OrderedModuleList ml = (OrderedModuleList)m;
        if ( ! coeff.equals( ml.coeff ) ) {
            //System.out.println("Coefficient");
            return false;
        }
        if ( ! Arrays.equals( vars, ml.vars ) ) {
            //System.out.println("String[]");
            return false;
        }
        if ( ! tord.equals( ml.tord ) ) {
            //System.out.println("TermOrder");
            return false;
        }
        if ( list == null && ml.list != null ) {
            //System.out.println("List, null");
            return false;
        }
        if ( list != null && ml.list == null ) {
            //System.out.println("List, null");
            return false;
        }
        if ( list.size() != ml.list.size() ) {
            //System.out.println("List, size");
            return false;
        }
        Iterator jt = ml.list.iterator();
        for ( Iterator it = list.iterator(); 
              it.hasNext() && jt.hasNext(); ) {
            Object mi = it.next();
            Object mj = jt.next();
            if ( ! ( mi instanceof List ) ) {
                //System.out.println("List, mi");
                return false;
            }
            if ( ! ( mj instanceof List ) ) {
                //System.out.println("List, mj");
                return false;
            }
            Object[] mia = ((List)mi).toArray();
            Object[] mja = ((List)mj).toArray();
            for ( int k = 0; k < mia.length; k++ ) {
                OrderedPolynomial pi = (OrderedPolynomial)mia[k];
                OrderedPolynomial pj = (OrderedPolynomial)mja[k];
                if ( ! pi.equals( pj ) ) {
                    //System.out.println("OrderedPolynomial");
                    //System.out.println("pi = " + pi);
                    //System.out.println("pj = " + pj);
                   return false;
                }
            }
        }

        if ( table == null && ml.table != null ) {
            return false;
        }
        if ( table != null && ml.table == null ) {
            return false;
        }
        // otherwise tables may be different
        return true;
    }



    /**
     * Sort a list of vectors of polynomials with respect to the 
     * ascending order of the leading Exponent vectors of the 
     * first column. 
     * The term order is taken from the first polynomials TermOrder.
     */

    public static List sort(List l) {
        if ( l == null ) {
            return l;
        }
        if ( l.size() <= 1 ) { // nothing to sort
            return l;
        }
        List v = (List)l.get(0);
        OrderedPolynomial p = (OrderedPolynomial)v.get(0);
        final Comparator e = p.getTermOrder().getAscendComparator();
        Comparator c = new Comparator() {
                public int compare(Object o1, Object o2) {
                       List l1 = (List)o1;
                       List l2 = (List)o2;
                       int c = 0;
                       for ( int i = 0; i < l1.size(); i++ ) {
                           OrderedPolynomial p1 = (OrderedPolynomial)l1.get(i);
                           OrderedPolynomial p2 = (OrderedPolynomial)l2.get(i);
                           ExpVector e1 = p1.leadingExpVector();
                           ExpVector e2 = p2.leadingExpVector();
                           if ( e1 == null && e2 != null ) {
                               return -1; 
                           }
                           if ( e1 != null && e2 == null ) {
                               return 1; 
                           }
                           if ( e1 == null && e2 == null ) {
                               continue; 
                           }
                           if ( e1.length() != e2.length() ) {
                              if ( e1.length() > e2.length() ) {
                                 return 1; 
                              } else {
                                 return -1;
                              }
                           }
                           c = e.compare(e1,e2);
                           if ( c != 0 ) {
                               return c;
                           }
                       }
                       return c;
                }
            };
        Object[] s = l.toArray();
        Arrays.sort( s, c );
        return new ArrayList( Arrays.asList(s) );
    }



    /**
     * get OrderedPolynomialList.
     * Embed module in a polynomial ring and sort polynomials. 
     */

    public OrderedPolynomialList getOrderedPolynomialList() {
        PolynomialList pl = super.getPolynomialList();
        return new OrderedPolynomialList(pl.coeff, pl.vars, pl.tord, 
                                         pl.list, pl.table);
    }


    /**
     * get OrderedModuleList from PolynomialList.
     * Extract module from polynomial ring and sort generators. 
     */

    public static OrderedModuleList getOrderedModuleList(
                                       int i, 
                                       PolynomialList pl) {
        ModuleList ml = ModuleList.getModuleList(i,pl);
        return new OrderedModuleList(ml.coeff, ml.vars, ml.tord,
                                     ml.list, ml.table);
    }

}
