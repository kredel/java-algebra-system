/*
 * $Id$
 */

package edu.jas.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.arith.Coefficient;

import edu.jas.poly.ExpVector;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.SolvablePolynomial;
import edu.jas.poly.OrderedMapPolynomial;
import edu.jas.poly.PolynomialList;

import edu.jas.ring.Reduction;
import edu.jas.ring.GroebnerBase;

/**
 * Syzygy class for solvable polynomials.
 * Implements Syzygy computations and tests.
 * @author Heinz Kredel
 */

public class SolvableSyzygy  {

    private static final Logger logger = Logger.getLogger(SolvableSyzygy.class);

    /**
     * Solvable Syzygy module from Groebner base
     * F must be a Groebner base.
     */

    public static List leftZeroRelations(List F) {  
        return leftZeroRelations(0,F);
    }

    public static List leftZeroRelations(int modv, List F) {  
        List Z = new ArrayList();
        ArrayList S = new ArrayList( F.size() );
        for ( int i = 0; i < F.size(); i++ ) {
            S.add( null );
        }
        SolvablePolynomial pi, pj, s, h, zero;
        zero = null;
	for ( int i = 0; i < F.size(); i++ ) {
	    pi = (SolvablePolynomial) F.get(i);
            if ( pi != null && zero == null ) {
                zero = (SolvablePolynomial)pi.getZERO();
            }
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = (SolvablePolynomial) F.get(j);
                //logger.info("p"+i+", p"+j+" = " + pi + ", " +pj);

		if ( ! Reduction.ModuleCriterion( modv, pi, pj ) ) continue;
		// if ( ! Reduction.GBCriterion4( pi, pj ) ) continue;
                ArrayList row = (ArrayList)S.clone();

		s = leftSPolynomial( row, i, pi, j, pj );
                //logger.info("row = " + row);
		if ( s.isZERO() ) {
                   Z.add( row );
                   continue;
                }

		h = leftNormalform( row, F, s );
		if ( ! h.isZERO() ) {
                   throw new RuntimeException("Syzygy no leftGB");
                }
                if ( logger.isDebugEnabled() ) {
                   logger.info("row = " + row);
                }
                Z.add( row );
	    }
	}
        // set null to zero
        for (Iterator it = Z.iterator(); it.hasNext(); ) {
            ArrayList vr = (ArrayList)it.next();
            for ( int j = 0; j < vr.size(); j++ ) {
                if ( vr.get(j) == null ) {
                    vr.set(j,zero);
                }
            }
        }
        return Z;
    }



    /**
     * S-Polynomial
     */

    public static SolvablePolynomial leftSPolynomial(ArrayList S,
                                                int i,
                                                SolvablePolynomial Ap, 
                                                int j,
                                                SolvablePolynomial Bp) {  
        if ( logger.isInfoEnabled() ) {
	   if ( Bp == null || Bp.isZERO() ) {
               throw new RuntimeException("Spol B is zero");
	   }
	   if ( Ap == null || Ap.isZERO() ) {
               throw new RuntimeException("Spol A is zero");
	   }
           if ( ! Ap.getTermOrder().equals( Bp.getTermOrder() ) ) { 
              logger.error("term orderings not equal"); 
           }
	}
        Map.Entry ma = Ap.leadingMonomial();
        Map.Entry mb = Bp.leadingMonomial();

        ExpVector e = (ExpVector) ma.getKey();
        ExpVector f = (ExpVector) mb.getKey();

        ExpVector g  = ExpVector.EVLCM(e,f);
        ExpVector e1 = ExpVector.EVDIF(g,e);
        ExpVector f1 = ExpVector.EVDIF(g,f);

        Coefficient a = (Coefficient) ma.getValue();
        Coefficient b = (Coefficient) mb.getValue();

        SolvablePolynomial App = Ap.multiplyLeft( b, e1 );
        SolvablePolynomial Bpp = Bp.multiplyLeft( a, f1 );
        SolvablePolynomial Cp = (SolvablePolynomial)App.subtract(Bpp);

        SolvablePolynomial zero = (SolvablePolynomial)Ap.getZERO();
        SolvablePolynomial As = (SolvablePolynomial)zero.add( b.negate(), e1 );
        SolvablePolynomial Bs = (SolvablePolynomial)zero.add( a, f1 );
        S.set( i, As );
        S.set( j, Bs );

        return Cp;
    }


    /**
     * LeftNormalform.
     */

    public static SolvablePolynomial leftNormalform(ArrayList row,
                                               List Pp, 
                                               SolvablePolynomial Ap) {  
        if ( Pp == null ) return Ap;
        if ( Pp.isEmpty() ) return Ap;
        int i;
        int l = Pp.size();
        Map.Entry m;
        Object[] P;
        synchronized (Pp) {
           P = Pp.toArray();
	}
        ExpVector[] htl = new ExpVector[ l ];
        Coefficient[] lbc = new Coefficient[ l ];
        SolvablePolynomial[] p = new SolvablePolynomial[ l ];
        SolvablePolynomial zero = null;
	int j = 0;
        for ( i = 0; i < l; i++ ) { 
            p[i] = (SolvablePolynomial) P[i];
            m = p[i].leadingMonomial();
	    if ( m != null ) { 
               p[j] = p[i];
               htl[j] = (ExpVector) m.getKey();
               lbc[j] = (Coefficient) m.getValue();
               if ( p[j] != null && zero == null ) {
                   zero = (SolvablePolynomial)p[j].getZERO(/*p[j].getTermOrder()*/);
               }
	       j++;
	    }
	}
	l = j;
        ExpVector e;
        Coefficient a;
        boolean mt = false;
        SolvablePolynomial R = Ap.getZERO( Ap.getRelationTable(), Ap.getTermOrder() );

        SolvablePolynomial fac = null;
        // OrderedPolynomial T = null;
        SolvablePolynomial Q = null;
        SolvablePolynomial S = Ap;
        while ( S.length() > 0 ) { 
	      m = S.leadingMonomial();
              e = (ExpVector) m.getKey();
              a = (Coefficient) m.getValue();
              for ( i = 0; i < l; i++ ) {
                  mt = ExpVector.EVMT( e, htl[i] );
                  if ( mt ) break; 
	      }
              if ( ! mt ) { 
                 //logger.debug("irred");
                 R = (SolvablePolynomial)R.add( a, e );
                 S = (SolvablePolynomial)S.subtract( a, e ); 
		 // System.out.println(" S = " + S);
                 throw new RuntimeException("Syzygy no leftGB");
	      } else { 
		 e = ExpVector.EVDIF( e, htl[i] );
                 //logger.info("red div = " + e);
                 a = a.divide( lbc[i] );
                 Q = p[i].multiplyLeft( a, e );
                 S = (SolvablePolynomial)S.subtract( Q );
                 fac = (SolvablePolynomial)row.get(i);
                 if ( fac == null ) {
                    fac = (SolvablePolynomial)zero.add( a, e );
                 } else {
                    fac = (SolvablePolynomial)fac.add( a, e );
                 }
                 row.set(i,fac);
              }
	}
        return R;
    }


    /**
     * Test if sysygy
     */

    public static boolean isLeftZeroRelation(List Z, List F) {  
        for ( Iterator it = Z.iterator(); it.hasNext(); ) {
            List row = (List)it.next();
            SolvablePolynomial p = leftScalarProduct(row,F);
            if ( p == null ) continue;
            if ( ! p.isZERO() ) {
                logger.info("is not ZeroRelation = " + p);
                return false;
            }
        }
        return true;
    }


    /**
     * Scalar product of vectors of polynomials.
     */

    public static SolvablePolynomial leftScalarProduct(List r, List F) {  
        SolvablePolynomial sp = null;
        Iterator it = r.iterator();
        Iterator jt = F.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            SolvablePolynomial pi = (SolvablePolynomial)it.next();
            SolvablePolynomial pj = (SolvablePolynomial)jt.next();
            if ( pi == null || pj == null ) continue;
            if ( sp == null ) {
                sp = (SolvablePolynomial)pi.multiply(pj);
            } else {
                sp = (SolvablePolynomial)sp.add( pi.multiply(pj) );
            }
        }
        if ( it.hasNext() || jt.hasNext() ) {
            logger.error("scalarProduct wrong sizes");
        }
        return sp;
    }

}
