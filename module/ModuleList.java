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


/**
 * list of vectors of polynomials
 * mainly for storage and printing/toString and conversion
 * @author Heinz Kredel
 */

public class ModuleList {

    public final Coefficient coeff;
    public final String[] vars;
    public final TermOrder tord;
    public final List list;
    public final RelationTable table;

    public ModuleList( String[] v, int eo, List l ) {
        this( null, v, new TermOrder(eo), l);
    }

    public ModuleList( String[] v, TermOrder to, List l ) {
        this( null, v, to, l, null);
    }

    public ModuleList( String[] v, TermOrder to, List l, RelationTable rt ) {
        this( null, v, to, l, rt);
    }

    public ModuleList( Coefficient c, String[] v, int eo, List l ) {
        this( c, v, new TermOrder(eo), l);
    }

    public ModuleList( Coefficient c, String[] v, TermOrder to, List l ) {
        this( c, v, to, l, null);
    }

    public ModuleList( Coefficient c, String[] v, TermOrder to, 
                       List l, RelationTable rt ) {
        if ( c == null ) {
           coeff = new BigRational();
        } else {
           coeff = c;
        }
	vars = v;
	tord = to;
	list = sort( padCols(l) ); 
        table = rt;
    }


    /**
     * equals from Object.
     */

