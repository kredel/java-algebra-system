/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.arith.Coefficient;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.SolvablePolynomial;

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

    public static boolean isLeftGB(List F) {  
        SolvablePolynomial pi, pj, s, h;
	for ( int i = 0; i < F.size(); i++ ) {
	    pi = (SolvablePolynomial) F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = (SolvablePolynomial) F.get(j);
		// if ( ! Reduction.GBCriterion4( pi, pj ) ) continue;
		s = Reduction.leftSPolynomial( pi, pj );
		if ( s.isZERO() ) continue;
		h = Reduction.leftNormalform( F, s );
		if ( ! h.isZERO() ) return false;
	    }
	}
	return true;
    }


    /**
     * Twosided Groebner base test
     */

    public static boolean isTwosidedGB(List Fp) {  
        if ( Fp == null || Fp.size() <= 1 ) {
            return true;
        }
        List X = generateUnivar( Fp );
        List F = new ArrayList( Fp.size() * (1+X.size()) );
        F.addAll( Fp );
        SolvablePolynomial p, x, pi, pj, s, h;
	for ( int i = 0; i < Fp.size(); i++ ) {
	    p = (SolvablePolynomial) Fp.get(i);
            for ( int j = 0; j < X.size(); j++ ) {
                x = (SolvablePolynomial) X.get(j);
                p = (SolvablePolynomial) p.multiply( x );
                F.add( p );
            }
        }
	for ( int i = 0; i < F.size(); i++ ) {
	    pi = (SolvablePolynomial) F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = (SolvablePolynomial) F.get(j);
		// if ( ! Reduction.GBCriterion4( pi, pj ) ) continue;
		s = Reduction.leftSPolynomial( pi, pj );
		if ( s.isZERO() ) continue;
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
    protected static List generateUnivar(List F) {
        OrderedPolynomial p = (OrderedPolynomial)F.get(0);
        OrderedPolynomial zero = p.getZERO( p.getTermOrder() );
        Coefficient one = p.leadingBaseCoefficient().fromInteger(1l);
        int r = p.numberOfVariables();
        ExpVector e;
        List pols = new ArrayList(r);
        for ( int i = 0; i < r; i++ ) {
            e = new ExpVector(r,i,1);
            p = zero.add(one,e);
            pols.add( p );
        }
        return pols;
    }


    /**
     * Left Groebner base using pairlist class.
     */

    public static ArrayList leftGB(List F) {  
        SolvablePolynomial p;
        ArrayList G = new ArrayList();
        OrderedPairlist pairlist = null; 
        int l = F.size();
        ListIterator it = F.listIterator();
        while ( it.hasNext() ) { 
            p = (SolvablePolynomial) it.next();
            if ( p.length() > 0 ) {
               p = (SolvablePolynomial) p.monic();
               if ( p.isONE() ) {
		  G.clear(); G.add( p );
                  return G; // since no threads are activated
	       }
               G.add( p );
	       if ( pairlist == null ) {
                  pairlist = new OrderedPairlist( p.getTermOrder() );
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

        Pair pair;
        SolvablePolynomial pi;
        SolvablePolynomial pj;
        SolvablePolynomial S;
        SolvablePolynomial H;
        while ( pairlist.hasNext() ) {
              pair = (Pair) pairlist.removeNext();
              if ( pair == null ) continue; 

              pi = (SolvablePolynomial)pair.pi; 
              pj = (SolvablePolynomial)pair.pj; 
              if ( false && logger.isDebugEnabled() ) {
                 logger.info("pi    = " + pi );
                 logger.info("pj    = " + pj );
	      }

              S = Reduction.leftSPolynomial( pi, pj );
              if ( S.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( false &&  logger.isDebugEnabled() ) {
                 logger.info("ht(S) = " + S.leadingExpVector() );
	      }

              H = Reduction.leftNormalform( G, S );
              if ( H.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( false && logger.isDebugEnabled() ) {
                 logger.info("ht(H) = " + H.leadingExpVector() );
	      }

	      H = (SolvablePolynomial) H.monic();
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

    public static ArrayList leftGBMI(List Gp) {  
        SolvablePolynomial a;
        ArrayList G = new ArrayList();
        ListIterator it = Gp.listIterator();
        while ( it.hasNext() ) { 
            a = (SolvablePolynomial) it.next();
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
        SolvablePolynomial p;
        ArrayList F = new ArrayList();
	boolean mt;

        while ( G.size() > 0 ) {
            a = (SolvablePolynomial) G.remove(0);
	    e = a.leadingExpVector();

            it = G.listIterator();
	    mt = false;
	    while ( it.hasNext() && ! mt ) {
               p = (SolvablePolynomial) it.next();
               f = p.leadingExpVector();
	       mt = ExpVector.EVMT( e, f );
	    }
            it = F.listIterator();
	    while ( it.hasNext() && ! mt ) {
               p = (SolvablePolynomial) it.next();
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

        F = new ArrayList();
        while ( G.size() > 0 ) {
            a = (SolvablePolynomial) G.remove(0);
	    // System.out.println("doing " + a.length());
            a = Reduction.leftNormalform( G, a );
            a = Reduction.leftNormalform( F, a );
            F.add( a );
	}
	return F;
    }



    /**
     * Twosided Groebner base using pairlist class.
     */

    public static ArrayList twosidedGB(List Fp) {  
        if ( Fp == null || Fp.size() <= 0 ) { // 0 not 1
            return new ArrayList( Fp );
        }
        List X = generateUnivar( Fp );
        List F = new ArrayList( Fp.size() * (1+X.size()) );
        F.addAll( Fp );
        SolvablePolynomial p, x;
	for ( int i = 0; i < Fp.size(); i++ ) {
	    p = (SolvablePolynomial) Fp.get(i);
            for ( int j = 0; j < X.size(); j++ ) {
                x = (SolvablePolynomial) X.get(j);
                p = (SolvablePolynomial) p.multiply( x );
                p = Reduction.leftNormalform( F, p );
                if ( !p.isZERO() ) {
                   F.add( p );
                }
            }
        }
        ArrayList G = new ArrayList();
        OrderedPairlist pairlist = null; 
        int l = F.size();
        ListIterator it = F.listIterator();
        while ( it.hasNext() ) { 
            p = (SolvablePolynomial) it.next();
            if ( p.length() > 0 ) {
               p = (SolvablePolynomial) p.monic();
               if ( p.isONE() ) {
		  G.clear(); G.add( p );
                  return G; // since no threads are activated
	       }
               G.add( p );
	       if ( pairlist == null ) {
                  pairlist = new OrderedPairlist( p.getTermOrder() );
               }
               // putOne not required
               pairlist.put( p );
	    } else { 
               l--;
            }
	}
        if ( l <= 1 ) { // 1 ok
           return G; // since no threads are activated
        }

        Pair pair;
        SolvablePolynomial pi;
        SolvablePolynomial pj;
        SolvablePolynomial S;
        SolvablePolynomial H;
        while ( pairlist.hasNext() ) {
              pair = (Pair) pairlist.removeNext();
              if ( pair == null ) continue; 

              pi = (SolvablePolynomial)pair.pi; 
              pj = (SolvablePolynomial)pair.pj; 
              if ( false && logger.isDebugEnabled() ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
	      }

              S = Reduction.leftSPolynomial( pi, pj );
              if ( S.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
	      }

              H = Reduction.leftNormalform( G, S );
              if ( H.isZERO() ) {
                 pair.setZero();
                 continue;
              }
              if ( logger.isDebugEnabled() ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
	      }

	      H = (SolvablePolynomial) H.monic();
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
                     x = (SolvablePolynomial) X.get(j);
                     p = (SolvablePolynomial) H.multiply( x );
                     p = Reduction.leftNormalform( G, p );
                     if ( !p.isZERO() ) {
                        p = (SolvablePolynomial)p.monic();
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
