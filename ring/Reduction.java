/*
 * $Id$
 */

package edu.jas.ring;

import java.util.TreeMap;
//import java.util.Comparator;
//import java.util.Set;
import java.util.Map;
import java.util.List;
//import java.util.LinkedList;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Iterator;

import org.apache.log4j.Logger;

import edu.jas.arith.Coefficient;
import edu.jas.poly.ExpVector;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.OrderedMapPolynomial;

import edu.jas.arith.BigRational;
import edu.jas.poly.RatPolynomial;
import edu.jas.poly.TreePolynomial;


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
	      return Ap;
	   }
	   if ( Ap == null || Ap.isZERO() ) {
	      return (OrderedPolynomial) Bp.negate();
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

        OrderedPolynomial App = (OrderedPolynomial) Ap.multiply( b, e1 );
        OrderedPolynomial Bpp = (OrderedPolynomial) Bp.multiply( a, f1 );
       
        OrderedPolynomial Cp = (OrderedPolynomial) App.subtract(Bpp);
        return Cp;
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
	}
        ExpVector ei = A.leadingExpVector();
        ExpVector ej = B.leadingExpVector();
        ExpVector g = ExpVector.EVSUM(ei,ej);
	// boolean t =  g == e ;
        ExpVector h = ExpVector.EVDIF(g,e);
        int s = ExpVector.EVSIGN(h);
        return ! ( s == 0 );
    }

    public static boolean GBCriterion4(OrderedPolynomial A, 
                                       OrderedPolynomial B) {  
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

    public static OrderedPolynomial Normalform(List Pp, 
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
		 logger.debug("irred");
                 //T = new OrderedMapPolynomial( a, e );
                 R = R.add( a, e );
                 S = S.subtract( a, e ); 
		 // System.out.println(" S = " + S);
	      }
              else { 
		 logger.debug("red");
		 e = ExpVector.EVDIF( e, htl[i] );
                 a = a.divide( lbc[i] );
                 Q = (OrderedPolynomial) p[i].multiply( a, e );
                 S = S.subtract( Q );
              }
	}
        return R;
    }


    /**
     * Irreducible set.
     */

    public static ArrayList IrreducibleSet(List Pp) {  
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
            a = Normalform( P, a );
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

}
