/*
 * $Id$
 */

package edu.jas.application;


import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.ColorPolynomial;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomialRing;

import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;


/**
 * Pair list management.
 * Implemented using ColorPolynomial, TreeMap and BitSet.
 * @author Heinz Kredel
 */

public class OrderedCPairlist<C extends GcdRingElem<C> > 
             implements Serializable, Cloneable {


    protected final GenPolynomialRing<GenPolynomial<C>> ring;


    protected final List<ColorPolynomial<C>> P;


    protected final SortedMap<ExpVector,LinkedList<CPair<C>>> pairlist;


    protected final List<BitSet> red;


    protected final CReductionSeq<C> reduction;


    protected boolean oneInGB = false;


    protected boolean useCriterion4 = false; //unused


    protected int putCount;


    protected int remCount;


    protected final int moduleVars; // unused


    private static final Logger logger = Logger.getLogger(OrderedCPairlist.class);


    /**
     * Constructor for OrderedPairlist.
     * @param r polynomial factory.
     */
    public OrderedCPairlist(GenPolynomialRing<GenPolynomial<C>> r) {
        this(0,r);
    }


    /**
     * Constructor for OrderedPairlist.
     * @param m number of module variables.
     * @param r polynomial factory.
     */
    public OrderedCPairlist(int m, GenPolynomialRing<GenPolynomial<C>> r) {
         moduleVars = m;
         ring = r;
         P = new ArrayList<ColorPolynomial<C>>();
         pairlist = new TreeMap<ExpVector,LinkedList<CPair<C>>>( 
                               ring.tord.getAscendComparator() );
         //pairlist = new TreeMap( to.getSugarComparator() );
         red = new ArrayList<BitSet>();
         putCount = 0;
         remCount = 0;
         if ( ring instanceof GenSolvablePolynomialRing ) {
            useCriterion4 = false;
         }
         reduction = new CReductionSeq<C>();
    }


    /**
     * Constructor for OrderedPairlist.
     * @param m number of module variables.
     * @param r polynomial factory.
     */
    private OrderedCPairlist(int m, 
                             GenPolynomialRing<GenPolynomial<C>> ring,
                             List<ColorPolynomial<C>> P,
                             SortedMap<ExpVector,LinkedList<CPair<C>>> pl,
                             List<BitSet> red,
                             CReductionSeq<C> cred,
                             int pc,
                             int rc 
                             ) {
        moduleVars =m;
        this.ring = ring;
        this.P = P;
        pairlist = pl;
        this.red = red;
        reduction = cred;
        putCount = pc;
        remCount = rc;
    }


    /**
     * Clone this OrderedPairlist.
     * @return a 2 level clone of this.
     */
    @Override
    public OrderedCPairlist<C> clone() {
        return new OrderedCPairlist<C>(
                          moduleVars,
                          ring,
                          new ArrayList<ColorPolynomial<C>>( P ),
                          clonePairlist(),
                          cloneBitSet(),
                          reduction,
                          putCount,
                          remCount);
    }


    /**
     * Clone this pairlist.
     * @return a 2 level clone of this pairlist.
     */
    private SortedMap<ExpVector,LinkedList<CPair<C>>> clonePairlist() {
        SortedMap<ExpVector,LinkedList<CPair<C>>> pl 
            = new TreeMap<ExpVector,LinkedList<CPair<C>>>( ring.tord.getAscendComparator() );
        for ( Map.Entry<ExpVector,LinkedList<CPair<C>>> m : pairlist.entrySet() ) {
              ExpVector e = m.getKey();
              LinkedList<CPair<C>> l = m.getValue();
              l = new LinkedList<CPair<C>>( l );
              pl.put(e,l);
        }
        return pl;
    }


    /**
     * pairCount.
     * @return number of pairs in this pairlist.
     */
    public int pairCount() {
        int c = 0;
        for ( Map.Entry<ExpVector,LinkedList<CPair<C>>> m : pairlist.entrySet() ) {
              LinkedList<CPair<C>> l = m.getValue();
              c += l.size();
        }
        return c;
    }
                             

    /**
     * Clone this reduction BitSet.
     * @return a 2 level clone of this reduction BitSet.
     */
    private List<BitSet> cloneBitSet() {
        List<BitSet> r = new ArrayList<BitSet>( this.red.size() );
        for ( BitSet b : red ) {
            BitSet n = (BitSet) b.clone();
            r.add( n );
        }
        return r;
    }


    /**
     * bitCount.
     * @return number of set bits in this bitset.
     */
    public int bitCount() {
        int c = 0;
        for ( BitSet b : red ) {
            c += b.cardinality();
        }
        return c;
    }


    /**
     * toString.
     * @return counters of this.
     */
    @Override
    public String toString() {
        int p = pairCount();
        int b = bitCount();
        if ( p != b ) {
           return "OrderedCPairlist( pairCount=" + p
                + ", bitCount=" + b 
                + ", putCount=" + putCount 
                + ", remCount=" + remCount 
                + " )";
        } else {
           return "OrderedCPairlist( pairCount=" + p
                + ", putCount=" + putCount 
                + ", remCount=" + remCount 
                + " )";
        }
    }


    /**
     * Put one Polynomial to the pairlist and reduction matrix.
     * @param p polynomial.
     * @return the index of the added polynomial.
     */
    public synchronized int put(ColorPolynomial<C> p) { 
           putCount++;
           if ( oneInGB ) { 
               return P.size()-1;
           }
           CPair<C> pair;
           ExpVector e; 
           ExpVector f; 
           ExpVector g; 
           ColorPolynomial<C> pj; 
           BitSet redi;
           LinkedList<CPair<C>> x;
           LinkedList<CPair<C>> xl;
           e = p.leadingExpVector();
           //System.out.println("p = " + p);
           int l = P.size();
           for ( int j = 0; j < l; j++ ) {
               pj = P.get(j);
               //System.out.println("pj = " + pj);
               f = pj.leadingExpVector(); 
               if ( moduleVars > 0 ) {
                   if ( ExpVector.EVILCP( e, f, 0, moduleVars ) != 0 ) {
                       continue; // skip pair
                   }
               }
               //System.out.println("e = " + e + ", f = " + f);
               g = ExpVector.EVLCM( e, f );
               pair = new CPair<C>( pj, p, j, l);
               // redi = (BitSet)red.get(j);
               ///if ( j < l ) redi.set( l );
               // System.out.println("bitset."+j+" = " + redi );  

               //multiple pairs under same keys -> list of pairs
               x = pairlist.get( g );
               if ( x == null ) {
                  xl = new LinkedList<CPair<C>>();
               } else {
                  xl = x; 
               }
               //xl.addLast( pair ); // first or last ?
               xl.addFirst( pair ); // first or last ? better for d- e-GBs
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
           return P.size()-1;
    }


    /**
     * Remove the next required pair from the pairlist and reduction matrix.
     * Appy the criterions 3 and 4 to see if the S-polynomial is required.
     * @return the next pair if one exists, otherwise null.
     */
    public synchronized CPair<C> removeNext() { 
       if ( oneInGB ) {
          return null;
       }
       Iterator< Map.Entry<ExpVector,LinkedList<CPair<C>>> > ip 
             = pairlist.entrySet().iterator();

       CPair<C> pair = null;
       boolean c = false;
       int i, j;

       while ( !c && ip.hasNext() )  {
           Map.Entry<ExpVector,LinkedList<CPair<C>>> me = ip.next();
           ExpVector g =  me.getKey();
           LinkedList<CPair<C>> xl = me.getValue();
           if ( logger.isInfoEnabled() )
              logger.info("g  = " + g);
           pair = null;
           while ( !c && xl.size() > 0 ) {
                 pair = xl.removeFirst();
                 // xl is also modified in pairlist 
                 i = pair.i; 
                 j = pair.j; 
                 // System.out.println("pair(" + j + "," +i+") ");
                 if ( useCriterion4 ) {
                    // c = reduction.criterion4( pair.pi, pair.pj, g ); 
                    c = true;
                 } else {
                    c = true;
                 }
                 //System.out.println("c4  = " + c);  
                 if ( c ) {
                    //c = criterion3( i, j, g );
                    //System.out.println("c3  = " + c); 
                 }
                 red.get( j ).clear(i); // set(i,false) jdk1.4
           }
           if ( xl.size() == 0 ) ip.remove(); 
              // = pairlist.remove( g );
       }
       if ( ! c ) {
          pair = null;
       } else {
          remCount++; // count only real pairs
       }
       return pair; 
    }


    /**
     * Test if there is possibly a pair in the list.
     * @return true if a next pair could exist, otherwise false.
     */
    public boolean hasNext() { 
          return pairlist.size() > 0;
    }


    /**
     * Get the list of polynomials.
     * @return the polynomial list.
     */
    public List<ColorPolynomial<C>> getList() { 
          return P;
    }


    /**
     * Get the number of polynomials put to the pairlist.
     * @return the number of calls to put.
     */
    public int putCount() { 
          return putCount;
    }


    /**
     * Get the number of required pairs removed from the pairlist.
     * @return the number of non null pairs delivered.
     */
    public int remCount() { 
          return remCount;
    }


    /**
     * Put to ONE-Polynomial to the pairlist.
     * @param one polynomial. (no more required)
     * @return the index of the last polynomial.
     */
    public synchronized int putOne(ColorPolynomial<C> one) { 
        putCount++;
        if ( one == null ) {
           return P.size()-1;
        }
        if ( ! one.isONE() ) {
           return P.size()-1;
        }
        oneInGB = true;
        pairlist.clear();
        P.clear();
        P.add(one);
        red.clear();
        return P.size()-1;
    }


    /**
     * GB criterium 3.
     * @return true if the S-polynomial(i,j) is required.
     */
    public boolean criterion3(int i, int j, ExpVector eij) {  
        // assert i < j;
        boolean s;
        s = red.get( j ).get(i); 
        if ( ! s ) { 
           logger.warn("c3.s false for " + j + " " + i); 
           return s;
        }
        s = true;
        boolean m;
        ColorPolynomial<C> A;
        ExpVector ek;
        for ( int k = 0; k < P.size(); k++ ) {
            A = P.get( k );
            ek = A.leadingExpVector();
            m = ExpVector.EVMT(eij,ek);
            if ( m ) {
                if ( k < i ) {
                   // System.out.println("k < i "+k+" "+i); 
                   s =    red.get( i ).get(k) 
                       || red.get( j ).get(k); 
                }
                if ( i < k && k < j ) {
                   // System.out.println("i < k < j "+i+" "+k+" "+j); 
                   s =    red.get( k ).get(i) 
                       || red.get( j ).get(k); 
                }
                if ( j < k ) {
                    //System.out.println("j < k "+j+" "+k); 
                   s =    red.get( k ).get(i) 
                       || red.get( k ).get(j); 
                }
                //System.out.println("s."+k+" = " + s); 
                if ( ! s ) return s;
            }
        }
        return true;
    }
}

