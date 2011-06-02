/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.arith.Rational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.root.ComplexRoots;
import edu.jas.root.ComplexRootsSturm;
import edu.jas.root.RealRootTuple;
import edu.jas.structure.GcdRingElem;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Roots factory.
 * @author Heinz Kredel
 */
public class RootFactory {


    private static final Logger logger = Logger.getLogger(RootFactory.class);


    //private static boolean debug = logger.isDebugEnabled();


    /**
     * Is complex algebraic number a root of a polynomial.
     * @param f univariate polynomial.
     * @param r complex algebraic number.
     * @return true, if f(r) == 0, else false;
     */
    public static <C extends GcdRingElem<C> & Rational> 
           boolean isRoot(GenPolynomial<Complex<C>> f, Complex<RealAlgebraicNumber<C>> r) {
        ComplexRing<RealAlgebraicNumber<C>> cr = r.factory(); 
        GenPolynomialRing<Complex<RealAlgebraicNumber<C>>> cfac 
           = new GenPolynomialRing<Complex<RealAlgebraicNumber<C>>>(cr,f.factory());
        GenPolynomial<Complex<RealAlgebraicNumber<C>>> p;
        p = PolyUtilApp.<C> convertToComplexRealCoefficients(cfac,f);
        //System.out.println("p = " + p);
        Complex<RealAlgebraicNumber<C>> a = PolyUtil.<Complex<RealAlgebraicNumber<C>>> evaluateMain(cr,p,r);
        boolean t = a.isZERO();
        if ( !t ) {
            logger.info("p(r) = " + a);
        }
        return t;
    }


