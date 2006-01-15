/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;

import edu.jas.module.Syzygy;


/**
 * Groebner Base sequential class.
 * Implements Groebner bases and GB test.
 * @author Heinz Kredel
 */

public class GroebnerBaseSeqPairSeq<C extends RingElem<C>> 
       extends GroebnerBaseAbstract<C>  {

    private static final Logger logger = Logger.getLogger(GroebnerBaseSeqPairSeq.class);
    private final boolean debug = logger.isDebugEnabled();


    Syzygy<C> syz;


    /**
     * Constructor.
     */
    public GroebnerBaseSeqPairSeq() {
        syz = new Syzygy<C>();
    }


    /**
     * Groebner base using pairlist class.
     * @param C coefficient type.
     * @param modv module variable nunber.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> 
             GB( int modv, 
                 List<GenPolynomial<C>> F ) {  
        GenPolynomial<C> p;
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        CriticalPairList<C> pairlist = null; 
        int len = F.size();
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( p.length() > 0 ) {
               p = p.monic();
               if ( p.isONE() ) {
                  G.clear(); G.add( p );
                  return G; // since no threads are activated
               }
               G.add( p );
               if ( pairlist == null ) {
                  pairlist = new CriticalPairList<C>( modv, p.ring );
               }
               // putOne not required
               pairlist.put( p );
            } else { 
               len--;
            }
        }
        if ( len <= 1 ) {
           return G; // since no threads are activated
        }

        CriticalPair<C> pair;
        GenPolynomial<C> pi;
        GenPolynomial<C> pj;
        GenPolynomial<C> S;
        GenPolynomial<C> H;
        while ( pairlist.hasNext() ) {
              pair = pairlist.getNext();
              if ( pair == null ) { 
                 pairlist.update(); // ?
                 continue; 
              }
              pi = pair.pi; 
              pj = pair.pj; 
              if ( debug ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
              }

              S = red.SPolynomial( pi, pj );
              if ( S.isZERO() ) {
                 pairlist.update( pair, S );
                 continue;
              }
              if ( debug ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
              }

              H = red.normalform( G, S );
              if ( H.isZERO() ) {
                 pairlist.update( pair, H );
                 continue;
              }
              if ( debug ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
              }

              H = H.monic();
              if ( H.isONE() ) {
                  // pairlist.record( pair, H );
                 G.clear(); G.add( H );
                 return G; // since no threads are activated
              }
              if ( debug ) {
                 logger.debug("H = " + H );
              }
              G.add( H );
              pairlist.update( pair, H );
              //pairlist.update();
        }
        logger.debug("#sequential list = "+G.size());
        G = minimalGB(G);
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
        return G;
    }


    /** 
     * Extended Groebner base using critical pair class.
     * Make abstract!
     * @param C coefficient type.
     * @param F polynomial list.
     * @return a container for an extended Groebner base of F.
     */
    public ExtendedGB<C>  
                  extGB( List<GenPolynomial<C>> F ) {
        return extGB(0,F); 
    }


    /**
     * Extended Groebner base using critical pair class.
     * @param C coefficient type.
     * @param modv module variable nunber.
     * @param F polynomial list.
     * @return a container for an extended Groebner base of F.
     */
    public ExtendedGB<C> 
             extGB( int modv, 
                    List<GenPolynomial<C>> F ) {  
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        List<List<GenPolynomial<C>>> F2G = new ArrayList<List<GenPolynomial<C>>>();
        List<List<GenPolynomial<C>>> G2F = new ArrayList<List<GenPolynomial<C>>>();
        CriticalPairList<C> pairlist = null; 
        boolean oneInGB = false;
        int len = F.size();

        List<GenPolynomial<C>> row = null;
        GenPolynomialRing<C> ring = null;
        GenPolynomial<C> H;
        GenPolynomial<C> p;

        int nzlen = 0;
        for ( GenPolynomial<C> f : F ) { 
            if ( f.length() > 0 ) {
                nzlen++;
            }
            if ( ring == null ) {
               ring = f.ring;
            }
        }
        GenPolynomial<C> mone = ring.getONE(); //.negate();
        int k = 0;
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( p.length() > 0 ) {
               row = new ArrayList<GenPolynomial<C>>( nzlen );
               for ( int j = 0; j < nzlen; j++ ) {
                   row.add(null);
               }
               //C c = p.leadingBaseCoefficient();
               //c = c.inverse();
               //p = p.multiply( c );
               row.set( k, mone ); //.multiply(c) );
               k++;
               if ( p.isUnit() ) {
                  G.clear(); G.add( p );
                  G2F.clear(); G2F.add( row );
                  oneInGB = true;
                  break;
               }
               G.add( p );
               G2F.add( row );
               if ( pairlist == null ) {
                  pairlist = new CriticalPairList<C>( modv, p.ring );
               }
               // putOne not required
               pairlist.put( p );
            } else { 
               len--;
            }
        }
        ExtendedGB<C> exgb;
        if ( len <= 1 || oneInGB ) {
           // adjust F2G
           for ( GenPolynomial<C> f : F ) {
               row = new ArrayList<GenPolynomial<C>>( G.size() );
               for ( int j = 0; j < G.size(); j++ ) {
                   row.add(null);
               }
               H = red.normalform( row, G, f );
               if ( ! H.isZERO() ) {
                  logger.error("nonzero H = " + H );
               }
               F2G.add( row );
           }
           exgb = new ExtendedGB(F,G,F2G,G2F);
           //System.out.println("exgb 1 = " + exgb);
           return exgb;
        }

        CriticalPair<C> pair;
        int i, j;
        GenPolynomial<C> pi;
        GenPolynomial<C> pj;
        GenPolynomial<C> S;
        while ( pairlist.hasNext() && ! oneInGB ) {
              pair = pairlist.getNext();
              if ( pair == null ) { 
                 pairlist.update(); // ?
                 continue; 
              }
              i = pair.i; 
              j = pair.j; 
              pi = pair.pi; 
              pj = pair.pj; 
              if ( debug ) {
                 logger.info("i, pi    = " + i + ", " + pi );
                 logger.info("j, pj    = " + j + ", " + pj );
              }

              row = new ArrayList<GenPolynomial<C>>( G.size()+1 );
              for ( int m = 0; m < G.size()+1; m++ ) {
                  row.add(null);
              }
              S = red.SPolynomial( row, i, pi, j, pj );
              if ( S.isZERO() ) {
                 pairlist.update( pair, S );
                 // do not add to G2F
                 continue;
              }
              if ( debug ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
              }

              H = red.normalform( row, G, S );
              if ( H.isZERO() ) {
                 pairlist.update( pair, H );
                 // do not add to G2F
                 continue;
              }
              if ( debug ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
              }

              //  H = H.monic();
              C c = H.leadingBaseCoefficient();
              c = c.inverse();
              H = H.multiply( c );
              row = syz.scalarProduct( mone.multiply(c), row );
              row.set( G.size(), mone );
              if ( H.isONE() ) {
                 // pairlist.record( pair, H );
                 // G.clear(); 
                 G.add( H );
                 G2F.add( row );
                 oneInGB = true;
                 break; 
              }
              if ( debug ) {
                 logger.debug("H = " + H );
              }
              G.add( H );
              pairlist.update( pair, H );
              G2F.add( row );
        }
        //exgb = new ExtendedGB(F,G,F2G,G2F);
        //System.out.println("exgb unnorm = " + exgb);
        G2F = normalizeMatrix( F.size(), G2F );
        //exgb = new ExtendedGB(F,G,F2G,G2F);
        //System.out.println("exgb nonmin = " + exgb);
        exgb = minimalExtendedGB(F.size(),G,G2F);
        G = exgb.G;
        G2F = exgb.G2F;
        logger.debug("#sequential list = " + G.size());
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
        // setup matrices
        for ( GenPolynomial<C> f : F ) {
            row = new ArrayList<GenPolynomial<C>>( G.size() );
            for ( int m = 0; m < G.size(); m++ ) {
                row.add(null);
            }
            H = red.normalform( row, G, f );
            if ( ! H.isZERO() ) {
               logger.error("nonzero H = " + H );
            }
            F2G.add( row );
        }
        return new ExtendedGB(F,G,F2G,G2F);
    }


    /**
     * Test if M is a reduction matrix.
     * @param C coefficient type.
     * @param exgb an ExtendedGB container.
     * @return true, if exgb contains a reduction matrix, else false.
     */
    public boolean
           isReductionMatrix(ExtendedGB<C> exgb) {  
        if ( exgb == null ) {
            return true;
        }
        return isReductionMatrix(exgb.F,exgb.G,exgb.F2G,exgb.G2F);
    }


    /**
     * Test if M is a reduction matrix.
     * @param C coefficient type.
     * @param F a polynomial list.
     * @param G a Groebner base.
     * @param Mf a possible reduction matrix.
     * @param Mg a possible reduction matrix.
     * @return true, if Mg and Mf are reduction matrices, else false.
     */
    public boolean
           isReductionMatrix(List<GenPolynomial<C>> F, 
                             List<GenPolynomial<C>> G,
                             List<List<GenPolynomial<C>>> Mf,  
                             List<List<GenPolynomial<C>>> Mg) {  
        // no more check G and Mg: G * Mg[i] == 0
        // check F and Mg: F * Mg[i] == G[i]
        int k = 0;
        for ( List<GenPolynomial<C>> row : Mg ) {
            GenPolynomial<C> sp = null;
            Iterator<GenPolynomial<C>> it = F.iterator();
            Iterator<GenPolynomial<C>> jt = row.iterator();
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
            if ( it.hasNext() /*no || jt.hasNext()*/ ) {
               logger.error("isReductionMatrix wrong sizes");
            }
            GenPolynomial<C> x = G.get( k );
            GenPolynomial<C> y = x.add( sp );
            if ( ! y.isZERO() ) {
               y = x.subtract( sp ); // if from identity part
               if ( ! y.isZERO() ) {
                  logger.error("F isReductionMatrix s, k = " + F.size() + ", " + k);
                  logger.error("F isReductionMatrix x = " + x);
                  logger.error("F isReductionMatrix sp = " + sp);
                  logger.error("F isReductionMatrix y = " + y);
                  return false;
               }
            }
            k++;
        }
        // check G and Mf: G * Mf[i] == F[i]
        k = 0;
        for ( List<GenPolynomial<C>> row : Mf ) {
            GenPolynomial<C> sp = null;
            Iterator<GenPolynomial<C>> it = G.iterator();
            Iterator<GenPolynomial<C>> jt = row.iterator();
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
               logger.error("Fr isReductionMatrix gs, rs = " + G.size() + ", " + row.size());
            }
            GenPolynomial<C> x = F.get( k );
            GenPolynomial<C> y = x.subtract( sp );
            if ( ! y.isZERO() ) {
               logger.error("Fr isReductionMatrix s, k = " + G.size() + ", " + k);
               logger.error("Fr isReductionMatrix x = " + x);
               logger.error("Fr isReductionMatrix sp = " + sp);
               logger.error("Fr isReductionMatrix y = " + y);
               return false;
            }
            k++;
        }
        return true;
    }


    /**
     * Nomalize M.
     * Make all rows the same size and make certain column elements zero.
     * @param C coefficient type.
     * @param M a reduction matrix.
     * @return nomalized M.
     */
    public List<List<GenPolynomial<C>>> 
           normalizeMatrix(int flen, List<List<GenPolynomial<C>>> M) {  
        if ( M == null ) {
            return M;
        }
        if ( M.size() == 0 ) {
            return M;
        }
        List<List<GenPolynomial<C>>> N = new ArrayList<List<GenPolynomial<C>>>();
        List<List<GenPolynomial<C>>> K = new ArrayList<List<GenPolynomial<C>>>();
        int len = M.get( M.size()-1 ).size(); // longest row
        //System.out.println("norm len = " + len);
        for ( List<GenPolynomial<C>> row : M ) {
            // System.out.println("row = " + row);
            List<GenPolynomial<C>> nrow = new ArrayList<GenPolynomial<C>>( row );
            for ( int i = row.size(); i < len; i++ ) {
                nrow.add( null );
            }
            //System.out.println("nrow = " + nrow);
            N.add( nrow );
        }
        int k = flen;
        for ( int i = 0; i < N.size(); i++ ) { // 0
            List<GenPolynomial<C>> row = N.get( i );
            if ( debug ) {
               logger.info("row = " + row);
            }
            K.add( row );
            if ( i < flen ) { // skip identity part
               continue;
            }
            List<GenPolynomial<C>> xrow;
            GenPolynomial<C> a;
            //System.out.println("norm i = " + i);
            for ( int j = i+1; j < N.size(); j++ ) {
                List<GenPolynomial<C>> nrow = N.get( j );
                //System.out.println("nrow = " + nrow);
                if ( k < nrow.size() ) { // always true
                   a = nrow.get( k );
                   //System.out.println("a = " + a);
                   if ( a != null && !a.isZERO() ) {
                      xrow = syz.scalarProduct( a.negate(), row);
                      xrow = syz.vectorAdd(xrow,nrow);
                      //System.out.println("xrow = " + xrow);
                      N.set( j, xrow );
                   }
                }
            }
            k++;
        }
        return K;
    }


    /**
     * Minimal extended groebner basis.
     * @param C coefficient type.
     * @param Gp a Groebner base.
     * @param M a reduction matrix, is modified.
     * @return a (partially) reduced Groebner base of Gp in a container.
     */
    public ExtendedGB<C> 
        minimalExtendedGB(int flen,
                          List<GenPolynomial<C>> Gp,
                          List<List<GenPolynomial<C>>> M) {  
        if ( Gp == null ) {
           return new ExtendedGB(null,Gp,null,M);
        }
        if ( Gp.size() <= 1 ) {
           return new ExtendedGB(null,Gp,null,M);
        }
        List<GenPolynomial<C>> G;
        List<GenPolynomial<C>> F;
        G = new ArrayList<GenPolynomial<C>>( Gp );
        F = new ArrayList<GenPolynomial<C>>( Gp.size() );

        List<List<GenPolynomial<C>>> Mg;
        List<List<GenPolynomial<C>>> Mf;
        Mg = new ArrayList<List<GenPolynomial<C>>>( M.size() );
        Mf = new ArrayList<List<GenPolynomial<C>>>( M.size() );
        List<GenPolynomial<C>> row;
        for ( List<GenPolynomial<C>> r : M ) {
            // must be copied also
            row = new ArrayList<GenPolynomial<C>>( r );
            Mg.add( row );
        }
        row = null;

        GenPolynomial<C> a;
        ExpVector e;        
        ExpVector f;        
        GenPolynomial<C> p;
        boolean mt;
        ListIterator<GenPolynomial<C>> it;
        ArrayList<Integer> ix = new ArrayList<Integer>();
        ArrayList<Integer> jx = new ArrayList<Integer>();
        int k = 0;
        //System.out.println("flen, Gp, M = " + flen + ", " + Gp.size() + ", " + M.size() );
        while ( G.size() > 0 ) {
            a = G.remove(0);
            e = a.leadingExpVector();

            it = G.listIterator();
            mt = false;
            while ( it.hasNext() && ! mt ) {
               p = it.next();
               f = p.leadingExpVector();
               mt = ExpVector.EVMT( e, f );
            }
            it = F.listIterator();
            while ( it.hasNext() && ! mt ) {
               p = it.next();
               f = p.leadingExpVector();
               mt = ExpVector.EVMT( e, f );
            }
            //System.out.println("k, mt = " + k + ", " + mt);
            if ( ! mt ) {
               F.add( a );
               ix.add( k );
            } else { // drop polynomial and corresponding row and column
               // F.add( a.ring.getZERO() );
               jx.add( k );
            }
            k++;
        }
        if ( debug ) {
           logger.debug("ix, #M, jx = " + ix + ", " + Mg.size() + ", " + jx);
        }
        int fix = -1; // copied polys
        // copy Mg to Mf as indicated by ix
        for ( int i = 0; i < ix.size(); i++ ) {
            int u = ix.get(i); 
            if ( u >= flen && fix == -1 ) {
               fix = Mf.size();
            }
            //System.out.println("copy u, fix = " + u + ", " + fix);
            if ( u >= 0 ) {
               row = Mg.get( u );
               Mf.add( row );
            }
        }
        // remove colums of Mf as indicated by jx
        for ( int i = jx.size()-1; i >= 0; i-- ) {
            int u = jx.get(i);
            if ( u >= flen ) {
               logger.info("remove column " + u);
               for ( List<GenPolynomial<C>> r : Mf ) {
                   if ( true || debug ) {
                      p = r.get( u );
                      if ( p != null && ! p.isZERO() ) {
                         logger.error("remove p non zero = " + p);
                      }
                   }
                   r.remove( u );
               }
            }
        }
        if ( F.size() <= 1 || fix == -1 ) {
           return new ExtendedGB(null,F,null,Mf);
        }
        // must return, since extended normalform has not correct order of polys
        /*
        G = F;
        F = new ArrayList<GenPolynomial<C>>( G.size() );
        List<GenPolynomial<C>> temp;
        k = 0;
        final int len = G.size();
        while ( G.size() > 0 ) {
            a = G.remove(0);
            if ( k >= fix ) { // dont touch copied polys
               row = Mf.get( k );
               //System.out.println("doing k = " + k + ", " + a);
               // must keep order, but removed polys missing
               temp = new ArrayList<GenPolynomial<C>>( len );
               temp.addAll( F );
               temp.add( a.ring.getZERO() ); // ??
               temp.addAll( G );
               //System.out.println("row before = " + row);
               a = red.normalform( row, temp, a );
               //System.out.println("row after  = " + row);
            }
            F.add( a );
            k++;
        }
        // does Mf need renormalization?
        */
        return new ExtendedGB(null,F,null,Mf);
    }

}
