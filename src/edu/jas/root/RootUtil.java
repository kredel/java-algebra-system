/*
 * $Id$
 */

package edu.jas.root;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;

import edu.jas.util.ListUtil;


/**
 * Real root utilities. 
 * For example real root count.
 * @author Heinz Kredel
 */
public class RootUtil {


    private static final Logger logger = Logger.getLogger(RootUtil.class);
    private static boolean debug = logger.isDebugEnabled();


    /**
     * Count changes in sign. 
     * @param <C> coefficient type.
     * @param L list of coefficients.
     * @return number of sign changes in L.
     */
    public static <C extends RingElem<C>> long signVar( List<C> L ) {
        long v = 0;
        if ( L == null || L.isEmpty() ) {
            return v;
        }
        C A = L.get(0);
        for ( int i = 1; i < L.size(); i++ ) {
            C B = L.get(i);
            while ( B == null || B.signum() == 0 ) {
                i++;
                if ( i >= L.size() ) {
                    return v;
                }
                B = L.get(i);
            }
            if ( A.signum() * B.signum() < 0 ) {
                v++;
            }
            A = B;
        }
        return v;
    }


    /**
     * Sturm sequence. 
     * @param <C> coefficient type.
     * @param f univariate polynomial.
     * @return a Sturm sequence for f.
     */
    public static <C extends RingElem<C>> 
      List<GenPolynomial<C>> sturmSequence( GenPolynomial<C> f ) {
        List<GenPolynomial<C>> S = new ArrayList<GenPolynomial<C>>();
        if ( f == null || f.isZERO() ) {
            return S;
        }
        if ( f.isConstant() ) {
            S.add( f.monic() );
            return S;
        }
        GenPolynomial<C> F = f;
        S.add(F);
        GenPolynomial<C> G = PolyUtil.<C> baseDeriviative(f);
        while ( ! G.isZERO() ) {
            GenPolynomial<C> r = F.remainder(G);
            F = G;
            G = r.negate();
            S.add( F/*.monic()*/ );
        }
        //System.out.println("F = " + F);
        if ( F.isConstant() ) {
            return S;
        }
        // make squarefree
        List<GenPolynomial<C>> Sp = new ArrayList<GenPolynomial<C>>( S.size() );
        for ( GenPolynomial<C> p : S ) {
            p = p.divide(F);
            Sp.add(p);
        }
        return Sp;
    }


    /**
     * Real root bound. 
     * With f(M) * f(-M) != 0.
     * @param <C> coefficient type.
     * @param f univariate polynomial.
     * @return M such that -M &lt; root(f) &gt; M.
     */
    public static <C extends RingElem<C>> C realRootBound(GenPolynomial<C> f) {
        if ( f == null ) {
            return null;
        }
        C M = f.ring.coFac.getONE();
        if ( f.isZERO() || f.isConstant() ) {
            return M;
        }
        C a = f.leadingBaseCoefficient().abs();
        for ( C c : f.getMap().values() ) {
            C d = c.abs().divide(a);
            if ( M.compareTo(d) < 0 ) {
                M = d;
            }
        }
        M = M.sum(f.ring.coFac.getONE());
        return M;
    }


    /**
     * Isolating intervals for the real roots.
     * @param <C> coefficient type.
     * @param f univariate polynomial.
     * @return a list of isolating intervalls for the real roots of f.
     */
    public static <C extends RingElem<C>> 
      List<Interval<C>> realRoots( GenPolynomial<C> f ) {
        List<Interval<C>> R = new ArrayList<Interval<C>>();
        if ( f == null ) {
            return R;
        }
        if ( f.isZERO() ) {
            C z = f.ring.coFac.getZERO();
            R.add( new Interval<C>(z) );
            return R;
        }
        GenPolynomial<C> F = f;
        C M = RootUtil.<C>realRootBound(F); // M != 0, since >= 2
        List<GenPolynomial<C>> S = RootUtil.<C>sturmSequence(F);
        System.out.println("S = " + S);
        //System.out.println("f_S = " + S.get(0));
        Interval<C> iv = new Interval<C>(M.negate(), M);
        System.out.println("iv = " + iv);
        List<Interval<C>> Rp = RootUtil.<C>realRoots( iv, /*S.get(0),*/ S );
        R.addAll(Rp);
        return R;
    }


