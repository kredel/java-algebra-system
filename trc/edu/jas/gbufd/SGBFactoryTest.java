/*
 * $Id$
 */

package edu.jas.gbufd;


// import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import edu.jas.arith.BigComplex;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.gb.SGBProxy;
import edu.jas.gb.SolvableGroebnerBase;
import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.gb.SolvableGroebnerBaseSeq;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.structure.RingFactory;


/**
 * Solvable Groebner base factory tests with JUnit.
 * @author Heinz Kredel
 */

public class SGBFactoryTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>SGBFactoryTest</CODE> object.
     * @param name String.
     */
    public SGBFactoryTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SGBFactoryTest.class);
        return suite;
    }


    //private final static int bitlen = 100;

    TermOrder to = new TermOrder(TermOrder.INVLEX);


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
        SolvableGroebnerBase<BigInteger> bba;

        bba = SGBFactory.getImplementation(bi);
        //System.out.println("bba = " + bba);
        assertTrue("bba integer " + bba, bba instanceof SolvableGroebnerBasePseudoSeq);
    }


    /**
     * Test get ModInteger implementation.
     */
    public void testModInteger() {
        ModIntegerRing mi = new ModIntegerRing(19, true);
        SolvableGroebnerBase<ModInteger> bba;

        bba = SGBFactory.getImplementation(mi);
        //System.out.println("bba = " + bba);
        assertTrue("bba modular field " + bba, bba instanceof SolvableGroebnerBaseSeq);

        mi = new ModIntegerRing(30);
        bba = SGBFactory.getImplementation(mi);
        //System.out.println("bba = " + bba);
        assertTrue("bba modular ring " + bba, bba instanceof SolvableGroebnerBasePseudoSeq);
    }


    /**
     * Test get BigRational implementation.
     */
    public void testBigRational() {
        BigRational b = new BigRational();
        SolvableGroebnerBase<BigRational> bba;

        bba = SGBFactory.getImplementation(b);
        //System.out.println("bba = " + bba);
        assertTrue("bba field " + bba, bba instanceof SolvableGroebnerBaseSeq);
    }


    /**
     * Test get BigComplex implementation.
     */
    public void testBigComplex() {
        BigComplex b = new BigComplex();
        SolvableGroebnerBase<BigComplex> bba;

        bba = SGBFactory.<BigComplex> getImplementation(b);
        //System.out.println("bba = " + bba);
        assertTrue("bba field " + bba, bba instanceof SolvableGroebnerBaseSeq);
    }


    /**
     * Test get AlgebraicNumber&lt;BigRational&gt; implementation.
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

        SolvableGroebnerBase<AlgebraicNumber<BigRational>> bba;

        bba = SGBFactory.<AlgebraicNumber<BigRational>> getImplementation(afac);
        //System.out.println("bba1 = " + bba);
        assertTrue("bba algebraic ring " + bba, bba instanceof SolvableGroebnerBasePseudoSeq);

        mo = fac.univariate(0).subtract(fac.getONE());
        afac = new AlgebraicNumberRing<BigRational>(mo, true);

        bba = SGBFactory.<AlgebraicNumber<BigRational>> getImplementation(afac);
        //System.out.println("bba1 = " + bba);
        assertTrue("bba algebraic field " + bba, bba instanceof SolvableGroebnerBaseSeq);
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
        SolvableGroebnerBase<AlgebraicNumber<ModInteger>> bba;

        bba = SGBFactory.<AlgebraicNumber<ModInteger>> getImplementation(afac);
        //System.out.println("bba2 = " + bba);
        assertTrue("bba algebraic ring " + bba, bba instanceof SolvableGroebnerBasePseudoSeq);
    }


    /**
     * Test get GenPolynomial implementation.
     */
    @SuppressWarnings("unchecked")
    public void testGenPolynomial() {
        BigRational b = new BigRational();
        GenPolynomialRing<BigRational> fac;
        fac = new GenPolynomialRing<BigRational>(b, rl, to);
        RingFactory<GenPolynomial<BigRational>> rf = fac;

        SolvableGroebnerBase<GenPolynomial<BigRational>> bba;

        bba = SGBFactory.getImplementation(fac);
        //System.out.println("bba = " + bba);
        assertTrue("bba recursive polynomial " + bba, bba instanceof SolvableGroebnerBasePseudoRecSeq);

        SolvableGroebnerBase<BigRational> bb;
        bb = SGBFactory.<BigRational> getImplementation((RingFactory) rf);
        //System.out.println("bb = " + bb);
        assertTrue("bba recursive polynomial " + bb, bb instanceof SolvableGroebnerBasePseudoRecSeq);
    }


    /**
     * Test get proxy implementation.
     */
    public void testProxy() {
        BigRational b = new BigRational();
        SolvableGroebnerBaseAbstract<BigRational> bba;

        bba = SGBFactory.getProxy(b);
        //System.out.println("bba = " + bba);
        assertTrue("bba field " + bba, bba instanceof SGBProxy);
        bba.terminate();


        ModIntegerRing m = new ModIntegerRing(2 * 3);
        SolvableGroebnerBaseAbstract<ModInteger> bbm;

        bbm = SGBFactory.getProxy(m);
        //System.out.println("bba = " + bba);
        assertTrue("bbm ! field " + bbm, !(bbm instanceof SGBProxy));
        bbm.terminate();
    }

}
