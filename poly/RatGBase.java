/*
 * $Id$
 */

package edu.jas.poly;

import java.util.TreeMap;
import java.util.Comparator;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Iterator;

import edu.jas.arith.BigRational;
import edu.jas.ThreadPool;
import edu.jas.Semaphore;

public class RatGBase  {

    public static RatPolynomial DIRPSP(RatPolynomial Ap, RatPolynomial Bp) {  
        if ( Ap.evord != Bp.evord ) { System.out.println("ERROR"); }

        Map.Entry ma = RatPolynomial.DIRPLM(Ap);
        Map.Entry mb = RatPolynomial.DIRPLM(Bp);

        ExpVector e = (ExpVector) ma.getKey();
        ExpVector f = (ExpVector) mb.getKey();

        ExpVector g = ExpVector.EVLCM(e,f);
        ExpVector e1 = ExpVector.EVDIF(g,e);
        ExpVector f1 = ExpVector.EVDIF(g,f);

        BigRational a = (BigRational) ma.getValue();
        BigRational b = (BigRational) mb.getValue();

        RatPolynomial App = new RatPolynomial( b, e1 );
        RatPolynomial Bpp = new RatPolynomial( a, f1 );

        App = RatPolynomial.DIRPPR( Ap, App );
        Bpp = RatPolynomial.DIRPPR( Bp, Bpp );
       
        RatPolynomial Cp = RatPolynomial.DIRPDF( App, Bpp );
        return Cp;
    }


    /**
     * GB criterium 4.
     * @returns true if the S-polynomial(i,j) is required.
     */

    public static boolean DIGBC4(RatPolynomial A, 
                                 RatPolynomial B, 
                                 ExpVector e) {  
        if ( A.evord != B.evord ) { System.out.println("ERROR"); }
        ExpVector ei = RatPolynomial.DIRPEV(A);
        ExpVector ej = RatPolynomial.DIRPEV(B);
        ExpVector g = ExpVector.EVSUM(ei,ej);
	//        boolean t =  g == e ;
        ExpVector h = ExpVector.EVDIF(g,e);
        int s = ExpVector.EVSIGN(h);
        return ! ( s == 0 );
    }


    /**
     * Normalform.
     */

    public static RatPolynomial DIRPNF(List Pp, RatPolynomial Ap) {  
        if ( Pp.isEmpty() ) return Ap;
        int i;
        int l = Pp.size();
        Map.Entry m;
        Object[] P;
        synchronized (Pp) {
           P = Pp.toArray();
	}
        ExpVector[] htl = new ExpVector[ l ];
        BigRational[] lbc = new BigRational[ l ];
        RatPolynomial[] p = new RatPolynomial[ l ];
	int j = 0;
        for ( i = 0; i < l; i++ ) { 
            p[i] = (RatPolynomial) P[i];
            m = RatPolynomial.DIRPLM( p[i] );
	    if ( m != null ) { 
               p[j] = p[i];
               htl[j] = (ExpVector) m.getKey();
               lbc[j] = (BigRational) m.getValue();
	       j++;
	    }
	}
	l = j;
        ExpVector e;
        BigRational a;
        boolean mt = false;
        RatPolynomial R = new RatPolynomial( RatPolynomial.DIPNOV(Ap) );
        RatPolynomial T = null;
        RatPolynomial Q = null;
        RatPolynomial S = Ap;
        while ( S.length() > 0 ) { 
              m = RatPolynomial.DIRPLM( S );
              e = (ExpVector) m.getKey();
              a = (BigRational) m.getValue();
              for ( i = 0; i < l; i++ ) {
                  mt = ExpVector.EVMT( e, htl[i] );
                  if ( mt ) break; 
	      }
              if ( ! mt ) { 
		  //System.out.println(e + " irred ");
                 T = new RatPolynomial( a, e );
                 R = RatPolynomial.DIRPSM( R, T );
                 S = RatPolynomial.DIRPDF( S, T ); // bad performance
		 //		 System.out.println(" S = " + S);
	      }
              else { 
		  //                 System.out.println(e + " red ");
		 e = ExpVector.EVDIF( e, htl[i] );
                 a = BigRational.RNQ( a, lbc[i] );
                 T = new RatPolynomial( a, e );
                 Q = RatPolynomial.DIRPPR( p[i], T );
                 S = RatPolynomial.DIRPDF( S, Q );
              }
	}
        return R;
    }


