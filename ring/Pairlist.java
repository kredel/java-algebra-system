/*
 * $Id$
 * log: Pairlist.java,v 1.4 2003/10/26 kredel Exp 
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

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.RatPolynomial;

/**
 * Pair list management
 */

public class Pairlist {

    private ArrayList P;
    private TreeMap pairlist;
    private ArrayList red;

    private static Logger logger = Logger.getLogger(Pairlist.class);

    public Pairlist(Comparator lorder) {
         P = new ArrayList();
         pairlist = new TreeMap(lorder);
         red = new ArrayList();
    }

    class Pair {
	public final RatPolynomial pi;
	public final RatPolynomial pj;
	public final int i;
	public final int j;

	Pair(Object a, RatPolynomial b, int i, int j) {
	    pi = (RatPolynomial)a; pj = b; 
            this.i = i; this.j = j;
	}
    }

    public synchronized void put(RatPolynomial p) { 
           Pair pair;
           ExpVector e; 
           ExpVector f; 
           ExpVector g; 
	   BitSet redi;
           Object x;
           LinkedList xl;
           e = RatPolynomial.DIRPEV( p );
	   int l = P.size();
           for ( int j = 0; j < l; j++ ) {
               f = RatPolynomial.DIRPEV( (RatPolynomial)P.get(j) ); 
               g = ExpVector.EVLCM( e, f );
               pair = new Pair( P.get(j), p, j, l);
	       // redi = (BitSet)red.get(j);
	       ///if ( j < l ) redi.set( l );
	       // System.out.println("bitset."+j+" = " + redi );  

	       //multiple pairs under same keys -> list of pairs
               x = pairlist.get( (Object) g );
               if ( x == null ) xl = new LinkedList();
	          else xl = (LinkedList) x; 

               xl.addLast( (Object) pair); // first or last ?
               pairlist.put( (Object) g, (Object) xl );
	   }
	   // System.out.println("pairlist.keys@put = " + pairlist.keySet() );  
           P.add( (Object) p );
	   redi = new BitSet();
           if ( l > 0 ) { // redi.set( 0, l ) jdk 1.4
	       for ( int i=0; i<l; i++ ) redi.set(i);
	   }
	   red.add( redi );
    }


    public synchronized Pair removeNext() { 
	//System.out.println("pairlist.keys@remove = " + pairlist.keySet() );  

       Set pk = pairlist.entrySet();
       Iterator ip = pk.iterator();

       Pair pair = null;
       boolean c = false;
       int i, j;

       while ( !c & ip.hasNext() )  {

           Map.Entry me = (Map.Entry) ip.next();

           ExpVector g = (ExpVector) me.getKey();
           LinkedList xl =(LinkedList) me.getValue();
           if ( logger.isInfoEnabled() )
	      logger.info("g  = " + g);

	   while ( !c & xl.size() > 0 ) {
                 pair = (Pair) xl.removeFirst();
                 // xl is also modified in pairlist 
                 i = pair.i; 
                 j = pair.j; 
                 //System.out.println("pair(" + j + "," +i+") ");
                 c = RatGBase.DIGBC4( pair.pi, pair.pj, g ); 
                 //System.out.println("c4  = " + c);  
		 if ( c ) {
                    c = DIGBC3( i, j, g );
                    //System.out.println("c3  = " + c); 
		 }
                 ((BitSet)red.get( j )).clear(i); // set(i,false) jdk1.4
	   }
           if ( xl.size() == 0 ) ip.remove(); 
              // = pairlist.remove( g );
       }
       return pair; 
    }

    public boolean hasNext() { 
          return pairlist.size() > 0;
    }


    /**
     * GB criterium 3.
     * @return true if the S-polynomial(i,j) is required.
     */

    public boolean DIGBC3(int i, int j, ExpVector eij) {  
	// assert i < j;
	boolean s;
        s = ((BitSet)red.get( j )).get(i); 
	if ( ! s ) { 
           logger.warn("c3.s false for " + j + " " + i); 
           return s;
	}
	s = true;
	boolean m;
	RatPolynomial A;
        ExpVector ek;
	for ( int k = 0; k < P.size(); k++ ) {
	    A = (RatPolynomial) P.get( k );
            ek = RatPolynomial.DIRPEV(A);
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
