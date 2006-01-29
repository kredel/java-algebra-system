/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.poly.GenSolvablePolynomial;

import edu.jas.structure.RingElem;


/**
 * Solvable Groebner Bases class, sequential algorithms.
 * Implements common left and twosided Groebner bases 
 * and left and twosided GB tests.
 * @author Heinz Kredel.
 */

public class SolvableGroebnerBaseSeq<C extends RingElem<C>> 
       extends SolvableGroebnerBaseAbstract<C>  {

    private static final Logger logger = Logger.getLogger(SolvableGroebnerBaseSeq.class);

    private final boolean debug = logger.isDebugEnabled();

    /**
     * Constructor.
     */
    public SolvableGroebnerBaseSeq() {
    }


    /**
     * Left Groebner base using pairlist class.
     * @param C coefficient type.
     * @param modv number of module variables.
     * @param F solvable polynomial list.
     * @return leftGB(F) a left Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> 
               leftGB(int modv, 
                      List<GenSolvablePolynomial<C>> F) {  
        List<GenSolvablePolynomial<C>> G 
           = new ArrayList<GenSolvablePolynomial<C>>();
        OrderedPairlist<C> pairlist = null; 
        int l = F.size();
        //  ListIterator it = F.listIterator();
        for ( GenSolvablePolynomial<C> p: F ) { 
            //  p = (SolvablePolynomial) it.next();
            if ( p.length() > 0 ) {
               p = (GenSolvablePolynomial<C>)p.monic();
               if ( p.isONE() ) {
                  G.clear(); G.add( p );
                  return G; // since no threads are activated
               }
               G.add( p );
               if ( pairlist == null ) {
                   pairlist = new OrderedPairlist<C>( modv, p.ring );
               }
               // putOne not required
               pairlist.put( p );
            } else { 
               l--;
            }
        }
        if ( l <= 1 ) {
           return G; // since no threads are activated
        }

        GenSolvablePolynomial<C> pi, pj, S, H;
        Pair<C> pair;
        while ( pairlist.hasNext() ) {
              pair = (Pair<C>) pairlist.removeNext();
              if ( pair == null ) {
                 continue; 
              }
              pi = (GenSolvablePolynomial<C>)pair.pi; 
              pj = (GenSolvablePolynomial<C>)pair.pj; 
              if ( false && debug ) {
                 logger.info("pi    = " + pi );
                 logger.info("pj    = " + pj );
              }

              S = sred.leftSPolynomial( pi, pj );
              if ( S.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( false && debug ) {
                 logger.info("ht(S) = " + S.leadingExpVector() );
              }

              H = sred.leftNormalform( G, S );
              if ( H.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( false && debug ) {
                 logger.info("ht(H) = " + H.leadingExpVector() );
              }

              H = (GenSolvablePolynomial<C>)H.monic();
              if ( H.isONE() ) {
                  G.clear(); G.add( H );
                  return G; // since no threads are activated
              }
              if ( debug ) {
                 logger.debug("H = " + H );
              }
              if ( H.length() > 0 ) {
                 l++;
                 G.add( H );
                 pairlist.put( H );
              }
        }
        logger.debug("#sequential list = "+G.size());
        G = leftMinimalGB(G);
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
        return G;
    }


    /**
     * Twosided Groebner base using pairlist class.
     * @param C coefficient type.
     * @param modv number of module variables.
     * @param Fp solvable polynomial list.
     * @return tsGB(Fp) a twosided Groebner base of Fp.
     */
    public List<GenSolvablePolynomial<C>> 
               twosidedGB(int modv, 
                          List<GenSolvablePolynomial<C>> Fp) {  
        if ( Fp == null || Fp.size() == 0 ) { // 0 not 1
            return new ArrayList<GenSolvablePolynomial<C>>( Fp );
        }
        List<GenSolvablePolynomial<C>> X = generateUnivar( modv, Fp );
        //System.out.println("X univ = " + X);
        List<GenSolvablePolynomial<C>> F 
            = new ArrayList<GenSolvablePolynomial<C>>( Fp.size() * (1+X.size()) );
        F.addAll( Fp );
        GenSolvablePolynomial<C> p, x, q;
        for ( int i = 0; i < Fp.size(); i++ ) {
            p = Fp.get(i);
            for ( int j = 0; j < X.size(); j++ ) {
                x = X.get(j);
                q = p.multiply( x );
                q = sred.leftNormalform( F, q );
                if ( !q.isZERO() ) {
                   F.add( q );
                }
            }
        }
        //System.out.println("F generated = " + F);
        List<GenSolvablePolynomial<C>> G 
            = new ArrayList<GenSolvablePolynomial<C>>();
        OrderedPairlist<C> pairlist = null; 
        int l = F.size();
        ListIterator<GenSolvablePolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( p.length() > 0 ) {
               p = (GenSolvablePolynomial<C>)p.monic();
               if ( p.isONE() ) {
                  G.clear(); G.add( p );
                  return G; // since no threads are activated
               }
               G.add( p );
               if ( pairlist == null ) {
                  pairlist = new OrderedPairlist<C>( modv, p.ring );
               }
               // putOne not required
               pairlist.put( p );
            } else { 
               l--;
            }
        }
        //System.out.println("G to check = " + G);
        if ( l <= 1 ) { // 1 ok
           return G; // since no threads are activated
        }

        Pair<C> pair;
        GenSolvablePolynomial<C> pi, pj, S, H;
        while ( pairlist.hasNext() ) {
              pair = (Pair<C>) pairlist.removeNext();
              if ( pair == null ) {
                 continue; 
              }

              pi = (GenSolvablePolynomial<C>)pair.pi; 
              pj = (GenSolvablePolynomial<C>)pair.pj; 
              if ( false && debug ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
              }

              S = sred.leftSPolynomial( pi, pj );
              if ( S.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( debug ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
              }

              H = sred.leftNormalform( G, S );
              if ( H.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( debug ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
              }

              H = (GenSolvablePolynomial<C>)H.monic();
              if ( H.isONE() ) {
                  G.clear(); G.add( H );
                  return G; // since no threads are activated
              }
              if ( debug ) {
                 logger.debug("H = " + H );
              }
              if ( H.length() > 0 ) {
                 l++;
                 G.add( H );
                 pairlist.put( H );
                 for ( int j = 0; j < X.size(); j++ ) {
                     l++;
                     x = X.get(j);
                     p = H.multiply( x );
                     p = sred.leftNormalform( G, p );
                     if ( !p.isZERO() ) {
                        p = (GenSolvablePolynomial<C>)p.monic();
                        if ( p.isONE() ) {
                           G.clear(); G.add( p );
                           return G; // since no threads are activated
                        }
                        G.add( p );
                        pairlist.put( p );
                     }
                 }
              }
        }
        logger.debug("#sequential list = "+G.size());
        G = leftMinimalGB(G);
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
        return G;
    }

}
