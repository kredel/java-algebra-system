/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Set;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.jas.arith.Coefficient;


/**
 * Solvable Ordered Map Polynomial. 
 * Abstract implementation of Solvable OrderedPolynomial.
 * Implementation based on Sorted Map / TreeMap
 * @author Heinz Kredel
 */

public abstract class SolvableOrderedMapPolynomial 
                      extends OrderedMapPolynomial {

    private static Logger logger = Logger.getLogger(SolvableOrderedMapPolynomial.class);

    protected final RelationTable table;

    /**
     * Constructors for SolvableOrderedMapPolynomial
     */
    public SolvableOrderedMapPolynomial(RelationTable table) { 
	super();
        this.table = table;
    }

    public SolvableOrderedMapPolynomial(RelationTable table, int r) { 
        super(r);
        this.table = table;
    }

    public SolvableOrderedMapPolynomial(RelationTable table, Map t) { 
	super(t);
        this.table = table;
    }

    public SolvableOrderedMapPolynomial(RelationTable table, String[] v) { 
        super(v);
        this.table = table;
    }

    public SolvableOrderedMapPolynomial(RelationTable table, Coefficient a, ExpVector e) { 
        super(a,e);
        this.table = table;
    }

    public SolvableOrderedMapPolynomial(RelationTable table, TermOrder to) { 
        super(to);
        this.table = table;
    }

    public SolvableOrderedMapPolynomial(RelationTable table, TermOrder to, Map t) { 
	super(to,t);
        this.table = table;
    }

    public SolvableOrderedMapPolynomial(RelationTable table, OrderedPolynomial o) { 
	super(o);
        this.table = table;
    }

    public SolvableOrderedMapPolynomial(RelationTable table, UnorderedPolynomial u) { 
	super(u);
        this.table = table;
    }

    public SolvableOrderedMapPolynomial(RelationTable table, TermOrder to, UnorderedPolynomial u) { 
	super(to,u);
        this.table = table;
    }

    public SolvableOrderedMapPolynomial(RelationTable table, TermOrder to, OrderedPolynomial o) { 
	super(to,o);
        this.table = table;
    }


    /**
     * Methods of SolvableOrderedMapPolynomial
     */

    public RelationTable getRelationTable() { 
        return table; 
    }


    abstract public OrderedPolynomial getZERO(RelationTable table);
    // { return new SolvableOrderedMapPolynomial(table,vars); }

    abstract public OrderedPolynomial getZERO(RelationTable table, TermOrder t);
    // { return new SolvableOrderedMapPolynomial(table,vars,t); }

    abstract public OrderedPolynomial getONE(RelationTable table);
    // { return new SolvableOrderedMapPolynomial(table,vars); }

    abstract public OrderedPolynomial getONE(RelationTable table, TermOrder t);
    // { return new SolvableOrderedMapPolynomial(table,vars,t); }



    public String toString() { 
        StringBuffer erg = new StringBuffer("Solvable{ ");
        erg.append( super.toString() );
        erg.append(" | #rel = ");
        erg.append( table.size() ); // not table itself!
        erg.append(" }");
        return erg.toString(); 
    }


    /**
     * Multiply. Implementation using map.put on result polynomial.
     */

     public OrderedPolynomial multiply(OrderedPolynomial Bp) {  
	if ( Bp == null ) return getZERO();
        if ( ! this.order.equals( Bp.getTermOrder() ) ) { 
           logger.error("term orderings not equal"); 
        }
        // this.val != empty
        int rl = numberOfVariables();
        ExpVector Z = new ExpVector( rl ); // = 0
        OrderedPolynomial Cp = getZERO(table,order); 
	Cp.setVars( vars ); 
        OrderedPolynomial zero = getZERO(table,order);
	zero.setVars( vars ); 
        Coefficient one = null;
        OrderedPolynomial C1 = null;
        OrderedPolynomial C2 = null;
        //Map C = Cp.getMap();
        Map A = val; //this.getMap();
        Iterator ai = A.entrySet().iterator();
        Map B = Bp.getMap();
        Set Bk = B.entrySet();
        while ( ai.hasNext() ) {
            Map.Entry y = (Map.Entry) ai.next();
	    ExpVector e = (ExpVector) y.getKey(); 
            //logger.debug("e = " + e);
            int[] ep = e.dependencyOnVariables();
            int el1 = rl + 1;
            if ( ep.length > 0 ) {
                el1 = ep[0];
            }
            int el1s = rl+1-el1; 
            Coefficient a = (Coefficient) y.getValue(); 
            //logger.debug("a = " + a);
            if ( one == null ) {
                one = a.fromInteger( 1l );
            }
            Iterator bi = Bk.iterator();
            while ( bi.hasNext() ) {
                OrderedPolynomial Cs = null;
                Map.Entry x = (Map.Entry) bi.next();
	        ExpVector f = (ExpVector) x.getKey(); 
                //logger.debug("f = " + f);
                int[] fp = f.dependencyOnVariables();
                int fl1 = 0; 
                if ( fp.length > 0 ) {
                   fl1 = fp[fp.length-1];
                }
                int fl1s = rl+1-fl1; 
                Coefficient b = (Coefficient) x.getValue(); 
                //logger.debug("b = " + b);
                //logger.debug("el1s = " + el1s + " fl1s = " + fl1s);
                if ( el1s <= fl1s ) { // symmetric
	            ExpVector g = ExpVector.EVSUM(e,f); 
                    //logger.debug("g = " + g);
                    Cs = zero.add( one, g ); // symmetric!
                } else { // unsymmetric
                    // split e = e1 * e2, f = f1 * f2
                    ExpVector e1 = e.subst(el1,0);
                    ExpVector e2 = Z.subst(el1,e.getVal(el1));
                    ExpVector e4;
                    ExpVector f1 = f.subst(fl1,0);
                    ExpVector f2 = Z.subst(fl1,f.getVal(fl1));
                    logger.debug("e1 = " + e1 + " e2 = " + e2);
                    logger.debug("f1 = " + f1 + " f2 = " + f2);
                    TableRelation rel = table.lookup(e2,f2,this); 
                    //logger.debug("relation = " + rel);
                    Cs = rel.p; 
                    if ( rel.f != null ) {
                       C2 = zero.add( one, rel.f );
                       Cs = Cs.multiply( C2 );
                       if ( rel.e == null ) {
                          e4 = e2;
                       } else {
                          e4 = e2.dif( rel.e );
                       }
                       table.update(e4,f2,Cs);
                    }
                    if ( rel.e != null ) {
                       C1 = zero.add( one, rel.e );
                       Cs = C1.multiply( Cs );
                       table.update(e2,f2,Cs);
                    }
                    if ( !f1.isZERO() ) {
                       C2 = zero.add( one, f1 );
                       Cs = Cs.multiply( C2 ); 
                       //table.update(?,f1,Cs)
                    }
                    if ( !e1.isZERO() ) {
                       C1 = zero.add( one, e1 );
                       Cs = C1.multiply( Cs ); 
                       //table.update(e1,?,Cs)
                    }
                }
                Coefficient c = a.multiply(b);
                //logger.debug("c = " + c);
                Cs = Cs.multiply( c ); // symmetric!
                //logger.debug("Cs = " + Cs);
                Cp = Cp.add( Cs );
            }
        }
        return Cp;
    }


}