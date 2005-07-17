/*
 * $Id$
 */

package edu.jas.module;

import java.util.List;
import java.util.ArrayList;

import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.jas.structure.RingFactory;
import edu.jas.structure.RingElem;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;


/**
 * list of vectors of polynomials
 * mainly for storage and printing/toString and conversion
 * @author Heinz Kredel
 */

public class ModuleList<C extends RingElem<C> > implements Serializable {

    private static Logger logger = Logger.getLogger(ModuleList.class);

    public final GenPolynomialRing< C > ring;

    public final List< List< GenPolynomial<C> > > list;

    public final int rows; // -1 is undefined
    public final int cols; // -1 is undefined


    /**
     * Contstructor.
     */
    public ModuleList( GenPolynomialRing< C > r,
                       List< List<GenPolynomial< C >>> l) {
        ring = r;
	list = padCols(r,l); 
        if ( list == null ) {
            rows = -1; 
            cols = -1;
        } else {
            rows = list.size();
            if ( rows > 0 ) {
                cols = list.get(0).size();
            } else {
                cols = -1;
            }
        }
    }

    /**
     * Contstructor.
     */
    public ModuleList( GenSolvablePolynomialRing< C > r,
                       List< List<GenSolvablePolynomial< C >>> l) {
        this(r,castToList(l));
    }


    /**
     * equals from Object.
     */

    public boolean equals(Object m) {
        if ( ! (m instanceof ModuleList) ) {
            //System.out.println("ModuleList");
            return false;
        }
        ModuleList<C> ml = null;
        try {
            ml = (ModuleList<C>)m;
        } catch (ClassCastException ignored) {
        }
        if ( ml == null ) { 
            return false;
        }
        if ( ! ring.equals( ml.ring ) ) {
            //System.out.println("Ring");
            return false;
        }
        if ( list == null && ml.list != null ) {
            //System.out.println("List, null");
            return false;
        }
        if ( list != null && ml.list == null ) {
            //System.out.println("List, null");
            return false;
        }
        if ( list.size() != ml.list.size() ) {
            //System.out.println("List, size");
            return false;
        }
        // compare sorted lists
        List otl = OrderedModuleList.sort( ring, list );
        List oml = OrderedModuleList.sort( ring, ml.list );
        if ( ! otl.equals(oml) ) {
            return false;
        }
        return true;
    }


    @Override
    @SuppressWarnings("unchecked") // not jet working
    public String toString() {
	StringBuffer erg = new StringBuffer();
        String[] vars = null;
        if ( ring != null ) {
           erg.append( ring.toString() );
           vars = ring.getVars();
        }
        boolean first = true;
        erg.append("(\n");
        for ( List< GenPolynomial<C> > row: list ) {
            if ( first ) {
               first = false;
            } else {
               erg.append( ",\n" );
            }
            boolean ifirst = true;
            erg.append(" ( ");
            String os;
            for ( GenPolynomial<C> oa: row ) {
                if ( vars != null ) {
                   os = oa.toString(vars);
                } else {
                   os = oa.toString();
                }
                if ( ifirst ) {
                   ifirst = false;
                } else {
                   erg.append( ", " );
                   if ( os.length() > 100 ) {
                      erg.append("\n");
                   }
                }
                erg.append( os );
            }
            erg.append(" )");
        }
        erg.append("\n)");
	return erg.toString();
    }


