/*
 * $Id$
 */

package edu.jas.poly;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import java.io.Serializable;

import org.apache.log4j.Logger;


/**
 * RelationTable for solvable polynomials.
 * @author Heinz Kredel
 */


public class RelationTable implements Serializable {

    private static Logger logger = Logger.getLogger(RelationTable.class);

    //private final List table;
    private final Map table;
    private int numvar;

    /**
     * Constructors for RelationTable
     */

    public RelationTable() {
        table = new HashMap();
        numvar = -1;
    }

    public int getNumvar() {
        return numvar;
    }

    public String toString() {
        Object k, v;
        StringBuffer s = new StringBuffer("RelationTable[");
        for (Iterator it = table.keySet().iterator(); it.hasNext(); ) { 
            k = it.next();
            s.append( k.toString() );
            v = table.get( k );
            s.append("=");
            s.append( v.toString() );
            if ( it.hasNext() ) {
               s.append(",");
            }
        }
        s.append("]");
        return s.toString();
    }


    public String toString(String[] vars) {
        if ( vars == null ) {
            return toString();
        }
        Object k, v;
        StringBuffer s = new StringBuffer("RelationTable\n(\n");
        for (Iterator it = table.keySet().iterator(); it.hasNext(); ) { 
            k = it.next();
            v = table.get( k );
            List l = (List)v;
            for (Iterator jt = l.iterator(); jt.hasNext(); ) { 
                ExpVectorPair ep = (ExpVectorPair)jt.next();
                s.append("( " + ep.getFirst().toString(vars) + " ), " );
                s.append("( " + ep.getSecond().toString(vars) + " ), " );
                OrderedPolynomial p = (OrderedPolynomial)jt.next();
                s.append("( " + p.toString(vars) + " )" );
                if ( jt.hasNext() ) {
                   s.append(",\n");
                }
            }
            if ( it.hasNext() ) {
               s.append(",\n");
            }
        }
        s.append("\n)");
        return s.toString();
    }


    public void update(ExpVector e, ExpVector f, OrderedPolynomial p) {
        if ( logger.isDebugEnabled() ) {
            logger.info("new relation = " + e + " .*. " + f + " = " + p);
        }
        if ( numvar < 0 ) {
            numvar = e.length();
        }
        if ( numvar != e.length() ) {
            logger.info("relation = " + e + " .*. " + f + " = " + p);
            throw new RuntimeException("update "+numvar+" != e.len "+e.length());
        }
        List key = makeKey(e,f);
        ExpVectorPair evp = new ExpVectorPair( e, f );
        List part = (List)table.get( key );
        if ( part == null ) { // initialization only
           part = new LinkedList();
           part.add( evp );
           part.add( p );
           table.put( key, part );
           return;
        }
        Object o;
        int index = -1;
        for ( ListIterator it = part.listIterator(); it.hasNext(); ) {
            ExpVectorPair look = (ExpVectorPair)it.next();
            o = it.next();
            if ( look.isMultiple( evp ) ) {
                index = it.nextIndex(); 
                // last index of or first index of: break
            }
        }
        if ( index < 0 ) {
           index = 0;
        }
        part.add( index, evp );
        part.add( index+1, p );
        // table.put( key, part ); // required??
    }


    public TableRelation lookup(ExpVector e, ExpVector f, OrderedPolynomial one) {
        if ( numvar < 0 ) {
            numvar = e.length();
        }
        if ( numvar != e.length() ) {
            logger.info("lookup relation = " + e + " .*. " + f + ", " + one);
            logger.info("relation = " + this);
            throw new RuntimeException("update "+numvar+" != e.len "+e.length());
        }
        List key = makeKey(e,f);
        List part = (List)table.get( key );
        if ( part == null ) { // symmetric product
            ExpVector ef = ExpVector.EVSUM( e, f );
            OrderedPolynomial p = (SolvablePolynomial)one.getONE().multiply( ef );
            return new TableRelation(null,null,p);
        }
        ExpVectorPair evp = new ExpVectorPair( e, f );
        ExpVector ep = null;
        ExpVector fp = null;
        OrderedPolynomial p = null;
        for ( Iterator it = part.iterator(); it.hasNext(); ) {
            ExpVectorPair look = (ExpVectorPair)it.next();
            p = (OrderedPolynomial)it.next();
            if ( evp.isMultiple( look ) ) {
                ep = e.dif( look.getFirst() );
                fp = f.dif( look.getSecond() );
                if ( ep.isZERO() ) {
                    ep = null;
                }
                if ( fp.isZERO() ) {
                    fp = null;
                }
                return new TableRelation(ep,fp,p);
            }
        }
        // unreacheable code!
        return new TableRelation(ep,fp,p);
    }


