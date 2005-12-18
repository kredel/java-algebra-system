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
import edu.jas.poly.PolynomialList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;

import edu.jas.ring.Reduction;
import edu.jas.ring.GroebnerBase;

import edu.jas.module.ModuleList;
import edu.jas.module.GenVector;
import edu.jas.module.GenVectorModul;


/**
 * Syzygy class.
 * Implements Syzygy computations and tests.
 * @author Heinz Kredel
 */

public class Syzygy  {

    private static final Logger logger = Logger.getLogger(Syzygy.class);

    /**
     * Syzygy module from Groebner base.
     * F must be a Groebner base.
     * @param C coefficient type.
     * @param F a Groebner base.
     * @return syz(F), a basis for the module of syzygies for F.
     */

    public static <C extends RingElem<C>>
           List<List<GenPolynomial<C>>> 
           zeroRelations(List<GenPolynomial<C>> F) {  
        return zeroRelations(0,F);
    }


    /**
     * Syzygy module from Groebner base.
     * F must be a Groebner base.
     * @param C coefficient type.
     * @param modv number of module variables.
     * @param F a Groebner base.
     * @return syz(F), a basis for the module of syzygies for F.
     */

    public static <C extends RingElem<C>>
           List<List<GenPolynomial<C>>> 
           zeroRelations(int modv, List<GenPolynomial<C>> F) {  
        List<List<GenPolynomial<C>>> Z 
           = new ArrayList<List<GenPolynomial<C>>>();
        if ( F == null ) {
           return Z;
        }
        GenVectorModul<GenPolynomial<C>> mfac = null;
        int i = 0;
        while ( mfac == null && i < F.size() ) {
            GenPolynomial<C> p = F.get(i);
            if ( p != null ) {
               mfac = new GenVectorModul<GenPolynomial<C>>( p.ring, 
                                                            F.size() );
            }
        }
        if ( mfac == null ) {
           return Z;
        }
        GenVector<GenPolynomial<C>> v = mfac.fromList( F );
        //System.out.println("F = " + F + " v = " + v);
        return zeroRelations(modv,v);
    }


    /**
     * Syzygy module from Groebner base.
     * v must be a Groebner base.
     * @param C coefficient type.
     * @param modv number of module variables.
     * @param v a Groebner base.
     * @return syz(v), a basis for the module of syzygies for v.
     */

