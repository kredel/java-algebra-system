
/*
 * $Id$
 */

package edu.jas.ufd;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;

import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.GreatestCommonDivisorSubres;
import edu.jas.ufd.GreatestCommonDivisorPrimitive;
import edu.jas.ufd.GreatestCommonDivisorModular;
import edu.jas.ufd.GreatestCommonDivisorModEval;


/**
 * Factorization algorithms factory.
 * Select appropriate factorization engine based on the coefficient types.
 * @author Heinz Kredel
 * @usage To create classes that implement the <code>Factorization</code> interface use the
 * <code>FactorFactory</code>. It will select an appropriate 
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

public class FactorFactory /*<C extends GcdRingElem<C>>*/ {

    private static final Logger logger = Logger.getLogger(FactorFactory.class);



    /**
     * Protected factory constructor.
     */
     protected FactorFactory() {
     }                                              


    /**
     * Determine suitable implementation of factorization algorithm, case ModInteger.
     * @param fac ModInteger.
     * @return factorization algorithm implementation.
     */
    public static //<C extends ModInteger>
           FactorAbstract<ModInteger> getImplementation( ModIntegerRing fac ) {
        return new FactorModular();
    }


    /**
     * Determine suitable implementation of factorization algorithm, case BigInteger.
     * @param fac BigInteger.
     * @return factorization algorithm implementation.
     */
    public static //<C extends BigInteger>
           FactorAbstract<BigInteger> getImplementation( BigInteger fac ) {
        return new FactorInteger();
    }


    /**
     * Determine suitable implementation of factorization algorithms, case BigRational.
     * @param fac BigRational.
     * @return factorization algorithm implementation.
     */
    public static //<C extends BigRational>
           FactorAbstract<BigRational> getImplementation( BigRational fac ) {
        return new FactorRational();
    }


    /**
     * Determine suitable implementation of factorization algorithms, case BigRational.
     * @param fac AlgebraicNumber&lt;C&gt;.
     * @return factorization algorithm implementation.
     */
    public static <C extends GcdRingElem<C>>
           FactorAbstract<AlgebraicNumber<C>> getImplementation( AlgebraicNumberRing<C> fac ) {
        FactorAbstract<C> coeffFac = getImplementation( fac.ring.coFac );
        return new FactorAlgebraic<C>( coeffFac );
    }


    /**
     * Determine suitable implementation of factorization algorithms, other cases.
     * @param fac RingFactory&lt;C&gt;.
     * @return factorization algorithm implementation.
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>>
           FactorAbstract<C> getImplementation( RingFactory<C> fac ) {
        FactorAbstract/*raw type<C>*/ ufd = null; 
        logger.info("fac = " + fac.getClass().getName());
        int t = 0;
        AlgebraicNumberRing afac = null;
        while ( true ) { // switch
            Object ofac = fac;
            BigInteger b = new BigInteger(1);
            RingElem<C> bc = fac.fromInteger(1);
            if ( b.equals( bc ) ) {
                t = 1;
                logger.info("getImplementation = BigInteger");
                break;
            } 
            BigRational r = new BigRational(1);
            if ( r.equals( fac ) ) {
                t = 2;
                logger.info("getImplementation = BigRational");
                break;
            }
            if ( fac.characteristic().signum() > 0 ) {
                 ModIntegerRing m = new ModIntegerRing( fac.characteristic() );
                 //C mc = fac.fromInteger(1);
                 if ( m.equals( fac ) ) {
                     t = 3;
                     logger.info("getImplementation = ModInteger");
                     break;
                 } 
            }
            if ( ofac instanceof AlgebraicNumberRing ) {
                afac = (AlgebraicNumberRing) ofac;
                logger.info("getImplementation = AlgebraicNumberRing");
                if ( fac.characteristic().signum() == 0 ) {
                    t = 4;
                } else {
                    t = 5;
                }
                break;
            }
            break;
        }
        System.out.println("t = " + t);
        if ( t == 0 ) {
            throw new RuntimeException("no factorization implementation for " + fac);
        }
        if ( t == 1 ) {
            ufd = new FactorInteger();
        }
        if ( t == 2 ) {
            ufd = new FactorRational();
        }
        if ( t == 3 ) {
            ufd = new FactorModular();
        }
        if ( t == 4 || t == 5) {
            FactorAbstract/*raw <C>*/ coeffFac = getImplementation( afac );
            ufd = new FactorAlgebraic/*raw <C>*/( coeffFac );
        }
        logger.info("ufd = " + ufd);
        return (FactorAbstract<C>) ufd;
    }

}
