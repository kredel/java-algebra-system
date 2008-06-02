/*
 * $Id$
 */

package edu.jas.application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ColorPolynomial;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.GcdRingElem;

import edu.jas.application.Ideal;


/**
 * Polynomial parametric ring Reduction sequential use algorithm.
 * Implements normalform and coloring and condition stuff.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class CReductionSeq<C extends GcdRingElem<C>>
             /*extends ReductionAbstract<C>*/ 
             /*implements CReduction<C>*/ {

    private static final Logger logger = Logger.getLogger(CReductionSeq.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public CReductionSeq() {
    }


    /**
     * S-Polynomial.
     * @param Ap polynomial.
     * @param Bp polynomial.
     * @return spol(Ap,Bp) the S-polynomial of Ap and Bp.
     */
    public ColorPolynomial<C> 
           SPolynomial(ColorPolynomial<C> Ap, 
                       ColorPolynomial<C> Bp) {  
        if ( Bp == null || Bp.isZERO() ) {
           return Bp;
        }
        if ( Ap == null || Ap.isZERO() ) {
            return Ap;
        }

        Map.Entry<ExpVector,GenPolynomial<C>> ma = Ap.red.leadingMonomial();
        Map.Entry<ExpVector,GenPolynomial<C>> mb = Bp.red.leadingMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g  = ExpVector.EVLCM(e,f);
        ExpVector e1 = ExpVector.EVDIF(g,e);
        ExpVector f1 = ExpVector.EVDIF(g,f);

        GenPolynomial<C> a = ma.getValue();
        GenPolynomial<C> b = mb.getValue();

        ColorPolynomial<C> App = Ap.multiply( b, e1 );
        ColorPolynomial<C> Bpp = Bp.multiply( a, f1 );
        ColorPolynomial<C> Cp = App.subtract(Bpp);
        return Cp;
    }


    /**
     * S-Polynomial with recording.
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
        throw new RuntimeException("not implemented");
    }


    /**
     * Is top reducible.
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is top reducible with respect to P.
     */
    public boolean isTopReducible(List<ColorPolynomial<C>> P, 
                                  ColorPolynomial<C> A) {  
        if ( P == null || P.isEmpty() ) {
           return false;
        }
        if ( A == null || A.isZERO() ) {
           return false;
        }
        boolean mt = false;
        ExpVector e = A.leadingExpVector();
        for ( ColorPolynomial<C> p : P ) {
            mt = ExpVector.EVMT( e, p.leadingExpVector() );
            if ( mt ) {
               return true;
            } 
        }
        return false;
    }


    /**
     * Is reducible.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return true if Ap is reducible with respect to Pp.
     */
    public boolean isReducible(List<ColorPolynomial<C>> Pp, 
                               ColorPolynomial<C> Ap) {  
        return !isNormalform(Pp,Ap);
    }


    /**
     * Is in Normalform.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return true if Ap is in normalform with respect to Pp.
     */
    @SuppressWarnings("unchecked") 
    public boolean isNormalform(List<ColorPolynomial<C>> Pp, 
                                ColorPolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return true;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return true;
        }
        int l;
        ColorPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = new ColorPolynomial[l];
            //P = Pp.toArray();
            for ( int i = 0; i < Pp.size(); i++ ) {
                P[i] = Pp.get(i);
            }
        }
        ExpVector[] htl = new ExpVector[ l ];
        ColorPolynomial<C>[] p = new ColorPolynomial[ l ];
        Map.Entry<ExpVector,GenPolynomial<C>> m;
        int i;
        int j = 0;
        for ( i = 0; i < l; i++ ) { 
            p[i] = P[i];
            m = p[i].red.leadingMonomial();
            if ( m != null ) { 
               p[j] = p[i];
               htl[j] = m.getKey();
               j++;
            }
        }
        l = j;
        boolean mt = false;
        for ( ExpVector e : Ap.red.getMap().keySet() ) { 
            for ( i = 0; i < l; i++ ) {
                mt = ExpVector.EVMT( e, htl[i] );
                if ( mt ) {
                   return false;
                } 
            }
        }
        for ( ExpVector e : Ap.white.getMap().keySet() ) { 
            for ( i = 0; i < l; i++ ) {
                mt = ExpVector.EVMT( e, htl[i] );
                if ( mt ) {
                   return false;
                } 
            }
        }
        return true;
    }


    /**
     * Is in Normalform.
     * @param Pp polynomial list.
     * @return true if each Ap in Pp is in normalform with respect to Pp\{Ap}.
     */
    public boolean isNormalform( List<ColorPolynomial<C>> Pp ) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return true;
        }
        ColorPolynomial<C> Ap;
        List<ColorPolynomial<C>> P = new LinkedList<ColorPolynomial<C>>( Pp );
        int s = P.size();
        for ( int i = 0; i < s; i++ ) {
            Ap = P.remove(i);
            if ( ! isNormalform(P,Ap) ) {
               return false;
            }
            P.add(Ap);
        }
        return true;
    }


    /**
     * Normalform.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return nf(Ap) with respect to Pp.
     */
    @SuppressWarnings("unchecked") 
    public ColorPolynomial<C> normalform(List<ColorPolynomial<C>> Pp, 
                                         ColorPolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        Map.Entry<ExpVector,GenPolynomial<C>> m;
        int l;
        ColorPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = new ColorPolynomial[l];
            //P = Pp.toArray();
            for ( int i = 0; i < Pp.size(); i++ ) {
                P[i] = Pp.get(i);
            }
        }
        ExpVector[] htl = new ExpVector[ l ];
        Object[] lbc = new Object[ l ]; // want C[] 
        ColorPolynomial<C>[] p = new ColorPolynomial[ l ];
        int i;
        int j = 0;
        for ( i = 0; i < l; i++ ) { 
            if ( P[i] == null ) {
               continue;
            }
            p[i] = P[i];
            m = p[i].red.leadingMonomial();
            if ( m != null ) { 
               p[j] = p[i];
               htl[j] = m.getKey();
               lbc[j] = m.getValue();
               j++;
            }
        }
        l = j;
        ExpVector e;
        GenPolynomial<C> a;
        boolean mt = false;
        GenPolynomial<GenPolynomial<C>> zero = p[0].red.ring.getZERO();
        ColorPolynomial<C> R = new ColorPolynomial<C>(zero,zero,zero);

        //ColorPolynomial<C> T = null;
        ColorPolynomial<C> Q = null;
        ColorPolynomial<C> S = Ap;
        while ( S.length() > 0 ) { 
              m = S.leadingMonomial();
              e = m.getKey();
              a = m.getValue();
              //System.out.println("NF, e = " + e);
              for ( i = 0; i < l; i++ ) {
                  mt = ExpVector.EVMT( e, htl[i] );
                  if ( mt ) break; 
              }
              if ( ! mt ) { 
                 //logger.debug("irred");
                 //T = new OrderedMapPolynomial( a, e );
                 R = R.sum( a, e );
                 S = S.subtract( a, e ); 
                 // System.out.println(" S = " + S);
              } else { 
                 e = ExpVector.EVDIF( e, htl[i] );
                 //logger.info("red div = " + e);
                 GenPolynomial<C> c = (GenPolynomial<C>) lbc[i];
                 if ( a.remainder(c).isZERO() ) {   //c.isUnit() ) {
                    a = a.divide( c );
                 } else {
                    S = S.multiply( c );
                    R = R.multiply( c );
                 }
                 Q = p[i].multiply( a, e );
                 S = S.subtract( Q );
              }
        }
        return R;
    }


    /**
     * Normalform with recording.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the normal form of Ap wrt. Pp.
     */
    @SuppressWarnings("unchecked") 
        public ColorPolynomial<C> 
        normalform(List<ColorPolynomial<C>> row,
                   List<ColorPolynomial<C>> Pp, 
                   ColorPolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
            return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
            return Ap;
        }
        throw new RuntimeException("not implemented");
        //return Ap;
    }


    /*
     * -------- coloring and condition stuff ------------------------------
     */

    /**
     * Determine polynomial relative to conditions in F.
     * @param A polynomial.
     * @return new F.
     */
    public List<ColoredSystem<C>> 
        unusedDetermine( List<ColoredSystem<C>> CS, 
                   GenPolynomial<GenPolynomial<C>> A) {  
        if ( A == null || A.isZERO() ) {
            return CS;
        }
        GenPolynomial<GenPolynomial<C>> zero = A.ring.getZERO();
        List<ColoredSystem<C>> NCS = new ArrayList<ColoredSystem<C>>( CS ); //.size() );
        for ( ColoredSystem<C> cs : CS ) {
            GenPolynomial<GenPolynomial<C>> green = zero;
            GenPolynomial<GenPolynomial<C>> red;
            GenPolynomial<GenPolynomial<C>> white;
            GenPolynomial<GenPolynomial<C>> Ap = A;
            GenPolynomial<GenPolynomial<C>> Bp;
            ColorPolynomial<C> nz;
            ColoredSystem<C> NS;
            Ideal<C> id = cs.condition.zero; //.clone();
            //System.out.println("starting id = " + id);
            List<ColorPolynomial<C>> S = cs.list;
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
                   Condition<C> ids = new Condition<C>(id);
                   NS = new ColoredSystem<C>( ids, Sp );
                   NS = NS.reDetermine();
                   //System.out.println("NS = " + NS);
                   NCS.add( NS );
                   break;
                }
                if ( id.contains(c) ) {
                   //System.out.println("c in id = " + c);
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
                // re determine existing polynomials
                Condition<C> ids = new Condition<C>(id);
                NS = new ColoredSystem<C>( ids, Sp );
                if ( logger.isDebugEnabled() ) {
                   logger.info("new determined " + NS);
                }
                NS = NS.reDetermine();
                if ( logger.isDebugEnabled() ) {
                   logger.info("re determined " + NS);
                }
                //System.out.println("NS = " + NS);
                NCS.add( NS );

                id = id.sum( c );
                //System.out.println("id = " + id);
                if ( id.isONE() ) { // can treat remaining coeffs as green
                   //System.out.println("dropping " + cs);
                   //System.out.println("by green " + green.sum(c,e));
                   break; // drop system
                }
                green = green.sum(c,e);
                Ap = Bp;
                if ( Bp.isZERO() ) {
                    // add green poly and condition
                    red = red.subtract(c,e);
                    nz = new ColorPolynomial<C>(green,red,white); 
                    System.out.println("nz = " + nz);
                    //if ( !nz.isZERO() ) {
                        Sp = new ArrayList<ColorPolynomial<C>>( S );
                        Sp.add( nz );
                        // re determine existing polynomials
                        ids = new Condition<C>(id);
                        NS = new ColoredSystem<C>( ids, Sp );
                        if ( logger.isDebugEnabled() ) {
                            logger.info("new determined " + NS);
                        }
                        NS = NS.reDetermine();
                        if ( logger.isDebugEnabled() ) {
                            logger.info("re determined " + NS);
                        }
                        //System.out.println("NS = " + NS);
                        NCS.add( NS );
                        //}
                }
            }
        }
        if ( logger.isDebugEnabled() ) {
           logger.info("determined " + NCS);
        }
        return NCS;
    }


    /**
     * Determine polynomial list relative to conditions in F.
     * @param H polynomial list.
     * @return new determined F.
     */
    public List<ColoredSystem<C>> 
        unusedDetermine( List<ColoredSystem<C>> CS, 
                         List<GenPolynomial<GenPolynomial<C>>> H) {  
        if ( H == null || H.size() == 0 ) {
            return CS;
        }
        List<ColoredSystem<C>> NCS = CS;
        for ( GenPolynomial<GenPolynomial<C>> A : H ) {
            NCS = unusedDetermine( NCS, A );
        }
        return NCS;
    }


    /**
     * Determine polynomial list.
     * @param H polynomial list.
     * @return new determined list of colored systems.
     */
    public List<ColoredSystem<C>> 
           determine( List<GenPolynomial<GenPolynomial<C>>> H) {  
        if ( H == null || H.size() == 0 ) {
           List<ColoredSystem<C>> CS = new ArrayList<ColoredSystem<C>>();
           return CS;
        }
        List<Condition<C>> cd = caseDistinction( H );
        return determine(cd,H);
    }


    /**
     * Determine polynomial list.
     * @param H polynomial list.
     * @param cd case distiction, an condition list.
     * @return new determined list of colored systems.
     */
    public List<ColoredSystem<C>> 
           determine( List<Condition<C>> cd,
                      List<GenPolynomial<GenPolynomial<C>>> H) {  
        List<ColoredSystem<C>> CS = new ArrayList<ColoredSystem<C>>();
        if ( H == null || H.size() == 0 ) {
           return CS;
        }
        for ( Condition<C> cond : cd ) {
            System.out.println("cond = " + cond);
            if ( cond.zero.isONE() ) { // should not happen
               System.out.println("ideal is one = " + cond.zero);
               continue; // can treat all coeffs as green
            }
            //if ( cond.isEmpty() ) { // not use this code
            //   System.out.println("condition is empty = " + cond);
            //   continue; // can skip condition (?)
            //}
            List<ColorPolynomial<C>> S = new ArrayList<ColorPolynomial<C>>();
            for ( GenPolynomial<GenPolynomial<C>> A : H ) {
                ColorPolynomial<C> nz = determine( cond, A );
                System.out.println("nz = " + nz);
                if ( nz!= null && !nz.isZERO() ) {
                   S.add( nz );
                }
            }
            ColoredSystem<C> cs = new ColoredSystem<C>( cond, S );
            //System.out.println("cs = " + cs);
            CS.add( cs );
        }
        return CS;
    }


    /**
     * Determine polynomial.
     * @param A polynomial.
     * @param cond a condition.
     * @return new determined colored polynomial.
     */
    public ColorPolynomial<C> 
           determine( Condition<C> cond,
                      GenPolynomial<GenPolynomial<C>> A) {  
        ColorPolynomial<C> cp = null;
        if ( A == null ) {
           return cp;
        }
        GenPolynomial<GenPolynomial<C>> zero = A.ring.getZERO();
        GenPolynomial<GenPolynomial<C>> green = zero;
        GenPolynomial<GenPolynomial<C>> red = zero;
        GenPolynomial<GenPolynomial<C>> white = zero;
        if ( A.isZERO() ) {
           cp = new ColorPolynomial<C>(green,red,white); 
           return cp;
        }
        GenPolynomial<GenPolynomial<C>> Ap = A;
        GenPolynomial<GenPolynomial<C>> Bp;
        while( !Ap.isZERO() ) {
            Map.Entry<ExpVector,GenPolynomial<C>> m = Ap.leadingMonomial();
            ExpVector e = m.getKey();
            GenPolynomial<C> c = m.getValue();
            Bp = Ap.reductum();
            if ( c.isConstant() ) {
                red = zero.sum(c,e);
                white = Bp;
                cp = new ColorPolynomial<C>(green,red,white); 
                //System.out.println("cp = " + cp);
                break;
            }
            if ( cond.zero.contains( c ) ) {
                //System.out.println("c in id = " + c);
                green = green.sum(c,e);
                Ap = Bp;
            } else {
                if ( !cond.nonZero.contains( c ) ) {
                   //System.out.println("error, c non zero = " + c);
                   throw new RuntimeException("error, c non zero = " + c);
                }
                red = zero.sum(c,e);
                white = Bp;
                cp = new ColorPolynomial<C>(green,red,white); 
                //System.out.println("cp = " + cp);
                break;
            }
        }
        // return zero for zero or green polynomial
        if ( red.isZERO() ) {
           if ( !white.isZERO() ) { // debug
              throw new RuntimeException("error, white non zero = " + white);
           }
           //System.out.println("all green terms = " + green);
           cp = new ColorPolynomial<C>(green,red,white); 
        }
        return cp;
    }


    /**
     * Determine polynomial relative to a condition.
     * @param cs a colored system.
     * @param A color polynomial.
     * @return list of colored systems.
     */
    public List<ColoredSystem<C>> 
        determine( ColoredSystem<C> cs, 
                   ColorPolynomial<C> A ) {  
        List<ColoredSystem<C>> NCS = new ArrayList<ColoredSystem<C>>();
        if ( A == null || A.isZERO() ) {
           //NCS.add( cs );
           return NCS;
        }
        List<ColorPolynomial<C>> S = cs.list;
        Condition<C> cond = cs.condition; //.clone();
        OrderedCPairlist<C> pl = cs.pairlist;
        List<ColorPolynomial<C>> Sp;
        ColorPolynomial<C> nz;
        ColoredSystem<C> NS;
//         if ( A.isDetermined() ) { ... } // dont use this   
        System.out.println("to determine = " + A);
        GenPolynomial<GenPolynomial<C>> Ap = A.getPolynomial();
        List<Condition<C>> cd = caseDistinction( cs, A );
        for ( Condition<C> cnd : cd ) {
           Sp = new ArrayList<ColorPolynomial<C>>( S );
           nz = determine( cnd, Ap );
           if ( nz == null || nz.isZERO() ) {
              continue;
           }
           System.out.println("new determinated nz = " + nz);
           Sp.add( nz );
           NS = new ColoredSystem<C>( cnd, Sp, pl );
           //NS = NS.reDetermine();
           NCS.add( NS );
        }
        //System.out.println("new determination = " + NCS);
        return NCS;
    }


    /**
     * Determine polynomial relative to a condition and add pairs.
     * @param cs a colored system.
     * @param A color polynomial.
     * @return list of colored systems.
     */
    public List<ColoredSystem<C>> 
        determineAddPairs( ColoredSystem<C> cs, 
                           ColorPolynomial<C> A ) {  
        List<ColoredSystem<C>> NCS = new ArrayList<ColoredSystem<C>>();
        if ( A == null || A.isZERO() ) {
           //NCS.add( cs );
           return NCS;
        }
        List<ColorPolynomial<C>> S = cs.list;
        Condition<C> cond = cs.condition; //.clone(); done in Condition itself
        OrderedCPairlist<C> pl = cs.pairlist;
        List<ColorPolynomial<C>> Sp;
        ColorPolynomial<C> nz;
        ColoredSystem<C> NS;
//         if ( A.isDetermined() ) { ... } // dont use this   
        System.out.println("to determine = " + A);
        GenPolynomial<GenPolynomial<C>> Ap = A.getPolynomial();
        List<Condition<C>> cd = caseDistinction( cs, A );
        System.out.println("# cases = " + cd.size());
        for ( Condition<C> cnd : cd ) {
           Sp = new ArrayList<ColorPolynomial<C>>( S );
           nz = determine( cnd, Ap );
           if ( nz == null || nz.isZERO() ) {
              continue;
           }
           System.out.println("new determinated nz = " + nz);
           Sp.add( nz );
           OrderedCPairlist<C> PL = pl.clone();
           PL.put( nz );
           NS = new ColoredSystem<C>( cnd, Sp, PL );
           //NS = NS.reDetermine();
           NCS.add( NS );
        }
        //System.out.println("new determination = " + NCS);
        return NCS;
    }


    /**
     * Case distinction conditions of parametric polynomial list.
     * @param L list of parametric polynomials.
     * @return list of conditions as case distinction.
     */
    public List<Condition<C>> oldCaseDistinction( List<GenPolynomial<GenPolynomial<C>>> L) {  
        List<Condition<C>> cd = new ArrayList<Condition<C>>();
        if ( L == null || L.size() == 0 ) {
            return cd;
        }
        GenPolynomialRing<GenPolynomial<C>> fac = L.get(0).ring;
        RingFactory<GenPolynomial<C>> crfac = fac.coFac;
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) crfac;
        List<GenPolynomial<C>> F = new ArrayList<GenPolynomial<C>>();
        Ideal<C> I = new Ideal<C>(cfac,F);

        List<GenPolynomial<C>> NZ = new ArrayList<GenPolynomial<C>>();
        Condition<C> sc = new Condition<C>( I, NZ );
        System.out.println("starting cond = " + sc);
        cd.add(sc);

        List<Condition<C>> C = cd;
        GenPolynomial<GenPolynomial<C>> zero = fac.getZERO();
        GenPolynomial<GenPolynomial<C>> Ap;
        GenPolynomial<GenPolynomial<C>> Bp;

        for ( GenPolynomial<GenPolynomial<C>> A : L ) {
            C = new ArrayList<Condition<C>>( /*leer!*/ );
            for ( Condition<C> cond : cd ) {
                Condition<C> cz = cond;
                Ap = A;
                while( !Ap.isZERO() ) {
                    GenPolynomial<C> c = Ap.leadingBaseCoefficient();
                    Bp = Ap.reductum();
                    if ( c.isConstant() ) { // red
                        System.out.println("c constant = " + c);
                        Ap = Bp;
                        break;
                    }
                    if ( cz.zero.contains( c ) ) { // green
                        System.out.println("c in zero = " + c);
                        Ap = Bp;
                        continue;
                    }
                    if ( cz.nonZero.contains( c ) ) { // red
                        System.out.println("c in nonZero = " + c);
                        Ap = Bp;
                        break;
                    }
                    // white
                    System.out.println("c white = " + c);
                    Condition<C> nc = cz.sumNonZero( c );
                    C.add( nc );
                    cz = cz.sumZero( c );
                    Ap = Bp;
                    if ( cz.zero.isONE() ) { // can treat remaining coeffs as green
                       System.out.println("dropping " + cz);
                       break; // drop system
                    }
                }
                if ( C.contains(cz) ) {
                   System.out.println("double entry " + cz);
                }
                if ( !cz.zero.isONE() ) { // can treat remaining coeffs as green
                   C.add( cz );
                } else {
                  System.out.println("dropped " + cz);
                }
                if ( C.size() == 0 ) {
                   System.out.println("readd starting cond = " + sc);
                   C.add(sc);
                }
            }
            cd = C;
        }
        //System.out.println("cd = " + cd);
        return cd;
    }


    /**
     * Case distinction conditions of parametric polynomial list.
     * @param L list of parametric polynomials.
     * @return list of conditions as case distinction.
     */
    public List<Condition<C>> caseDistinction( List<GenPolynomial<GenPolynomial<C>>> L) {  
        List<Condition<C>> cd = new ArrayList<Condition<C>>();
        if ( L == null || L.size() == 0 ) {
            return cd;
        }
        for ( GenPolynomial<GenPolynomial<C>> A : L ) {
            if ( A != null && !A.isZERO() ) {
               cd = caseDistinction( cd, A );
            }
        }
        //System.out.println("cd = " + cd);
        return cd;
    }


    /**
     * Case distinction conditions of parametric polynomial list.
     * @param cd a list of conditions.
     * @param A a parametric polynomial.
     * @return list of conditions as case distinction.
     */
    public List<Condition<C>> caseDistinction( List<Condition<C>> cd,
                                               GenPolynomial<GenPolynomial<C>> A) {  
        if ( A == null || A.isZERO() ) {
            return cd;
        }
        // construct empty condition
        GenPolynomialRing<GenPolynomial<C>> fac = A.ring;
        RingFactory<GenPolynomial<C>> crfac = fac.coFac;
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) crfac;
        List<GenPolynomial<C>> F = new ArrayList<GenPolynomial<C>>();
        Ideal<C> I = new Ideal<C>(cfac,F);
        List<GenPolynomial<C>> NZ = new ArrayList<GenPolynomial<C>>();
        Condition<C> sc = new Condition<C>( I, NZ );
        //System.out.println("starting cond = " + sc);
        if ( cd == null ) {
           cd = new ArrayList<Condition<C>>();
        }
        if ( cd.size() == 0 ) {
           cd.add(sc);
        }
        GenPolynomial<GenPolynomial<C>> Ap;
        GenPolynomial<GenPolynomial<C>> Bp;

        List<Condition<C>> C = new ArrayList<Condition<C>>( /*leer!*/ );
        for ( Condition<C> cond : cd ) {
            Condition<C> cz = cond;
            Ap = A;
            while( !Ap.isZERO() ) {
                GenPolynomial<C> c = Ap.leadingBaseCoefficient();
                Bp = Ap.reductum();
                if ( c.isConstant() ) { // red
                    //System.out.println("c constant = " + c);
                    Ap = Bp;
                    break;
                }
                if ( cz.zero.contains( c ) ) { // green
                    //System.out.println("c in zero = " + c);
                    Ap = Bp;
                    continue;
                }
                if ( cz.nonZero.contains( c ) ) { // red
                    //System.out.println("c in nonZero = " + c);
                    Ap = Bp;
                    break;
                }
                // white
                //System.out.println("c white = " + c);
                Condition<C> nc = cz.sumNonZero( c );
                C.add( nc );
                cz = cz.sumZero( c );
                Ap = Bp;
                if ( cz.zero.isONE() ) { // can treat remaining coeffs as green
                    System.out.println("dropping " + cz);
                    break; // drop system
                }
            }
            if ( C.contains(cz) ) {
                System.out.println("double entry " + cz);
            }
            if ( !cz.zero.isONE() ) { // can treat remaining coeffs as green
                C.add( cz );
            } else {
               System.out.println("dropped " + cz);
            }
            if ( C.size() == 0 ) {
               System.out.println("re-add starting cond = " + sc);
               C.add(sc);
            }
        }
        //System.out.println("C = " + C);
        return C;
    }


    /**
     * Case distinction ideals of parametric polynomial list.
     * @param A a parametric polynomial.
     * @param cs a colored system.
     * @return list of case distinction conditions.
     */
    public List<Condition<C>> caseDistinction( ColoredSystem<C> cs,
                                               ColorPolynomial<C> A) {  
        List<Condition<C>> cd = new ArrayList<Condition<C>>();
        if ( A == null || A.isZERO() ) {
            return cd;
        }
        Condition<C> cond = cs.condition;
        //System.out.println("starting condition = " + cond);
        cd.add( cond );
        //if ( A.isDetermined() ) {
        //   return cd;
        //}
        //GenPolynomial<GenPolynomial<C>> Ap = A.getEssentialPolynomial();
        GenPolynomial<GenPolynomial<C>> Ap = A.getPolynomial();
        cd = caseDistinction( cd, Ap );
        //System.out.println("new case distinction:");
        //System.out.println("cd = " + cd);
        return cd;
    }

}