    private List makeKey(ExpVector e, ExpVector f) {
        int[] de = e.dependencyOnVariables();
        int[] df = f.dependencyOnVariables();
        List key = new ArrayList( de.length + df.length );
        for (int i = 0; i < de.length; i++ ) {
            key.add( new Integer( de[i] ) );
        }
        for (int i = 0; i < df.length; i++ ) {
            key.add( new Integer( df[i] ) );
        }
        return key;
    }


    public int size() {
        int s = 0;
        if ( table == null ) {
            return s;
        }
        for ( Iterator it = table.values().iterator(); it.hasNext(); ) {
            List list = (List)it.next();
            s += list.size()/2;
        }
        return s;
    }


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend all ExpVectors by i elements.
     */

    public RelationTable extend(int i, String[] v) {  
        RelationTable tab = new RelationTable();
        if ( size() == 0 ) {
            return tab;
        }
        int j = 0;
        long k = 0l;
        Object key, val;
        for (Iterator it = table.keySet().iterator(); it.hasNext(); ) { 
            key = it.next();
            val = table.get( key );
            List l = (List)val;
            for (Iterator jt = l.iterator(); jt.hasNext(); ) { 
                ExpVectorPair ep = (ExpVectorPair)jt.next();
                ExpVector e = ep.getFirst();
                ExpVector f = ep.getSecond();
                OrderedMapPolynomial p = (OrderedMapPolynomial)jt.next();
                ExpVector ex = e.extend(i,j,k);
                ExpVector fx = f.extend(i,j,k);
                OrderedPolynomial px = ((SolvableOrderedMapPolynomial)p).extend(i,j,k,v,tab);
                tab.update( ex, fx, px );
            }
        }
        return tab;
    }


    /**
     * Contract variables. Used e.g. in module embedding.
     * remove i elements of each ExpVector.
     */

    public RelationTable contract(int i) { 
        RelationTable tab = new RelationTable();
        if ( size() == 0 ) {
            return tab;
        }
        Object key, val;
        for (Iterator it = table.keySet().iterator(); it.hasNext(); ) { 
            key = it.next();
            val = table.get( key );
            List l = (List)val;
            for (Iterator jt = l.iterator(); jt.hasNext(); ) { 
                ExpVectorPair ep = (ExpVectorPair)jt.next();
                ExpVector e = ep.getFirst();
                ExpVector f = ep.getSecond();
                OrderedMapPolynomial p = (OrderedMapPolynomial)jt.next();
                ExpVector ec = e.contract(i,e.length()-i);
                ExpVector fc = f.contract(i,f.length()-i);
                Map mc = ((SolvableOrderedMapPolynomial)p).contract(i,tab);
                OrderedPolynomial pc = null;
                if ( mc.size() == 1 ) {
                   pc = (OrderedPolynomial)(mc.values().toArray())[0];
                } else {
                   // should not happen
                   logger.info("p = " + p);
                   throw new RuntimeException("Map.size() != 1: " + mc.size());
                }
                tab.update( ec, fc, pc );
            }
        }
        return tab;
    }

}




/**
 * TableRelation container for storage and printing in RelationTable.
 * @author Heinz Kredel
 */

class TableRelation implements Serializable {

    public final ExpVector e;
    public final ExpVector f;
    public final OrderedPolynomial p;

    public TableRelation(ExpVector e, ExpVector f, OrderedPolynomial p) {
        this.e = e;
        this.f = f;
        this.p = p;
    }

    public String toString() {
        StringBuffer s = new StringBuffer("TableRelation[");
        s.append(""+e);
        s.append(" | ");
        s.append(""+f);
        s.append(" = ");
        s.append(""+p);
        s.append("]");
        return s.toString();
    }

}