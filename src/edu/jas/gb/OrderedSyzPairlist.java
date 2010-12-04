/*
 * $Id$
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.structure.RingElem;

/**
 * Pair list management.
 * The Buchberger algorithm following 
 * the syzygy criterions by Gebauer &amp; M&ouml;ller in Reduce.
 * Implemented using GenPolynomial, TreeMap and BitSet.
 * @author Heinz Kredel
 */

public class OrderedSyzPairlist<C extends RingElem<C> > implements PairList<C> {

    protected final ArrayList<GenPolynomial<C>> P;
    protected final SortedMap<ExpVector,LinkedList<Pair<C>>> pairlist;
    protected final ArrayList<BitSet> red;
    protected final GenPolynomialRing<C> ring;
    protected final Reduction<C> reduction;
    protected boolean oneInGB = false;
    protected boolean useCriterion4 = true;
    protected int putCount;
    protected int remCount;
    protected final int moduleVars;

    private static final Logger logger = Logger.getLogger(OrderedSyzPairlist.class);


    /**
     * Constructor for OrderedPairlist.
     * @param r polynomial factory.
     */
    public OrderedSyzPairlist() {
        moduleVars = 0;
        ring = null;
        P = null;
        pairlist = null; 
        red = null;
        reduction = null;
        putCount = 0;
        remCount = 0;
    }


    /**
     * Constructor for OrderedPairlist.
     * @param r polynomial factory.
     */
    public OrderedSyzPairlist(GenPolynomialRing<C> r) {
        this(0,r);
    }


    /**
     * Constructor for OrderedPairlist.
     * @param m number of module variables.
     * @param r polynomial factory.
     */
    public OrderedSyzPairlist(int m, GenPolynomialRing<C> r) {
        moduleVars = m;
        ring = r;
        P = new ArrayList<GenPolynomial<C>>();
        pairlist = new TreeMap<ExpVector,LinkedList<Pair<C>>>( 
                                                              ring.tord.getAscendComparator() );
        //pairlist = new TreeMap( to.getSugarComparator() );
        red = new ArrayList<BitSet>();
        putCount = 0;
        remCount = 0;
        if ( !ring.isCommutative() ) { // instanceof GenSolvablePolynomialRing ) {
            useCriterion4 = false;
        }
        reduction = new ReductionSeq<C>();
    }


    /**
     * Create a new PairList.
     * @param r polynomial ring.
     */
    public PairList<C> create(GenPolynomialRing<C> r) {
        return new OrderedSyzPairlist<C>(r);
    }


    /**
     * Create a new PairList.
     * @param m number of module variables.
     * @param r polynomial ring.
     */
    public PairList<C> create(int m, GenPolynomialRing<C> r) {
        return new OrderedSyzPairlist<C>(m,r);
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("OrderedSyzPairlist(");
        //s.append("polys="+P.size());
        s.append("#put="+putCount);
        s.append(", #rem="+remCount);
        if ( pairlist != null && pairlist.size() != 0 ) {
            s.append(", size="+pairlist.size());
        }
        s.append(")");
        return s.toString();
    }


