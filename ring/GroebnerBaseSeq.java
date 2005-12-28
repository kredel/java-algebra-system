/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;

import edu.jas.ring.OrderedPairlist;

/**
 * Groebner Base sequential class.
 * Implements Groebner bases and GB test.
 * @author Heinz Kredel
 */

public class GroebnerBaseSeq<C extends RingElem<C>> 
       extends GroebnerBaseAbstract<C>  {

    private static final Logger logger = Logger.getLogger(GroebnerBaseSeq.class);


    /**
     * Constructor.
     */
    public GroebnerBaseSeq() {
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
        OrderedPairlist<C> pairlist = null; 
        int l = F.size();
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
        GenPolynomial<C> H;
        while ( pairlist.hasNext() ) {
              pair = pairlist.removeNext();
              if ( pair == null ) continue; 

              pi = pair.pi; 
              pj = pair.pj; 
              if ( false && logger.isDebugEnabled() ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
              }

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

              H = H.monic();
              if ( H.isONE() ) {
                  G.clear(); G.add( H );
                  return G; // since no threads are activated
              }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("H = " + H );
              }
              if ( H.length() > 0 ) {
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