    /**
     * Irreducible set.
     */

    public static ArrayList DIRLIS(List Pp) {  
        RatPolynomial a;
        ArrayList P = new ArrayList();
        ListIterator it = Pp.listIterator();
        while ( it.hasNext() ) { 
            a = (RatPolynomial) it.next();
            if ( a.length() != 0 ) {
               a = RatPolynomial.DIRPMC( a );
               P.add( (Object) a );
	    }
	}
        int l = P.size();
        if ( l <= 1 ) return P;

        int irr = 0;
        ExpVector e;        
        ExpVector f;        
        System.out.print("irr = ");
        while ( irr != l ) {
            it = P.listIterator(); 
	    a = (RatPolynomial) it.next();
            P.remove(0);
            e = RatPolynomial.DIRPEV( a );
            a = DIRPNF( P, a );
            System.out.print(irr + " ");
            if ( a.length() == 0 ) { l--;
	       if ( l <= 1 ) { return P; }
	    } else {
	       f = RatPolynomial.DIRPEV( a );
               if ( ExpVector.EVSIGN( f ) == 0 ) { 
		  P = new ArrayList(); P.add( (Object) RatPolynomial.DIRPMC(a) ); 
	          return P;
               }    
               if ( e.equals( f ) ) {
		  irr++;
	       } else {
                  irr = 0; a = RatPolynomial.DIRPMC( a );
	       }
               P.add( (Object) a );
	    }
	}
        System.out.println();
	return P;
    }


    /**
     * Groebner base using pairlist class.
     */

    public static ArrayList DIRPGB(List Pp) {  
        RatPolynomial p;
        ArrayList P = new ArrayList();
        Pairlist pairlist = new Pairlist( RatPolynomial.ordm );
        int l = Pp.size();
        ListIterator it = Pp.listIterator();
        while ( it.hasNext() ) { 
            p = (RatPolynomial) it.next();
            if ( p.length() > 0 ) {
               p = RatPolynomial.DIRPMC( p );
               if ( p.isONE() ) {
		  P.clear(); P.add( p );
                  return P;
	       }
               P.add( (Object) p );
               pairlist.put( p );
	    }
            else l--;
	}
        if ( l <= 1 ) return P;

        Pairlist.Pair pair;
        RatPolynomial pi;
        RatPolynomial pj;
        RatPolynomial S;
        RatPolynomial H;
        while ( pairlist.hasNext() ) {
              pair = (Pairlist.Pair) pairlist.removeNext();
              if ( pair == null ) continue; 

              pi = pair.pi; //(RatPolynomial) pair[0];
              pj = pair.pj; //(RatPolynomial) pair[1];
	      //System.out.println("pi  = " + pi);
              //System.out.println("pj  = " + pj);

              S = DIRPSP( pi, pj );
	      // System.out.println("S   = " + S);
              if ( S.length() == 0 ) continue;

              H = DIRPNF( P, S );
	      H = RatPolynomial.DIRPMC( H );
	      if ( H.isONE() ) {
		  P.clear(); P.add( H );
                  return P;
	      }
              //System.out.println("H   = " + H);
              if ( H.length() > 0 ) {
		 l++;
                 P.add( (Object) H );
                 pairlist.put( H );
              }
	}
	//        P = DIRLIS(P);
	P = DIGBMI(P);
        System.out.println();
	return P;
    }

    /**
     * Parallel Groebner base using pairlist class.
     */

