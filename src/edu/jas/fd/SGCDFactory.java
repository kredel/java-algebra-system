/*
 * $Id$
 */

package edu.jas.fd;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.kern.ComputerThreads;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Solvable greatest common divisor algorithms factory. Select appropriate SGCD
 * engine based on the coefficient types.
 * <p>
 * <b>Usage:</b> To create objects that implement the
 * <code>GreatestCommonDivisor</code> interface use the
 * <code>SGCDFactory</code>. It will select an appropriate implementation based
 * on the types of polynomial coefficients C. There are two methods to obtain an
 * implementation: <code>getProxy()</code> and <code>getImplementation()</code>.
 * <code>getImplementation()</code> returns an object of a class which
 * implements the <code>GreatestCommonDivisor</code> interface.
 * <code>getProxy()</code> returns a proxy object of a class which implements
 * the <code>GreatestCommonDivisor</code> interface. The proxy will run two
 * implementations in parallel, return the first computed result and cancel the
 * second running task. On systems with one CPU the computing time will be two
 * times the time of the fastest algorithm implementation. On systems with more
 * than two CPUs the computing time will be the time of the fastest algorithm
 * implementation.
 * 
 * <pre>
 * GreatestCommonDivisor&lt;CT&gt; engine;
 * engine = SGCDFactory.&lt;CT&gt; getImplementation(cofac);
 * or engine = SGCDFactory.&lt;CT&gt; getProxy(cofac);
 * c = engine.leftGcd(a, b);
 * </pre>
 * <p>
 * For example, if the coefficient type is <code>BigInteger</code>, the usage
 * looks like
 * 
 * <pre>
 * BigInteger cofac = new BigInteger();
 * GreatestCommonDivisor&lt;BigInteger&gt; engine;
 * engine = SGCDFactory.getImplementation(cofac);
 * or engine = SGCDFactory.getProxy(cofac);
 * c = engine.leftGcd(a, b);
 * </pre>
 * 
 * @author Heinz Kredel
 *
 * @see edu.jas.fd.GreatestCommonDivisor#leftGcd(edu.jas.poly.GenSolvablePolynomial
 *      P, edu.jas.poly.GenSolvablePolynomial S)
 */

public class SGCDFactory {


    private static final Logger logger = LogManager.getLogger(SGCDFactory.class);


    /**
     * Protected factory constructor.
     */
    protected SGCDFactory() {
    }


    /**
     * Determine suitable implementation of gcd algorithms, case ModLong.
     * @param fac ModLongRing.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<ModLong> getImplementation(ModLongRing fac) {
        logger.info("fac = {}", fac.getClass().getName());
        GreatestCommonDivisorAbstract<ModLong> ufd;
        if (fac.isField()) {
            ufd = new GreatestCommonDivisorSimple<ModLong>(fac);
            return ufd;
        }
        ufd = new GreatestCommonDivisorPrimitive<ModLong>(fac);
        return ufd;
    }


    /**
     * Determine suitable proxy for gcd algorithms, case ModLong.
     * @param fac ModLongRing.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<ModLong> getProxy(ModLongRing fac) {
        logger.info("fac = {}", fac.getClass().getName());
        GreatestCommonDivisorAbstract<ModLong> ufd1, ufd2;
        ufd1 = new GreatestCommonDivisorPrimitive<ModLong>(fac);
        if (fac.isField()) {
            ufd2 = new GreatestCommonDivisorSimple<ModLong>(fac);
        } else {
            ufd2 = new GreatestCommonDivisorSyzygy<ModLong>(fac);
        }
        return new SGCDParallelProxy<ModLong>(fac, ufd1, ufd2);
    }


    /**
     * Determine suitable implementation of gcd algorithms, case ModInteger.
     * @param fac ModIntegerRing.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<ModInteger> getImplementation(ModIntegerRing fac) {
        logger.info("fac = {}", fac.getClass().getName());
        GreatestCommonDivisorAbstract<ModInteger> ufd;
        if (fac.isField()) {
            ufd = new GreatestCommonDivisorSimple<ModInteger>(fac);
            return ufd;
        }
        ufd = new GreatestCommonDivisorPrimitive<ModInteger>(fac);
        return ufd;
    }


    /**
     * Determine suitable proxy for gcd algorithms, case ModInteger.
     * @param fac ModIntegerRing.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<ModInteger> getProxy(ModIntegerRing fac) {
        logger.info("fac = {}", fac.getClass().getName());
        GreatestCommonDivisorAbstract<ModInteger> ufd1, ufd2;
        ufd1 = new GreatestCommonDivisorPrimitive<ModInteger>(fac);
        if (fac.isField()) {
            ufd2 = new GreatestCommonDivisorSimple<ModInteger>(fac);
        } else {
            ufd2 = new GreatestCommonDivisorSyzygy<ModInteger>(fac);
        }
        return new SGCDParallelProxy<ModInteger>(fac, ufd1, ufd2);
    }


    /**
     * Determine suitable implementation of gcd algorithms, case BigInteger.
     * @param fac BigInteger.
     * @return gcd algorithm implementation.
     */
    @SuppressWarnings("unused")
    public static GreatestCommonDivisorAbstract<BigInteger> getImplementation(BigInteger fac) {
        GreatestCommonDivisorAbstract<BigInteger> ufd;
        logger.info("fac = {}", fac.getClass().getName());
        if (!fac.isField()) { // = false
            ufd = new GreatestCommonDivisorPrimitive<BigInteger>(fac);
        } else {
            ufd = new GreatestCommonDivisorSimple<BigInteger>(fac);
        }
        return ufd;
    }


