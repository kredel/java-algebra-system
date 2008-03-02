/*
 * $Id$
 */

package edu.jas.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collection;

import org.apache.log4j.Logger;

import edu.jas.structure.RingFactory;
import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RegularRingElem;
import edu.jas.structure.ProductRing;
import edu.jas.structure.Product;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;

import edu.jas.ring.GroebnerBase;
import edu.jas.ring.GroebnerBaseAbstract;
import edu.jas.ring.GroebnerBaseSeq;
import edu.jas.ring.OrderedRPairlist;
import edu.jas.ring.RGroebnerBaseSeq;
import edu.jas.ring.RPseudoReduction;
import edu.jas.ring.Pair;
import edu.jas.ring.RPseudoReductionSeq;

import edu.jas.application.PolyUtilApp;

import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GCDFactory;


/**
 * Comprehensive Groebner Base with 
 * regular ring pseudo reduction sequential algorithm.
 * Implements C-Groebner bases and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ComprehensiveGroebnerBaseSeq<C extends GcdRingElem<C>, 
                                          R extends RegularRingElem<R>> 
       extends GroebnerBaseAbstract<GenPolynomial<C>>  {


    private static final Logger logger = Logger.getLogger(ComprehensiveGroebnerBaseSeq.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Greatest common divisor engine for coefficient content and primitive parts.
     */
    protected final GreatestCommonDivisorAbstract<R> engine;


    /**
     * Coefficient Groebner base engine.
     */
    protected final GroebnerBase<Residue<C>> bb;


    /**
     * Reduction engine.
     */
    protected RPseudoReduction<R> red;  // shadow super.red 


    /**
     * Flag if engine should be used.
     */
    private final boolean usePP = false;


    /**
     * Polynomial coefficient ring factory.
     */
    protected final RingFactory<C> cofac;


    /**
     * Constructor.
     * @param rf base coefficient ring factory.
     */
    public ComprehensiveGroebnerBaseSeq(RingFactory<C> rf) {
        this( new RPseudoReductionSeq<R>(), rf );
    }


    /**
     * Constructor.
     * @param red R-pseuso-Reduction engine
     * @param rf base coefficient ring factory.
     */
    @SuppressWarnings("unchecked") 
    public ComprehensiveGroebnerBaseSeq(RPseudoReduction<R> red, 
                                        RingFactory<C> rf) {
        super(null); // red not possible since type of type
        this.red = red;
        cofac = rf;
        // selection for C but used for R:
        engine = (GreatestCommonDivisorAbstract<R>) GCDFactory.<C>getImplementation( cofac );
        //not used: engine = (GreatestCommonDivisorAbstract<C>)GCDFactory.<R>getProxy( rf );
        bb = new GroebnerBaseSeq<Residue<C>>();
    }


    /**
     * Comprehensive-Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a Comprehensive-Groebner base, else false.
     */
    @Override
    public boolean isGB(int modv, List<GenPolynomial<GenPolynomial<C>>> F) {  
        if ( F == null || F.size() == 0 ) {
           return true;
        }
        // collect coefficients
        GenPolynomialRing<GenPolynomial<C>> pfac = null;
        Set<GenPolynomial<C>> coeffs = new HashSet<GenPolynomial<C>>();
        for ( GenPolynomial<GenPolynomial<C>> p : F ) {
            if ( pfac == null ) {
               if ( p != null ) {
                  pfac = p.ring;
               }
            }
            Collection<GenPolynomial<C>> c = p.getMap().values();
            coeffs.addAll( c );
        }
        if ( coeffs.size() == 0 ) { // only zero polynomials
           return true;
        }
        GenPolynomialRing<C> pcfac = (GenPolynomialRing<C>) pfac.coFac;
        boolean isField = pcfac.coFac.isField();
        Set<GenPolynomial<C>> cfs = new HashSet<GenPolynomial<C>>();
        for ( GenPolynomial<C> p : coeffs ) {
            if ( p.isZERO() || p.isConstant() ) { // all are non zero
               continue;
            }
            if ( isField ) {
               p = p.monic();
            } else {
               p = p.abs();
            }
            cfs.add( p );
        }
        coeffs = cfs;
        if ( coeffs.size() == 0 ) { // only constant coefficients
           if ( ! super.isGB( modv, F ) ) {
              System.out.println("isCGB F = ");
              return false;
           }
           return true;
        }
        System.out.println("isCGB coeffs = " + coeffs);
        // specialization anf test for GB
        GenPolynomial<C> a = coeffs.iterator().next();
        RingFactory<GenPolynomial<C>> rcfac = a.ring;
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>)rcfac;
        PolynomialList<C> plist;
        List<GenPolynomial<C>> pl;
        Ideal<C> ideal;
        List<GenPolynomial<Residue<C>>> f;
        ResidueRing<C> rfac;
        GenPolynomialRing<Residue<C>> rpfac;
        for ( GenPolynomial<C> s : coeffs ) { // need power set
            pl = new ArrayList<GenPolynomial<C>>( 1 );
            pl.add( s );
            plist = new PolynomialList<C>( cfac, pl );
            ideal = new Ideal<C>( plist, true ); // one element GB
            rfac = new ResidueRing<C>( ideal );
            rpfac = new GenPolynomialRing<Residue<C>>( rfac, pfac );
            f = PolyUtilApp.<C>toResidue( rpfac, F );
            if ( ! bb.isGB( modv, f ) ) {
               System.out.println("isCGB sigma(F) = " + f);
               return false;
            }
        }
        GenPolynomial<C> s1, s2;
        // not correct
        for ( Iterator<GenPolynomial<C>> ci = coeffs.iterator(); 
              ci.hasNext();   ) { // need power set
            s1 = ci.next();
            pl = new ArrayList<GenPolynomial<C>>( 2 );
            pl.add( s1 );
            if ( !ci.hasNext() ) {
               break;
            }
            s2 = ci.next(); // not correct
            pl.add( s2 );
            plist = new PolynomialList<C>( cfac, pl );
            ideal = new Ideal<C>( plist ); // one element GB
            rfac = new ResidueRing<C>( ideal );
            rpfac = new GenPolynomialRing<Residue<C>>( rfac, pfac );
            f = PolyUtilApp.<C>toResidue( rpfac, F );
            if ( ! bb.isGB( modv, f ) ) {
               System.out.println("isCGB sigma(F) = " + f);
               return false;
            }
        }
        return true;
    }


    /**
     * R-Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a R-Groebner base of F.
     */
    @Override
    @SuppressWarnings("unchecked") 
    public List<GenPolynomial<GenPolynomial<C>>> 
             GB( int modv, 
                 List<GenPolynomial<GenPolynomial<C>>> F ) {  
        if ( F == null ) {
           return F;
        }
        /* normalize input */
        GenPolynomialRing<GenPolynomial<C>> pring = null;
        GenPolynomialRing<C> cpring = null;
        List<GenPolynomial<GenPolynomial<C>>> Gr 
            = new ArrayList<GenPolynomial<GenPolynomial<C>>>();
        for ( GenPolynomial<GenPolynomial<C>> p : F ) { 
            if ( p != null && !p.isZERO() ) {
                //if ( usePP && !p.isConstant() ) { // do not remove all factors
                //  p = engine.basePrimitivePart(p); // not monic, no field
                //}
               p = p.abs();
               if ( pring == null ) {
                  pring = p.ring;
                  cpring = (GenPolynomialRing<C>) pring.coFac;
               }
               Gr.add( p ); //G.add( 0, p ); //reverse list
            }
        }

        /* initial setup and starting decompostition */
        PolynomialList<R> PL
           = (PolynomialList) PolyUtilApp.<C>productEmptyDecomposition( Gr );
        GenPolynomialRing<R> rpring = PL.ring;
        PL = PolyUtilApp.<R>productDecomposition( PL );
        List<GenPolynomial<R>> G = PL.list;
        System.out.println("initial rcgb: " + PolyUtilApp.productSliceToString( PolyUtilApp.productSlice( (PolynomialList)PL ) ) );

        if ( G.size() <= 1 ) {
           // return Gr; // since boolean closed and no threads are activated
        }
        /* setup pair list */
        OrderedRPairlist<R> pairlist = null; 
        for ( GenPolynomial<R> p : G ) { 
            if ( pairlist == null ) {
               pairlist = new OrderedRPairlist<R>( modv, p.ring );
            }
            // putOne not required
            pairlist.put( p );
        }

        /* loop on critical pairs */
        Pair<R> pair;
        GenPolynomial<R> pi;
        GenPolynomial<R> pj;
        GenPolynomial<R> S;
        //GenPolynomial<R> D;
        GenPolynomial<R> H;
        while ( pairlist.hasNext() ) {
              pair = pairlist.removeNext();
              //System.out.println("pair = " + pair);
              if ( pair == null ) continue; 

              pi = G.get( pair.i ); // pair.pi not up-to-date
              pj = G.get( pair.j ); // pair.pj not up-to-date
              if ( debug ) {
                 logger.info("pi    = " + pi );
                 logger.info("pj    = " + pj );
              }
              if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                 continue;
              }

              // S-polynomial -----------------------
              //if ( pair.getUseCriterion3() ) { // correct ?
              //if ( pair.getUseCriterion4() ) { // correct ?
              S = red.SPolynomial( pi, pj );
              if ( S.isZERO() ) {
                  pair.setZero();
                  continue;
              }
              //System.out.println("S("+pair.pi+","+pair.pj+") = " + S);
              if ( debug ) {
                  logger.debug("ht(S) = " + S.leadingExpVector() );
              }

              H = red.normalform( G, S );
              if ( H.isZERO() ) {
                  pair.setZero();
                  continue;
              }
              if ( debug ) {
                  logger.debug("ht(H) = " + H.leadingExpVector() );
              }
              if ( /*usePP &&*/ !H.isConstant() ) { // do not remove all factors
                  H = engine.basePrimitivePart(H); 
              }
              H = H.abs(); // not monic, no field
              if ( H.isConstant() && H.leadingBaseCoefficient().isFull() ) { 
                  // mostly useless
                  G.clear(); G.add( H );
                  break;
                  //return Gr; // not boolean closed ok, no threads are activated
              }
              if ( debug ) {
                  logger.debug("H = " + H );
              }
              if ( ! H.isZERO() ) {
                  logger.info("Sred = " + H);
                  int g1 = G.size();
                  PL = new PolynomialList<R>(rpring,G);
                  PL = PolyUtilApp.<R>productDecomposition( PL, H );
                  //G.add( H );
                  G = PL.list; // overwite G, pairlist not up-to-date
                  int g2 = G.size(); 
                  for ( int i = g1; i < g2; i++ ) { // g2-g1 == 1
                      GenPolynomial<R> h = G.remove( g1 ); // since h stays != 0
                      if ( /*usePP &&*/ !h.isConstant() ) { // do not remove all factors
                          h = engine.basePrimitivePart(h); 
                      }
                      h = h.abs(); // monic() not ok, since no field
                      logger.info("decomp(Sred) = " + h);
                      G.add( h );
                      pairlist.put( h ); // old polynomials not up-to-date
                  }
                  if ( false && debug ) {
                      if ( !pair.getUseCriterion3() || !pair.getUseCriterion4() ) {
                          logger.info("H != 0 but: " + pair);
                      }
                  }
              }
        }
        logger.debug("#sequential list = " + G.size());
        //System.out.println("\nisGB = " + isGB(G));
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );

        PL = new PolynomialList<R>(rpring,G);
        System.out.println("\nRGB = " + PL);

        System.out.println("final rcgb: " + PolyUtilApp.productSliceToString( PolyUtilApp.productSlice( (PolynomialList)PL ) ) );

        G = minimalCGB(G);

        PL = new PolynomialList<R>(rpring,G);
        PolynomialList<C> Gpl = PolyUtilApp.<C>productSlicesUnion( (PolynomialList)PL );
        Gr = (List) Gpl.list;
        return Gr;
    }


    /**
     * Minimal ordered Groebner basis.
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     * @todo use primitivePart
     */
    //@Override
    public List<GenPolynomial<R>> 
        minimalCGB(List<GenPolynomial<R>> Gp) {  
        if ( Gp == null || Gp.size() <= 1 ) {
            return Gp;
        }
        // remove zero polynomials
        List<GenPolynomial<R>> G
            = new ArrayList<GenPolynomial<R>>( Gp.size() );
        for ( GenPolynomial<R> a : Gp ) { 
            if ( a != null && !a.isZERO() ) { // always true in GB()
               a = a.abs();  // already positive in GB
               G.add( a );
            }
        }
        // remove top reducible polynomials
        logger.info("minGB start with " + G.size() );
        GenPolynomial<R> a, b;
        List<GenPolynomial<R>> F;
        F = new ArrayList<GenPolynomial<R>>( G.size() );
        while ( G.size() > 0 ) {
            a = G.remove(0); b = a;
            if ( red.isStrongTopReducible(G,a) || red.isStrongTopReducible(F,a) ) {
               // try to drop polynomial 
               List<GenPolynomial<R>> ff;
               ff = new ArrayList<GenPolynomial<R>>( G );
               ff.addAll(F);
               a = red.normalform( ff, a );
               if ( a.isZERO() ) {
                   //if ( !isGB( ff ) ) { // is really required, but why?
                   //  logger.info("minGB not dropped " + b);
                   //  F.add(b);
                   //} else {
                   if ( debug ) {
                      logger.debug("minGB dropped " + b);
                   //  }
                  }
               } else {
                  logger.info("minGB cannot drop " + b + " reduced to " + a);
                  F.add(a);
               }
            } else { // not top reducible, keep polynomial
               F.add(a);
            }
        }
        G = F;
        // reduce remaining polynomials
        int len = G.size();
        int el = 0;
        while ( el < len ) {
            el++;
            a = G.remove(0); b = a;
            System.out.println("minGB a = " + a);
            a = red.normalform( G, a );
            System.out.println("minGB red(a) = " + a);
            if ( usePP && !a.isConstant() ) { // do not remove all factors
               a = engine.basePrimitivePart(a); // not a.monic() since no field
            }
            a = a.abs();
            G.add( a );
        }
        logger.info("minGB end   with " + G.size() );
        return G;
    }

}