    public static ArrayList DIRPGBparallel1(List Pp, int threads) {  
        RatPolynomial p;
        ArrayList P = new ArrayList();
        Pairlist pairlist = new Pairlist( RatPolynomial.ordm );
        int l = Pp.size();
        ListIterator it = Pp.listIterator();
        while ( it.hasNext() ) { 
            p = (RatPolynomial) it.next();
            if ( p.length() > 0 ) {
               p = RatPolynomial.DIRPMC( p );
               if ( p.isONE() ) {
		  P.clear(); P.add( p );
                  return P;
	       }
               P.add( (Object) p );
               pairlist.put( p );
	    }
            else l--;
	}
        if ( l <= 1 ) return P;

	if ( threads < 1 ) threads = 1;
	ThreadPool T = new ThreadPool(threads);

        Pairlist.Pair pair;
        RatPolynomial pi;
        RatPolynomial pj;
        RatPolynomial S;
        RatPolynomial H;
	Reducer1 R = null;
	int sleeps = 0;
        while ( pairlist.hasNext() || T.hasJobs() ) {
	      while ( ! pairlist.hasNext() || T.hasJobs(threads) ) {
                                           // dont go to far ahead
		  try {
		      sleeps++;
                      if ( sleeps % 10 == 0 ) {
                         System.out.println(" main is sleeping");
		      } else {
                         System.out.print("s");
		      }
		      Thread.currentThread().sleep(100);
		  } catch (InterruptedException e) {
                     break;
		  }
		  if ( ! T.hasJobs() ) break;
              }
              pair = (Pairlist.Pair) pairlist.removeNext();
              if ( pair == null ) continue; 

              pi = pair.pi; 
              pj = pair.pj; 
	      //System.out.println("pi  = " + pi);
              //System.out.println("pj  = " + pj);

              S = DIRPSP( pi, pj );
              //System.out.println("S   = " + S);
              if ( S.isZERO() ) continue;

	      R = new Reducer1( R, P, S, pairlist );
	      T.addJob( R );
	}
	P = DIGBMIparallel(P,T);
	//	R.done.P();
	T.terminate();
        System.out.println();
	return P;
    }

    /**
     * reducing worker threads
     */

    static class Reducer1 implements Runnable {
	private List P;
	private RatPolynomial S;
	private Pairlist pairlist;
	private Reducer1 proceeding;
	private boolean working = true;
	private boolean sema = false;
	public final Semaphore done = new Semaphore(0);

	Reducer1(Reducer1 pr, List P, RatPolynomial p, Pairlist l) {
	    proceeding = pr;
	    this.P = P;
	    S = p;
	    pairlist = l;
	} 

	public void run() {
           RatPolynomial H = S;
	   int l = 0;
	   int plen = 0;
	   do {
              System.out.print("ht(S|H) = " + Polynomial.DIPLEV(H));
	      if ( P.size() > plen ) { // only new polynomials
		 plen = P.size();
                 H = DIRPNF( P, H );
                 System.out.println(", ht(H) = " + Polynomial.DIPLEV(H));
	         H = RatPolynomial.DIRPMC( H );
	      } else {
                 System.out.println();
	      }
              // System.out.println("H   = " + H);
	      if ( H.isONE() ) {
		  synchronized (P) {
                      P.clear(); P.add( H );
		  }
                  working = false;
	          done.V();
                  return;
	      }
	      l++;
	      // if ( H.isZERO() ) break; must wait on chain
	      if ( proceeding == null ) break;
	      if ( ! proceeding.done() ) { 
		 try {
		     System.out.println("wait on proceeding");
		     // proceeding.wait(100);
		     sema = proceeding.done.P(100);
		     // System.out.println("sema = " + sema);
		 } catch (InterruptedException e) { }
	      }
	   } while ( ! proceeding.done() && ! sema );

	   if ( H != null && ! H.isZERO() ) {
	       // System.out.println("add(H) = " + Polynomial.DIPLEV(H));
              synchronized (P) {
                  P.add( H );
	      }
              pairlist.put( H );
	   }
	   working = false;
   	   done.V();
	}

	public boolean done() {
	    return ! working;
	}
    }

    /**
     * Parallel Groebner base using pairlist class.
     */

    public static ArrayList DIRPGBparallel(List Pp, int threads) {  
        RatPolynomial p;
        ArrayList P = new ArrayList();
        Pairlist pairlist = new Pairlist( RatPolynomial.ordm );
        int l = Pp.size();
        ListIterator it = Pp.listIterator();
        while ( it.hasNext() ) { 
            p = (RatPolynomial) it.next();
            if ( p.length() > 0 ) {
               p = RatPolynomial.DIRPMC( p );
               if ( p.isONE() ) {
		  P.clear(); P.add( p );
                  return P;
	       }
               P.add( (Object) p );
               pairlist.put( p );
	    }
            else l--;
	}
        if ( l <= 1 ) return P;

	if ( threads < 1 ) threads = 1;
	ThreadPool T = new ThreadPool(threads);
	Terminator fin = new Terminator(threads);
	Reducer R;
	for ( int i = 0; i < threads; i++ ) {
	      R = new Reducer( fin, P, pairlist );
	      T.addJob( R );
	}
	fin.done();
	// System.out.println("\n main loop ended \n");
	P = DIGBMIparallel(P,T);
	T.terminate();
        System.out.println();
	return P;
    }