    /**
     * Determine suitable proxy for gcd algorithms, case BigInteger.
     * @param fac BigInteger.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<BigInteger> getProxy(BigInteger fac) {
        if (fac == null) {
            throw new IllegalArgumentException("fac == null not supported");
        }
        logger.info("fac = {}", fac.getClass().getName());
        GreatestCommonDivisorAbstract<BigInteger> ufd1, ufd2;
        ufd1 = new GreatestCommonDivisorPrimitive<BigInteger>(fac);
        ufd2 = new GreatestCommonDivisorSyzygy<BigInteger>(fac);
        return new SGCDParallelProxy<BigInteger>(fac, ufd1, ufd2);
    }


    /**
     * Determine suitable implementation of gcd algorithms, case BigRational.
     * @param fac BigRational.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<BigRational> getImplementation(BigRational fac) {
        if (fac == null) {
            throw new IllegalArgumentException("fac == null not supported");
        }
        logger.info("fac = {}", fac.getClass().getName());
        GreatestCommonDivisorAbstract<BigRational> ufd;
        if (fac.isField()) { // = true
            ufd = new GreatestCommonDivisorSimple<BigRational>(fac);
            return ufd;
        }
        ufd = new GreatestCommonDivisorPrimitive<BigRational>(fac);
        return ufd;
    }


    /**
     * Determine suitable proxy for gcd algorithms, case BigRational.
     * @param fac BigRational.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<BigRational> getProxy(BigRational fac) {
        if (fac == null) {
            throw new IllegalArgumentException("fac == null not supported");
        }
        logger.info("fac = {}", fac.getClass().getName());
        GreatestCommonDivisorAbstract<BigRational> ufd1, ufd2;
        ufd1 = new GreatestCommonDivisorPrimitive<BigRational>(fac);
        ufd2 = new GreatestCommonDivisorSimple<BigRational>(fac);
        return new SGCDParallelProxy<BigRational>(fac, ufd1, ufd2);
    }


    /**
     * Determine suitable implementation of gcd algorithms, other cases.
     * @param fac RingFactory&lt;C&gt;.
     * @return gcd algorithm implementation.
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> GreatestCommonDivisorAbstract<C> getImplementation(
                    RingFactory<C> fac) {
        //System.out.println("SGCDFactory: " + fac.getClass().getName());
        GreatestCommonDivisorAbstract/*raw type<C>*/ ufd;
        logger.info("fac = {}", fac.getClass().getName());
        Object ofac = fac;
        if (ofac instanceof BigInteger) {
            ufd = new GreatestCommonDivisorPrimitive<C>(fac);
        } else if (ofac instanceof ModIntegerRing) {
            if (fac.isField()) {
                ufd = new GreatestCommonDivisorSimple<C>(fac);
            } else {
                ufd = new GreatestCommonDivisorPrimitive<C>(fac);
            }
        } else if (ofac instanceof ModLongRing) {
            if (fac.isField()) {
                ufd = new GreatestCommonDivisorSimple<C>(fac);
            } else {
                ufd = new GreatestCommonDivisorPrimitive<C>(fac);
            }
        } else if (ofac instanceof BigRational) {
            ufd = new GreatestCommonDivisorSimple<C>(fac);
        } else {
            if (fac.isField()) {
                ufd = new GreatestCommonDivisorSimple<C>(fac);
            } else {
                ufd = new GreatestCommonDivisorPrimitive<C>(fac);
            }
        }
        //System.out.println("SGCDFactory: " + ufd.getClass().getName());
        logger.debug("implementation = {}", ufd);
        return ufd;
    }


    /**
     * Determine fake implementation of gcd algorithms, other cases.
     * @param fac RingFactory&lt;C&gt;.
     * @return fake gcd algorithm implementation.
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> GreatestCommonDivisorAbstract<C> getFakeImplementation(
                    RingFactory<C> fac) {
        GreatestCommonDivisorAbstract/*raw type<C>*/ ufd;
        logger.info("fac = {}", fac.getClass().getName());
        //Object ofac = fac;
        ufd = new GreatestCommonDivisorFake<C>(fac);
        if (!(fac instanceof BigRational)) {
            //System.out.println("SGCDFactory: " + fac.getClass().getName());
            //System.out.println("SGCDFactory: " + ufd.getClass().getName());
        }
        logger.debug("implementation = {}", ufd);
        return ufd;
    }


    /**
     * Determine suitable proxy for gcd algorithms, other cases.
     * @param fac RingFactory&lt;C&gt;.
     * @return gcd algorithm implementation. <b>Note:</b> This method contains a
     *         hack for Google app engine to not use threads.
     * @see edu.jas.kern.ComputerThreads#NO_THREADS
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> GreatestCommonDivisorAbstract<C> getProxy(RingFactory<C> fac) {
        if (ComputerThreads.NO_THREADS) { // hack for Google app engine
            return SGCDFactory.<C> getImplementation(fac);
        }
        GreatestCommonDivisorAbstract/*raw type<C>*/ ufd;
        logger.info("fac = {}", fac.getClass().getName());
        Object ofac = fac;
        if (ofac instanceof BigInteger) {
            ufd = new SGCDParallelProxy<C>(fac, new GreatestCommonDivisorSimple<C>(fac),
                            new GreatestCommonDivisorPrimitive<C>(fac));
        } else if (ofac instanceof ModIntegerRing) {
            ufd = new SGCDParallelProxy<C>(fac, new GreatestCommonDivisorSimple<C>(fac),
                            new GreatestCommonDivisorPrimitive<C>(fac));
        } else if (ofac instanceof ModLongRing) {
            ufd = new SGCDParallelProxy<C>(fac, new GreatestCommonDivisorSimple<C>(fac),
                            new GreatestCommonDivisorPrimitive<C>(fac));
        } else if (ofac instanceof BigRational) {
            ufd = new SGCDParallelProxy<C>(fac, new GreatestCommonDivisorPrimitive<C>(fac),
                            new GreatestCommonDivisorSimple<C>(fac));
        } else {
            if (fac.isField()) {
                ufd = new SGCDParallelProxy<C>(fac, new GreatestCommonDivisorSimple<C>(fac),
                                new GreatestCommonDivisorPrimitive<C>(fac));
            } else {
                ufd = new SGCDParallelProxy<C>(fac, new GreatestCommonDivisorSyzygy<C>(fac),
                                new GreatestCommonDivisorPrimitive<C>(fac));
            }
        }
        logger.debug("ufd = {}", ufd);
        return ufd;
    }

}
