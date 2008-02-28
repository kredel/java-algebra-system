/*
 * $Id$
 */

package edu.jas.application;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import edu.jas.structure.RingFactory;
import edu.jas.structure.RegularRingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;

import edu.jas.ring.OrderedRPairlist;
import edu.jas.ring.RGroebnerBaseSeq;
import edu.jas.ring.RPseudoReduction;
import edu.jas.ring.Pair;
import edu.jas.ring.RPseudoReductionSeq;

import edu.jas.application.PolyUtilApp;

import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GCDFactory;


/**
 * Comprehensive Groebner Base with regular ring pseudo reduction sequential algorithm.
 * Implements C-Groebner bases and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ComprehensiveGroebnerBaseSeq<C extends RegularRingElem<C>> 
       extends RGroebnerBaseSeq<C>  {


    private static final Logger logger = Logger.getLogger(ComprehensiveGroebnerBaseSeq.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Greatest common divisor engine for coefficient content and primitive parts.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * Flag if engine should be used.
     */
    private final boolean usePP = false;


    /**
     * Coefficient ring factory.
     */
    protected final RingFactory<C> cofac;


    /**
     * Constructor.
     * @param rf coefficient ring factory.
     */
    public ComprehensiveGroebnerBaseSeq(RingFactory<C> rf) {
        this( new RPseudoReductionSeq<C>(), rf );
    }


    /**
     * Constructor.
     * @param red R-pseuso-Reduction engine
     * @param rf coefficient ring factory.
     * <b>Note:</b> red must be an instance of PseudoReductionSeq.
     */
    public ComprehensiveGroebnerBaseSeq(RPseudoReduction<C> red, RingFactory<C> rf) {
        super(red);
        cofac = rf;
        engine = (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getImplementation( rf );
        //not used: engine = (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getProxy( rf );
    }


    /**
     * R-Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a R-Groebner base, else false.
     */
    @Override
    public boolean isGB(int modv, List<GenPolynomial<C>> F) {  
        if ( F == null ) {
           return true;
        }
        GenPolynomial<C> pi, pj, s, h;
        for ( int i = 0; i < F.size(); i++ ) {
            pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                   continue;
                }
                // red.criterion4 not applicable
                s = red.SPolynomial( pi, pj );
                if ( s.isZERO() ) {
                   continue;
                }
                s = red.normalform( F, s );
                if ( ! s.isZERO() ) {
                   if ( debug ) {
                      System.out.println("p"+i+" = "+pi);
                      System.out.println("p"+j+" = "+pj);
                      System.out.println("s-pol = " + red.SPolynomial( pi, pj ) );
                      System.out.println("s-pol("+i+","+j+") != 0: " + s);
                      //System.out.println("red = " + red.getClass().getName());
                   }
                   return false;
                }
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
    public List<GenPolynomial<C>> 
             GB( int modv, 
                 List<GenPolynomial<C>> F ) {  
        if ( F == null ) {
           return F;
        }
        /* normalize input */
        GenPolynomialRing<C> pring = null;
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        for ( GenPolynomial<C> p : F ) { 
            if ( p != null && !p.isZERO() ) {
               if ( usePP && !p.isConstant() ) { // do not remove all factors
                  p = engine.basePrimitivePart(p); // not monic, no field
               }
               p = p.abs();
               if ( pring == null ) {
                  pring = p.ring;
               }
               G.add( p ); //G.add( 0, p ); //reverse list
            }
        }

        /* initial decompostition */
        PolynomialList<C> PL = null;
        PL = new PolynomialList<C>(pring,G);
        PL = PolyUtilApp.<C>productDecomposition(PL);
        G = PL.list;
        System.out.println("initial rcgb: " + PolyUtilApp.productSliceToString( PolyUtilApp.productSlice( (PolynomialList)PL ) ) );

        /* boolean closure */
        //List<GenPolynomial<C>> bcF = red.reducedBooleanClosure(G);
        //logger.info("#bcF-#F = " + (bcF.size()-G.size()));
        //G = bcF;
        if ( G.size() <= 1 ) {
           return G; // since boolean closed and no threads are activated
        }
        /* setup pair list */
        OrderedRPairlist<C> pairlist = null; 
        for ( GenPolynomial<C> p : G ) { 
            if ( pairlist == null ) {
               pairlist = new OrderedRPairlist<C>( modv, p.ring );
            }
            // putOne not required
            pairlist.put( p );
        }

        /* loop on critical pairs */
        Pair<C> pair;
        GenPolynomial<C> pi;
        GenPolynomial<C> pj;
        GenPolynomial<C> S;
        //GenPolynomial<C> D;
        GenPolynomial<C> H;
        List<GenPolynomial<C>> bcH;
        while ( pairlist.hasNext() ) {
              pair = pairlist.removeNext();
              //System.out.println("pair = " + pair);
              if ( pair == null ) continue; 

              pi = G.get( pair.i ); // pair.pi not up-to-date
              pj = G.get( pair.j ); // pair.pj not up-to-date
              if ( logger.isDebugEnabled() ) {
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
              if ( logger.isDebugEnabled() ) {
                  logger.debug("ht(S) = " + S.leadingExpVector() );
              }

              H = red.normalform( G, S );
              if ( H.isZERO() ) {
                  pair.setZero();
                  continue;
              }
              if ( logger.isDebugEnabled() ) {
                  logger.debug("ht(H) = " + H.leadingExpVector() );
              }
              if ( /*usePP &&*/ !H.isConstant() ) { // do not remove all factors
                  H = engine.basePrimitivePart(H); 
              }
              H = H.abs(); // not monic, no field
              if ( H.isConstant() && H.leadingBaseCoefficient().isFull() ) { 
                  // mostly useless
                  G.clear(); G.add( H );
                  return G; // not boolean closed ok, no threads are activated
              }
              if ( logger.isDebugEnabled() ) {
                  logger.debug("H = " + H );
              }
              if ( !H.isZERO() ) {
                  logger.info("Sred = " + H);
                  int g1 = G.size();
                  PL = new PolynomialList<C>(pring,G);
                  PL = PolyUtilApp.<C>productDecomposition( PL, H );
                  //G.add( H );
                  G = PL.list; // overwite G, pairlist not up-to-date
                  int g2 = G.size(); 
                  for ( int i = g1; i < g2; i++ ) { // g2-g1 == 1
                      //GenPolynomial<C> hh = G.get( i ); // since hh stays != 0
                      GenPolynomial<C> h = G.remove( g1 ); // since hh stays != 0
                      //logger.info("extend(Sred)_"+i+" = " + h);
                      //bcH = red.reducedBooleanClosure(G,hh);
                      //logger.info("#bcH = " + bcH.size());
                      //for ( GenPolynomial<C> h: bcH ) {
                      if ( /*usePP &&*/ !h.isConstant() ) { // do not remove all factors
                          h = engine.basePrimitivePart(h); 
                      }
                      h = h.abs(); // monic() not ok, since no field
                      logger.info("decomp(Sred) = " + h);
                      G.add( h );
                      pairlist.put( h ); // old polynomials not up-to-date
                      //}
                  }
                  if ( debug ) {
                      if ( !pair.getUseCriterion3() || !pair.getUseCriterion4() ) {
                          logger.info("H != 0 but: " + pair);
                      }
                  }
              }
        }
        logger.debug("#sequential list = " + G.size());
        System.out.println("\nisGB = " + isGB(G));
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );

        PL = new PolynomialList<C>(pring,G);
        System.out.println("\nGB = " + PL);

        System.out.println("final rcgb: " + PolyUtilApp.productSliceToString( PolyUtilApp.productSlice( (PolynomialList)PL ) ) );

        G = minimalGB(G);

        //PL = new PolynomialList<C>(pring,G);
        //System.out.println("GB.slice(0) = " + PolyUtilApp.productSliceRaw(PL,0));

        //G = red.irreducibleSet(G); // not correct since not boolean closed
        return G;
    }


    /**
     * Minimal ordered Groebner basis.
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     * @todo use primitivePart
     */
    @Override
    public List<GenPolynomial<C>> 
        minimalGB(List<GenPolynomial<C>> Gp) {  
        if ( Gp == null || Gp.size() <= 1 ) {
            return Gp;
        }
        // remove zero polynomials
        List<GenPolynomial<C>> G
            = new ArrayList<GenPolynomial<C>>( Gp.size() );
        for ( GenPolynomial<C> a : Gp ) { 
            if ( a != null && !a.isZERO() ) { // always true in GB()
               a = a.abs();  // already positive in GB
               G.add( a );
            }
        }
        // remove top reducible polynomials
        logger.info("minGB start with " + G.size() );
        GenPolynomial<C> a, b;
        List<GenPolynomial<C>> F;
        F = new ArrayList<GenPolynomial<C>>( G.size() );
        while ( G.size() > 0 ) {
            a = G.remove(0); b = a;
            if ( red.isStrongTopReducible(G,a) || red.isStrongTopReducible(F,a) ) {
               // try to drop polynomial 
               List<GenPolynomial<C>> ff;
               ff = new ArrayList<GenPolynomial<C>>( G );
               ff.addAll(F);
               a = red.normalform( ff, a );
               if ( a.isZERO() ) {
                  if ( !isGB( ff ) ) { // is really required, but why?
                     logger.info("minGB not dropped " + b);
                     F.add(b);
                  } else {
                     if ( debug ) {
                        logger.debug("minGB dropped " + b);
                     }
                  }
               } else {
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
            /*
            if ( red.isBooleanClosed(a) ) {
               List<GenPolynomial<C>> ff;
               ff = new ArrayList<GenPolynomial<C>>( G );
               ff.add( a );
               if ( isGB( ff ) ) {
                  if ( debug ) {
                     logger.debug("minGB reduced " + b + " to " +a);
                  }
                  G.add( a ); 
               } else {
                  logger.info("minGB not reduced " + b + " to " +a);
                  G.add( b ); 
               }
               continue;
            } else {
                G.add( b ); // do not reduce 
            }
            */
        }
        /* stratify: collect polynomials with equal leading terms */
        /*
        ExpVector e, f;
        F = new ArrayList<GenPolynomial<C>>( G.size() );
        List<GenPolynomial<C>> ff;
        ff = new ArrayList<GenPolynomial<C>>( G );
        for ( int i = 0; i < ff.size(); i++ ) {
            a = ff.get(i);
            if ( a == null || a.isZERO() ) {
               continue;
            }
            e = a.leadingExpVector();
            for ( int j = i+1; j < ff.size(); j++ ) {
                b = ff.get(j);
                if ( b == null || b.isZERO() ) {
                   continue;
                }
                f = b.leadingExpVector();
                if ( e.equals(f) ) {
                   //System.out.println("minGB e == f: " + a + ", " + b);
                   a = a.sum(b);
                   ff.set(j,null);
                }
            }
            F.add( a );
        }
        if ( isGB(F) ) {
           G = F;
        } else {
           logger.info("minGB not stratified " + F);
        }
        */
        logger.info("minGB end   with " + G.size() );
        return G;
    }


        /* info on boolean algebra element blocks 
        Map<C,List<GenPolynomial<C>>> bd = new TreeMap<C,List<GenPolynomial<C>>>();
        for ( GenPolynomial<C> p : G ) { 
            C cf = p.leadingBaseCoefficient();
            cf = cf.idempotent();
            List<GenPolynomial<C>> block = bd.get( cf );
            if ( block == null ) {
               block = new ArrayList<GenPolynomial<C>>();
            }
            block.add( p ); 
            bd.put( cf, block );
        }
        System.out.println("\nminGB bd:");
        for( C k: bd.keySet() ) {
           System.out.println("\nkey = " + k + ":");
           System.out.println("val = " + bd.get(k));
        }
        System.out.println();
        */

}