    /**
     * reducing worker threads
     */

    static class Reducer implements Runnable {
	private List P;
	private Pairlist pairlist;
	private Terminator pool;

	Reducer(Terminator fin, List P, Pairlist L) {
	    pool = fin;
	    this.P = P;
	    pairlist = L;
	} 

	public void run() {
           Pairlist.Pair pair;
           RatPolynomial pi;
           RatPolynomial pj;
           RatPolynomial S;
           RatPolynomial H;
	   boolean set = false;
	   int red = 0;
	   int sleeps = 0;
           while ( pairlist.hasNext() || pool.hasJobs() ) {
	      while ( ! pairlist.hasNext() ) {
                  // wait
                  pool.beIdle(); set = true;
		  try {
		      sleeps++;
                      if ( sleeps % 10 == 0 ) {
                         System.out.println(" reducer is sleeping");
		      } else {
                         System.out.print("r");
		      }
		      Thread.currentThread().sleep(100);
		  } catch (InterruptedException e) {
                     break;
		  }
		  if ( ! pool.hasJobs() ) break;
              }
              if ( ! pairlist.hasNext() && ! pool.hasJobs() ) break;
              if ( set ) pool.notIdle();

              pair = (Pairlist.Pair) pairlist.removeNext();
              if ( pair == null ) continue; 

              pi = pair.pi; 
              pj = pair.pj; 
	      //System.out.println("pi  = " + pi);
              //System.out.println("pj  = " + pj);

              S = DIRPSP( pi, pj );
              //System.out.println("S   = " + S);
              if ( S.isZERO() ) continue;

              System.out.print("ht(S) = " + Polynomial.DIPLEV(S));
              H = DIRPNF( P, S );
	      red++;
              System.out.println(", ht(H) = " + Polynomial.DIPLEV(H));
              H = RatPolynomial.DIRPMC( H );
              // System.out.println("H   = " + H);
	      if ( H.isONE() ) {
		  synchronized (P) {
                      P.clear(); P.add( H );
		  }
	          pool.allIdle();
                  return;
	      }
	      if ( H != null && ! H.isZERO() ) {
	         // System.out.println("add(H) = " + Polynomial.DIPLEV(H));
                 synchronized (P) {
                     P.add( H );
	         }
                 pairlist.put( H );
	      }
	   }
           System.out.println( "Reducer["+
                      Thread.currentThread().getName()+
                      "] terminated, done " + red + " reductions");
	}

    }


    /**
     * terminating helper class
     */

    static class Terminator {

	private int workers = 0;
	private int idler = 0;
	private Semaphore fin = new Semaphore(0);

	Terminator(int workers) {
	    this.workers = workers;
	}

	synchronized void beIdle() {
	    idler++;
	    if ( idler >= workers ) fin.V();
	}

	synchronized void allIdle() {
	    idler = workers;
	    fin.V();
	}

	synchronized void notIdle() {
	    idler--;
	}

	boolean hasJobs() {
	    return ( idler < workers );
	}

	void done() {
            try { fin.P();
	    } catch (InterruptedException e) { }
	}


    }

    /**
     * Minimal ordered groebner basis.
     */

