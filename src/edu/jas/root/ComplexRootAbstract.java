/*
 * $Id$
 */

package edu.jas.root;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Arrays;

import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigDecimal;
import edu.jas.arith.ToRational;
import edu.jas.arith.Roots;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.Monomial;

import edu.jas.structure.RingElem;
import edu.jas.structure.StarRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;
import edu.jas.structure.Complex;
import edu.jas.structure.ComplexRing;
import edu.jas.structure.Power;

import edu.jas.ufd.Squarefree;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Complex roots abstract class.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public class ComplexRootAbstract<C extends RingElem<C>> 
                                   /*implements RealRoots<C>*/ {


    private static final Logger logger = Logger.getLogger(ComplexRootAbstract.class);


    private static boolean debug = true || logger.isDebugEnabled();


    /**
     * Root bound. With f(M) * f(-M) != 0.
     * @param f univariate polynomial.
     * @return M such that -M &lt; root(f) &lt; M.
     */
    public Complex<C> rootBound(GenPolynomial<Complex<C>> f) {
        if (f == null) {
            return null;
        }
        RingFactory<Complex<C>> cfac = f.ring.coFac;
        Complex<C> M = cfac.getONE();
        if (f.isZERO() || f.isConstant()) {
            return M;
        }
        Complex<C> a = f.leadingBaseCoefficient().norm();
        for (Complex<C> c : f.getMap().values()) {
            Complex<C> d = c.norm().divide(a);
            if (M.compareTo(d) < 0) {
                M = d;
            }
        }
        M = M.sum(f.ring.coFac.getONE());
        //System.out.println("M = " + M);
        return M;
    }


    /**
     * Cauchy index of rational function f/g on interval.
     * @param [a,b] interval I.
     * @param f univariate polynomial.
     * @param g univariate polynomial.
     * @return winding number of f/g in I.
     */
    public long indexOfCauchy(C a, C b, GenPolynomial<C> f, GenPolynomial<C> g) {
        List<GenPolynomial<C>> S = sturmSequence(g,f);
        //System.out.println("S = " + S);
        if ( debug ) {
            logger.info("sturmSeq = " + S);
        }
        RingFactory<C> cfac = f.ring.coFac;
        List<C> l = PolyUtil.<C> evaluateMain(cfac, S, a);
        List<C> r = PolyUtil.<C> evaluateMain(cfac, S, b);
        long v = RootUtil.<C> signVar(l) - RootUtil.<C> signVar(r);
        //System.out.println("v = " + v);
//         if (v < 0L) {
//             v = -v;
//         }
        return v;
    }


    /**
     * Routh index of complex function f + i g on interval.
     * @param [a,b] interval I.
     * @param f univariate polynomial.
     * @param g univariate polynomial != 0.
     * @return index number of f + i g.
     */
    public long[] indexOfRouth(C a, C b, GenPolynomial<C> f, GenPolynomial<C> g) {
        List<GenPolynomial<C>> S = sturmSequence(f,g);
        //System.out.println("S = " + S);
        RingFactory<C> cfac = f.ring.coFac;
        List<C> l = PolyUtil.<C> evaluateMain(cfac, S, a);
        List<C> r = PolyUtil.<C> evaluateMain(cfac, S, b);
        long v = RootUtil.<C> signVar(l) - RootUtil.<C> signVar(r);
        //System.out.println("v = " + v);

        long d = f.degree(0);
        if ( d < g.degree(0) ) {
            d = g.degree(0);
        }
        //System.out.println("d = " + d);
        long ui = ( d - v ) / 2;
        long li = ( d + v ) / 2;
        //System.out.println("upper = " + ui);
        //System.out.println("lower = " + li);
        return new long[] { ui, li };
    }


    /**
     * Sturm sequence.
     * @param f univariate polynomial.
     * @param g univariate polynomial.
     * @return a Sturm sequence for f.
     */
    public List<GenPolynomial<C>> sturmSequence(GenPolynomial<C> f, GenPolynomial<C> g) {
        List<GenPolynomial<C>> S = new ArrayList<GenPolynomial<C>>();
        if (f == null || f.isZERO()) {
            return S;
        }
        if (f.isConstant()) {
            S.add(f.monic());
            return S;
        }
        GenPolynomial<C> F = f;
        S.add(F);
        GenPolynomial<C> G = g; //PolyUtil.<C> baseDeriviative(f);
        while (!G.isZERO()) {
            GenPolynomial<C> r = F.remainder(G);
            F = G;
            G = r.negate();
            S.add(F/*.monic()*/);
        }
        //System.out.println("F = " + F);
        if (F.isConstant()) {
            return S;
        }
        // make squarefree
        List<GenPolynomial<C>> Sp = new ArrayList<GenPolynomial<C>>(S.size());
        for (GenPolynomial<C> p : S) {
            p = p.divide(F);
            Sp.add(p);
        }
        return Sp;
    }


    /**
     * Real part. 
     * @param fac result polynomial factory.
     * @param A polynomial with BigComplex coefficients to be converted.
     * @return polynomial with real part of the coefficients.
     */
    public //static <C extends RingElem<C>>
        GenPolynomial<C> 
        realPart( GenPolynomialRing<C> fac,
                  GenPolynomial<Complex<C>> A ) {
        return PolyUtil.<Complex<C>,C>map(fac,A, new RealPart<C>() );
    }


    /**
     * Imaginary part. 
     * @param fac result polynomial factory.
     * @param A polynomial with BigComplex coefficients to be converted.
     * @return polynomial with imaginary part of coefficients.
     */
    public //static <C extends RingElem<C>>
        GenPolynomial<C> 
        imaginaryPart( GenPolynomialRing<C> fac,
                       GenPolynomial<Complex<C>> A ) {
        return PolyUtil.<Complex<C>,C>map(fac,A, new ImagPart<C>() );
    }


    /**
     * Taylor series for polynomial.
     * @param f univariate polynomial.
     * @param a expansion point.
     * @return Taylor series (a polynomial) of f at a.
     */
    public static <C extends RingElem<C>>
           GenPolynomial<C> seriesOfTaylor(GenPolynomial<C> f, C a) {
        if ( f == null ) {
            return null;
        }
        GenPolynomialRing<C> fac = f.ring;
        if ( fac.nvar > 1 ) {
            throw new RuntimeException("only for univariate polynomials");
        }
        if ( f.isZERO() || f.isConstant() ) {
            return f;
        }
        GenPolynomial<C> s = fac.getZERO();
        C fa = PolyUtil.<C> evaluateMain(fac.coFac, f, a);
        s = s.sum(fa);
        //System.out.println("s = " + s);
        long n = 1;
        long i = 0;
        GenPolynomial<C> g = PolyUtil.<C> baseDeriviative(f);
        GenPolynomial<C> p = fac.getONE();
        //GenPolynomial<C> xa = fac.univariate(0,1).subtract(a);
        //System.out.println("xa = " + xa);
        while ( ! g.isZERO() ) {
            i++;
            n *= i;
            //p = p.multiply(xa);
            //System.out.println("p = " + p);
            fa = PolyUtil.<C> evaluateMain(fac.coFac, g, a);
            GenPolynomial<C> q = fac.univariate(0,i); //p;
            q = q.multiply(fa);
            q = q.divide( fac.fromInteger(n) );
            //System.out.println("q = " + q);
            s = s.sum(q);
            g = PolyUtil.<C> baseDeriviative(g);
        }
        //System.out.println("s = " + s);
        return s;
    }


    /**
     * Substitute linear polynomial to polynomial.
     * @param f univariate polynomial.
     * @param a constant coefficient of substituent.
     * @param b linear coefficient of substituent.
     * @return f(a+b*z).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<C> substituteLinear(GenPolynomial<C> f, C a, C b) {
        if ( f == null ) {
            return null;
        }
        GenPolynomialRing<C> fac = f.ring;
        if ( fac.nvar > 1 ) {
            throw new RuntimeException("only for univariate polynomials");
        }
        if ( f.isZERO() || f.isConstant() ) {
            return f;
        }
        GenPolynomial<C> z = fac.univariate(0,1L).multiply(b);
        // assert decending exponents, i.e. compatible term order
        Map<ExpVector,C> val = f.getMap();
        GenPolynomial<C> s = null;
        long el1 = -1; // undefined
        long el2 = -1;
        for ( ExpVector e : val.keySet() ) {
            el2 = e.getVal(0);
            if ( s == null /*el1 < 0*/ ) { // first turn
                s = fac.getZERO().sum( val.get( e ) );
            } else {
               for ( long i = el2; i < el1; i++ ) {
                   s = s.multiply( z );
               }
               s = s.sum( val.get( e ) );
            }
            el1 = el2;
        }
        for ( long i = 0; i < el2; i++ ) {
            s = s.multiply( z );
        }
        //System.out.println("s = " + s);
        return s;
    }


    /**
     * Winding number of complex function p on rectangle.
     * @param rect rectangle.
     * @param a univariate complex polynomial.
     * @return winding number of a arround rect.
     */
    @SuppressWarnings("unchecked")
    public long windingNumber(Rectangle<C> rect, GenPolynomial<Complex<C>> a) {
        ComplexRing<C> cr = (ComplexRing<C>)a.ring.coFac;
        RingFactory<C> cf = cr.ring;
        GenPolynomialRing<C> fac = new GenPolynomialRing<C>(cf,a.ring);
        C zero = cf.getZERO();
        C one = cf.getONE();
        Complex<C> im = cr.getIMAG();

        Complex<C>[] corner = rect.corners;

        GenPolynomial<Complex<C>>[] PC 
             = (GenPolynomial<Complex<C>>[]) new GenPolynomial[5];
        GenPolynomial<C>[] PCre 
             = (GenPolynomial<C>[]) new GenPolynomial[5];
        GenPolynomial<C>[] PCim 
             = (GenPolynomial<C>[]) new GenPolynomial[5];

        for ( int i = 0; i < 4; i++) {
            Complex<C> t = corner[i+1].subtract(corner[i]);
            //Complex<C> t = Power.<Complex<C>> power(cr,im,i);
            //System.out.println("t = " + t);
            GenPolynomial<Complex<C>> pc = seriesOfTaylor(a,corner[i]);
            pc = substituteLinear(pc,corner[i],t);
            PC[i] = pc;
            //System.out.println("PC["+i+"] = " + pc);
            GenPolynomial<C> f = realPart(fac,pc);
            GenPolynomial<C> g = imaginaryPart(fac,pc);
            //System.out.println("re() = " + f.toScript());
            //System.out.println("im() = " + g.toScript());
            PCre[i] = f;
            PCim[i] = g;
        }
        PC[4] = PC[0];
        PCre[4] = PCre[0];
        PCim[4] = PCim[0];

        long ix = 0L;
        for ( int i = 0; i < 4; i++) {
            long ci = indexOfCauchy(zero,one,PCre[i],PCim[i]);
            //System.out.println("ci["+i+","+(i+1)+"] = " + ci);
            ix += ci;
        }
        return ix/2L;
    }


    /**
     * List of complex roots of complex polynomial a on rectangle.
     * @param rect rectangle.
     * @param a univariate squarefree complex polynomial.
     * @return list of complex roots.
     */
    public List<Rectangle<C>> complexRoots(Rectangle<C> rect, GenPolynomial<Complex<C>> a) {
        ComplexRing<C> cr = (ComplexRing<C>)a.ring.coFac;

        List<Rectangle<C>> roots = new ArrayList<Rectangle<C>>();
        //System.out.println("rect = " + rect); 
        long n = windingNumber(rect,a);
        if ( n < 0 ) {
            throw new RuntimeException("negative winding number " + n);
        }
        if ( n == 0 ) {
            return roots;
        }
        if ( n == 1 ) {
            roots.add(rect);
            return roots;
        }
        Complex<C> eps = cr.fromInteger(1);
        eps = eps.divide( cr.fromInteger(1000) ); // 1/1000
        //System.out.println("eps = " + eps);

        //System.out.println("rect = " + rect); 
        Complex<C> delta = rect.corners[3].subtract(rect.corners[1]);
        delta = delta.divide( cr.fromInteger(2) );
        //System.out.println("delta = " + delta); 
        Complex<C> center = rect.corners[1].sum( delta );
        while ( center.isZERO() || center.getRe().isZERO() || center.getIm().isZERO() ) { 
            delta = delta.sum( delta.multiply(eps) ); // distort
            //System.out.println("delta = " + delta); 
            center = rect.corners[1].sum( delta );
            eps = eps.sum( eps.multiply(cr.getIMAG()) );
        }
        //System.out.println("center = " + center); 
        if ( debug ) {
            logger.info("new center = " + center); 
        }
        Complex<C>[] cp = Arrays.<Complex<C>>copyOf(rect.corners,4);
        // cp[0] fix
        cp[1] = new Complex<C>(cr,cp[1].getRe(),center.getIm());
        cp[2] = center;
        cp[3] = new Complex<C>(cr,center.getRe(),cp[3].getIm());
        Rectangle<C> nw = new Rectangle<C>(cp);
        //System.out.println("nw = " + nw); 
        List<Rectangle<C>> nwr = complexRoots(nw,a);
        //System.out.println("#nwr = " + nwr.size()); 
        roots.addAll(nwr);

        cp = Arrays.<Complex<C>>copyOf(rect.corners,4);
        cp[0] = new Complex<C>(cr,cp[0].getRe(),center.getIm());
        // cp[1] fix
        cp[2] = new Complex<C>(cr,center.getRe(),cp[2].getIm());
        cp[3] = center;
        Rectangle<C> sw = new Rectangle<C>(cp);
        //System.out.println("sw = " + sw); 
        List<Rectangle<C>> swr = complexRoots(sw,a);
        //System.out.println("#swr = " + swr.size()); 
        roots.addAll(swr);

        cp = Arrays.<Complex<C>>copyOf(rect.corners,4);
        cp[0] = center;
        cp[1] = new Complex<C>(cr,center.getRe(),cp[1].getIm());
        // cp[2] fix
        cp[3] = new Complex<C>(cr,cp[3].getRe(),center.getIm());
        Rectangle<C> se = new Rectangle<C>(cp);
        //System.out.println("se = " + se); 
        List<Rectangle<C>> ser = complexRoots(se,a);
        //System.out.println("#ser = " + ser.size()); 
        roots.addAll(ser);

        cp = Arrays.<Complex<C>>copyOf(rect.corners,4);
        cp[0] = new Complex<C>(cr,center.getRe(),cp[0].getIm());
        cp[1] = center;
        cp[2] = new Complex<C>(cr,cp[2].getRe(),center.getIm());
        // cp[3] fix
        Rectangle<C> ne = new Rectangle<C>(cp);
        //System.out.println("ne = " + ne); 
        List<Rectangle<C>> ner = complexRoots(ne,a);
        //System.out.println("#ner = " + ner.size()); 
        roots.addAll(ner);

        return roots;
    }


    /**
     * List of complex roots of complex polynomial.
     * @param a univariate complex polynomial.
     * @return list of complex roots.
     */
    @SuppressWarnings("unchecked")
    public List<Rectangle<C>> complexRoots(GenPolynomial<Complex<C>> a) {
        ComplexRing<C> cr = (ComplexRing<C>)a.ring.coFac;
        Squarefree<Complex<C>> engine = SquarefreeFactory.<Complex<C>> getImplementation(cr);
        SortedMap<GenPolynomial<Complex<C>>,Long> sa = engine.squarefreeFactors(a);
        List<Rectangle<C>> roots = new ArrayList<Rectangle<C>>();
        for ( GenPolynomial<Complex<C>> p : sa.keySet() ) {
            Complex<C> Mb = rootBound(p);
            C M = Mb.getRe();
            C M1 = M.sum(M.factory().fromInteger(1));
            //System.out.println("M = " + M);
            if ( debug ) {
                logger.info("rootBound = " + M);
            }
            Complex<C>[] corner = (Complex<C>[]) new Complex[4];
            corner[0] = new Complex<C>(cr,M1.negate(),M);            // nw
            corner[1] = new Complex<C>(cr,M1.negate(),M1.negate());  // sw
            corner[2] = new Complex<C>(cr,M,M1.negate());            // se
            corner[3] = new Complex<C>(cr,M,M);                      // ne
            Rectangle<C> rect = new Rectangle<C>(corner);

            List<Rectangle<C>> rs = complexRoots(rect,p);
            long e = sa.get(p);
            for ( int i = 0; i < e; i++ ) {
                 roots.addAll(rs);
            }
        }
        return roots;
    }


    /**
     * Complex root refinement of complex polynomial a on rectangle.
     * @param rect rectangle containing exactly one complex root.
     * @param a univariate squarefree complex polynomial.
     * @param len rational length for refinement.
     * @return refined complex root.
     */
    public Rectangle<C> complexRootRefinement(Rectangle<C> rect, GenPolynomial<Complex<C>> a, BigRational len) {
        ComplexRing<C> cr = (ComplexRing<C>)a.ring.coFac;
        Rectangle<C> root = rect;
        long w;
        if ( debug ) {
            w = windingNumber(root,a);
            if ( w < 1 ) {
                System.out.println("#root = " + w); 
                System.out.println("root = " + root); 
                throw new RuntimeException("no initial isolating rectangle " + rect);
            }
        }
        Complex<C> eps = cr.fromInteger(1);
        eps = eps.divide( cr.fromInteger(1000) ); // 1/1000
        //System.out.println("eps = " + eps);

        while ( root.rationalLength().compareTo( len ) > 0 ) {

            //System.out.println("------------------------------------"); 
            //System.out.println("root = " + root + ", len = " + new BigDecimal(root.rationalLength())); 
            Complex<C> delta = root.corners[3].subtract(root.corners[1]);
            delta = delta.divide( cr.fromInteger(2) );
            //System.out.println("delta = " + delta); 
            Complex<C> center = root.corners[1].sum( delta );
            while ( center.isZERO() || center.getRe().isZERO() || center.getIm().isZERO() ) { 
                delta = delta.sum( delta.multiply(eps) ); // distort
                //System.out.println("delta = " + delta); 
                center = root.corners[1].sum( delta );
                eps = eps.sum( eps.multiply(cr.getIMAG()) );
            }
            //System.out.println("center = " + center); 
            if ( debug ) {
                logger.info("new center = " + center); 
            }
            Complex<C>[] cp = Arrays.<Complex<C>>copyOf(root.corners,4);
            // cp[0] fix
            cp[1] = new Complex<C>(cr,cp[1].getRe(),center.getIm());
            cp[2] = center;
            cp[3] = new Complex<C>(cr,center.getRe(),cp[3].getIm());
            Rectangle<C> nw = new Rectangle<C>(cp);
            //System.out.println("nw = " + nw); 
            w = windingNumber(nw,a);
            //System.out.println("#nwr = " + w); 
            if ( w == 1 ) {
                root = nw;
                continue;
            }

            cp = Arrays.<Complex<C>>copyOf(root.corners,4);
            cp[0] = new Complex<C>(cr,cp[0].getRe(),center.getIm());
            // cp[1] fix
            cp[2] = new Complex<C>(cr,center.getRe(),cp[2].getIm());
            cp[3] = center;
            Rectangle<C> sw = new Rectangle<C>(cp);
            //System.out.println("sw = " + sw); 
            w = windingNumber(sw,a);
            //System.out.println("#swr = " + w); 
            if ( w == 1 ) {
                root = sw;
                continue;
            }

            cp = Arrays.<Complex<C>>copyOf(root.corners,4);
            cp[0] = center;
            cp[1] = new Complex<C>(cr,center.getRe(),cp[1].getIm());
            // cp[2] fix
            cp[3] = new Complex<C>(cr,cp[3].getRe(),center.getIm());
            Rectangle<C> se = new Rectangle<C>(cp);
            //System.out.println("se = " + se); 
            w = windingNumber(se,a);
            //System.out.println("#ser = " + w); 
            if ( w == 1 ) {
                root = se;
                continue;
            }

            cp = Arrays.<Complex<C>>copyOf(root.corners,4);
            cp[0] = new Complex<C>(cr,center.getRe(),cp[0].getIm());
            cp[1] = center;
            cp[2] = new Complex<C>(cr,cp[2].getRe(),center.getIm());
            // cp[3] fix
            Rectangle<C> ne = new Rectangle<C>(cp);
            //System.out.println("ne = " + ne); 
            w = windingNumber(ne,a);
            //System.out.println("#ner = " + w); 
            if ( w == 1 ) {
                root = ne;
                continue;
            }
            if ( debug ) {
                w = windingNumber(root,a);
                System.out.println("#root = " + w); 
                System.out.println("root = " + root); 
            }
            throw new RuntimeException("no isolating rectangle " + rect);
        }
        return root;
    }


    /**
     * Distance.
     * @param a complex number.
     * @param b complex number.
     * @return |a-b|.
     */
    public C distance(Complex<C> a, Complex<C> b) {
        Complex<C> d =  a.subtract(b);
        C r = d.norm().getRe();
        String s = r.toString();
        BigRational rs = new BigRational(s);
        //System.out.println("s  = " + s);
        BigDecimal rd = new BigDecimal(rs);
        rd = Roots.sqrt(rd);
        //System.out.println("rd = " + rd);
        r = a.ring.ring.parse( rd.toString() );
        //System.out.println("rd = " + rd + ", r  = " + r);
        return r;
    }

}

/**
 * Real part functor.
 */
class RealPart<C extends RingElem<C>> implements UnaryFunctor<Complex<C>,C> {
    public C eval(Complex<C> c) {
        if ( c == null ) {
            return null;
        } else {
            return c.getRe();
        }
    }
}


/**
 * Imaginary part functor.
 */
class ImagPart<C extends RingElem<C>> implements UnaryFunctor<Complex<C>,C> {
    public C eval(Complex<C> c) {
        if ( c == null ) {
            return null;
        } else {
            return c.getIm();
        }
    }
}
