
/*
 * $Id$
 */

package edu.jas.poly;

import java.math.BigInteger;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

//import java.util.List;
//import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
//import java.util.SortedMap;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.PrettyPrint;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.TermOrder;
import edu.jas.poly.ExpVector;


/**
 * GenPolynomialRing generic polynomial factory implementing RingFactory;
 * Factory for n-variate ordered polynomials over C.
 * Almost immutable object, except variable names.
 * @author Heinz Kredel
 */

public class GenPolynomialRing<C extends RingElem<C> > 
             implements RingFactory< GenPolynomial<C> > {


    /** The factory for the coefficients. 
     */
    public final RingFactory< C > coFac;


    /** The number of variables.
     */
    public final int nvar;


    /** The term order.
     */
    public final TermOrder tord;


    /** The names of the variables.
     * This value can be modified. 
     */
    protected String[] vars;


    /**
     * The constant polynomial 0 for this ring.
     */
    public final GenPolynomial<C> ZERO;


    /**
     * The constant polynomial 1 for this ring.
     */
    public final GenPolynomial<C> ONE;


    /**
     * The constant exponent vector 0 for this ring.
     */
    public final ExpVector evzero;


    /**
     * A default random sequence generator.
     */
    protected final static Random random = new Random(); 


    private static Logger logger = Logger.getLogger(GenPolynomialRing.class);


    /** The constructor creates a polynomial factory object
     * with the default term order.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     */
    public GenPolynomialRing(RingFactory< C > cf, int n) {
        this(cf,n,new TermOrder(),null);
    }


    /** The constructor creates a polynomial factory object.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param t a term order.
     */
    public GenPolynomialRing(RingFactory< C > cf, int n, TermOrder t) {
        this(cf,n,t,null);
    }


    /** The constructor creates a polynomial factory object.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param t a term order.
     * @param v names for the variables.
     */
    public GenPolynomialRing(RingFactory< C > cf, int n, TermOrder t, 
                             String[] v) {
        coFac = cf;
        nvar = n;
        tord = t;
        vars = v;
        ZERO = new GenPolynomial<C>( this );
        C coeff = coFac.getONE();
        evzero = new ExpVector(nvar);
        ONE  = new GenPolynomial<C>( this, coeff, evzero );
    }



    /** Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String res = "";
        if ( PrettyPrint.isTrue() ) {
           res = coFac.getClass().getSimpleName();
           if ( coFac instanceof AlgebraicNumber ) {
              AlgebraicNumber an = (AlgebraicNumber)coFac;  
              String[] v = an.modul.ring.vars;
              res +=  "[ ("
                     + an.modul.ring.varsToString()
                     + ") ("
                     + an.modul.toString( v )
                     + ") ]";
           }
           res +=   "(" 
                  + varsToString()
                  + ") " 
                  + tord.toString()
                  + " ";
        } else {
           res = this.getClass().getSimpleName() 
                 + "[ " // + coFac.toString() + " : "
                 + coFac.getClass().getSimpleName();
           if ( coFac instanceof AlgebraicNumber ) {
              AlgebraicNumber an = (AlgebraicNumber)coFac;  
              res +=  "[ ("
                     + an.modul.ring.varsToString()
                     + ") ("
                     + an.modul
                     + ") ]";
           }
           res +=  ", " + nvar
                  + ", " + tord.toString()
                  + ", " + varsToString()
                  + " ]";
        }
        return res;
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals( Object other ) { 
        if ( ! (other instanceof GenPolynomialRing) ) {
            return false;
        }
        GenPolynomialRing<C> oring = null;
        try {
            oring = (GenPolynomialRing<C>)other;
        } catch (ClassCastException ignored) {
        }
        if ( oring == null ) {
            return false;
        }
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


    /** Get the variable names. 
     * @return vars.
     */
    public String[] getVars() {
        return vars;
    }


    /** Set the variable names. 
     * @return old vars.
     */
    public String[] setVars(String[] v) {
        String[] t = vars;
        vars = v;
        return t;
    }


    /** Get a String representation of the variable names. 
     * @return names seperated by commas.
     */
    public String varsToString() {
        String s = "";
        if ( vars == null ) {
            return s;
        }
        for ( int i = 0; i < vars.length; i++ ) {
            if ( i != 0 ) {
               s += ", ";
            }
            s += vars[i];
        }
        return s;
    }


    /** Get the zero element from the coefficients.
     * @return 0 as C.
     */
    public C getZEROCoefficient() {
        return coFac.getZERO();
    }


    /** Get the one element from the coefficients.
     * @return 1 as C.
     */
    public C getONECoefficient() {
        return coFac.getONE();
    }


    /** Get the zero element.
     * @return 0 as GenPolynomial<C>.
     */
    public GenPolynomial<C> getZERO() {
        return ZERO;
    }


    /** Get the one element.
     * @return 1 as GenPolynomial<C>.
     */
    public GenPolynomial<C> getONE() {
        return ONE;
    }


    /** Get a (constant) GenPolynomial<C> element from a long value.
     * @param a long.
     * @return a GenPolynomial<C>.
     */
    public GenPolynomial<C> fromInteger(long a) {
        return new GenPolynomial<C>( this, coFac.fromInteger(a), evzero );
    }


    /** Get a (constant) GenPolynomial<C> element from a BigInteger value.
     * @param a BigInteger.
     * @return a GenPolynomial<C>.
     */
    public GenPolynomial<C> fromInteger(BigInteger a) {
        return new GenPolynomial<C>( this, coFac.fromInteger(a), evzero );
    }


    /**
     * Random polynomial.
     * Generates a random polynomial with
     * k = 5, 
     * l = n, 
     * d = (nvar == 1) ?   n : 3,
     * q = (nvar == 1) ? 0.7 : 0.3. 
     * @param n number of terms.
     * @return a random polynomial.
     */
    public GenPolynomial<C> random(int n) {
        return random(n,random);
    }


    /**
     * Random polynomial.
     * Generates a random polynomial with
     * k = 5, 
     * l = n, 
     * d = (nvar == 1) ?   n : 3,
     * q = (nvar == 1) ? 0.7 : 0.3.
     * @param n number of terms.
     * @param rnd is a source for random bits.
     * @return a random polynomial.
     */
    public GenPolynomial<C> random(int n, Random rnd) {
        if ( nvar == 1 ) {
            return random(5,n,n,0.7f,rnd);
        } else {
            return random(5,n,3,0.3f,rnd);
        }
    }


    /**
     * Generate a random polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @return a random polynomial.
     */
    public GenPolynomial<C> random(int k, int l, int d, float q) {
        return random(k,l,d,q,random);
    }


    /**
     * Generate a random polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @param rnd is a source for random bits.
     * @return a random polynomial.
     */
    public GenPolynomial<C> random(int k, int l, int d, float q, 
                                   Random rnd) {
        GenPolynomial<C> r = getZERO(); //.clone() or copy( ZERO ); 
        ExpVector e;
        C a;
        // add l random coeffs and exponents
        for ( int i = 0; i < l; i++ ) {
            e = ExpVector.EVRAND(nvar, d, q, rnd);
            a = coFac.random(k,rnd);
            r = r.add(a,e); // somewhat inefficient but clean
            //System.out.println("e = " + e + " a = " + a);
        }
        // System.out.println("r = " + r);
        return r;
    }


    /**
     * Copy polynomial c.
     * @param c
     * @return a copy of c.
     */
    public GenPolynomial<C> copy(GenPolynomial<C> c) {
        System.out.println("GP copy = " + this);
        return new GenPolynomial<C>( this, c.val );
    }


    /**
     * Parse a polynomial with the use of GenPolynomialTokenizer.
     * @param s String.
     * @return GenPolynomial from s.
     */
    public GenPolynomial<C> parse(String s) {
        return parse( new StringReader(s) );
    }


    /**
     * Parse a polynomial with the use of GenPolynomialTokenizer.
     * @param r Reader.
     * @return next GenPolynomial from r.
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
     * @param i number of variables to extend.
     * @return extended polynomial ring factory.
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
        TermOrder to = tord.extend(nvar,i);
        GenPolynomialRing<C> pfac 
            = new GenPolynomialRing<C>(coFac,nvar+i,to,v);
        return pfac;
    }


    /**
     * Contract variables. Used e.g. in module embedding.
     * Contract number of variables by i.
     * @param i number of variables to remove.
     * @return contracted polynomial ring factory.
     */
    public GenPolynomialRing<C> contract(int i) {
        String[] v = null;
        if ( vars != null ) {
           v = new String[ vars.length - i ];
           for ( int j = 0; j < vars.length-i; j++ ) {
               v[j] = vars[j];
           }
        }
        TermOrder to = tord.contract(i,nvar-i);
        GenPolynomialRing<C> pfac 
            = new GenPolynomialRing<C>(coFac,nvar-i,to,v);
        return pfac;
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @return polynomial ring factory with reversed variables.
     */
    public GenPolynomialRing<C> reverse() {
        String[] v = null;
        if ( vars != null ) {
           v = new String[ vars.length ];
           for ( int j = 0; j < vars.length; j++ ) {
               v[j] = vars[ vars.length - 1 - j ];
           }
        }
        TermOrder to = tord.reverse();
        GenPolynomialRing<C> pfac 
            = new GenPolynomialRing<C>(coFac,nvar,to,v);
        return pfac;
    }

}