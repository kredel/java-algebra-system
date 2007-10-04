/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

//import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;

import edu.jas.ring.OrderedPairlist;

/**
 * D-Groebner Base sequential algorithm.
 * Implements D-Groebner bases and GB test.
 * @author Heinz Kredel
 */

public class DGroebnerBaseSeq<C extends RingElem<C>> 
       extends GroebnerBaseAbstract<C>  {


    private static final Logger logger = Logger.getLogger(DGroebnerBaseSeq.class);


    /**
     * Reduction engine.
     */
    protected DReduction<C> red;  // shadow super.red


    /**
     * Constructor.
     */
    public DGroebnerBaseSeq() {
        this( new DReductionSeq<C>() );
    }


    /**
     * Constructor.
     * @param red D-Reduction engine
     */
    public DGroebnerBaseSeq(DReduction<C> red) {
        super(red);
        this.red = red;
    }


    /**
     * D-Groebner base test.
     * @typeparam C coefficient type.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a D-Groebner base, else false.
     */
    public boolean isGB(int modv, List<GenPolynomial<C>> F) {  
        GenPolynomial<C> pi, pj, s, h, d;
        for ( int i = 0; i < F.size(); i++ ) {
            pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                   continue;
                }
                d = red.DPolynomial( pi, pj );
                if ( d.isZERO() ) {
                   continue;
                }
                h = red.normalform( F, d );
                // check for top reduction only
                if ( ! h.isZERO() ) {
                   System.out.println("d-pol != 0: " + h);
                   return false;
                }
                //if ( ! red.criterion4( pi, pj ) ) { 
                //   continue;
                //}
                s = red.SPolynomial( pi, pj );
                if ( s.isZERO() ) {
                   continue;
                }
                h = red.normalform( F, s );
                if ( ! h.isZERO() ) {
                   System.out.println("s-pol != 0: " + h);
                   return false;
                }
            }
        }
        return true;
    }


    /**
     * D-Groebner base using pairlist class.
     * @typeparam C coefficient type.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a D-Groebner base of F.
     */
    public List<GenPolynomial<C>> 
             GB( int modv, 
                 List<GenPolynomial<C>> F ) {  
        //throw new RuntimeException("not jet implemented");
        GenPolynomial<C> p;
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        OrderedPairlist<C> pairlist = null; 
        int l = F.size();
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( !p.isZERO() ) {
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

        Pair<C> pair;
        GenPolynomial<C> pi;
        GenPolynomial<C> pj;
        GenPolynomial<C> S;
        GenPolynomial<C> D;
        GenPolynomial<C> H;
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

              // D-polynomial case ----------------------
              D = red.DPolynomial( pi, pj );
              if ( ! D.isZERO() ) {
                 H = red.normalform( G, D );
                 if ( H.isONE() ) {
                    G.clear(); G.add( H );
                    return G; // since no threads are activated
                 }
                 if ( !H.isZERO() && !H.equals(D) ) {
                    l++;
                    G.add( H );
                    pairlist.put( H );
                 }
              }

              // S-polynomial case -----------------------
              S = red.SPolynomial( pi, pj );
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
              if ( !H.isZERO() && !H.equals(S) ) {
                 l++;
                 G.add( H );
                 pairlist.put( H );
              }
        }
        logger.debug("#sequential list = "+G.size());
        G = minimalGB(G);
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
        return G;
    }

}
