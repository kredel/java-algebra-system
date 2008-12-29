
/*
 * $Id$
 */

package edu.jas.ufd;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import edu.jas.structure.Power;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;

import edu.jas.util.KsubSet;


/**
 * Abstract factorization algorithm class.
 * @author Heinz Kredel
 */

public abstract class FactorAbstract<C extends GcdRingElem<C> > 
                      implements Factorization<C> {


    /**
     * GenPolynomial test if is irreducible.
     * @param P GenPolynomial<C>.
     * @return true if P is irreducible, else false.
     */
    public boolean isIrreducible( GenPolynomial<C> P ) {
        if ( ! isSquarefree( P ) ) {
            return false;
        }
        List<GenPolynomial<C>> F = factorsSquarefree( P );
        if ( F.size() == 1 ) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * GenPolynomial test if a non trivial factorization exsists.
     * @param P GenPolynomial<C>.
     * @return true if P is reducible, else false.
     */
    public boolean isReducible( GenPolynomial<C> P ) {
        return ! isIrreducible( P );
    }


    /**
     * GenPolynomial test if is squarefree.
     * @param P GenPolynomial<C>.
     * @return true if P is squarefree, else false.
     */
    public boolean isSquarefree( GenPolynomial<C> P ) {
        GenPolynomial<C> S = squarefreePart( P );
        return P.equals( S );
    }


    /**
     * GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial<C>.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    public List<GenPolynomial<C>> factorsSquarefree( GenPolynomial<C> P ) {
        if ( P == null ) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar == 1 ) {
            return baseFactorsSquarefree( P );
        }
        List<GenPolynomial<C>> factors = new ArrayList<GenPolynomial<C>>();
        if ( P.isZERO() ) {
            return factors;
        }
        long d = P.degree() + 1L;
        GenPolynomial<C> kr = PolyUfdUtil.<C> substituteKronecker( P, d ); 
        System.out.println("subs(P,d=" + d + ") = " + kr);

        List<GenPolynomial<C>> klist;
        // check if kr is also squarefree and factor univariate
        if ( isSquarefree( kr ) ) {
             klist = baseFactorsSquarefree( kr );
        } else {
             System.out.println("kr not squarefree ");
             SortedMap<GenPolynomial<C>,Integer> slist = baseFactors( kr );
             System.out.println("slist = " + slist);
             if ( !isFactorization( kr, slist ) ) {
                throw new RuntimeException("no factorization");
             }
             klist = new ArrayList<GenPolynomial<C>>( slist.keySet() );
        }
//         if ( klist.size() == 1 ) {
//             factors.add( P );
//             return factors;
//         }
        System.out.println("klist = " + klist);
        klist = PolyUfdUtil.<C>backSubstituteKronecker(pfac,klist,d);
        System.out.println("back(klist) = " + klist);

        // combine trial factors
        int dl = (klist.size()+1)/2;
        int ti = 0;
        GenPolynomial<C> u = P;
        for ( int j = 1; j <= dl; j++ ) {
            KsubSet<GenPolynomial<C>> ps = new KsubSet<GenPolynomial<C>>( klist, j );
            for ( List<GenPolynomial<C>> flist : ps ) {
                //System.out.println("flist = " + flist);
                GenPolynomial<C> trial = pfac.getONE();
                for ( int k = 0; k < flist.size(); k++ ) {
                    trial = trial.multiply( flist.get(k) );
                }
                ti++;
                if ( ti % 10 == 0 ) {
                   System.out.println("ti = " + ti);
                }
                //GenPolynomial<C> trial = PolyUfdUtil.<C> backSubstituteKronecker( pfac, utrial, d ); 
                if ( u.remainder(trial).isZERO() ) {
                    System.out.println("trial = " + trial);
                    factors.add( trial );
                    u = u.divide( trial );
                    if ( klist.removeAll( flist ) ) {
                        System.out.println("new klist = " + klist);
                        dl = (klist.size()+1)/2;
                        j = 1;
                        if ( klist.size() > 0 ) {
                           ps = new KsubSet<GenPolynomial<C>>( klist, j );
                        }
                        break;
                    } else {
                       System.out.println("error removing flist from klist = " + klist);
                    }
                }
            }
        }
        if ( !u.isONE() && !u.equals(P) ) {
            System.out.println("rest u = " + u);
            factors.add( u );
        }
        if ( factors.size() == 0 ) {
            System.out.println("irred u = " + u);
            factors.add( P );
        }
        return factors;
    }


    /**
     * Univariate GenPolynomial factorization.
     * @param P GenPolynomial<C> in one variable.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i**e_i.
     */
    public SortedMap<GenPolynomial<C>,Integer> baseFactors( GenPolynomial<C> P ) {
        if ( P == null ) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        SortedMap<GenPolynomial<C>,Integer> factors
           = new TreeMap<GenPolynomial<C>,Integer>( pfac.getComparator() );
        if ( P.isZERO() ) {
            return factors;
        }
        if ( pfac.nvar > 1 ) {
            throw new RuntimeException(this.getClass().getName()
                    + " only for univariate polynomials");
        }
        GreatestCommonDivisorAbstract<C> engine 
         = (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getImplementation( pfac.coFac );
        C c = engine.baseContent(P);
        if ( ! c.isONE() ) {
           GenPolynomial<C> pc = pfac.getONE().multiply( c );
           factors.put( pc, 1 );
           P = P.divide(c); // make primitive
        }
        SortedMap<Integer,GenPolynomial<C>> facs = engine.baseSquarefreeFactors(P);
        System.out.println("facs    = " + facs);
        for ( Integer d : facs.keySet() ) {
            GenPolynomial<C> g = facs.get( d );
            List<GenPolynomial<C>> sfacs = baseFactorsSquarefree(g);
            //System.out.println("sfacs   = " + sfacs);
            for ( GenPolynomial<C> h : sfacs ) {
                factors.put( h, d );
            }
        }
        System.out.println("factors = " + factors);
        return factors;
    }


    /**
     * Univariate GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial<C> in one variable.
     * @return [p_1, ..., p_k] with P = prod_{i=1,...,k} p_i.
     */
    public abstract List<GenPolynomial<C>> baseFactorsSquarefree( GenPolynomial<C> P );


    /**
     * GenPolynomial factorization.
     * @param P GenPolynomial<C>.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i**e_i.
     */
    public SortedMap<GenPolynomial<C>,Integer> factors( GenPolynomial<C> P ) {
        if ( P == null ) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar == 1 ) {
            return baseFactors( P );
        }
        SortedMap<GenPolynomial<C>,Integer> factors 
           = new TreeMap<GenPolynomial<C>,Integer>( pfac.getComparator() );
        if ( P.isZERO() ) {
            return factors;
        }
        GreatestCommonDivisorAbstract<C> engine 
           = (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getImplementation( pfac.coFac );
        C c = engine.baseContent(P); // is this meaningful?
        if ( ! c.isONE() ) {
           System.out.println("baseContent = " + c);
           GenPolynomial<C> pc = pfac.getONE().multiply( c );
           factors.put( pc, 1 );
           P = P.divide(c); // make base primitive
        }
        SortedMap<Integer,GenPolynomial<C>> facs = engine.squarefreeFactors(P);
        System.out.println("facs    = " + facs);
        for ( Integer d : facs.keySet() ) {
            GenPolynomial<C> g = facs.get( d );
            List<GenPolynomial<C>> sfacs = factorsSquarefree( g );
            //System.out.println("sfacs   = " + sfacs);
            for ( GenPolynomial<C> h : sfacs ) {
                factors.put( h, d );
            }
        }
        System.out.println("factors = " + factors);
        return factors;
    }


    /**
     * GenPolynomial greatest squarefree divisor.
     * @param P GenPolynomial.
     * @return squarefree(P).
     */
    public GenPolynomial<C> squarefreePart( GenPolynomial<C> P ) {
        if ( P == null ) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        //GenPolynomialRing<C> pfac = P.ring;
        RingFactory<C> cfac = P.ring.coFac;
        GreatestCommonDivisor<C> engine = GCDFactory.<C> getProxy( cfac );
        return engine.squarefreePart(P);
    }


    /**
     * GenPolynomial squarefree factorization.
     * @param P GenPolynomial.
     * @return [e_1 -> p_1, ..., e_k -> p_k] with P = prod_{i=1,...,k} p_i**e_i. 
     */
    public Map<Integer,GenPolynomial<C>> squarefreeFactors( GenPolynomial<C> P ) {
        if ( P == null ) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        //GenPolynomialRing<C> pfac = P.ring;
        RingFactory<C> cfac = P.ring.coFac;
        GreatestCommonDivisor<C> engine = GCDFactory.<C> getProxy( cfac );
        return engine.squarefreeFactors(P);
    }


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial<C>.
     * @param F = [p_1,...,p_k].
     * @return true if P = prod_{i=1,...,r} p_i, else false.
     */
    public boolean isFactorization( GenPolynomial<C> P, List<GenPolynomial<C>> F ) {
        if ( P == null || F == null ) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        GenPolynomial<C> t = P.ring.getONE();
        for ( GenPolynomial<C> f: F ) {
            t = t.multiply( f );
        }
        return P.equals(t);
    }


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial<C>.
     * @param F = [p_1 -> e_1, ..., p_k -> e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i , else false.
     */
    public boolean isFactorization( GenPolynomial<C> P, SortedMap<GenPolynomial<C>,Integer> F ) {
        if ( P == null || F == null ) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        if ( P.isZERO() && F.size() == 0 ) {
            return true;
        }
        GenPolynomial<C> t = P.ring.getONE();
        for ( GenPolynomial<C> f: F.keySet() ) {
            Integer E = F.get(f);
            if ( E != null ) {
                int e = E.intValue();
                GenPolynomial<C> g = Power.<GenPolynomial<C>> positivePower( f, e );
                t = t.multiply( g );
            } else {
                //t = t.multiply( f );
                Set<GenPolynomial<C>> ks = F.keySet();
                System.out.println("\n\nE == null: " + f);
                System.out.println("F.keySet(): " + ks);
                System.out.println("F.values(): " + F.values());
                System.out.println("F.compar(): " + F.comparator());
                System.out.println("f in ks:    " + ks.contains(f));
                throw new RuntimeException("wrong TreeMap entries");
            }
        }
        return P.equals(t);
    }

}