    /**
     * Isolating intervals for the real roots.
     * @param <C> coefficient type.
     * @param iv interval with f(left) * f(right) != 0.
     * param f univariate polynomial.
     * @param S sturm sequence for f and I.
     * @return a list of isolating intervalls for the real roots of f in I.
     */
    public static <C extends RingElem<C>> 
           List<Interval<C>> realRoots( Interval<C> iv, 
                                        //GenPolynomial<C> f,
                                        List<GenPolynomial<C>> S ) {
        List<Interval<C>> R = new ArrayList<Interval<C>>();
        // check sign variations at interval bounds
        GenPolynomial<C> f = S.get(0); // squarefree part
        //System.out.println("iv = " + iv);
        RingFactory<C> cfac = f.ring.coFac;
        List<C> l = PolyUtil.<C> evaluateMain(cfac, S, iv.left);
        List<C> r = PolyUtil.<C> evaluateMain(cfac, S, iv.right);
        long v = RootUtil.<C>signVar(l) - RootUtil.<C>signVar(r);
        //System.out.println("v = " + v);
        if ( v == 0 ) {
            return R;
        }
        if ( v == 1 ) {
            R.add(iv);
            return R;
        }
        // now v &gt; 1
        // bi-sect interval, such that f(c) != 0
        C c = RootUtil.<C>bisectionPoint( iv, f );
        //System.out.println("c = " + c);
        // recursion on both sub-intervals
        Interval<C> iv1 = new Interval<C>(iv.left,c);
        Interval<C> iv2 = new Interval<C>(c,iv.right);
        List<Interval<C>> R1 = RootUtil.<C>realRoots( iv1, /*f,*/ S );
        System.out.println("R1 = " + R1);
        List<Interval<C>> R2 = RootUtil.<C>realRoots( iv2, /*f,*/ S );
        System.out.println("R2 = " + R2);

        // refine isolating intervals if adjacent 
        if ( R1.isEmpty() ) {
            R.addAll( R2 );
            return R;
        }
        if ( R2.isEmpty() ) {
            R.addAll( R1 );
            return R;
        }
        iv1 = R1.get( R1.size()-1 ); // last
        iv2 = R2.get(0); // first
        if ( iv1.right.compareTo( iv2.left ) < 0 ) {
            R.addAll(R1);
            R.addAll(R2);
            return R;
        }
        // now iv1.right == iv2.left
        //System.out.println("iv1 = " + iv1);
        //System.out.println("iv2 = " + iv2);
        R1.remove(iv1);
        R2.remove(iv2);
        while ( iv1.right.equals(iv2.left) ) {
            C d1 = RootUtil.<C>bisectionPoint( iv1, f );
            C d2 = RootUtil.<C>bisectionPoint( iv2, f );
            Interval<C> iv11 = new Interval<C>(iv1.left,d1);
            Interval<C> iv12 = new Interval<C>(d1,iv1.right);
            Interval<C> iv21 = new Interval<C>(iv2.left,d2);
            Interval<C> iv22 = new Interval<C>(d2,iv2.right);

            boolean b11 = RootUtil.<C>signChange( iv11, f );
            boolean b12 = RootUtil.<C>signChange( iv12, f );
            boolean b21 = RootUtil.<C>signChange( iv21, f );
            boolean b22 = RootUtil.<C>signChange( iv22, f );
            if ( !b12 ) {
                iv1 = iv11;
                if ( !b21 ) {
                    iv2 = iv22;
                } else {
                    iv2 = iv21;
                }
                break;
            }
            if ( !b21 ) {
                iv2 = iv22;
                if ( !b11 ) {
                    iv1 = iv12;
                } else {
                    iv1 = iv11;
                }
                break;
            }
            iv1 = iv12;
            iv2 = iv21;
            System.out.println("iv1 = " + iv1);
            System.out.println("iv2 = " + iv2);
        }
        R.addAll( R1 );
        R.add(iv1);
        R.add(iv2);
        R.addAll( R2 );
        return R;
    }


    /**
     * Bi-section point.
     * @param <C> coefficient type.
     * @param iv interval with f(left) * f(right) != 0.
     * @param f univariate polynomial, non-zero.
     * @return a point c in the interval iv such that f(c) != 0.
     */
    public static <C extends RingElem<C>> 
           C bisectionPoint( Interval<C> iv, GenPolynomial<C> f ) {
        if ( f == null ) {
            return null;
        }
        RingFactory<C> cfac = f.ring.coFac;
        C two = cfac.fromInteger(2);
        C c = iv.left.sum(iv.right);
        c = c.divide(two);
        if ( f.isZERO() || f.isConstant() ) {
            return c;
        }
        C m = PolyUtil.<C> evaluateMain(cfac, f, c);
        while ( m.isZERO() ) {
            C d = iv.left.sum(c);
            d = d.divide(two);
            if ( d.equals(c) ) {
                d = iv.right.sum(c);
                d = d.divide(two);
                if ( d.equals(c) ) {
                    throw new RuntimeException("should not happen " + iv);
                }
            }
            c = d;
            m = PolyUtil.<C> evaluateMain(cfac, f, c);
            //System.out.println("c = " + c);
        }
        //System.out.println("c = " + c);
        return c;
    }


    /**
     * Sign changes on interval bounds.
     * @param <C> coefficient type.
     * @param iv root isolating interval with f(left) * f(right) != 0.
     * @param f univariate polynomial.
     * @return true if f(left) * f(right) &lt; 0, else false
     */
    public static <C extends RingElem<C>> 
           boolean signChange( Interval<C> iv, GenPolynomial<C> f ) {
        if ( f == null ) {
            return false;
        }
        RingFactory<C> cfac = f.ring.coFac;
        C l = PolyUtil.<C> evaluateMain(cfac, f, iv.left);
        C r = PolyUtil.<C> evaluateMain(cfac, f, iv.right);
        return l.signum() * r.signum() < 0;
    }


