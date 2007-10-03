/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.Iterator;
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
             extends ReductionAbstract<C> 
             implements DReduction<C> {

    private static final Logger logger = Logger.getLogger(DReductionSeq.class);


    /**
     * Constructor.
     */
    public DReductionSeq() {
    }


    /**
     * Normalform using d-reduction.
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

        //System.out.println("egcd[0] " + c[0]);

        GenPolynomial<C> App = Ap.multiply( c[1], e1 );
        GenPolynomial<C> Bpp = Bp.multiply( c[2], f1 );
        GenPolynomial<C> Cp = App.sum(Bpp);
        return Cp;
    }


    /**
     * D-Polynomial with recording.
     * @typeparam C coefficient type.
     * @param S recording matrix, is modified.
     * @param i index of Ap in basis list.
     * @param Ap a polynomial.
     * @param j index of Bp in basis list.
     * @param Bp a polynomial.
     * @return dpol(Ap, Bp), the d-Polynomial for Ap and Bp.
     */
    public GenPolynomial<C> 
           DPolynomial(List<GenPolynomial<C>> S,
                       int i,
                       GenPolynomial<C> Ap, 
                       int j,
                       GenPolynomial<C> Bp) {
        throw new RuntimeException("not jet implemented");
    }


    /**
     * GB criterium 4.
     * Use only for commutative polynomial rings.
     * @typeparam C coefficient type.
     * @param A polynomial.
     * @param B polynomial.
     * @param e = lcm(ht(A),ht(B))
     * @return true if the S-polynomial(i,j) is required, else false.
     */
    public boolean criterion4(GenPolynomial<C> A, 
                              GenPolynomial<C> B, 
                              ExpVector e) {  
        throw new RuntimeException("criterion4 does not hold");
    }


    /**
     * GB criterium 4.
     * Use only for commutative polynomial rings.
     * @typeparam C coefficient type.
     * @param A polynomial.
     * @param B polynomial.
     * @return true if the S-polynomial(i,j) is required, else false.
     */
    public boolean criterion4(GenPolynomial<C> A, 
                              GenPolynomial<C> B) {  
        throw new RuntimeException("criterion4 does not hold");
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


    /**
     * Irreducible set.
     * @typeparam C coefficient type.
     * @param Pp polynomial list.
     * @return a list P of polynomials which are in normalform wrt. P.
     */
    public List<GenPolynomial<C>> irreducibleSet(List<GenPolynomial<C>> Pp) {  
        ArrayList<GenPolynomial<C>> P = new ArrayList<GenPolynomial<C>>();
        if ( Pp == null ) {
           return null;
        }
        for ( GenPolynomial<C> a : Pp ) {
            if ( !a.isZERO() ) {
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
            //it = P.listIterator(); 
            a = P.get(0); //it.next();
            P.remove(0);
            e = a.leadingExpVector();
            a = normalform( P, a );
            logger.debug(String.valueOf(irr));
            if ( a.isZERO() ) { l--;
               if ( l <= 1 ) { return P; }
            } else {
               f = a.leadingExpVector();
               if ( e.equals( f ) ) {
                  irr++;
               } else {
                  irr = 0; 
               }
               P.add( a );
            }
        }
        //System.out.println();
        return P;
    }

}
