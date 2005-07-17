/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;

import edu.jas.poly.GenSolvablePolynomial;

import edu.jas.util.DistHashTable;


/**
 * Polynomial Reduction class.
 * Implements S-Polynomial, Normalform, Criterion 4 and Irreducible Set.
 * @author Heinz Kredel
 */

public class Reduction  {

    private static Logger logger = Logger.getLogger(Reduction.class);


    /**
     * S-Polynomial
     */

    public static <C extends RingElem<C>> 
        GenPolynomial<C> SPolynomial(GenPolynomial<C> Ap, 
                                     GenPolynomial<C> Bp) {  
        if ( logger.isInfoEnabled() ) {
	   if ( Bp == null || Bp.isZERO() ) {
              return Ap.ring.getZERO(); 
	   }
	   if ( Ap == null || Ap.isZERO() ) {
              return Bp.ring.getZERO(); 
	   }
           if ( ! Ap.ring.equals( Bp.ring ) ) { 
              logger.error("rings not equal"); 
           }
	}
        Map.Entry<ExpVector,C> ma = Ap.leadingMonomial();
        Map.Entry<ExpVector,C> mb = Bp.leadingMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g  = ExpVector.EVLCM(e,f);
        ExpVector e1 = ExpVector.EVDIF(g,e);
        ExpVector f1 = ExpVector.EVDIF(g,f);

        C a = ma.getValue();
        C b = mb.getValue();

        GenPolynomial<C> App = Ap.multiply( b, e1 );
        GenPolynomial<C> Bpp = Bp.multiply( a, f1 );
        GenPolynomial<C> Cp = App.subtract(Bpp);
        return Cp;
    }


    /**
     * Left S-Polynomial
     */

    public static <C extends RingElem<C>>
           GenSolvablePolynomial<C> 
           leftSPolynomial(GenSolvablePolynomial<C> Ap, 
                           GenSolvablePolynomial<C> Bp) {  
        if ( logger.isInfoEnabled() ) {
	   if ( Bp == null || Bp.isZERO() ) {
               if ( Ap != null ) {
                  return Ap.ring.getZERO(); 
               } else {
                  return null;
               }
	   }
	   if ( Ap == null || Ap.isZERO() ) {
              return Bp.ring.getZERO(); 
	   }
           if ( ! Ap.ring.equals( Bp.ring ) ) { 
              logger.error("rings not equal"); 
           }
	}
        Map.Entry<ExpVector,C> ma 
            = Ap.leadingMonomial();
        Map.Entry<ExpVector,C> mb 
            = Bp.leadingMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g = ExpVector.EVLCM(e,f);
        ExpVector e1 = ExpVector.EVDIF(g,e);
        ExpVector f1 = ExpVector.EVDIF(g,f);

        C a = ma.getValue();
        C b = mb.getValue();

        GenSolvablePolynomial<C> App = Ap.multiplyLeft( b, e1 );
        GenSolvablePolynomial<C> Bpp = Bp.multiplyLeft( a, f1 );
        GenSolvablePolynomial<C> Cp = (GenSolvablePolynomial<C>) App.subtract(Bpp);
        return Cp;
    }



    /**
     * Module criterium.
     * @return true if the module S-polynomial(i,j) is required.
     */

    public static <C extends RingElem<C>> 
           boolean ModuleCriterion(int modv, 
                                   GenPolynomial<C> A, 
                                   GenPolynomial<C> B) {  
        if ( modv == 0 ) {
            return true;
        }
        ExpVector ei = A.leadingExpVector();
        ExpVector ej = B.leadingExpVector();
        if ( ExpVector.EVILCP( ei, ej, 0, modv ) != 0 ) {
           return false; // skip pair
        }
        return true;
    }


    /**
     * GB criterium 4.
     * @param e = lcm(ht(A),ht(B)) 
     * @return true if the S-polynomial(i,j) is required.
     */