    /**
     * Complex algebraic numbers.
     * @param f univariate polynomial.
     * @return a list of different complex algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<Complex<RealAlgebraicNumber<C>>> complexAlgebraicNumbersComplex(GenPolynomial<Complex<C>> f) {
        GenPolynomialRing<Complex<C>> pfac = f.factory();
        if (pfac.nvar != 1) {
            throw new IllegalArgumentException("only for univariate polynomials");
        }
        ComplexRing<C> cfac = (ComplexRing<C>) pfac.coFac;
        ComplexRoots<C> cr = new ComplexRootsSturm<C>(cfac);
        SquarefreeAbstract<Complex<C>> engine = SquarefreeFactory.<Complex<C>> getImplementation(cfac);
        Map<GenPolynomial<Complex<C>>,Long> F = engine.squarefreeFactors(f.monic());
        Set<GenPolynomial<Complex<C>>> S = F.keySet();
        //System.out.println("S = " + S);
        List<Complex<RealAlgebraicNumber<C>>> list = new ArrayList<Complex<RealAlgebraicNumber<C>>>();
        for (GenPolynomial<Complex<C>> sp : S) {
            if (sp.isConstant() || sp.isZERO()) {
                continue;
            }
            List<Complex<RealAlgebraicNumber<C>>> ls = RootFactory.<C> complexAlgebraicNumbersSquarefree(sp);
            long m = F.get(sp);
            for (long i = 0L; i < m; i++) {
                list.addAll(ls);
            }
        }
        return list;
    }


    /**
     * Complex algebraic numbers.
     * @param f univariate squarefree polynomial.
     * @return a list of different complex algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<Complex<RealAlgebraicNumber<C>>> complexAlgebraicNumbersSquarefree(GenPolynomial<Complex<C>> f) {
        GenPolynomialRing<Complex<C>> pfac = f.factory();
        if (pfac.nvar != 1) {
            throw new IllegalArgumentException("only for univariate polynomials");
        }
        ComplexRing<C> cfac = (ComplexRing<C>) pfac.coFac;
        ComplexRoots<C> cr = new ComplexRootsSturm<C>(cfac);
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        GenPolynomialRing<Complex<C>> tfac = new GenPolynomialRing<Complex<C>>(cfac, 2, to); //,vars); //tord?
        //System.out.println("tfac = " + tfac);
        GenPolynomial<Complex<C>> t = tfac.univariate(1, 1L).sum(
                        tfac.univariate(0, 1L).multiply(cfac.getIMAG()));
        //System.out.println("t = " + t); // t = x + i y

        GenPolynomialRing<C> rfac = new GenPolynomialRing<C>(cfac.ring, tfac); //tord?
        //System.out.println("rfac = " + rfac);

        List<Complex<RealAlgebraicNumber<C>>> list = new ArrayList<Complex<RealAlgebraicNumber<C>>>();
        GenPolynomial<Complex<C>> sp = f;
        if (sp.isConstant() || sp.isZERO()) {
            return list;
        }
        GenPolynomial<Complex<C>> su = PolyUtil.<Complex<C>> substituteUnivariate(sp, t);
        //System.out.println("su = " + su);
        su = su.monic();
        //System.out.println("su = " + su);
        GenPolynomial<C> re = PolyUtil.<C> realPartFromComplex(rfac, su);
        GenPolynomial<C> im = PolyUtil.<C> imaginaryPartFromComplex(rfac, su);
        if ( logger.isInfoEnabled() ) {
            logger.debug("rfac = " + rfac.toScript());
            logger.info("t = " + t);
            logger.info("re   = " + re.toScript());
            logger.info("im   = " + im.toScript());
        }
        List<GenPolynomial<C>> li = new ArrayList<GenPolynomial<C>>(2);
        li.add(re);
        li.add(im);
        Ideal<C> id = new Ideal<C>(rfac, li);
        //System.out.println("id = " + id);

        List<IdealWithUniv<C>> idul = id.zeroDimRootDecomposition();

        IdealWithRealAlgebraicRoots<C, C> idr;
        for (IdealWithUniv<C> idu : idul) {
            //System.out.println("---idu = " + idu);
            idr = PolyUtilApp.<C, C> realAlgebraicRoots(idu);
            //System.out.println("---idr = " + idr);
            for (List<edu.jas.root.RealAlgebraicNumber<C>> crr : idr.ran) {
                //System.out.println("crr = " + crr);
                RealRootTuple<C> root = new RealRootTuple<C>(crr);
                //System.out.println("root = " + root);
                RealAlgebraicRing<C> car = new RealAlgebraicRing<C>(idu, root);
                //System.out.println("car = " + car);
                List<RealAlgebraicNumber<C>> gens = car.generators();
                //System.out.println("gens = " + gens);
                int sg = gens.size();
                RealAlgebraicNumber<C> rre = gens.get(sg-2);
                RealAlgebraicNumber<C> rim = gens.get(sg-1);
                ComplexRing<RealAlgebraicNumber<C>> cring = new ComplexRing<RealAlgebraicNumber<C>>(car);
                Complex<RealAlgebraicNumber<C>> crn = new Complex<RealAlgebraicNumber<C>>(cring,rre,rim);
                //System.out.println("crn = " + crn.toScript());
                list.add(crn);
            }
        }
        return list;
    }


    /*
     * Complex algebraic numbers.
     * @param f univariate polynomial.
     * @param eps rational precision.
     * @return a list of different complex algebraic numbers.
     public static <C extends GcdRingElem<C> & Rational> 
     List<ComplexAlgebraicNumber<C>> complexAlgebraicNumbersComplex(GenPolynomial<Complex<C>> f, BigRational eps) {
     ComplexRoots<C> cr = new ComplexRootsSturm<C>(f.ring.coFac);
     SquarefreeAbstract<Complex<C>> engine = SquarefreeFactory
     .<Complex<C>> getImplementation(f.ring.coFac);
     Set<GenPolynomial<Complex<C>>> S = engine.squarefreeFactors(f).keySet();
     List<ComplexAlgebraicNumber<C>> list = new ArrayList<ComplexAlgebraicNumber<C>>();
     for (GenPolynomial<Complex<C>> sp : S) {
     List<Rectangle<C>> iv = cr.complexRoots(sp);
     for (Rectangle<C> I : iv) {
     Rectangle<C> Iv = I;
     try {
     Iv = cr.complexRootRefinement(I,sp,eps);
     } catch (InvalidBoundaryException e) {
     e.printStackTrace();
     }
     ComplexAlgebraicRing<C> car = new ComplexAlgebraicRing<C>(sp, Iv);
     car.setEps(eps);
     ComplexAlgebraicNumber<C> cn = car.getGenerator();
     list.add(cn);
     }
     }
     return list;
     }
    */

}
