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
import edu.jas.poly.GenSolvablePolynomial;

import edu.jas.ring.OrderedPairlist;
import edu.jas.ring.Reduction;


/**
 * Solvable Groebner Bases class.
 * Implements left Groebner bases and left GB test.
 * @author Heinz Kredel
 */

public class SolvableGroebnerBase  {

    private static final Logger logger = Logger.getLogger(SolvableGroebnerBase.class);

    /**
     * Left Groebner base test
     */

    public static <C extends RingElem<C>>
           boolean isLeftGB(List<GenSolvablePolynomial<C>> F) {  
        return isLeftGB(0,F);
    }

    public static <C extends RingElem<C>>
           boolean isLeftGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        GenSolvablePolynomial<C> pi, pj, s, h;
	for ( int i = 0; i < F.size(); i++ ) {
	    pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
		if ( ! Reduction.<C>ModuleCriterion( modv, pi, pj ) ) {
                   continue;
                }
		// if ( ! Reduction.<C>GBCriterion4( pi, pj ) ) { continue; }
		s = Reduction.<C>leftSPolynomial( pi, pj );
		if ( s.isZERO() ) {
                   continue;
                }
		h = Reduction.<C>leftNormalform( F, s );
		if ( ! h.isZERO() ) {
                   return false;
                }
	    }
	}
	return true;
    }


    /**
     * Twosided Groebner base test
     */

    public static <C extends RingElem<C>>
           boolean isTwosidedGB(List<GenSolvablePolynomial<C>> Fp) {  
        return isTwosidedGB(0,Fp);
    }

    public static <C extends RingElem<C>>
           boolean isTwosidedGB(int modv, List<GenSolvablePolynomial<C>> Fp) {  
        if ( Fp == null || Fp.size() == 0 ) { // 0 not 1
            return true;
        }
        List<GenSolvablePolynomial<C>> X = generateUnivar( modv, Fp );
        List<GenSolvablePolynomial<C>> F 
            = new ArrayList<GenSolvablePolynomial<C>>( Fp.size() * (1+X.size()) );
        F.addAll( Fp );
        GenSolvablePolynomial<C> p, x, pi, pj, s, h;
	for ( int i = 0; i < Fp.size(); i++ ) {
	    p = Fp.get(i);
            for ( int j = 0; j < X.size(); j++ ) {
                x = X.get(j);
                p = p.multiply( x );
                F.add( p );
            }
        }
        //System.out.println("F to check = " + F);
	for ( int i = 0; i < F.size(); i++ ) {
	    pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
		if ( ! Reduction.<C>ModuleCriterion( modv, pi, pj ) ) {
                   continue;
                }
		// if ( ! Reduction.<C>GBCriterion4( pi, pj ) ) { continue; }
		s = Reduction.<C>leftSPolynomial( pi, pj );
		if ( s.isZERO() ) {
                   continue;
                }
		h = Reduction.leftNormalform( F, s );
		if ( ! h.isZERO() ) {
                   logger.info("is not TwosidedGB: " + h);
                   return false;
                }
	    }
	}
	return true;
    }


    /**
     * Generate solvable polynomials in each variable.
     */
    protected static <C extends RingElem<C>> 
              List<GenSolvablePolynomial<C>> 
              generateUnivar(List<GenSolvablePolynomial<C>> F) {
        return generateUnivar(0,F);
    }

    protected static <C extends RingElem<C>>
              List<GenSolvablePolynomial<C>> 
              generateUnivar(int modv, List<GenSolvablePolynomial<C>> F) {
        GenSolvablePolynomial<C> p = F.get(0);
        GenSolvablePolynomial<C> zero = p.ring.getZERO();
        C one = p.ring.coFac.getONE();
        int r = p.numberOfVariables()-modv;
        ExpVector e;
        List<GenSolvablePolynomial<C>> pols 
            = new ArrayList<GenSolvablePolynomial<C>>(r);
        for ( int i = 0; i < r; i++ ) {
            e = new ExpVector(r,i,1);
            if ( modv > 0 ) {
                e = e.extend(modv,0,0l);
            }
            p = (GenSolvablePolynomial<C>)zero.add(one,e);
            pols.add( p );
        }
        return pols;
    }


    /**
     * Left Groebner base using pairlist class.
     */

    public static <C extends RingElem<C>>
           ArrayList<GenSolvablePolynomial<C>> 
           leftGB(List F) {  
        return leftGB(0,F);
    }

    public static <C extends RingElem<C>>
           ArrayList<GenSolvablePolynomial<C>> 
           leftGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        ArrayList<GenSolvablePolynomial<C>> G 
           = new ArrayList<GenSolvablePolynomial<C>>();
        OrderedPairlist pairlist = null; 
        int l = F.size();
        //  ListIterator it = F.listIterator();
        for ( GenSolvablePolynomial<C> p: F ) { 
            //  p = (SolvablePolynomial) it.next();
            if ( p.length() > 0 ) {
               p = (GenSolvablePolynomial<C>)p.monic();
               if ( p.isONE() ) {
		  G.clear(); G.add( p );
                  return G; // since no threads are activated
	       }
               G.add( p );
	       if ( pairlist == null ) {
                   pairlist = new OrderedPairlist( modv, p.ring );
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

        GenSolvablePolynomial<C> pi, pj, S, H;
        Pair<C> pair;
        while ( pairlist.hasNext() ) {
              pair = (Pair<C>) pairlist.removeNext();
              if ( pair == null ) {
                 continue; 
              }
              pi = (GenSolvablePolynomial<C>)pair.pi; 
              pj = (GenSolvablePolynomial<C>)pair.pj; 
              if ( false && logger.isDebugEnabled() ) {
                 logger.info("pi    = " + pi );
                 logger.info("pj    = " + pj );
	      }

              S = Reduction.<C>leftSPolynomial( pi, pj );
              if ( S.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( false &&  logger.isDebugEnabled() ) {
                 logger.info("ht(S) = " + S.leadingExpVector() );
	      }

              H = Reduction.<C>leftNormalform( G, S );
              if ( H.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( false && logger.isDebugEnabled() ) {
                 logger.info("ht(H) = " + H.leadingExpVector() );
	      }

	      H = (GenSolvablePolynomial<C>)H.monic();
	      if ( H.isONE() ) {
		  G.clear(); G.add( H );
                  return G; // since no threads are activated
	      }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("H = " + H );
	      }
              if ( H.length() > 0 ) {
		 l++;
                 G.add( H );
                 pairlist.put( H );
              }
	}
        logger.debug("#sequential list = "+G.size());
	G = leftGBMI(G);
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
	return G;
    }


    /**
     * Left minimal ordered groebner basis.
     */

    public static <C extends RingElem<C>>
           ArrayList<GenSolvablePolynomial<C>> 
           leftGBMI(List<GenSolvablePolynomial<C>> Gp) {  
        ArrayList<GenSolvablePolynomial<C>> G 
           = new ArrayList<GenSolvablePolynomial<C>>();
        ListIterator<GenSolvablePolynomial<C>> it = Gp.listIterator();
        for ( GenSolvablePolynomial<C> a: Gp ) { 
            // a = (SolvablePolynomial) it.next();
            if ( a.length() != 0 ) { // always true
	       // already monic a = a.monic();
               G.add( a );
	    }
	}
        if ( G.size() <= 1 ) {
           return G;
        }

        ExpVector e;        
        ExpVector f;        
        GenSolvablePolynomial<C> a, p;
        ArrayList<GenSolvablePolynomial<C>> F 
           = new ArrayList<GenSolvablePolynomial<C>>();
	boolean mt;

        while ( G.size() > 0 ) {
            a = G.remove(0);
	    e = a.leadingExpVector();

            it = G.listIterator();
	    mt = false;
	    while ( it.hasNext() && ! mt ) {
               p = it.next();
               f = p.leadingExpVector();
	       mt = ExpVector.EVMT( e, f );
	    }
            it = F.listIterator();
	    while ( it.hasNext() && ! mt ) {
               p = it.next();
               f = p.leadingExpVector();
	       mt = ExpVector.EVMT( e, f );
	    }
	    if ( ! mt ) {
		F.add( a );
	    } else {
		// System.out.println("dropped " + a.length());
	    }
	}
	G = F;
        if ( G.size() <= 1 ) {
           return G;
        }

        F = new ArrayList<GenSolvablePolynomial<C>>();
        while ( G.size() > 0 ) {
            a = G.remove(0);
	    // System.out.println("doing " + a.length());
            a = Reduction.<C>leftNormalform( G, a );
            a = Reduction.<C>leftNormalform( F, a );
            F.add( a );
	}
	return F;
    }



    /**
     * Twosided Groebner base using pairlist class.
     */

    public static <C extends RingElem<C>>
           ArrayList<GenSolvablePolynomial<C>> 
           twosidedGB(List<GenSolvablePolynomial<C>> Fp) {  
        return twosidedGB(0,Fp);
    }

    public static <C extends RingElem<C>>
           ArrayList<GenSolvablePolynomial<C>> 
           twosidedGB(int modv, List<GenSolvablePolynomial<C>> Fp) {  
        if ( Fp == null || Fp.size() == 0 ) { // 0 not 1
            return new ArrayList<GenSolvablePolynomial<C>>( Fp );
        }
        List<GenSolvablePolynomial<C>> X = generateUnivar( modv, Fp );
        //System.out.println("X univ = " + X);
        List<GenSolvablePolynomial<C>> F 
            = new ArrayList<GenSolvablePolynomial<C>>( Fp.size() * (1+X.size()) );
        F.addAll( Fp );
        GenSolvablePolynomial<C> p, x, q;
	for ( int i = 0; i < Fp.size(); i++ ) {
	    p = Fp.get(i);
            for ( int j = 0; j < X.size(); j++ ) {
                x = X.get(j);
                q = p.multiply( x );
                q = Reduction.<C>leftNormalform( F, q );
                if ( !q.isZERO() ) {
                   F.add( q );
                }
            }
        }
        //System.out.println("F generated = " + F);
        ArrayList<GenSolvablePolynomial<C>> G 
            = new ArrayList<GenSolvablePolynomial<C>>();
        OrderedPairlist pairlist = null; 
        int l = F.size();
        ListIterator<GenSolvablePolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( p.length() > 0 ) {
               p = (GenSolvablePolynomial<C>)p.monic();
               if ( p.isONE() ) {
		  G.clear(); G.add( p );
                  return G; // since no threads are activated
	       }
               G.add( p );
	       if ( pairlist == null ) {
                  pairlist = new OrderedPairlist( modv, p.ring );
               }
               // putOne not required
               pairlist.put( p );
	    } else { 
               l--;
            }
	}
        //System.out.println("G to check = " + G);
        if ( l <= 1 ) { // 1 ok
           return G; // since no threads are activated
        }

        Pair pair;
        GenSolvablePolynomial<C> pi, pj, S, H;
        while ( pairlist.hasNext() ) {
              pair = (Pair) pairlist.removeNext();
              if ( pair == null ) {
                 continue; 
              }

              pi = (GenSolvablePolynomial<C>)pair.pi; 
              pj = (GenSolvablePolynomial<C>)pair.pj; 
              if ( false && logger.isDebugEnabled() ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
	      }

              S = Reduction.<C>leftSPolynomial( pi, pj );
              if ( S.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
	      }

              H = Reduction.<C>leftNormalform( G, S );
              if ( H.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
	      }

	      H = (GenSolvablePolynomial<C>)H.monic();
	      if ( H.isONE() ) {
		  G.clear(); G.add( H );
                  return G; // since no threads are activated
	      }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("H = " + H );
	      }
              if ( H.length() > 0 ) {
		 l++;
                 G.add( H );
                 pairlist.put( H );
                 for ( int j = 0; j < X.size(); j++ ) {
                     l++;
                     x = X.get(j);
                     p = H.multiply( x );
                     p = Reduction.<C>leftNormalform( G, p );
                     if ( !p.isZERO() ) {
                        p = (GenSolvablePolynomial<C>)p.monic();
	                if ( p.isONE() ) {
		           G.clear(); G.add( p );
                           return G; // since no threads are activated
	                }
                        G.add( p );
                        pairlist.put( p );
                     }
                 }
              }
	}
        logger.debug("#sequential list = "+G.size());
	G = leftGBMI(G);
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
	return G;
    }

}