    /**
     * Put one Polynomial to the pairlist and reduction matrix.
     * @param p polynomial.
     * @return the index of the added polynomial.
     */
    public synchronized int put(GenPolynomial<C> p) { 
        putCount++;
        if ( oneInGB ) { 
            return P.size()-1;
        }
        ExpVector e = p.leadingExpVector();
        int ps = P.size();
        BitSet redi = new BitSet();
        //redi.set( 0, ps ); // [0..ps-1] = true
        red.add( redi ); // all zeros
        P.add(  p );
        // remove from existing pairs:
        List<ExpVector> es = new ArrayList<ExpVector>();
        for (Map.Entry<ExpVector,LinkedList<Pair<C>>> me : pairlist.entrySet()) {
            ExpVector g = me.getKey();
            if ( moduleVars > 0 ) {
                if ( !reduction.moduleCriterion( moduleVars, e, g) ) {
                    continue; // skip pair
                }
            }
            ExpVector ge = g.lcm(e);
            LinkedList<Pair<C>> ll = me.getValue();
            if (g.compareTo(ge) == 0) {
                LinkedList<Pair<C>> lle = new LinkedList<Pair<C>>();
                for (Pair<C> pair : ll) {
                    ExpVector eil = pair.pi.leadingExpVector().lcm(e);
                    if ( g.compareTo(eil) == 0 ) {
                        continue;
                    }
                    ExpVector ejl = pair.pj.leadingExpVector().lcm(e);
                    if ( g.compareTo(ejl) == 0 ) {
                        continue;
                    }
                    // g == ge && g != eil && g != ejl  
                    //System.out.println("to skip " + pair);
                    red.get( pair.j ).clear( pair.i ); 
                    lle.add(pair);
                }
                if ( lle.size() > 0 ) {
                    for ( Pair<C> pair : lle ) {
                        ll.remove(pair); 
                    }
                    if ( ! es.contains(g) ) {
                        es.add(g);
                    }
                }
            }
        }
        for ( ExpVector ei : es ) {
            LinkedList<Pair<C>> ll = pairlist.get(ei);
            if ( ll.size() == 0 ) {
                ll = pairlist.remove(ei);
                //System.out.println("removed empty for = " + ei);
            }
        }
        // generate new pairs:
        SortedMap<ExpVector,LinkedList<Pair<C>>> npl 
            = new TreeMap<ExpVector,LinkedList<Pair<C>>>( ring.tord.getAscendComparator() );
        for ( int j = 0; j < ps; j++ ) {
            GenPolynomial<C> pj = P.get(j);
            ExpVector f = pj.leadingExpVector(); 
            if ( moduleVars > 0 ) {
                if ( !reduction.moduleCriterion( moduleVars, e, f) ) {
                    //red.get( j ).clear(l); 
                    continue; // skip pair
                }
            }
            ExpVector g =  e.lcm( f );
            //System.out.println("g  = " + g);  
            Pair<C> pair = new Pair<C>( pj, p, j, ps);
            //System.out.println("pair.new      = " + pair);
            //multiple pairs under same keys -> list of pairs
            LinkedList<Pair<C>> xl = npl.get( g );
            if ( xl == null ) {
                xl = new LinkedList<Pair<C>>();
            }
            //xl.addLast( pair ); // first or last ?
            xl.addFirst( pair ); // first or last ? better for d- e-GBs
            npl.put( g, xl );
        }
        //System.out.println("npl.new      = " + npl.keySet());
        // skip by divisibility:
        es = new ArrayList<ExpVector>(npl.size());
        for ( ExpVector eil : npl.keySet() ) {
            for ( ExpVector ejl : npl.keySet() ) {
                if ( eil.compareTo(ejl) == 0 ) {
                    continue;
                }
                if ( eil.multipleOf(ejl) ) {
                    if ( !es.contains(eil) ) {
                        es.add(eil);
                    }
                }
            }
        }
        //System.out.println("npl.skip div = " + es);
        for ( ExpVector ei : es ) {
            LinkedList<Pair<C>> ignored = npl.remove(ei);
            //                for ( Pair<C> pair : ignored ) {
            //                    if ( red.get( pair.j ).get( pair.i ) ) {
            //                        System.out.println("reset for pair = " + pair); 
            //                        red.get( pair.j ).clear( pair.i ); 
            //                }
            //                }
        }
        // skip by criterion 4:
        if ( useCriterion4 ) {
            es = new ArrayList<ExpVector>(npl.size());
            for ( ExpVector ei : npl.keySet() ) {
                LinkedList<Pair<C>> exl = npl.get( ei );
                //System.out.println("exl = " + exl ); 
                boolean c = true;
                for ( Pair<C> pair : exl ) {
                    c = c && reduction.criterion4( pair.pi, pair.pj, pair.e ); 
                }
                //System.out.println("c4 = " + c ); 
                if ( c ) {
                    if ( exl.size() > 1 ) {
                        Pair<C> pair = exl.getFirst(); // or getLast()
                        exl.clear();
                        exl.add(pair);
                        //npl.put(ei,exl);
                    }
                } else {
                    if ( !es.contains(ei) ) {
                        es.add(ei);
                    }
                }
            }
            //System.out.println("npl.skip c4  = " + es);
            for ( ExpVector ei : es ) {
                LinkedList<Pair<C>> ignored = npl.remove(ei);
                //                    for ( Pair<C> pair : ignored ) {
                //                        if ( false && reduction.criterion4( pair.pi, pair.pj, pair.e ) ) {
                //                            continue;
                //                     }
                //                        //System.out.println("npl.skip c4  = " + pair.i + ", " + pair.j);
                //                        red.get( pair.j ).clear( pair.i ); 
                //                    }
            }
        }
        // add to existing pairlist:
        //System.out.println("npl.put new  = " + npl.keySet() );  
        for ( ExpVector ei : npl.keySet() ) {
            LinkedList<Pair<C>> exl = npl.get( ei );
            for ( Pair<C> pair : exl ) {
                red.get( pair.j ).set( pair.i ); 
            }
            LinkedList<Pair<C>> ex = pairlist.get( ei );
            if ( ex != null ) {
                //System.out.println("pairlist.add_ex = " + ex ); 
                exl.addAll(ex); 
                //for ( Pair<C> ep : ex ) {
                //    exl.addFirst(ep);
                //}
            }
            //System.out.println("pairlist.add = " + ei + ", exl = " + exl); 
            pairlist.put(ei,exl);
        }
        //System.out.println("pairlist.set = " + red); //.get( pair.j )); //pair);
        //System.out.println("pairlist.key = " + pairlist.keySet() );  
        return P.size()-1;
    }


