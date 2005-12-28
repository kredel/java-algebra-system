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
 * Solvable Polynomial Reduction class.
 * Implements left normalform.
 * @author Heinz Kredel
 */

public class SolvableReductionSeq<C extends RingElem<C>>
             extends SolvableReductionAbstract<C> {

    private static Logger logger = Logger.getLogger(SolvableReductionSeq.class);


    /**
     * Constructor.
     */
    public SolvableReductionSeq() {
    }


    /**
     * Left Normalform.
     * @param C coefficient type.
     * @param Ap solvable polynomial.
     * @param Pp solvable polynomial list.
     * @return left-nf(Ap) with respect to Pp.
     */
    public GenSolvablePolynomial<C> 
           leftNormalform(List<GenSolvablePolynomial<C>> Pp, 
                          GenSolvablePolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        int l;
        Map.Entry<ExpVector,C> m;
        GenSolvablePolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = new GenSolvablePolynomial[l];
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
     * LeftNormalform with recording.
     * @param C coefficient type.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the left normal form of Ap wrt. Pp.
     */
    public GenSolvablePolynomial<C> 
           leftNormalform(List<GenSolvablePolynomial<C>> row,
                          List<GenSolvablePolynomial<C>> Pp, 
                          GenSolvablePolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        int l = Pp.size();
        GenSolvablePolynomial<C>[] P = new GenSolvablePolynomial[ l ];
        synchronized (Pp) {
            //P = Pp.toArray();
            for ( int i = 0; i < Pp.size(); i++ ) {
                P[i] = Pp.get(i);
            }
	}
        ExpVector[] htl = new ExpVector[ l ];
        Object[] lbc = new Object[ l ]; // want <C>
        GenSolvablePolynomial<C>[] p = new GenSolvablePolynomial[ l ];
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
        GenSolvablePolynomial<C> zero = Ap.ring.getZERO();
        GenSolvablePolynomial<C> R = Ap.ring.getZERO();

        GenSolvablePolynomial<C> fac = null;
        // GenSolvablePolynomial<C> T = null;
        GenSolvablePolynomial<C> Q = null;
        GenSolvablePolynomial<C> S = Ap;
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
                 R = (GenSolvablePolynomial<C>)R.add( a, e );
                 S = (GenSolvablePolynomial<C>)S.subtract( a, e ); 
		 // System.out.println(" S = " + S);
                 throw new RuntimeException("Syzygy no leftGB");
	      } else { 
		 e = ExpVector.EVDIF( e, htl[i] );
                 //logger.info("red div = " + e);
                 a = a.divide( (C)lbc[i] );
                 Q = p[i].multiplyLeft( a, e );
                 S = (GenSolvablePolynomial<C>)S.subtract( Q );
                 fac = row.get(i);
                 if ( fac == null ) {
                    fac = (GenSolvablePolynomial<C>)zero.add( a, e );
                 } else {
                    fac = (GenSolvablePolynomial<C>)fac.add( a, e );
                 }
                 row.set(i,fac);
              }
	}
        return R;
    }

}
