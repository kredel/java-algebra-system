/*
 * $Id$
 */

package edu.jas.ring;

import java.util.TreeMap;
import java.util.Comparator;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.BitSet;
import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.TermOrder;

import edu.jas.ring.Reduction;

/**
 * Pair list management.
 * Implemented using OrderedPolynomial, TreeMap and BitSet.
 * @author Heinz Kredel
 */

public class OrderedPairlist {

    private final ArrayList P;
    private final TreeMap pairlist;
    private final ArrayList red;
    private final TermOrder torder;
    private boolean oneInGB = false;
    private int putCount;
    private int remCount;
    private int pairCount;
    private int lastPair;
    private final TreeMap pairsequence;

    private static Logger logger = Logger.getLogger(OrderedPairlist.class);

    /**
     * Constructor for OrderedPairlist
     */

  public OrderedPairlist(TermOrder to) {
         torder = to;
         P = new ArrayList();
         pairlist = new TreeMap( to.getAscendComparator() );
         red = new ArrayList();
	 putCount = 0;
	 remCount = 0;
	 pairCount = 0;
	 lastPair = 0;
         pairsequence = new TreeMap( /* default order */ );
  }


    /**
     * Put parallel one Polynomial to the pairlist and 
     * reduction matrix, record the orginating Pair / null
     */

  public synchronized void putParallel(OrderedPolynomial p, Pair org) { 
         if ( org == null ) {
             put( p, org );
         } else {
            if ( logger.isInfoEnabled() ) {
               logger.info("pa = " + org);
            }
            pairsequence.put( org, p ); // needed to set polynomial
            removeZeros();
            removeAndPut();
         }
  }


  public /*synchronized elsewhere*/ void removeZeros() { 
       if ( pairsequence.size() == 0 ) {
           return;
       }
       Pair k = (Pair)pairsequence.firstKey();
       while ( (k != null) && k.isZero() ) {
             logger.info("k0 = " + k);
             lastPair = k.getPairNumber();
             pairsequence.remove( k );
             if ( pairsequence.size() > 0 ) {
                k = (Pair)pairsequence.firstKey();
             } else {
                k = null;
             }
       }
       logger.info("k- = " + k);
  }


  public /*synchronized elsewhere*/ void removeAndPut() { 
       if ( pairsequence.size() == 0 ) {
           return;
       }
       Pair k = (Pair)pairsequence.firstKey();
       while ( (k != null) && (k.getPairNumber() <= lastPair+1) 
             ) {
           logger.info("k1 = " + k);
           OrderedPolynomial q = (OrderedPolynomial)pairsequence.get( k );
           if ( q != null ) {
              put( q, k );
              lastPair = k.getPairNumber();
              pairsequence.remove( k );
           } else {
              break;
           }
           if ( pairsequence.size() > 0 ) {
              k = (Pair)pairsequence.firstKey();
           } else {
              k = null;
           }
       } 
       logger.info("k+ = " + k);
  }


    /**
     * remove the next required pair from the pairlist and 
     * reduction matrix
     * appy the criterions 3 and 4 to see if the S-polynomial 
     * is required
     */

  public synchronized Pair removeNextParallel() { 
      Pair pair = null;
      if ( pairlist.size() == 0 ) {
         logger.info("rnp");
         removeZeros();
         removeAndPut();
      }
      pair = removeNext();
      if ( pair != null ) {
           pairCount++;
           pair.pairNumber( pairCount );
           pairsequence.put(pair,null);
      }
      return pair;
  }


    /**
     * Put one Polynomial to the pairlist and reduction matrix
     * record the orginating Pair / null
     */

    public /* synchronized elsewhere*/ void put(OrderedPolynomial p, Pair org) { 
	 putCount++;
         if ( org != null ) {
            if ( logger.isInfoEnabled() ) {
               logger.info("or = " + org);
            }
         }
         if ( oneInGB ) return;
         Pair pair;
         ExpVector e; 
         ExpVector f; 
         ExpVector g; 
         OrderedPolynomial pj; 
         BitSet redi;
         Object x;
         LinkedList xl;
         e = p.leadingExpVector();
         int l = P.size();
         for ( int j = 0; j < l; j++ ) {
             pj = (OrderedPolynomial) P.get(j);
             f = pj.leadingExpVector(); 
             g = ExpVector.EVLCM( e, f );
             pair = new Pair( pj, p, j, l);
             // redi = (BitSet)red.get(j);
             ///if ( j < l ) redi.set( l );
             // System.out.println("bitset."+j+" = " + redi );  

             //multiple pairs under same keys -> list of pairs
             x = pairlist.get( g );
             if ( x == null ) {
                xl = new LinkedList();
             } else {
                xl = (LinkedList) x; 
             }
             //xl.addLast( pair ); // first or last ?
             xl.addFirst( pair ); // first or last ?
             pairlist.put( g, xl );
         }
         // System.out.println("pairlist.keys@put = " + pairlist.keySet() );  
         P.add(  p );
         redi = new BitSet();
         redi.set( 0, l ); // jdk 1.4
	 // if ( l > 0 ) { // jdk 1.3
         //    for ( int i=0; i<l; i++ ) redi.set(i);
         // }
         red.add( redi );
  }


