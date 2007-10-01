/*
 * $Id$
 */

package edu.jas.ring;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;

import edu.jas.structure.RingElem;


/**
 * Polynomial D-Reduction sequential use algorithm.
 * Implements normalform.
 * @author Heinz Kredel
 */

public class DReductionSeq<C extends RingElem<C>>
             extends ReductionAbstract<C> {

    private static final Logger logger = Logger.getLogger(DReductionSeq.class);


    /**
     * Constructor.
     */
    public DReductionSeq() {
    }


    /**
     * Normalform using e-reduction.
     * @typeparam C coefficient type.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return e-nf(Ap) with respect to Pp.
     */
    //SuppressWarnings("unchecked") // not jet working
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> Pp, 
                                       GenPolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenPolynomial<C>[])new GenPolynomial[l];
            //P = Pp.toArray();
            for ( int i = 0; i < Pp.size(); i++ ) {
                P[i] = Pp.get(i);
            }
        }
        Map.Entry<ExpVector,C> m;
        ExpVector[] htl = new ExpVector[ l ];
        C[] lbc = (C[]) new RingElem[ l ]; // want <C>
        GenPolynomial<C>[] p = (GenPolynomial<C>[])new GenPolynomial[ l ];
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
        GenPolynomial<C> T = Ap.ring.getZERO();
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
                 R = R.sum( a, e );
                 //S = S.subtract( a, e ); 
                 S = S.reductum(); 
                 // System.out.println(" S = " + S);
              } else { 
                 ExpVector f = ExpVector.EVDIF( e, htl[i] );
                 //logger.info("red div = " + f);
                 C r = a.remainder( lbc[i] );
                 C b = a.divide( lbc[i] );
                 Q = p[i].multiply( b, f );
                 if ( r.isZERO() ) {
                    S = S.subtract( Q ); // ok also with reductum
                 } else {
                    T = T.sum( r, e );   // ok also with R
                    S = S.reductum().subtract( Q.reductum() );
                 }
              }
        }
        return R.sum(T);
    }


    /**
     * Normalform using d-reduction.
     * @typeparam C coefficient type.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return e-nf(Ap) with respect to Pp.
     */
    //SuppressWarnings("unchecked") // not jet working
    public GenPolynomial<C> dNormalform(List<GenPolynomial<C>> Pp, 
                                        GenPolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenPolynomial<C>[])new GenPolynomial[l];
            //P = Pp.toArray();
            for ( int i = 0; i < Pp.size(); i++ ) {
                P[i] = Pp.get(i);
            }
        }
        Map.Entry<ExpVector,C> m;
        ExpVector[] htl = new ExpVector[ l ];
        C[] lbc = (C[]) new Object[ l ]; // want <C>
        GenPolynomial<C>[] p = (GenPolynomial<C>[])new GenPolynomial[ l ];
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
                 R = R.sum( a, e );
                 //S = S.subtract( a, e ); 
                 S = S.reductum(); 
                 // System.out.println(" S = " + S);
              } else { 
                 //logger.info("red div = " + e);
                 C r = a.remainder( lbc[i] );
                 if ( ! r.isZERO() ) {
                    //logger.debug("irred");
                    R = R.sum( a, e );
                    S = S.reductum(); 
                 } else {
                    ExpVector f = ExpVector.EVDIF( e, htl[i] );
                    C b = a.divide( lbc[i] );
                    Q = p[i].multiply( b, f );
                    S = S.subtract( Q ); // ok also with reductum
                 }
              }
        }
        return R;
    }


    /**
     * D-Polynomial.
     * @typeparam C coefficient type.
     * @param Ap polynomial.
     * @param Bp polynomial.
     * @return dpol(Ap,Bp) the D-polynomial of Ap and Bp.
     */
    public GenPolynomial<C> 
           DPolynomial(GenPolynomial<C> Ap, 
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
        C[] c = a.egcd(b);

     System.out.println("egcd[0] " + c[0]);

        GenPolynomial<C> App = Ap.multiply( c[1], e1 );
        GenPolynomial<C> Bpp = Bp.multiply( c[2], f1 );
        GenPolynomial<C> Cp = App.sum(Bpp);
        return Cp;
    }


    /**
     * Normalform with recording.
     * @typeparam C coefficient type.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the normal form of Ap wrt. Pp.
     */
    @SuppressWarnings("unchecked") // not jet working
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
        throw new RuntimeException("not jet implemented");
        /*
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
                R = R.sum( a, e );
                S = S.subtract( a, e ); 
                // System.out.println(" S = " + S);
                //throw new RuntimeException("Syzygy no GB");
            } else { 
                e = ExpVector.EVDIF( e, htl[i] );
                //logger.info("red div = " + e);
                C c = (C)lbc[i];
                a = a.divide( c );
                Q = p[i].multiply( a, e );
                S = S.subtract( Q );
                fac = row.get(i);
                if ( fac == null ) {
                    fac = zero.sum( a, e );
                } else {
                    fac = fac.sum( a, e );
                }
                row.set(i,fac);
            }
        }
        return R;
        */
    }

}
