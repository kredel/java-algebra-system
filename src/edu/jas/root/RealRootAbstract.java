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
import edu.jas.poly.AlgebraicNumber;

import edu.jas.util.ListUtil;


/**
 * Real roots abstract class. 
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public abstract class RealRootAbstract<C extends RingElem<C>> 
                      implements RealRoots<C> {


    private static final Logger logger = Logger.getLogger(RealRootAbstract.class);
    private static boolean debug = logger.isDebugEnabled();


    /**
     * Real root bound. 
     * With f(M) * f(-M) != 0.
     * @param f univariate polynomial.
     * @return M such that -M &lt; root(f) &gt; M.
     */
    public C realRootBound(GenPolynomial<C> f) {
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
     * Size bound. 
     * @param iv interval.
     * @param f univariate polynomial.
     * @return B such that |f(c)| &lt; B for c in iv.
     */
    public C sizeBound(Interval<C> iv, GenPolynomial<C> f) {
        if ( f == null ) {
            return null;
        }
        if ( f.isZERO() ) {
            return f.ring.coFac.getONE();
        }
        if ( f.isConstant() ) {
            return f.leadingBaseCoefficient().abs();
        }
        GenPolynomial<C> fa = f.map( new UnaryFunctor<C,C>() { 
                                         public C eval(C a) {
                                             return a.abs();
                                         }
                                     } 
                                     );
        System.out.println("fa = " + fa);
        C M = iv.left.abs();
        if ( M.compareTo( iv.right.abs() ) < 0 ) {
            M = iv.right.abs();
        }
        System.out.println("M = " + M);
        RingFactory<C> cfac = f.ring.coFac;
        C B = PolyUtil.<C> evaluateMain(cfac, fa, M);
        System.out.println("B = " + B);
        return B;
    }


    /**
     * Bi-section point.
     * @param iv interval with f(left) * f(right) != 0.
     * @param f univariate polynomial, non-zero.
     * @return a point c in the interval iv such that f(c) != 0.
     */
    public C bisectionPoint( Interval<C> iv, GenPolynomial<C> f ) {
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
     * Isolating intervals for the real roots.
     * @param f univariate polynomial.
     * @return a list of isolating intervalls for the real roots of f.
     */
    public abstract List<Interval<C>> realRoots( GenPolynomial<C> f );


    /**
     * Isolating intervals for the real roots.
     * @param f univariate polynomial.
     * @param eps requested intervals length.
     * @return a list of isolating intervals v such that |v| &lt; eps.
     */
    public List<Interval<C>> realRoots( GenPolynomial<C> f, C eps ) {
        List<Interval<C>> iv = realRoots(f);
        return refineIntervals(iv,f,eps);
    }


    /**
     * Sign changes on interval bounds.
     * @param iv root isolating interval with f(left) * f(right) != 0.
     * @param f univariate polynomial.
     * @return true if f(left) * f(right) &lt; 0, else false
     */
    public boolean signChange( Interval<C> iv, GenPolynomial<C> f ) {
        if ( f == null ) {
            return false;
        }
        RingFactory<C> cfac = f.ring.coFac;
        C l = PolyUtil.<C> evaluateMain(cfac, f, iv.left);
        C r = PolyUtil.<C> evaluateMain(cfac, f, iv.right);
        return l.signum() * r.signum() < 0;
    }


    /**
     * Number of real roots in interval.
     * @param iv interval with f(left) * f(right) != 0.
     * @param f univariate polynomial.
     * @return number of real roots of f in I.
     */
    public abstract long realRootCount( Interval<C> iv, GenPolynomial<C> f);


    /**
     * Refine interval.
     * @param iv root isolating interval with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param eps requested interval length.
     * @return a new interval v such that |v| &lt; eps.
     */
    public Interval<C> refineInterval( Interval<C> iv, 
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
        System.out.println("r_eps = " + eps.getClass());
        while (v.length().compareTo(eps) >= 0) {
            System.out.println("|v| = " + v.length().getClass());
            C c = v.left.sum(v.right);
            c = c.divide(two);
            System.out.println("c = " + c);
            if ( ((Object)c) instanceof AlgebraicNumber ) {
                //AlgebraicNumber y = (AlgebraicNumber) c;
                AlgebraicNumber y = (AlgebraicNumber) v.length();
                RealAlgebraicNumber x = new RealAlgebraicNumber((RealAlgebraicRing)y.ring,y.val);
                System.out.println("||v|| = " + x.magnitude());
                AlgebraicNumber ye = (AlgebraicNumber) eps;
                RealAlgebraicNumber xe = new RealAlgebraicNumber((RealAlgebraicRing)ye.ring,ye.val);
                if ( x.compareTo(xe) < 0 ) {
                    System.out.println("break at ||v|| = " + x.magnitude());
                    break;
                }
            }
            //c = RootUtil.<C>bisectionPoint(v,f);
            if ( PolyUtil.<C> evaluateMain(cfac, f, c).isZERO() ) {
                v = new Interval<C>(c,c);
                break;
            }
            Interval<C> iv1 = new Interval<C>(v.left,c);
            if ( signChange(iv1,f) ) {
                v = iv1;
            } else {
                v = new Interval<C>(c,v.right);
            }
        }
        return v;
    }


    /**
     * Refine intervals.
     * @param V list of isolating intervals with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param eps requested intervals length.
     * @return a list of new intervals v such that |v| &lt; eps.
     */
    public List<Interval<C>> refineIntervals( List<Interval<C>> V, 
                                              GenPolynomial<C> f,
                                              C eps ) {
        if ( f == null || f.isZERO() || f.isConstant() || eps == null ) {
            return V;
        }
        List<Interval<C>> IV = new ArrayList<Interval<C>>();
        for ( Interval<C> v : V ) {
            Interval<C> iv = refineInterval(v,f,eps);
            IV.add(iv);
        }
        return IV;
    }


    /**
     * Algebraic number sign.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @return sign(g(v)), with v a new interval contained 
     *         in iv such that g(v) != 0.
     */
    public abstract int algebraicSign( Interval<C> iv, 
                                       GenPolynomial<C> f,
                                       GenPolynomial<C> g );

}
