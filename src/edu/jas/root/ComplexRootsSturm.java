/*
 * $Id$
 */

package edu.jas.root;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.Complex;
import edu.jas.structure.ComplexRing;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Complex roots implemented by Sturm sequences. Algorithms use exact method
 * derived from Wilf's numeric Routh-Hurwitz method.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public class ComplexRootsSturm<C extends RingElem<C>> extends ComplexRootAbstract<C> {


    private static final Logger logger = Logger.getLogger(ComplexRootsSturm.class);


    private static boolean debug = true || logger.isDebugEnabled();


    /**
     * Cauchy index of rational function f/g on interval.
     * @param [a,b] interval I.
     * @param f univariate polynomial.
     * @param g univariate polynomial.
     * @return winding number of f/g in I.
     */
    public long indexOfCauchy(C a, C b, GenPolynomial<C> f, GenPolynomial<C> g) {
        List<GenPolynomial<C>> S = sturmSequence(g, f);
        //System.out.println("S = " + S);
        if (debug) {
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
        List<GenPolynomial<C>> S = sturmSequence(f, g);
        //System.out.println("S = " + S);
        RingFactory<C> cfac = f.ring.coFac;
        List<C> l = PolyUtil.<C> evaluateMain(cfac, S, a);
        List<C> r = PolyUtil.<C> evaluateMain(cfac, S, b);
        long v = RootUtil.<C> signVar(l) - RootUtil.<C> signVar(r);
        //System.out.println("v = " + v);

        long d = f.degree(0);
        if (d < g.degree(0)) {
            d = g.degree(0);
        }
        //System.out.println("d = " + d);
        long ui = (d - v) / 2;
        long li = (d + v) / 2;
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
     * Complex root count of complex polynomial on rectangle.
     * @param rect rectangle.
     * @param a univariate complex polynomial.
     * @return root count of a in rectangle.
     */
    @Override
    public long complexRootCount(Rectangle<C> rect, GenPolynomial<Complex<C>> a) {
        return windingNumber(rect, a);
    }


    /**
     * Winding number of complex function p on rectangle.
     * @param rect rectangle.
     * @param a univariate complex polynomial.
     * @return winding number of a arround rect.
     */
    @SuppressWarnings("unchecked")
    public long windingNumber(Rectangle<C> rect, GenPolynomial<Complex<C>> a) {
        ComplexRing<C> cr = (ComplexRing<C>) a.ring.coFac;
        RingFactory<C> cf = cr.ring;
        GenPolynomialRing<C> fac = new GenPolynomialRing<C>(cf, a.ring);
        C zero = cf.getZERO();
        C one = cf.getONE();
        Complex<C> im = cr.getIMAG();

        Complex<C>[] corner = rect.corners;

        GenPolynomial<Complex<C>>[] PC = (GenPolynomial<Complex<C>>[]) new GenPolynomial[5];
        GenPolynomial<C>[] PCre = (GenPolynomial<C>[]) new GenPolynomial[5];
        GenPolynomial<C>[] PCim = (GenPolynomial<C>[]) new GenPolynomial[5];

        for (int i = 0; i < 4; i++) {
            Complex<C> t = corner[i + 1].subtract(corner[i]);
            //Complex<C> t = Power.<Complex<C>> power(cr,im,i);
            //System.out.println("t = " + t);
            GenPolynomial<Complex<C>> pc = seriesOfTaylor(a, corner[i]);
            pc = substituteLinear(pc, corner[i], t);
            PC[i] = pc;
            //System.out.println("PC["+i+"] = " + pc);
            GenPolynomial<C> f = realPart(fac, pc);
            GenPolynomial<C> g = imaginaryPart(fac, pc);
            //System.out.println("re() = " + f.toScript());
            //System.out.println("im() = " + g.toScript());
            PCre[i] = f;
            PCim[i] = g;
        }
        PC[4] = PC[0];
        PCre[4] = PCre[0];
        PCim[4] = PCim[0];

        long ix = 0L;
        for (int i = 0; i < 4; i++) {
            long ci = indexOfCauchy(zero, one, PCre[i], PCim[i]);
            //System.out.println("ci["+i+","+(i+1)+"] = " + ci);
            ix += ci;
        }
        return ix / 2L;
    }


    /**
     * List of complex roots of complex polynomial a on rectangle.
     * @param rect rectangle.
     * @param a univariate squarefree complex polynomial.
     * @return list of complex roots.
     */
    @Override
    public List<Rectangle<C>> complexRoots(Rectangle<C> rect, GenPolynomial<Complex<C>> a) {
        ComplexRing<C> cr = (ComplexRing<C>) a.ring.coFac;

        List<Rectangle<C>> roots = new ArrayList<Rectangle<C>>();
        //System.out.println("rect = " + rect); 
        long n = windingNumber(rect, a);
        if (n < 0) {
            throw new RuntimeException("negative winding number " + n);
        }
        if (n == 0) {
            return roots;
        }
        if (n == 1) {
            roots.add(rect);
            return roots;
        }
        Complex<C> eps = cr.fromInteger(1);
        eps = eps.divide(cr.fromInteger(1000)); // 1/1000
        //System.out.println("eps = " + eps);

        //System.out.println("rect = " + rect); 
        Complex<C> delta = rect.corners[3].subtract(rect.corners[1]);
        delta = delta.divide(cr.fromInteger(2));
        //System.out.println("delta = " + delta); 
        Complex<C> center = rect.corners[1].sum(delta);
        while (center.isZERO() || center.getRe().isZERO() || center.getIm().isZERO()) {
            delta = delta.sum(delta.multiply(eps)); // distort
            //System.out.println("delta = " + delta); 
            center = rect.corners[1].sum(delta);
            eps = eps.sum(eps.multiply(cr.getIMAG()));
        }
        //System.out.println("center = " + center); 
        if (debug) {
            logger.info("new center = " + center);
        }
        Complex<C>[] cp = Arrays.<Complex<C>> copyOf(rect.corners, 4);
        // cp[0] fix
        cp[1] = new Complex<C>(cr, cp[1].getRe(), center.getIm());
        cp[2] = center;
        cp[3] = new Complex<C>(cr, center.getRe(), cp[3].getIm());
        Rectangle<C> nw = new Rectangle<C>(cp);
        //System.out.println("nw = " + nw); 
        List<Rectangle<C>> nwr = complexRoots(nw, a);
        //System.out.println("#nwr = " + nwr.size()); 
        roots.addAll(nwr);

        cp = Arrays.<Complex<C>> copyOf(rect.corners, 4);
        cp[0] = new Complex<C>(cr, cp[0].getRe(), center.getIm());
        // cp[1] fix
        cp[2] = new Complex<C>(cr, center.getRe(), cp[2].getIm());
        cp[3] = center;
        Rectangle<C> sw = new Rectangle<C>(cp);
        //System.out.println("sw = " + sw); 
        List<Rectangle<C>> swr = complexRoots(sw, a);
        //System.out.println("#swr = " + swr.size()); 
        roots.addAll(swr);

        cp = Arrays.<Complex<C>> copyOf(rect.corners, 4);
        cp[0] = center;
        cp[1] = new Complex<C>(cr, center.getRe(), cp[1].getIm());
        // cp[2] fix
        cp[3] = new Complex<C>(cr, cp[3].getRe(), center.getIm());
        Rectangle<C> se = new Rectangle<C>(cp);
        //System.out.println("se = " + se); 
        List<Rectangle<C>> ser = complexRoots(se, a);
        //System.out.println("#ser = " + ser.size()); 
        roots.addAll(ser);

        cp = Arrays.<Complex<C>> copyOf(rect.corners, 4);
        cp[0] = new Complex<C>(cr, center.getRe(), cp[0].getIm());
        cp[1] = center;
        cp[2] = new Complex<C>(cr, cp[2].getRe(), center.getIm());
        // cp[3] fix
        Rectangle<C> ne = new Rectangle<C>(cp);
        //System.out.println("ne = " + ne); 
        List<Rectangle<C>> ner = complexRoots(ne, a);
        //System.out.println("#ner = " + ner.size()); 
        roots.addAll(ner);

        return roots;
    }

}
