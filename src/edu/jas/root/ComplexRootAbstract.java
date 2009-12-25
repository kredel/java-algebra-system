/*
 * $Id$
 */

package edu.jas.root;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.arith.Roots;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.Complex;
import edu.jas.structure.ComplexRing;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;
import edu.jas.ufd.Squarefree;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Complex roots abstract class.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public abstract class ComplexRootAbstract<C extends RingElem<C>> implements ComplexRoots<C> {


    private static final Logger logger = Logger.getLogger(ComplexRootAbstract.class);


    private static boolean debug = true || logger.isDebugEnabled();


    /**
     * Engine for square free decomposition.
     */
    public final Squarefree<Complex<C>> engine;


    /**
     * Constructor.
     * @param cf coefficient factory.
     */
    public ComplexRootAbstract(RingFactory<Complex<C>> cf) {
        engine = SquarefreeFactory.<Complex<C>> getImplementation(cf);
    }


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
     * Complex root count of complex polynomial on rectangle.
     * @param rect rectangle.
     * @param a univariate complex polynomial.
     * @return root count of a in rectangle.
     */
    public abstract long complexRootCount(Rectangle<C> rect, GenPolynomial<Complex<C>> a) throws InvalidBoundaryException;


    /**
     * List of complex roots of complex polynomial a on rectangle.
     * @param rect rectangle.
     * @param a univariate squarefree complex polynomial.
     * @return list of complex roots.
     */
    public abstract List<Rectangle<C>> complexRoots(Rectangle<C> rect, GenPolynomial<Complex<C>> a) throws InvalidBoundaryException;


    /**
     * List of complex roots of complex polynomial.
     * @param a univariate complex polynomial.
     * @return list of complex roots.
     */
    @SuppressWarnings("unchecked")
    public List<Rectangle<C>> complexRoots(GenPolynomial<Complex<C>> a) {
        ComplexRing<C> cr = (ComplexRing<C>) a.ring.coFac;
        SortedMap<GenPolynomial<Complex<C>>, Long> sa = engine.squarefreeFactors(a);
        List<Rectangle<C>> roots = new ArrayList<Rectangle<C>>();
        for (GenPolynomial<Complex<C>> p : sa.keySet()) {
            Complex<C> Mb = rootBound(p);
            C M = Mb.getRe();
            C M1 = M.sum(M.factory().fromInteger(1));
            //System.out.println("M = " + M);
            if (debug) {
                logger.info("rootBound = " + M);
            }
            Complex<C>[] corner = (Complex<C>[]) new Complex[4];
            corner[0] = new Complex<C>(cr, M1.negate(), M); // nw
            corner[1] = new Complex<C>(cr, M1.negate(), M1.negate()); // sw
            corner[2] = new Complex<C>(cr, M, M1.negate()); // se
            corner[3] = new Complex<C>(cr, M, M); // ne
            Rectangle<C> rect = new Rectangle<C>(corner);
            try {
                List<Rectangle<C>> rs = complexRoots(rect, p);
                long e = sa.get(p);
                for (int i = 0; i < e; i++) { // add with multiplicity
                    roots.addAll(rs);
                }
            } catch (InvalidBoundaryException e) {
                throw new RuntimeException("this should not happen " + e);
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
    public Rectangle<C> complexRootRefinement(Rectangle<C> rect, GenPolynomial<Complex<C>> a, BigRational len) 
                        throws InvalidBoundaryException {
        ComplexRing<C> cr = (ComplexRing<C>) a.ring.coFac;
        Rectangle<C> root = rect;
        long w;
        if (debug) {
            w = complexRootCount(root, a);
            if (w != 1) {
                System.out.println("#root = " + w);
                System.out.println("root = " + root);
                throw new RuntimeException("no initial isolating rectangle " + rect);
            }
        }
        Complex<C> eps = cr.fromInteger(1);
        eps = eps.divide(cr.fromInteger(1000)); // 1/1000
        //System.out.println("eps = " + eps);
        BigRational length = len.multiply(len);
        //System.out.println("length = " + length);

        Complex<C> delta = null;
        boolean work = true;
        while ( work ) {
            try {
                while (root.rationalLength().compareTo(length) > 0) {
                    //System.out.println("root = " + root + ", len = " + new BigDecimal(root.rationalLength())); 
                    if ( delta == null ) {
                        delta = root.corners[3].subtract(root.corners[1]);
                        delta = delta.divide(cr.fromInteger(2));
                        //System.out.println("delta = " + toDecimal(delta)); 
                    }
                    Complex<C> center = root.corners[1].sum(delta);
                    //System.out.println("refine center = " + toDecimal(center)); 
                    if (debug) {
                        logger.info("new center = " + center);
                    }

                    Complex<C>[] cp = Arrays.<Complex<C>> copyOf(root.corners, 4);
                    // cp[0] fix
                    cp[1] = new Complex<C>(cr, cp[1].getRe(), center.getIm());
                    cp[2] = center;
                    cp[3] = new Complex<C>(cr, center.getRe(), cp[3].getIm());
                    Rectangle<C> nw = new Rectangle<C>(cp);
                    //System.out.println("nw = " + nw); 
                    w = complexRootCount(nw, a);
                    //System.out.println("#nwr = " + w); 
                    if (w == 1) {
                        root = nw;
                        delta = null;
                        continue;
                    }

                    cp = Arrays.<Complex<C>> copyOf(root.corners, 4);
                    cp[0] = new Complex<C>(cr, cp[0].getRe(), center.getIm());
                    // cp[1] fix
                    cp[2] = new Complex<C>(cr, center.getRe(), cp[2].getIm());
                    cp[3] = center;
                    Rectangle<C> sw = new Rectangle<C>(cp);
                    //System.out.println("sw = " + sw); 
                    w = complexRootCount(sw, a);
                    //System.out.println("#swr = " + w); 
                    if (w == 1) {
                        root = sw;
                        delta = null;
                        continue;
                    }

                    cp = Arrays.<Complex<C>> copyOf(root.corners, 4);
                    cp[0] = center;
                    cp[1] = new Complex<C>(cr, center.getRe(), cp[1].getIm());
                    // cp[2] fix
                    cp[3] = new Complex<C>(cr, cp[3].getRe(), center.getIm());
                    Rectangle<C> se = new Rectangle<C>(cp);
                    //System.out.println("se = " + se); 
                    w = complexRootCount(se, a);
                    //System.out.println("#ser = " + w); 
                    if (w == 1) {
                        root = se;
                        delta = null;
                        continue;
                    }

                    cp = Arrays.<Complex<C>> copyOf(root.corners, 4);
                    cp[0] = new Complex<C>(cr, center.getRe(), cp[0].getIm());
                    cp[1] = center;
                    cp[2] = new Complex<C>(cr, cp[2].getRe(), center.getIm());
                    // cp[3] fix
                    Rectangle<C> ne = new Rectangle<C>(cp);
                    //System.out.println("ne = " + ne); 
                    w = complexRootCount(ne, a);
                    //System.out.println("#ner = " + w); 
                    if (w == 1) {
                        root = ne;
                        delta = null;
                        continue;
                    }
                    if (false) {
                        w = complexRootCount(root, a);
                        System.out.println("#root = " + w);
                        System.out.println("root = " + root);
                    }
                    throw new RuntimeException("no isolating rectangle " + rect);
                }
                work = false;
            } catch(InvalidBoundaryException e) {
                // repeat
                delta = delta.sum(delta.multiply(eps)); // distort
                System.out.println("new refine delta = " + toDecimal(delta)); 
                eps = eps.sum(eps.multiply(cr.getIMAG()));
            }
        }
        return root;
    }


    /**
     * Get decimal approximation.
     * @param a complex number.
     * @return decimal(a).
     */
    public String toDecimal(Complex<C> a) {
        C r = a.getRe();
        String s = r.toString();
        BigRational rs = new BigRational(s);
        //System.out.println("s  = " + s);
        BigDecimal rd = new BigDecimal(rs);
        //System.out.println("rd = " + rd);
        C i = a.getIm();
        s = i.toString();
        BigRational is = new BigRational(s);
        //System.out.println("s  = " + s);
        BigDecimal id = new BigDecimal(is);
        //System.out.println("id = " + id);
        return rd.toString() + " i " + id.toString();
    }

    /**
     * Distance.
     * @param a complex number.
     * @param b complex number.
     * @return |a-b|.
    public C distance(Complex<C> a, Complex<C> b) {
        Complex<C> d = a.subtract(b);
        C r = d.norm().getRe();
        String s = r.toString();
        BigRational rs = new BigRational(s);
        //System.out.println("s  = " + s);
        BigDecimal rd = new BigDecimal(rs);
        rd = Roots.sqrt(rd);
        //System.out.println("rd = " + rd);
        r = a.ring.ring.parse(rd.toString());
        //System.out.println("rd = " + rd + ", r  = " + r);
        return r;
    }
     */

}
