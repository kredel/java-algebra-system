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
import edu.jas.poly.RecSolvablePolynomial;
import edu.jas.poly.RecSolvablePolynomialRing;
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
        ComputerThreads.terminate();
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


    //GenSolvablePolynomialRing<BigRational> cfac;


    RecSolvablePolynomialRing<BigRational> rfac;
    //GenSolvablePolynomialRing<GenPolynomial<BigRational>> rfac;


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
        //String[] vars = new String[] { "a1", "a2", "a3", "a4", "a5" }; // #=rl
        String[] vars = new String[] { "a1", "a2", "a3", "a4" }; // #=rl
        dfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), to, vars);
        //cfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), rl - 1, to);
        //rfac = new GenSolvablePolynomialRing<GenPolynomial<BigRational>>(cfac, 1, to);
        rfac = (RecSolvablePolynomialRing<BigRational>) dfac.recursive(1);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        dfac = null;
        //cfac = null;
        rfac = null;
        //ComputerThreads.terminate();
    }


    /**
     * Test get BigRational parallel implementation.
     */
    public void testBigRational() {
        long t;
        BigRational bi = new BigRational();

        GreatestCommonDivisor<BigRational> fd_par;
        GreatestCommonDivisorAbstract<BigRational> fd;

        fd_par = SGCDFactory.<BigRational> getProxy(bi);
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

            t = System.currentTimeMillis();
            d = (GenSolvablePolynomial<BigRational>) fd_par.leftGcd(a, b);
            t = System.currentTimeMillis() - t;
            assertTrue("time >= 0: " + t, t >= 0);
            //System.out.println("i proxy time = " + t);
            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            //System.out.println("e = " + e);

            e = FDUtil.<BigRational> leftBaseSparsePseudoRemainder(d, c);
            //System.out.println("e = " + e);
            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());

            // now with right variants:
            c = (GenSolvablePolynomial<BigRational>) fd.rightPrimitivePart(c).abs();
            a = c.multiply(a);
            b = c.multiply(b);
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            t = System.currentTimeMillis();
            d = (GenSolvablePolynomial<BigRational>) fd_par.rightGcd(a, b);
            t = System.currentTimeMillis() - t;
            assertTrue("time >= 0: " + t, t >= 0);

            e = FDUtil.<BigRational> rightBaseSparsePseudoRemainder(d, c);
            //System.out.println("e = " + e);
            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());
        }
    }


    /**
     * Test get ModInteger parallel implementation.
     */
    public void testModInteger() {
        long t;
        ModIntegerRing mi = new ModIntegerRing(19, true);
        //ModIntegerRing mi = new ModIntegerRing(536870909, true);

        GenSolvablePolynomial<ModInteger> a, b, c, d, e;

        GreatestCommonDivisor<ModInteger> fd_par;
        GreatestCommonDivisorAbstract<ModInteger> fd;

        fd_par = SGCDFactory.<ModInteger> getProxy(mi);
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

            t = System.currentTimeMillis();
            d = (GenSolvablePolynomial<ModInteger>) fd_par.leftGcd(a, b);
            t = System.currentTimeMillis() - t;
            assertTrue("time >= 0: " + t, t >= 0);
            //System.out.println("m proxy time = " + t);
            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            //System.out.println("e = " + e);

            e = FDUtil.<ModInteger> leftBaseSparsePseudoRemainder(d, c);
            //System.out.println("e = " + e);
            assertTrue("c | gcd(ac,bc) " + e + ", " + d + ", " + c, e.isZERO());

            // now with right variants:
            c = (GenSolvablePolynomial<ModInteger>) fd.rightPrimitivePart(c).abs();
            a = c.multiply(a);
            b = c.multiply(b);
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            t = System.currentTimeMillis();
            d = (GenSolvablePolynomial<ModInteger>) fd_par.rightGcd(a, b);
            t = System.currentTimeMillis() - t;
            assertTrue("time >= 0: " + t, t >= 0);

            e = FDUtil.<ModInteger> rightBaseSparsePseudoRemainder(d, c);
            //System.out.println("e = " + e);
            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());
        }
    }


    /**
     * Test get BigInteger implementation.
     */
    public void testBigInteger() {
        BigInteger b = new BigInteger();
        GreatestCommonDivisor<BigInteger> fd;

        fd = SGCDFactory.<BigInteger> getImplementation(b);
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


    /**
     * Test get BigRational base parallel implementation.
     */
    public void testBigRationalBase() {
        long t;
        BigRational bi = new BigRational();

        GreatestCommonDivisorAbstract<BigRational> fd_par;
        GreatestCommonDivisorAbstract<BigRational> fd;

        fd_par = SGCDFactory.<BigRational> getProxy(bi);
        //System.out.println("fd_par = " + fd_par);
        assertTrue("fd_par != null " + fd_par, fd_par != null);

        fd = new GreatestCommonDivisorFake<BigRational>(bi);

        //System.out.println("fd = " + fd);
        assertTrue("fd != null " + fd, fd != null);

        int rl = 1;
        dfac = new GenSolvablePolynomialRing<BigRational>(bi, 1, to);

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

            t = System.currentTimeMillis();
            d = (GenSolvablePolynomial<BigRational>) fd_par.leftBaseGcd(a, b);
            t = System.currentTimeMillis() - t;
            assertTrue("time >= 0: " + t, t >= 0);
            //System.out.println("i proxy time = " + t);
            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            //System.out.println("e = " + e);

            e = FDUtil.<BigRational> leftBaseSparsePseudoRemainder(d, c);
            //System.out.println("e = " + e);
            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());

            // now with right variants:
            c = (GenSolvablePolynomial<BigRational>) fd.rightPrimitivePart(c).abs();
            a = c.multiply(a);
            b = c.multiply(b);
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            t = System.currentTimeMillis();
            d = (GenSolvablePolynomial<BigRational>) fd_par.rightBaseGcd(a, b);
            t = System.currentTimeMillis() - t;
            assertTrue("time >= 0: " + t, t >= 0);

            e = FDUtil.<BigRational> rightBaseSparsePseudoRemainder(d, c);
            //System.out.println("e = " + e);
            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());
        }
    }


    /**
     * Test get BigRational left recursive univariate parallel implementation.
     */
    public void testBigRationalLeftRecursiveUniv() {
        long t;
        BigRational bi = new BigRational();

        GreatestCommonDivisorAbstract<BigRational> fd_par;
        GreatestCommonDivisorAbstract<BigRational> fd;

        fd_par = SGCDFactory.<BigRational> getProxy(bi);
        //System.out.println("fd_par = " + fd_par);
        assertTrue("fd_par != null " + fd_par, fd_par != null);

        fd = new GreatestCommonDivisorFake<BigRational>(bi);

        //System.out.println("fd = " + fd);
        assertTrue("fd != null " + fd, fd != null);

        for (int i = 0; i < 1; i++) { // 10-50
            ar = rfac.random(kl + i, ll, el, q);
            br = rfac.random(kl + i, ll, el, q);
            cr = rfac.random(kl, ll, el, q);
            //cr = rfac.getONE();
            //cr = c.multiply( rfac.univariate(0) );
            cr = (GenSolvablePolynomial<GenPolynomial<BigRational>>) fd.leftRecursivePrimitivePart(cr).abs();
            //System.out.println("ar = " + ar);
            //System.out.println("br = " + br);
            //System.out.println("cr = " + cr);

            if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( cr" + i + " ) <> 0", cr.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !cr.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !cr.isONE() );

            ar = ar.multiply(cr);
            br = br.multiply(cr);
            //System.out.println("ar = " + ar);
            //System.out.println("br = " + br);

            t = System.currentTimeMillis();
            //                                                              left
            dr = (GenSolvablePolynomial<GenPolynomial<BigRational>>) fd_par.leftRecursiveUnivariateGcd(ar, br);
            t = System.currentTimeMillis() - t;
            assertTrue("time >= 0: " + t, t >= 0);
            //System.out.println("i proxy time = " + t);
            //System.out.println("cr = " + cr);
            //System.out.println("dr = " + dr);

            //recursiveLeftSparsePseudoRemainder
            er = FDUtil.<BigRational> recursiveSparsePseudoRemainder(dr, cr);
            //System.out.println("er = " + er);
            assertTrue("c | gcd(ac,bc) " + cr + " ~| " + dr, er.isZERO()||dr.isONE());
        }
    }


    /**
     * Test get BigRational right recursive univariate parallel implementation.
     */
    public void testBigRationalRightRecursiveUniv() {
        long t;
        BigRational bi = new BigRational();

        GreatestCommonDivisorAbstract<BigRational> fd_par;
        GreatestCommonDivisorAbstract<BigRational> fd;

        fd_par = SGCDFactory.<BigRational> getProxy(bi);
        //System.out.println("fd_par = " + fd_par);
        assertTrue("fd_par != null " + fd_par, fd_par != null);

        fd = new GreatestCommonDivisorFake<BigRational>(bi);

        //System.out.println("fd = " + fd);
        assertTrue("fd != null " + fd, fd != null);

        for (int i = 0; i < 1; i++) { // 10-50
            ar = rfac.random(kl + i, ll, el, q);
            br = rfac.random(kl + i, ll, el, q);
            cr = rfac.random(kl, ll, el, q);
            //cr = rfac.getONE();
            //cr = c.multiply( rfac.univariate(0) );
            //System.out.println("ar = " + ar);
            //System.out.println("br = " + br);
            //System.out.println("cr = " + cr);

            if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( cr" + i + " ) <> 0", cr.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !cr.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !cr.isONE() );

            // now with right variants:                                 right!
            cr = (GenSolvablePolynomial<GenPolynomial<BigRational>>) fd.rightRecursivePrimitivePart(cr).abs();
            ar = cr.multiply(ar);
            br = cr.multiply(br);
            //System.out.println("ar = " + ar);
            //System.out.println("br = " + br);
            //System.out.println("cr = " + cr);

            t = System.currentTimeMillis();
            dr = (GenSolvablePolynomial<GenPolynomial<BigRational>>) fd_par.rightRecursiveUnivariateGcd(ar, br);
            t = System.currentTimeMillis() - t;
            assertTrue("time >= 0: " + t, t >= 0);
            //System.out.println("cr = " + cr);
            //System.out.println("dr = " + dr);

            //recursiveRightSparsePseudoRemainder
            er = FDUtil.<BigRational> recursiveRightSparsePseudoRemainder(dr, cr);
            //System.out.println("er = " + er);
            assertTrue("c | gcd(ca,cb) " + cr + " ~| " + dr, er.isZERO()||dr.isONE());
        }
    }


    /**
     * Test get BigRational left recursive parallel implementation.
     */
    public void testBigRationalLeftRecursive() {
        long t;
        BigRational bi = new BigRational();
        rfac = (RecSolvablePolynomialRing<BigRational>) dfac.recursive(2);
        //System.out.println("rfac.gens = " + rfac.generators());

        GreatestCommonDivisorAbstract<BigRational> fd_par;
        GreatestCommonDivisorAbstract<BigRational> fd;

        fd_par = SGCDFactory.<BigRational> getProxy(bi);
        //System.out.println("fd_par = " + fd_par);
        assertTrue("fd_par != null " + fd_par, fd_par != null);

        fd = new GreatestCommonDivisorFake<BigRational>(bi);

        //System.out.println("fd = " + fd);
        assertTrue("fd != null " + fd, fd != null);

        for (int i = 0; i < 1; i++) { // 10-50
            ar = rfac.random(kl + i, ll, el, q);
            br = rfac.random(kl + i, ll, el, q);
            cr = rfac.random(kl, ll, el, q);
            //cr = rfac.getONE();
            //cr = c.multiply( rfac.univariate(0) );
            cr = (GenSolvablePolynomial<GenPolynomial<BigRational>>) fd.leftRecursivePrimitivePart(cr).abs();
            //System.out.println("ar = " + ar);
            //System.out.println("br = " + br);
            //System.out.println("cr = " + cr);

            if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( cr" + i + " ) <> 0", cr.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !cr.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !cr.isONE() );

            ar = ar.multiply(cr);
            br = br.multiply(cr);
            //System.out.println("ar = " + ar);
            //System.out.println("br = " + br);

            t = System.currentTimeMillis();
            dr = (GenSolvablePolynomial<GenPolynomial<BigRational>>) fd_par.leftRecursiveGcd(ar, br);
            t = System.currentTimeMillis() - t;
            assertTrue("time >= 0: " + t, t >= 0);
            //System.out.println("i proxy time = " + t);
            //System.out.println("cr = " + cr);
            //System.out.println("dr = " + dr);

            //recursiveLeftSparsePseudoRemainder
            er = FDUtil.<BigRational> recursiveSparsePseudoRemainder(dr, cr);
            //System.out.println("er = " + er);
            assertTrue("c | gcd(ac,bc) " + cr + " ~| " + dr, er.isZERO()||dr.isONE());
        }
    }


    /**
     * Test get BigRational right recursive parallel implementation.
     */
    public void testBigRationalRightRecursive() {
        long t;
        BigRational bi = new BigRational();
        rfac = (RecSolvablePolynomialRing<BigRational>) dfac.recursive(2);
        //System.out.println("rfac.gens = " + rfac.generators());

        GreatestCommonDivisorAbstract<BigRational> fd_par;
        GreatestCommonDivisorAbstract<BigRational> fd;

        fd_par = SGCDFactory.<BigRational> getProxy(bi);
        //System.out.println("fd_par = " + fd_par);
        assertTrue("fd_par != null " + fd_par, fd_par != null);

        fd = new GreatestCommonDivisorFake<BigRational>(bi);

        //System.out.println("fd = " + fd);
        assertTrue("fd != null " + fd, fd != null);

        for (int i = 0; i < 1; i++) { // 10-50
            ar = rfac.random(kl + i, ll, el, q);
            br = rfac.random(kl + i, ll, el, q);
            cr = rfac.random(kl, ll, el, q);
            //cr = rfac.getONE();
            //cr = c.multiply( rfac.univariate(0) );
            cr = (GenSolvablePolynomial<GenPolynomial<BigRational>>) fd.rightRecursivePrimitivePart(cr).abs();
            //System.out.println("ar = " + ar);
            //System.out.println("br = " + br);
            //System.out.println("cr = " + cr);

            if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( cr" + i + " ) <> 0", cr.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !cr.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !cr.isONE() );

            ar = cr.multiply(ar);
            br = cr.multiply(br);
            //System.out.println("ar = " + ar);
            //System.out.println("br = " + br);

            t = System.currentTimeMillis();
            dr = (GenSolvablePolynomial<GenPolynomial<BigRational>>) fd_par.rightRecursiveGcd(ar, br);
            t = System.currentTimeMillis() - t;
            assertTrue("time >= 0: " + t, t >= 0);
            //System.out.println("i proxy time = " + t);
            //System.out.println("cr = " + cr);
            //System.out.println("dr = " + dr);

            //recursiveRightSparsePseudoRemainder
            er = FDUtil.<BigRational> recursiveRightSparsePseudoRemainder(dr, cr);
            //System.out.println("er = " + er);
            assertTrue("c | gcd(ca,cb) " + cr + " ~| " + dr, er.isZERO()||dr.isONE());
        }
    }

}
