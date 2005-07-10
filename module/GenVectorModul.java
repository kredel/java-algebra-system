/*
 * $Id$
 */

package edu.jas.module;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.ModulElem;
import edu.jas.structure.ModulFactory;

import edu.jas.poly.GenPolynomial;


/**
 * GenVectorModul generic vector implementing ModulElem.
 * vectors of n columns over C.
 * @author Heinz Kredel
 */

public class GenVectorModul<C extends RingElem<C> > 
            implements ModulFactory< GenVector<C>, C > {

    public final RingFactory< C > coFac;
    public final int cols;

    public final GenVector<C> ZERO;

    private final static Random random = new Random(); 

    private static Logger logger = Logger.getLogger(GenVectorModul.class);


    /**
     * Constructors for GenVectorModul
     */

    public GenVectorModul(RingFactory< C > b, int s) {
        coFac = b;
        cols = s;
        List<C> z = new ArrayList( cols ); 
        for ( int i = 0; i < cols; i++ ) {
            z.add( coFac.getZERO() );
        }
        ZERO = new GenVector<C>( this, z );
    }


    /**
     * toString method.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append( coFac.getClass().getSimpleName() );
        s.append("[" + cols + "]");
        return s.toString();
    }


    public GenVector<C> getZERO() {
        return ZERO;
    }


    @Override
    public boolean equals( Object other ) { 
        if ( ! (other instanceof GenVectorModul) ) {
            return false;
        }
        GenVectorModul omod = (GenVectorModul)other;
        if ( cols != omod.cols ) {
            return false;
        }
        if ( ! coFac.equals(omod.coFac) ) {
            return false;
        }
        return true;
    }


    /**
     * From List of coefficients.
     * @param v list of coefficients.
     */
    public GenVector<C> fromList(List<C> v) {
        if ( v == null ) {
            return ZERO;
        }
        if ( v.size() > cols ) {
           throw new RuntimeException("size v > cols " + cols + " < " + v);
        }
        List<C> r = new ArrayList<C>( cols ); 
        r.addAll( v );
        // pad with zeros if required:
        for ( int i = r.size(); i < cols; i++ ) {
            r.add( coFac.getZERO() );
        }
        return new GenVector<C>( this, r );
    }


    /**
     * Random vector.
     * @param k size of random coefficients.
     * @param q density of nozero coefficients.
     */
    public GenVector<C> random(int k, float q) {
        List<C> r = new ArrayList<C>( cols ); 
        for ( int i = 0; i < cols; i++ ) {
            if ( random.nextFloat() < q ) {
                r.add( coFac.random(k) );
            } else {
                r.add( coFac.getZERO() );
            }
        }
        return new GenVector<C>( this, r );
    }


    /**
     * copy vector.
     */
    public GenVector<C> copy(GenVector<C> c) {
        if ( c == null ) {
           return c;
        } else {
           return c.clone();
        }
        //return new GenVector<C>( this, c.val );//clone val
    }


    /**
     * parse a vector from a String.
     */
    public GenVector<C> parse(String s) {
        throw new RuntimeException("parse not jet implemented");
        //return ZERO;
    }


    /**
     * parse a vector from a Reader.
     */
    public GenVector<C> parse(Reader r) {
        throw new RuntimeException("parse not jet implemented");
        //return ZERO;
    }


}