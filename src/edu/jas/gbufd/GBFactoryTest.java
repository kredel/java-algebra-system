/*
 * $Id$
 */

package edu.jas.gbufd;


//import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigComplex;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.Product;
import edu.jas.arith.ProductRing;
import edu.jas.gb.GBProxy;
import edu.jas.gb.GroebnerBase;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.structure.RingFactory;


/**
 * GreatestCommonDivisor factory tests with JUnit.
 * @author Heinz Kredel.
 */

public class GBFactoryTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GBFactoryTest</CODE> object.
     * @param name String.
     */
    public GBFactoryTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GBFactoryTest.class);
        return suite;
    }


    //private final static int bitlen = 100;

    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<BigInteger> dfac;


    GenPolynomialRing<BigInteger> cfac;


    GenPolynomialRing<GenPolynomial<BigInteger>> rfac;


    BigInteger ai;


    BigInteger bi;


    BigInteger ci;


    BigInteger di;


    BigInteger ei;


    GenPolynomial<BigInteger> a;


    GenPolynomial<BigInteger> b;


    GenPolynomial<BigInteger> c;


    GenPolynomial<BigInteger> d;


    GenPolynomial<BigInteger> e;


    GenPolynomial<GenPolynomial<BigInteger>> ar;


    GenPolynomial<GenPolynomial<BigInteger>> br;


    GenPolynomial<GenPolynomial<BigInteger>> cr;


    GenPolynomial<GenPolynomial<BigInteger>> dr;


    GenPolynomial<GenPolynomial<BigInteger>> er;


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
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl, to);
        cfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cfac, 1, to);
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
     * 
     */
    public void testBigInteger() {
        BigInteger bi = new BigInteger();
        GroebnerBase<BigInteger> bba;

        bba = GBFactory.getImplementation(bi);
        //System.out.println("bba = " + bba);
        assertTrue("bba integer " + bba, bba instanceof GroebnerBasePseudoSeq);
    }


    /**
     * Test get ModInteger implementation.
     * 
     */
    public void testModInteger() {
        ModIntegerRing mi = new ModIntegerRing(19, true);
        GroebnerBase<ModInteger> bba;

        bba = GBFactory.getImplementation(mi);
        //System.out.println("bba = " + bba);
        assertTrue("bba modular field " + bba, bba instanceof GroebnerBaseSeq);

        mi = new ModIntegerRing(30);
        bba = GBFactory.getImplementation(mi);
        //System.out.println("bba = " + bba);
        assertTrue("bba modular ring " + bba, bba instanceof GroebnerBasePseudoSeq);
    }


    /**
     * Test get BigRational implementation.
     * 
     */
    public void testBigRational() {
        BigRational b = new BigRational();
        GroebnerBase<BigRational> bba;

        bba = GBFactory.getImplementation(b);
        //System.out.println("bba = " + bba);
        assertTrue("bba field " + bba, bba instanceof GroebnerBaseSeq);
    }


    /**
     * Test get BigComplex implementation.
     * 
     */
    public void testBigComplex() {
        BigComplex b = new BigComplex();
        GroebnerBase<BigComplex> bba;

        bba = GBFactory.<BigComplex> getImplementation(b);
        //System.out.println("bba = " + bba);
        assertTrue("bba field " + bba, bba instanceof GroebnerBaseSeq);
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

        GroebnerBase<AlgebraicNumber<BigRational>> bba;

        bba = GBFactory.<AlgebraicNumber<BigRational>> getImplementation(afac);
        //System.out.println("bba1 = " + bba);
        assertTrue("bba algebraic ring " + bba, bba instanceof GroebnerBasePseudoSeq);

        mo = fac.univariate(0).subtract(fac.getONE());
        afac = new AlgebraicNumberRing<BigRational>(mo, true);

        bba = GBFactory.<AlgebraicNumber<BigRational>> getImplementation(afac);
        //System.out.println("bba1 = " + bba);
        assertTrue("bba algebraic field " + bba, bba instanceof GroebnerBaseSeq);
    }


    /**
     * Test get AlgebraicNumber&lt;ModInteger&gt; implementation.
     * 
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
        GroebnerBase<AlgebraicNumber<ModInteger>> bba;

        bba = GBFactory.<AlgebraicNumber<ModInteger>> getImplementation(afac);
        //System.out.println("bba2 = " + bba);
        assertTrue("bba algebraic ring " + bba, bba instanceof GroebnerBasePseudoSeq);
    }


    /**
     * Test get GenPolynomial implementation.
     * 
     */
    public void testGenPolynomial() {
        BigRational b = new BigRational();
        GenPolynomialRing<BigRational> fac;
        fac = new GenPolynomialRing<BigRational>(b, rl, to);
        RingFactory<GenPolynomial<BigRational>> rf = fac;

        GroebnerBase<GenPolynomial<BigRational>> bba;

        bba = GBFactory.getImplementation(fac);
        //System.out.println("bba = " + bba);
        assertTrue("bba recursive polynomial " + bba, bba instanceof GroebnerBasePseudoRecSeq);

        GroebnerBase<BigRational> bb;
        bb = GBFactory.<BigRational>getImplementation((RingFactory)rf);
        //System.out.println("bb = " + bb);
        assertTrue("bba recursive polynomial " + bb, bb instanceof GroebnerBasePseudoRecSeq);
    }


    /**
     * Test get Product implementation.
     * 
     */
    public void testProduct() {
        ModIntegerRing mi = new ModIntegerRing(19, true);
        ProductRing<ModInteger> fac;
        fac = new ProductRing<ModInteger>(mi, 3);
        RingFactory<Product<ModInteger>> rf = fac;

        GroebnerBase<Product<ModInteger>> bba;

        bba = GBFactory.getImplementation(fac);
        //System.out.println("bba = " + bba);
        assertTrue("bba product " + bba, bba instanceof RGroebnerBaseSeq);

        mi = new ModIntegerRing(30);
        fac = new ProductRing<ModInteger>(mi, 3);
        rf = fac;

        GroebnerBase<Product<ModInteger>> bb;
        bb = GBFactory.<Product<ModInteger>>getImplementation((RingFactory)rf);
        //System.out.println("bb = " + bb);
        assertTrue("bba product " + bb, bb instanceof RGroebnerBasePseudoSeq);
    }


    /**
     * Test get proxy implementation.
     * 
     */
    public void testProxy() {
        BigRational b = new BigRational();
        GroebnerBaseAbstract<BigRational> bba;

        bba = GBFactory.getProxy(b);
        //System.out.println("bba = " + bba);
        assertTrue("bba field " + bba, bba instanceof GBProxy);
        bba.terminate();


        ModIntegerRing m = new ModIntegerRing(2*3);
        GroebnerBaseAbstract<ModInteger> bbm;

        bbm = GBFactory.getProxy(m);
        //System.out.println("bba = " + bba);
        assertTrue("bbm ! field " + bbm, ! (bbm instanceof GBProxy) );
        bbm.terminate();
    }

}
