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

import edu.jas.arith.BigRational;

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
     * @return M such that -M &lt; root(f) &lt; M.
     */
    public C realRootBound(GenPolynomial<C> f) {
        if ( f == null ) {
            return null;
        }
        RingFactory<C> cfac = f.ring.coFac;
        C M = cfac.getONE();
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
        if ( (Object) M instanceof RealAlgebraicNumber ) {
            RealAlgebraicNumber Mr = (RealAlgebraicNumber) M;
            BigRational r = Mr.magnitude();
            M = cfac.fromInteger( r.numerator() ).divide( cfac.fromInteger( r.denominator() ) );
        }
        M = M.sum(f.ring.coFac.getONE());
        return M;
    }


    /**
     * Magnitude bound. 
     * @param iv interval.
     * @param f univariate polynomial.
     * @return B such that |f(c)| &lt; B for c in iv.
     */
    public C magnitudeBound(Interval<C> iv, GenPolynomial<C> f) {
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
        //System.out.println("fa = " + fa);
        C M = iv.left.abs();
        if ( M.compareTo( iv.right.abs() ) < 0 ) {
            M = iv.right.abs();
        }
        //System.out.println("M = " + M);
        RingFactory<C> cfac = f.ring.coFac;
        C B = PolyUtil.<C> evaluateMain(cfac, fa, M);
        //System.out.println("B = " + B);
        if ( (Object) B instanceof RealAlgebraicNumber ) {
            RealAlgebraicNumber Br = (RealAlgebraicNumber) B;
            BigRational r = Br.magnitude();
            B = cfac.fromInteger( r.numerator() ).divide( cfac.fromInteger( r.denominator() ) );
        }
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
        while (v.length().compareTo(eps) >= 0) {
            C c = v.left.sum(v.right);
            c = c.divide(two);
            //System.out.println("c = " + c);
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
     * Invariant interval for algebraic number sign.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @return v with v a new interval contained 
     *         in iv such that g(v) != 0.
     */
    public abstract Interval<C> invariantSignInterval( Interval<C> iv, 
                                                       GenPolynomial<C> f,
                                                       GenPolynomial<C> g );


    /**
     * Algebraic number sign.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0, 
     *           with iv such that g(iv) != 0.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @return sign(g(iv)) .
     */
    public int algebraicIntervalSign( Interval<C> iv, 
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
        C c = iv.left.sum(iv.right);
        c = c.divide(cfac.fromInteger(2));
        C ev = PolyUtil.<C> evaluateMain(cfac, g, c);
        //System.out.println("ev = " + ev);
        return ev.signum();
    }


    /**
     * Algebraic number sign.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @return sign(g(v)), with v a new interval contained 
     *         in iv such that g(v) != 0.
     */
    public int algebraicSign( Interval<C> iv, 
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
        Interval<C> v = invariantSignInterval( iv, f, g );
        return algebraicIntervalSign(v,f,g);
    }


    /**
     * Invariant interval for algebraic number magnitude.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @param eps length limit for interval length.
     * @return v with v a new interval contained 
     *         in iv such that |g(a) - g(b)| &lt; eps for a, b in v in iv.
     */
    public Interval<C> invariantMagnitudeInterval( Interval<C> iv, 
                                                   GenPolynomial<C> f,
                                                   GenPolynomial<C> g,
                                                   C eps ) {
        Interval<C> v = iv;
        if ( g == null || g.isZERO() ) {
            return v;
        }
        if ( g.isConstant() ) {
            return v;
        }
        if ( f == null || f.isZERO() || f.isConstant() ) { // ?
            return v;
        }
        GenPolynomial<C> gp = PolyUtil.<C> baseDeriviative(g);
        //System.out.println("g  = " + g);
        //System.out.println("gp = " + gp);
        C B = magnitudeBound(iv,gp);
        //System.out.println("B = " + B);

        RingFactory<C> cfac = f.ring.coFac;
        C two = cfac.fromInteger(2);

        while ( B.multiply(v.length()).compareTo(eps) >= 0 ) {
            C c = v.left.sum(v.right);
            c = c.divide(two);
            Interval<C> im = new Interval<C>(c,v.right);
            if ( signChange(im,f) ) {
                v = im;
            } else {
                v = new Interval<C>(v.left,c);
            }
            System.out.println("v = " + v.toDecimal());
        }
        return v;
    }


    /**
     * Algebraic number magnitude.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0,
     *         with iv such that |g(a) - g(b)| &lt; eps for a, b in iv.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @param eps length limit for interval length.
     * @return g(iv) .
     */
    public C algebraicIntervalMagnitude( Interval<C> iv, 
                                         GenPolynomial<C> f,
                                         GenPolynomial<C> g,
                                         C eps ) {
        if ( g.isZERO() || g.isConstant() ) {
            return g.leadingBaseCoefficient();
        }
        RingFactory<C> cfac = g.ring.coFac;
        C c = iv.left.sum(iv.right);
        c = c.divide(cfac.fromInteger(2));
        C ev = PolyUtil.<C> evaluateMain(cfac, g, c);
        //System.out.println("ev = " + ev);
        return ev;
    }


    /**
     * Algebraic number magnitude.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @param eps length limit for interval length.
     * @return g(iv) .
     */
    public C algebraicMagnitude( Interval<C> iv, 
                                 GenPolynomial<C> f,
                                 GenPolynomial<C> g,
                                 C eps ) {
        if ( g.isZERO() || g.isConstant() ) {
            return g.leadingBaseCoefficient();
        }
        Interval<C> v = invariantMagnitudeInterval( iv, f, g, eps );
        return algebraicIntervalMagnitude(v,f,g,eps);
    }

}