    public boolean equals(Object m) {
        if ( ! (m instanceof ModuleList) ) {
            System.out.println("ModuleList");
            return false;
        }
        ModuleList ml = (ModuleList)m;
        if ( ! coeff.equals( ml.coeff ) ) {
            System.out.println("Coefficient");
            return false;
        }
        if ( ! Arrays.equals( vars, ml.vars ) ) {
            System.out.println("String[]");
            return false;
        }
        if ( ! tord.equals( ml.tord ) ) {
            System.out.println("TermOrder");
            return false;
        }
        if ( list == null && ml.list != null ) {
            System.out.println("List, null");
            return false;
        }
        if ( list != null && ml.list == null ) {
            System.out.println("List, null");
            return false;
        }
        if ( list.size() != ml.list.size() ) {
            System.out.println("List, size");
            return false;
        }
        Iterator jt = ml.list.iterator();
        for ( Iterator it = list.iterator(); 
              it.hasNext() && jt.hasNext(); ) {
            Object mi = it.next();
            Object mj = jt.next();
            if ( ! ( mi instanceof List ) ) {
                System.out.println("List, mi");
                return false;
            }
            if ( ! ( mj instanceof List ) ) {
                System.out.println("List, mj");
                return false;
            }
            Object[] mia = ((List)mi).toArray();
            Object[] mja = ((List)mj).toArray();
            for ( int k = 0; k < mia.length; k++ ) {
                OrderedPolynomial pi = (OrderedPolynomial)mia[k];
                OrderedPolynomial pj = (OrderedPolynomial)mja[k];
                if ( ! pi.equals( pj ) ) {
                   System.out.println("OrderedPolynomial");
                   System.out.println("pi = " + pi);
                   System.out.println("pj = " + pj);
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
        if ( list != null && list.size() > 0 ) {
           Iterator it = list.iterator();
           erg.append("(\n");
           while ( it.hasNext() ) {
                 Object o = it.next();
                 if ( o instanceof List ) {
                     erg.append(" (\n");
                     for ( Iterator jt = ((List)o).iterator(); 
                           jt.hasNext(); ) {
                         Object oo = jt.next();
                         sa = "";
                         if ( oo == null ) {
	                    erg.append( "  ( 0 )" );
                         } else if ( oo instanceof OrderedPolynomial ) {
                            oa = (OrderedPolynomial) oo;
                            sa = oa.toString(vars);
	                    erg.append( "  ( " + sa + " )" );
	                 } else {
                             erg.append( oo.toString() );
                         }
	                 if ( jt.hasNext() ) {
                            erg.append(",\n");
                            if ( sa.length() > 100 ) {
                               erg.append("\n");
                            }
	                 } else { 
                            erg.append("\n");
                         }
                     }
                     erg.append(" )");
	         } else {
	            erg.append( o.toString() );
	         }
	         if ( it.hasNext() ) {
                    erg.append(",\n");
	         } else { 
                    erg.append("\n");
                 }
           }
           erg.append(")");
        }
	return erg.toString();
    }


    /**
     * pad Columns and remove zero rows.
     * make all rows have the same number of columns.
     */

    public List padCols(List l) {
        if ( l == null ) {
           return l;
        }
        int mcols = 0;
        int rs = 0;
        for ( Iterator it = l.iterator(); it.hasNext(); ) {
            List r = (List)it.next();
            if ( r != null ) {
                rs++;
                if ( r.size() > mcols ) {
                    mcols = r.size();
                }
            }
        }
        List norm = new ArrayList( rs );
        for ( Iterator it = l.iterator(); it.hasNext(); ) {
            List r = (List)it.next();
            if ( r != null ) {
                List rn = new ArrayList( r );
                while ( rn.size() < mcols ) {
                    rn.add( (OrderedPolynomial)null );
                }
                norm.add( rn );
            }
        }
        return norm;
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
                       OrderedPolynomial p1 = (OrderedPolynomial)l1.get(0);
                       OrderedPolynomial p2 = (OrderedPolynomial)l2.get(0);
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



    /**
     * get PolynomialList.
     * Embed module in a polynomial ring. 
     */

    public PolynomialList getPolynomialList() {
        if ( list == null ) {
           return new PolynomialList(coeff,vars,tord,list,table);
        }
        if ( list.size() == 0 ) { // nothing to do
           return new PolynomialList(coeff,vars,tord,list,table);
        }
        List row = null;
        int cols = 0;
        OrderedPolynomial p = null;
        int k = 0;
        // search non zero row and non zero polynomial
        while ( p == null && k < list.size() ) {
           row = (List)list.get(k);
           k++;
           //assert (row != null); 
           cols = row.size();
           int j = 0;
           while ( p == null && j < row.size() ) {
              p = (OrderedPolynomial)row.get(j);
              j++;
              if ( p != null ) {
                  if ( p.isZERO() ) {
                      p = null;
                  } else {
                      break;
                  }
              }
           }
        }
        if ( p == null ) {
           return new PolynomialList(coeff,vars,tord,(List)null,table);
        }
        //System.out.println("non zero p = "+p);
        ExpVector e = p.leadingExpVector();
        OrderedPolynomial zero = p.getZERO();

        int vs = e.length();
        List pols = new ArrayList( list.size() );
        String[] v = new String[ vars.length+cols ];
        for ( int i = 0; i < vars.length; i++ ) {
            v[i] = vars[i];
        }
        for ( int i = 0; i < cols; i++ ) {
            v[ vars.length + i ] = "e" + i;
        }
        OrderedMapPolynomial c = null;
        OrderedPolynomial d = null;
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            List r = (List)it.next();
            OrderedPolynomial ext = zero;
            int m = cols-1;
            for ( Iterator jt = r.iterator(); jt.hasNext(); ) {
                c = (OrderedMapPolynomial)jt.next();
                d = c.extend( cols, m, 1l, v );
                ext = ext.add(d); 
                m--;
            }
            pols.add( ext );
        }
        return new PolynomialList(coeff,v,tord,pols,table);
    }

    /**
     * get ModuleList from PolynomialList.
     * Extract module from polynomial ring. 
     */

    public static ModuleList getModuleList(int i, PolynomialList pl) {
        if ( pl == null ) {
           return new ModuleList(pl.coeff,pl.vars,pl.tord,
                                 (List)null,pl.table);
        }
        List l = pl.list;
        if ( l == null ) {
           return new ModuleList(pl.coeff,pl.vars,pl.tord,
                                 (List)null,pl.table);
        }
        int cols = pl.vars.length - i;
        String[] v = new String[ cols ];
        for ( int j = 0; j < cols; j++ ) {
            v[j] = pl.vars[j];
        }
        List m = new ArrayList( l.size() );
        ArrayList zr = new ArrayList( i-1 );
        OrderedPolynomial zero = null;
        for ( int j = 0; j < i; j++ ) {
            zr.add(j,zero);
        }
        for (Iterator it = l.iterator(); it.hasNext(); ) {
            OrderedMapPolynomial p = (OrderedMapPolynomial)it.next();
            if ( p != null ) {
                Map r = p.contract( i );
                //System.out.println("r = " + r ); 
                List row = (List)zr.clone();
                for ( Iterator jt = r.keySet().iterator(); jt.hasNext(); ) {
                    ExpVector e = (ExpVector)jt.next();
                    int[] dov = e.dependencyOnVariables();
                    int ix = 0;
                    if ( dov.length != 1 ) {
                       //throw new RuntimeException("wrong dependencyOnVariables " + e);
                       System.out.println("wrong dependencyOnVariables " + e);
                    } else {
                       ix = dov[0];
                    }
                    ix = i-1 - ix; // revert
                    //System.out.println("ix = " + ix ); 
                    OrderedPolynomial vi = (OrderedPolynomial)r.get( e );
                    if ( vi != null && zero == null ) {
                        if ( ! vi.isZERO() ) {
                            zero = vi.getZERO();
                        }
                    }
                    row.set(ix,vi);
                }
                //System.out.println("row = " + row ); 
                m.add( row );
            }
        }
        // assert zero != null, zero == (0)
        //System.out.println("zero = " + zero ); 
        for (Iterator it = m.iterator(); it.hasNext(); ) {
            ArrayList vr = (ArrayList)it.next();
            for ( int j = 0; j < i; j++ ) {
                if ( vr.get(j) == null ) {
                    vr.set(j,zero);
                }
            }
            //System.out.println("vr = " + vr ); 
        }
        return new ModuleList(pl.coeff,v,pl.tord,m,pl.table);
    }

}
