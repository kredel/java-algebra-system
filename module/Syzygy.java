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
import edu.jas.poly.PolynomialList;

import edu.jas.ring.Reduction;
import edu.jas.ring.GroebnerBase;

/**
 * Syzygy class.
 * Implements Syzygy computations and tests.
 * @author Heinz Kredel
 */

public class Syzygy  {

    private static final Logger logger = Logger.getLogger(Syzygy.class);

    /**
     * Syzygy module from Groebner base
     * F must be a Groebner base.
     */

    public static List zeroRelations(List F) {  
        return zeroRelations(0,F);
    }

    public static List zeroRelations(int modv, List F) {  
        List Z = new ArrayList();
        ArrayList S = new ArrayList( F.size() );
        for ( int i = 0; i < F.size(); i++ ) {
            S.add( null );
        }
        OrderedPolynomial pi, pj, s, h, zero;
        zero = null;
	for ( int i = 0; i < F.size(); i++ ) {
	    pi = (OrderedPolynomial) F.get(i);
            if ( pi != null && zero == null ) {
                zero = pi.getZERO();
            }
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = (OrderedPolynomial) F.get(j);
                //logger.info("p"+i+", p"+j+" = " + pi + ", " +pj);

		if ( ! Reduction.ModuleCriterion( modv, pi, pj ) ) continue;
		if ( ! Reduction.GBCriterion4( pi, pj ) ) continue;
                ArrayList row = (ArrayList)S.clone();

		s = SPolynomial( row, i, pi, j, pj );
                //logger.info("row = " + row);
		if ( s.isZERO() ) {
                   Z.add( row );
                   continue;
                }

		h = normalform( row, F, s );
		if ( ! h.isZERO() ) {
                   throw new RuntimeException("Syzygy no GB");
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


    public static ModuleList zeroRelations(ModuleList M) {  
        ModuleList N = null;
        if ( M == null ) {
            return N;
        }
        List t = (List)M.list;
        if ( t == null || t.size() == 0 ) {
            return N;
        }
        ModuleList Np = null;
        PolynomialList F = M.getPolynomialList();
        int modv = ((List)t.get(0)).size();;
        List G = zeroRelations(modv,F.list);
        if ( G == null ) {
            return N;
        }
        List Z = new ArrayList();
        for ( int i = 0; i < G.size(); i++ ) {
            F = new PolynomialList(F.coeff,F.vars,F.tord,(List)G.get(i),F.table);
            Np = ModuleList.getModuleList(modv,F);
            if ( Np != null ) {
                Z.addAll( Np.list );
            }
        }
        N = new ModuleList(M.coeff,M.vars,M.tord,Z,M.table);
        return N;
    }




    /**
     * S-Polynomial
     */

    public static OrderedPolynomial SPolynomial(ArrayList S,
                                                int i,
                                                OrderedPolynomial Ap, 
                                                int j,
                                                OrderedPolynomial Bp) {  
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

        OrderedPolynomial App = Ap.multiply( b, e1 );
        OrderedPolynomial Bpp = Bp.multiply( a, f1 );
        OrderedPolynomial Cp = App.subtract(Bpp);

        OrderedPolynomial zero = Ap.getZERO();
        OrderedPolynomial As = zero.add( b.negate(), e1 );
        OrderedPolynomial Bs = zero.add( a, f1 );
        S.set( i, As );
        S.set( j, Bs );

        return Cp;
    }


    /**
     * Normalform.
     */

    public static OrderedPolynomial normalform(ArrayList row,
                                               List Pp, 
                                               OrderedPolynomial Ap) {  
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
        OrderedPolynomial[] p = new OrderedPolynomial[ l ];
        OrderedPolynomial zero = null;
	int j = 0;
        for ( i = 0; i < l; i++ ) { 
            p[i] = (OrderedPolynomial) P[i];
            m = p[i].leadingMonomial();
	    if ( m != null ) { 
               p[j] = p[i];
               htl[j] = (ExpVector) m.getKey();
               lbc[j] = (Coefficient) m.getValue();
               if ( p[j] != null && zero == null ) {
                   zero = p[j].getZERO(/*p[j].getTermOrder()*/);
               }
	       j++;
	    }
	}
	l = j;
        ExpVector e;
        Coefficient a;
        boolean mt = false;
        OrderedPolynomial R = Ap.getZERO( /*Ap.getTermOrder()*/ );

        OrderedPolynomial fac = null;
        // OrderedPolynomial T = null;
        OrderedPolynomial Q = null;
        OrderedPolynomial S = Ap;
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
                 R = R.add( a, e );
                 S = S.subtract( a, e ); 
		 // System.out.println(" S = " + S);
                 throw new RuntimeException("Syzygy no GB");
	      } else { 
		 e = ExpVector.EVDIF( e, htl[i] );
                 //logger.info("red div = " + e);
                 a = a.divide( lbc[i] );
                 Q = p[i].multiply( a, e );
                 S = S.subtract( Q );
                 fac = (OrderedPolynomial)row.get(i);
                 if ( fac == null ) {
                    fac = zero.add( a, e );
                 } else {
                    fac = fac.add( a, e );
                 }
                 row.set(i,fac);
              }
	}
        return R;
    }


    /**
     * Test if sysygy
     */

    public static boolean isZeroRelation(List Z, List F) {  
        for ( Iterator it = Z.iterator(); it.hasNext(); ) {
            List row = (List)it.next();
            OrderedPolynomial p = scalarProduct(row,F);
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

    public static OrderedPolynomial scalarProduct(List r, List F) {  
        OrderedPolynomial sp = null;
        Iterator it = r.iterator();
        Iterator jt = F.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            OrderedPolynomial pi = (OrderedPolynomial)it.next();
            OrderedPolynomial pj = (OrderedPolynomial)jt.next();
            if ( pi == null || pj == null ) continue;
            if ( sp == null ) {
                sp = pi.multiply(pj);
            } else {
                sp = sp.add( pi.multiply(pj) );
            }
        }
        if ( it.hasNext() || jt.hasNext() ) {
            logger.error("scalarProduct wrong sizes");
        }
        return sp;
    }

}
