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
 * Solvable Polynomial Reduction abstract class.
 * Implements common left S-Polynomial, left normalform and 
 * left irreducible set.
 * @author Heinz Kredel
 */

public abstract class SolvableReductionAbstract<C extends RingElem<C>>
                      implements SolvableReduction<C> {

    private static Logger logger = Logger.getLogger(SolvableReductionAbstract.class);


    /**
     * Constructor.
     */
    public SolvableReductionAbstract() {
    }


    /**
     * Left S-Polynomial.
     * @param C coefficient type.
     * @param Ap solvable polynomial.
     * @param Bp solvable polynomial.
     * @return left-spol(Ap,Bp) the left S-polynomial of Ap and Bp.
     */
    public GenSolvablePolynomial<C> 
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
     * S-Polynomial with recording.
     * @param C coefficient type.
     * @param S recording matrix, is modified.
     * @param i index of Ap in basis list.
     * @param Ap a polynomial.
     * @param j index of Bp in basis list.
     * @param Bp a polynomial.
     * @return leftSpol(Ap, Bp), the left S-Polynomial for Ap and Bp.
     */
    public GenSolvablePolynomial<C> 
           leftSPolynomial(List<GenSolvablePolynomial<C>> S,
                           int i,
                           GenSolvablePolynomial<C> Ap, 
                           int j,
                           GenSolvablePolynomial<C> Bp) {  
        if ( logger.isInfoEnabled() ) {
	   if ( Bp == null || Bp.isZERO() ) {
               throw new RuntimeException("Spol B is zero");
	   }
	   if ( Ap == null || Ap.isZERO() ) {
               throw new RuntimeException("Spol A is zero");
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

        GenSolvablePolynomial<C> App = Ap.multiplyLeft( b, e1 );
        GenSolvablePolynomial<C> Bpp = Bp.multiplyLeft( a, f1 );
        GenSolvablePolynomial<C> Cp = (GenSolvablePolynomial<C>)App.subtract(Bpp);

        GenSolvablePolynomial<C> zero 
           = (GenSolvablePolynomial<C>)Ap.ring.getZERO();
        GenSolvablePolynomial<C> As = (GenSolvablePolynomial<C>)zero.add( b.negate(), e1 );
        GenSolvablePolynomial<C> Bs = (GenSolvablePolynomial<C>)zero.add( a, f1 );
        S.set( i, As );
        S.set( j, Bs );
        return Cp;
    }


    /**
     * Left Normalform.
     * @param C coefficient type.
     * @param Ap solvable polynomial.
     * @param Pp solvable polynomial list.
     * @return left-nf(Ap) with respect to Pp.
     */
    public abstract GenSolvablePolynomial<C> 
           leftNormalform(List<GenSolvablePolynomial<C>> Pp, 
                          GenSolvablePolynomial<C> Ap);


    /**
     * LeftNormalform with recording.
     * @param C coefficient type.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the left normal form of Ap wrt. Pp.
     */
    public abstract GenSolvablePolynomial<C> 
           leftNormalform(List<GenSolvablePolynomial<C>> row,
                          List<GenSolvablePolynomial<C>> Pp, 
                          GenSolvablePolynomial<C> Ap);


    /**
     * Left Normalform Set.
     * @param C coefficient type.
     * @param Ap solvable polynomial list.
     * @param Pp solvable polynomial list.
     * @return list of left-nf(a) with respect to Pp for all a in Ap.
     */
    public List<GenSolvablePolynomial<C>> 
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
     * @param C coefficient type.
     * @param Pp solvable polynomial list.
     * @return a list P of solvable polynomials which are in normalform wrt. P.
     */
    public List<GenSolvablePolynomial<C>> 
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

}
