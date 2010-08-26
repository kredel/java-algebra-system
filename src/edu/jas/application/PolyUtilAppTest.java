/*
 * $Id$
 */

package edu.jas.application;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.structure.Product;
import edu.jas.structure.ProductRing;


/**
 * PolyUtilApp tests with JUnit.
 * @author Heinz Kredel.
 */

public class PolyUtilAppTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>PolyUtilAppTest</CODE> object.
     * @param name String.
     */
    public PolyUtilAppTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(PolyUtilAppTest.class);
        return suite;
    }


    //private final static int bitlen = 100;

    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<BigRational> dfac;


    GenPolynomialRing<BigRational> cfac;


    GenPolynomialRing<GenPolynomial<BigRational>> rfac;


    BigRational ai;


    BigRational bi;


    BigRational ci;


    BigRational di;


    BigRational ei;


    GenPolynomial<BigRational> a;


    GenPolynomial<BigRational> b;


    GenPolynomial<BigRational> c;


    GenPolynomial<BigRational> d;


    GenPolynomial<BigRational> e;


    GenPolynomial<GenPolynomial<BigRational>> ar;


    GenPolynomial<GenPolynomial<BigRational>> br;


    GenPolynomial<GenPolynomial<BigRational>> cr;


    GenPolynomial<GenPolynomial<BigRational>> dr;


    GenPolynomial<GenPolynomial<BigRational>> er;


    int rl = 5;


    int kl = 5;


    int ll = 5;


    int el = 5;


    float q = 0.6f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        dfac = new GenPolynomialRing<BigRational>(new BigRational(1), rl, to);
        cfac = null; //new GenPolynomialRing<BigRational>(new BigRational(1),rl-1,to);
        rfac = null; //new GenPolynomialRing<GenPolynomial<BigRational>>(cfac,1,to);
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
     * Test product represenation conversion, rational numbers.
     * 
     */
    public void testProductConversionRN() {
        GenPolynomialRing<BigRational> ufac;
        ufac = new GenPolynomialRing<BigRational>(new BigRational(1), 1);

        ProductRing<GenPolynomial<BigRational>> pfac;
        pfac = new ProductRing<GenPolynomial<BigRational>>(ufac, rl);

        Product<GenPolynomial<BigRational>> cp;

        c = dfac.getONE();
        //System.out.println("c = " + c);

        cp = PolyUtilApp.<BigRational> toProduct(pfac, c);
        //System.out.println("cp = " + cp);
        assertTrue("isONE( cp )", cp.isONE());

        c = dfac.random(kl, ll, el, q);
        //System.out.println("c = " + c);

        cp = PolyUtilApp.<BigRational> toProduct(pfac, c);
        //System.out.println("cp = " + cp);
        assertTrue("!isONE( cp )", !cp.isONE());
    }


    /**
     * Test polynomal over product represenation conversion, algebraic numbers.
     * 
     */
    public void testPolyProductConversionAN() {
        GenPolynomialRing<BigRational> ufac;
        ufac = new GenPolynomialRing<BigRational>(new BigRational(1), 1);

        GenPolynomial<BigRational> m;
        m = ufac.univariate(0, 2);
        m = m.subtract(ufac.univariate(0, 1));
        //System.out.println("m = " + m);

        AlgebraicNumberRing<BigRational> afac;
        afac = new AlgebraicNumberRing<BigRational>(m);
        //System.out.println("afac = " + afac);

        ProductRing<AlgebraicNumber<BigRational>> pfac;
        pfac = new ProductRing<AlgebraicNumber<BigRational>>(afac, rl);

        GenPolynomialRing<Product<AlgebraicNumber<BigRational>>> dpfac;
        dpfac = new GenPolynomialRing<Product<AlgebraicNumber<BigRational>>>(pfac, 2);

        GenPolynomialRing<AlgebraicNumber<BigRational>> dfac;
        dfac = new GenPolynomialRing<AlgebraicNumber<BigRational>>(afac, 2, to);


        GenPolynomial<AlgebraicNumber<BigRational>> c;
        GenPolynomial<Product<AlgebraicNumber<BigRational>>> cp;

        c = dfac.getONE();
        //System.out.println("c = " + c);

        cp = PolyUtilApp.<AlgebraicNumber<BigRational>> toProductGen(dpfac, c);
        //System.out.println("cp = " + cp);
        assertTrue("isZERO( cp )", cp.isONE());

        c = dfac.random(kl, ll, el, q);
        //System.out.println("c = " + c);

        cp = PolyUtilApp.<AlgebraicNumber<BigRational>> toProductGen(dpfac, c);
        //System.out.println("cp = " + cp);
        assertTrue("!isONE( cp )", !cp.isONE());
    }


    /**
     * Test primitive element.
     * 
     */
    public void testPrimitiveElement() {
        String[] va = new String[] { "alpha" };
        String[] vb = new String[] { "beta" };
        GenPolynomialRing<BigRational> aufac, bufac;

        // x^3 - 2
        aufac = new GenPolynomialRing<BigRational>(new BigRational(1), 1, va);
        GenPolynomial<BigRational> m;
        m = aufac.univariate(0, 3);
        m = m.subtract(aufac.fromInteger(2));
        //System.out.println("m = " + m);

        // x^2 - 3
        bufac = new GenPolynomialRing<BigRational>(new BigRational(1), 1, vb);
        GenPolynomial<BigRational> n;
        n = bufac.univariate(0, 2);
        n = n.subtract(bufac.fromInteger(3));
        //System.out.println("n = " + n);

        AlgebraicNumberRing<BigRational> afac = new AlgebraicNumberRing<BigRational>(m);
        //System.out.println("afac = " + afac);

        AlgebraicNumberRing<BigRational> bfac = new AlgebraicNumberRing<BigRational>(n);
        //System.out.println("bfac = " + bfac);

        PrimitiveElement<BigRational> pe;
        pe = PolyUtilApp.<BigRational> primitiveElement(afac, bfac);
        //System.out.println("pe = " + pe);
        AlgebraicNumberRing<BigRational> cfac = pe.primitiveElem;

        AlgebraicNumber<BigRational> a = afac.getGenerator();
        AlgebraicNumber<BigRational> b = bfac.getGenerator();

        // convert to primitive element field
        AlgebraicNumber<BigRational> as = PolyUtilApp.<BigRational> convertToPrimitiveElem(cfac, pe.A, a);
        AlgebraicNumber<BigRational> bs = PolyUtilApp.<BigRational> convertToPrimitiveElem(cfac, pe.B, b);

        // test alpha+(t)beta == gamma
        AlgebraicNumber<BigRational> cs = as.sum(bs);
        //System.out.println("cs  = " + cs);
        assertEquals("alpha+beta == gamma", cs, cfac.getGenerator());
    }


    /**
     * Test primitive element of extension tower.
     * 
     */
    public void testPrimitiveElementTower() {
        String[] va = new String[] { "alpha" };
        String[] vb = new String[] { "beta" };
        GenPolynomialRing<BigRational> ufac;
        ufac = new GenPolynomialRing<BigRational>(new BigRational(1), 1, va);

        GenPolynomial<BigRational> m;
        m = ufac.univariate(0, 3);
        m = m.subtract(ufac.fromInteger(2));
        //System.out.println("m = " + m);

        AlgebraicNumberRing<BigRational> afac;
        afac = new AlgebraicNumberRing<BigRational>(m);
        //System.out.println("afac = " + afac);

        GenPolynomialRing<AlgebraicNumber<BigRational>> aufac;
        aufac = new GenPolynomialRing<AlgebraicNumber<BigRational>>(afac, 1, vb);

        GenPolynomial<AlgebraicNumber<BigRational>> n;
        n = aufac.univariate(0, 2);
        n = n.subtract(aufac.getONE().multiply(afac.getGenerator()));
        //System.out.println("n = " + n);

        AlgebraicNumberRing<AlgebraicNumber<BigRational>> bfac;
        bfac = new AlgebraicNumberRing<AlgebraicNumber<BigRational>>(n);
        //System.out.println("bfac = " + bfac);

        PrimitiveElement<BigRational> pe;
        pe = PolyUtilApp.<BigRational> primitiveElement(bfac);
        //System.out.println("pe = " + pe);
        AlgebraicNumberRing<BigRational> cfac = pe.primitiveElem;

        AlgebraicNumber<BigRational> a = afac.getGenerator();
        AlgebraicNumber<AlgebraicNumber<BigRational>> b = bfac.getGenerator();

        // convert to primitive element ring
        AlgebraicNumber<BigRational> as = PolyUtilApp.<BigRational> convertToPrimitiveElem(cfac, pe.A, a);
        AlgebraicNumber<BigRational> bs = PolyUtilApp.<BigRational> convertToPrimitiveElem(cfac, pe.A, pe.B,
                b);

        // test alpha+(t)beta == gamma
        AlgebraicNumber<BigRational> cs = as.sum(bs);
        //System.out.println("cs  = " + cs);
        assertEquals("alpha+beta == gamma", cs, cfac.getGenerator());

        // test for polynomials, too simple
        String[] vx = new String[] { "x" };
        GenPolynomialRing<AlgebraicNumber<BigRational>> rafac = new GenPolynomialRing<AlgebraicNumber<BigRational>>(
                afac, 1, vx);
        GenPolynomialRing<AlgebraicNumber<AlgebraicNumber<BigRational>>> rbfac = new GenPolynomialRing<AlgebraicNumber<AlgebraicNumber<BigRational>>>(
                bfac, 1, vx);
        GenPolynomial<AlgebraicNumber<BigRational>> ap = rafac.getGenerators().get(0).multiply(a);
        GenPolynomial<AlgebraicNumber<AlgebraicNumber<BigRational>>> bp = rbfac.getGenerators().get(0)
                .multiply(b);

        GenPolynomial<AlgebraicNumber<BigRational>> asp = PolyUtilApp.<BigRational> convertToPrimitiveElem(
                cfac, pe.A, ap);
        GenPolynomial<AlgebraicNumber<BigRational>> bsp = PolyUtilApp.<BigRational> convertToPrimitiveElem(
                cfac, pe.A, pe.B, bp);
        //System.out.println("asp = " + asp);
        //System.out.println("bsp = " + bsp);

        // test alpha+(t)beta == gamma
        GenPolynomial<AlgebraicNumber<BigRational>> csp = asp.sum(bsp);
        //System.out.println("csp = " + csp);
        assertEquals("alpha+beta == gamma", csp.leadingBaseCoefficient(), cfac.getGenerator());
    }

}