    /**
     * Remove the next required pair from the pairlist and reduction matrix.
     * Appy the criterions 3 and 4 to see if the S-polynomial is required.
     * @return the next pair if one exists, otherwise null.
     */
    public synchronized Pair<C> removeNext() { 
        if ( oneInGB ) {
            return null;
        }
        Iterator< Map.Entry<ExpVector,LinkedList<Pair<C>>> > ip 
            = pairlist.entrySet().iterator();
        Pair<C> pair = null;
        boolean c = false;
        int i, j;
        while ( /*!c &&*/ ip.hasNext() )  {
            Map.Entry<ExpVector,LinkedList<Pair<C>>> me = ip.next();
            ExpVector g =  me.getKey();
            LinkedList<Pair<C>> xl = me.getValue();
            if ( logger.isInfoEnabled() )
                logger.info("g  = " + g);
            pair = null;
            while ( /*!c &&*/ xl.size() > 0 ) {
                pair = xl.removeFirst();
                // xl is also modified in pairlist 
                i = pair.i; 
                j = pair.j; 
                //System.out.println("pair.remove = " + pair );
                if ( !red.get(j).get(i) ) {
                    System.out.println("c_red.get(j).get(i) = " + g); // + ", " + red.get(j).get(i)); 
                    pair = null;
                    continue;
                    //break;
                }
                red.get(j).clear(i);
                break; 
            }
            if ( xl.size() == 0 ) {
                ip.remove(); 
                // = pairlist.remove( g );
            }
            if ( pair != null ) {
                break;
            }
        }
        remCount++; // count only real pairs
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
    public ArrayList<GenPolynomial<C>> getList() { 
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
    public synchronized int putOne(GenPolynomial<C> one) { 
        if ( one == null ) {
            return P.size()-1;
        }
        if ( ! one.isONE() ) {
            return P.size()-1;
        }
        return putOne();
    }


    /**
     * Put to ONE-Polynomial to the pairlist.
     * @param one polynomial. (no more required)
     * @return the index of the last polynomial.
     */
    public synchronized int putOne() { 
        putCount++;
        oneInGB = true;
        pairlist.clear();
        P.clear();
        P.add(ring.getONE());
        red.clear();
        return P.size()-1;
    }


    /**
     * GB criterium 3.
     * @return true if the S-polynomial(i,j) is required.
     */
    public boolean criterion3(int i, int j, ExpVector eij) {  
	throw new UnsupportedOperationException("not used in " + this.getClass().getName());
    }

}
