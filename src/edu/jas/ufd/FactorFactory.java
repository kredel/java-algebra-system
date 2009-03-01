
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


/**
 * Factorization algorithms factory.
 * Select appropriate factorization engine based on the coefficient types.
 * @author Heinz Kredel
 * @usage To create objects that implement the <code>Factorization</code> interface use the
 * <code>FactorFactory</code>. It will select an appropriate 
 * implementation based on the types of polynomial coefficients C.
 * To obtain an implementation use 
 * <code>getImplementation()</code>, it returns an object of a class which extends the
 * <code>FactorAbstract</code> class which implements the
 * <code>Factorization</code> interface.
 * <pre>
 * Factorization&lt;CT&gt; engine;
 *     engine = FactorFactory.&lt;CT&gt;getImplementation( cofac );
 * c = engine.factors(a);
 * </pre>
 * For example, if the coefficient type is BigInteger, the usage looks like
 * <pre>
 * BigInteger cofac = new BigInteger();
 * Factorization&lt;BigInteger&gt; engine; 
 *     engine = GCDFactory.getImplementation( cofac );
 * c = engine.factors(poly);
 * </pre>
 * @see edu.jas.ufd.Factorization#factors(edu.jas.poly.GenPolynomial P)
 * @see edu.jas.ufd.FactorAbstract#factors(edu.jas.poly.GenPolynomial P)
 */

public class FactorFactory {


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
    public static FactorAbstract<ModInteger> getImplementation( ModIntegerRing fac ) {
        return new FactorModular();
    }


    /**
     * Determine suitable implementation of factorization algorithm, case BigInteger.
     * @param fac BigInteger.
     * @return factorization algorithm implementation.
     */
    public static FactorAbstract<BigInteger> getImplementation( BigInteger fac ) {
        return new FactorInteger();
    }


    /** 
     * Determine suitable implementation of factorization algorithms, case BigRational.
     * @param fac BigRational.
     * @return factorization algorithm implementation.
     */
    public static FactorAbstract<BigRational> getImplementation( BigRational fac ) {
        return new FactorRational();
    }


    /**
     * Determine suitable implementation of factorization algorithms, case AlgebraicNumber&lt;C&gt;.
     * @param fac AlgebraicNumber&lt;C&gt;.
     * @param <C> coefficient type, e.g. BigRational, ModInteger.
     * @return factorization algorithm implementation.
     */
    public static <C extends GcdRingElem<C>>
           FactorAbstract<AlgebraicNumber<C>> getImplementation( AlgebraicNumberRing<C> fac ) {
        return new FactorAlgebraic<C>( fac );
    }


    /**
     * Determine suitable implementation of factorization algorithms, other cases.
     * @param <C> coefficient type
     * @param fac RingFactory&lt;C&gt;.
     * @return factorization algorithm implementation.
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> FactorAbstract<C> getImplementation( RingFactory<C> fac ) {
        //logger.info("fac = " + fac.getClass().getName());
        //System.out.println("fac = " + fac.getClass().getName());
        int t = 0;
        FactorAbstract/*raw type<C>*/ ufd = null; 
        AlgebraicNumberRing afac = null;
        while ( true ) { // switch
            Object ofac = fac;
            if ( ofac instanceof BigInteger ) {
                t = 1;
                break;
            } 
            if ( ofac instanceof BigRational ) {
                t = 2;
                break;
            }
            if ( ofac instanceof ModIntegerRing ) {
                t = 3;
                break;
            }
            if ( ofac instanceof AlgebraicNumberRing ) {
                afac = (AlgebraicNumberRing) ofac;
                ofac = afac.ring.coFac;
                if ( ofac instanceof BigInteger ) {
                    t = 4;
                } 
                if ( ofac instanceof ModIntegerRing ) {
                    t = 5;
                }
                break;
            }
            break;
        }
        //System.out.println("ft = " + t);
        if ( t == 0 ) {
            throw new RuntimeException("no factorization implementation for " + fac.getClass().getName());
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
            ufd = new FactorAlgebraic/*raw <C>*/( afac );
        }
        logger.debug("ufd = " + ufd);
        return (FactorAbstract<C>) ufd;
    }

}