    public static <C extends RingElem<C>> 
           boolean GBCriterion4(GenPolynomial<C> A, 
                                GenPolynomial<C> B, 
                                ExpVector e) {  
        if ( logger.isInfoEnabled() ) {
           if ( ! A.ring.equals( B.ring ) ) { 
              logger.error("rings equal"); 
           }
           if (   A instanceof GenSolvablePolynomial
               || B instanceof GenSolvablePolynomial ) {
              logger.error("GBCriterion4 not applicabable to SolvablePolynomials"); 
              return true;
           }
	}
        ExpVector ei = A.leadingExpVector();
        ExpVector ej = B.leadingExpVector();
        ExpVector g = ExpVector.EVSUM(ei,ej);
	// boolean t =  g == e ;
        ExpVector h = ExpVector.EVDIF(g,e);
        int s = ExpVector.EVSIGN(h);
        return ! ( s == 0 );
    }


    /**
     * GB criterium 4.
     * @return true if the S-polynomial(i,j) is required.
     */

    public static <C extends RingElem<C>> 
           boolean GBCriterion4(GenPolynomial<C> A, 
                                GenPolynomial<C> B) {  
        if ( logger.isInfoEnabled() ) {
           if (   A instanceof GenSolvablePolynomial
               || B instanceof GenSolvablePolynomial ) {
               logger.error("GBCriterion4 not applicabable to SolvablePolynomials"); 
               return true;
           }
        }
        ExpVector ei = A.leadingExpVector();
        ExpVector ej = B.leadingExpVector();
        ExpVector g = ExpVector.EVSUM(ei,ej);
        ExpVector e = ExpVector.EVLCM(ei,ej);
	//        boolean t =  g == e ;
        ExpVector h = ExpVector.EVDIF(g,e);
        int s = ExpVector.EVSIGN(h);
        return ! ( s == 0 );
    }


    /**
     * Normalform.
     */

    @SuppressWarnings("unchecked") // not jet working
    public static <C extends RingElem<C>> 
           GenPolynomial<C> normalform(List<GenPolynomial<C>> Pp, 
                                       GenPolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        Map.Entry<ExpVector,C> m;
        int l = Pp.size();
        GenPolynomial<C>[] P = new GenPolynomial[l];
        synchronized (Pp) {
            //P = Pp.toArray();
            for ( int i = 0; i < Pp.size(); i++ ) {
                P[i] = Pp.get(i);
            }
	}
        ExpVector[] htl = new ExpVector[ l ];
        Object[] lbc = new Object[ l ]; // want <C>
        GenPolynomial<C>[] p = new GenPolynomial[ l ];
        int i;
	int j = 0;
        for ( i = 0; i < l; i++ ) { 
            p[i] = P[i];
            m = p[i].leadingMonomial();
	    if ( m != null ) { 
               p[j] = p[i];
               htl[j] = m.getKey();
               lbc[j] = m.getValue();
	       j++;
	    }
	}
	l = j;
        ExpVector e;
        C a;
        boolean mt = false;
        GenPolynomial<C> R = Ap.ring.getZERO();

        GenPolynomial<C> T = null;
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
        while ( S.length() > 0 ) { 
	      m = S.leadingMonomial();
              e = m.getKey();
              a = m.getValue();
              for ( i = 0; i < l; i++ ) {
                  mt = ExpVector.EVMT( e, htl[i] );
                  if ( mt ) break; 
	      }
              if ( ! mt ) { 
                 //logger.debug("irred");
                 //T = new OrderedMapPolynomial( a, e );
                 R = R.add( a, e );
                 S = S.subtract( a, e ); 
		 // System.out.println(" S = " + S);
	      } else { 
		 e = ExpVector.EVDIF( e, htl[i] );
                 //logger.info("red div = " + e);
                 a = a.divide( (C)lbc[i] );
                 Q = p[i].multiply( a, e );
                 S = S.subtract( Q );
              }
	}
        return R;
    }


    /**
     * Normalform Set.
     */