    /**
     * Refine interval.
     * @param <C> coefficient type.
     * @param iv root isolating interval with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param eps requested interval length.
     * @return a new interval v such that |v| &lt; eps.
     */
    public static <C extends RingElem<C>> 
            Interval<C> refineInterval( Interval<C> iv, 
                                        GenPolynomial<C> f,
                                        C eps ) {
        if ( f == null || f.isZERO() || f.isConstant() || eps == null ) {
            return iv;
        }
        if ( iv.length().compareTo(eps) < 0 ) {
            return iv;
        }
        RingFactory<C> cfac = f.ring.coFac;
        C two = cfac.fromInteger(2);
        Interval<C> v = iv;
        while (v.length().compareTo(eps) >= 0) {
            C c = v.left.sum(v.right);
            c = c.divide(two);
            //c = RootUtil.<C>bisectionPoint(v,f);
            if ( PolyUtil.<C> evaluateMain(cfac, f, c).isZERO() ) {
                v = new Interval<C>(c,c);
                break;
            }
            Interval<C> iv1 = new Interval<C>(v.left,c);
            if ( RootUtil.<C>signChange(iv1,f) ) {
                v = iv1;
            } else {
                v = new Interval<C>(c,v.right);
            }
        }
        return v;
    }


    /**
     * Refine intervals.
     * @param <C> coefficient type.
     * @param V list of isolating intervals with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param eps requested intervals length.
     * @return a list of new intervals v such that |v| &lt; eps.
     */
    public static <C extends RingElem<C>> 
            List<Interval<C>> refineIntervals( List<Interval<C>> V, 
                                               GenPolynomial<C> f,
                                               C eps ) {
        if ( f == null || f.isZERO() || f.isConstant() || eps == null ) {
            return V;
        }
        List<Interval<C>> IV = new ArrayList<Interval<C>>();
        for ( Interval<C> v : V ) {
            Interval<C> iv = RootUtil.<C>refineInterval(v,f,eps);
            IV.add(iv);
        }
        return IV;
    }


    /**
     * Number of real roots in interval.
     * @param <C> coefficient type.
     * @param iv interval with f(left) * f(right) != 0.
     * param f univariate polynomial.
     * @param S sturm sequence for f and I.
     * @return number of real roots of f in I.
     */
    public static <C extends RingElem<C>> 
           long realRootCount( Interval<C> iv, 
                               //GenPolynomial<C> f,
                               List<GenPolynomial<C>> S ) {
        // check sign variations at interval bounds
        GenPolynomial<C> f = S.get(0); // squarefree part
        //System.out.println("iv = " + iv);
        RingFactory<C> cfac = f.ring.coFac;
        List<C> l = PolyUtil.<C> evaluateMain(cfac, S, iv.left);
        List<C> r = PolyUtil.<C> evaluateMain(cfac, S, iv.right);
        long v = RootUtil.<C>signVar(l) - RootUtil.<C>signVar(r);
        //System.out.println("v = " + v);
        return v;
    }


    /**
     * Algebraic number sign.
     * @param <C> coefficient type.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @return sign(g(v)), with v a new interval contained 
     *         in iv such that g(v) != 0.
     */
    public static <C extends RingElem<C>> 
            int algebraicSign( Interval<C> iv, 
                               GenPolynomial<C> f,
                               GenPolynomial<C> g ) {
        if ( g == null || g.isZERO() ) {
            return 0;
        }
        if ( f == null || f.isZERO() || f.isConstant() ) {
            return g.signum();
        }
        if ( g.isConstant() ) {
            return g.signum();
        }
        RingFactory<C> cfac = f.ring.coFac;
        C two = cfac.fromInteger(2);
        int sp = g.signum();
        List<GenPolynomial<C>> Sg = RootUtil.<C>sturmSequence(g);
        // System.out.println("g = " + g);
        g = Sg.get(0);
        if ( g.signum() != sp ) {
            sp = -1;
        } else {
            sp = 1;
        }
        //System.out.println("sp = " + sp);
        System.out.println("g = " + g);
        Interval<C> v = iv;
        while ( true ) {
            C c = v.left.sum(v.right);
            c = c.divide(two);
            long n = RootUtil.<C> realRootCount(v,Sg);
            if ( n == 0 ) {
               C ev = PolyUtil.<C> evaluateMain(cfac, g, c);
               System.out.println("ev = " + ev);
               return sp * ev.signum();
            }
            Interval<C> im = new Interval<C>(c,v.right);
            if ( RootUtil.<C>signChange(im,f) ) {
                v = im;
            } else {
                v = new Interval<C>(v.left,c);
            }
        }
        //return s;
    }

}
