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

import edu.jas.structure.RingElem;

import edu.jas.arith.BigRational;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;
import edu.jas.poly.RelationTable;

import edu.jas.ring.Reduction;
import edu.jas.ring.GroebnerBase;
import edu.jas.ring.SolvableGroebnerBase;

import edu.jas.module.ModuleList;


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



    /**
     * Left syzygy for left Groebner base.
     */
    public static <C extends RingElem<C>>
           List<List<GenSolvablePolynomial<C>>> 
           leftZeroRelations(List<GenSolvablePolynomial<C>> F) {  
        return leftZeroRelations(0,F);
    }

    /**
     * Left syzygy for left Groebner base.
     */
    public static <C extends RingElem<C>>
           List<List<GenSolvablePolynomial<C>>>
           leftZeroRelations(int modv, 
                             List<GenSolvablePolynomial<C>> F) {  
        List<List<GenSolvablePolynomial<C>>> Z 
           = new ArrayList<List<GenSolvablePolynomial<C>>>();
        ArrayList<GenSolvablePolynomial<C>> S 
           = new ArrayList<GenSolvablePolynomial<C>>( F.size() );
        for ( int i = 0; i < F.size(); i++ ) {
            S.add( null );
        }
        GenSolvablePolynomial<C> pi, pj, s, h, zero;
        zero = null;
	for ( int i = 0; i < F.size(); i++ ) {
	    pi = F.get(i);
            if ( pi != null && zero == null ) {
                zero = pi.ring.getZERO();
            }
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                //logger.info("p"+i+", p"+j+" = " + pi + ", " +pj);

		if ( ! Reduction.ModuleCriterion( modv, pi, pj ) ) {
                   continue;
                }
		// if ( ! Reduction.GBCriterion4( pi, pj ) ) continue;
                ArrayList<GenSolvablePolynomial<C>> row 
                   = (ArrayList<GenSolvablePolynomial<C>>)S.clone();

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
        for ( List<GenSolvablePolynomial<C>> vr : Z ) {
            for ( int j = 0; j < vr.size(); j++ ) {
                if ( vr.get(j) == null ) {
                    vr.set(j,zero);
                }
            }
        }
        return Z;
    }


    /**
     * Left syzygy for left module Groebner base.
     */
    public static <C extends RingElem<C>>
           ModuleList<C> 
           leftZeroRelations(ModuleList<C> M) {  
        ModuleList<C> N = null;
        if ( M == null || M.list == null) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }
        GenSolvablePolynomial<C> zero 
           = (GenSolvablePolynomial<C>)M.ring.getZERO();
        //logger.info("zero = " + zero);

        ModuleList<C> Np = null;
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        logger.info("modv = " + modv);
        List<List<GenSolvablePolynomial<C>>> G 
            = leftZeroRelations(modv,F.castToSolvableList());
        if ( G == null ) {
            return N;
        }
        List<List<GenSolvablePolynomial<C>>> Z 
            = new ArrayList<List<GenSolvablePolynomial<C>>>();
        for ( int i = 0; i < G.size(); i++ ) {
            List<GenSolvablePolynomial<C>> Gi = G.get(i);
            List Zi = new ArrayList();
            // System.out.println("\nG("+i+") = " + G.get(i));
            for ( int j = 0; j < Gi.size(); j++ ) {
                //System.out.println("\nG("+i+","+j+") = " + Gi.get(j));
                GenSolvablePolynomial<C> p = Gi.get(j);
                if ( p != null ) {
                    Map<ExpVector,GenPolynomial<C>> r = p.contract( M.ring );
                   //System.out.println("map("+i+","+j+") = " + r + ", size = " + r.size() );
                   if ( r.size() == 0 ) {
                       Zi.add(zero); 
                   } else if ( r.size() == 1 ) {
                       GenSolvablePolynomial<C> vi = (GenSolvablePolynomial<C>)(r.values().toArray())[0];
                       Zi.add(vi); 
                   } else { // will not happen
                       throw new RuntimeException("Map.size() > 1 = " + r.size());
                   }
                }
            }
            //System.out.println("\nZ("+i+") = " + Zi);
            Z.add( Zi );
        }
        N = new ModuleList<C>((GenSolvablePolynomialRing<C>)M.ring,Z);
        //System.out.println("\n\nN = " + N);
        return N;
    }




    /**
     * S-Polynomial with recording.
     */

    public static <C extends RingElem<C>>
           GenSolvablePolynomial<C> 
           leftSPolynomial(ArrayList<GenSolvablePolynomial<C>> S,
                           int i,
                           GenSolvablePolynomial<C> Ap, 
                           int j,
                           GenSolvablePolynomial<C> Bp) {  
        if ( logger.isInfoEnabled() ) {
	   if ( Bp == null || Bp.isZERO() ) {
               throw new RuntimeException("Spol B is zero");
	   }
	   if ( Ap == null || Ap.isZERO() ) {
               throw new RuntimeException("Spol A is zero");
	   }
           if ( ! Ap.ring.equals( Bp.ring ) ) { 
              logger.error("rings not equal"); 
           }
	}
        Map.Entry<ExpVector,C> ma = Ap.leadingMonomial();
        Map.Entry<ExpVector,C> mb = Bp.leadingMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g  = ExpVector.EVLCM(e,f);
        ExpVector e1 = ExpVector.EVDIF(g,e);
        ExpVector f1 = ExpVector.EVDIF(g,f);

        C a = ma.getValue();
        C b = mb.getValue();

        GenSolvablePolynomial<C> App = Ap.multiplyLeft( b, e1 );
        GenSolvablePolynomial<C> Bpp = Bp.multiplyLeft( a, f1 );
        GenSolvablePolynomial<C> Cp = (GenSolvablePolynomial<C>)App.subtract(Bpp);

        GenSolvablePolynomial<C> zero 
           = (GenSolvablePolynomial<C>)Ap.ring.getZERO();
        GenSolvablePolynomial<C> As = (GenSolvablePolynomial<C>)zero.add( b.negate(), e1 );
        GenSolvablePolynomial<C> Bs = (GenSolvablePolynomial<C>)zero.add( a, f1 );
        S.set( i, As );
        S.set( j, Bs );
        return Cp;
    }


    /**
     * LeftNormalform with recording.
     */

    public static <C extends RingElem<C>>
           GenSolvablePolynomial<C> 
           leftNormalform(ArrayList<GenSolvablePolynomial<C>> row,
                          List<GenSolvablePolynomial<C>> Pp, 
                          GenSolvablePolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        int l = Pp.size();
        GenSolvablePolynomial<C>[] P = new GenSolvablePolynomial[ l ];
        synchronized (Pp) {
            //P = Pp.toArray();
            for ( int i = 0; i < Pp.size(); i++ ) {
                P[i] = Pp.get(i);
            }
	}
        ExpVector[] htl = new ExpVector[ l ];
        Object[] lbc = new Object[ l ]; // want <C>
        GenSolvablePolynomial<C>[] p = new GenSolvablePolynomial[ l ];
        Map.Entry<ExpVector,C> m;
	int j = 0;
        int i;
        for ( i = 0; i < l; i++ ) { 
            p[i] = P[i];
            m = p[i].leadingMonomial();
	    if ( m != null ) { 
               p[j] = p[i];
               htl[j] = m.getKey();
               lbc[j] = m.getValue();
	       j++;
	    }
	}
	l = j;
        ExpVector e;
        C a;
        boolean mt = false;
        GenSolvablePolynomial<C> zero = Ap.ring.getZERO();
        GenSolvablePolynomial<C> R = Ap.ring.getZERO();

        GenSolvablePolynomial<C> fac = null;
        // GenSolvablePolynomial<C> T = null;
        GenSolvablePolynomial<C> Q = null;
        GenSolvablePolynomial<C> S = Ap;
        while ( S.length() > 0 ) { 
	      m = S.leadingMonomial();
              e = m.getKey();
              a = m.getValue();
              for ( i = 0; i < l; i++ ) {
                  mt = ExpVector.EVMT( e, htl[i] );
                  if ( mt ) break; 
	      }
              if ( ! mt ) { 
                 //logger.debug("irred");
                 R = (GenSolvablePolynomial<C>)R.add( a, e );
                 S = (GenSolvablePolynomial<C>)S.subtract( a, e ); 
		 // System.out.println(" S = " + S);
                 throw new RuntimeException("Syzygy no leftGB");
	      } else { 
		 e = ExpVector.EVDIF( e, htl[i] );
                 //logger.info("red div = " + e);
                 a = a.divide( (C)lbc[i] );
                 Q = p[i].multiplyLeft( a, e );
                 S = (GenSolvablePolynomial<C>)S.subtract( Q );
                 fac = row.get(i);
                 if ( fac == null ) {
                    fac = (GenSolvablePolynomial<C>)zero.add( a, e );
                 } else {
                    fac = (GenSolvablePolynomial<C>)fac.add( a, e );
                 }
                 row.set(i,fac);
              }
	}
        return R;
    }


    /**
     * Test if sysygy
     */

    public static <C extends RingElem<C>>
           boolean 
           isLeftZeroRelation(List<List<GenSolvablePolynomial<C>>> Z, 
                              List<GenSolvablePolynomial<C>> F) {  
        for ( List<GenSolvablePolynomial<C>> row : Z ) {
            GenSolvablePolynomial<C> p = leftScalarProduct(row,F);
            if ( p == null ) { 
               continue;
            }
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

    public static <C extends RingElem<C>>
           GenSolvablePolynomial<C> 
           leftScalarProduct(List<GenSolvablePolynomial<C>> r, 
                             List<GenSolvablePolynomial<C>> F) {  
        GenSolvablePolynomial<C> sp = null;
        Iterator<GenSolvablePolynomial<C>> it = r.iterator();
        Iterator<GenSolvablePolynomial<C>> jt = F.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            GenSolvablePolynomial<C> pi = it.next();
            GenSolvablePolynomial<C> pj = jt.next();
            if ( pi == null || pj == null ) {
               continue;
            }
            if ( sp == null ) {
                sp = pi.multiply(pj);
            } else {
                sp = (GenSolvablePolynomial<C>)sp.add( pi.multiply(pj) );
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

    public static <C extends RingElem<C>>
           boolean 
           isLeftZeroRelation(ModuleList<C> Z, 
                              ModuleList<C> F) {  
        if ( Z == null || Z.list == null ) {
            return true;
        }
        for ( List<GenSolvablePolynomial<C>> row : Z.castToSolvableList() ) {
            List<GenSolvablePolynomial<C>> zr = leftScalarProduct(row,F);
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

    public static <C extends RingElem<C>>
           List<GenSolvablePolynomial<C>> 
           leftScalarProduct(List<GenSolvablePolynomial<C>> r, 
                             ModuleList<C> F) {  
        List<GenSolvablePolynomial<C>> ZZ = null;
        Iterator<GenSolvablePolynomial<C>> it = r.iterator();
        Iterator<List<GenPolynomial<C>>> jt = F.list.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            GenSolvablePolynomial<C> pi = it.next();
            List<GenSolvablePolynomial<C>> vj = (List)jt.next();
            List<GenSolvablePolynomial<C>> Z = leftScalarProduct( pi, vj );
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

    public static <C extends RingElem<C>>
           List<GenSolvablePolynomial<C>>
           vectorAdd(List<GenSolvablePolynomial<C>> a, 
                     List<GenSolvablePolynomial<C>> b) {  
        if ( a == null ) {
            return b;
        }
        if ( b == null ) {
            return a;
        }
        List<GenSolvablePolynomial<C>> V 
            = new ArrayList<GenSolvablePolynomial<C>>( a.size() );
        Iterator<GenSolvablePolynomial<C>> it = a.iterator();
        Iterator<GenSolvablePolynomial<C>> jt = b.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            GenSolvablePolynomial<C> pi = it.next();
            GenSolvablePolynomial<C> pj = jt.next();
            GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>)pi.add( pj );
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

    public static <C extends RingElem<C>>
           boolean 
           isZero(List<GenSolvablePolynomial<C>> a) {  
        if ( a == null ) {
            return true;
        }
        for ( GenSolvablePolynomial<C> pi : a ) {
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

    public static <C extends RingElem<C>>
           List<GenSolvablePolynomial<C>> 
           leftScalarProduct(GenSolvablePolynomial<C> p, 
                             List<GenSolvablePolynomial<C>> F) {  
        List<GenSolvablePolynomial<C>> V 
            = new ArrayList<GenSolvablePolynomial<C>>( F.size() );
        for ( GenSolvablePolynomial<C> pi : F ) {
            pi = p.multiply( pi );
            V.add( pi );
        }
        return V;
    }


    /**
     * Resolution of a module.
     * Only with direct GBs.
     */
    public static <C extends RingElem<C>>
           List<SolvResPart<C>> 
           resolution(ModuleList<C> M) {  
        List<SolvResPart<C>> R = new ArrayList<SolvResPart<C>>();
        ModuleList<C> MM = M;
        ModuleList<C> GM;
        ModuleList<C> Z;
        while (true) {
          GM = ModSolvableGroebnerBase.<C>leftGB(MM);
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
    public static <C extends RingElem<C>>
           List // <SolvResPart<C>|SolvResPolPart<C>> 
           resolution(PolynomialList<C> F) {  
        List<List<GenSolvablePolynomial<C>>> Z;
        ModuleList<C> Zm;
        List<GenSolvablePolynomial<C>> G;
        PolynomialList<C> Gl;

        G = SolvableGroebnerBase.<C>leftGB( F.list );
        Z = SolvableSyzygy.<C>leftZeroRelations( G );
        Gl = new PolynomialList<C>((GenSolvablePolynomialRing<C>)F.ring, G);
        Zm = new ModuleList<C>((GenSolvablePolynomialRing<C>)F.ring, Z);

        List R = resolution(Zm);
        R.add( 0, new SolvResPolPart( F, Gl, Zm ) );
        return R;
    }

}


/**
 * Container for module resolution components.
 */

class SolvResPart<C extends RingElem<C>> implements Serializable {

    public final ModuleList<C> module;
    public final ModuleList<C> GB;
    public final ModuleList<C> syzygy;

    public SolvResPart(ModuleList<C> m, ModuleList<C> g, ModuleList<C> z) {
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


/**
 * Container for polynomial resolution components.
 */

class SolvResPolPart<C extends RingElem<C>> implements Serializable {

    public final PolynomialList<C> ideal;
    public final PolynomialList<C> GB;
    public final ModuleList<C> syzygy;

    public SolvResPolPart(PolynomialList<C> m, PolynomialList<C> g, 
                          ModuleList<C> z) {
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

