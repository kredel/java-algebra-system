/*
 * $Id$
 */

package edu.jas.poly;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;

import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomialRing;

import edu.jas.module.ModuleList;


/**
 * Set / list of polynomials.
 * For storage and printing/toString and sorting.
 * @author Heinz Kredel
 */

public class PolynomialList<C extends RingElem<C> > implements Serializable {

    private static Logger logger = Logger.getLogger(PolynomialList.class);

    public final GenPolynomialRing< C > ring;

    public final List< GenPolynomial<C> > list;


    /**
     * Contstructor.
     */
    public PolynomialList( GenPolynomialRing< C > r,
                           List<GenPolynomial< C >> l) {
        ring = r;
        list = l; 
    }

    /**
     * Contstructor.
     */
    public PolynomialList( GenSolvablePolynomialRing< C > r,
                           List<GenSolvablePolynomial< C >> l) {
        this(r,castToList(l)); 
    }


    /**
     * equals from Object.
     */

    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object p) {
        if ( ! (p instanceof PolynomialList) ) {
            System.out.println("no PolynomialList");
            return false;
        }
        PolynomialList< C > pl = null;
        try {
            pl = (PolynomialList< C >)p;
        } catch (ClassCastException ignored) {
        }
        if ( pl == null ) {
           return false;
        }
        if ( ! ring.equals( pl.ring ) ) {
            System.out.println("not same Ring");
            return false;
        }
        if ( list == null && pl.list != null ) {
            return false;
        }
        if ( list != null && pl.list == null ) {
            return false;
        }
        if ( list.size() != pl.list.size() ) {
            return false;
        }
        // compare sorted lists
        List<GenPolynomial<C>> l1 = OrderedPolynomialList.sort( ring, list );
        List<GenPolynomial<C>> l2 = OrderedPolynomialList.sort( ring, pl.list );
        for ( int i = 0; i < list.size(); i++ ) {
            GenPolynomial<C> a = l1.get(i);
            GenPolynomial<C> b = l2.get(i);
            if ( ! a.equals( b ) ) {
               System.out.println("PolynomialList");
               System.out.println("a = " + a);
               System.out.println("b = " + b);
               return false;
            }
        }
        // otherwise tables may be different
        return true;
    }


    public String toString() {
	StringBuffer erg = new StringBuffer();
        if ( ring != null ) {
           erg.append( ring.toString() );
        }
        String[] vars = ring.getVars();
        boolean first = true;
        erg.append("\n(\n");
        String sa = null;
        for ( GenPolynomial<C> oa: list ) {
            if ( vars != null ) {
               sa = oa.toString(vars);
            } else {
               sa = oa.toString();
            }
            if ( first ) {
               first = false;
            } else {
               erg.append( ", " );
               if ( sa.length() > 10 ) {
                  erg.append("\n");
               }
            }
            erg.append( "( " + sa + " )" );
        }
        erg.append("\n)");
	return erg.toString();
    }


    /**
     * get ModuleList from PolynomialList.
     * Extract module from polynomial ring. 
     */

    public ModuleList<C> getModuleList(int i) {
        GenPolynomialRing< C > pfac = ring.contract(i);
        logger.debug("contracted ring = " + pfac);
        //System.out.println("contracted ring = " + pfac);

        List<List<GenPolynomial<C>>> vecs = null;
        if ( list == null ) { 
           return new ModuleList<C>(pfac,vecs);
        }
        int rows = list.size();
        vecs = new ArrayList<List<GenPolynomial<C>>>( rows );
        if ( rows == 0 ) { // nothing to do
           return new ModuleList<C>(pfac,vecs);
        }

        ArrayList<GenPolynomial<C>> zr 
             = new ArrayList<GenPolynomial<C>>( i-1 );
        GenPolynomial<C> zero = pfac.getZERO();
        for ( int j = 0; j < i; j++ ) {
            zr.add(j,zero);
        }

        for ( GenPolynomial<C> p: list ) {
            if ( p != null ) {
                Map<ExpVector,GenPolynomial<C>> r = null;
                r = p.contract( pfac );
                //System.out.println("r = " + r ); 
                List<GenPolynomial<C>> row 
                    = (ArrayList<GenPolynomial<C>>)zr.clone();
                for ( ExpVector e: r.keySet() ) {
                    int[] dov = e.dependencyOnVariables();
                    int ix = 0;
                    if ( dov.length > 1 ) {
                       throw new RuntimeException("wrong dependencyOnVariables " + e);
                    } else if ( dov.length == 1 )  {
                       ix = dov[0];
                    }
                    //ix = i-1 - ix; // revert
                    //System.out.println("ix = " + ix ); 
                    GenPolynomial<C> vi = r.get( e );
                    row.set(ix,vi);
                }
                //System.out.println("row = " + row ); 
                vecs.add( row );
            }
        }
        return new ModuleList<C>(pfac,vecs);
    }


    /**
     * Get list as List of GenSolvablePolynomials.
     * Required because no List casts allowed. Equivalent to 
     * cast (List<GenSolvablePolynomial<C>>) list.
     */
    public List< GenSolvablePolynomial<C> > castToSolvableList() {
        List< GenSolvablePolynomial<C> > slist = null;
        if ( list == null ) {
            return slist;
        }
        slist = new ArrayList< GenSolvablePolynomial<C> >( list.size() ); 
        for ( GenPolynomial<C> p: list ) {
            if ( ! (p instanceof GenSolvablePolynomial) ) {
               throw new RuntimeException("no solvable polynomial "+p);
            }
            GenSolvablePolynomial<C> s
               = (GenSolvablePolynomial<C>) p;
            slist.add( s );
        }
        return slist;
    }

    /**
     * Get solvable polynomial list as List of GenPolynomials.
     * Required because no List casts allowed. Equivalent to 
     * cast (List<GenPolynomial<C>>) list.
     */
    public static <C extends RingElem<C> > 
           List< GenPolynomial<C> > 
           castToList( List<GenSolvablePolynomial<C>> slist) {
        List< GenPolynomial<C> > list = null;
        if ( slist == null ) {
            return list;
        }
        list = new ArrayList< GenPolynomial<C> >( slist.size() ); 
        for ( GenSolvablePolynomial<C> p: slist ) {
            list.add( p );
        }
        return list;
    }

}
