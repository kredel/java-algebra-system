/*
 * $Id$
 */

package edu.jas.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    protected final RingFactory<C> pcofac;


    /**
     * Coefficient ring factory.
     */
    protected final RingFactory<R> cofac;


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
    public ComprehensiveGroebnerBaseSeq(RPseudoReduction<R> red, 
                                        RingFactory<C> rf) {
        super(null); // red not possible since type of type
        this.red = red;
        pcofac = rf;
        GenPolynomialRing<C> rfp = new GenPolynomialRing<C>( rf, 0 );
        Ideal<C> id = new Ideal<C>( rfp, new ArrayList<GenPolynomial<C>>(), true );
        RingFactory<Residue<C>> rr = new ResidueRing<C>( id );
        List<RingFactory<Residue<C>>> rl = new ArrayList<RingFactory<Residue<C>>>();
        rl.add(rr);
        ProductRing<Residue<C>> pr = new ProductRing<Residue<C>>(rl);
        RingFactory<Product<Residue<C>>> rpr = (RingFactory<Product<Residue<C>>>)pr;
        cofac = (RingFactory)rpr; // want <R>
        engine = (GreatestCommonDivisorAbstract<R>)GCDFactory.<R>getImplementation( cofac );
        //not used: engine = (GreatestCommonDivisorAbstract<R>)GCDFactory.<R>getProxy( rf );
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
        System.out.println("isCGB coeffs = " + coeffs);
        GenPolynomial<C> a = coeffs.iterator().next();
        RingFactory<GenPolynomial<C>> rcfac = a.ring;
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>)rcfac;
        PolynomialList<C> plist;
        List<GenPolynomial<C>> pl;
        Ideal<C> ideal;
        List<GenPolynomial<Residue<C>>> f;
        ResidueRing<C> rfac;
        GenPolynomialRing<Residue<C>> rpfac;
        for ( GenPolynomial<C> s : coeffs ) {
            if ( s.isConstant() ) {
               continue;
            }
            pl = new ArrayList<GenPolynomial<C>>( 1 );
            pl.add( s );
            plist = new PolynomialList<C>( cfac, pl );
            ideal = new Ideal<C>( plist, true ); // one element GB
            rfac = new ResidueRing<C>( ideal );
            rpfac = new GenPolynomialRing<Residue<C>>( rfac, pfac );
            f = PolyUtilApp.<C>toResidue( rpfac, F );
            System.out.println("isCGB sigma(F) = " + f);
            if ( ! bb.isGB( f ) ) {
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

        /* initial decompostition */
        /*
        GenPolynomialRing<C> rfp = new GenPolynomialRing<C>( pcofac, cpring );
        Ideal<C> id = new Ideal<C>( rfp, new ArrayList<GenPolynomial<C>>(), true );
        RingFactory<Residue<C>> rr = new ResidueRing<C>( id );
        List<RingFactory<Residue<C>>> rl = new ArrayList<RingFactory<Residue<C>>>();
        rl.add(rr);
        ProductRing<Residue<C>> pr = new ProductRing<Residue<C>>(rl);
        RingFactory<Product<Residue<C>>> rpr = (RingFactory<Product<Residue<C>>>)pr;
        RingFactory<R> rcofac = (RingFactory)rpr; // want <R>
        GenPolynomialRing<R> rpring = new GenPolynomialRing<R>( rcofac, pring );
        List<GenPolynomial<R>> G = new ArrayList<GenPolynomial<R>>();
        for ( GenPolynomial<GenPolynomial<C>> g : Gr ) {
            G.add( (GenPolynomial) g ); // hack but decomp works with it
        }
        */
        PolynomialList<R> PL = null;
        //PL = new PolynomialList<R>(rpring,G);
        PL = (PolynomialList) PolyUtilApp.<C>productEmptyDecomposition( Gr );
        GenPolynomialRing<R> rpring = PL.ring;

        PL = PolyUtilApp.<R>productDecomposition( PL );
        List<GenPolynomial<R>> G;
        G = PL.list;
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
                  break;
                  //return Gr; // not boolean closed ok, no threads are activated
              }
              if ( logger.isDebugEnabled() ) {
                  logger.debug("H = " + H );
              }
              if ( !H.isZERO() ) {
                  logger.info("Sred = " + H);
                  int g1 = G.size();
                  PL = new PolynomialList<R>(rpring,G);
                  PL = PolyUtilApp.<R>productDecomposition( PL, H );
                  //G.add( H );
                  G = PL.list; // overwite G, pairlist not up-to-date
                  int g2 = G.size(); 
                  for ( int i = g1; i < g2; i++ ) { // g2-g1 == 1
                      GenPolynomial<R> h = G.remove( g1 ); // since hh stays != 0
                      if ( /*usePP &&*/ !h.isConstant() ) { // do not remove all factors
                          h = engine.basePrimitivePart(h); 
                      }
                      h = h.abs(); // monic() not ok, since no field
                      logger.info("decomp(Sred) = " + h);
                      G.add( h );
                      pairlist.put( h ); // old polynomials not up-to-date
                  }
                  if ( debug ) {
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
        System.out.println("\nGB = " + PL);

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
               List<GenPolynomial<R>> ff;
               ff = new ArrayList<GenPolynomial<R>>( G );
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
        F = new ArrayList<GenPolynomial<R>>( G.size() );
        List<GenPolynomial<R>> ff;
        ff = new ArrayList<GenPolynomial<R>>( G );
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
        Map<C,List<GenPolynomial<R>>> bd = new TreeMap<C,List<GenPolynomial<R>>>();
        for ( GenPolynomialR p : G ) { 
            C cf = p.leadingBaseCoefficient();
            cf = cf.idempotent();
            List<GenPolynomial<R>> block = bd.get( cf );
            if ( block == null ) {
               block = new ArrayList<GenPolynomial<R>>();
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
