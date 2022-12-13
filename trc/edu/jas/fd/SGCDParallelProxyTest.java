/*
 * $Id$
 */

package edu.jas.fd;


import edu.jas.arith.BigComplex;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Solvable GreatestCommonDivisor parallel proxy tests with JUnit.
 * @author Heinz Kredel
 */

public class SGCDParallelProxyTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        //ComputerThreads.terminate();
        //System.out.println("System.exit(0)");
    }


    /**
     * Constructs a <CODE>SGCDParallelProxyTest</CODE> object.
     * @param name String.
     */
    public SGCDParallelProxyTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SGCDParallelProxyTest.class);
        return suite;
    }


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenSolvablePolynomialRing<BigRational> dfac;


    GenSolvablePolynomialRing<BigRational> cfac;


    GenSolvablePolynomialRing<GenPolynomial<BigRational>> rfac;


    BigRational ai, bi, ci, di, ei;


    GenSolvablePolynomial<BigRational> a, b, c, d, e;


    GenSolvablePolynomial<GenPolynomial<BigRational>> ar, br, cr, dr, er;


    int rl = 5;


    int kl = 5;


    int ll = 7;


    int el = 3;


    float q = 0.3f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        dfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), rl, to);
        cfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), rl - 1, to);
        rfac = new GenSolvablePolynomialRing<GenPolynomial<BigRational>>(cfac, 1, to);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        dfac = null;
        cfac = null;
        rfac = null;
        ComputerThreads.terminate();
    }


    /**
     * Test get BigRational implementation.
     */
    public void testBigRational() {
        long t;
        BigRational bi = new BigRational();

        GreatestCommonDivisor<BigRational> fd_par;
        GreatestCommonDivisorAbstract<BigRational> fd;

        fd_par = SGCDFactory./*<BigRational>*/getProxy(bi);
        //System.out.println("fd_par = " + fd_par);
        assertTrue("fd_par != null " + fd_par, fd_par != null);

        fd = new GreatestCommonDivisorFake<BigRational>(bi);

        //System.out.println("fd = " + fd);
        assertTrue("fd != null " + fd, fd != null);

        dfac = new GenSolvablePolynomialRing<BigRational>(bi, 4, to);

        for (int i = 0; i < 1; i++) { // 10-50
            a = dfac.random(kl + i * 10, ll + i, el, q);
            b = dfac.random(kl + i * 10, ll + i, el, q);
            c = dfac.random(kl + 2, ll, el, q);
            //c = dfac.getONE();
            //c = c.multiply( dfac.univariate(0) );
            c = (GenSolvablePolynomial<BigRational>) fd.leftPrimitivePart(c).abs();
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            a = a.multiply(c);
            b = b.multiply(c);
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);
            /*
            System.out.println("\ni degrees: a = " + a.degree()
                                        + ", b = " + b.degree()
                                        + ", c = " + c.degree());
            */
            t = System.currentTimeMillis();
            d = (GenSolvablePolynomial<BigRational>) fd_par.leftGcd(a, b);
            t = System.currentTimeMillis() - t;
            //System.out.println("i proxy time = " + t);
            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            //System.out.println("e = " + e);

            e = FDUtil.<BigRational> leftBaseSparsePseudoRemainder(d, c);
            //System.out.println("e = " + e);
            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());
        }
        // obsolete ((SGCDParallelProxy<BigRational>)fd_par).terminate();
        ComputerThreads.terminate();
    }


    /**
     * Test get ModInteger implementation.
     */
    public void testModInteger() {
        long t;
        ModIntegerRing mi = new ModIntegerRing(19, true);
        //ModIntegerRing mi = new ModIntegerRing(536870909, true);

        GenSolvablePolynomial<ModInteger> a, b, c, d, e;

        GreatestCommonDivisor<ModInteger> fd_par;
        GreatestCommonDivisorAbstract<ModInteger> fd;

        fd_par = SGCDFactory.getProxy(mi);
        //System.out.println("fd_par = " + fd_par);
        assertTrue("fd_par != null " + fd_par, fd_par != null);

        fd = new GreatestCommonDivisorFake<ModInteger>(mi);

        //System.out.println("fd = " + fd);
        assertTrue("fd != null " + fd, fd != null);

        GenSolvablePolynomialRing<ModInteger> dfac;
        dfac = new GenSolvablePolynomialRing<ModInteger>(mi, 4, to);

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl + i * 2, ll + i, el, q);
            b = dfac.random(kl + i * 2, ll + i, el, q);
            c = dfac.random(kl, ll, el, q);
            //a = dfac.random(kl,ll+i,el,q);
            //b = dfac.random(kl,ll+i,el,q);
            //c = dfac.random(kl,ll,el,q);
            //c = dfac.getONE();
            //c = c.multiply( dfac.univariate(0) );
            c = (GenSolvablePolynomial<ModInteger>) fd.leftPrimitivePart(c).abs();
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            a = a.multiply(c);
            b = b.multiply(c);
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);
            /*
            System.out.println("\nm degrees: a = " + a.degree()
                                        + ", b = " + b.degree()
                                        + ", c = " + c.degree());
            */
            t = System.currentTimeMillis();
            d = (GenSolvablePolynomial<ModInteger>) fd_par.leftGcd(a, b);
            t = System.currentTimeMillis() - t;
            //System.out.println("m proxy time = " + t);
            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            //System.out.println("e = " + e);

            e = FDUtil.<ModInteger> leftBaseSparsePseudoRemainder(d, c);
            //System.out.println("e = " + e);
            assertTrue("c | gcd(ac,bc) " + e + ", " + d + ", " + c, e.isZERO());
        }
        // obsolete ((SGCDParallelProxy<ModInteger>)fd_par).terminate();
        ComputerThreads.terminate();
    }


    /**
     * Test get BigInteger implementation.
     */
    public void testBigInteger() {
        BigInteger b = new BigInteger();
        GreatestCommonDivisor<BigInteger> fd;

        fd = SGCDFactory./*<BigInteger>*/getImplementation(b);
        //System.out.println("fd = " + fd);
        assertTrue("fd = Primitive " + fd, fd instanceof GreatestCommonDivisorPrimitive);
    }


    /**
     * Test get BigComplex implementation.
     */
    public void testBigComplex() {
        BigComplex b = new BigComplex();
        GreatestCommonDivisor<BigComplex> fd;

        fd = SGCDFactory.<BigComplex> getImplementation(b);
        //System.out.println("fd = " + fd);
        assertTrue("fd != Simple " + fd, fd instanceof GreatestCommonDivisorSimple);
    }

}
