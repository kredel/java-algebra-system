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

import edu.jas.structure.RingElem;
import edu.jas.structure.PrettyPrint;


/**
 * RelationTable for solvable polynomials.
 * @author Heinz Kredel
 */


public class RelationTable<C extends RingElem<C>> implements Serializable {

    private static Logger logger = Logger.getLogger(RelationTable.class);

    public final Map< List<Integer>, List > table;
    public final GenSolvablePolynomialRing<C> ring;


    /**
     * Constructors for RelationTable requires ring factory.
     */

    public RelationTable(GenSolvablePolynomialRing<C> r) {
        table = new HashMap< List<Integer>, List >();
        ring = r;
        if ( ring == null ) {
           throw new IllegalArgumentException("RelationTable no ring");
        }
    }


    /**
     * RelationTable equals.
     * Tests same keySets only, not relations itself.
     */

    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object p) {
        if ( ! (p instanceof RelationTable) ) {
            System.out.println("no RelationTable");
            return false;
        }
        RelationTable< C > tab = null;
        try {
            tab = (RelationTable< C >)p;
        } catch (ClassCastException ignored) {
        }
        if ( tab == null ) {
           return false;
        }
        if ( ! ring.equals( tab.ring ) ) {
            System.out.println("not same Ring");
            return false;
        }
        for ( List<Integer> k: table.keySet() ) { 
            List a = table.get(k);
            List b = tab.table.get(k);
            if ( b == null ) {
                return false;
            }
            // check contents, but only base relations
            if ( ! a.equals(b) ) {
                return false;
            }
        }
        for ( List<Integer> k: tab.table.keySet() ) { 
            List a = table.get(k);
            List b = tab.table.get(k);
            if ( a == null ) {
               return false;
            }
            // check contents, but only base relations
            if ( ! a.equals(b) ) {
               return false;
            }
        }
        return true;
    }


    public String toString() {
        List v;
        StringBuffer s = new StringBuffer("RelationTable[");
        boolean first = true;
        for ( List<Integer> k: table.keySet() ) { 
            if ( first ) {
               first = false;
            } else {
               s.append( ", " );
            }
            s.append( k.toString() );
            v = table.get( k );
            s.append("=");
            s.append( v.toString() );
        }
        s.append("]");
        return s.toString();
    }


    public String toString(String[] vars) {
        if ( vars == null ) {
            return toString();
        }
        List v;
        StringBuffer s = new StringBuffer("RelationTable\n(");
        if ( PrettyPrint.isTrue() ) {
            boolean first = true;
            for ( List<Integer> k: table.keySet() ) { 
                if ( first ) {
                    first = false;
                    s.append( "\n" );
                } else {
                    s.append( ",\n" );
                }
                v = table.get( k );
                for (Iterator jt = v.iterator(); jt.hasNext(); ) { 
                    ExpVectorPair ep = (ExpVectorPair)jt.next();
                    s.append("( " + ep.getFirst().toString(vars) + " ), " );
                    s.append("( " + ep.getSecond().toString(vars) + " ), " );
                    GenSolvablePolynomial<C> p 
                        = (GenSolvablePolynomial<C>)jt.next();
                    s.append("( " + p.toString(vars) + " )" );
                    if ( jt.hasNext() ) {
                        s.append(",\n");
                    }
                }
            }
        } else {
            boolean first = true;
            for ( List<Integer> k: table.keySet() ) { 
                if ( first ) {
                    first = false;
                } else {
                    s.append( ",\n" );
                }
                v = table.get( k );
                for (Iterator jt = v.iterator(); jt.hasNext(); ) { 
                    ExpVectorPair ep = (ExpVectorPair)jt.next();
                    s.append("( " + ep.getFirst().toString(vars) + " ), " );
                    s.append("( " + ep.getSecond().toString(vars) + " ), " );
                    GenSolvablePolynomial<C> p 
                        = (GenSolvablePolynomial<C>)jt.next();
                    s.append("( " + p.toString(vars) + " )" );
                    if ( jt.hasNext() ) {
                        s.append(",\n");
                    }
                }
            }
        }
        s.append("\n)\n");
        return s.toString();
    }


    /**
     * Update RelationTable with new relation.
     * relation is e * f = p
     */

    public void update(ExpVector e, ExpVector f, GenSolvablePolynomial<C> p) {
        if ( logger.isDebugEnabled() ) {
            logger.info("new relation = " + e + " .*. " + f + " = " + p);
        }
        if ( p == null ) {
           throw new IllegalArgumentException("RelationTable update p == null");
        }
        List<Integer> key = makeKey(e,f);
        ExpVectorPair evp = new ExpVectorPair( e, f );
        List part = table.get( key );
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


    /**
     * Lookup RelationTable for exiting relation.
     * Find p with e * f = p.
     * If no relation for e * f is contained in the table then
     * return the symmetric product p = 1 e f. 
     */

    public TableRelation<C> lookup(ExpVector e, ExpVector f) {
        List<Integer> key = makeKey(e,f);
        List part = table.get( key );
        if ( part == null ) { // symmetric product
            ExpVector ef = ExpVector.EVSUM( e, f );
            GenSolvablePolynomial<C> p = ring.getONE().multiply( ef );
            return new TableRelation<C>(null,null,p);
        }
        ExpVectorPair evp = new ExpVectorPair( e, f );
        ExpVector ep = null;
        ExpVector fp = null;
        ExpVectorPair look = null;
        GenSolvablePolynomial<C> p = null;
        for ( Iterator it = part.iterator(); it.hasNext(); ) {
            look = (ExpVectorPair)it.next();
            p = (GenSolvablePolynomial<C>)it.next();
            if ( evp.isMultiple( look ) ) {
                ep = e.dif( look.getFirst() );
                fp = f.dif( look.getSecond() );
                if ( ep.isZERO() ) {
                    ep = null;
                }
                if ( fp.isZERO() ) {
                    fp = null;
                }
                return new TableRelation<C>(ep,fp,p);
            }
        }
        // unreacheable code!
        return new TableRelation<C>(ep,fp,p);
    }


    /**
     * Construct a key for (e,f).
     */
    protected List<Integer> makeKey(ExpVector e, ExpVector f) {
        int[] de = e.dependencyOnVariables();
        int[] df = f.dependencyOnVariables();
        List<Integer> key = new ArrayList<Integer>( de.length + df.length );
        for (int i = 0; i < de.length; i++ ) {
            key.add( new Integer( de[i] ) );
        }
        for (int i = 0; i < df.length; i++ ) {
            key.add( new Integer( df[i] ) );
        }
        return key;
    }


    /**
     * Size of the table, i.e. the number of non-commutative relations.
     */
    public int size() {
        int s = 0;
        if ( table == null ) {
            return s;
        }
        for ( Iterator<List> it = table.values().iterator(); it.hasNext(); ) {
            List list = it.next();
            s += list.size()/2;
        }
        return s;
    }


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend all ExpVectors by i elements.
     */

    public void extend(RelationTable<C> tab) {  
        if ( tab.table.size() == 0 ) {
            return;
        }
        int i = ring.nvar - tab.ring.nvar;
        int j = 0;
        long k = 0l;
        List val;
        for ( List<Integer> key: tab.table.keySet() ) { 
            val = tab.table.get( key );
            for ( Iterator jt = val.iterator(); jt.hasNext(); ) { 
                ExpVectorPair ep = (ExpVectorPair)jt.next();
                ExpVector e = ep.getFirst();
                ExpVector f = ep.getSecond();
                GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>)jt.next();
                ExpVector ex = e.extend(i,j,k); 
                ExpVector fx = f.extend(i,j,k); 
                GenSolvablePolynomial<C> px 
                   = (GenSolvablePolynomial<C>)p.extend(ring,j,k);
                this.update( ex, fx, px ); 
            }
        }
        return;
    }


    /**
     * Contract variables. Used e.g. in module embedding.
     * remove i elements of each ExpVector.
     */
    public void contract(RelationTable<C> tab) { 
        if ( tab.table.size() == 0 ) {
            return;
        }
        int i = tab.ring.nvar - ring.nvar;
        List val;
        for ( List<Integer> key: tab.table.keySet() ) { 
            val = tab.table.get( key );
            for (Iterator jt = val.iterator(); jt.hasNext(); ) { 
                ExpVectorPair ep = (ExpVectorPair)jt.next();
                ExpVector e = ep.getFirst();
                ExpVector f = ep.getSecond();
                GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>)jt.next();
                ExpVector ec = e.contract(i,e.length()-i); 
                ExpVector fc = f.contract(i,f.length()-i); 
                Map<ExpVector,GenPolynomial<C>> mc = p.contract(ring);
                GenSolvablePolynomial<C> pc = null;
                for ( GenPolynomial<C> x : mc.values() ) {
                    if ( pc != null ) {
                       // should not happen 
                       logger.info("p = " + p);
                       throw new RuntimeException("Map.size() != 1: " + mc.size());
                    }
                    pc = (GenSolvablePolynomial<C>)x;
                }
                this.update( ec, fc, pc );
            }
        }
        return;
    }

}




/**
 * TableRelation container for storage and printing in RelationTable.
 * @author Heinz Kredel
 */

class TableRelation<C extends RingElem<C>> implements Serializable {

    public final ExpVector e;
    public final ExpVector f;
    public final GenSolvablePolynomial<C> p;

    public TableRelation(ExpVector e, ExpVector f, 
                         GenSolvablePolynomial<C> p) {
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