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

    /**
     * Constructors for RelationTable
     */

    public RelationTable() {
        table = new HashMap();
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


    public void update(ExpVector e, ExpVector f, OrderedPolynomial p) {
        if ( logger.isDebugEnabled() ) {
            logger.info("new relation = " + e + " .*. " + f + " = " + p);
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

}


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