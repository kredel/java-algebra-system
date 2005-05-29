
/*
 * $Id$
 */

package edu.jas.poly;

import java.math.BigInteger;
import java.io.Reader;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.TermOrder;
import edu.jas.poly.ExpVector;


/**
 * GenPolynomialRing generic polynomial factroy implementing RingFactory;
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

    public String toString() {
        return this.getClass().getSimpleName() 
                 + "[ " + coFac.toString()
                 + ", " + nvar
                 + ", " + tord.toString()
                 + " ]";
    }

    public String[] getVars() {
        return vars;
    }

    public String[] setVars(String[] v) {
        String[] t = vars;
        vars = v;
        return t;
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

    public GenPolynomial<C> fromInteger(long a) {
        return new GenPolynomial<C>( this, coFac.fromInteger(a), evzero );
    }

    public GenPolynomial<C> fromInteger(BigInteger a) {
        return new GenPolynomial<C>( this, coFac.fromInteger(a), evzero );
    }

    public GenPolynomial<C> random(int n /* other parms */) {
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
                rv.put( e, a );
            } else {
                b = b.add(a);
                if ( ! b.isZERO() ) {
                   rv.put( e, b );
                }
            }
        }
        return r;
    }

    public GenPolynomial<C> copy(GenPolynomial<C> c) {
        return new GenPolynomial<C>( this, c.val );
    }

    public GenPolynomial<C> parse(String s) {
        return ZERO;
    }

    public GenPolynomial<C> parse(Reader r) {
        return ZERO;
    }

}