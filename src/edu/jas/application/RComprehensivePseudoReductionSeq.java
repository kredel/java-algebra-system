/*
 * $Id$
 */

package edu.jas.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.structure.RegularRingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;

import edu.jas.ring.RPseudoReductionSeq;


/**
 * Polynomial regular ring pseudo reduction sequential use algorithm.
 * Implements fraction free normalform algorithm.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class RComprehensivePseudoReductionSeq<C extends RegularRingElem<C>>
             extends RPseudoReductionSeq<C> {

    private static final Logger logger = Logger.getLogger(RComprehensivePseudoReductionSeq.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public RComprehensivePseudoReductionSeq() {
    }


    /**
     * S-Polynomial.
     * @param Ap polynomial.
     * @param Bp polynomial.
     * @return spol(Ap,Bp) the S-polynomial of Ap and Bp.
     */
    public GenPolynomial<C> 
           SPolynomial(GenPolynomial<C> Ap, 
                       GenPolynomial<C> Bp) {  
        if ( Bp == null || Bp.isZERO() ) {
           if ( Ap == null ) {
              return Bp;
           } 
           return Ap.ring.getZERO(); 
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Bp.ring.getZERO(); 
        }
        if ( debug ) {
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
     * S-Polynomial list.
     * @param Ap polynomial.
     * @param Bp polynomial.
     * @return spol(Ap,Bp) the list of S-polynomials of Ap and Bp.
     */
    public List<GenPolynomial<C>> 
           SPolynomialList(GenPolynomial<C> Ap, 
                           GenPolynomial<C> Bp) {  
        List<GenPolynomial<C>> spl = new ArrayList<GenPolynomial<C>>();
        if ( Bp == null || Bp.isZERO() ) {
           if ( Ap == null ) {
              spl.add(Bp);
              return spl;
           } 
           spl.add(Ap.ring.getZERO()); 
           return spl;
        }
        if ( Ap == null || Ap.isZERO() ) {
           spl.add(Bp.ring.getZERO()); 
           return spl;
        }
        if ( debug ) {
           if ( ! Ap.ring.equals( Bp.ring ) ) { 
              logger.error("rings not equal"); 
           }
        }
        GenPolynomial<C> LA = Ap.ring.getZERO().clone();
        GenPolynomial<C> LB = Bp.ring.getZERO().clone();
        GenPolynomial<C> App, Bpp, LAp, LBp, Cp, LC;
        C ai = Ap.ring.getZEROCoefficient();
        C bi = Bp.ring.getZEROCoefficient();
        C aip, bip;
        while ( !Ap.isZERO() && !Bp.isZERO() ) {
            Map.Entry<ExpVector,C> ma = Ap.leadingMonomial();
            Map.Entry<ExpVector,C> mb = Bp.leadingMonomial();
            ExpVector e = ma.getKey();
            ExpVector f = mb.getKey();
            ExpVector g  = ExpVector.EVLCM(e,f);
            ExpVector e1 = ExpVector.EVDIF(g,e);
            ExpVector f1 = ExpVector.EVDIF(g,f);

            C a = ma.getValue();
            C b = mb.getValue();
            aip = ai.idempotentOr(a);
            bip = bi.idempotentOr(b);
            if ( !aip.equals(ai) || !bip.equals(bi) ) {
               //System.out.println("ai  = " + ai + ", bi  = " + bi);
               System.out.println("aip = " + aip + ", bip = " + bip);
               App = Ap.multiply( b, e1 );
               LAp = LA.multiply( b, e1 );
               Bpp = Bp.multiply( a, f1 );
               LBp = LB.multiply( b, f1 );
               Cp = App.subtract(Bpp);
               LC = LAp.subtract(LBp);
               Cp = Cp.sum(LC);
               if ( !spl.contains(Cp) ) {
                  spl.add(Cp);
               }
            }
            // shift to next term
            LA.doPutToMap( e, a );
            LB.doPutToMap( f, b );
            Ap = Ap.reductum();
            Bp = Bp.reductum();
            ai = aip;
            bi = bip;
        }
        return spl;
    }


    /*
     * -------- boolean closure stuff -----------------------------------------
     * -------- is all in superclass
     */

}
