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

import edu.jas.arith.Coefficient;
import edu.jas.poly.ExpVector;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.SolvablePolynomial;
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

    public static OrderedPolynomial SPolynomial(OrderedPolynomial Ap, 
                                                OrderedPolynomial Bp) {  
        if ( logger.isInfoEnabled() ) {
	   if ( Bp == null || Bp.isZERO() ) {
              return Ap; // wrong zero
	   }
	   if ( Ap == null || Ap.isZERO() ) {
	      return (OrderedPolynomial) Bp.negate(); // wrong zero
	   }
           if ( ! Ap.getTermOrder().equals( Bp.getTermOrder() ) ) { 
              logger.error("term orderings not equal"); 
           }
	}
        Map.Entry ma = Ap.leadingMonomial();
        Map.Entry mb = Bp.leadingMonomial();

        ExpVector e = (ExpVector) ma.getKey();
        ExpVector f = (ExpVector) mb.getKey();

        ExpVector g  = ExpVector.EVLCM(e,f);
        ExpVector e1 = ExpVector.EVDIF(g,e);
        ExpVector f1 = ExpVector.EVDIF(g,f);

        Coefficient a = (Coefficient) ma.getValue();
        Coefficient b = (Coefficient) mb.getValue();

        OrderedPolynomial App = Ap.multiply( b, e1 );
        OrderedPolynomial Bpp = Bp.multiply( a, f1 );
        OrderedPolynomial Cp = App.subtract(Bpp);
        return Cp;
    }


    /**
     * Left S-Polynomial
     */

    public static SolvablePolynomial leftSPolynomial(SolvablePolynomial Ap, 
                                                     SolvablePolynomial Bp) {  
        if ( logger.isInfoEnabled() ) {
	   if ( Bp == null || Bp.isZERO() ) {
	      return Ap; // wrong zero
	   }
	   if ( Ap == null || Ap.isZERO() ) {
	      return (SolvablePolynomial)Bp.negate(); // wrong zero
	   }
           if ( ! Ap.getTermOrder().equals( Bp.getTermOrder() ) ) { 
              logger.error("term orderings not equal"); 
           }
	}
        Map.Entry ma = Ap.leadingMonomial();
        Map.Entry mb = Bp.leadingMonomial();

        ExpVector e = (ExpVector) ma.getKey();
        ExpVector f = (ExpVector) mb.getKey();

        ExpVector g = ExpVector.EVLCM(e,f);
        ExpVector e1 = ExpVector.EVDIF(g,e);
        ExpVector f1 = ExpVector.EVDIF(g,f);

        Coefficient a = (Coefficient) ma.getValue();
        Coefficient b = (Coefficient) mb.getValue();

        SolvablePolynomial App = Ap.multiplyLeft( b, e1 );
        SolvablePolynomial Bpp = Bp.multiplyLeft( a, f1 );
        SolvablePolynomial Cp = (SolvablePolynomial) App.subtract(Bpp);
        return Cp;
    }


    /**
     * Module criterium.
     * @return true if the module S-polynomial(i,j) is required.
     */

    public static boolean ModuleCriterion(int modv, 
                                          OrderedPolynomial A, 
                                          OrderedPolynomial B) {  
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

    public static boolean GBCriterion4(OrderedPolynomial A, 
                                       OrderedPolynomial B, 
                                       ExpVector e) {  
        if ( logger.isInfoEnabled() ) {
           if ( ! A.getTermOrder().equals( B.getTermOrder() ) ) { 
              logger.error("term orderings not equal"); 
           }
           if (   A instanceof SolvablePolynomial
               || B instanceof SolvablePolynomial ) {
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

    public static boolean GBCriterion4(OrderedPolynomial A, 
                                       OrderedPolynomial B) {  
        if (   A instanceof SolvablePolynomial
            || B instanceof SolvablePolynomial ) {
            logger.error("GBCriterion4 not applicabable to SolvablePolynomials"); 
            return true;
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

    public static OrderedPolynomial normalform(List Pp, 
                                               OrderedPolynomial Ap) {  
        if ( Pp == null ) return Ap;
        if ( Pp.isEmpty() ) return Ap;
        int i;
        int l = Pp.size();
        Map.Entry m;
        Object[] P;
        synchronized (Pp) {
           P = Pp.toArray();
	}
        ExpVector[] htl = new ExpVector[ l ];
        Coefficient[] lbc = new Coefficient[ l ];
        OrderedPolynomial[] p = new OrderedPolynomial[ l ];
	int j = 0;
        for ( i = 0; i < l; i++ ) { 
            p[i] = (OrderedPolynomial) P[i];
            m = p[i].leadingMonomial();
	    if ( m != null ) { 
               p[j] = p[i];
               htl[j] = (ExpVector) m.getKey();
               lbc[j] = (Coefficient) m.getValue();
	       j++;
	    }
	}
	l = j;
        ExpVector e;
        Coefficient a;
        boolean mt = false;
        OrderedPolynomial R = Ap.getZERO( Ap.getTermOrder() );

        OrderedPolynomial T = null;
        OrderedPolynomial Q = null;
        OrderedPolynomial S = Ap;
        while ( S.length() > 0 ) { 
	      m = S.leadingMonomial();
              e = (ExpVector) m.getKey();
              a = (Coefficient) m.getValue();
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
                 a = a.divide( lbc[i] );
                 Q = p[i].multiply( a, e );
                 S = S.subtract( Q );
              }
	}
        return R;
    }


    /**
     * Left Normalform.
     */

    public static SolvablePolynomial leftNormalform(List Pp, 
                                                    SolvablePolynomial Ap) {  
        if ( Pp == null ) return Ap;
        if ( Pp.isEmpty() ) return Ap;
        int i;
        int l = Pp.size();
        Map.Entry m;
        Object[] P;
        synchronized (Pp) {
           P = Pp.toArray();
	}
        ExpVector[] htl = new ExpVector[ l ];
        Coefficient[] lbc = new Coefficient[ l ];
        SolvablePolynomial[] p = new SolvablePolynomial[ l ];
	int j = 0;
        for ( i = 0; i < l; i++ ) { 
            p[i] = (SolvablePolynomial) P[i];
            m = p[i].leadingMonomial();
	    if ( m != null ) { 
               p[j] = p[i];
               htl[j] = (ExpVector) m.getKey();
               lbc[j] = (Coefficient) m.getValue();
	       j++;
	    }
	}
	l = j;
        ExpVector e;
        Coefficient a;
        boolean mt = false;
        SolvablePolynomial R = Ap.getZERO( Ap.getRelationTable(), Ap.getTermOrder() );

        SolvablePolynomial T = null;
        SolvablePolynomial Q = null;
        SolvablePolynomial S = Ap;
        while ( S.length() > 0 ) { 
	      m = S.leadingMonomial();
              e = (ExpVector) m.getKey();
              //logger.info("red = " + e);
              a = (Coefficient) m.getValue();
              for ( i = 0; i < l; i++ ) {
                  mt = ExpVector.EVMT( e, htl[i] );
                  if ( mt ) break; 
	      }
              if ( ! mt ) { 
                 //logger.debug("irred");
                 //T = new OrderedMapPolynomial( a, e );
                 R = (SolvablePolynomial)R.add( a, e );
                 S = (SolvablePolynomial)S.subtract( a, e ); 
		 // System.out.println(" S = " + S);
	      } else { 
                 //logger.debug("red");
		 e = ExpVector.EVDIF( e, htl[i] );
                 a = a.divide( lbc[i] );
                 Q = p[i].multiplyLeft( a, e );
                 S = (SolvablePolynomial)S.subtract( Q );
              }
	}
        return R;
    }


    /**
     * Normalform. Allows concurrent modification of the list.
     */

    public static OrderedPolynomial normalformMod(/*Array*/List Pp, 
                                                  OrderedPolynomial Ap) {  
        if ( Pp == null ) return Ap;
        if ( Pp.isEmpty() ) return Ap;
        int l = Pp.size();
        Map.Entry m;
        Map.Entry m1;
        Object[] P = Pp.toArray();
        Iterator it;
        ExpVector e;
        ExpVector f = null;
        Coefficient a;
        boolean mt = false;

        OrderedPolynomial Rz = Ap.getZERO( Ap.getTermOrder() );
        OrderedPolynomial R = Rz;
        OrderedPolynomial p = null;
        // OrderedPolynomial T = null;
        OrderedPolynomial Q = null;
        OrderedPolynomial S = Ap;
        while ( S.length() > 0 ) { 
              if ( Pp.size() != l ) { 
                 //long t = System.currentTimeMillis();
                 synchronized (Pp) { // required, bad in parallel
                    P = Pp.toArray();
                 }
                 l = P.length;
                 //t = System.currentTimeMillis()-t;
                 //logger.info("Pp.toArray() = " + t + " ms, size() = " + l);
                 S = Ap; // S.add(R)? // restart reduction ?
                 R = Rz; 
              }
	      m = S.leadingMonomial();
              e = (ExpVector) m.getKey();
              a = (Coefficient) m.getValue();
              for ( int i = 0; i < P.length ; i++ ) {
                  p = (OrderedPolynomial)P[i];
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
                 a = a.divide( (Coefficient)m1.getValue() );
                 Q = (OrderedPolynomial)p.multiply( a, e );
                 S = S.subtract( Q );
              }
	}
        return R;
    }


    /**
     * Normalform. Allows concurrent modification of the list.
     */

    public static OrderedPolynomial normalformMod(DistHashTable Pp, 
                                                  OrderedPolynomial Ap) {  
        if ( Pp == null ) return Ap;
        if ( Pp.isEmpty() ) return Ap;
        int l = Pp.size();
        Map.Entry m;
        Map.Entry m1;
        Object[] P;
        synchronized (Pp) { // required, ok in dist
           P = Pp.values().toArray();
        }
        Iterator it;
        ExpVector e;
        ExpVector f = null;
        Coefficient a;
        boolean mt = false;

        OrderedPolynomial Rz = Ap.getZERO( Ap.getTermOrder() );
        OrderedPolynomial R = Rz;
        OrderedPolynomial p = null;
        // OrderedPolynomial T = null;
        OrderedPolynomial Q = null;
        OrderedPolynomial S = Ap;
        while ( S.length() > 0 ) { 
              if ( Pp.size() != l ) { 
                 //long t = System.currentTimeMillis();
                 synchronized (Pp) { // required, ok in dist
                     P = Pp.values().toArray();
                 }
                 l = P.length;
                 //t = System.currentTimeMillis()-t;
                 //logger.info("Pp.values().toArray() = " + t + " ms, size() = " + l);
                 S = Ap; // S.add(R)? // restart reduction ?
                 R = Rz; 
              }
	      m = S.leadingMonomial();
              e = (ExpVector) m.getKey();
              a = (Coefficient) m.getValue();
              for ( int i = 0; i < P.length ; i++ ) {
                  p = (OrderedPolynomial)P[i];
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
                 a = a.divide( (Coefficient)m1.getValue() );
                 Q = (OrderedPolynomial)p.multiply( a, e );
                 S = S.subtract( Q );
              }
	}
        return R;
    }


    /**
     * Irreducible set.
     */

    public static ArrayList irreducibleSet(List Pp) {  
        OrderedPolynomial a;
        ArrayList P = new ArrayList();
        ListIterator it = Pp.listIterator();
        while ( it.hasNext() ) { 
            a = (OrderedPolynomial) it.next();
            if ( a.length() != 0 ) {
               a = a.monic();
               P.add( (Object) a );
	    }
	}
        int l = P.size();
        if ( l <= 1 ) return P;

        int irr = 0;
        ExpVector e;        
        ExpVector f;        
        logger.debug("irr = ");
        while ( irr != l ) {
            it = P.listIterator(); 
	    a = (OrderedPolynomial) it.next();
            P.remove(0);
            e = a.leadingExpVector();
            a = normalform( P, a );
            logger.debug(String.valueOf(irr));
            if ( a.length() == 0 ) { l--;
	       if ( l <= 1 ) { return P; }
	    } else {
	       f = a.leadingExpVector();
               if ( ExpVector.EVSIGN( f ) == 0 ) { 
		  P = new ArrayList(); 
                  P.add( a.monic() ); 
	          return P;
               }    
               if ( e.equals( f ) ) {
		  irr++;
	       } else {
                  irr = 0; a = a.monic();
	       }
               P.add( (Object) a );
	    }
	}
        //System.out.println();
	return P;
    }


    /**
     * Left irreducible set.
     */

    public static ArrayList leftIrreducibleSet(List Pp) {  
        SolvablePolynomial a;
        ArrayList P = new ArrayList();
        ListIterator it = Pp.listIterator();
        while ( it.hasNext() ) { 
            a = (SolvablePolynomial) it.next();
            if ( a.length() != 0 ) {
               a = (SolvablePolynomial)a.monic();
               P.add( a );
	    }
	}
        int l = P.size();
        if ( l <= 1 ) return P;

        int irr = 0;
        ExpVector e;        
        ExpVector f;        
        logger.debug("irr = ");
        while ( irr != l ) {
            it = P.listIterator(); 
	    a = (SolvablePolynomial) it.next();
            P.remove(0);
            e = a.leadingExpVector();
            a = leftNormalform( P, a );
            logger.debug(String.valueOf(irr));
            if ( a.length() == 0 ) { l--;
	       if ( l <= 1 ) { return P; }
	    } else {
	       f = a.leadingExpVector();
               if ( ExpVector.EVSIGN( f ) == 0 ) { 
		  P = new ArrayList(); 
                  P.add( (SolvablePolynomial)a.monic() ); 
	          return P;
               }    
               if ( e.equals( f ) ) {
		  irr++;
	       } else {
                  irr = 0; a = (SolvablePolynomial)a.monic();
	       }
               P.add( a );
	    }
	}
        //System.out.println();
	return P;
    }

}
