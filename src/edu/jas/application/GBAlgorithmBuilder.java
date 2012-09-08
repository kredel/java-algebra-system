/*
 * $Id$
 */

package edu.jas.application;


import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.gb.GBOptimized;
import edu.jas.gb.GBProxy;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.GroebnerBaseParallel;
import edu.jas.gbufd.GBFactory;
import edu.jas.gbufd.GroebnerBaseFGLM;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;


/**
 * Builder for commutative Gr&ouml;bner bases algorithm implementations.
 * @author Heinz Kredel
 */
public class GBAlgorithmBuilder<C extends GcdRingElem<C>> implements Serializable {


    private static final Logger logger = Logger.getLogger(GBAlgorithmBuilder.class);


    /**
     * The current GB algorithm implementation.
     */
    private GroebnerBaseAbstract<C> algo;


    /**
     * The current polynomial ring.
     */
    public final GenPolynomialRing<C> ring;


    /**
     * Constructor not for use.
     */
    protected GBAlgorithmBuilder() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param ring the polynomial ring.
     */
    public GBAlgorithmBuilder(GenPolynomialRing<C> ring) {
        this(ring, null);
    }


    /**
     * Constructor.
     * @param ring the polynomial ring.
     * @param algo already determined algorithm.
     */
    public GBAlgorithmBuilder(GenPolynomialRing<C> ring, GroebnerBaseAbstract<C> algo) {
        this.ring = ring;
        this.algo = algo;
    }


    /**
     * Build the GB algorithm implementaton.
     */
    public GroebnerBaseAbstract<C> build() {
        if (algo == null) {
            algo = GBFactory.<C> getImplementation(ring.coFac);
        }
        return algo;
    }


    /**
     * Define polynomial ring.
     * @param fac the commutative polynomial ring.
     */
    public static <C extends GcdRingElem<C>> GBAlgorithmBuilder<C> polynomialRing(GenPolynomialRing<C> fac) {
        return new GBAlgorithmBuilder<C>(fac);
    }


    /**
     * Request term order optimization.
     * Call optimize(true) for return of permuted polynomials.
     */
    public GBAlgorithmBuilder<C> optimize() {
        return optimize(true);
    }


    /**
     * Request term order optimization.
     * @param rP true for return of permuted polynomials, false for inverse
     *            permuted polynomials and new GB computation.
     */
    public GBAlgorithmBuilder<C> optimize(boolean rP) {
        if (algo == null) {
           algo = GBFactory.<C> getImplementation(ring.coFac);
        }
        GroebnerBaseAbstract<C> bb = new GBOptimized<C>(algo, rP);
        return new GBAlgorithmBuilder<C>(ring, bb);
    }


    /**
     * Request fraction free algorithm.
     * @param rP true for return of permuted polynomials, false for inverse
     *            permuted polynomials and new GB computation.
     */
    @SuppressWarnings("unchecked")
    public GBAlgorithmBuilder<C> fractionFree() {
        if (ring.coFac instanceof BigRational) {
            BigRational cf = (BigRational) ring.coFac;
            GroebnerBaseAbstract<BigRational> bb = GBFactory.getImplementation(cf, GBFactory.Algo.ffgb);
            GroebnerBaseAbstract<C> cbb = (GroebnerBaseAbstract<C>) bb;
            return new GBAlgorithmBuilder<C>(ring, cbb);
        }
        if (ring.coFac instanceof QuotientRing) {
            QuotientRing<C> cf = (QuotientRing<C>) ring.coFac;
            GroebnerBaseAbstract<Quotient<C>> bb = GBFactory.<C> getImplementation(cf, GBFactory.Algo.ffgb);
            GroebnerBaseAbstract<C> cbb = (GroebnerBaseAbstract<C>) bb;
            return new GBAlgorithmBuilder<C>(ring, cbb);
        }
        logger.warn("no fraction free algorithm implemented for " + ring);
        return this;
    }


    /**
     * Request d- or e-GB algorithm.
     * @param a algorithm from GBFactory.Algo.
     */
    @SuppressWarnings("unchecked")
    public GBAlgorithmBuilder<C> domainAlgorithm(GBFactory.Algo a) {
        if (ring.coFac instanceof BigInteger) {
            BigInteger cf = (BigInteger) ring.coFac;
            GroebnerBaseAbstract<BigInteger> bb = GBFactory.getImplementation(cf, a);
            GroebnerBaseAbstract<C> cbb = (GroebnerBaseAbstract<C>) bb;
            return new GBAlgorithmBuilder<C>(ring, cbb);
        }
        logger.warn("no domain algorithm implemented for " + ring);
        return this;
    }


    /**
     * Request parallel algorithm.
     */
    @SuppressWarnings("unchecked")
    public GBAlgorithmBuilder<C> parallel() {
        if (ComputerThreads.NO_THREADS) {
            logger.warn("parallel algorithms disabled");
            return this;
        }
        if (ring.coFac.isField()) {
            if (algo == null) {
                algo = GBFactory.<C> getImplementation(ring.coFac);
            }
            GroebnerBaseAbstract<C> bb = new GroebnerBaseParallel<C>(ComputerThreads.N_CPUS);
            GroebnerBaseAbstract<C> pbb = new GBProxy<C>(algo, bb);
            return new GBAlgorithmBuilder<C>(ring, pbb);
        }
        logger.warn("no parallel algorithm implemented for " + ring);
        return this;
    }


    /**
     * Request FGLM algorithm.
     */
    @SuppressWarnings("unchecked")
    public GBAlgorithmBuilder<C> graded() {
        if (ring.coFac.isField()) {
            GroebnerBaseAbstract<C> bb;
            if (algo == null) {
                bb = new GroebnerBaseFGLM<C>();
            } else {
                bb = new GroebnerBaseFGLM<C>(algo);
            }
            return new GBAlgorithmBuilder<C>(ring, bb);
        }
        logger.warn("no FGLM algorithm implemented for " + ring);
        return this;
    }


    /**
     * String representation of the GB algorithm implementation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer(" ");
        if ( algo != null ) {
            s.append(algo.toString());
            s.append(" for ");
        }
        s.append(ring.toString());
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer(" ");
        if ( algo != null ) {
            s.append(algo.toString()); // nonsense
            s.append(" ");
        }
        s.append(ring.toScript());
        return s.toString();
    }

}