    /**
     * remove the next required pair from the pairlist and 
     * reduction matrix
     * appy the criterions 3 and 4 to see if the S-polynomial 
     * is required
     */

    public /*synchronized elsewhere*/ Pair removeNext() { 
       remCount++;
       if ( oneInGB ) return null;
       Set pk = pairlist.entrySet();
       Iterator ip = pk.iterator();

       Pair pair = null;
       boolean c = false;
       int i, j;

       while ( !c & ip.hasNext() )  {

           Map.Entry me = (Map.Entry) ip.next();

           ExpVector g = (ExpVector) me.getKey();
           LinkedList xl =(LinkedList) me.getValue();
           if ( logger.isInfoEnabled() ) {
              logger.info("g  = " + g);
           }
           pair = null;

           while ( !c & xl.size() > 0 ) {
                 pair = (Pair) xl.removeFirst();
                 // xl is also modified in pairlist 
                 i = pair.i; 
                 j = pair.j; 
                 //System.out.println("pair(" + j + "," +i+") ");
                 c = Reduction.GBCriterion4( pair.pi, pair.pj, g ); 
                 //System.out.println("c4  = " + c);  
                 if ( ! c ) { 
                     pair.setZero();
                 }
                 if ( c ) {
                    c = GBCriterion3( i, j, g );
                    //System.out.println("c3  = " + c); 
                    if ( ! c ) { 
                       pair.setZero();
                    }
                 }
                 ((BitSet)red.get( j )).clear(i); // set(i,false) jdk1.4
           }
           if ( xl.size() == 0 ) ip.remove(); 
              // = pairlist.remove( g );
       }
       if ( ! c ) { 
          pair = null;
       } 
       return pair; 
  }


    /**
     * Test if there is possibly a pair in the list
     */

  public boolean hasNext() { 
      return (pairlist.size() > 0) || (pairsequence.size() > 0);
  }


    /**
     * Get the list of polynomials
     */

  public ArrayList getList() { 
        return P;
  }


    /**
     * Get the number of polynomials put to the pairlist
     */

  public int putCount() { 
       return putCount;
  }


    /**
     * Get the number of required pairs removed from the pairlist
     */

  public int remCount() { 
       return remCount;
  }


    /**
     * Get the total number of pairs generated
     */

  public int pairCount() { 
       return pairCount;
  }


    /**
     * Put to ONE-Polynomial to the pairlist
     */

  public synchronized void putOne(OrderedPolynomial one, Pair org) { 
        putCount++;
        if ( one == null ) return;
        if ( ! one.isONE() ) return;
        oneInGB = true;
        pairlist.clear();
        P.clear();
        P.add(one);
        red.clear();
  }


    /**
     * GB criterium 3.
     * @return true if the S-polynomial(i,j) is required.
     */

  public boolean GBCriterion3(int i, int j, ExpVector eij) {  
        // assert i < j;
        boolean s;
        s = ((BitSet)red.get( j )).get(i); 
        if ( ! s ) { 
           logger.warn("c3.s false for " + j + " " + i); 
           return s;
        }
        s = true;
        boolean m;
        OrderedPolynomial A;
        ExpVector ek;
        for ( int k = 0; k < P.size(); k++ ) {
            A = (OrderedPolynomial) P.get( k );
            ek = A.leadingExpVector();
            m = ExpVector.EVMT(eij,ek);
            if ( m ) {
                if ( k < i ) {
                   // System.out.println("k < i "+k+" "+i); 
                   s =    ((BitSet)red.get( i )).get(k) 
                       || ((BitSet)red.get( j )).get(k); 
                }
                if ( i < k && k < j ) {
                   // System.out.println("i < k < j "+i+" "+k+" "+j); 
                   s =    ((BitSet)red.get( k )).get(i) 
                       || ((BitSet)red.get( j )).get(k); 
                }
                if ( j < k ) {
                    //System.out.println("j < k "+j+" "+k); 
                   s =    ((BitSet)red.get( k )).get(i) 
                       || ((BitSet)red.get( k )).get(j); 
                }
                //System.out.println("s."+k+" = " + s); 
                if ( ! s ) return s;
            }
        }
      return true;
  }
}

