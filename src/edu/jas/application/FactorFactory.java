/*
 * $Id$
 */

package edu.jas.application;


import org.apache.log4j.Logger;

import edu.jas.arith.Rational;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.FactorAbstract;


/**
 * Factorization algorithms factory. Select appropriate factorization engine
 * based on the coefficient types.
 * @author Heinz Kredel
 * @usage To create objects that implement the <code>Factorization</code>
 *        interface use the <code>FactorFactory</code>. It will select an
 *        appropriate implementation based on the types of polynomial
 *        coefficients C. To obtain an implementation use
 *        <code>getImplementation()</code>, it returns an object of a class
 *        which extends the <code>FactorAbstract</code> class which implements
 *        the <code>Factorization</code> interface.
 * 
 *        <pre>
 * Factorization&lt;CT&gt; engine;
 * engine = FactorFactory.&lt;CT&gt; getImplementation(cofac);
 * c = engine.factors(a);
 * </pre>
 * 
 *        For example, if the coefficient type is BigInteger, the usage looks
 *        like
 * 
 *        <pre>
 * BigInteger cofac = new BigInteger();
 * Factorization&lt;BigInteger&gt; engine;
 * engine = FactorFactory.getImplementation(cofac);
 * Sm = engine.factors(poly);
 * </pre>
 * 
 * @see edu.jas.ufd.Factorization#factors(edu.jas.poly.GenPolynomial P)
 * @see edu.jas.ufd.FactorFactory#getImplementation(edu.jas.structure.RingFactory
 *      P)
 */

public class FactorFactory extends edu.jas.ufd.FactorFactory {


    private static final Logger logger = Logger.getLogger(FactorFactory.class);


    /**
     * Protected factory constructor.
     */
    protected FactorFactory() {
    }


    /**
     * Determine suitable implementation of factorization algorithms, case
     * RealAlgebraicNumber&lt;C&gt;.
     * @param fac RealAlgebraicRing&lt;C&gt;.
     * @param <C> coefficient type, e.g. BigRational.
     * @return factorization algorithm implementation.
     */
    public static <C extends GcdRingElem<C> & Rational> FactorAbstract<edu.jas.application.RealAlgebraicNumber<C>> getImplementation(
                    edu.jas.application.RealAlgebraicRing<C> fac) {
        return new FactorRealReal<C>(fac);
    }


    /**
     * Determine suitable implementation of factorization algorithms, other
     * cases.
     * @param <C> coefficient type
     * @param fac RingFactory&lt;C&gt;.
     * @return factorization algorithm implementation.
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> FactorAbstract<C> getImplementation(RingFactory<C> fac) {
        //logger.info("fac = " + fac.getClass().getName());
        //System.out.println("fac_o = " + fac.getClass().getName());
        FactorAbstract/*raw type<C>*/ufd = null;
        edu.jas.application.RealAlgebraicRing rrfac = null;
        Object ofac = fac;
        if (ofac instanceof edu.jas.application.RealAlgebraicRing) {
            //System.out.println("rrfac_o = " + ofac);
            rrfac = (edu.jas.application.RealAlgebraicRing) ofac;
            ofac = rrfac.realRing;
            ufd = new FactorRealReal/*raw <C>*/(rrfac);
        } else {
            ufd = edu.jas.ufd.FactorFactory.getImplementation(fac);
            return (FactorAbstract<C>) ufd;
        }
        logger.info("ufd = " + ufd);
        return (FactorAbstract<C>) ufd;
    }

}
