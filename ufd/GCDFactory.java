
/*
 * $Id$
 */

package edu.jas.ufd;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.arith.ModInteger;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;

import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GreatestCommonDivisorSubres;
import edu.jas.ufd.GreatestCommonDivisorPrimitive;
import edu.jas.ufd.GreatestCommonDivisorModular;
import edu.jas.ufd.GreatestCommonDivisorModEval;


/**
 * Greatest common divisor algorithms factory.
 * Select appropriate GCD engine based on the coefficient types.
 * @todo Base decision also an degree vectors and number of variables 
 * of polynomials. 
 * Incorporate also number of CPUs / threads available (done with GCDProxy).
 * @author Heinz Kredel
 * @usage To create classes that implement this interface use the
 * GreatestCommonDivisorFactory. It will select an appropriate 
 * implementation based on the types of polynomial coefficients CT.
 * There are two methods to obtain an implementation: 
 * getProxy() and getImplementation(). 
 * getImplementation() returns an object of a class which implements the
 * GreatestCommonDivisor interface.
 * getProxy() returns a proxy object of a class which implements the
 * GreatestCommonDivisor interface. The proxy will run two implementations 
 * in parallel, return the first computed result 
 * and cancel the second running task. 
 * On systems with one CPU the computing time will be two times 
 * the time of the fastest algorithm implmentation. On systems 
 * with more than two CPUs the computing time will be the time of the 
 * fastest algorithm implmentation.
 * <pre>
 * GreatestCommonDivisor&lt;CT&gt; engine;
 *     engine = GCDFactory.&lt;CT&gt;getImplementation( cofac );
 * or  engine = GCDFactory.&lt;CT&gt;getProxy( cofac );
 * c = engine.gcd(a,b);
 * </pre>
 * For example, if the coefficient type is BigInteger, the usage looks like
 * <pre>
 * BigInteger cofac = new BigInteger();
 * GreatestCommonDivisor&lt;BigInteger&gt; engine; 
 *     engine = GCDFactory.&lt;BigInteger&gt;getImplementation( cofac );
 * or  engine = GCDFactory.&lt;BigInteger&gt;getProxy( cofac );
 * c = engine.gcd(a,b);
 * </pre>
 * @see edu.jas.ufd.GreatestCommonDivisor#gcd( edu.jas.poly.GenPolynomial P, edu.jas.poly.GenPolynomial S)
 */

public class GCDFactory /*<C extends GcdRingElem<C>>*/ {

    private static final Logger logger = Logger.getLogger(GCDFactory.class);



    /**
     * Protected factory constructor.
     */
     protected GCDFactory() {
     }                                              


    /**
     * Determine suitable implementation of gcd algorithms, case ModInteger.
     * @param fac ModInteger.
     * @return gcd algorithm implementation.
     */
    public static <C extends ModInteger>
           GreatestCommonDivisor<ModInteger> 
           getImplementation( ModInteger fac ) {
        GreatestCommonDivisor<ModInteger> ufd; 
        if ( fac.isField() ) {
           ufd = new GreatestCommonDivisorModEval();
           return ufd;
        }
        ufd = new GreatestCommonDivisorSubres<ModInteger>();
        return ufd;
    }


    /**
     * Determine suitable proxy for gcd algorithms, case ModInteger.
     * @param fac ModInteger.
     * @return gcd algorithm implementation.
     */
    public static <C extends ModInteger>
           GreatestCommonDivisor<ModInteger> 
           getProxy( ModInteger fac ) {
        GreatestCommonDivisor<ModInteger> ufd1, ufd2; 
        if ( fac.isField() ) {
           ufd1 = new GreatestCommonDivisorModEval();
        } else {
           ufd1 = new GreatestCommonDivisorSimple<ModInteger>();
        }
        ufd2 = new GreatestCommonDivisorSubres<ModInteger>();
        return new GCDProxy<ModInteger>(ufd1,ufd2);
    }


    /**
     * Determine suitable implementation of gcd algorithms, case BigInteger.
     * @param fac BigInteger.
     * @return gcd algorithm implementation.
     */
    public static <C extends BigInteger>
           GreatestCommonDivisor<BigInteger> 
           getImplementation( BigInteger fac ) {
        GreatestCommonDivisor<BigInteger> ufd; 
        if ( true ) {
           ufd = new GreatestCommonDivisorModular();
           return ufd;
        }
        ufd = new GreatestCommonDivisorSubres<BigInteger>();
        return ufd;
    }


    /**
     * Determine suitable procy for gcd algorithms, case BigInteger.
     * @param fac BigInteger.
     * @return gcd algorithm implementation.
     */
    public static <C extends BigInteger>
           GreatestCommonDivisor<BigInteger> 
           getProxy( BigInteger fac ) {
        GreatestCommonDivisor<BigInteger> ufd1, ufd2; 
        ufd1 = new GreatestCommonDivisorModular();
        ufd2 = new GreatestCommonDivisorSubres<BigInteger>();
        return new GCDProxy<BigInteger>(ufd1,ufd2);
    }


    /**
     * Determine suitable implementation of gcd algorithms, case BigRational.
     * @param fac BigRational.
     * @return gcd algorithm implementation.
     */
    public static <C extends BigRational>
           GreatestCommonDivisor<BigRational> 
           getImplementation( BigRational fac ) {
        GreatestCommonDivisor<BigRational> ufd; 
        ufd = new GreatestCommonDivisorPrimitive<BigRational>();
        return ufd;
    }


    /**
     * Determine suitable proxy for gcd algorithms, case BigRational.
     * @param fac BigRational.
     * @return gcd algorithm implementation.
     */
    public static <C extends BigRational>
           GreatestCommonDivisor<BigRational> 
           getProxy( BigRational fac ) {
        GreatestCommonDivisor<BigRational> ufd1, ufd2; 
        ufd1 = new GreatestCommonDivisorSimple<BigRational>();
        ufd2 = new GreatestCommonDivisorPrimitive<BigRational>();
        return new GCDProxy<BigRational>(ufd1,ufd2);
    }


    /**
     * Determine suitable implementation of gcd algorithms, other cases.
     * @param fac RingFactory<C>.
     * @return gcd algorithm implementation.
     */
    public static <C extends GcdRingElem<C>>
           GreatestCommonDivisor<C> 
           getImplementation( RingFactory<C> fac ) {
        GreatestCommonDivisor<C> ufd; 
        if ( fac.isField() ) {
           ufd = new GreatestCommonDivisorSimple<C>();
           return ufd;
        }
        ufd = new GreatestCommonDivisorSubres<C>();
        return ufd;
    }


    /**
     * Determine suitable proxy for gcd algorithms, other cases.
     * @param fac RingFactory<C>.
     * @return gcd algorithm implementation.
     */
    public static <C extends GcdRingElem<C>>
           GreatestCommonDivisor<C> 
           getProxy( RingFactory<C> fac ) {
        GreatestCommonDivisor<C> ufd1, ufd2; 
        ufd1 = new GreatestCommonDivisorSimple<C>();
        ufd2 = new GreatestCommonDivisorSubres<C>();
        return new GCDProxy<C>(ufd1,ufd2);
    }


}
