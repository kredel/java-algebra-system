/*
 * $Id$
 */

package edu.jas.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.jas.arith.Coefficient;

import edu.jas.poly.ExpVector;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.SolvablePolynomial;
import edu.jas.poly.OrderedMapPolynomial;
import edu.jas.poly.SolvableOrderedMapPolynomial;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.RelationTable;

import edu.jas.ring.Reduction;
import edu.jas.ring.SolvableGroebnerBase;

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


    public static ModuleList leftZeroRelations(ModuleList M) {  
        ModuleList N = null;
        if ( M == null ) {
            return N;
        }
        List t = (List)M.list;
        if ( t == null || t.size() == 0 ) {
            return N;
        }
        SolvablePolynomial zero = (SolvablePolynomial)((List)M.list.get(0)).get(0);
        zero = (SolvablePolynomial)zero.getZERO();
        //logger.info("zero = " + zero);

        ModuleList Np = null;
        PolynomialList F = M.getPolynomialList();
        int modv = ((List)t.get(0)).size();;
        logger.info("modv = " + modv);
        List G = leftZeroRelations(modv,F.list);
        if ( G == null ) {
            return N;
        }
        RelationTable contab = null;
        if ( G.size() != 0 && ((List)G.get(0)).size() != 0 ) {
            RelationTable tab = ((SolvableOrderedMapPolynomial)((List)G.get(0)).get(0)).getRelationTable();
            logger.info("tab = " + tab);
            contab = tab.contract( modv );
            logger.info("contab = " + contab);
        }
        List Z = new ArrayList();
        for ( int i = 0; i < G.size(); i++ ) {
            List Gi = (List)G.get(i);
            List Zi = new ArrayList();
            // System.out.println("\nG("+i+") = " + G.get(i));
            for ( int j = 0; j < Gi.size(); j++ ) {
                //System.out.println("\nG("+i+","+j+") = " + Gi.get(j));
                SolvableOrderedMapPolynomial p = (SolvableOrderedMapPolynomial)Gi.get(j);
                if ( p != null ) {
                    Map r = p.contract( modv, contab );
                   //System.out.println("map("+i+","+j+") = " + r + ", size = " + r.size() );
                   if ( r.size() == 0 ) {
                       Zi.add(zero); 
                   } else if ( r.size() == 1 ) {
                       SolvablePolynomial vi = (SolvablePolynomial)(r.values().toArray())[0];
                       Zi.add(vi); 
                   } else { // will not happen
                       throw new RuntimeException("Map.size() > 1 = " + r.size());
                   }
                }
            }
            //System.out.println("\nZ("+i+") = " + Zi);
            Z.add( Zi );
        }
        N = new ModuleList(M.coeff,M.vars,M.tord,Z,contab);
        //System.out.println("\n\nN = " + N);
        return N;
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


    /**
     * Test if sysygy of modules
     */

    public static boolean isLeftZeroRelation(ModuleList Z, ModuleList F) {  
        if ( Z == null ) {
            return true;
        }
        if ( Z.list == null ) {
            return true;
        }
        for ( Iterator it = Z.list.iterator(); it.hasNext(); ) {
            List row = (List)it.next();
            List zr = leftScalarProduct(row,F);
            if ( ! isZero(zr) ) {
                logger.info("is not ZeroRelation (" + zr.size() + ") = " + zr);
                return false;
            }
        }
        return true;
    }


    /**
     * product of vector and matrix of polynomials.
     */

    public static List leftScalarProduct(List r, ModuleList F) {  
        List ZZ = null;
        Iterator it = r.iterator();
        Iterator jt = F.list.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            OrderedPolynomial pi = (OrderedPolynomial)it.next();
            List vj = (List)jt.next();
            List Z = leftScalarProduct( pi, vj );
            //System.out.println("pi" + pi);
            //System.out.println("vj" + vj);
            // System.out.println("scalarProduct" + Z);
            if ( ZZ == null ) {
                ZZ = Z;
            } else {
                ZZ = vectorAdd(ZZ,Z);
            }
        }
        if ( it.hasNext() || jt.hasNext() ) {
            logger.error("scalarProduct wrong sizes");
        }
        if ( logger.isDebugEnabled() ) {
            logger.debug("scalarProduct" + ZZ);
        }
        return ZZ;
    }

    /**
     * Addition of vectors of polynomials.
     */

    public static List vectorAdd(List a, List b) {  
        if ( a == null ) {
            return b;
        }
        if ( b == null ) {
            return a;
        }
        List V = new ArrayList( a.size() );
        Iterator it = a.iterator();
        Iterator jt = b.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            OrderedPolynomial pi = (OrderedPolynomial)it.next();
            OrderedPolynomial pj = (OrderedPolynomial)jt.next();
            OrderedPolynomial p = pi.add( pj );
            V.add( p );
        }
        //System.out.println("vectorAdd" + V);
        if ( it.hasNext() || jt.hasNext() ) {
            logger.error("vectorAdd wrong sizes");
        }
        return V;
    }

    /**
     * test vector of zero polynomials.
     */

    public static boolean isZero(List a) {  
        if ( a == null ) {
            return true;
        }
        Iterator it = a.iterator();
        while ( it.hasNext() ) {
            OrderedPolynomial pi = (OrderedPolynomial)it.next();
            if ( pi == null ) {
                continue;
            }
            if ( ! pi.isZERO() ) {
                return false;
            }
        }
        return true;
    }


    /**
     * Scalar product of polynomial with vector of polynomials.
     */

    public static List leftScalarProduct(OrderedPolynomial p, List F) {  
        List V = new ArrayList( F.size() );
        for ( Iterator it = F.iterator(); it.hasNext(); ) {
            OrderedPolynomial pi = (OrderedPolynomial)it.next();
            pi = p.multiply( pi );
            V.add( pi );
        }
        return V;
    }



    /**
     * Resolution of a module.
     * Only with direct GBs.
     */
    public static List resolution(ModuleList M) {  
        List R = new ArrayList();
        ModuleList MM = M;
        ModuleList GM;
        ModuleList Z;
        while (true) {
          GM = ModSolvableGroebnerBase.leftGB(MM);
          Z = leftZeroRelations(GM);
          R.add( new SolvResPart(MM,GM,Z) );
          if ( Z == null || Z.list == null || Z.list.size() == 0 ) {
              break;
          }
          MM = Z;
        }
        return R;
    }


    /**
     * Resolution of a polynomial list.
     * Only with direct GBs.
     */
    public static List resolution(PolynomialList F) {  
        List Z;
        ModuleList Zm;
        List G;
        PolynomialList Gl;

        G = SolvableGroebnerBase.leftGB( F.list );
        Z = leftZeroRelations( G );
        Gl = new PolynomialList(F.coeff,F.vars,F.tord, G, F.table);
        Zm = new ModuleList(F.coeff,F.vars,F.tord, Z, F.table);

        List R = resolution(Zm);
        R.add( 0, new SolvResPolPart( F, Gl, Zm ) );
        return R;
    }

}


class SolvResPart implements Serializable {

    public final ModuleList module;
    public final ModuleList GB;
    public final ModuleList syzygy;

    public SolvResPart(ModuleList m, ModuleList g, ModuleList z) {
        module = m;
        GB = g;
        syzygy = z;
    }

    public String toString() {
        StringBuffer s = new StringBuffer("SolvResPart(\n");
        s.append("module = " + module);
        s.append("\n GB = " + GB);
        s.append("\n syzygy = " + syzygy);
        s.append(")");
        return s.toString();
    }
}

class SolvResPolPart implements Serializable {

    public final PolynomialList ideal;
    public final PolynomialList GB;
    public final ModuleList syzygy;

    public SolvResPolPart(PolynomialList m, PolynomialList g, ModuleList z) {
        ideal = m;
        GB = g;
        syzygy = z;
    }

    public String toString() {
        StringBuffer s = new StringBuffer("SolvResPolPart(\n");
        s.append("ideal = " + ideal);
        s.append("\n GB = " + GB);
        s.append("\n syzygy = " + syzygy);
        s.append(")");
        return s.toString();
    }

}

