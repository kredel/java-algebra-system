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
 * @usage To create objects that implement the <code>GroebnerBase</code>
 *        interface one can use the <code>GBFactory</code> or this <code>GBAlgorithmBuilder</code>. 
 *        This class will select and compose an
 *        appropriate implementation based on the types of polynomial
 *        coefficients C and the desired properties. To build an implementation start with
 *        the static method <code>polynomialRing()</code> to define the polynomial ring. 
 *        Then continue to construct the algorithm with the methods 
 *        <ul>
 *        <li><code>optimize()</code> or <code>optimize(boolean)</code> for term order (variable order) 
 *            optimization,
 *        </li>
 *        <li><code>fractionFree()</code> for clearing denominators and computing with pseudo reduction,
 *        </li>
 *        <li><code>graded()</code> for using the FGLM algorithm to first compute with a graded term order and 
 *            then constructing a lexicographical Gr&ouml;bner base,
 *        </li>
 *        <li><code>parallel()</code> additionaly compute a Gr&ouml;bner base over a field in parallel,
 *        </li>
 *        <li><code>euclideanDomain()</code> for computing a e-Gr&ouml;bner base,
 *        </li>
 *        <li><code>domainAlgorithm(Algo)</code> for computing a d- or e-Gr&ouml;bner base,
 *        </li>
 *        </ul>
 *        Finaly call the method <code>build()</code> to obtain an implementaton of
 *        class <code>GroebnerBaseAbstract</code>. For example 
 * 
 *        <pre>
 * GenPolynomialRing&lt;C&gt; pf = new GenPolynomialRing&lt;C&gt;(cofac, vars);
 * GroebnerBaseAbstract&lt;C&gt; engine;
 * engine = GBAlgorithmBuilder.&lt;C&gt; polynomialRing(pf).fractionFree().parallel().optimize().build();
 * c = engine.GB(A);
 * </pre>
 * 
 *        For example, if the coefficient type is BigRational, the usage looks
 *        like
 * 
 *        <pre>
 * GenPolynomialRing&lt;BigRational&gt; pf = new GenPolynomialRing&lt;BigRational&gt;(cofac, vars);
 * GroebnerBaseAbstract&lt;BigRational&gt; engine;
 * engine = GBAlgorithmBuilder.&lt;BigRational&gt; polynomialRing(pf).fractionFree().parallel().optimize().build();
 * c = engine.GB(A);
 * </pre>
 * 
 * <b>Note:</b> Not all combinations are meanigful  
 * 
 * @see edu.jas.gb.GroebnerBase
 * @see edu.jas.gbufd.GBFactory
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
     * @return GBAlgorithmBuilder object.
     */
    public static <C extends GcdRingElem<C>> GBAlgorithmBuilder<C> polynomialRing(GenPolynomialRing<C> fac) {
        return new GBAlgorithmBuilder<C>(fac);
    }


    /**
     * Request term order optimization.
     * Call optimize(true) for return of permuted polynomials.
     * @return GBAlgorithmBuilder object.
     */
    public GBAlgorithmBuilder<C> optimize() {
        return optimize(true);
    }


    /**
     * Request term order optimization.
     * @param rP true for return of permuted polynomials, false for inverse
     *            permuted polynomials and new GB computation.
     * @return GBAlgorithmBuilder object.
     */
    public GBAlgorithmBuilder<C> optimize(boolean rP) {
        if (algo == null) {
           algo = GBFactory.<C> getImplementation(ring.coFac);
        }
        GroebnerBaseAbstract<C> bb = new GBOptimized<C>(algo, rP);
        return new GBAlgorithmBuilder<C>(ring, bb);
    }


    /**
     * Request fraction free algorithm.  For BigRational and Quotient
     * coefficients denominators are cleared and pseudo reduction is
     * used.
     * @return GBAlgorithmBuilder object.
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
     * Request e-GB algorithm.
     * @return GBAlgorithmBuilder object.
     */
    public GBAlgorithmBuilder<C> euclideanDomain() {
        return domainAlgorithm(GBFactory.Algo.egb);
    }


    /**
     * Request d- or e-GB algorithm.
     * @param a algorithm from GBFactory.Algo.
     * @return GBAlgorithmBuilder object.
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
     * Additionaly run a parallel algorithm via GBProxy.
     * @return GBAlgorithmBuilder object.
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
     * @return GBAlgorithmBuilder object.
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
