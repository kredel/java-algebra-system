/*
 * $Id$
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.structure.RingElem;
import edu.jas.gb.OrderedPairlist;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.vector.BasicLinAlg;


/**
 * Groebner Base sequential algorithm.
 * Implements Groebner bases and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 *
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see edu.jas.gbufd.GBFactory
 */

public class GroebnerBaseSeq<C extends RingElem<C>> 
    extends GroebnerBaseAbstract<C>  {


    private static final Logger logger = LogManager.getLogger(GroebnerBaseSeq.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public GroebnerBaseSeq() {
        super();
    }


    /**
     * Constructor.
     * @param red Reduction engine
     */
    public GroebnerBaseSeq(Reduction<C> red) {
        super(red);
    }


    /**
     * Constructor.
     * @param pl pair selection strategy
     */
    public GroebnerBaseSeq(PairList<C> pl) {
        super(pl);
    }


    /**
     * Constructor.
     * @param red Reduction engine
     * @param pl pair selection strategy
     */
    public GroebnerBaseSeq(Reduction<C> red, PairList<C> pl) {
        super(red,pl);
    }


    /**
     * Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> GB( int modv, List<GenPolynomial<C>> F ) {  
        List<GenPolynomial<C>> G = normalizeZerosOnes(F);
        G = PolyUtil.<C> monic(G);
        if ( G.size() <= 1 ) {
            return G;
        }
        GenPolynomialRing<C> ring = G.get(0).ring;
        if ( ! ring.coFac.isField() ) {
            throw new IllegalArgumentException("coefficients not from a field");
        }
        PairList<C> pairlist = strategy.create( modv, ring ); 
        pairlist.put(G);
        logger.info("start {}", pairlist);

        Pair<C> pair;
        GenPolynomial<C> pi, pj, S, H;
        while ( pairlist.hasNext() ) {
            pair = pairlist.removeNext();
            //logger.debug("pair = {}", pair);
            if ( pair == null ) {
                continue; 
            }
            pi = pair.pi; 
            pj = pair.pj; 
            if ( /*false &&*/ debug ) {
                logger.debug("pi    = {}", pi );
                logger.debug("pj    = {}", pj );
            }

            S = red.SPolynomial( pi, pj );
            if ( S.isZERO() ) {
                pair.setZero();
                continue;
            }
            if ( debug ) {
                logger.debug("ht(S) = {}", S.leadingExpVector() );
            }

            H = red.normalform( G, S );
            if ( debug ) {
                //logger.info("pair = {}", pair);
                //logger.info("ht(S) = {}", S.monic()); //.leadingExpVector() );
                logger.info("ht(H) = {}", H.monic()); //.leadingExpVector() );
            }
            if ( H.isZERO() ) {
                pair.setZero();
                continue;
            }
            H = H.monic();
            if ( debug ) {
                logger.info("ht(H) = {}", H.leadingExpVector() );
            }

            H = H.monic();
            if ( H.isONE() ) {
                G.clear(); G.add( H );
                pairlist.putOne();
                logger.info("end {}", pairlist);
                return G; // since no threads are activated
            }
            if ( debug ) {
                logger.info("H = {}", H );
            }
            if ( H.length() > 0 ) {
                //l++;
                G.add( H );
                pairlist.put( H );
            }
        }
        logger.debug("#sequential list = {}", G.size());
        G = minimalGB(G);
        logger.info("end {}", pairlist);
        return G;
    }


    /**
     * Extended Groebner base using critical pair class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return a container for an extended Groebner base of F.
     */
    @Override
    public ExtendedGB<C> extGB(int modv, List<GenPolynomial<C>> F) {
        if ( F == null || F.isEmpty() ) {
            throw new IllegalArgumentException("null or empty F not allowed");
        }
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        List<List<GenPolynomial<C>>> F2G = new ArrayList<List<GenPolynomial<C>>>();
        List<List<GenPolynomial<C>>> G2F = new ArrayList<List<GenPolynomial<C>>>();
        PairList<C> pairlist = null; 
        boolean oneInGB = false;
        int len = F.size();

        List<GenPolynomial<C>> row = null;
        List<GenPolynomial<C>> rows = null;
        List<GenPolynomial<C>> rowh = null;
        GenPolynomialRing<C> ring = null;
        GenPolynomial<C> H;
        GenPolynomial<C> p;

        int nzlen = 0;
        for ( GenPolynomial<C> f : F ) { 
            if ( f.length() > 0 ) {
                nzlen++;
            }
            if ( ring == null ) {
                ring = f.ring;
            }
        }
        GenPolynomial<C> mone = ring.getONE(); //.negate();
        int k = 0;
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( p.length() > 0 ) {
                row = blas.genVector(nzlen, null);
                //C c = p.leadingBaseCoefficient();
                //c = c.inverse();
                //p = p.multiply( c );
                row.set( k, mone ); //.multiply(c) );
                k++;
                if ( p.isUnit() ) {
                    G.clear(); G.add( p );
                    G2F.clear(); G2F.add( row );
                    oneInGB = true;
                    break;
                }
                G.add( p );
                G2F.add( row );
                if ( pairlist == null ) {
                    //pairlist = new OrderedPairlist<C>( modv, p.ring );
                    pairlist = strategy.create( modv, p.ring );
                    if ( ! p.ring.coFac.isField() ) {
                        throw new RuntimeException("coefficients not from a field");
                    }
                }
                // putOne not required
                pairlist.put( p );
            } else { 
                len--;
            }
        }
        ExtendedGB<C> exgb;
        if ( len <= 1 || oneInGB ) {
            // adjust F2G
            for ( GenPolynomial<C> f : F ) {
                row = blas.genVector(G.size(), null);
                H = red.normalform( row, G, f );
                if ( ! H.isZERO() ) {
                    logger.error("nonzero H = {}", H );
                }
                F2G.add( row );
            }
            exgb = new ExtendedGB<C>(F,G,F2G,G2F);
            //System.out.println("exgb 1 = " + exgb);
            return exgb;
        }

        Pair<C> pair;
        int i, j;
        GenPolynomial<C> pi;
        GenPolynomial<C> pj;
        GenPolynomial<C> S;
        GenPolynomial<C> x;
        GenPolynomial<C> y;
        //GenPolynomial<C> z;
        while ( pairlist.hasNext() && ! oneInGB ) {
            pair = pairlist.removeNext();
            if ( pair == null ) { 
                continue; 
            }
            i = pair.i; 
            j = pair.j; 
            pi = pair.pi; 
            pj = pair.pj; 
            if ( debug ) {
                logger.info("i, pi    = {}, {}", i, pi );
                logger.info("j, pj    = {}, {}", j, pj );
            }

            rows = blas.genVector(G.size(), null);
            S = red.SPolynomial( rows, i, pi, j, pj );
            if ( debug ) {
                logger.debug("is reduction S = " 
                             + red.isReductionNF( rows, G, ring.getZERO(), S ) );
            }
            if ( S.isZERO() ) {
                // do not add to G2F
                continue;
            }
            if ( debug ) {
                logger.debug("ht(S) = {}", S.leadingExpVector() );
            }

            rowh = blas.genVector(G.size(), null);
            H = red.normalform( rowh, G, S );
            if ( debug ) {
                logger.debug("is reduction H = " 
                             + red.isReductionNF( rowh, G, S, H ) );
            }
            if ( H.isZERO() ) {
                // do not add to G2F
                continue;
            }
            if ( debug ) {
                logger.debug("ht(H) = {}", H.leadingExpVector() );
            }

            row = blas.vectorCombineOld(rows,rowh);
            // if ( debug ) {
            //     logger.debug("is reduction 0+sum(row,G) == H : "
            //                  + red.isReductionNF( row, G, H, ring.getZERO() ) );
            // }

            //  H = H.monic();
            C c = H.leadingBaseCoefficient();
            c = c.inverse();
            H = H.multiply( c );
            row = blas.scalarProduct( mone.multiply(c), row );
            row.set( G.size(), mone );
            if ( H.isONE() ) {
                // G.clear(); 
                G.add( H );
                G2F.add( row );
                oneInGB = true;
                break; 
            }
            if ( debug ) {
                logger.debug("H = {}", H );
            }
            G.add( H );
            pairlist.put( H );
            G2F.add( row );
        }
        if ( debug ) {
            exgb = new ExtendedGB<C>(F,G,F2G,G2F);
            logger.info("exgb unnorm = {}", exgb);
        }
        G2F = normalizeMatrix( F.size(), G2F );
        if ( debug ) {
            exgb = new ExtendedGB<C>(F,G,F2G,G2F);
            logger.info("exgb nonmin = {}", exgb);
            boolean t2 = isReductionMatrix( exgb );
            logger.info("exgb t2 = {}", t2);
        }
        exgb = minimalExtendedGB(F.size(),G,G2F);
        G = exgb.G;
        G2F = exgb.G2F;
        logger.debug("#sequential list = {}", G.size());
        logger.info("{}", pairlist);
        // setup matrices F and F2G
        for ( GenPolynomial<C> f : F ) {
            row = blas.genVector(G.size(), null);
            H = red.normalform( row, G, f );
            if ( ! H.isZERO() ) {
                logger.error("nonzero H = {}", H );
            }
            F2G.add( row );
        }
        exgb = new ExtendedGB<C>(F,G,F2G,G2F);
        if ( debug ) {
            logger.info("exgb nonmin = {}", exgb);
            boolean t2 = isReductionMatrix( exgb );
            logger.info("exgb t2 = {}", t2);
        }
        return exgb;
    }

}
