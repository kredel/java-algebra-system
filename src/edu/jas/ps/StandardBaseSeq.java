/*
 * $Id$
 */

package edu.jas.ps;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

//import edu.jas.poly.ExpVector;
//import edu.jas.poly.GenPolynomial;
//import edu.jas.poly.GenPolynomialRing;

import edu.jas.ps.ReductionSeq;
import edu.jas.ps.Pair;
import edu.jas.ps.OrderedPairlist;
import edu.jas.ps.MultiVarPowerSeries;
import edu.jas.ps.MultiVarPowerSeriesRing;


/**
 * Dtandard Base sequential algorithm.
 * Implements Standard bases and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class StandardBaseSeq<C extends RingElem<C>> 
				       /*extends StandardBaseAbstract<C>*/  {

    private static final Logger logger = Logger.getLogger(StandardBaseSeq.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Reduction engine.
     */
    public final ReductionSeq<C> red;


    /**
     * Constructor.
     */
    public StandardBaseSeq() {
        //super();
	this( new ReductionSeq<C>() );
    }


    /**
     * Constructor.
     * @param red Reduction engine
     */
    public StandardBaseSeq(ReductionSeq<C> red) {
        this.red = red; //super(red);
    }


    /**
     * Standard base using pairlist class.
     * @param F polynomial list.
     * @return STD(F) a Standard base of F.
     */
    public List<MultiVarPowerSeries<C>> STD( List<MultiVarPowerSeries<C>> F ) {  
        return STD(0,F);
    }



    /**
     * Standard base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return STD(F) a Standard base of F.
     */
    public List<MultiVarPowerSeries<C>> STD( int modv, List<MultiVarPowerSeries<C>> F ) {  
        MultiVarPowerSeries<C> p;
        List<MultiVarPowerSeries<C>> G = new ArrayList<MultiVarPowerSeries<C>>();
        OrderedPairlist<C> pairlist = null; 
        int l = F.size();
        ListIterator<MultiVarPowerSeries<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( p.truncate() > 0 ) {
		//p = p.monic();
               if ( p.isONE() ) {
                  G.clear(); G.add( p );
                  return G; // since no threads are activated
               }
               G.add( p );
               if ( pairlist == null ) {
                  pairlist = new OrderedPairlist<C>( modv, p.ring );
                  if ( ! p.ring.coFac.isField() ) {
                     throw new IllegalArgumentException("coefficients not from a field");
                  }
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
        MultiVarPowerSeries<C> pi;
        MultiVarPowerSeries<C> pj;
        MultiVarPowerSeries<C> S;
        MultiVarPowerSeries<C> H;
        while ( pairlist.hasNext() ) {
              pair = pairlist.removeNext();
              //logger.debug("pair = " + pair);
              if ( pair == null ) {
                  continue; 
              }
              pi = pair.pi; 
              pj = pair.pj; 
              if ( /*false &&*/ debug ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
              }

              S = red.SPolynomial( pi, pj );
              if ( S.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( logger.isInfoEnabled() ) {
                  logger.info("ht(S) = " + S.orderExpVector() );
              }

              H = red.normalform( G, S );
              if ( H.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( logger.isInfoEnabled() ) {
                  logger.info("ht(H) = " + H.orderExpVector() );
              }

              //H = H.monic();
              if ( H.isONE() ) {
                  G.clear(); G.add( H );
                  return G; // since no threads are activated
              }
              if ( logger.isInfoEnabled() ) {
                 logger.info("H = " + H );
              }
              if ( ! H.isZERO() ) {
                 l++;
                 G.add( H );
                 pairlist.put( H );
              }
        }
        logger.debug("#sequential list = "+G.size());
        //G = minimalGB(G);
        logger.info("" + pairlist); 
        return G;
    }

}
