/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;

import edu.jas.ring.OrderedDPairlist;

/**
 * D-Groebner Base sequential algorithm.
 * Implements D-Groebner bases and GB test.
 * @author Heinz Kredel
 */

public class DGroebnerBaseSeq<C extends RingElem<C>> 
       extends GroebnerBaseAbstract<C>  {


    private static final Logger logger = Logger.getLogger(DGroebnerBaseSeq.class);


    /**
     * Reduction engine.
     */
    protected DReduction<C> red;  // shadow super.red


    /**
     * Constructor.
     */
    public DGroebnerBaseSeq() {
        this( new DReductionSeq<C>() );
    }


    /**
     * Constructor.
     * @param red D-Reduction engine
     */
    public DGroebnerBaseSeq(DReduction<C> red) {
        super(red);
        this.red = red;
    }


    /**
     * D-Groebner base test.
     * @typeparam C coefficient type.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a D-Groebner base, else false.
     */
    public boolean isGB(int modv, List<GenPolynomial<C>> F) {  
        GenPolynomial<C> pi, pj, s, h, d;
        for ( int i = 0; i < F.size(); i++ ) {
            pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                   continue;
                }
                d = red.GPolynomial( pi, pj );
                if ( ! d.isZERO() ) {
                   // better check for top reduction only
                   d = red.normalform( F, d );
                }
                if ( ! d.isZERO() ) {
                   System.out.println("d-pol("+i+","+j+") != 0: " + d);
                   return false;
                }
                // works ok
                //if ( ! red.criterion4( pi, pj ) ) { 
                //   continue;
                //}
                s = red.SPolynomial( pi, pj );
                if ( ! s.isZERO() ) {
                   s = red.normalform( F, s );
                }
                if ( ! s.isZERO() ) {
                   System.out.println("s-pol("+i+","+j+") != 0: " + s);
                   return false;
                }
            }
        }
        return true;
    }


    /**
     * D-Groebner base using pairlist class.
     * @typeparam C coefficient type.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a D-Groebner base of F.
     */
    public List<GenPolynomial<C>> 
             GB( int modv, 
                 List<GenPolynomial<C>> F ) {  
        //throw new RuntimeException("not jet implemented");
        GenPolynomial<C> p;
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        OrderedDPairlist<C> pairlist = null; 
        int l = F.size();
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( !p.isZERO() ) {
               p = p.abs(); // not monic
               if ( p.isONE() ) {
                  G.clear(); G.add( p );
                  return G; // since no threads are activated
               }
               G.add( p );
               if ( pairlist == null ) {
                  pairlist = new OrderedDPairlist<C>( modv, p.ring );
               }
               // putOne not required
               pairlist.put( p );
            } else { 
               l--;
            }
        }
        if ( l <= 1 ) {
           return G; // since no threads are activated
        }

        DPair<C> pair;
        GenPolynomial<C> pi;
        GenPolynomial<C> pj;
        GenPolynomial<C> S;
        GenPolynomial<C> D;
        GenPolynomial<C> H;
        int len = G.size();
        System.out.println("len = " + len);
        /*
        // D-polynomial case ----------------------
        for ( int i = 0; i < len; i++ ) {
            pi = G.get(i);
            for ( int j = i+1; j < len; j++ ) {
                pj = G.get(j);
                D = red.GPolynomial( pi, pj );
                //System.out.println("D_d = " + D);
                if ( ! D.isZERO() ) {
                   H = red.normalform( G, D );
                   System.out.println("D_d_i = " + D);
                   System.out.println("H_d_i = " + H);
                   if ( H.isONE() ) {
                      G.clear(); G.add( H );
                      return G; // since no threads are activated
                   }
                   if ( !H.isZERO() ) {
                      l++;
                      G.add( H );
                      pairlist.put( H );
                   }
                }
            }
        }
        */
        while ( pairlist.hasNext() ) {
              pair = pairlist.removeNext();
              //System.out.println("pair = " + pair);
              if ( pair == null ) continue; 

              pi = pair.pi; 
              pj = pair.pj; 
              if ( false && logger.isDebugEnabled() ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
              }

              // D-polynomial case ----------------------
              D = red.GPolynomial( pi, pj );
              //System.out.println("D_d = " + D);
              if ( ! D.isZERO() ) {
                  H = red.normalform( G, D );
                  if ( H.isONE() ) {
                      G.clear(); G.add( H );
                      return G; // since no threads are activated
                  }
                  if ( !H.isZERO() ) {
                      l++;
                      G.add( H );
                      pairlist.put( H );
                      System.out.println("Dred = " + H);
                  }
              }

              // S-polynomial case -----------------------
              if ( pair.getUseCriterion3() && pair.getUseCriterion4() ) {
                  S = red.SPolynomial( pi, pj );
                  //System.out.println("S_d = " + S);
                  if ( S.isZERO() ) {
                      pair.setZero();
                      continue;
                  }
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

                  if ( H.isONE() ) {
                      G.clear(); G.add( H );
                      return G; // since no threads are activated
                  }
                  if ( logger.isDebugEnabled() ) {
                      logger.debug("H = " + H );
                  }
                  if ( !H.isZERO() ) {
                      System.out.println("Sred = " + H);
                      len = G.size();
                      l++;
                      G.add( H );
                      pairlist.put( H );
                  }
              }
        }
        logger.debug("#sequential list = " + G.size());
        G = minimalGB(G);
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
        return G;
    }


    /**
     * Minimal ordered d-groebner basis.
     * @typeparam C coefficient type.
     * @param Gp a Groebner base.
     * @return a d-reduced Groebner base of Gp.
     */
    public List<GenPolynomial<C>> 
                minimalGB(List<GenPolynomial<C>> Gp) {  
        if ( Gp == null ) {
            return Gp;
        }
        GenPolynomial<C> a;
        List<GenPolynomial<C>> G;
        G = new ArrayList<GenPolynomial<C>>( Gp.size() );
        ListIterator<GenPolynomial<C>> it = Gp.listIterator();
        while ( it.hasNext() ) { 
            a = it.next();
            if ( a.length() != 0 ) { // always true
               // already positive a = a.abs();
               G.add( a );
            }
        }
        if ( G.size() <= 1 ) {
           return G;
        }
        ExpVector e;        
        ExpVector f;        
        GenPolynomial<C> p;
        C c, d;
        C r = null;
        List<GenPolynomial<C>> F;
        F = new ArrayList<GenPolynomial<C>>( G.size() );
        boolean mt;
        while ( G.size() > 0 ) {
            a = G.remove(0);
            e = a.leadingExpVector();
            c = a.leadingBaseCoefficient();

            it = G.listIterator();
            mt = false;
            while ( it.hasNext() && ! mt ) {
               p = it.next();
               f = p.leadingExpVector();
               mt = ExpVector.EVMT( e, f );
               if ( mt ) {
                  d = p.leadingBaseCoefficient();
                  r = c.remainder( d );
                  mt = r.isZERO(); // && mt
               }
            }
            it = F.listIterator();
            while ( it.hasNext() && ! mt ) {
               p = it.next();
               f = p.leadingExpVector();
               mt = ExpVector.EVMT( e, f );
               if ( mt ) {
                  d = p.leadingBaseCoefficient();
                  r = c.remainder( d );
                  mt = r.isZERO(); // && mt
               }
            }
            if ( ! mt ) {
                F.add( a );
            } else {
                System.out.println("dropped " + a);
                List<GenPolynomial<C>> ff;
                ff = new ArrayList<GenPolynomial<C>>( G );
                ff.addAll(F);
                a = red.normalform( ff, a );
                if ( !a.isZERO() ) {
                   System.out.println("error, nf(a) " + a);
                }
            }
        }
        G = F;
        if ( G.size() <= 1 ) {
           return G;
        }

        F = new ArrayList<GenPolynomial<C>>( G.size() );
        while ( G.size() > 0 ) {
            a = G.remove(0);
            //System.out.println("doing " + a.length());
            a = red.normalform( G, a );
            a = red.normalform( F, a );
            F.add( a );
        }
        return F;
    }

}
