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
                      extends OrderedMapPolynomial 
                      implements SolvablePolynomial {

    private static Logger logger = Logger.getLogger(SolvableOrderedMapPolynomial.class);

    protected final RelationTable table;
 
    private final boolean debug = logger.isDebugEnabled();

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

    public SolvableOrderedMapPolynomial(RelationTable table, TermOrder to, Coefficient a, ExpVector e) { 
        super(to,a,e);
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


    abstract public SolvablePolynomial getZERO(RelationTable table);
    // { return new SolvableOrderedMapPolynomial(table,vars); }

    abstract public SolvablePolynomial getZERO(RelationTable table, TermOrder t);
    // { return new SolvableOrderedMapPolynomial(table,vars,t); }

    abstract public SolvablePolynomial getONE(RelationTable table);
    // { return new SolvableOrderedMapPolynomial(table,vars); }

    abstract public SolvablePolynomial getONE(RelationTable table, TermOrder t);
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
           logger.error("SPol term orderings not equal, this = "+this+", Bp = "+Bp); 
        }
        // this.val != empty
        int rl = numberOfVariables();
        ExpVector Z = new ExpVector( rl ); // = 0
        SolvablePolynomial Cp = getZERO(table,order); 
	Cp.setVars( vars ); 
        SolvablePolynomial zero = getZERO(table,order);
	zero.setVars( vars ); 
        Coefficient one = null;
        SolvablePolynomial C1 = null;
        SolvablePolynomial C2 = null;
        //Map C = Cp.getMap();
        Map A = val; //this.getMap();
        Iterator ai = A.entrySet().iterator();
        Map B = Bp.getMap();
        Set Bk = B.entrySet();
        while ( ai.hasNext() ) {
            Map.Entry y = (Map.Entry) ai.next();
            Coefficient a = (Coefficient) y.getValue(); 
            //logger.debug("a = " + a);
	    ExpVector e = (ExpVector) y.getKey(); 
            if ( debug ) logger.debug("e = " + e);
            int[] ep = e.dependencyOnVariables();
            int el1 = rl + 1;
            if ( ep.length > 0 ) {
                el1 = ep[0];
            }
            int el1s = rl+1-el1; 
            if ( one == null ) {
                one = a.fromInteger( 1l );
            }
            Iterator bi = Bk.iterator();
            while ( bi.hasNext() ) {
                Map.Entry x = (Map.Entry) bi.next();
                Coefficient b = (Coefficient) x.getValue(); 
                //logger.debug("b = " + b);
	        ExpVector f = (ExpVector) x.getKey(); 
                if ( debug ) logger.debug("f = " + f);
                int[] fp = f.dependencyOnVariables();
                int fl1 = 0; 
                if ( fp.length > 0 ) {
                   fl1 = fp[fp.length-1];
                }
                int fl1s = rl+1-fl1; 
                if ( debug ) logger.debug("el1s = " + el1s + " fl1s = " + fl1s);
                SolvablePolynomial Cs = null;
                if ( el1s <= fl1s ) { // symmetric
	            ExpVector g = ExpVector.EVSUM(e,f); 
                    if ( debug ) logger.debug("g = " + g);
                    Cs = (SolvablePolynomial)zero.add( one, g ); // symmetric!
                } else { // unsymmetric
                    // split e = e1 * e2, f = f1 * f2
                    ExpVector e1 = e.subst(el1,0);
                    ExpVector e2 = Z.subst(el1,e.getVal(el1));
                    ExpVector e4;
                    ExpVector f1 = f.subst(fl1,0);
                    ExpVector f2 = Z.subst(fl1,f.getVal(fl1));
                    if ( debug ) logger.debug("e1 = " + e1 + " e2 = " + e2);
                    if ( debug ) logger.debug("f1 = " + f1 + " f2 = " + f2);
                    TableRelation rel = table.lookup(e2,f2,this); 
                    //logger.info("relation = " + rel);
                    Cs = (SolvablePolynomial)rel.p; 
                    if ( rel.f != null ) {
                       C2 = (SolvablePolynomial)zero.add( one, rel.f );
                       Cs = (SolvablePolynomial)Cs.multiply( C2 );
                       if ( rel.e == null ) {
                          e4 = e2;
                       } else {
                          e4 = e2.dif( rel.e );
                       }
                       table.update(e4,f2,Cs);
                    }
                    if ( rel.e != null ) {
                       C1 = (SolvablePolynomial)zero.add( one, rel.e );
                       Cs = (SolvablePolynomial)C1.multiply( Cs );
                       table.update(e2,f2,Cs);
                    }
                    if ( !f1.isZERO() ) {
                       C2 = (SolvablePolynomial)zero.add( one, f1 );
                       Cs = (SolvablePolynomial)Cs.multiply( C2 ); 
                       //table.update(?,f1,Cs)
                    }
                    if ( !e1.isZERO() ) {
                       C1 = (SolvablePolynomial)zero.add( one, e1 );
                       Cs = (SolvablePolynomial)C1.multiply( Cs ); 
                       //table.update(e1,?,Cs)
                    }
                }
                Coefficient c = a.multiply(b);
                //logger.debug("c = " + c);
                Cs = (SolvablePolynomial)Cs.multiply( c ); // symmetric!
                if ( debug ) logger.debug("Cs = " + Cs);
                Cp = (SolvablePolynomial)Cp.add( Cs );
            }
        }
        return Cp;
    }


    /*
    protected static OrderedPolynomial multiply(RelationTable table,
                                                ExpVector e, 
                                                ExpVector f,
                                                ExpVector Z,
                                                Coefficient one,
                                                OrderedPolynomial zero) {  
        int rl = e.length();
        OrderedPolynomial C1 = null;
        OrderedPolynomial C2 = null;

            int[] ep = e.dependencyOnVariables();
            int el1 = rl + 1;
            if ( ep.length > 0 ) {
                el1 = ep[0];
            }
            int el1s = rl+1-el1; 

                int[] fp = f.dependencyOnVariables();
                int fl1 = 0; 
                if ( fp.length > 0 ) {
                   fl1 = fp[fp.length-1];
                }
                int fl1s = rl+1-fl1; 
                //logger.debug("el1s = " + el1s + " fl1s = " + fl1s);
                OrderedPolynomial Cs = null;
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
                    TableRelation rel = table.lookup(e2,f2,one); 
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
            return Cs;
    }
    */



    /**
     * Product with number.
     */

    public OrderedPolynomial multiply(Coefficient b) {  
        OrderedPolynomial Cp = getZERO(table,order); 
        Cp.setVars(vars);
        if ( b.isZERO() ) { 
            return Cp;
        }
        Map C = Cp.getMap();
        Map A = val; //this.getMap();
        Iterator ai = A.entrySet().iterator();
        while ( ai.hasNext() ) {
            Map.Entry y = (Map.Entry) ai.next();
            ExpVector e = (ExpVector) y.getKey(); 
            //System.out.println("e = " + e);
            Coefficient a = (Coefficient) y.getValue(); 
            //System.out.println("a = " + a);
            Coefficient c = a.multiply(b);
            //System.out.println("c = " + c);
            C.put( e, c );
        }
        return Cp;
    }


    /**
     * Product with number and exponent vector.
     */

    public OrderedPolynomial multiply(Coefficient b, ExpVector e) {  
        OrderedPolynomial Cp = getZERO(table,order); 
        Cp.setVars(vars);
        if ( b.isZERO() ) { 
            return Cp;
        }
        Cp = Cp.add(b,e);
        return multiply(Cp);
    }


    /**
     * Left product with number and exponent vector.
     */

    public SolvablePolynomial multiplyLeft(Coefficient b, ExpVector e) {  
        SolvablePolynomial Cp = getZERO(table,order); 
        Cp.setVars(vars);
        if ( b.isZERO() ) { 
            return Cp;
        }
        Cp = (SolvablePolynomial)Cp.add(b,e);
        Cp = (SolvablePolynomial)Cp.multiply(this);
        return Cp;
    }


    /**
     * Left product with exponent vector.
     */

    public SolvablePolynomial multiplyLeft(ExpVector e) {  
        SolvablePolynomial Cp = getZERO(table,order); 
        Cp.setVars(vars);
        if ( e.isZERO() ) { 
            return this;
        }
        Coefficient b = leadingBaseCoefficient().ONE;
        Cp = (SolvablePolynomial)Cp.add(b,e);
        return (SolvablePolynomial)Cp.multiply(this);
    }


    /**
     * Product with 'monomial'.
     */

    public SolvablePolynomial multiplyLeft(Map.Entry m) {  
        if ( m == null ) return null;
        return multiplyLeft( (Coefficient)m.getValue(), (ExpVector)m.getKey() );
    }


}