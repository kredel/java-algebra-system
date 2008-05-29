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
import edu.jas.poly.ColorPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;

import edu.jas.ring.GroebnerBase;
import edu.jas.ring.GroebnerBaseAbstract;
import edu.jas.ring.GroebnerBasePseudoSeq;
import edu.jas.ring.OrderedRPairlist;
import edu.jas.ring.RGroebnerBaseSeq;
import edu.jas.ring.RPseudoReduction;
import edu.jas.ring.Pair;
import edu.jas.ring.RPseudoReductionSeq;

import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GCDFactory;

import edu.jas.application.PolyUtilApp;
import edu.jas.application.PolyUtilComp;


/**
 * Comprehensive Groebner Base sequential algorithm.
 * Implements C-Groebner bases and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ComprehensiveGroebnerBaseSeq<C extends GcdRingElem<C>>
             /*extends GroebnerBaseAbstract<GenPolynomial<C>>*/  {


    private static final Logger logger = Logger.getLogger(ComprehensiveGroebnerBaseSeq.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Greatest common divisor engine for coefficient content and primitive parts.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * Flag if gcd engine should be used.
     */
    private final boolean notFaithfull = false;


    /**
     * Comprehensive reduction engine.
     */
    protected final CReductionSeq<C> cred;  


    /**
     * Polynomial coefficient ring factory.
     */
    protected final RingFactory<C> cofac;


    /**
     * Constructor.
     * @param rf base coefficient ring factory.
     */
    public ComprehensiveGroebnerBaseSeq(RingFactory<C> rf) {
        this( new CReductionSeq<C>(), rf );
    }


    /**
     * Constructor.
     * @param red C-pseuso-Reduction engine
     * @param rf base coefficient ring factory.
     */
    @SuppressWarnings("unchecked") 
    public ComprehensiveGroebnerBaseSeq( CReductionSeq<C> red, 
                                         RingFactory<C> rf ) {
        //super(null); // red not possible since type of type
        cred = red;
        cofac = rf;
        // selection for C but used for R:
        GreatestCommonDivisor<C> e = GCDFactory.<C>getImplementation( cofac );
        engine = (GreatestCommonDivisorAbstract<C>) e;
        //not used: engine = GCDFactory.<R>getProxy( rf );
    }


    /**
     * Comprehensive-Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a Comprehensive-Groebner base, else false.
     */
    //@Override
    public boolean isGB(List<GenPolynomial<GenPolynomial<C>>> F) {  
        if ( F == null || F.size() == 0 ) {
            return true;
        }
        // extract coefficient factory
        GenPolynomial<GenPolynomial<C>> f = F.get(0);
        GenPolynomialRing<GenPolynomial<C>> fac = f.ring;
        RingFactory<GenPolynomial<C>> prfac = fac.coFac;
        GenPolynomialRing<C> pr = (GenPolynomialRing<C>) prfac;
        // setup condition ideal
        List<GenPolynomial<C>> fi = new ArrayList<GenPolynomial<C>>();
        Ideal<C> id = new Ideal<C>(pr,fi); 
        // setup list of empty colored system
        List<ColorPolynomial<C>> cp = new ArrayList<ColorPolynomial<C>>();
        Condition<C> ids = new Condition<C>(id);
        ColoredSystem<C> s = new ColoredSystem<C>(ids,cp);
        //System.out.println("s = " + s);
        List<ColoredSystem<C>> CSp = new ArrayList<ColoredSystem<C>>();
        CSp.add(s);
        //System.out.println("CSp = " + CSp);
        // determine polynomials
        //List<ColoredSystem<C>> CS = cred.determine(CSp,F);
        List<ColoredSystem<C>> CS = cred.determine(F); //new
        //System.out.println("CS = " + CS);
        // check if all S-polynomials reduce to zero
        ColorPolynomial<C> p, q, h;
        for ( ColoredSystem<C> cs : CS ) {
            if ( true || debug ) {
               if ( !cs.isDetermined() ) {
                  System.out.println("not determined, cs = " + cs);
                  return false;
               }
               if ( !cs.checkInvariant() ) {
                  System.out.println("not invariant, cs = " + cs);
                  return false;
               }
            }
            List<ColorPolynomial<C>> S = cs.list;
            int k = S.size();
            for ( int j = 0; j < k; j++ ) {
                p = S.get(j);
                for ( int l = j+1; l < k; l++ ) {
                    q = S.get(l);
                    h = cred.SPolynomial(p,q);
                    //System.out.println("spol(a,b) = " + h);
                    h = cred.normalform( S, h );
                    //System.out.println("NF(spol(a,b)) = " + h);
                    if ( true || debug ) {
                       if ( !cred.isNormalform( S, h ) ) {
                          System.out.println("not normalform, h = " + h);
                          return false;
                       }
                    }
                    if ( !h.isZERO() ) {
                       h = cs.reDetermine( h );
                       if ( !h.isZERO() ) {
                          System.out.println("p = " + p);
                          System.out.println("q = " + q);
                          System.out.println("NF(spol(p,q)) = " + h);
                          System.out.println("cs = " + cs);
                          return false;
                       }
                    }
                }
            }
        }
        return true;
    }


    /**
     * Comprehensive-Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a Comprehensive-Groebner base, else false.
     */
    //@Override
    public boolean isGBsys(List<ColoredSystem<C>> CS) {  
        if ( CS == null || CS.size() == 0 ) {
            return true;
        }
        ColorPolynomial<C> p, q, h;
        for ( ColoredSystem<C> cs : CS ) {
            if ( true || debug ) {
               if ( !cs.isDetermined() ) {
                  System.out.println("not determined, cs = " + cs);
                  return false;
               }
               if ( !cs.checkInvariant() ) {
                  System.out.println("not invariant, cs = " + cs);
                  return false;
               }
            }
            List<ColorPolynomial<C>> S = cs.list;
            int k = S.size();
            for ( int j = 0; j < k; j++ ) {
                p = S.get(j);
                for ( int l = j+1; l < k; l++ ) {
                    q = S.get(l);
                    h = cred.SPolynomial(p,q);
                    //System.out.println("spol(a,b) = " + h);
                    h = cred.normalform( S, h );
                    //System.out.println("NF(spol(a,b)) = " + h);
                    if ( true || debug ) {
                       if ( !cred.isNormalform( S, h ) ) {
                          System.out.println("not normalform, h = " + h);
                          System.out.println("cs = " + cs);
                          return false;
                       }
                    }
                    if ( !h.isZERO() ) {
                       h = cs.reDetermine( h );
                       if ( !h.isZERO() ) {
                          System.out.println("p = " + p);
                          System.out.println("q = " + q);
                          System.out.println("NF(spol(p,q)) = " + h);
                          System.out.println("cs = " + cs);
                          return false;
                       }
                    }
                }
            }
        }
        return true;
    }


    /**
     * Comprehensive Groebner base system using pairlist class.
     * @param F polynomial list.
     * @return GBsys(F) a Comprehensive Groebner base system of F.
     */
    //@Override
    //@SuppressWarnings("unchecked") 
    public List<ColoredSystem<C>> 
           GBsys( List<GenPolynomial<GenPolynomial<C>>> F ) {  
        if ( F == null ) {
           return null;
        }
        // extract coefficient factory
        GenPolynomial<GenPolynomial<C>> f = F.get(0);
        GenPolynomialRing<GenPolynomial<C>> fac = f.ring;
        RingFactory<GenPolynomial<C>> prfac = fac.coFac;
        GenPolynomialRing<C> pr = (GenPolynomialRing<C>) prfac;
        // setup condition ideal
        List<GenPolynomial<C>> fi = new ArrayList<GenPolynomial<C>>();
        Ideal<C> id = new Ideal<C>(pr,fi); 
        // setup list of empty colored system
        List<ColorPolynomial<C>> cp = new ArrayList<ColorPolynomial<C>>();
        Condition<C> ids = new Condition<C>(id);
        ColoredSystem<C> s = new ColoredSystem<C>(ids,cp);
        //System.out.println("s = " + s);
        List<ColoredSystem<C>> CSp = new ArrayList<ColoredSystem<C>>();
        CSp.add(s);
        //System.out.println("CSp = " + CSp);
        // determine polynomials
        //List<ColoredSystem<C>> CS = cred.determine(CSp,F);
        List<ColoredSystem<C>> CS = cred.determine(F); //new
        //System.out.println("CS = " + CS);
        //CS.remove(0); // empty colored system
        if ( logger.isInfoEnabled() ) {
           logger.info("determined polynomials =\n" + CS );
        }
        //System.out.println("CS = " + CS);

        // setup pair lists
        List<ColoredSystem<C>> CSs = new ArrayList<ColoredSystem<C>>();
        for ( ColoredSystem<C> cs : CS ) {
            OrderedCPairlist<C> pairlist = new OrderedCPairlist<C>( fac );
            for ( ColorPolynomial<C> p : cs.list ) {
                //System.out.println("p = " + p);
                pairlist.put( p );
            }
            ColoredSystem<C> css = new ColoredSystem<C>( cs.condition, cs.list, pairlist );
            CSs.add(css);
        }

        // main loop
        List<ColoredSystem<C>> CSb = new ArrayList<ColoredSystem<C>>();
        while ( CSs.size() > 0 ) {
            ColoredSystem<C> cs = CSs.remove(0);
            logger.info("poped GBsys = " + cs);
            OrderedCPairlist<C> pairlist = cs.pairlist;
            List<ColorPolynomial<C>> G = cs.list;
            //logger.info( pairlist.toString() );

            CPair<C> pair;
            ColorPolynomial<C> pi;
            ColorPolynomial<C> pj;
            ColorPolynomial<C> S;
            ColorPolynomial<C> H;
            while ( pairlist.hasNext() ) {
                pair = pairlist.removeNext();
                if ( pair == null ) continue; 

                pi = pair.pi; 
                pj = pair.pj; 
                if ( logger.isDebugEnabled() ) {
                    logger.debug("pi    = " + pi );
                    logger.debug("pj    = " + pj );
                }

                S = cred.SPolynomial( pi, pj );
                if ( S.isZERO() ) {
                    pair.setZero();
                    continue;
                }
                if ( logger.isDebugEnabled() ) {
                    logger.debug("ht(S) = " + S.leadingExpVector() );
                }

                H = cred.normalform( G, S );
                if ( H.isZERO() ) {
                    pair.setZero();
                    continue;
                }
                if ( true || logger.isDebugEnabled() ) {
                    logger.info("ht(H) = " + H.leadingExpVector() );
                }

                H = H.abs();
                //if ( H.isONE() ) {
                //    G.clear(); G.add( H );
                //    return G; // since no threads are activated
                //}
                if ( logger.isDebugEnabled() ) {
                    logger.debug("H = " + H );
                }
                logger.info("H = " + H );
                if ( ! H.isZERO() ) {
                    //GenPolynomial<GenPolynomial<C>> Hp =H.getPolynomial();
                    //GenPolynomial<C> cont = engine.recursiveContent( H.getPolynomial() );
                    //logger.info("cont(H) = " + cont );
                   //H = H.divide( cont );
                   //logger.info("pp(H) = " + H );
                   //if ( !H.divide(cont).multiply(cont).equals( H ) ) {
                    // logger.info("pp(H)cont(H) != H");
                    //}

                   List<ColoredSystem<C>> ncs = cred.determine( cs, H );
                   int len = ncs.size()-1;
                   int i = -1;
                   int gsi = G.size();
                   for ( ColoredSystem<C> cpp : ncs ) {
                       i++;
                       if ( cpp.list.size() <= gsi ) { // no new polynomial added
                          System.out.println("no new polynomial in " + cpp );
                          cs = new ColoredSystem<C>(cpp.condition,G,pairlist); //replace my cs with new conditions
                          continue;
                       }
                       ColorPolynomial<C> Hc = cpp.list.get( gsi ); // assert added is last
                       Hc = Hc.abs();
                       logger.info("Hc = " + Hc);
                       if ( Hc.isZERO() ) {
                          continue;
                       }
                       if ( i == len ) { // non cloneing must be last!
                          G = cpp.list;
                          pairlist.put( Hc ); // not cloned
                          cs = new ColoredSystem<C>(cpp.condition,G,pairlist); //replace my cs with new conditions
                       } else { // cloneing
                          OrderedCPairlist<C> plp = pairlist.clone();
                          plp.put( Hc );
                          ColoredSystem<C> cppp = new ColoredSystem<C>(cpp.condition,cpp.list,plp);
                          logger.info("pushed GBsys = " + cppp);
                          CSs.add( cppp );
                       }
                   }
                }
            }
            // all spols reduce to zero in this branch
            System.out.println("G = " + G );
            G = minimalGB(cs,G);
            System.out.println("min(G) = " + G );

            cs = new ColoredSystem<C>(cs.condition,G,pairlist);
            logger.info("#sequential done = " + cs.condition);
            logger.info( pairlist.toString() );
            CSb.add( cs );
            if ( false ) {
               break;
            }
        }
        return CSb;
    }


    /**
     * Comprehensive Groebner base using pairlist class.
     * @param F polynomial list.
     * @return GB(F) a Comprehensive Groebner base of F.
     */
    //@Override
    //@SuppressWarnings("unchecked") 
    public List<GenPolynomial<GenPolynomial<C>>> 
             GB( List<GenPolynomial<GenPolynomial<C>>> F ) {  
        if ( F == null ) {
           return F;
        }
        // compute Groebner system
        List<ColoredSystem<C>> Gsys = GBsys( F );
        //System.out.println("\n\nGBsys = " + Gsys);
        System.out.println("isGBsys() = " + isGBsys(Gsys) + "\n--------------------");

        // combine for CGB
        Set<GenPolynomial<GenPolynomial<C>>> Gs = 
            new HashSet<GenPolynomial<GenPolynomial<C>>>();
        ColorPolynomial<C> p, q, h;
        GenPolynomial<GenPolynomial<C>> f;
        for ( ColoredSystem<C> cs : Gsys ) {
            if ( true || debug ) {
               if ( !cs.isDetermined() ) {
                  System.out.println("not determined, cs = " + cs);
               }
               if ( !cs.checkInvariant() ) {
                  System.out.println("not invariant, cs = " + cs);
               }
            }
            List<ColorPolynomial<C>> S = cs.list;
            int k = S.size();
            for ( int j = 0; j < k; j++ ) {
                p = S.get(j);
                f = p.getPolynomial();
                Gs.add( f );
            }
        }
        List<GenPolynomial<GenPolynomial<C>>> G = 
            new ArrayList<GenPolynomial<GenPolynomial<C>>>( Gs );
        return G;
    }


    /**
     * Minimal ordered Groebner basis.
     * @param cs colored system.
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     */
    //@Override
    public List<ColorPolynomial<C>> minimalGB( ColoredSystem<C> cs,
                                               List<ColorPolynomial<C>> Gp ) {  
        if ( Gp == null || Gp.size() <= 1 ) {
            return Gp;
        }
        // remove zero polynomials
        List<ColorPolynomial<C>> G
            = new ArrayList<ColorPolynomial<C>>( Gp.size() );
        for ( ColorPolynomial<C> a : Gp ) { 
            if ( a != null && !a.isZERO() ) { // always true in GB()
               // already positive a = a.abs();
               G.add( a );
            }
        }
        if ( G.size() <= 1 ) {
           return G;
        }
        // remove top reducible polynomials
        ColorPolynomial<C> a;
        List<ColorPolynomial<C>> F;
        F = new ArrayList<ColorPolynomial<C>>( G.size() );
        while ( G.size() > 0 ) {
            a = G.remove(0);
            if ( cred.isTopReducible(G,a) || cred.isTopReducible(F,a) ) {
               // drop polynomial 
               if ( true || debug ) {
                  System.out.println("dropped " + a);
                  List<ColorPolynomial<C>> ff;
                  ff = new ArrayList<ColorPolynomial<C>>( G );
                  ff.addAll(F);
                  a = cred.normalform( ff, a );
                  a = cs.reDetermine( a );
                  if ( !a.isZERO() ) {
                     System.out.println("error, nf(a) " + a);
                  }
               }
            } else {
                F.add(a);
            }
        }
        G = F;
        if ( G.size() <= 1 ) {
           return G;
        }
        // reduce remaining polynomials
        int len = G.size();
        int i = 0;
        while ( i < len ) {
            a = G.remove(0);
            //System.out.println("doing " + a.length());
            a = cred.normalform( G, a );
            a = cs.reDetermine( a );
            //a = engine.basePrimitivePart(a); //a.monic(); was not required
            //a = a.abs();
            //a = red.normalform( F, a );
            G.add( a ); // adds as last
            i++;
        }
        return G;
    }


}
