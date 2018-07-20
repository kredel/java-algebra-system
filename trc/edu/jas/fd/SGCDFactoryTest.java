/*
 * $Id$
 */

package edu.jas.fd;



import edu.jas.arith.BigComplex;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;

// import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Solvable GreatestCommonDivisor factory tests with JUnit.
 * @author Heinz Kredel
 */

public class SGCDFactoryTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>SGCDFactoryTest</CODE> object.
     * @param name String.
     */
    public SGCDFactoryTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SGCDFactoryTest.class);
        return suite;
    }


    TermOrder to = TermOrderByName.INVLEX;


    GenSolvablePolynomialRing<BigInteger> dfac;


    GenSolvablePolynomialRing<BigInteger> cfac;


    GenSolvablePolynomialRing<GenPolynomial<BigInteger>> rfac;


    BigInteger ai, bi, ci, di, ei;


    GenSolvablePolynomial<BigInteger> a, b, c, d, e;


    GenSolvablePolynomial<GenPolynomial<BigInteger>> ar, br, cr, dr, er;


    int rl = 5;


    int kl = 4;


    int ll = 5;


    int el = 3;


    float q = 0.3f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        dfac = new GenSolvablePolynomialRing<BigInteger>(new BigInteger(1), rl, to);
        cfac = new GenSolvablePolynomialRing<BigInteger>(new BigInteger(1), rl - 1, to);
        rfac = new GenSolvablePolynomialRing<GenPolynomial<BigInteger>>(cfac, 1, to);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        dfac = null;
        cfac = null;
        rfac = null;
    }


    /**
     * Test get BigInteger implementation.
     */
    public void testBigInteger() {
        BigInteger bi = new BigInteger();
        GreatestCommonDivisor<BigInteger> ufd;

        ufd = SGCDFactory.getImplementation(bi);
        //System.out.println("ufd = " + ufd);
        assertTrue("ufd = Primitive " + ufd, ufd instanceof GreatestCommonDivisorPrimitive);
    }


    /**
     * Test get ModInteger implementation.
     */
    public void testModInteger() {
        ModIntegerRing mi = new ModIntegerRing(19, true);
        GreatestCommonDivisor<ModInteger> ufd;

        ufd = SGCDFactory.getImplementation(mi);
        //System.out.println("ufd = " + ufd);
        assertTrue("ufd = Simple " + ufd, ufd instanceof GreatestCommonDivisorSimple);

        mi = new ModIntegerRing(30);
        ufd = SGCDFactory.getImplementation(mi);
        //System.out.println("ufd = " + ufd);
        assertTrue("ufd = Primitive " + ufd, ufd instanceof GreatestCommonDivisorPrimitive);
    }


    /**
     * Test get BigRational implementation.
     */
    public void testBigRational() {
        BigRational b = new BigRational();
        GreatestCommonDivisor<BigRational> ufd;

        ufd = SGCDFactory.getImplementation(b);
        //System.out.println("ufd = " + ufd);
        assertTrue("ufd = Primitive " + ufd, ufd instanceof GreatestCommonDivisorPrimitive);
    }


    /**
     * Test get BigComplex implementation.
     */
    public void testBigComplex() {
        BigComplex b = new BigComplex();
        GreatestCommonDivisor<BigComplex> ufd;

        ufd = SGCDFactory.<BigComplex> getImplementation(b);
        //System.out.println("ufd = " + ufd);
        assertTrue("ufd = Simple " + ufd, ufd instanceof GreatestCommonDivisorSimple);
    }


    /**
     * Test get AlgebraicNumber&lt;BigRational&gt; implementation.
     * 
     */
    public void testAlgebraicNumberBigRational() {
        BigRational b = new BigRational();
        GenPolynomialRing<BigRational> fac;
        fac = new GenPolynomialRing<BigRational>(b, 1);
        GenPolynomial<BigRational> mo = fac.random(kl, ll, el, q);
        while (mo.isZERO() || mo.isONE() || mo.isConstant()) {
            mo = fac.random(kl, ll, el, q);
        }

        AlgebraicNumberRing<BigRational> afac;
        afac = new AlgebraicNumberRing<BigRational>(mo);

        GreatestCommonDivisor<AlgebraicNumber<BigRational>> ufd;

        ufd = SGCDFactory.<AlgebraicNumber<BigRational>> getImplementation(afac);
        //System.out.println("ufd1 = " + ufd);
        assertTrue("ufd = Primitive " + ufd, ufd instanceof GreatestCommonDivisorPrimitive);


        mo = fac.univariate(0).subtract(fac.getONE());
        afac = new AlgebraicNumberRing<BigRational>(mo, true);

        ufd = SGCDFactory.<AlgebraicNumber<BigRational>> getImplementation(afac);
        //System.out.println("ufd1 = " + ufd);
        assertTrue("ufd = Simple " + ufd, ufd instanceof GreatestCommonDivisorSimple);
    }


    /**
     * Test get AlgebraicNumber&lt;ModInteger&gt; implementation.
     */
    public void testAlgebraicNumberModInteger() {
        ModIntegerRing b = new ModIntegerRing(19, true);
        GenPolynomialRing<ModInteger> fac;
        fac = new GenPolynomialRing<ModInteger>(b, 1);
        GenPolynomial<ModInteger> mo = fac.random(kl, ll, el, q);
        while (mo.isZERO() || mo.isONE() || mo.isConstant()) {
            mo = fac.random(kl, ll, el, q);
        }

        AlgebraicNumberRing<ModInteger> afac;
        afac = new AlgebraicNumberRing<ModInteger>(mo);

        AlgebraicNumber<ModInteger> a = afac.getONE();
        assertTrue("a == 1 " + a, a.isONE());
        GreatestCommonDivisor<AlgebraicNumber<ModInteger>> ufd;

        ufd = SGCDFactory.<AlgebraicNumber<ModInteger>> getImplementation(afac);
        //System.out.println("ufd2 = " + ufd);
        assertTrue("ufd = Primitive " + ufd, ufd instanceof GreatestCommonDivisorPrimitive);

        mo = fac.univariate(0).subtract(fac.getONE());
        afac = new AlgebraicNumberRing<ModInteger>(mo, true);
        ufd = SGCDFactory.<AlgebraicNumber<ModInteger>> getImplementation(afac);
        //System.out.println("ufd2 = " + ufd);
        assertTrue("ufd = Simple " + ufd, ufd instanceof GreatestCommonDivisorSimple);
    }

}