    public static <C extends RingElem<C>>
           List<List<GenPolynomial<C>>> 
           zeroRelations(int modv, GenVector<GenPolynomial<C>> v) {  

        List<List<GenPolynomial<C>>> Z 
           = new ArrayList<List<GenPolynomial<C>>>();

        GenVectorModul<GenPolynomial<C>> mfac = v.modul;
        List<GenPolynomial<C>> F = v.val; 
        GenVector<GenPolynomial<C>> S = mfac.getZERO();
        GenPolynomial<C> pi, pj, s, h, zero;
        zero = mfac.coFac.getZERO();
	for ( int i = 0; i < F.size(); i++ ) {
	    pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                //logger.info("p"+i+", p"+j+" = " + pi + ", " +pj);

		if ( ! Reduction.<C>ModuleCriterion( modv, pi, pj ) ) {
                   continue;
                }
		// if ( ! Reduction.GBCriterion4( pi, pj ) ) { continue; }
                List<GenPolynomial<C>> row = S.clone().val;

		s = SPolynomial( row, i, pi, j, pj );
		if ( s.isZERO() ) {
                   Z.add( row );
                   continue;
                }

		h = normalform( row, F, s );
		if ( ! h.isZERO() ) {
                   throw new RuntimeException("Syzygy no GB");
                }
                if ( logger.isDebugEnabled() ) {
                   logger.info("row = " + row.size());
                }
                Z.add( row );
	    }
	}
        return Z;
    }


    /**
     * Syzygy module from module Groebner base.
     * M must be a module Groebner base.
     * @param C coefficient type.
     * @param M a module Groebner base.
     * @return syz(M), a basis for the module of syzygies for M.
     */

    public static <C extends RingElem<C>>
           ModuleList<C> 
           zeroRelations(ModuleList<C> M) {  
        ModuleList<C> N = M;
        if ( M == null || M.list == null) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }
        GenPolynomial<C> zero = M.ring.getZERO();
        //System.out.println("zero = " + zero);

        ModuleList<C> Np = null;
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        //System.out.println("modv = " + modv);
        List<List<GenPolynomial<C>>> G = zeroRelations(modv,F.list);
        if ( G == null ) {
            return N;
        }
        List<List<GenPolynomial<C>>> Z 
           = new ArrayList<List<GenPolynomial<C>>>();
        for ( int i = 0; i < G.size(); i++ ) {
            //F = new PolynomialList(F.ring,(List)G.get(i));
            List<GenPolynomial<C>> Gi = G.get(i);
            List<GenPolynomial<C>> Zi = new ArrayList<GenPolynomial<C>>();
            // System.out.println("\nG("+i+") = " + G.get(i));
            for ( int j = 0; j < Gi.size(); j++ ) {
                //System.out.println("\nG("+i+","+j+") = " + Gi.get(j));
                GenPolynomial<C> p = Gi.get(j);
                if ( p != null ) {
                   Map<ExpVector,GenPolynomial<C>> r = p.contract( M.ring );
                   int s = 0;
                   for ( GenPolynomial<C> vi : r.values() ) {
                       Zi.add(vi); 
                       s++;
                   }
                   if ( s == 0 ) {
                       Zi.add(zero); 
                   } else if ( s > 1 ) { // will not happen
                       System.out.println("p = " + p );
                       System.out.println("map("+i+","+j+") = " 
                                          + r + ", size = " + r.size() );
                       throw new RuntimeException("Map.size() > 1 = " + r.size());
                   }
                }
            }
            //System.out.println("\nZ("+i+") = " + Zi);
            Z.add( Zi );
        }
        N = new ModuleList<C>(M.ring,Z);
        //System.out.println("\n\nN = " + N);
        return N;
    }




    /**
     * S-Polynomial with recording.
     * @param C coefficient type.
     * @param S recording matrix, is modified.
     * @param i index of Ap in basis list.
     * @param Ap a polynomial.
     * @param j index of Bp in basis list.
     * @param Bp a polynomial.
     * @return Spol(Ap, Bp), the S-Polynomial for Ap and Bp.
     */

    public static <C extends RingElem<C>>
           GenPolynomial<C> 
           SPolynomial(List<GenPolynomial<C>> S,
                       int i,
                       GenPolynomial<C> Ap, 
                       int j,
                       GenPolynomial<C> Bp) {  
        if ( logger.isInfoEnabled() ) {
	   if ( Bp == null || Bp.isZERO() ) {
               throw new RuntimeException("Spol B is zero");
	   }
	   if ( Ap == null || Ap.isZERO() ) {
               throw new RuntimeException("Spol A is zero");
	   }
           if ( ! Ap.ring.equals( Bp.ring ) ) { 
              logger.error("term orderings not equal"); 
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

        GenPolynomial<C> App = Ap.multiply( b, e1 );
        GenPolynomial<C> Bpp = Bp.multiply( a, f1 );
        GenPolynomial<C> Cp = App.subtract(Bpp);

        GenPolynomial<C> zero = Ap.ring.getZERO();
        GenPolynomial<C> As = (GenPolynomial<C>)zero.add( b.negate(), e1 );
        GenPolynomial<C> Bs = (GenPolynomial<C>)zero.add( a, f1 );
        S.set( i, As );
        S.set( j, Bs );

        return Cp;
    }


    /**
     * Normalform with recording.
     * @param C coefficient type.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the normal form of Ap wrt. Pp.
     */

    public static <C extends RingElem<C>>
           GenPolynomial<C> 
           normalform(List<GenPolynomial<C>> row,
                      List<GenPolynomial<C>> Pp, 
                      GenPolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        int l = Pp.size();
        GenPolynomial<C>[] P = new GenPolynomial[l];
        synchronized (Pp) {
            //P = Pp.toArray();
            for ( int i = 0; i < Pp.size(); i++ ) {
                P[i] = Pp.get(i);
            }
	}
        ExpVector[] htl = new ExpVector[ l ];
        Object[] lbc = new Object[ l ]; // want <C>
        GenPolynomial<C>[] p = new GenPolynomial[ l ];
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
        GenPolynomial<C> zero = Ap.ring.getZERO();
        GenPolynomial<C> R = Ap.ring.getZERO();

        GenPolynomial<C> fac = null;
        // GenPolynomial<C> T = null;
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
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
                 R = R.add( a, e );
                 S = S.subtract( a, e ); 
		 // System.out.println(" S = " + S);
                 throw new RuntimeException("Syzygy no GB");
	      } else { 
		 e = ExpVector.EVDIF( e, htl[i] );
                 //logger.info("red div = " + e);
                 a = a.divide( (C)lbc[i] );
                 Q = p[i].multiply( a, e );
                 S = S.subtract( Q );
                 fac = row.get(i);
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
     * Test if sysygy.
     * @param C coefficient type.
     * @param Z list of sysygies.
     * @param F a polynomial list.
     * @return true, if Z is a list of syzygies for F, else false.
     */

    public static <C extends RingElem<C>>
           boolean 
           isZeroRelation(List<List<GenPolynomial<C>>> Z, 
                          List<GenPolynomial<C>> F) {  
        for ( List<GenPolynomial<C>> row: Z ) {
            GenPolynomial<C> p = scalarProduct(row,F);
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
     * @param C coefficient type.
     * @param r a polynomial list.
     * @param F a polynomial list.
     * @return the scalar product of r and F.
     */

    public static <C extends RingElem<C>>
           GenPolynomial<C> 
           scalarProduct(List<GenPolynomial<C>> r, 
                         List<GenPolynomial<C>> F) {  
        GenPolynomial<C> sp = null;
        Iterator<GenPolynomial<C>> it = r.iterator();
        Iterator<GenPolynomial<C>> jt = F.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            GenPolynomial<C> pi = it.next();
            GenPolynomial<C> pj = jt.next();
            if ( pi == null || pj == null ) {
               continue;
            }
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


    /**
     * Test if sysygy of modules.
     * @param C coefficient type.
     * @param Z list of sysygies.
     * @param F a module list.
     * @return true, if Z is a list of syzygies for F, else false.
     */

    public static <C extends RingElem<C>>
           boolean 
           isZeroRelation(ModuleList<C> Z, ModuleList<C> F) {  
        if ( Z == null || Z.list == null ) {
            return true;
        }
        for ( List<GenPolynomial<C>> row : Z.list ) {
            List<GenPolynomial<C>> zr = scalarProduct(row,F);
            if ( ! isZero(zr) ) {
                logger.info("is not ZeroRelation (" + zr.size() + ") = " + zr);
                return false;
            }
        }
        return true;
    }


    /**
     * product of vector and matrix of polynomials.
     * @param C coefficient type.
     * @param r a polynomial list.
     * @param F a polynomial list.
     * @return the scalar product of r and F.
     */

    public static <C extends RingElem<C>>
           List<GenPolynomial<C>> 
           scalarProduct(List<GenPolynomial<C>> r, ModuleList<C> F) {  
        List<GenPolynomial<C>> ZZ = null;
        Iterator<GenPolynomial<C>> it = r.iterator();
        Iterator<List<GenPolynomial<C>>> jt = F.list.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            GenPolynomial<C> pi = it.next();
            List<GenPolynomial<C>> vj = jt.next();
            List<GenPolynomial<C>> Z = scalarProduct( pi, vj );
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
     * @param C coefficient type.
     * @param a a polynomial list.
     * @param b a polynomial list.
     * @return a+b, the vector sum of a and b.
     */

    public static <C extends RingElem<C>>
           List<GenPolynomial<C>> 
           vectorAdd(List<GenPolynomial<C>> a, List<GenPolynomial<C>> b) {  
        if ( a == null ) {
            return b;
        }
        if ( b == null ) {
            return a;
        }
        List<GenPolynomial<C>> V = new ArrayList<GenPolynomial<C>>( a.size() );
        Iterator<GenPolynomial<C>> it = a.iterator();
        Iterator<GenPolynomial<C>> jt = b.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            GenPolynomial<C> pi = it.next();
            GenPolynomial<C> pj = jt.next();
            GenPolynomial<C> p = pi.add( pj );
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
     * @param C coefficient type.
     * @param a a polynomial list.
     * @return true, if all polynomial in a are zero, else false.
     */
    public static <C extends RingElem<C>>
           boolean 
           isZero(List<GenPolynomial<C>> a) {  
        if ( a == null ) {
            return true;
        }
        for ( GenPolynomial<C> pi : a ) {
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
     * @param C coefficient type.
     * @param p a polynomial.
     * @param F a polynomial list.
     * @return the scalar product of p and F.
     */

    public static <C extends RingElem<C>>
           List<GenPolynomial<C>> 
           scalarProduct(GenPolynomial<C> p, List<GenPolynomial<C>> F) {  
        List<GenPolynomial<C>> V = new ArrayList<GenPolynomial<C>>( F.size() );
        for ( GenPolynomial<C> pi : F ) {
            pi = p.multiply( pi );
            V.add( pi );
        }
        return V;
    }


    /**
     * Resolution of a module.
     * Only with direct GBs.
     * @param C coefficient type.
     * @param M a module list of a Groebner basis.
     * @return a resolution of M.
     */
    public static <C extends RingElem<C>>
           List<ResPart<C>>
           resolution(ModuleList<C> M) {  
        List<ResPart<C>> R = new ArrayList<ResPart<C>>();
        ModuleList<C> MM = M;
        ModuleList<C> GM;
        ModuleList<C> Z;
        while (true) {
          GM = ModGroebnerBase.<C>GB(MM);
          Z = zeroRelations(GM);
          R.add( new ResPart<C>(MM,GM,Z) );
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
     * @param C coefficient type.
     * @param F a polynomial list of a Groebner basis.
     * @return a resolution of F.
     */
    public static <C extends RingElem<C>>
           List // <ResPart<C>|ResPolPart<C>>
           resolution(PolynomialList<C> F) {  
        List<List<GenPolynomial<C>>> Z;
        ModuleList<C> Zm;
        List<GenPolynomial<C>> G;
        PolynomialList<C> Gl;

        G = GroebnerBase.<C>GB( F.list );
        Z = zeroRelations( G );
        Gl = new PolynomialList<C>(F.ring, G);
        Zm = new ModuleList<C>(F.ring, Z);

        List R = resolution(Zm); //// <ResPart<C>|ResPolPart<C>>
        R.add( 0, new ResPolPart<C>( F, Gl, Zm ) ); 
        return R;
    }

}


/**
 * Container for module resolution components.
 */
class ResPart<C extends RingElem<C>> implements Serializable {

    public final ModuleList<C> module;
    public final ModuleList<C> GB;
    public final ModuleList<C> syzygy;

   /**
     * ResPart.
     * @param m a module list.
     * @param g a module list GB.
     * @param z a syzygy module list.
     */
    public ResPart(ModuleList<C> m, ModuleList<C> g, ModuleList<C> z) {
        module = m;
        GB = g;
        syzygy = z;
    }


/**
 * toString.
 */
    public String toString() {
        StringBuffer s = new StringBuffer("ResPart(\n");
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
class ResPolPart<C extends RingElem<C>> implements Serializable {

    public final PolynomialList<C> ideal;
    public final PolynomialList<C> GB;
    public final ModuleList<C> syzygy;

    /**
      * ResPolPart.
      * @param m a polynomial list.
      * @param g a polynomial list GB.
      * @param z a syzygy module list.
      */
    public ResPolPart(PolynomialList<C> m, PolynomialList<C> g, ModuleList<C> z) {
        ideal = m;
        GB = g;
        syzygy = z;
    }


   /**
     * toString.
     */
    public String toString() {
        StringBuffer s = new StringBuffer("ResPolPart(\n");
        s.append("ideal = " + ideal);
        s.append("\n GB = " + GB);
        s.append("\n syzygy = " + syzygy);
        s.append(")");
        return s.toString();
    }

}
