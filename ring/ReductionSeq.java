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
 * Polynomial Reduction sequential class.
 * Implements normalform.
 * @author Heinz Kredel
 */

public class ReductionSeq<C extends RingElem<C>>
             extends ReductionAbstract<C> {

    private static Logger logger = Logger.getLogger(ReductionSeq.class);


    /**
     * Constructor.
     */
    public ReductionSeq() {
    }


    /**
     * Normalform.
     * @param C coefficient type.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return nf(Ap) with respect to Pp.
     */
    @SuppressWarnings("unchecked") // not jet working
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> Pp, 
                                       GenPolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        Map.Entry<ExpVector,C> m;
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = new GenPolynomial[l];
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
     * Normalform with recording.
     * @param C coefficient type.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the normal form of Ap wrt. Pp.
     */
    public GenPolynomial<C> 
           normalform(List<GenPolynomial<C>> row,
                      List<GenPolynomial<C>> Pp, 
                      GenPolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
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
        Map.Entry<ExpVector,C> m;
	int j = 0;
        int i;
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
        GenPolynomial<C> zero = Ap.ring.getZERO();
        GenPolynomial<C> R = Ap.ring.getZERO();

        GenPolynomial<C> fac = null;
        // GenPolynomial<C> T = null;
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
                 R = R.add( a, e );
                 S = S.subtract( a, e ); 
		 // System.out.println(" S = " + S);
                 //throw new RuntimeException("Syzygy no GB");
	      } else { 
		 e = ExpVector.EVDIF( e, htl[i] );
                 //logger.info("red div = " + e);
                 a = a.divide( (C)lbc[i] );
                 Q = p[i].multiply( a, e );
                 S = S.subtract( Q );
                 fac = row.get(i);
                 if ( fac == null ) {
                    fac = zero.add( a, e );
                 } else {
                    fac = fac.add( a, e );
                 }
                 row.set(i,fac);
              }
	}
        return R;
    }

}
