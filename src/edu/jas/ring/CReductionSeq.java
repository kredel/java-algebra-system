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
import edu.jas.poly.ColorPolynomial;

import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;

import edu.jas.application.Ideal;


/**
 * Polynomial parametric ring Reduction sequential use algorithm.
 * Implements normalform and coloring and condition stuff.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class CReductionSeq<C extends GcdRingElem<C>>
    extends ReductionAbstract<C> 
            /*implements CReduction<C>*/ {

    private static final Logger logger = Logger.getLogger(CReductionSeq.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public CReductionSeq() {
    }


    /**
     * Normalform.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return nf(Ap) with respect to Pp.
     */
    //@SuppressWarnings("unchecked") 
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> Pp, 
                                       GenPolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
            return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
            return Ap;
        }
        return Ap;
    }


    /**
     * Normalform with recording.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the normal form of Ap wrt. Pp.
     */
    @SuppressWarnings("unchecked") 
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
        return Ap;
    }


    /*
     * -------- coloring and condition stuff -----------------------------------------
     */

    /**
     * Determine polynomial relative to conditions in F.
     * @param A polynomial.
     * @return new F.
     */
    public List<ColoredSystem<C>> 
        determine( List<ColoredSystem<C>> CS, 
                   GenPolynomial<GenPolynomial<C>> A) {  
        if ( A == null || A.isZERO() ) {
            return CS;
        }
        GenPolynomial<GenPolynomial<C>> zero = A.ring.getZERO();
        List<ColoredSystem<C>> NCS = new ArrayList<ColoredSystem<C>>( CS.size() );
        for ( ColoredSystem<C> cs : CS ) {
            GenPolynomial<GenPolynomial<C>> green = zero;
            GenPolynomial<GenPolynomial<C>> red;
            GenPolynomial<GenPolynomial<C>> white;
            GenPolynomial<GenPolynomial<C>> Ap = A;
            GenPolynomial<GenPolynomial<C>> Bp;
            ColorPolynomial<C> nz;
            ColoredSystem<C> NS;
            Ideal<C> id = cs.conditions.clone();
            System.out.println("starting id = " + id);
            List<ColorPolynomial<C>> S = cs.S;
            List<ColorPolynomial<C>> Sp;
            while( !Ap.isZERO() ) {
                Map.Entry<ExpVector,GenPolynomial<C>> m = Ap.leadingMonomial();
                ExpVector e = m.getKey();
                GenPolynomial<C> c = m.getValue();
                Bp = Ap.reductum();
                if ( c.isConstant() ) {
                   red = zero.sum(c,e);
                   white = Bp;
                   nz = new ColorPolynomial<C>(green,red,white); 
                   //System.out.println("nz = " + nz);
                   Sp = new ArrayList<ColorPolynomial<C>>( S );
                   Sp.add( nz );
                   NS = new ColoredSystem<C>( id, Sp );
                   System.out.println("NS = " + NS);
                   NCS.add( NS );
                   break;
                }
                if ( id.contains(c) ) {
                   System.out.println("c in id = " + c);
                   green = green.sum(c,e);
                   Ap = Bp;
                   continue;
                }
                red = zero.sum(c,e);
                white = Bp;
                nz = new ColorPolynomial<C>(green,red,white); 
                //System.out.println("nz = " + nz);
                Sp = new ArrayList<ColorPolynomial<C>>( S );
                Sp.add( nz );
                NS = new ColoredSystem<C>( id, Sp );
                System.out.println("NS = " + NS);
                NCS.add( NS );

                id = id.sum( c );
                System.out.println("id = " + id);
                if ( id.isONE() ) { // can treat remaining coeffs as red
                   break;
                }
                green = green.sum(c,e);
                Ap = Bp;
            }
        }
        return NCS;
    }


    /**
     * Determine polynomial list relative to conditions in F.
     * @param H polynomial list.
     * @return new determined F.
     */
    public List<ColoredSystem<C>> 
        determine( List<ColoredSystem<C>> CS, 
                   List<GenPolynomial<GenPolynomial<C>>> H) {  
        if ( H == null || H.size() == 0 ) {
            return CS;
        }
        List<ColoredSystem<C>> NCS = CS;
        for ( GenPolynomial<GenPolynomial<C>> A : H ) {
            NCS = determine( NCS, A );
        }
        return NCS;
    }

}
