/*
 * $Id$
 */

package edu.jas.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;

import org.apache.log4j.Logger;


import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;

import edu.jas.ring.Reduction;
import edu.jas.ring.GroebnerBase;
import edu.jas.ring.SolvableGroebnerBase;

import edu.jas.module.ModuleList;


/**
 * Module Groebner Bases class.
 * Implements Groebner bases and GB test.
 * @author Heinz Kredel
 */

public class ModSolvableGroebnerBase  {

    private static final Logger logger = Logger.getLogger(ModSolvableGroebnerBase.class);

    /**
     * Module left Groebner base test.
     * @param C coefficient type.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return true, if F is a left Groebner base, else false.
     */
    public static <C extends RingElem<C>>  
           boolean 
           isLeftGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return SolvableGroebnerBase.isLeftGB(modv,F);
    }

    /**
     * Module left Groebner base test.
     * @param C coefficient type.
     * @param M a module basis.
     * @return true, if M is a left Groebner base, else false.
     */
    public static <C extends RingElem<C>>
           boolean 
           isLeftGB(ModuleList<C> M) {  
        if ( M == null || M.list == null ) {
            return true;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return true;
        }
        int modv = M.cols; // > 0  
        PolynomialList<C> F = M.getPolynomialList();
        return SolvableGroebnerBase.<C>isLeftGB(modv,F.castToSolvableList());
    }


    /**
     * Left Groebner base using pairlist class.
     * @param C coefficient type.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return leftGB(F) a left Groebner base for F.
     */
    public static <C extends RingElem<C>>
           List<GenSolvablePolynomial<C>> 
           leftGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return SolvableGroebnerBase.leftGB(modv,F);
    }

    /**
     * Left Groebner base using pairlist class.
     * @param C coefficient type.
     * @param M a module basis.
     * @return leftGB(M) a left Groebner base for M.
     */
    public static <C extends RingElem<C>>
           ModuleList<C> 
           leftGB(ModuleList<C> M) {  
        ModuleList<C> N = M;
        if ( M == null || M.list == null ) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }
        PolynomialList<C> F = M.getPolynomialList();
        GenSolvablePolynomialRing<C> sring 
            = (GenSolvablePolynomialRing<C>)F.ring;
        int modv = M.cols;
        List<GenSolvablePolynomial<C>> G 
            = SolvableGroebnerBase.leftGB(modv,F.castToSolvableList());
        F = new PolynomialList<C>(sring,G);
        N = F.getModuleList(modv);
        return N;
    }



    /**
     * Module twosided Groebner base test.
     * @param C coefficient type.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return true, if F is a twosided Groebner base, else false.
     */
    public static <C extends RingElem<C>>
           boolean 
           isTwosidedGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return SolvableGroebnerBase.isTwosidedGB(modv,F);
    }

    /**
     * Module twosided Groebner base test.
     * @param C coefficient type.
     * @param M a module basis.
     * @return true, if M is a twosided Groebner base, else false.
     */
    public static <C extends RingElem<C>>
           boolean 
           isTwosidedGB(ModuleList<C> M) {  
        if ( M == null || M.list == null ) {
            return true;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return true;
        }
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        return SolvableGroebnerBase.isTwosidedGB(modv,F.castToSolvableList());
    }


    /**
     * Twosided Groebner base using pairlist class.
     * @param C coefficient type.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return tsGB(F) a twosided Groebner base for F.
     */
    public static <C extends RingElem<C>>
           List<GenSolvablePolynomial<C>> 
           twosidedGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return SolvableGroebnerBase.twosidedGB(modv,F);
    }

    /**
     * Twosided Groebner base using pairlist class.
     * @param C coefficient type.
     * @param M a module basis.
     * @return tsGB(M) a twosided Groebner base for M.
     */
    public static <C extends RingElem<C>>
           ModuleList<C> 
           twosidedGB(ModuleList<C> M) {  
        ModuleList<C> N = M;
        if ( M == null || M.list == null ) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }
        PolynomialList<C> F = M.getPolynomialList();
        GenSolvablePolynomialRing<C> sring 
            = (GenSolvablePolynomialRing<C>)F.ring;
        int modv = M.cols;
        List<GenSolvablePolynomial<C>> G 
            = SolvableGroebnerBase.twosidedGB(modv,F.castToSolvableList());
        F = new PolynomialList<C>(sring,G);
        N = F.getModuleList(modv);
        return N;
    }


    /**
     * Left reduction base.
     * Reduce matrix to diagonal form using rows with units.
     */
    /* ----------------------------------------------------------------------
    public static <C extends RingElem<C>>
           ModuleList<C> 
           leftReductionBase(ModuleList<C> M) {  
        ModuleList<C> N = M;
        if ( M == null || M.list == null ) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }
        int modv = M.cols;
        List<List<GenSolvablePolynomial<C>>> mo = M.list;

        // find rows with unit components
        List<List<GenSolvablePolynomial<C>>> noOne 
            = new ArrayList<List<GenSolvablePolynomial<C>>>();
        List<List<GenSolvablePolynomial<C>>> withOne 
            = new ArrayList<List<GenSolvablePolynomial<C>>>();
        for (Iterator it = mo.iterator(); it.hasNext(); ) {
            List<GenSolvablePolynomial<C>> row = it.next();
            if ( hasOne( row ) >= 0 ) {
                withOne.add( row );
            } else {
                noOne.add( row );
            }
        }
        // sort rows with unit components to reduction order
        int kk = ((List)withOne.get( 0 )).size();
        Object[] temp = new Object[ kk ];
        for ( int i = 0; i < withOne.size(); i++ ) {
            List w = (List)withOne.get( i );
            int k = hasOne( w );
            temp[ k ] = w;
        }
        withOne = new ArrayList( withOne.size() );
        for ( int i = temp.length-1; i >= 0; i-- ) {
            if ( temp[ i ] != null ) {
               withOne.add( temp[ i ] );
            }
        }
        List old = new ArrayList( withOne );
        // interreduce this vectors
        for ( int i = 0; i < withOne.size(); i++ ) {
            Object r = withOne.remove( 0 );
            List R = new ArrayList();
            R.add( r );
            withOne = gaussReduce( withOne, R );
            withOne.add( r );
        }

        if ( true || logger.isDebugEnabled() ) {
           ModuleList m1 = new ModuleList(M.coeff,M.vars,M.tord,old,M.table);
           logger.info("old rows with 1:\n" + m1);
           //System.out.println("old rows with 1:\n" + m1);

           m1 = new ModuleList(M.coeff,M.vars,M.tord,withOne,M.table);
           logger.info("rows with 1:\n" + m1);
           System.out.println("rows with 1:\n" + m1);

           ModuleList mn = new ModuleList(M.coeff,M.vars,M.tord,noOne,M.table);
        }

        // Gaussian reduction loops
        List red = gaussReduce( noOne, withOne );

        N = new ModuleList(M.coeff,M.vars,M.tord,red,M.table);
        N.list.addAll( withOne );
        return N;
    }
    */

    /**
     * test if vector has a component with a unit.
     * return last index of one component.
     */
    /*
    public static int hasOne( List row ) {
        int col = -1;
        int i = 0;
        for (Iterator it = row.iterator(); it.hasNext(); i++ ) {
            OrderedPolynomial pol = (OrderedPolynomial)it.next();
            if ( pol != null ) {
                if ( pol.isONE() || pol.negate().isONE() ) {
                    col = i;
                }
            }
        }
        return col;
    }
    */


    /**
     * number of trailing zero components.
     */
    /*
    public static int trailingZeros( List row ) {
        int col = row.size()-1;
        for (int i = col; i >= 0; i-- ) {
            OrderedPolynomial pol = (OrderedPolynomial)row.get(i);
            if ( pol != null ) {
                if ( !pol.isZERO() ) {
                    return col-i;
                }
            }
        }
        return row.size();
    }
    */

    /**
     * Linear combination.
     * compute a + l b.
     */
    /*
    public static List linearCombination( List a, SolvablePolynomial l, List b ) {
        if ( a == null ) {
            throw new RuntimeException("a null " + a );
            //return null; // wrong @fixme
        }
        if ( l == null || b == null ) {
            return a; 
        }
        if ( a.size() != b.size() ) {
            throw new RuntimeException("a,b wrong sizes " + a + ", " + b );
        }
        if ( l.isZERO() ) {
            //System.out.println("l is zero");
            return a;
        }
        List c = new ArrayList( a.size() );
        Iterator jt = b.iterator();
        for (Iterator it = a.iterator(); it.hasNext(); ) {
            SolvablePolynomial ap = (SolvablePolynomial)it.next();
            SolvablePolynomial bp = (SolvablePolynomial)jt.next();
            OrderedPolynomial cp = ap.add( l.multiply(bp) );
            c.add( cp );
        }
        return c;
    }
    */

    /**
     * Reduce a with b.
     */
    /*
    public static List gaussReduce( List a, List b ) {
        if ( a == null ) {
            return null; 
        }
        if ( b == null ) {
            return a; 
        }
        // Gaussian reduction loops
        List red = new ArrayList();
        for ( Iterator it = a.iterator(); it.hasNext(); ) {
            List r = (List) it.next();
            //PolynomialList F = new PolynomialList(M.coeff,M.vars,M.tord,r,M.table);
            //System.out.println("r = " + F);
            for ( Iterator jt = b.iterator(); jt.hasNext(); ) {
                List m = (List) jt.next();

                int k = hasOne( m );
                // assert k >= 0;
                SolvablePolynomial l = (SolvablePolynomial)r.get(k);
                SolvablePolynomial e = (SolvablePolynomial)m.get(k);
                if ( e.isONE() ) { 
                   l = (SolvablePolynomial)l.negate();
                } // else e = -1
                //System.out.println("k = " + k);
                //System.out.println("l = " + l);

                r = linearCombination( r, l, m );
            }
            //F = new PolynomialList(M.coeff,M.vars,M.tord,r,M.table);
            //System.out.println("r = " + F);
            //System.out.println("trailingZeros(r) = " + trailingZeros( r ));
            if ( trailingZeros( r ) < r.size() ) {
               red.add(r);
            }
        }

        return red;
    }
    */


    /**
     * intersect to n components.
     */
    /*
    public static List intersect( int p, List row ) {
        List irow = new ArrayList( p );
        for (int i = 0; i < p; i++ ) {
            OrderedPolynomial pol = (OrderedPolynomial)row.get(i);
            irow.add( pol );
        }
        return irow;
    }
    */

    /**
     * Intersection to n components.
     */
    /*
    public static ModuleList intersection(int p, ModuleList M) {  
        ModuleList N = M;
        List mo = (List)M.list;
        if ( mo == null || mo.size() == 0 ) {
           return N;
        }
        List red = new ArrayList();
        for ( Iterator it = mo.iterator(); it.hasNext(); ) {
            List r = (List) it.next();
            //System.out.println("s-trailingZeros(r) = " + (r.size()-trailingZeros( r )));
            if ( r.size()-trailingZeros( r ) <= p ) {
               r = intersect(p,r);
               red.add(r);
            }
        }
        N = new ModuleList(M.coeff,M.vars,M.tord,red,M.table);
        return N;
    }
    */

    /**
     * Left irreducible base.
     */
    /*
    public static ArrayList leftIrreducibleBase(int modv, List F) {  
        return leftIrreducibleSet(modv,F);
    }
    */


    /**
     * Left irreducible base.
     */
    /*
    public static ModuleList leftIrreducibleBase(ModuleList M) {  
        ModuleList N = M;
        List mo = (List)M.list;
        int modv = 0;
        if ( mo == null || mo.size() == 0 ) {
           return N;
        }
        modv = ((List)mo.get(0)).size();
        PolynomialList F = M.getPolynomialList();
        List G = leftIrreducibleSet(modv,F.list);
        F = new PolynomialList(F.coeff,F.vars,F.tord,G,F.table);
        N = ModuleList.getModuleList(modv,F);
        return N;
    }
    */


    /**
     * Left irreducible set.
     */
    /*
    public static ArrayList leftIrreducibleSet(int modv, List Pp) {  
        SolvablePolynomial a;
        ArrayList P = new ArrayList();
        ListIterator it = Pp.listIterator();
        while ( it.hasNext() ) { 
            a = (SolvablePolynomial) it.next();
            if ( a.length() != 0 ) {
               a = (SolvablePolynomial)a.monic();
               P.add( a );
	    }
	}
        int l = P.size();
        if ( l <= 1 ) return P;

        int irr = 0;
        ExpVector e;        
        ExpVector f;        
        logger.debug("irr = ");
        while ( irr != l ) {
            it = P.listIterator(); 
	    a = (SolvablePolynomial) it.next();
            P.remove(0);
            e = a.leadingExpVector();
            a = Reduction.leftNormalform( P, a );
            logger.debug(String.valueOf(irr));
            if ( a.length() == 0 ) { l--;
	       if ( l <= 1 ) { return P; }
	    } else {
	       f = a.leadingExpVector();
               if ( ExpVector.EVSIGN( f ) == 0 ) { 
		  P = new ArrayList(); 
                  P.add( (SolvablePolynomial)a.monic() ); 
	          return P;
               }    
               if ( e.equals( f ) ) {
		  irr++;
	       } else {
                  irr = 0; a = (SolvablePolynomial)a.monic();
	       }
               P.add( a );
	    }
	}
        //System.out.println();
	return P;
    }
    */

}