    public static ArrayList DIGBMI(List Pp) {  
        RatPolynomial a;
        ArrayList P = new ArrayList();
        ListIterator it = Pp.listIterator();
        while ( it.hasNext() ) { 
            a = (RatPolynomial) it.next();
            if ( a.length() != 0 ) { // always true
	       // already monic  a = RatPolynomial.DIRPMC( a );
               P.add( (Object) a );
	    }
	}
        if ( P.size() <= 1 ) return P;

        ExpVector e;        
        ExpVector f;        
        RatPolynomial p;
        ArrayList Q = new ArrayList();
	boolean mt;

        while ( P.size() > 0 ) {
            a = (RatPolynomial) P.remove(0);
	    e = RatPolynomial.DIRPEV( a );

            it = P.listIterator();
	    mt = false;
	    while ( it.hasNext() && ! mt ) {
               p = (RatPolynomial) it.next();
               f = RatPolynomial.DIRPEV( p );
	       mt = ExpVector.EVMT( e, f );
	    }
            it = Q.listIterator();
	    while ( it.hasNext() && ! mt ) {
               p = (RatPolynomial) it.next();
               f = RatPolynomial.DIRPEV( p );
	       mt = ExpVector.EVMT( e, f );
	    }
	    if ( ! mt ) {
		Q.add( (Object)a );
	    } else {
		// System.out.println("dropped " + a.length());
	    }
	}
	P = Q;
        if ( P.size() <= 1 ) return P;

        Q = new ArrayList();
        while ( P.size() > 0 ) {
            a = (RatPolynomial) P.remove(0);
	    // System.out.println("doing " + a.length());
            a = DIRPNF( P, a );
            a = DIRPNF( Q, a );
            Q.add( (Object)a );
	}
	return Q;
    }

    /**
     * Minimal ordered groebner basis, parallel.
     */

    public static ArrayList DIGBMIparallel(List Pp, ThreadPool T) {  
        RatPolynomial a;
        ArrayList P = new ArrayList();
        ListIterator it = Pp.listIterator();
        while ( it.hasNext() ) { 
            a = (RatPolynomial) it.next();
            if ( a.length() != 0 ) { // always true
	       // already monic  a = RatPolynomial.DIRPMC( a );
               P.add( (Object) a );
	    }
	}
        if ( P.size() <= 1 ) return P;

        ExpVector e;        
        ExpVector f;        
        RatPolynomial p;
        ArrayList Q = new ArrayList();
	boolean mt;

        while ( P.size() > 0 ) {
            a = (RatPolynomial) P.remove(0);
	    e = RatPolynomial.DIRPEV( a );

            it = P.listIterator();
	    mt = false;
	    while ( it.hasNext() && ! mt ) {
               p = (RatPolynomial) it.next();
               f = RatPolynomial.DIRPEV( p );
	       mt = ExpVector.EVMT( e, f );
	    }
            it = Q.listIterator();
	    while ( it.hasNext() && ! mt ) {
               p = (RatPolynomial) it.next();
               f = RatPolynomial.DIRPEV( p );
	       mt = ExpVector.EVMT( e, f );
	    }
	    if ( ! mt ) {
		Q.add( (Object)a );
	    } else {
		// System.out.println("dropped " + a.length());
	    }
	}
	P = Q;
        if ( P.size() <= 1 ) return P;

	MiReducer[] mirs = new MiReducer[ P.size() ];
        int i = 0;
        Q = new ArrayList( P.size() );
        while ( P.size() > 0 ) {
            a = (RatPolynomial) P.remove(0);
	    // System.out.println("doing " + a.length());
	    mirs[i] = new MiReducer( (List)P.clone(), (List)Q.clone(), a );
	    T.addJob( mirs[i] );
	    i++;
            Q.add( (Object)a );
	}
	P = Q;
	Q = new ArrayList( P.size() );
	for ( i = 0; i < mirs.length; i++ ) {
	    a = (RatPolynomial) mirs[i].getNF();
            Q.add( (Object)a );
	}
	return Q;
    }


    /**
     * reducing worker threads for minimal GB
     */

    static class MiReducer implements Runnable {
	private List P;
	private List Q;
	private RatPolynomial S;
	private RatPolynomial H;
	private Semaphore done = new Semaphore(0);

	MiReducer(List P, List Q, RatPolynomial p) {
	    this.P = P;
	    this.Q = Q;
	    S = p;
	    H = S;
	} 

	public RatPolynomial getNF() {
	    try { done.P();
	    } catch (InterruptedException e) { }
	    return H;
	}

	public void run() {
              System.out.print("ht(S) = " + Polynomial.DIPLEV(S));
              H = DIRPNF( P, H );
              H = DIRPNF( Q, H );
	      done.V();
              System.out.println(", ht(H) = " + Polynomial.DIPLEV(H));
	      // H = RatPolynomial.DIRPMC( H );
	}

    }


}
