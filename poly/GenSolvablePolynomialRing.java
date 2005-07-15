
/*
 * $Id$
 */

package edu.jas.poly;

import java.math.BigInteger;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.PrettyPrint;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.TermOrder;
import edu.jas.poly.ExpVector;


/**
 * GenSolvablePolynomialRing generic polynomial factory implementing RingFactory;
 * Factory for n-variate ordered polynomials over C.
 * @author Heinz Kredel
 */

public class GenSolvablePolynomialRing<C extends RingElem<C> > 
             extends GenPolynomialRing<C> {

    public final RelationTable<C> table;

    // hide super ZERO ONE
    public final GenSolvablePolynomial<C> ZERO;
    public final GenSolvablePolynomial<C> ONE;

    //private final static Random random = new Random(); 

    private static Logger logger = Logger.getLogger(GenSolvablePolynomialRing.class);


    /**
     * Constructors.
     */

    public GenSolvablePolynomialRing(RingFactory< C > cf, int n) {
        this(cf,n,new TermOrder(),null,null);
    }

    public GenSolvablePolynomialRing(RingFactory< C > cf, int n, 
                                     RelationTable<C> rt) {
        this(cf,n,new TermOrder(),null,rt);
    }

    public GenSolvablePolynomialRing(RingFactory< C > cf, int n, TermOrder t) {
        this(cf,n,t,null,null);
    }

    public GenSolvablePolynomialRing(RingFactory< C > cf, int n, TermOrder t, 
                                     RelationTable<C> rt) {
        this(cf,n,t,null,rt);
    }

    public GenSolvablePolynomialRing(RingFactory< C > cf, int n, TermOrder t, 
                                     String[] vars) {
        this(cf,n,t,vars,null);
    }

    public GenSolvablePolynomialRing(RingFactory< C > cf, int n, TermOrder t, 
                                     String[] v, RelationTable<C> rt) {
        super(cf,n,t,v);
        if ( rt == null ) {
           table = new RelationTable<C>(this);
        } else {
           table = rt;
        }
        ZERO = new GenSolvablePolynomial<C>( this );
        C coeff = coFac.getONE();
        //evzero = new ExpVector(nvar);
        ONE  = new GenSolvablePolynomial<C>( this, coeff, evzero );
    }


    /**
     * Methods of GenSolvablePolynomialRing
     */

    @Override
    public String toString() {
        String res = "";
        if ( PrettyPrint.isTrue() ) {
           res = coFac.getClass().getSimpleName()
                 + "(" 
                 + varsToString()
                 + ") " 
                 + tord.toString()
                 + "\n"
                 + table.toString(vars);
        } else {
           res = this.getClass().getSimpleName() 
                 + "[ " // + coFac.toString() + " : "
                 + coFac.getClass().getSimpleName()
                 + ", " + nvar
                 + ", " + tord.toString()
                 + ", " + varsToString()
                 + ", #rel = " + table.size()
                 + " ]";
        }
        return res;
    }


    @Override
    public boolean equals( Object other ) { 
        if ( ! (other instanceof GenSolvablePolynomialRing) ) {
            return false;
        }
        GenSolvablePolynomialRing oring = (GenSolvablePolynomialRing)other;
        if ( nvar != oring.nvar ) {
            return false;
        }
        if ( ! coFac.equals(oring.coFac) ) {
            return false;
        }
        if ( ! tord.equals(oring.tord) ) {
            return false;
        }
        // same variables required ?
        if ( ! Arrays.equals(vars,oring.vars) ) {
            return false;
        }
        // check same base relations
        //if ( ! table.equals(oring.table) ) {
        //    return false;
        //}
        return true;
    }


    public GenSolvablePolynomial<C> getZERO() {
        return ZERO;
    }

    public GenSolvablePolynomial<C> getONE() {
        return ONE;
    }


    /**
     * construct a polynomial from a long.
     */
    public GenSolvablePolynomial<C> fromInteger(long a) {
        return new GenSolvablePolynomial<C>( this, coFac.fromInteger(a), 
                                                   evzero );
    }

    /**
     * construct a polynomial from a BigInteger.
     */
    public GenSolvablePolynomial<C> fromInteger(BigInteger a) {
        return new GenSolvablePolynomial<C>( this, coFac.fromInteger(a), 
                                                   evzero );
    }


    /**
     * Random polynomial.
     */
    public GenSolvablePolynomial<C> random(int n) {
        return random(5,n,3,0.3f);
    }

    /**
     * Random polynomial.
     * @param k size of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     */
    public GenSolvablePolynomial<C> random(int k, int l, int d, float q) {
        GenSolvablePolynomial<C> r = getZERO(); //.clone();
                 // copy( ZERO ); 
                 // new GenPolynomial<C>( this, getZERO().val );
        ExpVector e;
        C a;
        // add random coeffs and exponents
        for ( int i = 0; i < l; i++ ) {
            e = ExpVector.EVRAND(nvar, d, q);
            a = coFac.random(k);
            r = (GenSolvablePolynomial<C>)r.add(a,e); 
                // somewhat inefficient but clean
        }
        // System.out.println("r = " + r);
        return r;
    }


    /**
     * copy this polynomial.
     */
    public GenSolvablePolynomial<C> copy(GenSolvablePolynomial<C> c) {
        //System.out.println("GSP copy = " + this);
        return new GenSolvablePolynomial<C>( this, c.val );
    }


    /**
     * Parse a polynomial with the use of GenPolynomialTokenizer
     */
    public GenSolvablePolynomial<C> parse(String s) {
        //return getZERO();
        return parse( new StringReader(s) );
    }


    /**
     * Parse a polynomial with the use of GenPolynomialTokenizer
     */
    public GenSolvablePolynomial<C> parse(Reader r) {
        GenPolynomialTokenizer pt = new GenPolynomialTokenizer(this,r);
        GenSolvablePolynomial<C> p = null;
        try {
            p = pt.nextSolvablePolynomial();
        } catch (IOException e) {
            logger.error(e.toString()+" parse " + this);
            p = ZERO;
        }
        return p;
        /*
        return getZERO();
        */
    }


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend number of variables by i.
     */
    public GenSolvablePolynomialRing<C> extend(int i) {
        GenPolynomialRing<C> pfac = super.extend(i);

        GenSolvablePolynomialRing<C> spfac 
            = new GenSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                                               pfac.tord, pfac.vars);
        spfac.table.extend(this.table);
        return spfac;
    }


    /**
     * Contract variables. Used e.g. in module embedding.
     * remove i variables.
     */
    public GenSolvablePolynomialRing<C> contract(int i) {
        GenPolynomialRing<C> pfac = super.contract(i);

        GenSolvablePolynomialRing<C> spfac 
            = new GenSolvablePolynomialRing<C>(pfac.coFac, pfac.nvar,
                                               pfac.tord, pfac.vars);
        spfac.table.contract(this.table);
        return spfac;
    }


}