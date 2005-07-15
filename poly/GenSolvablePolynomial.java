/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Set;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
//import java.util.Iterator;
//import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Generic Solvable Polynomial. 
 * Implementation based on Sorted Map / TreeMap
 * @author Heinz Kredel
 */

public class GenSolvablePolynomial<C extends RingElem<C>> 
             extends GenPolynomial<C> {
    //       implements RingElem< GenSolvablePolynomial<C> > {

    public final GenSolvablePolynomialRing< C > ring;


    private static Logger logger = Logger.getLogger(GenSolvablePolynomial.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructors for GenSolvablePolynomial
     */

    public GenSolvablePolynomial(GenSolvablePolynomialRing< C > r) {
        super(r);
        ring = r;
    }

    public GenSolvablePolynomial(GenSolvablePolynomialRing< C > r, 
                                 SortedMap<ExpVector,C> v) {
        this(r);
        val.putAll( v ); // assume no zero coefficients
    }

    public GenSolvablePolynomial(GenSolvablePolynomialRing< C > r, 
                                 C c, ExpVector e) {
        this(r);
        if ( ! c.isZERO() ) {
           val.put(e,c);
        }
    }

    /**
     * Clone this GenSolvablePolynomial
     */
    public GenSolvablePolynomial<C> clone() {
        //return ring.copy(this);
        return new GenSolvablePolynomial<C>(ring,this.val);
    }


    /*
    @inherit
    public String toString() { 
    }
    */


    /**
     * Multiply. Implementation using map.put on result polynomial.
     */

     public GenSolvablePolynomial<C> multiply(GenSolvablePolynomial<C> Bp) {  
        if ( Bp == null || Bp.isZERO() ) {
           return ring.getZERO();
        }
        if ( this.isZERO() ) {
           return this;
        }
        logger.debug("ring = " + ring);
        ExpVector Z = ring.evzero;
        GenSolvablePolynomial<C> Cp = ring.getZERO().clone(); 
        GenSolvablePolynomial<C> zero = ring.getZERO().clone();
        C one = ring.getONECoefficient();

        GenSolvablePolynomial<C> C1 = null;
        GenSolvablePolynomial<C> C2 = null;
        //Map C = Cp.getMap();
        Map<ExpVector,C> A = val; //this.getMap();
        Map<ExpVector,C> B = Bp.getMap();
        Set<Map.Entry<ExpVector,C>> Bk = B.entrySet();
        for ( Map.Entry<ExpVector,C> y:  A.entrySet() ) {
            C a = y.getValue(); 
            //logger.debug("a = " + a);
	    ExpVector e = y.getKey(); 
            if ( debug ) logger.debug("e = " + e);
            int[] ep = e.dependencyOnVariables();
            int el1 = ring.nvar + 1;
            if ( ep.length > 0 ) {
                el1 = ep[0];
            }
            int el1s = ring.nvar + 1 - el1; 
            for ( Map.Entry<ExpVector,C> x: Bk ) {
                C b = x.getValue(); 
                //logger.debug("b = " + b);
	        ExpVector f = x.getKey(); 
                if ( debug ) logger.debug("f = " + f);
                int[] fp = f.dependencyOnVariables();
                int fl1 = 0; 
                if ( fp.length > 0 ) {
                   fl1 = fp[fp.length-1];
                }
                int fl1s = ring.nvar + 1 - fl1; 
                if ( debug ) logger.debug("el1s = " + el1s + " fl1s = " + fl1s);
                GenSolvablePolynomial<C> Cs = null;
                if ( el1s <= fl1s ) { // symmetric
	            ExpVector g = ExpVector.EVSUM(e,f); 
                    if ( debug ) logger.debug("g = " + g);
                    Cs = (GenSolvablePolynomial<C>)zero.add( one, g ); // symmetric!
                    //Cs = new GenSolvablePolynomial<C>(ring,one,g); // symmetric!
                } else { // unsymmetric
                    // split e = e1 * e2, f = f1 * f2
                    ExpVector e1 = e.subst(el1,0);
                    ExpVector e2 = Z.subst(el1,e.getVal(el1));
                    ExpVector e4;
                    ExpVector f1 = f.subst(fl1,0);
                    ExpVector f2 = Z.subst(fl1,f.getVal(fl1));
                    if ( debug ) logger.debug("e1 = " + e1 + " e2 = " + e2);
                    if ( debug ) logger.debug("f1 = " + f1 + " f2 = " + f2);
                    TableRelation<C> rel = ring.table.lookup(e2,f2); 
                    //logger.info("relation = " + rel);
                    Cs = rel.p; //ring.copy( rel.p ); // do not clone() 
                    if ( rel.f != null ) {
                       C2 = (GenSolvablePolynomial<C>)zero.add( one, rel.f );
                       Cs = Cs.multiply( C2 );
                       if ( rel.e == null ) {
                          e4 = e2;
                       } else {
                          e4 = e2.dif( rel.e );
                       }
                       ring.table.update(e4,f2,Cs);
                    }
                    if ( rel.e != null ) {
                       C1 = (GenSolvablePolynomial<C>)zero.add( one, rel.e );
                       Cs = C1.multiply( Cs );
                       ring.table.update(e2,f2,Cs);
                    }
                    if ( !f1.isZERO() ) {
                       C2 = (GenSolvablePolynomial<C>)zero.add( one, f1 );
                       Cs = Cs.multiply( C2 ); 
                       //ring.table.update(?,f1,Cs)
                    }
                    if ( !e1.isZERO() ) {
                       C1 = (GenSolvablePolynomial<C>)zero.add( one, e1 );
                       Cs = C1.multiply( Cs ); 
                       //ring.table.update(e1,?,Cs)
                    }
                }
                C c = a.multiply(b);
                //logger.debug("c = " + c);
                Cs = Cs.multiply( c ); // symmetric!
                if ( debug ) logger.debug("Cs = " + Cs);
                Cp = (GenSolvablePolynomial<C>)Cp.add( Cs );
            }
        }
        return Cp;
    }



    /**
     * Product with number.
     */
    public GenSolvablePolynomial<C> multiply(C b) {  
        GenSolvablePolynomial<C> Cp = ring.getZERO().clone(); 
        if ( b.isZERO() ) { 
            return Cp;
        }
        Map<ExpVector,C> Cm = Cp.getMap();
        Map<ExpVector,C> Am = val; 
        for ( Map.Entry<ExpVector,C> y: Am.entrySet() ) { 
            ExpVector e = y.getKey(); 
            //System.out.println("e = " + e);
            C a = y.getValue(); 
            //System.out.println("a = " + a);
            C c = a.multiply(b);
            //System.out.println("c = " + c);
            Cm.put( e, c );
        }
        return Cp;
    }

    /**
     * Product with exponent vector.
     */
    public GenSolvablePolynomial<C> multiply(ExpVector e) {  
        GenSolvablePolynomial<C> Cp = ring.getZERO().clone(); 
        if ( e.isZERO() ) { 
            return this;
        }
        C b = ring.getONECoefficient();
        Cp = new GenSolvablePolynomial<C>(ring,b,e);
        return multiply(Cp);
    }

    /**
     * Product with number and exponent vector.
     */
    public GenSolvablePolynomial<C> multiply(C b, ExpVector e) {  
        GenSolvablePolynomial<C> Cp = ring.getZERO().clone(); 
        if ( b.isZERO() ) { 
            return Cp;
        }
        //Cp = (GenSolvablePolynomial<C>)Cp.add(b,e);
        Cp = new GenSolvablePolynomial<C>(ring,b,e);
        return multiply(Cp);
    }


    /**
     * Left product with number and exponent vector.
     */
    public GenSolvablePolynomial<C> multiplyLeft(C b, ExpVector e) {  
        GenSolvablePolynomial<C> Cp = ring.getZERO().clone(); 
        if ( b.isZERO() ) { 
            return Cp;
        }
        Cp = new GenSolvablePolynomial<C>(ring,b,e);
        Cp = Cp.multiply(this);
        return Cp;
    }

    /**
     * Left product with exponent vector.
     */
    public GenSolvablePolynomial<C> multiplyLeft(ExpVector e) {  
        GenSolvablePolynomial<C> Cp = ring.getZERO().clone(); 
        if ( e.isZERO() ) { 
            return this;
        }
        C b = ring.getONECoefficient();
        Cp = new GenSolvablePolynomial<C>(ring,b,e);
        return Cp.multiply(this);
    }


    /**
     * Product with 'monomial'.
     */
    public GenSolvablePolynomial<C> multiplyLeft(Map.Entry<ExpVector,C> m) {  
        if ( m == null ) {
           return ring.getZERO();
        }
        return multiplyLeft( m.getValue(), m.getKey() );
    }


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend all ExpVectors by i elements and multiply by x_j^k.
     */

    /**
     * Contract variables. Used e.g. in module embedding.
     * remove i elements of each ExpVector.
     */

}