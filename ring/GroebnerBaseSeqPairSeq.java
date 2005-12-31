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


/**
 * Groebner Base sequential class.
 * Implements Groebner bases and GB test.
 * @author Heinz Kredel
 */

public class GroebnerBaseSeqPairSeq<C extends RingElem<C>> 
       extends GroebnerBaseAbstract<C>  {

    private static final Logger logger = Logger.getLogger(GroebnerBaseSeqPairSeq.class);


    /**
     * Constructor.
     */
    public GroebnerBaseSeqPairSeq() {
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
              if ( true || logger.isDebugEnabled() ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
              }

              S = red.SPolynomial( pi, pj );
              if ( S.isZERO() ) {
                 pairlist.update( pair, S );
                 continue;
              }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
              }

              H = red.normalform( G, S );
              if ( H.isZERO() ) {
                 pairlist.update( pair, H );
                 continue;
              }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
              }

              H = H.monic();
              if ( H.isONE() ) {
                  // pairlist.record( pair, H );
                 G.clear(); G.add( H );
                 return G; // since no threads are activated
              }
              if ( logger.isDebugEnabled() ) {
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

}