    public static <C extends RingElem<C>> 
           List<GenPolynomial<C>> normalform(List<GenPolynomial<C>> Pp, 
                                             List<GenPolynomial<C>> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isEmpty() ) {
           return Ap;
        }
        ArrayList<GenPolynomial<C>> red 
           = new ArrayList<GenPolynomial<C>>();
        for ( GenPolynomial<C> A : Ap ) {
            A = normalform( Pp, A );
            red.add( A );
        }
        return red;
    }



    /**
     * Irreducible set.
     */

    public static <C extends RingElem<C>> 
           List<GenPolynomial<C>> irreducibleSet(List<GenPolynomial<C>> Pp) {  
        ArrayList<GenPolynomial<C>> P = new ArrayList<GenPolynomial<C>>();
        for ( GenPolynomial<C> a : Pp ) {
            if ( a.length() != 0 ) {
               a = a.monic();
               P.add( a );
	    }
	}
        int l = P.size();
        if ( l <= 1 ) return P;

        int irr = 0;
        ExpVector e;        
        ExpVector f;        
        GenPolynomial<C> a;
        Iterator<GenPolynomial<C>> it;
        logger.debug("irr = ");
        while ( irr != l ) {
            it = P.listIterator(); 
	    a = it.next();
            P.remove(0);
            e = a.leadingExpVector();
            a = normalform( P, a );
            logger.debug(String.valueOf(irr));
            if ( a.length() == 0 ) { l--;
	       if ( l <= 1 ) { return P; }
	    } else {
	       f = a.leadingExpVector();
               if ( ExpVector.EVSIGN( f ) == 0 ) { 
		  P = new ArrayList<GenPolynomial<C>>(); 
                  P.add( a.monic() ); 
	          return P;
               }    
               if ( e.equals( f ) ) {
		  irr++;
	       } else {
                  irr = 0; a = a.monic();
	       }
               P.add( a );
	    }
	}
        //System.out.println();
	return P;
    }


    /**
     * Left Normalform.
     */

    public static <C extends RingElem<C>> 
           GenSolvablePolynomial<C> 
           leftNormalform(List<GenSolvablePolynomial<C>> Pp, 
                          GenSolvablePolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        int l = Pp.size();
        Map.Entry<ExpVector,C> m;
        GenSolvablePolynomial<C>[] P = new GenSolvablePolynomial[ l ];
        synchronized (Pp) {
            //P = Pp.toArray();
            for ( int j = 0; j < Pp.size(); j++ ) {
                P[j] = Pp.get(j);
            }
	}
        int i;
        ExpVector[] htl = new ExpVector[ l ];
        Object[] lbc = new Object[ l ]; // want <C>
        GenSolvablePolynomial<C>[] p = new GenSolvablePolynomial[ l ];
	int j = 0;
        for ( i = 0; i < l; i++ ) { 
            p[i] = P[i];
            m = p[i].leadingMonomial();
	    if ( m != null ) { 
               p[j] = p[i];
               htl[j] = m.getKey();
               lbc[j] = m.getValue();
	       j++;
	    }
	}
	l = j;
        ExpVector e;
        C a;
        boolean mt = false;
        GenSolvablePolynomial<C> R = Ap.ring.getZERO();

        GenSolvablePolynomial<C> T = null;
        GenSolvablePolynomial<C> Q = null;
        GenSolvablePolynomial<C> S = Ap;
        while ( S.length() > 0 ) { 
	      m = S.leadingMonomial();
              e = m.getKey();
              //logger.info("red = " + e);
              a = m.getValue();
              for ( i = 0; i < l; i++ ) {
                  mt = ExpVector.EVMT( e, htl[i] );
                  if ( mt ) break; 
	      }
              if ( ! mt ) { 
                 //logger.debug("irred");
                 //T = new OrderedMapPolynomial( a, e );
                 R = (GenSolvablePolynomial<C>)R.add( a, e );
                 S = (GenSolvablePolynomial<C>)S.subtract( a, e ); 
		 // System.out.println(" S = " + S);
	      } else { 
                 //logger.debug("red");
		 e = ExpVector.EVDIF( e, htl[i] );
                 a = a.divide( (C)lbc[i] );
                 Q = p[i].multiplyLeft( a, e );
                 S = (GenSolvablePolynomial<C>)S.subtract( Q );
              }
	}
        return R;
    }


    /**
     * Left Normalform Set.
     */

    public static <C extends RingElem<C>> 
           List<GenSolvablePolynomial<C>> 
           leftNormalform(List<GenSolvablePolynomial<C>> Pp, 
                          List<GenSolvablePolynomial<C>> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isEmpty() ) {
           return Ap;
        }
        ArrayList<GenSolvablePolynomial<C>> red 
           = new ArrayList<GenSolvablePolynomial<C>>();
        for ( GenSolvablePolynomial<C> A : Ap ) {
            A = leftNormalform( Pp, A );
            red.add( A );
        }
        return red;
    }


    /**
     * Left irreducible set.
     */

    public static <C extends RingElem<C>> 
           List<GenSolvablePolynomial<C>> 
           leftIrreducibleSet(List<GenSolvablePolynomial<C>> Pp) {  
        ArrayList<GenSolvablePolynomial<C>> P 
           = new ArrayList<GenSolvablePolynomial<C>>();
        for ( GenSolvablePolynomial<C> a : Pp ) {
            if ( a.length() != 0 ) {
               a = (GenSolvablePolynomial<C>)a.monic();
               P.add( a );
	    }
	}
        int l = P.size();
        if ( l <= 1 ) return P;

        int irr = 0;
        ExpVector e;        
        ExpVector f;        
        GenSolvablePolynomial<C> a;
        Iterator<GenSolvablePolynomial<C>> it;
        logger.debug("irr = ");
        while ( irr != l ) {
            it = P.listIterator(); 
	    a = it.next();
            P.remove(0);
            e = a.leadingExpVector();
            a = leftNormalform( P, a );
            logger.debug(String.valueOf(irr));
            if ( a.length() == 0 ) { l--;
	       if ( l <= 1 ) { return P; }
	    } else {
	       f = a.leadingExpVector();
               if ( ExpVector.EVSIGN( f ) == 0 ) { 
		  P = new ArrayList<GenSolvablePolynomial<C>>(); 
                  P.add( (GenSolvablePolynomial<C>)a.monic() ); 
	          return P;
               }    
               if ( e.equals( f ) ) {
		  irr++;
	       } else {
                  irr = 0; a = (GenSolvablePolynomial<C>)a.monic();
	       }
               P.add( a );
	    }
	}
        //System.out.println();
	return P;
    }


    /**
     * Normalform. Allows concurrent modification of the list.
     */

    public static <C extends RingElem<C>>
           GenPolynomial<C> 
           normalformMod(/*Array*/List<GenPolynomial<C>> Pp, 
                         GenPolynomial<C> Ap) {  
        if ( Pp == null ) return Ap;
        if ( Pp.isEmpty() ) return Ap;
        int l = Pp.size();
        GenPolynomial<C>[] P = new GenPolynomial[l];
        synchronized (Pp) { // required, ok in dist
           //P = Pp.values().toArray();
           for ( int i = 0; i < Pp.size(); i++ ) {
               P[i] = Pp.get(i);
           }
        }

        Map.Entry<ExpVector,C> m;
        Map.Entry<ExpVector,C> m1;
        ExpVector e;
        ExpVector f = null;
        C a;
        boolean mt = false;
        GenPolynomial<C> Rz = Ap.ring.getZERO();
        GenPolynomial<C> R = Rz;
        GenPolynomial<C> p = null;
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
        while ( S.length() > 0 ) { 
              if ( Pp.size() != l ) { 
                 //long t = System.currentTimeMillis();
                 l = Pp.size();
                 synchronized (Pp) { // required, bad in parallel
                    P = new GenPolynomial[ l ];
                    //P = Pp.toArray();
                    for ( int i = 0; i < Pp.size(); i++ ) {
                        P[i] = Pp.get(i);
                    }
                 }
                 //t = System.currentTimeMillis()-t;
                 //logger.info("Pp.toArray() = " + t + " ms, size() = " + l);
                 S = Ap; // S.add(R)? // restart reduction ?
                 R = Rz; 
              }
	      m = S.leadingMonomial();
              e = m.getKey();
              a = m.getValue();
              for ( int i = 0; i < P.length ; i++ ) {
                  p = P[i];
                  f = p.leadingExpVector();
                  if ( f != null ) {
                     mt = ExpVector.EVMT( e, f );
                     if ( mt ) break; 
                  }
	      }
              if ( ! mt ) { 
                 //logger.debug("irred");
                 //T = new OrderedMapPolynomial( a, e );
                 R = R.add( a, e );
                 S = S.subtract( a, e ); 
		 // System.out.println(" S = " + S);
	      } else { 
                 //logger.debug("red");
                 m1 = p.leadingMonomial();
		 e = ExpVector.EVDIF( e, f );
                 a = a.divide( m1.getValue() );
                 Q = p.multiply( a, e );
                 S = S.subtract( Q );
              }
	}
        return R;
    }


    /**
     * Normalform. Allows concurrent modification of the list.
     */

    public static <C extends RingElem<C>>
           GenPolynomial<C> 
           normalformMod(DistHashTable Pp, 
                         GenPolynomial<C> Ap) {  
        if ( Pp == null ) return Ap;
        if ( Pp.isEmpty() ) return Ap;
        int l = Pp.size();
        GenPolynomial<C>[] P = new GenPolynomial[l];
        synchronized (Pp) { // required, ok in dist
           //P = Pp.values().toArray();
           Collection<GenPolynomial<C>> Pv 
               = (Collection<GenPolynomial<C>>)Pp.values();
           int i = 0;
           for ( GenPolynomial<C> x : Pv ) {
               P[i++] = x;
           }
        }

        Map.Entry<ExpVector,C> m;
        Map.Entry<ExpVector,C> m1;
        ExpVector e;
        ExpVector f = null;
        C a;
        boolean mt = false;
        GenPolynomial<C> Rz = Ap.ring.getZERO();
        GenPolynomial<C> R = Rz;
        GenPolynomial<C> p = null;
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
        while ( S.length() > 0 ) { 
              if ( Pp.size() != l ) { 
                 //long t = System.currentTimeMillis();
                 l = Pp.size();
                 synchronized (Pp) { // required, ok in distributed
                    P = new GenPolynomial[ l ];
                    //P = Pp.values().toArray();
                    Collection<GenPolynomial<C>> Pv 
                        = (Collection<GenPolynomial<C>>)Pp.values();
                    int i = 0;
                    for ( GenPolynomial<C> x : Pv ) {
                        P[i++] = x;
                    }
                 }
                 //t = System.currentTimeMillis()-t;
                 //logger.info("Pp.toArray() = " + t + " ms, size() = " + l);
                 S = Ap; // S.add(R)? // restart reduction ?
                 R = Rz; 
              }

	      m = S.leadingMonomial();
              e = m.getKey();
              a = m.getValue();
              for ( int i = 0; i < P.length ; i++ ) {
                  p = P[i];
                  f = p.leadingExpVector();
                  if ( f != null ) {
                     mt = ExpVector.EVMT( e, f );
                     if ( mt ) break; 
                  }
	      }
              if ( ! mt ) { 
                 //logger.debug("irred");
                 //T = new OrderedMapPolynomial( a, e );
                 R = R.add( a, e );
                 S = S.subtract( a, e ); 
		 // System.out.println(" S = " + S);
	      } else { 
                 //logger.debug("red");
                 m1 = p.leadingMonomial();
		 e = ExpVector.EVDIF( e, f );
                 a = a.divide( m1.getValue() );
                 Q = p.multiply( a, e );
                 S = S.subtract( Q );
              }
	}
        return R;
    }

}
