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
import edu.jas.poly.GenSolvablePolynomial;

import edu.jas.structure.RingElem;


/**
 * Polynomial Reduction abstract class.
 * Implements common S-Polynomial, normalform, criterion 4 
 * module criterion and irreducible set.
 * @author Heinz Kredel
 */

public abstract class ReductionAbstract<C extends RingElem<C>>
                      implements Reduction<C> {

    private static Logger logger = Logger.getLogger(ReductionAbstract.class);
    private boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public ReductionAbstract() {
    }


    /**
     * S-Polynomial.
     * @typeparam C coefficient type.
     * @param Ap polynomial.
     * @param Bp polynomial.
     * @return spol(Ap,Bp) the S-polynomial of Ap and Bp.
     */
    public GenPolynomial<C> 
           SPolynomial(GenPolynomial<C> Ap, 
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
     * S-Polynomial with recording.
     * @typeparam C coefficient type.
     * @param S recording matrix, is modified. 
     *        <b>Note</b> the negative Spolynomial is recorded as 
     *        required by all applications.
     * @param i index of Ap in basis list.
     * @param Ap a polynomial.
     * @param j index of Bp in basis list.
     * @param Bp a polynomial.
     * @return Spol(Ap, Bp), the S-Polynomial for Ap and Bp.
     */
    public GenPolynomial<C> 
        SPolynomial(List<GenPolynomial<C>> S,
                    int i,
                    GenPolynomial<C> Ap, 
                    int j,
                    GenPolynomial<C> Bp) {  
        if ( logger.isInfoEnabled() ) {
            if ( Bp == null || Bp.isZERO() ) {
                throw new RuntimeException("Spol B is zero");
            }
            if ( Ap == null || Ap.isZERO() ) {
                throw new RuntimeException("Spol A is zero");
            }
            if ( ! Ap.ring.equals( Bp.ring ) ) { 
                logger.error("term orderings not equal"); 
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
        GenPolynomial<C> Cp  = App.subtract(Bpp);

        GenPolynomial<C> zero = Ap.ring.getZERO();
        GenPolynomial<C> As = zero.add( b.negate(), e1 );
        GenPolynomial<C> Bs = zero.add( a /*correct .negate()*/, f1 );
        S.set( i, As );
        S.set( j, Bs );

        return Cp;
    }


    /**
     * Module criterium.
     * @typeparam C coefficient type.
     * @param modv number of module variables.
     * @param A polynomial.
     * @param B polynomial.
     * @return true if the module S-polynomial(i,j) is required.
     */
    public boolean moduleCriterion(int modv, 
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
     * Use only for commutative polynomial rings.
     * @typeparam C coefficient type.
     * @param A polynomial.
     * @param B polynomial.
     * @return true if the S-polynomial(i,j) is required, else false.
     */
    public boolean criterion4(GenPolynomial<C> A, 
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
     * Normalform Set.
     * @typeparam C coefficient type.
     * @param Ap polynomial list.
     * @param Pp polynomial list.
     * @return list of nf(a) with respect to Pp for all a in Ap.
     */
    public List<GenPolynomial<C>> normalform(List<GenPolynomial<C>> Pp, 
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
     * @typeparam C coefficient type.
     * @param Pp polynomial list.
     * @return a list P of polynomials which are in normalform wrt. P.
     */
    public List<GenPolynomial<C>> irreducibleSet(List<GenPolynomial<C>> Pp) {  
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
     * Is reduction of normal form.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @param Np nf(Pp,Ap), a normal form of Ap wrt. Pp.
     * @return true, if Np + sum( row[i]*Pp[i] ) == Ap, else false.
     */

    public boolean 
           isReductionNF(List<GenPolynomial<C>> row,
                         List<GenPolynomial<C>> Pp, 
                         GenPolynomial<C> Ap,
                         GenPolynomial<C> Np) {
        if ( row == null && Pp != null ) {
            return false;
        }
        if ( row != null && Pp == null ) {
            return false;
        }
        if ( row.size() != Pp.size() ) {
            return false;
        }
        GenPolynomial<C> t = Np;
        GenPolynomial<C> r;
        GenPolynomial<C> p;
        for ( int m = 0; m < Pp.size(); m++ ) {
            r = row.get(m);
            p = Pp.get(m);
            if ( r != null && p != null ) {
               if ( t == null ) {
                  t = r.multiply(p);
               } else {
                  t = t.add( r.multiply(p) );
               }
            }
            //System.out.println("r = " + r );
            //System.out.println("p = " + p );
        }
        if ( debug ) {
           logger.info("t = " + t );
           logger.info("a = " + Ap );
        }
        if ( t == null ) {
           if ( Ap == null ) {
              return true;
           } else {
              return Ap.isZERO();
           }
        } else {
           t = t.subtract( Ap );
           return t.isZERO();
        }
    }
}
