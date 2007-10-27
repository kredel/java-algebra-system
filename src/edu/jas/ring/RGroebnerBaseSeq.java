/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RegularRingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;

import edu.jas.ring.OrderedDPairlist;

/**
 * R-Groebner Base sequential algorithm.
 * Implements R-Groebner bases and GB test.
 * <b>Note:</b> Minimal reduced GBs are not unique.
 * see BWK, section 10.1.
 * @author Heinz Kredel
 */

public class RGroebnerBaseSeq<C extends RegularRingElem<C>> 
       extends GroebnerBaseAbstract<C>  {


    private static final Logger logger = Logger.getLogger(RGroebnerBaseSeq.class);
    private final boolean debug = logger.isDebugEnabled();



    /**
     * Reduction engine.
     */
    protected RReduction<C> red;  // shadow super.red ??


    /**
     * Constructor.
     */
    public RGroebnerBaseSeq() {
        this( new RReductionSeq<C>() );
    }


    /**
     * Constructor.
     * @param red D-Reduction engine
     */
    public RGroebnerBaseSeq(RReduction<C> red) {
        super(red);
        this.red = red;
    }


    /**
     * R-Groebner base test.
     * @typeparam C coefficient type.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a D-Groebner base, else false.
     */
    public boolean isGB(int modv, List<GenPolynomial<C>> F) {  
        if ( ! red.isBooleanClosed(F) ) {
           return false;
        }
        GenPolynomial<C> pi, pj, s, h, d;
        for ( int i = 0; i < F.size(); i++ ) {
            pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                   continue;
                }
                // works ?
                //if ( ! red.criterion4( pi, pj ) ) { 
                //   continue;
                //}
                s = red.SPolynomial( pi, pj );
                if ( ! s.isZERO() ) {
                   s = red.normalform( F, s );
                }
                if ( ! s.isZERO() ) {
                   System.out.println("s-pol("+i+","+j+") != 0: " + s);
                   return false;
                }
            }
        }
        return true;
    }


    /**
     * R-Groebner base using pairlist class.
     * @typeparam C coefficient type.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a R-Groebner base of F.
     */
    public List<GenPolynomial<C>> 
             GB( int modv, 
                 List<GenPolynomial<C>> F ) {  
        //throw new RuntimeException("not jet implemented");
        GenPolynomial<C> p;
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();

        List<GenPolynomial<C>> bcF = red.reducedBooleanClosure(F);
        F = bcF;

        OrderedPairlist<C> pairlist = null; 
        int l = F.size();
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( !p.isZERO() ) {
               p = p.abs(); // not monic
               if ( p.isONE() ) {
                  G.clear(); G.add( p );
                  return G; // since no threads are activated
               }
               G.add( p ); //G.add( 0, p ); //reverse list
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

        Pair<C> pair;
        GenPolynomial<C> pi;
        GenPolynomial<C> pj;
        GenPolynomial<C> S;
        GenPolynomial<C> D;
        GenPolynomial<C> H;
        List<GenPolynomial<C>> bcH;
        //int len = G.size();
        //System.out.println("len = " + len);
        while ( pairlist.hasNext() ) {
              pair = pairlist.removeNext();
              //System.out.println("pair = " + pair);
              if ( pair == null ) continue; 

              pi = pair.pi; 
              pj = pair.pj; 
              if ( false && logger.isDebugEnabled() ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
              }

              // S-polynomial -----------------------
              if ( true || (pair.getUseCriterion3() && pair.getUseCriterion4()) ) {
                  S = red.SPolynomial( pi, pj );
                  //System.out.println("S_d = " + S);
                  if ( S.isZERO() ) {
                      pair.setZero();
                      continue;
                  }
                  if ( logger.isDebugEnabled() ) {
                      logger.debug("ht(S) = " + S.leadingExpVector() );
                  }

                  H = red.normalform( G, S );
                  if ( H.isZERO() ) {
                      pair.setZero();
                      continue;
                  }
                  if ( logger.isDebugEnabled() ) {
                      logger.debug("ht(H) = " + H.leadingExpVector() );
                  }

                  if ( H.isONE() ) {
                      G.clear(); G.add( H );
                      return G; // since no threads are activated
                  }
                  if ( logger.isDebugEnabled() ) {
                      logger.debug("H = " + H );
                  }
                  if ( !H.isZERO() ) {
                      logger.info("Sred = " + H);
                      //len = G.size();
                      l++;
                      bcH = red.reducedBooleanClosure(G,H);
                      G.addAll( bcH );
                      for ( GenPolynomial<C> h: bcH ) {
                          pairlist.put( h );
                      }
                  }
              }
        }
        logger.debug("#sequential list = " + G.size());
        G = minimalGB(G);
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
        return G;
    }

}
