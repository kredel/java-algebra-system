/*
 * $Id$
 */

package edu.jas.arith;


import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import edu.jas.arith.Rational;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;


/**
 * Arithmetic utilities.
 * @author Heinz Kredel
 */

public class ArithUtil {


    private static final Logger logger = LogManager.getLogger(ArithUtil.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Continued fraction.
     * @param A ratonal number.
     * @return continued fraction for A.
     */
    public static List<BigInteger> continuedFraction(BigRational A) {
        List<BigInteger> cf = new ArrayList<BigInteger>();
        if (A == null) {
            return cf;
        }
        if (A.isZERO() || A.isONE()) {
            cf.add(new BigInteger(A.num));
            return cf;
        }
        BigRational x = A;
        BigInteger q = new BigInteger(x.floor());
        cf.add(q);
        BigRational xd = x.subtract(new BigRational(q));
        while (!xd.isZERO()) {
            x = xd.inverse();
            q = new BigInteger(x.floor());
            cf.add(q);
            xd = x.subtract(new BigRational(q));
        }
        if (debug) {
            logger.info("cf = " + cf);
        }
        return cf;
    }


    /**
     * Continued fraction approximation.
     * @param A continued fraction.
     * @return ratonal number approximation for A.
     */
    public static BigRational continuedFractionApprox(List<BigInteger> A) {
        BigRational ab = BigRational.ZERO;
	if (A == null || A.isEmpty()) {
            return ab;
        }
        BigInteger a2, a1, b2, b1, a, b;
        a2 = BigInteger.ZERO; a1 = BigInteger.ONE;
        b2 = BigInteger.ONE; b1 = BigInteger.ZERO;
        for (BigInteger q : A) {
            a = q.multiply(a1).sum(a2);
            b = q.multiply(b1).sum(b2);
            //System.out.println("A/B = " + new BigRational(a,b));
            a2 = a1; a1 = a;
            b2 = b1; b1 = b;
        }
        ab = new BigRational(a1,b1);
        return ab;
    }

}
