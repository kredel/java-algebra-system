/*
 * $Id$
 */

package edu.jas.root;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.jas.arith.Rational;
import edu.jas.arith.BigRational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Roots factory.
 * @author Heinz Kredel
 */
public class RootFactory {


    /**
     * Real algebraic numbers.
     * @param f univariate polynomial.
     * @return a list of different real algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<RealAlgebraicNumber<C>> realAlgebraicNumbers(GenPolynomial<C> f) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        SquarefreeAbstract<C> engine = SquarefreeFactory.<C> getImplementation(f.ring.coFac);
        Set<GenPolynomial<C>> S = engine.squarefreeFactors(f).keySet();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        for (GenPolynomial<C> sp : S) {
            List<Interval<C>> iv = rr.realRoots(sp);
            for (Interval<C> I : iv) {
                RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(sp, I);
                RealAlgebraicNumber<C> rn = rar.getGenerator();
                list.add(rn);
            }
        }
        return list;
    }


    /**
     * Real algebraic numbers.
     * @param f univariate polynomial.
     * @param eps rational precision.
     * @return a list of different real algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<RealAlgebraicNumber<C>> realAlgebraicNumbers(GenPolynomial<C> f, BigRational eps) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        SquarefreeAbstract<C> engine = SquarefreeFactory.<C> getImplementation(f.ring.coFac);
        Set<GenPolynomial<C>> S = engine.squarefreeFactors(f).keySet();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        for (GenPolynomial<C> sp : S) {
            List<Interval<C>> iv = rr.realRoots(sp);
            for (Interval<C> I : iv) {
                RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(sp, I);
                rar.setEps(eps);
                RealAlgebraicNumber<C> rn = rar.getGenerator();
                list.add(rn);
            }
        }
        return list;
    }


    /**
     * Real algebraic numbers from a field.
     * @param f univariate polynomial.
     * @return a list of different real algebraic numbers from a field.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<RealAlgebraicNumber<C>> realAlgebraicNumbersField(GenPolynomial<C> f) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        FactorAbstract<C> engine = FactorFactory.<C> getImplementation(f.ring.coFac);
        Set<GenPolynomial<C>> S = engine.baseFactors(f).keySet();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        for (GenPolynomial<C> sp : S) {
            List<Interval<C>> iv = rr.realRoots(sp);
            for (Interval<C> I : iv) {
                RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(sp, I, true);//field
                RealAlgebraicNumber<C> rn = rar.getGenerator();
                list.add(rn);
            }
        }
        return list;
    }


    /**
     * Real algebraic numbers from a field.
     * @param f univariate polynomial.
     * @param eps rational precision.
     * @return a list of different real algebraic numbers from a field.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<RealAlgebraicNumber<C>> realAlgebraicNumbersField(GenPolynomial<C> f, BigRational eps) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        FactorAbstract<C> engine = FactorFactory.<C> getImplementation(f.ring.coFac);
        Set<GenPolynomial<C>> S = engine.baseFactors(f).keySet();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        for (GenPolynomial<C> sp : S) {
            List<Interval<C>> iv = rr.realRoots(sp);
            for (Interval<C> I : iv) {
                RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(sp, I, true);//field
                rar.setEps(eps);
                RealAlgebraicNumber<C> rn = rar.getGenerator();
                list.add(rn);
            }
        }
        return list;
    }


    /**
     * Real algebraic numbers from a irreducible polynomial.
     * @param f univariate irreducible polynomial.
     * @return a list of different real algebraic numbers from a field.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<RealAlgebraicNumber<C>> realAlgebraicNumbersIrred(GenPolynomial<C> f) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        List<Interval<C>> iv = rr.realRoots(f);
        for (Interval<C> I : iv) {
            RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(f, I, true);//field
            RealAlgebraicNumber<C> rn = rar.getGenerator();
            list.add(rn);
        }
        return list;
    }


    /**
     * Real algebraic numbers from a irreducible polynomial.
     * @param f univariate irreducible polynomial.
     * @param eps rational precision.
     * @return a list of different real algebraic numbers from a field.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<RealAlgebraicNumber<C>> realAlgebraicNumbersIrred(GenPolynomial<C> f, BigRational eps) {
        RealRoots<C> rr = new RealRootsSturm<C>();
        List<RealAlgebraicNumber<C>> list = new ArrayList<RealAlgebraicNumber<C>>();
        List<Interval<C>> iv = rr.realRoots(f);
        for (Interval<C> I : iv) {
            RealAlgebraicRing<C> rar = new RealAlgebraicRing<C>(f, I, true);//field
            rar.setEps(eps);
            RealAlgebraicNumber<C> rn = rar.getGenerator();
            list.add(rn);
        }
        return list;
    }


    /**
     * Complex algebraic numbers.
     * @param f univariate polynomial.
     * @return a list of different complex algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<ComplexAlgebraicNumber<C>> complexAlgebraicNumbersComplex(GenPolynomial<Complex<C>> f) {
        ComplexRoots<C> cr = new ComplexRootsSturm<C>(f.ring.coFac);
        SquarefreeAbstract<Complex<C>> engine = SquarefreeFactory
                .<Complex<C>> getImplementation(f.ring.coFac);
        Set<GenPolynomial<Complex<C>>> S = engine.squarefreeFactors(f).keySet();
        List<ComplexAlgebraicNumber<C>> list = new ArrayList<ComplexAlgebraicNumber<C>>();
        for (GenPolynomial<Complex<C>> sp : S) {
            List<Rectangle<C>> iv = cr.complexRoots(sp);
            for (Rectangle<C> I : iv) {
                ComplexAlgebraicRing<C> car = new ComplexAlgebraicRing<C>(sp, I);
                ComplexAlgebraicNumber<C> cn = car.getGenerator();
                list.add(cn);
            }
        }
        return list;
    }


    /**
     * Complex algebraic numbers.
     * @param f univariate polynomial.
     * @param eps rational precision.
     * @return a list of different complex algebraic numbers.
     */
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
                ComplexAlgebraicRing<C> car = new ComplexAlgebraicRing<C>(sp, I);
                car.setEps(eps);
                ComplexAlgebraicNumber<C> cn = car.getGenerator();
                list.add(cn);
            }
        }
        return list;
    }


    /**
     * Complex algebraic numbers.
     * @param f univariate (rational) polynomial.
     * @return a list of different complex algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> 
           List<ComplexAlgebraicNumber<C>> complexAlgebraicNumbers(GenPolynomial<C> f) {
        ComplexRing<C> cr = new ComplexRing<C>( f.ring.coFac );
        GenPolynomialRing<Complex<C>> fac = new GenPolynomialRing<Complex<C>>(cr,f.ring);
        GenPolynomial<Complex<C>> fc = PolyUtil.<C>complexFromAny(fac,f); 
        return complexAlgebraicNumbersComplex(fc);
    }


    /**
     * Complex algebraic numbers.
     * @param f univariate (rational) polynomial.
     * @param eps rational precision.
     * @return a list of different complex algebraic numbers.
     */
    public static <C extends GcdRingElem<C> & Rational> 
                             List<ComplexAlgebraicNumber<C>> complexAlgebraicNumbers(GenPolynomial<C> f, BigRational eps) {
        ComplexRing<C> cr = new ComplexRing<C>( f.ring.coFac );
        GenPolynomialRing<Complex<C>> fac = new GenPolynomialRing<Complex<C>>(cr,f.ring);
        GenPolynomial<Complex<C>> fc = PolyUtil.<C>complexFromAny(fac,f); 
        return complexAlgebraicNumbersComplex(fc,eps);
    }

}
