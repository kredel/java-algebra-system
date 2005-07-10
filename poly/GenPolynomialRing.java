
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

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.TermOrder;
import edu.jas.poly.ExpVector;


/**
 * GenPolynomialRing generic polynomial factory implementing RingFactory;
 * Factory for n-variate ordered polynomials over C.
 * @author Heinz Kredel
 */

public class GenPolynomialRing<C extends RingElem<C> > 
             implements RingFactory< GenPolynomial<C> > {

    public final RingFactory< C > coFac;
    public final int nvar;
    public final TermOrder tord;

    protected String[] vars;

    public final GenPolynomial<C> ZERO;
    public final GenPolynomial<C> ONE;

    public final ExpVector evzero;

    private final static Random random = new Random(); 

    private static Logger logger = Logger.getLogger(GenPolynomialRing.class);


    /**
     * Constructors.
     */

    public GenPolynomialRing(RingFactory< C > cf, int n) {
        this(cf,n,new TermOrder(),null);
    }

    public GenPolynomialRing(RingFactory< C > cf, int n, TermOrder t) {
        this(cf,n,t,null);
    }

    public GenPolynomialRing(RingFactory< C > cf, int n, TermOrder t, String[] v) {
        coFac = cf;
        nvar = n;
        tord = t;
        vars = v;
        ZERO = new GenPolynomial<C>( this );
        C coeff = coFac.getONE();
        evzero = new ExpVector(nvar);
        ONE  = new GenPolynomial<C>( this, coeff, evzero );
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() 
                 + "[ " // + coFac.toString() + " : "
                 + coFac.getClass().getSimpleName()
                 + ", " + nvar
                 + ", " + tord.toString()
                 + ", " + varsToString()
                 + " ]";
    }


    @Override
    public boolean equals( Object other ) { 
        if ( ! (other instanceof GenPolynomialRing) ) {
            return false;
        }
        GenPolynomialRing oring = (GenPolynomialRing)other;
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
        return true;
    }


    public String[] getVars() {
        return vars;
    }

    public String[] setVars(String[] v) {
        String[] t = vars;
        vars = v;
        return t;
    }

    public String varsToString() {
        String s = "";
        if ( vars == null ) {
            return s;
        }
        for ( int i = 0; i < vars.length; i++ ) {
            s += vars[i] + " ";
        }
        return s;
    }

    public C getZEROCoefficient() {
        return coFac.getZERO();
    }

    public C getONECoefficient() {
        return coFac.getONE();
    }


    public GenPolynomial<C> getZERO() {
        return ZERO;
    }

    public GenPolynomial<C> getONE() {
        return ONE;
    }


    /**
     * construct a polynomial from a long.
     */
    public GenPolynomial<C> fromInteger(long a) {
        return new GenPolynomial<C>( this, coFac.fromInteger(a), evzero );
    }

    /**
     * construct a polynomial from a BigInteger.
     */
    public GenPolynomial<C> fromInteger(BigInteger a) {
        return new GenPolynomial<C>( this, coFac.fromInteger(a), evzero );
    }


    /**
     * Random polynomial.
     */
    public GenPolynomial<C> random(int n) {
        if ( nvar == 1 ) {
           return random(5,n,n,0.7f);
        } else {
           return random(5,n,3,0.3f);
        }
    }

    /**
     * Random polynomial.
     * @param k size of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     */
    public GenPolynomial<C> random(int k, int l, int d, float q) {
        GenPolynomial<C> r = getZERO(); //.clone();
                 // copy( ZERO ); 
                 // new GenPolynomial<C>( this, getZERO().val );
        ExpVector e;
        C a;
        // add random coeffs and exponents
        for ( int i = 0; i < l; i++ ) {
            e = ExpVector.EVRAND(nvar, d, q);
            a = coFac.random(k);
            r = r.add(a,e); // somewhat inefficient but clean
            //System.out.println("e = " + e + " a = " + a);
        }
        // System.out.println("r = " + r);
        return r;
    }

    public GenPolynomial<C> NOrandom(int n /* other parms */) {
        GenPolynomial<C> r = getZERO().clone();
                 // copy( ZERO ); 
                 // new GenPolynomial<C>( this, getZERO().val );
        SortedMap<ExpVector,C> rv = r.val;
        ExpVector e;
        C a;
        C b;
        // add random coeffs and exponents
        for ( int i = 0; i < n; i++ ) {
            e = ExpVector.EVRAND(nvar, 3, 0.3f);
            a = coFac.random(5);
            b = rv.get( e );
            if ( b == null ) {
                if ( ! a.isZERO() ) {
                   rv.put( e, a );
                }
            } else {
                b = b.add(a);
                if ( ! b.isZERO() ) {
                   rv.put( e, b );
                }
            }
        }
        return r;
    }


    /**
     * copy this polynomial.
     */
    public GenPolynomial<C> copy(GenPolynomial<C> c) {
        System.out.println("GP copy = " + this);
        return new GenPolynomial<C>( this, c.val );
    }


    /**
     * Parse a polynomial with the use of GenPolynomialTokenizer
     */
    public GenPolynomial<C> parse(String s) {
        return parse( new StringReader(s) );
    }


    /**
     * Parse a polynomial with the use of GenPolynomialTokenizer
     */
    public GenPolynomial<C> parse(Reader r) {
        GenPolynomialTokenizer pt = new GenPolynomialTokenizer(this,r);
        GenPolynomial<C> p = null;
        try {
            p = pt.nextPolynomial();
        } catch (IOException e) {
            logger.error(e.toString()+" parse " + this);
            p = ZERO;
        }
        return p;
    }


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend number of variables by i.
     */
    public GenPolynomialRing<C> extend(int i) {
        // add module variable names
        String[] v = null;
        if ( vars != null ) {
           v = new String[ vars.length + i ];
           for ( int k = 0; k < vars.length; k++ ) {
               v[k] = vars[k];
           }
           for ( int k = 0; k < i; k++ ) {
               v[ vars.length + k ] = "e" + (k+1);
           }
        }
        GenPolynomialRing<C> pfac 
            = new GenPolynomialRing<C>(coFac,nvar+i,tord,v);
        return pfac;
    }


    /**
     * Contract variables. Used e.g. in module embedding.
     * remove i variables.
     */
    public GenPolynomialRing<C> contract(int i) {
        String[] v = null;
        if ( vars != null ) {
           v = new String[ vars.length - i ];
           for ( int j = 0; j < vars.length-i; j++ ) {
               v[j] = vars[j];
           }
        }
        GenPolynomialRing<C> pfac 
            = new GenPolynomialRing<C>(coFac,nvar-i,tord,v);
        return pfac;
    }

}