    /**
     * pad Columns and remove zero rows.
     * make all rows have the same number of columns.
     */
    public static <C extends RingElem<C> >
                      List< List<GenPolynomial< C >>> 
                      padCols(GenPolynomialRing< C > ring,
                              List< List<GenPolynomial< C >>> l) {
        if ( l == null ) {
           return l;
        }
        int mcols = 0;
        int rs = 0;
        for ( List< GenPolynomial<C> > row: l ) {
            if ( row != null ) {
               rs++;
               if ( row.size() > mcols ) {
                  mcols = row.size();
               }
            }
        }
        List< List<GenPolynomial<C>> > norm 
            = new ArrayList< List<GenPolynomial<C>> >( rs );
        for ( List< GenPolynomial<C> > row: l ) {
            if ( row != null ) {
                List<GenPolynomial<C>> rn 
                    = new ArrayList<GenPolynomial<C>>( row );
                while ( rn.size() < mcols ) {
                    rn.add( ring.getZERO() );
                }
                norm.add( rn );
            }
        }
        return norm;
    }



    /**
     * get PolynomialList.
     * Embed module in a polynomial ring. 
     */
    public PolynomialList<C> getPolynomialList() {
        GenPolynomialRing< C > pfac = ring.extend(cols);
        logger.debug("extended ring = " + pfac);
        //System.out.println("extended ring = " + pfac);

        /*
        RelationTable extab = null;
        if ( table != null ) {
           extab = table.extend(cols,v);
           zero = ((SolvableOrderedMapPolynomial)zero).getZERO( extab );
           //logger.info("zero = " + zero);
        }
        */

        List<GenPolynomial<C>> pols = null;
        if ( list == null ) { // rows < 0
           return new PolynomialList<C>(pfac,pols);
        }
        pols = new ArrayList<GenPolynomial<C>>( rows );
        if ( rows == 0 ) { // nothing to do
           return new PolynomialList<C>(pfac,pols);
        }

        GenPolynomial<C> zero = pfac.getZERO();
        GenPolynomial<C> d = null;
        for ( List<GenPolynomial<C>> r: list ) {
            GenPolynomial<C> ext = zero;
            //int m = cols-1;
            int m = 0;
            for ( GenPolynomial<C> c: r ) {
                d = c.extend( pfac, m, 1l );
                ext = ext.add(d); 
                m++;
            }
            pols.add( ext );
        }
        return new PolynomialList<C>(pfac, pols);
    }


    /**
     * Get list as List of GenSolvablePolynomials.
     * Required because no List casts allowed. Equivalent to 
     * cast (List<List<GenSolvablePolynomial<C>>>) list.
     */
    public List< List< GenSolvablePolynomial<C> > > castToSolvableList() {
        List< List<GenSolvablePolynomial<C>> > slist = null;
        if ( list == null ) {
            return slist;
        }
        slist = new ArrayList< List<GenSolvablePolynomial<C>> >( list.size() ); 
        for ( List< GenPolynomial<C>> row: list ) {
            List< GenSolvablePolynomial<C> > srow 
                = new ArrayList< GenSolvablePolynomial<C> >( row.size() ); 
            for ( GenPolynomial<C> p: row ) {
                if ( ! (p instanceof GenSolvablePolynomial) ) {
                    throw new RuntimeException("no solvable polynomial "+p);
                }
                GenSolvablePolynomial<C> s
                    = (GenSolvablePolynomial<C>) p;
                srow.add( s );
            }
            slist.add( srow );
        }
        return slist;
    }

    /**
     * Get of solvable polynomials list as List of GenPolynomials.
     * Required because no List casts allowed. Equivalent to 
     * cast (List<List<GenPolynomial<C>>>) list.
     */
    public static <C extends RingElem<C> >
           List< List< GenPolynomial<C> > > 
           castToList( List< List<GenSolvablePolynomial<C>> > slist ) {
        List< List<GenPolynomial<C>> > list = null;
        if ( slist == null ) {
            return list;
        }
        list = new ArrayList< List<GenPolynomial<C>> >( slist.size() ); 
        for ( List< GenSolvablePolynomial<C>> srow: slist ) {
            List< GenPolynomial<C> > row 
                = new ArrayList< GenPolynomial<C> >( srow.size() ); 
            for ( GenSolvablePolynomial<C> s: srow ) {
                row.add( s );
            }
            list.add( row );
        }
        return list;
    }

}
